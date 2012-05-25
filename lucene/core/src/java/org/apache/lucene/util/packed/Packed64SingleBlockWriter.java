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
name|store
operator|.
name|DataOutput
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
operator|.
name|Writer
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link Writer} for {@link Packed64SingleBlock} readers.  */
end_comment
begin_class
DECL|class|Packed64SingleBlockWriter
specifier|final
class|class
name|Packed64SingleBlockWriter
extends|extends
name|Writer
block|{
DECL|field|pending
specifier|private
name|long
name|pending
decl_stmt|;
DECL|field|shift
specifier|private
name|int
name|shift
decl_stmt|;
DECL|field|written
specifier|private
name|int
name|written
decl_stmt|;
DECL|method|Packed64SingleBlockWriter
name|Packed64SingleBlockWriter
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|out
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
assert|assert
name|Packed64SingleBlock
operator|.
name|isSupported
argument_list|(
name|bitsPerValue
argument_list|)
operator|:
name|bitsPerValue
operator|+
literal|" is not supported"
assert|;
name|pending
operator|=
literal|0
expr_stmt|;
name|shift
operator|=
literal|0
expr_stmt|;
name|written
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFormat
specifier|protected
name|int
name|getFormat
parameter_list|()
block|{
return|return
name|PackedInts
operator|.
name|PACKED_SINGLE_BLOCK
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|long
name|v
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|v
operator|<=
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|bitsPerValue
argument_list|)
operator|:
literal|"v="
operator|+
name|v
operator|+
literal|" maxValue="
operator|+
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|bitsPerValue
argument_list|)
assert|;
assert|assert
name|v
operator|>=
literal|0
assert|;
if|if
condition|(
name|shift
operator|+
name|bitsPerValue
operator|>
name|Long
operator|.
name|SIZE
condition|)
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|pending
argument_list|)
expr_stmt|;
name|pending
operator|=
literal|0
expr_stmt|;
name|shift
operator|=
literal|0
expr_stmt|;
block|}
name|pending
operator||=
name|v
operator|<<
name|shift
expr_stmt|;
name|shift
operator|+=
name|bitsPerValue
expr_stmt|;
operator|++
name|written
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|written
operator|<
name|valueCount
condition|)
block|{
name|add
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
comment|// Auto flush
block|}
if|if
condition|(
name|shift
operator|>
literal|0
condition|)
block|{
comment|// add was called at least once
name|out
operator|.
name|writeLong
argument_list|(
name|pending
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Packed64SingleBlockWriter(written "
operator|+
name|written
operator|+
literal|"/"
operator|+
name|valueCount
operator|+
literal|" with "
operator|+
name|bitsPerValue
operator|+
literal|" bits/value)"
return|;
block|}
block|}
end_class
end_unit
