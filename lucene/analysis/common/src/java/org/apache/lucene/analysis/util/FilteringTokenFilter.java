begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|PositionIncrementAttribute
import|;
end_import
begin_comment
comment|/**  * Abstract base class for TokenFilters that may remove tokens.  * You have to implement {@link #accept} and return a boolean if the current  * token should be preserved. {@link #incrementToken} uses this method  * to decide if a token should be passed to the caller.  */
end_comment
begin_class
DECL|class|FilteringTokenFilter
specifier|public
specifier|abstract
class|class
name|FilteringTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|private
name|boolean
name|enablePositionIncrements
decl_stmt|;
comment|// no init needed, as ctor enforces setting value!
DECL|field|first
specifier|private
name|boolean
name|first
init|=
literal|true
decl_stmt|;
comment|// only used when not preserving gaps
DECL|method|FilteringTokenFilter
specifier|public
name|FilteringTokenFilter
parameter_list|(
name|boolean
name|enablePositionIncrements
parameter_list|,
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|enablePositionIncrements
operator|=
name|enablePositionIncrements
expr_stmt|;
block|}
comment|/** Override this method and return if the current input token should be returned by {@link #incrementToken}. */
DECL|method|accept
specifier|protected
specifier|abstract
name|boolean
name|accept
parameter_list|()
throws|throws
name|IOException
function_decl|;
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
name|enablePositionIncrements
condition|)
block|{
name|int
name|skippedPositions
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|accept
argument_list|()
condition|)
block|{
if|if
condition|(
name|skippedPositions
operator|!=
literal|0
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|+
name|skippedPositions
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
name|skippedPositions
operator|+=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
while|while
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|accept
argument_list|()
condition|)
block|{
if|if
condition|(
name|first
condition|)
block|{
comment|// first token having posinc=0 is illegal.
if|if
condition|(
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|0
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
comment|// reached EOS -- return false
return|return
literal|false
return|;
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
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|first
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * @see #setEnablePositionIncrements(boolean)    */
DECL|method|getEnablePositionIncrements
specifier|public
name|boolean
name|getEnablePositionIncrements
parameter_list|()
block|{
return|return
name|enablePositionIncrements
return|;
block|}
comment|/**    * If<code>true</code>, this TokenFilter will preserve    * positions of the incoming tokens (ie, accumulate and    * set position increments of the removed tokens).    * Generally,<code>true</code> is best as it does not    * lose information (positions of the original tokens)    * during indexing.    *     *<p> When set, when a token is stopped    * (omitted), the position increment of the following    * token is incremented.    *    *<p><b>NOTE</b>: be sure to also    * set org.apache.lucene.queryparser.classic.QueryParser#setEnablePositionIncrements if    * you use QueryParser to create queries.    */
DECL|method|setEnablePositionIncrements
specifier|public
name|void
name|setEnablePositionIncrements
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
name|this
operator|.
name|enablePositionIncrements
operator|=
name|enable
expr_stmt|;
block|}
block|}
end_class
end_unit
