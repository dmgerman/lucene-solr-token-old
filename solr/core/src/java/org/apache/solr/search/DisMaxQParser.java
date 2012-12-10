begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|parser
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
name|ArrayList
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
begin_comment
comment|/**  * Query parser for dismax queries  *<p>  *<b>Note: This API is experimental and may change in non backward-compatible ways in the future</b>  *  *  */
end_comment
begin_class
DECL|class|DisMaxQParser
specifier|public
class|class
name|DisMaxQParser
extends|extends
name|QParser
block|{
comment|/**    * A field we can't ever find in any schema, so we can safely tell DisjunctionMaxQueryParser to use it as our    * defaultField, and map aliases from it to any field in our schema.    */
DECL|field|IMPOSSIBLE_FIELD_NAME
specifier|private
specifier|static
name|String
name|IMPOSSIBLE_FIELD_NAME
init|=
literal|"\uFFFC\uFFFC\uFFFC"
decl_stmt|;
comment|/**    * Applies the appropriate default rules for the "mm" param based on the     * effective value of the "q.op" param    *    * @see QueryParsing#getQueryParserDefaultOperator    * @see QueryParsing#OP    * @see DisMaxParams#MM    */
DECL|method|parseMinShouldMatch
specifier|public
specifier|static
name|String
name|parseMinShouldMatch
parameter_list|(
specifier|final
name|IndexSchema
name|schema
parameter_list|,
specifier|final
name|SolrParams
name|params
parameter_list|)
block|{
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|parser
operator|.
name|QueryParser
operator|.
name|Operator
name|op
init|=
name|QueryParsing
operator|.
name|getQueryParserDefaultOperator
argument_list|(
name|schema
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|OP
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|params
operator|.
name|get
argument_list|(
name|DisMaxParams
operator|.
name|MM
argument_list|,
name|op
operator|.
name|equals
argument_list|(
name|QueryParser
operator|.
name|Operator
operator|.
name|AND
argument_list|)
condition|?
literal|"100%"
else|:
literal|"0%"
argument_list|)
return|;
block|}
comment|/**    * Uses {@link SolrPluginUtils#parseFieldBoosts(String)} with the 'qf' parameter. Falls back to the 'df' parameter    * or {@link org.apache.solr.schema.IndexSchema#getDefaultSearchFieldName()}.    */
DECL|method|parseQueryFields
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|parseQueryFields
parameter_list|(
specifier|final
name|IndexSchema
name|indexSchema
parameter_list|,
specifier|final
name|SolrParams
name|solrParams
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|queryFields
init|=
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
name|solrParams
operator|.
name|getParams
argument_list|(
name|DisMaxParams
operator|.
name|QF
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryFields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|df
init|=
name|QueryParsing
operator|.
name|getDefaultField
argument_list|(
name|indexSchema
argument_list|,
name|solrParams
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|DF
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|df
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SyntaxError
argument_list|(
literal|"Neither "
operator|+
name|DisMaxParams
operator|.
name|QF
operator|+
literal|", "
operator|+
name|CommonParams
operator|.
name|DF
operator|+
literal|", nor the default search field are present."
argument_list|)
throw|;
block|}
name|queryFields
operator|.
name|put
argument_list|(
name|df
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
return|return
name|queryFields
return|;
block|}
DECL|method|DisMaxQParser
specifier|public
name|DisMaxQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|field|queryFields
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|queryFields
decl_stmt|;
DECL|field|parsedUserQuery
specifier|protected
name|Query
name|parsedUserQuery
decl_stmt|;
DECL|field|boostParams
specifier|protected
name|String
index|[]
name|boostParams
decl_stmt|;
DECL|field|boostQueries
specifier|protected
name|List
argument_list|<
name|Query
argument_list|>
name|boostQueries
decl_stmt|;
DECL|field|altUserQuery
specifier|protected
name|Query
name|altUserQuery
decl_stmt|;
DECL|field|altQParser
specifier|protected
name|QParser
name|altQParser
decl_stmt|;
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|SolrParams
name|solrParams
init|=
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|localParams
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|queryFields
operator|=
name|parseQueryFields
argument_list|(
name|req
operator|.
name|getSchema
argument_list|()
argument_list|,
name|solrParams
argument_list|)
expr_stmt|;
comment|/* the main query we will execute.  we disable the coord because      * this query is an artificial construct      */
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|notBlank
init|=
name|addMainQuery
argument_list|(
name|query
argument_list|,
name|solrParams
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|notBlank
condition|)
return|return
literal|null
return|;
name|addBoostQuery
argument_list|(
name|query
argument_list|,
name|solrParams
argument_list|)
expr_stmt|;
name|addBoostFunctions
argument_list|(
name|query
argument_list|,
name|solrParams
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
DECL|method|addBoostFunctions
specifier|protected
name|void
name|addBoostFunctions
parameter_list|(
name|BooleanQuery
name|query
parameter_list|,
name|SolrParams
name|solrParams
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|String
index|[]
name|boostFuncs
init|=
name|solrParams
operator|.
name|getParams
argument_list|(
name|DisMaxParams
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
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|ff
init|=
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
name|boostFunc
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|ff
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Query
name|fq
init|=
name|subQuery
argument_list|(
name|f
argument_list|,
name|FunctionQParserPlugin
operator|.
name|NAME
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|Float
name|b
init|=
name|ff
operator|.
name|get
argument_list|(
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|b
condition|)
block|{
name|fq
operator|.
name|setBoost
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|add
argument_list|(
name|fq
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
block|}
DECL|method|addBoostQuery
specifier|protected
name|void
name|addBoostQuery
parameter_list|(
name|BooleanQuery
name|query
parameter_list|,
name|SolrParams
name|solrParams
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|boostParams
operator|=
name|solrParams
operator|.
name|getParams
argument_list|(
name|DisMaxParams
operator|.
name|BQ
argument_list|)
expr_stmt|;
comment|//List<Query> boostQueries = SolrPluginUtils.parseQueryStrings(req, boostParams);
name|boostQueries
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|boostParams
operator|!=
literal|null
operator|&&
name|boostParams
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|boostQueries
operator|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|qs
range|:
name|boostParams
control|)
block|{
if|if
condition|(
name|qs
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|Query
name|q
init|=
name|subQuery
argument_list|(
name|qs
argument_list|,
literal|null
argument_list|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|boostQueries
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|/* if the default boost was used, and we've got a BooleanQuery            * extract the subqueries out and use them directly            */
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
block|}
comment|/** Adds the main query to the query argument. If its blank then false is returned. */
DECL|method|addMainQuery
specifier|protected
name|boolean
name|addMainQuery
parameter_list|(
name|BooleanQuery
name|query
parameter_list|,
name|SolrParams
name|solrParams
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|phraseFields
init|=
name|SolrPluginUtils
operator|.
name|parseFieldBoosts
argument_list|(
name|solrParams
operator|.
name|getParams
argument_list|(
name|DisMaxParams
operator|.
name|PF
argument_list|)
argument_list|)
decl_stmt|;
name|float
name|tiebreaker
init|=
name|solrParams
operator|.
name|getFloat
argument_list|(
name|DisMaxParams
operator|.
name|TIE
argument_list|,
literal|0.0f
argument_list|)
decl_stmt|;
comment|/* a parser for dealing with user input, which will convert      * things to DisjunctionMaxQueries      */
name|SolrPluginUtils
operator|.
name|DisjunctionMaxQueryParser
name|up
init|=
name|getParser
argument_list|(
name|queryFields
argument_list|,
name|DisMaxParams
operator|.
name|QS
argument_list|,
name|solrParams
argument_list|,
name|tiebreaker
argument_list|)
decl_stmt|;
comment|/* for parsing sloppy phrases using DisjunctionMaxQueries */
name|SolrPluginUtils
operator|.
name|DisjunctionMaxQueryParser
name|pp
init|=
name|getParser
argument_list|(
name|phraseFields
argument_list|,
name|DisMaxParams
operator|.
name|PS
argument_list|,
name|solrParams
argument_list|,
name|tiebreaker
argument_list|)
decl_stmt|;
comment|/* * * Main User Query * * */
name|parsedUserQuery
operator|=
literal|null
expr_stmt|;
name|String
name|userQuery
init|=
name|getString
argument_list|()
decl_stmt|;
name|altUserQuery
operator|=
literal|null
expr_stmt|;
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
name|altUserQuery
operator|=
name|getAlternateUserQuery
argument_list|(
name|solrParams
argument_list|)
expr_stmt|;
if|if
condition|(
name|altUserQuery
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|query
operator|.
name|add
argument_list|(
name|altUserQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// There is a valid query string
name|userQuery
operator|=
name|SolrPluginUtils
operator|.
name|partialEscape
argument_list|(
name|SolrPluginUtils
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
name|userQuery
operator|=
name|SolrPluginUtils
operator|.
name|stripIllegalOperators
argument_list|(
name|userQuery
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|parsedUserQuery
operator|=
name|getUserQuery
argument_list|(
name|userQuery
argument_list|,
name|up
argument_list|,
name|solrParams
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|parsedUserQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|Query
name|phrase
init|=
name|getPhraseQuery
argument_list|(
name|userQuery
argument_list|,
name|pp
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
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|getAlternateUserQuery
specifier|protected
name|Query
name|getAlternateUserQuery
parameter_list|(
name|SolrParams
name|solrParams
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|String
name|altQ
init|=
name|solrParams
operator|.
name|get
argument_list|(
name|DisMaxParams
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
name|QParser
name|altQParser
init|=
name|subQuery
argument_list|(
name|altQ
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|altQParser
operator|.
name|getQuery
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|getPhraseQuery
specifier|protected
name|Query
name|getPhraseQuery
parameter_list|(
name|String
name|userQuery
parameter_list|,
name|SolrPluginUtils
operator|.
name|DisjunctionMaxQueryParser
name|pp
parameter_list|)
throws|throws
name|SyntaxError
block|{
comment|/* * * Add on Phrases for the Query * * */
comment|/* build up phrase boosting queries */
comment|/* if the userQuery already has some quotes, strip them out.      * we've already done the phrases they asked for in the main      * part of the query, this is to boost docs that may not have      * matched those phrases but do match looser phrases.     */
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
return|return
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
return|;
block|}
DECL|method|getUserQuery
specifier|protected
name|Query
name|getUserQuery
parameter_list|(
name|String
name|userQuery
parameter_list|,
name|SolrPluginUtils
operator|.
name|DisjunctionMaxQueryParser
name|up
parameter_list|,
name|SolrParams
name|solrParams
parameter_list|)
throws|throws
name|SyntaxError
block|{
name|String
name|minShouldMatch
init|=
name|parseMinShouldMatch
argument_list|(
name|req
operator|.
name|getSchema
argument_list|()
argument_list|,
name|solrParams
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
name|Query
name|query
init|=
name|dis
decl_stmt|;
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
name|SolrPluginUtils
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
name|SolrPluginUtils
operator|.
name|setMinShouldMatch
argument_list|(
name|t
argument_list|,
name|minShouldMatch
argument_list|)
expr_stmt|;
name|query
operator|=
name|t
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|method|getParser
specifier|protected
name|SolrPluginUtils
operator|.
name|DisjunctionMaxQueryParser
name|getParser
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fields
parameter_list|,
name|String
name|paramName
parameter_list|,
name|SolrParams
name|solrParams
parameter_list|,
name|float
name|tiebreaker
parameter_list|)
block|{
name|int
name|slop
init|=
name|solrParams
operator|.
name|getInt
argument_list|(
name|paramName
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|SolrPluginUtils
operator|.
name|DisjunctionMaxQueryParser
name|parser
init|=
operator|new
name|SolrPluginUtils
operator|.
name|DisjunctionMaxQueryParser
argument_list|(
name|this
argument_list|,
name|IMPOSSIBLE_FIELD_NAME
argument_list|)
decl_stmt|;
name|parser
operator|.
name|addAlias
argument_list|(
name|IMPOSSIBLE_FIELD_NAME
argument_list|,
name|tiebreaker
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setPhraseSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
return|return
name|parser
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultHighlightFields
specifier|public
name|String
index|[]
name|getDefaultHighlightFields
parameter_list|()
block|{
return|return
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
name|queryFields
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHighlightQuery
specifier|public
name|Query
name|getHighlightQuery
parameter_list|()
throws|throws
name|SyntaxError
block|{
return|return
name|parsedUserQuery
operator|==
literal|null
condition|?
name|altUserQuery
else|:
name|parsedUserQuery
return|;
block|}
annotation|@
name|Override
DECL|method|addDebugInfo
specifier|public
name|void
name|addDebugInfo
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|debugInfo
parameter_list|)
block|{
name|super
operator|.
name|addDebugInfo
argument_list|(
name|debugInfo
argument_list|)
expr_stmt|;
name|debugInfo
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
name|debugInfo
operator|.
name|add
argument_list|(
literal|"boost_queries"
argument_list|,
name|boostParams
argument_list|)
expr_stmt|;
name|debugInfo
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
name|debugInfo
operator|.
name|add
argument_list|(
literal|"boostfuncs"
argument_list|,
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
name|DisMaxParams
operator|.
name|BF
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
