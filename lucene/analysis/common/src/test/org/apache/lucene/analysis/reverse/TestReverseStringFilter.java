begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.reverse
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|reverse
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
name|core
operator|.
name|KeywordTokenizer
import|;
end_import
begin_class
DECL|class|TestReverseStringFilter
specifier|public
class|class
name|TestReverseStringFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testFilter
specifier|public
name|void
name|testFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|stream
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
comment|// 1-4 length string
operator|(
operator|(
name|Tokenizer
operator|)
name|stream
operator|)
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"Do have a nice day"
argument_list|)
argument_list|)
expr_stmt|;
name|ReverseStringFilter
name|filter
init|=
operator|new
name|ReverseStringFilter
argument_list|(
name|stream
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
literal|"oD"
block|,
literal|"evah"
block|,
literal|"a"
block|,
literal|"ecin"
block|,
literal|"yad"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFilterWithMark
specifier|public
name|void
name|testFilterWithMark
parameter_list|()
throws|throws
name|Exception
block|{
name|TokenStream
name|stream
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
comment|// 1-4 length string
operator|(
operator|(
name|Tokenizer
operator|)
name|stream
operator|)
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"Do have a nice day"
argument_list|)
argument_list|)
expr_stmt|;
name|ReverseStringFilter
name|filter
init|=
operator|new
name|ReverseStringFilter
argument_list|(
name|stream
argument_list|,
literal|'\u0001'
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
literal|"\u0001oD"
block|,
literal|"\u0001evah"
block|,
literal|"\u0001a"
block|,
literal|"\u0001ecin"
block|,
literal|"\u0001yad"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReverseString
specifier|public
name|void
name|testReverseString
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"A"
argument_list|,
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"BA"
argument_list|,
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
literal|"AB"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"CBA"
argument_list|,
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
literal|"ABC"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReverseChar
specifier|public
name|void
name|testReverseChar
parameter_list|()
throws|throws
name|Exception
block|{
name|char
index|[]
name|buffer
init|=
block|{
literal|'A'
block|,
literal|'B'
block|,
literal|'C'
block|,
literal|'D'
block|,
literal|'E'
block|,
literal|'F'
block|}
decl_stmt|;
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
name|buffer
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ABEDCF"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReverseSupplementary
specifier|public
name|void
name|testReverseSupplementary
parameter_list|()
throws|throws
name|Exception
block|{
comment|// supplementary at end
name|assertEquals
argument_list|(
literal|"ð©¬è±éä¹æ¯ç"
argument_list|,
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
literal|"çæ¯ä¹éè±ð©¬"
argument_list|)
argument_list|)
expr_stmt|;
comment|// supplementary at end - 1
name|assertEquals
argument_list|(
literal|"að©¬è±éä¹æ¯ç"
argument_list|,
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
literal|"çæ¯ä¹éè±ð©¬a"
argument_list|)
argument_list|)
expr_stmt|;
comment|// supplementary at start
name|assertEquals
argument_list|(
literal|"fedcbað©¬"
argument_list|,
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
literal|"ð©¬abcdef"
argument_list|)
argument_list|)
expr_stmt|;
comment|// supplementary at start + 1
name|assertEquals
argument_list|(
literal|"fedcbað©¬z"
argument_list|,
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
literal|"zð©¬abcdef"
argument_list|)
argument_list|)
expr_stmt|;
comment|// supplementary medial
name|assertEquals
argument_list|(
literal|"gfeð©¬dcba"
argument_list|,
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
literal|"abcdð©¬efg"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReverseSupplementaryChar
specifier|public
name|void
name|testReverseSupplementaryChar
parameter_list|()
throws|throws
name|Exception
block|{
comment|// supplementary at end
name|char
index|[]
name|buffer
init|=
literal|"abcçæ¯ä¹éè±ð©¬"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
name|buffer
argument_list|,
literal|3
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abcð©¬è±éä¹æ¯ç"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
comment|// supplementary at end - 1
name|buffer
operator|=
literal|"abcçæ¯ä¹éè±ð©¬d"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
name|buffer
argument_list|,
literal|3
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abcdð©¬è±éä¹æ¯ç"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
comment|// supplementary at start
name|buffer
operator|=
literal|"abcð©¬çæ¯ä¹éè±"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
name|buffer
argument_list|,
literal|3
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abcè±éä¹æ¯çð©¬"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
comment|// supplementary at start + 1
name|buffer
operator|=
literal|"abcdð©¬çæ¯ä¹éè±"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
name|buffer
argument_list|,
literal|3
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abcè±éä¹æ¯çð©¬d"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
comment|// supplementary medial
name|buffer
operator|=
literal|"abcçæ¯ð©¬def"
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|ReverseStringFilter
operator|.
name|reverse
argument_list|(
name|buffer
argument_list|,
literal|3
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abcfedð©¬æ¯ç"
argument_list|,
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
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
name|ReverseStringFilter
argument_list|(
name|tokenizer
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
name|ReverseStringFilter
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
block|}
block|}
end_class
end_unit
