begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|util
operator|.
name|Bits
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
name|util
operator|.
name|PriorityQueue
import|;
end_import
begin_comment
comment|/**  * {@link BulkScorer} that is used for pure disjunctions and disjunctions  * that have low values of {@link BooleanQuery.Builder#setMinimumNumberShouldMatch(int)}  * and dense clauses. This scorer scores documents by batches of 2048 docs.  */
end_comment
begin_class
DECL|class|BooleanScorer
specifier|final
class|class
name|BooleanScorer
extends|extends
name|BulkScorer
block|{
DECL|field|SHIFT
specifier|static
specifier|final
name|int
name|SHIFT
init|=
literal|11
decl_stmt|;
DECL|field|SIZE
specifier|static
specifier|final
name|int
name|SIZE
init|=
literal|1
operator|<<
name|SHIFT
decl_stmt|;
DECL|field|MASK
specifier|static
specifier|final
name|int
name|MASK
init|=
name|SIZE
operator|-
literal|1
decl_stmt|;
DECL|field|SET_SIZE
specifier|static
specifier|final
name|int
name|SET_SIZE
init|=
literal|1
operator|<<
operator|(
name|SHIFT
operator|-
literal|6
operator|)
decl_stmt|;
DECL|field|SET_MASK
specifier|static
specifier|final
name|int
name|SET_MASK
init|=
name|SET_SIZE
operator|-
literal|1
decl_stmt|;
DECL|class|Bucket
specifier|static
class|class
name|Bucket
block|{
DECL|field|score
name|double
name|score
decl_stmt|;
DECL|field|freq
name|int
name|freq
decl_stmt|;
block|}
DECL|class|BulkScorerAndDoc
specifier|private
class|class
name|BulkScorerAndDoc
block|{
DECL|field|scorer
specifier|final
name|BulkScorer
name|scorer
decl_stmt|;
DECL|field|cost
specifier|final
name|long
name|cost
decl_stmt|;
DECL|field|next
name|int
name|next
decl_stmt|;
DECL|method|BulkScorerAndDoc
name|BulkScorerAndDoc
parameter_list|(
name|BulkScorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|this
operator|.
name|cost
operator|=
name|scorer
operator|.
name|cost
argument_list|()
expr_stmt|;
name|this
operator|.
name|next
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|advance
name|void
name|advance
parameter_list|(
name|int
name|min
parameter_list|)
throws|throws
name|IOException
block|{
name|score
argument_list|(
name|orCollector
argument_list|,
literal|null
argument_list|,
name|min
argument_list|,
name|min
argument_list|)
expr_stmt|;
block|}
DECL|method|score
name|void
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
name|next
operator|=
name|scorer
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|acceptDocs
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
block|}
comment|// See MinShouldMatchSumScorer for an explanation
DECL|method|cost
specifier|private
specifier|static
name|long
name|cost
parameter_list|(
name|Collection
argument_list|<
name|BulkScorer
argument_list|>
name|scorers
parameter_list|,
name|int
name|minShouldMatch
parameter_list|)
block|{
specifier|final
name|PriorityQueue
argument_list|<
name|BulkScorer
argument_list|>
name|pq
init|=
operator|new
name|PriorityQueue
argument_list|<
name|BulkScorer
argument_list|>
argument_list|(
name|scorers
operator|.
name|size
argument_list|()
operator|-
name|minShouldMatch
operator|+
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|BulkScorer
name|a
parameter_list|,
name|BulkScorer
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|cost
argument_list|()
operator|>
name|b
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|BulkScorer
name|scorer
range|:
name|scorers
control|)
block|{
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
name|long
name|cost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BulkScorer
name|scorer
init|=
name|pq
operator|.
name|pop
argument_list|()
init|;
name|scorer
operator|!=
literal|null
condition|;
name|scorer
operator|=
name|pq
operator|.
name|pop
argument_list|()
control|)
block|{
name|cost
operator|+=
name|scorer
operator|.
name|cost
argument_list|()
expr_stmt|;
block|}
return|return
name|cost
return|;
block|}
DECL|class|HeadPriorityQueue
specifier|static
specifier|final
class|class
name|HeadPriorityQueue
extends|extends
name|PriorityQueue
argument_list|<
name|BulkScorerAndDoc
argument_list|>
block|{
DECL|method|HeadPriorityQueue
specifier|public
name|HeadPriorityQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|BulkScorerAndDoc
name|a
parameter_list|,
name|BulkScorerAndDoc
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|next
operator|<
name|b
operator|.
name|next
return|;
block|}
block|}
DECL|class|TailPriorityQueue
specifier|static
specifier|final
class|class
name|TailPriorityQueue
extends|extends
name|PriorityQueue
argument_list|<
name|BulkScorerAndDoc
argument_list|>
block|{
DECL|method|TailPriorityQueue
specifier|public
name|TailPriorityQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|BulkScorerAndDoc
name|a
parameter_list|,
name|BulkScorerAndDoc
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|cost
operator|<
name|b
operator|.
name|cost
return|;
block|}
DECL|method|get
specifier|public
name|BulkScorerAndDoc
name|get
parameter_list|(
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|i
operator|<
literal|0
operator|||
name|i
operator|>=
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
return|return
operator|(
name|BulkScorerAndDoc
operator|)
name|getHeapArray
argument_list|()
index|[
literal|1
operator|+
name|i
index|]
return|;
block|}
block|}
DECL|field|buckets
specifier|final
name|Bucket
index|[]
name|buckets
init|=
operator|new
name|Bucket
index|[
name|SIZE
index|]
decl_stmt|;
comment|// This is basically an inlined FixedBitSet... seems to help with bound checks
DECL|field|matching
specifier|final
name|long
index|[]
name|matching
init|=
operator|new
name|long
index|[
name|SET_SIZE
index|]
decl_stmt|;
DECL|field|coordFactors
specifier|final
name|float
index|[]
name|coordFactors
decl_stmt|;
DECL|field|leads
specifier|final
name|BulkScorerAndDoc
index|[]
name|leads
decl_stmt|;
DECL|field|head
specifier|final
name|HeadPriorityQueue
name|head
decl_stmt|;
DECL|field|tail
specifier|final
name|TailPriorityQueue
name|tail
decl_stmt|;
DECL|field|fakeScorer
specifier|final
name|FakeScorer
name|fakeScorer
init|=
operator|new
name|FakeScorer
argument_list|()
decl_stmt|;
DECL|field|minShouldMatch
specifier|final
name|int
name|minShouldMatch
decl_stmt|;
DECL|field|cost
specifier|final
name|long
name|cost
decl_stmt|;
DECL|class|OrCollector
specifier|final
class|class
name|OrCollector
implements|implements
name|LeafCollector
block|{
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|i
init|=
name|doc
operator|&
name|MASK
decl_stmt|;
specifier|final
name|int
name|idx
init|=
name|i
operator|>>>
literal|6
decl_stmt|;
name|matching
index|[
name|idx
index|]
operator||=
literal|1L
operator|<<
name|i
expr_stmt|;
specifier|final
name|Bucket
name|bucket
init|=
name|buckets
index|[
name|i
index|]
decl_stmt|;
name|bucket
operator|.
name|freq
operator|++
expr_stmt|;
name|bucket
operator|.
name|score
operator|+=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|orCollector
specifier|final
name|OrCollector
name|orCollector
init|=
operator|new
name|OrCollector
argument_list|()
decl_stmt|;
DECL|method|BooleanScorer
name|BooleanScorer
parameter_list|(
name|BooleanWeight
name|weight
parameter_list|,
name|boolean
name|disableCoord
parameter_list|,
name|int
name|maxCoord
parameter_list|,
name|Collection
argument_list|<
name|BulkScorer
argument_list|>
name|scorers
parameter_list|,
name|int
name|minShouldMatch
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
block|{
if|if
condition|(
name|minShouldMatch
argument_list|<
literal|1
operator|||
name|minShouldMatch
argument_list|>
name|scorers
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minShouldMatch should be within 1..num_scorers. Got "
operator|+
name|minShouldMatch
argument_list|)
throw|;
block|}
if|if
condition|(
name|scorers
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"This scorer can only be used with two scorers or more, got "
operator|+
name|scorers
operator|.
name|size
argument_list|()
argument_list|)
throw|;
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
name|buckets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buckets
index|[
name|i
index|]
operator|=
operator|new
name|Bucket
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|leads
operator|=
operator|new
name|BulkScorerAndDoc
index|[
name|scorers
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|this
operator|.
name|head
operator|=
operator|new
name|HeadPriorityQueue
argument_list|(
name|scorers
operator|.
name|size
argument_list|()
operator|-
name|minShouldMatch
operator|+
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|tail
operator|=
operator|new
name|TailPriorityQueue
argument_list|(
name|minShouldMatch
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|minShouldMatch
operator|=
name|minShouldMatch
expr_stmt|;
for|for
control|(
name|BulkScorer
name|scorer
range|:
name|scorers
control|)
block|{
if|if
condition|(
name|needsScores
operator|==
literal|false
condition|)
block|{
comment|// OrCollector calls score() all the time so we have to explicitly
comment|// disable scoring in order to avoid decoding useless norms
name|scorer
operator|=
name|BooleanWeight
operator|.
name|disableScoring
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BulkScorerAndDoc
name|evicted
init|=
name|tail
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|BulkScorerAndDoc
argument_list|(
name|scorer
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|evicted
operator|!=
literal|null
condition|)
block|{
name|head
operator|.
name|add
argument_list|(
name|evicted
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|cost
operator|=
name|cost
argument_list|(
name|scorers
argument_list|,
name|minShouldMatch
argument_list|)
expr_stmt|;
name|coordFactors
operator|=
operator|new
name|float
index|[
name|scorers
operator|.
name|size
argument_list|()
operator|+
literal|1
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|coordFactors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|coordFactors
index|[
name|i
index|]
operator|=
name|disableCoord
condition|?
literal|1.0f
else|:
name|weight
operator|.
name|coord
argument_list|(
name|i
argument_list|,
name|maxCoord
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
DECL|method|scoreDocument
specifier|private
name|void
name|scoreDocument
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|int
name|base
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FakeScorer
name|fakeScorer
init|=
name|this
operator|.
name|fakeScorer
decl_stmt|;
specifier|final
name|Bucket
name|bucket
init|=
name|buckets
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|bucket
operator|.
name|freq
operator|>=
name|minShouldMatch
condition|)
block|{
name|fakeScorer
operator|.
name|freq
operator|=
name|bucket
operator|.
name|freq
expr_stmt|;
name|fakeScorer
operator|.
name|score
operator|=
operator|(
name|float
operator|)
name|bucket
operator|.
name|score
operator|*
name|coordFactors
index|[
name|bucket
operator|.
name|freq
index|]
expr_stmt|;
specifier|final
name|int
name|doc
init|=
name|base
operator||
name|i
decl_stmt|;
name|fakeScorer
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|bucket
operator|.
name|freq
operator|=
literal|0
expr_stmt|;
name|bucket
operator|.
name|score
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|scoreMatches
specifier|private
name|void
name|scoreMatches
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|int
name|base
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|matching
index|[]
init|=
name|this
operator|.
name|matching
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|matching
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
name|long
name|bits
init|=
name|matching
index|[
name|idx
index|]
decl_stmt|;
while|while
condition|(
name|bits
operator|!=
literal|0L
condition|)
block|{
name|int
name|ntz
init|=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|bits
argument_list|)
decl_stmt|;
name|int
name|doc
init|=
name|idx
operator|<<
literal|6
operator||
name|ntz
decl_stmt|;
name|scoreDocument
argument_list|(
name|collector
argument_list|,
name|base
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|bits
operator|^=
literal|1L
operator|<<
name|ntz
expr_stmt|;
block|}
block|}
block|}
DECL|method|scoreWindowIntoBitSetAndReplay
specifier|private
name|void
name|scoreWindowIntoBitSetAndReplay
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|base
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|,
name|BulkScorerAndDoc
index|[]
name|scorers
parameter_list|,
name|int
name|numScorers
parameter_list|)
throws|throws
name|IOException
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
name|numScorers
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|BulkScorerAndDoc
name|scorer
init|=
name|scorers
index|[
name|i
index|]
decl_stmt|;
assert|assert
name|scorer
operator|.
name|next
operator|<
name|max
assert|;
name|scorer
operator|.
name|score
argument_list|(
name|orCollector
argument_list|,
name|acceptDocs
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
name|scoreMatches
argument_list|(
name|collector
argument_list|,
name|base
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|matching
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
DECL|method|advance
specifier|private
name|BulkScorerAndDoc
name|advance
parameter_list|(
name|int
name|min
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|tail
operator|.
name|size
argument_list|()
operator|==
name|minShouldMatch
operator|-
literal|1
assert|;
specifier|final
name|HeadPriorityQueue
name|head
init|=
name|this
operator|.
name|head
decl_stmt|;
specifier|final
name|TailPriorityQueue
name|tail
init|=
name|this
operator|.
name|tail
decl_stmt|;
name|BulkScorerAndDoc
name|headTop
init|=
name|head
operator|.
name|top
argument_list|()
decl_stmt|;
name|BulkScorerAndDoc
name|tailTop
init|=
name|tail
operator|.
name|top
argument_list|()
decl_stmt|;
while|while
condition|(
name|headTop
operator|.
name|next
operator|<
name|min
condition|)
block|{
if|if
condition|(
name|tailTop
operator|==
literal|null
operator|||
name|headTop
operator|.
name|cost
operator|<=
name|tailTop
operator|.
name|cost
condition|)
block|{
name|headTop
operator|.
name|advance
argument_list|(
name|min
argument_list|)
expr_stmt|;
name|headTop
operator|=
name|head
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// swap the top of head and tail
specifier|final
name|BulkScorerAndDoc
name|previousHeadTop
init|=
name|headTop
decl_stmt|;
name|tailTop
operator|.
name|advance
argument_list|(
name|min
argument_list|)
expr_stmt|;
name|headTop
operator|=
name|head
operator|.
name|updateTop
argument_list|(
name|tailTop
argument_list|)
expr_stmt|;
name|tailTop
operator|=
name|tail
operator|.
name|updateTop
argument_list|(
name|previousHeadTop
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|headTop
return|;
block|}
DECL|method|scoreWindowMultipleScorers
specifier|private
name|void
name|scoreWindowMultipleScorers
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|windowBase
parameter_list|,
name|int
name|windowMin
parameter_list|,
name|int
name|windowMax
parameter_list|,
name|int
name|maxFreq
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|maxFreq
operator|<
name|minShouldMatch
operator|&&
name|maxFreq
operator|+
name|tail
operator|.
name|size
argument_list|()
operator|>=
name|minShouldMatch
condition|)
block|{
comment|// a match is still possible
specifier|final
name|BulkScorerAndDoc
name|candidate
init|=
name|tail
operator|.
name|pop
argument_list|()
decl_stmt|;
name|candidate
operator|.
name|advance
argument_list|(
name|windowMin
argument_list|)
expr_stmt|;
if|if
condition|(
name|candidate
operator|.
name|next
operator|<
name|windowMax
condition|)
block|{
name|leads
index|[
name|maxFreq
operator|++
index|]
operator|=
name|candidate
expr_stmt|;
block|}
else|else
block|{
name|head
operator|.
name|add
argument_list|(
name|candidate
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|maxFreq
operator|>=
name|minShouldMatch
condition|)
block|{
comment|// There might be matches in other scorers from the tail too
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tail
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|leads
index|[
name|maxFreq
operator|++
index|]
operator|=
name|tail
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|tail
operator|.
name|clear
argument_list|()
expr_stmt|;
name|scoreWindowIntoBitSetAndReplay
argument_list|(
name|collector
argument_list|,
name|acceptDocs
argument_list|,
name|windowBase
argument_list|,
name|windowMin
argument_list|,
name|windowMax
argument_list|,
name|leads
argument_list|,
name|maxFreq
argument_list|)
expr_stmt|;
block|}
comment|// Push back scorers into head and tail
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxFreq
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|BulkScorerAndDoc
name|evicted
init|=
name|head
operator|.
name|insertWithOverflow
argument_list|(
name|leads
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|evicted
operator|!=
literal|null
condition|)
block|{
name|tail
operator|.
name|add
argument_list|(
name|evicted
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|scoreWindowSingleScorer
specifier|private
name|void
name|scoreWindowSingleScorer
parameter_list|(
name|BulkScorerAndDoc
name|bulkScorer
parameter_list|,
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|windowMin
parameter_list|,
name|int
name|windowMax
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|tail
operator|.
name|size
argument_list|()
operator|==
literal|0
assert|;
specifier|final
name|int
name|nextWindowBase
init|=
name|head
operator|.
name|top
argument_list|()
operator|.
name|next
operator|&
operator|~
name|MASK
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|Math
operator|.
name|max
argument_list|(
name|windowMax
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|max
argument_list|,
name|nextWindowBase
argument_list|)
argument_list|)
decl_stmt|;
name|bulkScorer
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|acceptDocs
argument_list|,
name|windowMin
argument_list|,
name|end
argument_list|)
expr_stmt|;
comment|// reset the scorer that should be used for the general case
name|collector
operator|.
name|setScorer
argument_list|(
name|fakeScorer
argument_list|)
expr_stmt|;
block|}
DECL|method|scoreWindow
specifier|private
name|BulkScorerAndDoc
name|scoreWindow
parameter_list|(
name|BulkScorerAndDoc
name|top
parameter_list|,
name|LeafCollector
name|collector
parameter_list|,
name|LeafCollector
name|singleClauseCollector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|windowBase
init|=
name|top
operator|.
name|next
operator|&
operator|~
name|MASK
decl_stmt|;
comment|// find the window that the next match belongs to
specifier|final
name|int
name|windowMin
init|=
name|Math
operator|.
name|max
argument_list|(
name|min
argument_list|,
name|windowBase
argument_list|)
decl_stmt|;
specifier|final
name|int
name|windowMax
init|=
name|Math
operator|.
name|min
argument_list|(
name|max
argument_list|,
name|windowBase
operator|+
name|SIZE
argument_list|)
decl_stmt|;
comment|// Fill 'leads' with all scorers from 'head' that are in the right window
name|leads
index|[
literal|0
index|]
operator|=
name|head
operator|.
name|pop
argument_list|()
expr_stmt|;
name|int
name|maxFreq
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|head
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|head
operator|.
name|top
argument_list|()
operator|.
name|next
operator|<
name|windowMax
condition|)
block|{
name|leads
index|[
name|maxFreq
operator|++
index|]
operator|=
name|head
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|minShouldMatch
operator|==
literal|1
operator|&&
name|maxFreq
operator|==
literal|1
condition|)
block|{
comment|// special case: only one scorer can match in the current window,
comment|// we can collect directly
specifier|final
name|BulkScorerAndDoc
name|bulkScorer
init|=
name|leads
index|[
literal|0
index|]
decl_stmt|;
name|scoreWindowSingleScorer
argument_list|(
name|bulkScorer
argument_list|,
name|singleClauseCollector
argument_list|,
name|acceptDocs
argument_list|,
name|windowMin
argument_list|,
name|windowMax
argument_list|,
name|max
argument_list|)
expr_stmt|;
return|return
name|head
operator|.
name|add
argument_list|(
name|bulkScorer
argument_list|)
return|;
block|}
else|else
block|{
comment|// general case, collect through a bit set first and then replay
name|scoreWindowMultipleScorers
argument_list|(
name|collector
argument_list|,
name|acceptDocs
argument_list|,
name|windowBase
argument_list|,
name|windowMin
argument_list|,
name|windowMax
argument_list|,
name|maxFreq
argument_list|)
expr_stmt|;
return|return
name|head
operator|.
name|top
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|int
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
name|fakeScorer
operator|.
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
name|collector
operator|.
name|setScorer
argument_list|(
name|fakeScorer
argument_list|)
expr_stmt|;
specifier|final
name|LeafCollector
name|singleClauseCollector
decl_stmt|;
if|if
condition|(
name|coordFactors
index|[
literal|1
index|]
operator|==
literal|1f
condition|)
block|{
name|singleClauseCollector
operator|=
name|collector
expr_stmt|;
block|}
else|else
block|{
name|singleClauseCollector
operator|=
operator|new
name|FilterLeafCollector
argument_list|(
name|collector
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setScorer
argument_list|(
operator|new
name|BooleanTopLevelScorers
operator|.
name|BoostedScorer
argument_list|(
name|scorer
argument_list|,
name|coordFactors
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
name|BulkScorerAndDoc
name|top
init|=
name|advance
argument_list|(
name|min
argument_list|)
decl_stmt|;
while|while
condition|(
name|top
operator|.
name|next
operator|<
name|max
condition|)
block|{
name|top
operator|=
name|scoreWindow
argument_list|(
name|top
argument_list|,
name|collector
argument_list|,
name|singleClauseCollector
argument_list|,
name|acceptDocs
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
return|return
name|top
operator|.
name|next
return|;
block|}
block|}
end_class
end_unit
