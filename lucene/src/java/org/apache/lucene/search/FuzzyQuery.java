begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TermsEnum
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ToStringUtils
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/** Implements the fuzzy search query. The similarity measurement  * is based on the Levenshtein (edit distance) algorithm.  *   *<p><em>Warning:</em> this query is not very scalable with its default prefix  * length of 0 - in this case, *every* term will be enumerated and  * cause an edit score calculation.  *   *<p>This query uses {@link MultiTermQuery.TopTermsScoringBooleanQueryRewrite}  * as default. So terms will be collected and scored according to their  * edit distance. Only the top terms are used for building the {@link BooleanQuery}.  * It is not recommended to change the rewrite mode for fuzzy queries.  */
end_comment
begin_class
DECL|class|FuzzyQuery
specifier|public
class|class
name|FuzzyQuery
extends|extends
name|MultiTermQuery
block|{
DECL|field|defaultMinSimilarity
specifier|public
specifier|final
specifier|static
name|float
name|defaultMinSimilarity
init|=
literal|0.5f
decl_stmt|;
DECL|field|defaultPrefixLength
specifier|public
specifier|final
specifier|static
name|int
name|defaultPrefixLength
init|=
literal|0
decl_stmt|;
DECL|field|defaultMaxExpansions
specifier|public
specifier|final
specifier|static
name|int
name|defaultMaxExpansions
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|minimumSimilarity
specifier|private
name|float
name|minimumSimilarity
decl_stmt|;
DECL|field|prefixLength
specifier|private
name|int
name|prefixLength
decl_stmt|;
DECL|field|termLongEnough
specifier|private
name|boolean
name|termLongEnough
init|=
literal|false
decl_stmt|;
DECL|field|term
specifier|protected
name|Term
name|term
decl_stmt|;
comment|/**    * Create a new FuzzyQuery that will match terms with a similarity     * of at least<code>minimumSimilarity</code> to<code>term</code>.    * If a<code>prefixLength</code>&gt; 0 is specified, a common prefix    * of that length is also required.    *     * @param term the term to search for    * @param minimumSimilarity a value between 0 and 1 to set the required similarity    *  between the query term and the matching terms. For example, for a    *<code>minimumSimilarity</code> of<code>0.5</code> a term of the same length    *  as the query term is considered similar to the query term if the edit distance    *  between both terms is less than<code>length(term)*0.5</code>    * @param prefixLength length of common (non-fuzzy) prefix    * @param maxExpansions the maximum number of terms to match. If this number is    *  greater than {@link BooleanQuery#getMaxClauseCount} when the query is rewritten,     *  then the maxClauseCount will be used instead.    * @throws IllegalArgumentException if minimumSimilarity is&gt;= 1 or&lt; 0    * or if prefixLength&lt; 0    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|float
name|minimumSimilarity
parameter_list|,
name|int
name|prefixLength
parameter_list|,
name|int
name|maxExpansions
parameter_list|)
block|{
name|super
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
if|if
condition|(
name|minimumSimilarity
operator|>=
literal|1.0f
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimumSimilarity>= 1"
argument_list|)
throw|;
elseif|else
if|if
condition|(
name|minimumSimilarity
operator|<
literal|0.0f
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimumSimilarity< 0"
argument_list|)
throw|;
if|if
condition|(
name|prefixLength
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"prefixLength< 0"
argument_list|)
throw|;
if|if
condition|(
name|maxExpansions
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxExpansions< 0"
argument_list|)
throw|;
name|setRewriteMethod
argument_list|(
operator|new
name|MultiTermQuery
operator|.
name|TopTermsScoringBooleanQueryRewrite
argument_list|(
name|maxExpansions
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|text
init|=
name|term
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|codePointCount
argument_list|(
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
operator|>
literal|1.0f
operator|/
operator|(
literal|1.0f
operator|-
name|minimumSimilarity
operator|)
condition|)
block|{
name|this
operator|.
name|termLongEnough
operator|=
literal|true
expr_stmt|;
block|}
name|this
operator|.
name|minimumSimilarity
operator|=
name|minimumSimilarity
expr_stmt|;
name|this
operator|.
name|prefixLength
operator|=
name|prefixLength
expr_stmt|;
block|}
comment|/**    * Calls {@link #FuzzyQuery(Term, float) FuzzyQuery(term, minimumSimilarity, prefixLength, Integer.MAX_VALUE)}.    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|float
name|minimumSimilarity
parameter_list|,
name|int
name|prefixLength
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|minimumSimilarity
argument_list|,
name|prefixLength
argument_list|,
name|defaultMaxExpansions
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls {@link #FuzzyQuery(Term, float) FuzzyQuery(term, minimumSimilarity, 0, Integer.MAX_VALUE)}.    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|float
name|minimumSimilarity
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|minimumSimilarity
argument_list|,
name|defaultPrefixLength
argument_list|,
name|defaultMaxExpansions
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls {@link #FuzzyQuery(Term, float) FuzzyQuery(term, 0.5f, 0, Integer.MAX_VALUE)}.    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|defaultMinSimilarity
argument_list|,
name|defaultPrefixLength
argument_list|,
name|defaultMaxExpansions
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the minimum similarity that is required for this query to match.    * @return float value between 0.0 and 1.0    */
DECL|method|getMinSimilarity
specifier|public
name|float
name|getMinSimilarity
parameter_list|()
block|{
return|return
name|minimumSimilarity
return|;
block|}
comment|/**    * Returns the non-fuzzy prefix length. This is the number of characters at the start    * of a term that must be identical (not fuzzy) to the query term if the query    * is to match that term.     */
DECL|method|getPrefixLength
specifier|public
name|int
name|getPrefixLength
parameter_list|()
block|{
return|return
name|prefixLength
return|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
DECL|method|getEnum
specifier|protected
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|termLongEnough
condition|)
block|{
comment|// can only match if it's exact
return|return
operator|new
name|SingleTermEnum
argument_list|(
name|reader
argument_list|,
name|term
argument_list|)
return|;
block|}
return|return
operator|new
name|FuzzyTermEnum
argument_list|(
name|reader
argument_list|,
name|getTerm
argument_list|()
argument_list|,
name|minimumSimilarity
argument_list|,
name|prefixLength
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|termLongEnough
condition|)
block|{
comment|// can only match if it's exact
return|return
operator|new
name|SingleTermsEnum
argument_list|(
name|reader
argument_list|,
name|term
argument_list|)
return|;
block|}
return|return
operator|new
name|FuzzyTermsEnum
argument_list|(
name|reader
argument_list|,
name|getTerm
argument_list|()
argument_list|,
name|minimumSimilarity
argument_list|,
name|prefixLength
argument_list|)
return|;
block|}
comment|/**    * Returns the pattern term.    */
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'~'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|Float
operator|.
name|toString
argument_list|(
name|minimumSimilarity
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|minimumSimilarity
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|prefixLength
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|term
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|term
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FuzzyQuery
name|other
init|=
operator|(
name|FuzzyQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|minimumSimilarity
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|minimumSimilarity
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|prefixLength
operator|!=
name|other
operator|.
name|prefixLength
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|term
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
