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
name|TermDocs
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
name|TermEnum
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
name|OpenBitSet
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import
begin_comment
comment|/**  * A wrapper for {@link MultiTermQuery}, that exposes its  * functionality as a {@link Filter}.  *<P>  *<code>MultiTermQueryWrapperFilter</code> is not designed to  * be used by itself. Normally you subclass it to provide a Filter  * counterpart for a {@link MultiTermQuery} subclass.  *<P>  * For example, {@link RangeFilter} and {@link PrefixFilter} extend  *<code>MultiTermQueryWrapperFilter</code>.  * This class also provides the functionality behind  * {@link MultiTermQuery#getFilter}, this is why it is not abstract.  */
end_comment
begin_class
DECL|class|MultiTermQueryWrapperFilter
specifier|public
class|class
name|MultiTermQueryWrapperFilter
extends|extends
name|Filter
block|{
DECL|field|query
specifier|protected
specifier|final
name|MultiTermQuery
name|query
decl_stmt|;
comment|/**    * Wrap a {@link MultiTermQuery} as a Filter.    */
DECL|method|MultiTermQueryWrapperFilter
specifier|protected
name|MultiTermQueryWrapperFilter
parameter_list|(
name|MultiTermQuery
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
comment|//@Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// query.toString should be ok for the filter, too, if the query boost is 1.0f
return|return
name|query
operator|.
name|toString
argument_list|()
return|;
block|}
comment|//@Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MultiTermQueryWrapperFilter
operator|)
name|o
operator|)
operator|.
name|query
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|//@Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|query
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Expert: Return the number of unique terms visited during execution of the filter.    * If there are many of them, you may consider using another filter type    * or optimize your total term count in index.    *<p>This method is not thread safe, be sure to only call it when no filter is running!    * If you re-use the same filter instance for another    * search, be sure to first reset the term counter    * with {@link #clearTotalNumberOfTerms}.    * @see #clearTotalNumberOfTerms    */
DECL|method|getTotalNumberOfTerms
specifier|public
name|int
name|getTotalNumberOfTerms
parameter_list|()
block|{
return|return
name|query
operator|.
name|getTotalNumberOfTerms
argument_list|()
return|;
block|}
comment|/**    * Expert: Resets the counting of unique terms.    * Do this before executing the filter.    * @see #getTotalNumberOfTerms    */
DECL|method|clearTotalNumberOfTerms
specifier|public
name|void
name|clearTotalNumberOfTerms
parameter_list|()
block|{
name|query
operator|.
name|clearTotalNumberOfTerms
argument_list|()
expr_stmt|;
block|}
DECL|class|TermGenerator
specifier|abstract
class|class
name|TermGenerator
block|{
DECL|method|generate
specifier|public
name|void
name|generate
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|TermEnum
name|enumerator
parameter_list|)
throws|throws
name|IOException
block|{
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
break|break;
name|query
operator|.
name|numberOfTerms
operator|++
expr_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|handleDoc
argument_list|(
name|termDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
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
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|handleDoc
specifier|abstract
specifier|public
name|void
name|handleDoc
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
block|}
comment|/**    * Returns a BitSet with true for documents which should be    * permitted in search results, and false for those that should    * not.    * @deprecated Use {@link #getDocIdSet(IndexReader)} instead.    */
comment|//@Override
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermEnum
name|enumerator
init|=
name|query
operator|.
name|getEnum
argument_list|(
name|reader
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
operator|new
name|TermGenerator
argument_list|()
block|{
specifier|public
name|void
name|handleDoc
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|generate
argument_list|(
name|reader
argument_list|,
name|enumerator
argument_list|)
expr_stmt|;
return|return
name|bitSet
return|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns a DocIdSet with documents that should be    * permitted in search results.    */
comment|//@Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermEnum
name|enumerator
init|=
name|query
operator|.
name|getEnum
argument_list|(
name|reader
argument_list|)
decl_stmt|;
try|try
block|{
comment|// if current term in enum is null, the enum is empty -> shortcut
if|if
condition|(
name|enumerator
operator|.
name|term
argument_list|()
operator|==
literal|null
condition|)
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
comment|// else fill into a OpenBitSet
specifier|final
name|OpenBitSet
name|bitSet
init|=
operator|new
name|OpenBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
operator|new
name|TermGenerator
argument_list|()
block|{
specifier|public
name|void
name|handleDoc
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|generate
argument_list|(
name|reader
argument_list|,
name|enumerator
argument_list|)
expr_stmt|;
return|return
name|bitSet
return|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
