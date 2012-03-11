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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/** @lucene.experimental */
end_comment
begin_class
DECL|class|FieldInfo
specifier|public
specifier|final
class|class
name|FieldInfo
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|number
specifier|public
specifier|final
name|int
name|number
decl_stmt|;
DECL|field|isIndexed
specifier|public
name|boolean
name|isIndexed
decl_stmt|;
DECL|field|docValueType
specifier|private
name|DocValues
operator|.
name|Type
name|docValueType
decl_stmt|;
comment|// True if any document indexed term vectors
DECL|field|storeTermVector
specifier|public
name|boolean
name|storeTermVector
decl_stmt|;
DECL|field|normType
specifier|private
name|DocValues
operator|.
name|Type
name|normType
decl_stmt|;
DECL|field|omitNorms
specifier|public
name|boolean
name|omitNorms
decl_stmt|;
comment|// omit norms associated with indexed fields
DECL|field|indexOptions
specifier|public
name|IndexOptions
name|indexOptions
decl_stmt|;
DECL|field|storePayloads
specifier|public
name|boolean
name|storePayloads
decl_stmt|;
comment|// whether this field stores payloads together with term positions
comment|/**    * Controls how much information is stored in the postings lists.    * @lucene.experimental    */
DECL|enum|IndexOptions
specifier|public
specifier|static
enum|enum
name|IndexOptions
block|{
comment|// NOTE: order is important here; FieldInfo uses this
comment|// order to merge two conflicting IndexOptions (always
comment|// "downgrades" by picking the lowest).
comment|/** only documents are indexed: term frequencies and positions are omitted */
comment|// TODO: maybe rename to just DOCS?
DECL|enum constant|DOCS_ONLY
name|DOCS_ONLY
block|,
comment|/** only documents and term frequencies are indexed: positions are omitted */
DECL|enum constant|DOCS_AND_FREQS
name|DOCS_AND_FREQS
block|,
comment|/** documents, frequencies and positions */
DECL|enum constant|DOCS_AND_FREQS_AND_POSITIONS
name|DOCS_AND_FREQS_AND_POSITIONS
block|,
comment|/** documents, frequencies, positions and offsets */
DECL|enum constant|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
block|,   }
empty_stmt|;
comment|/**    * @lucene.experimental    */
DECL|method|FieldInfo
specifier|public
name|FieldInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|,
name|int
name|number
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|IndexOptions
name|indexOptions
parameter_list|,
name|DocValues
operator|.
name|Type
name|docValues
parameter_list|,
name|DocValues
operator|.
name|Type
name|normsType
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|isIndexed
operator|=
name|isIndexed
expr_stmt|;
name|this
operator|.
name|number
operator|=
name|number
expr_stmt|;
name|this
operator|.
name|docValueType
operator|=
name|docValues
expr_stmt|;
if|if
condition|(
name|isIndexed
condition|)
block|{
name|this
operator|.
name|storeTermVector
operator|=
name|storeTermVector
expr_stmt|;
name|this
operator|.
name|storePayloads
operator|=
name|storePayloads
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|omitNorms
expr_stmt|;
name|this
operator|.
name|indexOptions
operator|=
name|indexOptions
expr_stmt|;
name|this
operator|.
name|normType
operator|=
operator|!
name|omitNorms
condition|?
name|normsType
else|:
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// for non-indexed fields, leave defaults
name|this
operator|.
name|storeTermVector
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|storePayloads
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
expr_stmt|;
name|this
operator|.
name|normType
operator|=
literal|null
expr_stmt|;
block|}
assert|assert
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
operator|||
operator|!
name|storePayloads
assert|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
return|return
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|number
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValueType
argument_list|,
name|normType
argument_list|)
return|;
block|}
comment|// should only be called by FieldInfos#addOrUpdate
DECL|method|update
name|void
name|update
parameter_list|(
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|IndexOptions
name|indexOptions
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|isIndexed
operator|!=
name|isIndexed
condition|)
block|{
name|this
operator|.
name|isIndexed
operator|=
literal|true
expr_stmt|;
comment|// once indexed, always index
block|}
if|if
condition|(
name|isIndexed
condition|)
block|{
comment|// if updated field data is not for indexing, leave the updates out
if|if
condition|(
name|this
operator|.
name|storeTermVector
operator|!=
name|storeTermVector
condition|)
block|{
name|this
operator|.
name|storeTermVector
operator|=
literal|true
expr_stmt|;
comment|// once vector, always vector
block|}
if|if
condition|(
name|this
operator|.
name|storePayloads
operator|!=
name|storePayloads
condition|)
block|{
name|this
operator|.
name|storePayloads
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|omitNorms
operator|!=
name|omitNorms
condition|)
block|{
name|this
operator|.
name|omitNorms
operator|=
literal|true
expr_stmt|;
comment|// if one require omitNorms at least once, it remains off for life
block|}
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|!=
name|indexOptions
condition|)
block|{
comment|// downgrade
name|this
operator|.
name|indexOptions
operator|=
name|this
operator|.
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|indexOptions
argument_list|)
operator|<
literal|0
condition|?
name|this
operator|.
name|indexOptions
else|:
name|indexOptions
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// cannot store payloads if we don't store positions:
name|this
operator|.
name|storePayloads
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
assert|assert
name|this
operator|.
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
operator|||
operator|!
name|this
operator|.
name|storePayloads
assert|;
block|}
DECL|method|setDocValuesType
name|void
name|setDocValuesType
parameter_list|(
name|DocValues
operator|.
name|Type
name|type
parameter_list|,
name|boolean
name|force
parameter_list|)
block|{
if|if
condition|(
name|docValueType
operator|==
literal|null
operator|||
name|force
condition|)
block|{
name|docValueType
operator|=
name|type
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|!=
name|docValueType
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValues type already set to "
operator|+
name|docValueType
operator|+
literal|" but was: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
comment|/**    * @return true if this field has any docValues.    */
DECL|method|hasDocValues
specifier|public
name|boolean
name|hasDocValues
parameter_list|()
block|{
return|return
name|docValueType
operator|!=
literal|null
return|;
block|}
comment|/**    * @return {@link DocValues.Type} of the docValues. this may be null if the field has no docvalues.    */
DECL|method|getDocValuesType
specifier|public
name|DocValues
operator|.
name|Type
name|getDocValuesType
parameter_list|()
block|{
return|return
name|docValueType
return|;
block|}
comment|/**    * @return {@link DocValues.Type} of the norm. this may be null if the field has no norms.    */
DECL|method|getNormType
specifier|public
name|DocValues
operator|.
name|Type
name|getNormType
parameter_list|()
block|{
return|return
name|normType
return|;
block|}
DECL|method|setStoreTermVectors
specifier|public
name|void
name|setStoreTermVectors
parameter_list|()
block|{
name|storeTermVector
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|setNormValueType
specifier|public
name|void
name|setNormValueType
parameter_list|(
name|Type
name|type
parameter_list|,
name|boolean
name|force
parameter_list|)
block|{
if|if
condition|(
name|normType
operator|==
literal|null
operator|||
name|force
condition|)
block|{
name|normType
operator|=
name|type
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|!=
name|normType
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Norm type already set to "
operator|+
name|normType
argument_list|)
throw|;
block|}
block|}
comment|/**    * @return true if norms are explicitly omitted for this field    */
DECL|method|omitNorms
specifier|public
name|boolean
name|omitNorms
parameter_list|()
block|{
return|return
name|omitNorms
return|;
block|}
comment|/**    * @return true if this field actually has any norms.    */
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|()
block|{
return|return
name|isIndexed
operator|&&
operator|!
name|omitNorms
operator|&&
name|normType
operator|!=
literal|null
return|;
block|}
block|}
end_class
end_unit
