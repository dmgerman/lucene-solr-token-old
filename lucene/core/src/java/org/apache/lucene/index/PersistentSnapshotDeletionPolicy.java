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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|LockObtainFailedException
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
begin_comment
comment|/**  * A {@link SnapshotDeletionPolicy} which adds a persistence layer so that  * snapshots can be maintained across the life of an application. The snapshots  * are persisted in a {@link Directory} and are committed as soon as  * {@link #snapshot(String)} or {@link #release(String)} is called.  *<p>  *<b>NOTE:</b> this class receives a {@link Directory} to persist the data into  * a Lucene index. It is highly recommended to use a dedicated directory (and on  * stable storage as well) for persisting the snapshots' information, and not  * reuse the content index directory, or otherwise conflicts and index  * corruption will occur.  *<p>  *<b>NOTE:</b> you should call {@link #close()} when you're done using this  * class for safety (it will close the {@link IndexWriter} instance used).  */
end_comment
begin_class
DECL|class|PersistentSnapshotDeletionPolicy
specifier|public
class|class
name|PersistentSnapshotDeletionPolicy
extends|extends
name|SnapshotDeletionPolicy
block|{
comment|// Used to validate that the given directory includes just one document w/ the
comment|// given ID field. Otherwise, it's not a valid Directory for snapshotting.
DECL|field|SNAPSHOTS_ID
specifier|private
specifier|static
specifier|final
name|String
name|SNAPSHOTS_ID
init|=
literal|"$SNAPSHOTS_DOC$"
decl_stmt|;
comment|// The index writer which maintains the snapshots metadata
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
comment|/**    * Reads the snapshots information from the given {@link Directory}. This    * method can be used if the snapshots information is needed, however you    * cannot instantiate the deletion policy (because e.g., some other process    * keeps a lock on the snapshots directory).    */
DECL|method|readSnapshotsInfo
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|readSnapshotsInfo
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|snapshots
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|int
name|numDocs
init|=
name|r
operator|.
name|numDocs
argument_list|()
decl_stmt|;
comment|// index is allowed to have exactly one document or 0.
if|if
condition|(
name|numDocs
operator|==
literal|1
condition|)
block|{
name|Document
name|doc
init|=
name|r
operator|.
name|document
argument_list|(
name|r
operator|.
name|maxDoc
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|getField
argument_list|(
name|SNAPSHOTS_ID
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"directory is not a valid snapshots store!"
argument_list|)
throw|;
block|}
name|doc
operator|.
name|removeField
argument_list|(
name|SNAPSHOTS_ID
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexableField
name|f
range|:
name|doc
control|)
block|{
name|snapshots
operator|.
name|put
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|numDocs
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"should be at most 1 document in the snapshots directory: "
operator|+
name|numDocs
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|snapshots
return|;
block|}
comment|/**    * {@link PersistentSnapshotDeletionPolicy} wraps another    * {@link IndexDeletionPolicy} to enable flexible snapshotting.    *     * @param primary    *          the {@link IndexDeletionPolicy} that is used on non-snapshotted    *          commits. Snapshotted commits, by definition, are not deleted until    *          explicitly released via {@link #release(String)}.    * @param dir    *          the {@link Directory} which will be used to persist the snapshots    *          information.    * @param mode    *          specifies whether a new index should be created, deleting all    *          existing snapshots information (immediately), or open an existing    *          index, initializing the class with the snapshots information.    * @param matchVersion    *          specifies the {@link Version} that should be used when opening the    *          IndexWriter.    */
DECL|method|PersistentSnapshotDeletionPolicy
specifier|public
name|PersistentSnapshotDeletionPolicy
parameter_list|(
name|IndexDeletionPolicy
name|primary
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|OpenMode
name|mode
parameter_list|,
name|Version
name|matchVersion
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|primary
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Initialize the index writer over the snapshot directory.
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|matchVersion
argument_list|,
literal|null
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|mode
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|!=
name|OpenMode
operator|.
name|APPEND
condition|)
block|{
comment|// IndexWriter no longer creates a first commit on an empty Directory. So
comment|// if we were asked to CREATE*, call commit() just to be sure. If the
comment|// index contains information and mode is CREATE_OR_APPEND, it's a no-op.
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
try|try
block|{
comment|// Initializes the snapshots information. This code should basically run
comment|// only if mode != CREATE, but if it is, it's no harm as we only open the
comment|// reader once and immediately close it.
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|readSnapshotsInfo
argument_list|(
name|dir
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|registerSnapshotInfo
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// don't leave any open file handles
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// don't leave any open file handles
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|onInit
specifier|public
specifier|synchronized
name|void
name|onInit
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|IndexCommit
argument_list|>
name|commits
parameter_list|)
throws|throws
name|IOException
block|{
comment|// super.onInit() needs to be called first to ensure that initialization
comment|// behaves as expected. The superclass, SnapshotDeletionPolicy, ensures
comment|// that any snapshot IDs with empty IndexCommits are released. Since this
comment|// happens, this class needs to persist these changes.
name|super
operator|.
name|onInit
argument_list|(
name|commits
argument_list|)
expr_stmt|;
name|persistSnapshotInfos
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Snapshots the last commit using the given ID. Once this method returns, the    * snapshot information is persisted in the directory.    *     * @see SnapshotDeletionPolicy#snapshot(String)    */
annotation|@
name|Override
DECL|method|snapshot
specifier|public
specifier|synchronized
name|IndexCommit
name|snapshot
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|checkSnapshotted
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|SNAPSHOTS_ID
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|id
operator|+
literal|" is reserved and cannot be used as a snapshot id"
argument_list|)
throw|;
block|}
name|persistSnapshotInfos
argument_list|(
name|id
argument_list|,
name|lastCommit
operator|.
name|getSegmentsFileName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|snapshot
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * Deletes a snapshotted commit by ID. Once this method returns, the snapshot    * information is committed to the directory.    *     * @see SnapshotDeletionPolicy#release(String)    */
annotation|@
name|Override
DECL|method|release
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|release
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|persistSnapshotInfos
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Closes the index which writes the snapshots to the directory. */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Persists all snapshots information. If the given id and segment are not    * null, it persists their information as well.    */
DECL|method|persistSnapshotInfos
specifier|private
name|void
name|persistSnapshotInfos
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|segment
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|ft
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|SNAPSHOTS_ID
argument_list|,
literal|""
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|super
operator|.
name|getSnapshots
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|id
argument_list|,
name|segment
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
