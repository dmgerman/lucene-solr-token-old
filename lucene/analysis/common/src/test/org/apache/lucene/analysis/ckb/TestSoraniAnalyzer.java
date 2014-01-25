begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ckb
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ckb
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
name|util
operator|.
name|CharArraySet
import|;
end_import
begin_comment
comment|/**  * Test the Sorani analyzer  */
end_comment
begin_class
DECL|class|TestSoraniAnalyzer
specifier|public
class|class
name|TestSoraniAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * This test fails with NPE when the stopwords file is missing in classpath    */
DECL|method|testResourcesAvailable
specifier|public
name|void
name|testResourcesAvailable
parameter_list|()
block|{
operator|new
name|SoraniAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
DECL|method|testStopwords
specifier|public
name|void
name|testStopwords
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|SoraniAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¦ÛÙ Ù¾ÛØ§ÙÛ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ù¾ÛØ§Ù"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCustomStopwords
specifier|public
name|void
name|testCustomStopwords
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
operator|new
name|SoraniAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø¦ÛÙ Ù¾ÛØ§ÙÛ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø¦ÛÙ"
block|,
literal|"Ù¾ÛØ§Ù"
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
name|Analyzer
name|a
init|=
operator|new
name|SoraniAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ù¾ÛØ§ÙÛ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ù¾ÛØ§Ù"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ù¾ÛØ§Ù"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ù¾ÛØ§Ù"
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
name|IOException
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|TEST_VERSION_CURRENT
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
literal|"Ù¾ÛØ§ÙÛ"
argument_list|)
expr_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|SoraniAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|CharArraySet
operator|.
name|EMPTY_SET
argument_list|,
name|set
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ù¾ÛØ§ÙÛ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ù¾ÛØ§ÙÛ"
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
name|SoraniAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
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