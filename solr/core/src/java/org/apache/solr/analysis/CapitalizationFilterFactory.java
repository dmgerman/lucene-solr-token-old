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
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
operator|.
name|CapitalizationFilter
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
name|util
operator|.
name|CharArraySet
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
name|util
operator|.
name|TokenFilterFactory
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
name|Collections
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
begin_comment
comment|/**  * Factory for {@link CapitalizationFilter}.  *<p/>  * The factory takes parameters:<br/>  * "onlyFirstWord" - should each word be capitalized or all of the words?<br/>  * "keep" - a keep word list.  Each word that should be kept separated by whitespace.<br/>  * "keepIgnoreCase - true or false.  If true, the keep list will be considered case-insensitive.<br/>  * "forceFirstLetter" - Force the first letter to be capitalized even if it is in the keep list<br/>  * "okPrefix" - do not change word capitalization if a word begins with something in this list.  * for example if "McK" is on the okPrefix list, the word "McKinley" should not be changed to  * "Mckinley"<br/>  * "minWordLength" - how long the word needs to be to get capitalization applied.  If the  * minWordLength is 3, "and"> "And" but "or" stays "or"<br/>  * "maxWordCount" - if the token contains more then maxWordCount words, the capitalization is  * assumed to be correct.<br/>  *  *<pre class="prettyprint">  *&lt;fieldType name="text_cptlztn" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.CapitalizationFilterFactory" onlyFirstWord="true"  *     	     keep="java solr lucene" keepIgnoreCase="false"  *     	     okPrefix="McK McD McA"/&gt;     *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|CapitalizationFilterFactory
specifier|public
class|class
name|CapitalizationFilterFactory
extends|extends
name|TokenFilterFactory
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
DECL|field|KEEP_IGNORE_CASE
specifier|public
specifier|static
specifier|final
name|String
name|KEEP_IGNORE_CASE
init|=
literal|"keepIgnoreCase"
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
comment|//Map<String,String> keep = new HashMap<String, String>(); // not synchronized because it is only initialized once
DECL|field|keep
name|CharArraySet
name|keep
decl_stmt|;
DECL|field|okPrefix
name|Collection
argument_list|<
name|char
index|[]
argument_list|>
name|okPrefix
init|=
name|Collections
operator|.
name|emptyList
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
name|CapitalizationFilter
operator|.
name|DEFAULT_MAX_WORD_COUNT
decl_stmt|;
DECL|field|maxTokenLength
name|int
name|maxTokenLength
init|=
name|CapitalizationFilter
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
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
name|assureMatchVersion
argument_list|()
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
name|boolean
name|ignoreCase
init|=
literal|false
decl_stmt|;
name|String
name|ignoreStr
init|=
name|args
operator|.
name|get
argument_list|(
name|KEEP_IGNORE_CASE
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|ignoreStr
argument_list|)
condition|)
block|{
name|ignoreCase
operator|=
literal|true
expr_stmt|;
block|}
name|keep
operator|=
operator|new
name|CharArraySet
argument_list|(
name|luceneMatchVersion
argument_list|,
literal|10
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
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
name|add
argument_list|(
name|k
operator|.
name|toCharArray
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
name|okPrefix
operator|=
operator|new
name|ArrayList
argument_list|<
name|char
index|[]
argument_list|>
argument_list|()
expr_stmt|;
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
operator|.
name|toCharArray
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
DECL|method|create
specifier|public
name|CapitalizationFilter
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
name|onlyFirstWord
argument_list|,
name|keep
argument_list|,
name|forceFirstLetter
argument_list|,
name|okPrefix
argument_list|,
name|minWordLength
argument_list|,
name|maxWordCount
argument_list|,
name|maxTokenLength
argument_list|)
return|;
block|}
block|}
end_class
end_unit
