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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|store
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
name|document
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
name|search
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_class
DECL|class|TestStressIndexing
specifier|public
class|class
name|TestStressIndexing
extends|extends
name|LuceneTestCase
block|{
DECL|field|ANALYZER
specifier|private
specifier|static
specifier|final
name|Analyzer
name|ANALYZER
init|=
operator|new
name|SimpleAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
DECL|field|RANDOM
specifier|private
name|Random
name|RANDOM
decl_stmt|;
DECL|class|TimedThread
specifier|private
specifier|static
specifier|abstract
class|class
name|TimedThread
extends|extends
name|Thread
block|{
DECL|field|failed
specifier|volatile
name|boolean
name|failed
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|RUN_TIME_SEC
specifier|private
specifier|static
name|int
name|RUN_TIME_SEC
init|=
literal|1
decl_stmt|;
DECL|field|allThreads
specifier|private
name|TimedThread
index|[]
name|allThreads
decl_stmt|;
DECL|method|doWork
specifier|abstract
specifier|public
name|void
name|doWork
parameter_list|()
throws|throws
name|Throwable
function_decl|;
DECL|method|TimedThread
name|TimedThread
parameter_list|(
name|TimedThread
index|[]
name|threads
parameter_list|)
block|{
name|this
operator|.
name|allThreads
operator|=
name|threads
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
name|long
name|stopTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|1000
operator|*
name|RUN_TIME_SEC
decl_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
try|try
block|{
do|do
block|{
if|if
condition|(
name|anyErrors
argument_list|()
condition|)
break|break;
name|doWork
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
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
catch|catch
parameter_list|(
name|Throwable
name|e
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
operator|+
literal|": exc"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|anyErrors
specifier|private
name|boolean
name|anyErrors
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|allThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|allThreads
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|allThreads
index|[
name|i
index|]
operator|.
name|failed
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
block|}
DECL|class|IndexerThread
specifier|private
class|class
name|IndexerThread
extends|extends
name|TimedThread
block|{
DECL|field|writer
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|count
specifier|public
name|int
name|count
decl_stmt|;
DECL|field|nextID
name|int
name|nextID
decl_stmt|;
DECL|method|IndexerThread
specifier|public
name|IndexerThread
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|TimedThread
index|[]
name|threads
parameter_list|)
block|{
name|super
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWork
specifier|public
name|void
name|doWork
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Add 10 docs:
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|n
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nextID
operator|++
argument_list|)
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
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|n
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
comment|// Delete 5 docs:
name|int
name|deleteID
init|=
name|nextID
operator|-
literal|1
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
literal|5
condition|;
name|j
operator|++
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
literal|""
operator|+
name|deleteID
argument_list|)
argument_list|)
expr_stmt|;
name|deleteID
operator|-=
literal|2
expr_stmt|;
block|}
block|}
block|}
DECL|class|SearcherThread
specifier|private
specifier|static
class|class
name|SearcherThread
extends|extends
name|TimedThread
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|method|SearcherThread
specifier|public
name|SearcherThread
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|TimedThread
index|[]
name|threads
parameter_list|)
block|{
name|super
argument_list|(
name|threads
argument_list|)
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWork
specifier|public
name|void
name|doWork
parameter_list|()
throws|throws
name|Throwable
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
operator|(
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|,
literal|true
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|count
operator|+=
literal|100
expr_stmt|;
block|}
block|}
comment|/*     Run one indexer and 2 searchers against single index as     stress test.   */
DECL|method|runStressTest
specifier|public
name|void
name|runStressTest
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|MergeScheduler
name|mergeScheduler
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexWriter
name|modifier
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|ANALYZER
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
name|modifier
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|TimedThread
index|[]
name|threads
init|=
operator|new
name|TimedThread
index|[
literal|4
index|]
decl_stmt|;
name|int
name|numThread
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|mergeScheduler
operator|!=
literal|null
condition|)
name|modifier
operator|.
name|setMergeScheduler
argument_list|(
name|mergeScheduler
argument_list|)
expr_stmt|;
comment|// One modifier that writes 10 docs then removes 5, over
comment|// and over:
name|IndexerThread
name|indexerThread
init|=
operator|new
name|IndexerThread
argument_list|(
name|modifier
argument_list|,
name|threads
argument_list|)
decl_stmt|;
name|threads
index|[
name|numThread
operator|++
index|]
operator|=
name|indexerThread
expr_stmt|;
name|indexerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|IndexerThread
name|indexerThread2
init|=
operator|new
name|IndexerThread
argument_list|(
name|modifier
argument_list|,
name|threads
argument_list|)
decl_stmt|;
name|threads
index|[
name|numThread
operator|++
index|]
operator|=
name|indexerThread2
expr_stmt|;
name|indexerThread2
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Two searchers that constantly just re-instantiate the
comment|// searcher:
name|SearcherThread
name|searcherThread1
init|=
operator|new
name|SearcherThread
argument_list|(
name|directory
argument_list|,
name|threads
argument_list|)
decl_stmt|;
name|threads
index|[
name|numThread
operator|++
index|]
operator|=
name|searcherThread1
expr_stmt|;
name|searcherThread1
operator|.
name|start
argument_list|()
expr_stmt|;
name|SearcherThread
name|searcherThread2
init|=
operator|new
name|SearcherThread
argument_list|(
name|directory
argument_list|,
name|threads
argument_list|)
decl_stmt|;
name|threads
index|[
name|numThread
operator|++
index|]
operator|=
name|searcherThread2
expr_stmt|;
name|searcherThread2
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
name|numThread
condition|;
name|i
operator|++
control|)
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
name|modifier
operator|.
name|close
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
name|numThread
condition|;
name|i
operator|++
control|)
name|assertTrue
argument_list|(
operator|!
name|threads
index|[
name|i
index|]
operator|.
name|failed
argument_list|)
expr_stmt|;
comment|//System.out.println("    Writer: " + indexerThread.count + " iterations");
comment|//System.out.println("Searcher 1: " + searcherThread1.count + " searchers created");
comment|//System.out.println("Searcher 2: " + searcherThread2.count + " searchers created");
block|}
comment|/*     Run above stress test against RAMDirectory and then     FSDirectory.   */
DECL|method|testStressIndexAndSearching
specifier|public
name|void
name|testStressIndexAndSearching
parameter_list|()
throws|throws
name|Exception
block|{
name|RANDOM
operator|=
name|newRandom
argument_list|()
expr_stmt|;
comment|// With ConcurrentMergeScheduler, in RAMDir
name|Directory
name|directory
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|runStressTest
argument_list|(
name|directory
argument_list|,
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// With ConcurrentMergeScheduler, in FSDir
name|File
name|dirPath
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"lucene.test.stress"
argument_list|)
decl_stmt|;
name|directory
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
name|runStressTest
argument_list|(
name|directory
argument_list|,
operator|new
name|ConcurrentMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
