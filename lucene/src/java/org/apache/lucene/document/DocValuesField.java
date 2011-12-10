begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|IndexableFieldType
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
name|DocValue
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
name|DocValues
operator|.
name|Type
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
comment|/**  *<p>  * This class provides a {@link Field} that enables storing of typed  * per-document values for scoring, sorting or value retrieval. Here's an  * example usage, adding an int value:  *   *<pre>  * document.add(new DocValuesField(name).setInt(value));  *</pre>  *   * For optimal performance, re-use the<code>DocValuesField</code> and  * {@link Document} instance for more than one document:  *   *<pre>  *  DocValuesField field = new DocValuesField(name);  *  Document document = new Document();  *  document.add(field);  *   *  for(all documents) {  *    ...  *    field.setInt(value)  *    writer.addDocument(document);  *    ...  *  }  *</pre>  *   *<p>  * If doc values are stored in addition to an indexed ({@link FieldType#setIndexed(boolean)}) or stored  * ({@link FieldType#setStored(boolean)}) value it's recommended to pass the appropriate {@link FieldType}  * when creating the field:  *   *<pre>  *  DocValuesField field = new DocValuesField(name, StringField.TYPE_STORED);  *  Document document = new Document();  *  document.add(field);  *  for(all documents) {  *    ...  *    field.setInt(value)  *    writer.addDocument(document);  *    ...  *  }  *</pre>  *   * */
end_comment
begin_class
DECL|class|DocValuesField
specifier|public
class|class
name|DocValuesField
extends|extends
name|Field
implements|implements
name|DocValue
block|{
DECL|field|bytes
specifier|protected
name|BytesRef
name|bytes
decl_stmt|;
DECL|field|doubleValue
specifier|protected
name|double
name|doubleValue
decl_stmt|;
DECL|field|longValue
specifier|protected
name|long
name|longValue
decl_stmt|;
DECL|field|type
specifier|protected
name|DocValues
operator|.
name|Type
name|type
decl_stmt|;
DECL|field|bytesComparator
specifier|protected
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|bytesComparator
decl_stmt|;
comment|/**    * Creates a new {@link DocValuesField} with the given name.    */
DECL|method|DocValuesField
specifier|public
name|DocValuesField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|new
name|FieldType
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DocValuesField
specifier|public
name|DocValuesField
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|DocValuesField
specifier|public
name|DocValuesField
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|type
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docValue
specifier|public
name|DocValue
name|docValue
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/**    * Sets the given<code>long</code> value and sets the field's {@link Type} to    * {@link Type#VAR_INTS} unless already set. If you want to change the    * default type use {@link #setDocValuesType(DocValues.Type)}.    */
DECL|method|setInt
specifier|public
name|void
name|setInt
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|setInt
argument_list|(
name|value
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the given<code>long</code> value as a 64 bit signed integer.    *     * @param value    *          the value to set    * @param fixed    *          if<code>true</code> {@link Type#FIXED_INTS_64} is used    *          otherwise {@link Type#VAR_INTS}    */
DECL|method|setInt
specifier|public
name|void
name|setInt
parameter_list|(
name|long
name|value
parameter_list|,
name|boolean
name|fixed
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|fixed
condition|?
name|DocValues
operator|.
name|Type
operator|.
name|FIXED_INTS_64
else|:
name|DocValues
operator|.
name|Type
operator|.
name|VAR_INTS
expr_stmt|;
block|}
name|longValue
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Sets the given<code>int</code> value and sets the field's {@link Type} to    * {@link Type#VAR_INTS} unless already set. If you want to change the    * default type use {@link #setDocValuesType(DocValues.Type)}.    */
DECL|method|setInt
specifier|public
name|void
name|setInt
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|setInt
argument_list|(
name|value
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the given<code>int</code> value as a 32 bit signed integer.    *     * @param value    *          the value to set    * @param fixed    *          if<code>true</code> {@link Type#FIXED_INTS_32} is used    *          otherwise {@link Type#VAR_INTS}    */
DECL|method|setInt
specifier|public
name|void
name|setInt
parameter_list|(
name|int
name|value
parameter_list|,
name|boolean
name|fixed
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|fixed
condition|?
name|DocValues
operator|.
name|Type
operator|.
name|FIXED_INTS_32
else|:
name|DocValues
operator|.
name|Type
operator|.
name|VAR_INTS
expr_stmt|;
block|}
name|longValue
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Sets the given<code>short</code> value and sets the field's {@link Type} to    * {@link Type#VAR_INTS} unless already set. If you want to change the    * default type use {@link #setDocValuesType(DocValues.Type)}.    */
DECL|method|setInt
specifier|public
name|void
name|setInt
parameter_list|(
name|short
name|value
parameter_list|)
block|{
name|setInt
argument_list|(
name|value
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the given<code>short</code> value as a 16 bit signed integer.    *     * @param value    *          the value to set    * @param fixed    *          if<code>true</code> {@link Type#FIXED_INTS_16} is used    *          otherwise {@link Type#VAR_INTS}    */
DECL|method|setInt
specifier|public
name|void
name|setInt
parameter_list|(
name|short
name|value
parameter_list|,
name|boolean
name|fixed
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|fixed
condition|?
name|DocValues
operator|.
name|Type
operator|.
name|FIXED_INTS_16
else|:
name|DocValues
operator|.
name|Type
operator|.
name|VAR_INTS
expr_stmt|;
block|}
name|longValue
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Sets the given<code>byte</code> value and sets the field's {@link Type} to    * {@link Type#VAR_INTS} unless already set. If you want to change the    * default type use {@link #setDocValuesType(DocValues.Type)}.    */
DECL|method|setInt
specifier|public
name|void
name|setInt
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|setInt
argument_list|(
name|value
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the given<code>byte</code> value as a 8 bit signed integer.    *     * @param value    *          the value to set    * @param fixed    *          if<code>true</code> {@link Type#FIXED_INTS_8} is used    *          otherwise {@link Type#VAR_INTS}    */
DECL|method|setInt
specifier|public
name|void
name|setInt
parameter_list|(
name|byte
name|value
parameter_list|,
name|boolean
name|fixed
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|fixed
condition|?
name|DocValues
operator|.
name|Type
operator|.
name|FIXED_INTS_8
else|:
name|DocValues
operator|.
name|Type
operator|.
name|VAR_INTS
expr_stmt|;
block|}
name|longValue
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Sets the given<code>float</code> value and sets the field's {@link Type}    * to {@link Type#FLOAT_32} unless already set. If you want to    * change the type use {@link #setDocValuesType(DocValues.Type)}.    */
DECL|method|setFloat
specifier|public
name|void
name|setFloat
parameter_list|(
name|float
name|value
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|DocValues
operator|.
name|Type
operator|.
name|FLOAT_32
expr_stmt|;
block|}
name|doubleValue
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Sets the given<code>double</code> value and sets the field's {@link Type}    * to {@link Type#FLOAT_64} unless already set. If you want to    * change the default type use {@link #setDocValuesType(DocValues.Type)}.    */
DECL|method|setFloat
specifier|public
name|void
name|setFloat
parameter_list|(
name|double
name|value
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|DocValues
operator|.
name|Type
operator|.
name|FLOAT_64
expr_stmt|;
block|}
name|doubleValue
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Sets the given {@link BytesRef} value and the field's {@link Type}. The    * comparator for this field is set to<code>null</code>. If a    *<code>null</code> comparator is set the default comparator for the given    * {@link Type} is used.    */
DECL|method|setBytes
specifier|public
name|void
name|setBytes
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|DocValues
operator|.
name|Type
name|type
parameter_list|)
block|{
name|setBytes
argument_list|(
name|value
argument_list|,
name|type
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the given {@link BytesRef} value, the field's {@link Type} and the    * field's comparator. If the {@link Comparator} is set to<code>null</code>    * the default for the given {@link Type} is used instead.    *     * @throws IllegalArgumentException    *           if the value or the type are null    */
DECL|method|setBytes
specifier|public
name|void
name|setBytes
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|DocValues
operator|.
name|Type
name|type
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value must not be null"
argument_list|)
throw|;
block|}
name|setDocValuesType
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
name|bytes
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bytes
operator|.
name|copyBytes
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|bytesComparator
operator|=
name|comp
expr_stmt|;
block|}
comment|/**    * Returns the set {@link BytesRef} or<code>null</code> if not set.    */
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
comment|/**    * Returns the set {@link BytesRef} comparator or<code>null</code> if not set    */
DECL|method|bytesComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|bytesComparator
parameter_list|()
block|{
return|return
name|bytesComparator
return|;
block|}
comment|/**    * Returns the set floating point value or<code>0.0d</code> if not set.    */
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|()
block|{
return|return
name|doubleValue
return|;
block|}
comment|/**    * Returns the set<code>long</code> value of<code>0</code> if not set.    */
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|()
block|{
return|return
name|longValue
return|;
block|}
comment|/**    * Sets the {@link BytesRef} comparator for this field. If the field has a    * numeric {@link Type} the comparator will be ignored.    */
DECL|method|setBytesComparator
specifier|public
name|void
name|setBytesComparator
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
name|this
operator|.
name|bytesComparator
operator|=
name|comp
expr_stmt|;
block|}
comment|/**    * Sets the {@link Type} for this field.    */
DECL|method|setDocValuesType
specifier|public
name|void
name|setDocValuesType
parameter_list|(
name|DocValues
operator|.
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Type must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**    * Returns always<code>null</code>    */
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|docValueType
specifier|public
name|DocValues
operator|.
name|Type
name|docValueType
parameter_list|()
block|{
return|return
name|type
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
specifier|final
name|String
name|value
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_VAR_SORTED
case|:
comment|// don't use to unicode string this is not necessarily unicode here
name|value
operator|=
literal|"bytes: "
operator|+
name|bytes
operator|.
name|toString
argument_list|()
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
name|value
operator|=
literal|"int16: "
operator|+
name|longValue
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|value
operator|=
literal|"int32: "
operator|+
name|longValue
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
name|value
operator|=
literal|"int64: "
operator|+
name|longValue
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|value
operator|=
literal|"int8: "
operator|+
name|longValue
expr_stmt|;
break|break;
case|case
name|VAR_INTS
case|:
name|value
operator|=
literal|"vint: "
operator|+
name|longValue
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|value
operator|=
literal|"float32: "
operator|+
name|doubleValue
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|value
operator|=
literal|"float64: "
operator|+
name|doubleValue
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown type: "
operator|+
name|type
argument_list|)
throw|;
block|}
return|return
literal|"<"
operator|+
name|name
argument_list|()
operator|+
literal|": DocValuesField "
operator|+
name|value
operator|+
literal|">"
return|;
block|}
comment|/**    * Returns an DocValuesField holding the value from    * the provided string field, as the specified type.  The    * incoming field must have a string value.  The name, {@link    * FieldType} and string value are carried over from the    * incoming Field.    */
DECL|method|build
specifier|public
specifier|static
name|DocValuesField
name|build
parameter_list|(
name|Field
name|field
parameter_list|,
name|DocValues
operator|.
name|Type
name|type
parameter_list|)
block|{
if|if
condition|(
name|field
operator|instanceof
name|DocValuesField
condition|)
block|{
return|return
operator|(
name|DocValuesField
operator|)
name|field
return|;
block|}
specifier|final
name|DocValuesField
name|valField
init|=
operator|new
name|DocValuesField
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
operator|.
name|fieldType
argument_list|()
argument_list|,
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_VAR_SORTED
case|:
name|BytesRef
name|ref
init|=
name|field
operator|.
name|isBinary
argument_list|()
condition|?
name|field
operator|.
name|binaryValue
argument_list|()
else|:
operator|new
name|BytesRef
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
decl_stmt|;
name|valField
operator|.
name|setBytes
argument_list|(
name|ref
argument_list|,
name|type
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|VAR_INTS
case|:
name|valField
operator|.
name|setInt
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
name|valField
operator|.
name|setFloat
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|valField
operator|.
name|setFloat
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown type: "
operator|+
name|type
argument_list|)
throw|;
block|}
return|return
name|valField
return|;
block|}
block|}
end_class
end_unit
