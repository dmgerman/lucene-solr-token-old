begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.vector
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|vector
package|;
end_package
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
name|exception
operator|.
name|UnsupportedSpatialOperation
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
name|query
operator|.
name|SpatialArgs
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
name|query
operator|.
name|SpatialOperation
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
name|Circle
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
name|IndexableField
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
name|FunctionQuery
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
name|search
operator|.
name|*
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
name|FieldCache
operator|.
name|DoubleParser
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
name|SpatialStrategy
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
name|CachingDoubleValueSource
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
name|NumericFieldInfo
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
name|ValueSourceFilter
import|;
end_import
begin_class
DECL|class|TwoDoublesStrategy
specifier|public
class|class
name|TwoDoublesStrategy
extends|extends
name|SpatialStrategy
argument_list|<
name|TwoDoublesFieldInfo
argument_list|>
block|{
DECL|field|finfo
specifier|private
specifier|final
name|NumericFieldInfo
name|finfo
decl_stmt|;
DECL|field|parser
specifier|private
specifier|final
name|DoubleParser
name|parser
decl_stmt|;
DECL|method|TwoDoublesStrategy
specifier|public
name|TwoDoublesStrategy
parameter_list|(
name|SpatialContext
name|ctx
parameter_list|,
name|NumericFieldInfo
name|finfo
parameter_list|,
name|DoubleParser
name|parser
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|finfo
operator|=
name|finfo
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isPolyField
specifier|public
name|boolean
name|isPolyField
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|createFields
specifier|public
name|IndexableField
index|[]
name|createFields
parameter_list|(
name|TwoDoublesFieldInfo
name|fieldInfo
parameter_list|,
name|Shape
name|shape
parameter_list|,
name|boolean
name|index
parameter_list|,
name|boolean
name|store
parameter_list|)
block|{
if|if
condition|(
name|shape
operator|instanceof
name|Point
condition|)
block|{
name|Point
name|point
init|=
operator|(
name|Point
operator|)
name|shape
decl_stmt|;
name|IndexableField
index|[]
name|f
init|=
operator|new
name|IndexableField
index|[
operator|(
name|index
condition|?
literal|2
else|:
literal|0
operator|)
operator|+
operator|(
name|store
condition|?
literal|1
else|:
literal|0
operator|)
index|]
decl_stmt|;
if|if
condition|(
name|index
condition|)
block|{
name|f
index|[
literal|0
index|]
operator|=
name|finfo
operator|.
name|createDouble
argument_list|(
name|fieldInfo
operator|.
name|getFieldNameX
argument_list|()
argument_list|,
name|point
operator|.
name|getX
argument_list|()
argument_list|)
expr_stmt|;
name|f
index|[
literal|1
index|]
operator|=
name|finfo
operator|.
name|createDouble
argument_list|(
name|fieldInfo
operator|.
name|getFieldNameY
argument_list|()
argument_list|,
name|point
operator|.
name|getY
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|store
condition|)
block|{
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|customType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|f
index|[
name|f
operator|.
name|length
operator|-
literal|1
index|]
operator|=
operator|new
name|Field
argument_list|(
name|fieldInfo
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|ctx
operator|.
name|toString
argument_list|(
name|shape
argument_list|)
argument_list|,
name|customType
argument_list|)
expr_stmt|;
block|}
return|return
name|f
return|;
block|}
if|if
condition|(
operator|!
name|ignoreIncompatibleGeometry
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"TwoDoublesStrategy can not index: "
operator|+
name|shape
argument_list|)
throw|;
block|}
return|return
operator|new
name|IndexableField
index|[
literal|0
index|]
return|;
comment|// nothing (solr does not support null)
block|}
annotation|@
name|Override
DECL|method|createField
specifier|public
name|IndexableField
name|createField
parameter_list|(
name|TwoDoublesFieldInfo
name|indexInfo
parameter_list|,
name|Shape
name|shape
parameter_list|,
name|boolean
name|index
parameter_list|,
name|boolean
name|store
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Point is poly field"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|makeValueSource
specifier|public
name|ValueSource
name|makeValueSource
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|TwoDoublesFieldInfo
name|fieldInfo
parameter_list|)
block|{
name|Point
name|p
init|=
name|args
operator|.
name|getShape
argument_list|()
operator|.
name|getCenter
argument_list|()
decl_stmt|;
return|return
operator|new
name|DistanceValueSource
argument_list|(
name|p
argument_list|,
name|ctx
operator|.
name|getDistCalc
argument_list|()
argument_list|,
name|fieldInfo
argument_list|,
name|parser
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|makeFilter
specifier|public
name|Filter
name|makeFilter
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|TwoDoublesFieldInfo
name|fieldInfo
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|getShape
argument_list|()
operator|instanceof
name|Circle
condition|)
block|{
if|if
condition|(
name|SpatialOperation
operator|.
name|is
argument_list|(
name|args
operator|.
name|getOperation
argument_list|()
argument_list|,
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|SpatialOperation
operator|.
name|IsWithin
argument_list|)
condition|)
block|{
name|Circle
name|circle
init|=
operator|(
name|Circle
operator|)
name|args
operator|.
name|getShape
argument_list|()
decl_stmt|;
name|Query
name|bbox
init|=
name|makeWithin
argument_list|(
name|circle
operator|.
name|getBoundingBox
argument_list|()
argument_list|,
name|fieldInfo
argument_list|)
decl_stmt|;
comment|// Make the ValueSource
name|ValueSource
name|valueSource
init|=
name|makeValueSource
argument_list|(
name|args
argument_list|,
name|fieldInfo
argument_list|)
decl_stmt|;
return|return
operator|new
name|ValueSourceFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
name|bbox
argument_list|)
argument_list|,
name|valueSource
argument_list|,
literal|0
argument_list|,
name|circle
operator|.
name|getDistance
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|QueryWrapperFilter
argument_list|(
name|makeQuery
argument_list|(
name|args
argument_list|,
name|fieldInfo
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|makeQuery
specifier|public
name|Query
name|makeQuery
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|TwoDoublesFieldInfo
name|fieldInfo
parameter_list|)
block|{
comment|// For starters, just limit the bbox
name|Shape
name|shape
init|=
name|args
operator|.
name|getShape
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|shape
operator|instanceof
name|Rectangle
operator|)
condition|)
block|{
throw|throw
operator|new
name|InvalidShapeException
argument_list|(
literal|"A rectangle is the only supported shape (so far), not "
operator|+
name|shape
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
comment|//TODO
block|}
name|Rectangle
name|bbox
init|=
operator|(
name|Rectangle
operator|)
name|shape
decl_stmt|;
if|if
condition|(
name|bbox
operator|.
name|getCrossesDateLine
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Crossing dateline not yet supported"
argument_list|)
throw|;
block|}
name|ValueSource
name|valueSource
init|=
literal|null
decl_stmt|;
name|Query
name|spatial
init|=
literal|null
decl_stmt|;
name|SpatialOperation
name|op
init|=
name|args
operator|.
name|getOperation
argument_list|()
decl_stmt|;
if|if
condition|(
name|SpatialOperation
operator|.
name|is
argument_list|(
name|op
argument_list|,
name|SpatialOperation
operator|.
name|BBoxWithin
argument_list|,
name|SpatialOperation
operator|.
name|BBoxIntersects
argument_list|)
condition|)
block|{
name|spatial
operator|=
name|makeWithin
argument_list|(
name|bbox
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|SpatialOperation
operator|.
name|is
argument_list|(
name|op
argument_list|,
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|SpatialOperation
operator|.
name|IsWithin
argument_list|)
condition|)
block|{
name|spatial
operator|=
name|makeWithin
argument_list|(
name|bbox
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|getShape
argument_list|()
operator|instanceof
name|Circle
condition|)
block|{
name|Circle
name|circle
init|=
operator|(
name|Circle
operator|)
name|args
operator|.
name|getShape
argument_list|()
decl_stmt|;
comment|// Make the ValueSource
name|valueSource
operator|=
name|makeValueSource
argument_list|(
name|args
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
name|ValueSourceFilter
name|vsf
init|=
operator|new
name|ValueSourceFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
name|spatial
argument_list|)
argument_list|,
name|valueSource
argument_list|,
literal|0
argument_list|,
name|circle
operator|.
name|getDistance
argument_list|()
argument_list|)
decl_stmt|;
name|spatial
operator|=
operator|new
name|FilteredQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|vsf
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|op
operator|==
name|SpatialOperation
operator|.
name|IsDisjointTo
condition|)
block|{
name|spatial
operator|=
name|makeDisjoint
argument_list|(
name|bbox
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|spatial
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedSpatialOperation
argument_list|(
name|args
operator|.
name|getOperation
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|valueSource
operator|!=
literal|null
condition|)
block|{
name|valueSource
operator|=
operator|new
name|CachingDoubleValueSource
argument_list|(
name|valueSource
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|valueSource
operator|=
name|makeValueSource
argument_list|(
name|args
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
name|Query
name|spatialRankingQuery
init|=
operator|new
name|FunctionQuery
argument_list|(
name|valueSource
argument_list|)
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|spatial
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|spatialRankingQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
return|return
name|bq
return|;
block|}
comment|/**    * Constructs a query to retrieve documents that fully contain the input envelope.    * @return the spatial query    */
DECL|method|makeWithin
specifier|private
name|Query
name|makeWithin
parameter_list|(
name|Rectangle
name|bbox
parameter_list|,
name|TwoDoublesFieldInfo
name|fieldInfo
parameter_list|)
block|{
name|Query
name|qX
init|=
name|NumericRangeQuery
operator|.
name|newDoubleRange
argument_list|(
name|fieldInfo
operator|.
name|getFieldNameX
argument_list|()
argument_list|,
name|finfo
operator|.
name|precisionStep
argument_list|,
name|bbox
operator|.
name|getMinX
argument_list|()
argument_list|,
name|bbox
operator|.
name|getMaxX
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Query
name|qY
init|=
name|NumericRangeQuery
operator|.
name|newDoubleRange
argument_list|(
name|fieldInfo
operator|.
name|getFieldNameY
argument_list|()
argument_list|,
name|finfo
operator|.
name|precisionStep
argument_list|,
name|bbox
operator|.
name|getMinY
argument_list|()
argument_list|,
name|bbox
operator|.
name|getMaxY
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|qX
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|qY
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
return|return
name|bq
return|;
block|}
comment|/**    * Constructs a query to retrieve documents that fully contain the input envelope.    * @return the spatial query    */
DECL|method|makeDisjoint
name|Query
name|makeDisjoint
parameter_list|(
name|Rectangle
name|bbox
parameter_list|,
name|TwoDoublesFieldInfo
name|fieldInfo
parameter_list|)
block|{
name|Query
name|qX
init|=
name|NumericRangeQuery
operator|.
name|newDoubleRange
argument_list|(
name|fieldInfo
operator|.
name|getFieldNameX
argument_list|()
argument_list|,
name|finfo
operator|.
name|precisionStep
argument_list|,
name|bbox
operator|.
name|getMinX
argument_list|()
argument_list|,
name|bbox
operator|.
name|getMaxX
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Query
name|qY
init|=
name|NumericRangeQuery
operator|.
name|newDoubleRange
argument_list|(
name|fieldInfo
operator|.
name|getFieldNameY
argument_list|()
argument_list|,
name|finfo
operator|.
name|precisionStep
argument_list|,
name|bbox
operator|.
name|getMinY
argument_list|()
argument_list|,
name|bbox
operator|.
name|getMaxY
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|qX
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|qY
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
return|return
name|bq
return|;
block|}
block|}
end_class
end_unit
