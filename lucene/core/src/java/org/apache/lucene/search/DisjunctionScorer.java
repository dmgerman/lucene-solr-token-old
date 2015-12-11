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
name|util
operator|.
name|PriorityQueue
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
name|DisiPriorityQueue
name|subScorers
decl_stmt|;
DECL|field|approximation
specifier|private
specifier|final
name|DisjunctionDISIApproximation
name|approximation
decl_stmt|;
DECL|field|twoPhase
specifier|private
specifier|final
name|TwoPhase
name|twoPhase
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
name|DisiPriorityQueue
argument_list|(
name|subScorers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Scorer
name|scorer
range|:
name|subScorers
control|)
block|{
specifier|final
name|DisiWrapper
name|w
init|=
operator|new
name|DisiWrapper
argument_list|(
name|scorer
argument_list|)
decl_stmt|;
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
name|needsScores
operator|=
name|needsScores
expr_stmt|;
name|this
operator|.
name|approximation
operator|=
operator|new
name|DisjunctionDISIApproximation
argument_list|(
name|this
operator|.
name|subScorers
argument_list|)
expr_stmt|;
name|boolean
name|hasApproximation
init|=
literal|false
decl_stmt|;
name|float
name|sumMatchCost
init|=
literal|0
decl_stmt|;
name|long
name|sumApproxCost
init|=
literal|0
decl_stmt|;
comment|// Compute matchCost as the average over the matchCost of the subScorers.
comment|// This is weighted by the cost, which is an expected number of matching documents.
for|for
control|(
name|DisiWrapper
name|w
range|:
name|this
operator|.
name|subScorers
control|)
block|{
name|long
name|costWeight
init|=
operator|(
name|w
operator|.
name|cost
operator|<=
literal|1
operator|)
condition|?
literal|1
else|:
name|w
operator|.
name|cost
decl_stmt|;
name|sumApproxCost
operator|+=
name|costWeight
expr_stmt|;
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
name|sumMatchCost
operator|+=
name|w
operator|.
name|matchCost
operator|*
name|costWeight
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hasApproximation
operator|==
literal|false
condition|)
block|{
comment|// no sub scorer supports approximations
name|twoPhase
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|float
name|matchCost
init|=
name|sumMatchCost
operator|/
name|sumApproxCost
decl_stmt|;
name|twoPhase
operator|=
operator|new
name|TwoPhase
argument_list|(
name|approximation
argument_list|,
name|matchCost
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
if|if
condition|(
name|twoPhase
operator|!=
literal|null
condition|)
block|{
return|return
name|TwoPhaseIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhase
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|approximation
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|twoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|twoPhaseIterator
parameter_list|()
block|{
return|return
name|twoPhase
return|;
block|}
DECL|class|TwoPhase
specifier|private
class|class
name|TwoPhase
extends|extends
name|TwoPhaseIterator
block|{
DECL|field|matchCost
specifier|private
specifier|final
name|float
name|matchCost
decl_stmt|;
comment|// list of verified matches on the current doc
DECL|field|verifiedMatches
name|DisiWrapper
name|verifiedMatches
decl_stmt|;
comment|// priority queue of approximations on the current doc that have not been verified yet
DECL|field|unverifiedMatches
specifier|final
name|PriorityQueue
argument_list|<
name|DisiWrapper
argument_list|>
name|unverifiedMatches
decl_stmt|;
DECL|method|TwoPhase
specifier|private
name|TwoPhase
parameter_list|(
name|DocIdSetIterator
name|approximation
parameter_list|,
name|float
name|matchCost
parameter_list|)
block|{
name|super
argument_list|(
name|approximation
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchCost
operator|=
name|matchCost
expr_stmt|;
name|unverifiedMatches
operator|=
operator|new
name|PriorityQueue
argument_list|<
name|DisiWrapper
argument_list|>
argument_list|(
name|DisjunctionScorer
operator|.
name|this
operator|.
name|subScorers
operator|.
name|size
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|DisiWrapper
name|a
parameter_list|,
name|DisiWrapper
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|matchCost
operator|<
name|b
operator|.
name|matchCost
return|;
block|}
block|}
expr_stmt|;
block|}
DECL|method|getSubMatches
name|DisiWrapper
name|getSubMatches
parameter_list|()
throws|throws
name|IOException
block|{
comment|// iteration order does not matter
for|for
control|(
name|DisiWrapper
name|w
range|:
name|unverifiedMatches
control|)
block|{
if|if
condition|(
name|w
operator|.
name|twoPhaseView
operator|.
name|matches
argument_list|()
condition|)
block|{
name|w
operator|.
name|next
operator|=
name|verifiedMatches
expr_stmt|;
name|verifiedMatches
operator|=
name|w
expr_stmt|;
block|}
block|}
name|unverifiedMatches
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|verifiedMatches
return|;
block|}
annotation|@
name|Override
DECL|method|matches
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
name|verifiedMatches
operator|=
literal|null
expr_stmt|;
name|unverifiedMatches
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|DisiWrapper
name|w
init|=
name|subScorers
operator|.
name|topList
argument_list|()
init|;
name|w
operator|!=
literal|null
condition|;
control|)
block|{
name|DisiWrapper
name|next
init|=
name|w
operator|.
name|next
decl_stmt|;
if|if
condition|(
name|w
operator|.
name|twoPhaseView
operator|==
literal|null
condition|)
block|{
comment|// implicitly verified, move it to verifiedMatches
name|w
operator|.
name|next
operator|=
name|verifiedMatches
expr_stmt|;
name|verifiedMatches
operator|=
name|w
expr_stmt|;
if|if
condition|(
name|needsScores
operator|==
literal|false
condition|)
block|{
comment|// we can stop here
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
name|unverifiedMatches
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
name|w
operator|=
name|next
expr_stmt|;
block|}
if|if
condition|(
name|verifiedMatches
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// verify subs that have an two-phase iterator
comment|// least-costly ones first
while|while
condition|(
name|unverifiedMatches
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DisiWrapper
name|w
init|=
name|unverifiedMatches
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|w
operator|.
name|twoPhaseView
operator|.
name|matches
argument_list|()
condition|)
block|{
name|w
operator|.
name|next
operator|=
literal|null
expr_stmt|;
name|verifiedMatches
operator|=
name|w
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|matchCost
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
name|matchCost
return|;
block|}
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
DECL|method|getSubMatches
name|DisiWrapper
name|getSubMatches
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|twoPhase
operator|==
literal|null
condition|)
block|{
return|return
name|subScorers
operator|.
name|topList
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|twoPhase
operator|.
name|getSubMatches
argument_list|()
return|;
block|}
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
name|DisiWrapper
name|subMatches
init|=
name|getSubMatches
argument_list|()
decl_stmt|;
name|int
name|freq
init|=
literal|1
decl_stmt|;
for|for
control|(
name|DisiWrapper
name|w
init|=
name|subMatches
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
return|return
name|score
argument_list|(
name|getSubMatches
argument_list|()
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
name|DisiWrapper
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
name|DisiWrapper
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
block|}
end_class
end_unit
