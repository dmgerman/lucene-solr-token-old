begin_unit
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
begin_comment
comment|/**  * Simple tests to ensure the Arabic filter Factories are working.  */
end_comment
begin_class
DECL|class|TestArabicFilters
specifier|public
class|class
name|TestArabicFilters
extends|extends
name|BaseTokenTestCase
block|{
comment|/**    * Test ArabicLetterTokenizerFactory    * @deprecated (3.1) Remove in Lucene 5.0    */
annotation|@
name|Deprecated
DECL|method|testTokenizer
specifier|public
name|void
name|testTokenizer
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Ø§ÙØ°ÙÙ ÙÙÙÙØª Ø£ÙÙØ§ÙÙÙ"
argument_list|)
decl_stmt|;
name|ArabicLetterTokenizerFactory
name|factory
init|=
operator|new
name|ArabicLetterTokenizerFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|Tokenizer
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø§ÙØ°ÙÙ"
block|,
literal|"ÙÙÙÙØª"
block|,
literal|"Ø£ÙÙØ§ÙÙÙ"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test ArabicNormalizationFilterFactory    */
DECL|method|testNormalizer
specifier|public
name|void
name|testNormalizer
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Ø§ÙØ°ÙÙ ÙÙÙÙØª Ø£ÙÙØ§ÙÙÙ"
argument_list|)
decl_stmt|;
name|StandardTokenizerFactory
name|factory
init|=
operator|new
name|StandardTokenizerFactory
argument_list|()
decl_stmt|;
name|ArabicNormalizationFilterFactory
name|filterFactory
init|=
operator|new
name|ArabicNormalizationFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|filterFactory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|filterFactory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø§ÙØ°ÙÙ"
block|,
literal|"ÙÙÙØª"
block|,
literal|"Ø§ÙÙØ§ÙÙÙ"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test ArabicStemFilterFactory    */
DECL|method|testStemmer
specifier|public
name|void
name|testStemmer
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"Ø§ÙØ°ÙÙ ÙÙÙÙØª Ø£ÙÙØ§ÙÙÙ"
argument_list|)
decl_stmt|;
name|StandardTokenizerFactory
name|factory
init|=
operator|new
name|StandardTokenizerFactory
argument_list|()
decl_stmt|;
name|ArabicNormalizationFilterFactory
name|normFactory
init|=
operator|new
name|ArabicNormalizationFilterFactory
argument_list|()
decl_stmt|;
name|ArabicStemFilterFactory
name|stemFactory
init|=
operator|new
name|ArabicStemFilterFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|normFactory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
name|factory
operator|.
name|create
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|normFactory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|stream
operator|=
name|stemFactory
operator|.
name|create
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø°ÙÙ"
block|,
literal|"ÙÙÙØª"
block|,
literal|"Ø§ÙÙØ§ÙÙÙ"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test PersianCharFilterFactory    */
DECL|method|testPersianCharFilter
specifier|public
name|void
name|testPersianCharFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"ÙÛâØ®ÙØ±Ø¯"
argument_list|)
decl_stmt|;
name|PersianCharFilterFactory
name|charfilterFactory
init|=
operator|new
name|PersianCharFilterFactory
argument_list|()
decl_stmt|;
name|StandardTokenizerFactory
name|tokenizerFactory
init|=
operator|new
name|StandardTokenizerFactory
argument_list|()
decl_stmt|;
name|tokenizerFactory
operator|.
name|init
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
expr_stmt|;
name|TokenStream
name|stream
init|=
name|tokenizerFactory
operator|.
name|create
argument_list|(
name|charfilterFactory
operator|.
name|create
argument_list|(
name|CharReader
operator|.
name|get
argument_list|(
name|reader
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÙÛ"
block|,
literal|"Ø®ÙØ±Ø¯"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
