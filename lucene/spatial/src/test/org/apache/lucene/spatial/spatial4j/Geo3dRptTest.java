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
name|io
operator|.
name|IOException
import|;
end_import
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Repeat
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|composite
operator|.
name|CompositeSpatialStrategy
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
name|prefix
operator|.
name|RandomSpatialOpStrategyTestCase
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
name|prefix
operator|.
name|RecursivePrefixTreeStrategy
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
name|prefix
operator|.
name|tree
operator|.
name|GeohashPrefixTree
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
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTree
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
name|query
operator|.
name|SpatialOperation
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
name|serialized
operator|.
name|SerializedDVStrategy
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
name|Geo3dShape
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
name|GeoPolygonFactory
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
DECL|class|Geo3dRptTest
specifier|public
class|class
name|Geo3dRptTest
extends|extends
name|RandomSpatialOpStrategyTestCase
block|{
DECL|field|grid
specifier|private
name|SpatialPrefixTree
name|grid
decl_stmt|;
DECL|field|rptStrategy
specifier|private
name|RecursivePrefixTreeStrategy
name|rptStrategy
decl_stmt|;
block|{
name|this
operator|.
name|ctx
operator|=
name|SpatialContext
operator|.
name|GEO
expr_stmt|;
block|}
DECL|method|setupGeohashGrid
specifier|private
name|void
name|setupGeohashGrid
parameter_list|()
block|{
name|this
operator|.
name|grid
operator|=
operator|new
name|GeohashPrefixTree
argument_list|(
name|ctx
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//A fairly shallow grid
name|this
operator|.
name|rptStrategy
operator|=
name|newRPT
argument_list|()
expr_stmt|;
block|}
DECL|method|newRPT
specifier|protected
name|RecursivePrefixTreeStrategy
name|newRPT
parameter_list|()
block|{
specifier|final
name|RecursivePrefixTreeStrategy
name|rpt
init|=
operator|new
name|RecursivePrefixTreeStrategy
argument_list|(
name|this
operator|.
name|grid
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"_rpt"
argument_list|)
decl_stmt|;
name|rpt
operator|.
name|setDistErrPct
argument_list|(
literal|0.10
argument_list|)
expr_stmt|;
comment|//not too many cells
return|return
name|rpt
return|;
block|}
annotation|@
name|Override
DECL|method|needsDocValues
specifier|protected
name|boolean
name|needsDocValues
parameter_list|()
block|{
return|return
literal|true
return|;
comment|//due to SerializedDVStrategy
block|}
DECL|method|setupStrategy
specifier|private
name|void
name|setupStrategy
parameter_list|()
block|{
comment|//setup
name|setupGeohashGrid
argument_list|()
expr_stmt|;
name|SerializedDVStrategy
name|serializedDVStrategy
init|=
operator|new
name|SerializedDVStrategy
argument_list|(
name|ctx
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"_sdv"
argument_list|)
decl_stmt|;
name|this
operator|.
name|strategy
operator|=
operator|new
name|CompositeSpatialStrategy
argument_list|(
literal|"composite_"
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|rptStrategy
argument_list|,
name|serializedDVStrategy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Repeat
argument_list|(
name|iterations
operator|=
literal|20
argument_list|)
comment|//Seed("A9C215F48F200BB0")
DECL|method|testOperations
specifier|public
name|void
name|testOperations
parameter_list|()
throws|throws
name|IOException
block|{
name|setupStrategy
argument_list|()
expr_stmt|;
name|testOperationRandomShapes
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTriangleDisjointRect2
specifier|public
name|void
name|testTriangleDisjointRect2
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Rectangle
name|rect
init|=
name|ctx
operator|.
name|makeRectangle
argument_list|(
operator|-
literal|176
argument_list|,
operator|-
literal|176
argument_list|,
operator|-
literal|37
argument_list|,
operator|-
literal|34
argument_list|)
decl_stmt|;
specifier|final
name|Shape
name|triangle
init|=
name|makeTriangle
argument_list|(
literal|116
argument_list|,
literal|45
argument_list|,
literal|169
argument_list|,
literal|7
argument_list|,
literal|92
argument_list|,
operator|-
literal|63
argument_list|)
decl_stmt|;
comment|//these shouldn't intersect
name|assertEquals
argument_list|(
name|SpatialRelation
operator|.
name|DISJOINT
argument_list|,
name|triangle
operator|.
name|relate
argument_list|(
name|rect
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeTriangle
specifier|private
name|Shape
name|makeTriangle
parameter_list|(
name|double
name|x1
parameter_list|,
name|double
name|y1
parameter_list|,
name|double
name|x2
parameter_list|,
name|double
name|y2
parameter_list|,
name|double
name|x3
parameter_list|,
name|double
name|y3
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|geoPoints
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|geoPoints
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|y1
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|x1
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
argument_list|)
expr_stmt|;
name|geoPoints
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|y2
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|x2
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
argument_list|)
expr_stmt|;
name|geoPoints
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|y3
operator|*
name|DEGREES_TO_RADIANS
argument_list|,
name|x3
operator|*
name|DEGREES_TO_RADIANS
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|convexPointIndex
init|=
literal|0
decl_stmt|;
specifier|final
name|GeoShape
name|shape
init|=
name|GeoPolygonFactory
operator|.
name|makeGeoPolygon
argument_list|(
name|geoPoints
argument_list|,
name|convexPointIndex
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
annotation|@
name|Override
DECL|method|randomIndexedShape
specifier|protected
name|Shape
name|randomIndexedShape
parameter_list|()
block|{
return|return
name|randomRectangle
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|randomQueryShape
specifier|protected
name|Shape
name|randomQueryShape
parameter_list|()
block|{
comment|//random triangle
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|geoPoints
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|geoPoints
operator|.
name|size
argument_list|()
operator|<
literal|3
condition|)
block|{
specifier|final
name|Point
name|point
init|=
name|randomPoint
argument_list|()
decl_stmt|;
specifier|final
name|GeoPoint
name|gPt
init|=
operator|new
name|GeoPoint
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|geoPoints
operator|.
name|contains
argument_list|(
name|gPt
argument_list|)
operator|==
literal|false
condition|)
block|{
name|geoPoints
operator|.
name|add
argument_list|(
name|gPt
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|convexPointIndex
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|final
name|GeoShape
name|shape
init|=
name|GeoPolygonFactory
operator|.
name|makeGeoPolygon
argument_list|(
name|geoPoints
argument_list|,
name|convexPointIndex
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
block|}
end_class
end_unit
