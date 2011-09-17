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
name|lucene
operator|.
name|analysis
operator|.
name|reverse
operator|.
name|ReverseStringFilter
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
name|grouping
operator|.
name|SearchGroup
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
name|util
operator|.
name|BytesRef
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
name|GroupParams
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
name|search
operator|.
name|Grouping
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
name|distributed
operator|.
name|ShardRequestFactory
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Concrete implementation of {@link ShardRequestFactory} that creates {@link ShardRequest} instances for getting the  * top groups from all shards.  */
end_comment
begin_class
DECL|class|TopGroupsShardRequestFactory
specifier|public
class|class
name|TopGroupsShardRequestFactory
implements|implements
name|ShardRequestFactory
block|{
comment|/**    * Represents a string value for    */
DECL|field|GROUP_NULL_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_NULL_VALUE
init|=
literal|""
operator|+
name|ReverseStringFilter
operator|.
name|START_OF_HEADING_MARKER
decl_stmt|;
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
comment|// If we have a group.query we need to query all shards... Or we move this to the group first phase queries
name|boolean
name|containsGroupByQuery
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getQueries
argument_list|()
operator|.
name|length
operator|>
literal|0
decl_stmt|;
comment|// TODO: If groups.truncate=true we only have to query the specific shards even faceting and statistics are enabled
if|if
condition|(
operator|(
name|rb
operator|.
name|getQueryCommand
argument_list|()
operator|.
name|getFlags
argument_list|()
operator|&
name|SolrIndexSearcher
operator|.
name|GET_DOCSET
operator|)
operator|!=
literal|0
operator|||
name|containsGroupByQuery
condition|)
block|{
comment|// In case we need more results such as faceting and statistics we have to query all shards
return|return
name|createRequestForAllShards
argument_list|(
name|rb
argument_list|)
return|;
block|}
else|else
block|{
comment|// In case we only need top groups we only have to query the shards that contain these groups.
return|return
name|createRequestForSpecificShards
argument_list|(
name|rb
argument_list|)
return|;
block|}
block|}
DECL|method|createRequestForSpecificShards
specifier|private
name|ShardRequest
index|[]
name|createRequestForSpecificShards
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
comment|// Determine all unique shards to query for TopGroups
name|Set
argument_list|<
name|String
argument_list|>
name|shards
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|command
range|:
name|rb
operator|.
name|searchGroupToShard
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|,
name|String
argument_list|>
name|groupsToShard
init|=
name|rb
operator|.
name|searchGroupToShard
operator|.
name|get
argument_list|(
name|command
argument_list|)
decl_stmt|;
name|shards
operator|.
name|addAll
argument_list|(
name|groupsToShard
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ShardRequest
index|[]
name|sreqs
init|=
operator|new
name|ShardRequest
index|[
name|shards
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|shard
range|:
name|shards
control|)
block|{
name|ShardRequest
name|sreq
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|purpose
operator|=
name|ShardRequest
operator|.
name|PURPOSE_GET_TOP_IDS
expr_stmt|;
name|sreq
operator|.
name|actualShards
operator|=
operator|new
name|String
index|[]
block|{
name|shard
block|}
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
comment|// If group.format=simple group.offset doesn't make sense
name|Grouping
operator|.
name|Format
name|responseFormat
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getResponseFormat
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseFormat
operator|==
name|Grouping
operator|.
name|Format
operator|.
name|simple
operator|||
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|isMain
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|GroupParams
operator|.
name|GROUP_OFFSET
argument_list|)
expr_stmt|;
block|}
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
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"group.distibuted.second"
argument_list|,
literal|"true"
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
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
name|entry
range|:
name|rb
operator|.
name|mergedSearchGroups
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
name|searchGroup
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|String
name|groupValue
decl_stmt|;
if|if
condition|(
name|searchGroup
operator|.
name|groupValue
operator|!=
literal|null
condition|)
block|{
name|String
name|rawGroupValue
init|=
name|searchGroup
operator|.
name|groupValue
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|FieldType
name|fieldType
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
name|groupValue
operator|=
name|fieldType
operator|.
name|indexedToReadable
argument_list|(
name|rawGroupValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|groupValue
operator|=
name|GROUP_NULL_VALUE
expr_stmt|;
block|}
name|sreq
operator|.
name|params
operator|.
name|add
argument_list|(
literal|"group.topgroups."
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|groupValue
argument_list|)
expr_stmt|;
block|}
block|}
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
name|sreqs
index|[
name|i
operator|++
index|]
operator|=
name|sreq
expr_stmt|;
block|}
return|return
name|sreqs
return|;
block|}
DECL|method|createRequestForAllShards
specifier|private
name|ShardRequest
index|[]
name|createRequestForAllShards
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
name|sreq
operator|.
name|purpose
operator|=
name|ShardRequest
operator|.
name|PURPOSE_GET_TOP_IDS
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
comment|// If group.format=simple group.offset doesn't make sense
name|Grouping
operator|.
name|Format
name|responseFormat
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getResponseFormat
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseFormat
operator|==
name|Grouping
operator|.
name|Format
operator|.
name|simple
operator|||
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|isMain
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|remove
argument_list|(
name|GroupParams
operator|.
name|GROUP_OFFSET
argument_list|)
expr_stmt|;
block|}
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
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"group.distibuted.second"
argument_list|,
literal|"true"
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
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
name|entry
range|:
name|rb
operator|.
name|mergedSearchGroups
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
name|searchGroup
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|String
name|groupValue
decl_stmt|;
if|if
condition|(
name|searchGroup
operator|.
name|groupValue
operator|!=
literal|null
condition|)
block|{
name|String
name|rawGroupValue
init|=
name|searchGroup
operator|.
name|groupValue
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|FieldType
name|fieldType
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
name|groupValue
operator|=
name|fieldType
operator|.
name|indexedToReadable
argument_list|(
name|rawGroupValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|groupValue
operator|=
name|GROUP_NULL_VALUE
expr_stmt|;
block|}
name|sreq
operator|.
name|params
operator|.
name|add
argument_list|(
literal|"group.topgroups."
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|groupValue
argument_list|)
expr_stmt|;
block|}
block|}
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
