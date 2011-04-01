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
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|DocumentsWriterPerThread
operator|.
name|FlushedSegment
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
name|DocumentsWriterPerThread
operator|.
name|IndexingChain
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
name|DocumentsWriterPerThreadPool
operator|.
name|ThreadState
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
name|FieldInfos
operator|.
name|FieldNumberBiMap
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
name|Query
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
name|SimilarityProvider
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
begin_comment
comment|/**  * This class accepts multiple added documents and directly  * writes a single segment file.  It does this more  * efficiently than creating a single segment per document  * (with DocumentWriter) and doing standard merges on those  * segments.  *  * Each added document is passed to the {@link DocConsumer},  * which in turn processes the document and interacts with  * other consumers in the indexing chain.  Certain  * consumers, like {@link StoredFieldsWriter} and {@link  * TermVectorsTermsWriter}, digest a document and  * immediately write bytes to the "doc store" files (ie,  * they do not consume RAM per document, except while they  * are processing the document).  *  * Other consumers, eg {@link FreqProxTermsWriter} and  * {@link NormsWriter}, buffer bytes in RAM and flush only  * when a new segment is produced.   * Once we have used our allowed RAM buffer, or the number  * of added docs is large enough (in the case we are  * flushing by doc count instead of RAM usage), we create a  * real segment and flush it to the Directory.  *  * Threads:  *  * Multiple threads are allowed into addDocument at once.  * There is an initial synchronized call to getThreadState  * which allocates a ThreadState for this thread.  The same  * thread will get the same ThreadState over time (thread  * affinity) so that if there are consistent patterns (for  * example each thread is indexing a different content  * source) then we make better use of RAM.  Then  * processDocument is called on that ThreadState without  * synchronization (most of the "heavy lifting" is in this  * call).  Finally the synchronized "finishDocument" is  * called to flush changes to the directory.  *  * When flush is called by IndexWriter we forcefully idle  * all threads and flush only once they are all idle.  This  * means you can call flush with a given thread even while  * other threads are actively adding/deleting documents.  *  *  * Exceptions:  *  * Because this class directly updates in-memory posting  * lists, and flushes stored fields and term vectors  * directly to files in the directory, there are certain  * limited times when an exception can corrupt this state.  * For example, a disk full while flushing stored fields  * leaves this file in a corrupt state.  Or, an OOM  * exception while appending to the in-memory posting lists  * can corrupt that posting list.  We call such exceptions  * "aborting exceptions".  In these cases we must call  * abort() to discard all docs added since the last flush.  *  * All other exceptions ("non-aborting exceptions") can  * still partially update the index structures.  These  * updates are consistent, but, they represent only a part  * of the document seen up until the exception was hit.  * When this happens, we immediately mark the document as  * deleted so that the document is always atomically ("all  * or none") added to the index.  */
end_comment
begin_class
DECL|class|DocumentsWriter
specifier|final
class|class
name|DocumentsWriter
block|{
DECL|field|directory
name|Directory
name|directory
decl_stmt|;
DECL|field|closed
specifier|private
specifier|volatile
name|boolean
name|closed
decl_stmt|;
DECL|field|infoStream
name|PrintStream
name|infoStream
decl_stmt|;
DECL|field|similarityProvider
name|SimilarityProvider
name|similarityProvider
decl_stmt|;
DECL|field|newFiles
name|List
argument_list|<
name|String
argument_list|>
name|newFiles
decl_stmt|;
DECL|field|indexWriter
specifier|final
name|IndexWriter
name|indexWriter
decl_stmt|;
DECL|field|numDocsInRAM
specifier|private
name|AtomicInteger
name|numDocsInRAM
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|bufferedDeletesStream
specifier|final
name|BufferedDeletesStream
name|bufferedDeletesStream
decl_stmt|;
comment|// TODO: cutover to BytesRefHash
DECL|field|pendingDeletes
specifier|private
specifier|final
name|BufferedDeletes
name|pendingDeletes
init|=
operator|new
name|BufferedDeletes
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|abortedFiles
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|abortedFiles
decl_stmt|;
comment|// List of files that were written before last abort()
DECL|field|chain
specifier|final
name|IndexingChain
name|chain
decl_stmt|;
DECL|field|perThreadPool
specifier|final
name|DocumentsWriterPerThreadPool
name|perThreadPool
decl_stmt|;
DECL|field|flushPolicy
specifier|final
name|FlushPolicy
name|flushPolicy
decl_stmt|;
DECL|field|flushControl
specifier|final
name|DocumentsWriterFlushControl
name|flushControl
decl_stmt|;
DECL|field|healthiness
specifier|final
name|Healthiness
name|healthiness
decl_stmt|;
DECL|method|DocumentsWriter
name|DocumentsWriter
parameter_list|(
name|IndexWriterConfig
name|config
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|FieldNumberBiMap
name|globalFieldNumbers
parameter_list|,
name|BufferedDeletesStream
name|bufferedDeletesStream
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|indexWriter
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|similarityProvider
operator|=
name|config
operator|.
name|getSimilarityProvider
argument_list|()
expr_stmt|;
name|this
operator|.
name|bufferedDeletesStream
operator|=
name|bufferedDeletesStream
expr_stmt|;
name|this
operator|.
name|perThreadPool
operator|=
name|config
operator|.
name|getIndexerThreadPool
argument_list|()
expr_stmt|;
name|this
operator|.
name|chain
operator|=
name|config
operator|.
name|getIndexingChain
argument_list|()
expr_stmt|;
name|this
operator|.
name|perThreadPool
operator|.
name|initialize
argument_list|(
name|this
argument_list|,
name|globalFieldNumbers
argument_list|,
name|config
argument_list|)
expr_stmt|;
specifier|final
name|FlushPolicy
name|configuredPolicy
init|=
name|config
operator|.
name|getFlushPolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|configuredPolicy
operator|==
literal|null
condition|)
block|{
name|flushPolicy
operator|=
operator|new
name|FlushByRamOrCountsPolicy
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|flushPolicy
operator|=
name|configuredPolicy
expr_stmt|;
block|}
name|flushPolicy
operator|.
name|init
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|healthiness
operator|=
operator|new
name|Healthiness
argument_list|()
expr_stmt|;
specifier|final
name|long
name|maxRamPerDWPT
init|=
name|config
operator|.
name|getRAMPerThreadHardLimitMB
argument_list|()
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|flushControl
operator|=
operator|new
name|DocumentsWriterFlushControl
argument_list|(
name|flushPolicy
argument_list|,
name|perThreadPool
argument_list|,
name|healthiness
argument_list|,
name|pendingDeletes
argument_list|,
name|maxRamPerDWPT
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteQueries
name|boolean
name|deleteQueries
parameter_list|(
specifier|final
name|Query
modifier|...
name|queries
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
for|for
control|(
name|Query
name|query
range|:
name|queries
control|)
block|{
name|pendingDeletes
operator|.
name|addQuery
argument_list|(
name|query
argument_list|,
name|BufferedDeletes
operator|.
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|threadsIterator
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|threadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ThreadState
name|state
init|=
name|threadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|state
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|state
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|state
operator|.
name|perThread
operator|.
name|deleteQueries
argument_list|(
name|queries
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|state
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|deleteQuery
name|boolean
name|deleteQuery
parameter_list|(
specifier|final
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|deleteQueries
argument_list|(
name|query
argument_list|)
return|;
block|}
DECL|method|deleteTerms
name|boolean
name|deleteTerms
parameter_list|(
specifier|final
name|Term
modifier|...
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|pendingDeletes
operator|.
name|addTerm
argument_list|(
name|term
argument_list|,
name|BufferedDeletes
operator|.
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
block|}
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|threadsIterator
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|threadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ThreadState
name|state
init|=
name|threadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|state
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|state
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|state
operator|.
name|perThread
operator|.
name|deleteTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|flushControl
operator|.
name|doOnDelete
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|state
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|flushControl
operator|.
name|flushDeletes
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
condition|)
block|{
name|flushDeletes
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|// TODO: we could check w/ FreqProxTermsWriter: if the
comment|// term doesn't exist, don't bother buffering into the
comment|// per-DWPT map (but still must go into the global map)
DECL|method|deleteTerm
name|boolean
name|deleteTerm
parameter_list|(
specifier|final
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|deleteTerms
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|deleteTerm
name|void
name|deleteTerm
parameter_list|(
specifier|final
name|Term
name|term
parameter_list|,
name|ThreadState
name|exclude
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|pendingDeletes
operator|.
name|addTerm
argument_list|(
name|term
argument_list|,
name|BufferedDeletes
operator|.
name|MAX_INT
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|threadsIterator
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|threadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ThreadState
name|state
init|=
name|threadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|!=
name|exclude
condition|)
block|{
name|state
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|state
operator|.
name|perThread
operator|.
name|deleteTerms
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|flushControl
operator|.
name|doOnDelete
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|state
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|flushControl
operator|.
name|flushDeletes
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
condition|)
block|{
name|flushDeletes
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|flushDeletes
specifier|private
name|void
name|flushDeletes
parameter_list|()
throws|throws
name|IOException
block|{
name|maybePushPendingDeletes
argument_list|()
expr_stmt|;
name|indexWriter
operator|.
name|applyAllDeletes
argument_list|()
expr_stmt|;
name|indexWriter
operator|.
name|flushCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
DECL|method|setInfoStream
specifier|synchronized
name|void
name|setInfoStream
parameter_list|(
name|PrintStream
name|infoStream
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
name|pushConfigChange
argument_list|()
expr_stmt|;
block|}
DECL|method|pushConfigChange
specifier|private
specifier|final
name|void
name|pushConfigChange
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|it
init|=
name|perThreadPool
operator|.
name|getAllPerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|DocumentsWriterPerThread
name|perThread
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|perThread
decl_stmt|;
name|perThread
operator|.
name|docState
operator|.
name|infoStream
operator|=
name|this
operator|.
name|infoStream
expr_stmt|;
block|}
block|}
comment|/** Returns how many docs are currently buffered in RAM. */
DECL|method|getNumDocs
name|int
name|getNumDocs
parameter_list|()
block|{
return|return
name|numDocsInRAM
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|abortedFiles
name|Collection
argument_list|<
name|String
argument_list|>
name|abortedFiles
parameter_list|()
block|{
return|return
name|abortedFiles
return|;
block|}
comment|// returns boolean for asserts
DECL|method|message
name|boolean
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|indexWriter
operator|.
name|message
argument_list|(
literal|"DW: "
operator|+
name|message
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this IndexWriter is closed"
argument_list|)
throw|;
block|}
block|}
comment|/** Called if we hit an exception at a bad time (when    *  updating the index files) and must discard all    *  currently buffered docs.  This resets our state,    *  discarding any docs added since last flush. */
DECL|method|abort
specifier|synchronized
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|pendingDeletes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"docWriter: abort"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|threadsIterator
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|threadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ThreadState
name|perThread
init|=
name|threadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|perThread
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|perThread
operator|.
name|isActive
argument_list|()
condition|)
block|{
comment|// we might be closed
name|perThread
operator|.
name|perThread
operator|.
name|abort
argument_list|()
expr_stmt|;
name|perThread
operator|.
name|perThread
operator|.
name|checkAndResetHasAborted
argument_list|()
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|closed
assert|;
block|}
block|}
finally|finally
block|{
name|perThread
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
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
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"docWriter: done abort; abortedFiles="
operator|+
name|abortedFiles
operator|+
literal|" success="
operator|+
name|success
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|anyChanges
name|boolean
name|anyChanges
parameter_list|()
block|{
return|return
name|numDocsInRAM
operator|.
name|get
argument_list|()
operator|!=
literal|0
operator|||
name|anyDeletions
argument_list|()
return|;
block|}
DECL|method|getBufferedDeleteTermsSize
specifier|public
name|int
name|getBufferedDeleteTermsSize
parameter_list|()
block|{
return|return
name|pendingDeletes
operator|.
name|terms
operator|.
name|size
argument_list|()
return|;
block|}
comment|//for testing
DECL|method|getNumBufferedDeleteTerms
specifier|public
name|int
name|getNumBufferedDeleteTerms
parameter_list|()
block|{
return|return
name|pendingDeletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|anyDeletions
specifier|public
name|boolean
name|anyDeletions
parameter_list|()
block|{
return|return
name|pendingDeletes
operator|.
name|any
argument_list|()
return|;
block|}
DECL|method|close
name|void
name|close
parameter_list|()
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|flushControl
operator|.
name|setClosed
argument_list|()
expr_stmt|;
block|}
DECL|method|updateDocument
name|boolean
name|updateDocument
parameter_list|(
specifier|final
name|Document
name|doc
parameter_list|,
specifier|final
name|Analyzer
name|analyzer
parameter_list|,
specifier|final
name|Term
name|delTerm
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|boolean
name|maybeMerge
init|=
literal|false
decl_stmt|;
specifier|final
name|boolean
name|isUpdate
init|=
name|delTerm
operator|!=
literal|null
decl_stmt|;
if|if
condition|(
name|healthiness
operator|.
name|isStalled
argument_list|()
condition|)
block|{
comment|/*        * if we are allowed to hijack threads for flushing we try to flush out         * as many pending DWPT to release memory and get back healthy status.        */
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"WARNING DocumentsWriter is stalled try to hijack thread to flush pending segment"
argument_list|)
expr_stmt|;
block|}
comment|// try pick up pending threads here if possile
specifier|final
name|DocumentsWriterPerThread
name|flushingDWPT
decl_stmt|;
name|flushingDWPT
operator|=
name|flushControl
operator|.
name|getFlushIfPending
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// don't push the delete here since the update could fail!
name|maybeMerge
operator|=
name|doFlush
argument_list|(
name|flushingDWPT
argument_list|)
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
operator|&&
name|healthiness
operator|.
name|isStalled
argument_list|()
condition|)
block|{
name|message
argument_list|(
literal|"WARNING DocumentsWriter is stalled might block thread until DocumentsWriter is not stalled anymore"
argument_list|)
expr_stmt|;
block|}
name|healthiness
operator|.
name|waitIfStalled
argument_list|()
expr_stmt|;
comment|// block if stalled
block|}
name|ThreadState
name|perThread
init|=
name|perThreadPool
operator|.
name|getAndLock
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|this
argument_list|,
name|doc
argument_list|)
decl_stmt|;
name|DocumentsWriterPerThread
name|flushingDWPT
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|perThread
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
assert|assert
literal|false
operator|:
literal|"perThread is not active but we are still open"
assert|;
block|}
specifier|final
name|DocumentsWriterPerThread
name|dwpt
init|=
name|perThread
operator|.
name|perThread
decl_stmt|;
try|try
block|{
name|dwpt
operator|.
name|updateDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|,
name|delTerm
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|dwpt
operator|.
name|checkAndResetHasAborted
argument_list|()
condition|)
block|{
name|flushControl
operator|.
name|doOnAbort
argument_list|(
name|perThread
argument_list|)
expr_stmt|;
block|}
block|}
name|flushingDWPT
operator|=
name|flushControl
operator|.
name|doAfterDocument
argument_list|(
name|perThread
argument_list|,
name|isUpdate
argument_list|)
expr_stmt|;
name|numDocsInRAM
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|perThread
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
comment|// delete term from other DWPTs later, so that this thread
comment|// doesn't have to lock multiple DWPTs at the same time
if|if
condition|(
name|isUpdate
condition|)
block|{
name|deleteTerm
argument_list|(
name|delTerm
argument_list|,
name|perThread
argument_list|)
expr_stmt|;
block|}
name|maybeMerge
operator||=
name|doFlush
argument_list|(
name|flushingDWPT
argument_list|)
expr_stmt|;
return|return
name|maybeMerge
return|;
block|}
DECL|method|doFlush
specifier|private
name|boolean
name|doFlush
parameter_list|(
name|DocumentsWriterPerThread
name|flushingDWPT
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|maybeMerge
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|flushingDWPT
operator|!=
literal|null
condition|)
block|{
name|maybeMerge
operator|=
literal|true
expr_stmt|;
try|try
block|{
comment|// flush concurrently without locking
specifier|final
name|FlushedSegment
name|newSegment
init|=
name|flushingDWPT
operator|.
name|flush
argument_list|()
decl_stmt|;
name|finishFlushedSegment
argument_list|(
name|newSegment
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|flushControl
operator|.
name|doAfterFlush
argument_list|(
name|flushingDWPT
argument_list|)
expr_stmt|;
name|flushingDWPT
operator|.
name|checkAndResetHasAborted
argument_list|()
expr_stmt|;
name|indexWriter
operator|.
name|flushCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|flushingDWPT
operator|=
name|flushControl
operator|.
name|nextPendingFlush
argument_list|()
expr_stmt|;
block|}
return|return
name|maybeMerge
return|;
block|}
DECL|method|finishFlushedSegment
specifier|private
name|void
name|finishFlushedSegment
parameter_list|(
name|FlushedSegment
name|newSegment
parameter_list|)
throws|throws
name|IOException
block|{
name|pushDeletes
argument_list|(
name|newSegment
argument_list|)
expr_stmt|;
if|if
condition|(
name|newSegment
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|addFlushedSegment
argument_list|(
name|newSegment
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|subtractFlushedNumDocs
specifier|final
name|void
name|subtractFlushedNumDocs
parameter_list|(
name|int
name|numFlushed
parameter_list|)
block|{
name|int
name|oldValue
init|=
name|numDocsInRAM
operator|.
name|get
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|numDocsInRAM
operator|.
name|compareAndSet
argument_list|(
name|oldValue
argument_list|,
name|oldValue
operator|-
name|numFlushed
argument_list|)
condition|)
block|{
name|oldValue
operator|=
name|numDocsInRAM
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|pushDeletes
specifier|private
specifier|synchronized
name|void
name|pushDeletes
parameter_list|(
name|FlushedSegment
name|flushedSegment
parameter_list|)
block|{
name|maybePushPendingDeletes
argument_list|()
expr_stmt|;
if|if
condition|(
name|flushedSegment
operator|!=
literal|null
condition|)
block|{
name|BufferedDeletes
name|deletes
init|=
name|flushedSegment
operator|.
name|segmentDeletes
decl_stmt|;
specifier|final
name|long
name|delGen
init|=
name|bufferedDeletesStream
operator|.
name|getNextGen
argument_list|()
decl_stmt|;
comment|// Lock order: DW -> BD
if|if
condition|(
name|deletes
operator|!=
literal|null
operator|&&
name|deletes
operator|.
name|any
argument_list|()
condition|)
block|{
specifier|final
name|FrozenBufferedDeletes
name|packet
init|=
operator|new
name|FrozenBufferedDeletes
argument_list|(
name|deletes
argument_list|,
name|delGen
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"flush: push buffered deletes"
argument_list|)
expr_stmt|;
block|}
name|bufferedDeletesStream
operator|.
name|push
argument_list|(
name|packet
argument_list|)
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|message
argument_list|(
literal|"flush: delGen="
operator|+
name|packet
operator|.
name|gen
argument_list|)
expr_stmt|;
block|}
block|}
name|flushedSegment
operator|.
name|segmentInfo
operator|.
name|setBufferedDeletesGen
argument_list|(
name|delGen
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|maybePushPendingDeletes
specifier|private
specifier|synchronized
specifier|final
name|void
name|maybePushPendingDeletes
parameter_list|()
block|{
specifier|final
name|long
name|delGen
init|=
name|bufferedDeletesStream
operator|.
name|getNextGen
argument_list|()
decl_stmt|;
if|if
condition|(
name|pendingDeletes
operator|.
name|any
argument_list|()
condition|)
block|{
name|indexWriter
operator|.
name|bufferedDeletesStream
operator|.
name|push
argument_list|(
operator|new
name|FrozenBufferedDeletes
argument_list|(
name|pendingDeletes
argument_list|,
name|delGen
argument_list|)
argument_list|)
expr_stmt|;
name|pendingDeletes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|flushAllThreads
specifier|final
name|boolean
name|flushAllThreads
parameter_list|(
specifier|final
name|boolean
name|flushDeletes
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|threadsIterator
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
name|boolean
name|anythingFlushed
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|threadsIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|ThreadState
name|perThread
init|=
name|threadsIterator
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|DocumentsWriterPerThread
name|flushingDWPT
decl_stmt|;
comment|/*        * TODO: maybe we can leverage incoming / indexing threads here if we mark        * all active threads pending so that we don't need to block until we got        * the handle. Yet, we need to figure out how to identify that a certain        * DWPT has been flushed since they are simply replaced once checked out        * for flushing. This would give us another level of concurrency during        * commit.        *         * Maybe we simply iterate them and store the ThreadStates and mark        * all as flushPending and at the same time record the DWPT instance as a        * key for the pending ThreadState. This way we can easily iterate until        * all DWPT have changed.        */
name|perThread
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|perThread
operator|.
name|isActive
argument_list|()
condition|)
block|{
assert|assert
name|closed
assert|;
continue|continue;
comment|//this perThread is already done maybe by a concurrently indexing thread
block|}
specifier|final
name|DocumentsWriterPerThread
name|dwpt
init|=
name|perThread
operator|.
name|perThread
decl_stmt|;
comment|// Always flush docs if there are any
specifier|final
name|boolean
name|flushDocs
init|=
name|dwpt
operator|.
name|getNumDocsInRAM
argument_list|()
operator|>
literal|0
decl_stmt|;
specifier|final
name|String
name|segment
init|=
name|dwpt
operator|.
name|getSegment
argument_list|()
decl_stmt|;
comment|// If we are flushing docs, segment must not be null:
assert|assert
name|segment
operator|!=
literal|null
operator|||
operator|!
name|flushDocs
assert|;
if|if
condition|(
name|flushDocs
condition|)
block|{
comment|// check out and set pending if not already set
name|flushingDWPT
operator|=
name|flushControl
operator|.
name|tryCheckoutForFlush
argument_list|(
name|perThread
argument_list|,
literal|true
argument_list|)
expr_stmt|;
assert|assert
name|flushingDWPT
operator|!=
literal|null
operator|:
literal|"DWPT must never be null here since we hold the lock and it holds documents"
assert|;
assert|assert
name|dwpt
operator|==
name|flushingDWPT
operator|:
literal|"flushControl returned different DWPT"
assert|;
try|try
block|{
specifier|final
name|FlushedSegment
name|newSegment
init|=
name|dwpt
operator|.
name|flush
argument_list|()
decl_stmt|;
name|anythingFlushed
operator|=
literal|true
expr_stmt|;
name|finishFlushedSegment
argument_list|(
name|newSegment
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|flushControl
operator|.
name|doAfterFlush
argument_list|(
name|flushingDWPT
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|perThread
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|anythingFlushed
operator|&&
name|flushDeletes
condition|)
block|{
name|maybePushPendingDeletes
argument_list|()
expr_stmt|;
block|}
return|return
name|anythingFlushed
return|;
block|}
comment|//  /* We have three pools of RAM: Postings, byte blocks
comment|//   * (holds freq/prox posting data) and per-doc buffers
comment|//   * (stored fields/term vectors).  Different docs require
comment|//   * varying amount of storage from these classes.  For
comment|//   * example, docs with many unique single-occurrence short
comment|//   * terms will use up the Postings RAM and hardly any of
comment|//   * the other two.  Whereas docs with very large terms will
comment|//   * use alot of byte blocks RAM.  This method just frees
comment|//   * allocations from the pools once we are over-budget,
comment|//   * which balances the pools to match the current docs. */
comment|//  void balanceRAM() {
comment|//
comment|//    final boolean doBalance;
comment|//    final long deletesRAMUsed;
comment|//
comment|//    deletesRAMUsed = bufferedDeletes.bytesUsed();
comment|//
comment|//    synchronized(this) {
comment|//      if (ramBufferSize == IndexWriterConfig.DISABLE_AUTO_FLUSH || bufferIsFull) {
comment|//        return;
comment|//      }
comment|//
comment|//      doBalance = bytesUsed() + deletesRAMUsed>= ramBufferSize;
comment|//    }
comment|//
comment|//    if (doBalance) {
comment|//
comment|//      if (infoStream != null)
comment|//        message("  RAM: balance allocations: usedMB=" + toMB(bytesUsed()) +
comment|//                " vs trigger=" + toMB(ramBufferSize) +
comment|//                " deletesMB=" + toMB(deletesRAMUsed) +
comment|//                " byteBlockFree=" + toMB(byteBlockAllocator.bytesUsed()) +
comment|//                " perDocFree=" + toMB(perDocAllocator.bytesUsed()));
comment|//
comment|//      final long startBytesUsed = bytesUsed() + deletesRAMUsed;
comment|//
comment|//      int iter = 0;
comment|//
comment|//      // We free equally from each pool in 32 KB
comment|//      // chunks until we are below our threshold
comment|//      // (freeLevel)
comment|//
comment|//      boolean any = true;
comment|//
comment|//      while(bytesUsed()+deletesRAMUsed> freeLevel) {
comment|//
comment|//        synchronized(this) {
comment|//          if (0 == perDocAllocator.numBufferedBlocks()&&
comment|//              0 == byteBlockAllocator.numBufferedBlocks()&&
comment|//              0 == freeIntBlocks.size()&& !any) {
comment|//            // Nothing else to free -- must flush now.
comment|//            bufferIsFull = bytesUsed()+deletesRAMUsed> ramBufferSize;
comment|//            if (infoStream != null) {
comment|//              if (bytesUsed()+deletesRAMUsed> ramBufferSize)
comment|//                message("    nothing to free; set bufferIsFull");
comment|//              else
comment|//                message("    nothing to free");
comment|//            }
comment|//            break;
comment|//          }
comment|//
comment|//          if ((0 == iter % 4)&& byteBlockAllocator.numBufferedBlocks()> 0) {
comment|//            byteBlockAllocator.freeBlocks(1);
comment|//          }
comment|//          if ((1 == iter % 4)&& freeIntBlocks.size()> 0) {
comment|//            freeIntBlocks.remove(freeIntBlocks.size()-1);
comment|//            bytesUsed.addAndGet(-INT_BLOCK_SIZE * RamUsageEstimator.NUM_BYTES_INT);
comment|//          }
comment|//          if ((2 == iter % 4)&& perDocAllocator.numBufferedBlocks()> 0) {
comment|//            perDocAllocator.freeBlocks(32); // Remove upwards of 32 blocks (each block is 1K)
comment|//          }
comment|//        }
comment|//
comment|//        if ((3 == iter % 4)&& any)
comment|//          // Ask consumer to free any recycled state
comment|//          any = consumer.freeRAM();
comment|//
comment|//        iter++;
comment|//      }
comment|//
comment|//      if (infoStream != null)
comment|//        message("    after free: freedMB=" + nf.format((startBytesUsed-bytesUsed()-deletesRAMUsed)/1024./1024.) + " usedMB=" + nf.format((bytesUsed()+deletesRAMUsed)/1024./1024.));
comment|//    }
comment|//  }
block|}
end_class
end_unit
