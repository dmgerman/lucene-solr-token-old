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
comment|/** This GeoBBox represents an area rectangle limited only in north latitude. */
end_comment
begin_class
DECL|class|GeoSouthLatitudeZone
specifier|public
class|class
name|GeoSouthLatitudeZone
extends|extends
name|GeoBBoxBase
block|{
DECL|field|topLat
specifier|public
specifier|final
name|double
name|topLat
decl_stmt|;
DECL|field|cosTopLat
specifier|public
specifier|final
name|double
name|cosTopLat
decl_stmt|;
DECL|field|topPlane
specifier|public
specifier|final
name|SidedPlane
name|topPlane
decl_stmt|;
DECL|field|interiorPoint
specifier|public
specifier|final
name|GeoPoint
name|interiorPoint
decl_stmt|;
DECL|field|planePoints
specifier|public
specifier|final
specifier|static
name|GeoPoint
index|[]
name|planePoints
init|=
operator|new
name|GeoPoint
index|[
literal|0
index|]
decl_stmt|;
DECL|field|topBoundaryPoint
specifier|public
specifier|final
name|GeoPoint
name|topBoundaryPoint
decl_stmt|;
comment|// Edge points
DECL|field|edgePoints
specifier|public
specifier|final
name|GeoPoint
index|[]
name|edgePoints
decl_stmt|;
DECL|method|GeoSouthLatitudeZone
specifier|public
name|GeoSouthLatitudeZone
parameter_list|(
specifier|final
name|double
name|topLat
parameter_list|)
block|{
name|this
operator|.
name|topLat
operator|=
name|topLat
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
name|this
operator|.
name|cosTopLat
operator|=
name|Math
operator|.
name|cos
argument_list|(
name|topLat
argument_list|)
expr_stmt|;
comment|// Construct sample points, so we get our sidedness right
specifier|final
name|Vector
name|topPoint
init|=
operator|new
name|Vector
argument_list|(
literal|0.0
argument_list|,
literal|0.0
argument_list|,
name|sinTopLat
argument_list|)
decl_stmt|;
comment|// Compute an interior point.  Pick one whose lat is between top and bottom.
specifier|final
name|double
name|middleLat
init|=
operator|(
name|topLat
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
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
name|this
operator|.
name|interiorPoint
operator|=
operator|new
name|GeoPoint
argument_list|(
name|Math
operator|.
name|sqrt
argument_list|(
literal|1.0
operator|-
name|sinMiddleLat
operator|*
name|sinMiddleLat
argument_list|)
argument_list|,
literal|0.0
argument_list|,
name|sinMiddleLat
argument_list|)
expr_stmt|;
name|this
operator|.
name|topBoundaryPoint
operator|=
operator|new
name|GeoPoint
argument_list|(
name|Math
operator|.
name|sqrt
argument_list|(
literal|1.0
operator|-
name|sinTopLat
operator|*
name|sinTopLat
argument_list|)
argument_list|,
literal|0.0
argument_list|,
name|sinTopLat
argument_list|)
expr_stmt|;
name|this
operator|.
name|topPlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|interiorPoint
argument_list|,
name|sinTopLat
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
name|topBoundaryPoint
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
decl_stmt|;
return|return
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|newTopLat
argument_list|,
name|newBottomLat
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|,
name|Math
operator|.
name|PI
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
name|topPlane
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
comment|// This is a bit tricky.  I guess we should interpret this as meaning the angle of a circle that
comment|// would contain all the bounding box points, when starting in the "center".
if|if
condition|(
name|topLat
operator|>
literal|0.0
condition|)
return|return
name|Math
operator|.
name|PI
return|;
name|double
name|maxCosLat
init|=
name|cosTopLat
decl_stmt|;
return|return
name|maxCosLat
operator|*
name|Math
operator|.
name|PI
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
name|topPlane
argument_list|,
name|notablePoints
argument_list|,
name|planePoints
argument_list|,
name|bounds
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
name|noLongitudeBound
argument_list|()
operator|.
name|addLatitudeZone
argument_list|(
name|topLat
argument_list|)
operator|.
name|noBottomLatitudeBound
argument_list|()
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
name|topBoundaryPoint
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
comment|// Second, the shortcut of seeing whether endpoints are in/out is not going to
comment|// work with no area endpoints.  So we rely entirely on intersections.
if|if
condition|(
name|path
operator|.
name|intersects
argument_list|(
name|topPlane
argument_list|,
name|planePoints
argument_list|)
condition|)
return|return
name|OVERLAPS
return|;
comment|// There is another case for latitude zones only.  This is when the boundaries of the shape all fit
comment|// within the zone, but the shape includes areas outside the zone crossing a pole.
comment|// In this case, the above "overlaps" check is insufficient.  We also need to check a point on either boundary
comment|// whether it is within the shape.  If both such points are within, then CONTAINS is the right answer.  If
comment|// one such point is within, then OVERLAPS is the right answer.
if|if
condition|(
name|insideShape
condition|)
return|return
name|CONTAINS
return|;
if|if
condition|(
name|insideRectangle
operator|==
name|ALL_INSIDE
condition|)
return|return
name|WITHIN
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
name|GeoSouthLatitudeZone
operator|)
condition|)
return|return
literal|false
return|;
name|GeoSouthLatitudeZone
name|other
init|=
operator|(
name|GeoSouthLatitudeZone
operator|)
name|o
decl_stmt|;
return|return
name|other
operator|.
name|topPlane
operator|.
name|equals
argument_list|(
name|topPlane
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
name|topPlane
operator|.
name|hashCode
argument_list|()
decl_stmt|;
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
literal|"GeoSouthLatitudeZone: {toplat="
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
literal|")}"
return|;
block|}
block|}
end_class
end_unit
