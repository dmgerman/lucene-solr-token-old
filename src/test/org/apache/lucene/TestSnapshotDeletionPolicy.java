begin_unit
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
package|;
end_package
begin_comment
comment|// Intentionally not in org.apache.lucene.index, to assert
end_comment
begin_comment
comment|// that we do not require any package private access.
end_comment
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|IndexInput
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|IndexCommit
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
name|KeepOnlyLastCommitDeletionPolicy
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
name|IndexWriter
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
name|TestIndexWriter
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
name|SnapshotDeletionPolicy
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
begin_comment
comment|//
end_comment
begin_comment
comment|// This was developed for Lucene In Action,
end_comment
begin_comment
comment|// http://lucenebook.com
end_comment
begin_comment
comment|//
end_comment
begin_class
DECL|class|TestSnapshotDeletionPolicy
specifier|public
class|class
name|TestSnapshotDeletionPolicy
extends|extends
name|LuceneTestCase
block|{
DECL|field|INDEX_PATH
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_PATH
init|=
literal|"test.snapshots"
decl_stmt|;
DECL|method|testSnapshotDeletionPolicy
specifier|public
name|void
name|testSnapshotDeletionPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dir
init|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
name|INDEX_PATH
argument_list|)
decl_stmt|;
try|try
block|{
name|Directory
name|fsDir
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|runTest
argument_list|(
name|fsDir
argument_list|)
expr_stmt|;
name|fsDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|MockRAMDirectory
name|dir2
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|runTest
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testReuseAcrossWriters
specifier|public
name|void
name|testReuseAcrossWriters
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|SnapshotDeletionPolicy
name|dp
init|=
operator|new
name|SnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
name|dp
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
comment|// Force frequent flushes
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
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
literal|"content"
argument_list|,
literal|"aaa"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
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
literal|7
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|IndexCommit
name|cp
init|=
name|dp
operator|.
name|snapshot
argument_list|()
decl_stmt|;
name|copyFiles
argument_list|(
name|dir
argument_list|,
name|cp
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|copyFiles
argument_list|(
name|dir
argument_list|,
name|cp
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
name|dp
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
expr_stmt|;
name|copyFiles
argument_list|(
name|dir
argument_list|,
name|cp
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
literal|7
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|copyFiles
argument_list|(
name|dir
argument_list|,
name|cp
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|copyFiles
argument_list|(
name|dir
argument_list|,
name|cp
argument_list|)
expr_stmt|;
name|dp
operator|.
name|release
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
name|dp
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|copyFiles
argument_list|(
name|dir
argument_list|,
name|cp
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected IOException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// expected
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|runTest
specifier|private
name|void
name|runTest
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Run for ~1 seconds
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
decl_stmt|;
name|SnapshotDeletionPolicy
name|dp
init|=
operator|new
name|SnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
name|dp
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
comment|// Force frequent flushes
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|Thread
name|t
init|=
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
literal|"content"
argument_list|,
literal|"aaa"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopTime
condition|)
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
literal|27
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
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
name|t
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
literal|"addDocument failed"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
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
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// While the above indexing thread is running, take many
comment|// backups:
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopTime
condition|)
block|{
name|backupIndex
argument_list|(
name|dir
argument_list|,
name|dp
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|20
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|t
operator|.
name|isAlive
argument_list|()
condition|)
break|break;
block|}
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
comment|// Add one more document to force writer to commit a
comment|// final segment, so deletion policy has a chance to
comment|// delete again:
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
literal|"content"
argument_list|,
literal|"aaa"
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
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
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
comment|// Make sure we don't have any leftover files in the
comment|// directory:
name|writer
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
literal|"some files were not deleted but should have been"
argument_list|)
expr_stmt|;
block|}
comment|/** Example showing how to use the SnapshotDeletionPolicy    *  to take a backup.  This method does not really do a    *  backup; instead, it reads every byte of every file    *  just to test that the files indeed exist and are    *  readable even while the index is changing. */
DECL|method|backupIndex
specifier|public
name|void
name|backupIndex
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SnapshotDeletionPolicy
name|dp
parameter_list|)
throws|throws
name|Exception
block|{
comment|// To backup an index we first take a snapshot:
try|try
block|{
name|copyFiles
argument_list|(
name|dir
argument_list|,
name|dp
operator|.
name|snapshot
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// Make sure to release the snapshot, otherwise these
comment|// files will never be deleted during this IndexWriter
comment|// session:
name|dp
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|copyFiles
specifier|private
name|void
name|copyFiles
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|IndexCommit
name|cp
parameter_list|)
throws|throws
name|Exception
block|{
comment|// While we hold the snapshot, and nomatter how long
comment|// we take to do the backup, the IndexWriter will
comment|// never delete the files in the snapshot:
name|Collection
argument_list|<
name|String
argument_list|>
name|files
init|=
name|cp
operator|.
name|getFileNames
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|fileName
range|:
name|files
control|)
block|{
comment|// NOTE: in a real backup you would not use
comment|// readFile; you would need to use something else
comment|// that copies the file to a backup location.  This
comment|// could even be a spawned shell process (eg "tar",
comment|// "zip") that takes the list of files and builds a
comment|// backup.
name|readFile
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|buffer
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
DECL|method|readFile
specifier|private
name|void
name|readFile
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|size
init|=
name|dir
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|long
name|bytesLeft
init|=
name|size
decl_stmt|;
while|while
condition|(
name|bytesLeft
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|numToRead
decl_stmt|;
if|if
condition|(
name|bytesLeft
operator|<
name|buffer
operator|.
name|length
condition|)
name|numToRead
operator|=
operator|(
name|int
operator|)
name|bytesLeft
expr_stmt|;
else|else
name|numToRead
operator|=
name|buffer
operator|.
name|length
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|numToRead
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|bytesLeft
operator|-=
name|numToRead
expr_stmt|;
block|}
comment|// Don't do this in your real backups!  This is just
comment|// to force a backup to take a somewhat long time, to
comment|// make sure we are exercising the fact that the
comment|// IndexWriter should not delete this file even when I
comment|// take my time reading it.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
