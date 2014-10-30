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
name|BitDocIdSet
operator|.
name|Builder
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
DECL|method|testFull
specifier|public
name|void
name|testFull
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|maxDoc
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DocIdSet
name|set
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|DocIdSetIterator
name|it
init|=
name|set
operator|.
name|iterator
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
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|it
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
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
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
name|or
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
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
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
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// try upgrades
specifier|final
name|int
name|doc
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|ref
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|builder
operator|.
name|or
argument_list|(
operator|new
name|RoaringDocIdSet
operator|.
name|Builder
argument_list|(
name|maxDoc
argument_list|)
operator|.
name|add
argument_list|(
name|doc
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
literal|1000
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
name|or
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
block|}
end_class
end_unit
