begin_unit
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|PhraseQuery
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|TermQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanNearQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanTermQuery
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
name|LuceneTestCase
import|;
end_import
begin_class
DECL|class|MissesTest
specifier|public
class|class
name|MissesTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testTermQuery
specifier|public
name|void
name|testTermQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
try|try
init|(
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
init|)
block|{
specifier|final
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"this is a<B>foo</B> bar example"
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|analyzer
argument_list|,
literal|"test"
argument_list|,
literal|"this is a foo bar example"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|analyzer
argument_list|,
literal|"test"
argument_list|,
literal|"this does not match"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testBooleanQuery
specifier|public
name|void
name|testBooleanQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
try|try
init|(
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
init|)
block|{
specifier|final
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"this is a<B>foo</B><B>bar</B> example"
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|analyzer
argument_list|,
literal|"test"
argument_list|,
literal|"this is a foo bar example"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|analyzer
argument_list|,
literal|"test"
argument_list|,
literal|"this does not match"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPhraseQuery
specifier|public
name|void
name|testPhraseQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
try|try
init|(
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
init|)
block|{
specifier|final
name|PhraseQuery
name|query
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"this is a<B>foo</B><B>bar</B> example"
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|analyzer
argument_list|,
literal|"test"
argument_list|,
literal|"this is a foo bar example"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|analyzer
argument_list|,
literal|"test"
argument_list|,
literal|"this does not match"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSpanNearQuery
specifier|public
name|void
name|testSpanNearQuery
parameter_list|()
throws|throws
name|IOException
throws|,
name|InvalidTokenOffsetsException
block|{
try|try
init|(
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
init|)
block|{
specifier|final
name|Query
name|query
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"test"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Highlighter
name|highlighter
init|=
operator|new
name|Highlighter
argument_list|(
operator|new
name|SimpleHTMLFormatter
argument_list|()
argument_list|,
operator|new
name|QueryScorer
argument_list|(
name|query
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"this is a<B>foo</B><B>bar</B> example"
argument_list|,
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|analyzer
argument_list|,
literal|"test"
argument_list|,
literal|"this is a foo bar example"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|analyzer
argument_list|,
literal|"test"
argument_list|,
literal|"this does not match"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
