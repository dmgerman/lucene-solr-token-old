begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|util
operator|.
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_MASK
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
name|util
operator|.
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SHIFT
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
name|Comparator
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
name|util
operator|.
name|ByteBlockPool
operator|.
name|DirectAllocator
import|;
end_import
begin_comment
comment|/**  * {@link BytesRefHash} is a special purpose hash-map like data-structure  * optimized for {@link BytesRef} instances. BytesRefHash maintains mappings of  * byte arrays to ordinal (Map&lt;BytesRef,int&gt;) storing the hashed bytes  * efficiently in continuous storage. The mapping to the ordinal is  * encapsulated inside {@link BytesRefHash} and is guaranteed to be increased  * for each added {@link BytesRef}.  *   *<p>  * Note: The maximum capacity {@link BytesRef} instance passed to  * {@link #add(BytesRef)} must not be longer than {@link ByteBlockPool#BYTE_BLOCK_SIZE}-2.   * The internal storage is limited to 2GB total byte storage.  *</p>  *   * @lucene.internal  */
end_comment
begin_class
DECL|class|BytesRefHash
specifier|public
specifier|final
class|class
name|BytesRefHash
block|{
DECL|field|DEFAULT_CAPACITY
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_CAPACITY
init|=
literal|16
decl_stmt|;
comment|// the following fields are needed by comparator,
comment|// so package private to prevent access$-methods:
DECL|field|pool
specifier|final
name|ByteBlockPool
name|pool
decl_stmt|;
DECL|field|bytesStart
name|int
index|[]
name|bytesStart
decl_stmt|;
DECL|field|scratch1
specifier|private
specifier|final
name|BytesRef
name|scratch1
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|hashSize
specifier|private
name|int
name|hashSize
decl_stmt|;
DECL|field|hashHalfSize
specifier|private
name|int
name|hashHalfSize
decl_stmt|;
DECL|field|hashMask
specifier|private
name|int
name|hashMask
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|field|lastCount
specifier|private
name|int
name|lastCount
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|ords
specifier|private
name|int
index|[]
name|ords
decl_stmt|;
DECL|field|bytesStartArray
specifier|private
specifier|final
name|BytesStartArray
name|bytesStartArray
decl_stmt|;
DECL|field|bytesUsed
specifier|private
name|Counter
name|bytesUsed
decl_stmt|;
comment|/**    * Creates a new {@link BytesRefHash} with a {@link ByteBlockPool} using a    * {@link DirectAllocator}.    */
DECL|method|BytesRefHash
specifier|public
name|BytesRefHash
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|DirectAllocator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link BytesRefHash}    */
DECL|method|BytesRefHash
specifier|public
name|BytesRefHash
parameter_list|(
name|ByteBlockPool
name|pool
parameter_list|)
block|{
name|this
argument_list|(
name|pool
argument_list|,
name|DEFAULT_CAPACITY
argument_list|,
operator|new
name|DirectBytesStartArray
argument_list|(
name|DEFAULT_CAPACITY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link BytesRefHash}    */
DECL|method|BytesRefHash
specifier|public
name|BytesRefHash
parameter_list|(
name|ByteBlockPool
name|pool
parameter_list|,
name|int
name|capacity
parameter_list|,
name|BytesStartArray
name|bytesStartArray
parameter_list|)
block|{
name|hashSize
operator|=
name|capacity
expr_stmt|;
name|hashHalfSize
operator|=
name|hashSize
operator|>>
literal|1
expr_stmt|;
name|hashMask
operator|=
name|hashSize
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|ords
operator|=
operator|new
name|int
index|[
name|hashSize
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|ords
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytesStartArray
operator|=
name|bytesStartArray
expr_stmt|;
name|bytesStart
operator|=
name|bytesStartArray
operator|.
name|init
argument_list|()
expr_stmt|;
name|bytesUsed
operator|=
name|bytesStartArray
operator|.
name|bytesUsed
argument_list|()
operator|==
literal|null
condition|?
name|Counter
operator|.
name|newCounter
argument_list|()
else|:
name|bytesStartArray
operator|.
name|bytesUsed
argument_list|()
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|hashSize
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the number of {@link BytesRef} values in this {@link BytesRefHash}.    *     * @return the number of {@link BytesRef} values in this {@link BytesRefHash}.    */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/**    * Populates and returns a {@link BytesRef} with the bytes for the given ord.    *<p>    * Note: the given ord must be a positive integer less that the current size (    * {@link #size()})    *</p>    *    * @param ord the ord    * @param ref the {@link BytesRef} to populate    *     * @return the given BytesRef instance populated with the bytes for the given ord    */
DECL|method|get
specifier|public
name|BytesRef
name|get
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
assert|assert
name|bytesStart
operator|!=
literal|null
operator|:
literal|"bytesStart is null - not initialized"
assert|;
assert|assert
name|ord
operator|<
name|bytesStart
operator|.
name|length
operator|:
literal|"ord exceeds byteStart len: "
operator|+
name|bytesStart
operator|.
name|length
assert|;
name|pool
operator|.
name|setBytesRef
argument_list|(
name|ref
argument_list|,
name|bytesStart
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
return|return
name|ref
return|;
block|}
comment|/**    * Returns the ords array in arbitrary order. Valid ords start at offset of 0    * and end at a limit of {@link #size()} - 1    *<p>    * Note: This is a destructive operation. {@link #clear()} must be called in    * order to reuse this {@link BytesRefHash} instance.    *</p>    */
DECL|method|compact
specifier|private
name|int
index|[]
name|compact
parameter_list|()
block|{
assert|assert
name|bytesStart
operator|!=
literal|null
operator|:
literal|"Bytesstart is null - not initialized"
assert|;
name|int
name|upto
init|=
literal|0
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
name|hashSize
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ords
index|[
name|i
index|]
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|upto
operator|<
name|i
condition|)
block|{
name|ords
index|[
name|upto
index|]
operator|=
name|ords
index|[
name|i
index|]
expr_stmt|;
name|ords
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|upto
operator|++
expr_stmt|;
block|}
block|}
assert|assert
name|upto
operator|==
name|count
assert|;
name|lastCount
operator|=
name|count
expr_stmt|;
return|return
name|ords
return|;
block|}
comment|/**    * Returns the values array sorted by the referenced byte values.    *<p>    * Note: This is a destructive operation. {@link #clear()} must be called in    * order to reuse this {@link BytesRefHash} instance.    *</p>    *     * @param comp    *          the {@link Comparator} used for sorting    */
DECL|method|sort
specifier|public
name|int
index|[]
name|sort
parameter_list|(
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|compact
init|=
name|compact
argument_list|()
decl_stmt|;
operator|new
name|SorterTemplate
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
specifier|final
name|int
name|o
init|=
name|compact
index|[
name|i
index|]
decl_stmt|;
name|compact
index|[
name|i
index|]
operator|=
name|compact
index|[
name|j
index|]
expr_stmt|;
name|compact
index|[
name|j
index|]
operator|=
name|o
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
specifier|final
name|int
name|ord1
init|=
name|compact
index|[
name|i
index|]
decl_stmt|,
name|ord2
init|=
name|compact
index|[
name|j
index|]
decl_stmt|;
assert|assert
name|bytesStart
operator|.
name|length
operator|>
name|ord1
operator|&&
name|bytesStart
operator|.
name|length
operator|>
name|ord2
assert|;
name|pool
operator|.
name|setBytesRef
argument_list|(
name|scratch1
argument_list|,
name|bytesStart
index|[
name|ord1
index|]
argument_list|)
expr_stmt|;
name|pool
operator|.
name|setBytesRef
argument_list|(
name|scratch2
argument_list|,
name|bytesStart
index|[
name|ord2
index|]
argument_list|)
expr_stmt|;
return|return
name|comp
operator|.
name|compare
argument_list|(
name|scratch1
argument_list|,
name|scratch2
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|compact
index|[
name|i
index|]
decl_stmt|;
assert|assert
name|bytesStart
operator|.
name|length
operator|>
name|ord
assert|;
name|pool
operator|.
name|setBytesRef
argument_list|(
name|pivot
argument_list|,
name|bytesStart
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|compact
index|[
name|j
index|]
decl_stmt|;
assert|assert
name|bytesStart
operator|.
name|length
operator|>
name|ord
assert|;
name|pool
operator|.
name|setBytesRef
argument_list|(
name|scratch2
argument_list|,
name|bytesStart
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
return|return
name|comp
operator|.
name|compare
argument_list|(
name|pivot
argument_list|,
name|scratch2
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|BytesRef
name|pivot
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|,
name|scratch1
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|,
name|scratch2
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
block|}
operator|.
name|quickSort
argument_list|(
literal|0
argument_list|,
name|count
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|compact
return|;
block|}
DECL|method|equals
specifier|private
name|boolean
name|equals
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|b
parameter_list|)
block|{
name|pool
operator|.
name|setBytesRef
argument_list|(
name|scratch1
argument_list|,
name|bytesStart
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
return|return
name|scratch1
operator|.
name|bytesEquals
argument_list|(
name|b
argument_list|)
return|;
block|}
DECL|method|shrink
specifier|private
name|boolean
name|shrink
parameter_list|(
name|int
name|targetSize
parameter_list|)
block|{
comment|// Cannot use ArrayUtil.shrink because we require power
comment|// of 2:
name|int
name|newSize
init|=
name|hashSize
decl_stmt|;
while|while
condition|(
name|newSize
operator|>=
literal|8
operator|&&
name|newSize
operator|/
literal|4
operator|>
name|targetSize
condition|)
block|{
name|newSize
operator|/=
literal|2
expr_stmt|;
block|}
if|if
condition|(
name|newSize
operator|!=
name|hashSize
condition|)
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
operator|-
operator|(
name|hashSize
operator|-
name|newSize
operator|)
argument_list|)
expr_stmt|;
name|hashSize
operator|=
name|newSize
expr_stmt|;
name|ords
operator|=
operator|new
name|int
index|[
name|hashSize
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|ords
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|hashHalfSize
operator|=
name|newSize
operator|/
literal|2
expr_stmt|;
name|hashMask
operator|=
name|newSize
operator|-
literal|1
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Clears the {@link BytesRef} which maps to the given {@link BytesRef}    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|boolean
name|resetPool
parameter_list|)
block|{
name|lastCount
operator|=
name|count
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|resetPool
condition|)
block|{
name|pool
operator|.
name|reset
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// we don't need to 0-fill the buffers
block|}
name|bytesStart
operator|=
name|bytesStartArray
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|lastCount
operator|!=
operator|-
literal|1
operator|&&
name|shrink
argument_list|(
name|lastCount
argument_list|)
condition|)
block|{
comment|// shrink clears the hash entries
return|return;
block|}
name|Arrays
operator|.
name|fill
argument_list|(
name|ords
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|clear
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Closes the BytesRefHash and releases all internally used memory    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|clear
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ords
operator|=
literal|null
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
operator|-
name|hashSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a new {@link BytesRef}    *     * @param bytes    *          the bytes to hash    * @return the ord the given bytes are hashed if there was no mapping for the    *         given bytes, otherwise<code>(-(ord)-1)</code>. This guarantees    *         that the return value will always be&gt;= 0 if the given bytes    *         haven't been hashed before.    *     * @throws MaxBytesLengthExceededException    *           if the given bytes are> 2 +    *           {@link ByteBlockPool#BYTE_BLOCK_SIZE}    */
DECL|method|add
specifier|public
name|int
name|add
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|bytes
argument_list|,
name|bytes
operator|.
name|hashCode
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Adds a new {@link BytesRef} with a pre-calculated hash code.    *     * @param bytes    *          the bytes to hash    * @param code    *          the bytes hash code    *     *<p>    *          Hashcode is defined as:    *     *<pre class="prettyprint">    * int hash = 0;    * for (int i = offset; i&lt; offset + length; i++) {    *   hash = 31 * hash + bytes[i];    * }    *</pre>    *     * @return the ord the given bytes are hashed if there was no mapping for the    *         given bytes, otherwise<code>(-(ord)-1)</code>. This guarantees    *         that the return value will always be&gt;= 0 if the given bytes    *         haven't been hashed before.    *     * @throws MaxBytesLengthExceededException    *           if the given bytes are>    *           {@link ByteBlockPool#BYTE_BLOCK_SIZE} - 2    */
DECL|method|add
specifier|public
name|int
name|add
parameter_list|(
name|BytesRef
name|bytes
parameter_list|,
name|int
name|code
parameter_list|)
block|{
assert|assert
name|bytesStart
operator|!=
literal|null
operator|:
literal|"Bytesstart is null - not initialized"
assert|;
specifier|final
name|int
name|length
init|=
name|bytes
operator|.
name|length
decl_stmt|;
comment|// final position
name|int
name|hashPos
init|=
name|code
operator|&
name|hashMask
decl_stmt|;
name|int
name|e
init|=
name|ords
index|[
name|hashPos
index|]
decl_stmt|;
if|if
condition|(
name|e
operator|!=
operator|-
literal|1
operator|&&
operator|!
name|equals
argument_list|(
name|e
argument_list|,
name|bytes
argument_list|)
condition|)
block|{
comment|// Conflict: keep searching different locations in
comment|// the hash table.
specifier|final
name|int
name|inc
init|=
operator|(
operator|(
name|code
operator|>>
literal|8
operator|)
operator|+
name|code
operator|)
operator||
literal|1
decl_stmt|;
do|do
block|{
name|code
operator|+=
name|inc
expr_stmt|;
name|hashPos
operator|=
name|code
operator|&
name|hashMask
expr_stmt|;
name|e
operator|=
name|ords
index|[
name|hashPos
index|]
expr_stmt|;
block|}
do|while
condition|(
name|e
operator|!=
operator|-
literal|1
operator|&&
operator|!
name|equals
argument_list|(
name|e
argument_list|,
name|bytes
argument_list|)
condition|)
do|;
block|}
if|if
condition|(
name|e
operator|==
operator|-
literal|1
condition|)
block|{
comment|// new entry
specifier|final
name|int
name|len2
init|=
literal|2
operator|+
name|bytes
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|len2
operator|+
name|pool
operator|.
name|byteUpto
operator|>
name|BYTE_BLOCK_SIZE
condition|)
block|{
if|if
condition|(
name|len2
operator|>
name|BYTE_BLOCK_SIZE
condition|)
block|{
throw|throw
operator|new
name|MaxBytesLengthExceededException
argument_list|(
literal|"bytes can be at most "
operator|+
operator|(
name|BYTE_BLOCK_SIZE
operator|-
literal|2
operator|)
operator|+
literal|" in length; got "
operator|+
name|bytes
operator|.
name|length
argument_list|)
throw|;
block|}
name|pool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|buffer
init|=
name|pool
operator|.
name|buffer
decl_stmt|;
specifier|final
name|int
name|bufferUpto
init|=
name|pool
operator|.
name|byteUpto
decl_stmt|;
if|if
condition|(
name|count
operator|>=
name|bytesStart
operator|.
name|length
condition|)
block|{
name|bytesStart
operator|=
name|bytesStartArray
operator|.
name|grow
argument_list|()
expr_stmt|;
assert|assert
name|count
operator|<
name|bytesStart
operator|.
name|length
operator|+
literal|1
operator|:
literal|"count: "
operator|+
name|count
operator|+
literal|" len: "
operator|+
name|bytesStart
operator|.
name|length
assert|;
block|}
name|e
operator|=
name|count
operator|++
expr_stmt|;
name|bytesStart
index|[
name|e
index|]
operator|=
name|bufferUpto
operator|+
name|pool
operator|.
name|byteOffset
expr_stmt|;
comment|// We first encode the length, followed by the
comment|// bytes. Length is encoded as vInt, but will consume
comment|// 1 or 2 bytes at most (we reject too-long terms,
comment|// above).
if|if
condition|(
name|length
operator|<
literal|128
condition|)
block|{
comment|// 1 byte to store length
name|buffer
index|[
name|bufferUpto
index|]
operator|=
operator|(
name|byte
operator|)
name|length
expr_stmt|;
name|pool
operator|.
name|byteUpto
operator|+=
name|length
operator|+
literal|1
expr_stmt|;
assert|assert
name|length
operator|>=
literal|0
operator|:
literal|"Length must be positive: "
operator|+
name|length
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|buffer
argument_list|,
name|bufferUpto
operator|+
literal|1
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// 2 byte to store length
name|buffer
index|[
name|bufferUpto
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|length
operator|&
literal|0x7f
operator|)
argument_list|)
expr_stmt|;
name|buffer
index|[
name|bufferUpto
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|length
operator|>>
literal|7
operator|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|pool
operator|.
name|byteUpto
operator|+=
name|length
operator|+
literal|2
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|buffer
argument_list|,
name|bufferUpto
operator|+
literal|2
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
assert|assert
name|ords
index|[
name|hashPos
index|]
operator|==
operator|-
literal|1
assert|;
name|ords
index|[
name|hashPos
index|]
operator|=
name|e
expr_stmt|;
if|if
condition|(
name|count
operator|==
name|hashHalfSize
condition|)
block|{
name|rehash
argument_list|(
literal|2
operator|*
name|hashSize
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|e
return|;
block|}
return|return
operator|-
operator|(
name|e
operator|+
literal|1
operator|)
return|;
block|}
DECL|method|addByPoolOffset
specifier|public
name|int
name|addByPoolOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
assert|assert
name|bytesStart
operator|!=
literal|null
operator|:
literal|"Bytesstart is null - not initialized"
assert|;
comment|// final position
name|int
name|code
init|=
name|offset
decl_stmt|;
name|int
name|hashPos
init|=
name|offset
operator|&
name|hashMask
decl_stmt|;
name|int
name|e
init|=
name|ords
index|[
name|hashPos
index|]
decl_stmt|;
if|if
condition|(
name|e
operator|!=
operator|-
literal|1
operator|&&
name|bytesStart
index|[
name|e
index|]
operator|!=
name|offset
condition|)
block|{
comment|// Conflict: keep searching different locations in
comment|// the hash table.
specifier|final
name|int
name|inc
init|=
operator|(
operator|(
name|code
operator|>>
literal|8
operator|)
operator|+
name|code
operator|)
operator||
literal|1
decl_stmt|;
do|do
block|{
name|code
operator|+=
name|inc
expr_stmt|;
name|hashPos
operator|=
name|code
operator|&
name|hashMask
expr_stmt|;
name|e
operator|=
name|ords
index|[
name|hashPos
index|]
expr_stmt|;
block|}
do|while
condition|(
name|e
operator|!=
operator|-
literal|1
operator|&&
name|bytesStart
index|[
name|e
index|]
operator|!=
name|offset
condition|)
do|;
block|}
if|if
condition|(
name|e
operator|==
operator|-
literal|1
condition|)
block|{
comment|// new entry
if|if
condition|(
name|count
operator|>=
name|bytesStart
operator|.
name|length
condition|)
block|{
name|bytesStart
operator|=
name|bytesStartArray
operator|.
name|grow
argument_list|()
expr_stmt|;
assert|assert
name|count
operator|<
name|bytesStart
operator|.
name|length
operator|+
literal|1
operator|:
literal|"count: "
operator|+
name|count
operator|+
literal|" len: "
operator|+
name|bytesStart
operator|.
name|length
assert|;
block|}
name|e
operator|=
name|count
operator|++
expr_stmt|;
name|bytesStart
index|[
name|e
index|]
operator|=
name|offset
expr_stmt|;
assert|assert
name|ords
index|[
name|hashPos
index|]
operator|==
operator|-
literal|1
assert|;
name|ords
index|[
name|hashPos
index|]
operator|=
name|e
expr_stmt|;
if|if
condition|(
name|count
operator|==
name|hashHalfSize
condition|)
block|{
name|rehash
argument_list|(
literal|2
operator|*
name|hashSize
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|e
return|;
block|}
return|return
operator|-
operator|(
name|e
operator|+
literal|1
operator|)
return|;
block|}
comment|/**    * Called when hash is too small (> 50% occupied) or too large (< 20%    * occupied).    */
DECL|method|rehash
specifier|private
name|void
name|rehash
parameter_list|(
specifier|final
name|int
name|newSize
parameter_list|,
name|boolean
name|hashOnData
parameter_list|)
block|{
specifier|final
name|int
name|newMask
init|=
name|newSize
operator|-
literal|1
decl_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
operator|(
name|newSize
operator|)
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
name|newHash
init|=
operator|new
name|int
index|[
name|newSize
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|newHash
argument_list|,
operator|-
literal|1
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
name|hashSize
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|e0
init|=
name|ords
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|e0
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|code
decl_stmt|;
if|if
condition|(
name|hashOnData
condition|)
block|{
specifier|final
name|int
name|off
init|=
name|bytesStart
index|[
name|e0
index|]
decl_stmt|;
specifier|final
name|int
name|start
init|=
name|off
operator|&
name|BYTE_BLOCK_MASK
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
name|pool
operator|.
name|buffers
index|[
name|off
operator|>>
name|BYTE_BLOCK_SHIFT
index|]
decl_stmt|;
name|code
operator|=
literal|0
expr_stmt|;
specifier|final
name|int
name|len
decl_stmt|;
name|int
name|pos
decl_stmt|;
if|if
condition|(
operator|(
name|bytes
index|[
name|start
index|]
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
block|{
comment|// length is 1 byte
name|len
operator|=
name|bytes
index|[
name|start
index|]
expr_stmt|;
name|pos
operator|=
name|start
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|len
operator|=
operator|(
name|bytes
index|[
name|start
index|]
operator|&
literal|0x7f
operator|)
operator|+
operator|(
operator|(
name|bytes
index|[
name|start
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|7
operator|)
expr_stmt|;
name|pos
operator|=
name|start
operator|+
literal|2
expr_stmt|;
block|}
specifier|final
name|int
name|endPos
init|=
name|pos
operator|+
name|len
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|endPos
condition|)
block|{
name|code
operator|=
literal|31
operator|*
name|code
operator|+
name|bytes
index|[
name|pos
operator|++
index|]
expr_stmt|;
block|}
block|}
else|else
block|{
name|code
operator|=
name|bytesStart
index|[
name|e0
index|]
expr_stmt|;
block|}
name|int
name|hashPos
init|=
name|code
operator|&
name|newMask
decl_stmt|;
assert|assert
name|hashPos
operator|>=
literal|0
assert|;
if|if
condition|(
name|newHash
index|[
name|hashPos
index|]
operator|!=
operator|-
literal|1
condition|)
block|{
specifier|final
name|int
name|inc
init|=
operator|(
operator|(
name|code
operator|>>
literal|8
operator|)
operator|+
name|code
operator|)
operator||
literal|1
decl_stmt|;
do|do
block|{
name|code
operator|+=
name|inc
expr_stmt|;
name|hashPos
operator|=
name|code
operator|&
name|newMask
expr_stmt|;
block|}
do|while
condition|(
name|newHash
index|[
name|hashPos
index|]
operator|!=
operator|-
literal|1
condition|)
do|;
block|}
name|newHash
index|[
name|hashPos
index|]
operator|=
name|e0
expr_stmt|;
block|}
block|}
name|hashMask
operator|=
name|newMask
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
operator|(
operator|-
name|ords
operator|.
name|length
operator|)
argument_list|)
expr_stmt|;
name|ords
operator|=
name|newHash
expr_stmt|;
name|hashSize
operator|=
name|newSize
expr_stmt|;
name|hashHalfSize
operator|=
name|newSize
operator|/
literal|2
expr_stmt|;
block|}
comment|/**    * reinitializes the {@link BytesRefHash} after a previous {@link #clear()}    * call. If {@link #clear()} has not been called previously this method has no    * effect.    */
DECL|method|reinit
specifier|public
name|void
name|reinit
parameter_list|()
block|{
if|if
condition|(
name|bytesStart
operator|==
literal|null
condition|)
block|{
name|bytesStart
operator|=
name|bytesStartArray
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ords
operator|==
literal|null
condition|)
block|{
name|ords
operator|=
operator|new
name|int
index|[
name|hashSize
index|]
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
name|hashSize
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the bytesStart offset into the internally used    * {@link ByteBlockPool} for the given ord    *     * @param ord    *          the ord to look up    * @return the bytesStart offset into the internally used    *         {@link ByteBlockPool} for the given ord    */
DECL|method|byteStart
specifier|public
name|int
name|byteStart
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
assert|assert
name|bytesStart
operator|!=
literal|null
operator|:
literal|"Bytesstart is null - not initialized"
assert|;
assert|assert
name|ord
operator|>=
literal|0
operator|&&
name|ord
operator|<
name|count
operator|:
name|ord
assert|;
return|return
name|bytesStart
index|[
name|ord
index|]
return|;
block|}
comment|/**    * Thrown if a {@link BytesRef} exceeds the {@link BytesRefHash} limit of    * {@link ByteBlockPool#BYTE_BLOCK_SIZE}-2.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|MaxBytesLengthExceededException
specifier|public
specifier|static
class|class
name|MaxBytesLengthExceededException
extends|extends
name|RuntimeException
block|{
DECL|method|MaxBytesLengthExceededException
name|MaxBytesLengthExceededException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Manages allocation of the per-term addresses. */
DECL|class|BytesStartArray
specifier|public
specifier|abstract
specifier|static
class|class
name|BytesStartArray
block|{
comment|/**      * Initializes the BytesStartArray. This call will allocate memory      *       * @return the initialized bytes start array      */
DECL|method|init
specifier|public
specifier|abstract
name|int
index|[]
name|init
parameter_list|()
function_decl|;
comment|/**      * Grows the {@link BytesStartArray}      *       * @return the grown array      */
DECL|method|grow
specifier|public
specifier|abstract
name|int
index|[]
name|grow
parameter_list|()
function_decl|;
comment|/**      * clears the {@link BytesStartArray} and returns the cleared instance.      *       * @return the cleared instance, this might be<code>null</code>      */
DECL|method|clear
specifier|public
specifier|abstract
name|int
index|[]
name|clear
parameter_list|()
function_decl|;
comment|/**      * A {@link Counter} reference holding the number of bytes used by this      * {@link BytesStartArray}. The {@link BytesRefHash} uses this reference to      * track it memory usage      *       * @return a {@link AtomicLong} reference holding the number of bytes used      *         by this {@link BytesStartArray}.      */
DECL|method|bytesUsed
specifier|public
specifier|abstract
name|Counter
name|bytesUsed
parameter_list|()
function_decl|;
block|}
comment|/** A simple {@link BytesStartArray} that tracks    *  memory allocation using a private {@link AtomicLong}    *  instance.  */
DECL|class|DirectBytesStartArray
specifier|public
specifier|static
class|class
name|DirectBytesStartArray
extends|extends
name|BytesStartArray
block|{
comment|// TODO: can't we just merge this w/
comment|// TrackingDirectBytesStartArray...?  Just add a ctor
comment|// that makes a private bytesUsed?
DECL|field|initSize
specifier|protected
specifier|final
name|int
name|initSize
decl_stmt|;
DECL|field|bytesStart
specifier|private
name|int
index|[]
name|bytesStart
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|method|DirectBytesStartArray
specifier|public
name|DirectBytesStartArray
parameter_list|(
name|int
name|initSize
parameter_list|,
name|Counter
name|counter
parameter_list|)
block|{
name|this
operator|.
name|bytesUsed
operator|=
name|counter
expr_stmt|;
name|this
operator|.
name|initSize
operator|=
name|initSize
expr_stmt|;
block|}
DECL|method|DirectBytesStartArray
specifier|public
name|DirectBytesStartArray
parameter_list|(
name|int
name|initSize
parameter_list|)
block|{
name|this
argument_list|(
name|initSize
argument_list|,
name|Counter
operator|.
name|newCounter
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|int
index|[]
name|clear
parameter_list|()
block|{
return|return
name|bytesStart
operator|=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|grow
specifier|public
name|int
index|[]
name|grow
parameter_list|()
block|{
assert|assert
name|bytesStart
operator|!=
literal|null
assert|;
return|return
name|bytesStart
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|bytesStart
argument_list|,
name|bytesStart
operator|.
name|length
operator|+
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|int
index|[]
name|init
parameter_list|()
block|{
return|return
name|bytesStart
operator|=
operator|new
name|int
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|initSize
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|bytesUsed
specifier|public
name|Counter
name|bytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
return|;
block|}
block|}
block|}
end_class
end_unit
