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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedContext
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Seed
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
name|GeoCircle
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
name|junit
operator|.
name|Rule
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import static
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|distance
operator|.
name|DistanceUtils
operator|.
name|DEGREES_TO_RADIANS
import|;
end_import
begin_class
DECL|class|Geo3dShapeTest
specifier|public
class|class
name|Geo3dShapeTest
extends|extends
name|RandomizedShapeTest
block|{
annotation|@
name|Rule
DECL|field|testLog
specifier|public
specifier|final
name|TestLog
name|testLog
init|=
name|TestLog
operator|.
name|instance
decl_stmt|;
DECL|method|random
specifier|static
name|Random
name|random
parameter_list|()
block|{
return|return
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getRandom
argument_list|()
return|;
block|}
block|{
name|ctx
operator|=
name|SpatialContext
operator|.
name|GEO
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Seed
argument_list|(
literal|"FAD1BAB12B6DCCFE"
argument_list|)
DECL|method|testGeoCircleRect
specifier|public
name|void
name|testGeoCircleRect
parameter_list|()
block|{
operator|new
name|RectIntersectionTestHelper
argument_list|<
name|Geo3dShape
argument_list|>
argument_list|(
name|ctx
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Geo3dShape
name|generateRandomShape
parameter_list|(
name|Point
name|nearP
parameter_list|)
block|{
comment|// Circles
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|circleRadius
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|180
argument_list|)
decl_stmt|;
specifier|final
name|Point
name|point
init|=
name|nearP
decl_stmt|;
try|try
block|{
specifier|final
name|GeoShape
name|shape
init|=
operator|new
name|GeoCircle
argument_list|(
name|point
operator|.
name|getY
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|point
operator|.
name|getX
argument_list|()
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|circleRadius
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
decl_stmt|;
return|return
operator|new
name|Geo3dShape
argument_list|(
name|shape
argument_list|,
name|ctx
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// This is what happens when we create a shape that is invalid.  Although it is conceivable that there are cases where
comment|// the exception is thrown incorrectly, we aren't going to be able to do that in this random test.
continue|continue;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Point
name|randomPointInEmptyShape
parameter_list|(
name|Geo3dShape
name|shape
parameter_list|)
block|{
name|GeoPoint
name|geoPoint
init|=
operator|(
operator|(
name|GeoCircle
operator|)
name|shape
operator|.
name|shape
operator|)
operator|.
name|center
decl_stmt|;
return|return
name|geoPointToSpatial4jPoint
argument_list|(
name|geoPoint
argument_list|)
return|;
block|}
block|}
operator|.
name|testRelateWithRectangle
argument_list|()
expr_stmt|;
block|}
comment|//TODO PORT OTHER TESTS
DECL|method|geoPointToSpatial4jPoint
specifier|private
name|Point
name|geoPointToSpatial4jPoint
parameter_list|(
name|GeoPoint
name|geoPoint
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|makePoint
argument_list|(
name|geoPoint
operator|.
name|x
operator|*
name|DistanceUtils
operator|.
name|RADIANS_TO_DEGREES
argument_list|,
name|geoPoint
operator|.
name|y
operator|*
name|DistanceUtils
operator|.
name|RADIANS_TO_DEGREES
argument_list|)
return|;
block|}
block|}
end_class
end_unit
