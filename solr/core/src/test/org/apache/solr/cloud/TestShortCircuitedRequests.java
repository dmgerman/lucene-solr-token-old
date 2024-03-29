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
name|params
operator|.
name|ShardParams
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
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|TestShortCircuitedRequests
specifier|public
class|class
name|TestShortCircuitedRequests
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|method|TestShortCircuitedRequests
specifier|public
name|TestShortCircuitedRequests
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
name|super
operator|.
name|sliceCount
operator|=
literal|4
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
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|DEFAULT_COLLECTION
argument_list|)
operator|.
name|getSlices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"a!doc1"
argument_list|)
expr_stmt|;
comment|// shard3
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"b!doc1"
argument_list|)
expr_stmt|;
comment|// shard1
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"c!doc1"
argument_list|)
expr_stmt|;
comment|// shard2
name|index
argument_list|(
literal|"id"
argument_list|,
literal|"e!doc1"
argument_list|)
expr_stmt|;
comment|// shard4
name|commit
argument_list|()
expr_stmt|;
name|doQuery
argument_list|(
literal|"a!doc1"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|ShardParams
operator|.
name|_ROUTE_
argument_list|,
literal|"a!"
argument_list|)
expr_stmt|;
comment|// can go to any random node
comment|// query shard3 directly with _route_=a! so that we trigger the short circuited request path
name|Replica
name|shard3
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLeader
argument_list|(
name|DEFAULT_COLLECTION
argument_list|,
literal|"shard3"
argument_list|)
decl_stmt|;
name|String
name|nodeName
init|=
name|shard3
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
name|SolrClient
name|shard3Client
init|=
name|getClient
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
name|QueryResponse
name|response
init|=
name|shard3Client
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|add
argument_list|(
name|ShardParams
operator|.
name|_ROUTE_
argument_list|,
literal|"a!"
argument_list|)
operator|.
name|add
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Could not find doc"
argument_list|,
literal|1
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|?
argument_list|>
name|sinfo
init|=
operator|(
name|NamedList
argument_list|<
name|?
argument_list|>
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"missing shard info for short circuited request"
argument_list|,
name|sinfo
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
