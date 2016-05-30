begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package
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
name|Arrays
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
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
name|IndexReader
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
name|CursorMarkParams
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
name|SpellingParams
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
name|QueryComponent
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
name|SearchComponent
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
name|LocalSolrQueryRequest
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
name|EarlyTerminatingCollectorException
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
begin_class
DECL|class|SpellCheckCollator
specifier|public
class|class
name|SpellCheckCollator
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
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
DECL|field|maxCollations
specifier|private
name|int
name|maxCollations
init|=
literal|1
decl_stmt|;
DECL|field|maxCollationTries
specifier|private
name|int
name|maxCollationTries
init|=
literal|0
decl_stmt|;
DECL|field|maxCollationEvaluations
specifier|private
name|int
name|maxCollationEvaluations
init|=
literal|10000
decl_stmt|;
DECL|field|suggestionsMayOverlap
specifier|private
name|boolean
name|suggestionsMayOverlap
init|=
literal|false
decl_stmt|;
DECL|field|docCollectionLimit
specifier|private
name|int
name|docCollectionLimit
init|=
literal|0
decl_stmt|;
DECL|method|collate
specifier|public
name|List
argument_list|<
name|SpellCheckCollation
argument_list|>
name|collate
parameter_list|(
name|SpellingResult
name|result
parameter_list|,
name|String
name|originalQuery
parameter_list|,
name|ResponseBuilder
name|ultimateResponse
parameter_list|)
block|{
name|List
argument_list|<
name|SpellCheckCollation
argument_list|>
name|collations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|QueryComponent
name|queryComponent
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ultimateResponse
operator|.
name|components
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SearchComponent
name|sc
range|:
name|ultimateResponse
operator|.
name|components
control|)
block|{
if|if
condition|(
name|sc
operator|instanceof
name|QueryComponent
condition|)
block|{
name|queryComponent
operator|=
operator|(
name|QueryComponent
operator|)
name|sc
expr_stmt|;
break|break;
block|}
block|}
block|}
name|boolean
name|verifyCandidateWithQuery
init|=
literal|true
decl_stmt|;
name|int
name|maxTries
init|=
name|maxCollationTries
decl_stmt|;
name|int
name|maxNumberToIterate
init|=
name|maxTries
decl_stmt|;
if|if
condition|(
name|maxTries
operator|<
literal|1
condition|)
block|{
name|maxTries
operator|=
literal|1
expr_stmt|;
name|maxNumberToIterate
operator|=
name|maxCollations
expr_stmt|;
name|verifyCandidateWithQuery
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|queryComponent
operator|==
literal|null
operator|&&
name|verifyCandidateWithQuery
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not find an instance of QueryComponent.  Disabling collation verification against the index."
argument_list|)
expr_stmt|;
name|maxTries
operator|=
literal|1
expr_stmt|;
name|verifyCandidateWithQuery
operator|=
literal|false
expr_stmt|;
block|}
name|docCollectionLimit
operator|=
name|docCollectionLimit
operator|>
literal|0
condition|?
name|docCollectionLimit
else|:
literal|0
expr_stmt|;
name|int
name|maxDocId
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|verifyCandidateWithQuery
operator|&&
name|docCollectionLimit
operator|>
literal|0
condition|)
block|{
name|IndexReader
name|reader
init|=
name|ultimateResponse
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|maxDocId
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
name|int
name|tryNo
init|=
literal|0
decl_stmt|;
name|int
name|collNo
init|=
literal|0
decl_stmt|;
name|PossibilityIterator
name|possibilityIter
init|=
operator|new
name|PossibilityIterator
argument_list|(
name|result
operator|.
name|getSuggestions
argument_list|()
argument_list|,
name|maxNumberToIterate
argument_list|,
name|maxCollationEvaluations
argument_list|,
name|suggestionsMayOverlap
argument_list|)
decl_stmt|;
while|while
condition|(
name|tryNo
operator|<
name|maxTries
operator|&&
name|collNo
operator|<
name|maxCollations
operator|&&
name|possibilityIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|PossibilityIterator
operator|.
name|RankedSpellPossibility
name|possibility
init|=
name|possibilityIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|collationQueryStr
init|=
name|getCollation
argument_list|(
name|originalQuery
argument_list|,
name|possibility
operator|.
name|corrections
argument_list|)
decl_stmt|;
name|int
name|hits
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|verifyCandidateWithQuery
condition|)
block|{
name|tryNo
operator|++
expr_stmt|;
name|SolrParams
name|origParams
init|=
name|ultimateResponse
operator|.
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|origParams
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|origParamIterator
init|=
name|origParams
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
name|int
name|pl
init|=
name|SpellingParams
operator|.
name|SPELLCHECK_COLLATE_PARAM_OVERRIDE
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|origParamIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|origParamName
init|=
name|origParamIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|origParamName
operator|.
name|startsWith
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_COLLATE_PARAM_OVERRIDE
argument_list|)
operator|&&
name|origParamName
operator|.
name|length
argument_list|()
operator|>
name|pl
condition|)
block|{
name|String
index|[]
name|val
init|=
name|origParams
operator|.
name|getParams
argument_list|(
name|origParamName
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|.
name|length
operator|==
literal|1
operator|&&
name|val
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
name|origParamName
operator|.
name|substring
argument_list|(
name|pl
argument_list|)
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|params
operator|.
name|set
argument_list|(
name|origParamName
operator|.
name|substring
argument_list|(
name|pl
argument_list|)
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
name|collationQueryStr
argument_list|)
expr_stmt|;
name|params
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|START
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|ROWS
argument_list|,
literal|""
operator|+
name|docCollectionLimit
argument_list|)
expr_stmt|;
comment|// we don't want any stored fields
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
comment|// we'll sort by doc id to ensure no scoring is done.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|SORT
argument_list|,
literal|"_docid_ asc"
argument_list|)
expr_stmt|;
comment|// CursorMark does not like _docid_ sorting, and we don't need it.
name|params
operator|.
name|remove
argument_list|(
name|CursorMarkParams
operator|.
name|CURSOR_MARK_PARAM
argument_list|)
expr_stmt|;
comment|// If a dismax query, don't add unnecessary clauses for scoring
name|params
operator|.
name|remove
argument_list|(
name|DisMaxParams
operator|.
name|TIE
argument_list|)
expr_stmt|;
name|params
operator|.
name|remove
argument_list|(
name|DisMaxParams
operator|.
name|PF
argument_list|)
expr_stmt|;
name|params
operator|.
name|remove
argument_list|(
name|DisMaxParams
operator|.
name|PF2
argument_list|)
expr_stmt|;
name|params
operator|.
name|remove
argument_list|(
name|DisMaxParams
operator|.
name|PF3
argument_list|)
expr_stmt|;
name|params
operator|.
name|remove
argument_list|(
name|DisMaxParams
operator|.
name|BQ
argument_list|)
expr_stmt|;
name|params
operator|.
name|remove
argument_list|(
name|DisMaxParams
operator|.
name|BF
argument_list|)
expr_stmt|;
comment|// Collate testing does not support Grouping (see SOLR-2577)
name|params
operator|.
name|remove
argument_list|(
name|GroupParams
operator|.
name|GROUP
argument_list|)
expr_stmt|;
comment|// creating a request here... make sure to close it!
name|ResponseBuilder
name|checkResponse
init|=
operator|new
name|ResponseBuilder
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|ultimateResponse
operator|.
name|req
operator|.
name|getCore
argument_list|()
argument_list|,
name|params
argument_list|)
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|,
name|Arrays
operator|.
expr|<
name|SearchComponent
operator|>
name|asList
argument_list|(
name|queryComponent
argument_list|)
argument_list|)
decl_stmt|;
name|checkResponse
operator|.
name|setQparser
argument_list|(
name|ultimateResponse
operator|.
name|getQparser
argument_list|()
argument_list|)
expr_stmt|;
name|checkResponse
operator|.
name|setFilters
argument_list|(
name|ultimateResponse
operator|.
name|getFilters
argument_list|()
argument_list|)
expr_stmt|;
name|checkResponse
operator|.
name|setQueryString
argument_list|(
name|collationQueryStr
argument_list|)
expr_stmt|;
name|checkResponse
operator|.
name|components
operator|=
name|Arrays
operator|.
expr|<
name|SearchComponent
operator|>
name|asList
argument_list|(
name|queryComponent
argument_list|)
expr_stmt|;
try|try
block|{
name|queryComponent
operator|.
name|prepare
argument_list|(
name|checkResponse
argument_list|)
expr_stmt|;
if|if
condition|(
name|docCollectionLimit
operator|>
literal|0
condition|)
block|{
name|int
name|f
init|=
name|checkResponse
operator|.
name|getFieldFlags
argument_list|()
decl_stmt|;
name|checkResponse
operator|.
name|setFieldFlags
argument_list|(
name|f
operator||=
name|SolrIndexSearcher
operator|.
name|TERMINATE_EARLY
argument_list|)
expr_stmt|;
block|}
name|queryComponent
operator|.
name|process
argument_list|(
name|checkResponse
argument_list|)
expr_stmt|;
name|hits
operator|=
operator|(
name|Integer
operator|)
name|checkResponse
operator|.
name|rsp
operator|.
name|getToLog
argument_list|()
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EarlyTerminatingCollectorException
name|etce
parameter_list|)
block|{
assert|assert
operator|(
name|docCollectionLimit
operator|>
literal|0
operator|)
assert|;
assert|assert
literal|0
operator|<
name|etce
operator|.
name|getNumberScanned
argument_list|()
assert|;
assert|assert
literal|0
operator|<
name|etce
operator|.
name|getNumberCollected
argument_list|()
assert|;
if|if
condition|(
name|etce
operator|.
name|getNumberScanned
argument_list|()
operator|==
name|maxDocId
condition|)
block|{
name|hits
operator|=
name|etce
operator|.
name|getNumberCollected
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|hits
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
call|(
name|float
call|)
argument_list|(
name|maxDocId
operator|*
name|etce
operator|.
name|getNumberCollected
argument_list|()
argument_list|)
operator|)
operator|/
operator|(
name|float
operator|)
name|etce
operator|.
name|getNumberScanned
argument_list|()
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception trying to re-query to check if a spell check possibility would return any hits."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|checkResponse
operator|.
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hits
operator|>
literal|0
operator|||
operator|!
name|verifyCandidateWithQuery
condition|)
block|{
name|collNo
operator|++
expr_stmt|;
name|SpellCheckCollation
name|collation
init|=
operator|new
name|SpellCheckCollation
argument_list|()
decl_stmt|;
name|collation
operator|.
name|setCollationQuery
argument_list|(
name|collationQueryStr
argument_list|)
expr_stmt|;
name|collation
operator|.
name|setHits
argument_list|(
name|hits
argument_list|)
expr_stmt|;
name|collation
operator|.
name|setInternalRank
argument_list|(
name|suggestionsMayOverlap
condition|?
operator|(
operator|(
name|possibility
operator|.
name|rank
operator|*
literal|1000
operator|)
operator|+
name|possibility
operator|.
name|index
operator|)
else|:
name|possibility
operator|.
name|rank
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|misspellingsAndCorrections
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SpellCheckCorrection
name|corr
range|:
name|possibility
operator|.
name|corrections
control|)
block|{
name|misspellingsAndCorrections
operator|.
name|add
argument_list|(
name|corr
operator|.
name|getOriginal
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|corr
operator|.
name|getCorrection
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|collation
operator|.
name|setMisspellingsAndCorrections
argument_list|(
name|misspellingsAndCorrections
argument_list|)
expr_stmt|;
name|collations
operator|.
name|add
argument_list|(
name|collation
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Collation: "
operator|+
name|collationQueryStr
operator|+
operator|(
name|verifyCandidateWithQuery
condition|?
operator|(
literal|" will return "
operator|+
name|hits
operator|+
literal|" hits."
operator|)
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|collations
return|;
block|}
DECL|method|getCollation
specifier|private
name|String
name|getCollation
parameter_list|(
name|String
name|origQuery
parameter_list|,
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|corrections
parameter_list|)
block|{
name|StringBuilder
name|collation
init|=
operator|new
name|StringBuilder
argument_list|(
name|origQuery
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|String
name|corr
init|=
literal|""
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
name|corrections
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SpellCheckCorrection
name|correction
init|=
name|corrections
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Token
name|tok
init|=
name|correction
operator|.
name|getOriginal
argument_list|()
decl_stmt|;
comment|// we are replacing the query in order, but injected terms might cause
comment|// illegal offsets due to previous replacements.
if|if
condition|(
name|tok
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|corr
operator|=
name|correction
operator|.
name|getCorrection
argument_list|()
expr_stmt|;
name|boolean
name|addParenthesis
init|=
literal|false
decl_stmt|;
name|Character
name|requiredOrProhibited
init|=
literal|null
decl_stmt|;
name|int
name|indexOfSpace
init|=
name|corr
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
name|StringBuilder
name|corrSb
init|=
operator|new
name|StringBuilder
argument_list|(
name|corr
argument_list|)
decl_stmt|;
name|int
name|bump
init|=
literal|1
decl_stmt|;
comment|//If the correction contains whitespace (because it involved breaking a word in 2+ words),
comment|//then be sure all of the new words have the same optional/required/prohibited status in the query.
while|while
condition|(
name|indexOfSpace
operator|>
operator|-
literal|1
operator|&&
name|indexOfSpace
operator|<
name|corr
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
name|addParenthesis
operator|=
literal|true
expr_stmt|;
name|char
name|previousChar
init|=
name|tok
operator|.
name|startOffset
argument_list|()
operator|>
literal|0
condition|?
name|origQuery
operator|.
name|charAt
argument_list|(
name|tok
operator|.
name|startOffset
argument_list|()
operator|-
literal|1
argument_list|)
else|:
literal|' '
decl_stmt|;
if|if
condition|(
name|previousChar
operator|==
literal|'-'
operator|||
name|previousChar
operator|==
literal|'+'
condition|)
block|{
name|corrSb
operator|.
name|insert
argument_list|(
name|indexOfSpace
operator|+
name|bump
argument_list|,
name|previousChar
argument_list|)
expr_stmt|;
if|if
condition|(
name|requiredOrProhibited
operator|==
literal|null
condition|)
block|{
name|requiredOrProhibited
operator|=
name|previousChar
expr_stmt|;
block|}
name|bump
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|tok
operator|.
name|getFlags
argument_list|()
operator|&
name|QueryConverter
operator|.
name|TERM_IN_BOOLEAN_QUERY_FLAG
operator|)
operator|==
name|QueryConverter
operator|.
name|TERM_IN_BOOLEAN_QUERY_FLAG
condition|)
block|{
name|corrSb
operator|.
name|insert
argument_list|(
name|indexOfSpace
operator|+
name|bump
argument_list|,
literal|"AND "
argument_list|)
expr_stmt|;
name|bump
operator|+=
literal|4
expr_stmt|;
block|}
name|indexOfSpace
operator|=
name|correction
operator|.
name|getCorrection
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|,
name|indexOfSpace
operator|+
name|bump
argument_list|)
expr_stmt|;
block|}
name|int
name|oneForReqOrProhib
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|addParenthesis
condition|)
block|{
if|if
condition|(
name|requiredOrProhibited
operator|!=
literal|null
condition|)
block|{
name|corrSb
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
name|requiredOrProhibited
argument_list|)
expr_stmt|;
name|oneForReqOrProhib
operator|++
expr_stmt|;
block|}
name|corrSb
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
literal|'('
argument_list|)
expr_stmt|;
name|corrSb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
name|corr
operator|=
name|corrSb
operator|.
name|toString
argument_list|()
expr_stmt|;
name|int
name|startIndex
init|=
name|tok
operator|.
name|startOffset
argument_list|()
operator|+
name|offset
operator|-
name|oneForReqOrProhib
decl_stmt|;
name|int
name|endIndex
init|=
name|tok
operator|.
name|endOffset
argument_list|()
operator|+
name|offset
decl_stmt|;
name|collation
operator|.
name|replace
argument_list|(
name|startIndex
argument_list|,
name|endIndex
argument_list|,
name|corr
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|corr
operator|.
name|length
argument_list|()
operator|-
name|oneForReqOrProhib
operator|-
operator|(
name|tok
operator|.
name|endOffset
argument_list|()
operator|-
name|tok
operator|.
name|startOffset
argument_list|()
operator|)
expr_stmt|;
block|}
return|return
name|collation
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setMaxCollations
specifier|public
name|SpellCheckCollator
name|setMaxCollations
parameter_list|(
name|int
name|maxCollations
parameter_list|)
block|{
name|this
operator|.
name|maxCollations
operator|=
name|maxCollations
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMaxCollationTries
specifier|public
name|SpellCheckCollator
name|setMaxCollationTries
parameter_list|(
name|int
name|maxCollationTries
parameter_list|)
block|{
name|this
operator|.
name|maxCollationTries
operator|=
name|maxCollationTries
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMaxCollationEvaluations
specifier|public
name|SpellCheckCollator
name|setMaxCollationEvaluations
parameter_list|(
name|int
name|maxCollationEvaluations
parameter_list|)
block|{
name|this
operator|.
name|maxCollationEvaluations
operator|=
name|maxCollationEvaluations
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSuggestionsMayOverlap
specifier|public
name|SpellCheckCollator
name|setSuggestionsMayOverlap
parameter_list|(
name|boolean
name|suggestionsMayOverlap
parameter_list|)
block|{
name|this
operator|.
name|suggestionsMayOverlap
operator|=
name|suggestionsMayOverlap
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDocCollectionLimit
specifier|public
name|SpellCheckCollator
name|setDocCollectionLimit
parameter_list|(
name|int
name|docCollectionLimit
parameter_list|)
block|{
name|this
operator|.
name|docCollectionLimit
operator|=
name|docCollectionLimit
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class
end_unit
