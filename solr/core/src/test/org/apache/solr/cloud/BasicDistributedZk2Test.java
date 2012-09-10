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
name|Set
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|CommonParams
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
name|servlet
operator|.
name|SolrDispatchFilter
import|;
end_import
begin_comment
comment|/**  * This test simply does a bunch of basic things in solrcloud mode and asserts things  * work as expected.  */
end_comment
begin_class
DECL|class|BasicDistributedZk2Test
specifier|public
class|class
name|BasicDistributedZk2Test
extends|extends
name|AbstractFullDistribZkTestBase
block|{
comment|/*    * (non-Javadoc)    *     * @see org.apache.solr.BaseDistributedSearchTestCase#doTest()    *     * Create 3 shards, each with one replica    */
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
name|boolean
name|testFinished
init|=
literal|false
decl_stmt|;
try|try
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
name|indexr
argument_list|(
name|id
argument_list|,
literal|1
argument_list|,
name|i1
argument_list|,
literal|100
argument_list|,
name|tlong
argument_list|,
literal|100
argument_list|,
name|t1
argument_list|,
literal|"now is the time for all good men"
argument_list|,
literal|"foo_f"
argument_list|,
literal|1.414f
argument_list|,
literal|"foo_b"
argument_list|,
literal|"true"
argument_list|,
literal|"foo_d"
argument_list|,
literal|1.414d
argument_list|)
expr_stmt|;
comment|// make sure we are in a steady state...
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|assertDocCounts
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|indexAbunchOfDocs
argument_list|()
expr_stmt|;
comment|// check again
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|assertDocCounts
argument_list|(
name|VERBOSE
argument_list|)
expr_stmt|;
name|checkQueries
argument_list|()
expr_stmt|;
name|assertDocCounts
argument_list|(
name|VERBOSE
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"n_tl1 desc"
argument_list|)
expr_stmt|;
name|brindDownShardIndexSomeDocsAndRecover
argument_list|()
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"n_tl1 desc"
argument_list|)
expr_stmt|;
comment|// test adding another replica to a shard - it should do a
comment|// recovery/replication to pick up the index from the leader
name|addNewReplica
argument_list|()
expr_stmt|;
name|long
name|docId
init|=
name|testUpdateAndDelete
argument_list|()
decl_stmt|;
comment|// index a bad doc...
try|try
block|{
name|indexr
argument_list|(
name|t1
argument_list|,
literal|"a doc with no id"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"this should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|// expected
block|}
comment|// TODO: bring this to it's own method?
comment|// try indexing to a leader that has no replicas up
name|ZkNodeProps
name|leaderProps
init|=
name|zkStateReader
operator|.
name|getLeaderProps
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD2
argument_list|)
decl_stmt|;
name|String
name|nodeName
init|=
name|leaderProps
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
decl_stmt|;
name|chaosMonkey
operator|.
name|stopShardExcept
argument_list|(
name|SHARD2
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
name|SolrServer
name|client
init|=
name|getClient
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
name|index_specific
argument_list|(
name|client
argument_list|,
literal|"id"
argument_list|,
name|docId
operator|+
literal|1
argument_list|,
name|t1
argument_list|,
literal|"what happens here?"
argument_list|)
expr_stmt|;
comment|// expire a session...
name|CloudJettyRunner
name|cloudJetty
init|=
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|chaosMonkey
operator|.
name|expireSession
argument_list|(
name|cloudJetty
operator|.
name|jetty
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
literal|"id"
argument_list|,
name|docId
operator|+
literal|1
argument_list|,
name|t1
argument_list|,
literal|"slip this doc in"
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|checkShardConsistency
argument_list|(
literal|"shard1"
argument_list|)
expr_stmt|;
name|testFinished
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|testFinished
condition|)
block|{
name|printLayoutOnTearDown
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
DECL|method|testUpdateAndDelete
specifier|private
name|long
name|testUpdateAndDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|docId
init|=
literal|99999999L
decl_stmt|;
name|indexr
argument_list|(
literal|"id"
argument_list|,
name|docId
argument_list|,
name|t1
argument_list|,
literal|"originalcontent"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
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
name|add
argument_list|(
literal|"q"
argument_list|,
name|t1
operator|+
literal|":originalcontent"
argument_list|)
expr_stmt|;
name|QueryResponse
name|results
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|query
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// update doc
name|indexr
argument_list|(
literal|"id"
argument_list|,
name|docId
argument_list|,
name|t1
argument_list|,
literal|"updatedcontent"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|results
operator|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|results
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"q"
argument_list|,
name|t1
operator|+
literal|":updatedcontent"
argument_list|)
expr_stmt|;
name|results
operator|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|UpdateRequest
name|uReq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
comment|// uReq.setParam(UpdateParams.UPDATE_CHAIN, DISTRIB_UPDATE_CHAIN);
name|uReq
operator|.
name|deleteById
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|docId
argument_list|)
argument_list|)
operator|.
name|process
argument_list|(
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|results
operator|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|results
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|docId
return|;
block|}
DECL|method|brindDownShardIndexSomeDocsAndRecover
specifier|private
name|void
name|brindDownShardIndexSomeDocsAndRecover
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|long
name|deadShardCount
init|=
name|shardToJetty
operator|.
name|get
argument_list|(
name|SHARD2
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
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"n_tl1 desc"
argument_list|)
expr_stmt|;
comment|// kill a shard
name|CloudJettyRunner
name|deadShard
init|=
name|chaosMonkey
operator|.
name|stopShard
argument_list|(
name|SHARD2
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|cloudClient
operator|.
name|connect
argument_list|()
expr_stmt|;
comment|// we are careful to make sure the downed node is no longer in the state,
comment|// because on some systems (especially freebsd w/ blackhole enabled), trying
comment|// to talk to a downed node causes grief
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
name|SHARD2
argument_list|)
argument_list|)
expr_stmt|;
name|jetties
operator|.
name|remove
argument_list|(
name|deadShard
argument_list|)
expr_stmt|;
for|for
control|(
name|CloudJettyRunner
name|cjetty
range|:
name|jetties
control|)
block|{
name|waitToSeeNotLive
argument_list|(
operator|(
operator|(
name|SolrDispatchFilter
operator|)
name|cjetty
operator|.
name|jetty
operator|.
name|getDispatchFilter
argument_list|()
operator|.
name|getFilter
argument_list|()
operator|)
operator|.
name|getCores
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|deadShard
argument_list|)
expr_stmt|;
block|}
name|waitToSeeNotLive
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|deadShard
argument_list|)
expr_stmt|;
comment|// ensure shard is dead
try|try
block|{
name|index_specific
argument_list|(
name|deadShard
operator|.
name|client
operator|.
name|solrClient
argument_list|,
name|id
argument_list|,
literal|999
argument_list|,
name|i1
argument_list|,
literal|107
argument_list|,
name|t1
argument_list|,
literal|"specific doc!"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"This server should be down and this update should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
comment|// expected..
block|}
name|commit
argument_list|()
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"n_tl1 desc"
argument_list|)
expr_stmt|;
comment|// long cloudClientDocs = cloudClient.query(new
comment|// SolrQuery("*:*")).getResults().getNumFound();
comment|// System.out.println("clouddocs:" + cloudClientDocs);
comment|// try to index to a living shard at shard2
name|long
name|numFound1
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
name|index_specific
argument_list|(
name|shardToJetty
operator|.
name|get
argument_list|(
name|SHARD2
argument_list|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|client
operator|.
name|solrClient
argument_list|,
name|id
argument_list|,
literal|1000
argument_list|,
name|i1
argument_list|,
literal|108
argument_list|,
name|t1
argument_list|,
literal|"specific doc!"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|checkShardConsistency
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"n_tl1 desc"
argument_list|)
expr_stmt|;
comment|// try adding a doc with CloudSolrServer
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|DEFAULT_COLLECTION
argument_list|)
expr_stmt|;
name|long
name|numFound2
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
name|numFound1
operator|+
literal|1
argument_list|,
name|numFound2
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|1001
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
comment|// ureq.setParam("update.chain", DISTRIB_UPDATE_CHAIN);
name|ureq
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"n_tl1 desc"
argument_list|)
expr_stmt|;
name|long
name|numFound3
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
comment|// lets just check that the one doc since last commit made it in...
name|assertEquals
argument_list|(
name|numFound2
operator|+
literal|1
argument_list|,
name|numFound3
argument_list|)
expr_stmt|;
comment|// test debugging
name|testDebugQueries
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|controlClient
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
argument_list|)
expr_stmt|;
for|for
control|(
name|SolrServer
name|client
range|:
name|clients
control|)
block|{
try|try
block|{
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|q
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|client
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                    }
block|}
block|}
comment|// TODO: This test currently fails because debug info is obtained only
comment|// on shards with matches.
comment|// query("q","matchesnothing","fl","*,score", "debugQuery", "true");
comment|// this should trigger a recovery phase on deadShard
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|deadShard
operator|.
name|jetty
argument_list|)
expr_stmt|;
comment|// make sure we have published we are recovering
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|deadShardCount
operator|=
name|shardToJetty
operator|.
name|get
argument_list|(
name|SHARD2
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
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
comment|// if we properly recovered, we should now have the couple missing docs that
comment|// came in while shard was down
name|checkShardConsistency
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// recover over 100 docs so we do more than just peer sync (replicate recovery)
name|chaosMonkey
operator|.
name|stopJetty
argument_list|(
name|deadShard
argument_list|)
expr_stmt|;
for|for
control|(
name|CloudJettyRunner
name|cjetty
range|:
name|jetties
control|)
block|{
name|waitToSeeNotLive
argument_list|(
operator|(
operator|(
name|SolrDispatchFilter
operator|)
name|cjetty
operator|.
name|jetty
operator|.
name|getDispatchFilter
argument_list|()
operator|.
name|getFilter
argument_list|()
operator|)
operator|.
name|getCores
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|deadShard
argument_list|)
expr_stmt|;
block|}
name|waitToSeeNotLive
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|deadShard
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
literal|226
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|2000
operator|+
name|i
argument_list|)
expr_stmt|;
name|controlClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|ureq
operator|=
operator|new
name|UpdateRequest
argument_list|()
expr_stmt|;
name|ureq
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// ureq.setParam("update.chain", DISTRIB_UPDATE_CHAIN);
name|ureq
operator|.
name|process
argument_list|(
name|cloudClient
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
literal|1500
argument_list|)
expr_stmt|;
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|deadShard
operator|.
name|jetty
argument_list|)
expr_stmt|;
comment|// make sure we have published we are recovering
name|Thread
operator|.
name|sleep
argument_list|(
literal|1500
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
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|addNewReplica
specifier|private
name|void
name|addNewReplica
parameter_list|()
throws|throws
name|Exception
block|{
name|JettySolrRunner
name|newReplica
init|=
name|createJettys
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// new server should be part of first shard
comment|// how many docs are on the new shard?
for|for
control|(
name|CloudJettyRunner
name|cjetty
range|:
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"total:"
operator|+
name|cjetty
operator|.
name|client
operator|.
name|solrClient
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
argument_list|)
expr_stmt|;
block|}
name|checkShardConsistency
argument_list|(
literal|"shard1"
argument_list|)
expr_stmt|;
name|assertDocCounts
argument_list|(
name|VERBOSE
argument_list|)
expr_stmt|;
block|}
DECL|method|testDebugQueries
specifier|private
name|void
name|testDebugQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|handle
operator|.
name|put
argument_list|(
literal|"explain"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"time"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"now their fox sat had put"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"id:[1 TO 5]"
argument_list|,
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"id:[1 TO 5]"
argument_list|,
name|CommonParams
operator|.
name|DEBUG
argument_list|,
name|CommonParams
operator|.
name|TIMING
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"id:[1 TO 5]"
argument_list|,
name|CommonParams
operator|.
name|DEBUG
argument_list|,
name|CommonParams
operator|.
name|RESULTS
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"id:[1 TO 5]"
argument_list|,
name|CommonParams
operator|.
name|DEBUG
argument_list|,
name|CommonParams
operator|.
name|QUERY
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
