begin_unit
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|CharBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|BytesRef
import|;
end_import
begin_comment
comment|/**  *  Does nothing other than convert the char array to a byte array using the specified encoding.  *  **/
end_comment
begin_class
DECL|class|IdentityEncoder
specifier|public
class|class
name|IdentityEncoder
extends|extends
name|AbstractEncoder
implements|implements
name|PayloadEncoder
block|{
DECL|field|charset
specifier|protected
name|Charset
name|charset
init|=
name|StandardCharsets
operator|.
name|UTF_8
decl_stmt|;
DECL|method|IdentityEncoder
specifier|public
name|IdentityEncoder
parameter_list|()
block|{   }
DECL|method|IdentityEncoder
specifier|public
name|IdentityEncoder
parameter_list|(
name|Charset
name|charset
parameter_list|)
block|{
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|BytesRef
name|encode
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
specifier|final
name|ByteBuffer
name|bb
init|=
name|charset
operator|.
name|encode
argument_list|(
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|bb
operator|.
name|hasArray
argument_list|()
condition|)
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|bb
operator|.
name|array
argument_list|()
argument_list|,
name|bb
operator|.
name|arrayOffset
argument_list|()
operator|+
name|bb
operator|.
name|position
argument_list|()
argument_list|,
name|bb
operator|.
name|remaining
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|// normally it should always have an array, but who knows?
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|bb
operator|.
name|remaining
argument_list|()
index|]
decl_stmt|;
name|bb
operator|.
name|get
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
operator|new
name|BytesRef
argument_list|(
name|b
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
