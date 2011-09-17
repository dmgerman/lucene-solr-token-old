begin_unit
begin_package
DECL|package|org.apache.solr.search.grouping.distributed.responseprocessor
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
name|responseprocessor
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
name|handler
operator|.
name|component
operator|.
name|ShardResponse
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
name|SortSpec
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
name|ShardResponseProcessor
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
name|shardresultserializer
operator|.
name|SearchGroupsResultTransformer
import|;
end_import
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
name|*
import|;
end_import
begin_comment
comment|/**  * Concrete implementation for merging {@link SearchGroup} instances from shard responses.  */
end_comment
begin_class
DECL|class|SearchGroupShardResponseProcessor
specifier|public
class|class
name|SearchGroupShardResponseProcessor
implements|implements
name|ShardResponseProcessor
block|{
comment|/**    * {@inheritDoc}    */
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|shardRequest
parameter_list|)
block|{
name|SortSpec
name|ss
init|=
name|rb
operator|.
name|getSortSpec
argument_list|()
decl_stmt|;
name|Sort
name|groupSort
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getGroupSort
argument_list|()
decl_stmt|;
name|String
index|[]
name|fields
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|commandSearchGroups
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|,
name|String
argument_list|>
argument_list|>
name|tempSearchGroupToShard
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|commandSearchGroups
operator|.
name|put
argument_list|(
name|field
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
argument_list|(
name|shardRequest
operator|.
name|responses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tempSearchGroupToShard
operator|.
name|put
argument_list|(
name|field
argument_list|,
operator|new
name|HashMap
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|rb
operator|.
name|searchGroupToShard
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|rb
operator|.
name|searchGroupToShard
operator|.
name|put
argument_list|(
name|field
argument_list|,
operator|new
name|HashMap
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|SearchGroupsResultTransformer
name|serializer
init|=
operator|new
name|SearchGroupsResultTransformer
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|ShardResponse
name|srsp
range|:
name|shardRequest
operator|.
name|responses
control|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|firstPhaseResult
init|=
operator|(
name|NamedList
argument_list|<
name|NamedList
argument_list|>
operator|)
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"firstPhase"
argument_list|)
decl_stmt|;
name|Map
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
name|result
init|=
name|serializer
operator|.
name|transformToNative
argument_list|(
name|firstPhaseResult
argument_list|,
name|groupSort
argument_list|,
literal|null
argument_list|,
name|srsp
operator|.
name|getShard
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|commandSearchGroups
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|searchGroups
init|=
name|result
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|searchGroups
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|commandSearchGroups
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|.
name|add
argument_list|(
name|searchGroups
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
name|searchGroup
range|:
name|searchGroups
control|)
block|{
name|tempSearchGroupToShard
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|.
name|put
argument_list|(
name|searchGroup
argument_list|,
name|srsp
operator|.
name|getShard
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|String
name|groupField
range|:
name|commandSearchGroups
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
name|topGroups
init|=
name|commandSearchGroups
operator|.
name|get
argument_list|(
name|groupField
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|mergedTopGroups
init|=
name|SearchGroup
operator|.
name|merge
argument_list|(
name|topGroups
argument_list|,
name|ss
operator|.
name|getOffset
argument_list|()
argument_list|,
name|ss
operator|.
name|getCount
argument_list|()
argument_list|,
name|groupSort
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergedTopGroups
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|rb
operator|.
name|mergedSearchGroups
operator|.
name|put
argument_list|(
name|groupField
argument_list|,
name|mergedTopGroups
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchGroup
argument_list|<
name|BytesRef
argument_list|>
name|mergedTopGroup
range|:
name|mergedTopGroups
control|)
block|{
name|rb
operator|.
name|searchGroupToShard
operator|.
name|get
argument_list|(
name|groupField
argument_list|)
operator|.
name|put
argument_list|(
name|mergedTopGroup
argument_list|,
name|tempSearchGroupToShard
operator|.
name|get
argument_list|(
name|groupField
argument_list|)
operator|.
name|get
argument_list|(
name|mergedTopGroup
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
