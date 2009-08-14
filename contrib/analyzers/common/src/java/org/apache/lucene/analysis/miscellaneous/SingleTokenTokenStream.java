begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|Token
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
name|FlagsAttribute
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
name|OffsetAttribute
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
name|PositionIncrementAttribute
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
name|TermAttribute
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
name|TypeAttribute
import|;
end_import
begin_comment
comment|/**  * A token stream containing a single token.  */
end_comment
begin_class
DECL|class|SingleTokenTokenStream
specifier|public
class|class
name|SingleTokenTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|exhausted
specifier|private
name|boolean
name|exhausted
init|=
literal|false
decl_stmt|;
comment|// The token needs to be immutable, so work with clones!
DECL|field|singleToken
specifier|private
name|Token
name|singleToken
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|field|flagsAtt
specifier|private
name|FlagsAttribute
name|flagsAtt
decl_stmt|;
DECL|field|posIncAtt
specifier|private
name|PositionIncrementAttribute
name|posIncAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
decl_stmt|;
DECL|field|payloadAtt
specifier|private
name|PayloadAttribute
name|payloadAtt
decl_stmt|;
DECL|method|SingleTokenTokenStream
specifier|public
name|SingleTokenTokenStream
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
assert|assert
name|token
operator|!=
literal|null
assert|;
name|this
operator|.
name|singleToken
operator|=
operator|(
name|Token
operator|)
name|token
operator|.
name|clone
argument_list|()
expr_stmt|;
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
operator|(
name|OffsetAttribute
operator|)
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|flagsAtt
operator|=
operator|(
name|FlagsAttribute
operator|)
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posIncAtt
operator|=
operator|(
name|PositionIncrementAttribute
operator|)
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|typeAtt
operator|=
operator|(
name|TypeAttribute
operator|)
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|payloadAtt
operator|=
operator|(
name|PayloadAttribute
operator|)
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|exhausted
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Token
name|clone
init|=
operator|(
name|Token
operator|)
name|singleToken
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|clone
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|clone
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|clone
operator|.
name|startOffset
argument_list|()
argument_list|,
name|clone
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|flagsAtt
operator|.
name|setFlags
argument_list|(
name|clone
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|clone
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
name|clone
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|payloadAtt
operator|.
name|setPayload
argument_list|(
name|clone
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
name|exhausted
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** @deprecated Will be removed in Lucene 3.0. This method is final, as it should    * not be overridden. Delegates to the backwards compatibility layer. */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
return|return
name|super
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
return|;
block|}
comment|/** @deprecated Will be removed in Lucene 3.0. This method is final, as it should    * not be overridden. Delegates to the backwards compatibility layer. */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
return|return
name|super
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|exhausted
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|getToken
specifier|public
name|Token
name|getToken
parameter_list|()
block|{
return|return
operator|(
name|Token
operator|)
name|singleToken
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|setToken
specifier|public
name|void
name|setToken
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
name|this
operator|.
name|singleToken
operator|=
operator|(
name|Token
operator|)
name|token
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
