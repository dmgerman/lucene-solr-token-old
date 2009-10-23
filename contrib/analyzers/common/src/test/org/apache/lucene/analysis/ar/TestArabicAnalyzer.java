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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|util
operator|.
name|Version
import|;
end_import
begin_comment
comment|/**  * Test the Arabic Analyzer  *  */
end_comment
begin_class
DECL|class|TestArabicAnalyzer
specifier|public
class|class
name|TestArabicAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** This test fails with NPE when the     * stopwords file is missing in classpath */
DECL|method|testResourcesAvailable
specifier|public
name|void
name|testResourcesAvailable
parameter_list|()
block|{
operator|new
name|ArabicAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Some simple tests showing some features of the analyzer, how some regular forms will conflate    */
DECL|method|testBasicFeatures
specifier|public
name|void
name|testBasicFeatures
parameter_list|()
throws|throws
name|Exception
block|{
name|ArabicAnalyzer
name|a
init|=
operator|new
name|ArabicAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙØ¨ÙØ±"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙØ¨ÙØ±"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙØ¨ÙØ±Ø©"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙØ¨ÙØ±"
block|}
argument_list|)
expr_stmt|;
comment|// feminine marker
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙØ´Ø±ÙØ¨"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙØ´Ø±ÙØ¨"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙØ´Ø±ÙØ¨Ø§Øª"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙØ´Ø±ÙØ¨"
block|}
argument_list|)
expr_stmt|;
comment|// plural -at
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø£ÙØ±ÙÙÙÙÙ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø§ÙØ±ÙÙ"
block|}
argument_list|)
expr_stmt|;
comment|// plural -in
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø§ÙØ±ÙÙÙ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø§ÙØ±ÙÙ"
block|}
argument_list|)
expr_stmt|;
comment|// singular with bare alif
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙØªØ§Ø¨"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙØªØ§Ø¨"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø§ÙÙØªØ§Ø¨"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙØªØ§Ø¨"
block|}
argument_list|)
expr_stmt|;
comment|// definite article
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙØ§ ÙÙÙØª Ø£ÙÙØ§ÙÙÙ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙÙÙØª"
block|,
literal|"Ø§ÙÙØ§ÙÙÙ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø§ÙØ°ÙÙ ÙÙÙØª Ø£ÙÙØ§ÙÙÙ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙÙÙØª"
block|,
literal|"Ø§ÙÙØ§ÙÙÙ"
block|}
argument_list|)
expr_stmt|;
comment|// stopwords
block|}
comment|/**    * Simple tests to show things are getting reset correctly, etc.    */
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|ArabicAnalyzer
name|a
init|=
operator|new
name|ArabicAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"ÙØ¨ÙØ±"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙØ¨ÙØ±"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"ÙØ¨ÙØ±Ø©"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙØ¨ÙØ±"
block|}
argument_list|)
expr_stmt|;
comment|// feminine marker
block|}
comment|/**    * Non-arabic text gets treated in a similar way as SimpleAnalyzer.    */
DECL|method|testEnglishInput
specifier|public
name|void
name|testEnglishInput
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
operator|new
name|ArabicAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
literal|"English text."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"english"
block|,
literal|"text"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that custom stopwords work, and are not case-sensitive.    */
DECL|method|testCustomStopwords
specifier|public
name|void
name|testCustomStopwords
parameter_list|()
throws|throws
name|Exception
block|{
name|ArabicAnalyzer
name|a
init|=
operator|new
name|ArabicAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the"
block|,
literal|"and"
block|,
literal|"a"
block|}
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"The quick brown fox."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quick"
block|,
literal|"brown"
block|,
literal|"fox"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
