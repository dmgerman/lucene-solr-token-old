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
name|java
operator|.
name|util
operator|.
name|Map
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
name|CodecUtil
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
name|IOContext
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
name|IndexOutput
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
begin_comment
comment|/**  * A {@link SnapshotDeletionPolicy} which adds a persistence layer so that  * snapshots can be maintained across the life of an application. The snapshots  * are persisted in a {@link Directory} and are committed as soon as  * {@link #snapshot()} or {@link #release(IndexCommit)} is called.  *<p>  *<b>NOTE:</b> Sharing {@link PersistentSnapshotDeletionPolicy}s that write to  * the same directory across {@link IndexWriter}s will corrupt snapshots. You  * should make sure every {@link IndexWriter} has its own  * {@link PersistentSnapshotDeletionPolicy} and that they all write to a  * different {@link Directory}.  It is OK to use the same  * Directory that holds the index.  *  *<p> This class adds a {@link #release(long)} method to  * release commits from a previous snapshot's {@link IndexCommit#getGeneration}.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PersistentSnapshotDeletionPolicy
specifier|public
class|class
name|PersistentSnapshotDeletionPolicy
extends|extends
name|SnapshotDeletionPolicy
block|{
comment|/** Prefix used for the save file. */
DECL|field|SNAPSHOTS_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|SNAPSHOTS_PREFIX
init|=
literal|"snapshots_"
decl_stmt|;
DECL|field|VERSION_START
specifier|private
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|private
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|CODEC_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"snapshots"
decl_stmt|;
comment|// The index writer which maintains the snapshots metadata
DECL|field|nextWriteGen
specifier|private
name|long
name|nextWriteGen
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
comment|/**    * {@link PersistentSnapshotDeletionPolicy} wraps another    * {@link IndexDeletionPolicy} to enable flexible    * snapshotting, passing {@link OpenMode#CREATE_OR_APPEND}    * by default.    *     * @param primary    *          the {@link IndexDeletionPolicy} that is used on non-snapshotted    *          commits. Snapshotted commits, by definition, are not deleted until    *          explicitly released via {@link #release}.    * @param dir    *          the {@link Directory} which will be used to persist the snapshots    *          information.    */
DECL|method|PersistentSnapshotDeletionPolicy
specifier|public
name|PersistentSnapshotDeletionPolicy
parameter_list|(
name|IndexDeletionPolicy
name|primary
parameter_list|,
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|primary
argument_list|,
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@link PersistentSnapshotDeletionPolicy} wraps another    * {@link IndexDeletionPolicy} to enable flexible snapshotting.    *     * @param primary    *          the {@link IndexDeletionPolicy} that is used on non-snapshotted    *          commits. Snapshotted commits, by definition, are not deleted until    *          explicitly released via {@link #release}.    * @param dir    *          the {@link Directory} which will be used to persist the snapshots    *          information.    * @param mode    *          specifies whether a new index should be created, deleting all    *          existing snapshots information (immediately), or open an existing    *          index, initializing the class with the snapshots information.    */
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
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|primary
argument_list|)
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|OpenMode
operator|.
name|CREATE
condition|)
block|{
name|clearPriorSnapshots
argument_list|()
expr_stmt|;
block|}
name|loadPriorSnapshots
argument_list|()
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|OpenMode
operator|.
name|APPEND
operator|&&
name|nextWriteGen
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"no snapshots stored in this directory"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Snapshots the last commit. Once this method returns, the    * snapshot information is persisted in the directory.    *     * @see SnapshotDeletionPolicy#snapshot    */
annotation|@
name|Override
DECL|method|snapshot
specifier|public
specifier|synchronized
name|IndexCommit
name|snapshot
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexCommit
name|ic
init|=
name|super
operator|.
name|snapshot
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|persist
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|super
operator|.
name|release
argument_list|(
name|ic
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Suppress so we keep throwing original exception
block|}
block|}
block|}
return|return
name|ic
return|;
block|}
comment|/**    * Deletes a snapshotted commit. Once this method returns, the snapshot    * information is persisted in the directory.    *     * @see SnapshotDeletionPolicy#release    */
annotation|@
name|Override
DECL|method|release
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|release
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|persist
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|incRef
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Suppress so we keep throwing original exception
block|}
block|}
block|}
block|}
comment|/**    * Deletes a snapshotted commit by generation. Once this method returns, the snapshot    * information is persisted in the directory.    *     * @see IndexCommit#getGeneration    * @see SnapshotDeletionPolicy#release    */
DECL|method|release
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|(
name|long
name|gen
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|releaseGen
argument_list|(
name|gen
argument_list|)
expr_stmt|;
name|persist
argument_list|()
expr_stmt|;
block|}
DECL|method|persist
specifier|synchronized
specifier|private
name|void
name|persist
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|fileName
init|=
name|SNAPSHOTS_PREFIX
operator|+
name|nextWriteGen
decl_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|refCounts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
name|ent
range|:
name|refCounts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
name|dir
operator|.
name|sync
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|fileName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextWriteGen
operator|>
literal|0
condition|)
block|{
name|String
name|lastSaveFile
init|=
name|SNAPSHOTS_PREFIX
operator|+
operator|(
name|nextWriteGen
operator|-
literal|1
operator|)
decl_stmt|;
comment|// exception OK: likely it didn't exist
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|dir
argument_list|,
name|lastSaveFile
argument_list|)
expr_stmt|;
block|}
name|nextWriteGen
operator|++
expr_stmt|;
block|}
DECL|method|clearPriorSnapshots
specifier|private
specifier|synchronized
name|void
name|clearPriorSnapshots
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|file
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|file
operator|.
name|startsWith
argument_list|(
name|SNAPSHOTS_PREFIX
argument_list|)
condition|)
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Returns the file name the snapshots are currently    *  saved to, or null if no snapshots have been saved. */
DECL|method|getLastSaveFile
specifier|public
name|String
name|getLastSaveFile
parameter_list|()
block|{
if|if
condition|(
name|nextWriteGen
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|SNAPSHOTS_PREFIX
operator|+
operator|(
name|nextWriteGen
operator|-
literal|1
operator|)
return|;
block|}
block|}
comment|/**    * Reads the snapshots information from the given {@link Directory}. This    * method can be used if the snapshots information is needed, however you    * cannot instantiate the deletion policy (because e.g., some other process    * keeps a lock on the snapshots directory).    */
DECL|method|loadPriorSnapshots
specifier|private
specifier|synchronized
name|void
name|loadPriorSnapshots
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|genLoaded
init|=
operator|-
literal|1
decl_stmt|;
name|IOException
name|ioe
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|snapshotFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|dir
operator|.
name|listAll
argument_list|()
control|)
block|{
if|if
condition|(
name|file
operator|.
name|startsWith
argument_list|(
name|SNAPSHOTS_PREFIX
argument_list|)
condition|)
block|{
name|long
name|gen
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|file
operator|.
name|substring
argument_list|(
name|SNAPSHOTS_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|genLoaded
operator|==
operator|-
literal|1
operator|||
name|gen
operator|>
name|genLoaded
condition|)
block|{
name|snapshotFiles
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|file
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_START
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|in
operator|.
name|readVInt
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|long
name|commitGen
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
name|int
name|refCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|commitGen
argument_list|,
name|refCount
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe2
parameter_list|)
block|{
comment|// Save first exception& throw in the end
if|if
condition|(
name|ioe
operator|==
literal|null
condition|)
block|{
name|ioe
operator|=
name|ioe2
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|genLoaded
operator|=
name|gen
expr_stmt|;
name|refCounts
operator|.
name|clear
argument_list|()
expr_stmt|;
name|refCounts
operator|.
name|putAll
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|genLoaded
operator|==
operator|-
literal|1
condition|)
block|{
comment|// Nothing was loaded...
if|if
condition|(
name|ioe
operator|!=
literal|null
condition|)
block|{
comment|// ... not for lack of trying:
throw|throw
name|ioe
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|snapshotFiles
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// Remove any broken / old snapshot files:
name|String
name|curFileName
init|=
name|SNAPSHOTS_PREFIX
operator|+
name|genLoaded
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|snapshotFiles
control|)
block|{
if|if
condition|(
operator|!
name|curFileName
operator|.
name|equals
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|dir
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|nextWriteGen
operator|=
literal|1
operator|+
name|genLoaded
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
