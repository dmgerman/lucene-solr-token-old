begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|nio
operator|.
name|file
operator|.
name|NoSuchFileException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
operator|.
name|Entry
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
name|DirectoryReader
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
name|IndexFileNames
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
name|IndexWriterConfig
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
name|IOUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|LocalReplicatorTest
specifier|public
class|class
name|LocalReplicatorTest
extends|extends
name|ReplicatorTestCase
block|{
DECL|field|VERSION_ID
specifier|private
specifier|static
specifier|final
name|String
name|VERSION_ID
init|=
literal|"version"
decl_stmt|;
DECL|field|replicator
specifier|private
name|LocalReplicator
name|replicator
decl_stmt|;
DECL|field|sourceDir
specifier|private
name|Directory
name|sourceDir
decl_stmt|;
DECL|field|sourceWriter
specifier|private
name|IndexWriter
name|sourceWriter
decl_stmt|;
annotation|@
name|Before
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
name|sourceDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
operator|new
name|SnapshotDeletionPolicy
argument_list|(
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sourceWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|sourceDir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|replicator
operator|=
operator|new
name|LocalReplicator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
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
name|sourceWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|replicator
argument_list|,
name|sourceDir
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|createRevision
specifier|private
name|Revision
name|createRevision
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|sourceWriter
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|sourceWriter
operator|.
name|setCommitData
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
name|VERSION_ID
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|sourceWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
operator|new
name|IndexRevision
argument_list|(
name|sourceWriter
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testCheckForUpdateNoRevisions
specifier|public
name|void
name|testCheckForUpdateNoRevisions
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
name|replicator
operator|.
name|checkForUpdate
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testObtainFileAlreadyClosed
specifier|public
name|void
name|testObtainFileAlreadyClosed
parameter_list|()
throws|throws
name|IOException
block|{
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|SessionToken
name|res
init|=
name|replicator
operator|.
name|checkForUpdate
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|res
operator|.
name|sourceFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|entry
init|=
name|res
operator|.
name|sourceFiles
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|replicator
operator|.
name|close
argument_list|()
expr_stmt|;
name|expectThrows
argument_list|(
name|AlreadyClosedException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|replicator
operator|.
name|obtainFile
argument_list|(
name|res
operator|.
name|id
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|fileName
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPublishAlreadyClosed
specifier|public
name|void
name|testPublishAlreadyClosed
parameter_list|()
throws|throws
name|IOException
block|{
name|replicator
operator|.
name|close
argument_list|()
expr_stmt|;
name|expectThrows
argument_list|(
name|AlreadyClosedException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateAlreadyClosed
specifier|public
name|void
name|testUpdateAlreadyClosed
parameter_list|()
throws|throws
name|IOException
block|{
name|replicator
operator|.
name|close
argument_list|()
expr_stmt|;
name|expectThrows
argument_list|(
name|AlreadyClosedException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|replicator
operator|.
name|checkForUpdate
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPublishSameRevision
specifier|public
name|void
name|testPublishSameRevision
parameter_list|()
throws|throws
name|IOException
block|{
name|Revision
name|rev
init|=
name|createRevision
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|rev
argument_list|)
expr_stmt|;
name|SessionToken
name|res
init|=
name|replicator
operator|.
name|checkForUpdate
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rev
operator|.
name|getVersion
argument_list|()
argument_list|,
name|res
operator|.
name|version
argument_list|)
expr_stmt|;
name|replicator
operator|.
name|release
argument_list|(
name|res
operator|.
name|id
argument_list|)
expr_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
operator|new
name|IndexRevision
argument_list|(
name|sourceWriter
argument_list|)
argument_list|)
expr_stmt|;
name|res
operator|=
name|replicator
operator|.
name|checkForUpdate
argument_list|(
name|res
operator|.
name|version
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|res
argument_list|)
expr_stmt|;
comment|// now make sure that publishing same revision doesn't leave revisions
comment|// "locked", i.e. that replicator releases revisions even when they are not
comment|// kept
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DirectoryReader
operator|.
name|listCommits
argument_list|(
name|sourceDir
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPublishOlderRev
specifier|public
name|void
name|testPublishOlderRev
parameter_list|()
throws|throws
name|IOException
block|{
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Revision
name|old
init|=
operator|new
name|IndexRevision
argument_list|(
name|sourceWriter
argument_list|)
decl_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// should fail to publish an older revision
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|replicator
operator|.
name|publish
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DirectoryReader
operator|.
name|listCommits
argument_list|(
name|sourceDir
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testObtainMissingFile
specifier|public
name|void
name|testObtainMissingFile
parameter_list|()
throws|throws
name|IOException
block|{
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|SessionToken
name|res
init|=
name|replicator
operator|.
name|checkForUpdate
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|replicator
operator|.
name|obtainFile
argument_list|(
name|res
operator|.
name|id
argument_list|,
name|res
operator|.
name|sourceFiles
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
literal|"madeUpFile"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed obtaining an unrecognized file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
decl||
name|NoSuchFileException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testSessionExpiration
specifier|public
name|void
name|testSessionExpiration
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|SessionToken
name|session
init|=
name|replicator
operator|.
name|checkForUpdate
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|replicator
operator|.
name|setExpirationThreshold
argument_list|(
literal|5
argument_list|)
expr_stmt|;
comment|// expire quickly
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
comment|// sufficient for expiration
comment|// should fail to obtain a file for an expired session
name|expectThrows
argument_list|(
name|SessionExpiredException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|replicator
operator|.
name|obtainFile
argument_list|(
name|session
operator|.
name|id
argument_list|,
name|session
operator|.
name|sourceFiles
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
name|session
operator|.
name|sourceFiles
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|fileName
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateToLatest
specifier|public
name|void
name|testUpdateToLatest
parameter_list|()
throws|throws
name|IOException
block|{
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Revision
name|rev
init|=
name|createRevision
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|rev
argument_list|)
expr_stmt|;
name|SessionToken
name|res
init|=
name|replicator
operator|.
name|checkForUpdate
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rev
operator|.
name|compareTo
argument_list|(
name|res
operator|.
name|version
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRevisionRelease
specifier|public
name|void
name|testRevisionRelease
parameter_list|()
throws|throws
name|Exception
block|{
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|sourceDir
argument_list|,
name|IndexFileNames
operator|.
name|SEGMENTS
operator|+
literal|"_1"
argument_list|)
argument_list|)
expr_stmt|;
name|replicator
operator|.
name|publish
argument_list|(
name|createRevision
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// now the files of revision 1 can be deleted
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|sourceDir
argument_list|,
name|IndexFileNames
operator|.
name|SEGMENTS
operator|+
literal|"_2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"segments_1 should not be found in index directory after revision is released"
argument_list|,
name|slowFileExists
argument_list|(
name|sourceDir
argument_list|,
name|IndexFileNames
operator|.
name|SEGMENTS
operator|+
literal|"_1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
