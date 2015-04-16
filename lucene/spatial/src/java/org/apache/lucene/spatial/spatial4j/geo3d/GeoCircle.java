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
comment|/** Circular area with a center and radius. */
end_comment
begin_class
DECL|class|GeoCircle
specifier|public
class|class
name|GeoCircle
extends|extends
name|GeoBaseExtendedShape
implements|implements
name|GeoDistanceShape
implements|,
name|GeoSizeable
block|{
DECL|field|center
specifier|public
specifier|final
name|GeoPoint
name|center
decl_stmt|;
DECL|field|cutoffAngle
specifier|public
specifier|final
name|double
name|cutoffAngle
decl_stmt|;
DECL|field|cutoffNormalDistance
specifier|public
specifier|final
name|double
name|cutoffNormalDistance
decl_stmt|;
DECL|field|cutoffLinearDistance
specifier|public
specifier|final
name|double
name|cutoffLinearDistance
decl_stmt|;
DECL|field|circlePlane
specifier|public
specifier|final
name|SidedPlane
name|circlePlane
decl_stmt|;
DECL|method|GeoCircle
specifier|public
name|GeoCircle
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|cutoffAngle
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
if|if
condition|(
name|lat
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|||
name|lat
argument_list|>
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
literal|"Latitude out of bounds"
argument_list|)
throw|;
if|if
condition|(
name|lon
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|lon
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Longitude out of bounds"
argument_list|)
throw|;
if|if
condition|(
name|cutoffAngle
argument_list|<
literal|0.0
operator|||
name|cutoffAngle
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cutoff angle out of bounds"
argument_list|)
throw|;
name|double
name|sinAngle
init|=
name|Math
operator|.
name|sin
argument_list|(
name|cutoffAngle
argument_list|)
decl_stmt|;
name|double
name|cosAngle
init|=
name|Math
operator|.
name|cos
argument_list|(
name|cutoffAngle
argument_list|)
decl_stmt|;
name|this
operator|.
name|center
operator|=
operator|new
name|GeoPoint
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
name|this
operator|.
name|cutoffNormalDistance
operator|=
name|sinAngle
expr_stmt|;
comment|// Need the chord distance.  This is just the chord distance: sqrt((1 - cos(angle))^2 + (sin(angle))^2).
name|double
name|xDiff
init|=
literal|1.0
operator|-
name|cosAngle
decl_stmt|;
name|this
operator|.
name|cutoffLinearDistance
operator|=
name|Math
operator|.
name|sqrt
argument_list|(
name|xDiff
operator|*
name|xDiff
operator|+
name|sinAngle
operator|*
name|sinAngle
argument_list|)
expr_stmt|;
name|this
operator|.
name|cutoffAngle
operator|=
name|cutoffAngle
expr_stmt|;
name|this
operator|.
name|circlePlane
operator|=
operator|new
name|SidedPlane
argument_list|(
name|center
argument_list|,
name|center
argument_list|,
operator|-
name|cosAngle
argument_list|)
expr_stmt|;
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
name|cutoffAngle
return|;
block|}
comment|/** Compute an estimate of "distance" to the GeoPoint.     * A return value of Double.MAX_VALUE should be returned for     * points outside of the shape.     */
annotation|@
name|Override
DECL|method|computeNormalDistance
specifier|public
name|double
name|computeNormalDistance
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
name|double
name|normalDistance
init|=
name|this
operator|.
name|center
operator|.
name|normalDistance
argument_list|(
name|point
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalDistance
operator|>
name|cutoffNormalDistance
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|normalDistance
return|;
block|}
comment|/** Compute an estimate of "distance" to the GeoPoint.     * A return value of Double.MAX_VALUE should be returned for     * points outside of the shape.     */
annotation|@
name|Override
DECL|method|computeNormalDistance
specifier|public
name|double
name|computeNormalDistance
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
name|normalDistance
init|=
name|this
operator|.
name|center
operator|.
name|normalDistance
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
name|normalDistance
operator|>
name|cutoffNormalDistance
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|normalDistance
return|;
block|}
comment|/** Compute a squared estimate of the "distance" to the     * GeoPoint.  Double.MAX_VALUE indicates a point outside of the     * shape.     */
annotation|@
name|Override
DECL|method|computeSquaredNormalDistance
specifier|public
name|double
name|computeSquaredNormalDistance
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
name|double
name|normalDistanceSquared
init|=
name|this
operator|.
name|center
operator|.
name|normalDistanceSquared
argument_list|(
name|point
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalDistanceSquared
operator|>
name|cutoffNormalDistance
operator|*
name|cutoffNormalDistance
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|normalDistanceSquared
return|;
block|}
comment|/** Compute a squared estimate of the "distance" to the     * GeoPoint.  Double.MAX_VALUE indicates a point outside of the     * shape.     */
annotation|@
name|Override
DECL|method|computeSquaredNormalDistance
specifier|public
name|double
name|computeSquaredNormalDistance
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
name|normalDistanceSquared
init|=
name|this
operator|.
name|center
operator|.
name|normalDistanceSquared
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
name|normalDistanceSquared
operator|>
name|cutoffNormalDistance
operator|*
name|cutoffNormalDistance
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|normalDistanceSquared
return|;
block|}
comment|/** Compute a linear distance to the vector.     * return Double.MAX_VALUE for points outside the shape.     */
annotation|@
name|Override
DECL|method|computeLinearDistance
specifier|public
name|double
name|computeLinearDistance
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
name|double
name|linearDistance
init|=
name|this
operator|.
name|center
operator|.
name|linearDistance
argument_list|(
name|point
argument_list|)
decl_stmt|;
if|if
condition|(
name|linearDistance
operator|>
name|cutoffLinearDistance
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|linearDistance
return|;
block|}
comment|/** Compute a linear distance to the vector.     * return Double.MAX_VALUE for points outside the shape.     */
annotation|@
name|Override
DECL|method|computeLinearDistance
specifier|public
name|double
name|computeLinearDistance
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
name|linearDistance
init|=
name|this
operator|.
name|center
operator|.
name|linearDistance
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
name|linearDistance
operator|>
name|cutoffLinearDistance
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|linearDistance
return|;
block|}
comment|/** Compute a squared linear distance to the vector.     */
annotation|@
name|Override
DECL|method|computeSquaredLinearDistance
specifier|public
name|double
name|computeSquaredLinearDistance
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
name|double
name|linearDistanceSquared
init|=
name|this
operator|.
name|center
operator|.
name|linearDistanceSquared
argument_list|(
name|point
argument_list|)
decl_stmt|;
if|if
condition|(
name|linearDistanceSquared
operator|>
name|cutoffLinearDistance
operator|*
name|cutoffLinearDistance
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|linearDistanceSquared
return|;
block|}
comment|/** Compute a squared linear distance to the vector.     */
annotation|@
name|Override
DECL|method|computeSquaredLinearDistance
specifier|public
name|double
name|computeSquaredLinearDistance
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
name|linearDistanceSquared
init|=
name|this
operator|.
name|center
operator|.
name|linearDistanceSquared
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
name|linearDistanceSquared
operator|>
name|cutoffLinearDistance
operator|*
name|cutoffLinearDistance
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|linearDistanceSquared
return|;
block|}
comment|/** Compute a true, accurate, great-circle distance.     * Double.MAX_VALUE indicates a point is outside of the shape.     */
annotation|@
name|Override
DECL|method|computeArcDistance
specifier|public
name|double
name|computeArcDistance
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
name|double
name|dist
init|=
name|this
operator|.
name|center
operator|.
name|arcDistance
argument_list|(
name|point
argument_list|)
decl_stmt|;
if|if
condition|(
name|dist
operator|>
name|cutoffAngle
condition|)
return|return
name|Double
operator|.
name|MAX_VALUE
return|;
return|return
name|dist
return|;
block|}
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
comment|// Fastest way of determining membership
return|return
name|circlePlane
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
comment|// Fastest way of determining membership
return|return
name|circlePlane
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
DECL|method|getInteriorPoint
specifier|public
name|GeoPoint
name|getInteriorPoint
parameter_list|()
block|{
return|return
name|center
return|;
block|}
annotation|@
name|Override
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
name|Plane
name|p
parameter_list|,
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
return|return
name|circlePlane
operator|.
name|intersects
argument_list|(
name|p
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
name|bounds
operator|=
name|super
operator|.
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
name|circlePlane
operator|.
name|recordBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
return|return
name|bounds
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
name|GeoCircle
operator|)
condition|)
return|return
literal|false
return|;
name|GeoCircle
name|other
init|=
operator|(
name|GeoCircle
operator|)
name|o
decl_stmt|;
return|return
name|other
operator|.
name|center
operator|.
name|equals
argument_list|(
name|center
argument_list|)
operator|&&
name|other
operator|.
name|cutoffAngle
operator|==
name|cutoffAngle
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
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|result
operator|=
name|center
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|cutoffAngle
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Circle: center = "
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|center
argument_list|)
operator|.
name|append
argument_list|(
literal|" radius = "
argument_list|)
operator|.
name|append
argument_list|(
name|cutoffAngle
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
