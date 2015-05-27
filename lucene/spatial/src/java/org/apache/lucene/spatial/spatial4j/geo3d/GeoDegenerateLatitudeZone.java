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
comment|/**  * This GeoBBox represents an area rectangle of one specific latitude with  * no longitude bounds.  *  * @lucene.internal  */
end_comment
begin_class
DECL|class|GeoDegenerateLatitudeZone
specifier|public
class|class
name|GeoDegenerateLatitudeZone
extends|extends
name|GeoBaseBBox
block|{
DECL|field|latitude
specifier|public
specifier|final
name|double
name|latitude
decl_stmt|;
DECL|field|sinLatitude
specifier|public
specifier|final
name|double
name|sinLatitude
decl_stmt|;
DECL|field|plane
specifier|public
specifier|final
name|Plane
name|plane
decl_stmt|;
DECL|field|interiorPoint
specifier|public
specifier|final
name|GeoPoint
name|interiorPoint
decl_stmt|;
DECL|field|edgePoints
specifier|public
specifier|final
name|GeoPoint
index|[]
name|edgePoints
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
DECL|method|GeoDegenerateLatitudeZone
specifier|public
name|GeoDegenerateLatitudeZone
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|latitude
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
name|this
operator|.
name|latitude
operator|=
name|latitude
expr_stmt|;
name|this
operator|.
name|sinLatitude
operator|=
name|Math
operator|.
name|sin
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
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
comment|// Compute an interior point.
name|interiorPoint
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|sinLatitude
argument_list|,
literal|0.0
argument_list|,
name|cosLatitude
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|interiorPoint
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
name|double
name|newTopLat
init|=
name|latitude
operator|+
name|angle
decl_stmt|;
name|double
name|newBottomLat
init|=
name|latitude
operator|-
name|angle
decl_stmt|;
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
name|Math
operator|.
name|abs
argument_list|(
name|point
operator|.
name|z
operator|-
name|this
operator|.
name|sinLatitude
argument_list|)
operator|<
literal|1e-10
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
name|Math
operator|.
name|abs
argument_list|(
name|z
operator|-
name|this
operator|.
name|sinLatitude
argument_list|)
operator|<
literal|1e-10
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
return|return
name|Math
operator|.
name|PI
return|;
block|}
comment|/**    * Returns the center of a circle into which the area will be inscribed.    *    * @return the center.    */
annotation|@
name|Override
DECL|method|getCenter
specifier|public
name|GeoPoint
name|getCenter
parameter_list|()
block|{
comment|// Totally arbitrary
return|return
name|interiorPoint
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
name|plane
argument_list|,
name|notablePoints
argument_list|,
name|planePoints
argument_list|,
name|bounds
argument_list|)
return|;
block|}
comment|/**    * Compute longitude/latitude bounds for the shape.    *    * @param bounds is the optional input bounds object.  If this is null,    *               a bounds object will be created.  Otherwise, the input object will be modified.    * @return a Bounds object describing the shape's bounds.  If the bounds cannot    * be computed, then return a Bounds object with noLongitudeBound,    * noTopLatitudeBound, and noBottomLatitudeBound.    */
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
name|latitude
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
comment|// Second, the shortcut of seeing whether endpoints are in/out is not going to
comment|// work with no area endpoints.  So we rely entirely on intersections.
comment|//System.out.println("Got here! latitude="+latitude+" path="+path);
if|if
condition|(
name|path
operator|.
name|intersects
argument_list|(
name|plane
argument_list|,
name|planePoints
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
name|interiorPoint
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
name|GeoDegenerateLatitudeZone
operator|)
condition|)
return|return
literal|false
return|;
name|GeoDegenerateLatitudeZone
name|other
init|=
operator|(
name|GeoDegenerateLatitudeZone
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
name|latitude
operator|==
name|latitude
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
name|latitude
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
literal|"GeoDegenerateLatitudeZone: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", lat="
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
literal|")}"
return|;
block|}
block|}
end_class
end_unit
