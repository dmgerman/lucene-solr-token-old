begin_unit
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|params
operator|.
name|CommonParams
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|SolrQueryResponse
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
DECL|class|DebugComponentTest
specifier|public
class|class
name|DebugComponentTest
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
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"title"
argument_list|,
literal|"this is a title."
argument_list|,
literal|"inStock_b1"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"title"
argument_list|,
literal|"this is another title."
argument_list|,
literal|"inStock_b1"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"title"
argument_list|,
literal|"Mary had a little lamb."
argument_list|,
literal|"inStock_b1"
argument_list|,
literal|"false"
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
DECL|method|testBasicInterface
specifier|public
name|void
name|testBasicInterface
parameter_list|()
throws|throws
name|Exception
block|{
comment|//make sure the basics are in place
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//str[@name='rawquerystring']='*:*'"
argument_list|,
literal|"//str[@name='querystring']='*:*'"
argument_list|,
literal|"//str[@name='parsedquery']='MatchAllDocsQuery(*:*)'"
argument_list|,
literal|"//str[@name='parsedquery_toString']='*:*'"
argument_list|,
literal|"count(//lst[@name='explain']/*)=3"
argument_list|,
literal|"//lst[@name='explain']/str[@name='1']"
argument_list|,
literal|"//lst[@name='explain']/str[@name='2']"
argument_list|,
literal|"//lst[@name='explain']/str[@name='3']"
argument_list|,
literal|"//str[@name='QParser']"
argument_list|,
comment|// make sure the QParser is specified
literal|"count(//lst[@name='timing']/*)=3"
argument_list|,
comment|//should be three pieces to timings
literal|"//lst[@name='timing']/double[@name='time']"
argument_list|,
comment|//make sure we have a time value, but don't specify it's result
literal|"count(//lst[@name='prepare']/*)>0"
argument_list|,
literal|"//lst[@name='prepare']/double[@name='time']"
argument_list|,
literal|"count(//lst[@name='process']/*)>0"
argument_list|,
literal|"//lst[@name='process']/double[@name='time']"
argument_list|)
expr_stmt|;
block|}
comment|// Test the ability to specify which pieces to include
annotation|@
name|Test
DECL|method|testPerItemInterface
specifier|public
name|void
name|testPerItemInterface
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Same as debugQuery = true
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"debug"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//str[@name='rawquerystring']='*:*'"
argument_list|,
literal|"//str[@name='querystring']='*:*'"
argument_list|,
literal|"//str[@name='parsedquery']='MatchAllDocsQuery(*:*)'"
argument_list|,
literal|"//str[@name='parsedquery_toString']='*:*'"
argument_list|,
literal|"//str[@name='QParser']"
argument_list|,
comment|// make sure the QParser is specified
literal|"count(//lst[@name='explain']/*)=3"
argument_list|,
literal|"//lst[@name='explain']/str[@name='1']"
argument_list|,
literal|"//lst[@name='explain']/str[@name='2']"
argument_list|,
literal|"//lst[@name='explain']/str[@name='3']"
argument_list|,
literal|"count(//lst[@name='timing']/*)=3"
argument_list|,
comment|//should be three pieces to timings
literal|"//lst[@name='timing']/double[@name='time']"
argument_list|,
comment|//make sure we have a time value, but don't specify it's result
literal|"count(//lst[@name='prepare']/*)>0"
argument_list|,
literal|"//lst[@name='prepare']/double[@name='time']"
argument_list|,
literal|"count(//lst[@name='process']/*)>0"
argument_list|,
literal|"//lst[@name='process']/double[@name='time']"
argument_list|)
expr_stmt|;
comment|//timing only
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"debug"
argument_list|,
name|CommonParams
operator|.
name|TIMING
argument_list|)
argument_list|,
literal|"count(//str[@name='rawquerystring'])=0"
argument_list|,
literal|"count(//str[@name='querystring'])=0"
argument_list|,
literal|"count(//str[@name='parsedquery'])=0"
argument_list|,
literal|"count(//str[@name='parsedquery_toString'])=0"
argument_list|,
literal|"count(//lst[@name='explain']/*)=0"
argument_list|,
literal|"count(//str[@name='QParser'])=0"
argument_list|,
comment|// make sure the QParser is specified
literal|"count(//lst[@name='timing']/*)=3"
argument_list|,
comment|//should be three pieces to timings
literal|"//lst[@name='timing']/double[@name='time']"
argument_list|,
comment|//make sure we have a time value, but don't specify it's result
literal|"count(//lst[@name='prepare']/*)>0"
argument_list|,
literal|"//lst[@name='prepare']/double[@name='time']"
argument_list|,
literal|"count(//lst[@name='process']/*)>0"
argument_list|,
literal|"//lst[@name='process']/double[@name='time']"
argument_list|)
expr_stmt|;
comment|//query only
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"debug"
argument_list|,
name|CommonParams
operator|.
name|QUERY
argument_list|)
argument_list|,
literal|"//str[@name='rawquerystring']='*:*'"
argument_list|,
literal|"//str[@name='querystring']='*:*'"
argument_list|,
literal|"//str[@name='parsedquery']='MatchAllDocsQuery(*:*)'"
argument_list|,
literal|"//str[@name='parsedquery_toString']='*:*'"
argument_list|,
literal|"count(//lst[@name='explain']/*)=0"
argument_list|,
literal|"//str[@name='QParser']"
argument_list|,
comment|// make sure the QParser is specified
literal|"count(//lst[@name='timing']/*)=0"
argument_list|)
expr_stmt|;
comment|//explains
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"debug"
argument_list|,
name|CommonParams
operator|.
name|RESULTS
argument_list|)
argument_list|,
literal|"count(//str[@name='rawquerystring'])=0"
argument_list|,
literal|"count(//str[@name='querystring'])=0"
argument_list|,
literal|"count(//str[@name='parsedquery'])=0"
argument_list|,
literal|"count(//str[@name='parsedquery_toString'])=0"
argument_list|,
literal|"count(//lst[@name='explain']/*)=3"
argument_list|,
literal|"//lst[@name='explain']/str[@name='1']"
argument_list|,
literal|"//lst[@name='explain']/str[@name='2']"
argument_list|,
literal|"//lst[@name='explain']/str[@name='3']"
argument_list|,
literal|"count(//str[@name='QParser'])=0"
argument_list|,
comment|// make sure the QParser is specified
literal|"count(//lst[@name='timing']/*)=0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"debug"
argument_list|,
name|CommonParams
operator|.
name|RESULTS
argument_list|,
literal|"debug"
argument_list|,
name|CommonParams
operator|.
name|QUERY
argument_list|)
argument_list|,
literal|"//str[@name='rawquerystring']='*:*'"
argument_list|,
literal|"//str[@name='querystring']='*:*'"
argument_list|,
literal|"//str[@name='parsedquery']='MatchAllDocsQuery(*:*)'"
argument_list|,
literal|"//str[@name='parsedquery_toString']='*:*'"
argument_list|,
literal|"//str[@name='QParser']"
argument_list|,
comment|// make sure the QParser is specified
literal|"count(//lst[@name='explain']/*)=3"
argument_list|,
literal|"//lst[@name='explain']/str[@name='1']"
argument_list|,
literal|"//lst[@name='explain']/str[@name='2']"
argument_list|,
literal|"//lst[@name='explain']/str[@name='3']"
argument_list|,
literal|"count(//lst[@name='timing']/*)=0"
argument_list|)
expr_stmt|;
comment|//Grouping
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"debug"
argument_list|,
name|CommonParams
operator|.
name|RESULTS
argument_list|,
literal|"group"
argument_list|,
name|CommonParams
operator|.
name|TRUE
argument_list|,
literal|"group.field"
argument_list|,
literal|"inStock_b1"
argument_list|,
literal|"debug"
argument_list|,
name|CommonParams
operator|.
name|TRUE
argument_list|)
argument_list|,
literal|"//str[@name='rawquerystring']='*:*'"
argument_list|,
literal|"count(//lst[@name='explain']/*)=2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testModifyRequestTrack
specifier|public
name|void
name|testModifyRequestTrack
parameter_list|()
block|{
name|DebugComponent
name|component
init|=
operator|new
name|DebugComponent
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SearchComponent
argument_list|>
name|components
init|=
operator|new
name|ArrayList
argument_list|<
name|SearchComponent
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|components
operator|.
name|add
argument_list|(
name|component
argument_list|)
expr_stmt|;
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
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"test query"
argument_list|,
literal|"distrib"
argument_list|,
literal|"true"
argument_list|,
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|,
literal|"123456-my_rid"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|resp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|ResponseBuilder
name|rb
init|=
operator|new
name|ResponseBuilder
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
name|components
argument_list|)
decl_stmt|;
name|ShardRequest
name|sreq
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|sreq
operator|.
name|purpose
operator|=
name|ShardRequest
operator|.
name|PURPOSE_GET_FIELDS
expr_stmt|;
name|sreq
operator|.
name|purpose
operator||=
name|ShardRequest
operator|.
name|PURPOSE_GET_DEBUG
expr_stmt|;
comment|//expecting the same results with debugQuery=true or debug=track
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|rb
operator|.
name|setDebug
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rb
operator|.
name|setDebug
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|rb
operator|.
name|setDebugTrack
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//should not depend on other debug options
name|rb
operator|.
name|setDebugQuery
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|rb
operator|.
name|setDebugTimings
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|rb
operator|.
name|setDebugResults
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|component
operator|.
name|modifyRequest
argument_list|(
name|rb
argument_list|,
literal|null
argument_list|,
name|sreq
argument_list|)
expr_stmt|;
comment|//if the request has debugQuery=true or debug=track, the sreq should get debug=track always
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|sreq
operator|.
name|params
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
name|CommonParams
operator|.
name|TRACK
argument_list|)
argument_list|)
expr_stmt|;
comment|//the purpose must be added as readable param to be included in the shard logs
name|assertEquals
argument_list|(
literal|"GET_FIELDS,GET_DEBUG"
argument_list|,
name|sreq
operator|.
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|REQUEST_PURPOSE
argument_list|)
argument_list|)
expr_stmt|;
comment|//the rid must be added to be included in the shard logs
name|assertEquals
argument_list|(
literal|"123456-my_rid"
argument_list|,
name|sreq
operator|.
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPrepare
specifier|public
name|void
name|testPrepare
parameter_list|()
throws|throws
name|IOException
block|{
name|DebugComponent
name|component
init|=
operator|new
name|DebugComponent
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SearchComponent
argument_list|>
name|components
init|=
operator|new
name|ArrayList
argument_list|<
name|SearchComponent
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|components
operator|.
name|add
argument_list|(
name|component
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
decl_stmt|;
name|ResponseBuilder
name|rb
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
name|req
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"test query"
argument_list|,
literal|"distrib"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|rb
operator|=
operator|new
name|ResponseBuilder
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|components
argument_list|)
expr_stmt|;
name|rb
operator|.
name|isDistrib
operator|=
literal|true
expr_stmt|;
comment|//expecting the same results with debugQuery=true or debug=track
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|rb
operator|.
name|setDebug
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rb
operator|.
name|setDebug
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|rb
operator|.
name|setDebugTrack
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//should not depend on other debug options
name|rb
operator|.
name|setDebugQuery
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|rb
operator|.
name|setDebugTimings
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|rb
operator|.
name|setDebugResults
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|component
operator|.
name|prepare
argument_list|(
name|rb
argument_list|)
expr_stmt|;
name|ensureRidPresent
argument_list|(
name|rb
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|req
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"test query"
argument_list|,
literal|"distrib"
argument_list|,
literal|"true"
argument_list|,
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|rb
operator|=
operator|new
name|ResponseBuilder
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|components
argument_list|)
expr_stmt|;
name|rb
operator|.
name|isDistrib
operator|=
literal|true
expr_stmt|;
name|rb
operator|.
name|setDebug
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|component
operator|.
name|prepare
argument_list|(
name|rb
argument_list|)
expr_stmt|;
name|ensureRidPresent
argument_list|(
name|rb
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|ensureRidPresent
specifier|private
name|void
name|ensureRidPresent
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|String
name|expectedRid
parameter_list|)
block|{
name|SolrQueryRequest
name|req
init|=
name|rb
operator|.
name|req
decl_stmt|;
name|SolrQueryResponse
name|resp
init|=
name|rb
operator|.
name|rsp
decl_stmt|;
comment|//a generated request ID should be added to the request
name|String
name|rid
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedRid
operator|==
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|rid
operator|+
literal|" Doesn't match expected pattern."
argument_list|,
name|Pattern
operator|.
name|matches
argument_list|(
literal|".*-collection1-[0-9]*-[0-9]+"
argument_list|,
name|rid
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"Expecting "
operator|+
name|expectedRid
operator|+
literal|" but found "
operator|+
name|rid
argument_list|,
name|expectedRid
argument_list|,
name|rid
argument_list|)
expr_stmt|;
block|}
comment|//The request ID is added to the debug/track section
name|assertEquals
argument_list|(
name|rid
argument_list|,
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|rb
operator|.
name|getDebugInfo
argument_list|()
operator|.
name|get
argument_list|(
literal|"track"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|)
argument_list|)
expr_stmt|;
comment|//RID must be added to the toLog, so that it's included in the main request log
name|assertEquals
argument_list|(
name|rid
argument_list|,
name|resp
operator|.
name|getToLog
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
