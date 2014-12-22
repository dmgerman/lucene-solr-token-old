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
name|java
operator|.
name|util
operator|.
name|Collections
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
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import
begin_comment
comment|/**  * Base test case for BitSets.  */
end_comment
begin_class
annotation|@
name|Ignore
DECL|class|BaseBitSetTestCase
specifier|public
specifier|abstract
class|class
name|BaseBitSetTestCase
parameter_list|<
name|T
extends|extends
name|BitSet
parameter_list|>
extends|extends
name|LuceneTestCase
block|{
comment|/** Create a copy of the given {@link BitSet} which has<code>length</code> bits. */
DECL|method|copyOf
specifier|public
specifier|abstract
name|T
name|copyOf
parameter_list|(
name|BitSet
name|bs
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Create a random set which has<code>numBitsSet</code> of its<code>numBits</code> bits set. */
DECL|method|randomSet
specifier|static
name|java
operator|.
name|util
operator|.
name|BitSet
name|randomSet
parameter_list|(
name|int
name|numBits
parameter_list|,
name|int
name|numBitsSet
parameter_list|)
block|{
assert|assert
name|numBitsSet
operator|<=
name|numBits
assert|;
specifier|final
name|java
operator|.
name|util
operator|.
name|BitSet
name|set
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|BitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
if|if
condition|(
name|numBitsSet
operator|==
name|numBits
condition|)
block|{
name|set
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
block|}
else|else
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
name|numBitsSet
condition|;
operator|++
name|i
control|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|o
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|set
operator|.
name|get
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|set
operator|.
name|set
argument_list|(
name|o
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
return|return
name|set
return|;
block|}
comment|/** Same as {@link #randomSet(int, int)} but given a load factor. */
DECL|method|randomSet
specifier|static
name|java
operator|.
name|util
operator|.
name|BitSet
name|randomSet
parameter_list|(
name|int
name|numBits
parameter_list|,
name|float
name|percentSet
parameter_list|)
block|{
return|return
name|randomSet
argument_list|(
name|numBits
argument_list|,
call|(
name|int
call|)
argument_list|(
name|percentSet
operator|*
name|numBits
argument_list|)
argument_list|)
return|;
block|}
DECL|method|assertEquals
specifier|protected
name|void
name|assertEquals
parameter_list|(
name|BitSet
name|set1
parameter_list|,
name|T
name|set2
parameter_list|,
name|int
name|maxDoc
parameter_list|)
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
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
literal|"Different at "
operator|+
name|i
argument_list|,
name|set1
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|set2
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test the {@link BitSet#cardinality()} method. */
DECL|method|testCardinality
specifier|public
name|void
name|testCardinality
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
for|for
control|(
name|float
name|percentSet
range|:
operator|new
name|float
index|[]
block|{
literal|0
block|,
literal|0.01f
block|,
literal|0.1f
block|,
literal|0.5f
block|,
literal|0.9f
block|,
literal|0.99f
block|,
literal|1f
block|}
control|)
block|{
name|BitSet
name|set1
init|=
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
name|percentSet
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|T
name|set2
init|=
name|copyOf
argument_list|(
name|set1
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|set1
operator|.
name|cardinality
argument_list|()
argument_list|,
name|set2
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test {@link BitSet#prevSetBit(int)}. */
DECL|method|testPrevSetBit
specifier|public
name|void
name|testPrevSetBit
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
for|for
control|(
name|float
name|percentSet
range|:
operator|new
name|float
index|[]
block|{
literal|0
block|,
literal|0.01f
block|,
literal|0.1f
block|,
literal|0.5f
block|,
literal|0.9f
block|,
literal|0.99f
block|,
literal|1f
block|}
control|)
block|{
name|BitSet
name|set1
init|=
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
name|percentSet
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|T
name|set2
init|=
name|copyOf
argument_list|(
name|set1
argument_list|,
name|numBits
argument_list|)
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
name|numBits
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|set1
operator|.
name|prevSetBit
argument_list|(
name|i
argument_list|)
argument_list|,
name|set2
operator|.
name|prevSetBit
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Test {@link BitSet#nextSetBit(int)}. */
DECL|method|testNextSetBit
specifier|public
name|void
name|testNextSetBit
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
for|for
control|(
name|float
name|percentSet
range|:
operator|new
name|float
index|[]
block|{
literal|0
block|,
literal|0.01f
block|,
literal|0.1f
block|,
literal|0.5f
block|,
literal|0.9f
block|,
literal|0.99f
block|,
literal|1f
block|}
control|)
block|{
name|BitSet
name|set1
init|=
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
name|percentSet
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|T
name|set2
init|=
name|copyOf
argument_list|(
name|set1
argument_list|,
name|numBits
argument_list|)
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
name|numBits
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|set1
operator|.
name|nextSetBit
argument_list|(
name|i
argument_list|)
argument_list|,
name|set2
operator|.
name|nextSetBit
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Test the {@link BitSet#set} method. */
DECL|method|testSet
specifier|public
name|void
name|testSet
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
name|BitSet
name|set1
init|=
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
literal|0
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|T
name|set2
init|=
name|copyOf
argument_list|(
name|set1
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iters
init|=
literal|10000
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|index
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|set1
operator|.
name|set
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|set2
operator|.
name|set
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|set1
argument_list|,
name|set2
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
block|}
comment|/** Test the {@link BitSet#clear(int)} method. */
DECL|method|testClear
specifier|public
name|void
name|testClear
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
for|for
control|(
name|float
name|percentSet
range|:
operator|new
name|float
index|[]
block|{
literal|0
block|,
literal|0.01f
block|,
literal|0.1f
block|,
literal|0.5f
block|,
literal|0.9f
block|,
literal|0.99f
block|,
literal|1f
block|}
control|)
block|{
name|BitSet
name|set1
init|=
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
name|percentSet
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|T
name|set2
init|=
name|copyOf
argument_list|(
name|set1
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iters
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numBits
operator|*
literal|2
argument_list|)
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|index
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|set1
operator|.
name|clear
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|set2
operator|.
name|clear
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|set1
argument_list|,
name|set2
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test the {@link BitSet#clear(int,int)} method. */
DECL|method|testClearRange
specifier|public
name|void
name|testClearRange
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
for|for
control|(
name|float
name|percentSet
range|:
operator|new
name|float
index|[]
block|{
literal|0
block|,
literal|0.01f
block|,
literal|0.1f
block|,
literal|0.5f
block|,
literal|0.9f
block|,
literal|0.99f
block|,
literal|1f
block|}
control|)
block|{
name|BitSet
name|set1
init|=
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
name|percentSet
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|T
name|set2
init|=
name|copyOf
argument_list|(
name|set1
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iters
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|from
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
specifier|final
name|int
name|to
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numBits
operator|+
literal|1
argument_list|)
decl_stmt|;
name|set1
operator|.
name|clear
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
name|set2
operator|.
name|clear
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set1
argument_list|,
name|set2
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|randomCopy
specifier|private
name|DocIdSet
name|randomCopy
parameter_list|(
name|BitSet
name|set
parameter_list|,
name|int
name|numBits
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|,
name|set
operator|.
name|cardinality
argument_list|()
argument_list|)
return|;
case|case
literal|1
case|:
return|return
operator|new
name|BitDocIdSet
argument_list|(
name|copyOf
argument_list|(
name|set
argument_list|,
name|numBits
argument_list|)
argument_list|,
name|set
operator|.
name|cardinality
argument_list|()
argument_list|)
return|;
case|case
literal|2
case|:
specifier|final
name|RoaringDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|RoaringDocIdSet
operator|.
name|Builder
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|set
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|i
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|i
operator|=
name|i
operator|+
literal|1
operator|>=
name|numBits
condition|?
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
else|:
name|set
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
case|case
literal|3
case|:
name|FixedBitSet
name|fbs
init|=
operator|new
name|FixedBitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|fbs
operator|.
name|or
argument_list|(
operator|new
name|BitSetIterator
argument_list|(
name|set
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|BitDocIdSet
argument_list|(
name|fbs
argument_list|)
return|;
case|case
literal|4
case|:
name|SparseFixedBitSet
name|sfbs
init|=
operator|new
name|SparseFixedBitSet
argument_list|(
name|numBits
argument_list|)
decl_stmt|;
name|sfbs
operator|.
name|or
argument_list|(
operator|new
name|BitSetIterator
argument_list|(
name|set
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|BitDocIdSet
argument_list|(
name|sfbs
argument_list|)
return|;
default|default:
name|fail
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|testOr
specifier|private
name|void
name|testOr
parameter_list|(
name|float
name|load
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
name|BitSet
name|set1
init|=
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
literal|0
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
comment|// empty
name|T
name|set2
init|=
name|copyOf
argument_list|(
name|set1
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iterations
condition|;
operator|++
name|iter
control|)
block|{
name|DocIdSet
name|otherSet
init|=
name|randomCopy
argument_list|(
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
name|load
argument_list|)
argument_list|,
name|numBits
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|DocIdSetIterator
name|otherIterator
init|=
name|otherSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherIterator
operator|!=
literal|null
condition|)
block|{
name|set1
operator|.
name|or
argument_list|(
name|otherIterator
argument_list|)
expr_stmt|;
name|set2
operator|.
name|or
argument_list|(
name|otherSet
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set1
argument_list|,
name|set2
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Test {@link BitSet#or(DocIdSetIterator)} on sparse sets. */
DECL|method|testOrSparse
specifier|public
name|void
name|testOrSparse
parameter_list|()
throws|throws
name|IOException
block|{
name|testOr
argument_list|(
literal|0.001f
argument_list|)
expr_stmt|;
block|}
comment|/** Test {@link BitSet#or(DocIdSetIterator)} on dense sets. */
DECL|method|testOrDense
specifier|public
name|void
name|testOrDense
parameter_list|()
throws|throws
name|IOException
block|{
name|testOr
argument_list|(
literal|0.5f
argument_list|)
expr_stmt|;
block|}
comment|/** Test {@link BitSet#or(DocIdSetIterator)} on a random density. */
DECL|method|testOrRandom
specifier|public
name|void
name|testOrRandom
parameter_list|()
throws|throws
name|IOException
block|{
name|testOr
argument_list|(
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAnd
specifier|private
name|void
name|testAnd
parameter_list|(
name|float
name|load
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
name|BitSet
name|set1
init|=
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
name|numBits
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
comment|// full
name|T
name|set2
init|=
name|copyOf
argument_list|(
name|set1
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iterations
condition|;
operator|++
name|iter
control|)
block|{
comment|// BitSets have specializations to merge with certain impls, so we randomize the impl...
name|DocIdSet
name|otherSet
init|=
name|randomCopy
argument_list|(
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
name|load
argument_list|)
argument_list|,
name|numBits
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|DocIdSetIterator
name|otherIterator
init|=
name|otherSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherIterator
operator|!=
literal|null
condition|)
block|{
name|set1
operator|.
name|and
argument_list|(
name|otherIterator
argument_list|)
expr_stmt|;
name|set2
operator|.
name|and
argument_list|(
name|otherSet
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set1
argument_list|,
name|set2
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Test {@link BitSet#and(DocIdSetIterator)} on sparse sets. */
DECL|method|testAndSparse
specifier|public
name|void
name|testAndSparse
parameter_list|()
throws|throws
name|IOException
block|{
name|testAnd
argument_list|(
literal|0.1f
argument_list|)
expr_stmt|;
block|}
comment|/** Test {@link BitSet#and(DocIdSetIterator)} on dense sets. */
DECL|method|testAndDense
specifier|public
name|void
name|testAndDense
parameter_list|()
throws|throws
name|IOException
block|{
name|testAnd
argument_list|(
literal|0.99f
argument_list|)
expr_stmt|;
block|}
comment|/** Test {@link BitSet#and(DocIdSetIterator)} on a random density. */
DECL|method|testAndRandom
specifier|public
name|void
name|testAndRandom
parameter_list|()
throws|throws
name|IOException
block|{
name|testAnd
argument_list|(
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAndNot
specifier|private
name|void
name|testAndNot
parameter_list|(
name|float
name|load
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
name|BitSet
name|set1
init|=
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
name|numBits
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
comment|// full
name|T
name|set2
init|=
name|copyOf
argument_list|(
name|set1
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iterations
condition|;
operator|++
name|iter
control|)
block|{
name|DocIdSet
name|otherSet
init|=
name|randomCopy
argument_list|(
operator|new
name|JavaUtilBitSet
argument_list|(
name|randomSet
argument_list|(
name|numBits
argument_list|,
name|load
argument_list|)
argument_list|,
name|numBits
argument_list|)
argument_list|,
name|numBits
argument_list|)
decl_stmt|;
name|DocIdSetIterator
name|otherIterator
init|=
name|otherSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherIterator
operator|!=
literal|null
condition|)
block|{
name|set1
operator|.
name|andNot
argument_list|(
name|otherIterator
argument_list|)
expr_stmt|;
name|set2
operator|.
name|andNot
argument_list|(
name|otherSet
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set1
argument_list|,
name|set2
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Test {@link BitSet#andNot(DocIdSetIterator)} on sparse sets. */
DECL|method|testAndNotSparse
specifier|public
name|void
name|testAndNotSparse
parameter_list|()
throws|throws
name|IOException
block|{
name|testAndNot
argument_list|(
literal|0.01f
argument_list|)
expr_stmt|;
block|}
comment|/** Test {@link BitSet#andNot(DocIdSetIterator)} on dense sets. */
DECL|method|testAndNotDense
specifier|public
name|void
name|testAndNotDense
parameter_list|()
throws|throws
name|IOException
block|{
name|testAndNot
argument_list|(
literal|0.9f
argument_list|)
expr_stmt|;
block|}
comment|/** Test {@link BitSet#andNot(DocIdSetIterator)} on a random density. */
DECL|method|testAndNotRandom
specifier|public
name|void
name|testAndNotRandom
parameter_list|()
throws|throws
name|IOException
block|{
name|testAndNot
argument_list|(
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|JavaUtilBitSet
specifier|private
specifier|static
class|class
name|JavaUtilBitSet
extends|extends
name|BitSet
block|{
DECL|field|bitSet
specifier|private
specifier|final
name|java
operator|.
name|util
operator|.
name|BitSet
name|bitSet
decl_stmt|;
DECL|field|numBits
specifier|private
specifier|final
name|int
name|numBits
decl_stmt|;
DECL|method|JavaUtilBitSet
name|JavaUtilBitSet
parameter_list|(
name|java
operator|.
name|util
operator|.
name|BitSet
name|bitSet
parameter_list|,
name|int
name|numBits
parameter_list|)
block|{
name|this
operator|.
name|bitSet
operator|=
name|bitSet
expr_stmt|;
name|this
operator|.
name|numBits
operator|=
name|numBits
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|bitSet
operator|.
name|clear
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|bitSet
operator|.
name|get
argument_list|(
name|index
argument_list|)
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
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|startIndex
operator|>=
name|endIndex
condition|)
block|{
return|return;
block|}
name|bitSet
operator|.
name|clear
argument_list|(
name|startIndex
argument_list|,
name|endIndex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cardinality
specifier|public
name|int
name|cardinality
parameter_list|()
block|{
return|return
name|bitSet
operator|.
name|cardinality
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|prevSetBit
specifier|public
name|int
name|prevSetBit
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|bitSet
operator|.
name|previousSetBit
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextSetBit
specifier|public
name|int
name|nextSetBit
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|int
name|next
init|=
name|bitSet
operator|.
name|nextSetBit
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|==
operator|-
literal|1
condition|)
block|{
name|next
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
expr_stmt|;
block|}
return|return
name|next
return|;
block|}
block|}
block|}
end_class
end_unit
