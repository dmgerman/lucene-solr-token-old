begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene3x
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene3x
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
name|DocValuesFormat
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
name|FieldInfosFormat
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
name|LiveDocsFormat
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
name|PerDocConsumer
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
name|PerDocProducer
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
name|PostingsFormat
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
name|SegmentInfosFormat
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
name|StoredFieldsFormat
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
name|TermVectorsFormat
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
name|lucene40
operator|.
name|Lucene40LiveDocsFormat
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
name|lucene40
operator|.
name|Lucene40StoredFieldsFormat
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
name|IndexFileNames
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
name|PerDocWriteState
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
name|SegmentInfo
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
name|SegmentReadState
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
name|MutableBits
import|;
end_import
begin_comment
comment|/**  * Supports the Lucene 3.x index format (readonly)  */
end_comment
begin_class
DECL|class|Lucene3xCodec
specifier|public
class|class
name|Lucene3xCodec
extends|extends
name|Codec
block|{
DECL|method|Lucene3xCodec
specifier|public
name|Lucene3xCodec
parameter_list|()
block|{
name|super
argument_list|(
literal|"Lucene3x"
argument_list|)
expr_stmt|;
block|}
DECL|field|postingsFormat
specifier|private
specifier|final
name|PostingsFormat
name|postingsFormat
init|=
operator|new
name|Lucene3xPostingsFormat
argument_list|()
decl_stmt|;
comment|// TODO: this should really be a different impl
DECL|field|fieldsFormat
specifier|private
specifier|final
name|StoredFieldsFormat
name|fieldsFormat
init|=
operator|new
name|Lucene40StoredFieldsFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|StoredFieldsWriter
name|fieldsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this codec can only be used for reading"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
DECL|field|vectorsFormat
specifier|private
specifier|final
name|TermVectorsFormat
name|vectorsFormat
init|=
operator|new
name|Lucene3xTermVectorsFormat
argument_list|()
decl_stmt|;
DECL|field|fieldInfosFormat
specifier|private
specifier|final
name|FieldInfosFormat
name|fieldInfosFormat
init|=
operator|new
name|Lucene3xFieldInfosFormat
argument_list|()
decl_stmt|;
DECL|field|infosFormat
specifier|private
specifier|final
name|SegmentInfosFormat
name|infosFormat
init|=
operator|new
name|Lucene3xSegmentInfosFormat
argument_list|()
decl_stmt|;
DECL|field|normsFormat
specifier|private
specifier|final
name|Lucene3xNormsFormat
name|normsFormat
init|=
operator|new
name|Lucene3xNormsFormat
argument_list|()
decl_stmt|;
comment|// TODO: this should really be a different impl
DECL|field|liveDocsFormat
specifier|private
specifier|final
name|LiveDocsFormat
name|liveDocsFormat
init|=
operator|new
name|Lucene40LiveDocsFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|writeLiveDocs
parameter_list|(
name|MutableBits
name|bits
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this codec can only be used for reading"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
comment|// 3.x doesn't support docvalues
DECL|field|docValuesFormat
specifier|private
specifier|final
name|DocValuesFormat
name|docValuesFormat
init|=
operator|new
name|DocValuesFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PerDocConsumer
name|docsConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|PerDocProducer
name|docsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{}
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|postingsFormat
specifier|public
name|PostingsFormat
name|postingsFormat
parameter_list|()
block|{
return|return
name|postingsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|docValuesFormat
specifier|public
name|DocValuesFormat
name|docValuesFormat
parameter_list|()
block|{
return|return
name|docValuesFormat
return|;
block|}
annotation|@
name|Override
DECL|method|storedFieldsFormat
specifier|public
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
block|{
return|return
name|fieldsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|termVectorsFormat
specifier|public
name|TermVectorsFormat
name|termVectorsFormat
parameter_list|()
block|{
return|return
name|vectorsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|fieldInfosFormat
specifier|public
name|FieldInfosFormat
name|fieldInfosFormat
parameter_list|()
block|{
return|return
name|fieldInfosFormat
return|;
block|}
annotation|@
name|Override
DECL|method|segmentInfosFormat
specifier|public
name|SegmentInfosFormat
name|segmentInfosFormat
parameter_list|()
block|{
return|return
name|infosFormat
return|;
block|}
annotation|@
name|Override
DECL|method|normsFormat
specifier|public
name|Lucene3xNormsFormat
name|normsFormat
parameter_list|()
block|{
return|return
name|normsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|liveDocsFormat
specifier|public
name|LiveDocsFormat
name|liveDocsFormat
parameter_list|()
block|{
return|return
name|liveDocsFormat
return|;
block|}
comment|// overrides the default implementation in codec.java to handle CFS without CFE
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|info
operator|.
name|getUseCompoundFile
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
comment|// NOTE: we don't add the CFE extension: because 3.x format doesn't use it.
block|}
else|else
block|{
name|super
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
comment|// override the default implementation in codec.java to handle separate norms files, and shared compound docstores
annotation|@
name|Override
DECL|method|separateFiles
specifier|public
name|void
name|separateFiles
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|separateFiles
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|normsFormat
argument_list|()
operator|.
name|separateFiles
argument_list|(
name|dir
argument_list|,
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|getDocStoreOffset
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// We are sharing doc stores (stored fields, term
comment|// vectors) with other segments
assert|assert
name|info
operator|.
name|getDocStoreSegment
argument_list|()
operator|!=
literal|null
assert|;
if|if
condition|(
name|info
operator|.
name|getDocStoreIsCompoundFile
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|IndexFileNames
operator|.
name|COMPOUND_FILE_STORE_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// otherwise, if its not a compound docstore, storedfieldsformat/termvectorsformat are each adding their relevant files
block|}
block|}
block|}
end_class
end_unit
