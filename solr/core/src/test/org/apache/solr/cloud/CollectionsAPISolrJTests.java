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
name|NamedList
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
name|Map
import|;
end_import
begin_class
annotation|@
name|LuceneTestCase
operator|.
name|Slow
DECL|class|CollectionsAPISolrJTests
specifier|public
class|class
name|CollectionsAPISolrJTests
extends|extends
name|AbstractFullDistribZkTestBase
block|{
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
name|testCreateAndDeleteCollection
argument_list|()
expr_stmt|;
name|testCreateAndDeleteShard
argument_list|()
expr_stmt|;
name|testReloadCollection
argument_list|()
expr_stmt|;
name|testCreateAndDeleteAlias
argument_list|()
expr_stmt|;
name|testSplitShard
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
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
block|}
DECL|method|testCreateAndDeleteCollection
specifier|protected
name|void
name|testCreateAndDeleteCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collectionName
init|=
literal|"solrj_test"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|createCollectionRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|createCollectionRequest
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setNumShards
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setReplicationFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setConfigName
argument_list|(
literal|"conf1"
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setRouterField
argument_list|(
literal|"myOwnField"
argument_list|)
expr_stmt|;
name|CollectionAdminResponse
name|response
init|=
name|createCollectionRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|coresStatus
init|=
name|response
operator|.
name|getCollectionCoresStatus
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|coresStatus
operator|.
name|size
argument_list|()
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|NamedList
argument_list|<
name|Integer
argument_list|>
name|status
init|=
name|coresStatus
operator|.
name|get
argument_list|(
name|collectionName
operator|+
literal|"_shard"
operator|+
operator|(
name|i
operator|/
literal|2
operator|+
literal|1
operator|)
operator|+
literal|"_replica"
operator|+
operator|(
name|i
operator|%
literal|2
operator|+
literal|1
operator|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|status
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|status
operator|.
name|get
argument_list|(
literal|"QTime"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|Delete
name|deleteCollectionRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
decl_stmt|;
name|deleteCollectionRequest
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|response
operator|=
name|deleteCollectionRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|nodesStatus
init|=
name|response
operator|.
name|getCollectionNodesStatus
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"Deleted collection "
operator|+
name|collectionName
operator|+
literal|"still exists"
argument_list|,
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollectionOrNull
argument_list|(
name|collectionName
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|nodesStatus
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test Creating a collection with new stateformat.
name|collectionName
operator|=
literal|"solrj_newstateformat"
expr_stmt|;
name|createCollectionRequest
operator|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
expr_stmt|;
name|createCollectionRequest
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setNumShards
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setConfigName
argument_list|(
literal|"conf1"
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setStateFormat
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|response
operator|=
name|createCollectionRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collectionName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Collection state does not exist"
argument_list|,
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|exists
argument_list|(
name|ZkStateReader
operator|.
name|getCollectionPath
argument_list|(
name|collectionName
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateAndDeleteShard
specifier|protected
name|void
name|testCreateAndDeleteShard
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
comment|// Create an implicit collection
name|String
name|collectionName
init|=
literal|"solrj_implicit"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|createCollectionRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|createCollectionRequest
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setShards
argument_list|(
literal|"shardA,shardB"
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setConfigName
argument_list|(
literal|"conf1"
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setRouterName
argument_list|(
literal|"implicit"
argument_list|)
expr_stmt|;
name|CollectionAdminResponse
name|response
init|=
name|createCollectionRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|coresStatus
init|=
name|response
operator|.
name|getCollectionCoresStatus
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|coresStatus
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
comment|// Add a shard to the implicit collection
name|CollectionAdminRequest
operator|.
name|CreateShard
name|createShardRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|CreateShard
argument_list|()
decl_stmt|;
name|createShardRequest
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|createShardRequest
operator|.
name|setShardName
argument_list|(
literal|"shardC"
argument_list|)
expr_stmt|;
name|response
operator|=
name|createShardRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|coresStatus
operator|=
name|response
operator|.
name|getCollectionCoresStatus
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|coresStatus
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|coresStatus
operator|.
name|get
argument_list|(
name|collectionName
operator|+
literal|"_shardC_replica1"
argument_list|)
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|DeleteShard
name|deleteShardRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|DeleteShard
argument_list|()
decl_stmt|;
name|deleteShardRequest
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|deleteShardRequest
operator|.
name|setShardName
argument_list|(
literal|"shardC"
argument_list|)
expr_stmt|;
name|response
operator|=
name|deleteShardRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|nodesStatus
init|=
name|response
operator|.
name|getCollectionNodesStatus
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|nodesStatus
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testReloadCollection
specifier|protected
name|void
name|testReloadCollection
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|DEFAULT_COLLECTION
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|Reload
name|reloadCollectionRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Reload
argument_list|()
decl_stmt|;
name|reloadCollectionRequest
operator|.
name|setCollectionName
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|CollectionAdminResponse
name|response
init|=
name|reloadCollectionRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateAndDeleteAlias
specifier|protected
name|void
name|testCreateAndDeleteAlias
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|CollectionAdminRequest
operator|.
name|CreateAlias
name|createAliasRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|CreateAlias
argument_list|()
decl_stmt|;
name|createAliasRequest
operator|.
name|setCollectionName
argument_list|(
literal|"solrj_alias"
argument_list|)
expr_stmt|;
name|createAliasRequest
operator|.
name|setAliasedCollections
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|CollectionAdminResponse
name|response
init|=
name|createAliasRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|DeleteAlias
name|deleteAliasRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|DeleteAlias
argument_list|()
decl_stmt|;
name|deleteAliasRequest
operator|.
name|setCollectionName
argument_list|(
literal|"solrj_alias"
argument_list|)
expr_stmt|;
name|deleteAliasRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSplitShard
specifier|protected
name|void
name|testSplitShard
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collectionName
init|=
literal|"solrj_test_splitshard"
decl_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|createCollectionRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|createCollectionRequest
operator|.
name|setConfigName
argument_list|(
literal|"conf1"
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setNumShards
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|SplitShard
name|splitShardRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|SplitShard
argument_list|()
decl_stmt|;
name|splitShardRequest
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|splitShardRequest
operator|.
name|setShardName
argument_list|(
literal|"shard1"
argument_list|)
expr_stmt|;
name|CollectionAdminResponse
name|response
init|=
name|splitShardRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|coresStatus
init|=
name|response
operator|.
name|getCollectionCoresStatus
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|coresStatus
operator|.
name|get
argument_list|(
name|collectionName
operator|+
literal|"_shard1_0_replica1"
argument_list|)
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|coresStatus
operator|.
name|get
argument_list|(
name|collectionName
operator|+
literal|"_shard1_1_replica1"
argument_list|)
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collectionName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|10
argument_list|)
expr_stmt|;
comment|// Test splitting using split.key
name|splitShardRequest
operator|=
operator|new
name|CollectionAdminRequest
operator|.
name|SplitShard
argument_list|()
expr_stmt|;
name|splitShardRequest
operator|.
name|setCollectionName
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|splitShardRequest
operator|.
name|setSplitKey
argument_list|(
literal|"b!"
argument_list|)
expr_stmt|;
name|response
operator|=
name|splitShardRequest
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collectionName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|10
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
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getActiveSlices
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ClusterState: "
operator|+
name|clusterState
operator|.
name|getActiveSlices
argument_list|(
name|collectionName
argument_list|)
argument_list|,
literal|5
argument_list|,
name|slices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
