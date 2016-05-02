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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|PointValues
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
begin_class
DECL|class|TestDocIdSetBuilder
specifier|public
class|class
name|TestDocIdSetBuilder
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|null
argument_list|,
operator|new
name|DocIdSetBuilder
argument_list|(
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertEquals
specifier|private
name|void
name|assertEquals
parameter_list|(
name|DocIdSet
name|d1
parameter_list|,
name|DocIdSet
name|d2
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|d1
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|d2
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|d2
operator|.
name|iterator
argument_list|()
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|d2
operator|==
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|d1
operator|.
name|iterator
argument_list|()
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DocIdSetIterator
name|i1
init|=
name|d1
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DocIdSetIterator
name|i2
init|=
name|d2
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|i1
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|i1
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|i2
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|i2
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSparse
specifier|public
name|void
name|testSparse
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
literal|1000000
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
decl_stmt|;
name|DocIdSetBuilder
name|builder
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numIterators
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|FixedBitSet
name|ref
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
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
name|numIterators
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|baseInc
init|=
literal|200000
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|RoaringDocIdSet
operator|.
name|Builder
name|b
init|=
operator|new
name|RoaringDocIdSet
operator|.
name|Builder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
init|;
name|doc
operator|<
name|maxDoc
condition|;
name|doc
operator|+=
name|baseInc
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
control|)
block|{
name|b
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|ref
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
name|b
operator|.
name|build
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DocIdSet
name|result
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|IntArrayDocIdSet
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|ref
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|testDense
specifier|public
name|void
name|testDense
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
literal|1000000
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000000
argument_list|)
decl_stmt|;
name|DocIdSetBuilder
name|builder
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numIterators
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|FixedBitSet
name|ref
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
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
name|numIterators
condition|;
operator|++
name|i
control|)
block|{
name|RoaringDocIdSet
operator|.
name|Builder
name|b
init|=
operator|new
name|RoaringDocIdSet
operator|.
name|Builder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
init|;
name|doc
operator|<
name|maxDoc
condition|;
name|doc
operator|+=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
control|)
block|{
name|b
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|ref
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
name|b
operator|.
name|build
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DocIdSet
name|result
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|BitDocIdSet
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|ref
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10000000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|maxDoc
operator|/
literal|2
condition|;
name|i
operator|<<=
literal|1
control|)
block|{
specifier|final
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|i
argument_list|)
decl_stmt|;
specifier|final
name|FixedBitSet
name|docs
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|c
operator|<
name|numDocs
condition|)
block|{
specifier|final
name|int
name|d
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|docs
operator|.
name|get
argument_list|(
name|d
argument_list|)
operator|==
literal|false
condition|)
block|{
name|docs
operator|.
name|set
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|c
operator|+=
literal|1
expr_stmt|;
block|}
block|}
specifier|final
name|int
index|[]
name|array
init|=
operator|new
name|int
index|[
name|numDocs
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
index|]
decl_stmt|;
name|DocIdSetIterator
name|it
init|=
operator|new
name|BitSetIterator
argument_list|(
name|docs
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|it
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|it
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|array
index|[
name|j
operator|++
index|]
operator|=
name|doc
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|j
argument_list|)
expr_stmt|;
comment|// add some duplicates
while|while
condition|(
name|j
operator|<
name|array
operator|.
name|length
condition|)
block|{
name|array
index|[
name|j
operator|++
index|]
operator|=
name|array
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
index|]
expr_stmt|;
block|}
comment|// shuffle
for|for
control|(
name|j
operator|=
name|array
operator|.
name|length
operator|-
literal|1
init|;
name|j
operator|>=
literal|1
condition|;
operator|--
name|j
control|)
block|{
specifier|final
name|int
name|k
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|int
name|tmp
init|=
name|array
index|[
name|j
index|]
decl_stmt|;
name|array
index|[
name|j
index|]
operator|=
name|array
index|[
name|k
index|]
expr_stmt|;
name|array
index|[
name|k
index|]
operator|=
name|tmp
expr_stmt|;
block|}
comment|// add docs out of order
name|DocIdSetBuilder
name|builder
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
for|for
control|(
name|j
operator|=
literal|0
init|;
name|j
operator|<
name|array
operator|.
name|length
condition|;
control|)
block|{
specifier|final
name|int
name|l
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|array
operator|.
name|length
operator|-
name|j
argument_list|)
decl_stmt|;
name|DocIdSetBuilder
operator|.
name|BulkAdder
name|adder
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|,
name|budget
init|=
literal|0
init|;
name|k
operator|<
name|l
condition|;
operator|++
name|k
control|)
block|{
if|if
condition|(
name|budget
operator|==
literal|0
operator|||
name|rarely
argument_list|()
condition|)
block|{
name|budget
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|l
operator|-
name|k
operator|+
literal|5
argument_list|)
expr_stmt|;
name|adder
operator|=
name|builder
operator|.
name|grow
argument_list|(
name|budget
argument_list|)
expr_stmt|;
block|}
name|adder
operator|.
name|add
argument_list|(
name|array
index|[
name|j
operator|++
index|]
argument_list|)
expr_stmt|;
name|budget
operator|--
expr_stmt|;
block|}
block|}
specifier|final
name|DocIdSet
name|expected
init|=
operator|new
name|BitDocIdSet
argument_list|(
name|docs
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSet
name|actual
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMisleadingDISICost
specifier|public
name|void
name|testMisleadingDISICost
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1000
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|DocIdSetBuilder
name|builder
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|FixedBitSet
name|expected
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|FixedBitSet
name|docs
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDoc
operator|/
literal|1000
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
name|numDocs
condition|;
operator|++
name|j
control|)
block|{
name|docs
operator|.
name|set
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|expected
operator|.
name|or
argument_list|(
name|docs
argument_list|)
expr_stmt|;
comment|// We provide a cost of 0 here to make sure the builder can deal with wrong costs
name|builder
operator|.
name|add
argument_list|(
operator|new
name|BitSetIterator
argument_list|(
name|docs
argument_list|,
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|expected
argument_list|)
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testLeverageStats
specifier|public
name|void
name|testLeverageStats
parameter_list|()
throws|throws
name|IOException
block|{
comment|// single-valued points
name|PointValues
name|values
init|=
operator|new
name|DummyPointValues
argument_list|(
literal|42
argument_list|,
literal|42
argument_list|)
decl_stmt|;
name|DocIdSetBuilder
name|builder
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
literal|100
argument_list|,
name|values
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1d
argument_list|,
name|builder
operator|.
name|numValuesPerDoc
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|multivalued
argument_list|)
expr_stmt|;
name|DocIdSetBuilder
operator|.
name|BulkAdder
name|adder
init|=
name|builder
operator|.
name|grow
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|adder
operator|.
name|add
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|adder
operator|.
name|add
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|DocIdSet
name|set
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|set
operator|instanceof
name|BitDocIdSet
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|set
operator|.
name|iterator
argument_list|()
operator|.
name|cost
argument_list|()
argument_list|)
expr_stmt|;
comment|// multi-valued points
name|values
operator|=
operator|new
name|DummyPointValues
argument_list|(
literal|42
argument_list|,
literal|63
argument_list|)
expr_stmt|;
name|builder
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
literal|100
argument_list|,
name|values
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.5
argument_list|,
name|builder
operator|.
name|numValuesPerDoc
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|multivalued
argument_list|)
expr_stmt|;
name|adder
operator|=
name|builder
operator|.
name|grow
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|adder
operator|.
name|add
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|adder
operator|.
name|add
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|set
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|instanceof
name|BitDocIdSet
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|set
operator|.
name|iterator
argument_list|()
operator|.
name|cost
argument_list|()
argument_list|)
expr_stmt|;
comment|// it thinks the same doc was added twice
comment|// incomplete stats
name|values
operator|=
operator|new
name|DummyPointValues
argument_list|(
literal|42
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|builder
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
literal|100
argument_list|,
name|values
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1d
argument_list|,
name|builder
operator|.
name|numValuesPerDoc
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|multivalued
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|DummyPointValues
argument_list|(
operator|-
literal|1
argument_list|,
literal|84
argument_list|)
expr_stmt|;
name|builder
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
literal|100
argument_list|,
name|values
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1d
argument_list|,
name|builder
operator|.
name|numValuesPerDoc
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|multivalued
argument_list|)
expr_stmt|;
comment|// single-valued terms
name|Terms
name|terms
init|=
operator|new
name|DummyTerms
argument_list|(
literal|42
argument_list|,
literal|42
argument_list|)
decl_stmt|;
name|builder
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
literal|100
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1d
argument_list|,
name|builder
operator|.
name|numValuesPerDoc
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|builder
operator|.
name|multivalued
argument_list|)
expr_stmt|;
name|adder
operator|=
name|builder
operator|.
name|grow
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|adder
operator|.
name|add
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|adder
operator|.
name|add
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|set
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|instanceof
name|BitDocIdSet
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|set
operator|.
name|iterator
argument_list|()
operator|.
name|cost
argument_list|()
argument_list|)
expr_stmt|;
comment|// multi-valued terms
name|terms
operator|=
operator|new
name|DummyTerms
argument_list|(
literal|42
argument_list|,
literal|63
argument_list|)
expr_stmt|;
name|builder
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
literal|100
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.5
argument_list|,
name|builder
operator|.
name|numValuesPerDoc
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|multivalued
argument_list|)
expr_stmt|;
name|adder
operator|=
name|builder
operator|.
name|grow
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|adder
operator|.
name|add
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|adder
operator|.
name|add
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|set
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|set
operator|instanceof
name|BitDocIdSet
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|set
operator|.
name|iterator
argument_list|()
operator|.
name|cost
argument_list|()
argument_list|)
expr_stmt|;
comment|// it thinks the same doc was added twice
comment|// incomplete stats
name|terms
operator|=
operator|new
name|DummyTerms
argument_list|(
literal|42
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|builder
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
literal|100
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1d
argument_list|,
name|builder
operator|.
name|numValuesPerDoc
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|multivalued
argument_list|)
expr_stmt|;
name|terms
operator|=
operator|new
name|DummyTerms
argument_list|(
operator|-
literal|1
argument_list|,
literal|84
argument_list|)
expr_stmt|;
name|builder
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
literal|100
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1d
argument_list|,
name|builder
operator|.
name|numValuesPerDoc
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|builder
operator|.
name|multivalued
argument_list|)
expr_stmt|;
block|}
DECL|class|DummyTerms
specifier|private
specifier|static
class|class
name|DummyTerms
extends|extends
name|Terms
block|{
DECL|field|docCount
specifier|private
specifier|final
name|int
name|docCount
decl_stmt|;
DECL|field|numValues
specifier|private
specifier|final
name|long
name|numValues
decl_stmt|;
DECL|method|DummyTerms
name|DummyTerms
parameter_list|(
name|int
name|docCount
parameter_list|,
name|long
name|numValues
parameter_list|)
block|{
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|numValues
operator|=
name|numValues
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getSumDocFreq
specifier|public
name|long
name|getSumDocFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|numValues
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|docCount
return|;
block|}
annotation|@
name|Override
DECL|method|hasFreqs
specifier|public
name|boolean
name|hasFreqs
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|hasOffsets
specifier|public
name|boolean
name|hasOffsets
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|hasPositions
specifier|public
name|boolean
name|hasPositions
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|class|DummyPointValues
specifier|private
specifier|static
class|class
name|DummyPointValues
extends|extends
name|PointValues
block|{
DECL|field|docCount
specifier|private
specifier|final
name|int
name|docCount
decl_stmt|;
DECL|field|numPoints
specifier|private
specifier|final
name|long
name|numPoints
decl_stmt|;
DECL|method|DummyPointValues
name|DummyPointValues
parameter_list|(
name|int
name|docCount
parameter_list|,
name|long
name|numPoints
parameter_list|)
block|{
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|numPoints
operator|=
name|numPoints
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|intersect
specifier|public
name|void
name|intersect
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getMinPackedValue
specifier|public
name|byte
index|[]
name|getMinPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getMaxPackedValue
specifier|public
name|byte
index|[]
name|getMaxPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getNumDimensions
specifier|public
name|int
name|getNumDimensions
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getBytesPerDimension
specifier|public
name|int
name|getBytesPerDimension
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|numPoints
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|docCount
return|;
block|}
block|}
block|}
end_class
end_unit
