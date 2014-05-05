begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
begin_comment
comment|/**  * A {@link RateLimiter rate limiting} {@link IndexOutput}  *   * @lucene.internal  */
end_comment
begin_class
DECL|class|RateLimitedIndexOutput
specifier|final
class|class
name|RateLimitedIndexOutput
extends|extends
name|BufferedIndexOutput
block|{
DECL|field|delegate
specifier|private
specifier|final
name|IndexOutput
name|delegate
decl_stmt|;
DECL|field|bufferedDelegate
specifier|private
specifier|final
name|BufferedIndexOutput
name|bufferedDelegate
decl_stmt|;
DECL|field|rateLimiter
specifier|private
specifier|final
name|RateLimiter
name|rateLimiter
decl_stmt|;
comment|/** How many bytes we've written since we last called rateLimiter.pause. */
DECL|field|bytesSinceLastPause
specifier|private
name|long
name|bytesSinceLastPause
decl_stmt|;
DECL|method|RateLimitedIndexOutput
name|RateLimitedIndexOutput
parameter_list|(
specifier|final
name|RateLimiter
name|rateLimiter
parameter_list|,
specifier|final
name|IndexOutput
name|delegate
parameter_list|)
block|{
comment|// TODO should we make buffer size configurable
if|if
condition|(
name|delegate
operator|instanceof
name|BufferedIndexOutput
condition|)
block|{
name|bufferedDelegate
operator|=
operator|(
name|BufferedIndexOutput
operator|)
name|delegate
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|bufferedDelegate
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|rateLimiter
operator|=
name|rateLimiter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flushBuffer
specifier|protected
name|void
name|flushBuffer
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
throws|throws
name|IOException
block|{
name|bytesSinceLastPause
operator|+=
name|len
expr_stmt|;
if|if
condition|(
name|bytesSinceLastPause
operator|>
name|rateLimiter
operator|.
name|getMinPauseCheckBytes
argument_list|()
condition|)
block|{
name|rateLimiter
operator|.
name|pause
argument_list|(
name|bytesSinceLastPause
argument_list|)
expr_stmt|;
name|bytesSinceLastPause
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|bufferedDelegate
operator|!=
literal|null
condition|)
block|{
name|bufferedDelegate
operator|.
name|flushBuffer
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|delegate
operator|.
name|writeBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
