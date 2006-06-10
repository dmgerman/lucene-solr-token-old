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
name|util
operator|.
name|Parameter
import|;
end_import
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
name|io
operator|.
name|Serializable
import|;
end_import
begin_comment
comment|/**   A field is a section of a Document.  Each field has two parts, a name and a   value.  Values may be free text, provided as a String or as a Reader, or they   may be atomic keywords, which are not further processed.  Such keywords may   be used to represent dates, urls, etc.  Fields are optionally stored in the   index, so that they may be returned with hits on the document.   */
end_comment
begin_class
DECL|class|Field
specifier|public
specifier|final
class|class
name|Field
extends|extends
name|AbstractField
implements|implements
name|Fieldable
implements|,
name|Serializable
block|{
comment|/** Specifies whether and how a field should be stored. */
DECL|class|Store
specifier|public
specifier|static
specifier|final
class|class
name|Store
extends|extends
name|Parameter
implements|implements
name|Serializable
block|{
DECL|method|Store
specifier|private
name|Store
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** Store the original field value in the index in a compressed form. This is      * useful for long documents and for binary valued fields.      */
DECL|field|COMPRESS
specifier|public
specifier|static
specifier|final
name|Store
name|COMPRESS
init|=
operator|new
name|Store
argument_list|(
literal|"COMPRESS"
argument_list|)
decl_stmt|;
comment|/** Store the original field value in the index. This is useful for short texts      * like a document's title which should be displayed with the results. The      * value is stored in its original form, i.e. no analyzer is used before it is      * stored.      */
DECL|field|YES
specifier|public
specifier|static
specifier|final
name|Store
name|YES
init|=
operator|new
name|Store
argument_list|(
literal|"YES"
argument_list|)
decl_stmt|;
comment|/** Do not store the field value in the index. */
DECL|field|NO
specifier|public
specifier|static
specifier|final
name|Store
name|NO
init|=
operator|new
name|Store
argument_list|(
literal|"NO"
argument_list|)
decl_stmt|;
block|}
comment|/** Specifies whether and how a field should be indexed. */
DECL|class|Index
specifier|public
specifier|static
specifier|final
class|class
name|Index
extends|extends
name|Parameter
implements|implements
name|Serializable
block|{
DECL|method|Index
specifier|private
name|Index
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** Do not index the field value. This field can thus not be searched,      * but one can still access its contents provided it is      * {@link Field.Store stored}. */
DECL|field|NO
specifier|public
specifier|static
specifier|final
name|Index
name|NO
init|=
operator|new
name|Index
argument_list|(
literal|"NO"
argument_list|)
decl_stmt|;
comment|/** Index the field's value so it can be searched. An Analyzer will be used      * to tokenize and possibly further normalize the text before its      * terms will be stored in the index. This is useful for common text.      */
DECL|field|TOKENIZED
specifier|public
specifier|static
specifier|final
name|Index
name|TOKENIZED
init|=
operator|new
name|Index
argument_list|(
literal|"TOKENIZED"
argument_list|)
decl_stmt|;
comment|/** Index the field's value without using an Analyzer, so it can be searched.      * As no analyzer is used the value will be stored as a single term. This is      * useful for unique Ids like product numbers.      */
DECL|field|UN_TOKENIZED
specifier|public
specifier|static
specifier|final
name|Index
name|UN_TOKENIZED
init|=
operator|new
name|Index
argument_list|(
literal|"UN_TOKENIZED"
argument_list|)
decl_stmt|;
comment|/** Index the field's value without an Analyzer, and disable      * the storing of norms.  No norms means that index-time boosting      * and field length normalization will be disabled.  The benefit is      * less memory usage as norms take up one byte per indexed field      * for every document in the index.      */
DECL|field|NO_NORMS
specifier|public
specifier|static
specifier|final
name|Index
name|NO_NORMS
init|=
operator|new
name|Index
argument_list|(
literal|"NO_NORMS"
argument_list|)
decl_stmt|;
block|}
comment|/** Specifies whether and how a field should have term vectors. */
DECL|class|TermVector
specifier|public
specifier|static
specifier|final
class|class
name|TermVector
extends|extends
name|Parameter
implements|implements
name|Serializable
block|{
DECL|method|TermVector
specifier|private
name|TermVector
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** Do not store term vectors.       */
DECL|field|NO
specifier|public
specifier|static
specifier|final
name|TermVector
name|NO
init|=
operator|new
name|TermVector
argument_list|(
literal|"NO"
argument_list|)
decl_stmt|;
comment|/** Store the term vectors of each document. A term vector is a list      * of the document's terms and their number of occurences in that document. */
DECL|field|YES
specifier|public
specifier|static
specifier|final
name|TermVector
name|YES
init|=
operator|new
name|TermVector
argument_list|(
literal|"YES"
argument_list|)
decl_stmt|;
comment|/**      * Store the term vector + token position information      *       * @see #YES      */
DECL|field|WITH_POSITIONS
specifier|public
specifier|static
specifier|final
name|TermVector
name|WITH_POSITIONS
init|=
operator|new
name|TermVector
argument_list|(
literal|"WITH_POSITIONS"
argument_list|)
decl_stmt|;
comment|/**      * Store the term vector + Token offset information      *       * @see #YES      */
DECL|field|WITH_OFFSETS
specifier|public
specifier|static
specifier|final
name|TermVector
name|WITH_OFFSETS
init|=
operator|new
name|TermVector
argument_list|(
literal|"WITH_OFFSETS"
argument_list|)
decl_stmt|;
comment|/**      * Store the term vector + Token position and offset information      *       * @see #YES      * @see #WITH_POSITIONS      * @see #WITH_OFFSETS      */
DECL|field|WITH_POSITIONS_OFFSETS
specifier|public
specifier|static
specifier|final
name|TermVector
name|WITH_POSITIONS_OFFSETS
init|=
operator|new
name|TermVector
argument_list|(
literal|"WITH_POSITIONS_OFFSETS"
argument_list|)
decl_stmt|;
block|}
comment|/** The value of the field as a String, or null.  If null, the Reader value    * or binary value is used.  Exactly one of stringValue(), readerValue(), and    * binaryValue() must be set. */
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
name|fieldsData
operator|instanceof
name|String
condition|?
operator|(
name|String
operator|)
name|fieldsData
else|:
literal|null
return|;
block|}
comment|/** The value of the field as a Reader, or null.  If null, the String value    * or binary value is  used.  Exactly one of stringValue(), readerValue(),    * and binaryValue() must be set. */
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
name|fieldsData
operator|instanceof
name|Reader
condition|?
operator|(
name|Reader
operator|)
name|fieldsData
else|:
literal|null
return|;
block|}
comment|/** The value of the field in Binary, or null.  If null, the Reader or    * String value is used.  Exactly one of stringValue(), readerValue() and    * binaryValue() must be set. */
DECL|method|binaryValue
specifier|public
name|byte
index|[]
name|binaryValue
parameter_list|()
block|{
return|return
name|fieldsData
operator|instanceof
name|byte
index|[]
condition|?
operator|(
name|byte
index|[]
operator|)
name|fieldsData
else|:
literal|null
return|;
block|}
comment|/**    * Create a field by specifying its name, value and how it will    * be saved in the index. Term vectors will not be stored in the index.    *     * @param name The name of the field    * @param value The string to process    * @param store Whether<code>value</code> should be stored in the index    * @param index Whether the field should be indexed, and if so, if it should    *  be tokenized before indexing     * @throws NullPointerException if name or value is<code>null</code>    * @throws IllegalArgumentException if the field is neither stored nor indexed     */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|Store
name|store
parameter_list|,
name|Index
name|index
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a field by specifying its name, value and how it will    * be saved in the index.    *     * @param name The name of the field    * @param value The string to process    * @param store Whether<code>value</code> should be stored in the index    * @param index Whether the field should be indexed, and if so, if it should    *  be tokenized before indexing     * @param termVector Whether term vector should be stored    * @throws NullPointerException if name or value is<code>null</code>    * @throws IllegalArgumentException in any of the following situations:    *<ul>     *<li>the field is neither stored nor indexed</li>     *<li>the field is not indexed but termVector is<code>TermVector.YES</code></li>    *</ul>     */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|Store
name|store
parameter_list|,
name|Index
name|index
parameter_list|,
name|TermVector
name|termVector
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"value cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name and value cannot both be empty"
argument_list|)
throw|;
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|NO
operator|&&
name|store
operator|==
name|Store
operator|.
name|NO
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"it doesn't make sense to have a field that "
operator|+
literal|"is neither indexed nor stored"
argument_list|)
throw|;
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|NO
operator|&&
name|termVector
operator|!=
name|TermVector
operator|.
name|NO
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot store term vector information "
operator|+
literal|"for a field that is not indexed"
argument_list|)
throw|;
name|this
operator|.
name|name
operator|=
name|name
operator|.
name|intern
argument_list|()
expr_stmt|;
comment|// field names are interned
name|this
operator|.
name|fieldsData
operator|=
name|value
expr_stmt|;
if|if
condition|(
name|store
operator|==
name|Store
operator|.
name|YES
condition|)
block|{
name|this
operator|.
name|isStored
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isCompressed
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|store
operator|==
name|Store
operator|.
name|COMPRESS
condition|)
block|{
name|this
operator|.
name|isStored
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isCompressed
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|store
operator|==
name|Store
operator|.
name|NO
condition|)
block|{
name|this
operator|.
name|isStored
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|isCompressed
operator|=
literal|false
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown store parameter "
operator|+
name|store
argument_list|)
throw|;
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|NO
condition|)
block|{
name|this
operator|.
name|isIndexed
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|TOKENIZED
condition|)
block|{
name|this
operator|.
name|isIndexed
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|UN_TOKENIZED
condition|)
block|{
name|this
operator|.
name|isIndexed
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|NO_NORMS
condition|)
block|{
name|this
operator|.
name|isIndexed
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown index parameter "
operator|+
name|index
argument_list|)
throw|;
block|}
name|this
operator|.
name|isBinary
operator|=
literal|false
expr_stmt|;
name|setStoreTermVector
argument_list|(
name|termVector
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a tokenized and indexed field that is not stored. Term vectors will    * not be stored.  The Reader is read only when the Document is added to the index.    *     * @param name The name of the field    * @param reader The reader with the content    * @throws NullPointerException if name or reader is<code>null</code>    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|reader
argument_list|,
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a tokenized and indexed field that is not stored, optionally with     * storing term vectors.  The Reader is read only when the Document is added to the index.    *     * @param name The name of the field    * @param reader The reader with the content    * @param termVector Whether term vector should be stored    * @throws NullPointerException if name or reader is<code>null</code>    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|TermVector
name|termVector
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"reader cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|name
operator|=
name|name
operator|.
name|intern
argument_list|()
expr_stmt|;
comment|// field names are interned
name|this
operator|.
name|fieldsData
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|isStored
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|isCompressed
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|isIndexed
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isBinary
operator|=
literal|false
expr_stmt|;
name|setStoreTermVector
argument_list|(
name|termVector
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a stored field with binary value. Optionally the value may be compressed.    *     * @param name The name of the field    * @param value The binary value    * @param store How<code>value</code> should be stored (compressed or not)    * @throws IllegalArgumentException if store is<code>Store.NO</code>     */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|value
parameter_list|,
name|Store
name|store
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|name
operator|=
name|name
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|fieldsData
operator|=
name|value
expr_stmt|;
if|if
condition|(
name|store
operator|==
name|Store
operator|.
name|YES
condition|)
block|{
name|this
operator|.
name|isStored
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isCompressed
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|store
operator|==
name|Store
operator|.
name|COMPRESS
condition|)
block|{
name|this
operator|.
name|isStored
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|isCompressed
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|store
operator|==
name|Store
operator|.
name|NO
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"binary values can't be unstored"
argument_list|)
throw|;
else|else
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown store parameter "
operator|+
name|store
argument_list|)
throw|;
name|this
operator|.
name|isIndexed
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|isTokenized
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|isBinary
operator|=
literal|true
expr_stmt|;
name|setStoreTermVector
argument_list|(
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
