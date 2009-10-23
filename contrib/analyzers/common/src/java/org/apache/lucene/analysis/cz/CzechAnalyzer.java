begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.cz
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cz
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
name|*
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
name|Collections
import|;
end_import
begin_comment
comment|/**  * {@link Analyzer} for Czech language.   *<p>  * Supports an external list of stopwords (words that  * will not be indexed at all).   * A default set of stopwords is used unless an alternative list is specified.  *</p>  *  *<p><b>NOTE</b>: This class uses the same {@link Version}  * dependent settings as {@link StandardAnalyzer}.</p>  */
end_comment
begin_class
DECL|class|CzechAnalyzer
specifier|public
specifier|final
class|class
name|CzechAnalyzer
extends|extends
name|Analyzer
block|{
comment|/** 	 * List of typical stopwords. 	 */
DECL|field|CZECH_STOP_WORDS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|CZECH_STOP_WORDS
init|=
block|{
literal|"a"
block|,
literal|"s"
block|,
literal|"k"
block|,
literal|"o"
block|,
literal|"i"
block|,
literal|"u"
block|,
literal|"v"
block|,
literal|"z"
block|,
literal|"dnes"
block|,
literal|"cz"
block|,
literal|"t\u00edmto"
block|,
literal|"bude\u0161"
block|,
literal|"budem"
block|,
literal|"byli"
block|,
literal|"jse\u0161"
block|,
literal|"m\u016fj"
block|,
literal|"sv\u00fdm"
block|,
literal|"ta"
block|,
literal|"tomto"
block|,
literal|"tohle"
block|,
literal|"tuto"
block|,
literal|"tyto"
block|,
literal|"jej"
block|,
literal|"zda"
block|,
literal|"pro\u010d"
block|,
literal|"m\u00e1te"
block|,
literal|"tato"
block|,
literal|"kam"
block|,
literal|"tohoto"
block|,
literal|"kdo"
block|,
literal|"kte\u0159\u00ed"
block|,
literal|"mi"
block|,
literal|"n\u00e1m"
block|,
literal|"tom"
block|,
literal|"tomuto"
block|,
literal|"m\u00edt"
block|,
literal|"nic"
block|,
literal|"proto"
block|,
literal|"kterou"
block|,
literal|"byla"
block|,
literal|"toho"
block|,
literal|"proto\u017ee"
block|,
literal|"asi"
block|,
literal|"ho"
block|,
literal|"na\u0161i"
block|,
literal|"napi\u0161te"
block|,
literal|"re"
block|,
literal|"co\u017e"
block|,
literal|"t\u00edm"
block|,
literal|"tak\u017ee"
block|,
literal|"sv\u00fdch"
block|,
literal|"jej\u00ed"
block|,
literal|"sv\u00fdmi"
block|,
literal|"jste"
block|,
literal|"aj"
block|,
literal|"tu"
block|,
literal|"tedy"
block|,
literal|"teto"
block|,
literal|"bylo"
block|,
literal|"kde"
block|,
literal|"ke"
block|,
literal|"prav\u00e9"
block|,
literal|"ji"
block|,
literal|"nad"
block|,
literal|"nejsou"
block|,
literal|"\u010di"
block|,
literal|"pod"
block|,
literal|"t\u00e9ma"
block|,
literal|"mezi"
block|,
literal|"p\u0159es"
block|,
literal|"ty"
block|,
literal|"pak"
block|,
literal|"v\u00e1m"
block|,
literal|"ani"
block|,
literal|"kdy\u017e"
block|,
literal|"v\u0161ak"
block|,
literal|"neg"
block|,
literal|"jsem"
block|,
literal|"tento"
block|,
literal|"\u010dl\u00e1nku"
block|,
literal|"\u010dl\u00e1nky"
block|,
literal|"aby"
block|,
literal|"jsme"
block|,
literal|"p\u0159ed"
block|,
literal|"pta"
block|,
literal|"jejich"
block|,
literal|"byl"
block|,
literal|"je\u0161t\u011b"
block|,
literal|"a\u017e"
block|,
literal|"bez"
block|,
literal|"tak\u00e9"
block|,
literal|"pouze"
block|,
literal|"prvn\u00ed"
block|,
literal|"va\u0161e"
block|,
literal|"kter\u00e1"
block|,
literal|"n\u00e1s"
block|,
literal|"nov\u00fd"
block|,
literal|"tipy"
block|,
literal|"pokud"
block|,
literal|"m\u016f\u017ee"
block|,
literal|"strana"
block|,
literal|"jeho"
block|,
literal|"sv\u00e9"
block|,
literal|"jin\u00e9"
block|,
literal|"zpr\u00e1vy"
block|,
literal|"nov\u00e9"
block|,
literal|"nen\u00ed"
block|,
literal|"v\u00e1s"
block|,
literal|"jen"
block|,
literal|"podle"
block|,
literal|"zde"
block|,
literal|"u\u017e"
block|,
literal|"b\u00fdt"
block|,
literal|"v\u00edce"
block|,
literal|"bude"
block|,
literal|"ji\u017e"
block|,
literal|"ne\u017e"
block|,
literal|"kter\u00fd"
block|,
literal|"by"
block|,
literal|"kter\u00e9"
block|,
literal|"co"
block|,
literal|"nebo"
block|,
literal|"ten"
block|,
literal|"tak"
block|,
literal|"m\u00e1"
block|,
literal|"p\u0159i"
block|,
literal|"od"
block|,
literal|"po"
block|,
literal|"jsou"
block|,
literal|"jak"
block|,
literal|"dal\u0161\u00ed"
block|,
literal|"ale"
block|,
literal|"si"
block|,
literal|"se"
block|,
literal|"ve"
block|,
literal|"to"
block|,
literal|"jako"
block|,
literal|"za"
block|,
literal|"zp\u011bt"
block|,
literal|"ze"
block|,
literal|"do"
block|,
literal|"pro"
block|,
literal|"je"
block|,
literal|"na"
block|,
literal|"atd"
block|,
literal|"atp"
block|,
literal|"jakmile"
block|,
literal|"p\u0159i\u010dem\u017e"
block|,
literal|"j\u00e1"
block|,
literal|"on"
block|,
literal|"ona"
block|,
literal|"ono"
block|,
literal|"oni"
block|,
literal|"ony"
block|,
literal|"my"
block|,
literal|"vy"
block|,
literal|"j\u00ed"
block|,
literal|"ji"
block|,
literal|"m\u011b"
block|,
literal|"mne"
block|,
literal|"jemu"
block|,
literal|"tomu"
block|,
literal|"t\u011bm"
block|,
literal|"t\u011bmu"
block|,
literal|"n\u011bmu"
block|,
literal|"n\u011bmu\u017e"
block|,
literal|"jeho\u017e"
block|,
literal|"j\u00ed\u017e"
block|,
literal|"jeliko\u017e"
block|,
literal|"je\u017e"
block|,
literal|"jako\u017e"
block|,
literal|"na\u010de\u017e"
block|,     }
decl_stmt|;
comment|/** 	 * Contains the stopwords used with the {@link StopFilter}. 	 */
DECL|field|stoptable
specifier|private
name|Set
name|stoptable
decl_stmt|;
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|/** 	 * Builds an analyzer with the default stop words ({@link #CZECH_STOP_WORDS}). 	 */
DECL|method|CzechAnalyzer
specifier|public
name|CzechAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|)
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|CZECH_STOP_WORDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|CzechAnalyzer
specifier|public
name|CzechAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
modifier|...
name|stopwords
parameter_list|)
block|{
name|stoptable
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
DECL|method|CzechAnalyzer
specifier|public
name|CzechAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|HashSet
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|stopwords
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|CzechAnalyzer
specifier|public
name|CzechAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|File
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|stoptable
operator|=
name|WordlistLoader
operator|.
name|getWordSet
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
comment|/**      * Loads stopwords hash from resource stream (file, database...).      * @param   wordfile    File containing the wordlist      * @param   encoding    Encoding used (win-1250, iso-8859-2, ...), null for default system encoding      */
DECL|method|loadStopWords
specifier|public
name|void
name|loadStopWords
parameter_list|(
name|InputStream
name|wordfile
parameter_list|,
name|String
name|encoding
parameter_list|)
block|{
name|setPreviousTokenStream
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// force a new stopfilter to be created
if|if
condition|(
name|wordfile
operator|==
literal|null
condition|)
block|{
name|stoptable
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
return|return;
block|}
try|try
block|{
comment|// clear any previous table (if present)
name|stoptable
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
name|InputStreamReader
name|isr
decl_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
name|isr
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|wordfile
argument_list|)
expr_stmt|;
else|else
name|isr
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|wordfile
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
name|stoptable
operator|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|isr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// clear any previous table (if present)
comment|// TODO: throw IOException
name|stoptable
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** 	 * Creates a {@link TokenStream} which tokenizes all the text in the provided {@link Reader}. 	 * 	 * @return  A {@link TokenStream} built from a {@link StandardTokenizer} filtered with 	 * 			{@link StandardFilter}, {@link LowerCaseFilter}, and {@link StopFilter} 	 */
DECL|method|tokenStream
specifier|public
specifier|final
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
name|matchVersion
argument_list|,
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
name|StopFilter
operator|.
name|getEnablePositionIncrementsVersionDefault
argument_list|(
name|matchVersion
argument_list|)
argument_list|,
name|result
argument_list|,
name|stoptable
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
comment|/**      * Returns a (possibly reused) {@link TokenStream} which tokenizes all the text in       * the provided {@link Reader}.      *      * @return  A {@link TokenStream} built from a {@link StandardTokenizer} filtered with      *          {@link StandardFilter}, {@link LowerCaseFilter}, and {@link StopFilter}      */
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
name|StandardTokenizer
argument_list|(
name|matchVersion
argument_list|,
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
name|stoptable
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
