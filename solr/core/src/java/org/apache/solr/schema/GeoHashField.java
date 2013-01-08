begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|LiteralValueSource
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
name|StorableField
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
name|search
operator|.
name|SortField
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
name|io
operator|.
name|ParseUtils
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
name|exception
operator|.
name|InvalidShapeException
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
name|io
operator|.
name|GeohashUtils
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
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|TextResponseWriter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|QParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrConstantScoreQuery
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SpatialOptions
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|ValueSourceRangeFilter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|distance
operator|.
name|GeohashHaversineFunction
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * This is a class that represents a<a  * href="http://en.wikipedia.org/wiki/Geohash">Geohash</a> field. The field is  * provided as a lat/lon pair and is internally represented as a string.  *  * @see com.spatial4j.core.io.ParseUtils#parseLatitudeLongitude(double[], String)  */
end_comment
begin_class
DECL|class|GeoHashField
specifier|public
class|class
name|GeoHashField
extends|extends
name|FieldType
implements|implements
name|SpatialQueryable
block|{
DECL|field|ctx
specifier|private
specifier|final
name|SpatialContext
name|ctx
init|=
name|SpatialContext
operator|.
name|GEO
decl_stmt|;
annotation|@
name|Override
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|top
parameter_list|)
block|{
return|return
name|getStringSort
argument_list|(
name|field
argument_list|,
name|top
argument_list|)
return|;
block|}
comment|//QUESTION: Should we do a fast and crude one?  Or actually check distances
comment|//Fast and crude could use EdgeNGrams, but that would require a different
comment|//encoding.  Plus there are issues around the Equator/Prime Meridian
annotation|@
name|Override
DECL|method|createSpatialQuery
specifier|public
name|Query
name|createSpatialQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SpatialOptions
name|options
parameter_list|)
block|{
name|double
index|[]
name|point
init|=
operator|new
name|double
index|[
literal|0
index|]
decl_stmt|;
try|try
block|{
name|point
operator|=
name|ParseUtils
operator|.
name|parsePointDouble
argument_list|(
literal|null
argument_list|,
name|options
operator|.
name|pointStr
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidShapeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|String
name|geohash
init|=
name|GeohashUtils
operator|.
name|encodeLatLon
argument_list|(
name|point
index|[
literal|0
index|]
argument_list|,
name|point
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
comment|//TODO: optimize this
return|return
operator|new
name|SolrConstantScoreQuery
argument_list|(
operator|new
name|ValueSourceRangeFilter
argument_list|(
operator|new
name|GeohashHaversineFunction
argument_list|(
name|getValueSource
argument_list|(
name|options
operator|.
name|field
argument_list|,
name|parser
argument_list|)
argument_list|,
operator|new
name|LiteralValueSource
argument_list|(
name|geohash
argument_list|)
argument_list|,
name|options
operator|.
name|radius
argument_list|)
argument_list|,
literal|"0"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|options
operator|.
name|distance
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|StorableField
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|toExternal
argument_list|(
name|f
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|StorableField
name|f
parameter_list|)
block|{
name|Point
name|p
init|=
name|GeohashUtils
operator|.
name|decode
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
return|return
name|p
operator|.
name|getY
argument_list|()
operator|+
literal|","
operator|+
name|p
operator|.
name|getX
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
comment|// validate that the string is of the form
comment|// latitude, longitude
name|double
index|[]
name|latLon
init|=
operator|new
name|double
index|[
literal|0
index|]
decl_stmt|;
try|try
block|{
name|latLon
operator|=
name|ParseUtils
operator|.
name|parseLatitudeLongitude
argument_list|(
literal|null
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidShapeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|GeohashUtils
operator|.
name|encodeLatLon
argument_list|(
name|latLon
index|[
literal|0
index|]
argument_list|,
name|latLon
index|[
literal|1
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|QParser
name|parser
parameter_list|)
block|{
name|field
operator|.
name|checkFieldCacheSource
argument_list|(
name|parser
argument_list|)
expr_stmt|;
return|return
operator|new
name|StrFieldSource
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
block|}
end_class
end_unit
