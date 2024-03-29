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
comment|/**  * Holds mathematical constants associated with the model of a planet.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PlanetModel
specifier|public
class|class
name|PlanetModel
block|{
comment|/** Planet model corresponding to sphere. */
DECL|field|SPHERE
specifier|public
specifier|static
specifier|final
name|PlanetModel
name|SPHERE
init|=
operator|new
name|PlanetModel
argument_list|(
literal|1.0
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
comment|/** Mean radius */
comment|// see http://earth-info.nga.mil/GandG/publications/tr8350.2/wgs84fin.pdf
DECL|field|WGS84_MEAN
specifier|public
specifier|static
specifier|final
name|double
name|WGS84_MEAN
init|=
literal|6371008.7714
decl_stmt|;
comment|/** Polar radius */
DECL|field|WGS84_POLAR
specifier|public
specifier|static
specifier|final
name|double
name|WGS84_POLAR
init|=
literal|6356752.314245
decl_stmt|;
comment|/** Equatorial radius */
DECL|field|WGS84_EQUATORIAL
specifier|public
specifier|static
specifier|final
name|double
name|WGS84_EQUATORIAL
init|=
literal|6378137.0
decl_stmt|;
comment|/** Planet model corresponding to WGS84 */
DECL|field|WGS84
specifier|public
specifier|static
specifier|final
name|PlanetModel
name|WGS84
init|=
operator|new
name|PlanetModel
argument_list|(
name|WGS84_EQUATORIAL
operator|/
name|WGS84_MEAN
argument_list|,
name|WGS84_POLAR
operator|/
name|WGS84_MEAN
argument_list|)
decl_stmt|;
comment|// Surface of the planet:
comment|// x^2/a^2 + y^2/b^2 + z^2/c^2 = 1.0
comment|// Scaling factors are a,b,c.  geo3d can only support models where a==b, so use ab instead.
comment|/** The x/y scaling factor */
DECL|field|ab
specifier|public
specifier|final
name|double
name|ab
decl_stmt|;
comment|/** The z scaling factor */
DECL|field|c
specifier|public
specifier|final
name|double
name|c
decl_stmt|;
comment|/** The inverse of ab */
DECL|field|inverseAb
specifier|public
specifier|final
name|double
name|inverseAb
decl_stmt|;
comment|/** The inverse of c */
DECL|field|inverseC
specifier|public
specifier|final
name|double
name|inverseC
decl_stmt|;
comment|/** The square of the inverse of ab */
DECL|field|inverseAbSquared
specifier|public
specifier|final
name|double
name|inverseAbSquared
decl_stmt|;
comment|/** The square of the inverse of c */
DECL|field|inverseCSquared
specifier|public
specifier|final
name|double
name|inverseCSquared
decl_stmt|;
comment|/** The flattening value */
DECL|field|flattening
specifier|public
specifier|final
name|double
name|flattening
decl_stmt|;
comment|/** The square ratio */
DECL|field|squareRatio
specifier|public
specifier|final
name|double
name|squareRatio
decl_stmt|;
comment|// We do NOT include radius, because all computations in geo3d are in radians, not meters.
comment|// Compute north and south pole for planet model, since these are commonly used.
comment|/** North pole */
DECL|field|NORTH_POLE
specifier|public
specifier|final
name|GeoPoint
name|NORTH_POLE
decl_stmt|;
comment|/** South pole */
DECL|field|SOUTH_POLE
specifier|public
specifier|final
name|GeoPoint
name|SOUTH_POLE
decl_stmt|;
comment|/** Min X pole */
DECL|field|MIN_X_POLE
specifier|public
specifier|final
name|GeoPoint
name|MIN_X_POLE
decl_stmt|;
comment|/** Max X pole */
DECL|field|MAX_X_POLE
specifier|public
specifier|final
name|GeoPoint
name|MAX_X_POLE
decl_stmt|;
comment|/** Min Y pole */
DECL|field|MIN_Y_POLE
specifier|public
specifier|final
name|GeoPoint
name|MIN_Y_POLE
decl_stmt|;
comment|/** Max Y pole */
DECL|field|MAX_Y_POLE
specifier|public
specifier|final
name|GeoPoint
name|MAX_Y_POLE
decl_stmt|;
comment|/** Constructor.    * @param ab is the x/y scaling factor.    * @param c is the z scaling factor.    */
DECL|method|PlanetModel
specifier|public
name|PlanetModel
parameter_list|(
specifier|final
name|double
name|ab
parameter_list|,
specifier|final
name|double
name|c
parameter_list|)
block|{
name|this
operator|.
name|ab
operator|=
name|ab
expr_stmt|;
name|this
operator|.
name|c
operator|=
name|c
expr_stmt|;
name|this
operator|.
name|inverseAb
operator|=
literal|1.0
operator|/
name|ab
expr_stmt|;
name|this
operator|.
name|inverseC
operator|=
literal|1.0
operator|/
name|c
expr_stmt|;
name|this
operator|.
name|flattening
operator|=
operator|(
name|ab
operator|-
name|c
operator|)
operator|*
name|inverseAb
expr_stmt|;
name|this
operator|.
name|squareRatio
operator|=
operator|(
name|ab
operator|*
name|ab
operator|-
name|c
operator|*
name|c
operator|)
operator|/
operator|(
name|c
operator|*
name|c
operator|)
expr_stmt|;
name|this
operator|.
name|inverseAbSquared
operator|=
name|inverseAb
operator|*
name|inverseAb
expr_stmt|;
name|this
operator|.
name|inverseCSquared
operator|=
name|inverseC
operator|*
name|inverseC
expr_stmt|;
name|this
operator|.
name|NORTH_POLE
operator|=
operator|new
name|GeoPoint
argument_list|(
name|c
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
literal|1.0
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|this
operator|.
name|SOUTH_POLE
operator|=
operator|new
name|GeoPoint
argument_list|(
name|c
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|1.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|this
operator|.
name|MIN_X_POLE
operator|=
operator|new
name|GeoPoint
argument_list|(
name|ab
argument_list|,
operator|-
literal|1.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|this
operator|.
name|MAX_X_POLE
operator|=
operator|new
name|GeoPoint
argument_list|(
name|ab
argument_list|,
literal|1.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|this
operator|.
name|MIN_Y_POLE
operator|=
operator|new
name|GeoPoint
argument_list|(
name|ab
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|1.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
expr_stmt|;
name|this
operator|.
name|MAX_Y_POLE
operator|=
operator|new
name|GeoPoint
argument_list|(
name|ab
argument_list|,
literal|0.0
argument_list|,
literal|1.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
expr_stmt|;
block|}
comment|/** Find the minimum magnitude of all points on the ellipsoid.    * @return the minimum magnitude for the planet.    */
DECL|method|getMinimumMagnitude
specifier|public
name|double
name|getMinimumMagnitude
parameter_list|()
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|this
operator|.
name|ab
argument_list|,
name|this
operator|.
name|c
argument_list|)
return|;
block|}
comment|/** Find the maximum magnitude of all points on the ellipsoid.    * @return the maximum magnitude for the planet.    */
DECL|method|getMaximumMagnitude
specifier|public
name|double
name|getMaximumMagnitude
parameter_list|()
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|this
operator|.
name|ab
argument_list|,
name|this
operator|.
name|c
argument_list|)
return|;
block|}
comment|/** Find the minimum x value.    *@return the minimum X value.    */
DECL|method|getMinimumXValue
specifier|public
name|double
name|getMinimumXValue
parameter_list|()
block|{
return|return
operator|-
name|this
operator|.
name|ab
return|;
block|}
comment|/** Find the maximum x value.    *@return the maximum X value.    */
DECL|method|getMaximumXValue
specifier|public
name|double
name|getMaximumXValue
parameter_list|()
block|{
return|return
name|this
operator|.
name|ab
return|;
block|}
comment|/** Find the minimum y value.    *@return the minimum Y value.    */
DECL|method|getMinimumYValue
specifier|public
name|double
name|getMinimumYValue
parameter_list|()
block|{
return|return
operator|-
name|this
operator|.
name|ab
return|;
block|}
comment|/** Find the maximum y value.    *@return the maximum Y value.    */
DECL|method|getMaximumYValue
specifier|public
name|double
name|getMaximumYValue
parameter_list|()
block|{
return|return
name|this
operator|.
name|ab
return|;
block|}
comment|/** Find the minimum z value.    *@return the minimum Z value.    */
DECL|method|getMinimumZValue
specifier|public
name|double
name|getMinimumZValue
parameter_list|()
block|{
return|return
operator|-
name|this
operator|.
name|c
return|;
block|}
comment|/** Find the maximum z value.    *@return the maximum Z value.    */
DECL|method|getMaximumZValue
specifier|public
name|double
name|getMaximumZValue
parameter_list|()
block|{
return|return
name|this
operator|.
name|c
return|;
block|}
comment|/** Check if point is on surface.    * @param v is the point to check.    * @return true if the point is on the planet surface.    */
DECL|method|pointOnSurface
specifier|public
name|boolean
name|pointOnSurface
parameter_list|(
specifier|final
name|Vector
name|v
parameter_list|)
block|{
return|return
name|pointOnSurface
argument_list|(
name|v
operator|.
name|x
argument_list|,
name|v
operator|.
name|y
argument_list|,
name|v
operator|.
name|z
argument_list|)
return|;
block|}
comment|/** Check if point is on surface.    * @param x is the x coord.    * @param y is the y coord.    * @param z is the z coord.    */
DECL|method|pointOnSurface
specifier|public
name|boolean
name|pointOnSurface
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
comment|// Equation of planet surface is:
comment|// x^2 / a^2 + y^2 / b^2 + z^2 / c^2 - 1 = 0
return|return
name|Math
operator|.
name|abs
argument_list|(
name|x
operator|*
name|x
operator|*
name|inverseAb
operator|*
name|inverseAb
operator|+
name|y
operator|*
name|y
operator|*
name|inverseAb
operator|*
name|inverseAb
operator|+
name|z
operator|*
name|z
operator|*
name|inverseC
operator|*
name|inverseC
operator|-
literal|1.0
argument_list|)
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
return|;
block|}
comment|/** Check if point is outside surface.    * @param v is the point to check.    * @return true if the point is outside the planet surface.    */
DECL|method|pointOutside
specifier|public
name|boolean
name|pointOutside
parameter_list|(
specifier|final
name|Vector
name|v
parameter_list|)
block|{
return|return
name|pointOutside
argument_list|(
name|v
operator|.
name|x
argument_list|,
name|v
operator|.
name|y
argument_list|,
name|v
operator|.
name|z
argument_list|)
return|;
block|}
comment|/** Check if point is outside surface.    * @param x is the x coord.    * @param y is the y coord.    * @param z is the z coord.    */
DECL|method|pointOutside
specifier|public
name|boolean
name|pointOutside
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
comment|// Equation of planet surface is:
comment|// x^2 / a^2 + y^2 / b^2 + z^2 / c^2 - 1 = 0
return|return
operator|(
name|x
operator|*
name|x
operator|+
name|y
operator|*
name|y
operator|)
operator|*
name|inverseAb
operator|*
name|inverseAb
operator|+
name|z
operator|*
name|z
operator|*
name|inverseC
operator|*
name|inverseC
operator|-
literal|1.0
operator|>
name|Vector
operator|.
name|MINIMUM_RESOLUTION
return|;
block|}
comment|/** Compute a GeoPoint that's scaled to actually be on the planet surface.    * @param vector is the vector.    * @return the scaled point.    */
DECL|method|createSurfacePoint
specifier|public
name|GeoPoint
name|createSurfacePoint
parameter_list|(
specifier|final
name|Vector
name|vector
parameter_list|)
block|{
return|return
name|createSurfacePoint
argument_list|(
name|vector
operator|.
name|x
argument_list|,
name|vector
operator|.
name|y
argument_list|,
name|vector
operator|.
name|z
argument_list|)
return|;
block|}
comment|/** Compute a GeoPoint that's based on (x,y,z) values, but is scaled to actually be on the planet surface.    * @param x is the x value.    * @param y is the y value.    * @param z is the z value.    * @return the scaled point.    */
DECL|method|createSurfacePoint
specifier|public
name|GeoPoint
name|createSurfacePoint
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
comment|// The equation of the surface is:
comment|// (x^2 / a^2 + y^2 / b^2 + z^2 / c^2) = 1
comment|// We will need to scale the passed-in x, y, z values:
comment|// ((tx)^2 / a^2 + (ty)^2 / b^2 + (tz)^2 / c^2) = 1
comment|// t^2 * (x^2 / a^2 + y^2 / b^2 + z^2 / c^2)  = 1
comment|// t = sqrt ( 1 / (x^2 / a^2 + y^2 / b^2 + z^2 / c^2))
specifier|final
name|double
name|t
init|=
name|Math
operator|.
name|sqrt
argument_list|(
literal|1.0
operator|/
operator|(
name|x
operator|*
name|x
operator|*
name|inverseAbSquared
operator|+
name|y
operator|*
name|y
operator|*
name|inverseAbSquared
operator|+
name|z
operator|*
name|z
operator|*
name|inverseCSquared
operator|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|GeoPoint
argument_list|(
name|t
operator|*
name|x
argument_list|,
name|t
operator|*
name|y
argument_list|,
name|t
operator|*
name|z
argument_list|)
return|;
block|}
comment|/** Compute a GeoPoint that's a bisection between two other GeoPoints.    * @param pt1 is the first point.    * @param pt2 is the second point.    * @return the bisection point, or null if a unique one cannot be found.    */
DECL|method|bisection
specifier|public
name|GeoPoint
name|bisection
parameter_list|(
specifier|final
name|GeoPoint
name|pt1
parameter_list|,
specifier|final
name|GeoPoint
name|pt2
parameter_list|)
block|{
specifier|final
name|double
name|A0
init|=
operator|(
name|pt1
operator|.
name|x
operator|+
name|pt2
operator|.
name|x
operator|)
operator|*
literal|0.5
decl_stmt|;
specifier|final
name|double
name|B0
init|=
operator|(
name|pt1
operator|.
name|y
operator|+
name|pt2
operator|.
name|y
operator|)
operator|*
literal|0.5
decl_stmt|;
specifier|final
name|double
name|C0
init|=
operator|(
name|pt1
operator|.
name|z
operator|+
name|pt2
operator|.
name|z
operator|)
operator|*
literal|0.5
decl_stmt|;
specifier|final
name|double
name|denom
init|=
name|inverseAbSquared
operator|*
name|A0
operator|*
name|A0
operator|+
name|inverseAbSquared
operator|*
name|B0
operator|*
name|B0
operator|+
name|inverseCSquared
operator|*
name|C0
operator|*
name|C0
decl_stmt|;
if|if
condition|(
name|denom
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
condition|)
block|{
comment|// Bisection is undefined
return|return
literal|null
return|;
block|}
specifier|final
name|double
name|t
init|=
name|Math
operator|.
name|sqrt
argument_list|(
literal|1.0
operator|/
name|denom
argument_list|)
decl_stmt|;
return|return
operator|new
name|GeoPoint
argument_list|(
name|t
operator|*
name|A0
argument_list|,
name|t
operator|*
name|B0
argument_list|,
name|t
operator|*
name|C0
argument_list|)
return|;
block|}
comment|/** Compute surface distance between two points.    * @param pt1 is the first point.    * @param pt2 is the second point.    * @return the adjusted angle, when multiplied by the mean earth radius, yields a surface distance.  This will differ    * from GeoPoint.arcDistance() only when the planet model is not a sphere. @see {@link GeoPoint#arcDistance(Vector)}    */
DECL|method|surfaceDistance
specifier|public
name|double
name|surfaceDistance
parameter_list|(
specifier|final
name|GeoPoint
name|pt1
parameter_list|,
specifier|final
name|GeoPoint
name|pt2
parameter_list|)
block|{
specifier|final
name|double
name|L
init|=
name|pt2
operator|.
name|getLongitude
argument_list|()
operator|-
name|pt1
operator|.
name|getLongitude
argument_list|()
decl_stmt|;
specifier|final
name|double
name|U1
init|=
name|Math
operator|.
name|atan
argument_list|(
operator|(
literal|1.0
operator|-
name|flattening
operator|)
operator|*
name|Math
operator|.
name|tan
argument_list|(
name|pt1
operator|.
name|getLatitude
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|double
name|U2
init|=
name|Math
operator|.
name|atan
argument_list|(
operator|(
literal|1.0
operator|-
name|flattening
operator|)
operator|*
name|Math
operator|.
name|tan
argument_list|(
name|pt2
operator|.
name|getLatitude
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinU1
init|=
name|Math
operator|.
name|sin
argument_list|(
name|U1
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosU1
init|=
name|Math
operator|.
name|cos
argument_list|(
name|U1
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinU2
init|=
name|Math
operator|.
name|sin
argument_list|(
name|U2
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosU2
init|=
name|Math
operator|.
name|cos
argument_list|(
name|U2
argument_list|)
decl_stmt|;
specifier|final
name|double
name|dCosU1CosU2
init|=
name|cosU1
operator|*
name|cosU2
decl_stmt|;
specifier|final
name|double
name|dCosU1SinU2
init|=
name|cosU1
operator|*
name|sinU2
decl_stmt|;
specifier|final
name|double
name|dSinU1SinU2
init|=
name|sinU1
operator|*
name|sinU2
decl_stmt|;
specifier|final
name|double
name|dSinU1CosU2
init|=
name|sinU1
operator|*
name|cosU2
decl_stmt|;
name|double
name|lambda
init|=
name|L
decl_stmt|;
name|double
name|lambdaP
init|=
name|Math
operator|.
name|PI
operator|*
literal|2.0
decl_stmt|;
name|int
name|iterLimit
init|=
literal|0
decl_stmt|;
name|double
name|cosSqAlpha
decl_stmt|;
name|double
name|sinSigma
decl_stmt|;
name|double
name|cos2SigmaM
decl_stmt|;
name|double
name|cosSigma
decl_stmt|;
name|double
name|sigma
decl_stmt|;
name|double
name|sinAlpha
decl_stmt|;
name|double
name|C
decl_stmt|;
name|double
name|sinLambda
decl_stmt|,
name|cosLambda
decl_stmt|;
do|do
block|{
name|sinLambda
operator|=
name|Math
operator|.
name|sin
argument_list|(
name|lambda
argument_list|)
expr_stmt|;
name|cosLambda
operator|=
name|Math
operator|.
name|cos
argument_list|(
name|lambda
argument_list|)
expr_stmt|;
name|sinSigma
operator|=
name|Math
operator|.
name|sqrt
argument_list|(
operator|(
name|cosU2
operator|*
name|sinLambda
operator|)
operator|*
operator|(
name|cosU2
operator|*
name|sinLambda
operator|)
operator|+
operator|(
name|dCosU1SinU2
operator|-
name|dSinU1CosU2
operator|*
name|cosLambda
operator|)
operator|*
operator|(
name|dCosU1SinU2
operator|-
name|dSinU1CosU2
operator|*
name|cosLambda
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sinSigma
operator|==
literal|0.0
condition|)
block|{
return|return
literal|0.0
return|;
block|}
name|cosSigma
operator|=
name|dSinU1SinU2
operator|+
name|dCosU1CosU2
operator|*
name|cosLambda
expr_stmt|;
name|sigma
operator|=
name|Math
operator|.
name|atan2
argument_list|(
name|sinSigma
argument_list|,
name|cosSigma
argument_list|)
expr_stmt|;
name|sinAlpha
operator|=
name|dCosU1CosU2
operator|*
name|sinLambda
operator|/
name|sinSigma
expr_stmt|;
name|cosSqAlpha
operator|=
literal|1.0
operator|-
name|sinAlpha
operator|*
name|sinAlpha
expr_stmt|;
name|cos2SigmaM
operator|=
name|cosSigma
operator|-
literal|2.0
operator|*
name|dSinU1SinU2
operator|/
name|cosSqAlpha
expr_stmt|;
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|cos2SigmaM
argument_list|)
condition|)
name|cos2SigmaM
operator|=
literal|0.0
expr_stmt|;
comment|// equatorial line: cosSqAlpha=0
name|C
operator|=
name|flattening
operator|/
literal|16.0
operator|*
name|cosSqAlpha
operator|*
operator|(
literal|4.0
operator|+
name|flattening
operator|*
operator|(
literal|4.0
operator|-
literal|3.0
operator|*
name|cosSqAlpha
operator|)
operator|)
expr_stmt|;
name|lambdaP
operator|=
name|lambda
expr_stmt|;
name|lambda
operator|=
name|L
operator|+
operator|(
literal|1.0
operator|-
name|C
operator|)
operator|*
name|flattening
operator|*
name|sinAlpha
operator|*
operator|(
name|sigma
operator|+
name|C
operator|*
name|sinSigma
operator|*
operator|(
name|cos2SigmaM
operator|+
name|C
operator|*
name|cosSigma
operator|*
operator|(
operator|-
literal|1.0
operator|+
literal|2.0
operator|*
name|cos2SigmaM
operator|*
name|cos2SigmaM
operator|)
operator|)
operator|)
expr_stmt|;
block|}
do|while
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|lambda
operator|-
name|lambdaP
argument_list|)
operator|>
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|&&
operator|++
name|iterLimit
operator|<
literal|40
condition|)
do|;
specifier|final
name|double
name|uSq
init|=
name|cosSqAlpha
operator|*
name|this
operator|.
name|squareRatio
decl_stmt|;
specifier|final
name|double
name|A
init|=
literal|1.0
operator|+
name|uSq
operator|/
literal|16384.0
operator|*
operator|(
literal|4096.0
operator|+
name|uSq
operator|*
operator|(
operator|-
literal|768.0
operator|+
name|uSq
operator|*
operator|(
literal|320.0
operator|-
literal|175.0
operator|*
name|uSq
operator|)
operator|)
operator|)
decl_stmt|;
specifier|final
name|double
name|B
init|=
name|uSq
operator|/
literal|1024.0
operator|*
operator|(
literal|256.0
operator|+
name|uSq
operator|*
operator|(
operator|-
literal|128.0
operator|+
name|uSq
operator|*
operator|(
literal|74.0
operator|-
literal|47.0
operator|*
name|uSq
operator|)
operator|)
operator|)
decl_stmt|;
specifier|final
name|double
name|deltaSigma
init|=
name|B
operator|*
name|sinSigma
operator|*
operator|(
name|cos2SigmaM
operator|+
name|B
operator|/
literal|4.0
operator|*
operator|(
name|cosSigma
operator|*
operator|(
operator|-
literal|1.0
operator|+
literal|2.0
operator|*
name|cos2SigmaM
operator|*
name|cos2SigmaM
operator|)
operator|-
name|B
operator|/
literal|6.0
operator|*
name|cos2SigmaM
operator|*
operator|(
operator|-
literal|3.0
operator|+
literal|4.0
operator|*
name|sinSigma
operator|*
name|sinSigma
operator|)
operator|*
operator|(
operator|-
literal|3.0
operator|+
literal|4.0
operator|*
name|cos2SigmaM
operator|*
name|cos2SigmaM
operator|)
operator|)
operator|)
decl_stmt|;
return|return
name|c
operator|*
name|A
operator|*
operator|(
name|sigma
operator|-
name|deltaSigma
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
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
name|PlanetModel
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|PlanetModel
name|other
init|=
operator|(
name|PlanetModel
operator|)
name|o
decl_stmt|;
return|return
name|ab
operator|==
name|other
operator|.
name|ab
operator|&&
name|c
operator|==
name|other
operator|.
name|c
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
name|Double
operator|.
name|hashCode
argument_list|(
name|ab
argument_list|)
operator|+
name|Double
operator|.
name|hashCode
argument_list|(
name|c
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
if|if
condition|(
name|this
operator|.
name|equals
argument_list|(
name|SPHERE
argument_list|)
condition|)
block|{
return|return
literal|"PlanetModel.SPHERE"
return|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|equals
argument_list|(
name|WGS84
argument_list|)
condition|)
block|{
return|return
literal|"PlanetModel.WGS84"
return|;
block|}
else|else
block|{
return|return
literal|"PlanetModel(ab="
operator|+
name|ab
operator|+
literal|" c="
operator|+
name|c
operator|+
literal|")"
return|;
block|}
block|}
block|}
end_class
end_unit
