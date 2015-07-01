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
comment|/**  * Bounding box limited on left and right.  * The left-right maximum extent for this shape is PI; for anything larger, use  * {@link GeoWideLongitudeSlice}.  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|GeoLongitudeSlice
specifier|public
class|class
name|GeoLongitudeSlice
extends|extends
name|GeoBaseBBox
block|{
DECL|field|leftLon
specifier|public
specifier|final
name|double
name|leftLon
decl_stmt|;
DECL|field|rightLon
specifier|public
specifier|final
name|double
name|rightLon
decl_stmt|;
DECL|field|leftPlane
specifier|public
specifier|final
name|SidedPlane
name|leftPlane
decl_stmt|;
DECL|field|rightPlane
specifier|public
specifier|final
name|SidedPlane
name|rightPlane
decl_stmt|;
DECL|field|planePoints
specifier|public
specifier|final
name|GeoPoint
index|[]
name|planePoints
decl_stmt|;
DECL|field|centerPoint
specifier|public
specifier|final
name|GeoPoint
name|centerPoint
decl_stmt|;
DECL|field|edgePoints
specifier|public
specifier|final
name|GeoPoint
index|[]
name|edgePoints
decl_stmt|;
comment|/**    * Accepts only values in the following ranges: lon: {@code -PI -> PI}    */
DECL|method|GeoLongitudeSlice
specifier|public
name|GeoLongitudeSlice
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|leftLon
parameter_list|,
name|double
name|rightLon
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
comment|// Argument checking
if|if
condition|(
name|leftLon
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|leftLon
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Left longitude out of range"
argument_list|)
throw|;
if|if
condition|(
name|rightLon
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|rightLon
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Right longitude out of range"
argument_list|)
throw|;
name|double
name|extent
init|=
name|rightLon
operator|-
name|leftLon
decl_stmt|;
if|if
condition|(
name|extent
operator|<
literal|0.0
condition|)
block|{
name|extent
operator|+=
literal|2.0
operator|*
name|Math
operator|.
name|PI
expr_stmt|;
block|}
if|if
condition|(
name|extent
operator|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Width of rectangle too great"
argument_list|)
throw|;
name|this
operator|.
name|leftLon
operator|=
name|leftLon
expr_stmt|;
name|this
operator|.
name|rightLon
operator|=
name|rightLon
expr_stmt|;
specifier|final
name|double
name|sinLeftLon
init|=
name|Math
operator|.
name|sin
argument_list|(
name|leftLon
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosLeftLon
init|=
name|Math
operator|.
name|cos
argument_list|(
name|leftLon
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinRightLon
init|=
name|Math
operator|.
name|sin
argument_list|(
name|rightLon
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosRightLon
init|=
name|Math
operator|.
name|cos
argument_list|(
name|rightLon
argument_list|)
decl_stmt|;
comment|// Normalize
while|while
condition|(
name|leftLon
operator|>
name|rightLon
condition|)
block|{
name|rightLon
operator|+=
name|Math
operator|.
name|PI
operator|*
literal|2.0
expr_stmt|;
block|}
specifier|final
name|double
name|middleLon
init|=
operator|(
name|leftLon
operator|+
name|rightLon
operator|)
operator|*
literal|0.5
decl_stmt|;
name|this
operator|.
name|centerPoint
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
literal|0.0
argument_list|,
name|middleLon
argument_list|)
expr_stmt|;
name|this
operator|.
name|leftPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|centerPoint
argument_list|,
name|cosLeftLon
argument_list|,
name|sinLeftLon
argument_list|)
expr_stmt|;
name|this
operator|.
name|rightPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|centerPoint
argument_list|,
name|cosRightLon
argument_list|,
name|sinRightLon
argument_list|)
expr_stmt|;
name|this
operator|.
name|planePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|planetModel
operator|.
name|NORTH_POLE
block|,
name|planetModel
operator|.
name|SOUTH_POLE
block|}
expr_stmt|;
name|this
operator|.
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|planetModel
operator|.
name|NORTH_POLE
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expand
specifier|public
name|GeoBBox
name|expand
parameter_list|(
specifier|final
name|double
name|angle
parameter_list|)
block|{
comment|// Figuring out when we escalate to a special case requires some prefiguring
name|double
name|currentLonSpan
init|=
name|rightLon
operator|-
name|leftLon
decl_stmt|;
if|if
condition|(
name|currentLonSpan
operator|<
literal|0.0
condition|)
name|currentLonSpan
operator|+=
name|Math
operator|.
name|PI
operator|*
literal|2.0
expr_stmt|;
name|double
name|newLeftLon
init|=
name|leftLon
operator|-
name|angle
decl_stmt|;
name|double
name|newRightLon
init|=
name|rightLon
operator|+
name|angle
decl_stmt|;
if|if
condition|(
name|currentLonSpan
operator|+
literal|2.0
operator|*
name|angle
operator|>=
name|Math
operator|.
name|PI
operator|*
literal|2.0
condition|)
block|{
name|newLeftLon
operator|=
operator|-
name|Math
operator|.
name|PI
expr_stmt|;
name|newRightLon
operator|=
name|Math
operator|.
name|PI
expr_stmt|;
block|}
return|return
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|planetModel
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
name|newLeftLon
argument_list|,
name|newRightLon
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
parameter_list|(
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
return|return
name|leftPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|&&
name|rightPlane
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRadius
specifier|public
name|double
name|getRadius
parameter_list|()
block|{
comment|// Compute the extent and divide by two
name|double
name|extent
init|=
name|rightLon
operator|-
name|leftLon
decl_stmt|;
if|if
condition|(
name|extent
operator|<
literal|0.0
condition|)
name|extent
operator|+=
name|Math
operator|.
name|PI
operator|*
literal|2.0
expr_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
name|extent
operator|*
literal|0.5
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCenter
specifier|public
name|GeoPoint
name|getCenter
parameter_list|()
block|{
return|return
name|centerPoint
return|;
block|}
annotation|@
name|Override
DECL|method|getEdgePoints
specifier|public
name|GeoPoint
index|[]
name|getEdgePoints
parameter_list|()
block|{
return|return
name|edgePoints
return|;
block|}
annotation|@
name|Override
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
specifier|final
name|Plane
name|p
parameter_list|,
specifier|final
name|GeoPoint
index|[]
name|notablePoints
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
return|return
name|p
operator|.
name|intersects
argument_list|(
name|planetModel
argument_list|,
name|leftPlane
argument_list|,
name|notablePoints
argument_list|,
name|planePoints
argument_list|,
name|bounds
argument_list|,
name|rightPlane
argument_list|)
operator|||
name|p
operator|.
name|intersects
argument_list|(
name|planetModel
argument_list|,
name|rightPlane
argument_list|,
name|notablePoints
argument_list|,
name|planePoints
argument_list|,
name|bounds
argument_list|,
name|leftPlane
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBounds
specifier|public
name|Bounds
name|getBounds
parameter_list|(
name|Bounds
name|bounds
parameter_list|)
block|{
if|if
condition|(
name|bounds
operator|==
literal|null
condition|)
name|bounds
operator|=
operator|new
name|Bounds
argument_list|()
expr_stmt|;
name|bounds
operator|.
name|noTopLatitudeBound
argument_list|()
operator|.
name|noBottomLatitudeBound
argument_list|()
expr_stmt|;
name|bounds
operator|.
name|addLongitudeSlice
argument_list|(
name|leftLon
argument_list|,
name|rightLon
argument_list|)
expr_stmt|;
return|return
name|bounds
return|;
block|}
annotation|@
name|Override
DECL|method|getRelationship
specifier|public
name|int
name|getRelationship
parameter_list|(
specifier|final
name|GeoShape
name|path
parameter_list|)
block|{
specifier|final
name|int
name|insideRectangle
init|=
name|isShapeInsideBBox
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|insideRectangle
operator|==
name|SOME_INSIDE
condition|)
return|return
name|OVERLAPS
return|;
specifier|final
name|boolean
name|insideShape
init|=
name|path
operator|.
name|isWithin
argument_list|(
name|planetModel
operator|.
name|NORTH_POLE
argument_list|)
decl_stmt|;
if|if
condition|(
name|insideRectangle
operator|==
name|ALL_INSIDE
operator|&&
name|insideShape
condition|)
return|return
name|OVERLAPS
return|;
if|if
condition|(
name|path
operator|.
name|intersects
argument_list|(
name|leftPlane
argument_list|,
name|planePoints
argument_list|,
name|rightPlane
argument_list|)
operator|||
name|path
operator|.
name|intersects
argument_list|(
name|rightPlane
argument_list|,
name|planePoints
argument_list|,
name|leftPlane
argument_list|)
condition|)
block|{
return|return
name|OVERLAPS
return|;
block|}
if|if
condition|(
name|insideRectangle
operator|==
name|ALL_INSIDE
condition|)
block|{
return|return
name|WITHIN
return|;
block|}
if|if
condition|(
name|insideShape
condition|)
block|{
return|return
name|CONTAINS
return|;
block|}
return|return
name|DISJOINT
return|;
block|}
annotation|@
name|Override
DECL|method|outsideDistance
specifier|protected
name|double
name|outsideDistance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
parameter_list|,
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
specifier|final
name|double
name|leftDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
argument_list|,
name|leftPlane
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|,
name|rightPlane
argument_list|)
decl_stmt|;
specifier|final
name|double
name|rightDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
argument_list|,
name|rightPlane
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|,
name|leftPlane
argument_list|)
decl_stmt|;
specifier|final
name|double
name|northDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
operator|.
name|NORTH_POLE
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
decl_stmt|;
specifier|final
name|double
name|southDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
operator|.
name|SOUTH_POLE
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|northDistance
argument_list|,
name|southDistance
argument_list|)
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|leftDistance
argument_list|,
name|rightDistance
argument_list|)
argument_list|)
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
operator|!
operator|(
name|o
operator|instanceof
name|GeoLongitudeSlice
operator|)
condition|)
return|return
literal|false
return|;
name|GeoLongitudeSlice
name|other
init|=
operator|(
name|GeoLongitudeSlice
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|&&
name|other
operator|.
name|leftLon
operator|==
name|leftLon
operator|&&
name|other
operator|.
name|rightLon
operator|==
name|rightLon
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
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|leftLon
argument_list|)
decl_stmt|;
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
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|rightLon
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
literal|"GeoLongitudeSlice: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", leftlon="
operator|+
name|leftLon
operator|+
literal|"("
operator|+
name|leftLon
operator|*
literal|180.0
operator|/
name|Math
operator|.
name|PI
operator|+
literal|"), rightlon="
operator|+
name|rightLon
operator|+
literal|"("
operator|+
name|rightLon
operator|*
literal|180.0
operator|/
name|Math
operator|.
name|PI
operator|+
literal|")}"
return|;
block|}
block|}
end_class
end_unit
