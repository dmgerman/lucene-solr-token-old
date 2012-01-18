begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
package|;
end_package
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
name|handler
operator|.
name|component
operator|.
name|HighlightComponent
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
name|TestHarness
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
DECL|class|FastVectorHighlighterTest
specifier|public
class|class
name|FastVectorHighlighterTest
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
DECL|method|testConfig
specifier|public
name|void
name|testConfig
parameter_list|()
block|{
name|SolrHighlighter
name|highlighter
init|=
name|HighlightComponent
operator|.
name|getHighlighter
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
decl_stmt|;
comment|// Make sure we loaded one fragListBuilder
name|SolrFragListBuilder
name|solrFlbNull
init|=
name|highlighter
operator|.
name|fragListBuilders
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|SolrFragListBuilder
name|solrFlbEmpty
init|=
name|highlighter
operator|.
name|fragListBuilders
operator|.
name|get
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|SolrFragListBuilder
name|solrFlbSimple
init|=
name|highlighter
operator|.
name|fragListBuilders
operator|.
name|get
argument_list|(
literal|"simple"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|solrFlbNull
argument_list|,
name|solrFlbEmpty
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|solrFlbNull
operator|instanceof
name|SimpleFragListBuilder
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|solrFlbSimple
operator|instanceof
name|SimpleFragListBuilder
argument_list|)
expr_stmt|;
comment|// Make sure we loaded two fragmentsBuilders
name|SolrFragmentsBuilder
name|solrFbNull
init|=
name|highlighter
operator|.
name|fragmentsBuilders
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|SolrFragmentsBuilder
name|solrFbEmpty
init|=
name|highlighter
operator|.
name|fragmentsBuilders
operator|.
name|get
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|SolrFragmentsBuilder
name|solrFbSimple
init|=
name|highlighter
operator|.
name|fragmentsBuilders
operator|.
name|get
argument_list|(
literal|"simple"
argument_list|)
decl_stmt|;
name|SolrFragmentsBuilder
name|solrFbSO
init|=
name|highlighter
operator|.
name|fragmentsBuilders
operator|.
name|get
argument_list|(
literal|"scoreOrder"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|solrFbNull
argument_list|,
name|solrFbEmpty
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|solrFbNull
operator|instanceof
name|SimpleFragmentsBuilder
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|solrFbSimple
operator|instanceof
name|SimpleFragmentsBuilder
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|solrFbSO
operator|instanceof
name|ScoreOrderFragmentsBuilder
argument_list|)
expr_stmt|;
comment|// Make sure we loaded two boundaryScanners
name|SolrBoundaryScanner
name|solrBsNull
init|=
name|highlighter
operator|.
name|boundaryScanners
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|SolrBoundaryScanner
name|solrBsEmpty
init|=
name|highlighter
operator|.
name|boundaryScanners
operator|.
name|get
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|SolrBoundaryScanner
name|solrBsSimple
init|=
name|highlighter
operator|.
name|boundaryScanners
operator|.
name|get
argument_list|(
literal|"simple"
argument_list|)
decl_stmt|;
name|SolrBoundaryScanner
name|solrBsBI
init|=
name|highlighter
operator|.
name|boundaryScanners
operator|.
name|get
argument_list|(
literal|"breakIterator"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|solrBsNull
argument_list|,
name|solrBsEmpty
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|solrBsNull
operator|instanceof
name|SimpleBoundaryScanner
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|solrBsSimple
operator|instanceof
name|SimpleBoundaryScanner
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|solrBsBI
operator|instanceof
name|BreakIteratorBoundaryScanner
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
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
name|args
operator|.
name|put
argument_list|(
literal|"hl"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"tv_text"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.snippets"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"hl.useFastVectorHighlighter"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|TestHarness
operator|.
name|LocalRequestFactory
name|sumLRF
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|200
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"tv_text"
argument_list|,
literal|"basic fast vector highlighter test"
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Basic summarization"
argument_list|,
name|sumLRF
operator|.
name|makeRequest
argument_list|(
literal|"tv_text:vector"
argument_list|)
argument_list|,
literal|"//lst[@name='highlighting']/lst[@name='1']"
argument_list|,
literal|"//lst[@name='1']/arr[@name='tv_text']/str[.='basic fast<em>vector</em> highlighter test']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
