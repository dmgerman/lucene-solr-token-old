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
DECL|class|AnalyticsMergeStrategyTest
specifier|public
class|class
name|AnalyticsMergeStrategyTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|AnalyticsMergeStrategyTest
specifier|public
name|AnalyticsMergeStrategyTest
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
literal|"solrconfig-analytics-query.xml"
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
literal|"{!count}"
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
name|assertCount
argument_list|(
name|rsp
argument_list|,
literal|11
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
literal|"id:(1 2 5 6)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
literal|"{!count}"
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
name|assertCount
argument_list|(
name|rsp
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCount
specifier|private
name|void
name|assertCount
parameter_list|(
name|QueryResponse
name|rsp
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|NamedList
name|response
init|=
name|rsp
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|NamedList
name|analytics
init|=
operator|(
name|NamedList
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"analytics"
argument_list|)
decl_stmt|;
name|Integer
name|c
init|=
operator|(
name|Integer
operator|)
name|analytics
operator|.
name|get
argument_list|(
literal|"mycount"
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|intValue
argument_list|()
operator|!=
name|count
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Count is not correct:"
operator|+
name|count
operator|+
literal|":"
operator|+
name|c
operator|.
name|intValue
argument_list|()
argument_list|)
throw|;
block|}
name|long
name|numFound
init|=
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|intValue
argument_list|()
operator|!=
name|numFound
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Count does not equal numFound:"
operator|+
name|c
operator|.
name|intValue
argument_list|()
operator|+
literal|":"
operator|+
name|numFound
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit