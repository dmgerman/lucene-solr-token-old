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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|BaseGeoPointTestCase
import|;
end_import
begin_comment
comment|/**  * random testing for GeoPoint query logic (with deprecated numeric encoding)  * @deprecated remove this when TermEncoding.NUMERIC is removed  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|TestLegacyGeoPointQuery
specifier|public
class|class
name|TestLegacyGeoPointQuery
extends|extends
name|BaseGeoPointTestCase
block|{
annotation|@
name|Override
DECL|method|quantizeLat
specifier|protected
name|double
name|quantizeLat
parameter_list|(
name|double
name|lat
parameter_list|)
block|{
return|return
name|GeoEncodingUtils
operator|.
name|mortonUnhashLat
argument_list|(
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
name|lat
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|quantizeLon
specifier|protected
name|double
name|quantizeLon
parameter_list|(
name|double
name|lon
parameter_list|)
block|{
return|return
name|GeoEncodingUtils
operator|.
name|mortonUnhashLon
argument_list|(
name|GeoEncodingUtils
operator|.
name|mortonHash
argument_list|(
literal|0
argument_list|,
name|lon
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addPointToDoc
specifier|protected
name|void
name|addPointToDoc
parameter_list|(
name|String
name|field
parameter_list|,
name|Document
name|doc
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|GeoPointField
argument_list|(
name|field
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|GeoPointField
operator|.
name|NUMERIC_TYPE_NOT_STORED
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newRectQuery
specifier|protected
name|Query
name|newRectQuery
parameter_list|(
name|String
name|field
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
return|return
operator|new
name|GeoPointInBBoxQuery
argument_list|(
name|field
argument_list|,
name|TermEncoding
operator|.
name|NUMERIC
argument_list|,
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
annotation|@
name|Override
DECL|method|newDistanceQuery
specifier|protected
name|Query
name|newDistanceQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|radiusMeters
parameter_list|)
block|{
return|return
operator|new
name|GeoPointDistanceQuery
argument_list|(
name|field
argument_list|,
name|TermEncoding
operator|.
name|NUMERIC
argument_list|,
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radiusMeters
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newPolygonQuery
specifier|protected
name|Query
name|newPolygonQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
index|[]
name|lats
parameter_list|,
name|double
index|[]
name|lons
parameter_list|)
block|{
return|return
operator|new
name|GeoPointInPolygonQuery
argument_list|(
name|field
argument_list|,
name|TermEncoding
operator|.
name|NUMERIC
argument_list|,
name|lats
argument_list|,
name|lons
argument_list|)
return|;
block|}
comment|// legacy encoding is too slow somehow for this random test, its not up to the task.
annotation|@
name|Override
DECL|method|testRandomDistance
specifier|public
name|void
name|testRandomDistance
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"legacy encoding is too slow/hangs on this test"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testRandomDistanceHuge
specifier|public
name|void
name|testRandomDistanceHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"legacy encoding is too slow/hangs on this test"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testSamePointManyTimes
specifier|public
name|void
name|testSamePointManyTimes
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"legacy encoding goes OOM on this test"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
