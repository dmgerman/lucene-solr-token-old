begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
comment|/** Simple tests for SimpleQParserPlugin. */
end_comment
begin_class
DECL|class|TestSimpleQParserPlugin
specifier|public
class|class
name|TestSimpleQParserPlugin
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
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-simpleqpplugin.xml"
argument_list|)
expr_stmt|;
name|index
argument_list|()
expr_stmt|;
block|}
DECL|method|index
specifier|public
specifier|static
name|void
name|index
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"text0"
argument_list|,
literal|"t0 t0 t0"
argument_list|,
literal|"text1"
argument_list|,
literal|"t0 t1 t2"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"kw0 kw0 kw0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"43"
argument_list|,
literal|"text0"
argument_list|,
literal|"t0 t1 t2"
argument_list|,
literal|"text1"
argument_list|,
literal|"t3 t4 t5"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"kw0 kw1 kw2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"44"
argument_list|,
literal|"text0"
argument_list|,
literal|"t0 t1 t1"
argument_list|,
literal|"text1"
argument_list|,
literal|"t6 t7 t8"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"kw3 kw4 kw5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"45"
argument_list|,
literal|"text0"
argument_list|,
literal|"t0 t0 t1"
argument_list|,
literal|"text1"
argument_list|,
literal|"t9 t10 t11"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"kw6 kw7 kw8"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"46"
argument_list|,
literal|"text0"
argument_list|,
literal|"t1 t1 t1"
argument_list|,
literal|"text1"
argument_list|,
literal|"t12 t13 t14"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"kw9 kw10 kw11"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"47"
argument_list|,
literal|"text0"
argument_list|,
literal|"and"
argument_list|,
literal|"text1"
argument_list|,
literal|"+"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"+"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"48"
argument_list|,
literal|"text0"
argument_list|,
literal|"not"
argument_list|,
literal|"text1"
argument_list|,
literal|"-"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"-"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"49"
argument_list|,
literal|"text0"
argument_list|,
literal|"or"
argument_list|,
literal|"text1"
argument_list|,
literal|"|"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"|"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"50"
argument_list|,
literal|"text0"
argument_list|,
literal|"prefix"
argument_list|,
literal|"text1"
argument_list|,
literal|"t*"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"kw*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"51"
argument_list|,
literal|"text0"
argument_list|,
literal|"phrase"
argument_list|,
literal|"text1"
argument_list|,
literal|"\""
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"52"
argument_list|,
literal|"text0"
argument_list|,
literal|"open"
argument_list|,
literal|"text1"
argument_list|,
literal|"("
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"("
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"53"
argument_list|,
literal|"text0"
argument_list|,
literal|"close"
argument_list|,
literal|"text1"
argument_list|,
literal|")"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"54"
argument_list|,
literal|"text0"
argument_list|,
literal|"escape"
argument_list|,
literal|"text1"
argument_list|,
literal|"\\"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"\\"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"55"
argument_list|,
literal|"text0"
argument_list|,
literal|"whitespace"
argument_list|,
literal|"text1"
argument_list|,
literal|"whitespace"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"55"
argument_list|,
literal|"text0"
argument_list|,
literal|"whitespace"
argument_list|,
literal|"text1"
argument_list|,
literal|"whitespace"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"\n"
argument_list|)
argument_list|)
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
DECL|method|testQueryFields
specifier|public
name|void
name|testQueryFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0^2 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"t3"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0^3 text1^4 text-keyword0^0.55"
argument_list|,
literal|"q"
argument_list|,
literal|"t0"
argument_list|)
argument_list|,
literal|"/response/numFound==4"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text-keyword0^9.2"
argument_list|,
literal|"q"
argument_list|,
literal|"\"kw9 kw10 kw11\""
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"kw9 kw10 kw11"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"kw9"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0"
argument_list|,
literal|"q"
argument_list|,
literal|"t2"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0^1.1 text1^0.9"
argument_list|,
literal|"q"
argument_list|,
literal|"t2 t9 t12"
argument_list|)
argument_list|,
literal|"/response/numFound==4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultField
specifier|public
name|void
name|testDefaultField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"q"
argument_list|,
literal|"t2 t9 t12"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"q"
argument_list|,
literal|"t3"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"df"
argument_list|,
literal|"text1"
argument_list|,
literal|"q"
argument_list|,
literal|"t2 t9 t12"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"df"
argument_list|,
literal|"text1"
argument_list|,
literal|"q"
argument_list|,
literal|"t3"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"df"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\"kw9 kw10 kw11\""
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"df"
argument_list|,
literal|"text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"kw9 kw10 kw11"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueryFieldPriority
specifier|public
name|void
name|testQueryFieldPriority
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0^2 text1 text-keyword0"
argument_list|,
literal|"df"
argument_list|,
literal|"text0"
argument_list|,
literal|"q"
argument_list|,
literal|"t3"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnlyAndOperatorEnabledDisabled
specifier|public
name|void
name|testOnlyAndOperatorEnabledDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"+"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"NOT, OR, PHRASE, PREFIX, PRECEDENCE, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"-"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"NOT, OR, PHRASE, PREFIX, PRECEDENCE, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"+"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"-"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnlyNotOperatorEnabledDisabled
specifier|public
name|void
name|testOnlyNotOperatorEnabledDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"-"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, OR, PHRASE, PREFIX, PRECEDENCE, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"|"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, OR, PHRASE, PREFIX, PRECEDENCE, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"-"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"NOT"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"|"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"NOT"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnlyOrOperatorEnabledDisabled
specifier|public
name|void
name|testOnlyOrOperatorEnabledDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"|"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, PHRASE, PREFIX, PRECEDENCE, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\""
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, PHRASE, PREFIX, PRECEDENCE, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"|"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"OR"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\""
argument_list|,
literal|"q.operators"
argument_list|,
literal|"OR"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnlyPhraseOperatorEnabledDisabled
specifier|public
name|void
name|testOnlyPhraseOperatorEnabledDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\""
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PREFIX, PRECEDENCE, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"|"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PREFIX, PRECEDENCE, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\""
argument_list|,
literal|"q.operators"
argument_list|,
literal|"PHRASE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"|"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"PHRASE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnlyPrefixOperatorEnabledDisabled
specifier|public
name|void
name|testOnlyPrefixOperatorEnabledDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"t*"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PHRASE, PRECEDENCE, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"("
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PHRASE, PRECEDENCE, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"t*"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"PREFIX"
argument_list|)
argument_list|,
literal|"/response/numFound==6"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"("
argument_list|,
literal|"q.operators"
argument_list|,
literal|"PREFIX"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnlyPrecedenceOperatorEnabledDisabled
specifier|public
name|void
name|testOnlyPrecedenceOperatorEnabledDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"("
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PHRASE, PREFIX, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\\"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PHRASE, PREFIX, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"("
argument_list|,
literal|"q.operators"
argument_list|,
literal|"PRECEDENCE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\\"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"PRECEDENCE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|")"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PHRASE, PREFIX, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\\"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PHRASE, PREFIX, ESCAPE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|")"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"PRECEDENCE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\\"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"PRECEDENCE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnlyEscapeOperatorEnabledDisabled
specifier|public
name|void
name|testOnlyEscapeOperatorEnabledDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\\"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PHRASE, PREFIX, PRECEDENCE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\n"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PHRASE, PREFIX, PRECEDENCE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\\"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"ESCAPE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\n"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"ESCAPE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnlyWhitespaceOperatorEnabledDisabled
specifier|public
name|void
name|testOnlyWhitespaceOperatorEnabledDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\n"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PHRASE, PREFIX, PRECEDENCE, ESCAPE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\\"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PHRASE, PREFIX, PRECEDENCE, ESCAPE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\n"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"\\"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArbitraryOperatorsEnabledDisabled
specifier|public
name|void
name|testArbitraryOperatorsEnabledDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"kw0+kw1+kw2| \\ "
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, OR, PHRASE"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"t1 + t2 \\"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"t0 + (-t1 -t2) |"
argument_list|,
literal|"q.operators"
argument_list|,
literal|"AND, NOT, PRECEDENCE, WHITESPACE"
argument_list|)
argument_list|,
literal|"/response/numFound==4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoOperators
specifier|public
name|void
name|testNoOperators
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"kw0 kw1 kw2"
argument_list|,
literal|"q.operators"
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text1"
argument_list|,
literal|"q"
argument_list|,
literal|"t1 t2 t3"
argument_list|,
literal|"q.operators"
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultOperator
specifier|public
name|void
name|testDefaultOperator
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text1 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"t2 t3"
argument_list|,
literal|"q.op"
argument_list|,
literal|"AND"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text0 text-keyword0"
argument_list|,
literal|"q"
argument_list|,
literal|"t0 t2"
argument_list|,
literal|"q.op"
argument_list|,
literal|"AND"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"simple"
argument_list|,
literal|"qf"
argument_list|,
literal|"text1"
argument_list|,
literal|"q"
argument_list|,
literal|"t2 t3"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit