begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene49
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene49
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
name|Closeable
import|;
end_import
begin_comment
comment|// javadocs
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
name|Arrays
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
name|HashSet
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
name|store
operator|.
name|RAMOutputStream
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
name|MathUtil
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
name|StringHelper
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
name|DirectWriter
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
name|MonotonicBlockPackedWriter
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
comment|/** writer for {@link Lucene49DocValuesFormat} */
end_comment
begin_class
DECL|class|Lucene49DocValuesConsumer
specifier|public
class|class
name|Lucene49DocValuesConsumer
extends|extends
name|DocValuesConsumer
implements|implements
name|Closeable
block|{
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|16384
decl_stmt|;
DECL|field|ADDRESS_INTERVAL
specifier|static
specifier|final
name|int
name|ADDRESS_INTERVAL
init|=
literal|16
decl_stmt|;
comment|/** Compressed using packed blocks of ints. */
DECL|field|DELTA_COMPRESSED
specifier|public
specifier|static
specifier|final
name|int
name|DELTA_COMPRESSED
init|=
literal|0
decl_stmt|;
comment|/** Compressed by computing the GCD. */
DECL|field|GCD_COMPRESSED
specifier|public
specifier|static
specifier|final
name|int
name|GCD_COMPRESSED
init|=
literal|1
decl_stmt|;
comment|/** Compressed by giving IDs to unique values. */
DECL|field|TABLE_COMPRESSED
specifier|public
specifier|static
specifier|final
name|int
name|TABLE_COMPRESSED
init|=
literal|2
decl_stmt|;
comment|/** Compressed with monotonically increasing values */
DECL|field|MONOTONIC_COMPRESSED
specifier|public
specifier|static
specifier|final
name|int
name|MONOTONIC_COMPRESSED
init|=
literal|3
decl_stmt|;
comment|/** Uncompressed binary, written directly (fixed length). */
DECL|field|BINARY_FIXED_UNCOMPRESSED
specifier|public
specifier|static
specifier|final
name|int
name|BINARY_FIXED_UNCOMPRESSED
init|=
literal|0
decl_stmt|;
comment|/** Uncompressed binary, written directly (variable length). */
DECL|field|BINARY_VARIABLE_UNCOMPRESSED
specifier|public
specifier|static
specifier|final
name|int
name|BINARY_VARIABLE_UNCOMPRESSED
init|=
literal|1
decl_stmt|;
comment|/** Compressed binary with shared prefixes */
DECL|field|BINARY_PREFIX_COMPRESSED
specifier|public
specifier|static
specifier|final
name|int
name|BINARY_PREFIX_COMPRESSED
init|=
literal|2
decl_stmt|;
comment|/** Standard storage for sorted set values with 1 level of indirection:    *  docId -> address -> ord. */
DECL|field|SORTED_WITH_ADDRESSES
specifier|public
specifier|static
specifier|final
name|int
name|SORTED_WITH_ADDRESSES
init|=
literal|0
decl_stmt|;
comment|/** Single-valued sorted set values, encoded as sorted values, so no level    *  of indirection: docId -> ord. */
DECL|field|SORTED_SINGLE_VALUED
specifier|public
specifier|static
specifier|final
name|int
name|SORTED_SINGLE_VALUED
init|=
literal|1
decl_stmt|;
DECL|field|data
DECL|field|meta
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
comment|/** expert: Creates a new writer */
DECL|method|Lucene49DocValuesConsumer
specifier|public
name|Lucene49DocValuesConsumer
parameter_list|(
name|SegmentWriteState
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
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
name|dataCodec
argument_list|,
name|Lucene49DocValuesFormat
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
name|metaExtension
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
name|metaCodec
argument_list|,
name|Lucene49DocValuesFormat
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
argument_list|)
expr_stmt|;
block|}
block|}
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
name|addNumericField
argument_list|(
name|field
argument_list|,
name|values
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|addNumericField
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
parameter_list|,
name|boolean
name|optimizeStorage
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
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
name|long
name|gcd
init|=
literal|0
decl_stmt|;
name|boolean
name|missing
init|=
literal|false
decl_stmt|;
comment|// TODO: more efficient?
name|HashSet
argument_list|<
name|Long
argument_list|>
name|uniqueValues
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|optimizeStorage
condition|)
block|{
name|uniqueValues
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
specifier|final
name|long
name|v
decl_stmt|;
if|if
condition|(
name|nv
operator|==
literal|null
condition|)
block|{
name|v
operator|=
literal|0
expr_stmt|;
name|missing
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|v
operator|=
name|nv
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|gcd
operator|!=
literal|1
condition|)
block|{
if|if
condition|(
name|v
argument_list|<
name|Long
operator|.
name|MIN_VALUE
operator|/
literal|2
operator|||
name|v
argument_list|>
name|Long
operator|.
name|MAX_VALUE
operator|/
literal|2
condition|)
block|{
comment|// in that case v - minValue might overflow and make the GCD computation return
comment|// wrong results. Since these extreme values are unlikely, we just discard
comment|// GCD computation for them
name|gcd
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|count
operator|!=
literal|0
condition|)
block|{
comment|// minValue needs to be set first
name|gcd
operator|=
name|MathUtil
operator|.
name|gcd
argument_list|(
name|gcd
argument_list|,
name|v
operator|-
name|minValue
argument_list|)
expr_stmt|;
block|}
block|}
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
name|v
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
operator|++
name|count
expr_stmt|;
block|}
block|}
else|else
block|{
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
operator|++
name|count
expr_stmt|;
block|}
block|}
specifier|final
name|long
name|delta
init|=
name|maxValue
operator|-
name|minValue
decl_stmt|;
specifier|final
name|int
name|deltaBitsRequired
init|=
name|delta
operator|<
literal|0
condition|?
literal|64
else|:
name|DirectWriter
operator|.
name|bitsRequired
argument_list|(
name|delta
argument_list|)
decl_stmt|;
specifier|final
name|int
name|format
decl_stmt|;
if|if
condition|(
name|uniqueValues
operator|!=
literal|null
operator|&&
name|DirectWriter
operator|.
name|bitsRequired
argument_list|(
name|uniqueValues
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|<
name|deltaBitsRequired
condition|)
block|{
name|format
operator|=
name|TABLE_COMPRESSED
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|gcd
operator|!=
literal|0
operator|&&
name|gcd
operator|!=
literal|1
condition|)
block|{
specifier|final
name|long
name|gcdDelta
init|=
operator|(
name|maxValue
operator|-
name|minValue
operator|)
operator|/
name|gcd
decl_stmt|;
specifier|final
name|long
name|gcdBitsRequired
init|=
name|gcdDelta
operator|<
literal|0
condition|?
literal|64
else|:
name|DirectWriter
operator|.
name|bitsRequired
argument_list|(
name|gcdDelta
argument_list|)
decl_stmt|;
name|format
operator|=
name|gcdBitsRequired
operator|<
name|deltaBitsRequired
condition|?
name|GCD_COMPRESSED
else|:
name|DELTA_COMPRESSED
expr_stmt|;
block|}
else|else
block|{
name|format
operator|=
name|DELTA_COMPRESSED
expr_stmt|;
block|}
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeByte
argument_list|(
name|Lucene49DocValuesFormat
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|format
argument_list|)
expr_stmt|;
if|if
condition|(
name|missing
condition|)
block|{
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
name|writeMissingBitset
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|meta
operator|.
name|writeLong
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
block|}
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
name|meta
operator|.
name|writeVLong
argument_list|(
name|count
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|format
condition|)
block|{
case|case
name|GCD_COMPRESSED
case|:
name|meta
operator|.
name|writeLong
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|gcd
argument_list|)
expr_stmt|;
specifier|final
name|long
name|maxDelta
init|=
operator|(
name|maxValue
operator|-
name|minValue
operator|)
operator|/
name|gcd
decl_stmt|;
specifier|final
name|int
name|bits
init|=
name|maxDelta
operator|<
literal|0
condition|?
literal|64
else|:
name|DirectWriter
operator|.
name|bitsRequired
argument_list|(
name|maxDelta
argument_list|)
decl_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|bits
argument_list|)
expr_stmt|;
specifier|final
name|DirectWriter
name|quotientWriter
init|=
name|DirectWriter
operator|.
name|getInstance
argument_list|(
name|data
argument_list|,
name|count
argument_list|,
name|bits
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
name|long
name|value
init|=
name|nv
operator|==
literal|null
condition|?
literal|0
else|:
name|nv
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|quotientWriter
operator|.
name|add
argument_list|(
operator|(
name|value
operator|-
name|minValue
operator|)
operator|/
name|gcd
argument_list|)
expr_stmt|;
block|}
name|quotientWriter
operator|.
name|finish
argument_list|()
expr_stmt|;
break|break;
case|case
name|DELTA_COMPRESSED
case|:
specifier|final
name|long
name|minDelta
init|=
name|delta
operator|<
literal|0
condition|?
literal|0
else|:
name|minValue
decl_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|minDelta
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|deltaBitsRequired
argument_list|)
expr_stmt|;
specifier|final
name|DirectWriter
name|writer
init|=
name|DirectWriter
operator|.
name|getInstance
argument_list|(
name|data
argument_list|,
name|count
argument_list|,
name|deltaBitsRequired
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
name|long
name|v
init|=
name|nv
operator|==
literal|null
condition|?
literal|0
else|:
name|nv
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|writer
operator|.
name|add
argument_list|(
name|v
operator|-
name|minDelta
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
break|break;
case|case
name|TABLE_COMPRESSED
case|:
specifier|final
name|Long
index|[]
name|decode
init|=
name|uniqueValues
operator|.
name|toArray
argument_list|(
operator|new
name|Long
index|[
name|uniqueValues
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|decode
argument_list|)
expr_stmt|;
specifier|final
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
name|encode
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|decode
operator|.
name|length
argument_list|)
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
name|decode
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|meta
operator|.
name|writeLong
argument_list|(
name|decode
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|encode
operator|.
name|put
argument_list|(
name|decode
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|bitsRequired
init|=
name|DirectWriter
operator|.
name|bitsRequired
argument_list|(
name|uniqueValues
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|bitsRequired
argument_list|)
expr_stmt|;
specifier|final
name|DirectWriter
name|ordsWriter
init|=
name|DirectWriter
operator|.
name|getInstance
argument_list|(
name|data
argument_list|,
name|count
argument_list|,
name|bitsRequired
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
name|ordsWriter
operator|.
name|add
argument_list|(
name|encode
operator|.
name|get
argument_list|(
name|nv
operator|==
literal|null
condition|?
literal|0
else|:
name|nv
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ordsWriter
operator|.
name|finish
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
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
block|}
comment|// TODO: in some cases representing missing with minValue-1 wouldn't take up additional space and so on,
comment|// but this is very simple, and algorithms only check this for values of 0 anyway (doesnt slow down normal decode)
DECL|method|writeMissingBitset
name|void
name|writeMissingBitset
parameter_list|(
name|Iterable
argument_list|<
name|?
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|bits
init|=
literal|0
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Object
name|v
range|:
name|values
control|)
block|{
if|if
condition|(
name|count
operator|==
literal|8
condition|)
block|{
name|data
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|bits
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|bits
operator||=
literal|1
operator|<<
operator|(
name|count
operator|&
literal|7
operator|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|data
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
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
name|meta
operator|.
name|writeByte
argument_list|(
name|Lucene49DocValuesFormat
operator|.
name|BINARY
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
name|long
name|count
init|=
literal|0
decl_stmt|;
name|boolean
name|missing
init|=
literal|false
decl_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
specifier|final
name|int
name|length
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|length
operator|=
literal|0
expr_stmt|;
name|missing
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|length
operator|=
name|v
operator|.
name|length
expr_stmt|;
block|}
name|minLength
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minLength
argument_list|,
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
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
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
name|count
operator|++
expr_stmt|;
block|}
name|meta
operator|.
name|writeVInt
argument_list|(
name|minLength
operator|==
name|maxLength
condition|?
name|BINARY_FIXED_UNCOMPRESSED
else|:
name|BINARY_VARIABLE_UNCOMPRESSED
argument_list|)
expr_stmt|;
if|if
condition|(
name|missing
condition|)
block|{
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
name|writeMissingBitset
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|meta
operator|.
name|writeLong
argument_list|(
operator|-
literal|1L
argument_list|)
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
name|writeVLong
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
if|if
condition|(
name|minLength
operator|!=
name|maxLength
condition|)
block|{
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
name|meta
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
specifier|final
name|MonotonicBlockPackedWriter
name|writer
init|=
operator|new
name|MonotonicBlockPackedWriter
argument_list|(
name|data
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|long
name|addr
init|=
literal|0
decl_stmt|;
name|writer
operator|.
name|add
argument_list|(
name|addr
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
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|addr
operator|+=
name|v
operator|.
name|length
expr_stmt|;
block|}
name|writer
operator|.
name|add
argument_list|(
name|addr
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
comment|/** expert: writes a value dictionary for a sorted/sortedset field */
DECL|method|addTermsDict
specifier|protected
name|void
name|addTermsDict
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
comment|// first check if its a "fixed-length" terms dict
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
block|}
if|if
condition|(
name|minLength
operator|==
name|maxLength
condition|)
block|{
comment|// no index needed: direct addressing by mult
name|addBinaryField
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// header
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeByte
argument_list|(
name|Lucene49DocValuesFormat
operator|.
name|BINARY
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|BINARY_PREFIX_COMPRESSED
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
comment|// now write the bytes: sharing prefixes within a block
specifier|final
name|long
name|startFP
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
comment|// currently, we have to store the delta from expected for every 1/nth term
comment|// we could avoid this, but its not much and less overall RAM than the previous approach!
name|RAMOutputStream
name|addressBuffer
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
name|MonotonicBlockPackedWriter
name|termAddresses
init|=
operator|new
name|MonotonicBlockPackedWriter
argument_list|(
name|addressBuffer
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|BytesRef
name|lastTerm
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|long
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
if|if
condition|(
name|count
operator|%
name|ADDRESS_INTERVAL
operator|==
literal|0
condition|)
block|{
name|termAddresses
operator|.
name|add
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
operator|-
name|startFP
argument_list|)
expr_stmt|;
comment|// force the first term in a block to be abs-encoded
name|lastTerm
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
comment|// prefix-code
name|int
name|sharedPrefix
init|=
name|StringHelper
operator|.
name|bytesDifference
argument_list|(
name|lastTerm
argument_list|,
name|v
argument_list|)
decl_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|sharedPrefix
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|v
operator|.
name|length
operator|-
name|sharedPrefix
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
operator|+
name|sharedPrefix
argument_list|,
name|v
operator|.
name|length
operator|-
name|sharedPrefix
argument_list|)
expr_stmt|;
name|lastTerm
operator|.
name|copyBytes
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
specifier|final
name|long
name|indexStartFP
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
comment|// write addresses of indexed terms
name|termAddresses
operator|.
name|finish
argument_list|()
expr_stmt|;
name|addressBuffer
operator|.
name|writeTo
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|addressBuffer
operator|=
literal|null
expr_stmt|;
name|termAddresses
operator|=
literal|null
expr_stmt|;
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
name|writeVLong
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
name|meta
operator|.
name|writeVInt
argument_list|(
name|ADDRESS_INTERVAL
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|indexStartFP
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|BLOCK_SIZE
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
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeByte
argument_list|(
name|Lucene49DocValuesFormat
operator|.
name|SORTED
argument_list|)
expr_stmt|;
name|addTermsDict
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
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addSortedNumericField
specifier|public
name|void
name|addSortedNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToValueCount
parameter_list|,
specifier|final
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
name|meta
operator|.
name|writeByte
argument_list|(
name|Lucene49DocValuesFormat
operator|.
name|SORTED_NUMERIC
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSingleValued
argument_list|(
name|docToValueCount
argument_list|)
condition|)
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|SORTED_SINGLE_VALUED
argument_list|)
expr_stmt|;
comment|// The field is single-valued, we can encode it as NUMERIC
name|addNumericField
argument_list|(
name|field
argument_list|,
name|singletonView
argument_list|(
name|docToValueCount
argument_list|,
name|values
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|SORTED_WITH_ADDRESSES
argument_list|)
expr_stmt|;
comment|// write the stream of values as a numeric field
name|addNumericField
argument_list|(
name|field
argument_list|,
name|values
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// write the doc -> ord count as a absolute index to the stream
name|addAddresses
argument_list|(
name|field
argument_list|,
name|docToValueCount
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addSortedSetField
specifier|public
name|void
name|addSortedSetField
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
specifier|final
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrdCount
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|Number
argument_list|>
name|ords
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
name|meta
operator|.
name|writeByte
argument_list|(
name|Lucene49DocValuesFormat
operator|.
name|SORTED_SET
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSingleValued
argument_list|(
name|docToOrdCount
argument_list|)
condition|)
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|SORTED_SINGLE_VALUED
argument_list|)
expr_stmt|;
comment|// The field is single-valued, we can encode it as SORTED
name|addSortedField
argument_list|(
name|field
argument_list|,
name|values
argument_list|,
name|singletonView
argument_list|(
name|docToOrdCount
argument_list|,
name|ords
argument_list|,
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|SORTED_WITH_ADDRESSES
argument_list|)
expr_stmt|;
comment|// write the ord -> byte[] as a binary field
name|addTermsDict
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
comment|// write the stream of ords as a numeric field
comment|// NOTE: we could return an iterator that delta-encodes these within a doc
name|addNumericField
argument_list|(
name|field
argument_list|,
name|ords
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// write the doc -> ord count as a absolute index to the stream
name|addAddresses
argument_list|(
name|field
argument_list|,
name|docToOrdCount
argument_list|)
expr_stmt|;
block|}
block|}
comment|// writes addressing information as MONOTONIC_COMPRESSED integer
DECL|method|addAddresses
specifier|private
name|void
name|addAddresses
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
name|meta
operator|.
name|writeByte
argument_list|(
name|Lucene49DocValuesFormat
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|MONOTONIC_COMPRESSED
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
operator|-
literal|1L
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
name|meta
operator|.
name|writeVLong
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
specifier|final
name|MonotonicBlockPackedWriter
name|writer
init|=
operator|new
name|MonotonicBlockPackedWriter
argument_list|(
name|data
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
name|long
name|addr
init|=
literal|0
decl_stmt|;
name|writer
operator|.
name|add
argument_list|(
name|addr
argument_list|)
expr_stmt|;
for|for
control|(
name|Number
name|v
range|:
name|values
control|)
block|{
name|addr
operator|+=
name|v
operator|.
name|longValue
argument_list|()
expr_stmt|;
name|writer
operator|.
name|add
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|meta
operator|!=
literal|null
condition|)
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// write EOF marker
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|meta
argument_list|)
expr_stmt|;
comment|// write checksum
block|}
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// write checksum
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
name|meta
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
name|meta
argument_list|)
expr_stmt|;
block|}
name|meta
operator|=
name|data
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
