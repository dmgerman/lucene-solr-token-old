begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo3d
package|;
end_package
begin_comment
comment|/**  * Degenerate bounding box wider than PI and limited on two sides (left lon, right lon).  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|GeoWideDegenerateHorizontalLine
specifier|public
class|class
name|GeoWideDegenerateHorizontalLine
extends|extends
name|GeoBaseBBox
block|{
comment|/** The latitude of the line */
DECL|field|latitude
specifier|protected
specifier|final
name|double
name|latitude
decl_stmt|;
comment|/** The left longitude cutoff of the line */
DECL|field|leftLon
specifier|protected
specifier|final
name|double
name|leftLon
decl_stmt|;
comment|/** The right longitude cutoff of the line */
DECL|field|rightLon
specifier|protected
specifier|final
name|double
name|rightLon
decl_stmt|;
comment|/** The left end of the line */
DECL|field|LHC
specifier|protected
specifier|final
name|GeoPoint
name|LHC
decl_stmt|;
comment|/** The right end of the line */
DECL|field|RHC
specifier|protected
specifier|final
name|GeoPoint
name|RHC
decl_stmt|;
comment|/** The plane the line is in */
DECL|field|plane
specifier|protected
specifier|final
name|Plane
name|plane
decl_stmt|;
comment|/** The left cutoff plane */
DECL|field|leftPlane
specifier|protected
specifier|final
name|SidedPlane
name|leftPlane
decl_stmt|;
comment|/** The right cutoff plane */
DECL|field|rightPlane
specifier|protected
specifier|final
name|SidedPlane
name|rightPlane
decl_stmt|;
comment|/** Notable points for the line */
DECL|field|planePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|planePoints
decl_stmt|;
comment|/** Center point for the line */
DECL|field|centerPoint
specifier|protected
specifier|final
name|GeoPoint
name|centerPoint
decl_stmt|;
comment|/** Left/right combination bound */
DECL|field|eitherBound
specifier|protected
specifier|final
name|EitherBound
name|eitherBound
decl_stmt|;
comment|/** A point on the line */
DECL|field|edgePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|edgePoints
decl_stmt|;
comment|/**    * Accepts only values in the following ranges: lat: {@code -PI/2 -> PI/2}, lon: {@code -PI -> PI}.    * Horizontal angle must be greater than or equal to PI.    *@param planetModel is the planet model.    *@param latitude is the line latitude.    *@param leftLon is the left cutoff longitude.    *@param rightLon is the right cutoff longitude.    */
DECL|method|GeoWideDegenerateHorizontalLine
specifier|public
name|GeoWideDegenerateHorizontalLine
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|latitude
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
name|latitude
operator|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|||
name|latitude
operator|<
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Latitude out of range"
argument_list|)
throw|;
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
operator|<
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Width of rectangle too small"
argument_list|)
throw|;
name|this
operator|.
name|latitude
operator|=
name|latitude
expr_stmt|;
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
name|sinLatitude
init|=
name|Math
operator|.
name|sin
argument_list|(
name|latitude
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosLatitude
init|=
name|Math
operator|.
name|cos
argument_list|(
name|latitude
argument_list|)
decl_stmt|;
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
comment|// Now build the two points
name|this
operator|.
name|LHC
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|sinLatitude
argument_list|,
name|sinLeftLon
argument_list|,
name|cosLatitude
argument_list|,
name|cosLeftLon
argument_list|,
name|latitude
argument_list|,
name|leftLon
argument_list|)
expr_stmt|;
name|this
operator|.
name|RHC
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|sinLatitude
argument_list|,
name|sinRightLon
argument_list|,
name|cosLatitude
argument_list|,
name|cosRightLon
argument_list|,
name|latitude
argument_list|,
name|rightLon
argument_list|)
expr_stmt|;
name|this
operator|.
name|plane
operator|=
operator|new
name|Plane
argument_list|(
name|planetModel
argument_list|,
name|sinLatitude
argument_list|)
expr_stmt|;
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
name|double
name|sinMiddleLon
init|=
name|Math
operator|.
name|sin
argument_list|(
name|middleLon
argument_list|)
decl_stmt|;
name|double
name|cosMiddleLon
init|=
name|Math
operator|.
name|cos
argument_list|(
name|middleLon
argument_list|)
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
name|sinLatitude
argument_list|,
name|sinMiddleLon
argument_list|,
name|cosLatitude
argument_list|,
name|cosMiddleLon
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
name|LHC
block|,
name|RHC
block|}
expr_stmt|;
name|this
operator|.
name|eitherBound
operator|=
operator|new
name|EitherBound
argument_list|()
expr_stmt|;
name|this
operator|.
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|centerPoint
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
specifier|final
name|double
name|newTopLat
init|=
name|latitude
operator|+
name|angle
decl_stmt|;
specifier|final
name|double
name|newBottomLat
init|=
name|latitude
operator|-
name|angle
decl_stmt|;
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
name|newTopLat
argument_list|,
name|newBottomLat
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
name|plane
operator|.
name|evaluateIsZero
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|&&
operator|(
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
operator|||
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
operator|)
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
comment|// Here we compute the distance from the middle point to one of the corners.  However, we need to be careful
comment|// to use the longest of three distances: the distance to a corner on the top; the distnace to a corner on the bottom, and
comment|// the distance to the right or left edge from the center.
specifier|final
name|double
name|topAngle
init|=
name|centerPoint
operator|.
name|arcDistance
argument_list|(
name|RHC
argument_list|)
decl_stmt|;
specifier|final
name|double
name|bottomAngle
init|=
name|centerPoint
operator|.
name|arcDistance
argument_list|(
name|LHC
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
name|topAngle
argument_list|,
name|bottomAngle
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
comment|// Right and left bounds are essentially independent hemispheres; crossing into the wrong part of one
comment|// requires crossing into the right part of the other.  So intersection can ignore the left/right bounds.
return|return
name|p
operator|.
name|intersects
argument_list|(
name|planetModel
argument_list|,
name|plane
argument_list|,
name|notablePoints
argument_list|,
name|planePoints
argument_list|,
name|bounds
argument_list|,
name|eitherBound
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBounds
specifier|public
name|void
name|getBounds
parameter_list|(
name|Bounds
name|bounds
parameter_list|)
block|{
name|super
operator|.
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
name|bounds
operator|.
name|isWide
argument_list|()
operator|.
name|addHorizontalPlane
argument_list|(
name|planetModel
argument_list|,
name|latitude
argument_list|,
name|plane
argument_list|,
name|eitherBound
argument_list|)
operator|.
name|addPoint
argument_list|(
name|LHC
argument_list|)
operator|.
name|addPoint
argument_list|(
name|RHC
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|path
operator|.
name|intersects
argument_list|(
name|plane
argument_list|,
name|planePoints
argument_list|,
name|eitherBound
argument_list|)
condition|)
block|{
return|return
name|OVERLAPS
return|;
block|}
if|if
condition|(
name|path
operator|.
name|isWithin
argument_list|(
name|centerPoint
argument_list|)
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
name|distance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
argument_list|,
name|plane
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|,
name|eitherBound
argument_list|)
decl_stmt|;
specifier|final
name|double
name|LHCDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|LHC
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
name|RHCDistance
init|=
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|RHC
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
name|distance
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|LHCDistance
argument_list|,
name|RHCDistance
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
name|GeoWideDegenerateHorizontalLine
operator|)
condition|)
return|return
literal|false
return|;
name|GeoWideDegenerateHorizontalLine
name|other
init|=
operator|(
name|GeoWideDegenerateHorizontalLine
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
name|LHC
operator|.
name|equals
argument_list|(
name|LHC
argument_list|)
operator|&&
name|other
operator|.
name|RHC
operator|.
name|equals
argument_list|(
name|RHC
argument_list|)
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
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|LHC
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|RHC
operator|.
name|hashCode
argument_list|()
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
literal|"GeoWideDegenerateHorizontalLine: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", latitude="
operator|+
name|latitude
operator|+
literal|"("
operator|+
name|latitude
operator|*
literal|180.0
operator|/
name|Math
operator|.
name|PI
operator|+
literal|"), leftlon="
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
literal|"), rightLon="
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
comment|/** Membership implementation representing a wide cutoff (more than 180 degrees).    */
DECL|class|EitherBound
specifier|protected
class|class
name|EitherBound
implements|implements
name|Membership
block|{
comment|/** Constructor.      */
DECL|method|EitherBound
specifier|public
name|EitherBound
parameter_list|()
block|{     }
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
operator|||
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
block|}
block|}
end_class
end_unit
