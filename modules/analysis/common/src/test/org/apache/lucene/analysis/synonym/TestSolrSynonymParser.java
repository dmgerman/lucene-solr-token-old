begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.synonym
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|synonym
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
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|MockAnalyzer
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
name|en
operator|.
name|EnglishAnalyzer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/**  * Tests parser for the Solr synonyms format  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TestSolrSynonymParser
specifier|public
class|class
name|TestSolrSynonymParser
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/** Tests some simple examples from the solr wiki */
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testFile
init|=
literal|"i-pod, ipod, ipoooood\n"
operator|+
literal|"foo => foo bar\n"
operator|+
literal|"foo => baz\n"
operator|+
literal|"this test, that testing"
decl_stmt|;
name|SolrSynonymParser
name|parser
init|=
operator|new
name|SolrSynonymParser
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|StringReader
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SynonymMap
name|map
init|=
name|parser
operator|.
name|build
argument_list|()
decl_stmt|;
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
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|SynonymFilter
argument_list|(
name|tokenizer
argument_list|,
name|map
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ball"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ball"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"i-pod"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"i-pod"
block|,
literal|"ipod"
block|,
literal|"ipoooood"
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
literal|0
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"foo"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"baz"
block|,
literal|"bar"
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
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"this test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"that"
block|,
literal|"test"
block|,
literal|"testing"
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
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** parse a syn file with bad syntax */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ParseException
operator|.
name|class
argument_list|)
DECL|method|testInvalidDoubleMap
specifier|public
name|void
name|testInvalidDoubleMap
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testFile
init|=
literal|"a => b => c"
decl_stmt|;
name|SolrSynonymParser
name|parser
init|=
operator|new
name|SolrSynonymParser
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|StringReader
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** parse a syn file with bad syntax */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ParseException
operator|.
name|class
argument_list|)
DECL|method|testInvalidAnalyzesToNothingOutput
specifier|public
name|void
name|testInvalidAnalyzesToNothingOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testFile
init|=
literal|"a => 1"
decl_stmt|;
name|SolrSynonymParser
name|parser
init|=
operator|new
name|SolrSynonymParser
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|StringReader
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** parse a syn file with bad syntax */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ParseException
operator|.
name|class
argument_list|)
DECL|method|testInvalidAnalyzesToNothingInput
specifier|public
name|void
name|testInvalidAnalyzesToNothingInput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testFile
init|=
literal|"1 => a"
decl_stmt|;
name|SolrSynonymParser
name|parser
init|=
operator|new
name|SolrSynonymParser
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|StringReader
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** parse a syn file with bad syntax */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ParseException
operator|.
name|class
argument_list|)
DECL|method|testInvalidPositionsInput
specifier|public
name|void
name|testInvalidPositionsInput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testFile
init|=
literal|"testola => the test"
decl_stmt|;
name|SolrSynonymParser
name|parser
init|=
operator|new
name|SolrSynonymParser
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
operator|new
name|EnglishAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|StringReader
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** parse a syn file with bad syntax */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ParseException
operator|.
name|class
argument_list|)
DECL|method|testInvalidPositionsOutput
specifier|public
name|void
name|testInvalidPositionsOutput
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testFile
init|=
literal|"the test => testola"
decl_stmt|;
name|SolrSynonymParser
name|parser
init|=
operator|new
name|SolrSynonymParser
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
operator|new
name|EnglishAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|StringReader
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** parse a syn file with some escaped syntax chars */
DECL|method|testEscapedStuff
specifier|public
name|void
name|testEscapedStuff
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testFile
init|=
literal|"a\\=>a => b\\=>b\n"
operator|+
literal|"a\\,a => b\\,b"
decl_stmt|;
name|SolrSynonymParser
name|parser
init|=
operator|new
name|SolrSynonymParser
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|parser
operator|.
name|add
argument_list|(
operator|new
name|StringReader
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SynonymMap
name|map
init|=
name|parser
operator|.
name|build
argument_list|()
decl_stmt|;
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
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
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
name|SynonymFilter
argument_list|(
name|tokenizer
argument_list|,
name|map
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"ball"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ball"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"a=>a"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"b=>b"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"a,a"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"b,b"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
