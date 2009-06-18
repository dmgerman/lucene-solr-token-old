begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|utils
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
name|Reader
import|;
end_import
begin_comment
comment|/**  * Implements a {@link Reader} over a {@link StringBuffer} instance. Although  * one can use {@link java.io.StringReader} by passing it  * {@link StringBuffer#toString()}, it is better to use this class, as it  * doesn't mark the passed-in {@link StringBuffer} as shared (which will cause  * inner char[] allocations at the next append() attempt).<br>  * Notes:  *<ul>  *<li>This implementation assumes the underlying {@link StringBuffer} is not  * changed during the use of this {@link Reader} implementation.  *<li>This implementation is thread-safe.  *<li>The implementation looks very much like {@link java.io.StringReader} (for  * the right reasons).  *<li>If one wants to reuse that instance, then the following needs to be done:  *<pre>  * StringBuffer sb = new StringBuffer("some text");  * Reader reader = new StringBufferReader(sb);  * ... read from reader - dont close it ! ...  * sb.setLength(0);  * sb.append("some new text");  * reader.reset();  * ... read the new string from the reader ...  *</pre>  *</ul>  */
end_comment
begin_class
DECL|class|StringBufferReader
specifier|public
class|class
name|StringBufferReader
extends|extends
name|Reader
block|{
comment|// TODO (3.0): change to StringBuffer (including the name of the class)
comment|// The StringBuffer to read from.
DECL|field|sb
specifier|private
name|StringBuffer
name|sb
decl_stmt|;
comment|// The length of 'sb'.
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
comment|// The next position to read from the StringBuffer.
DECL|field|next
specifier|private
name|int
name|next
init|=
literal|0
decl_stmt|;
comment|// The mark position. The default value 0 means the start of the text.
DECL|field|mark
specifier|private
name|int
name|mark
init|=
literal|0
decl_stmt|;
DECL|method|StringBufferReader
specifier|public
name|StringBufferReader
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
name|set
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
comment|/** Check to make sure that the stream has not been closed. */
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|sb
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Stream has already been closed"
argument_list|)
throw|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|sb
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Mark the present position in the stream. Subsequent calls to reset() will    * reposition the stream to this point.    *     * @param readAheadLimit Limit on the number of characters that may be read    *        while still preserving the mark. Because the stream's input comes    *        from a StringBuffer, there is no actual limit, so this argument     *        must not be negative, but is otherwise ignored.    * @exception IllegalArgumentException If readAheadLimit is< 0    * @exception IOException If an I/O error occurs    */
DECL|method|mark
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readAheadLimit
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|readAheadLimit
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Read-ahead limit cannpt be negative: "
operator|+
name|readAheadLimit
argument_list|)
throw|;
block|}
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|mark
operator|=
name|next
expr_stmt|;
block|}
block|}
DECL|method|markSupported
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|next
operator|>=
name|length
condition|?
operator|-
literal|1
else|:
name|sb
operator|.
name|charAt
argument_list|(
name|next
operator|++
argument_list|)
return|;
block|}
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
name|cbuf
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// Validate parameters
if|if
condition|(
name|off
argument_list|<
literal|0
operator|||
name|off
argument_list|>
name|cbuf
operator|.
name|length
operator|||
name|len
argument_list|<
literal|0
operator|||
name|off
operator|+
name|len
argument_list|>
name|cbuf
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"off="
operator|+
name|off
operator|+
literal|" len="
operator|+
name|len
operator|+
literal|" cbuf.length="
operator|+
name|cbuf
operator|.
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|next
operator|>=
name|length
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|length
operator|-
name|next
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|sb
operator|.
name|getChars
argument_list|(
name|next
argument_list|,
name|next
operator|+
name|n
argument_list|,
name|cbuf
argument_list|,
name|off
argument_list|)
expr_stmt|;
name|next
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
block|}
DECL|method|ready
specifier|public
name|boolean
name|ready
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|next
operator|=
name|mark
expr_stmt|;
name|length
operator|=
name|sb
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|StringBuffer
name|sb
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|this
operator|.
name|sb
operator|=
name|sb
expr_stmt|;
name|length
operator|=
name|sb
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|skip
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|ns
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|>=
name|length
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// Bound skip by beginning and end of the source
name|long
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|length
operator|-
name|next
argument_list|,
name|ns
argument_list|)
decl_stmt|;
name|n
operator|=
name|Math
operator|.
name|max
argument_list|(
operator|-
name|next
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|next
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
block|}
block|}
end_class
end_unit
