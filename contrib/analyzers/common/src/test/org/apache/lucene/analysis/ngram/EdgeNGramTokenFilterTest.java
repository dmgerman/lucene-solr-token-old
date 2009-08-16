begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ngram
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ngram
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|WhitespaceTokenizer
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
name|tokenattributes
operator|.
name|TermAttribute
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_comment
comment|/**  * Tests {@link EdgeNGramTokenFilter} for correctness.  */
end_comment
begin_class
DECL|class|EdgeNGramTokenFilterTest
specifier|public
class|class
name|EdgeNGramTokenFilterTest
extends|extends
name|TestCase
block|{
DECL|field|input
specifier|private
name|TokenStream
name|input
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|input
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"abcde"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidInput
specifier|public
name|void
name|testInvalidInput
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|input
argument_list|,
name|EdgeNGramTokenFilter
operator|.
name|Side
operator|.
name|FRONT
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gotException
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidInput2
specifier|public
name|void
name|testInvalidInput2
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|input
argument_list|,
name|EdgeNGramTokenFilter
operator|.
name|Side
operator|.
name|FRONT
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gotException
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidInput3
specifier|public
name|void
name|testInvalidInput3
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|gotException
init|=
literal|false
decl_stmt|;
try|try
block|{
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|input
argument_list|,
name|EdgeNGramTokenFilter
operator|.
name|Side
operator|.
name|FRONT
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|gotException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|gotException
argument_list|)
expr_stmt|;
block|}
DECL|method|testFrontUnigram
specifier|public
name|void
name|testFrontUnigram
parameter_list|()
throws|throws
name|Exception
block|{
name|EdgeNGramTokenFilter
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|input
argument_list|,
name|EdgeNGramTokenFilter
operator|.
name|Side
operator|.
name|FRONT
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|tokenizer
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(a,0,1)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBackUnigram
specifier|public
name|void
name|testBackUnigram
parameter_list|()
throws|throws
name|Exception
block|{
name|EdgeNGramTokenFilter
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|input
argument_list|,
name|EdgeNGramTokenFilter
operator|.
name|Side
operator|.
name|BACK
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|tokenizer
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(e,4,5)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOversizedNgrams
specifier|public
name|void
name|testOversizedNgrams
parameter_list|()
throws|throws
name|Exception
block|{
name|EdgeNGramTokenFilter
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|input
argument_list|,
name|EdgeNGramTokenFilter
operator|.
name|Side
operator|.
name|FRONT
argument_list|,
literal|6
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFrontRangeOfNgrams
specifier|public
name|void
name|testFrontRangeOfNgrams
parameter_list|()
throws|throws
name|Exception
block|{
name|EdgeNGramTokenFilter
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|input
argument_list|,
name|EdgeNGramTokenFilter
operator|.
name|Side
operator|.
name|FRONT
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|tokenizer
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(a,0,1)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(ab,0,2)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(abc,0,3)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBackRangeOfNgrams
specifier|public
name|void
name|testBackRangeOfNgrams
parameter_list|()
throws|throws
name|Exception
block|{
name|EdgeNGramTokenFilter
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|input
argument_list|,
name|EdgeNGramTokenFilter
operator|.
name|Side
operator|.
name|BACK
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|tokenizer
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(e,4,5)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(de,3,5)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(cde,2,5)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSmallTokenInStream
specifier|public
name|void
name|testSmallTokenInStream
parameter_list|()
throws|throws
name|Exception
block|{
name|input
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"abc de fgh"
argument_list|)
argument_list|)
expr_stmt|;
name|EdgeNGramTokenFilter
name|tokenizer
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|input
argument_list|,
name|EdgeNGramTokenFilter
operator|.
name|Side
operator|.
name|FRONT
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|tokenizer
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(abc,0,3)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(fgh,0,3)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testReset
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
name|WhitespaceTokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"abcde"
argument_list|)
argument_list|)
decl_stmt|;
name|EdgeNGramTokenFilter
name|filter
init|=
operator|new
name|EdgeNGramTokenFilter
argument_list|(
name|tokenizer
argument_list|,
name|EdgeNGramTokenFilter
operator|.
name|Side
operator|.
name|FRONT
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|filter
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(a,0,1)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(ab,0,2)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tokenizer
operator|.
name|reset
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"abcde"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(a,0,1)"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
