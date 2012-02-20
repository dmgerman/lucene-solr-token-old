begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.base.distance
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|distance
package|;
end_package
begin_comment
comment|/**  * Enum representing difference distance units, currently only kilometers and  * miles  */
end_comment
begin_enum
DECL|enum|DistanceUnits
specifier|public
enum|enum
name|DistanceUnits
block|{
comment|//TODO do we need circumference?
DECL|enum constant|KILOMETERS
name|KILOMETERS
argument_list|(
literal|"km"
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
argument_list|,
literal|40076
argument_list|)
block|,
DECL|enum constant|MILES
name|MILES
argument_list|(
literal|"miles"
argument_list|,
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_MI
argument_list|,
literal|24902
argument_list|)
block|,
DECL|enum constant|RADIANS
name|RADIANS
argument_list|(
literal|"radians"
argument_list|,
literal|1
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|2
argument_list|)
block|,
comment|//experimental
DECL|enum constant|CARTESIAN
name|CARTESIAN
argument_list|(
literal|"u"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
block|;
DECL|field|units
specifier|private
specifier|final
name|String
name|units
decl_stmt|;
DECL|field|earthCircumference
specifier|private
specifier|final
name|double
name|earthCircumference
decl_stmt|;
DECL|field|earthRadius
specifier|private
specifier|final
name|double
name|earthRadius
decl_stmt|;
comment|/**    * Creates a new DistanceUnit that represents the given unit    *    * @param units Distance unit in String form    * @param earthRadius Radius of the Earth in the specific distance unit    * @param earthCircumfence Circumference of the Earth in the specific distance unit    */
DECL|method|DistanceUnits
name|DistanceUnits
parameter_list|(
name|String
name|units
parameter_list|,
name|double
name|earthRadius
parameter_list|,
name|double
name|earthCircumfence
parameter_list|)
block|{
name|this
operator|.
name|units
operator|=
name|units
expr_stmt|;
name|this
operator|.
name|earthCircumference
operator|=
name|earthCircumfence
expr_stmt|;
name|this
operator|.
name|earthRadius
operator|=
name|earthRadius
expr_stmt|;
block|}
comment|/**    * Returns the DistanceUnit which represents the given unit    *    * @param unit Unit whose DistanceUnit should be found    * @return DistanceUnit representing the unit    * @throws IllegalArgumentException if no DistanceUnit which represents the given unit is found    */
DECL|method|findDistanceUnit
specifier|public
specifier|static
name|DistanceUnits
name|findDistanceUnit
parameter_list|(
name|String
name|unit
parameter_list|)
block|{
if|if
condition|(
name|MILES
operator|.
name|getUnits
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|unit
argument_list|)
operator|||
name|unit
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"mi"
argument_list|)
condition|)
block|{
return|return
name|MILES
return|;
block|}
if|if
condition|(
name|KILOMETERS
operator|.
name|getUnits
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|unit
argument_list|)
condition|)
block|{
return|return
name|KILOMETERS
return|;
block|}
if|if
condition|(
name|CARTESIAN
operator|.
name|getUnits
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|unit
argument_list|)
operator|||
name|unit
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|CARTESIAN
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown distance unit "
operator|+
name|unit
argument_list|)
throw|;
block|}
comment|/**    * Converts the given distance in given DistanceUnit, to a distance in the unit represented by {@code this}    *    * @param distance Distance to convert    * @param from Unit to convert the distance from    * @return Given distance converted to the distance in the given unit    */
DECL|method|convert
specifier|public
name|double
name|convert
parameter_list|(
name|double
name|distance
parameter_list|,
name|DistanceUnits
name|from
parameter_list|)
block|{
if|if
condition|(
name|from
operator|==
name|this
condition|)
block|{
return|return
name|distance
return|;
block|}
if|if
condition|(
name|this
operator|==
name|CARTESIAN
operator|||
name|from
operator|==
name|CARTESIAN
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't convert cartesian distances: "
operator|+
name|from
operator|+
literal|" -> "
operator|+
name|this
argument_list|)
throw|;
block|}
return|return
operator|(
name|this
operator|==
name|MILES
operator|)
condition|?
name|distance
operator|*
name|DistanceUtils
operator|.
name|KM_TO_MILES
else|:
name|distance
operator|*
name|DistanceUtils
operator|.
name|MILES_TO_KM
return|;
block|}
comment|/**    * Returns the string representation of the distance unit    *    * @return String representation of the distance unit    */
DECL|method|getUnits
specifier|public
name|String
name|getUnits
parameter_list|()
block|{
return|return
name|units
return|;
block|}
comment|/**    * Returns the<a href="http://en.wikipedia.org/wiki/Earth_radius">average earth radius</a>    *    * @return the average earth radius    */
DECL|method|earthRadius
specifier|public
name|double
name|earthRadius
parameter_list|()
block|{
return|return
name|earthRadius
return|;
block|}
comment|/**    * Returns the<a href="http://www.lyberty.com/encyc/articles/earth.html">circumference of the Earth</a>    *    * @return  the circumference of the Earth    */
DECL|method|earthCircumference
specifier|public
name|double
name|earthCircumference
parameter_list|()
block|{
return|return
name|earthCircumference
return|;
block|}
DECL|method|isGeo
specifier|public
name|boolean
name|isGeo
parameter_list|()
block|{
return|return
name|earthRadius
operator|>
literal|0
return|;
block|}
block|}
end_enum
end_unit
