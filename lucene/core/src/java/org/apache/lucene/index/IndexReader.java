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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|util
operator|.
name|Bits
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_comment
comment|/** IndexReader is an abstract class, providing an interface for accessing an  index.  Search of an index is done entirely through this abstract interface,  so that any subclass which implements it is searchable.<p>There are two different types of IndexReaders:<ul><li>{@link AtomicReader}: These indexes do not consist of several sub-readers,   they are atomic. They support retrieval of stored fields, doc values, terms,   and postings.<li>{@link CompositeReader}: Instances (like {@link DirectoryReader})   of this reader can only   be used to get stored fields from the underlying AtomicReaders,   but it is not possible to directly retrieve postings. To do that, get   the sub-readers via {@link CompositeReader#getSequentialSubReaders}.   Alternatively, you can mimic an {@link AtomicReader} (with a serious slowdown),   by wrapping composite readers with {@link SlowCompositeReaderWrapper}.</ul><p>IndexReader instances for indexes on disk are usually constructed  with a call to one of the static<code>DirectoryReader.open()</code> methods,  e.g. {@link DirectoryReader#open(org.apache.lucene.store.Directory)}. {@link DirectoryReader} implements  the {@link CompositeReader} interface, it is not possible to directly get postings.<p> For efficiency, in this API documents are often referred to via<i>document numbers</i>, non-negative integers which each name a unique  document in the index.  These document numbers are ephemeral -- they may change  as documents are added to and deleted from an index.  Clients should thus not  rely on a given document having the same number between sessions.<p><a name="thread-safety"></a><p><b>NOTE</b>: {@link  IndexReader} instances are completely thread  safe, meaning multiple threads can call any of its methods,  concurrently.  If your application requires external  synchronization, you should<b>not</b> synchronize on the<code>IndexReader</code> instance; use your own  (non-Lucene) objects instead. */
end_comment
begin_class
DECL|class|IndexReader
specifier|public
specifier|abstract
class|class
name|IndexReader
implements|implements
name|Closeable
block|{
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|closedByChild
specifier|private
name|boolean
name|closedByChild
init|=
literal|false
decl_stmt|;
DECL|field|refCount
specifier|private
specifier|final
name|AtomicInteger
name|refCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|method|IndexReader
name|IndexReader
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
name|this
operator|instanceof
name|CompositeReader
operator|||
name|this
operator|instanceof
name|AtomicReader
operator|)
condition|)
throw|throw
operator|new
name|Error
argument_list|(
literal|"IndexReader should never be directly extended, subclass AtomicReader or CompositeReader instead."
argument_list|)
throw|;
block|}
comment|/**    * A custom listener that's invoked when the IndexReader    * is closed.    *    * @lucene.experimental    */
DECL|interface|ReaderClosedListener
specifier|public
specifier|static
interface|interface
name|ReaderClosedListener
block|{
comment|/** Invoked when the {@link IndexReader} is closed. */
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
function_decl|;
block|}
DECL|field|readerClosedListeners
specifier|private
specifier|final
name|Set
argument_list|<
name|ReaderClosedListener
argument_list|>
name|readerClosedListeners
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|LinkedHashSet
argument_list|<
name|ReaderClosedListener
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|parentReaders
specifier|private
specifier|final
name|Set
argument_list|<
name|IndexReader
argument_list|>
name|parentReaders
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|IndexReader
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|/** Expert: adds a {@link ReaderClosedListener}.  The    * provided listener will be invoked when this reader is closed.    *    * @lucene.experimental */
DECL|method|addReaderClosedListener
specifier|public
specifier|final
name|void
name|addReaderClosedListener
parameter_list|(
name|ReaderClosedListener
name|listener
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|readerClosedListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: remove a previously added {@link ReaderClosedListener}.    *    * @lucene.experimental */
DECL|method|removeReaderClosedListener
specifier|public
specifier|final
name|void
name|removeReaderClosedListener
parameter_list|(
name|ReaderClosedListener
name|listener
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|readerClosedListeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: This method is called by {@code IndexReader}s which wrap other readers    * (e.g. {@link CompositeReader} or {@link FilterAtomicReader}) to register the parent    * at the child (this reader) on construction of the parent. When this reader is closed,    * it will mark all registered parents as closed, too. The references to parent readers    * are weak only, so they can be GCed once they are no longer in use.    * @lucene.experimental */
DECL|method|registerParentReader
specifier|public
specifier|final
name|void
name|registerParentReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|parentReaders
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|notifyReaderClosedListeners
specifier|private
name|void
name|notifyReaderClosedListeners
parameter_list|()
block|{
synchronized|synchronized
init|(
name|readerClosedListeners
init|)
block|{
for|for
control|(
name|ReaderClosedListener
name|listener
range|:
name|readerClosedListeners
control|)
block|{
name|listener
operator|.
name|onClose
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|reportCloseToParentReaders
specifier|private
name|void
name|reportCloseToParentReaders
parameter_list|()
block|{
synchronized|synchronized
init|(
name|parentReaders
init|)
block|{
for|for
control|(
name|IndexReader
name|parent
range|:
name|parentReaders
control|)
block|{
name|parent
operator|.
name|closedByChild
operator|=
literal|true
expr_stmt|;
comment|// cross memory barrier by a fake write:
name|parent
operator|.
name|refCount
operator|.
name|addAndGet
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// recurse:
name|parent
operator|.
name|reportCloseToParentReaders
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Expert: returns the current refCount for this reader */
DECL|method|getRefCount
specifier|public
specifier|final
name|int
name|getRefCount
parameter_list|()
block|{
comment|// NOTE: don't ensureOpen, so that callers can see
comment|// refCount is 0 (reader is closed)
return|return
name|refCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Expert: increments the refCount of this IndexReader    * instance.  RefCounts are used to determine when a    * reader can be closed safely, i.e. as soon as there are    * no more references.  Be sure to always call a    * corresponding {@link #decRef}, in a finally clause;    * otherwise the reader may never be closed.  Note that    * {@link #close} simply calls decRef(), which means that    * the IndexReader will not really be closed until {@link    * #decRef} has been called for all outstanding    * references.    *    * @see #decRef    * @see #tryIncRef    */
DECL|method|incRef
specifier|public
specifier|final
name|void
name|incRef
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|refCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * Expert: increments the refCount of this IndexReader    * instance only if the IndexReader has not been closed yet    * and returns<code>true</code> iff the refCount was    * successfully incremented, otherwise<code>false</code>.    * If this method returns<code>false</code> the reader is either    * already closed or is currently been closed. Either way this    * reader instance shouldn't be used by an application unless    *<code>true</code> is returned.    *<p>    * RefCounts are used to determine when a    * reader can be closed safely, i.e. as soon as there are    * no more references.  Be sure to always call a    * corresponding {@link #decRef}, in a finally clause;    * otherwise the reader may never be closed.  Note that    * {@link #close} simply calls decRef(), which means that    * the IndexReader will not really be closed until {@link    * #decRef} has been called for all outstanding    * references.    *    * @see #decRef    * @see #incRef    */
DECL|method|tryIncRef
specifier|public
specifier|final
name|boolean
name|tryIncRef
parameter_list|()
block|{
name|int
name|count
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|refCount
operator|.
name|get
argument_list|()
operator|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|refCount
operator|.
name|compareAndSet
argument_list|(
name|count
argument_list|,
name|count
operator|+
literal|1
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Expert: decreases the refCount of this IndexReader    * instance.  If the refCount drops to 0, then this    * reader is closed.  If an exception is hit, the refCount    * is unchanged.    *    * @throws IOException in case an IOException occurs in  doClose()    *    * @see #incRef    */
DECL|method|decRef
specifier|public
specifier|final
name|void
name|decRef
parameter_list|()
throws|throws
name|IOException
block|{
comment|// only check refcount here (don't call ensureOpen()), so we can
comment|// still close the reader if it was made invalid by a child:
if|if
condition|(
name|refCount
operator|.
name|get
argument_list|()
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this IndexReader is closed"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|rc
init|=
name|refCount
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|0
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|doClose
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
comment|// Put reference back on failure
name|refCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
name|reportCloseToParentReaders
argument_list|()
expr_stmt|;
name|notifyReaderClosedListeners
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rc
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"too many decRef calls: refCount is "
operator|+
name|rc
operator|+
literal|" after decrement"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Throws AlreadyClosedException if this IndexReader or any    * of its child readers is closed, otherwise returns.    */
DECL|method|ensureOpen
specifier|protected
specifier|final
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|refCount
operator|.
name|get
argument_list|()
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this IndexReader is closed"
argument_list|)
throw|;
block|}
comment|// the happens before rule on reading the refCount, which must be after the fake write,
comment|// ensures that we see the value:
if|if
condition|(
name|closedByChild
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this IndexReader cannot be used anymore as one of its child readers was closed"
argument_list|)
throw|;
block|}
block|}
comment|/** {@inheritDoc}    *<p>For caching purposes, {@code IndexReader} subclasses are not allowed    * to implement equals/hashCode, so methods are declared final.    * To lookup instances from caches use {@link #getCoreCacheKey} and     * {@link #getCombinedCoreAndDeletesKey}.    */
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
name|this
operator|==
name|obj
operator|)
return|;
block|}
comment|/** {@inheritDoc}    *<p>For caching purposes, {@code IndexReader} subclasses are not allowed    * to implement equals/hashCode, so methods are declared final.    * To lookup instances from caches use {@link #getCoreCacheKey} and     * {@link #getCombinedCoreAndDeletesKey}.    */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Retrieve term vectors for this document, or null if    *  term vectors were not indexed.  The returned Fields    *  instance acts like a single-document inverted index    *  (the docID will be 0). */
DECL|method|getTermVectors
specifier|public
specifier|abstract
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Retrieve term vector for this document and field, or    *  null if term vectors were not indexed.  The returned    *  Fields instance acts like a single-document inverted    *  index (the docID will be 0). */
DECL|method|getTermVector
specifier|public
specifier|final
name|Terms
name|getTermVector
parameter_list|(
name|int
name|docID
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Fields
name|vectors
init|=
name|getTermVectors
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|vectors
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|vectors
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/** Returns the number of documents in this index. */
DECL|method|numDocs
specifier|public
specifier|abstract
name|int
name|numDocs
parameter_list|()
function_decl|;
comment|/** Returns one greater than the largest possible document number.    * This may be used to, e.g., determine how big to allocate an array which    * will have an element for every document number in an index.    */
DECL|method|maxDoc
specifier|public
specifier|abstract
name|int
name|maxDoc
parameter_list|()
function_decl|;
comment|/** Returns the number of deleted documents. */
DECL|method|numDeletedDocs
specifier|public
specifier|final
name|int
name|numDeletedDocs
parameter_list|()
block|{
return|return
name|maxDoc
argument_list|()
operator|-
name|numDocs
argument_list|()
return|;
block|}
comment|/** Expert: visits the fields of a stored document, for    *  custom processing/loading of each field.  If you    *  simply want to load all fields, use {@link    *  #document(int)}.  If you want to load a subset, use    *  {@link DocumentStoredFieldVisitor}.  */
DECL|method|document
specifier|public
specifier|abstract
name|void
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the stored fields of the<code>n</code><sup>th</sup>    *<code>Document</code> in this index.  This is just    * sugar for using {@link DocumentStoredFieldVisitor}.    *<p>    *<b>NOTE:</b> for performance reasons, this method does not check if the    * requested document is deleted, and therefore asking for a deleted document    * may yield unspecified results. Usually this is not required, however you    * can test if the doc is deleted by checking the {@link    * Bits} returned from {@link MultiFields#getLiveDocs}.    *    *<b>NOTE:</b> only the content of a field is returned,    * if that field was stored during indexing.  Metadata    * like boost, omitNorm, IndexOptions, tokenized, etc.,    * are not preserved.    *     * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
comment|// TODO: we need a separate StoredField, so that the
comment|// Document returned here contains that class not
comment|// IndexableField
DECL|method|document
specifier|public
specifier|final
name|StoredDocument
name|document
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocumentStoredFieldVisitor
name|visitor
init|=
operator|new
name|DocumentStoredFieldVisitor
argument_list|()
decl_stmt|;
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
return|return
name|visitor
operator|.
name|getDocument
argument_list|()
return|;
block|}
comment|/**    * Like {@link #document(int)} but only loads the specified    * fields.  Note that this is simply sugar for {@link    * DocumentStoredFieldVisitor#DocumentStoredFieldVisitor(Set)}.    */
DECL|method|document
specifier|public
specifier|final
name|StoredDocument
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|fieldsToLoad
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocumentStoredFieldVisitor
name|visitor
init|=
operator|new
name|DocumentStoredFieldVisitor
argument_list|(
name|fieldsToLoad
argument_list|)
decl_stmt|;
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
return|return
name|visitor
operator|.
name|getDocument
argument_list|()
return|;
block|}
comment|/** Returns true if any documents have been deleted. Implementers should    *  consider overriding this method if {@link #maxDoc()} or {@link #numDocs()}    *  are not constant-time operations. */
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
name|numDeletedDocs
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/**    * Closes files associated with this index.    * Also saves any new deletions to disk.    * No other methods should be called after this has been called.    * @throws IOException if there is a low-level IO error    */
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|final
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|decRef
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/** Implements close. */
DECL|method|doClose
specifier|protected
specifier|abstract
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Expert: Returns the root {@link IndexReaderContext} for this    * {@link IndexReader}'s sub-reader tree.     *<p>    * Iff this reader is composed of sub    * readers, i.e. this reader being a composite reader, this method returns a    * {@link CompositeReaderContext} holding the reader's direct children as well as a    * view of the reader tree's atomic leaf contexts. All sub-    * {@link IndexReaderContext} instances referenced from this readers top-level    * context are private to this reader and are not shared with another context    * tree. For example, IndexSearcher uses this API to drive searching by one    * atomic leaf reader at a time. If this reader is not composed of child    * readers, this method returns an {@link AtomicReaderContext}.    *<p>    * Note: Any of the sub-{@link CompositeReaderContext} instances referenced    * from this top-level context do not support {@link CompositeReaderContext#leaves()}.    * Only the top-level context maintains the convenience leaf-view    * for performance reasons.    */
DECL|method|getContext
specifier|public
specifier|abstract
name|IndexReaderContext
name|getContext
parameter_list|()
function_decl|;
comment|/**    * Returns the reader's leaves, or itself if this reader is atomic.    * This is a convenience method calling {@code this.getContext().leaves()}.    * @see IndexReaderContext#leaves()    */
DECL|method|leaves
specifier|public
specifier|final
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
parameter_list|()
block|{
return|return
name|getContext
argument_list|()
operator|.
name|leaves
argument_list|()
return|;
block|}
comment|/** Expert: Returns a key for this IndexReader, so FieldCache/CachingWrapperFilter can find    * it again.    * This key must not have equals()/hashCode() methods, so&quot;equals&quot; means&quot;identical&quot;. */
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
comment|// Don't can ensureOpen since FC calls this (to evict)
comment|// on close
return|return
name|this
return|;
block|}
comment|/** Expert: Returns a key for this IndexReader that also includes deletions,    * so FieldCache/CachingWrapperFilter can find it again.    * This key must not have equals()/hashCode() methods, so&quot;equals&quot; means&quot;identical&quot;. */
DECL|method|getCombinedCoreAndDeletesKey
specifier|public
name|Object
name|getCombinedCoreAndDeletesKey
parameter_list|()
block|{
comment|// Don't can ensureOpen since FC calls this (to evict)
comment|// on close
return|return
name|this
return|;
block|}
comment|/** Returns the number of documents containing the     *<code>term</code>.  This method returns 0 if the term or    * field does not exists.  This method does not take into    * account deleted documents that have not yet been merged    * away.     * @see TermsEnum#docFreq()    */
DECL|method|docFreq
specifier|public
specifier|abstract
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of documents containing the term    *<code>term</code>.  This method returns 0 if the term or    * field does not exists, or -1 if the Codec does not support    * the measure.  This method does not take into account deleted     * documents that have not yet been merged away.    * @see TermsEnum#totalTermFreq()     */
DECL|method|totalTermFreq
specifier|public
specifier|abstract
name|long
name|totalTermFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the sum of {@link TermsEnum#docFreq()} for all terms in this field,    * or -1 if this measure isn't stored by the codec. Note that, just like other    * term measures, this measure does not take deleted documents into account.    *     * @see Terms#getSumDocFreq()    */
DECL|method|getSumDocFreq
specifier|public
specifier|abstract
name|long
name|getSumDocFreq
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the number of documents that have at least one term for this field,    * or -1 if this measure isn't stored by the codec. Note that, just like other    * term measures, this measure does not take deleted documents into account.    *     * @see Terms#getDocCount()    */
DECL|method|getDocCount
specifier|public
specifier|abstract
name|int
name|getDocCount
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the sum of {@link TermsEnum#totalTermFreq} for all terms in this    * field, or -1 if this measure isn't stored by the codec (or if this fields    * omits term freq and positions). Note that, just like other term measures,    * this measure does not take deleted documents into account.    *     * @see Terms#getSumTotalTermFreq()    */
DECL|method|getSumTotalTermFreq
specifier|public
specifier|abstract
name|long
name|getSumTotalTermFreq
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class
end_unit
