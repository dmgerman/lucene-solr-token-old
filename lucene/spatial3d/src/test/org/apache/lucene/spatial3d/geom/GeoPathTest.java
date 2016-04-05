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
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|toRadians
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
DECL|class|GeoPathTest
specifier|public
class|class
name|GeoPathTest
block|{
annotation|@
name|Test
DECL|method|testPathDistance
specifier|public
name|void
name|testPathDistance
parameter_list|()
block|{
comment|// Start with a really simple case
name|GeoStandardPath
name|p
decl_stmt|;
name|GeoPoint
name|gp
decl_stmt|;
name|p
operator|=
operator|new
name|GeoStandardPath
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
literal|0.0
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
literal|0.0
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
literal|0.0
argument_list|,
literal|0.2
argument_list|)
expr_stmt|;
name|p
operator|.
name|done
argument_list|()
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
literal|0.15
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.0
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
literal|0.15
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.15
operator|+
literal|0.05
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.000001
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
literal|0.12
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.12
operator|+
literal|0.0
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.000001
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
literal|0.05
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.000001
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
literal|0.25
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.20
operator|+
literal|0.05
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.000001
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
literal|0.05
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
operator|+
literal|0.05
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
comment|// Compute path distances now
name|p
operator|=
operator|new
name|GeoStandardPath
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
literal|0.0
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
literal|0.0
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
literal|0.0
argument_list|,
literal|0.2
argument_list|)
expr_stmt|;
name|p
operator|.
name|done
argument_list|()
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
literal|0.15
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.15
operator|+
literal|0.05
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.000001
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
literal|0.12
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.12
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
comment|// Now try a vertical path, and make sure distances are as expected
name|p
operator|=
operator|new
name|GeoStandardPath
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.25
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|p
operator|.
name|done
argument_list|()
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
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.0
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
literal|1.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.0
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
literal|0.25
operator|+
literal|0.05
argument_list|,
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|+
literal|0.05
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.000001
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
operator|-
literal|0.5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
operator|+
literal|0.05
argument_list|,
name|p
operator|.
name|computeDistance
argument_list|(
name|DistanceStyle
operator|.
name|ARC
argument_list|,
name|gp
argument_list|)
argument_list|,
literal|0.000001
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPathPointWithin
specifier|public
name|void
name|testPathPointWithin
parameter_list|()
block|{
comment|// Tests whether we can properly detect whether a point is within a path or not
name|GeoStandardPath
name|p
decl_stmt|;
name|GeoPoint
name|gp
decl_stmt|;
name|p
operator|=
operator|new
name|GeoStandardPath
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
comment|// Build a diagonal path crossing the equator
name|p
operator|.
name|addPoint
argument_list|(
operator|-
literal|0.2
argument_list|,
operator|-
literal|0.2
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
literal|0.2
argument_list|,
literal|0.2
argument_list|)
expr_stmt|;
name|p
operator|.
name|done
argument_list|()
expr_stmt|;
comment|// Test points on the path
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
literal|0.2
argument_list|,
operator|-
literal|0.2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
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
literal|0.0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
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
literal|0.1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test points off the path
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
literal|0.2
argument_list|,
literal|0.2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
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
name|p
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
literal|0.2
argument_list|,
operator|-
literal|0.2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
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
name|p
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
comment|// Repeat the test, but across the terminator
name|p
operator|=
operator|new
name|GeoStandardPath
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
comment|// Build a diagonal path crossing the equator
name|p
operator|.
name|addPoint
argument_list|(
operator|-
literal|0.2
argument_list|,
name|Math
operator|.
name|PI
operator|-
literal|0.2
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
literal|0.2
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|+
literal|0.2
argument_list|)
expr_stmt|;
name|p
operator|.
name|done
argument_list|()
expr_stmt|;
comment|// Test points on the path
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
literal|0.2
argument_list|,
name|Math
operator|.
name|PI
operator|-
literal|0.2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
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
name|assertTrue
argument_list|(
name|p
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
operator|+
literal|0.1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|isWithin
argument_list|(
name|gp
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test points off the path
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
literal|0.2
argument_list|,
operator|-
name|Math
operator|.
name|PI
operator|+
literal|0.2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
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
name|p
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
literal|0.2
argument_list|,
name|Math
operator|.
name|PI
operator|-
literal|0.2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
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
literal|0.0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p
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
DECL|method|testGetRelationship
specifier|public
name|void
name|testGetRelationship
parameter_list|()
block|{
name|GeoArea
name|rect
decl_stmt|;
name|GeoStandardPath
name|p
decl_stmt|;
name|GeoStandardPath
name|c
decl_stmt|;
name|GeoPoint
name|point
decl_stmt|;
name|GeoPoint
name|pointApprox
decl_stmt|;
name|int
name|relationship
decl_stmt|;
name|GeoArea
name|area
decl_stmt|;
name|PlanetModel
name|planetModel
decl_stmt|;
name|planetModel
operator|=
operator|new
name|PlanetModel
argument_list|(
literal|1.151145876105594
argument_list|,
literal|0.8488541238944061
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoStandardPath
argument_list|(
name|planetModel
argument_list|,
literal|0.008726646259971648
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
operator|-
literal|0.6925658899376476
argument_list|,
literal|0.6316613927914589
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.27828548161836364
argument_list|,
literal|0.6785795524104564
argument_list|)
expr_stmt|;
name|c
operator|.
name|done
argument_list|()
expr_stmt|;
name|point
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
operator|-
literal|0.49298555067758226
argument_list|,
literal|0.9892440995026406
argument_list|)
expr_stmt|;
name|pointApprox
operator|=
operator|new
name|GeoPoint
argument_list|(
literal|0.5110940362119821
argument_list|,
literal|0.7774603209946239
argument_list|,
operator|-
literal|0.49984312299556544
argument_list|)
expr_stmt|;
name|area
operator|=
name|GeoAreaFactory
operator|.
name|makeGeoArea
argument_list|(
name|planetModel
argument_list|,
literal|0.49937141144985997
argument_list|,
literal|0.5161765426256085
argument_list|,
literal|0.3337218719537796
argument_list|,
literal|0.8544419570901649
argument_list|,
operator|-
literal|0.6347692823688085
argument_list|,
literal|0.3069696588119369
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|c
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
comment|// Start by testing the basic kinds of relationship, increasing in order of difficulty.
name|p
operator|=
operator|new
name|GeoStandardPath
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
operator|-
literal|0.3
argument_list|,
operator|-
literal|0.3
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
literal|0.3
argument_list|,
literal|0.3
argument_list|)
expr_stmt|;
name|p
operator|.
name|done
argument_list|()
expr_stmt|;
comment|// Easiest: The path is wholly contains the georect
name|rect
operator|=
operator|new
name|GeoRectangle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.05
argument_list|,
operator|-
literal|0.05
argument_list|,
operator|-
literal|0.05
argument_list|,
literal|0.05
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoArea
operator|.
name|CONTAINS
argument_list|,
name|rect
operator|.
name|getRelationship
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
comment|// Next easiest: Some endpoints of the rectangle are inside, and some are outside.
name|rect
operator|=
operator|new
name|GeoRectangle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.05
argument_list|,
operator|-
literal|0.05
argument_list|,
operator|-
literal|0.05
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoArea
operator|.
name|OVERLAPS
argument_list|,
name|rect
operator|.
name|getRelationship
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now, all points are outside, but the figures intersect
name|rect
operator|=
operator|new
name|GeoRectangle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.05
argument_list|,
operator|-
literal|0.05
argument_list|,
operator|-
literal|0.5
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoArea
operator|.
name|OVERLAPS
argument_list|,
name|rect
operator|.
name|getRelationship
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
comment|// Finally, all points are outside, and the figures *do not* intersect
name|rect
operator|=
operator|new
name|GeoRectangle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.5
argument_list|,
operator|-
literal|0.5
argument_list|,
operator|-
literal|0.5
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoArea
operator|.
name|WITHIN
argument_list|,
name|rect
operator|.
name|getRelationship
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check that segment edge overlap detection works
name|rect
operator|=
operator|new
name|GeoRectangle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.1
argument_list|,
literal|0.0
argument_list|,
operator|-
literal|0.1
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoArea
operator|.
name|OVERLAPS
argument_list|,
name|rect
operator|.
name|getRelationship
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|rect
operator|=
operator|new
name|GeoRectangle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.2
argument_list|,
literal|0.1
argument_list|,
operator|-
literal|0.2
argument_list|,
operator|-
literal|0.1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoArea
operator|.
name|DISJOINT
argument_list|,
name|rect
operator|.
name|getRelationship
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check if overlap at endpoints behaves as expected next
name|rect
operator|=
operator|new
name|GeoRectangle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.5
argument_list|,
operator|-
literal|0.5
argument_list|,
operator|-
literal|0.5
argument_list|,
operator|-
literal|0.35
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoArea
operator|.
name|OVERLAPS
argument_list|,
name|rect
operator|.
name|getRelationship
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|rect
operator|=
operator|new
name|GeoRectangle
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.5
argument_list|,
operator|-
literal|0.5
argument_list|,
operator|-
literal|0.5
argument_list|,
operator|-
literal|0.45
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GeoArea
operator|.
name|DISJOINT
argument_list|,
name|rect
operator|.
name|getRelationship
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPathBounds
specifier|public
name|void
name|testPathBounds
parameter_list|()
block|{
name|GeoStandardPath
name|c
decl_stmt|;
name|LatLonBounds
name|b
decl_stmt|;
name|XYZBounds
name|xyzb
decl_stmt|;
name|GeoPoint
name|point
decl_stmt|;
name|int
name|relationship
decl_stmt|;
name|GeoArea
name|area
decl_stmt|;
name|PlanetModel
name|planetModel
decl_stmt|;
name|planetModel
operator|=
operator|new
name|PlanetModel
argument_list|(
literal|0.751521665790406
argument_list|,
literal|1.248478334209594
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoStandardPath
argument_list|(
name|planetModel
argument_list|,
literal|0.7504915783575618
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.10869761172400265
argument_list|,
literal|0.08895880215465272
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.22467878641991612
argument_list|,
literal|0.10972973084229565
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
operator|-
literal|0.7398772468744732
argument_list|,
operator|-
literal|0.4465812941383364
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
operator|-
literal|0.18462055300079366
argument_list|,
operator|-
literal|0.6713857796763727
argument_list|)
expr_stmt|;
name|c
operator|.
name|done
argument_list|()
expr_stmt|;
name|point
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
operator|-
literal|0.626645355125733
argument_list|,
operator|-
literal|1.409304625439381
argument_list|)
expr_stmt|;
name|xyzb
operator|=
operator|new
name|XYZBounds
argument_list|()
expr_stmt|;
name|c
operator|.
name|getBounds
argument_list|(
name|xyzb
argument_list|)
expr_stmt|;
name|area
operator|=
name|GeoAreaFactory
operator|.
name|makeGeoArea
argument_list|(
name|planetModel
argument_list|,
name|xyzb
operator|.
name|getMinimumX
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMaximumX
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMinimumY
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMaximumY
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMinimumZ
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMaximumZ
argument_list|()
argument_list|)
expr_stmt|;
name|relationship
operator|=
name|area
operator|.
name|getRelationship
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|relationship
operator|==
name|GeoArea
operator|.
name|WITHIN
operator|||
name|relationship
operator|==
name|GeoArea
operator|.
name|OVERLAPS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|area
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
comment|// No longer true due to fixed GeoStandardPath waypoints.
comment|//assertTrue(c.isWithin(point));
name|c
operator|=
operator|new
name|GeoStandardPath
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
literal|0.6894050545377601
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
operator|-
literal|0.0788176065762948
argument_list|,
literal|0.9431251741731624
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.510387871458147
argument_list|,
literal|0.5327078872484678
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
operator|-
literal|0.5624521609859962
argument_list|,
literal|1.5398841746888388
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
operator|-
literal|0.5025171434638661
argument_list|,
operator|-
literal|0.5895998642788894
argument_list|)
expr_stmt|;
name|c
operator|.
name|done
argument_list|()
expr_stmt|;
name|point
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
literal|0.023652082107211682
argument_list|,
literal|0.023131910152748437
argument_list|)
expr_stmt|;
comment|//System.err.println("Point.x = "+point.x+"; point.y="+point.y+"; point.z="+point.z);
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
name|xyzb
operator|=
operator|new
name|XYZBounds
argument_list|()
expr_stmt|;
name|c
operator|.
name|getBounds
argument_list|(
name|xyzb
argument_list|)
expr_stmt|;
name|area
operator|=
name|GeoAreaFactory
operator|.
name|makeGeoArea
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|xyzb
operator|.
name|getMinimumX
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMaximumX
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMinimumY
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMaximumY
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMinimumZ
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMaximumZ
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.err.println("minx="+xyzb.getMinimumX()+" maxx="+xyzb.getMaximumX()+" miny="+xyzb.getMinimumY()+" maxy="+xyzb.getMaximumY()+" minz="+xyzb.getMinimumZ()+" maxz="+xyzb.getMaximumZ());
comment|//System.err.println("point.x="+point.x+" point.y="+point.y+" point.z="+point.z);
name|relationship
operator|=
name|area
operator|.
name|getRelationship
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|relationship
operator|==
name|GeoArea
operator|.
name|WITHIN
operator|||
name|relationship
operator|==
name|GeoArea
operator|.
name|OVERLAPS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|area
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoStandardPath
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
literal|0.7766715171374766
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
operator|-
literal|0.2751718361148076
argument_list|,
operator|-
literal|0.7786721269011477
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.5728375851539309
argument_list|,
operator|-
literal|1.2700115736820465
argument_list|)
expr_stmt|;
name|c
operator|.
name|done
argument_list|()
expr_stmt|;
name|point
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
operator|-
literal|0.01580760332365284
argument_list|,
operator|-
literal|0.03956004622490505
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|c
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
name|xyzb
operator|=
operator|new
name|XYZBounds
argument_list|()
expr_stmt|;
name|c
operator|.
name|getBounds
argument_list|(
name|xyzb
argument_list|)
expr_stmt|;
name|area
operator|=
name|GeoAreaFactory
operator|.
name|makeGeoArea
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|xyzb
operator|.
name|getMinimumX
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMaximumX
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMinimumY
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMaximumY
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMinimumZ
argument_list|()
argument_list|,
name|xyzb
operator|.
name|getMaximumZ
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.err.println("minx="+xyzb.getMinimumX()+" maxx="+xyzb.getMaximumX()+" miny="+xyzb.getMinimumY()+" maxy="+xyzb.getMaximumY()+" minz="+xyzb.getMinimumZ()+" maxz="+xyzb.getMaximumZ());
comment|//System.err.println("point.x="+point.x+" point.y="+point.y+" point.z="+point.z);
name|relationship
operator|=
name|area
operator|.
name|getRelationship
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|relationship
operator|==
name|GeoArea
operator|.
name|WITHIN
operator|||
name|relationship
operator|==
name|GeoArea
operator|.
name|OVERLAPS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|area
operator|.
name|isWithin
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|GeoStandardPath
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
operator|-
literal|0.3
argument_list|,
operator|-
literal|0.3
argument_list|)
expr_stmt|;
name|c
operator|.
name|addPoint
argument_list|(
literal|0.3
argument_list|,
literal|0.3
argument_list|)
expr_stmt|;
name|c
operator|.
name|done
argument_list|()
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
literal|0.4046919
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
literal|0.4046919
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
literal|0.3999999
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
literal|0.3999999
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
annotation|@
name|Test
DECL|method|testCoLinear
specifier|public
name|void
name|testCoLinear
parameter_list|()
block|{
comment|// p1: (12,-90), p2: (11, -55), (129, -90)
name|GeoStandardPath
name|p
init|=
operator|new
name|GeoStandardPath
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|0.1
argument_list|)
decl_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
name|toRadians
argument_list|(
operator|-
literal|90
argument_list|)
argument_list|,
name|toRadians
argument_list|(
literal|12
argument_list|)
argument_list|)
expr_stmt|;
comment|//south pole
name|p
operator|.
name|addPoint
argument_list|(
name|toRadians
argument_list|(
operator|-
literal|55
argument_list|)
argument_list|,
name|toRadians
argument_list|(
literal|11
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|addPoint
argument_list|(
name|toRadians
argument_list|(
operator|-
literal|90
argument_list|)
argument_list|,
name|toRadians
argument_list|(
literal|129
argument_list|)
argument_list|)
expr_stmt|;
comment|//south pole again
name|p
operator|.
name|done
argument_list|()
expr_stmt|;
comment|//at least test this doesn't bomb like it used too -- LUCENE-6520
block|}
block|}
end_class
end_unit
