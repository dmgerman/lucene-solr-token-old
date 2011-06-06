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
name|Field
operator|.
name|Index
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
name|util
operator|.
name|*
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
begin_class
DECL|class|TestRollingUpdates
specifier|public
class|class
name|TestRollingUpdates
extends|extends
name|LuceneTestCase
block|{
comment|// Just updates the same set of N docs over and over, to
comment|// stress out deletions
annotation|@
name|Test
DECL|method|testRollingUpdates
specifier|public
name|void
name|testRollingUpdates
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
name|IndexWriter
name|w
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
specifier|final
name|int
name|SIZE
init|=
operator|(
name|TEST_NIGHTLY
condition|?
literal|200
else|:
literal|20
operator|)
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
name|int
name|id
init|=
literal|0
decl_stmt|;
name|IndexReader
name|r
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|numUpdates
init|=
call|(
name|int
call|)
argument_list|(
name|SIZE
operator|*
operator|(
literal|2
operator|+
name|random
operator|.
name|nextDouble
argument_list|()
operator|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docIter
init|=
literal|0
init|;
name|docIter
operator|<
name|numUpdates
condition|;
name|docIter
operator|++
control|)
block|{
specifier|final
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
specifier|final
name|String
name|myID
init|=
literal|""
operator|+
name|id
decl_stmt|;
if|if
condition|(
name|id
operator|==
name|SIZE
operator|-
literal|1
condition|)
block|{
name|id
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|id
operator|++
expr_stmt|;
block|}
name|doc
operator|.
name|getField
argument_list|(
literal|"docid"
argument_list|)
operator|.
name|setValue
argument_list|(
name|myID
argument_list|)
expr_stmt|;
name|w
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"docid"
argument_list|,
name|myID
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|docIter
operator|>=
name|SIZE
operator|&&
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
name|r
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|boolean
name|applyDeletions
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|r
operator|=
name|w
operator|.
name|getReader
argument_list|(
name|applyDeletions
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"applyDeletions="
operator|+
name|applyDeletions
operator|+
literal|" r.numDocs()="
operator|+
name|r
operator|.
name|numDocs
argument_list|()
operator|+
literal|" vs SIZE="
operator|+
name|SIZE
argument_list|,
operator|!
name|applyDeletions
operator|||
name|r
operator|.
name|numDocs
argument_list|()
operator|==
name|SIZE
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|SIZE
argument_list|,
name|w
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|docs
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
DECL|method|testUpdateSameDoc
specifier|public
name|void
name|testUpdateSameDoc
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
for|for
control|(
name|int
name|r
init|=
literal|0
init|;
name|r
operator|<
literal|3
condition|;
name|r
operator|++
control|)
block|{
specifier|final
name|IndexWriter
name|w
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
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numUpdates
init|=
operator|(
name|TEST_NIGHTLY
condition|?
literal|200
else|:
literal|20
operator|)
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
name|int
name|numThreads
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|IndexingThread
index|[]
name|threads
init|=
operator|new
name|IndexingThread
index|[
name|numThreads
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
name|numThreads
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
name|IndexingThread
argument_list|(
name|docs
argument_list|,
name|w
argument_list|,
name|numUpdates
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
name|numThreads
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
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|IndexReader
name|open
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|open
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|open
operator|.
name|close
argument_list|()
expr_stmt|;
name|docs
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
DECL|class|IndexingThread
specifier|static
class|class
name|IndexingThread
extends|extends
name|Thread
block|{
DECL|field|docs
specifier|final
name|LineFileDocs
name|docs
decl_stmt|;
DECL|field|writer
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|num
specifier|final
name|int
name|num
decl_stmt|;
DECL|method|IndexingThread
specifier|public
name|IndexingThread
parameter_list|(
name|LineFileDocs
name|docs
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|num
operator|=
name|num
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|IndexReader
name|open
init|=
literal|null
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
name|num
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
comment|// docs.nextDoc();
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"id"
argument_list|,
literal|"test"
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|updateDocument
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"test"
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|open
operator|==
literal|null
condition|)
block|{
name|open
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|open
operator|.
name|reopen
argument_list|()
decl_stmt|;
if|if
condition|(
name|reader
operator|!=
name|open
condition|)
block|{
name|open
operator|.
name|close
argument_list|()
expr_stmt|;
name|open
operator|=
name|reader
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"iter: "
operator|+
name|i
operator|+
literal|" numDocs: "
operator|+
name|open
operator|.
name|numDocs
argument_list|()
operator|+
literal|" del: "
operator|+
name|open
operator|.
name|numDeletedDocs
argument_list|()
operator|+
literal|" max: "
operator|+
name|open
operator|.
name|maxDoc
argument_list|()
argument_list|,
literal|1
argument_list|,
name|open
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|open
operator|!=
literal|null
condition|)
block|{
name|open
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
end_unit
