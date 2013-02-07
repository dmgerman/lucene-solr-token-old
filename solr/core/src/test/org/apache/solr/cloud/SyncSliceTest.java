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
name|HashSet
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
name|Set
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|UpdateRequest
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
name|params
operator|.
name|CollectionParams
operator|.
name|CollectionAction
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
name|AfterClass
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
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_comment
comment|/**  * Test sync phase that occurs when Leader goes down and a new Leader is  * elected.  */
end_comment
begin_class
annotation|@
name|Slow
DECL|class|SyncSliceTest
specifier|public
class|class
name|SyncSliceTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeSuperClass
specifier|public
specifier|static
name|void
name|beforeSuperClass
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: we use an fs based dir because something
comment|// like a ram dir will not recovery correctly right now
comment|// due to tran log persisting across restarts
name|useFactory
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterSuperClass
specifier|public
specifier|static
name|void
name|afterSuperClass
parameter_list|()
block|{        }
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
comment|// we expect this time of exception as shards go up and down...
comment|//ignoreException(".*");
name|System
operator|.
name|setProperty
argument_list|(
literal|"numShards"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sliceCount
argument_list|)
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
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
DECL|method|SyncSliceTest
specifier|public
name|SyncSliceTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|1
expr_stmt|;
name|shardCount
operator|=
name|TEST_NIGHTLY
condition|?
literal|7
else|:
literal|4
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
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|skipServers
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|docId
init|=
literal|0
decl_stmt|;
name|indexDoc
argument_list|(
name|skipServers
argument_list|,
name|id
argument_list|,
name|docId
operator|++
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|tlong
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"to come to the aid of their country."
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|skipServers
argument_list|,
name|id
argument_list|,
name|docId
operator|++
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|tlong
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"old haven was blue."
argument_list|)
expr_stmt|;
name|skipServers
operator|.
name|add
argument_list|(
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|url
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|skipServers
argument_list|,
name|id
argument_list|,
name|docId
operator|++
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|tlong
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"but the song was fancy."
argument_list|)
expr_stmt|;
name|skipServers
operator|.
name|add
argument_list|(
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|url
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|skipServers
argument_list|,
name|id
argument_list|,
name|docId
operator|++
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|tlong
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"under the moon and over the lake"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// shard should be inconsistent
name|String
name|shardFailMessage
init|=
name|checkShardConsistency
argument_list|(
literal|"shard1"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|shardFailMessage
argument_list|)
expr_stmt|;
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
name|CollectionAction
operator|.
name|SYNCSHARD
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
literal|"collection1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"shard"
argument_list|,
literal|"shard1"
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
literal|"shard1"
argument_list|)
operator|.
name|get
argument_list|(
literal|2
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
literal|30000
argument_list|)
expr_stmt|;
name|baseServer
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|checkShardConsistency
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|long
name|cloudClientDocs
init|=
name|cloudClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|cloudClientDocs
argument_list|)
expr_stmt|;
comment|// kill the leader - new leader could have all the docs or be missing one
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
name|skipServers
operator|=
name|getRandomOtherJetty
argument_list|(
name|leaderJetty
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// but not the leader
comment|// this doc won't be on one node
name|indexDoc
argument_list|(
name|skipServers
argument_list|,
name|id
argument_list|,
name|docId
operator|++
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|tlong
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"to come to the aid of their country."
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|CloudJettyRunner
argument_list|>
name|jetties
init|=
operator|new
name|HashSet
argument_list|<
name|CloudJettyRunner
argument_list|>
argument_list|()
decl_stmt|;
name|jetties
operator|.
name|addAll
argument_list|(
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
argument_list|)
expr_stmt|;
name|jetties
operator|.
name|remove
argument_list|(
name|leaderJetty
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shardCount
operator|-
literal|1
argument_list|,
name|jetties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
name|cloudClientDocs
operator|=
name|cloudClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|cloudClientDocs
argument_list|)
expr_stmt|;
name|CloudJettyRunner
name|deadJetty
init|=
name|leaderJetty
decl_stmt|;
comment|// let's get the latest leader
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
comment|// give a moment to be sure it has started recovering
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|15
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|skipServers
operator|=
name|getRandomOtherJetty
argument_list|(
name|leaderJetty
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|skipServers
operator|.
name|addAll
argument_list|(
name|getRandomOtherJetty
argument_list|(
name|leaderJetty
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// skip list should be
comment|//System.out.println("leader:" + leaderJetty.url);
comment|//System.out.println("skip list:" + skipServers);
comment|// we are skipping  2 nodes
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|skipServers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// more docs than can peer sync
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|300
condition|;
name|i
operator|++
control|)
block|{
name|indexDoc
argument_list|(
name|skipServers
argument_list|,
name|id
argument_list|,
name|docId
operator|++
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|tlong
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"to come to the aid of their country."
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// shard should be inconsistent
name|shardFailMessage
operator|=
name|waitTillInconsistent
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Test Setup Failure: shard1 should have just been set up to be inconsistent - but it's still consistent"
argument_list|,
name|shardFailMessage
argument_list|)
expr_stmt|;
name|jetties
operator|=
operator|new
name|HashSet
argument_list|<
name|CloudJettyRunner
argument_list|>
argument_list|()
expr_stmt|;
name|jetties
operator|.
name|addAll
argument_list|(
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
argument_list|)
expr_stmt|;
name|jetties
operator|.
name|remove
argument_list|(
name|leaderJetty
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shardCount
operator|-
literal|1
argument_list|,
name|jetties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// kill the current leader
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
literal|3000
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
literal|2000
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|checkShardConsistency
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|waitTillInconsistent
specifier|private
name|String
name|waitTillInconsistent
parameter_list|()
throws|throws
name|Exception
throws|,
name|InterruptedException
block|{
name|String
name|shardFailMessage
init|=
literal|null
decl_stmt|;
name|shardFailMessage
operator|=
name|pollConsistency
argument_list|(
name|shardFailMessage
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|shardFailMessage
operator|=
name|pollConsistency
argument_list|(
name|shardFailMessage
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
name|shardFailMessage
operator|=
name|pollConsistency
argument_list|(
name|shardFailMessage
argument_list|,
literal|5000
argument_list|)
expr_stmt|;
name|shardFailMessage
operator|=
name|pollConsistency
argument_list|(
name|shardFailMessage
argument_list|,
literal|8000
argument_list|)
expr_stmt|;
return|return
name|shardFailMessage
return|;
block|}
DECL|method|pollConsistency
specifier|private
name|String
name|pollConsistency
parameter_list|(
name|String
name|shardFailMessage
parameter_list|,
name|int
name|sleep
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|Exception
block|{
try|try
block|{
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|shardFailMessage
operator|==
literal|null
condition|)
block|{
comment|// try again
name|Thread
operator|.
name|sleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
name|shardFailMessage
operator|=
name|checkShardConsistency
argument_list|(
literal|"shard1"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|shardFailMessage
return|;
block|}
DECL|method|getRandomJetty
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getRandomJetty
parameter_list|()
block|{
return|return
name|getRandomOtherJetty
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getRandomOtherJetty
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getRandomOtherJetty
parameter_list|(
name|CloudJettyRunner
name|leader
parameter_list|,
name|CloudJettyRunner
name|down
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|skipServers
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|candidates
init|=
operator|new
name|ArrayList
argument_list|<
name|CloudJettyRunner
argument_list|>
argument_list|()
decl_stmt|;
name|candidates
operator|.
name|addAll
argument_list|(
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|leader
operator|!=
literal|null
condition|)
block|{
name|candidates
operator|.
name|remove
argument_list|(
name|leader
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|down
operator|!=
literal|null
condition|)
block|{
name|candidates
operator|.
name|remove
argument_list|(
name|down
argument_list|)
expr_stmt|;
block|}
name|CloudJettyRunner
name|cjetty
init|=
name|candidates
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|candidates
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|skipServers
operator|.
name|add
argument_list|(
name|cjetty
operator|.
name|url
operator|+
literal|"/"
argument_list|)
expr_stmt|;
return|return
name|skipServers
return|;
block|}
DECL|method|indexDoc
specifier|protected
name|void
name|indexDoc
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|skipServers
parameter_list|,
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
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
name|controlClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|UpdateRequest
name|ureq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|ureq
operator|.
name|add
argument_list|(
name|doc
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
name|String
name|skip
range|:
name|skipServers
control|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"test.distrib.skip.servers"
argument_list|,
name|skip
argument_list|)
expr_stmt|;
block|}
name|ureq
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|process
argument_list|(
name|cloudClient
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
block|}
end_class
end_unit
