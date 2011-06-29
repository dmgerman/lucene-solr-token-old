begin_unit
begin_package
DECL|package|org.apache.lucene.facet.enhancements.association
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|enhancements
operator|.
name|association
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryProperty
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * An {@link AssociationProperty} which treats the association as float - the  * association bits are actually float bits, and thus merging two associations  * is done by float summation.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|AssociationFloatProperty
specifier|public
class|class
name|AssociationFloatProperty
extends|extends
name|AssociationProperty
block|{
comment|/**    * Constructor.    *     * @param value    *            The association value.    */
DECL|method|AssociationFloatProperty
specifier|public
name|AssociationFloatProperty
parameter_list|(
name|float
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|AssociationFloatProperty
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AssociationFloatProperty
name|o
init|=
operator|(
name|AssociationFloatProperty
operator|)
name|other
decl_stmt|;
return|return
name|o
operator|.
name|association
operator|==
name|this
operator|.
name|association
return|;
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
literal|"AssociationFloatProperty"
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
operator|+
operator|(
name|int
operator|)
name|association
return|;
block|}
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|CategoryProperty
name|other
parameter_list|)
block|{
name|AssociationFloatProperty
name|o
init|=
operator|(
name|AssociationFloatProperty
operator|)
name|other
decl_stmt|;
name|this
operator|.
name|association
operator|=
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|this
operator|.
name|association
argument_list|)
operator|+
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|o
operator|.
name|association
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getFloatAssociation
specifier|public
name|float
name|getFloatAssociation
parameter_list|()
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|association
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
operator|+
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|getAssociation
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
