begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
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
name|Serializable
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
name|AttributeImpl
import|;
end_import
begin_comment
comment|/**  * The term text of a Token.  *   *<p><font color="#FF0000">  * WARNING: The status of the new TokenStream, AttributeSource and Attributes is experimental.   * The APIs introduced in these classes with Lucene 2.9 might change in the future.   * We will make our best efforts to keep the APIs backwards-compatible.</font>  */
end_comment
begin_class
DECL|class|TermAttributeImpl
specifier|public
class|class
name|TermAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|TermAttribute
implements|,
name|Cloneable
implements|,
name|Serializable
block|{
DECL|field|MIN_BUFFER_SIZE
specifier|private
specifier|static
name|int
name|MIN_BUFFER_SIZE
init|=
literal|10
decl_stmt|;
DECL|field|termBuffer
specifier|private
name|char
index|[]
name|termBuffer
decl_stmt|;
DECL|field|termLength
specifier|private
name|int
name|termLength
decl_stmt|;
comment|/** Returns the Token's term text.    *     * This method has a performance penalty    * because the text is stored internally in a char[].  If    * possible, use {@link #termBuffer()} and {@link    * #termLength()} directly instead.  If you really need a    * String, use this method, which is nothing more than    * a convenience call to<b>new String(token.termBuffer(), 0, token.termLength())</b>    */
DECL|method|term
specifier|public
name|String
name|term
parameter_list|()
block|{
name|initTermBuffer
argument_list|()
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
return|;
block|}
comment|/** Copies the contents of buffer, starting at offset for    *  length characters, into the termBuffer array.    *  @param buffer the buffer to copy    *  @param offset the index in the buffer of the first character to copy    *  @param length the number of characters to copy    */
DECL|method|setTermBuffer
specifier|public
name|void
name|setTermBuffer
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|char
index|[]
name|newCharBuffer
init|=
name|growTermBuffer
argument_list|(
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|newCharBuffer
operator|!=
literal|null
condition|)
block|{
name|termBuffer
operator|=
name|newCharBuffer
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|termLength
operator|=
name|length
expr_stmt|;
block|}
comment|/** Copies the contents of buffer into the termBuffer array.    *  @param buffer the buffer to copy    */
DECL|method|setTermBuffer
specifier|public
name|void
name|setTermBuffer
parameter_list|(
name|String
name|buffer
parameter_list|)
block|{
name|int
name|length
init|=
name|buffer
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
index|[]
name|newCharBuffer
init|=
name|growTermBuffer
argument_list|(
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|newCharBuffer
operator|!=
literal|null
condition|)
block|{
name|termBuffer
operator|=
name|newCharBuffer
expr_stmt|;
block|}
name|buffer
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|length
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|termLength
operator|=
name|length
expr_stmt|;
block|}
comment|/** Copies the contents of buffer, starting at offset and continuing    *  for length characters, into the termBuffer array.    *  @param buffer the buffer to copy    *  @param offset the index in the buffer of the first character to copy    *  @param length the number of characters to copy    */
DECL|method|setTermBuffer
specifier|public
name|void
name|setTermBuffer
parameter_list|(
name|String
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
assert|assert
name|offset
operator|<=
name|buffer
operator|.
name|length
argument_list|()
assert|;
assert|assert
name|offset
operator|+
name|length
operator|<=
name|buffer
operator|.
name|length
argument_list|()
assert|;
name|char
index|[]
name|newCharBuffer
init|=
name|growTermBuffer
argument_list|(
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|newCharBuffer
operator|!=
literal|null
condition|)
block|{
name|termBuffer
operator|=
name|newCharBuffer
expr_stmt|;
block|}
name|buffer
operator|.
name|getChars
argument_list|(
name|offset
argument_list|,
name|offset
operator|+
name|length
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|termLength
operator|=
name|length
expr_stmt|;
block|}
comment|/** Returns the internal termBuffer character array which    *  you can then directly alter.  If the array is too    *  small for your token, use {@link    *  #resizeTermBuffer(int)} to increase it.  After    *  altering the buffer be sure to call {@link    *  #setTermLength} to record the number of valid    *  characters that were placed into the termBuffer. */
DECL|method|termBuffer
specifier|public
name|char
index|[]
name|termBuffer
parameter_list|()
block|{
name|initTermBuffer
argument_list|()
expr_stmt|;
return|return
name|termBuffer
return|;
block|}
comment|/** Grows the termBuffer to at least size newSize, preserving the    *  existing content. Note: If the next operation is to change    *  the contents of the term buffer use    *  {@link #setTermBuffer(char[], int, int)},    *  {@link #setTermBuffer(String)}, or    *  {@link #setTermBuffer(String, int, int)}    *  to optimally combine the resize with the setting of the termBuffer.    *  @param newSize minimum size of the new termBuffer    *  @return newly created termBuffer with length>= newSize    */
DECL|method|resizeTermBuffer
specifier|public
name|char
index|[]
name|resizeTermBuffer
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
name|char
index|[]
name|newCharBuffer
init|=
name|growTermBuffer
argument_list|(
name|newSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|termBuffer
operator|==
literal|null
condition|)
block|{
comment|// If there were termText, then preserve it.
comment|// note that if termBuffer is null then newCharBuffer cannot be null
assert|assert
name|newCharBuffer
operator|!=
literal|null
assert|;
name|termBuffer
operator|=
name|newCharBuffer
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|newCharBuffer
operator|!=
literal|null
condition|)
block|{
comment|// Note: if newCharBuffer != null then termBuffer needs to grow.
comment|// If there were a termBuffer, then preserve it
name|System
operator|.
name|arraycopy
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|newCharBuffer
argument_list|,
literal|0
argument_list|,
name|termBuffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|termBuffer
operator|=
name|newCharBuffer
expr_stmt|;
block|}
return|return
name|termBuffer
return|;
block|}
comment|/** Allocates a buffer char[] of at least newSize    *  @param newSize minimum size of the buffer    *  @return newly created buffer with length>= newSize or null if the current termBuffer is big enough    */
DECL|method|growTermBuffer
specifier|private
name|char
index|[]
name|growTermBuffer
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
if|if
condition|(
name|termBuffer
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|termBuffer
operator|.
name|length
operator|>=
name|newSize
condition|)
comment|// Already big enough
return|return
literal|null
return|;
else|else
comment|// Not big enough; create a new array with slight
comment|// over allocation:
return|return
operator|new
name|char
index|[
name|ArrayUtil
operator|.
name|getNextSize
argument_list|(
name|newSize
argument_list|)
index|]
return|;
block|}
else|else
block|{
comment|// determine the best size
comment|// The buffer is always at least MIN_BUFFER_SIZE
if|if
condition|(
name|newSize
operator|<
name|MIN_BUFFER_SIZE
condition|)
block|{
name|newSize
operator|=
name|MIN_BUFFER_SIZE
expr_stmt|;
block|}
return|return
operator|new
name|char
index|[
name|newSize
index|]
return|;
block|}
block|}
comment|// TODO: once we remove the deprecated termText() method
comment|// and switch entirely to char[] termBuffer we don't need
comment|// to use this method anymore
DECL|method|initTermBuffer
specifier|private
name|void
name|initTermBuffer
parameter_list|()
block|{
if|if
condition|(
name|termBuffer
operator|==
literal|null
condition|)
block|{
name|termBuffer
operator|=
operator|new
name|char
index|[
name|MIN_BUFFER_SIZE
index|]
expr_stmt|;
name|termLength
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/** Return number of valid characters (length of the term)    *  in the termBuffer array. */
DECL|method|termLength
specifier|public
name|int
name|termLength
parameter_list|()
block|{
return|return
name|termLength
return|;
block|}
comment|/** Set number of valid characters (length of the term) in    *  the termBuffer array. Use this to truncate the termBuffer    *  or to synchronize with external manipulation of the termBuffer.    *  Note: to grow the size of the array,    *  use {@link #resizeTermBuffer(int)} first.    *  @param length the truncated length    */
DECL|method|setTermLength
specifier|public
name|void
name|setTermLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|initTermBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|length
operator|>
name|termBuffer
operator|.
name|length
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"length "
operator|+
name|length
operator|+
literal|" exceeds the size of the termBuffer ("
operator|+
name|termBuffer
operator|.
name|length
operator|+
literal|")"
argument_list|)
throw|;
name|termLength
operator|=
name|length
expr_stmt|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|initTermBuffer
argument_list|()
expr_stmt|;
name|int
name|code
init|=
name|termLength
decl_stmt|;
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|ArrayUtil
operator|.
name|hashCode
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
return|return
name|code
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|termLength
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|TermAttributeImpl
name|t
init|=
operator|(
name|TermAttributeImpl
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// Do a deep clone
if|if
condition|(
name|termBuffer
operator|!=
literal|null
condition|)
block|{
name|t
operator|.
name|termBuffer
operator|=
operator|(
name|char
index|[]
operator|)
name|termBuffer
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|TermAttribute
condition|)
block|{
name|initTermBuffer
argument_list|()
expr_stmt|;
name|TermAttributeImpl
name|o
init|=
operator|(
operator|(
name|TermAttributeImpl
operator|)
name|other
operator|)
decl_stmt|;
name|o
operator|.
name|initTermBuffer
argument_list|()
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
name|termLength
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|termBuffer
index|[
name|i
index|]
operator|!=
name|o
operator|.
name|termBuffer
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|initTermBuffer
argument_list|()
expr_stmt|;
return|return
literal|"term="
operator|+
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
return|;
block|}
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
name|TermAttribute
name|t
init|=
operator|(
name|TermAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setTermBuffer
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termLength
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
