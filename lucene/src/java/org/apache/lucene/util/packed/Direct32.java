begin_unit
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexInput
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
comment|/**  * Direct wrapping of 32 bit values to a backing array of ints.  * @lucene.internal  */
end_comment
begin_class
DECL|class|Direct32
specifier|public
class|class
name|Direct32
extends|extends
name|PackedInts
operator|.
name|ReaderImpl
implements|implements
name|PackedInts
operator|.
name|Mutable
block|{
DECL|field|values
specifier|private
name|int
index|[]
name|values
decl_stmt|;
DECL|field|BITS_PER_VALUE
specifier|private
specifier|static
specifier|final
name|int
name|BITS_PER_VALUE
init|=
literal|32
decl_stmt|;
DECL|method|Direct32
specifier|public
name|Direct32
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|BITS_PER_VALUE
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|int
index|[
name|valueCount
index|]
expr_stmt|;
block|}
DECL|method|Direct32
specifier|public
name|Direct32
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|valueCount
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|BITS_PER_VALUE
argument_list|)
expr_stmt|;
name|int
index|[]
name|values
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
name|i
init|=
literal|0
init|;
name|i
operator|<
name|valueCount
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|mod
init|=
name|valueCount
operator|%
literal|2
decl_stmt|;
if|if
condition|(
name|mod
operator|!=
literal|0
condition|)
block|{
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
comment|/**    * Creates an array backed by the given values.    *</p><p>    * Note: The values are used directly, so changes to the given values will    * affect the structure.    * @param values   used as the internal backing array.    */
DECL|method|Direct32
specifier|public
name|Direct32
parameter_list|(
name|int
index|[]
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|values
operator|.
name|length
argument_list|,
name|BITS_PER_VALUE
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
DECL|method|getArray
specifier|public
name|int
index|[]
name|getArray
parameter_list|()
block|{
return|return
name|values
return|;
block|}
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
literal|0xFFFFFFFFL
operator|&
name|values
index|[
name|index
index|]
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
name|int
call|)
argument_list|(
name|value
operator|&
literal|0xFFFFFFFF
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
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|values
operator|.
name|length
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
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
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
