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
name|store
operator|.
name|RAMOutputStream
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
name|ArrayUtil
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
name|Collection
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
begin_class
DECL|class|TermVectorsTermsWriter
specifier|final
class|class
name|TermVectorsTermsWriter
extends|extends
name|TermsHashConsumer
block|{
DECL|field|docWriter
specifier|final
name|DocumentsWriter
name|docWriter
decl_stmt|;
DECL|field|termVectorsWriter
name|TermVectorsWriter
name|termVectorsWriter
decl_stmt|;
DECL|field|docFreeList
name|PerDoc
index|[]
name|docFreeList
init|=
operator|new
name|PerDoc
index|[
literal|1
index|]
decl_stmt|;
DECL|field|freeCount
name|int
name|freeCount
decl_stmt|;
DECL|field|tvx
name|IndexOutput
name|tvx
decl_stmt|;
DECL|field|tvd
name|IndexOutput
name|tvd
decl_stmt|;
DECL|field|tvf
name|IndexOutput
name|tvf
decl_stmt|;
DECL|field|lastDocID
name|int
name|lastDocID
decl_stmt|;
DECL|method|TermVectorsTermsWriter
specifier|public
name|TermVectorsTermsWriter
parameter_list|(
name|DocumentsWriter
name|docWriter
parameter_list|)
block|{
name|this
operator|.
name|docWriter
operator|=
name|docWriter
expr_stmt|;
block|}
DECL|method|addThread
specifier|public
name|TermsHashConsumerPerThread
name|addThread
parameter_list|(
name|TermsHashPerThread
name|termsHashPerThread
parameter_list|)
block|{
return|return
operator|new
name|TermVectorsTermsWriterPerThread
argument_list|(
name|termsHashPerThread
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|createPostings
name|void
name|createPostings
parameter_list|(
name|RawPostingList
index|[]
name|postings
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|count
parameter_list|)
block|{
specifier|final
name|int
name|end
init|=
name|start
operator|+
name|count
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
name|postings
index|[
name|i
index|]
operator|=
operator|new
name|PostingList
argument_list|()
expr_stmt|;
block|}
DECL|method|flush
specifier|synchronized
name|void
name|flush
parameter_list|(
name|Map
argument_list|<
name|TermsHashConsumerPerThread
argument_list|,
name|Collection
argument_list|<
name|TermsHashConsumerPerField
argument_list|>
argument_list|>
name|threadsAndFields
parameter_list|,
specifier|final
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|tvx
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|state
operator|.
name|numDocsInStore
operator|>
literal|0
condition|)
comment|// In case there are some final documents that we
comment|// didn't see (because they hit a non-aborting exception):
name|fill
argument_list|(
name|state
operator|.
name|numDocsInStore
operator|-
name|docWriter
operator|.
name|getDocStoreOffset
argument_list|()
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|flush
argument_list|()
expr_stmt|;
name|tvd
operator|.
name|flush
argument_list|()
expr_stmt|;
name|tvf
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|TermsHashConsumerPerThread
argument_list|,
name|Collection
argument_list|<
name|TermsHashConsumerPerField
argument_list|>
argument_list|>
name|entry
range|:
name|threadsAndFields
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
specifier|final
name|TermsHashConsumerPerField
name|field
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|TermVectorsTermsWriterPerField
name|perField
init|=
operator|(
name|TermVectorsTermsWriterPerField
operator|)
name|field
decl_stmt|;
name|perField
operator|.
name|termsHashPerField
operator|.
name|reset
argument_list|()
expr_stmt|;
name|perField
operator|.
name|shrinkHash
argument_list|()
expr_stmt|;
block|}
name|TermVectorsTermsWriterPerThread
name|perThread
init|=
operator|(
name|TermVectorsTermsWriterPerThread
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|perThread
operator|.
name|termsHashPerThread
operator|.
name|reset
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|closeDocStore
specifier|synchronized
name|void
name|closeDocStore
parameter_list|(
specifier|final
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|tvx
operator|!=
literal|null
condition|)
block|{
comment|// At least one doc in this run had term vectors
comment|// enabled
name|fill
argument_list|(
name|state
operator|.
name|numDocsInStore
operator|-
name|docWriter
operator|.
name|getDocStoreOffset
argument_list|()
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|close
argument_list|()
expr_stmt|;
name|tvf
operator|.
name|close
argument_list|()
expr_stmt|;
name|tvd
operator|.
name|close
argument_list|()
expr_stmt|;
name|tvx
operator|=
literal|null
expr_stmt|;
assert|assert
name|state
operator|.
name|docStoreSegmentName
operator|!=
literal|null
assert|;
specifier|final
name|String
name|fileName
init|=
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_INDEX_EXTENSION
decl_stmt|;
if|if
condition|(
literal|4
operator|+
operator|(
operator|(
name|long
operator|)
name|state
operator|.
name|numDocsInStore
operator|)
operator|*
literal|16
operator|!=
name|state
operator|.
name|directory
operator|.
name|fileLength
argument_list|(
name|fileName
argument_list|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"after flush: tvx size mismatch: "
operator|+
name|state
operator|.
name|numDocsInStore
operator|+
literal|" docs vs "
operator|+
name|state
operator|.
name|directory
operator|.
name|fileLength
argument_list|(
name|fileName
argument_list|)
operator|+
literal|" length in bytes of "
operator|+
name|fileName
operator|+
literal|" file exists?="
operator|+
name|state
operator|.
name|directory
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
argument_list|)
throw|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_INDEX_EXTENSION
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_FIELDS_EXTENSION
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_DOCUMENTS_EXTENSION
argument_list|)
expr_stmt|;
name|docWriter
operator|.
name|removeOpenFile
argument_list|(
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_INDEX_EXTENSION
argument_list|)
expr_stmt|;
name|docWriter
operator|.
name|removeOpenFile
argument_list|(
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_FIELDS_EXTENSION
argument_list|)
expr_stmt|;
name|docWriter
operator|.
name|removeOpenFile
argument_list|(
name|state
operator|.
name|docStoreSegmentName
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_DOCUMENTS_EXTENSION
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|field|allocCount
name|int
name|allocCount
decl_stmt|;
DECL|method|getPerDoc
specifier|synchronized
name|PerDoc
name|getPerDoc
parameter_list|()
block|{
if|if
condition|(
name|freeCount
operator|==
literal|0
condition|)
block|{
name|allocCount
operator|++
expr_stmt|;
if|if
condition|(
name|allocCount
operator|>
name|docFreeList
operator|.
name|length
condition|)
block|{
comment|// Grow our free list up front to make sure we have
comment|// enough space to recycle all outstanding PerDoc
comment|// instances
assert|assert
name|allocCount
operator|==
literal|1
operator|+
name|docFreeList
operator|.
name|length
assert|;
name|docFreeList
operator|=
operator|new
name|PerDoc
index|[
name|ArrayUtil
operator|.
name|getNextSize
argument_list|(
name|allocCount
argument_list|)
index|]
expr_stmt|;
block|}
return|return
operator|new
name|PerDoc
argument_list|()
return|;
block|}
else|else
return|return
name|docFreeList
index|[
operator|--
name|freeCount
index|]
return|;
block|}
comment|/** Fills in no-term-vectors for all docs we haven't seen    *  since the last doc that had term vectors. */
DECL|method|fill
name|void
name|fill
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|docStoreOffset
init|=
name|docWriter
operator|.
name|getDocStoreOffset
argument_list|()
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|docID
operator|+
name|docStoreOffset
decl_stmt|;
if|if
condition|(
name|lastDocID
operator|<
name|end
condition|)
block|{
specifier|final
name|long
name|tvfPosition
init|=
name|tvf
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
while|while
condition|(
name|lastDocID
operator|<
name|end
condition|)
block|{
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvd
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|tvd
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvfPosition
argument_list|)
expr_stmt|;
name|lastDocID
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|method|initTermVectorsWriter
specifier|synchronized
name|void
name|initTermVectorsWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|tvx
operator|==
literal|null
condition|)
block|{
specifier|final
name|String
name|docStoreSegment
init|=
name|docWriter
operator|.
name|getDocStoreSegment
argument_list|()
decl_stmt|;
if|if
condition|(
name|docStoreSegment
operator|==
literal|null
condition|)
return|return;
assert|assert
name|docStoreSegment
operator|!=
literal|null
assert|;
comment|// If we hit an exception while init'ing the term
comment|// vector output files, we must abort this segment
comment|// because those files will be in an unknown
comment|// state:
name|tvx
operator|=
name|docWriter
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|docStoreSegment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_INDEX_EXTENSION
argument_list|)
expr_stmt|;
name|tvd
operator|=
name|docWriter
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|docStoreSegment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_DOCUMENTS_EXTENSION
argument_list|)
expr_stmt|;
name|tvf
operator|=
name|docWriter
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|docStoreSegment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_FIELDS_EXTENSION
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|writeInt
argument_list|(
name|TermVectorsReader
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
name|tvd
operator|.
name|writeInt
argument_list|(
name|TermVectorsReader
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeInt
argument_list|(
name|TermVectorsReader
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
name|docWriter
operator|.
name|addOpenFile
argument_list|(
name|docStoreSegment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_INDEX_EXTENSION
argument_list|)
expr_stmt|;
name|docWriter
operator|.
name|addOpenFile
argument_list|(
name|docStoreSegment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_FIELDS_EXTENSION
argument_list|)
expr_stmt|;
name|docWriter
operator|.
name|addOpenFile
argument_list|(
name|docStoreSegment
operator|+
literal|"."
operator|+
name|IndexFileNames
operator|.
name|VECTORS_DOCUMENTS_EXTENSION
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|method|finishDocument
specifier|synchronized
name|void
name|finishDocument
parameter_list|(
name|PerDoc
name|perDoc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docWriter
operator|.
name|writer
operator|.
name|testPoint
argument_list|(
literal|"TermVectorsTermsWriter.finishDocument start"
argument_list|)
assert|;
name|initTermVectorsWriter
argument_list|()
expr_stmt|;
name|fill
argument_list|(
name|perDoc
operator|.
name|docID
argument_list|)
expr_stmt|;
comment|// Append term vectors to the real outputs:
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvd
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvf
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|tvd
operator|.
name|writeVInt
argument_list|(
name|perDoc
operator|.
name|numVectorFields
argument_list|)
expr_stmt|;
if|if
condition|(
name|perDoc
operator|.
name|numVectorFields
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|perDoc
operator|.
name|numVectorFields
condition|;
name|i
operator|++
control|)
name|tvd
operator|.
name|writeVInt
argument_list|(
name|perDoc
operator|.
name|fieldNumbers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
assert|assert
literal|0
operator|==
name|perDoc
operator|.
name|fieldPointers
index|[
literal|0
index|]
assert|;
name|long
name|lastPos
init|=
name|perDoc
operator|.
name|fieldPointers
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|perDoc
operator|.
name|numVectorFields
condition|;
name|i
operator|++
control|)
block|{
name|long
name|pos
init|=
name|perDoc
operator|.
name|fieldPointers
index|[
name|i
index|]
decl_stmt|;
name|tvd
operator|.
name|writeVLong
argument_list|(
name|pos
operator|-
name|lastPos
argument_list|)
expr_stmt|;
name|lastPos
operator|=
name|pos
expr_stmt|;
block|}
name|perDoc
operator|.
name|tvf
operator|.
name|writeTo
argument_list|(
name|tvf
argument_list|)
expr_stmt|;
name|perDoc
operator|.
name|tvf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|perDoc
operator|.
name|numVectorFields
operator|=
literal|0
expr_stmt|;
block|}
assert|assert
name|lastDocID
operator|==
name|perDoc
operator|.
name|docID
operator|+
name|docWriter
operator|.
name|getDocStoreOffset
argument_list|()
assert|;
name|lastDocID
operator|++
expr_stmt|;
name|free
argument_list|(
name|perDoc
argument_list|)
expr_stmt|;
assert|assert
name|docWriter
operator|.
name|writer
operator|.
name|testPoint
argument_list|(
literal|"TermVectorsTermsWriter.finishDocument end"
argument_list|)
assert|;
block|}
DECL|method|freeRAM
specifier|public
name|boolean
name|freeRAM
parameter_list|()
block|{
comment|// We don't hold any state beyond one doc, so we don't
comment|// free persistent RAM here
return|return
literal|false
return|;
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
if|if
condition|(
name|tvx
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|tvx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{       }
name|tvx
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|tvd
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|tvd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{       }
name|tvd
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|tvf
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|tvf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{       }
name|tvf
operator|=
literal|null
expr_stmt|;
block|}
name|lastDocID
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|free
specifier|synchronized
name|void
name|free
parameter_list|(
name|PerDoc
name|doc
parameter_list|)
block|{
assert|assert
name|freeCount
operator|<
name|docFreeList
operator|.
name|length
assert|;
name|docFreeList
index|[
name|freeCount
operator|++
index|]
operator|=
name|doc
expr_stmt|;
block|}
DECL|class|PerDoc
class|class
name|PerDoc
extends|extends
name|DocumentsWriter
operator|.
name|DocWriter
block|{
comment|// TODO: use something more memory efficient; for small
comment|// docs the 1024 buffer size of RAMOutputStream wastes alot
DECL|field|tvf
name|RAMOutputStream
name|tvf
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|numVectorFields
name|int
name|numVectorFields
decl_stmt|;
DECL|field|fieldNumbers
name|int
index|[]
name|fieldNumbers
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
DECL|field|fieldPointers
name|long
index|[]
name|fieldPointers
init|=
operator|new
name|long
index|[
literal|1
index|]
decl_stmt|;
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
name|tvf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|numVectorFields
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|abort
name|void
name|abort
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
name|free
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|addField
name|void
name|addField
parameter_list|(
specifier|final
name|int
name|fieldNumber
parameter_list|)
block|{
if|if
condition|(
name|numVectorFields
operator|==
name|fieldNumbers
operator|.
name|length
condition|)
block|{
name|fieldNumbers
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|fieldNumbers
argument_list|)
expr_stmt|;
name|fieldPointers
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|fieldPointers
argument_list|)
expr_stmt|;
block|}
name|fieldNumbers
index|[
name|numVectorFields
index|]
operator|=
name|fieldNumber
expr_stmt|;
name|fieldPointers
index|[
name|numVectorFields
index|]
operator|=
name|tvf
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|numVectorFields
operator|++
expr_stmt|;
block|}
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
return|return
name|tvf
operator|.
name|sizeInBytes
argument_list|()
return|;
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|finishDocument
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PostingList
specifier|static
specifier|final
class|class
name|PostingList
extends|extends
name|RawPostingList
block|{
DECL|field|freq
name|int
name|freq
decl_stmt|;
comment|// How many times this term occurred in the current doc
DECL|field|lastOffset
name|int
name|lastOffset
decl_stmt|;
comment|// Last offset we saw
DECL|field|lastPosition
name|int
name|lastPosition
decl_stmt|;
comment|// Last position where this term occurred
block|}
DECL|method|bytesPerPosting
name|int
name|bytesPerPosting
parameter_list|()
block|{
return|return
name|RawPostingList
operator|.
name|BYTES_SIZE
operator|+
literal|3
operator|*
name|DocumentsWriter
operator|.
name|INT_NUM_BYTE
return|;
block|}
block|}
end_class
end_unit
