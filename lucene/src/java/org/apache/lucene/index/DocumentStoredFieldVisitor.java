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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|BinaryField
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
name|Document
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
name|FieldType
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
name|NumericField
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
name|TextField
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
name|store
operator|.
name|IndexInput
import|;
end_import
begin_comment
comment|/** A {@link StoredFieldVisitor} that creates a {@link  *  Document} containing all stored fields, or only specific  *  requested fields provided to {@link #DocumentStoredFieldVisitor(Set)}  *  This is used by {@link IndexReader#document(int)} to load a  *  document.  *  * @lucene.experimental */
end_comment
begin_class
DECL|class|DocumentStoredFieldVisitor
specifier|public
class|class
name|DocumentStoredFieldVisitor
extends|extends
name|StoredFieldVisitor
block|{
DECL|field|doc
specifier|private
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
DECL|field|fieldsToAdd
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fieldsToAdd
decl_stmt|;
comment|/** Load only fields named in the provided<code>Set&lt;String&gt;</code>. */
DECL|method|DocumentStoredFieldVisitor
specifier|public
name|DocumentStoredFieldVisitor
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|fieldsToAdd
parameter_list|)
block|{
name|this
operator|.
name|fieldsToAdd
operator|=
name|fieldsToAdd
expr_stmt|;
block|}
comment|/** Load only fields named in the provided<code>Set&lt;String&gt;</code>. */
DECL|method|DocumentStoredFieldVisitor
specifier|public
name|DocumentStoredFieldVisitor
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|fieldsToAdd
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|fieldsToAdd
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Load all stored fields. */
DECL|method|DocumentStoredFieldVisitor
specifier|public
name|DocumentStoredFieldVisitor
parameter_list|()
block|{
name|this
operator|.
name|fieldsToAdd
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|binaryField
specifier|public
name|boolean
name|binaryField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|IndexInput
name|in
parameter_list|,
name|int
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|accept
argument_list|(
name|fieldInfo
argument_list|)
condition|)
block|{
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|.
name|seek
argument_list|(
name|in
operator|.
name|getFilePointer
argument_list|()
operator|+
name|numBytes
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|stringField
specifier|public
name|boolean
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|IndexInput
name|in
parameter_list|,
name|int
name|numUTF8Bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|accept
argument_list|(
name|fieldInfo
argument_list|)
condition|)
block|{
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|numUTF8Bytes
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
name|fieldInfo
operator|.
name|storeTermVector
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
name|fieldInfo
operator|.
name|storePositionWithTermVector
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
name|fieldInfo
operator|.
name|storeOffsetWithTermVector
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
name|fieldInfo
operator|.
name|storeTermVector
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setOmitNorms
argument_list|(
name|fieldInfo
operator|.
name|omitNorms
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|fieldInfo
operator|.
name|indexOptions
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|ft
argument_list|,
operator|new
name|String
argument_list|(
name|b
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|.
name|seek
argument_list|(
name|in
operator|.
name|getFilePointer
argument_list|()
operator|+
name|numUTF8Bytes
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|intField
specifier|public
name|boolean
name|intField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|accept
argument_list|(
name|fieldInfo
argument_list|)
condition|)
block|{
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|NumericField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexed
argument_list|(
name|fieldInfo
operator|.
name|isIndexed
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|ft
argument_list|)
operator|.
name|setIntValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|longField
specifier|public
name|boolean
name|longField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|value
parameter_list|)
block|{
if|if
condition|(
name|accept
argument_list|(
name|fieldInfo
argument_list|)
condition|)
block|{
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|NumericField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexed
argument_list|(
name|fieldInfo
operator|.
name|isIndexed
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|ft
argument_list|)
operator|.
name|setLongValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|floatField
specifier|public
name|boolean
name|floatField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|float
name|value
parameter_list|)
block|{
if|if
condition|(
name|accept
argument_list|(
name|fieldInfo
argument_list|)
condition|)
block|{
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|NumericField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexed
argument_list|(
name|fieldInfo
operator|.
name|isIndexed
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|ft
argument_list|)
operator|.
name|setFloatValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|doubleField
specifier|public
name|boolean
name|doubleField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|double
name|value
parameter_list|)
block|{
if|if
condition|(
name|accept
argument_list|(
name|fieldInfo
argument_list|)
condition|)
block|{
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|NumericField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexed
argument_list|(
name|fieldInfo
operator|.
name|isIndexed
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|ft
argument_list|)
operator|.
name|setDoubleValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|accept
specifier|private
name|boolean
name|accept
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
return|return
name|fieldsToAdd
operator|==
literal|null
operator|||
name|fieldsToAdd
operator|.
name|contains
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
return|;
block|}
DECL|method|getDocument
specifier|public
name|Document
name|getDocument
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
block|}
end_class
end_unit
