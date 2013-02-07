begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package
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
name|AtomicReaderContext
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
name|BinaryDocValues
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
name|IntsRef
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
name|encoding
operator|.
name|IntDecoder
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/** A {@link CategoryListIterator} which reads the ordinals from a {@link BinaryDocValues}. */
end_comment
begin_class
DECL|class|DocValuesCategoryListIterator
specifier|public
class|class
name|DocValuesCategoryListIterator
implements|implements
name|CategoryListIterator
block|{
DECL|field|decoder
specifier|private
specifier|final
name|IntDecoder
name|decoder
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|hashCode
specifier|private
specifier|final
name|int
name|hashCode
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|32
argument_list|)
decl_stmt|;
DECL|field|current
specifier|private
name|BinaryDocValues
name|current
decl_stmt|;
comment|/**    * Constructs a new {@link DocValuesCategoryListIterator}.    */
DECL|method|DocValuesCategoryListIterator
specifier|public
name|DocValuesCategoryListIterator
parameter_list|(
name|String
name|field
parameter_list|,
name|IntDecoder
name|decoder
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|decoder
operator|=
name|decoder
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|field
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hashCode
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
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|DocValuesCategoryListIterator
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DocValuesCategoryListIterator
name|other
init|=
operator|(
name|DocValuesCategoryListIterator
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|hashCode
operator|!=
name|other
operator|.
name|hashCode
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Hash codes are the same, check equals() to avoid cases of hash-collisions.
return|return
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|boolean
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|current
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
expr_stmt|;
return|return
name|current
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getOrdinals
specifier|public
name|void
name|getOrdinals
parameter_list|(
name|int
name|docID
parameter_list|,
name|IntsRef
name|ints
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|current
operator|!=
literal|null
operator|:
literal|"don't call this if setNextReader returned false"
assert|;
name|current
operator|.
name|get
argument_list|(
name|docID
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|ints
operator|.
name|length
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|decoder
operator|.
name|decode
argument_list|(
name|bytes
argument_list|,
name|ints
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|field
return|;
block|}
block|}
end_class
end_unit
