begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial3d.geom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
package|;
end_package
begin_comment
comment|/**  * Combination of a plane, and a sign value indicating what evaluation values are on the correct  * side of the plane.  *  * @lucene.experimental  */
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
comment|/** The sign value for evaluation of a point on the correct side of the plane */
DECL|field|sigNum
specifier|public
specifier|final
name|double
name|sigNum
decl_stmt|;
comment|/**    * Construct a SidedPlane identical to an existing one, but reversed.    *    * @param sidedPlane is the existing plane.    */
DECL|method|SidedPlane
specifier|public
name|SidedPlane
parameter_list|(
specifier|final
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
comment|/**    * Construct a sided plane from a pair of vectors describing points, and including    * origin, plus a point p which describes the side.    *    * @param p point to evaluate    * @param A is the first in-plane point    * @param B is the second in-plane point    */
DECL|method|SidedPlane
specifier|public
name|SidedPlane
parameter_list|(
specifier|final
name|Vector
name|p
parameter_list|,
specifier|final
name|Vector
name|A
parameter_list|,
specifier|final
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
if|if
condition|(
name|sigNum
operator|==
literal|0.0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot determine sidedness because check point is on plane."
argument_list|)
throw|;
block|}
comment|/**    * Construct a sided plane from a pair of vectors describing points, and including    * origin, plus a point p which describes the side.    *    * @param p point to evaluate    * @param onSide is true if the point is on the correct side of the plane, false otherwise.    * @param A is the first in-plane point    * @param B is the second in-plane point    */
DECL|method|SidedPlane
specifier|public
name|SidedPlane
parameter_list|(
specifier|final
name|Vector
name|p
parameter_list|,
specifier|final
name|boolean
name|onSide
parameter_list|,
specifier|final
name|Vector
name|A
parameter_list|,
specifier|final
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
name|onSide
condition|?
name|Math
operator|.
name|signum
argument_list|(
name|evaluate
argument_list|(
name|p
argument_list|)
argument_list|)
else|:
operator|-
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
if|if
condition|(
name|sigNum
operator|==
literal|0.0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot determine sidedness because check point is on plane."
argument_list|)
throw|;
block|}
comment|/**    * Construct a sided plane from a point and a Z coordinate.    *    * @param p      point to evaluate.    * @param planetModel is the planet model.    * @param sinLat is the sin of the latitude of the plane.    */
DECL|method|SidedPlane
specifier|public
name|SidedPlane
parameter_list|(
name|Vector
name|p
parameter_list|,
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
name|double
name|sinLat
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|,
name|sinLat
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
if|if
condition|(
name|sigNum
operator|==
literal|0.0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot determine sidedness because check point is on plane."
argument_list|)
throw|;
block|}
comment|/**    * Construct a sided vertical plane from a point and specified x and y coordinates.    *    * @param p point to evaluate.    * @param x is the specified x.    * @param y is the specified y.    */
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
if|if
condition|(
name|sigNum
operator|==
literal|0.0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot determine sidedness because check point is on plane."
argument_list|)
throw|;
block|}
comment|/**    * Construct a sided plane with a normal vector and offset.    *    * @param p point to evaluate.    * @param v is the normal vector.    * @param D is the origin offset for the plan.    */
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
if|if
condition|(
name|sigNum
operator|==
literal|0.0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot determine sidedness because check point is on plane."
argument_list|)
throw|;
block|}
comment|/**    * Construct a sided plane with a normal vector and offset.    *    * @param pX X coord of point to evaluate.    * @param pY Y coord of point to evaluate.    * @param pZ Z coord of point to evaluate.    * @param v is the normal vector.    * @param D is the origin offset for the plan.    */
DECL|method|SidedPlane
specifier|public
name|SidedPlane
parameter_list|(
name|double
name|pX
parameter_list|,
name|double
name|pY
parameter_list|,
name|double
name|pZ
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
name|pX
argument_list|,
name|pY
argument_list|,
name|pZ
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sigNum
operator|==
literal|0.0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot determine sidedness because check point is on plane."
argument_list|)
throw|;
block|}
comment|/** Construct a sided plane from two points and a third normal vector.    */
DECL|method|constructNormalizedPerpendicularSidedPlane
specifier|public
specifier|static
name|SidedPlane
name|constructNormalizedPerpendicularSidedPlane
parameter_list|(
specifier|final
name|Vector
name|insidePoint
parameter_list|,
specifier|final
name|Vector
name|normalVector
parameter_list|,
specifier|final
name|Vector
name|point1
parameter_list|,
specifier|final
name|Vector
name|point2
parameter_list|)
block|{
specifier|final
name|Vector
name|pointsVector
init|=
operator|new
name|Vector
argument_list|(
name|point1
operator|.
name|x
operator|-
name|point2
operator|.
name|x
argument_list|,
name|point1
operator|.
name|y
operator|-
name|point2
operator|.
name|y
argument_list|,
name|point1
operator|.
name|z
operator|-
name|point2
operator|.
name|z
argument_list|)
decl_stmt|;
specifier|final
name|Vector
name|newNormalVector
init|=
operator|new
name|Vector
argument_list|(
name|normalVector
argument_list|,
name|pointsVector
argument_list|)
decl_stmt|;
try|try
block|{
comment|// To construct the plane, we now just need D, which is simply the negative of the evaluation of the circle normal vector at one of the points.
return|return
operator|new
name|SidedPlane
argument_list|(
name|insidePoint
argument_list|,
name|newNormalVector
argument_list|,
operator|-
name|newNormalVector
operator|.
name|dotProduct
argument_list|(
name|point1
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** Construct a sided plane from three points.    */
DECL|method|constructNormalizedThreePointSidedPlane
specifier|public
specifier|static
name|SidedPlane
name|constructNormalizedThreePointSidedPlane
parameter_list|(
specifier|final
name|Vector
name|insidePoint
parameter_list|,
specifier|final
name|Vector
name|point1
parameter_list|,
specifier|final
name|Vector
name|point2
parameter_list|,
specifier|final
name|Vector
name|point3
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Vector
name|planeNormal
init|=
operator|new
name|Vector
argument_list|(
operator|new
name|Vector
argument_list|(
name|point1
operator|.
name|x
operator|-
name|point2
operator|.
name|x
argument_list|,
name|point1
operator|.
name|y
operator|-
name|point2
operator|.
name|y
argument_list|,
name|point1
operator|.
name|z
operator|-
name|point2
operator|.
name|z
argument_list|)
argument_list|,
operator|new
name|Vector
argument_list|(
name|point2
operator|.
name|x
operator|-
name|point3
operator|.
name|x
argument_list|,
name|point2
operator|.
name|y
operator|-
name|point3
operator|.
name|y
argument_list|,
name|point2
operator|.
name|z
operator|-
name|point3
operator|.
name|z
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|SidedPlane
argument_list|(
name|insidePoint
argument_list|,
name|planeNormal
argument_list|,
operator|-
name|planeNormal
operator|.
name|dotProduct
argument_list|(
name|point2
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
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
name|evalResult
init|=
name|evaluate
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|evalResult
argument_list|)
operator|<
name|MINIMUM_RESOLUTION
condition|)
return|return
literal|true
return|;
name|double
name|sigNum
init|=
name|Math
operator|.
name|signum
argument_list|(
name|evalResult
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
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SidedPlane
operator|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|SidedPlane
name|that
init|=
operator|(
name|SidedPlane
operator|)
name|o
decl_stmt|;
return|return
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|sigNum
argument_list|,
name|sigNum
argument_list|)
operator|==
literal|0
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
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|sigNum
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
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
