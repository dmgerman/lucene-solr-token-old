begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.asserting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|asserting
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|codecs
operator|.
name|PointsFormat
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
name|codecs
operator|.
name|PointsReader
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
name|codecs
operator|.
name|PointsWriter
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
name|MergeState
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
name|SegmentReadState
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
name|SegmentWriteState
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
name|Accountable
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
name|BytesRef
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
name|TestUtil
import|;
end_import
begin_comment
comment|/**  * Just like the default point format but with additional asserts.  */
end_comment
begin_class
DECL|class|AssertingPointsFormat
specifier|public
specifier|final
class|class
name|AssertingPointsFormat
extends|extends
name|PointsFormat
block|{
DECL|field|in
specifier|private
specifier|final
name|PointsFormat
name|in
decl_stmt|;
comment|/** Create a new AssertingPointsFormat */
DECL|method|AssertingPointsFormat
specifier|public
name|AssertingPointsFormat
parameter_list|()
block|{
name|this
argument_list|(
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
operator|.
name|pointsFormat
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: Create an AssertingPointsFormat.    * This is only intended to pass special parameters for testing.    */
comment|// TODO: can we randomize this a cleaner way? e.g. stored fields and vectors do
comment|// this with a separate codec...
DECL|method|AssertingPointsFormat
specifier|public
name|AssertingPointsFormat
parameter_list|(
name|PointsFormat
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsWriter
specifier|public
name|PointsWriter
name|fieldsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingPointsWriter
argument_list|(
name|state
argument_list|,
name|in
operator|.
name|fieldsWriter
argument_list|(
name|state
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsReader
specifier|public
name|PointsReader
name|fieldsReader
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingPointsReader
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|in
operator|.
name|fieldsReader
argument_list|(
name|state
argument_list|)
argument_list|)
return|;
block|}
comment|/** Validates in the 1D case that all points are visited in order, and point values are in bounds of the last cell checked */
DECL|class|AssertingIntersectVisitor
specifier|static
class|class
name|AssertingIntersectVisitor
implements|implements
name|IntersectVisitor
block|{
DECL|field|in
specifier|final
name|IntersectVisitor
name|in
decl_stmt|;
DECL|field|numDims
specifier|final
name|int
name|numDims
decl_stmt|;
DECL|field|bytesPerDim
specifier|final
name|int
name|bytesPerDim
decl_stmt|;
DECL|field|lastDocValue
specifier|final
name|byte
index|[]
name|lastDocValue
decl_stmt|;
DECL|field|lastMinPackedValue
specifier|final
name|byte
index|[]
name|lastMinPackedValue
decl_stmt|;
DECL|field|lastMaxPackedValue
specifier|final
name|byte
index|[]
name|lastMaxPackedValue
decl_stmt|;
DECL|field|lastCompareResult
specifier|private
name|Relation
name|lastCompareResult
decl_stmt|;
DECL|field|lastDocID
specifier|private
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|docBudget
specifier|private
name|int
name|docBudget
decl_stmt|;
DECL|method|AssertingIntersectVisitor
specifier|public
name|AssertingIntersectVisitor
parameter_list|(
name|int
name|numDims
parameter_list|,
name|int
name|bytesPerDim
parameter_list|,
name|IntersectVisitor
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|numDims
operator|=
name|numDims
expr_stmt|;
name|this
operator|.
name|bytesPerDim
operator|=
name|bytesPerDim
expr_stmt|;
name|lastMaxPackedValue
operator|=
operator|new
name|byte
index|[
name|numDims
operator|*
name|bytesPerDim
index|]
expr_stmt|;
name|lastMinPackedValue
operator|=
operator|new
name|byte
index|[
name|numDims
operator|*
name|bytesPerDim
index|]
expr_stmt|;
if|if
condition|(
name|numDims
operator|==
literal|1
condition|)
block|{
name|lastDocValue
operator|=
operator|new
name|byte
index|[
name|bytesPerDim
index|]
expr_stmt|;
block|}
else|else
block|{
name|lastDocValue
operator|=
literal|null
expr_stmt|;
block|}
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
throws|throws
name|IOException
block|{
assert|assert
operator|--
name|docBudget
operator|>=
literal|0
operator|:
literal|"called add() more times than the last call to grow() reserved"
assert|;
comment|// This method, not filtering each hit, should only be invoked when the cell is inside the query shape:
assert|assert
name|lastCompareResult
operator|==
name|Relation
operator|.
name|CELL_INSIDE_QUERY
assert|;
name|in
operator|.
name|visit
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
throws|throws
name|IOException
block|{
assert|assert
operator|--
name|docBudget
operator|>=
literal|0
operator|:
literal|"called add() more times than the last call to grow() reserved"
assert|;
comment|// This method, to filter each doc's value, should only be invoked when the cell crosses the query shape:
assert|assert
name|lastCompareResult
operator|==
name|PointValues
operator|.
name|Relation
operator|.
name|CELL_CROSSES_QUERY
assert|;
comment|// This doc's packed value should be contained in the last cell passed to compare:
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
assert|assert
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|lastMinPackedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|packedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|)
operator|<=
literal|0
operator|:
literal|"dim="
operator|+
name|dim
operator|+
literal|" of "
operator|+
name|numDims
operator|+
literal|" value="
operator|+
operator|new
name|BytesRef
argument_list|(
name|packedValue
argument_list|)
assert|;
assert|assert
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|lastMaxPackedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|packedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|)
operator|>=
literal|0
operator|:
literal|"dim="
operator|+
name|dim
operator|+
literal|" of "
operator|+
name|numDims
operator|+
literal|" value="
operator|+
operator|new
name|BytesRef
argument_list|(
name|packedValue
argument_list|)
assert|;
block|}
comment|// TODO: we should assert that this "matches" whatever relation the last call to compare had returned
assert|assert
name|packedValue
operator|.
name|length
operator|==
name|numDims
operator|*
name|bytesPerDim
assert|;
if|if
condition|(
name|numDims
operator|==
literal|1
condition|)
block|{
name|int
name|cmp
init|=
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|lastDocValue
argument_list|,
literal|0
argument_list|,
name|packedValue
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
comment|// ok
block|}
elseif|else
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
assert|assert
name|lastDocID
operator|<=
name|docID
operator|:
literal|"doc ids are out of order when point values are the same!"
assert|;
block|}
else|else
block|{
comment|// out of order!
assert|assert
literal|false
operator|:
literal|"point values are out of order"
assert|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|packedValue
argument_list|,
literal|0
argument_list|,
name|lastDocValue
argument_list|,
literal|0
argument_list|,
name|bytesPerDim
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|visit
argument_list|(
name|docID
argument_list|,
name|packedValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|grow
specifier|public
name|void
name|grow
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|in
operator|.
name|grow
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|docBudget
operator|=
name|count
expr_stmt|;
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
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|numDims
condition|;
name|dim
operator|++
control|)
block|{
assert|assert
name|StringHelper
operator|.
name|compare
argument_list|(
name|bytesPerDim
argument_list|,
name|minPackedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|,
name|maxPackedValue
argument_list|,
name|dim
operator|*
name|bytesPerDim
argument_list|)
operator|<=
literal|0
assert|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|maxPackedValue
argument_list|,
literal|0
argument_list|,
name|lastMaxPackedValue
argument_list|,
literal|0
argument_list|,
name|numDims
operator|*
name|bytesPerDim
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|minPackedValue
argument_list|,
literal|0
argument_list|,
name|lastMinPackedValue
argument_list|,
literal|0
argument_list|,
name|numDims
operator|*
name|bytesPerDim
argument_list|)
expr_stmt|;
name|lastCompareResult
operator|=
name|in
operator|.
name|compare
argument_list|(
name|minPackedValue
argument_list|,
name|maxPackedValue
argument_list|)
expr_stmt|;
return|return
name|lastCompareResult
return|;
block|}
block|}
DECL|class|AssertingPointsReader
specifier|static
class|class
name|AssertingPointsReader
extends|extends
name|PointsReader
block|{
DECL|field|in
specifier|private
specifier|final
name|PointsReader
name|in
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|AssertingPointsReader
name|AssertingPointsReader
parameter_list|(
name|int
name|maxDoc
parameter_list|,
name|PointsReader
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
comment|// do a few simple checks on init
assert|assert
name|toString
argument_list|()
operator|!=
literal|null
assert|;
assert|assert
name|ramBytesUsed
argument_list|()
operator|>=
literal|0
assert|;
assert|assert
name|getChildResources
argument_list|()
operator|!=
literal|null
assert|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close again
block|}
annotation|@
name|Override
DECL|method|intersect
specifier|public
name|void
name|intersect
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|intersect
argument_list|(
name|fieldName
argument_list|,
operator|new
name|AssertingIntersectVisitor
argument_list|(
name|in
operator|.
name|getNumDimensions
argument_list|(
name|fieldName
argument_list|)
argument_list|,
name|in
operator|.
name|getBytesPerDimension
argument_list|(
name|fieldName
argument_list|)
argument_list|,
name|visitor
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|v
init|=
name|in
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
assert|assert
name|v
operator|>=
literal|0
assert|;
return|return
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|res
init|=
name|in
operator|.
name|getChildResources
argument_list|()
decl_stmt|;
name|TestUtil
operator|.
name|checkReadOnly
argument_list|(
name|res
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMergeInstance
specifier|public
name|PointsReader
name|getMergeInstance
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingPointsReader
argument_list|(
name|maxDoc
argument_list|,
name|in
operator|.
name|getMergeInstance
argument_list|()
argument_list|)
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|in
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getMinPackedValue
specifier|public
name|byte
index|[]
name|getMinPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|assertStats
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|getMinPackedValue
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxPackedValue
specifier|public
name|byte
index|[]
name|getMaxPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|assertStats
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|getMaxPackedValue
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDimensions
specifier|public
name|int
name|getNumDimensions
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|assertStats
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|getNumDimensions
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesPerDimension
specifier|public
name|int
name|getBytesPerDimension
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
block|{
name|assertStats
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|getBytesPerDimension
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|assertStats
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|size
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|assertStats
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|in
operator|.
name|getDocCount
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
DECL|method|assertStats
specifier|private
name|void
name|assertStats
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
assert|assert
name|in
operator|.
name|size
argument_list|(
name|fieldName
argument_list|)
operator|>=
literal|0
assert|;
assert|assert
name|in
operator|.
name|getDocCount
argument_list|(
name|fieldName
argument_list|)
operator|>=
literal|0
assert|;
assert|assert
name|in
operator|.
name|getDocCount
argument_list|(
name|fieldName
argument_list|)
operator|<=
name|in
operator|.
name|size
argument_list|(
name|fieldName
argument_list|)
assert|;
assert|assert
name|in
operator|.
name|getDocCount
argument_list|(
name|fieldName
argument_list|)
operator|<=
name|maxDoc
assert|;
block|}
block|}
DECL|class|AssertingPointsWriter
specifier|static
class|class
name|AssertingPointsWriter
extends|extends
name|PointsWriter
block|{
DECL|field|in
specifier|private
specifier|final
name|PointsWriter
name|in
decl_stmt|;
DECL|method|AssertingPointsWriter
name|AssertingPointsWriter
parameter_list|(
name|SegmentWriteState
name|writeState
parameter_list|,
name|PointsWriter
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeField
specifier|public
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|PointsReader
name|values
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|getPointDimensionCount
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"writing field=\""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" but pointDimensionalCount is 0"
argument_list|)
throw|;
block|}
name|in
operator|.
name|writeField
argument_list|(
name|fieldInfo
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close again
block|}
block|}
block|}
end_class
end_unit
