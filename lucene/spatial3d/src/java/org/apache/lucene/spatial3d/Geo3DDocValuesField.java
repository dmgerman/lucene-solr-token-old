begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
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
name|index
operator|.
name|DocValuesType
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
name|index
operator|.
name|FieldInfo
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
name|search
operator|.
name|FieldDoc
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
name|search
operator|.
name|SortField
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
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
name|spatial3d
operator|.
name|geom
operator|.
name|PlanetModel
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoPoint
import|;
end_import
begin_comment
comment|/**   * An per-document 3D location field.  *<p>  * Sorting by distance is efficient. Multiple values for the same field in one document  * is allowed.   *<p>  * This field defines static factory methods for common operations:  *<ul>  *<li>TBD  *</ul>  *<p>  * If you also need query operations, you should add a separate {@link Geo3DPoint} instance.  *<p>  *<b>WARNING</b>: Values are indexed with some loss of precision from the  * original {@code double} values (4.190951585769653E-8 for the latitude component  * and 8.381903171539307E-8 for longitude).  * @see Geo3DPoint  */
end_comment
begin_class
DECL|class|Geo3DDocValuesField
specifier|public
class|class
name|Geo3DDocValuesField
extends|extends
name|Field
block|{
comment|// These are the multiplicative constants we need to use to arrive at values that fit in 21 bits.
comment|// The formula we use to go from double to encoded value is:  Math.floor((value - minimum) * factor + 0.5)
comment|// If we plug in maximum for value, we should get 0x1FFFFF.
comment|// So, 0x1FFFFF = Math.floor((maximum - minimum) * factor + 0.5)
comment|// We factor out the 0.5 and Math.floor by stating instead:
comment|// 0x200000 = (maximum - minimum) * factor
comment|// So, factor = 0x200000 / (maximum - minimum)
DECL|field|inverseMaximumValue
specifier|private
specifier|final
name|double
name|inverseMaximumValue
init|=
literal|1.0
operator|/
call|(
name|double
call|)
argument_list|(
literal|0x200000
argument_list|)
decl_stmt|;
DECL|field|inverseXFactor
specifier|private
specifier|final
name|double
name|inverseXFactor
init|=
operator|(
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumXValue
argument_list|()
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumXValue
argument_list|()
operator|)
operator|*
name|inverseMaximumValue
decl_stmt|;
DECL|field|inverseYFactor
specifier|private
specifier|final
name|double
name|inverseYFactor
init|=
operator|(
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumYValue
argument_list|()
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumYValue
argument_list|()
operator|)
operator|*
name|inverseMaximumValue
decl_stmt|;
DECL|field|inverseZFactor
specifier|private
specifier|final
name|double
name|inverseZFactor
init|=
operator|(
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumZValue
argument_list|()
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumZValue
argument_list|()
operator|)
operator|*
name|inverseMaximumValue
decl_stmt|;
DECL|field|xFactor
specifier|private
specifier|final
name|double
name|xFactor
init|=
literal|1.0
operator|/
name|inverseXFactor
decl_stmt|;
DECL|field|yFactor
specifier|private
specifier|final
name|double
name|yFactor
init|=
literal|1.0
operator|/
name|inverseYFactor
decl_stmt|;
DECL|field|zFactor
specifier|private
specifier|final
name|double
name|zFactor
init|=
literal|1.0
operator|/
name|inverseZFactor
decl_stmt|;
comment|/**    * Type for a Geo3DDocValuesField    *<p>    * Each value stores a 64-bit long where the three values (x, y, and z) are given    * 21 bits each.  Each 21-bit value represents the maximum extent in that dimension    * for the WGS84 planet model.    */
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**     * Creates a new Geo3DDocValuesField with the specified x, y, and z    * @param name field name    * @param point is the point.    * @throws IllegalArgumentException if the field name is null or the point is out of bounds    */
DECL|method|Geo3DDocValuesField
specifier|public
name|Geo3DDocValuesField
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|setLocationValue
argument_list|(
name|point
argument_list|)
expr_stmt|;
block|}
comment|/**     * Creates a new Geo3DDocValuesField with the specified x, y, and z    * @param name field name    * @param x is the x value for the point.    * @param y is the y value for the point.    * @param z is the z value for the point.    * @throws IllegalArgumentException if the field name is null or x, y, or z are out of bounds    */
DECL|method|Geo3DDocValuesField
specifier|public
name|Geo3DDocValuesField
parameter_list|(
specifier|final
name|String
name|name
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
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|setLocationValue
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
expr_stmt|;
block|}
comment|/**    * Change the values of this field    * @param point is the point.    * @throws IllegalArgumentException if the point is out of bounds    */
DECL|method|setLocationValue
specifier|public
name|void
name|setLocationValue
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
name|setLocationValue
argument_list|(
name|point
operator|.
name|x
argument_list|,
name|point
operator|.
name|y
argument_list|,
name|point
operator|.
name|z
argument_list|)
expr_stmt|;
block|}
comment|/**    * Change the values of this field    * @param x is the x value for the point.    * @param y is the y value for the point.    * @param z is the z value for the point.    * @throws IllegalArgumentException if x, y, or z are out of bounds    */
DECL|method|setLocationValue
specifier|public
name|void
name|setLocationValue
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
name|int
name|XEncoded
init|=
name|encodeX
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|int
name|YEncoded
init|=
name|encodeY
argument_list|(
name|y
argument_list|)
decl_stmt|;
name|int
name|ZEncoded
init|=
name|encodeZ
argument_list|(
name|z
argument_list|)
decl_stmt|;
name|fieldsData
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
name|XEncoded
operator|&
literal|0x1FFFFF
argument_list|)
operator|)
operator|<<
literal|42
operator|)
operator||
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
name|YEncoded
operator|&
literal|0x1FFFFF
argument_list|)
operator|)
operator|<<
literal|21
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|ZEncoded
operator|&
literal|0x1FFFFF
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
comment|// For encoding/decoding, we generally want the following behavior:
comment|// (1) If you encode the maximum value or the minimum value, the resulting int fits in 21 bits.
comment|// (2) If you decode an encoded value, you get back the original value for both the minimum and maximum planet model values.
comment|// (3) Rounding occurs such that a small delta from the minimum and maximum planet model values still returns the same
comment|// values -- that is, these are in the center of the range of input values that should return the minimum or maximum when decoded
DECL|method|encodeX
specifier|private
name|int
name|encodeX
parameter_list|(
specifier|final
name|double
name|x
parameter_list|)
block|{
if|if
condition|(
name|x
operator|>
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumXValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"x value exceeds WGS84 maximum"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|x
operator|<
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumXValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"x value less than WGS84 minimum"
argument_list|)
throw|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
operator|(
name|x
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumXValue
argument_list|()
operator|)
operator|*
name|xFactor
operator|+
literal|0.5
argument_list|)
return|;
block|}
DECL|method|decodeX
specifier|private
name|double
name|decodeX
parameter_list|(
specifier|final
name|int
name|x
parameter_list|)
block|{
return|return
name|x
operator|*
name|inverseXFactor
operator|+
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumXValue
argument_list|()
return|;
block|}
DECL|method|encodeY
specifier|private
name|int
name|encodeY
parameter_list|(
specifier|final
name|double
name|y
parameter_list|)
block|{
if|if
condition|(
name|y
operator|>
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumYValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"y value exceeds WGS84 maximum"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|y
operator|<
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumYValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"y value less than WGS84 minimum"
argument_list|)
throw|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
operator|(
name|y
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumYValue
argument_list|()
operator|)
operator|*
name|yFactor
operator|+
literal|0.5
argument_list|)
return|;
block|}
DECL|method|decodeY
specifier|private
name|double
name|decodeY
parameter_list|(
specifier|final
name|int
name|y
parameter_list|)
block|{
return|return
name|y
operator|*
name|inverseYFactor
operator|+
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumYValue
argument_list|()
return|;
block|}
DECL|method|encodeZ
specifier|private
name|int
name|encodeZ
parameter_list|(
specifier|final
name|double
name|z
parameter_list|)
block|{
if|if
condition|(
name|z
operator|>
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMaximumZValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"z value exceeds WGS84 maximum"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|z
operator|<
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumZValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"z value less than WGS84 minimum"
argument_list|)
throw|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|floor
argument_list|(
operator|(
name|z
operator|-
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumZValue
argument_list|()
operator|)
operator|*
name|zFactor
operator|+
literal|0.5
argument_list|)
return|;
block|}
DECL|method|decodeZ
specifier|private
name|double
name|decodeZ
parameter_list|(
specifier|final
name|int
name|z
parameter_list|)
block|{
return|return
name|z
operator|*
name|inverseZFactor
operator|+
name|PlanetModel
operator|.
name|WGS84
operator|.
name|getMinimumZValue
argument_list|()
return|;
block|}
comment|/** helper: checks a fieldinfo and throws exception if its definitely not a Geo3DDocValuesField */
DECL|method|checkCompatible
specifier|static
name|void
name|checkCompatible
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
comment|// dv properties could be "unset", if you e.g. used only StoredField with this same name in the segment.
if|if
condition|(
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|NONE
operator|&&
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|TYPE
operator|.
name|docValuesType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" was indexed with docValuesType="
operator|+
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|+
literal|" but this type has docValuesType="
operator|+
name|TYPE
operator|.
name|docValuesType
argument_list|()
operator|+
literal|", is the field really a Geo3DDocValuesField?"
argument_list|)
throw|;
block|}
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
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|long
name|currentValue
init|=
name|Long
operator|.
name|valueOf
argument_list|(
operator|(
name|Long
operator|)
name|fieldsData
argument_list|)
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|decodeX
argument_list|(
operator|(
call|(
name|int
call|)
argument_list|(
name|currentValue
operator|>>
literal|42
argument_list|)
operator|)
operator|&
literal|0x1FFFFF
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|decodeY
argument_list|(
operator|(
call|(
name|int
call|)
argument_list|(
name|currentValue
operator|>>
literal|21
argument_list|)
operator|)
operator|&
literal|0x1FFFFF
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|decodeZ
argument_list|(
operator|(
call|(
name|int
call|)
argument_list|(
name|currentValue
argument_list|)
operator|)
operator|&
literal|0x1FFFFF
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
