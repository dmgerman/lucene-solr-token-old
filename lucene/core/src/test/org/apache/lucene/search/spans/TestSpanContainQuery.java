begin_unit
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|IndexReader
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
name|RandomIndexWriter
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
name|CheckHits
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
name|IndexSearcher
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
name|store
operator|.
name|Directory
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
begin_import
import|import static
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
name|SpanTestUtil
operator|.
name|*
import|;
end_import
begin_class
DECL|class|TestSpanContainQuery
specifier|public
class|class
name|TestSpanContainQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|directory
name|Directory
name|directory
decl_stmt|;
DECL|field|field
specifier|static
specifier|final
name|String
name|field
init|=
literal|"field"
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
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|field
argument_list|,
name|docFields
index|[
name|i
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
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
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
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
DECL|field|docFields
name|String
index|[]
name|docFields
init|=
block|{
literal|"w1 w2 w3 w4 w5"
block|,
literal|"w1 w3 w2 w3"
block|,
literal|"w1 xx w2 yy w3"
block|,
literal|"w1 w3 xx w2 yy w3"
block|,   }
decl_stmt|;
DECL|method|checkHits
name|void
name|checkHits
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
index|[]
name|results
parameter_list|)
throws|throws
name|Exception
block|{
name|CheckHits
operator|.
name|checkHits
argument_list|(
name|random
argument_list|()
argument_list|,
name|query
argument_list|,
name|field
argument_list|,
name|searcher
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
DECL|method|makeSpans
name|Spans
name|makeSpans
parameter_list|(
name|SpanQuery
name|sq
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|MultiSpansWrapper
operator|.
name|wrap
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|sq
argument_list|)
return|;
block|}
DECL|method|tstEqualSpans
name|void
name|tstEqualSpans
parameter_list|(
name|String
name|mes
parameter_list|,
name|SpanQuery
name|expectedQ
parameter_list|,
name|SpanQuery
name|actualQ
parameter_list|)
throws|throws
name|Exception
block|{
name|Spans
name|expected
init|=
name|makeSpans
argument_list|(
name|expectedQ
argument_list|)
decl_stmt|;
name|Spans
name|actual
init|=
name|makeSpans
argument_list|(
name|actualQ
argument_list|)
decl_stmt|;
name|tstEqualSpans
argument_list|(
name|mes
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
DECL|method|tstEqualSpans
name|void
name|tstEqualSpans
parameter_list|(
name|String
name|mes
parameter_list|,
name|Spans
name|expected
parameter_list|,
name|Spans
name|actual
parameter_list|)
throws|throws
name|Exception
block|{
while|while
condition|(
name|expected
operator|.
name|nextDoc
argument_list|()
operator|!=
name|Spans
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|docID
argument_list|()
argument_list|,
name|actual
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|docID
argument_list|()
argument_list|,
name|actual
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|expected
operator|.
name|nextStartPosition
argument_list|()
operator|!=
name|Spans
operator|.
name|NO_MORE_POSITIONS
condition|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|startPosition
argument_list|()
argument_list|,
name|actual
operator|.
name|nextStartPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"start"
argument_list|,
name|expected
operator|.
name|startPosition
argument_list|()
argument_list|,
name|actual
operator|.
name|startPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"end"
argument_list|,
name|expected
operator|.
name|endPosition
argument_list|()
argument_list|,
name|actual
operator|.
name|endPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSpanContainTerm
specifier|public
name|void
name|testSpanContainTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|stq
init|=
name|spanTermQuery
argument_list|(
name|field
argument_list|,
literal|"w3"
argument_list|)
decl_stmt|;
name|SpanQuery
name|containingQ
init|=
name|spanContainingQuery
argument_list|(
name|stq
argument_list|,
name|stq
argument_list|)
decl_stmt|;
name|SpanQuery
name|containedQ
init|=
name|spanWithinQuery
argument_list|(
name|stq
argument_list|,
name|stq
argument_list|)
decl_stmt|;
name|tstEqualSpans
argument_list|(
literal|"containing"
argument_list|,
name|stq
argument_list|,
name|containingQ
argument_list|)
expr_stmt|;
name|tstEqualSpans
argument_list|(
literal|"containing"
argument_list|,
name|stq
argument_list|,
name|containedQ
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanContainPhraseBothWords
specifier|public
name|void
name|testSpanContainPhraseBothWords
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|w2
init|=
literal|"w2"
decl_stmt|;
name|String
name|w3
init|=
literal|"w3"
decl_stmt|;
name|SpanQuery
name|phraseQ
init|=
name|spanNearOrderedQuery
argument_list|(
name|field
argument_list|,
literal|0
argument_list|,
name|w2
argument_list|,
name|w3
argument_list|)
decl_stmt|;
name|SpanQuery
name|w23
init|=
name|spanOrQuery
argument_list|(
name|field
argument_list|,
name|w2
argument_list|,
name|w3
argument_list|)
decl_stmt|;
name|SpanQuery
name|containingPhraseOr
init|=
name|spanContainingQuery
argument_list|(
name|phraseQ
argument_list|,
name|w23
argument_list|)
decl_stmt|;
name|SpanQuery
name|containedPhraseOr
init|=
name|spanWithinQuery
argument_list|(
name|phraseQ
argument_list|,
name|w23
argument_list|)
decl_stmt|;
name|tstEqualSpans
argument_list|(
literal|"containing phrase or"
argument_list|,
name|phraseQ
argument_list|,
name|containingPhraseOr
argument_list|)
expr_stmt|;
name|Spans
name|spans
init|=
name|makeSpans
argument_list|(
name|containedPhraseOr
argument_list|)
decl_stmt|;
name|assertNext
argument_list|(
name|spans
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertNext
argument_list|(
name|spans
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertNext
argument_list|(
name|spans
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertNext
argument_list|(
name|spans
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertFinished
argument_list|(
name|spans
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanContainPhraseFirstWord
specifier|public
name|void
name|testSpanContainPhraseFirstWord
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|w2
init|=
literal|"w2"
decl_stmt|;
name|String
name|w3
init|=
literal|"w3"
decl_stmt|;
name|SpanQuery
name|stqw2
init|=
name|spanTermQuery
argument_list|(
name|field
argument_list|,
name|w2
argument_list|)
decl_stmt|;
name|SpanQuery
name|phraseQ
init|=
name|spanNearOrderedQuery
argument_list|(
name|field
argument_list|,
literal|0
argument_list|,
name|w2
argument_list|,
name|w3
argument_list|)
decl_stmt|;
name|SpanQuery
name|containingPhraseW2
init|=
name|spanContainingQuery
argument_list|(
name|phraseQ
argument_list|,
name|stqw2
argument_list|)
decl_stmt|;
name|SpanQuery
name|containedPhraseW2
init|=
name|spanWithinQuery
argument_list|(
name|phraseQ
argument_list|,
name|stqw2
argument_list|)
decl_stmt|;
name|tstEqualSpans
argument_list|(
literal|"containing phrase w2"
argument_list|,
name|phraseQ
argument_list|,
name|containingPhraseW2
argument_list|)
expr_stmt|;
name|Spans
name|spans
init|=
name|makeSpans
argument_list|(
name|containedPhraseW2
argument_list|)
decl_stmt|;
name|assertNext
argument_list|(
name|spans
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertNext
argument_list|(
name|spans
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertFinished
argument_list|(
name|spans
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanContainPhraseSecondWord
specifier|public
name|void
name|testSpanContainPhraseSecondWord
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|w2
init|=
literal|"w2"
decl_stmt|;
name|String
name|w3
init|=
literal|"w3"
decl_stmt|;
name|SpanQuery
name|stqw3
init|=
name|spanTermQuery
argument_list|(
name|field
argument_list|,
name|w3
argument_list|)
decl_stmt|;
name|SpanQuery
name|phraseQ
init|=
name|spanNearOrderedQuery
argument_list|(
name|field
argument_list|,
literal|0
argument_list|,
name|w2
argument_list|,
name|w3
argument_list|)
decl_stmt|;
name|SpanQuery
name|containingPhraseW3
init|=
name|spanContainingQuery
argument_list|(
name|phraseQ
argument_list|,
name|stqw3
argument_list|)
decl_stmt|;
name|SpanQuery
name|containedPhraseW3
init|=
name|spanWithinQuery
argument_list|(
name|phraseQ
argument_list|,
name|stqw3
argument_list|)
decl_stmt|;
name|tstEqualSpans
argument_list|(
literal|"containing phrase w3"
argument_list|,
name|phraseQ
argument_list|,
name|containingPhraseW3
argument_list|)
expr_stmt|;
name|Spans
name|spans
init|=
name|makeSpans
argument_list|(
name|containedPhraseW3
argument_list|)
decl_stmt|;
name|assertNext
argument_list|(
name|spans
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertNext
argument_list|(
name|spans
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertFinished
argument_list|(
name|spans
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
