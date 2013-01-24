begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|codecs
operator|.
name|lucene40
operator|.
name|Lucene40FieldInfosReader
operator|.
name|LegacyDocValuesType
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
name|CompoundFileDirectory
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
begin_class
DECL|class|Lucene40DocValuesWriter
class|class
name|Lucene40DocValuesWriter
extends|extends
name|DocValuesConsumer
block|{
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|SegmentWriteState
name|state
decl_stmt|;
DECL|field|legacyKey
specifier|private
specifier|final
name|String
name|legacyKey
decl_stmt|;
comment|// note: intentionally ignores seg suffix
comment|// String filename = IndexFileNames.segmentFileName(state.segmentInfo.name, "dv", IndexFileNames.COMPOUND_FILE_EXTENSION);
DECL|method|Lucene40DocValuesWriter
name|Lucene40DocValuesWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|String
name|filename
parameter_list|,
name|String
name|legacyKey
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|legacyKey
operator|=
name|legacyKey
expr_stmt|;
name|this
operator|.
name|dir
operator|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|filename
argument_list|,
name|state
operator|.
name|context
argument_list|,
literal|true
argument_list|)
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
comment|// examine the values to determine best type to use
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
for|for
control|(
name|Number
name|n
range|:
name|values
control|)
block|{
name|long
name|v
init|=
name|n
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
block|}
name|String
name|fileName
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|IndexOutput
name|data
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
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
if|if
condition|(
name|minValue
operator|>=
name|Byte
operator|.
name|MIN_VALUE
operator|&&
name|maxValue
operator|<=
name|Byte
operator|.
name|MAX_VALUE
operator|&&
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxValue
operator|-
name|minValue
argument_list|)
operator|>
literal|4
condition|)
block|{
comment|// fits in a byte[], would be more than 4bpv, just write byte[]
name|addBytesField
argument_list|(
name|field
argument_list|,
name|data
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|minValue
operator|>=
name|Short
operator|.
name|MIN_VALUE
operator|&&
name|maxValue
operator|<=
name|Short
operator|.
name|MAX_VALUE
operator|&&
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxValue
operator|-
name|minValue
argument_list|)
operator|>
literal|8
condition|)
block|{
comment|// fits in a short[], would be more than 8bpv, just write short[]
name|addShortsField
argument_list|(
name|field
argument_list|,
name|data
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|minValue
operator|>=
name|Integer
operator|.
name|MIN_VALUE
operator|&&
name|maxValue
operator|<=
name|Integer
operator|.
name|MAX_VALUE
operator|&&
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxValue
operator|-
name|minValue
argument_list|)
operator|>
literal|16
condition|)
block|{
comment|// fits in a int[], would be more than 16bpv, just write int[]
name|addIntsField
argument_list|(
name|field
argument_list|,
name|data
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addVarIntsField
argument_list|(
name|field
argument_list|,
name|data
argument_list|,
name|values
argument_list|,
name|minValue
argument_list|,
name|maxValue
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
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addBytesField
specifier|private
name|void
name|addBytesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|IndexOutput
name|output
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
name|field
operator|.
name|putAttribute
argument_list|(
name|legacyKey
argument_list|,
name|LegacyDocValuesType
operator|.
name|FIXED_INTS_8
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// size
for|for
control|(
name|Number
name|n
range|:
name|values
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
name|n
operator|.
name|byteValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addShortsField
specifier|private
name|void
name|addShortsField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|IndexOutput
name|output
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
name|field
operator|.
name|putAttribute
argument_list|(
name|legacyKey
argument_list|,
name|LegacyDocValuesType
operator|.
name|FIXED_INTS_16
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// size
for|for
control|(
name|Number
name|n
range|:
name|values
control|)
block|{
name|output
operator|.
name|writeShort
argument_list|(
name|n
operator|.
name|shortValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addIntsField
specifier|private
name|void
name|addIntsField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|IndexOutput
name|output
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
name|field
operator|.
name|putAttribute
argument_list|(
name|legacyKey
argument_list|,
name|LegacyDocValuesType
operator|.
name|FIXED_INTS_32
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|INTS_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|// size
for|for
control|(
name|Number
name|n
range|:
name|values
control|)
block|{
name|output
operator|.
name|writeInt
argument_list|(
name|n
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addVarIntsField
specifier|private
name|void
name|addVarIntsField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|IndexOutput
name|output
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|,
name|long
name|minValue
parameter_list|,
name|long
name|maxValue
parameter_list|)
throws|throws
name|IOException
block|{
name|field
operator|.
name|putAttribute
argument_list|(
name|legacyKey
argument_list|,
name|LegacyDocValuesType
operator|.
name|VAR_INTS
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_VERSION_CURRENT
argument_list|)
expr_stmt|;
specifier|final
name|long
name|delta
init|=
name|maxValue
operator|-
name|minValue
decl_stmt|;
if|if
condition|(
name|delta
operator|<
literal|0
condition|)
block|{
comment|// writes longs
name|output
operator|.
name|writeByte
argument_list|(
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_FIXED_64
argument_list|)
expr_stmt|;
for|for
control|(
name|Number
name|n
range|:
name|values
control|)
block|{
name|output
operator|.
name|writeLong
argument_list|(
name|n
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// writes packed ints
name|output
operator|.
name|writeByte
argument_list|(
name|Lucene40DocValuesFormat
operator|.
name|VAR_INTS_PACKED
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
literal|0
operator|-
name|minValue
argument_list|)
expr_stmt|;
comment|// default value (representation of 0)
name|PackedInts
operator|.
name|Writer
name|writer
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|output
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|delta
argument_list|)
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
for|for
control|(
name|Number
name|n
range|:
name|values
control|)
block|{
name|writer
operator|.
name|add
argument_list|(
name|n
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
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
comment|// examine the values to determine best type to use
name|HashSet
argument_list|<
name|BytesRef
argument_list|>
name|uniqueValues
init|=
operator|new
name|HashSet
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
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
for|for
control|(
name|BytesRef
name|b
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
name|b
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
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|uniqueValues
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|uniqueValues
operator|.
name|add
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|b
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|uniqueValues
operator|.
name|size
argument_list|()
operator|>
literal|256
condition|)
block|{
name|uniqueValues
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
name|int
name|maxDoc
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|fixed
init|=
name|minLength
operator|==
name|maxLength
decl_stmt|;
comment|// nocommit
specifier|final
name|boolean
name|dedup
init|=
name|fixed
operator|&&
operator|(
name|uniqueValues
operator|!=
literal|null
operator|&&
name|uniqueValues
operator|.
name|size
argument_list|()
operator|*
literal|2
operator|<
name|maxDoc
operator|)
decl_stmt|;
if|if
condition|(
name|dedup
condition|)
block|{
comment|// we will deduplicate and deref values
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexOutput
name|data
init|=
literal|null
decl_stmt|;
name|IndexOutput
name|index
init|=
literal|null
decl_stmt|;
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|String
name|indexName
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"idx"
argument_list|)
decl_stmt|;
try|try
block|{
name|data
operator|=
name|dir
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
name|index
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|indexName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|fixed
condition|)
block|{
name|addFixedDerefBytesField
argument_list|(
name|field
argument_list|,
name|data
argument_list|,
name|index
argument_list|,
name|values
argument_list|,
name|minLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
literal|false
assert|;
comment|// nocommit
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
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|data
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|data
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// we dont deduplicate, just write values straight
if|if
condition|(
name|fixed
condition|)
block|{
comment|// fixed byte[]
name|String
name|fileName
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|IndexOutput
name|data
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
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
name|addFixedStraightBytesField
argument_list|(
name|field
argument_list|,
name|data
argument_list|,
name|values
argument_list|,
name|minLength
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
name|data
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// variable byte[]
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexOutput
name|data
init|=
literal|null
decl_stmt|;
name|IndexOutput
name|index
init|=
literal|null
decl_stmt|;
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|String
name|indexName
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"idx"
argument_list|)
decl_stmt|;
try|try
block|{
name|data
operator|=
name|dir
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
name|index
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|indexName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|addVarStraightBytesField
argument_list|(
name|field
argument_list|,
name|data
argument_list|,
name|index
argument_list|,
name|values
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
name|data
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|data
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|addFixedStraightBytesField
specifier|private
name|void
name|addFixedStraightBytesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|IndexOutput
name|output
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|field
operator|.
name|putAttribute
argument_list|(
name|legacyKey
argument_list|,
name|LegacyDocValuesType
operator|.
name|BYTES_FIXED_STRAIGHT
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_FIXED_STRAIGHT_CODEC_NAME
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_FIXED_STRAIGHT_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
name|output
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
block|}
block|}
comment|// NOTE: 4.0 file format docs are crazy/wrong here...
DECL|method|addVarStraightBytesField
specifier|private
name|void
name|addVarStraightBytesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|IndexOutput
name|data
parameter_list|,
name|IndexOutput
name|index
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|field
operator|.
name|putAttribute
argument_list|(
name|legacyKey
argument_list|,
name|LegacyDocValuesType
operator|.
name|BYTES_VAR_STRAIGHT
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|data
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_VAR_STRAIGHT_CODEC_NAME_DAT
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_VAR_STRAIGHT_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|index
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_VAR_STRAIGHT_CODEC_NAME_IDX
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_VAR_STRAIGHT_VERSION_CURRENT
argument_list|)
expr_stmt|;
comment|/* values */
specifier|final
name|long
name|startPos
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
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
block|}
comment|/* addresses */
specifier|final
name|long
name|maxAddress
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
operator|-
name|startPos
decl_stmt|;
name|index
operator|.
name|writeVLong
argument_list|(
name|maxAddress
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
assert|assert
name|maxDoc
operator|!=
name|Integer
operator|.
name|MAX_VALUE
assert|;
comment|// unsupported by the 4.0 impl
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
name|index
argument_list|,
name|maxDoc
operator|+
literal|1
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxAddress
argument_list|)
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|long
name|currentPosition
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
name|w
operator|.
name|add
argument_list|(
name|currentPosition
argument_list|)
expr_stmt|;
name|currentPosition
operator|+=
name|v
operator|.
name|length
expr_stmt|;
block|}
comment|// write sentinel
assert|assert
name|currentPosition
operator|==
name|maxAddress
assert|;
name|w
operator|.
name|add
argument_list|(
name|currentPosition
argument_list|)
expr_stmt|;
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
DECL|method|addFixedDerefBytesField
specifier|private
name|void
name|addFixedDerefBytesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|IndexOutput
name|data
parameter_list|,
name|IndexOutput
name|index
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|field
operator|.
name|putAttribute
argument_list|(
name|legacyKey
argument_list|,
name|LegacyDocValuesType
operator|.
name|BYTES_FIXED_DEREF
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|data
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_FIXED_DEREF_CODEC_NAME_DAT
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_FIXED_DEREF_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|index
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_FIXED_DEREF_CODEC_NAME_IDX
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_FIXED_DEREF_VERSION_CURRENT
argument_list|)
expr_stmt|;
comment|// deduplicate
name|TreeSet
argument_list|<
name|BytesRef
argument_list|>
name|dictionary
init|=
operator|new
name|TreeSet
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
name|dictionary
operator|.
name|add
argument_list|(
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* values */
name|data
operator|.
name|writeInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|dictionary
control|)
block|{
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
block|}
comment|/* ordinals */
name|int
name|valueCount
init|=
name|dictionary
operator|.
name|size
argument_list|()
decl_stmt|;
assert|assert
name|valueCount
operator|>
literal|0
assert|;
name|index
operator|.
name|writeInt
argument_list|(
name|valueCount
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
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
name|index
argument_list|,
name|maxDoc
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|valueCount
operator|-
literal|1
argument_list|)
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
name|int
name|ord
init|=
name|dictionary
operator|.
name|headSet
argument_list|(
name|v
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|w
operator|.
name|add
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
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
comment|// examine the values to determine best type to use
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
for|for
control|(
name|BytesRef
name|b
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
name|b
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
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexOutput
name|data
init|=
literal|null
decl_stmt|;
name|IndexOutput
name|index
init|=
literal|null
decl_stmt|;
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"dat"
argument_list|)
decl_stmt|;
name|String
name|indexName
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
name|Integer
operator|.
name|toString
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|,
literal|"idx"
argument_list|)
decl_stmt|;
try|try
block|{
name|data
operator|=
name|dir
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
name|index
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|indexName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|minLength
operator|==
name|maxLength
condition|)
block|{
comment|// fixed byte[]
name|addFixedSortedBytesField
argument_list|(
name|field
argument_list|,
name|data
argument_list|,
name|index
argument_list|,
name|values
argument_list|,
name|docToOrd
argument_list|,
name|minLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// var byte[]
name|addVarSortedBytesField
argument_list|(
name|field
argument_list|,
name|data
argument_list|,
name|index
argument_list|,
name|values
argument_list|,
name|docToOrd
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
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|data
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|data
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addFixedSortedBytesField
specifier|private
name|void
name|addFixedSortedBytesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|IndexOutput
name|data
parameter_list|,
name|IndexOutput
name|index
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
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|field
operator|.
name|putAttribute
argument_list|(
name|legacyKey
argument_list|,
name|LegacyDocValuesType
operator|.
name|BYTES_FIXED_SORTED
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|data
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_FIXED_SORTED_CODEC_NAME_DAT
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_FIXED_SORTED_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|index
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_FIXED_SORTED_CODEC_NAME_IDX
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_FIXED_SORTED_VERSION_CURRENT
argument_list|)
expr_stmt|;
comment|/* values */
name|data
operator|.
name|writeInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|int
name|valueCount
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
name|valueCount
operator|++
expr_stmt|;
block|}
comment|/* ordinals */
name|index
operator|.
name|writeInt
argument_list|(
name|valueCount
argument_list|)
expr_stmt|;
name|int
name|maxDoc
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
assert|assert
name|valueCount
operator|>
literal|0
assert|;
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
name|index
argument_list|,
name|maxDoc
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|valueCount
operator|-
literal|1
argument_list|)
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
for|for
control|(
name|Number
name|n
range|:
name|docToOrd
control|)
block|{
name|w
operator|.
name|add
argument_list|(
name|n
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
DECL|method|addVarSortedBytesField
specifier|private
name|void
name|addVarSortedBytesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|IndexOutput
name|data
parameter_list|,
name|IndexOutput
name|index
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
name|field
operator|.
name|putAttribute
argument_list|(
name|legacyKey
argument_list|,
name|LegacyDocValuesType
operator|.
name|BYTES_VAR_SORTED
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|data
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_VAR_SORTED_CODEC_NAME_DAT
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_VAR_SORTED_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|index
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_VAR_SORTED_CODEC_NAME_IDX
argument_list|,
name|Lucene40DocValuesFormat
operator|.
name|BYTES_VAR_SORTED_VERSION_CURRENT
argument_list|)
expr_stmt|;
comment|/* values */
specifier|final
name|long
name|startPos
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|int
name|valueCount
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
name|valueCount
operator|++
expr_stmt|;
block|}
comment|/* addresses */
specifier|final
name|long
name|maxAddress
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
operator|-
name|startPos
decl_stmt|;
name|index
operator|.
name|writeLong
argument_list|(
name|maxAddress
argument_list|)
expr_stmt|;
assert|assert
name|valueCount
operator|!=
name|Integer
operator|.
name|MAX_VALUE
assert|;
comment|// unsupported by the 4.0 impl
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
name|index
argument_list|,
name|valueCount
operator|+
literal|1
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxAddress
argument_list|)
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|long
name|currentPosition
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
name|w
operator|.
name|add
argument_list|(
name|currentPosition
argument_list|)
expr_stmt|;
name|currentPosition
operator|+=
name|v
operator|.
name|length
expr_stmt|;
block|}
comment|// write sentinel
assert|assert
name|currentPosition
operator|==
name|maxAddress
assert|;
name|w
operator|.
name|add
argument_list|(
name|currentPosition
argument_list|)
expr_stmt|;
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
comment|/* ordinals */
specifier|final
name|int
name|maxDoc
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
assert|assert
name|valueCount
operator|>
literal|0
assert|;
specifier|final
name|PackedInts
operator|.
name|Writer
name|ords
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|index
argument_list|,
name|maxDoc
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|valueCount
operator|-
literal|1
argument_list|)
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
for|for
control|(
name|Number
name|n
range|:
name|docToOrd
control|)
block|{
name|ords
operator|.
name|add
argument_list|(
name|n
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ords
operator|.
name|finish
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
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
