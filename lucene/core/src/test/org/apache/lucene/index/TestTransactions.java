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
name|store
operator|.
name|RAMDirectory
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
name|English
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
DECL|class|TestTransactions
specifier|public
class|class
name|TestTransactions
extends|extends
name|LuceneTestCase
block|{
DECL|field|doFail
specifier|private
specifier|static
specifier|volatile
name|boolean
name|doFail
decl_stmt|;
DECL|class|RandomFailure
specifier|private
class|class
name|RandomFailure
extends|extends
name|MockDirectoryWrapper
operator|.
name|Failure
block|{
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
name|TestTransactions
operator|.
name|doFail
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|%
literal|10
operator|<=
literal|3
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"now failing randomly but on purpose"
argument_list|)
throw|;
block|}
block|}
block|}
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
DECL|field|RUN_TIME_MSEC
specifier|private
specifier|static
name|float
name|RUN_TIME_MSEC
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
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
call|(
name|long
call|)
argument_list|(
name|RUN_TIME_MSEC
argument_list|)
decl_stmt|;
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
DECL|field|dir1
name|Directory
name|dir1
decl_stmt|;
DECL|field|dir2
name|Directory
name|dir2
decl_stmt|;
DECL|field|lock
name|Object
name|lock
decl_stmt|;
DECL|field|nextID
name|int
name|nextID
decl_stmt|;
DECL|method|IndexerThread
specifier|public
name|IndexerThread
parameter_list|(
name|Object
name|lock
parameter_list|,
name|Directory
name|dir1
parameter_list|,
name|Directory
name|dir2
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
name|lock
operator|=
name|lock
expr_stmt|;
name|this
operator|.
name|dir1
operator|=
name|dir1
expr_stmt|;
name|this
operator|.
name|dir2
operator|=
name|dir2
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
name|IndexWriter
name|writer1
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir1
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|3
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
literal|2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|writer1
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
comment|// Intentionally use different params so flush/merge
comment|// happen @ different times
name|IndexWriter
name|writer2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
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
literal|3
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|writer2
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
name|update
argument_list|(
name|writer1
argument_list|)
expr_stmt|;
name|update
argument_list|(
name|writer2
argument_list|)
expr_stmt|;
name|TestTransactions
operator|.
name|doFail
operator|=
literal|true
expr_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
try|try
block|{
name|writer1
operator|.
name|prepareCommit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|writer1
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|writer2
operator|.
name|rollback
argument_list|()
expr_stmt|;
return|return;
block|}
try|try
block|{
name|writer2
operator|.
name|prepareCommit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|writer1
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|writer2
operator|.
name|rollback
argument_list|()
expr_stmt|;
return|return;
block|}
name|writer1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer2
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|TestTransactions
operator|.
name|doFail
operator|=
literal|false
expr_stmt|;
block|}
name|writer1
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Add 10 docs:
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|StringField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
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
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
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
name|TextField
operator|.
name|TYPE_UNSTORED
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
DECL|field|dir1
name|Directory
name|dir1
decl_stmt|;
DECL|field|dir2
name|Directory
name|dir2
decl_stmt|;
DECL|field|lock
name|Object
name|lock
decl_stmt|;
DECL|method|SearcherThread
specifier|public
name|SearcherThread
parameter_list|(
name|Object
name|lock
parameter_list|,
name|Directory
name|dir1
parameter_list|,
name|Directory
name|dir2
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
name|lock
operator|=
name|lock
expr_stmt|;
name|this
operator|.
name|dir1
operator|=
name|dir1
expr_stmt|;
name|this
operator|.
name|dir2
operator|=
name|dir2
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
name|IndexReader
name|r1
decl_stmt|,
name|r2
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|r1
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
expr_stmt|;
name|r2
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r1
operator|.
name|numDocs
argument_list|()
operator|!=
name|r2
operator|.
name|numDocs
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"doc counts differ: r1="
operator|+
name|r1
operator|.
name|numDocs
argument_list|()
operator|+
literal|" r2="
operator|+
name|r2
operator|.
name|numDocs
argument_list|()
argument_list|)
throw|;
name|r1
operator|.
name|close
argument_list|()
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|initIndex
specifier|public
name|void
name|initIndex
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|Throwable
block|{
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
argument_list|()
argument_list|)
argument_list|)
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
literal|7
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
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
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
name|TextField
operator|.
name|TYPE_UNSTORED
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testTransactions
specifier|public
name|void
name|testTransactions
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// we cant use non-ramdir on windows, because this test needs to double-write.
name|MockDirectoryWrapper
name|dir1
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|MockDirectoryWrapper
name|dir2
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|dir1
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|dir2
operator|.
name|setPreventDoubleWrite
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|dir1
operator|.
name|failOn
argument_list|(
operator|new
name|RandomFailure
argument_list|()
argument_list|)
expr_stmt|;
name|dir2
operator|.
name|failOn
argument_list|(
operator|new
name|RandomFailure
argument_list|()
argument_list|)
expr_stmt|;
name|dir1
operator|.
name|setFailOnOpenInput
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|dir2
operator|.
name|setFailOnOpenInput
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|initIndex
argument_list|(
name|dir1
argument_list|)
expr_stmt|;
name|initIndex
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
name|TimedThread
index|[]
name|threads
init|=
operator|new
name|TimedThread
index|[
literal|3
index|]
decl_stmt|;
name|int
name|numThread
init|=
literal|0
decl_stmt|;
name|IndexerThread
name|indexerThread
init|=
operator|new
name|IndexerThread
argument_list|(
name|this
argument_list|,
name|dir1
argument_list|,
name|dir2
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
name|SearcherThread
name|searcherThread1
init|=
operator|new
name|SearcherThread
argument_list|(
name|this
argument_list|,
name|dir1
argument_list|,
name|dir2
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
name|this
argument_list|,
name|dir1
argument_list|,
name|dir2
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
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
