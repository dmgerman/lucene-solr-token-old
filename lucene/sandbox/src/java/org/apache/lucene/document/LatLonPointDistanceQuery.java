begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DocValues
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
name|FieldInfo
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
name|LeafReader
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
name|LeafReaderContext
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
name|SortedNumericDocValues
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
name|search
operator|.
name|ConstantScoreScorer
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
name|ConstantScoreWeight
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|IndexSearcher
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
name|Scorer
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
name|TwoPhaseIterator
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
name|Weight
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
name|GeoRect
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
name|GeoUtils
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
name|BitSet
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
name|FixedBitSet
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
name|SloppyMath
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
name|SparseFixedBitSet
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
name|StringHelper
import|;
end_import
begin_comment
comment|/**  * Distance query for {@link LatLonPoint}.  */
end_comment
begin_class
DECL|class|LatLonPointDistanceQuery
specifier|final
class|class
name|LatLonPointDistanceQuery
extends|extends
name|Query
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|latitude
specifier|final
name|double
name|latitude
decl_stmt|;
DECL|field|longitude
specifier|final
name|double
name|longitude
decl_stmt|;
DECL|field|radiusMeters
specifier|final
name|double
name|radiusMeters
decl_stmt|;
DECL|method|LatLonPointDistanceQuery
specifier|public
name|LatLonPointDistanceQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|double
name|latitude
parameter_list|,
name|double
name|longitude
parameter_list|,
name|double
name|radiusMeters
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Double
operator|.
name|isFinite
argument_list|(
name|radiusMeters
argument_list|)
operator|==
literal|false
operator|||
name|radiusMeters
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"radiusMeters: '"
operator|+
name|radiusMeters
operator|+
literal|"' is invalid"
argument_list|)
throw|;
block|}
name|GeoUtils
operator|.
name|checkLatitude
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|checkLongitude
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|latitude
operator|=
name|latitude
expr_stmt|;
name|this
operator|.
name|longitude
operator|=
name|longitude
expr_stmt|;
name|this
operator|.
name|radiusMeters
operator|=
name|radiusMeters
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
name|GeoRect
name|box
init|=
name|GeoUtils
operator|.
name|circleToBBox
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|radiusMeters
argument_list|)
decl_stmt|;
comment|// create bounding box(es) for the distance range
comment|// these are pre-encoded with LatLonPoint's encoding
specifier|final
name|byte
name|minLat
index|[]
init|=
operator|new
name|byte
index|[
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
specifier|final
name|byte
name|maxLat
index|[]
init|=
operator|new
name|byte
index|[
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
specifier|final
name|byte
name|minLon
index|[]
init|=
operator|new
name|byte
index|[
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
specifier|final
name|byte
name|maxLon
index|[]
init|=
operator|new
name|byte
index|[
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
comment|// second set of longitude ranges to check (for cross-dateline case)
specifier|final
name|byte
name|minLon2
index|[]
init|=
operator|new
name|byte
index|[
name|Integer
operator|.
name|BYTES
index|]
decl_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
name|box
operator|.
name|minLat
argument_list|)
argument_list|,
name|minLat
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|LatLonPoint
operator|.
name|encodeLatitude
argument_list|(
name|box
operator|.
name|maxLat
argument_list|)
argument_list|,
name|maxLat
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// crosses dateline: split
if|if
condition|(
name|box
operator|.
name|crossesDateline
argument_list|()
condition|)
block|{
comment|// box1
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
name|minLon
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|box
operator|.
name|maxLon
argument_list|)
argument_list|,
name|maxLon
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// box2
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|box
operator|.
name|minLon
argument_list|)
argument_list|,
name|minLon2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|box
operator|.
name|minLon
argument_list|)
argument_list|,
name|minLon
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|LatLonPoint
operator|.
name|encodeLongitude
argument_list|(
name|box
operator|.
name|maxLon
argument_list|)
argument_list|,
name|maxLon
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// disable box2
name|NumericUtils
operator|.
name|intToSortableBytes
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|minLon2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// compute a maximum partial haversin: unless our box is crazy, we can use this bound
comment|// to reject edge cases faster in matches()
specifier|final
name|double
name|maxPartialDistance
decl_stmt|;
if|if
condition|(
name|box
operator|.
name|maxLon
operator|-
name|longitude
operator|<
literal|90
operator|&&
name|longitude
operator|-
name|box
operator|.
name|minLon
operator|<
literal|90
condition|)
block|{
name|maxPartialDistance
operator|=
name|Math
operator|.
name|max
argument_list|(
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|latitude
argument_list|,
name|box
operator|.
name|maxLon
argument_list|)
argument_list|,
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|box
operator|.
name|maxLat
argument_list|,
name|longitude
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|maxPartialDistance
operator|=
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
block|}
specifier|final
name|double
name|axisLat
init|=
name|GeoUtils
operator|.
name|axisLat
argument_list|(
name|latitude
argument_list|,
name|radiusMeters
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|PointValues
name|values
init|=
name|reader
operator|.
name|getPointValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment had any points fields
return|return
literal|null
return|;
block|}
name|FieldInfo
name|fieldInfo
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|==
literal|null
condition|)
block|{
comment|// No docs in this segment indexed this field at all
return|return
literal|null
return|;
block|}
name|LatLonPoint
operator|.
name|checkCompatible
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
comment|// approximation (postfiltering has not yet been applied)
name|DocIdSetBuilder
name|result
init|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
comment|// subset of documents that need no postfiltering, this is purely an optimization
specifier|final
name|BitSet
name|preApproved
decl_stmt|;
comment|// dumb heuristic: if the field is really sparse, use a sparse impl
if|if
condition|(
name|values
operator|.
name|getDocCount
argument_list|(
name|field
argument_list|)
operator|*
literal|100L
operator|<
name|reader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|preApproved
operator|=
operator|new
name|SparseFixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|preApproved
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|values
operator|.
name|intersect
argument_list|(
name|field
argument_list|,
operator|new
name|IntersectVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|result
operator|.
name|grow
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|result
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|preApproved
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
comment|// we bounds check individual values, as subtrees may cross, but we are being sent the values anyway:
comment|// this reduces the amount of docvalues fetches (improves approximation)
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|packedValue
argument_list|,
literal|0
argument_list|,
name|maxLat
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|packedValue
argument_list|,
literal|0
argument_list|,
name|minLat
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// latitude out of bounding box range
return|return;
block|}
if|if
condition|(
operator|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|packedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|maxLon
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|packedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|minLon
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
operator|)
operator|&&
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|packedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|minLon2
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// longitude out of bounding box range
return|return;
block|}
name|result
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
comment|// algorithm: we create a bounding box (two bounding boxes if we cross the dateline).
comment|// 1. check our bounding box(es) first. if the subtree is entirely outside of those, bail.
comment|// 2. check if the subtree is disjoint. it may cross the bounding box but not intersect with circle
comment|// 3. see if the subtree is fully contained. if the subtree is enormous along the x axis, wrapping half way around the world, etc: then this can't work, just go to step 3.
comment|// 4. recurse naively (subtrees crossing over circle edge)
annotation|@
name|Override
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
if|if
condition|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|minPackedValue
argument_list|,
literal|0
argument_list|,
name|maxLat
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|maxPackedValue
argument_list|,
literal|0
argument_list|,
name|minLat
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// latitude out of bounding box range
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
if|if
condition|(
operator|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|minPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|maxLon
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
operator|||
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|maxPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|minLon
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
operator|)
operator|&&
name|StringHelper
operator|.
name|compare
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|maxPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|,
name|minLon2
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// longitude out of bounding box range
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
name|double
name|latMin
init|=
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|minPackedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|lonMin
init|=
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|minPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
name|double
name|latMax
init|=
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
name|maxPackedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|double
name|lonMax
init|=
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
name|maxPackedValue
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|longitude
argument_list|<
name|lonMin
operator|||
name|longitude
argument_list|>
name|lonMax
operator|)
operator|&&
operator|(
name|axisLat
operator|+
name|GeoUtils
operator|.
name|AXISLAT_ERROR
argument_list|<
name|latMin
operator|||
name|axisLat
operator|-
name|GeoUtils
operator|.
name|AXISLAT_ERROR
argument_list|>
name|latMax
operator|)
condition|)
block|{
comment|// circle not fully inside / crossing axis
if|if
condition|(
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|latMin
argument_list|,
name|lonMin
argument_list|)
operator|>
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|latMin
argument_list|,
name|lonMax
argument_list|)
operator|>
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|latMax
argument_list|,
name|lonMin
argument_list|)
operator|>
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|latMax
argument_list|,
name|lonMax
argument_list|)
operator|>
name|radiusMeters
condition|)
block|{
comment|// no points inside
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
block|}
if|if
condition|(
name|lonMax
operator|-
name|longitude
operator|<
literal|90
operator|&&
name|longitude
operator|-
name|lonMin
operator|<
literal|90
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|latMin
argument_list|,
name|lonMin
argument_list|)
operator|<=
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|latMin
argument_list|,
name|lonMax
argument_list|)
operator|<=
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|latMax
argument_list|,
name|lonMin
argument_list|)
operator|<=
name|radiusMeters
operator|&&
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|latMax
argument_list|,
name|lonMax
argument_list|)
operator|<=
name|radiusMeters
condition|)
block|{
comment|// we are fully enclosed, collect everything within this subtree
return|return
name|Relation
operator|.
name|CELL_INSIDE_QUERY
return|;
block|}
else|else
block|{
comment|// recurse: its inside our bounding box(es), but not fully, or it wraps around.
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|DocIdSet
name|set
init|=
name|result
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|disi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// return two-phase iterator using docvalues to postfilter candidates
name|SortedNumericDocValues
name|docValues
init|=
name|DocValues
operator|.
name|getSortedNumeric
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|TwoPhaseIterator
name|iterator
init|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|disi
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|docId
init|=
name|disi
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|preApproved
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|docValues
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|docValues
operator|.
name|count
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|long
name|encoded
init|=
name|docValues
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|double
name|docLatitude
init|=
name|LatLonPoint
operator|.
name|decodeLatitude
argument_list|(
call|(
name|int
call|)
argument_list|(
name|encoded
operator|>>
literal|32
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|docLongitude
init|=
name|LatLonPoint
operator|.
name|decodeLongitude
argument_list|(
call|(
name|int
call|)
argument_list|(
name|encoded
operator|&
literal|0xFFFFFFFF
argument_list|)
argument_list|)
decl_stmt|;
comment|// first check the partial distance, if its more than that, it can't be<= radiusMeters
name|double
name|h1
init|=
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|latitude
argument_list|,
name|longitude
argument_list|,
name|docLatitude
argument_list|,
name|docLongitude
argument_list|)
decl_stmt|;
if|if
condition|(
name|h1
operator|>
name|maxPartialDistance
condition|)
block|{
continue|continue;
block|}
comment|// fully confirm with part 2:
if|if
condition|(
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|h1
argument_list|)
operator|<=
name|radiusMeters
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
literal|20
return|;
comment|// TODO: make this fancier
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|iterator
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|getLatitude
specifier|public
name|double
name|getLatitude
parameter_list|()
block|{
return|return
name|latitude
return|;
block|}
DECL|method|getLongitude
specifier|public
name|double
name|getLongitude
parameter_list|()
block|{
return|return
name|longitude
return|;
block|}
DECL|method|getRadiusMeters
specifier|public
name|double
name|getRadiusMeters
parameter_list|()
block|{
return|return
name|radiusMeters
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
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|field
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|radiusMeters
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
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
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|LatLonPointDistanceQuery
name|other
init|=
operator|(
name|LatLonPointDistanceQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
operator|==
literal|false
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|latitude
argument_list|)
operator|!=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|latitude
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|longitude
argument_list|)
operator|!=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|longitude
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|radiusMeters
argument_list|)
operator|!=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|radiusMeters
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|longitude
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" +/- "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|radiusMeters
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" meters"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
