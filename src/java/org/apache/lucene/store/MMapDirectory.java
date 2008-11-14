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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
operator|.
name|MapMode
import|;
end_import
begin_comment
comment|/** File-based {@link Directory} implementation that uses mmap for input.  *  *<p>To use this, invoke Java with the System property  * org.apache.lucene.FSDirectory.class set to  * org.apache.lucene.store.MMapDirectory.  This will cause {@link  * FSDirectory#getDirectory(File,boolean)} to return instances of this class.  */
end_comment
begin_class
DECL|class|MMapDirectory
specifier|public
class|class
name|MMapDirectory
extends|extends
name|FSDirectory
block|{
comment|/** Create a new MMapDirectory for the named location.    * @param path the path of the directory    * @param lockFactory the lock factory to use, or null for the default.    * @throws IOException    */
DECL|method|MMapDirectory
specifier|public
name|MMapDirectory
parameter_list|(
name|File
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
expr_stmt|;
block|}
comment|// back compatibility so FSDirectory can instantiate via reflection
DECL|method|MMapDirectory
specifier|protected
name|MMapDirectory
parameter_list|()
throws|throws
name|IOException
block|{   }
DECL|class|MMapIndexInput
specifier|private
specifier|static
class|class
name|MMapIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|buffer
specifier|private
name|ByteBuffer
name|buffer
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|method|MMapIndexInput
specifier|private
name|MMapIndexInput
parameter_list|(
name|RandomAccessFile
name|raf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|length
operator|=
name|raf
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|raf
operator|.
name|getChannel
argument_list|()
operator|.
name|map
argument_list|(
name|MapMode
operator|.
name|READ_ONLY
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|buffer
operator|.
name|get
argument_list|()
return|;
block|}
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
name|buffer
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
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|position
argument_list|()
return|;
block|}
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
name|buffer
operator|.
name|position
argument_list|(
operator|(
name|int
operator|)
name|pos
argument_list|)
expr_stmt|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|MMapIndexInput
name|clone
init|=
operator|(
name|MMapIndexInput
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
name|buffer
operator|.
name|duplicate
argument_list|()
expr_stmt|;
return|return
name|clone
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
DECL|class|MultiMMapIndexInput
specifier|private
specifier|static
class|class
name|MultiMMapIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|buffers
specifier|private
name|ByteBuffer
index|[]
name|buffers
decl_stmt|;
DECL|field|bufSizes
specifier|private
name|int
index|[]
name|bufSizes
decl_stmt|;
comment|// keep here, ByteBuffer.size() method is optional
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|curBufIndex
specifier|private
name|int
name|curBufIndex
decl_stmt|;
DECL|field|maxBufSize
specifier|private
specifier|final
name|int
name|maxBufSize
decl_stmt|;
DECL|field|curBuf
specifier|private
name|ByteBuffer
name|curBuf
decl_stmt|;
comment|// redundant for speed: buffers[curBufIndex]
DECL|field|curAvail
specifier|private
name|int
name|curAvail
decl_stmt|;
comment|// redundant for speed: (bufSizes[curBufIndex] - curBuf.position())
DECL|method|MultiMMapIndexInput
specifier|public
name|MultiMMapIndexInput
parameter_list|(
name|RandomAccessFile
name|raf
parameter_list|,
name|int
name|maxBufSize
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|length
operator|=
name|raf
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxBufSize
operator|=
name|maxBufSize
expr_stmt|;
if|if
condition|(
name|maxBufSize
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Non positive maxBufSize: "
operator|+
name|maxBufSize
argument_list|)
throw|;
if|if
condition|(
operator|(
name|length
operator|/
name|maxBufSize
operator|)
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"RandomAccessFile too big for maximum buffer size: "
operator|+
name|raf
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
name|int
name|nrBuffers
init|=
call|(
name|int
call|)
argument_list|(
name|length
operator|/
name|maxBufSize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|nrBuffers
operator|*
name|maxBufSize
operator|)
operator|<
name|length
condition|)
name|nrBuffers
operator|++
expr_stmt|;
name|this
operator|.
name|buffers
operator|=
operator|new
name|ByteBuffer
index|[
name|nrBuffers
index|]
expr_stmt|;
name|this
operator|.
name|bufSizes
operator|=
operator|new
name|int
index|[
name|nrBuffers
index|]
expr_stmt|;
name|long
name|bufferStart
init|=
literal|0
decl_stmt|;
name|FileChannel
name|rafc
init|=
name|raf
operator|.
name|getChannel
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|bufNr
init|=
literal|0
init|;
name|bufNr
operator|<
name|nrBuffers
condition|;
name|bufNr
operator|++
control|)
block|{
name|int
name|bufSize
init|=
operator|(
name|length
operator|>
operator|(
name|bufferStart
operator|+
name|maxBufSize
operator|)
operator|)
condition|?
name|maxBufSize
else|:
call|(
name|int
call|)
argument_list|(
name|length
operator|-
name|bufferStart
argument_list|)
decl_stmt|;
name|this
operator|.
name|buffers
index|[
name|bufNr
index|]
operator|=
name|rafc
operator|.
name|map
argument_list|(
name|MapMode
operator|.
name|READ_ONLY
argument_list|,
name|bufferStart
argument_list|,
name|bufSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|bufSizes
index|[
name|bufNr
index|]
operator|=
name|bufSize
expr_stmt|;
name|bufferStart
operator|+=
name|bufSize
expr_stmt|;
block|}
name|seek
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Performance might be improved by reading ahead into an array of
comment|// eg. 128 bytes and readByte() from there.
if|if
condition|(
name|curAvail
operator|==
literal|0
condition|)
block|{
name|curBufIndex
operator|++
expr_stmt|;
name|curBuf
operator|=
name|buffers
index|[
name|curBufIndex
index|]
expr_stmt|;
comment|// index out of bounds when too many bytes requested
name|curBuf
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|curAvail
operator|=
name|bufSizes
index|[
name|curBufIndex
index|]
expr_stmt|;
block|}
name|curAvail
operator|--
expr_stmt|;
return|return
name|curBuf
operator|.
name|get
argument_list|()
return|;
block|}
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
name|curBuf
operator|=
name|buffers
index|[
name|curBufIndex
index|]
expr_stmt|;
comment|// index out of bounds when too many bytes requested
name|curBuf
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|curAvail
operator|=
name|bufSizes
index|[
name|curBufIndex
index|]
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
name|curAvail
operator|-=
name|len
expr_stmt|;
block|}
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
operator|(
name|curBufIndex
operator|*
operator|(
name|long
operator|)
name|maxBufSize
operator|)
operator|+
name|curBuf
operator|.
name|position
argument_list|()
return|;
block|}
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
name|curBufIndex
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|/
name|maxBufSize
argument_list|)
expr_stmt|;
name|curBuf
operator|=
name|buffers
index|[
name|curBufIndex
index|]
expr_stmt|;
name|int
name|bufOffset
init|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|-
operator|(
name|curBufIndex
operator|*
name|maxBufSize
operator|)
argument_list|)
decl_stmt|;
name|curBuf
operator|.
name|position
argument_list|(
name|bufOffset
argument_list|)
expr_stmt|;
name|curAvail
operator|=
name|bufSizes
index|[
name|curBufIndex
index|]
operator|-
name|bufOffset
expr_stmt|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|MultiMMapIndexInput
name|clone
init|=
operator|(
name|MultiMMapIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|buffers
operator|=
operator|new
name|ByteBuffer
index|[
name|buffers
operator|.
name|length
index|]
expr_stmt|;
comment|// No need to clone bufSizes.
comment|// Since most clones will use only one buffer, duplicate() could also be
comment|// done lazy in clones, eg. when adapting curBuf.
for|for
control|(
name|int
name|bufNr
init|=
literal|0
init|;
name|bufNr
operator|<
name|buffers
operator|.
name|length
condition|;
name|bufNr
operator|++
control|)
block|{
name|clone
operator|.
name|buffers
index|[
name|bufNr
index|]
operator|=
name|buffers
index|[
name|bufNr
index|]
operator|.
name|duplicate
argument_list|()
expr_stmt|;
block|}
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
name|RuntimeException
name|newException
init|=
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
decl_stmt|;
name|newException
operator|.
name|initCause
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
throw|throw
name|newException
throw|;
block|}
empty_stmt|;
return|return
name|clone
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
DECL|field|MAX_BBUF
specifier|private
specifier|final
name|int
name|MAX_BBUF
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|getFile
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|f
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|(
name|raf
operator|.
name|length
argument_list|()
operator|<=
name|MAX_BBUF
operator|)
condition|?
operator|(
name|IndexInput
operator|)
operator|new
name|MMapIndexInput
argument_list|(
name|raf
argument_list|)
else|:
operator|(
name|IndexInput
operator|)
operator|new
name|MultiMMapIndexInput
argument_list|(
name|raf
argument_list|,
name|MAX_BBUF
argument_list|)
return|;
block|}
finally|finally
block|{
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|openInput
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class
end_unit
