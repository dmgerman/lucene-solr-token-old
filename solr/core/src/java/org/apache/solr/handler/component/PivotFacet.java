begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package
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
name|BitSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|FacetParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
operator|.
name|FacetComponent
operator|.
name|FacetBase
import|;
end_import
begin_comment
comment|/**  * Models a single instance of a "pivot" specified by a {@link FacetParams#FACET_PIVOT}   * param, which may contain multiple nested fields.  *  * This class is also used to coordinate the refinement requests needed from various   * shards when doing processing a distributed request  */
end_comment
begin_class
DECL|class|PivotFacet
specifier|public
class|class
name|PivotFacet
extends|extends
name|FacetBase
block|{
comment|/**     * Local param used to indicate that refinements are required on a pivot. Should    * also be used as the prefix for concatenating with the value to determine the    * name of the multi-valued param that will contain all of the values needed for     * refinement.    */
DECL|field|REFINE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|REFINE_PARAM
init|=
literal|"fpt"
decl_stmt|;
comment|// TODO: is this really needed? can't we just loop over 0<=i<rb.shards.length ?
DECL|field|knownShards
specifier|public
specifier|final
name|BitSet
name|knownShards
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
DECL|field|queuedRefinements
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|PivotFacetValue
argument_list|>
argument_list|>
name|queuedRefinements
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// if null, then either we haven't collected any responses from shards
comment|// or all the shards that have responded so far haven't had any values for the top
comment|// field of this pivot.  May be null forever if no doc in any shard has a value
comment|// for the top field of the pivot
DECL|field|pivotFacetField
specifier|private
name|PivotFacetField
name|pivotFacetField
decl_stmt|;
DECL|method|PivotFacet
specifier|public
name|PivotFacet
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|String
name|facetStr
parameter_list|)
block|{
name|super
argument_list|(
name|rb
argument_list|,
name|FacetParams
operator|.
name|FACET_PIVOT
argument_list|,
name|facetStr
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tracks that the specified shard needs to be asked to refine the specified     * {@link PivotFacetValue}     *     * @see #getQueuedRefinements    */
DECL|method|addRefinement
specifier|public
name|void
name|addRefinement
parameter_list|(
name|int
name|shardNumber
parameter_list|,
name|PivotFacetValue
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
name|queuedRefinements
operator|.
name|containsKey
argument_list|(
name|shardNumber
argument_list|)
condition|)
block|{
name|queuedRefinements
operator|.
name|put
argument_list|(
name|shardNumber
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|PivotFacetValue
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|queuedRefinements
operator|.
name|get
argument_list|(
name|shardNumber
argument_list|)
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * An immutable List of the {@link PivotFacetValue}s that need to be    * refined for this pivot.  Once these refinements have been processed,     * the caller should clear them using {@link #removeAllRefinementsForShard}    *    * @see #addRefinement    * @see #removeAllRefinementsForShard    * @return a list of the values to refine, or an empty list.    */
DECL|method|getQueuedRefinements
specifier|public
name|List
argument_list|<
name|PivotFacetValue
argument_list|>
name|getQueuedRefinements
parameter_list|(
name|int
name|shardNumber
parameter_list|)
block|{
name|List
argument_list|<
name|PivotFacetValue
argument_list|>
name|raw
init|=
name|queuedRefinements
operator|.
name|get
argument_list|(
name|shardNumber
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|raw
condition|)
block|{
name|raw
operator|=
name|Collections
operator|.
expr|<
name|PivotFacetValue
operator|>
name|emptyList
argument_list|()
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|raw
argument_list|)
return|;
block|}
comment|/**    * Clears the list of queued refinements for the specified shard    *    * @see #addRefinement    * @see #getQueuedRefinements    */
DECL|method|removeAllRefinementsForShard
specifier|public
name|void
name|removeAllRefinementsForShard
parameter_list|(
name|int
name|shardNumber
parameter_list|)
block|{
name|queuedRefinements
operator|.
name|remove
argument_list|(
name|shardNumber
argument_list|)
expr_stmt|;
block|}
comment|/**    * If true, then additional refinement requests are needed to flesh out the correct    * counts for this Pivot    *    * @see #getQueuedRefinements    */
DECL|method|isRefinementsRequired
specifier|public
name|boolean
name|isRefinementsRequired
parameter_list|()
block|{
return|return
operator|!
name|queuedRefinements
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**     * A recursive method for generating<code>NamedLists</code> for this pivot    * suitable for including in a pivot facet response to the original distributed request.    *    * @see PivotFacetField#trim    * @see PivotFacetField#convertToListOfNamedLists    */
DECL|method|getTrimmedPivotsAsListOfNamedLists
specifier|public
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|getTrimmedPivotsAsListOfNamedLists
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|==
name|pivotFacetField
condition|)
block|{
comment|// no values in any shard for the top field of this pivot
return|return
name|Collections
operator|.
expr|<
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|>
name|emptyList
argument_list|()
return|;
block|}
name|pivotFacetField
operator|.
name|trim
argument_list|()
expr_stmt|;
return|return
name|pivotFacetField
operator|.
name|convertToListOfNamedLists
argument_list|()
return|;
block|}
comment|/**     * A recursive method for determining which {@link PivotFacetValue}s need to be    * refined for this pivot.    *    * @see PivotFacetField#queuePivotRefinementRequests    */
DECL|method|queuePivotRefinementRequests
specifier|public
name|void
name|queuePivotRefinementRequests
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|==
name|pivotFacetField
condition|)
return|return;
comment|// NOOP
name|pivotFacetField
operator|.
name|sort
argument_list|()
expr_stmt|;
name|pivotFacetField
operator|.
name|queuePivotRefinementRequests
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Recursively merges the response from the specified shard, tracking the known shards.    *     * @see PivotFacetField#contributeFromShard    * @see PivotFacetField#createFromListOfNamedLists    */
DECL|method|mergeResponseFromShard
specifier|public
name|void
name|mergeResponseFromShard
parameter_list|(
name|int
name|shardNumber
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|,
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|response
parameter_list|)
block|{
name|knownShards
operator|.
name|set
argument_list|(
name|shardNumber
argument_list|)
expr_stmt|;
if|if
condition|(
name|pivotFacetField
operator|==
literal|null
condition|)
block|{
name|pivotFacetField
operator|=
name|PivotFacetField
operator|.
name|createFromListOfNamedLists
argument_list|(
name|shardNumber
argument_list|,
name|rb
argument_list|,
literal|null
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pivotFacetField
operator|.
name|contributeFromShard
argument_list|(
name|shardNumber
argument_list|,
name|rb
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"["
operator|+
name|facetStr
operator|+
literal|"] | "
operator|+
name|this
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
end_class
end_unit
