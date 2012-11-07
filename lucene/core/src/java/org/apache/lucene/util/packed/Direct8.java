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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|DataInput
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
name|Arrays
import|;
end_import
begin_comment
comment|/**  * Direct wrapping of 8-bits values to a backing array.  * @lucene.internal  */
end_comment
begin_class
DECL|class|Direct8
specifier|final
class|class
name|Direct8
extends|extends
name|PackedInts
operator|.
name|MutableImpl
block|{
DECL|field|values
specifier|final
name|byte
index|[]
name|values
decl_stmt|;
DECL|method|Direct8
name|Direct8
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|byte
index|[
name|valueCount
index|]
expr_stmt|;
block|}
DECL|method|Direct8
name|Direct8
parameter_list|(
name|int
name|packedIntsVersion
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|int
name|valueCount
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|valueCount
argument_list|)
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|valueCount
argument_list|)
expr_stmt|;
comment|// because packed ints have not always been byte-aligned
specifier|final
name|int
name|remaining
init|=
call|(
name|int
call|)
argument_list|(
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
operator|.
name|byteCount
argument_list|(
name|packedIntsVersion
argument_list|,
name|valueCount
argument_list|,
literal|8
argument_list|)
operator|-
literal|1L
operator|*
name|valueCount
argument_list|)
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
name|remaining
condition|;
operator|++
name|i
control|)
block|{
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
return|return
name|values
index|[
name|index
index|]
operator|&
literal|0xFFL
return|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
specifier|final
name|int
name|index
parameter_list|,
specifier|final
name|long
name|value
parameter_list|)
block|{
name|values
index|[
name|index
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|values
argument_list|)
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
operator|(
name|byte
operator|)
literal|0L
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|int
name|index
parameter_list|,
name|long
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
assert|assert
name|len
operator|>
literal|0
operator|:
literal|"len must be> 0 (got "
operator|+
name|len
operator|+
literal|")"
assert|;
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|valueCount
assert|;
assert|assert
name|off
operator|+
name|len
operator|<=
name|arr
operator|.
name|length
assert|;
specifier|final
name|int
name|gets
init|=
name|Math
operator|.
name|min
argument_list|(
name|valueCount
operator|-
name|index
argument_list|,
name|len
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|index
init|,
name|o
init|=
name|off
init|,
name|end
init|=
name|index
operator|+
name|gets
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
operator|,
operator|++
name|o
control|)
block|{
name|arr
index|[
name|o
index|]
operator|=
name|values
index|[
name|i
index|]
operator|&
literal|0xFFL
expr_stmt|;
block|}
return|return
name|gets
return|;
block|}
DECL|method|set
specifier|public
name|int
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|long
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
assert|assert
name|len
operator|>
literal|0
operator|:
literal|"len must be> 0 (got "
operator|+
name|len
operator|+
literal|")"
assert|;
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|valueCount
assert|;
assert|assert
name|off
operator|+
name|len
operator|<=
name|arr
operator|.
name|length
assert|;
specifier|final
name|int
name|sets
init|=
name|Math
operator|.
name|min
argument_list|(
name|valueCount
operator|-
name|index
argument_list|,
name|len
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|index
init|,
name|o
init|=
name|off
init|,
name|end
init|=
name|index
operator|+
name|sets
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
operator|,
operator|++
name|o
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|arr
index|[
name|o
index|]
expr_stmt|;
block|}
return|return
name|sets
return|;
block|}
annotation|@
name|Override
DECL|method|fill
specifier|public
name|void
name|fill
parameter_list|(
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|,
name|long
name|val
parameter_list|)
block|{
assert|assert
name|val
operator|==
operator|(
name|val
operator|&
literal|0xFFL
operator|)
assert|;
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
name|fromIndex
argument_list|,
name|toIndex
argument_list|,
operator|(
name|byte
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
