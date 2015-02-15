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
name|impl
operator|.
name|HttpSolrClient
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
name|AbstractFullDistribZkTestBase
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
name|handler
operator|.
name|TestBlobHandler
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
name|RESTfulServerProvider
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
name|RestTestHarness
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
name|SimplePostTool
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Arrays
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
name|zip
operator|.
name|ZipEntry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
import|;
end_import
begin_class
DECL|class|TestDynamicLoading
specifier|public
class|class
name|TestDynamicLoading
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDynamicLoading
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|restTestHarnesses
specifier|private
name|List
argument_list|<
name|RestTestHarness
argument_list|>
name|restTestHarnesses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|setupHarnesses
specifier|private
name|void
name|setupHarnesses
parameter_list|()
block|{
for|for
control|(
specifier|final
name|SolrClient
name|client
range|:
name|clients
control|)
block|{
name|RestTestHarness
name|harness
init|=
operator|new
name|RestTestHarness
argument_list|(
operator|new
name|RESTfulServerProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getBaseURL
parameter_list|()
block|{
return|return
operator|(
operator|(
name|HttpSolrClient
operator|)
name|client
operator|)
operator|.
name|getBaseURL
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|restTestHarnesses
operator|.
name|add
argument_list|(
name|harness
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|RestTestHarness
name|r
range|:
name|restTestHarnesses
control|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDynamicLoading
specifier|public
name|void
name|testDynamicLoading
parameter_list|()
throws|throws
name|Exception
block|{
name|setupHarnesses
argument_list|()
expr_stmt|;
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|"'create-requesthandler' : { 'name' : '/test1', 'class': 'org.apache.solr.core.BlobStoreTestRequestHandler' , 'lib':'test','version':'1'}\n"
operator|+
literal|"}"
decl_stmt|;
name|RestTestHarness
name|client
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|client
argument_list|,
literal|"/config?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/config/overlay?wt=json"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"overlay"
argument_list|,
literal|"requestHandler"
argument_list|,
literal|"/test1"
argument_list|,
literal|"lib"
argument_list|)
argument_list|,
literal|"test"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|Map
name|map
init|=
name|TestSolrConfigHandler
operator|.
name|getRespMap
argument_list|(
literal|"/test1?wt=json"
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|map
operator|=
operator|(
name|Map
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"error"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".system collection not available"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
argument_list|)
expr_stmt|;
name|HttpSolrClient
name|randomClient
init|=
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|clients
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|baseURL
init|=
name|randomClient
operator|.
name|getBaseURL
argument_list|()
decl_stmt|;
name|baseURL
operator|=
name|baseURL
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|baseURL
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
name|TestBlobHandler
operator|.
name|createSystemCollection
argument_list|(
operator|new
name|HttpSolrClient
argument_list|(
name|baseURL
argument_list|,
name|randomClient
operator|.
name|getHttpClient
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|".system"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|map
operator|=
name|TestSolrConfigHandler
operator|.
name|getRespMap
argument_list|(
literal|"/test1?wt=json"
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|map
operator|=
operator|(
name|Map
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"error"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no such blob or version available: test/1"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
argument_list|)
expr_stmt|;
name|ByteBuffer
name|jar
init|=
name|generateZip
argument_list|(
name|TestDynamicLoading
operator|.
name|class
argument_list|,
name|BlobStoreTestRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|TestBlobHandler
operator|.
name|postAndCheck
argument_list|(
name|cloudClient
argument_list|,
name|baseURL
argument_list|,
name|jar
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
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
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|=
name|TestSolrConfigHandler
operator|.
name|getRespMap
argument_list|(
literal|"/test1?wt=json"
argument_list|,
name|client
argument_list|)
expr_stmt|;
if|if
condition|(
name|BlobStoreTestRequestHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
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
operator|new
name|String
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|map
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|success
argument_list|)
expr_stmt|;
name|jar
operator|=
name|generateZip
argument_list|(
name|TestDynamicLoading
operator|.
name|class
argument_list|,
name|BlobStoreTestRequestHandlerV2
operator|.
name|class
argument_list|)
expr_stmt|;
name|TestBlobHandler
operator|.
name|postAndCheck
argument_list|(
name|cloudClient
argument_list|,
name|baseURL
argument_list|,
name|jar
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|" {\n"
operator|+
literal|"  'set' : {'watched': {"
operator|+
literal|"                    'x':'X val',\n"
operator|+
literal|"                    'y': 'Y val'}\n"
operator|+
literal|"             }\n"
operator|+
literal|"  }"
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|client
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"watched"
argument_list|,
literal|"x"
argument_list|)
argument_list|,
literal|"X val"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|"{\n"
operator|+
literal|"'update-requesthandler' : { 'name' : '/test1', 'class': 'org.apache.solr.core.BlobStoreTestRequestHandlerV2' , 'lib':'test','version':2}\n"
operator|+
literal|"}"
expr_stmt|;
name|client
operator|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|client
argument_list|,
literal|"/config?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|client
argument_list|,
literal|null
argument_list|,
literal|"/config/overlay?wt=json"
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"overlay"
argument_list|,
literal|"requestHandler"
argument_list|,
literal|"/test1"
argument_list|,
literal|"version"
argument_list|)
argument_list|,
literal|2l
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|success
operator|=
literal|false
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|=
name|TestSolrConfigHandler
operator|.
name|getRespMap
argument_list|(
literal|"/test1?wt=json"
argument_list|,
name|client
argument_list|)
expr_stmt|;
if|if
condition|(
name|BlobStoreTestRequestHandlerV2
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
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
literal|"New version of class is not loaded "
operator|+
operator|new
name|String
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|map
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|success
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|=
name|TestSolrConfigHandler
operator|.
name|getRespMap
argument_list|(
literal|"/test1?wt=json"
argument_list|,
name|client
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"X val"
operator|.
name|equals
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
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
name|payload
operator|=
literal|" {\n"
operator|+
literal|"  'set' : {'watched': {"
operator|+
literal|"                    'x':'X val changed',\n"
operator|+
literal|"                    'y': 'Y val'}\n"
operator|+
literal|"             }\n"
operator|+
literal|"  }"
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|client
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|payload
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
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|=
name|TestSolrConfigHandler
operator|.
name|getRespMap
argument_list|(
literal|"/test1?wt=json"
argument_list|,
name|client
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"X val changed"
operator|.
name|equals
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
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
literal|"listener did not get triggered"
operator|+
operator|new
name|String
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|map
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|success
argument_list|)
expr_stmt|;
block|}
DECL|method|generateZip
specifier|public
specifier|static
name|ByteBuffer
name|generateZip
parameter_list|(
name|Class
modifier|...
name|classes
parameter_list|)
throws|throws
name|IOException
block|{
name|ZipOutputStream
name|zipOut
init|=
literal|null
decl_stmt|;
name|SimplePostTool
operator|.
name|BAOS
name|bos
init|=
operator|new
name|SimplePostTool
operator|.
name|BAOS
argument_list|()
decl_stmt|;
name|zipOut
operator|=
operator|new
name|ZipOutputStream
argument_list|(
name|bos
argument_list|)
expr_stmt|;
name|zipOut
operator|.
name|setLevel
argument_list|(
name|ZipOutputStream
operator|.
name|DEFLATED
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
name|c
range|:
name|classes
control|)
block|{
name|String
name|path
init|=
name|c
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|.
name|concat
argument_list|(
literal|".class"
argument_list|)
decl_stmt|;
name|ZipEntry
name|entry
init|=
operator|new
name|ZipEntry
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|ByteBuffer
name|b
init|=
name|SimplePostTool
operator|.
name|inputStreamToByteArray
argument_list|(
name|c
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|zipOut
operator|.
name|putNextEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|zipOut
operator|.
name|write
argument_list|(
name|b
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
name|zipOut
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
name|zipOut
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|bos
operator|.
name|getByteBuffer
argument_list|()
return|;
block|}
block|}
end_class
end_unit
