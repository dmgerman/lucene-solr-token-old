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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_comment
comment|/**  * A memory-resident {@link IndexOutput} implementation.  *  * @version $Id$  */
end_comment
begin_class
DECL|class|RAMOutputStream
specifier|public
class|class
name|RAMOutputStream
extends|extends
name|BufferedIndexOutput
block|{
DECL|field|file
specifier|private
name|RAMFile
name|file
decl_stmt|;
DECL|field|pointer
specifier|private
name|long
name|pointer
init|=
literal|0
decl_stmt|;
comment|/** Construct an empty output buffer. */
DECL|method|RAMOutputStream
specifier|public
name|RAMOutputStream
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|RAMFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|RAMOutputStream
name|RAMOutputStream
parameter_list|(
name|RAMFile
name|f
parameter_list|)
block|{
name|file
operator|=
name|f
expr_stmt|;
block|}
comment|/** Copy the current contents of this buffer to the named output. */
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
specifier|final
name|long
name|end
init|=
name|file
operator|.
name|length
decl_stmt|;
name|long
name|pos
init|=
literal|0
decl_stmt|;
name|int
name|buffer
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|end
condition|)
block|{
name|int
name|length
init|=
name|BUFFER_SIZE
decl_stmt|;
name|long
name|nextPos
init|=
name|pos
operator|+
name|length
decl_stmt|;
if|if
condition|(
name|nextPos
operator|>
name|end
condition|)
block|{
comment|// at the last buffer
name|length
operator|=
call|(
name|int
call|)
argument_list|(
name|end
operator|-
name|pos
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBytes
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|file
operator|.
name|buffers
operator|.
name|elementAt
argument_list|(
name|buffer
operator|++
argument_list|)
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|pos
operator|=
name|nextPos
expr_stmt|;
block|}
block|}
comment|/** Resets this to an empty buffer. */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
try|try
block|{
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// should never happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|file
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|flushBuffer
specifier|public
name|void
name|flushBuffer
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|byte
index|[]
name|buffer
decl_stmt|;
name|int
name|bufferPos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|bufferPos
operator|!=
name|len
condition|)
block|{
name|int
name|bufferNumber
init|=
call|(
name|int
call|)
argument_list|(
name|pointer
operator|/
name|BUFFER_SIZE
argument_list|)
decl_stmt|;
name|int
name|bufferOffset
init|=
call|(
name|int
call|)
argument_list|(
name|pointer
operator|%
name|BUFFER_SIZE
argument_list|)
decl_stmt|;
name|int
name|bytesInBuffer
init|=
name|BUFFER_SIZE
operator|-
name|bufferOffset
decl_stmt|;
name|int
name|remainInSrcBuffer
init|=
name|len
operator|-
name|bufferPos
decl_stmt|;
name|int
name|bytesToCopy
init|=
name|bytesInBuffer
operator|>=
name|remainInSrcBuffer
condition|?
name|remainInSrcBuffer
else|:
name|bytesInBuffer
decl_stmt|;
if|if
condition|(
name|bufferNumber
operator|==
name|file
operator|.
name|buffers
operator|.
name|size
argument_list|()
condition|)
block|{
name|buffer
operator|=
operator|new
name|byte
index|[
name|BUFFER_SIZE
index|]
expr_stmt|;
name|file
operator|.
name|buffers
operator|.
name|addElement
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|=
operator|(
name|byte
index|[]
operator|)
name|file
operator|.
name|buffers
operator|.
name|elementAt
argument_list|(
name|bufferNumber
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|src
argument_list|,
name|bufferPos
argument_list|,
name|buffer
argument_list|,
name|bufferOffset
argument_list|,
name|bytesToCopy
argument_list|)
expr_stmt|;
name|bufferPos
operator|+=
name|bytesToCopy
expr_stmt|;
name|pointer
operator|+=
name|bytesToCopy
expr_stmt|;
block|}
if|if
condition|(
name|pointer
operator|>
name|file
operator|.
name|length
condition|)
name|file
operator|.
name|length
operator|=
name|pointer
expr_stmt|;
name|file
operator|.
name|lastModified
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|super
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|pointer
operator|=
name|pos
expr_stmt|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|file
operator|.
name|length
return|;
block|}
block|}
end_class
end_unit
