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
comment|/**  * Distance shapes have capabilities of both geohashing and distance  * computation (which also includes point membership determination).  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoBaseDistanceShape
specifier|public
specifier|abstract
class|class
name|GeoBaseDistanceShape
extends|extends
name|GeoBaseMembershipShape
implements|implements
name|GeoDistanceShape
block|{
comment|/** Constructor.    *@param planetModel is the planet model to use.    */
DECL|method|GeoBaseDistanceShape
specifier|public
name|GeoBaseDistanceShape
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
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
return|return
name|isWithin
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
return|;
block|}
annotation|@
name|Override
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
parameter_list|,
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|computeDistance
argument_list|(
name|distanceStyle
argument_list|,
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
return|;
block|}
annotation|@
name|Override
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
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
if|if
condition|(
operator|!
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
block|{
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
return|return
name|distance
argument_list|(
name|distanceStyle
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
return|;
block|}
comment|/** Called by a {@code computeDistance} method if X/Y/Z is not within this shape. */
DECL|method|distance
specifier|protected
specifier|abstract
name|double
name|distance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
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
function_decl|;
annotation|@
name|Override
DECL|method|getDistanceBounds
specifier|public
name|void
name|getDistanceBounds
parameter_list|(
specifier|final
name|Bounds
name|bounds
parameter_list|,
specifier|final
name|DistanceStyle
name|distanceStyle
parameter_list|,
specifier|final
name|double
name|distanceValue
parameter_list|)
block|{
if|if
condition|(
name|distanceValue
operator|==
name|Double
operator|.
name|POSITIVE_INFINITY
condition|)
block|{
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
return|return;
block|}
name|distanceBounds
argument_list|(
name|bounds
argument_list|,
name|distanceStyle
argument_list|,
name|distanceValue
argument_list|)
expr_stmt|;
block|}
comment|/** Called by a {@code getDistanceBounds} method if distanceValue is not Double.POSITIVE_INFINITY. */
DECL|method|distanceBounds
specifier|protected
specifier|abstract
name|void
name|distanceBounds
parameter_list|(
specifier|final
name|Bounds
name|bounds
parameter_list|,
specifier|final
name|DistanceStyle
name|distanceStyle
parameter_list|,
specifier|final
name|double
name|distanceValue
parameter_list|)
function_decl|;
block|}
end_class
end_unit
