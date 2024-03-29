begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|NumericDocValues
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
name|packed
operator|.
name|PackedInts
import|;
end_import
begin_comment
comment|/** Abstraction over an array of longs.  *  This class extends NumericDocValues so that we don't need to add another  *  level of abstraction every time we want eg. to use the {@link PackedInts}  *  utility classes to represent a {@link NumericDocValues} instance.  *  @lucene.internal */
end_comment
begin_class
DECL|class|LongValues
specifier|public
specifier|abstract
class|class
name|LongValues
extends|extends
name|NumericDocValues
block|{
comment|/** An instance that returns the provided value. */
DECL|field|IDENTITY
specifier|public
specifier|static
specifier|final
name|LongValues
name|IDENTITY
init|=
operator|new
name|LongValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
return|return
name|index
return|;
block|}
block|}
decl_stmt|;
comment|/** Get value at<code>index</code>. */
DECL|method|get
specifier|public
specifier|abstract
name|long
name|get
parameter_list|(
name|long
name|index
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
return|return
name|get
argument_list|(
operator|(
name|long
operator|)
name|idx
argument_list|)
return|;
block|}
block|}
end_class
end_unit
