begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
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
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|LowerCaseFilter
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
name|StopFilter
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
name|lucene
operator|.
name|analysis
operator|.
name|Tokenizer
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
name|util
operator|.
name|Version
import|;
end_import
begin_comment
comment|/**  * {@link Analyzer} for Russian language.   *<p>  * Supports an external list of stopwords (words that  * will not be indexed at all).  * A default set of stopwords is used unless an alternative list is specified.  *</p>  */
end_comment
begin_class
DECL|class|RussianAnalyzer
specifier|public
specifier|final
class|class
name|RussianAnalyzer
extends|extends
name|Analyzer
block|{
comment|/**      * List of typical Russian stopwords.      */
DECL|field|RUSSIAN_STOP_WORDS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|RUSSIAN_STOP_WORDS
init|=
block|{
literal|"Ð°"
block|,
literal|"Ð±ÐµÐ·"
block|,
literal|"Ð±Ð¾Ð»ÐµÐµ"
block|,
literal|"Ð±Ñ"
block|,
literal|"Ð±ÑÐ»"
block|,
literal|"Ð±ÑÐ»Ð°"
block|,
literal|"Ð±ÑÐ»Ð¸"
block|,
literal|"Ð±ÑÐ»Ð¾"
block|,
literal|"Ð±ÑÑÑ"
block|,
literal|"Ð²"
block|,
literal|"Ð²Ð°Ð¼"
block|,
literal|"Ð²Ð°Ñ"
block|,
literal|"Ð²ÐµÑÑ"
block|,
literal|"Ð²Ð¾"
block|,
literal|"Ð²Ð¾Ñ"
block|,
literal|"Ð²ÑÐµ"
block|,
literal|"Ð²ÑÐµÐ³Ð¾"
block|,
literal|"Ð²ÑÐµÑ"
block|,
literal|"Ð²Ñ"
block|,
literal|"Ð³Ð´Ðµ"
block|,
literal|"Ð´Ð°"
block|,
literal|"Ð´Ð°Ð¶Ðµ"
block|,
literal|"Ð´Ð»Ñ"
block|,
literal|"Ð´Ð¾"
block|,
literal|"ÐµÐ³Ð¾"
block|,
literal|"ÐµÐµ"
block|,
literal|"ÐµÐ¹"
block|,
literal|"ÐµÑ"
block|,
literal|"ÐµÑÐ»Ð¸"
block|,
literal|"ÐµÑÑÑ"
block|,
literal|"ÐµÑÐµ"
block|,
literal|"Ð¶Ðµ"
block|,
literal|"Ð·Ð°"
block|,
literal|"Ð·Ð´ÐµÑÑ"
block|,
literal|"Ð¸"
block|,
literal|"Ð¸Ð·"
block|,
literal|"Ð¸Ð»Ð¸"
block|,
literal|"Ð¸Ð¼"
block|,
literal|"Ð¸Ñ"
block|,
literal|"Ðº"
block|,
literal|"ÐºÐ°Ðº"
block|,
literal|"ÐºÐ¾"
block|,
literal|"ÐºÐ¾Ð³Ð´Ð°"
block|,
literal|"ÐºÑÐ¾"
block|,
literal|"Ð»Ð¸"
block|,
literal|"Ð»Ð¸Ð±Ð¾"
block|,
literal|"Ð¼Ð½Ðµ"
block|,
literal|"Ð¼Ð¾Ð¶ÐµÑ"
block|,
literal|"Ð¼Ñ"
block|,
literal|"Ð½Ð°"
block|,
literal|"Ð½Ð°Ð´Ð¾"
block|,
literal|"Ð½Ð°Ñ"
block|,
literal|"Ð½Ðµ"
block|,
literal|"Ð½ÐµÐ³Ð¾"
block|,
literal|"Ð½ÐµÐµ"
block|,
literal|"Ð½ÐµÑ"
block|,
literal|"Ð½Ð¸"
block|,
literal|"Ð½Ð¸Ñ"
block|,
literal|"Ð½Ð¾"
block|,
literal|"Ð½Ñ"
block|,
literal|"Ð¾"
block|,
literal|"Ð¾Ð±"
block|,
literal|"Ð¾Ð´Ð½Ð°ÐºÐ¾"
block|,
literal|"Ð¾Ð½"
block|,
literal|"Ð¾Ð½Ð°"
block|,
literal|"Ð¾Ð½Ð¸"
block|,
literal|"Ð¾Ð½Ð¾"
block|,
literal|"Ð¾Ñ"
block|,
literal|"Ð¾ÑÐµÐ½Ñ"
block|,
literal|"Ð¿Ð¾"
block|,
literal|"Ð¿Ð¾Ð´"
block|,
literal|"Ð¿ÑÐ¸"
block|,
literal|"Ñ"
block|,
literal|"ÑÐ¾"
block|,
literal|"ÑÐ°Ðº"
block|,
literal|"ÑÐ°ÐºÐ¶Ðµ"
block|,
literal|"ÑÐ°ÐºÐ¾Ð¹"
block|,
literal|"ÑÐ°Ð¼"
block|,
literal|"ÑÐµ"
block|,
literal|"ÑÐµÐ¼"
block|,
literal|"ÑÐ¾"
block|,
literal|"ÑÐ¾Ð³Ð¾"
block|,
literal|"ÑÐ¾Ð¶Ðµ"
block|,
literal|"ÑÐ¾Ð¹"
block|,
literal|"ÑÐ¾Ð»ÑÐºÐ¾"
block|,
literal|"ÑÐ¾Ð¼"
block|,
literal|"ÑÑ"
block|,
literal|"Ñ"
block|,
literal|"ÑÐ¶Ðµ"
block|,
literal|"ÑÐ¾ÑÑ"
block|,
literal|"ÑÐµÐ³Ð¾"
block|,
literal|"ÑÐµÐ¹"
block|,
literal|"ÑÐµÐ¼"
block|,
literal|"ÑÑÐ¾"
block|,
literal|"ÑÑÐ¾Ð±Ñ"
block|,
literal|"ÑÑÐµ"
block|,
literal|"ÑÑÑ"
block|,
literal|"ÑÑÐ°"
block|,
literal|"ÑÑÐ¸"
block|,
literal|"ÑÑÐ¾"
block|,
literal|"Ñ"
block|}
decl_stmt|;
comment|/**      * Contains the stopwords used with the StopFilter.      */
DECL|field|stopSet
specifier|private
name|Set
name|stopSet
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|RUSSIAN_STOP_WORDS
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds an analyzer with the given stop words.      */
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
modifier|...
name|stopwords
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|stopSet
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**      * Builds an analyzer with the given stop words.      * TODO: create a Set version of this ctor      */
DECL|method|RussianAnalyzer
specifier|public
name|RussianAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Map
name|stopwords
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|stopSet
operator|=
operator|new
name|HashSet
argument_list|(
name|stopwords
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**      * Creates a {@link TokenStream} which tokenizes all the text in the       * provided {@link Reader}.      *      * @return  A {@link TokenStream} built from a       *   {@link RussianLetterTokenizer} filtered with       *   {@link RussianLowerCaseFilter}, {@link StopFilter},       *   and {@link RussianStemFilter}      */
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|result
init|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|result
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|RussianStemFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|SavedStreams
specifier|private
class|class
name|SavedStreams
block|{
DECL|field|source
name|Tokenizer
name|source
decl_stmt|;
DECL|field|result
name|TokenStream
name|result
decl_stmt|;
block|}
empty_stmt|;
comment|/**      * Returns a (possibly reused) {@link TokenStream} which tokenizes all the text       * in the provided {@link Reader}.      *      * @return  A {@link TokenStream} built from a       *   {@link RussianLetterTokenizer} filtered with       *   {@link RussianLowerCaseFilter}, {@link StopFilter},       *   and {@link RussianStemFilter}      */
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SavedStreams
name|streams
init|=
operator|(
name|SavedStreams
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|streams
operator|==
literal|null
condition|)
block|{
name|streams
operator|=
operator|new
name|SavedStreams
argument_list|()
expr_stmt|;
name|streams
operator|.
name|source
operator|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|streams
operator|.
name|source
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|streams
operator|.
name|result
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|RussianStemFilter
argument_list|(
name|streams
operator|.
name|result
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|streams
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|streams
operator|.
name|source
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|streams
operator|.
name|result
return|;
block|}
block|}
end_class
end_unit
