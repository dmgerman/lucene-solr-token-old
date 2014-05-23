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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HttpSolrServer
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
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|HashMap
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
DECL|class|DeleteShardTest
specifier|public
class|class
name|DeleteShardTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|method|DeleteShardTest
specifier|public
name|DeleteShardTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|shardCount
operator|=
literal|2
expr_stmt|;
name|sliceCount
operator|=
literal|2
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"numShards"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
operator|||
name|printLayoutOnTearDown
condition|)
block|{
name|super
operator|.
name|printLayout
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|controlClient
operator|!=
literal|null
condition|)
block|{
name|controlClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cloudClient
operator|!=
literal|null
condition|)
block|{
name|cloudClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|controlClientCloud
operator|!=
literal|null
condition|)
block|{
name|controlClientCloud
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"numShards"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: Custom hash slice deletion test
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
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
name|Slice
name|slice1
init|=
name|clusterState
operator|.
name|getSlice
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|)
decl_stmt|;
name|Slice
name|slice2
init|=
name|clusterState
operator|.
name|getSlice
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Shard1 not found"
argument_list|,
name|slice1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Shard2 not found"
argument_list|,
name|slice2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Shard1 is not active"
argument_list|,
name|Slice
operator|.
name|ACTIVE
argument_list|,
name|slice1
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Shard2 is not active"
argument_list|,
name|Slice
operator|.
name|ACTIVE
argument_list|,
name|slice2
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|deleteShard
argument_list|(
name|SHARD1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Deleting an active shard should not have succeeded"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrServer
operator|.
name|RemoteSolrException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|setSliceState
argument_list|(
name|SHARD1
argument_list|,
name|Slice
operator|.
name|INACTIVE
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
name|slice1
operator|=
name|clusterState
operator|.
name|getSlice
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Shard1 is not inactive yet."
argument_list|,
name|Slice
operator|.
name|INACTIVE
argument_list|,
name|slice1
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|deleteShard
argument_list|(
name|SHARD1
argument_list|)
expr_stmt|;
name|confirmShardDeletion
argument_list|(
name|SHARD1
argument_list|)
expr_stmt|;
name|setSliceState
argument_list|(
name|SHARD2
argument_list|,
name|Slice
operator|.
name|CONSTRUCTION
argument_list|)
expr_stmt|;
name|deleteShard
argument_list|(
name|SHARD2
argument_list|)
expr_stmt|;
name|confirmShardDeletion
argument_list|(
name|SHARD2
argument_list|)
expr_stmt|;
block|}
DECL|method|confirmShardDeletion
specifier|protected
name|void
name|confirmShardDeletion
parameter_list|(
name|String
name|shard
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|KeeperException
throws|,
name|InterruptedException
block|{
name|ZkStateReader
name|zkStateReader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|int
name|counter
init|=
literal|10
decl_stmt|;
while|while
condition|(
name|counter
operator|--
operator|>
literal|0
condition|)
block|{
name|zkStateReader
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
if|if
condition|(
name|clusterState
operator|.
name|getSlice
argument_list|(
literal|"collection1"
argument_list|,
name|shard
argument_list|)
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
literal|"Cluster still contains shard1 even after waiting for it to be deleted."
argument_list|,
name|clusterState
operator|.
name|getSlice
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteShard
specifier|protected
name|void
name|deleteShard
parameter_list|(
name|String
name|shard
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
throws|,
name|KeeperException
throws|,
name|InterruptedException
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
name|DELETESHARD
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
name|AbstractFullDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"shard"
argument_list|,
name|shard
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
name|String
name|baseUrl
init|=
operator|(
operator|(
name|HttpSolrServer
operator|)
name|shardToJetty
operator|.
name|get
argument_list|(
name|SHARD1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|client
operator|.
name|solrClient
operator|)
operator|.
name|getBaseURL
argument_list|()
decl_stmt|;
name|baseUrl
operator|=
name|baseUrl
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|baseUrl
operator|.
name|length
argument_list|()
operator|-
literal|"collection1"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|HttpSolrServer
name|baseServer
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
name|baseServer
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|baseServer
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|baseServer
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|baseServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|setSliceState
specifier|protected
name|void
name|setSliceState
parameter_list|(
name|String
name|slice
parameter_list|,
name|String
name|state
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
throws|,
name|KeeperException
throws|,
name|InterruptedException
block|{
name|DistributedQueue
name|inQueue
init|=
name|Overseer
operator|.
name|getInQueue
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|propMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
literal|"updateshardstate"
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|slice
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|propMap
argument_list|)
decl_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|inQueue
operator|.
name|offer
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|transition
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|counter
init|=
literal|10
init|;
name|counter
operator|>
literal|0
condition|;
name|counter
operator|--
control|)
block|{
name|zkStateReader
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|String
name|sliceState
init|=
name|clusterState
operator|.
name|getSlice
argument_list|(
literal|"collection1"
argument_list|,
name|slice
argument_list|)
operator|.
name|getState
argument_list|()
decl_stmt|;
if|if
condition|(
name|sliceState
operator|.
name|equals
argument_list|(
name|state
argument_list|)
condition|)
block|{
name|transition
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|transition
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
name|SERVER_ERROR
argument_list|,
literal|"Could not set shard ["
operator|+
name|slice
operator|+
literal|"] as "
operator|+
name|state
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
