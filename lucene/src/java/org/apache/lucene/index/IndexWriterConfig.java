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
name|index
operator|.
name|DocumentsWriter
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
name|IndexWriter
operator|.
name|IndexReaderWarmer
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
name|codecs
operator|.
name|CodecProvider
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
name|Similarity
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
comment|/**  * Holds all the configuration of {@link IndexWriter}. This object is only used  * while constructing a new IndexWriter. Those settings cannot be changed  * afterwards, except instantiating a new IndexWriter.  *<p>  * All setter methods return {@link IndexWriterConfig} to allow chaining  * settings conveniently. Thus someone can do:  *   *<pre>  * IndexWriterConfig conf = new IndexWriterConfig(analyzer);  * conf.setter1().setter2();  *</pre>  *   * @since 3.1  */
end_comment
begin_class
DECL|class|IndexWriterConfig
specifier|public
specifier|final
class|class
name|IndexWriterConfig
implements|implements
name|Cloneable
block|{
DECL|field|UNLIMITED_FIELD_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|UNLIMITED_FIELD_LENGTH
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/**    * Specifies the open mode for {@link IndexWriter}:    *<ul>    * {@link #CREATE} - creates a new index or overwrites an existing one.    * {@link #CREATE_OR_APPEND} - creates a new index if one does not exist,    * otherwise it opens the index and documents will be appended.    * {@link #APPEND} - opens an existing index.    *</ul>    */
DECL|enum|OpenMode
DECL|enum constant|CREATE
DECL|enum constant|APPEND
DECL|enum constant|CREATE_OR_APPEND
specifier|public
specifier|static
enum|enum
name|OpenMode
block|{
name|CREATE
block|,
name|APPEND
block|,
name|CREATE_OR_APPEND
block|}
comment|/** Default value is 128. Change using {@link #setTermIndexInterval(int)}. */
DECL|field|DEFAULT_TERM_INDEX_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_TERM_INDEX_INTERVAL
init|=
literal|32
decl_stmt|;
comment|/** Denotes a flush trigger is disabled. */
DECL|field|DISABLE_AUTO_FLUSH
specifier|public
specifier|final
specifier|static
name|int
name|DISABLE_AUTO_FLUSH
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Disabled by default (because IndexWriter flushes by RAM usage by default). */
DECL|field|DEFAULT_MAX_BUFFERED_DELETE_TERMS
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_MAX_BUFFERED_DELETE_TERMS
init|=
name|DISABLE_AUTO_FLUSH
decl_stmt|;
comment|/** Disabled by default (because IndexWriter flushes by RAM usage by default). */
DECL|field|DEFAULT_MAX_BUFFERED_DOCS
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_MAX_BUFFERED_DOCS
init|=
name|DISABLE_AUTO_FLUSH
decl_stmt|;
comment|/**    * Default value is 16 MB (which means flush when buffered docs consume    * approximately 16 MB RAM).    */
DECL|field|DEFAULT_RAM_BUFFER_SIZE_MB
specifier|public
specifier|final
specifier|static
name|double
name|DEFAULT_RAM_BUFFER_SIZE_MB
init|=
literal|16.0
decl_stmt|;
comment|/**    * Default value for the write lock timeout (1,000 ms).    *     * @see #setDefaultWriteLockTimeout(long)    */
DECL|field|WRITE_LOCK_TIMEOUT
specifier|public
specifier|static
name|long
name|WRITE_LOCK_TIMEOUT
init|=
literal|1000
decl_stmt|;
comment|/** Default {@link CodecProvider}. */
DECL|field|DEFAULT_CODEC_PROVIDER
specifier|public
specifier|final
specifier|static
name|CodecProvider
name|DEFAULT_CODEC_PROVIDER
init|=
name|CodecProvider
operator|.
name|getDefault
argument_list|()
decl_stmt|;
comment|/** The maximum number of simultaneous threads that may be    *  indexing documents at once in IndexWriter; if more    *  than this many threads arrive they will wait for    *  others to finish. */
DECL|field|DEFAULT_MAX_THREAD_STATES
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_MAX_THREAD_STATES
init|=
literal|8
decl_stmt|;
comment|/** Default setting for {@link #setReaderPooling}. */
DECL|field|DEFAULT_READER_POOLING
specifier|public
specifier|final
specifier|static
name|boolean
name|DEFAULT_READER_POOLING
init|=
literal|false
decl_stmt|;
comment|/** Default value is 1. Change using {@link #setReaderTermsIndexDivisor(int)}. */
DECL|field|DEFAULT_READER_TERMS_INDEX_DIVISOR
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_READER_TERMS_INDEX_DIVISOR
init|=
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
decl_stmt|;
comment|/**    * Sets the default (for any instance) maximum time to wait for a write lock    * (in milliseconds).    */
DECL|method|setDefaultWriteLockTimeout
specifier|public
specifier|static
name|void
name|setDefaultWriteLockTimeout
parameter_list|(
name|long
name|writeLockTimeout
parameter_list|)
block|{
name|WRITE_LOCK_TIMEOUT
operator|=
name|writeLockTimeout
expr_stmt|;
block|}
comment|/**    * Returns the default write lock timeout for newly instantiated    * IndexWriterConfigs.    *     * @see #setDefaultWriteLockTimeout(long)    */
DECL|method|getDefaultWriteLockTimeout
specifier|public
specifier|static
name|long
name|getDefaultWriteLockTimeout
parameter_list|()
block|{
return|return
name|WRITE_LOCK_TIMEOUT
return|;
block|}
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|delPolicy
specifier|private
name|IndexDeletionPolicy
name|delPolicy
decl_stmt|;
DECL|field|commit
specifier|private
name|IndexCommit
name|commit
decl_stmt|;
DECL|field|openMode
specifier|private
name|OpenMode
name|openMode
decl_stmt|;
DECL|field|maxFieldLength
specifier|private
name|int
name|maxFieldLength
decl_stmt|;
DECL|field|similarity
specifier|private
name|Similarity
name|similarity
decl_stmt|;
DECL|field|termIndexInterval
specifier|private
name|int
name|termIndexInterval
decl_stmt|;
DECL|field|mergeScheduler
specifier|private
name|MergeScheduler
name|mergeScheduler
decl_stmt|;
DECL|field|writeLockTimeout
specifier|private
name|long
name|writeLockTimeout
decl_stmt|;
DECL|field|maxBufferedDeleteTerms
specifier|private
name|int
name|maxBufferedDeleteTerms
decl_stmt|;
DECL|field|ramBufferSizeMB
specifier|private
name|double
name|ramBufferSizeMB
decl_stmt|;
DECL|field|maxBufferedDocs
specifier|private
name|int
name|maxBufferedDocs
decl_stmt|;
DECL|field|indexingChain
specifier|private
name|IndexingChain
name|indexingChain
decl_stmt|;
DECL|field|mergedSegmentWarmer
specifier|private
name|IndexReaderWarmer
name|mergedSegmentWarmer
decl_stmt|;
DECL|field|codecProvider
specifier|private
name|CodecProvider
name|codecProvider
decl_stmt|;
DECL|field|mergePolicy
specifier|private
name|MergePolicy
name|mergePolicy
decl_stmt|;
DECL|field|maxThreadStates
specifier|private
name|int
name|maxThreadStates
decl_stmt|;
DECL|field|readerPooling
specifier|private
name|boolean
name|readerPooling
decl_stmt|;
DECL|field|readerTermsIndexDivisor
specifier|private
name|int
name|readerTermsIndexDivisor
decl_stmt|;
comment|// required for clone
DECL|field|matchVersion
specifier|private
name|Version
name|matchVersion
decl_stmt|;
comment|/**    * Creates a new config that with defaults that match the specified    * {@link Version} as well as the default {@link Analyzer}. {@link Version} is    * a placeholder for future changes. The default settings are relevant to 3.1    * and before. In the future, if different settings will apply to different    * versions, they will be documented here.    */
DECL|method|IndexWriterConfig
specifier|public
name|IndexWriterConfig
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|matchVersion
operator|=
name|matchVersion
expr_stmt|;
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|delPolicy
operator|=
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
expr_stmt|;
name|commit
operator|=
literal|null
expr_stmt|;
name|openMode
operator|=
name|OpenMode
operator|.
name|CREATE_OR_APPEND
expr_stmt|;
name|maxFieldLength
operator|=
name|UNLIMITED_FIELD_LENGTH
expr_stmt|;
name|similarity
operator|=
name|Similarity
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|termIndexInterval
operator|=
name|DEFAULT_TERM_INDEX_INTERVAL
expr_stmt|;
name|mergeScheduler
operator|=
operator|new
name|ConcurrentMergeScheduler
argument_list|()
expr_stmt|;
name|writeLockTimeout
operator|=
name|WRITE_LOCK_TIMEOUT
expr_stmt|;
name|maxBufferedDeleteTerms
operator|=
name|DEFAULT_MAX_BUFFERED_DELETE_TERMS
expr_stmt|;
name|ramBufferSizeMB
operator|=
name|DEFAULT_RAM_BUFFER_SIZE_MB
expr_stmt|;
name|maxBufferedDocs
operator|=
name|DEFAULT_MAX_BUFFERED_DOCS
expr_stmt|;
name|indexingChain
operator|=
name|DocumentsWriter
operator|.
name|defaultIndexingChain
expr_stmt|;
name|mergedSegmentWarmer
operator|=
literal|null
expr_stmt|;
name|codecProvider
operator|=
name|DEFAULT_CODEC_PROVIDER
expr_stmt|;
name|mergePolicy
operator|=
operator|new
name|LogByteSizeMergePolicy
argument_list|()
expr_stmt|;
name|maxThreadStates
operator|=
name|DEFAULT_MAX_THREAD_STATES
expr_stmt|;
name|readerPooling
operator|=
name|DEFAULT_READER_POOLING
expr_stmt|;
name|readerTermsIndexDivisor
operator|=
name|DEFAULT_READER_TERMS_INDEX_DIVISOR
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
comment|// Shallow clone is the only thing that's possible, since parameters like
comment|// analyzer, index commit etc. do not implemnt Cloneable.
try|try
block|{
return|return
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
comment|// should not happen
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the default analyzer to use for indexing documents. */
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
comment|/** Specifies {@link OpenMode} of that index. */
DECL|method|setOpenMode
specifier|public
name|IndexWriterConfig
name|setOpenMode
parameter_list|(
name|OpenMode
name|openMode
parameter_list|)
block|{
name|this
operator|.
name|openMode
operator|=
name|openMode
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the {@link OpenMode} set by {@link #setOpenMode(OpenMode)}. */
DECL|method|getOpenMode
specifier|public
name|OpenMode
name|getOpenMode
parameter_list|()
block|{
return|return
name|openMode
return|;
block|}
comment|/**    * Expert: allows an optional {@link IndexDeletionPolicy} implementation to be    * specified. You can use this to control when prior commits are deleted from    * the index. The default policy is {@link KeepOnlyLastCommitDeletionPolicy}    * which removes all prior commits as soon as a new commit is done (this    * matches behavior before 2.2). Creating your own policy can allow you to    * explicitly keep previous "point in time" commits alive in the index for    * some time, to allow readers to refresh to the new commit without having the    * old commit deleted out from under them. This is necessary on filesystems    * like NFS that do not support "delete on last close" semantics, which    * Lucene's "point in time" search normally relies on.    *<p>    *<b>NOTE:</b> the deletion policy cannot be null. If<code>null</code> is    * passed, the deletion policy will be set to the default.    */
DECL|method|setIndexDeletionPolicy
specifier|public
name|IndexWriterConfig
name|setIndexDeletionPolicy
parameter_list|(
name|IndexDeletionPolicy
name|delPolicy
parameter_list|)
block|{
name|this
operator|.
name|delPolicy
operator|=
name|delPolicy
operator|==
literal|null
condition|?
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
else|:
name|delPolicy
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the {@link IndexDeletionPolicy} specified in    * {@link #setIndexDeletionPolicy(IndexDeletionPolicy)} or the default    * {@link KeepOnlyLastCommitDeletionPolicy}/    */
DECL|method|getIndexDeletionPolicy
specifier|public
name|IndexDeletionPolicy
name|getIndexDeletionPolicy
parameter_list|()
block|{
return|return
name|delPolicy
return|;
block|}
comment|/**    * The maximum number of terms that will be indexed for a single field in a    * document. This limits the amount of memory required for indexing, so that    * collections with very large files will not crash the indexing process by    * running out of memory. This setting refers to the number of running terms,    * not to the number of different terms.    *<p>    *<b>NOTE:</b> this silently truncates large documents, excluding from the    * index all terms that occur further in the document. If you know your source    * documents are large, be sure to set this value high enough to accomodate    * the expected size. If you set it to {@link #UNLIMITED_FIELD_LENGTH}, then    * the only limit is your memory, but you should anticipate an    * OutOfMemoryError.    *<p>    * By default it is set to {@link #UNLIMITED_FIELD_LENGTH}.    */
DECL|method|setMaxFieldLength
specifier|public
name|IndexWriterConfig
name|setMaxFieldLength
parameter_list|(
name|int
name|maxFieldLength
parameter_list|)
block|{
name|this
operator|.
name|maxFieldLength
operator|=
name|maxFieldLength
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the maximum number of terms that will be indexed for a single field    * in a document.    *     * @see #setMaxFieldLength(int)    */
DECL|method|getMaxFieldLength
specifier|public
name|int
name|getMaxFieldLength
parameter_list|()
block|{
return|return
name|maxFieldLength
return|;
block|}
comment|/**    * Expert: allows to open a certain commit point. The default is null which    * opens the latest commit point.    */
DECL|method|setIndexCommit
specifier|public
name|IndexWriterConfig
name|setIndexCommit
parameter_list|(
name|IndexCommit
name|commit
parameter_list|)
block|{
name|this
operator|.
name|commit
operator|=
name|commit
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the {@link IndexCommit} as specified in    * {@link #setIndexCommit(IndexCommit)} or the default,<code>null</code>    * which specifies to open the latest index commit point.    */
DECL|method|getIndexCommit
specifier|public
name|IndexCommit
name|getIndexCommit
parameter_list|()
block|{
return|return
name|commit
return|;
block|}
comment|/**    * Expert: set the {@link Similarity} implementation used by this IndexWriter.    *<p>    *<b>NOTE:</b> the similarity cannot be null. If<code>null</code> is passed,    * the similarity will be set to the default.    *     * @see Similarity#setDefault(Similarity)    */
DECL|method|setSimilarity
specifier|public
name|IndexWriterConfig
name|setSimilarity
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
block|{
name|this
operator|.
name|similarity
operator|=
name|similarity
operator|==
literal|null
condition|?
name|Similarity
operator|.
name|getDefault
argument_list|()
else|:
name|similarity
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Expert: returns the {@link Similarity} implementation used by this    * IndexWriter. This defaults to the current value of    * {@link Similarity#getDefault()}.    */
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
return|return
name|similarity
return|;
block|}
comment|/**    * Expert: set the interval between indexed terms. Large values cause less    * memory to be used by IndexReader, but slow random-access to terms. Small    * values cause more memory to be used by an IndexReader, and speed    * random-access to terms.    *<p>    * This parameter determines the amount of computation required per query    * term, regardless of the number of documents that contain that term. In    * particular, it is the maximum number of other terms that must be scanned    * before a term is located and its frequency and position information may be    * processed. In a large index with user-entered query terms, query processing    * time is likely to be dominated not by term lookup but rather by the    * processing of frequency and positional data. In a small index or when many    * uncommon query terms are generated (e.g., by wildcard queries) term lookup    * may become a dominant cost.    *<p>    * In particular,<code>numUniqueTerms/interval</code> terms are read into    * memory by an IndexReader, and, on average,<code>interval/2</code> terms    * must be scanned for each random term access.    *     * @see #DEFAULT_TERM_INDEX_INTERVAL    */
DECL|method|setTermIndexInterval
specifier|public
name|IndexWriterConfig
name|setTermIndexInterval
parameter_list|(
name|int
name|interval
parameter_list|)
block|{
name|this
operator|.
name|termIndexInterval
operator|=
name|interval
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the interval between indexed terms.    *     * @see #setTermIndexInterval(int)    */
DECL|method|getTermIndexInterval
specifier|public
name|int
name|getTermIndexInterval
parameter_list|()
block|{
return|return
name|termIndexInterval
return|;
block|}
comment|/**    * Expert: sets the merge scheduler used by this writer. The default is    * {@link ConcurrentMergeScheduler}.    *<p>    *<b>NOTE:</b> the merge scheduler cannot be null. If<code>null</code> is    * passed, the merge scheduler will be set to the default.    */
DECL|method|setMergeScheduler
specifier|public
name|IndexWriterConfig
name|setMergeScheduler
parameter_list|(
name|MergeScheduler
name|mergeScheduler
parameter_list|)
block|{
name|this
operator|.
name|mergeScheduler
operator|=
name|mergeScheduler
operator|==
literal|null
condition|?
operator|new
name|ConcurrentMergeScheduler
argument_list|()
else|:
name|mergeScheduler
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the {@link MergeScheduler} that was set by    * {@link #setMergeScheduler(MergeScheduler)}    */
DECL|method|getMergeScheduler
specifier|public
name|MergeScheduler
name|getMergeScheduler
parameter_list|()
block|{
return|return
name|mergeScheduler
return|;
block|}
comment|/**    * Sets the maximum time to wait for a write lock (in milliseconds) for this    * instance. You can change the default value for all instances by calling    * {@link #setDefaultWriteLockTimeout(long)}.    */
DECL|method|setWriteLockTimeout
specifier|public
name|IndexWriterConfig
name|setWriteLockTimeout
parameter_list|(
name|long
name|writeLockTimeout
parameter_list|)
block|{
name|this
operator|.
name|writeLockTimeout
operator|=
name|writeLockTimeout
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns allowed timeout when acquiring the write lock.    *     * @see #setWriteLockTimeout(long)    */
DECL|method|getWriteLockTimeout
specifier|public
name|long
name|getWriteLockTimeout
parameter_list|()
block|{
return|return
name|writeLockTimeout
return|;
block|}
comment|/**    * Determines the minimal number of delete terms required before the buffered    * in-memory delete terms are applied and flushed. If there are documents    * buffered in memory at the time, they are merged and a new segment is    * created.     *<p>Disabled by default (writer flushes by RAM usage).    *     * @throws IllegalArgumentException if maxBufferedDeleteTerms    * is enabled but smaller than 1    * @see #setRAMBufferSizeMB    */
DECL|method|setMaxBufferedDeleteTerms
specifier|public
name|IndexWriterConfig
name|setMaxBufferedDeleteTerms
parameter_list|(
name|int
name|maxBufferedDeleteTerms
parameter_list|)
block|{
if|if
condition|(
name|maxBufferedDeleteTerms
operator|!=
name|DISABLE_AUTO_FLUSH
operator|&&
name|maxBufferedDeleteTerms
operator|<
literal|1
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxBufferedDeleteTerms must at least be 1 when enabled"
argument_list|)
throw|;
name|this
operator|.
name|maxBufferedDeleteTerms
operator|=
name|maxBufferedDeleteTerms
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the number of buffered deleted terms that will trigger a flush if    * enabled.    *     * @see #setMaxBufferedDeleteTerms(int)    */
DECL|method|getMaxBufferedDeleteTerms
specifier|public
name|int
name|getMaxBufferedDeleteTerms
parameter_list|()
block|{
return|return
name|maxBufferedDeleteTerms
return|;
block|}
comment|/**    * Determines the amount of RAM that may be used for buffering added documents    * and deletions before they are flushed to the Directory. Generally for    * faster indexing performance it's best to flush by RAM usage instead of    * document count and use as large a RAM buffer as you can.    *     *<p>    * When this is set, the writer will flush whenever buffered documents and    * deletions use this much RAM. Pass in {@link #DISABLE_AUTO_FLUSH} to prevent    * triggering a flush due to RAM usage. Note that if flushing by document    * count is also enabled, then the flush will be triggered by whichever comes    * first.    *     *<p>    *<b>NOTE</b>: the account of RAM usage for pending deletions is only    * approximate. Specifically, if you delete by Query, Lucene currently has no    * way to measure the RAM usage of individual Queries so the accounting will    * under-estimate and you should compensate by either calling commit()    * periodically yourself, or by using {@link #setMaxBufferedDeleteTerms(int)}    * to flush by count instead of RAM usage (each buffered delete Query counts     * as one).    *     *<p>    *<b>NOTE</b>: because IndexWriter uses<code>int</code>s when managing its    * internal storage, the absolute maximum value for this setting is somewhat    * less than 2048 MB. The precise limit depends on various factors, such as    * how large your documents are, how many fields have norms, etc., so it's    * best to set this value comfortably under 2048.    *     *<p>    * The default value is {@link #DEFAULT_RAM_BUFFER_SIZE_MB}.    *     * @throws IllegalArgumentException    *           if ramBufferSize is enabled but non-positive, or it disables    *           ramBufferSize when maxBufferedDocs is already disabled    */
DECL|method|setRAMBufferSizeMB
specifier|public
name|IndexWriterConfig
name|setRAMBufferSizeMB
parameter_list|(
name|double
name|ramBufferSizeMB
parameter_list|)
block|{
if|if
condition|(
name|ramBufferSizeMB
operator|>
literal|2048.0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ramBufferSize "
operator|+
name|ramBufferSizeMB
operator|+
literal|" is too large; should be comfortably less than 2048"
argument_list|)
throw|;
block|}
if|if
condition|(
name|ramBufferSizeMB
operator|!=
name|DISABLE_AUTO_FLUSH
operator|&&
name|ramBufferSizeMB
operator|<=
literal|0.0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ramBufferSize should be> 0.0 MB when enabled"
argument_list|)
throw|;
if|if
condition|(
name|ramBufferSizeMB
operator|==
name|DISABLE_AUTO_FLUSH
operator|&&
name|maxBufferedDocs
operator|==
name|DISABLE_AUTO_FLUSH
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"at least one of ramBufferSize and maxBufferedDocs must be enabled"
argument_list|)
throw|;
name|this
operator|.
name|ramBufferSizeMB
operator|=
name|ramBufferSizeMB
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the value set by {@link #setRAMBufferSizeMB(double)} if enabled. */
DECL|method|getRAMBufferSizeMB
specifier|public
name|double
name|getRAMBufferSizeMB
parameter_list|()
block|{
return|return
name|ramBufferSizeMB
return|;
block|}
comment|/**    * Determines the minimal number of documents required before the buffered    * in-memory documents are flushed as a new Segment. Large values generally    * give faster indexing.    *     *<p>    * When this is set, the writer will flush every maxBufferedDocs added    * documents. Pass in {@link #DISABLE_AUTO_FLUSH} to prevent triggering a    * flush due to number of buffered documents. Note that if flushing by RAM    * usage is also enabled, then the flush will be triggered by whichever comes    * first.    *     *<p>    * Disabled by default (writer flushes by RAM usage).    *     * @see #setRAMBufferSizeMB(double)    *     * @throws IllegalArgumentException    *           if maxBufferedDocs is enabled but smaller than 2, or it disables    *           maxBufferedDocs when ramBufferSize is already disabled    */
DECL|method|setMaxBufferedDocs
specifier|public
name|IndexWriterConfig
name|setMaxBufferedDocs
parameter_list|(
name|int
name|maxBufferedDocs
parameter_list|)
block|{
if|if
condition|(
name|maxBufferedDocs
operator|!=
name|DISABLE_AUTO_FLUSH
operator|&&
name|maxBufferedDocs
operator|<
literal|2
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxBufferedDocs must at least be 2 when enabled"
argument_list|)
throw|;
if|if
condition|(
name|maxBufferedDocs
operator|==
name|DISABLE_AUTO_FLUSH
operator|&&
name|ramBufferSizeMB
operator|==
name|DISABLE_AUTO_FLUSH
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"at least one of ramBufferSize and maxBufferedDocs must be enabled"
argument_list|)
throw|;
name|this
operator|.
name|maxBufferedDocs
operator|=
name|maxBufferedDocs
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the number of buffered added documents that will trigger a flush if    * enabled.    *     * @see #setMaxBufferedDocs(int)    */
DECL|method|getMaxBufferedDocs
specifier|public
name|int
name|getMaxBufferedDocs
parameter_list|()
block|{
return|return
name|maxBufferedDocs
return|;
block|}
comment|/** Set the merged segment warmer. See {@link IndexReaderWarmer}. */
DECL|method|setMergedSegmentWarmer
specifier|public
name|IndexWriterConfig
name|setMergedSegmentWarmer
parameter_list|(
name|IndexReaderWarmer
name|mergeSegmentWarmer
parameter_list|)
block|{
name|this
operator|.
name|mergedSegmentWarmer
operator|=
name|mergeSegmentWarmer
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the current merged segment warmer. See {@link IndexReaderWarmer}. */
DECL|method|getMergedSegmentWarmer
specifier|public
name|IndexReaderWarmer
name|getMergedSegmentWarmer
parameter_list|()
block|{
return|return
name|mergedSegmentWarmer
return|;
block|}
comment|/**    * Expert: {@link MergePolicy} is invoked whenever there are changes to the    * segments in the index. Its role is to select which merges to do, if any,    * and return a {@link MergePolicy.MergeSpecification} describing the merges.    * It also selects merges to do for optimize(). (The default is    * {@link LogByteSizeMergePolicy}.    */
DECL|method|setMergePolicy
specifier|public
name|IndexWriterConfig
name|setMergePolicy
parameter_list|(
name|MergePolicy
name|mergePolicy
parameter_list|)
block|{
name|this
operator|.
name|mergePolicy
operator|=
name|mergePolicy
operator|==
literal|null
condition|?
operator|new
name|LogByteSizeMergePolicy
argument_list|()
else|:
name|mergePolicy
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Set the CodecProvider. See {@link CodecProvider}. */
DECL|method|setCodecProvider
specifier|public
name|IndexWriterConfig
name|setCodecProvider
parameter_list|(
name|CodecProvider
name|codecProvider
parameter_list|)
block|{
name|this
operator|.
name|codecProvider
operator|=
name|codecProvider
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the current merged segment warmer. See {@link IndexReaderWarmer}. */
DECL|method|getCodecProvider
specifier|public
name|CodecProvider
name|getCodecProvider
parameter_list|()
block|{
return|return
name|codecProvider
return|;
block|}
comment|/**    * Returns the current MergePolicy in use by this writer.    *     * @see #setMergePolicy(MergePolicy)    */
DECL|method|getMergePolicy
specifier|public
name|MergePolicy
name|getMergePolicy
parameter_list|()
block|{
return|return
name|mergePolicy
return|;
block|}
comment|/**    * Sets the max number of simultaneous threads that may be indexing documents    * at once in IndexWriter. Values&lt; 1 are invalid and if passed    *<code>maxThreadStates</code> will be set to    * {@link #DEFAULT_MAX_THREAD_STATES}.    */
DECL|method|setMaxThreadStates
specifier|public
name|IndexWriterConfig
name|setMaxThreadStates
parameter_list|(
name|int
name|maxThreadStates
parameter_list|)
block|{
name|this
operator|.
name|maxThreadStates
operator|=
name|maxThreadStates
operator|<
literal|1
condition|?
name|DEFAULT_MAX_THREAD_STATES
else|:
name|maxThreadStates
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the max number of simultaneous threads that    *  may be indexing documents at once in IndexWriter. */
DECL|method|getMaxThreadStates
specifier|public
name|int
name|getMaxThreadStates
parameter_list|()
block|{
return|return
name|maxThreadStates
return|;
block|}
comment|/** By default, IndexWriter does not pool the    *  SegmentReaders it must open for deletions and    *  merging, unless a near-real-time reader has been    *  obtained by calling {@link IndexWriter#getReader}.    *  This method lets you enable pooling without getting a    *  near-real-time reader.  NOTE: if you set this to    *  false, IndexWriter will still pool readers once    *  {@link IndexWriter#getReader} is called. */
DECL|method|setReaderPooling
specifier|public
name|IndexWriterConfig
name|setReaderPooling
parameter_list|(
name|boolean
name|readerPooling
parameter_list|)
block|{
name|this
operator|.
name|readerPooling
operator|=
name|readerPooling
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns true if IndexWriter should pool readers even    *  if {@link IndexWriter#getReader} has not been called. */
DECL|method|getReaderPooling
specifier|public
name|boolean
name|getReaderPooling
parameter_list|()
block|{
return|return
name|readerPooling
return|;
block|}
comment|/** Expert: sets the {@link DocConsumer} chain to be used to process documents. */
DECL|method|setIndexingChain
name|IndexWriterConfig
name|setIndexingChain
parameter_list|(
name|IndexingChain
name|indexingChain
parameter_list|)
block|{
name|this
operator|.
name|indexingChain
operator|=
name|indexingChain
operator|==
literal|null
condition|?
name|DocumentsWriter
operator|.
name|defaultIndexingChain
else|:
name|indexingChain
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the indexing chain set on {@link #setIndexingChain(IndexingChain)}. */
DECL|method|getIndexingChain
name|IndexingChain
name|getIndexingChain
parameter_list|()
block|{
return|return
name|indexingChain
return|;
block|}
comment|/** Sets the termsIndexDivisor passed to any readers that    *  IndexWriter opens, for example when applying deletes    *  or creating a near-real-time reader in {@link    *  IndexWriter#getReader}. */
DECL|method|setReaderTermsIndexDivisor
specifier|public
name|IndexWriterConfig
name|setReaderTermsIndexDivisor
parameter_list|(
name|int
name|divisor
parameter_list|)
block|{
if|if
condition|(
name|divisor
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"divisor must be>= 1 (got "
operator|+
name|divisor
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|readerTermsIndexDivisor
operator|=
name|divisor
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** @see #setReaderTermsIndexDivisor() */
DECL|method|getReaderTermsIndexDivisor
specifier|public
name|int
name|getReaderTermsIndexDivisor
parameter_list|()
block|{
return|return
name|readerTermsIndexDivisor
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"matchVersion="
argument_list|)
operator|.
name|append
argument_list|(
name|matchVersion
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"analyzer="
argument_list|)
operator|.
name|append
argument_list|(
name|analyzer
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"delPolicy="
argument_list|)
operator|.
name|append
argument_list|(
name|delPolicy
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"commit="
argument_list|)
operator|.
name|append
argument_list|(
name|commit
operator|==
literal|null
condition|?
literal|"null"
else|:
name|commit
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"openMode="
argument_list|)
operator|.
name|append
argument_list|(
name|openMode
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"maxFieldLength="
argument_list|)
operator|.
name|append
argument_list|(
name|maxFieldLength
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"similarity="
argument_list|)
operator|.
name|append
argument_list|(
name|similarity
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"termIndexInterval="
argument_list|)
operator|.
name|append
argument_list|(
name|termIndexInterval
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"mergeScheduler="
argument_list|)
operator|.
name|append
argument_list|(
name|mergeScheduler
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"default WRITE_LOCK_TIMEOUT="
argument_list|)
operator|.
name|append
argument_list|(
name|WRITE_LOCK_TIMEOUT
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"writeLockTimeout="
argument_list|)
operator|.
name|append
argument_list|(
name|writeLockTimeout
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"maxBufferedDeleteTerms="
argument_list|)
operator|.
name|append
argument_list|(
name|maxBufferedDeleteTerms
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ramBufferSizeMB="
argument_list|)
operator|.
name|append
argument_list|(
name|ramBufferSizeMB
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"maxBufferedDocs="
argument_list|)
operator|.
name|append
argument_list|(
name|maxBufferedDocs
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"mergedSegmentWarmer="
argument_list|)
operator|.
name|append
argument_list|(
name|mergedSegmentWarmer
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"codecProvider="
argument_list|)
operator|.
name|append
argument_list|(
name|codecProvider
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"mergePolicy="
argument_list|)
operator|.
name|append
argument_list|(
name|mergePolicy
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"maxThreadStates="
argument_list|)
operator|.
name|append
argument_list|(
name|maxThreadStates
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"readerPooling="
argument_list|)
operator|.
name|append
argument_list|(
name|readerPooling
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"readerTermsIndexDivisor="
argument_list|)
operator|.
name|append
argument_list|(
name|readerTermsIndexDivisor
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
