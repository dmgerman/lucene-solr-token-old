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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Token
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
name|*
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
name|Hashtable
import|;
end_import
begin_comment
comment|/**  *  * @author Edwin de Jonge  *  * Analyzer for Dutch language. Supports an external list of stopwords (words that  * will not be indexed at all), an external list of exclusions (word that will  * not be stemmed, but indexed) and an external list of word-stem pairs that overrule  * the algorithm (dictionary stemming).  * A default set of stopwords is used unless an alternative list is specified, the  * exclusion list is empty by default.  * As start for the Analyzer the German Analyzer was used. The stemming algorithm  * implemented can be found at @link  */
end_comment
begin_class
DECL|class|DutchAnalyzer
specifier|public
class|class
name|DutchAnalyzer
extends|extends
name|Analyzer
block|{
comment|/** 	 * List of typical Dutch stopwords. 	 */
DECL|field|DUTCH_STOP_WORDS
specifier|private
name|String
index|[]
name|DUTCH_STOP_WORDS
init|=
block|{
literal|"de"
block|,
literal|"en"
block|,
literal|"van"
block|,
literal|"ik"
block|,
literal|"te"
block|,
literal|"dat"
block|,
literal|"die"
block|,
literal|"in"
block|,
literal|"een"
block|,
literal|"hij"
block|,
literal|"het"
block|,
literal|"niet"
block|,
literal|"zijn"
block|,
literal|"is"
block|,
literal|"was"
block|,
literal|"op"
block|,
literal|"aan"
block|,
literal|"met"
block|,
literal|"als"
block|,
literal|"voor"
block|,
literal|"had"
block|,
literal|"er"
block|,
literal|"maar"
block|,
literal|"om"
block|,
literal|"hem"
block|,
literal|"dan"
block|,
literal|"zou"
block|,
literal|"of"
block|,
literal|"wat"
block|,
literal|"mijn"
block|,
literal|"men"
block|,
literal|"dit"
block|,
literal|"zo"
block|,
literal|"door"
block|,
literal|"over"
block|,
literal|"ze"
block|,
literal|"zich"
block|,
literal|"bij"
block|,
literal|"ook"
block|,
literal|"tot"
block|,
literal|"je"
block|,
literal|"mij"
block|,
literal|"uit"
block|,
literal|"der"
block|,
literal|"daar"
block|,
literal|"haar"
block|,
literal|"naar"
block|,
literal|"heb"
block|,
literal|"hoe"
block|,
literal|"heeft"
block|,
literal|"hebben"
block|,
literal|"deze"
block|,
literal|"u"
block|,
literal|"want"
block|,
literal|"nog"
block|,
literal|"zal"
block|,
literal|"me"
block|,
literal|"zij"
block|,
literal|"nu"
block|,
literal|"ge"
block|,
literal|"geen"
block|,
literal|"omdat"
block|,
literal|"iets"
block|,
literal|"worden"
block|,
literal|"toch"
block|,
literal|"al"
block|,
literal|"waren"
block|,
literal|"veel"
block|,
literal|"meer"
block|,
literal|"doen"
block|,
literal|"toen"
block|,
literal|"moet"
block|,
literal|"ben"
block|,
literal|"zonder"
block|,
literal|"kan"
block|,
literal|"hun"
block|,
literal|"dus"
block|,
literal|"alles"
block|,
literal|"onder"
block|,
literal|"ja"
block|,
literal|"eens"
block|,
literal|"hier"
block|,
literal|"wie"
block|,
literal|"werd"
block|,
literal|"altijd"
block|,
literal|"doch"
block|,
literal|"wordt"
block|,
literal|"wezen"
block|,
literal|"kunnen"
block|,
literal|"ons"
block|,
literal|"zelf"
block|,
literal|"tegen"
block|,
literal|"na"
block|,
literal|"reeds"
block|,
literal|"wil"
block|,
literal|"kon"
block|,
literal|"niets"
block|,
literal|"uw"
block|,
literal|"iemand"
block|,
literal|"geweest"
block|,
literal|"andere"
block|}
decl_stmt|;
comment|/** 	 * Contains the stopwords used with the StopFilter. 	 */
DECL|field|stoptable
specifier|private
name|Hashtable
name|stoptable
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/** 	 * Contains words that should be indexed but not stemmed. 	 */
DECL|field|excltable
specifier|private
name|Hashtable
name|excltable
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
DECL|field|_stemdict
specifier|private
name|Hashtable
name|_stemdict
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/** 	 * Builds an analyzer. 	 */
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|()
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopTable
argument_list|(
name|DUTCH_STOP_WORDS
argument_list|)
expr_stmt|;
name|_stemdict
operator|.
name|put
argument_list|(
literal|"fiets"
argument_list|,
literal|"fiets"
argument_list|)
expr_stmt|;
comment|//otherwise fiet
name|_stemdict
operator|.
name|put
argument_list|(
literal|"bromfiets"
argument_list|,
literal|"bromfiets"
argument_list|)
expr_stmt|;
comment|//otherwise bromfiet
name|_stemdict
operator|.
name|put
argument_list|(
literal|"ei"
argument_list|,
literal|"eier"
argument_list|)
expr_stmt|;
name|_stemdict
operator|.
name|put
argument_list|(
literal|"kind"
argument_list|,
literal|"kinder"
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 * 	 * @param stopwords 	 */
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
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
name|makeStopTable
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 * 	 * @param stopwords 	 */
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|Hashtable
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|stopwords
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 * 	 *  @param stopwords 	 */
DECL|method|DutchAnalyzer
specifier|public
name|DutchAnalyzer
parameter_list|(
name|File
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|WordlistLoader
operator|.
name|getWordtable
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from an array of Strings. 	 * 	 * @param exclusionlist 	 */
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
name|makeStopTable
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from a Hashtable. 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|Hashtable
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|exclusionlist
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from the words contained in the given file. 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|File
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|WordlistLoader
operator|.
name|getWordtable
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Reads a stemdictionary file , that overrules the stemming algorithm 	 * This is a textfile that contains per line 	 * word\tstem 	 * i.e: tabseperated 	 */
DECL|method|setStemDictionary
specifier|public
name|void
name|setStemDictionary
parameter_list|(
name|File
name|stemdict
parameter_list|)
block|{
name|_stemdict
operator|=
name|WordlistLoader
operator|.
name|getStemDict
argument_list|(
name|stemdict
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Creates a TokenStream which tokenizes all the text in the provided TextReader. 	 * 	 * @return A TokenStream build from a StandardTokenizer filtered with StandardFilter, StopFilter, GermanStemFilter 	 */
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
name|DutchStemFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|,
name|_stemdict
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
