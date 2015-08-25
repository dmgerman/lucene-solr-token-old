begin_unit
begin_package
DECL|package|org.apache.solr.cloud.overseer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|overseer
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
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|cloud
operator|.
name|AbstractZkTestCase
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
name|cloud
operator|.
name|Overseer
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
name|cloud
operator|.
name|OverseerTest
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
name|cloud
operator|.
name|ZkController
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
name|cloud
operator|.
name|ZkTestServer
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
name|DocRouter
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
name|Utils
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
name|KeeperException
import|;
end_import
begin_class
DECL|class|ZkStateReaderTest
specifier|public
class|class
name|ZkStateReaderTest
extends|extends
name|SolrTestCaseJ4
block|{
comment|/** Uses explicit refresh to ensure latest changes are visible. */
DECL|method|testStateFormatUpdateWithExplicitRefresh
specifier|public
name|void
name|testStateFormatUpdateWithExplicitRefresh
parameter_list|()
throws|throws
name|Exception
block|{
name|testStateFormatUpdate
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Uses explicit refresh to ensure latest changes are visible. */
DECL|method|testStateFormatUpdateWithExplicitRefreshLazy
specifier|public
name|void
name|testStateFormatUpdateWithExplicitRefreshLazy
parameter_list|()
throws|throws
name|Exception
block|{
name|testStateFormatUpdate
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** ZkStateReader should automatically pick up changes based on ZK watches. */
DECL|method|testStateFormatUpdateWithTimeDelay
specifier|public
name|void
name|testStateFormatUpdateWithTimeDelay
parameter_list|()
throws|throws
name|Exception
block|{
name|testStateFormatUpdate
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** ZkStateReader should automatically pick up changes based on ZK watches. */
DECL|method|testStateFormatUpdateWithTimeDelayLazy
specifier|public
name|void
name|testStateFormatUpdateWithTimeDelayLazy
parameter_list|()
throws|throws
name|Exception
block|{
name|testStateFormatUpdate
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testStateFormatUpdate
specifier|public
name|void
name|testStateFormatUpdate
parameter_list|(
name|boolean
name|explicitRefresh
parameter_list|,
name|boolean
name|isInteresting
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"testStateFormatUpdate"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|OverseerTest
operator|.
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|ZkController
operator|.
name|createClusterZkNodes
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|ZkStateReader
name|reader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|reader
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
if|if
condition|(
name|isInteresting
condition|)
block|{
name|reader
operator|.
name|addCollectionWatch
argument_list|(
literal|"c1"
argument_list|)
expr_stmt|;
block|}
name|ZkStateWriter
name|writer
init|=
operator|new
name|ZkStateWriter
argument_list|(
name|reader
argument_list|,
operator|new
name|Overseer
operator|.
name|Stats
argument_list|()
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|{
comment|// create new collection with stateFormat = 1
name|DocCollection
name|stateV1
init|=
operator|new
name|DocCollection
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|CLUSTER_STATE
argument_list|)
decl_stmt|;
name|ZkWriteCommand
name|c1
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c1"
argument_list|,
name|stateV1
argument_list|)
decl_stmt|;
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|c1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writePendingUpdates
argument_list|()
expr_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/clusterstate.json"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"c1"
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|exists
init|=
name|zkClient
operator|.
name|exists
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1/state.json"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|exists
argument_list|)
expr_stmt|;
if|if
condition|(
name|explicitRefresh
condition|)
block|{
name|reader
operator|.
name|updateClusterState
argument_list|()
expr_stmt|;
block|}
else|else
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|reader
operator|.
name|getClusterState
argument_list|()
operator|.
name|hasCollection
argument_list|(
literal|"c1"
argument_list|)
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
block|}
name|DocCollection
name|collection
init|=
name|reader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"c1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|collection
operator|.
name|getStateFormat
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
comment|// Now update the collection to stateFormat = 2
name|DocCollection
name|stateV2
init|=
operator|new
name|DocCollection
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1/state.json"
argument_list|)
decl_stmt|;
name|ZkWriteCommand
name|c2
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c1"
argument_list|,
name|stateV2
argument_list|)
decl_stmt|;
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|c2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writePendingUpdates
argument_list|()
expr_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/clusterstate.json"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"c1"
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|exists
init|=
name|zkClient
operator|.
name|exists
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1/state.json"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exists
argument_list|)
expr_stmt|;
if|if
condition|(
name|explicitRefresh
condition|)
block|{
name|reader
operator|.
name|updateClusterState
argument_list|()
expr_stmt|;
block|}
else|else
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|reader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"c1"
argument_list|)
operator|.
name|getStateFormat
argument_list|()
operator|==
literal|2
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
block|}
name|DocCollection
name|collection
init|=
name|reader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"c1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|collection
operator|.
name|getStateFormat
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testExternalCollectionWatchedNotWatched
specifier|public
name|void
name|testExternalCollectionWatchedNotWatched
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"testExternalCollectionWatchedNotWatched"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|OverseerTest
operator|.
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|ZkController
operator|.
name|createClusterZkNodes
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|ZkStateReader
name|reader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|reader
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
name|ZkStateWriter
name|writer
init|=
operator|new
name|ZkStateWriter
argument_list|(
name|reader
argument_list|,
operator|new
name|Overseer
operator|.
name|Stats
argument_list|()
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// create new collection with stateFormat = 2
name|ZkWriteCommand
name|c1
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|DocCollection
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1/state.json"
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|c1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writePendingUpdates
argument_list|()
expr_stmt|;
name|reader
operator|.
name|updateClusterState
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollectionRef
argument_list|(
literal|"c1"
argument_list|)
operator|.
name|isLazilyLoaded
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|addCollectionWatch
argument_list|(
literal|"c1"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollectionRef
argument_list|(
literal|"c1"
argument_list|)
operator|.
name|isLazilyLoaded
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|removeZKWatch
argument_list|(
literal|"c1"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollectionRef
argument_list|(
literal|"c1"
argument_list|)
operator|.
name|isLazilyLoaded
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
