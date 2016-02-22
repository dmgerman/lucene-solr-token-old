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
name|RamUsageEstimator
import|;
end_import
begin_comment
comment|/** A long field that is indexed dimensionally such that finding  *  all documents within an N-dimensional shape or range at search time is  *  efficient.  Multiple values for the same field in one documents  *  is allowed. */
end_comment
begin_class
DECL|class|LongPoint
specifier|public
specifier|final
class|class
name|LongPoint
extends|extends
name|Field
block|{
DECL|method|getType
specifier|private
specifier|static
name|FieldType
name|getType
parameter_list|(
name|int
name|numDims
parameter_list|)
block|{
name|FieldType
name|type
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|type
operator|.
name|setDimensions
argument_list|(
name|numDims
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
argument_list|)
expr_stmt|;
name|type
operator|.
name|freeze
argument_list|()
expr_stmt|;
return|return
name|type
return|;
block|}
annotation|@
name|Override
DECL|method|setLongValue
specifier|public
name|void
name|setLongValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|setLongValues
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Change the values of this field */
DECL|method|setLongValues
specifier|public
name|void
name|setLongValues
parameter_list|(
name|long
modifier|...
name|point
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|!=
name|point
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this field (name="
operator|+
name|name
operator|+
literal|") uses "
operator|+
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|+
literal|" dimensions; cannot change to (incoming) "
operator|+
name|point
operator|.
name|length
operator|+
literal|" dimensions"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|pack
argument_list|(
name|point
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setBytesValue
specifier|public
name|void
name|setBytesValue
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change value type from long to BytesRef"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
block|{
if|if
condition|(
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this field (name="
operator|+
name|name
operator|+
literal|") uses "
operator|+
name|type
operator|.
name|pointDimensionCount
argument_list|()
operator|+
literal|" dimensions; cannot convert to a single numeric value"
argument_list|)
throw|;
block|}
name|BytesRef
name|bytes
init|=
operator|(
name|BytesRef
operator|)
name|fieldsData
decl_stmt|;
assert|assert
name|bytes
operator|.
name|length
operator|==
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
assert|;
return|return
name|NumericUtils
operator|.
name|bytesToLong
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|)
return|;
block|}
DECL|method|pack
specifier|private
specifier|static
name|BytesRef
name|pack
parameter_list|(
name|long
modifier|...
name|point
parameter_list|)
block|{
if|if
condition|(
name|point
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|point
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point cannot be 0 dimensions"
argument_list|)
throw|;
block|}
name|byte
index|[]
name|packed
init|=
operator|new
name|byte
index|[
name|point
operator|.
name|length
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
index|]
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|point
operator|.
name|length
condition|;
name|dim
operator|++
control|)
block|{
name|NumericUtils
operator|.
name|longToBytes
argument_list|(
name|point
index|[
name|dim
index|]
argument_list|,
name|packed
argument_list|,
name|dim
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BytesRef
argument_list|(
name|packed
argument_list|)
return|;
block|}
comment|/** Creates a new LongPoint, indexing the    *  provided N-dimensional int point.    *    *  @param name field name    *  @param point int[] value    *  @throws IllegalArgumentException if the field name or value is null.    */
DECL|method|LongPoint
specifier|public
name|LongPoint
parameter_list|(
name|String
name|name
parameter_list|,
name|long
modifier|...
name|point
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|pack
argument_list|(
name|point
argument_list|)
argument_list|,
name|getType
argument_list|(
name|point
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// public helper methods (e.g. for queries)
comment|// TODO: try to rectify with pack() above, which works on a single concatenated array...
comment|/** Encode n-dimensional long values into binary encoding */
DECL|method|encode
specifier|public
specifier|static
name|byte
index|[]
index|[]
name|encode
parameter_list|(
name|Long
name|value
index|[]
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|encoded
init|=
operator|new
name|byte
index|[
name|value
operator|.
name|length
index|]
index|[]
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
name|value
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|value
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|encoded
index|[
name|i
index|]
operator|=
name|encodeDimension
argument_list|(
name|value
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|encoded
return|;
block|}
comment|/** Encode single long dimension */
DECL|method|encodeDimension
specifier|public
specifier|static
name|byte
index|[]
name|encodeDimension
parameter_list|(
name|Long
name|value
parameter_list|)
block|{
name|byte
name|encoded
index|[]
init|=
operator|new
name|byte
index|[
name|Long
operator|.
name|BYTES
index|]
decl_stmt|;
name|NumericUtils
operator|.
name|longToBytes
argument_list|(
name|value
argument_list|,
name|encoded
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|encoded
return|;
block|}
comment|/** Decode single long dimension */
DECL|method|decodeDimension
specifier|public
specifier|static
name|Long
name|decodeDimension
parameter_list|(
name|byte
name|value
index|[]
parameter_list|)
block|{
return|return
name|NumericUtils
operator|.
name|bytesToLong
argument_list|(
name|value
argument_list|,
literal|0
argument_list|)
return|;
block|}
block|}
end_class
end_unit
