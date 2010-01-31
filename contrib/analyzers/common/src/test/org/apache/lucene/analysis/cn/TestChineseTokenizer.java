begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.cn
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
comment|/** @deprecated Remove this test when ChineseAnalyzer is removed. */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|TestChineseTokenizer
specifier|public
class|class
name|TestChineseTokenizer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testOtherLetterOffset
specifier|public
name|void
name|testOtherLetterOffset
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|s
init|=
literal|"aå¤©b"
decl_stmt|;
name|ChineseTokenizer
name|tokenizer
init|=
operator|new
name|ChineseTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|correctStartOffset
init|=
literal|0
decl_stmt|;
name|int
name|correctEndOffset
init|=
literal|1
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
name|tokenizer
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|correctStartOffset
argument_list|,
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|correctEndOffset
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|correctStartOffset
operator|++
expr_stmt|;
name|correctEndOffset
operator|++
expr_stmt|;
block|}
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
name|ChineseAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"ä¸­åäººæ°å±åå½"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸­"
block|,
literal|"å"
block|,
literal|"äºº"
block|,
literal|"æ°"
block|,
literal|"å±"
block|,
literal|"å"
block|,
literal|"å½"
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
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
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
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"åäº¬å¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"å"
block|,
literal|"äº¬"
block|,
literal|"å¸"
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
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*      * Analyzer that just uses ChineseTokenizer, not ChineseFilter.      * convenience to show the behavior of the tokenizer      */
DECL|class|JustChineseTokenizerAnalyzer
specifier|private
class|class
name|JustChineseTokenizerAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
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
return|return
operator|new
name|ChineseTokenizer
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
comment|/*      * Analyzer that just uses ChineseFilter, not ChineseTokenizer.      * convenience to show the behavior of the filter.      */
DECL|class|JustChineseFilterAnalyzer
specifier|private
class|class
name|JustChineseFilterAnalyzer
extends|extends
name|Analyzer
block|{
annotation|@
name|Override
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
return|return
operator|new
name|ChineseFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/*      * ChineseTokenizer tokenizes numbers as one token, but they are filtered by ChineseFilter      */
DECL|method|testNumerics
specifier|public
name|void
name|testNumerics
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|justTokenizer
init|=
operator|new
name|JustChineseTokenizerAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|justTokenizer
argument_list|,
literal|"ä¸­1234"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸­"
block|,
literal|"1234"
block|}
argument_list|)
expr_stmt|;
comment|// in this case the ChineseAnalyzer (which applies ChineseFilter) will remove the numeric token.
name|Analyzer
name|a
init|=
operator|new
name|ChineseAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ä¸­1234"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ä¸­"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*      * ChineseTokenizer tokenizes english similar to SimpleAnalyzer.      * it will lowercase terms automatically.      *       * ChineseFilter has an english stopword list, it also removes any single character tokens.      * the stopword list is case-sensitive.      */
DECL|method|testEnglish
specifier|public
name|void
name|testEnglish
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|chinese
init|=
operator|new
name|ChineseAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|chinese
argument_list|,
literal|"This is a Test. b c d"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test"
block|}
argument_list|)
expr_stmt|;
name|Analyzer
name|justTokenizer
init|=
operator|new
name|JustChineseTokenizerAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|justTokenizer
argument_list|,
literal|"This is a Test. b c d"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"test"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|}
argument_list|)
expr_stmt|;
name|Analyzer
name|justFilter
init|=
operator|new
name|JustChineseFilterAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|justFilter
argument_list|,
literal|"This is a Test. b c d"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"This"
block|,
literal|"Test."
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
