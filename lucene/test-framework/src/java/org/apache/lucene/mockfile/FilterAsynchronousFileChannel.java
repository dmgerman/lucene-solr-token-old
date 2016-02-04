begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
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
name|channels
operator|.
name|AsynchronousFileChannel
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|CompletionHandler
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileLock
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import
begin_comment
comment|/**    * A {@code FilterAsynchronousFileChannel} contains another   * {@code AsynchronousFileChannel}, which it uses as its basic   * source of data, possibly transforming the data along the   * way or providing additional functionality.   */
end_comment
begin_class
DECL|class|FilterAsynchronousFileChannel
specifier|public
class|class
name|FilterAsynchronousFileChannel
extends|extends
name|AsynchronousFileChannel
block|{
comment|/**     * The underlying {@code AsynchronousFileChannel} instance.     */
DECL|field|delegate
specifier|protected
specifier|final
name|AsynchronousFileChannel
name|delegate
decl_stmt|;
comment|/**    * Construct a {@code FilterAsynchronousFileChannel} based on     * the specified base channel.    *<p>    * Note that base channel is closed if this channel is closed.    * @param delegate specified base channel.    */
DECL|method|FilterAsynchronousFileChannel
specifier|public
name|FilterAsynchronousFileChannel
parameter_list|(
name|AsynchronousFileChannel
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
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
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isOpen
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isOpen
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|truncate
specifier|public
name|AsynchronousFileChannel
name|truncate
parameter_list|(
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|truncate
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|force
specifier|public
name|void
name|force
parameter_list|(
name|boolean
name|metaData
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|force
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lock
specifier|public
parameter_list|<
name|A
parameter_list|>
name|void
name|lock
parameter_list|(
name|long
name|position
parameter_list|,
name|long
name|size
parameter_list|,
name|boolean
name|shared
parameter_list|,
name|A
name|attachment
parameter_list|,
name|CompletionHandler
argument_list|<
name|FileLock
argument_list|,
name|?
super|super
name|A
argument_list|>
name|handler
parameter_list|)
block|{
name|delegate
operator|.
name|lock
argument_list|(
name|position
argument_list|,
name|size
argument_list|,
name|shared
argument_list|,
name|attachment
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lock
specifier|public
name|Future
argument_list|<
name|FileLock
argument_list|>
name|lock
parameter_list|(
name|long
name|position
parameter_list|,
name|long
name|size
parameter_list|,
name|boolean
name|shared
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|lock
argument_list|(
name|position
argument_list|,
name|size
argument_list|,
name|shared
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|tryLock
specifier|public
name|FileLock
name|tryLock
parameter_list|(
name|long
name|position
parameter_list|,
name|long
name|size
parameter_list|,
name|boolean
name|shared
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|tryLock
argument_list|(
name|position
argument_list|,
name|size
argument_list|,
name|shared
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
parameter_list|<
name|A
parameter_list|>
name|void
name|read
parameter_list|(
name|ByteBuffer
name|dst
parameter_list|,
name|long
name|position
parameter_list|,
name|A
name|attachment
parameter_list|,
name|CompletionHandler
argument_list|<
name|Integer
argument_list|,
name|?
super|super
name|A
argument_list|>
name|handler
parameter_list|)
block|{
name|delegate
operator|.
name|read
argument_list|(
name|dst
argument_list|,
name|position
argument_list|,
name|attachment
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|Future
argument_list|<
name|Integer
argument_list|>
name|read
parameter_list|(
name|ByteBuffer
name|dst
parameter_list|,
name|long
name|position
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|read
argument_list|(
name|dst
argument_list|,
name|position
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
parameter_list|<
name|A
parameter_list|>
name|void
name|write
parameter_list|(
name|ByteBuffer
name|src
parameter_list|,
name|long
name|position
parameter_list|,
name|A
name|attachment
parameter_list|,
name|CompletionHandler
argument_list|<
name|Integer
argument_list|,
name|?
super|super
name|A
argument_list|>
name|handler
parameter_list|)
block|{
name|delegate
operator|.
name|write
argument_list|(
name|src
argument_list|,
name|position
argument_list|,
name|attachment
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|Future
argument_list|<
name|Integer
argument_list|>
name|write
parameter_list|(
name|ByteBuffer
name|src
parameter_list|,
name|long
name|position
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|write
argument_list|(
name|src
argument_list|,
name|position
argument_list|)
return|;
block|}
block|}
end_class
end_unit
