begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.icu
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
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Normalizer2
import|;
end_import
begin_comment
comment|/**  * Tests the ICUNormalizer2Filter  */
end_comment
begin_class
DECL|class|TestICUNormalizer2Filter
specifier|public
class|class
name|TestICUNormalizer2Filter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|a
name|Analyzer
name|a
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
name|a
operator|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
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
name|tokenizer
argument_list|,
operator|new
name|ICUNormalizer2Filter
argument_list|(
name|tokenizer
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
name|a
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
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|IOException
block|{
comment|// case folding
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"This is a test"
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
block|}
argument_list|)
expr_stmt|;
comment|// case folding
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"RuÃ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"russ"
block|}
argument_list|)
expr_stmt|;
comment|// case folding
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÎÎÎªÎÎ£"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Î¼Î¬ÏÎ¿Ï"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÎÎ¬ÏÎ¿Ï"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Î¼Î¬ÏÎ¿Ï"
block|}
argument_list|)
expr_stmt|;
comment|// supplementary case folding
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ð"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ð¾"
block|}
argument_list|)
expr_stmt|;
comment|// normalization
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ï´³ï´ºï°§"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø·ÙØ·ÙØ·Ù"
block|}
argument_list|)
expr_stmt|;
comment|// removal of default ignorables
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"à¤à¥âà¤·"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¤à¥à¤·"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testAlternate
specifier|public
name|void
name|testAlternate
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
specifier|public
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
name|tokenizer
argument_list|,
operator|new
name|ICUNormalizer2Filter
argument_list|(
name|tokenizer
argument_list|,
comment|/* specify nfc with decompose to get nfd */
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|"nfc"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|DECOMPOSE
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// decompose EAcute into E + combining Acute
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"\u00E9"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"\u0065\u0301"
block|}
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
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
name|a
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
name|ICUNormalizer2Filter
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
