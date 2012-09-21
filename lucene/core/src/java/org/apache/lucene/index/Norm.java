begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|ByteDocValuesField
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
name|document
operator|.
name|DerefBytesDocValuesField
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
name|document
operator|.
name|DoubleDocValuesField
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FloatDocValuesField
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
name|document
operator|.
name|IntDocValuesField
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
name|document
operator|.
name|LongDocValuesField
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
name|document
operator|.
name|PackedLongDocValuesField
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
name|document
operator|.
name|ShortDocValuesField
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
name|document
operator|.
name|SortedBytesDocValuesField
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
name|document
operator|.
name|StoredField
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
name|document
operator|.
name|StraightBytesDocValuesField
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
operator|.
name|Type
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
name|similarities
operator|.
name|Similarity
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
begin_comment
comment|/**  * Stores the normalization value with {@link StorableField} computed in  * {@link Similarity#computeNorm(FieldInvertState, Norm)} per field.  * Normalization values must be consistent within a single field, different  * value types are not permitted within a single field. All values set must be  * fixed size values ie. all values passed to {@link Norm#setBytes(BytesRef)}  * must have the same length per field.  *   * @lucene.experimental  * @lucene.internal  */
end_comment
begin_class
DECL|class|Norm
specifier|public
specifier|final
class|class
name|Norm
block|{
DECL|field|field
specifier|private
name|StoredField
name|field
decl_stmt|;
DECL|field|spare
specifier|private
name|BytesRef
name|spare
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Norm
specifier|public
name|Norm
parameter_list|()
block|{   }
comment|/**    * Returns the {@link StorableField} representation for this norm    */
DECL|method|field
specifier|public
name|StorableField
name|field
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/**    * Returns the {@link Type} for this norm.    */
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|field
operator|==
literal|null
condition|?
literal|null
else|:
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|docValueType
argument_list|()
return|;
block|}
comment|/**    * Returns a spare {@link BytesRef}     */
DECL|method|getSpare
specifier|public
name|BytesRef
name|getSpare
parameter_list|()
block|{
if|if
condition|(
name|spare
operator|==
literal|null
condition|)
block|{
name|spare
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
return|return
name|spare
return|;
block|}
comment|/**    * Sets a float norm value    */
DECL|method|setFloat
specifier|public
name|void
name|setFloat
parameter_list|(
name|float
name|norm
parameter_list|)
block|{
name|setType
argument_list|(
name|Type
operator|.
name|FLOAT_32
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|.
name|setFloatValue
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets a double norm value    */
DECL|method|setDouble
specifier|public
name|void
name|setDouble
parameter_list|(
name|double
name|norm
parameter_list|)
block|{
name|setType
argument_list|(
name|Type
operator|.
name|FLOAT_64
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|.
name|setDoubleValue
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets a short norm value    */
DECL|method|setShort
specifier|public
name|void
name|setShort
parameter_list|(
name|short
name|norm
parameter_list|)
block|{
name|setType
argument_list|(
name|Type
operator|.
name|FIXED_INTS_16
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|.
name|setShortValue
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets a int norm value    */
DECL|method|setInt
specifier|public
name|void
name|setInt
parameter_list|(
name|int
name|norm
parameter_list|)
block|{
name|setType
argument_list|(
name|Type
operator|.
name|FIXED_INTS_32
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|.
name|setIntValue
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets a long norm value    */
DECL|method|setLong
specifier|public
name|void
name|setLong
parameter_list|(
name|long
name|norm
parameter_list|)
block|{
name|setType
argument_list|(
name|Type
operator|.
name|FIXED_INTS_64
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|.
name|setLongValue
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets a byte norm value    */
DECL|method|setByte
specifier|public
name|void
name|setByte
parameter_list|(
name|byte
name|norm
parameter_list|)
block|{
name|setType
argument_list|(
name|Type
operator|.
name|FIXED_INTS_8
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|.
name|setByteValue
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets a fixed byte array norm value    */
DECL|method|setBytes
specifier|public
name|void
name|setBytes
parameter_list|(
name|BytesRef
name|norm
parameter_list|)
block|{
name|setType
argument_list|(
name|Type
operator|.
name|BYTES_FIXED_STRAIGHT
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|.
name|setBytesValue
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
DECL|method|setType
specifier|private
name|void
name|setType
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|type
operator|!=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|docValueType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"FieldType missmatch - expected "
operator|+
name|type
operator|+
literal|" but was "
operator|+
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|docValueType
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|VAR_INTS
case|:
name|field
operator|=
operator|new
name|PackedLongDocValuesField
argument_list|(
literal|""
argument_list|,
operator|(
name|long
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|field
operator|=
operator|new
name|ByteDocValuesField
argument_list|(
literal|""
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
name|field
operator|=
operator|new
name|ShortDocValuesField
argument_list|(
literal|""
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|field
operator|=
operator|new
name|IntDocValuesField
argument_list|(
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
name|field
operator|=
operator|new
name|LongDocValuesField
argument_list|(
literal|""
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|field
operator|=
operator|new
name|FloatDocValuesField
argument_list|(
literal|""
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|field
operator|=
operator|new
name|DoubleDocValuesField
argument_list|(
literal|""
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
name|field
operator|=
operator|new
name|StraightBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_STRAIGHT
case|:
name|field
operator|=
operator|new
name|StraightBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_DEREF
case|:
name|field
operator|=
operator|new
name|DerefBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_DEREF
case|:
name|field
operator|=
operator|new
name|DerefBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_SORTED
case|:
name|field
operator|=
operator|new
name|SortedBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_SORTED
case|:
name|field
operator|=
operator|new
name|SortedBytesDocValuesField
argument_list|(
literal|""
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown Type: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
end_unit
