begin_unit
begin_package
DECL|package|org.apache.solr.search.grouping.distributed.requestfactory
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|requestfactory
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
name|params
operator|.
name|ShardParams
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
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
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
name|search
operator|.
name|grouping
operator|.
name|GroupingSpecification
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
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|ShardRequestFactory
import|;
end_import
begin_comment
comment|/**  * Concrete implementation of {@link ShardRequestFactory} that creates {@link ShardRequest} instances for getting the  * search groups from all shards.  */
end_comment
begin_class
DECL|class|SearchGroupsRequestFactory
specifier|public
class|class
name|SearchGroupsRequestFactory
implements|implements
name|ShardRequestFactory
block|{
comment|/**    * {@inheritDoc}    */
DECL|method|constructRequest
specifier|public
name|ShardRequest
index|[]
name|constructRequest
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|ShardRequest
name|sreq
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
name|GroupingSpecification
name|groupingSpecification
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupingSpecification
operator|.
name|getFields
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|ShardRequest
index|[
literal|0
index|]
return|;
block|}
name|sreq
operator|.
name|purpose
operator|=
name|ShardRequest
operator|.
name|PURPOSE_GET_TOP_GROUPS
expr_stmt|;
name|sreq
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: base on current params or original params?
comment|// don't pass through any shards param
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|ShardParams
operator|.
name|SHARDS
argument_list|)
expr_stmt|;
comment|// set the start (offset) to 0 for each shard request so we can properly merge
comment|// results from the start.
if|if
condition|(
name|rb
operator|.
name|shards_start
operator|>
operator|-
literal|1
condition|)
block|{
comment|// if the client set shards.start set this explicitly
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|,
name|rb
operator|.
name|shards_start
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: should we even use the SortSpec?  That's obtained from the QParser, and
comment|// perhaps we shouldn't attempt to parse the query at this level?
comment|// Alternate Idea: instead of specifying all these things at the upper level,
comment|// we could just specify that this is a shard request.
if|if
condition|(
name|rb
operator|.
name|shards_rows
operator|>
operator|-
literal|1
condition|)
block|{
comment|// if the client set shards.rows set this explicity
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|,
name|rb
operator|.
name|shards_rows
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|,
name|rb
operator|.
name|getSortSpec
argument_list|()
operator|.
name|getOffset
argument_list|()
operator|+
name|rb
operator|.
name|getSortSpec
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// in this first phase, request only the unique key field
comment|// and any fields needed for merging.
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"group.distibuted.first"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|rb
operator|.
name|getFieldFlags
argument_list|()
operator|&
name|SolrIndexSearcher
operator|.
name|GET_SCORES
operator|)
operator|!=
literal|0
operator|||
name|rb
operator|.
name|getSortSpec
argument_list|()
operator|.
name|includesScore
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
name|rb
operator|.
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|",score"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
name|rb
operator|.
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ShardRequest
index|[]
block|{
name|sreq
block|}
return|;
block|}
block|}
end_class
end_unit
