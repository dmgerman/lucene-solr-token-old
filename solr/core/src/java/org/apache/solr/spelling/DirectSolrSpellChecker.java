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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|analysis
operator|.
name|core
operator|.
name|WhitespaceAnalyzer
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
name|DirectSpellChecker
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
name|lucene
operator|.
name|search
operator|.
name|spell
operator|.
name|SuggestMode
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
name|SuggestWordFrequencyComparator
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
name|SuggestWordQueue
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
begin_comment
comment|/**  * Spellchecker implementation that uses {@link DirectSpellChecker}  *<p>  * Requires no auxiliary index or data structure.  *<p>  * Supported options:  *<ul>  *<li>field: Used as the source of terms.  *<li>distanceMeasure: Sets {@link DirectSpellChecker#setDistance(StringDistance)}.   *       Note: to set the default {@link DirectSpellChecker#INTERNAL_LEVENSHTEIN}, use "internal".  *<li>accuracy: Sets {@link DirectSpellChecker#setAccuracy(float)}.  *<li>maxEdits: Sets {@link DirectSpellChecker#setMaxEdits(int)}.  *<li>minPrefix: Sets {@link DirectSpellChecker#setMinPrefix(int)}.  *<li>maxInspections: Sets {@link DirectSpellChecker#setMaxInspections(int)}.  *<li>comparatorClass: Sets {@link DirectSpellChecker#setComparator(Comparator)}.  *       Note: score-then-frequency can be specified as "score" and frequency-then-score  *       can be specified as "freq".  *<li>thresholdTokenFrequency: sets {@link DirectSpellChecker#setThresholdFrequency(float)}.  *<li>minQueryLength: sets {@link DirectSpellChecker#setMinQueryLength(int)}.  *<li>maxQueryFrequency: sets {@link DirectSpellChecker#setMaxQueryFrequency(float)}.  *</ul>  * @see DirectSpellChecker  */
end_comment
begin_class
DECL|class|DirectSolrSpellChecker
specifier|public
class|class
name|DirectSolrSpellChecker
extends|extends
name|SolrSpellChecker
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
name|DirectSolrSpellChecker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// configuration params shared with other spellcheckers
DECL|field|COMPARATOR_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|COMPARATOR_CLASS
init|=
name|AbstractLuceneSpellChecker
operator|.
name|COMPARATOR_CLASS
decl_stmt|;
DECL|field|SCORE_COMP
specifier|public
specifier|static
specifier|final
name|String
name|SCORE_COMP
init|=
name|AbstractLuceneSpellChecker
operator|.
name|SCORE_COMP
decl_stmt|;
DECL|field|FREQ_COMP
specifier|public
specifier|static
specifier|final
name|String
name|FREQ_COMP
init|=
name|AbstractLuceneSpellChecker
operator|.
name|FREQ_COMP
decl_stmt|;
DECL|field|STRING_DISTANCE
specifier|public
specifier|static
specifier|final
name|String
name|STRING_DISTANCE
init|=
name|AbstractLuceneSpellChecker
operator|.
name|STRING_DISTANCE
decl_stmt|;
DECL|field|ACCURACY
specifier|public
specifier|static
specifier|final
name|String
name|ACCURACY
init|=
name|AbstractLuceneSpellChecker
operator|.
name|ACCURACY
decl_stmt|;
DECL|field|THRESHOLD_TOKEN_FREQUENCY
specifier|public
specifier|static
specifier|final
name|String
name|THRESHOLD_TOKEN_FREQUENCY
init|=
name|IndexBasedSpellChecker
operator|.
name|THRESHOLD_TOKEN_FREQUENCY
decl_stmt|;
DECL|field|INTERNAL_DISTANCE
specifier|public
specifier|static
specifier|final
name|String
name|INTERNAL_DISTANCE
init|=
literal|"internal"
decl_stmt|;
DECL|field|DEFAULT_ACCURACY
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_ACCURACY
init|=
literal|0.5f
decl_stmt|;
DECL|field|DEFAULT_THRESHOLD_TOKEN_FREQUENCY
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_THRESHOLD_TOKEN_FREQUENCY
init|=
literal|0.0f
decl_stmt|;
DECL|field|MAXEDITS
specifier|public
specifier|static
specifier|final
name|String
name|MAXEDITS
init|=
literal|"maxEdits"
decl_stmt|;
DECL|field|DEFAULT_MAXEDITS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAXEDITS
init|=
literal|2
decl_stmt|;
comment|// params specific to this implementation
DECL|field|MINPREFIX
specifier|public
specifier|static
specifier|final
name|String
name|MINPREFIX
init|=
literal|"minPrefix"
decl_stmt|;
DECL|field|DEFAULT_MINPREFIX
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MINPREFIX
init|=
literal|1
decl_stmt|;
DECL|field|MAXINSPECTIONS
specifier|public
specifier|static
specifier|final
name|String
name|MAXINSPECTIONS
init|=
literal|"maxInspections"
decl_stmt|;
DECL|field|DEFAULT_MAXINSPECTIONS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAXINSPECTIONS
init|=
literal|5
decl_stmt|;
DECL|field|MINQUERYLENGTH
specifier|public
specifier|static
specifier|final
name|String
name|MINQUERYLENGTH
init|=
literal|"minQueryLength"
decl_stmt|;
DECL|field|DEFAULT_MINQUERYLENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MINQUERYLENGTH
init|=
literal|4
decl_stmt|;
DECL|field|MAXQUERYFREQUENCY
specifier|public
specifier|static
specifier|final
name|String
name|MAXQUERYFREQUENCY
init|=
literal|"maxQueryFrequency"
decl_stmt|;
DECL|field|DEFAULT_MAXQUERYFREQUENCY
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_MAXQUERYFREQUENCY
init|=
literal|0.01f
decl_stmt|;
DECL|field|checker
specifier|private
name|DirectSpellChecker
name|checker
init|=
operator|new
name|DirectSpellChecker
argument_list|()
decl_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"init: "
operator|+
name|config
argument_list|)
expr_stmt|;
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
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
name|comp
init|=
name|SuggestWordQueue
operator|.
name|DEFAULT_COMPARATOR
decl_stmt|;
name|String
name|compClass
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|COMPARATOR_CLASS
argument_list|)
decl_stmt|;
if|if
condition|(
name|compClass
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|compClass
operator|.
name|equalsIgnoreCase
argument_list|(
name|SCORE_COMP
argument_list|)
condition|)
name|comp
operator|=
name|SuggestWordQueue
operator|.
name|DEFAULT_COMPARATOR
expr_stmt|;
elseif|else
if|if
condition|(
name|compClass
operator|.
name|equalsIgnoreCase
argument_list|(
name|FREQ_COMP
argument_list|)
condition|)
name|comp
operator|=
operator|new
name|SuggestWordFrequencyComparator
argument_list|()
expr_stmt|;
else|else
comment|//must be a FQCN
name|comp
operator|=
operator|(
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
operator|)
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|compClass
argument_list|,
name|Comparator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|StringDistance
name|sd
init|=
name|DirectSpellChecker
operator|.
name|INTERNAL_LEVENSHTEIN
decl_stmt|;
name|String
name|distClass
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
name|STRING_DISTANCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|distClass
operator|!=
literal|null
operator|&&
operator|!
name|distClass
operator|.
name|equalsIgnoreCase
argument_list|(
name|INTERNAL_DISTANCE
argument_list|)
condition|)
name|sd
operator|=
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|newInstance
argument_list|(
name|distClass
argument_list|,
name|StringDistance
operator|.
name|class
argument_list|)
expr_stmt|;
name|float
name|minAccuracy
init|=
name|DEFAULT_ACCURACY
decl_stmt|;
name|Float
name|accuracy
init|=
operator|(
name|Float
operator|)
name|config
operator|.
name|get
argument_list|(
name|ACCURACY
argument_list|)
decl_stmt|;
if|if
condition|(
name|accuracy
operator|!=
literal|null
condition|)
name|minAccuracy
operator|=
name|accuracy
expr_stmt|;
name|int
name|maxEdits
init|=
name|DEFAULT_MAXEDITS
decl_stmt|;
name|Integer
name|edits
init|=
operator|(
name|Integer
operator|)
name|config
operator|.
name|get
argument_list|(
name|MAXEDITS
argument_list|)
decl_stmt|;
if|if
condition|(
name|edits
operator|!=
literal|null
condition|)
name|maxEdits
operator|=
name|edits
expr_stmt|;
name|int
name|minPrefix
init|=
name|DEFAULT_MINPREFIX
decl_stmt|;
name|Integer
name|prefix
init|=
operator|(
name|Integer
operator|)
name|config
operator|.
name|get
argument_list|(
name|MINPREFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
name|minPrefix
operator|=
name|prefix
expr_stmt|;
name|int
name|maxInspections
init|=
name|DEFAULT_MAXINSPECTIONS
decl_stmt|;
name|Integer
name|inspections
init|=
operator|(
name|Integer
operator|)
name|config
operator|.
name|get
argument_list|(
name|MAXINSPECTIONS
argument_list|)
decl_stmt|;
if|if
condition|(
name|inspections
operator|!=
literal|null
condition|)
name|maxInspections
operator|=
name|inspections
expr_stmt|;
name|float
name|minThreshold
init|=
name|DEFAULT_THRESHOLD_TOKEN_FREQUENCY
decl_stmt|;
name|Float
name|threshold
init|=
operator|(
name|Float
operator|)
name|config
operator|.
name|get
argument_list|(
name|THRESHOLD_TOKEN_FREQUENCY
argument_list|)
decl_stmt|;
if|if
condition|(
name|threshold
operator|!=
literal|null
condition|)
name|minThreshold
operator|=
name|threshold
expr_stmt|;
name|int
name|minQueryLength
init|=
name|DEFAULT_MINQUERYLENGTH
decl_stmt|;
name|Integer
name|queryLength
init|=
operator|(
name|Integer
operator|)
name|config
operator|.
name|get
argument_list|(
name|MINQUERYLENGTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryLength
operator|!=
literal|null
condition|)
name|minQueryLength
operator|=
name|queryLength
expr_stmt|;
name|float
name|maxQueryFrequency
init|=
name|DEFAULT_MAXQUERYFREQUENCY
decl_stmt|;
name|Float
name|queryFreq
init|=
operator|(
name|Float
operator|)
name|config
operator|.
name|get
argument_list|(
name|MAXQUERYFREQUENCY
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryFreq
operator|!=
literal|null
condition|)
name|maxQueryFrequency
operator|=
name|queryFreq
expr_stmt|;
name|checker
operator|.
name|setComparator
argument_list|(
name|comp
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setDistance
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setMaxEdits
argument_list|(
name|maxEdits
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setMinPrefix
argument_list|(
name|minPrefix
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setAccuracy
argument_list|(
name|minAccuracy
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setThresholdFrequency
argument_list|(
name|minThreshold
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setMaxInspections
argument_list|(
name|maxInspections
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setMinQueryLength
argument_list|(
name|minQueryLength
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setMaxQueryFrequency
argument_list|(
name|maxQueryFrequency
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setLowerCaseTerms
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|name
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
block|{}
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
block|{}
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"getSuggestions: "
operator|+
name|options
operator|.
name|tokens
argument_list|)
expr_stmt|;
name|SpellingResult
name|result
init|=
operator|new
name|SpellingResult
argument_list|()
decl_stmt|;
name|float
name|accuracy
init|=
operator|(
name|options
operator|.
name|accuracy
operator|==
name|Float
operator|.
name|MIN_VALUE
operator|)
condition|?
name|checker
operator|.
name|getAccuracy
argument_list|()
else|:
name|options
operator|.
name|accuracy
decl_stmt|;
name|SuggestMode
name|mode
init|=
name|options
operator|.
name|onlyMorePopular
condition|?
name|SuggestMode
operator|.
name|SUGGEST_MORE_POPULAR
else|:
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
decl_stmt|;
for|for
control|(
name|Token
name|token
range|:
name|options
operator|.
name|tokens
control|)
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|token
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|SuggestWord
index|[]
name|suggestions
init|=
name|checker
operator|.
name|suggestSimilar
argument_list|(
name|term
argument_list|,
name|options
operator|.
name|count
argument_list|,
name|options
operator|.
name|reader
argument_list|,
name|mode
argument_list|,
name|accuracy
argument_list|)
decl_stmt|;
name|int
name|docFreq
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|extendedResults
operator|||
name|suggestions
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|docFreq
operator|=
name|options
operator|.
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|extendedResults
condition|)
block|{
name|result
operator|.
name|addFrequency
argument_list|(
name|token
argument_list|,
name|docFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|suggestions
operator|.
name|length
operator|==
literal|0
operator|&&
name|docFreq
operator|==
literal|0
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|empty
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|,
name|empty
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|SuggestWord
name|suggestion
range|:
name|suggestions
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|,
name|suggestion
operator|.
name|string
argument_list|,
name|suggestion
operator|.
name|freq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getAccuracy
specifier|public
name|float
name|getAccuracy
parameter_list|()
block|{
return|return
name|checker
operator|.
name|getAccuracy
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getStringDistance
specifier|public
name|StringDistance
name|getStringDistance
parameter_list|()
block|{
return|return
name|checker
operator|.
name|getDistance
argument_list|()
return|;
block|}
block|}
end_class
end_unit
