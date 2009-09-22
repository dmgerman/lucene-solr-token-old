begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.tier
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|tier
package|;
end_package
begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import
begin_import
import|import
name|java
operator|.
name|math
operator|.
name|RoundingMode
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|spatial
operator|.
name|geometry
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
name|tier
operator|.
name|projections
operator|.
name|CartesianTierPlotter
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
name|tier
operator|.
name|projections
operator|.
name|IProjector
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
name|tier
operator|.
name|projections
operator|.
name|SinusoidalProjector
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
name|geometry
operator|.
name|LatLng
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
name|geometry
operator|.
name|FloatLatLng
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
name|geometry
operator|.
name|shape
operator|.
name|LLRect
import|;
end_import
begin_comment
comment|/**  *<p><font color="red"><b>NOTE:</b> This API is still in  * flux and might change in incompatible ways in the next  * release.</font>  */
end_comment
begin_class
DECL|class|CartesianPolyFilterBuilder
specifier|public
class|class
name|CartesianPolyFilterBuilder
block|{
comment|// Finer granularity than 1 mile isn't accurate with
comment|// standard java math.  Also, there's already a 2nd
comment|// precise filter, if needed, in DistanceQueryBuilder,
comment|// that will make the filtering exact.
DECL|field|MILES_FLOOR
specifier|public
specifier|static
specifier|final
name|double
name|MILES_FLOOR
init|=
literal|1.0
decl_stmt|;
DECL|field|projector
specifier|private
name|IProjector
name|projector
init|=
operator|new
name|SinusoidalProjector
argument_list|()
decl_stmt|;
DECL|field|log
specifier|private
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|tierPrefix
specifier|private
specifier|final
name|String
name|tierPrefix
decl_stmt|;
DECL|method|CartesianPolyFilterBuilder
specifier|public
name|CartesianPolyFilterBuilder
parameter_list|(
name|String
name|tierPrefix
parameter_list|)
block|{
name|this
operator|.
name|tierPrefix
operator|=
name|tierPrefix
expr_stmt|;
block|}
DECL|method|getBoxShape
specifier|public
name|Shape
name|getBoxShape
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|,
name|double
name|miles
parameter_list|)
block|{
if|if
condition|(
name|miles
operator|<
name|MILES_FLOOR
condition|)
block|{
name|miles
operator|=
name|MILES_FLOOR
expr_stmt|;
block|}
name|Rectangle
name|box
init|=
name|DistanceUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getBoundary
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|miles
argument_list|)
decl_stmt|;
name|LLRect
name|box1
init|=
name|LLRect
operator|.
name|createBox
argument_list|(
operator|new
name|FloatLatLng
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|)
argument_list|,
name|miles
argument_list|,
name|miles
argument_list|)
decl_stmt|;
name|LatLng
name|ll
init|=
name|box1
operator|.
name|getLowerLeft
argument_list|()
decl_stmt|;
name|LatLng
name|ur
init|=
name|box1
operator|.
name|getUpperRight
argument_list|()
decl_stmt|;
name|double
name|latY
init|=
name|ur
operator|.
name|getLat
argument_list|()
decl_stmt|;
name|double
name|latX
init|=
name|ll
operator|.
name|getLat
argument_list|()
decl_stmt|;
name|double
name|longY
init|=
name|ur
operator|.
name|getLng
argument_list|()
decl_stmt|;
name|double
name|longX
init|=
name|ll
operator|.
name|getLng
argument_list|()
decl_stmt|;
name|double
name|longX2
init|=
literal|0.0
decl_stmt|;
if|if
condition|(
name|ur
operator|.
name|getLng
argument_list|()
operator|<
literal|0.0
operator|&&
name|ll
operator|.
name|getLng
argument_list|()
operator|>
literal|0.0
condition|)
block|{
name|longX2
operator|=
name|ll
operator|.
name|getLng
argument_list|()
expr_stmt|;
name|longX
operator|=
operator|-
literal|180.0
expr_stmt|;
block|}
if|if
condition|(
name|ur
operator|.
name|getLng
argument_list|()
operator|>
literal|0.0
operator|&&
name|ll
operator|.
name|getLng
argument_list|()
operator|<
literal|0.0
condition|)
block|{
name|longX2
operator|=
name|ll
operator|.
name|getLng
argument_list|()
expr_stmt|;
name|longX
operator|=
literal|0.0
expr_stmt|;
block|}
comment|//System.err.println("getBoxShape:"+latY+"," + longY);
comment|//System.err.println("getBoxShape:"+latX+"," + longX);
name|CartesianTierPlotter
name|ctp
init|=
operator|new
name|CartesianTierPlotter
argument_list|(
literal|2
argument_list|,
name|projector
argument_list|,
name|tierPrefix
argument_list|)
decl_stmt|;
name|int
name|bestFit
init|=
name|ctp
operator|.
name|bestFit
argument_list|(
name|miles
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Best Fit is : "
operator|+
name|bestFit
argument_list|)
expr_stmt|;
name|ctp
operator|=
operator|new
name|CartesianTierPlotter
argument_list|(
name|bestFit
argument_list|,
name|projector
argument_list|,
name|tierPrefix
argument_list|)
expr_stmt|;
name|Shape
name|shape
init|=
operator|new
name|Shape
argument_list|(
name|ctp
operator|.
name|getTierFieldName
argument_list|()
argument_list|)
decl_stmt|;
comment|// generate shape
comment|// iterate from startX->endX
comment|//     iterate from startY -> endY
comment|//      shape.add(currentLat.currentLong);
name|shape
operator|=
name|getShapeLoop
argument_list|(
name|shape
argument_list|,
name|ctp
argument_list|,
name|latX
argument_list|,
name|longX
argument_list|,
name|latY
argument_list|,
name|longY
argument_list|)
expr_stmt|;
if|if
condition|(
name|longX2
operator|!=
literal|0.0
condition|)
block|{
if|if
condition|(
name|longX2
operator|!=
literal|0.0
condition|)
block|{
if|if
condition|(
name|longX
operator|==
literal|0.0
condition|)
block|{
name|longX
operator|=
name|longX2
expr_stmt|;
name|longY
operator|=
literal|0.0
expr_stmt|;
name|shape
operator|=
name|getShapeLoop
argument_list|(
name|shape
argument_list|,
name|ctp
argument_list|,
name|latX
argument_list|,
name|longX
argument_list|,
name|latY
argument_list|,
name|longY
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|longX
operator|=
name|longX2
expr_stmt|;
name|longY
operator|=
operator|-
literal|180.0
expr_stmt|;
name|shape
operator|=
name|getShapeLoop
argument_list|(
name|shape
argument_list|,
name|ctp
argument_list|,
name|latY
argument_list|,
name|longY
argument_list|,
name|latX
argument_list|,
name|longX
argument_list|)
expr_stmt|;
block|}
block|}
comment|//System.err.println("getBoxShape2:"+latY+"," + longY);
comment|//System.err.println("getBoxShape2:"+latX+"," + longX);
block|}
return|return
name|shape
return|;
block|}
DECL|method|getShapeLoop
specifier|public
name|Shape
name|getShapeLoop
parameter_list|(
name|Shape
name|shape
parameter_list|,
name|CartesianTierPlotter
name|ctp
parameter_list|,
name|double
name|latX
parameter_list|,
name|double
name|longX
parameter_list|,
name|double
name|latY
parameter_list|,
name|double
name|longY
parameter_list|)
block|{
comment|//System.err.println("getShapeLoop:"+latY+"," + longY);
comment|//System.err.println("getShapeLoop:"+latX+"," + longX);
name|double
name|beginAt
init|=
name|ctp
operator|.
name|getTierBoxId
argument_list|(
name|latX
argument_list|,
name|longX
argument_list|)
decl_stmt|;
name|double
name|endAt
init|=
name|ctp
operator|.
name|getTierBoxId
argument_list|(
name|latY
argument_list|,
name|longY
argument_list|)
decl_stmt|;
name|double
name|tierVert
init|=
name|ctp
operator|.
name|getTierVerticalPosDivider
argument_list|()
decl_stmt|;
comment|//System.err.println(" | "+ beginAt+" | "+ endAt);
name|double
name|startX
init|=
name|beginAt
operator|-
operator|(
name|beginAt
operator|%
literal|1
operator|)
decl_stmt|;
name|double
name|startY
init|=
name|beginAt
operator|-
name|startX
decl_stmt|;
comment|//should give a whole number
name|double
name|endX
init|=
name|endAt
operator|-
operator|(
name|endAt
operator|%
literal|1
operator|)
decl_stmt|;
name|double
name|endY
init|=
name|endAt
operator|-
name|endX
decl_stmt|;
comment|//should give a whole number
name|int
name|scale
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|log10
argument_list|(
name|tierVert
argument_list|)
decl_stmt|;
name|endY
operator|=
operator|new
name|BigDecimal
argument_list|(
name|endY
argument_list|)
operator|.
name|setScale
argument_list|(
name|scale
argument_list|,
name|RoundingMode
operator|.
name|HALF_EVEN
argument_list|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
name|startY
operator|=
operator|new
name|BigDecimal
argument_list|(
name|startY
argument_list|)
operator|.
name|setScale
argument_list|(
name|scale
argument_list|,
name|RoundingMode
operator|.
name|HALF_EVEN
argument_list|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|log
operator|.
name|fine
argument_list|(
literal|"scale "
operator|+
name|scale
operator|+
literal|" startX "
operator|+
name|startX
operator|+
literal|" endX "
operator|+
name|endX
operator|+
literal|" startY "
operator|+
name|startY
operator|+
literal|" endY "
operator|+
name|endY
operator|+
literal|" tierVert "
operator|+
name|tierVert
argument_list|)
expr_stmt|;
block|}
name|double
name|xInc
init|=
literal|1.0d
operator|/
name|tierVert
decl_stmt|;
name|xInc
operator|=
operator|new
name|BigDecimal
argument_list|(
name|xInc
argument_list|)
operator|.
name|setScale
argument_list|(
name|scale
argument_list|,
name|RoundingMode
operator|.
name|HALF_EVEN
argument_list|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
comment|//System.err.println("go from startX:"+startX+" to:" + endX);
for|for
control|(
init|;
name|startX
operator|<=
name|endX
condition|;
name|startX
operator|++
control|)
block|{
name|double
name|itY
init|=
name|startY
decl_stmt|;
comment|//System.err.println("go from startY:"+startY+" to:" + endY);
while|while
condition|(
name|itY
operator|<=
name|endY
condition|)
block|{
comment|//create a boxId
comment|// startX.startY
name|double
name|boxId
init|=
name|startX
operator|+
name|itY
decl_stmt|;
name|shape
operator|.
name|addBox
argument_list|(
name|boxId
argument_list|)
expr_stmt|;
comment|//System.err.println("----"+startX+" and "+itY);
comment|//System.err.println("----"+boxId);
name|itY
operator|+=
name|xInc
expr_stmt|;
comment|// java keeps 0.0001 as 1.0E-1
comment|// which ends up as 0.00011111
name|itY
operator|=
operator|new
name|BigDecimal
argument_list|(
name|itY
argument_list|)
operator|.
name|setScale
argument_list|(
name|scale
argument_list|,
name|RoundingMode
operator|.
name|HALF_EVEN
argument_list|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|shape
return|;
block|}
DECL|method|getBoundingArea
specifier|public
name|Filter
name|getBoundingArea
parameter_list|(
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|,
name|double
name|miles
parameter_list|)
block|{
name|Shape
name|shape
init|=
name|getBoxShape
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|miles
argument_list|)
decl_stmt|;
return|return
operator|new
name|CartesianShapeFilter
argument_list|(
name|shape
argument_list|,
name|shape
operator|.
name|getTierId
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
