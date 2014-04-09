begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|TrieDateField
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
name|schema
operator|.
name|TrieField
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
name|DateMathParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|text
operator|.
name|SimpleDateFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import
begin_comment
comment|/**  * Tests for TrieField functionality  *  *  * @since solr 1.4  */
end_comment
begin_class
DECL|class|TestTrie
specifier|public
class|class
name|TestTrie
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
literal|"schema-trie.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrieIntRangeSearch
specifier|public
name|void
name|testTrieIntRangeSearch
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"tint"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Range filter must match only 5 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tint:[2 TO 6]"
argument_list|)
argument_list|,
literal|"//*[@numFound='5']"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|11
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
operator|-
name|i
argument_list|)
argument_list|,
literal|"tint"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
operator|-
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Range filter must match only 5 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tint:[-6 TO -2]"
argument_list|)
argument_list|,
literal|"//*[@numFound='5']"
argument_list|)
expr_stmt|;
comment|// Test open ended range searches
name|assertQ
argument_list|(
literal|"Range filter tint:[-9 to *] must match 20 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tint:[-10 TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='20']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Range filter tint:[* to 9] must match 20 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tint:[* TO 10]"
argument_list|)
argument_list|,
literal|"//*[@numFound='20']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Range filter tint:[* to *] must match 20 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tint:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='20']"
argument_list|)
expr_stmt|;
comment|// Sorting
name|assertQ
argument_list|(
literal|"Sort descending does not work correctly on tint fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"tint desc"
argument_list|)
argument_list|,
literal|"//*[@numFound='20']"
argument_list|,
literal|"//int[@name='tint'][.='9']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Sort ascending does not work correctly on tint fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"tint asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='20']"
argument_list|,
literal|"//int[@name='tint'][.='-10']"
argument_list|)
expr_stmt|;
comment|// Function queries
name|assertQ
argument_list|(
literal|"Function queries does not work correctly on tint fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"_val_:\"sum(tint,1)\""
argument_list|)
argument_list|,
literal|"//*[@numFound='20']"
argument_list|,
literal|"//int[@name='tint'][.='9']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrieTermQuery
specifier|public
name|void
name|testTrieTermQuery
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"tint"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"tfloat"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|*
name|i
operator|*
literal|31.11f
argument_list|)
argument_list|,
literal|"tlong"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|+
operator|(
name|long
operator|)
name|i
argument_list|)
argument_list|,
literal|"tdouble"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|*
literal|2.33d
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// Use with q
name|assertQ
argument_list|(
literal|"Term query on trie int field must match 1 document"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"tint:2"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Term query on trie float field must match 1 document"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"tfloat:124.44"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Term query on trie long field must match 1 document"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"tlong:2147483648"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Term query on trie double field must match 1 document"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"tdouble:4.66"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// Use with fq
name|assertQ
argument_list|(
literal|"Term query on trie int field must match 1 document"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tint:2"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Term query on trie float field must match 1 document"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tfloat:124.44"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Term query on trie long field must match 1 document"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tlong:2147483648"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Term query on trie double field must match 1 document"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tdouble:4.66"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrieFloatRangeSearch
specifier|public
name|void
name|testTrieFloatRangeSearch
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"tfloat"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|*
name|i
operator|*
literal|31.11f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tfloat:[0 TO 2518.0]"
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Range filter must match only 5 documents"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='9']"
argument_list|)
expr_stmt|;
name|req
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tfloat:[0 TO *]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Range filter must match 10 documents"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='10']"
argument_list|)
expr_stmt|;
comment|// Sorting
name|assertQ
argument_list|(
literal|"Sort descending does not work correctly on tfloat fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"tfloat desc"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|,
literal|"//float[@name='tfloat'][.='2519.9102']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Sort ascending does not work correctly on tfloat fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"tfloat asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|,
literal|"//float[@name='tfloat'][.='0.0']"
argument_list|)
expr_stmt|;
comment|// Function queries
name|assertQ
argument_list|(
literal|"Function queries does not work correctly on tfloat fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"_val_:\"sum(tfloat,1.0)\""
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|,
literal|"//float[@name='tfloat'][.='2519.9102']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrieLongRangeSearch
specifier|public
name|void
name|testTrieLongRangeSearch
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|long
name|i
init|=
name|Integer
operator|.
name|MAX_VALUE
init|,
name|c
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|10l
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|c
operator|++
argument_list|)
argument_list|,
literal|"tlong"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fq
init|=
literal|"tlong:["
operator|+
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|" TO "
operator|+
operator|(
literal|5l
operator|+
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|+
literal|"]"
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Range filter must match only 5 documents"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='6']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Range filter tlong:[* to *] must match 10 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tlong:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|)
expr_stmt|;
comment|// Sorting
name|assertQ
argument_list|(
literal|"Sort descending does not work correctly on tlong fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"tlong desc"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|,
literal|"//long[@name='tlong'][.='2147483656']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Sort ascending does not work correctly on tlong fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"tlong asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|,
literal|"//long[@name='tlong'][.='2147483647']"
argument_list|)
expr_stmt|;
comment|// Function queries
name|assertQ
argument_list|(
literal|"Function queries does not work correctly on tlong fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"_val_:\"sum(tlong,1.0)\""
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|,
literal|"//long[@name='tlong'][.='2147483656']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrieDoubleRangeSearch
specifier|public
name|void
name|testTrieDoubleRangeSearch
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|long
name|i
init|=
name|Integer
operator|.
name|MAX_VALUE
init|,
name|c
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|10l
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|c
operator|++
argument_list|)
argument_list|,
literal|"tdouble"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|*
literal|2.33d
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fq
init|=
literal|"tdouble:["
operator|+
name|Integer
operator|.
name|MAX_VALUE
operator|*
literal|2.33d
operator|+
literal|" TO "
operator|+
operator|(
literal|5l
operator|+
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|*
literal|2.33d
operator|+
literal|"]"
decl_stmt|;
name|assertQ
argument_list|(
literal|"Range filter must match only 5 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
name|fq
argument_list|)
argument_list|,
literal|"//*[@numFound='6']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Range filter tdouble:[* to *] must match 10 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tdouble:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|)
expr_stmt|;
comment|// Sorting
name|assertQ
argument_list|(
literal|"Sort descending does not work correctly on tdouble fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"tdouble desc"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|,
literal|"//double[@name='tdouble'][.='5.0036369184800005E9']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Sort ascending does not work correctly on tdouble fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"tdouble asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|,
literal|"//double[@name='tdouble'][.='5.00363689751E9']"
argument_list|)
expr_stmt|;
comment|// Function queries
name|assertQ
argument_list|(
literal|"Function queries does not work correctly on tdouble fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"_val_:\"sum(tdouble,1.0)\""
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|,
literal|"//double[@name='tdouble'][.='5.0036369184800005E9']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrieDateRangeSearch
specifier|public
name|void
name|testTrieDateRangeSearch
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"tdate"
argument_list|,
literal|"1995-12-31T23:"
operator|+
operator|(
name|i
operator|<
literal|10
condition|?
literal|"0"
operator|+
name|i
else|:
name|i
operator|)
operator|+
literal|":59.999Z"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tdate:[1995-12-31T23:00:59.999Z TO 1995-12-31T23:04:59.999Z]"
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"Range filter must match only 5 documents"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='5']"
argument_list|)
expr_stmt|;
comment|// Test open ended range searches
name|assertQ
argument_list|(
literal|"Range filter tint:[1995-12-31T23:00:59.999Z to *] must match 10 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tdate:[1995-12-31T23:00:59.999Z TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Range filter tint:[* to 1995-12-31T23:09:59.999Z] must match 10 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tdate:[* TO 1995-12-31T23:09:59.999Z]"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Range filter tint:[* to *] must match 10 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tdate:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|)
expr_stmt|;
comment|// Test date math syntax
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss'Z'"
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|format
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|DateMathParser
name|dmp
init|=
operator|new
name|DateMathParser
argument_list|(
name|TrieDateField
operator|.
name|UTC
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|String
name|largestDate
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
comment|// index 10 days starting with today
name|String
name|d
init|=
name|format
operator|.
name|format
argument_list|(
name|i
operator|==
literal|0
condition|?
name|dmp
operator|.
name|parseMath
argument_list|(
literal|"/DAY"
argument_list|)
else|:
name|dmp
operator|.
name|parseMath
argument_list|(
literal|"/DAY+"
operator|+
name|i
operator|+
literal|"DAYS"
argument_list|)
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"tdate"
argument_list|,
name|d
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|9
condition|)
name|largestDate
operator|=
name|d
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Range filter must match only 10 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tdate:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='10']"
argument_list|)
expr_stmt|;
name|req
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tdate:[NOW/DAY TO NOW/DAY+5DAYS]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Range filter must match only 6 documents"
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='6']"
argument_list|)
expr_stmt|;
comment|// Test Term Queries
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"tdate"
argument_list|,
literal|"1995-12-31T23:59:59.999Z"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Term query must match only 1 document"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"tdate:1995-12-31T23\\:59\\:59.999Z"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Term query must match only 1 document"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"tdate:1995-12-31T23\\:59\\:59.999Z"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// Sorting
name|assertQ
argument_list|(
literal|"Sort descending does not work correctly on tdate fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"tdate desc"
argument_list|)
argument_list|,
literal|"//*[@numFound='11']"
argument_list|,
literal|"//date[@name='tdate'][.='"
operator|+
name|largestDate
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Sort ascending does not work correctly on tdate fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"tdate asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='11']"
argument_list|,
literal|"//date[@name='tdate'][.='1995-12-31T23:59:59.999Z']"
argument_list|)
expr_stmt|;
comment|// Function queries
name|assertQ
argument_list|(
literal|"Function queries does not work correctly on tdate fields"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"_val_:\"sum(tdate,1.0)\""
argument_list|)
argument_list|,
literal|"//*[@numFound='11']"
argument_list|,
literal|"//date[@name='tdate'][.='"
operator|+
name|largestDate
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrieDoubleRangeSearch_CustomPrecisionStep
specifier|public
name|void
name|testTrieDoubleRangeSearch_CustomPrecisionStep
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|long
name|i
init|=
name|Integer
operator|.
name|MAX_VALUE
init|,
name|c
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|10l
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|c
operator|++
argument_list|)
argument_list|,
literal|"tdouble4"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|*
literal|2.33d
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fq
init|=
literal|"tdouble4:["
operator|+
name|Integer
operator|.
name|MAX_VALUE
operator|*
literal|2.33d
operator|+
literal|" TO "
operator|+
operator|(
literal|5l
operator|+
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|*
literal|2.33d
operator|+
literal|"]"
decl_stmt|;
name|assertQ
argument_list|(
literal|"Range filter must match only 5 documents"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
name|fq
argument_list|)
argument_list|,
literal|"//*[@numFound='6']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTrieFacet_PrecisionStep
specifier|public
name|void
name|testTrieFacet_PrecisionStep
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Future protect - assert 0<precisionStep<64
name|checkPrecisionSteps
argument_list|(
literal|"tint"
argument_list|)
expr_stmt|;
name|checkPrecisionSteps
argument_list|(
literal|"tfloat"
argument_list|)
expr_stmt|;
name|checkPrecisionSteps
argument_list|(
literal|"tdouble"
argument_list|)
expr_stmt|;
name|checkPrecisionSteps
argument_list|(
literal|"tlong"
argument_list|)
expr_stmt|;
name|checkPrecisionSteps
argument_list|(
literal|"tdate"
argument_list|)
expr_stmt|;
comment|// For tdate tests
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ss'Z'"
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|format
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
expr_stmt|;
name|DateMathParser
name|dmp
init|=
operator|new
name|DateMathParser
argument_list|(
name|TrieDateField
operator|.
name|UTC
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|long
name|l
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|+
name|i
operator|*
literal|1L
decl_stmt|;
comment|// index 10 days starting with today
name|String
name|d
init|=
name|format
operator|.
name|format
argument_list|(
name|i
operator|==
literal|0
condition|?
name|dmp
operator|.
name|parseMath
argument_list|(
literal|"/DAY"
argument_list|)
else|:
name|dmp
operator|.
name|parseMath
argument_list|(
literal|"/DAY+"
operator|+
name|i
operator|+
literal|"DAYS"
argument_list|)
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"tint"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"tlong"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|l
argument_list|)
argument_list|,
literal|"tfloat"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|*
name|i
operator|*
literal|31.11f
argument_list|)
argument_list|,
literal|"tdouble"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|*
literal|2.33d
argument_list|)
argument_list|,
literal|"tdate"
argument_list|,
name|d
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|long
name|l
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|+
name|i
operator|*
literal|1L
decl_stmt|;
name|String
name|d
init|=
name|format
operator|.
name|format
argument_list|(
name|i
operator|==
literal|0
condition|?
name|dmp
operator|.
name|parseMath
argument_list|(
literal|"/DAY"
argument_list|)
else|:
name|dmp
operator|.
name|parseMath
argument_list|(
literal|"/DAY+"
operator|+
name|i
operator|+
literal|"DAYS"
argument_list|)
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
operator|(
name|i
operator|+
literal|1
operator|)
operator|*
literal|10
argument_list|)
argument_list|,
literal|"tint"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"tlong"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|l
argument_list|)
argument_list|,
literal|"tfloat"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|*
name|i
operator|*
literal|31.11f
argument_list|)
argument_list|,
literal|"tdouble"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
operator|*
literal|2.33d
argument_list|)
argument_list|,
literal|"tdate"
argument_list|,
name|d
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"15"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"tint"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"tlong"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"tfloat"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"tdouble"
argument_list|,
literal|"facet.date"
argument_list|,
literal|"tdate"
argument_list|,
literal|"facet.date.start"
argument_list|,
literal|"NOW/DAY"
argument_list|,
literal|"facet.date.end"
argument_list|,
literal|"NOW/DAY+6DAYS"
argument_list|,
literal|"facet.date.gap"
argument_list|,
literal|"+1DAY"
argument_list|)
decl_stmt|;
name|testFacetField
argument_list|(
name|req
argument_list|,
literal|"tint"
argument_list|,
literal|"0"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|testFacetField
argument_list|(
name|req
argument_list|,
literal|"tint"
argument_list|,
literal|"5"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|testFacetField
argument_list|(
name|req
argument_list|,
literal|"tlong"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|testFacetField
argument_list|(
name|req
argument_list|,
literal|"tlong"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|5L
argument_list|)
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|testFacetField
argument_list|(
name|req
argument_list|,
literal|"tfloat"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|31.11f
argument_list|)
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|testFacetField
argument_list|(
name|req
argument_list|,
literal|"tfloat"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|5
operator|*
literal|5
operator|*
literal|31.11f
argument_list|)
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|testFacetField
argument_list|(
name|req
argument_list|,
literal|"tdouble"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|2.33d
argument_list|)
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|testFacetField
argument_list|(
name|req
argument_list|,
literal|"tdouble"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
literal|5
operator|*
literal|2.33d
argument_list|)
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|testFacetDate
argument_list|(
name|req
argument_list|,
literal|"tdate"
argument_list|,
name|format
operator|.
name|format
argument_list|(
name|dmp
operator|.
name|parseMath
argument_list|(
literal|"/DAY"
argument_list|)
argument_list|)
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|testFacetDate
argument_list|(
name|req
argument_list|,
literal|"tdate"
argument_list|,
name|format
operator|.
name|format
argument_list|(
name|dmp
operator|.
name|parseMath
argument_list|(
literal|"/DAY+5DAYS"
argument_list|)
argument_list|)
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkPrecisionSteps
specifier|private
name|void
name|checkPrecisionSteps
parameter_list|(
name|String
name|fieldType
parameter_list|)
block|{
name|FieldType
name|type
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldType
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|TrieField
condition|)
block|{
name|TrieField
name|field
init|=
operator|(
name|TrieField
operator|)
name|type
decl_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|getPrecisionStep
argument_list|()
operator|>
literal|0
operator|&&
name|field
operator|.
name|getPrecisionStep
argument_list|()
operator|<
literal|64
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFacetField
specifier|private
name|void
name|testFacetField
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|value
parameter_list|,
name|String
name|count
parameter_list|)
block|{
name|String
name|xpath
init|=
literal|"//lst[@name='facet_fields']/lst[@name='"
operator|+
name|field
operator|+
literal|"']/int[@name='"
operator|+
name|value
operator|+
literal|"'][.='"
operator|+
name|count
operator|+
literal|"']"
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
name|xpath
argument_list|)
expr_stmt|;
block|}
DECL|method|testFacetDate
specifier|private
name|void
name|testFacetDate
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|value
parameter_list|,
name|String
name|count
parameter_list|)
block|{
name|String
name|xpath
init|=
literal|"//lst[@name='facet_dates']/lst[@name='"
operator|+
name|field
operator|+
literal|"']/int[@name='"
operator|+
name|value
operator|+
literal|"'][.='"
operator|+
name|count
operator|+
literal|"']"
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
name|xpath
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
