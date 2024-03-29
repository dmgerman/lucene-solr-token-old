begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * A constant-scoring {@link Scorer}.  * @lucene.internal  */
end_comment
begin_class
DECL|class|ConstantScoreScorer
specifier|public
specifier|final
class|class
name|ConstantScoreScorer
extends|extends
name|Scorer
block|{
DECL|field|score
specifier|private
specifier|final
name|float
name|score
decl_stmt|;
DECL|field|twoPhaseIterator
specifier|private
specifier|final
name|TwoPhaseIterator
name|twoPhaseIterator
decl_stmt|;
DECL|field|disi
specifier|private
specifier|final
name|DocIdSetIterator
name|disi
decl_stmt|;
comment|/** Constructor based on a {@link DocIdSetIterator} which will be used to    *  drive iteration. Two phase iteration will not be supported.    *  @param weight the parent weight    *  @param score the score to return on each document    *  @param disi the iterator that defines matching documents */
DECL|method|ConstantScoreScorer
specifier|public
name|ConstantScoreScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|float
name|score
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|twoPhaseIterator
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|disi
operator|=
name|disi
expr_stmt|;
block|}
comment|/** Constructor based on a {@link TwoPhaseIterator}. In that case the    *  {@link Scorer} will support two-phase iteration.    *  @param weight the parent weight    *  @param score the score to return on each document    *  @param twoPhaseIterator the iterator that defines matching documents */
DECL|method|ConstantScoreScorer
specifier|public
name|ConstantScoreScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|float
name|score
parameter_list|,
name|TwoPhaseIterator
name|twoPhaseIterator
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|twoPhaseIterator
operator|=
name|twoPhaseIterator
expr_stmt|;
name|this
operator|.
name|disi
operator|=
name|TwoPhaseIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhaseIterator
argument_list|)
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
name|disi
return|;
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
name|twoPhaseIterator
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
name|disi
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
block|}
end_class
end_unit
