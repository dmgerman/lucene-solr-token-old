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
name|ArrayList
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|TreeMap
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
name|TestUtil
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
name|RequestStatusState
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
name|ImplicitDocRouter
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
name|junit
operator|.
name|BeforeClass
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
name|common
operator|.
name|params
operator|.
name|ShardParams
operator|.
name|_ROUTE_
import|;
end_import
begin_class
DECL|class|TestCloudBackupRestore
specifier|public
class|class
name|TestCloudBackupRestore
extends|extends
name|SolrCloudTestCase
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
DECL|field|NUM_SHARDS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_SHARDS
init|=
literal|2
decl_stmt|;
comment|//granted we sometimes shard split to get more
DECL|field|docsSeed
specifier|private
specifier|static
name|long
name|docsSeed
decl_stmt|;
comment|// see indexDocs()
annotation|@
name|BeforeClass
DECL|method|createCluster
specifier|public
specifier|static
name|void
name|createCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCluster
argument_list|(
literal|2
argument_list|)
comment|// nodes
operator|.
name|addConfig
argument_list|(
literal|"conf1"
argument_list|,
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"cloud-minimal"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
name|docsSeed
operator|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
expr_stmt|;
block|}
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
name|String
name|collectionName
init|=
literal|"backuprestore"
decl_stmt|;
name|boolean
name|isImplicit
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|int
name|replFactor
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
literal|"conf1"
argument_list|,
name|NUM_SHARDS
argument_list|,
name|replFactor
argument_list|)
decl_stmt|;
if|if
condition|(
name|NUM_SHARDS
operator|*
name|replFactor
operator|>
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
operator|||
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|create
operator|.
name|setMaxShardsPerNode
argument_list|(
name|NUM_SHARDS
argument_list|)
expr_stmt|;
comment|//just to assert it survives the restoration
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|create
operator|.
name|setAutoAddReplicas
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//just to assert it survives the restoration
block|}
name|Properties
name|coreProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|coreProps
operator|.
name|put
argument_list|(
literal|"customKey"
argument_list|,
literal|"customValue"
argument_list|)
expr_stmt|;
comment|//just to assert it survives the restoration
name|create
operator|.
name|setProperties
argument_list|(
name|coreProps
argument_list|)
expr_stmt|;
if|if
condition|(
name|isImplicit
condition|)
block|{
comment|//implicit router
name|create
operator|.
name|setRouterName
argument_list|(
name|ImplicitDocRouter
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|create
operator|.
name|setNumShards
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//erase it. TODO suggest a new createCollectionWithImplicitRouter method
name|create
operator|.
name|setShards
argument_list|(
literal|"shard1,shard2"
argument_list|)
expr_stmt|;
comment|// however still same number as NUM_SHARDS; we assume this later
name|create
operator|.
name|setRouterField
argument_list|(
literal|"shard_s"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//composite id router
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|create
operator|.
name|setRouterField
argument_list|(
literal|"shard_s"
argument_list|)
expr_stmt|;
block|}
block|}
name|CloudSolrClient
name|solrClient
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|create
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
expr_stmt|;
name|indexDocs
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isImplicit
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// shard split the first shard
name|int
name|prevActiveSliceCount
init|=
name|getActiveSliceCount
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|SplitShard
name|splitShard
init|=
name|CollectionAdminRequest
operator|.
name|splitShard
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|splitShard
operator|.
name|setShardName
argument_list|(
literal|"shard1"
argument_list|)
expr_stmt|;
name|splitShard
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
expr_stmt|;
comment|// wait until we see one more active slice...
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|getActiveSliceCount
argument_list|(
name|collectionName
argument_list|)
operator|!=
name|prevActiveSliceCount
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|i
operator|<
literal|30
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
comment|// issue a hard commit.  Split shard does a soft commit which isn't good enough for the backup/snapshooter to see
name|solrClient
operator|.
name|commit
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
block|}
name|testBackupAndRestore
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
block|}
DECL|method|getActiveSliceCount
specifier|private
name|int
name|getActiveSliceCount
parameter_list|(
name|String
name|collectionName
parameter_list|)
block|{
return|return
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
operator|.
name|getActiveSlices
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|indexDocs
specifier|private
name|void
name|indexDocs
parameter_list|(
name|String
name|collectionName
parameter_list|)
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|docsSeed
argument_list|)
decl_stmt|;
comment|// use a constant seed for the whole test run so that we can easily re-index.
name|int
name|numDocs
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|numDocs
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Indexing ZERO test docs"
argument_list|)
expr_stmt|;
return|return;
block|}
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numDocs
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
name|numDocs
condition|;
name|i
operator|++
control|)
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
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"shard_s"
argument_list|,
literal|"shard"
operator|+
operator|(
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|NUM_SHARDS
argument_list|)
operator|)
argument_list|)
expr_stmt|;
comment|// for implicit router
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|add
argument_list|(
name|collectionName
argument_list|,
name|docs
argument_list|)
expr_stmt|;
comment|// batch
name|client
operator|.
name|commit
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
block|}
DECL|method|testBackupAndRestore
specifier|private
name|void
name|testBackupAndRestore
parameter_list|(
name|String
name|collectionName
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|backupName
init|=
literal|"mytestbackup"
decl_stmt|;
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|DocCollection
name|backupCollection
init|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|origShardToDocCount
init|=
name|getShardToDocCountMap
argument_list|(
name|client
argument_list|,
name|backupCollection
argument_list|)
decl_stmt|;
assert|assert
name|origShardToDocCount
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
assert|;
name|String
name|location
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Triggering Backup command"
argument_list|)
expr_stmt|;
block|{
name|CollectionAdminRequest
operator|.
name|Backup
name|backup
init|=
name|CollectionAdminRequest
operator|.
name|backupCollection
argument_list|(
name|collectionName
argument_list|,
name|backupName
argument_list|)
operator|.
name|setLocation
argument_list|(
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|backup
operator|.
name|process
argument_list|(
name|client
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|RequestStatusState
operator|.
name|COMPLETED
argument_list|,
name|backup
operator|.
name|processAndWait
argument_list|(
name|client
argument_list|,
literal|30
argument_list|)
argument_list|)
expr_stmt|;
comment|//async
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Triggering Restore command"
argument_list|)
expr_stmt|;
name|String
name|restoreCollectionName
init|=
name|collectionName
operator|+
literal|"_restored"
decl_stmt|;
name|boolean
name|sameConfig
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
block|{
name|CollectionAdminRequest
operator|.
name|Restore
name|restore
init|=
name|CollectionAdminRequest
operator|.
name|restoreCollection
argument_list|(
name|restoreCollectionName
argument_list|,
name|backupName
argument_list|)
operator|.
name|setLocation
argument_list|(
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
name|origShardToDocCount
operator|.
name|size
argument_list|()
operator|>
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// may need to increase maxShardsPerNode (e.g. if it was shard split, then now we need more)
name|restore
operator|.
name|setMaxShardsPerNode
argument_list|(
name|origShardToDocCount
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"customKey"
argument_list|,
literal|"customVal"
argument_list|)
expr_stmt|;
name|restore
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
if|if
condition|(
name|sameConfig
operator|==
literal|false
condition|)
block|{
name|restore
operator|.
name|setConfigName
argument_list|(
literal|"customConfigName"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|restore
operator|.
name|process
argument_list|(
name|client
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|RequestStatusState
operator|.
name|COMPLETED
argument_list|,
name|restore
operator|.
name|processAndWait
argument_list|(
name|client
argument_list|,
literal|30
argument_list|)
argument_list|)
expr_stmt|;
comment|//async
block|}
name|AbstractDistribZkTestBase
operator|.
name|waitForRecoveriesToFinish
argument_list|(
name|restoreCollectionName
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|log
operator|.
name|isDebugEnabled
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|30
argument_list|)
expr_stmt|;
block|}
comment|//Check the number of results are the same
name|DocCollection
name|restoreCollection
init|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|restoreCollectionName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|origShardToDocCount
argument_list|,
name|getShardToDocCountMap
argument_list|(
name|client
argument_list|,
name|restoreCollection
argument_list|)
argument_list|)
expr_stmt|;
comment|//Re-index same docs (should be identical docs given same random seed) and test we have the same result.  Helps
comment|//  test we reconstituted the hash ranges / doc router.
if|if
condition|(
operator|!
operator|(
name|restoreCollection
operator|.
name|getRouter
argument_list|()
operator|instanceof
name|ImplicitDocRouter
operator|)
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|indexDocs
argument_list|(
name|restoreCollectionName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|origShardToDocCount
argument_list|,
name|getShardToDocCountMap
argument_list|(
name|client
argument_list|,
name|restoreCollection
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|backupCollection
operator|.
name|getReplicationFactor
argument_list|()
argument_list|,
name|restoreCollection
operator|.
name|getReplicationFactor
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|backupCollection
operator|.
name|getAutoAddReplicas
argument_list|()
argument_list|,
name|restoreCollection
operator|.
name|getAutoAddReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|backupCollection
operator|.
name|getActiveSlices
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|restoreCollection
operator|.
name|getActiveSlices
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sameConfig
condition|?
literal|"conf1"
else|:
literal|"customConfigName"
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|readConfigName
argument_list|(
name|restoreCollectionName
argument_list|)
argument_list|)
expr_stmt|;
comment|// assert added core properties:
comment|// DWS: did via manual inspection.
comment|// TODO Find the applicable core.properties on the file system but how?
block|}
DECL|method|getShardToDocCountMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getShardToDocCountMap
parameter_list|(
name|CloudSolrClient
name|client
parameter_list|,
name|DocCollection
name|docCollection
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|shardToDocCount
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|docCollection
operator|.
name|getActiveSlices
argument_list|()
control|)
block|{
name|String
name|shardName
init|=
name|slice
operator|.
name|getName
argument_list|()
decl_stmt|;
name|long
name|docsInShard
init|=
name|client
operator|.
name|query
argument_list|(
name|docCollection
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setParam
argument_list|(
name|_ROUTE_
argument_list|,
name|shardName
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|shardToDocCount
operator|.
name|put
argument_list|(
name|shardName
argument_list|,
operator|(
name|int
operator|)
name|docsInShard
argument_list|)
expr_stmt|;
block|}
return|return
name|shardToDocCount
return|;
block|}
block|}
end_class
end_unit
