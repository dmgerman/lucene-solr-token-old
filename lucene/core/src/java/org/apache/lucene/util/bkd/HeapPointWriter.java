begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util.bkd
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|bkd
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|RamUsageEstimator
import|;
end_import
begin_class
DECL|class|HeapPointWriter
specifier|final
class|class
name|HeapPointWriter
implements|implements
name|PointWriter
block|{
DECL|field|docIDs
name|int
index|[]
name|docIDs
decl_stmt|;
DECL|field|ords
name|long
index|[]
name|ords
decl_stmt|;
DECL|field|nextWrite
specifier|private
name|int
name|nextWrite
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
DECL|field|maxSize
specifier|final
name|int
name|maxSize
decl_stmt|;
DECL|field|valuesPerBlock
specifier|final
name|int
name|valuesPerBlock
decl_stmt|;
DECL|field|packedBytesLength
specifier|final
name|int
name|packedBytesLength
decl_stmt|;
comment|// NOTE: can't use ByteBlockPool because we need random-write access when sorting in heap
DECL|field|blocks
specifier|final
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|blocks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|HeapPointWriter
specifier|public
name|HeapPointWriter
parameter_list|(
name|int
name|initSize
parameter_list|,
name|int
name|maxSize
parameter_list|,
name|int
name|packedBytesLength
parameter_list|)
block|{
name|docIDs
operator|=
operator|new
name|int
index|[
name|initSize
index|]
expr_stmt|;
name|ords
operator|=
operator|new
name|long
index|[
name|initSize
index|]
expr_stmt|;
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
name|this
operator|.
name|packedBytesLength
operator|=
name|packedBytesLength
expr_stmt|;
comment|// 4K per page, unless each value is> 4K:
name|valuesPerBlock
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
literal|4096
operator|/
name|packedBytesLength
argument_list|)
expr_stmt|;
block|}
DECL|method|copyFrom
specifier|public
name|void
name|copyFrom
parameter_list|(
name|HeapPointWriter
name|other
parameter_list|)
block|{
if|if
condition|(
name|docIDs
operator|.
name|length
operator|<
name|other
operator|.
name|nextWrite
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"docIDs.length="
operator|+
name|docIDs
operator|.
name|length
operator|+
literal|" other.nextWrite="
operator|+
name|other
operator|.
name|nextWrite
argument_list|)
throw|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|docIDs
argument_list|,
literal|0
argument_list|,
name|docIDs
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|nextWrite
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|ords
argument_list|,
literal|0
argument_list|,
name|ords
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|nextWrite
argument_list|)
expr_stmt|;
for|for
control|(
name|byte
index|[]
name|block
range|:
name|other
operator|.
name|blocks
control|)
block|{
name|blocks
operator|.
name|add
argument_list|(
name|block
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nextWrite
operator|=
name|other
operator|.
name|nextWrite
expr_stmt|;
block|}
DECL|method|readPackedValue
name|void
name|readPackedValue
parameter_list|(
name|int
name|index
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
block|{
assert|assert
name|bytes
operator|.
name|length
operator|==
name|packedBytesLength
assert|;
name|int
name|block
init|=
name|index
operator|/
name|valuesPerBlock
decl_stmt|;
name|int
name|blockIndex
init|=
name|index
operator|%
name|valuesPerBlock
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|blocks
operator|.
name|get
argument_list|(
name|block
argument_list|)
argument_list|,
name|blockIndex
operator|*
name|packedBytesLength
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|packedBytesLength
argument_list|)
expr_stmt|;
block|}
DECL|method|writePackedValue
name|void
name|writePackedValue
parameter_list|(
name|int
name|index
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
block|{
assert|assert
name|bytes
operator|.
name|length
operator|==
name|packedBytesLength
assert|;
name|int
name|block
init|=
name|index
operator|/
name|valuesPerBlock
decl_stmt|;
name|int
name|blockIndex
init|=
name|index
operator|%
name|valuesPerBlock
decl_stmt|;
comment|//System.out.println("writePackedValue: index=" + index + " bytes.length=" + bytes.length + " block=" + block + " blockIndex=" + blockIndex + " valuesPerBlock=" + valuesPerBlock);
while|while
condition|(
name|blocks
operator|.
name|size
argument_list|()
operator|<=
name|block
condition|)
block|{
comment|// If this is the last block, only allocate as large as necessary for maxSize:
name|int
name|valuesInBlock
init|=
name|Math
operator|.
name|min
argument_list|(
name|valuesPerBlock
argument_list|,
name|maxSize
operator|-
operator|(
name|blocks
operator|.
name|size
argument_list|()
operator|*
name|valuesPerBlock
operator|)
argument_list|)
decl_stmt|;
name|blocks
operator|.
name|add
argument_list|(
operator|new
name|byte
index|[
name|valuesInBlock
operator|*
name|packedBytesLength
index|]
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|blocks
operator|.
name|get
argument_list|(
name|block
argument_list|)
argument_list|,
name|blockIndex
operator|*
name|packedBytesLength
argument_list|,
name|packedBytesLength
argument_list|)
expr_stmt|;
block|}
DECL|method|growExact
specifier|private
name|int
index|[]
name|growExact
parameter_list|(
name|int
index|[]
name|arr
parameter_list|,
name|int
name|size
parameter_list|)
block|{
assert|assert
name|size
operator|>
name|arr
operator|.
name|length
assert|;
name|int
index|[]
name|newArr
init|=
operator|new
name|int
index|[
name|size
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|newArr
argument_list|,
literal|0
argument_list|,
name|arr
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|newArr
return|;
block|}
DECL|method|growExact
specifier|private
name|long
index|[]
name|growExact
parameter_list|(
name|long
index|[]
name|arr
parameter_list|,
name|int
name|size
parameter_list|)
block|{
assert|assert
name|size
operator|>
name|arr
operator|.
name|length
assert|;
name|long
index|[]
name|newArr
init|=
operator|new
name|long
index|[
name|size
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|newArr
argument_list|,
literal|0
argument_list|,
name|arr
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|newArr
return|;
block|}
annotation|@
name|Override
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
name|byte
index|[]
name|packedValue
parameter_list|,
name|long
name|ord
parameter_list|,
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|closed
operator|==
literal|false
assert|;
assert|assert
name|packedValue
operator|.
name|length
operator|==
name|packedBytesLength
assert|;
if|if
condition|(
name|ords
operator|.
name|length
operator|==
name|nextWrite
condition|)
block|{
name|int
name|nextSize
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxSize
argument_list|,
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|nextWrite
operator|+
literal|1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|nextSize
operator|>
name|nextWrite
operator|:
literal|"nextSize="
operator|+
name|nextSize
operator|+
literal|" vs nextWrite="
operator|+
name|nextWrite
assert|;
name|ords
operator|=
name|growExact
argument_list|(
name|ords
argument_list|,
name|nextSize
argument_list|)
expr_stmt|;
name|docIDs
operator|=
name|growExact
argument_list|(
name|docIDs
argument_list|,
name|nextSize
argument_list|)
expr_stmt|;
block|}
name|writePackedValue
argument_list|(
name|nextWrite
argument_list|,
name|packedValue
argument_list|)
expr_stmt|;
name|ords
index|[
name|nextWrite
index|]
operator|=
name|ord
expr_stmt|;
name|docIDs
index|[
name|nextWrite
index|]
operator|=
name|docID
expr_stmt|;
name|nextWrite
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReader
specifier|public
name|PointReader
name|getReader
parameter_list|(
name|long
name|start
parameter_list|)
block|{
return|return
operator|new
name|HeapPointReader
argument_list|(
name|blocks
argument_list|,
name|valuesPerBlock
argument_list|,
name|packedBytesLength
argument_list|,
name|ords
argument_list|,
name|docIDs
argument_list|,
operator|(
name|int
operator|)
name|start
argument_list|,
name|nextWrite
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"HeapPointWriter(count="
operator|+
name|nextWrite
operator|+
literal|" alloc="
operator|+
name|ords
operator|.
name|length
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
