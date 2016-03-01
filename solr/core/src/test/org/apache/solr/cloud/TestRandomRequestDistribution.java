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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|Collection
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
name|lucene
operator|.
name|util
operator|.
name|TestUtil
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
name|BaseDistributedSearchTestCase
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
name|SolrTestCaseJ4
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
name|SolrQuery
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
name|embedded
operator|.
name|JettySolrRunner
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
name|HttpSolrClient
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
name|CollectionAdminRequest
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
name|overseer
operator|.
name|OverseerAction
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
name|ZkNodeProps
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
name|ZkStateReader
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
name|Utils
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
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|SolrRequestHandler
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_class
annotation|@
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
DECL|class|TestRandomRequestDistribution
specifier|public
class|class
name|TestRandomRequestDistribution
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|nodeNames
name|List
argument_list|<
name|String
argument_list|>
name|nodeNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
annotation|@
name|Test
annotation|@
name|BaseDistributedSearchTestCase
operator|.
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|3
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForThingsToLevelOut
argument_list|(
literal|30
argument_list|)
expr_stmt|;
for|for
control|(
name|CloudJettyRunner
name|cloudJetty
range|:
name|cloudJettys
control|)
block|{
name|nodeNames
operator|.
name|add
argument_list|(
name|cloudJetty
operator|.
name|nodeName
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|nodeNames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|testRequestTracking
argument_list|()
expr_stmt|;
name|testQueryAgainstDownReplica
argument_list|()
expr_stmt|;
block|}
comment|/**    * Asserts that requests aren't always sent to the same poor node. See SOLR-7493    */
DECL|method|testRequestTracking
specifier|private
name|void
name|testRequestTracking
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
literal|"a1x2"
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|2
argument_list|)
operator|.
name|setCreateNodeSet
argument_list|(
name|nodeNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
literal|','
operator|+
name|nodeNames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
literal|"b1x1"
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|1
argument_list|)
operator|.
name|setCreateNodeSet
argument_list|(
name|nodeNames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|"a1x2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|"b1x1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|()
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|DocCollection
name|b1x1
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
literal|"b1x1"
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|b1x1
operator|.
name|getSlice
argument_list|(
literal|"shard1"
argument_list|)
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|replicas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|baseUrl
init|=
name|replicas
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|baseUrl
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|baseUrl
operator|+=
literal|"/"
expr_stmt|;
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|baseUrl
operator|+
literal|"a1x2"
argument_list|)
decl_stmt|;
name|client
operator|.
name|setSoTimeout
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|client
operator|.
name|setConnectionTimeout
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Making requests to "
operator|+
name|baseUrl
operator|+
literal|"a1x2"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|client
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|shardVsCount
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|JettySolrRunner
name|runner
range|:
name|jettys
control|)
block|{
name|CoreContainer
name|container
init|=
name|runner
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrCore
name|core
range|:
name|container
operator|.
name|getCores
argument_list|()
control|)
block|{
name|SolrRequestHandler
name|select
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|long
name|c
init|=
operator|(
name|long
operator|)
name|select
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
decl_stmt|;
name|shardVsCount
operator|.
name|put
argument_list|(
name|core
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Shard count map = "
operator|+
name|shardVsCount
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|shardVsCount
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
literal|"Shard "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" received all 10 requests"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|!=
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Asserts that requests against a collection are only served by a 'active' local replica    */
DECL|method|testQueryAgainstDownReplica
specifier|private
name|void
name|testQueryAgainstDownReplica
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Creating collection 'football' with 1 shard and 2 replicas"
argument_list|)
expr_stmt|;
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
literal|"football"
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|2
argument_list|)
operator|.
name|setCreateNodeSet
argument_list|(
name|nodeNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
literal|','
operator|+
name|nodeNames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|"football"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|()
expr_stmt|;
name|Replica
name|leader
init|=
literal|null
decl_stmt|;
name|Replica
name|notLeader
init|=
literal|null
decl_stmt|;
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getSlice
argument_list|(
literal|"football"
argument_list|,
literal|"shard1"
argument_list|)
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
if|if
condition|(
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|LEADER_PROP
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|leader
operator|=
name|replica
expr_stmt|;
block|}
else|else
block|{
name|notLeader
operator|=
name|replica
expr_stmt|;
block|}
block|}
comment|//Simulate a replica being in down state.
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerAction
operator|.
name|STATE
operator|.
name|toLower
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
name|notLeader
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
argument_list|,
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|,
name|notLeader
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|,
literal|"football"
argument_list|,
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
argument_list|,
literal|"shard1"
argument_list|,
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
name|notLeader
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|,
name|ZkStateReader
operator|.
name|ROLES_PROP
argument_list|,
literal|""
argument_list|,
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|,
name|Replica
operator|.
name|State
operator|.
name|DOWN
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Forcing {} to go into 'down' state"
argument_list|,
name|notLeader
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|DistributedQueue
name|q
init|=
name|Overseer
operator|.
name|getStateUpdateQueue
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
name|q
operator|.
name|offer
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
name|verifyReplicaStatus
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
literal|"football"
argument_list|,
literal|"shard1"
argument_list|,
name|notLeader
operator|.
name|getName
argument_list|()
argument_list|,
name|Replica
operator|.
name|State
operator|.
name|DOWN
argument_list|)
expr_stmt|;
comment|//Query against the node which hosts the down replica
name|String
name|baseUrl
init|=
name|notLeader
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|baseUrl
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|baseUrl
operator|+=
literal|"/"
expr_stmt|;
name|String
name|path
init|=
name|baseUrl
operator|+
literal|"football"
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Firing queries against path="
operator|+
name|path
argument_list|)
expr_stmt|;
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|client
operator|.
name|setSoTimeout
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|client
operator|.
name|setConnectionTimeout
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|SolrCore
name|leaderCore
init|=
literal|null
decl_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|CoreContainer
name|container
init|=
name|jetty
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrCore
name|core
range|:
name|container
operator|.
name|getCores
argument_list|()
control|)
block|{
if|if
condition|(
name|core
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|leader
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|leaderCore
operator|=
name|core
expr_stmt|;
break|break;
block|}
block|}
block|}
name|assertNotNull
argument_list|(
name|leaderCore
argument_list|)
expr_stmt|;
comment|//All queries should be served by the active replica
comment|//To make sure that's true we keep querying the down replica
comment|//If queries are getting processed by the down replica then the cluster state hasn't updated for that replica locally
comment|//So we keep trying till it has updated and then verify if ALL queries go to the active reploca
name|long
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|count
operator|++
expr_stmt|;
name|client
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|select
init|=
name|leaderCore
operator|.
name|getRequestHandler
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|long
name|c
init|=
operator|(
name|long
operator|)
name|select
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|1
condition|)
block|{
break|break;
comment|//cluster state has got update locally
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|>
literal|10000
condition|)
block|{
name|fail
argument_list|(
literal|"After 10k queries we still see all requests being processed by the down replica"
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Now we fire a few additional queries and make sure ALL of them
comment|//are served by the active replica
name|int
name|moreQueries
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|count
operator|=
literal|1
expr_stmt|;
comment|//Since 1 query has already hit the leader
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|moreQueries
condition|;
name|i
operator|++
control|)
block|{
name|client
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|SolrRequestHandler
name|select
init|=
name|leaderCore
operator|.
name|getRequestHandler
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|long
name|c
init|=
operator|(
name|long
operator|)
name|select
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Query wasn't served by leader"
argument_list|,
name|count
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
