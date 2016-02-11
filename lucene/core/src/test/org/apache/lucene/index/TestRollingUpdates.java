begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|memory
operator|.
name|MemoryPostingsFormat
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
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|BaseDirectoryWrapper
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
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//provider.register(new MemoryCodec());
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|Codec
operator|.
name|setDefault
argument_list|(
name|TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|MemoryPostingsFormat
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|random
operator|.
name|nextFloat
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|analyzer
operator|.
name|setMaxTokenLength
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|IndexWriter
operator|.
name|MAX_TERM_LENGTH
argument_list|)
argument_list|)
expr_stmt|;
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
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|SIZE
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
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
name|IndexSearcher
name|s
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
operator|(
name|TEST_NIGHTLY
condition|?
literal|200
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
else|:
literal|5
operator|*
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|)
operator|)
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
literal|"TEST: numUpdates="
operator|+
name|numUpdates
argument_list|)
expr_stmt|;
block|}
name|int
name|updateCount
init|=
literal|0
decl_stmt|;
comment|// TODO: sometimes update ids not in order...
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
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
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
literal|"  docIter="
operator|+
name|docIter
operator|+
literal|" id="
operator|+
name|id
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|Field
operator|)
name|doc
operator|.
name|getField
argument_list|(
literal|"docid"
argument_list|)
operator|)
operator|.
name|setStringValue
argument_list|(
name|myID
argument_list|)
expr_stmt|;
name|Term
name|idTerm
init|=
operator|new
name|Term
argument_list|(
literal|"docid"
argument_list|,
name|myID
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|doUpdate
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
operator|&&
name|updateCount
operator|<
name|SIZE
condition|)
block|{
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
name|idTerm
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|doUpdate
operator|=
operator|!
name|w
operator|.
name|tryDeleteDocument
argument_list|(
name|r
argument_list|,
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
if|if
condition|(
name|VERBOSE
condition|)
block|{
if|if
condition|(
name|doUpdate
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  tryDeleteDocument failed"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  tryDeleteDocument succeeded"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|doUpdate
operator|=
literal|true
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
literal|"  no searcher: doUpdate=true"
argument_list|)
expr_stmt|;
block|}
block|}
name|updateCount
operator|++
expr_stmt|;
if|if
condition|(
name|doUpdate
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|w
operator|.
name|updateDocument
argument_list|(
name|idTerm
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// It's OK to not be atomic for this test (no separate thread reopening readers):
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|idTerm
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docIter
operator|>=
name|SIZE
operator|&&
name|random
argument_list|()
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
argument_list|()
operator|.
name|nextBoolean
argument_list|()
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
literal|"TEST: reopen applyDeletions="
operator|+
name|applyDeletions
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|w
operator|.
name|getReader
argument_list|(
name|applyDeletions
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|applyDeletions
condition|)
block|{
name|s
operator|=
name|newSearcher
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
literal|null
expr_stmt|;
block|}
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
name|updateCount
operator|=
literal|0
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
name|TestIndexWriter
operator|.
name|assertNoUnreferencedFiles
argument_list|(
name|dir
argument_list|,
literal|"leftover files after rolling updates"
argument_list|)
expr_stmt|;
name|docs
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// LUCENE-4455:
name|SegmentInfos
name|infos
init|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|long
name|totalBytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SegmentCommitInfo
name|sipc
range|:
name|infos
control|)
block|{
name|totalBytes
operator|+=
name|sipc
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
name|long
name|totalBytes2
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|fileName
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|IndexFileNames
operator|.
name|CODEC_FILE_PATTERN
operator|.
name|matcher
argument_list|(
name|fileName
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|totalBytes2
operator|+=
name|dir
operator|.
name|fileLength
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|totalBytes2
argument_list|,
name|totalBytes
argument_list|)
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
argument_list|()
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
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numUpdates
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|int
name|numThreads
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
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
name|DirectoryReader
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
name|DirectoryReader
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
name|BytesRef
name|br
init|=
operator|new
name|BytesRef
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"id"
argument_list|,
name|br
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|br
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
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
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|open
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
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
