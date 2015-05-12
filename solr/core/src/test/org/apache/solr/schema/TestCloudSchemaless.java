begin_unit
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
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
name|SolrException
operator|.
name|ErrorCode
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
name|util
operator|.
name|BaseTestHarness
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
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|restlet
operator|.
name|ext
operator|.
name|servlet
operator|.
name|ServerServlet
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
name|lang
operator|.
name|Math
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
name|SortedMap
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
begin_comment
comment|/**  * Tests a schemaless collection configuration with SolrCloud  */
end_comment
begin_class
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|TestCloudSchemaless
specifier|public
class|class
name|TestCloudSchemaless
extends|extends
name|AbstractFullDistribZkTestBase
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
name|TestCloudManagedSchemaConcurrent
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SUCCESS_XPATH
specifier|private
specifier|static
specifier|final
name|String
name|SUCCESS_XPATH
init|=
literal|"/response/lst[@name='responseHeader']/int[@name='status'][.='0']"
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
name|useJettyDataDir
operator|=
literal|false
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
block|}
annotation|@
name|After
DECL|method|teardDown
specifier|public
name|void
name|teardDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
for|for
control|(
name|RestTestHarness
name|h
range|:
name|restTestHarnesses
control|)
block|{
name|h
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|TestCloudSchemaless
specifier|public
name|TestCloudSchemaless
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema-add-schema-fields-update-processor.xml"
expr_stmt|;
name|sliceCount
operator|=
literal|4
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|initSysProperties
specifier|public
specifier|static
name|void
name|initSysProperties
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig-schemaless.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getExtraServlets
specifier|public
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|getExtraServlets
parameter_list|()
block|{
specifier|final
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ServletHolder
name|solrRestApi
init|=
operator|new
name|ServletHolder
argument_list|(
literal|"SolrSchemaRestApi"
argument_list|,
name|ServerServlet
operator|.
name|class
argument_list|)
decl_stmt|;
name|solrRestApi
operator|.
name|setInitParameter
argument_list|(
literal|"org.restlet.application"
argument_list|,
literal|"org.apache.solr.rest.SolrSchemaRestApi"
argument_list|)
expr_stmt|;
name|extraServlets
operator|.
name|put
argument_list|(
name|solrRestApi
argument_list|,
literal|"/schema/*"
argument_list|)
expr_stmt|;
comment|// '/schema/*' matches '/schema', '/schema/', and '/schema/whatever...'
return|return
name|extraServlets
return|;
block|}
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
DECL|method|getExpectedFieldResponses
specifier|private
name|String
index|[]
name|getExpectedFieldResponses
parameter_list|(
name|int
name|numberOfDocs
parameter_list|)
block|{
name|String
index|[]
name|expectedAddFields
init|=
operator|new
name|String
index|[
literal|1
operator|+
name|numberOfDocs
index|]
decl_stmt|;
name|expectedAddFields
index|[
literal|0
index|]
operator|=
name|SUCCESS_XPATH
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
name|numberOfDocs
condition|;
operator|++
name|i
control|)
block|{
name|String
name|newFieldName
init|=
literal|"newTestFieldInt"
operator|+
name|i
decl_stmt|;
name|expectedAddFields
index|[
literal|1
operator|+
name|i
index|]
operator|=
literal|"/response/arr[@name='fields']/lst/str[@name='name'][.='"
operator|+
name|newFieldName
operator|+
literal|"']"
expr_stmt|;
block|}
return|return
name|expectedAddFields
return|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|8
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|setupHarnesses
argument_list|()
expr_stmt|;
comment|// First, add a bunch of documents in a single update with the same new field.
comment|// This tests that the replicas properly handle schema additions.
name|int
name|slices
init|=
name|getCommonCloudSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getActiveSlices
argument_list|(
literal|"collection1"
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|trials
init|=
literal|50
decl_stmt|;
comment|// generate enough docs so that we can expect at least a doc per slice
name|int
name|numDocsPerTrial
init|=
call|(
name|int
call|)
argument_list|(
name|slices
operator|*
operator|(
name|Math
operator|.
name|log
argument_list|(
name|slices
argument_list|)
operator|+
literal|1
operator|)
argument_list|)
decl_stmt|;
name|SolrClient
name|randomClient
init|=
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
name|int
name|docNumber
init|=
literal|0
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
name|trials
condition|;
operator|++
name|i
control|)
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDocsPerTrial
condition|;
operator|++
name|j
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
name|Long
operator|.
name|toHexString
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"newTestFieldInt"
operator|+
name|docNumber
operator|++
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"constantField"
argument_list|,
literal|"3.14159"
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|randomClient
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
name|randomClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
index|[]
name|expectedFields
init|=
name|getExpectedFieldResponses
argument_list|(
name|docNumber
argument_list|)
decl_stmt|;
comment|// Check that all the fields were added
for|for
control|(
name|RestTestHarness
name|client
range|:
name|restTestHarnesses
control|)
block|{
name|String
name|request
init|=
literal|"/schema/fields?wt=xml"
decl_stmt|;
name|String
name|response
init|=
name|client
operator|.
name|query
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
name|result
init|=
name|BaseTestHarness
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
name|expectedFields
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"QUERY FAILED: xpath="
operator|+
name|result
operator|+
literal|"  request="
operator|+
name|request
operator|+
literal|"  response="
operator|+
name|response
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Now, let's ensure that writing the same field with two different types fails
name|int
name|failTrials
init|=
literal|50
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
name|failTrials
condition|;
operator|++
name|i
control|)
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
literal|null
decl_stmt|;
name|SolrInputDocument
name|intDoc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|intDoc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|Long
operator|.
name|toHexString
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|intDoc
operator|.
name|addField
argument_list|(
literal|"longOrDateField"
operator|+
name|i
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|dateDoc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|dateDoc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|Long
operator|.
name|toHexString
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dateDoc
operator|.
name|addField
argument_list|(
literal|"longOrDateField"
operator|+
name|i
argument_list|,
literal|"1995-12-31T23:59:59Z"
argument_list|)
expr_stmt|;
comment|// randomize the order of the docs
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|docs
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|intDoc
argument_list|,
name|dateDoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|docs
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|dateDoc
argument_list|,
name|intDoc
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|randomClient
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|randomClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected Bad Request Exception"
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
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ErrorCode
operator|.
name|getErrorCode
argument_list|(
name|se
operator|.
name|code
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|CloudSolrClient
name|cloudSolrClient
init|=
name|getCommonCloudSolrClient
argument_list|()
decl_stmt|;
name|cloudSolrClient
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|cloudSolrClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected Bad Request Exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ErrorCode
operator|.
name|getErrorCode
argument_list|(
operator|(
name|ex
operator|)
operator|.
name|code
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
