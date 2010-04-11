begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.nl
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|nl
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
name|KeywordMarkerFilter
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
name|ReusableAnalyzerBase
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
name|miscellaneous
operator|.
name|StemmerOverrideFilter
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
name|snowball
operator|.
name|SnowballFilter
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
name|StandardAnalyzer
import|;
end_import
begin_comment
comment|// for javadoc
end_comment
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
name|HashMap
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
name|Set
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
comment|/**  * {@link Analyzer} for Dutch language.   *<p>  * Supports an external list of stopwords (words that  * will not be indexed at all), an external list of exclusions (word that will  * not be stemmed, but indexed) and an external list of word-stem pairs that overrule  * the algorithm (dictionary stemming).  * A default set of stopwords is used unless an alternative list is specified, but the  * exclusion list is empty by default.  *</p>  *  *<a name="version"/>  *<p>You must specify the required {@link Version}  * compatibility when creating DutchAnalyzer:  *<ul>  *<li> As of 3.1, Snowball stemming is done with SnowballFilter,   *        LowerCaseFilter is used prior to StopFilter, and Snowball   *        stopwords are used by default.  *<li> As of 2.9, StopFilter preserves position  *        increments  *</ul>  *   *<p><b>NOTE</b>: This class uses the same {@link Version}  * dependent settings as {@link StandardAnalyzer}.</p>  */
end_comment
begin_class
DECL|class|DutchAnalyzer
specifier|public
specifier|final
class|class
name|DutchAnalyzer
extends|extends
name|ReusableAnalyzerBase
block|{
comment|/**    * List of typical Dutch stopwords.    * @deprecated use {@link #getDefaultStopSet()} instead    */
annotation|@
name|Deprecated
DECL|field|DUTCH_STOP_WORDS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|DUTCH_STOP_WORDS
init|=
name|getDefaultStopSet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
comment|/** File containing default Dutch stopwords. */
DECL|field|DEFAULT_STOPWORD_FILE
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_STOPWORD_FILE
init|=
literal|"dutch_stop.txt"
decl_stmt|;
comment|/**    * Returns an unmodifiable instance of the default stop-words set.    * @return an unmodifiable instance of the default stop-words set.    */
DECL|method|getDefaultStopSet
specifier|public
specifier|static
name|Set
argument_list|<
name|?
argument_list|>
name|getDefaultStopSet
parameter_list|()
block|{
return|return
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET
return|;
block|}
DECL|class|DefaultSetHolder
specifier|private
specifier|static
class|class
name|DefaultSetHolder
block|{
DECL|field|DEFAULT_STOP_SET
specifier|static
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|DEFAULT_STOP_SET
decl_stmt|;
static|static
block|{
try|try
block|{
name|DEFAULT_STOP_SET
operator|=
name|WordlistLoader
operator|.
name|getSnowballWordSet
argument_list|(
name|SnowballFilter
operator|.
name|class
argument_list|,
name|DEFAULT_STOPWORD_FILE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// default set should always be present as it is part of the
comment|// distribution (JAR)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to load default stopword set"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Contains the stopwords used with the StopFilter.    */
DECL|field|stoptable
specifier|private
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|stoptable
decl_stmt|;
comment|/**    * Contains words that should be indexed but not stemmed.    */
DECL|field|excltable
specifier|private
name|Set
argument_list|<
name|?
argument_list|>
name|excltable
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
DECL|field|stemdict
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|stemdict
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
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/**    * Builds an analyzer with the default stop words ({@link #DUTCH_STOP_WORDS})     * and a few default entries for the stem exclusion table.    *     */
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|DefaultSetHolder
operator|.
name|DEFAULT_STOP_SET
argument_list|)
expr_stmt|;
name|stemdict
operator|.
name|put
argument_list|(
literal|"fiets"
argument_list|,
literal|"fiets"
argument_list|)
expr_stmt|;
comment|//otherwise fiet
name|stemdict
operator|.
name|put
argument_list|(
literal|"bromfiets"
argument_list|,
literal|"bromfiets"
argument_list|)
expr_stmt|;
comment|//otherwise bromfiet
name|stemdict
operator|.
name|put
argument_list|(
literal|"ei"
argument_list|,
literal|"eier"
argument_list|)
expr_stmt|;
name|stemdict
operator|.
name|put
argument_list|(
literal|"kind"
argument_list|,
literal|"kinder"
argument_list|)
expr_stmt|;
block|}
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
expr_stmt|;
block|}
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stopwords
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|stemExclusionTable
parameter_list|)
block|{
name|stoptable
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|CharArraySet
operator|.
name|copy
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
name|excltable
operator|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
name|CharArraySet
operator|.
name|copy
argument_list|(
name|matchVersion
argument_list|,
name|stemExclusionTable
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    *    * @param matchVersion    * @param stopwords    * @deprecated use {@link #DutchAnalyzer(Version, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
modifier|...
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|matchVersion
argument_list|,
name|stopwords
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    *    * @param stopwords    * @deprecated use {@link #DutchAnalyzer(Version, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|HashSet
argument_list|<
name|?
argument_list|>
name|stopwords
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
operator|(
name|Set
argument_list|<
name|?
argument_list|>
operator|)
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    *    * @param stopwords    * @deprecated use {@link #DutchAnalyzer(Version, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|File
name|stopwords
parameter_list|)
block|{
comment|// this is completely broken!
try|try
block|{
name|stoptable
operator|=
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: throw IOException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Builds an exclusionlist from an array of Strings.    *    * @param exclusionlist    * @deprecated use {@link #DutchAnalyzer(Version, Set, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|String
modifier|...
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|matchVersion
argument_list|,
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
comment|/**    * Builds an exclusionlist from a Hashtable.    * @deprecated use {@link #DutchAnalyzer(Version, Set, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|HashSet
argument_list|<
name|?
argument_list|>
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|exclusionlist
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
comment|/**    * Builds an exclusionlist from the words contained in the given file.    * @deprecated use {@link #DutchAnalyzer(Version, Set, Set)} instead    */
annotation|@
name|Deprecated
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|File
name|exclusionlist
parameter_list|)
block|{
try|try
block|{
name|excltable
operator|=
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: throw IOException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Reads a stemdictionary file , that overrules the stemming algorithm    * This is a textfile that contains per line    *<tt>word<b>\t</b>stem</tt>, i.e: two tab seperated words    */
DECL|method|setStemDictionary
specifier|public
name|void
name|setStemDictionary
parameter_list|(
name|File
name|stemdictFile
parameter_list|)
block|{
try|try
block|{
name|stemdict
operator|=
name|WordlistLoader
operator|.
name|getStemDict
argument_list|(
name|stemdictFile
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stemmer to be created
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: throw IOException
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns a (possibly reused) {@link TokenStream} which tokenizes all the     * text in the provided {@link Reader}.    *    * @return A {@link TokenStream} built from a {@link StandardTokenizer}    *   filtered with {@link StandardFilter}, {@link LowerCaseFilter},     *   {@link StopFilter}, {@link KeywordMarkerFilter} if a stem exclusion set is provided,    *   {@link StemmerOverrideFilter}, and {@link SnowballFilter}    */
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|aReader
parameter_list|)
block|{
if|if
condition|(
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
condition|)
block|{
specifier|final
name|Tokenizer
name|source
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|aReader
argument_list|)
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|StandardFilter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|excltable
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
operator|new
name|KeywordMarkerFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|stemdict
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
operator|new
name|StemmerOverrideFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|,
name|stemdict
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|SnowballFilter
argument_list|(
name|result
argument_list|,
operator|new
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|DutchStemmer
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
name|result
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|Tokenizer
name|source
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
name|aReader
argument_list|)
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|StandardFilter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|excltable
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
operator|new
name|KeywordMarkerFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|DutchStemFilter
argument_list|(
name|result
argument_list|,
name|stemdict
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
name|result
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
