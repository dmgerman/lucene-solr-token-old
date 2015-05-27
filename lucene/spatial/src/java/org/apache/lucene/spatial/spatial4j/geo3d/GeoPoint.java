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
comment|/**  * This class represents a point on the surface of a unit sphere.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoPoint
specifier|public
class|class
name|GeoPoint
extends|extends
name|Vector
block|{
DECL|field|magnitude
specifier|protected
name|double
name|magnitude
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|sinLat
parameter_list|,
specifier|final
name|double
name|sinLon
parameter_list|,
specifier|final
name|double
name|cosLat
parameter_list|,
specifier|final
name|double
name|cosLon
parameter_list|)
block|{
name|this
argument_list|(
name|computeMagnitude
argument_list|(
name|planetModel
argument_list|,
name|cosLat
operator|*
name|cosLon
argument_list|,
name|cosLat
operator|*
name|sinLon
argument_list|,
name|sinLat
argument_list|)
argument_list|,
name|cosLat
operator|*
name|cosLon
argument_list|,
name|cosLat
operator|*
name|sinLon
argument_list|,
name|sinLat
argument_list|)
expr_stmt|;
block|}
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|double
name|lon
parameter_list|)
block|{
name|this
argument_list|(
name|planetModel
argument_list|,
name|Math
operator|.
name|sin
argument_list|(
name|lat
argument_list|)
argument_list|,
name|Math
operator|.
name|sin
argument_list|(
name|lon
argument_list|)
argument_list|,
name|Math
operator|.
name|cos
argument_list|(
name|lat
argument_list|)
argument_list|,
name|Math
operator|.
name|cos
argument_list|(
name|lon
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|(
specifier|final
name|double
name|magnitude
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
name|x
operator|*
name|magnitude
argument_list|,
name|y
operator|*
name|magnitude
argument_list|,
name|z
operator|*
name|magnitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|magnitude
operator|=
name|magnitude
expr_stmt|;
block|}
DECL|method|GeoPoint
specifier|public
name|GeoPoint
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
name|super
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
expr_stmt|;
block|}
DECL|method|arcDistance
specifier|public
name|double
name|arcDistance
parameter_list|(
specifier|final
name|GeoPoint
name|v
parameter_list|)
block|{
return|return
name|Tools
operator|.
name|safeAcos
argument_list|(
name|dotProduct
argument_list|(
name|v
argument_list|)
operator|/
operator|(
name|magnitude
argument_list|()
operator|*
name|v
operator|.
name|magnitude
argument_list|()
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|magnitude
specifier|public
name|double
name|magnitude
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|magnitude
operator|==
name|Double
operator|.
name|NEGATIVE_INFINITY
condition|)
block|{
name|this
operator|.
name|magnitude
operator|=
name|super
operator|.
name|magnitude
argument_list|()
expr_stmt|;
block|}
return|return
name|magnitude
return|;
block|}
block|}
end_class
end_unit
