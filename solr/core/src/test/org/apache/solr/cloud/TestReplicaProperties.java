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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|QueryRequest
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
name|params
operator|.
name|CollectionParams
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
name|ModifiableSolrParams
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
name|zookeeper
operator|.
name|KeeperException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
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
begin_class
annotation|@
name|Slow
DECL|class|TestReplicaProperties
specifier|public
class|class
name|TestReplicaProperties
extends|extends
name|ReplicaPropertiesBase
block|{
DECL|field|COLLECTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTION_NAME
init|=
literal|"testcollection"
decl_stmt|;
DECL|method|TestReplicaProperties
specifier|public
name|TestReplicaProperties
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
name|sliceCount
operator|=
literal|2
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|4
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|CloudSolrClient
name|client
init|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
init|)
block|{
comment|// Mix up a bunch of different combinations of shards and replicas in order to exercise boundary cases.
comment|// shards, replicationfactor, maxreplicaspernode
name|int
name|shards
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
decl_stmt|;
if|if
condition|(
name|shards
operator|<
literal|2
condition|)
name|shards
operator|=
literal|2
expr_stmt|;
name|int
name|rFactor
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
if|if
condition|(
name|rFactor
operator|<
literal|2
condition|)
name|rFactor
operator|=
literal|2
expr_stmt|;
name|createCollection
argument_list|(
literal|null
argument_list|,
name|COLLECTION_NAME
argument_list|,
name|shards
argument_list|,
name|rFactor
argument_list|,
name|shards
operator|*
name|rFactor
operator|+
literal|1
argument_list|,
name|client
argument_list|,
literal|null
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
block|}
name|waitForCollection
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|COLLECTION_NAME
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|COLLECTION_NAME
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|listCollection
argument_list|()
expr_stmt|;
name|clusterAssignPropertyTest
argument_list|()
expr_stmt|;
block|}
DECL|method|listCollection
specifier|private
name|void
name|listCollection
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
try|try
init|(
name|CloudSolrClient
name|client
init|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
init|)
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|LIST
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SolrRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/collections"
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
init|=
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|collections
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|rsp
operator|.
name|get
argument_list|(
literal|"collections"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"control_collection was not found in list"
argument_list|,
name|collections
operator|.
name|contains
argument_list|(
literal|"control_collection"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DEFAULT_COLLECTION
operator|+
literal|" was not found in list"
argument_list|,
name|collections
operator|.
name|contains
argument_list|(
name|DEFAULT_COLLECTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|COLLECTION_NAME
operator|+
literal|" was not found in list"
argument_list|,
name|collections
operator|.
name|contains
argument_list|(
name|COLLECTION_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|clusterAssignPropertyTest
specifier|private
name|void
name|clusterAssignPropertyTest
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|CloudSolrClient
name|client
init|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
init|)
block|{
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
try|try
block|{
name|doPropertyAction
argument_list|(
name|client
argument_list|,
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|BALANCESHARDUNIQUE
operator|.
name|toString
argument_list|()
argument_list|,
literal|"property"
argument_list|,
literal|"preferredLeader"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Should have seen missing required parameter 'collection' error"
argument_list|,
name|se
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Missing required parameter: collection"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doPropertyAction
argument_list|(
name|client
argument_list|,
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|BALANCESHARDUNIQUE
operator|.
name|toString
argument_list|()
argument_list|,
literal|"collection"
argument_list|,
name|COLLECTION_NAME
argument_list|,
literal|"property"
argument_list|,
literal|"preferredLeader"
argument_list|)
expr_stmt|;
name|verifyUniqueAcrossCollection
argument_list|(
name|client
argument_list|,
name|COLLECTION_NAME
argument_list|,
literal|"property.preferredleader"
argument_list|)
expr_stmt|;
name|doPropertyAction
argument_list|(
name|client
argument_list|,
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|BALANCESHARDUNIQUE
operator|.
name|toString
argument_list|()
argument_list|,
literal|"collection"
argument_list|,
name|COLLECTION_NAME
argument_list|,
literal|"property"
argument_list|,
literal|"property.newunique"
argument_list|,
literal|"shardUnique"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|verifyUniqueAcrossCollection
argument_list|(
name|client
argument_list|,
name|COLLECTION_NAME
argument_list|,
literal|"property.newunique"
argument_list|)
expr_stmt|;
try|try
block|{
name|doPropertyAction
argument_list|(
name|client
argument_list|,
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|BALANCESHARDUNIQUE
operator|.
name|toString
argument_list|()
argument_list|,
literal|"collection"
argument_list|,
name|COLLECTION_NAME
argument_list|,
literal|"property"
argument_list|,
literal|"whatever"
argument_list|,
literal|"shardUnique"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown an exception here."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Should have gotten a specific error message here"
argument_list|,
name|se
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Balancing properties amongst replicas in a slice requires that the "
operator|+
literal|"property be pre-defined as a unique property (e.g. 'preferredLeader') or that 'shardUnique' be set to 'true'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Should be able to set non-unique-per-slice values in several places.
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|COLLECTION_NAME
argument_list|)
operator|.
name|getSlicesMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|sliceList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|slices
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|c1_s1
init|=
name|sliceList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|replicasList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|slices
operator|.
name|get
argument_list|(
name|c1_s1
argument_list|)
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|c1_s1_r1
init|=
name|replicasList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|c1_s1_r2
init|=
name|replicasList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|addProperty
argument_list|(
name|client
argument_list|,
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|ADDREPLICAPROP
operator|.
name|toString
argument_list|()
argument_list|,
literal|"collection"
argument_list|,
name|COLLECTION_NAME
argument_list|,
literal|"shard"
argument_list|,
name|c1_s1
argument_list|,
literal|"replica"
argument_list|,
name|c1_s1_r1
argument_list|,
literal|"property"
argument_list|,
literal|"bogus1"
argument_list|,
literal|"property.value"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|addProperty
argument_list|(
name|client
argument_list|,
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|ADDREPLICAPROP
operator|.
name|toString
argument_list|()
argument_list|,
literal|"collection"
argument_list|,
name|COLLECTION_NAME
argument_list|,
literal|"shard"
argument_list|,
name|c1_s1
argument_list|,
literal|"replica"
argument_list|,
name|c1_s1_r2
argument_list|,
literal|"property"
argument_list|,
literal|"property.bogus1"
argument_list|,
literal|"property.value"
argument_list|,
literal|"whatever"
argument_list|)
expr_stmt|;
try|try
block|{
name|doPropertyAction
argument_list|(
name|client
argument_list|,
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|BALANCESHARDUNIQUE
operator|.
name|toString
argument_list|()
argument_list|,
literal|"collection"
argument_list|,
name|COLLECTION_NAME
argument_list|,
literal|"property"
argument_list|,
literal|"bogus1"
argument_list|,
literal|"shardUnique"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown parameter error here"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Should have caught specific exception "
argument_list|,
name|se
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Balancing properties amongst replicas in a slice requires that the property be "
operator|+
literal|"pre-defined as a unique property (e.g. 'preferredLeader') or that 'shardUnique' be set to 'true'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Should have no effect despite the "shardUnique" param being set.
name|doPropertyAction
argument_list|(
name|client
argument_list|,
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|BALANCESHARDUNIQUE
operator|.
name|toString
argument_list|()
argument_list|,
literal|"collection"
argument_list|,
name|COLLECTION_NAME
argument_list|,
literal|"property"
argument_list|,
literal|"property.bogus1"
argument_list|,
literal|"shardUnique"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|verifyPropertyVal
argument_list|(
name|client
argument_list|,
name|COLLECTION_NAME
argument_list|,
name|c1_s1_r1
argument_list|,
literal|"property.bogus1"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|verifyPropertyVal
argument_list|(
name|client
argument_list|,
name|COLLECTION_NAME
argument_list|,
name|c1_s1_r2
argument_list|,
literal|"property.bogus1"
argument_list|,
literal|"whatever"
argument_list|)
expr_stmt|;
comment|// At this point we've assigned a preferred leader. Make it happen and check that all the nodes that are
comment|// leaders _also_ have the preferredLeader property set.
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
name|doPropertyAction
argument_list|(
name|client
argument_list|,
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|REBALANCELEADERS
operator|.
name|toString
argument_list|()
argument_list|,
literal|"collection"
argument_list|,
name|COLLECTION_NAME
argument_list|)
decl_stmt|;
name|verifyLeaderAssignment
argument_list|(
name|client
argument_list|,
name|COLLECTION_NAME
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyLeaderAssignment
specifier|private
name|void
name|verifyLeaderAssignment
parameter_list|(
name|CloudSolrClient
name|client
parameter_list|,
name|String
name|collectionName
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|String
name|lastFailMsg
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|300
condition|;
operator|++
name|idx
control|)
block|{
comment|// Keep trying while Overseer writes the ZK state for up to 30 seconds.
name|lastFailMsg
operator|=
literal|""
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|clusterState
operator|.
name|getSlices
argument_list|(
name|collectionName
argument_list|)
control|)
block|{
name|Boolean
name|foundLeader
init|=
literal|false
decl_stmt|;
name|Boolean
name|foundPreferred
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
name|Boolean
name|isLeader
init|=
name|replica
operator|.
name|getBool
argument_list|(
literal|"leader"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Boolean
name|isPreferred
init|=
name|replica
operator|.
name|getBool
argument_list|(
literal|"property.preferredleader"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|isLeader
operator|!=
name|isPreferred
condition|)
block|{
name|lastFailMsg
operator|=
literal|"Replica should NOT have preferredLeader != leader. Preferred: "
operator|+
name|isPreferred
operator|.
name|toString
argument_list|()
operator|+
literal|" leader is "
operator|+
name|isLeader
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|foundLeader
operator|&&
name|isLeader
condition|)
block|{
name|lastFailMsg
operator|=
literal|"There should only be a single leader in _any_ shard! Replica "
operator|+
name|replica
operator|.
name|getName
argument_list|()
operator|+
literal|" is the second leader in slice "
operator|+
name|slice
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|foundPreferred
operator|&&
name|isPreferred
condition|)
block|{
name|lastFailMsg
operator|=
literal|"There should only be a single preferredLeader in _any_ shard! Replica "
operator|+
name|replica
operator|.
name|getName
argument_list|()
operator|+
literal|" is the second preferredLeader in slice "
operator|+
name|slice
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|foundLeader
operator|=
name|foundLeader
condition|?
name|foundLeader
else|:
name|isLeader
expr_stmt|;
name|foundPreferred
operator|=
name|foundPreferred
condition|?
name|foundPreferred
else|:
name|isPreferred
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lastFailMsg
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
name|lastFailMsg
argument_list|)
expr_stmt|;
block|}
DECL|method|addProperty
specifier|private
name|void
name|addProperty
parameter_list|(
name|CloudSolrClient
name|client
parameter_list|,
name|String
modifier|...
name|paramsIn
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|assertTrue
argument_list|(
literal|"paramsIn must be an even multiple of 2, it is: "
operator|+
name|paramsIn
operator|.
name|length
argument_list|,
operator|(
name|paramsIn
operator|.
name|length
operator|%
literal|2
operator|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|paramsIn
operator|.
name|length
condition|;
name|idx
operator|+=
literal|2
control|)
block|{
name|params
operator|.
name|set
argument_list|(
name|paramsIn
index|[
name|idx
index|]
argument_list|,
name|paramsIn
index|[
name|idx
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/collections"
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
