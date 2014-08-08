begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
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
name|util
operator|.
name|CharArraySet
import|;
end_import
begin_comment
comment|/**  * Test case for RussianAnalyzer.  */
end_comment
begin_class
DECL|class|TestRussianAnalyzer
specifier|public
class|class
name|TestRussianAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** Check that RussianAnalyzer doesnt discard any numbers */
DECL|method|testDigitsInRussianCharset
specifier|public
name|void
name|testDigitsInRussianCharset
parameter_list|()
throws|throws
name|IOException
block|{
name|RussianAnalyzer
name|ra
init|=
operator|new
name|RussianAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|ra
argument_list|,
literal|"text 1000"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"text"
block|,
literal|"1000"
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
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|RussianAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐÐ¼ÐµÑÑÐµ Ñ ÑÐµÐ¼ Ð¾ ÑÐ¸Ð»Ðµ ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½Ð¾Ð¹ ÑÐ½ÐµÑÐ³Ð¸Ð¸ Ð¸Ð¼ÐµÐ»Ð¸ Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ ÐµÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð²Ð¼ÐµÑÑ"
block|,
literal|"ÑÐ¸Ð»"
block|,
literal|"ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½"
block|,
literal|"ÑÐ½ÐµÑÐ³"
block|,
literal|"Ð¸Ð¼ÐµÐ»"
block|,
literal|"Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐÐ¾ Ð·Ð½Ð°Ð½Ð¸Ðµ ÑÑÐ¾ ÑÑÐ°Ð½Ð¸Ð»Ð¾ÑÑ Ð² ÑÐ°Ð¹Ð½Ðµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð·Ð½Ð°Ð½"
block|,
literal|"ÑÑ"
block|,
literal|"ÑÑÐ°Ð½"
block|,
literal|"ÑÐ°Ð¹Ð½"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithStemExclusionSet
specifier|public
name|void
name|testWithStemExclusionSet
parameter_list|()
throws|throws
name|Exception
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
literal|"Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ"
argument_list|)
expr_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|RussianAnalyzer
operator|.
name|getDefaultStopSet
argument_list|()
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐÐ¼ÐµÑÑÐµ Ñ ÑÐµÐ¼ Ð¾ ÑÐ¸Ð»Ðµ ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½Ð¾Ð¹ ÑÐ½ÐµÑÐ³Ð¸Ð¸ Ð¸Ð¼ÐµÐ»Ð¸ Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ ÐµÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð²Ð¼ÐµÑÑ"
block|,
literal|"ÑÐ¸Ð»"
block|,
literal|"ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½"
block|,
literal|"ÑÐ½ÐµÑÐ³"
block|,
literal|"Ð¸Ð¼ÐµÐ»"
block|,
literal|"Ð¿ÑÐµÐ´ÑÑÐ°Ð²Ð»ÐµÐ½Ð¸Ðµ"
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
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|RussianAnalyzer
argument_list|()
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
