begin_unit
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|PriorityQueue
import|;
end_import
begin_comment
comment|/** Keeps highest results, first by largest int value,  *  then tie break by smallest ord. */
end_comment
begin_class
DECL|class|TopOrdAndIntQueue
specifier|public
class|class
name|TopOrdAndIntQueue
extends|extends
name|PriorityQueue
argument_list|<
name|TopOrdAndIntQueue
operator|.
name|OrdAndValue
argument_list|>
block|{
comment|/** Holds a single entry. */
DECL|class|OrdAndValue
specifier|public
specifier|static
specifier|final
class|class
name|OrdAndValue
block|{
comment|/** Ordinal of the entry. */
DECL|field|ord
specifier|public
name|int
name|ord
decl_stmt|;
comment|/** Value associated with the ordinal. */
DECL|field|value
specifier|public
name|int
name|value
decl_stmt|;
comment|/** Default constructor. */
DECL|method|OrdAndValue
specifier|public
name|OrdAndValue
parameter_list|()
block|{     }
block|}
comment|/** Sole constructor. */
DECL|method|TopOrdAndIntQueue
specifier|public
name|TopOrdAndIntQueue
parameter_list|(
name|int
name|topN
parameter_list|)
block|{
name|super
argument_list|(
name|topN
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|OrdAndValue
name|a
parameter_list|,
name|OrdAndValue
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|value
operator|<
name|b
operator|.
name|value
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|a
operator|.
name|value
operator|>
name|b
operator|.
name|value
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|a
operator|.
name|ord
operator|>
name|b
operator|.
name|ord
return|;
block|}
block|}
block|}
end_class
end_unit
