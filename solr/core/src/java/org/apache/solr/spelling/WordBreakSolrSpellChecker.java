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
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|lucene
operator|.
name|index
operator|.
name|Term
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
name|CombineSuggestion
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
name|SuggestWord
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
name|WordBreakSpellChecker
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
name|WordBreakSpellChecker
operator|.
name|BreakSuggestionSortMethod
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import
begin_comment
comment|/**  *<p>  * A spellchecker that breaks and combines words.    *</p>  *<p>  * This will not combine adjacent tokens that do not have   * the same required status (prohibited, required, optional).    * However, this feature depends on incoming term flags   * being properly set. ({@link QueryConverter#PROHIBITED_TERM_FLAG},  * {@link QueryConverter#REQUIRED_TERM_FLAG},   * {@link QueryConverter#TERM_IN_BOOLEAN_QUERY_FLAG}, and  * {@link QueryConverter#TERM_PRECEDES_NEW_BOOLEAN_OPERATOR_FLAG} )  * This feature breaks completely if the upstream analyzer or query  * converter sets flags with the same values but different meanings.  * The default query converter (if not using "spellcheck.q")   * is {@link SpellingQueryConverter}, which properly sets these flags.  *</p>  */
end_comment
begin_class
DECL|class|WordBreakSolrSpellChecker
specifier|public
class|class
name|WordBreakSolrSpellChecker
extends|extends
name|SolrSpellChecker
block|{
comment|/**    *<p>    * Try to combine multiple words into one? [true|false]    *</p>    */
DECL|field|PARAM_COMBINE_WORDS
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_COMBINE_WORDS
init|=
literal|"combineWords"
decl_stmt|;
comment|/**    *<p>    * Try to break words into multiples? [true|false]    *</p>    */
DECL|field|PARAM_BREAK_WORDS
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_BREAK_WORDS
init|=
literal|"breakWords"
decl_stmt|;
comment|/**    * See {@link WordBreakSpellChecker#setMaxChanges}    */
DECL|field|PARAM_MAX_CHANGES
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_MAX_CHANGES
init|=
literal|"maxChanges"
decl_stmt|;
comment|/**    * See {@link WordBreakSpellChecker#setMaxCombineWordLength}    */
DECL|field|PARAM_MAX_COMBINE_WORD_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_MAX_COMBINE_WORD_LENGTH
init|=
literal|"maxCombinedLength"
decl_stmt|;
comment|/**    * See {@link WordBreakSpellChecker#setMinBreakWordLength}    */
DECL|field|PARAM_MIN_BREAK_WORD_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_MIN_BREAK_WORD_LENGTH
init|=
literal|"minBreakLength"
decl_stmt|;
comment|/**    * See {@link BreakSuggestionTieBreaker} for options.    */
DECL|field|PARAM_BREAK_SUGGESTION_TIE_BREAKER
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_BREAK_SUGGESTION_TIE_BREAKER
init|=
literal|"breakSugestionTieBreaker"
decl_stmt|;
comment|/**    * See {@link WordBreakSpellChecker#setMaxEvaluations}    */
DECL|field|PARAM_MAX_EVALUATIONS
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_MAX_EVALUATIONS
init|=
literal|"maxEvaluations"
decl_stmt|;
comment|/**    * See {@link WordBreakSpellChecker#setMinSuggestionFrequency}    */
DECL|field|PARAM_MIN_SUGGESTION_FREQUENCY
specifier|public
specifier|static
specifier|final
name|String
name|PARAM_MIN_SUGGESTION_FREQUENCY
init|=
literal|"minSuggestionFreq"
decl_stmt|;
comment|/**    *<p>    *  Specify a value on the "breakSugestionTieBreaker" parameter.    *    The default is MAX_FREQ.    *</p>      */
DECL|enum|BreakSuggestionTieBreaker
specifier|public
enum|enum
name|BreakSuggestionTieBreaker
block|{
comment|/**      * See      * {@link BreakSuggestionSortMethod#NUM_CHANGES_THEN_MAX_FREQUENCY}      * #      */
DECL|enum constant|MAX_FREQ
name|MAX_FREQ
block|,
comment|/**      * See      * {@link BreakSuggestionSortMethod#NUM_CHANGES_THEN_SUMMED_FREQUENCY}      */
DECL|enum constant|SUM_FREQ
name|SUM_FREQ
block|}
empty_stmt|;
DECL|field|wbsp
specifier|private
name|WordBreakSpellChecker
name|wbsp
init|=
literal|null
decl_stmt|;
DECL|field|combineWords
specifier|private
name|boolean
name|combineWords
init|=
literal|false
decl_stmt|;
DECL|field|breakWords
specifier|private
name|boolean
name|breakWords
init|=
literal|false
decl_stmt|;
DECL|field|sortMethod
specifier|private
name|BreakSuggestionSortMethod
name|sortMethod
init|=
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
decl_stmt|;
DECL|field|spacePattern
specifier|private
specifier|static
specifier|final
name|Pattern
name|spacePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|String
name|init
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
name|config
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|String
name|name
init|=
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|,
name|core
argument_list|)
decl_stmt|;
name|combineWords
operator|=
name|boolParam
argument_list|(
name|config
argument_list|,
name|PARAM_COMBINE_WORDS
argument_list|)
expr_stmt|;
name|breakWords
operator|=
name|boolParam
argument_list|(
name|config
argument_list|,
name|PARAM_BREAK_WORDS
argument_list|)
expr_stmt|;
name|wbsp
operator|=
operator|new
name|WordBreakSpellChecker
argument_list|()
expr_stmt|;
name|String
name|bstb
init|=
name|strParam
argument_list|(
name|config
argument_list|,
name|PARAM_BREAK_SUGGESTION_TIE_BREAKER
argument_list|)
decl_stmt|;
if|if
condition|(
name|bstb
operator|!=
literal|null
condition|)
block|{
name|bstb
operator|=
name|bstb
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
if|if
condition|(
name|bstb
operator|.
name|equals
argument_list|(
name|BreakSuggestionTieBreaker
operator|.
name|SUM_FREQ
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|sortMethod
operator|=
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_SUMMED_FREQUENCY
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bstb
operator|.
name|equals
argument_list|(
name|BreakSuggestionTieBreaker
operator|.
name|MAX_FREQ
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|sortMethod
operator|=
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid value for parameter "
operator|+
name|PARAM_BREAK_SUGGESTION_TIE_BREAKER
operator|+
literal|" : "
operator|+
name|bstb
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|sortMethod
operator|=
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
expr_stmt|;
block|}
name|int
name|mc
init|=
name|intParam
argument_list|(
name|config
argument_list|,
name|PARAM_MAX_CHANGES
argument_list|)
decl_stmt|;
if|if
condition|(
name|mc
operator|>
literal|0
condition|)
block|{
name|wbsp
operator|.
name|setMaxChanges
argument_list|(
name|mc
argument_list|)
expr_stmt|;
block|}
name|int
name|mcl
init|=
name|intParam
argument_list|(
name|config
argument_list|,
name|PARAM_MAX_COMBINE_WORD_LENGTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|mcl
operator|>
literal|0
condition|)
block|{
name|wbsp
operator|.
name|setMaxCombineWordLength
argument_list|(
name|mcl
argument_list|)
expr_stmt|;
block|}
name|int
name|mbwl
init|=
name|intParam
argument_list|(
name|config
argument_list|,
name|PARAM_MIN_BREAK_WORD_LENGTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|mbwl
operator|>
literal|0
condition|)
block|{
name|wbsp
operator|.
name|setMinBreakWordLength
argument_list|(
name|mbwl
argument_list|)
expr_stmt|;
block|}
name|int
name|me
init|=
name|intParam
argument_list|(
name|config
argument_list|,
name|PARAM_MAX_EVALUATIONS
argument_list|)
decl_stmt|;
if|if
condition|(
name|me
operator|>
literal|0
condition|)
block|{
name|wbsp
operator|.
name|setMaxEvaluations
argument_list|(
name|me
argument_list|)
expr_stmt|;
block|}
name|int
name|msf
init|=
name|intParam
argument_list|(
name|config
argument_list|,
name|PARAM_MIN_SUGGESTION_FREQUENCY
argument_list|)
decl_stmt|;
if|if
condition|(
name|msf
operator|>
literal|0
condition|)
block|{
name|wbsp
operator|.
name|setMinSuggestionFrequency
argument_list|(
name|msf
argument_list|)
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
DECL|method|strParam
specifier|private
name|String
name|strParam
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
name|config
parameter_list|,
name|String
name|paramName
parameter_list|)
block|{
name|Object
name|o
init|=
name|config
operator|.
name|get
argument_list|(
name|paramName
argument_list|)
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
literal|null
else|:
name|o
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|boolParam
specifier|private
name|boolean
name|boolParam
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
name|config
parameter_list|,
name|String
name|paramName
parameter_list|)
block|{
name|String
name|s
init|=
name|strParam
argument_list|(
name|config
argument_list|,
name|paramName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|s
argument_list|)
operator|||
literal|"on"
operator|.
name|equalsIgnoreCase
argument_list|(
name|s
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|intParam
specifier|private
name|int
name|intParam
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
name|config
parameter_list|,
name|String
name|paramName
parameter_list|)
block|{
name|Object
name|o
init|=
name|config
operator|.
name|get
argument_list|(
name|paramName
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid integer for parameter "
operator|+
name|paramName
operator|+
literal|" : "
operator|+
name|o
argument_list|)
throw|;
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
name|IndexReader
name|ir
init|=
name|options
operator|.
name|reader
decl_stmt|;
name|int
name|numSuggestions
init|=
name|options
operator|.
name|count
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Token
index|[]
name|tokenArr
init|=
name|options
operator|.
name|tokens
operator|.
name|toArray
argument_list|(
operator|new
name|Token
index|[
name|options
operator|.
name|tokens
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Token
argument_list|>
name|tokenArrWithSeparators
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|options
operator|.
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|2
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|termArr
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|options
operator|.
name|tokens
operator|.
name|size
argument_list|()
operator|+
literal|2
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ResultEntry
argument_list|>
name|breakSuggestionList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ResultEntry
argument_list|>
name|noBreakSuggestionList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|lastOneProhibited
init|=
literal|false
decl_stmt|;
name|boolean
name|lastOneRequired
init|=
literal|false
decl_stmt|;
name|boolean
name|lastOneprocedesNewBooleanOp
init|=
literal|false
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
name|tokenArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|prohibited
init|=
operator|(
name|tokenArr
index|[
name|i
index|]
operator|.
name|getFlags
argument_list|()
operator|&
name|QueryConverter
operator|.
name|PROHIBITED_TERM_FLAG
operator|)
operator|==
name|QueryConverter
operator|.
name|PROHIBITED_TERM_FLAG
decl_stmt|;
name|boolean
name|required
init|=
operator|(
name|tokenArr
index|[
name|i
index|]
operator|.
name|getFlags
argument_list|()
operator|&
name|QueryConverter
operator|.
name|REQUIRED_TERM_FLAG
operator|)
operator|==
name|QueryConverter
operator|.
name|REQUIRED_TERM_FLAG
decl_stmt|;
name|boolean
name|procedesNewBooleanOp
init|=
operator|(
name|tokenArr
index|[
name|i
index|]
operator|.
name|getFlags
argument_list|()
operator|&
name|QueryConverter
operator|.
name|TERM_PRECEDES_NEW_BOOLEAN_OPERATOR_FLAG
operator|)
operator|==
name|QueryConverter
operator|.
name|TERM_PRECEDES_NEW_BOOLEAN_OPERATOR_FLAG
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
operator|(
name|prohibited
operator|!=
name|lastOneProhibited
operator|||
name|required
operator|!=
name|lastOneRequired
operator|||
name|lastOneprocedesNewBooleanOp
operator|)
condition|)
block|{
name|termArr
operator|.
name|add
argument_list|(
name|WordBreakSpellChecker
operator|.
name|SEPARATOR_TERM
argument_list|)
expr_stmt|;
name|tokenArrWithSeparators
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|lastOneProhibited
operator|=
name|prohibited
expr_stmt|;
name|lastOneRequired
operator|=
name|required
expr_stmt|;
name|lastOneprocedesNewBooleanOp
operator|=
name|procedesNewBooleanOp
expr_stmt|;
name|Term
name|thisTerm
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|tokenArr
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|termArr
operator|.
name|add
argument_list|(
name|thisTerm
argument_list|)
expr_stmt|;
name|tokenArrWithSeparators
operator|.
name|add
argument_list|(
name|tokenArr
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|breakWords
condition|)
block|{
name|SuggestWord
index|[]
index|[]
name|breakSuggestions
init|=
name|wbsp
operator|.
name|suggestWordBreaks
argument_list|(
name|thisTerm
argument_list|,
name|numSuggestions
argument_list|,
name|ir
argument_list|,
name|options
operator|.
name|suggestMode
argument_list|,
name|sortMethod
argument_list|)
decl_stmt|;
if|if
condition|(
name|breakSuggestions
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|noBreakSuggestionList
operator|.
name|add
argument_list|(
operator|new
name|ResultEntry
argument_list|(
name|tokenArr
index|[
name|i
index|]
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|SuggestWord
index|[]
name|breakSuggestion
range|:
name|breakSuggestions
control|)
block|{
name|sb
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|firstOne
init|=
literal|true
decl_stmt|;
name|int
name|freq
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SuggestWord
name|word
range|:
name|breakSuggestion
control|)
block|{
if|if
condition|(
operator|!
name|firstOne
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|firstOne
operator|=
literal|false
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|word
operator|.
name|string
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortMethod
operator|==
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
condition|)
block|{
name|freq
operator|=
name|Math
operator|.
name|max
argument_list|(
name|freq
argument_list|,
name|word
operator|.
name|freq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|freq
operator|+=
name|word
operator|.
name|freq
expr_stmt|;
block|}
block|}
name|breakSuggestionList
operator|.
name|add
argument_list|(
operator|new
name|ResultEntry
argument_list|(
name|tokenArr
index|[
name|i
index|]
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|freq
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|breakSuggestionList
operator|.
name|addAll
argument_list|(
name|noBreakSuggestionList
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ResultEntry
argument_list|>
name|combineSuggestionList
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|CombineSuggestion
index|[]
name|combineSuggestions
init|=
name|wbsp
operator|.
name|suggestWordCombinations
argument_list|(
name|termArr
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|termArr
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|numSuggestions
argument_list|,
name|ir
argument_list|,
name|options
operator|.
name|suggestMode
argument_list|)
decl_stmt|;
if|if
condition|(
name|combineWords
condition|)
block|{
name|combineSuggestionList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|combineSuggestions
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|CombineSuggestion
name|cs
range|:
name|combineSuggestions
control|)
block|{
name|int
name|firstTermIndex
init|=
name|cs
operator|.
name|originalTermIndexes
index|[
literal|0
index|]
decl_stmt|;
name|int
name|lastTermIndex
init|=
name|cs
operator|.
name|originalTermIndexes
index|[
name|cs
operator|.
name|originalTermIndexes
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|sb
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|firstTermIndex
init|;
name|i
operator|<=
name|lastTermIndex
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
name|firstTermIndex
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|tokenArrWithSeparators
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Token
name|token
init|=
operator|new
name|Token
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|tokenArrWithSeparators
operator|.
name|get
argument_list|(
name|firstTermIndex
argument_list|)
operator|.
name|startOffset
argument_list|()
argument_list|,
name|tokenArrWithSeparators
operator|.
name|get
argument_list|(
name|lastTermIndex
argument_list|)
operator|.
name|endOffset
argument_list|()
argument_list|)
decl_stmt|;
name|combineSuggestionList
operator|.
name|add
argument_list|(
operator|new
name|ResultEntry
argument_list|(
name|token
argument_list|,
name|cs
operator|.
name|suggestion
operator|.
name|string
argument_list|,
name|cs
operator|.
name|suggestion
operator|.
name|freq
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Interleave the two lists of suggestions into one SpellingResult
name|SpellingResult
name|result
init|=
operator|new
name|SpellingResult
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|ResultEntry
argument_list|>
name|breakIter
init|=
name|breakSuggestionList
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|ResultEntry
argument_list|>
name|combineIter
init|=
name|combineSuggestionList
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|ResultEntry
name|lastBreak
init|=
name|breakIter
operator|.
name|hasNext
argument_list|()
condition|?
name|breakIter
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
name|ResultEntry
name|lastCombine
init|=
name|combineIter
operator|.
name|hasNext
argument_list|()
condition|?
name|combineIter
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
name|int
name|breakCount
init|=
literal|0
decl_stmt|;
name|int
name|combineCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|lastBreak
operator|!=
literal|null
operator|||
name|lastCombine
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|lastBreak
operator|==
literal|null
condition|)
block|{
name|addToResult
argument_list|(
name|result
argument_list|,
name|lastCombine
operator|.
name|token
argument_list|,
name|getCombineFrequency
argument_list|(
name|ir
argument_list|,
name|lastCombine
operator|.
name|token
argument_list|)
argument_list|,
name|lastCombine
operator|.
name|suggestion
argument_list|,
name|lastCombine
operator|.
name|freq
argument_list|)
expr_stmt|;
name|lastCombine
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lastCombine
operator|==
literal|null
condition|)
block|{
name|addToResult
argument_list|(
name|result
argument_list|,
name|lastBreak
operator|.
name|token
argument_list|,
name|ir
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|lastBreak
operator|.
name|token
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|lastBreak
operator|.
name|suggestion
argument_list|,
name|lastBreak
operator|.
name|freq
argument_list|)
expr_stmt|;
name|lastBreak
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lastBreak
operator|.
name|freq
operator|<
name|lastCombine
operator|.
name|freq
condition|)
block|{
name|addToResult
argument_list|(
name|result
argument_list|,
name|lastCombine
operator|.
name|token
argument_list|,
name|getCombineFrequency
argument_list|(
name|ir
argument_list|,
name|lastCombine
operator|.
name|token
argument_list|)
argument_list|,
name|lastCombine
operator|.
name|suggestion
argument_list|,
name|lastCombine
operator|.
name|freq
argument_list|)
expr_stmt|;
name|lastCombine
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lastCombine
operator|.
name|freq
operator|<
name|lastBreak
operator|.
name|freq
condition|)
block|{
name|addToResult
argument_list|(
name|result
argument_list|,
name|lastBreak
operator|.
name|token
argument_list|,
name|ir
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|lastBreak
operator|.
name|token
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|lastBreak
operator|.
name|suggestion
argument_list|,
name|lastBreak
operator|.
name|freq
argument_list|)
expr_stmt|;
name|lastBreak
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|breakCount
operator|>=
name|combineCount
condition|)
block|{
comment|//TODO: Should reverse>= to< ??S
name|addToResult
argument_list|(
name|result
argument_list|,
name|lastCombine
operator|.
name|token
argument_list|,
name|getCombineFrequency
argument_list|(
name|ir
argument_list|,
name|lastCombine
operator|.
name|token
argument_list|)
argument_list|,
name|lastCombine
operator|.
name|suggestion
argument_list|,
name|lastCombine
operator|.
name|freq
argument_list|)
expr_stmt|;
name|lastCombine
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|addToResult
argument_list|(
name|result
argument_list|,
name|lastBreak
operator|.
name|token
argument_list|,
name|ir
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|lastBreak
operator|.
name|token
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|lastBreak
operator|.
name|suggestion
argument_list|,
name|lastBreak
operator|.
name|freq
argument_list|)
expr_stmt|;
name|lastBreak
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|lastBreak
operator|==
literal|null
operator|&&
name|breakIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|lastBreak
operator|=
name|breakIter
operator|.
name|next
argument_list|()
expr_stmt|;
name|breakCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|lastCombine
operator|==
literal|null
operator|&&
name|combineIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|lastCombine
operator|=
name|combineIter
operator|.
name|next
argument_list|()
expr_stmt|;
name|combineCount
operator|++
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|addToResult
specifier|private
name|void
name|addToResult
parameter_list|(
name|SpellingResult
name|result
parameter_list|,
name|Token
name|token
parameter_list|,
name|int
name|tokenFrequency
parameter_list|,
name|String
name|suggestion
parameter_list|,
name|int
name|suggestionFrequency
parameter_list|)
block|{
if|if
condition|(
name|suggestion
operator|==
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|addFrequency
argument_list|(
name|token
argument_list|,
name|tokenFrequency
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|,
name|suggestion
argument_list|,
name|suggestionFrequency
argument_list|)
expr_stmt|;
name|result
operator|.
name|addFrequency
argument_list|(
name|token
argument_list|,
name|tokenFrequency
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getCombineFrequency
specifier|private
name|int
name|getCombineFrequency
parameter_list|(
name|IndexReader
name|ir
parameter_list|,
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|words
init|=
name|spacePattern
operator|.
name|split
argument_list|(
name|token
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|result
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|sortMethod
operator|==
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
condition|)
block|{
for|for
control|(
name|String
name|word
range|:
name|words
control|)
block|{
name|result
operator|=
name|Math
operator|.
name|max
argument_list|(
name|result
argument_list|,
name|ir
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|word
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|String
name|word
range|:
name|words
control|)
block|{
name|result
operator|+=
name|ir
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|word
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
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
block|{
comment|/* no-op */
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
comment|/* no-op */
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
