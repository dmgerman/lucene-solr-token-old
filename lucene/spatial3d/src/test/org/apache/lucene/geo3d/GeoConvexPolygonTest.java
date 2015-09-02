begin_unit
begin_package
DECL|package|org.apache.lucene.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo3d
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import
begin_class
DECL|class|GeoConvexPolygonTest
specifier|public
class|class
name|GeoConvexPolygonTest
block|{
annotation|@
name|Test
DECL|method|testPolygonPointWithin
specifier|public
name|void
name|testPolygonPointWithin
parameter_list|()
block|{
name|GeoConvexPolygon
name|c
decl_stmt|;
name|GeoPoint
name|gp
decl_stmt|;
name|c
operator|=
operator|new
name|GeoConvexPolygon
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
literal|0.1
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.0
argument_list|,
operator|-
literal|0.6
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.1
argument_list|,
operator|-
literal|0.5
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.0
argument_list|,
operator|-
literal|0.4
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|c
operator|.
name|done
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Sample some points within
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.55
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.45
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
literal|0.05
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.05
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
comment|// Sample some nearby points outside, and compute distance-to-shape for them as well
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.65
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.05
argument_list|,
name|c
operator|.
name|computeOutsideDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|1e-12
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.05
argument_list|,
name|c
operator|.
name|computeOutsideDistance
argument_list|(
name|DistanceStyle
operator|.
name|NORMAL
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|1e-3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.05
argument_list|,
name|c
operator|.
name|computeOutsideDistance
argument_list|(
name|DistanceStyle
operator|.
name|LINEAR
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|1e-3
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.35
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
literal|0.15
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.15
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
comment|// Random points outside
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
name|gp
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPolygonBounds
specifier|public
name|void
name|testPolygonBounds
parameter_list|()
block|{
name|GeoConvexPolygon
name|c
decl_stmt|;
name|LatLonBounds
name|b
decl_stmt|;
name|c
operator|=
operator|new
name|GeoConvexPolygon
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
literal|0.1
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.0
argument_list|,
operator|-
literal|0.6
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.1
argument_list|,
operator|-
literal|0.5
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.0
argument_list|,
operator|-
literal|0.4
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|c
operator|.
name|done
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|b
operator|=
operator|new
name|LatLonBounds
argument_list|()
expr_stmt|;
name|c
operator|.
name|getBounds
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.6
argument_list|,
name|b
operator|.
name|getLeftLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.4
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|0.1
argument_list|,
name|b
operator|.
name|getMinLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.1
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
