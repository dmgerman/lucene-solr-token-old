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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|store
operator|.
name|Directory
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
name|*
import|;
end_import
begin_comment
comment|/** An IndexReader which reads multiple indexes, appending their content.  *  * @version $Id$  */
end_comment
begin_class
DECL|class|MultiReader
specifier|public
class|class
name|MultiReader
extends|extends
name|IndexReader
block|{
DECL|field|subReaders
specifier|private
name|IndexReader
index|[]
name|subReaders
decl_stmt|;
DECL|field|starts
specifier|private
name|int
index|[]
name|starts
decl_stmt|;
comment|// 1st docno for each segment
DECL|field|normsCache
specifier|private
name|Hashtable
name|normsCache
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
DECL|field|maxDoc
specifier|private
name|int
name|maxDoc
init|=
literal|0
decl_stmt|;
DECL|field|numDocs
specifier|private
name|int
name|numDocs
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|hasDeletions
specifier|private
name|boolean
name|hasDeletions
init|=
literal|false
decl_stmt|;
comment|/**   *<p>Construct a MultiReader aggregating the named set of (sub)readers.   * Directory locking for delete, undeleteAll, and setNorm operations is   * left to the subreaders.</p>   *<p>Note that all subreaders are closed if this Multireader is closed.</p>   * @param subReaders set of (sub)readers   * @throws IOException   */
DECL|method|MultiReader
specifier|public
name|MultiReader
parameter_list|(
name|IndexReader
index|[]
name|subReaders
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|subReaders
operator|.
name|length
operator|==
literal|0
condition|?
literal|null
else|:
name|subReaders
index|[
literal|0
index|]
operator|.
name|directory
argument_list|()
argument_list|)
expr_stmt|;
name|initialize
argument_list|(
name|subReaders
argument_list|)
expr_stmt|;
block|}
comment|/** Construct reading the named set of readers. */
DECL|method|MultiReader
name|MultiReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfos
name|sis
parameter_list|,
name|boolean
name|closeDirectory
parameter_list|,
name|IndexReader
index|[]
name|subReaders
parameter_list|)
block|{
name|super
argument_list|(
name|directory
argument_list|,
name|sis
argument_list|,
name|closeDirectory
argument_list|)
expr_stmt|;
name|initialize
argument_list|(
name|subReaders
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize
specifier|private
name|void
name|initialize
parameter_list|(
name|IndexReader
index|[]
name|subReaders
parameter_list|)
block|{
name|this
operator|.
name|subReaders
operator|=
name|subReaders
expr_stmt|;
name|starts
operator|=
operator|new
name|int
index|[
name|subReaders
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
comment|// build starts array
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|starts
index|[
name|i
index|]
operator|=
name|maxDoc
expr_stmt|;
name|maxDoc
operator|+=
name|subReaders
index|[
name|i
index|]
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
comment|// compute maxDocs
if|if
condition|(
name|subReaders
index|[
name|i
index|]
operator|.
name|hasDeletions
argument_list|()
condition|)
name|hasDeletions
operator|=
literal|true
expr_stmt|;
block|}
name|starts
index|[
name|subReaders
operator|.
name|length
index|]
operator|=
name|maxDoc
expr_stmt|;
block|}
comment|/** Return an array of term frequency vectors for the specified document.    *  The array contains a vector for each vectorized field in the document.    *  Each vector vector contains term numbers and frequencies for all terms    *  in a given vectorized field.    *  If no such fields existed, the method returns null.    */
DECL|method|getTermFreqVectors
specifier|public
name|TermFreqVector
index|[]
name|getTermFreqVectors
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find segment num
return|return
name|subReaders
index|[
name|i
index|]
operator|.
name|getTermFreqVectors
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to segment
block|}
DECL|method|getTermFreqVector
specifier|public
name|TermFreqVector
name|getTermFreqVector
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find segment num
return|return
name|subReaders
index|[
name|i
index|]
operator|.
name|getTermFreqVector
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|method|numDocs
specifier|public
specifier|synchronized
name|int
name|numDocs
parameter_list|()
block|{
if|if
condition|(
name|numDocs
operator|==
operator|-
literal|1
condition|)
block|{
comment|// check cache
name|int
name|n
init|=
literal|0
decl_stmt|;
comment|// cache miss--recompute
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|n
operator|+=
name|subReaders
index|[
name|i
index|]
operator|.
name|numDocs
argument_list|()
expr_stmt|;
comment|// sum from readers
name|numDocs
operator|=
name|n
expr_stmt|;
block|}
return|return
name|numDocs
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
DECL|method|document
specifier|public
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find segment num
return|return
name|subReaders
index|[
name|i
index|]
operator|.
name|document
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to segment reader
block|}
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find segment num
return|return
name|subReaders
index|[
name|i
index|]
operator|.
name|isDeleted
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to segment reader
block|}
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
name|hasDeletions
return|;
block|}
DECL|method|doDelete
specifier|protected
name|void
name|doDelete
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|numDocs
operator|=
operator|-
literal|1
expr_stmt|;
comment|// invalidate cache
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find segment num
name|subReaders
index|[
name|i
index|]
operator|.
name|delete
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
comment|// dispatch to segment reader
name|hasDeletions
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|doUndeleteAll
specifier|protected
name|void
name|doUndeleteAll
parameter_list|()
throws|throws
name|IOException
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|subReaders
index|[
name|i
index|]
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
name|hasDeletions
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|readerIndex
specifier|private
name|int
name|readerIndex
parameter_list|(
name|int
name|n
parameter_list|)
block|{
comment|// find reader for doc n:
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// search starts array
name|int
name|hi
init|=
name|subReaders
operator|.
name|length
operator|-
literal|1
decl_stmt|;
comment|// for first element less
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>
literal|1
decl_stmt|;
name|int
name|midValue
init|=
name|starts
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|n
operator|<
name|midValue
condition|)
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|n
operator|>
name|midValue
condition|)
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
else|else
block|{
comment|// found a match
while|while
condition|(
name|mid
operator|+
literal|1
operator|<
name|subReaders
operator|.
name|length
operator|&&
name|starts
index|[
name|mid
operator|+
literal|1
index|]
operator|==
name|midValue
condition|)
block|{
name|mid
operator|++
expr_stmt|;
comment|// scan to last match
block|}
return|return
name|mid
return|;
block|}
block|}
return|return
name|hi
return|;
block|}
DECL|method|norms
specifier|public
specifier|synchronized
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|bytes
init|=
operator|(
name|byte
index|[]
operator|)
name|normsCache
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
return|return
name|bytes
return|;
comment|// cache hit
name|bytes
operator|=
operator|new
name|byte
index|[
name|maxDoc
argument_list|()
index|]
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|subReaders
index|[
name|i
index|]
operator|.
name|norms
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|,
name|starts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|normsCache
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
comment|// update cache
return|return
name|bytes
return|;
block|}
DECL|method|norms
specifier|public
specifier|synchronized
name|void
name|norms
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
name|result
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|bytes
init|=
operator|(
name|byte
index|[]
operator|)
name|normsCache
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
comment|// cache hit
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
name|offset
argument_list|,
name|maxDoc
argument_list|()
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
comment|// read from segments
name|subReaders
index|[
name|i
index|]
operator|.
name|norms
argument_list|(
name|field
argument_list|,
name|result
argument_list|,
name|offset
operator|+
name|starts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|doSetNorm
specifier|protected
name|void
name|doSetNorm
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|field
parameter_list|,
name|byte
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|normsCache
operator|.
name|remove
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|// clear cache
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|n
argument_list|)
decl_stmt|;
comment|// find segment num
name|subReaders
index|[
name|i
index|]
operator|.
name|setNorm
argument_list|(
name|n
operator|-
name|starts
index|[
name|i
index|]
argument_list|,
name|field
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// dispatch
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|MultiTermEnum
argument_list|(
name|subReaders
argument_list|,
name|starts
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MultiTermEnum
argument_list|(
name|subReaders
argument_list|,
name|starts
argument_list|,
name|term
argument_list|)
return|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|total
init|=
literal|0
decl_stmt|;
comment|// sum freqs in segments
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|total
operator|+=
name|subReaders
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|(
name|t
argument_list|)
expr_stmt|;
return|return
name|total
return|;
block|}
DECL|method|termDocs
specifier|public
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|MultiTermDocs
argument_list|(
name|subReaders
argument_list|,
name|starts
argument_list|)
return|;
block|}
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|MultiTermPositions
argument_list|(
name|subReaders
argument_list|,
name|starts
argument_list|)
return|;
block|}
DECL|method|doCommit
specifier|protected
name|void
name|doCommit
parameter_list|()
throws|throws
name|IOException
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|subReaders
index|[
name|i
index|]
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|doClose
specifier|protected
specifier|synchronized
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|subReaders
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * @see IndexReader#getFieldNames()    */
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|()
throws|throws
name|IOException
block|{
comment|// maintain a unique set of field names
name|Set
name|fieldSet
init|=
operator|new
name|HashSet
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
name|subReaders
index|[
name|i
index|]
decl_stmt|;
name|Collection
name|names
init|=
name|reader
operator|.
name|getFieldNames
argument_list|()
decl_stmt|;
name|fieldSet
operator|.
name|addAll
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldSet
return|;
block|}
comment|/**    * @see IndexReader#getFieldNames(boolean)    */
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|(
name|boolean
name|indexed
parameter_list|)
throws|throws
name|IOException
block|{
comment|// maintain a unique set of field names
name|Set
name|fieldSet
init|=
operator|new
name|HashSet
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
name|subReaders
index|[
name|i
index|]
decl_stmt|;
name|Collection
name|names
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|indexed
argument_list|)
decl_stmt|;
name|fieldSet
operator|.
name|addAll
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldSet
return|;
block|}
DECL|method|getIndexedFieldNames
specifier|public
name|Collection
name|getIndexedFieldNames
parameter_list|(
name|Field
operator|.
name|TermVector
name|tvSpec
parameter_list|)
block|{
comment|// maintain a unique set of field names
name|Set
name|fieldSet
init|=
operator|new
name|HashSet
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
name|subReaders
index|[
name|i
index|]
decl_stmt|;
name|Collection
name|names
init|=
name|reader
operator|.
name|getIndexedFieldNames
argument_list|(
name|tvSpec
argument_list|)
decl_stmt|;
name|fieldSet
operator|.
name|addAll
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldSet
return|;
block|}
comment|/**    * @see IndexReader#getFieldNames(IndexReader.FieldOption)    */
DECL|method|getFieldNames
specifier|public
name|Collection
name|getFieldNames
parameter_list|(
name|IndexReader
operator|.
name|FieldOption
name|fieldNames
parameter_list|)
block|{
comment|// maintain a unique set of field names
name|Set
name|fieldSet
init|=
operator|new
name|HashSet
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
name|subReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
name|subReaders
index|[
name|i
index|]
decl_stmt|;
name|Collection
name|names
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|fieldNames
argument_list|)
decl_stmt|;
name|fieldSet
operator|.
name|addAll
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldSet
return|;
block|}
block|}
end_class
begin_class
DECL|class|MultiTermEnum
class|class
name|MultiTermEnum
extends|extends
name|TermEnum
block|{
DECL|field|queue
specifier|private
name|SegmentMergeQueue
name|queue
decl_stmt|;
DECL|field|term
specifier|private
name|Term
name|term
decl_stmt|;
DECL|field|docFreq
specifier|private
name|int
name|docFreq
decl_stmt|;
DECL|method|MultiTermEnum
specifier|public
name|MultiTermEnum
parameter_list|(
name|IndexReader
index|[]
name|readers
parameter_list|,
name|int
index|[]
name|starts
parameter_list|,
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|queue
operator|=
operator|new
name|SegmentMergeQueue
argument_list|(
name|readers
operator|.
name|length
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
name|readers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
name|readers
index|[
name|i
index|]
decl_stmt|;
name|TermEnum
name|termEnum
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|termEnum
operator|=
name|reader
operator|.
name|terms
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
else|else
name|termEnum
operator|=
name|reader
operator|.
name|terms
argument_list|()
expr_stmt|;
name|SegmentMergeInfo
name|smi
init|=
operator|new
name|SegmentMergeInfo
argument_list|(
name|starts
index|[
name|i
index|]
argument_list|,
name|termEnum
argument_list|,
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|?
name|smi
operator|.
name|next
argument_list|()
else|:
name|termEnum
operator|.
name|term
argument_list|()
operator|!=
literal|null
condition|)
name|queue
operator|.
name|put
argument_list|(
name|smi
argument_list|)
expr_stmt|;
comment|// initialize queue
else|else
name|smi
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|next
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|SegmentMergeInfo
name|top
init|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|top
argument_list|()
decl_stmt|;
if|if
condition|(
name|top
operator|==
literal|null
condition|)
block|{
name|term
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
name|term
operator|=
name|top
operator|.
name|term
expr_stmt|;
name|docFreq
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|top
operator|!=
literal|null
operator|&&
name|term
operator|.
name|compareTo
argument_list|(
name|top
operator|.
name|term
argument_list|)
operator|==
literal|0
condition|)
block|{
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
name|docFreq
operator|+=
name|top
operator|.
name|termEnum
operator|.
name|docFreq
argument_list|()
expr_stmt|;
comment|// increment freq
if|if
condition|(
name|top
operator|.
name|next
argument_list|()
condition|)
name|queue
operator|.
name|put
argument_list|(
name|top
argument_list|)
expr_stmt|;
comment|// restore queue
else|else
name|top
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// done with a segment
name|top
operator|=
operator|(
name|SegmentMergeInfo
operator|)
name|queue
operator|.
name|top
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|term
specifier|public
name|Term
name|term
parameter_list|()
block|{
return|return
name|term
return|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
return|return
name|docFreq
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|queue
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
begin_class
DECL|class|MultiTermDocs
class|class
name|MultiTermDocs
implements|implements
name|TermDocs
block|{
DECL|field|readers
specifier|protected
name|IndexReader
index|[]
name|readers
decl_stmt|;
DECL|field|starts
specifier|protected
name|int
index|[]
name|starts
decl_stmt|;
DECL|field|term
specifier|protected
name|Term
name|term
decl_stmt|;
DECL|field|base
specifier|protected
name|int
name|base
init|=
literal|0
decl_stmt|;
DECL|field|pointer
specifier|protected
name|int
name|pointer
init|=
literal|0
decl_stmt|;
DECL|field|readerTermDocs
specifier|private
name|TermDocs
index|[]
name|readerTermDocs
decl_stmt|;
DECL|field|current
specifier|protected
name|TermDocs
name|current
decl_stmt|;
comment|// == readerTermDocs[pointer]
DECL|method|MultiTermDocs
specifier|public
name|MultiTermDocs
parameter_list|(
name|IndexReader
index|[]
name|r
parameter_list|,
name|int
index|[]
name|s
parameter_list|)
block|{
name|readers
operator|=
name|r
expr_stmt|;
name|starts
operator|=
name|s
expr_stmt|;
name|readerTermDocs
operator|=
operator|new
name|TermDocs
index|[
name|r
operator|.
name|length
index|]
expr_stmt|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|base
operator|+
name|current
operator|.
name|doc
argument_list|()
return|;
block|}
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|current
operator|.
name|freq
argument_list|()
return|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|base
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|pointer
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|current
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|TermEnum
name|termEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|seek
argument_list|(
name|termEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|current
operator|!=
literal|null
operator|&&
name|current
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|pointer
operator|<
name|readers
operator|.
name|length
condition|)
block|{
name|base
operator|=
name|starts
index|[
name|pointer
index|]
expr_stmt|;
name|current
operator|=
name|termDocs
argument_list|(
name|pointer
operator|++
argument_list|)
expr_stmt|;
return|return
name|next
argument_list|()
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
comment|/** Optimized implementation. */
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|int
index|[]
name|docs
parameter_list|,
specifier|final
name|int
index|[]
name|freqs
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
while|while
condition|(
name|current
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|pointer
operator|<
name|readers
operator|.
name|length
condition|)
block|{
comment|// try next segment
name|base
operator|=
name|starts
index|[
name|pointer
index|]
expr_stmt|;
name|current
operator|=
name|termDocs
argument_list|(
name|pointer
operator|++
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
name|int
name|end
init|=
name|current
operator|.
name|read
argument_list|(
name|docs
argument_list|,
name|freqs
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|==
literal|0
condition|)
block|{
comment|// none left in segment
name|current
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// got some
specifier|final
name|int
name|b
init|=
name|base
decl_stmt|;
comment|// adjust doc numbers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
name|docs
index|[
name|i
index|]
operator|+=
name|b
expr_stmt|;
return|return
name|end
return|;
block|}
block|}
block|}
comment|/** As yet unoptimized implementation. */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
do|do
block|{
if|if
condition|(
operator|!
name|next
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
do|while
condition|(
name|target
operator|>
name|doc
argument_list|()
condition|)
do|;
return|return
literal|true
return|;
block|}
DECL|method|termDocs
specifier|private
name|TermDocs
name|termDocs
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|term
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|TermDocs
name|result
init|=
name|readerTermDocs
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
name|result
operator|=
name|readerTermDocs
index|[
name|i
index|]
operator|=
name|termDocs
argument_list|(
name|readers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|termDocs
specifier|protected
name|TermDocs
name|termDocs
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|termDocs
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
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
name|readerTermDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|readerTermDocs
index|[
name|i
index|]
operator|!=
literal|null
condition|)
name|readerTermDocs
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
begin_class
DECL|class|MultiTermPositions
class|class
name|MultiTermPositions
extends|extends
name|MultiTermDocs
implements|implements
name|TermPositions
block|{
DECL|method|MultiTermPositions
specifier|public
name|MultiTermPositions
parameter_list|(
name|IndexReader
index|[]
name|r
parameter_list|,
name|int
index|[]
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|termDocs
specifier|protected
name|TermDocs
name|termDocs
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|TermDocs
operator|)
name|reader
operator|.
name|termPositions
argument_list|()
return|;
block|}
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|TermPositions
operator|)
name|current
operator|)
operator|.
name|nextPosition
argument_list|()
return|;
block|}
block|}
end_class
end_unit
