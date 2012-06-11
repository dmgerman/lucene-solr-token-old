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
begin_comment
comment|/**  * Abstract superclass for language modeling Similarities. The following inner  * types are introduced:  *<ul>  *<li>{@link LMStats}, which defines a new statistic, the probability that  *   the collection language model generates the current term;</li>  *<li>{@link CollectionModel}, which is a strategy interface for object that  *   compute the collection language model {@code p(w|C)};</li>  *<li>{@link DefaultCollectionModel}, an implementation of the former, that  *   computes the term probability as the number of occurrences of the term in the  *   collection, divided by the total number of tokens.</li>  *</ul>   *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|LMSimilarity
specifier|public
specifier|abstract
class|class
name|LMSimilarity
extends|extends
name|SimilarityBase
block|{
comment|/** The collection model. */
DECL|field|collectionModel
specifier|protected
specifier|final
name|CollectionModel
name|collectionModel
decl_stmt|;
comment|/** Creates a new instance with the specified collection language model. */
DECL|method|LMSimilarity
specifier|public
name|LMSimilarity
parameter_list|(
name|CollectionModel
name|collectionModel
parameter_list|)
block|{
name|this
operator|.
name|collectionModel
operator|=
name|collectionModel
expr_stmt|;
block|}
comment|/** Creates a new instance with the default collection language model. */
DECL|method|LMSimilarity
specifier|public
name|LMSimilarity
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|DefaultCollectionModel
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newStats
specifier|protected
name|BasicStats
name|newStats
parameter_list|(
name|String
name|field
parameter_list|,
name|float
name|queryBoost
parameter_list|)
block|{
return|return
operator|new
name|LMStats
argument_list|(
name|field
argument_list|,
name|queryBoost
argument_list|)
return|;
block|}
comment|/**    * Computes the collection probability of the current term in addition to the    * usual statistics.    */
annotation|@
name|Override
DECL|method|fillBasicStats
specifier|protected
name|void
name|fillBasicStats
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
name|termStats
parameter_list|)
block|{
name|super
operator|.
name|fillBasicStats
argument_list|(
name|stats
argument_list|,
name|collectionStats
argument_list|,
name|termStats
argument_list|)
expr_stmt|;
name|LMStats
name|lmStats
init|=
operator|(
name|LMStats
operator|)
name|stats
decl_stmt|;
name|lmStats
operator|.
name|setCollectionProbability
argument_list|(
name|collectionModel
operator|.
name|computeProbability
argument_list|(
name|stats
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|protected
name|void
name|explain
parameter_list|(
name|Explanation
name|expl
parameter_list|,
name|BasicStats
name|stats
parameter_list|,
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
name|expl
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|collectionModel
operator|.
name|computeProbability
argument_list|(
name|stats
argument_list|)
argument_list|,
literal|"collection probability"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the name of the LM method. The values of the parameters should be    * included as well.    *<p>Used in {@link #toString()}</p>.    */
DECL|method|getName
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Returns the name of the LM method. If a custom collection model strategy is    * used, its name is included as well.    * @see #getName()    * @see CollectionModel#getName()    * @see DefaultCollectionModel     */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|coll
init|=
name|collectionModel
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|coll
operator|!=
literal|null
condition|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"LM %s - %s"
argument_list|,
name|getName
argument_list|()
argument_list|,
name|coll
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"LM %s"
argument_list|,
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/** Stores the collection distribution of the current term. */
DECL|class|LMStats
specifier|public
specifier|static
class|class
name|LMStats
extends|extends
name|BasicStats
block|{
comment|/** The probability that the current term is generated by the collection. */
DECL|field|collectionProbability
specifier|private
name|float
name|collectionProbability
decl_stmt|;
DECL|method|LMStats
specifier|public
name|LMStats
parameter_list|(
name|String
name|field
parameter_list|,
name|float
name|queryBoost
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|queryBoost
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the probability that the current term is generated by the      * collection.      */
DECL|method|getCollectionProbability
specifier|public
specifier|final
name|float
name|getCollectionProbability
parameter_list|()
block|{
return|return
name|collectionProbability
return|;
block|}
comment|/**      * Sets the probability that the current term is generated by the      * collection.      */
DECL|method|setCollectionProbability
specifier|public
specifier|final
name|void
name|setCollectionProbability
parameter_list|(
name|float
name|collectionProbability
parameter_list|)
block|{
name|this
operator|.
name|collectionProbability
operator|=
name|collectionProbability
expr_stmt|;
block|}
block|}
comment|/** A strategy for computing the collection language model. */
DECL|interface|CollectionModel
specifier|public
specifier|static
interface|interface
name|CollectionModel
block|{
comment|/**      * Computes the probability {@code p(w|C)} according to the language model      * strategy for the current term.      */
DECL|method|computeProbability
specifier|public
name|float
name|computeProbability
parameter_list|(
name|BasicStats
name|stats
parameter_list|)
function_decl|;
comment|/** The name of the collection model strategy. */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
block|}
comment|/**    * Models {@code p(w|C)} as the number of occurrences of the term in the    * collection, divided by the total number of tokens {@code + 1}.    */
DECL|class|DefaultCollectionModel
specifier|public
specifier|static
class|class
name|DefaultCollectionModel
implements|implements
name|CollectionModel
block|{
annotation|@
name|Override
DECL|method|computeProbability
specifier|public
name|float
name|computeProbability
parameter_list|(
name|BasicStats
name|stats
parameter_list|)
block|{
return|return
operator|(
name|stats
operator|.
name|getTotalTermFreq
argument_list|()
operator|+
literal|1F
operator|)
operator|/
operator|(
name|stats
operator|.
name|getNumberOfFieldTokens
argument_list|()
operator|+
literal|1F
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
