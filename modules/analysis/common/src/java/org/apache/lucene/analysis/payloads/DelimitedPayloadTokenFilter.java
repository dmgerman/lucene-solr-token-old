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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|analysis
operator|.
name|TokenFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
import|;
end_import
begin_comment
comment|/**  * Characters before the delimiter are the "token", those after are the payload.  *<p/>  * For example, if the delimiter is '|', then for the string "foo|bar", foo is the token  * and "bar" is a payload.  *<p/>  * Note, you can also include a {@link org.apache.lucene.analysis.payloads.PayloadEncoder} to convert the payload in an appropriate way (from characters to bytes).  *<p/>  * Note make sure your Tokenizer doesn't split on the delimiter, or this won't work  *  * @see PayloadEncoder  */
end_comment
begin_class
DECL|class|DelimitedPayloadTokenFilter
specifier|public
specifier|final
class|class
name|DelimitedPayloadTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|DEFAULT_DELIMITER
specifier|public
specifier|static
specifier|final
name|char
name|DEFAULT_DELIMITER
init|=
literal|'|'
decl_stmt|;
DECL|field|delimiter
specifier|private
specifier|final
name|char
name|delimiter
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|payAtt
specifier|private
specifier|final
name|PayloadAttribute
name|payAtt
init|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|encoder
specifier|private
specifier|final
name|PayloadEncoder
name|encoder
decl_stmt|;
DECL|method|DelimitedPayloadTokenFilter
specifier|public
name|DelimitedPayloadTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|char
name|delimiter
parameter_list|,
name|PayloadEncoder
name|encoder
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|delimiter
operator|=
name|delimiter
expr_stmt|;
name|this
operator|.
name|encoder
operator|=
name|encoder
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
specifier|final
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|termAtt
operator|.
name|length
argument_list|()
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
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|==
name|delimiter
condition|)
block|{
name|payAtt
operator|.
name|setPayload
argument_list|(
name|encoder
operator|.
name|encode
argument_list|(
name|buffer
argument_list|,
name|i
operator|+
literal|1
argument_list|,
operator|(
name|length
operator|-
operator|(
name|i
operator|+
literal|1
operator|)
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|// simply set a new length
return|return
literal|true
return|;
block|}
block|}
comment|// we have not seen the delimiter
name|payAtt
operator|.
name|setPayload
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
