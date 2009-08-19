begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.de
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|de
package|;
end_package
begin_comment
comment|// This file is encoded in UTF-8
end_comment
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|analysis
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
name|analysis
operator|.
name|standard
operator|.
name|StandardFilter
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
name|standard
operator|.
name|StandardTokenizer
import|;
end_import
begin_comment
comment|/**  * {@link Analyzer} for German language.   *<p>  * Supports an external list of stopwords (words that  * will not be indexed at all) and an external list of exclusions (word that will  * not be stemmed, but indexed).  * A default set of stopwords is used unless an alternative list is specified, but the  * exclusion list is empty by default.  *</p>  *   * @version $Id$  */
end_comment
begin_class
DECL|class|GermanAnalyzer
specifier|public
class|class
name|GermanAnalyzer
extends|extends
name|Analyzer
block|{
comment|/**    * List of typical german stopwords.    */
DECL|field|GERMAN_STOP_WORDS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|GERMAN_STOP_WORDS
init|=
block|{
literal|"einer"
block|,
literal|"eine"
block|,
literal|"eines"
block|,
literal|"einem"
block|,
literal|"einen"
block|,
literal|"der"
block|,
literal|"die"
block|,
literal|"das"
block|,
literal|"dass"
block|,
literal|"daÃ"
block|,
literal|"du"
block|,
literal|"er"
block|,
literal|"sie"
block|,
literal|"es"
block|,
literal|"was"
block|,
literal|"wer"
block|,
literal|"wie"
block|,
literal|"wir"
block|,
literal|"und"
block|,
literal|"oder"
block|,
literal|"ohne"
block|,
literal|"mit"
block|,
literal|"am"
block|,
literal|"im"
block|,
literal|"in"
block|,
literal|"aus"
block|,
literal|"auf"
block|,
literal|"ist"
block|,
literal|"sein"
block|,
literal|"war"
block|,
literal|"wird"
block|,
literal|"ihr"
block|,
literal|"ihre"
block|,
literal|"ihres"
block|,
literal|"als"
block|,
literal|"fÃ¼r"
block|,
literal|"von"
block|,
literal|"mit"
block|,
literal|"dich"
block|,
literal|"dir"
block|,
literal|"mich"
block|,
literal|"mir"
block|,
literal|"mein"
block|,
literal|"sein"
block|,
literal|"kein"
block|,
literal|"durch"
block|,
literal|"wegen"
block|,
literal|"wird"
block|}
decl_stmt|;
comment|/**    * Contains the stopwords used with the {@link StopFilter}.    */
DECL|field|stopSet
specifier|private
name|Set
name|stopSet
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|/**    * Contains words that should be indexed but not stemmed.    */
DECL|field|exclusionSet
specifier|private
name|Set
name|exclusionSet
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|/**    * Builds an analyzer with the default stop words:    * {@link #GERMAN_STOP_WORDS}.    */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|()
block|{
name|stopSet
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|GERMAN_STOP_WORDS
argument_list|)
expr_stmt|;
name|setOverridesTokenStreamMethod
argument_list|(
name|GermanAnalyzer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|String
index|[]
name|stopwords
parameter_list|)
block|{
name|stopSet
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
name|setOverridesTokenStreamMethod
argument_list|(
name|GermanAnalyzer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|Map
name|stopwords
parameter_list|)
block|{
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
name|setOverridesTokenStreamMethod
argument_list|(
name|GermanAnalyzer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|File
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|stopSet
operator|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
name|setOverridesTokenStreamMethod
argument_list|(
name|GermanAnalyzer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an exclusionlist from an array of Strings.    */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|String
index|[]
name|exclusionlist
parameter_list|)
block|{
name|exclusionSet
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
comment|/**    * Builds an exclusionlist from a {@link Map}    */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|Map
name|exclusionlist
parameter_list|)
block|{
name|exclusionSet
operator|=
operator|new
name|HashSet
argument_list|(
name|exclusionlist
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
comment|/**    * Builds an exclusionlist from the words contained in the given file.    */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|File
name|exclusionlist
parameter_list|)
throws|throws
name|IOException
block|{
name|exclusionSet
operator|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
comment|/**    * Creates a {@link TokenStream} which tokenizes all the text in the provided {@link Reader}.    *    * @return A {@link TokenStream} built from a {@link StandardTokenizer} filtered with    *         {@link StandardFilter}, {@link LowerCaseFilter}, {@link StopFilter}, and    *         {@link GermanStemFilter}    */
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
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StandardFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
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
name|result
argument_list|,
name|stopSet
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|GermanStemFilter
argument_list|(
name|result
argument_list|,
name|exclusionSet
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
comment|/**    * Returns a (possibly reused) {@link TokenStream} which tokenizes all the text     * in the provided {@link Reader}.    *    * @return A {@link TokenStream} built from a {@link StandardTokenizer} filtered with    *         {@link StandardFilter}, {@link LowerCaseFilter}, {@link StopFilter}, and    *         {@link GermanStemFilter}    */
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
if|if
condition|(
name|overridesTokenStreamMethod
condition|)
block|{
comment|// LUCENE-1678: force fallback to tokenStream() if we
comment|// have been subclassed and that subclass overrides
comment|// tokenStream but not reusableTokenStream
return|return
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|reader
argument_list|)
return|;
block|}
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
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|StandardFilter
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
name|LowerCaseFilter
argument_list|(
name|streams
operator|.
name|result
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
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
name|GermanStemFilter
argument_list|(
name|streams
operator|.
name|result
argument_list|,
name|exclusionSet
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
