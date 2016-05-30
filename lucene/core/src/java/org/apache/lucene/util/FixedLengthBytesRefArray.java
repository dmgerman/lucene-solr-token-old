begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_comment
comment|/**  * Just like {@link BytesRefArray} except all values have the same length.  *   *<b>Note: This class is not Thread-Safe!</b>  *   * @lucene.internal  * @lucene.experimental  */
end_comment
begin_class
DECL|class|FixedLengthBytesRefArray
specifier|final
class|class
name|FixedLengthBytesRefArray
implements|implements
name|SortableBytesRefArray
block|{
DECL|field|valueLength
specifier|private
specifier|final
name|int
name|valueLength
decl_stmt|;
DECL|field|valuesPerBlock
specifier|private
specifier|final
name|int
name|valuesPerBlock
decl_stmt|;
comment|/** How many values have been appended */
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
comment|/** How many blocks are used */
DECL|field|currentBlock
specifier|private
name|int
name|currentBlock
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|nextEntry
specifier|private
name|int
name|nextEntry
decl_stmt|;
DECL|field|blocks
specifier|private
name|byte
index|[]
index|[]
name|blocks
decl_stmt|;
comment|/**    * Creates a new {@link BytesRefArray} with a counter to track allocated bytes    */
DECL|method|FixedLengthBytesRefArray
specifier|public
name|FixedLengthBytesRefArray
parameter_list|(
name|int
name|valueLength
parameter_list|)
block|{
name|this
operator|.
name|valueLength
operator|=
name|valueLength
expr_stmt|;
comment|// ~32K per page, unless each value is> 32K:
name|valuesPerBlock
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
literal|32768
operator|/
name|valueLength
argument_list|)
expr_stmt|;
name|nextEntry
operator|=
name|valuesPerBlock
expr_stmt|;
name|blocks
operator|=
operator|new
name|byte
index|[
literal|0
index|]
index|[]
expr_stmt|;
block|}
comment|/**    * Clears this {@link BytesRefArray}    */
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|size
operator|=
literal|0
expr_stmt|;
name|blocks
operator|=
operator|new
name|byte
index|[
literal|0
index|]
index|[]
expr_stmt|;
name|currentBlock
operator|=
operator|-
literal|1
expr_stmt|;
name|nextEntry
operator|=
name|valuesPerBlock
expr_stmt|;
block|}
comment|/**    * Appends a copy of the given {@link BytesRef} to this {@link BytesRefArray}.    * @param bytes the bytes to append    * @return the index of the appended bytes    */
annotation|@
name|Override
DECL|method|append
specifier|public
name|int
name|append
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|!=
name|valueLength
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value length is "
operator|+
name|bytes
operator|.
name|length
operator|+
literal|" but is supposed to always be "
operator|+
name|valueLength
argument_list|)
throw|;
block|}
if|if
condition|(
name|nextEntry
operator|==
name|valuesPerBlock
condition|)
block|{
name|currentBlock
operator|++
expr_stmt|;
if|if
condition|(
name|currentBlock
operator|==
name|blocks
operator|.
name|length
condition|)
block|{
name|int
name|size
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|currentBlock
operator|+
literal|1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|next
init|=
operator|new
name|byte
index|[
name|size
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blocks
argument_list|,
literal|0
argument_list|,
name|next
argument_list|,
literal|0
argument_list|,
name|blocks
operator|.
name|length
argument_list|)
expr_stmt|;
name|blocks
operator|=
name|next
expr_stmt|;
block|}
name|blocks
index|[
name|currentBlock
index|]
operator|=
operator|new
name|byte
index|[
name|valuesPerBlock
operator|*
name|valueLength
index|]
expr_stmt|;
name|nextEntry
operator|=
literal|0
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|blocks
index|[
name|currentBlock
index|]
argument_list|,
name|nextEntry
operator|*
name|valueLength
argument_list|,
name|valueLength
argument_list|)
expr_stmt|;
name|nextEntry
operator|++
expr_stmt|;
return|return
name|size
operator|++
return|;
block|}
comment|/**    * Returns the current size of this {@link FixedLengthBytesRefArray}    * @return the current size of this {@link FixedLengthBytesRefArray}    */
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|sort
specifier|private
name|int
index|[]
name|sort
parameter_list|(
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|orderedEntries
init|=
operator|new
name|int
index|[
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|orderedEntries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|orderedEntries
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
if|if
condition|(
name|comp
operator|instanceof
name|BytesRefComparator
condition|)
block|{
name|BytesRefComparator
name|bComp
init|=
operator|(
name|BytesRefComparator
operator|)
name|comp
decl_stmt|;
operator|new
name|MSBRadixSorter
argument_list|(
name|bComp
operator|.
name|comparedBytesCount
argument_list|)
block|{
name|BytesRef
name|scratch
decl_stmt|;
block|{
name|scratch
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|scratch
operator|.
name|length
operator|=
name|valueLength
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|int
name|o
init|=
name|orderedEntries
index|[
name|i
index|]
decl_stmt|;
name|orderedEntries
index|[
name|i
index|]
operator|=
name|orderedEntries
index|[
name|j
index|]
expr_stmt|;
name|orderedEntries
index|[
name|j
index|]
operator|=
name|o
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|byteAt
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|k
parameter_list|)
block|{
name|int
name|index1
init|=
name|orderedEntries
index|[
name|i
index|]
decl_stmt|;
name|scratch
operator|.
name|bytes
operator|=
name|blocks
index|[
name|index1
operator|/
name|valuesPerBlock
index|]
expr_stmt|;
name|scratch
operator|.
name|offset
operator|=
operator|(
name|index1
operator|%
name|valuesPerBlock
operator|)
operator|*
name|valueLength
expr_stmt|;
return|return
name|bComp
operator|.
name|byteAt
argument_list|(
name|scratch
argument_list|,
name|k
argument_list|)
return|;
block|}
block|}
operator|.
name|sort
argument_list|(
literal|0
argument_list|,
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|orderedEntries
return|;
block|}
specifier|final
name|BytesRef
name|pivot
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|scratch1
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|scratch2
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|pivot
operator|.
name|length
operator|=
name|valueLength
expr_stmt|;
name|scratch1
operator|.
name|length
operator|=
name|valueLength
expr_stmt|;
name|scratch2
operator|.
name|length
operator|=
name|valueLength
expr_stmt|;
operator|new
name|IntroSorter
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|int
name|o
init|=
name|orderedEntries
index|[
name|i
index|]
decl_stmt|;
name|orderedEntries
index|[
name|i
index|]
operator|=
name|orderedEntries
index|[
name|j
index|]
expr_stmt|;
name|orderedEntries
index|[
name|j
index|]
operator|=
name|o
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|int
name|index1
init|=
name|orderedEntries
index|[
name|i
index|]
decl_stmt|;
name|scratch1
operator|.
name|bytes
operator|=
name|blocks
index|[
name|index1
operator|/
name|valuesPerBlock
index|]
expr_stmt|;
name|scratch1
operator|.
name|offset
operator|=
operator|(
name|index1
operator|%
name|valuesPerBlock
operator|)
operator|*
name|valueLength
expr_stmt|;
name|int
name|index2
init|=
name|orderedEntries
index|[
name|j
index|]
decl_stmt|;
name|scratch2
operator|.
name|bytes
operator|=
name|blocks
index|[
name|index2
operator|/
name|valuesPerBlock
index|]
expr_stmt|;
name|scratch2
operator|.
name|offset
operator|=
operator|(
name|index2
operator|%
name|valuesPerBlock
operator|)
operator|*
name|valueLength
expr_stmt|;
return|return
name|comp
operator|.
name|compare
argument_list|(
name|scratch1
argument_list|,
name|scratch2
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|int
name|index
init|=
name|orderedEntries
index|[
name|i
index|]
decl_stmt|;
name|pivot
operator|.
name|bytes
operator|=
name|blocks
index|[
name|index
operator|/
name|valuesPerBlock
index|]
expr_stmt|;
name|pivot
operator|.
name|offset
operator|=
operator|(
name|index
operator|%
name|valuesPerBlock
operator|)
operator|*
name|valueLength
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
name|orderedEntries
index|[
name|j
index|]
decl_stmt|;
name|scratch2
operator|.
name|bytes
operator|=
name|blocks
index|[
name|index
operator|/
name|valuesPerBlock
index|]
expr_stmt|;
name|scratch2
operator|.
name|offset
operator|=
operator|(
name|index
operator|%
name|valuesPerBlock
operator|)
operator|*
name|valueLength
expr_stmt|;
return|return
name|comp
operator|.
name|compare
argument_list|(
name|pivot
argument_list|,
name|scratch2
argument_list|)
return|;
block|}
block|}
operator|.
name|sort
argument_list|(
literal|0
argument_list|,
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|orderedEntries
return|;
block|}
comment|/**    *<p>    * Returns a {@link BytesRefIterator} with point in time semantics. The    * iterator provides access to all so far appended {@link BytesRef} instances.    *</p>    *<p>    * The iterator will iterate the byte values in the order specified by the comparator.    *</p>    *<p>    * This is a non-destructive operation.    *</p>    */
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|BytesRefIterator
name|iterator
parameter_list|(
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
specifier|final
name|BytesRef
name|result
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|result
operator|.
name|length
operator|=
name|valueLength
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|indices
init|=
name|sort
argument_list|(
name|comp
argument_list|)
decl_stmt|;
return|return
operator|new
name|BytesRefIterator
argument_list|()
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|pos
operator|<
name|size
condition|)
block|{
name|int
name|index
init|=
name|indices
index|[
name|pos
index|]
decl_stmt|;
name|pos
operator|++
expr_stmt|;
name|result
operator|.
name|bytes
operator|=
name|blocks
index|[
name|index
operator|/
name|valuesPerBlock
index|]
expr_stmt|;
name|result
operator|.
name|offset
operator|=
operator|(
name|index
operator|%
name|valuesPerBlock
operator|)
operator|*
name|valueLength
expr_stmt|;
return|return
name|result
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
