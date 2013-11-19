begin_unit
begin_package
DECL|package|org.apache.lucene.facet.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|simple
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
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
name|BytesRef
import|;
end_import
begin_comment
comment|/** Associates an arbitrary float with the added facet  *  path, encoding the float into a 4-byte BytesRef. */
end_comment
begin_class
DECL|class|FloatAssociationFacetField
specifier|public
class|class
name|FloatAssociationFacetField
extends|extends
name|AssociationFacetField
block|{
comment|/** Utility ctor: associates an int value (translates it    *  to 4-byte BytesRef). */
DECL|method|FloatAssociationFacetField
specifier|public
name|FloatAssociationFacetField
parameter_list|(
name|float
name|assoc
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|floatToBytesRef
argument_list|(
name|assoc
argument_list|)
argument_list|,
name|dim
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|floatToBytesRef
specifier|public
specifier|static
name|BytesRef
name|floatToBytesRef
parameter_list|(
name|float
name|v
parameter_list|)
block|{
return|return
name|IntAssociationFacetField
operator|.
name|intToBytesRef
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
DECL|method|bytesRefToFloat
specifier|public
specifier|static
name|float
name|bytesRefToFloat
parameter_list|(
name|BytesRef
name|b
parameter_list|)
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|IntAssociationFacetField
operator|.
name|bytesRefToInt
argument_list|(
name|b
argument_list|)
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
literal|"FloatAssociationFacetField(dim="
operator|+
name|dim
operator|+
literal|" path="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|path
argument_list|)
operator|+
literal|" value="
operator|+
name|bytesRefToFloat
argument_list|(
name|assoc
argument_list|)
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
