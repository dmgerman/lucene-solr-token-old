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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_comment
comment|/**  * A {@link Query} that matches documents containing a subset of terms provided  * by a {@link FilteredTermEnum} enumeration.  *<P>  *<code>MultiTermQuery</code> is not designed to be used by itself.  *<BR>  * The reason being that it is not intialized with a {@link FilteredTermEnum}  * enumeration. A {@link FilteredTermEnum} enumeration needs to be provided.  *<P>  * For example, {@link WildcardQuery} and {@link FuzzyQuery} extend  *<code>MultiTermQuery</code> to provide {@link WildcardTermEnum} and  * {@link FuzzyTermEnum}, respectively.  */
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
DECL|field|term
specifier|private
name|Term
name|term
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
comment|/** Returns the pattern term. */
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
argument_list|()
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
literal|false
argument_list|,
literal|false
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
DECL|method|combine
specifier|public
name|Query
name|combine
parameter_list|(
name|Query
index|[]
name|queries
parameter_list|)
block|{
return|return
name|Query
operator|.
name|mergeBooleanQueries
argument_list|(
name|queries
argument_list|)
return|;
block|}
comment|/** Prints a user-readable version of this query. */
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
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"^"
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
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
