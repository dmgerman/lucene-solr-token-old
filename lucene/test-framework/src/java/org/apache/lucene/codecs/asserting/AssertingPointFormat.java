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
name|PointFormat
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
name|PointReader
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
name|PointWriter
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
name|TestUtil
import|;
end_import
begin_comment
comment|/**  * Just like the default point format but with additional asserts.  */
end_comment
begin_class
DECL|class|AssertingPointFormat
specifier|public
specifier|final
class|class
name|AssertingPointFormat
extends|extends
name|PointFormat
block|{
DECL|field|in
specifier|private
specifier|final
name|PointFormat
name|in
init|=
name|TestUtil
operator|.
name|getDefaultCodec
argument_list|()
operator|.
name|pointFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|fieldsWriter
specifier|public
name|PointWriter
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
name|AssertingPointWriter
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
name|PointReader
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
name|AssertingPointReader
argument_list|(
name|in
operator|.
name|fieldsReader
argument_list|(
name|state
argument_list|)
argument_list|)
return|;
block|}
DECL|class|AssertingPointReader
specifier|static
class|class
name|AssertingPointReader
extends|extends
name|PointReader
block|{
DECL|field|in
specifier|private
specifier|final
name|PointReader
name|in
decl_stmt|;
DECL|method|AssertingPointReader
name|AssertingPointReader
parameter_list|(
name|PointReader
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
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
comment|// TODO: wrap the visitor and make sure things are being reasonable
name|in
operator|.
name|intersect
argument_list|(
name|fieldName
argument_list|,
name|visitor
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
name|PointReader
name|getMergeInstance
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertingPointReader
argument_list|(
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
return|return
name|in
operator|.
name|getBytesPerDimension
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
block|}
DECL|class|AssertingPointWriter
specifier|static
class|class
name|AssertingPointWriter
extends|extends
name|PointWriter
block|{
DECL|field|in
specifier|private
specifier|final
name|PointWriter
name|in
decl_stmt|;
DECL|method|AssertingPointWriter
name|AssertingPointWriter
parameter_list|(
name|SegmentWriteState
name|writeState
parameter_list|,
name|PointWriter
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
name|PointReader
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
