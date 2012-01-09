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
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
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
name|SortedMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
operator|.
name|IndexOptions
import|;
end_import
begin_comment
comment|/** Access to the Field Info file that describes document fields and whether or  *  not they are indexed. Each segment has a separate Field Info file. Objects  *  of this class are thread-safe for multiple readers, but only one thread can  *  be adding documents at a time, with no other reader or writer threads  *  accessing this object.  *  @lucene.experimental  */
end_comment
begin_class
DECL|class|FieldInfos
specifier|public
specifier|final
class|class
name|FieldInfos
implements|implements
name|Iterable
argument_list|<
name|FieldInfo
argument_list|>
block|{
DECL|class|FieldNumberBiMap
specifier|static
specifier|final
class|class
name|FieldNumberBiMap
block|{
DECL|field|numberToName
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|numberToName
decl_stmt|;
DECL|field|nameToNumber
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nameToNumber
decl_stmt|;
DECL|field|lowestUnassignedFieldNumber
specifier|private
name|int
name|lowestUnassignedFieldNumber
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|FieldNumberBiMap
name|FieldNumberBiMap
parameter_list|()
block|{
name|this
operator|.
name|nameToNumber
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberToName
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the global field number for the given field name. If the name      * does not exist yet it tries to add it with the given preferred field      * number assigned if possible otherwise the first unassigned field number      * is used as the field number.      */
DECL|method|addOrGet
specifier|synchronized
name|int
name|addOrGet
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|preferredFieldNumber
parameter_list|)
block|{
name|Integer
name|fieldNumber
init|=
name|nameToNumber
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldNumber
operator|==
literal|null
condition|)
block|{
specifier|final
name|Integer
name|preferredBoxed
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|preferredFieldNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|preferredFieldNumber
operator|!=
operator|-
literal|1
operator|&&
operator|!
name|numberToName
operator|.
name|containsKey
argument_list|(
name|preferredBoxed
argument_list|)
condition|)
block|{
comment|// cool - we can use this number globally
name|fieldNumber
operator|=
name|preferredBoxed
expr_stmt|;
block|}
else|else
block|{
comment|// find a new FieldNumber
while|while
condition|(
name|numberToName
operator|.
name|containsKey
argument_list|(
operator|++
name|lowestUnassignedFieldNumber
argument_list|)
condition|)
block|{
comment|// might not be up to date - lets do the work once needed
block|}
name|fieldNumber
operator|=
name|lowestUnassignedFieldNumber
expr_stmt|;
block|}
name|numberToName
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|nameToNumber
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|fieldNumber
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldNumber
operator|.
name|intValue
argument_list|()
return|;
block|}
comment|/**      * Sets the given field number and name if not yet set.       */
DECL|method|setIfNotSet
specifier|synchronized
name|void
name|setIfNotSet
parameter_list|(
name|int
name|fieldNumber
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|Integer
name|boxedFieldNumber
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|numberToName
operator|.
name|containsKey
argument_list|(
name|boxedFieldNumber
argument_list|)
operator|&&
operator|!
name|nameToNumber
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|numberToName
operator|.
name|put
argument_list|(
name|boxedFieldNumber
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|nameToNumber
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|boxedFieldNumber
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|containsConsistent
argument_list|(
name|boxedFieldNumber
argument_list|,
name|fieldName
argument_list|)
assert|;
block|}
block|}
comment|// used by assert
DECL|method|containsConsistent
specifier|synchronized
name|boolean
name|containsConsistent
parameter_list|(
name|Integer
name|number
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|equals
argument_list|(
name|numberToName
operator|.
name|get
argument_list|(
name|number
argument_list|)
argument_list|)
operator|&&
name|number
operator|.
name|equals
argument_list|(
name|nameToNumber
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|field|byNumber
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|FieldInfo
argument_list|>
name|byNumber
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|byName
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
name|byName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|globalFieldNumbers
specifier|private
specifier|final
name|FieldNumberBiMap
name|globalFieldNumbers
decl_stmt|;
DECL|field|hasFreq
specifier|private
name|boolean
name|hasFreq
decl_stmt|;
comment|// only set if readonly
DECL|field|hasProx
specifier|private
name|boolean
name|hasProx
decl_stmt|;
comment|// only set if readonly
DECL|field|hasVectors
specifier|private
name|boolean
name|hasVectors
decl_stmt|;
comment|// only set if readonly
DECL|field|version
specifier|private
name|long
name|version
decl_stmt|;
comment|// internal use to track changes
comment|/**    * Creates a new read-only FieldInfos: only public to be accessible    * from the codecs package    *     * @lucene.internal    */
DECL|method|FieldInfos
specifier|public
name|FieldInfos
parameter_list|(
name|FieldInfo
index|[]
name|infos
parameter_list|,
name|boolean
name|hasFreq
parameter_list|,
name|boolean
name|hasProx
parameter_list|,
name|boolean
name|hasVectors
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|hasFreq
operator|=
name|hasFreq
expr_stmt|;
name|this
operator|.
name|hasProx
operator|=
name|hasProx
expr_stmt|;
name|this
operator|.
name|hasVectors
operator|=
name|hasVectors
expr_stmt|;
for|for
control|(
name|FieldInfo
name|info
range|:
name|infos
control|)
block|{
name|putInternal
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|FieldInfos
specifier|public
name|FieldInfos
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|FieldNumberBiMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|FieldInfos
name|other
parameter_list|)
block|{
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|other
control|)
block|{
name|add
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Creates a new FieldInfos instance with the given {@link FieldNumberBiMap}.     * If the {@link FieldNumberBiMap} is<code>null</code> this instance will be read-only.    * @see #isReadOnly()    */
DECL|method|FieldInfos
name|FieldInfos
parameter_list|(
name|FieldNumberBiMap
name|globalFieldNumbers
parameter_list|)
block|{
name|this
operator|.
name|globalFieldNumbers
operator|=
name|globalFieldNumbers
expr_stmt|;
block|}
comment|/**    * adds the given field to this FieldInfos name / number mapping. The given FI    * must be present in the global field number mapping before this method it    * called    */
DECL|method|putInternal
specifier|private
name|void
name|putInternal
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{
assert|assert
operator|!
name|byNumber
operator|.
name|containsKey
argument_list|(
name|fi
operator|.
name|number
argument_list|)
assert|;
assert|assert
operator|!
name|byName
operator|.
name|containsKey
argument_list|(
name|fi
operator|.
name|name
argument_list|)
assert|;
assert|assert
name|globalFieldNumbers
operator|==
literal|null
operator|||
name|globalFieldNumbers
operator|.
name|containsConsistent
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|fi
operator|.
name|number
argument_list|)
argument_list|,
name|fi
operator|.
name|name
argument_list|)
assert|;
name|byNumber
operator|.
name|put
argument_list|(
name|fi
operator|.
name|number
argument_list|,
name|fi
argument_list|)
expr_stmt|;
name|byName
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|fi
argument_list|)
expr_stmt|;
block|}
DECL|method|nextFieldNumber
specifier|private
name|int
name|nextFieldNumber
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|preferredFieldNumber
parameter_list|)
block|{
comment|// get a global number for this field
specifier|final
name|int
name|fieldNumber
init|=
name|globalFieldNumbers
operator|.
name|addOrGet
argument_list|(
name|name
argument_list|,
name|preferredFieldNumber
argument_list|)
decl_stmt|;
assert|assert
name|byNumber
operator|.
name|get
argument_list|(
name|fieldNumber
argument_list|)
operator|==
literal|null
operator|:
literal|"field number "
operator|+
name|fieldNumber
operator|+
literal|" already taken"
assert|;
return|return
name|fieldNumber
return|;
block|}
comment|/**    * Returns a deep clone of this FieldInfos instance.    */
annotation|@
name|Override
DECL|method|clone
specifier|synchronized
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|FieldInfos
name|fis
init|=
operator|new
name|FieldInfos
argument_list|(
name|globalFieldNumbers
argument_list|)
decl_stmt|;
name|fis
operator|.
name|hasFreq
operator|=
name|hasFreq
expr_stmt|;
name|fis
operator|.
name|hasProx
operator|=
name|hasProx
expr_stmt|;
name|fis
operator|.
name|hasVectors
operator|=
name|hasVectors
expr_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|this
control|)
block|{
name|FieldInfo
name|clone
init|=
call|(
name|FieldInfo
call|)
argument_list|(
name|fi
argument_list|)
operator|.
name|clone
argument_list|()
decl_stmt|;
name|fis
operator|.
name|putInternal
argument_list|(
name|clone
argument_list|)
expr_stmt|;
block|}
return|return
name|fis
return|;
block|}
comment|/** Returns true if any fields do not positions */
DECL|method|hasProx
specifier|public
name|boolean
name|hasProx
parameter_list|()
block|{
if|if
condition|(
name|isReadOnly
argument_list|()
condition|)
block|{
return|return
name|hasProx
return|;
block|}
comment|// mutable FIs must check!
for|for
control|(
name|FieldInfo
name|fi
range|:
name|this
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
name|fi
operator|.
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** Returns true if any fields have freqs */
DECL|method|hasFreq
specifier|public
name|boolean
name|hasFreq
parameter_list|()
block|{
if|if
condition|(
name|isReadOnly
argument_list|()
condition|)
block|{
return|return
name|hasFreq
return|;
block|}
comment|// mutable FIs must check!
for|for
control|(
name|FieldInfo
name|fi
range|:
name|this
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
name|fi
operator|.
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Adds or updates fields that are indexed. Whether they have termvectors has to be specified.    *     * @param names The names of the fields    * @param storeTermVectors Whether the fields store term vectors or not    */
DECL|method|addOrUpdateIndexed
specifier|synchronized
specifier|public
name|void
name|addOrUpdateIndexed
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|boolean
name|storeTermVectors
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|addOrUpdate
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|storeTermVectors
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Assumes the fields are not storing term vectors.    *     * @param names The names of the fields    * @param isIndexed Whether the fields are indexed or not    *     * @see #addOrUpdate(String, boolean)    */
DECL|method|addOrUpdate
specifier|synchronized
specifier|public
name|void
name|addOrUpdate
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|boolean
name|isIndexed
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|addOrUpdate
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Calls 5 parameter add with false for all TermVector parameters.    *     * @param name The name of the IndexableField    * @param isIndexed true if the field is indexed    * @see #addOrUpdate(String, boolean, boolean)    */
DECL|method|addOrUpdate
specifier|synchronized
specifier|public
name|void
name|addOrUpdate
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|)
block|{
name|addOrUpdate
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** If the field is not yet known, adds it. If it is known, checks to make    *  sure that the isIndexed flag is the same as was given previously for this    *  field. If not - marks it as being indexed.  Same goes for the TermVector    * parameters.    *     * @param name The name of the field    * @param isIndexed true if the field is indexed    * @param storeTermVector true if the term vector should be stored    */
DECL|method|addOrUpdate
specifier|synchronized
specifier|public
name|void
name|addOrUpdate
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|)
block|{
name|addOrUpdate
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** If the field is not yet known, adds it. If it is known, checks to make    *  sure that the isIndexed flag is the same as was given previously for this    *  field. If not - marks it as being indexed.  Same goes for the TermVector    * parameters.    *    * @param name The name of the field    * @param isIndexed true if the field is indexed    * @param storeTermVector true if the term vector should be stored    * @param omitNorms true if the norms for the indexed field should be omitted    */
DECL|method|addOrUpdate
specifier|synchronized
specifier|public
name|void
name|addOrUpdate
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|)
block|{
name|addOrUpdate
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
literal|false
argument_list|,
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** If the field is not yet known, adds it. If it is known, checks to make    *  sure that the isIndexed flag is the same as was given previously for this    *  field. If not - marks it as being indexed.  Same goes for the TermVector    * parameters.    *    * @param name The name of the field    * @param isIndexed true if the field is indexed    * @param storeTermVector true if the term vector should be stored    * @param omitNorms true if the norms for the indexed field should be omitted    * @param storePayloads true if payloads should be stored for this field    * @param indexOptions if term freqs should be omitted for this field    */
DECL|method|addOrUpdate
specifier|synchronized
specifier|public
name|FieldInfo
name|addOrUpdate
parameter_list|(
name|String
name|name
parameter_list|,
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
parameter_list|,
name|DocValues
operator|.
name|Type
name|docValues
parameter_list|)
block|{
return|return
name|addOrUpdateInternal
argument_list|(
name|name
argument_list|,
operator|-
literal|1
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValues
argument_list|)
return|;
block|}
comment|// NOTE: this method does not carry over termVector
comment|// booleans nor docValuesType; the indexer chain
comment|// (TermVectorsConsumerPerField, DocFieldProcessor) must
comment|// set these fields when they succeed in consuming
comment|// the document:
DECL|method|addOrUpdate
specifier|public
name|FieldInfo
name|addOrUpdate
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|fieldType
parameter_list|)
block|{
comment|// TODO: really, indexer shouldn't even call this
comment|// method (it's only called from DocFieldProcessor);
comment|// rather, each component in the chain should update
comment|// what it "owns".  EG fieldType.indexOptions() should
comment|// be updated by maybe FreqProxTermsWriterPerField:
return|return
name|addOrUpdateInternal
argument_list|(
name|name
argument_list|,
operator|-
literal|1
argument_list|,
name|fieldType
operator|.
name|indexed
argument_list|()
argument_list|,
literal|false
argument_list|,
name|fieldType
operator|.
name|omitNorms
argument_list|()
argument_list|,
literal|false
argument_list|,
name|fieldType
operator|.
name|indexOptions
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|addOrUpdateInternal
specifier|synchronized
specifier|private
name|FieldInfo
name|addOrUpdateInternal
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|preferredFieldNumber
parameter_list|,
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
parameter_list|,
name|DocValues
operator|.
name|Type
name|docValues
parameter_list|)
block|{
if|if
condition|(
name|globalFieldNumbers
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"FieldInfos are read-only, create a new instance with a global field map to make modifications to FieldInfos"
argument_list|)
throw|;
block|}
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
specifier|final
name|int
name|fieldNumber
init|=
name|nextFieldNumber
argument_list|(
name|name
argument_list|,
name|preferredFieldNumber
argument_list|)
decl_stmt|;
name|fi
operator|=
name|addInternal
argument_list|(
name|name
argument_list|,
name|fieldNumber
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValues
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fi
operator|.
name|update
argument_list|(
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|)
expr_stmt|;
name|fi
operator|.
name|setDocValuesType
argument_list|(
name|docValues
argument_list|)
expr_stmt|;
block|}
name|version
operator|++
expr_stmt|;
return|return
name|fi
return|;
block|}
DECL|method|add
specifier|synchronized
specifier|public
name|FieldInfo
name|add
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{
comment|// IMPORTANT - reuse the field number if possible for consistent field numbers across segments
return|return
name|addOrUpdateInternal
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|fi
operator|.
name|number
argument_list|,
name|fi
operator|.
name|isIndexed
argument_list|,
name|fi
operator|.
name|storeTermVector
argument_list|,
name|fi
operator|.
name|omitNorms
argument_list|,
name|fi
operator|.
name|storePayloads
argument_list|,
name|fi
operator|.
name|indexOptions
argument_list|,
name|fi
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
return|;
block|}
comment|/*    * NOTE: if you call this method from a public method make sure you check if we are modifiable and throw an exception otherwise    */
DECL|method|addInternal
specifier|private
name|FieldInfo
name|addInternal
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|fieldNumber
parameter_list|,
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
parameter_list|,
name|DocValues
operator|.
name|Type
name|docValuesType
parameter_list|)
block|{
comment|// don't check modifiable here since we use that to initially build up FIs
if|if
condition|(
name|globalFieldNumbers
operator|!=
literal|null
condition|)
block|{
name|globalFieldNumbers
operator|.
name|setIfNotSet
argument_list|(
name|fieldNumber
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|final
name|FieldInfo
name|fi
init|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|fieldNumber
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValuesType
argument_list|)
decl_stmt|;
name|putInternal
argument_list|(
name|fi
argument_list|)
expr_stmt|;
return|return
name|fi
return|;
block|}
DECL|method|fieldNumber
specifier|public
name|int
name|fieldNumber
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
return|return
operator|(
name|fi
operator|!=
literal|null
operator|)
condition|?
name|fi
operator|.
name|number
else|:
operator|-
literal|1
return|;
block|}
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|byName
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
comment|/**    * Return the fieldName identified by its number.    *     * @param fieldNumber    * @return the fieldName or an empty string when the field    * with the given number doesn't exist.    */
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
return|return
operator|(
name|fi
operator|!=
literal|null
operator|)
condition|?
name|fi
operator|.
name|name
else|:
literal|""
return|;
block|}
comment|/**    * Return the fieldinfo object referenced by the fieldNumber.    * @param fieldNumber    * @return the FieldInfo object or null when the given fieldNumber    * doesn't exist.    */
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
return|return
operator|(
name|fieldNumber
operator|>=
literal|0
operator|)
condition|?
name|byNumber
operator|.
name|get
argument_list|(
name|fieldNumber
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|FieldInfo
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|byNumber
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
assert|assert
name|byNumber
operator|.
name|size
argument_list|()
operator|==
name|byName
operator|.
name|size
argument_list|()
assert|;
return|return
name|byNumber
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|hasVectors
specifier|public
name|boolean
name|hasVectors
parameter_list|()
block|{
if|if
condition|(
name|isReadOnly
argument_list|()
condition|)
block|{
return|return
name|hasVectors
return|;
block|}
comment|// mutable FIs must check
for|for
control|(
name|FieldInfo
name|fi
range|:
name|this
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|storeTermVector
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|()
block|{
for|for
control|(
name|FieldInfo
name|fi
range|:
name|this
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|isIndexed
operator|&&
operator|!
name|fi
operator|.
name|omitNorms
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Returns<code>true</code> iff this instance is not backed by a    * {@link org.apache.lucene.index.FieldInfos.FieldNumberBiMap}. Instances read from a directory via    * {@link FieldInfos#FieldInfos(FieldInfo[], boolean, boolean, boolean)} will always be read-only    * since no {@link org.apache.lucene.index.FieldInfos.FieldNumberBiMap} is supplied, otherwise     *<code>false</code>.    */
DECL|method|isReadOnly
specifier|public
specifier|final
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
name|globalFieldNumbers
operator|==
literal|null
return|;
block|}
DECL|method|getVersion
specifier|synchronized
specifier|final
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|asReadOnly
specifier|final
name|FieldInfos
name|asReadOnly
parameter_list|()
block|{
if|if
condition|(
name|isReadOnly
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
specifier|final
name|FieldInfos
name|roFis
init|=
operator|new
name|FieldInfos
argument_list|(
operator|(
name|FieldNumberBiMap
operator|)
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|this
control|)
block|{
name|FieldInfo
name|clone
init|=
call|(
name|FieldInfo
call|)
argument_list|(
name|fieldInfo
argument_list|)
operator|.
name|clone
argument_list|()
decl_stmt|;
name|roFis
operator|.
name|putInternal
argument_list|(
name|clone
argument_list|)
expr_stmt|;
name|roFis
operator|.
name|hasVectors
operator||=
name|clone
operator|.
name|storeTermVector
expr_stmt|;
name|roFis
operator|.
name|hasProx
operator||=
name|clone
operator|.
name|isIndexed
operator|&&
name|clone
operator|.
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
expr_stmt|;
name|roFis
operator|.
name|hasFreq
operator||=
name|clone
operator|.
name|isIndexed
operator|&&
name|clone
operator|.
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
expr_stmt|;
block|}
return|return
name|roFis
return|;
block|}
DECL|method|anyDocValuesFields
specifier|public
name|boolean
name|anyDocValuesFields
parameter_list|()
block|{
for|for
control|(
name|FieldInfo
name|fi
range|:
name|this
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Creates a new {@link FieldInfo} instance from the given instance. If the given instance is    * read-only this instance will be read-only too.    *     * @see #isReadOnly()    */
DECL|method|from
specifier|static
name|FieldInfos
name|from
parameter_list|(
name|FieldInfos
name|other
parameter_list|)
block|{
return|return
operator|new
name|FieldInfos
argument_list|(
name|other
operator|.
name|globalFieldNumbers
argument_list|)
return|;
block|}
block|}
end_class
end_unit
