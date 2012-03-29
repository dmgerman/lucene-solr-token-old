begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|DefaultHttpClient
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|conn
operator|.
name|tsccm
operator|.
name|ThreadSafeClientConnManager
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
begin_comment
comment|/**  *   *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrExceptionTest
specifier|public
class|class
name|SolrExceptionTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSolrException
specifier|public
name|void
name|testSolrException
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// test a connection to a solr server that probably doesn't exist
comment|// this is a very simple test and most of the test should be considered verified
comment|// if the compiler won't let you by without the try/catch
name|boolean
name|gotExpectedError
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// switched to a local address to avoid going out on the net, ns lookup issues, etc.
comment|// set a 1ms timeout to let the connection fail faster.
name|DefaultHttpClient
name|httpClient
init|=
operator|new
name|DefaultHttpClient
argument_list|(
operator|new
name|ThreadSafeClientConnManager
argument_list|()
argument_list|)
decl_stmt|;
name|httpClient
operator|.
name|getParams
argument_list|()
operator|.
name|setIntParameter
argument_list|(
literal|"http.connection.timeout"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|SolrServer
name|client
init|=
operator|new
name|HttpSolrServer
argument_list|(
literal|"http://[ff01::114]:11235/solr/"
argument_list|,
name|httpClient
argument_list|)
decl_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"test123"
argument_list|)
decl_stmt|;
name|client
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|sse
parameter_list|)
block|{
name|gotExpectedError
operator|=
literal|true
expr_stmt|;
comment|/***       assertTrue(UnknownHostException.class == sse.getRootCause().getClass()               //If one is using OpenDNS, then you don't get UnknownHostException, instead you get back that the query couldn't execute               || (sse.getRootCause().getClass() == SolrException.class&& ((SolrException) sse.getRootCause()).code() == 302&& sse.getMessage().equals("Error executing query")));       ***/
block|}
name|assertTrue
argument_list|(
name|gotExpectedError
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
