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
name|XmlUpdateRequestHandler
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
name|XmlUpdateRequestHandler
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
argument_list|(
literal|null
argument_list|,
name|inputFactory
argument_list|)
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
DECL|method|testCommitWithin
specifier|public
name|void
name|testCommitWithin
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
argument_list|(
name|p
argument_list|,
name|inputFactory
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
name|add
operator|.
name|commitWithin
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
