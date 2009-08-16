begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.fr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fr
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
begin_comment
comment|/**  * Analyzer for French language. Supports an external list of stopwords (words that  * will not be indexed at all) and an external list of exclusions (word that will  * not be stemmed, but indexed).  * A default set of stopwords is used unless an alternative list is specified, the  * exclusion list is empty by default.  *  *  * @version $Id$  */
end_comment
begin_class
DECL|class|FrenchAnalyzer
specifier|public
specifier|final
class|class
name|FrenchAnalyzer
extends|extends
name|Analyzer
block|{
comment|/**    * Extended list of typical French stopwords.    */
DECL|field|FRENCH_STOP_WORDS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|FRENCH_STOP_WORDS
init|=
block|{
literal|"a"
block|,
literal|"afin"
block|,
literal|"ai"
block|,
literal|"ainsi"
block|,
literal|"aprÃ¨s"
block|,
literal|"attendu"
block|,
literal|"au"
block|,
literal|"aujourd"
block|,
literal|"auquel"
block|,
literal|"aussi"
block|,
literal|"autre"
block|,
literal|"autres"
block|,
literal|"aux"
block|,
literal|"auxquelles"
block|,
literal|"auxquels"
block|,
literal|"avait"
block|,
literal|"avant"
block|,
literal|"avec"
block|,
literal|"avoir"
block|,
literal|"c"
block|,
literal|"car"
block|,
literal|"ce"
block|,
literal|"ceci"
block|,
literal|"cela"
block|,
literal|"celle"
block|,
literal|"celles"
block|,
literal|"celui"
block|,
literal|"cependant"
block|,
literal|"certain"
block|,
literal|"certaine"
block|,
literal|"certaines"
block|,
literal|"certains"
block|,
literal|"ces"
block|,
literal|"cet"
block|,
literal|"cette"
block|,
literal|"ceux"
block|,
literal|"chez"
block|,
literal|"ci"
block|,
literal|"combien"
block|,
literal|"comme"
block|,
literal|"comment"
block|,
literal|"concernant"
block|,
literal|"contre"
block|,
literal|"d"
block|,
literal|"dans"
block|,
literal|"de"
block|,
literal|"debout"
block|,
literal|"dedans"
block|,
literal|"dehors"
block|,
literal|"delÃ "
block|,
literal|"depuis"
block|,
literal|"derriÃ¨re"
block|,
literal|"des"
block|,
literal|"dÃ©sormais"
block|,
literal|"desquelles"
block|,
literal|"desquels"
block|,
literal|"dessous"
block|,
literal|"dessus"
block|,
literal|"devant"
block|,
literal|"devers"
block|,
literal|"devra"
block|,
literal|"divers"
block|,
literal|"diverse"
block|,
literal|"diverses"
block|,
literal|"doit"
block|,
literal|"donc"
block|,
literal|"dont"
block|,
literal|"du"
block|,
literal|"duquel"
block|,
literal|"durant"
block|,
literal|"dÃ¨s"
block|,
literal|"elle"
block|,
literal|"elles"
block|,
literal|"en"
block|,
literal|"entre"
block|,
literal|"environ"
block|,
literal|"est"
block|,
literal|"et"
block|,
literal|"etc"
block|,
literal|"etre"
block|,
literal|"eu"
block|,
literal|"eux"
block|,
literal|"exceptÃ©"
block|,
literal|"hormis"
block|,
literal|"hors"
block|,
literal|"hÃ©las"
block|,
literal|"hui"
block|,
literal|"il"
block|,
literal|"ils"
block|,
literal|"j"
block|,
literal|"je"
block|,
literal|"jusqu"
block|,
literal|"jusque"
block|,
literal|"l"
block|,
literal|"la"
block|,
literal|"laquelle"
block|,
literal|"le"
block|,
literal|"lequel"
block|,
literal|"les"
block|,
literal|"lesquelles"
block|,
literal|"lesquels"
block|,
literal|"leur"
block|,
literal|"leurs"
block|,
literal|"lorsque"
block|,
literal|"lui"
block|,
literal|"lÃ "
block|,
literal|"ma"
block|,
literal|"mais"
block|,
literal|"malgrÃ©"
block|,
literal|"me"
block|,
literal|"merci"
block|,
literal|"mes"
block|,
literal|"mien"
block|,
literal|"mienne"
block|,
literal|"miennes"
block|,
literal|"miens"
block|,
literal|"moi"
block|,
literal|"moins"
block|,
literal|"mon"
block|,
literal|"moyennant"
block|,
literal|"mÃªme"
block|,
literal|"mÃªmes"
block|,
literal|"n"
block|,
literal|"ne"
block|,
literal|"ni"
block|,
literal|"non"
block|,
literal|"nos"
block|,
literal|"notre"
block|,
literal|"nous"
block|,
literal|"nÃ©anmoins"
block|,
literal|"nÃ´tre"
block|,
literal|"nÃ´tres"
block|,
literal|"on"
block|,
literal|"ont"
block|,
literal|"ou"
block|,
literal|"outre"
block|,
literal|"oÃ¹"
block|,
literal|"par"
block|,
literal|"parmi"
block|,
literal|"partant"
block|,
literal|"pas"
block|,
literal|"passÃ©"
block|,
literal|"pendant"
block|,
literal|"plein"
block|,
literal|"plus"
block|,
literal|"plusieurs"
block|,
literal|"pour"
block|,
literal|"pourquoi"
block|,
literal|"proche"
block|,
literal|"prÃ¨s"
block|,
literal|"puisque"
block|,
literal|"qu"
block|,
literal|"quand"
block|,
literal|"que"
block|,
literal|"quel"
block|,
literal|"quelle"
block|,
literal|"quelles"
block|,
literal|"quels"
block|,
literal|"qui"
block|,
literal|"quoi"
block|,
literal|"quoique"
block|,
literal|"revoici"
block|,
literal|"revoilÃ "
block|,
literal|"s"
block|,
literal|"sa"
block|,
literal|"sans"
block|,
literal|"sauf"
block|,
literal|"se"
block|,
literal|"selon"
block|,
literal|"seront"
block|,
literal|"ses"
block|,
literal|"si"
block|,
literal|"sien"
block|,
literal|"sienne"
block|,
literal|"siennes"
block|,
literal|"siens"
block|,
literal|"sinon"
block|,
literal|"soi"
block|,
literal|"soit"
block|,
literal|"son"
block|,
literal|"sont"
block|,
literal|"sous"
block|,
literal|"suivant"
block|,
literal|"sur"
block|,
literal|"ta"
block|,
literal|"te"
block|,
literal|"tes"
block|,
literal|"tien"
block|,
literal|"tienne"
block|,
literal|"tiennes"
block|,
literal|"tiens"
block|,
literal|"toi"
block|,
literal|"ton"
block|,
literal|"tous"
block|,
literal|"tout"
block|,
literal|"toute"
block|,
literal|"toutes"
block|,
literal|"tu"
block|,
literal|"un"
block|,
literal|"une"
block|,
literal|"va"
block|,
literal|"vers"
block|,
literal|"voici"
block|,
literal|"voilÃ "
block|,
literal|"vos"
block|,
literal|"votre"
block|,
literal|"vous"
block|,
literal|"vu"
block|,
literal|"vÃ´tre"
block|,
literal|"vÃ´tres"
block|,
literal|"y"
block|,
literal|"Ã "
block|,
literal|"Ã§a"
block|,
literal|"Ã¨s"
block|,
literal|"Ã©tÃ©"
block|,
literal|"Ãªtre"
block|,
literal|"Ã´"
block|}
decl_stmt|;
comment|/**    * Contains the stopwords used with the StopFilter.    */
DECL|field|stoptable
specifier|private
name|Set
name|stoptable
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|/**    * Contains words that should be indexed but not stemmed.    */
DECL|field|excltable
specifier|private
name|Set
name|excltable
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|/**    * Builds an analyzer with the default stop words ({@link #FRENCH_STOP_WORDS}).    */
DECL|method|FrenchAnalyzer
specifier|public
name|FrenchAnalyzer
parameter_list|()
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|FRENCH_STOP_WORDS
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an analyzer with the given stop words.    */
DECL|method|FrenchAnalyzer
specifier|public
name|FrenchAnalyzer
parameter_list|(
name|String
index|[]
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
block|}
comment|/**    * Builds an analyzer with the given stop words.    * @throws IOException    */
DECL|method|FrenchAnalyzer
specifier|public
name|FrenchAnalyzer
parameter_list|(
name|File
name|stopwords
parameter_list|)
throws|throws
name|IOException
block|{
name|stoptable
operator|=
operator|new
name|HashSet
argument_list|(
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|stopwords
argument_list|)
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
name|excltable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builds an exclusionlist from a Map.    */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|Map
name|exclusionlist
parameter_list|)
block|{
name|excltable
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
block|}
comment|/**    * Builds an exclusionlist from the words contained in the given file.    * @throws IOException    */
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
name|excltable
operator|=
operator|new
name|HashSet
argument_list|(
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
name|exclusionlist
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a TokenStream which tokenizes all the text in the provided Reader.    *    * @return A TokenStream built from a StandardTokenizer filtered with    *         StandardFilter, StopFilter, FrenchStemFilter and LowerCaseFilter    */
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
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fieldName must not be null"
argument_list|)
throw|;
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"reader must not be null"
argument_list|)
throw|;
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
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|FrenchStemFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
comment|// Convert to lowercase after stemming!
name|result
operator|=
operator|new
name|LowerCaseFilter
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
comment|/**    * Returns a (possibly reused) TokenStream which tokenizes all the text     * in the provided Reader.    *    * @return A TokenStream built from a StandardTokenizer filtered with    *         StandardFilter, StopFilter, FrenchStemFilter and LowerCaseFilter    */
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
name|StopFilter
argument_list|(
name|streams
operator|.
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|streams
operator|.
name|result
operator|=
operator|new
name|FrenchStemFilter
argument_list|(
name|streams
operator|.
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
comment|// Convert to lowercase after stemming!
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
