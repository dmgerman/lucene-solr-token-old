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
name|index
operator|.
name|FilterLeafReader
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
name|index
operator|.
name|FilteredTermsEnum
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
name|index
operator|.
name|Terms
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
name|index
operator|.
name|TermsEnum
import|;
end_import
begin_comment
comment|/**  * This is a helper class to generate prefix-encoded representations for numerical values  * and supplies converters to represent float/double values as sortable integers/longs.  *  *<p>To quickly execute range queries in Apache Lucene, a range is divided recursively  * into multiple intervals for searching: The center of the range is searched only with  * the lowest possible precision in the trie, while the boundaries are matched  * more exactly. This reduces the number of terms dramatically.  *  *<p>This class generates terms to achieve this: First the numerical integer values need to  * be converted to bytes. For that integer values (32 bit or 64 bit) are made unsigned  * and the bits are converted to ASCII chars with each 7 bit. The resulting byte[] is  * sortable like the original integer value (even using UTF-8 sort order). Each value is also  * prefixed (in the first char) by the<code>shift</code> value (number of bits removed) used  * during encoding.  *  *<p>To also index floating point numbers, this class supplies two methods to convert them  * to integer values by changing their bit layout: {@link #doubleToSortableLong},  * {@link #floatToSortableInt}. You will have no precision loss by  * converting floating point numbers to integers and back (only that the integer form  * is not usable). Other data types like dates can easily converted to longs or ints (e.g.  * date to long: {@link java.util.Date#getTime}).  *  *<p>For easy usage, the trie algorithm is implemented for indexing inside  * {@link org.apache.lucene.analysis.LegacyNumericTokenStream} that can index<code>int</code>,<code>long</code>,  *<code>float</code>, and<code>double</code>. For querying,  * {@link org.apache.lucene.search.LegacyNumericRangeQuery} implements the query part  * for the same data types.  *  *<p>This class can also be used, to generate lexicographically sortable (according to  * {@link BytesRef#getUTF8SortedAsUTF16Comparator()}) representations of numeric data  * types for other usages (e.g. sorting).  *  * @lucene.internal  *  * @deprecated Please use {@link org.apache.lucene.index.PointValues} instead.  *  * @since 2.9, API changed non backwards-compliant in 4.0  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|LegacyNumericUtils
specifier|public
specifier|final
class|class
name|LegacyNumericUtils
block|{
DECL|method|LegacyNumericUtils
specifier|private
name|LegacyNumericUtils
parameter_list|()
block|{}
comment|// no instance!
comment|/**    * The default precision step used by {@link org.apache.lucene.document.LegacyLongField},    * {@link org.apache.lucene.document.LegacyDoubleField}, {@link org.apache.lucene.analysis.LegacyNumericTokenStream}, {@link    * org.apache.lucene.search.LegacyNumericRangeQuery}.    */
DECL|field|PRECISION_STEP_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|PRECISION_STEP_DEFAULT
init|=
literal|16
decl_stmt|;
comment|/**    * The default precision step used by {@link org.apache.lucene.document.LegacyIntField} and    * {@link org.apache.lucene.document.LegacyFloatField}.    */
DECL|field|PRECISION_STEP_DEFAULT_32
specifier|public
specifier|static
specifier|final
name|int
name|PRECISION_STEP_DEFAULT_32
init|=
literal|8
decl_stmt|;
comment|/**    * Longs are stored at lower precision by shifting off lower bits. The shift count is    * stored as<code>SHIFT_START_LONG+shift</code> in the first byte    */
DECL|field|SHIFT_START_LONG
specifier|public
specifier|static
specifier|final
name|byte
name|SHIFT_START_LONG
init|=
literal|0x20
decl_stmt|;
comment|/**    * The maximum term length (used for<code>byte[]</code> buffer size)    * for encoding<code>long</code> values.    * @see #longToPrefixCodedBytes    */
DECL|field|BUF_SIZE_LONG
specifier|public
specifier|static
specifier|final
name|int
name|BUF_SIZE_LONG
init|=
literal|63
operator|/
literal|7
operator|+
literal|2
decl_stmt|;
comment|/**    * Integers are stored at lower precision by shifting off lower bits. The shift count is    * stored as<code>SHIFT_START_INT+shift</code> in the first byte    */
DECL|field|SHIFT_START_INT
specifier|public
specifier|static
specifier|final
name|byte
name|SHIFT_START_INT
init|=
literal|0x60
decl_stmt|;
comment|/**    * The maximum term length (used for<code>byte[]</code> buffer size)    * for encoding<code>int</code> values.    * @see #intToPrefixCodedBytes    */
DECL|field|BUF_SIZE_INT
specifier|public
specifier|static
specifier|final
name|int
name|BUF_SIZE_INT
init|=
literal|31
operator|/
literal|7
operator|+
literal|2
decl_stmt|;
comment|/**    * Returns prefix coded bits after reducing the precision by<code>shift</code> bits.    * This is method is used by {@link org.apache.lucene.analysis.LegacyNumericTokenStream}.    * After encoding, {@code bytes.offset} will always be 0.     * @param val the numeric value    * @param shift how many bits to strip from the right    * @param bytes will contain the encoded value    */
DECL|method|longToPrefixCoded
specifier|public
specifier|static
name|void
name|longToPrefixCoded
parameter_list|(
specifier|final
name|long
name|val
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|,
specifier|final
name|BytesRefBuilder
name|bytes
parameter_list|)
block|{
name|longToPrefixCodedBytes
argument_list|(
name|val
argument_list|,
name|shift
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns prefix coded bits after reducing the precision by<code>shift</code> bits.    * This is method is used by {@link org.apache.lucene.analysis.LegacyNumericTokenStream}.    * After encoding, {@code bytes.offset} will always be 0.    * @param val the numeric value    * @param shift how many bits to strip from the right    * @param bytes will contain the encoded value    */
DECL|method|intToPrefixCoded
specifier|public
specifier|static
name|void
name|intToPrefixCoded
parameter_list|(
specifier|final
name|int
name|val
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|,
specifier|final
name|BytesRefBuilder
name|bytes
parameter_list|)
block|{
name|intToPrefixCodedBytes
argument_list|(
name|val
argument_list|,
name|shift
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns prefix coded bits after reducing the precision by<code>shift</code> bits.    * This is method is used by {@link org.apache.lucene.analysis.LegacyNumericTokenStream}.    * After encoding, {@code bytes.offset} will always be 0.    * @param val the numeric value    * @param shift how many bits to strip from the right    * @param bytes will contain the encoded value    */
DECL|method|longToPrefixCodedBytes
specifier|public
specifier|static
name|void
name|longToPrefixCodedBytes
parameter_list|(
specifier|final
name|long
name|val
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|,
specifier|final
name|BytesRefBuilder
name|bytes
parameter_list|)
block|{
comment|// ensure shift is 0..63
if|if
condition|(
operator|(
name|shift
operator|&
operator|~
literal|0x3f
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal shift value, must be 0..63; got shift="
operator|+
name|shift
argument_list|)
throw|;
block|}
name|int
name|nChars
init|=
operator|(
operator|(
operator|(
literal|63
operator|-
name|shift
operator|)
operator|*
literal|37
operator|)
operator|>>
literal|8
operator|)
operator|+
literal|1
decl_stmt|;
comment|// i/7 is the same as (i*37)>>8 for i in 0..63
name|bytes
operator|.
name|setLength
argument_list|(
name|nChars
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// one extra for the byte that contains the shift info
name|bytes
operator|.
name|grow
argument_list|(
name|BUF_SIZE_LONG
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|setByteAt
argument_list|(
literal|0
argument_list|,
call|(
name|byte
call|)
argument_list|(
name|SHIFT_START_LONG
operator|+
name|shift
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|sortableBits
init|=
name|val
operator|^
literal|0x8000000000000000L
decl_stmt|;
name|sortableBits
operator|>>>=
name|shift
expr_stmt|;
while|while
condition|(
name|nChars
operator|>
literal|0
condition|)
block|{
comment|// Store 7 bits per byte for compatibility
comment|// with UTF-8 encoding of terms
name|bytes
operator|.
name|setByteAt
argument_list|(
name|nChars
operator|--
argument_list|,
call|(
name|byte
call|)
argument_list|(
name|sortableBits
operator|&
literal|0x7f
argument_list|)
argument_list|)
expr_stmt|;
name|sortableBits
operator|>>>=
literal|7
expr_stmt|;
block|}
block|}
comment|/**    * Returns prefix coded bits after reducing the precision by<code>shift</code> bits.    * This is method is used by {@link org.apache.lucene.analysis.LegacyNumericTokenStream}.    * After encoding, {@code bytes.offset} will always be 0.     * @param val the numeric value    * @param shift how many bits to strip from the right    * @param bytes will contain the encoded value    */
DECL|method|intToPrefixCodedBytes
specifier|public
specifier|static
name|void
name|intToPrefixCodedBytes
parameter_list|(
specifier|final
name|int
name|val
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|,
specifier|final
name|BytesRefBuilder
name|bytes
parameter_list|)
block|{
comment|// ensure shift is 0..31
if|if
condition|(
operator|(
name|shift
operator|&
operator|~
literal|0x1f
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal shift value, must be 0..31; got shift="
operator|+
name|shift
argument_list|)
throw|;
block|}
name|int
name|nChars
init|=
operator|(
operator|(
operator|(
literal|31
operator|-
name|shift
operator|)
operator|*
literal|37
operator|)
operator|>>
literal|8
operator|)
operator|+
literal|1
decl_stmt|;
comment|// i/7 is the same as (i*37)>>8 for i in 0..63
name|bytes
operator|.
name|setLength
argument_list|(
name|nChars
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// one extra for the byte that contains the shift info
name|bytes
operator|.
name|grow
argument_list|(
name|LegacyNumericUtils
operator|.
name|BUF_SIZE_LONG
argument_list|)
expr_stmt|;
comment|// use the max
name|bytes
operator|.
name|setByteAt
argument_list|(
literal|0
argument_list|,
call|(
name|byte
call|)
argument_list|(
name|SHIFT_START_INT
operator|+
name|shift
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|sortableBits
init|=
name|val
operator|^
literal|0x80000000
decl_stmt|;
name|sortableBits
operator|>>>=
name|shift
expr_stmt|;
while|while
condition|(
name|nChars
operator|>
literal|0
condition|)
block|{
comment|// Store 7 bits per byte for compatibility
comment|// with UTF-8 encoding of terms
name|bytes
operator|.
name|setByteAt
argument_list|(
name|nChars
operator|--
argument_list|,
call|(
name|byte
call|)
argument_list|(
name|sortableBits
operator|&
literal|0x7f
argument_list|)
argument_list|)
expr_stmt|;
name|sortableBits
operator|>>>=
literal|7
expr_stmt|;
block|}
block|}
comment|/**    * Returns the shift value from a prefix encoded {@code long}.    * @throws NumberFormatException if the supplied {@link BytesRef} is    * not correctly prefix encoded.    */
DECL|method|getPrefixCodedLongShift
specifier|public
specifier|static
name|int
name|getPrefixCodedLongShift
parameter_list|(
specifier|final
name|BytesRef
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|bytes
index|[
name|val
operator|.
name|offset
index|]
operator|-
name|SHIFT_START_LONG
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|63
operator|||
name|shift
operator|<
literal|0
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid shift value ("
operator|+
name|shift
operator|+
literal|") in prefixCoded bytes (is encoded value really an INT?)"
argument_list|)
throw|;
return|return
name|shift
return|;
block|}
comment|/**    * Returns the shift value from a prefix encoded {@code int}.    * @throws NumberFormatException if the supplied {@link BytesRef} is    * not correctly prefix encoded.    */
DECL|method|getPrefixCodedIntShift
specifier|public
specifier|static
name|int
name|getPrefixCodedIntShift
parameter_list|(
specifier|final
name|BytesRef
name|val
parameter_list|)
block|{
specifier|final
name|int
name|shift
init|=
name|val
operator|.
name|bytes
index|[
name|val
operator|.
name|offset
index|]
operator|-
name|SHIFT_START_INT
decl_stmt|;
if|if
condition|(
name|shift
operator|>
literal|31
operator|||
name|shift
operator|<
literal|0
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid shift value in prefixCoded bytes (is encoded value really an INT?)"
argument_list|)
throw|;
return|return
name|shift
return|;
block|}
comment|/**    * Returns a long from prefixCoded bytes.    * Rightmost bits will be zero for lower precision codes.    * This method can be used to decode a term's value.    * @throws NumberFormatException if the supplied {@link BytesRef} is    * not correctly prefix encoded.    * @see #longToPrefixCodedBytes    */
DECL|method|prefixCodedToLong
specifier|public
specifier|static
name|long
name|prefixCodedToLong
parameter_list|(
specifier|final
name|BytesRef
name|val
parameter_list|)
block|{
name|long
name|sortableBits
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|val
operator|.
name|offset
operator|+
literal|1
init|,
name|limit
init|=
name|val
operator|.
name|offset
operator|+
name|val
operator|.
name|length
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|sortableBits
operator|<<=
literal|7
expr_stmt|;
specifier|final
name|byte
name|b
init|=
name|val
operator|.
name|bytes
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid prefixCoded numerical value representation (byte "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|b
operator|&
literal|0xff
argument_list|)
operator|+
literal|" at position "
operator|+
operator|(
name|i
operator|-
name|val
operator|.
name|offset
operator|)
operator|+
literal|" is invalid)"
argument_list|)
throw|;
block|}
name|sortableBits
operator||=
name|b
expr_stmt|;
block|}
return|return
operator|(
name|sortableBits
operator|<<
name|getPrefixCodedLongShift
argument_list|(
name|val
argument_list|)
operator|)
operator|^
literal|0x8000000000000000L
return|;
block|}
comment|/**    * Returns an int from prefixCoded bytes.    * Rightmost bits will be zero for lower precision codes.    * This method can be used to decode a term's value.    * @throws NumberFormatException if the supplied {@link BytesRef} is    * not correctly prefix encoded.    * @see #intToPrefixCodedBytes    */
DECL|method|prefixCodedToInt
specifier|public
specifier|static
name|int
name|prefixCodedToInt
parameter_list|(
specifier|final
name|BytesRef
name|val
parameter_list|)
block|{
name|int
name|sortableBits
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|val
operator|.
name|offset
operator|+
literal|1
init|,
name|limit
init|=
name|val
operator|.
name|offset
operator|+
name|val
operator|.
name|length
init|;
name|i
operator|<
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|sortableBits
operator|<<=
literal|7
expr_stmt|;
specifier|final
name|byte
name|b
init|=
name|val
operator|.
name|bytes
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid prefixCoded numerical value representation (byte "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|b
operator|&
literal|0xff
argument_list|)
operator|+
literal|" at position "
operator|+
operator|(
name|i
operator|-
name|val
operator|.
name|offset
operator|)
operator|+
literal|" is invalid)"
argument_list|)
throw|;
block|}
name|sortableBits
operator||=
name|b
expr_stmt|;
block|}
return|return
operator|(
name|sortableBits
operator|<<
name|getPrefixCodedIntShift
argument_list|(
name|val
argument_list|)
operator|)
operator|^
literal|0x80000000
return|;
block|}
comment|/**    * Converts a<code>double</code> value to a sortable signed<code>long</code>.    * The value is converted by getting their IEEE 754 floating-point&quot;double format&quot;    * bit layout and then some bits are swapped, to be able to compare the result as long.    * By this the precision is not reduced, but the value can easily used as a long.    * The sort order (including {@link Double#NaN}) is defined by    * {@link Double#compareTo}; {@code NaN} is greater than positive infinity.    * @see #sortableLongToDouble    */
DECL|method|doubleToSortableLong
specifier|public
specifier|static
name|long
name|doubleToSortableLong
parameter_list|(
name|double
name|val
parameter_list|)
block|{
return|return
name|sortableDoubleBits
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Converts a sortable<code>long</code> back to a<code>double</code>.    * @see #doubleToSortableLong    */
DECL|method|sortableLongToDouble
specifier|public
specifier|static
name|double
name|sortableLongToDouble
parameter_list|(
name|long
name|val
parameter_list|)
block|{
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|sortableDoubleBits
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Converts a<code>float</code> value to a sortable signed<code>int</code>.    * The value is converted by getting their IEEE 754 floating-point&quot;float format&quot;    * bit layout and then some bits are swapped, to be able to compare the result as int.    * By this the precision is not reduced, but the value can easily used as an int.    * The sort order (including {@link Float#NaN}) is defined by    * {@link Float#compareTo}; {@code NaN} is greater than positive infinity.    * @see #sortableIntToFloat    */
DECL|method|floatToSortableInt
specifier|public
specifier|static
name|int
name|floatToSortableInt
parameter_list|(
name|float
name|val
parameter_list|)
block|{
return|return
name|sortableFloatBits
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Converts a sortable<code>int</code> back to a<code>float</code>.    * @see #floatToSortableInt    */
DECL|method|sortableIntToFloat
specifier|public
specifier|static
name|float
name|sortableIntToFloat
parameter_list|(
name|int
name|val
parameter_list|)
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|sortableFloatBits
argument_list|(
name|val
argument_list|)
argument_list|)
return|;
block|}
comment|/** Converts IEEE 754 representation of a double to sortable order (or back to the original) */
DECL|method|sortableDoubleBits
specifier|public
specifier|static
name|long
name|sortableDoubleBits
parameter_list|(
name|long
name|bits
parameter_list|)
block|{
return|return
name|bits
operator|^
operator|(
name|bits
operator|>>
literal|63
operator|)
operator|&
literal|0x7fffffffffffffffL
return|;
block|}
comment|/** Converts IEEE 754 representation of a float to sortable order (or back to the original) */
DECL|method|sortableFloatBits
specifier|public
specifier|static
name|int
name|sortableFloatBits
parameter_list|(
name|int
name|bits
parameter_list|)
block|{
return|return
name|bits
operator|^
operator|(
name|bits
operator|>>
literal|31
operator|)
operator|&
literal|0x7fffffff
return|;
block|}
comment|/**    * Splits a long range recursively.    * You may implement a builder that adds clauses to a    * {@link org.apache.lucene.search.BooleanQuery} for each call to its    * {@link LongRangeBuilder#addRange(BytesRef,BytesRef)}    * method.    *<p>This method is used by {@link org.apache.lucene.search.LegacyNumericRangeQuery}.    */
DECL|method|splitLongRange
specifier|public
specifier|static
name|void
name|splitLongRange
parameter_list|(
specifier|final
name|LongRangeBuilder
name|builder
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
specifier|final
name|long
name|minBound
parameter_list|,
specifier|final
name|long
name|maxBound
parameter_list|)
block|{
name|splitRange
argument_list|(
name|builder
argument_list|,
literal|64
argument_list|,
name|precisionStep
argument_list|,
name|minBound
argument_list|,
name|maxBound
argument_list|)
expr_stmt|;
block|}
comment|/**    * Splits an int range recursively.    * You may implement a builder that adds clauses to a    * {@link org.apache.lucene.search.BooleanQuery} for each call to its    * {@link IntRangeBuilder#addRange(BytesRef,BytesRef)}    * method.    *<p>This method is used by {@link org.apache.lucene.search.LegacyNumericRangeQuery}.    */
DECL|method|splitIntRange
specifier|public
specifier|static
name|void
name|splitIntRange
parameter_list|(
specifier|final
name|IntRangeBuilder
name|builder
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
specifier|final
name|int
name|minBound
parameter_list|,
specifier|final
name|int
name|maxBound
parameter_list|)
block|{
name|splitRange
argument_list|(
name|builder
argument_list|,
literal|32
argument_list|,
name|precisionStep
argument_list|,
name|minBound
argument_list|,
name|maxBound
argument_list|)
expr_stmt|;
block|}
comment|/** This helper does the splitting for both 32 and 64 bit. */
DECL|method|splitRange
specifier|private
specifier|static
name|void
name|splitRange
parameter_list|(
specifier|final
name|Object
name|builder
parameter_list|,
specifier|final
name|int
name|valSize
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|,
name|long
name|minBound
parameter_list|,
name|long
name|maxBound
parameter_list|)
block|{
if|if
condition|(
name|precisionStep
operator|<
literal|1
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"precisionStep must be>=1"
argument_list|)
throw|;
if|if
condition|(
name|minBound
operator|>
name|maxBound
condition|)
return|return;
for|for
control|(
name|int
name|shift
init|=
literal|0
init|;
condition|;
name|shift
operator|+=
name|precisionStep
control|)
block|{
comment|// calculate new bounds for inner precision
specifier|final
name|long
name|diff
init|=
literal|1L
operator|<<
operator|(
name|shift
operator|+
name|precisionStep
operator|)
decl_stmt|,
name|mask
init|=
operator|(
operator|(
literal|1L
operator|<<
name|precisionStep
operator|)
operator|-
literal|1L
operator|)
operator|<<
name|shift
decl_stmt|;
specifier|final
name|boolean
name|hasLower
init|=
operator|(
name|minBound
operator|&
name|mask
operator|)
operator|!=
literal|0L
decl_stmt|,
name|hasUpper
init|=
operator|(
name|maxBound
operator|&
name|mask
operator|)
operator|!=
name|mask
decl_stmt|;
specifier|final
name|long
name|nextMinBound
init|=
operator|(
name|hasLower
condition|?
operator|(
name|minBound
operator|+
name|diff
operator|)
else|:
name|minBound
operator|)
operator|&
operator|~
name|mask
decl_stmt|,
name|nextMaxBound
init|=
operator|(
name|hasUpper
condition|?
operator|(
name|maxBound
operator|-
name|diff
operator|)
else|:
name|maxBound
operator|)
operator|&
operator|~
name|mask
decl_stmt|;
specifier|final
name|boolean
name|lowerWrapped
init|=
name|nextMinBound
argument_list|<
name|minBound
argument_list|,
name|upperWrapped
operator|=
name|nextMaxBound
argument_list|>
name|maxBound
decl_stmt|;
if|if
condition|(
name|shift
operator|+
name|precisionStep
operator|>=
name|valSize
operator|||
name|nextMinBound
operator|>
name|nextMaxBound
operator|||
name|lowerWrapped
operator|||
name|upperWrapped
condition|)
block|{
comment|// We are in the lowest precision or the next precision is not available.
name|addRange
argument_list|(
name|builder
argument_list|,
name|valSize
argument_list|,
name|minBound
argument_list|,
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
comment|// exit the split recursion loop
break|break;
block|}
if|if
condition|(
name|hasLower
condition|)
name|addRange
argument_list|(
name|builder
argument_list|,
name|valSize
argument_list|,
name|minBound
argument_list|,
name|minBound
operator||
name|mask
argument_list|,
name|shift
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasUpper
condition|)
name|addRange
argument_list|(
name|builder
argument_list|,
name|valSize
argument_list|,
name|maxBound
operator|&
operator|~
name|mask
argument_list|,
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
comment|// recurse to next precision
name|minBound
operator|=
name|nextMinBound
expr_stmt|;
name|maxBound
operator|=
name|nextMaxBound
expr_stmt|;
block|}
block|}
comment|/** Helper that delegates to correct range builder */
DECL|method|addRange
specifier|private
specifier|static
name|void
name|addRange
parameter_list|(
specifier|final
name|Object
name|builder
parameter_list|,
specifier|final
name|int
name|valSize
parameter_list|,
name|long
name|minBound
parameter_list|,
name|long
name|maxBound
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
comment|// for the max bound set all lower bits (that were shifted away):
comment|// this is important for testing or other usages of the splitted range
comment|// (e.g. to reconstruct the full range). The prefixEncoding will remove
comment|// the bits anyway, so they do not hurt!
name|maxBound
operator||=
operator|(
literal|1L
operator|<<
name|shift
operator|)
operator|-
literal|1L
expr_stmt|;
comment|// delegate to correct range builder
switch|switch
condition|(
name|valSize
condition|)
block|{
case|case
literal|64
case|:
operator|(
operator|(
name|LongRangeBuilder
operator|)
name|builder
operator|)
operator|.
name|addRange
argument_list|(
name|minBound
argument_list|,
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
break|break;
case|case
literal|32
case|:
operator|(
operator|(
name|IntRangeBuilder
operator|)
name|builder
operator|)
operator|.
name|addRange
argument_list|(
operator|(
name|int
operator|)
name|minBound
argument_list|,
operator|(
name|int
operator|)
name|maxBound
argument_list|,
name|shift
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// Should not happen!
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"valSize must be 32 or 64."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Callback for {@link #splitLongRange}.    * You need to overwrite only one of the methods.    * @lucene.internal    * @since 2.9, API changed non backwards-compliant in 4.0    */
DECL|class|LongRangeBuilder
specifier|public
specifier|static
specifier|abstract
class|class
name|LongRangeBuilder
block|{
comment|/**      * Overwrite this method, if you like to receive the already prefix encoded range bounds.      * You can directly build classical (inclusive) range queries from them.      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
name|BytesRef
name|minPrefixCoded
parameter_list|,
name|BytesRef
name|maxPrefixCoded
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Overwrite this method, if you like to receive the raw long range bounds.      * You can use this for e.g. debugging purposes (print out range bounds).      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
specifier|final
name|long
name|min
parameter_list|,
specifier|final
name|long
name|max
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
specifier|final
name|BytesRefBuilder
name|minBytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|,
name|maxBytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|longToPrefixCodedBytes
argument_list|(
name|min
argument_list|,
name|shift
argument_list|,
name|minBytes
argument_list|)
expr_stmt|;
name|longToPrefixCodedBytes
argument_list|(
name|max
argument_list|,
name|shift
argument_list|,
name|maxBytes
argument_list|)
expr_stmt|;
name|addRange
argument_list|(
name|minBytes
operator|.
name|get
argument_list|()
argument_list|,
name|maxBytes
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Callback for {@link #splitIntRange}.    * You need to overwrite only one of the methods.    * @lucene.internal    * @since 2.9, API changed non backwards-compliant in 4.0    */
DECL|class|IntRangeBuilder
specifier|public
specifier|static
specifier|abstract
class|class
name|IntRangeBuilder
block|{
comment|/**      * Overwrite this method, if you like to receive the already prefix encoded range bounds.      * You can directly build classical range (inclusive) queries from them.      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
name|BytesRef
name|minPrefixCoded
parameter_list|,
name|BytesRef
name|maxPrefixCoded
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Overwrite this method, if you like to receive the raw int range bounds.      * You can use this for e.g. debugging purposes (print out range bounds).      */
DECL|method|addRange
specifier|public
name|void
name|addRange
parameter_list|(
specifier|final
name|int
name|min
parameter_list|,
specifier|final
name|int
name|max
parameter_list|,
specifier|final
name|int
name|shift
parameter_list|)
block|{
specifier|final
name|BytesRefBuilder
name|minBytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|,
name|maxBytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|intToPrefixCodedBytes
argument_list|(
name|min
argument_list|,
name|shift
argument_list|,
name|minBytes
argument_list|)
expr_stmt|;
name|intToPrefixCodedBytes
argument_list|(
name|max
argument_list|,
name|shift
argument_list|,
name|maxBytes
argument_list|)
expr_stmt|;
name|addRange
argument_list|(
name|minBytes
operator|.
name|get
argument_list|()
argument_list|,
name|maxBytes
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Filters the given {@link TermsEnum} by accepting only prefix coded 64 bit    * terms with a shift value of<tt>0</tt>.    *     * @param termsEnum    *          the terms enum to filter    * @return a filtered {@link TermsEnum} that only returns prefix coded 64 bit    *         terms with a shift value of<tt>0</tt>.    */
DECL|method|filterPrefixCodedLongs
specifier|public
specifier|static
name|TermsEnum
name|filterPrefixCodedLongs
parameter_list|(
name|TermsEnum
name|termsEnum
parameter_list|)
block|{
return|return
operator|new
name|SeekingNumericFilteredTermsEnum
argument_list|(
name|termsEnum
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
return|return
name|LegacyNumericUtils
operator|.
name|getPrefixCodedLongShift
argument_list|(
name|term
argument_list|)
operator|==
literal|0
condition|?
name|AcceptStatus
operator|.
name|YES
else|:
name|AcceptStatus
operator|.
name|END
return|;
block|}
block|}
return|;
block|}
comment|/**    * Filters the given {@link TermsEnum} by accepting only prefix coded 32 bit    * terms with a shift value of<tt>0</tt>.    *     * @param termsEnum    *          the terms enum to filter    * @return a filtered {@link TermsEnum} that only returns prefix coded 32 bit    *         terms with a shift value of<tt>0</tt>.    */
DECL|method|filterPrefixCodedInts
specifier|public
specifier|static
name|TermsEnum
name|filterPrefixCodedInts
parameter_list|(
name|TermsEnum
name|termsEnum
parameter_list|)
block|{
return|return
operator|new
name|SeekingNumericFilteredTermsEnum
argument_list|(
name|termsEnum
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
return|return
name|LegacyNumericUtils
operator|.
name|getPrefixCodedIntShift
argument_list|(
name|term
argument_list|)
operator|==
literal|0
condition|?
name|AcceptStatus
operator|.
name|YES
else|:
name|AcceptStatus
operator|.
name|END
return|;
block|}
block|}
return|;
block|}
comment|/** Just like FilteredTermsEnum, except it adds a limited    *  seekCeil implementation that only works with {@link    *  #filterPrefixCodedInts} and {@link    *  #filterPrefixCodedLongs}. */
DECL|class|SeekingNumericFilteredTermsEnum
specifier|private
specifier|static
specifier|abstract
class|class
name|SeekingNumericFilteredTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|method|SeekingNumericFilteredTermsEnum
specifier|public
name|SeekingNumericFilteredTermsEnum
parameter_list|(
specifier|final
name|TermsEnum
name|tenum
parameter_list|)
block|{
name|super
argument_list|(
name|tenum
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
comment|// NOTE: This is not general!!  It only handles YES
comment|// and END, because that's all we need for the numeric
comment|// case here
name|SeekStatus
name|status
init|=
name|tenum
operator|.
name|seekCeil
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|SeekStatus
operator|.
name|END
condition|)
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
name|actualTerm
operator|=
name|tenum
operator|.
name|term
argument_list|()
expr_stmt|;
if|if
condition|(
name|accept
argument_list|(
name|actualTerm
argument_list|)
operator|==
name|AcceptStatus
operator|.
name|YES
condition|)
block|{
return|return
name|status
return|;
block|}
else|else
block|{
return|return
name|SeekStatus
operator|.
name|END
return|;
block|}
block|}
block|}
DECL|method|intTerms
specifier|private
specifier|static
name|Terms
name|intTerms
parameter_list|(
name|Terms
name|terms
parameter_list|)
block|{
return|return
operator|new
name|FilterLeafReader
operator|.
name|FilterTerms
argument_list|(
name|terms
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|filterPrefixCodedInts
argument_list|(
name|in
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|longTerms
specifier|private
specifier|static
name|Terms
name|longTerms
parameter_list|(
name|Terms
name|terms
parameter_list|)
block|{
return|return
operator|new
name|FilterLeafReader
operator|.
name|FilterTerms
argument_list|(
name|terms
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|filterPrefixCodedLongs
argument_list|(
name|in
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns the minimum int value indexed into this    * numeric field or null if no terms exist.    */
DECL|method|getMinInt
specifier|public
specifier|static
name|Integer
name|getMinInt
parameter_list|(
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
comment|// All shift=0 terms are sorted first, so we don't need
comment|// to filter the incoming terms; we can just get the
comment|// min:
name|BytesRef
name|min
init|=
name|terms
operator|.
name|getMin
argument_list|()
decl_stmt|;
return|return
operator|(
name|min
operator|!=
literal|null
operator|)
condition|?
name|LegacyNumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|min
argument_list|)
else|:
literal|null
return|;
block|}
comment|/**    * Returns the maximum int value indexed into this    * numeric field or null if no terms exist.    */
DECL|method|getMaxInt
specifier|public
specifier|static
name|Integer
name|getMaxInt
parameter_list|(
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|max
init|=
name|intTerms
argument_list|(
name|terms
argument_list|)
operator|.
name|getMax
argument_list|()
decl_stmt|;
return|return
operator|(
name|max
operator|!=
literal|null
operator|)
condition|?
name|LegacyNumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|max
argument_list|)
else|:
literal|null
return|;
block|}
comment|/**    * Returns the minimum long value indexed into this    * numeric field or null if no terms exist.    */
DECL|method|getMinLong
specifier|public
specifier|static
name|Long
name|getMinLong
parameter_list|(
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
comment|// All shift=0 terms are sorted first, so we don't need
comment|// to filter the incoming terms; we can just get the
comment|// min:
name|BytesRef
name|min
init|=
name|terms
operator|.
name|getMin
argument_list|()
decl_stmt|;
return|return
operator|(
name|min
operator|!=
literal|null
operator|)
condition|?
name|LegacyNumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|min
argument_list|)
else|:
literal|null
return|;
block|}
comment|/**    * Returns the maximum long value indexed into this    * numeric field or null if no terms exist.    */
DECL|method|getMaxLong
specifier|public
specifier|static
name|Long
name|getMaxLong
parameter_list|(
name|Terms
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesRef
name|max
init|=
name|longTerms
argument_list|(
name|terms
argument_list|)
operator|.
name|getMax
argument_list|()
decl_stmt|;
return|return
operator|(
name|max
operator|!=
literal|null
operator|)
condition|?
name|LegacyNumericUtils
operator|.
name|prefixCodedToLong
argument_list|(
name|max
argument_list|)
else|:
literal|null
return|;
block|}
block|}
end_class
end_unit
