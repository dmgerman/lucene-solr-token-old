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
name|store
operator|.
name|Directory
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
comment|/** Holds the CheckAbort instance, which is invoked    *  periodically to see if the merge has been aborted. */
DECL|field|checkAbort
specifier|public
specifier|final
name|CheckAbort
name|checkAbort
decl_stmt|;
comment|/** InfoStream for debugging messages. */
DECL|field|infoStream
specifier|public
specifier|final
name|InfoStream
name|infoStream
decl_stmt|;
comment|/** Counter used for periodic calls to checkAbort    * @lucene.internal */
DECL|field|checkAbortCount
specifier|public
name|int
name|checkAbortCount
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|MergeState
name|MergeState
parameter_list|(
name|List
argument_list|<
name|LeafReader
argument_list|>
name|readers
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|InfoStream
name|infoStream
parameter_list|,
name|CheckAbort
name|checkAbort
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
name|LeafReader
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
name|NormsProducer
name|normsProducer
decl_stmt|;
name|DocValuesProducer
name|docValuesProducer
decl_stmt|;
name|StoredFieldsReader
name|storedFieldsReader
decl_stmt|;
name|TermVectorsReader
name|termVectorsReader
decl_stmt|;
name|FieldsProducer
name|fieldsProducer
decl_stmt|;
if|if
condition|(
name|reader
operator|instanceof
name|SegmentReader
condition|)
block|{
name|SegmentReader
name|segmentReader
init|=
operator|(
name|SegmentReader
operator|)
name|reader
decl_stmt|;
name|normsProducer
operator|=
name|segmentReader
operator|.
name|getNormsReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|normsProducer
operator|!=
literal|null
condition|)
block|{
name|normsProducer
operator|=
name|normsProducer
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|docValuesProducer
operator|=
name|segmentReader
operator|.
name|getDocValuesReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|docValuesProducer
operator|!=
literal|null
condition|)
block|{
name|docValuesProducer
operator|=
name|docValuesProducer
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|storedFieldsReader
operator|=
name|segmentReader
operator|.
name|getFieldsReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|storedFieldsReader
operator|!=
literal|null
condition|)
block|{
name|storedFieldsReader
operator|=
name|storedFieldsReader
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|termVectorsReader
operator|=
name|segmentReader
operator|.
name|getTermVectorsReader
argument_list|()
expr_stmt|;
if|if
condition|(
name|termVectorsReader
operator|!=
literal|null
condition|)
block|{
name|termVectorsReader
operator|=
name|termVectorsReader
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
name|fieldsProducer
operator|=
name|segmentReader
operator|.
name|fields
argument_list|()
operator|.
name|getMergeInstance
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// A "foreign" reader
name|normsProducer
operator|=
name|readerToNormsProducer
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|docValuesProducer
operator|=
name|readerToDocValuesProducer
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|storedFieldsReader
operator|=
name|readerToStoredFieldsReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|termVectorsReader
operator|=
name|readerToTermVectorsReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|fieldsProducer
operator|=
name|readerToFieldsProducer
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|normsProducers
index|[
name|i
index|]
operator|=
name|normsProducer
expr_stmt|;
name|docValuesProducers
index|[
name|i
index|]
operator|=
name|docValuesProducer
expr_stmt|;
name|storedFieldsReaders
index|[
name|i
index|]
operator|=
name|storedFieldsReader
expr_stmt|;
name|termVectorsReaders
index|[
name|i
index|]
operator|=
name|termVectorsReader
expr_stmt|;
name|fieldsProducers
index|[
name|i
index|]
operator|=
name|fieldsProducer
expr_stmt|;
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
name|this
operator|.
name|checkAbort
operator|=
name|checkAbort
expr_stmt|;
name|setDocMaps
argument_list|(
name|readers
argument_list|)
expr_stmt|;
block|}
DECL|method|readerToNormsProducer
specifier|private
name|NormsProducer
name|readerToNormsProducer
parameter_list|(
specifier|final
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|NormsProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NumericDocValues
name|getNorms
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|getNormValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We already checkIntegrity the entire reader up front in SegmentMerger
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
DECL|method|readerToDocValuesProducer
specifier|private
name|DocValuesProducer
name|readerToDocValuesProducer
parameter_list|(
specifier|final
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|DocValuesProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|getNumericDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|getBinaryDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|getSortedDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SortedNumericDocValues
name|getSortedNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|getSortedNumericDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SortedSetDocValues
name|getSortedSet
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Bits
name|getDocsWithField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|getDocsWithField
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We already checkIntegrity the entire reader up front in SegmentMerger
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
DECL|method|readerToStoredFieldsReader
specifier|private
name|StoredFieldsReader
name|readerToStoredFieldsReader
parameter_list|(
specifier|final
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|StoredFieldsReader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visitDocument
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|reader
operator|.
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|StoredFieldsReader
name|clone
parameter_list|()
block|{
return|return
name|readerToStoredFieldsReader
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We already checkIntegrity the entire reader up front in SegmentMerger
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
DECL|method|readerToTermVectorsReader
specifier|private
name|TermVectorsReader
name|readerToTermVectorsReader
parameter_list|(
specifier|final
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|TermVectorsReader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Fields
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|TermVectorsReader
name|clone
parameter_list|()
block|{
return|return
name|readerToTermVectorsReader
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We already checkIntegrity the entire reader up front in SegmentMerger
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
DECL|method|readerToFieldsProducer
specifier|private
name|FieldsProducer
name|readerToFieldsProducer
parameter_list|(
specifier|final
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
return|return
operator|new
name|FieldsProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|fields
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We already checkIntegrity the entire reader up front in SegmentMerger
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
comment|// NOTE: removes any "all deleted" readers from mergeState.readers
DECL|method|setDocMaps
specifier|private
name|void
name|setDocMaps
parameter_list|(
name|List
argument_list|<
name|LeafReader
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
name|LeafReader
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
name|setDocCount
argument_list|(
name|docBase
argument_list|)
expr_stmt|;
block|}
comment|/**    * Class for recording units of work when merging segments.    */
DECL|class|CheckAbort
specifier|public
specifier|static
class|class
name|CheckAbort
block|{
DECL|field|workCount
specifier|private
name|double
name|workCount
decl_stmt|;
DECL|field|merge
specifier|private
specifier|final
name|MergePolicy
operator|.
name|OneMerge
name|merge
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
comment|/** Creates a #CheckAbort instance. */
DECL|method|CheckAbort
specifier|public
name|CheckAbort
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|this
operator|.
name|merge
operator|=
name|merge
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
comment|/**      * Records the fact that roughly units amount of work      * have been done since this method was last called.      * When adding time-consuming code into SegmentMerger,      * you should test different values for units to ensure      * that the time in between calls to merge.checkAborted      * is up to ~ 1 second.      */
DECL|method|work
specifier|public
name|void
name|work
parameter_list|(
name|double
name|units
parameter_list|)
throws|throws
name|MergePolicy
operator|.
name|MergeAbortedException
block|{
name|workCount
operator|+=
name|units
expr_stmt|;
if|if
condition|(
name|workCount
operator|>=
literal|10000.0
condition|)
block|{
name|merge
operator|.
name|checkAborted
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|workCount
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/** If you use this: IW.close(false) cannot abort your merge!      * @lucene.internal */
DECL|field|NONE
specifier|static
specifier|final
name|MergeState
operator|.
name|CheckAbort
name|NONE
init|=
operator|new
name|MergeState
operator|.
name|CheckAbort
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|work
parameter_list|(
name|double
name|units
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
decl_stmt|;
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
name|LeafReader
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
