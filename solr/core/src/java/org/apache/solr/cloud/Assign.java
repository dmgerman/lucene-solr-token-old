begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|LinkedHashMap
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
name|Locale
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|cloud
operator|.
name|rule
operator|.
name|ReplicaAssigner
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
name|cloud
operator|.
name|rule
operator|.
name|Rule
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
name|SolrException
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
name|DocCollection
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
name|Replica
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
name|StrUtils
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
name|core
operator|.
name|CoreContainer
import|;
end_import
begin_import
import|import static
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
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
import|;
end_import
begin_import
import|import static
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
name|ZkStateReader
operator|.
name|MAX_SHARDS_PER_NODE
import|;
end_import
begin_class
DECL|class|Assign
specifier|public
class|class
name|Assign
block|{
DECL|field|COUNT
specifier|private
specifier|static
name|Pattern
name|COUNT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"core_node(\\d+)"
argument_list|)
decl_stmt|;
DECL|method|assignNode
specifier|public
specifier|static
name|String
name|assignNode
parameter_list|(
name|DocCollection
name|collection
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|sliceMap
init|=
name|collection
operator|!=
literal|null
condition|?
name|collection
operator|.
name|getSlicesMap
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|sliceMap
operator|==
literal|null
condition|)
block|{
return|return
literal|"core_node1"
return|;
block|}
name|int
name|max
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|sliceMap
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|Matcher
name|m
init|=
name|COUNT
operator|.
name|matcher
argument_list|(
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|"core_node"
operator|+
operator|(
name|max
operator|+
literal|1
operator|)
return|;
block|}
comment|/**    * Assign a new unique id up to slices count - then add replicas evenly.    *    * @return the assigned shard id    */
DECL|method|assignShard
specifier|public
specifier|static
name|String
name|assignShard
parameter_list|(
name|DocCollection
name|collection
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
name|collection
operator|!=
literal|null
condition|?
name|collection
operator|.
name|getActiveSlicesMap
argument_list|()
else|:
literal|null
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
argument_list|<>
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
argument_list|<>
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
parameter_list|(
name|o1
parameter_list|,
name|o2
parameter_list|)
lambda|->
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
DECL|method|buildCoreName
specifier|static
name|String
name|buildCoreName
parameter_list|(
name|DocCollection
name|collection
parameter_list|,
name|String
name|shard
parameter_list|)
block|{
name|Slice
name|slice
init|=
name|collection
operator|.
name|getSlice
argument_list|(
name|shard
argument_list|)
decl_stmt|;
name|int
name|replicaNum
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|String
name|replicaName
init|=
name|collection
operator|.
name|getName
argument_list|()
operator|+
literal|"_"
operator|+
name|shard
operator|+
literal|"_replica"
operator|+
name|replicaNum
decl_stmt|;
name|boolean
name|exists
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
if|if
condition|(
name|replicaName
operator|.
name|equals
argument_list|(
name|replica
operator|.
name|getStr
argument_list|(
name|CORE_NAME_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|exists
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|exists
condition|)
name|replicaNum
operator|++
expr_stmt|;
else|else
break|break;
block|}
return|return
name|collection
operator|.
name|getName
argument_list|()
operator|+
literal|"_"
operator|+
name|shard
operator|+
literal|"_replica"
operator|+
name|replicaNum
return|;
block|}
DECL|class|ReplicaCount
specifier|static
class|class
name|ReplicaCount
block|{
DECL|field|nodeName
specifier|public
specifier|final
name|String
name|nodeName
decl_stmt|;
DECL|field|thisCollectionNodes
specifier|public
name|int
name|thisCollectionNodes
init|=
literal|0
decl_stmt|;
DECL|field|totalNodes
specifier|public
name|int
name|totalNodes
init|=
literal|0
decl_stmt|;
DECL|method|ReplicaCount
name|ReplicaCount
parameter_list|(
name|String
name|nodeName
parameter_list|)
block|{
name|this
operator|.
name|nodeName
operator|=
name|nodeName
expr_stmt|;
block|}
DECL|method|weight
specifier|public
name|int
name|weight
parameter_list|()
block|{
return|return
operator|(
name|thisCollectionNodes
operator|*
literal|100
operator|)
operator|+
name|totalNodes
return|;
block|}
block|}
comment|// Only called from createShard and addReplica (so far).
comment|//
comment|// Gets a list of candidate nodes to put the required replica(s) on. Throws errors if not enough replicas
comment|// could be created on live nodes given maxShardsPerNode, Replication factor (if from createShard) etc.
DECL|method|getNodesForNewReplicas
specifier|public
specifier|static
name|List
argument_list|<
name|ReplicaCount
argument_list|>
name|getNodesForNewReplicas
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|String
name|shard
parameter_list|,
name|int
name|numberOfNodes
parameter_list|,
name|String
name|createNodeSetStr
parameter_list|,
name|CoreContainer
name|cc
parameter_list|)
block|{
name|DocCollection
name|coll
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|Integer
name|maxShardsPerNode
init|=
name|coll
operator|.
name|getInt
argument_list|(
name|MAX_SHARDS_PER_NODE
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|createNodeList
init|=
name|createNodeSetStr
operator|==
literal|null
condition|?
literal|null
else|:
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|createNodeSetStr
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|ReplicaCount
argument_list|>
name|nodeNameVsShardCount
init|=
name|getNodeNameVsShardCount
argument_list|(
name|collectionName
argument_list|,
name|clusterState
argument_list|,
name|createNodeList
argument_list|)
decl_stmt|;
if|if
condition|(
name|createNodeList
operator|==
literal|null
condition|)
block|{
comment|// We only care if we haven't been told to put new replicas on specific nodes.
name|int
name|availableSlots
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ReplicaCount
argument_list|>
name|ent
range|:
name|nodeNameVsShardCount
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|//ADDREPLICA can put more than maxShardsPerNode on an instnace, so this test is necessary.
if|if
condition|(
name|maxShardsPerNode
operator|>
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|thisCollectionNodes
condition|)
block|{
name|availableSlots
operator|+=
operator|(
name|maxShardsPerNode
operator|-
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|thisCollectionNodes
operator|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|availableSlots
operator|<
name|numberOfNodes
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Cannot create %d new replicas for collection %s given the current number of live nodes and a maxShardsPerNode of %d"
argument_list|,
name|numberOfNodes
argument_list|,
name|collectionName
argument_list|,
name|maxShardsPerNode
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|List
name|l
init|=
operator|(
name|List
operator|)
name|coll
operator|.
name|get
argument_list|(
name|DocCollection
operator|.
name|RULE
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
condition|)
block|{
return|return
name|getNodesViaRules
argument_list|(
name|clusterState
argument_list|,
name|shard
argument_list|,
name|numberOfNodes
argument_list|,
name|cc
argument_list|,
name|coll
argument_list|,
name|createNodeList
argument_list|,
name|l
argument_list|)
return|;
block|}
name|ArrayList
argument_list|<
name|ReplicaCount
argument_list|>
name|sortedNodeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nodeNameVsShardCount
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|sortedNodeList
argument_list|,
operator|(
name|x
operator|,
name|y
operator|)
operator|->
operator|(
name|x
operator|.
name|weight
argument_list|()
operator|<
name|y
operator|.
name|weight
argument_list|()
operator|)
condition|?
operator|-
literal|1
else|:
operator|(
operator|(
name|x
operator|.
name|weight
argument_list|()
operator|==
name|y
operator|.
name|weight
argument_list|()
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
argument_list|)
expr_stmt|;
return|return
name|sortedNodeList
return|;
block|}
DECL|method|getNodesViaRules
specifier|private
specifier|static
name|List
argument_list|<
name|ReplicaCount
argument_list|>
name|getNodesViaRules
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|shard
parameter_list|,
name|int
name|numberOfNodes
parameter_list|,
name|CoreContainer
name|cc
parameter_list|,
name|DocCollection
name|coll
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|createNodeList
parameter_list|,
name|List
name|l
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Rule
argument_list|>
name|rules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|l
control|)
name|rules
operator|.
name|add
argument_list|(
operator|new
name|Rule
argument_list|(
operator|(
name|Map
operator|)
name|o
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|shardVsNodes
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|coll
operator|.
name|getSlices
argument_list|()
control|)
block|{
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|n
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|shardVsNodes
operator|.
name|put
argument_list|(
name|slice
operator|.
name|getName
argument_list|()
argument_list|,
name|n
argument_list|)
expr_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|Integer
name|count
init|=
name|n
operator|.
name|get
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
name|count
operator|=
literal|0
expr_stmt|;
name|n
operator|.
name|put
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
operator|++
name|count
argument_list|)
expr_stmt|;
block|}
block|}
name|List
name|snitches
init|=
operator|(
name|List
operator|)
name|coll
operator|.
name|get
argument_list|(
name|DocCollection
operator|.
name|SNITCH
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nodesList
init|=
name|createNodeList
operator|==
literal|null
condition|?
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|clusterState
operator|.
name|getLiveNodes
argument_list|()
argument_list|)
else|:
name|createNodeList
decl_stmt|;
name|Map
argument_list|<
name|ReplicaAssigner
operator|.
name|Position
argument_list|,
name|String
argument_list|>
name|positions
init|=
operator|new
name|ReplicaAssigner
argument_list|(
name|rules
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|shard
argument_list|,
name|numberOfNodes
argument_list|)
argument_list|,
name|snitches
argument_list|,
name|shardVsNodes
argument_list|,
name|nodesList
argument_list|,
name|cc
argument_list|,
name|clusterState
argument_list|)
operator|.
name|getNodeMappings
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ReplicaCount
argument_list|>
name|repCounts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|positions
operator|.
name|values
argument_list|()
control|)
block|{
name|repCounts
operator|.
name|add
argument_list|(
operator|new
name|ReplicaCount
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|repCounts
return|;
block|}
DECL|method|getNodeNameVsShardCount
specifier|private
specifier|static
name|HashMap
argument_list|<
name|String
argument_list|,
name|ReplicaCount
argument_list|>
name|getNodeNameVsShardCount
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|createNodeList
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|clusterState
operator|.
name|getLiveNodes
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nodeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|nodeList
operator|.
name|addAll
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
if|if
condition|(
name|createNodeList
operator|!=
literal|null
condition|)
name|nodeList
operator|.
name|retainAll
argument_list|(
name|createNodeList
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|ReplicaCount
argument_list|>
name|nodeNameVsShardCount
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|nodeList
control|)
block|{
name|nodeNameVsShardCount
operator|.
name|put
argument_list|(
name|s
argument_list|,
operator|new
name|ReplicaCount
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|createNodeList
operator|!=
literal|null
condition|)
block|{
comment|// Overrides petty considerations about maxShardsPerNode
if|if
condition|(
name|createNodeList
operator|.
name|size
argument_list|()
operator|!=
name|nodeNameVsShardCount
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"At least one of the node(s) specified are not currently active, no action taken."
argument_list|)
throw|;
block|}
return|return
name|nodeNameVsShardCount
return|;
block|}
name|DocCollection
name|coll
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|Integer
name|maxShardsPerNode
init|=
name|coll
operator|.
name|getInt
argument_list|(
name|MAX_SHARDS_PER_NODE
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|collections
init|=
name|clusterState
operator|.
name|getCollectionsMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|entry
range|:
name|collections
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DocCollection
name|c
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|//identify suitable nodes  by checking the no:of cores in each of them
for|for
control|(
name|Slice
name|slice
range|:
name|c
operator|.
name|getSlices
argument_list|()
control|)
block|{
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
name|ReplicaCount
name|count
init|=
name|nodeNameVsShardCount
operator|.
name|get
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|count
operator|.
name|totalNodes
operator|++
expr_stmt|;
comment|// Used ot "weigh" whether this node should be used later.
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|collectionName
argument_list|)
condition|)
block|{
name|count
operator|.
name|thisCollectionNodes
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|.
name|thisCollectionNodes
operator|>=
name|maxShardsPerNode
condition|)
name|nodeNameVsShardCount
operator|.
name|remove
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|nodeNameVsShardCount
return|;
block|}
block|}
end_class
end_unit
