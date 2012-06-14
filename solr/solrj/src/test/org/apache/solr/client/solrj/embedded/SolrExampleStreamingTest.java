begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.embedded
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
name|embedded
package|;
end_package
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
name|SolrExampleTests
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
name|ConcurrentUpdateSolrServer
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
name|XMLResponseParser
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
name|RequestWriter
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
begin_comment
comment|/**  *   *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrExampleStreamingTest
specifier|public
class|class
name|SolrExampleStreamingTest
extends|extends
name|SolrExampleTests
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
annotation|@
name|Override
DECL|method|createNewSolrServer
specifier|public
name|SolrServer
name|createNewSolrServer
parameter_list|()
block|{
try|try
block|{
comment|// setup the server...
name|String
name|url
init|=
literal|"http://localhost:"
operator|+
name|port
operator|+
name|context
decl_stmt|;
comment|// smaller queue size hits locks more often
name|ConcurrentUpdateSolrServer
name|s
init|=
operator|new
name|ConcurrentUpdateSolrServer
argument_list|(
name|url
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
block|{
specifier|public
name|Throwable
name|lastError
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|handleError
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|lastError
operator|=
name|ex
expr_stmt|;
block|}
block|}
decl_stmt|;
name|s
operator|.
name|setParser
argument_list|(
operator|new
name|XMLResponseParser
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|.
name|setRequestWriter
argument_list|(
operator|new
name|RequestWriter
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
