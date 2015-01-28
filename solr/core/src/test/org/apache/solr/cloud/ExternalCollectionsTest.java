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
name|zookeeper
operator|.
name|data
operator|.
name|Stat
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
DECL|class|ExternalCollectionsTest
specifier|public
class|class
name|ExternalCollectionsTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|client
specifier|private
name|CloudSolrClient
name|client
decl_stmt|;
annotation|@
name|Override
DECL|method|distribSetUp
specifier|public
name|void
name|distribSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|client
operator|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getSolrXml
specifier|protected
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"solr-no-core.xml"
return|;
block|}
DECL|method|ExternalCollectionsTest
specifier|public
name|ExternalCollectionsTest
parameter_list|()
block|{
name|checkCreatedVsState
operator|=
literal|false
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
name|testZkNodeLocation
argument_list|()
expr_stmt|;
name|testConfNameAndCollectionNameSame
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStateFormat
specifier|protected
name|String
name|getStateFormat
parameter_list|()
block|{
return|return
literal|"2"
return|;
block|}
DECL|method|testConfNameAndCollectionNameSame
specifier|private
name|void
name|testConfNameAndCollectionNameSame
parameter_list|()
throws|throws
name|Exception
block|{
comment|// .system collection precreates the configset
name|createCollection
argument_list|(
literal|".system"
argument_list|,
name|client
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|".system"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testZkNodeLocation
specifier|private
name|void
name|testZkNodeLocation
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collectionName
init|=
literal|"myExternColl"
decl_stmt|;
name|createCollection
argument_list|(
name|collectionName
argument_list|,
name|client
argument_list|,
literal|2
argument_list|,
literal|2
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
literal|"does not exist collection state externally"
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
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|getData
argument_list|(
name|ZkStateReader
operator|.
name|getCollectionPath
argument_list|(
name|collectionName
argument_list|)
argument_list|,
literal|null
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DocCollection
name|c
init|=
name|ZkStateReader
operator|.
name|getCollectionLive
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
name|collectionName
argument_list|)
decl_stmt|;
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
name|assertEquals
argument_list|(
literal|"The zkversion of the nodes must be same zkver:"
operator|+
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|,
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|,
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
operator|.
name|getZNodeVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"DocCllection#getStateFormat() must be> 1"
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
name|collectionName
argument_list|)
operator|.
name|getStateFormat
argument_list|()
operator|>
literal|1
argument_list|)
expr_stmt|;
comment|// remove collection
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
name|DELETE
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
name|collectionName
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
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
name|client
operator|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|checkForMissingCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"collection state should not exist externally"
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
block|}
end_class
end_unit
