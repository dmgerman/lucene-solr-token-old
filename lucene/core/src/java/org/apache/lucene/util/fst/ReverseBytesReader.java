begin_unit
begin_package
DECL|package|org.apache.lucene.util.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/** Reads in reverse from a single byte[]. */
end_comment
begin_class
DECL|class|ReverseBytesReader
specifier|final
class|class
name|ReverseBytesReader
extends|extends
name|FST
operator|.
name|BytesReader
block|{
DECL|field|bytes
specifier|private
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
decl_stmt|;
DECL|method|ReverseBytesReader
specifier|public
name|ReverseBytesReader
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
block|{
return|return
name|bytes
index|[
name|pos
operator|--
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|b
index|[
name|offset
operator|+
name|i
index|]
operator|=
name|bytes
index|[
name|pos
operator|--
index|]
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|skipBytes
specifier|public
name|void
name|skipBytes
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|pos
operator|-=
name|count
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPosition
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|setPosition
specifier|public
name|void
name|setPosition
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
name|this
operator|.
name|pos
operator|=
operator|(
name|int
operator|)
name|pos
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reversed
specifier|public
name|boolean
name|reversed
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
