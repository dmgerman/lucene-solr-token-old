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
name|Collections
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
name|Random
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
name|concurrent
operator|.
name|TimeUnit
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
name|JSONTestUtil
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
operator|.
name|SuppressSSL
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
name|SolrClient
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|CoreAdminRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|CollectionAdminResponse
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
name|CoreAdminResponse
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
name|SolrDocumentList
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
name|ZkCoreNodeProps
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
begin_import
import|import static
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
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
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
name|update
operator|.
name|processor
operator|.
name|DistributedUpdateProcessor
operator|.
name|DISTRIB_FROM
import|;
end_import
begin_class
annotation|@
name|Slow
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|DistributedVersionInfoTest
specifier|public
class|class
name|DistributedVersionInfoTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
specifier|transient
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DistributedVersionInfoTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxWaitSecsToSeeAllActive
specifier|protected
specifier|static
specifier|final
name|int
name|maxWaitSecsToSeeAllActive
init|=
literal|30
decl_stmt|;
annotation|@
name|Test
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
literal|30000
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"DistributedVersionInfoTest RUNNING"
argument_list|)
expr_stmt|;
name|testReplicaVersionHandling
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"DistributedVersionInfoTest succeeded ... shutting down now!"
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplicaVersionHandling
specifier|protected
name|void
name|testReplicaVersionHandling
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|testCollectionName
init|=
literal|"c8n_vers_1x3"
decl_stmt|;
name|String
name|shardId
init|=
literal|"shard1"
decl_stmt|;
name|int
name|rf
init|=
literal|3
decl_stmt|;
name|createCollectionRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|1
argument_list|,
name|rf
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
specifier|final
name|Replica
name|leader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|notLeaders
init|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
name|shardId
argument_list|,
literal|1
argument_list|,
name|rf
argument_list|,
name|maxWaitSecsToSeeAllActive
argument_list|)
decl_stmt|;
name|sendDoc
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// verify doc is on the leader and replica
name|assertDocsExistInAllReplicas
argument_list|(
name|notLeaders
argument_list|,
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// get max version from the leader and replica
name|Replica
name|replica
init|=
name|notLeaders
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Long
name|maxOnLeader
init|=
name|getMaxVersionFromIndex
argument_list|(
name|leader
argument_list|)
decl_stmt|;
name|Long
name|maxOnReplica
init|=
name|getMaxVersionFromIndex
argument_list|(
name|replica
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"leader and replica should have same max version: "
operator|+
name|maxOnLeader
argument_list|,
name|maxOnLeader
argument_list|,
name|maxOnReplica
argument_list|)
expr_stmt|;
comment|// send the same doc but with a lower version than the max in the index
try|try
init|(
name|SolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|replica
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
init|)
block|{
name|String
name|docId
init|=
name|String
operator|.
name|valueOf
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|id
argument_list|,
name|docId
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"_version_"
argument_list|,
name|maxOnReplica
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// bad version!!!
comment|// simulate what the leader does when sending a doc to a replica
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
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|DistributedUpdateProcessor
operator|.
name|DistribPhase
operator|.
name|FROMLEADER
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|DISTRIB_FROM
argument_list|,
name|leader
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
expr_stmt|;
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|req
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sending doc with out-of-date version ("
operator|+
operator|(
name|maxOnReplica
operator|-
literal|1
operator|)
operator|+
literal|") document directly to replica"
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Long
name|docVersion
init|=
name|getVersionFromIndex
argument_list|(
name|replica
argument_list|,
name|docId
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"older version should have been thrown away"
argument_list|,
name|maxOnReplica
argument_list|,
name|docVersion
argument_list|)
expr_stmt|;
block|}
name|reloadCollection
argument_list|(
name|leader
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
name|maxOnLeader
operator|=
name|getMaxVersionFromIndex
argument_list|(
name|leader
argument_list|)
expr_stmt|;
name|maxOnReplica
operator|=
name|getMaxVersionFromIndex
argument_list|(
name|replica
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"leader and replica should have same max version after reload"
argument_list|,
name|maxOnLeader
argument_list|,
name|maxOnReplica
argument_list|)
expr_stmt|;
comment|// now start sending docs while collection is reloading
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|deletedDocs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|docsSent
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
literal|5150
argument_list|)
decl_stmt|;
name|Thread
name|docSenderThread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// brief delay before sending docs
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
operator|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|+
literal|1
operator|)
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
name|int
name|docId
init|=
name|i
operator|+
literal|1
decl_stmt|;
try|try
block|{
name|sendDoc
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|docsSent
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
block|}
block|}
block|}
decl_stmt|;
name|Thread
name|reloaderThread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|300
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|reloadCollection
argument_list|(
name|leader
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|300
argument_list|)
operator|+
literal|300
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
block|}
block|}
decl_stmt|;
name|Thread
name|deleteThread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// brief delay before sending docs
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|200
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
name|int
name|docToDelete
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|docsSent
operator|.
name|get
argument_list|()
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
operator|!
name|deletedDocs
operator|.
name|contains
argument_list|(
name|docToDelete
argument_list|)
condition|)
block|{
name|delI
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|docToDelete
argument_list|)
argument_list|)
expr_stmt|;
name|deletedDocs
operator|.
name|add
argument_list|(
name|docToDelete
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|Thread
name|committerThread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|200
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|+
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
block|}
block|}
decl_stmt|;
name|docSenderThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|reloaderThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|committerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|deleteThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|docSenderThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|reloaderThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|committerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|deleteThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Total of "
operator|+
name|deletedDocs
operator|.
name|size
argument_list|()
operator|+
literal|" docs deleted"
argument_list|)
expr_stmt|;
name|maxOnLeader
operator|=
name|getMaxVersionFromIndex
argument_list|(
name|leader
argument_list|)
expr_stmt|;
name|maxOnReplica
operator|=
name|getMaxVersionFromIndex
argument_list|(
name|replica
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"leader and replica should have same max version before reload"
argument_list|,
name|maxOnLeader
argument_list|,
name|maxOnReplica
argument_list|)
expr_stmt|;
name|reloadCollection
argument_list|(
name|leader
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
name|maxOnLeader
operator|=
name|getMaxVersionFromIndex
argument_list|(
name|leader
argument_list|)
expr_stmt|;
name|maxOnReplica
operator|=
name|getMaxVersionFromIndex
argument_list|(
name|replica
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"leader and replica should have same max version after reload"
argument_list|,
name|maxOnLeader
argument_list|,
name|maxOnReplica
argument_list|)
expr_stmt|;
name|assertDocsExistInAllReplicas
argument_list|(
name|notLeaders
argument_list|,
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|,
name|deletedDocs
argument_list|)
expr_stmt|;
comment|// try to clean up
try|try
block|{
name|CollectionAdminRequest
operator|.
name|Delete
name|req
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|testCollectionName
argument_list|)
decl_stmt|;
name|req
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// don't fail the test
name|log
operator|.
name|warn
argument_list|(
literal|"Could not delete collection {} after test completed"
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getMaxVersionFromIndex
specifier|protected
name|long
name|getMaxVersionFromIndex
parameter_list|(
name|Replica
name|replica
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
return|return
name|getVersionFromIndex
argument_list|(
name|replica
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getVersionFromIndex
specifier|protected
name|long
name|getVersionFromIndex
parameter_list|(
name|Replica
name|replica
parameter_list|,
name|String
name|docId
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|Long
name|vers
init|=
literal|null
decl_stmt|;
name|String
name|queryStr
init|=
operator|(
name|docId
operator|!=
literal|null
operator|)
condition|?
literal|"id:"
operator|+
name|docId
else|:
literal|"_version_:[0 TO *]"
decl_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
name|queryStr
argument_list|)
decl_stmt|;
name|query
operator|.
name|setRows
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|query
operator|.
name|setFields
argument_list|(
literal|"id"
argument_list|,
literal|"_version_"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addSort
argument_list|(
operator|new
name|SolrQuery
operator|.
name|SortClause
argument_list|(
literal|"_version_"
argument_list|,
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParam
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|SolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|replica
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
init|)
block|{
name|QueryResponse
name|qr
init|=
name|client
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|hits
init|=
name|qr
operator|.
name|getResults
argument_list|()
decl_stmt|;
if|if
condition|(
name|hits
operator|.
name|isEmpty
argument_list|()
condition|)
name|fail
argument_list|(
literal|"No results returned from query: "
operator|+
name|query
argument_list|)
expr_stmt|;
name|vers
operator|=
operator|(
name|Long
operator|)
name|hits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstValue
argument_list|(
literal|"_version_"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|vers
operator|==
literal|null
condition|)
name|fail
argument_list|(
literal|"Failed to get version using query "
operator|+
name|query
operator|+
literal|" from "
operator|+
name|replica
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|vers
operator|.
name|longValue
argument_list|()
return|;
block|}
DECL|method|createCollectionRetry
specifier|private
name|void
name|createCollectionRetry
parameter_list|(
name|String
name|testCollectionName
parameter_list|,
name|int
name|numShards
parameter_list|,
name|int
name|replicationFactor
parameter_list|,
name|int
name|maxShardsPerNode
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|CollectionAdminResponse
name|resp
init|=
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
name|numShards
argument_list|,
name|replicationFactor
argument_list|,
name|maxShardsPerNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|resp
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"failure"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|CollectionAdminRequest
operator|.
name|Delete
name|req
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCollectionName
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|req
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|resp
operator|=
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
name|numShards
argument_list|,
name|replicationFactor
argument_list|,
name|maxShardsPerNode
argument_list|)
expr_stmt|;
if|if
condition|(
name|resp
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"failure"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Could not create "
operator|+
name|testCollectionName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertDocsExistInAllReplicas
specifier|protected
name|void
name|assertDocsExistInAllReplicas
parameter_list|(
name|List
argument_list|<
name|Replica
argument_list|>
name|notLeaders
parameter_list|,
name|String
name|testCollectionName
parameter_list|,
name|int
name|firstDocId
parameter_list|,
name|int
name|lastDocId
parameter_list|,
name|Set
argument_list|<
name|Integer
argument_list|>
name|deletedDocs
parameter_list|)
throws|throws
name|Exception
block|{
name|Replica
name|leader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|HttpSolrClient
name|leaderSolr
init|=
name|getHttpSolrClient
argument_list|(
name|leader
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|HttpSolrClient
argument_list|>
name|replicas
init|=
operator|new
name|ArrayList
argument_list|<
name|HttpSolrClient
argument_list|>
argument_list|(
name|notLeaders
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Replica
name|r
range|:
name|notLeaders
control|)
name|replicas
operator|.
name|add
argument_list|(
name|getHttpSolrClient
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|int
name|d
init|=
name|firstDocId
init|;
name|d
operator|<=
name|lastDocId
condition|;
name|d
operator|++
control|)
block|{
if|if
condition|(
name|deletedDocs
operator|!=
literal|null
operator|&&
name|deletedDocs
operator|.
name|contains
argument_list|(
name|d
argument_list|)
condition|)
continue|continue;
name|String
name|docId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|d
argument_list|)
decl_stmt|;
name|Long
name|leaderVers
init|=
name|assertDocExists
argument_list|(
name|leaderSolr
argument_list|,
name|testCollectionName
argument_list|,
name|docId
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|HttpSolrClient
name|replicaSolr
range|:
name|replicas
control|)
name|assertDocExists
argument_list|(
name|replicaSolr
argument_list|,
name|testCollectionName
argument_list|,
name|docId
argument_list|,
name|leaderVers
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|leaderSolr
operator|!=
literal|null
condition|)
block|{
name|leaderSolr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|HttpSolrClient
name|replicaSolr
range|:
name|replicas
control|)
block|{
name|replicaSolr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getHttpSolrClient
specifier|protected
name|HttpSolrClient
name|getHttpSolrClient
parameter_list|(
name|Replica
name|replica
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|HttpSolrClient
argument_list|(
name|replica
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
return|;
block|}
DECL|method|sendDoc
specifier|protected
name|void
name|sendDoc
parameter_list|(
name|int
name|docId
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
name|doc
operator|.
name|addField
argument_list|(
name|id
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|docId
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"a_t"
argument_list|,
literal|"hello"
operator|+
name|docId
argument_list|)
expr_stmt|;
name|sendDocsWithRetry
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|doc
argument_list|)
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
comment|/**    * Query the real-time get handler for a specific doc by ID to verify it    * exists in the provided server, using distrib=false so it doesn't route to another replica.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|assertDocExists
specifier|protected
name|Long
name|assertDocExists
parameter_list|(
name|HttpSolrClient
name|solr
parameter_list|,
name|String
name|coll
parameter_list|,
name|String
name|docId
parameter_list|,
name|Long
name|expVers
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryRequest
name|qr
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"id"
argument_list|,
name|docId
argument_list|,
literal|"distrib"
argument_list|,
literal|"false"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,_version_"
argument_list|)
argument_list|)
decl_stmt|;
name|NamedList
name|rsp
init|=
name|solr
operator|.
name|request
argument_list|(
name|qr
argument_list|)
decl_stmt|;
name|SolrDocument
name|doc
init|=
operator|(
name|SolrDocument
operator|)
name|rsp
operator|.
name|get
argument_list|(
literal|"doc"
argument_list|)
decl_stmt|;
name|String
name|match
init|=
name|JSONTestUtil
operator|.
name|matchObj
argument_list|(
literal|"/id"
argument_list|,
name|doc
argument_list|,
operator|new
name|Integer
argument_list|(
name|docId
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Doc with id="
operator|+
name|docId
operator|+
literal|" not found in "
operator|+
name|solr
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|" due to: "
operator|+
name|match
operator|+
literal|"; rsp="
operator|+
name|rsp
argument_list|,
name|match
operator|==
literal|null
argument_list|)
expr_stmt|;
name|Long
name|vers
init|=
operator|(
name|Long
operator|)
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"_version_"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|vers
argument_list|)
expr_stmt|;
if|if
condition|(
name|expVers
operator|!=
literal|null
condition|)
name|assertEquals
argument_list|(
literal|"expected version of doc "
operator|+
name|docId
operator|+
literal|" to be "
operator|+
name|expVers
argument_list|,
name|expVers
argument_list|,
name|vers
argument_list|)
expr_stmt|;
return|return
name|vers
return|;
block|}
DECL|method|reloadCollection
specifier|protected
name|boolean
name|reloadCollection
parameter_list|(
name|Replica
name|replica
parameter_list|,
name|String
name|testCollectionName
parameter_list|)
throws|throws
name|Exception
block|{
name|ZkCoreNodeProps
name|coreProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|replica
argument_list|)
decl_stmt|;
name|String
name|coreName
init|=
name|coreProps
operator|.
name|getCoreName
argument_list|()
decl_stmt|;
name|boolean
name|reloadedOk
init|=
literal|false
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|coreProps
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
init|)
block|{
name|CoreAdminResponse
name|statusResp
init|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
name|coreName
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|long
name|leaderCoreStartTime
init|=
name|statusResp
operator|.
name|getStartTime
argument_list|(
name|coreName
argument_list|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// send reload command for the collection
name|log
operator|.
name|info
argument_list|(
literal|"Sending RELOAD command for "
operator|+
name|testCollectionName
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
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|RELOAD
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"name"
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
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
name|log
operator|.
name|info
argument_list|(
literal|"Sending reload command to "
operator|+
name|testCollectionName
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// reload can take a short while
comment|// verify reload is done, waiting up to 30 seconds for slow test environments
name|long
name|timeout
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
condition|)
block|{
name|statusResp
operator|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
name|coreName
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|long
name|startTimeAfterReload
init|=
name|statusResp
operator|.
name|getStartTime
argument_list|(
name|coreName
argument_list|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|startTimeAfterReload
operator|>
name|leaderCoreStartTime
condition|)
block|{
name|reloadedOk
operator|=
literal|true
expr_stmt|;
break|break;
block|}
comment|// else ... still waiting to see the reloaded core report a later start time
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|reloadedOk
return|;
block|}
block|}
end_class
end_unit
