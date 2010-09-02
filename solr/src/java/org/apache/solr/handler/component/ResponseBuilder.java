begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|RTimer
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
name|QParser
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
name|util
operator|.
name|SolrPluginUtils
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
name|HashSet
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
name|Set
import|;
end_import
begin_comment
comment|/**  * This class is experimental and will be changing in the future.  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|ResponseBuilder
specifier|public
class|class
name|ResponseBuilder
block|{
DECL|field|req
specifier|public
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|rsp
specifier|public
name|SolrQueryResponse
name|rsp
decl_stmt|;
DECL|field|doHighlights
specifier|public
name|boolean
name|doHighlights
decl_stmt|;
DECL|field|doFacets
specifier|public
name|boolean
name|doFacets
decl_stmt|;
DECL|field|doStats
specifier|public
name|boolean
name|doStats
decl_stmt|;
DECL|field|doTerms
specifier|public
name|boolean
name|doTerms
decl_stmt|;
DECL|field|needDocList
specifier|private
name|boolean
name|needDocList
init|=
literal|false
decl_stmt|;
DECL|field|needDocSet
specifier|private
name|boolean
name|needDocSet
init|=
literal|false
decl_stmt|;
DECL|field|fieldFlags
specifier|private
name|int
name|fieldFlags
init|=
literal|0
decl_stmt|;
comment|//private boolean debug = false;
DECL|field|debugTimings
DECL|field|debugQuery
DECL|field|debugResults
specifier|private
name|boolean
name|debugTimings
decl_stmt|,
name|debugQuery
decl_stmt|,
name|debugResults
decl_stmt|;
DECL|field|qparser
specifier|private
name|QParser
name|qparser
init|=
literal|null
decl_stmt|;
DECL|field|queryString
specifier|private
name|String
name|queryString
init|=
literal|null
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
init|=
literal|null
decl_stmt|;
DECL|field|filters
specifier|private
name|List
argument_list|<
name|Query
argument_list|>
name|filters
init|=
literal|null
decl_stmt|;
DECL|field|sortSpec
specifier|private
name|SortSpec
name|sortSpec
init|=
literal|null
decl_stmt|;
DECL|field|results
specifier|private
name|DocListAndSet
name|results
init|=
literal|null
decl_stmt|;
DECL|field|debugInfo
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|debugInfo
init|=
literal|null
decl_stmt|;
DECL|field|timer
specifier|private
name|RTimer
name|timer
init|=
literal|null
decl_stmt|;
DECL|field|highlightQuery
specifier|private
name|Query
name|highlightQuery
init|=
literal|null
decl_stmt|;
DECL|field|components
specifier|public
name|List
argument_list|<
name|SearchComponent
argument_list|>
name|components
decl_stmt|;
comment|//////////////////////////////////////////////////////////
comment|//////////////////////////////////////////////////////////
comment|//// Distributed Search section
comment|//////////////////////////////////////////////////////////
comment|//////////////////////////////////////////////////////////
DECL|field|FIELD_SORT_VALUES
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_SORT_VALUES
init|=
literal|"fsv"
decl_stmt|;
DECL|field|SHARDS
specifier|public
specifier|static
specifier|final
name|String
name|SHARDS
init|=
literal|"shards"
decl_stmt|;
DECL|field|IDS
specifier|public
specifier|static
specifier|final
name|String
name|IDS
init|=
literal|"ids"
decl_stmt|;
comment|/**    * public static final String NUMDOCS = "nd";    * public static final String DOCFREQS = "tdf";    * public static final String TERMS = "terms";    * public static final String EXTRACT_QUERY_TERMS = "eqt";    * public static final String LOCAL_SHARD = "local";    * public static final String DOC_QUERY = "dq";    * *    */
DECL|field|STAGE_START
specifier|public
specifier|static
name|int
name|STAGE_START
init|=
literal|0
decl_stmt|;
DECL|field|STAGE_PARSE_QUERY
specifier|public
specifier|static
name|int
name|STAGE_PARSE_QUERY
init|=
literal|1000
decl_stmt|;
DECL|field|STAGE_EXECUTE_QUERY
specifier|public
specifier|static
name|int
name|STAGE_EXECUTE_QUERY
init|=
literal|2000
decl_stmt|;
DECL|field|STAGE_GET_FIELDS
specifier|public
specifier|static
name|int
name|STAGE_GET_FIELDS
init|=
literal|3000
decl_stmt|;
DECL|field|STAGE_DONE
specifier|public
specifier|static
name|int
name|STAGE_DONE
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|stage
specifier|public
name|int
name|stage
decl_stmt|;
comment|// What stage is this current request at?
comment|//The address of the Shard
DECL|field|shards
specifier|public
name|String
index|[]
name|shards
decl_stmt|;
DECL|field|shards_rows
specifier|public
name|int
name|shards_rows
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|shards_start
specifier|public
name|int
name|shards_start
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|outgoing
specifier|public
name|List
argument_list|<
name|ShardRequest
argument_list|>
name|outgoing
decl_stmt|;
comment|// requests to be sent
DECL|field|finished
specifier|public
name|List
argument_list|<
name|ShardRequest
argument_list|>
name|finished
decl_stmt|;
comment|// requests that have received responses from all shards
DECL|method|getShardNum
specifier|public
name|int
name|getShardNum
parameter_list|(
name|String
name|shard
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
name|shards
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|shards
index|[
name|i
index|]
operator|==
name|shard
operator|||
name|shards
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|shard
argument_list|)
condition|)
return|return
name|i
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|addRequest
specifier|public
name|void
name|addRequest
parameter_list|(
name|SearchComponent
name|me
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{
name|outgoing
operator|.
name|add
argument_list|(
name|sreq
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_PRIVATE
operator|)
operator|==
literal|0
condition|)
block|{
comment|// if this isn't a private request, let other components modify it.
for|for
control|(
name|SearchComponent
name|component
range|:
name|components
control|)
block|{
if|if
condition|(
name|component
operator|!=
name|me
condition|)
block|{
name|component
operator|.
name|modifyRequest
argument_list|(
name|this
argument_list|,
name|me
argument_list|,
name|sreq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|field|globalCollectionStat
specifier|public
name|GlobalCollectionStat
name|globalCollectionStat
decl_stmt|;
DECL|field|resultIds
name|Map
argument_list|<
name|Object
argument_list|,
name|ShardDoc
argument_list|>
name|resultIds
decl_stmt|;
comment|// Maps uniqueKeyValue to ShardDoc, which may be used to
comment|// determine order of the doc or uniqueKey in the final
comment|// returned sequence.
comment|// Only valid after STAGE_EXECUTE_QUERY has completed.
DECL|field|_facetInfo
specifier|public
name|FacetComponent
operator|.
name|FacetInfo
name|_facetInfo
decl_stmt|;
comment|/* private... components that don't own these shouldn't use them */
DECL|field|_responseDocs
name|SolrDocumentList
name|_responseDocs
decl_stmt|;
DECL|field|_statsInfo
name|StatsInfo
name|_statsInfo
decl_stmt|;
DECL|field|_termsHelper
name|TermsComponent
operator|.
name|TermsHelper
name|_termsHelper
decl_stmt|;
comment|/**    * Utility function to add debugging info.  This will make sure a valid    * debugInfo exists before adding to it.    */
DECL|method|addDebugInfo
specifier|public
name|void
name|addDebugInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
if|if
condition|(
name|debugInfo
operator|==
literal|null
condition|)
block|{
name|debugInfo
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|debugInfo
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
comment|//-------------------------------------------------------------------------
comment|//-------------------------------------------------------------------------
DECL|method|isDebug
specifier|public
name|boolean
name|isDebug
parameter_list|()
block|{
return|return
name|debugQuery
operator|||
name|debugTimings
operator|||
name|debugResults
return|;
block|}
comment|/**    *    * @return true if all debugging options are on    */
DECL|method|isDebugAll
specifier|public
name|boolean
name|isDebugAll
parameter_list|()
block|{
return|return
name|debugQuery
operator|&&
name|debugTimings
operator|&&
name|debugResults
return|;
block|}
DECL|method|setDebug
specifier|public
name|void
name|setDebug
parameter_list|(
name|boolean
name|dbg
parameter_list|)
block|{
name|debugQuery
operator|=
name|dbg
expr_stmt|;
name|debugTimings
operator|=
name|dbg
expr_stmt|;
name|debugResults
operator|=
name|dbg
expr_stmt|;
block|}
DECL|method|isDebugTimings
specifier|public
name|boolean
name|isDebugTimings
parameter_list|()
block|{
return|return
name|debugTimings
return|;
block|}
DECL|method|setDebugTimings
specifier|public
name|void
name|setDebugTimings
parameter_list|(
name|boolean
name|debugTimings
parameter_list|)
block|{
name|this
operator|.
name|debugTimings
operator|=
name|debugTimings
expr_stmt|;
block|}
DECL|method|isDebugQuery
specifier|public
name|boolean
name|isDebugQuery
parameter_list|()
block|{
return|return
name|debugQuery
return|;
block|}
DECL|method|setDebugQuery
specifier|public
name|void
name|setDebugQuery
parameter_list|(
name|boolean
name|debugQuery
parameter_list|)
block|{
name|this
operator|.
name|debugQuery
operator|=
name|debugQuery
expr_stmt|;
block|}
DECL|method|isDebugResults
specifier|public
name|boolean
name|isDebugResults
parameter_list|()
block|{
return|return
name|debugResults
return|;
block|}
DECL|method|setDebugResults
specifier|public
name|void
name|setDebugResults
parameter_list|(
name|boolean
name|debugResults
parameter_list|)
block|{
name|this
operator|.
name|debugResults
operator|=
name|debugResults
expr_stmt|;
block|}
DECL|method|getDebugInfo
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getDebugInfo
parameter_list|()
block|{
return|return
name|debugInfo
return|;
block|}
DECL|method|setDebugInfo
specifier|public
name|void
name|setDebugInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|debugInfo
parameter_list|)
block|{
name|this
operator|.
name|debugInfo
operator|=
name|debugInfo
expr_stmt|;
block|}
DECL|method|getFieldFlags
specifier|public
name|int
name|getFieldFlags
parameter_list|()
block|{
return|return
name|fieldFlags
return|;
block|}
DECL|method|setFieldFlags
specifier|public
name|void
name|setFieldFlags
parameter_list|(
name|int
name|fieldFlags
parameter_list|)
block|{
name|this
operator|.
name|fieldFlags
operator|=
name|fieldFlags
expr_stmt|;
block|}
DECL|method|getFilters
specifier|public
name|List
argument_list|<
name|Query
argument_list|>
name|getFilters
parameter_list|()
block|{
return|return
name|filters
return|;
block|}
DECL|method|setFilters
specifier|public
name|void
name|setFilters
parameter_list|(
name|List
argument_list|<
name|Query
argument_list|>
name|filters
parameter_list|)
block|{
name|this
operator|.
name|filters
operator|=
name|filters
expr_stmt|;
block|}
DECL|method|getHighlightQuery
specifier|public
name|Query
name|getHighlightQuery
parameter_list|()
block|{
return|return
name|highlightQuery
return|;
block|}
DECL|method|setHighlightQuery
specifier|public
name|void
name|setHighlightQuery
parameter_list|(
name|Query
name|highlightQuery
parameter_list|)
block|{
name|this
operator|.
name|highlightQuery
operator|=
name|highlightQuery
expr_stmt|;
block|}
DECL|method|isNeedDocList
specifier|public
name|boolean
name|isNeedDocList
parameter_list|()
block|{
return|return
name|needDocList
return|;
block|}
DECL|method|setNeedDocList
specifier|public
name|void
name|setNeedDocList
parameter_list|(
name|boolean
name|needDocList
parameter_list|)
block|{
name|this
operator|.
name|needDocList
operator|=
name|needDocList
expr_stmt|;
block|}
DECL|method|isNeedDocSet
specifier|public
name|boolean
name|isNeedDocSet
parameter_list|()
block|{
return|return
name|needDocSet
return|;
block|}
DECL|method|setNeedDocSet
specifier|public
name|void
name|setNeedDocSet
parameter_list|(
name|boolean
name|needDocSet
parameter_list|)
block|{
name|this
operator|.
name|needDocSet
operator|=
name|needDocSet
expr_stmt|;
block|}
DECL|method|getQparser
specifier|public
name|QParser
name|getQparser
parameter_list|()
block|{
return|return
name|qparser
return|;
block|}
DECL|method|setQparser
specifier|public
name|void
name|setQparser
parameter_list|(
name|QParser
name|qparser
parameter_list|)
block|{
name|this
operator|.
name|qparser
operator|=
name|qparser
expr_stmt|;
block|}
DECL|method|getQueryString
specifier|public
name|String
name|getQueryString
parameter_list|()
block|{
return|return
name|queryString
return|;
block|}
DECL|method|setQueryString
specifier|public
name|void
name|setQueryString
parameter_list|(
name|String
name|qstr
parameter_list|)
block|{
name|this
operator|.
name|queryString
operator|=
name|qstr
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
DECL|method|setQuery
specifier|public
name|void
name|setQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
DECL|method|getResults
specifier|public
name|DocListAndSet
name|getResults
parameter_list|()
block|{
return|return
name|results
return|;
block|}
DECL|method|setResults
specifier|public
name|void
name|setResults
parameter_list|(
name|DocListAndSet
name|results
parameter_list|)
block|{
name|this
operator|.
name|results
operator|=
name|results
expr_stmt|;
block|}
DECL|method|getSortSpec
specifier|public
name|SortSpec
name|getSortSpec
parameter_list|()
block|{
return|return
name|sortSpec
return|;
block|}
DECL|method|setSortSpec
specifier|public
name|void
name|setSortSpec
parameter_list|(
name|SortSpec
name|sort
parameter_list|)
block|{
name|this
operator|.
name|sortSpec
operator|=
name|sort
expr_stmt|;
block|}
DECL|method|getTimer
specifier|public
name|RTimer
name|getTimer
parameter_list|()
block|{
return|return
name|timer
return|;
block|}
DECL|method|setTimer
specifier|public
name|void
name|setTimer
parameter_list|(
name|RTimer
name|timer
parameter_list|)
block|{
name|this
operator|.
name|timer
operator|=
name|timer
expr_stmt|;
block|}
DECL|class|GlobalCollectionStat
specifier|public
specifier|static
class|class
name|GlobalCollectionStat
block|{
DECL|field|numDocs
specifier|public
specifier|final
name|long
name|numDocs
decl_stmt|;
DECL|field|dfMap
specifier|public
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|dfMap
decl_stmt|;
DECL|method|GlobalCollectionStat
specifier|public
name|GlobalCollectionStat
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|dfMap
parameter_list|)
block|{
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
name|this
operator|.
name|dfMap
operator|=
name|dfMap
expr_stmt|;
block|}
block|}
comment|/**    * Creates a SolrIndexSearcher.QueryCommand from this    * ResponseBuilder.  TimeAllowed is left unset.    */
DECL|method|getQueryCommand
specifier|public
name|SolrIndexSearcher
operator|.
name|QueryCommand
name|getQueryCommand
parameter_list|()
block|{
name|SolrIndexSearcher
operator|.
name|QueryCommand
name|cmd
init|=
operator|new
name|SolrIndexSearcher
operator|.
name|QueryCommand
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|setQuery
argument_list|(
name|getQuery
argument_list|()
argument_list|)
operator|.
name|setFilterList
argument_list|(
name|getFilters
argument_list|()
argument_list|)
operator|.
name|setSort
argument_list|(
name|getSortSpec
argument_list|()
operator|.
name|getSort
argument_list|()
argument_list|)
operator|.
name|setOffset
argument_list|(
name|getSortSpec
argument_list|()
operator|.
name|getOffset
argument_list|()
argument_list|)
operator|.
name|setLen
argument_list|(
name|getSortSpec
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
operator|.
name|setFlags
argument_list|(
name|getFieldFlags
argument_list|()
argument_list|)
operator|.
name|setNeedDocSet
argument_list|(
name|isNeedDocSet
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|cmd
return|;
block|}
comment|/**    * Sets results from a SolrIndexSearcher.QueryResult.    */
DECL|method|setResult
specifier|public
name|void
name|setResult
parameter_list|(
name|SolrIndexSearcher
operator|.
name|QueryResult
name|result
parameter_list|)
block|{
name|setResults
argument_list|(
name|result
operator|.
name|getDocListAndSet
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|isPartialResults
argument_list|()
condition|)
block|{
name|rsp
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|add
argument_list|(
literal|"partialResults"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
