begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import
begin_comment
comment|/**  *  Access to the Field Info file that describes document fields and whether or  *  not they are indexed. Each segment has a separate Field Info file. Objects  *  of this class are thread-safe for multiple readers, but only one thread can  *  be adding documents at a time, with no other reader or writer threads  *  accessing this object.  **/
end_comment
begin_class
DECL|class|FieldInfo
specifier|public
specifier|final
class|class
name|FieldInfo
block|{
comment|/** Field's name */
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
comment|/** Internal field number */
DECL|field|number
specifier|public
specifier|final
name|int
name|number
decl_stmt|;
DECL|field|docValuesType
specifier|private
name|DocValuesType
name|docValuesType
init|=
name|DocValuesType
operator|.
name|NONE
decl_stmt|;
comment|// True if any document indexed term vectors
DECL|field|storeTermVector
specifier|private
name|boolean
name|storeTermVector
decl_stmt|;
DECL|field|omitNorms
specifier|private
name|boolean
name|omitNorms
decl_stmt|;
comment|// omit norms associated with indexed fields
DECL|field|indexOptions
specifier|private
name|IndexOptions
name|indexOptions
init|=
name|IndexOptions
operator|.
name|NONE
decl_stmt|;
DECL|field|storePayloads
specifier|private
name|boolean
name|storePayloads
decl_stmt|;
comment|// whether this field stores payloads together with term positions
DECL|field|attributes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
DECL|field|dvGen
specifier|private
name|long
name|dvGen
decl_stmt|;
comment|/** If both of these are positive it means this field indexed points    *  (see {@link org.apache.lucene.codecs.PointsFormat}). */
DECL|field|pointDimensionCount
specifier|private
name|int
name|pointDimensionCount
decl_stmt|;
DECL|field|pointNumBytes
specifier|private
name|int
name|pointNumBytes
decl_stmt|;
comment|/**    * Sole constructor.    *    * @lucene.experimental    */
DECL|method|FieldInfo
specifier|public
name|FieldInfo
parameter_list|(
name|String
name|name
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
name|DocValuesType
name|docValues
parameter_list|,
name|long
name|dvGen
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|,
name|int
name|pointDimensionCount
parameter_list|,
name|int
name|pointNumBytes
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|number
operator|=
name|number
expr_stmt|;
name|this
operator|.
name|docValuesType
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|docValues
argument_list|,
literal|"DocValuesType cannot be null (field: \""
operator|+
name|name
operator|+
literal|"\")"
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexOptions
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|indexOptions
argument_list|,
literal|"IndexOptions cannot be null (field: \""
operator|+
name|name
operator|+
literal|"\")"
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
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
block|}
name|this
operator|.
name|dvGen
operator|=
name|dvGen
expr_stmt|;
name|this
operator|.
name|attributes
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|this
operator|.
name|pointDimensionCount
operator|=
name|pointDimensionCount
expr_stmt|;
name|this
operator|.
name|pointNumBytes
operator|=
name|pointNumBytes
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
comment|/**     * Performs internal consistency checks.    * Always returns true (or throws IllegalStateException)     */
DECL|method|checkConsistency
specifier|public
name|boolean
name|checkConsistency
parameter_list|()
block|{
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
comment|// Cannot store payloads unless positions are indexed:
if|if
condition|(
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
operator|&&
name|storePayloads
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"indexed field '"
operator|+
name|name
operator|+
literal|"' cannot have payloads without positions"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|storeTermVector
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"non-indexed field '"
operator|+
name|name
operator|+
literal|"' cannot store term vectors"
argument_list|)
throw|;
block|}
if|if
condition|(
name|storePayloads
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"non-indexed field '"
operator|+
name|name
operator|+
literal|"' cannot store payloads"
argument_list|)
throw|;
block|}
if|if
condition|(
name|omitNorms
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"non-indexed field '"
operator|+
name|name
operator|+
literal|"' cannot omit norms"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|pointDimensionCount
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"pointDimensionCount must be>= 0; got "
operator|+
name|pointDimensionCount
argument_list|)
throw|;
block|}
if|if
condition|(
name|pointNumBytes
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"pointNumBytes must be>= 0; got "
operator|+
name|pointNumBytes
argument_list|)
throw|;
block|}
if|if
condition|(
name|pointDimensionCount
operator|!=
literal|0
operator|&&
name|pointNumBytes
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"pointNumBytes must be> 0 when pointDimensionCount="
operator|+
name|pointDimensionCount
argument_list|)
throw|;
block|}
if|if
condition|(
name|pointNumBytes
operator|!=
literal|0
operator|&&
name|pointDimensionCount
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"pointDimensionCount must be> 0 when pointNumBytes="
operator|+
name|pointNumBytes
argument_list|)
throw|;
block|}
if|if
condition|(
name|dvGen
operator|!=
operator|-
literal|1
operator|&&
name|docValuesType
operator|==
name|DocValuesType
operator|.
name|NONE
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"field '"
operator|+
name|name
operator|+
literal|"' cannot have a docvalues update generation without having docvalues"
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
comment|// should only be called by FieldInfos#addOrUpdate
DECL|method|update
name|void
name|update
parameter_list|(
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
name|int
name|dimensionCount
parameter_list|,
name|int
name|dimensionNumBytes
parameter_list|)
block|{
if|if
condition|(
name|indexOptions
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"IndexOptions cannot be null (field: \""
operator|+
name|name
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
comment|//System.out.println("FI.update field=" + name + " indexed=" + indexed + " omitNorms=" + omitNorms + " this.omitNorms=" + this.omitNorms);
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|!=
name|indexOptions
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
name|this
operator|.
name|indexOptions
operator|=
name|indexOptions
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
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
block|}
block|}
if|if
condition|(
name|this
operator|.
name|pointDimensionCount
operator|==
literal|0
operator|&&
name|dimensionCount
operator|!=
literal|0
condition|)
block|{
name|this
operator|.
name|pointDimensionCount
operator|=
name|dimensionCount
expr_stmt|;
name|this
operator|.
name|pointNumBytes
operator|=
name|dimensionNumBytes
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
comment|// if updated field data is not for indexing, leave the updates out
name|this
operator|.
name|storeTermVector
operator||=
name|storeTermVector
expr_stmt|;
comment|// once vector, always vector
name|this
operator|.
name|storePayloads
operator||=
name|storePayloads
expr_stmt|;
comment|// Awkward: only drop norms if incoming update is indexed:
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|&&
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
block|}
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|NONE
operator|||
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
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
comment|/** Record that this field is indexed with points, with the    *  specified number of dimensions and bytes per dimension. */
DECL|method|setPointDimensions
specifier|public
name|void
name|setPointDimensions
parameter_list|(
name|int
name|count
parameter_list|,
name|int
name|numBytes
parameter_list|)
block|{
if|if
condition|(
name|count
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point dimension count must be>= 0; got "
operator|+
name|count
operator|+
literal|" for field=\""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
if|if
condition|(
name|count
operator|>
name|PointValues
operator|.
name|MAX_DIMENSIONS
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point dimension count must be< PointValues.MAX_DIMENSIONS (= "
operator|+
name|PointValues
operator|.
name|MAX_DIMENSIONS
operator|+
literal|"); got "
operator|+
name|count
operator|+
literal|" for field=\""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
if|if
condition|(
name|numBytes
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point numBytes must be>= 0; got "
operator|+
name|numBytes
operator|+
literal|" for field=\""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
if|if
condition|(
name|numBytes
operator|>
name|PointValues
operator|.
name|MAX_NUM_BYTES
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"point numBytes must be<= PointValues.MAX_NUM_BYTES (= "
operator|+
name|PointValues
operator|.
name|MAX_NUM_BYTES
operator|+
literal|"); got "
operator|+
name|numBytes
operator|+
literal|" for field=\""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
if|if
condition|(
name|pointDimensionCount
operator|!=
literal|0
operator|&&
name|pointDimensionCount
operator|!=
name|count
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change point dimension count from "
operator|+
name|pointDimensionCount
operator|+
literal|" to "
operator|+
name|count
operator|+
literal|" for field=\""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
if|if
condition|(
name|pointNumBytes
operator|!=
literal|0
operator|&&
name|pointNumBytes
operator|!=
name|numBytes
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change point numBytes from "
operator|+
name|pointNumBytes
operator|+
literal|" to "
operator|+
name|numBytes
operator|+
literal|" for field=\""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
name|pointDimensionCount
operator|=
name|count
expr_stmt|;
name|pointNumBytes
operator|=
name|numBytes
expr_stmt|;
block|}
comment|/** Return point dimension count */
DECL|method|getPointDimensionCount
specifier|public
name|int
name|getPointDimensionCount
parameter_list|()
block|{
return|return
name|pointDimensionCount
return|;
block|}
comment|/** Return number of bytes per dimension */
DECL|method|getPointNumBytes
specifier|public
name|int
name|getPointNumBytes
parameter_list|()
block|{
return|return
name|pointNumBytes
return|;
block|}
DECL|method|setDocValuesType
name|void
name|setDocValuesType
parameter_list|(
name|DocValuesType
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
name|NullPointerException
argument_list|(
literal|"DocValuesType cannot be null (field: \""
operator|+
name|name
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|docValuesType
operator|!=
name|DocValuesType
operator|.
name|NONE
operator|&&
name|type
operator|!=
name|DocValuesType
operator|.
name|NONE
operator|&&
name|docValuesType
operator|!=
name|type
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change DocValues type from "
operator|+
name|docValuesType
operator|+
literal|" to "
operator|+
name|type
operator|+
literal|" for field \""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
name|docValuesType
operator|=
name|type
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
comment|/** Returns IndexOptions for the field, or IndexOptions.NONE if the field is not indexed */
DECL|method|getIndexOptions
specifier|public
name|IndexOptions
name|getIndexOptions
parameter_list|()
block|{
return|return
name|indexOptions
return|;
block|}
comment|/** Record the {@link IndexOptions} to use with this field. */
DECL|method|setIndexOptions
specifier|public
name|void
name|setIndexOptions
parameter_list|(
name|IndexOptions
name|newIndexOptions
parameter_list|)
block|{
if|if
condition|(
name|indexOptions
operator|!=
name|newIndexOptions
condition|)
block|{
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
name|indexOptions
operator|=
name|newIndexOptions
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|newIndexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
comment|// downgrade
name|indexOptions
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|newIndexOptions
argument_list|)
operator|<
literal|0
condition|?
name|indexOptions
else|:
name|newIndexOptions
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|NONE
operator|||
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
name|storePayloads
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/**    * Returns {@link DocValuesType} of the docValues; this is    * {@code DocValuesType.NONE} if the field has no docvalues.    */
DECL|method|getDocValuesType
specifier|public
name|DocValuesType
name|getDocValuesType
parameter_list|()
block|{
return|return
name|docValuesType
return|;
block|}
comment|/** Sets the docValues generation of this field. */
DECL|method|setDocValuesGen
name|void
name|setDocValuesGen
parameter_list|(
name|long
name|dvGen
parameter_list|)
block|{
name|this
operator|.
name|dvGen
operator|=
name|dvGen
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
comment|/**    * Returns the docValues generation of this field, or -1 if no docValues    * updates exist for it.    */
DECL|method|getDocValuesGen
specifier|public
name|long
name|getDocValuesGen
parameter_list|()
block|{
return|return
name|dvGen
return|;
block|}
DECL|method|setStoreTermVectors
name|void
name|setStoreTermVectors
parameter_list|()
block|{
name|storeTermVector
operator|=
literal|true
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
DECL|method|setStorePayloads
name|void
name|setStorePayloads
parameter_list|()
block|{
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|&&
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
condition|)
block|{
name|storePayloads
operator|=
literal|true
expr_stmt|;
block|}
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
comment|/**    * Returns true if norms are explicitly omitted for this field    */
DECL|method|omitsNorms
specifier|public
name|boolean
name|omitsNorms
parameter_list|()
block|{
return|return
name|omitNorms
return|;
block|}
comment|/** Omit norms for this field. */
DECL|method|setOmitsNorms
specifier|public
name|void
name|setOmitsNorms
parameter_list|()
block|{
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot omit norms: this field is not indexed"
argument_list|)
throw|;
block|}
name|omitNorms
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Returns true if this field actually has any norms.    */
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|()
block|{
return|return
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|&&
name|omitNorms
operator|==
literal|false
return|;
block|}
comment|/**    * Returns true if any payloads exist for this field.    */
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|storePayloads
return|;
block|}
comment|/**    * Returns true if any term vectors exist for this field.    */
DECL|method|hasVectors
specifier|public
name|boolean
name|hasVectors
parameter_list|()
block|{
return|return
name|storeTermVector
return|;
block|}
comment|/**    * Get a codec attribute value, or null if it does not exist    */
DECL|method|getAttribute
specifier|public
name|String
name|getAttribute
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Puts a codec attribute value.    *<p>    * This is a key-value mapping for the field that the codec can use    * to store additional metadata, and will be available to the codec    * when reading the segment via {@link #getAttribute(String)}    *<p>    * If a value already exists for the field, it will be replaced with     * the new value.    */
DECL|method|putAttribute
specifier|public
name|String
name|putAttribute
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * Returns internal codec attributes map.    */
DECL|method|attributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|()
block|{
return|return
name|attributes
return|;
block|}
block|}
end_class
end_unit
