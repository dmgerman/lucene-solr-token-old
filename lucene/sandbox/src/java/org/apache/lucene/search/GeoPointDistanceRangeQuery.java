begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|List
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
name|util
operator|.
name|GeoProjectionUtils
import|;
end_import
begin_comment
comment|/** Implements a point distance range query on a GeoPoint field. This is based on  * {@code org.apache.lucene.search.GeoPointDistanceQuery} and is implemented using a  * {@code org.apache.lucene.search.BooleanClause.MUST_NOT} clause to exclude any points that fall within  * minRadius from the provided point.  *  *    @lucene.experimental  */
end_comment
begin_class
DECL|class|GeoPointDistanceRangeQuery
specifier|public
specifier|final
class|class
name|GeoPointDistanceRangeQuery
extends|extends
name|GeoPointDistanceQuery
block|{
DECL|field|minRadius
specifier|protected
specifier|final
name|double
name|minRadius
decl_stmt|;
DECL|method|GeoPointDistanceRangeQuery
specifier|public
name|GeoPointDistanceRangeQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|minRadius
parameter_list|,
specifier|final
name|double
name|maxRadius
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|maxRadius
argument_list|)
expr_stmt|;
name|this
operator|.
name|minRadius
operator|=
name|minRadius
expr_stmt|;
block|}
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
name|Query
name|q
init|=
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|minRadius
operator|==
literal|0.0
condition|)
block|{
return|return
name|q
return|;
block|}
comment|// add an exclusion query
name|BooleanQuery
operator|.
name|Builder
name|bqb
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
comment|// create a new exclusion query
name|GeoPointDistanceQuery
name|exclude
init|=
operator|new
name|GeoPointDistanceQuery
argument_list|(
name|field
argument_list|,
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|minRadius
argument_list|)
decl_stmt|;
comment|// full map search
if|if
condition|(
name|radius
operator|>=
name|GeoProjectionUtils
operator|.
name|SEMIMINOR_AXIS
condition|)
block|{
name|bqb
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|GeoPointInBBoxQuery
argument_list|(
name|this
operator|.
name|field
argument_list|,
operator|-
literal|180.0
argument_list|,
operator|-
literal|90.0
argument_list|,
literal|180.0
argument_list|,
literal|90.0
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bqb
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|q
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bqb
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|exclude
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|bqb
operator|.
name|build
argument_list|()
return|;
block|}
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
name|this
operator|.
name|field
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
name|this
operator|.
name|field
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
return|return
name|sb
operator|.
name|append
argument_list|(
literal|" Center: ["
argument_list|)
operator|.
name|append
argument_list|(
name|centerLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|centerLat
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|append
argument_list|(
literal|" From Distance: "
argument_list|)
operator|.
name|append
argument_list|(
name|minRadius
argument_list|)
operator|.
name|append
argument_list|(
literal|" m"
argument_list|)
operator|.
name|append
argument_list|(
literal|" To Distance: "
argument_list|)
operator|.
name|append
argument_list|(
name|radius
argument_list|)
operator|.
name|append
argument_list|(
literal|" m"
argument_list|)
operator|.
name|append
argument_list|(
literal|" Lower Left: ["
argument_list|)
operator|.
name|append
argument_list|(
name|minLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|minLat
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|append
argument_list|(
literal|" Upper Right: ["
argument_list|)
operator|.
name|append
argument_list|(
name|maxLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|maxLat
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getMinRadiusMeters
specifier|public
name|double
name|getMinRadiusMeters
parameter_list|()
block|{
return|return
name|this
operator|.
name|minRadius
return|;
block|}
DECL|method|getMaxRadiusMeters
specifier|public
name|double
name|getMaxRadiusMeters
parameter_list|()
block|{
return|return
name|this
operator|.
name|radius
return|;
block|}
block|}
end_class
end_unit
