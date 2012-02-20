begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.base.shape.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|simple
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|EqualsBuilder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|SpatialRelation
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
name|spatial
operator|.
name|base
operator|.
name|context
operator|.
name|SpatialContext
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
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|Rectangle
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
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|Point
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
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|Shape
import|;
end_import
begin_class
DECL|class|PointImpl
specifier|public
class|class
name|PointImpl
implements|implements
name|Point
block|{
DECL|field|x
specifier|private
specifier|final
name|double
name|x
decl_stmt|;
DECL|field|y
specifier|private
specifier|final
name|double
name|y
decl_stmt|;
DECL|method|PointImpl
specifier|public
name|PointImpl
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|)
block|{
name|this
operator|.
name|x
operator|=
name|x
expr_stmt|;
name|this
operator|.
name|y
operator|=
name|y
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getX
specifier|public
name|double
name|getX
parameter_list|()
block|{
return|return
name|x
return|;
block|}
annotation|@
name|Override
DECL|method|getY
specifier|public
name|double
name|getY
parameter_list|()
block|{
return|return
name|y
return|;
block|}
annotation|@
name|Override
DECL|method|getBoundingBox
specifier|public
name|Rectangle
name|getBoundingBox
parameter_list|()
block|{
return|return
operator|new
name|RectangleImpl
argument_list|(
name|x
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|y
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCenter
specifier|public
name|PointImpl
name|getCenter
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|relate
specifier|public
name|SpatialRelation
name|relate
parameter_list|(
name|Shape
name|other
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|Point
condition|)
return|return
name|this
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|?
name|SpatialRelation
operator|.
name|INTERSECTS
else|:
name|SpatialRelation
operator|.
name|DISJOINT
return|;
return|return
name|other
operator|.
name|relate
argument_list|(
name|this
argument_list|,
name|ctx
argument_list|)
operator|.
name|transpose
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasArea
specifier|public
name|boolean
name|hasArea
parameter_list|()
block|{
return|return
literal|false
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
literal|"Pt(x="
operator|+
name|x
operator|+
literal|",y="
operator|+
name|y
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|obj
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
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PointImpl
name|rhs
init|=
operator|(
name|PointImpl
operator|)
name|obj
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|x
argument_list|,
name|rhs
operator|.
name|x
argument_list|)
operator|.
name|append
argument_list|(
name|y
argument_list|,
name|rhs
operator|.
name|y
argument_list|)
operator|.
name|isEquals
argument_list|()
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
operator|new
name|HashCodeBuilder
argument_list|(
literal|5
argument_list|,
literal|89
argument_list|)
operator|.
name|append
argument_list|(
name|x
argument_list|)
operator|.
name|append
argument_list|(
name|y
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
