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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|search
operator|.
name|Hits
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
name|Searcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|// for javadoc
end_comment
begin_comment
comment|/** Documents are the unit of indexing and search.  *  * A Document is a set of fields.  Each field has a name and a textual value.  * A field may be {@link Fieldable#isStored() stored} with the document, in which  * case it is returned with search hits on the document.  Thus each document  * should typically contain one or more stored fields which uniquely identify  * it.  *  *<p>Note that fields which are<i>not</i> {@link Fieldable#isStored() stored} are  *<i>not</i> available in documents retrieved from the index, e.g. with {@link  * Hits#doc(int)}, {@link Searcher#doc(int)} or {@link  * IndexReader#document(int)}.  */
end_comment
begin_class
DECL|class|Document
specifier|public
specifier|final
class|class
name|Document
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
DECL|field|fields
name|List
name|fields
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
comment|/** Constructs a new document with no fields. */
DECL|method|Document
specifier|public
name|Document
parameter_list|()
block|{}
comment|/** Sets a boost factor for hits on any field of this document.  This value    * will be multiplied into the score of all hits on this document.    *    *<p>Values are multiplied into the value of {@link Fieldable#getBoost()} of    * each field in this document.  Thus, this method in effect sets a default    * boost for the fields of this document.    *    * @see Fieldable#setBoost(float)    */
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
comment|/** Returns the boost factor for hits on any field of this document.    *    *<p>The default value is 1.0.    *    *<p>Note: This value is not stored directly with the document in the index.    * Documents returned from {@link IndexReader#document(int)} and    * {@link Hits#doc(int)} may thus not have the same value present as when    * this document was indexed.    *    * @see #setBoost(float)    */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
comment|/**    *<p>Adds a field to a document.  Several fields may be added with    * the same name.  In this case, if the fields are indexed, their text is    * treated as though appended for the purposes of search.</p>    *<p> Note that add like the removeField(s) methods only makes sense     * prior to adding a document to an index. These methods cannot    * be used to change the content of an existing index! In order to achieve this,    * a document has to be deleted from an index and a new changed version of that    * document has to be added.</p>    */
DECL|method|add
specifier|public
specifier|final
name|void
name|add
parameter_list|(
name|Fieldable
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
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
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
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
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
comment|/** Returns a field with the given name if any exist in this document, or    * null.  If multiple fields exists with this name, this method returns the    * first value added.    * Do not use this method with lazy loaded fields.    */
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
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
return|return
name|field
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns a field with the given name if any exist in this document, or    * null.  If multiple fields exists with this name, this method returns the    * first value added.    */
DECL|method|getFieldable
specifier|public
name|Fieldable
name|getFieldable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
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
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
return|return
name|field
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns the string value of the field with the given name if any exist in    * this document, or null.  If multiple fields exist with this name, this    * method returns the first value added. If only binary fields with this name    * exist, returns null.    */
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
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
operator|(
operator|!
name|field
operator|.
name|isBinary
argument_list|()
operator|)
condition|)
return|return
name|field
operator|.
name|stringValue
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns an Enumeration of all the fields in a document.    * @deprecated use {@link #getFields()} instead    */
DECL|method|fields
specifier|public
specifier|final
name|Enumeration
name|fields
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Vector
operator|)
name|fields
operator|)
operator|.
name|elements
argument_list|()
return|;
block|}
comment|/** Returns a List of all the fields in a document.    *<p>Note that fields which are<i>not</i> {@link Fieldable#isStored() stored} are    *<i>not</i> available in documents retrieved from the index, e.g. with {@link    * Hits#doc(int)}, {@link Searcher#doc(int)} or {@link IndexReader#document(int)}.    */
DECL|method|getFields
specifier|public
specifier|final
name|List
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
comment|/**    * Returns an array of {@link Field}s with the given name.    * This method can return<code>null</code>.    * Do not use with lazy loaded fields.    *    * @param name the name of the field    * @return a<code>Field[]</code> array    */
DECL|method|getFields
specifier|public
specifier|final
name|Field
index|[]
name|getFields
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
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
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
name|result
operator|.
name|add
argument_list|(
name|field
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
return|return
literal|null
return|;
return|return
operator|(
name|Field
index|[]
operator|)
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
comment|/**    * Returns an array of {@link Fieldable}s with the given name.    * This method can return<code>null</code>.    *    * @param name the name of the field    * @return a<code>Fieldable[]</code> array or<code>null</code>    */
DECL|method|getFieldables
specifier|public
name|Fieldable
index|[]
name|getFieldables
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
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
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
name|result
operator|.
name|add
argument_list|(
name|field
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
return|return
literal|null
return|;
return|return
operator|(
name|Fieldable
index|[]
operator|)
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|Fieldable
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**    * Returns an array of values of the field specified as the method parameter.    * This method can return<code>null</code>.    *    * @param name the name of the field    * @return a<code>String[]</code> of field values or<code>null</code>    */
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
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
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
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
operator|(
operator|!
name|field
operator|.
name|isBinary
argument_list|()
operator|)
condition|)
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
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|String
index|[]
operator|)
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
comment|/**   * Returns an array of byte arrays for of the fields that have the name specified   * as the method parameter. This method will return<code>null</code> if no   * binary fields with the specified name are available.   *   * @param name the name of the field   * @return a<code>byte[][]</code> of binary field values or<code>null</code>   */
DECL|method|getBinaryValues
specifier|public
specifier|final
name|byte
index|[]
index|[]
name|getBinaryValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
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
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
operator|(
name|field
operator|.
name|isBinary
argument_list|()
operator|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|field
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
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
return|return
literal|null
return|;
return|return
operator|(
name|byte
index|[]
index|[]
operator|)
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|byte
index|[
name|result
operator|.
name|size
argument_list|()
index|]
index|[]
argument_list|)
return|;
block|}
comment|/**   * Returns an array of bytes for the first (or only) field that has the name   * specified as the method parameter. This method will return<code>null</code>   * if no binary fields with the specified name are available.   * There may be non-binary fields with the same name.   *   * @param name the name of the field.   * @return a<code>byte[]</code> containing the binary field value or<code>null</code>   */
DECL|method|getBinaryValue
specifier|public
specifier|final
name|byte
index|[]
name|getBinaryValue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
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
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
operator|(
name|field
operator|.
name|isBinary
argument_list|()
operator|)
condition|)
return|return
name|field
operator|.
name|binaryValue
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Prints the fields of a document for human consumption. */
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
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
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
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
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
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
block|}
end_class
end_unit
