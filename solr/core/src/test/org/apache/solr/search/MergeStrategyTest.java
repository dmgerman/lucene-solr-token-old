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
name|BaseDistributedSearchTestCase
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|SolrDocument
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
name|SolrDocumentList
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
name|handler
operator|.
name|component
operator|.
name|MergeStrategy
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|handler
operator|.
name|component
operator|.
name|ShardRequest
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/**  * Test for QueryComponent's distributed querying  *  * @see org.apache.solr.handler.component.QueryComponent  */
end_comment
begin_class
DECL|class|MergeStrategyTest
specifier|public
class|class
name|MergeStrategyTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|MergeStrategyTest
specifier|public
name|MergeStrategyTest
parameter_list|()
block|{
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|shardCount
operator|=
literal|3
expr_stmt|;
name|stress
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|setUpBeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-plugcollector.xml"
argument_list|,
literal|"schema15.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|0
argument_list|,
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"sort_i"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|0
argument_list|,
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"sort_i"
argument_list|,
literal|"50"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|1
argument_list|,
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"sort_i"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|1
argument_list|,
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"sort_i"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|0
argument_list|,
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"sort_i"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|1
argument_list|,
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"sort_i"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|2
argument_list|,
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"sort_i"
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|2
argument_list|,
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"sort_i"
argument_list|,
literal|"1500"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|2
argument_list|,
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"sort_i"
argument_list|,
literal|"1300"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|1
argument_list|,
literal|"id"
argument_list|,
literal|"12"
argument_list|,
literal|"sort_i"
argument_list|,
literal|"15"
argument_list|)
expr_stmt|;
name|index_specific
argument_list|(
literal|1
argument_list|,
literal|"id"
argument_list|,
literal|"13"
argument_list|,
literal|"sort_i"
argument_list|,
literal|"16"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"explain"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"score"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"wt"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"distrib"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"shards.qt"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"shards"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"q"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"_version_"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
comment|//Test mergeStrategy that uses score
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"{!rank q=$qq}"
argument_list|,
literal|"qq"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"12"
argument_list|,
literal|"sort"
argument_list|,
literal|"sort_i asc"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
comment|//Test without mergeStrategy
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"12"
argument_list|,
literal|"sort"
argument_list|,
literal|"sort_i asc"
argument_list|)
expr_stmt|;
comment|//Test mergeStrategy1 that uses a sort field.
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"{!rank mergeStrategy=1 q=$qq}"
argument_list|,
literal|"qq"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"12"
argument_list|,
literal|"sort"
argument_list|,
literal|"sort_i asc"
argument_list|)
expr_stmt|;
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
literal|"qq"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"12"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"{!rank q=$qq}"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"sort_i asc"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
name|setDistributedParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|queryServer
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|assertOrder
argument_list|(
name|rsp
argument_list|,
literal|"10"
argument_list|,
literal|"11"
argument_list|,
literal|"9"
argument_list|,
literal|"2"
argument_list|,
literal|"13"
argument_list|,
literal|"12"
argument_list|,
literal|"6"
argument_list|,
literal|"1"
argument_list|,
literal|"5"
argument_list|,
literal|"8"
argument_list|,
literal|"7"
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
literal|"rows"
argument_list|,
literal|"12"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"sort"
argument_list|,
literal|"sort_i asc"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
name|setDistributedParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|queryServer
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|assertOrder
argument_list|(
name|rsp
argument_list|,
literal|"7"
argument_list|,
literal|"8"
argument_list|,
literal|"5"
argument_list|,
literal|"1"
argument_list|,
literal|"6"
argument_list|,
literal|"12"
argument_list|,
literal|"13"
argument_list|,
literal|"2"
argument_list|,
literal|"9"
argument_list|,
literal|"11"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|MergeStrategy
name|m1
init|=
operator|new
name|MergeStrategy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{       }
specifier|public
name|boolean
name|mergesIds
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|handlesMergeFields
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|handleMergeFields
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
decl_stmt|;
name|MergeStrategy
name|m2
init|=
operator|new
name|MergeStrategy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{       }
specifier|public
name|boolean
name|mergesIds
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|handlesMergeFields
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|handleMergeFields
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|100
return|;
block|}
block|}
decl_stmt|;
name|MergeStrategy
name|m3
init|=
operator|new
name|MergeStrategy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{       }
specifier|public
name|boolean
name|mergesIds
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|handlesMergeFields
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|handleMergeFields
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|50
return|;
block|}
block|}
decl_stmt|;
name|MergeStrategy
index|[]
name|merges
init|=
block|{
name|m1
block|,
name|m2
block|,
name|m3
block|}
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|merges
argument_list|,
name|MergeStrategy
operator|.
name|MERGE_COMP
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|merges
index|[
literal|0
index|]
operator|.
name|getCost
argument_list|()
operator|==
literal|1
operator|)
assert|;
assert|assert
operator|(
name|merges
index|[
literal|1
index|]
operator|.
name|getCost
argument_list|()
operator|==
literal|50
operator|)
assert|;
assert|assert
operator|(
name|merges
index|[
literal|2
index|]
operator|.
name|getCost
argument_list|()
operator|==
literal|100
operator|)
assert|;
block|}
DECL|method|assertOrder
specifier|private
name|void
name|assertOrder
parameter_list|(
name|QueryResponse
name|rsp
parameter_list|,
name|String
modifier|...
name|docs
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrDocumentList
name|list
init|=
name|rsp
operator|.
name|getResults
argument_list|()
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SolrDocument
name|doc
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|o
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|docs
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Order is not correct:"
operator|+
name|o
operator|+
literal|"!="
operator|+
name|docs
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
end_unit
