begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_comment
comment|/**  * Methods for manipulating arrays.  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|ArrayUtil
specifier|public
specifier|final
class|class
name|ArrayUtil
block|{
comment|/** Maximum length for an array (Integer.MAX_VALUE - RamUsageEstimator.NUM_BYTES_ARRAY_HEADER). */
DECL|field|MAX_ARRAY_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|MAX_ARRAY_LENGTH
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|-
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
decl_stmt|;
DECL|method|ArrayUtil
specifier|private
name|ArrayUtil
parameter_list|()
block|{}
comment|// no instance
comment|/*      Begin Apache Harmony code       Revision taken on Friday, June 12. https://svn.apache.org/repos/asf/harmony/enhanced/classlib/archive/java6/modules/luni/src/main/java/java/lang/Integer.java     */
comment|/**    * Parses a char array into an int.    * @param chars the character array    * @param offset The offset into the array    * @param len The length    * @return the int    * @throws NumberFormatException if it can't parse    */
DECL|method|parseInt
specifier|public
specifier|static
name|int
name|parseInt
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|NumberFormatException
block|{
return|return
name|parseInt
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
literal|10
argument_list|)
return|;
block|}
comment|/**    * Parses the string argument as if it was an int value and returns the    * result. Throws NumberFormatException if the string does not represent an    * int quantity. The second argument specifies the radix to use when parsing    * the value.    *    * @param chars a string representation of an int quantity.    * @param radix the base to use for conversion.    * @return int the value represented by the argument    * @throws NumberFormatException if the argument could not be parsed as an int quantity.    */
DECL|method|parseInt
specifier|public
specifier|static
name|int
name|parseInt
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|radix
parameter_list|)
throws|throws
name|NumberFormatException
block|{
if|if
condition|(
name|chars
operator|==
literal|null
operator|||
name|radix
argument_list|<
name|Character
operator|.
name|MIN_RADIX
operator|||
name|radix
argument_list|>
name|Character
operator|.
name|MAX_RADIX
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|()
throw|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"chars length is 0"
argument_list|)
throw|;
block|}
name|boolean
name|negative
init|=
name|chars
index|[
name|offset
operator|+
name|i
index|]
operator|==
literal|'-'
decl_stmt|;
if|if
condition|(
name|negative
operator|&&
operator|++
name|i
operator|==
name|len
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"can't convert to an int"
argument_list|)
throw|;
block|}
if|if
condition|(
name|negative
operator|==
literal|true
condition|)
block|{
name|offset
operator|++
expr_stmt|;
name|len
operator|--
expr_stmt|;
block|}
return|return
name|parse
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
name|radix
argument_list|,
name|negative
argument_list|)
return|;
block|}
DECL|method|parse
specifier|private
specifier|static
name|int
name|parse
parameter_list|(
name|char
index|[]
name|chars
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|radix
parameter_list|,
name|boolean
name|negative
parameter_list|)
throws|throws
name|NumberFormatException
block|{
name|int
name|max
init|=
name|Integer
operator|.
name|MIN_VALUE
operator|/
name|radix
decl_stmt|;
name|int
name|result
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|digit
init|=
name|Character
operator|.
name|digit
argument_list|(
name|chars
index|[
name|i
operator|+
name|offset
index|]
argument_list|,
name|radix
argument_list|)
decl_stmt|;
if|if
condition|(
name|digit
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Unable to parse"
argument_list|)
throw|;
block|}
if|if
condition|(
name|max
operator|>
name|result
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Unable to parse"
argument_list|)
throw|;
block|}
name|int
name|next
init|=
name|result
operator|*
name|radix
operator|-
name|digit
decl_stmt|;
if|if
condition|(
name|next
operator|>
name|result
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Unable to parse"
argument_list|)
throw|;
block|}
name|result
operator|=
name|next
expr_stmt|;
block|}
comment|/*while (offset< len) {      }*/
if|if
condition|(
operator|!
name|negative
condition|)
block|{
name|result
operator|=
operator|-
name|result
expr_stmt|;
if|if
condition|(
name|result
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Unable to parse"
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/*   END APACHE HARMONY CODE   */
comment|/** Returns an array size&gt;= minTargetSize, generally    *  over-allocating exponentially to achieve amortized    *  linear-time cost as the array grows.    *    *  NOTE: this was originally borrowed from Python 2.4.2    *  listobject.c sources (attribution in LICENSE.txt), but    *  has now been substantially changed based on    *  discussions from java-dev thread with subject "Dynamic    *  array reallocation algorithms", started on Jan 12    *  2010.    *    * @param minTargetSize Minimum required value to be returned.    * @param bytesPerElement Bytes used by each element of    * the array.  See constants in {@link RamUsageEstimator}.    *    * @lucene.internal    */
DECL|method|oversize
specifier|public
specifier|static
name|int
name|oversize
parameter_list|(
name|int
name|minTargetSize
parameter_list|,
name|int
name|bytesPerElement
parameter_list|)
block|{
if|if
condition|(
name|minTargetSize
operator|<
literal|0
condition|)
block|{
comment|// catch usage that accidentally overflows int
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid array size "
operator|+
name|minTargetSize
argument_list|)
throw|;
block|}
if|if
condition|(
name|minTargetSize
operator|==
literal|0
condition|)
block|{
comment|// wait until at least one element is requested
return|return
literal|0
return|;
block|}
if|if
condition|(
name|minTargetSize
operator|>
name|MAX_ARRAY_LENGTH
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"requested array size "
operator|+
name|minTargetSize
operator|+
literal|" exceeds maximum array in java ("
operator|+
name|MAX_ARRAY_LENGTH
operator|+
literal|")"
argument_list|)
throw|;
block|}
comment|// asymptotic exponential growth by 1/8th, favors
comment|// spending a bit more CPU to not tie up too much wasted
comment|// RAM:
name|int
name|extra
init|=
name|minTargetSize
operator|>>
literal|3
decl_stmt|;
if|if
condition|(
name|extra
operator|<
literal|3
condition|)
block|{
comment|// for very small arrays, where constant overhead of
comment|// realloc is presumably relatively high, we grow
comment|// faster
name|extra
operator|=
literal|3
expr_stmt|;
block|}
name|int
name|newSize
init|=
name|minTargetSize
operator|+
name|extra
decl_stmt|;
comment|// add 7 to allow for worst case byte alignment addition below:
if|if
condition|(
name|newSize
operator|+
literal|7
operator|<
literal|0
operator|||
name|newSize
operator|+
literal|7
operator|>
name|MAX_ARRAY_LENGTH
condition|)
block|{
comment|// int overflowed, or we exceeded the maximum array length
return|return
name|MAX_ARRAY_LENGTH
return|;
block|}
if|if
condition|(
name|Constants
operator|.
name|JRE_IS_64BIT
condition|)
block|{
comment|// round up to 8 byte alignment in 64bit env
switch|switch
condition|(
name|bytesPerElement
condition|)
block|{
case|case
literal|4
case|:
comment|// round up to multiple of 2
return|return
operator|(
name|newSize
operator|+
literal|1
operator|)
operator|&
literal|0x7ffffffe
return|;
case|case
literal|2
case|:
comment|// round up to multiple of 4
return|return
operator|(
name|newSize
operator|+
literal|3
operator|)
operator|&
literal|0x7ffffffc
return|;
case|case
literal|1
case|:
comment|// round up to multiple of 8
return|return
operator|(
name|newSize
operator|+
literal|7
operator|)
operator|&
literal|0x7ffffff8
return|;
case|case
literal|8
case|:
comment|// no rounding
default|default:
comment|// odd (invalid?) size
return|return
name|newSize
return|;
block|}
block|}
else|else
block|{
comment|// round up to 4 byte alignment in 64bit env
switch|switch
condition|(
name|bytesPerElement
condition|)
block|{
case|case
literal|2
case|:
comment|// round up to multiple of 2
return|return
operator|(
name|newSize
operator|+
literal|1
operator|)
operator|&
literal|0x7ffffffe
return|;
case|case
literal|1
case|:
comment|// round up to multiple of 4
return|return
operator|(
name|newSize
operator|+
literal|3
operator|)
operator|&
literal|0x7ffffffc
return|;
case|case
literal|4
case|:
case|case
literal|8
case|:
comment|// no rounding
default|default:
comment|// odd (invalid?) size
return|return
name|newSize
return|;
block|}
block|}
block|}
DECL|method|grow
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
index|[]
name|grow
parameter_list|(
name|T
index|[]
name|array
parameter_list|,
name|int
name|minSize
parameter_list|)
block|{
assert|assert
name|minSize
operator|>=
literal|0
operator|:
literal|"size must be positive (got "
operator|+
name|minSize
operator|+
literal|"): likely integer overflow?"
assert|;
if|if
condition|(
name|array
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|array
argument_list|,
name|oversize
argument_list|(
name|minSize
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
argument_list|)
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|short
index|[]
name|grow
parameter_list|(
name|short
index|[]
name|array
parameter_list|,
name|int
name|minSize
parameter_list|)
block|{
assert|assert
name|minSize
operator|>=
literal|0
operator|:
literal|"size must be positive (got "
operator|+
name|minSize
operator|+
literal|"): likely integer overflow?"
assert|;
if|if
condition|(
name|array
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|array
argument_list|,
name|oversize
argument_list|(
name|minSize
argument_list|,
name|Short
operator|.
name|BYTES
argument_list|)
argument_list|)
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|short
index|[]
name|grow
parameter_list|(
name|short
index|[]
name|array
parameter_list|)
block|{
return|return
name|grow
argument_list|(
name|array
argument_list|,
literal|1
operator|+
name|array
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|float
index|[]
name|grow
parameter_list|(
name|float
index|[]
name|array
parameter_list|,
name|int
name|minSize
parameter_list|)
block|{
assert|assert
name|minSize
operator|>=
literal|0
operator|:
literal|"size must be positive (got "
operator|+
name|minSize
operator|+
literal|"): likely integer overflow?"
assert|;
if|if
condition|(
name|array
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|array
argument_list|,
name|oversize
argument_list|(
name|minSize
argument_list|,
name|Float
operator|.
name|BYTES
argument_list|)
argument_list|)
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|float
index|[]
name|grow
parameter_list|(
name|float
index|[]
name|array
parameter_list|)
block|{
return|return
name|grow
argument_list|(
name|array
argument_list|,
literal|1
operator|+
name|array
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|double
index|[]
name|grow
parameter_list|(
name|double
index|[]
name|array
parameter_list|,
name|int
name|minSize
parameter_list|)
block|{
assert|assert
name|minSize
operator|>=
literal|0
operator|:
literal|"size must be positive (got "
operator|+
name|minSize
operator|+
literal|"): likely integer overflow?"
assert|;
if|if
condition|(
name|array
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|array
argument_list|,
name|oversize
argument_list|(
name|minSize
argument_list|,
name|Double
operator|.
name|BYTES
argument_list|)
argument_list|)
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|double
index|[]
name|grow
parameter_list|(
name|double
index|[]
name|array
parameter_list|)
block|{
return|return
name|grow
argument_list|(
name|array
argument_list|,
literal|1
operator|+
name|array
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|int
index|[]
name|grow
parameter_list|(
name|int
index|[]
name|array
parameter_list|,
name|int
name|minSize
parameter_list|)
block|{
assert|assert
name|minSize
operator|>=
literal|0
operator|:
literal|"size must be positive (got "
operator|+
name|minSize
operator|+
literal|"): likely integer overflow?"
assert|;
if|if
condition|(
name|array
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|array
argument_list|,
name|oversize
argument_list|(
name|minSize
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|int
index|[]
name|grow
parameter_list|(
name|int
index|[]
name|array
parameter_list|)
block|{
return|return
name|grow
argument_list|(
name|array
argument_list|,
literal|1
operator|+
name|array
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|long
index|[]
name|grow
parameter_list|(
name|long
index|[]
name|array
parameter_list|,
name|int
name|minSize
parameter_list|)
block|{
assert|assert
name|minSize
operator|>=
literal|0
operator|:
literal|"size must be positive (got "
operator|+
name|minSize
operator|+
literal|"): likely integer overflow?"
assert|;
if|if
condition|(
name|array
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|array
argument_list|,
name|oversize
argument_list|(
name|minSize
argument_list|,
name|Long
operator|.
name|BYTES
argument_list|)
argument_list|)
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|long
index|[]
name|grow
parameter_list|(
name|long
index|[]
name|array
parameter_list|)
block|{
return|return
name|grow
argument_list|(
name|array
argument_list|,
literal|1
operator|+
name|array
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|byte
index|[]
name|grow
parameter_list|(
name|byte
index|[]
name|array
parameter_list|,
name|int
name|minSize
parameter_list|)
block|{
assert|assert
name|minSize
operator|>=
literal|0
operator|:
literal|"size must be positive (got "
operator|+
name|minSize
operator|+
literal|"): likely integer overflow?"
assert|;
if|if
condition|(
name|array
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|array
argument_list|,
name|oversize
argument_list|(
name|minSize
argument_list|,
name|Byte
operator|.
name|BYTES
argument_list|)
argument_list|)
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|byte
index|[]
name|grow
parameter_list|(
name|byte
index|[]
name|array
parameter_list|)
block|{
return|return
name|grow
argument_list|(
name|array
argument_list|,
literal|1
operator|+
name|array
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|char
index|[]
name|grow
parameter_list|(
name|char
index|[]
name|array
parameter_list|,
name|int
name|minSize
parameter_list|)
block|{
assert|assert
name|minSize
operator|>=
literal|0
operator|:
literal|"size must be positive (got "
operator|+
name|minSize
operator|+
literal|"): likely integer overflow?"
assert|;
if|if
condition|(
name|array
operator|.
name|length
operator|<
name|minSize
condition|)
block|{
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|array
argument_list|,
name|oversize
argument_list|(
name|minSize
argument_list|,
name|Character
operator|.
name|BYTES
argument_list|)
argument_list|)
return|;
block|}
else|else
return|return
name|array
return|;
block|}
DECL|method|grow
specifier|public
specifier|static
name|char
index|[]
name|grow
parameter_list|(
name|char
index|[]
name|array
parameter_list|)
block|{
return|return
name|grow
argument_list|(
name|array
argument_list|,
literal|1
operator|+
name|array
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**    * Returns hash of chars in range start (inclusive) to    * end (inclusive)    */
DECL|method|hashCode
specifier|public
specifier|static
name|int
name|hashCode
parameter_list|(
name|char
index|[]
name|array
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|int
name|code
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|end
operator|-
literal|1
init|;
name|i
operator|>=
name|start
condition|;
name|i
operator|--
control|)
name|code
operator|=
name|code
operator|*
literal|31
operator|+
name|array
index|[
name|i
index|]
expr_stmt|;
return|return
name|code
return|;
block|}
comment|// Since Arrays.equals doesn't implement offsets for equals
comment|/**    * See if two array slices are the same.    *    * @param left        The left array to compare    * @param offsetLeft  The offset into the array.  Must be positive    * @param right       The right array to compare    * @param offsetRight the offset into the right array.  Must be positive    * @param length      The length of the section of the array to compare    * @return true if the two arrays, starting at their respective offsets, are equal    *     * @see java.util.Arrays#equals(byte[], byte[])    */
DECL|method|equals
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|byte
index|[]
name|left
parameter_list|,
name|int
name|offsetLeft
parameter_list|,
name|byte
index|[]
name|right
parameter_list|,
name|int
name|offsetRight
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
operator|(
name|offsetLeft
operator|+
name|length
operator|<=
name|left
operator|.
name|length
operator|)
operator|&&
operator|(
name|offsetRight
operator|+
name|length
operator|<=
name|right
operator|.
name|length
operator|)
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
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|left
index|[
name|offsetLeft
operator|+
name|i
index|]
operator|!=
name|right
index|[
name|offsetRight
operator|+
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
comment|// Since Arrays.equals doesn't implement offsets for equals
comment|/**    * See if two array slices are the same.    *    * @param left        The left array to compare    * @param offsetLeft  The offset into the array.  Must be positive    * @param right       The right array to compare    * @param offsetRight the offset into the right array.  Must be positive    * @param length      The length of the section of the array to compare    * @return true if the two arrays, starting at their respective offsets, are equal    *     * @see java.util.Arrays#equals(char[], char[])    */
DECL|method|equals
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|int
index|[]
name|left
parameter_list|,
name|int
name|offsetLeft
parameter_list|,
name|int
index|[]
name|right
parameter_list|,
name|int
name|offsetRight
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
operator|(
name|offsetLeft
operator|+
name|length
operator|<=
name|left
operator|.
name|length
operator|)
operator|&&
operator|(
name|offsetRight
operator|+
name|length
operator|<=
name|right
operator|.
name|length
operator|)
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
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|left
index|[
name|offsetLeft
operator|+
name|i
index|]
operator|!=
name|right
index|[
name|offsetRight
operator|+
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
comment|/** Swap values stored in slots<code>i</code> and<code>j</code> */
DECL|method|swap
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|swap
parameter_list|(
name|T
index|[]
name|arr
parameter_list|,
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
specifier|final
name|T
name|tmp
init|=
name|arr
index|[
name|i
index|]
decl_stmt|;
name|arr
index|[
name|i
index|]
operator|=
name|arr
index|[
name|j
index|]
expr_stmt|;
name|arr
index|[
name|j
index|]
operator|=
name|tmp
expr_stmt|;
block|}
comment|// intro-sorts
comment|/**    * Sorts the given array slice using the {@link Comparator}. This method uses the intro sort    * algorithm, but falls back to insertion sort for small arrays.    * @param fromIndex start index (inclusive)    * @param toIndex end index (exclusive)    */
DECL|method|introSort
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|introSort
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|,
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comp
parameter_list|)
block|{
if|if
condition|(
name|toIndex
operator|-
name|fromIndex
operator|<=
literal|1
condition|)
return|return;
operator|new
name|ArrayIntroSorter
argument_list|<>
argument_list|(
name|a
argument_list|,
name|comp
argument_list|)
operator|.
name|sort
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sorts the given array using the {@link Comparator}. This method uses the intro sort    * algorithm, but falls back to insertion sort for small arrays.    */
DECL|method|introSort
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|introSort
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comp
parameter_list|)
block|{
name|introSort
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|a
operator|.
name|length
argument_list|,
name|comp
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sorts the given array slice in natural order. This method uses the intro sort    * algorithm, but falls back to insertion sort for small arrays.    * @param fromIndex start index (inclusive)    * @param toIndex end index (exclusive)    */
DECL|method|introSort
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|?
super|super
name|T
argument_list|>
parameter_list|>
name|void
name|introSort
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|)
block|{
if|if
condition|(
name|toIndex
operator|-
name|fromIndex
operator|<=
literal|1
condition|)
return|return;
name|introSort
argument_list|(
name|a
argument_list|,
name|fromIndex
argument_list|,
name|toIndex
argument_list|,
name|Comparator
operator|.
name|naturalOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sorts the given array in natural order. This method uses the intro sort    * algorithm, but falls back to insertion sort for small arrays.    */
DECL|method|introSort
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|?
super|super
name|T
argument_list|>
parameter_list|>
name|void
name|introSort
parameter_list|(
name|T
index|[]
name|a
parameter_list|)
block|{
name|introSort
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|a
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// tim sorts:
comment|/**    * Sorts the given array slice using the {@link Comparator}. This method uses the Tim sort    * algorithm, but falls back to binary sort for small arrays.    * @param fromIndex start index (inclusive)    * @param toIndex end index (exclusive)    */
DECL|method|timSort
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|timSort
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|,
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comp
parameter_list|)
block|{
if|if
condition|(
name|toIndex
operator|-
name|fromIndex
operator|<=
literal|1
condition|)
return|return;
operator|new
name|ArrayTimSorter
argument_list|<>
argument_list|(
name|a
argument_list|,
name|comp
argument_list|,
name|a
operator|.
name|length
operator|/
literal|64
argument_list|)
operator|.
name|sort
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sorts the given array using the {@link Comparator}. This method uses the Tim sort    * algorithm, but falls back to binary sort for small arrays.    */
DECL|method|timSort
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|timSort
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|Comparator
argument_list|<
name|?
super|super
name|T
argument_list|>
name|comp
parameter_list|)
block|{
name|timSort
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|a
operator|.
name|length
argument_list|,
name|comp
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sorts the given array slice in natural order. This method uses the Tim sort    * algorithm, but falls back to binary sort for small arrays.    * @param fromIndex start index (inclusive)    * @param toIndex end index (exclusive)    */
DECL|method|timSort
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|?
super|super
name|T
argument_list|>
parameter_list|>
name|void
name|timSort
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|)
block|{
if|if
condition|(
name|toIndex
operator|-
name|fromIndex
operator|<=
literal|1
condition|)
return|return;
name|timSort
argument_list|(
name|a
argument_list|,
name|fromIndex
argument_list|,
name|toIndex
argument_list|,
name|Comparator
operator|.
name|naturalOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sorts the given array in natural order. This method uses the Tim sort    * algorithm, but falls back to binary sort for small arrays.    */
DECL|method|timSort
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|?
super|super
name|T
argument_list|>
parameter_list|>
name|void
name|timSort
parameter_list|(
name|T
index|[]
name|a
parameter_list|)
block|{
name|timSort
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|a
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
