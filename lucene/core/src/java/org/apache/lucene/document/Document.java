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
name|util
operator|.
name|*
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
name|IndexDocument
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
begin_comment
comment|// for javadoc
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
name|IndexableField
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
name|search
operator|.
name|IndexSearcher
import|;
end_import
begin_comment
comment|// for javadoc
end_comment
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
name|ScoreDoc
import|;
end_import
begin_comment
comment|// for javadoc
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
name|FilterIterator
import|;
end_import
begin_comment
comment|/** Documents are the unit of indexing and search.  *  * A Document is a set of fields.  Each field has a name and a textual value.  * A field may be {@link org.apache.lucene.index.IndexableFieldType#stored() stored} with the document, in which  * case it is returned with search hits on the document.  Thus each document  * should typically contain one or more stored fields which uniquely identify  * it.  *  *<p>Note that fields which are<i>not</i> {@link org.apache.lucene.index.IndexableFieldType#stored() stored} are  *<i>not</i> available in documents retrieved from the index, e.g. with {@link  * ScoreDoc#doc} or {@link IndexReader#document(int)}.  */
end_comment
begin_class
DECL|class|Document
specifier|public
specifier|final
class|class
name|Document
implements|implements
name|IndexDocument
block|{
DECL|field|fields
specifier|private
specifier|final
name|List
argument_list|<
name|Field
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Constructs a new document with no fields. */
DECL|method|Document
specifier|public
name|Document
parameter_list|()
block|{}
comment|/**   * Creates a Document from StoredDocument so it that can be used e.g. for another   * round of indexing.   *   */
DECL|method|Document
specifier|public
name|Document
parameter_list|(
name|StoredDocument
name|storedDoc
parameter_list|)
block|{
for|for
control|(
name|StorableField
name|field
range|:
name|storedDoc
operator|.
name|getFields
argument_list|()
control|)
block|{
name|Field
name|newField
init|=
operator|new
name|Field
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
operator|(
name|FieldType
operator|)
name|field
operator|.
name|fieldType
argument_list|()
argument_list|)
decl_stmt|;
name|newField
operator|.
name|fieldsData
operator|=
name|field
operator|.
name|stringValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|newField
operator|.
name|fieldsData
operator|==
literal|null
condition|)
block|{
name|newField
operator|.
name|fieldsData
operator|=
name|field
operator|.
name|numericValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|newField
operator|.
name|fieldsData
operator|==
literal|null
condition|)
block|{
name|newField
operator|.
name|fieldsData
operator|=
name|field
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|newField
operator|.
name|fieldsData
operator|==
literal|null
condition|)
block|{
name|newField
operator|.
name|fieldsData
operator|=
name|field
operator|.
name|readerValue
argument_list|()
expr_stmt|;
block|}
name|add
argument_list|(
name|newField
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *<p>Adds a field to a document.  Several fields may be added with    * the same name.  In this case, if the fields are indexed, their text is    * treated as though appended for the purposes of search.</p>    *<p> Note that add like the removeField(s) methods only makes sense     * prior to adding a document to an index. These methods cannot    * be used to change the content of an existing index! In order to achieve this,    * a document has to be deleted from an index and a new changed version of that    * document has to be added.</p>    */
DECL|method|add
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|Field
name|field
parameter_list|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>Removes field with the specified name from the document.    * If multiple fields exist with this name, this method removes the first field that has been added.    * If there is no field with the specified name, the document remains unchanged.</p>    *<p> Note that the removeField(s) methods like the add method only make sense     * prior to adding a document to an index. These methods cannot    * be used to change the content of an existing index! In order to achieve this,    * a document has to be deleted from an index and a new changed version of that    * document has to be added.</p>    */
DECL|method|removeField
specifier|public
specifier|final
name|void
name|removeField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Field
argument_list|>
name|it
init|=
name|fields
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
block|}
comment|/**    *<p>Removes all fields with the given name from the document.    * If there is no field with the specified name, the document remains unchanged.</p>    *<p> Note that the removeField(s) methods like the add method only make sense     * prior to adding a document to an index. These methods cannot    * be used to change the content of an existing index! In order to achieve this,    * a document has to be deleted from an index and a new changed version of that    * document has to be added.</p>    */
DECL|method|removeFields
specifier|public
specifier|final
name|void
name|removeFields
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Field
argument_list|>
name|it
init|=
name|fields
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**   * Returns an array of byte arrays for of the fields that have the name specified   * as the method parameter.  This method returns an empty   * array when there are no matching fields.  It never   * returns null.   *   * @param name the name of the field   * @return a<code>BytesRef[]</code> of binary field values   */
DECL|method|getBinaryValues
specifier|public
specifier|final
name|BytesRef
index|[]
name|getBinaryValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|BytesRef
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|StorableField
argument_list|>
name|it
init|=
name|storedFieldsIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|StorableField
name|field
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
name|field
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|BytesRef
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**   * Returns an array of bytes for the first (or only) field that has the name   * specified as the method parameter. This method will return<code>null</code>   * if no binary fields with the specified name are available.   * There may be non-binary fields with the same name.   *   * @param name the name of the field.   * @return a<code>BytesRef</code> containing the binary field value or<code>null</code>   */
DECL|method|getBinaryValue
specifier|public
specifier|final
name|BytesRef
name|getBinaryValue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|StorableField
argument_list|>
name|it
init|=
name|storedFieldsIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|StorableField
name|field
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
name|field
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
return|return
name|bytes
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns a field with the given name if any exist in this document, or    * null.  If multiple fields exists with this name, this method returns the    * first value added.    */
DECL|method|getField
specifier|public
specifier|final
name|Field
name|getField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|field
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Returns an array of {@link IndexableField}s with the given name.    * This method returns an empty array when there are no    * matching fields.  It never returns null.    *    * @param name the name of the field    * @return a<code>Field[]</code> array    */
DECL|method|getFields
specifier|public
name|Field
index|[]
name|getFields
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|Field
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|Field
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** Returns a List of all the fields in a document.    *<p>Note that fields which are<i>not</i> stored are    *<i>not</i> available in documents retrieved from the    * index, e.g. {@link IndexSearcher#doc(int)} or {@link    * IndexReader#document(int)}.    *     * @return an immutable<code>List&lt;Field&gt;</code>     */
DECL|method|getFields
specifier|public
specifier|final
name|List
argument_list|<
name|Field
argument_list|>
name|getFields
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|fields
argument_list|)
return|;
block|}
DECL|field|NO_STRINGS
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|NO_STRINGS
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Returns an array of values of the field specified as the method parameter.    * This method returns an empty array when there are no    * matching fields.  It never returns null.    * For {@link IntField}, {@link LongField}, {@link    * FloatField} and {@link DoubleField} it returns the string value of the number. If you want    * the actual numeric field instances back, use {@link #getFields}.    * @param name the name of the field    * @return a<code>String[]</code> of field values    */
DECL|method|getValues
specifier|public
specifier|final
name|String
index|[]
name|getValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|StorableField
argument_list|>
name|it
init|=
name|storedFieldsIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|StorableField
name|field
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|field
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|NO_STRINGS
return|;
block|}
return|return
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** Returns the string value of the field with the given name if any exist in    * this document, or null.  If multiple fields exist with this name, this    * method returns the first value added. If only binary fields with this name    * exist, returns null.    * For {@link IntField}, {@link LongField}, {@link    * FloatField} and {@link DoubleField} it returns the string value of the number. If you want    * the actual numeric field instance back, use {@link #getField}.    */
DECL|method|get
specifier|public
specifier|final
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|StorableField
argument_list|>
name|it
init|=
name|storedFieldsIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|StorableField
name|field
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|field
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|field
operator|.
name|stringValue
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Prints the fields of a document for human consumption. */
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"Document<"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|IndexableField
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|field
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|fields
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Obtains all indexed fields in document */
annotation|@
name|Override
DECL|method|indexableFields
specifier|public
name|Iterable
argument_list|<
name|IndexableField
argument_list|>
name|indexableFields
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|IndexableField
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|IndexableField
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Document
operator|.
name|this
operator|.
name|indexedFieldsIterator
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/** Obtains all stored fields in document. */
annotation|@
name|Override
DECL|method|storableFields
specifier|public
name|Iterable
argument_list|<
name|StorableField
argument_list|>
name|storableFields
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|StorableField
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|StorableField
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Document
operator|.
name|this
operator|.
name|storedFieldsIterator
argument_list|()
return|;
block|}
block|}
return|;
block|}
DECL|method|storedFieldsIterator
specifier|private
name|Iterator
argument_list|<
name|StorableField
argument_list|>
name|storedFieldsIterator
parameter_list|()
block|{
return|return
operator|new
name|FilterIterator
argument_list|<
name|StorableField
argument_list|,
name|Field
argument_list|>
argument_list|(
name|fields
operator|.
name|iterator
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|predicateFunction
parameter_list|(
name|Field
name|field
parameter_list|)
block|{
return|return
name|field
operator|.
name|type
operator|.
name|stored
argument_list|()
operator|||
name|field
operator|.
name|type
operator|.
name|docValueType
argument_list|()
operator|!=
literal|null
return|;
block|}
block|}
return|;
block|}
DECL|method|indexedFieldsIterator
specifier|private
name|Iterator
argument_list|<
name|IndexableField
argument_list|>
name|indexedFieldsIterator
parameter_list|()
block|{
return|return
operator|new
name|FilterIterator
argument_list|<
name|IndexableField
argument_list|,
name|Field
argument_list|>
argument_list|(
name|fields
operator|.
name|iterator
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|predicateFunction
parameter_list|(
name|Field
name|field
parameter_list|)
block|{
return|return
name|field
operator|.
name|type
operator|.
name|indexOptions
argument_list|()
operator|!=
literal|null
return|;
block|}
block|}
return|;
block|}
comment|/** Removes all the fields from document. */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|fields
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
