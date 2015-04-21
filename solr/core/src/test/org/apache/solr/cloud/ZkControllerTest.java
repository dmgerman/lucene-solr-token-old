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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|ZkConfigManager
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
name|core
operator|.
name|CloudConfig
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
name|handler
operator|.
name|admin
operator|.
name|CoreAdminHandler
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
name|handler
operator|.
name|component
operator|.
name|HttpShardHandlerFactory
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
name|UpdateShardHandler
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
name|UpdateShardHandlerConfig
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
begin_class
annotation|@
name|Slow
DECL|class|ZkControllerTest
specifier|public
class|class
name|ZkControllerTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|COLLECTION_NAME
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION_NAME
init|=
literal|"collection1"
decl_stmt|;
DECL|field|TIMEOUT
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
literal|10000
decl_stmt|;
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
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
name|Exception
block|{    }
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{    }
DECL|method|testNodeNameUrlConversion
specifier|public
name|void
name|testNodeNameUrlConversion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// nodeName from parts
name|assertEquals
argument_list|(
literal|"localhost:8888_solr"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"localhost"
argument_list|,
literal|"8888"
argument_list|,
literal|"solr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost:8888_solr"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"localhost"
argument_list|,
literal|"8888"
argument_list|,
literal|"/solr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost:8888_solr"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"localhost"
argument_list|,
literal|"8888"
argument_list|,
literal|"/solr/"
argument_list|)
argument_list|)
expr_stmt|;
comment|// root context
name|assertEquals
argument_list|(
literal|"localhost:8888_"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"localhost"
argument_list|,
literal|"8888"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localhost:8888_"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"localhost"
argument_list|,
literal|"8888"
argument_list|,
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
comment|// subdir
name|assertEquals
argument_list|(
literal|"foo-bar:77_solr%2Fsub_dir"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar"
argument_list|,
literal|"77"
argument_list|,
literal|"solr/sub_dir"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo-bar:77_solr%2Fsub_dir"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar"
argument_list|,
literal|"77"
argument_list|,
literal|"/solr/sub_dir"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo-bar:77_solr%2Fsub_dir"
argument_list|,
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar"
argument_list|,
literal|"77"
argument_list|,
literal|"/solr/sub_dir/"
argument_list|)
argument_list|)
expr_stmt|;
comment|// setup a SolrZkClient to do some getBaseUrlForNodeName testing
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"zkData"
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
name|ZkStateReader
name|zkStateReader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
comment|// getBaseUrlForNodeName
name|assertEquals
argument_list|(
literal|"http://zzz.xxx:1234/solr"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
literal|"zzz.xxx:1234_solr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://xxx:99"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
literal|"xxx:99_"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar.baz.org:9999/some_dir"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
literal|"foo-bar.baz.org:9999_some_dir"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar.baz.org:9999/solr/sub_dir"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
literal|"foo-bar.baz.org:9999_solr%2Fsub_dir"
argument_list|)
argument_list|)
expr_stmt|;
comment|// generateNodeName + getBaseUrlForNodeName
name|assertEquals
argument_list|(
literal|"http://foo:9876/solr"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo"
argument_list|,
literal|"9876"
argument_list|,
literal|"solr"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo:9876/solr"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo"
argument_list|,
literal|"9876"
argument_list|,
literal|"/solr"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo:9876/solr"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo"
argument_list|,
literal|"9876"
argument_list|,
literal|"/solr/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo.bar.com:9876/solr/sub_dir"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo.bar.com"
argument_list|,
literal|"9876"
argument_list|,
literal|"solr/sub_dir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo.bar.com:9876/solr/sub_dir"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo.bar.com"
argument_list|,
literal|"9876"
argument_list|,
literal|"/solr/sub_dir/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar:9876"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar"
argument_list|,
literal|"9876"
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar:9876"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar"
argument_list|,
literal|"9876"
argument_list|,
literal|"/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar.com:80/some_dir"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar.com"
argument_list|,
literal|"80"
argument_list|,
literal|"some_dir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"http://foo-bar.com:80/some_dir"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar.com"
argument_list|,
literal|"80"
argument_list|,
literal|"/some_dir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//Verify the URL Scheme is taken into account
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|create
argument_list|(
name|ZkStateReader
operator|.
name|CLUSTER_PROPS
argument_list|,
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"urlScheme"
argument_list|,
literal|"https"
argument_list|)
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"https://zzz.xxx:1234/solr"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
literal|"zzz.xxx:1234_solr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"https://foo-bar.com:80/some_dir"
argument_list|,
name|zkStateReader
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|ZkController
operator|.
name|generateNodeName
argument_list|(
literal|"foo-bar.com"
argument_list|,
literal|"80"
argument_list|,
literal|"/some_dir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkStateReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadConfigName
specifier|public
name|void
name|testReadConfigName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|CoreContainer
name|cc
init|=
literal|null
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
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|)
decl_stmt|;
name|String
name|actualConfigName
init|=
literal|"firstConfig"
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkConfigManager
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|actualConfigName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"configName"
argument_list|,
name|actualConfigName
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|zkProps
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|props
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
literal|"/"
operator|+
name|COLLECTION_NAME
argument_list|,
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|zkProps
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|cc
operator|=
name|getCoreContainer
argument_list|()
expr_stmt|;
name|CloudConfig
name|cloudConfig
init|=
operator|new
name|CloudConfig
operator|.
name|CloudConfigBuilder
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|8983
argument_list|,
literal|"solr"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ZkController
name|zkController
init|=
operator|new
name|ZkController
argument_list|(
name|cc
argument_list|,
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
name|cloudConfig
argument_list|,
operator|new
name|CurrentCoreDescriptorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|getCurrentDescriptors
parameter_list|()
block|{
comment|// do nothing
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|configName
init|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|readConfigName
argument_list|(
name|COLLECTION_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|configName
argument_list|,
name|actualConfigName
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkController
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testGetHostName
specifier|public
name|void
name|testGetHostName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|CoreContainer
name|cc
init|=
literal|null
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
name|cc
operator|=
name|getCoreContainer
argument_list|()
expr_stmt|;
name|ZkController
name|zkController
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CloudConfig
name|cloudConfig
init|=
operator|new
name|CloudConfig
operator|.
name|CloudConfigBuilder
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|8983
argument_list|,
literal|"solr"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|zkController
operator|=
operator|new
name|ZkController
argument_list|(
name|cc
argument_list|,
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
name|cloudConfig
argument_list|,
operator|new
name|CurrentCoreDescriptorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|getCurrentDescriptors
parameter_list|()
block|{
comment|// do nothing
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"ZkController did not normalize host name correctly"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
name|zkController
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*   Test that:   1) LIR state to 'down' is not set unless publishing node is a leader     1a) Test that leader can publish when LIR node already exists in zk     1b) Test that leader can publish when LIR node does not exist - TODO   2) LIR state to 'active' or 'recovery' can be set regardless of whether publishing     node is leader or not - TODO    */
DECL|method|testEnsureReplicaInLeaderInitiatedRecovery
specifier|public
name|void
name|testEnsureReplicaInLeaderInitiatedRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"testEnsureReplicaInLeaderInitiatedRecovery"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|CoreContainer
name|cc
init|=
literal|null
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
name|cc
operator|=
name|getCoreContainer
argument_list|()
expr_stmt|;
name|ZkController
name|zkController
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CloudConfig
name|cloudConfig
init|=
operator|new
name|CloudConfig
operator|.
name|CloudConfigBuilder
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|8983
argument_list|,
literal|"solr"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|zkController
operator|=
operator|new
name|ZkController
argument_list|(
name|cc
argument_list|,
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
name|cloudConfig
argument_list|,
operator|new
name|CurrentCoreDescriptorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|getCurrentDescriptors
parameter_list|()
block|{
comment|// do nothing
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|propMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
literal|"http://127.0.0.1:8983/solr"
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
literal|"replica1"
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|,
literal|"127.0.0.1:8983_solr"
argument_list|)
expr_stmt|;
name|Replica
name|replica
init|=
operator|new
name|Replica
argument_list|(
literal|"replica1"
argument_list|,
name|propMap
argument_list|)
decl_stmt|;
try|try
block|{
comment|// this method doesn't throw exception when node isn't leader
name|zkController
operator|.
name|ensureReplicaInLeaderInitiatedRecovery
argument_list|(
literal|"c1"
argument_list|,
literal|"shard1"
argument_list|,
operator|new
name|ZkCoreNodeProps
argument_list|(
name|replica
argument_list|)
argument_list|,
literal|"non_existent_leader"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"ZkController should not write LIR state for node which is not leader"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertNull
argument_list|(
literal|"ZkController should not write LIR state for node which is not leader"
argument_list|,
name|zkController
operator|.
name|getLeaderInitiatedRecoveryState
argument_list|(
literal|"c1"
argument_list|,
literal|"shard1"
argument_list|,
literal|"replica1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
name|zkController
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-6665"
argument_list|)
DECL|method|testPublishAndWaitForDownStates
specifier|public
name|void
name|testPublishAndWaitForDownStates
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"testPublishAndWaitForDownStates"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|CoreContainer
name|cc
init|=
literal|null
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
name|cc
operator|=
name|getCoreContainer
argument_list|()
expr_stmt|;
name|ZkController
name|zkController
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CloudConfig
name|cloudConfig
init|=
operator|new
name|CloudConfig
operator|.
name|CloudConfigBuilder
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|8983
argument_list|,
literal|"solr"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|zkController
operator|=
operator|new
name|ZkController
argument_list|(
name|cc
argument_list|,
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
name|cloudConfig
argument_list|,
operator|new
name|CurrentCoreDescriptorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|getCurrentDescriptors
parameter_list|()
block|{
comment|// do nothing
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|DocCollection
argument_list|>
name|collectionStates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|replicas
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// add two replicas with the same core name but one of them should be on a different node
comment|// than this ZkController instance
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|Replica
name|r
init|=
operator|new
name|Replica
argument_list|(
literal|"core_node"
operator|+
name|i
argument_list|,
name|map
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|,
name|i
operator|==
literal|1
condition|?
literal|"active"
else|:
literal|"down"
argument_list|,
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|,
name|i
operator|==
literal|1
condition|?
literal|"127.0.0.1:8983_solr"
else|:
literal|"non_existent_host"
argument_list|,
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
literal|"collection1"
argument_list|)
argument_list|)
decl_stmt|;
name|replicas
operator|.
name|put
argument_list|(
literal|"core_node"
operator|+
name|i
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sliceProps
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|sliceProps
operator|.
name|put
argument_list|(
literal|"state"
argument_list|,
name|Slice
operator|.
name|State
operator|.
name|ACTIVE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Slice
name|slice
init|=
operator|new
name|Slice
argument_list|(
literal|"shard1"
argument_list|,
name|replicas
argument_list|,
name|sliceProps
argument_list|)
decl_stmt|;
name|DocCollection
name|c
init|=
operator|new
name|DocCollection
argument_list|(
literal|"testPublishAndWaitForDownStates"
argument_list|,
name|map
argument_list|(
literal|"shard1"
argument_list|,
name|slice
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|ClusterState
name|state
init|=
operator|new
name|ClusterState
argument_list|(
literal|0
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
name|map
argument_list|(
literal|"testPublishAndWaitForDownStates"
argument_list|,
name|c
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|zkController
operator|.
name|getZkClient
argument_list|()
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|getCollectionPath
argument_list|(
literal|"testPublishAndWaitForDownStates"
argument_list|)
argument_list|,
name|bytes
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|hasCollection
argument_list|(
literal|"testPublishAndWaitForDownStates"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"testPublishAndWaitForDownStates"
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|timeout
init|=
name|now
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|ZkController
operator|.
name|WAIT_DOWN_STATES_TIMEOUT_SECONDS
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|zkController
operator|.
name|publishAndWaitForDownStates
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The ZkController.publishAndWaitForDownStates should have timed out but it didn't"
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
operator|>=
name|timeout
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
name|zkController
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getCoreContainer
specifier|private
name|CoreContainer
name|getCoreContainer
parameter_list|()
block|{
return|return
operator|new
name|MockCoreContainer
argument_list|()
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
DECL|class|MockCoreContainer
specifier|private
specifier|static
class|class
name|MockCoreContainer
extends|extends
name|CoreContainer
block|{
DECL|method|MockCoreContainer
specifier|public
name|MockCoreContainer
parameter_list|()
block|{
name|super
argument_list|(
operator|(
name|Object
operator|)
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardHandlerFactory
operator|=
operator|new
name|HttpShardHandlerFactory
argument_list|()
expr_stmt|;
name|this
operator|.
name|coreAdminHandler
operator|=
operator|new
name|CoreAdminHandler
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|void
name|load
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|getUpdateShardHandler
specifier|public
name|UpdateShardHandler
name|getUpdateShardHandler
parameter_list|()
block|{
return|return
operator|new
name|UpdateShardHandler
argument_list|(
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
