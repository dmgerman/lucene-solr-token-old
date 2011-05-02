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
name|index
operator|.
name|SerialMergeScheduler
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
name|_TestUtil
import|;
end_import
begin_class
DECL|class|TestCachingSpanFilter
specifier|public
class|class
name|TestCachingSpanFilter
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEnforceDeletions
specifier|public
name|void
name|testEnforceDeletions
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
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
operator|.
comment|// asserts below requires no unexpected merges:
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// NOTE: cannot use writer.getReader because RIW (on
comment|// flipping a coin) may give us a newly opened reader,
comment|// but we use .reopen on this reader below and expect to
comment|// (must) get an NRT reader:
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|writer
operator|.
name|w
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// same reason we don't wrap?
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// add a doc, refresh the reader, and check that its there
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
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
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
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|SpanFilter
name|startFilter
init|=
operator|new
name|SpanQueryFilter
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// ignore deletions
name|CachingSpanFilter
name|filter
init|=
operator|new
name|CachingSpanFilter
argument_list|(
name|startFilter
argument_list|,
name|CachingWrapperFilter
operator|.
name|DeletesMode
operator|.
name|IGNORE
argument_list|)
decl_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|filter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[query + filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|ConstantScoreQuery
name|constantScore
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// now delete the doc, refresh the reader, and see that
comment|// it's not there
name|_TestUtil
operator|.
name|keepFullyDeletedSegments
argument_list|(
name|writer
operator|.
name|w
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|filter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[query + filter] Should *not* find a hit..."
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// force cache to regenerate:
name|filter
operator|=
operator|new
name|CachingSpanFilter
argument_list|(
name|startFilter
argument_list|,
name|CachingWrapperFilter
operator|.
name|DeletesMode
operator|.
name|RECACHE
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|filter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[query + filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|constantScore
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// NOTE: important to hold ref here so GC doesn't clear
comment|// the cache entry!  Else the assert below may sometimes
comment|// fail:
name|IndexReader
name|oldReader
init|=
name|reader
decl_stmt|;
comment|// make sure we get a cache hit when we reopen readers
comment|// that had no new deletions
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|!=
name|oldReader
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|missCount
init|=
name|filter
operator|.
name|missCount
decl_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should find a hit..."
argument_list|,
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|missCount
argument_list|,
name|filter
operator|.
name|missCount
argument_list|)
expr_stmt|;
comment|// now delete the doc, refresh the reader, and see that it's not there
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|=
name|refreshReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|filter
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[query + filter] Should *not* find a hit..."
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|docs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|constantScore
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[just filter] Should *not* find a hit..."
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// NOTE: silliness to make sure JRE does not optimize
comment|// away our holding onto oldReader to prevent
comment|// CachingWrapperFilter's WeakHashMap from dropping the
comment|// entry:
name|assertTrue
argument_list|(
name|oldReader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
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
DECL|method|refreshReader
specifier|private
specifier|static
name|IndexReader
name|refreshReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|oldReader
init|=
name|reader
decl_stmt|;
name|reader
operator|=
name|reader
operator|.
name|reopen
argument_list|()
expr_stmt|;
if|if
condition|(
name|reader
operator|!=
name|oldReader
condition|)
block|{
name|oldReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|reader
return|;
block|}
block|}
end_class
end_unit
