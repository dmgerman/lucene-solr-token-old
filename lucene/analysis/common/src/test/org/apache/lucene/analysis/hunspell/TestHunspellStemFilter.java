begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
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
name|java
operator|.
name|io
operator|.
name|InputStream
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|hunspell
operator|.
name|Dictionary
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
name|hunspell
operator|.
name|HunspellStemFilter
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
name|IOUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_class
DECL|class|TestHunspellStemFilter
specifier|public
class|class
name|TestHunspellStemFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|dictionary
specifier|private
specifier|static
name|Dictionary
name|dictionary
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
comment|// no multiple try-with to workaround bogus VerifyError
name|InputStream
name|affixStream
init|=
name|TestStemmer
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"simple.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|TestStemmer
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"simple.dic"
argument_list|)
decl_stmt|;
try|try
block|{
name|dictionary
operator|=
operator|new
name|Dictionary
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|dictionary
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Simple test for KeywordAttribute */
DECL|method|testKeywordAttribute
specifier|public
name|void
name|testKeywordAttribute
parameter_list|()
throws|throws
name|IOException
block|{
name|MockTokenizer
name|tokenizer
init|=
name|whitespaceMockTokenizer
argument_list|(
literal|"lucene is awesome"
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setEnableChecks
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|HunspellStemFilter
name|filter
init|=
operator|new
name|HunspellStemFilter
argument_list|(
name|tokenizer
argument_list|,
name|dictionary
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
literal|"lucene"
block|,
literal|"lucen"
block|,
literal|"is"
block|,
literal|"awesome"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
comment|// assert with keyword marker
name|tokenizer
operator|=
name|whitespaceMockTokenizer
argument_list|(
literal|"lucene is awesome"
argument_list|)
expr_stmt|;
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"Lucene"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|filter
operator|=
operator|new
name|HunspellStemFilter
argument_list|(
operator|new
name|SetKeywordMarkerFilter
argument_list|(
name|tokenizer
argument_list|,
name|set
argument_list|)
argument_list|,
name|dictionary
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lucene"
block|,
literal|"is"
block|,
literal|"awesome"
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
comment|/** simple test for longestOnly option */
DECL|method|testLongestOnly
specifier|public
name|void
name|testLongestOnly
parameter_list|()
throws|throws
name|IOException
block|{
name|MockTokenizer
name|tokenizer
init|=
name|whitespaceMockTokenizer
argument_list|(
literal|"lucene is awesome"
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setEnableChecks
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|HunspellStemFilter
name|filter
init|=
operator|new
name|HunspellStemFilter
argument_list|(
name|tokenizer
argument_list|,
name|dictionary
argument_list|,
literal|true
argument_list|,
literal|true
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
literal|"lucene"
block|,
literal|"is"
block|,
literal|"awesome"
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
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
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
name|HunspellStemFilter
argument_list|(
name|tokenizer
argument_list|,
name|dictionary
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
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
name|HunspellStemFilter
argument_list|(
name|tokenizer
argument_list|,
name|dictionary
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
block|}
DECL|method|testIgnoreCaseNoSideEffects
specifier|public
name|void
name|testIgnoreCaseNoSideEffects
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Dictionary
name|d
decl_stmt|;
comment|// no multiple try-with to workaround bogus VerifyError
name|InputStream
name|affixStream
init|=
name|TestStemmer
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"simple.aff"
argument_list|)
decl_stmt|;
name|InputStream
name|dictStream
init|=
name|TestStemmer
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"simple.dic"
argument_list|)
decl_stmt|;
try|try
block|{
name|d
operator|=
operator|new
name|Dictionary
argument_list|(
name|affixStream
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|dictStream
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|affixStream
argument_list|,
name|dictStream
argument_list|)
expr_stmt|;
block|}
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
name|HunspellStemFilter
argument_list|(
name|tokenizer
argument_list|,
name|d
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
literal|"NoChAnGy"
argument_list|,
literal|"NoChAnGy"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
