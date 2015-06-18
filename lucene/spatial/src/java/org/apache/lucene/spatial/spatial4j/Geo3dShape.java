begin_unit
begin_package
DECL|package|org.apache.lucene.spatial.spatial4j
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
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|distance
operator|.
name|DistanceUtils
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|SpatialRelation
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|impl
operator|.
name|RectangleImpl
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
name|spatial4j
operator|.
name|geo3d
operator|.
name|Bounds
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
name|spatial4j
operator|.
name|geo3d
operator|.
name|GeoArea
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
name|spatial4j
operator|.
name|geo3d
operator|.
name|GeoAreaFactory
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
name|spatial4j
operator|.
name|geo3d
operator|.
name|GeoPoint
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
name|spatial4j
operator|.
name|geo3d
operator|.
name|GeoShape
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
name|spatial4j
operator|.
name|geo3d
operator|.
name|PlanetModel
import|;
end_import
begin_comment
comment|/**  * A Spatial4j Shape wrapping a {@link GeoShape} ("Geo3D") -- a 3D planar geometry based Spatial4j Shape implementation.  * Geo3D implements shapes on the surface of a sphere or ellipsoid.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Geo3dShape
specifier|public
class|class
name|Geo3dShape
implements|implements
name|Shape
block|{
comment|/** The required size of this adjustment depends on the actual planetary model chosen.    * This value is big enough to account for WGS84. */
DECL|field|ROUNDOFF_ADJUSTMENT
specifier|protected
specifier|static
specifier|final
name|double
name|ROUNDOFF_ADJUSTMENT
init|=
literal|0.05
decl_stmt|;
DECL|field|ctx
specifier|public
specifier|final
name|SpatialContext
name|ctx
decl_stmt|;
DECL|field|shape
specifier|public
specifier|final
name|GeoShape
name|shape
decl_stmt|;
DECL|field|planetModel
specifier|public
specifier|final
name|PlanetModel
name|planetModel
decl_stmt|;
DECL|field|boundingBox
specifier|private
specifier|volatile
name|Rectangle
name|boundingBox
init|=
literal|null
decl_stmt|;
comment|// lazy initialized
DECL|method|Geo3dShape
specifier|public
name|Geo3dShape
parameter_list|(
specifier|final
name|GeoShape
name|shape
parameter_list|,
specifier|final
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|this
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
name|shape
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
DECL|method|Geo3dShape
specifier|public
name|Geo3dShape
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|GeoShape
name|shape
parameter_list|,
specifier|final
name|SpatialContext
name|ctx
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ctx
operator|.
name|isGeo
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"SpatialContext.isGeo() must be true"
argument_list|)
throw|;
block|}
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|planetModel
operator|=
name|planetModel
expr_stmt|;
name|this
operator|.
name|shape
operator|=
name|shape
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|relate
specifier|public
name|SpatialRelation
name|relate
parameter_list|(
name|Shape
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|Rectangle
condition|)
return|return
name|relate
argument_list|(
operator|(
name|Rectangle
operator|)
name|other
argument_list|)
return|;
elseif|else
if|if
condition|(
name|other
operator|instanceof
name|Point
condition|)
return|return
name|relate
argument_list|(
operator|(
name|Point
operator|)
name|other
argument_list|)
return|;
else|else
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unimplemented shape relationship determination: "
operator|+
name|other
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|relate
specifier|protected
name|SpatialRelation
name|relate
parameter_list|(
name|Rectangle
name|r
parameter_list|)
block|{
comment|// Construct the right kind of GeoArea first
name|GeoArea
name|geoArea
init|=
name|GeoAreaFactory
operator|.
name|makeGeoArea
argument_list|(
name|planetModel
argument_list|,
name|r
operator|.
name|getMaxY
argument_list|()
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
argument_list|,
name|r
operator|.
name|getMinY
argument_list|()
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
argument_list|,
name|r
operator|.
name|getMinX
argument_list|()
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
argument_list|,
name|r
operator|.
name|getMaxX
argument_list|()
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
argument_list|)
decl_stmt|;
name|int
name|relationship
init|=
name|geoArea
operator|.
name|getRelationship
argument_list|(
name|shape
argument_list|)
decl_stmt|;
if|if
condition|(
name|relationship
operator|==
name|GeoArea
operator|.
name|WITHIN
condition|)
return|return
name|SpatialRelation
operator|.
name|WITHIN
return|;
elseif|else
if|if
condition|(
name|relationship
operator|==
name|GeoArea
operator|.
name|CONTAINS
condition|)
return|return
name|SpatialRelation
operator|.
name|CONTAINS
return|;
elseif|else
if|if
condition|(
name|relationship
operator|==
name|GeoArea
operator|.
name|OVERLAPS
condition|)
return|return
name|SpatialRelation
operator|.
name|INTERSECTS
return|;
elseif|else
if|if
condition|(
name|relationship
operator|==
name|GeoArea
operator|.
name|DISJOINT
condition|)
return|return
name|SpatialRelation
operator|.
name|DISJOINT
return|;
else|else
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown relationship returned: "
operator|+
name|relationship
argument_list|)
throw|;
block|}
DECL|method|relate
specifier|protected
name|SpatialRelation
name|relate
parameter_list|(
name|Point
name|p
parameter_list|)
block|{
comment|// Create a GeoPoint
name|GeoPoint
name|point
init|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|p
operator|.
name|getY
argument_list|()
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
argument_list|,
name|p
operator|.
name|getX
argument_list|()
operator|*
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
argument_list|)
decl_stmt|;
if|if
condition|(
name|shape
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
condition|)
block|{
comment|// Point within shape
return|return
name|SpatialRelation
operator|.
name|CONTAINS
return|;
block|}
return|return
name|SpatialRelation
operator|.
name|DISJOINT
return|;
block|}
annotation|@
name|Override
DECL|method|getBoundingBox
specifier|public
name|Rectangle
name|getBoundingBox
parameter_list|()
block|{
name|Rectangle
name|bbox
init|=
name|this
operator|.
name|boundingBox
decl_stmt|;
comment|//volatile read once
if|if
condition|(
name|bbox
operator|==
literal|null
condition|)
block|{
name|Bounds
name|bounds
init|=
name|shape
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|double
name|leftLon
decl_stmt|;
name|double
name|rightLon
decl_stmt|;
if|if
condition|(
name|bounds
operator|.
name|checkNoLongitudeBound
argument_list|()
condition|)
block|{
name|leftLon
operator|=
operator|-
literal|180.0
expr_stmt|;
name|rightLon
operator|=
literal|180.0
expr_stmt|;
block|}
else|else
block|{
name|leftLon
operator|=
name|bounds
operator|.
name|getLeftLongitude
argument_list|()
operator|.
name|doubleValue
argument_list|()
operator|*
name|DistanceUtils
operator|.
name|RADIANS_TO_DEGREES
expr_stmt|;
name|rightLon
operator|=
name|bounds
operator|.
name|getRightLongitude
argument_list|()
operator|.
name|doubleValue
argument_list|()
operator|*
name|DistanceUtils
operator|.
name|RADIANS_TO_DEGREES
expr_stmt|;
block|}
name|double
name|minLat
decl_stmt|;
if|if
condition|(
name|bounds
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
condition|)
block|{
name|minLat
operator|=
operator|-
literal|90.0
expr_stmt|;
block|}
else|else
block|{
name|minLat
operator|=
name|bounds
operator|.
name|getMinLatitude
argument_list|()
operator|.
name|doubleValue
argument_list|()
operator|*
name|DistanceUtils
operator|.
name|RADIANS_TO_DEGREES
expr_stmt|;
block|}
name|double
name|maxLat
decl_stmt|;
if|if
condition|(
name|bounds
operator|.
name|checkNoTopLatitudeBound
argument_list|()
condition|)
block|{
name|maxLat
operator|=
literal|90.0
expr_stmt|;
block|}
else|else
block|{
name|maxLat
operator|=
name|bounds
operator|.
name|getMaxLatitude
argument_list|()
operator|.
name|doubleValue
argument_list|()
operator|*
name|DistanceUtils
operator|.
name|RADIANS_TO_DEGREES
expr_stmt|;
block|}
name|bbox
operator|=
operator|new
name|RectangleImpl
argument_list|(
name|leftLon
argument_list|,
name|rightLon
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|ctx
argument_list|)
operator|.
name|getBuffered
argument_list|(
name|ROUNDOFF_ADJUSTMENT
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|boundingBox
operator|=
name|bbox
expr_stmt|;
block|}
return|return
name|bbox
return|;
block|}
annotation|@
name|Override
DECL|method|hasArea
specifier|public
name|boolean
name|hasArea
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getArea
specifier|public
name|double
name|getArea
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getCenter
specifier|public
name|Point
name|getCenter
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getBuffered
specifier|public
name|Shape
name|getBuffered
parameter_list|(
name|double
name|distance
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
literal|false
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
return|return
literal|"Geo3dShape{planetmodel="
operator|+
name|planetModel
operator|+
literal|", shape="
operator|+
name|shape
operator|+
literal|'}'
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
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|Geo3dShape
operator|)
condition|)
return|return
literal|false
return|;
name|Geo3dShape
name|tr
init|=
operator|(
name|Geo3dShape
operator|)
name|other
decl_stmt|;
return|return
name|tr
operator|.
name|ctx
operator|.
name|equals
argument_list|(
name|ctx
argument_list|)
operator|&&
name|tr
operator|.
name|planetModel
operator|.
name|equals
argument_list|(
name|planetModel
argument_list|)
operator|&&
name|tr
operator|.
name|shape
operator|.
name|equals
argument_list|(
name|shape
argument_list|)
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
name|planetModel
operator|.
name|hashCode
argument_list|()
operator|+
name|shape
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
