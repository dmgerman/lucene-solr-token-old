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
begin_class
DECL|class|IntBlockPool
specifier|final
class|class
name|IntBlockPool
block|{
DECL|field|buffers
specifier|public
name|int
index|[]
index|[]
name|buffers
init|=
operator|new
name|int
index|[
literal|10
index|]
index|[]
decl_stmt|;
DECL|field|bufferUpto
name|int
name|bufferUpto
init|=
operator|-
literal|1
decl_stmt|;
comment|// Which buffer we are upto
DECL|field|intUpto
specifier|public
name|int
name|intUpto
init|=
name|DocumentsWriter
operator|.
name|INT_BLOCK_SIZE
decl_stmt|;
comment|// Where we are in head buffer
DECL|field|buffer
specifier|public
name|int
index|[]
name|buffer
decl_stmt|;
comment|// Current head buffer
DECL|field|intOffset
specifier|public
name|int
name|intOffset
init|=
operator|-
name|DocumentsWriter
operator|.
name|INT_BLOCK_SIZE
decl_stmt|;
comment|// Current head offset
DECL|field|docWriter
specifier|final
specifier|private
name|DocumentsWriter
name|docWriter
decl_stmt|;
DECL|field|trackAllocations
specifier|final
name|boolean
name|trackAllocations
decl_stmt|;
DECL|method|IntBlockPool
specifier|public
name|IntBlockPool
parameter_list|(
name|DocumentsWriter
name|docWriter
parameter_list|,
name|boolean
name|trackAllocations
parameter_list|)
block|{
name|this
operator|.
name|docWriter
operator|=
name|docWriter
expr_stmt|;
name|this
operator|.
name|trackAllocations
operator|=
name|trackAllocations
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
name|bufferUpto
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|bufferUpto
operator|>
literal|0
condition|)
comment|// Recycle all but the first buffer
name|docWriter
operator|.
name|recycleIntBlocks
argument_list|(
name|buffers
argument_list|,
literal|1
argument_list|,
literal|1
operator|+
name|bufferUpto
argument_list|)
expr_stmt|;
comment|// Reuse first buffer
name|bufferUpto
operator|=
literal|0
expr_stmt|;
name|intUpto
operator|=
literal|0
expr_stmt|;
name|intOffset
operator|=
literal|0
expr_stmt|;
name|buffer
operator|=
name|buffers
index|[
literal|0
index|]
expr_stmt|;
block|}
block|}
DECL|method|nextBuffer
specifier|public
name|void
name|nextBuffer
parameter_list|()
block|{
if|if
condition|(
literal|1
operator|+
name|bufferUpto
operator|==
name|buffers
operator|.
name|length
condition|)
block|{
name|int
index|[]
index|[]
name|newBuffers
init|=
operator|new
name|int
index|[
call|(
name|int
call|)
argument_list|(
name|buffers
operator|.
name|length
operator|*
literal|1.5
argument_list|)
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffers
argument_list|,
literal|0
argument_list|,
name|newBuffers
argument_list|,
literal|0
argument_list|,
name|buffers
operator|.
name|length
argument_list|)
expr_stmt|;
name|buffers
operator|=
name|newBuffers
expr_stmt|;
block|}
name|buffer
operator|=
name|buffers
index|[
literal|1
operator|+
name|bufferUpto
index|]
operator|=
name|docWriter
operator|.
name|getIntBlock
argument_list|(
name|trackAllocations
argument_list|)
expr_stmt|;
name|bufferUpto
operator|++
expr_stmt|;
name|intUpto
operator|=
literal|0
expr_stmt|;
name|intOffset
operator|+=
name|DocumentsWriter
operator|.
name|INT_BLOCK_SIZE
expr_stmt|;
block|}
block|}
end_class
end_unit
