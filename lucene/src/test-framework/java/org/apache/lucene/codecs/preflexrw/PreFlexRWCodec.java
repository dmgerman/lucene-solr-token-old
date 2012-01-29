begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.preflexrw
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|preflexrw
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
name|lucene3x
operator|.
name|Lucene3xCodec
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
name|lucene3x
operator|.
name|Lucene3xNormsFormat
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
name|util
operator|.
name|LuceneTestCase
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
name|StringHelper
import|;
end_import
begin_comment
comment|/**  * Writes 3.x-like indexes (not perfect emulation yet) for testing only!  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PreFlexRWCodec
specifier|public
class|class
name|PreFlexRWCodec
extends|extends
name|Lucene3xCodec
block|{
DECL|field|postings
specifier|private
specifier|final
name|PostingsFormat
name|postings
init|=
operator|new
name|PreFlexRWPostingsFormat
argument_list|()
decl_stmt|;
DECL|field|norms
specifier|private
specifier|final
name|Lucene3xNormsFormat
name|norms
init|=
operator|new
name|PreFlexRWNormsFormat
argument_list|()
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfosFormat
name|fieldInfos
init|=
operator|new
name|PreFlexRWFieldInfosFormat
argument_list|()
decl_stmt|;
DECL|field|termVectors
specifier|private
specifier|final
name|TermVectorsFormat
name|termVectors
init|=
operator|new
name|PreFlexRWTermVectorsFormat
argument_list|()
decl_stmt|;
DECL|field|segmentInfos
specifier|private
specifier|final
name|SegmentInfosFormat
name|segmentInfos
init|=
operator|new
name|PreFlexRWSegmentInfosFormat
argument_list|()
decl_stmt|;
comment|// TODO: this should really be a different impl
DECL|field|liveDocs
specifier|private
specifier|final
name|LiveDocsFormat
name|liveDocs
init|=
operator|new
name|Lucene40LiveDocsFormat
argument_list|()
decl_stmt|;
comment|// TODO: this should really be a different impl
DECL|field|storedFields
specifier|private
specifier|final
name|StoredFieldsFormat
name|storedFields
init|=
operator|new
name|Lucene40StoredFieldsFormat
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|postingsFormat
specifier|public
name|PostingsFormat
name|postingsFormat
parameter_list|()
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|PREFLEX_IMPERSONATION_IS_ACTIVE
condition|)
block|{
return|return
name|postings
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|postingsFormat
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|normsFormat
specifier|public
name|Lucene3xNormsFormat
name|normsFormat
parameter_list|()
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|PREFLEX_IMPERSONATION_IS_ACTIVE
condition|)
block|{
return|return
name|norms
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|normsFormat
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|segmentInfosFormat
specifier|public
name|SegmentInfosFormat
name|segmentInfosFormat
parameter_list|()
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|PREFLEX_IMPERSONATION_IS_ACTIVE
condition|)
block|{
return|return
name|segmentInfos
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|segmentInfosFormat
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|fieldInfosFormat
specifier|public
name|FieldInfosFormat
name|fieldInfosFormat
parameter_list|()
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|PREFLEX_IMPERSONATION_IS_ACTIVE
condition|)
block|{
return|return
name|fieldInfos
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|fieldInfosFormat
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|termVectorsFormat
specifier|public
name|TermVectorsFormat
name|termVectorsFormat
parameter_list|()
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|PREFLEX_IMPERSONATION_IS_ACTIVE
condition|)
block|{
return|return
name|termVectors
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|termVectorsFormat
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|liveDocsFormat
specifier|public
name|LiveDocsFormat
name|liveDocsFormat
parameter_list|()
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|PREFLEX_IMPERSONATION_IS_ACTIVE
condition|)
block|{
return|return
name|liveDocs
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|liveDocsFormat
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|storedFieldsFormat
specifier|public
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|PREFLEX_IMPERSONATION_IS_ACTIVE
condition|)
block|{
return|return
name|storedFields
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|storedFieldsFormat
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
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
operator|&&
name|LuceneTestCase
operator|.
name|PREFLEX_IMPERSONATION_IS_ACTIVE
condition|)
block|{
comment|// because we don't fully emulate 3.x codec, PreFlexRW actually writes 4.x format CFS files.
comment|// so we must check segment version here to see if its a "real" 3.x segment or a "fake"
comment|// one that we wrote with a 4.x-format CFS+CFE
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
name|String
name|version
init|=
name|info
operator|.
name|getVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
operator|&&
name|StringHelper
operator|.
name|getVersionComparator
argument_list|()
operator|.
name|compare
argument_list|(
literal|"4.0"
argument_list|,
name|version
argument_list|)
operator|<=
literal|0
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
name|COMPOUND_FILE_ENTRIES_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|files
argument_list|(
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
