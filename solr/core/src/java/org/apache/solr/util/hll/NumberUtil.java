begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util.hll
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
package|;
end_package
begin_comment
comment|/**  * A collection of utilities to work with numbers.  */
end_comment
begin_class
DECL|class|NumberUtil
class|class
name|NumberUtil
block|{
comment|// loge(2) (log-base e of 2)
DECL|field|LOGE_2
specifier|public
specifier|static
specifier|final
name|double
name|LOGE_2
init|=
literal|0.6931471805599453
decl_stmt|;
comment|// ************************************************************************
comment|/**      * Computes the<code>log2</code> (log-base-two) of the specified value.      *      * @param  value the<code>double</code> for which the<code>log2</code> is      *         desired.      * @return the<code>log2</code> of the specified value      */
DECL|method|log2
specifier|public
specifier|static
name|double
name|log2
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
comment|// REF:  http://en.wikipedia.org/wiki/Logarithmic_scale (conversion of bases)
return|return
name|Math
operator|.
name|log
argument_list|(
name|value
argument_list|)
operator|/
name|LOGE_2
return|;
block|}
comment|// ========================================================================
comment|// the hex characters
DECL|field|HEX
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|HEX
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'A'
block|,
literal|'B'
block|,
literal|'C'
block|,
literal|'D'
block|,
literal|'E'
block|,
literal|'F'
block|}
decl_stmt|;
comment|// ------------------------------------------------------------------------
comment|/**      * Converts the specified array of<code>byte</code>s into a string of      * hex characters (low<code>byte</code> first).      *      * @param  bytes the array of<code>byte</code>s that are to be converted.      *         This cannot be<code>null</code> though it may be empty.      * @param  offset the offset in<code>bytes</code> at which the bytes will      *         be taken.  This cannot be negative and must be less than      *<code>bytes.length - 1</code>.      * @param  count the number of bytes to be retrieved from the specified array.      *         This cannot be negative.  If greater than<code>bytes.length - offset</code>      *         then that value is used.      * @return a string of at most<code>count</code> characters that represents      *         the specified byte array in hex.  This will never be<code>null</code>      *         though it may be empty if<code>bytes</code> is empty or<code>count</code>      *         is zero.      * @throws IllegalArgumentException if<code>offset</code> is greater than      *         or equal to<code>bytes.length</code>.      * @see #fromHex(String, int, int)      */
DECL|method|toHex
specifier|public
specifier|static
name|String
name|toHex
parameter_list|(
specifier|final
name|byte
index|[]
name|bytes
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|offset
operator|>=
name|bytes
operator|.
name|length
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Offset is greater than the length ("
operator|+
name|offset
operator|+
literal|">= "
operator|+
name|bytes
operator|.
name|length
operator|+
literal|")."
argument_list|)
comment|/*by contract*/
throw|;
specifier|final
name|int
name|byteCount
init|=
name|Math
operator|.
name|min
argument_list|(
operator|(
name|bytes
operator|.
name|length
operator|-
name|offset
operator|)
argument_list|,
name|count
argument_list|)
decl_stmt|;
specifier|final
name|int
name|upperBound
init|=
name|byteCount
operator|+
name|offset
decl_stmt|;
specifier|final
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|byteCount
operator|*
literal|2
comment|/*two chars per byte*/
index|]
decl_stmt|;
name|int
name|charIndex
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|upperBound
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|byte
name|value
init|=
name|bytes
index|[
name|i
index|]
decl_stmt|;
name|chars
index|[
name|charIndex
operator|++
index|]
operator|=
name|HEX
index|[
operator|(
name|value
operator|>>>
literal|4
operator|)
operator|&
literal|0x0F
index|]
expr_stmt|;
name|chars
index|[
name|charIndex
operator|++
index|]
operator|=
name|HEX
index|[
name|value
operator|&
literal|0x0F
index|]
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|chars
argument_list|)
return|;
block|}
comment|/**      * Converts the specified array of hex characters into an array of<code>byte</code>s      * (low<code>byte</code> first).      *      * @param  string the string of hex characters to be converted into<code>byte</code>s.      *         This cannot be<code>null</code> though it may be blank.      * @param  offset the offset in the string at which the characters will be      *         taken.  This cannot be negative and must be less than<code>string.length() - 1</code>.      * @param  count the number of characters to be retrieved from the specified      *         string.  This cannot be negative and must be divisible by two      *         (since there are two characters per<code>byte</code>).      * @return the array of<code>byte</code>s that were converted from the      *         specified string (in the specified range).  This will never be      *<code>null</code> though it may be empty if<code>string</code>      *         is empty or<code>count</code> is zero.      * @throws IllegalArgumentException if<code>offset</code> is greater than      *         or equal to<code>string.length()</code> or if<code>count</code>      *         is not divisible by two.      * @see #toHex(byte[], int, int)      */
DECL|method|fromHex
specifier|public
specifier|static
name|byte
index|[]
name|fromHex
parameter_list|(
specifier|final
name|String
name|string
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|offset
operator|>=
name|string
operator|.
name|length
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Offset is greater than the length ("
operator|+
name|offset
operator|+
literal|">= "
operator|+
name|string
operator|.
name|length
argument_list|()
operator|+
literal|")."
argument_list|)
comment|/*by contract*/
throw|;
if|if
condition|(
operator|(
name|count
operator|&
literal|0x01
operator|)
operator|!=
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Count is not divisible by two ("
operator|+
name|count
operator|+
literal|")."
argument_list|)
comment|/*by contract*/
throw|;
specifier|final
name|int
name|charCount
init|=
name|Math
operator|.
name|min
argument_list|(
operator|(
name|string
operator|.
name|length
argument_list|()
operator|-
name|offset
operator|)
argument_list|,
name|count
argument_list|)
decl_stmt|;
specifier|final
name|int
name|upperBound
init|=
name|offset
operator|+
name|charCount
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|charCount
operator|>>>
literal|1
comment|/*aka /2*/
index|]
decl_stmt|;
name|int
name|byteIndex
init|=
literal|0
comment|/*beginning*/
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|upperBound
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|bytes
index|[
name|byteIndex
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
operator|(
name|digit
argument_list|(
name|string
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
operator|<<
literal|4
operator|)
operator||
name|digit
argument_list|(
name|string
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
operator|)
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
comment|// ------------------------------------------------------------------------
comment|/**      * @param  character a hex character to be converted to a<code>byte</code>.      *         This cannot be a character other than [a-fA-F0-9].      * @return the value of the specified character.  This will be a value<code>0</code>      *         through<code>15</code>.      * @throws IllegalArgumentException if the specified character is not in      *         [a-fA-F0-9]      */
DECL|method|digit
specifier|private
specifier|static
specifier|final
name|int
name|digit
parameter_list|(
specifier|final
name|char
name|character
parameter_list|)
block|{
switch|switch
condition|(
name|character
condition|)
block|{
case|case
literal|'0'
case|:
return|return
literal|0
return|;
case|case
literal|'1'
case|:
return|return
literal|1
return|;
case|case
literal|'2'
case|:
return|return
literal|2
return|;
case|case
literal|'3'
case|:
return|return
literal|3
return|;
case|case
literal|'4'
case|:
return|return
literal|4
return|;
case|case
literal|'5'
case|:
return|return
literal|5
return|;
case|case
literal|'6'
case|:
return|return
literal|6
return|;
case|case
literal|'7'
case|:
return|return
literal|7
return|;
case|case
literal|'8'
case|:
return|return
literal|8
return|;
case|case
literal|'9'
case|:
return|return
literal|9
return|;
case|case
literal|'a'
case|:
case|case
literal|'A'
case|:
return|return
literal|10
return|;
case|case
literal|'b'
case|:
case|case
literal|'B'
case|:
return|return
literal|11
return|;
case|case
literal|'c'
case|:
case|case
literal|'C'
case|:
return|return
literal|12
return|;
case|case
literal|'d'
case|:
case|case
literal|'D'
case|:
return|return
literal|13
return|;
case|case
literal|'e'
case|:
case|case
literal|'E'
case|:
return|return
literal|14
return|;
case|case
literal|'f'
case|:
case|case
literal|'F'
case|:
return|return
literal|15
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Character is not in [a-fA-F0-9] ('"
operator|+
name|character
operator|+
literal|"')."
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
