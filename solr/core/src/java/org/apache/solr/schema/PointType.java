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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|distance
operator|.
name|DistanceUtils
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
name|VectorValueSource
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
name|BooleanClause
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
name|BooleanQuery
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|uninverting
operator|.
name|UninvertingReader
operator|.
name|Type
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
name|common
operator|.
name|params
operator|.
name|MapSolrParams
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
name|params
operator|.
name|SolrParams
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
name|SpatialOptions
import|;
end_import
begin_comment
comment|/**  * A point type that indexes a point in an n-dimensional space as separate fields and supports range queries.  * See {@link LatLonType} for geo-spatial queries.  */
end_comment
begin_class
DECL|class|PointType
specifier|public
class|class
name|PointType
extends|extends
name|CoordinateFieldType
implements|implements
name|SpatialQueryable
block|{
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|SolrParams
name|p
init|=
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|dimension
operator|=
name|p
operator|.
name|getInt
argument_list|(
name|DIMENSION
argument_list|,
name|DEFAULT_DIMENSION
argument_list|)
expr_stmt|;
if|if
condition|(
name|dimension
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"The dimension must be> 0: "
operator|+
name|dimension
argument_list|)
throw|;
block|}
name|args
operator|.
name|remove
argument_list|(
name|DIMENSION
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
comment|// cache suffixes
name|createSuffixCache
argument_list|(
name|dimension
argument_list|)
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
comment|// really only true if the field is indexed
block|}
annotation|@
name|Override
DECL|method|createFields
specifier|public
name|List
argument_list|<
name|IndexableField
argument_list|>
name|createFields
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|value
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|String
name|externalVal
init|=
name|value
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
index|[]
name|point
init|=
name|parseCommaSeparatedList
argument_list|(
name|externalVal
argument_list|,
name|dimension
argument_list|)
decl_stmt|;
comment|// TODO: this doesn't currently support polyFields as sub-field types
name|List
argument_list|<
name|IndexableField
argument_list|>
name|f
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|dimension
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|indexed
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dimension
condition|;
name|i
operator|++
control|)
block|{
name|SchemaField
name|sf
init|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|f
operator|.
name|add
argument_list|(
name|sf
operator|.
name|createField
argument_list|(
name|point
index|[
name|i
index|]
argument_list|,
name|sf
operator|.
name|indexed
argument_list|()
operator|&&
operator|!
name|sf
operator|.
name|omitNorms
argument_list|()
condition|?
name|boost
else|:
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|field
operator|.
name|stored
argument_list|()
condition|)
block|{
name|String
name|storedVal
init|=
name|externalVal
decl_stmt|;
comment|// normalize or not?
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
operator|.
name|add
argument_list|(
name|createField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|storedVal
argument_list|,
name|customType
argument_list|,
literal|1f
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|f
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
name|ArrayList
argument_list|<
name|ValueSource
argument_list|>
name|vs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|dimension
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
name|dimension
condition|;
name|i
operator|++
control|)
block|{
name|SchemaField
name|sub
init|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|vs
operator|.
name|add
argument_list|(
name|sub
operator|.
name|getType
argument_list|()
operator|.
name|getValueSource
argument_list|(
name|sub
argument_list|,
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PointTypeValueSource
argument_list|(
name|field
argument_list|,
name|vs
argument_list|)
return|;
block|}
comment|/**    * It never makes sense to create a single field, so make it impossible to happen by    * throwing UnsupportedOperationException    *    */
annotation|@
name|Override
DECL|method|createField
specifier|public
name|IndexableField
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|value
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"PointType uses multiple fields.  field="
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
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
name|IndexableField
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
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
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
literal|"Sorting not supported on PointType "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getUninversionType
specifier|public
name|Type
name|getUninversionType
parameter_list|(
name|SchemaField
name|sf
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
comment|/**    * Care should be taken in calling this with higher order dimensions for performance reasons.    */
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
comment|//Query could look like: [x1,y1 TO x2,y2] for 2 dimension, but could look like: [x1,y1,z1 TO x2,y2,z2], and can be extrapolated to n-dimensions
comment|//thus, this query essentially creates a box, cube, etc.
name|String
index|[]
name|p1
init|=
name|parseCommaSeparatedList
argument_list|(
name|part1
argument_list|,
name|dimension
argument_list|)
decl_stmt|;
name|String
index|[]
name|p2
init|=
name|parseCommaSeparatedList
argument_list|(
name|part2
argument_list|,
name|dimension
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|result
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|result
operator|.
name|setDisableCoord
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dimension
condition|;
name|i
operator|++
control|)
block|{
name|SchemaField
name|subSF
init|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
argument_list|,
name|schema
argument_list|)
decl_stmt|;
comment|// points must currently be ordered... should we support specifying any two opposite corner points?
name|result
operator|.
name|add
argument_list|(
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
name|p1
index|[
name|i
index|]
argument_list|,
name|p2
index|[
name|i
index|]
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldQuery
specifier|public
name|Query
name|getFieldQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
name|String
index|[]
name|p1
init|=
name|parseCommaSeparatedList
argument_list|(
name|externalVal
argument_list|,
name|dimension
argument_list|)
decl_stmt|;
comment|//TODO: should we assert that p1.length == dimension?
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq
operator|.
name|setDisableCoord
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dimension
condition|;
name|i
operator|++
control|)
block|{
name|SchemaField
name|sf
init|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|Query
name|tq
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getFieldQuery
argument_list|(
name|parser
argument_list|,
name|sf
argument_list|,
name|p1
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
return|return
name|bq
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * Calculates the range and creates a RangeQuery (bounding box) wrapped in a BooleanQuery (unless the dimension is    * 1, one range for every dimension, AND'd together by a Boolean    *    * @param parser  The parser    * @param options The {@link org.apache.solr.search.SpatialOptions} for this filter.    * @return The Query representing the bounding box around the point.    */
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
name|String
index|[]
name|pointStrs
init|=
name|parseCommaSeparatedList
argument_list|(
name|options
operator|.
name|pointStr
argument_list|,
name|dimension
argument_list|)
decl_stmt|;
name|double
index|[]
name|point
init|=
operator|new
name|double
index|[
name|dimension
index|]
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pointStrs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|point
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|pointStrs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
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
name|IndexSchema
name|schema
init|=
name|parser
operator|.
name|getReq
argument_list|()
operator|.
name|getSchema
argument_list|()
decl_stmt|;
if|if
condition|(
name|dimension
operator|==
literal|1
condition|)
block|{
comment|//TODO: Handle distance measures
name|String
name|lower
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|point
index|[
literal|0
index|]
operator|-
name|options
operator|.
name|distance
argument_list|)
decl_stmt|;
name|String
name|upper
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|point
index|[
literal|0
index|]
operator|+
name|options
operator|.
name|distance
argument_list|)
decl_stmt|;
name|SchemaField
name|subSF
init|=
name|subField
argument_list|(
name|options
operator|.
name|field
argument_list|,
literal|0
argument_list|,
name|schema
argument_list|)
decl_stmt|;
comment|// points must currently be ordered... should we support specifying any two opposite corner points?
return|return
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
else|else
block|{
name|BooleanQuery
operator|.
name|Builder
name|tmp
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
comment|//TODO: Handle distance measures, as this assumes Euclidean
name|double
index|[]
name|ur
init|=
name|vectorBoxCorner
argument_list|(
name|point
argument_list|,
literal|null
argument_list|,
name|options
operator|.
name|distance
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|double
index|[]
name|ll
init|=
name|vectorBoxCorner
argument_list|(
name|point
argument_list|,
literal|null
argument_list|,
name|options
operator|.
name|distance
argument_list|,
literal|false
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
name|ur
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SchemaField
name|subSF
init|=
name|subField
argument_list|(
name|options
operator|.
name|field
argument_list|,
name|i
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|Query
name|range
init|=
name|subSF
operator|.
name|getType
argument_list|()
operator|.
name|getRangeQuery
argument_list|(
name|parser
argument_list|,
name|subSF
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ll
index|[
name|i
index|]
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|ur
index|[
name|i
index|]
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|tmp
operator|.
name|add
argument_list|(
name|range
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
return|return
name|tmp
operator|.
name|build
argument_list|()
return|;
block|}
block|}
DECL|field|SIN_PI_DIV_4
specifier|private
specifier|static
specifier|final
name|double
name|SIN_PI_DIV_4
init|=
name|Math
operator|.
name|sin
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
decl_stmt|;
comment|/**    * Return the coordinates of a vector that is the corner of a box (upper right or lower left), assuming a Rectangular    * coordinate system.  Note, this does not apply for points on a sphere or ellipse (although it could be used as an    * approximation).    *    * @param center     The center point    * @param result     Holds the result, potentially resizing if needed.    * @param distance   The d from the center to the corner    * @param upperRight If true, return the coords for the upper right corner, else return the lower left.    * @return The point, either the upperLeft or the lower right    */
DECL|method|vectorBoxCorner
specifier|public
specifier|static
name|double
index|[]
name|vectorBoxCorner
parameter_list|(
name|double
index|[]
name|center
parameter_list|,
name|double
index|[]
name|result
parameter_list|,
name|double
name|distance
parameter_list|,
name|boolean
name|upperRight
parameter_list|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
operator|||
name|result
operator|.
name|length
operator|!=
name|center
operator|.
name|length
condition|)
block|{
name|result
operator|=
operator|new
name|double
index|[
name|center
operator|.
name|length
index|]
expr_stmt|;
block|}
if|if
condition|(
name|upperRight
operator|==
literal|false
condition|)
block|{
name|distance
operator|=
operator|-
name|distance
expr_stmt|;
block|}
comment|//We don't care about the power here,
comment|// b/c we are always in a rectangular coordinate system, so any norm can be used by
comment|//using the definition of sine
name|distance
operator|=
name|SIN_PI_DIV_4
operator|*
name|distance
expr_stmt|;
comment|// sin(Pi/4) == (2^0.5)/2 == opp/hyp == opp/distance, solve for opp, similarly for cosine
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|center
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|center
index|[
name|i
index|]
operator|+
name|distance
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Given a string containing<i>dimension</i> values encoded in it, separated by commas,    * return a String array of length<i>dimension</i> containing the values.    *    * @param externalVal The value to parse    * @param dimension   The expected number of values for the point    * @return An array of the values that make up the point (aka vector)    * @throws SolrException if the dimension specified does not match the number found    */
DECL|method|parseCommaSeparatedList
specifier|public
specifier|static
name|String
index|[]
name|parseCommaSeparatedList
parameter_list|(
name|String
name|externalVal
parameter_list|,
name|int
name|dimension
parameter_list|)
throws|throws
name|SolrException
block|{
comment|//TODO: Should we support sparse vectors?
name|String
index|[]
name|out
init|=
operator|new
name|String
index|[
name|dimension
index|]
decl_stmt|;
name|int
name|idx
init|=
name|externalVal
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
name|int
name|end
init|=
name|idx
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|idx
operator|==
operator|-
literal|1
operator|&&
name|dimension
operator|==
literal|1
operator|&&
name|externalVal
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//we have a single point, dimension better be 1
name|out
index|[
literal|0
index|]
operator|=
name|externalVal
operator|.
name|trim
argument_list|()
expr_stmt|;
name|i
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
comment|//if it is zero, that is an error
comment|//Parse out a comma separated list of values, as in: 73.5,89.2,7773.4
for|for
control|(
init|;
name|i
operator|<
name|dimension
condition|;
name|i
operator|++
control|)
block|{
while|while
condition|(
name|start
operator|<
name|end
operator|&&
name|externalVal
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
operator|==
literal|' '
condition|)
name|start
operator|++
expr_stmt|;
while|while
condition|(
name|end
operator|>
name|start
operator|&&
name|externalVal
operator|.
name|charAt
argument_list|(
name|end
operator|-
literal|1
argument_list|)
operator|==
literal|' '
condition|)
name|end
operator|--
expr_stmt|;
if|if
condition|(
name|start
operator|==
name|end
condition|)
block|{
break|break;
block|}
name|out
index|[
name|i
index|]
operator|=
name|externalVal
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|start
operator|=
name|idx
operator|+
literal|1
expr_stmt|;
name|end
operator|=
name|externalVal
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|,
name|start
argument_list|)
expr_stmt|;
name|idx
operator|=
name|end
expr_stmt|;
if|if
condition|(
name|end
operator|==
operator|-
literal|1
condition|)
block|{
name|end
operator|=
name|externalVal
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|i
operator|!=
name|dimension
condition|)
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
literal|"incompatible dimension ("
operator|+
name|dimension
operator|+
literal|") and values ("
operator|+
name|externalVal
operator|+
literal|").  Only "
operator|+
name|i
operator|+
literal|" values specified"
argument_list|)
throw|;
block|}
return|return
name|out
return|;
block|}
annotation|@
name|Override
DECL|method|getSphereRadius
specifier|public
name|double
name|getSphereRadius
parameter_list|()
block|{
comment|// This won't likely be used. You should probably be using LatLonType instead if you felt the need for this.
comment|// This is here just for backward compatibility reasons.
return|return
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
return|;
block|}
block|}
end_class
begin_class
DECL|class|PointTypeValueSource
class|class
name|PointTypeValueSource
extends|extends
name|VectorValueSource
block|{
DECL|field|sf
specifier|private
specifier|final
name|SchemaField
name|sf
decl_stmt|;
DECL|method|PointTypeValueSource
specifier|public
name|PointTypeValueSource
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
parameter_list|)
block|{
name|super
argument_list|(
name|sources
argument_list|)
expr_stmt|;
name|this
operator|.
name|sf
operator|=
name|sf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"point"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|name
argument_list|()
operator|+
literal|"("
operator|+
name|sf
operator|.
name|getName
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
