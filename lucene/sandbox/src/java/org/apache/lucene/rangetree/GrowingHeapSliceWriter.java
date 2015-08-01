begin_unit
begin_package
DECL|package|org.apache.lucene.rangetree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|rangetree
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
DECL|class|GrowingHeapSliceWriter
specifier|final
class|class
name|GrowingHeapSliceWriter
implements|implements
name|SliceWriter
block|{
DECL|field|values
name|long
index|[]
name|values
decl_stmt|;
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
DECL|field|maxSize
specifier|final
name|int
name|maxSize
decl_stmt|;
DECL|method|GrowingHeapSliceWriter
specifier|public
name|GrowingHeapSliceWriter
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|values
operator|=
operator|new
name|long
index|[
literal|16
index|]
expr_stmt|;
name|docIDs
operator|=
operator|new
name|int
index|[
literal|16
index|]
expr_stmt|;
name|ords
operator|=
operator|new
name|long
index|[
literal|16
index|]
expr_stmt|;
name|this
operator|.
name|maxSize
operator|=
name|maxSize
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
name|long
name|value
parameter_list|,
name|long
name|ord
parameter_list|,
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|ord
operator|==
name|nextWrite
assert|;
if|if
condition|(
name|values
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
name|values
operator|=
name|growExact
argument_list|(
name|values
argument_list|,
name|nextSize
argument_list|)
expr_stmt|;
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
name|values
index|[
name|nextWrite
index|]
operator|=
name|value
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
name|SliceReader
name|getReader
parameter_list|(
name|long
name|start
parameter_list|)
block|{
return|return
operator|new
name|HeapSliceReader
argument_list|(
name|values
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
block|{   }
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
literal|"GrowingHeapSliceWriter(count="
operator|+
name|nextWrite
operator|+
literal|" alloc="
operator|+
name|values
operator|.
name|length
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
