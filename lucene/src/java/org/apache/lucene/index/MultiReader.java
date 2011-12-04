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
name|Collection
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|codecs
operator|.
name|PerDocValues
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
name|ReaderUtil
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
name|MapBackedSet
import|;
end_import
begin_comment
comment|/** An IndexReader which reads multiple indexes, appending  *  their content. */
end_comment
begin_class
DECL|class|MultiReader
specifier|public
class|class
name|MultiReader
extends|extends
name|IndexReader
implements|implements
name|Cloneable
block|{
DECL|field|subReaders
specifier|protected
name|IndexReader
index|[]
name|subReaders
decl_stmt|;
DECL|field|topLevelContext
specifier|private
specifier|final
name|ReaderContext
name|topLevelContext
decl_stmt|;
DECL|field|starts
specifier|private
name|int
index|[]
name|starts
decl_stmt|;
comment|// 1st docno for each segment
DECL|field|decrefOnClose
specifier|private
name|boolean
index|[]
name|decrefOnClose
decl_stmt|;
comment|// remember which subreaders to decRef on close
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
comment|/**   *<p>Construct a MultiReader aggregating the named set of (sub)readers.   *<p>Note that all subreaders are closed if this Multireader is closed.</p>   * @param subReaders set of (sub)readers   */
DECL|method|MultiReader
specifier|public
name|MultiReader
parameter_list|(
name|IndexReader
modifier|...
name|subReaders
parameter_list|)
throws|throws
name|IOException
block|{
name|topLevelContext
operator|=
name|initialize
argument_list|(
name|subReaders
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    *<p>Construct a MultiReader aggregating the named set of (sub)readers.    * @param closeSubReaders indicates whether the subreaders should be closed    * when this MultiReader is closed    * @param subReaders set of (sub)readers    */
DECL|method|MultiReader
specifier|public
name|MultiReader
parameter_list|(
name|IndexReader
index|[]
name|subReaders
parameter_list|,
name|boolean
name|closeSubReaders
parameter_list|)
throws|throws
name|IOException
block|{
name|topLevelContext
operator|=
name|initialize
argument_list|(
name|subReaders
argument_list|,
name|closeSubReaders
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize
specifier|private
name|ReaderContext
name|initialize
parameter_list|(
name|IndexReader
index|[]
name|subReaders
parameter_list|,
name|boolean
name|closeSubReaders
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|subReaders
operator|=
name|subReaders
operator|.
name|clone
argument_list|()
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
name|decrefOnClose
operator|=
operator|new
name|boolean
index|[
name|subReaders
operator|.
name|length
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
operator|!
name|closeSubReaders
condition|)
block|{
name|subReaders
index|[
name|i
index|]
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|decrefOnClose
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|decrefOnClose
index|[
name|i
index|]
operator|=
literal|false
expr_stmt|;
block|}
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
block|{
name|hasDeletions
operator|=
literal|true
expr_stmt|;
block|}
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
name|readerFinishedListeners
operator|=
operator|new
name|MapBackedSet
argument_list|<
name|ReaderFinishedListener
argument_list|>
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|ReaderFinishedListener
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ReaderUtil
operator|.
name|buildReaderContext
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUniqueTermCount
specifier|public
name|long
name|getUniqueTermCount
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|""
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"please use MultiFields.getFields, or wrap your IndexReader with SlowMultiReaderWrapper, if you really need a top level Fields"
argument_list|)
throw|;
block|}
comment|/**    * Tries to reopen the subreaders.    *<br>    * If one or more subreaders could be re-opened (i. e. IndexReader.openIfChanged(subReader)     * returned a new instance), then a new MultiReader instance     * is returned, otherwise this instance is returned.    *<p>    * A re-opened instance might share one or more subreaders with the old     * instance. Index modification operations result in undefined behavior    * when performed before the old instance is closed.    * (see {@link IndexReader#openIfChanged}).    *<p>    * If subreaders are shared, then the reference count of those    * readers is increased to ensure that the subreaders remain open    * until the last referring reader is closed.    *     * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error     */
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
specifier|synchronized
name|IndexReader
name|doOpenIfChanged
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|doOpenIfChanged
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/**    * Clones the subreaders.    * (see {@link IndexReader#clone()}).    *<br>    *<p>    * If subreaders are shared, then the reference count of those    * readers is increased to ensure that the subreaders remain open    * until the last referring reader is closed.    */
annotation|@
name|Override
DECL|method|clone
specifier|public
specifier|synchronized
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
name|doOpenIfChanged
argument_list|(
literal|true
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"please use MultiFields.getLiveDocs, or wrap your IndexReader with SlowMultiReaderWrapper, if you really need a top level Bits liveDocs"
argument_list|)
throw|;
block|}
comment|/**    * If clone is true then we clone each of the subreaders    * @param doClone    * @return New IndexReader, or null if open/clone is not necessary    * @throws CorruptIndexException    * @throws IOException    */
DECL|method|doOpenIfChanged
specifier|protected
name|IndexReader
name|doOpenIfChanged
parameter_list|(
name|boolean
name|doClone
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
name|IndexReader
index|[]
name|newSubReaders
init|=
operator|new
name|IndexReader
index|[
name|subReaders
operator|.
name|length
index|]
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
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
block|{
if|if
condition|(
name|doClone
condition|)
block|{
name|newSubReaders
index|[
name|i
index|]
operator|=
operator|(
name|IndexReader
operator|)
name|subReaders
index|[
name|i
index|]
operator|.
name|clone
argument_list|()
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|IndexReader
name|newSubReader
init|=
name|IndexReader
operator|.
name|openIfChanged
argument_list|(
name|subReaders
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|newSubReader
operator|!=
literal|null
condition|)
block|{
name|newSubReaders
index|[
name|i
index|]
operator|=
name|newSubReader
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|newSubReaders
index|[
name|i
index|]
operator|=
name|subReaders
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
operator|&&
name|changed
condition|)
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
name|newSubReaders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|newSubReaders
index|[
name|i
index|]
operator|!=
name|subReaders
index|[
name|i
index|]
condition|)
block|{
try|try
block|{
name|newSubReaders
index|[
name|i
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{
comment|// keep going - we want to clean up as much as possible
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|changed
condition|)
block|{
name|boolean
index|[]
name|newDecrefOnClose
init|=
operator|new
name|boolean
index|[
name|subReaders
operator|.
name|length
index|]
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
if|if
condition|(
name|newSubReaders
index|[
name|i
index|]
operator|==
name|subReaders
index|[
name|i
index|]
condition|)
block|{
name|newSubReaders
index|[
name|i
index|]
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|newDecrefOnClose
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|MultiReader
name|mr
init|=
operator|new
name|MultiReader
argument_list|(
name|newSubReaders
argument_list|)
decl_stmt|;
name|mr
operator|.
name|decrefOnClose
operator|=
name|newDecrefOnClose
expr_stmt|;
return|return
name|mr
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTermVectors
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|docID
argument_list|)
decl_stmt|;
comment|// find segment num
return|return
name|subReaders
index|[
name|i
index|]
operator|.
name|getTermVectors
argument_list|(
name|docID
operator|-
name|starts
index|[
name|i
index|]
argument_list|)
return|;
comment|// dispatch to segment
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
comment|// NOTE: multiple threads may wind up init'ing
comment|// numDocs... but that's harmless
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
annotation|@
name|Override
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
DECL|method|document
specifier|public
name|void
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|int
name|i
init|=
name|readerIndex
argument_list|(
name|docID
argument_list|)
decl_stmt|;
comment|// find segment num
name|subReaders
index|[
name|i
index|]
operator|.
name|document
argument_list|(
name|docID
operator|-
name|starts
index|[
name|i
index|]
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
comment|// dispatch to segment reader
block|}
annotation|@
name|Override
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|hasDeletions
return|;
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
return|return
name|DirectoryReader
operator|.
name|readerIndex
argument_list|(
name|n
argument_list|,
name|this
operator|.
name|starts
argument_list|,
name|this
operator|.
name|subReaders
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
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
block|{
if|if
condition|(
name|subReaders
index|[
name|i
index|]
operator|.
name|hasNorms
argument_list|(
name|field
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"please use MultiNorms.norms, or wrap your IndexReader with SlowMultiReaderWrapper, if you really need a top level norms"
argument_list|)
throw|;
block|}
annotation|@
name|Override
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
name|ensureOpen
argument_list|()
expr_stmt|;
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
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
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
block|{
name|total
operator|+=
name|subReaders
index|[
name|i
index|]
operator|.
name|docFreq
argument_list|(
name|field
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
annotation|@
name|Override
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
block|{
if|if
condition|(
name|decrefOnClose
index|[
name|i
index|]
condition|)
block|{
name|subReaders
index|[
name|i
index|]
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|subReaders
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
annotation|@
name|Override
DECL|method|getFieldNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|(
name|IndexReader
operator|.
name|FieldOption
name|fieldNames
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|DirectoryReader
operator|.
name|getFieldNames
argument_list|(
name|fieldNames
argument_list|,
name|this
operator|.
name|subReaders
argument_list|)
return|;
block|}
comment|/**    * Checks recursively if all subreaders are up to date.     */
annotation|@
name|Override
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
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
block|{
if|if
condition|(
operator|!
name|subReaders
index|[
name|i
index|]
operator|.
name|isCurrent
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// all subreaders are up to date
return|return
literal|true
return|;
block|}
comment|/** Not implemented.    * @throws UnsupportedOperationException    */
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"MultiReader does not support this method."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getSequentialSubReaders
specifier|public
name|IndexReader
index|[]
name|getSequentialSubReaders
parameter_list|()
block|{
return|return
name|subReaders
return|;
block|}
annotation|@
name|Override
DECL|method|getTopReaderContext
specifier|public
name|ReaderContext
name|getTopReaderContext
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|topLevelContext
return|;
block|}
annotation|@
name|Override
DECL|method|addReaderFinishedListener
specifier|public
name|void
name|addReaderFinishedListener
parameter_list|(
name|ReaderFinishedListener
name|listener
parameter_list|)
block|{
name|super
operator|.
name|addReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexReader
name|sub
range|:
name|subReaders
control|)
block|{
name|sub
operator|.
name|addReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeReaderFinishedListener
specifier|public
name|void
name|removeReaderFinishedListener
parameter_list|(
name|ReaderFinishedListener
name|listener
parameter_list|)
block|{
name|super
operator|.
name|removeReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexReader
name|sub
range|:
name|subReaders
control|)
block|{
name|sub
operator|.
name|removeReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|perDocValues
specifier|public
name|PerDocValues
name|perDocValues
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"please use MultiPerDocValues#getPerDocs, or wrap your IndexReader with SlowMultiReaderWrapper, if you really need a top level Fields"
argument_list|)
throw|;
block|}
block|}
end_class
end_unit
