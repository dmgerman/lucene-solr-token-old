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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Map
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
name|DocIdSetIterator
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
name|packed
operator|.
name|PagedGrowableWriter
import|;
end_import
begin_comment
comment|/**  * Holds updates of a single DocValues field, for a set of documents.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|DocValuesFieldUpdates
specifier|abstract
class|class
name|DocValuesFieldUpdates
block|{
DECL|field|PAGE_SIZE
specifier|protected
specifier|static
specifier|final
name|int
name|PAGE_SIZE
init|=
literal|1024
decl_stmt|;
comment|/**    * An iterator over documents and their updated values. Only documents with    * updates are returned by this iterator, and the documents are returned in    * increasing order.    */
DECL|class|Iterator
specifier|static
specifier|abstract
class|class
name|Iterator
block|{
comment|/**      * Returns the next document which has an update, or      * {@link DocIdSetIterator#NO_MORE_DOCS} if there are no more documents to      * return.      */
DECL|method|nextDoc
specifier|abstract
name|int
name|nextDoc
parameter_list|()
function_decl|;
comment|/** Returns the current document this iterator is on. */
DECL|method|doc
specifier|abstract
name|int
name|doc
parameter_list|()
function_decl|;
comment|/**      * Returns the value of the document returned from {@link #nextDoc()}. A      * {@code null} value means that it was unset for this document.      */
DECL|method|value
specifier|abstract
name|Object
name|value
parameter_list|()
function_decl|;
comment|/**      * Reset the iterator's state. Should be called before {@link #nextDoc()}      * and {@link #value()}.      */
DECL|method|reset
specifier|abstract
name|void
name|reset
parameter_list|()
function_decl|;
block|}
DECL|class|Container
specifier|static
class|class
name|Container
block|{
DECL|field|numericDVUpdates
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NumericDocValuesFieldUpdates
argument_list|>
name|numericDVUpdates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|binaryDVUpdates
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BinaryDocValuesFieldUpdates
argument_list|>
name|binaryDVUpdates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|any
name|boolean
name|any
parameter_list|()
block|{
for|for
control|(
name|NumericDocValuesFieldUpdates
name|updates
range|:
name|numericDVUpdates
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|updates
operator|.
name|any
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
for|for
control|(
name|BinaryDocValuesFieldUpdates
name|updates
range|:
name|binaryDVUpdates
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|updates
operator|.
name|any
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
DECL|method|size
name|int
name|size
parameter_list|()
block|{
return|return
name|numericDVUpdates
operator|.
name|size
argument_list|()
operator|+
name|binaryDVUpdates
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|ramBytesPerDoc
name|long
name|ramBytesPerDoc
parameter_list|()
block|{
name|long
name|ramBytesPerDoc
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NumericDocValuesFieldUpdates
name|updates
range|:
name|numericDVUpdates
operator|.
name|values
argument_list|()
control|)
block|{
name|ramBytesPerDoc
operator|+=
name|updates
operator|.
name|ramBytesPerDoc
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|BinaryDocValuesFieldUpdates
name|updates
range|:
name|binaryDVUpdates
operator|.
name|values
argument_list|()
control|)
block|{
name|ramBytesPerDoc
operator|+=
name|updates
operator|.
name|ramBytesPerDoc
argument_list|()
expr_stmt|;
block|}
return|return
name|ramBytesPerDoc
return|;
block|}
DECL|method|getUpdates
name|DocValuesFieldUpdates
name|getUpdates
parameter_list|(
name|String
name|field
parameter_list|,
name|DocValuesType
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|NUMERIC
case|:
return|return
name|numericDVUpdates
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
case|case
name|BINARY
case|:
return|return
name|binaryDVUpdates
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unsupported type: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
DECL|method|newUpdates
name|DocValuesFieldUpdates
name|newUpdates
parameter_list|(
name|String
name|field
parameter_list|,
name|DocValuesType
name|type
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|NUMERIC
case|:
assert|assert
name|numericDVUpdates
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|==
literal|null
assert|;
name|NumericDocValuesFieldUpdates
name|numericUpdates
init|=
operator|new
name|NumericDocValuesFieldUpdates
argument_list|(
name|field
argument_list|,
name|maxDoc
argument_list|)
decl_stmt|;
name|numericDVUpdates
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|numericUpdates
argument_list|)
expr_stmt|;
return|return
name|numericUpdates
return|;
case|case
name|BINARY
case|:
assert|assert
name|binaryDVUpdates
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|==
literal|null
assert|;
name|BinaryDocValuesFieldUpdates
name|binaryUpdates
init|=
operator|new
name|BinaryDocValuesFieldUpdates
argument_list|(
name|field
argument_list|,
name|maxDoc
argument_list|)
decl_stmt|;
name|binaryDVUpdates
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|binaryUpdates
argument_list|)
expr_stmt|;
return|return
name|binaryUpdates
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unsupported type: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"numericDVUpdates="
operator|+
name|numericDVUpdates
operator|+
literal|" binaryDVUpdates="
operator|+
name|binaryDVUpdates
return|;
block|}
block|}
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|type
specifier|final
name|DocValuesType
name|type
decl_stmt|;
DECL|method|DocValuesFieldUpdates
specifier|protected
name|DocValuesFieldUpdates
parameter_list|(
name|String
name|field
parameter_list|,
name|DocValuesType
name|type
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
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
literal|"DocValuesType cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**    * Returns the estimated capacity of a {@link PagedGrowableWriter} given the    * actual number of stored elements.    */
DECL|method|estimateCapacity
specifier|protected
specifier|static
name|int
name|estimateCapacity
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|size
operator|/
name|PAGE_SIZE
argument_list|)
operator|*
name|PAGE_SIZE
return|;
block|}
comment|/**    * Add an update to a document. For unsetting a value you should pass    * {@code null}.    */
DECL|method|add
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|int
name|doc
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**    * Returns an {@link Iterator} over the updated documents and their    * values.    */
DECL|method|iterator
specifier|public
specifier|abstract
name|Iterator
name|iterator
parameter_list|()
function_decl|;
comment|/**    * Merge with another {@link DocValuesFieldUpdates}. This is called for a    * segment which received updates while it was being merged. The given updates    * should override whatever updates are in that instance.    */
DECL|method|merge
specifier|public
specifier|abstract
name|void
name|merge
parameter_list|(
name|DocValuesFieldUpdates
name|other
parameter_list|)
function_decl|;
comment|/** Returns true if this instance contains any updates. */
DECL|method|any
specifier|public
specifier|abstract
name|boolean
name|any
parameter_list|()
function_decl|;
comment|/** Returns approximate RAM bytes used per document. */
DECL|method|ramBytesPerDoc
specifier|public
specifier|abstract
name|long
name|ramBytesPerDoc
parameter_list|()
function_decl|;
block|}
end_class
end_unit
