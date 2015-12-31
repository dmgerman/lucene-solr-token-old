begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SloppyMath
operator|.
name|PIO2
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SloppyMath
operator|.
name|TO_DEGREES
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SloppyMath
operator|.
name|TO_RADIANS
import|;
end_import
begin_comment
comment|/**  * Reusable geo-spatial projection utility methods.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoProjectionUtils
specifier|public
class|class
name|GeoProjectionUtils
block|{
comment|// WGS84 earth-ellipsoid major (a) minor (b) radius, (f) flattening and eccentricity (e)
DECL|field|SEMIMAJOR_AXIS
specifier|public
specifier|static
specifier|final
name|double
name|SEMIMAJOR_AXIS
init|=
literal|6_378_137
decl_stmt|;
comment|// [m]
DECL|field|FLATTENING
specifier|public
specifier|static
specifier|final
name|double
name|FLATTENING
init|=
literal|1.0
operator|/
literal|298.257223563
decl_stmt|;
DECL|field|SEMIMINOR_AXIS
specifier|public
specifier|static
specifier|final
name|double
name|SEMIMINOR_AXIS
init|=
name|SEMIMAJOR_AXIS
operator|*
operator|(
literal|1.0
operator|-
name|FLATTENING
operator|)
decl_stmt|;
comment|//6_356_752.31420; // [m]
DECL|field|ECCENTRICITY
specifier|public
specifier|static
specifier|final
name|double
name|ECCENTRICITY
init|=
name|StrictMath
operator|.
name|sqrt
argument_list|(
operator|(
literal|2.0
operator|-
name|FLATTENING
operator|)
operator|*
name|FLATTENING
argument_list|)
decl_stmt|;
DECL|field|SEMIMAJOR_AXIS2
specifier|static
specifier|final
name|double
name|SEMIMAJOR_AXIS2
init|=
name|SEMIMAJOR_AXIS
operator|*
name|SEMIMAJOR_AXIS
decl_stmt|;
DECL|field|SEMIMINOR_AXIS2
specifier|static
specifier|final
name|double
name|SEMIMINOR_AXIS2
init|=
name|SEMIMINOR_AXIS
operator|*
name|SEMIMINOR_AXIS
decl_stmt|;
DECL|field|MIN_LON_RADIANS
specifier|public
specifier|static
specifier|final
name|double
name|MIN_LON_RADIANS
init|=
name|TO_RADIANS
operator|*
name|GeoUtils
operator|.
name|MIN_LON_INCL
decl_stmt|;
DECL|field|MIN_LAT_RADIANS
specifier|public
specifier|static
specifier|final
name|double
name|MIN_LAT_RADIANS
init|=
name|TO_RADIANS
operator|*
name|GeoUtils
operator|.
name|MIN_LAT_INCL
decl_stmt|;
DECL|field|MAX_LON_RADIANS
specifier|public
specifier|static
specifier|final
name|double
name|MAX_LON_RADIANS
init|=
name|TO_RADIANS
operator|*
name|GeoUtils
operator|.
name|MAX_LON_INCL
decl_stmt|;
DECL|field|MAX_LAT_RADIANS
specifier|public
specifier|static
specifier|final
name|double
name|MAX_LAT_RADIANS
init|=
name|TO_RADIANS
operator|*
name|GeoUtils
operator|.
name|MAX_LAT_INCL
decl_stmt|;
DECL|field|E2
specifier|private
specifier|static
specifier|final
name|double
name|E2
init|=
operator|(
name|SEMIMAJOR_AXIS2
operator|-
name|SEMIMINOR_AXIS2
operator|)
operator|/
operator|(
name|SEMIMAJOR_AXIS2
operator|)
decl_stmt|;
DECL|field|EP2
specifier|private
specifier|static
specifier|final
name|double
name|EP2
init|=
operator|(
name|SEMIMAJOR_AXIS2
operator|-
name|SEMIMINOR_AXIS2
operator|)
operator|/
operator|(
name|SEMIMINOR_AXIS2
operator|)
decl_stmt|;
comment|/**    * Converts from geocentric earth-centered earth-fixed to geodesic lat/lon/alt    * @param x Cartesian x coordinate    * @param y Cartesian y coordinate    * @param z Cartesian z coordinate    * @param lla 0: longitude 1: latitude: 2: altitude    * @return double array as 0: longitude 1: latitude 2: altitude    */
DECL|method|ecfToLLA
specifier|public
specifier|static
specifier|final
name|double
index|[]
name|ecfToLLA
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
parameter_list|,
name|double
index|[]
name|lla
parameter_list|)
block|{
name|boolean
name|atPole
init|=
literal|false
decl_stmt|;
specifier|final
name|double
name|ad_c
init|=
literal|1.0026000D
decl_stmt|;
specifier|final
name|double
name|cos67P5
init|=
literal|0.38268343236508977D
decl_stmt|;
if|if
condition|(
name|lla
operator|==
literal|null
condition|)
block|{
name|lla
operator|=
operator|new
name|double
index|[
literal|3
index|]
expr_stmt|;
block|}
if|if
condition|(
name|x
operator|!=
literal|0.0
condition|)
block|{
name|lla
index|[
literal|0
index|]
operator|=
name|StrictMath
operator|.
name|atan2
argument_list|(
name|y
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|y
operator|>
literal|0
condition|)
block|{
name|lla
index|[
literal|0
index|]
operator|=
name|PIO2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|y
operator|<
literal|0
condition|)
block|{
name|lla
index|[
literal|0
index|]
operator|=
operator|-
name|PIO2
expr_stmt|;
block|}
else|else
block|{
name|atPole
operator|=
literal|true
expr_stmt|;
name|lla
index|[
literal|0
index|]
operator|=
literal|0.0D
expr_stmt|;
if|if
condition|(
name|z
operator|>
literal|0.0
condition|)
block|{
name|lla
index|[
literal|1
index|]
operator|=
name|PIO2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|z
operator|<
literal|0.0
condition|)
block|{
name|lla
index|[
literal|1
index|]
operator|=
operator|-
name|PIO2
expr_stmt|;
block|}
else|else
block|{
name|lla
index|[
literal|1
index|]
operator|=
name|PIO2
expr_stmt|;
name|lla
index|[
literal|2
index|]
operator|=
operator|-
name|SEMIMINOR_AXIS
expr_stmt|;
return|return
name|lla
return|;
block|}
block|}
block|}
specifier|final
name|double
name|w2
init|=
name|x
operator|*
name|x
operator|+
name|y
operator|*
name|y
decl_stmt|;
specifier|final
name|double
name|w
init|=
name|StrictMath
operator|.
name|sqrt
argument_list|(
name|w2
argument_list|)
decl_stmt|;
specifier|final
name|double
name|t0
init|=
name|z
operator|*
name|ad_c
decl_stmt|;
specifier|final
name|double
name|s0
init|=
name|StrictMath
operator|.
name|sqrt
argument_list|(
name|t0
operator|*
name|t0
operator|+
name|w2
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinB0
init|=
name|t0
operator|/
name|s0
decl_stmt|;
specifier|final
name|double
name|cosB0
init|=
name|w
operator|/
name|s0
decl_stmt|;
specifier|final
name|double
name|sin3B0
init|=
name|sinB0
operator|*
name|sinB0
operator|*
name|sinB0
decl_stmt|;
specifier|final
name|double
name|t1
init|=
name|z
operator|+
name|SEMIMINOR_AXIS
operator|*
name|EP2
operator|*
name|sin3B0
decl_stmt|;
specifier|final
name|double
name|sum
init|=
name|w
operator|-
name|SEMIMAJOR_AXIS
operator|*
name|E2
operator|*
name|cosB0
operator|*
name|cosB0
operator|*
name|cosB0
decl_stmt|;
specifier|final
name|double
name|s1
init|=
name|StrictMath
operator|.
name|sqrt
argument_list|(
name|t1
operator|*
name|t1
operator|+
name|sum
operator|*
name|sum
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinP1
init|=
name|t1
operator|/
name|s1
decl_stmt|;
specifier|final
name|double
name|cosP1
init|=
name|sum
operator|/
name|s1
decl_stmt|;
specifier|final
name|double
name|rn
init|=
name|SEMIMAJOR_AXIS
operator|/
name|StrictMath
operator|.
name|sqrt
argument_list|(
literal|1.0D
operator|-
name|E2
operator|*
name|sinP1
operator|*
name|sinP1
argument_list|)
decl_stmt|;
if|if
condition|(
name|cosP1
operator|>=
name|cos67P5
condition|)
block|{
name|lla
index|[
literal|2
index|]
operator|=
name|w
operator|/
name|cosP1
operator|-
name|rn
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cosP1
operator|<=
operator|-
name|cos67P5
condition|)
block|{
name|lla
index|[
literal|2
index|]
operator|=
name|w
operator|/
operator|-
name|cosP1
operator|-
name|rn
expr_stmt|;
block|}
else|else
block|{
name|lla
index|[
literal|2
index|]
operator|=
name|z
operator|/
name|sinP1
operator|+
name|rn
operator|*
operator|(
name|E2
operator|-
literal|1.0
operator|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|atPole
condition|)
block|{
name|lla
index|[
literal|1
index|]
operator|=
name|StrictMath
operator|.
name|atan
argument_list|(
name|sinP1
operator|/
name|cosP1
argument_list|)
expr_stmt|;
block|}
name|lla
index|[
literal|0
index|]
operator|=
name|TO_DEGREES
operator|*
name|lla
index|[
literal|0
index|]
expr_stmt|;
name|lla
index|[
literal|1
index|]
operator|=
name|TO_DEGREES
operator|*
name|lla
index|[
literal|1
index|]
expr_stmt|;
return|return
name|lla
return|;
block|}
comment|/**    * Converts from geodesic lon lat alt to geocentric earth-centered earth-fixed    * @param lon geodesic longitude    * @param lat geodesic latitude    * @param alt geodesic altitude    * @param ecf reusable earth-centered earth-fixed result    * @return either a new ecef array or the reusable ecf parameter    */
DECL|method|llaToECF
specifier|public
specifier|static
specifier|final
name|double
index|[]
name|llaToECF
parameter_list|(
name|double
name|lon
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|alt
parameter_list|,
name|double
index|[]
name|ecf
parameter_list|)
block|{
name|lon
operator|=
name|TO_RADIANS
operator|*
name|lon
expr_stmt|;
name|lat
operator|=
name|TO_RADIANS
operator|*
name|lat
expr_stmt|;
specifier|final
name|double
name|sl
init|=
name|SloppyMath
operator|.
name|sin
argument_list|(
name|lat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|s2
init|=
name|sl
operator|*
name|sl
decl_stmt|;
specifier|final
name|double
name|cl
init|=
name|SloppyMath
operator|.
name|cos
argument_list|(
name|lat
argument_list|)
decl_stmt|;
if|if
condition|(
name|ecf
operator|==
literal|null
condition|)
block|{
name|ecf
operator|=
operator|new
name|double
index|[
literal|3
index|]
expr_stmt|;
block|}
if|if
condition|(
name|lat
argument_list|<
operator|-
name|PIO2
operator|&&
name|lat
argument_list|>
operator|-
literal|1.001D
operator|*
name|PIO2
condition|)
block|{
name|lat
operator|=
operator|-
name|PIO2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lat
operator|>
name|PIO2
operator|&&
name|lat
operator|<
literal|1.001D
operator|*
name|PIO2
condition|)
block|{
name|lat
operator|=
name|PIO2
expr_stmt|;
block|}
assert|assert
operator|(
name|lat
operator|>=
operator|-
name|PIO2
operator|)
operator|||
operator|(
name|lat
operator|<=
name|PIO2
operator|)
assert|;
if|if
condition|(
name|lon
operator|>
name|StrictMath
operator|.
name|PI
condition|)
block|{
name|lon
operator|-=
operator|(
literal|2
operator|*
name|StrictMath
operator|.
name|PI
operator|)
expr_stmt|;
block|}
specifier|final
name|double
name|rn
init|=
name|SEMIMAJOR_AXIS
operator|/
name|StrictMath
operator|.
name|sqrt
argument_list|(
literal|1.0D
operator|-
name|E2
operator|*
name|s2
argument_list|)
decl_stmt|;
name|ecf
index|[
literal|0
index|]
operator|=
operator|(
name|rn
operator|+
name|alt
operator|)
operator|*
name|cl
operator|*
name|SloppyMath
operator|.
name|cos
argument_list|(
name|lon
argument_list|)
expr_stmt|;
name|ecf
index|[
literal|1
index|]
operator|=
operator|(
name|rn
operator|+
name|alt
operator|)
operator|*
name|cl
operator|*
name|SloppyMath
operator|.
name|sin
argument_list|(
name|lon
argument_list|)
expr_stmt|;
name|ecf
index|[
literal|2
index|]
operator|=
operator|(
operator|(
name|rn
operator|*
operator|(
literal|1.0
operator|-
name|E2
operator|)
operator|)
operator|+
name|alt
operator|)
operator|*
name|sl
expr_stmt|;
return|return
name|ecf
return|;
block|}
comment|/**    * Converts from lat lon alt (in degrees) to East North Up right-hand coordinate system    * @param lon longitude in degrees    * @param lat latitude in degrees    * @param alt altitude in meters    * @param centerLon reference point longitude in degrees    * @param centerLat reference point latitude in degrees    * @param centerAlt reference point altitude in meters    * @param enu result east, north, up coordinate    * @return east, north, up coordinate    */
DECL|method|llaToENU
specifier|public
specifier|static
name|double
index|[]
name|llaToENU
parameter_list|(
specifier|final
name|double
name|lon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|double
name|alt
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|centerAlt
parameter_list|,
name|double
index|[]
name|enu
parameter_list|)
block|{
if|if
condition|(
name|enu
operator|==
literal|null
condition|)
block|{
name|enu
operator|=
operator|new
name|double
index|[
literal|3
index|]
expr_stmt|;
block|}
comment|// convert point to ecf coordinates
specifier|final
name|double
index|[]
name|ecf
init|=
name|llaToECF
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|,
name|alt
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// convert from ecf to enu
return|return
name|ecfToENU
argument_list|(
name|ecf
index|[
literal|0
index|]
argument_list|,
name|ecf
index|[
literal|1
index|]
argument_list|,
name|ecf
index|[
literal|2
index|]
argument_list|,
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|centerAlt
argument_list|,
name|enu
argument_list|)
return|;
block|}
comment|/**    * Converts from East North Up right-hand rule to lat lon alt in degrees    * @param x easting (in meters)    * @param y northing (in meters)    * @param z up (in meters)    * @param centerLon reference point longitude (in degrees)    * @param centerLat reference point latitude (in degrees)    * @param centerAlt reference point altitude (in meters)    * @param lla resulting lat, lon, alt point (in degrees)    * @return lat, lon, alt point (in degrees)    */
DECL|method|enuToLLA
specifier|public
specifier|static
name|double
index|[]
name|enuToLLA
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
parameter_list|,
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|centerAlt
parameter_list|,
name|double
index|[]
name|lla
parameter_list|)
block|{
comment|// convert enuToECF
if|if
condition|(
name|lla
operator|==
literal|null
condition|)
block|{
name|lla
operator|=
operator|new
name|double
index|[
literal|3
index|]
expr_stmt|;
block|}
comment|// convert enuToECF, storing intermediate result in lla
name|lla
operator|=
name|enuToECF
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|,
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|centerAlt
argument_list|,
name|lla
argument_list|)
expr_stmt|;
comment|// convert ecf to LLA
return|return
name|ecfToLLA
argument_list|(
name|lla
index|[
literal|0
index|]
argument_list|,
name|lla
index|[
literal|1
index|]
argument_list|,
name|lla
index|[
literal|2
index|]
argument_list|,
name|lla
argument_list|)
return|;
block|}
comment|/**    * Convert from Earth-Centered-Fixed to Easting, Northing, Up Right Hand System    * @param x ECF X coordinate (in meters)    * @param y ECF Y coordinate (in meters)    * @param z ECF Z coordinate (in meters)    * @param centerLon ENU origin longitude (in degrees)    * @param centerLat ENU origin latitude (in degrees)    * @param centerAlt ENU altitude (in meters)    * @param enu reusable enu result    * @return Easting, Northing, Up coordinate    */
DECL|method|ecfToENU
specifier|public
specifier|static
name|double
index|[]
name|ecfToENU
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|z
parameter_list|,
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|centerAlt
parameter_list|,
name|double
index|[]
name|enu
parameter_list|)
block|{
if|if
condition|(
name|enu
operator|==
literal|null
condition|)
block|{
name|enu
operator|=
operator|new
name|double
index|[
literal|3
index|]
expr_stmt|;
block|}
comment|// create rotation matrix and rotate to enu orientation
specifier|final
name|double
index|[]
index|[]
name|phi
init|=
name|createPhiTransform
argument_list|(
name|centerLon
argument_list|,
name|centerLat
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// convert origin to ENU
specifier|final
name|double
index|[]
name|originECF
init|=
name|llaToECF
argument_list|(
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|centerAlt
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|double
index|[]
name|originENU
init|=
operator|new
name|double
index|[
literal|3
index|]
decl_stmt|;
name|originENU
index|[
literal|0
index|]
operator|=
operator|(
operator|(
name|phi
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|*
name|originECF
index|[
literal|0
index|]
operator|)
operator|+
operator|(
name|phi
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|*
name|originECF
index|[
literal|1
index|]
operator|)
operator|+
operator|(
name|phi
index|[
literal|0
index|]
index|[
literal|2
index|]
operator|*
name|originECF
index|[
literal|2
index|]
operator|)
operator|)
expr_stmt|;
name|originENU
index|[
literal|1
index|]
operator|=
operator|(
operator|(
name|phi
index|[
literal|1
index|]
index|[
literal|0
index|]
operator|*
name|originECF
index|[
literal|0
index|]
operator|)
operator|+
operator|(
name|phi
index|[
literal|1
index|]
index|[
literal|1
index|]
operator|*
name|originECF
index|[
literal|1
index|]
operator|)
operator|+
operator|(
name|phi
index|[
literal|1
index|]
index|[
literal|2
index|]
operator|*
name|originECF
index|[
literal|2
index|]
operator|)
operator|)
expr_stmt|;
name|originENU
index|[
literal|2
index|]
operator|=
operator|(
operator|(
name|phi
index|[
literal|2
index|]
index|[
literal|0
index|]
operator|*
name|originECF
index|[
literal|0
index|]
operator|)
operator|+
operator|(
name|phi
index|[
literal|2
index|]
index|[
literal|1
index|]
operator|*
name|originECF
index|[
literal|1
index|]
operator|)
operator|+
operator|(
name|phi
index|[
literal|2
index|]
index|[
literal|2
index|]
operator|*
name|originECF
index|[
literal|2
index|]
operator|)
operator|)
expr_stmt|;
comment|// rotate then translate
name|enu
index|[
literal|0
index|]
operator|=
operator|(
operator|(
name|phi
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|*
name|x
operator|)
operator|+
operator|(
name|phi
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|*
name|y
operator|)
operator|+
operator|(
name|phi
index|[
literal|0
index|]
index|[
literal|2
index|]
operator|*
name|z
operator|)
operator|)
operator|-
name|originENU
index|[
literal|0
index|]
expr_stmt|;
name|enu
index|[
literal|1
index|]
operator|=
operator|(
operator|(
name|phi
index|[
literal|1
index|]
index|[
literal|0
index|]
operator|*
name|x
operator|)
operator|+
operator|(
name|phi
index|[
literal|1
index|]
index|[
literal|1
index|]
operator|*
name|y
operator|)
operator|+
operator|(
name|phi
index|[
literal|1
index|]
index|[
literal|2
index|]
operator|*
name|z
operator|)
operator|)
operator|-
name|originENU
index|[
literal|1
index|]
expr_stmt|;
name|enu
index|[
literal|2
index|]
operator|=
operator|(
operator|(
name|phi
index|[
literal|2
index|]
index|[
literal|0
index|]
operator|*
name|x
operator|)
operator|+
operator|(
name|phi
index|[
literal|2
index|]
index|[
literal|1
index|]
operator|*
name|y
operator|)
operator|+
operator|(
name|phi
index|[
literal|2
index|]
index|[
literal|2
index|]
operator|*
name|z
operator|)
operator|)
operator|-
name|originENU
index|[
literal|2
index|]
expr_stmt|;
return|return
name|enu
return|;
block|}
comment|/**    * Convert from Easting, Northing, Up Right-Handed system to Earth Centered Fixed system    * @param x ENU x coordinate (in meters)    * @param y ENU y coordinate (in meters)    * @param z ENU z coordinate (in meters)    * @param centerLon ENU origin longitude (in degrees)    * @param centerLat ENU origin latitude (in degrees)    * @param centerAlt ENU origin altitude (in meters)    * @param ecf reusable ecf result    * @return ecf result coordinate    */
DECL|method|enuToECF
specifier|public
specifier|static
name|double
index|[]
name|enuToECF
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
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|centerAlt
parameter_list|,
name|double
index|[]
name|ecf
parameter_list|)
block|{
if|if
condition|(
name|ecf
operator|==
literal|null
condition|)
block|{
name|ecf
operator|=
operator|new
name|double
index|[
literal|3
index|]
expr_stmt|;
block|}
name|double
index|[]
index|[]
name|phi
init|=
name|createTransposedPhiTransform
argument_list|(
name|centerLon
argument_list|,
name|centerLat
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|double
index|[]
name|ecfOrigin
init|=
name|llaToECF
argument_list|(
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|centerAlt
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// rotate and translate
name|ecf
index|[
literal|0
index|]
operator|=
operator|(
name|phi
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|*
name|x
operator|+
name|phi
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|*
name|y
operator|+
name|phi
index|[
literal|0
index|]
index|[
literal|2
index|]
operator|*
name|z
operator|)
operator|+
name|ecfOrigin
index|[
literal|0
index|]
expr_stmt|;
name|ecf
index|[
literal|1
index|]
operator|=
operator|(
name|phi
index|[
literal|1
index|]
index|[
literal|0
index|]
operator|*
name|x
operator|+
name|phi
index|[
literal|1
index|]
index|[
literal|1
index|]
operator|*
name|y
operator|+
name|phi
index|[
literal|1
index|]
index|[
literal|2
index|]
operator|*
name|z
operator|)
operator|+
name|ecfOrigin
index|[
literal|1
index|]
expr_stmt|;
name|ecf
index|[
literal|2
index|]
operator|=
operator|(
name|phi
index|[
literal|2
index|]
index|[
literal|0
index|]
operator|*
name|x
operator|+
name|phi
index|[
literal|2
index|]
index|[
literal|1
index|]
operator|*
name|y
operator|+
name|phi
index|[
literal|2
index|]
index|[
literal|2
index|]
operator|*
name|z
operator|)
operator|+
name|ecfOrigin
index|[
literal|2
index|]
expr_stmt|;
return|return
name|ecf
return|;
block|}
comment|/**    * Create the rotation matrix for converting Earth Centered Fixed to Easting Northing Up    * @param originLon ENU origin longitude (in degrees)    * @param originLat ENU origin latitude (in degrees)    * @param phiMatrix reusable phi matrix result    * @return phi rotation matrix    */
DECL|method|createPhiTransform
specifier|private
specifier|static
name|double
index|[]
index|[]
name|createPhiTransform
parameter_list|(
name|double
name|originLon
parameter_list|,
name|double
name|originLat
parameter_list|,
name|double
index|[]
index|[]
name|phiMatrix
parameter_list|)
block|{
if|if
condition|(
name|phiMatrix
operator|==
literal|null
condition|)
block|{
name|phiMatrix
operator|=
operator|new
name|double
index|[
literal|3
index|]
index|[
literal|3
index|]
expr_stmt|;
block|}
name|originLon
operator|=
name|TO_RADIANS
operator|*
name|originLon
expr_stmt|;
name|originLat
operator|=
name|TO_RADIANS
operator|*
name|originLat
expr_stmt|;
specifier|final
name|double
name|sLon
init|=
name|SloppyMath
operator|.
name|sin
argument_list|(
name|originLon
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cLon
init|=
name|SloppyMath
operator|.
name|cos
argument_list|(
name|originLon
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sLat
init|=
name|SloppyMath
operator|.
name|sin
argument_list|(
name|originLat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cLat
init|=
name|SloppyMath
operator|.
name|cos
argument_list|(
name|originLat
argument_list|)
decl_stmt|;
name|phiMatrix
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|=
operator|-
name|sLon
expr_stmt|;
name|phiMatrix
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|=
name|cLon
expr_stmt|;
name|phiMatrix
index|[
literal|0
index|]
index|[
literal|2
index|]
operator|=
literal|0.0D
expr_stmt|;
name|phiMatrix
index|[
literal|1
index|]
index|[
literal|0
index|]
operator|=
operator|-
name|sLat
operator|*
name|cLon
expr_stmt|;
name|phiMatrix
index|[
literal|1
index|]
index|[
literal|1
index|]
operator|=
operator|-
name|sLat
operator|*
name|sLon
expr_stmt|;
name|phiMatrix
index|[
literal|1
index|]
index|[
literal|2
index|]
operator|=
name|cLat
expr_stmt|;
name|phiMatrix
index|[
literal|2
index|]
index|[
literal|0
index|]
operator|=
name|cLat
operator|*
name|cLon
expr_stmt|;
name|phiMatrix
index|[
literal|2
index|]
index|[
literal|1
index|]
operator|=
name|cLat
operator|*
name|sLon
expr_stmt|;
name|phiMatrix
index|[
literal|2
index|]
index|[
literal|2
index|]
operator|=
name|sLat
expr_stmt|;
return|return
name|phiMatrix
return|;
block|}
comment|/**    * Create the transposed rotation matrix for converting Easting Northing Up coordinates to Earth Centered Fixed    * @param originLon ENU origin longitude (in degrees)    * @param originLat ENU origin latitude (in degrees)    * @param phiMatrix reusable phi rotation matrix result    * @return transposed phi rotation matrix    */
DECL|method|createTransposedPhiTransform
specifier|private
specifier|static
name|double
index|[]
index|[]
name|createTransposedPhiTransform
parameter_list|(
name|double
name|originLon
parameter_list|,
name|double
name|originLat
parameter_list|,
name|double
index|[]
index|[]
name|phiMatrix
parameter_list|)
block|{
if|if
condition|(
name|phiMatrix
operator|==
literal|null
condition|)
block|{
name|phiMatrix
operator|=
operator|new
name|double
index|[
literal|3
index|]
index|[
literal|3
index|]
expr_stmt|;
block|}
name|originLon
operator|=
name|TO_RADIANS
operator|*
name|originLon
expr_stmt|;
name|originLat
operator|=
name|TO_RADIANS
operator|*
name|originLat
expr_stmt|;
specifier|final
name|double
name|sLat
init|=
name|SloppyMath
operator|.
name|sin
argument_list|(
name|originLat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cLat
init|=
name|SloppyMath
operator|.
name|cos
argument_list|(
name|originLat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sLon
init|=
name|SloppyMath
operator|.
name|sin
argument_list|(
name|originLon
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cLon
init|=
name|SloppyMath
operator|.
name|cos
argument_list|(
name|originLon
argument_list|)
decl_stmt|;
name|phiMatrix
index|[
literal|0
index|]
index|[
literal|0
index|]
operator|=
operator|-
name|sLon
expr_stmt|;
name|phiMatrix
index|[
literal|1
index|]
index|[
literal|0
index|]
operator|=
name|cLon
expr_stmt|;
name|phiMatrix
index|[
literal|2
index|]
index|[
literal|0
index|]
operator|=
literal|0.0D
expr_stmt|;
name|phiMatrix
index|[
literal|0
index|]
index|[
literal|1
index|]
operator|=
operator|-
name|sLat
operator|*
name|cLon
expr_stmt|;
name|phiMatrix
index|[
literal|1
index|]
index|[
literal|1
index|]
operator|=
operator|-
name|sLat
operator|*
name|sLon
expr_stmt|;
name|phiMatrix
index|[
literal|2
index|]
index|[
literal|1
index|]
operator|=
name|cLat
expr_stmt|;
name|phiMatrix
index|[
literal|0
index|]
index|[
literal|2
index|]
operator|=
name|cLat
operator|*
name|cLon
expr_stmt|;
name|phiMatrix
index|[
literal|1
index|]
index|[
literal|2
index|]
operator|=
name|cLat
operator|*
name|sLon
expr_stmt|;
name|phiMatrix
index|[
literal|2
index|]
index|[
literal|2
index|]
operator|=
name|sLat
expr_stmt|;
return|return
name|phiMatrix
return|;
block|}
comment|/**    * Finds a point along a bearing from a given lon,lat geolocation using vincenty's distance formula    *    * @param lon origin longitude in degrees    * @param lat origin latitude in degrees    * @param bearing azimuthal bearing in degrees    * @param dist distance in meters    * @param pt resulting point    * @return the point along a bearing at a given distance in meters    */
DECL|method|pointFromLonLatBearingVincenty
specifier|public
specifier|static
specifier|final
name|double
index|[]
name|pointFromLonLatBearingVincenty
parameter_list|(
name|double
name|lon
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|bearing
parameter_list|,
name|double
name|dist
parameter_list|,
name|double
index|[]
name|pt
parameter_list|)
block|{
if|if
condition|(
name|pt
operator|==
literal|null
condition|)
block|{
name|pt
operator|=
operator|new
name|double
index|[
literal|2
index|]
expr_stmt|;
block|}
specifier|final
name|double
name|alpha1
init|=
name|TO_RADIANS
operator|*
name|bearing
decl_stmt|;
specifier|final
name|double
name|cosA1
init|=
name|SloppyMath
operator|.
name|cos
argument_list|(
name|alpha1
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinA1
init|=
name|SloppyMath
operator|.
name|sin
argument_list|(
name|alpha1
argument_list|)
decl_stmt|;
specifier|final
name|double
name|tanU1
init|=
operator|(
literal|1
operator|-
name|FLATTENING
operator|)
operator|*
name|SloppyMath
operator|.
name|tan
argument_list|(
name|TO_RADIANS
operator|*
name|lat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosU1
init|=
literal|1
operator|/
name|StrictMath
operator|.
name|sqrt
argument_list|(
operator|(
literal|1
operator|+
name|tanU1
operator|*
name|tanU1
operator|)
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinU1
init|=
name|tanU1
operator|*
name|cosU1
decl_stmt|;
specifier|final
name|double
name|sig1
init|=
name|StrictMath
operator|.
name|atan2
argument_list|(
name|tanU1
argument_list|,
name|cosA1
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinAlpha
init|=
name|cosU1
operator|*
name|sinA1
decl_stmt|;
specifier|final
name|double
name|cosSqAlpha
init|=
literal|1
operator|-
name|sinAlpha
operator|*
name|sinAlpha
decl_stmt|;
specifier|final
name|double
name|uSq
init|=
name|cosSqAlpha
operator|*
name|EP2
decl_stmt|;
specifier|final
name|double
name|A
init|=
literal|1
operator|+
name|uSq
operator|/
literal|16384D
operator|*
operator|(
literal|4096D
operator|+
name|uSq
operator|*
operator|(
operator|-
literal|768D
operator|+
name|uSq
operator|*
operator|(
literal|320D
operator|-
literal|175D
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
literal|1024D
operator|*
operator|(
literal|256D
operator|+
name|uSq
operator|*
operator|(
operator|-
literal|128D
operator|+
name|uSq
operator|*
operator|(
literal|74D
operator|-
literal|47D
operator|*
name|uSq
operator|)
operator|)
operator|)
decl_stmt|;
name|double
name|sigma
init|=
name|dist
operator|/
operator|(
name|SEMIMINOR_AXIS
operator|*
name|A
operator|)
decl_stmt|;
name|double
name|sigmaP
decl_stmt|;
name|double
name|sinSigma
decl_stmt|,
name|cosSigma
decl_stmt|,
name|cos2SigmaM
decl_stmt|,
name|deltaSigma
decl_stmt|;
do|do
block|{
name|cos2SigmaM
operator|=
name|SloppyMath
operator|.
name|cos
argument_list|(
literal|2
operator|*
name|sig1
operator|+
name|sigma
argument_list|)
expr_stmt|;
name|sinSigma
operator|=
name|SloppyMath
operator|.
name|sin
argument_list|(
name|sigma
argument_list|)
expr_stmt|;
name|cosSigma
operator|=
name|SloppyMath
operator|.
name|cos
argument_list|(
name|sigma
argument_list|)
expr_stmt|;
name|deltaSigma
operator|=
name|B
operator|*
name|sinSigma
operator|*
operator|(
name|cos2SigmaM
operator|+
operator|(
name|B
operator|/
literal|4D
operator|)
operator|*
operator|(
name|cosSigma
operator|*
operator|(
operator|-
literal|1
operator|+
literal|2
operator|*
name|cos2SigmaM
operator|*
name|cos2SigmaM
operator|)
operator|-
operator|(
name|B
operator|/
literal|6
operator|)
operator|*
name|cos2SigmaM
operator|*
operator|(
operator|-
literal|3
operator|+
literal|4
operator|*
name|sinSigma
operator|*
name|sinSigma
operator|)
operator|*
operator|(
operator|-
literal|3
operator|+
literal|4
operator|*
name|cos2SigmaM
operator|*
name|cos2SigmaM
operator|)
operator|)
operator|)
expr_stmt|;
name|sigmaP
operator|=
name|sigma
expr_stmt|;
name|sigma
operator|=
name|dist
operator|/
operator|(
name|SEMIMINOR_AXIS
operator|*
name|A
operator|)
operator|+
name|deltaSigma
expr_stmt|;
block|}
do|while
condition|(
name|StrictMath
operator|.
name|abs
argument_list|(
name|sigma
operator|-
name|sigmaP
argument_list|)
operator|>
literal|1E
operator|-
literal|12
condition|)
do|;
specifier|final
name|double
name|tmp
init|=
name|sinU1
operator|*
name|sinSigma
operator|-
name|cosU1
operator|*
name|cosSigma
operator|*
name|cosA1
decl_stmt|;
specifier|final
name|double
name|lat2
init|=
name|StrictMath
operator|.
name|atan2
argument_list|(
name|sinU1
operator|*
name|cosSigma
operator|+
name|cosU1
operator|*
name|sinSigma
operator|*
name|cosA1
argument_list|,
operator|(
literal|1
operator|-
name|FLATTENING
operator|)
operator|*
name|StrictMath
operator|.
name|sqrt
argument_list|(
name|sinAlpha
operator|*
name|sinAlpha
operator|+
name|tmp
operator|*
name|tmp
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|double
name|lambda
init|=
name|StrictMath
operator|.
name|atan2
argument_list|(
name|sinSigma
operator|*
name|sinA1
argument_list|,
name|cosU1
operator|*
name|cosSigma
operator|-
name|sinU1
operator|*
name|sinSigma
operator|*
name|cosA1
argument_list|)
decl_stmt|;
specifier|final
name|double
name|c
init|=
name|FLATTENING
operator|/
literal|16
operator|*
name|cosSqAlpha
operator|*
operator|(
literal|4
operator|+
name|FLATTENING
operator|*
operator|(
literal|4
operator|-
literal|3
operator|*
name|cosSqAlpha
operator|)
operator|)
decl_stmt|;
specifier|final
name|double
name|lam
init|=
name|lambda
operator|-
operator|(
literal|1
operator|-
name|c
operator|)
operator|*
name|FLATTENING
operator|*
name|sinAlpha
operator|*
operator|(
name|sigma
operator|+
name|c
operator|*
name|sinSigma
operator|*
operator|(
name|cos2SigmaM
operator|+
name|c
operator|*
name|cosSigma
operator|*
operator|(
operator|-
literal|1
operator|+
literal|2
operator|*
name|cos2SigmaM
operator|*
name|cos2SigmaM
operator|)
operator|)
operator|)
decl_stmt|;
name|pt
index|[
literal|0
index|]
operator|=
name|GeoUtils
operator|.
name|normalizeLon
argument_list|(
name|lon
operator|+
name|TO_DEGREES
operator|*
name|lam
argument_list|)
expr_stmt|;
name|pt
index|[
literal|1
index|]
operator|=
name|GeoUtils
operator|.
name|normalizeLat
argument_list|(
name|TO_DEGREES
operator|*
name|lat2
argument_list|)
expr_stmt|;
return|return
name|pt
return|;
block|}
comment|/**    * Finds a point along a bearing from a given lon,lat geolocation using great circle arc    *    * @param lon origin longitude in degrees    * @param lat origin latitude in degrees    * @param bearing azimuthal bearing in degrees    * @param dist distance in meters    * @param pt resulting point    * @return the point along a bearing at a given distance in meters    */
DECL|method|pointFromLonLatBearingGreatCircle
specifier|public
specifier|static
specifier|final
name|double
index|[]
name|pointFromLonLatBearingGreatCircle
parameter_list|(
name|double
name|lon
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|bearing
parameter_list|,
name|double
name|dist
parameter_list|,
name|double
index|[]
name|pt
parameter_list|)
block|{
if|if
condition|(
name|pt
operator|==
literal|null
condition|)
block|{
name|pt
operator|=
operator|new
name|double
index|[
literal|2
index|]
expr_stmt|;
block|}
name|lon
operator|*=
name|TO_RADIANS
expr_stmt|;
name|lat
operator|*=
name|TO_RADIANS
expr_stmt|;
name|bearing
operator|*=
name|TO_RADIANS
expr_stmt|;
specifier|final
name|double
name|cLat
init|=
name|SloppyMath
operator|.
name|cos
argument_list|(
name|lat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sLat
init|=
name|SloppyMath
operator|.
name|sin
argument_list|(
name|lat
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sinDoR
init|=
name|SloppyMath
operator|.
name|sin
argument_list|(
name|dist
operator|/
name|GeoProjectionUtils
operator|.
name|SEMIMAJOR_AXIS
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cosDoR
init|=
name|SloppyMath
operator|.
name|cos
argument_list|(
name|dist
operator|/
name|GeoProjectionUtils
operator|.
name|SEMIMAJOR_AXIS
argument_list|)
decl_stmt|;
name|pt
index|[
literal|1
index|]
operator|=
name|SloppyMath
operator|.
name|asin
argument_list|(
name|sLat
operator|*
name|cosDoR
operator|+
name|cLat
operator|*
name|sinDoR
operator|*
name|SloppyMath
operator|.
name|cos
argument_list|(
name|bearing
argument_list|)
argument_list|)
expr_stmt|;
name|pt
index|[
literal|0
index|]
operator|=
name|TO_DEGREES
operator|*
operator|(
name|lon
operator|+
name|Math
operator|.
name|atan2
argument_list|(
name|SloppyMath
operator|.
name|sin
argument_list|(
name|bearing
argument_list|)
operator|*
name|sinDoR
operator|*
name|cLat
argument_list|,
name|cosDoR
operator|-
name|sLat
operator|*
name|SloppyMath
operator|.
name|sin
argument_list|(
name|pt
index|[
literal|1
index|]
argument_list|)
argument_list|)
operator|)
expr_stmt|;
name|pt
index|[
literal|1
index|]
operator|*=
name|TO_DEGREES
expr_stmt|;
return|return
name|pt
return|;
block|}
comment|/**    * Finds the bearing (in degrees) between 2 geo points (lon, lat) using great circle arc    * @param lon1 first point longitude in degrees    * @param lat1 first point latitude in degrees    * @param lon2 second point longitude in degrees    * @param lat2 second point latitude in degrees    * @return the bearing (in degrees) between the two provided points    */
DECL|method|bearingGreatCircle
specifier|public
specifier|static
name|double
name|bearingGreatCircle
parameter_list|(
name|double
name|lon1
parameter_list|,
name|double
name|lat1
parameter_list|,
name|double
name|lon2
parameter_list|,
name|double
name|lat2
parameter_list|)
block|{
name|double
name|dLon
init|=
operator|(
name|lon2
operator|-
name|lon1
operator|)
operator|*
name|TO_RADIANS
decl_stmt|;
name|lat2
operator|*=
name|TO_RADIANS
expr_stmt|;
name|lat1
operator|*=
name|TO_RADIANS
expr_stmt|;
name|double
name|y
init|=
name|SloppyMath
operator|.
name|sin
argument_list|(
name|dLon
argument_list|)
operator|*
name|SloppyMath
operator|.
name|cos
argument_list|(
name|lat2
argument_list|)
decl_stmt|;
name|double
name|x
init|=
name|SloppyMath
operator|.
name|cos
argument_list|(
name|lat1
argument_list|)
operator|*
name|SloppyMath
operator|.
name|sin
argument_list|(
name|lat2
argument_list|)
operator|-
name|SloppyMath
operator|.
name|sin
argument_list|(
name|lat1
argument_list|)
operator|*
name|SloppyMath
operator|.
name|cos
argument_list|(
name|lat2
argument_list|)
operator|*
name|SloppyMath
operator|.
name|cos
argument_list|(
name|dLon
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|atan2
argument_list|(
name|y
argument_list|,
name|x
argument_list|)
operator|*
name|TO_DEGREES
return|;
block|}
block|}
end_class
end_unit
