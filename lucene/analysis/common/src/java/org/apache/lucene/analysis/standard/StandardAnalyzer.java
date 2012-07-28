begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
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
name|core
operator|.
name|StopAnalyzer
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
name|StopwordAnalyzerBase
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
name|WordlistLoader
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
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
begin_comment
comment|/**  * Filters {@link StandardTokenizer} with {@link StandardFilter}, {@link  * LowerCaseFilter} and {@link StopFilter}, using a list of  * English stop words.  *  *<a name="version"/>  *<p>You must specify the required {@link Version}  * compatibility when creating StandardAnalyzer:  *<ul>  *<li> As of 3.4, Hiragana and Han characters are no longer wrongly split  *        from their combining characters. If you use a previous version number,  *        you get the exact broken behavior for backwards compatibility.  *<li> As of 3.1, StandardTokenizer implements Unicode text segmentation,  *        and StopFilter correctly handles Unicode 4.0 supplementary characters  *        in stopwords.  {@link ClassicTokenizer} and {@link ClassicAnalyzer}   *        are the pre-3.1 implementations of StandardTokenizer and  *        StandardAnalyzer.  *<li> As of 2.9, StopFilter preserves position increments  *<li> As of 2.4, Tokens incorrectly identified as acronyms  *        are corrected (see<a href="https://issues.apache.org/jira/browse/LUCENE-1068">LUCENE-1068</a>)  *</ul>  */
end_comment
begin_class
DECL|class|StandardAnalyzer
specifier|public
specifier|final
class|class
name|StandardAnalyzer
extends|extends
name|StopwordAnalyzerBase
block|{
comment|/** Default maximum allowed token length */
DECL|field|DEFAULT_MAX_TOKEN_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_TOKEN_LENGTH
init|=
literal|255
decl_stmt|;
DECL|field|maxTokenLength
specifier|private
name|int
name|maxTokenLength
init|=
name|DEFAULT_MAX_TOKEN_LENGTH
decl_stmt|;
comment|/** An unmodifiable set containing some common English words that are usually not   useful for searching. */
DECL|field|STOP_WORDS_SET
specifier|public
specifier|static
specifier|final
name|CharArraySet
name|STOP_WORDS_SET
init|=
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
decl_stmt|;
comment|/** Builds an analyzer with the given stop words.    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    * @param stopWords stop words */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|CharArraySet
name|stopWords
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the default stop words ({@link    * #STOP_WORDS_SET}).    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|STOP_WORDS_SET
argument_list|)
expr_stmt|;
block|}
comment|/** Builds an analyzer with the stop words from the given reader.    * @see WordlistLoader#getWordSet(Reader, Version)    * @param matchVersion Lucene version to match See {@link    *<a href="#version">above</a>}    * @param stopwords Reader to read stop words from */
DECL|method|StandardAnalyzer
specifier|public
name|StandardAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|loadStopwordSet
argument_list|(
name|stopwords
argument_list|,
name|matchVersion
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set maximum allowed token length.  If a token is seen    * that exceeds this length then it is discarded.  This    * setting only takes effect the next time tokenStream or    * tokenStream is called.    */
DECL|method|setMaxTokenLength
specifier|public
name|void
name|setMaxTokenLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|maxTokenLength
operator|=
name|length
expr_stmt|;
block|}
comment|/**    * @see #setMaxTokenLength    */
DECL|method|getMaxTokenLength
specifier|public
name|int
name|getMaxTokenLength
parameter_list|()
block|{
return|return
name|maxTokenLength
return|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|,
specifier|final
name|Reader
name|reader
parameter_list|)
block|{
specifier|final
name|StandardTokenizer
name|src
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|src
operator|.
name|setMaxTokenLength
argument_list|(
name|maxTokenLength
argument_list|)
expr_stmt|;
name|TokenStream
name|tok
init|=
operator|new
name|StandardFilter
argument_list|(
name|matchVersion
argument_list|,
name|src
argument_list|)
decl_stmt|;
name|tok
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|tok
argument_list|)
expr_stmt|;
name|tok
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|tok
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|src
argument_list|,
name|tok
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|setReader
parameter_list|(
specifier|final
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|src
operator|.
name|setMaxTokenLength
argument_list|(
name|StandardAnalyzer
operator|.
name|this
operator|.
name|maxTokenLength
argument_list|)
expr_stmt|;
name|super
operator|.
name|setReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
