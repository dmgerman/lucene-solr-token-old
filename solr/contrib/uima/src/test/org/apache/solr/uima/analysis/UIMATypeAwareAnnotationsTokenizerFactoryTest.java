begin_unit
begin_package
DECL|package|org.apache.solr.uima.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uima
operator|.
name|analysis
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
comment|/**  */
end_comment
begin_class
DECL|class|UIMATypeAwareAnnotationsTokenizerFactoryTest
specifier|public
class|class
name|UIMATypeAwareAnnotationsTokenizerFactoryTest
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
literal|"uima/uima-tokenizers-solrconfig.xml"
argument_list|,
literal|"uima/uima-tokenizers-schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInitialization
specifier|public
name|void
name|testInitialization
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
literal|"nouns"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldType
argument_list|(
literal|"nouns"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIndexAndQuery
specifier|public
name|void
name|testIndexAndQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">123</field><field name=\"text\">The counter counts the beans: 1 and 2 and three.</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"nouns"
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//lst[@name='nouns']/int[@name='beans']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//lst[@name='nouns']/int[@name='counter']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//lst[@name='nouns']/int[@name!='The']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//lst[@name='nouns']/int[@name!='counts']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//lst[@name='nouns']/int[@name!='the']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//lst[@name='nouns']/int[@name!=':']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//lst[@name='nouns']/int[@name!='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//lst[@name='nouns']/int[@name!='and']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//lst[@name='nouns']/int[@name!='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//lst[@name='nouns']/int[@name!='three']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//lst[@name='nouns']/int[@name!='.']"
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
