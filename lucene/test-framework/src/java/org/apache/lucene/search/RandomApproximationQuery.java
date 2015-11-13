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
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
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
name|IndexReader
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
name|LeafReaderContext
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
name|Term
import|;
end_import
begin_comment
comment|/**  * A {@link Query} that adds random approximations to its scorers.  */
end_comment
begin_class
DECL|class|RandomApproximationQuery
specifier|public
class|class
name|RandomApproximationQuery
extends|extends
name|Query
block|{
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|RandomApproximationQuery
specifier|public
name|RandomApproximationQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|rewritten
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|!=
name|query
condition|)
block|{
return|return
operator|new
name|RandomApproximationQuery
argument_list|(
name|rewritten
argument_list|,
name|random
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|RandomApproximationQuery
name|that
init|=
operator|(
name|RandomApproximationQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
name|that
operator|.
name|query
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
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
return|return
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|query
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|weight
init|=
name|query
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|)
decl_stmt|;
return|return
operator|new
name|RandomApproximationWeight
argument_list|(
name|weight
argument_list|,
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|class|RandomApproximationWeight
specifier|private
specifier|static
class|class
name|RandomApproximationWeight
extends|extends
name|Weight
block|{
DECL|field|weight
specifier|private
specifier|final
name|Weight
name|weight
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|RandomApproximationWeight
name|RandomApproximationWeight
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
name|weight
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|weight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|getValueForNormalization
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|weight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|RandomApproximationScorer
argument_list|(
name|scorer
argument_list|,
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|RandomApproximationScorer
specifier|private
specifier|static
class|class
name|RandomApproximationScorer
extends|extends
name|Scorer
block|{
DECL|field|scorer
specifier|private
specifier|final
name|Scorer
name|scorer
decl_stmt|;
DECL|field|twoPhaseView
specifier|private
specifier|final
name|RandomTwoPhaseView
name|twoPhaseView
decl_stmt|;
DECL|method|RandomApproximationScorer
name|RandomApproximationScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
name|scorer
operator|.
name|getWeight
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|this
operator|.
name|twoPhaseView
operator|=
operator|new
name|RandomTwoPhaseView
argument_list|(
name|random
argument_list|,
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
return|return
name|twoPhaseView
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
name|scorer
operator|.
name|score
argument_list|()
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
name|scorer
operator|.
name|freq
argument_list|()
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
name|scorer
operator|.
name|docID
argument_list|()
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
return|return
name|scorer
operator|.
name|nextDoc
argument_list|()
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
return|return
name|scorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
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
name|scorer
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
DECL|class|RandomTwoPhaseView
specifier|private
specifier|static
class|class
name|RandomTwoPhaseView
extends|extends
name|TwoPhaseIterator
block|{
DECL|field|disi
specifier|private
specifier|final
name|DocIdSetIterator
name|disi
decl_stmt|;
DECL|field|lastDoc
specifier|private
name|int
name|lastDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|randomMatchCost
specifier|private
specifier|final
name|float
name|randomMatchCost
decl_stmt|;
DECL|method|RandomTwoPhaseView
name|RandomTwoPhaseView
parameter_list|(
name|Random
name|random
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|RandomApproximation
argument_list|(
name|random
argument_list|,
name|disi
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|disi
operator|=
name|disi
expr_stmt|;
name|this
operator|.
name|randomMatchCost
operator|=
name|random
operator|.
name|nextFloat
argument_list|()
operator|*
literal|200
expr_stmt|;
comment|// between 0 and 200
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
if|if
condition|(
name|approximation
operator|.
name|docID
argument_list|()
operator|==
operator|-
literal|1
operator|||
name|approximation
operator|.
name|docID
argument_list|()
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"matches() should not be called on doc ID "
operator|+
name|approximation
operator|.
name|docID
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|lastDoc
operator|==
name|approximation
operator|.
name|docID
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"matches() has been called twice on doc ID "
operator|+
name|approximation
operator|.
name|docID
argument_list|()
argument_list|)
throw|;
block|}
name|lastDoc
operator|=
name|approximation
operator|.
name|docID
argument_list|()
expr_stmt|;
return|return
name|approximation
operator|.
name|docID
argument_list|()
operator|==
name|disi
operator|.
name|docID
argument_list|()
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
name|randomMatchCost
return|;
block|}
block|}
DECL|class|RandomApproximation
specifier|private
specifier|static
class|class
name|RandomApproximation
extends|extends
name|DocIdSetIterator
block|{
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|disi
specifier|private
specifier|final
name|DocIdSetIterator
name|disi
decl_stmt|;
DECL|field|doc
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|RandomApproximation
specifier|public
name|RandomApproximation
parameter_list|(
name|Random
name|random
parameter_list|,
name|DocIdSetIterator
name|disi
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|disi
operator|=
name|disi
expr_stmt|;
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
return|return
name|advance
argument_list|(
name|doc
operator|+
literal|1
argument_list|)
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
if|if
condition|(
name|disi
operator|.
name|docID
argument_list|()
operator|<
name|target
condition|)
block|{
name|disi
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|disi
operator|.
name|docID
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
return|return
name|doc
operator|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
name|target
argument_list|,
name|disi
operator|.
name|docID
argument_list|()
argument_list|)
return|;
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
name|disi
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
