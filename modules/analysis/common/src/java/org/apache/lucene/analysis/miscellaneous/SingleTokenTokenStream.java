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
name|util
operator|.
name|AttributeImpl
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
name|CharTermAttribute
import|;
end_import
begin_comment
comment|/**  * A {@link TokenStream} containing a single token.  */
end_comment
begin_class
DECL|class|SingleTokenTokenStream
specifier|public
specifier|final
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
DECL|field|tokenAtt
specifier|private
specifier|final
name|AttributeImpl
name|tokenAtt
decl_stmt|;
DECL|method|SingleTokenTokenStream
specifier|public
name|SingleTokenTokenStream
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
name|super
argument_list|(
name|Token
operator|.
name|TOKEN_ATTRIBUTE_FACTORY
argument_list|)
expr_stmt|;
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
name|tokenAtt
operator|=
operator|(
name|AttributeImpl
operator|)
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|tokenAtt
operator|instanceof
name|Token
operator|)
assert|;
block|}
annotation|@
name|Override
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
else|else
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|singleToken
operator|.
name|copyTo
argument_list|(
name|tokenAtt
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
block|}
annotation|@
name|Override
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
