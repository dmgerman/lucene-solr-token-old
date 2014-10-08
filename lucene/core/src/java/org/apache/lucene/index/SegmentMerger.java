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
name|Codec
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
name|DocValuesConsumer
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
name|FieldsConsumer
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
name|NormsConsumer
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
name|StoredFieldsWriter
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
name|TermVectorsWriter
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
name|store
operator|.
name|IOContext
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
name|IOUtils
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
begin_comment
comment|/**  * The SegmentMerger class combines two or more Segments, represented by an  * IndexReader, into a single Segment.  Call the merge method to combine the  * segments.  *  * @see #merge  */
end_comment
begin_class
DECL|class|SegmentMerger
specifier|final
class|class
name|SegmentMerger
block|{
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|codec
specifier|private
specifier|final
name|Codec
name|codec
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|IOContext
name|context
decl_stmt|;
DECL|field|mergeState
specifier|final
name|MergeState
name|mergeState
decl_stmt|;
DECL|field|fieldInfosBuilder
specifier|private
specifier|final
name|FieldInfos
operator|.
name|Builder
name|fieldInfosBuilder
decl_stmt|;
comment|// note, just like in codec apis Directory 'dir' is NOT the same as segmentInfo.dir!!
DECL|method|SegmentMerger
name|SegmentMerger
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
name|Directory
name|dir
parameter_list|,
name|MergeState
operator|.
name|CheckAbort
name|checkAbort
parameter_list|,
name|FieldInfos
operator|.
name|FieldNumbers
name|fieldNumbers
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// validate incoming readers
for|for
control|(
name|LeafReader
name|reader
range|:
name|readers
control|)
block|{
if|if
condition|(
operator|(
name|reader
operator|instanceof
name|SegmentReader
operator|)
operator|==
literal|false
condition|)
block|{
comment|// We only validate foreign readers up front: each index component
comment|// calls .checkIntegrity itself for each incoming producer
name|reader
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
block|}
name|mergeState
operator|=
operator|new
name|MergeState
argument_list|(
name|readers
argument_list|,
name|segmentInfo
argument_list|,
name|infoStream
argument_list|,
name|checkAbort
argument_list|)
expr_stmt|;
name|directory
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|codec
operator|=
name|segmentInfo
operator|.
name|getCodec
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|fieldInfosBuilder
operator|=
operator|new
name|FieldInfos
operator|.
name|Builder
argument_list|(
name|fieldNumbers
argument_list|)
expr_stmt|;
block|}
comment|/** True if any merging should happen */
DECL|method|shouldMerge
name|boolean
name|shouldMerge
parameter_list|()
block|{
return|return
name|mergeState
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/**    * Merges the readers into the directory passed to the constructor    * @return The number of documents that were merged    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|merge
name|MergeState
name|merge
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|shouldMerge
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Merge would result in 0 document segment"
argument_list|)
throw|;
block|}
comment|// NOTE: it's important to add calls to
comment|// checkAbort.work(...) if you make any changes to this
comment|// method that will spend alot of time.  The frequency
comment|// of this check impacts how long
comment|// IndexWriter.close(false) takes to actually stop the
comment|// background merge threads.
name|mergeFieldInfos
argument_list|()
expr_stmt|;
name|long
name|t0
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|t0
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
name|int
name|numMerged
init|=
name|mergeFields
argument_list|()
decl_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
operator|(
operator|(
name|t1
operator|-
name|t0
operator|)
operator|/
literal|1000000
operator|)
operator|+
literal|" msec to merge stored fields ["
operator|+
name|numMerged
operator|+
literal|" docs]"
argument_list|)
expr_stmt|;
block|}
assert|assert
name|numMerged
operator|==
name|mergeState
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
operator|:
literal|"numMerged="
operator|+
name|numMerged
operator|+
literal|" vs mergeState.segmentInfo.getDocCount()="
operator|+
name|mergeState
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
assert|;
specifier|final
name|SegmentWriteState
name|segmentWriteState
init|=
operator|new
name|SegmentWriteState
argument_list|(
name|mergeState
operator|.
name|infoStream
argument_list|,
name|directory
argument_list|,
name|mergeState
operator|.
name|segmentInfo
argument_list|,
name|mergeState
operator|.
name|mergeFieldInfos
argument_list|,
literal|null
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|t0
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
name|mergeTerms
argument_list|(
name|segmentWriteState
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
operator|(
operator|(
name|t1
operator|-
name|t0
operator|)
operator|/
literal|1000000
operator|)
operator|+
literal|" msec to merge postings ["
operator|+
name|numMerged
operator|+
literal|" docs]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|t0
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mergeState
operator|.
name|mergeFieldInfos
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|mergeDocValues
argument_list|(
name|segmentWriteState
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
operator|(
operator|(
name|t1
operator|-
name|t0
operator|)
operator|/
literal|1000000
operator|)
operator|+
literal|" msec to merge doc values ["
operator|+
name|numMerged
operator|+
literal|" docs]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergeState
operator|.
name|mergeFieldInfos
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|t0
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
name|mergeNorms
argument_list|(
name|segmentWriteState
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
operator|(
operator|(
name|t1
operator|-
name|t0
operator|)
operator|/
literal|1000000
operator|)
operator|+
literal|" msec to merge norms ["
operator|+
name|numMerged
operator|+
literal|" docs]"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|mergeState
operator|.
name|mergeFieldInfos
operator|.
name|hasVectors
argument_list|()
condition|)
block|{
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|t0
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
name|numMerged
operator|=
name|mergeVectors
argument_list|()
expr_stmt|;
if|if
condition|(
name|mergeState
operator|.
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"SM"
argument_list|)
condition|)
block|{
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|mergeState
operator|.
name|infoStream
operator|.
name|message
argument_list|(
literal|"SM"
argument_list|,
operator|(
operator|(
name|t1
operator|-
name|t0
operator|)
operator|/
literal|1000000
operator|)
operator|+
literal|" msec to merge vectors ["
operator|+
name|numMerged
operator|+
literal|" docs]"
argument_list|)
expr_stmt|;
block|}
assert|assert
name|numMerged
operator|==
name|mergeState
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
assert|;
block|}
comment|// write the merged infos
name|codec
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|write
argument_list|(
name|directory
argument_list|,
name|mergeState
operator|.
name|segmentInfo
argument_list|,
literal|""
argument_list|,
name|mergeState
operator|.
name|mergeFieldInfos
argument_list|,
name|context
argument_list|)
expr_stmt|;
return|return
name|mergeState
return|;
block|}
DECL|method|mergeDocValues
specifier|private
name|void
name|mergeDocValues
parameter_list|(
name|SegmentWriteState
name|segmentWriteState
parameter_list|)
throws|throws
name|IOException
block|{
name|DocValuesConsumer
name|consumer
init|=
name|codec
operator|.
name|docValuesFormat
argument_list|()
operator|.
name|fieldsConsumer
argument_list|(
name|segmentWriteState
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|mergeNorms
specifier|private
name|void
name|mergeNorms
parameter_list|(
name|SegmentWriteState
name|segmentWriteState
parameter_list|)
throws|throws
name|IOException
block|{
name|NormsConsumer
name|consumer
init|=
name|codec
operator|.
name|normsFormat
argument_list|()
operator|.
name|normsConsumer
argument_list|(
name|segmentWriteState
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|mergeFieldInfos
specifier|public
name|void
name|mergeFieldInfos
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|FieldInfos
name|readerFieldInfos
range|:
name|mergeState
operator|.
name|fieldInfos
control|)
block|{
for|for
control|(
name|FieldInfo
name|fi
range|:
name|readerFieldInfos
control|)
block|{
name|fieldInfosBuilder
operator|.
name|add
argument_list|(
name|fi
argument_list|)
expr_stmt|;
block|}
block|}
name|mergeState
operator|.
name|mergeFieldInfos
operator|=
name|fieldInfosBuilder
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
comment|/**    * Merge stored fields from each of the segments into the new one.    * @return The number of documents in all of the readers    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|mergeFields
specifier|private
name|int
name|mergeFields
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|StoredFieldsWriter
name|fieldsWriter
init|=
name|codec
operator|.
name|storedFieldsFormat
argument_list|()
operator|.
name|fieldsWriter
argument_list|(
name|directory
argument_list|,
name|mergeState
operator|.
name|segmentInfo
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|int
name|numDocs
decl_stmt|;
try|try
block|{
name|numDocs
operator|=
name|fieldsWriter
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|fieldsWriter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|fieldsWriter
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|numDocs
return|;
block|}
comment|/**    * Merge the TermVectors from each of the segments into the new one.    * @throws IOException if there is a low-level IO error    */
DECL|method|mergeVectors
specifier|private
name|int
name|mergeVectors
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|TermVectorsWriter
name|termVectorsWriter
init|=
name|codec
operator|.
name|termVectorsFormat
argument_list|()
operator|.
name|vectorsWriter
argument_list|(
name|directory
argument_list|,
name|mergeState
operator|.
name|segmentInfo
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|int
name|numDocs
decl_stmt|;
try|try
block|{
name|numDocs
operator|=
name|termVectorsWriter
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|termVectorsWriter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|termVectorsWriter
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|numDocs
return|;
block|}
DECL|method|mergeTerms
specifier|private
name|void
name|mergeTerms
parameter_list|(
name|SegmentWriteState
name|segmentWriteState
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldsConsumer
name|consumer
init|=
name|codec
operator|.
name|postingsFormat
argument_list|()
operator|.
name|fieldsConsumer
argument_list|(
name|segmentWriteState
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|consumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
