begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import
begin_comment
comment|/**  *   * @lucene.internal  */
end_comment
begin_class
DECL|class|ParallelArray
specifier|public
specifier|abstract
class|class
name|ParallelArray
parameter_list|<
name|T
extends|extends
name|ParallelArray
parameter_list|<
name|?
parameter_list|>
parameter_list|>
block|{
DECL|field|size
specifier|public
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|bytesUsed
specifier|protected
specifier|final
name|AtomicLong
name|bytesUsed
decl_stmt|;
DECL|method|ParallelArray
specifier|protected
name|ParallelArray
parameter_list|(
specifier|final
name|int
name|size
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|size
operator|)
operator|*
name|bytesPerEntry
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|bytesPerEntry
specifier|protected
specifier|abstract
name|int
name|bytesPerEntry
parameter_list|()
function_decl|;
DECL|method|bytesUsed
specifier|public
name|AtomicLong
name|bytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
return|;
block|}
DECL|method|deref
specifier|public
name|void
name|deref
parameter_list|()
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
operator|-
name|size
operator|)
operator|*
name|bytesPerEntry
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|newInstance
specifier|public
specifier|abstract
name|T
name|newInstance
parameter_list|(
name|int
name|size
parameter_list|)
function_decl|;
DECL|method|grow
specifier|public
specifier|final
name|T
name|grow
parameter_list|()
block|{
name|int
name|newSize
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|size
operator|+
literal|1
argument_list|,
name|bytesPerEntry
argument_list|()
argument_list|)
decl_stmt|;
name|T
name|newArray
init|=
name|newInstance
argument_list|(
name|newSize
argument_list|)
decl_stmt|;
name|copyTo
argument_list|(
name|newArray
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|newSize
operator|-
name|size
operator|)
operator|*
name|bytesPerEntry
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newArray
return|;
block|}
DECL|method|copyTo
specifier|protected
specifier|abstract
name|void
name|copyTo
parameter_list|(
name|T
name|toArray
parameter_list|,
name|int
name|numToCopy
parameter_list|)
function_decl|;
block|}
end_class
end_unit
