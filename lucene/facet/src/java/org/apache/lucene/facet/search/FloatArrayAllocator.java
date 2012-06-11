begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * An FloatArrayAllocator is an object which manages float array objects  * of a certain size. These float arrays are needed temporarily during  * faceted search (see {@link FacetsAccumulator} and can be reused across searches  * instead of being allocated afresh on every search.  *<P>  * An FloatArrayAllocator is thread-safe.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FloatArrayAllocator
specifier|public
specifier|final
class|class
name|FloatArrayAllocator
extends|extends
name|TemporaryObjectAllocator
argument_list|<
name|float
index|[]
argument_list|>
block|{
comment|// An FloatArrayAllocater deals with integer arrays of a fixed size, size.
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
comment|/**    * Construct an allocator for float arrays of size<CODE>size</CODE>,    * keeping around a pool of up to<CODE>maxArrays</CODE> old arrays.    *<P>    * Note that the pool size only restricts the number of arrays that hang    * around when not needed, but<I>not</I> the maximum number of arrays    * that are allocated when actually is use: If a number of concurrent    * threads ask for an allocation, all of them will get a counter array,    * even if their number is greater than maxArrays. If an application wants    * to limit the number of concurrent threads making allocations, it needs    * to do so on its own - for example by blocking new threads until the    * existing ones have finished.    *<P>    * In particular, when maxArrays=0, this object behaves as a trivial    * allocator, always allocating a new array and never reusing an old one.     */
DECL|method|FloatArrayAllocator
specifier|public
name|FloatArrayAllocator
parameter_list|(
name|int
name|size
parameter_list|,
name|int
name|maxArrays
parameter_list|)
block|{
name|super
argument_list|(
name|maxArrays
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|float
index|[]
name|create
parameter_list|()
block|{
return|return
operator|new
name|float
index|[
name|size
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|float
index|[]
name|array
parameter_list|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|array
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
