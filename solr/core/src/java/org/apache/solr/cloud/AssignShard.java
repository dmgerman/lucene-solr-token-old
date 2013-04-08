begin_unit
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment
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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|cloud
operator|.
name|ClusterState
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
name|cloud
operator|.
name|Slice
import|;
end_import
begin_class
DECL|class|AssignShard
specifier|public
class|class
name|AssignShard
block|{
comment|/**    * Assign a new unique id up to slices count - then add replicas evenly.    *     * @return the assigned shard id    */
DECL|method|assignShard
specifier|public
specifier|static
name|String
name|assignShard
parameter_list|(
name|String
name|collection
parameter_list|,
name|ClusterState
name|state
parameter_list|,
name|Integer
name|numShards
parameter_list|)
block|{
if|if
condition|(
name|numShards
operator|==
literal|null
condition|)
block|{
name|numShards
operator|=
literal|1
expr_stmt|;
block|}
name|String
name|returnShardId
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|sliceMap
init|=
name|state
operator|.
name|getActiveSlicesMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
comment|// TODO: now that we create shards ahead of time, is this code needed?  Esp since hash ranges aren't assigned when creating via this method?
if|if
condition|(
name|sliceMap
operator|==
literal|null
condition|)
block|{
return|return
literal|"shard1"
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|shardIdNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|sliceMap
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardIdNames
operator|.
name|size
argument_list|()
operator|<
name|numShards
condition|)
block|{
return|return
literal|"shard"
operator|+
operator|(
name|shardIdNames
operator|.
name|size
argument_list|()
operator|+
literal|1
operator|)
return|;
block|}
comment|// TODO: don't need to sort to find shard with fewest replicas!
comment|// else figure out which shard needs more replicas
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|shardId
range|:
name|shardIdNames
control|)
block|{
name|int
name|cnt
init|=
name|sliceMap
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|shardIdNames
argument_list|,
operator|new
name|Comparator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|o1
parameter_list|,
name|String
name|o2
parameter_list|)
block|{
name|Integer
name|one
init|=
name|map
operator|.
name|get
argument_list|(
name|o1
argument_list|)
decl_stmt|;
name|Integer
name|two
init|=
name|map
operator|.
name|get
argument_list|(
name|o2
argument_list|)
decl_stmt|;
return|return
name|one
operator|.
name|compareTo
argument_list|(
name|two
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|returnShardId
operator|=
name|shardIdNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|returnShardId
return|;
block|}
block|}
end_class
end_unit
