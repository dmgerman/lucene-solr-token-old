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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
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
name|IndexWriter
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
comment|/** Store the original field value in the index in a compressed form. This is      * useful for long documents and for binary valued fields.      * @deprecated Please use {@link CompressionTools} instead.      * For string fields that were previously indexed and stored using compression,      * the new way to achive this is: First add the field indexed-only (no store)      * and additionally using the same field name as a binary, stored field      * with {@link CompressionTools#compressString}.      */
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
comment|/** Index the tokens produced by running the field's      * value through an Analyzer.  This is useful for      * common text. */
DECL|field|ANALYZED
specifier|public
specifier|static
specifier|final
name|Index
name|ANALYZED
init|=
operator|new
name|Index
argument_list|(
literal|"ANALYZED"
argument_list|)
decl_stmt|;
comment|/** @deprecated this has been renamed to {@link #ANALYZED} */
DECL|field|TOKENIZED
specifier|public
specifier|static
specifier|final
name|Index
name|TOKENIZED
init|=
name|ANALYZED
decl_stmt|;
comment|/** Index the field's value without using an Analyzer, so it can be searched.      * As no analyzer is used the value will be stored as a single term. This is      * useful for unique Ids like product numbers.      */
DECL|field|NOT_ANALYZED
specifier|public
specifier|static
specifier|final
name|Index
name|NOT_ANALYZED
init|=
operator|new
name|Index
argument_list|(
literal|"NOT_ANALYZED"
argument_list|)
decl_stmt|;
comment|/** @deprecated This has been renamed to {@link #NOT_ANALYZED} */
DECL|field|UN_TOKENIZED
specifier|public
specifier|static
specifier|final
name|Index
name|UN_TOKENIZED
init|=
name|NOT_ANALYZED
decl_stmt|;
comment|/** Expert: Index the field's value without an Analyzer,      * and also disable the storing of norms.  Note that you      * can also separately enable/disable norms by calling      * {@link #setOmitNorms}.  No norms means that      * index-time field and document boosting and field      * length normalization are disabled.  The benefit is      * less memory usage as norms take up one byte of RAM      * per indexed field for every document in the index,      * during searching.  Note that once you index a given      * field<i>with</i> norms enabled, disabling norms will      * have no effect.  In other words, for this to have the      * above described effect on a field, all instances of      * that field must be indexed with NOT_ANALYZED_NO_NORMS      * from the beginning. */
DECL|field|NOT_ANALYZED_NO_NORMS
specifier|public
specifier|static
specifier|final
name|Index
name|NOT_ANALYZED_NO_NORMS
init|=
operator|new
name|Index
argument_list|(
literal|"NOT_ANALYZED_NO_NORMS"
argument_list|)
decl_stmt|;
comment|/** @deprecated This has been renamed to      *  {@link #NOT_ANALYZED_NO_NORMS} */
DECL|field|NO_NORMS
specifier|public
specifier|static
specifier|final
name|Index
name|NO_NORMS
init|=
name|NOT_ANALYZED_NO_NORMS
decl_stmt|;
comment|/** Expert: Index the tokens produced by running the      *  field's value through an Analyzer, and also      *  separately disable the storing of norms.  See      *  {@link #NOT_ANALYZED_NO_NORMS} for what norms are      *  and why you may want to disable them. */
DECL|field|ANALYZED_NO_NORMS
specifier|public
specifier|static
specifier|final
name|Index
name|ANALYZED_NO_NORMS
init|=
operator|new
name|Index
argument_list|(
literal|"ANALYZED_NO_NORMS"
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
comment|/** The value of the field as a String, or null.  If null, the Reader value,    * binary value, or TokenStream value is used.  Exactly one of stringValue(),     * readerValue(), getBinaryValue(), and tokenStreamValue() must be set. */
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
comment|/** The value of the field as a Reader, or null.  If null, the String value,    * binary value, or TokenStream value is used.  Exactly one of stringValue(),     * readerValue(), getBinaryValue(), and tokenStreamValue() must be set. */
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
comment|/** The value of the field in Binary, or null.  If null, the Reader value,    * String value, or TokenStream value is used. Exactly one of stringValue(),     * readerValue(), getBinaryValue(), and tokenStreamValue() must be set.    * @deprecated This method must allocate a new byte[] if    * the {@link AbstractField#getBinaryOffset()} is non-zero    * or {@link AbstractField#getBinaryLength()} is not the    * full length of the byte[]. Please use {@link    * AbstractField#getBinaryValue()} instead, which simply    * returns the byte[].    */
DECL|method|binaryValue
specifier|public
name|byte
index|[]
name|binaryValue
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isBinary
condition|)
return|return
literal|null
return|;
specifier|final
name|byte
index|[]
name|data
init|=
operator|(
name|byte
index|[]
operator|)
name|fieldsData
decl_stmt|;
if|if
condition|(
name|binaryOffset
operator|==
literal|0
operator|&&
name|data
operator|.
name|length
operator|==
name|binaryLength
condition|)
return|return
name|data
return|;
comment|//Optimization
specifier|final
name|byte
index|[]
name|ret
init|=
operator|new
name|byte
index|[
name|binaryLength
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|binaryOffset
argument_list|,
name|ret
argument_list|,
literal|0
argument_list|,
name|binaryLength
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/** The value of the field as a TokesStream, or null.  If null, the Reader value,    * String value, or binary value is used. Exactly one of stringValue(),     * readerValue(), getBinaryValue(), and tokenStreamValue() must be set. */
DECL|method|tokenStreamValue
specifier|public
name|TokenStream
name|tokenStreamValue
parameter_list|()
block|{
return|return
name|fieldsData
operator|instanceof
name|TokenStream
condition|?
operator|(
name|TokenStream
operator|)
name|fieldsData
else|:
literal|null
return|;
block|}
comment|/**<p>Expert: change the value of this field.  This can    *  be used during indexing to re-use a single Field    *  instance to improve indexing speed by avoiding GC cost    *  of new'ing and reclaiming Field instances.  Typically    *  a single {@link Document} instance is re-used as    *  well.  This helps most on small documents.</p>    *     *<p>Note that you should only use this method after the    *  Field has been consumed (ie, the {@link Document}    *  containing this Field has been added to the index).    *  Also, each Field instance should only be used once    *  within a single {@link Document} instance.  See<a    *  href="http://wiki.apache.org/lucene-java/ImproveIndexingSpeed">ImproveIndexingSpeed</a>    *  for details.</p> */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|isBinary
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a String value on a binary field"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/** Expert: change the value of this field.  See<a href="#setValue(java.lang.String)">setValue(String)</a>. */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|Reader
name|value
parameter_list|)
block|{
if|if
condition|(
name|isBinary
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a Reader value on a binary field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|isStored
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a Reader value on a stored field"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|value
expr_stmt|;
block|}
comment|/** Expert: change the value of this field.  See<a href="#setValue(java.lang.String)">setValue(String)</a>. */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isBinary
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a byte[] value on a non-binary field"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|value
expr_stmt|;
name|binaryLength
operator|=
name|value
operator|.
name|length
expr_stmt|;
name|binaryOffset
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Expert: change the value of this field.  See<a href="#setValue(java.lang.String)">setValue(String)</a>. */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
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
if|if
condition|(
operator|!
name|isBinary
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a byte[] value on a non-binary field"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|value
expr_stmt|;
name|binaryLength
operator|=
name|length
expr_stmt|;
name|binaryOffset
operator|=
name|offset
expr_stmt|;
block|}
comment|/** Expert: change the value of this field.  See<a href="#setValue(java.lang.String)">setValue(String)</a>. */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
name|TokenStream
name|value
parameter_list|)
block|{
if|if
condition|(
name|isBinary
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a TokenStream value on a binary field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|isStored
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot set a TokenStream value on a stored field"
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|value
expr_stmt|;
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
name|this
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|value
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|termVector
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a field by specifying its name, value and how it will    * be saved in the index.    *     * @param name The name of the field    * @param internName Whether to .intern() name or not    * @param value The string to process    * @param store Whether<code>value</code> should be stored in the index    * @param index Whether the field should be indexed, and if so, if it should    *  be tokenized before indexing     * @param termVector Whether term vector should be stored    * @throws NullPointerException if name or value is<code>null</code>    * @throws IllegalArgumentException in any of the following situations:    *<ul>     *<li>the field is neither stored nor indexed</li>     *<li>the field is not indexed but termVector is<code>TermVector.YES</code></li>    *</ul>     */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|internName
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
if|if
condition|(
name|internName
condition|)
comment|// field names are optionally interned
name|name
operator|=
name|name
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
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
name|this
operator|.
name|omitTermFreqAndPositions
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
elseif|else
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|ANALYZED
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
name|NOT_ANALYZED
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
name|NOT_ANALYZED_NO_NORMS
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
elseif|else
if|if
condition|(
name|index
operator|==
name|Index
operator|.
name|ANALYZED_NO_NORMS
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
comment|/**    * Create a tokenized and indexed field that is not stored. Term vectors will    * not be stored.  The Reader is read only when the Document is added to the index,    * i.e. you may not close the Reader until {@link IndexWriter#addDocument(Document)}    * has been called.    *     * @param name The name of the field    * @param reader The reader with the content    * @throws NullPointerException if name or reader is<code>null</code>    */
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
comment|/**    * Create a tokenized and indexed field that is not stored, optionally with     * storing term vectors.  The Reader is read only when the Document is added to the index,    * i.e. you may not close the Reader until {@link IndexWriter#addDocument(Document)}    * has been called.    *     * @param name The name of the field    * @param reader The reader with the content    * @param termVector Whether term vector should be stored    * @throws NullPointerException if name or reader is<code>null</code>    */
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
comment|/**    * Create a tokenized and indexed field that is not stored. Term vectors will    * not be stored. This is useful for pre-analyzed fields.    * The TokenStream is read only when the Document is added to the index,    * i.e. you may not close the TokenStream until {@link IndexWriter#addDocument(Document)}    * has been called.    *     * @param name The name of the field    * @param tokenStream The TokenStream with the content    * @throws NullPointerException if name or tokenStream is<code>null</code>    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|TokenStream
name|tokenStream
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|tokenStream
argument_list|,
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a tokenized and indexed field that is not stored, optionally with     * storing term vectors.  This is useful for pre-analyzed fields.    * The TokenStream is read only when the Document is added to the index,    * i.e. you may not close the TokenStream until {@link IndexWriter#addDocument(Document)}    * has been called.    *     * @param name The name of the field    * @param tokenStream The TokenStream with the content    * @param termVector Whether term vector should be stored    * @throws NullPointerException if name or tokenStream is<code>null</code>    */
DECL|method|Field
specifier|public
name|Field
parameter_list|(
name|String
name|name
parameter_list|,
name|TokenStream
name|tokenStream
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
name|tokenStream
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"tokenStream cannot be null"
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
name|tokenStream
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
name|this
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|,
name|store
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a stored field with binary value. Optionally the value may be compressed.    *     * @param name The name of the field    * @param value The binary value    * @param offset Starting offset in value where this Field's bytes are    * @param length Number of bytes to use for this Field, starting at offset    * @param store How<code>value</code> should be stored (compressed or not)    * @throws IllegalArgumentException if store is<code>Store.NO</code>     */
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
name|int
name|offset
parameter_list|,
name|int
name|length
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
name|isStored
operator|=
literal|true
expr_stmt|;
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
name|isStored
operator|=
literal|true
expr_stmt|;
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
name|isIndexed
operator|=
literal|false
expr_stmt|;
name|isTokenized
operator|=
literal|false
expr_stmt|;
name|omitTermFreqAndPositions
operator|=
literal|false
expr_stmt|;
name|omitNorms
operator|=
literal|true
expr_stmt|;
name|isBinary
operator|=
literal|true
expr_stmt|;
name|binaryLength
operator|=
name|length
expr_stmt|;
name|binaryOffset
operator|=
name|offset
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
