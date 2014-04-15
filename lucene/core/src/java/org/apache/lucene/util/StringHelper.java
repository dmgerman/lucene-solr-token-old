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
name|util
operator|.
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import
begin_comment
comment|/**  * Methods for manipulating strings.  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|StringHelper
specifier|public
specifier|abstract
class|class
name|StringHelper
block|{
comment|/**    * Compares two {@link BytesRef}, element by element, and returns the    * number of elements common to both arrays.    *    * @param left The first {@link BytesRef} to compare    * @param right The second {@link BytesRef} to compare    * @return The number of common elements.    */
DECL|method|bytesDifference
specifier|public
specifier|static
name|int
name|bytesDifference
parameter_list|(
name|BytesRef
name|left
parameter_list|,
name|BytesRef
name|right
parameter_list|)
block|{
name|int
name|len
init|=
name|left
operator|.
name|length
operator|<
name|right
operator|.
name|length
condition|?
name|left
operator|.
name|length
else|:
name|right
operator|.
name|length
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytesLeft
init|=
name|left
operator|.
name|bytes
decl_stmt|;
specifier|final
name|int
name|offLeft
init|=
name|left
operator|.
name|offset
decl_stmt|;
name|byte
index|[]
name|bytesRight
init|=
name|right
operator|.
name|bytes
decl_stmt|;
specifier|final
name|int
name|offRight
init|=
name|right
operator|.
name|offset
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
if|if
condition|(
name|bytesLeft
index|[
name|i
operator|+
name|offLeft
index|]
operator|!=
name|bytesRight
index|[
name|i
operator|+
name|offRight
index|]
condition|)
return|return
name|i
return|;
return|return
name|len
return|;
block|}
DECL|method|StringHelper
specifier|private
name|StringHelper
parameter_list|()
block|{   }
comment|/**    * @return a Comparator over versioned strings such as X.YY.Z    * @lucene.internal    */
DECL|method|getVersionComparator
specifier|public
specifier|static
name|Comparator
argument_list|<
name|String
argument_list|>
name|getVersionComparator
parameter_list|()
block|{
return|return
name|versionComparator
return|;
block|}
DECL|field|versionComparator
specifier|private
specifier|static
name|Comparator
argument_list|<
name|String
argument_list|>
name|versionComparator
init|=
operator|new
name|Comparator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|a
parameter_list|,
name|String
name|b
parameter_list|)
block|{
name|StringTokenizer
name|aTokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|a
argument_list|,
literal|"."
argument_list|)
decl_stmt|;
name|StringTokenizer
name|bTokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|b
argument_list|,
literal|"."
argument_list|)
decl_stmt|;
while|while
condition|(
name|aTokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|int
name|aToken
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|aTokens
operator|.
name|nextToken
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|bTokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|int
name|bToken
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|bTokens
operator|.
name|nextToken
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aToken
operator|!=
name|bToken
condition|)
block|{
return|return
name|aToken
operator|<
name|bToken
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
block|}
else|else
block|{
comment|// a has some extra trailing tokens. if these are all zeroes, thats ok.
if|if
condition|(
name|aToken
operator|!=
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
comment|// b has some extra trailing tokens. if these are all zeroes, thats ok.
while|while
condition|(
name|bTokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
if|if
condition|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|bTokens
operator|.
name|nextToken
argument_list|()
argument_list|)
operator|!=
literal|0
condition|)
return|return
operator|-
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
decl_stmt|;
DECL|method|equals
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
if|if
condition|(
name|s1
operator|==
literal|null
condition|)
block|{
return|return
name|s2
operator|==
literal|null
return|;
block|}
else|else
block|{
return|return
name|s1
operator|.
name|equals
argument_list|(
name|s2
argument_list|)
return|;
block|}
block|}
comment|/**    * Returns<code>true</code> iff the ref starts with the given prefix.    * Otherwise<code>false</code>.    *     * @param ref    *          the {@link BytesRef} to test    * @param prefix    *          the expected prefix    * @return Returns<code>true</code> iff the ref starts with the given prefix.    *         Otherwise<code>false</code>.    */
DECL|method|startsWith
specifier|public
specifier|static
name|boolean
name|startsWith
parameter_list|(
name|BytesRef
name|ref
parameter_list|,
name|BytesRef
name|prefix
parameter_list|)
block|{
return|return
name|sliceEquals
argument_list|(
name|ref
argument_list|,
name|prefix
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Returns<code>true</code> iff the ref ends with the given suffix. Otherwise    *<code>false</code>.    *     * @param ref    *          the {@link BytesRef} to test    * @param suffix    *          the expected suffix    * @return Returns<code>true</code> iff the ref ends with the given suffix.    *         Otherwise<code>false</code>.    */
DECL|method|endsWith
specifier|public
specifier|static
name|boolean
name|endsWith
parameter_list|(
name|BytesRef
name|ref
parameter_list|,
name|BytesRef
name|suffix
parameter_list|)
block|{
return|return
name|sliceEquals
argument_list|(
name|ref
argument_list|,
name|suffix
argument_list|,
name|ref
operator|.
name|length
operator|-
name|suffix
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|sliceEquals
specifier|private
specifier|static
name|boolean
name|sliceEquals
parameter_list|(
name|BytesRef
name|sliceToTest
parameter_list|,
name|BytesRef
name|other
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|<
literal|0
operator|||
name|sliceToTest
operator|.
name|length
operator|-
name|pos
operator|<
name|other
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|i
init|=
name|sliceToTest
operator|.
name|offset
operator|+
name|pos
decl_stmt|;
name|int
name|j
init|=
name|other
operator|.
name|offset
decl_stmt|;
specifier|final
name|int
name|k
init|=
name|other
operator|.
name|offset
operator|+
name|other
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|j
operator|<
name|k
condition|)
block|{
if|if
condition|(
name|sliceToTest
operator|.
name|bytes
index|[
name|i
operator|++
index|]
operator|!=
name|other
operator|.
name|bytes
index|[
name|j
operator|++
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
comment|/** Pass this as the seed to {@link #murmurhash3_x86_32}. */
comment|// Poached from Guava: set a different salt/seed
comment|// for each JVM instance, to frustrate hash key collision
comment|// denial of service attacks, and to catch any places that
comment|// somehow rely on hash function/order across JVM
comment|// instances:
DECL|field|GOOD_FAST_HASH_SEED
specifier|public
specifier|static
specifier|final
name|int
name|GOOD_FAST_HASH_SEED
decl_stmt|;
static|static
block|{
name|String
name|prop
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.seed"
argument_list|)
decl_stmt|;
if|if
condition|(
name|prop
operator|!=
literal|null
condition|)
block|{
comment|// So if there is a test failure that relied on hash
comment|// order, we remain reproducible based on the test seed:
if|if
condition|(
name|prop
operator|.
name|length
argument_list|()
operator|>
literal|8
condition|)
block|{
name|prop
operator|=
name|prop
operator|.
name|substring
argument_list|(
name|prop
operator|.
name|length
argument_list|()
operator|-
literal|8
argument_list|)
expr_stmt|;
block|}
name|GOOD_FAST_HASH_SEED
operator|=
operator|(
name|int
operator|)
name|Long
operator|.
name|parseLong
argument_list|(
name|prop
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|GOOD_FAST_HASH_SEED
operator|=
operator|(
name|int
operator|)
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns the MurmurHash3_x86_32 hash.    * Original source/tests at https://github.com/yonik/java_util/    */
DECL|method|murmurhash3_x86_32
specifier|public
specifier|static
name|int
name|murmurhash3_x86_32
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|int
name|seed
parameter_list|)
block|{
specifier|final
name|int
name|c1
init|=
literal|0xcc9e2d51
decl_stmt|;
specifier|final
name|int
name|c2
init|=
literal|0x1b873593
decl_stmt|;
name|int
name|h1
init|=
name|seed
decl_stmt|;
name|int
name|roundedEnd
init|=
name|offset
operator|+
operator|(
name|len
operator|&
literal|0xfffffffc
operator|)
decl_stmt|;
comment|// round down to 4 byte block
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|roundedEnd
condition|;
name|i
operator|+=
literal|4
control|)
block|{
comment|// little endian load order
name|int
name|k1
init|=
operator|(
name|data
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|i
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|i
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
name|data
index|[
name|i
operator|+
literal|3
index|]
operator|<<
literal|24
operator|)
decl_stmt|;
name|k1
operator|*=
name|c1
expr_stmt|;
name|k1
operator|=
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|k1
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|k1
operator|*=
name|c2
expr_stmt|;
name|h1
operator|^=
name|k1
expr_stmt|;
name|h1
operator|=
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|h1
argument_list|,
literal|13
argument_list|)
expr_stmt|;
name|h1
operator|=
name|h1
operator|*
literal|5
operator|+
literal|0xe6546b64
expr_stmt|;
block|}
comment|// tail
name|int
name|k1
init|=
literal|0
decl_stmt|;
switch|switch
condition|(
name|len
operator|&
literal|0x03
condition|)
block|{
case|case
literal|3
case|:
name|k1
operator|=
operator|(
name|data
index|[
name|roundedEnd
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
expr_stmt|;
comment|// fallthrough
case|case
literal|2
case|:
name|k1
operator||=
operator|(
name|data
index|[
name|roundedEnd
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
expr_stmt|;
comment|// fallthrough
case|case
literal|1
case|:
name|k1
operator||=
operator|(
name|data
index|[
name|roundedEnd
index|]
operator|&
literal|0xff
operator|)
expr_stmt|;
name|k1
operator|*=
name|c1
expr_stmt|;
name|k1
operator|=
name|Integer
operator|.
name|rotateLeft
argument_list|(
name|k1
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|k1
operator|*=
name|c2
expr_stmt|;
name|h1
operator|^=
name|k1
expr_stmt|;
block|}
comment|// finalization
name|h1
operator|^=
name|len
expr_stmt|;
comment|// fmix(h1);
name|h1
operator|^=
name|h1
operator|>>>
literal|16
expr_stmt|;
name|h1
operator|*=
literal|0x85ebca6b
expr_stmt|;
name|h1
operator|^=
name|h1
operator|>>>
literal|13
expr_stmt|;
name|h1
operator|*=
literal|0xc2b2ae35
expr_stmt|;
name|h1
operator|^=
name|h1
operator|>>>
literal|16
expr_stmt|;
return|return
name|h1
return|;
block|}
DECL|method|murmurhash3_x86_32
specifier|public
specifier|static
name|int
name|murmurhash3_x86_32
parameter_list|(
name|BytesRef
name|bytes
parameter_list|,
name|int
name|seed
parameter_list|)
block|{
return|return
name|murmurhash3_x86_32
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
name|seed
argument_list|)
return|;
block|}
block|}
end_class
end_unit
