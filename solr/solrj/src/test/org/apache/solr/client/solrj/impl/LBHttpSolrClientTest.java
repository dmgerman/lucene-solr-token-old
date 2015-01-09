begin_unit
begin_comment
comment|/**  *   */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.impl
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
operator|.
name|impl
package|;
end_package
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
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
name|CloseableHttpClient
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
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
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
begin_comment
comment|/**  * Test the LBHttpSolrClient.  */
end_comment
begin_class
DECL|class|LBHttpSolrClientTest
specifier|public
class|class
name|LBHttpSolrClientTest
block|{
comment|/**    * Test method for {@link LBHttpSolrClient#LBHttpSolrClient(org.apache.http.client.HttpClient, org.apache.solr.client.solrj.ResponseParser, java.lang.String[])}.    *     * Validate that the parser passed in is used in the<code>HttpSolrClient</code> instances created.    */
annotation|@
name|Test
DECL|method|testLBHttpSolrClientHttpClientResponseParserStringArray
specifier|public
name|void
name|testLBHttpSolrClientHttpClientResponseParserStringArray
parameter_list|()
throws|throws
name|IOException
block|{
name|CloseableHttpClient
name|httpClient
init|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
name|LBHttpSolrClient
name|testClient
init|=
operator|new
name|LBHttpSolrClient
argument_list|(
name|httpClient
argument_list|,
operator|(
name|ResponseParser
operator|)
literal|null
argument_list|)
decl_stmt|;
name|HttpSolrClient
name|httpSolrClient
init|=
name|testClient
operator|.
name|makeSolrClient
argument_list|(
literal|"http://127.0.0.1:8080"
argument_list|)
decl_stmt|;
try|try
block|{
name|assertNull
argument_list|(
literal|"Generated server should have null parser."
argument_list|,
name|httpSolrClient
operator|.
name|getParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|httpSolrClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|testClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|httpClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|ResponseParser
name|parser
init|=
operator|new
name|BinaryResponseParser
argument_list|()
decl_stmt|;
name|httpClient
operator|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
expr_stmt|;
name|testClient
operator|=
operator|new
name|LBHttpSolrClient
argument_list|(
name|httpClient
argument_list|,
name|parser
argument_list|)
expr_stmt|;
name|httpSolrClient
operator|=
name|testClient
operator|.
name|makeSolrClient
argument_list|(
literal|"http://127.0.0.1:8080"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid parser passed to generated server."
argument_list|,
name|parser
argument_list|,
name|httpSolrClient
operator|.
name|getParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|httpSolrClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|testClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|httpClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
