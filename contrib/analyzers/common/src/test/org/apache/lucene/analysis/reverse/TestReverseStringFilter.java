begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"Do have a nice day"
argument_list|)
argument_list|)
decl_stmt|;
comment|// 1-4 length string
name|ReverseStringFilter
name|filter
init|=
operator|new
name|ReverseStringFilter
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|TermAttribute
name|text
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
literal|"oD"
argument_list|,
name|text
operator|.
name|term
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
literal|"evah"
argument_list|,
name|text
operator|.
name|term
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
literal|"a"
argument_list|,
name|text
operator|.
name|term
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
literal|"ecin"
argument_list|,
name|text
operator|.
name|term
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
literal|"yad"
argument_list|,
name|text
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
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
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"Do have a nice day"
argument_list|)
argument_list|)
decl_stmt|;
comment|// 1-4 length string
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
name|TermAttribute
name|text
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
literal|"\u0001oD"
argument_list|,
name|text
operator|.
name|term
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
literal|"\u0001evah"
argument_list|,
name|text
operator|.
name|term
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
literal|"\u0001a"
argument_list|,
name|text
operator|.
name|term
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
literal|"\u0001ecin"
argument_list|,
name|text
operator|.
name|term
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
literal|"\u0001yad"
argument_list|,
name|text
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filter
operator|.
name|incrementToken
argument_list|()
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
block|}
end_class
end_unit
