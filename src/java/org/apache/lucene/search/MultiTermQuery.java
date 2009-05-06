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
name|util
operator|.
name|ToStringUtils
import|;
end_import
begin_comment
comment|/**  * A {@link Query} that matches documents containing a subset of terms provided  * by a {@link FilteredTermEnum} enumeration.  *<P>  *<code>MultiTermQuery</code> is not designed to be used by itself.<BR>  * The reason being that it is not intialized with a {@link FilteredTermEnum}  * enumeration. A {@link FilteredTermEnum} enumeration needs to be provided.  *<P>  * For example, {@link WildcardQuery} and {@link FuzzyQuery} extend  *<code>MultiTermQuery</code> to provide {@link WildcardTermEnum} and  * {@link FuzzyTermEnum}, respectively.  *   * The pattern Term may be null. A query that uses a null pattern Term should  * override equals and hashcode.  */
end_comment
begin_class
DECL|class|MultiTermQuery
specifier|public
specifier|abstract
class|class
name|MultiTermQuery
extends|extends
name|Query
block|{
comment|/* @deprecated move to sub class */
DECL|field|term
specifier|protected
name|Term
name|term
decl_stmt|;
DECL|field|constantScoreRewrite
specifier|protected
name|boolean
name|constantScoreRewrite
init|=
literal|false
decl_stmt|;
DECL|field|numberOfTerms
specifier|transient
name|int
name|numberOfTerms
init|=
literal|0
decl_stmt|;
comment|/** Constructs a query for terms matching<code>term</code>. */
DECL|method|MultiTermQuery
specifier|public
name|MultiTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
block|}
comment|/**    * Constructs a query matching terms that cannot be represented with a single    * Term.    */
DECL|method|MultiTermQuery
specifier|public
name|MultiTermQuery
parameter_list|()
block|{   }
comment|/**    * Returns the pattern term.    * @deprecated check sub class for possible term access - getTerm does not    * make sense for all MultiTermQuerys and will be removed.    */
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
comment|/** Construct the enumeration to be used, expanding the pattern term. */
DECL|method|getEnum
specifier|protected
specifier|abstract
name|FilteredTermEnum
name|getEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Expert: Return the number of unique terms visited during execution of the query.    * If there are many of them, you may consider using another query type    * or optimize your total term count in index.    *<p>This method is not thread safe, be sure to only call it when no query is running!    * If you re-use the same query instance for another    * search, be sure to first reset the term counter    * with {@link #clearTotalNumberOfTerms}.    *<p>On optimized indexes / no MultiReaders, you get the correct number of    * unique terms for the whole index. Use this number to compare different queries.    * For non-optimized indexes this number can also be achived in    * non-constant-score mode. In constant-score mode you get the total number of    * terms seeked for all segments / sub-readers.    * @see #clearTotalNumberOfTerms    */
DECL|method|getTotalNumberOfTerms
specifier|public
name|int
name|getTotalNumberOfTerms
parameter_list|()
block|{
return|return
name|numberOfTerms
return|;
block|}
comment|/**    * Expert: Resets the counting of unique terms.    * Do this before executing the query/filter.    * @see #getTotalNumberOfTerms    */
DECL|method|clearTotalNumberOfTerms
specifier|public
name|void
name|clearTotalNumberOfTerms
parameter_list|()
block|{
name|numberOfTerms
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|getFilter
specifier|protected
name|Filter
name|getFilter
parameter_list|()
block|{
return|return
operator|new
name|MultiTermQueryWrapperFilter
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
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
name|constantScoreRewrite
condition|)
block|{
name|FilteredTermEnum
name|enumerator
init|=
name|getEnum
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|t
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|numberOfTerms
operator|++
expr_stmt|;
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
name|t
argument_list|)
decl_stmt|;
comment|// found a match
name|tq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
operator|*
name|enumerator
operator|.
name|difference
argument_list|()
argument_list|)
expr_stmt|;
comment|// set the boost
name|query
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// add to query
block|}
block|}
do|while
condition|(
name|enumerator
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
else|else
block|{
name|Query
name|query
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|getFilter
argument_list|()
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
block|}
comment|/* Prints a user-readable version of this query.    * Implemented for back compat in case MultiTermQuery    * subclasses do no implement.    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
condition|)
block|{
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
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"termPattern:unknown"
argument_list|)
expr_stmt|;
block|}
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
DECL|method|getConstantScoreRewrite
specifier|public
name|boolean
name|getConstantScoreRewrite
parameter_list|()
block|{
return|return
name|constantScoreRewrite
return|;
block|}
DECL|method|setConstantScoreRewrite
specifier|public
name|void
name|setConstantScoreRewrite
parameter_list|(
name|boolean
name|constantScoreRewrite
parameter_list|)
block|{
name|this
operator|.
name|constantScoreRewrite
operator|=
name|constantScoreRewrite
expr_stmt|;
block|}
comment|//@Override
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
literal|1
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
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|constantScoreRewrite
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|//@Override
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
name|obj
operator|==
literal|null
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
name|MultiTermQuery
name|other
init|=
operator|(
name|MultiTermQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|!=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|other
operator|.
name|getBoost
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|constantScoreRewrite
operator|!=
name|other
operator|.
name|constantScoreRewrite
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
