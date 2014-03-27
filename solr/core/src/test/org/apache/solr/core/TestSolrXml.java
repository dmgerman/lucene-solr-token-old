begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
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
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
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
begin_class
DECL|class|TestSolrXml
specifier|public
class|class
name|TestSolrXml
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|solrHome
specifier|private
specifier|final
name|File
name|solrHome
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
name|TestSolrXml
operator|.
name|getClassName
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrHome"
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testAllInfoPresent
specifier|public
name|void
name|testAllInfoPresent
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|testSrcRoot
init|=
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|testSrcRoot
argument_list|,
literal|"solr-50-all.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|SolrResourceLoader
name|loader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|loader
operator|=
operator|new
name|SolrResourceLoader
argument_list|(
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|ConfigSolr
name|cfg
init|=
name|ConfigSolr
operator|.
name|fromSolrHome
argument_list|(
name|loader
argument_list|,
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testAdminHandler"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_ADMINHANDLER
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testCollectionsHandler"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_COLLECTIONSHANDLER
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testInfoHandler"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_INFOHANDLER
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|11
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_CORELOADTHREADS
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testCoreRootDirectory"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_COREROOTDIRECTORY
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|22
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_DISTRIBUPDATECONNTIMEOUT
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|33
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_DISTRIBUPDATESOTIMEOUT
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|3
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_MAXUPDATECONNECTIONS
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|37
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_MAXUPDATECONNECTIONSPERHOST
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testHost"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_HOST
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testHostContext"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_HOSTCONTEXT
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|44
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_HOSTPORT
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|55
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_LEADERVOTEWAIT
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testLoggingClass"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_LOGGING_CLASS
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testLoggingEnabled"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_LOGGING_ENABLED
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|88
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_LOGGING_WATCHER_SIZE
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|99
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_LOGGING_WATCHER_THRESHOLD
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testManagementPath"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_MANAGEMENTPATH
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testSharedLib"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_SHAREDLIB
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testShareSchema"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_SHARESCHEMA
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|66
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_TRANSIENTCACHESIZE
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|77
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_ZKCLIENTTIMEOUT
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"testZkHost"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_ZKHOST
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Did not find expected value"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_PERSISTENT
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Did not find expected value"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_CORES_DEFAULT_CORE_NAME
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Did not find expected value"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_ADMINPATH
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Test  a few property substitutions that happen to be in solr-50-all.xml.
DECL|method|testPropertySub
specifier|public
name|void
name|testPropertySub
parameter_list|()
throws|throws
name|IOException
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"coreRootDirectory"
argument_list|,
literal|"myCoreRoot"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"hostPort"
argument_list|,
literal|"8888"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"shareSchema"
argument_list|,
literal|"newShareSchema"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"socketTimeout"
argument_list|,
literal|"220"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"connTimeout"
argument_list|,
literal|"200"
argument_list|)
expr_stmt|;
name|File
name|testSrcRoot
init|=
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|testSrcRoot
argument_list|,
literal|"solr-50-all.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|SolrResourceLoader
name|loader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|loader
operator|=
operator|new
name|SolrResourceLoader
argument_list|(
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|ConfigSolr
name|cfg
init|=
name|ConfigSolr
operator|.
name|fromSolrHome
argument_list|(
name|loader
argument_list|,
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"myCoreRoot"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_COREROOTDIRECTORY
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|8888
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_HOSTPORT
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Did not find expected value"
argument_list|,
literal|"newShareSchema"
argument_list|,
name|cfg
operator|.
name|get
argument_list|(
name|ConfigSolr
operator|.
name|CfgProp
operator|.
name|SOLR_SHARESCHEMA
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|loader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
