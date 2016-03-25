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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|randomFloat
import|;
end_import
begin_comment
comment|/**  * Test basic GeoPoint functionality.  */
end_comment
begin_class
DECL|class|GeoPointTest
specifier|public
class|class
name|GeoPointTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|DEGREES_TO_RADIANS
specifier|static
specifier|final
name|double
name|DEGREES_TO_RADIANS
init|=
name|Math
operator|.
name|PI
operator|/
literal|180
decl_stmt|;
annotation|@
name|Test
DECL|method|testConversion
specifier|public
name|void
name|testConversion
parameter_list|()
block|{
name|testPointRoundTrip
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|90
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
literal|0
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
name|testPointRoundTrip
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
operator|-
literal|90
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
literal|0
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
name|testPointRoundTrip
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
literal|90
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
literal|0
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
name|testPointRoundTrip
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
operator|-
literal|90
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
literal|0
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
specifier|final
name|int
name|times
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|times
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|double
name|pLat
init|=
operator|(
name|randomFloat
argument_list|()
operator|*
literal|180.0
operator|-
literal|90.0
operator|)
operator|*
name|DEGREES_TO_RADIANS
decl_stmt|;
specifier|final
name|double
name|pLon
init|=
operator|(
name|randomFloat
argument_list|()
operator|*
literal|360.0
operator|-
literal|180.0
operator|)
operator|*
name|DEGREES_TO_RADIANS
decl_stmt|;
name|testPointRoundTrip
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
name|pLat
argument_list|,
name|pLon
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
comment|//1e-6 since there's a square root in there (Karl says)
name|testPointRoundTrip
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|pLat
argument_list|,
name|pLon
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPointRoundTrip
specifier|protected
name|void
name|testPointRoundTrip
parameter_list|(
name|PlanetModel
name|planetModel
parameter_list|,
name|double
name|pLat
parameter_list|,
name|double
name|pLon
parameter_list|,
name|double
name|epsilon
parameter_list|)
block|{
specifier|final
name|GeoPoint
name|p1
init|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|pLat
argument_list|,
name|pLon
argument_list|)
decl_stmt|;
comment|// In order to force the reverse conversion, we have to construct a geopoint from just x,y,z
specifier|final
name|GeoPoint
name|p2
init|=
operator|new
name|GeoPoint
argument_list|(
name|p1
operator|.
name|x
argument_list|,
name|p1
operator|.
name|y
argument_list|,
name|p1
operator|.
name|z
argument_list|)
decl_stmt|;
comment|// Now, construct the final point based on getLatitude() and getLongitude()
specifier|final
name|GeoPoint
name|p3
init|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|p2
operator|.
name|getLatitude
argument_list|()
argument_list|,
name|p2
operator|.
name|getLongitude
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|dist
init|=
name|p1
operator|.
name|arcDistance
argument_list|(
name|p3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dist
argument_list|,
name|epsilon
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSurfaceDistance
specifier|public
name|void
name|testSurfaceDistance
parameter_list|()
block|{
specifier|final
name|int
name|times
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|times
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|double
name|p1Lat
init|=
operator|(
name|randomFloat
argument_list|()
operator|*
literal|180.0
operator|-
literal|90.0
operator|)
operator|*
name|DEGREES_TO_RADIANS
decl_stmt|;
specifier|final
name|double
name|p1Lon
init|=
operator|(
name|randomFloat
argument_list|()
operator|*
literal|360.0
operator|-
literal|180.0
operator|)
operator|*
name|DEGREES_TO_RADIANS
decl_stmt|;
specifier|final
name|double
name|p2Lat
init|=
operator|(
name|randomFloat
argument_list|()
operator|*
literal|180.0
operator|-
literal|90.0
operator|)
operator|*
name|DEGREES_TO_RADIANS
decl_stmt|;
specifier|final
name|double
name|p2Lon
init|=
operator|(
name|randomFloat
argument_list|()
operator|*
literal|360.0
operator|-
literal|180.0
operator|)
operator|*
name|DEGREES_TO_RADIANS
decl_stmt|;
specifier|final
name|GeoPoint
name|p1
init|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
name|p1Lat
argument_list|,
name|p1Lon
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|p2
init|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
name|p2Lat
argument_list|,
name|p2Lon
argument_list|)
decl_stmt|;
specifier|final
name|double
name|arcDistance
init|=
name|p1
operator|.
name|arcDistance
argument_list|(
name|p2
argument_list|)
decl_stmt|;
comment|// Compute ellipsoid distance; it should agree for a sphere
specifier|final
name|double
name|surfaceDistance
init|=
name|PlanetModel
operator|.
name|SPHERE
operator|.
name|surfaceDistance
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|arcDistance
argument_list|,
name|surfaceDistance
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
block|}
comment|// Now try some WGS84 points (taken randomly and compared against a known-good implementation)
name|assertEquals
argument_list|(
literal|1.1444648695765323
argument_list|,
name|PlanetModel
operator|.
name|WGS84
operator|.
name|surfaceDistance
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
literal|0.038203808753702884
argument_list|,
operator|-
literal|0.6701260455506466
argument_list|)
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
operator|-
literal|0.8453720422675458
argument_list|,
literal|0.1737353153814496
argument_list|)
argument_list|)
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.4345148695890722
argument_list|,
name|PlanetModel
operator|.
name|WGS84
operator|.
name|surfaceDistance
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
literal|0.5220926323378574
argument_list|,
literal|0.6758041581907408
argument_list|)
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
operator|-
literal|0.8453720422675458
argument_list|,
literal|0.1737353153814496
argument_list|)
argument_list|)
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.32418144616446
argument_list|,
name|PlanetModel
operator|.
name|WGS84
operator|.
name|surfaceDistance
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
literal|0.09541335760967473
argument_list|,
literal|1.2091829760623236
argument_list|)
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
operator|-
literal|0.8501591797459979
argument_list|,
operator|-
literal|2.3044806381627594
argument_list|)
argument_list|)
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.018421047005435
argument_list|,
name|PlanetModel
operator|.
name|WGS84
operator|.
name|surfaceDistance
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
literal|0.3402853531962009
argument_list|,
operator|-
literal|0.43544195327249957
argument_list|)
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
operator|-
literal|0.8501591797459979
argument_list|,
operator|-
literal|2.3044806381627594
argument_list|)
argument_list|)
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBisection
specifier|public
name|void
name|testBisection
parameter_list|()
block|{
specifier|final
name|int
name|times
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|times
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|double
name|p1Lat
init|=
operator|(
name|randomFloat
argument_list|()
operator|*
literal|180.0
operator|-
literal|90.0
operator|)
operator|*
name|DEGREES_TO_RADIANS
decl_stmt|;
specifier|final
name|double
name|p1Lon
init|=
operator|(
name|randomFloat
argument_list|()
operator|*
literal|360.0
operator|-
literal|180.0
operator|)
operator|*
name|DEGREES_TO_RADIANS
decl_stmt|;
specifier|final
name|double
name|p2Lat
init|=
operator|(
name|randomFloat
argument_list|()
operator|*
literal|180.0
operator|-
literal|90.0
operator|)
operator|*
name|DEGREES_TO_RADIANS
decl_stmt|;
specifier|final
name|double
name|p2Lon
init|=
operator|(
name|randomFloat
argument_list|()
operator|*
literal|360.0
operator|-
literal|180.0
operator|)
operator|*
name|DEGREES_TO_RADIANS
decl_stmt|;
specifier|final
name|GeoPoint
name|p1
init|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|p1Lat
argument_list|,
name|p1Lon
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|p2
init|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|p2Lat
argument_list|,
name|p2Lon
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|pMid
init|=
name|PlanetModel
operator|.
name|WGS84
operator|.
name|bisection
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
decl_stmt|;
if|if
condition|(
name|pMid
operator|!=
literal|null
condition|)
block|{
specifier|final
name|double
name|arcDistance
init|=
name|p1
operator|.
name|arcDistance
argument_list|(
name|p2
argument_list|)
decl_stmt|;
specifier|final
name|double
name|sum
init|=
name|pMid
operator|.
name|arcDistance
argument_list|(
name|p1
argument_list|)
operator|+
name|pMid
operator|.
name|arcDistance
argument_list|(
name|p2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|arcDistance
argument_list|,
name|sum
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testBadLatLon
specifier|public
name|void
name|testBadLatLon
parameter_list|()
block|{
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|SPHERE
argument_list|,
literal|50.0
argument_list|,
literal|32.2
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
