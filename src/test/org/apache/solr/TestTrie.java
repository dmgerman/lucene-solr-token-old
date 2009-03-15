begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DateField
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
name|DateMathParser
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
comment|/**  * Tests for TrieField functionality  *  * @version $Id$  * @since solr 1.4  */
end_comment
begin_class
DECL|class|TestTrie
specifier|public
class|class
name|TestTrie
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema-trie.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
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
block|}
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
block|}
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
block|}
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
block|}
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
name|DateField
operator|.
name|UTC
argument_list|,
name|Locale
operator|.
name|US
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
literal|"Range filter must match only 5 documents"
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
block|}
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
block|}
end_class
end_unit
