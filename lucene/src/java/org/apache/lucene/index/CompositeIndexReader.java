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
name|Closeable
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
name|LinkedHashSet
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
name|index
operator|.
name|AtomicIndexReader
operator|.
name|AtomicReaderContext
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
name|DocumentStoredFieldVisitor
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
name|SearcherManager
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
name|Bits
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
name|BytesRef
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
name|ReaderUtil
import|;
end_import
begin_comment
comment|// for javadocs
end_comment
begin_comment
comment|/** IndexReader is an abstract class, providing an interface for accessing an  index.  Search of an index is done entirely through this abstract interface,  so that any subclass which implements it is searchable.<p> Concrete subclasses of IndexReader are usually constructed with a call to  one of the static<code>open()</code> methods, e.g. {@link  #open(Directory)}.<p> For efficiency, in this API documents are often referred to via<i>document numbers</i>, non-negative integers which each name a unique  document in the index.  These document numbers are ephemeral--they may change  as documents are added to and deleted from an index.  Clients should thus not  rely on a given document having the same number between sessions.<p><b>NOTE</b>: for backwards API compatibility, several methods are not listed   as abstract, but have no useful implementations in this base class and   instead always throw UnsupportedOperationException.  Subclasses are   strongly encouraged to override these methods, but in many cases may not   need to.</p><p><a name="thread-safety"></a><p><b>NOTE</b>: {@link  IndexReader} instances are completely thread  safe, meaning multiple threads can call any of its methods,  concurrently.  If your application requires external  synchronization, you should<b>not</b> synchronize on the<code>IndexReader</code> instance; use your own  (non-Lucene) objects instead. */
end_comment
begin_class
DECL|class|CompositeIndexReader
specifier|public
specifier|abstract
class|class
name|CompositeIndexReader
extends|extends
name|IndexReader
block|{
DECL|method|CompositeIndexReader
specifier|protected
name|CompositeIndexReader
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
specifier|final
name|IndexReader
index|[]
name|subReaders
init|=
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|subReaders
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|subReaders
operator|.
name|length
operator|>
literal|0
operator|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|subReaders
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|subReaders
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTopReaderContext
specifier|public
specifier|abstract
name|CompositeReaderContext
name|getTopReaderContext
parameter_list|()
function_decl|;
comment|/**    * If the index has changed since it was opened, open and return a new reader;    * else, return {@code null}.    *     * @see #openIfChanged(IndexReader)    */
DECL|method|doOpenIfChanged
specifier|protected
name|CompositeIndexReader
name|doOpenIfChanged
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This reader does not support reopen()."
argument_list|)
throw|;
block|}
comment|/**    * If the index has changed since it was opened, open and return a new reader;    * else, return {@code null}.    *     * @see #openIfChanged(IndexReader, IndexCommit)    */
DECL|method|doOpenIfChanged
specifier|protected
name|CompositeIndexReader
name|doOpenIfChanged
parameter_list|(
specifier|final
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This reader does not support reopen(IndexCommit)."
argument_list|)
throw|;
block|}
comment|/**    * If the index has changed since it was opened, open and return a new reader;    * else, return {@code null}.    *     * @see #openIfChanged(IndexReader, IndexWriter, boolean)    */
DECL|method|doOpenIfChanged
specifier|protected
name|CompositeIndexReader
name|doOpenIfChanged
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|writer
operator|.
name|getReader
argument_list|(
name|applyAllDeletes
argument_list|)
return|;
block|}
comment|/**    * Version number when this IndexReader was opened. Not    * implemented in the IndexReader base class.    *    *<p>If this reader is based on a Directory (ie, was    * created by calling {@link #open}, or {@link #openIfChanged} on    * a reader based on a Directory), then this method    * returns the version recorded in the commit that the    * reader opened.  This version is advanced every time    * {@link IndexWriter#commit} is called.</p>    *    *<p>If instead this reader is a near real-time reader    * (ie, obtained by a call to {@link    * IndexWriter#getReader}, or by calling {@link #openIfChanged}    * on a near real-time reader), then this method returns    * the version of the last commit done by the writer.    * Note that even as further changes are made with the    * writer, the version will not changed until a commit is    * completed.  Thus, you should not rely on this method to    * determine when a near real-time reader should be    * opened.  Use {@link #isCurrent} instead.</p>    */
DECL|method|getVersion
specifier|public
specifier|abstract
name|long
name|getVersion
parameter_list|()
function_decl|;
comment|/**    * Check whether any new changes have occurred to the    * index since this reader was opened.    *    *<p>If this reader is based on a Directory (ie, was    * created by calling {@link #open}, or {@link #openIfChanged} on    * a reader based on a Directory), then this method checks    * if any further commits (see {@link IndexWriter#commit}    * have occurred in that directory).</p>    *    *<p>If instead this reader is a near real-time reader    * (ie, obtained by a call to {@link    * IndexWriter#getReader}, or by calling {@link #openIfChanged}    * on a near real-time reader), then this method checks if    * either a new commit has occurred, or any new    * uncommitted changes have taken place via the writer.    * Note that even if the writer has only performed    * merging, this method will still return false.</p>    *    *<p>In any event, if this returns false, you should call    * {@link #openIfChanged} to get a new reader that sees the    * changes.</p>    *    * @throws CorruptIndexException if the index is corrupt    * @throws IOException           if there is a low-level IO error    * @throws UnsupportedOperationException unless overridden in subclass    */
DECL|method|isCurrent
specifier|public
specifier|abstract
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
function_decl|;
comment|/**    * Returns the time the index in the named directory was last modified.     * Do not use this to check whether the reader is still up-to-date, use    * {@link #isCurrent()} instead.     * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|lastModified
specifier|public
specifier|static
name|long
name|lastModified
parameter_list|(
specifier|final
name|Directory
name|directory
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
operator|(
operator|(
name|Long
operator|)
operator|new
name|SegmentInfos
operator|.
name|FindSegmentsFile
argument_list|(
name|directory
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Object
name|doBody
parameter_list|(
name|String
name|segmentFileName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|directory
operator|.
name|fileModified
argument_list|(
name|segmentFileName
argument_list|)
argument_list|)
return|;
block|}
block|}
operator|.
name|run
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
comment|/**    * Reads version number from segments files. The version number is    * initialized with a timestamp and then increased by one for each change of    * the index.    *     * @param directory where the index resides.    * @return version number.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|getCurrentVersion
specifier|public
specifier|static
name|long
name|getCurrentVersion
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|SegmentInfos
operator|.
name|readCurrentVersion
argument_list|(
name|directory
argument_list|)
return|;
block|}
comment|/**    * Reads commitUserData, previously passed to {@link    * IndexWriter#commit(Map)}, from current index    * segments file.  This will return null if {@link    * IndexWriter#commit(Map)} has never been called for    * this index.    *     * @param directory where the index resides.    * @return commit userData.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    *    * @see #getCommitUserData()    */
DECL|method|getCommitUserData
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getCommitUserData
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|SegmentInfos
operator|.
name|readCurrentUserData
argument_list|(
name|directory
argument_list|)
return|;
block|}
comment|/**    * Retrieve the String userData optionally passed to    * IndexWriter#commit.  This will return null if {@link    * IndexWriter#commit(Map)} has never been called for    * this index.    *    * @see #getCommitUserData(Directory)    */
DECL|method|getCommitUserData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getCommitUserData
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This reader does not support this method."
argument_list|)
throw|;
block|}
comment|/**    * Expert: return the IndexCommit that this reader has    * opened.  This method is only implemented by those    * readers that correspond to a Directory with its own    * segments_N file.    *    * @lucene.experimental    */
DECL|method|getIndexCommit
specifier|public
name|IndexCommit
name|getIndexCommit
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This reader does not support this method."
argument_list|)
throw|;
block|}
comment|/** Expert: returns the sequential sub readers that this    *  reader is logically composed of. If this reader is not composed    *  of sequential child readers, it should return null.    *  If this method returns an empty array, that means this    *  reader is a null reader (for example a MultiReader    *  that has no sub readers).    */
DECL|method|getSequentialSubReaders
specifier|public
specifier|abstract
name|IndexReader
index|[]
name|getSequentialSubReaders
parameter_list|()
function_decl|;
comment|/** For IndexReader implementations that use    *  TermInfosReader to read terms, this returns the    *  current indexDivisor as specified when the reader was    *  opened.    */
DECL|method|getTermInfosIndexDivisor
specifier|public
name|int
name|getTermInfosIndexDivisor
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This reader does not support this method."
argument_list|)
throw|;
block|}
comment|/**    * {@link ReaderContext} for {@link CompositeIndexReader} instance.    * @lucene.experimental    */
DECL|class|CompositeReaderContext
specifier|public
specifier|static
specifier|final
class|class
name|CompositeReaderContext
extends|extends
name|ReaderContext
block|{
DECL|field|children
specifier|private
specifier|final
name|ReaderContext
index|[]
name|children
decl_stmt|;
DECL|field|leaves
specifier|private
specifier|final
name|AtomicReaderContext
index|[]
name|leaves
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|CompositeIndexReader
name|reader
decl_stmt|;
comment|/**      * Creates a {@link CompositeReaderContext} for intermediate readers that aren't      * not top-level readers in the current context      */
DECL|method|CompositeReaderContext
specifier|public
name|CompositeReaderContext
parameter_list|(
name|CompositeReaderContext
name|parent
parameter_list|,
name|CompositeIndexReader
name|reader
parameter_list|,
name|int
name|ordInParent
parameter_list|,
name|int
name|docbaseInParent
parameter_list|,
name|ReaderContext
index|[]
name|children
parameter_list|)
block|{
name|this
argument_list|(
name|parent
argument_list|,
name|reader
argument_list|,
name|ordInParent
argument_list|,
name|docbaseInParent
argument_list|,
name|children
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a {@link CompositeReaderContext} for top-level readers with parent set to<code>null</code>      */
DECL|method|CompositeReaderContext
specifier|public
name|CompositeReaderContext
parameter_list|(
name|CompositeIndexReader
name|reader
parameter_list|,
name|ReaderContext
index|[]
name|children
parameter_list|,
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|reader
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|children
argument_list|,
name|leaves
argument_list|)
expr_stmt|;
block|}
DECL|method|CompositeReaderContext
specifier|private
name|CompositeReaderContext
parameter_list|(
name|CompositeReaderContext
name|parent
parameter_list|,
name|CompositeIndexReader
name|reader
parameter_list|,
name|int
name|ordInParent
parameter_list|,
name|int
name|docbaseInParent
parameter_list|,
name|ReaderContext
index|[]
name|children
parameter_list|,
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|ordInParent
argument_list|,
name|docbaseInParent
argument_list|)
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|children
expr_stmt|;
name|this
operator|.
name|leaves
operator|=
name|leaves
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|leaves
specifier|public
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|()
block|{
return|return
name|leaves
return|;
block|}
annotation|@
name|Override
DECL|method|children
specifier|public
name|ReaderContext
index|[]
name|children
parameter_list|()
block|{
return|return
name|children
return|;
block|}
annotation|@
name|Override
DECL|method|reader
specifier|public
name|CompositeIndexReader
name|reader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
block|}
block|}
end_class
end_unit
