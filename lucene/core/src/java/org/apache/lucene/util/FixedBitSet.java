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
name|io
operator|.
name|IOException
import|;
end_import
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSet
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
name|search
operator|.
name|DocIdSetIterator
import|;
end_import
begin_comment
comment|// TODO: maybe merge with BitVector?  Problem is BitVector
end_comment
begin_comment
comment|// caches its cardinality...
end_comment
begin_comment
comment|/** BitSet of fixed length (numBits), backed by accessible  *  ({@link #getBits}) long[], accessed with an int index,  *  implementing Bits and DocIdSet.  Unlike {@link  *  OpenBitSet} this bit set does not auto-expand, cannot  *  handle long index, and does not have fastXX/XX variants  *  (just X).  *  * @lucene.internal  **/
end_comment
begin_class
DECL|class|FixedBitSet
specifier|public
specifier|final
class|class
name|FixedBitSet
extends|extends
name|DocIdSet
implements|implements
name|Bits
block|{
DECL|field|bits
specifier|private
specifier|final
name|long
index|[]
name|bits
decl_stmt|;
DECL|field|numBits
specifier|private
name|int
name|numBits
decl_stmt|;
comment|/** returns the number of 64 bit words it would take to hold numBits */
DECL|method|bits2words
specifier|public
specifier|static
name|int
name|bits2words
parameter_list|(
name|int
name|numBits
parameter_list|)
block|{
name|int
name|numLong
init|=
name|numBits
operator|>>>
literal|6
decl_stmt|;
if|if
condition|(
operator|(
name|numBits
operator|&
literal|63
operator|)
operator|!=
literal|0
condition|)
block|{
name|numLong
operator|++
expr_stmt|;
block|}
return|return
name|numLong
return|;
block|}
DECL|method|FixedBitSet
specifier|public
name|FixedBitSet
parameter_list|(
name|int
name|numBits
parameter_list|)
block|{
name|this
operator|.
name|numBits
operator|=
name|numBits
expr_stmt|;
name|bits
operator|=
operator|new
name|long
index|[
name|bits2words
argument_list|(
name|numBits
argument_list|)
index|]
expr_stmt|;
block|}
DECL|method|FixedBitSet
specifier|public
name|FixedBitSet
parameter_list|(
name|long
index|[]
name|storedBits
parameter_list|,
name|int
name|numBits
parameter_list|)
block|{
name|this
operator|.
name|numBits
operator|=
name|numBits
expr_stmt|;
name|this
operator|.
name|bits
operator|=
name|storedBits
expr_stmt|;
block|}
comment|/** Makes full copy. */
DECL|method|FixedBitSet
specifier|public
name|FixedBitSet
parameter_list|(
name|FixedBitSet
name|other
parameter_list|)
block|{
name|bits
operator|=
operator|new
name|long
index|[
name|other
operator|.
name|bits
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|bits
argument_list|,
literal|0
argument_list|,
name|bits
argument_list|,
literal|0
argument_list|,
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
name|numBits
operator|=
name|other
operator|.
name|numBits
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|OpenBitSetIterator
argument_list|(
name|bits
argument_list|,
name|bits
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bits
specifier|public
name|Bits
name|bits
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|numBits
return|;
block|}
comment|/** This DocIdSet implementation is cacheable. */
annotation|@
name|Override
DECL|method|isCacheable
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/** Expert. */
DECL|method|getBits
specifier|public
name|long
index|[]
name|getBits
parameter_list|()
block|{
return|return
name|bits
return|;
block|}
comment|/** Returns number of set bits.  NOTE: this visits every    *  long in the backing bits array, and the result is not    *  internally cached! */
DECL|method|cardinality
specifier|public
name|int
name|cardinality
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|BitUtil
operator|.
name|pop_array
argument_list|(
name|bits
argument_list|,
literal|0
argument_list|,
name|bits
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|numBits
operator|:
literal|"index="
operator|+
name|index
assert|;
name|int
name|i
init|=
name|index
operator|>>
literal|6
decl_stmt|;
comment|// div 64
comment|// signed shift will keep a negative index and force an
comment|// array-index-out-of-bounds-exception, removing the need for an explicit check.
name|int
name|bit
init|=
name|index
operator|&
literal|0x3f
decl_stmt|;
comment|// mod 64
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|bit
decl_stmt|;
return|return
operator|(
name|bits
index|[
name|i
index|]
operator|&
name|bitmask
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|numBits
assert|;
name|int
name|wordNum
init|=
name|index
operator|>>
literal|6
decl_stmt|;
comment|// div 64
name|int
name|bit
init|=
name|index
operator|&
literal|0x3f
decl_stmt|;
comment|// mod 64
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|bit
decl_stmt|;
name|bits
index|[
name|wordNum
index|]
operator||=
name|bitmask
expr_stmt|;
block|}
DECL|method|getAndSet
specifier|public
name|boolean
name|getAndSet
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|numBits
assert|;
name|int
name|wordNum
init|=
name|index
operator|>>
literal|6
decl_stmt|;
comment|// div 64
name|int
name|bit
init|=
name|index
operator|&
literal|0x3f
decl_stmt|;
comment|// mod 64
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|bit
decl_stmt|;
name|boolean
name|val
init|=
operator|(
name|bits
index|[
name|wordNum
index|]
operator|&
name|bitmask
operator|)
operator|!=
literal|0
decl_stmt|;
name|bits
index|[
name|wordNum
index|]
operator||=
name|bitmask
expr_stmt|;
return|return
name|val
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|numBits
assert|;
name|int
name|wordNum
init|=
name|index
operator|>>
literal|6
decl_stmt|;
name|int
name|bit
init|=
name|index
operator|&
literal|0x03f
decl_stmt|;
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|bit
decl_stmt|;
name|bits
index|[
name|wordNum
index|]
operator|&=
operator|~
name|bitmask
expr_stmt|;
block|}
DECL|method|getAndClear
specifier|public
name|boolean
name|getAndClear
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|numBits
assert|;
name|int
name|wordNum
init|=
name|index
operator|>>
literal|6
decl_stmt|;
comment|// div 64
name|int
name|bit
init|=
name|index
operator|&
literal|0x3f
decl_stmt|;
comment|// mod 64
name|long
name|bitmask
init|=
literal|1L
operator|<<
name|bit
decl_stmt|;
name|boolean
name|val
init|=
operator|(
name|bits
index|[
name|wordNum
index|]
operator|&
name|bitmask
operator|)
operator|!=
literal|0
decl_stmt|;
name|bits
index|[
name|wordNum
index|]
operator|&=
operator|~
name|bitmask
expr_stmt|;
return|return
name|val
return|;
block|}
comment|/** Returns the index of the first set bit starting at the index specified.    *  -1 is returned if there are no more set bits.    */
DECL|method|nextSetBit
specifier|public
name|int
name|nextSetBit
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|numBits
assert|;
name|int
name|i
init|=
name|index
operator|>>
literal|6
decl_stmt|;
specifier|final
name|int
name|subIndex
init|=
name|index
operator|&
literal|0x3f
decl_stmt|;
comment|// index within the word
name|long
name|word
init|=
name|bits
index|[
name|i
index|]
operator|>>
name|subIndex
decl_stmt|;
comment|// skip all the bits to the right of index
if|if
condition|(
name|word
operator|!=
literal|0
condition|)
block|{
return|return
operator|(
name|i
operator|<<
literal|6
operator|)
operator|+
name|subIndex
operator|+
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|word
argument_list|)
return|;
block|}
while|while
condition|(
operator|++
name|i
operator|<
name|bits
operator|.
name|length
condition|)
block|{
name|word
operator|=
name|bits
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|word
operator|!=
literal|0
condition|)
block|{
return|return
operator|(
name|i
operator|<<
literal|6
operator|)
operator|+
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|word
argument_list|)
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/** Returns the index of the last set bit before or on the index specified.    *  -1 is returned if there are no more set bits.    */
DECL|method|prevSetBit
specifier|public
name|int
name|prevSetBit
parameter_list|(
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|numBits
operator|:
literal|"index="
operator|+
name|index
operator|+
literal|" numBits="
operator|+
name|numBits
assert|;
name|int
name|i
init|=
name|index
operator|>>
literal|6
decl_stmt|;
specifier|final
name|int
name|subIndex
init|=
name|index
operator|&
literal|0x3f
decl_stmt|;
comment|// index within the word
name|long
name|word
init|=
operator|(
name|bits
index|[
name|i
index|]
operator|<<
operator|(
literal|63
operator|-
name|subIndex
operator|)
operator|)
decl_stmt|;
comment|// skip all the bits to the left of index
if|if
condition|(
name|word
operator|!=
literal|0
condition|)
block|{
return|return
operator|(
name|i
operator|<<
literal|6
operator|)
operator|+
name|subIndex
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|word
argument_list|)
return|;
comment|// See LUCENE-3197
block|}
while|while
condition|(
operator|--
name|i
operator|>=
literal|0
condition|)
block|{
name|word
operator|=
name|bits
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|word
operator|!=
literal|0
condition|)
block|{
return|return
operator|(
name|i
operator|<<
literal|6
operator|)
operator|+
literal|63
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|word
argument_list|)
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/** Does in-place OR of the bits provided by the    *  iterator. */
DECL|method|or
specifier|public
name|void
name|or
parameter_list|(
name|DocIdSetIterator
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|iter
operator|instanceof
name|OpenBitSetIterator
operator|&&
name|iter
operator|.
name|docID
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
specifier|final
name|OpenBitSetIterator
name|obs
init|=
operator|(
name|OpenBitSetIterator
operator|)
name|iter
decl_stmt|;
name|or
argument_list|(
name|obs
operator|.
name|arr
argument_list|,
name|obs
operator|.
name|words
argument_list|)
expr_stmt|;
comment|// advance after last doc that would be accepted if standard
comment|// iteration is used (to exhaust it):
name|obs
operator|.
name|advance
argument_list|(
name|numBits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
operator|)
operator|<
name|numBits
condition|)
block|{
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** this = this OR other */
DECL|method|or
specifier|public
name|void
name|or
parameter_list|(
name|FixedBitSet
name|other
parameter_list|)
block|{
name|or
argument_list|(
name|other
operator|.
name|bits
argument_list|,
name|other
operator|.
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|or
specifier|private
name|void
name|or
parameter_list|(
specifier|final
name|long
index|[]
name|otherArr
parameter_list|,
specifier|final
name|int
name|otherLen
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|thisArr
init|=
name|this
operator|.
name|bits
decl_stmt|;
name|int
name|pos
init|=
name|Math
operator|.
name|min
argument_list|(
name|thisArr
operator|.
name|length
argument_list|,
name|otherLen
argument_list|)
decl_stmt|;
while|while
condition|(
operator|--
name|pos
operator|>=
literal|0
condition|)
block|{
name|thisArr
index|[
name|pos
index|]
operator||=
name|otherArr
index|[
name|pos
index|]
expr_stmt|;
block|}
block|}
comment|/** Does in-place AND of the bits provided by the    *  iterator. */
DECL|method|and
specifier|public
name|void
name|and
parameter_list|(
name|DocIdSetIterator
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|iter
operator|instanceof
name|OpenBitSetIterator
operator|&&
name|iter
operator|.
name|docID
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
specifier|final
name|OpenBitSetIterator
name|obs
init|=
operator|(
name|OpenBitSetIterator
operator|)
name|iter
decl_stmt|;
name|and
argument_list|(
name|obs
operator|.
name|arr
argument_list|,
name|obs
operator|.
name|words
argument_list|)
expr_stmt|;
comment|// advance after last doc that would be accepted if standard
comment|// iteration is used (to exhaust it):
name|obs
operator|.
name|advance
argument_list|(
name|numBits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|numBits
operator|==
literal|0
condition|)
return|return;
name|int
name|disiDoc
decl_stmt|,
name|bitSetDoc
init|=
name|nextSetBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
while|while
condition|(
name|bitSetDoc
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|disiDoc
operator|=
name|iter
operator|.
name|advance
argument_list|(
name|bitSetDoc
argument_list|)
operator|)
operator|<
name|numBits
condition|)
block|{
name|clear
argument_list|(
name|bitSetDoc
argument_list|,
name|disiDoc
argument_list|)
expr_stmt|;
name|disiDoc
operator|++
expr_stmt|;
name|bitSetDoc
operator|=
operator|(
name|disiDoc
operator|<
name|numBits
operator|)
condition|?
name|nextSetBit
argument_list|(
name|disiDoc
argument_list|)
else|:
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|bitSetDoc
operator|!=
operator|-
literal|1
condition|)
block|{
name|clear
argument_list|(
name|bitSetDoc
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** this = this AND other */
DECL|method|and
specifier|public
name|void
name|and
parameter_list|(
name|FixedBitSet
name|other
parameter_list|)
block|{
name|and
argument_list|(
name|other
operator|.
name|bits
argument_list|,
name|other
operator|.
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|and
specifier|private
name|void
name|and
parameter_list|(
specifier|final
name|long
index|[]
name|otherArr
parameter_list|,
specifier|final
name|int
name|otherLen
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|thisArr
init|=
name|this
operator|.
name|bits
decl_stmt|;
name|int
name|pos
init|=
name|Math
operator|.
name|min
argument_list|(
name|thisArr
operator|.
name|length
argument_list|,
name|otherLen
argument_list|)
decl_stmt|;
while|while
condition|(
operator|--
name|pos
operator|>=
literal|0
condition|)
block|{
name|thisArr
index|[
name|pos
index|]
operator|&=
name|otherArr
index|[
name|pos
index|]
expr_stmt|;
block|}
if|if
condition|(
name|thisArr
operator|.
name|length
operator|>
name|otherLen
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|thisArr
argument_list|,
name|otherLen
argument_list|,
name|thisArr
operator|.
name|length
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Does in-place AND NOT of the bits provided by the    *  iterator. */
DECL|method|andNot
specifier|public
name|void
name|andNot
parameter_list|(
name|DocIdSetIterator
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|iter
operator|instanceof
name|OpenBitSetIterator
operator|&&
name|iter
operator|.
name|docID
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
specifier|final
name|OpenBitSetIterator
name|obs
init|=
operator|(
name|OpenBitSetIterator
operator|)
name|iter
decl_stmt|;
name|andNot
argument_list|(
name|obs
operator|.
name|arr
argument_list|,
name|obs
operator|.
name|words
argument_list|)
expr_stmt|;
comment|// advance after last doc that would be accepted if standard
comment|// iteration is used (to exhaust it):
name|obs
operator|.
name|advance
argument_list|(
name|numBits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
operator|)
operator|<
name|numBits
condition|)
block|{
name|clear
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** this = this AND NOT other */
DECL|method|andNot
specifier|public
name|void
name|andNot
parameter_list|(
name|FixedBitSet
name|other
parameter_list|)
block|{
name|andNot
argument_list|(
name|other
operator|.
name|bits
argument_list|,
name|other
operator|.
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|andNot
specifier|private
name|void
name|andNot
parameter_list|(
specifier|final
name|long
index|[]
name|otherArr
parameter_list|,
specifier|final
name|int
name|otherLen
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|thisArr
init|=
name|this
operator|.
name|bits
decl_stmt|;
name|int
name|pos
init|=
name|Math
operator|.
name|min
argument_list|(
name|thisArr
operator|.
name|length
argument_list|,
name|otherLen
argument_list|)
decl_stmt|;
while|while
condition|(
operator|--
name|pos
operator|>=
literal|0
condition|)
block|{
name|thisArr
index|[
name|pos
index|]
operator|&=
operator|~
name|otherArr
index|[
name|pos
index|]
expr_stmt|;
block|}
block|}
comment|// NOTE: no .isEmpty() here because that's trappy (ie,
comment|// typically isEmpty is low cost, but this one wouldn't
comment|// be)
comment|/** Flips a range of bits    *    * @param startIndex lower index    * @param endIndex one-past the last bit to flip    */
DECL|method|flip
specifier|public
name|void
name|flip
parameter_list|(
name|int
name|startIndex
parameter_list|,
name|int
name|endIndex
parameter_list|)
block|{
assert|assert
name|startIndex
operator|>=
literal|0
operator|&&
name|startIndex
operator|<
name|numBits
assert|;
assert|assert
name|endIndex
operator|>=
literal|0
operator|&&
name|endIndex
operator|<=
name|numBits
assert|;
if|if
condition|(
name|endIndex
operator|<=
name|startIndex
condition|)
block|{
return|return;
block|}
name|int
name|startWord
init|=
name|startIndex
operator|>>
literal|6
decl_stmt|;
name|int
name|endWord
init|=
operator|(
name|endIndex
operator|-
literal|1
operator|)
operator|>>
literal|6
decl_stmt|;
comment|/*** Grrr, java shifting wraps around so -1L>>>64 == -1      * for that reason, make sure not to use endmask if the bits to flip will      * be zero in the last word (redefine endWord to be the last changed...)     long startmask = -1L<< (startIndex& 0x3f);     // example: 11111...111000     long endmask = -1L>>> (64-(endIndex& 0x3f));   // example: 00111...111111     ***/
name|long
name|startmask
init|=
operator|-
literal|1L
operator|<<
name|startIndex
decl_stmt|;
name|long
name|endmask
init|=
operator|-
literal|1L
operator|>>>
operator|-
name|endIndex
decl_stmt|;
comment|// 64-(endIndex&0x3f) is the same as -endIndex due to wrap
if|if
condition|(
name|startWord
operator|==
name|endWord
condition|)
block|{
name|bits
index|[
name|startWord
index|]
operator|^=
operator|(
name|startmask
operator|&
name|endmask
operator|)
expr_stmt|;
return|return;
block|}
name|bits
index|[
name|startWord
index|]
operator|^=
name|startmask
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startWord
operator|+
literal|1
init|;
name|i
operator|<
name|endWord
condition|;
name|i
operator|++
control|)
block|{
name|bits
index|[
name|i
index|]
operator|=
operator|~
name|bits
index|[
name|i
index|]
expr_stmt|;
block|}
name|bits
index|[
name|endWord
index|]
operator|^=
name|endmask
expr_stmt|;
block|}
comment|/** Sets a range of bits    *    * @param startIndex lower index    * @param endIndex one-past the last bit to set    */
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|int
name|startIndex
parameter_list|,
name|int
name|endIndex
parameter_list|)
block|{
assert|assert
name|startIndex
operator|>=
literal|0
operator|&&
name|startIndex
operator|<
name|numBits
assert|;
assert|assert
name|endIndex
operator|>=
literal|0
operator|&&
name|endIndex
operator|<=
name|numBits
assert|;
if|if
condition|(
name|endIndex
operator|<=
name|startIndex
condition|)
block|{
return|return;
block|}
name|int
name|startWord
init|=
name|startIndex
operator|>>
literal|6
decl_stmt|;
name|int
name|endWord
init|=
operator|(
name|endIndex
operator|-
literal|1
operator|)
operator|>>
literal|6
decl_stmt|;
name|long
name|startmask
init|=
operator|-
literal|1L
operator|<<
name|startIndex
decl_stmt|;
name|long
name|endmask
init|=
operator|-
literal|1L
operator|>>>
operator|-
name|endIndex
decl_stmt|;
comment|// 64-(endIndex&0x3f) is the same as -endIndex due to wrap
if|if
condition|(
name|startWord
operator|==
name|endWord
condition|)
block|{
name|bits
index|[
name|startWord
index|]
operator||=
operator|(
name|startmask
operator|&
name|endmask
operator|)
expr_stmt|;
return|return;
block|}
name|bits
index|[
name|startWord
index|]
operator||=
name|startmask
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|bits
argument_list|,
name|startWord
operator|+
literal|1
argument_list|,
name|endWord
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|bits
index|[
name|endWord
index|]
operator||=
name|endmask
expr_stmt|;
block|}
comment|/** Clears a range of bits.    *    * @param startIndex lower index    * @param endIndex one-past the last bit to clear    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|int
name|startIndex
parameter_list|,
name|int
name|endIndex
parameter_list|)
block|{
assert|assert
name|startIndex
operator|>=
literal|0
operator|&&
name|startIndex
operator|<
name|numBits
assert|;
assert|assert
name|endIndex
operator|>=
literal|0
operator|&&
name|endIndex
operator|<=
name|numBits
assert|;
if|if
condition|(
name|endIndex
operator|<=
name|startIndex
condition|)
block|{
return|return;
block|}
name|int
name|startWord
init|=
name|startIndex
operator|>>
literal|6
decl_stmt|;
name|int
name|endWord
init|=
operator|(
name|endIndex
operator|-
literal|1
operator|)
operator|>>
literal|6
decl_stmt|;
name|long
name|startmask
init|=
operator|-
literal|1L
operator|<<
name|startIndex
decl_stmt|;
name|long
name|endmask
init|=
operator|-
literal|1L
operator|>>>
operator|-
name|endIndex
decl_stmt|;
comment|// 64-(endIndex&0x3f) is the same as -endIndex due to wrap
comment|// invert masks since we are clearing
name|startmask
operator|=
operator|~
name|startmask
expr_stmt|;
name|endmask
operator|=
operator|~
name|endmask
expr_stmt|;
if|if
condition|(
name|startWord
operator|==
name|endWord
condition|)
block|{
name|bits
index|[
name|startWord
index|]
operator|&=
operator|(
name|startmask
operator||
name|endmask
operator|)
expr_stmt|;
return|return;
block|}
name|bits
index|[
name|startWord
index|]
operator|&=
name|startmask
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|bits
argument_list|,
name|startWord
operator|+
literal|1
argument_list|,
name|endWord
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|bits
index|[
name|endWord
index|]
operator|&=
name|endmask
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|FixedBitSet
name|clone
parameter_list|()
block|{
return|return
operator|new
name|FixedBitSet
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** returns true if both sets have the same bits set */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|FixedBitSet
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|FixedBitSet
name|other
init|=
operator|(
name|FixedBitSet
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|numBits
operator|!=
name|other
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|bits
argument_list|,
name|other
operator|.
name|bits
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|long
name|h
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|bits
operator|.
name|length
init|;
operator|--
name|i
operator|>=
literal|0
condition|;
control|)
block|{
name|h
operator|^=
name|bits
index|[
name|i
index|]
expr_stmt|;
name|h
operator|=
operator|(
name|h
operator|<<
literal|1
operator|)
operator||
operator|(
name|h
operator|>>>
literal|63
operator|)
expr_stmt|;
comment|// rotate left
block|}
comment|// fold leftmost bits into right and add a constant to prevent
comment|// empty sets from returning 0, which is too common.
return|return
call|(
name|int
call|)
argument_list|(
operator|(
name|h
operator|>>
literal|32
operator|)
operator|^
name|h
argument_list|)
operator|+
literal|0x98761234
return|;
block|}
block|}
end_class
end_unit
