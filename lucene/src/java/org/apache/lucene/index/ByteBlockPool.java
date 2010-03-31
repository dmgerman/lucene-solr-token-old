begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/* Class that Posting and PostingVector use to write byte  * streams into shared fixed-size byte[] arrays.  The idea  * is to allocate slices of increasing lengths For  * example, the first slice is 5 bytes, the next slice is  * 14, etc.  We start by writing our bytes into the first  * 5 bytes.  When we hit the end of the slice, we allocate  * the next slice and then write the address of the new  * slice into the last 4 bytes of the previous slice (the  * "forwarding address").  *  * Each slice is filled with 0's initially, and we mark  * the end with a non-zero byte.  This way the methods  * that are writing into the slice don't need to record  * its length and instead allocate a new slice once they  * hit a non-zero byte. */
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
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
begin_class
DECL|class|ByteBlockPool
specifier|final
class|class
name|ByteBlockPool
block|{
DECL|class|Allocator
specifier|abstract
specifier|static
class|class
name|Allocator
block|{
DECL|method|recycleByteBlocks
specifier|abstract
name|void
name|recycleByteBlocks
parameter_list|(
name|byte
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
DECL|method|recycleByteBlocks
specifier|abstract
name|void
name|recycleByteBlocks
parameter_list|(
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|blocks
parameter_list|)
function_decl|;
DECL|method|getByteBlock
specifier|abstract
name|byte
index|[]
name|getByteBlock
parameter_list|(
name|boolean
name|trackAllocations
parameter_list|)
function_decl|;
block|}
DECL|field|buffers
specifier|public
name|byte
index|[]
index|[]
name|buffers
init|=
operator|new
name|byte
index|[
literal|10
index|]
index|[]
decl_stmt|;
DECL|field|bufferUpto
name|int
name|bufferUpto
init|=
operator|-
literal|1
decl_stmt|;
comment|// Which buffer we are upto
DECL|field|byteUpto
specifier|public
name|int
name|byteUpto
init|=
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_SIZE
decl_stmt|;
comment|// Where we are in head buffer
DECL|field|buffer
specifier|public
name|byte
index|[]
name|buffer
decl_stmt|;
comment|// Current head buffer
DECL|field|byteOffset
specifier|public
name|int
name|byteOffset
init|=
operator|-
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_SIZE
decl_stmt|;
comment|// Current head offset
DECL|field|trackAllocations
specifier|private
specifier|final
name|boolean
name|trackAllocations
decl_stmt|;
DECL|field|allocator
specifier|private
specifier|final
name|Allocator
name|allocator
decl_stmt|;
DECL|method|ByteBlockPool
specifier|public
name|ByteBlockPool
parameter_list|(
name|Allocator
name|allocator
parameter_list|,
name|boolean
name|trackAllocations
parameter_list|)
block|{
name|this
operator|.
name|allocator
operator|=
name|allocator
expr_stmt|;
name|this
operator|.
name|trackAllocations
operator|=
name|trackAllocations
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
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
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
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
name|byteUpto
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|bufferUpto
operator|>
literal|0
condition|)
comment|// Recycle all but the first buffer
name|allocator
operator|.
name|recycleByteBlocks
argument_list|(
name|buffers
argument_list|,
literal|1
argument_list|,
literal|1
operator|+
name|bufferUpto
argument_list|)
expr_stmt|;
comment|// Re-use the first buffer
name|bufferUpto
operator|=
literal|0
expr_stmt|;
name|byteUpto
operator|=
literal|0
expr_stmt|;
name|byteOffset
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
block|}
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
name|byte
index|[]
index|[]
name|newBuffers
init|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|buffers
operator|.
name|length
operator|+
literal|1
argument_list|,
name|NUM_BYTES_OBJECT_REF
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
name|getByteBlock
argument_list|(
name|trackAllocations
argument_list|)
expr_stmt|;
name|bufferUpto
operator|++
expr_stmt|;
name|byteUpto
operator|=
literal|0
expr_stmt|;
name|byteOffset
operator|+=
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_SIZE
expr_stmt|;
block|}
DECL|method|newSlice
specifier|public
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
name|byteUpto
operator|>
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_SIZE
operator|-
name|size
condition|)
name|nextBuffer
argument_list|()
expr_stmt|;
specifier|final
name|int
name|upto
init|=
name|byteUpto
decl_stmt|;
name|byteUpto
operator|+=
name|size
expr_stmt|;
name|buffer
index|[
name|byteUpto
operator|-
literal|1
index|]
operator|=
literal|16
expr_stmt|;
return|return
name|upto
return|;
block|}
comment|// Size of each slice.  These arrays should be at most 16
comment|// elements (index is encoded with 4 bits).  First array
comment|// is just a compact way to encode X+1 with a max.  Second
comment|// array is the length of each slice, ie first slice is 5
comment|// bytes, next slice is 14 bytes, etc.
DECL|field|nextLevelArray
specifier|final
specifier|static
name|int
index|[]
name|nextLevelArray
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
DECL|field|levelSizeArray
specifier|final
specifier|static
name|int
index|[]
name|levelSizeArray
init|=
block|{
literal|5
block|,
literal|14
block|,
literal|20
block|,
literal|30
block|,
literal|40
block|,
literal|40
block|,
literal|80
block|,
literal|80
block|,
literal|120
block|,
literal|200
block|}
decl_stmt|;
DECL|field|FIRST_LEVEL_SIZE
specifier|final
specifier|static
name|int
name|FIRST_LEVEL_SIZE
init|=
name|levelSizeArray
index|[
literal|0
index|]
decl_stmt|;
DECL|method|allocSlice
specifier|public
name|int
name|allocSlice
parameter_list|(
specifier|final
name|byte
index|[]
name|slice
parameter_list|,
specifier|final
name|int
name|upto
parameter_list|)
block|{
specifier|final
name|int
name|level
init|=
name|slice
index|[
name|upto
index|]
operator|&
literal|15
decl_stmt|;
specifier|final
name|int
name|newLevel
init|=
name|nextLevelArray
index|[
name|level
index|]
decl_stmt|;
specifier|final
name|int
name|newSize
init|=
name|levelSizeArray
index|[
name|newLevel
index|]
decl_stmt|;
comment|// Maybe allocate another block
if|if
condition|(
name|byteUpto
operator|>
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_SIZE
operator|-
name|newSize
condition|)
name|nextBuffer
argument_list|()
expr_stmt|;
specifier|final
name|int
name|newUpto
init|=
name|byteUpto
decl_stmt|;
specifier|final
name|int
name|offset
init|=
name|newUpto
operator|+
name|byteOffset
decl_stmt|;
name|byteUpto
operator|+=
name|newSize
expr_stmt|;
comment|// Copy forward the past 3 bytes (which we are about
comment|// to overwrite with the forwarding address):
name|buffer
index|[
name|newUpto
index|]
operator|=
name|slice
index|[
name|upto
operator|-
literal|3
index|]
expr_stmt|;
name|buffer
index|[
name|newUpto
operator|+
literal|1
index|]
operator|=
name|slice
index|[
name|upto
operator|-
literal|2
index|]
expr_stmt|;
name|buffer
index|[
name|newUpto
operator|+
literal|2
index|]
operator|=
name|slice
index|[
name|upto
operator|-
literal|1
index|]
expr_stmt|;
comment|// Write forwarding address at end of last slice:
name|slice
index|[
name|upto
operator|-
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>>
literal|24
argument_list|)
expr_stmt|;
name|slice
index|[
name|upto
operator|-
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>>
literal|16
argument_list|)
expr_stmt|;
name|slice
index|[
name|upto
operator|-
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>>
literal|8
argument_list|)
expr_stmt|;
name|slice
index|[
name|upto
index|]
operator|=
operator|(
name|byte
operator|)
name|offset
expr_stmt|;
comment|// Write new level:
name|buffer
index|[
name|byteUpto
operator|-
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|16
operator||
name|newLevel
argument_list|)
expr_stmt|;
return|return
name|newUpto
operator|+
literal|3
return|;
block|}
block|}
end_class
end_unit
