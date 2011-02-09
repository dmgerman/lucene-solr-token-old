begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package
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
name|util
operator|.
name|ReusableAnalyzerBase
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestStandardAnalyzer
specifier|public
class|class
name|TestStandardAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testHugeDoc
specifier|public
name|void
name|testHugeDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
name|whitespace
index|[]
init|=
operator|new
name|char
index|[
literal|4094
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|whitespace
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|whitespace
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"testing 1234"
argument_list|)
expr_stmt|;
name|String
name|input
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|StandardTokenizer
name|tokenizer
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertTokenStreamContents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"testing"
block|,
literal|"1234"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|field|a
specifier|private
name|Analyzer
name|a
init|=
operator|new
name|ReusableAnalyzerBase
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
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|testArmenian
specifier|public
name|void
name|testArmenian
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÕÕ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ« 13 Õ´Õ«Õ¬Õ«Õ¸Õ¶ Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨ (4,600` Õ°Õ¡ÕµÕ¥ÖÕ¥Õ¶ Õ¾Õ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ¸ÖÕ´) Õ£ÖÕ¾Õ¥Õ¬ Õ¥Õ¶ Õ¯Õ¡Õ´Õ¡Õ¾Õ¸ÖÕ¶Õ¥ÖÕ« Õ¯Õ¸Õ²Õ´Õ«Ö Õ¸Ö Õ°Õ¡Õ´Õ¡ÖÕµÕ¡ Õ¢Õ¸Õ¬Õ¸Ö Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨ Õ¯Õ¡ÖÕ¸Õ² Õ§ Õ­Õ´Õ¢Õ¡Õ£ÖÕ¥Õ¬ ÖÕ¡Õ¶Õ¯Õ¡Ö Õ´Õ¡ÖÕ¤ Õ¸Õ¾ Õ¯Õ¡ÖÕ¸Õ² Õ§ Õ¢Õ¡ÖÕ¥Õ¬ ÕÕ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ« Õ¯Õ¡ÕµÖÕ¨Ö"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÕÕ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ«"
block|,
literal|"13"
block|,
literal|"Õ´Õ«Õ¬Õ«Õ¸Õ¶"
block|,
literal|"Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨"
block|,
literal|"4,600"
block|,
literal|"Õ°Õ¡ÕµÕ¥ÖÕ¥Õ¶"
block|,
literal|"Õ¾Õ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ¸ÖÕ´"
block|,
literal|"Õ£ÖÕ¾Õ¥Õ¬"
block|,
literal|"Õ¥Õ¶"
block|,
literal|"Õ¯Õ¡Õ´Õ¡Õ¾Õ¸ÖÕ¶Õ¥ÖÕ«"
block|,
literal|"Õ¯Õ¸Õ²Õ´Õ«Ö"
block|,
literal|"Õ¸Ö"
block|,
literal|"Õ°Õ¡Õ´Õ¡ÖÕµÕ¡"
block|,
literal|"Õ¢Õ¸Õ¬Õ¸Ö"
block|,
literal|"Õ°Õ¸Õ¤Õ¾Õ¡Õ®Õ¶Õ¥ÖÕ¨"
block|,
literal|"Õ¯Õ¡ÖÕ¸Õ²"
block|,
literal|"Õ§"
block|,
literal|"Õ­Õ´Õ¢Õ¡Õ£ÖÕ¥Õ¬"
block|,
literal|"ÖÕ¡Õ¶Õ¯Õ¡Ö"
block|,
literal|"Õ´Õ¡ÖÕ¤"
block|,
literal|"Õ¸Õ¾"
block|,
literal|"Õ¯Õ¡ÖÕ¸Õ²"
block|,
literal|"Õ§"
block|,
literal|"Õ¢Õ¡ÖÕ¥Õ¬"
block|,
literal|"ÕÕ«ÖÕ«ÕºÕ¥Õ¤Õ«Õ¡ÕµÕ«"
block|,
literal|"Õ¯Õ¡ÕµÖÕ¨"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAmharic
specifier|public
name|void
name|testAmharic
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ááªááµá« á¨á£á á¥á ááá á¨á°áá áµá­á­ááá áá» áááá  ááááµ (á¢áá³á­á­ááá²á«) ááá¢ ááááá"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ááªááµá«"
block|,
literal|"á¨á£á"
block|,
literal|"á¥á"
block|,
literal|"ááá"
block|,
literal|"á¨á°áá"
block|,
literal|"áµá­á­ááá"
block|,
literal|"áá»"
block|,
literal|"áááá "
block|,
literal|"ááááµ"
block|,
literal|"á¢áá³á­á­ááá²á«"
block|,
literal|"áá"
block|,
literal|"ááááá"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testArabic
specifier|public
name|void
name|testArabic
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ø§ÙÙÙÙÙ Ø§ÙÙØ«Ø§Ø¦ÙÙ Ø§ÙØ£ÙÙ Ø¹Ù ÙÙÙÙØ¨ÙØ¯ÙØ§ ÙØ³ÙÙ \"Ø§ÙØ­ÙÙÙØ© Ø¨Ø§ÙØ£Ø±ÙØ§Ù: ÙØµØ© ÙÙÙÙØ¨ÙØ¯ÙØ§\" (Ø¨Ø§ÙØ¥ÙØ¬ÙÙØ²ÙØ©: Truth in Numbers: The Wikipedia Story)Ø Ø³ÙØªÙ Ø¥Ø·ÙØ§ÙÙ ÙÙ 2008."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø§ÙÙÙÙÙ"
block|,
literal|"Ø§ÙÙØ«Ø§Ø¦ÙÙ"
block|,
literal|"Ø§ÙØ£ÙÙ"
block|,
literal|"Ø¹Ù"
block|,
literal|"ÙÙÙÙØ¨ÙØ¯ÙØ§"
block|,
literal|"ÙØ³ÙÙ"
block|,
literal|"Ø§ÙØ­ÙÙÙØ©"
block|,
literal|"Ø¨Ø§ÙØ£Ø±ÙØ§Ù"
block|,
literal|"ÙØµØ©"
block|,
literal|"ÙÙÙÙØ¨ÙØ¯ÙØ§"
block|,
literal|"Ø¨Ø§ÙØ¥ÙØ¬ÙÙØ²ÙØ©"
block|,
literal|"Truth"
block|,
literal|"in"
block|,
literal|"Numbers"
block|,
literal|"The"
block|,
literal|"Wikipedia"
block|,
literal|"Story"
block|,
literal|"Ø³ÙØªÙ"
block|,
literal|"Ø¥Ø·ÙØ§ÙÙ"
block|,
literal|"ÙÙ"
block|,
literal|"2008"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAramaic
specifier|public
name|void
name|testAramaic
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÜÜÜ©ÜÜ¦ÜÜÜ (ÜÜ¢ÜÜ ÜÜ: Wikipedia) ÜÜ ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ ÜÜÜªÜ¬Ü ÜÜÜ¢ÜÜªÜ¢Ü ÜÜ Ü«Ü¢ÌÜ Ü£ÜÜÜÌÜÜ Ü«Ü¡Ü ÜÜ¬Ü Ü¡Ü¢ Ü¡ÌÜ Ü¬Ü Ü\"ÜÜÜ©Ü\" Ü\"ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ\"Ü"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÜÜÜ©ÜÜ¦ÜÜÜ"
block|,
literal|"ÜÜ¢ÜÜ ÜÜ"
block|,
literal|"Wikipedia"
block|,
literal|"ÜÜ"
block|,
literal|"ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ"
block|,
literal|"ÜÜÜªÜ¬Ü"
block|,
literal|"ÜÜÜ¢ÜÜªÜ¢Ü"
block|,
literal|"ÜÜ Ü«Ü¢ÌÜ"
block|,
literal|"Ü£ÜÜÜÌÜ"
block|,
literal|"Ü«Ü¡Ü"
block|,
literal|"ÜÜ¬Ü"
block|,
literal|"Ü¡Ü¢"
block|,
literal|"Ü¡ÌÜ Ü¬Ü"
block|,
literal|"Ü"
block|,
literal|"ÜÜÜ©Ü"
block|,
literal|"Ü"
block|,
literal|"ÜÜÜ¢Ü£Ü©Ü ÜÜ¦ÜÜÜ"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBengali
specifier|public
name|void
name|testBengali
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"à¦à¦ à¦¬à¦¿à¦¶à§à¦¬à¦à§à¦· à¦ªà¦°à¦¿à¦à¦¾à¦²à¦¨à¦¾ à¦à¦°à§ à¦à¦à¦à¦¿à¦®à¦¿à¦¡à¦¿à¦¯à¦¼à¦¾ à¦«à¦¾à¦à¦¨à§à¦¡à§à¦¶à¦¨ (à¦à¦à¦à¦¿ à¦à¦²à¦¾à¦­à¦à¦¨à¦ à¦¸à¦à¦¸à§à¦¥à¦¾)à¥¤ à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾à¦° à¦¶à§à¦°à§ à§§à§« à¦à¦¾à¦¨à§à¦¯à¦¼à¦¾à¦°à¦¿, à§¨à§¦à§¦à§§ à¦¸à¦¾à¦²à§à¥¤ à¦à¦à¦¨ à¦ªà¦°à§à¦¯à¦¨à§à¦¤ à§¨à§¦à§¦à¦à¦¿à¦°à¦ à¦¬à§à¦¶à§ à¦­à¦¾à¦·à¦¾à¦¯à¦¼ à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾ à¦°à¦¯à¦¼à§à¦à§à¥¤"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¦à¦"
block|,
literal|"à¦¬à¦¿à¦¶à§à¦¬à¦à§à¦·"
block|,
literal|"à¦ªà¦°à¦¿à¦à¦¾à¦²à¦¨à¦¾"
block|,
literal|"à¦à¦°à§"
block|,
literal|"à¦à¦à¦à¦¿à¦®à¦¿à¦¡à¦¿à¦¯à¦¼à¦¾"
block|,
literal|"à¦«à¦¾à¦à¦¨à§à¦¡à§à¦¶à¦¨"
block|,
literal|"à¦à¦à¦à¦¿"
block|,
literal|"à¦à¦²à¦¾à¦­à¦à¦¨à¦"
block|,
literal|"à¦¸à¦à¦¸à§à¦¥à¦¾"
block|,
literal|"à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾à¦°"
block|,
literal|"à¦¶à§à¦°à§"
block|,
literal|"à§§à§«"
block|,
literal|"à¦à¦¾à¦¨à§à¦¯à¦¼à¦¾à¦°à¦¿"
block|,
literal|"à§¨à§¦à§¦à§§"
block|,
literal|"à¦¸à¦¾à¦²à§"
block|,
literal|"à¦à¦à¦¨"
block|,
literal|"à¦ªà¦°à§à¦¯à¦¨à§à¦¤"
block|,
literal|"à§¨à§¦à§¦à¦à¦¿à¦°à¦"
block|,
literal|"à¦¬à§à¦¶à§"
block|,
literal|"à¦­à¦¾à¦·à¦¾à¦¯à¦¼"
block|,
literal|"à¦à¦à¦à¦¿à¦ªà¦¿à¦¡à¦¿à¦¯à¦¼à¦¾"
block|,
literal|"à¦°à¦¯à¦¼à§à¦à§"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFarsi
specifier|public
name|void
name|testFarsi
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÙÛÚ©Û Ù¾Ø¯ÛØ§Û Ø§ÙÚ¯ÙÛØ³Û Ø¯Ø± ØªØ§Ø±ÛØ® Û²Ûµ Ø¯Û Û±Û³Û·Û¹ Ø¨Ù ØµÙØ±Øª ÙÚ©ÙÙÛ Ø¨Ø±Ø§Û Ø¯Ø§ÙØ´ÙØ§ÙÙÙ ØªØ®ØµØµÛ ÙÙÙ¾Ø¯ÛØ§ ÙÙØ´ØªÙ Ø´Ø¯."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙÛÚ©Û"
block|,
literal|"Ù¾Ø¯ÛØ§Û"
block|,
literal|"Ø§ÙÚ¯ÙÛØ³Û"
block|,
literal|"Ø¯Ø±"
block|,
literal|"ØªØ§Ø±ÛØ®"
block|,
literal|"Û²Ûµ"
block|,
literal|"Ø¯Û"
block|,
literal|"Û±Û³Û·Û¹"
block|,
literal|"Ø¨Ù"
block|,
literal|"ØµÙØ±Øª"
block|,
literal|"ÙÚ©ÙÙÛ"
block|,
literal|"Ø¨Ø±Ø§Û"
block|,
literal|"Ø¯Ø§ÙØ´ÙØ§ÙÙÙ"
block|,
literal|"ØªØ®ØµØµÛ"
block|,
literal|"ÙÙÙ¾Ø¯ÛØ§"
block|,
literal|"ÙÙØ´ØªÙ"
block|,
literal|"Ø´Ø¯"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testGreek
specifier|public
name|void
name|testGreek
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÎÏÎ¬ÏÎµÏÎ±Î¹ ÏÎµ ÏÏÎ½ÎµÏÎ³Î±ÏÎ¯Î± Î±ÏÏ ÎµÎ¸ÎµÎ»Î¿Î½ÏÎ­Ï Î¼Îµ ÏÎ¿ Î»Î¿Î³Î¹ÏÎ¼Î¹ÎºÏ wiki, ÎºÎ¬ÏÎ¹ ÏÎ¿Ï ÏÎ·Î¼Î±Î¯Î½ÎµÎ¹ ÏÏÎ¹ Î¬ÏÎ¸ÏÎ± Î¼ÏÎ¿ÏÎµÎ¯ Î½Î± ÏÏÎ¿ÏÏÎµÎ¸Î¿ÏÎ½ Î® Î½Î± Î±Î»Î»Î¬Î¾Î¿ÏÎ½ Î±ÏÏ ÏÎ¿Î½ ÎºÎ±Î¸Î­Î½Î±."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÎÏÎ¬ÏÎµÏÎ±Î¹"
block|,
literal|"ÏÎµ"
block|,
literal|"ÏÏÎ½ÎµÏÎ³Î±ÏÎ¯Î±"
block|,
literal|"Î±ÏÏ"
block|,
literal|"ÎµÎ¸ÎµÎ»Î¿Î½ÏÎ­Ï"
block|,
literal|"Î¼Îµ"
block|,
literal|"ÏÎ¿"
block|,
literal|"Î»Î¿Î³Î¹ÏÎ¼Î¹ÎºÏ"
block|,
literal|"wiki"
block|,
literal|"ÎºÎ¬ÏÎ¹"
block|,
literal|"ÏÎ¿Ï"
block|,
literal|"ÏÎ·Î¼Î±Î¯Î½ÎµÎ¹"
block|,
literal|"ÏÏÎ¹"
block|,
literal|"Î¬ÏÎ¸ÏÎ±"
block|,
literal|"Î¼ÏÎ¿ÏÎµÎ¯"
block|,
literal|"Î½Î±"
block|,
literal|"ÏÏÎ¿ÏÏÎµÎ¸Î¿ÏÎ½"
block|,
literal|"Î®"
block|,
literal|"Î½Î±"
block|,
literal|"Î±Î»Î»Î¬Î¾Î¿ÏÎ½"
block|,
literal|"Î±ÏÏ"
block|,
literal|"ÏÎ¿Î½"
block|,
literal|"ÎºÎ±Î¸Î­Î½Î±"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testThai
specifier|public
name|void
name|testThai
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ. à¹à¸¥à¹à¸§à¹à¸à¸­à¸à¸°à¹à¸à¹à¸«à¸? à¹à¹à¹à¹"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
block|,
literal|"à¹à¸¥à¹à¸§à¹à¸à¸­à¸à¸°à¹à¸à¹à¸«à¸"
block|,
literal|"à¹à¹à¹à¹"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testLao
specifier|public
name|void
name|testLao
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"àºªàº²àºàº²àº¥àº°àºàº°àº¥àº±àº àºàº°àºàº²àºàº´àºàº°à»àº àºàº°àºàº²àºàº»àºàº¥àº²àº§"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"àºªàº²àºàº²àº¥àº°àºàº°àº¥àº±àº"
block|,
literal|"àºàº°àºàº²àºàº´àºàº°à»àº"
block|,
literal|"àºàº°àºàº²àºàº»àºàº¥àº²àº§"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTibetan
specifier|public
name|void
name|testTibetan
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"à½¦à¾£à½¼à½à¼à½à½à½¼à½à¼à½à½à¼à½£à½¦à¼à½ à½à½²à½¦à¼à½à½¼à½à¼à½¡à½²à½à¼à½à½²à¼à½à½à½¦à¼à½à½¼à½à¼à½ à½à½ºà½£à¼à½à½´à¼à½à½à½¼à½à¼à½à½¢à¼à½§à¼à½à½à¼à½à½à½ºà¼à½à½à½à¼à½à½à½²à½¦à¼à½¦à½¼à¼ à¼"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à½¦à¾£à½¼à½"
block|,
literal|"à½à½à½¼à½"
block|,
literal|"à½à½"
block|,
literal|"à½£à½¦"
block|,
literal|"à½ à½à½²à½¦"
block|,
literal|"à½à½¼à½"
block|,
literal|"à½¡à½²à½"
block|,
literal|"à½à½²"
block|,
literal|"à½à½à½¦"
block|,
literal|"à½à½¼à½"
block|,
literal|"à½ à½à½ºà½£"
block|,
literal|"à½à½´"
block|,
literal|"à½à½à½¼à½"
block|,
literal|"à½à½¢"
block|,
literal|"à½§"
block|,
literal|"à½à½"
block|,
literal|"à½à½à½º"
block|,
literal|"à½à½à½"
block|,
literal|"à½à½à½²à½¦"
block|,
literal|"à½¦à½¼"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*    * For chinese, tokenize as char (these can later form bigrams or whatever)    */
DECL|method|testChinese
specifier|public
name|void
name|testChinese
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ææ¯ä¸­å½äººã ï¼ï¼ï¼ï¼ ï¼´ï½ï½ï½ï½ "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æ"
block|,
literal|"æ¯"
block|,
literal|"ä¸­"
block|,
literal|"å½"
block|,
literal|"äºº"
block|,
literal|"ï¼ï¼ï¼ï¼"
block|,
literal|"ï¼´ï½ï½ï½ï½"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"."
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|" "
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
comment|/* test various jira issues this analyzer is related to */
DECL|method|testLUCENE1545
specifier|public
name|void
name|testLUCENE1545
parameter_list|()
throws|throws
name|Exception
block|{
comment|/*      * Standard analyzer does not correctly tokenize combining character U+0364 COMBINING LATIN SMALL LETTRE E.      * The word "moÍ¤chte" is incorrectly tokenized into "mo" "chte", the combining character is lost.      * Expected result is only on token "moÍ¤chte".      */
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"moÍ¤chte"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"moÍ¤chte"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* Tests from StandardAnalyzer, just to show behavior is similar */
DECL|method|testAlphanumericSA
specifier|public
name|void
name|testAlphanumericSA
parameter_list|()
throws|throws
name|Exception
block|{
comment|// alphanumeric tokens
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"B2B"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"B2B"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"2B"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"2B"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDelimitersSA
specifier|public
name|void
name|testDelimitersSA
parameter_list|()
throws|throws
name|Exception
block|{
comment|// other delimiters: "-", "/", ","
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"some-dashed-phrase"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"some"
block|,
literal|"dashed"
block|,
literal|"phrase"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"dogs,chase,cats"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dogs"
block|,
literal|"chase"
block|,
literal|"cats"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ac/dc"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ac"
block|,
literal|"dc"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testApostrophesSA
specifier|public
name|void
name|testApostrophesSA
parameter_list|()
throws|throws
name|Exception
block|{
comment|// internal apostrophes: O'Reilly, you're, O'Reilly's
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"O'Reilly"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"O'Reilly"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"you're"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"you're"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"she's"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"she's"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Jim's"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Jim's"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"don't"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"don't"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"O'Reilly's"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"O'Reilly's"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testNumericSA
specifier|public
name|void
name|testNumericSA
parameter_list|()
throws|throws
name|Exception
block|{
comment|// floating point, serial, model numbers, ip addresses, etc.
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"21.35"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"21.35"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"R2D2 C3PO"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"R2D2"
block|,
literal|"C3PO"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"216.239.63.104"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"216.239.63.104"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"216.239.63.104"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"216.239.63.104"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTextWithNumbersSA
specifier|public
name|void
name|testTextWithNumbersSA
parameter_list|()
throws|throws
name|Exception
block|{
comment|// numbers
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"David has 5000 bones"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"David"
block|,
literal|"has"
block|,
literal|"5000"
block|,
literal|"bones"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testVariousTextSA
specifier|public
name|void
name|testVariousTextSA
parameter_list|()
throws|throws
name|Exception
block|{
comment|// various
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"C embedded developers wanted"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"C"
block|,
literal|"embedded"
block|,
literal|"developers"
block|,
literal|"wanted"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo bar FOO BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"FOO"
block|,
literal|"BAR"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo      bar .  FOO<> BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"FOO"
block|,
literal|"BAR"
block|}
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"\"QUOTED\" word"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"QUOTED"
block|,
literal|"word"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKoreanSA
specifier|public
name|void
name|testKoreanSA
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Korean words
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ìëíì¸ì íê¸ìëë¤"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ìëíì¸ì"
block|,
literal|"íê¸ìëë¤"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testOffsets
specifier|public
name|void
name|testOffsets
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"David has 5000 bones"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"David"
block|,
literal|"has"
block|,
literal|"5000"
block|,
literal|"bones"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|6
block|,
literal|10
block|,
literal|15
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|9
block|,
literal|14
block|,
literal|20
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTypes
specifier|public
name|void
name|testTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"David has 5000 bones"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"David"
block|,
literal|"has"
block|,
literal|"5000"
block|,
literal|"bones"
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
literal|"<NUM>"
block|,
literal|"<ALPHANUM>"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnicodeWordBreaks
specifier|public
name|void
name|testUnicodeWordBreaks
parameter_list|()
throws|throws
name|Exception
block|{
name|WordBreakTestUnicode_6_0_0
name|wordBreakTest
init|=
operator|new
name|WordBreakTestUnicode_6_0_0
argument_list|()
decl_stmt|;
name|wordBreakTest
operator|.
name|test
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
DECL|method|testSupplementary
specifier|public
name|void
name|testSupplementary
parameter_list|()
throws|throws
name|Exception
block|{
name|BaseTokenStreamTestCase
operator|.
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ð©¬è±éä¹æ¯ç"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ð©¬"
block|,
literal|"è±"
block|,
literal|"é"
block|,
literal|"ä¹"
block|,
literal|"æ¯"
block|,
literal|"ç"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<IDEOGRAPHIC>"
block|,
literal|"<IDEOGRAPHIC>"
block|,
literal|"<IDEOGRAPHIC>"
block|,
literal|"<IDEOGRAPHIC>"
block|,
literal|"<IDEOGRAPHIC>"
block|,
literal|"<IDEOGRAPHIC>"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
