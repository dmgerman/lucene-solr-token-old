begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|util
operator|.
name|*
import|;
end_import
begin_class
DECL|class|TestCollapseQParserPlugin
specifier|public
class|class
name|TestCollapseQParserPlugin
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
literal|"solrconfig-collapseqparser.xml"
argument_list|,
literal|"schema11.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCollapseQueries
specifier|public
name|void
name|testCollapseQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|doc
init|=
block|{
literal|"id"
block|,
literal|"1"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
literal|"group_s"
block|,
literal|"group1"
block|,
literal|"test_ti"
block|,
literal|"5"
block|,
literal|"test_tl"
block|,
literal|"10"
block|,
literal|"test_tf"
block|,
literal|"2000"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc1
init|=
block|{
literal|"id"
block|,
literal|"2"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
literal|"group_s"
block|,
literal|"group1"
block|,
literal|"test_ti"
block|,
literal|"50"
block|,
literal|"test_tl"
block|,
literal|"100"
block|,
literal|"test_tf"
block|,
literal|"200"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc1
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc2
init|=
block|{
literal|"id"
block|,
literal|"3"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
literal|"test_ti"
block|,
literal|"5000"
block|,
literal|"test_tl"
block|,
literal|"100"
block|,
literal|"test_tf"
block|,
literal|"200"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc2
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc3
init|=
block|{
literal|"id"
block|,
literal|"4"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
literal|"test_ti"
block|,
literal|"500"
block|,
literal|"test_tl"
block|,
literal|"1000"
block|,
literal|"test_tf"
block|,
literal|"2000"
block|}
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc3
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|//Test collapse by score
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field=group_s}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|,
literal|"//doc[./int[@name='test_ti']='50']"
argument_list|)
expr_stmt|;
comment|//Test collapse by score with elevation
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"YYYY"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field=group_s nullPolicy=collapse}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"defType"
argument_list|,
literal|"edismax"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"bf"
argument_list|,
literal|"field(test_ti)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"qf"
argument_list|,
literal|"term_s"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"qt"
argument_list|,
literal|"/elevate"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|,
literal|"//doc[./int[1][@name='test_ti']='5']"
argument_list|)
expr_stmt|;
comment|//Test collapse by min int field
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field=group_s min=test_ti}"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|,
literal|"//doc[./int[@name='test_ti']='5']"
argument_list|)
expr_stmt|;
comment|//Test collapse by max int field
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field=group_s max=test_ti}"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|,
literal|"//doc[./int[@name='test_ti']='50']"
argument_list|)
expr_stmt|;
comment|//Test collapse by min long field
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field=group_s min=test_tl}"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|,
literal|"//doc[./int[@name='test_ti']='5']"
argument_list|)
expr_stmt|;
comment|//Test collapse by max long field
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field=group_s max=test_tl}"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|,
literal|"//doc[./int[@name='test_ti']='50']"
argument_list|)
expr_stmt|;
comment|//Test collapse by min float field
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field=group_s min=test_tf}"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|,
literal|"//doc[./int[@name='test_ti']='50']"
argument_list|)
expr_stmt|;
comment|//Test collapse by min float field
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field=group_s max=test_tf}"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|,
literal|"//doc[./int[@name='test_ti']='5']"
argument_list|)
expr_stmt|;
comment|//Test nullPolicy expand
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field=group_s max=test_tf nullPolicy=expand}"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
comment|//Test nullPolicy collapse
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"test_ti:(500 5000)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!collapse field=group_s max=test_tf nullPolicy=collapse}"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|,
literal|"//doc[./int[@name='test_ti']='500']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit