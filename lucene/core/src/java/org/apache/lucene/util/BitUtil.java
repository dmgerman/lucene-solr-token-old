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
begin_comment
DECL|package|org.apache.lucene.util
comment|// from org.apache.solr.util rev 555343
end_comment
begin_comment
comment|/**  A variety of high efficiency bit twiddling routines.  * @lucene.internal  */
end_comment
begin_class
DECL|class|BitUtil
specifier|public
specifier|final
class|class
name|BitUtil
block|{
DECL|method|BitUtil
specifier|private
name|BitUtil
parameter_list|()
block|{}
comment|// no instance
comment|// The pop methods used to rely on bit-manipulation tricks for speed but it
comment|// turns out that it is faster to use the Long.bitCount method (which is an
comment|// intrinsic since Java 6u18) in a naive loop, see LUCENE-2221
comment|/** Returns the number of set bits in an array of longs. */
DECL|method|pop_array
specifier|public
specifier|static
name|long
name|pop_array
parameter_list|(
name|long
index|[]
name|arr
parameter_list|,
name|int
name|wordOffset
parameter_list|,
name|int
name|numWords
parameter_list|)
block|{
name|long
name|popCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|wordOffset
init|,
name|end
init|=
name|wordOffset
operator|+
name|numWords
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
name|popCount
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|arr
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|popCount
return|;
block|}
comment|/** Returns the popcount or cardinality of the two sets after an intersection.    *  Neither array is modified. */
DECL|method|pop_intersect
specifier|public
specifier|static
name|long
name|pop_intersect
parameter_list|(
name|long
index|[]
name|arr1
parameter_list|,
name|long
index|[]
name|arr2
parameter_list|,
name|int
name|wordOffset
parameter_list|,
name|int
name|numWords
parameter_list|)
block|{
name|long
name|popCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|wordOffset
init|,
name|end
init|=
name|wordOffset
operator|+
name|numWords
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
name|popCount
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|arr1
index|[
name|i
index|]
operator|&
name|arr2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|popCount
return|;
block|}
comment|/** Returns the popcount or cardinality of the union of two sets.     *  Neither array is modified. */
DECL|method|pop_union
specifier|public
specifier|static
name|long
name|pop_union
parameter_list|(
name|long
index|[]
name|arr1
parameter_list|,
name|long
index|[]
name|arr2
parameter_list|,
name|int
name|wordOffset
parameter_list|,
name|int
name|numWords
parameter_list|)
block|{
name|long
name|popCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|wordOffset
init|,
name|end
init|=
name|wordOffset
operator|+
name|numWords
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
name|popCount
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|arr1
index|[
name|i
index|]
operator||
name|arr2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|popCount
return|;
block|}
comment|/** Returns the popcount or cardinality of {@code A& ~B}.    *  Neither array is modified. */
DECL|method|pop_andnot
specifier|public
specifier|static
name|long
name|pop_andnot
parameter_list|(
name|long
index|[]
name|arr1
parameter_list|,
name|long
index|[]
name|arr2
parameter_list|,
name|int
name|wordOffset
parameter_list|,
name|int
name|numWords
parameter_list|)
block|{
name|long
name|popCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|wordOffset
init|,
name|end
init|=
name|wordOffset
operator|+
name|numWords
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
name|popCount
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|arr1
index|[
name|i
index|]
operator|&
operator|~
name|arr2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|popCount
return|;
block|}
comment|/** Returns the popcount or cardinality of A ^ B     * Neither array is modified. */
DECL|method|pop_xor
specifier|public
specifier|static
name|long
name|pop_xor
parameter_list|(
name|long
index|[]
name|arr1
parameter_list|,
name|long
index|[]
name|arr2
parameter_list|,
name|int
name|wordOffset
parameter_list|,
name|int
name|numWords
parameter_list|)
block|{
name|long
name|popCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|wordOffset
init|,
name|end
init|=
name|wordOffset
operator|+
name|numWords
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
name|popCount
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|arr1
index|[
name|i
index|]
operator|^
name|arr2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|popCount
return|;
block|}
comment|/** returns the next highest power of two, or the current value if it's already a power of two or zero*/
DECL|method|nextHighestPowerOfTwo
specifier|public
specifier|static
name|int
name|nextHighestPowerOfTwo
parameter_list|(
name|int
name|v
parameter_list|)
block|{
name|v
operator|--
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|1
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|2
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|4
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|8
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|16
expr_stmt|;
name|v
operator|++
expr_stmt|;
return|return
name|v
return|;
block|}
comment|/** returns the next highest power of two, or the current value if it's already a power of two or zero*/
DECL|method|nextHighestPowerOfTwo
specifier|public
specifier|static
name|long
name|nextHighestPowerOfTwo
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|v
operator|--
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|1
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|2
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|4
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|8
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|16
expr_stmt|;
name|v
operator||=
name|v
operator|>>
literal|32
expr_stmt|;
name|v
operator|++
expr_stmt|;
return|return
name|v
return|;
block|}
comment|/** Same as {@link #zigZagEncode(long)} but on integers. */
DECL|method|zigZagEncode
specifier|public
specifier|static
name|int
name|zigZagEncode
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
name|i
operator|>>
literal|31
operator|)
operator|^
operator|(
name|i
operator|<<
literal|1
operator|)
return|;
block|}
comment|/**     *<a href="https://developers.google.com/protocol-buffers/docs/encoding#types">Zig-zag</a>     * encode the provided long. Assuming the input is a signed long whose     * absolute value can be stored on<tt>n</tt> bits, the returned value will     * be an unsigned long that can be stored on<tt>n+1</tt> bits.     */
DECL|method|zigZagEncode
specifier|public
specifier|static
name|long
name|zigZagEncode
parameter_list|(
name|long
name|l
parameter_list|)
block|{
return|return
operator|(
name|l
operator|>>
literal|63
operator|)
operator|^
operator|(
name|l
operator|<<
literal|1
operator|)
return|;
block|}
comment|/** Decode an int previously encoded with {@link #zigZagEncode(int)}. */
DECL|method|zigZagDecode
specifier|public
specifier|static
name|int
name|zigZagDecode
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
operator|(
name|i
operator|>>>
literal|1
operator|)
operator|^
operator|-
operator|(
name|i
operator|&
literal|1
operator|)
operator|)
return|;
block|}
comment|/** Decode a long previously encoded with {@link #zigZagEncode(long)}. */
DECL|method|zigZagDecode
specifier|public
specifier|static
name|long
name|zigZagDecode
parameter_list|(
name|long
name|l
parameter_list|)
block|{
return|return
operator|(
operator|(
name|l
operator|>>>
literal|1
operator|)
operator|^
operator|-
operator|(
name|l
operator|&
literal|1
operator|)
operator|)
return|;
block|}
block|}
end_class
end_unit
