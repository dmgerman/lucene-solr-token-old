begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial.base.shape.simple
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
name|shape
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
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|EqualsBuilder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|HashCodeBuilder
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
name|shape
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * A circle, also known as a point-radius, based on a  * {@link org.apache.lucene.spatial.base.distance.DistanceCalculator} which does all the work. This implementation  * should work for both cartesian 2D and geodetic sphere surfaces.  * Threadsafe& immutable.  */
end_comment
begin_class
DECL|class|CircleImpl
specifier|public
class|class
name|CircleImpl
implements|implements
name|Circle
block|{
DECL|field|point
specifier|protected
specifier|final
name|Point
name|point
decl_stmt|;
DECL|field|distance
specifier|protected
specifier|final
name|double
name|distance
decl_stmt|;
DECL|field|ctx
specifier|protected
specifier|final
name|SpatialContext
name|ctx
decl_stmt|;
comment|/* below is calculated& cached: */
DECL|field|enclosingBox
specifier|protected
specifier|final
name|Rectangle
name|enclosingBox
decl_stmt|;
comment|//we don't have a line shape so we use a rectangle for these axis
DECL|method|CircleImpl
specifier|public
name|CircleImpl
parameter_list|(
name|Point
name|p
parameter_list|,
name|double
name|dist
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
comment|//We assume any normalization / validation of params already occurred (including bounding dist)
name|this
operator|.
name|point
operator|=
name|p
expr_stmt|;
name|this
operator|.
name|distance
operator|=
name|dist
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|enclosingBox
operator|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|calcBoxByDistFromPt
argument_list|(
name|point
argument_list|,
name|distance
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
DECL|method|getCenter
specifier|public
name|Point
name|getCenter
parameter_list|()
block|{
return|return
name|point
return|;
block|}
annotation|@
name|Override
DECL|method|getDistance
specifier|public
name|double
name|getDistance
parameter_list|()
block|{
return|return
name|distance
return|;
block|}
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|point
argument_list|,
name|x
argument_list|,
name|y
argument_list|)
operator|<=
name|distance
return|;
block|}
annotation|@
name|Override
DECL|method|hasArea
specifier|public
name|boolean
name|hasArea
parameter_list|()
block|{
return|return
name|distance
operator|>
literal|0
return|;
block|}
comment|/**    * Note that the bounding box might contain a minX that is> maxX, due to WGS84 dateline.    * @return    */
annotation|@
name|Override
DECL|method|getBoundingBox
specifier|public
name|Rectangle
name|getBoundingBox
parameter_list|()
block|{
return|return
name|enclosingBox
return|;
block|}
annotation|@
name|Override
DECL|method|relate
specifier|public
name|SpatialRelation
name|relate
parameter_list|(
name|Shape
name|other
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|ctx
operator|==
name|ctx
assert|;
comment|//This shortcut was problematic in testing due to distinctions of CONTAINS/WITHIN for no-area shapes (lines, points).
comment|//    if (distance == 0) {
comment|//      return point.relate(other,ctx).intersects() ? SpatialRelation.WITHIN : SpatialRelation.DISJOINT;
comment|//    }
if|if
condition|(
name|other
operator|instanceof
name|Point
condition|)
block|{
return|return
name|relate
argument_list|(
operator|(
name|Point
operator|)
name|other
argument_list|,
name|ctx
argument_list|)
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|Rectangle
condition|)
block|{
return|return
name|relate
argument_list|(
operator|(
name|Rectangle
operator|)
name|other
argument_list|,
name|ctx
argument_list|)
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|Circle
condition|)
block|{
return|return
name|relate
argument_list|(
operator|(
name|Circle
operator|)
name|other
argument_list|,
name|ctx
argument_list|)
return|;
block|}
return|return
name|other
operator|.
name|relate
argument_list|(
name|this
argument_list|,
name|ctx
argument_list|)
operator|.
name|transpose
argument_list|()
return|;
block|}
DECL|method|relate
specifier|public
name|SpatialRelation
name|relate
parameter_list|(
name|Point
name|point
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
return|return
name|contains
argument_list|(
name|point
operator|.
name|getX
argument_list|()
argument_list|,
name|point
operator|.
name|getY
argument_list|()
argument_list|)
condition|?
name|SpatialRelation
operator|.
name|CONTAINS
else|:
name|SpatialRelation
operator|.
name|DISJOINT
return|;
block|}
DECL|method|relate
specifier|public
name|SpatialRelation
name|relate
parameter_list|(
name|Rectangle
name|r
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
comment|//Note: Surprisingly complicated!
comment|//--We start by leveraging the fact we have a calculated bbox that is "cheaper" than use of DistanceCalculator.
specifier|final
name|SpatialRelation
name|bboxSect
init|=
name|enclosingBox
operator|.
name|relate
argument_list|(
name|r
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
if|if
condition|(
name|bboxSect
operator|==
name|SpatialRelation
operator|.
name|DISJOINT
operator|||
name|bboxSect
operator|==
name|SpatialRelation
operator|.
name|WITHIN
condition|)
return|return
name|bboxSect
return|;
elseif|else
if|if
condition|(
name|bboxSect
operator|==
name|SpatialRelation
operator|.
name|CONTAINS
operator|&&
name|enclosingBox
operator|.
name|equals
argument_list|(
name|r
argument_list|)
condition|)
comment|//nasty identity edge-case
return|return
name|SpatialRelation
operator|.
name|WITHIN
return|;
comment|//bboxSect is INTERSECTS or CONTAINS
comment|//The result can be DISJOINT, CONTAINS, or INTERSECTS (not WITHIN)
return|return
name|relateRectanglePhase2
argument_list|(
name|r
argument_list|,
name|bboxSect
argument_list|,
name|ctx
argument_list|)
return|;
block|}
DECL|method|relateRectanglePhase2
specifier|protected
name|SpatialRelation
name|relateRectanglePhase2
parameter_list|(
specifier|final
name|Rectangle
name|r
parameter_list|,
name|SpatialRelation
name|bboxSect
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
comment|/*      !! DOES NOT WORK WITH GEO CROSSING DATELINE OR WORLD-WRAP.      TODO upgrade to handle crossing dateline, but not world-wrap; use some x-shifting code from RectangleImpl.      */
comment|//At this point, the only thing we are certain of is that circle is *NOT* WITHIN r, since the bounding box of a
comment|// circle MUST be within r for the circle to be within r.
comment|//--Quickly determine if they are DISJOINT or not.
comment|//see http://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection/1879223#1879223
specifier|final
name|double
name|closestX
decl_stmt|;
name|double
name|ctr_x
init|=
name|getXAxis
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctr_x
operator|<
name|r
operator|.
name|getMinX
argument_list|()
condition|)
name|closestX
operator|=
name|r
operator|.
name|getMinX
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
name|ctr_x
operator|>
name|r
operator|.
name|getMaxX
argument_list|()
condition|)
name|closestX
operator|=
name|r
operator|.
name|getMaxX
argument_list|()
expr_stmt|;
else|else
name|closestX
operator|=
name|ctr_x
expr_stmt|;
specifier|final
name|double
name|closestY
decl_stmt|;
name|double
name|ctr_y
init|=
name|getYAxis
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctr_y
operator|<
name|r
operator|.
name|getMinY
argument_list|()
condition|)
name|closestY
operator|=
name|r
operator|.
name|getMinY
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
name|ctr_y
operator|>
name|r
operator|.
name|getMaxY
argument_list|()
condition|)
name|closestY
operator|=
name|r
operator|.
name|getMaxY
argument_list|()
expr_stmt|;
else|else
name|closestY
operator|=
name|ctr_y
expr_stmt|;
comment|//Check if there is an intersection from this circle to closestXY
name|boolean
name|didContainOnClosestXY
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|ctr_x
operator|==
name|closestX
condition|)
block|{
name|double
name|deltaY
init|=
name|Math
operator|.
name|abs
argument_list|(
name|ctr_y
operator|-
name|closestY
argument_list|)
decl_stmt|;
name|double
name|distYCirc
init|=
operator|(
name|ctr_y
operator|<
name|closestY
condition|?
name|enclosingBox
operator|.
name|getMaxY
argument_list|()
operator|-
name|ctr_y
else|:
name|ctr_y
operator|-
name|enclosingBox
operator|.
name|getMinY
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|deltaY
operator|>
name|distYCirc
condition|)
return|return
name|SpatialRelation
operator|.
name|DISJOINT
return|;
block|}
elseif|else
if|if
condition|(
name|ctr_y
operator|==
name|closestY
condition|)
block|{
name|double
name|deltaX
init|=
name|Math
operator|.
name|abs
argument_list|(
name|ctr_x
operator|-
name|closestX
argument_list|)
decl_stmt|;
name|double
name|distXCirc
init|=
operator|(
name|ctr_x
operator|<
name|closestX
condition|?
name|enclosingBox
operator|.
name|getMaxX
argument_list|()
operator|-
name|ctr_x
else|:
name|ctr_x
operator|-
name|enclosingBox
operator|.
name|getMinX
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|deltaX
operator|>
name|distXCirc
condition|)
return|return
name|SpatialRelation
operator|.
name|DISJOINT
return|;
block|}
else|else
block|{
comment|//fallback on more expensive calculation
name|didContainOnClosestXY
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|!
name|contains
argument_list|(
name|closestX
argument_list|,
name|closestY
argument_list|)
condition|)
return|return
name|SpatialRelation
operator|.
name|DISJOINT
return|;
block|}
comment|//At this point we know that it's *NOT* DISJOINT, so there is some level of intersection. It's *NOT* WITHIN either.
comment|// The only question left is whether circle CONTAINS r or simply intersects it.
comment|//If circle contains r, then its bbox MUST also CONTAIN r.
if|if
condition|(
name|bboxSect
operator|!=
name|SpatialRelation
operator|.
name|CONTAINS
condition|)
return|return
name|SpatialRelation
operator|.
name|INTERSECTS
return|;
comment|//Find the farthest point of r away from the center of the circle. If that point is contained, then all of r is
comment|// contained.
name|double
name|farthestX
init|=
name|r
operator|.
name|getMaxX
argument_list|()
operator|-
name|ctr_x
operator|>
name|ctr_x
operator|-
name|r
operator|.
name|getMinX
argument_list|()
condition|?
name|r
operator|.
name|getMaxX
argument_list|()
else|:
name|r
operator|.
name|getMinX
argument_list|()
decl_stmt|;
name|double
name|farthestY
init|=
name|r
operator|.
name|getMaxY
argument_list|()
operator|-
name|ctr_y
operator|>
name|ctr_y
operator|-
name|r
operator|.
name|getMinY
argument_list|()
condition|?
name|r
operator|.
name|getMaxY
argument_list|()
else|:
name|r
operator|.
name|getMinY
argument_list|()
decl_stmt|;
if|if
condition|(
name|contains
argument_list|(
name|farthestX
argument_list|,
name|farthestY
argument_list|)
condition|)
return|return
name|SpatialRelation
operator|.
name|CONTAINS
return|;
return|return
name|SpatialRelation
operator|.
name|INTERSECTS
return|;
block|}
comment|/**    * The y axis horizontal of maximal left-right extent of the circle.    */
DECL|method|getYAxis
specifier|protected
name|double
name|getYAxis
parameter_list|()
block|{
return|return
name|point
operator|.
name|getY
argument_list|()
return|;
block|}
DECL|method|getXAxis
specifier|protected
name|double
name|getXAxis
parameter_list|()
block|{
return|return
name|point
operator|.
name|getX
argument_list|()
return|;
block|}
DECL|method|relate
specifier|public
name|SpatialRelation
name|relate
parameter_list|(
name|Circle
name|circle
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
block|{
name|double
name|crossDist
init|=
name|ctx
operator|.
name|getDistCalc
argument_list|()
operator|.
name|distance
argument_list|(
name|point
argument_list|,
name|circle
operator|.
name|getCenter
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|aDist
init|=
name|distance
decl_stmt|,
name|bDist
init|=
name|circle
operator|.
name|getDistance
argument_list|()
decl_stmt|;
if|if
condition|(
name|crossDist
operator|>
name|aDist
operator|+
name|bDist
condition|)
return|return
name|SpatialRelation
operator|.
name|DISJOINT
return|;
if|if
condition|(
name|crossDist
operator|<
name|aDist
operator|&&
name|crossDist
operator|+
name|bDist
operator|<=
name|aDist
condition|)
return|return
name|SpatialRelation
operator|.
name|CONTAINS
return|;
if|if
condition|(
name|crossDist
operator|<
name|bDist
operator|&&
name|crossDist
operator|+
name|aDist
operator|<=
name|bDist
condition|)
return|return
name|SpatialRelation
operator|.
name|WITHIN
return|;
return|return
name|SpatialRelation
operator|.
name|INTERSECTS
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Circle("
operator|+
name|point
operator|+
literal|",d="
operator|+
name|distance
operator|+
literal|')'
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|obj
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CircleImpl
name|rhs
init|=
operator|(
name|CircleImpl
operator|)
name|obj
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|point
argument_list|,
name|rhs
operator|.
name|point
argument_list|)
operator|.
name|append
argument_list|(
name|distance
argument_list|,
name|rhs
operator|.
name|distance
argument_list|)
operator|.
name|append
argument_list|(
name|ctx
argument_list|,
name|rhs
operator|.
name|ctx
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|(
literal|11
argument_list|,
literal|97
argument_list|)
operator|.
name|append
argument_list|(
name|point
argument_list|)
operator|.
name|append
argument_list|(
name|distance
argument_list|)
operator|.
name|append
argument_list|(
name|ctx
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
