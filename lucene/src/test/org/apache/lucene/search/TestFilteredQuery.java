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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|TextField
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
operator|.
name|AtomicReaderContext
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
name|Bits
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
name|DocIdBitSet
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
begin_comment
comment|/**  * FilteredQuery JUnit tests.  *  *<p>Created: Apr 21, 2004 1:21:46 PM  *  *  * @since   1.4  */
end_comment
begin_class
DECL|class|TestFilteredQuery
specifier|public
class|class
name|TestFilteredQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|field|filter
specifier|private
name|Filter
name|filter
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
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
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
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"one two three four five"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"sorter"
argument_list|,
literal|"b"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
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
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"one two three four"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"sorter"
argument_list|,
literal|"d"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
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
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"one two three y"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"sorter"
argument_list|,
literal|"a"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
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
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"one two x"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"sorter"
argument_list|,
literal|"c"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
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
comment|// tests here require single segment (eg try seed
comment|// 8239472272678419952L), because SingleDocTestFilter(x)
comment|// blindly accepts that docID in any sub-segment
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
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
name|query
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
name|newStaticFilterB
argument_list|()
expr_stmt|;
block|}
comment|// must be static for serialization tests
DECL|method|newStaticFilterB
specifier|private
specifier|static
name|Filter
name|newStaticFilterB
parameter_list|()
block|{
return|return
operator|new
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
if|if
condition|(
name|acceptDocs
operator|==
literal|null
condition|)
name|acceptDocs
operator|=
operator|new
name|Bits
operator|.
name|MatchAllBits
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|BitSet
name|bitset
init|=
operator|new
name|BitSet
argument_list|(
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
name|acceptDocs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
condition|)
name|bitset
operator|.
name|set
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|acceptDocs
operator|.
name|get
argument_list|(
literal|3
argument_list|)
condition|)
name|bitset
operator|.
name|set
argument_list|(
literal|3
argument_list|)
expr_stmt|;
return|return
operator|new
name|DocIdBitSet
argument_list|(
name|bitset
argument_list|)
return|;
block|}
block|}
return|;
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
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
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
DECL|method|testFilteredQuery
specifier|public
name|void
name|testFilteredQuery
parameter_list|()
throws|throws
name|Exception
block|{
comment|// force the filter to be executed as bits
name|tFilteredQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// force the filter to be executed as iterator
name|tFilteredQuery
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|tFilteredQuery
specifier|private
name|void
name|tFilteredQuery
parameter_list|(
specifier|final
name|boolean
name|useRandomAccess
parameter_list|)
throws|throws
name|Exception
block|{
name|Query
name|filteredquery
init|=
operator|new
name|FilteredQueryRA
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|useRandomAccess
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|filteredquery
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|filteredquery
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|filteredquery
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"sorter"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|filteredquery
operator|=
operator|new
name|FilteredQueryRA
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
name|filter
argument_list|,
name|useRandomAccess
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|filteredquery
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|filteredquery
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|filteredquery
operator|=
operator|new
name|FilteredQueryRA
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"x"
argument_list|)
argument_list|)
argument_list|,
name|filter
argument_list|,
name|useRandomAccess
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|filteredquery
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|filteredquery
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|filteredquery
operator|=
operator|new
name|FilteredQueryRA
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
argument_list|,
name|filter
argument_list|,
name|useRandomAccess
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|filteredquery
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|filteredquery
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
comment|// test boost
name|Filter
name|f
init|=
name|newStaticFilterA
argument_list|()
decl_stmt|;
name|float
name|boost
init|=
literal|2.5f
decl_stmt|;
name|BooleanQuery
name|bq1
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
decl_stmt|;
name|tq
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq2
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|tq
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|filteredquery
operator|=
operator|new
name|FilteredQueryRA
argument_list|(
name|tq
argument_list|,
name|f
argument_list|,
name|useRandomAccess
argument_list|)
expr_stmt|;
name|filteredquery
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|filteredquery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertScoreEquals
argument_list|(
name|bq1
argument_list|,
name|bq2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|boost
argument_list|,
name|filteredquery
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0f
argument_list|,
name|tq
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// the boost value of the underlying query shouldn't have changed
block|}
comment|// must be static for serialization tests
DECL|method|newStaticFilterA
specifier|private
specifier|static
name|Filter
name|newStaticFilterA
parameter_list|()
block|{
return|return
operator|new
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
name|assertNull
argument_list|(
literal|"acceptDocs should be null, as we have an index without deletions"
argument_list|,
name|acceptDocs
argument_list|)
expr_stmt|;
name|BitSet
name|bitset
init|=
operator|new
name|BitSet
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|bitset
operator|.
name|set
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
expr_stmt|;
return|return
operator|new
name|DocIdBitSet
argument_list|(
name|bitset
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Tests whether the scores of the two queries are the same.    */
DECL|method|assertScoreEquals
specifier|public
name|void
name|assertScoreEquals
parameter_list|(
name|Query
name|q1
parameter_list|,
name|Query
name|q2
parameter_list|)
throws|throws
name|Exception
block|{
name|ScoreDoc
index|[]
name|hits1
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q1
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|ScoreDoc
index|[]
name|hits2
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q2
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
name|hits1
operator|.
name|length
argument_list|,
name|hits2
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|hits1
index|[
name|i
index|]
operator|.
name|score
argument_list|,
name|hits2
index|[
name|i
index|]
operator|.
name|score
argument_list|,
literal|0.000001f
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This tests FilteredQuery's rewrite correctness    */
DECL|method|testRangeQuery
specifier|public
name|void
name|testRangeQuery
parameter_list|()
throws|throws
name|Exception
block|{
comment|// force the filter to be executed as bits
name|tRangeQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tRangeQuery
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|tRangeQuery
specifier|private
name|void
name|tRangeQuery
parameter_list|(
specifier|final
name|boolean
name|useRandomAccess
parameter_list|)
throws|throws
name|Exception
block|{
name|TermRangeQuery
name|rq
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"sorter"
argument_list|,
literal|"b"
argument_list|,
literal|"d"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Query
name|filteredquery
init|=
operator|new
name|FilteredQueryRA
argument_list|(
name|rq
argument_list|,
name|filter
argument_list|,
name|useRandomAccess
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|filteredquery
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|filteredquery
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanMUST
specifier|public
name|void
name|testBooleanMUST
parameter_list|()
throws|throws
name|Exception
block|{
comment|// force the filter to be executed as bits
name|tBooleanMUST
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// force the filter to be executed as iterator
name|tBooleanMUST
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|tBooleanMUST
specifier|private
name|void
name|tBooleanMUST
parameter_list|(
specifier|final
name|boolean
name|useRandomAccess
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|FilteredQueryRA
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|SingleDocTestFilter
argument_list|(
literal|0
argument_list|)
argument_list|,
name|useRandomAccess
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|FilteredQueryRA
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|SingleDocTestFilter
argument_list|(
literal|1
argument_list|)
argument_list|,
name|useRandomAccess
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|bq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|testBooleanSHOULD
specifier|public
name|void
name|testBooleanSHOULD
parameter_list|()
throws|throws
name|Exception
block|{
comment|// force the filter to be executed as bits
name|tBooleanSHOULD
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// force the filter to be executed as iterator
name|tBooleanSHOULD
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|tBooleanSHOULD
specifier|private
name|void
name|tBooleanSHOULD
parameter_list|(
specifier|final
name|boolean
name|useRandomAccess
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|FilteredQueryRA
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|SingleDocTestFilter
argument_list|(
literal|0
argument_list|)
argument_list|,
name|useRandomAccess
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|FilteredQueryRA
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|SingleDocTestFilter
argument_list|(
literal|1
argument_list|)
argument_list|,
name|useRandomAccess
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|bq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
comment|// Make sure BooleanQuery, which does out-of-order
comment|// scoring, inside FilteredQuery, works
DECL|method|testBoolean2
specifier|public
name|void
name|testBoolean2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// force the filter to be executed as bits
name|tBoolean2
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// force the filter to be executed as iterator
name|tBoolean2
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|tBoolean2
specifier|private
name|void
name|tBoolean2
parameter_list|(
specifier|final
name|boolean
name|useRandomAccess
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|FilteredQueryRA
argument_list|(
name|bq
argument_list|,
operator|new
name|SingleDocTestFilter
argument_list|(
literal|0
argument_list|)
argument_list|,
name|useRandomAccess
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|testChainedFilters
specifier|public
name|void
name|testChainedFilters
parameter_list|()
throws|throws
name|Exception
block|{
comment|// force the filter to be executed as bits
name|tChainedFilters
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// force the filter to be executed as iterator
name|tChainedFilters
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|tChainedFilters
specifier|private
name|void
name|tChainedFilters
parameter_list|(
specifier|final
name|boolean
name|useRandomAccess
parameter_list|)
throws|throws
name|Exception
block|{
name|Query
name|query
init|=
operator|new
name|TestFilteredQuery
operator|.
name|FilteredQueryRA
argument_list|(
operator|new
name|TestFilteredQuery
operator|.
name|FilteredQueryRA
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|CachingWrapperFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|useRandomAccess
argument_list|)
argument_list|,
operator|new
name|CachingWrapperFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"four"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|useRandomAccess
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
comment|// one more:
name|query
operator|=
operator|new
name|TestFilteredQuery
operator|.
name|FilteredQueryRA
argument_list|(
name|query
argument_list|,
operator|new
name|CachingWrapperFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"five"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|useRandomAccess
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|class|FilteredQueryRA
specifier|public
specifier|static
specifier|final
class|class
name|FilteredQueryRA
extends|extends
name|FilteredQuery
block|{
DECL|field|useRandomAccess
specifier|private
specifier|final
name|boolean
name|useRandomAccess
decl_stmt|;
DECL|method|FilteredQueryRA
specifier|public
name|FilteredQueryRA
parameter_list|(
name|Query
name|q
parameter_list|,
name|Filter
name|f
parameter_list|,
name|boolean
name|useRandomAccess
parameter_list|)
block|{
name|super
argument_list|(
name|q
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|this
operator|.
name|useRandomAccess
operator|=
name|useRandomAccess
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|useRandomAccess
specifier|protected
name|boolean
name|useRandomAccess
parameter_list|(
name|Bits
name|bits
parameter_list|,
name|int
name|firstFilterDoc
parameter_list|)
block|{
return|return
name|useRandomAccess
return|;
block|}
block|}
block|}
end_class
end_unit
