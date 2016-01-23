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
name|LinkedHashMap
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
name|List
import|;
end_import
begin_comment
comment|/**  * Implementations of SolrSpellChecker must return suggestions as SpellResult instance.  * This is converted into the required NamedList format in SpellCheckComponent.  *   * @since solr 1.3  */
end_comment
begin_class
DECL|class|SpellingResult
specifier|public
class|class
name|SpellingResult
block|{
DECL|field|tokens
specifier|private
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
decl_stmt|;
comment|/**    * Key == token    * Value = Map  -> key is the suggestion, value is the frequency of the token in the collection    */
DECL|field|suggestions
specifier|private
name|Map
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
name|suggestions
init|=
operator|new
name|LinkedHashMap
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
argument_list|()
decl_stmt|;
DECL|field|tokenFrequency
specifier|private
name|Map
argument_list|<
name|Token
argument_list|,
name|Integer
argument_list|>
name|tokenFrequency
decl_stmt|;
DECL|field|NO_FREQUENCY_INFO
specifier|public
specifier|static
specifier|final
name|int
name|NO_FREQUENCY_INFO
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|SpellingResult
specifier|public
name|SpellingResult
parameter_list|()
block|{   }
DECL|method|SpellingResult
specifier|public
name|SpellingResult
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
parameter_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
block|}
comment|/**    * Adds a whole bunch of suggestions, and does not worry about frequency.    *    * @param token The token to associate the suggestions with    * @param suggestions The suggestions    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Token
name|token
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|suggestions
parameter_list|)
block|{
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|map
init|=
name|this
operator|.
name|suggestions
operator|.
name|get
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|map
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|suggestions
operator|.
name|put
argument_list|(
name|token
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|suggestion
range|:
name|suggestions
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|suggestion
argument_list|,
name|NO_FREQUENCY_INFO
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Token
name|token
parameter_list|,
name|int
name|docFreq
parameter_list|)
block|{
if|if
condition|(
name|tokenFrequency
operator|==
literal|null
condition|)
block|{
name|tokenFrequency
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|Token
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|tokenFrequency
operator|.
name|put
argument_list|(
name|token
argument_list|,
name|docFreq
argument_list|)
expr_stmt|;
block|}
comment|/**    * Suggestions must be added with the best suggestion first.  ORDER is important.    * @param token The {@link org.apache.lucene.analysis.Token}    * @param suggestion The suggestion for the Token    * @param docFreq The document frequency    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Token
name|token
parameter_list|,
name|String
name|suggestion
parameter_list|,
name|int
name|docFreq
parameter_list|)
block|{
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|map
init|=
name|this
operator|.
name|suggestions
operator|.
name|get
argument_list|(
name|token
argument_list|)
decl_stmt|;
comment|//Don't bother adding if we already have this token
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|map
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|suggestions
operator|.
name|put
argument_list|(
name|token
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|suggestion
argument_list|,
name|docFreq
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the suggestions for the given token.    *    * @param token The {@link org.apache.lucene.analysis.Token} to look up    * @return A LinkedHashMap of the suggestions.  Key is the suggestion, value is the token frequency in the index, else {@link #NO_FREQUENCY_INFO}.    *    * The suggestions are added in sorted order (i.e. best suggestion first) then the iterator will return the suggestions in order    */
DECL|method|get
specifier|public
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|get
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
return|return
name|suggestions
operator|.
name|get
argument_list|(
name|token
argument_list|)
return|;
block|}
comment|/**    * The token frequency of the input token in the collection    *    * @param token The token    * @return The frequency or null    */
DECL|method|getTokenFrequency
specifier|public
name|Integer
name|getTokenFrequency
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
return|return
name|tokenFrequency
operator|.
name|get
argument_list|(
name|token
argument_list|)
return|;
block|}
DECL|method|hasTokenFrequencyInfo
specifier|public
name|boolean
name|hasTokenFrequencyInfo
parameter_list|()
block|{
return|return
name|tokenFrequency
operator|!=
literal|null
operator|&&
operator|!
name|tokenFrequency
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * All the suggestions.  The ordering of the inner LinkedHashMap is by best suggestion first.    * @return The Map of suggestions for each Token.  Key is the token, value is a LinkedHashMap whose key is the Suggestion and the value is the frequency or {@link #NO_FREQUENCY_INFO} if frequency info is not available.    *    */
DECL|method|getSuggestions
specifier|public
name|Map
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
name|getSuggestions
parameter_list|()
block|{
return|return
name|suggestions
return|;
block|}
DECL|method|getTokenFrequency
specifier|public
name|Map
argument_list|<
name|Token
argument_list|,
name|Integer
argument_list|>
name|getTokenFrequency
parameter_list|()
block|{
return|return
name|tokenFrequency
return|;
block|}
comment|/**    * @return The original tokens    */
DECL|method|getTokens
specifier|public
name|Collection
argument_list|<
name|Token
argument_list|>
name|getTokens
parameter_list|()
block|{
return|return
name|tokens
return|;
block|}
DECL|method|setTokens
specifier|public
name|void
name|setTokens
parameter_list|(
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
parameter_list|)
block|{
name|this
operator|.
name|tokens
operator|=
name|tokens
expr_stmt|;
block|}
block|}
end_class
end_unit
