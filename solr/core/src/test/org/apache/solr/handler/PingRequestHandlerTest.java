begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package
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
name|SolrPing
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
name|SolrPingResponse
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
name|MiniSolrCloudCluster
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
name|util
operator|.
name|NamedList
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
begin_class
DECL|class|PingRequestHandlerTest
specifier|public
class|class
name|PingRequestHandlerTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|NUM_SERVERS
specifier|protected
name|int
name|NUM_SERVERS
init|=
literal|5
decl_stmt|;
DECL|field|NUM_SHARDS
specifier|protected
name|int
name|NUM_SHARDS
init|=
literal|2
decl_stmt|;
DECL|field|REPLICATION_FACTOR
specifier|protected
name|int
name|REPLICATION_FACTOR
init|=
literal|2
decl_stmt|;
DECL|field|fileName
specifier|private
specifier|final
name|String
name|fileName
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".server-enabled"
decl_stmt|;
DECL|field|healthcheckFile
specifier|private
name|File
name|healthcheckFile
init|=
literal|null
decl_stmt|;
DECL|field|handler
specifier|private
name|PingRequestHandler
name|handler
init|=
literal|null
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
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|tmpDir
init|=
name|initCoreDataDir
decl_stmt|;
comment|// by default, use relative file in dataDir
name|healthcheckFile
operator|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|String
name|fileNameParam
init|=
name|fileName
decl_stmt|;
comment|// sometimes randomly use an absolute File path instead
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|healthcheckFile
operator|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
name|fileNameParam
operator|=
name|healthcheckFile
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|healthcheckFile
operator|.
name|exists
argument_list|()
condition|)
name|FileUtils
operator|.
name|forceDelete
argument_list|(
name|healthcheckFile
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|PingRequestHandler
argument_list|()
expr_stmt|;
name|NamedList
name|initParams
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|initParams
operator|.
name|add
argument_list|(
name|PingRequestHandler
operator|.
name|HEALTHCHECK_FILE_PARAM
argument_list|,
name|fileNameParam
argument_list|)
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|initParams
argument_list|)
expr_stmt|;
name|handler
operator|.
name|inform
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPingWithNoHealthCheck
specifier|public
name|void
name|testPingWithNoHealthCheck
parameter_list|()
throws|throws
name|Exception
block|{
comment|// for this test, we don't want any healthcheck file configured at all
name|handler
operator|=
operator|new
name|PingRequestHandler
argument_list|()
expr_stmt|;
name|handler
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|inform
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|rsp
init|=
literal|null
decl_stmt|;
name|rsp
operator|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"OK"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"ping"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"OK"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEnablingServer
specifier|public
name|void
name|testEnablingServer
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
operator|!
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// first make sure that ping responds back that the service is disabled
name|SolrQueryResponse
name|sqr
init|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|()
argument_list|)
decl_stmt|;
name|SolrException
name|se
init|=
operator|(
name|SolrException
operator|)
name|sqr
operator|.
name|getException
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Response should have been replaced with a 503 SolrException."
argument_list|,
name|se
operator|.
name|code
argument_list|()
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
operator|.
name|code
argument_list|)
expr_stmt|;
comment|// now enable
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"enable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|healthcheckFile
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now verify that the handler response with success
name|SolrQueryResponse
name|rsp
init|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"OK"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
comment|// enable when already enabled shouldn't cause any problems
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"enable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDisablingServer
specifier|public
name|void
name|testDisablingServer
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
operator|!
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|healthcheckFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
comment|// first make sure that ping responds back that the service is enabled
name|SolrQueryResponse
name|rsp
init|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"OK"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now disable
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"disable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// now make sure that ping responds back that the service is disabled
name|SolrQueryResponse
name|sqr
init|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|()
argument_list|)
decl_stmt|;
name|SolrException
name|se
init|=
operator|(
name|SolrException
operator|)
name|sqr
operator|.
name|getException
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Response should have been replaced with a 503 SolrException."
argument_list|,
name|se
operator|.
name|code
argument_list|()
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
operator|.
name|code
argument_list|)
expr_stmt|;
comment|// disable when already disabled shouldn't cause any problems
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"disable"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|healthcheckFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testGettingStatus
specifier|public
name|void
name|testGettingStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryResponse
name|rsp
init|=
literal|null
decl_stmt|;
name|handler
operator|.
name|handleEnable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"enabled"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleEnable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"disabled"
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBadActionRaisesException
specifier|public
name|void
name|testBadActionRaisesException
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|SolrQueryResponse
name|rsp
init|=
name|makeRequest
argument_list|(
name|handler
argument_list|,
name|req
argument_list|(
literal|"action"
argument_list|,
literal|"badaction"
argument_list|)
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Should have thrown a SolrException for the bad action"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
name|se
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPingInClusterWithNoHealthCheck
specifier|public
name|void
name|testPingInClusterWithNoHealthCheck
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"solr-no-core.xml"
argument_list|)
decl_stmt|;
name|MiniSolrCloudCluster
name|miniCluster
init|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
name|NUM_SERVERS
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|,
name|solrXml
argument_list|,
name|buildJettyConfig
argument_list|(
literal|"/solr"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|CloudSolrClient
name|cloudSolrClient
init|=
name|miniCluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
try|try
block|{
name|assertNotNull
argument_list|(
name|miniCluster
operator|.
name|getZkServer
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|JettySolrRunner
argument_list|>
name|jettys
init|=
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_SERVERS
argument_list|,
name|jettys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|assertTrue
argument_list|(
name|jetty
operator|.
name|isRunning
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// create collection
name|String
name|collectionName
init|=
literal|"testSolrCloudCollection"
decl_stmt|;
name|String
name|configName
init|=
literal|"solrCloudCollectionConfig"
decl_stmt|;
name|File
name|configDir
init|=
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
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
name|miniCluster
operator|.
name|uploadConfigDir
argument_list|(
name|configDir
argument_list|,
name|configName
argument_list|)
expr_stmt|;
name|miniCluster
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
name|NUM_SHARDS
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
name|configName
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Send distributed and non-distributed ping query
name|SolrPingWithDistrib
name|reqDistrib
init|=
operator|new
name|SolrPingWithDistrib
argument_list|()
decl_stmt|;
name|reqDistrib
operator|.
name|setDistrib
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|SolrPingResponse
name|rsp
init|=
name|reqDistrib
operator|.
name|process
argument_list|(
name|cloudSolrClient
argument_list|,
name|collectionName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|SolrPing
name|reqNonDistrib
init|=
operator|new
name|SolrPing
argument_list|()
decl_stmt|;
name|rsp
operator|=
name|reqNonDistrib
operator|.
name|process
argument_list|(
name|cloudSolrClient
argument_list|,
name|collectionName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rsp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
comment|// delete the collection we created earlier
name|miniCluster
operator|.
name|deleteCollection
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|miniCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Helper Method: Executes the request against the handler, returns     * the response, and closes the request.    */
DECL|method|makeRequest
specifier|private
name|SolrQueryResponse
name|makeRequest
parameter_list|(
name|PingRequestHandler
name|handler
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
try|try
block|{
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|rsp
return|;
block|}
DECL|class|SolrPingWithDistrib
class|class
name|SolrPingWithDistrib
extends|extends
name|SolrPing
block|{
DECL|method|setDistrib
specifier|public
name|SolrPing
name|setDistrib
parameter_list|(
name|boolean
name|distrib
parameter_list|)
block|{
name|getParams
argument_list|()
operator|.
name|add
argument_list|(
literal|"distrib"
argument_list|,
name|distrib
condition|?
literal|"true"
else|:
literal|"false"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
end_class
end_unit
