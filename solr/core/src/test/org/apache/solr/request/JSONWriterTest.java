begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|CommonParams
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
name|search
operator|.
name|ReturnFields
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
name|JSONResponseWriter
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
name|response
operator|.
name|PythonResponseWriter
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
name|RubyResponseWriter
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
name|SolrReturnFields
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
comment|/** Test some aspects of JSON/python writer output (very incomplete)  *  */
end_comment
begin_class
DECL|class|JSONWriterTest
specifier|public
class|class
name|JSONWriterTest
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
DECL|method|jsonEq
specifier|private
name|void
name|jsonEq
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|received
parameter_list|)
block|{
name|expected
operator|=
name|expected
operator|.
name|trim
argument_list|()
expr_stmt|;
name|received
operator|=
name|received
operator|.
name|trim
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|received
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTypes
specifier|public
name|void
name|testTypes
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
name|PythonResponseWriter
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
name|Float
operator|.
name|NaN
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"data2"
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"data3"
argument_list|,
name|Float
operator|.
name|POSITIVE_INFINITY
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
name|jsonEq
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
literal|"{'data1':float('NaN'),'data2':-float('Inf'),'data3':float('Inf')}"
argument_list|)
expr_stmt|;
name|w
operator|=
operator|new
name|RubyResponseWriter
argument_list|()
expr_stmt|;
name|buf
operator|=
operator|new
name|StringWriter
argument_list|()
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
name|jsonEq
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
literal|"{'data1'=>(0.0/0.0),'data2'=>-(1.0/0.0),'data3'=>(1.0/0.0)}"
argument_list|)
expr_stmt|;
name|w
operator|=
operator|new
name|JSONResponseWriter
argument_list|()
expr_stmt|;
name|buf
operator|=
operator|new
name|StringWriter
argument_list|()
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
name|jsonEq
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
literal|"{\"data1\":\"NaN\",\"data2\":\"-Infinity\",\"data3\":\"Infinity\"}"
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
DECL|method|testJSON
specifier|public
name|void
name|testJSON
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"wt"
argument_list|,
literal|"json"
argument_list|,
literal|"json.nl"
argument_list|,
literal|"arrarr"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|JSONResponseWriter
name|w
init|=
operator|new
name|JSONResponseWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|buf
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"data1"
argument_list|,
literal|"he\u2028llo\u2029!"
argument_list|)
expr_stmt|;
comment|// make sure that 2028 and 2029 are both escaped (they are illegal in javascript)
name|nl
operator|.
name|add
argument_list|(
literal|null
argument_list|,
literal|42
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"nl"
argument_list|,
name|nl
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"byte"
argument_list|,
name|Byte
operator|.
name|valueOf
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"short"
argument_list|,
name|Short
operator|.
name|valueOf
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"bytes"
argument_list|,
literal|"abc"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
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
name|jsonEq
argument_list|(
literal|"{\"nl\":[[\"data1\",\"he\\u2028llo\\u2029!\"],[null,42]],\"byte\":-3,\"short\":-4,\"bytes\":\"YWJj\"}"
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
DECL|method|testJSONSolrDocument
specifier|public
name|void
name|testJSONSolrDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"json"
argument_list|,
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"id,score"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|JSONResponseWriter
name|w
init|=
operator|new
name|JSONResponseWriter
argument_list|()
decl_stmt|;
name|ReturnFields
name|returnFields
init|=
operator|new
name|SolrReturnFields
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|setReturnFields
argument_list|(
name|returnFields
argument_list|)
expr_stmt|;
name|StringWriter
name|buf
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|SolrDocument
name|solrDoc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|solrDoc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|solrDoc
operator|.
name|addField
argument_list|(
literal|"subject"
argument_list|,
literal|"hello2"
argument_list|)
expr_stmt|;
name|solrDoc
operator|.
name|addField
argument_list|(
literal|"title"
argument_list|,
literal|"hello3"
argument_list|)
expr_stmt|;
name|solrDoc
operator|.
name|addField
argument_list|(
literal|"score"
argument_list|,
literal|"0.7"
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|list
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|list
operator|.
name|setNumFound
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|list
operator|.
name|setStart
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|list
operator|.
name|setMaxScore
argument_list|(
literal|0.7f
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|solrDoc
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"response"
argument_list|,
name|list
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
name|String
name|result
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"response contains unexpected fields: "
operator|+
name|result
argument_list|,
name|result
operator|.
name|contains
argument_list|(
literal|"hello"
argument_list|)
operator|||
name|result
operator|.
name|contains
argument_list|(
literal|"\"subject\""
argument_list|)
operator|||
name|result
operator|.
name|contains
argument_list|(
literal|"\"title\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"response doesn't contain expected fields: "
operator|+
name|result
argument_list|,
name|result
operator|.
name|contains
argument_list|(
literal|"\"id\""
argument_list|)
operator|&&
name|result
operator|.
name|contains
argument_list|(
literal|"\"score\""
argument_list|)
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
