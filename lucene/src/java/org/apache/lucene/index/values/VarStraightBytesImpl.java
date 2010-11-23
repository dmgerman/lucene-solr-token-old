begin_unit
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|values
operator|.
name|Bytes
operator|.
name|BytesBaseSource
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
name|values
operator|.
name|Bytes
operator|.
name|BytesReaderBase
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
name|values
operator|.
name|Bytes
operator|.
name|BytesWriterBase
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|IndexInput
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
name|ArrayUtil
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
name|PagedBytes
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
name|RamUsageEstimator
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
name|packed
operator|.
name|PackedInts
import|;
end_import
begin_comment
comment|// Variable length byte[] per document, no sharing
end_comment
begin_class
DECL|class|VarStraightBytesImpl
class|class
name|VarStraightBytesImpl
block|{
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"VarStraightBytes"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|class|Writer
specifier|static
class|class
name|Writer
extends|extends
name|BytesWriterBase
block|{
DECL|field|address
specifier|private
name|long
name|address
decl_stmt|;
comment|// start at -1 if the first added value is> 0
DECL|field|lastDocID
specifier|private
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|docToAddress
specifier|private
name|long
index|[]
name|docToAddress
decl_stmt|;
DECL|method|Writer
specifier|public
name|Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
name|docToAddress
operator|=
operator|new
name|long
index|[
literal|1
index|]
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
DECL|method|Writer
specifier|public
name|Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
operator|new
name|AtomicLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Fills up to but not including this docID
DECL|method|fill
specifier|private
name|void
name|fill
parameter_list|(
specifier|final
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|>=
name|docToAddress
operator|.
name|length
condition|)
block|{
name|int
name|oldSize
init|=
name|docToAddress
operator|.
name|length
decl_stmt|;
name|docToAddress
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|docToAddress
argument_list|,
literal|1
operator|+
name|docID
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
operator|(
name|docToAddress
operator|.
name|length
operator|-
name|oldSize
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|lastDocID
operator|+
literal|1
init|;
name|i
operator|<
name|docID
condition|;
name|i
operator|++
control|)
block|{
name|docToAddress
index|[
name|i
index|]
operator|=
name|address
expr_stmt|;
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
return|return;
comment|// default
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
name|initDataOut
argument_list|()
expr_stmt|;
name|fill
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|docToAddress
index|[
name|docID
index|]
operator|=
name|address
expr_stmt|;
name|datOut
operator|.
name|writeBytes
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|address
operator|+=
name|bytes
operator|.
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|synchronized
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|initIndexOut
argument_list|()
expr_stmt|;
name|fill
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|idxOut
operator|.
name|writeVLong
argument_list|(
name|address
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Writer
name|w
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|idxOut
argument_list|,
name|docCount
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|address
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docCount
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|add
argument_list|(
name|docToAddress
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
operator|(
name|docToAddress
operator|.
name|length
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
name|docToAddress
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|finish
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
operator|.
name|get
argument_list|()
return|;
block|}
block|}
DECL|class|Reader
specifier|public
specifier|static
class|class
name|Reader
extends|extends
name|BytesReaderBase
block|{
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|Reader
name|Reader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|Source
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|)
return|;
block|}
DECL|class|Source
specifier|private
class|class
name|Source
extends|extends
name|BytesBaseSource
block|{
DECL|field|addresses
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|addresses
decl_stmt|;
DECL|method|Source
specifier|public
name|Source
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|datIn
argument_list|,
name|idxIn
argument_list|,
operator|new
name|PagedBytes
argument_list|(
name|PAGED_BYTES_BITS
argument_list|)
argument_list|,
name|idxIn
operator|.
name|readVLong
argument_list|()
argument_list|)
expr_stmt|;
name|addresses
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
name|missingValues
operator|.
name|bytesValue
operator|=
operator|new
name|BytesRef
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// empty
block|}
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
specifier|final
name|long
name|address
init|=
name|addresses
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|docID
operator|==
name|maxDoc
operator|-
literal|1
condition|?
call|(
name|int
call|)
argument_list|(
name|totalLengthInBytes
operator|-
name|address
argument_list|)
else|:
call|(
name|int
call|)
argument_list|(
name|addresses
operator|.
name|get
argument_list|(
literal|1
operator|+
name|docID
argument_list|)
operator|-
name|address
argument_list|)
decl_stmt|;
return|return
name|data
operator|.
name|fill
argument_list|(
name|bytesRef
argument_list|,
name|address
argument_list|,
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
block|{
return|return
name|Values
operator|.
name|BYTES_VAR_STRAIGHT
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|protected
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|addresses
operator|.
name|size
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|VarStraightBytesEnum
argument_list|(
name|source
argument_list|,
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|)
return|;
block|}
DECL|class|VarStraightBytesEnum
specifier|private
class|class
name|VarStraightBytesEnum
extends|extends
name|ValuesEnum
block|{
DECL|field|addresses
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|addresses
decl_stmt|;
DECL|field|datIn
specifier|private
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|idxIn
specifier|private
specifier|final
name|IndexInput
name|idxIn
decl_stmt|;
DECL|field|fp
specifier|private
specifier|final
name|long
name|fp
decl_stmt|;
DECL|field|totBytes
specifier|private
specifier|final
name|int
name|totBytes
decl_stmt|;
DECL|field|ref
specifier|private
specifier|final
name|BytesRef
name|ref
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|VarStraightBytesEnum
specifier|protected
name|VarStraightBytesEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|Values
operator|.
name|BYTES_VAR_STRAIGHT
argument_list|)
expr_stmt|;
name|totBytes
operator|=
name|idxIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|fp
operator|=
name|datIn
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|addresses
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
name|this
operator|.
name|datIn
operator|=
name|datIn
expr_stmt|;
name|this
operator|.
name|idxIn
operator|=
name|idxIn
expr_stmt|;
name|ref
operator|=
name|attr
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
name|idxIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
specifier|final
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|maxDoc
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
specifier|final
name|long
name|addr
init|=
name|addresses
operator|.
name|get
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|addr
operator|==
name|totBytes
condition|)
block|{
comment|// empty values at the end
name|ref
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
return|return
name|pos
operator|=
name|target
return|;
block|}
name|datIn
operator|.
name|seek
argument_list|(
name|fp
operator|+
name|addr
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
call|(
name|int
call|)
argument_list|(
name|target
operator|==
name|maxDoc
operator|-
literal|1
condition|?
name|totBytes
operator|-
name|addr
else|:
name|addresses
operator|.
name|get
argument_list|(
name|target
operator|+
literal|1
argument_list|)
operator|-
name|addr
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|.
name|bytes
operator|.
name|length
operator|<
name|size
condition|)
name|ref
operator|.
name|grow
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|ref
operator|.
name|length
operator|=
name|size
expr_stmt|;
name|datIn
operator|.
name|readBytes
argument_list|(
name|ref
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
name|pos
operator|=
name|target
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|advance
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
block|{
return|return
name|Values
operator|.
name|BYTES_VAR_STRAIGHT
return|;
block|}
block|}
block|}
end_class
end_unit
