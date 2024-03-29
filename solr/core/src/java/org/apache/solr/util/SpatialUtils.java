begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
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
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|context
operator|.
name|SpatialContext
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
name|exception
operator|.
name|InvalidShapeException
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
name|shape
operator|.
name|Point
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
name|shape
operator|.
name|Rectangle
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
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_comment
comment|/** Utility methods pertaining to spatial. */
end_comment
begin_class
DECL|class|SpatialUtils
specifier|public
class|class
name|SpatialUtils
block|{
DECL|method|SpatialUtils
specifier|private
name|SpatialUtils
parameter_list|()
block|{}
comment|/**    * Parses a 'geom' parameter (might also be used to parse shapes for indexing). {@code geomStr} can either be WKT or    * a rectangle-range syntax (see {@link #parseRectangle(String, org.locationtech.spatial4j.context.SpatialContext)}.    */
DECL|method|parseGeomSolrException
specifier|public
specifier|static
name|Shape
name|parseGeomSolrException
parameter_list|(
name|String
name|geomStr
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
if|if
condition|(
name|geomStr
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"0-length geometry string"
argument_list|)
throw|;
block|}
name|char
name|c
init|=
name|geomStr
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'['
operator|||
name|c
operator|==
literal|'{'
condition|)
block|{
return|return
name|parseRectangeSolrException
argument_list|(
name|geomStr
argument_list|,
name|ctx
argument_list|)
return|;
block|}
comment|//TODO parse a raw point?
try|try
block|{
return|return
name|ctx
operator|.
name|readShapeFromWkt
argument_list|(
name|geomStr
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
literal|"Expecting WKT or '[minPoint TO maxPoint]': "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Parses either "lat, lon" (spaces optional on either comma side) or "x y" style formats. Spaces can be basically    * anywhere.  And not any whitespace, just the space char.    *    * @param str Non-null; may have leading or trailing spaces    * @param ctx Non-null    * @return Non-null    * @throws InvalidShapeException If for any reason there was a problem parsing the string or creating the point.    */
DECL|method|parsePoint
specifier|public
specifier|static
name|Point
name|parsePoint
parameter_list|(
name|String
name|str
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
comment|//note we don't do generic whitespace, just a literal space char detection
try|try
block|{
name|double
name|x
decl_stmt|,
name|y
decl_stmt|;
name|str
operator|=
name|str
operator|.
name|trim
argument_list|()
expr_stmt|;
comment|//TODO use findIndexNotSpace instead?
name|int
name|commaIdx
init|=
name|str
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
if|if
condition|(
name|commaIdx
operator|==
operator|-
literal|1
condition|)
block|{
comment|//  "x y" format
name|int
name|spaceIdx
init|=
name|str
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
decl_stmt|;
if|if
condition|(
name|spaceIdx
operator|==
operator|-
literal|1
condition|)
throw|throw
operator|new
name|InvalidShapeException
argument_list|(
literal|"Point must be in 'lat, lon' or 'x y' format: "
operator|+
name|str
argument_list|)
throw|;
name|int
name|middleEndIdx
init|=
name|findIndexNotSpace
argument_list|(
name|str
argument_list|,
name|spaceIdx
operator|+
literal|1
argument_list|,
operator|+
literal|1
argument_list|)
decl_stmt|;
name|x
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|spaceIdx
argument_list|)
argument_list|)
expr_stmt|;
name|y
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|middleEndIdx
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// "lat, lon" format
name|int
name|middleStartIdx
init|=
name|findIndexNotSpace
argument_list|(
name|str
argument_list|,
name|commaIdx
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|middleEndIdx
init|=
name|findIndexNotSpace
argument_list|(
name|str
argument_list|,
name|commaIdx
operator|+
literal|1
argument_list|,
operator|+
literal|1
argument_list|)
decl_stmt|;
name|y
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|middleStartIdx
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|x
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|middleEndIdx
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|x
operator|=
name|ctx
operator|.
name|normX
argument_list|(
name|x
argument_list|)
expr_stmt|;
comment|//by default norm* methods do nothing but perhaps it's been customized
name|y
operator|=
name|ctx
operator|.
name|normY
argument_list|(
name|y
argument_list|)
expr_stmt|;
return|return
name|ctx
operator|.
name|makePoint
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
return|;
comment|//will verify x& y fit in boundary
block|}
catch|catch
parameter_list|(
name|InvalidShapeException
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
name|InvalidShapeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|findIndexNotSpace
specifier|private
specifier|static
name|int
name|findIndexNotSpace
parameter_list|(
name|String
name|str
parameter_list|,
name|int
name|startIdx
parameter_list|,
name|int
name|inc
parameter_list|)
block|{
assert|assert
name|inc
operator|==
operator|+
literal|1
operator|||
name|inc
operator|==
operator|-
literal|1
assert|;
name|int
name|idx
init|=
name|startIdx
decl_stmt|;
while|while
condition|(
name|idx
operator|>=
literal|0
operator|&&
name|idx
operator|<
name|str
operator|.
name|length
argument_list|()
operator|&&
name|str
operator|.
name|charAt
argument_list|(
name|idx
argument_list|)
operator|==
literal|' '
condition|)
name|idx
operator|+=
name|inc
expr_stmt|;
return|return
name|idx
return|;
block|}
comment|/** Calls {@link #parsePoint(String, org.locationtech.spatial4j.context.SpatialContext)} and wraps    * the exception with {@link org.apache.solr.common.SolrException} with a helpful message. */
DECL|method|parsePointSolrException
specifier|public
specifier|static
name|Point
name|parsePointSolrException
parameter_list|(
name|String
name|externalVal
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
throws|throws
name|SolrException
block|{
try|try
block|{
return|return
name|parsePoint
argument_list|(
name|externalVal
argument_list|,
name|ctx
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidShapeException
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
name|externalVal
argument_list|)
condition|)
name|message
operator|=
literal|"Can't parse point '"
operator|+
name|externalVal
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
comment|/**    * Parses {@code str} in the format of '[minPoint TO maxPoint]' where {@code minPoint} is the lower left corner    * and maxPoint is the upper-right corner of the bounding box.  Both corners may optionally be wrapped with a quote    * and then it's parsed via {@link #parsePoint(String, org.locationtech.spatial4j.context.SpatialContext)}.    * @param str Non-null; may *not* have leading or trailing spaces    * @param ctx Non-null    * @return the Rectangle    * @throws InvalidShapeException If for any reason there was a problem parsing the string or creating the rectangle.    */
DECL|method|parseRectangle
specifier|public
specifier|static
name|Rectangle
name|parseRectangle
parameter_list|(
name|String
name|str
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
comment|//note we don't do generic whitespace, just a literal space char detection
try|try
block|{
name|int
name|toIdx
init|=
name|str
operator|.
name|indexOf
argument_list|(
literal|" TO "
argument_list|)
decl_stmt|;
if|if
condition|(
name|toIdx
operator|==
operator|-
literal|1
operator|||
name|str
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'['
operator|||
name|str
operator|.
name|charAt
argument_list|(
name|str
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|']'
condition|)
block|{
throw|throw
operator|new
name|InvalidShapeException
argument_list|(
literal|"expecting '[bottomLeft TO topRight]'"
argument_list|)
throw|;
block|}
name|String
name|leftPart
init|=
name|unwrapQuotes
argument_list|(
name|str
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|toIdx
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|rightPart
init|=
name|unwrapQuotes
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|toIdx
operator|+
literal|" TO "
operator|.
name|length
argument_list|()
argument_list|,
name|str
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ctx
operator|.
name|makeRectangle
argument_list|(
name|parsePoint
argument_list|(
name|leftPart
argument_list|,
name|ctx
argument_list|)
argument_list|,
name|parsePoint
argument_list|(
name|rightPart
argument_list|,
name|ctx
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidShapeException
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
name|InvalidShapeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Calls {@link #parseRectangle(String, org.locationtech.spatial4j.context.SpatialContext)} and wraps the exception with    * {@link org.apache.solr.common.SolrException} with a helpful message.    */
DECL|method|parseRectangeSolrException
specifier|public
specifier|static
name|Rectangle
name|parseRectangeSolrException
parameter_list|(
name|String
name|externalVal
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
throws|throws
name|SolrException
block|{
try|try
block|{
return|return
name|parseRectangle
argument_list|(
name|externalVal
argument_list|,
name|ctx
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidShapeException
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
name|externalVal
argument_list|)
condition|)
name|message
operator|=
literal|"Can't parse rectangle '"
operator|+
name|externalVal
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
DECL|method|unwrapQuotes
specifier|private
specifier|static
name|String
name|unwrapQuotes
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
operator|>=
literal|2
operator|&&
name|str
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'\"'
operator|&&
name|str
operator|.
name|charAt
argument_list|(
name|str
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'\"'
condition|)
block|{
return|return
name|str
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|str
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
return|return
name|str
return|;
block|}
block|}
end_class
end_unit
