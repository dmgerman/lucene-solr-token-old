begin_unit
begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
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
name|ResponseParser
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
name|SolrServerException
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
name|util
operator|.
name|ExternalPaths
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import
begin_comment
comment|/**  * Extend SolrJettyTestBase because the SOLR-2535 bug only manifested itself when  * the {@link org.apache.solr.servlet.SolrDispatchFilter} is used, which isn't for embedded Solr use.  */
end_comment
begin_class
DECL|class|ShowFileRequestHandlerTest
specifier|public
class|class
name|ShowFileRequestHandlerTest
extends|extends
name|SolrJettyTestBase
block|{
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
name|createJetty
argument_list|(
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testDirList
specifier|public
name|void
name|testDirList
parameter_list|()
throws|throws
name|SolrServerException
block|{
name|SolrServer
name|server
init|=
name|getSolrServer
argument_list|()
decl_stmt|;
comment|//assertQ(req("qt", "/admin/file")); TODO file bug that SolrJettyTestBase extends SolrTestCaseJ4
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/file"
argument_list|)
expr_stmt|;
name|QueryResponse
name|resp
init|=
name|request
operator|.
name|process
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|resp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|NamedList
operator|)
name|resp
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"files"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|//some files
block|}
DECL|method|testGetRawFile
specifier|public
name|void
name|testGetRawFile
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|SolrServer
name|server
init|=
name|getSolrServer
argument_list|()
decl_stmt|;
comment|//assertQ(req("qt", "/admin/file")); TODO file bug that SolrJettyTestBase extends SolrTestCaseJ4
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"file"
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/file"
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|readFile
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|request
operator|.
name|setResponseParser
argument_list|(
operator|new
name|ResponseParser
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getWriterType
parameter_list|()
block|{
return|return
literal|"mock"
return|;
comment|//unfortunately this gets put onto params wt=mock but it apparently has no effect
block|}
annotation|@
name|Override
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|InputStream
name|body
parameter_list|,
name|String
name|encoding
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|body
operator|.
name|read
argument_list|()
operator|>=
literal|0
condition|)
name|readFile
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|processResponse
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"TODO unimplemented"
argument_list|)
throw|;
comment|//TODO
block|}
block|}
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|//runs request
comment|//request.process(server); but we don't have a NamedList response
name|assertTrue
argument_list|(
name|readFile
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
