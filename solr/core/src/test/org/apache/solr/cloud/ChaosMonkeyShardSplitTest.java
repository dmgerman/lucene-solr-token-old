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
name|SolrServer
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
name|SolrInputDocument
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
name|DocRouter
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
name|cloud
operator|.
name|SolrZkClient
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
name|handler
operator|.
name|component
operator|.
name|HttpShardHandlerFactory
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
name|Collection
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_comment
comment|/**  * Test split phase that occurs when a Collection API split call is made.  */
end_comment
begin_class
annotation|@
name|Slow
DECL|class|ChaosMonkeyShardSplitTest
specifier|public
class|class
name|ChaosMonkeyShardSplitTest
extends|extends
name|ShardSplitTest
block|{
DECL|field|TIMEOUT
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
literal|10000
decl_stmt|;
DECL|field|killCounter
specifier|private
name|AtomicInteger
name|killCounter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
annotation|@
name|Before
annotation|@
name|Override
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
block|}
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
name|waitForThingsToLevelOut
argument_list|(
literal|15
argument_list|)
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
specifier|final
name|DocRouter
name|router
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|)
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|Slice
name|shard1
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
name|DocRouter
operator|.
name|Range
name|shard1Range
init|=
name|shard1
operator|.
name|getRange
argument_list|()
operator|!=
literal|null
condition|?
name|shard1
operator|.
name|getRange
argument_list|()
else|:
name|router
operator|.
name|fullRange
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
init|=
name|router
operator|.
name|partitionRange
argument_list|(
literal|2
argument_list|,
name|shard1Range
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|docCounts
init|=
operator|new
name|int
index|[
name|ranges
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|numReplicas
init|=
name|shard1
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|Thread
name|indexThread
init|=
literal|null
decl_stmt|;
name|OverseerRestarter
name|killer
init|=
literal|null
decl_stmt|;
name|Thread
name|killerThread
init|=
literal|null
decl_stmt|;
specifier|final
name|SolrServer
name|solrServer
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
literal|100
condition|;
name|id
operator|++
control|)
block|{
name|indexAndUpdateCount
argument_list|(
name|router
argument_list|,
name|ranges
argument_list|,
name|docCounts
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|indexThread
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|max
init|=
name|atLeast
argument_list|(
literal|401
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|101
init|;
name|id
operator|<
name|max
condition|;
name|id
operator|++
control|)
block|{
try|try
block|{
name|indexAndUpdateCount
argument_list|(
name|router
argument_list|,
name|ranges
argument_list|,
name|docCounts
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|atLeast
argument_list|(
literal|25
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while adding doc"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
expr_stmt|;
name|indexThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// kill the leader
name|CloudJettyRunner
name|leaderJetty
init|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
decl_stmt|;
name|chaosMonkey
operator|.
name|killJetty
argument_list|(
name|leaderJetty
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|90
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|checkShardConsistency
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|CloudJettyRunner
name|deadJetty
init|=
name|leaderJetty
decl_stmt|;
comment|// TODO: Check total docs ?
comment|// long cloudClientDocs = cloudClient.query(new
comment|// SolrQuery("*:*")).getResults().getNumFound();
comment|// Wait until new leader is elected
while|while
condition|(
name|deadJetty
operator|==
name|leaderJetty
condition|)
block|{
name|updateMappingsFromZk
argument_list|(
name|this
operator|.
name|jettys
argument_list|,
name|this
operator|.
name|clients
argument_list|)
expr_stmt|;
name|leaderJetty
operator|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
expr_stmt|;
block|}
comment|// bring back dead node
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|deadJetty
operator|.
name|jetty
argument_list|)
expr_stmt|;
comment|// he is not the leader anymore
name|waitTillRecovered
argument_list|()
expr_stmt|;
comment|// Kill the overseer
comment|// TODO: Actually kill the Overseer instance
name|killer
operator|=
operator|new
name|OverseerRestarter
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|killerThread
operator|=
operator|new
name|Thread
argument_list|(
name|killer
argument_list|)
expr_stmt|;
name|killerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|killCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|splitShard
argument_list|(
name|SHARD1
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Layout after split: \n"
argument_list|)
expr_stmt|;
name|printLayout
argument_list|()
expr_stmt|;
comment|// distributed commit on all shards
block|}
finally|finally
block|{
if|if
condition|(
name|indexThread
operator|!=
literal|null
condition|)
name|indexThread
operator|.
name|join
argument_list|()
expr_stmt|;
if|if
condition|(
name|solrServer
operator|!=
literal|null
condition|)
name|solrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|killer
operator|!=
literal|null
condition|)
block|{
name|killer
operator|.
name|run
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|killerThread
operator|!=
literal|null
condition|)
block|{
name|killerThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|checkDocCountsAndShardStates
argument_list|(
name|docCounts
argument_list|,
name|numReplicas
argument_list|)
expr_stmt|;
comment|// todo - can't call waitForThingsToLevelOut because it looks for
comment|// jettys of all shards
comment|// and the new sub-shards don't have any.
name|waitForRecoveriesToFinish
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// waitForThingsToLevelOut(15);
block|}
DECL|class|OverseerRestarter
specifier|private
class|class
name|OverseerRestarter
implements|implements
name|Runnable
block|{
DECL|field|overseerClient
name|SolrZkClient
name|overseerClient
init|=
literal|null
decl_stmt|;
DECL|field|run
specifier|public
specifier|volatile
name|boolean
name|run
init|=
literal|true
decl_stmt|;
DECL|field|zkAddress
specifier|private
specifier|final
name|String
name|zkAddress
decl_stmt|;
DECL|method|OverseerRestarter
specifier|public
name|OverseerRestarter
parameter_list|(
name|String
name|zkAddress
parameter_list|)
block|{
name|this
operator|.
name|zkAddress
operator|=
name|zkAddress
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|overseerClient
operator|=
name|electNewOverseer
argument_list|(
name|zkAddress
argument_list|)
expr_stmt|;
while|while
condition|(
name|run
condition|)
block|{
if|if
condition|(
name|killCounter
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|killCounter
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Killing overseer after 800ms"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|800
argument_list|)
expr_stmt|;
name|overseerClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|overseerClient
operator|=
name|electNewOverseer
argument_list|(
name|zkAddress
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// e.printStackTrace();
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// e.printStackTrace();
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// ignore
block|}
finally|finally
block|{
if|if
condition|(
name|overseerClient
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|overseerClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
block|}
DECL|method|waitTillRecovered
specifier|private
name|void
name|waitTillRecovered
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
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
name|DocCollection
name|collection1
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
literal|"collection1"
argument_list|)
decl_stmt|;
name|Slice
name|slice
init|=
name|collection1
operator|.
name|getSlice
argument_list|(
literal|"shard1"
argument_list|)
decl_stmt|;
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
name|boolean
name|allActive
init|=
literal|true
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
operator|!
name|clusterState
operator|.
name|liveNodesContain
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
operator|||
operator|!
name|replica
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
condition|)
block|{
name|allActive
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|allActive
condition|)
block|{
return|return;
block|}
block|}
name|printLayout
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"timeout waiting to see recovered node"
argument_list|)
expr_stmt|;
block|}
comment|// skip the randoms - they can deadlock...
annotation|@
name|Override
DECL|method|indexr
specifier|protected
name|void
name|indexr
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
literal|"rnd_b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/**    * Elects a new overseer    *    * @return SolrZkClient    */
DECL|method|electNewOverseer
specifier|private
name|SolrZkClient
name|electNewOverseer
parameter_list|(
name|String
name|address
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|address
argument_list|,
name|TIMEOUT
argument_list|)
decl_stmt|;
name|ZkStateReader
name|reader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|LeaderElector
name|overseerElector
init|=
operator|new
name|LeaderElector
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
comment|// TODO: close Overseer
name|Overseer
name|overseer
init|=
operator|new
name|Overseer
argument_list|(
operator|new
name|HttpShardHandlerFactory
argument_list|()
operator|.
name|getShardHandler
argument_list|()
argument_list|,
literal|"/admin/cores"
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|overseer
operator|.
name|close
argument_list|()
expr_stmt|;
name|ElectionContext
name|ec
init|=
operator|new
name|OverseerElectionContext
argument_list|(
name|zkClient
argument_list|,
name|overseer
argument_list|,
name|address
operator|.
name|replaceAll
argument_list|(
literal|"/"
argument_list|,
literal|"_"
argument_list|)
argument_list|)
decl_stmt|;
name|overseerElector
operator|.
name|setup
argument_list|(
name|ec
argument_list|)
expr_stmt|;
name|overseerElector
operator|.
name|joinElection
argument_list|(
name|ec
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|zkClient
return|;
block|}
block|}
end_class
end_unit
