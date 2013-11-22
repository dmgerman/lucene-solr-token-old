begin_unit
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|SolrJettyTestBase
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
name|SolrServer
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
name|HttpSolrServer
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
name|CoreAdminRequest
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
name|QueryResponse
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
name|util
operator|.
name|NamedList
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
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|DistributedDebugComponentTest
specifier|public
class|class
name|DistributedDebugComponentTest
extends|extends
name|SolrJettyTestBase
block|{
DECL|field|collection1
specifier|private
specifier|static
name|SolrServer
name|collection1
decl_stmt|;
DECL|field|collection2
specifier|private
specifier|static
name|SolrServer
name|collection2
decl_stmt|;
DECL|field|shard1
specifier|private
specifier|static
name|String
name|shard1
decl_stmt|;
DECL|field|shard2
specifier|private
specifier|static
name|String
name|shard2
decl_stmt|;
DECL|field|solrHome
specifier|private
specifier|static
name|File
name|solrHome
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeTest
specifier|public
specifier|static
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|solrHome
operator|=
name|createSolrHome
argument_list|()
expr_stmt|;
name|createJetty
argument_list|(
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|url
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|collection1
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|collection2
operator|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
operator|+
literal|"/collection2"
argument_list|)
expr_stmt|;
name|String
name|urlCollection1
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
literal|"collection1"
decl_stmt|;
name|String
name|urlCollection2
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
literal|"collection2"
decl_stmt|;
name|shard1
operator|=
name|urlCollection1
operator|.
name|replaceAll
argument_list|(
literal|"http://"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|shard2
operator|=
name|urlCollection2
operator|.
name|replaceAll
argument_list|(
literal|"http://"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|//create second core
name|CoreAdminRequest
operator|.
name|Create
name|req
init|=
operator|new
name|CoreAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
literal|"collection2"
argument_list|)
expr_stmt|;
name|collection1
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|createSolrHome
specifier|private
specifier|static
name|File
name|createSolrHome
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|DistributedDebugComponentTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|setupJettyTestHome
argument_list|(
name|workDir
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"collection1"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"collection2"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|workDir
return|;
block|}
annotation|@
name|AfterClass
DECL|method|afterTest
specifier|public
specifier|static
name|void
name|afterTest
parameter_list|()
throws|throws
name|Exception
block|{
name|collection1
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|collection2
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|collection1
operator|=
literal|null
expr_stmt|;
name|collection2
operator|=
literal|null
expr_stmt|;
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jetty
operator|=
literal|null
expr_stmt|;
name|cleanUpJettyHome
argument_list|(
name|solrHome
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
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
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"text"
argument_list|,
literal|"batman"
argument_list|)
expr_stmt|;
name|collection1
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|collection1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"text"
argument_list|,
literal|"superman"
argument_list|)
expr_stmt|;
name|collection2
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|collection2
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSimpleSearch
specifier|public
name|void
name|testSimpleSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"debug"
argument_list|,
literal|"track"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setFields
argument_list|(
literal|"id"
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|shard1
operator|+
literal|","
operator|+
name|shard2
argument_list|)
expr_stmt|;
name|QueryResponse
name|response
init|=
name|collection1
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|track
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|response
operator|.
name|getDebugMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"track"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|track
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|track
operator|.
name|get
argument_list|(
literal|"rid"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|track
operator|.
name|get
argument_list|(
literal|"EXECUTE_QUERY"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"EXECUTE_QUERY"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|shard1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"EXECUTE_QUERY"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|shard2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"GET_FIELDS"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|shard1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"GET_FIELDS"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|shard2
argument_list|)
argument_list|)
expr_stmt|;
name|assertElementsPresent
argument_list|(
call|(
name|NamedList
argument_list|<
name|String
argument_list|>
call|)
argument_list|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"EXECUTE_QUERY"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
name|shard1
argument_list|)
argument_list|,
literal|"QTime"
argument_list|,
literal|"ElapsedTime"
argument_list|,
literal|"RequestPurpose"
argument_list|,
literal|"NumFound"
argument_list|,
literal|"Response"
argument_list|)
expr_stmt|;
name|assertElementsPresent
argument_list|(
call|(
name|NamedList
argument_list|<
name|String
argument_list|>
call|)
argument_list|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"EXECUTE_QUERY"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
name|shard2
argument_list|)
argument_list|,
literal|"QTime"
argument_list|,
literal|"ElapsedTime"
argument_list|,
literal|"RequestPurpose"
argument_list|,
literal|"NumFound"
argument_list|,
literal|"Response"
argument_list|)
expr_stmt|;
name|assertElementsPresent
argument_list|(
call|(
name|NamedList
argument_list|<
name|String
argument_list|>
call|)
argument_list|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"GET_FIELDS"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
name|shard1
argument_list|)
argument_list|,
literal|"QTime"
argument_list|,
literal|"ElapsedTime"
argument_list|,
literal|"RequestPurpose"
argument_list|,
literal|"NumFound"
argument_list|,
literal|"Response"
argument_list|)
expr_stmt|;
name|assertElementsPresent
argument_list|(
call|(
name|NamedList
argument_list|<
name|String
argument_list|>
call|)
argument_list|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"GET_FIELDS"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
name|shard2
argument_list|)
argument_list|,
literal|"QTime"
argument_list|,
literal|"ElapsedTime"
argument_list|,
literal|"RequestPurpose"
argument_list|,
literal|"NumFound"
argument_list|,
literal|"Response"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"omitHeader"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|response
operator|=
name|collection1
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"QTime is not included in the response when omitHeader is set to true"
argument_list|,
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|response
operator|.
name|getDebugMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"track"
argument_list|)
operator|)
operator|.
name|findRecursive
argument_list|(
literal|"EXECUTE_QUERY"
argument_list|,
name|shard1
argument_list|,
literal|"QTime"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"QTime is not included in the response when omitHeader is set to true"
argument_list|,
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|response
operator|.
name|getDebugMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"track"
argument_list|)
operator|)
operator|.
name|findRecursive
argument_list|(
literal|"GET_FIELDS"
argument_list|,
name|shard2
argument_list|,
literal|"QTime"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"id:1"
argument_list|)
expr_stmt|;
name|response
operator|=
name|collection1
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|track
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|response
operator|.
name|getDebugMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"track"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"EXECUTE_QUERY"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|shard1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"EXECUTE_QUERY"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|shard2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"GET_FIELDS"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|shard1
argument_list|)
argument_list|)
expr_stmt|;
comment|// This test is invalid, as GET_FIELDS should not be executed in shard 2
name|assertNull
argument_list|(
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|track
operator|.
name|get
argument_list|(
literal|"GET_FIELDS"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|shard2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertElementsPresent
specifier|private
name|void
name|assertElementsPresent
parameter_list|(
name|NamedList
argument_list|<
name|String
argument_list|>
name|namedList
parameter_list|,
name|String
modifier|...
name|elements
parameter_list|)
block|{
for|for
control|(
name|String
name|element
range|:
name|elements
control|)
block|{
name|String
name|value
init|=
name|namedList
operator|.
name|get
argument_list|(
name|element
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Expected element '"
operator|+
name|element
operator|+
literal|"' but was not found"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected element '"
operator|+
name|element
operator|+
literal|"' but was empty"
argument_list|,
operator|!
name|value
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit