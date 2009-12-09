begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.cjk
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cjk
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
name|Arrays
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
begin_comment
comment|/**  * An {@link Analyzer} that tokenizes text with {@link CJKTokenizer} and  * filters with {@link StopFilter}  *  */
end_comment
begin_class
DECL|class|CJKAnalyzer
specifier|public
specifier|final
class|class
name|CJKAnalyzer
extends|extends
name|Analyzer
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * An array containing some common English words that are not usually    * useful for searching and some double-byte interpunctions.    * @deprecated use {@link #getDefaultStopSet()} instead    */
comment|// TODO make this final in 3.1 -
comment|// this might be revised and merged with StopFilter stop words too
DECL|field|STOP_WORDS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|STOP_WORDS
init|=
block|{
literal|"a"
block|,
literal|"and"
block|,
literal|"are"
block|,
literal|"as"
block|,
literal|"at"
block|,
literal|"be"
block|,
literal|"but"
block|,
literal|"by"
block|,
literal|"for"
block|,
literal|"if"
block|,
literal|"in"
block|,
literal|"into"
block|,
literal|"is"
block|,
literal|"it"
block|,
literal|"no"
block|,
literal|"not"
block|,
literal|"of"
block|,
literal|"on"
block|,
literal|"or"
block|,
literal|"s"
block|,
literal|"such"
block|,
literal|"t"
block|,
literal|"that"
block|,
literal|"the"
block|,
literal|"their"
block|,
literal|"then"
block|,
literal|"there"
block|,
literal|"these"
block|,
literal|"they"
block|,
literal|"this"
block|,
literal|"to"
block|,
literal|"was"
block|,
literal|"will"
block|,
literal|"with"
block|,
literal|""
block|,
literal|"www"
block|}
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
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
init|=
name|CharArraySet
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|STOP_WORDS
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
block|}
comment|/**    * stop word list    */
DECL|field|stopTable
specifier|private
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|stopTable
decl_stmt|;
DECL|field|matchVersion
specifier|private
specifier|final
name|Version
name|matchVersion
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Builds an analyzer which removes words in {@link #STOP_WORDS}.    */
DECL|method|CJKAnalyzer
specifier|public
name|CJKAnalyzer
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
block|}
comment|/**    * Builds an analyzer with the given stop words    *     * @param matchVersion    *          lucene compatibility version    * @param stopwords    *          a stopword set    */
DECL|method|CJKAnalyzer
specifier|public
name|CJKAnalyzer
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
name|stopTable
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
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|/**    * Builds an analyzer which removes words in the provided array.    *    * @param stopWords stop word array    * @deprecated use {@link #CJKAnalyzer(Version, Set)} instead    */
DECL|method|CJKAnalyzer
specifier|public
name|CJKAnalyzer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|String
modifier|...
name|stopWords
parameter_list|)
block|{
name|stopTable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|matchVersion
argument_list|,
name|stopWords
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Creates a {@link TokenStream} which tokenizes all the text in the provided {@link Reader}.    *    * @param fieldName lucene field name    * @param reader    input {@link Reader}    * @return A {@link TokenStream} built from {@link CJKTokenizer}, filtered with    *    {@link StopFilter}    */
annotation|@
name|Override
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
return|return
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
operator|new
name|CJKTokenizer
argument_list|(
name|reader
argument_list|)
argument_list|,
name|stopTable
argument_list|)
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
comment|/**    * Returns a (possibly reused) {@link TokenStream} which tokenizes all the text     * in the provided {@link Reader}.    *    * @param fieldName lucene field name    * @param reader    Input {@link Reader}    * @return A {@link TokenStream} built from {@link CJKTokenizer}, filtered with    *    {@link StopFilter}    */
annotation|@
name|Override
DECL|method|reusableTokenStream
specifier|public
specifier|final
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
comment|/* tokenStream() is final, no back compat issue */
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
name|CJKTokenizer
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|matchVersion
argument_list|,
name|streams
operator|.
name|source
argument_list|,
name|stopTable
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
