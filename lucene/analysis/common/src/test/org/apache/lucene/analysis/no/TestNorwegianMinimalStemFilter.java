begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.no
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|no
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|VocabularyAssert
operator|.
name|*
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|no
operator|.
name|NorwegianLightStemmer
operator|.
name|BOKMAAL
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|no
operator|.
name|NorwegianLightStemmer
operator|.
name|NYNORSK
import|;
end_import
begin_comment
comment|/**  * Simple tests for {@link NorwegianMinimalStemFilter}  */
end_comment
begin_class
DECL|class|TestNorwegianMinimalStemFilter
specifier|public
class|class
name|TestNorwegianMinimalStemFilter
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
name|NorwegianMinimalStemFilter
argument_list|(
name|source
argument_list|,
name|BOKMAAL
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
comment|/** Test against a BokmÃ¥l vocabulary file */
DECL|method|testVocabulary
specifier|public
name|void
name|testVocabulary
parameter_list|()
throws|throws
name|IOException
block|{
name|assertVocabulary
argument_list|(
name|analyzer
argument_list|,
name|Files
operator|.
name|newInputStream
argument_list|(
name|getDataPath
argument_list|(
literal|"nb_minimal.txt"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test against a Nynorsk vocabulary file */
DECL|method|testNynorskVocabulary
specifier|public
name|void
name|testNynorskVocabulary
parameter_list|()
throws|throws
name|IOException
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
name|NorwegianMinimalStemFilter
argument_list|(
name|source
argument_list|,
name|NYNORSK
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertVocabulary
argument_list|(
name|analyzer
argument_list|,
name|Files
operator|.
name|newInputStream
argument_list|(
name|getDataPath
argument_list|(
literal|"nn_minimal.txt"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testKeyword
specifier|public
name|void
name|testKeyword
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|CharArraySet
name|exclusionSet
init|=
operator|new
name|CharArraySet
argument_list|(
name|asSet
argument_list|(
literal|"sekretÃ¦ren"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|TokenStream
name|sink
init|=
operator|new
name|SetKeywordMarkerFilter
argument_list|(
name|source
argument_list|,
name|exclusionSet
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
operator|new
name|NorwegianMinimalStemFilter
argument_list|(
name|sink
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
literal|"sekretÃ¦ren"
argument_list|,
literal|"sekretÃ¦ren"
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
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
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
name|NorwegianMinimalStemFilter
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
