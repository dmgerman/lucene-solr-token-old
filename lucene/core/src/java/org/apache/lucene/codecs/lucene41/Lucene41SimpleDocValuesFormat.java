begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene41
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|SimpleDVConsumer
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
name|SimpleDVProducer
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
name|SimpleDocValuesFormat
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
name|index
operator|.
name|DocValues
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
name|FieldInfos
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
name|NumericDocValues
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
name|SegmentReadState
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
name|index
operator|.
name|SortedDocValues
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
begin_class
DECL|class|Lucene41SimpleDocValuesFormat
specifier|public
class|class
name|Lucene41SimpleDocValuesFormat
extends|extends
name|SimpleDocValuesFormat
block|{
DECL|method|Lucene41SimpleDocValuesFormat
specifier|public
name|Lucene41SimpleDocValuesFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"Lucene41"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|SimpleDVConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene41SimpleDocValuesConsumer
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|SimpleDVProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene41SimpleDocValuesProducer
argument_list|(
name|state
argument_list|)
return|;
block|}
DECL|class|Lucene41SimpleDocValuesConsumer
specifier|static
class|class
name|Lucene41SimpleDocValuesConsumer
extends|extends
name|SimpleDVConsumer
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
DECL|method|Lucene41SimpleDocValuesConsumer
name|Lucene41SimpleDocValuesConsumer
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
literal|"dvd"
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
literal|"dvm"
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
name|addr
return|;
comment|// nocommit don't box
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
DECL|class|NumericEntry
specifier|static
class|class
name|NumericEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|minValue
name|long
name|minValue
decl_stmt|;
DECL|field|header
name|PackedInts
operator|.
name|Header
name|header
decl_stmt|;
block|}
DECL|class|BinaryEntry
specifier|static
class|class
name|BinaryEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|minLength
name|int
name|minLength
decl_stmt|;
DECL|field|maxLength
name|int
name|maxLength
decl_stmt|;
block|}
DECL|class|Lucene41SimpleDocValuesProducer
specifier|static
class|class
name|Lucene41SimpleDocValuesProducer
extends|extends
name|SimpleDVProducer
block|{
DECL|field|numerics
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
name|numerics
decl_stmt|;
DECL|field|ords
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
name|ords
decl_stmt|;
DECL|field|binaries
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|BinaryEntry
argument_list|>
name|binaries
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|IndexInput
name|data
decl_stmt|;
DECL|method|Lucene41SimpleDocValuesProducer
name|Lucene41SimpleDocValuesProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|"dvm"
argument_list|)
decl_stmt|;
comment|// slurpy slurp
name|IndexInput
name|in
init|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|metaName
argument_list|,
name|state
operator|.
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|numerics
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
argument_list|()
expr_stmt|;
name|ords
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
argument_list|()
expr_stmt|;
name|binaries
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|BinaryEntry
argument_list|>
argument_list|()
expr_stmt|;
name|readFields
argument_list|(
name|numerics
argument_list|,
name|ords
argument_list|,
name|binaries
argument_list|,
name|in
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"dvd"
argument_list|)
decl_stmt|;
name|data
operator|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|dataName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|readFields
specifier|static
name|void
name|readFields
parameter_list|(
name|Map
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
name|numerics
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
name|ords
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|BinaryEntry
argument_list|>
name|binaries
parameter_list|,
name|IndexInput
name|meta
parameter_list|,
name|FieldInfos
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|fieldNumber
init|=
name|meta
operator|.
name|readVInt
argument_list|()
decl_stmt|;
while|while
condition|(
name|fieldNumber
operator|!=
operator|-
literal|1
condition|)
block|{
name|DocValues
operator|.
name|Type
name|type
init|=
name|infos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
operator|.
name|getDocValuesType
argument_list|()
decl_stmt|;
if|if
condition|(
name|DocValues
operator|.
name|isNumber
argument_list|(
name|type
argument_list|)
operator|||
name|DocValues
operator|.
name|isFloat
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|numerics
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readNumericField
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DocValues
operator|.
name|isBytes
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|BinaryEntry
name|b
init|=
name|readBinaryField
argument_list|(
name|meta
argument_list|)
decl_stmt|;
name|binaries
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|b
operator|.
name|minLength
operator|!=
name|b
operator|.
name|maxLength
condition|)
block|{
name|fieldNumber
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|// waste
comment|// variable length byte[]: read addresses as a numeric dv field
name|numerics
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readNumericField
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|DocValues
operator|.
name|isSortedBytes
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|BinaryEntry
name|b
init|=
name|readBinaryField
argument_list|(
name|meta
argument_list|)
decl_stmt|;
name|binaries
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|b
operator|.
name|minLength
operator|!=
name|b
operator|.
name|maxLength
condition|)
block|{
name|fieldNumber
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|// waste
comment|// variable length byte[]: read addresses as a numeric dv field
name|numerics
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readNumericField
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// sorted byte[]: read ords as a numeric dv field
name|fieldNumber
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
comment|// waste
name|ords
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readNumericField
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fieldNumber
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|readNumericField
specifier|static
name|NumericEntry
name|readNumericField
parameter_list|(
name|IndexInput
name|meta
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericEntry
name|entry
init|=
operator|new
name|NumericEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|minValue
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|header
operator|=
name|PackedInts
operator|.
name|readHeader
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|entry
operator|.
name|offset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
return|return
name|entry
return|;
block|}
DECL|method|readBinaryField
specifier|static
name|BinaryEntry
name|readBinaryField
parameter_list|(
name|IndexInput
name|meta
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryEntry
name|entry
init|=
operator|new
name|BinaryEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|minLength
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|maxLength
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|count
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|offset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
return|return
name|entry
return|;
block|}
annotation|@
name|Override
DECL|method|getNumeric
specifier|public
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit: user can currently get back a numericDV of the addresses...
name|NumericEntry
name|entry
init|=
name|numerics
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
return|return
name|getNumeric
argument_list|(
name|field
argument_list|,
name|entry
argument_list|)
return|;
block|}
DECL|method|getNumeric
specifier|private
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|NumericEntry
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit: what are we doing with clone?!
specifier|final
name|IndexInput
name|data
init|=
name|this
operator|.
name|data
operator|.
name|clone
argument_list|()
decl_stmt|;
name|data
operator|.
name|seek
argument_list|(
name|entry
operator|.
name|offset
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Reader
name|reader
init|=
name|PackedInts
operator|.
name|getDirectReaderNoHeader
argument_list|(
name|data
argument_list|,
name|entry
operator|.
name|header
argument_list|)
decl_stmt|;
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|entry
operator|.
name|minValue
operator|+
name|reader
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|entry
operator|.
name|header
operator|.
name|getValueCount
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getBinary
specifier|public
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryEntry
name|bytes
init|=
name|binaries
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|.
name|minLength
operator|==
name|bytes
operator|.
name|maxLength
condition|)
block|{
return|return
name|getFixedBinary
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getVariableBinary
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
return|;
block|}
block|}
DECL|method|getFixedBinary
specifier|private
name|BinaryDocValues
name|getFixedBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|BinaryEntry
name|bytes
parameter_list|)
block|{
comment|// nocommit: what are we doing with clone?!
specifier|final
name|IndexInput
name|data
init|=
name|this
operator|.
name|data
operator|.
name|clone
argument_list|()
decl_stmt|;
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|long
name|address
init|=
name|bytes
operator|.
name|offset
operator|+
name|docID
operator|*
operator|(
name|long
operator|)
name|bytes
operator|.
name|maxLength
decl_stmt|;
try|try
block|{
name|data
operator|.
name|seek
argument_list|(
name|address
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|length
operator|<
name|bytes
operator|.
name|maxLength
condition|)
block|{
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|bytes
operator|.
name|maxLength
index|]
expr_stmt|;
block|}
name|data
operator|.
name|readBytes
argument_list|(
name|result
operator|.
name|bytes
argument_list|,
name|result
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|maxLength
argument_list|)
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|bytes
operator|.
name|maxLength
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|count
return|;
block|}
block|}
return|;
block|}
DECL|method|getVariableBinary
specifier|private
name|BinaryDocValues
name|getVariableBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|BinaryEntry
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit: what are we doing with clone?!
specifier|final
name|IndexInput
name|data
init|=
name|this
operator|.
name|data
operator|.
name|clone
argument_list|()
decl_stmt|;
specifier|final
name|NumericDocValues
name|addresses
init|=
name|getNumeric
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|long
name|startAddress
init|=
name|docID
operator|==
literal|0
condition|?
name|bytes
operator|.
name|offset
else|:
name|bytes
operator|.
name|offset
operator|+
name|addresses
operator|.
name|get
argument_list|(
name|docID
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|endAddress
init|=
name|bytes
operator|.
name|offset
operator|+
name|addresses
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|endAddress
operator|-
name|startAddress
argument_list|)
decl_stmt|;
try|try
block|{
name|data
operator|.
name|seek
argument_list|(
name|startAddress
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|length
operator|<
name|length
condition|)
block|{
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
block|}
name|data
operator|.
name|readBytes
argument_list|(
name|result
operator|.
name|bytes
argument_list|,
name|result
operator|.
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|count
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getSorted
specifier|public
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BinaryDocValues
name|binary
init|=
name|getBinary
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|NumericDocValues
name|ordinals
init|=
name|getNumeric
argument_list|(
name|field
argument_list|,
name|ords
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|SortedDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|ordinals
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|binary
operator|.
name|get
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|ordinals
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|binary
operator|.
name|size
argument_list|()
return|;
block|}
block|}
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
name|data
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
