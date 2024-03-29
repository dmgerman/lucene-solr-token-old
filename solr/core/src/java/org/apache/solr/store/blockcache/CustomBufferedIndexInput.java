begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
package|;
end_package
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|BufferedIndexInput
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
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|CustomBufferedIndexInput
specifier|public
specifier|abstract
class|class
name|CustomBufferedIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|BUFFER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|32768
decl_stmt|;
DECL|field|bufferSize
specifier|private
name|int
name|bufferSize
init|=
name|BUFFER_SIZE
decl_stmt|;
DECL|field|buffer
specifier|protected
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|bufferStart
specifier|private
name|long
name|bufferStart
init|=
literal|0
decl_stmt|;
comment|// position in file of buffer
DECL|field|bufferLength
specifier|private
name|int
name|bufferLength
init|=
literal|0
decl_stmt|;
comment|// end of valid bytes
DECL|field|bufferPosition
specifier|private
name|int
name|bufferPosition
init|=
literal|0
decl_stmt|;
comment|// next byte to read
DECL|field|store
specifier|private
name|Store
name|store
decl_stmt|;
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bufferPosition
operator|>=
name|bufferLength
condition|)
name|refill
argument_list|()
expr_stmt|;
return|return
name|buffer
index|[
name|bufferPosition
operator|++
index|]
return|;
block|}
DECL|method|CustomBufferedIndexInput
specifier|public
name|CustomBufferedIndexInput
parameter_list|(
name|String
name|resourceDesc
parameter_list|)
block|{
name|this
argument_list|(
name|resourceDesc
argument_list|,
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|CustomBufferedIndexInput
specifier|public
name|CustomBufferedIndexInput
parameter_list|(
name|String
name|resourceDesc
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|resourceDesc
argument_list|)
expr_stmt|;
name|checkBufferSize
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|BufferStore
operator|.
name|instance
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
block|}
DECL|method|checkBufferSize
specifier|private
name|void
name|checkBufferSize
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
if|if
condition|(
name|bufferSize
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"bufferSize must be greater than 0 (got "
operator|+
name|bufferSize
operator|+
literal|")"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|public
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
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|public
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
parameter_list|,
name|boolean
name|useBuffer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|len
operator|<=
operator|(
name|bufferLength
operator|-
name|bufferPosition
operator|)
condition|)
block|{
comment|// the buffer contains enough data to satisfy this request
if|if
condition|(
name|len
operator|>
literal|0
condition|)
comment|// to allow b to be null if len is 0...
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|bufferPosition
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bufferPosition
operator|+=
name|len
expr_stmt|;
block|}
else|else
block|{
comment|// the buffer does not have enough data. First serve all we've got.
name|int
name|available
init|=
name|bufferLength
operator|-
name|bufferPosition
decl_stmt|;
if|if
condition|(
name|available
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|bufferPosition
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|available
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|available
expr_stmt|;
name|len
operator|-=
name|available
expr_stmt|;
name|bufferPosition
operator|+=
name|available
expr_stmt|;
block|}
comment|// and now, read the remaining 'len' bytes:
if|if
condition|(
name|useBuffer
operator|&&
name|len
operator|<
name|bufferSize
condition|)
block|{
comment|// If the amount left to read is small enough, and
comment|// we are allowed to use our buffer, do it in the usual
comment|// buffered way: fill the buffer and copy from it:
name|refill
argument_list|()
expr_stmt|;
if|if
condition|(
name|bufferLength
operator|<
name|len
condition|)
block|{
comment|// Throw an exception when refill() could not read len bytes:
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|bufferLength
argument_list|)
expr_stmt|;
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
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bufferPosition
operator|=
name|len
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// The amount left to read is larger than the buffer
comment|// or we've been asked to not use our buffer -
comment|// there's no performance reason not to read it all
comment|// at once. Note that unlike the previous code of
comment|// this function, there is no need to do a seek
comment|// here, because there's no need to reread what we
comment|// had in the buffer.
name|long
name|after
init|=
name|bufferStart
operator|+
name|bufferPosition
operator|+
name|len
decl_stmt|;
if|if
condition|(
name|after
operator|>
name|length
argument_list|()
condition|)
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"read past EOF: "
operator|+
name|this
argument_list|)
throw|;
name|readInternal
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bufferStart
operator|=
name|after
expr_stmt|;
name|bufferPosition
operator|=
literal|0
expr_stmt|;
name|bufferLength
operator|=
literal|0
expr_stmt|;
comment|// trigger refill() on read
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|readInt
specifier|public
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
literal|4
operator|<=
operator|(
name|bufferLength
operator|-
name|bufferPosition
operator|)
condition|)
block|{
return|return
operator|(
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xFF
operator|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|readInt
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|readLong
specifier|public
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
literal|8
operator|<=
operator|(
name|bufferLength
operator|-
name|bufferPosition
operator|)
condition|)
block|{
specifier|final
name|int
name|i1
init|=
operator|(
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
specifier|final
name|int
name|i2
init|=
operator|(
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|buffer
index|[
name|bufferPosition
operator|++
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
return|return
operator|(
operator|(
operator|(
name|long
operator|)
name|i1
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
name|i2
operator|&
literal|0xFFFFFFFFL
operator|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|readLong
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|readVInt
specifier|public
name|int
name|readVInt
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
literal|5
operator|<=
operator|(
name|bufferLength
operator|-
name|bufferPosition
operator|)
condition|)
block|{
name|byte
name|b
init|=
name|buffer
index|[
name|bufferPosition
operator|++
index|]
decl_stmt|;
name|int
name|i
init|=
name|b
operator|&
literal|0x7F
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
name|b
operator|=
name|buffer
index|[
name|bufferPosition
operator|++
index|]
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
name|shift
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|readVInt
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|readVLong
specifier|public
name|long
name|readVLong
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
literal|9
operator|<=
name|bufferLength
operator|-
name|bufferPosition
condition|)
block|{
name|byte
name|b
init|=
name|buffer
index|[
name|bufferPosition
operator|++
index|]
decl_stmt|;
name|long
name|i
init|=
name|b
operator|&
literal|0x7F
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
name|b
operator|=
name|buffer
index|[
name|bufferPosition
operator|++
index|]
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
name|shift
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|readVLong
argument_list|()
return|;
block|}
block|}
DECL|method|refill
specifier|private
name|void
name|refill
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|start
init|=
name|bufferStart
operator|+
name|bufferPosition
decl_stmt|;
name|long
name|end
init|=
name|start
operator|+
name|bufferSize
decl_stmt|;
if|if
condition|(
name|end
operator|>
name|length
argument_list|()
condition|)
comment|// don't read past EOF
name|end
operator|=
name|length
argument_list|()
expr_stmt|;
name|int
name|newLength
init|=
call|(
name|int
call|)
argument_list|(
name|end
operator|-
name|start
argument_list|)
decl_stmt|;
if|if
condition|(
name|newLength
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"read past EOF: "
operator|+
name|this
argument_list|)
throw|;
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
name|buffer
operator|=
name|store
operator|.
name|takeBuffer
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|seekInternal
argument_list|(
name|bufferStart
argument_list|)
expr_stmt|;
block|}
name|readInternal
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
name|bufferLength
operator|=
name|newLength
expr_stmt|;
name|bufferStart
operator|=
name|start
expr_stmt|;
name|bufferPosition
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
name|closeInternal
argument_list|()
expr_stmt|;
name|store
operator|.
name|putBuffer
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|closeInternal
specifier|protected
specifier|abstract
name|void
name|closeInternal
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Expert: implements buffer refill. Reads bytes from the current position in    * the input.    *     * @param b    *          the array to read bytes into    * @param offset    *          the offset in the array to start storing bytes    * @param length    *          the number of bytes to read    */
DECL|method|readInternal
specifier|protected
specifier|abstract
name|void
name|readInternal
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|bufferStart
operator|+
name|bufferPosition
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>=
name|bufferStart
operator|&&
name|pos
operator|<
operator|(
name|bufferStart
operator|+
name|bufferLength
operator|)
condition|)
name|bufferPosition
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|-
name|bufferStart
argument_list|)
expr_stmt|;
comment|// seek
comment|// within
comment|// buffer
else|else
block|{
name|bufferStart
operator|=
name|pos
expr_stmt|;
name|bufferPosition
operator|=
literal|0
expr_stmt|;
name|bufferLength
operator|=
literal|0
expr_stmt|;
comment|// trigger refill() on read()
name|seekInternal
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Expert: implements seek. Sets current position in this file, where the next    * {@link #readInternal(byte[],int,int)} will occur.    *     * @see #readInternal(byte[],int,int)    */
DECL|method|seekInternal
specifier|protected
specifier|abstract
name|void
name|seekInternal
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|clone
specifier|public
name|IndexInput
name|clone
parameter_list|()
block|{
name|CustomBufferedIndexInput
name|clone
init|=
operator|(
name|CustomBufferedIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|buffer
operator|=
literal|null
expr_stmt|;
name|clone
operator|.
name|bufferLength
operator|=
literal|0
expr_stmt|;
name|clone
operator|.
name|bufferPosition
operator|=
literal|0
expr_stmt|;
name|clone
operator|.
name|bufferStart
operator|=
name|getFilePointer
argument_list|()
expr_stmt|;
return|return
name|clone
return|;
block|}
annotation|@
name|Override
DECL|method|slice
specifier|public
name|IndexInput
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
throws|throws
name|IOException
block|{
return|return
name|BufferedIndexInput
operator|.
name|wrap
argument_list|(
name|sliceDescription
argument_list|,
name|this
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * Flushes the in-memory bufer to the given output, copying at most    *<code>numBytes</code>.    *<p>    *<b>NOTE:</b> this method does not refill the buffer, however it does    * advance the buffer position.    *     * @return the number of bytes actually flushed from the in-memory buffer.    */
DECL|method|flushBuffer
specifier|protected
name|int
name|flushBuffer
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|long
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|toCopy
init|=
name|bufferLength
operator|-
name|bufferPosition
decl_stmt|;
if|if
condition|(
name|toCopy
operator|>
name|numBytes
condition|)
block|{
name|toCopy
operator|=
operator|(
name|int
operator|)
name|numBytes
expr_stmt|;
block|}
if|if
condition|(
name|toCopy
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|writeBytes
argument_list|(
name|buffer
argument_list|,
name|bufferPosition
argument_list|,
name|toCopy
argument_list|)
expr_stmt|;
name|bufferPosition
operator|+=
name|toCopy
expr_stmt|;
block|}
return|return
name|toCopy
return|;
block|}
block|}
end_class
end_unit
