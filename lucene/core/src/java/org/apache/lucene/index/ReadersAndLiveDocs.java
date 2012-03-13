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
name|codecs
operator|.
name|LiveDocsFormat
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
name|MutableBits
import|;
end_import
begin_comment
comment|// Used by IndexWriter to hold open SegmentReaders (for
end_comment
begin_comment
comment|// searching or merging), plus pending deletes,
end_comment
begin_comment
comment|// for a given segment
end_comment
begin_class
DECL|class|ReadersAndLiveDocs
class|class
name|ReadersAndLiveDocs
block|{
comment|// Not final because we replace (clone) when we need to
comment|// change it and it's been shared:
DECL|field|info
specifier|public
specifier|final
name|SegmentInfo
name|info
decl_stmt|;
comment|// Tracks how many consumers are using this instance:
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
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
comment|// Set once (null, and then maybe set, and never set again):
DECL|field|reader
specifier|private
name|SegmentReader
name|reader
decl_stmt|;
comment|// TODO: it's sometimes wasteful that we hold open two
comment|// separate SRs (one for merging one for
comment|// reading)... maybe just use a single SR?  The gains of
comment|// not loading the terms index (for merging in the
comment|// non-NRT case) are far less now... and if the app has
comment|// any deletes it'll open real readers anyway.
comment|// Set once (null, and then maybe set, and never set again):
DECL|field|mergeReader
specifier|private
name|SegmentReader
name|mergeReader
decl_stmt|;
comment|// Holds the current shared (readable and writable
comment|// liveDocs).  This is null when there are no deleted
comment|// docs, and it's copy-on-write (cloned whenever we need
comment|// to change it but it's been shared to an external NRT
comment|// reader).
DECL|field|liveDocs
specifier|private
name|Bits
name|liveDocs
decl_stmt|;
comment|// How many further deletions we've done against
comment|// liveDocs vs when we loaded it or last wrote it:
DECL|field|pendingDeleteCount
specifier|private
name|int
name|pendingDeleteCount
decl_stmt|;
comment|// True if the current liveDocs is referenced by an
comment|// external NRT reader:
DECL|field|shared
specifier|private
name|boolean
name|shared
decl_stmt|;
DECL|method|ReadersAndLiveDocs
specifier|public
name|ReadersAndLiveDocs
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|SegmentInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|shared
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|incRef
specifier|public
name|void
name|incRef
parameter_list|()
block|{
specifier|final
name|int
name|rc
init|=
name|refCount
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
assert|assert
name|rc
operator|>
literal|1
assert|;
block|}
DECL|method|decRef
specifier|public
name|void
name|decRef
parameter_list|()
block|{
specifier|final
name|int
name|rc
init|=
name|refCount
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
assert|assert
name|rc
operator|>=
literal|0
assert|;
block|}
DECL|method|refCount
specifier|public
name|int
name|refCount
parameter_list|()
block|{
specifier|final
name|int
name|rc
init|=
name|refCount
operator|.
name|get
argument_list|()
decl_stmt|;
assert|assert
name|rc
operator|>=
literal|0
assert|;
return|return
name|rc
return|;
block|}
DECL|method|getPendingDeleteCount
specifier|public
specifier|synchronized
name|int
name|getPendingDeleteCount
parameter_list|()
block|{
return|return
name|pendingDeleteCount
return|;
block|}
comment|// Call only from assert!
DECL|method|verifyDocCounts
specifier|public
specifier|synchronized
name|boolean
name|verifyDocCounts
parameter_list|()
block|{
name|int
name|count
decl_stmt|;
if|if
condition|(
name|liveDocs
operator|!=
literal|null
condition|)
block|{
name|count
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|info
operator|.
name|docCount
condition|;
name|docID
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|count
operator|=
name|info
operator|.
name|docCount
expr_stmt|;
block|}
assert|assert
name|info
operator|.
name|docCount
operator|-
name|info
operator|.
name|getDelCount
argument_list|()
operator|-
name|pendingDeleteCount
operator|==
name|count
operator|:
literal|"info.docCount="
operator|+
name|info
operator|.
name|docCount
operator|+
literal|" info.getDelCount()="
operator|+
name|info
operator|.
name|getDelCount
argument_list|()
operator|+
literal|" pendingDeleteCount="
operator|+
name|pendingDeleteCount
operator|+
literal|" count="
operator|+
name|count
assert|;
empty_stmt|;
return|return
literal|true
return|;
block|}
comment|// Get reader for searching/deleting
DECL|method|getReader
specifier|public
specifier|synchronized
name|SegmentReader
name|getReader
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("  livedocs=" + rld.liveDocs);
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
comment|// We steal returned ref:
name|reader
operator|=
operator|new
name|SegmentReader
argument_list|(
name|info
argument_list|,
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getReaderTermsIndexDivisor
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
condition|)
block|{
name|liveDocs
operator|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
block|}
comment|//System.out.println("ADD seg=" + rld.info + " isMerge=" + isMerge + " " + readerMap.size() + " in pool");
comment|//System.out.println(Thread.currentThread().getName() + ": getReader seg=" + info.name);
block|}
comment|// Ref for caller
name|reader
operator|.
name|incRef
argument_list|()
expr_stmt|;
return|return
name|reader
return|;
block|}
comment|// Get reader for merging (does not load the terms
comment|// index):
DECL|method|getMergeReader
specifier|public
specifier|synchronized
name|SegmentReader
name|getMergeReader
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("  livedocs=" + rld.liveDocs);
if|if
condition|(
name|mergeReader
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
comment|// Just use the already opened non-merge reader
comment|// for merging.  In the NRT case this saves us
comment|// pointless double-open:
comment|//System.out.println("PROMOTE non-merge reader seg=" + rld.info);
comment|// Ref for us:
name|reader
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|mergeReader
operator|=
name|reader
expr_stmt|;
comment|//System.out.println(Thread.currentThread().getName() + ": getMergeReader share seg=" + info.name);
block|}
else|else
block|{
comment|//System.out.println(Thread.currentThread().getName() + ": getMergeReader seg=" + info.name);
comment|// We steal returned ref:
name|mergeReader
operator|=
operator|new
name|SegmentReader
argument_list|(
name|info
argument_list|,
operator|-
literal|1
argument_list|,
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
condition|)
block|{
name|liveDocs
operator|=
name|mergeReader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Ref for caller
name|mergeReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
return|return
name|mergeReader
return|;
block|}
DECL|method|release
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|(
name|SegmentReader
name|sr
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|info
operator|==
name|sr
operator|.
name|getSegmentInfo
argument_list|()
assert|;
name|sr
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
DECL|method|delete
specifier|public
specifier|synchronized
name|boolean
name|delete
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|liveDocs
operator|!=
literal|null
assert|;
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
assert|assert
name|docID
operator|>=
literal|0
operator|&&
name|docID
operator|<
name|liveDocs
operator|.
name|length
argument_list|()
operator|:
literal|"out of bounds: docid="
operator|+
name|docID
operator|+
literal|" liveDocsLength="
operator|+
name|liveDocs
operator|.
name|length
argument_list|()
operator|+
literal|" seg="
operator|+
name|info
operator|.
name|name
operator|+
literal|" docCount="
operator|+
name|info
operator|.
name|docCount
assert|;
assert|assert
operator|!
name|shared
assert|;
specifier|final
name|boolean
name|didDelete
init|=
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|didDelete
condition|)
block|{
operator|(
operator|(
name|MutableBits
operator|)
name|liveDocs
operator|)
operator|.
name|clear
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|pendingDeleteCount
operator|++
expr_stmt|;
comment|//System.out.println("  new del seg=" + info + " docID=" + docID + " pendingDelCount=" + pendingDeleteCount + " totDelCount=" + (info.docCount-liveDocs.count()));
block|}
return|return
name|didDelete
return|;
block|}
comment|// NOTE: removes callers ref
DECL|method|dropReaders
specifier|public
specifier|synchronized
name|void
name|dropReaders
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
comment|//System.out.println("  pool.drop info=" + info + " rc=" + reader.getRefCount());
name|reader
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|mergeReader
operator|!=
literal|null
condition|)
block|{
comment|//System.out.println("  pool.drop info=" + info + " merge rc=" + mergeReader.getRefCount());
name|mergeReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|mergeReader
operator|=
literal|null
expr_stmt|;
block|}
name|decRef
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a ref to a clone.  NOTE: this clone is not    * enrolled in the pool, so you should simply close()    * it when you're done (ie, do not call release()).    */
DECL|method|getReadOnlyClone
specifier|public
specifier|synchronized
name|SegmentReader
name|getReadOnlyClone
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|getReader
argument_list|(
name|context
argument_list|)
operator|.
name|decRef
argument_list|()
expr_stmt|;
assert|assert
name|reader
operator|!=
literal|null
assert|;
block|}
name|shared
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|SegmentReader
argument_list|(
name|reader
operator|.
name|getSegmentInfo
argument_list|()
argument_list|,
name|reader
operator|.
name|core
argument_list|,
name|liveDocs
argument_list|,
name|info
operator|.
name|docCount
operator|-
name|info
operator|.
name|getDelCount
argument_list|()
operator|-
name|pendingDeleteCount
argument_list|)
return|;
block|}
else|else
block|{
assert|assert
name|reader
operator|.
name|getLiveDocs
argument_list|()
operator|==
name|liveDocs
assert|;
name|reader
operator|.
name|incRef
argument_list|()
expr_stmt|;
return|return
name|reader
return|;
block|}
block|}
DECL|method|initWritableLiveDocs
specifier|public
specifier|synchronized
name|void
name|initWritableLiveDocs
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
assert|assert
name|info
operator|.
name|docCount
operator|>
literal|0
assert|;
comment|//System.out.println("initWritableLivedocs seg=" + info + " liveDocs=" + liveDocs + " shared=" + shared);
if|if
condition|(
name|shared
condition|)
block|{
comment|// Copy on write: this means we've cloned a
comment|// SegmentReader sharing the current liveDocs
comment|// instance; must now make a private clone so we can
comment|// change it:
name|LiveDocsFormat
name|liveDocsFormat
init|=
name|info
operator|.
name|getCodec
argument_list|()
operator|.
name|liveDocsFormat
argument_list|()
decl_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
condition|)
block|{
comment|//System.out.println("create BV seg=" + info);
name|liveDocs
operator|=
name|liveDocsFormat
operator|.
name|newLiveDocs
argument_list|(
name|info
operator|.
name|docCount
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|liveDocs
operator|=
name|liveDocsFormat
operator|.
name|newLiveDocs
argument_list|(
name|liveDocs
argument_list|)
expr_stmt|;
block|}
name|shared
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|liveDocs
operator|!=
literal|null
assert|;
block|}
block|}
DECL|method|getLiveDocs
specifier|public
specifier|synchronized
name|Bits
name|getLiveDocs
parameter_list|()
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
return|return
name|liveDocs
return|;
block|}
DECL|method|getReadOnlyLiveDocs
specifier|public
specifier|synchronized
name|Bits
name|getReadOnlyLiveDocs
parameter_list|()
block|{
comment|//System.out.println("getROLiveDocs seg=" + info);
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
name|shared
operator|=
literal|true
expr_stmt|;
comment|//if (liveDocs != null) {
comment|//System.out.println("  liveCount=" + liveDocs.count());
comment|//}
return|return
name|liveDocs
return|;
block|}
DECL|method|dropChanges
specifier|public
specifier|synchronized
name|void
name|dropChanges
parameter_list|()
block|{
comment|// Discard (don't save) changes when we are dropping
comment|// the reader; this is used only on the sub-readers
comment|// after a successful merge.  If deletes had
comment|// accumulated on those sub-readers while the merge
comment|// is running, by now we have carried forward those
comment|// deletes onto the newly merged segment, so we can
comment|// discard them on the sub-readers:
name|pendingDeleteCount
operator|=
literal|0
expr_stmt|;
block|}
comment|// Commit live docs to the directory (writes new
comment|// _X_N.del files); returns true if it wrote the file
comment|// and false if there were no new deletes to write:
DECL|method|writeLiveDocs
specifier|public
specifier|synchronized
name|boolean
name|writeLiveDocs
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("rld.writeLiveDocs seg=" + info + " pendingDelCount=" + pendingDeleteCount);
if|if
condition|(
name|pendingDeleteCount
operator|!=
literal|0
condition|)
block|{
comment|// We have new deletes
assert|assert
name|liveDocs
operator|.
name|length
argument_list|()
operator|==
name|info
operator|.
name|docCount
assert|;
comment|// Save in case we need to rollback on failure:
specifier|final
name|SegmentInfo
name|sav
init|=
operator|(
name|SegmentInfo
operator|)
name|info
operator|.
name|clone
argument_list|()
decl_stmt|;
name|info
operator|.
name|advanceDelGen
argument_list|()
expr_stmt|;
name|info
operator|.
name|setDelCount
argument_list|(
name|info
operator|.
name|getDelCount
argument_list|()
operator|+
name|pendingDeleteCount
argument_list|)
expr_stmt|;
comment|// We can write directly to the actual name (vs to a
comment|// .tmp& renaming it) because the file is not live
comment|// until segments file is written:
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|info
operator|.
name|getCodec
argument_list|()
operator|.
name|liveDocsFormat
argument_list|()
operator|.
name|writeLiveDocs
argument_list|(
operator|(
name|MutableBits
operator|)
name|liveDocs
argument_list|,
name|dir
argument_list|,
name|info
argument_list|,
name|IOContext
operator|.
name|DEFAULT
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
name|info
operator|.
name|reset
argument_list|(
name|sav
argument_list|)
expr_stmt|;
block|}
block|}
name|pendingDeleteCount
operator|=
literal|0
expr_stmt|;
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
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ReadersAndLiveDocs(seg="
operator|+
name|info
operator|+
literal|" pendingDeleteCount="
operator|+
name|pendingDeleteCount
operator|+
literal|" shared="
operator|+
name|shared
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
