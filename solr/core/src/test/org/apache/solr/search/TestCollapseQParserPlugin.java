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
name|SolrException
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
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntOpenHashSet
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|assertU
argument_list|(
name|commit
argument_list|()
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
name|assertU
argument_list|(
name|commit
argument_list|()
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
name|String
index|[]
name|doc4
init|=
block|{
literal|"id"
block|,
literal|"5"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
literal|"group_s"
block|,
literal|"group2"
block|,
literal|"test_ti"
block|,
literal|"4"
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
name|doc4
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|doc5
init|=
block|{
literal|"id"
block|,
literal|"6"
block|,
literal|"term_s"
block|,
literal|"YYYY"
block|,
literal|"group_s"
block|,
literal|"group2"
block|,
literal|"test_ti"
block|,
literal|"10"
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
name|doc5
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|//Test collapse by score and following sort by score
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
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|// SOLR-5544 test ordering with empty sort param
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
literal|"{!collapse field=group_s nullPolicy=expand min=test_tf}"
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
literal|"sort"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=4]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='3.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[4]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|// Test value source collapse criteria
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
literal|"{!collapse field=group_s nullPolicy=collapse min=field(test_ti)}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"test_ti desc"
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
literal|"//result/doc[1]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='5.0']"
argument_list|)
expr_stmt|;
comment|// Test value source collapse criteria with cscore function
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
literal|"{!collapse field=group_s nullPolicy=collapse min=cscore()}"
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
literal|"*[count(//doc)=3]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='5.0']"
argument_list|)
expr_stmt|;
comment|// Test value source collapse criteria with compound cscore function
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
literal|"{!collapse field=group_s nullPolicy=collapse min=sum(cscore(),field(test_ti))}"
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
literal|"*[count(//doc)=3]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='5.0']"
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
literal|"*[count(//doc)=4]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='3.0']"
argument_list|,
literal|"//result/doc[4]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|//Test SOLR-5773 with score collapse criteria
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
name|params
operator|.
name|add
argument_list|(
literal|"elevateIds"
argument_list|,
literal|"1,5"
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
literal|"//result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='3.0']"
argument_list|)
expr_stmt|;
comment|//Test SOLR-5773 with max field collapse criteria
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
literal|"{!collapse field=group_s min=test_ti nullPolicy=collapse}"
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
name|params
operator|.
name|add
argument_list|(
literal|"elevateIds"
argument_list|,
literal|"1,5"
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
literal|"//result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='4.0']"
argument_list|)
expr_stmt|;
comment|//Test SOLR-5773 elevating documents with null group
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
name|params
operator|.
name|add
argument_list|(
literal|"elevateIds"
argument_list|,
literal|"3,4"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=4]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='3.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[4]/float[@name='id'][.='6.0']"
argument_list|)
expr_stmt|;
comment|//Test collapse by min int field and sort
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
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|)
expr_stmt|;
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
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='5.0']"
argument_list|)
expr_stmt|;
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
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"test_tl asc,id desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|)
expr_stmt|;
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
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"score desc,id asc"
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
literal|"field(id)"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
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
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"test_ti asc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='6.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='2.0']"
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
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"test_ti desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='1.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='5.0']"
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
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"test_ti desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='6.0']"
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
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"test_ti desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='2.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='6.0']"
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
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"test_ti asc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
argument_list|)
expr_stmt|;
comment|//Test collapse by min float field sort by score
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
literal|"field(id)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"score, id"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!tag=test}term_s:YYYY"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet.field"
argument_list|,
literal|"{!ex=test}term_s"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='1.0']"
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
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=4]"
argument_list|,
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='3.0']"
argument_list|,
literal|"//result/doc[4]/float[@name='id'][.='1.0']"
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
literal|"*:*"
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
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"id desc"
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
literal|"//result/doc[1]/float[@name='id'][.='5.0']"
argument_list|,
literal|"//result/doc[2]/float[@name='id'][.='4.0']"
argument_list|,
literal|"//result/doc[3]/float[@name='id'][.='1.0']"
argument_list|)
expr_stmt|;
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
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!tag=test_ti}id:5"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet.field"
argument_list|,
literal|"{!ex=test_ti}test_ti"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet.mincount"
argument_list|,
literal|"1"
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
literal|"*[count(//lst[@name='facet_fields']/lst[@name='test_ti']/int)=2]"
argument_list|)
expr_stmt|;
comment|// SOLR-5230 - ensure CollapsingFieldValueCollector.finish() is called
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
literal|"{!collapse field=group_s}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"group"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"group.field"
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMissingFieldParam
specifier|public
name|void
name|testMissingFieldParam
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"{!collapse}"
argument_list|)
expr_stmt|;
name|assertQEx
argument_list|(
literal|"It should respond with a bad request when the 'field' param is missing"
argument_list|,
name|req
argument_list|(
name|params
argument_list|)
argument_list|,
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
