begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.icu.segmentation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
operator|.
name|segmentation
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
name|cjk
operator|.
name|CJKBigramFilter
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
name|icu
operator|.
name|ICUNormalizer2Filter
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
begin_comment
comment|/**  * Tests ICUTokenizer's ability to work with CJKBigramFilter.  * Most tests adopted from TestCJKTokenizer  */
end_comment
begin_class
DECL|class|TestWithCJKBigramFilter
specifier|public
class|class
name|TestWithCJKBigramFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * ICUTokenizer+CJKBigramFilter    */
DECL|field|analyzer
specifier|private
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
name|source
init|=
operator|new
name|ICUTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|CJKBigramFilter
argument_list|(
name|source
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|StopFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|result
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/**    * ICUTokenizer+ICUNormalizer2Filter+CJKBigramFilter.    *     * ICUNormalizer2Filter uses nfkc_casefold by default, so this is a language-independent    * superset of CJKWidthFilter's foldings.    */
DECL|field|analyzer2
specifier|private
name|Analyzer
name|analyzer2
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
name|source
init|=
operator|new
name|ICUTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// we put this before the CJKBigramFilter, because the normalization might combine
comment|// some halfwidth katakana forms, which will affect the bigramming.
name|TokenStream
name|result
init|=
operator|new
name|ICUNormalizer2Filter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|CJKBigramFilter
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|StopFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|result
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|testJa1
specifier|public
name|void
name|testJa1
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸äºä¸åäºå­ä¸å«ä¹å"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸äº"
block|,
literal|"äºä¸"
block|,
literal|"ä¸å"
block|,
literal|"åäº"
block|,
literal|"äºå­"
block|,
literal|"å­ä¸"
block|,
literal|"ä¸å«"
block|,
literal|"å«ä¹"
block|,
literal|"ä¹å"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
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
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testJa2
specifier|public
name|void
name|testJa2
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸ äºä¸å äºå­ä¸å«ä¹ å"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸"
block|,
literal|"äºä¸"
block|,
literal|"ä¸å"
block|,
literal|"äºå­"
block|,
literal|"å­ä¸"
block|,
literal|"ä¸å«"
block|,
literal|"å«ä¹"
block|,
literal|"å"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|3
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|4
block|,
literal|5
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
block|,
literal|13
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
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
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testC
specifier|public
name|void
name|testC
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"abc defgh ijklmn opqrstu vwxy z"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abc"
block|,
literal|"defgh"
block|,
literal|"ijklmn"
block|,
literal|"opqrstu"
block|,
literal|"vwxy"
block|,
literal|"z"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|4
block|,
literal|10
block|,
literal|17
block|,
literal|25
block|,
literal|30
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|9
block|,
literal|16
block|,
literal|24
block|,
literal|29
block|,
literal|31
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
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
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * LUCENE-2207: wrong offset calculated by end()     */
DECL|method|testFinalOffset
specifier|public
name|void
name|testFinalOffset
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ãã   "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"test   "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ããtest"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"test"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|6
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<ALPHANUM>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"testãã    "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
block|,
literal|"ãã"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|6
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|,
literal|"<DOUBLE>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMix
specifier|public
name|void
name|testMix
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ãããããabcããããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"abc"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|5
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|8
block|,
literal|10
block|,
literal|11
block|,
literal|12
block|,
literal|13
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
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
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMix2
specifier|public
name|void
name|testMix2
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ãããããabãcãããã ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ab"
block|,
literal|"ã"
block|,
literal|"c"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ã"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|5
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
block|,
literal|14
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|11
block|,
literal|12
block|,
literal|13
block|,
literal|15
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<SINGLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
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
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Non-english text (outside of CJK) is treated normally, according to unicode rules     */
DECL|method|testNonIdeographic
specifier|public
name|void
name|testNonIdeographic
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸ Ø±ÙØ¨Ø±Øª ÙÙÙØ±"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸"
block|,
literal|"Ø±ÙØ¨Ø±Øª"
block|,
literal|"ÙÙÙØ±"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|7
block|,
literal|12
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
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
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Same as the above, except with a nonspacing mark to show correctness.    */
DECL|method|testNonIdeographicNonLetter
specifier|public
name|void
name|testNonIdeographicNonLetter
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸ Ø±ÙÙØ¨Ø±Øª ÙÙÙØ±"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸"
block|,
literal|"Ø±ÙÙØ¨Ø±Øª"
block|,
literal|"ÙÙÙØ±"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|8
block|,
literal|13
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
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
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSurrogates
specifier|public
name|void
name|testSurrogates
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ð©¬è±éä¹æ¯ç"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ð©¬è±"
block|,
literal|"è±é"
block|,
literal|"éä¹"
block|,
literal|"ä¹æ¯"
block|,
literal|"æ¯ç"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
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
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"ãããããabcããããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"abc"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|5
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|8
block|,
literal|10
block|,
literal|11
block|,
literal|12
block|,
literal|13
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
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
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"ãããããabãcãããã ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ab"
block|,
literal|"ã"
block|,
literal|"c"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ãã"
block|,
literal|"ã"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|5
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|,
literal|11
block|,
literal|14
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|11
block|,
literal|12
block|,
literal|13
block|,
literal|15
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<SINGLE>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|,
literal|"<SINGLE>"
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
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleChar
specifier|public
name|void
name|testSingleChar
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<SINGLE>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTokenStream
specifier|public
name|void
name|testTokenStream
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ä¸ä¸ä¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸ä¸"
block|,
literal|"ä¸ä¸"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<DOUBLE>"
block|,
literal|"<DOUBLE>"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
