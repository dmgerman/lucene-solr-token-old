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
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/**  * A pool for int blocks similar to {@link ByteBlockPool}  * @lucene.internal  */
end_comment
begin_class
DECL|class|IntBlockPool
specifier|public
specifier|final
class|class
name|IntBlockPool
block|{
DECL|field|INT_BLOCK_SHIFT
specifier|public
specifier|final
specifier|static
name|int
name|INT_BLOCK_SHIFT
init|=
literal|13
decl_stmt|;
DECL|field|INT_BLOCK_SIZE
specifier|public
specifier|final
specifier|static
name|int
name|INT_BLOCK_SIZE
init|=
literal|1
operator|<<
name|INT_BLOCK_SHIFT
decl_stmt|;
DECL|field|INT_BLOCK_MASK
specifier|public
specifier|final
specifier|static
name|int
name|INT_BLOCK_MASK
init|=
name|INT_BLOCK_SIZE
operator|-
literal|1
decl_stmt|;
comment|/** Abstract class for allocating and freeing int    *  blocks. */
DECL|class|Allocator
specifier|public
specifier|abstract
specifier|static
class|class
name|Allocator
block|{
DECL|field|blockSize
specifier|protected
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|method|Allocator
specifier|public
name|Allocator
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
block|}
DECL|method|recycleIntBlocks
specifier|public
specifier|abstract
name|void
name|recycleIntBlocks
parameter_list|(
name|int
index|[]
index|[]
name|blocks
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
function_decl|;
DECL|method|getIntBlock
specifier|public
name|int
index|[]
name|getIntBlock
parameter_list|()
block|{
return|return
operator|new
name|int
index|[
name|blockSize
index|]
return|;
block|}
block|}
comment|/** A simple {@link Allocator} that never recycles. */
DECL|class|DirectAllocator
specifier|public
specifier|static
specifier|final
class|class
name|DirectAllocator
extends|extends
name|Allocator
block|{
comment|/**      * Creates a new {@link DirectAllocator} with a default block size      */
DECL|method|DirectAllocator
specifier|public
name|DirectAllocator
parameter_list|()
block|{
name|super
argument_list|(
name|INT_BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recycleIntBlocks
specifier|public
name|void
name|recycleIntBlocks
parameter_list|(
name|int
index|[]
index|[]
name|blocks
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{     }
block|}
comment|/** array of buffers currently used in the pool. Buffers are allocated if needed don't modify this outside of this class */
DECL|field|buffers
specifier|public
name|int
index|[]
index|[]
name|buffers
init|=
operator|new
name|int
index|[
literal|10
index|]
index|[]
decl_stmt|;
comment|/** index into the buffers array pointing to the current buffer used as the head */
DECL|field|bufferUpto
specifier|private
name|int
name|bufferUpto
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Pointer to the current position in head buffer */
DECL|field|intUpto
specifier|public
name|int
name|intUpto
init|=
name|INT_BLOCK_SIZE
decl_stmt|;
comment|/** Current head buffer */
DECL|field|buffer
specifier|public
name|int
index|[]
name|buffer
decl_stmt|;
comment|/** Current head offset */
DECL|field|intOffset
specifier|public
name|int
name|intOffset
init|=
operator|-
name|INT_BLOCK_SIZE
decl_stmt|;
DECL|field|allocator
specifier|private
specifier|final
name|Allocator
name|allocator
decl_stmt|;
comment|/**    * Creates a new {@link IntBlockPool} with a default {@link Allocator}.    * @see IntBlockPool#nextBuffer()    */
DECL|method|IntBlockPool
specifier|public
name|IntBlockPool
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|DirectAllocator
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link IntBlockPool} with the given {@link Allocator}.    * @see IntBlockPool#nextBuffer()    */
DECL|method|IntBlockPool
specifier|public
name|IntBlockPool
parameter_list|(
name|Allocator
name|allocator
parameter_list|)
block|{
name|this
operator|.
name|allocator
operator|=
name|allocator
expr_stmt|;
block|}
comment|/**    * Resets the pool to its initial state reusing the first buffer. Calling    * {@link IntBlockPool#nextBuffer()} is not needed after reset.    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|this
operator|.
name|reset
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: Resets the pool to its initial state reusing the first buffer.     * @param zeroFillBuffers if<code>true</code> the buffers are filled with<tt>0</tt>.     *        This should be set to<code>true</code> if this pool is used with     *        {@link SliceWriter}.    * @param reuseFirst if<code>true</code> the first buffer will be reused and calling    *        {@link IntBlockPool#nextBuffer()} is not needed after reset iff the     *        block pool was used before ie. {@link IntBlockPool#nextBuffer()} was called before.    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|boolean
name|zeroFillBuffers
parameter_list|,
name|boolean
name|reuseFirst
parameter_list|)
block|{
if|if
condition|(
name|bufferUpto
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// We allocated at least one buffer
if|if
condition|(
name|zeroFillBuffers
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
name|bufferUpto
condition|;
name|i
operator|++
control|)
block|{
comment|// Fully zero fill buffers that we fully used
name|Arrays
operator|.
name|fill
argument_list|(
name|buffers
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// Partial zero fill the final buffer
name|Arrays
operator|.
name|fill
argument_list|(
name|buffers
index|[
name|bufferUpto
index|]
argument_list|,
literal|0
argument_list|,
name|intUpto
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bufferUpto
operator|>
literal|0
operator|||
operator|!
name|reuseFirst
condition|)
block|{
specifier|final
name|int
name|offset
init|=
name|reuseFirst
condition|?
literal|1
else|:
literal|0
decl_stmt|;
comment|// Recycle all but the first buffer
name|allocator
operator|.
name|recycleIntBlocks
argument_list|(
name|buffers
argument_list|,
name|offset
argument_list|,
literal|1
operator|+
name|bufferUpto
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|buffers
argument_list|,
name|offset
argument_list|,
name|bufferUpto
operator|+
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reuseFirst
condition|)
block|{
comment|// Re-use the first buffer
name|bufferUpto
operator|=
literal|0
expr_stmt|;
name|intUpto
operator|=
literal|0
expr_stmt|;
name|intOffset
operator|=
literal|0
expr_stmt|;
name|buffer
operator|=
name|buffers
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|bufferUpto
operator|=
operator|-
literal|1
expr_stmt|;
name|buffers
index|[
literal|0
index|]
operator|=
literal|null
expr_stmt|;
name|intUpto
operator|=
name|INT_BLOCK_SIZE
expr_stmt|;
name|intOffset
operator|=
operator|-
name|INT_BLOCK_SIZE
expr_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Advances the pool to its next buffer. This method should be called once    * after the constructor to initialize the pool. In contrast to the    * constructor a {@link IntBlockPool#reset()} call will advance the pool to    * its first buffer immediately.    */
DECL|method|nextBuffer
specifier|public
name|void
name|nextBuffer
parameter_list|()
block|{
if|if
condition|(
literal|1
operator|+
name|bufferUpto
operator|==
name|buffers
operator|.
name|length
condition|)
block|{
name|int
index|[]
index|[]
name|newBuffers
init|=
operator|new
name|int
index|[
call|(
name|int
call|)
argument_list|(
name|buffers
operator|.
name|length
operator|*
literal|1.5
argument_list|)
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffers
argument_list|,
literal|0
argument_list|,
name|newBuffers
argument_list|,
literal|0
argument_list|,
name|buffers
operator|.
name|length
argument_list|)
expr_stmt|;
name|buffers
operator|=
name|newBuffers
expr_stmt|;
block|}
name|buffer
operator|=
name|buffers
index|[
literal|1
operator|+
name|bufferUpto
index|]
operator|=
name|allocator
operator|.
name|getIntBlock
argument_list|()
expr_stmt|;
name|bufferUpto
operator|++
expr_stmt|;
name|intUpto
operator|=
literal|0
expr_stmt|;
name|intOffset
operator|+=
name|INT_BLOCK_SIZE
expr_stmt|;
block|}
comment|/**    * Creates a new int slice with the given starting size and returns the slices offset in the pool.    * @see SliceReader    */
DECL|method|newSlice
specifier|private
name|int
name|newSlice
parameter_list|(
specifier|final
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|intUpto
operator|>
name|INT_BLOCK_SIZE
operator|-
name|size
condition|)
block|{
name|nextBuffer
argument_list|()
expr_stmt|;
assert|assert
name|assertSliceBuffer
argument_list|(
name|buffer
argument_list|)
assert|;
block|}
specifier|final
name|int
name|upto
init|=
name|intUpto
decl_stmt|;
name|intUpto
operator|+=
name|size
expr_stmt|;
name|buffer
index|[
name|intUpto
operator|-
literal|1
index|]
operator|=
literal|1
expr_stmt|;
return|return
name|upto
return|;
block|}
DECL|method|assertSliceBuffer
specifier|private
specifier|static
specifier|final
name|boolean
name|assertSliceBuffer
parameter_list|(
name|int
index|[]
name|buffer
parameter_list|)
block|{
name|int
name|count
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
name|buffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|count
operator|+=
name|buffer
index|[
name|i
index|]
expr_stmt|;
comment|// for slices the buffer must only have 0 values
block|}
return|return
name|count
operator|==
literal|0
return|;
block|}
comment|// no need to make this public unless we support different sizes
comment|// TODO make the levels and the sizes configurable
comment|/**    * An array holding the offset into the {@link IntBlockPool#LEVEL_SIZE_ARRAY}    * to quickly navigate to the next slice level.    */
DECL|field|NEXT_LEVEL_ARRAY
specifier|private
specifier|final
specifier|static
name|int
index|[]
name|NEXT_LEVEL_ARRAY
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|9
block|}
decl_stmt|;
comment|/**    * An array holding the level sizes for int slices.    */
DECL|field|LEVEL_SIZE_ARRAY
specifier|private
specifier|final
specifier|static
name|int
index|[]
name|LEVEL_SIZE_ARRAY
init|=
block|{
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|16
block|,
literal|32
block|,
literal|64
block|,
literal|128
block|,
literal|256
block|,
literal|512
block|,
literal|1024
block|}
decl_stmt|;
comment|/**    * The first level size for new slices    */
DECL|field|FIRST_LEVEL_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|FIRST_LEVEL_SIZE
init|=
name|LEVEL_SIZE_ARRAY
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Allocates a new slice from the given offset    */
DECL|method|allocSlice
specifier|private
name|int
name|allocSlice
parameter_list|(
specifier|final
name|int
index|[]
name|slice
parameter_list|,
specifier|final
name|int
name|sliceOffset
parameter_list|)
block|{
specifier|final
name|int
name|level
init|=
name|slice
index|[
name|sliceOffset
index|]
decl_stmt|;
specifier|final
name|int
name|newLevel
init|=
name|NEXT_LEVEL_ARRAY
index|[
name|level
operator|-
literal|1
index|]
decl_stmt|;
specifier|final
name|int
name|newSize
init|=
name|LEVEL_SIZE_ARRAY
index|[
name|newLevel
index|]
decl_stmt|;
comment|// Maybe allocate another block
if|if
condition|(
name|intUpto
operator|>
name|INT_BLOCK_SIZE
operator|-
name|newSize
condition|)
block|{
name|nextBuffer
argument_list|()
expr_stmt|;
assert|assert
name|assertSliceBuffer
argument_list|(
name|buffer
argument_list|)
assert|;
block|}
specifier|final
name|int
name|newUpto
init|=
name|intUpto
decl_stmt|;
specifier|final
name|int
name|offset
init|=
name|newUpto
operator|+
name|intOffset
decl_stmt|;
name|intUpto
operator|+=
name|newSize
expr_stmt|;
comment|// Write forwarding address at end of last slice:
name|slice
index|[
name|sliceOffset
index|]
operator|=
name|offset
expr_stmt|;
comment|// Write new level:
name|buffer
index|[
name|intUpto
operator|-
literal|1
index|]
operator|=
name|newLevel
expr_stmt|;
return|return
name|newUpto
return|;
block|}
comment|/**    * A {@link SliceWriter} that allows to write multiple integer slices into a given {@link IntBlockPool}.    *     *  @see SliceReader    *  @lucene.internal    */
DECL|class|SliceWriter
specifier|public
specifier|static
class|class
name|SliceWriter
block|{
DECL|field|offset
specifier|private
name|int
name|offset
decl_stmt|;
DECL|field|pool
specifier|private
specifier|final
name|IntBlockPool
name|pool
decl_stmt|;
DECL|method|SliceWriter
specifier|public
name|SliceWriter
parameter_list|(
name|IntBlockPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
comment|/**      *       */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|int
name|sliceOffset
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|sliceOffset
expr_stmt|;
block|}
comment|/**      * Writes the given value into the slice and resizes the slice if needed      */
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|int
index|[]
name|ints
init|=
name|pool
operator|.
name|buffers
index|[
name|offset
operator|>>
name|INT_BLOCK_SHIFT
index|]
decl_stmt|;
assert|assert
name|ints
operator|!=
literal|null
assert|;
name|int
name|relativeOffset
init|=
name|offset
operator|&
name|INT_BLOCK_MASK
decl_stmt|;
if|if
condition|(
name|ints
index|[
name|relativeOffset
index|]
operator|!=
literal|0
condition|)
block|{
comment|// End of slice; allocate a new one
name|relativeOffset
operator|=
name|pool
operator|.
name|allocSlice
argument_list|(
name|ints
argument_list|,
name|relativeOffset
argument_list|)
expr_stmt|;
name|ints
operator|=
name|pool
operator|.
name|buffer
expr_stmt|;
name|offset
operator|=
name|relativeOffset
operator|+
name|pool
operator|.
name|intOffset
expr_stmt|;
block|}
name|ints
index|[
name|relativeOffset
index|]
operator|=
name|value
expr_stmt|;
name|offset
operator|++
expr_stmt|;
block|}
comment|/**      * starts a new slice and returns the start offset. The returned value      * should be used as the start offset to initialize a {@link SliceReader}.      */
DECL|method|startNewSlice
specifier|public
name|int
name|startNewSlice
parameter_list|()
block|{
return|return
name|offset
operator|=
name|pool
operator|.
name|newSlice
argument_list|(
name|FIRST_LEVEL_SIZE
argument_list|)
operator|+
name|pool
operator|.
name|intOffset
return|;
block|}
comment|/**      * Returns the offset of the currently written slice. The returned value      * should be used as the end offset to initialize a {@link SliceReader} once      * this slice is fully written or to reset the this writer if another slice      * needs to be written.      */
DECL|method|getCurrentOffset
specifier|public
name|int
name|getCurrentOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
block|}
comment|/**    * A {@link SliceReader} that can read int slices written by a {@link SliceWriter}    * @lucene.internal    */
DECL|class|SliceReader
specifier|public
specifier|static
specifier|final
class|class
name|SliceReader
block|{
DECL|field|pool
specifier|private
specifier|final
name|IntBlockPool
name|pool
decl_stmt|;
DECL|field|upto
specifier|private
name|int
name|upto
decl_stmt|;
DECL|field|bufferUpto
specifier|private
name|int
name|bufferUpto
decl_stmt|;
DECL|field|bufferOffset
specifier|private
name|int
name|bufferOffset
decl_stmt|;
DECL|field|buffer
specifier|private
name|int
index|[]
name|buffer
decl_stmt|;
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|field|level
specifier|private
name|int
name|level
decl_stmt|;
DECL|field|end
specifier|private
name|int
name|end
decl_stmt|;
comment|/**      * Creates a new {@link SliceReader} on the given pool      */
DECL|method|SliceReader
specifier|public
name|SliceReader
parameter_list|(
name|IntBlockPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
comment|/**      * Resets the reader to a slice give the slices absolute start and end offset in the pool      */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
name|bufferUpto
operator|=
name|startOffset
operator|/
name|INT_BLOCK_SIZE
expr_stmt|;
name|bufferOffset
operator|=
name|bufferUpto
operator|*
name|INT_BLOCK_SIZE
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|endOffset
expr_stmt|;
name|upto
operator|=
name|startOffset
expr_stmt|;
name|level
operator|=
literal|1
expr_stmt|;
name|buffer
operator|=
name|pool
operator|.
name|buffers
index|[
name|bufferUpto
index|]
expr_stmt|;
name|upto
operator|=
name|startOffset
operator|&
name|INT_BLOCK_MASK
expr_stmt|;
specifier|final
name|int
name|firstSize
init|=
name|IntBlockPool
operator|.
name|LEVEL_SIZE_ARRAY
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|startOffset
operator|+
name|firstSize
operator|>=
name|endOffset
condition|)
block|{
comment|// There is only this one slice to read
name|limit
operator|=
name|endOffset
operator|&
name|INT_BLOCK_MASK
expr_stmt|;
block|}
else|else
block|{
name|limit
operator|=
name|upto
operator|+
name|firstSize
operator|-
literal|1
expr_stmt|;
block|}
block|}
comment|/**      * Returns<code>true</code> iff the current slice is fully read. If this      * method returns<code>true</code> {@link SliceReader#readInt()} should not      * be called again on this slice.      */
DECL|method|endOfSlice
specifier|public
name|boolean
name|endOfSlice
parameter_list|()
block|{
assert|assert
name|upto
operator|+
name|bufferOffset
operator|<=
name|end
assert|;
return|return
name|upto
operator|+
name|bufferOffset
operator|==
name|end
return|;
block|}
comment|/**      * Reads the next int from the current slice and returns it.      * @see SliceReader#endOfSlice()      */
DECL|method|readInt
specifier|public
name|int
name|readInt
parameter_list|()
block|{
assert|assert
operator|!
name|endOfSlice
argument_list|()
assert|;
assert|assert
name|upto
operator|<=
name|limit
assert|;
if|if
condition|(
name|upto
operator|==
name|limit
condition|)
name|nextSlice
argument_list|()
expr_stmt|;
return|return
name|buffer
index|[
name|upto
operator|++
index|]
return|;
block|}
DECL|method|nextSlice
specifier|private
name|void
name|nextSlice
parameter_list|()
block|{
comment|// Skip to our next slice
specifier|final
name|int
name|nextIndex
init|=
name|buffer
index|[
name|limit
index|]
decl_stmt|;
name|level
operator|=
name|NEXT_LEVEL_ARRAY
index|[
name|level
operator|-
literal|1
index|]
expr_stmt|;
specifier|final
name|int
name|newSize
init|=
name|LEVEL_SIZE_ARRAY
index|[
name|level
index|]
decl_stmt|;
name|bufferUpto
operator|=
name|nextIndex
operator|/
name|INT_BLOCK_SIZE
expr_stmt|;
name|bufferOffset
operator|=
name|bufferUpto
operator|*
name|INT_BLOCK_SIZE
expr_stmt|;
name|buffer
operator|=
name|pool
operator|.
name|buffers
index|[
name|bufferUpto
index|]
expr_stmt|;
name|upto
operator|=
name|nextIndex
operator|&
name|INT_BLOCK_MASK
expr_stmt|;
if|if
condition|(
name|nextIndex
operator|+
name|newSize
operator|>=
name|end
condition|)
block|{
comment|// We are advancing to the final slice
assert|assert
name|end
operator|-
name|nextIndex
operator|>
literal|0
assert|;
name|limit
operator|=
name|end
operator|-
name|bufferOffset
expr_stmt|;
block|}
else|else
block|{
comment|// This is not the final slice (subtract 4 for the
comment|// forwarding address at the end of this new slice)
name|limit
operator|=
name|upto
operator|+
name|newSize
operator|-
literal|1
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
