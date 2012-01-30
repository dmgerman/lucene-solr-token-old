begin_unit
begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|AtomicReaderContext
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
name|FieldInvertState
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
name|Norm
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
name|CollectionStatistics
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
name|Explanation
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
name|TermStatistics
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
comment|/**  * Implements the CombSUM method for combining evidence from multiple  * similarity values described in: Joseph A. Shaw, Edward A. Fox.   * In Text REtrieval Conference (1993), pp. 243-252  * @lucene.experimental  */
end_comment
begin_class
DECL|class|MultiSimilarity
specifier|public
class|class
name|MultiSimilarity
extends|extends
name|Similarity
block|{
DECL|field|sims
specifier|protected
specifier|final
name|Similarity
name|sims
index|[]
decl_stmt|;
DECL|method|MultiSimilarity
specifier|public
name|MultiSimilarity
parameter_list|(
name|Similarity
name|sims
index|[]
parameter_list|)
block|{
name|this
operator|.
name|sims
operator|=
name|sims
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|void
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|,
name|Norm
name|norm
parameter_list|)
block|{
name|sims
index|[
literal|0
index|]
operator|.
name|computeNorm
argument_list|(
name|state
argument_list|,
name|norm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeStats
specifier|public
name|Stats
name|computeStats
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|float
name|queryBoost
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
block|{
name|Stats
name|subStats
index|[]
init|=
operator|new
name|Stats
index|[
name|sims
operator|.
name|length
index|]
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
name|subStats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subStats
index|[
name|i
index|]
operator|=
name|sims
index|[
name|i
index|]
operator|.
name|computeStats
argument_list|(
name|collectionStats
argument_list|,
name|queryBoost
argument_list|,
name|termStats
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MultiStats
argument_list|(
name|subStats
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|exactDocScorer
specifier|public
name|ExactDocScorer
name|exactDocScorer
parameter_list|(
name|Stats
name|stats
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ExactDocScorer
name|subScorers
index|[]
init|=
operator|new
name|ExactDocScorer
index|[
name|sims
operator|.
name|length
index|]
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
name|subScorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subScorers
index|[
name|i
index|]
operator|=
name|sims
index|[
name|i
index|]
operator|.
name|exactDocScorer
argument_list|(
operator|(
operator|(
name|MultiStats
operator|)
name|stats
operator|)
operator|.
name|subStats
index|[
name|i
index|]
argument_list|,
name|fieldName
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MultiExactDocScorer
argument_list|(
name|subScorers
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sloppyDocScorer
specifier|public
name|SloppyDocScorer
name|sloppyDocScorer
parameter_list|(
name|Stats
name|stats
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|SloppyDocScorer
name|subScorers
index|[]
init|=
operator|new
name|SloppyDocScorer
index|[
name|sims
operator|.
name|length
index|]
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
name|subScorers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subScorers
index|[
name|i
index|]
operator|=
name|sims
index|[
name|i
index|]
operator|.
name|sloppyDocScorer
argument_list|(
operator|(
operator|(
name|MultiStats
operator|)
name|stats
operator|)
operator|.
name|subStats
index|[
name|i
index|]
argument_list|,
name|fieldName
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MultiSloppyDocScorer
argument_list|(
name|subScorers
argument_list|)
return|;
block|}
DECL|class|MultiExactDocScorer
specifier|public
specifier|static
class|class
name|MultiExactDocScorer
extends|extends
name|ExactDocScorer
block|{
DECL|field|subScorers
specifier|private
specifier|final
name|ExactDocScorer
name|subScorers
index|[]
decl_stmt|;
DECL|method|MultiExactDocScorer
name|MultiExactDocScorer
parameter_list|(
name|ExactDocScorer
name|subScorers
index|[]
parameter_list|)
block|{
name|this
operator|.
name|subScorers
operator|=
name|subScorers
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|freq
parameter_list|)
block|{
name|float
name|sum
init|=
literal|0.0f
decl_stmt|;
for|for
control|(
name|ExactDocScorer
name|subScorer
range|:
name|subScorers
control|)
block|{
name|sum
operator|+=
name|subScorer
operator|.
name|score
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|,
name|Explanation
name|freq
parameter_list|)
block|{
name|Explanation
name|expl
init|=
operator|new
name|Explanation
argument_list|(
name|score
argument_list|(
name|doc
argument_list|,
operator|(
name|int
operator|)
name|freq
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
literal|"sum of:"
argument_list|)
decl_stmt|;
for|for
control|(
name|ExactDocScorer
name|subScorer
range|:
name|subScorers
control|)
block|{
name|expl
operator|.
name|addDetail
argument_list|(
name|subScorer
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|expl
return|;
block|}
block|}
DECL|class|MultiSloppyDocScorer
specifier|public
specifier|static
class|class
name|MultiSloppyDocScorer
extends|extends
name|SloppyDocScorer
block|{
DECL|field|subScorers
specifier|private
specifier|final
name|SloppyDocScorer
name|subScorers
index|[]
decl_stmt|;
DECL|method|MultiSloppyDocScorer
name|MultiSloppyDocScorer
parameter_list|(
name|SloppyDocScorer
name|subScorers
index|[]
parameter_list|)
block|{
name|this
operator|.
name|subScorers
operator|=
name|subScorers
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|)
block|{
name|float
name|sum
init|=
literal|0.0f
decl_stmt|;
for|for
control|(
name|SloppyDocScorer
name|subScorer
range|:
name|subScorers
control|)
block|{
name|sum
operator|+=
name|subScorer
operator|.
name|score
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|,
name|Explanation
name|freq
parameter_list|)
block|{
name|Explanation
name|expl
init|=
operator|new
name|Explanation
argument_list|(
name|score
argument_list|(
name|doc
argument_list|,
name|freq
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
literal|"sum of:"
argument_list|)
decl_stmt|;
for|for
control|(
name|SloppyDocScorer
name|subScorer
range|:
name|subScorers
control|)
block|{
name|expl
operator|.
name|addDetail
argument_list|(
name|subScorer
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|expl
return|;
block|}
annotation|@
name|Override
DECL|method|computeSlopFactor
specifier|public
name|float
name|computeSlopFactor
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
name|subScorers
index|[
literal|0
index|]
operator|.
name|computeSlopFactor
argument_list|(
name|distance
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computePayloadFactor
specifier|public
name|float
name|computePayloadFactor
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
return|return
name|subScorers
index|[
literal|0
index|]
operator|.
name|computePayloadFactor
argument_list|(
name|doc
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|payload
argument_list|)
return|;
block|}
block|}
DECL|class|MultiStats
specifier|public
specifier|static
class|class
name|MultiStats
extends|extends
name|Stats
block|{
DECL|field|subStats
specifier|final
name|Stats
name|subStats
index|[]
decl_stmt|;
DECL|method|MultiStats
name|MultiStats
parameter_list|(
name|Stats
name|subStats
index|[]
parameter_list|)
block|{
name|this
operator|.
name|subStats
operator|=
name|subStats
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
block|{
name|float
name|sum
init|=
literal|0.0f
decl_stmt|;
for|for
control|(
name|Stats
name|stat
range|:
name|subStats
control|)
block|{
name|sum
operator|+=
name|stat
operator|.
name|getValueForNormalization
argument_list|()
expr_stmt|;
block|}
return|return
name|sum
operator|/
name|subStats
operator|.
name|length
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
name|queryNorm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
for|for
control|(
name|Stats
name|stat
range|:
name|subStats
control|)
block|{
name|stat
operator|.
name|normalize
argument_list|(
name|queryNorm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
