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
name|update
operator|.
name|processor
operator|.
name|CdcrUpdateProcessor
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
begin_class
DECL|class|CdcrVersionReplicationTest
specifier|public
class|class
name|CdcrVersionReplicationTest
extends|extends
name|BaseCdcrDistributedZkTest
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
DECL|field|vfield
specifier|private
specifier|static
specifier|final
name|String
name|vfield
init|=
name|DistributedUpdateProcessor
operator|.
name|VERSION_FIELD
decl_stmt|;
DECL|field|solrServer
name|SolrClient
name|solrServer
decl_stmt|;
DECL|method|CdcrVersionReplicationTest
specifier|public
name|CdcrVersionReplicationTest
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
name|super
operator|.
name|createTargetCollection
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|createClientRandomly
name|SolrClient
name|createClientRandomly
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|r
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|// testing the smart cloud client (requests to leaders) is more important than testing the forwarding logic
if|if
condition|(
name|r
operator|<
literal|80
condition|)
block|{
return|return
name|createCloudClient
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
return|;
block|}
if|if
condition|(
name|r
operator|<
literal|90
condition|)
block|{
return|return
name|createNewSolrServer
argument_list|(
name|shardToJetty
operator|.
name|get
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
operator|.
name|get
argument_list|(
name|SHARD1
argument_list|)
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|url
argument_list|)
return|;
block|}
return|return
name|createNewSolrServer
argument_list|(
name|shardToJetty
operator|.
name|get
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
operator|.
name|get
argument_list|(
name|SHARD2
argument_list|)
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|url
argument_list|)
return|;
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
DECL|method|testCdcrDocVersions
specifier|public
name|void
name|testCdcrDocVersions
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrClient
name|client
init|=
name|createClientRandomly
argument_list|()
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
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|doTestCdcrDocVersions
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
comment|// work arround SOLR-5628
block|}
finally|finally
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doTestCdcrDocVersions
specifier|private
name|void
name|doTestCdcrDocVersions
parameter_list|(
name|SolrClient
name|solrClient
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|solrServer
operator|=
name|solrClient
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"### STARTING doCdcrTestDocVersions - Add commands, client: "
operator|+
name|solrClient
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc1"
argument_list|,
literal|10
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc2"
argument_list|,
literal|11
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"11"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc3"
argument_list|,
literal|10
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc4"
argument_list|,
literal|11
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"11"
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
comment|// versions are preserved and verifiable both by query and by real-time get
name|doQuery
argument_list|(
name|solrClient
argument_list|,
literal|"doc1,10,doc2,11,doc3,10,doc4,11"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doRealTimeGet
argument_list|(
literal|"doc1,doc2,doc3,doc4"
argument_list|,
literal|"10,11,10,11"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc1"
argument_list|,
literal|5
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc2"
argument_list|,
literal|10
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc3"
argument_list|,
literal|9
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"9"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc4"
argument_list|,
literal|8
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"8"
argument_list|)
expr_stmt|;
comment|// lower versions are ignored
name|doRealTimeGet
argument_list|(
literal|"doc1,doc2,doc3,doc4"
argument_list|,
literal|"10,11,10,11"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc1"
argument_list|,
literal|12
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"12"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc2"
argument_list|,
literal|12
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"12"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc3"
argument_list|,
literal|12
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"12"
argument_list|)
expr_stmt|;
name|vadd
argument_list|(
literal|"doc4"
argument_list|,
literal|12
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"12"
argument_list|)
expr_stmt|;
comment|// higher versions are accepted
name|doRealTimeGet
argument_list|(
literal|"doc1,doc2,doc3,doc4"
argument_list|,
literal|"12,12,12,12"
argument_list|)
expr_stmt|;
comment|// non-cdcr update requests throw a version conflict exception for non-equal versions (optimistic locking feature)
name|vaddFail
argument_list|(
literal|"doc1"
argument_list|,
literal|13
argument_list|,
literal|409
argument_list|)
expr_stmt|;
name|vaddFail
argument_list|(
literal|"doc2"
argument_list|,
literal|13
argument_list|,
literal|409
argument_list|)
expr_stmt|;
name|vaddFail
argument_list|(
literal|"doc3"
argument_list|,
literal|13
argument_list|,
literal|409
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
comment|// versions are still as they were
name|doQuery
argument_list|(
name|solrClient
argument_list|,
literal|"doc1,12,doc2,12,doc3,12,doc4,12"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// query all shard replicas individually
name|doQueryShardReplica
argument_list|(
name|SHARD1
argument_list|,
literal|"doc1,12,doc2,12,doc3,12,doc4,12"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doQueryShardReplica
argument_list|(
name|SHARD2
argument_list|,
literal|"doc1,12,doc2,12,doc3,12,doc4,12"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// optimistic locking update
name|vadd
argument_list|(
literal|"doc4"
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|solrClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"ids"
argument_list|,
literal|"doc4"
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|version
init|=
operator|(
name|long
operator|)
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|vfield
argument_list|)
decl_stmt|;
comment|// update accepted and a new version number was generated
name|assertTrue
argument_list|(
name|version
operator|>
literal|1_000_000_000_000l
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"### STARTING doCdcrTestDocVersions - Delete commands"
argument_list|)
expr_stmt|;
comment|// send a delete update with an older version number
name|vdelete
argument_list|(
literal|"doc1"
argument_list|,
literal|5
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
comment|// must ignore the delete
name|doRealTimeGet
argument_list|(
literal|"doc1"
argument_list|,
literal|"12"
argument_list|)
expr_stmt|;
comment|// send a delete update with a higher version number
name|vdelete
argument_list|(
literal|"doc1"
argument_list|,
literal|13
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|"13"
argument_list|)
expr_stmt|;
comment|// must be deleted
name|doRealTimeGet
argument_list|(
literal|"doc1"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// send a delete update with a higher version number
name|vdelete
argument_list|(
literal|"doc4"
argument_list|,
name|version
operator|+
literal|1
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
literal|""
operator|+
operator|(
name|version
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
comment|// must be deleted
name|doRealTimeGet
argument_list|(
literal|"doc4"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
comment|// query each shard replica individually
name|doQueryShardReplica
argument_list|(
name|SHARD1
argument_list|,
literal|"doc2,12,doc3,12"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doQueryShardReplica
argument_list|(
name|SHARD2
argument_list|,
literal|"doc2,12,doc3,12"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// version conflict thanks to optimistic locking
if|if
condition|(
name|solrClient
operator|instanceof
name|CloudSolrClient
condition|)
comment|// TODO: it seems that optimistic locking doesn't work with forwarding, test with shard2 client
name|vdeleteFail
argument_list|(
literal|"doc2"
argument_list|,
literal|50
argument_list|,
literal|409
argument_list|)
expr_stmt|;
comment|// cleanup after ourselves for the next run
comment|// deleteByQuery should work as usual with the CDCR_UPDATE param
name|doDeleteByQuery
argument_list|(
literal|"id:doc*"
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
comment|// deleteByQuery with a version lower than anything else should have no effect
name|doQuery
argument_list|(
name|solrClient
argument_list|,
literal|"doc2,12,doc3,12"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doDeleteByQuery
argument_list|(
literal|"id:doc*"
argument_list|,
name|CdcrUpdateProcessor
operator|.
name|CDCR_UPDATE
argument_list|,
literal|""
argument_list|,
name|vfield
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
literal|51
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
expr_stmt|;
comment|// deleteByQuery with a version higher than everything else should delete all remaining docs
name|doQuery
argument_list|(
name|solrClient
argument_list|,
literal|""
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// check that replicas are as expected too
name|doQueryShardReplica
argument_list|(
name|SHARD1
argument_list|,
literal|""
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|doQueryShardReplica
argument_list|(
name|SHARD2
argument_list|,
literal|""
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
block|}
comment|// ------------------ auxiliary methods ------------------
DECL|method|doQueryShardReplica
name|void
name|doQueryShardReplica
parameter_list|(
name|String
name|shard
parameter_list|,
name|String
name|expectedDocs
parameter_list|,
name|String
modifier|...
name|queryParams
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|CloudJettyRunner
name|jetty
range|:
name|shardToJetty
operator|.
name|get
argument_list|(
name|SOURCE_COLLECTION
argument_list|)
operator|.
name|get
argument_list|(
name|shard
argument_list|)
control|)
block|{
name|doQuery
argument_list|(
name|jetty
operator|.
name|client
argument_list|,
name|expectedDocs
argument_list|,
name|queryParams
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|vdelete
name|void
name|vdelete
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|version
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
name|vfield
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|version
argument_list|)
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
name|params
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|req
operator|.
name|setParam
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
name|params
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|solrServer
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|vdeleteFail
name|void
name|vdeleteFail
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|version
parameter_list|,
name|int
name|errCode
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
try|try
block|{
name|vdelete
argument_list|(
name|id
argument_list|,
name|version
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|SolrException
operator|&&
name|e
operator|.
name|getCause
argument_list|()
operator|!=
name|e
condition|)
block|{
name|e
operator|=
operator|(
name|SolrException
operator|)
name|e
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|errCode
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|ex
parameter_list|)
block|{
name|Throwable
name|t
init|=
name|ex
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|SolrException
condition|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
name|SolrException
name|exception
init|=
operator|(
name|SolrException
operator|)
name|t
decl_stmt|;
name|assertEquals
argument_list|(
name|errCode
argument_list|,
name|exception
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
literal|"ERROR"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|failed
argument_list|)
expr_stmt|;
block|}
DECL|method|vadd
name|void
name|vadd
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|version
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|vfield
argument_list|,
name|version
argument_list|)
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
name|params
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|req
operator|.
name|setParam
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
name|params
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|solrServer
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|vaddFail
name|void
name|vaddFail
parameter_list|(
name|String
name|id
parameter_list|,
name|long
name|version
parameter_list|,
name|int
name|errCode
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
try|try
block|{
name|vadd
argument_list|(
name|id
argument_list|,
name|version
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|SolrException
operator|&&
name|e
operator|.
name|getCause
argument_list|()
operator|!=
name|e
condition|)
block|{
name|e
operator|=
operator|(
name|SolrException
operator|)
name|e
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|errCode
argument_list|,
name|e
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|ex
parameter_list|)
block|{
name|Throwable
name|t
init|=
name|ex
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|SolrException
condition|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
name|SolrException
name|exception
init|=
operator|(
name|SolrException
operator|)
name|t
decl_stmt|;
name|assertEquals
argument_list|(
name|errCode
argument_list|,
name|exception
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
literal|"ERROR"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|failed
argument_list|)
expr_stmt|;
block|}
DECL|method|doQuery
name|void
name|doQuery
parameter_list|(
name|SolrClient
name|ss
parameter_list|,
name|String
name|expectedDocs
parameter_list|,
name|String
modifier|...
name|queryParams
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|strs
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|expectedDocs
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedIds
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
name|strs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|String
name|id
init|=
name|strs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|vS
init|=
name|strs
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|Long
name|v
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|vS
argument_list|)
decl_stmt|;
name|expectedIds
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
name|QueryResponse
name|rsp
init|=
name|ss
operator|.
name|query
argument_list|(
name|params
argument_list|(
name|queryParams
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|obtainedIds
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|rsp
operator|.
name|getResults
argument_list|()
control|)
block|{
name|obtainedIds
operator|.
name|put
argument_list|(
operator|(
name|String
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|vfield
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedIds
argument_list|,
name|obtainedIds
argument_list|)
expr_stmt|;
block|}
DECL|method|doRealTimeGet
name|void
name|doRealTimeGet
parameter_list|(
name|String
name|ids
parameter_list|,
name|String
name|versions
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedIds
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|strs
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|ids
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|verS
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|versions
argument_list|,
literal|","
argument_list|,
literal|true
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
name|strs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|verS
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|expectedIds
operator|.
name|put
argument_list|(
name|strs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|verS
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|QueryResponse
name|rsp
init|=
name|solrServer
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|,
literal|"ids"
argument_list|,
name|ids
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|obtainedIds
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|rsp
operator|.
name|getResults
argument_list|()
control|)
block|{
name|obtainedIds
operator|.
name|put
argument_list|(
operator|(
name|String
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|,
name|doc
operator|.
name|get
argument_list|(
name|vfield
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedIds
argument_list|,
name|obtainedIds
argument_list|)
expr_stmt|;
block|}
DECL|method|doDeleteByQuery
name|void
name|doDeleteByQuery
parameter_list|(
name|String
name|q
parameter_list|,
name|String
modifier|...
name|reqParams
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|deleteByQuery
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|(
name|reqParams
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|process
argument_list|(
name|solrServer
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
