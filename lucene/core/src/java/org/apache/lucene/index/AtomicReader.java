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
name|io
operator|.
name|IOException
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
name|SearcherManager
import|;
end_import
begin_comment
comment|// javadocs
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
name|Bits
import|;
end_import
begin_comment
comment|/** {@code AtomicReader} is an abstract class, providing an interface for accessing an  index.  Search of an index is done entirely through this abstract interface,  so that any subclass which implements it is searchable. IndexReaders implemented  by this subclass do not consist of several sub-readers,  they are atomic. They support retrieval of stored fields, doc values, terms,  and postings.<p>For efficiency, in this API documents are often referred to via<i>document numbers</i>, non-negative integers which each name a unique  document in the index.  These document numbers are ephemeral -- they may change  as documents are added to and deleted from an index.  Clients should thus not  rely on a given document having the same number between sessions.<p><a name="thread-safety"></a><p><b>NOTE</b>: {@link  IndexReader} instances are completely thread  safe, meaning multiple threads can call any of its methods,  concurrently.  If your application requires external  synchronization, you should<b>not</b> synchronize on the<code>IndexReader</code> instance; use your own  (non-Lucene) objects instead. */
end_comment
begin_class
DECL|class|AtomicReader
specifier|public
specifier|abstract
class|class
name|AtomicReader
extends|extends
name|IndexReader
block|{
DECL|field|readerContext
specifier|private
specifier|final
name|AtomicReaderContext
name|readerContext
init|=
operator|new
name|AtomicReaderContext
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|AtomicReader
specifier|protected
name|AtomicReader
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContext
specifier|public
specifier|final
name|AtomicReaderContext
name|getContext
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|readerContext
return|;
block|}
comment|/**    * Returns {@link Fields} for this reader.    * This method may return null if the reader has no    * postings.    */
DECL|method|fields
specifier|public
specifier|abstract
name|Fields
name|fields
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|docFreq
specifier|public
specifier|final
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|docFreq
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/** Returns the number of documents containing the term    *<code>t</code>.  This method returns 0 if the term or    * field does not exists.  This method does not take into    * account deleted documents that have not yet been merged    * away. */
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
specifier|final
name|long
name|totalTermFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSumDocFreq
specifier|public
specifier|final
name|long
name|getSumDocFreq
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Terms
name|terms
init|=
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|terms
operator|.
name|getSumDocFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
specifier|final
name|int
name|getDocCount
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Terms
name|terms
init|=
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|terms
operator|.
name|getDocCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
specifier|final
name|long
name|getSumTotalTermFreq
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Terms
name|terms
init|=
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|terms
operator|.
name|getSumTotalTermFreq
argument_list|()
return|;
block|}
comment|/** This may return null if the field does not exist.*/
DECL|method|terms
specifier|public
specifier|final
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/** Returns {@link DocsEnum} for the specified term.    *  This will return null if either the field or    *  term does not exist.     *  @see TermsEnum#docs(Bits, DocsEnum) */
DECL|method|termDocsEnum
specifier|public
specifier|final
name|DocsEnum
name|termDocsEnum
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|term
operator|.
name|field
argument_list|()
operator|!=
literal|null
assert|;
assert|assert
name|term
operator|.
name|bytes
argument_list|()
operator|!=
literal|null
assert|;
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|docs
argument_list|(
name|getLiveDocs
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns {@link DocsAndPositionsEnum} for the specified    *  term.  This will return null if the    *  field or term does not exist or positions weren't indexed.     *  @see TermsEnum#docsAndPositions(Bits, DocsAndPositionsEnum) */
DECL|method|termPositionsEnum
specifier|public
specifier|final
name|DocsAndPositionsEnum
name|termPositionsEnum
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|term
operator|.
name|field
argument_list|()
operator|!=
literal|null
assert|;
assert|assert
name|term
operator|.
name|bytes
argument_list|()
operator|!=
literal|null
assert|;
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
name|getLiveDocs
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns {@link NumericDocValues} for this field, or    *  null if no {@link NumericDocValues} were indexed for    *  this field.  The returned instance should only be    *  used by a single thread. */
DECL|method|getNumericDocValues
specifier|public
specifier|abstract
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link BinaryDocValues} for this field, or    *  null if no {@link BinaryDocValues} were indexed for    *  this field.  The returned instance should only be    *  used by a single thread. */
DECL|method|getBinaryDocValues
specifier|public
specifier|abstract
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link SortedDocValues} for this field, or    *  null if no {@link SortedDocValues} were indexed for    *  this field.  The returned instance should only be    *  used by a single thread. */
DECL|method|getSortedDocValues
specifier|public
specifier|abstract
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link SortedSetDocValues} for this field, or    *  null if no {@link SortedSetDocValues} were indexed for    *  this field.  The returned instance should only be    *  used by a single thread. */
DECL|method|getSortedSetDocValues
specifier|public
specifier|abstract
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns a {@link Bits} at the size of<code>reader.maxDoc()</code>,     *  with turned on bits for each docid that does have a value for this field,    *  or null if no DocValues were indexed for this field. The    *  returned instance should only be used by a single thread */
DECL|method|getDocsWithField
specifier|public
specifier|abstract
name|Bits
name|getDocsWithField
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns {@link NumericDocValues} representing norms    *  for this field, or null if no {@link NumericDocValues}    *  were indexed. The returned instance should only be    *  used by a single thread. */
DECL|method|getNormValues
specifier|public
specifier|abstract
name|NumericDocValues
name|getNormValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the {@link FieldInfos} describing all fields in    * this reader.    * @lucene.experimental    */
DECL|method|getFieldInfos
specifier|public
specifier|abstract
name|FieldInfos
name|getFieldInfos
parameter_list|()
function_decl|;
comment|/** Returns the {@link Bits} representing live (not    *  deleted) docs.  A set bit indicates the doc ID has not    *  been deleted.  If this method returns null it means    *  there are no deleted documents (all documents are    *  live).    *    *  The returned instance has been safely published for    *  use by multiple threads without additional    *  synchronization.    */
DECL|method|getLiveDocs
specifier|public
specifier|abstract
name|Bits
name|getLiveDocs
parameter_list|()
function_decl|;
block|}
end_class
end_unit
