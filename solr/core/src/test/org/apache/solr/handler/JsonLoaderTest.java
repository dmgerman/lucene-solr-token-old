begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|noggit
operator|.
name|JSONParser
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
name|SolrTestCaseJ4
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
name|SolrInputField
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
name|ContentStreamBase
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
name|update
operator|.
name|AddUpdateCommand
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
name|update
operator|.
name|CommitUpdateCommand
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
name|update
operator|.
name|DeleteUpdateCommand
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
name|update
operator|.
name|RollbackUpdateCommand
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
DECL|class|JsonLoaderTest
specifier|public
class|class
name|JsonLoaderTest
extends|extends
name|SolrTestCaseJ4
block|{
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
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|field|input
specifier|static
name|String
name|input
init|=
operator|(
literal|"{\n"
operator|+
literal|"\n"
operator|+
literal|"'add': {\n"
operator|+
literal|"  'doc': {\n"
operator|+
literal|"    'bool': true,\n"
operator|+
literal|"    'f0': 'v0',\n"
operator|+
literal|"    'f2': {\n"
operator|+
literal|"      'boost': 2.3,\n"
operator|+
literal|"      'value': 'test'\n"
operator|+
literal|"    },\n"
operator|+
literal|"    'array': [ 'aaa', 'bbb' ],\n"
operator|+
literal|"    'boosted': {\n"
operator|+
literal|"      'boost': 6.7,\n"
operator|+
literal|"      'value': [ 'aaa', 'bbb' ]\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"},\n"
operator|+
literal|"'add': {\n"
operator|+
literal|"  'commitWithin': 1234,\n"
operator|+
literal|"  'overwrite': false,\n"
operator|+
literal|"  'boost': 3.45,\n"
operator|+
literal|"  'doc': {\n"
operator|+
literal|"    'f1': 'v1',\n"
operator|+
literal|"    'f1': 'v2',\n"
operator|+
literal|"    'f2': null\n"
operator|+
literal|"  }\n"
operator|+
literal|"},\n"
operator|+
literal|"\n"
operator|+
literal|"'commit': {},\n"
operator|+
literal|"'optimize': { 'waitSearcher':false },\n"
operator|+
literal|"\n"
operator|+
literal|"'delete': { 'id':'ID' },\n"
operator|+
literal|"'delete': { 'query':'QUERY' },\n"
operator|+
literal|"'rollback': {}\n"
operator|+
literal|"\n"
operator|+
literal|"}\n"
operator|+
literal|""
operator|)
operator|.
name|replace
argument_list|(
literal|'\''
argument_list|,
literal|'"'
argument_list|)
decl_stmt|;
DECL|method|testParsing
specifier|public
name|void
name|testParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|BufferingRequestProcessor
name|p
init|=
operator|new
name|BufferingRequestProcessor
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|JsonLoader
name|loader
init|=
operator|new
name|JsonLoader
argument_list|(
name|req
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|loader
operator|.
name|load
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|p
operator|.
name|addCommands
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|AddUpdateCommand
name|add
init|=
name|p
operator|.
name|addCommands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|d
init|=
name|add
operator|.
name|solrDoc
decl_stmt|;
name|SolrInputField
name|f
init|=
name|d
operator|.
name|getField
argument_list|(
literal|"boosted"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6.7f
argument_list|,
name|f
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|f
operator|.
name|getValues
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|//
name|add
operator|=
name|p
operator|.
name|addCommands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|d
operator|=
name|add
operator|.
name|solrDoc
expr_stmt|;
name|f
operator|=
name|d
operator|.
name|getField
argument_list|(
literal|"f1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|f
operator|.
name|getValues
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3.45f
argument_list|,
name|d
operator|.
name|getDocumentBoost
argument_list|()
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|add
operator|.
name|overwrite
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|d
operator|.
name|getField
argument_list|(
literal|"f2"
argument_list|)
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// parse the commit commands
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|p
operator|.
name|commitCommands
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|CommitUpdateCommand
name|commit
init|=
name|p
operator|.
name|commitCommands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|commit
operator|.
name|optimize
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|commit
operator|.
name|waitSearcher
argument_list|)
expr_stmt|;
name|commit
operator|=
name|p
operator|.
name|commitCommands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|commit
operator|.
name|optimize
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|commit
operator|.
name|waitSearcher
argument_list|)
expr_stmt|;
comment|// DELETE COMMANDS
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|p
operator|.
name|deleteCommands
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DeleteUpdateCommand
name|delete
init|=
name|p
operator|.
name|deleteCommands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|delete
operator|.
name|id
argument_list|,
literal|"ID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|delete
operator|.
name|query
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|delete
operator|=
name|p
operator|.
name|deleteCommands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|delete
operator|.
name|id
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|delete
operator|.
name|query
argument_list|,
literal|"QUERY"
argument_list|)
expr_stmt|;
comment|// ROLLBACK COMMANDS
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|p
operator|.
name|rollbackCommands
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testSimpleFormat
specifier|public
name|void
name|testSimpleFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|str
init|=
literal|"[{'id':'1'},{'id':'2'}]"
operator|.
name|replace
argument_list|(
literal|'\''
argument_list|,
literal|'"'
argument_list|)
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"commitWithin"
argument_list|,
literal|"100"
argument_list|,
literal|"overwrite"
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|BufferingRequestProcessor
name|p
init|=
operator|new
name|BufferingRequestProcessor
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|JsonLoader
name|loader
init|=
operator|new
name|JsonLoader
argument_list|(
name|req
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|loader
operator|.
name|load
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|p
operator|.
name|addCommands
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|AddUpdateCommand
name|add
init|=
name|p
operator|.
name|addCommands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|d
init|=
name|add
operator|.
name|solrDoc
decl_stmt|;
name|SolrInputField
name|f
init|=
name|d
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|f
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|add
operator|.
name|commitWithin
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|add
operator|.
name|overwrite
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|add
operator|=
name|p
operator|.
name|addCommands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|d
operator|=
name|add
operator|.
name|solrDoc
expr_stmt|;
name|f
operator|=
name|d
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|f
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|add
operator|.
name|commitWithin
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|add
operator|.
name|overwrite
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testSimpleFormatInAdd
specifier|public
name|void
name|testSimpleFormatInAdd
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|str
init|=
literal|"{'add':[{'id':'1'},{'id':'2'}]}"
operator|.
name|replace
argument_list|(
literal|'\''
argument_list|,
literal|'"'
argument_list|)
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|BufferingRequestProcessor
name|p
init|=
operator|new
name|BufferingRequestProcessor
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|JsonLoader
name|loader
init|=
operator|new
name|JsonLoader
argument_list|(
name|req
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|loader
operator|.
name|load
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|p
operator|.
name|addCommands
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|AddUpdateCommand
name|add
init|=
name|p
operator|.
name|addCommands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|d
init|=
name|add
operator|.
name|solrDoc
decl_stmt|;
name|SolrInputField
name|f
init|=
name|d
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|f
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|add
operator|.
name|commitWithin
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|add
operator|.
name|overwrite
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|add
operator|=
name|p
operator|.
name|addCommands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|d
operator|=
name|add
operator|.
name|solrDoc
expr_stmt|;
name|f
operator|=
name|d
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|f
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|add
operator|.
name|commitWithin
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|add
operator|.
name|overwrite
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNullValues
specifier|public
name|void
name|testNullValues
parameter_list|()
throws|throws
name|Exception
block|{
name|updateJ
argument_list|(
literal|"[{'id':'10','foo_s':null,'foo2_s':['hi',null,'there']}]"
operator|.
name|replace
argument_list|(
literal|'\''
argument_list|,
literal|'"'
argument_list|)
argument_list|,
name|params
argument_list|(
literal|"commit"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:10"
argument_list|,
literal|"fl"
argument_list|,
literal|"foo_s,foo2_s"
argument_list|)
argument_list|,
literal|"/response/docs/[0]=={'foo2_s':['hi','there']}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_class
DECL|class|BufferingRequestProcessor
class|class
name|BufferingRequestProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|addCommands
name|List
argument_list|<
name|AddUpdateCommand
argument_list|>
name|addCommands
init|=
operator|new
name|ArrayList
argument_list|<
name|AddUpdateCommand
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|deleteCommands
name|List
argument_list|<
name|DeleteUpdateCommand
argument_list|>
name|deleteCommands
init|=
operator|new
name|ArrayList
argument_list|<
name|DeleteUpdateCommand
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|commitCommands
name|List
argument_list|<
name|CommitUpdateCommand
argument_list|>
name|commitCommands
init|=
operator|new
name|ArrayList
argument_list|<
name|CommitUpdateCommand
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|rollbackCommands
name|List
argument_list|<
name|RollbackUpdateCommand
argument_list|>
name|rollbackCommands
init|=
operator|new
name|ArrayList
argument_list|<
name|RollbackUpdateCommand
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|BufferingRequestProcessor
specifier|public
name|BufferingRequestProcessor
parameter_list|(
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|addCommands
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processDelete
specifier|public
name|void
name|processDelete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|deleteCommands
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processCommit
specifier|public
name|void
name|processCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|commitCommands
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processRollback
specifier|public
name|void
name|processRollback
parameter_list|(
name|RollbackUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|rollbackCommands
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing?
block|}
block|}
end_class
end_unit
