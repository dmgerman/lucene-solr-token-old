begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.base.context.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|context
operator|.
name|simple
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
name|spatial
operator|.
name|base
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
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|distance
operator|.
name|DistanceCalculator
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
name|base
operator|.
name|distance
operator|.
name|DistanceUnits
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
name|base
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
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|Circle
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
name|base
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
name|lucene
operator|.
name|spatial
operator|.
name|base
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
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|base
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
name|spatial
operator|.
name|base
operator|.
name|shape
operator|.
name|simple
operator|.
name|CircleImpl
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
name|base
operator|.
name|shape
operator|.
name|simple
operator|.
name|GeoCircleImpl
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
name|base
operator|.
name|shape
operator|.
name|simple
operator|.
name|PointImpl
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
name|base
operator|.
name|shape
operator|.
name|simple
operator|.
name|RectangleImpl
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
begin_class
DECL|class|SimpleSpatialContext
specifier|public
class|class
name|SimpleSpatialContext
extends|extends
name|SpatialContext
block|{
DECL|field|GEO_KM
specifier|public
specifier|static
name|SimpleSpatialContext
name|GEO_KM
init|=
operator|new
name|SimpleSpatialContext
argument_list|(
name|DistanceUnits
operator|.
name|KILOMETERS
argument_list|)
decl_stmt|;
DECL|method|SimpleSpatialContext
specifier|public
name|SimpleSpatialContext
parameter_list|(
name|DistanceUnits
name|units
parameter_list|)
block|{
name|this
argument_list|(
name|units
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|SimpleSpatialContext
specifier|public
name|SimpleSpatialContext
parameter_list|(
name|DistanceUnits
name|units
parameter_list|,
name|DistanceCalculator
name|calculator
parameter_list|,
name|Rectangle
name|worldBounds
parameter_list|)
block|{
name|super
argument_list|(
name|units
argument_list|,
name|calculator
argument_list|,
name|worldBounds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readShape
specifier|public
name|Shape
name|readShape
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|InvalidShapeException
block|{
name|Shape
name|s
init|=
name|super
operator|.
name|readStandardShape
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidShapeException
argument_list|(
literal|"Unable to read: "
operator|+
name|value
argument_list|)
throw|;
block|}
return|return
name|s
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|Shape
name|shape
parameter_list|)
block|{
if|if
condition|(
name|Point
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|shape
argument_list|)
condition|)
block|{
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|nf
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|nf
operator|.
name|setMaximumFractionDigits
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|nf
operator|.
name|setMinimumFractionDigits
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|Point
name|point
init|=
operator|(
name|Point
operator|)
name|shape
decl_stmt|;
return|return
name|nf
operator|.
name|format
argument_list|(
name|point
operator|.
name|getX
argument_list|()
argument_list|)
operator|+
literal|" "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|point
operator|.
name|getY
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|Rectangle
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|shape
argument_list|)
condition|)
block|{
return|return
name|writeRect
argument_list|(
operator|(
name|Rectangle
operator|)
name|shape
argument_list|)
return|;
block|}
return|return
name|shape
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|makeCircle
specifier|public
name|Circle
name|makeCircle
parameter_list|(
name|Point
name|point
parameter_list|,
name|double
name|distance
parameter_list|)
block|{
if|if
condition|(
name|distance
operator|<
literal|0
condition|)
throw|throw
operator|new
name|InvalidShapeException
argument_list|(
literal|"distance must be>= 0; got "
operator|+
name|distance
argument_list|)
throw|;
if|if
condition|(
name|isGeo
argument_list|()
condition|)
return|return
operator|new
name|GeoCircleImpl
argument_list|(
name|point
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|distance
argument_list|,
name|maxCircleDistance
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
else|else
return|return
operator|new
name|CircleImpl
argument_list|(
name|point
argument_list|,
name|distance
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|makeRect
specifier|public
name|Rectangle
name|makeRect
parameter_list|(
name|double
name|minX
parameter_list|,
name|double
name|maxX
parameter_list|,
name|double
name|minY
parameter_list|,
name|double
name|maxY
parameter_list|)
block|{
comment|//--Normalize parameters
if|if
condition|(
name|isGeo
argument_list|()
condition|)
block|{
name|double
name|delta
init|=
name|calcWidth
argument_list|(
name|minX
argument_list|,
name|maxX
argument_list|)
decl_stmt|;
if|if
condition|(
name|delta
operator|>=
literal|360
condition|)
block|{
comment|//The only way to officially support complete longitude wrap-around is via western longitude = -180. We can't
comment|// support any point because 0 is undifferentiated in sign.
name|minX
operator|=
operator|-
literal|180
expr_stmt|;
name|maxX
operator|=
literal|180
expr_stmt|;
block|}
else|else
block|{
name|minX
operator|=
name|normX
argument_list|(
name|minX
argument_list|)
expr_stmt|;
name|maxX
operator|=
name|normX
argument_list|(
name|maxX
argument_list|)
expr_stmt|;
assert|assert
name|Math
operator|.
name|abs
argument_list|(
name|delta
operator|-
name|calcWidth
argument_list|(
name|minX
argument_list|,
name|maxX
argument_list|)
argument_list|)
operator|<
literal|0.0001
assert|;
comment|//recompute delta; should be the same
block|}
if|if
condition|(
name|minY
operator|>
name|maxY
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxY must be>= minY"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minY
argument_list|<
operator|-
literal|90
operator|||
name|minY
argument_list|>
literal|90
operator|||
name|maxY
argument_list|<
operator|-
literal|90
operator|||
name|maxY
argument_list|>
literal|90
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minY or maxY is outside of -90 to 90 bounds. What did you mean?"
argument_list|)
throw|;
comment|//      debatable what to do in this situation.
comment|//      if (minY< -90) {
comment|//        minX = -180;
comment|//        maxX = 180;
comment|//        maxY = Math.min(90,Math.max(maxY,-90 + (-90 - minY)));
comment|//        minY = -90;
comment|//      }
comment|//      if (maxY> 90) {
comment|//        minX = -180;
comment|//        maxX = 180;
comment|//        minY = Math.max(-90,Math.min(minY,90 - (maxY - 90)));
comment|//        maxY = 90;
comment|//      }
block|}
else|else
block|{
comment|//these normalizations probably won't do anything since it's not geo but should probably call them any way.
name|minX
operator|=
name|normX
argument_list|(
name|minX
argument_list|)
expr_stmt|;
name|maxX
operator|=
name|normX
argument_list|(
name|maxX
argument_list|)
expr_stmt|;
name|minY
operator|=
name|normY
argument_list|(
name|minY
argument_list|)
expr_stmt|;
name|maxY
operator|=
name|normY
argument_list|(
name|maxY
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RectangleImpl
argument_list|(
name|minX
argument_list|,
name|maxX
argument_list|,
name|minY
argument_list|,
name|maxY
argument_list|)
return|;
block|}
DECL|method|calcWidth
specifier|private
name|double
name|calcWidth
parameter_list|(
name|double
name|minX
parameter_list|,
name|double
name|maxX
parameter_list|)
block|{
name|double
name|w
init|=
name|maxX
operator|-
name|minX
decl_stmt|;
if|if
condition|(
name|w
operator|<
literal|0
condition|)
block|{
comment|//only true when minX> maxX (WGS84 assumed)
name|w
operator|+=
literal|360
expr_stmt|;
assert|assert
name|w
operator|>=
literal|0
assert|;
block|}
return|return
name|w
return|;
block|}
annotation|@
name|Override
DECL|method|makePoint
specifier|public
name|Point
name|makePoint
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|)
block|{
return|return
operator|new
name|PointImpl
argument_list|(
name|normX
argument_list|(
name|x
argument_list|)
argument_list|,
name|normY
argument_list|(
name|y
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
