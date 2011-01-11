begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|File
import|;
end_import
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
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|index
operator|.
name|codecs
operator|.
name|CodecProvider
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
name|Sort
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
name|SortField
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
name|TopDocs
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
name|FSDirectory
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
name|MockDirectoryWrapper
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
name|NamedThreadFactory
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
name|BytesRef
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
name|LineFileDocs
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
comment|// TODO
end_comment
begin_comment
comment|//   - mix in optimize, addIndexes
end_comment
begin_comment
comment|//   - randomoly mix in non-congruent docs
end_comment
begin_class
DECL|class|TestNRTThreads
specifier|public
class|class
name|TestNRTThreads
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testNRTThreads
specifier|public
name|void
name|testNRTThreads
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|t0
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|getDefaultFieldCodec
argument_list|()
operator|.
name|equals
argument_list|(
literal|"SimpleText"
argument_list|)
condition|)
block|{
comment|// no
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|setDefaultFieldCodec
argument_list|(
literal|"Standard"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LineFileDocs
name|docs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|)
decl_stmt|;
specifier|final
name|File
name|tempDir
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"nrtopenfiles"
argument_list|)
decl_stmt|;
specifier|final
name|MockDirectoryWrapper
name|dir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|,
name|FSDirectory
operator|.
name|open
argument_list|(
name|tempDir
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setMergedSegmentWarmer
argument_list|(
operator|new
name|IndexWriter
operator|.
name|IndexReaderWarmer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|warm
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now warm merged reader="
operator|+
name|reader
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|delDocs
init|=
name|reader
operator|.
name|getDeletedDocs
argument_list|()
decl_stmt|;
name|int
name|sum
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|inc
init|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|maxDoc
operator|/
literal|50
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|+=
name|inc
control|)
block|{
if|if
condition|(
name|delDocs
operator|==
literal|null
operator|||
operator|!
name|delDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
specifier|final
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|sum
operator|+=
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
name|sum
operator|+=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"united"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: warm visited "
operator|+
name|sum
operator|+
literal|" fields"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|writer
operator|.
name|setInfoStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
name|MergeScheduler
name|ms
init|=
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
decl_stmt|;
if|if
condition|(
name|ms
operator|instanceof
name|ConcurrentMergeScheduler
condition|)
block|{
comment|// try to keep max file open count down
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|ms
operator|)
operator|.
name|setMaxThreadCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|ms
operator|)
operator|.
name|setMaxMergeCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|LogMergePolicy
name|lmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|lmp
operator|.
name|getMergeFactor
argument_list|()
operator|>
literal|5
condition|)
block|{
name|lmp
operator|.
name|setMergeFactor
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|NUM_INDEX_THREADS
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|NUM_SEARCH_THREADS
init|=
literal|3
decl_stmt|;
specifier|final
name|int
name|RUN_TIME_SEC
init|=
name|LuceneTestCase
operator|.
name|TEST_NIGHTLY
condition|?
literal|300
else|:
literal|5
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|addCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|delCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|delIDs
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|long
name|stopTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|RUN_TIME_SEC
operator|*
literal|1000
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|NUM_INDEX_THREADS
index|]
decl_stmt|;
for|for
control|(
name|int
name|thread
init|=
literal|0
init|;
name|thread
operator|<
name|NUM_INDEX_THREADS
condition|;
name|thread
operator|++
control|)
block|{
name|threads
index|[
name|thread
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|toDeleteIDs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopTime
operator|&&
operator|!
name|failed
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
comment|//System.out.println(Thread.currentThread().getName() + ": add doc id:" + doc.get("id"));
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we use update but it never replaces a
comment|// prior doc
if|if
condition|(
name|VERBOSE
condition|)
block|{
comment|//System.out.println(Thread.currentThread().getName() + ": update doc id:" + doc.get("id"));
block|}
name|writer
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|3
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
comment|//System.out.println(Thread.currentThread().getName() + ": buffer del id:" + doc.get("id"));
block|}
name|toDeleteIDs
operator|.
name|add
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
operator|==
literal|17
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": apply "
operator|+
name|toDeleteIDs
operator|.
name|size
argument_list|()
operator|+
literal|" deletes"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|id
range|:
name|toDeleteIDs
control|)
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|count
init|=
name|delCount
operator|.
name|addAndGet
argument_list|(
name|toDeleteIDs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": tot "
operator|+
name|count
operator|+
literal|" deletes"
argument_list|)
expr_stmt|;
block|}
name|delIDs
operator|.
name|addAll
argument_list|(
name|toDeleteIDs
argument_list|)
expr_stmt|;
name|toDeleteIDs
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|addCount
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": hit exc"
argument_list|)
expr_stmt|;
name|exc
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|exc
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": indexing done"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|threads
index|[
name|thread
index|]
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|threads
index|[
name|thread
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: DONE start indexing threads ["
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|)
operator|+
literal|" ms]"
argument_list|)
expr_stmt|;
block|}
comment|// let index build up a bit
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|boolean
name|any
init|=
literal|false
decl_stmt|;
comment|// silly starting guess:
specifier|final
name|AtomicInteger
name|totTermCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|ExecutorService
name|es
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|NamedThreadFactory
argument_list|(
literal|"NRT search threads"
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopTime
operator|&&
operator|!
name|failed
operator|.
name|get
argument_list|()
condition|)
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now reopen r="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|r2
init|=
name|r
operator|.
name|reopen
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|!=
name|r2
condition|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|r2
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now close reader="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|openDeletedFiles
init|=
name|dir
operator|.
name|getOpenDeletedFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|openDeletedFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OBD files: "
operator|+
name|openDeletedFiles
argument_list|)
expr_stmt|;
block|}
name|any
operator||=
name|openDeletedFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
expr_stmt|;
comment|//assertEquals("open but deleted: " + openDeletedFiles, 0, openDeletedFiles.size());
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now open"
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: got new reader="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("numDocs=" + r.numDocs() + "
comment|//openDelFileCount=" + dir.openDeleteFileCount());
name|smokeTestReader
argument_list|(
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|numDocs
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|,
name|es
argument_list|)
decl_stmt|;
comment|// run search threads
specifier|final
name|long
name|searchStopTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|500
decl_stmt|;
specifier|final
name|Thread
index|[]
name|searchThreads
init|=
operator|new
name|Thread
index|[
name|NUM_SEARCH_THREADS
index|]
decl_stmt|;
specifier|final
name|AtomicInteger
name|totHits
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|thread
init|=
literal|0
init|;
name|thread
operator|<
name|NUM_SEARCH_THREADS
condition|;
name|thread
operator|++
control|)
block|{
name|searchThreads
index|[
name|thread
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|TermsEnum
name|termsEnum
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|s
operator|.
name|getIndexReader
argument_list|()
argument_list|,
literal|"body"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|seenTermCount
init|=
literal|0
decl_stmt|;
name|int
name|shift
decl_stmt|;
name|int
name|trigger
decl_stmt|;
if|if
condition|(
name|totTermCount
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|)
block|{
name|shift
operator|=
literal|0
expr_stmt|;
name|trigger
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|shift
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|totTermCount
operator|.
name|get
argument_list|()
operator|/
literal|10
argument_list|)
expr_stmt|;
name|trigger
operator|=
name|totTermCount
operator|.
name|get
argument_list|()
operator|/
literal|10
expr_stmt|;
block|}
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|searchStopTime
condition|)
block|{
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|seenTermCount
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|totTermCount
operator|.
name|set
argument_list|(
name|seenTermCount
argument_list|)
expr_stmt|;
name|seenTermCount
operator|=
literal|0
expr_stmt|;
name|trigger
operator|=
name|totTermCount
operator|.
name|get
argument_list|()
operator|/
literal|10
expr_stmt|;
comment|//System.out.println("trigger " + trigger);
name|shift
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|totTermCount
operator|.
name|get
argument_list|()
operator|/
literal|10
argument_list|)
expr_stmt|;
name|termsEnum
operator|.
name|seek
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|seenTermCount
operator|++
expr_stmt|;
comment|// search 10 terms
if|if
condition|(
name|trigger
operator|==
literal|0
condition|)
block|{
name|trigger
operator|=
literal|1
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|seenTermCount
operator|+
name|shift
operator|)
operator|%
name|trigger
operator|==
literal|0
condition|)
block|{
comment|//if (VERBOSE) {
comment|//System.out.println(Thread.currentThread().getName() + " now search body:" + term.utf8ToString());
comment|//}
name|totHits
operator|.
name|addAndGet
argument_list|(
name|runQuery
argument_list|(
name|s
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
name|term
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": search done"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|failed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
name|searchThreads
index|[
name|thread
index|]
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|searchThreads
index|[
name|thread
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|thread
init|=
literal|0
init|;
name|thread
operator|<
name|NUM_SEARCH_THREADS
condition|;
name|thread
operator|++
control|)
block|{
name|searchThreads
index|[
name|thread
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: DONE search: totHits="
operator|+
name|totHits
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
name|es
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|es
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: all searching done ["
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|)
operator|+
literal|" ms]"
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("numDocs=" + r.numDocs() + " openDelFileCount=" + dir.openDeleteFileCount());
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|openDeletedFiles
init|=
name|dir
operator|.
name|getOpenDeletedFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|openDeletedFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OBD files: "
operator|+
name|openDeletedFiles
argument_list|)
expr_stmt|;
block|}
name|any
operator||=
name|openDeletedFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
expr_stmt|;
name|assertFalse
argument_list|(
literal|"saw non-zero open-but-deleted count"
argument_list|,
name|any
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now join"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|thread
init|=
literal|0
init|;
name|thread
operator|<
name|NUM_INDEX_THREADS
condition|;
name|thread
operator|++
control|)
block|{
name|threads
index|[
name|thread
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: done join ["
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|)
operator|+
literal|" ms]; addCount="
operator|+
name|addCount
operator|+
literal|" delCount="
operator|+
name|delCount
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexReader
name|r2
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
specifier|final
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r2
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|delIDs
control|)
block|{
specifier|final
name|TopDocs
name|hits
init|=
name|s
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|hits
operator|.
name|totalHits
operator|!=
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"doc id="
operator|+
name|id
operator|+
literal|" is supposed to be deleted, but got docID="
operator|+
name|hits
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"index="
operator|+
name|writer
operator|.
name|segString
argument_list|()
operator|+
literal|" addCount="
operator|+
name|addCount
operator|+
literal|" delCount="
operator|+
name|delCount
argument_list|,
name|addCount
operator|.
name|get
argument_list|()
operator|-
name|delCount
operator|.
name|get
argument_list|()
argument_list|,
name|r2
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"index="
operator|+
name|writer
operator|.
name|segString
argument_list|()
operator|+
literal|" addCount="
operator|+
name|addCount
operator|+
literal|" delCount="
operator|+
name|delCount
argument_list|,
name|addCount
operator|.
name|get
argument_list|()
operator|-
name|delCount
operator|.
name|get
argument_list|()
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
name|docs
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: done ["
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|t0
operator|)
operator|+
literal|" ms]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|runQuery
specifier|private
name|int
name|runQuery
parameter_list|(
name|IndexSearcher
name|s
parameter_list|,
name|Query
name|q
parameter_list|)
throws|throws
name|Exception
block|{
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
expr_stmt|;
return|return
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"title"
argument_list|,
name|SortField
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
operator|.
name|totalHits
return|;
block|}
DECL|method|smokeTestReader
specifier|private
name|void
name|smokeTestReader
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|runQuery
argument_list|(
name|s
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"united"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|runQuery
argument_list|(
name|s
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"titleTokenized"
argument_list|,
literal|"states"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"united"
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"states"
argument_list|)
argument_list|)
expr_stmt|;
name|runQuery
argument_list|(
name|s
argument_list|,
name|pq
argument_list|)
expr_stmt|;
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
