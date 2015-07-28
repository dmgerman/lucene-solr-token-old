begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|core
operator|.
name|CoreContainer
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
name|CoreDescriptor
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrRequestHandler
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
name|request
operator|.
name|SolrRequestInfo
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
name|response
operator|.
name|QueryResponseWriter
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
name|response
operator|.
name|SolrQueryResponse
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
name|search
operator|.
name|join
operator|.
name|TestScoreJoinQPNoScore
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
name|servlet
operator|.
name|DirectSolrConnection
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
begin_class
DECL|class|TestCrossCoreJoin
specifier|public
class|class
name|TestCrossCoreJoin
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|fromCore
specifier|private
specifier|static
name|SolrCore
name|fromCore
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
comment|//    initCore("solrconfig.xml","schema12.xml");
comment|// File testHome = createTempDir().toFile();
comment|// FileUtils.copyDirectory(getFile("solrj/solr"), testHome);
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|,
name|TEST_HOME
argument_list|()
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
specifier|final
name|CoreContainer
name|coreContainer
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
specifier|final
name|CoreDescriptor
name|toCoreDescriptor
init|=
name|coreContainer
operator|.
name|getCoreDescriptor
argument_list|(
literal|"collection1"
argument_list|)
decl_stmt|;
specifier|final
name|CoreDescriptor
name|fromCoreDescriptor
init|=
operator|new
name|CoreDescriptor
argument_list|(
literal|"fromCore"
argument_list|,
name|toCoreDescriptor
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|getSchemaName
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
block|}
decl_stmt|;
name|fromCore
operator|=
name|coreContainer
operator|.
name|create
argument_list|(
name|fromCoreDescriptor
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"name"
argument_list|,
literal|"john"
argument_list|,
literal|"title"
argument_list|,
literal|"Director"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Engineering"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"name"
argument_list|,
literal|"mark"
argument_list|,
literal|"title"
argument_list|,
literal|"VP"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Marketing"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"name"
argument_list|,
literal|"nancy"
argument_list|,
literal|"title"
argument_list|,
literal|"MTS"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Sales"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"name"
argument_list|,
literal|"dave"
argument_list|,
literal|"title"
argument_list|,
literal|"MTS"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Support"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Engineering"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"name"
argument_list|,
literal|"tina"
argument_list|,
literal|"title"
argument_list|,
literal|"VP"
argument_list|,
literal|"dept_s"
argument_list|,
literal|"Engineering"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|update
argument_list|(
name|fromCore
argument_list|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Engineering"
argument_list|,
literal|"text"
argument_list|,
literal|"These guys develop stuff"
argument_list|,
literal|"cat"
argument_list|,
literal|"dev"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|update
argument_list|(
name|fromCore
argument_list|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Marketing"
argument_list|,
literal|"text"
argument_list|,
literal|"These guys make you look good"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|update
argument_list|(
name|fromCore
argument_list|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Sales"
argument_list|,
literal|"text"
argument_list|,
literal|"These guys sell stuff"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|update
argument_list|(
name|fromCore
argument_list|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"13"
argument_list|,
literal|"dept_id_s"
argument_list|,
literal|"Support"
argument_list|,
literal|"text"
argument_list|,
literal|"These guys help customers"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|update
argument_list|(
name|fromCore
argument_list|,
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|update
specifier|public
specifier|static
name|String
name|update
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|xml
parameter_list|)
throws|throws
name|Exception
block|{
name|DirectSolrConnection
name|connection
init|=
operator|new
name|DirectSolrConnection
argument_list|(
name|core
argument_list|)
decl_stmt|;
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"/update"
argument_list|)
decl_stmt|;
return|return
name|connection
operator|.
name|request
argument_list|(
name|handler
argument_list|,
literal|null
argument_list|,
name|xml
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testJoin
specifier|public
name|void
name|testJoin
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestJoin
argument_list|(
literal|"{!join"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testScoreJoin
specifier|public
name|void
name|testScoreJoin
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestJoin
argument_list|(
literal|"{!join "
operator|+
name|TestScoreJoinQPNoScore
operator|.
name|whateverScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestJoin
name|void
name|doTestJoin
parameter_list|(
name|String
name|joinPrefix
parameter_list|)
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|joinPrefix
operator|+
literal|" from=dept_id_s to=dept_s fromIndex=fromCore}cat:dev"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"debugQuery"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"true"
else|:
literal|"false"
argument_list|)
argument_list|,
literal|"/response=={'numFound':3,'start':0,'docs':[{'id':'1'},{'id':'4'},{'id':'5'}]}"
argument_list|)
expr_stmt|;
comment|// find people that develop stuff - but limit via filter query to a name of "john"
comment|// this tests filters being pushed down to queries (SOLR-3062)
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|joinPrefix
operator|+
literal|" from=dept_id_s to=dept_s fromIndex=fromCore}cat:dev"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"fq"
argument_list|,
literal|"name:john"
argument_list|,
literal|"debugQuery"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"true"
else|:
literal|"false"
argument_list|)
argument_list|,
literal|"/response=={'numFound':1,'start':0,'docs':[{'id':'1'}]}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCoresAreDifferent
specifier|public
name|void
name|testCoresAreDifferent
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQEx
argument_list|(
literal|"schema12.xml"
operator|+
literal|" has no \"cat\" field"
argument_list|,
name|req
argument_list|(
literal|"cat:*"
argument_list|)
argument_list|,
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
specifier|final
name|LocalSolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|fromCore
argument_list|,
literal|"cat:*"
argument_list|,
literal|"lucene"
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|resp
init|=
name|query
argument_list|(
name|fromCore
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resp
argument_list|,
name|resp
operator|.
name|contains
argument_list|(
literal|"numFound=\"1\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resp
argument_list|,
name|resp
operator|.
name|contains
argument_list|(
literal|"<int name=\"id\">10</int>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|query
specifier|public
name|String
name|query
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|handler
init|=
literal|"standard"
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrRequestInfo
operator|.
name|setRequestInfo
argument_list|(
operator|new
name|SolrRequestInfo
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|core
operator|.
name|getRequestHandler
argument_list|(
name|handler
argument_list|)
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
name|rsp
operator|.
name|getException
argument_list|()
throw|;
block|}
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|(
literal|32000
argument_list|)
decl_stmt|;
name|QueryResponseWriter
name|responseWriter
init|=
name|core
operator|.
name|getQueryResponseWriter
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|responseWriter
operator|.
name|write
argument_list|(
name|sw
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|SolrRequestInfo
operator|.
name|clearRequestInfo
argument_list|()
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|AfterClass
DECL|method|nukeAll
specifier|public
specifier|static
name|void
name|nukeAll
parameter_list|()
block|{
name|fromCore
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
