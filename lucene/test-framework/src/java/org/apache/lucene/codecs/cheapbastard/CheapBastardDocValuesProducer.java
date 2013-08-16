begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.cheapbastard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|cheapbastard
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|diskdv
operator|.
name|DiskDocValuesConsumer
operator|.
name|DELTA_COMPRESSED
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|diskdv
operator|.
name|DiskDocValuesConsumer
operator|.
name|GCD_COMPRESSED
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|diskdv
operator|.
name|DiskDocValuesConsumer
operator|.
name|TABLE_COMPRESSED
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
name|HashMap
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
name|DocValuesProducer
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
name|DocValuesProducer
operator|.
name|SortedSetDocsWithField
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
name|diskdv
operator|.
name|DiskDocValuesConsumer
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
name|diskdv
operator|.
name|DiskDocValuesFormat
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
name|CorruptIndexException
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
name|index
operator|.
name|SortedSetDocValues
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
name|Bits
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
name|BlockPackedReader
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
name|MonotonicBlockPackedReader
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
begin_class
DECL|class|CheapBastardDocValuesProducer
class|class
name|CheapBastardDocValuesProducer
extends|extends
name|DocValuesProducer
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
DECL|field|ordIndexes
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
name|ordIndexes
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
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|CheapBastardDocValuesProducer
name|CheapBastardDocValuesProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|,
name|String
name|dataCodec
parameter_list|,
name|String
name|dataExtension
parameter_list|,
name|String
name|metaCodec
parameter_list|,
name|String
name|metaExtension
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
name|metaExtension
argument_list|)
decl_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
comment|// read in the entries from the metadata file.
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
specifier|final
name|int
name|version
decl_stmt|;
try|try
block|{
name|version
operator|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|metaCodec
argument_list|,
name|DiskDocValuesFormat
operator|.
name|VERSION_CURRENT
argument_list|,
name|DiskDocValuesFormat
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
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
name|ordIndexes
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
name|in
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
name|success
operator|=
literal|false
expr_stmt|;
try|try
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
name|dataExtension
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
specifier|final
name|int
name|version2
init|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|data
argument_list|,
name|dataCodec
argument_list|,
name|DiskDocValuesFormat
operator|.
name|VERSION_CURRENT
argument_list|,
name|DiskDocValuesFormat
operator|.
name|VERSION_CURRENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|!=
name|version2
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Versions mismatch"
argument_list|)
throw|;
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
name|closeWhileHandlingException
argument_list|(
name|this
operator|.
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readFields
specifier|private
name|void
name|readFields
parameter_list|(
name|IndexInput
name|meta
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
name|byte
name|type
init|=
name|meta
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|DiskDocValuesFormat
operator|.
name|NUMERIC
condition|)
block|{
name|numerics
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readNumericEntry
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DiskDocValuesFormat
operator|.
name|BINARY
condition|)
block|{
name|BinaryEntry
name|b
init|=
name|readBinaryEntry
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
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DiskDocValuesFormat
operator|.
name|SORTED
condition|)
block|{
comment|// sorted = binary + numeric
if|if
condition|(
name|meta
operator|.
name|readVInt
argument_list|()
operator|!=
name|fieldNumber
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sorted entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
if|if
condition|(
name|meta
operator|.
name|readByte
argument_list|()
operator|!=
name|DiskDocValuesFormat
operator|.
name|BINARY
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sorted entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
name|BinaryEntry
name|b
init|=
name|readBinaryEntry
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
name|meta
operator|.
name|readVInt
argument_list|()
operator|!=
name|fieldNumber
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sorted entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
if|if
condition|(
name|meta
operator|.
name|readByte
argument_list|()
operator|!=
name|DiskDocValuesFormat
operator|.
name|NUMERIC
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sorted entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
name|NumericEntry
name|n
init|=
name|readNumericEntry
argument_list|(
name|meta
argument_list|)
decl_stmt|;
name|ords
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|DiskDocValuesFormat
operator|.
name|SORTED_SET
condition|)
block|{
comment|// sortedset = binary + numeric + ordIndex
if|if
condition|(
name|meta
operator|.
name|readVInt
argument_list|()
operator|!=
name|fieldNumber
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sortedset entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
if|if
condition|(
name|meta
operator|.
name|readByte
argument_list|()
operator|!=
name|DiskDocValuesFormat
operator|.
name|BINARY
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sortedset entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
name|BinaryEntry
name|b
init|=
name|readBinaryEntry
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
name|meta
operator|.
name|readVInt
argument_list|()
operator|!=
name|fieldNumber
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sortedset entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
if|if
condition|(
name|meta
operator|.
name|readByte
argument_list|()
operator|!=
name|DiskDocValuesFormat
operator|.
name|NUMERIC
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sortedset entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
name|NumericEntry
name|n1
init|=
name|readNumericEntry
argument_list|(
name|meta
argument_list|)
decl_stmt|;
name|ords
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|n1
argument_list|)
expr_stmt|;
if|if
condition|(
name|meta
operator|.
name|readVInt
argument_list|()
operator|!=
name|fieldNumber
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sortedset entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
if|if
condition|(
name|meta
operator|.
name|readByte
argument_list|()
operator|!=
name|DiskDocValuesFormat
operator|.
name|NUMERIC
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sortedset entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
name|NumericEntry
name|n2
init|=
name|readNumericEntry
argument_list|(
name|meta
argument_list|)
decl_stmt|;
name|ordIndexes
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|n2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid type: "
operator|+
name|type
operator|+
literal|", resource="
operator|+
name|meta
argument_list|)
throw|;
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
DECL|method|readNumericEntry
specifier|static
name|NumericEntry
name|readNumericEntry
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
name|format
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|packedIntsVersion
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
name|entry
operator|.
name|count
operator|=
name|meta
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|blockSize
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|entry
operator|.
name|format
condition|)
block|{
case|case
name|GCD_COMPRESSED
case|:
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
name|gcd
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
break|break;
case|case
name|TABLE_COMPRESSED
case|:
if|if
condition|(
name|entry
operator|.
name|count
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Cannot use TABLE_COMPRESSED with more than MAX_VALUE values, input="
operator|+
name|meta
argument_list|)
throw|;
block|}
specifier|final
name|int
name|uniqueValues
init|=
name|meta
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|uniqueValues
operator|>
literal|256
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"TABLE_COMPRESSED cannot have more than 256 distinct values, input="
operator|+
name|meta
argument_list|)
throw|;
block|}
name|entry
operator|.
name|table
operator|=
operator|new
name|long
index|[
name|uniqueValues
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|uniqueValues
condition|;
operator|++
name|i
control|)
block|{
name|entry
operator|.
name|table
index|[
name|i
index|]
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|DELTA_COMPRESSED
case|:
break|break;
default|default:
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Unknown format: "
operator|+
name|entry
operator|.
name|format
operator|+
literal|", input="
operator|+
name|meta
argument_list|)
throw|;
block|}
return|return
name|entry
return|;
block|}
DECL|method|readBinaryEntry
specifier|static
name|BinaryEntry
name|readBinaryEntry
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
name|int
name|format
init|=
name|meta
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|format
operator|!=
name|DiskDocValuesConsumer
operator|.
name|BINARY_FIXED_UNCOMPRESSED
operator|&&
name|format
operator|!=
name|DiskDocValuesConsumer
operator|.
name|BINARY_VARIABLE_UNCOMPRESSED
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Unexpected format for binary entry: "
operator|+
name|format
operator|+
literal|", input="
operator|+
name|meta
argument_list|)
throw|;
block|}
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
name|readVLong
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
if|if
condition|(
name|entry
operator|.
name|minLength
operator|!=
name|entry
operator|.
name|maxLength
condition|)
block|{
name|entry
operator|.
name|addressesOffset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|packedIntsVersion
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|blockSize
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
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
name|LongNumericDocValues
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
switch|switch
condition|(
name|entry
operator|.
name|format
condition|)
block|{
case|case
name|DELTA_COMPRESSED
case|:
specifier|final
name|BlockPackedReader
name|reader
init|=
operator|new
name|BlockPackedReader
argument_list|(
name|data
argument_list|,
name|entry
operator|.
name|packedIntsVersion
argument_list|,
name|entry
operator|.
name|blockSize
argument_list|,
name|entry
operator|.
name|count
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongNumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|long
name|id
parameter_list|)
block|{
return|return
name|reader
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
return|;
case|case
name|GCD_COMPRESSED
case|:
specifier|final
name|long
name|min
init|=
name|entry
operator|.
name|minValue
decl_stmt|;
specifier|final
name|long
name|mult
init|=
name|entry
operator|.
name|gcd
decl_stmt|;
specifier|final
name|BlockPackedReader
name|quotientReader
init|=
operator|new
name|BlockPackedReader
argument_list|(
name|data
argument_list|,
name|entry
operator|.
name|packedIntsVersion
argument_list|,
name|entry
operator|.
name|blockSize
argument_list|,
name|entry
operator|.
name|count
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongNumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|long
name|id
parameter_list|)
block|{
return|return
name|min
operator|+
name|mult
operator|*
name|quotientReader
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
return|;
case|case
name|TABLE_COMPRESSED
case|:
specifier|final
name|long
index|[]
name|table
init|=
name|entry
operator|.
name|table
decl_stmt|;
specifier|final
name|int
name|bitsRequired
init|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|table
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|PackedInts
operator|.
name|Reader
name|ords
init|=
name|PackedInts
operator|.
name|getDirectReaderNoHeader
argument_list|(
name|data
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|entry
operator|.
name|packedIntsVersion
argument_list|,
operator|(
name|int
operator|)
name|entry
operator|.
name|count
argument_list|,
name|bitsRequired
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongNumericDocValues
argument_list|()
block|{
annotation|@
name|Override
name|long
name|get
parameter_list|(
name|long
name|id
parameter_list|)
block|{
return|return
name|table
index|[
operator|(
name|int
operator|)
name|ords
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|id
argument_list|)
index|]
return|;
block|}
block|}
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
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
name|LongBinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|long
name|id
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
name|id
operator|*
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
comment|// NOTE: we could have one buffer, but various consumers (e.g. FieldComparatorSource)
comment|// assume "they" own the bytes after calling this!
specifier|final
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|bytes
operator|.
name|maxLength
index|]
decl_stmt|;
name|data
operator|.
name|readBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|.
name|bytes
operator|=
name|buffer
expr_stmt|;
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|buffer
operator|.
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
name|bytes
operator|.
name|addressesOffset
argument_list|)
expr_stmt|;
specifier|final
name|MonotonicBlockPackedReader
name|addresses
init|=
operator|new
name|MonotonicBlockPackedReader
argument_list|(
name|data
argument_list|,
name|bytes
operator|.
name|packedIntsVersion
argument_list|,
name|bytes
operator|.
name|blockSize
argument_list|,
name|bytes
operator|.
name|count
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|LongBinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|long
name|id
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|long
name|startAddress
init|=
name|bytes
operator|.
name|offset
operator|+
operator|(
name|id
operator|==
literal|0
condition|?
literal|0
else|:
operator|+
name|addresses
operator|.
name|get
argument_list|(
name|id
operator|-
literal|1
argument_list|)
operator|)
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
name|id
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
comment|// NOTE: we could have one buffer, but various consumers (e.g. FieldComparatorSource)
comment|// assume "they" own the bytes after calling this!
specifier|final
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|data
operator|.
name|readBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|.
name|bytes
operator|=
name|buffer
expr_stmt|;
name|result
operator|.
name|offset
operator|=
literal|0
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
name|int
name|valueCount
init|=
operator|(
name|int
operator|)
name|binaries
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
operator|.
name|count
decl_stmt|;
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
name|valueCount
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedSet
specifier|public
name|SortedSetDocValues
name|getSortedSet
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|valueCount
init|=
name|binaries
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
operator|.
name|count
decl_stmt|;
specifier|final
name|LongBinaryDocValues
name|binary
init|=
operator|(
name|LongBinaryDocValues
operator|)
name|getBinary
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|LongNumericDocValues
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
name|NumericEntry
name|entry
init|=
name|ordIndexes
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
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
name|MonotonicBlockPackedReader
name|ordIndex
init|=
operator|new
name|MonotonicBlockPackedReader
argument_list|(
name|data
argument_list|,
name|entry
operator|.
name|packedIntsVersion
argument_list|,
name|entry
operator|.
name|blockSize
argument_list|,
name|entry
operator|.
name|count
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|SortedSetDocValues
argument_list|()
block|{
name|long
name|offset
decl_stmt|;
name|long
name|endOffset
decl_stmt|;
annotation|@
name|Override
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
if|if
condition|(
name|offset
operator|==
name|endOffset
condition|)
block|{
return|return
name|NO_MORE_ORDS
return|;
block|}
else|else
block|{
name|long
name|ord
init|=
name|ordinals
operator|.
name|get
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|offset
operator|++
expr_stmt|;
return|return
name|ord
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|offset
operator|=
operator|(
name|docID
operator|==
literal|0
condition|?
literal|0
else|:
name|ordIndex
operator|.
name|get
argument_list|(
name|docID
operator|-
literal|1
argument_list|)
operator|)
expr_stmt|;
name|endOffset
operator|=
name|ordIndex
operator|.
name|get
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|long
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
name|long
name|getValueCount
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getDocsWithField
specifier|public
name|Bits
name|getDocsWithField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|field
operator|.
name|getDocValuesType
argument_list|()
operator|==
name|FieldInfo
operator|.
name|DocValuesType
operator|.
name|SORTED_SET
condition|)
block|{
return|return
operator|new
name|SortedSetDocsWithField
argument_list|(
name|getSortedSet
argument_list|(
name|field
argument_list|)
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Bits
operator|.
name|MatchAllBits
argument_list|(
name|maxDoc
argument_list|)
return|;
block|}
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
DECL|class|NumericEntry
specifier|static
class|class
name|NumericEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|format
name|int
name|format
decl_stmt|;
DECL|field|packedIntsVersion
name|int
name|packedIntsVersion
decl_stmt|;
DECL|field|count
name|long
name|count
decl_stmt|;
DECL|field|blockSize
name|int
name|blockSize
decl_stmt|;
DECL|field|minValue
name|long
name|minValue
decl_stmt|;
DECL|field|gcd
name|long
name|gcd
decl_stmt|;
DECL|field|table
name|long
name|table
index|[]
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
name|long
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
DECL|field|addressesOffset
name|long
name|addressesOffset
decl_stmt|;
DECL|field|packedIntsVersion
name|int
name|packedIntsVersion
decl_stmt|;
DECL|field|blockSize
name|int
name|blockSize
decl_stmt|;
block|}
comment|// internally we compose complex dv (sorted/sortedset) from other ones
DECL|class|LongNumericDocValues
specifier|static
specifier|abstract
class|class
name|LongNumericDocValues
extends|extends
name|NumericDocValues
block|{
annotation|@
name|Override
DECL|method|get
specifier|public
specifier|final
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|get
argument_list|(
operator|(
name|long
operator|)
name|docID
argument_list|)
return|;
block|}
DECL|method|get
specifier|abstract
name|long
name|get
parameter_list|(
name|long
name|id
parameter_list|)
function_decl|;
block|}
DECL|class|LongBinaryDocValues
specifier|static
specifier|abstract
class|class
name|LongBinaryDocValues
extends|extends
name|BinaryDocValues
block|{
annotation|@
name|Override
DECL|method|get
specifier|public
specifier|final
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
name|get
argument_list|(
operator|(
name|long
operator|)
name|docID
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|get
specifier|abstract
name|void
name|get
parameter_list|(
name|long
name|id
parameter_list|,
name|BytesRef
name|Result
parameter_list|)
function_decl|;
block|}
block|}
end_class
end_unit
