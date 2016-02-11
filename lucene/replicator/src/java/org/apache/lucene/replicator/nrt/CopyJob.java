begin_unit
begin_package
DECL|package|org.apache.lucene.replicator.nrt
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
operator|.
name|nrt
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
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
name|Locale
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
name|Set
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
name|ConcurrentHashMap
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
name|atomic
operator|.
name|AtomicLong
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
name|CorruptIndexException
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
comment|/** Handles copying one set of files, e.g. all files for a new NRT point, or files for pre-copying a merged segment.  *  This notifies the caller via OnceDone when the job finishes or failed.  *  * @lucene.experimental */
end_comment
begin_class
DECL|class|CopyJob
specifier|public
specifier|abstract
class|class
name|CopyJob
implements|implements
name|Comparable
argument_list|<
name|CopyJob
argument_list|>
block|{
DECL|field|counter
specifier|private
specifier|final
specifier|static
name|AtomicLong
name|counter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|dest
specifier|protected
specifier|final
name|ReplicaNode
name|dest
decl_stmt|;
DECL|field|files
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|files
decl_stmt|;
DECL|field|ord
specifier|public
specifier|final
name|long
name|ord
init|=
name|counter
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
comment|/** True for an NRT sync, false for pre-copying a newly merged segment */
DECL|field|highPriority
specifier|public
specifier|final
name|boolean
name|highPriority
decl_stmt|;
DECL|field|onceDone
specifier|public
specifier|final
name|OnceDone
name|onceDone
decl_stmt|;
DECL|field|startNS
specifier|public
specifier|final
name|long
name|startNS
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
DECL|field|reason
specifier|public
specifier|final
name|String
name|reason
decl_stmt|;
DECL|field|toCopy
specifier|protected
specifier|final
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
argument_list|>
name|toCopy
decl_stmt|;
DECL|field|totBytes
specifier|protected
name|long
name|totBytes
decl_stmt|;
DECL|field|totBytesCopied
specifier|protected
name|long
name|totBytesCopied
decl_stmt|;
comment|// The file we are currently copying:
DECL|field|current
specifier|protected
name|CopyOneFile
name|current
decl_stmt|;
comment|// Set when we are cancelled
DECL|field|exc
specifier|protected
specifier|volatile
name|Throwable
name|exc
decl_stmt|;
DECL|field|cancelReason
specifier|protected
specifier|volatile
name|String
name|cancelReason
decl_stmt|;
comment|// toString may concurrently access this:
DECL|field|copiedFiles
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|copiedFiles
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|CopyJob
specifier|protected
name|CopyJob
parameter_list|(
name|String
name|reason
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|files
parameter_list|,
name|ReplicaNode
name|dest
parameter_list|,
name|boolean
name|highPriority
parameter_list|,
name|OnceDone
name|onceDone
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
name|this
operator|.
name|files
operator|=
name|files
expr_stmt|;
name|this
operator|.
name|dest
operator|=
name|dest
expr_stmt|;
name|this
operator|.
name|highPriority
operator|=
name|highPriority
expr_stmt|;
name|this
operator|.
name|onceDone
operator|=
name|onceDone
expr_stmt|;
comment|// Exceptions in here are bad:
try|try
block|{
name|this
operator|.
name|toCopy
operator|=
name|dest
operator|.
name|getFilesToCopy
argument_list|(
name|this
operator|.
name|files
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|cancel
argument_list|(
literal|"exc during init"
argument_list|,
name|t
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"exception while checking local files"
argument_list|,
literal|"n/a"
argument_list|,
name|t
argument_list|)
throw|;
block|}
block|}
comment|/** Callback invoked by CopyJob once all files have (finally) finished copying */
DECL|interface|OnceDone
specifier|public
interface|interface
name|OnceDone
block|{
DECL|method|run
specifier|public
name|void
name|run
parameter_list|(
name|CopyJob
name|job
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/** Transfers whatever tmp files were already copied in this previous job and cancels the previous job */
DECL|method|transferAndCancel
specifier|public
specifier|synchronized
name|void
name|transferAndCancel
parameter_list|(
name|CopyJob
name|prevJob
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|prevJob
init|)
block|{
name|dest
operator|.
name|message
argument_list|(
literal|"CopyJob: now transfer prevJob "
operator|+
name|prevJob
argument_list|)
expr_stmt|;
try|try
block|{
name|_transferAndCancel
argument_list|(
name|prevJob
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|dest
operator|.
name|message
argument_list|(
literal|"xfer: exc during transferAndCancel"
argument_list|)
expr_stmt|;
name|cancel
argument_list|(
literal|"exc during transferAndCancel"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|reThrow
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|_transferAndCancel
specifier|private
specifier|synchronized
name|void
name|_transferAndCancel
parameter_list|(
name|CopyJob
name|prevJob
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Caller must already be sync'd on prevJob:
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|prevJob
argument_list|)
assert|;
if|if
condition|(
name|prevJob
operator|.
name|exc
operator|!=
literal|null
condition|)
block|{
comment|// Already cancelled
name|dest
operator|.
name|message
argument_list|(
literal|"xfer: prevJob was already cancelled; skip transfer"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Cancel the previous job
name|prevJob
operator|.
name|exc
operator|=
operator|new
name|Throwable
argument_list|()
expr_stmt|;
comment|// Carry over already copied files that we also want to copy
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
argument_list|>
name|it
init|=
name|toCopy
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|long
name|bytesAlreadyCopied
init|=
literal|0
decl_stmt|;
comment|// Iterate over all files we think we need to copy:
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FileMetaData
argument_list|>
name|ent
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|prevTmpFileName
init|=
name|prevJob
operator|.
name|copiedFiles
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|prevTmpFileName
operator|!=
literal|null
condition|)
block|{
comment|// This fileName is common to both jobs, and the old job already finished copying it (to a temp file), so we keep it:
name|long
name|fileLength
init|=
name|ent
operator|.
name|getValue
argument_list|()
operator|.
name|length
decl_stmt|;
name|bytesAlreadyCopied
operator|+=
name|fileLength
expr_stmt|;
name|dest
operator|.
name|message
argument_list|(
literal|"xfer: carry over already-copied file "
operator|+
name|fileName
operator|+
literal|" ("
operator|+
name|prevTmpFileName
operator|+
literal|", "
operator|+
name|fileLength
operator|+
literal|" bytes)"
argument_list|)
expr_stmt|;
name|copiedFiles
operator|.
name|put
argument_list|(
name|fileName
argument_list|,
name|prevTmpFileName
argument_list|)
expr_stmt|;
comment|// So we don't try to delete it, below:
name|prevJob
operator|.
name|copiedFiles
operator|.
name|remove
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
comment|// So it's not in our copy list anymore:
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|prevJob
operator|.
name|current
operator|!=
literal|null
operator|&&
name|prevJob
operator|.
name|current
operator|.
name|name
operator|.
name|equals
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
comment|// This fileName is common to both jobs, and it's the file that the previous job was in the process of copying.  In this case
comment|// we continue copying it from the prevoius job.  This is important for cases where we are copying over a large file
comment|// because otherwise we could keep failing the NRT copy and restarting this file from the beginning and never catch up:
name|dest
operator|.
name|message
argument_list|(
literal|"xfer: carry over in-progress file "
operator|+
name|fileName
operator|+
literal|" ("
operator|+
name|prevJob
operator|.
name|current
operator|.
name|tmpName
operator|+
literal|") bytesCopied="
operator|+
name|prevJob
operator|.
name|current
operator|.
name|getBytesCopied
argument_list|()
operator|+
literal|" of "
operator|+
name|prevJob
operator|.
name|current
operator|.
name|bytesToCopy
argument_list|)
expr_stmt|;
name|bytesAlreadyCopied
operator|+=
name|prevJob
operator|.
name|current
operator|.
name|getBytesCopied
argument_list|()
expr_stmt|;
assert|assert
name|current
operator|==
literal|null
assert|;
comment|// must set current first, before writing/read to c.in/out in case that hits an exception, so that we then close the temp
comment|// IndexOutput when cancelling ourselves:
name|current
operator|=
name|newCopyOneFile
argument_list|(
name|prevJob
operator|.
name|current
argument_list|)
expr_stmt|;
comment|// Tell our new (primary) connection we'd like to copy this file first, but resuming from how many bytes we already copied last time:
comment|// We do this even if bytesToCopy == bytesCopied, because we still need to readLong() the checksum from the primary connection:
assert|assert
name|prevJob
operator|.
name|current
operator|.
name|getBytesCopied
argument_list|()
operator|<=
name|prevJob
operator|.
name|current
operator|.
name|bytesToCopy
assert|;
name|prevJob
operator|.
name|current
operator|=
literal|null
expr_stmt|;
name|totBytes
operator|+=
name|current
operator|.
name|metaData
operator|.
name|length
expr_stmt|;
comment|// So it's not in our copy list anymore:
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|dest
operator|.
name|message
argument_list|(
literal|"xfer: file "
operator|+
name|fileName
operator|+
literal|" will be fully copied"
argument_list|)
expr_stmt|;
block|}
block|}
name|dest
operator|.
name|message
argument_list|(
literal|"xfer: "
operator|+
name|bytesAlreadyCopied
operator|+
literal|" bytes already copied of "
operator|+
name|totBytes
argument_list|)
expr_stmt|;
comment|// Delete all temp files the old job wrote but we don't need:
name|dest
operator|.
name|message
argument_list|(
literal|"xfer: now delete old temp files: "
operator|+
name|prevJob
operator|.
name|copiedFiles
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|dest
operator|.
name|dir
argument_list|,
name|prevJob
operator|.
name|copiedFiles
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|prevJob
operator|.
name|current
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|prevJob
operator|.
name|current
argument_list|)
expr_stmt|;
if|if
condition|(
name|Node
operator|.
name|VERBOSE_FILES
condition|)
block|{
name|dest
operator|.
name|message
argument_list|(
literal|"remove partial file "
operator|+
name|prevJob
operator|.
name|current
operator|.
name|tmpName
argument_list|)
expr_stmt|;
block|}
name|dest
operator|.
name|deleter
operator|.
name|deleteNewFile
argument_list|(
name|prevJob
operator|.
name|current
operator|.
name|tmpName
argument_list|)
expr_stmt|;
name|prevJob
operator|.
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|newCopyOneFile
specifier|protected
specifier|abstract
name|CopyOneFile
name|newCopyOneFile
parameter_list|(
name|CopyOneFile
name|current
parameter_list|)
function_decl|;
comment|/** Begin copying files */
DECL|method|start
specifier|public
specifier|abstract
name|void
name|start
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Use current thread (blocking) to do all copying and then return once done, or throw exception on failure */
DECL|method|runBlocking
specifier|public
specifier|abstract
name|void
name|runBlocking
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|cancel
specifier|public
name|void
name|cancel
parameter_list|(
name|String
name|reason
parameter_list|,
name|Throwable
name|exc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|exc
operator|!=
literal|null
condition|)
block|{
comment|// Already cancelled
return|return;
block|}
name|dest
operator|.
name|message
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"top: cancel after copying %s; exc=%s:\n  files=%s\n  copiedFiles=%s"
argument_list|,
name|Node
operator|.
name|bytesToString
argument_list|(
name|totBytesCopied
argument_list|)
argument_list|,
name|exc
argument_list|,
name|files
operator|==
literal|null
condition|?
literal|"null"
else|:
name|files
operator|.
name|keySet
argument_list|()
argument_list|,
name|copiedFiles
operator|.
name|keySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|exc
operator|==
literal|null
condition|)
block|{
name|exc
operator|=
operator|new
name|Throwable
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|exc
operator|=
name|exc
expr_stmt|;
name|this
operator|.
name|cancelReason
operator|=
name|reason
expr_stmt|;
comment|// Delete all temp files we wrote:
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|dest
operator|.
name|dir
argument_list|,
name|copiedFiles
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|current
argument_list|)
expr_stmt|;
if|if
condition|(
name|Node
operator|.
name|VERBOSE_FILES
condition|)
block|{
name|dest
operator|.
name|message
argument_list|(
literal|"remove partial file "
operator|+
name|current
operator|.
name|tmpName
argument_list|)
expr_stmt|;
block|}
name|dest
operator|.
name|deleter
operator|.
name|deleteNewFile
argument_list|(
name|current
operator|.
name|tmpName
argument_list|)
expr_stmt|;
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** Return true if this job is trying to copy any of the same files as the other job */
DECL|method|conflicts
specifier|public
specifier|abstract
name|boolean
name|conflicts
parameter_list|(
name|CopyJob
name|other
parameter_list|)
function_decl|;
comment|/** Renames all copied (tmp) files to their true file names */
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getFailed
specifier|public
specifier|abstract
name|boolean
name|getFailed
parameter_list|()
function_decl|;
comment|/** Returns only those file names (a subset of {@link #getFileNames}) that need to be copied */
DECL|method|getFileNamesToCopy
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getFileNamesToCopy
parameter_list|()
function_decl|;
comment|/** Returns all file names referenced in this copy job */
DECL|method|getFileNames
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getFileNames
parameter_list|()
function_decl|;
DECL|method|getCopyState
specifier|public
specifier|abstract
name|CopyState
name|getCopyState
parameter_list|()
function_decl|;
DECL|method|getTotalBytesCopied
specifier|public
specifier|abstract
name|long
name|getTotalBytesCopied
parameter_list|()
function_decl|;
block|}
end_class
end_unit
