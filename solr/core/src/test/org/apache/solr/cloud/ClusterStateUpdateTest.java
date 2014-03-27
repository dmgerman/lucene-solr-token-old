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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|core
operator|.
name|CoreContainer
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
name|core
operator|.
name|CoreDescriptor
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
name|core
operator|.
name|SolrCore
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
name|AfterClass
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_class
annotation|@
name|Slow
DECL|class|ClusterStateUpdateTest
specifier|public
class|class
name|ClusterStateUpdateTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractZkTestCase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|VERBOSE
specifier|private
specifier|static
specifier|final
name|boolean
name|VERBOSE
init|=
literal|false
decl_stmt|;
DECL|field|zkServer
specifier|protected
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|zkDir
specifier|protected
name|String
name|zkDir
decl_stmt|;
DECL|field|container1
specifier|private
name|CoreContainer
name|container1
decl_stmt|;
DECL|field|container2
specifier|private
name|CoreContainer
name|container2
decl_stmt|;
DECL|field|container3
specifier|private
name|CoreContainer
name|container3
decl_stmt|;
DECL|field|dataDir1
specifier|private
name|File
name|dataDir1
decl_stmt|;
DECL|field|dataDir2
specifier|private
name|File
name|dataDir2
decl_stmt|;
DECL|field|dataDir3
specifier|private
name|File
name|dataDir3
decl_stmt|;
DECL|field|dataDir4
specifier|private
name|File
name|dataDir4
decl_stmt|;
DECL|field|solrHomeDirectory
specifier|private
specifier|static
specifier|volatile
name|File
name|solrHomeDirectory
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"ZkControllerTest"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|IOException
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"genericCoreNodeNames"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
if|if
condition|(
name|solrHomeDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
name|copyMinFullSetup
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"genericCoreNodeNames"
argument_list|)
expr_stmt|;
if|if
condition|(
name|solrHomeDirectory
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|solrHomeDirectory
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkClientTimeout"
argument_list|,
literal|"3000"
argument_list|)
expr_stmt|;
name|zkDir
operator|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
expr_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|buildZooKeeper
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_START "
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|dataDir1
operator|=
operator|new
name|File
argument_list|(
name|dataDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"data1"
argument_list|)
expr_stmt|;
name|dataDir1
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataDir2
operator|=
operator|new
name|File
argument_list|(
name|dataDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"data2"
argument_list|)
expr_stmt|;
name|dataDir2
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataDir3
operator|=
operator|new
name|File
argument_list|(
name|dataDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"data3"
argument_list|)
expr_stmt|;
name|dataDir3
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataDir4
operator|=
operator|new
name|File
argument_list|(
name|dataDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"data4"
argument_list|)
expr_stmt|;
name|dataDir4
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// set some system properties for use by tests
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|TEST_HOME
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"hostPort"
argument_list|,
literal|"1661"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|ClusterStateUpdateTest
operator|.
name|this
operator|.
name|dataDir1
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|container1
operator|=
operator|new
name|CoreContainer
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|container1
operator|.
name|load
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"hostPort"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"hostPort"
argument_list|,
literal|"1662"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|ClusterStateUpdateTest
operator|.
name|this
operator|.
name|dataDir2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|container2
operator|=
operator|new
name|CoreContainer
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|container2
operator|.
name|load
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"hostPort"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"hostPort"
argument_list|,
literal|"1663"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|ClusterStateUpdateTest
operator|.
name|this
operator|.
name|dataDir3
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|container3
operator|=
operator|new
name|CoreContainer
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|container3
operator|.
name|load
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"hostPort"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.solr.home"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_END "
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCoreRegistration
specifier|public
name|void
name|testCoreRegistration
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solrcloud.update.delay"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props2
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|props2
operator|.
name|put
argument_list|(
literal|"configName"
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|zkProps2
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|props2
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
name|AbstractZkTestCase
operator|.
name|TIMEOUT
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
literal|"/testcore"
argument_list|,
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|zkProps2
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/testcore/shards"
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|CoreDescriptor
name|dcore
init|=
name|buildCoreDescriptor
argument_list|(
name|container1
argument_list|,
literal|"testcore"
argument_list|,
literal|"testcore"
argument_list|)
operator|.
name|withDataDir
argument_list|(
name|dataDir4
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|container1
operator|.
name|getZkController
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|container1
operator|.
name|preRegisterInZk
argument_list|(
name|dcore
argument_list|)
expr_stmt|;
block|}
name|SolrCore
name|core
init|=
name|container1
operator|.
name|create
argument_list|(
name|dcore
argument_list|)
decl_stmt|;
name|container1
operator|.
name|register
argument_list|(
name|core
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ZkController
name|zkController2
init|=
name|container2
operator|.
name|getZkController
argument_list|()
decl_stmt|;
name|String
name|host
init|=
name|zkController2
operator|.
name|getHostName
argument_list|()
decl_stmt|;
comment|// slight pause - TODO: takes an oddly long amount of time to schedule tasks
comment|// with almost no delay ...
name|ClusterState
name|clusterState2
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|75
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|clusterState2
operator|=
name|zkController2
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
name|slices
operator|=
name|clusterState2
operator|.
name|getSlicesMap
argument_list|(
literal|"testcore"
argument_list|)
expr_stmt|;
if|if
condition|(
name|slices
operator|!=
literal|null
operator|&&
name|slices
operator|.
name|containsKey
argument_list|(
literal|"shard1"
argument_list|)
operator|&&
name|slices
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|slices
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|slices
operator|.
name|containsKey
argument_list|(
literal|"shard1"
argument_list|)
argument_list|)
expr_stmt|;
name|Slice
name|slice
init|=
name|slices
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"shard1"
argument_list|,
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|shards
init|=
name|slice
operator|.
name|getReplicasMap
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|shards
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Replica
name|zkProps
init|=
name|shards
operator|.
name|get
argument_list|(
name|host
operator|+
literal|":1661_solr_testcore"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|zkProps
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|host
operator|+
literal|":1661_solr"
argument_list|,
name|zkProps
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://"
operator|+
name|host
operator|+
literal|":1661/solr"
argument_list|,
name|zkProps
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|clusterState2
operator|.
name|getLiveNodes
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|liveNodes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|liveNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|container3
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// slight pause (15s timeout) for watch to trigger
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
literal|5
operator|*
literal|15
operator|)
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|zkController2
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
operator|.
name|size
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
literal|200
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|zkController2
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// quickly kill / start client
name|container2
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|getConnection
argument_list|()
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|container2
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"hostPort"
argument_list|,
literal|"1662"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|ClusterStateUpdateTest
operator|.
name|this
operator|.
name|dataDir2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|container2
operator|=
operator|new
name|CoreContainer
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|container2
operator|.
name|load
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"hostPort"
argument_list|)
expr_stmt|;
comment|// pause for watch to trigger
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
if|if
condition|(
name|container1
operator|.
name|getZkController
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|liveNodesContain
argument_list|(
name|container2
operator|.
name|getZkController
argument_list|()
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|container1
operator|.
name|getZkController
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|liveNodesContain
argument_list|(
name|container2
operator|.
name|getZkController
argument_list|()
operator|.
name|getNodeName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// core.close();  // don't close - this core is managed by container1 now
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
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|printLayout
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|container1
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|container2
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|container3
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkClientTimeout"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkHost"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"hostPort"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solrcloud.update.delay"
argument_list|)
expr_stmt|;
block|}
DECL|method|printLayout
specifier|static
name|void
name|printLayout
parameter_list|(
name|String
name|zkHost
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkHost
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
