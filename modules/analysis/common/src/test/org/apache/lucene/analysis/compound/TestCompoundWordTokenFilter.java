begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.compound
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
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
name|io
operator|.
name|StringReader
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
name|CharReader
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
name|MockTokenizer
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
name|TokenFilter
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
name|charfilter
operator|.
name|MappingCharFilter
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
name|charfilter
operator|.
name|NormalizeCharMap
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
name|compound
operator|.
name|hyphenation
operator|.
name|HyphenationTree
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
name|WhitespaceTokenizer
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|Attribute
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
name|AttributeImpl
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import
begin_class
DECL|class|TestCompoundWordTokenFilter
specifier|public
class|class
name|TestCompoundWordTokenFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|makeDictionary
specifier|private
specifier|static
name|CharArraySet
name|makeDictionary
parameter_list|(
name|String
modifier|...
name|dictionary
parameter_list|)
block|{
return|return
operator|new
name|CharArraySet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|dictionary
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|testHyphenationCompoundWordsDA
specifier|public
name|void
name|testHyphenationCompoundWordsDA
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|dict
init|=
name|makeDictionary
argument_list|(
literal|"lÃ¦se"
argument_list|,
literal|"hest"
argument_list|)
decl_stmt|;
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"da_UTF8.xml"
argument_list|)
operator|.
name|toExternalForm
argument_list|()
argument_list|)
decl_stmt|;
name|HyphenationTree
name|hyphenator
init|=
name|HyphenationCompoundWordTokenFilter
operator|.
name|getHyphenationTree
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|HyphenationCompoundWordTokenFilter
name|tf
init|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"min veninde som er lidt af en lÃ¦sehest"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"min"
block|,
literal|"veninde"
block|,
literal|"som"
block|,
literal|"er"
block|,
literal|"lidt"
block|,
literal|"af"
block|,
literal|"en"
block|,
literal|"lÃ¦sehest"
block|,
literal|"lÃ¦se"
block|,
literal|"hest"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testHyphenationCompoundWordsDELongestMatch
specifier|public
name|void
name|testHyphenationCompoundWordsDELongestMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|dict
init|=
name|makeDictionary
argument_list|(
literal|"basketball"
argument_list|,
literal|"basket"
argument_list|,
literal|"ball"
argument_list|,
literal|"kurv"
argument_list|)
decl_stmt|;
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"da_UTF8.xml"
argument_list|)
operator|.
name|toExternalForm
argument_list|()
argument_list|)
decl_stmt|;
name|HyphenationTree
name|hyphenator
init|=
name|HyphenationCompoundWordTokenFilter
operator|.
name|getHyphenationTree
argument_list|(
name|is
argument_list|)
decl_stmt|;
comment|// the word basket will not be added due to the longest match option
name|HyphenationCompoundWordTokenFilter
name|tf
init|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"basketballkurv"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
literal|40
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"basketballkurv"
block|,
literal|"basketball"
block|,
literal|"ball"
block|,
literal|"kurv"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * With hyphenation-only, you can get a lot of nonsense tokens.    * This can be controlled with the min/max subword size.    */
DECL|method|testHyphenationOnly
specifier|public
name|void
name|testHyphenationOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"da_UTF8.xml"
argument_list|)
operator|.
name|toExternalForm
argument_list|()
argument_list|)
decl_stmt|;
name|HyphenationTree
name|hyphenator
init|=
name|HyphenationCompoundWordTokenFilter
operator|.
name|getHyphenationTree
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|HyphenationCompoundWordTokenFilter
name|tf
init|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"basketballkurv"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
decl_stmt|;
comment|// min=2, max=4
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"basketballkurv"
block|,
literal|"ba"
block|,
literal|"sket"
block|,
literal|"bal"
block|,
literal|"ball"
block|,
literal|"kurv"
block|}
argument_list|)
expr_stmt|;
name|tf
operator|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"basketballkurv"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|)
expr_stmt|;
comment|// min=4, max=6
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"basketballkurv"
block|,
literal|"basket"
block|,
literal|"sket"
block|,
literal|"ball"
block|,
literal|"lkurv"
block|,
literal|"kurv"
block|}
argument_list|)
expr_stmt|;
name|tf
operator|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"basketballkurv"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
literal|4
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// min=4, max=10
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"basketballkurv"
block|,
literal|"basket"
block|,
literal|"basketbal"
block|,
literal|"basketball"
block|,
literal|"sket"
block|,
literal|"sketbal"
block|,
literal|"sketball"
block|,
literal|"ball"
block|,
literal|"ballkurv"
block|,
literal|"lkurv"
block|,
literal|"kurv"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDumbCompoundWordsSE
specifier|public
name|void
name|testDumbCompoundWordsSE
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|dict
init|=
name|makeDictionary
argument_list|(
literal|"Bil"
argument_list|,
literal|"DÃ¶rr"
argument_list|,
literal|"Motor"
argument_list|,
literal|"Tak"
argument_list|,
literal|"Borr"
argument_list|,
literal|"Slag"
argument_list|,
literal|"Hammar"
argument_list|,
literal|"Pelar"
argument_list|,
literal|"Glas"
argument_list|,
literal|"Ãgon"
argument_list|,
literal|"Fodral"
argument_list|,
literal|"Bas"
argument_list|,
literal|"Fiol"
argument_list|,
literal|"Makare"
argument_list|,
literal|"GesÃ¤ll"
argument_list|,
literal|"Sko"
argument_list|,
literal|"Vind"
argument_list|,
literal|"Rute"
argument_list|,
literal|"Torkare"
argument_list|,
literal|"Blad"
argument_list|)
decl_stmt|;
name|DictionaryCompoundWordTokenFilter
name|tf
init|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"BildÃ¶rr Bilmotor Biltak Slagborr Hammarborr Pelarborr GlasÃ¶gonfodral Basfiolsfodral BasfiolsfodralmakaregesÃ¤ll Skomakare Vindrutetorkare Vindrutetorkarblad abba"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|dict
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"BildÃ¶rr"
block|,
literal|"Bil"
block|,
literal|"dÃ¶rr"
block|,
literal|"Bilmotor"
block|,
literal|"Bil"
block|,
literal|"motor"
block|,
literal|"Biltak"
block|,
literal|"Bil"
block|,
literal|"tak"
block|,
literal|"Slagborr"
block|,
literal|"Slag"
block|,
literal|"borr"
block|,
literal|"Hammarborr"
block|,
literal|"Hammar"
block|,
literal|"borr"
block|,
literal|"Pelarborr"
block|,
literal|"Pelar"
block|,
literal|"borr"
block|,
literal|"GlasÃ¶gonfodral"
block|,
literal|"Glas"
block|,
literal|"Ã¶gon"
block|,
literal|"fodral"
block|,
literal|"Basfiolsfodral"
block|,
literal|"Bas"
block|,
literal|"fiol"
block|,
literal|"fodral"
block|,
literal|"BasfiolsfodralmakaregesÃ¤ll"
block|,
literal|"Bas"
block|,
literal|"fiol"
block|,
literal|"fodral"
block|,
literal|"makare"
block|,
literal|"gesÃ¤ll"
block|,
literal|"Skomakare"
block|,
literal|"Sko"
block|,
literal|"makare"
block|,
literal|"Vindrutetorkare"
block|,
literal|"Vind"
block|,
literal|"rute"
block|,
literal|"torkare"
block|,
literal|"Vindrutetorkarblad"
block|,
literal|"Vind"
block|,
literal|"rute"
block|,
literal|"blad"
block|,
literal|"abba"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|3
block|,
literal|8
block|,
literal|8
block|,
literal|11
block|,
literal|17
block|,
literal|17
block|,
literal|20
block|,
literal|24
block|,
literal|24
block|,
literal|28
block|,
literal|33
block|,
literal|33
block|,
literal|39
block|,
literal|44
block|,
literal|44
block|,
literal|49
block|,
literal|54
block|,
literal|54
block|,
literal|58
block|,
literal|62
block|,
literal|69
block|,
literal|69
block|,
literal|72
block|,
literal|77
block|,
literal|84
block|,
literal|84
block|,
literal|87
block|,
literal|92
block|,
literal|98
block|,
literal|104
block|,
literal|111
block|,
literal|111
block|,
literal|114
block|,
literal|121
block|,
literal|121
block|,
literal|125
block|,
literal|129
block|,
literal|137
block|,
literal|137
block|,
literal|141
block|,
literal|151
block|,
literal|156
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|7
block|,
literal|3
block|,
literal|7
block|,
literal|16
block|,
literal|11
block|,
literal|16
block|,
literal|23
block|,
literal|20
block|,
literal|23
block|,
literal|32
block|,
literal|28
block|,
literal|32
block|,
literal|43
block|,
literal|39
block|,
literal|43
block|,
literal|53
block|,
literal|49
block|,
literal|53
block|,
literal|68
block|,
literal|58
block|,
literal|62
block|,
literal|68
block|,
literal|83
block|,
literal|72
block|,
literal|76
block|,
literal|83
block|,
literal|110
block|,
literal|87
block|,
literal|91
block|,
literal|98
block|,
literal|104
block|,
literal|110
block|,
literal|120
block|,
literal|114
block|,
literal|120
block|,
literal|136
block|,
literal|125
block|,
literal|129
block|,
literal|136
block|,
literal|155
block|,
literal|141
block|,
literal|145
block|,
literal|155
block|,
literal|160
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDumbCompoundWordsSELongestMatch
specifier|public
name|void
name|testDumbCompoundWordsSELongestMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|dict
init|=
name|makeDictionary
argument_list|(
literal|"Bil"
argument_list|,
literal|"DÃ¶rr"
argument_list|,
literal|"Motor"
argument_list|,
literal|"Tak"
argument_list|,
literal|"Borr"
argument_list|,
literal|"Slag"
argument_list|,
literal|"Hammar"
argument_list|,
literal|"Pelar"
argument_list|,
literal|"Glas"
argument_list|,
literal|"Ãgon"
argument_list|,
literal|"Fodral"
argument_list|,
literal|"Bas"
argument_list|,
literal|"Fiols"
argument_list|,
literal|"Makare"
argument_list|,
literal|"GesÃ¤ll"
argument_list|,
literal|"Sko"
argument_list|,
literal|"Vind"
argument_list|,
literal|"Rute"
argument_list|,
literal|"Torkare"
argument_list|,
literal|"Blad"
argument_list|,
literal|"Fiolsfodral"
argument_list|)
decl_stmt|;
name|DictionaryCompoundWordTokenFilter
name|tf
init|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"BasfiolsfodralmakaregesÃ¤ll"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"BasfiolsfodralmakaregesÃ¤ll"
block|,
literal|"Bas"
block|,
literal|"fiolsfodral"
block|,
literal|"fodral"
block|,
literal|"makare"
block|,
literal|"gesÃ¤ll"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|3
block|,
literal|8
block|,
literal|14
block|,
literal|20
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|26
block|,
literal|3
block|,
literal|14
block|,
literal|14
block|,
literal|20
block|,
literal|26
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTokenEndingWithWordComponentOfMinimumLength
specifier|public
name|void
name|testTokenEndingWithWordComponentOfMinimumLength
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|dict
init|=
name|makeDictionary
argument_list|(
literal|"ab"
argument_list|,
literal|"cd"
argument_list|,
literal|"ef"
argument_list|)
decl_stmt|;
name|DictionaryCompoundWordTokenFilter
name|tf
init|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"abcdef"
argument_list|)
argument_list|)
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abcdef"
block|,
literal|"ab"
block|,
literal|"cd"
block|,
literal|"ef"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|2
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|2
block|,
literal|4
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWordComponentWithLessThanMinimumLength
specifier|public
name|void
name|testWordComponentWithLessThanMinimumLength
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|dict
init|=
name|makeDictionary
argument_list|(
literal|"abc"
argument_list|,
literal|"d"
argument_list|,
literal|"efg"
argument_list|)
decl_stmt|;
name|DictionaryCompoundWordTokenFilter
name|tf
init|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"abcdefg"
argument_list|)
argument_list|)
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// since "d" is shorter than the minimum subword size, it should not be added to the token stream
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abcdefg"
block|,
literal|"abc"
block|,
literal|"efg"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|7
block|,
literal|3
block|,
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReset
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|dict
init|=
name|makeDictionary
argument_list|(
literal|"Rind"
argument_list|,
literal|"Fleisch"
argument_list|,
literal|"Draht"
argument_list|,
literal|"Schere"
argument_list|,
literal|"Gesetz"
argument_list|,
literal|"Aufgabe"
argument_list|,
literal|"Ãberwachung"
argument_list|)
decl_stmt|;
name|Tokenizer
name|wsTokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"RindfleischÃ¼berwachungsgesetz"
argument_list|)
argument_list|)
decl_stmt|;
name|DictionaryCompoundWordTokenFilter
name|tf
init|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|wsTokenizer
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|tf
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RindfleischÃ¼berwachungsgesetz"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Rind"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|wsTokenizer
operator|.
name|reset
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"RindfleischÃ¼berwachungsgesetz"
argument_list|)
argument_list|)
expr_stmt|;
name|tf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|tf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RindfleischÃ¼berwachungsgesetz"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRetainMockAttribute
specifier|public
name|void
name|testRetainMockAttribute
parameter_list|()
throws|throws
name|Exception
block|{
name|CharArraySet
name|dict
init|=
name|makeDictionary
argument_list|(
literal|"abc"
argument_list|,
literal|"d"
argument_list|,
literal|"efg"
argument_list|)
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"abcdefg"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
operator|new
name|MockRetainAttributeFilter
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|stream
operator|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|stream
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|MockRetainAttribute
name|retAtt
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|MockRetainAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Custom attribute value was lost"
argument_list|,
name|retAtt
operator|.
name|getRetain
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|interface|MockRetainAttribute
specifier|public
specifier|static
interface|interface
name|MockRetainAttribute
extends|extends
name|Attribute
block|{
DECL|method|setRetain
name|void
name|setRetain
parameter_list|(
name|boolean
name|attr
parameter_list|)
function_decl|;
DECL|method|getRetain
name|boolean
name|getRetain
parameter_list|()
function_decl|;
block|}
DECL|class|MockRetainAttributeImpl
specifier|public
specifier|static
specifier|final
class|class
name|MockRetainAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|MockRetainAttribute
block|{
DECL|field|retain
specifier|private
name|boolean
name|retain
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|retain
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|getRetain
specifier|public
name|boolean
name|getRetain
parameter_list|()
block|{
return|return
name|retain
return|;
block|}
DECL|method|setRetain
specifier|public
name|void
name|setRetain
parameter_list|(
name|boolean
name|retain
parameter_list|)
block|{
name|this
operator|.
name|retain
operator|=
name|retain
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
name|MockRetainAttribute
name|t
init|=
operator|(
name|MockRetainAttribute
operator|)
name|target
decl_stmt|;
name|t
operator|.
name|setRetain
argument_list|(
name|retain
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MockRetainAttributeFilter
specifier|private
specifier|static
class|class
name|MockRetainAttributeFilter
extends|extends
name|TokenFilter
block|{
DECL|field|retainAtt
name|MockRetainAttribute
name|retainAtt
init|=
name|addAttribute
argument_list|(
name|MockRetainAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|MockRetainAttributeFilter
name|MockRetainAttributeFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|retainAtt
operator|.
name|setRetain
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|// SOLR-2891
comment|// *CompoundWordTokenFilter blindly adds term length to offset, but this can take things out of bounds
comment|// wrt original text if a previous filter increases the length of the word (in this case Ã¼ -> ue)
comment|// so in this case we behave like WDF, and preserve any modified offsets
DECL|method|testInvalidOffsets
specifier|public
name|void
name|testInvalidOffsets
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CharArraySet
name|dict
init|=
name|makeDictionary
argument_list|(
literal|"fall"
argument_list|)
decl_stmt|;
specifier|final
name|NormalizeCharMap
name|normMap
init|=
operator|new
name|NormalizeCharMap
argument_list|()
decl_stmt|;
name|normMap
operator|.
name|add
argument_list|(
literal|"Ã¼"
argument_list|,
literal|"ue"
argument_list|)
expr_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TokenFilter
name|filter
init|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
name|dict
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|filter
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"bankÃ¼berfall"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"bankueberfall"
block|,
literal|"fall"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|12
block|,
literal|12
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CharArraySet
name|dict
init|=
name|makeDictionary
argument_list|(
literal|"a"
argument_list|,
literal|"e"
argument_list|,
literal|"i"
argument_list|,
literal|"o"
argument_list|,
literal|"u"
argument_list|,
literal|"y"
argument_list|,
literal|"bc"
argument_list|,
literal|"def"
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
name|dict
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|a
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"da_UTF8.xml"
argument_list|)
operator|.
name|toExternalForm
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|HyphenationTree
name|hyphenator
init|=
name|HyphenationCompoundWordTokenFilter
operator|.
name|getHyphenationTree
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|Analyzer
name|b
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TokenFilter
name|filter
init|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
name|hyphenator
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|filter
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|b
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
