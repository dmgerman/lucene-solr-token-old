begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|util
operator|.
name|NamedList
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
name|PHPSerializedResponseWriter
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
name|common
operator|.
name|SolrDocument
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
name|SolrDocumentList
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
begin_comment
comment|/**   * Basic PHPS tests based on JSONWriterTest  *  */
end_comment
begin_class
DECL|class|TestPHPSerializedResponseWriter
specifier|public
class|class
name|TestPHPSerializedResponseWriter
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
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
annotation|@
name|Test
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"dummy"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|QueryResponseWriter
name|w
init|=
operator|new
name|PHPSerializedResponseWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|buf
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"data1"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"data2"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"data3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a:3:{s:5:\"data1\";s:5:\"hello\";s:5:\"data2\";i:42;s:5:\"data3\";b:1;}"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
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
DECL|method|testSolrDocuments
specifier|public
name|void
name|testSolrDocuments
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|QueryResponseWriter
name|w
init|=
operator|new
name|PHPSerializedResponseWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|buf
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|SolrDocument
name|d
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|SolrDocument
name|d1
init|=
name|d
decl_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"data1"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"data2"
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"data3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// multivalued fields:
comment|// map value
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nl
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|nl
operator|.
name|put
argument_list|(
literal|"data4.1"
argument_list|,
literal|"hello"
argument_list|)
expr_stmt|;
name|nl
operator|.
name|put
argument_list|(
literal|"data4.2"
argument_list|,
literal|"hashmap"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"data4"
argument_list|,
name|nl
argument_list|)
expr_stmt|;
comment|// array value
name|d
operator|.
name|addField
argument_list|(
literal|"data5"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"data5.1"
argument_list|,
literal|"data5.2"
argument_list|,
literal|"data5.3"
argument_list|)
argument_list|)
expr_stmt|;
comment|// adding one more document to test array indexes
name|d
operator|=
operator|new
name|SolrDocument
argument_list|()
expr_stmt|;
name|SolrDocument
name|d2
init|=
name|d
decl_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|sdl
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|sdl
operator|.
name|add
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|sdl
operator|.
name|add
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"response"
argument_list|,
name|sdl
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a:1:{s:8:\"response\";a:3:{s:8:\"numFound\";i:0;s:5:\"start\";i:0;s:4:\"docs\";a:2:{i:0;a:6:{s:2:\"id\";s:1:\"1\";s:5:\"data1\";s:5:\"hello\";s:5:\"data2\";i:42;s:5:\"data3\";b:1;s:5:\"data4\";a:2:{s:7:\"data4.2\";s:7:\"hashmap\";s:7:\"data4.1\";s:5:\"hello\";}s:5:\"data5\";a:3:{i:0;s:7:\"data5.1\";i:1;s:7:\"data5.2\";i:2;s:7:\"data5.3\";}}i:1;a:1:{s:2:\"id\";s:1:\"2\";}}}}"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
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
