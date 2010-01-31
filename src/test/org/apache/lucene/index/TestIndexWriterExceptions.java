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
name|Version
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
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|MockRAMDirectory
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
name|WhitespaceAnalyzer
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
begin_class
DECL|class|TestIndexWriterExceptions
specifier|public
class|class
name|TestIndexWriterExceptions
extends|extends
name|LuceneTestCase
block|{
DECL|field|DEBUG
specifier|final
specifier|private
specifier|static
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
DECL|class|IndexerThread
specifier|private
class|class
name|IndexerThread
extends|extends
name|Thread
block|{
DECL|field|writer
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|r
specifier|final
name|Random
name|r
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|Random
argument_list|(
literal|47
argument_list|)
decl_stmt|;
DECL|field|failure
name|Throwable
name|failure
decl_stmt|;
DECL|method|IndexerThread
specifier|public
name|IndexerThread
parameter_list|(
name|int
name|i
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
block|{
name|setName
argument_list|(
literal|"Indexer "
operator|+
name|i
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content1"
argument_list|,
literal|"aaa bbb ccc ddd"
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content6"
argument_list|,
literal|"aaa bbb ccc ddd"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content2"
argument_list|,
literal|"aaa bbb ccc ddd"
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content3"
argument_list|,
literal|"aaa bbb ccc ddd"
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
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content4"
argument_list|,
literal|"aaa bbb ccc ddd"
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content5"
argument_list|,
literal|"aaa bbb ccc ddd"
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
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content7"
argument_list|,
literal|"aaa bbb ccc ddd"
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
name|NOT_ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Field
name|idField
init|=
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|""
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
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
specifier|final
name|long
name|stopTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|500
decl_stmt|;
do|do
block|{
name|doFail
operator|.
name|set
argument_list|(
name|this
argument_list|)
expr_stmt|;
specifier|final
name|String
name|id
init|=
literal|""
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
decl_stmt|;
name|idField
operator|.
name|setValue
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|Term
name|idTerm
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
name|idTerm
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"EXC: "
argument_list|)
expr_stmt|;
name|re
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|writer
operator|.
name|getDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
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
literal|": unexpected exception1"
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
name|failure
operator|=
name|ioe
expr_stmt|;
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
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
literal|": unexpected exception2"
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
name|failure
operator|=
name|t
expr_stmt|;
break|break;
block|}
name|doFail
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// After a possible exception (above) I should be able
comment|// to add a new document without hitting an
comment|// exception:
try|try
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
name|idTerm
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
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
literal|": unexpected exception3"
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
name|failure
operator|=
name|t
expr_stmt|;
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
DECL|field|doFail
name|ThreadLocal
argument_list|<
name|Thread
argument_list|>
name|doFail
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
DECL|class|MockIndexWriter
specifier|public
class|class
name|MockIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|r
name|Random
name|r
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|Random
argument_list|(
literal|17
argument_list|)
decl_stmt|;
DECL|method|MockIndexWriter
specifier|public
name|MockIndexWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|a
parameter_list|,
name|boolean
name|create
parameter_list|,
name|MaxFieldLength
name|mfl
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|a
argument_list|,
name|create
argument_list|,
name|mfl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testPoint
name|boolean
name|testPoint
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|doFail
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"startDoFlush"
argument_list|)
operator|&&
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
operator|==
literal|17
condition|)
block|{
if|if
condition|(
name|DEBUG
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
literal|": NOW FAIL: "
operator|+
name|name
argument_list|)
expr_stmt|;
comment|//new Throwable().printStackTrace(System.out);
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|": intentionally failing at "
operator|+
name|name
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
block|}
DECL|method|testRandomExceptions
specifier|public
name|void
name|testRandomExceptions
parameter_list|()
throws|throws
name|Throwable
block|{
name|MockRAMDirectory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|MockIndexWriter
name|writer
init|=
operator|new
name|MockIndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|writer
operator|.
name|getMergeScheduler
argument_list|()
operator|)
operator|.
name|setSuppressExceptions
argument_list|()
expr_stmt|;
comment|//writer.setMaxBufferedDocs(10);
name|writer
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|0.1
argument_list|)
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
name|writer
operator|.
name|setInfoStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|IndexerThread
name|thread
init|=
operator|new
name|IndexerThread
argument_list|(
literal|0
argument_list|,
name|writer
argument_list|)
decl_stmt|;
name|thread
operator|.
name|run
argument_list|()
expr_stmt|;
if|if
condition|(
name|thread
operator|.
name|failure
operator|!=
literal|null
condition|)
block|{
name|thread
operator|.
name|failure
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"thread "
operator|+
name|thread
operator|.
name|getName
argument_list|()
operator|+
literal|": hit unexpected failure"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"exception during close:"
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
name|writer
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
comment|// Confirm that when doc hits exception partway through tokenization, it's deleted:
name|IndexReader
name|r2
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
name|int
name|count
init|=
name|r2
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content4"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|count2
init|=
name|r2
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content4"
argument_list|,
literal|"ddd"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|count2
argument_list|)
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomExceptionsThreads
specifier|public
name|void
name|testRandomExceptionsThreads
parameter_list|()
throws|throws
name|Throwable
block|{
name|MockRAMDirectory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|MockIndexWriter
name|writer
init|=
operator|new
name|MockIndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
operator|(
operator|(
name|ConcurrentMergeScheduler
operator|)
name|writer
operator|.
name|getMergeScheduler
argument_list|()
operator|)
operator|.
name|setSuppressExceptions
argument_list|()
expr_stmt|;
comment|//writer.setMaxBufferedDocs(10);
name|writer
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|0.2
argument_list|)
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
name|writer
operator|.
name|setInfoStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
specifier|final
name|int
name|NUM_THREADS
init|=
literal|4
decl_stmt|;
specifier|final
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
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|IndexerThread
argument_list|(
name|i
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|threads
index|[
name|i
index|]
operator|.
name|failure
operator|!=
literal|null
condition|)
name|fail
argument_list|(
literal|"thread "
operator|+
name|threads
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|+
literal|": hit unexpected failure"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"exception during close:"
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
name|writer
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
comment|// Confirm that when doc hits exception partway through tokenization, it's deleted:
name|IndexReader
name|r2
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
name|int
name|count
init|=
name|r2
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content4"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|count2
init|=
name|r2
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content4"
argument_list|,
literal|"ddd"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
name|count2
argument_list|)
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
