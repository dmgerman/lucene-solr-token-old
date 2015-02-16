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
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|List
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
name|ScorerPriorityQueue
operator|.
name|ScorerWrapper
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
name|BytesRef
import|;
end_import
begin_comment
comment|/**  * Base class for Scorers that score disjunctions.  */
end_comment
begin_class
DECL|class|DisjunctionScorer
specifier|abstract
class|class
name|DisjunctionScorer
extends|extends
name|Scorer
block|{
DECL|field|needsScores
specifier|private
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|field|subScorers
specifier|private
specifier|final
name|ScorerPriorityQueue
name|subScorers
decl_stmt|;
DECL|field|cost
specifier|private
specifier|final
name|long
name|cost
decl_stmt|;
comment|/** Linked list of scorers which are on the current doc */
DECL|field|topScorers
specifier|private
name|ScorerWrapper
name|topScorers
decl_stmt|;
DECL|method|DisjunctionScorer
specifier|protected
name|DisjunctionScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|subScorers
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
if|if
condition|(
name|subScorers
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
literal|"There must be at least 2 subScorers"
argument_list|)
throw|;
block|}
name|this
operator|.
name|subScorers
operator|=
operator|new
name|ScorerPriorityQueue
argument_list|(
name|subScorers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|cost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Scorer
name|scorer
range|:
name|subScorers
control|)
block|{
specifier|final
name|ScorerWrapper
name|w
init|=
operator|new
name|ScorerWrapper
argument_list|(
name|scorer
argument_list|)
decl_stmt|;
name|cost
operator|+=
name|w
operator|.
name|cost
expr_stmt|;
name|this
operator|.
name|subScorers
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
name|this
operator|.
name|needsScores
operator|=
name|needsScores
expr_stmt|;
block|}
comment|/**    * A {@link DocIdSetIterator} which is a disjunction of the approximations of    * the provided iterators.    */
DECL|class|DisjunctionDISIApproximation
specifier|private
specifier|static
class|class
name|DisjunctionDISIApproximation
extends|extends
name|DocIdSetIterator
block|{
DECL|field|subScorers
specifier|final
name|ScorerPriorityQueue
name|subScorers
decl_stmt|;
DECL|field|cost
specifier|final
name|long
name|cost
decl_stmt|;
DECL|method|DisjunctionDISIApproximation
name|DisjunctionDISIApproximation
parameter_list|(
name|ScorerPriorityQueue
name|subScorers
parameter_list|)
block|{
name|this
operator|.
name|subScorers
operator|=
name|subScorers
expr_stmt|;
name|long
name|cost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ScorerWrapper
name|w
range|:
name|subScorers
control|)
block|{
name|cost
operator|+=
name|w
operator|.
name|cost
expr_stmt|;
block|}
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
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
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|subScorers
operator|.
name|top
argument_list|()
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|ScorerWrapper
name|top
init|=
name|subScorers
operator|.
name|top
argument_list|()
decl_stmt|;
specifier|final
name|int
name|doc
init|=
name|top
operator|.
name|doc
decl_stmt|;
do|do
block|{
name|top
operator|.
name|doc
operator|=
name|top
operator|.
name|approximation
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|top
operator|=
name|subScorers
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|top
operator|.
name|doc
operator|==
name|doc
condition|)
do|;
return|return
name|top
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|ScorerWrapper
name|top
init|=
name|subScorers
operator|.
name|top
argument_list|()
decl_stmt|;
do|do
block|{
name|top
operator|.
name|doc
operator|=
name|top
operator|.
name|approximation
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|top
operator|=
name|subScorers
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|top
operator|.
name|doc
operator|<
name|target
condition|)
do|;
return|return
name|top
operator|.
name|doc
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseDocIdSetIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
name|boolean
name|hasApproximation
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ScorerWrapper
name|w
range|:
name|subScorers
control|)
block|{
if|if
condition|(
name|w
operator|.
name|twoPhaseView
operator|!=
literal|null
condition|)
block|{
name|hasApproximation
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|hasApproximation
operator|==
literal|false
condition|)
block|{
comment|// none of the sub scorers supports approximations
return|return
literal|null
return|;
block|}
return|return
operator|new
name|TwoPhaseDocIdSetIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|approximation
parameter_list|()
block|{
comment|// note it is important to share the same pq as this scorer so that
comment|// rebalancing the pq through the approximation will also rebalance
comment|// the pq in this scorer.
return|return
operator|new
name|DisjunctionDISIApproximation
argument_list|(
name|subScorers
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
name|ScorerWrapper
name|topScorers
init|=
name|subScorers
operator|.
name|topList
argument_list|()
decl_stmt|;
comment|// remove the head of the list as long as it does not match
while|while
condition|(
name|topScorers
operator|.
name|twoPhaseView
operator|!=
literal|null
operator|&&
name|topScorers
operator|.
name|twoPhaseView
operator|.
name|matches
argument_list|()
operator|==
literal|false
condition|)
block|{
name|topScorers
operator|=
name|topScorers
operator|.
name|next
expr_stmt|;
if|if
condition|(
name|topScorers
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// now we know we have at least one match since the first element of 'matchList' matches
if|if
condition|(
name|needsScores
condition|)
block|{
comment|// if scores or freqs are needed, we also need to remove scorers
comment|// from the top list that do not actually match
name|ScorerWrapper
name|previous
init|=
name|topScorers
decl_stmt|;
for|for
control|(
name|ScorerWrapper
name|w
init|=
name|topScorers
operator|.
name|next
init|;
name|w
operator|!=
literal|null
condition|;
name|w
operator|=
name|w
operator|.
name|next
control|)
block|{
if|if
condition|(
name|w
operator|.
name|twoPhaseView
operator|!=
literal|null
operator|&&
name|w
operator|.
name|twoPhaseView
operator|.
name|matches
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// w does not match, remove it
name|previous
operator|.
name|next
operator|=
name|w
operator|.
name|next
expr_stmt|;
block|}
else|else
block|{
name|previous
operator|=
name|w
expr_stmt|;
block|}
block|}
comment|// We need to explicitely set the list of top scorers to avoid the
comment|// laziness of DisjunctionScorer.score() that would take all scorers
comment|// positioned on the same doc as the top of the pq, including
comment|// non-matching scorers
name|DisjunctionScorer
operator|.
name|this
operator|.
name|topScorers
operator|=
name|topScorers
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
specifier|final
name|long
name|cost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
specifier|final
name|int
name|docID
parameter_list|()
block|{
return|return
name|subScorers
operator|.
name|top
argument_list|()
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
specifier|final
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|topScorers
operator|=
literal|null
expr_stmt|;
name|ScorerWrapper
name|top
init|=
name|subScorers
operator|.
name|top
argument_list|()
decl_stmt|;
specifier|final
name|int
name|doc
init|=
name|top
operator|.
name|doc
decl_stmt|;
do|do
block|{
name|top
operator|.
name|doc
operator|=
name|top
operator|.
name|scorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|top
operator|=
name|subScorers
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|top
operator|.
name|doc
operator|==
name|doc
condition|)
do|;
return|return
name|top
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
specifier|final
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|topScorers
operator|=
literal|null
expr_stmt|;
name|ScorerWrapper
name|top
init|=
name|subScorers
operator|.
name|top
argument_list|()
decl_stmt|;
do|do
block|{
name|top
operator|.
name|doc
operator|=
name|top
operator|.
name|scorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|top
operator|=
name|subScorers
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|top
operator|.
name|doc
operator|<
name|target
condition|)
do|;
return|return
name|top
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
specifier|final
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|topScorers
operator|==
literal|null
condition|)
block|{
name|topScorers
operator|=
name|subScorers
operator|.
name|topList
argument_list|()
expr_stmt|;
block|}
name|int
name|freq
init|=
literal|1
decl_stmt|;
for|for
control|(
name|ScorerWrapper
name|w
init|=
name|topScorers
operator|.
name|next
init|;
name|w
operator|!=
literal|null
condition|;
name|w
operator|=
name|w
operator|.
name|next
control|)
block|{
name|freq
operator|+=
literal|1
expr_stmt|;
block|}
return|return
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|topScorers
operator|==
literal|null
condition|)
block|{
name|topScorers
operator|=
name|subScorers
operator|.
name|topList
argument_list|()
expr_stmt|;
block|}
return|return
name|score
argument_list|(
name|topScorers
argument_list|)
return|;
block|}
comment|/** Compute the score for the given linked list of scorers. */
DECL|method|score
specifier|protected
specifier|abstract
name|float
name|score
parameter_list|(
name|ScorerWrapper
name|topList
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|getChildren
specifier|public
specifier|final
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|ChildScorer
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ScorerWrapper
name|scorer
range|:
name|subScorers
control|)
block|{
name|children
operator|.
name|add
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|scorer
operator|.
name|scorer
argument_list|,
literal|"SHOULD"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|children
return|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|startOffset
specifier|public
name|int
name|startOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|endOffset
specifier|public
name|int
name|endOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
