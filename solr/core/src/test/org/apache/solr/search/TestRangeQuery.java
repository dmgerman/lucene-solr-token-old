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
name|SolrInputDocument
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
name|response
operator|.
name|ResultContext
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
name|util
operator|.
name|*
import|;
end_import
begin_class
DECL|class|TestRangeQuery
specifier|public
class|class
name|TestRangeQuery
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
DECL|field|r
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|method|addInt
name|void
name|addInt
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|int
name|l
parameter_list|,
name|int
name|u
parameter_list|,
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|int
name|v
init|=
literal|0
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|l
operator|&&
name|l
operator|==
name|u
condition|)
block|{
name|v
operator|=
name|r
operator|.
name|nextInt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|v
operator|=
name|r
operator|.
name|nextInt
argument_list|(
name|u
operator|-
name|l
argument_list|)
operator|+
name|l
expr_stmt|;
block|}
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|field
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
DECL|interface|DocProcessor
interface|interface
name|DocProcessor
block|{
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
function_decl|;
block|}
DECL|method|createIndex
specifier|public
name|void
name|createIndex
parameter_list|(
name|int
name|nDocs
parameter_list|,
name|DocProcessor
name|proc
parameter_list|)
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
name|nDocs
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|proc
operator|.
name|process
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRangeQueries
specifier|public
name|void
name|testRangeQueries
parameter_list|()
throws|throws
name|Exception
block|{
comment|// ensure that we aren't losing precision on any fields in addition to testing other non-numeric fields
comment|// that aren't tested in testRandomRangeQueries()
name|int
name|i
init|=
literal|2000000000
decl_stmt|;
name|long
name|l
init|=
literal|500000000000000000L
decl_stmt|;
name|double
name|d
init|=
literal|0.3333333333333333
decl_stmt|;
comment|// first 3 values will be indexed, the last two won't be
name|String
index|[]
name|ints
init|=
block|{
literal|""
operator|+
operator|(
name|i
operator|-
literal|1
operator|)
block|,
literal|""
operator|+
operator|(
name|i
operator|)
block|,
literal|""
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
block|,
literal|""
operator|+
operator|(
name|i
operator|-
literal|2
operator|)
block|,
literal|""
operator|+
operator|(
name|i
operator|+
literal|2
operator|)
block|}
decl_stmt|;
name|String
index|[]
name|longs
init|=
block|{
literal|""
operator|+
operator|(
name|l
operator|-
literal|1
operator|)
block|,
literal|""
operator|+
operator|(
name|l
operator|)
block|,
literal|""
operator|+
operator|(
name|l
operator|+
literal|1
operator|)
block|,
literal|""
operator|+
operator|(
name|l
operator|-
literal|2
operator|)
block|,
literal|""
operator|+
operator|(
name|l
operator|+
literal|2
operator|)
block|}
decl_stmt|;
name|String
index|[]
name|doubles
init|=
block|{
literal|""
operator|+
operator|(
name|d
operator|-
literal|1e-16
operator|)
block|,
literal|""
operator|+
operator|(
name|d
operator|)
block|,
literal|""
operator|+
operator|(
name|d
operator|+
literal|1e-16
operator|)
block|,
literal|""
operator|+
operator|(
name|d
operator|-
literal|2e-16
operator|)
block|,
literal|""
operator|+
operator|(
name|d
operator|+
literal|2e-16
operator|)
block|}
decl_stmt|;
name|String
index|[]
name|strings
init|=
block|{
literal|"aaa"
block|,
literal|"bbb"
block|,
literal|"ccc"
block|,
literal|"aa"
block|,
literal|"cccc"
block|}
decl_stmt|;
name|String
index|[]
name|dates
init|=
block|{
literal|"0299-12-31T23:59:59.999Z"
block|,
literal|"2000-01-01T00:00:00.000Z"
block|,
literal|"2000-01-01T00:00:00.001Z"
block|,
literal|"0299-12-31T23:59:59.998Z"
block|,
literal|"2000-01-01T00:00:00.002Z"
block|}
decl_stmt|;
comment|// fields that normal range queries should work on
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|norm_fields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|norm_fields
operator|.
name|put
argument_list|(
literal|"foo_i"
argument_list|,
name|ints
argument_list|)
expr_stmt|;
name|norm_fields
operator|.
name|put
argument_list|(
literal|"foo_l"
argument_list|,
name|longs
argument_list|)
expr_stmt|;
name|norm_fields
operator|.
name|put
argument_list|(
literal|"foo_d"
argument_list|,
name|doubles
argument_list|)
expr_stmt|;
name|norm_fields
operator|.
name|put
argument_list|(
literal|"foo_ti"
argument_list|,
name|ints
argument_list|)
expr_stmt|;
name|norm_fields
operator|.
name|put
argument_list|(
literal|"foo_tl"
argument_list|,
name|longs
argument_list|)
expr_stmt|;
name|norm_fields
operator|.
name|put
argument_list|(
literal|"foo_td"
argument_list|,
name|doubles
argument_list|)
expr_stmt|;
name|norm_fields
operator|.
name|put
argument_list|(
literal|"foo_tdt"
argument_list|,
name|dates
argument_list|)
expr_stmt|;
name|norm_fields
operator|.
name|put
argument_list|(
literal|"foo_s"
argument_list|,
name|strings
argument_list|)
expr_stmt|;
name|norm_fields
operator|.
name|put
argument_list|(
literal|"foo_dt"
argument_list|,
name|dates
argument_list|)
expr_stmt|;
comment|// fields that frange queries should work on
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|frange_fields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|frange_fields
operator|.
name|put
argument_list|(
literal|"foo_i"
argument_list|,
name|ints
argument_list|)
expr_stmt|;
name|frange_fields
operator|.
name|put
argument_list|(
literal|"foo_l"
argument_list|,
name|longs
argument_list|)
expr_stmt|;
name|frange_fields
operator|.
name|put
argument_list|(
literal|"foo_d"
argument_list|,
name|doubles
argument_list|)
expr_stmt|;
name|frange_fields
operator|.
name|put
argument_list|(
literal|"foo_ti"
argument_list|,
name|ints
argument_list|)
expr_stmt|;
name|frange_fields
operator|.
name|put
argument_list|(
literal|"foo_tl"
argument_list|,
name|longs
argument_list|)
expr_stmt|;
name|frange_fields
operator|.
name|put
argument_list|(
literal|"foo_td"
argument_list|,
name|doubles
argument_list|)
expr_stmt|;
name|frange_fields
operator|.
name|put
argument_list|(
literal|"foo_tdt"
argument_list|,
name|dates
argument_list|)
expr_stmt|;
name|frange_fields
operator|.
name|put
argument_list|(
literal|"foo_s"
argument_list|,
name|strings
argument_list|)
expr_stmt|;
name|frange_fields
operator|.
name|put
argument_list|(
literal|"foo_dt"
argument_list|,
name|dates
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|all_fields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|all_fields
operator|.
name|putAll
argument_list|(
name|norm_fields
argument_list|)
expr_stmt|;
name|all_fields
operator|.
name|putAll
argument_list|(
name|frange_fields
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ints
operator|.
name|length
operator|-
literal|2
condition|;
name|j
operator|++
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
literal|""
operator|+
name|j
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|entry
range|:
name|all_fields
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|adoc
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
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
comment|// simple test of a function rather than just the field
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange l=0 u=2}id"
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange l=0 u=2}product(id,2)"
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange l=100 u=102}sum(id,100)"
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|entry
range|:
name|norm_fields
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|f
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
index|[]
name|v
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":[* TO *]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":["
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|"]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":["
operator|+
name|v
index|[
literal|1
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|"]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":["
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|1
index|]
operator|+
literal|"]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":["
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|"]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":["
operator|+
name|v
index|[
literal|1
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|1
index|]
operator|+
literal|"]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":["
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|"]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":["
operator|+
name|v
index|[
literal|3
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|3
index|]
operator|+
literal|"]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":["
operator|+
name|v
index|[
literal|4
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|4
index|]
operator|+
literal|"]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":{"
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":{"
operator|+
name|v
index|[
literal|1
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":{"
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|1
index|]
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|f
operator|+
literal|":{"
operator|+
name|v
index|[
literal|3
index|]
operator|+
literal|" TO "
operator|+
name|v
index|[
literal|4
index|]
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|entry
range|:
name|frange_fields
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|f
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
index|[]
name|v
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|1
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|3
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|4
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange"
operator|+
literal|" u="
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange"
operator|+
literal|" u="
operator|+
name|v
index|[
literal|1
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange"
operator|+
literal|" u="
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange"
operator|+
literal|" u="
operator|+
name|v
index|[
literal|3
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange"
operator|+
literal|" u="
operator|+
name|v
index|[
literal|4
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incl=false"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incl=false"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|1
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incl=false"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incl=false"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|3
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incl=false"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|4
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incu=false"
operator|+
literal|" u="
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incu=false"
operator|+
literal|" u="
operator|+
name|v
index|[
literal|1
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incu=false"
operator|+
literal|" u="
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=2]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incu=false"
operator|+
literal|" u="
operator|+
name|v
index|[
literal|3
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incu=false"
operator|+
literal|" u="
operator|+
name|v
index|[
literal|4
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incl=true incu=true"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|" u="
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incl=false incu=false"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|0
index|]
operator|+
literal|" u="
operator|+
name|v
index|[
literal|2
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"{!frange incl=false incu=false"
operator|+
literal|" l="
operator|+
name|v
index|[
literal|3
index|]
operator|+
literal|" u="
operator|+
name|v
index|[
literal|4
index|]
operator|+
literal|"}"
operator|+
name|f
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRandomRangeQueries
specifier|public
name|void
name|testRandomRangeQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|handler
init|=
literal|""
decl_stmt|;
specifier|final
name|String
index|[]
name|fields
init|=
block|{
literal|"foo_s"
block|,
literal|"foo_i"
block|,
literal|"foo_l"
block|,
literal|"foo_f"
block|,
literal|"foo_d"
block|,
literal|"foo_ti"
block|,
literal|"foo_tl"
block|,
literal|"foo_tf"
block|,
literal|"foo_td"
block|}
decl_stmt|;
specifier|final
name|int
name|l
init|=
operator|-
literal|5
decl_stmt|;
specifier|final
name|int
name|u
init|=
literal|25
decl_stmt|;
name|createIndex
argument_list|(
literal|15
argument_list|,
operator|new
name|DocProcessor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
block|{
comment|// 10% of the docs have missing values
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|!=
literal|0
condition|)
name|addInt
argument_list|(
name|doc
argument_list|,
name|l
argument_list|,
name|u
argument_list|,
name|fields
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// fields that a normal range query will work correctly on
name|String
index|[]
name|norm_fields
init|=
block|{
literal|"foo_i"
block|,
literal|"foo_l"
block|,
literal|"foo_f"
block|,
literal|"foo_d"
block|,
literal|"foo_ti"
block|,
literal|"foo_tl"
block|,
literal|"foo_tf"
block|,
literal|"foo_td"
block|}
decl_stmt|;
comment|// fields that a value source range query should work on
name|String
index|[]
name|frange_fields
init|=
block|{
literal|"foo_i"
block|,
literal|"foo_l"
block|,
literal|"foo_f"
block|,
literal|"foo_d"
block|}
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|int
name|lower
init|=
name|l
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|u
operator|-
name|l
operator|+
literal|10
argument_list|)
operator|-
literal|5
decl_stmt|;
name|int
name|upper
init|=
name|lower
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|u
operator|+
literal|5
operator|-
name|lower
argument_list|)
decl_stmt|;
name|boolean
name|lowerMissing
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|1
decl_stmt|;
name|boolean
name|upperMissing
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|1
decl_stmt|;
name|boolean
name|inclusive
init|=
name|lowerMissing
operator|||
name|upperMissing
operator|||
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
comment|// lower=2; upper=2; inclusive=true;
comment|// inclusive=true; lowerMissing=true; upperMissing=true;
name|List
argument_list|<
name|String
argument_list|>
name|qs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|norm_fields
control|)
block|{
name|String
name|q
init|=
name|field
operator|+
literal|':'
operator|+
operator|(
name|inclusive
condition|?
literal|'['
else|:
literal|'{'
operator|)
operator|+
operator|(
name|lowerMissing
condition|?
literal|"*"
else|:
name|lower
operator|)
operator|+
literal|" TO "
operator|+
operator|(
name|upperMissing
condition|?
literal|"*"
else|:
name|upper
operator|)
operator|+
operator|(
name|inclusive
condition|?
literal|']'
else|:
literal|'}'
operator|)
decl_stmt|;
name|qs
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|field
range|:
name|frange_fields
control|)
block|{
name|String
name|q
init|=
literal|"{!frange v="
operator|+
name|field
operator|+
operator|(
name|lowerMissing
condition|?
literal|""
else|:
operator|(
literal|" l="
operator|+
name|lower
operator|)
operator|)
operator|+
operator|(
name|upperMissing
condition|?
literal|""
else|:
operator|(
literal|" u="
operator|+
name|upper
operator|)
operator|)
operator|+
operator|(
name|inclusive
condition|?
literal|""
else|:
literal|" incl=false"
operator|)
operator|+
operator|(
name|inclusive
condition|?
literal|""
else|:
literal|" incu=false"
operator|)
operator|+
literal|"}"
decl_stmt|;
name|qs
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
name|SolrQueryResponse
name|last
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|q
range|:
name|qs
control|)
block|{
comment|// System.out.println("QUERY="+q);
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
name|q
argument_list|,
literal|"rows"
argument_list|,
literal|"1000"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|qr
init|=
name|h
operator|.
name|queryAndResponse
argument_list|(
name|handler
argument_list|,
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
comment|// we only test if the same docs matched since some queries will include factors like idf, etc.
name|DocList
name|rA
init|=
operator|(
operator|(
name|ResultContext
operator|)
name|qr
operator|.
name|getResponse
argument_list|()
operator|)
operator|.
name|getDocList
argument_list|()
decl_stmt|;
name|DocList
name|rB
init|=
operator|(
operator|(
name|ResultContext
operator|)
name|last
operator|.
name|getResponse
argument_list|()
operator|)
operator|.
name|getDocList
argument_list|()
decl_stmt|;
name|sameDocs
argument_list|(
name|rA
argument_list|,
name|rB
argument_list|)
expr_stmt|;
block|}
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|last
operator|=
name|qr
expr_stmt|;
block|}
block|}
block|}
DECL|method|sameDocs
specifier|static
name|boolean
name|sameDocs
parameter_list|(
name|DocSet
name|a
parameter_list|,
name|DocSet
name|b
parameter_list|)
block|{
name|DocIterator
name|i
init|=
name|a
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// System.out.println("SIZES="+a.size() + "," + b.size());
name|assertEquals
argument_list|(
name|a
operator|.
name|size
argument_list|()
argument_list|,
name|b
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|doc
init|=
name|i
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
comment|// System.out.println("MATCH! " + doc);
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
