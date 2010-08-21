begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
begin_class
DECL|class|MutableValueFloat
specifier|public
class|class
name|MutableValueFloat
extends|extends
name|MutableValue
block|{
DECL|field|value
specifier|public
name|float
name|value
decl_stmt|;
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Object
name|toObject
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|MutableValue
name|source
parameter_list|)
block|{
name|value
operator|=
operator|(
operator|(
name|MutableValueFloat
operator|)
name|source
operator|)
operator|.
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|duplicate
specifier|public
name|MutableValue
name|duplicate
parameter_list|()
block|{
name|MutableValueFloat
name|v
init|=
operator|new
name|MutableValueFloat
argument_list|()
decl_stmt|;
name|v
operator|.
name|value
operator|=
name|this
operator|.
name|value
expr_stmt|;
return|return
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|equalsSameType
specifier|public
name|boolean
name|equalsSameType
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|value
operator|==
operator|(
operator|(
name|MutableValueFloat
operator|)
name|other
operator|)
operator|.
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|compareSameType
specifier|public
name|int
name|compareSameType
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|Float
operator|.
name|compare
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|MutableValueFloat
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
comment|// handles NaN
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class
end_unit
