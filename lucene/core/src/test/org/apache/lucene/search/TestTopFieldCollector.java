begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
operator|.
name|Store
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
name|NumericDocValuesField
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
name|StringField
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
name|LeafReaderContext
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
name|FieldValueHitQueue
operator|.
name|Entry
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
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import
begin_class
DECL|class|TestTopFieldCollector
specifier|public
class|class
name|TestTopFieldCollector
extends|extends
name|LuceneTestCase
block|{
DECL|field|is
specifier|private
name|IndexSearcher
name|is
decl_stmt|;
DECL|field|ir
specifier|private
name|IndexReader
name|ir
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
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
name|numDocs
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
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|ir
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|is
operator|=
name|newSearcher
argument_list|(
name|ir
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
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
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
DECL|method|testSortWithoutFillFields
specifier|public
name|void
name|testSortWithoutFillFields
parameter_list|()
throws|throws
name|Exception
block|{
comment|// There was previously a bug in TopFieldCollector when fillFields was set
comment|// to false - the same doc and score was set in ScoreDoc[] array. This test
comment|// asserts that if fillFields is false, the documents are set properly. It
comment|// does not use Searcher's default search methods (with Sort) since all set
comment|// fillFields to true.
name|Sort
index|[]
name|sort
init|=
operator|new
name|Sort
index|[]
block|{
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_DOC
argument_list|)
block|,
operator|new
name|Sort
argument_list|()
block|}
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
name|sort
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|TopDocsCollector
argument_list|<
name|Entry
argument_list|>
name|tdc
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
index|[
name|i
index|]
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|is
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|tdc
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|tdc
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|sd
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|sd
index|[
name|j
index|]
operator|.
name|doc
operator|!=
name|sd
index|[
name|j
operator|-
literal|1
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSortWithoutScoreTracking
specifier|public
name|void
name|testSortWithoutScoreTracking
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Two Sort criteria to instantiate the multi/single comparators.
name|Sort
index|[]
name|sort
init|=
operator|new
name|Sort
index|[]
block|{
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_DOC
argument_list|)
block|,
operator|new
name|Sort
argument_list|()
block|}
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
name|sort
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|TopDocsCollector
argument_list|<
name|Entry
argument_list|>
name|tdc
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
index|[
name|i
index|]
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|is
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|tdc
argument_list|)
expr_stmt|;
name|TopDocs
name|td
init|=
name|tdc
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|td
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sd
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|Float
operator|.
name|isNaN
argument_list|(
name|sd
index|[
name|j
index|]
operator|.
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|Float
operator|.
name|isNaN
argument_list|(
name|td
operator|.
name|getMaxScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortWithScoreNoMaxScoreTracking
specifier|public
name|void
name|testSortWithScoreNoMaxScoreTracking
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Two Sort criteria to instantiate the multi/single comparators.
name|Sort
index|[]
name|sort
init|=
operator|new
name|Sort
index|[]
block|{
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_DOC
argument_list|)
block|,
operator|new
name|Sort
argument_list|()
block|}
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
name|sort
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|TopDocsCollector
argument_list|<
name|Entry
argument_list|>
name|tdc
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
index|[
name|i
index|]
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|is
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|tdc
argument_list|)
expr_stmt|;
name|TopDocs
name|td
init|=
name|tdc
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|td
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sd
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|sd
index|[
name|j
index|]
operator|.
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|Float
operator|.
name|isNaN
argument_list|(
name|td
operator|.
name|getMaxScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// MultiComparatorScoringNoMaxScoreCollector
DECL|method|testSortWithScoreNoMaxScoreTrackingMulti
specifier|public
name|void
name|testSortWithScoreNoMaxScoreTrackingMulti
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Two Sort criteria to instantiate the multi/single comparators.
name|Sort
index|[]
name|sort
init|=
operator|new
name|Sort
index|[]
block|{
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_DOC
argument_list|,
name|SortField
operator|.
name|FIELD_SCORE
argument_list|)
block|}
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
name|sort
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|TopDocsCollector
argument_list|<
name|Entry
argument_list|>
name|tdc
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
index|[
name|i
index|]
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|is
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|tdc
argument_list|)
expr_stmt|;
name|TopDocs
name|td
init|=
name|tdc
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|td
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sd
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|sd
index|[
name|j
index|]
operator|.
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|Float
operator|.
name|isNaN
argument_list|(
name|td
operator|.
name|getMaxScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortWithScoreAndMaxScoreTracking
specifier|public
name|void
name|testSortWithScoreAndMaxScoreTracking
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Two Sort criteria to instantiate the multi/single comparators.
name|Sort
index|[]
name|sort
init|=
operator|new
name|Sort
index|[]
block|{
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_DOC
argument_list|)
block|,
operator|new
name|Sort
argument_list|()
block|}
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
name|sort
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|TopDocsCollector
argument_list|<
name|Entry
argument_list|>
name|tdc
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
index|[
name|i
index|]
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|is
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|tdc
argument_list|)
expr_stmt|;
name|TopDocs
name|td
init|=
name|tdc
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|td
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sd
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|sd
index|[
name|j
index|]
operator|.
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|td
operator|.
name|getMaxScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortWithScoreAndMaxScoreTrackingNoResults
specifier|public
name|void
name|testSortWithScoreAndMaxScoreTrackingNoResults
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Two Sort criteria to instantiate the multi/single comparators.
name|Sort
index|[]
name|sort
init|=
operator|new
name|Sort
index|[]
block|{
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_DOC
argument_list|)
block|,
operator|new
name|Sort
argument_list|()
block|}
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
name|sort
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TopDocsCollector
argument_list|<
name|Entry
argument_list|>
name|tdc
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
index|[
name|i
index|]
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|tdc
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Float
operator|.
name|isNaN
argument_list|(
name|td
operator|.
name|getMaxScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testComputeScoresOnlyOnce
specifier|public
name|void
name|testComputeScoresOnlyOnce
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|StringField
name|text
init|=
operator|new
name|StringField
argument_list|(
literal|"text"
argument_list|,
literal|"foo"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|NumericDocValuesField
name|relevance
init|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"relevance"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|relevance
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|text
operator|.
name|setStringValue
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|text
operator|.
name|setStringValue
argument_list|(
literal|"baz"
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Query
name|foo
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|bar
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|foo
operator|=
operator|new
name|BoostQuery
argument_list|(
name|foo
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Query
name|baz
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
decl_stmt|;
name|baz
operator|=
operator|new
name|BoostQuery
argument_list|(
name|baz
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
name|foo
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|add
argument_list|(
name|bar
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|add
argument_list|(
name|baz
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
for|for
control|(
name|Sort
name|sort
range|:
operator|new
name|Sort
index|[]
block|{
operator|new
name|Sort
argument_list|(
name|SortField
operator|.
name|FIELD_SCORE
argument_list|)
block|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"f"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
argument_list|)
block|}
control|)
block|{
for|for
control|(
name|boolean
name|doDocScores
range|:
operator|new
name|boolean
index|[]
block|{
literal|false
block|,
literal|true
block|}
control|)
block|{
for|for
control|(
name|boolean
name|doMaxScore
range|:
operator|new
name|boolean
index|[]
block|{
literal|false
block|,
literal|true
block|}
control|)
block|{
specifier|final
name|TopFieldCollector
name|topCollector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|true
argument_list|,
name|doDocScores
argument_list|,
name|doMaxScore
argument_list|)
decl_stmt|;
specifier|final
name|Collector
name|assertingCollector
init|=
operator|new
name|Collector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafCollector
name|in
init|=
name|topCollector
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterLeafCollector
argument_list|(
name|in
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
specifier|final
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|s
init|=
operator|new
name|Scorer
argument_list|(
literal|null
argument_list|)
block|{
name|int
name|lastComputedDoc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|lastComputedDoc
operator|==
name|docID
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Score computed twice on "
operator|+
name|docID
argument_list|()
argument_list|)
throw|;
block|}
name|lastComputedDoc
operator|=
name|docID
argument_list|()
expr_stmt|;
return|return
name|scorer
operator|.
name|score
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|scorer
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
name|scorer
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|super
operator|.
name|setScorer
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
name|topCollector
operator|.
name|needsScores
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|assertingCollector
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
