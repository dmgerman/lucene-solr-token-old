begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|EOFException
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
name|nio
operator|.
name|BufferUnderflowException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|util
operator|.
name|WeakIdentityMap
import|;
end_import
begin_comment
comment|/**  * Base IndexInput implementation that uses an array  * of ByteBuffers to represent a file.  *<p>  * Because Java's ByteBuffer uses an int to address the  * values, it's necessary to access a file greater  * Integer.MAX_VALUE in size using multiple byte buffers.  *<p>  * For efficiency, this class requires that the buffers  * are a power-of-two (<code>chunkSizePower</code>).  */
end_comment
begin_class
DECL|class|ByteBufferIndexInput
specifier|abstract
class|class
name|ByteBufferIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|buffers
specifier|private
name|ByteBuffer
index|[]
name|buffers
decl_stmt|;
DECL|field|chunkSizeMask
specifier|private
specifier|final
name|long
name|chunkSizeMask
decl_stmt|;
DECL|field|chunkSizePower
specifier|private
specifier|final
name|int
name|chunkSizePower
decl_stmt|;
DECL|field|offset
specifier|private
name|int
name|offset
decl_stmt|;
DECL|field|length
specifier|private
name|long
name|length
decl_stmt|;
DECL|field|sliceDescription
specifier|private
name|String
name|sliceDescription
decl_stmt|;
DECL|field|curBufIndex
specifier|private
name|int
name|curBufIndex
decl_stmt|;
DECL|field|curBuf
specifier|private
name|ByteBuffer
name|curBuf
decl_stmt|;
comment|// redundant for speed: buffers[curBufIndex]
DECL|field|isClone
specifier|private
name|boolean
name|isClone
init|=
literal|false
decl_stmt|;
DECL|field|clones
specifier|private
specifier|final
name|WeakIdentityMap
argument_list|<
name|ByteBufferIndexInput
argument_list|,
name|Boolean
argument_list|>
name|clones
init|=
name|WeakIdentityMap
operator|.
name|newConcurrentHashMap
argument_list|()
decl_stmt|;
DECL|method|ByteBufferIndexInput
name|ByteBufferIndexInput
parameter_list|(
name|String
name|resourceDescription
parameter_list|,
name|ByteBuffer
index|[]
name|buffers
parameter_list|,
name|long
name|length
parameter_list|,
name|int
name|chunkSizePower
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|resourceDescription
argument_list|)
expr_stmt|;
name|this
operator|.
name|buffers
operator|=
name|buffers
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|chunkSizePower
operator|=
name|chunkSizePower
expr_stmt|;
name|this
operator|.
name|chunkSizeMask
operator|=
operator|(
literal|1L
operator|<<
name|chunkSizePower
operator|)
operator|-
literal|1L
expr_stmt|;
assert|assert
name|chunkSizePower
operator|>=
literal|0
operator|&&
name|chunkSizePower
operator|<=
literal|30
assert|;
assert|assert
operator|(
name|length
operator|>>>
name|chunkSizePower
operator|)
operator|<
name|Integer
operator|.
name|MAX_VALUE
assert|;
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
specifier|final
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|curBuf
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|e
parameter_list|)
block|{
do|do
block|{
name|curBufIndex
operator|++
expr_stmt|;
if|if
condition|(
name|curBufIndex
operator|>=
name|buffers
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"read past EOF: "
operator|+
name|this
argument_list|)
throw|;
block|}
name|curBuf
operator|=
name|buffers
index|[
name|curBufIndex
index|]
expr_stmt|;
name|curBuf
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|curBuf
operator|.
name|hasRemaining
argument_list|()
condition|)
do|;
return|return
name|curBuf
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Already closed: "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|public
specifier|final
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|curBuf
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|e
parameter_list|)
block|{
name|int
name|curAvail
init|=
name|curBuf
operator|.
name|remaining
argument_list|()
decl_stmt|;
while|while
condition|(
name|len
operator|>
name|curAvail
condition|)
block|{
name|curBuf
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|curAvail
argument_list|)
expr_stmt|;
name|len
operator|-=
name|curAvail
expr_stmt|;
name|offset
operator|+=
name|curAvail
expr_stmt|;
name|curBufIndex
operator|++
expr_stmt|;
if|if
condition|(
name|curBufIndex
operator|>=
name|buffers
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"read past EOF: "
operator|+
name|this
argument_list|)
throw|;
block|}
name|curBuf
operator|=
name|buffers
index|[
name|curBufIndex
index|]
expr_stmt|;
name|curBuf
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|curAvail
operator|=
name|curBuf
operator|.
name|remaining
argument_list|()
expr_stmt|;
block|}
name|curBuf
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Already closed: "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|readShort
specifier|public
specifier|final
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|curBuf
operator|.
name|getShort
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|e
parameter_list|)
block|{
return|return
name|super
operator|.
name|readShort
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Already closed: "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|readInt
specifier|public
specifier|final
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|curBuf
operator|.
name|getInt
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|e
parameter_list|)
block|{
return|return
name|super
operator|.
name|readInt
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Already closed: "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|readLong
specifier|public
specifier|final
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|curBuf
operator|.
name|getLong
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|e
parameter_list|)
block|{
return|return
name|super
operator|.
name|readLong
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Already closed: "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
specifier|final
name|long
name|getFilePointer
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
operator|(
operator|(
name|long
operator|)
name|curBufIndex
operator|)
operator|<<
name|chunkSizePower
operator|)
operator|+
name|curBuf
operator|.
name|position
argument_list|()
operator|-
name|offset
return|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Already closed: "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
specifier|final
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
comment|// necessary in case offset != 0 and pos< 0, but pos>= -offset
if|if
condition|(
name|pos
operator|<
literal|0L
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Seeking to negative position: "
operator|+
name|this
argument_list|)
throw|;
block|}
name|pos
operator|+=
name|offset
expr_stmt|;
comment|// we use>> here to preserve negative, so we will catch AIOOBE,
comment|// in case pos + offset overflows.
specifier|final
name|int
name|bi
init|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|>>
name|chunkSizePower
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|ByteBuffer
name|b
init|=
name|buffers
index|[
name|bi
index|]
decl_stmt|;
name|b
operator|.
name|position
argument_list|(
call|(
name|int
call|)
argument_list|(
name|pos
operator|&
name|chunkSizeMask
argument_list|)
argument_list|)
expr_stmt|;
comment|// write values, on exception all is unchanged
name|this
operator|.
name|curBufIndex
operator|=
name|bi
expr_stmt|;
name|this
operator|.
name|curBuf
operator|=
name|b
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|aioobe
parameter_list|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"seek past EOF: "
operator|+
name|this
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"seek past EOF: "
operator|+
name|this
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Already closed: "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
specifier|final
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
specifier|final
name|ByteBufferIndexInput
name|clone
parameter_list|()
block|{
specifier|final
name|ByteBufferIndexInput
name|clone
init|=
name|buildSlice
argument_list|(
literal|0L
argument_list|,
name|this
operator|.
name|length
argument_list|)
decl_stmt|;
try|try
block|{
name|clone
operator|.
name|seek
argument_list|(
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Should never happen: "
operator|+
name|this
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
return|return
name|clone
return|;
block|}
comment|/**    * Creates a slice of this index input, with the given description, offset, and length. The slice is seeked to the beginning.    */
DECL|method|slice
specifier|public
specifier|final
name|ByteBufferIndexInput
name|slice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
block|{
if|if
condition|(
name|isClone
condition|)
block|{
comment|// well we could, but this is stupid
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot slice() "
operator|+
name|sliceDescription
operator|+
literal|" from a cloned IndexInput: "
operator|+
name|this
argument_list|)
throw|;
block|}
specifier|final
name|ByteBufferIndexInput
name|clone
init|=
name|buildSlice
argument_list|(
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|clone
operator|.
name|sliceDescription
operator|=
name|sliceDescription
expr_stmt|;
try|try
block|{
name|clone
operator|.
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Should never happen: "
operator|+
name|this
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
return|return
name|clone
return|;
block|}
DECL|method|buildSlice
specifier|private
name|ByteBufferIndexInput
name|buildSlice
parameter_list|(
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
block|{
if|if
condition|(
name|buffers
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Already closed: "
operator|+
name|this
argument_list|)
throw|;
block|}
if|if
condition|(
name|offset
operator|<
literal|0
operator|||
name|length
argument_list|<
literal|0
operator|||
name|offset
operator|+
name|length
argument_list|>
name|this
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"slice() "
operator|+
name|sliceDescription
operator|+
literal|" out of bounds: offset="
operator|+
name|offset
operator|+
literal|",length="
operator|+
name|length
operator|+
literal|",fileLength="
operator|+
name|this
operator|.
name|length
operator|+
literal|": "
operator|+
name|this
argument_list|)
throw|;
block|}
comment|// include our own offset into the final offset:
name|offset
operator|+=
name|this
operator|.
name|offset
expr_stmt|;
specifier|final
name|ByteBufferIndexInput
name|clone
init|=
operator|(
name|ByteBufferIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|isClone
operator|=
literal|true
expr_stmt|;
comment|// we keep clone.clones, so it shares the same map with original and we have no additional cost on clones
assert|assert
name|clone
operator|.
name|clones
operator|==
name|this
operator|.
name|clones
assert|;
name|clone
operator|.
name|buffers
operator|=
name|buildSlice
argument_list|(
name|buffers
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|clone
operator|.
name|offset
operator|=
call|(
name|int
call|)
argument_list|(
name|offset
operator|&
name|chunkSizeMask
argument_list|)
expr_stmt|;
name|clone
operator|.
name|length
operator|=
name|length
expr_stmt|;
comment|// register the new clone in our clone list to clean it up on closing:
name|this
operator|.
name|clones
operator|.
name|put
argument_list|(
name|clone
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/** Returns a sliced view from a set of already-existing buffers:     *  the last buffer's limit() will be correct, but    *  you must deal with offset separately (the first buffer will not be adjusted) */
DECL|method|buildSlice
specifier|private
name|ByteBuffer
index|[]
name|buildSlice
parameter_list|(
name|ByteBuffer
index|[]
name|buffers
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
block|{
specifier|final
name|long
name|sliceEnd
init|=
name|offset
operator|+
name|length
decl_stmt|;
specifier|final
name|int
name|startIndex
init|=
call|(
name|int
call|)
argument_list|(
name|offset
operator|>>>
name|chunkSizePower
argument_list|)
decl_stmt|;
specifier|final
name|int
name|endIndex
init|=
call|(
name|int
call|)
argument_list|(
name|sliceEnd
operator|>>>
name|chunkSizePower
argument_list|)
decl_stmt|;
comment|// we always allocate one more slice, the last one may be a 0 byte one
specifier|final
name|ByteBuffer
name|slices
index|[]
init|=
operator|new
name|ByteBuffer
index|[
name|endIndex
operator|-
name|startIndex
operator|+
literal|1
index|]
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
name|slices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|slices
index|[
name|i
index|]
operator|=
name|buffers
index|[
name|startIndex
operator|+
name|i
index|]
operator|.
name|duplicate
argument_list|()
expr_stmt|;
block|}
comment|// set the last buffer's limit for the sliced view.
name|slices
index|[
name|slices
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|limit
argument_list|(
call|(
name|int
call|)
argument_list|(
name|sliceEnd
operator|&
name|chunkSizeMask
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|slices
return|;
block|}
DECL|method|unsetBuffers
specifier|private
name|void
name|unsetBuffers
parameter_list|()
block|{
name|buffers
operator|=
literal|null
expr_stmt|;
name|curBuf
operator|=
literal|null
expr_stmt|;
name|curBufIndex
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|buffers
operator|==
literal|null
condition|)
return|return;
comment|// make local copy, then un-set early
specifier|final
name|ByteBuffer
index|[]
name|bufs
init|=
name|buffers
decl_stmt|;
name|unsetBuffers
argument_list|()
expr_stmt|;
name|clones
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|isClone
condition|)
return|return;
comment|// for extra safety unset also all clones' buffers:
for|for
control|(
name|Iterator
argument_list|<
name|ByteBufferIndexInput
argument_list|>
name|it
init|=
name|this
operator|.
name|clones
operator|.
name|keyIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|ByteBufferIndexInput
name|clone
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
assert|assert
name|clone
operator|.
name|isClone
assert|;
name|clone
operator|.
name|unsetBuffers
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|clones
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|ByteBuffer
name|b
range|:
name|bufs
control|)
block|{
name|freeBuffer
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|unsetBuffers
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Called when the contents of a buffer will be no longer needed.    */
DECL|method|freeBuffer
specifier|protected
specifier|abstract
name|void
name|freeBuffer
parameter_list|(
name|ByteBuffer
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|sliceDescription
operator|!=
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|" [slice="
operator|+
name|sliceDescription
operator|+
literal|"]"
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
