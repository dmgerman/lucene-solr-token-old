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
name|commons
operator|.
name|lang
operator|.
name|ObjectUtils
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
name|handler
operator|.
name|loader
operator|.
name|XMLLoader
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
name|processor
operator|.
name|BufferingRequestProcessor
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
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import
begin_class
DECL|class|XmlUpdateRequestHandlerTest
specifier|public
class|class
name|XmlUpdateRequestHandlerTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|inputFactory
specifier|private
specifier|static
name|XMLInputFactory
name|inputFactory
init|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
DECL|field|handler
specifier|protected
specifier|static
name|UpdateRequestHandler
name|handler
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
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|UpdateRequestHandler
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadDoc
specifier|public
name|void
name|testReadDoc
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
literal|"<doc boost=\"5.5\">"
operator|+
literal|"<field name=\"id\" boost=\"2.2\">12345</field>"
operator|+
literal|"<field name=\"name\">kitten</field>"
operator|+
literal|"<field name=\"cat\" boost=\"3\">aaa</field>"
operator|+
literal|"<field name=\"cat\" boost=\"4\">bbb</field>"
operator|+
literal|"<field name=\"cat\" boost=\"5\">bbb</field>"
operator|+
literal|"<field name=\"ab\">a&amp;b</field>"
operator|+
literal|"</doc>"
decl_stmt|;
name|XMLStreamReader
name|parser
init|=
name|inputFactory
operator|.
name|createXMLStreamReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// read the START document...
comment|//null for the processor is all right here
name|XMLLoader
name|loader
init|=
operator|new
name|XMLLoader
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
name|loader
operator|.
name|readDoc
argument_list|(
name|parser
argument_list|)
decl_stmt|;
comment|// Read boosts
name|assertEquals
argument_list|(
literal|5.5f
argument_list|,
name|doc
operator|.
name|getDocumentBoost
argument_list|()
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0f
argument_list|,
name|doc
operator|.
name|getField
argument_list|(
literal|"name"
argument_list|)
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.2f
argument_list|,
name|doc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
comment|// Boost is the product of each value
name|assertEquals
argument_list|(
operator|(
literal|3
operator|*
literal|4
operator|*
literal|5.0f
operator|)
argument_list|,
name|doc
operator|.
name|getField
argument_list|(
literal|"cat"
argument_list|)
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
comment|// Read values
name|assertEquals
argument_list|(
literal|"12345"
argument_list|,
name|doc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"kitten"
argument_list|,
name|doc
operator|.
name|getField
argument_list|(
literal|"name"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a&b"
argument_list|,
name|doc
operator|.
name|getField
argument_list|(
literal|"ab"
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// read something with escaped characters
name|Collection
argument_list|<
name|Object
argument_list|>
name|out
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"cat"
argument_list|)
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[aaa, bbb, bbb]"
argument_list|,
name|out
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRequestParams
specifier|public
name|void
name|testRequestParams
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
literal|"<add>"
operator|+
literal|"<doc>"
operator|+
literal|"<field name=\"id\">12345</field>"
operator|+
literal|"<field name=\"name\">kitten</field>"
operator|+
literal|"</doc>"
operator|+
literal|"</add>"
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
name|XMLLoader
name|loader
init|=
operator|new
name|XMLLoader
argument_list|()
operator|.
name|init
argument_list|(
literal|null
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
name|xml
argument_list|)
argument_list|,
name|p
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
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|add
operator|.
name|commitWithin
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
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadDelete
specifier|public
name|void
name|testReadDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
literal|"<update>"
operator|+
literal|"<delete>"
operator|+
literal|"<query>id:150</query>"
operator|+
literal|"<id>150</id>"
operator|+
literal|"<id>200</id>"
operator|+
literal|"<query>id:200</query>"
operator|+
literal|"</delete>"
operator|+
literal|"<delete commitWithin=\"500\">"
operator|+
literal|"<query>id:150</query>"
operator|+
literal|"</delete>"
operator|+
literal|"<delete>"
operator|+
literal|"<id>150</id>"
operator|+
literal|"</delete>"
operator|+
literal|"</update>"
decl_stmt|;
name|MockUpdateRequestProcessor
name|p
init|=
operator|new
name|MockUpdateRequestProcessor
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|p
operator|.
name|expectDelete
argument_list|(
literal|null
argument_list|,
literal|"id:150"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|p
operator|.
name|expectDelete
argument_list|(
literal|"150"
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|p
operator|.
name|expectDelete
argument_list|(
literal|"200"
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|p
operator|.
name|expectDelete
argument_list|(
literal|null
argument_list|,
literal|"id:200"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|p
operator|.
name|expectDelete
argument_list|(
literal|null
argument_list|,
literal|"id:150"
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|p
operator|.
name|expectDelete
argument_list|(
literal|"150"
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|XMLLoader
name|loader
init|=
operator|new
name|XMLLoader
argument_list|()
operator|.
name|init
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|loader
operator|.
name|load
argument_list|(
name|req
argument_list|()
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|xml
argument_list|)
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|p
operator|.
name|assertNoCommandsPending
argument_list|()
expr_stmt|;
block|}
DECL|class|MockUpdateRequestProcessor
specifier|private
class|class
name|MockUpdateRequestProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|deleteCommands
specifier|private
name|Queue
argument_list|<
name|DeleteUpdateCommand
argument_list|>
name|deleteCommands
init|=
operator|new
name|LinkedList
argument_list|<
name|DeleteUpdateCommand
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|MockUpdateRequestProcessor
specifier|public
name|MockUpdateRequestProcessor
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
DECL|method|expectDelete
specifier|public
name|void
name|expectDelete
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|query
parameter_list|,
name|int
name|commitWithin
parameter_list|)
block|{
name|DeleteUpdateCommand
name|cmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|cmd
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|cmd
operator|.
name|commitWithin
operator|=
name|commitWithin
expr_stmt|;
name|deleteCommands
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNoCommandsPending
specifier|public
name|void
name|assertNoCommandsPending
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|deleteCommands
operator|.
name|isEmpty
argument_list|()
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
name|DeleteUpdateCommand
name|expected
init|=
name|deleteCommands
operator|.
name|poll
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Unexpected delete command: ["
operator|+
name|cmd
operator|+
literal|"]"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected ["
operator|+
name|expected
operator|+
literal|"] but found ["
operator|+
name|cmd
operator|+
literal|"]"
argument_list|,
name|ObjectUtils
operator|.
name|equals
argument_list|(
name|expected
operator|.
name|id
argument_list|,
name|cmd
operator|.
name|id
argument_list|)
operator|&&
name|ObjectUtils
operator|.
name|equals
argument_list|(
name|expected
operator|.
name|query
argument_list|,
name|cmd
operator|.
name|query
argument_list|)
operator|&&
name|expected
operator|.
name|commitWithin
operator|==
name|cmd
operator|.
name|commitWithin
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
