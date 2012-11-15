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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|NumericDocValuesConsumer
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
name|packed
operator|.
name|AppendingLongBuffer
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
comment|// nocommit pick numeric or number ... then fix all places ...
end_comment
begin_comment
comment|/** Buffers up pending long per doc, then flushes when  *  segment flushes. */
end_comment
begin_comment
comment|// nocommit name?
end_comment
begin_comment
comment|// nocommit make this a consumer in the chain?
end_comment
begin_class
DECL|class|NumberDVWriter
class|class
name|NumberDVWriter
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
name|AppendingLongBuffer
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
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|minValue
name|long
name|minValue
decl_stmt|;
DECL|field|maxValue
name|long
name|maxValue
decl_stmt|;
DECL|field|anyValues
specifier|private
name|boolean
name|anyValues
decl_stmt|;
DECL|method|NumberDVWriter
specifier|public
name|NumberDVWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|Counter
name|iwBytesUsed
parameter_list|)
block|{
name|pending
operator|=
operator|new
name|AppendingLongBuffer
argument_list|()
expr_stmt|;
name|bytesUsed
operator|=
name|pending
operator|.
name|ramBytesUsed
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
name|mergeValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
comment|// Fill in any holes:
for|for
control|(
name|int
name|i
init|=
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
name|mergeValue
argument_list|(
literal|0
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
name|updateBytesUsed
argument_list|()
expr_stmt|;
comment|//System.out.println("ADD: " + value);
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
DECL|method|mergeValue
specifier|private
name|void
name|mergeValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
name|anyValues
condition|)
block|{
name|anyValues
operator|=
literal|true
expr_stmt|;
name|minValue
operator|=
name|maxValue
operator|=
name|value
expr_stmt|;
block|}
else|else
block|{
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|value
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|value
argument_list|,
name|minValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|,
name|NumericDocValuesConsumer
name|consumer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|bufferedDocCount
init|=
name|pending
operator|.
name|size
argument_list|()
decl_stmt|;
name|AppendingLongBuffer
operator|.
name|Iterator
name|it
init|=
name|pending
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|bufferedDocCount
condition|;
name|docID
operator|++
control|)
block|{
assert|assert
name|it
operator|.
name|hasNext
argument_list|()
assert|;
name|consumer
operator|.
name|add
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
assert|assert
operator|!
name|it
operator|.
name|hasNext
argument_list|()
assert|;
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
for|for
control|(
name|int
name|docID
init|=
name|bufferedDocCount
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
name|consumer
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|reset
argument_list|()
expr_stmt|;
comment|//System.out.println("FLUSH");
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
comment|// nocommit do we really need this...?  can't parent alloc
comment|// a new instance after flush?
DECL|method|reset
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|pending
operator|=
operator|new
name|AppendingLongBuffer
argument_list|()
expr_stmt|;
name|updateBytesUsed
argument_list|()
expr_stmt|;
name|anyValues
operator|=
literal|false
expr_stmt|;
name|minValue
operator|=
name|maxValue
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class
end_unit
