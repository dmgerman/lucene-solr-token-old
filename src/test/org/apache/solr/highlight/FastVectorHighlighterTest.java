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
name|util
operator|.
name|AbstractSolrTestCase
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
begin_class
DECL|class|FastVectorHighlighterTest
specifier|public
class|class
name|FastVectorHighlighterTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
annotation|@
name|Override
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
annotation|@
name|Override
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
DECL|method|testConfig
specifier|public
name|void
name|testConfig
parameter_list|()
block|{
name|SolrHighlighter
name|highlighter
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getHighlighter
argument_list|()
decl_stmt|;
comment|// Make sure we loaded the one fragListBuilder
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
comment|// Make sure we loaded the one fragmentsBuilder
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
name|solrFbColored
init|=
name|highlighter
operator|.
name|fragmentsBuilders
operator|.
name|get
argument_list|(
literal|"colored"
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
name|ScoreOrderFragmentsBuilder
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|solrFbColored
operator|instanceof
name|MultiColoredScoreOrderFragmentsBuilder
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|solrFbSO
operator|instanceof
name|ScoreOrderFragmentsBuilder
argument_list|)
expr_stmt|;
block|}
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
literal|"//lst[@name='1']/arr[@name='tv_text']/str[.=' fast<b>vector</b> highlighter test']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
