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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
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
name|store
operator|.
name|IndexOutput
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
name|ByteBlockPool
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
name|ByteBlockPool
operator|.
name|DirectTrackingAllocator
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
name|IOUtils
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
begin_comment
comment|// Simplest storage: stores fixed length byte[] per
end_comment
begin_comment
comment|// document, with no dedup and no sorting.
end_comment
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|FixedStraightBytesImpl
class|class
name|FixedStraightBytesImpl
block|{
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"FixedStraightBytes"
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
DECL|field|size
specifier|private
name|int
name|size
init|=
operator|-
literal|1
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
DECL|field|pool
specifier|private
specifier|final
name|ByteBlockPool
name|pool
decl_stmt|;
DECL|field|merge
specifier|private
name|boolean
name|merge
decl_stmt|;
DECL|field|byteBlockSize
specifier|private
specifier|final
name|int
name|byteBlockSize
decl_stmt|;
DECL|field|datOut
specifier|private
name|IndexOutput
name|datOut
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
name|bytesUsed
argument_list|)
expr_stmt|;
name|pool
operator|=
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|DirectTrackingAllocator
argument_list|(
name|bytesUsed
argument_list|)
argument_list|)
expr_stmt|;
name|byteBlockSize
operator|=
name|BYTE_BLOCK_SIZE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
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
assert|assert
name|lastDocID
operator|<
name|docID
assert|;
assert|assert
operator|!
name|merge
assert|;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|>
name|BYTE_BLOCK_SIZE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"bytes arrays> "
operator|+
name|Short
operator|.
name|MAX_VALUE
operator|+
literal|" are not supported"
argument_list|)
throw|;
block|}
name|size
operator|=
name|bytes
operator|.
name|length
expr_stmt|;
name|pool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|.
name|length
operator|!=
name|size
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"expected bytes size="
operator|+
name|size
operator|+
literal|" but got "
operator|+
name|bytes
operator|.
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|lastDocID
operator|+
literal|1
operator|<
name|docID
condition|)
block|{
name|advancePool
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
name|pool
operator|.
name|copy
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
block|}
DECL|method|advancePool
specifier|private
specifier|final
name|void
name|advancePool
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
assert|assert
operator|!
name|merge
assert|;
name|long
name|numBytes
init|=
operator|(
name|docID
operator|-
operator|(
name|lastDocID
operator|+
literal|1
operator|)
operator|)
operator|*
name|size
decl_stmt|;
while|while
condition|(
name|numBytes
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|numBytes
operator|+
name|pool
operator|.
name|byteUpto
operator|<
name|byteBlockSize
condition|)
block|{
name|pool
operator|.
name|byteUpto
operator|+=
name|numBytes
expr_stmt|;
name|numBytes
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|numBytes
operator|-=
name|byteBlockSize
operator|-
name|pool
operator|.
name|byteUpto
expr_stmt|;
name|pool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
block|}
assert|assert
name|numBytes
operator|==
literal|0
assert|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|protected
name|void
name|merge
parameter_list|(
name|MergeState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|merge
operator|=
literal|true
expr_stmt|;
name|datOut
operator|=
name|getDataOut
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|state
operator|.
name|bits
operator|==
literal|null
operator|&&
name|state
operator|.
name|reader
operator|instanceof
name|Reader
condition|)
block|{
name|Reader
name|reader
init|=
operator|(
name|Reader
operator|)
name|state
operator|.
name|reader
decl_stmt|;
specifier|final
name|int
name|maxDocs
init|=
name|reader
operator|.
name|maxDoc
decl_stmt|;
if|if
condition|(
name|maxDocs
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|size
operator|=
name|reader
operator|.
name|size
expr_stmt|;
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lastDocID
operator|+
literal|1
operator|<
name|state
operator|.
name|docBase
condition|)
block|{
name|fill
argument_list|(
name|datOut
argument_list|,
name|state
operator|.
name|docBase
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
name|state
operator|.
name|docBase
operator|-
literal|1
expr_stmt|;
block|}
comment|// TODO should we add a transfer to API to each reader?
specifier|final
name|IndexInput
name|cloneData
init|=
name|reader
operator|.
name|cloneData
argument_list|()
decl_stmt|;
try|try
block|{
name|datOut
operator|.
name|copyBytes
argument_list|(
name|cloneData
argument_list|,
name|size
operator|*
name|maxDocs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
literal|true
argument_list|,
name|cloneData
argument_list|)
expr_stmt|;
block|}
name|lastDocID
operator|+=
name|maxDocs
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|merge
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
operator|!
name|success
argument_list|,
name|datOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|mergeDoc
specifier|protected
name|void
name|mergeDoc
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|lastDocID
operator|<
name|docID
assert|;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|size
operator|=
name|bytesRef
operator|.
name|length
expr_stmt|;
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
assert|assert
name|size
operator|==
name|bytesRef
operator|.
name|length
assert|;
if|if
condition|(
name|lastDocID
operator|+
literal|1
operator|<
name|docID
condition|)
block|{
name|fill
argument_list|(
name|datOut
argument_list|,
name|docID
argument_list|)
expr_stmt|;
block|}
name|datOut
operator|.
name|writeBytes
argument_list|(
name|bytesRef
operator|.
name|bytes
argument_list|,
name|bytesRef
operator|.
name|offset
argument_list|,
name|bytesRef
operator|.
name|length
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
block|}
comment|// Fills up to but not including this docID
DECL|method|fill
specifier|private
name|void
name|fill
parameter_list|(
name|IndexOutput
name|datOut
parameter_list|,
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|size
operator|>=
literal|0
assert|;
specifier|final
name|long
name|numBytes
init|=
operator|(
name|docID
operator|-
operator|(
name|lastDocID
operator|+
literal|1
operator|)
operator|)
operator|*
name|size
decl_stmt|;
specifier|final
name|byte
name|zero
init|=
literal|0
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numBytes
condition|;
name|i
operator|++
control|)
block|{
name|datOut
operator|.
name|writeByte
argument_list|(
name|zero
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finish
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|merge
condition|)
block|{
comment|// indexing path - no disk IO until here
assert|assert
name|datOut
operator|==
literal|null
assert|;
name|datOut
operator|=
name|getDataOut
argument_list|()
expr_stmt|;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|datOut
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|pool
operator|.
name|writePool
argument_list|(
name|datOut
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lastDocID
operator|+
literal|1
operator|<
name|docCount
condition|)
block|{
name|fill
argument_list|(
name|datOut
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// merge path - datOut should be initialized
assert|assert
name|datOut
operator|!=
literal|null
assert|;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no data added
name|datOut
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fill
argument_list|(
name|datOut
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|dropBuffersAndReset
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|closeSafely
argument_list|(
operator|!
name|success
argument_list|,
name|datOut
argument_list|)
expr_stmt|;
block|}
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
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
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
literal|false
argument_list|)
expr_stmt|;
name|size
operator|=
name|datIn
operator|.
name|readInt
argument_list|()
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
name|size
operator|==
literal|1
condition|?
operator|new
name|SingleByteSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|maxDoc
argument_list|)
else|:
operator|new
name|StraightBytesSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|size
argument_list|,
name|maxDoc
argument_list|)
return|;
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
block|}
comment|// specialized version for single bytes
DECL|class|SingleByteSource
specifier|private
specifier|static
class|class
name|SingleByteSource
extends|extends
name|Source
block|{
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
DECL|method|SingleByteSource
specifier|public
name|SingleByteSource
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
try|try
block|{
name|data
operator|=
operator|new
name|byte
index|[
name|maxDoc
index|]
expr_stmt|;
name|datIn
operator|.
name|readBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeSafely
argument_list|(
literal|false
argument_list|,
name|datIn
argument_list|)
expr_stmt|;
block|}
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
name|bytesRef
operator|.
name|length
operator|=
literal|1
expr_stmt|;
name|bytesRef
operator|.
name|bytes
operator|=
name|data
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
name|docID
expr_stmt|;
return|return
name|bytesRef
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|ValueType
name|type
parameter_list|()
block|{
return|return
name|ValueType
operator|.
name|BYTES_FIXED_STRAIGHT
return|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SourceEnum
argument_list|(
name|attrSource
argument_list|,
name|type
argument_list|()
argument_list|,
name|this
argument_list|,
name|maxDoc
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
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
name|numDocs
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
name|bytesRef
operator|.
name|length
operator|=
literal|1
expr_stmt|;
name|bytesRef
operator|.
name|bytes
operator|=
name|data
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
name|target
expr_stmt|;
return|return
name|pos
operator|=
name|target
return|;
block|}
block|}
return|;
block|}
block|}
DECL|class|StraightBytesSource
specifier|private
specifier|static
class|class
name|StraightBytesSource
extends|extends
name|BytesBaseSource
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|StraightBytesSource
specifier|public
name|StraightBytesSource
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|datIn
argument_list|,
literal|null
argument_list|,
operator|new
name|PagedBytes
argument_list|(
name|PAGED_BYTES_BITS
argument_list|)
argument_list|,
name|size
operator|*
name|maxDoc
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
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
return|return
name|data
operator|.
name|fillSlice
argument_list|(
name|bytesRef
argument_list|,
name|docID
operator|*
name|size
argument_list|,
name|size
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
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|ValueType
name|type
parameter_list|()
block|{
return|return
name|ValueType
operator|.
name|BYTES_FIXED_STRAIGHT
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
name|maxDoc
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
name|FixedStraightBytesEnum
argument_list|(
name|source
argument_list|,
name|cloneData
argument_list|()
argument_list|,
name|size
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
DECL|class|FixedStraightBytesEnum
specifier|private
specifier|static
specifier|final
class|class
name|FixedStraightBytesEnum
extends|extends
name|ValuesEnum
block|{
DECL|field|datIn
specifier|private
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|fp
specifier|private
specifier|final
name|long
name|fp
decl_stmt|;
DECL|method|FixedStraightBytesEnum
specifier|public
name|FixedStraightBytesEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|datIn
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|ValueType
operator|.
name|BYTES_FIXED_STRAIGHT
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
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|bytesRef
operator|.
name|grow
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|bytesRef
operator|.
name|length
operator|=
name|size
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|fp
operator|=
name|datIn
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
DECL|method|copyFrom
specifier|protected
name|void
name|copyFrom
parameter_list|(
name|ValuesEnum
name|valuesEnum
parameter_list|)
block|{
name|bytesRef
operator|=
name|valuesEnum
operator|.
name|bytesRef
expr_stmt|;
if|if
condition|(
name|bytesRef
operator|.
name|bytes
operator|.
name|length
operator|<
name|size
condition|)
block|{
name|bytesRef
operator|.
name|grow
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
name|bytesRef
operator|.
name|length
operator|=
name|size
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
block|}
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
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
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
operator|||
name|size
operator|==
literal|0
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
if|if
condition|(
operator|(
name|target
operator|-
literal|1
operator|)
operator|!=
name|pos
condition|)
comment|// pos inc == 1
name|datIn
operator|.
name|seek
argument_list|(
name|fp
operator|+
name|target
operator|*
name|size
argument_list|)
expr_stmt|;
name|datIn
operator|.
name|readBytes
argument_list|(
name|bytesRef
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
if|if
condition|(
name|pos
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
name|ValueType
name|type
parameter_list|()
block|{
return|return
name|ValueType
operator|.
name|BYTES_FIXED_STRAIGHT
return|;
block|}
block|}
block|}
end_class
end_unit
