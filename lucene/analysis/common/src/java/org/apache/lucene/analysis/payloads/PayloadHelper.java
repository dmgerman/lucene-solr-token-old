begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|payloads
package|;
end_package
begin_comment
comment|/**  * Utility methods for encoding payloads.  *  **/
end_comment
begin_class
DECL|class|PayloadHelper
specifier|public
class|class
name|PayloadHelper
block|{
DECL|method|encodeFloat
specifier|public
specifier|static
name|byte
index|[]
name|encodeFloat
parameter_list|(
name|float
name|payload
parameter_list|)
block|{
return|return
name|encodeFloat
argument_list|(
name|payload
argument_list|,
operator|new
name|byte
index|[
literal|4
index|]
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|encodeFloat
specifier|public
specifier|static
name|byte
index|[]
name|encodeFloat
parameter_list|(
name|float
name|payload
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|encodeInt
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|payload
argument_list|)
argument_list|,
name|data
argument_list|,
name|offset
argument_list|)
return|;
block|}
DECL|method|encodeInt
specifier|public
specifier|static
name|byte
index|[]
name|encodeInt
parameter_list|(
name|int
name|payload
parameter_list|)
block|{
return|return
name|encodeInt
argument_list|(
name|payload
argument_list|,
operator|new
name|byte
index|[
literal|4
index|]
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|encodeInt
specifier|public
specifier|static
name|byte
index|[]
name|encodeInt
parameter_list|(
name|int
name|payload
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|data
index|[
name|offset
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|payload
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|data
index|[
name|offset
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|payload
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|data
index|[
name|offset
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|payload
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|data
index|[
name|offset
operator|+
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
name|payload
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/**    * @see #decodeFloat(byte[], int)    * @see #encodeFloat(float)    * @return the decoded float    */
DECL|method|decodeFloat
specifier|public
specifier|static
name|float
name|decodeFloat
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
return|return
name|decodeFloat
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Decode the payload that was encoded using {@link #encodeFloat(float)}.    * NOTE: the length of the array must be at least offset + 4 long.    * @param bytes The bytes to decode    * @param offset The offset into the array.    * @return The float that was encoded    *    * @see #encodeFloat(float)    */
DECL|method|decodeFloat
specifier|public
specifier|static
specifier|final
name|float
name|decodeFloat
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|decodeInt
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
DECL|method|decodeInt
specifier|public
specifier|static
specifier|final
name|int
name|decodeInt
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
operator|(
operator|(
name|bytes
index|[
name|offset
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
index|[
name|offset
operator|+
literal|3
index|]
operator|&
literal|0xFF
operator|)
return|;
block|}
block|}
end_class
end_unit
