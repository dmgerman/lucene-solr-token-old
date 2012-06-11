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
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Sort
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SortField
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
name|IndexSchema
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
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|QueryParsingTest
specifier|public
class|class
name|QueryParsingTest
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
comment|/**    * Test that the main QParserPlugins people are likely to use    * as defaults fail with a consistent exception when the query string     * is either empty or null.    * @see<a href="https://issues.apache.org/jira/browse/SOLR-435">SOLR-435</a>    * @see<a href="https://issues.apache.org/jira/browse/SOLR-2001">SOLR-2001</a>    */
DECL|method|testQParserEmptyInput
specifier|public
name|void
name|testQParserEmptyInput
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|parsersTested
init|=
operator|new
name|String
index|[]
block|{
name|OldLuceneQParserPlugin
operator|.
name|NAME
block|,
name|LuceneQParserPlugin
operator|.
name|NAME
block|,
name|DisMaxQParserPlugin
operator|.
name|NAME
block|,
name|ExtendedDismaxQParserPlugin
operator|.
name|NAME
block|}
decl_stmt|;
for|for
control|(
name|String
name|defType
range|:
name|parsersTested
control|)
block|{
for|for
control|(
name|String
name|qstr
range|:
operator|new
name|String
index|[]
block|{
literal|null
block|,
literal|""
block|}
control|)
block|{
name|QParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|QParser
operator|.
name|getParser
argument_list|(
name|qstr
argument_list|,
name|defType
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"getParser excep using defType="
operator|+
name|defType
operator|+
literal|" with qstr="
operator|+
name|qstr
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|Query
name|q
init|=
name|parser
operator|.
name|parse
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"expected no query"
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testSort
specifier|public
name|void
name|testSort
parameter_list|()
throws|throws
name|Exception
block|{
name|Sort
name|sort
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"score desc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"sort"
argument_list|,
name|sort
argument_list|)
expr_stmt|;
comment|//only 1 thing in the list, no Sort specified
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"score asc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|SortField
index|[]
name|flds
init|=
name|sort
operator|.
name|getSort
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight desc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight desc,bday asc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"bday"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//order aliases
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight top,bday asc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"bday"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight top,bday bottom"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"bday"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getReverse
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//test weird spacing
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight         desc,            bday         asc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"bday"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
comment|//handles trailing commas
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight desc,"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
comment|//test functions
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow(weight, 2) desc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|REWRITEABLE
argument_list|)
expr_stmt|;
comment|//Not thrilled about the fragility of string matching here, but...
comment|//the value sources get wrapped, so the out field is different than the input
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"pow(float(weight),const(2))"
argument_list|)
expr_stmt|;
comment|//test functions (more deep)
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"sum(product(r_f1,sum(d_f1,t_f1,1.0)),a_f1) asc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|REWRITEABLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"sum(product(float(r_f1),sum(float(d_f1),float(t_f1),const(1.0))),float(a_f1))"
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow(weight,                 2.0)         desc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|REWRITEABLE
argument_list|)
expr_stmt|;
comment|//Not thrilled about the fragility of string matching here, but...
comment|//the value sources get wrapped, so the out field is different than the input
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"pow(float(weight),const(2.0))"
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow(weight, 2.0) desc, weight    desc,   bday    asc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|REWRITEABLE
argument_list|)
expr_stmt|;
comment|//Not thrilled about the fragility of string matching here, but...
comment|//the value sources get wrapped, so the out field is different than the input
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"pow(float(weight),const(2.0))"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|1
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|2
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"bday"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|2
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
comment|//handles trailing commas
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight desc,"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"weight"
argument_list|)
expr_stmt|;
comment|//Test literals in functions
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"strdist(foo_s1, \"junk\", jw) desc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|flds
operator|=
name|sort
operator|.
name|getSort
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|REWRITEABLE
argument_list|)
expr_stmt|;
comment|//the value sources get wrapped, so the out field is different than the input
name|assertEquals
argument_list|(
name|flds
index|[
literal|0
index|]
operator|.
name|getField
argument_list|()
argument_list|,
literal|"strdist(str(foo_s1),literal(junk), dist=org.apache.lucene.search.spell.JaroWinklerDistance)"
argument_list|)
expr_stmt|;
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|""
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|sort
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
DECL|method|testBad
specifier|public
name|void
name|testBad
parameter_list|()
throws|throws
name|Exception
block|{
name|Sort
name|sort
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
decl_stmt|;
comment|//test some bad vals
try|try
block|{
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight, desc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"w"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"weight desc, bday"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{     }
try|try
block|{
comment|//bad number of commas
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow(weight,,2) desc, bday asc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{     }
try|try
block|{
comment|//bad function
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow() desc, bday asc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{     }
try|try
block|{
comment|//bad number of parens
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
literal|"pow((weight,2) desc, bday asc"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{     }
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
