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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
operator|.
name|Spans
import|;
end_import
begin_comment
comment|/**  * Wrapper used in {@link DisiPriorityQueue}.  * @lucene.internal  */
end_comment
begin_class
DECL|class|DisiWrapper
specifier|public
class|class
name|DisiWrapper
block|{
DECL|field|iterator
specifier|public
specifier|final
name|DocIdSetIterator
name|iterator
decl_stmt|;
DECL|field|scorer
specifier|public
specifier|final
name|Scorer
name|scorer
decl_stmt|;
DECL|field|cost
specifier|public
specifier|final
name|long
name|cost
decl_stmt|;
DECL|field|matchCost
specifier|public
specifier|final
name|float
name|matchCost
decl_stmt|;
comment|// the match cost for two-phase iterators, 0 otherwise
DECL|field|doc
specifier|public
name|int
name|doc
decl_stmt|;
comment|// the current doc, used for comparison
DECL|field|next
specifier|public
name|DisiWrapper
name|next
decl_stmt|;
comment|// reference to a next element, see #topList
comment|// An approximation of the iterator, or the iterator itself if it does not
comment|// support two-phase iteration
DECL|field|approximation
specifier|public
specifier|final
name|DocIdSetIterator
name|approximation
decl_stmt|;
comment|// A two-phase view of the iterator, or null if the iterator does not support
comment|// two-phase iteration
DECL|field|twoPhaseView
specifier|public
specifier|final
name|TwoPhaseIterator
name|twoPhaseView
decl_stmt|;
comment|// FOR SPANS
DECL|field|spans
specifier|public
specifier|final
name|Spans
name|spans
decl_stmt|;
DECL|field|lastApproxMatchDoc
specifier|public
name|int
name|lastApproxMatchDoc
decl_stmt|;
comment|// last doc of approximation that did match
DECL|field|lastApproxNonMatchDoc
specifier|public
name|int
name|lastApproxNonMatchDoc
decl_stmt|;
comment|// last doc of approximation that did not match
DECL|method|DisiWrapper
specifier|public
name|DisiWrapper
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
name|this
operator|.
name|spans
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|iterator
operator|=
name|scorer
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|cost
operator|=
name|iterator
operator|.
name|cost
argument_list|()
expr_stmt|;
name|this
operator|.
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|twoPhaseView
operator|=
name|scorer
operator|.
name|twoPhaseIterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|twoPhaseView
operator|!=
literal|null
condition|)
block|{
name|approximation
operator|=
name|twoPhaseView
operator|.
name|approximation
argument_list|()
expr_stmt|;
name|matchCost
operator|=
name|twoPhaseView
operator|.
name|matchCost
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|approximation
operator|=
name|iterator
expr_stmt|;
name|matchCost
operator|=
literal|0f
expr_stmt|;
block|}
block|}
DECL|method|DisiWrapper
specifier|public
name|DisiWrapper
parameter_list|(
name|Spans
name|spans
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|spans
operator|=
name|spans
expr_stmt|;
name|this
operator|.
name|iterator
operator|=
name|spans
expr_stmt|;
name|this
operator|.
name|cost
operator|=
name|iterator
operator|.
name|cost
argument_list|()
expr_stmt|;
name|this
operator|.
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|twoPhaseView
operator|=
name|spans
operator|.
name|asTwoPhaseIterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|twoPhaseView
operator|!=
literal|null
condition|)
block|{
name|approximation
operator|=
name|twoPhaseView
operator|.
name|approximation
argument_list|()
expr_stmt|;
name|matchCost
operator|=
name|twoPhaseView
operator|.
name|matchCost
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|approximation
operator|=
name|iterator
expr_stmt|;
name|matchCost
operator|=
literal|0f
expr_stmt|;
block|}
name|this
operator|.
name|lastApproxNonMatchDoc
operator|=
operator|-
literal|2
expr_stmt|;
name|this
operator|.
name|lastApproxMatchDoc
operator|=
operator|-
literal|2
expr_stmt|;
block|}
block|}
end_class
end_unit
