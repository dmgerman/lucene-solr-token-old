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
name|IndexReader
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
name|index
operator|.
name|StorableField
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/** A field whose value is stored so that {@link  *  IndexSearcher#doc} and {@link IndexReader#document} will  *  return the field and its value. */
end_comment
begin_class
DECL|class|StoredField
specifier|public
class|class
name|StoredField
extends|extends
name|Field
block|{
comment|/**    * Type for a stored-only field.    */
DECL|field|TYPE
specifier|public
specifier|final
specifier|static
name|FieldType
name|TYPE
decl_stmt|;
static|static
block|{
name|TYPE
operator|=
operator|new
name|FieldType
argument_list|()
expr_stmt|;
name|TYPE
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a stored-only field with the given binary value.    *<p>NOTE: the provided byte[] is not copied so be sure    * not to change it until you're done with this field.    * @param name field name    * @param value byte array pointing to binary content (not copied)    * @throws IllegalArgumentException if the field name is null.    */
DECL|method|StoredField
specifier|protected
name|StoredField
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: allows you to customize the {@link    * FieldType}.    *<p>NOTE: the provided byte[] is not copied so be sure    * not to change it until you're done with this field.    * @param name field name    * @param value byte array pointing to binary content (not copied)    * @param type custom {@link FieldType} for this field    * @throws IllegalArgumentException if the field name is null.    */
DECL|method|StoredField
specifier|public
name|StoredField
parameter_list|(
name|String
name|name
parameter_list|,
name|BytesRef
name|bytes
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|bytes
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
DECL|method|StoredField
specifier|public
name|StoredField
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a stored-only field with the given binary value.    *<p>NOTE: the provided byte[] is not copied so be sure    * not to change it until you're done with this field.    * @param name field name    * @param value byte array pointing to binary content (not copied)    * @param offset starting position of the byte array    * @param length valid length of the byte array    * @throws IllegalArgumentException if the field name is null.    */
DECL|method|StoredField
specifier|public
name|StoredField
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|value
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a stored-only field with the given binary value.    *<p>NOTE: the provided BytesRef is not copied so be sure    * not to change it until you're done with this field.    * @param name field name    * @param value BytesRef pointing to binary content (not copied)    * @throws IllegalArgumentException if the field name is null.    */
DECL|method|StoredField
specifier|public
name|StoredField
parameter_list|(
name|String
name|name
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a stored-only field with the given string value.    * @param name field name    * @param value string value    * @throws IllegalArgumentException if the field name or value is null.    */
DECL|method|StoredField
specifier|public
name|StoredField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
block|}
DECL|method|StoredField
specifier|public
name|StoredField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|// TODO: not great but maybe not a big problem?
comment|/**    * Create a stored-only field with the given integer value.    * @param name field name    * @param value integer value    * @throws IllegalArgumentException if the field name is null.    */
DECL|method|StoredField
specifier|public
name|StoredField
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Create a stored-only field with the given float value.    * @param name field name    * @param value float value    * @throws IllegalArgumentException if the field name is null.    */
DECL|method|StoredField
specifier|public
name|StoredField
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Create a stored-only field with the given long value.    * @param name field name    * @param value long value    * @throws IllegalArgumentException if the field name is null.    */
DECL|method|StoredField
specifier|public
name|StoredField
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Create a stored-only field with the given double value.    * @param name field name    * @param value double value    * @throws IllegalArgumentException if the field name is null.    */
DECL|method|StoredField
specifier|public
name|StoredField
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
block|}
end_class
end_unit
