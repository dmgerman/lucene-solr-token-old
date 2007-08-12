begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|lucene
operator|.
name|queryParser
operator|.
name|QueryParser
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
name|BooleanClause
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
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|DisMaxParams
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
name|FacetParams
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
name|core
operator|.
name|SolrCore
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
name|highlight
operator|.
name|SolrHighlighter
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
name|SimpleFacets
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
name|request
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
name|util
operator|.
name|SolrPluginUtils
import|;
end_import
begin_comment
comment|/**  *<p>  * A Generic query plugin designed to be given a simple query expression  * from a user, which it will then query against a variety of  * pre-configured fields, in a variety of ways, using BooleanQueries,  * DisjunctionMaxQueries, and PhraseQueries.  *</p>  *  *<p>  * All of the following options may be configured for this plugin  * in the solrconfig as defaults, and may be overriden as request parameters  *</p>  *  *<ul>  *<li>q.alt - An alternate query to be used in cases where the main  *             query (q) is not specified (or blank).  This query should  *             be expressed in the Standard SolrQueryParser syntax (you  *             can use<code>q.alt=*:*</code> to denote that all documents  *             should be returned when no query is specified)  *</li>  *<li>tie - (Tie breaker) float value to use as tiebreaker in  *           DisjunctionMaxQueries (should be something much less than 1)  *</li>  *<li> qf - (Query Fields) fields and boosts to use when building  *           DisjunctionMaxQueries from the users query.  Format is:  *           "<code>fieldA^1.0 fieldB^2.2</code>".  *           This param can be specified multiple times, and the fields  *           are additive.  *</li>  *<li> mm - (Minimum Match) this supports a wide variety of  *           complex expressions.  *           read {@link SolrPluginUtils#setMinShouldMatch SolrPluginUtils.setMinShouldMatch} and<a href="http://lucene.apache.org/solr/api/org/apache/solr/util/doc-files/min-should-match.html">mm expression format</a> for details.  *</li>  *<li> pf - (Phrase Fields) fields/boosts to make phrase queries out  *           of, to boost the users query for exact matches on the specified fields.  *           Format is: "<code>fieldA^1.0 fieldB^2.2</code>".  *           This param can be specified multiple times, and the fields  *           are additive.  *</li>  *<li> ps - (Phrase Slop) amount of slop on phrase queries built for pf  *           fields.  *</li>  *<li> qs - (Query Slop) amount of slop on phrase queries explicitly  *           specified in the "q" for qf fields.  *</li>  *<li> bq - (Boost Query) a raw lucene query that will be included in the   *           users query to influence the score.  If this is a BooleanQuery  *           with a default boost (1.0f), then the individual clauses will be  *           added directly to the main query.  Otherwise, the query will be  *           included as is.  *           This param can be specified multiple times, and the boosts are   *           are additive.  NOTE: the behaviour listed above is only in effect  *           if a single<code>bq</code> paramter is specified.  Hence you can  *           disable it by specifying an additional, blank,<code>bq</code>   *           parameter.  *</li>  *<li> bf - (Boost Functions) functions (with optional boosts) that will be  *           included in the users query to influence the score.  *           Format is: "<code>funcA(arg1,arg2)^1.2  *           funcB(arg3,arg4)^2.2</code>".  NOTE: Whitespace is not allowed  *           in the function arguments.  *           This param can be specified multiple times, and the functions  *           are additive.  *</li>  *<li> fq - (Filter Query) a raw lucene query that can be used  *           to restrict the super set of products we are interested in - more  *           efficient then using bq, but doesn't influence score.  *           This param can be specified multiple times, and the filters  *           are additive.  *</li>  *</ul>  *  *<p>  * The following options are only available as request params...  *</p>  *  *<ul>  *<li>   q - (Query) the raw unparsed, unescaped, query from the user.  *</li>  *<li>sort - (Order By) list of fields and direction to sort on.  *</li>  *</ul>  *  *<pre>  * :TODO: document facet param support  *  *</pre>  */
end_comment
begin_class
DECL|class|DisMaxRequestHandler
specifier|public
class|class
name|DisMaxRequestHandler
extends|extends
name|RequestHandlerBase
block|{
comment|/**    * A field we can't ever find in any schema, so we can safely tell    * DisjunctionMaxQueryParser to use it as our defaultField, and    * map aliases from it to any field in our schema.    */
DECL|field|IMPOSSIBLE_FIELD_NAME
specifier|private
specifier|static
name|String
name|IMPOSSIBLE_FIELD_NAME
init|=
literal|"\uFFFC\uFFFC\uFFFC"
decl_stmt|;
comment|/** shorten the class references for utilities */
DECL|class|U
specifier|private
specifier|static
class|class
name|U
extends|extends
name|SolrPluginUtils
block|{
comment|/* :NOOP */
block|}
comment|/** shorten the class references for utilities */
DECL|interface|DMP
specifier|private
specifier|static
interface|interface
name|DMP
extends|extends
name|DisMaxParams
block|{
comment|/* :NOOP */
block|}
DECL|method|DisMaxRequestHandler
specifier|public
name|DisMaxRequestHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** Sets the default variables for any useful info it finds in the config.    * If a config option is not in the format expected, logs a warning    * and ignores it.    */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
comment|// Handle an old format
if|if
condition|(
operator|-
literal|1
operator|==
name|args
operator|.
name|indexOf
argument_list|(
literal|"defaults"
argument_list|,
literal|0
argument_list|)
condition|)
block|{
comment|// no explict defaults list, use all args implicitly
comment|// indexOf so "<null name="defaults"/> is valid indicator of no defaults
name|defaults
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// otherwise use the new one.
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
name|SolrIndexSearcher
name|s
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|IndexSchema
name|schema
init|=
name|req
operator|.
name|getSchema
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|queryFields
init|=
name|U
operator|.
name|parseFieldBoosts
argument_list|(
name|params
operator|.
name|getParams
argument_list|(
name|DMP
operator|.
name|QF
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|phraseFields
init|=
name|U
operator|.
name|parseFieldBoosts
argument_list|(
name|params
operator|.
name|getParams
argument_list|(
name|DMP
operator|.
name|PF
argument_list|)
argument_list|)
decl_stmt|;
name|float
name|tiebreaker
init|=
name|params
operator|.
name|getFloat
argument_list|(
name|DMP
operator|.
name|TIE
argument_list|,
literal|0.0f
argument_list|)
decl_stmt|;
name|int
name|pslop
init|=
name|params
operator|.
name|getInt
argument_list|(
name|DMP
operator|.
name|PS
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|qslop
init|=
name|params
operator|.
name|getInt
argument_list|(
name|DMP
operator|.
name|QS
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/* a generic parser for parsing regular lucene queries */
name|QueryParser
name|p
init|=
name|schema
operator|.
name|getSolrQueryParser
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|/* a parser for dealing with user input, which will convert        * things to DisjunctionMaxQueries        */
name|U
operator|.
name|DisjunctionMaxQueryParser
name|up
init|=
operator|new
name|U
operator|.
name|DisjunctionMaxQueryParser
argument_list|(
name|schema
argument_list|,
name|IMPOSSIBLE_FIELD_NAME
argument_list|)
decl_stmt|;
name|up
operator|.
name|addAlias
argument_list|(
name|IMPOSSIBLE_FIELD_NAME
argument_list|,
name|tiebreaker
argument_list|,
name|queryFields
argument_list|)
expr_stmt|;
name|up
operator|.
name|setPhraseSlop
argument_list|(
name|qslop
argument_list|)
expr_stmt|;
comment|/* for parsing sloppy phrases using DisjunctionMaxQueries */
name|U
operator|.
name|DisjunctionMaxQueryParser
name|pp
init|=
operator|new
name|U
operator|.
name|DisjunctionMaxQueryParser
argument_list|(
name|schema
argument_list|,
name|IMPOSSIBLE_FIELD_NAME
argument_list|)
decl_stmt|;
name|pp
operator|.
name|addAlias
argument_list|(
name|IMPOSSIBLE_FIELD_NAME
argument_list|,
name|tiebreaker
argument_list|,
name|phraseFields
argument_list|)
expr_stmt|;
name|pp
operator|.
name|setPhraseSlop
argument_list|(
name|pslop
argument_list|)
expr_stmt|;
comment|/* the main query we will execute.  we disable the coord because        * this query is an artificial construct        */
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|/* * * Main User Query * * */
name|Query
name|parsedUserQuery
init|=
literal|null
decl_stmt|;
name|String
name|userQuery
init|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|)
decl_stmt|;
name|Query
name|altUserQuery
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|userQuery
operator|==
literal|null
operator|||
name|userQuery
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|<
literal|1
condition|)
block|{
comment|// If no query is specified, we may have an alternate
name|String
name|altQ
init|=
name|params
operator|.
name|get
argument_list|(
name|DMP
operator|.
name|ALTQ
argument_list|)
decl_stmt|;
if|if
condition|(
name|altQ
operator|!=
literal|null
condition|)
block|{
name|altUserQuery
operator|=
name|p
operator|.
name|parse
argument_list|(
name|altQ
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|altUserQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"missing query string"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// There is a valid query string
name|userQuery
operator|=
name|U
operator|.
name|partialEscape
argument_list|(
name|U
operator|.
name|stripUnbalancedQuotes
argument_list|(
name|userQuery
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|String
name|minShouldMatch
init|=
name|params
operator|.
name|get
argument_list|(
name|DMP
operator|.
name|MM
argument_list|,
literal|"100%"
argument_list|)
decl_stmt|;
name|Query
name|dis
init|=
name|up
operator|.
name|parse
argument_list|(
name|userQuery
argument_list|)
decl_stmt|;
name|parsedUserQuery
operator|=
name|dis
expr_stmt|;
if|if
condition|(
name|dis
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|t
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|U
operator|.
name|flattenBooleanQuery
argument_list|(
name|t
argument_list|,
operator|(
name|BooleanQuery
operator|)
name|dis
argument_list|)
expr_stmt|;
name|U
operator|.
name|setMinShouldMatch
argument_list|(
name|t
argument_list|,
name|minShouldMatch
argument_list|)
expr_stmt|;
name|parsedUserQuery
operator|=
name|t
expr_stmt|;
block|}
name|query
operator|.
name|add
argument_list|(
name|parsedUserQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
comment|/* * * Add on Phrases for the Query * * */
comment|/* build up phrase boosting queries */
comment|/* if the userQuery already has some quotes, stip them out.          * we've already done the phrases they asked for in the main          * part of the query, this is to boost docs that may not have          * matched those phrases but do match looser phrases.          */
name|String
name|userPhraseQuery
init|=
name|userQuery
operator|.
name|replace
argument_list|(
literal|"\""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|Query
name|phrase
init|=
name|pp
operator|.
name|parse
argument_list|(
literal|"\""
operator|+
name|userPhraseQuery
operator|+
literal|"\""
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|phrase
condition|)
block|{
name|query
operator|.
name|add
argument_list|(
name|phrase
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* * * Boosting Query * * */
name|String
index|[]
name|boostParams
init|=
name|params
operator|.
name|getParams
argument_list|(
name|DMP
operator|.
name|BQ
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Query
argument_list|>
name|boostQueries
init|=
name|U
operator|.
name|parseQueryStrings
argument_list|(
name|req
argument_list|,
name|boostParams
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|boostQueries
condition|)
block|{
if|if
condition|(
literal|1
operator|==
name|boostQueries
operator|.
name|size
argument_list|()
operator|&&
literal|1
operator|==
name|boostParams
operator|.
name|length
condition|)
block|{
comment|/* legacy logic */
name|Query
name|f
init|=
name|boostQueries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
literal|1.0f
operator|==
name|f
operator|.
name|getBoost
argument_list|()
operator|&&
name|f
operator|instanceof
name|BooleanQuery
condition|)
block|{
comment|/* if the default boost was used, and we've got a BooleanQuery              * extract the subqueries out and use them directly              */
for|for
control|(
name|Object
name|c
range|:
operator|(
operator|(
name|BooleanQuery
operator|)
name|f
operator|)
operator|.
name|clauses
argument_list|()
control|)
block|{
name|query
operator|.
name|add
argument_list|(
operator|(
name|BooleanClause
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|query
operator|.
name|add
argument_list|(
name|f
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|Query
name|f
range|:
name|boostQueries
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|f
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/* * * Boosting Functions * * */
name|String
index|[]
name|boostFuncs
init|=
name|params
operator|.
name|getParams
argument_list|(
name|DMP
operator|.
name|BF
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|boostFuncs
operator|&&
literal|0
operator|!=
name|boostFuncs
operator|.
name|length
condition|)
block|{
for|for
control|(
name|String
name|boostFunc
range|:
name|boostFuncs
control|)
block|{
if|if
condition|(
literal|null
operator|==
name|boostFunc
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|boostFunc
argument_list|)
condition|)
continue|continue;
name|List
argument_list|<
name|Query
argument_list|>
name|funcs
init|=
name|U
operator|.
name|parseFuncs
argument_list|(
name|schema
argument_list|,
name|boostFunc
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|f
range|:
name|funcs
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|f
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/* * * Restrict Results * * */
name|List
argument_list|<
name|Query
argument_list|>
name|restrictions
init|=
name|U
operator|.
name|parseFilterQueries
argument_list|(
name|req
argument_list|)
decl_stmt|;
comment|/* * * Generate Main Results * * */
name|flags
operator||=
name|U
operator|.
name|setReturnFields
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|DocListAndSet
name|results
init|=
operator|new
name|DocListAndSet
argument_list|()
decl_stmt|;
name|NamedList
name|facetInfo
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|FacetParams
operator|.
name|FACET
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|results
operator|=
name|s
operator|.
name|getDocListAndSet
argument_list|(
name|query
argument_list|,
name|restrictions
argument_list|,
name|SolrPluginUtils
operator|.
name|getSort
argument_list|(
name|req
argument_list|)
argument_list|,
name|req
operator|.
name|getStart
argument_list|()
argument_list|,
name|req
operator|.
name|getLimit
argument_list|()
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|facetInfo
operator|=
name|getFacetInfo
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|results
operator|.
name|docSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|results
operator|.
name|docList
operator|=
name|s
operator|.
name|getDocList
argument_list|(
name|query
argument_list|,
name|restrictions
argument_list|,
name|SolrPluginUtils
operator|.
name|getSort
argument_list|(
name|req
argument_list|)
argument_list|,
name|req
operator|.
name|getStart
argument_list|()
argument_list|,
name|req
operator|.
name|getLimit
argument_list|()
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"response"
argument_list|,
name|results
operator|.
name|docList
argument_list|)
expr_stmt|;
comment|// pre-fetch returned documents
name|U
operator|.
name|optimizePreFetchDocs
argument_list|(
name|results
operator|.
name|docList
argument_list|,
name|query
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|facetInfo
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"facet_counts"
argument_list|,
name|facetInfo
argument_list|)
expr_stmt|;
comment|/* * * Debugging Info * * */
try|try
block|{
name|NamedList
name|debug
init|=
name|U
operator|.
name|doStandardDebug
argument_list|(
name|req
argument_list|,
name|userQuery
argument_list|,
name|query
argument_list|,
name|results
operator|.
name|docList
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|debug
condition|)
block|{
name|debug
operator|.
name|add
argument_list|(
literal|"altquerystring"
argument_list|,
name|altUserQuery
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|boostQueries
condition|)
block|{
name|debug
operator|.
name|add
argument_list|(
literal|"boost_queries"
argument_list|,
name|boostParams
argument_list|)
expr_stmt|;
name|debug
operator|.
name|add
argument_list|(
literal|"parsed_boost_queries"
argument_list|,
name|QueryParsing
operator|.
name|toString
argument_list|(
name|boostQueries
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|debug
operator|.
name|add
argument_list|(
literal|"boostfuncs"
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|DMP
operator|.
name|BF
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|restrictions
condition|)
block|{
name|debug
operator|.
name|add
argument_list|(
literal|"filter_queries"
argument_list|,
name|params
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|FQ
argument_list|)
argument_list|)
expr_stmt|;
name|debug
operator|.
name|add
argument_list|(
literal|"parsed_filter_queries"
argument_list|,
name|QueryParsing
operator|.
name|toString
argument_list|(
name|restrictions
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"debug"
argument_list|,
name|debug
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|logOnce
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
literal|"Exception during debug"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"exception_during_debug"
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* * * Highlighting/Summarizing  * * */
name|SolrHighlighter
name|highlighter
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getHighlighter
argument_list|()
decl_stmt|;
if|if
condition|(
name|highlighter
operator|.
name|isHighlightingEnabled
argument_list|(
name|params
argument_list|)
operator|&&
name|parsedUserQuery
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|highFields
init|=
name|queryFields
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|NamedList
name|sumData
init|=
name|highlighter
operator|.
name|doHighlighting
argument_list|(
name|results
operator|.
name|docList
argument_list|,
name|parsedUserQuery
operator|.
name|rewrite
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getReader
argument_list|()
argument_list|)
argument_list|,
name|req
argument_list|,
name|highFields
argument_list|)
decl_stmt|;
if|if
condition|(
name|sumData
operator|!=
literal|null
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"highlighting"
argument_list|,
name|sumData
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Fetches information about Facets for this request.    *    * Subclasses may with to override this method to provide more     * advanced faceting behavior.    * @see SimpleFacets#getFacetCounts    */
DECL|method|getFacetInfo
specifier|protected
name|NamedList
name|getFacetInfo
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|DocSet
name|mainSet
parameter_list|)
block|{
name|SimpleFacets
name|f
init|=
operator|new
name|SimpleFacets
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|mainSet
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|f
operator|.
name|getFacetCounts
argument_list|()
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"DisjunctionMax Request Handler: Does relevancy based queries "
operator|+
literal|"across a variety of fields using configured boosts"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
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
try|try
block|{
return|return
operator|new
name|URL
index|[]
block|{
operator|new
name|URL
argument_list|(
literal|"http://wiki.apache.org/solr/DisMaxRequestHandler"
argument_list|)
block|}
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
