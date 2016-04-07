begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
comment|/* some code derived from jodk: http://code.google.com/p/jodk/ (apache 2.0)  * asin() derived from fdlibm: http://www.netlib.org/fdlibm/e_asin.c (public domain):  * =============================================================================  * Copyright (C) 1993 by Sun Microsystems, Inc. All rights reserved.  *  * Developed at SunSoft, a Sun Microsystems, Inc. business.  * Permission to use, copy, modify, and distribute this  * software is freely granted, provided that this notice   * is preserved.  * =============================================================================  */
end_comment
begin_comment
comment|/** Math functions that trade off accuracy for speed. */
end_comment
begin_class
DECL|class|SloppyMath
specifier|public
class|class
name|SloppyMath
block|{
comment|/**    * Returns the Haversine distance in meters between two points    * specified in decimal degrees (latitude/longitude).  This works correctly    * even if the dateline is between the two points.    *<p>    * Error is at most 2E-1 (20cm) from the actual haversine distance, but is typically    * much smaller for reasonable distances: around 1E-5 (0.01mm) for distances less than    * 1000km.    *    * @param lat1 Latitude of the first point.    * @param lon1 Longitude of the first point.    * @param lat2 Latitude of the second point.    * @param lon2 Longitude of the second point.    * @return distance in meters.    */
DECL|method|haversinMeters
specifier|public
specifier|static
name|double
name|haversinMeters
parameter_list|(
name|double
name|lat1
parameter_list|,
name|double
name|lon1
parameter_list|,
name|double
name|lat2
parameter_list|,
name|double
name|lon2
parameter_list|)
block|{
return|return
name|haversinMeters
argument_list|(
name|haversinSortKey
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the Haversine distance in meters between two points    * given the previous result from {@link #haversinSortKey(double, double, double, double)}    * @return distance in meters.    */
DECL|method|haversinMeters
specifier|public
specifier|static
name|double
name|haversinMeters
parameter_list|(
name|double
name|sortKey
parameter_list|)
block|{
return|return
name|TO_METERS
operator|*
literal|2
operator|*
name|asin
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|1
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
name|sortKey
operator|*
literal|0.5
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the Haversine distance in kilometers between two points    * specified in decimal degrees (latitude/longitude).  This works correctly    * even if the dateline is between the two points.    *    * @param lat1 Latitude of the first point.    * @param lon1 Longitude of the first point.    * @param lat2 Latitude of the second point.    * @param lon2 Longitude of the second point.    * @return distance in kilometers.    * @deprecated Use {@link #haversinMeters(double, double, double, double) instead}    */
annotation|@
name|Deprecated
DECL|method|haversinKilometers
specifier|public
specifier|static
name|double
name|haversinKilometers
parameter_list|(
name|double
name|lat1
parameter_list|,
name|double
name|lon1
parameter_list|,
name|double
name|lat2
parameter_list|,
name|double
name|lon2
parameter_list|)
block|{
name|double
name|h
init|=
name|haversinSortKey
argument_list|(
name|lat1
argument_list|,
name|lon1
argument_list|,
name|lat2
argument_list|,
name|lon2
argument_list|)
decl_stmt|;
return|return
name|TO_KILOMETERS
operator|*
literal|2
operator|*
name|asin
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|1
argument_list|,
name|Math
operator|.
name|sqrt
argument_list|(
name|h
operator|*
literal|0.5
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns a sort key for distance. This is less expensive to compute than     * {@link #haversinMeters(double, double, double, double)}, but it always compares the same.    * This can be converted into an actual distance with {@link #haversinMeters(double)}, which    * effectively does the second half of the computation.    */
DECL|method|haversinSortKey
specifier|public
specifier|static
name|double
name|haversinSortKey
parameter_list|(
name|double
name|lat1
parameter_list|,
name|double
name|lon1
parameter_list|,
name|double
name|lat2
parameter_list|,
name|double
name|lon2
parameter_list|)
block|{
name|double
name|x1
init|=
name|lat1
operator|*
name|TO_RADIANS
decl_stmt|;
name|double
name|x2
init|=
name|lat2
operator|*
name|TO_RADIANS
decl_stmt|;
name|double
name|h1
init|=
literal|1
operator|-
name|cos
argument_list|(
name|x1
operator|-
name|x2
argument_list|)
decl_stmt|;
name|double
name|h2
init|=
literal|1
operator|-
name|cos
argument_list|(
operator|(
name|lon1
operator|-
name|lon2
operator|)
operator|*
name|TO_RADIANS
argument_list|)
decl_stmt|;
name|double
name|h
init|=
name|h1
operator|+
name|cos
argument_list|(
name|x1
argument_list|)
operator|*
name|cos
argument_list|(
name|x2
argument_list|)
operator|*
name|h2
decl_stmt|;
comment|// clobber crazy precision so subsequent rounding does not create ties.
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|h
argument_list|)
operator|&
literal|0xFFFFFFFFFFFFFFF8L
argument_list|)
return|;
block|}
comment|/**    * Returns the trigonometric cosine of an angle.    *<p>    * Error is around 1E-15.    *<p>    * Special cases:    *<ul>    *<li>If the argument is {@code NaN} or an infinity, then the result is {@code NaN}.    *</ul>    * @param a an angle, in radians.    * @return the cosine of the argument.    * @see Math#cos(double)    */
DECL|method|cos
specifier|public
specifier|static
name|double
name|cos
parameter_list|(
name|double
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|<
literal|0.0
condition|)
block|{
name|a
operator|=
operator|-
name|a
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|>
name|SIN_COS_MAX_VALUE_FOR_INT_MODULO
condition|)
block|{
return|return
name|Math
operator|.
name|cos
argument_list|(
name|a
argument_list|)
return|;
block|}
comment|// index: possibly outside tables range.
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|a
operator|*
name|SIN_COS_INDEXER
operator|+
literal|0.5
argument_list|)
decl_stmt|;
name|double
name|delta
init|=
operator|(
name|a
operator|-
name|index
operator|*
name|SIN_COS_DELTA_HI
operator|)
operator|-
name|index
operator|*
name|SIN_COS_DELTA_LO
decl_stmt|;
comment|// Making sure index is within tables range.
comment|// Last value of each table is the same than first, so we ignore it (tabs size minus one) for modulo.
name|index
operator|&=
operator|(
name|SIN_COS_TABS_SIZE
operator|-
literal|2
operator|)
expr_stmt|;
comment|// index % (SIN_COS_TABS_SIZE-1)
name|double
name|indexCos
init|=
name|cosTab
index|[
name|index
index|]
decl_stmt|;
name|double
name|indexSin
init|=
name|sinTab
index|[
name|index
index|]
decl_stmt|;
return|return
name|indexCos
operator|+
name|delta
operator|*
operator|(
operator|-
name|indexSin
operator|+
name|delta
operator|*
operator|(
operator|-
name|indexCos
operator|*
name|ONE_DIV_F2
operator|+
name|delta
operator|*
operator|(
name|indexSin
operator|*
name|ONE_DIV_F3
operator|+
name|delta
operator|*
name|indexCos
operator|*
name|ONE_DIV_F4
operator|)
operator|)
operator|)
return|;
block|}
comment|/**    * Returns the arc sine of a value.    *<p>    * The returned angle is in the range<i>-pi</i>/2 through<i>pi</i>/2.     * Error is around 1E-7.    *<p>    * Special cases:    *<ul>    *<li>If the argument is {@code NaN} or its absolute value is greater than 1, then the result is {@code NaN}.    *</ul>    * @param a the value whose arc sine is to be returned.    * @return arc sine of the argument    * @see Math#asin(double)    */
comment|// because asin(-x) = -asin(x), asin(x) only needs to be computed on [0,1].
comment|// ---> we only have to compute asin(x) on [0,1].
comment|// For values not close to +-1, we use look-up tables;
comment|// for values near +-1, we use code derived from fdlibm.
DECL|method|asin
specifier|public
specifier|static
name|double
name|asin
parameter_list|(
name|double
name|a
parameter_list|)
block|{
name|boolean
name|negateResult
decl_stmt|;
if|if
condition|(
name|a
operator|<
literal|0.0
condition|)
block|{
name|a
operator|=
operator|-
name|a
expr_stmt|;
name|negateResult
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|negateResult
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|<=
name|ASIN_MAX_VALUE_FOR_TABS
condition|)
block|{
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|a
operator|*
name|ASIN_INDEXER
operator|+
literal|0.5
argument_list|)
decl_stmt|;
name|double
name|delta
init|=
name|a
operator|-
name|index
operator|*
name|ASIN_DELTA
decl_stmt|;
name|double
name|result
init|=
name|asinTab
index|[
name|index
index|]
operator|+
name|delta
operator|*
operator|(
name|asinDer1DivF1Tab
index|[
name|index
index|]
operator|+
name|delta
operator|*
operator|(
name|asinDer2DivF2Tab
index|[
name|index
index|]
operator|+
name|delta
operator|*
operator|(
name|asinDer3DivF3Tab
index|[
name|index
index|]
operator|+
name|delta
operator|*
name|asinDer4DivF4Tab
index|[
name|index
index|]
operator|)
operator|)
operator|)
decl_stmt|;
return|return
name|negateResult
condition|?
operator|-
name|result
else|:
name|result
return|;
block|}
else|else
block|{
comment|// value> ASIN_MAX_VALUE_FOR_TABS, or value is NaN
comment|// This part is derived from fdlibm.
if|if
condition|(
name|a
operator|<
literal|1.0
condition|)
block|{
name|double
name|t
init|=
operator|(
literal|1.0
operator|-
name|a
operator|)
operator|*
literal|0.5
decl_stmt|;
name|double
name|p
init|=
name|t
operator|*
operator|(
name|ASIN_PS0
operator|+
name|t
operator|*
operator|(
name|ASIN_PS1
operator|+
name|t
operator|*
operator|(
name|ASIN_PS2
operator|+
name|t
operator|*
operator|(
name|ASIN_PS3
operator|+
name|t
operator|*
operator|(
name|ASIN_PS4
operator|+
name|t
operator|*
name|ASIN_PS5
operator|)
operator|)
operator|)
operator|)
operator|)
decl_stmt|;
name|double
name|q
init|=
literal|1.0
operator|+
name|t
operator|*
operator|(
name|ASIN_QS1
operator|+
name|t
operator|*
operator|(
name|ASIN_QS2
operator|+
name|t
operator|*
operator|(
name|ASIN_QS3
operator|+
name|t
operator|*
name|ASIN_QS4
operator|)
operator|)
operator|)
decl_stmt|;
name|double
name|s
init|=
name|Math
operator|.
name|sqrt
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|double
name|z
init|=
name|s
operator|+
name|s
operator|*
operator|(
name|p
operator|/
name|q
operator|)
decl_stmt|;
name|double
name|result
init|=
name|ASIN_PIO2_HI
operator|-
operator|(
operator|(
name|z
operator|+
name|z
operator|)
operator|-
name|ASIN_PIO2_LO
operator|)
decl_stmt|;
return|return
name|negateResult
condition|?
operator|-
name|result
else|:
name|result
return|;
block|}
else|else
block|{
comment|// value>= 1.0, or value is NaN
if|if
condition|(
name|a
operator|==
literal|1.0
condition|)
block|{
return|return
name|negateResult
condition|?
operator|-
name|Math
operator|.
name|PI
operator|/
literal|2
else|:
name|Math
operator|.
name|PI
operator|/
literal|2
return|;
block|}
else|else
block|{
return|return
name|Double
operator|.
name|NaN
return|;
block|}
block|}
block|}
block|}
comment|// haversin
comment|// TODO: remove these for java 9, they fixed Math.toDegrees()/toRadians() to work just like this.
DECL|field|TO_RADIANS
specifier|public
specifier|static
specifier|final
name|double
name|TO_RADIANS
init|=
name|Math
operator|.
name|PI
operator|/
literal|180D
decl_stmt|;
DECL|field|TO_DEGREES
specifier|public
specifier|static
specifier|final
name|double
name|TO_DEGREES
init|=
literal|180D
operator|/
name|Math
operator|.
name|PI
decl_stmt|;
comment|// Earth's mean radius, in meters and kilometers; see http://earth-info.nga.mil/GandG/publications/tr8350.2/wgs84fin.pdf
DECL|field|TO_METERS
specifier|private
specifier|static
specifier|final
name|double
name|TO_METERS
init|=
literal|6_371_008.7714D
decl_stmt|;
comment|// equatorial radius
DECL|field|TO_KILOMETERS
specifier|private
specifier|static
specifier|final
name|double
name|TO_KILOMETERS
init|=
literal|6_371.0087714D
decl_stmt|;
comment|// equatorial radius
comment|// cos/asin
DECL|field|ONE_DIV_F2
specifier|private
specifier|static
specifier|final
name|double
name|ONE_DIV_F2
init|=
literal|1
operator|/
literal|2.0
decl_stmt|;
DECL|field|ONE_DIV_F3
specifier|private
specifier|static
specifier|final
name|double
name|ONE_DIV_F3
init|=
literal|1
operator|/
literal|6.0
decl_stmt|;
DECL|field|ONE_DIV_F4
specifier|private
specifier|static
specifier|final
name|double
name|ONE_DIV_F4
init|=
literal|1
operator|/
literal|24.0
decl_stmt|;
DECL|field|PIO2_HI
specifier|private
specifier|static
specifier|final
name|double
name|PIO2_HI
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0x3FF921FB54400000L
argument_list|)
decl_stmt|;
comment|// 1.57079632673412561417e+00 first 33 bits of pi/2
DECL|field|PIO2_LO
specifier|private
specifier|static
specifier|final
name|double
name|PIO2_LO
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0x3DD0B4611A626331L
argument_list|)
decl_stmt|;
comment|// 6.07710050650619224932e-11 pi/2 - PIO2_HI
DECL|field|TWOPI_HI
specifier|private
specifier|static
specifier|final
name|double
name|TWOPI_HI
init|=
literal|4
operator|*
name|PIO2_HI
decl_stmt|;
DECL|field|TWOPI_LO
specifier|private
specifier|static
specifier|final
name|double
name|TWOPI_LO
init|=
literal|4
operator|*
name|PIO2_LO
decl_stmt|;
DECL|field|SIN_COS_TABS_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SIN_COS_TABS_SIZE
init|=
operator|(
literal|1
operator|<<
literal|11
operator|)
operator|+
literal|1
decl_stmt|;
DECL|field|SIN_COS_DELTA_HI
specifier|private
specifier|static
specifier|final
name|double
name|SIN_COS_DELTA_HI
init|=
name|TWOPI_HI
operator|/
operator|(
name|SIN_COS_TABS_SIZE
operator|-
literal|1
operator|)
decl_stmt|;
DECL|field|SIN_COS_DELTA_LO
specifier|private
specifier|static
specifier|final
name|double
name|SIN_COS_DELTA_LO
init|=
name|TWOPI_LO
operator|/
operator|(
name|SIN_COS_TABS_SIZE
operator|-
literal|1
operator|)
decl_stmt|;
DECL|field|SIN_COS_INDEXER
specifier|private
specifier|static
specifier|final
name|double
name|SIN_COS_INDEXER
init|=
literal|1
operator|/
operator|(
name|SIN_COS_DELTA_HI
operator|+
name|SIN_COS_DELTA_LO
operator|)
decl_stmt|;
DECL|field|sinTab
specifier|private
specifier|static
specifier|final
name|double
index|[]
name|sinTab
init|=
operator|new
name|double
index|[
name|SIN_COS_TABS_SIZE
index|]
decl_stmt|;
DECL|field|cosTab
specifier|private
specifier|static
specifier|final
name|double
index|[]
name|cosTab
init|=
operator|new
name|double
index|[
name|SIN_COS_TABS_SIZE
index|]
decl_stmt|;
comment|// Max abs value for fast modulo, above which we use regular angle normalization.
comment|// This value must be< (Integer.MAX_VALUE / SIN_COS_INDEXER), to stay in range of int type.
comment|// The higher it is, the higher the error, but also the faster it is for lower values.
comment|// If you set it to ((Integer.MAX_VALUE / SIN_COS_INDEXER) * 0.99), worse accuracy on double range is about 1e-10.
DECL|field|SIN_COS_MAX_VALUE_FOR_INT_MODULO
specifier|static
specifier|final
name|double
name|SIN_COS_MAX_VALUE_FOR_INT_MODULO
init|=
operator|(
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|>>
literal|9
operator|)
operator|/
name|SIN_COS_INDEXER
operator|)
operator|*
literal|0.99
decl_stmt|;
comment|// Supposed to be>= sin(77.2deg), as fdlibm code is supposed to work with values> 0.975,
comment|// but seems to work well enough as long as value>= sin(25deg).
DECL|field|ASIN_MAX_VALUE_FOR_TABS
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_MAX_VALUE_FOR_TABS
init|=
name|StrictMath
operator|.
name|sin
argument_list|(
name|Math
operator|.
name|toRadians
argument_list|(
literal|73.0
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|ASIN_TABS_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|ASIN_TABS_SIZE
init|=
operator|(
literal|1
operator|<<
literal|13
operator|)
operator|+
literal|1
decl_stmt|;
DECL|field|ASIN_DELTA
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_DELTA
init|=
name|ASIN_MAX_VALUE_FOR_TABS
operator|/
operator|(
name|ASIN_TABS_SIZE
operator|-
literal|1
operator|)
decl_stmt|;
DECL|field|ASIN_INDEXER
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_INDEXER
init|=
literal|1
operator|/
name|ASIN_DELTA
decl_stmt|;
DECL|field|asinTab
specifier|private
specifier|static
specifier|final
name|double
index|[]
name|asinTab
init|=
operator|new
name|double
index|[
name|ASIN_TABS_SIZE
index|]
decl_stmt|;
DECL|field|asinDer1DivF1Tab
specifier|private
specifier|static
specifier|final
name|double
index|[]
name|asinDer1DivF1Tab
init|=
operator|new
name|double
index|[
name|ASIN_TABS_SIZE
index|]
decl_stmt|;
DECL|field|asinDer2DivF2Tab
specifier|private
specifier|static
specifier|final
name|double
index|[]
name|asinDer2DivF2Tab
init|=
operator|new
name|double
index|[
name|ASIN_TABS_SIZE
index|]
decl_stmt|;
DECL|field|asinDer3DivF3Tab
specifier|private
specifier|static
specifier|final
name|double
index|[]
name|asinDer3DivF3Tab
init|=
operator|new
name|double
index|[
name|ASIN_TABS_SIZE
index|]
decl_stmt|;
DECL|field|asinDer4DivF4Tab
specifier|private
specifier|static
specifier|final
name|double
index|[]
name|asinDer4DivF4Tab
init|=
operator|new
name|double
index|[
name|ASIN_TABS_SIZE
index|]
decl_stmt|;
DECL|field|ASIN_PIO2_HI
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_PIO2_HI
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0x3FF921FB54442D18L
argument_list|)
decl_stmt|;
comment|// 1.57079632679489655800e+00
DECL|field|ASIN_PIO2_LO
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_PIO2_LO
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0x3C91A62633145C07L
argument_list|)
decl_stmt|;
comment|// 6.12323399573676603587e-17
DECL|field|ASIN_PS0
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_PS0
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0x3fc5555555555555L
argument_list|)
decl_stmt|;
comment|//  1.66666666666666657415e-01
DECL|field|ASIN_PS1
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_PS1
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0xbfd4d61203eb6f7dL
argument_list|)
decl_stmt|;
comment|// -3.25565818622400915405e-01
DECL|field|ASIN_PS2
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_PS2
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0x3fc9c1550e884455L
argument_list|)
decl_stmt|;
comment|//  2.01212532134862925881e-01
DECL|field|ASIN_PS3
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_PS3
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0xbfa48228b5688f3bL
argument_list|)
decl_stmt|;
comment|// -4.00555345006794114027e-02
DECL|field|ASIN_PS4
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_PS4
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0x3f49efe07501b288L
argument_list|)
decl_stmt|;
comment|//  7.91534994289814532176e-04
DECL|field|ASIN_PS5
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_PS5
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0x3f023de10dfdf709L
argument_list|)
decl_stmt|;
comment|//  3.47933107596021167570e-05
DECL|field|ASIN_QS1
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_QS1
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0xc0033a271c8a2d4bL
argument_list|)
decl_stmt|;
comment|// -2.40339491173441421878e+00
DECL|field|ASIN_QS2
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_QS2
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0x40002ae59c598ac8L
argument_list|)
decl_stmt|;
comment|//  2.02094576023350569471e+00
DECL|field|ASIN_QS3
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_QS3
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0xbfe6066c1b8d0159L
argument_list|)
decl_stmt|;
comment|// -6.88283971605453293030e-01
DECL|field|ASIN_QS4
specifier|private
specifier|static
specifier|final
name|double
name|ASIN_QS4
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
literal|0x3fb3b8c5b12e9282L
argument_list|)
decl_stmt|;
comment|//  7.70381505559019352791e-02
comment|/** Initializes look-up tables. */
static|static
block|{
comment|// sin and cos
specifier|final
name|int
name|SIN_COS_PI_INDEX
init|=
operator|(
name|SIN_COS_TABS_SIZE
operator|-
literal|1
operator|)
operator|/
literal|2
decl_stmt|;
specifier|final
name|int
name|SIN_COS_PI_MUL_2_INDEX
init|=
literal|2
operator|*
name|SIN_COS_PI_INDEX
decl_stmt|;
specifier|final
name|int
name|SIN_COS_PI_MUL_0_5_INDEX
init|=
name|SIN_COS_PI_INDEX
operator|/
literal|2
decl_stmt|;
specifier|final
name|int
name|SIN_COS_PI_MUL_1_5_INDEX
init|=
literal|3
operator|*
name|SIN_COS_PI_INDEX
operator|/
literal|2
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
name|SIN_COS_TABS_SIZE
condition|;
name|i
operator|++
control|)
block|{
comment|// angle: in [0,2*PI].
name|double
name|angle
init|=
name|i
operator|*
name|SIN_COS_DELTA_HI
operator|+
name|i
operator|*
name|SIN_COS_DELTA_LO
decl_stmt|;
name|double
name|sinAngle
init|=
name|StrictMath
operator|.
name|sin
argument_list|(
name|angle
argument_list|)
decl_stmt|;
name|double
name|cosAngle
init|=
name|StrictMath
operator|.
name|cos
argument_list|(
name|angle
argument_list|)
decl_stmt|;
comment|// For indexes corresponding to null cosine or sine, we make sure the value is zero
comment|// and not an epsilon. This allows for a much better accuracy for results close to zero.
if|if
condition|(
name|i
operator|==
name|SIN_COS_PI_INDEX
condition|)
block|{
name|sinAngle
operator|=
literal|0.0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
name|SIN_COS_PI_MUL_2_INDEX
condition|)
block|{
name|sinAngle
operator|=
literal|0.0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
name|SIN_COS_PI_MUL_0_5_INDEX
condition|)
block|{
name|cosAngle
operator|=
literal|0.0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
name|SIN_COS_PI_MUL_1_5_INDEX
condition|)
block|{
name|cosAngle
operator|=
literal|0.0
expr_stmt|;
block|}
name|sinTab
index|[
name|i
index|]
operator|=
name|sinAngle
expr_stmt|;
name|cosTab
index|[
name|i
index|]
operator|=
name|cosAngle
expr_stmt|;
block|}
comment|// asin
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ASIN_TABS_SIZE
condition|;
name|i
operator|++
control|)
block|{
comment|// x: in [0,ASIN_MAX_VALUE_FOR_TABS].
name|double
name|x
init|=
name|i
operator|*
name|ASIN_DELTA
decl_stmt|;
name|asinTab
index|[
name|i
index|]
operator|=
name|StrictMath
operator|.
name|asin
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|double
name|oneMinusXSqInv
init|=
literal|1.0
operator|/
operator|(
literal|1
operator|-
name|x
operator|*
name|x
operator|)
decl_stmt|;
name|double
name|oneMinusXSqInv0_5
init|=
name|StrictMath
operator|.
name|sqrt
argument_list|(
name|oneMinusXSqInv
argument_list|)
decl_stmt|;
name|double
name|oneMinusXSqInv1_5
init|=
name|oneMinusXSqInv0_5
operator|*
name|oneMinusXSqInv
decl_stmt|;
name|double
name|oneMinusXSqInv2_5
init|=
name|oneMinusXSqInv1_5
operator|*
name|oneMinusXSqInv
decl_stmt|;
name|double
name|oneMinusXSqInv3_5
init|=
name|oneMinusXSqInv2_5
operator|*
name|oneMinusXSqInv
decl_stmt|;
name|asinDer1DivF1Tab
index|[
name|i
index|]
operator|=
name|oneMinusXSqInv0_5
expr_stmt|;
name|asinDer2DivF2Tab
index|[
name|i
index|]
operator|=
operator|(
name|x
operator|*
name|oneMinusXSqInv1_5
operator|)
operator|*
name|ONE_DIV_F2
expr_stmt|;
name|asinDer3DivF3Tab
index|[
name|i
index|]
operator|=
operator|(
operator|(
literal|1
operator|+
literal|2
operator|*
name|x
operator|*
name|x
operator|)
operator|*
name|oneMinusXSqInv2_5
operator|)
operator|*
name|ONE_DIV_F3
expr_stmt|;
name|asinDer4DivF4Tab
index|[
name|i
index|]
operator|=
operator|(
operator|(
literal|5
operator|+
literal|2
operator|*
name|x
operator|*
operator|(
literal|2
operator|+
name|x
operator|*
operator|(
literal|5
operator|-
literal|2
operator|*
name|x
operator|)
operator|)
operator|)
operator|*
name|oneMinusXSqInv3_5
operator|)
operator|*
name|ONE_DIV_F4
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
