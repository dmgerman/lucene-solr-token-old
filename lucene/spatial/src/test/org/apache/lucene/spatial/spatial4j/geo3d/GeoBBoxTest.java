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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
DECL|class|GeoBBoxTest
specifier|public
class|class
name|GeoBBoxTest
block|{
DECL|field|DEGREES_TO_RADIANS
specifier|protected
specifier|final
name|double
name|DEGREES_TO_RADIANS
init|=
name|Math
operator|.
name|PI
operator|/
literal|180.0
decl_stmt|;
annotation|@
name|Test
DECL|method|testBBoxDegenerate
specifier|public
name|void
name|testBBoxDegenerate
parameter_list|()
block|{
name|GeoBBox
name|box
decl_stmt|;
name|GeoConvexPolygon
name|cp
decl_stmt|;
name|int
name|relationship
decl_stmt|;
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
init|=
operator|new
name|ArrayList
argument_list|<
name|GeoPoint
argument_list|>
argument_list|()
decl_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|24
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
operator|-
literal|30
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
literal|11
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
literal|101
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
literal|49
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
operator|-
literal|176
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
argument_list|)
expr_stmt|;
name|GeoMembershipShape
name|shape
init|=
name|GeoPolygonFactory
operator|.
name|makeGeoPolygon
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
name|points
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|box
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
literal|64
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
operator|-
literal|64
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
operator|-
literal|180
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
literal|180
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
expr_stmt|;
name|relationship
operator|=
name|box
operator|.
name|getRelationship
argument_list|(
name|shape
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoArea
operator|.
name|CONTAINS
argument_list|,
name|relationship
argument_list|)
expr_stmt|;
name|box
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
literal|61.85
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
operator|-
literal|67.5
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
operator|-
literal|180
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
operator|-
literal|168.75
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Shape = "
operator|+
name|shape
operator|+
literal|" Rect = "
operator|+
name|box
argument_list|)
expr_stmt|;
name|relationship
operator|=
name|box
operator|.
name|getRelationship
argument_list|(
name|shape
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoArea
operator|.
name|CONTAINS
argument_list|,
name|relationship
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBBoxPointWithin
specifier|public
name|void
name|testBBoxPointWithin
parameter_list|()
block|{
name|GeoBBox
name|box
decl_stmt|;
name|GeoPoint
name|gp
decl_stmt|;
comment|// Standard normal Rect box, not crossing dateline
name|box
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
operator|-
literal|1.0
argument_list|,
literal|1.0
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
literal|0.1
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
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
literal|0.1
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
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
name|box
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
literal|0.1
argument_list|,
literal|1.1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
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
literal|0.1
argument_list|,
operator|-
literal|1.1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
comment|// Standard normal Rect box, crossing dateline
name|box
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
name|Math
operator|.
name|PI
operator|-
literal|1.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|+
literal|1.0
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
literal|0.1
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
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
literal|0.1
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
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
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
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
literal|0.1
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|+
literal|1.1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
comment|//bad lon: gp = new GeoPoint(PlanetModel.SPHERE, -0.1, -Math.PI - 1.1);
comment|//assertFalse(box.isWithin(gp));
comment|// Latitude zone rectangle
name|box
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|,
name|Math
operator|.
name|PI
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
literal|0.1
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
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
literal|0.1
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
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
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
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
literal|0.1
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|+
literal|1.1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
comment|//bad lon: gp = new GeoPoint(PlanetModel.SPHERE, -0.1, -Math.PI - 1.1);
comment|//assertTrue(box.isWithin(gp));
comment|// World
name|box
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|,
name|Math
operator|.
name|PI
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
literal|0.1
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
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
literal|0.1
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
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
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
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
literal|0.1
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|+
literal|1.1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
comment|//bad lat: gp = new GeoPoint(PlanetModel.SPHERE, -0.1, -Math.PI - 1.1);
comment|//assertTrue(box.isWithin(gp));
block|}
annotation|@
name|Test
DECL|method|testBBoxExpand
specifier|public
name|void
name|testBBoxExpand
parameter_list|()
block|{
name|GeoBBox
name|box
decl_stmt|;
name|GeoPoint
name|gp
decl_stmt|;
comment|// Standard normal Rect box, not crossing dateline
name|box
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
operator|-
literal|1.0
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|box
operator|=
name|box
operator|.
name|expand
argument_list|(
literal|0.1
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
literal|0.0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
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
literal|0.0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
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
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|-
literal|0.05
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
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
name|Math
operator|.
name|PI
operator|*
literal|0.25
operator|-
literal|0.15
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
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
literal|0.1
argument_list|,
operator|-
literal|1.05
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
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
literal|0.1
argument_list|,
operator|-
literal|1.15
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
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
literal|0.1
argument_list|,
literal|1.05
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|box
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
literal|0.1
argument_list|,
literal|1.15
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|box
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
DECL|method|testBBoxBounds
specifier|public
name|void
name|testBBoxBounds
parameter_list|()
block|{
name|GeoBBox
name|c
decl_stmt|;
name|Bounds
name|b
decl_stmt|;
name|c
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
operator|-
literal|1.0
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
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
literal|1.0
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
literal|1.0
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
name|Math
operator|.
name|PI
operator|*
literal|0.25
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
literal|0.0
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|c
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
literal|1.0
argument_list|,
operator|-
literal|1.0
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
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
comment|//assertEquals(1.0,b.getLeftLongitude(),0.000001);
comment|//assertEquals(-1.0,b.getRightLongitude(),0.000001);
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
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
literal|0.0
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|c
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
operator|-
literal|1.0
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
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
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
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
literal|1.0
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
literal|1.0
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|c
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|1.0
argument_list|,
operator|-
literal|1.0
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
comment|//assertEquals(1.0,b.getLeftLongitude(),0.000001);
comment|//assertEquals(-1.0,b.getRightLongitude(),0.000001);
comment|// Check wide variants of rectangle and longitude slice
name|c
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|+
literal|0.1
argument_list|,
name|Math
operator|.
name|PI
operator|-
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
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
comment|//assertEquals(-Math.PI+0.1,b.getLeftLongitude(),0.000001);
comment|//assertEquals(Math.PI-0.1,b.getRightLongitude(),0.000001);
name|assertEquals
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
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
literal|0.0
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|c
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
name|Math
operator|.
name|PI
operator|-
literal|0.1
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|+
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
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
name|Math
operator|.
name|PI
operator|-
literal|0.1
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
name|Math
operator|.
name|PI
operator|+
literal|0.1
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
name|Math
operator|.
name|PI
operator|*
literal|0.25
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
literal|0.0
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
name|c
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|+
literal|0.1
argument_list|,
name|Math
operator|.
name|PI
operator|-
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
comment|//assertEquals(-Math.PI+0.1,b.getLeftLongitude(),0.000001);
comment|//assertEquals(Math.PI-0.1,b.getRightLongitude(),0.000001);
name|c
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
name|Math
operator|.
name|PI
operator|-
literal|0.1
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|+
literal|0.1
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
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
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|-
literal|0.1
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
name|Math
operator|.
name|PI
operator|+
literal|0.1
argument_list|,
name|b
operator|.
name|getRightLongitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
comment|// Check latitude zone
name|c
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|1.0
argument_list|,
operator|-
literal|1.0
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|b
operator|=
name|c
operator|.
name|getBounds
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
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
literal|1.0
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
literal|1.0
argument_list|,
name|b
operator|.
name|getMaxLatitude
argument_list|()
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
comment|// Now, combine a few things to test the bounds object
name|GeoBBox
name|c1
decl_stmt|;
name|GeoBBox
name|c2
decl_stmt|;
name|c1
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|c2
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|b
operator|=
operator|new
name|Bounds
argument_list|()
expr_stmt|;
name|b
operator|=
name|c1
operator|.
name|getBounds
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|b
operator|=
name|c2
operator|.
name|getBounds
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|c1
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|c2
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|,
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|)
expr_stmt|;
name|b
operator|=
operator|new
name|Bounds
argument_list|()
expr_stmt|;
name|b
operator|=
name|c1
operator|.
name|getBounds
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|b
operator|=
name|c2
operator|.
name|getBounds
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
comment|//assertEquals(-Math.PI,b.getLeftLongitude(),0.000001);
comment|//assertEquals(Math.PI*0.5,b.getRightLongitude(),0.000001);
name|c1
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|c2
operator|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
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
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
argument_list|,
literal|0.0
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
expr_stmt|;
name|b
operator|=
operator|new
name|Bounds
argument_list|()
expr_stmt|;
name|b
operator|=
name|c1
operator|.
name|getBounds
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|b
operator|=
name|c2
operator|.
name|getBounds
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoLongitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoTopLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|checkNoBottomLatitudeBound
argument_list|()
argument_list|)
expr_stmt|;
comment|//assertEquals(-Math.PI * 0.5,b.getLeftLongitude(),0.000001);
comment|//assertEquals(Math.PI,b.getRightLongitude(),0.000001);
block|}
block|}
end_class
end_unit
