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
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|List
import|;
end_import
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
name|commons
operator|.
name|io
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
name|commons
operator|.
name|io
operator|.
name|filefilter
operator|.
name|RegexFileFilter
import|;
end_import
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
name|filefilter
operator|.
name|TrueFileFilter
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
name|util
operator|.
name|ExternalPaths
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
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
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
begin_comment
comment|// TODO: This test would be a lot faster if it used a solrhome with fewer config
end_comment
begin_comment
comment|// files - there are a lot of them to upload
end_comment
begin_class
DECL|class|ZkCLITest
specifier|public
class|class
name|ZkCLITest
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
DECL|field|solrHome
specifier|private
name|String
name|solrHome
decl_stmt|;
DECL|field|zkClient
specifier|private
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|SOLR_HOME
specifier|protected
specifier|static
specifier|final
name|String
name|SOLR_HOME
init|=
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
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
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|)
expr_stmt|;
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
name|createTempDir
argument_list|()
expr_stmt|;
name|boolean
name|useNewSolrXml
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|useNewSolrXml
condition|)
block|{
name|solrHome
operator|=
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
expr_stmt|;
block|}
else|else
block|{
name|File
name|tmpSolrHome
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"tmp-solr-home"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
argument_list|)
argument_list|,
name|tmpSolrHome
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"old-solr-example/solr.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|tmpSolrHome
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|solrHome
operator|=
name|tmpSolrHome
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
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
name|log
operator|.
name|info
argument_list|(
literal|"ZooKeeper dataDir:"
operator|+
name|zkDir
argument_list|)
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
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkHost
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
literal|"/solr"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|zkClient
operator|=
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
DECL|method|testBootstrap
specifier|public
name|void
name|testBootstrap
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test bootstrap_conf
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"bootstrap"
block|,
literal|"-solrhome"
block|,
name|this
operator|.
name|solrHome
block|}
decl_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/collection1"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"bootstrap"
block|,
literal|"-solrhome"
block|,
name|ExternalPaths
operator|.
name|EXAMPLE_MULTICORE_HOME
block|}
expr_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/core0"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/core1"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBootstrapWithChroot
specifier|public
name|void
name|testBootstrapWithChroot
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|chroot
init|=
literal|"/foo/bar"
decl_stmt|;
name|assertFalse
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
name|chroot
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
operator|+
name|chroot
block|,
literal|"-cmd"
block|,
literal|"bootstrap"
block|,
literal|"-solrhome"
block|,
name|this
operator|.
name|solrHome
block|}
decl_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
name|chroot
operator|+
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/collection1"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMakePath
specifier|public
name|void
name|testMakePath
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test bootstrap_conf
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"makepath"
block|,
literal|"/path/mynewpath"
block|}
decl_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/path/mynewpath"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPut
specifier|public
name|void
name|testPut
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test put
name|String
name|data
init|=
literal|"my data"
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"put"
block|,
literal|"/data.txt"
block|,
name|data
block|}
decl_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/data.txt"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/data.txt"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|,
name|data
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutFile
specifier|public
name|void
name|testPutFile
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test put file
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"putfile"
block|,
literal|"/solr.xml"
block|,
name|SOLR_HOME
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr-stress-new.xml"
block|}
decl_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|fromZk
init|=
operator|new
name|String
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/solr.xml"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|File
name|locFile
init|=
operator|new
name|File
argument_list|(
name|SOLR_HOME
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr-stress-new.xml"
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|locFile
argument_list|)
decl_stmt|;
name|String
name|fromLoc
decl_stmt|;
try|try
block|{
name|fromLoc
operator|=
operator|new
name|String
argument_list|(
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|is
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Should get back what we put in ZK"
argument_list|,
name|fromZk
argument_list|,
name|fromLoc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutFileNotExists
specifier|public
name|void
name|testPutFileNotExists
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test put file
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"putfile"
block|,
literal|"/solr.xml"
block|,
name|SOLR_HOME
operator|+
name|File
operator|.
name|separator
operator|+
literal|"not-there.xml"
block|}
decl_stmt|;
try|try
block|{
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have had a file not found exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|fne
parameter_list|)
block|{
name|String
name|msg
init|=
name|fne
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Didn't find expected error message containing 'not-there.xml' in "
operator|+
name|msg
argument_list|,
name|msg
operator|.
name|indexOf
argument_list|(
literal|"not-there.xml"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testList
specifier|public
name|void
name|testList
parameter_list|()
throws|throws
name|Exception
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/test"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"list"
block|}
decl_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpConfigLinkConfigClearZk
specifier|public
name|void
name|testUpConfigLinkConfigClearZk
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test upconfig
name|String
name|confsetname
init|=
literal|"confsetone"
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"upconfig"
block|,
literal|"-confdir"
block|,
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
block|,
literal|"-confname"
block|,
name|confsetname
block|}
decl_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|confsetname
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// print help
comment|// ZkCLI.main(new String[0]);
comment|// test linkconfig
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"linkconfig"
block|,
literal|"-collection"
block|,
literal|"collection1"
block|,
literal|"-confname"
block|,
name|confsetname
block|}
expr_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|collectionProps
init|=
name|ZkNodeProps
operator|.
name|load
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/collection1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|collectionProps
operator|.
name|containsKey
argument_list|(
literal|"configName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|confsetname
argument_list|,
name|collectionProps
operator|.
name|getStr
argument_list|(
literal|"configName"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test down config
name|File
name|confDir
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"solrtest-confdropspot-"
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|confDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"downconfig"
block|,
literal|"-confdir"
block|,
name|confDir
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-confname"
block|,
name|confsetname
block|}
expr_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|File
index|[]
name|files
init|=
name|confDir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|zkFiles
init|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|confsetname
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|files
operator|.
name|length
argument_list|,
name|zkFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|sourceConfDir
init|=
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
argument_list|)
decl_stmt|;
comment|// filter out all directories starting with . (e.g. .svn)
name|Collection
argument_list|<
name|File
argument_list|>
name|sourceFiles
init|=
name|FileUtils
operator|.
name|listFiles
argument_list|(
name|sourceConfDir
argument_list|,
name|TrueFileFilter
operator|.
name|INSTANCE
argument_list|,
operator|new
name|RegexFileFilter
argument_list|(
literal|"[^\\.].*"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|sourceFile
range|:
name|sourceFiles
control|)
block|{
name|int
name|indexOfRelativePath
init|=
name|sourceFile
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|lastIndexOf
argument_list|(
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
argument_list|)
decl_stmt|;
name|String
name|relativePathofFile
init|=
name|sourceFile
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|substring
argument_list|(
name|indexOfRelativePath
operator|+
literal|17
argument_list|,
name|sourceFile
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|downloadedFile
init|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
name|relativePathofFile
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|downloadedFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" does not exist source:"
operator|+
name|sourceFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|downloadedFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Content didn't change"
argument_list|,
name|FileUtils
operator|.
name|contentEquals
argument_list|(
name|sourceFile
argument_list|,
name|downloadedFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// test reset zk
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"clear"
block|,
literal|"/"
block|}
expr_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|zkClient
operator|.
name|getChildren
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGet
specifier|public
name|void
name|testGet
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|getNode
init|=
literal|"/getNode"
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|String
argument_list|(
literal|"getNode-data"
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|this
operator|.
name|zkClient
operator|.
name|create
argument_list|(
name|getNode
argument_list|,
name|data
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"get"
block|,
name|getNode
block|}
decl_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFile
specifier|public
name|void
name|testGetFile
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|getNode
init|=
literal|"/getFileNode"
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|String
argument_list|(
literal|"getFileNode-data"
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|this
operator|.
name|zkClient
operator|.
name|create
argument_list|(
name|getNode
argument_list|,
name|data
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"solrtest-getfile-"
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"getfile"
block|,
name|getNode
block|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|byte
index|[]
name|readData
init|=
name|FileUtils
operator|.
name|readFileToByteArray
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|readData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFileNotExists
specifier|public
name|void
name|testGetFileNotExists
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|getNode
init|=
literal|"/getFileNotExistsNode"
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"solrtest-getfilenotexists-"
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-zkhost"
block|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
block|,
literal|"-cmd"
block|,
literal|"getfile"
block|,
name|getNode
block|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
block|}
decl_stmt|;
try|try
block|{
name|ZkCLI
operator|.
name|main
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected NoNodeException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|ex
parameter_list|)
block|{     }
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SolrException
operator|.
name|class
argument_list|)
DECL|method|testInvalidZKAddress
specifier|public
name|void
name|testInvalidZKAddress
parameter_list|()
throws|throws
name|SolrException
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
literal|"----------:33332"
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|zkClient
operator|.
name|close
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
block|}
DECL|method|printLayout
specifier|private
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
