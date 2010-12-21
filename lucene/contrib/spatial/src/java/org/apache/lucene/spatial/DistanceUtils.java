begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
package|;
end_package
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
name|geometry
operator|.
name|DistanceUnits
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
name|geometry
operator|.
name|FloatLatLng
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
name|geometry
operator|.
name|LatLng
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
name|geometry
operator|.
name|shape
operator|.
name|LLRect
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
name|geometry
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
name|tier
operator|.
name|InvalidGeoException
import|;
end_import
begin_comment
comment|/**  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment
begin_class
DECL|class|DistanceUtils
specifier|public
class|class
name|DistanceUtils
block|{
DECL|field|DEGREES_TO_RADIANS
specifier|public
specifier|static
specifier|final
name|double
name|DEGREES_TO_RADIANS
init|=
name|Math
operator|.
name|PI
operator|/
literal|180.0
decl_stmt|;
DECL|field|RADIANS_TO_DEGREES
specifier|public
specifier|static
specifier|final
name|double
name|RADIANS_TO_DEGREES
init|=
literal|180.0
operator|/
name|Math
operator|.
name|PI
decl_stmt|;
comment|//pre-compute some angles that are commonly used
DECL|field|DEG_45_AS_RADS
specifier|public
specifier|static
specifier|final
name|double
name|DEG_45_AS_RADS
init|=
name|Math
operator|.
name|PI
operator|/
literal|4.0
decl_stmt|;
DECL|field|SIN_45_AS_RADS
specifier|public
specifier|static
specifier|final
name|double
name|SIN_45_AS_RADS
init|=
name|Math
operator|.
name|sin
argument_list|(
name|DEG_45_AS_RADS
argument_list|)
decl_stmt|;
DECL|field|DEG_90_AS_RADS
specifier|public
specifier|static
specifier|final
name|double
name|DEG_90_AS_RADS
init|=
name|Math
operator|.
name|PI
operator|/
literal|2
decl_stmt|;
DECL|field|DEG_180_AS_RADS
specifier|public
specifier|static
specifier|final
name|double
name|DEG_180_AS_RADS
init|=
name|Math
operator|.
name|PI
decl_stmt|;
DECL|field|DEG_225_AS_RADS
specifier|public
specifier|static
specifier|final
name|double
name|DEG_225_AS_RADS
init|=
literal|5
operator|*
name|DEG_45_AS_RADS
decl_stmt|;
DECL|field|DEG_270_AS_RADS
specifier|public
specifier|static
specifier|final
name|double
name|DEG_270_AS_RADS
init|=
literal|3
operator|*
name|DEG_90_AS_RADS
decl_stmt|;
DECL|field|KM_TO_MILES
specifier|public
specifier|static
specifier|final
name|double
name|KM_TO_MILES
init|=
literal|0.621371192
decl_stmt|;
DECL|field|MILES_TO_KM
specifier|public
specifier|static
specifier|final
name|double
name|MILES_TO_KM
init|=
literal|1.609344
decl_stmt|;
comment|/**    * The International Union of Geodesy and Geophysics says the Earth's mean radius in KM is:    *    * [1] http://en.wikipedia.org/wiki/Earth_radius    */
DECL|field|EARTH_MEAN_RADIUS_KM
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_MEAN_RADIUS_KM
init|=
literal|6371.009
decl_stmt|;
DECL|field|EARTH_MEAN_RADIUS_MI
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_MEAN_RADIUS_MI
init|=
name|EARTH_MEAN_RADIUS_KM
operator|/
name|MILES_TO_KM
decl_stmt|;
DECL|field|EARTH_EQUATORIAL_RADIUS_MI
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_EQUATORIAL_RADIUS_MI
init|=
literal|3963.205
decl_stmt|;
DECL|field|EARTH_EQUATORIAL_RADIUS_KM
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_EQUATORIAL_RADIUS_KM
init|=
name|EARTH_EQUATORIAL_RADIUS_MI
operator|*
name|MILES_TO_KM
decl_stmt|;
DECL|method|getDistanceMi
specifier|public
specifier|static
name|double
name|getDistanceMi
parameter_list|(
name|double
name|x1
parameter_list|,
name|double
name|y1
parameter_list|,
name|double
name|x2
parameter_list|,
name|double
name|y2
parameter_list|)
block|{
return|return
name|getLLMDistance
argument_list|(
name|x1
argument_list|,
name|y1
argument_list|,
name|x2
argument_list|,
name|y2
argument_list|)
return|;
block|}
comment|/**    *     * @param x1    * @param y1    * @param miles    * @return boundary rectangle where getY/getX is top left, getMinY/getMinX is bottom right    */
DECL|method|getBoundary
specifier|public
specifier|static
name|Rectangle
name|getBoundary
parameter_list|(
name|double
name|x1
parameter_list|,
name|double
name|y1
parameter_list|,
name|double
name|miles
parameter_list|)
block|{
name|LLRect
name|box
init|=
name|LLRect
operator|.
name|createBox
argument_list|(
operator|new
name|FloatLatLng
argument_list|(
name|x1
argument_list|,
name|y1
argument_list|)
argument_list|,
name|miles
argument_list|,
name|miles
argument_list|)
decl_stmt|;
comment|//System.out.println("Box: "+maxX+" | "+ maxY+" | "+ minX + " | "+ minY);
return|return
name|box
operator|.
name|toRectangle
argument_list|()
return|;
block|}
DECL|method|getLLMDistance
specifier|public
specifier|static
name|double
name|getLLMDistance
parameter_list|(
name|double
name|x1
parameter_list|,
name|double
name|y1
parameter_list|,
name|double
name|x2
parameter_list|,
name|double
name|y2
parameter_list|)
block|{
name|LatLng
name|p1
init|=
operator|new
name|FloatLatLng
argument_list|(
name|x1
argument_list|,
name|y1
argument_list|)
decl_stmt|;
name|LatLng
name|p2
init|=
operator|new
name|FloatLatLng
argument_list|(
name|x2
argument_list|,
name|y2
argument_list|)
decl_stmt|;
return|return
name|p1
operator|.
name|arcDistance
argument_list|(
name|p2
argument_list|,
name|DistanceUnits
operator|.
name|MILES
argument_list|)
return|;
block|}
comment|/**    * distance/radius.    * @param distance The distance travelled    * @param radius The radius of the sphere    * @return The angular distance, in radians    */
DECL|method|angularDistance
specifier|public
specifier|static
name|double
name|angularDistance
parameter_list|(
name|double
name|distance
parameter_list|,
name|double
name|radius
parameter_list|)
block|{
return|return
name|distance
operator|/
name|radius
return|;
block|}
comment|/**    * Calculate the p-norm (i.e. length) beteen two vectors    *    * @param vec1  The first vector    * @param vec2  The second vector    * @param power The power (2 for Euclidean distance, 1 for manhattan, etc.)    * @return The length.    *<p/>    *         See http://en.wikipedia.org/wiki/Lp_space    * @see #vectorDistance(double[], double[], double, double)    */
DECL|method|vectorDistance
specifier|public
specifier|static
name|double
name|vectorDistance
parameter_list|(
name|double
index|[]
name|vec1
parameter_list|,
name|double
index|[]
name|vec2
parameter_list|,
name|double
name|power
parameter_list|)
block|{
return|return
name|vectorDistance
argument_list|(
name|vec1
argument_list|,
name|vec2
argument_list|,
name|power
argument_list|,
literal|1.0
operator|/
name|power
argument_list|)
return|;
block|}
comment|/**    * Calculate the p-norm (i.e. length) between two vectors    *    * @param vec1         The first vector    * @param vec2         The second vector    * @param power        The power (2 for Euclidean distance, 1 for manhattan, etc.)    * @param oneOverPower If you've precalculated oneOverPower and cached it, use this method to save one division operation over {@link #vectorDistance(double[], double[], double)}.    * @return The length.    */
DECL|method|vectorDistance
specifier|public
specifier|static
name|double
name|vectorDistance
parameter_list|(
name|double
index|[]
name|vec1
parameter_list|,
name|double
index|[]
name|vec2
parameter_list|,
name|double
name|power
parameter_list|,
name|double
name|oneOverPower
parameter_list|)
block|{
name|double
name|result
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|power
operator|==
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vec1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|+=
name|vec1
index|[
name|i
index|]
operator|-
name|vec2
index|[
name|i
index|]
operator|==
literal|0
condition|?
literal|0
else|:
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|power
operator|==
literal|1.0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vec1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|+=
name|vec1
index|[
name|i
index|]
operator|-
name|vec2
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|power
operator|==
literal|2.0
condition|)
block|{
name|result
operator|=
name|Math
operator|.
name|sqrt
argument_list|(
name|squaredEuclideanDistance
argument_list|(
name|vec1
argument_list|,
name|vec2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|power
operator|==
name|Integer
operator|.
name|MAX_VALUE
operator|||
name|Double
operator|.
name|isInfinite
argument_list|(
name|power
argument_list|)
condition|)
block|{
comment|//infinite norm?
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vec1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|Math
operator|.
name|max
argument_list|(
name|result
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|vec1
index|[
name|i
index|]
argument_list|,
name|vec2
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vec1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|+=
name|Math
operator|.
name|pow
argument_list|(
name|vec1
index|[
name|i
index|]
operator|-
name|vec2
index|[
name|i
index|]
argument_list|,
name|power
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|Math
operator|.
name|pow
argument_list|(
name|result
argument_list|,
name|oneOverPower
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Return the coordinates of a vector that is the corner of a box (upper right or lower left), assuming a Rectangular    * coordinate system.  Note, this does not apply for points on a sphere or ellipse (although it could be used as an approximatation).    *    * @param center     The center point    * @param result Holds the result, potentially resizing if needed.    * @param distance   The d from the center to the corner    * @param upperRight If true, return the coords for the upper right corner, else return the lower left.    * @return The point, either the upperLeft or the lower right    */
DECL|method|vectorBoxCorner
specifier|public
specifier|static
name|double
index|[]
name|vectorBoxCorner
parameter_list|(
name|double
index|[]
name|center
parameter_list|,
name|double
index|[]
name|result
parameter_list|,
name|double
name|distance
parameter_list|,
name|boolean
name|upperRight
parameter_list|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
operator|||
name|result
operator|.
name|length
operator|!=
name|center
operator|.
name|length
condition|)
block|{
name|result
operator|=
operator|new
name|double
index|[
name|center
operator|.
name|length
index|]
expr_stmt|;
block|}
if|if
condition|(
name|upperRight
operator|==
literal|false
condition|)
block|{
name|distance
operator|=
operator|-
name|distance
expr_stmt|;
block|}
comment|//We don't care about the power here,
comment|// b/c we are always in a rectangular coordinate system, so any norm can be used by
comment|//using the definition of sine
name|distance
operator|=
name|SIN_45_AS_RADS
operator|*
name|distance
expr_stmt|;
comment|// sin(Pi/4) == (2^0.5)/2 == opp/hyp == opp/distance, solve for opp, similarily for cosine
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|center
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|center
index|[
name|i
index|]
operator|+
name|distance
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * @param latCenter  In degrees    * @param lonCenter  In degrees    * @param distance The distance    * @param result A preallocated array to hold the results.  If null, a new one is constructed.    * @param upperRight If true, calculate the upper right corner, else the lower left    * @param sphereRadius The radius of the sphere to use.    * @return The Lat/Lon in degrees    *    * @see #latLonCorner(double, double, double, double[], boolean, double)    */
DECL|method|latLonCornerDegs
specifier|public
specifier|static
name|double
index|[]
name|latLonCornerDegs
parameter_list|(
name|double
name|latCenter
parameter_list|,
name|double
name|lonCenter
parameter_list|,
name|double
name|distance
parameter_list|,
name|double
index|[]
name|result
parameter_list|,
name|boolean
name|upperRight
parameter_list|,
name|double
name|sphereRadius
parameter_list|)
block|{
name|result
operator|=
name|latLonCorner
argument_list|(
name|latCenter
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|lonCenter
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|distance
argument_list|,
name|result
argument_list|,
name|upperRight
argument_list|,
name|sphereRadius
argument_list|)
expr_stmt|;
name|result
index|[
literal|0
index|]
operator|=
name|result
index|[
literal|0
index|]
operator|*
name|RADIANS_TO_DEGREES
expr_stmt|;
name|result
index|[
literal|1
index|]
operator|=
name|result
index|[
literal|1
index|]
operator|*
name|RADIANS_TO_DEGREES
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Uses Haversine to calculate the corner of a box (upper right or lower left) that is the<i>distance</i> away, given a sphere of the specified<i>radius</i>.    *    * NOTE: This is not the same as calculating a box that transcribes a circle of the given distance.    *    * @param latCenter  In radians    * @param lonCenter  In radians    * @param distance   The distance    * @param result A preallocated array to hold the results.  If null, a new one is constructed.    * @param upperRight If true, give lat/lon for the upper right corner, else lower left    * @param sphereRadius     The radius to use for the calculation    * @return The Lat/Lon in Radians     */
DECL|method|latLonCorner
specifier|public
specifier|static
name|double
index|[]
name|latLonCorner
parameter_list|(
name|double
name|latCenter
parameter_list|,
name|double
name|lonCenter
parameter_list|,
name|double
name|distance
parameter_list|,
name|double
index|[]
name|result
parameter_list|,
name|boolean
name|upperRight
parameter_list|,
name|double
name|sphereRadius
parameter_list|)
block|{
comment|// Haversine formula
name|double
name|brng
init|=
name|upperRight
condition|?
name|DEG_45_AS_RADS
else|:
name|DEG_225_AS_RADS
decl_stmt|;
name|result
operator|=
name|pointOnBearing
argument_list|(
name|latCenter
argument_list|,
name|lonCenter
argument_list|,
name|distance
argument_list|,
name|brng
argument_list|,
name|result
argument_list|,
name|sphereRadius
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Given a start point (startLat, startLon) and a bearing on a sphere of radius<i>sphereRadius</i>, return the destination point.    * @param startLat The starting point latitude, in radians    * @param startLon The starting point longitude, in radians    * @param distance The distance to travel along the bearing.  The units are assumed to be the same as the sphereRadius units, both of which is up to the caller to know    * @param bearing The bearing, in radians.  North is a 0 deg. bearing, east is 90 deg, south is 180 deg, west is 270 deg.     * @param result A preallocated array to hold the results.  If null, a new one is constructed.    * @param sphereRadius The radius of the sphere to use for the calculation.    * @return The destination point, in radians.  First entry is latitude, second is longitude    */
DECL|method|pointOnBearing
specifier|public
specifier|static
name|double
index|[]
name|pointOnBearing
parameter_list|(
name|double
name|startLat
parameter_list|,
name|double
name|startLon
parameter_list|,
name|double
name|distance
parameter_list|,
name|double
name|bearing
parameter_list|,
name|double
index|[]
name|result
parameter_list|,
name|double
name|sphereRadius
parameter_list|)
block|{
comment|/*  	lat2 = asin(sin(lat1)*cos(d/R) + cos(lat1)*sin(d/R)*cos(Î¸))   	lon2 = lon1 + atan2(sin(Î¸)*sin(d/R)*cos(lat1), cos(d/R)âsin(lat1)*sin(lat2))           */
name|double
name|cosAngDist
init|=
name|Math
operator|.
name|cos
argument_list|(
name|distance
operator|/
name|sphereRadius
argument_list|)
decl_stmt|;
name|double
name|cosStartLat
init|=
name|Math
operator|.
name|cos
argument_list|(
name|startLat
argument_list|)
decl_stmt|;
name|double
name|sinAngDist
init|=
name|Math
operator|.
name|sin
argument_list|(
name|distance
operator|/
name|sphereRadius
argument_list|)
decl_stmt|;
name|double
name|lat2
init|=
name|Math
operator|.
name|asin
argument_list|(
name|Math
operator|.
name|sin
argument_list|(
name|startLat
argument_list|)
operator|*
name|cosAngDist
operator|+
name|cosStartLat
operator|*
name|sinAngDist
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|bearing
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|lon2
init|=
name|startLon
operator|+
name|Math
operator|.
name|atan2
argument_list|(
name|Math
operator|.
name|sin
argument_list|(
name|bearing
argument_list|)
operator|*
name|sinAngDist
operator|*
name|cosStartLat
argument_list|,
name|cosAngDist
operator|-
name|Math
operator|.
name|sin
argument_list|(
name|startLat
argument_list|)
operator|*
name|Math
operator|.
name|sin
argument_list|(
name|lat2
argument_list|)
argument_list|)
decl_stmt|;
comment|/*lat2 = (lat2*180)/Math.PI;     lon2 = (lon2*180)/Math.PI;*/
comment|//From Lucene.  Move back to Lucene when synced
comment|// normalize long first
if|if
condition|(
name|result
operator|==
literal|null
operator|||
name|result
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
name|result
operator|=
operator|new
name|double
index|[
literal|2
index|]
expr_stmt|;
block|}
name|result
index|[
literal|0
index|]
operator|=
name|lat2
expr_stmt|;
name|result
index|[
literal|1
index|]
operator|=
name|lon2
expr_stmt|;
name|normLng
argument_list|(
name|result
argument_list|)
expr_stmt|;
comment|// normalize lat - could flip poles
name|normLat
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * @param latLng The lat/lon, in radians. lat in position 0, long in position 1    */
DECL|method|normLat
specifier|public
specifier|static
name|void
name|normLat
parameter_list|(
name|double
index|[]
name|latLng
parameter_list|)
block|{
if|if
condition|(
name|latLng
index|[
literal|0
index|]
operator|>
name|DEG_90_AS_RADS
condition|)
block|{
name|latLng
index|[
literal|0
index|]
operator|=
name|DEG_90_AS_RADS
operator|-
operator|(
name|latLng
index|[
literal|0
index|]
operator|-
name|DEG_90_AS_RADS
operator|)
expr_stmt|;
if|if
condition|(
name|latLng
index|[
literal|1
index|]
operator|<
literal|0
condition|)
block|{
name|latLng
index|[
literal|1
index|]
operator|=
name|latLng
index|[
literal|1
index|]
operator|+
name|DEG_180_AS_RADS
expr_stmt|;
block|}
else|else
block|{
name|latLng
index|[
literal|1
index|]
operator|=
name|latLng
index|[
literal|1
index|]
operator|-
name|DEG_180_AS_RADS
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|latLng
index|[
literal|0
index|]
operator|<
operator|-
name|DEG_90_AS_RADS
condition|)
block|{
name|latLng
index|[
literal|0
index|]
operator|=
operator|-
name|DEG_90_AS_RADS
operator|-
operator|(
name|latLng
index|[
literal|0
index|]
operator|+
name|DEG_90_AS_RADS
operator|)
expr_stmt|;
if|if
condition|(
name|latLng
index|[
literal|1
index|]
operator|<
literal|0
condition|)
block|{
name|latLng
index|[
literal|1
index|]
operator|=
name|latLng
index|[
literal|1
index|]
operator|+
name|DEG_180_AS_RADS
expr_stmt|;
block|}
else|else
block|{
name|latLng
index|[
literal|1
index|]
operator|=
name|latLng
index|[
literal|1
index|]
operator|-
name|DEG_180_AS_RADS
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns a normalized Lng rectangle shape for the bounding box    *    * @param latLng The lat/lon, in radians, lat in position 0, long in position 1    */
DECL|method|normLng
specifier|public
specifier|static
name|void
name|normLng
parameter_list|(
name|double
index|[]
name|latLng
parameter_list|)
block|{
if|if
condition|(
name|latLng
index|[
literal|1
index|]
operator|>
name|DEG_180_AS_RADS
condition|)
block|{
name|latLng
index|[
literal|1
index|]
operator|=
operator|-
literal|1.0
operator|*
operator|(
name|DEG_180_AS_RADS
operator|-
operator|(
name|latLng
index|[
literal|1
index|]
operator|-
name|DEG_180_AS_RADS
operator|)
operator|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|latLng
index|[
literal|1
index|]
operator|<
operator|-
name|DEG_180_AS_RADS
condition|)
block|{
name|latLng
index|[
literal|1
index|]
operator|=
operator|(
name|latLng
index|[
literal|1
index|]
operator|+
name|DEG_180_AS_RADS
operator|)
operator|+
name|DEG_180_AS_RADS
expr_stmt|;
block|}
block|}
comment|/**    * The square of the Euclidean Distance.  Not really a distance, but useful if all that matters is    * comparing the result to another one.    *    * @param vec1 The first point    * @param vec2 The second point    * @return The squared Euclidean distance    */
DECL|method|squaredEuclideanDistance
specifier|public
specifier|static
name|double
name|squaredEuclideanDistance
parameter_list|(
name|double
index|[]
name|vec1
parameter_list|,
name|double
index|[]
name|vec2
parameter_list|)
block|{
name|double
name|result
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vec1
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|double
name|v
init|=
name|vec1
index|[
name|i
index|]
operator|-
name|vec2
index|[
name|i
index|]
decl_stmt|;
name|result
operator|+=
name|v
operator|*
name|v
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * @param x1     The x coordinate of the first point, in radians    * @param y1     The y coordinate of the first point, in radians    * @param x2     The x coordinate of the second point, in radians    * @param y2     The y coordinate of the second point, in radians    * @param radius The radius of the sphere    * @return The distance between the two points, as determined by the Haversine formula.     */
DECL|method|haversine
specifier|public
specifier|static
name|double
name|haversine
parameter_list|(
name|double
name|x1
parameter_list|,
name|double
name|y1
parameter_list|,
name|double
name|x2
parameter_list|,
name|double
name|y2
parameter_list|,
name|double
name|radius
parameter_list|)
block|{
name|double
name|result
init|=
literal|0
decl_stmt|;
comment|//make sure they aren't all the same, as then we can just return 0
if|if
condition|(
operator|(
name|x1
operator|!=
name|x2
operator|)
operator|||
operator|(
name|y1
operator|!=
name|y2
operator|)
condition|)
block|{
name|double
name|diffX
init|=
name|x1
operator|-
name|x2
decl_stmt|;
name|double
name|diffY
init|=
name|y1
operator|-
name|y2
decl_stmt|;
name|double
name|hsinX
init|=
name|Math
operator|.
name|sin
argument_list|(
name|diffX
operator|*
literal|0.5
argument_list|)
decl_stmt|;
name|double
name|hsinY
init|=
name|Math
operator|.
name|sin
argument_list|(
name|diffY
operator|*
literal|0.5
argument_list|)
decl_stmt|;
name|double
name|h
init|=
name|hsinX
operator|*
name|hsinX
operator|+
operator|(
name|Math
operator|.
name|cos
argument_list|(
name|x1
argument_list|)
operator|*
name|Math
operator|.
name|cos
argument_list|(
name|x2
argument_list|)
operator|*
name|hsinY
operator|*
name|hsinY
operator|)
decl_stmt|;
name|result
operator|=
operator|(
name|radius
operator|*
literal|2
operator|*
name|Math
operator|.
name|atan2
argument_list|(
name|Math
operator|.
name|sqrt
argument_list|(
name|h
argument_list|)
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
literal|1
operator|-
name|h
argument_list|)
argument_list|)
operator|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Given a string containing<i>dimension</i> values encoded in it, separated by commas, return a String array of length<i>dimension</i>    * containing the values.    *    * @param out         A preallocated array.  Must be size dimension.  If it is not it will be resized.    * @param externalVal The value to parse    * @param dimension   The expected number of values for the point    * @return An array of the values that make up the point (aka vector)    * @throws org.apache.lucene.spatial.tier.InvalidGeoException if the dimension specified does not match the number of values in the externalValue.    */
DECL|method|parsePoint
specifier|public
specifier|static
name|String
index|[]
name|parsePoint
parameter_list|(
name|String
index|[]
name|out
parameter_list|,
name|String
name|externalVal
parameter_list|,
name|int
name|dimension
parameter_list|)
throws|throws
name|InvalidGeoException
block|{
comment|//TODO: Should we support sparse vectors?
if|if
condition|(
name|out
operator|==
literal|null
operator|||
name|out
operator|.
name|length
operator|!=
name|dimension
condition|)
name|out
operator|=
operator|new
name|String
index|[
name|dimension
index|]
expr_stmt|;
name|int
name|idx
init|=
name|externalVal
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|idx
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|idx
operator|==
operator|-
literal|1
operator|&&
name|dimension
operator|==
literal|1
operator|&&
name|externalVal
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//we have a single point, dimension better be 1
name|out
index|[
literal|0
index|]
operator|=
name|externalVal
operator|.
name|trim
argument_list|()
expr_stmt|;
name|i
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
comment|//if it is zero, that is an error
comment|//Parse out a comma separated list of point values, as in: 73.5,89.2,7773.4
for|for
control|(
init|;
name|i
operator|<
name|dimension
condition|;
name|i
operator|++
control|)
block|{
while|while
condition|(
name|start
operator|<
name|end
operator|&&
name|externalVal
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
operator|==
literal|' '
condition|)
name|start
operator|++
expr_stmt|;
while|while
condition|(
name|end
operator|>
name|start
operator|&&
name|externalVal
operator|.
name|charAt
argument_list|(
name|end
operator|-
literal|1
argument_list|)
operator|==
literal|' '
condition|)
name|end
operator|--
expr_stmt|;
if|if
condition|(
name|start
operator|==
name|end
condition|)
block|{
break|break;
block|}
name|out
index|[
name|i
index|]
operator|=
name|externalVal
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|start
operator|=
name|idx
operator|+
literal|1
expr_stmt|;
name|end
operator|=
name|externalVal
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|,
name|start
argument_list|)
expr_stmt|;
name|idx
operator|=
name|end
expr_stmt|;
if|if
condition|(
name|end
operator|==
operator|-
literal|1
condition|)
block|{
name|end
operator|=
name|externalVal
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|i
operator|!=
name|dimension
condition|)
block|{
throw|throw
operator|new
name|InvalidGeoException
argument_list|(
literal|"incompatible dimension ("
operator|+
name|dimension
operator|+
literal|") and values ("
operator|+
name|externalVal
operator|+
literal|").  Only "
operator|+
name|i
operator|+
literal|" values specified"
argument_list|)
throw|;
block|}
return|return
name|out
return|;
block|}
comment|/**    * Given a string containing<i>dimension</i> values encoded in it, separated by commas, return a double array of length<i>dimension</i>    * containing the values.    *    * @param out         A preallocated array.  Must be size dimension.  If it is not it will be resized.    * @param externalVal The value to parse    * @param dimension   The expected number of values for the point    * @return An array of the values that make up the point (aka vector)    * @throws InvalidGeoException if the dimension specified does not match the number of values in the externalValue.    */
DECL|method|parsePointDouble
specifier|public
specifier|static
name|double
index|[]
name|parsePointDouble
parameter_list|(
name|double
index|[]
name|out
parameter_list|,
name|String
name|externalVal
parameter_list|,
name|int
name|dimension
parameter_list|)
throws|throws
name|InvalidGeoException
block|{
if|if
condition|(
name|out
operator|==
literal|null
operator|||
name|out
operator|.
name|length
operator|!=
name|dimension
condition|)
name|out
operator|=
operator|new
name|double
index|[
name|dimension
index|]
expr_stmt|;
name|int
name|idx
init|=
name|externalVal
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|idx
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|idx
operator|==
operator|-
literal|1
operator|&&
name|dimension
operator|==
literal|1
operator|&&
name|externalVal
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//we have a single point, dimension better be 1
name|out
index|[
literal|0
index|]
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|externalVal
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
comment|//if it is zero, that is an error
comment|//Parse out a comma separated list of point values, as in: 73.5,89.2,7773.4
for|for
control|(
init|;
name|i
operator|<
name|dimension
condition|;
name|i
operator|++
control|)
block|{
comment|//TODO: abstract common code with other parsePoint
while|while
condition|(
name|start
operator|<
name|end
operator|&&
name|externalVal
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
operator|==
literal|' '
condition|)
name|start
operator|++
expr_stmt|;
while|while
condition|(
name|end
operator|>
name|start
operator|&&
name|externalVal
operator|.
name|charAt
argument_list|(
name|end
operator|-
literal|1
argument_list|)
operator|==
literal|' '
condition|)
name|end
operator|--
expr_stmt|;
if|if
condition|(
name|start
operator|==
name|end
condition|)
block|{
break|break;
block|}
name|out
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|externalVal
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
argument_list|)
expr_stmt|;
name|start
operator|=
name|idx
operator|+
literal|1
expr_stmt|;
name|end
operator|=
name|externalVal
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|,
name|start
argument_list|)
expr_stmt|;
name|idx
operator|=
name|end
expr_stmt|;
if|if
condition|(
name|end
operator|==
operator|-
literal|1
condition|)
block|{
name|end
operator|=
name|externalVal
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|i
operator|!=
name|dimension
condition|)
block|{
throw|throw
operator|new
name|InvalidGeoException
argument_list|(
literal|"incompatible dimension ("
operator|+
name|dimension
operator|+
literal|") and values ("
operator|+
name|externalVal
operator|+
literal|").  Only "
operator|+
name|i
operator|+
literal|" values specified"
argument_list|)
throw|;
block|}
return|return
name|out
return|;
block|}
DECL|method|parseLatitudeLongitude
specifier|public
specifier|static
specifier|final
name|double
index|[]
name|parseLatitudeLongitude
parameter_list|(
name|String
name|latLonStr
parameter_list|)
throws|throws
name|InvalidGeoException
block|{
return|return
name|parseLatitudeLongitude
argument_list|(
literal|null
argument_list|,
name|latLonStr
argument_list|)
return|;
block|}
comment|/**    * extract (by calling {@link #parsePoint(String[], String, int)} and validate the latitude and longitude contained    * in the String by making sure the latitude is between 90& -90 and longitude is between -180 and 180.    *<p/>    * The latitude is assumed to be the first part of the string and the longitude the second part.    *    * @param latLon    A preallocated array to hold the result    * @param latLonStr The string to parse.  Latitude is the first value, longitude is the second.    * @return The lat long    * @throws InvalidGeoException if there was an error parsing    */
DECL|method|parseLatitudeLongitude
specifier|public
specifier|static
specifier|final
name|double
index|[]
name|parseLatitudeLongitude
parameter_list|(
name|double
index|[]
name|latLon
parameter_list|,
name|String
name|latLonStr
parameter_list|)
throws|throws
name|InvalidGeoException
block|{
if|if
condition|(
name|latLon
operator|==
literal|null
condition|)
block|{
name|latLon
operator|=
operator|new
name|double
index|[
literal|2
index|]
expr_stmt|;
block|}
name|double
index|[]
name|toks
init|=
name|parsePointDouble
argument_list|(
literal|null
argument_list|,
name|latLonStr
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|toks
index|[
literal|0
index|]
operator|<
operator|-
literal|90.0
operator|||
name|toks
index|[
literal|0
index|]
operator|>
literal|90.0
condition|)
block|{
throw|throw
operator|new
name|InvalidGeoException
argument_list|(
literal|"Invalid latitude: latitudes are range -90 to 90: provided lat: ["
operator|+
name|toks
index|[
literal|0
index|]
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|latLon
index|[
literal|0
index|]
operator|=
name|toks
index|[
literal|0
index|]
expr_stmt|;
if|if
condition|(
name|toks
index|[
literal|1
index|]
operator|<
operator|-
literal|180.0
operator|||
name|toks
index|[
literal|1
index|]
operator|>
literal|180.0
condition|)
block|{
throw|throw
operator|new
name|InvalidGeoException
argument_list|(
literal|"Invalid longitude: longitudes are range -180 to 180: provided lon: ["
operator|+
name|toks
index|[
literal|1
index|]
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|latLon
index|[
literal|1
index|]
operator|=
name|toks
index|[
literal|1
index|]
expr_stmt|;
return|return
name|latLon
return|;
block|}
block|}
end_class
end_unit
