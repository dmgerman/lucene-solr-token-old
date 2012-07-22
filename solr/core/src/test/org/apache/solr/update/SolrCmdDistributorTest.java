begin_unit
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|File
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
name|List
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
name|SolrCmdDistributor
operator|.
name|Node
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
name|SolrCmdDistributor
operator|.
name|Response
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
name|SolrCmdDistributor
operator|.
name|StdNode
import|;
end_import
begin_class
DECL|class|SolrCmdDistributorTest
specifier|public
class|class
name|SolrCmdDistributorTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|SolrCmdDistributorTest
specifier|public
name|SolrCmdDistributorTest
parameter_list|()
block|{
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|shardCount
operator|=
literal|1
expr_stmt|;
name|stress
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|getSchemaFile
specifier|public
specifier|static
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
specifier|static
name|String
name|getSolrConfigFile
parameter_list|()
block|{
comment|// use this because it has /update and is minimal
return|return
literal|"solrconfig-tlog.xml"
return|;
block|}
comment|// TODO: for now we redefine this method so that it pulls from the above
comment|// we don't get helpful override behavior due to the method being static
DECL|method|createServers
specifier|protected
name|void
name|createServers
parameter_list|(
name|int
name|numShards
parameter_list|)
throws|throws
name|Exception
block|{
name|controlJetty
operator|=
name|createJetty
argument_list|(
operator|new
name|File
argument_list|(
name|getSolrHome
argument_list|()
argument_list|)
argument_list|,
name|testDir
operator|+
literal|"/control/data"
argument_list|,
literal|null
argument_list|,
name|getSolrConfigFile
argument_list|()
argument_list|,
name|getSchemaFile
argument_list|()
argument_list|)
expr_stmt|;
name|controlClient
operator|=
name|createNewSolrServer
argument_list|(
name|controlJetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
name|shardsArr
operator|=
operator|new
name|String
index|[
name|numShards
index|]
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|numShards
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|j
init|=
name|createJetty
argument_list|(
operator|new
name|File
argument_list|(
name|getSolrHome
argument_list|()
argument_list|)
argument_list|,
name|testDir
operator|+
literal|"/shard"
operator|+
name|i
operator|+
literal|"/data"
argument_list|,
literal|null
argument_list|,
name|getSolrConfigFile
argument_list|()
argument_list|,
name|getSchemaFile
argument_list|()
argument_list|)
decl_stmt|;
name|jettys
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|clients
operator|.
name|add
argument_list|(
name|createNewSolrServer
argument_list|(
name|j
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|shardStr
init|=
literal|"localhost:"
operator|+
name|j
operator|.
name|getLocalPort
argument_list|()
operator|+
name|context
decl_stmt|;
name|shardsArr
index|[
name|i
index|]
operator|=
name|shardStr
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|shardStr
argument_list|)
expr_stmt|;
block|}
name|shards
operator|=
name|sb
operator|.
name|toString
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
comment|//del("*:*");
name|SolrCmdDistributor
name|cmdDistrib
init|=
operator|new
name|SolrCmdDistributor
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|>
argument_list|()
decl_stmt|;
name|ZkNodeProps
name|nodeProps
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
operator|(
operator|(
name|HttpSolrServer
operator|)
name|controlClient
operator|)
operator|.
name|getBaseURL
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
operator|new
name|StdNode
argument_list|(
operator|new
name|ZkCoreNodeProps
argument_list|(
name|nodeProps
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// add one doc to controlClient
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|solrDoc
operator|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cmdDistrib
operator|.
name|distribAdd
argument_list|(
name|cmd
argument_list|,
name|nodes
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|CommitUpdateCommand
name|ccmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|cmdDistrib
operator|.
name|distribCommit
argument_list|(
name|ccmd
argument_list|,
name|nodes
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|cmdDistrib
operator|.
name|finish
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
name|cmdDistrib
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|response
operator|.
name|errors
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|response
operator|.
name|errors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|numFound
init|=
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
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|numFound
argument_list|)
expr_stmt|;
name|HttpSolrServer
name|client
init|=
operator|(
name|HttpSolrServer
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|nodeProps
operator|=
operator|new
name|ZkNodeProps
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
name|client
operator|.
name|getBaseURL
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
operator|new
name|StdNode
argument_list|(
operator|new
name|ZkCoreNodeProps
argument_list|(
name|nodeProps
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// add another 2 docs to control and 3 to client
name|cmdDistrib
operator|=
operator|new
name|SolrCmdDistributor
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|solrDoc
operator|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|cmdDistrib
operator|.
name|distribAdd
argument_list|(
name|cmd
argument_list|,
name|nodes
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|AddUpdateCommand
name|cmd2
init|=
operator|new
name|AddUpdateCommand
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|cmd2
operator|.
name|solrDoc
operator|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|cmdDistrib
operator|.
name|distribAdd
argument_list|(
name|cmd2
argument_list|,
name|nodes
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|AddUpdateCommand
name|cmd3
init|=
operator|new
name|AddUpdateCommand
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|cmd3
operator|.
name|solrDoc
operator|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|cmdDistrib
operator|.
name|distribAdd
argument_list|(
name|cmd3
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|cmdDistrib
operator|.
name|distribCommit
argument_list|(
name|ccmd
argument_list|,
name|nodes
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|cmdDistrib
operator|.
name|finish
argument_list|()
expr_stmt|;
name|response
operator|=
name|cmdDistrib
operator|.
name|getResponse
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|response
operator|.
name|errors
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|response
operator|.
name|errors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|results
init|=
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
decl_stmt|;
name|numFound
operator|=
name|results
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|results
operator|.
name|toString
argument_list|()
argument_list|,
literal|3
argument_list|,
name|numFound
argument_list|)
expr_stmt|;
name|numFound
operator|=
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
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|numFound
argument_list|)
expr_stmt|;
comment|// now delete doc 2 which is on both control and client1
name|DeleteUpdateCommand
name|dcmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|dcmd
operator|.
name|id
operator|=
literal|"2"
expr_stmt|;
name|cmdDistrib
operator|=
operator|new
name|SolrCmdDistributor
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|cmdDistrib
operator|.
name|distribDelete
argument_list|(
name|dcmd
argument_list|,
name|nodes
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|cmdDistrib
operator|.
name|distribCommit
argument_list|(
name|ccmd
argument_list|,
name|nodes
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|cmdDistrib
operator|.
name|finish
argument_list|()
expr_stmt|;
name|response
operator|=
name|cmdDistrib
operator|.
name|getResponse
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|response
operator|.
name|errors
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|response
operator|.
name|errors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|results
operator|=
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
expr_stmt|;
name|numFound
operator|=
name|results
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|results
operator|.
name|toString
argument_list|()
argument_list|,
literal|2
argument_list|,
name|numFound
argument_list|)
expr_stmt|;
name|numFound
operator|=
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
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|results
operator|.
name|toString
argument_list|()
argument_list|,
literal|2
argument_list|,
name|numFound
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
