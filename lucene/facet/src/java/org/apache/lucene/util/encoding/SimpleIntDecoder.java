begin_unit
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
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
name|java
operator|.
name|io
operator|.
name|StreamCorruptedException
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A simple stream decoder which can decode values encoded with  * {@link SimpleIntEncoder}.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|SimpleIntDecoder
specifier|public
class|class
name|SimpleIntDecoder
extends|extends
name|IntDecoder
block|{
comment|/**    * reusable buffer - allocated only once as this is not a thread-safe object    */
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
annotation|@
name|Override
DECL|method|decode
specifier|public
name|long
name|decode
parameter_list|()
throws|throws
name|IOException
block|{
comment|// we need exactly 4 bytes to decode an int in this decoder impl, otherwise, throw an exception
name|int
name|offset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|offset
operator|<
literal|4
condition|)
block|{
name|int
name|nRead
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
literal|4
operator|-
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|nRead
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|offset
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|StreamCorruptedException
argument_list|(
literal|"Need 4 bytes for decoding an int, got only "
operator|+
name|offset
argument_list|)
throw|;
block|}
return|return
name|EOS
return|;
block|}
name|offset
operator|+=
name|nRead
expr_stmt|;
block|}
name|int
name|v
init|=
name|buffer
index|[
literal|3
index|]
operator|&
literal|0xff
decl_stmt|;
name|v
operator||=
operator|(
name|buffer
index|[
literal|2
index|]
operator|<<
literal|8
operator|)
operator|&
literal|0xff00
expr_stmt|;
name|v
operator||=
operator|(
name|buffer
index|[
literal|1
index|]
operator|<<
literal|16
operator|)
operator|&
literal|0xff0000
expr_stmt|;
name|v
operator||=
operator|(
name|buffer
index|[
literal|0
index|]
operator|<<
literal|24
operator|)
operator|&
literal|0xff000000
expr_stmt|;
return|return
name|v
return|;
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
literal|"Simple"
return|;
block|}
block|}
end_class
end_unit
