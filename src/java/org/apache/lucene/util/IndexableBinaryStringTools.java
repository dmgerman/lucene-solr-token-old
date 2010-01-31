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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|CharBuffer
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
begin_comment
comment|/**  * Provides support for converting byte sequences to Strings and back again.  * The resulting Strings preserve the original byte sequences' sort order.  *<p/>  * The Strings are constructed using a Base 8000h encoding of the original  * binary data - each char of an encoded String represents a 15-bit chunk  * from the byte sequence.  Base 8000h was chosen because it allows for all  * lower 15 bits of char to be used without restriction; the surrogate range   * [U+D8000-U+DFFF] does not represent valid chars, and would require  * complicated handling to avoid them and allow use of char's high bit.  *<p/>  * Although unset bits are used as padding in the final char, the original  * byte sequence could contain trailing bytes with no set bits (null bytes):  * padding is indistinguishable from valid information.  To overcome this  * problem, a char is appended, indicating the number of encoded bytes in the  * final content char.  *<p/>  * Some methods in this class are defined over CharBuffers and ByteBuffers, but  * these are deprecated in favor of methods that operate directly on byte[] and  * char[] arrays.  Note that this class calls array() and arrayOffset()  * on the CharBuffers and ByteBuffers it uses, so only wrapped arrays may be  * used.  This class interprets the arrayOffset() and limit() values returned   * by its input buffers as beginning and end+1 positions on the wrapped array,  * respectively; similarly, on the output buffer, arrayOffset() is the first  * position written to, and limit() is set to one past the final output array  * position.  *<p/>  * WARNING: This means that the deprecated Buffer-based methods   * only work correctly with buffers that have an offset of 0. For example, they  * will not correctly interpret buffers returned by {@link ByteBuffer#slice}.    *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|IndexableBinaryStringTools
specifier|public
class|class
name|IndexableBinaryStringTools
block|{
DECL|field|CODING_CASES
specifier|private
specifier|static
specifier|final
name|CodingCase
index|[]
name|CODING_CASES
init|=
block|{
comment|// CodingCase(int initialShift, int finalShift)
operator|new
name|CodingCase
argument_list|(
literal|7
argument_list|,
literal|1
argument_list|)
block|,
comment|// CodingCase(int initialShift, int middleShift, int finalShift)
operator|new
name|CodingCase
argument_list|(
literal|14
argument_list|,
literal|6
argument_list|,
literal|2
argument_list|)
block|,
operator|new
name|CodingCase
argument_list|(
literal|13
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|)
block|,
operator|new
name|CodingCase
argument_list|(
literal|12
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|)
block|,
operator|new
name|CodingCase
argument_list|(
literal|11
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
block|,
operator|new
name|CodingCase
argument_list|(
literal|10
argument_list|,
literal|2
argument_list|,
literal|6
argument_list|)
block|,
operator|new
name|CodingCase
argument_list|(
literal|9
argument_list|,
literal|1
argument_list|,
literal|7
argument_list|)
block|,
operator|new
name|CodingCase
argument_list|(
literal|8
argument_list|,
literal|0
argument_list|)
block|}
decl_stmt|;
comment|// Export only static methods
DECL|method|IndexableBinaryStringTools
specifier|private
name|IndexableBinaryStringTools
parameter_list|()
block|{}
comment|/**    * Returns the number of chars required to encode the given byte sequence.    *     * @param original The byte sequence to be encoded. Must be backed by an    *        array.    * @return The number of chars required to encode the given byte sequence    * @throws IllegalArgumentException If the given ByteBuffer is not backed by    *         an array    * @deprecated Use {@link #getEncodedLength(byte[], int, int)} instead. This    *             method will be removed in Lucene 4.0    */
annotation|@
name|Deprecated
DECL|method|getEncodedLength
specifier|public
specifier|static
name|int
name|getEncodedLength
parameter_list|(
name|ByteBuffer
name|original
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|original
operator|.
name|hasArray
argument_list|()
condition|)
block|{
return|return
name|getEncodedLength
argument_list|(
name|original
operator|.
name|array
argument_list|()
argument_list|,
name|original
operator|.
name|arrayOffset
argument_list|()
argument_list|,
name|original
operator|.
name|limit
argument_list|()
operator|-
name|original
operator|.
name|arrayOffset
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"original argument must have a backing array"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns the number of chars required to encode the given bytes.    *     * @param inputArray byte sequence to be encoded    * @param inputOffset initial offset into inputArray    * @param inputLength number of bytes in inputArray    * @return The number of chars required to encode the number of bytes.    */
DECL|method|getEncodedLength
specifier|public
specifier|static
name|int
name|getEncodedLength
parameter_list|(
name|byte
index|[]
name|inputArray
parameter_list|,
name|int
name|inputOffset
parameter_list|,
name|int
name|inputLength
parameter_list|)
block|{
comment|// Use long for intermediaries to protect against overflow
return|return
call|(
name|int
call|)
argument_list|(
operator|(
operator|(
name|long
operator|)
name|inputLength
operator|*
literal|8L
operator|+
literal|14L
operator|)
operator|/
literal|15L
argument_list|)
operator|+
literal|1
return|;
block|}
comment|/**    * Returns the number of bytes required to decode the given char sequence.    *     * @param encoded The char sequence to be decoded. Must be backed by an array.    * @return The number of bytes required to decode the given char sequence    * @throws IllegalArgumentException If the given CharBuffer is not backed by    *         an array    * @deprecated Use {@link #getDecodedLength(char[], int, int)} instead. This    *             method will be removed in Lucene 4.0    */
annotation|@
name|Deprecated
DECL|method|getDecodedLength
specifier|public
specifier|static
name|int
name|getDecodedLength
parameter_list|(
name|CharBuffer
name|encoded
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|encoded
operator|.
name|hasArray
argument_list|()
condition|)
block|{
return|return
name|getDecodedLength
argument_list|(
name|encoded
operator|.
name|array
argument_list|()
argument_list|,
name|encoded
operator|.
name|arrayOffset
argument_list|()
argument_list|,
name|encoded
operator|.
name|limit
argument_list|()
operator|-
name|encoded
operator|.
name|arrayOffset
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"encoded argument must have a backing array"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns the number of bytes required to decode the given char sequence.    *     * @param encoded char sequence to be decoded    * @param offset initial offset    * @param length number of characters    * @return The number of bytes required to decode the given char sequence    */
DECL|method|getDecodedLength
specifier|public
specifier|static
name|int
name|getDecodedLength
parameter_list|(
name|char
index|[]
name|encoded
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
specifier|final
name|int
name|numChars
init|=
name|length
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|numChars
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
comment|// Use long for intermediaries to protect against overflow
specifier|final
name|long
name|numFullBytesInFinalChar
init|=
name|encoded
index|[
name|offset
operator|+
name|length
operator|-
literal|1
index|]
decl_stmt|;
specifier|final
name|long
name|numEncodedChars
init|=
name|numChars
operator|-
literal|1
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
operator|(
name|numEncodedChars
operator|*
literal|15L
operator|+
literal|7L
operator|)
operator|/
literal|8L
operator|+
name|numFullBytesInFinalChar
argument_list|)
return|;
block|}
block|}
comment|/**    * Encodes the input byte sequence into the output char sequence. Before    * calling this method, ensure that the output CharBuffer has sufficient    * capacity by calling {@link #getEncodedLength(java.nio.ByteBuffer)}.    *     * @param input The byte sequence to encode    * @param output Where the char sequence encoding result will go. The limit is    *        set to one past the position of the final char.    * @throws IllegalArgumentException If either the input or the output buffer    *         is not backed by an array    * @deprecated Use {@link #encode(byte[], int, int, char[], int, int)}    *             instead. This method will be removed in Lucene 4.0    */
annotation|@
name|Deprecated
DECL|method|encode
specifier|public
specifier|static
name|void
name|encode
parameter_list|(
name|ByteBuffer
name|input
parameter_list|,
name|CharBuffer
name|output
parameter_list|)
block|{
if|if
condition|(
name|input
operator|.
name|hasArray
argument_list|()
operator|&&
name|output
operator|.
name|hasArray
argument_list|()
condition|)
block|{
specifier|final
name|int
name|inputOffset
init|=
name|input
operator|.
name|arrayOffset
argument_list|()
decl_stmt|;
specifier|final
name|int
name|inputLength
init|=
name|input
operator|.
name|limit
argument_list|()
operator|-
name|inputOffset
decl_stmt|;
specifier|final
name|int
name|outputOffset
init|=
name|output
operator|.
name|arrayOffset
argument_list|()
decl_stmt|;
specifier|final
name|int
name|outputLength
init|=
name|getEncodedLength
argument_list|(
name|input
operator|.
name|array
argument_list|()
argument_list|,
name|inputOffset
argument_list|,
name|inputLength
argument_list|)
decl_stmt|;
name|output
operator|.
name|limit
argument_list|(
name|outputLength
operator|+
name|outputOffset
argument_list|)
expr_stmt|;
name|output
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|encode
argument_list|(
name|input
operator|.
name|array
argument_list|()
argument_list|,
name|inputOffset
argument_list|,
name|inputLength
argument_list|,
name|output
operator|.
name|array
argument_list|()
argument_list|,
name|outputOffset
argument_list|,
name|outputLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Arguments must have backing arrays"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Encodes the input byte sequence into the output char sequence.  Before    * calling this method, ensure that the output array has sufficient    * capacity by calling {@link #getEncodedLength(byte[], int, int)}.    *     * @param inputArray byte sequence to be encoded    * @param inputOffset initial offset into inputArray    * @param inputLength number of bytes in inputArray    * @param outputArray char sequence to store encoded result    * @param outputOffset initial offset into outputArray    * @param outputLength length of output, must be getEncodedLength    */
DECL|method|encode
specifier|public
specifier|static
name|void
name|encode
parameter_list|(
name|byte
index|[]
name|inputArray
parameter_list|,
name|int
name|inputOffset
parameter_list|,
name|int
name|inputLength
parameter_list|,
name|char
index|[]
name|outputArray
parameter_list|,
name|int
name|outputOffset
parameter_list|,
name|int
name|outputLength
parameter_list|)
block|{
assert|assert
operator|(
name|outputLength
operator|==
name|getEncodedLength
argument_list|(
name|inputArray
argument_list|,
name|inputOffset
argument_list|,
name|inputLength
argument_list|)
operator|)
assert|;
if|if
condition|(
name|inputLength
operator|>
literal|0
condition|)
block|{
name|int
name|inputByteNum
init|=
name|inputOffset
decl_stmt|;
name|int
name|caseNum
init|=
literal|0
decl_stmt|;
name|int
name|outputCharNum
init|=
name|outputOffset
decl_stmt|;
name|CodingCase
name|codingCase
decl_stmt|;
for|for
control|(
init|;
name|inputByteNum
operator|+
name|CODING_CASES
index|[
name|caseNum
index|]
operator|.
name|numBytes
operator|<=
name|inputLength
condition|;
operator|++
name|outputCharNum
control|)
block|{
name|codingCase
operator|=
name|CODING_CASES
index|[
name|caseNum
index|]
expr_stmt|;
if|if
condition|(
literal|2
operator|==
name|codingCase
operator|.
name|numBytes
condition|)
block|{
name|outputArray
index|[
name|outputCharNum
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|inputArray
index|[
name|inputByteNum
index|]
operator|&
literal|0xFF
operator|)
operator|<<
name|codingCase
operator|.
name|initialShift
operator|)
operator|+
operator|(
operator|(
operator|(
name|inputArray
index|[
name|inputByteNum
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|>>>
name|codingCase
operator|.
name|finalShift
operator|)
operator|&
name|codingCase
operator|.
name|finalMask
operator|)
operator|&
operator|(
name|short
operator|)
literal|0x7FFF
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// numBytes is 3
name|outputArray
index|[
name|outputCharNum
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|inputArray
index|[
name|inputByteNum
index|]
operator|&
literal|0xFF
operator|)
operator|<<
name|codingCase
operator|.
name|initialShift
operator|)
operator|+
operator|(
operator|(
name|inputArray
index|[
name|inputByteNum
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|<<
name|codingCase
operator|.
name|middleShift
operator|)
operator|+
operator|(
operator|(
operator|(
name|inputArray
index|[
name|inputByteNum
operator|+
literal|2
index|]
operator|&
literal|0xFF
operator|)
operator|>>>
name|codingCase
operator|.
name|finalShift
operator|)
operator|&
name|codingCase
operator|.
name|finalMask
operator|)
operator|&
operator|(
name|short
operator|)
literal|0x7FFF
argument_list|)
expr_stmt|;
block|}
name|inputByteNum
operator|+=
name|codingCase
operator|.
name|advanceBytes
expr_stmt|;
if|if
condition|(
operator|++
name|caseNum
operator|==
name|CODING_CASES
operator|.
name|length
condition|)
block|{
name|caseNum
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|// Produce final char (if any) and trailing count chars.
name|codingCase
operator|=
name|CODING_CASES
index|[
name|caseNum
index|]
expr_stmt|;
if|if
condition|(
name|inputByteNum
operator|+
literal|1
operator|<
name|inputLength
condition|)
block|{
comment|// codingCase.numBytes must be 3
name|outputArray
index|[
name|outputCharNum
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
operator|(
name|inputArray
index|[
name|inputByteNum
index|]
operator|&
literal|0xFF
operator|)
operator|<<
name|codingCase
operator|.
name|initialShift
operator|)
operator|+
operator|(
operator|(
name|inputArray
index|[
name|inputByteNum
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|<<
name|codingCase
operator|.
name|middleShift
operator|)
operator|)
operator|&
operator|(
name|short
operator|)
literal|0x7FFF
argument_list|)
expr_stmt|;
comment|// Add trailing char containing the number of full bytes in final char
name|outputArray
index|[
name|outputCharNum
operator|++
index|]
operator|=
operator|(
name|char
operator|)
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inputByteNum
operator|<
name|inputLength
condition|)
block|{
name|outputArray
index|[
name|outputCharNum
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|inputArray
index|[
name|inputByteNum
index|]
operator|&
literal|0xFF
operator|)
operator|<<
name|codingCase
operator|.
name|initialShift
operator|)
operator|&
operator|(
name|short
operator|)
literal|0x7FFF
argument_list|)
expr_stmt|;
comment|// Add trailing char containing the number of full bytes in final char
name|outputArray
index|[
name|outputCharNum
operator|++
index|]
operator|=
name|caseNum
operator|==
literal|0
condition|?
operator|(
name|char
operator|)
literal|1
else|:
operator|(
name|char
operator|)
literal|0
expr_stmt|;
block|}
else|else
block|{
comment|// No left over bits - last char is completely filled.
comment|// Add trailing char containing the number of full bytes in final char
name|outputArray
index|[
name|outputCharNum
operator|++
index|]
operator|=
operator|(
name|char
operator|)
literal|1
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Decodes the input char sequence into the output byte sequence. Before    * calling this method, ensure that the output ByteBuffer has sufficient    * capacity by calling {@link #getDecodedLength(java.nio.CharBuffer)}.    *     * @param input The char sequence to decode    * @param output Where the byte sequence decoding result will go. The limit is    *        set to one past the position of the final char.    * @throws IllegalArgumentException If either the input or the output buffer    *         is not backed by an array    * @deprecated Use {@link #decode(char[], int, int, byte[], int, int)}    *             instead. This method will be removed in Lucene 4.0    */
annotation|@
name|Deprecated
DECL|method|decode
specifier|public
specifier|static
name|void
name|decode
parameter_list|(
name|CharBuffer
name|input
parameter_list|,
name|ByteBuffer
name|output
parameter_list|)
block|{
if|if
condition|(
name|input
operator|.
name|hasArray
argument_list|()
operator|&&
name|output
operator|.
name|hasArray
argument_list|()
condition|)
block|{
specifier|final
name|int
name|inputOffset
init|=
name|input
operator|.
name|arrayOffset
argument_list|()
decl_stmt|;
specifier|final
name|int
name|inputLength
init|=
name|input
operator|.
name|limit
argument_list|()
operator|-
name|inputOffset
decl_stmt|;
specifier|final
name|int
name|outputOffset
init|=
name|output
operator|.
name|arrayOffset
argument_list|()
decl_stmt|;
specifier|final
name|int
name|outputLength
init|=
name|getDecodedLength
argument_list|(
name|input
operator|.
name|array
argument_list|()
argument_list|,
name|inputOffset
argument_list|,
name|inputLength
argument_list|)
decl_stmt|;
name|output
operator|.
name|limit
argument_list|(
name|outputLength
operator|+
name|outputOffset
argument_list|)
expr_stmt|;
name|output
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|decode
argument_list|(
name|input
operator|.
name|array
argument_list|()
argument_list|,
name|inputOffset
argument_list|,
name|inputLength
argument_list|,
name|output
operator|.
name|array
argument_list|()
argument_list|,
name|outputOffset
argument_list|,
name|outputLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Arguments must have backing arrays"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Decodes the input char sequence into the output byte sequence. Before    * calling this method, ensure that the output array has sufficient capacity    * by calling {@link #getDecodedLength(char[], int, int)}.    *     * @param inputArray char sequence to be decoded    * @param inputOffset initial offset into inputArray    * @param inputLength number of chars in inputArray    * @param outputArray byte sequence to store encoded result    * @param outputOffset initial offset into outputArray    * @param outputLength length of output, must be    *        getDecodedLength(inputArray, inputOffset, inputLength)    */
DECL|method|decode
specifier|public
specifier|static
name|void
name|decode
parameter_list|(
name|char
index|[]
name|inputArray
parameter_list|,
name|int
name|inputOffset
parameter_list|,
name|int
name|inputLength
parameter_list|,
name|byte
index|[]
name|outputArray
parameter_list|,
name|int
name|outputOffset
parameter_list|,
name|int
name|outputLength
parameter_list|)
block|{
assert|assert
operator|(
name|outputLength
operator|==
name|getDecodedLength
argument_list|(
name|inputArray
argument_list|,
name|inputOffset
argument_list|,
name|inputLength
argument_list|)
operator|)
assert|;
specifier|final
name|int
name|numInputChars
init|=
name|inputLength
operator|-
literal|1
decl_stmt|;
specifier|final
name|int
name|numOutputBytes
init|=
name|outputLength
decl_stmt|;
if|if
condition|(
name|numOutputBytes
operator|>
literal|0
condition|)
block|{
name|int
name|caseNum
init|=
literal|0
decl_stmt|;
name|int
name|outputByteNum
init|=
name|outputOffset
decl_stmt|;
name|int
name|inputCharNum
init|=
name|inputOffset
decl_stmt|;
name|short
name|inputChar
decl_stmt|;
name|CodingCase
name|codingCase
decl_stmt|;
for|for
control|(
init|;
name|inputCharNum
operator|<
name|numInputChars
operator|-
literal|1
condition|;
operator|++
name|inputCharNum
control|)
block|{
name|codingCase
operator|=
name|CODING_CASES
index|[
name|caseNum
index|]
expr_stmt|;
name|inputChar
operator|=
operator|(
name|short
operator|)
name|inputArray
index|[
name|inputCharNum
index|]
expr_stmt|;
if|if
condition|(
literal|2
operator|==
name|codingCase
operator|.
name|numBytes
condition|)
block|{
if|if
condition|(
literal|0
operator|==
name|caseNum
condition|)
block|{
name|outputArray
index|[
name|outputByteNum
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|inputChar
operator|>>>
name|codingCase
operator|.
name|initialShift
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|outputArray
index|[
name|outputByteNum
index|]
operator|+=
call|(
name|byte
call|)
argument_list|(
name|inputChar
operator|>>>
name|codingCase
operator|.
name|initialShift
argument_list|)
expr_stmt|;
block|}
name|outputArray
index|[
name|outputByteNum
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|inputChar
operator|&
name|codingCase
operator|.
name|finalMask
operator|)
operator|<<
name|codingCase
operator|.
name|finalShift
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// numBytes is 3
name|outputArray
index|[
name|outputByteNum
index|]
operator|+=
call|(
name|byte
call|)
argument_list|(
name|inputChar
operator|>>>
name|codingCase
operator|.
name|initialShift
argument_list|)
expr_stmt|;
name|outputArray
index|[
name|outputByteNum
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|inputChar
operator|&
name|codingCase
operator|.
name|middleMask
operator|)
operator|>>>
name|codingCase
operator|.
name|middleShift
argument_list|)
expr_stmt|;
name|outputArray
index|[
name|outputByteNum
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|inputChar
operator|&
name|codingCase
operator|.
name|finalMask
operator|)
operator|<<
name|codingCase
operator|.
name|finalShift
argument_list|)
expr_stmt|;
block|}
name|outputByteNum
operator|+=
name|codingCase
operator|.
name|advanceBytes
expr_stmt|;
if|if
condition|(
operator|++
name|caseNum
operator|==
name|CODING_CASES
operator|.
name|length
condition|)
block|{
name|caseNum
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|// Handle final char
name|inputChar
operator|=
operator|(
name|short
operator|)
name|inputArray
index|[
name|inputCharNum
index|]
expr_stmt|;
name|codingCase
operator|=
name|CODING_CASES
index|[
name|caseNum
index|]
expr_stmt|;
if|if
condition|(
literal|0
operator|==
name|caseNum
condition|)
block|{
name|outputArray
index|[
name|outputByteNum
index|]
operator|=
literal|0
expr_stmt|;
block|}
name|outputArray
index|[
name|outputByteNum
index|]
operator|+=
call|(
name|byte
call|)
argument_list|(
name|inputChar
operator|>>>
name|codingCase
operator|.
name|initialShift
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bytesLeft
init|=
name|numOutputBytes
operator|-
name|outputByteNum
decl_stmt|;
if|if
condition|(
name|bytesLeft
operator|>
literal|1
condition|)
block|{
if|if
condition|(
literal|2
operator|==
name|codingCase
operator|.
name|numBytes
condition|)
block|{
name|outputArray
index|[
name|outputByteNum
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|inputChar
operator|&
name|codingCase
operator|.
name|finalMask
operator|)
operator|>>>
name|codingCase
operator|.
name|finalShift
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// numBytes is 3
name|outputArray
index|[
name|outputByteNum
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|inputChar
operator|&
name|codingCase
operator|.
name|middleMask
operator|)
operator|>>>
name|codingCase
operator|.
name|middleShift
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytesLeft
operator|>
literal|2
condition|)
block|{
name|outputArray
index|[
name|outputByteNum
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|inputChar
operator|&
name|codingCase
operator|.
name|finalMask
operator|)
operator|<<
name|codingCase
operator|.
name|finalShift
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Decodes the given char sequence, which must have been encoded by    * {@link #encode(java.nio.ByteBuffer)} or    * {@link #encode(java.nio.ByteBuffer, java.nio.CharBuffer)}.    *     * @param input The char sequence to decode    * @return A byte sequence containing the decoding result. The limit is set to    *         one past the position of the final char.    * @throws IllegalArgumentException If the input buffer is not backed by an    *         array    * @deprecated Use {@link #decode(char[], int, int, byte[], int, int)}    *             instead. This method will be removed in Lucene 4.0    */
annotation|@
name|Deprecated
DECL|method|decode
specifier|public
specifier|static
name|ByteBuffer
name|decode
parameter_list|(
name|CharBuffer
name|input
parameter_list|)
block|{
name|byte
index|[]
name|outputArray
init|=
operator|new
name|byte
index|[
name|getDecodedLength
argument_list|(
name|input
argument_list|)
index|]
decl_stmt|;
name|ByteBuffer
name|output
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|outputArray
argument_list|)
decl_stmt|;
name|decode
argument_list|(
name|input
argument_list|,
name|output
argument_list|)
expr_stmt|;
return|return
name|output
return|;
block|}
comment|/**    * Encodes the input byte sequence.    *     * @param input The byte sequence to encode    * @return A char sequence containing the encoding result. The limit is set to    *         one past the position of the final char.    * @throws IllegalArgumentException If the input buffer is not backed by an    *         array    * @deprecated Use {@link #encode(byte[], int, int, char[], int, int)}    *             instead. This method will be removed in Lucene 4.0    */
annotation|@
name|Deprecated
DECL|method|encode
specifier|public
specifier|static
name|CharBuffer
name|encode
parameter_list|(
name|ByteBuffer
name|input
parameter_list|)
block|{
name|char
index|[]
name|outputArray
init|=
operator|new
name|char
index|[
name|getEncodedLength
argument_list|(
name|input
argument_list|)
index|]
decl_stmt|;
name|CharBuffer
name|output
init|=
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|outputArray
argument_list|)
decl_stmt|;
name|encode
argument_list|(
name|input
argument_list|,
name|output
argument_list|)
expr_stmt|;
return|return
name|output
return|;
block|}
DECL|class|CodingCase
specifier|static
class|class
name|CodingCase
block|{
DECL|field|numBytes
DECL|field|initialShift
DECL|field|middleShift
DECL|field|finalShift
DECL|field|advanceBytes
name|int
name|numBytes
decl_stmt|,
name|initialShift
decl_stmt|,
name|middleShift
decl_stmt|,
name|finalShift
decl_stmt|,
name|advanceBytes
init|=
literal|2
decl_stmt|;
DECL|field|middleMask
DECL|field|finalMask
name|short
name|middleMask
decl_stmt|,
name|finalMask
decl_stmt|;
DECL|method|CodingCase
name|CodingCase
parameter_list|(
name|int
name|initialShift
parameter_list|,
name|int
name|middleShift
parameter_list|,
name|int
name|finalShift
parameter_list|)
block|{
name|this
operator|.
name|numBytes
operator|=
literal|3
expr_stmt|;
name|this
operator|.
name|initialShift
operator|=
name|initialShift
expr_stmt|;
name|this
operator|.
name|middleShift
operator|=
name|middleShift
expr_stmt|;
name|this
operator|.
name|finalShift
operator|=
name|finalShift
expr_stmt|;
name|this
operator|.
name|finalMask
operator|=
call|(
name|short
call|)
argument_list|(
operator|(
name|short
operator|)
literal|0xFF
operator|>>>
name|finalShift
argument_list|)
expr_stmt|;
name|this
operator|.
name|middleMask
operator|=
call|(
name|short
call|)
argument_list|(
operator|(
name|short
operator|)
literal|0xFF
operator|<<
name|middleShift
argument_list|)
expr_stmt|;
block|}
DECL|method|CodingCase
name|CodingCase
parameter_list|(
name|int
name|initialShift
parameter_list|,
name|int
name|finalShift
parameter_list|)
block|{
name|this
operator|.
name|numBytes
operator|=
literal|2
expr_stmt|;
name|this
operator|.
name|initialShift
operator|=
name|initialShift
expr_stmt|;
name|this
operator|.
name|finalShift
operator|=
name|finalShift
expr_stmt|;
name|this
operator|.
name|finalMask
operator|=
call|(
name|short
call|)
argument_list|(
operator|(
name|short
operator|)
literal|0xFF
operator|>>>
name|finalShift
argument_list|)
expr_stmt|;
if|if
condition|(
name|finalShift
operator|!=
literal|0
condition|)
block|{
name|advanceBytes
operator|=
literal|1
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
