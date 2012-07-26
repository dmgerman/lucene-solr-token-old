begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|search
operator|.
name|spell
operator|.
name|StringDistance
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
name|handler
operator|.
name|component
operator|.
name|SpellCheckMergeData
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
begin_comment
comment|/**  *<p>This class lets a query be run through multiple spell checkers.  *    The initial use-case is to use {@link WordBreakSolrSpellChecker}  *    in conjunction with a "standard" spell checker   *    (such as {@link DirectSolrSpellChecker}  *</p>  */
end_comment
begin_class
DECL|class|ConjunctionSolrSpellChecker
specifier|public
class|class
name|ConjunctionSolrSpellChecker
extends|extends
name|SolrSpellChecker
block|{
DECL|field|stringDistance
specifier|private
name|StringDistance
name|stringDistance
init|=
literal|null
decl_stmt|;
DECL|field|accuracy
specifier|private
name|Float
name|accuracy
init|=
literal|null
decl_stmt|;
DECL|field|dictionaryName
specifier|private
name|String
name|dictionaryName
init|=
literal|null
decl_stmt|;
DECL|field|queryAnalyzer
specifier|private
name|Analyzer
name|queryAnalyzer
init|=
literal|null
decl_stmt|;
DECL|field|checkers
specifier|private
name|List
argument_list|<
name|SolrSpellChecker
argument_list|>
name|checkers
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrSpellChecker
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|initalized
specifier|private
name|boolean
name|initalized
init|=
literal|false
decl_stmt|;
DECL|method|addChecker
specifier|public
name|void
name|addChecker
parameter_list|(
name|SolrSpellChecker
name|checker
parameter_list|)
block|{
if|if
condition|(
name|initalized
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Need to add checkers before calling init()"
argument_list|)
throw|;
block|}
try|try
block|{
if|if
condition|(
name|stringDistance
operator|==
literal|null
condition|)
block|{
name|stringDistance
operator|=
name|checker
operator|.
name|getStringDistance
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|stringDistance
operator|!=
name|checker
operator|.
name|getStringDistance
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All checkers need to use the same StringDistance."
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
if|if
condition|(
name|accuracy
operator|==
literal|null
condition|)
block|{
name|accuracy
operator|=
name|checker
operator|.
name|getAccuracy
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|accuracy
operator|!=
name|checker
operator|.
name|getAccuracy
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All checkers need to use the same Accuracy."
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|queryAnalyzer
operator|==
literal|null
condition|)
block|{
name|queryAnalyzer
operator|=
name|checker
operator|.
name|getQueryAnalyzer
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queryAnalyzer
operator|!=
name|checker
operator|.
name|getQueryAnalyzer
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All checkers need to use the same Analyzer."
argument_list|)
throw|;
block|}
name|checkers
operator|.
name|add
argument_list|(
name|checker
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|init
specifier|public
name|String
name|init
parameter_list|(
name|NamedList
name|config
parameter_list|,
name|SolrCore
name|core
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
name|checkers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SolrSpellChecker
name|c
init|=
name|checkers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|dn
init|=
name|c
operator|.
name|init
argument_list|(
name|config
argument_list|,
name|core
argument_list|)
decl_stmt|;
comment|//TODO:  in the future, we could develop this further to allow
comment|//        multiple spellcheckers with per-field dictionaries...
if|if
condition|(
name|dictionaryName
operator|!=
literal|null
operator|&&
operator|!
name|dictionaryName
operator|.
name|equals
argument_list|(
name|dn
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot have more than one dictionary. ("
operator|+
name|dn
operator|+
literal|" , "
operator|+
name|dictionaryName
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|dictionaryName
operator|=
name|dn
expr_stmt|;
block|}
if|if
condition|(
name|dictionaryName
operator|==
literal|null
condition|)
block|{
name|dictionaryName
operator|=
name|DEFAULT_DICTIONARY_NAME
expr_stmt|;
block|}
name|initalized
operator|=
literal|true
expr_stmt|;
return|return
name|dictionaryName
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|SolrSpellChecker
name|c
range|:
name|checkers
control|)
block|{
name|c
operator|.
name|build
argument_list|(
name|core
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSuggestions
specifier|public
name|SpellingResult
name|getSuggestions
parameter_list|(
name|SpellingOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|SpellingResult
index|[]
name|results
init|=
operator|new
name|SpellingResult
index|[
name|checkers
operator|.
name|size
argument_list|()
index|]
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
name|checkers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|results
index|[
name|i
index|]
operator|=
name|checkers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getSuggestions
argument_list|(
name|options
argument_list|)
expr_stmt|;
block|}
return|return
name|mergeCheckers
argument_list|(
name|results
argument_list|,
name|options
operator|.
name|count
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|mergeSuggestions
specifier|public
name|SpellingResult
name|mergeSuggestions
parameter_list|(
name|SpellCheckMergeData
name|mergeData
parameter_list|,
name|int
name|numSug
parameter_list|,
name|int
name|count
parameter_list|,
name|boolean
name|extendedResults
parameter_list|)
block|{
name|SpellingResult
index|[]
name|results
init|=
operator|new
name|SpellingResult
index|[
name|checkers
operator|.
name|size
argument_list|()
index|]
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
name|checkers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|results
index|[
name|i
index|]
operator|=
name|checkers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|mergeSuggestions
argument_list|(
name|mergeData
argument_list|,
name|numSug
argument_list|,
name|count
argument_list|,
name|extendedResults
argument_list|)
expr_stmt|;
block|}
return|return
name|mergeCheckers
argument_list|(
name|results
argument_list|,
name|numSug
argument_list|)
return|;
block|}
comment|//TODO: This just interleaves the results.  In the future, we might want to let users give each checker its
comment|//      own weight and use that in combination to score& frequency to sort the results ?
DECL|method|mergeCheckers
specifier|private
name|SpellingResult
name|mergeCheckers
parameter_list|(
name|SpellingResult
index|[]
name|results
parameter_list|,
name|int
name|numSug
parameter_list|)
block|{
name|Map
argument_list|<
name|Token
argument_list|,
name|Integer
argument_list|>
name|combinedTokenFrequency
init|=
operator|new
name|HashMap
argument_list|<
name|Token
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Token
argument_list|,
name|List
argument_list|<
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|>
name|allSuggestions
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|Token
argument_list|,
name|List
argument_list|<
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SpellingResult
name|result
range|:
name|results
control|)
block|{
if|if
condition|(
name|result
operator|.
name|getTokenFrequency
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|combinedTokenFrequency
operator|.
name|putAll
argument_list|(
name|result
operator|.
name|getTokenFrequency
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Token
argument_list|,
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|entry
range|:
name|result
operator|.
name|getSuggestions
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|allForThisToken
init|=
name|allSuggestions
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|allForThisToken
operator|==
literal|null
condition|)
block|{
name|allForThisToken
operator|=
operator|new
name|ArrayList
argument_list|<
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|allSuggestions
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|allForThisToken
argument_list|)
expr_stmt|;
block|}
name|allForThisToken
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|SpellingResult
name|combinedResult
init|=
operator|new
name|SpellingResult
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Token
argument_list|,
name|List
argument_list|<
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|>
name|entry
range|:
name|allSuggestions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Token
name|original
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|>
name|corrIters
init|=
operator|new
name|ArrayList
argument_list|<
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|>
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|corrections
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|corrIters
operator|.
name|add
argument_list|(
name|corrections
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|numberAdded
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|numberAdded
operator|<
name|numSug
condition|)
block|{
name|boolean
name|anyData
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|iter
range|:
name|corrIters
control|)
block|{
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|anyData
operator|=
literal|true
expr_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|corr
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|combinedResult
operator|.
name|add
argument_list|(
name|original
argument_list|,
name|corr
operator|.
name|getKey
argument_list|()
argument_list|,
name|corr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Integer
name|tokenFrequency
init|=
name|combinedTokenFrequency
operator|.
name|get
argument_list|(
name|original
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenFrequency
operator|!=
literal|null
condition|)
block|{
name|combinedResult
operator|.
name|addFrequency
argument_list|(
name|original
argument_list|,
name|tokenFrequency
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|++
name|numberAdded
operator|==
name|numSug
condition|)
block|{
break|break;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|anyData
condition|)
block|{
break|break;
block|}
block|}
block|}
return|return
name|combinedResult
return|;
block|}
annotation|@
name|Override
DECL|method|reload
specifier|public
name|void
name|reload
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|SolrSpellChecker
name|c
range|:
name|checkers
control|)
block|{
name|c
operator|.
name|reload
argument_list|(
name|core
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getQueryAnalyzer
specifier|public
name|Analyzer
name|getQueryAnalyzer
parameter_list|()
block|{
return|return
name|queryAnalyzer
return|;
block|}
annotation|@
name|Override
DECL|method|getDictionaryName
specifier|public
name|String
name|getDictionaryName
parameter_list|()
block|{
return|return
name|dictionaryName
return|;
block|}
annotation|@
name|Override
DECL|method|getAccuracy
specifier|protected
name|float
name|getAccuracy
parameter_list|()
block|{
if|if
condition|(
name|accuracy
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|getAccuracy
argument_list|()
return|;
block|}
return|return
name|accuracy
return|;
block|}
annotation|@
name|Override
DECL|method|getStringDistance
specifier|protected
name|StringDistance
name|getStringDistance
parameter_list|()
block|{
if|if
condition|(
name|stringDistance
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|getStringDistance
argument_list|()
return|;
block|}
return|return
name|stringDistance
return|;
block|}
annotation|@
name|Override
DECL|method|isSuggestionsMayOverlap
specifier|public
name|boolean
name|isSuggestionsMayOverlap
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
