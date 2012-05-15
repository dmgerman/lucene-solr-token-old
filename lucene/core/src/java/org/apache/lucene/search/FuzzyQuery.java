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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|SingleTermsEnum
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
name|Terms
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
name|AttributeSource
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|LevenshteinAutomata
import|;
end_import
begin_comment
comment|/** Implements the fuzzy search query. The similarity measurement  * is based on the Damerau-Levenshtein (optimal string alignment) algorithm.  *   *<p>This query uses {@link MultiTermQuery.TopTermsScoringBooleanQueryRewrite}  * as default. So terms will be collected and scored according to their  * edit distance. Only the top terms are used for building the {@link BooleanQuery}.  * It is not recommended to change the rewrite mode for fuzzy queries.  */
end_comment
begin_class
DECL|class|FuzzyQuery
specifier|public
class|class
name|FuzzyQuery
extends|extends
name|MultiTermQuery
block|{
DECL|field|defaultMaxEdits
specifier|public
specifier|final
specifier|static
name|int
name|defaultMaxEdits
init|=
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
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
literal|50
decl_stmt|;
DECL|field|defaultTranspositions
specifier|public
specifier|final
specifier|static
name|boolean
name|defaultTranspositions
init|=
literal|true
decl_stmt|;
DECL|field|maxEdits
specifier|private
specifier|final
name|int
name|maxEdits
decl_stmt|;
DECL|field|maxExpansions
specifier|private
specifier|final
name|int
name|maxExpansions
decl_stmt|;
DECL|field|transpositions
specifier|private
specifier|final
name|boolean
name|transpositions
decl_stmt|;
DECL|field|prefixLength
specifier|private
specifier|final
name|int
name|prefixLength
decl_stmt|;
DECL|field|term
specifier|private
specifier|final
name|Term
name|term
decl_stmt|;
comment|/**    * Create a new FuzzyQuery that will match terms with an edit distance     * of at most<code>maxEdits</code> to<code>term</code>.    * If a<code>prefixLength</code>&gt; 0 is specified, a common prefix    * of that length is also required.    *     * @param term the term to search for    * @param maxEdits must be>= 0 and<= {@link LevenshteinAutomata#MAXIMUM_SUPPORTED_DISTANCE}.    * @param prefixLength length of common (non-fuzzy) prefix    * @param maxExpansions the maximum number of terms to match. If this number is    *  greater than {@link BooleanQuery#getMaxClauseCount} when the query is rewritten,     *  then the maxClauseCount will be used instead.    * @param transpositions true if transpositions should be treated as a primitive    *        edit operation. If this is false, comparisons will implement the classic    *        Levenshtein algorithm.    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|maxEdits
parameter_list|,
name|int
name|prefixLength
parameter_list|,
name|int
name|maxExpansions
parameter_list|,
name|boolean
name|transpositions
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
if|if
condition|(
name|maxEdits
argument_list|<
literal|0
operator|||
name|maxEdits
argument_list|>
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxEdits must be between 0 and "
operator|+
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
argument_list|)
throw|;
block|}
if|if
condition|(
name|prefixLength
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"prefixLength cannot be negative."
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxExpansions
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxExpansions cannot be negative."
argument_list|)
throw|;
block|}
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|maxEdits
operator|=
name|maxEdits
expr_stmt|;
name|this
operator|.
name|prefixLength
operator|=
name|prefixLength
expr_stmt|;
name|this
operator|.
name|transpositions
operator|=
name|transpositions
expr_stmt|;
name|this
operator|.
name|maxExpansions
operator|=
name|maxExpansions
expr_stmt|;
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
block|}
comment|/**    * Calls {@link #FuzzyQuery(Term, int, int, int, boolean)     * FuzzyQuery(term, minimumSimilarity, prefixLength, defaultMaxExpansions, defaultTranspositions)}.    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|maxEdits
parameter_list|,
name|int
name|prefixLength
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|maxEdits
argument_list|,
name|prefixLength
argument_list|,
name|defaultMaxExpansions
argument_list|,
name|defaultTranspositions
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls {@link #FuzzyQuery(Term, int, int) FuzzyQuery(term, maxEdits, defaultPrefixLength)}.    */
DECL|method|FuzzyQuery
specifier|public
name|FuzzyQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|maxEdits
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|maxEdits
argument_list|,
name|defaultPrefixLength
argument_list|)
expr_stmt|;
block|}
comment|/**    * Calls {@link #FuzzyQuery(Term, int) FuzzyQuery(term, defaultMaxEdits)}.    */
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
name|defaultMaxEdits
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the maximum number of edit distances allowed for this query to match.    */
DECL|method|getMaxEdits
specifier|public
name|int
name|getMaxEdits
parameter_list|()
block|{
return|return
name|maxEdits
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
DECL|method|getTermsEnum
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|maxEdits
operator|==
literal|0
operator|||
name|prefixLength
operator|>=
name|term
operator|.
name|text
argument_list|()
operator|.
name|length
argument_list|()
condition|)
block|{
comment|// can only match if it's exact
return|return
operator|new
name|SingleTermsEnum
argument_list|(
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|,
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
return|;
block|}
return|return
operator|new
name|FuzzyTermsEnum
argument_list|(
name|terms
argument_list|,
name|atts
argument_list|,
name|getTerm
argument_list|()
argument_list|,
name|maxEdits
argument_list|,
name|prefixLength
argument_list|,
name|transpositions
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
name|Integer
operator|.
name|toString
argument_list|(
name|maxEdits
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
name|maxEdits
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
name|maxExpansions
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|transpositions
condition|?
literal|0
else|:
literal|1
operator|)
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
name|maxEdits
operator|!=
name|other
operator|.
name|maxEdits
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
name|maxExpansions
operator|!=
name|other
operator|.
name|maxExpansions
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|transpositions
operator|!=
name|other
operator|.
name|transpositions
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
comment|/**    * @deprecated pass integer edit distances instead.    */
annotation|@
name|Deprecated
DECL|field|defaultMinSimilarity
specifier|public
specifier|final
specifier|static
name|float
name|defaultMinSimilarity
init|=
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
decl_stmt|;
comment|/**    * Helper function to convert from deprecated "minimumSimilarity" fractions    * to raw edit distances.    *     * @param minimumSimilarity scaled similarity    * @param termLen length (in unicode codepoints) of the term.    * @return equivalent number of maxEdits    * @deprecated pass integer edit distances instead.    */
annotation|@
name|Deprecated
DECL|method|floatToEdits
specifier|public
specifier|static
name|int
name|floatToEdits
parameter_list|(
name|float
name|minimumSimilarity
parameter_list|,
name|int
name|termLen
parameter_list|)
block|{
if|if
condition|(
name|minimumSimilarity
operator|>=
literal|1f
condition|)
block|{
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|minimumSimilarity
argument_list|,
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|minimumSimilarity
operator|==
literal|0.0f
condition|)
block|{
return|return
literal|0
return|;
comment|// 0 means exact, not infinite # of edits!
block|}
else|else
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
call|(
name|int
call|)
argument_list|(
operator|(
literal|1D
operator|-
name|minimumSimilarity
operator|)
operator|*
name|termLen
argument_list|)
argument_list|,
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
