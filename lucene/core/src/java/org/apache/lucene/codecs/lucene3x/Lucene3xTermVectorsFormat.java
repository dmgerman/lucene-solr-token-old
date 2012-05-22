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
name|index
operator|.
name|FieldInfos
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
name|store
operator|.
name|CompoundFileDirectory
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
begin_comment
comment|/**  * Lucene3x ReadOnly TermVectorsFormat implementation  * @deprecated (4.0) This is only used to read indexes created  * before 4.0.  * @lucene.experimental  */
end_comment
begin_class
annotation|@
name|Deprecated
class|class
DECL|class|Lucene3xTermVectorsFormat
name|Lucene3xTermVectorsFormat
extends|extends
name|TermVectorsFormat
block|{
annotation|@
name|Override
DECL|method|vectorsReader
specifier|public
name|TermVectorsReader
name|vectorsReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|Lucene3xTermVectorsReader
operator|.
name|VECTORS_FIELDS_EXTENSION
argument_list|)
decl_stmt|;
comment|// Unfortunately, for 3.x indices, each segment's
comment|// FieldInfos can lie about hasVectors (claim it's true
comment|// when really it's false).... so we have to carefully
comment|// check if the files really exist before trying to open
comment|// them (4.x has fixed this):
specifier|final
name|boolean
name|exists
decl_stmt|;
if|if
condition|(
name|segmentInfo
operator|.
name|getDocStoreOffset
argument_list|()
operator|!=
operator|-
literal|1
operator|&&
name|segmentInfo
operator|.
name|getDocStoreIsCompoundFile
argument_list|()
condition|)
block|{
name|String
name|cfxFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|,
literal|""
argument_list|,
name|Lucene3xCodec
operator|.
name|COMPOUND_FILE_STORE_EXTENSION
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentInfo
operator|.
name|dir
operator|.
name|fileExists
argument_list|(
name|cfxFileName
argument_list|)
condition|)
block|{
name|Directory
name|cfsDir
init|=
operator|new
name|CompoundFileDirectory
argument_list|(
name|segmentInfo
operator|.
name|dir
argument_list|,
name|cfxFileName
argument_list|,
name|context
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|exists
operator|=
name|cfsDir
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cfsDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|exists
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
name|exists
operator|=
name|directory
operator|.
name|fileExists
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|exists
condition|)
block|{
comment|// 3x's FieldInfos sometimes lies and claims a segment
comment|// has vectors when it doesn't:
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|Lucene3xTermVectorsReader
argument_list|(
name|directory
argument_list|,
name|segmentInfo
argument_list|,
name|fieldInfos
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|vectorsWriter
specifier|public
name|TermVectorsWriter
name|vectorsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
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
end_class
end_unit
