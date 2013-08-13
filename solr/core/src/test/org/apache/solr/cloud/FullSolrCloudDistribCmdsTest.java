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
name|impl
operator|.
name|ConcurrentUpdateSolrServer
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
name|SolrDocument
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
name|update
operator|.
name|VersionInfo
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
name|update
operator|.
name|processor
operator|.
name|DistributedUpdateProcessor
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
name|CreateMode
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
comment|/**  * Super basic testing, no shard restarting or anything.  */
end_comment
begin_class
annotation|@
name|Slow
DECL|class|FullSolrCloudDistribCmdsTest
specifier|public
class|class
name|FullSolrCloudDistribCmdsTest
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
block|{   }
DECL|method|FullSolrCloudDistribCmdsTest
specifier|public
name|FullSolrCloudDistribCmdsTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|shardCount
operator|=
literal|4
expr_stmt|;
name|sliceCount
operator|=
literal|2
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
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// add a doc, update it, and delete it
name|QueryResponse
name|results
decl_stmt|;
name|UpdateRequest
name|uReq
decl_stmt|;
name|long
name|docId
init|=
name|addUpdateDelete
argument_list|()
decl_stmt|;
comment|// add 2 docs in a request
name|SolrInputDocument
name|doc1
decl_stmt|;
name|SolrInputDocument
name|doc2
decl_stmt|;
name|docId
operator|=
name|addTwoDocsInOneRequest
argument_list|(
name|docId
argument_list|)
expr_stmt|;
comment|// two deletes
name|uReq
operator|=
operator|new
name|UpdateRequest
argument_list|()
expr_stmt|;
name|uReq
operator|.
name|deleteById
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|docId
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|deleteById
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|docId
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|controlClient
operator|.
name|deleteById
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|docId
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|controlClient
operator|.
name|deleteById
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|docId
operator|-
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|results
operator|=
name|query
argument_list|(
name|cloudClient
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
name|results
operator|=
name|query
argument_list|(
name|controlClient
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
comment|// add two docs together, a 3rd doc and a delete
name|indexr
argument_list|(
literal|"id"
argument_list|,
name|docId
operator|++
argument_list|,
name|t1
argument_list|,
literal|"originalcontent"
argument_list|)
expr_stmt|;
name|uReq
operator|=
operator|new
name|UpdateRequest
argument_list|()
expr_stmt|;
name|doc1
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|addFields
argument_list|(
name|doc1
argument_list|,
literal|"id"
argument_list|,
name|docId
operator|++
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|doc2
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|addFields
argument_list|(
name|doc2
argument_list|,
literal|"id"
argument_list|,
name|docId
operator|++
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|process
argument_list|(
name|controlClient
argument_list|)
expr_stmt|;
name|uReq
operator|=
operator|new
name|UpdateRequest
argument_list|()
expr_stmt|;
name|uReq
operator|.
name|deleteById
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|docId
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|controlClient
operator|.
name|deleteById
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|docId
operator|-
literal|2
argument_list|)
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
name|checkShardConsistency
argument_list|()
expr_stmt|;
name|results
operator|=
name|query
argument_list|(
name|controlClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
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
name|results
operator|=
name|query
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
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
name|docId
operator|=
name|testIndexQueryDeleteHierarchical
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|testIndexingWithSuss
argument_list|()
expr_stmt|;
comment|// TODO: testOptimisticUpdate(results);
name|testDeleteByQueryDistrib
argument_list|()
expr_stmt|;
name|testThatCantForwardToLeaderFails
argument_list|()
expr_stmt|;
block|}
DECL|method|testThatCantForwardToLeaderFails
specifier|private
name|void
name|testThatCantForwardToLeaderFails
parameter_list|()
throws|throws
name|Exception
block|{
name|ZkStateReader
name|zkStateReader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ZkNodeProps
name|props
init|=
name|zkStateReader
operator|.
name|getLeaderRetry
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
name|chaosMonkey
operator|.
name|stopShard
argument_list|(
literal|"shard1"
argument_list|)
expr_stmt|;
comment|// fake that the leader is still advertised
name|String
name|leaderPath
init|=
name|ZkStateReader
operator|.
name|getShardLeadersPath
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|int
name|fails
init|=
literal|0
decl_stmt|;
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|leaderPath
argument_list|,
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|props
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|EPHEMERAL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|200
init|;
name|i
operator|<
literal|210
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|index_specific
argument_list|(
name|cloudClient
argument_list|,
name|id
argument_list|,
name|i
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
name|fails
operator|++
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
comment|// expected
name|fails
operator|++
expr_stmt|;
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"A whole shard is down - some of these should fail"
argument_list|,
name|fails
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|addTwoDocsInOneRequest
specifier|private
name|long
name|addTwoDocsInOneRequest
parameter_list|(
name|long
name|docId
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryResponse
name|results
decl_stmt|;
name|UpdateRequest
name|uReq
decl_stmt|;
name|uReq
operator|=
operator|new
name|UpdateRequest
argument_list|()
expr_stmt|;
comment|//uReq.setParam(UpdateParams.UPDATE_CHAIN, DISTRIB_UPDATE_CHAIN);
name|SolrInputDocument
name|doc1
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc1
argument_list|,
literal|"id"
argument_list|,
name|docId
operator|++
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc2
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc2
argument_list|,
literal|"id"
argument_list|,
name|docId
operator|++
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|process
argument_list|(
name|controlClient
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|checkShardConsistency
argument_list|()
expr_stmt|;
name|assertDocCounts
argument_list|(
name|VERBOSE
argument_list|)
expr_stmt|;
name|results
operator|=
name|query
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
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
DECL|method|addUpdateDelete
specifier|private
name|long
name|addUpdateDelete
parameter_list|()
throws|throws
name|Exception
throws|,
name|IOException
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
name|assertDocCounts
argument_list|(
name|VERBOSE
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
comment|//uReq.setParam(UpdateParams.UPDATE_CHAIN, DISTRIB_UPDATE_CHAIN);
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
DECL|method|testDeleteByQueryDistrib
specifier|private
name|void
name|testDeleteByQueryDistrib
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|query
argument_list|(
name|cloudClient
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
DECL|method|testIndexQueryDeleteHierarchical
specifier|private
name|long
name|testIndexQueryDeleteHierarchical
parameter_list|(
name|long
name|docId
parameter_list|)
throws|throws
name|Exception
block|{
comment|//index
name|int
name|topDocsNum
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|int
name|childsNum
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|topDocsNum
condition|;
operator|++
name|i
control|)
block|{
name|UpdateRequest
name|uReq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|topDocument
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|topDocument
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|docId
operator|++
argument_list|)
expr_stmt|;
name|topDocument
operator|.
name|addField
argument_list|(
literal|"type_s"
argument_list|,
literal|"parent"
argument_list|)
expr_stmt|;
name|topDocument
operator|.
name|addField
argument_list|(
name|i
operator|+
literal|"parent_f1_s"
argument_list|,
literal|"v1"
argument_list|)
expr_stmt|;
name|topDocument
operator|.
name|addField
argument_list|(
name|i
operator|+
literal|"parent_f2_s"
argument_list|,
literal|"v2"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|childsNum
condition|;
operator|++
name|index
control|)
block|{
name|docId
operator|=
name|addChildren
argument_list|(
literal|"child"
argument_list|,
name|topDocument
argument_list|,
name|index
argument_list|,
literal|false
argument_list|,
name|docId
argument_list|)
expr_stmt|;
block|}
name|uReq
operator|.
name|add
argument_list|(
name|topDocument
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|process
argument_list|(
name|controlClient
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|checkShardConsistency
argument_list|()
expr_stmt|;
name|assertDocCounts
argument_list|(
name|VERBOSE
argument_list|)
expr_stmt|;
comment|//query
comment|// parents
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"type_s:parent"
argument_list|)
decl_stmt|;
name|QueryResponse
name|results
init|=
name|cloudClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|topDocsNum
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
comment|//childs
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"type_s:child"
argument_list|)
expr_stmt|;
name|results
operator|=
name|cloudClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|topDocsNum
operator|*
name|childsNum
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
comment|//grandchilds
name|query
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"type_s:grand"
argument_list|)
expr_stmt|;
name|results
operator|=
name|cloudClient
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
comment|//each topDoc has t childs where each child has x = 0 + 2 + 4 + ..(t-1)*2 grands
comment|//x = 2 * (1 + 2 + 3 +.. (t-1)) => arithmetic summ of t-1
comment|//x = 2 * ((t-1) * t / 2) = t * (t - 1)
name|assertEquals
argument_list|(
name|topDocsNum
operator|*
name|childsNum
operator|*
operator|(
name|childsNum
operator|-
literal|1
operator|)
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
comment|//delete
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
return|return
name|docId
return|;
block|}
DECL|method|addChildren
specifier|private
name|long
name|addChildren
parameter_list|(
name|String
name|prefix
parameter_list|,
name|SolrInputDocument
name|topDocument
parameter_list|,
name|int
name|childIndex
parameter_list|,
name|boolean
name|lastLevel
parameter_list|,
name|long
name|docId
parameter_list|)
block|{
name|SolrInputDocument
name|childDocument
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|childDocument
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|docId
operator|++
argument_list|)
expr_stmt|;
name|childDocument
operator|.
name|addField
argument_list|(
literal|"type_s"
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|childIndex
condition|;
operator|++
name|index
control|)
block|{
name|childDocument
operator|.
name|addField
argument_list|(
name|childIndex
operator|+
name|prefix
operator|+
name|index
operator|+
literal|"_s"
argument_list|,
name|childIndex
operator|+
literal|"value"
operator|+
name|index
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|lastLevel
condition|)
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
name|childIndex
operator|*
literal|2
condition|;
operator|++
name|i
control|)
block|{
name|docId
operator|=
name|addChildren
argument_list|(
literal|"grand"
argument_list|,
name|childDocument
argument_list|,
name|i
argument_list|,
literal|true
argument_list|,
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
name|topDocument
operator|.
name|addChildDocument
argument_list|(
name|childDocument
argument_list|)
expr_stmt|;
return|return
name|docId
return|;
block|}
DECL|method|testIndexingWithSuss
specifier|private
name|void
name|testIndexingWithSuss
parameter_list|()
throws|throws
name|Exception
block|{
name|ConcurrentUpdateSolrServer
name|suss
init|=
operator|new
name|ConcurrentUpdateSolrServer
argument_list|(
operator|(
operator|(
name|HttpSolrServer
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getBaseURL
argument_list|()
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|suss
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|suss
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|100
init|;
name|i
operator|<
literal|150
condition|;
name|i
operator|++
control|)
block|{
name|index_specific
argument_list|(
name|suss
argument_list|,
name|id
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|suss
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|checkShardConsistency
argument_list|()
expr_stmt|;
block|}
DECL|method|testOptimisticUpdate
specifier|private
name|void
name|testOptimisticUpdate
parameter_list|(
name|QueryResponse
name|results
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrDocument
name|doc
init|=
name|results
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Long
name|version
init|=
operator|(
name|Long
operator|)
name|doc
operator|.
name|getFieldValue
argument_list|(
name|VersionInfo
operator|.
name|VERSION_FIELD
argument_list|)
decl_stmt|;
name|Integer
name|theDoc
init|=
operator|(
name|Integer
operator|)
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|UpdateRequest
name|uReq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc1
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|uReq
operator|.
name|setParams
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|getParams
argument_list|()
operator|.
name|set
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|VERSION_FIELD
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|version
argument_list|)
argument_list|)
expr_stmt|;
name|addFields
argument_list|(
name|doc1
argument_list|,
literal|"id"
argument_list|,
name|theDoc
argument_list|,
name|t1
argument_list|,
literal|"theupdatestuff"
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|process
argument_list|(
name|controlClient
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
comment|// updating the old version should fail...
name|SolrInputDocument
name|doc2
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|uReq
operator|=
operator|new
name|UpdateRequest
argument_list|()
expr_stmt|;
name|uReq
operator|.
name|setParams
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|getParams
argument_list|()
operator|.
name|set
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|VERSION_FIELD
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|version
argument_list|)
argument_list|)
expr_stmt|;
name|addFields
argument_list|(
name|doc2
argument_list|,
literal|"id"
argument_list|,
name|theDoc
argument_list|,
name|t1
argument_list|,
literal|"thenewupdatestuff"
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|uReq
operator|.
name|process
argument_list|(
name|controlClient
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
literal|":thenewupdatestuff"
argument_list|)
expr_stmt|;
name|QueryResponse
name|res
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
literal|0
argument_list|,
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
name|t1
operator|+
literal|":theupdatestuff"
argument_list|)
expr_stmt|;
name|res
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
name|res
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|query
specifier|private
name|QueryResponse
name|query
parameter_list|(
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
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
return|return
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
return|;
block|}
annotation|@
name|Override
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
block|}
end_class
end_unit
