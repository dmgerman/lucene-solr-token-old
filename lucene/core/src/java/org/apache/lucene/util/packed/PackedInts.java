begin_unit
begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
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
name|Closeable
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
name|DataInput
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
name|DataOutput
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
name|CodecUtil
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
begin_comment
comment|/**  * Simplistic compression for array of unsigned long values.  * Each value is>= 0 and<= a specified maximum value.  The  * values are stored as packed ints, with each value  * consuming a fixed number of bits.  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|PackedInts
specifier|public
class|class
name|PackedInts
block|{
comment|/**    * At most 700% memory overhead, always select a direct implementation.    */
DECL|field|FASTEST
specifier|public
specifier|static
specifier|final
name|float
name|FASTEST
init|=
literal|7f
decl_stmt|;
comment|/**    * At most 50% memory overhead, always select a reasonably fast implementation.    */
DECL|field|FAST
specifier|public
specifier|static
specifier|final
name|float
name|FAST
init|=
literal|0.5f
decl_stmt|;
comment|/**    * At most 20% memory overhead.    */
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT
init|=
literal|0.2f
decl_stmt|;
comment|/**    * No memory overhead at all, but the returned implementation may be slow.    */
DECL|field|COMPACT
specifier|public
specifier|static
specifier|final
name|float
name|COMPACT
init|=
literal|0f
decl_stmt|;
comment|/**    * Default amount of memory to use for bulk operations.    */
DECL|field|DEFAULT_BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|1024
decl_stmt|;
comment|// 1K
DECL|field|CODEC_NAME
specifier|private
specifier|final
specifier|static
name|String
name|CODEC_NAME
init|=
literal|"PackedInts"
decl_stmt|;
DECL|field|VERSION_START
specifier|private
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|private
specifier|final
specifier|static
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|PACKED
specifier|static
specifier|final
name|int
name|PACKED
init|=
literal|0
decl_stmt|;
DECL|field|PACKED_SINGLE_BLOCK
specifier|static
specifier|final
name|int
name|PACKED_SINGLE_BLOCK
init|=
literal|1
decl_stmt|;
comment|/**    * A read-only random access array of positive integers.    * @lucene.internal    */
DECL|interface|Reader
specifier|public
specifier|static
interface|interface
name|Reader
block|{
comment|/**      * @param index the position of the wanted value.      * @return the value at the stated index.      */
DECL|method|get
name|long
name|get
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**      * Bulk get: read at least one and at most<code>len</code> longs starting      * from<code>index</code> into<code>arr[off:off+len]</code> and return      * the actual number of values that have been read.      */
DECL|method|get
name|int
name|get
parameter_list|(
name|int
name|index
parameter_list|,
name|long
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
function_decl|;
comment|/**      * @return the number of bits used to store any given value.      *         Note: This does not imply that memory usage is      *         {@code bitsPerValue * #values} as implementations are free to      *         use non-space-optimal packing of bits.      */
DECL|method|getBitsPerValue
name|int
name|getBitsPerValue
parameter_list|()
function_decl|;
comment|/**      * @return the number of values.      */
DECL|method|size
name|int
name|size
parameter_list|()
function_decl|;
comment|/**      * Expert: if the bit-width of this reader matches one of      * java's native types, returns the underlying array      * (ie, byte[], short[], int[], long[]); else, returns      * null.  Note that when accessing the array you must      * upgrade the type (bitwise AND with all ones), to      * interpret the full value as unsigned.  Ie,      * bytes[idx]&0xFF, shorts[idx]&0xFFFF, etc.      */
DECL|method|getArray
name|Object
name|getArray
parameter_list|()
function_decl|;
comment|/**      * Returns true if this implementation is backed by a      * native java array.      *      * @see #getArray      */
DECL|method|hasArray
name|boolean
name|hasArray
parameter_list|()
function_decl|;
block|}
comment|/**    * Run-once iterator interface, to decode previously saved PackedInts.    */
DECL|interface|ReaderIterator
specifier|public
specifier|static
interface|interface
name|ReaderIterator
extends|extends
name|Closeable
block|{
comment|/** Returns next value */
DECL|method|next
name|long
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns number of bits per value */
DECL|method|getBitsPerValue
name|int
name|getBitsPerValue
parameter_list|()
function_decl|;
comment|/** Returns number of values */
DECL|method|size
name|int
name|size
parameter_list|()
function_decl|;
comment|/** Returns the current position */
DECL|method|ord
name|int
name|ord
parameter_list|()
function_decl|;
comment|/** Skips to the given ordinal and returns its value.      * @return the value at the given position      * @throws IOException if reading the value throws an IOException*/
DECL|method|advance
name|long
name|advance
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|class|ReaderIteratorImpl
specifier|static
specifier|abstract
class|class
name|ReaderIteratorImpl
implements|implements
name|ReaderIterator
block|{
DECL|field|in
specifier|protected
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|bitsPerValue
specifier|protected
specifier|final
name|int
name|bitsPerValue
decl_stmt|;
DECL|field|valueCount
specifier|protected
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|method|ReaderIteratorImpl
specifier|protected
name|ReaderIteratorImpl
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|,
name|IndexInput
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|bitsPerValue
operator|=
name|bitsPerValue
expr_stmt|;
name|this
operator|.
name|valueCount
operator|=
name|valueCount
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBitsPerValue
specifier|public
name|int
name|getBitsPerValue
parameter_list|()
block|{
return|return
name|bitsPerValue
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|valueCount
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * A packed integer array that can be modified.    * @lucene.internal    */
DECL|interface|Mutable
specifier|public
specifier|static
interface|interface
name|Mutable
extends|extends
name|Reader
block|{
comment|/**      * Set the value at the given index in the array.      * @param index where the value should be positioned.      * @param value a value conforming to the constraints set by the array.      */
DECL|method|set
name|void
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|long
name|value
parameter_list|)
function_decl|;
comment|/**      * Bulk set: set at least one and at most<code>len</code> longs starting      * at<code>off</code> in<code>arr</code> into this mutable, starting at      *<code>index</code>. Returns the actual number of values that have been      * set.      */
DECL|method|set
name|int
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|long
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
function_decl|;
comment|/**      * Fill the mutable from<code>fromIndex</code> (inclusive) to      *<code>toIndex</code> (exclusive) with<code>val</code>.      */
DECL|method|fill
name|void
name|fill
parameter_list|(
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|,
name|long
name|val
parameter_list|)
function_decl|;
comment|/**      * Sets all values to 0.      */
DECL|method|clear
name|void
name|clear
parameter_list|()
function_decl|;
block|}
comment|/**    * A simple base for Readers that keeps track of valueCount and bitsPerValue.    * @lucene.internal    */
DECL|class|ReaderImpl
specifier|public
specifier|static
specifier|abstract
class|class
name|ReaderImpl
implements|implements
name|Reader
block|{
DECL|field|bitsPerValue
specifier|protected
specifier|final
name|int
name|bitsPerValue
decl_stmt|;
DECL|field|valueCount
specifier|protected
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|method|ReaderImpl
specifier|protected
name|ReaderImpl
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
name|this
operator|.
name|bitsPerValue
operator|=
name|bitsPerValue
expr_stmt|;
assert|assert
name|bitsPerValue
operator|>
literal|0
operator|&&
name|bitsPerValue
operator|<=
literal|64
operator|:
literal|"bitsPerValue="
operator|+
name|bitsPerValue
assert|;
name|this
operator|.
name|valueCount
operator|=
name|valueCount
expr_stmt|;
block|}
DECL|method|getBitsPerValue
specifier|public
name|int
name|getBitsPerValue
parameter_list|()
block|{
return|return
name|bitsPerValue
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|int
name|index
parameter_list|,
name|long
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|valueCount
assert|;
assert|assert
name|off
operator|+
name|len
operator|<=
name|arr
operator|.
name|length
assert|;
specifier|final
name|int
name|gets
init|=
name|Math
operator|.
name|min
argument_list|(
name|valueCount
operator|-
name|index
argument_list|,
name|len
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|index
init|,
name|o
init|=
name|off
init|,
name|end
init|=
name|index
operator|+
name|gets
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
operator|,
operator|++
name|o
control|)
block|{
name|arr
index|[
name|o
index|]
operator|=
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|gets
return|;
block|}
block|}
DECL|class|MutableImpl
specifier|public
specifier|static
specifier|abstract
class|class
name|MutableImpl
extends|extends
name|ReaderImpl
implements|implements
name|Mutable
block|{
DECL|method|MutableImpl
specifier|protected
name|MutableImpl
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
block|}
DECL|method|set
specifier|public
name|int
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|long
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
assert|assert
name|len
operator|>
literal|0
assert|;
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|valueCount
assert|;
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|valueCount
operator|-
name|index
argument_list|)
expr_stmt|;
assert|assert
name|off
operator|+
name|len
operator|<=
name|arr
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
name|index
init|,
name|o
init|=
name|off
init|,
name|end
init|=
name|index
operator|+
name|len
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
operator|,
operator|++
name|o
control|)
block|{
name|set
argument_list|(
name|i
argument_list|,
name|arr
index|[
name|o
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
DECL|method|fill
specifier|public
name|void
name|fill
parameter_list|(
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|,
name|long
name|val
parameter_list|)
block|{
assert|assert
name|val
operator|<=
name|maxValue
argument_list|(
name|bitsPerValue
argument_list|)
assert|;
assert|assert
name|fromIndex
operator|<=
name|toIndex
assert|;
for|for
control|(
name|int
name|i
init|=
name|fromIndex
init|;
name|i
operator|<
name|toIndex
condition|;
operator|++
name|i
control|)
block|{
name|set
argument_list|(
name|i
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** A write-once Writer.    * @lucene.internal    */
DECL|class|Writer
specifier|public
specifier|static
specifier|abstract
class|class
name|Writer
block|{
DECL|field|out
specifier|protected
specifier|final
name|DataOutput
name|out
decl_stmt|;
DECL|field|bitsPerValue
specifier|protected
specifier|final
name|int
name|bitsPerValue
decl_stmt|;
DECL|field|valueCount
specifier|protected
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|method|Writer
specifier|protected
name|Writer
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|bitsPerValue
operator|<=
literal|64
assert|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|valueCount
operator|=
name|valueCount
expr_stmt|;
name|this
operator|.
name|bitsPerValue
operator|=
name|bitsPerValue
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|bitsPerValue
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|valueCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|getFormat
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getFormat
specifier|protected
specifier|abstract
name|int
name|getFormat
parameter_list|()
function_decl|;
DECL|method|add
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|long
name|v
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Retrieve PackedInt data from the DataInput and return a packed int    * structure based on it.    *    * @param in positioned at the beginning of a stored packed int structure.    * @return a read only random access capable array of positive integers.    * @throws IOException if the structure could not be retrieved.    * @lucene.internal    */
DECL|method|getReader
specifier|public
specifier|static
name|Reader
name|getReader
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_START
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bitsPerValue
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
assert|assert
name|bitsPerValue
operator|>
literal|0
operator|&&
name|bitsPerValue
operator|<=
literal|64
operator|:
literal|"bitsPerValue="
operator|+
name|bitsPerValue
assert|;
specifier|final
name|int
name|valueCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|format
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|format
condition|)
block|{
case|case
name|PACKED
case|:
switch|switch
condition|(
name|bitsPerValue
condition|)
block|{
case|case
literal|8
case|:
return|return
operator|new
name|Direct8
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|)
return|;
case|case
literal|16
case|:
return|return
operator|new
name|Direct16
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|)
return|;
case|case
literal|24
case|:
return|return
operator|new
name|Packed8ThreeBlocks
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|)
return|;
case|case
literal|32
case|:
return|return
operator|new
name|Direct32
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|)
return|;
case|case
literal|48
case|:
return|return
operator|new
name|Packed16ThreeBlocks
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|)
return|;
case|case
literal|64
case|:
return|return
operator|new
name|Direct64
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|)
return|;
default|default:
return|return
operator|new
name|Packed64
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
return|;
block|}
case|case
name|PACKED_SINGLE_BLOCK
case|:
return|return
name|Packed64SingleBlock
operator|.
name|create
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unknwown Writer format: "
operator|+
name|format
argument_list|)
throw|;
block|}
block|}
comment|/**    * Retrieve PackedInts as a {@link ReaderIterator}    * @param in positioned at the beginning of a stored packed int structure.    * @return an iterator to access the values    * @throws IOException if the structure could not be retrieved.    * @lucene.internal    */
DECL|method|getReaderIterator
specifier|public
specifier|static
name|ReaderIterator
name|getReaderIterator
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_START
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bitsPerValue
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
assert|assert
name|bitsPerValue
operator|>
literal|0
operator|&&
name|bitsPerValue
operator|<=
literal|64
operator|:
literal|"bitsPerValue="
operator|+
name|bitsPerValue
assert|;
specifier|final
name|int
name|valueCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|format
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|format
condition|)
block|{
case|case
name|PACKED
case|:
return|return
operator|new
name|PackedReaderIterator
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|,
name|in
argument_list|)
return|;
case|case
name|PACKED_SINGLE_BLOCK
case|:
return|return
operator|new
name|Packed64SingleBlockReaderIterator
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|,
name|in
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unknwown Writer format: "
operator|+
name|format
argument_list|)
throw|;
block|}
block|}
comment|/**    * Retrieve PackedInts.Reader that does not load values    * into RAM but rather accesses all values via the    * provided IndexInput.    * @param in positioned at the beginning of a stored packed int structure.    * @return an Reader to access the values    * @throws IOException if the structure could not be retrieved.    * @lucene.internal    */
DECL|method|getDirectReader
specifier|public
specifier|static
name|Reader
name|getDirectReader
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_START
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bitsPerValue
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
assert|assert
name|bitsPerValue
operator|>
literal|0
operator|&&
name|bitsPerValue
operator|<=
literal|64
operator|:
literal|"bitsPerValue="
operator|+
name|bitsPerValue
assert|;
specifier|final
name|int
name|valueCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|format
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|format
condition|)
block|{
case|case
name|PACKED
case|:
return|return
operator|new
name|DirectPackedReader
argument_list|(
name|bitsPerValue
argument_list|,
name|valueCount
argument_list|,
name|in
argument_list|)
return|;
case|case
name|PACKED_SINGLE_BLOCK
case|:
return|return
operator|new
name|DirectPacked64SingleBlockReader
argument_list|(
name|bitsPerValue
argument_list|,
name|valueCount
argument_list|,
name|in
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unknwown Writer format: "
operator|+
name|format
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create a packed integer array with the given amount of values initialized    * to 0. the valueCount and the bitsPerValue cannot be changed after creation.    * All Mutables known by this factory are kept fully in RAM.    *     * Positive values of<code>acceptableOverheadRatio</code> will trade space    * for speed by selecting a faster but potentially less memory-efficient    * implementation. An<code>acceptableOverheadRatio</code> of    * {@link PackedInts#COMPACT} will make sure that the most memory-efficient    * implementation is selected whereas {@link PackedInts#FASTEST} will make sure    * that the fastest implementation is selected.    *    * @param valueCount   the number of elements    * @param bitsPerValue the number of bits available for any given value    * @param acceptableOverheadRatio an acceptable overhead    *        ratio per value    * @return a mutable packed integer array    * @throws java.io.IOException if the Mutable could not be created. With the    *         current implementations, this never happens, but the method    *         signature allows for future persistence-backed Mutables.    * @lucene.internal    */
DECL|method|getMutable
specifier|public
specifier|static
name|Mutable
name|getMutable
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
block|{
name|acceptableOverheadRatio
operator|=
name|Math
operator|.
name|max
argument_list|(
name|COMPACT
argument_list|,
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
name|acceptableOverheadRatio
operator|=
name|Math
operator|.
name|min
argument_list|(
name|FASTEST
argument_list|,
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
name|float
name|acceptableOverheadPerValue
init|=
name|acceptableOverheadRatio
operator|*
name|bitsPerValue
decl_stmt|;
comment|// in bits
name|int
name|maxBitsPerValue
init|=
name|bitsPerValue
operator|+
operator|(
name|int
operator|)
name|acceptableOverheadPerValue
decl_stmt|;
if|if
condition|(
name|bitsPerValue
operator|<=
literal|8
operator|&&
name|maxBitsPerValue
operator|>=
literal|8
condition|)
block|{
return|return
operator|new
name|Direct8
argument_list|(
name|valueCount
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|bitsPerValue
operator|<=
literal|16
operator|&&
name|maxBitsPerValue
operator|>=
literal|16
condition|)
block|{
return|return
operator|new
name|Direct16
argument_list|(
name|valueCount
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|bitsPerValue
operator|<=
literal|32
operator|&&
name|maxBitsPerValue
operator|>=
literal|32
condition|)
block|{
return|return
operator|new
name|Direct32
argument_list|(
name|valueCount
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|bitsPerValue
operator|<=
literal|64
operator|&&
name|maxBitsPerValue
operator|>=
literal|64
condition|)
block|{
return|return
operator|new
name|Direct64
argument_list|(
name|valueCount
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|valueCount
operator|<=
name|Packed8ThreeBlocks
operator|.
name|MAX_SIZE
operator|&&
name|bitsPerValue
operator|<=
literal|24
operator|&&
name|maxBitsPerValue
operator|>=
literal|24
condition|)
block|{
return|return
operator|new
name|Packed8ThreeBlocks
argument_list|(
name|valueCount
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|valueCount
operator|<=
name|Packed16ThreeBlocks
operator|.
name|MAX_SIZE
operator|&&
name|bitsPerValue
operator|<=
literal|48
operator|&&
name|maxBitsPerValue
operator|>=
literal|48
condition|)
block|{
return|return
operator|new
name|Packed16ThreeBlocks
argument_list|(
name|valueCount
argument_list|)
return|;
block|}
else|else
block|{
for|for
control|(
name|int
name|bpv
init|=
name|bitsPerValue
init|;
name|bpv
operator|<=
name|maxBitsPerValue
condition|;
operator|++
name|bpv
control|)
block|{
if|if
condition|(
name|Packed64SingleBlock
operator|.
name|isSupported
argument_list|(
name|bpv
argument_list|)
condition|)
block|{
name|float
name|overhead
init|=
name|Packed64SingleBlock
operator|.
name|overheadPerValue
argument_list|(
name|bpv
argument_list|)
decl_stmt|;
name|float
name|acceptableOverhead
init|=
name|acceptableOverheadPerValue
operator|+
name|bitsPerValue
operator|-
name|bpv
decl_stmt|;
if|if
condition|(
name|overhead
operator|<=
name|acceptableOverhead
condition|)
block|{
return|return
name|Packed64SingleBlock
operator|.
name|create
argument_list|(
name|valueCount
argument_list|,
name|bpv
argument_list|)
return|;
block|}
block|}
block|}
return|return
operator|new
name|Packed64
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
return|;
block|}
block|}
comment|/**    * Create a packed integer array writer for the given number of values at the    * given bits/value. Writers append to the given IndexOutput and has very    * low memory overhead.    *    * Positive values of<code>acceptableOverheadRatio</code> will trade space    * for speed by selecting a faster but potentially less memory-efficient    * implementation. An<code>acceptableOverheadRatio</code> of    * {@link PackedInts#COMPACT} will make sure that the most memory-efficient    * implementation is selected whereas {@link PackedInts#FASTEST} will make sure    * that the fastest implementation is selected.    *    * @param out          the destination for the produced bits.    * @param valueCount   the number of elements.    * @param bitsPerValue the number of bits available for any given value.    * @param acceptableOverheadRatio an acceptable overhead ratio per value    * @return a Writer ready for receiving values.    * @throws IOException if bits could not be written to out.    * @lucene.internal    */
DECL|method|getWriter
specifier|public
specifier|static
name|Writer
name|getWriter
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
throws|throws
name|IOException
block|{
name|acceptableOverheadRatio
operator|=
name|Math
operator|.
name|max
argument_list|(
name|COMPACT
argument_list|,
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
name|acceptableOverheadRatio
operator|=
name|Math
operator|.
name|min
argument_list|(
name|FASTEST
argument_list|,
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
name|float
name|acceptableOverheadPerValue
init|=
name|acceptableOverheadRatio
operator|*
name|bitsPerValue
decl_stmt|;
comment|// in bits
name|int
name|maxBitsPerValue
init|=
name|bitsPerValue
operator|+
operator|(
name|int
operator|)
name|acceptableOverheadPerValue
decl_stmt|;
if|if
condition|(
name|bitsPerValue
operator|<=
literal|8
operator|&&
name|maxBitsPerValue
operator|>=
literal|8
condition|)
block|{
return|return
operator|new
name|PackedWriter
argument_list|(
name|out
argument_list|,
name|valueCount
argument_list|,
literal|8
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|bitsPerValue
operator|<=
literal|16
operator|&&
name|maxBitsPerValue
operator|>=
literal|16
condition|)
block|{
return|return
operator|new
name|PackedWriter
argument_list|(
name|out
argument_list|,
name|valueCount
argument_list|,
literal|16
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|bitsPerValue
operator|<=
literal|32
operator|&&
name|maxBitsPerValue
operator|>=
literal|32
condition|)
block|{
return|return
operator|new
name|PackedWriter
argument_list|(
name|out
argument_list|,
name|valueCount
argument_list|,
literal|32
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|bitsPerValue
operator|<=
literal|64
operator|&&
name|maxBitsPerValue
operator|>=
literal|64
condition|)
block|{
return|return
operator|new
name|PackedWriter
argument_list|(
name|out
argument_list|,
name|valueCount
argument_list|,
literal|64
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|valueCount
operator|<=
name|Packed8ThreeBlocks
operator|.
name|MAX_SIZE
operator|&&
name|bitsPerValue
operator|<=
literal|24
operator|&&
name|maxBitsPerValue
operator|>=
literal|24
condition|)
block|{
return|return
operator|new
name|PackedWriter
argument_list|(
name|out
argument_list|,
name|valueCount
argument_list|,
literal|24
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|valueCount
operator|<=
name|Packed16ThreeBlocks
operator|.
name|MAX_SIZE
operator|&&
name|bitsPerValue
operator|<=
literal|48
operator|&&
name|maxBitsPerValue
operator|>=
literal|48
condition|)
block|{
return|return
operator|new
name|PackedWriter
argument_list|(
name|out
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
return|;
block|}
else|else
block|{
for|for
control|(
name|int
name|bpv
init|=
name|bitsPerValue
init|;
name|bpv
operator|<=
name|maxBitsPerValue
condition|;
operator|++
name|bpv
control|)
block|{
if|if
condition|(
name|Packed64SingleBlock
operator|.
name|isSupported
argument_list|(
name|bpv
argument_list|)
condition|)
block|{
name|float
name|overhead
init|=
name|Packed64SingleBlock
operator|.
name|overheadPerValue
argument_list|(
name|bpv
argument_list|)
decl_stmt|;
name|float
name|acceptableOverhead
init|=
name|acceptableOverheadPerValue
operator|+
name|bitsPerValue
operator|-
name|bpv
decl_stmt|;
if|if
condition|(
name|overhead
operator|<=
name|acceptableOverhead
condition|)
block|{
return|return
operator|new
name|Packed64SingleBlockWriter
argument_list|(
name|out
argument_list|,
name|valueCount
argument_list|,
name|bpv
argument_list|)
return|;
block|}
block|}
block|}
return|return
operator|new
name|PackedWriter
argument_list|(
name|out
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
return|;
block|}
block|}
comment|/** Returns how many bits are required to hold values up    *  to and including maxValue    * @param maxValue the maximum value that should be representable.    * @return the amount of bits needed to represent values from 0 to maxValue.    * @lucene.internal    */
DECL|method|bitsRequired
specifier|public
specifier|static
name|int
name|bitsRequired
parameter_list|(
name|long
name|maxValue
parameter_list|)
block|{
if|if
condition|(
name|maxValue
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxValue must be non-negative (got: "
operator|+
name|maxValue
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
literal|64
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|maxValue
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Calculates the maximum unsigned long that can be expressed with the given    * number of bits.    * @param bitsPerValue the number of bits available for any given value.    * @return the maximum value for the given bits.    * @lucene.internal    */
DECL|method|maxValue
specifier|public
specifier|static
name|long
name|maxValue
parameter_list|(
name|int
name|bitsPerValue
parameter_list|)
block|{
return|return
name|bitsPerValue
operator|==
literal|64
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
operator|~
operator|(
operator|~
literal|0L
operator|<<
name|bitsPerValue
operator|)
return|;
block|}
comment|/**    * Copy<code>src[srcPos:srcPos+len]</code> into    *<code>dest[destPos:destPos+len]</code> using at most<code>mem</code>    * bytes.    */
DECL|method|copy
specifier|public
specifier|static
name|void
name|copy
parameter_list|(
name|Reader
name|src
parameter_list|,
name|int
name|srcPos
parameter_list|,
name|Mutable
name|dest
parameter_list|,
name|int
name|destPos
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|mem
parameter_list|)
block|{
assert|assert
name|srcPos
operator|+
name|len
operator|<=
name|src
operator|.
name|size
argument_list|()
assert|;
assert|assert
name|destPos
operator|+
name|len
operator|<=
name|dest
operator|.
name|size
argument_list|()
assert|;
specifier|final
name|int
name|capacity
init|=
name|mem
operator|>>>
literal|3
decl_stmt|;
if|if
condition|(
name|capacity
operator|==
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
operator|++
name|i
control|)
block|{
name|dest
operator|.
name|set
argument_list|(
name|destPos
operator|++
argument_list|,
name|src
operator|.
name|get
argument_list|(
name|srcPos
operator|++
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// use bulk operations
name|long
index|[]
name|buf
init|=
operator|new
name|long
index|[
name|Math
operator|.
name|min
argument_list|(
name|capacity
argument_list|,
name|len
argument_list|)
index|]
decl_stmt|;
name|int
name|remaining
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|read
init|=
name|src
operator|.
name|get
argument_list|(
name|srcPos
argument_list|,
name|buf
argument_list|,
name|remaining
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|buf
operator|.
name|length
operator|-
name|remaining
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|read
operator|>
literal|0
assert|;
name|srcPos
operator|+=
name|read
expr_stmt|;
name|len
operator|-=
name|read
expr_stmt|;
name|remaining
operator|+=
name|read
expr_stmt|;
specifier|final
name|int
name|written
init|=
name|dest
operator|.
name|set
argument_list|(
name|destPos
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|remaining
argument_list|)
decl_stmt|;
assert|assert
name|written
operator|>
literal|0
assert|;
name|destPos
operator|+=
name|written
expr_stmt|;
if|if
condition|(
name|written
operator|<
name|remaining
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
name|written
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|remaining
operator|-
name|written
argument_list|)
expr_stmt|;
block|}
name|remaining
operator|-=
name|written
expr_stmt|;
block|}
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|written
init|=
name|dest
operator|.
name|set
argument_list|(
name|destPos
argument_list|,
name|buf
argument_list|,
literal|0
argument_list|,
name|remaining
argument_list|)
decl_stmt|;
name|remaining
operator|-=
name|written
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
