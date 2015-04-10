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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexOptions
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
comment|/** A field that is indexed but not tokenized: the entire  *  String value is indexed as a single token.  For example  *  this might be used for a 'country' field or an 'id'  *  field, or any field that you intend to use for sorting  *  or access through the field cache. */
end_comment
begin_class
DECL|class|StringField
specifier|public
specifier|final
class|class
name|StringField
extends|extends
name|Field
block|{
comment|/** Indexed, not tokenized, omits norms, indexes    *  DOCS_ONLY, not stored. */
DECL|field|TYPE_NOT_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_NOT_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
comment|/** Indexed, not tokenized, omits norms, indexes    *  DOCS_ONLY, stored */
DECL|field|TYPE_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE_NOT_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|TYPE_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a new textual StringField, indexing the provided String value    *  as a single token.    *    *  @param name field name    *  @param value String value    *  @param stored Store.YES if the content should also be stored    *  @throws IllegalArgumentException if the field name or value is null.    */
DECL|method|StringField
specifier|public
name|StringField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|Store
name|stored
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|stored
operator|==
name|Store
operator|.
name|YES
condition|?
name|TYPE_STORED
else|:
name|TYPE_NOT_STORED
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new binary StringField, indexing the provided binary (BytesRef)    *  value as a single token.    *    *  @param name field name    *  @param value BytesRef value.  The provided value is not cloned so    *         you must not change it until the document(s) holding it    *         have been indexed.    *  @param stored Store.YES if the content should also be stored    *  @throws IllegalArgumentException if the field name or value is null.    */
DECL|method|StringField
specifier|public
name|StringField
parameter_list|(
name|String
name|name
parameter_list|,
name|BytesRef
name|value
parameter_list|,
name|Store
name|stored
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|stored
operator|==
name|Store
operator|.
name|YES
condition|?
name|TYPE_STORED
else|:
name|TYPE_NOT_STORED
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
