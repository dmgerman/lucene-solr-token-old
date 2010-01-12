begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Fieldable
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
name|request
operator|.
name|XMLWriter
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
name|function
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
name|solr
operator|.
name|search
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
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|distance
operator|.
name|DistanceUtils
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
begin_comment
comment|/**  * A point type that indexes a point in an n-dimensional space as separate fields and uses  * range queries for bounding box calculations.  *<p/>  *<p/>  * NOTE: There can only be one sub type  */
end_comment
begin_class
DECL|class|PointType
specifier|public
class|class
name|PointType
extends|extends
name|CoordinateFieldType
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
name|BAD_REQUEST
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
name|this
operator|.
name|schema
operator|=
name|schema
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
name|Fieldable
index|[]
name|createFields
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|String
index|[]
name|point
init|=
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
literal|null
argument_list|,
name|externalVal
argument_list|,
name|dimension
argument_list|)
decl_stmt|;
comment|// TODO: this doesn't currently support polyFields as sub-field types
name|Fieldable
index|[]
name|f
init|=
operator|new
name|Fieldable
index|[
operator|(
name|field
operator|.
name|indexed
argument_list|()
condition|?
name|dimension
else|:
literal|0
operator|)
operator|+
operator|(
name|field
operator|.
name|stored
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
index|]
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
name|f
index|[
name|i
index|]
operator|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
argument_list|)
operator|.
name|createField
argument_list|(
name|point
index|[
name|i
index|]
argument_list|,
name|boost
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
name|f
index|[
name|f
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|createField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|storedVal
argument_list|,
name|getFieldStore
argument_list|(
name|field
argument_list|,
name|storedVal
argument_list|)
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|boost
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
argument_list|<
name|ValueSource
argument_list|>
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
comment|//It never makes sense to create a single field, so make it impossible to happen
annotation|@
name|Override
DECL|method|createField
specifier|public
name|Field
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
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
name|XMLWriter
name|xmlWriter
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|xmlWriter
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
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
name|Fieldable
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
literal|false
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
literal|"Sorting not suported on PointType "
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
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
literal|null
argument_list|,
name|part1
argument_list|,
name|dimension
argument_list|)
decl_stmt|;
name|String
index|[]
name|p2
init|=
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
literal|null
argument_list|,
name|part2
argument_list|,
name|dimension
argument_list|)
decl_stmt|;
name|BooleanQuery
name|result
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
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
name|subSF
init|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
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
name|DistanceUtils
operator|.
name|parsePoint
argument_list|(
literal|null
argument_list|,
name|externalVal
argument_list|,
name|dimension
argument_list|)
decl_stmt|;
comment|//TODO: should we assert that p1.length == dimension?
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
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
name|sf
init|=
name|subField
argument_list|(
name|field
argument_list|,
name|i
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
