begin_unit
begin_comment
comment|// This file has been automatically generated, DO NOT EDIT
end_comment
begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|LongBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import
begin_comment
comment|/**  * Efficient sequential read/write of packed integers.  */
end_comment
begin_class
DECL|class|BulkOperationPacked64
specifier|final
class|class
name|BulkOperationPacked64
extends|extends
name|BulkOperation
block|{
annotation|@
name|Override
DECL|method|blockCount
specifier|public
name|int
name|blockCount
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|valueCount
specifier|public
name|int
name|valueCount
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|blocks
argument_list|,
name|blocksOffset
argument_list|,
name|values
argument_list|,
name|valuesOffset
argument_list|,
name|valueCount
argument_list|()
operator|*
name|iterations
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|decode
specifier|public
name|void
name|decode
parameter_list|(
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|LongBuffer
operator|.
name|wrap
argument_list|(
name|values
argument_list|,
name|valuesOffset
argument_list|,
name|iterations
operator|*
name|valueCount
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|blocks
argument_list|,
name|blocksOffset
argument_list|,
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
argument_list|)
operator|.
name|asLongBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
name|valuesOffset
argument_list|,
name|blocks
argument_list|,
name|blocksOffset
argument_list|,
name|valueCount
argument_list|()
operator|*
name|iterations
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
