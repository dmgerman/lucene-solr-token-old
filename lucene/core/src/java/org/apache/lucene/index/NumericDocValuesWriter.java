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
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|DocValuesConsumer
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
name|OpenBitSet
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
name|packed
operator|.
name|AppendingDeltaPackedLongBuffer
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
name|packed
operator|.
name|PackedInts
import|;
end_import
begin_comment
comment|/** Buffers up pending long per doc, then flushes when  *  segment flushes. */
end_comment
begin_class
DECL|class|NumericDocValuesWriter
class|class
name|NumericDocValuesWriter
extends|extends
name|DocValuesWriter
block|{
DECL|field|MISSING
specifier|private
specifier|final
specifier|static
name|long
name|MISSING
init|=
literal|0L
decl_stmt|;
DECL|field|pending
specifier|private
name|AppendingDeltaPackedLongBuffer
name|pending
decl_stmt|;
DECL|field|iwBytesUsed
specifier|private
specifier|final
name|Counter
name|iwBytesUsed
decl_stmt|;
DECL|field|bytesUsed
specifier|private
name|long
name|bytesUsed
decl_stmt|;
DECL|field|docsWithField
specifier|private
specifier|final
name|OpenBitSet
name|docsWithField
decl_stmt|;
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|trackDocsWithField
specifier|private
specifier|final
name|boolean
name|trackDocsWithField
decl_stmt|;
DECL|method|NumericDocValuesWriter
specifier|public
name|NumericDocValuesWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|Counter
name|iwBytesUsed
parameter_list|,
name|boolean
name|trackDocsWithField
parameter_list|)
block|{
name|pending
operator|=
operator|new
name|AppendingDeltaPackedLongBuffer
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
expr_stmt|;
name|docsWithField
operator|=
operator|new
name|OpenBitSet
argument_list|()
expr_stmt|;
name|bytesUsed
operator|=
name|pending
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|docsWithFieldBytesUsed
argument_list|()
expr_stmt|;
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
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|bytesUsed
argument_list|)
expr_stmt|;
name|this
operator|.
name|trackDocsWithField
operator|=
name|trackDocsWithField
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
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
name|pending
operator|.
name|size
argument_list|()
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
comment|// Fill in any holes:
for|for
control|(
name|int
name|i
init|=
operator|(
name|int
operator|)
name|pending
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|docID
condition|;
operator|++
name|i
control|)
block|{
name|pending
operator|.
name|add
argument_list|(
name|MISSING
argument_list|)
expr_stmt|;
block|}
name|pending
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|trackDocsWithField
condition|)
block|{
name|docsWithField
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
name|updateBytesUsed
argument_list|()
expr_stmt|;
block|}
DECL|method|docsWithFieldBytesUsed
specifier|private
name|long
name|docsWithFieldBytesUsed
parameter_list|()
block|{
comment|// size of the long[] + some overhead
return|return
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|docsWithField
operator|.
name|getBits
argument_list|()
argument_list|)
operator|+
literal|64
return|;
block|}
DECL|method|updateBytesUsed
specifier|private
name|void
name|updateBytesUsed
parameter_list|()
block|{
specifier|final
name|long
name|newBytesUsed
init|=
name|pending
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|docsWithFieldBytesUsed
argument_list|()
decl_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|newBytesUsed
operator|-
name|bytesUsed
argument_list|)
expr_stmt|;
name|bytesUsed
operator|=
name|newBytesUsed
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
block|{   }
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
name|DocValuesConsumer
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
name|dvConsumer
operator|.
name|addNumericField
argument_list|(
name|fieldInfo
argument_list|,
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
name|NumericIterator
argument_list|(
name|maxDoc
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{   }
comment|// iterates over the values we have in ram
DECL|class|NumericIterator
specifier|private
class|class
name|NumericIterator
implements|implements
name|Iterator
argument_list|<
name|Number
argument_list|>
block|{
DECL|field|iter
specifier|final
name|AppendingDeltaPackedLongBuffer
operator|.
name|Iterator
name|iter
init|=
name|pending
operator|.
name|iterator
argument_list|()
decl_stmt|;
DECL|field|size
specifier|final
name|int
name|size
init|=
operator|(
name|int
operator|)
name|pending
operator|.
name|size
argument_list|()
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|method|NumericIterator
name|NumericIterator
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|upto
operator|<
name|maxDoc
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Number
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|Long
name|value
decl_stmt|;
if|if
condition|(
name|upto
operator|<
name|size
condition|)
block|{
name|long
name|v
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|trackDocsWithField
operator|||
name|docsWithField
operator|.
name|get
argument_list|(
name|upto
argument_list|)
condition|)
block|{
name|value
operator|=
name|v
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|value
operator|=
name|trackDocsWithField
condition|?
literal|null
else|:
name|MISSING
expr_stmt|;
block|}
name|upto
operator|++
expr_stmt|;
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|remove
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
block|}
block|}
end_class
end_unit
