begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Throwables
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|Cache
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|CacheBuilder
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
name|context
operator|.
name|SpatialContextFactory
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
name|io
operator|.
name|LegacyShapeReadWriterFormat
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
name|StoredField
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
name|Filter
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
name|FilteredQuery
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
name|query
operator|.
name|SpatialArgs
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
name|SpatialArgsParser
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|MapListener
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
name|util
operator|.
name|SpatialUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|text
operator|.
name|ParseException
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Locale
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
name|concurrent
operator|.
name|Callable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import
begin_comment
comment|/**  * Abstract base class for Solr FieldTypes based on a Lucene 4 {@link SpatialStrategy}.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|AbstractSpatialFieldType
specifier|public
specifier|abstract
class|class
name|AbstractSpatialFieldType
parameter_list|<
name|T
extends|extends
name|SpatialStrategy
parameter_list|>
extends|extends
name|FieldType
implements|implements
name|SpatialQueryable
block|{
comment|/** A local-param with one of "none" (default), "distance", or "recipDistance". */
DECL|field|SCORE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|SCORE_PARAM
init|=
literal|"score"
decl_stmt|;
comment|/** A local-param boolean that can be set to false to only return the    * FunctionQuery (score), and thus not do filtering.    */
DECL|field|FILTER_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|FILTER_PARAM
init|=
literal|"filter"
decl_stmt|;
DECL|field|log
specifier|protected
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|ctx
specifier|protected
name|SpatialContext
name|ctx
decl_stmt|;
DECL|field|argsParser
specifier|protected
name|SpatialArgsParser
name|argsParser
decl_stmt|;
DECL|field|fieldStrategyCache
specifier|private
specifier|final
name|Cache
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|fieldStrategyCache
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|String
name|units
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"units"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
literal|"degrees"
operator|.
name|equals
argument_list|(
name|units
argument_list|)
condition|)
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
literal|"Must specify units=\"degrees\" on field types with class "
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
throw|;
comment|//replace legacy rect format with ENVELOPE
name|String
name|wbStr
init|=
name|args
operator|.
name|get
argument_list|(
literal|"worldBounds"
argument_list|)
decl_stmt|;
if|if
condition|(
name|wbStr
operator|!=
literal|null
operator|&&
operator|!
name|wbStr
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"ENVELOPE"
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Using old worldBounds format? Should use ENVELOPE(xMin, xMax, yMax, yMin)."
argument_list|)
expr_stmt|;
name|String
index|[]
name|parts
init|=
name|wbStr
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
comment|//"xMin yMin xMax yMax"
if|if
condition|(
name|parts
operator|.
name|length
operator|==
literal|4
condition|)
block|{
name|args
operator|.
name|put
argument_list|(
literal|"worldBounds"
argument_list|,
literal|"ENVELOPE("
operator|+
name|parts
index|[
literal|0
index|]
operator|+
literal|", "
operator|+
name|parts
index|[
literal|2
index|]
operator|+
literal|", "
operator|+
name|parts
index|[
literal|3
index|]
operator|+
literal|", "
operator|+
name|parts
index|[
literal|1
index|]
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
comment|//else likely eventual exception
block|}
comment|//Solr expects us to remove the parameters we've used.
name|MapListener
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|argsWrap
init|=
operator|new
name|MapListener
argument_list|<>
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|ctx
operator|=
name|SpatialContextFactory
operator|.
name|makeSpatialContext
argument_list|(
name|argsWrap
argument_list|,
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|argsWrap
operator|.
name|getSeenKeys
argument_list|()
argument_list|)
expr_stmt|;
name|argsParser
operator|=
name|newSpatialArgsParser
argument_list|()
expr_stmt|;
block|}
DECL|method|newSpatialArgsParser
specifier|protected
name|SpatialArgsParser
name|newSpatialArgsParser
parameter_list|()
block|{
return|return
operator|new
name|SpatialArgsParser
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Shape
name|parseShape
parameter_list|(
name|String
name|str
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
throws|throws
name|ParseException
block|{
return|return
name|AbstractSpatialFieldType
operator|.
name|this
operator|.
name|parseShape
argument_list|(
name|str
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|//--------------------------------------------------------------
comment|// Indexing
comment|//--------------------------------------------------------------
annotation|@
name|Override
DECL|method|createField
specifier|public
specifier|final
name|Field
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"instead call createFields() because isPolyField() is true"
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
DECL|method|createFields
specifier|public
name|List
argument_list|<
name|StorableField
argument_list|>
name|createFields
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|String
name|shapeStr
init|=
literal|null
decl_stmt|;
name|Shape
name|shape
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|Shape
condition|)
block|{
name|shape
operator|=
operator|(
operator|(
name|Shape
operator|)
name|val
operator|)
expr_stmt|;
block|}
else|else
block|{
name|shapeStr
operator|=
name|val
operator|.
name|toString
argument_list|()
expr_stmt|;
name|shape
operator|=
name|parseShape
argument_list|(
name|shapeStr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shape
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Field {}: null shape for input: {}"
argument_list|,
name|field
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|StorableField
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|indexed
argument_list|()
condition|)
block|{
name|T
name|strategy
init|=
name|getStrategy
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|strategy
operator|.
name|createIndexableFields
argument_list|(
name|shape
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|field
operator|.
name|stored
argument_list|()
condition|)
block|{
if|if
condition|(
name|shapeStr
operator|==
literal|null
condition|)
name|shapeStr
operator|=
name|shapeToString
argument_list|(
name|shape
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|shapeStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|parseShape
specifier|protected
name|Shape
name|parseShape
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
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
literal|"empty string shape"
argument_list|)
throw|;
comment|//In Solr trunk we only support "lat, lon" (or x y) as an additional format; in v4.0 we do the
comment|// weird Circle& Rect formats too (Spatial4j LegacyShapeReadWriterFormat).
try|try
block|{
name|Shape
name|shape
init|=
name|LegacyShapeReadWriterFormat
operator|.
name|readShapeOrNull
argument_list|(
name|str
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
if|if
condition|(
name|shape
operator|!=
literal|null
condition|)
return|return
name|shape
return|;
return|return
name|ctx
operator|.
name|readShapeFromWkt
argument_list|(
name|str
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|message
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|message
operator|.
name|contains
argument_list|(
name|str
argument_list|)
condition|)
name|message
operator|=
literal|"Couldn't parse shape '"
operator|+
name|str
operator|+
literal|"' because: "
operator|+
name|message
expr_stmt|;
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
name|message
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns a String version of a shape to be used for the stored value. This method in Solr is only called if for some    * reason a Shape object is passed to the field type (perhaps via a custom UpdateRequestProcessor),    * *and* the field is marked as stored.<em>The default implementation throws an exception.</em>    *<p/>    * Spatial4j 0.4 is probably the last release to support SpatialContext.toString(shape) but it's deprecated with no    * planned replacement.  Shapes do have a toString() method but they are generally internal/diagnostic and not    * standard WKT.    * The solution is subclassing and calling ctx.toString(shape) or directly using LegacyShapeReadWriterFormat or    * passing in some sort of custom wrapped shape that holds a reference to a String or can generate it.    */
DECL|method|shapeToString
specifier|protected
name|String
name|shapeToString
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
comment|//    return ctx.toString(shape);
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
literal|"Getting a String from a Shape is no longer possible. See javadocs for commentary."
argument_list|)
throw|;
block|}
comment|/** Called from {@link #getStrategy(String)} upon first use by fieldName. } */
DECL|method|newSpatialStrategy
specifier|protected
specifier|abstract
name|T
name|newSpatialStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|isPolyField
specifier|public
specifier|final
name|boolean
name|isPolyField
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|//--------------------------------------------------------------
comment|// Query Support
comment|//--------------------------------------------------------------
comment|/**    * Implemented for compatibility with geofilt& bbox query parsers:    * {@link SpatialQueryable}.    */
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
name|Point
name|pt
init|=
name|SpatialUtils
operator|.
name|parsePointSolrException
argument_list|(
name|options
operator|.
name|pointStr
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|double
name|distDeg
init|=
name|DistanceUtils
operator|.
name|dist2Degrees
argument_list|(
name|options
operator|.
name|distance
argument_list|,
name|options
operator|.
name|radius
argument_list|)
decl_stmt|;
name|Shape
name|shape
init|=
name|ctx
operator|.
name|makeCircle
argument_list|(
name|pt
argument_list|,
name|distDeg
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|bbox
condition|)
name|shape
operator|=
name|shape
operator|.
name|getBoundingBox
argument_list|()
expr_stmt|;
name|SpatialArgs
name|spatialArgs
init|=
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|shape
argument_list|)
decl_stmt|;
return|return
name|getQueryFromSpatialArgs
argument_list|(
name|parser
argument_list|,
name|options
operator|.
name|field
argument_list|,
name|spatialArgs
argument_list|)
return|;
block|}
annotation|@
name|Override
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
if|if
condition|(
operator|!
name|minInclusive
operator|||
operator|!
name|maxInclusive
condition|)
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
literal|"Both sides of spatial range query must be inclusive: "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
name|Point
name|p1
init|=
name|SpatialUtils
operator|.
name|parsePointSolrException
argument_list|(
name|part1
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|Point
name|p2
init|=
name|SpatialUtils
operator|.
name|parsePointSolrException
argument_list|(
name|part2
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|Rectangle
name|bbox
init|=
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|p1
argument_list|,
name|p2
argument_list|)
decl_stmt|;
name|SpatialArgs
name|spatialArgs
init|=
operator|new
name|SpatialArgs
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|bbox
argument_list|)
decl_stmt|;
return|return
name|getQueryFromSpatialArgs
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|spatialArgs
argument_list|)
return|;
comment|//won't score by default
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
comment|//This is different from Solr 3 LatLonType's approach which uses the MultiValueSource concept to directly expose
comment|// the x& y pair of FieldCache value sources.
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
literal|"A ValueSource isn't directly available from this field. Instead try a query using the distance as the score."
argument_list|)
throw|;
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
return|return
name|getQueryFromSpatialArgs
argument_list|(
name|parser
argument_list|,
name|field
argument_list|,
name|parseSpatialArgs
argument_list|(
name|parser
argument_list|,
name|externalVal
argument_list|)
argument_list|)
return|;
block|}
DECL|method|parseSpatialArgs
specifier|protected
name|SpatialArgs
name|parseSpatialArgs
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
try|try
block|{
return|return
name|argsParser
operator|.
name|parse
argument_list|(
name|externalVal
argument_list|,
name|ctx
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
DECL|method|getQueryFromSpatialArgs
specifier|protected
name|Query
name|getQueryFromSpatialArgs
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|SpatialArgs
name|spatialArgs
parameter_list|)
block|{
name|T
name|strategy
init|=
name|getStrategy
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|SolrParams
name|localParams
init|=
name|parser
operator|.
name|getLocalParams
argument_list|()
decl_stmt|;
name|String
name|score
init|=
operator|(
name|localParams
operator|==
literal|null
condition|?
literal|null
else|:
name|localParams
operator|.
name|get
argument_list|(
name|SCORE_PARAM
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|score
operator|==
literal|null
operator|||
literal|"none"
operator|.
name|equals
argument_list|(
name|score
argument_list|)
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|score
argument_list|)
condition|)
block|{
comment|//FYI Solr FieldType doesn't have a getFilter(). We'll always grab
comment|// getQuery() but it's possible a strategy has a more efficient getFilter
comment|// that could be wrapped -- no way to know.
comment|//See SOLR-2883 needScore
return|return
name|strategy
operator|.
name|makeQuery
argument_list|(
name|spatialArgs
argument_list|)
return|;
comment|//ConstantScoreQuery
block|}
comment|//We get the valueSource for the score then the filter and combine them.
name|ValueSource
name|valueSource
decl_stmt|;
if|if
condition|(
literal|"distance"
operator|.
name|equals
argument_list|(
name|score
argument_list|)
condition|)
block|{
name|double
name|multiplier
init|=
literal|1.0
decl_stmt|;
comment|//TODO support units=kilometers
name|valueSource
operator|=
name|strategy
operator|.
name|makeDistanceValueSource
argument_list|(
name|spatialArgs
operator|.
name|getShape
argument_list|()
operator|.
name|getCenter
argument_list|()
argument_list|,
name|multiplier
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"recipDistance"
operator|.
name|equals
argument_list|(
name|score
argument_list|)
condition|)
block|{
name|valueSource
operator|=
name|strategy
operator|.
name|makeRecipDistanceValueSource
argument_list|(
name|spatialArgs
operator|.
name|getShape
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"'score' local-param must be one of 'none', 'distance', or 'recipDistance'"
argument_list|)
throw|;
block|}
name|FunctionQuery
name|functionQuery
init|=
operator|new
name|FunctionQuery
argument_list|(
name|valueSource
argument_list|)
decl_stmt|;
if|if
condition|(
name|localParams
operator|!=
literal|null
operator|&&
operator|!
name|localParams
operator|.
name|getBool
argument_list|(
name|FILTER_PARAM
argument_list|,
literal|true
argument_list|)
condition|)
return|return
name|functionQuery
return|;
name|Filter
name|filter
init|=
name|strategy
operator|.
name|makeFilter
argument_list|(
name|spatialArgs
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilteredQuery
argument_list|(
name|functionQuery
argument_list|,
name|filter
argument_list|)
return|;
block|}
comment|/**    * Gets the cached strategy for this field, creating it if necessary    * via {@link #newSpatialStrategy(String)}.    * @param fieldName Mandatory reference to the field name    * @return Non-null.    */
DECL|method|getStrategy
specifier|public
name|T
name|getStrategy
parameter_list|(
specifier|final
name|String
name|fieldName
parameter_list|)
block|{
try|try
block|{
return|return
name|fieldStrategyCache
operator|.
name|get
argument_list|(
name|fieldName
argument_list|,
operator|new
name|Callable
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|newSpatialStrategy
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|Throwables
operator|.
name|propagate
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
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
literal|"Sorting not supported on SpatialField: "
operator|+
name|field
operator|.
name|getName
argument_list|()
operator|+
literal|", instead try sorting by query."
argument_list|)
throw|;
block|}
block|}
end_class
end_unit
