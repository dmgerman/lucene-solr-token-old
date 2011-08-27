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
name|IOException
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
name|CountDownLatch
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
name|document
operator|.
name|FieldType
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
name|store
operator|.
name|AlreadyClosedException
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
name|ThreadInterruptedException
import|;
end_import
begin_comment
comment|/**  * MultiThreaded IndexWriter tests  */
end_comment
begin_class
DECL|class|TestIndexWriterWithThreads
specifier|public
class|class
name|TestIndexWriterWithThreads
extends|extends
name|LuceneTestCase
block|{
comment|// Used by test cases below
DECL|class|IndexerThread
specifier|private
class|class
name|IndexerThread
extends|extends
name|Thread
block|{
DECL|field|diskFull
name|boolean
name|diskFull
decl_stmt|;
DECL|field|error
name|Throwable
name|error
decl_stmt|;
DECL|field|ace
name|AlreadyClosedException
name|ace
decl_stmt|;
DECL|field|writer
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|noErrors
name|boolean
name|noErrors
decl_stmt|;
DECL|field|addCount
specifier|volatile
name|int
name|addCount
decl_stmt|;
DECL|method|IndexerThread
specifier|public
name|IndexerThread
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|boolean
name|noErrors
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|noErrors
operator|=
name|noErrors
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"aaa bbb ccc ddd eee fff ggg hhh iii jjj"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|idUpto
init|=
literal|0
decl_stmt|;
name|int
name|fullCount
init|=
literal|0
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
literal|200
decl_stmt|;
do|do
block|{
try|try
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
operator|(
name|idUpto
operator|++
operator|)
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|addCount
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
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
literal|"TEST: expected exc:"
argument_list|)
expr_stmt|;
name|ioe
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println(Thread.currentThread().getName() + ": hit exc");
comment|//ioe.printStackTrace(System.out);
if|if
condition|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"fake disk full at"
argument_list|)
operator|||
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"now failing on purpose"
argument_list|)
condition|)
block|{
name|diskFull
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
if|if
condition|(
name|fullCount
operator|++
operator|>=
literal|5
condition|)
break|break;
block|}
else|else
block|{
if|if
condition|(
name|noErrors
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
literal|": ERROR: unexpected IOException:"
argument_list|)
expr_stmt|;
name|ioe
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|error
operator|=
name|ioe
expr_stmt|;
block|}
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|//t.printStackTrace(System.out);
if|if
condition|(
name|noErrors
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
literal|": ERROR: unexpected Throwable:"
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
name|error
operator|=
name|t
expr_stmt|;
block|}
break|break;
block|}
block|}
do|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopTime
condition|)
do|;
block|}
block|}
comment|// LUCENE-1130: make sure immediate disk full on creating
comment|// an IndexWriter (hit during DW.ThreadState.init()), with
comment|// multiple threads, is OK:
DECL|method|testImmediateDiskFullWithThreads
specifier|public
name|void
name|testImmediateDiskFullWithThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|NUM_THREADS
init|=
literal|3
decl_stmt|;
specifier|final
name|int
name|numIterations
init|=
name|TEST_NIGHTLY
condition|?
literal|10
else|:
literal|3
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|numIterations
condition|;
name|iter
operator|++
control|)
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
literal|"\nTEST: iter="
operator|+
name|iter
argument_list|)
expr_stmt|;
block|}
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
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
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
operator|)
operator|.
name|setSuppressExceptions
argument_list|()
expr_stmt|;
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|4
operator|*
literal|1024
operator|+
literal|20
operator|*
name|iter
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setInfoStream
argument_list|(
name|VERBOSE
condition|?
name|System
operator|.
name|out
else|:
literal|null
argument_list|)
expr_stmt|;
name|IndexerThread
index|[]
name|threads
init|=
operator|new
name|IndexerThread
index|[
name|NUM_THREADS
index|]
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|IndexerThread
argument_list|(
name|writer
argument_list|,
literal|true
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
comment|// Without fix for LUCENE-1130: one of the
comment|// threads will hang
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hit unexpected Throwable"
argument_list|,
name|threads
index|[
name|i
index|]
operator|.
name|error
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// Make sure once disk space is avail again, we can
comment|// cleanly close:
name|dir
operator|.
name|setMaxSizeInBytes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// LUCENE-1130: make sure we can close() even while
comment|// threads are trying to add documents.  Strictly
comment|// speaking, this isn't valid us of Lucene's APIs, but we
comment|// still want to be robust to this case:
DECL|method|testCloseWithThreads
specifier|public
name|void
name|testCloseWithThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|NUM_THREADS
init|=
literal|3
decl_stmt|;
name|int
name|numIterations
init|=
name|TEST_NIGHTLY
condition|?
literal|7
else|:
literal|3
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|numIterations
condition|;
name|iter
operator|++
control|)
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
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
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
operator|)
operator|.
name|setSuppressExceptions
argument_list|()
expr_stmt|;
name|IndexerThread
index|[]
name|threads
init|=
operator|new
name|IndexerThread
index|[
name|NUM_THREADS
index|]
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|IndexerThread
argument_list|(
name|writer
argument_list|,
literal|false
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
name|boolean
name|done
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|done
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
comment|// only stop when at least one thread has added a doc
if|if
condition|(
name|threads
index|[
name|i
index|]
operator|.
name|addCount
operator|>
literal|0
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
operator|!
name|threads
index|[
name|i
index|]
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"thread failed before indexing a single document"
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|close
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Make sure threads that are adding docs are not hung:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
comment|// Without fix for LUCENE-1130: one of the
comment|// threads will hang
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
if|if
condition|(
name|threads
index|[
name|i
index|]
operator|.
name|isAlive
argument_list|()
condition|)
name|fail
argument_list|(
literal|"thread seems to be hung"
argument_list|)
expr_stmt|;
block|}
comment|// Quick test to make sure index is not corrupt:
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DocsEnum
name|tdocs
init|=
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
literal|"field"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|tdocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|count
operator|>
literal|0
argument_list|)
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
block|}
comment|// Runs test, with multiple threads, using the specific
comment|// failure to trigger an IOException
DECL|method|_testMultipleThreadsFailure
specifier|public
name|void
name|_testMultipleThreadsFailure
parameter_list|(
name|MockDirectoryWrapper
operator|.
name|Failure
name|failure
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|NUM_THREADS
init|=
literal|3
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|2
condition|;
name|iter
operator|++
control|)
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
literal|"TEST: iter="
operator|+
name|iter
argument_list|)
expr_stmt|;
block|}
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
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
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
operator|)
operator|.
name|setSuppressExceptions
argument_list|()
expr_stmt|;
name|writer
operator|.
name|setInfoStream
argument_list|(
name|VERBOSE
condition|?
name|System
operator|.
name|out
else|:
literal|null
argument_list|)
expr_stmt|;
name|IndexerThread
index|[]
name|threads
init|=
operator|new
name|IndexerThread
index|[
name|NUM_THREADS
index|]
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|IndexerThread
argument_list|(
name|writer
argument_list|,
literal|true
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|dir
operator|.
name|failOn
argument_list|(
name|failure
argument_list|)
expr_stmt|;
name|failure
operator|.
name|setDoFail
argument_list|()
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"hit unexpected Throwable"
argument_list|,
name|threads
index|[
name|i
index|]
operator|.
name|error
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|close
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|failure
operator|.
name|clearDoFail
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|success
condition|)
block|{
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|delDocs
init|=
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|j
operator|++
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
name|j
argument_list|)
condition|)
block|{
name|reader
operator|.
name|document
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|reader
operator|.
name|getTermFreqVectors
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Runs test, with one thread, using the specific failure
comment|// to trigger an IOException
DECL|method|_testSingleThreadFailure
specifier|public
name|void
name|_testSingleThreadFailure
parameter_list|(
name|MockDirectoryWrapper
operator|.
name|Failure
name|failure
parameter_list|)
throws|throws
name|IOException
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
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
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"aaa bbb ccc ddd eee fff ggg hhh iii jjj"
argument_list|,
name|customType
argument_list|)
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
literal|6
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|dir
operator|.
name|failOn
argument_list|(
name|failure
argument_list|)
expr_stmt|;
name|failure
operator|.
name|setDoFail
argument_list|()
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{     }
name|failure
operator|.
name|clearDoFail
argument_list|()
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Throws IOException during FieldsWriter.flushDocument and during DocumentsWriter.abort
DECL|class|FailOnlyOnAbortOrFlush
specifier|private
specifier|static
class|class
name|FailOnlyOnAbortOrFlush
extends|extends
name|MockDirectoryWrapper
operator|.
name|Failure
block|{
DECL|field|onlyOnce
specifier|private
name|boolean
name|onlyOnce
decl_stmt|;
DECL|method|FailOnlyOnAbortOrFlush
specifier|public
name|FailOnlyOnAbortOrFlush
parameter_list|(
name|boolean
name|onlyOnce
parameter_list|)
block|{
name|this
operator|.
name|onlyOnce
operator|=
name|onlyOnce
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|eval
specifier|public
name|void
name|eval
parameter_list|(
name|MockDirectoryWrapper
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doFail
condition|)
block|{
name|StackTraceElement
index|[]
name|trace
init|=
operator|new
name|Exception
argument_list|()
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
name|boolean
name|sawAbortOrFlushDoc
init|=
literal|false
decl_stmt|;
name|boolean
name|sawClose
init|=
literal|false
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
name|trace
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|"abort"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getMethodName
argument_list|()
argument_list|)
operator|||
literal|"finishDocument"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getMethodName
argument_list|()
argument_list|)
condition|)
block|{
name|sawAbortOrFlushDoc
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
literal|"close"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getMethodName
argument_list|()
argument_list|)
condition|)
block|{
name|sawClose
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sawAbortOrFlushDoc
operator|&&
operator|!
name|sawClose
condition|)
block|{
if|if
condition|(
name|onlyOnce
condition|)
name|doFail
operator|=
literal|false
expr_stmt|;
comment|//System.out.println(Thread.currentThread().getName() + ": now fail");
comment|//new Throwable().printStackTrace(System.out);
throw|throw
operator|new
name|IOException
argument_list|(
literal|"now failing on purpose"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|// LUCENE-1130: make sure initial IOException, and then 2nd
comment|// IOException during rollback(), is OK:
DECL|method|testIOExceptionDuringAbort
specifier|public
name|void
name|testIOExceptionDuringAbort
parameter_list|()
throws|throws
name|IOException
block|{
name|_testSingleThreadFailure
argument_list|(
operator|new
name|FailOnlyOnAbortOrFlush
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-1130: make sure initial IOException, and then 2nd
comment|// IOException during rollback(), is OK:
DECL|method|testIOExceptionDuringAbortOnlyOnce
specifier|public
name|void
name|testIOExceptionDuringAbortOnlyOnce
parameter_list|()
throws|throws
name|IOException
block|{
name|_testSingleThreadFailure
argument_list|(
operator|new
name|FailOnlyOnAbortOrFlush
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-1130: make sure initial IOException, and then 2nd
comment|// IOException during rollback(), with multiple threads, is OK:
DECL|method|testIOExceptionDuringAbortWithThreads
specifier|public
name|void
name|testIOExceptionDuringAbortWithThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|_testMultipleThreadsFailure
argument_list|(
operator|new
name|FailOnlyOnAbortOrFlush
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-1130: make sure initial IOException, and then 2nd
comment|// IOException during rollback(), with multiple threads, is OK:
DECL|method|testIOExceptionDuringAbortWithThreadsOnlyOnce
specifier|public
name|void
name|testIOExceptionDuringAbortWithThreadsOnlyOnce
parameter_list|()
throws|throws
name|Exception
block|{
name|_testMultipleThreadsFailure
argument_list|(
operator|new
name|FailOnlyOnAbortOrFlush
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Throws IOException during DocumentsWriter.writeSegment
DECL|class|FailOnlyInWriteSegment
specifier|private
specifier|static
class|class
name|FailOnlyInWriteSegment
extends|extends
name|MockDirectoryWrapper
operator|.
name|Failure
block|{
DECL|field|onlyOnce
specifier|private
name|boolean
name|onlyOnce
decl_stmt|;
DECL|method|FailOnlyInWriteSegment
specifier|public
name|FailOnlyInWriteSegment
parameter_list|(
name|boolean
name|onlyOnce
parameter_list|)
block|{
name|this
operator|.
name|onlyOnce
operator|=
name|onlyOnce
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|eval
specifier|public
name|void
name|eval
parameter_list|(
name|MockDirectoryWrapper
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|doFail
condition|)
block|{
name|StackTraceElement
index|[]
name|trace
init|=
operator|new
name|Exception
argument_list|()
operator|.
name|getStackTrace
argument_list|()
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
name|trace
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|"flush"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getMethodName
argument_list|()
argument_list|)
operator|&&
literal|"org.apache.lucene.index.DocFieldProcessor"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getClassName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|onlyOnce
condition|)
name|doFail
operator|=
literal|false
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"now failing on purpose"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
comment|// LUCENE-1130: test IOException in writeSegment
DECL|method|testIOExceptionDuringWriteSegment
specifier|public
name|void
name|testIOExceptionDuringWriteSegment
parameter_list|()
throws|throws
name|IOException
block|{
name|_testSingleThreadFailure
argument_list|(
operator|new
name|FailOnlyInWriteSegment
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-1130: test IOException in writeSegment
DECL|method|testIOExceptionDuringWriteSegmentOnlyOnce
specifier|public
name|void
name|testIOExceptionDuringWriteSegmentOnlyOnce
parameter_list|()
throws|throws
name|IOException
block|{
name|_testSingleThreadFailure
argument_list|(
operator|new
name|FailOnlyInWriteSegment
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-1130: test IOException in writeSegment, with threads
DECL|method|testIOExceptionDuringWriteSegmentWithThreads
specifier|public
name|void
name|testIOExceptionDuringWriteSegmentWithThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|_testMultipleThreadsFailure
argument_list|(
operator|new
name|FailOnlyInWriteSegment
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-1130: test IOException in writeSegment, with threads
DECL|method|testIOExceptionDuringWriteSegmentWithThreadsOnlyOnce
specifier|public
name|void
name|testIOExceptionDuringWriteSegmentWithThreadsOnlyOnce
parameter_list|()
throws|throws
name|Exception
block|{
name|_testMultipleThreadsFailure
argument_list|(
operator|new
name|FailOnlyInWriteSegment
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//  LUCENE-3365: Test adding two documents with the same field from two different IndexWriters
comment|//  that we attempt to open at the same time.  As long as the first IndexWriter completes
comment|//  and closes before the second IndexWriter time's out trying to get the Lock,
comment|//  we should see both documents
DECL|method|testOpenTwoIndexWritersOnDifferentThreads
specifier|public
name|void
name|testOpenTwoIndexWritersOnDifferentThreads
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|CountDownLatch
name|oneIWConstructed
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|DelayedIndexAndCloseRunnable
name|thread1
init|=
operator|new
name|DelayedIndexAndCloseRunnable
argument_list|(
name|dir
argument_list|,
name|oneIWConstructed
argument_list|)
decl_stmt|;
name|DelayedIndexAndCloseRunnable
name|thread2
init|=
operator|new
name|DelayedIndexAndCloseRunnable
argument_list|(
name|dir
argument_list|,
name|oneIWConstructed
argument_list|)
decl_stmt|;
name|thread1
operator|.
name|start
argument_list|()
expr_stmt|;
name|thread2
operator|.
name|start
argument_list|()
expr_stmt|;
name|oneIWConstructed
operator|.
name|await
argument_list|()
expr_stmt|;
name|thread1
operator|.
name|startIndexing
argument_list|()
expr_stmt|;
name|thread2
operator|.
name|startIndexing
argument_list|()
expr_stmt|;
name|thread1
operator|.
name|join
argument_list|()
expr_stmt|;
name|thread2
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Failed due to: "
operator|+
name|thread1
operator|.
name|failure
argument_list|,
name|thread1
operator|.
name|failed
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Failed due to: "
operator|+
name|thread2
operator|.
name|failure
argument_list|,
name|thread2
operator|.
name|failed
argument_list|)
expr_stmt|;
comment|// now verify that we have two documents in the index
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"IndexReader should have one document per thread running"
argument_list|,
literal|2
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
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
DECL|class|DelayedIndexAndCloseRunnable
specifier|static
class|class
name|DelayedIndexAndCloseRunnable
extends|extends
name|Thread
block|{
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|field|failed
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
DECL|field|failure
name|Throwable
name|failure
init|=
literal|null
decl_stmt|;
DECL|field|startIndexing
specifier|private
specifier|final
name|CountDownLatch
name|startIndexing
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|iwConstructed
specifier|private
name|CountDownLatch
name|iwConstructed
decl_stmt|;
DECL|method|DelayedIndexAndCloseRunnable
specifier|public
name|DelayedIndexAndCloseRunnable
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|CountDownLatch
name|iwConstructed
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|iwConstructed
operator|=
name|iwConstructed
expr_stmt|;
block|}
DECL|method|startIndexing
specifier|public
name|void
name|startIndexing
parameter_list|()
block|{
name|this
operator|.
name|startIndexing
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
name|newField
argument_list|(
literal|"field"
argument_list|,
literal|"testData"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
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
argument_list|)
decl_stmt|;
name|iwConstructed
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|startIndexing
operator|.
name|await
argument_list|()
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
name|failure
operator|=
name|e
expr_stmt|;
name|failure
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
end_class
end_unit
