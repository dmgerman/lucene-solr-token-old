begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.geopoint.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
operator|.
name|search
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
name|IndexReader
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
name|spatial
operator|.
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
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
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
operator|.
name|TermEncoding
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
name|util
operator|.
name|GeoEncodingUtils
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
name|util
operator|.
name|GeoRect
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
name|util
operator|.
name|Polygon
import|;
end_import
begin_comment
comment|/** Implements a simple point in polygon query on a GeoPoint field. This is based on  * {@code GeoPointInBBoxQueryImpl} and is implemented using a  * three phase approach. First, like {@code GeoPointInBBoxQueryImpl}  * candidate terms are queried using a numeric range based on the morton codes  * of the min and max lat/lon pairs. Terms passing this initial filter are passed  * to a secondary filter that verifies whether the decoded lat/lon point falls within  * (or on the boundary) of the bounding box query. Finally, the remaining candidate  * term is passed to the final point in polygon check. All value comparisons are subject  * to the same precision tolerance defined in {@value GeoEncodingUtils#TOLERANCE}  *  *<p>NOTES:  *    1.  The polygon coordinates need to be in either clockwise or counter-clockwise order.  *    2.  The polygon must not be self-crossing, otherwise the query may result in unexpected behavior  *    3.  All latitude/longitude values must be in decimal degrees.  *    4.  Complex computational geometry (e.g., dateline wrapping) is not supported  *    5.  For more advanced GeoSpatial indexing and query operations see spatial module  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoPointInPolygonQuery
specifier|public
specifier|final
class|class
name|GeoPointInPolygonQuery
extends|extends
name|GeoPointInBBoxQuery
block|{
comment|/** array of polygons being queried */
DECL|field|polygons
specifier|final
name|Polygon
index|[]
name|polygons
decl_stmt|;
comment|/**     * Constructs a new GeoPolygonQuery that will match encoded {@link org.apache.lucene.spatial.geopoint.document.GeoPointField} terms    * that fall within or on the boundary of the polygons defined by the input parameters.     */
DECL|method|GeoPointInPolygonQuery
specifier|public
name|GeoPointInPolygonQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|polygons
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new GeoPolygonQuery that will match encoded {@link org.apache.lucene.spatial.geopoint.document.GeoPointField} terms    * that fall within or on the boundary of the polygon defined by the input parameters.    * @deprecated Use {@link #GeoPointInPolygonQuery(String, Polygon[])}.    */
annotation|@
name|Deprecated
DECL|method|GeoPointInPolygonQuery
specifier|public
name|GeoPointInPolygonQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
index|[]
name|polyLats
parameter_list|,
specifier|final
name|double
index|[]
name|polyLons
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|polyLats
argument_list|,
name|polyLons
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new GeoPolygonQuery that will match encoded {@link org.apache.lucene.spatial.geopoint.document.GeoPointField} terms    * that fall within or on the boundary of the polygon defined by the input parameters.    * @deprecated Use {@link #GeoPointInPolygonQuery(String, GeoPointField.TermEncoding, Polygon[])} instead.    */
annotation|@
name|Deprecated
DECL|method|GeoPointInPolygonQuery
specifier|public
name|GeoPointInPolygonQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|TermEncoding
name|termEncoding
parameter_list|,
specifier|final
name|double
index|[]
name|polyLats
parameter_list|,
specifier|final
name|double
index|[]
name|polyLons
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|termEncoding
argument_list|,
operator|new
name|Polygon
argument_list|(
name|polyLats
argument_list|,
name|polyLons
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * Constructs a new GeoPolygonQuery that will match encoded {@link org.apache.lucene.spatial.geopoint.document.GeoPointField} terms    * that fall within or on the boundary of the polygon defined by the input parameters.     */
DECL|method|GeoPointInPolygonQuery
specifier|public
name|GeoPointInPolygonQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|TermEncoding
name|termEncoding
parameter_list|,
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|termEncoding
argument_list|,
name|Polygon
operator|.
name|getBoundingBox
argument_list|(
name|polygons
argument_list|)
argument_list|,
name|polygons
argument_list|)
expr_stmt|;
block|}
comment|// internal constructor
DECL|method|GeoPointInPolygonQuery
specifier|private
name|GeoPointInPolygonQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|TermEncoding
name|termEncoding
parameter_list|,
name|GeoRect
name|boundingBox
parameter_list|,
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|termEncoding
argument_list|,
name|boundingBox
operator|.
name|minLat
argument_list|,
name|boundingBox
operator|.
name|maxLat
argument_list|,
name|boundingBox
operator|.
name|minLon
argument_list|,
name|boundingBox
operator|.
name|maxLon
argument_list|)
expr_stmt|;
name|this
operator|.
name|polygons
operator|=
name|polygons
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
comment|/** throw exception if trying to change rewrite method */
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|GeoPointInPolygonQueryImpl
argument_list|(
name|field
argument_list|,
name|termEncoding
argument_list|,
name|this
argument_list|,
name|this
operator|.
name|minLat
argument_list|,
name|this
operator|.
name|maxLat
argument_list|,
name|this
operator|.
name|minLon
argument_list|,
name|this
operator|.
name|maxLon
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
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
name|polygons
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
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
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
name|GeoPointInPolygonQuery
name|other
init|=
operator|(
name|GeoPointInPolygonQuery
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
name|polygons
argument_list|,
name|other
operator|.
name|polygons
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
comment|/** print out this polygon query */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
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
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" field="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getField
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" Polygon: "
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
name|polygons
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * API utility method for returning copy of the polygon array    */
DECL|method|getPolygons
specifier|public
name|Polygon
index|[]
name|getPolygons
parameter_list|()
block|{
return|return
name|polygons
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
end_class
end_unit
