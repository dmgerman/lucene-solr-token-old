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
comment|/** Degenerate bounding box limited on two sides (top lat, bottom lat). */
end_comment
begin_class
DECL|class|GeoDegenerateVerticalLine
specifier|public
class|class
name|GeoDegenerateVerticalLine
extends|extends
name|GeoBBoxBase
block|{
DECL|field|topLat
specifier|public
specifier|final
name|double
name|topLat
decl_stmt|;
DECL|field|bottomLat
specifier|public
specifier|final
name|double
name|bottomLat
decl_stmt|;
DECL|field|longitude
specifier|public
specifier|final
name|double
name|longitude
decl_stmt|;
DECL|field|UHC
specifier|public
specifier|final
name|GeoPoint
name|UHC
decl_stmt|;
DECL|field|LHC
specifier|public
specifier|final
name|GeoPoint
name|LHC
decl_stmt|;
DECL|field|topPlane
specifier|public
specifier|final
name|SidedPlane
name|topPlane
decl_stmt|;
DECL|field|bottomPlane
specifier|public
specifier|final
name|SidedPlane
name|bottomPlane
decl_stmt|;
DECL|field|boundingPlane
specifier|public
specifier|final
name|SidedPlane
name|boundingPlane
decl_stmt|;
DECL|field|plane
specifier|public
specifier|final
name|Plane
name|plane
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
comment|/** Accepts only values in the following ranges: lat: {@code -PI/2 -> PI/2}, longitude: {@code -PI -> PI} */
DECL|method|GeoDegenerateVerticalLine
specifier|public
name|GeoDegenerateVerticalLine
parameter_list|(
specifier|final
name|double
name|topLat
parameter_list|,
specifier|final
name|double
name|bottomLat
parameter_list|,
specifier|final
name|double
name|longitude
parameter_list|)
block|{
comment|// Argument checking
if|if
condition|(
name|topLat
operator|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|||
name|topLat
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
literal|"Top latitude out of range"
argument_list|)
throw|;
if|if
condition|(
name|bottomLat
operator|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|||
name|bottomLat
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
literal|"Bottom latitude out of range"
argument_list|)
throw|;
if|if
condition|(
name|topLat
operator|<
name|bottomLat
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Top latitude less than bottom latitude"
argument_list|)
throw|;
if|if
condition|(
name|longitude
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|longitude
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Longitude out of range"
argument_list|)
throw|;
name|this
operator|.
name|topLat
operator|=
name|topLat
expr_stmt|;
name|this
operator|.
name|bottomLat
operator|=
name|bottomLat
expr_stmt|;
name|this
operator|.
name|longitude
operator|=
name|longitude
expr_stmt|;
specifier|final
name|double
name|sinTopLat
init|=
name|Math
operator|.
name|sin
argument_list|(
name|topLat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosTopLat
init|=
name|Math
operator|.
name|cos
argument_list|(
name|topLat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinBottomLat
init|=
name|Math
operator|.
name|sin
argument_list|(
name|bottomLat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosBottomLat
init|=
name|Math
operator|.
name|cos
argument_list|(
name|bottomLat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinLongitude
init|=
name|Math
operator|.
name|sin
argument_list|(
name|longitude
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosLongitude
init|=
name|Math
operator|.
name|cos
argument_list|(
name|longitude
argument_list|)
decl_stmt|;
comment|// Now build the two points
name|this
operator|.
name|UHC
operator|=
operator|new
name|GeoPoint
argument_list|(
name|sinTopLat
argument_list|,
name|sinLongitude
argument_list|,
name|cosTopLat
argument_list|,
name|cosLongitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|LHC
operator|=
operator|new
name|GeoPoint
argument_list|(
name|sinBottomLat
argument_list|,
name|sinLongitude
argument_list|,
name|cosBottomLat
argument_list|,
name|cosLongitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|plane
operator|=
operator|new
name|Plane
argument_list|(
name|cosLongitude
argument_list|,
name|sinLongitude
argument_list|)
expr_stmt|;
specifier|final
name|double
name|middleLat
init|=
operator|(
name|topLat
operator|+
name|bottomLat
operator|)
operator|*
literal|0.5
decl_stmt|;
specifier|final
name|double
name|sinMiddleLat
init|=
name|Math
operator|.
name|sin
argument_list|(
name|middleLat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosMiddleLat
init|=
name|Math
operator|.
name|cos
argument_list|(
name|middleLat
argument_list|)
decl_stmt|;
name|this
operator|.
name|centerPoint
operator|=
operator|new
name|GeoPoint
argument_list|(
name|sinMiddleLat
argument_list|,
name|sinLongitude
argument_list|,
name|cosMiddleLat
argument_list|,
name|cosLongitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|topPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|centerPoint
argument_list|,
name|sinTopLat
argument_list|)
expr_stmt|;
name|this
operator|.
name|bottomPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|centerPoint
argument_list|,
name|sinBottomLat
argument_list|)
expr_stmt|;
name|this
operator|.
name|boundingPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|centerPoint
argument_list|,
operator|-
name|sinLongitude
argument_list|,
name|cosLongitude
argument_list|)
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
name|topLat
operator|+
name|angle
decl_stmt|;
specifier|final
name|double
name|newBottomLat
init|=
name|bottomLat
operator|-
name|angle
decl_stmt|;
name|double
name|newLeftLon
init|=
name|longitude
operator|-
name|angle
decl_stmt|;
name|double
name|newRightLon
init|=
name|longitude
operator|+
name|angle
decl_stmt|;
name|double
name|currentLonSpan
init|=
literal|2.0
operator|*
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
name|Vector
name|point
parameter_list|)
block|{
return|return
name|plane
operator|.
name|evaluate
argument_list|(
name|point
argument_list|)
operator|==
literal|0.0
operator|&&
name|boundingPlane
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
operator|&&
name|topPlane
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
operator|&&
name|bottomPlane
operator|.
name|isWithin
argument_list|(
name|point
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
name|evaluate
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
operator|==
literal|0.0
operator|&&
name|boundingPlane
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
name|topPlane
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
name|bottomPlane
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
name|UHC
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
name|plane
argument_list|,
name|bounds
argument_list|,
name|boundingPlane
argument_list|,
name|topPlane
argument_list|,
name|bottomPlane
argument_list|)
return|;
block|}
comment|/** Compute longitude/latitude bounds for the shape.     *@param bounds is the optional input bounds object.  If this is null,     * a bounds object will be created.  Otherwise, the input object will be modified.     *@return a Bounds object describing the shape's bounds.  If the bounds cannot     * be computed, then return a Bounds object with noLongitudeBound,     * noTopLatitudeBound, and noBottomLatitudeBound.     */
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
name|addLatitudeZone
argument_list|(
name|topLat
argument_list|)
operator|.
name|addLatitudeZone
argument_list|(
name|bottomLat
argument_list|)
operator|.
name|addLongitudeSlice
argument_list|(
name|longitude
argument_list|,
name|longitude
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
if|if
condition|(
name|path
operator|.
name|intersects
argument_list|(
name|plane
argument_list|,
name|boundingPlane
argument_list|,
name|topPlane
argument_list|,
name|bottomPlane
argument_list|)
condition|)
return|return
name|OVERLAPS
return|;
if|if
condition|(
name|path
operator|.
name|isWithin
argument_list|(
name|centerPoint
argument_list|)
condition|)
return|return
name|CONTAINS
return|;
return|return
name|DISJOINT
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
name|GeoDegenerateVerticalLine
operator|)
condition|)
return|return
literal|false
return|;
name|GeoDegenerateVerticalLine
name|other
init|=
operator|(
name|GeoDegenerateVerticalLine
operator|)
name|o
decl_stmt|;
return|return
name|other
operator|.
name|UHC
operator|.
name|equals
argument_list|(
name|UHC
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
name|UHC
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
literal|"GeoDegenerateVerticalLine: {longitude="
operator|+
name|longitude
operator|+
literal|"("
operator|+
name|longitude
operator|*
literal|180.0
operator|/
name|Math
operator|.
name|PI
operator|+
literal|"), toplat="
operator|+
name|topLat
operator|+
literal|"("
operator|+
name|topLat
operator|*
literal|180.0
operator|/
name|Math
operator|.
name|PI
operator|+
literal|"), bottomlat="
operator|+
name|bottomLat
operator|+
literal|"("
operator|+
name|bottomLat
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
