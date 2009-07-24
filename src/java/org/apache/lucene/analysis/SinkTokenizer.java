begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_comment
comment|/**  * A SinkTokenizer can be used to cache Tokens for use in an Analyzer  *<p/>  * WARNING: {@link TeeTokenFilter} and {@link SinkTokenizer} only work with the old TokenStream API.  * If you switch to the new API, you need to use {@link TeeSinkTokenFilter} instead, which offers   * the same functionality.  * @see TeeTokenFilter  * @deprecated Use {@link TeeSinkTokenFilter} instead  *  **/
end_comment
begin_class
DECL|class|SinkTokenizer
specifier|public
class|class
name|SinkTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|lst
specifier|protected
name|List
comment|/*<Token>*/
name|lst
init|=
operator|new
name|ArrayList
comment|/*<Token>*/
argument_list|()
decl_stmt|;
DECL|field|iter
specifier|protected
name|Iterator
comment|/*<Token>*/
name|iter
decl_stmt|;
DECL|method|SinkTokenizer
specifier|public
name|SinkTokenizer
parameter_list|(
name|List
comment|/*<Token>*/
name|input
parameter_list|)
block|{
name|this
operator|.
name|lst
operator|=
name|input
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|lst
operator|==
literal|null
condition|)
name|this
operator|.
name|lst
operator|=
operator|new
name|ArrayList
comment|/*<Token>*/
argument_list|()
expr_stmt|;
block|}
DECL|method|SinkTokenizer
specifier|public
name|SinkTokenizer
parameter_list|()
block|{
name|this
operator|.
name|lst
operator|=
operator|new
name|ArrayList
comment|/*<Token>*/
argument_list|()
expr_stmt|;
block|}
DECL|method|SinkTokenizer
specifier|public
name|SinkTokenizer
parameter_list|(
name|int
name|initCap
parameter_list|)
block|{
name|this
operator|.
name|lst
operator|=
operator|new
name|ArrayList
comment|/*<Token>*/
argument_list|(
name|initCap
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the tokens in the internal List.    *<p/>    * WARNING: Adding tokens to this list requires the {@link #reset()} method to be called in order for them    * to be made available.  Also, this Tokenizer does nothing to protect against {@link java.util.ConcurrentModificationException}s    * in the case of adds happening while {@link #next(org.apache.lucene.analysis.Token)} is being called.    *<p/>    * WARNING: Since this SinkTokenizer can be reset and the cached tokens made available again, do not modify them. Modify clones instead.    *    * @return A List of {@link org.apache.lucene.analysis.Token}s    */
DECL|method|getTokens
specifier|public
name|List
comment|/*<Token>*/
name|getTokens
parameter_list|()
block|{
return|return
name|lst
return|;
block|}
comment|/**    * Returns the next token out of the list of cached tokens    * @return The next {@link org.apache.lucene.analysis.Token} in the Sink.    * @throws IOException    */
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
if|if
condition|(
name|iter
operator|==
literal|null
condition|)
name|iter
operator|=
name|lst
operator|.
name|iterator
argument_list|()
expr_stmt|;
comment|// Since this TokenStream can be reset we have to maintain the tokens as immutable
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Token
name|nextToken
init|=
operator|(
name|Token
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|(
name|Token
operator|)
name|nextToken
operator|.
name|clone
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Override this method to cache only certain tokens, or new tokens based    * on the old tokens.    *    * @param t The {@link org.apache.lucene.analysis.Token} to add to the sink    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|==
literal|null
condition|)
return|return;
name|lst
operator|.
name|add
argument_list|(
operator|(
name|Token
operator|)
name|t
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|//nothing to close
name|input
operator|=
literal|null
expr_stmt|;
name|lst
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Reset the internal data structures to the start at the front of the list of tokens.  Should be called    * if tokens were added to the list after an invocation of {@link #next(Token)}    * @throws IOException    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|iter
operator|=
name|lst
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
