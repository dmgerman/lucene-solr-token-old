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
name|TermStatistics
import|;
end_import
begin_comment
comment|/**  * Provides the ability to use a different {@link Similarity} for different fields.  *<p>  * Subclasses should implement {@link #get(String)} to return an appropriate  * Similarity (for example, using field-specific parameter values) for the field.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|PerFieldSimilarityWrapper
specifier|public
specifier|abstract
class|class
name|PerFieldSimilarityWrapper
extends|extends
name|Similarity
block|{
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
specifier|final
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
name|get
argument_list|(
name|state
operator|.
name|getName
argument_list|()
argument_list|)
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
DECL|method|computeWeight
specifier|public
specifier|final
name|SimWeight
name|computeWeight
parameter_list|(
name|float
name|queryBoost
parameter_list|,
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
block|{
name|PerFieldSimWeight
name|weight
init|=
operator|new
name|PerFieldSimWeight
argument_list|()
decl_stmt|;
name|weight
operator|.
name|delegate
operator|=
name|get
argument_list|(
name|collectionStats
operator|.
name|field
argument_list|()
argument_list|)
expr_stmt|;
name|weight
operator|.
name|delegateWeight
operator|=
name|weight
operator|.
name|delegate
operator|.
name|computeWeight
argument_list|(
name|queryBoost
argument_list|,
name|collectionStats
argument_list|,
name|termStats
argument_list|)
expr_stmt|;
return|return
name|weight
return|;
block|}
annotation|@
name|Override
DECL|method|exactSimScorer
specifier|public
specifier|final
name|ExactSimScorer
name|exactSimScorer
parameter_list|(
name|SimWeight
name|weight
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|PerFieldSimWeight
name|perFieldWeight
init|=
operator|(
name|PerFieldSimWeight
operator|)
name|weight
decl_stmt|;
return|return
name|perFieldWeight
operator|.
name|delegate
operator|.
name|exactSimScorer
argument_list|(
name|perFieldWeight
operator|.
name|delegateWeight
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sloppySimScorer
specifier|public
specifier|final
name|SloppySimScorer
name|sloppySimScorer
parameter_list|(
name|SimWeight
name|weight
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|PerFieldSimWeight
name|perFieldWeight
init|=
operator|(
name|PerFieldSimWeight
operator|)
name|weight
decl_stmt|;
return|return
name|perFieldWeight
operator|.
name|delegate
operator|.
name|sloppySimScorer
argument_list|(
name|perFieldWeight
operator|.
name|delegateWeight
argument_list|,
name|context
argument_list|)
return|;
block|}
comment|/**     * Returns a {@link Similarity} for scoring a field.    */
DECL|method|get
specifier|public
specifier|abstract
name|Similarity
name|get
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|class|PerFieldSimWeight
specifier|static
class|class
name|PerFieldSimWeight
extends|extends
name|SimWeight
block|{
DECL|field|delegate
name|Similarity
name|delegate
decl_stmt|;
DECL|field|delegateWeight
name|SimWeight
name|delegateWeight
decl_stmt|;
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
block|{
return|return
name|delegateWeight
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
name|queryNorm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|delegateWeight
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
end_class
end_unit
