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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|UpdateResponse
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
begin_comment
comment|/**  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|LargeVolumeTestBase
specifier|public
specifier|abstract
class|class
name|LargeVolumeTestBase
extends|extends
name|SolrExampleTestBase
block|{
DECL|field|gserver
name|SolrServer
name|gserver
init|=
literal|null
decl_stmt|;
comment|// for real load testing, make these numbers bigger
DECL|field|numdocs
specifier|static
specifier|final
name|int
name|numdocs
init|=
literal|100
decl_stmt|;
comment|//1000 * 1000;
DECL|field|threadCount
specifier|static
specifier|final
name|int
name|threadCount
init|=
literal|5
decl_stmt|;
DECL|method|testMultiThreaded
specifier|public
name|void
name|testMultiThreaded
parameter_list|()
throws|throws
name|Exception
block|{
name|gserver
operator|=
name|this
operator|.
name|getSolrServer
argument_list|()
expr_stmt|;
name|DocThread
index|[]
name|threads
init|=
operator|new
name|DocThread
index|[
name|threadCount
index|]
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
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|DocThread
argument_list|(
literal|"T"
operator|+
name|i
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|setName
argument_list|(
literal|"DocThread-"
operator|+
name|i
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Started thread: "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|query
argument_list|(
name|threadCount
operator|*
name|numdocs
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
block|}
DECL|method|query
specifier|private
name|void
name|query
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|QueryResponse
name|response
init|=
name|gserver
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|DocThread
specifier|public
class|class
name|DocThread
extends|extends
name|Thread
block|{
DECL|field|tserver
specifier|final
name|SolrServer
name|tserver
decl_stmt|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|DocThread
specifier|public
name|DocThread
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|tserver
operator|=
name|createNewSolrServer
argument_list|()
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|UpdateResponse
name|resp
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
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
name|numdocs
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|%
literal|200
operator|==
literal|0
condition|)
block|{
name|resp
operator|=
name|tserver
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
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
name|docs
operator|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|%
literal|5000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getName
argument_list|()
operator|+
literal|" - Committing "
operator|+
name|i
argument_list|)
expr_stmt|;
name|resp
operator|=
name|tserver
operator|.
name|commit
argument_list|()
expr_stmt|;
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
block|}
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
name|name
operator|+
name|i
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"cat"
argument_list|,
literal|"foocat"
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
name|resp
operator|=
name|tserver
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
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
name|resp
operator|=
name|tserver
operator|.
name|commit
argument_list|()
expr_stmt|;
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
name|resp
operator|=
name|tserver
operator|.
name|optimize
argument_list|()
expr_stmt|;
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
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|getName
argument_list|()
operator|+
literal|"---"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
