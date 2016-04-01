begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/**   * Represents a closed polygon on the earth's surface.  * @lucene.experimental   */
end_comment
begin_class
DECL|class|Polygon
specifier|public
specifier|final
class|class
name|Polygon
block|{
DECL|field|polyLats
specifier|private
specifier|final
name|double
index|[]
name|polyLats
decl_stmt|;
DECL|field|polyLons
specifier|private
specifier|final
name|double
index|[]
name|polyLons
decl_stmt|;
DECL|field|holes
specifier|private
specifier|final
name|Polygon
index|[]
name|holes
decl_stmt|;
comment|/** minimum latitude of this polygon's bounding box area */
DECL|field|minLat
specifier|public
specifier|final
name|double
name|minLat
decl_stmt|;
comment|/** maximum latitude of this polygon's bounding box area */
DECL|field|maxLat
specifier|public
specifier|final
name|double
name|maxLat
decl_stmt|;
comment|/** minimum longitude of this polygon's bounding box area */
DECL|field|minLon
specifier|public
specifier|final
name|double
name|minLon
decl_stmt|;
comment|/** maximum longitude of this polygon's bounding box area */
DECL|field|maxLon
specifier|public
specifier|final
name|double
name|maxLon
decl_stmt|;
comment|// TODO: we could also compute the maximal inner bounding box, to make relations faster to compute?
comment|/**     * Creates a new Polygon from the supplied latitude/longitude array, and optionally any holes.    */
DECL|method|Polygon
specifier|public
name|Polygon
parameter_list|(
name|double
index|[]
name|polyLats
parameter_list|,
name|double
index|[]
name|polyLons
parameter_list|,
name|Polygon
modifier|...
name|holes
parameter_list|)
block|{
if|if
condition|(
name|polyLats
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLats must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLons
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLons must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|holes
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"holes must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
operator|.
name|length
operator|!=
name|polyLons
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLats and polyLons must be equal length"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
operator|.
name|length
operator|!=
name|polyLons
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLats and polyLons must be equal length"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
operator|.
name|length
operator|<
literal|4
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"at least 4 polygon points required"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
index|[
literal|0
index|]
operator|!=
name|polyLats
index|[
name|polyLats
operator|.
name|length
operator|-
literal|1
index|]
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"first and last points of the polygon must be the same (it must close itself): polyLats[0]="
operator|+
name|polyLats
index|[
literal|0
index|]
operator|+
literal|" polyLats["
operator|+
operator|(
name|polyLats
operator|.
name|length
operator|-
literal|1
operator|)
operator|+
literal|"]="
operator|+
name|polyLats
index|[
name|polyLats
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLons
index|[
literal|0
index|]
operator|!=
name|polyLons
index|[
name|polyLons
operator|.
name|length
operator|-
literal|1
index|]
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"first and last points of the polygon must be the same (it must close itself): polyLons[0]="
operator|+
name|polyLons
index|[
literal|0
index|]
operator|+
literal|" polyLons["
operator|+
operator|(
name|polyLons
operator|.
name|length
operator|-
literal|1
operator|)
operator|+
literal|"]="
operator|+
name|polyLons
index|[
name|polyLons
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
throw|;
block|}
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
condition|;
name|i
operator|++
control|)
block|{
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|holes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Polygon
name|inner
init|=
name|holes
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|inner
operator|.
name|holes
operator|.
name|length
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"holes may not contain holes: polygons may not nest."
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|polyLats
operator|=
name|polyLats
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|polyLons
operator|=
name|polyLons
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|holes
operator|=
name|holes
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// compute bounding box
name|double
name|minLat
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxLat
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|double
name|minLon
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxLon
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
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
name|polyLats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|minLat
operator|=
name|Math
operator|.
name|min
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|,
name|minLat
argument_list|)
expr_stmt|;
name|maxLat
operator|=
name|Math
operator|.
name|max
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|,
name|maxLat
argument_list|)
expr_stmt|;
name|minLon
operator|=
name|Math
operator|.
name|min
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|,
name|minLon
argument_list|)
expr_stmt|;
name|maxLon
operator|=
name|Math
operator|.
name|max
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|,
name|maxLon
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|minLat
operator|=
name|minLat
expr_stmt|;
name|this
operator|.
name|maxLat
operator|=
name|maxLat
expr_stmt|;
name|this
operator|.
name|minLon
operator|=
name|minLon
expr_stmt|;
name|this
operator|.
name|maxLon
operator|=
name|maxLon
expr_stmt|;
block|}
comment|/** Returns true if the point is contained within this polygon */
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
comment|// check bounding box
if|if
condition|(
name|latitude
argument_list|<
name|minLat
operator|||
name|latitude
argument_list|>
name|maxLat
operator|||
name|longitude
argument_list|<
name|minLon
operator|||
name|longitude
argument_list|>
name|maxLon
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|/*       * simple even-odd point in polygon computation      *    1.  Determine if point is contained in the longitudinal range      *    2.  Determine whether point crosses the edge by computing the latitudinal delta      *        between the end-point of a parallel vector (originating at the point) and the      *        y-component of the edge sink      *      * NOTE: Requires polygon point (x,y) order either clockwise or counter-clockwise      */
name|boolean
name|inPoly
init|=
literal|false
decl_stmt|;
comment|/*      * Note: This is using a euclidean coordinate system which could result in      * upwards of 110KM error at the equator.      * TODO convert coordinates to cylindrical projection (e.g. mercator)      */
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|polyLats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|polyLons
index|[
name|i
index|]
operator|<=
name|longitude
operator|&&
name|polyLons
index|[
name|i
operator|-
literal|1
index|]
operator|>=
name|longitude
operator|||
name|polyLons
index|[
name|i
operator|-
literal|1
index|]
operator|<=
name|longitude
operator|&&
name|polyLons
index|[
name|i
index|]
operator|>=
name|longitude
condition|)
block|{
if|if
condition|(
name|polyLats
index|[
name|i
index|]
operator|+
operator|(
name|longitude
operator|-
name|polyLons
index|[
name|i
index|]
operator|)
operator|/
operator|(
name|polyLons
index|[
name|i
operator|-
literal|1
index|]
operator|-
name|polyLons
index|[
name|i
index|]
operator|)
operator|*
operator|(
name|polyLats
index|[
name|i
operator|-
literal|1
index|]
operator|-
name|polyLats
index|[
name|i
index|]
operator|)
operator|<=
name|latitude
condition|)
block|{
name|inPoly
operator|=
operator|!
name|inPoly
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|inPoly
condition|)
block|{
for|for
control|(
name|Polygon
name|hole
range|:
name|holes
control|)
block|{
if|if
condition|(
name|hole
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Computes whether a rectangle is within a polygon (shared boundaries not allowed)    */
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|)
block|{
comment|// check if rectangle crosses poly (to handle concave/pacman polys), then check that all 4 corners
comment|// are contained
name|boolean
name|contains
init|=
name|crosses
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
operator|==
literal|false
operator|&&
name|contains
argument_list|(
name|minLat
argument_list|,
name|minLon
argument_list|)
operator|&&
name|contains
argument_list|(
name|minLat
argument_list|,
name|maxLon
argument_list|)
operator|&&
name|contains
argument_list|(
name|maxLat
argument_list|,
name|maxLon
argument_list|)
operator|&&
name|contains
argument_list|(
name|maxLat
argument_list|,
name|minLon
argument_list|)
decl_stmt|;
if|if
condition|(
name|contains
condition|)
block|{
comment|// if we intersect with any hole, game over
for|for
control|(
name|Polygon
name|hole
range|:
name|holes
control|)
block|{
if|if
condition|(
name|hole
operator|.
name|crosses
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
operator|||
name|hole
operator|.
name|contains
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Convenience method for accurately computing whether a rectangle crosses a poly.    */
DECL|method|crosses
specifier|public
name|boolean
name|crosses
parameter_list|(
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
specifier|final
name|double
name|minLon
parameter_list|,
specifier|final
name|double
name|maxLon
parameter_list|)
block|{
comment|// if the bounding boxes are disjoint then the shape does not cross
if|if
condition|(
name|maxLon
argument_list|<
name|this
operator|.
name|minLon
operator|||
name|minLon
argument_list|>
name|this
operator|.
name|maxLon
operator|||
name|maxLat
argument_list|<
name|this
operator|.
name|minLat
operator|||
name|minLat
argument_list|>
name|this
operator|.
name|maxLat
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// if the rectangle fully encloses us, we cross.
if|if
condition|(
name|minLat
operator|<=
name|this
operator|.
name|minLat
operator|&&
name|maxLat
operator|>=
name|this
operator|.
name|maxLat
operator|&&
name|minLon
operator|<=
name|this
operator|.
name|minLon
operator|&&
name|maxLon
operator|>=
name|this
operator|.
name|maxLon
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// if we cross any hole, we cross
for|for
control|(
name|Polygon
name|hole
range|:
name|holes
control|)
block|{
if|if
condition|(
name|hole
operator|.
name|crosses
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/*      * Accurately compute (within restrictions of cartesian decimal degrees) whether a rectangle crosses a polygon      */
specifier|final
name|double
index|[]
index|[]
name|bbox
init|=
operator|new
name|double
index|[]
index|[]
block|{
block|{
name|minLon
block|,
name|minLat
block|}
block|,
block|{
name|maxLon
block|,
name|minLat
block|}
block|,
block|{
name|maxLon
block|,
name|maxLat
block|}
block|,
block|{
name|minLon
block|,
name|maxLat
block|}
block|,
block|{
name|minLon
block|,
name|minLat
block|}
block|}
decl_stmt|;
specifier|final
name|int
name|polyLength
init|=
name|polyLons
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|double
name|d
decl_stmt|,
name|s
decl_stmt|,
name|t
decl_stmt|,
name|a1
decl_stmt|,
name|b1
decl_stmt|,
name|c1
decl_stmt|,
name|a2
decl_stmt|,
name|b2
decl_stmt|,
name|c2
decl_stmt|;
name|double
name|x00
decl_stmt|,
name|y00
decl_stmt|,
name|x01
decl_stmt|,
name|y01
decl_stmt|,
name|x10
decl_stmt|,
name|y10
decl_stmt|,
name|x11
decl_stmt|,
name|y11
decl_stmt|;
comment|// computes the intersection point between each bbox edge and the polygon edge
for|for
control|(
name|short
name|b
init|=
literal|0
init|;
name|b
operator|<
literal|4
condition|;
operator|++
name|b
control|)
block|{
name|a1
operator|=
name|bbox
index|[
name|b
operator|+
literal|1
index|]
index|[
literal|1
index|]
operator|-
name|bbox
index|[
name|b
index|]
index|[
literal|1
index|]
expr_stmt|;
name|b1
operator|=
name|bbox
index|[
name|b
index|]
index|[
literal|0
index|]
operator|-
name|bbox
index|[
name|b
operator|+
literal|1
index|]
index|[
literal|0
index|]
expr_stmt|;
name|c1
operator|=
name|a1
operator|*
name|bbox
index|[
name|b
operator|+
literal|1
index|]
index|[
literal|0
index|]
operator|+
name|b1
operator|*
name|bbox
index|[
name|b
operator|+
literal|1
index|]
index|[
literal|1
index|]
expr_stmt|;
for|for
control|(
name|int
name|p
init|=
literal|0
init|;
name|p
operator|<
name|polyLength
condition|;
operator|++
name|p
control|)
block|{
name|a2
operator|=
name|polyLats
index|[
name|p
operator|+
literal|1
index|]
operator|-
name|polyLats
index|[
name|p
index|]
expr_stmt|;
name|b2
operator|=
name|polyLons
index|[
name|p
index|]
operator|-
name|polyLons
index|[
name|p
operator|+
literal|1
index|]
expr_stmt|;
comment|// compute determinant
name|d
operator|=
name|a1
operator|*
name|b2
operator|-
name|a2
operator|*
name|b1
expr_stmt|;
if|if
condition|(
name|d
operator|!=
literal|0
condition|)
block|{
comment|// lines are not parallel, check intersecting points
name|c2
operator|=
name|a2
operator|*
name|polyLons
index|[
name|p
operator|+
literal|1
index|]
operator|+
name|b2
operator|*
name|polyLats
index|[
name|p
operator|+
literal|1
index|]
expr_stmt|;
name|s
operator|=
operator|(
literal|1
operator|/
name|d
operator|)
operator|*
operator|(
name|b2
operator|*
name|c1
operator|-
name|b1
operator|*
name|c2
operator|)
expr_stmt|;
name|t
operator|=
operator|(
literal|1
operator|/
name|d
operator|)
operator|*
operator|(
name|a1
operator|*
name|c2
operator|-
name|a2
operator|*
name|c1
operator|)
expr_stmt|;
name|x00
operator|=
name|Math
operator|.
name|min
argument_list|(
name|bbox
index|[
name|b
index|]
index|[
literal|0
index|]
argument_list|,
name|bbox
index|[
name|b
operator|+
literal|1
index|]
index|[
literal|0
index|]
argument_list|)
operator|-
name|GeoEncodingUtils
operator|.
name|TOLERANCE
expr_stmt|;
name|x01
operator|=
name|Math
operator|.
name|max
argument_list|(
name|bbox
index|[
name|b
index|]
index|[
literal|0
index|]
argument_list|,
name|bbox
index|[
name|b
operator|+
literal|1
index|]
index|[
literal|0
index|]
argument_list|)
operator|+
name|GeoEncodingUtils
operator|.
name|TOLERANCE
expr_stmt|;
name|y00
operator|=
name|Math
operator|.
name|min
argument_list|(
name|bbox
index|[
name|b
index|]
index|[
literal|1
index|]
argument_list|,
name|bbox
index|[
name|b
operator|+
literal|1
index|]
index|[
literal|1
index|]
argument_list|)
operator|-
name|GeoEncodingUtils
operator|.
name|TOLERANCE
expr_stmt|;
name|y01
operator|=
name|Math
operator|.
name|max
argument_list|(
name|bbox
index|[
name|b
index|]
index|[
literal|1
index|]
argument_list|,
name|bbox
index|[
name|b
operator|+
literal|1
index|]
index|[
literal|1
index|]
argument_list|)
operator|+
name|GeoEncodingUtils
operator|.
name|TOLERANCE
expr_stmt|;
name|x10
operator|=
name|Math
operator|.
name|min
argument_list|(
name|polyLons
index|[
name|p
index|]
argument_list|,
name|polyLons
index|[
name|p
operator|+
literal|1
index|]
argument_list|)
operator|-
name|GeoEncodingUtils
operator|.
name|TOLERANCE
expr_stmt|;
name|x11
operator|=
name|Math
operator|.
name|max
argument_list|(
name|polyLons
index|[
name|p
index|]
argument_list|,
name|polyLons
index|[
name|p
operator|+
literal|1
index|]
argument_list|)
operator|+
name|GeoEncodingUtils
operator|.
name|TOLERANCE
expr_stmt|;
name|y10
operator|=
name|Math
operator|.
name|min
argument_list|(
name|polyLats
index|[
name|p
index|]
argument_list|,
name|polyLats
index|[
name|p
operator|+
literal|1
index|]
argument_list|)
operator|-
name|GeoEncodingUtils
operator|.
name|TOLERANCE
expr_stmt|;
name|y11
operator|=
name|Math
operator|.
name|max
argument_list|(
name|polyLats
index|[
name|p
index|]
argument_list|,
name|polyLats
index|[
name|p
operator|+
literal|1
index|]
argument_list|)
operator|+
name|GeoEncodingUtils
operator|.
name|TOLERANCE
expr_stmt|;
comment|// check whether the intersection point is touching one of the line segments
name|boolean
name|touching
init|=
operator|(
operator|(
name|x00
operator|==
name|s
operator|&&
name|y00
operator|==
name|t
operator|)
operator|||
operator|(
name|x01
operator|==
name|s
operator|&&
name|y01
operator|==
name|t
operator|)
operator|)
operator|||
operator|(
operator|(
name|x10
operator|==
name|s
operator|&&
name|y10
operator|==
name|t
operator|)
operator|||
operator|(
name|x11
operator|==
name|s
operator|&&
name|y11
operator|==
name|t
operator|)
operator|)
decl_stmt|;
comment|// if line segments are not touching and the intersection point is within the range of either segment
if|if
condition|(
operator|!
operator|(
name|touching
operator|||
name|x00
operator|>
name|s
operator|||
name|x01
argument_list|<
name|s
operator|||
name|y00
argument_list|>
name|t
operator|||
name|y01
argument_list|<
name|t
operator|||
name|x10
argument_list|>
name|s
operator|||
name|x11
argument_list|<
name|s
operator|||
name|y10
argument_list|>
name|t
operator|||
name|y11
operator|<
name|t
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
comment|// for each poly edge
block|}
comment|// for each bbox edge
return|return
literal|false
return|;
block|}
comment|/** Returns a copy of the internal latitude array */
DECL|method|getPolyLats
specifier|public
name|double
index|[]
name|getPolyLats
parameter_list|()
block|{
return|return
name|polyLats
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/** Returns a copy of the internal longitude array */
DECL|method|getPolyLons
specifier|public
name|double
index|[]
name|getPolyLons
parameter_list|()
block|{
return|return
name|polyLons
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/** Returns a copy of the internal holes array */
DECL|method|getHoles
specifier|public
name|Polygon
index|[]
name|getHoles
parameter_list|()
block|{
return|return
name|holes
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/** Returns the bounding box over an array of polygons */
DECL|method|getBoundingBox
specifier|public
specifier|static
name|GeoRect
name|getBoundingBox
parameter_list|(
name|Polygon
index|[]
name|polygons
parameter_list|)
block|{
comment|// compute bounding box
name|double
name|minLat
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxLat
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|double
name|minLon
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxLon
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
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
name|polygons
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|minLat
operator|=
name|Math
operator|.
name|min
argument_list|(
name|polygons
index|[
name|i
index|]
operator|.
name|minLat
argument_list|,
name|minLat
argument_list|)
expr_stmt|;
name|maxLat
operator|=
name|Math
operator|.
name|max
argument_list|(
name|polygons
index|[
name|i
index|]
operator|.
name|maxLat
argument_list|,
name|maxLat
argument_list|)
expr_stmt|;
name|minLon
operator|=
name|Math
operator|.
name|min
argument_list|(
name|polygons
index|[
name|i
index|]
operator|.
name|minLon
argument_list|,
name|minLon
argument_list|)
expr_stmt|;
name|maxLon
operator|=
name|Math
operator|.
name|max
argument_list|(
name|polygons
index|[
name|i
index|]
operator|.
name|maxLon
argument_list|,
name|maxLon
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GeoRect
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
return|;
block|}
comment|/** Helper for multipolygon logic: returns true if any of the supplied polygons contain the point */
DECL|method|contains
specifier|public
specifier|static
name|boolean
name|contains
parameter_list|(
name|Polygon
index|[]
name|polygons
parameter_list|,
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|)
block|{
for|for
control|(
name|Polygon
name|polygon
range|:
name|polygons
control|)
block|{
if|if
condition|(
name|polygon
operator|.
name|contains
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** Helper for multipolygon logic: returns true if any of the supplied polygons contain the rectangle */
DECL|method|contains
specifier|public
specifier|static
name|boolean
name|contains
parameter_list|(
name|Polygon
index|[]
name|polygons
parameter_list|,
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|)
block|{
for|for
control|(
name|Polygon
name|polygon
range|:
name|polygons
control|)
block|{
if|if
condition|(
name|polygon
operator|.
name|contains
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** Helper for multipolygon logic: returns true if any of the supplied polygons crosses the rectangle */
DECL|method|crosses
specifier|public
specifier|static
name|boolean
name|crosses
parameter_list|(
name|Polygon
index|[]
name|polygons
parameter_list|,
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|)
block|{
for|for
control|(
name|Polygon
name|polygon
range|:
name|polygons
control|)
block|{
if|if
condition|(
name|polygon
operator|.
name|crosses
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|holes
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|polyLats
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|polyLons
argument_list|)
expr_stmt|;
return|return
name|result
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Polygon
name|other
init|=
operator|(
name|Polygon
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|holes
argument_list|,
name|other
operator|.
name|holes
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|polyLats
argument_list|,
name|other
operator|.
name|polyLats
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|polyLons
argument_list|,
name|other
operator|.
name|polyLons
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|polyLats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|"] "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|holes
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", holes="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|holes
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
