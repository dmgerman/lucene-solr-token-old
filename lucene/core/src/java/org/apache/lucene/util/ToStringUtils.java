begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Helper methods to ease implementing {@link Object#toString()}.  */
end_comment
begin_class
DECL|class|ToStringUtils
specifier|public
specifier|final
class|class
name|ToStringUtils
block|{
DECL|method|ToStringUtils
specifier|private
name|ToStringUtils
parameter_list|()
block|{}
comment|// no instance
DECL|method|byteArray
specifier|public
specifier|static
name|void
name|byteArray
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|byte
index|[]
name|bytes
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"b["
argument_list|)
operator|.
name|append
argument_list|(
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|"]="
argument_list|)
operator|.
name|append
argument_list|(
name|bytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|bytes
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|HEX
specifier|private
specifier|final
specifier|static
name|char
index|[]
name|HEX
init|=
literal|"0123456789abcdef"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
DECL|method|longHex
specifier|public
specifier|static
name|String
name|longHex
parameter_list|(
name|long
name|x
parameter_list|)
block|{
name|char
index|[]
name|asHex
init|=
operator|new
name|char
index|[
literal|16
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|16
init|;
operator|--
name|i
operator|>=
literal|0
condition|;
name|x
operator|>>>=
literal|4
control|)
block|{
name|asHex
index|[
name|i
index|]
operator|=
name|HEX
index|[
operator|(
name|int
operator|)
name|x
operator|&
literal|0x0F
index|]
expr_stmt|;
block|}
return|return
literal|"0x"
operator|+
operator|new
name|String
argument_list|(
name|asHex
argument_list|)
return|;
block|}
block|}
end_class
end_unit
