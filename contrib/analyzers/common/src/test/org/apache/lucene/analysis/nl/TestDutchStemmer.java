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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|BaseTokenStreamTestCase
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
name|util
operator|.
name|Version
import|;
end_import
begin_comment
comment|/**  * Test the Dutch Stem Filter, which only modifies the term text.  *   * The code states that it uses the snowball algorithm, but tests reveal some differences.  *   */
end_comment
begin_class
DECL|class|TestDutchStemmer
specifier|public
class|class
name|TestDutchStemmer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|dataDir
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"dataDir"
argument_list|,
literal|"./bin"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|customDictFile
name|File
name|customDictFile
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"org/apache/lucene/analysis/nl/customStemDict.txt"
argument_list|)
decl_stmt|;
DECL|method|testWithSnowballExamples
specifier|public
name|void
name|testWithSnowballExamples
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
literal|"lichaamsziek"
argument_list|,
literal|"lichaamsziek"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichamelijk"
argument_list|,
literal|"licham"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichamelijke"
argument_list|,
literal|"licham"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichamelijkheden"
argument_list|,
literal|"licham"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichamen"
argument_list|,
literal|"licham"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichere"
argument_list|,
literal|"licher"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"licht"
argument_list|,
literal|"licht"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtbeeld"
argument_list|,
literal|"lichtbeeld"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtbruin"
argument_list|,
literal|"lichtbruin"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtdoorlatende"
argument_list|,
literal|"lichtdoorlat"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichte"
argument_list|,
literal|"licht"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichten"
argument_list|,
literal|"licht"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtende"
argument_list|,
literal|"lichtend"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtenvoorde"
argument_list|,
literal|"lichtenvoord"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichter"
argument_list|,
literal|"lichter"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtere"
argument_list|,
literal|"lichter"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichters"
argument_list|,
literal|"lichter"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtgevoeligheid"
argument_list|,
literal|"lichtgevoel"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtgewicht"
argument_list|,
literal|"lichtgewicht"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtgrijs"
argument_list|,
literal|"lichtgrijs"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichthoeveelheid"
argument_list|,
literal|"lichthoevel"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtintensiteit"
argument_list|,
literal|"lichtintensiteit"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtje"
argument_list|,
literal|"lichtj"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtjes"
argument_list|,
literal|"lichtjes"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtkranten"
argument_list|,
literal|"lichtkrant"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtkring"
argument_list|,
literal|"lichtkring"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtkringen"
argument_list|,
literal|"lichtkring"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtregelsystemen"
argument_list|,
literal|"lichtregelsystem"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtste"
argument_list|,
literal|"lichtst"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtstromende"
argument_list|,
literal|"lichtstrom"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtte"
argument_list|,
literal|"licht"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtten"
argument_list|,
literal|"licht"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichttoetreding"
argument_list|,
literal|"lichttoetred"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtverontreinigde"
argument_list|,
literal|"lichtverontreinigd"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lichtzinnige"
argument_list|,
literal|"lichtzinn"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lid"
argument_list|,
literal|"lid"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lidia"
argument_list|,
literal|"lidia"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lidmaatschap"
argument_list|,
literal|"lidmaatschap"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lidstaten"
argument_list|,
literal|"lidstat"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"lidvereniging"
argument_list|,
literal|"lidveren"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opgingen"
argument_list|,
literal|"opging"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opglanzing"
argument_list|,
literal|"opglanz"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opglanzingen"
argument_list|,
literal|"opglanz"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opglimlachten"
argument_list|,
literal|"opglimlacht"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opglimpen"
argument_list|,
literal|"opglimp"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opglimpende"
argument_list|,
literal|"opglimp"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opglimping"
argument_list|,
literal|"opglimp"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opglimpingen"
argument_list|,
literal|"opglimp"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opgraven"
argument_list|,
literal|"opgrav"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opgrijnzen"
argument_list|,
literal|"opgrijnz"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opgrijzende"
argument_list|,
literal|"opgrijz"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opgroeien"
argument_list|,
literal|"opgroei"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opgroeiende"
argument_list|,
literal|"opgroei"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opgroeiplaats"
argument_list|,
literal|"opgroeiplat"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophaal"
argument_list|,
literal|"ophal"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophaaldienst"
argument_list|,
literal|"ophaaldienst"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophaalkosten"
argument_list|,
literal|"ophaalkost"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophaalsystemen"
argument_list|,
literal|"ophaalsystem"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophaalt"
argument_list|,
literal|"ophaalt"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophaaltruck"
argument_list|,
literal|"ophaaltruck"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophalen"
argument_list|,
literal|"ophal"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophalend"
argument_list|,
literal|"ophal"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophalers"
argument_list|,
literal|"ophaler"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophef"
argument_list|,
literal|"ophef"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opheldering"
argument_list|,
literal|"ophelder"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophemelde"
argument_list|,
literal|"ophemeld"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophemelen"
argument_list|,
literal|"ophemel"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"opheusden"
argument_list|,
literal|"opheusd"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophief"
argument_list|,
literal|"ophief"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophield"
argument_list|,
literal|"ophield"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophieven"
argument_list|,
literal|"ophiev"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophoepelt"
argument_list|,
literal|"ophoepelt"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophoog"
argument_list|,
literal|"ophog"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophoogzand"
argument_list|,
literal|"ophoogzand"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophopen"
argument_list|,
literal|"ophop"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophoping"
argument_list|,
literal|"ophop"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"ophouden"
argument_list|,
literal|"ophoud"
argument_list|)
expr_stmt|;
block|}
comment|/**    * @deprecated remove this test in Lucene 4.0    */
annotation|@
name|Deprecated
DECL|method|testOldBuggyStemmer
specifier|public
name|void
name|testOldBuggyStemmer
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|DutchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"opheffen"
argument_list|,
literal|"ophef"
argument_list|)
expr_stmt|;
comment|// versus snowball 'opheff'
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"opheffende"
argument_list|,
literal|"ophef"
argument_list|)
expr_stmt|;
comment|// versus snowball 'opheff'
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"opheffing"
argument_list|,
literal|"ophef"
argument_list|)
expr_stmt|;
comment|// versus snowball 'opheff'
block|}
DECL|method|testSnowballCorrectness
specifier|public
name|void
name|testSnowballCorrectness
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|DutchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"opheffen"
argument_list|,
literal|"opheff"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"opheffende"
argument_list|,
literal|"opheff"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"opheffing"
argument_list|,
literal|"opheff"
argument_list|)
expr_stmt|;
block|}
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|DutchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"lichaamsziek"
argument_list|,
literal|"lichaamsziek"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"lichamelijk"
argument_list|,
literal|"licham"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"lichamelijke"
argument_list|,
literal|"licham"
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"lichamelijkheden"
argument_list|,
literal|"licham"
argument_list|)
expr_stmt|;
block|}
comment|/*     * Test that changes to the exclusion table are applied immediately    * when using reusable token streams.    */
DECL|method|testExclusionTableReuse
specifier|public
name|void
name|testExclusionTableReuse
parameter_list|()
throws|throws
name|Exception
block|{
name|DutchAnalyzer
name|a
init|=
operator|new
name|DutchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"lichamelijk"
argument_list|,
literal|"licham"
argument_list|)
expr_stmt|;
name|a
operator|.
name|setStemExclusionTable
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"lichamelijk"
block|}
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"lichamelijk"
argument_list|,
literal|"lichamelijk"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExclusionTableViaCtor
specifier|public
name|void
name|testExclusionTableViaCtor
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"lichamelijk"
argument_list|)
expr_stmt|;
name|DutchAnalyzer
name|a
init|=
operator|new
name|DutchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"lichamelijk lichamelijke"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lichamelijk"
block|,
literal|"licham"
block|}
argument_list|)
expr_stmt|;
name|a
operator|=
operator|new
name|DutchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|,
name|set
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"lichamelijk lichamelijke"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lichamelijk"
block|,
literal|"licham"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*     * Test that changes to the dictionary stemming table are applied immediately    * when using reusable token streams.    */
DECL|method|testStemDictionaryReuse
specifier|public
name|void
name|testStemDictionaryReuse
parameter_list|()
throws|throws
name|Exception
block|{
name|DutchAnalyzer
name|a
init|=
operator|new
name|DutchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"lichamelijk"
argument_list|,
literal|"licham"
argument_list|)
expr_stmt|;
name|a
operator|.
name|setStemDictionary
argument_list|(
name|customDictFile
argument_list|)
expr_stmt|;
name|checkOneTermReuse
argument_list|(
name|a
argument_list|,
literal|"lichamelijk"
argument_list|,
literal|"somethingentirelydifferent"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Prior to 3.1, this analyzer had no lowercase filter.    * stopwords were case sensitive. Preserve this for back compat.    * @deprecated Remove this test in Lucene 4.0    */
annotation|@
name|Deprecated
DECL|method|testBuggyStopwordsCasing
specifier|public
name|void
name|testBuggyStopwordsCasing
parameter_list|()
throws|throws
name|IOException
block|{
name|DutchAnalyzer
name|a
init|=
operator|new
name|DutchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Zelf"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"zelf"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that stopwords are not case sensitive    */
DECL|method|testStopwordsCasing
specifier|public
name|void
name|testStopwordsCasing
parameter_list|()
throws|throws
name|IOException
block|{
name|DutchAnalyzer
name|a
init|=
operator|new
name|DutchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Zelf"
argument_list|,
operator|new
name|String
index|[]
block|{ }
argument_list|)
expr_stmt|;
block|}
DECL|method|check
specifier|private
name|void
name|check
parameter_list|(
specifier|final
name|String
name|input
parameter_list|,
specifier|final
name|String
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
operator|new
name|DutchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
name|input
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
