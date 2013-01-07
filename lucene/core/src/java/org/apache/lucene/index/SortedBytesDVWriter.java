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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|SimpleDVConsumer
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ByteBlockPool
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
name|BytesRefHash
operator|.
name|DirectBytesStartArray
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
name|BytesRefHash
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
name|Counter
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
name|RamUsageEstimator
import|;
end_import
begin_comment
comment|/** Buffers up pending byte[] per doc, deref and sorting via  *  int ord, then flushes when segment flushes. */
end_comment
begin_comment
comment|// nocommit name?
end_comment
begin_comment
comment|// nocommit make this a consumer in the chain?
end_comment
begin_class
DECL|class|SortedBytesDVWriter
class|class
name|SortedBytesDVWriter
extends|extends
name|DocValuesWriter
block|{
DECL|field|hash
specifier|final
name|BytesRefHash
name|hash
decl_stmt|;
DECL|field|pending
specifier|private
name|int
index|[]
name|pending
init|=
operator|new
name|int
index|[
name|DEFAULT_PENDING_SIZE
index|]
decl_stmt|;
DECL|field|pendingIndex
specifier|private
name|int
name|pendingIndex
init|=
literal|0
decl_stmt|;
DECL|field|iwBytesUsed
specifier|private
specifier|final
name|Counter
name|iwBytesUsed
decl_stmt|;
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|BytesRef
name|EMPTY
init|=
operator|new
name|BytesRef
argument_list|(
name|BytesRef
operator|.
name|EMPTY_BYTES
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_PENDING_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_PENDING_SIZE
init|=
literal|16
decl_stmt|;
DECL|method|SortedBytesDVWriter
specifier|public
name|SortedBytesDVWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|Counter
name|iwBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|iwBytesUsed
operator|=
name|iwBytesUsed
expr_stmt|;
name|hash
operator|=
operator|new
name|BytesRefHash
argument_list|(
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|ByteBlockPool
operator|.
name|DirectTrackingAllocator
argument_list|(
name|iwBytesUsed
argument_list|)
argument_list|)
argument_list|,
name|BytesRefHash
operator|.
name|DEFAULT_CAPACITY
argument_list|,
operator|new
name|DirectBytesStartArray
argument_list|(
name|BytesRefHash
operator|.
name|DEFAULT_CAPACITY
argument_list|,
name|iwBytesUsed
argument_list|)
argument_list|)
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
name|DEFAULT_PENDING_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|addValue
specifier|public
name|void
name|addValue
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
name|pendingIndex
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValuesField \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" appears more than once in this document (only one value is allowed per field)"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|// nocommit improve message
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null sortedValue not allowed (field="
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|")"
argument_list|)
throw|;
block|}
comment|// Fill in any holes:
while|while
condition|(
name|pendingIndex
operator|<
name|docID
condition|)
block|{
name|addOneValue
argument_list|(
name|EMPTY
argument_list|)
expr_stmt|;
block|}
name|addOneValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
if|if
condition|(
name|pendingIndex
operator|<
name|maxDoc
condition|)
block|{
name|addOneValue
argument_list|(
name|EMPTY
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addOneValue
specifier|private
name|void
name|addOneValue
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
name|int
name|ord
init|=
name|hash
operator|.
name|add
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|ord
operator|=
operator|-
name|ord
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|pendingIndex
operator|<=
name|pending
operator|.
name|length
condition|)
block|{
name|int
name|pendingLen
init|=
name|pending
operator|.
name|length
decl_stmt|;
name|pending
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|pending
argument_list|,
name|pendingIndex
operator|+
literal|1
argument_list|)
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|pending
operator|.
name|length
operator|-
name|pendingLen
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
name|pending
index|[
name|pendingIndex
operator|++
index|]
operator|=
name|ord
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|SimpleDVConsumer
name|dvConsumer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|emptyOrd
decl_stmt|;
if|if
condition|(
name|pendingIndex
operator|<
name|maxDoc
condition|)
block|{
comment|// Make sure we added EMPTY value before sorting:
name|int
name|ord
init|=
name|hash
operator|.
name|add
argument_list|(
name|EMPTY
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
name|emptyOrd
operator|=
operator|-
name|ord
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|emptyOrd
operator|=
name|ord
expr_stmt|;
block|}
block|}
else|else
block|{
name|emptyOrd
operator|=
operator|-
literal|1
expr_stmt|;
block|}
specifier|final
name|int
name|valueCount
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|sortedValues
init|=
name|hash
operator|.
name|sort
argument_list|(
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|sortedValueRamUsage
init|=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
name|valueCount
decl_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|sortedValueRamUsage
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
name|ordMap
init|=
operator|new
name|int
index|[
name|valueCount
index|]
decl_stmt|;
for|for
control|(
name|int
name|ord
init|=
literal|0
init|;
name|ord
operator|<
name|valueCount
condition|;
name|ord
operator|++
control|)
block|{
name|ordMap
index|[
name|sortedValues
index|[
name|ord
index|]
index|]
operator|=
name|ord
expr_stmt|;
block|}
specifier|final
name|int
name|bufferedDocCount
init|=
name|pendingIndex
decl_stmt|;
name|dvConsumer
operator|.
name|addSortedField
argument_list|(
name|fieldInfo
argument_list|,
comment|// ord -> value
operator|new
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
name|int
name|ordUpto
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|ordUpto
operator|<
name|valueCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
name|hash
operator|.
name|get
argument_list|(
name|sortedValues
index|[
name|ordUpto
index|]
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|ordUpto
operator|++
expr_stmt|;
return|return
name|scratch
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|,
comment|// doc -> ord
operator|new
name|Iterable
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Number
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
name|int
name|docUpto
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|docUpto
operator|<
name|maxDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|next
parameter_list|()
block|{
name|int
name|ord
decl_stmt|;
if|if
condition|(
name|docUpto
operator|<
name|bufferedDocCount
condition|)
block|{
name|ord
operator|=
name|pending
index|[
name|docUpto
index|]
expr_stmt|;
block|}
else|else
block|{
name|ord
operator|=
name|emptyOrd
expr_stmt|;
block|}
name|docUpto
operator|++
expr_stmt|;
comment|// nocommit make
comment|// resuable Number?
return|return
name|ordMap
index|[
name|ord
index|]
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|sortedValueRamUsage
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|pending
operator|.
name|length
operator|-
name|DEFAULT_PENDING_SIZE
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
name|pending
operator|=
name|ArrayUtil
operator|.
name|shrink
argument_list|(
name|pending
argument_list|,
name|DEFAULT_PENDING_SIZE
argument_list|)
expr_stmt|;
name|pendingIndex
operator|=
literal|0
expr_stmt|;
name|hash
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
