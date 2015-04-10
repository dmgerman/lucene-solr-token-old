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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|IndexReader
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
name|StoredDocument
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
name|StoredFieldVisitor
import|;
end_import
begin_comment
comment|/** A {@link StoredFieldVisitor} that creates a {@link  *  Document} containing all stored fields, or only specific  *  requested fields provided to {@link #DocumentStoredFieldVisitor(Set)}.  *<p>  *  This is used by {@link IndexReader#document(int)} to load a  *  document.  *  * @lucene.experimental */
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
name|StoredDocument
name|doc
init|=
operator|new
name|StoredDocument
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
comment|/**     * Load only fields named in the provided<code>Set&lt;String&gt;</code>.     * @param fieldsToAdd Set of fields to load, or<code>null</code> (all fields).    */
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
comment|/** Load only fields named in the provided fields. */
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
argument_list|<>
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
name|void
name|binaryField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stringField
specifier|public
name|void
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
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
name|hasVectors
argument_list|()
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setOmitNorms
argument_list|(
name|fieldInfo
operator|.
name|omitsNorms
argument_list|()
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
operator|new
name|String
argument_list|(
name|value
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|intField
specifier|public
name|void
name|intField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|longField
specifier|public
name|void
name|longField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|floatField
specifier|public
name|void
name|floatField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doubleField
specifier|public
name|void
name|doubleField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|double
name|value
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsField
specifier|public
name|Status
name|needsField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
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
condition|?
name|Status
operator|.
name|YES
else|:
name|Status
operator|.
name|NO
return|;
block|}
comment|/**    * Retrieve the visited document.    * @return {@link StoredDocument} populated with stored fields. Note that only    *         the stored information in the field instances is valid,    *         data such as indexing options, term vector options,    *         etc is not set.    */
DECL|method|getDocument
specifier|public
name|StoredDocument
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
