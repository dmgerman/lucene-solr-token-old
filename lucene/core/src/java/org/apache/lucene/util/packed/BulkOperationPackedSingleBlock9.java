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
begin_comment
comment|/**  * Efficient sequential read/write of packed integers.  */
end_comment
begin_class
DECL|class|BulkOperationPackedSingleBlock9
specifier|final
class|class
name|BulkOperationPackedSingleBlock9
extends|extends
name|BulkOperation
block|{
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
DECL|method|valueCount
specifier|public
name|int
name|valueCount
parameter_list|()
block|{
return|return
literal|7
return|;
block|}
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
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|block
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|block
operator|&
literal|511L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|9
operator|)
operator|&
literal|511L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|18
operator|)
operator|&
literal|511L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|27
operator|)
operator|&
literal|511L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|36
operator|)
operator|&
literal|511L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
operator|(
name|block
operator|>>>
literal|45
operator|)
operator|&
literal|511L
argument_list|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|block
operator|>>>
literal|54
argument_list|)
expr_stmt|;
block|}
block|}
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
assert|assert
name|blocksOffset
operator|+
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|byte7
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte6
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte5
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte4
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte3
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte2
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte1
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte0
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte0
operator||
operator|(
operator|(
name|byte1
operator|&
literal|1
operator|)
operator|<<
literal|8
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte1
operator|>>>
literal|1
operator|)
operator||
operator|(
operator|(
name|byte2
operator|&
literal|3
operator|)
operator|<<
literal|7
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte2
operator|>>>
literal|2
operator|)
operator||
operator|(
operator|(
name|byte3
operator|&
literal|7
operator|)
operator|<<
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte3
operator|>>>
literal|3
operator|)
operator||
operator|(
operator|(
name|byte4
operator|&
literal|15
operator|)
operator|<<
literal|5
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte4
operator|>>>
literal|4
operator|)
operator||
operator|(
operator|(
name|byte5
operator|&
literal|31
operator|)
operator|<<
literal|4
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte5
operator|>>>
literal|5
operator|)
operator||
operator|(
operator|(
name|byte6
operator|&
literal|63
operator|)
operator|<<
literal|3
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte6
operator|>>>
literal|6
operator|)
operator||
operator|(
operator|(
name|byte7
operator|&
literal|127
operator|)
operator|<<
literal|2
operator|)
expr_stmt|;
block|}
block|}
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
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|block
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block
operator|&
literal|511L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|9
operator|)
operator|&
literal|511L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|18
operator|)
operator|&
literal|511L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|27
operator|)
operator|&
literal|511L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|36
operator|)
operator|&
literal|511L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|block
operator|>>>
literal|45
operator|)
operator|&
literal|511L
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|block
operator|>>>
literal|54
expr_stmt|;
block|}
block|}
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
assert|assert
name|blocksOffset
operator|+
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|byte7
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte6
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte5
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte4
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte3
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte2
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte1
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|byte0
init|=
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|&
literal|0xFF
decl_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
name|byte0
operator||
operator|(
operator|(
name|byte1
operator|&
literal|1
operator|)
operator|<<
literal|8
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte1
operator|>>>
literal|1
operator|)
operator||
operator|(
operator|(
name|byte2
operator|&
literal|3
operator|)
operator|<<
literal|7
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte2
operator|>>>
literal|2
operator|)
operator||
operator|(
operator|(
name|byte3
operator|&
literal|7
operator|)
operator|<<
literal|6
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte3
operator|>>>
literal|3
operator|)
operator||
operator|(
operator|(
name|byte4
operator|&
literal|15
operator|)
operator|<<
literal|5
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte4
operator|>>>
literal|4
operator|)
operator||
operator|(
operator|(
name|byte5
operator|&
literal|31
operator|)
operator|<<
literal|4
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte5
operator|>>>
literal|5
operator|)
operator||
operator|(
operator|(
name|byte6
operator|&
literal|63
operator|)
operator|<<
literal|3
operator|)
expr_stmt|;
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|=
operator|(
name|byte6
operator|>>>
literal|6
operator|)
operator||
operator|(
operator|(
name|byte7
operator|&
literal|127
operator|)
operator|<<
literal|2
operator|)
expr_stmt|;
block|}
block|}
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|int
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
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|9
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|18
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|27
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|36
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|45
operator|)
operator||
operator|(
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|&
literal|0xffffffffL
operator|)
operator|<<
literal|54
operator|)
expr_stmt|;
block|}
block|}
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
assert|assert
name|blocksOffset
operator|+
name|iterations
operator|*
name|blockCount
argument_list|()
operator|<=
name|blocks
operator|.
name|length
assert|;
assert|assert
name|valuesOffset
operator|+
name|iterations
operator|*
name|valueCount
argument_list|()
operator|<=
name|values
operator|.
name|length
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iterations
condition|;
operator|++
name|i
control|)
block|{
name|blocks
index|[
name|blocksOffset
operator|++
index|]
operator|=
name|values
index|[
name|valuesOffset
operator|++
index|]
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|9
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|18
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|27
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|36
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|45
operator|)
operator||
operator|(
name|values
index|[
name|valuesOffset
operator|++
index|]
operator|<<
literal|54
operator|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
