begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
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
name|Semaphore
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
name|index
operator|.
name|IndexReader
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
name|NRTManager
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
begin_comment
comment|// javadocs
end_comment
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
comment|/**  * Utility class to safely share {@link IndexSearcher} instances across multiple  * threads, while periodically reopening. This class ensures each searcher is  * closed only once all threads have finished using it.  *   *<p>  * Use {@link #acquire} to obtain the current searcher, and {@link #release} to  * release it, like this:  *   *<pre>  * IndexSearcher s = manager.acquire();  * try {  *   // Do searching, doc retrieval, etc. with s  * } finally {  *   manager.release(s);  * }  * // Do not use s after this!  * s = null;  *</pre>  *   *<p>  * In addition you should periodically call {@link #maybeReopen}. While it's  * possible to call this just before running each query, this is discouraged  * since it penalizes the unlucky queries that do the reopen. It's better to use  * a separate background thread, that periodically calls maybeReopen. Finally,  * be sure to call {@link #close} once you are done.  *   *<p>  *<b>NOTE</b>: if you have an {@link IndexWriter}, it's better to use  * {@link NRTManager} since that class pulls near-real-time readers from the  * IndexWriter.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|SearcherManager
specifier|public
specifier|abstract
class|class
name|SearcherManager
block|{
DECL|field|currentSearcher
specifier|protected
specifier|volatile
name|IndexSearcher
name|currentSearcher
decl_stmt|;
DECL|field|es
specifier|protected
specifier|final
name|ExecutorService
name|es
decl_stmt|;
DECL|field|warmer
specifier|protected
specifier|final
name|SearcherWarmer
name|warmer
decl_stmt|;
DECL|field|reopenLock
specifier|protected
specifier|final
name|Semaphore
name|reopenLock
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|method|SearcherManager
specifier|protected
name|SearcherManager
parameter_list|(
name|IndexReader
name|openedReader
parameter_list|,
name|SearcherWarmer
name|warmer
parameter_list|,
name|ExecutorService
name|es
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|es
operator|=
name|es
expr_stmt|;
name|this
operator|.
name|warmer
operator|=
name|warmer
expr_stmt|;
name|currentSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|openedReader
argument_list|,
name|es
argument_list|)
expr_stmt|;
block|}
comment|/**    * You must call this, periodically, to perform a reopen. This calls    * {@link #openIfChanged(IndexReader)} with the underlying reader, and if that returns a    * new reader, it's warmed (if you provided a {@link SearcherWarmer} and then    * swapped into production.    *     *<p>    *<b>Threads</b>: it's fine for more than one thread to call this at once.    * Only the first thread will attempt the reopen; subsequent threads will see    * that another thread is already handling reopen and will return immediately.    * Note that this means if another thread is already reopening then subsequent    * threads will return right away without waiting for the reader reopen to    * complete.    *</p>    *     *<p>    * This method returns true if a new reader was in fact opened.    *</p>    */
DECL|method|maybeReopen
specifier|public
name|boolean
name|maybeReopen
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// Ensure only 1 thread does reopen at once; other
comment|// threads just return immediately:
if|if
condition|(
name|reopenLock
operator|.
name|tryAcquire
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|IndexReader
name|newReader
init|=
name|openIfChanged
argument_list|(
name|currentSearcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|newReader
operator|!=
literal|null
condition|)
block|{
specifier|final
name|IndexSearcher
name|newSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|newReader
argument_list|,
name|es
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|warmer
operator|!=
literal|null
condition|)
block|{
name|warmer
operator|.
name|warm
argument_list|(
name|newSearcher
argument_list|)
expr_stmt|;
block|}
name|swapSearcher
argument_list|(
name|newSearcher
argument_list|)
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
name|release
argument_list|(
name|newSearcher
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
finally|finally
block|{
name|reopenLock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Returns<code>true</code> if no changes have occured since this searcher    * ie. reader was opened, otherwise<code>false</code>.    * @see IndexReader#isCurrent()     */
DECL|method|isSearcherCurrent
specifier|public
name|boolean
name|isSearcherCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
specifier|final
name|IndexSearcher
name|searcher
init|=
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|isCurrent
argument_list|()
return|;
block|}
finally|finally
block|{
name|release
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Release the searcher previously obtained with {@link #acquire}.    *     *<p>    *<b>NOTE</b>: it's safe to call this after {@link #close}.    */
DECL|method|release
specifier|public
name|void
name|release
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|searcher
operator|!=
literal|null
assert|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
comment|/**    * Close this SearcherManager to future searching. Any searches still in    * process in other threads won't be affected, and they should still call    * {@link #release} after they are done.    */
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|currentSearcher
operator|!=
literal|null
condition|)
block|{
comment|// make sure we can call this more than once
comment|// closeable javadoc says:
comment|// if this is already closed then invoking this method has no effect.
name|swapSearcher
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Obtain the current IndexSearcher. You must match every call to acquire with    * one call to {@link #release}; it's best to do so in a finally clause.    */
DECL|method|acquire
specifier|public
name|IndexSearcher
name|acquire
parameter_list|()
block|{
name|IndexSearcher
name|searcher
decl_stmt|;
do|do
block|{
if|if
condition|(
operator|(
name|searcher
operator|=
name|currentSearcher
operator|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this SearcherManager is closed"
argument_list|)
throw|;
block|}
block|}
do|while
condition|(
operator|!
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|tryIncRef
argument_list|()
condition|)
do|;
return|return
name|searcher
return|;
block|}
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
block|{
if|if
condition|(
name|currentSearcher
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this SearcherManager is closed"
argument_list|)
throw|;
block|}
block|}
DECL|method|swapSearcher
specifier|protected
specifier|synchronized
name|void
name|swapSearcher
parameter_list|(
name|IndexSearcher
name|newSearcher
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|IndexSearcher
name|oldSearcher
init|=
name|currentSearcher
decl_stmt|;
name|currentSearcher
operator|=
name|newSearcher
expr_stmt|;
name|release
argument_list|(
name|oldSearcher
argument_list|)
expr_stmt|;
block|}
DECL|method|openIfChanged
specifier|protected
specifier|abstract
name|IndexReader
name|openIfChanged
parameter_list|(
name|IndexReader
name|oldReader
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates and returns a new SearcherManager from the given {@link IndexWriter}.     * @param writer the IndexWriter to open the IndexReader from.    * @param applyAllDeletes If<code>true</code>, all buffered deletes will    *        be applied (made visible) in the {@link IndexSearcher} / {@link IndexReader}.    *        If<code>false</code>, the deletes are not applied but remain buffered     *        (in IndexWriter) so that they will be applied in the future.    *        Applying deletes can be costly, so if your app can tolerate deleted documents    *        being returned you might gain some performance by passing<code>false</code>.    * @param warmer An optional {@link SearcherWarmer}. Pass    *<code>null</code> if you don't require the searcher to warmed    *        before going live.  If this is<code>non-null</code> then a    *        merged segment warmer is installed on the    *        provided IndexWriter's config.    * @param es An optional {@link ExecutorService} so different segments can    *        be searched concurrently (see {@link    *        IndexSearcher#IndexSearcher(IndexReader,ExecutorService)}.  Pass<code>null</code>    *        to search segments sequentially.    *            * @see IndexReader#openIfChanged(IndexReader, IndexWriter, boolean)    * @throws IOException    */
DECL|method|open
specifier|public
specifier|static
name|SearcherManager
name|open
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|,
name|SearcherWarmer
name|warmer
parameter_list|,
name|ExecutorService
name|es
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexReader
name|open
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|SearcherManager
name|manager
init|=
operator|new
name|NRTSearcherManager
argument_list|(
name|writer
argument_list|,
name|applyAllDeletes
argument_list|,
name|open
argument_list|,
name|warmer
argument_list|,
name|es
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|manager
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|open
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Creates and returns a new SearcherManager from the given {@link Directory}.     * @param dir the directory to open the IndexReader on.    * @param warmer An optional {@link SearcherWarmer}.  Pass    *<code>null</code> if you don't require the searcher to warmed    *        before going live.  If this is<code>non-null</code> then a    *        merged segment warmer is installed on the    *        provided IndexWriter's config.    * @param es And optional {@link ExecutorService} so different segments can    *        be searched concurrently (see {@link    *        IndexSearcher#IndexSearcher(IndexReader,ExecutorService)}.  Pass<code>null</code>    *        to search segments sequentially.    *            * @throws IOException    */
DECL|method|open
specifier|public
specifier|static
name|SearcherManager
name|open
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SearcherWarmer
name|warmer
parameter_list|,
name|ExecutorService
name|es
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexReader
name|open
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|SearcherManager
name|manager
init|=
operator|new
name|DirectorySearchManager
argument_list|(
name|open
argument_list|,
name|warmer
argument_list|,
name|es
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|manager
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|open
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|NRTSearcherManager
specifier|static
specifier|final
class|class
name|NRTSearcherManager
extends|extends
name|SearcherManager
block|{
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|applyDeletes
specifier|private
specifier|final
name|boolean
name|applyDeletes
decl_stmt|;
DECL|method|NRTSearcherManager
name|NRTSearcherManager
parameter_list|(
specifier|final
name|IndexWriter
name|writer
parameter_list|,
specifier|final
name|boolean
name|applyDeletes
parameter_list|,
specifier|final
name|IndexReader
name|openedReader
parameter_list|,
specifier|final
name|SearcherWarmer
name|warmer
parameter_list|,
specifier|final
name|ExecutorService
name|es
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|openedReader
argument_list|,
name|warmer
argument_list|,
name|es
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|applyDeletes
operator|=
name|applyDeletes
expr_stmt|;
if|if
condition|(
name|warmer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|setMergedSegmentWarmer
argument_list|(
operator|new
name|IndexWriter
operator|.
name|IndexReaderWarmer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|warm
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|warmer
operator|.
name|warm
argument_list|(
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|,
name|es
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|openIfChanged
specifier|protected
name|IndexReader
name|openIfChanged
parameter_list|(
name|IndexReader
name|oldReader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|IndexReader
operator|.
name|openIfChanged
argument_list|(
name|oldReader
argument_list|,
name|writer
argument_list|,
name|applyDeletes
argument_list|)
return|;
block|}
block|}
DECL|class|DirectorySearchManager
specifier|static
specifier|final
class|class
name|DirectorySearchManager
extends|extends
name|SearcherManager
block|{
DECL|method|DirectorySearchManager
name|DirectorySearchManager
parameter_list|(
name|IndexReader
name|openedReader
parameter_list|,
name|SearcherWarmer
name|warmer
parameter_list|,
name|ExecutorService
name|es
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|openedReader
argument_list|,
name|warmer
argument_list|,
name|es
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openIfChanged
specifier|protected
name|IndexReader
name|openIfChanged
parameter_list|(
name|IndexReader
name|oldReader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|IndexReader
operator|.
name|openIfChanged
argument_list|(
name|oldReader
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
