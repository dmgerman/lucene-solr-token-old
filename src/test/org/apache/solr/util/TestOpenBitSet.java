begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import
begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment
begin_class
DECL|class|TestOpenBitSet
specifier|public
class|class
name|TestOpenBitSet
extends|extends
name|TestCase
block|{
DECL|field|rand
specifier|static
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|method|doGet
name|void
name|doGet
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|)
block|{
name|int
name|max
init|=
name|a
operator|.
name|size
argument_list|()
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
name|max
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
name|b
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"mismatch: BitSet=["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|a
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doNextSetBit
name|void
name|doNextSetBit
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|)
block|{
name|int
name|aa
init|=
operator|-
literal|1
decl_stmt|,
name|bb
init|=
operator|-
literal|1
decl_stmt|;
do|do
block|{
name|aa
operator|=
name|a
operator|.
name|nextSetBit
argument_list|(
name|aa
operator|+
literal|1
argument_list|)
expr_stmt|;
name|bb
operator|=
name|b
operator|.
name|nextSetBit
argument_list|(
name|bb
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|aa
operator|>=
literal|0
condition|)
do|;
block|}
comment|// test interleaving different BitSetIterator.next()
DECL|method|doIterate
name|void
name|doIterate
parameter_list|(
name|BitSet
name|a
parameter_list|,
name|OpenBitSet
name|b
parameter_list|)
block|{
name|int
name|aa
init|=
operator|-
literal|1
decl_stmt|,
name|bb
init|=
operator|-
literal|1
decl_stmt|;
name|BitSetIterator
name|iterator
init|=
operator|new
name|BitSetIterator
argument_list|(
name|b
argument_list|)
decl_stmt|;
do|do
block|{
name|aa
operator|=
name|a
operator|.
name|nextSetBit
argument_list|(
name|aa
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|)
name|bb
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
else|else
name|bb
operator|=
name|iterator
operator|.
name|next
argument_list|(
name|bb
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|aa
operator|>=
literal|0
condition|)
do|;
block|}
DECL|method|doRandomSets
name|void
name|doRandomSets
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|int
name|iter
parameter_list|)
block|{
name|BitSet
name|a0
init|=
literal|null
decl_stmt|;
name|OpenBitSet
name|b0
init|=
literal|null
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|int
name|sz
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxSize
argument_list|)
decl_stmt|;
name|BitSet
name|a
init|=
operator|new
name|BitSet
argument_list|(
name|sz
argument_list|)
decl_stmt|;
name|OpenBitSet
name|b
init|=
operator|new
name|OpenBitSet
argument_list|(
name|sz
argument_list|)
decl_stmt|;
comment|// test the various ways of setting bits
if|if
condition|(
name|sz
operator|>
literal|0
condition|)
block|{
name|int
name|nOper
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nOper
condition|;
name|j
operator|++
control|)
block|{
name|int
name|idx
decl_stmt|;
name|idx
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|set
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|fastSet
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|idx
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|clear
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|fastClear
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|idx
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
expr_stmt|;
name|a
operator|.
name|flip
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|b
operator|.
name|fastFlip
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|boolean
name|val
init|=
name|b
operator|.
name|flipAndGet
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|boolean
name|val2
init|=
name|b
operator|.
name|flipAndGet
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|val
operator|!=
name|val2
argument_list|)
expr_stmt|;
name|val
operator|=
name|b
operator|.
name|getAndSet
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|val2
operator|==
name|val
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|val
condition|)
name|b
operator|.
name|fastClear
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|==
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|// test that the various ways of accessing the bits are equivalent
name|doGet
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
comment|// test ranges, including possible extension
name|int
name|fromIndex
decl_stmt|,
name|toIndex
decl_stmt|;
name|fromIndex
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
operator|+
literal|80
argument_list|)
expr_stmt|;
name|toIndex
operator|=
name|fromIndex
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
operator|(
name|sz
operator|>>
literal|1
operator|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|BitSet
name|aa
init|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|aa
operator|.
name|flip
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|OpenBitSet
name|bb
init|=
operator|(
name|OpenBitSet
operator|)
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|bb
operator|.
name|flip
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|doIterate
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
comment|// a problem here is from flip or doIterate
name|fromIndex
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
operator|+
literal|80
argument_list|)
expr_stmt|;
name|toIndex
operator|=
name|fromIndex
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
operator|(
name|sz
operator|>>
literal|1
operator|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|aa
operator|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
expr_stmt|;
name|aa
operator|.
name|clear
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|bb
operator|=
operator|(
name|OpenBitSet
operator|)
name|b
operator|.
name|clone
argument_list|()
expr_stmt|;
name|bb
operator|.
name|clear
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|doNextSetBit
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
comment|// a problem here is from clear() or nextSetBit
name|fromIndex
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
operator|+
literal|80
argument_list|)
expr_stmt|;
name|toIndex
operator|=
name|fromIndex
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
operator|(
name|sz
operator|>>
literal|1
operator|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|aa
operator|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
expr_stmt|;
name|aa
operator|.
name|set
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|bb
operator|=
operator|(
name|OpenBitSet
operator|)
name|b
operator|.
name|clone
argument_list|()
expr_stmt|;
name|bb
operator|.
name|set
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
expr_stmt|;
name|doNextSetBit
argument_list|(
name|aa
argument_list|,
name|bb
argument_list|)
expr_stmt|;
comment|// a problem here is from set() or nextSetBit
if|if
condition|(
name|a0
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|a
operator|.
name|equals
argument_list|(
name|a0
argument_list|)
argument_list|,
name|b
operator|.
name|equals
argument_list|(
name|b0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|BitSet
name|a_and
init|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_and
operator|.
name|and
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|BitSet
name|a_or
init|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_or
operator|.
name|or
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|BitSet
name|a_xor
init|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_xor
operator|.
name|xor
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|BitSet
name|a_andn
init|=
operator|(
name|BitSet
operator|)
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_andn
operator|.
name|andNot
argument_list|(
name|a0
argument_list|)
expr_stmt|;
name|OpenBitSet
name|b_and
init|=
operator|(
name|OpenBitSet
operator|)
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|b
argument_list|,
name|b_and
argument_list|)
expr_stmt|;
name|b_and
operator|.
name|and
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|OpenBitSet
name|b_or
init|=
operator|(
name|OpenBitSet
operator|)
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|b_or
operator|.
name|or
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|OpenBitSet
name|b_xor
init|=
operator|(
name|OpenBitSet
operator|)
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|b_xor
operator|.
name|xor
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|OpenBitSet
name|b_andn
init|=
operator|(
name|OpenBitSet
operator|)
name|b
operator|.
name|clone
argument_list|()
decl_stmt|;
name|b_andn
operator|.
name|andNot
argument_list|(
name|b0
argument_list|)
expr_stmt|;
name|doIterate
argument_list|(
name|a_and
argument_list|,
name|b_and
argument_list|)
expr_stmt|;
name|doIterate
argument_list|(
name|a_or
argument_list|,
name|b_or
argument_list|)
expr_stmt|;
name|doIterate
argument_list|(
name|a_xor
argument_list|,
name|b_xor
argument_list|)
expr_stmt|;
name|doIterate
argument_list|(
name|a_andn
argument_list|,
name|b_andn
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_and
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_and
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_or
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_or
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_xor
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_xor
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_andn
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b_andn
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
comment|// test non-mutating popcounts
name|assertEquals
argument_list|(
name|b_and
operator|.
name|cardinality
argument_list|()
argument_list|,
name|OpenBitSet
operator|.
name|intersectionCount
argument_list|(
name|b
argument_list|,
name|b0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b_or
operator|.
name|cardinality
argument_list|()
argument_list|,
name|OpenBitSet
operator|.
name|unionCount
argument_list|(
name|b
argument_list|,
name|b0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b_xor
operator|.
name|cardinality
argument_list|()
argument_list|,
name|OpenBitSet
operator|.
name|xorCount
argument_list|(
name|b
argument_list|,
name|b0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b_andn
operator|.
name|cardinality
argument_list|()
argument_list|,
name|OpenBitSet
operator|.
name|andNotCount
argument_list|(
name|b
argument_list|,
name|b0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|a0
operator|=
name|a
expr_stmt|;
name|b0
operator|=
name|b
expr_stmt|;
block|}
block|}
comment|// large enough to flush obvious bugs, small enough to run in<.5 sec as part of a
comment|// larger testsuite.
DECL|method|testSmall
specifier|public
name|void
name|testSmall
parameter_list|()
block|{
name|doRandomSets
argument_list|(
literal|1200
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|testBig
specifier|public
name|void
name|testBig
parameter_list|()
block|{
comment|// uncomment to run a bigger test (~2 minutes).
comment|// doRandomSets(2000,200000);
block|}
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|OpenBitSet
name|b1
init|=
operator|new
name|OpenBitSet
argument_list|(
literal|1111
argument_list|)
decl_stmt|;
name|OpenBitSet
name|b2
init|=
operator|new
name|OpenBitSet
argument_list|(
literal|2222
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
name|b1
operator|.
name|set
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
name|b2
operator|.
name|set
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
name|b2
operator|.
name|set
argument_list|(
literal|2221
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
name|b1
operator|.
name|set
argument_list|(
literal|2221
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b2
operator|.
name|equals
argument_list|(
name|b1
argument_list|)
argument_list|)
expr_stmt|;
comment|// try different type of object
name|assertFalse
argument_list|(
name|b1
operator|.
name|equals
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
