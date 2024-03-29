begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.geo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
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
name|util
operator|.
name|NumericUtils
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
name|geo
operator|.
name|GeoUtils
operator|.
name|MAX_LAT_INCL
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
name|geo
operator|.
name|GeoUtils
operator|.
name|MAX_LON_INCL
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
name|geo
operator|.
name|GeoUtils
operator|.
name|MIN_LON_INCL
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
name|geo
operator|.
name|GeoUtils
operator|.
name|MIN_LAT_INCL
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
name|geo
operator|.
name|GeoUtils
operator|.
name|checkLatitude
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
name|geo
operator|.
name|GeoUtils
operator|.
name|checkLongitude
import|;
end_import
begin_comment
comment|/**  * reusable geopoint encoding methods  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoEncodingUtils
specifier|public
specifier|final
class|class
name|GeoEncodingUtils
block|{
comment|/** number of bits used for quantizing latitude and longitude values */
DECL|field|BITS
specifier|public
specifier|static
specifier|final
name|short
name|BITS
init|=
literal|32
decl_stmt|;
DECL|field|LAT_SCALE
specifier|private
specifier|static
specifier|final
name|double
name|LAT_SCALE
init|=
operator|(
literal|0x1L
operator|<<
name|BITS
operator|)
operator|/
literal|180.0D
decl_stmt|;
DECL|field|LAT_DECODE
specifier|private
specifier|static
specifier|final
name|double
name|LAT_DECODE
init|=
literal|1
operator|/
name|LAT_SCALE
decl_stmt|;
DECL|field|LON_SCALE
specifier|private
specifier|static
specifier|final
name|double
name|LON_SCALE
init|=
operator|(
literal|0x1L
operator|<<
name|BITS
operator|)
operator|/
literal|360.0D
decl_stmt|;
DECL|field|LON_DECODE
specifier|private
specifier|static
specifier|final
name|double
name|LON_DECODE
init|=
literal|1
operator|/
name|LON_SCALE
decl_stmt|;
comment|// No instance:
DECL|method|GeoEncodingUtils
specifier|private
name|GeoEncodingUtils
parameter_list|()
block|{   }
comment|/**    * Quantizes double (64 bit) latitude into 32 bits (rounding down: in the direction of -90)    * @param latitude latitude value: must be within standard +/-90 coordinate bounds.    * @return encoded value as a 32-bit {@code int}    * @throws IllegalArgumentException if latitude is out of bounds    */
DECL|method|encodeLatitude
specifier|public
specifier|static
name|int
name|encodeLatitude
parameter_list|(
name|double
name|latitude
parameter_list|)
block|{
name|checkLatitude
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
comment|// the maximum possible value cannot be encoded without overflow
if|if
condition|(
name|latitude
operator|==
literal|90.0D
condition|)
block|{
name|latitude
operator|=
name|Math
operator|.
name|nextDown
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|latitude
operator|/
name|LAT_DECODE
argument_list|)
return|;
block|}
comment|/**    * Quantizes double (64 bit) latitude into 32 bits (rounding up: in the direction of +90)    * @param latitude latitude value: must be within standard +/-90 coordinate bounds.    * @return encoded value as a 32-bit {@code int}    * @throws IllegalArgumentException if latitude is out of bounds    */
DECL|method|encodeLatitudeCeil
specifier|public
specifier|static
name|int
name|encodeLatitudeCeil
parameter_list|(
name|double
name|latitude
parameter_list|)
block|{
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
comment|// the maximum possible value cannot be encoded without overflow
if|if
condition|(
name|latitude
operator|==
literal|90.0D
condition|)
block|{
name|latitude
operator|=
name|Math
operator|.
name|nextDown
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|latitude
operator|/
name|LAT_DECODE
argument_list|)
return|;
block|}
comment|/**    * Quantizes double (64 bit) longitude into 32 bits (rounding down: in the direction of -180)    * @param longitude longitude value: must be within standard +/-180 coordinate bounds.    * @return encoded value as a 32-bit {@code int}    * @throws IllegalArgumentException if longitude is out of bounds    */
DECL|method|encodeLongitude
specifier|public
specifier|static
name|int
name|encodeLongitude
parameter_list|(
name|double
name|longitude
parameter_list|)
block|{
name|checkLongitude
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
comment|// the maximum possible value cannot be encoded without overflow
if|if
condition|(
name|longitude
operator|==
literal|180.0D
condition|)
block|{
name|longitude
operator|=
name|Math
operator|.
name|nextDown
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|longitude
operator|/
name|LON_DECODE
argument_list|)
return|;
block|}
comment|/**    * Quantizes double (64 bit) longitude into 32 bits (rounding up: in the direction of +180)    * @param longitude longitude value: must be within standard +/-180 coordinate bounds.    * @return encoded value as a 32-bit {@code int}    * @throws IllegalArgumentException if longitude is out of bounds    */
DECL|method|encodeLongitudeCeil
specifier|public
specifier|static
name|int
name|encodeLongitudeCeil
parameter_list|(
name|double
name|longitude
parameter_list|)
block|{
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
comment|// the maximum possible value cannot be encoded without overflow
if|if
condition|(
name|longitude
operator|==
literal|180.0D
condition|)
block|{
name|longitude
operator|=
name|Math
operator|.
name|nextDown
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|longitude
operator|/
name|LON_DECODE
argument_list|)
return|;
block|}
comment|/**    * Turns quantized value from {@link #encodeLatitude} back into a double.    * @param encoded encoded value: 32-bit quantized value.    * @return decoded latitude value.    */
DECL|method|decodeLatitude
specifier|public
specifier|static
name|double
name|decodeLatitude
parameter_list|(
name|int
name|encoded
parameter_list|)
block|{
name|double
name|result
init|=
name|encoded
operator|*
name|LAT_DECODE
decl_stmt|;
assert|assert
name|result
operator|>=
name|MIN_LAT_INCL
operator|&&
name|result
operator|<
name|MAX_LAT_INCL
assert|;
return|return
name|result
return|;
block|}
comment|/**    * Turns quantized value from byte array back into a double.    * @param src byte array containing 4 bytes to decode at {@code offset}    * @param offset offset into {@code src} to decode from.    * @return decoded latitude value.    */
DECL|method|decodeLatitude
specifier|public
specifier|static
name|double
name|decodeLatitude
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|decodeLatitude
argument_list|(
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|src
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Turns quantized value from {@link #encodeLongitude} back into a double.    * @param encoded encoded value: 32-bit quantized value.    * @return decoded longitude value.    */
DECL|method|decodeLongitude
specifier|public
specifier|static
name|double
name|decodeLongitude
parameter_list|(
name|int
name|encoded
parameter_list|)
block|{
name|double
name|result
init|=
name|encoded
operator|*
name|LON_DECODE
decl_stmt|;
assert|assert
name|result
operator|>=
name|MIN_LON_INCL
operator|&&
name|result
operator|<
name|MAX_LON_INCL
assert|;
return|return
name|result
return|;
block|}
comment|/**    * Turns quantized value from byte array back into a double.    * @param src byte array containing 4 bytes to decode at {@code offset}    * @param offset offset into {@code src} to decode from.    * @return decoded longitude value.    */
DECL|method|decodeLongitude
specifier|public
specifier|static
name|double
name|decodeLongitude
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|decodeLongitude
argument_list|(
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|src
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
