begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.spatial4j.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|spatial4j
operator|.
name|geo3d
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/** Combination of a plane, and a sign value indicating what evaluation values are on the correct * side of the plane. */
end_comment
begin_class
DECL|class|SidedPlane
specifier|public
class|class
name|SidedPlane
extends|extends
name|Plane
implements|implements
name|Membership
block|{
DECL|field|sigNum
specifier|public
specifier|final
name|double
name|sigNum
decl_stmt|;
comment|/** Construct a SidedPlane identical to an existing one, but reversed.     *@param sidedPlane is the existing plane.     */
DECL|method|SidedPlane
specifier|public
name|SidedPlane
parameter_list|(
name|SidedPlane
name|sidedPlane
parameter_list|)
block|{
name|super
argument_list|(
name|sidedPlane
argument_list|,
name|sidedPlane
operator|.
name|D
argument_list|)
expr_stmt|;
name|this
operator|.
name|sigNum
operator|=
operator|-
name|sidedPlane
operator|.
name|sigNum
expr_stmt|;
block|}
comment|/** Construct a sided plane from a pair of vectors describing points, and including      * origin, plus a point p which describes the side.      *@param p point to evaluate      *@param A is the first in-plane point      *@param B is the second in-plane point      */
DECL|method|SidedPlane
specifier|public
name|SidedPlane
parameter_list|(
name|Vector
name|p
parameter_list|,
name|Vector
name|A
parameter_list|,
name|Vector
name|B
parameter_list|)
block|{
name|super
argument_list|(
name|A
argument_list|,
name|B
argument_list|)
expr_stmt|;
name|sigNum
operator|=
name|Math
operator|.
name|signum
argument_list|(
name|evaluate
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a sided plane from a point and a Z coordinate.      *@param p point to evaluate.      *@param height is the Z coordinate of the plane.      */
DECL|method|SidedPlane
specifier|public
name|SidedPlane
parameter_list|(
name|Vector
name|p
parameter_list|,
name|double
name|height
parameter_list|)
block|{
name|super
argument_list|(
name|height
argument_list|)
expr_stmt|;
name|sigNum
operator|=
name|Math
operator|.
name|signum
argument_list|(
name|evaluate
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a sided vertical plane from a point and specified x and y coordinates.      *@param p point to evaluate.      *@param x is the specified x.      *@param y is the specified y.      */
DECL|method|SidedPlane
specifier|public
name|SidedPlane
parameter_list|(
name|Vector
name|p
parameter_list|,
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|)
block|{
name|super
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
expr_stmt|;
name|sigNum
operator|=
name|Math
operator|.
name|signum
argument_list|(
name|evaluate
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a sided plane with a normal vector and offset.      *@param p point to evaluate.      *@param v is the normal vector.      *@param D is the origin offset for the plan.      */
DECL|method|SidedPlane
specifier|public
name|SidedPlane
parameter_list|(
name|Vector
name|p
parameter_list|,
name|Vector
name|v
parameter_list|,
name|double
name|D
parameter_list|)
block|{
name|super
argument_list|(
name|v
argument_list|,
name|D
argument_list|)
expr_stmt|;
name|sigNum
operator|=
name|Math
operator|.
name|signum
argument_list|(
name|evaluate
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Check if a point is within this shape.      *@param point is the point to check.      *@return true if the point is within this shape      */
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
name|Vector
name|point
parameter_list|)
block|{
name|double
name|sigNum
init|=
name|Math
operator|.
name|signum
argument_list|(
name|evaluate
argument_list|(
name|point
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|sigNum
operator|==
literal|0.0
condition|)
return|return
literal|true
return|;
return|return
name|sigNum
operator|==
name|this
operator|.
name|sigNum
return|;
block|}
comment|/** Check if a point is within this shape.      *@param x is x coordinate of point to check.      *@param y is y coordinate of point to check.      *@param z is z coordinate of point to check.      *@return true if the point is within this shape      */
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|z
parameter_list|)
block|{
name|double
name|sigNum
init|=
name|Math
operator|.
name|signum
argument_list|(
name|this
operator|.
name|x
operator|*
name|x
operator|+
name|this
operator|.
name|y
operator|*
name|y
operator|+
name|this
operator|.
name|z
operator|*
name|z
argument_list|)
decl_stmt|;
return|return
name|sigNum
operator|==
name|this
operator|.
name|sigNum
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
literal|"[A="
operator|+
name|x
operator|+
literal|", B="
operator|+
name|y
operator|+
literal|", C="
operator|+
name|z
operator|+
literal|", D="
operator|+
name|D
operator|+
literal|", side="
operator|+
name|sigNum
operator|+
literal|"]"
return|;
block|}
block|}
end_class
end_unit
