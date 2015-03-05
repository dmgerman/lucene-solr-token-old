begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ar
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|core
operator|.
name|KeywordTokenizer
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
name|SetKeywordMarkerFilter
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
comment|/**  * Test the Arabic Normalization Filter  *  */
end_comment
begin_class
DECL|class|TestArabicStemFilter
specifier|public
class|class
name|TestArabicStemFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testAlPrefix
specifier|public
name|void
name|testAlPrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø§ÙØ­Ø³Ù"
argument_list|,
literal|"Ø­Ø³Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testWalPrefix
specifier|public
name|void
name|testWalPrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØ§ÙØ­Ø³Ù"
argument_list|,
literal|"Ø­Ø³Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBalPrefix
specifier|public
name|void
name|testBalPrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø¨Ø§ÙØ­Ø³Ù"
argument_list|,
literal|"Ø­Ø³Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testKalPrefix
specifier|public
name|void
name|testKalPrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØ§ÙØ­Ø³Ù"
argument_list|,
literal|"Ø­Ø³Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFalPrefix
specifier|public
name|void
name|testFalPrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØ§ÙØ­Ø³Ù"
argument_list|,
literal|"Ø­Ø³Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLlPrefix
specifier|public
name|void
name|testLlPrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙÙØ§Ø®Ø±"
argument_list|,
literal|"Ø§Ø®Ø±"
argument_list|)
expr_stmt|;
block|}
DECL|method|testWaPrefix
specifier|public
name|void
name|testWaPrefix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØ­Ø³Ù"
argument_list|,
literal|"Ø­Ø³Ù"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAhSuffix
specifier|public
name|void
name|testAhSuffix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø²ÙØ¬ÙØ§"
argument_list|,
literal|"Ø²ÙØ¬"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAnSuffix
specifier|public
name|void
name|testAnSuffix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø³Ø§ÙØ¯Ø§Ù"
argument_list|,
literal|"Ø³Ø§ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testAtSuffix
specifier|public
name|void
name|testAtSuffix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø³Ø§ÙØ¯Ø§Øª"
argument_list|,
literal|"Ø³Ø§ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testWnSuffix
specifier|public
name|void
name|testWnSuffix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø³Ø§ÙØ¯ÙÙ"
argument_list|,
literal|"Ø³Ø§ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testYnSuffix
specifier|public
name|void
name|testYnSuffix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø³Ø§ÙØ¯ÙÙ"
argument_list|,
literal|"Ø³Ø§ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testYhSuffix
specifier|public
name|void
name|testYhSuffix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø³Ø§ÙØ¯ÙÙ"
argument_list|,
literal|"Ø³Ø§ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testYpSuffix
specifier|public
name|void
name|testYpSuffix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø³Ø§ÙØ¯ÙØ©"
argument_list|,
literal|"Ø³Ø§ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testHSuffix
specifier|public
name|void
name|testHSuffix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø³Ø§ÙØ¯Ù"
argument_list|,
literal|"Ø³Ø§ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPSuffix
specifier|public
name|void
name|testPSuffix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø³Ø§ÙØ¯Ø©"
argument_list|,
literal|"Ø³Ø§ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testYSuffix
specifier|public
name|void
name|testYSuffix
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø³Ø§ÙØ¯Ù"
argument_list|,
literal|"Ø³Ø§ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testComboPrefSuf
specifier|public
name|void
name|testComboPrefSuf
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"ÙØ³Ø§ÙØ¯ÙÙ"
argument_list|,
literal|"Ø³Ø§ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testComboSuf
specifier|public
name|void
name|testComboSuf
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø³Ø§ÙØ¯ÙØ§Øª"
argument_list|,
literal|"Ø³Ø§ÙØ¯"
argument_list|)
expr_stmt|;
block|}
DECL|method|testShouldntStem
specifier|public
name|void
name|testShouldntStem
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"Ø§ÙÙ"
argument_list|,
literal|"Ø§ÙÙ"
argument_list|)
expr_stmt|;
block|}
DECL|method|testNonArabic
specifier|public
name|void
name|testNonArabic
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"English"
argument_list|,
literal|"English"
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithKeywordAttribute
specifier|public
name|void
name|testWithKeywordAttribute
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
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"Ø³Ø§ÙØ¯ÙØ§Øª"
argument_list|)
expr_stmt|;
name|MockTokenizer
name|tokenStream
init|=
name|whitespaceMockTokenizer
argument_list|(
literal|"Ø³Ø§ÙØ¯ÙØ§Øª"
argument_list|)
decl_stmt|;
name|ArabicStemFilter
name|filter
init|=
operator|new
name|ArabicStemFilter
argument_list|(
operator|new
name|SetKeywordMarkerFilter
argument_list|(
name|tokenStream
argument_list|,
name|set
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø³Ø§ÙØ¯ÙØ§Øª"
block|}
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
name|IOException
block|{
name|MockTokenizer
name|tokenStream
init|=
name|whitespaceMockTokenizer
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|ArabicStemFilter
name|filter
init|=
operator|new
name|ArabicStemFilter
argument_list|(
name|tokenStream
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
name|expected
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
parameter_list|()
throws|throws
name|IOException
block|{
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
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|ArabicStemFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
