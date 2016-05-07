begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|BooleanQuery
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
name|params
operator|.
name|MoreLikeThisParams
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
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|MoreLikeThisHandler
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
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|DocIterator
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
name|DocList
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
name|DocListAndSet
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
name|ReturnFields
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
name|SolrReturnFields
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_comment
comment|/**  * TODO!  *   *   * @since solr 1.3  */
end_comment
begin_class
DECL|class|MoreLikeThisComponent
specifier|public
class|class
name|MoreLikeThisComponent
extends|extends
name|SearchComponent
block|{
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"mlt"
decl_stmt|;
DECL|field|DIST_DOC_ID
specifier|public
specifier|static
specifier|final
name|String
name|DIST_DOC_ID
init|=
literal|"mlt.dist.id"
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|MoreLikeThisParams
operator|.
name|MLT
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|rb
operator|.
name|setNeedDocList
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|params
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|MoreLikeThisParams
operator|.
name|MLT
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|ReturnFields
name|returnFields
init|=
operator|new
name|SolrReturnFields
argument_list|(
name|rb
operator|.
name|req
argument_list|)
decl_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|returnFields
operator|.
name|wantsScore
argument_list|()
condition|)
block|{
name|flags
operator||=
name|SolrIndexSearcher
operator|.
name|GET_SCORES
expr_stmt|;
block|}
name|rb
operator|.
name|setFieldFlags
argument_list|(
name|flags
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Starting MoreLikeThis.Process.  isShard: "
operator|+
name|params
operator|.
name|getBool
argument_list|(
name|ShardParams
operator|.
name|IS_SHARD
argument_list|)
argument_list|)
expr_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|ShardParams
operator|.
name|IS_SHARD
argument_list|,
literal|false
argument_list|)
condition|)
block|{
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|MoreLikeThisComponent
operator|.
name|DIST_DOC_ID
argument_list|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// return empty response
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"moreLikeThis"
argument_list|,
operator|new
name|NamedList
argument_list|<
name|DocList
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|MoreLikeThisHandler
operator|.
name|MoreLikeThisHelper
name|mlt
init|=
operator|new
name|MoreLikeThisHandler
operator|.
name|MoreLikeThisHelper
argument_list|(
name|params
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|BooleanQuery
argument_list|>
name|bQuery
init|=
name|mlt
operator|.
name|getMoreLikeTheseQuery
argument_list|(
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docList
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|temp
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|BooleanQuery
argument_list|>
argument_list|>
name|idToQueryIt
init|=
name|bQuery
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|idToQueryIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|String
argument_list|,
name|BooleanQuery
argument_list|>
name|idToQuery
init|=
name|idToQueryIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|s
init|=
name|idToQuery
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"MLT Query:"
operator|+
name|s
argument_list|)
expr_stmt|;
name|temp
operator|.
name|add
argument_list|(
name|idToQuery
operator|.
name|getKey
argument_list|()
argument_list|,
name|idToQuery
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"moreLikeThis"
argument_list|,
name|temp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|NamedList
argument_list|<
name|DocList
argument_list|>
name|sim
init|=
name|getMoreLikeThese
argument_list|(
name|rb
argument_list|,
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docList
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"moreLikeThis"
argument_list|,
name|sim
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// non distrib case
name|NamedList
argument_list|<
name|DocList
argument_list|>
name|sim
init|=
name|getMoreLikeThese
argument_list|(
name|rb
argument_list|,
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docList
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"moreLikeThis"
argument_list|,
name|sim
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|handleResponses
specifier|public
name|void
name|handleResponses
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_TOP_IDS
operator|)
operator|!=
literal|0
operator|&&
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"ShardRequest.response.size: "
operator|+
name|sreq
operator|.
name|responses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardResponse
name|r
range|:
name|sreq
operator|.
name|responses
control|)
block|{
if|if
condition|(
name|r
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// This should only happen in case of using shards.tolerant=true. Omit this ShardResponse
continue|continue;
block|}
name|NamedList
argument_list|<
name|?
argument_list|>
name|moreLikeThisReponse
init|=
operator|(
name|NamedList
argument_list|<
name|?
argument_list|>
operator|)
name|r
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"moreLikeThis"
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"ShardRequest.response.shard: "
operator|+
name|r
operator|.
name|getShard
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|moreLikeThisReponse
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|entry
range|:
name|moreLikeThisReponse
control|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"id: \""
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"\" Query: \""
operator|+
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|ShardRequest
name|s
init|=
name|buildShardQuery
argument_list|(
name|rb
argument_list|,
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|rb
operator|.
name|addRequest
argument_list|(
name|this
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_MLT_RESULTS
operator|)
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|ShardResponse
name|r
range|:
name|sreq
operator|.
name|responses
control|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"MLT Query returned: "
operator|+
name|r
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|finishStage
specifier|public
name|void
name|finishStage
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
comment|// Handling Responses in finishStage, because solrResponse will put
comment|// moreLikeThis xml
comment|// segment ahead of result/response.
if|if
condition|(
name|rb
operator|.
name|stage
operator|==
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
operator|&&
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|COMPONENT_NAME
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|Object
argument_list|,
name|SolrDocumentList
argument_list|>
name|tempResults
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|mltcount
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getInt
argument_list|(
name|MoreLikeThisParams
operator|.
name|DOC_COUNT
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|String
name|keyName
init|=
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
decl_stmt|;
for|for
control|(
name|ShardRequest
name|sreq
range|:
name|rb
operator|.
name|finished
control|)
block|{
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_MLT_RESULTS
operator|)
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|ShardResponse
name|r
range|:
name|sreq
operator|.
name|responses
control|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"ShardRequest.response.shard: "
operator|+
name|r
operator|.
name|getShard
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|r
operator|.
name|getShardRequest
argument_list|()
operator|.
name|params
operator|.
name|get
argument_list|(
name|MoreLikeThisComponent
operator|.
name|DIST_DOC_ID
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|shardDocList
init|=
operator|(
name|SolrDocumentList
operator|)
name|r
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardDocList
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"MLT: results added for key: "
operator|+
name|key
operator|+
literal|" documents: "
operator|+
name|shardDocList
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//            if (log.isDebugEnabled()) {
comment|//              for (SolrDocument doc : shardDocList) {
comment|//                doc.addField("shard", "=" + r.getShard());
comment|//              }
comment|//            }
name|SolrDocumentList
name|mergedDocList
init|=
name|tempResults
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergedDocList
operator|==
literal|null
condition|)
block|{
name|mergedDocList
operator|=
operator|new
name|SolrDocumentList
argument_list|()
expr_stmt|;
name|mergedDocList
operator|.
name|addAll
argument_list|(
name|shardDocList
argument_list|)
expr_stmt|;
name|mergedDocList
operator|.
name|setNumFound
argument_list|(
name|shardDocList
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|mergedDocList
operator|.
name|setStart
argument_list|(
name|shardDocList
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|mergedDocList
operator|.
name|setMaxScore
argument_list|(
name|shardDocList
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mergedDocList
operator|=
name|mergeSolrDocumentList
argument_list|(
name|mergedDocList
argument_list|,
name|shardDocList
argument_list|,
name|mltcount
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Adding docs for key: "
operator|+
name|key
argument_list|)
expr_stmt|;
name|tempResults
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|mergedDocList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|NamedList
argument_list|<
name|SolrDocumentList
argument_list|>
name|list
init|=
name|buildMoreLikeThisNamed
argument_list|(
name|tempResults
argument_list|,
name|rb
operator|.
name|resultIds
argument_list|)
decl_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"moreLikeThis"
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|finishStage
argument_list|(
name|rb
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns NamedList based on the order of    * resultIds.shardDoc.positionInResponse    */
DECL|method|buildMoreLikeThisNamed
name|NamedList
argument_list|<
name|SolrDocumentList
argument_list|>
name|buildMoreLikeThisNamed
parameter_list|(
name|Map
argument_list|<
name|Object
argument_list|,
name|SolrDocumentList
argument_list|>
name|allMlt
parameter_list|,
name|Map
argument_list|<
name|Object
argument_list|,
name|ShardDoc
argument_list|>
name|resultIds
parameter_list|)
block|{
name|NamedList
argument_list|<
name|SolrDocumentList
argument_list|>
name|result
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Object
argument_list|>
name|sortingMap
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Object
argument_list|,
name|ShardDoc
argument_list|>
name|next
range|:
name|resultIds
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sortingMap
operator|.
name|put
argument_list|(
name|next
operator|.
name|getValue
argument_list|()
operator|.
name|positionInResponse
argument_list|,
name|next
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Object
name|key
range|:
name|sortingMap
operator|.
name|values
argument_list|()
control|)
block|{
name|SolrDocumentList
name|sdl
init|=
name|allMlt
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sdl
operator|==
literal|null
condition|)
block|{
name|sdl
operator|=
operator|new
name|SolrDocumentList
argument_list|()
expr_stmt|;
name|sdl
operator|.
name|setNumFound
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sdl
operator|.
name|setStart
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|,
name|sdl
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|mergeSolrDocumentList
specifier|public
name|SolrDocumentList
name|mergeSolrDocumentList
parameter_list|(
name|SolrDocumentList
name|one
parameter_list|,
name|SolrDocumentList
name|two
parameter_list|,
name|int
name|maxSize
parameter_list|,
name|String
name|idField
parameter_list|)
block|{
name|List
argument_list|<
name|SolrDocument
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// De-dup records sets. Shouldn't happen if indexed correctly.
name|Map
argument_list|<
name|String
argument_list|,
name|SolrDocument
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrDocument
name|doc
range|:
name|one
control|)
block|{
name|Object
name|id
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
name|idField
argument_list|)
decl_stmt|;
assert|assert
name|id
operator|!=
literal|null
operator|:
name|doc
operator|.
name|toString
argument_list|()
assert|;
name|map
operator|.
name|put
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|SolrDocument
name|doc
range|:
name|two
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|doc
operator|.
name|getFieldValue
argument_list|(
name|idField
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|l
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|map
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
comment|// Comparator to sort docs based on score. null scores/docs are set to 0.
comment|// hmm...we are ordering by scores that are not really comparable...
name|Comparator
argument_list|<
name|SolrDocument
argument_list|>
name|c
init|=
operator|new
name|Comparator
argument_list|<
name|SolrDocument
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|SolrDocument
name|o1
parameter_list|,
name|SolrDocument
name|o2
parameter_list|)
block|{
name|Float
name|f1
init|=
name|getFloat
argument_list|(
name|o1
argument_list|)
decl_stmt|;
name|Float
name|f2
init|=
name|getFloat
argument_list|(
name|o2
argument_list|)
decl_stmt|;
return|return
name|f2
operator|.
name|compareTo
argument_list|(
name|f1
argument_list|)
return|;
block|}
specifier|private
name|Float
name|getFloat
parameter_list|(
name|SolrDocument
name|doc
parameter_list|)
block|{
name|Float
name|f
init|=
literal|0f
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|Object
name|o
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|Float
condition|)
block|{
name|f
operator|=
operator|(
name|Float
operator|)
name|o
expr_stmt|;
block|}
block|}
return|return
name|f
return|;
block|}
block|}
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|l
argument_list|,
name|c
argument_list|)
expr_stmt|;
comment|// Truncate list to maxSize
if|if
condition|(
name|l
operator|.
name|size
argument_list|()
operator|>
name|maxSize
condition|)
block|{
name|l
operator|=
name|l
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|maxSize
argument_list|)
expr_stmt|;
block|}
comment|// Create SolrDocumentList Attributes from originals
name|SolrDocumentList
name|result
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMaxScore
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|one
operator|.
name|getMaxScore
argument_list|()
argument_list|,
name|two
operator|.
name|getMaxScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|setNumFound
argument_list|(
name|one
operator|.
name|getNumFound
argument_list|()
operator|+
name|two
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setStart
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|one
operator|.
name|getStart
argument_list|()
argument_list|,
name|two
operator|.
name|getStart
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|buildShardQuery
name|ShardRequest
name|buildShardQuery
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|String
name|q
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|ShardRequest
name|s
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
name|s
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
name|s
operator|.
name|purpose
operator||=
name|ShardRequest
operator|.
name|PURPOSE_GET_MLT_RESULTS
expr_stmt|;
comment|// Maybe unnecessary, but safe.
name|s
operator|.
name|purpose
operator||=
name|ShardRequest
operator|.
name|PURPOSE_PRIVATE
expr_stmt|;
name|s
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
comment|// s.params.remove(MoreLikeThisComponent.COMPONENT_NAME);
comment|// needed to correlate results
name|s
operator|.
name|params
operator|.
name|set
argument_list|(
name|MoreLikeThisComponent
operator|.
name|DIST_DOC_ID
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|s
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|mltcount
init|=
name|s
operator|.
name|params
operator|.
name|getInt
argument_list|(
name|MoreLikeThisParams
operator|.
name|DOC_COUNT
argument_list|,
literal|20
argument_list|)
decl_stmt|;
comment|// overrequest
name|s
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|,
name|mltcount
argument_list|)
expr_stmt|;
comment|// adding score to rank moreLikeThis
name|s
operator|.
name|params
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|)
expr_stmt|;
comment|// Should probably add something like this:
comment|// String fl = s.params.get(MoreLikeThisParams.RETURN_FL, "*");
comment|// if(fl != null){
comment|// s.params.set(CommonParams.FL, fl + ",score");
comment|// }
name|String
name|id
init|=
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
decl_stmt|;
name|s
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"score,"
operator|+
name|id
argument_list|)
expr_stmt|;
name|s
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"sort"
argument_list|,
literal|"score desc"
argument_list|)
expr_stmt|;
comment|// MLT Query is submitted as normal query to shards.
name|s
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
name|q
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
DECL|method|buildMLTQuery
name|ShardRequest
name|buildMLTQuery
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|String
name|q
parameter_list|)
block|{
name|ShardRequest
name|s
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
name|s
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|s
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|String
name|id
init|=
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
decl_stmt|;
name|s
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"score,"
operator|+
name|id
argument_list|)
expr_stmt|;
comment|// MLT Query is submitted as normal query to shards.
name|s
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
name|q
argument_list|)
expr_stmt|;
name|s
operator|.
name|shards
operator|=
name|ShardRequest
operator|.
name|ALL_SHARDS
expr_stmt|;
return|return
name|s
return|;
block|}
DECL|method|getMoreLikeThese
name|NamedList
argument_list|<
name|DocList
argument_list|>
name|getMoreLikeThese
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|DocList
name|docs
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrParams
name|p
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|MoreLikeThisHandler
operator|.
name|MoreLikeThisHelper
name|mltHelper
init|=
operator|new
name|MoreLikeThisHandler
operator|.
name|MoreLikeThisHelper
argument_list|(
name|p
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|DocList
argument_list|>
name|mlt
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|DocIterator
name|iterator
init|=
name|docs
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|dbg
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|rb
operator|.
name|isDebug
argument_list|()
condition|)
block|{
name|dbg
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|id
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|int
name|rows
init|=
name|p
operator|.
name|getInt
argument_list|(
name|MoreLikeThisParams
operator|.
name|DOC_COUNT
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|DocListAndSet
name|sim
init|=
name|mltHelper
operator|.
name|getMoreLikeThis
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|rows
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|searcher
operator|.
name|doc
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
name|mlt
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|sim
operator|.
name|docList
argument_list|)
expr_stmt|;
if|if
condition|(
name|dbg
operator|!=
literal|null
condition|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|docDbg
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|docDbg
operator|.
name|add
argument_list|(
literal|"rawMLTQuery"
argument_list|,
name|mltHelper
operator|.
name|getRawMLTQuery
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|docDbg
operator|.
name|add
argument_list|(
literal|"boostedMLTQuery"
argument_list|,
name|mltHelper
operator|.
name|getBoostedMLTQuery
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|docDbg
operator|.
name|add
argument_list|(
literal|"realMLTQuery"
argument_list|,
name|mltHelper
operator|.
name|getRealMLTQuery
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|explains
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|DocIterator
name|mltIte
init|=
name|sim
operator|.
name|docList
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|mltIte
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|mltid
init|=
name|mltIte
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|searcher
operator|.
name|doc
argument_list|(
name|mltid
argument_list|)
argument_list|)
decl_stmt|;
name|explains
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|searcher
operator|.
name|explain
argument_list|(
name|mltHelper
operator|.
name|getRealMLTQuery
argument_list|()
argument_list|,
name|mltid
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|docDbg
operator|.
name|add
argument_list|(
literal|"explain"
argument_list|,
name|explains
argument_list|)
expr_stmt|;
name|dbg
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|docDbg
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add debug information
if|if
condition|(
name|dbg
operator|!=
literal|null
condition|)
block|{
name|rb
operator|.
name|addDebugInfo
argument_list|(
literal|"moreLikeThis"
argument_list|,
name|dbg
argument_list|)
expr_stmt|;
block|}
return|return
name|mlt
return|;
block|}
comment|// ///////////////////////////////////////////
comment|// / SolrInfoMBean
comment|// //////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"More Like This"
return|;
block|}
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
