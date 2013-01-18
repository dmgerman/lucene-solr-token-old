begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.diskdv
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|diskdv
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
name|Iterator
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
name|codecs
operator|.
name|CodecUtil
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
name|codecs
operator|.
name|DocValuesConsumer
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
name|FieldInfo
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
name|IndexFileNames
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
name|SegmentWriteState
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
name|packed
operator|.
name|PackedInts
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
operator|.
name|FormatAndBits
import|;
end_import
begin_comment
comment|// nocommit fix exception handling (make sure tests find problems first)
end_comment
begin_class
DECL|class|DiskDocValuesConsumer
class|class
name|DiskDocValuesConsumer
extends|extends
name|DocValuesConsumer
block|{
DECL|field|data
DECL|field|meta
specifier|final
name|IndexOutput
name|data
decl_stmt|,
name|meta
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|DiskDocValuesConsumer
name|DiskDocValuesConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dataName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
literal|"ddvd"
argument_list|)
decl_stmt|;
name|data
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|dataName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|data
argument_list|,
name|DiskDocValuesFormat
operator|.
name|DATA_CODEC
argument_list|,
name|DiskDocValuesFormat
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|String
name|metaName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
literal|"ddvm"
argument_list|)
decl_stmt|;
name|meta
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|metaName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|meta
argument_list|,
name|DiskDocValuesFormat
operator|.
name|METADATA_CODEC
argument_list|,
name|DiskDocValuesFormat
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|maxDoc
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addNumericField
specifier|public
name|void
name|addNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|long
name|minValue
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|maxValue
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
name|long
name|v
init|=
name|nv
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minValue
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxValue
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|meta
operator|.
name|writeLong
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
name|long
name|delta
init|=
name|maxValue
operator|-
name|minValue
decl_stmt|;
specifier|final
name|int
name|bitsPerValue
decl_stmt|;
if|if
condition|(
name|delta
operator|<
literal|0
condition|)
block|{
name|bitsPerValue
operator|=
literal|64
expr_stmt|;
block|}
else|else
block|{
name|bitsPerValue
operator|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
name|FormatAndBits
name|formatAndBits
init|=
name|PackedInts
operator|.
name|fastestFormatAndBits
argument_list|(
name|count
argument_list|,
name|bitsPerValue
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
comment|// nocommit: refactor this crap in PackedInts.java
comment|// e.g. Header.load()/save() or something rather than how it works now.
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|meta
argument_list|,
name|PackedInts
operator|.
name|CODEC_NAME
argument_list|,
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|bitsPerValue
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|formatAndBits
operator|.
name|format
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Writer
name|writer
init|=
name|PackedInts
operator|.
name|getWriterNoHeader
argument_list|(
name|data
argument_list|,
name|formatAndBits
operator|.
name|format
argument_list|,
name|count
argument_list|,
name|formatAndBits
operator|.
name|bitsPerValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
name|writer
operator|.
name|add
argument_list|(
name|nv
operator|.
name|longValue
argument_list|()
operator|-
name|minValue
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addBinaryField
specifier|public
name|void
name|addBinaryField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write the byte[] data
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|int
name|minLength
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|maxLength
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
specifier|final
name|long
name|startFP
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
name|minLength
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minLength
argument_list|,
name|v
operator|.
name|length
argument_list|)
expr_stmt|;
name|maxLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxLength
argument_list|,
name|v
operator|.
name|length
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeBytes
argument_list|(
name|v
operator|.
name|bytes
argument_list|,
name|v
operator|.
name|offset
argument_list|,
name|v
operator|.
name|length
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|meta
operator|.
name|writeVInt
argument_list|(
name|minLength
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|maxLength
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|startFP
argument_list|)
expr_stmt|;
comment|// if minLength == maxLength, its a fixed-length byte[], we are done (the addresses are implicit)
comment|// otherwise, we need to record the length fields...
comment|// TODO: make this more efficient. this is just as inefficient as 4.0 codec.... we can do much better.
if|if
condition|(
name|minLength
operator|!=
name|maxLength
condition|)
block|{
name|addNumericField
argument_list|(
name|field
argument_list|,
operator|new
name|Iterable
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Number
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|inner
init|=
name|values
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
name|long
name|addr
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|inner
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|next
parameter_list|()
block|{
name|BytesRef
name|b
init|=
name|inner
operator|.
name|next
argument_list|()
decl_stmt|;
name|addr
operator|+=
name|b
operator|.
name|length
expr_stmt|;
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|addr
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addSortedField
specifier|public
name|void
name|addSortedField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrd
parameter_list|)
throws|throws
name|IOException
block|{
name|addBinaryField
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|addNumericField
argument_list|(
name|field
argument_list|,
name|docToOrd
argument_list|)
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
comment|// nocommit: just write this to a RAMfile or something and flush it here, with #fields first.
comment|// this meta is a tiny file so this hurts nobody
name|meta
operator|.
name|writeVInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|data
argument_list|,
name|meta
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
