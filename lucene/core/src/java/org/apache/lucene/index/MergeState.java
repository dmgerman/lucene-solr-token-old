begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|List
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
name|codecs
operator|.
name|DocValuesProducer
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
name|codecs
operator|.
name|FieldsProducer
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
name|codecs
operator|.
name|NormsProducer
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
name|codecs
operator|.
name|PointsReader
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
name|codecs
operator|.
name|StoredFieldsReader
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
name|codecs
operator|.
name|TermVectorsReader
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
name|InfoStream
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
name|PackedInts
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
name|PackedLongValues
import|;
end_import
begin_comment
comment|/** Holds common state used during segment merging.  *  * @lucene.experimental */
end_comment
begin_class
DECL|class|MergeState
specifier|public
class|class
name|MergeState
block|{
comment|/** {@link SegmentInfo} of the newly merged segment. */
DECL|field|segmentInfo
specifier|public
specifier|final
name|SegmentInfo
name|segmentInfo
decl_stmt|;
comment|/** {@link FieldInfos} of the newly merged segment. */
DECL|field|mergeFieldInfos
specifier|public
name|FieldInfos
name|mergeFieldInfos
decl_stmt|;
comment|/** Stored field producers being merged */
DECL|field|storedFieldsReaders
specifier|public
specifier|final
name|StoredFieldsReader
index|[]
name|storedFieldsReaders
decl_stmt|;
comment|/** Term vector producers being merged */
DECL|field|termVectorsReaders
specifier|public
specifier|final
name|TermVectorsReader
index|[]
name|termVectorsReaders
decl_stmt|;
comment|/** Norms producers being merged */
DECL|field|normsProducers
specifier|public
specifier|final
name|NormsProducer
index|[]
name|normsProducers
decl_stmt|;
comment|/** DocValues producers being merged */
DECL|field|docValuesProducers
specifier|public
specifier|final
name|DocValuesProducer
index|[]
name|docValuesProducers
decl_stmt|;
comment|/** FieldInfos being merged */
DECL|field|fieldInfos
specifier|public
specifier|final
name|FieldInfos
index|[]
name|fieldInfos
decl_stmt|;
comment|/** Live docs for each reader */
DECL|field|liveDocs
specifier|public
specifier|final
name|Bits
index|[]
name|liveDocs
decl_stmt|;
comment|/** Maps docIDs around deletions. */
DECL|field|docMaps
specifier|public
specifier|final
name|DocMap
index|[]
name|docMaps
decl_stmt|;
comment|/** Postings to merge */
DECL|field|fieldsProducers
specifier|public
specifier|final
name|FieldsProducer
index|[]
name|fieldsProducers
decl_stmt|;
comment|/** Point readers to merge */
DECL|field|pointsReaders
specifier|public
specifier|final
name|PointsReader
index|[]
name|pointsReaders
decl_stmt|;
comment|/** New docID base per reader. */
DECL|field|docBase
specifier|public
specifier|final
name|int
index|[]
name|docBase
decl_stmt|;
comment|/** Max docs per reader */
DECL|field|maxDocs
specifier|public
specifier|final
name|int
index|[]
name|maxDocs
decl_stmt|;
comment|/** InfoStream for debugging messages. */
DECL|field|infoStream
specifier|public
specifier|final
name|InfoStream
name|infoStream
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|MergeState
name|MergeState
parameter_list|(
name|List
argument_list|<
name|CodecReader
argument_list|>
name|readers
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|InfoStream
name|infoStream
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numReaders
init|=
name|readers
operator|.
name|size
argument_list|()
decl_stmt|;
name|docMaps
operator|=
operator|new
name|DocMap
index|[
name|numReaders
index|]
expr_stmt|;
name|docBase
operator|=
operator|new
name|int
index|[
name|numReaders
index|]
expr_stmt|;
name|maxDocs
operator|=
operator|new
name|int
index|[
name|numReaders
index|]
expr_stmt|;
name|fieldsProducers
operator|=
operator|new
name|FieldsProducer
index|[
name|numReaders
index|]
expr_stmt|;
name|normsProducers
operator|=
operator|new
name|NormsProducer
index|[
name|numReaders
index|]
expr_stmt|;
name|storedFieldsReaders
operator|=
operator|new
name|StoredFieldsReader
index|[
name|numReaders
index|]
expr_stmt|;
name|termVectorsReaders
operator|=
operator|new
name|TermVectorsReader
index|[
name|numReaders
index|]
expr_stmt|;
name|docValuesProducers
operator|=
operator|new
name|DocValuesProducer
index|[
name|numReaders
index|]
expr_stmt|;
name|pointsReaders
operator|=
operator|new
name|PointsReader
index|[
name|numReaders
index|]
expr_stmt|;
name|fieldInfos
operator|=
operator|new
name|FieldInfos
index|[
name|numReaders
index|]
expr_stmt|;
name|liveDocs
operator|=
operator|new
name|Bits
index|[
name|numReaders
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
name|numReaders
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|CodecReader
name|reader
init|=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|maxDocs
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|liveDocs
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
name|fieldInfos
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
expr_stmt|;
name|normsProducers
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getNormsReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|normsProducers
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|normsProducers
index|[
name|i
index|]
operator|=
name|normsProducers
index|[
name|i
index|]
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|docValuesProducers
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getDocValuesReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|docValuesProducers
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|docValuesProducers
index|[
name|i
index|]
operator|=
name|docValuesProducers
index|[
name|i
index|]
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|storedFieldsReaders
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getFieldsReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|storedFieldsReaders
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|storedFieldsReaders
index|[
name|i
index|]
operator|=
name|storedFieldsReaders
index|[
name|i
index|]
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|termVectorsReaders
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getTermVectorsReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|termVectorsReaders
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|termVectorsReaders
index|[
name|i
index|]
operator|=
name|termVectorsReaders
index|[
name|i
index|]
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|fieldsProducers
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getPostingsReader
argument_list|()
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
name|pointsReaders
index|[
name|i
index|]
operator|=
name|reader
operator|.
name|getPointsReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|pointsReaders
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|pointsReaders
index|[
name|i
index|]
operator|=
name|pointsReaders
index|[
name|i
index|]
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
block|}
name|this
operator|.
name|segmentInfo
operator|=
name|segmentInfo
expr_stmt|;
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
name|setDocMaps
argument_list|(
name|readers
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: removes any "all deleted" readers from mergeState.readers
DECL|method|setDocMaps
specifier|private
name|void
name|setDocMaps
parameter_list|(
name|List
argument_list|<
name|CodecReader
argument_list|>
name|readers
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numReaders
init|=
name|maxDocs
operator|.
name|length
decl_stmt|;
comment|// Remap docIDs
name|int
name|docBase
init|=
literal|0
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
name|numReaders
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|CodecReader
name|reader
init|=
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|this
operator|.
name|docBase
index|[
name|i
index|]
operator|=
name|docBase
expr_stmt|;
specifier|final
name|DocMap
name|docMap
init|=
name|DocMap
operator|.
name|build
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|docMaps
index|[
name|i
index|]
operator|=
name|docMap
expr_stmt|;
name|docBase
operator|+=
name|docMap
operator|.
name|numDocs
argument_list|()
expr_stmt|;
block|}
name|segmentInfo
operator|.
name|setMaxDoc
argument_list|(
name|docBase
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remaps docids around deletes during merge    */
DECL|class|DocMap
specifier|public
specifier|static
specifier|abstract
class|class
name|DocMap
block|{
DECL|method|DocMap
name|DocMap
parameter_list|()
block|{}
comment|/** Returns the mapped docID corresponding to the provided one. */
DECL|method|get
specifier|public
specifier|abstract
name|int
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
function_decl|;
comment|/** Returns the total number of documents, ignoring      *  deletions. */
DECL|method|maxDoc
specifier|public
specifier|abstract
name|int
name|maxDoc
parameter_list|()
function_decl|;
comment|/** Returns the number of not-deleted documents. */
DECL|method|numDocs
specifier|public
specifier|final
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|maxDoc
argument_list|()
operator|-
name|numDeletedDocs
argument_list|()
return|;
block|}
comment|/** Returns the number of deleted documents. */
DECL|method|numDeletedDocs
specifier|public
specifier|abstract
name|int
name|numDeletedDocs
parameter_list|()
function_decl|;
comment|/** Returns true if there are any deletions. */
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
name|numDeletedDocs
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/** Creates a {@link DocMap} instance appropriate for      *  this reader. */
DECL|method|build
specifier|public
specifier|static
name|DocMap
name|build
parameter_list|(
name|CodecReader
name|reader
parameter_list|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|reader
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
return|return
operator|new
name|NoDelDocMap
argument_list|(
name|maxDoc
argument_list|)
return|;
block|}
specifier|final
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
return|return
name|build
argument_list|(
name|maxDoc
argument_list|,
name|liveDocs
argument_list|)
return|;
block|}
DECL|method|build
specifier|static
name|DocMap
name|build
parameter_list|(
specifier|final
name|int
name|maxDoc
parameter_list|,
specifier|final
name|Bits
name|liveDocs
parameter_list|)
block|{
assert|assert
name|liveDocs
operator|!=
literal|null
assert|;
specifier|final
name|PackedLongValues
operator|.
name|Builder
name|docMapBuilder
init|=
name|PackedLongValues
operator|.
name|monotonicBuilder
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
name|int
name|del
init|=
literal|0
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
name|maxDoc
condition|;
operator|++
name|i
control|)
block|{
name|docMapBuilder
operator|.
name|add
argument_list|(
name|i
operator|-
name|del
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
operator|++
name|del
expr_stmt|;
block|}
block|}
specifier|final
name|PackedLongValues
name|docMap
init|=
name|docMapBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDeletedDocs
init|=
name|del
decl_stmt|;
assert|assert
name|docMap
operator|.
name|size
argument_list|()
operator|==
name|maxDoc
assert|;
return|return
operator|new
name|DocMap
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
if|if
condition|(
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
operator|(
name|int
operator|)
name|docMap
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|numDeletedDocs
parameter_list|()
block|{
return|return
name|numDeletedDocs
return|;
block|}
block|}
return|;
block|}
block|}
DECL|class|NoDelDocMap
specifier|private
specifier|static
specifier|final
class|class
name|NoDelDocMap
extends|extends
name|DocMap
block|{
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|NoDelDocMap
name|NoDelDocMap
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|docID
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
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
DECL|method|numDeletedDocs
specifier|public
name|int
name|numDeletedDocs
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
end_class
end_unit
