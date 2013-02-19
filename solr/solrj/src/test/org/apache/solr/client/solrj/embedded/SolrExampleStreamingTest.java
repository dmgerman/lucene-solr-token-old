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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|UpdateRequest
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
name|ExternalPaths
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|ArrayList
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
name|After
import|;
end_import
begin_comment
comment|/**  *   *  * @since solr 1.3  */
end_comment
begin_class
annotation|@
name|Slow
DECL|class|SolrExampleStreamingTest
specifier|public
class|class
name|SolrExampleStreamingTest
extends|extends
name|SolrExampleTests
block|{
DECL|field|handledException
specifier|protected
name|Throwable
name|handledException
init|=
literal|null
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
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
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
name|handledException
operator|=
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
DECL|method|testWaitOptions
specifier|public
name|void
name|testWaitOptions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// SOLR-3903
specifier|final
name|List
argument_list|<
name|Throwable
argument_list|>
name|failures
init|=
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|ConcurrentUpdateSolrServer
name|s
init|=
operator|new
name|ConcurrentUpdateSolrServer
argument_list|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
block|{
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
name|failures
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|int
name|docId
init|=
literal|42
decl_stmt|;
for|for
control|(
name|UpdateRequest
operator|.
name|ACTION
name|action
range|:
name|EnumSet
operator|.
name|allOf
argument_list|(
name|UpdateRequest
operator|.
name|ACTION
operator|.
name|class
argument_list|)
control|)
block|{
for|for
control|(
name|boolean
name|waitSearch
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
control|)
block|{
for|for
control|(
name|boolean
name|waitFlush
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
control|)
block|{
name|UpdateRequest
name|updateRequest
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|document
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|docId
operator|++
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|updateRequest
operator|.
name|setAction
argument_list|(
name|action
argument_list|,
name|waitSearch
argument_list|,
name|waitFlush
argument_list|)
expr_stmt|;
name|s
operator|.
name|request
argument_list|(
name|updateRequest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|s
operator|.
name|commit
argument_list|()
expr_stmt|;
name|s
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
name|s
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
literal|0
operator|!=
name|failures
operator|.
name|size
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|failures
operator|.
name|size
argument_list|()
operator|+
literal|" Unexpected Exception, starting with..."
argument_list|,
literal|null
argument_list|,
name|failures
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
