begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.commongrams
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|commongrams
package|;
end_package
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
name|*
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
name|CharTermAttribute
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
comment|/**  * Tests CommonGrams(Query)Filter  */
end_comment
begin_class
DECL|class|CommonGramsFilterTest
specifier|public
class|class
name|CommonGramsFilterTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|commonWords
specifier|private
specifier|static
specifier|final
name|CharArraySet
name|commonWords
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
literal|"s"
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|,
literal|"d"
argument_list|,
literal|"the"
argument_list|,
literal|"of"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|method|testReset
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|input
init|=
literal|"How the s a brown s cow d like A B thing?"
decl_stmt|;
name|WhitespaceTokenizer
name|wt
init|=
operator|new
name|WhitespaceTokenizer
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
name|CommonGramsFilter
name|cgf
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|wt
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|term
init|=
name|cgf
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cgf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"How"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cgf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"How_the"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cgf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cgf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the_s"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|wt
operator|.
name|reset
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|cgf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|cgf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"How"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueryReset
specifier|public
name|void
name|testQueryReset
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|input
init|=
literal|"How the s a brown s cow d like A B thing?"
decl_stmt|;
name|WhitespaceTokenizer
name|wt
init|=
operator|new
name|WhitespaceTokenizer
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
name|CommonGramsFilter
name|cgf
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|wt
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
name|CommonGramsQueryFilter
name|nsf
init|=
operator|new
name|CommonGramsQueryFilter
argument_list|(
name|cgf
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|term
init|=
name|wt
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nsf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"How_the"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nsf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the_s"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|wt
operator|.
name|reset
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|nsf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|nsf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"How_the"
argument_list|,
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is for testing CommonGramsQueryFilter which outputs a set of tokens    * optimized for querying with only one token at each position, either a    * unigram or a bigram It also will not return a token for the final position    * if the final word is already in the preceding bigram Example:(three    * tokens/positions in)    * "foo bar the"=>"foo:1|bar:2,bar-the:2|the:3=> "foo" "bar-the" (2 tokens    * out)    *     * @return Map<String,String>    */
DECL|method|testCommonGramsQueryFilter
specifier|public
name|void
name|testCommonGramsQueryFilter
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
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|field
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|in
argument_list|,
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
name|CommonGramsQueryFilter
argument_list|(
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
name|commonWords
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// Stop words used below are "of" "the" and "s"
comment|// two word queries
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"brown fox"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"brown"
block|,
literal|"fox"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"the fox"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the_fox"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"fox of"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"fox_of"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"of the"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"of_the"
block|}
argument_list|)
expr_stmt|;
comment|// one word queries
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"the"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|}
argument_list|)
expr_stmt|;
comment|// 3 word combinations s=stopword/common word n=not a stop word
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"n n n"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"n"
block|,
literal|"n"
block|,
literal|"n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"quick brown fox"
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
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"n n s"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"n"
block|,
literal|"n_s"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"quick brown the"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quick"
block|,
literal|"brown_the"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"n s n"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"n_s"
block|,
literal|"s_n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"quick the brown"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quick_the"
block|,
literal|"the_brown"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"n s s"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"n_s"
block|,
literal|"s_s"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"fox of the"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"fox_of"
block|,
literal|"of_the"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"s n n"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s_n"
block|,
literal|"n"
block|,
literal|"n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"the quick brown"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the_quick"
block|,
literal|"quick"
block|,
literal|"brown"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"s n s"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s_n"
block|,
literal|"n_s"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"the fox of"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the_fox"
block|,
literal|"fox_of"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"s s n"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s_s"
block|,
literal|"s_n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"of the fox"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"of_the"
block|,
literal|"the_fox"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"s s s"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s_s"
block|,
literal|"s_s"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"of the of"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"of_the"
block|,
literal|"the_of"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCommonGramsFilter
specifier|public
name|void
name|testCommonGramsFilter
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
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|field
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|in
argument_list|,
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
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|tokenizer
argument_list|,
name|commonWords
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// Stop words used below are "of" "the" and "s"
comment|// one word queries
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"the"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"foo"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|}
argument_list|)
expr_stmt|;
comment|// two word queries
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"brown fox"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"brown"
block|,
literal|"fox"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"the fox"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the"
block|,
literal|"the_fox"
block|,
literal|"fox"
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
name|a
argument_list|,
literal|"fox of"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"fox"
block|,
literal|"fox_of"
block|,
literal|"of"
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
name|a
argument_list|,
literal|"of the"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"of"
block|,
literal|"of_the"
block|,
literal|"the"
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
comment|// 3 word combinations s=stopword/common word n=not a stop word
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"n n n"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"n"
block|,
literal|"n"
block|,
literal|"n"
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
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"quick brown fox"
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
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"n n s"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"n"
block|,
literal|"n"
block|,
literal|"n_s"
block|,
literal|"s"
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
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"quick brown the"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quick"
block|,
literal|"brown"
block|,
literal|"brown_the"
block|,
literal|"the"
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
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"n s n"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"n"
block|,
literal|"n_s"
block|,
literal|"s"
block|,
literal|"s_n"
block|,
literal|"n"
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"quick the fox"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"quick"
block|,
literal|"quick_the"
block|,
literal|"the"
block|,
literal|"the_fox"
block|,
literal|"fox"
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"n s s"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"n"
block|,
literal|"n_s"
block|,
literal|"s"
block|,
literal|"s_s"
block|,
literal|"s"
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"fox of the"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"fox"
block|,
literal|"fox_of"
block|,
literal|"of"
block|,
literal|"of_the"
block|,
literal|"the"
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"s n n"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s"
block|,
literal|"s_n"
block|,
literal|"n"
block|,
literal|"n"
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
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"the quick brown"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the"
block|,
literal|"the_quick"
block|,
literal|"quick"
block|,
literal|"brown"
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
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"s n s"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s"
block|,
literal|"s_n"
block|,
literal|"n"
block|,
literal|"n_s"
block|,
literal|"s"
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"the fox of"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the"
block|,
literal|"the_fox"
block|,
literal|"fox"
block|,
literal|"fox_of"
block|,
literal|"of"
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"s s n"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s"
block|,
literal|"s_s"
block|,
literal|"s"
block|,
literal|"s_n"
block|,
literal|"n"
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"of the fox"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"of"
block|,
literal|"of_the"
block|,
literal|"the"
block|,
literal|"the_fox"
block|,
literal|"fox"
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"s s s"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"s"
block|,
literal|"s_s"
block|,
literal|"s"
block|,
literal|"s_s"
block|,
literal|"s"
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"of the of"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"of"
block|,
literal|"of_the"
block|,
literal|"the"
block|,
literal|"the_of"
block|,
literal|"of"
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that CommonGramsFilter works correctly in case-insensitive mode    */
DECL|method|testCaseSensitive
specifier|public
name|void
name|testCaseSensitive
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|input
init|=
literal|"How The s a brown s cow d like A B thing?"
decl_stmt|;
name|MockTokenizer
name|wt
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TokenFilter
name|cgf
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|wt
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|cgf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"How"
block|,
literal|"The"
block|,
literal|"The_s"
block|,
literal|"s"
block|,
literal|"s_a"
block|,
literal|"a"
block|,
literal|"a_brown"
block|,
literal|"brown"
block|,
literal|"brown_s"
block|,
literal|"s"
block|,
literal|"s_cow"
block|,
literal|"cow"
block|,
literal|"cow_d"
block|,
literal|"d"
block|,
literal|"d_like"
block|,
literal|"like"
block|,
literal|"A"
block|,
literal|"B"
block|,
literal|"thing?"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test CommonGramsQueryFilter in the case that the last word is a stopword    */
DECL|method|testLastWordisStopWord
specifier|public
name|void
name|testLastWordisStopWord
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|input
init|=
literal|"dog the"
decl_stmt|;
name|MockTokenizer
name|wt
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|CommonGramsFilter
name|cgf
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|wt
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
name|TokenFilter
name|nsf
init|=
operator|new
name|CommonGramsQueryFilter
argument_list|(
name|cgf
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|nsf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dog_the"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test CommonGramsQueryFilter in the case that the first word is a stopword    */
DECL|method|testFirstWordisStopWord
specifier|public
name|void
name|testFirstWordisStopWord
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|input
init|=
literal|"the dog"
decl_stmt|;
name|MockTokenizer
name|wt
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|CommonGramsFilter
name|cgf
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|wt
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
name|TokenFilter
name|nsf
init|=
operator|new
name|CommonGramsQueryFilter
argument_list|(
name|cgf
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|nsf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the_dog"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test CommonGramsQueryFilter in the case of a single (stop)word query    */
DECL|method|testOneWordQueryStopWord
specifier|public
name|void
name|testOneWordQueryStopWord
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|input
init|=
literal|"the"
decl_stmt|;
name|MockTokenizer
name|wt
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|CommonGramsFilter
name|cgf
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|wt
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
name|TokenFilter
name|nsf
init|=
operator|new
name|CommonGramsQueryFilter
argument_list|(
name|cgf
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|nsf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test CommonGramsQueryFilter in the case of a single word query    */
DECL|method|testOneWordQuery
specifier|public
name|void
name|testOneWordQuery
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|input
init|=
literal|"monster"
decl_stmt|;
name|MockTokenizer
name|wt
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|CommonGramsFilter
name|cgf
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|wt
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
name|TokenFilter
name|nsf
init|=
operator|new
name|CommonGramsQueryFilter
argument_list|(
name|cgf
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|nsf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"monster"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test CommonGramsQueryFilter when first and last words are stopwords.    */
DECL|method|TestFirstAndLastStopWord
specifier|public
name|void
name|TestFirstAndLastStopWord
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|input
init|=
literal|"the of"
decl_stmt|;
name|MockTokenizer
name|wt
init|=
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|CommonGramsFilter
name|cgf
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|wt
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
name|TokenFilter
name|nsf
init|=
operator|new
name|CommonGramsQueryFilter
argument_list|(
name|cgf
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|nsf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"the_of"
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
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|t
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
literal|false
argument_list|)
decl_stmt|;
name|CommonGramsFilter
name|cgf
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|t
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
name|cgf
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|a
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|Analyzer
name|b
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
name|t
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
literal|false
argument_list|)
decl_stmt|;
name|CommonGramsFilter
name|cgf
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|t
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|t
argument_list|,
operator|new
name|CommonGramsQueryFilter
argument_list|(
name|cgf
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|b
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
