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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|io
operator|.
name|Serializable
import|;
end_import
begin_class
DECL|class|RAMFile
class|class
name|RAMFile
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1l
decl_stmt|;
comment|// Direct read-only access to state supported for streams since a writing stream implies no other concurrent streams
DECL|field|buffers
name|ArrayList
name|buffers
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|length
name|long
name|length
decl_stmt|;
DECL|field|directory
name|RAMDirectory
name|directory
decl_stmt|;
DECL|field|sizeInBytes
name|long
name|sizeInBytes
decl_stmt|;
comment|// Only maintained if in a directory; updates synchronized on directory
comment|// This is publicly modifiable via Directory.touchFile(), so direct access not supported
DECL|field|lastModified
specifier|private
name|long
name|lastModified
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// File used as buffer, in no RAMDirectory
DECL|method|RAMFile
name|RAMFile
parameter_list|()
block|{}
DECL|method|RAMFile
name|RAMFile
parameter_list|(
name|RAMDirectory
name|directory
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
comment|// For non-stream access from thread that might be concurrent with writing
DECL|method|getLength
specifier|synchronized
name|long
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|setLength
specifier|synchronized
name|void
name|setLength
parameter_list|(
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
comment|// For non-stream access from thread that might be concurrent with writing
DECL|method|getLastModified
specifier|synchronized
name|long
name|getLastModified
parameter_list|()
block|{
return|return
name|lastModified
return|;
block|}
DECL|method|setLastModified
specifier|synchronized
name|void
name|setLastModified
parameter_list|(
name|long
name|lastModified
parameter_list|)
block|{
name|this
operator|.
name|lastModified
operator|=
name|lastModified
expr_stmt|;
block|}
comment|// Only one writing stream with no concurrent reading streams, so no file synchronization required
DECL|method|addBuffer
specifier|final
name|byte
index|[]
name|addBuffer
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|byte
index|[]
name|buffer
init|=
name|newBuffer
argument_list|(
name|size
argument_list|)
decl_stmt|;
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
synchronized|synchronized
init|(
name|directory
init|)
block|{
comment|// Ensure addition of buffer and adjustment to directory size are atomic wrt directory
name|buffers
operator|.
name|add
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|directory
operator|.
name|sizeInBytes
operator|+=
name|size
expr_stmt|;
name|sizeInBytes
operator|+=
name|size
expr_stmt|;
block|}
else|else
name|buffers
operator|.
name|add
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
return|return
name|buffer
return|;
block|}
comment|/**    * Expert: allocate a new buffer.     * Subclasses can allocate differently.     * @param size size of allocated buffer.    * @return allocated buffer.    */
DECL|method|newBuffer
name|byte
index|[]
name|newBuffer
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|byte
index|[
name|size
index|]
return|;
block|}
comment|// Only valid if in a directory
DECL|method|getSizeInBytes
name|long
name|getSizeInBytes
parameter_list|()
block|{
synchronized|synchronized
init|(
name|directory
init|)
block|{
return|return
name|sizeInBytes
return|;
block|}
block|}
block|}
end_class
end_unit
