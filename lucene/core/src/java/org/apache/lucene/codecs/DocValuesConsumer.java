begin_unit
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|AtomicReader
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
name|BinaryDocValues
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
name|FilteredTermsEnum
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
name|MergeState
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
name|MultiDocValues
operator|.
name|OrdinalMap
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
name|NumericDocValues
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
name|SegmentWriteState
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
name|SortedDocValues
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
name|SortedSetDocValues
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
name|TermsEnum
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
name|ArrayUtil
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
name|Bits
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
name|LongBitSet
import|;
end_import
begin_comment
comment|/**   * Abstract API that consumes numeric, binary and  * sorted docvalues.  Concrete implementations of this  * actually do "something" with the docvalues (write it into  * the index in a specific format).  *<p>  * The lifecycle is:  *<ol>  *<li>DocValuesConsumer is created by   *       {@link DocValuesFormat#fieldsConsumer(SegmentWriteState)} or  *       {@link NormsFormat#normsConsumer(SegmentWriteState)}.  *<li>{@link #addNumericField}, {@link #addBinaryField},  *       or {@link #addSortedField} are called for each Numeric,  *       Binary, or Sorted docvalues field. The API is a "pull" rather  *       than "push", and the implementation is free to iterate over the   *       values multiple times ({@link Iterable#iterator()}).  *<li>After all fields are added, the consumer is {@link #close}d.  *</ol>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DocValuesConsumer
specifier|public
specifier|abstract
class|class
name|DocValuesConsumer
implements|implements
name|Closeable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|DocValuesConsumer
specifier|protected
name|DocValuesConsumer
parameter_list|()
block|{}
comment|/**    * Writes numeric docvalues for a field.    * @param field field information    * @param values Iterable of numeric values (one for each document). {@code null} indicates    *               a missing value.    * @throws IOException if an I/O error occurred.    */
DECL|method|addNumericField
specifier|public
specifier|abstract
name|void
name|addNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Writes binary docvalues for a field.    * @param field field information    * @param values Iterable of binary values (one for each document). {@code null} indicates    *               a missing value.    * @throws IOException if an I/O error occurred.    */
DECL|method|addBinaryField
specifier|public
specifier|abstract
name|void
name|addBinaryField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Writes pre-sorted binary docvalues for a field.    * @param field field information    * @param values Iterable of binary values in sorted order (deduplicated).    * @param docToOrd Iterable of ordinals (one for each document). {@code -1} indicates    *                 a missing value.    * @throws IOException if an I/O error occurred.    */
DECL|method|addSortedField
specifier|public
specifier|abstract
name|void
name|addSortedField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrd
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Writes pre-sorted set docvalues for a field    * @param field field information    * @param values Iterable of binary values in sorted order (deduplicated).    * @param docToOrdCount Iterable of the number of values for each document. A zero ordinal    *                      count indicates a missing value.    * @param ords Iterable of ordinal occurrences (docToOrdCount*maxDoc total).    * @throws IOException if an I/O error occurred.    */
DECL|method|addSortedSetField
specifier|public
specifier|abstract
name|void
name|addSortedSetField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrdCount
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|ords
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Merges the numeric docvalues from<code>toMerge</code>.    *<p>    * The default implementation calls {@link #addNumericField}, passing    * an Iterable that merges and filters deleted documents on the fly.    */
DECL|method|mergeNumericField
specifier|public
name|void
name|mergeNumericField
parameter_list|(
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|,
specifier|final
name|MergeState
name|mergeState
parameter_list|,
specifier|final
name|List
argument_list|<
name|NumericDocValues
argument_list|>
name|toMerge
parameter_list|,
specifier|final
name|List
argument_list|<
name|Bits
argument_list|>
name|docsWithField
parameter_list|)
throws|throws
name|IOException
block|{
name|addNumericField
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|Iterable
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Number
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
name|int
name|readerUpto
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|docIDUpto
decl_stmt|;
name|Long
name|nextValue
decl_stmt|;
name|AtomicReader
name|currentReader
decl_stmt|;
name|NumericDocValues
name|currentValues
decl_stmt|;
name|Bits
name|currentLiveDocs
decl_stmt|;
name|Bits
name|currentDocsWithField
decl_stmt|;
name|boolean
name|nextIsSet
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextIsSet
operator|||
name|setNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
assert|assert
name|nextIsSet
assert|;
name|nextIsSet
operator|=
literal|false
expr_stmt|;
return|return
name|nextValue
return|;
block|}
specifier|private
name|boolean
name|setNext
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|readerUpto
operator|==
name|toMerge
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentReader
operator|==
literal|null
operator|||
name|docIDUpto
operator|==
name|currentReader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|readerUpto
operator|++
expr_stmt|;
if|if
condition|(
name|readerUpto
operator|<
name|toMerge
operator|.
name|size
argument_list|()
condition|)
block|{
name|currentReader
operator|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|currentValues
operator|=
name|toMerge
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|currentLiveDocs
operator|=
name|currentReader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
name|currentDocsWithField
operator|=
name|docsWithField
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
block|}
name|docIDUpto
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|currentLiveDocs
operator|==
literal|null
operator|||
name|currentLiveDocs
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
condition|)
block|{
name|nextIsSet
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|currentDocsWithField
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
condition|)
block|{
name|nextValue
operator|=
name|currentValues
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nextValue
operator|=
literal|null
expr_stmt|;
block|}
name|docIDUpto
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
name|docIDUpto
operator|++
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Merges the binary docvalues from<code>toMerge</code>.    *<p>    * The default implementation calls {@link #addBinaryField}, passing    * an Iterable that merges and filters deleted documents on the fly.    */
DECL|method|mergeBinaryField
specifier|public
name|void
name|mergeBinaryField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
specifier|final
name|MergeState
name|mergeState
parameter_list|,
specifier|final
name|List
argument_list|<
name|BinaryDocValues
argument_list|>
name|toMerge
parameter_list|,
specifier|final
name|List
argument_list|<
name|Bits
argument_list|>
name|docsWithField
parameter_list|)
throws|throws
name|IOException
block|{
name|addBinaryField
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
name|int
name|readerUpto
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|docIDUpto
decl_stmt|;
name|BytesRef
name|nextValue
decl_stmt|;
name|BytesRef
name|nextPointer
decl_stmt|;
comment|// points to null if missing, or nextValue
name|AtomicReader
name|currentReader
decl_stmt|;
name|BinaryDocValues
name|currentValues
decl_stmt|;
name|Bits
name|currentLiveDocs
decl_stmt|;
name|Bits
name|currentDocsWithField
decl_stmt|;
name|boolean
name|nextIsSet
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextIsSet
operator|||
name|setNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
assert|assert
name|nextIsSet
assert|;
name|nextIsSet
operator|=
literal|false
expr_stmt|;
return|return
name|nextPointer
return|;
block|}
specifier|private
name|boolean
name|setNext
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|readerUpto
operator|==
name|toMerge
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentReader
operator|==
literal|null
operator|||
name|docIDUpto
operator|==
name|currentReader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|readerUpto
operator|++
expr_stmt|;
if|if
condition|(
name|readerUpto
operator|<
name|toMerge
operator|.
name|size
argument_list|()
condition|)
block|{
name|currentReader
operator|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|currentValues
operator|=
name|toMerge
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|currentDocsWithField
operator|=
name|docsWithField
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|currentLiveDocs
operator|=
name|currentReader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
block|}
name|docIDUpto
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|currentLiveDocs
operator|==
literal|null
operator|||
name|currentLiveDocs
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
condition|)
block|{
name|nextIsSet
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|currentDocsWithField
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
condition|)
block|{
name|nextValue
operator|=
name|currentValues
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
expr_stmt|;
name|nextPointer
operator|=
name|nextValue
expr_stmt|;
block|}
else|else
block|{
name|nextPointer
operator|=
literal|null
expr_stmt|;
block|}
name|docIDUpto
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
name|docIDUpto
operator|++
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Merges the sorted docvalues from<code>toMerge</code>.    *<p>    * The default implementation calls {@link #addSortedField}, passing    * an Iterable that merges ordinals and values and filters deleted documents .    */
DECL|method|mergeSortedField
specifier|public
name|void
name|mergeSortedField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
specifier|final
name|MergeState
name|mergeState
parameter_list|,
name|List
argument_list|<
name|SortedDocValues
argument_list|>
name|toMerge
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|AtomicReader
name|readers
index|[]
init|=
name|mergeState
operator|.
name|readers
operator|.
name|toArray
argument_list|(
operator|new
name|AtomicReader
index|[
name|toMerge
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
specifier|final
name|SortedDocValues
name|dvs
index|[]
init|=
name|toMerge
operator|.
name|toArray
argument_list|(
operator|new
name|SortedDocValues
index|[
name|toMerge
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|// step 1: iterate thru each sub and mark terms still in use
name|TermsEnum
name|liveTerms
index|[]
init|=
operator|new
name|TermsEnum
index|[
name|dvs
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|sub
init|=
literal|0
init|;
name|sub
operator|<
name|liveTerms
operator|.
name|length
condition|;
name|sub
operator|++
control|)
block|{
name|AtomicReader
name|reader
init|=
name|readers
index|[
name|sub
index|]
decl_stmt|;
name|SortedDocValues
name|dv
init|=
name|dvs
index|[
name|sub
index|]
decl_stmt|;
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
condition|)
block|{
name|liveTerms
index|[
name|sub
index|]
operator|=
name|dv
operator|.
name|termsEnum
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LongBitSet
name|bitset
init|=
operator|new
name|LongBitSet
argument_list|(
name|dv
operator|.
name|getValueCount
argument_list|()
argument_list|)
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|int
name|ord
init|=
name|dv
operator|.
name|getOrd
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>=
literal|0
condition|)
block|{
name|bitset
operator|.
name|set
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|liveTerms
index|[
name|sub
index|]
operator|=
operator|new
name|BitsFilteredTermsEnum
argument_list|(
name|dv
operator|.
name|termsEnum
argument_list|()
argument_list|,
name|bitset
argument_list|)
expr_stmt|;
block|}
block|}
comment|// step 2: create ordinal map (this conceptually does the "merging")
specifier|final
name|OrdinalMap
name|map
init|=
operator|new
name|OrdinalMap
argument_list|(
name|this
argument_list|,
name|liveTerms
argument_list|)
decl_stmt|;
comment|// step 3: add field
name|addSortedField
argument_list|(
name|fieldInfo
argument_list|,
comment|// ord -> value
operator|new
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
name|int
name|currentOrd
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|currentOrd
operator|<
name|map
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|int
name|segmentNumber
init|=
name|map
operator|.
name|getFirstSegmentNumber
argument_list|(
name|currentOrd
argument_list|)
decl_stmt|;
name|int
name|segmentOrd
init|=
operator|(
name|int
operator|)
name|map
operator|.
name|getFirstSegmentOrd
argument_list|(
name|currentOrd
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|term
init|=
name|dvs
index|[
name|segmentNumber
index|]
operator|.
name|lookupOrd
argument_list|(
name|segmentOrd
argument_list|)
decl_stmt|;
name|currentOrd
operator|++
expr_stmt|;
return|return
name|term
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
argument_list|,
comment|// doc -> ord
operator|new
name|Iterable
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Number
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
name|int
name|readerUpto
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|docIDUpto
decl_stmt|;
name|int
name|nextValue
decl_stmt|;
name|AtomicReader
name|currentReader
decl_stmt|;
name|Bits
name|currentLiveDocs
decl_stmt|;
name|boolean
name|nextIsSet
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextIsSet
operator|||
name|setNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
assert|assert
name|nextIsSet
assert|;
name|nextIsSet
operator|=
literal|false
expr_stmt|;
comment|// TODO make a mutable number
return|return
name|nextValue
return|;
block|}
specifier|private
name|boolean
name|setNext
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|readerUpto
operator|==
name|readers
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentReader
operator|==
literal|null
operator|||
name|docIDUpto
operator|==
name|currentReader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|readerUpto
operator|++
expr_stmt|;
if|if
condition|(
name|readerUpto
operator|<
name|readers
operator|.
name|length
condition|)
block|{
name|currentReader
operator|=
name|readers
index|[
name|readerUpto
index|]
expr_stmt|;
name|currentLiveDocs
operator|=
name|currentReader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
block|}
name|docIDUpto
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|currentLiveDocs
operator|==
literal|null
operator|||
name|currentLiveDocs
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
condition|)
block|{
name|nextIsSet
operator|=
literal|true
expr_stmt|;
name|int
name|segOrd
init|=
name|dvs
index|[
name|readerUpto
index|]
operator|.
name|getOrd
argument_list|(
name|docIDUpto
argument_list|)
decl_stmt|;
name|nextValue
operator|=
name|segOrd
operator|==
operator|-
literal|1
condition|?
operator|-
literal|1
else|:
operator|(
name|int
operator|)
name|map
operator|.
name|getGlobalOrd
argument_list|(
name|readerUpto
argument_list|,
name|segOrd
argument_list|)
expr_stmt|;
name|docIDUpto
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
name|docIDUpto
operator|++
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Merges the sortedset docvalues from<code>toMerge</code>.    *<p>    * The default implementation calls {@link #addSortedSetField}, passing    * an Iterable that merges ordinals and values and filters deleted documents .    */
DECL|method|mergeSortedSetField
specifier|public
name|void
name|mergeSortedSetField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
specifier|final
name|MergeState
name|mergeState
parameter_list|,
name|List
argument_list|<
name|SortedSetDocValues
argument_list|>
name|toMerge
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|AtomicReader
name|readers
index|[]
init|=
name|mergeState
operator|.
name|readers
operator|.
name|toArray
argument_list|(
operator|new
name|AtomicReader
index|[
name|toMerge
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
specifier|final
name|SortedSetDocValues
name|dvs
index|[]
init|=
name|toMerge
operator|.
name|toArray
argument_list|(
operator|new
name|SortedSetDocValues
index|[
name|toMerge
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|// step 1: iterate thru each sub and mark terms still in use
name|TermsEnum
name|liveTerms
index|[]
init|=
operator|new
name|TermsEnum
index|[
name|dvs
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|sub
init|=
literal|0
init|;
name|sub
operator|<
name|liveTerms
operator|.
name|length
condition|;
name|sub
operator|++
control|)
block|{
name|AtomicReader
name|reader
init|=
name|readers
index|[
name|sub
index|]
decl_stmt|;
name|SortedSetDocValues
name|dv
init|=
name|dvs
index|[
name|sub
index|]
decl_stmt|;
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
condition|)
block|{
name|liveTerms
index|[
name|sub
index|]
operator|=
name|dv
operator|.
name|termsEnum
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LongBitSet
name|bitset
init|=
operator|new
name|LongBitSet
argument_list|(
name|dv
operator|.
name|getValueCount
argument_list|()
argument_list|)
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
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|dv
operator|.
name|setDocument
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|long
name|ord
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|dv
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|bitset
operator|.
name|set
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|liveTerms
index|[
name|sub
index|]
operator|=
operator|new
name|BitsFilteredTermsEnum
argument_list|(
name|dv
operator|.
name|termsEnum
argument_list|()
argument_list|,
name|bitset
argument_list|)
expr_stmt|;
block|}
block|}
comment|// step 2: create ordinal map (this conceptually does the "merging")
specifier|final
name|OrdinalMap
name|map
init|=
operator|new
name|OrdinalMap
argument_list|(
name|this
argument_list|,
name|liveTerms
argument_list|)
decl_stmt|;
comment|// step 3: add field
name|addSortedSetField
argument_list|(
name|fieldInfo
argument_list|,
comment|// ord -> value
operator|new
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
name|long
name|currentOrd
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|currentOrd
operator|<
name|map
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|int
name|segmentNumber
init|=
name|map
operator|.
name|getFirstSegmentNumber
argument_list|(
name|currentOrd
argument_list|)
decl_stmt|;
name|long
name|segmentOrd
init|=
name|map
operator|.
name|getFirstSegmentOrd
argument_list|(
name|currentOrd
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|term
init|=
name|dvs
index|[
name|segmentNumber
index|]
operator|.
name|lookupOrd
argument_list|(
name|segmentOrd
argument_list|)
decl_stmt|;
name|currentOrd
operator|++
expr_stmt|;
return|return
name|term
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
argument_list|,
comment|// doc -> ord count
operator|new
name|Iterable
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Number
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
name|int
name|readerUpto
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|docIDUpto
decl_stmt|;
name|int
name|nextValue
decl_stmt|;
name|AtomicReader
name|currentReader
decl_stmt|;
name|Bits
name|currentLiveDocs
decl_stmt|;
name|boolean
name|nextIsSet
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextIsSet
operator|||
name|setNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
assert|assert
name|nextIsSet
assert|;
name|nextIsSet
operator|=
literal|false
expr_stmt|;
comment|// TODO make a mutable number
return|return
name|nextValue
return|;
block|}
specifier|private
name|boolean
name|setNext
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|readerUpto
operator|==
name|readers
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentReader
operator|==
literal|null
operator|||
name|docIDUpto
operator|==
name|currentReader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|readerUpto
operator|++
expr_stmt|;
if|if
condition|(
name|readerUpto
operator|<
name|readers
operator|.
name|length
condition|)
block|{
name|currentReader
operator|=
name|readers
index|[
name|readerUpto
index|]
expr_stmt|;
name|currentLiveDocs
operator|=
name|currentReader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
block|}
name|docIDUpto
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|currentLiveDocs
operator|==
literal|null
operator|||
name|currentLiveDocs
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
condition|)
block|{
name|nextIsSet
operator|=
literal|true
expr_stmt|;
name|SortedSetDocValues
name|dv
init|=
name|dvs
index|[
name|readerUpto
index|]
decl_stmt|;
name|dv
operator|.
name|setDocument
argument_list|(
name|docIDUpto
argument_list|)
expr_stmt|;
name|nextValue
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|dv
operator|.
name|nextOrd
argument_list|()
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|nextValue
operator|++
expr_stmt|;
block|}
name|docIDUpto
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
name|docIDUpto
operator|++
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
argument_list|,
comment|// ords
operator|new
name|Iterable
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Number
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
name|int
name|readerUpto
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|docIDUpto
decl_stmt|;
name|long
name|nextValue
decl_stmt|;
name|AtomicReader
name|currentReader
decl_stmt|;
name|Bits
name|currentLiveDocs
decl_stmt|;
name|boolean
name|nextIsSet
decl_stmt|;
name|long
name|ords
index|[]
init|=
operator|new
name|long
index|[
literal|8
index|]
decl_stmt|;
name|int
name|ordUpto
decl_stmt|;
name|int
name|ordLength
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextIsSet
operator|||
name|setNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
assert|assert
name|nextIsSet
assert|;
name|nextIsSet
operator|=
literal|false
expr_stmt|;
comment|// TODO make a mutable number
return|return
name|nextValue
return|;
block|}
specifier|private
name|boolean
name|setNext
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|readerUpto
operator|==
name|readers
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|ordUpto
operator|<
name|ordLength
condition|)
block|{
name|nextValue
operator|=
name|ords
index|[
name|ordUpto
index|]
expr_stmt|;
name|ordUpto
operator|++
expr_stmt|;
name|nextIsSet
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|currentReader
operator|==
literal|null
operator|||
name|docIDUpto
operator|==
name|currentReader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|readerUpto
operator|++
expr_stmt|;
if|if
condition|(
name|readerUpto
operator|<
name|readers
operator|.
name|length
condition|)
block|{
name|currentReader
operator|=
name|readers
index|[
name|readerUpto
index|]
expr_stmt|;
name|currentLiveDocs
operator|=
name|currentReader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
block|}
name|docIDUpto
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|currentLiveDocs
operator|==
literal|null
operator|||
name|currentLiveDocs
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
condition|)
block|{
assert|assert
name|docIDUpto
operator|<
name|currentReader
operator|.
name|maxDoc
argument_list|()
assert|;
name|SortedSetDocValues
name|dv
init|=
name|dvs
index|[
name|readerUpto
index|]
decl_stmt|;
name|dv
operator|.
name|setDocument
argument_list|(
name|docIDUpto
argument_list|)
expr_stmt|;
name|ordUpto
operator|=
name|ordLength
operator|=
literal|0
expr_stmt|;
name|long
name|ord
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|dv
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
if|if
condition|(
name|ordLength
operator|==
name|ords
operator|.
name|length
condition|)
block|{
name|ords
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|ords
argument_list|,
name|ordLength
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|ords
index|[
name|ordLength
index|]
operator|=
name|map
operator|.
name|getGlobalOrd
argument_list|(
name|readerUpto
argument_list|,
name|ord
argument_list|)
expr_stmt|;
name|ordLength
operator|++
expr_stmt|;
block|}
name|docIDUpto
operator|++
expr_stmt|;
continue|continue;
block|}
name|docIDUpto
operator|++
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// TODO: seek-by-ord to nextSetBit
DECL|class|BitsFilteredTermsEnum
specifier|static
class|class
name|BitsFilteredTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|liveTerms
specifier|final
name|LongBitSet
name|liveTerms
decl_stmt|;
DECL|method|BitsFilteredTermsEnum
name|BitsFilteredTermsEnum
parameter_list|(
name|TermsEnum
name|in
parameter_list|,
name|LongBitSet
name|liveTerms
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//<-- not passing false here wasted about 3 hours of my time!!!!!!!!!!!!!
assert|assert
name|liveTerms
operator|!=
literal|null
assert|;
name|this
operator|.
name|liveTerms
operator|=
name|liveTerms
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|liveTerms
operator|.
name|get
argument_list|(
name|ord
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
else|else
block|{
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
