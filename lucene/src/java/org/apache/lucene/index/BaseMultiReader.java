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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
begin_class
DECL|class|BaseMultiReader
specifier|abstract
class|class
name|BaseMultiReader
parameter_list|<
name|R
extends|extends
name|IndexReader
parameter_list|>
extends|extends
name|IndexReader
block|{
DECL|field|subReaders
specifier|protected
specifier|final
name|R
index|[]
name|subReaders
decl_stmt|;
DECL|field|starts
specifier|protected
specifier|final
name|int
index|[]
name|starts
decl_stmt|;
comment|// 1st docno for each segment
DECL|field|topLevelContext
specifier|private
specifier|final
name|ReaderContext
name|topLevelContext
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|hasDeletions
specifier|private
specifier|final
name|boolean
name|hasDeletions
decl_stmt|;
DECL|method|BaseMultiReader
specifier|protected
name|BaseMultiReader
parameter_list|(
name|R
index|[]
name|subReaders
parameter_list|)
throws|throws
name|IOException
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
name|int
name|maxDoc
init|=
literal|0
decl_stmt|,
name|numDocs
init|=
literal|0
decl_stmt|;
name|boolean
name|hasDeletions
init|=
literal|false
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
name|numDocs
operator|+=
name|subReaders
index|[
name|i
index|]
operator|.
name|numDocs
argument_list|()
expr_stmt|;
comment|// compute numDocs
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
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
name|this
operator|.
name|hasDeletions
operator|=
name|hasDeletions
expr_stmt|;
name|topLevelContext
operator|=
name|ReaderUtil
operator|.
name|buildReaderContext
argument_list|(
name|this
argument_list|)
expr_stmt|;
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
annotation|@
name|Override
DECL|method|doOpenIfChanged
specifier|protected
specifier|abstract
name|IndexReader
name|doOpenIfChanged
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
function_decl|;
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
specifier|final
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
return|return
name|numDocs
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|public
specifier|final
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
specifier|final
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
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|hasDeletions
return|;
block|}
comment|/** Helper method for subclasses to get the corresponding reader for a doc ID */
DECL|method|readerIndex
specifier|protected
specifier|final
name|int
name|readerIndex
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
name|docID
operator|>=
name|maxDoc
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docID must be>= 0 and< maxDoc="
operator|+
name|maxDoc
operator|+
literal|" (got docID="
operator|+
name|docID
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docID
argument_list|,
name|this
operator|.
name|starts
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
comment|// maintain a unique set of field names
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fieldSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexReader
name|reader
range|:
name|subReaders
control|)
block|{
name|fieldSet
operator|.
name|addAll
argument_list|(
name|reader
operator|.
name|getFieldNames
argument_list|(
name|fieldNames
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldSet
return|;
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
return|return
name|topLevelContext
return|;
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
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
literal|"please use MultiDocValues#getDocValues, or wrap your IndexReader with SlowMultiReaderWrapper, if you really need a top level DocValues"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|normValues
specifier|public
name|DocValues
name|normValues
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
literal|"please use MultiDocValues#getNormValues, or wrap your IndexReader with SlowMultiReaderWrapper, if you really need a top level Norm DocValues "
argument_list|)
throw|;
block|}
block|}
end_class
end_unit
