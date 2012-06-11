begin_unit
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|BytesRef
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
name|BytesRefIterator
import|;
end_import
begin_comment
comment|/**  * Interface for enumerating term,weight pairs.  */
end_comment
begin_interface
DECL|interface|TermFreqIterator
specifier|public
interface|interface
name|TermFreqIterator
extends|extends
name|BytesRefIterator
block|{
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
function_decl|;
comment|/**    * Wraps a BytesRefIterator as a TermFreqIterator, with all weights    * set to<code>1</code>    */
DECL|class|TermFreqIteratorWrapper
specifier|public
specifier|static
class|class
name|TermFreqIteratorWrapper
implements|implements
name|TermFreqIterator
block|{
DECL|field|wrapped
specifier|private
name|BytesRefIterator
name|wrapped
decl_stmt|;
DECL|method|TermFreqIteratorWrapper
specifier|public
name|TermFreqIteratorWrapper
parameter_list|(
name|BytesRefIterator
name|wrapped
parameter_list|)
block|{
name|this
operator|.
name|wrapped
operator|=
name|wrapped
expr_stmt|;
block|}
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|wrapped
operator|.
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|wrapped
operator|.
name|getComparator
argument_list|()
return|;
block|}
block|}
block|}
end_interface
end_unit
