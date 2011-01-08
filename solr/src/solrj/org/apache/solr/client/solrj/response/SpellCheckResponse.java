begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_comment
comment|/**  * Encapsulates responses from SpellCheckComponent  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SpellCheckResponse
specifier|public
class|class
name|SpellCheckResponse
block|{
DECL|field|correctlySpelled
specifier|private
name|boolean
name|correctlySpelled
decl_stmt|;
DECL|field|collations
specifier|private
name|List
argument_list|<
name|Collation
argument_list|>
name|collations
decl_stmt|;
DECL|field|suggestions
specifier|private
name|List
argument_list|<
name|Suggestion
argument_list|>
name|suggestions
init|=
operator|new
name|ArrayList
argument_list|<
name|Suggestion
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|suggestionMap
name|Map
argument_list|<
name|String
argument_list|,
name|Suggestion
argument_list|>
name|suggestionMap
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Suggestion
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|SpellCheckResponse
specifier|public
name|SpellCheckResponse
parameter_list|(
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|spellInfo
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|sugg
init|=
name|spellInfo
operator|.
name|get
argument_list|(
literal|"suggestions"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sugg
operator|==
literal|null
condition|)
block|{
name|correctlySpelled
operator|=
literal|true
expr_stmt|;
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sugg
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|n
init|=
name|sugg
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"correctlySpelled"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|correctlySpelled
operator|=
operator|(
name|Boolean
operator|)
name|sugg
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"collationInternalRank"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
comment|//continue;
block|}
elseif|else
if|if
condition|(
literal|"collation"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|collationInfo
init|=
name|sugg
operator|.
name|getAll
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|collations
operator|=
operator|new
name|ArrayList
argument_list|<
name|Collation
argument_list|>
argument_list|(
name|collationInfo
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|collationInfo
control|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|collations
operator|.
name|add
argument_list|(
operator|new
name|Collation
argument_list|()
operator|.
name|setCollationQueryString
argument_list|(
operator|(
name|String
operator|)
name|sugg
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|NamedList
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|Object
argument_list|>
name|expandedCollation
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|o
decl_stmt|;
name|String
name|collationQuery
init|=
operator|(
name|String
operator|)
name|expandedCollation
operator|.
name|get
argument_list|(
literal|"collationQuery"
argument_list|)
decl_stmt|;
name|int
name|hits
init|=
operator|(
name|Integer
operator|)
name|expandedCollation
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|String
argument_list|>
name|misspellingsAndCorrections
init|=
operator|(
name|NamedList
argument_list|<
name|String
argument_list|>
operator|)
name|expandedCollation
operator|.
name|get
argument_list|(
literal|"misspellingsAndCorrections"
argument_list|)
decl_stmt|;
name|Collation
name|collation
init|=
operator|new
name|Collation
argument_list|()
decl_stmt|;
name|collation
operator|.
name|setCollationQueryString
argument_list|(
name|collationQuery
argument_list|)
expr_stmt|;
name|collation
operator|.
name|setNumberOfHits
argument_list|(
name|hits
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|misspellingsAndCorrections
operator|.
name|size
argument_list|()
condition|;
name|ii
operator|++
control|)
block|{
name|String
name|misspelling
init|=
name|misspellingsAndCorrections
operator|.
name|getName
argument_list|(
name|ii
argument_list|)
decl_stmt|;
name|String
name|correction
init|=
name|misspellingsAndCorrections
operator|.
name|getVal
argument_list|(
name|ii
argument_list|)
decl_stmt|;
name|collation
operator|.
name|addMisspellingsAndCorrection
argument_list|(
operator|new
name|Correction
argument_list|(
name|misspelling
argument_list|,
name|correction
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|collations
operator|.
name|add
argument_list|(
name|collation
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Should get Lists of Strings or List of NamedLists here."
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Suggestion
name|s
init|=
operator|new
name|Suggestion
argument_list|(
name|n
argument_list|,
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|sugg
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|suggestionMap
operator|.
name|put
argument_list|(
name|n
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|suggestions
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isCorrectlySpelled
specifier|public
name|boolean
name|isCorrectlySpelled
parameter_list|()
block|{
return|return
name|correctlySpelled
return|;
block|}
DECL|method|getSuggestions
specifier|public
name|List
argument_list|<
name|Suggestion
argument_list|>
name|getSuggestions
parameter_list|()
block|{
return|return
name|suggestions
return|;
block|}
DECL|method|getSuggestionMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Suggestion
argument_list|>
name|getSuggestionMap
parameter_list|()
block|{
return|return
name|suggestionMap
return|;
block|}
DECL|method|getSuggestion
specifier|public
name|Suggestion
name|getSuggestion
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|suggestionMap
operator|.
name|get
argument_list|(
name|token
argument_list|)
return|;
block|}
DECL|method|getFirstSuggestion
specifier|public
name|String
name|getFirstSuggestion
parameter_list|(
name|String
name|token
parameter_list|)
block|{
name|Suggestion
name|s
init|=
name|suggestionMap
operator|.
name|get
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|getAlternatives
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|null
return|;
return|return
name|s
operator|.
name|getAlternatives
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    *<p>    *  Return the first collated query string.  For convenience and backwards-compatibility.  Use getCollatedResults() for full data.    *</p>    * @return    */
DECL|method|getCollatedResult
specifier|public
name|String
name|getCollatedResult
parameter_list|()
block|{
return|return
name|collations
operator|==
literal|null
operator|||
name|collations
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|?
literal|null
else|:
name|collations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|collationQueryString
return|;
block|}
comment|/**    *<p>    *  Return all collations.      *  Will include # of hits and misspelling-to-correction details if "spellcheck.collateExtendedResults was true.    *</p>    * @return    */
DECL|method|getCollatedResults
specifier|public
name|List
argument_list|<
name|Collation
argument_list|>
name|getCollatedResults
parameter_list|()
block|{
return|return
name|collations
return|;
block|}
DECL|class|Suggestion
specifier|public
specifier|static
class|class
name|Suggestion
block|{
DECL|field|token
specifier|private
name|String
name|token
decl_stmt|;
DECL|field|numFound
specifier|private
name|int
name|numFound
decl_stmt|;
DECL|field|startOffset
specifier|private
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
specifier|private
name|int
name|endOffset
decl_stmt|;
DECL|field|originalFrequency
specifier|private
name|int
name|originalFrequency
decl_stmt|;
DECL|field|alternatives
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|alternatives
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|alternativeFrequencies
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|alternativeFrequencies
decl_stmt|;
DECL|method|Suggestion
specifier|public
name|Suggestion
parameter_list|(
name|String
name|token
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|suggestion
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|suggestion
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|n
init|=
name|suggestion
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"numFound"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|numFound
operator|=
operator|(
name|Integer
operator|)
name|suggestion
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"startOffset"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|startOffset
operator|=
operator|(
name|Integer
operator|)
name|suggestion
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"endOffset"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|endOffset
operator|=
operator|(
name|Integer
operator|)
name|suggestion
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"origFreq"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|originalFrequency
operator|=
operator|(
name|Integer
operator|)
name|suggestion
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"suggestion"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
name|list
init|=
operator|(
name|List
operator|)
name|suggestion
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|NamedList
condition|)
block|{
comment|// extended results detected
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|NamedList
argument_list|>
name|extended
init|=
operator|(
name|List
argument_list|<
name|NamedList
argument_list|>
operator|)
name|list
decl_stmt|;
name|alternativeFrequencies
operator|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|NamedList
name|nl
range|:
name|extended
control|)
block|{
name|alternatives
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"word"
argument_list|)
argument_list|)
expr_stmt|;
name|alternativeFrequencies
operator|.
name|add
argument_list|(
operator|(
name|Integer
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"freq"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|String
argument_list|>
name|alts
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|list
decl_stmt|;
name|alternatives
operator|.
name|addAll
argument_list|(
name|alts
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|getToken
specifier|public
name|String
name|getToken
parameter_list|()
block|{
return|return
name|token
return|;
block|}
DECL|method|getNumFound
specifier|public
name|int
name|getNumFound
parameter_list|()
block|{
return|return
name|numFound
return|;
block|}
DECL|method|getStartOffset
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
DECL|method|getEndOffset
specifier|public
name|int
name|getEndOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
DECL|method|getOriginalFrequency
specifier|public
name|int
name|getOriginalFrequency
parameter_list|()
block|{
return|return
name|originalFrequency
return|;
block|}
comment|/** The list of alternatives */
DECL|method|getAlternatives
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getAlternatives
parameter_list|()
block|{
return|return
name|alternatives
return|;
block|}
comment|/** The frequencies of the alternatives in the corpus, or null if this information was not returned */
DECL|method|getAlternativeFrequencies
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getAlternativeFrequencies
parameter_list|()
block|{
return|return
name|alternativeFrequencies
return|;
block|}
annotation|@
name|Deprecated
comment|/** @see #getAlternatives */
DECL|method|getSuggestions
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getSuggestions
parameter_list|()
block|{
return|return
name|alternatives
return|;
block|}
annotation|@
name|Deprecated
comment|/** @see #getAlternativeFrequencies */
DECL|method|getSuggestionFrequencies
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getSuggestionFrequencies
parameter_list|()
block|{
return|return
name|alternativeFrequencies
return|;
block|}
block|}
DECL|class|Collation
specifier|public
class|class
name|Collation
block|{
DECL|field|collationQueryString
specifier|private
name|String
name|collationQueryString
decl_stmt|;
DECL|field|misspellingsAndCorrections
specifier|private
name|List
argument_list|<
name|Correction
argument_list|>
name|misspellingsAndCorrections
init|=
operator|new
name|ArrayList
argument_list|<
name|Correction
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|numberOfHits
specifier|private
name|long
name|numberOfHits
decl_stmt|;
DECL|method|getNumberOfHits
specifier|public
name|long
name|getNumberOfHits
parameter_list|()
block|{
return|return
name|numberOfHits
return|;
block|}
DECL|method|setNumberOfHits
specifier|public
name|void
name|setNumberOfHits
parameter_list|(
name|long
name|numberOfHits
parameter_list|)
block|{
name|this
operator|.
name|numberOfHits
operator|=
name|numberOfHits
expr_stmt|;
block|}
DECL|method|getCollationQueryString
specifier|public
name|String
name|getCollationQueryString
parameter_list|()
block|{
return|return
name|collationQueryString
return|;
block|}
DECL|method|setCollationQueryString
specifier|public
name|Collation
name|setCollationQueryString
parameter_list|(
name|String
name|collationQueryString
parameter_list|)
block|{
name|this
operator|.
name|collationQueryString
operator|=
name|collationQueryString
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getMisspellingsAndCorrections
specifier|public
name|List
argument_list|<
name|Correction
argument_list|>
name|getMisspellingsAndCorrections
parameter_list|()
block|{
return|return
name|misspellingsAndCorrections
return|;
block|}
DECL|method|addMisspellingsAndCorrection
specifier|public
name|Collation
name|addMisspellingsAndCorrection
parameter_list|(
name|Correction
name|correction
parameter_list|)
block|{
name|this
operator|.
name|misspellingsAndCorrections
operator|.
name|add
argument_list|(
name|correction
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
DECL|class|Correction
specifier|public
class|class
name|Correction
block|{
DECL|field|original
specifier|private
name|String
name|original
decl_stmt|;
DECL|field|correction
specifier|private
name|String
name|correction
decl_stmt|;
DECL|method|Correction
specifier|public
name|Correction
parameter_list|(
name|String
name|original
parameter_list|,
name|String
name|correction
parameter_list|)
block|{
name|this
operator|.
name|original
operator|=
name|original
expr_stmt|;
name|this
operator|.
name|correction
operator|=
name|correction
expr_stmt|;
block|}
DECL|method|getOriginal
specifier|public
name|String
name|getOriginal
parameter_list|()
block|{
return|return
name|original
return|;
block|}
DECL|method|setOriginal
specifier|public
name|void
name|setOriginal
parameter_list|(
name|String
name|original
parameter_list|)
block|{
name|this
operator|.
name|original
operator|=
name|original
expr_stmt|;
block|}
DECL|method|getCorrection
specifier|public
name|String
name|getCorrection
parameter_list|()
block|{
return|return
name|correction
return|;
block|}
DECL|method|setCorrection
specifier|public
name|void
name|setCorrection
parameter_list|(
name|String
name|correction
parameter_list|)
block|{
name|this
operator|.
name|correction
operator|=
name|correction
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
