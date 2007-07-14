begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|Collection
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|TokenFilter
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
name|TokenStream
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
name|analysis
operator|.
name|BaseTokenFilterFactory
import|;
end_import
begin_comment
comment|/**  * A filter to apply normal capitalization rules to Tokens.  It will make the first letter  * capital and the rest lower case.    *   * This filter is particularly useful to build nice looking facet parameters.  This filter  * is not appropriate if you intend to use a prefix query.  *   * The factory takes parameters:  * "onlyFirstWord" - should each word be capitalized or all of the words?  * "keep" - a keep word list.  Each word that should be kept separated by whitespace.  * "okPrefix" - do not change word capitalization if a word begins with something in this list.  *   for example if "McK" is on the okPrefix list, the word "McKinley" should not be changed to  *   "Mckinley"  * "minWordLength" - how long the word needs to be to get capitalization applied.  If the   *   minWordLength is 3, "and"> "And" but "or" stays "or"  * "maxWordCount" - if the token contains more then maxWordCount words, the capitalization is  *   assumed to be correct.  *   * @since solr 1.3  * @version $Id$  */
end_comment
begin_class
DECL|class|CapitalizationFilterFactory
specifier|public
class|class
name|CapitalizationFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
DECL|field|KEEP
specifier|public
specifier|static
specifier|final
name|String
name|KEEP
init|=
literal|"keep"
decl_stmt|;
DECL|field|OK_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|OK_PREFIX
init|=
literal|"okPrefix"
decl_stmt|;
DECL|field|MIN_WORD_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|MIN_WORD_LENGTH
init|=
literal|"minWordLength"
decl_stmt|;
DECL|field|MAX_WORD_COUNT
specifier|public
specifier|static
specifier|final
name|String
name|MAX_WORD_COUNT
init|=
literal|"maxWordCount"
decl_stmt|;
DECL|field|MAX_TOKEN_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|MAX_TOKEN_LENGTH
init|=
literal|"maxTokenLength"
decl_stmt|;
DECL|field|ONLY_FIRST_WORD
specifier|public
specifier|static
specifier|final
name|String
name|ONLY_FIRST_WORD
init|=
literal|"onlyFirstWord"
decl_stmt|;
DECL|field|FORCE_FIRST_LETTER
specifier|public
specifier|static
specifier|final
name|String
name|FORCE_FIRST_LETTER
init|=
literal|"forceFirstLetter"
decl_stmt|;
DECL|field|keep
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|keep
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// not synchronized because it is only initialized once
DECL|field|okPrefix
name|Collection
argument_list|<
name|String
argument_list|>
name|okPrefix
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// for Example: McK
DECL|field|minWordLength
name|int
name|minWordLength
init|=
literal|0
decl_stmt|;
comment|// don't modify capitalization for words shorter then this
DECL|field|maxWordCount
name|int
name|maxWordCount
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|maxTokenLength
name|int
name|maxTokenLength
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|onlyFirstWord
name|boolean
name|onlyFirstWord
init|=
literal|true
decl_stmt|;
DECL|field|forceFirstLetter
name|boolean
name|forceFirstLetter
init|=
literal|true
decl_stmt|;
comment|// make sure the first letter is capitol even if it is in the keep list
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|k
init|=
name|args
operator|.
name|get
argument_list|(
name|KEEP
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|k
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|k
operator|=
name|st
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
expr_stmt|;
name|keep
operator|.
name|put
argument_list|(
name|k
operator|.
name|toUpperCase
argument_list|()
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|OK_PREFIX
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|k
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|okPrefix
operator|.
name|add
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|MIN_WORD_LENGTH
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|minWordLength
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|MAX_WORD_COUNT
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|maxWordCount
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|maxTokenLength
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|ONLY_FIRST_WORD
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|onlyFirstWord
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|k
operator|=
name|args
operator|.
name|get
argument_list|(
name|FORCE_FIRST_LETTER
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
name|forceFirstLetter
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processWord
specifier|public
name|String
name|processWord
parameter_list|(
name|String
name|w
parameter_list|,
name|int
name|wordCount
parameter_list|)
block|{
if|if
condition|(
name|w
operator|.
name|length
argument_list|()
operator|<
literal|1
condition|)
block|{
return|return
name|w
return|;
block|}
if|if
condition|(
name|onlyFirstWord
operator|&&
name|wordCount
operator|>
literal|0
condition|)
block|{
return|return
name|w
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
name|String
name|k
init|=
name|keep
operator|.
name|get
argument_list|(
name|w
operator|.
name|toUpperCase
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|wordCount
operator|==
literal|0
operator|&&
name|forceFirstLetter
operator|&&
name|Character
operator|.
name|isLowerCase
argument_list|(
name|k
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|Character
operator|.
name|toUpperCase
argument_list|(
name|k
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
name|k
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
return|;
block|}
return|return
name|k
return|;
block|}
if|if
condition|(
name|w
operator|.
name|length
argument_list|()
operator|<
name|minWordLength
condition|)
block|{
return|return
name|w
return|;
block|}
for|for
control|(
name|String
name|prefix
range|:
name|okPrefix
control|)
block|{
if|if
condition|(
name|w
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
return|return
name|w
return|;
block|}
block|}
comment|// We know it has at least one character
name|char
index|[]
name|chars
init|=
name|w
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|StringBuilder
name|word
init|=
operator|new
name|StringBuilder
argument_list|(
name|w
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|word
operator|.
name|append
argument_list|(
name|Character
operator|.
name|toUpperCase
argument_list|(
name|chars
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|chars
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|word
operator|.
name|append
argument_list|(
name|Character
operator|.
name|toLowerCase
argument_list|(
name|chars
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|word
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|CapitalizationFilter
argument_list|(
name|input
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_class
begin_comment
comment|/**  * This relies on the Factory so that the difficult stuff does not need to be  * re-initialized each time the filter runs.  *   * This is package protected since it is not useful without the Factory  */
end_comment
begin_class
DECL|class|CapitalizationFilter
class|class
name|CapitalizationFilter
extends|extends
name|TokenFilter
block|{
DECL|field|factory
specifier|protected
specifier|final
name|CapitalizationFilterFactory
name|factory
decl_stmt|;
DECL|method|CapitalizationFilter
specifier|public
name|CapitalizationFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
specifier|final
name|CapitalizationFilterFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|Token
name|t
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|String
name|s
init|=
name|t
operator|.
name|termText
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|<
name|factory
operator|.
name|maxTokenLength
condition|)
block|{
name|int
name|wordCount
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|word
init|=
operator|new
name|StringBuilder
argument_list|(
name|s
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|(
name|s
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|char
name|c
range|:
name|s
operator|.
name|toCharArray
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|<=
literal|' '
operator|||
name|c
operator|==
literal|'.'
condition|)
block|{
if|if
condition|(
name|word
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|text
operator|.
name|append
argument_list|(
name|factory
operator|.
name|processWord
argument_list|(
name|word
operator|.
name|toString
argument_list|()
argument_list|,
name|wordCount
operator|++
argument_list|)
argument_list|)
expr_stmt|;
name|word
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|text
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|word
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add the last word
if|if
condition|(
name|word
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|text
operator|.
name|append
argument_list|(
name|factory
operator|.
name|processWord
argument_list|(
name|word
operator|.
name|toString
argument_list|()
argument_list|,
name|wordCount
operator|++
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|wordCount
operator|<=
name|factory
operator|.
name|maxWordCount
condition|)
block|{
name|t
operator|.
name|setTermText
argument_list|(
name|text
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|t
return|;
block|}
block|}
end_class
end_unit
