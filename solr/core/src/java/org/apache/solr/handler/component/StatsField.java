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
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|index
operator|.
name|LeafReaderContext
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
name|*
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
name|queries
operator|.
name|function
operator|.
name|FunctionQuery
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|QueryValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|FieldCacheSource
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
name|SolrException
operator|.
name|ErrorCode
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
name|StatsParams
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
name|StrUtils
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
begin_comment
comment|// jdocs
end_comment
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
name|DocValuesStats
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
name|schema
operator|.
name|SchemaField
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
name|DocSet
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
name|QParserPlugin
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
name|QueryParsing
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
name|SyntaxError
import|;
end_import
begin_comment
comment|/**  * Models all of the information associated with a single {@link StatsParams#STATS_FIELD}  * instance.  *  * @see StatsComponent  */
end_comment
begin_class
DECL|class|StatsField
specifier|public
class|class
name|StatsField
block|{
DECL|field|searcher
specifier|private
specifier|final
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|rb
specifier|private
specifier|final
name|ResponseBuilder
name|rb
decl_stmt|;
DECL|field|originalParam
specifier|private
specifier|final
name|String
name|originalParam
decl_stmt|;
comment|// for error messages
DECL|field|localParams
specifier|private
specifier|final
name|SolrParams
name|localParams
decl_stmt|;
DECL|field|valueSource
specifier|private
specifier|final
name|ValueSource
name|valueSource
decl_stmt|;
comment|// may be null if simple field stats
DECL|field|schemaField
specifier|private
specifier|final
name|SchemaField
name|schemaField
decl_stmt|;
comment|// may be null if function/query stats
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|calcDistinct
specifier|private
specifier|final
name|boolean
name|calcDistinct
decl_stmt|;
comment|// TODO: put this inside localParams ? SOLR-6349 ?
DECL|field|facets
specifier|private
specifier|final
name|String
index|[]
name|facets
decl_stmt|;
DECL|field|excludeTagList
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|excludeTagList
decl_stmt|;
comment|/**    * @param rb the current request/response    * @param statsParam the raw {@link StatsParams#STATS_FIELD} string    */
DECL|method|StatsField
specifier|public
name|StatsField
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|String
name|statsParam
parameter_list|)
block|{
name|this
operator|.
name|rb
operator|=
name|rb
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
name|this
operator|.
name|originalParam
operator|=
name|statsParam
expr_stmt|;
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
try|try
block|{
name|SolrParams
name|localParams
init|=
name|QueryParsing
operator|.
name|getLocalParams
argument_list|(
name|originalParam
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|localParams
condition|)
block|{
comment|// simplest possible input: bare string (field name)
name|ModifiableSolrParams
name|customParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|customParams
operator|.
name|add
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|,
name|originalParam
argument_list|)
expr_stmt|;
name|localParams
operator|=
name|customParams
expr_stmt|;
block|}
name|this
operator|.
name|localParams
operator|=
name|localParams
expr_stmt|;
name|String
name|parserName
init|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|TYPE
argument_list|)
decl_stmt|;
name|SchemaField
name|sf
init|=
literal|null
decl_stmt|;
name|ValueSource
name|vs
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|parserName
argument_list|)
condition|)
block|{
comment|// basic request for field stats
name|sf
operator|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we have a non trivial request to compute stats over a query (or function)
comment|// NOTE we could use QParser.getParser(...) here, but that would redundently
comment|// reparse everything.  ( TODO: refactor a common method in QParser ?)
name|QParserPlugin
name|qplug
init|=
name|rb
operator|.
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getQueryPlugin
argument_list|(
name|parserName
argument_list|)
decl_stmt|;
name|QParser
name|qp
init|=
name|qplug
operator|.
name|createParser
argument_list|(
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|)
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|rb
operator|.
name|req
argument_list|)
decl_stmt|;
comment|// figure out what type of query we are dealing, get the most direct ValueSource
name|vs
operator|=
name|extractValueSource
argument_list|(
name|qp
operator|.
name|parse
argument_list|()
argument_list|)
expr_stmt|;
comment|// if this ValueSource directly corrisponds to a SchemaField, act as if
comment|// we were asked to compute stats on it directly
comment|// ie:  "stats.field={!func key=foo}field(foo)" == "stats.field=foo"
name|sf
operator|=
name|extractSchemaField
argument_list|(
name|vs
argument_list|,
name|searcher
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|sf
condition|)
block|{
name|vs
operator|=
literal|null
expr_stmt|;
block|}
block|}
assert|assert
operator|(
operator|(
literal|null
operator|==
name|vs
operator|)
operator|^
operator|(
literal|null
operator|==
name|sf
operator|)
operator|)
operator|:
literal|"exactly one of vs& sf must be null"
assert|;
name|this
operator|.
name|schemaField
operator|=
name|sf
expr_stmt|;
name|this
operator|.
name|valueSource
operator|=
name|vs
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyntaxError
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unable to parse "
operator|+
name|StatsParams
operator|.
name|STATS_FIELD
operator|+
literal|": "
operator|+
name|originalParam
operator|+
literal|" due to: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// allow explicit setting of the response key via localparams...
name|this
operator|.
name|key
operator|=
name|localParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|OUTPUT_KEY
argument_list|,
comment|// default to the main param value...
name|localParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|VALUE
argument_list|,
comment|// default to entire original param str.
name|originalParam
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|calcDistinct
operator|=
literal|null
operator|==
name|schemaField
condition|?
name|params
operator|.
name|getBool
argument_list|(
name|StatsParams
operator|.
name|STATS_CALC_DISTINCT
argument_list|,
literal|false
argument_list|)
else|:
name|params
operator|.
name|getFieldBool
argument_list|(
name|schemaField
operator|.
name|getName
argument_list|()
argument_list|,
name|StatsParams
operator|.
name|STATS_CALC_DISTINCT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
index|[]
name|facets
init|=
name|params
operator|.
name|getFieldParams
argument_list|(
name|key
argument_list|,
name|StatsParams
operator|.
name|STATS_FACET
argument_list|)
decl_stmt|;
name|this
operator|.
name|facets
operator|=
operator|(
literal|null
operator|==
name|facets
operator|)
condition|?
operator|new
name|String
index|[
literal|0
index|]
else|:
name|facets
expr_stmt|;
comment|// figure out if we need a special base DocSet
name|String
name|excludeStr
init|=
name|localParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|EXCLUDE
argument_list|)
decl_stmt|;
name|this
operator|.
name|excludeTagList
operator|=
operator|(
literal|null
operator|==
name|excludeStr
operator|)
condition|?
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
else|:
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|excludeStr
argument_list|,
literal|','
argument_list|)
expr_stmt|;
assert|assert
operator|(
operator|(
literal|null
operator|==
name|this
operator|.
name|valueSource
operator|)
operator|^
operator|(
literal|null
operator|==
name|this
operator|.
name|schemaField
operator|)
operator|)
operator|:
literal|"exactly one of valueSource& schemaField must be null"
assert|;
block|}
comment|/**    * Inspects a {@link Query} to see if it directly maps to a {@link ValueSource},    * and if so returns it -- otherwise wraps it as needed.    *    * @param q Query whose scores we have been asked to compute stats of    * @returns a ValueSource to use for computing the stats    */
DECL|method|extractValueSource
specifier|private
specifier|static
name|ValueSource
name|extractValueSource
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
return|return
operator|(
name|q
operator|instanceof
name|FunctionQuery
operator|)
condition|?
comment|// Common case: we're wrapping a func, so we can directly pull out ValueSource
operator|(
operator|(
name|FunctionQuery
operator|)
name|q
operator|)
operator|.
name|getValueSource
argument_list|()
else|:
comment|// asked to compute stats over a query, wrap it up as a ValueSource
operator|new
name|QueryValueSource
argument_list|(
name|q
argument_list|,
literal|0.0F
argument_list|)
return|;
block|}
comment|/**    * Inspects a {@link ValueSource} to see if it directly maps to a {@link SchemaField},     * and if so returns it.    *    * @param vs ValueSource we've been asked to compute stats of    * @param schema The Schema to use    * @returns Corrisponding {@link SchemaField} or null if the ValueSource is more complex    * @see FieldCacheSource    */
DECL|method|extractSchemaField
specifier|private
specifier|static
name|SchemaField
name|extractSchemaField
parameter_list|(
name|ValueSource
name|vs
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
if|if
condition|(
name|vs
operator|instanceof
name|FieldCacheSource
condition|)
block|{
name|String
name|fieldName
init|=
operator|(
operator|(
name|FieldCacheSource
operator|)
name|vs
operator|)
operator|.
name|getField
argument_list|()
decl_stmt|;
return|return
name|schema
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**     * The key to be used when refering to this {@link StatsField} instance in the     * response tp clients.    */
DECL|method|getOutputKey
specifier|public
name|String
name|getOutputKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
comment|/**    * Computes a base {@link DocSet} for the current request to be used    * when computing global stats for the local index.    *    * This is typically the same as the main DocSet for the {@link ResponseBuilder}    * unless {@link CommonParams#TAG tag}ged filter queries have been excluded using     * the {@link CommonParams#EXCLUDE ex} local param    */
DECL|method|computeBaseDocSet
specifier|public
name|DocSet
name|computeBaseDocSet
parameter_list|()
throws|throws
name|IOException
block|{
name|DocSet
name|docs
init|=
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docSet
decl_stmt|;
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|tagMap
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|rb
operator|.
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"tags"
argument_list|)
decl_stmt|;
if|if
condition|(
name|excludeTagList
operator|.
name|isEmpty
argument_list|()
operator|||
literal|null
operator|==
name|tagMap
condition|)
block|{
comment|// either the exclude list is empty, or there
comment|// aren't any tagged filters to exclude anyway.
return|return
name|docs
return|;
block|}
name|IdentityHashMap
argument_list|<
name|Query
argument_list|,
name|Boolean
argument_list|>
name|excludeSet
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|Query
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|excludeTag
range|:
name|excludeTagList
control|)
block|{
name|Object
name|olst
init|=
name|tagMap
operator|.
name|get
argument_list|(
name|excludeTag
argument_list|)
decl_stmt|;
comment|// tagMap has entries of List<String,List<QParser>>, but subject to change in the future
if|if
condition|(
operator|!
operator|(
name|olst
operator|instanceof
name|Collection
operator|)
condition|)
continue|continue;
for|for
control|(
name|Object
name|o
range|:
operator|(
name|Collection
argument_list|<
name|?
argument_list|>
operator|)
name|olst
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|QParser
operator|)
condition|)
continue|continue;
name|QParser
name|qp
init|=
operator|(
name|QParser
operator|)
name|o
decl_stmt|;
try|try
block|{
name|excludeSet
operator|.
name|put
argument_list|(
name|qp
operator|.
name|getQuery
argument_list|()
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyntaxError
name|e
parameter_list|)
block|{
comment|// this shouldn't be possible since the request should have already
comment|// failed when attempting to execute the query, but just in case...
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Excluded query can't be parsed: "
operator|+
name|originalParam
operator|+
literal|" due to: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|excludeSet
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
name|docs
return|;
name|List
argument_list|<
name|Query
argument_list|>
name|qlist
init|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
comment|// add the base query
if|if
condition|(
operator|!
name|excludeSet
operator|.
name|containsKey
argument_list|(
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|)
condition|)
block|{
name|qlist
operator|.
name|add
argument_list|(
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// add the filters
if|if
condition|(
name|rb
operator|.
name|getFilters
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Query
name|q
range|:
name|rb
operator|.
name|getFilters
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|excludeSet
operator|.
name|containsKey
argument_list|(
name|q
argument_list|)
condition|)
block|{
name|qlist
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// get the new base docset for this facet
return|return
name|searcher
operator|.
name|getDocSet
argument_list|(
name|qlist
argument_list|)
return|;
block|}
comment|/**    * Computes the {@link StatsValues} for this {@link StatsField} relative to the     * specified {@link DocSet}     * @see #computeBaseDocSet    */
DECL|method|computeLocalStatsValues
specifier|public
name|StatsValues
name|computeLocalStatsValues
parameter_list|(
name|DocSet
name|base
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|!=
name|schemaField
operator|&&
operator|(
name|schemaField
operator|.
name|multiValued
argument_list|()
operator|||
name|schemaField
operator|.
name|getType
argument_list|()
operator|.
name|multiValuedFieldCache
argument_list|()
operator|)
condition|)
block|{
comment|// TODO: should this also be used for single-valued string fields? (should work fine)
return|return
name|DocValuesStats
operator|.
name|getCounts
argument_list|(
name|searcher
argument_list|,
name|this
argument_list|,
name|base
argument_list|,
name|facets
argument_list|)
return|;
block|}
else|else
block|{
comment|// either a single valued field we pull from FieldCache, or an explicit
comment|// function ValueSource
return|return
name|computeLocalValueSourceStats
argument_list|(
name|base
argument_list|)
return|;
block|}
block|}
DECL|method|computeLocalValueSourceStats
specifier|private
name|StatsValues
name|computeLocalValueSourceStats
parameter_list|(
name|DocSet
name|base
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSchema
name|schema
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
decl_stmt|;
specifier|final
name|StatsValues
name|allstats
init|=
name|StatsValuesFactory
operator|.
name|createStatsValues
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FieldFacetStats
argument_list|>
name|facetStats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|facetField
range|:
name|facets
control|)
block|{
name|SchemaField
name|fsf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|facetField
argument_list|)
decl_stmt|;
if|if
condition|(
name|fsf
operator|.
name|multiValued
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Stats can only facet on single-valued fields, not: "
operator|+
name|facetField
argument_list|)
throw|;
block|}
name|facetStats
operator|.
name|add
argument_list|(
operator|new
name|FieldFacetStats
argument_list|(
name|searcher
argument_list|,
name|fsf
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Iterator
argument_list|<
name|LeafReaderContext
argument_list|>
name|ctxIt
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|LeafReaderContext
name|ctx
init|=
literal|null
decl_stmt|;
for|for
control|(
name|DocIterator
name|docsIt
init|=
name|base
operator|.
name|iterator
argument_list|()
init|;
name|docsIt
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|int
name|doc
init|=
name|docsIt
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctx
operator|==
literal|null
operator|||
name|doc
operator|>=
name|ctx
operator|.
name|docBase
operator|+
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
comment|// advance
do|do
block|{
name|ctx
operator|=
name|ctxIt
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|ctx
operator|==
literal|null
operator|||
name|doc
operator|>=
name|ctx
operator|.
name|docBase
operator|+
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|)
do|;
assert|assert
name|doc
operator|>=
name|ctx
operator|.
name|docBase
assert|;
comment|// propagate the context among accumulators.
name|allstats
operator|.
name|setNextReader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldFacetStats
name|f
range|:
name|facetStats
control|)
block|{
name|f
operator|.
name|setNextReader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
comment|// accumulate
name|allstats
operator|.
name|accumulate
argument_list|(
name|doc
operator|-
name|ctx
operator|.
name|docBase
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldFacetStats
name|f
range|:
name|facetStats
control|)
block|{
name|f
operator|.
name|facet
argument_list|(
name|doc
operator|-
name|ctx
operator|.
name|docBase
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|FieldFacetStats
name|f
range|:
name|facetStats
control|)
block|{
name|allstats
operator|.
name|addFacet
argument_list|(
name|f
operator|.
name|name
argument_list|,
name|f
operator|.
name|facetStatsValues
argument_list|)
expr_stmt|;
block|}
return|return
name|allstats
return|;
block|}
comment|/**    * The searcher that should be used for processing local stats    * @see SolrQueryRequest#getSearcher    */
DECL|method|getSearcher
specifier|public
name|SolrIndexSearcher
name|getSearcher
parameter_list|()
block|{
comment|// see AbstractStatsValues.setNextReader
return|return
name|searcher
return|;
block|}
comment|/**    * The {@link SchemaField} whose results these stats are computed over, may be null     * if the stats are computed over the results of a function or query    *    * @see #getValueSource    */
DECL|method|getSchemaField
specifier|public
name|SchemaField
name|getSchemaField
parameter_list|()
block|{
return|return
name|schemaField
return|;
block|}
comment|/**    * The {@link ValueSource} of a function or query whose results these stats are computed     * over, may be null if the stats are directly over a {@link SchemaField}    *    * @see #getValueSource    */
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|()
block|{
return|return
name|valueSource
return|;
block|}
comment|/**    * Wether or not the effective value of the {@link StatsParams#STATS_CALC_DISTINCT} param    * is true or false for this StatsField    */
DECL|method|getCalcDistinct
specifier|public
name|boolean
name|getCalcDistinct
parameter_list|()
block|{
return|return
name|calcDistinct
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"StatsField<"
operator|+
name|originalParam
operator|+
literal|">"
return|;
block|}
block|}
end_class
end_unit