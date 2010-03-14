begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|CommonsHttpSolrServer
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
name|core
operator|.
name|SolrCore
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
name|schema
operator|.
name|SchemaField
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import
begin_comment
comment|/**  * TODO? perhaps use:  *  http://docs.codehaus.org/display/JETTY/ServletTester  * rather then open a real connection?  *   * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrExampleJettyTest
specifier|public
class|class
name|SolrExampleJettyTest
extends|extends
name|SolrExampleTests
block|{
DECL|field|server
name|SolrServer
name|server
decl_stmt|;
DECL|field|jetty
name|JettySolrRunner
name|jetty
decl_stmt|;
DECL|field|port
name|int
name|port
init|=
literal|0
decl_stmt|;
DECL|field|context
specifier|static
specifier|final
name|String
name|context
init|=
literal|"/example"
decl_stmt|;
DECL|method|setUp
annotation|@
name|Override
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
name|jetty
operator|=
operator|new
name|JettySolrRunner
argument_list|(
name|context
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
name|port
operator|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Assigned Port#"
operator|+
name|port
argument_list|)
expr_stmt|;
name|server
operator|=
name|this
operator|.
name|createNewSolrServer
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// stop the server
block|}
annotation|@
name|Override
DECL|method|getSolrServer
specifier|protected
name|SolrServer
name|getSolrServer
parameter_list|()
block|{
return|return
name|server
return|;
block|}
annotation|@
name|Override
DECL|method|createNewSolrServer
specifier|protected
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
name|CommonsHttpSolrServer
name|s
init|=
operator|new
name|CommonsHttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|s
operator|.
name|setConnectionTimeout
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// 1/10th sec
name|s
operator|.
name|setDefaultMaxConnectionsPerHost
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|s
operator|.
name|setMaxTotalConnections
argument_list|(
literal|100
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
DECL|method|testBadSetup
specifier|public
name|void
name|testBadSetup
parameter_list|()
block|{
try|try
block|{
comment|// setup the server...
name|String
name|url
init|=
literal|"http://localhost/?core=xxx"
decl_stmt|;
name|CommonsHttpSolrServer
name|s
init|=
operator|new
name|CommonsHttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"CommonsHttpSolrServer should not allow a path with a parameter: "
operator|+
name|s
operator|.
name|getBaseURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class
end_unit
