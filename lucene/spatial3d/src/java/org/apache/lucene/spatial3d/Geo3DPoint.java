begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
package|;
end_package
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
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
name|index
operator|.
name|PointValues
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
name|geo
operator|.
name|Polygon
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
name|geo
operator|.
name|GeoUtils
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
name|spatial3d
operator|.
name|geom
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
name|spatial3d
operator|.
name|geom
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
name|spatial3d
operator|.
name|geom
operator|.
name|PlanetModel
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoCircleFactory
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoBBoxFactory
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
name|spatial3d
operator|.
name|geom
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoPathFactory
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoCompositePolygon
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoPolygon
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
name|search
operator|.
name|Query
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|NumericUtils
import|;
end_import
begin_comment
comment|/**  * Add this to a document to index lat/lon or x/y/z point, indexed as a 3D point.  * Multiple values are allowed: just add multiple Geo3DPoint to the document with the  * same field name.  *<p>  * This field defines static factory methods for creating a shape query:  *<ul>  *<li>{@link #newShapeQuery newShapeQuery()} for matching all points inside a specified shape  *</ul>  * @see PointValues  *  @lucene.experimental */
end_comment
begin_class
DECL|class|Geo3DPoint
specifier|public
specifier|final
class|class
name|Geo3DPoint
extends|extends
name|Field
block|{
comment|/** How many radians are in one earth surface meter */
DECL|field|RADIANS_PER_METER
specifier|public
specifier|final
specifier|static
name|double
name|RADIANS_PER_METER
init|=
literal|1.0
operator|/
name|PlanetModel
operator|.
name|WGS84_MEAN
decl_stmt|;
comment|/** How many radians are in one degree */
DECL|field|RADIANS_PER_DEGREE
specifier|public
specifier|final
specifier|static
name|double
name|RADIANS_PER_DEGREE
init|=
name|Math
operator|.
name|PI
operator|/
literal|180.0
decl_stmt|;
comment|/** Indexing {@link FieldType}. */
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE
operator|.
name|setDimensions
argument_list|(
literal|3
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**     * Creates a new Geo3DPoint field with the specified latitude, longitude (in degrees).    *    * @throws IllegalArgumentException if the field name is null or latitude or longitude are out of bounds    */
DECL|method|Geo3DPoint
specifier|public
name|Geo3DPoint
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
comment|// Translate latitude/longitude to x,y,z:
specifier|final
name|GeoPoint
name|point
init|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|fromDegrees
argument_list|(
name|latitude
argument_list|)
argument_list|,
name|fromDegrees
argument_list|(
name|longitude
argument_list|)
argument_list|)
decl_stmt|;
name|fillFieldsData
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
expr_stmt|;
block|}
comment|/** Converts degress to radians */
DECL|method|fromDegrees
specifier|private
specifier|static
name|double
name|fromDegrees
parameter_list|(
specifier|final
name|double
name|degrees
parameter_list|)
block|{
return|return
name|degrees
operator|*
name|RADIANS_PER_DEGREE
return|;
block|}
comment|/** Converts earth-surface meters to radians */
DECL|method|fromMeters
specifier|private
specifier|static
name|double
name|fromMeters
parameter_list|(
specifier|final
name|double
name|meters
parameter_list|)
block|{
return|return
name|meters
operator|*
name|RADIANS_PER_METER
return|;
block|}
comment|/**    * Create a query for matching points within the specified distance of the supplied location.    * @param field field name. must not be null.  Note that because    * {@link PlanetModel#WGS84} is used, this query is approximate and may have up    * to 0.5% error.    *    * @param latitude latitude at the center: must be within standard +/-90 coordinate bounds.    * @param longitude longitude at the center: must be within standard +/-180 coordinate bounds.    * @param radiusMeters maximum distance from the center in meters: must be non-negative and finite.    * @return query matching points within this distance    * @throws IllegalArgumentException if {@code field} is null, location has invalid coordinates, or radius is invalid.    */
DECL|method|newDistanceQuery
specifier|public
specifier|static
name|Query
name|newDistanceQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
name|latitude
parameter_list|,
specifier|final
name|double
name|longitude
parameter_list|,
specifier|final
name|double
name|radiusMeters
parameter_list|)
block|{
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
specifier|final
name|GeoShape
name|shape
init|=
name|GeoCircleFactory
operator|.
name|makeGeoCircle
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|fromDegrees
argument_list|(
name|latitude
argument_list|)
argument_list|,
name|fromDegrees
argument_list|(
name|longitude
argument_list|)
argument_list|,
name|fromMeters
argument_list|(
name|radiusMeters
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|newShapeQuery
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
comment|/**    * Create a query for matching a box.    *<p>    * The box may cross over the dateline.    * @param field field name. must not be null.    * @param minLatitude latitude lower bound: must be within standard +/-90 coordinate bounds.    * @param maxLatitude latitude upper bound: must be within standard +/-90 coordinate bounds.    * @param minLongitude longitude lower bound: must be within standard +/-180 coordinate bounds.    * @param maxLongitude longitude upper bound: must be within standard +/-180 coordinate bounds.    * @return query matching points within this box    * @throws IllegalArgumentException if {@code field} is null, or the box has invalid coordinates.    */
DECL|method|newBoxQuery
specifier|public
specifier|static
name|Query
name|newBoxQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
name|minLatitude
parameter_list|,
specifier|final
name|double
name|maxLatitude
parameter_list|,
specifier|final
name|double
name|minLongitude
parameter_list|,
specifier|final
name|double
name|maxLongitude
parameter_list|)
block|{
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|minLatitude
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|minLongitude
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|maxLatitude
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|maxLongitude
argument_list|)
expr_stmt|;
specifier|final
name|GeoShape
name|shape
init|=
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|fromDegrees
argument_list|(
name|maxLatitude
argument_list|)
argument_list|,
name|fromDegrees
argument_list|(
name|minLatitude
argument_list|)
argument_list|,
name|fromDegrees
argument_list|(
name|minLongitude
argument_list|)
argument_list|,
name|fromDegrees
argument_list|(
name|maxLongitude
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|newShapeQuery
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
comment|/**     * Create a query for matching a polygon.  The polygon should have a limited number of edges (less than 100) and be well-defined,    * with well-separated vertices.    *<p>    * The supplied {@code polygons} must be clockwise on the outside level, counterclockwise on the next level in, etc.    * @param field field name. must not be null.    * @param polygons is the list of polygons to use to construct the query; must be at least one.    * @return query matching points within this polygon    */
DECL|method|newPolygonQuery
specifier|public
specifier|static
name|Query
name|newPolygonQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
comment|//System.err.println("Creating polygon...");
if|if
condition|(
name|polygons
operator|.
name|length
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"need at least one polygon"
argument_list|)
throw|;
block|}
specifier|final
name|GeoShape
name|shape
decl_stmt|;
if|if
condition|(
name|polygons
operator|.
name|length
operator|==
literal|1
condition|)
block|{
specifier|final
name|GeoShape
name|component
init|=
name|fromPolygon
argument_list|(
name|polygons
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|component
operator|==
literal|null
condition|)
block|{
comment|// Polygon is degenerate
name|shape
operator|=
operator|new
name|GeoCompositePolygon
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|shape
operator|=
name|component
expr_stmt|;
block|}
block|}
else|else
block|{
specifier|final
name|GeoCompositePolygon
name|poly
init|=
operator|new
name|GeoCompositePolygon
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Polygon
name|p
range|:
name|polygons
control|)
block|{
specifier|final
name|GeoPolygon
name|component
init|=
name|fromPolygon
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|component
operator|!=
literal|null
condition|)
block|{
name|poly
operator|.
name|addShape
argument_list|(
name|component
argument_list|)
expr_stmt|;
block|}
block|}
name|shape
operator|=
name|poly
expr_stmt|;
block|}
comment|//System.err.println("...done");
return|return
name|newShapeQuery
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
comment|/**     * Create a query for matching a large polygon.  This differs from the related newPolygonQuery in that it    * does little or no legality checking and is optimized for very large numbers of polygon edges.    *<p>    * The supplied {@code polygons} must be clockwise on the outside level, counterclockwise on the next level in, etc.    * @param field field name. must not be null.    * @param polygons is the list of polygons to use to construct the query; must be at least one.    * @return query matching points within this polygon    */
DECL|method|newLargePolygonQuery
specifier|public
specifier|static
name|Query
name|newLargePolygonQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
if|if
condition|(
name|polygons
operator|.
name|length
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"need at least one polygon"
argument_list|)
throw|;
block|}
specifier|final
name|GeoShape
name|shape
init|=
name|fromLargePolygon
argument_list|(
name|polygons
argument_list|)
decl_stmt|;
return|return
name|newShapeQuery
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
comment|/**     * Create a query for matching a path.    *<p>    * @param field field name. must not be null.    * @param pathLatitudes latitude values for points of the path: must be within standard +/-90 coordinate bounds.    * @param pathLongitudes longitude values for points of the path: must be within standard +/-180 coordinate bounds.    * @param pathWidthMeters width of the path in meters.    * @return query matching points within this polygon    */
DECL|method|newPathQuery
specifier|public
specifier|static
name|Query
name|newPathQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
index|[]
name|pathLatitudes
parameter_list|,
specifier|final
name|double
index|[]
name|pathLongitudes
parameter_list|,
specifier|final
name|double
name|pathWidthMeters
parameter_list|)
block|{
if|if
condition|(
name|pathLatitudes
operator|.
name|length
operator|!=
name|pathLongitudes
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"same number of latitudes and longitudes required"
argument_list|)
throw|;
block|}
specifier|final
name|GeoPoint
index|[]
name|points
init|=
operator|new
name|GeoPoint
index|[
name|pathLatitudes
operator|.
name|length
index|]
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
name|pathLatitudes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|double
name|latitude
init|=
name|pathLatitudes
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|double
name|longitude
init|=
name|pathLongitudes
index|[
name|i
index|]
decl_stmt|;
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
name|points
index|[
name|i
index|]
operator|=
operator|new
name|GeoPoint
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|fromDegrees
argument_list|(
name|latitude
argument_list|)
argument_list|,
name|fromDegrees
argument_list|(
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|GeoShape
name|shape
init|=
name|GeoPathFactory
operator|.
name|makeGeoPath
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|fromMeters
argument_list|(
name|pathWidthMeters
argument_list|)
argument_list|,
name|points
argument_list|)
decl_stmt|;
return|return
name|newShapeQuery
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
return|;
block|}
comment|/**    * Convert a Polygon object to a large GeoPolygon.    * @param polygons is the list of polygons to convert.    * @return the large GeoPolygon.    */
DECL|method|fromLargePolygon
specifier|private
specifier|static
name|GeoPolygon
name|fromLargePolygon
parameter_list|(
specifier|final
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
return|return
name|GeoPolygonFactory
operator|.
name|makeLargeGeoPolygon
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|convertToDescription
argument_list|(
name|polygons
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Convert a list of polygons to a list of polygon descriptions.    * @param polygons is the list of polygons to convert.    * @return the list of polygon descriptions.    */
DECL|method|convertToDescription
specifier|private
specifier|static
name|List
argument_list|<
name|GeoPolygonFactory
operator|.
name|PolygonDescription
argument_list|>
name|convertToDescription
parameter_list|(
specifier|final
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|GeoPolygonFactory
operator|.
name|PolygonDescription
argument_list|>
name|descriptions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|polygons
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Polygon
name|polygon
range|:
name|polygons
control|)
block|{
specifier|final
name|Polygon
index|[]
name|theHoles
init|=
name|polygon
operator|.
name|getHoles
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|GeoPolygonFactory
operator|.
name|PolygonDescription
argument_list|>
name|holes
init|=
name|convertToDescription
argument_list|(
name|theHoles
argument_list|)
decl_stmt|;
comment|// Now do the polygon itself
specifier|final
name|double
index|[]
name|polyLats
init|=
name|polygon
operator|.
name|getPolyLats
argument_list|()
decl_stmt|;
specifier|final
name|double
index|[]
name|polyLons
init|=
name|polygon
operator|.
name|getPolyLons
argument_list|()
decl_stmt|;
comment|// I presume the arguments have already been checked
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|polyLats
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// We skip the last point anyway because the API requires it to be repeated, and geo3d doesn't repeat it.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|polyLats
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|index
init|=
name|polyLats
operator|.
name|length
operator|-
literal|2
operator|-
name|i
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
name|WGS84
argument_list|,
name|fromDegrees
argument_list|(
name|polyLats
index|[
name|index
index|]
argument_list|)
argument_list|,
name|fromDegrees
argument_list|(
name|polyLons
index|[
name|index
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|descriptions
operator|.
name|add
argument_list|(
operator|new
name|GeoPolygonFactory
operator|.
name|PolygonDescription
argument_list|(
name|points
argument_list|,
name|holes
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|descriptions
return|;
block|}
comment|/**     * Convert a Polygon object into a GeoPolygon.     * This method uses     * @param polygon is the Polygon object.     * @return the GeoPolygon.     */
DECL|method|fromPolygon
specifier|private
specifier|static
name|GeoPolygon
name|fromPolygon
parameter_list|(
specifier|final
name|Polygon
name|polygon
parameter_list|)
block|{
comment|// First, assemble the "holes".  The geo3d convention is to use the same polygon sense on the inner ring as the
comment|// outer ring, so we process these recursively with reverseMe flipped.
specifier|final
name|Polygon
index|[]
name|theHoles
init|=
name|polygon
operator|.
name|getHoles
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|GeoPolygon
argument_list|>
name|holeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|theHoles
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Polygon
name|hole
range|:
name|theHoles
control|)
block|{
comment|//System.out.println("Hole: "+hole);
specifier|final
name|GeoPolygon
name|component
init|=
name|fromPolygon
argument_list|(
name|hole
argument_list|)
decl_stmt|;
if|if
condition|(
name|component
operator|!=
literal|null
condition|)
block|{
name|holeList
operator|.
name|add
argument_list|(
name|component
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Now do the polygon itself
specifier|final
name|double
index|[]
name|polyLats
init|=
name|polygon
operator|.
name|getPolyLats
argument_list|()
decl_stmt|;
specifier|final
name|double
index|[]
name|polyLons
init|=
name|polygon
operator|.
name|getPolyLons
argument_list|()
decl_stmt|;
comment|// I presume the arguments have already been checked
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|polyLats
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// We skip the last point anyway because the API requires it to be repeated, and geo3d doesn't repeat it.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|polyLats
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|index
init|=
name|polyLats
operator|.
name|length
operator|-
literal|2
operator|-
name|i
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
name|WGS84
argument_list|,
name|fromDegrees
argument_list|(
name|polyLats
index|[
name|index
index|]
argument_list|)
argument_list|,
name|fromDegrees
argument_list|(
name|polyLons
index|[
name|index
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//System.err.println(" building polygon with "+points.size()+" points...");
specifier|final
name|GeoPolygon
name|rval
init|=
name|GeoPolygonFactory
operator|.
name|makeGeoPolygon
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|points
argument_list|,
name|holeList
argument_list|)
decl_stmt|;
comment|//System.err.println(" ...done");
return|return
name|rval
return|;
block|}
comment|/**     * Creates a new Geo3DPoint field with the specified x,y,z.    *    * @throws IllegalArgumentException if the field name is null or latitude or longitude are out of bounds    */
DECL|method|Geo3DPoint
specifier|public
name|Geo3DPoint
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|z
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|fillFieldsData
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
expr_stmt|;
block|}
DECL|method|fillFieldsData
specifier|private
name|void
name|fillFieldsData
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|z
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
literal|12
index|]
decl_stmt|;
name|encodeDimension
argument_list|(
name|x
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|encodeDimension
argument_list|(
name|y
argument_list|,
name|bytes
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|encodeDimension
argument_list|(
name|z
argument_list|,
name|bytes
argument_list|,
literal|2
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|// public helper methods (e.g. for queries)
comment|/** Encode single dimension */
DECL|method|encodeDimension
specifier|public
specifier|static
name|void
name|encodeDimension
parameter_list|(
name|double
name|value
parameter_list|,
name|byte
name|bytes
index|[]
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|Geo3DUtil
operator|.
name|encodeValue
argument_list|(
name|value
argument_list|)
argument_list|,
name|bytes
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
comment|/** Decode single dimension */
DECL|method|decodeDimension
specifier|public
specifier|static
name|double
name|decodeDimension
parameter_list|(
name|byte
name|value
index|[]
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|Geo3DUtil
operator|.
name|decodeValue
argument_list|(
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|value
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns a query matching all points inside the provided shape.    *     * @param field field name. must not be {@code null}.    * @param shape Which {@link GeoShape} to match    */
DECL|method|newShapeQuery
specifier|public
specifier|static
name|Query
name|newShapeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|GeoShape
name|shape
parameter_list|)
block|{
return|return
operator|new
name|PointInGeo3DShapeQuery
argument_list|(
name|field
argument_list|,
name|shape
argument_list|)
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
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|BytesRef
name|bytes
init|=
operator|(
name|BytesRef
operator|)
name|fieldsData
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" x="
operator|+
name|decodeDimension
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" y="
operator|+
name|decodeDimension
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
operator|+
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" z="
operator|+
name|decodeDimension
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
operator|+
literal|2
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
