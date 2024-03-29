begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.json
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|json
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|JSONTestUtil
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
name|SolrTestCaseHS
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
annotation|@
name|LuceneTestCase
operator|.
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene3x"
block|,
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|,
literal|"Lucene45"
block|,
literal|"Appending"
block|}
argument_list|)
DECL|class|TestJsonRequest
specifier|public
class|class
name|TestJsonRequest
extends|extends
name|SolrTestCaseHS
block|{
DECL|field|servers
specifier|private
specifier|static
name|SolrInstances
name|servers
decl_stmt|;
comment|// for distributed testing
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|JSONTestUtil
operator|.
name|failRepeatedKeys
operator|=
literal|true
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema_latest.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|initServers
specifier|public
specifier|static
name|void
name|initServers
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|servers
operator|==
literal|null
condition|)
block|{
name|servers
operator|=
operator|new
name|SolrInstances
argument_list|(
literal|3
argument_list|,
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema_latest.xml"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterTests
specifier|public
specifier|static
name|void
name|afterTests
parameter_list|()
throws|throws
name|Exception
block|{
name|JSONTestUtil
operator|.
name|failRepeatedKeys
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|servers
operator|!=
literal|null
condition|)
block|{
name|servers
operator|.
name|stop
argument_list|()
expr_stmt|;
name|servers
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLocalJsonRequest
specifier|public
name|void
name|testLocalJsonRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|doJsonRequest
argument_list|(
name|Client
operator|.
name|localClient
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDistribJsonRequest
specifier|public
name|void
name|testDistribJsonRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|initServers
argument_list|()
expr_stmt|;
name|initServers
argument_list|()
expr_stmt|;
name|Client
name|client
init|=
name|servers
operator|.
name|getClient
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|queryDefaults
argument_list|()
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|servers
operator|.
name|getShards
argument_list|()
argument_list|)
expr_stmt|;
name|doJsonRequest
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|doJsonRequest
specifier|public
specifier|static
name|void
name|doJsonRequest
parameter_list|(
name|Client
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"A"
argument_list|,
literal|"where_s"
argument_list|,
literal|"NY"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"B"
argument_list|,
literal|"where_s"
argument_list|,
literal|"NJ"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"A"
argument_list|,
literal|"where_s"
argument_list|,
literal|"NJ"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"B"
argument_list|,
literal|"where_s"
argument_list|,
literal|"NJ"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"B"
argument_list|,
literal|"where_s"
argument_list|,
literal|"NY"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// test json param
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{query:'cat_s:A'}"
argument_list|)
argument_list|,
literal|"response/numFound==2"
argument_list|)
expr_stmt|;
comment|// test multiple json params
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{query:'cat_s:A'}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:'where_s:NY'}"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test multiple json params with one being zero length
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{query:'cat_s:A'}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:'where_s:NY'}"
argument_list|,
literal|"json"
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test multiple json params with one being a comment
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{query:'cat_s:A'}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:'where_s:NY'}"
argument_list|,
literal|"json"
argument_list|,
literal|"/* */"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test merging multi-valued params into list
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{query:'*:*'}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:'where_s:NY'}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:'cat_s:A'}"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test merging multi-valued params into list, second value is already list
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{query:'*:*'}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:'where_s:NY'}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:['cat_s:A']}"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test merging multi-valued params into list, first value is already list
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{query:'*:*'}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:['where_s:NY']}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:'cat_s:A'}"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test merging multi-valued params into list, both values are already list
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{query:'*:*'}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:['where_s:NY']}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:['cat_s:A']}"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test inserting and merging with paths
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json.query"
argument_list|,
literal|"'*:*'"
argument_list|,
literal|"json.filter"
argument_list|,
literal|"'where_s:NY'"
argument_list|,
literal|"json.filter"
argument_list|,
literal|"'cat_s:A'"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test inserting and merging with paths with an empty string and a comment
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json.query"
argument_list|,
literal|"'*:*'"
argument_list|,
literal|"json.filter"
argument_list|,
literal|"'where_s:NY'"
argument_list|,
literal|"json.filter"
argument_list|,
literal|"'cat_s:A'"
argument_list|,
literal|"json.filter"
argument_list|,
literal|""
argument_list|,
literal|"json.filter"
argument_list|,
literal|"/* */"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test overwriting of non-multivalued params
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json.query"
argument_list|,
literal|"'foo_s:NONE'"
argument_list|,
literal|"json.filter"
argument_list|,
literal|"'where_s:NY'"
argument_list|,
literal|"json.filter"
argument_list|,
literal|"'cat_s:A'"
argument_list|,
literal|"json.query"
argument_list|,
literal|"'*:*'"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// normal parameter specified in the params block, including numeric params cast back to string
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{params:{q:'*:*', fq:['cat_s:A','where_s:NY'], start:0, rows:5, fl:id}}"
argument_list|)
argument_list|,
literal|"response/docs==[{id:'1'}]"
argument_list|)
expr_stmt|;
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{params:{q:'*:*', fq:['cat_s:A','where_s:(NY OR NJ)'], start:0, rows:1, fl:id, sort:'where_s asc'}}"
argument_list|)
argument_list|,
literal|"response/numFound==2"
argument_list|,
literal|"response/docs==[{id:'4'}]"
argument_list|)
expr_stmt|;
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{params:{q:'*:*', fq:['cat_s:A','where_s:(NY OR NJ)'], start:0, rows:1, fl:[id,'x:5.5'], sort:'where_s asc'}}"
argument_list|)
argument_list|,
literal|"response/numFound==2"
argument_list|,
literal|"response/docs==[{id:'4', x:5.5}]"
argument_list|)
expr_stmt|;
comment|// test merge params
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{params:{q:'*:*'}}"
argument_list|,
literal|"json"
argument_list|,
literal|"{params:{fq:['cat_s:A','where_s:(NY OR NJ)'], start:0, rows:1, fl:[id,'x:5.5']}}"
argument_list|,
literal|"json"
argument_list|,
literal|"{params:{sort:'where_s asc'}}"
argument_list|)
argument_list|,
literal|"response/numFound==2"
argument_list|,
literal|"response/docs==[{id:'4', x:5.5}]"
argument_list|)
expr_stmt|;
comment|// test offset/limit/sort/fields
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json.query"
argument_list|,
literal|"'*:*'"
argument_list|,
literal|"json.offset"
argument_list|,
literal|"1"
argument_list|,
literal|"json.limit"
argument_list|,
literal|"2"
argument_list|,
literal|"json.sort"
argument_list|,
literal|"'id desc'"
argument_list|,
literal|"json.fields"
argument_list|,
literal|"'id'"
argument_list|)
argument_list|,
literal|"response/docs==[{id:'5'},{id:'4'}]"
argument_list|)
expr_stmt|;
comment|// test offset/limit/sort/fields, multi-valued json.fields
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json.query"
argument_list|,
literal|"'*:*'"
argument_list|,
literal|"json.offset"
argument_list|,
literal|"1"
argument_list|,
literal|"json.limit"
argument_list|,
literal|"2"
argument_list|,
literal|"json.sort"
argument_list|,
literal|"'id desc'"
argument_list|,
literal|"json.fields"
argument_list|,
literal|"'id'"
argument_list|,
literal|"json.fields"
argument_list|,
literal|"'x:5.5'"
argument_list|)
argument_list|,
literal|"response/docs==[{id:'5', x:5.5},{id:'4', x:5.5}]"
argument_list|)
expr_stmt|;
comment|// test offset/limit/sort/fields, overwriting non-multivalued params
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json.query"
argument_list|,
literal|"'*:*'"
argument_list|,
literal|"json.offset"
argument_list|,
literal|"17"
argument_list|,
literal|"json.offset"
argument_list|,
literal|"1"
argument_list|,
literal|"json.limit"
argument_list|,
literal|"42"
argument_list|,
literal|"json.limit"
argument_list|,
literal|"2"
argument_list|,
literal|"json.sort"
argument_list|,
literal|"'id asc'"
argument_list|,
literal|"json.sort"
argument_list|,
literal|"'id desc'"
argument_list|,
literal|"json.fields"
argument_list|,
literal|"'id'"
argument_list|,
literal|"json.fields"
argument_list|,
literal|"'x:5.5'"
argument_list|)
argument_list|,
literal|"response/docs==[{id:'5', x:5.5},{id:'4', x:5.5}]"
argument_list|)
expr_stmt|;
comment|// test templating before parsing JSON
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"${OPENBRACE} query:'cat_s:A' ${CLOSEBRACE}"
argument_list|,
literal|"json"
argument_list|,
literal|"${OPENBRACE} filter:'where_s:NY'${CLOSEBRACE}"
argument_list|,
literal|"OPENBRACE"
argument_list|,
literal|"{"
argument_list|,
literal|"CLOSEBRACE"
argument_list|,
literal|"}"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test templating with params defined in the JSON itself!  Do we want to keep this functionality?
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{params:{V1:A,V2:NY}, query:'cat_s:${V1}'}"
argument_list|,
literal|"json"
argument_list|,
literal|"{filter:'where_s:${V2}'}"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|//
comment|// with body
comment|//
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"stream.body"
argument_list|,
literal|"{query:'cat_s:A'}"
argument_list|,
literal|"stream.contentType"
argument_list|,
literal|"application/json"
argument_list|)
argument_list|,
literal|"response/numFound==2"
argument_list|)
expr_stmt|;
comment|// test body in conjunction with query params
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"stream.body"
argument_list|,
literal|"{query:'cat_s:A'}"
argument_list|,
literal|"stream.contentType"
argument_list|,
literal|"application/json"
argument_list|,
literal|"json.filter"
argument_list|,
literal|"'where_s:NY'"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test that json body in params come "after" (will overwrite)
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"stream.body"
argument_list|,
literal|"{query:'*:*', filter:'where_s:NY'}"
argument_list|,
literal|"stream.contentType"
argument_list|,
literal|"application/json"
argument_list|,
literal|"json"
argument_list|,
literal|"{query:'cat_s:A'}"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test that json.x params come after body
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"stream.body"
argument_list|,
literal|"{query:'*:*', filter:'where_s:NY'}"
argument_list|,
literal|"stream.contentType"
argument_list|,
literal|"application/json"
argument_list|,
literal|"json.query"
argument_list|,
literal|"'cat_s:A'"
argument_list|)
argument_list|,
literal|"response/numFound==1"
argument_list|)
expr_stmt|;
comment|// test facet with json body
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"stream.body"
argument_list|,
literal|"{query:'*:*', facet:{x:'unique(where_s)'}}"
argument_list|,
literal|"stream.contentType"
argument_list|,
literal|"application/json"
argument_list|)
argument_list|,
literal|"facets=={count:6,x:2}"
argument_list|)
expr_stmt|;
comment|// test facet with json body, insert additional facets via query parameter
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"stream.body"
argument_list|,
literal|"{query:'*:*', facet:{x:'unique(where_s)'}}"
argument_list|,
literal|"stream.contentType"
argument_list|,
literal|"application/json"
argument_list|,
literal|"json.facet.y"
argument_list|,
literal|"{terms:{field:where_s}}"
argument_list|,
literal|"json.facet.z"
argument_list|,
literal|"'unique(where_s)'"
argument_list|)
argument_list|,
literal|"facets=={count:6,x:2, y:{buckets:[{val:NJ,count:3},{val:NY,count:2}]}, z:2}"
argument_list|)
expr_stmt|;
comment|// test debug
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{query:'cat_s:A'}"
argument_list|,
literal|"json.filter"
argument_list|,
literal|"'where_s:NY'"
argument_list|,
literal|"debug"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"debug/json=={query:'cat_s:A', filter:'where_s:NY'}"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// test failure on unknown parameter
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
literal|"json"
argument_list|,
literal|"{query:'cat_s:A', foobar_ignore_exception:5}"
argument_list|)
argument_list|,
literal|"response/numFound==2"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
