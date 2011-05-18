begin_unit
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
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
name|Closeable
import|;
end_import
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
name|Comparator
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
name|Fields
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
name|FieldsEnum
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
name|util
operator|.
name|AttributeSource
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
comment|/**  * nocommit - javadoc   *   * @see FieldsEnum#docValues()  * @see Fields#docValues(String)  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DocValues
specifier|public
specifier|abstract
class|class
name|DocValues
implements|implements
name|Closeable
block|{
comment|/*    * TODO: it might be useful to add another Random Access enum for some    * implementations like packed ints and only return such a random access enum    * if the impl supports random access. For super large segments it might be    * useful or even required in certain environements to have disc based random    * access    */
DECL|field|EMPTY_ARRAY
specifier|public
specifier|static
specifier|final
name|DocValues
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|DocValues
index|[
literal|0
index|]
decl_stmt|;
DECL|field|cache
specifier|private
name|SourceCache
name|cache
init|=
operator|new
name|SourceCache
operator|.
name|DirectSourceCache
argument_list|()
decl_stmt|;
comment|/**    * Returns an iterator that steps through all documents values for this    * {@link DocValues} field instance. {@link DocValuesEnum} will skip document    * without a value if applicable.    */
DECL|method|getEnum
specifier|public
name|DocValuesEnum
name|getEnum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getEnum
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**    * Returns an iterator that steps through all documents values for this    * {@link DocValues} field instance. {@link DocValuesEnum} will skip document    * without a value if applicable.    *<p>    * If an {@link AttributeSource} is supplied to this method the    * {@link DocValuesEnum} will use the given source to access implementation    * related attributes.    */
DECL|method|getEnum
specifier|public
specifier|abstract
name|DocValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Loads a new {@link Source} instance for this {@link DocValues} field    * instance. Source instances returned from this method are not cached. It is    * the callers responsibility to maintain the instance and release its    * resources once the source is not needed anymore.    *<p>    * This method will return null iff this {@link DocValues} represent a    * {@link SortedSource}.    *<p>    * For managed {@link Source} instances see {@link #getSource()}.    *     * @see #getSource()    * @see #setCache(SourceCache)    */
DECL|method|load
specifier|public
specifier|abstract
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a {@link Source} instance through the current {@link SourceCache}.    * Iff no {@link Source} has been loaded into the cache so far the source will    * be loaded through {@link #load()} and passed to the {@link SourceCache}.    * The caller of this method should not close the obtained {@link Source}    * instance unless it is not needed for the rest of its life time.    *<p>    * {@link Source} instances obtained from this method are closed / released    * from the cache once this {@link DocValues} instance is closed by the    * {@link IndexReader}, {@link Fields} or {@link FieldsEnum} the    * {@link DocValues} was created from.    *<p>    * This method will return null iff this {@link DocValues} represent a    * {@link SortedSource}.    */
DECL|method|getSource
specifier|public
name|Source
name|getSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|cache
operator|.
name|load
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Returns a {@link SortedSource} instance for this {@link DocValues} field    * instance like {@link #getSource()}.    *<p>    * This method will return null iff this {@link DocValues} represent a    * {@link Source} instead of a {@link SortedSource}.    */
DECL|method|getSortedSorted
specifier|public
name|SortedSource
name|getSortedSorted
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|cache
operator|.
name|loadSorted
argument_list|(
name|this
argument_list|,
name|comparator
argument_list|)
return|;
block|}
comment|/**    * Loads and returns a {@link SortedSource} instance for this    * {@link DocValues} field instance like {@link #load()}.    *<p>    * This method will return null iff this {@link DocValues} represent a    * {@link Source} instead of a {@link SortedSource}.    */
DECL|method|loadSorted
specifier|public
name|SortedSource
name|loadSorted
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comparator
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**    * Returns the {@link ValueType} of this {@link DocValues} instance    */
DECL|method|type
specifier|public
specifier|abstract
name|ValueType
name|type
parameter_list|()
function_decl|;
comment|/**    * Closes this {@link DocValues} instance. This method should only be called    * by the creator of this {@link DocValues} instance. API users should not    * close {@link DocValues} instances.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|cache
operator|.
name|close
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the {@link SourceCache} used by this {@link DocValues} instance. This    * method should be called before {@link #load()} or    * {@link #loadSorted(Comparator)} is called. All {@link Source} or    * {@link SortedSource} instances in the currently used cache will be closed    * before the new cache is installed.    *<p>    * Note: All instances previously obtained from {@link #load()} or    * {@link #loadSorted(Comparator)} will be closed.    *     * @throws IllegalArgumentException    *           if the given cache is<code>null</code>    *     */
DECL|method|setCache
specifier|public
name|void
name|setCache
parameter_list|(
name|SourceCache
name|cache
parameter_list|)
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cache must not be null"
argument_list|)
throw|;
synchronized|synchronized
init|(
name|this
operator|.
name|cache
init|)
block|{
name|this
operator|.
name|cache
operator|.
name|close
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
block|}
comment|/**    * Source of per document values like long, double or {@link BytesRef}    * depending on the {@link DocValues} fields {@link ValueType}. Source    * implementations provide random access semantics similar to array lookups    * and typically are entirely memory resident.    *<p>    * {@link Source} defines 3 {@link ValueType} //TODO finish this    */
DECL|class|Source
specifier|public
specifier|static
specifier|abstract
class|class
name|Source
block|{
comment|// TODO we might need a close method here to null out the internal used arrays?!
DECL|field|missingValue
specifier|protected
specifier|final
name|MissingValue
name|missingValue
init|=
operator|new
name|MissingValue
argument_list|()
decl_stmt|;
comment|/**      * Returns a<tt>long</tt> for the given document id or throws an      * {@link UnsupportedOperationException} if this source doesn't support      *<tt>long</tt> values.      *       * @throws UnsupportedOperationException      *           if this source doesn't support<tt>long</tt> values.      * @see MissingValue      * @see #getMissing()      */
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ints are not supported"
argument_list|)
throw|;
block|}
comment|/**      * Returns a<tt>double</tt> for the given document id or throws an      * {@link UnsupportedOperationException} if this source doesn't support      *<tt>double</tt> values.      *       * @throws UnsupportedOperationException      *           if this source doesn't support<tt>double</tt> values.      * @see MissingValue      * @see #getMissing()      */
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"floats are not supported"
argument_list|)
throw|;
block|}
comment|/**      * Returns a {@link BytesRef} for the given document id or throws an      * {@link UnsupportedOperationException} if this source doesn't support      *<tt>byte[]</tt> values.      *       * @throws UnsupportedOperationException      *           if this source doesn't support<tt>byte[]</tt> values.      * @see MissingValue      * @see #getMissing()      */
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"bytes are not supported"
argument_list|)
throw|;
block|}
comment|/**      * Returns number of unique values. Some implementations may throw      * UnsupportedOperationException.      */
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Returns a {@link DocValuesEnum} for this source.      */
DECL|method|getEnum
specifier|public
name|DocValuesEnum
name|getEnum
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getEnum
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**      * Returns a {@link MissingValue} instance for this {@link Source}.      * Depending on the type of this {@link Source} consumers of the API should      * check if the value returned from on of the getter methods represents a      * value for a missing document or rather a value for a document no value      * was specified during indexing.      */
DECL|method|getMissing
specifier|public
name|MissingValue
name|getMissing
parameter_list|()
block|{
return|return
name|missingValue
return|;
block|}
comment|/**      * Returns the {@link ValueType} of this source.      *       * @return the {@link ValueType} of this source.      */
DECL|method|type
specifier|public
specifier|abstract
name|ValueType
name|type
parameter_list|()
function_decl|;
comment|/**      * Returns a {@link DocValuesEnum} for this source which uses the given      * {@link AttributeSource}.      */
DECL|method|getEnum
specifier|public
specifier|abstract
name|DocValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * {@link DocValuesEnum} utility for {@link Source} implemenations.    *     */
DECL|class|SourceEnum
specifier|public
specifier|abstract
specifier|static
class|class
name|SourceEnum
extends|extends
name|DocValuesEnum
block|{
DECL|field|source
specifier|protected
specifier|final
name|Source
name|source
decl_stmt|;
DECL|field|numDocs
specifier|protected
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|pos
specifier|protected
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Creates a new {@link SourceEnum}      *       * @param attrs      *          the {@link AttributeSource} for this enum      * @param type      *          the enums {@link ValueType}      * @param source      *          the source this enum operates on      * @param numDocs      *          the number of documents within the source      */
DECL|method|SourceEnum
specifier|protected
name|SourceEnum
parameter_list|(
name|AttributeSource
name|attrs
parameter_list|,
name|ValueType
name|type
parameter_list|,
name|Source
name|source
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
name|super
argument_list|(
name|attrs
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|==
name|NO_MORE_DOCS
condition|)
return|return
name|NO_MORE_DOCS
return|;
return|return
name|advance
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
comment|/**    * A sorted variant of {@link Source} for<tt>byte[]</tt> values per document.    *<p>    * Note: {@link DocValuesEnum} obtained from a {@link SortedSource} will    * enumerate values in document order and not in sorted order.    */
DECL|class|SortedSource
specifier|public
specifier|static
specifier|abstract
class|class
name|SortedSource
extends|extends
name|Source
block|{
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
return|return
name|getByOrd
argument_list|(
name|ord
argument_list|(
name|docID
argument_list|)
argument_list|,
name|bytesRef
argument_list|)
return|;
block|}
comment|/**      * Returns ord for specified docID. If this docID had not been added to the      * Writer, the ord is 0. Ord is dense, ie, starts at 0, then increments by 1      * for the next (as defined by {@link Comparator} value.      */
DECL|method|ord
specifier|public
specifier|abstract
name|int
name|ord
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
comment|/** Returns value for specified ord. */
DECL|method|getByOrd
specifier|public
specifier|abstract
name|BytesRef
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
function_decl|;
DECL|class|LookupResult
specifier|public
specifier|static
class|class
name|LookupResult
block|{
comment|/**<code>true</code> iff the values was found */
DECL|field|found
specifier|public
name|boolean
name|found
decl_stmt|;
comment|/**        * the ordinal of the value if found or the ordinal of the value if it        * would be present in the source        */
DECL|field|ord
specifier|public
name|int
name|ord
decl_stmt|;
block|}
comment|/**      * Finds the largest ord whose value is less or equal to the requested      * value. If {@link LookupResult#found} is true, then ord is an exact match.      * The returned {@link LookupResult} may be reused across calls.      */
DECL|method|getByValue
specifier|public
specifier|final
name|LookupResult
name|getByValue
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
return|return
name|getByValue
argument_list|(
name|value
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Performs a lookup by value.      *       * @param value      *          the value to look up      * @param tmpRef      *          a temporary {@link BytesRef} instance used to compare internal      *          values to the given value. Must not be<code>null</code>      * @return the {@link LookupResult}      */
DECL|method|getByValue
specifier|public
specifier|abstract
name|LookupResult
name|getByValue
parameter_list|(
name|BytesRef
name|value
parameter_list|,
name|BytesRef
name|tmpRef
parameter_list|)
function_decl|;
block|}
comment|/**    * {@link MissingValue} is used by {@link Source} implementations to define an    * Implementation dependent value for documents that had no value assigned    * during indexing. Its purpose is similar to a default value but since the a    * missing value across {@link ValueType} and its implementations can be highly    * dynamic the actual values are not constant but defined per {@link Source}    * through the {@link MissingValue} struct. The actual value used to indicate    * a missing value can even changed within the same field from one segment to    * another. Certain {@link Ints} implementations for instance use a value    * outside of value set as the missing value.    */
DECL|class|MissingValue
specifier|public
specifier|final
specifier|static
class|class
name|MissingValue
block|{
DECL|field|longValue
specifier|public
name|long
name|longValue
decl_stmt|;
DECL|field|doubleValue
specifier|public
name|double
name|doubleValue
decl_stmt|;
DECL|field|bytesValue
specifier|public
name|BytesRef
name|bytesValue
decl_stmt|;
comment|/**      * Copies the values from the given {@link MissingValue}.      */
DECL|method|copy
specifier|public
specifier|final
name|void
name|copy
parameter_list|(
name|MissingValue
name|values
parameter_list|)
block|{
name|longValue
operator|=
name|values
operator|.
name|longValue
expr_stmt|;
name|doubleValue
operator|=
name|values
operator|.
name|doubleValue
expr_stmt|;
name|bytesValue
operator|=
name|values
operator|.
name|bytesValue
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
