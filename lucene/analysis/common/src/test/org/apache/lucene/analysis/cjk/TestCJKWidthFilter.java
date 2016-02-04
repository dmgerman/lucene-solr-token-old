begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_comment
comment|/**  * Tests for {@link CJKWidthFilter}  */
end_comment
begin_class
DECL|class|TestCJKWidthFilter
specifier|public
class|class
name|TestCJKWidthFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|analyzer
operator|=
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
name|source
init|=
operator|new
name|MockTokenizer
argument_list|(
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
name|source
argument_list|,
operator|new
name|CJKWidthFilter
argument_list|(
name|source
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Full-width ASCII forms normalized to half-width (basic latin)    */
DECL|method|testFullWidthASCII
specifier|public
name|void
name|testFullWidthASCII
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï¼´ï½ï½ï½ ï¼ï¼ï¼ï¼"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Test"
block|,
literal|"1234"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|5
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|9
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Half-width katakana forms normalized to standard katakana.    * A bit trickier in some cases, since half-width forms are decomposed    * and voice marks need to be recombined with a preceding base form.     */
DECL|method|testHalfWidthKana
specifier|public
name|void
name|testHalfWidthKana
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï½¶ï¾ï½¶ï¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã«ã¿ã«ã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï½³ï¾ï½¨ï½¯ï¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã´ã£ãã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ï¾ï¾ï¾ï½¿ï¾ï½¯ï½¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ããã½ããã¯"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomData
specifier|public
name|void
name|testRandomData
parameter_list|()
throws|throws
name|IOException
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
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
name|CJKWidthFilter
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
