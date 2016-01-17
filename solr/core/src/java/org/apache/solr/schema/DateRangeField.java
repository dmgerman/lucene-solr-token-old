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
name|Calendar
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|prefix
operator|.
name|NumberRangePrefixTreeStrategy
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
name|prefix
operator|.
name|tree
operator|.
name|DateRangePrefixTree
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
name|prefix
operator|.
name|tree
operator|.
name|NumberRangePrefixTree
operator|.
name|NRShape
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
name|prefix
operator|.
name|tree
operator|.
name|NumberRangePrefixTree
operator|.
name|UnitNRShape
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
name|SpatialOperation
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
name|request
operator|.
name|SolrRequestInfo
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
name|SyntaxError
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
name|DateFormatUtil
import|;
end_import
begin_comment
comment|/**  * A field for indexed dates and date ranges. It's mostly compatible with TrieDateField.  *  * @see NumberRangePrefixTreeStrategy  * @see DateRangePrefixTree  */
end_comment
begin_class
DECL|class|DateRangeField
specifier|public
class|class
name|DateRangeField
extends|extends
name|AbstractSpatialPrefixTreeFieldType
argument_list|<
name|NumberRangePrefixTreeStrategy
argument_list|>
block|{
DECL|field|OP_PARAM
specifier|private
specifier|static
specifier|final
name|String
name|OP_PARAM
init|=
literal|"op"
decl_stmt|;
comment|//local-param to resolve SpatialOperation
DECL|field|tree
specifier|private
specifier|static
specifier|final
name|DateRangePrefixTree
name|tree
init|=
name|DateRangePrefixTree
operator|.
name|INSTANCE
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
block|}
annotation|@
name|Override
DECL|method|newPrefixTreeStrategy
specifier|protected
name|NumberRangePrefixTreeStrategy
name|newPrefixTreeStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|NumberRangePrefixTreeStrategy
argument_list|(
name|tree
argument_list|,
name|fieldName
argument_list|)
return|;
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
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|val
operator|instanceof
name|Date
operator|||
name|val
operator|instanceof
name|Calendar
condition|)
comment|//From URP?
name|val
operator|=
name|tree
operator|.
name|toUnitShape
argument_list|(
name|val
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|createFields
argument_list|(
name|field
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getStoredValue
specifier|protected
name|String
name|getStoredValue
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|String
name|shapeStr
parameter_list|)
block|{
if|if
condition|(
name|shape
operator|instanceof
name|UnitNRShape
condition|)
block|{
name|UnitNRShape
name|unitShape
init|=
operator|(
name|UnitNRShape
operator|)
name|shape
decl_stmt|;
if|if
condition|(
name|unitShape
operator|.
name|getLevel
argument_list|()
operator|==
name|tree
operator|.
name|getMaxLevels
argument_list|()
condition|)
block|{
comment|//fully precise date. We can be fully compatible with TrieDateField.
name|Date
name|date
init|=
name|tree
operator|.
name|toCalendar
argument_list|(
name|unitShape
argument_list|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
return|return
name|DateFormatUtil
operator|.
name|formatExternal
argument_list|(
name|date
argument_list|)
return|;
block|}
block|}
return|return
operator|(
name|shapeStr
operator|==
literal|null
condition|?
name|shape
operator|.
name|toString
argument_list|()
else|:
name|shapeStr
operator|)
return|;
comment|//we don't normalize ranges here; should we?
block|}
annotation|@
name|Override
DECL|method|parseShape
specifier|protected
name|NRShape
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
name|contains
argument_list|(
literal|" TO "
argument_list|)
condition|)
block|{
comment|//TODO parsing range syntax doesn't support DateMath on either side or exclusive/inclusive
try|try
block|{
return|return
name|tree
operator|.
name|parseShape
argument_list|(
name|str
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
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
literal|"Couldn't parse date because: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
name|tree
operator|.
name|toShape
argument_list|(
name|parseCalendar
argument_list|(
name|str
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|parseCalendar
specifier|private
name|Calendar
name|parseCalendar
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|.
name|startsWith
argument_list|(
literal|"NOW"
argument_list|)
operator|||
name|str
operator|.
name|lastIndexOf
argument_list|(
literal|'Z'
argument_list|)
operator|>=
literal|0
condition|)
block|{
comment|//use Solr standard date format parsing rules.
comment|//TODO parse a Calendar instead of a Date, rounded according to DateMath syntax.
name|Date
name|date
init|=
name|DateFormatUtil
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|str
argument_list|)
decl_stmt|;
name|Calendar
name|cal
init|=
name|tree
operator|.
name|newCal
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTime
argument_list|(
name|date
argument_list|)
expr_stmt|;
return|return
name|cal
return|;
block|}
else|else
block|{
try|try
block|{
return|return
name|tree
operator|.
name|parseCalendar
argument_list|(
name|str
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
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
literal|"Couldn't parse date because: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** For easy compatibility with {@link DateFormatUtil#parseMath(Date, String)}. */
DECL|method|parseMath
specifier|public
name|Date
name|parseMath
parameter_list|(
name|Date
name|now
parameter_list|,
name|String
name|rawval
parameter_list|)
block|{
return|return
name|DateFormatUtil
operator|.
name|parseMath
argument_list|(
name|now
argument_list|,
name|rawval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shapeToString
specifier|protected
name|String
name|shapeToString
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
if|if
condition|(
name|shape
operator|instanceof
name|UnitNRShape
condition|)
block|{
name|UnitNRShape
name|unitShape
init|=
operator|(
name|UnitNRShape
operator|)
name|shape
decl_stmt|;
if|if
condition|(
name|unitShape
operator|.
name|getLevel
argument_list|()
operator|==
name|tree
operator|.
name|getMaxLevels
argument_list|()
condition|)
block|{
comment|//fully precise date. We can be fully compatible with TrieDateField.
name|Date
name|date
init|=
name|tree
operator|.
name|toCalendar
argument_list|(
name|unitShape
argument_list|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
return|return
name|DateFormatUtil
operator|.
name|formatExternal
argument_list|(
name|date
argument_list|)
return|;
block|}
block|}
return|return
name|shape
operator|.
name|toString
argument_list|()
return|;
comment|//range shape
block|}
annotation|@
name|Override
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
comment|//We avoid SpatialArgsParser entirely because it isn't very Solr-friendly
specifier|final
name|Shape
name|shape
init|=
name|parseShape
argument_list|(
name|externalVal
argument_list|)
decl_stmt|;
specifier|final
name|SolrParams
name|localParams
init|=
name|parser
operator|.
name|getLocalParams
argument_list|()
decl_stmt|;
name|SpatialOperation
name|op
init|=
name|SpatialOperation
operator|.
name|Intersects
decl_stmt|;
if|if
condition|(
name|localParams
operator|!=
literal|null
condition|)
block|{
name|String
name|opStr
init|=
name|localParams
operator|.
name|get
argument_list|(
name|OP_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|opStr
operator|!=
literal|null
condition|)
name|op
operator|=
name|SpatialOperation
operator|.
name|get
argument_list|(
name|opStr
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SpatialArgs
argument_list|(
name|op
argument_list|,
name|shape
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
name|startStr
parameter_list|,
name|String
name|endStr
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
name|parser
operator|==
literal|null
condition|)
block|{
comment|//null when invoked by SimpleFacets.  But getQueryFromSpatialArgs expects to get localParams.
specifier|final
name|SolrRequestInfo
name|requestInfo
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
name|parser
operator|=
operator|new
name|QParser
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|,
name|requestInfo
operator|.
name|getReq
argument_list|()
operator|.
name|getParams
argument_list|()
argument_list|,
name|requestInfo
operator|.
name|getReq
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
expr_stmt|;
block|}
name|Calendar
name|startCal
decl_stmt|;
if|if
condition|(
name|startStr
operator|==
literal|null
condition|)
block|{
name|startCal
operator|=
name|tree
operator|.
name|newCal
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|startCal
operator|=
name|parseCalendar
argument_list|(
name|startStr
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|minInclusive
condition|)
block|{
name|startCal
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|Calendar
name|endCal
decl_stmt|;
if|if
condition|(
name|endStr
operator|==
literal|null
condition|)
block|{
name|endCal
operator|=
name|tree
operator|.
name|newCal
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|endCal
operator|=
name|parseCalendar
argument_list|(
name|endStr
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|maxInclusive
condition|)
block|{
name|endCal
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|Shape
name|shape
init|=
name|tree
operator|.
name|toRangeShape
argument_list|(
name|tree
operator|.
name|toShape
argument_list|(
name|startCal
argument_list|)
argument_list|,
name|tree
operator|.
name|toShape
argument_list|(
name|endCal
argument_list|)
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
name|shape
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
block|}
block|}
end_class
end_unit
