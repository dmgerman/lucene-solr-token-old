begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.spatial3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
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
name|index
operator|.
name|PointValues
operator|.
name|IntersectVisitor
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
name|PointValues
operator|.
name|Relation
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoArea
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoAreaFactory
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
name|spatial3d
operator|.
name|geom
operator|.
name|GeoShape
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
name|spatial3d
operator|.
name|geom
operator|.
name|PlanetModel
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
name|DocIdSetBuilder
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
name|NumericUtils
import|;
end_import
begin_class
DECL|class|PointInShapeIntersectVisitor
class|class
name|PointInShapeIntersectVisitor
implements|implements
name|IntersectVisitor
block|{
DECL|field|hits
specifier|private
specifier|final
name|DocIdSetBuilder
name|hits
decl_stmt|;
DECL|field|shape
specifier|private
specifier|final
name|GeoShape
name|shape
decl_stmt|;
DECL|method|PointInShapeIntersectVisitor
specifier|public
name|PointInShapeIntersectVisitor
parameter_list|(
name|DocIdSetBuilder
name|hits
parameter_list|,
name|GeoShape
name|shape
parameter_list|)
block|{
name|this
operator|.
name|hits
operator|=
name|hits
expr_stmt|;
name|this
operator|.
name|shape
operator|=
name|shape
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|hits
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
block|{
assert|assert
name|packedValue
operator|.
name|length
operator|==
literal|12
assert|;
name|double
name|x
init|=
name|Geo3DPoint
operator|.
name|decodeDimension
argument_list|(
name|packedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|y
init|=
name|Geo3DPoint
operator|.
name|decodeDimension
argument_list|(
name|packedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
name|double
name|z
init|=
name|Geo3DPoint
operator|.
name|decodeDimension
argument_list|(
name|packedValue
argument_list|,
literal|2
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
if|if
condition|(
name|shape
operator|.
name|isWithin
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
condition|)
block|{
name|hits
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
block|{
comment|// Because the dimensional format operates in quantized (64 bit -> 32 bit) space, and the cell bounds
comment|// here are inclusive, we need to extend the bounds to the largest un-quantized values that
comment|// could quantize into these bounds.  The encoding (Geo3DUtil.encodeValue) does
comment|// a Math.round from double to long, so e.g. 1.4 -> 1, and -1.4 -> -1:
name|double
name|xMin
init|=
name|Geo3DUtil
operator|.
name|decodeValueFloor
argument_list|(
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|minPackedValue
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|xMax
init|=
name|Geo3DUtil
operator|.
name|decodeValueCeil
argument_list|(
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|maxPackedValue
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|yMin
init|=
name|Geo3DUtil
operator|.
name|decodeValueFloor
argument_list|(
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|minPackedValue
argument_list|,
literal|1
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|yMax
init|=
name|Geo3DUtil
operator|.
name|decodeValueCeil
argument_list|(
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|maxPackedValue
argument_list|,
literal|1
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|zMin
init|=
name|Geo3DUtil
operator|.
name|decodeValueFloor
argument_list|(
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|minPackedValue
argument_list|,
literal|2
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|zMax
init|=
name|Geo3DUtil
operator|.
name|decodeValueCeil
argument_list|(
name|NumericUtils
operator|.
name|sortableBytesToInt
argument_list|(
name|maxPackedValue
argument_list|,
literal|2
operator|*
name|Integer
operator|.
name|BYTES
argument_list|)
argument_list|)
decl_stmt|;
comment|//System.out.println("  compare: x=" + cellXMin + "-" + cellXMax + " y=" + cellYMin + "-" + cellYMax + " z=" + cellZMin + "-" + cellZMax);
assert|assert
name|xMin
operator|<=
name|xMax
assert|;
assert|assert
name|yMin
operator|<=
name|yMax
assert|;
assert|assert
name|zMin
operator|<=
name|zMax
assert|;
name|GeoArea
name|xyzSolid
init|=
name|GeoAreaFactory
operator|.
name|makeGeoArea
argument_list|(
name|PlanetModel
operator|.
name|WGS84
argument_list|,
name|xMin
argument_list|,
name|xMax
argument_list|,
name|yMin
argument_list|,
name|yMax
argument_list|,
name|zMin
argument_list|,
name|zMax
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|xyzSolid
operator|.
name|getRelationship
argument_list|(
name|shape
argument_list|)
condition|)
block|{
case|case
name|GeoArea
operator|.
name|CONTAINS
case|:
comment|// Shape fully contains the cell
comment|//System.out.println("    inside");
return|return
name|Relation
operator|.
name|CELL_INSIDE_QUERY
return|;
case|case
name|GeoArea
operator|.
name|OVERLAPS
case|:
comment|// They do overlap but neither contains the other:
comment|//System.out.println("    crosses1");
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
case|case
name|GeoArea
operator|.
name|WITHIN
case|:
comment|// Cell fully contains the shape:
comment|//System.out.println("    crosses2");
comment|// return Relation.SHAPE_INSIDE_CELL;
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
case|case
name|GeoArea
operator|.
name|DISJOINT
case|:
comment|// They do not overlap at all
comment|//System.out.println("    outside");
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
default|default:
assert|assert
literal|false
assert|;
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
block|}
block|}
end_class
end_unit
