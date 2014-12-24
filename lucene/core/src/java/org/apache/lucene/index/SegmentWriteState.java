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
name|codecs
operator|.
name|perfield
operator|.
name|PerFieldPostingsFormat
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
name|MutableBits
import|;
end_import
begin_comment
comment|/**  * Holder class for common parameters used during write.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SegmentWriteState
specifier|public
class|class
name|SegmentWriteState
block|{
comment|/** {@link InfoStream} used for debugging messages. */
DECL|field|infoStream
specifier|public
specifier|final
name|InfoStream
name|infoStream
decl_stmt|;
comment|/** {@link Directory} where this segment will be written    *  to. */
DECL|field|directory
specifier|public
specifier|final
name|Directory
name|directory
decl_stmt|;
comment|/** {@link SegmentInfo} describing this segment. */
DECL|field|segmentInfo
specifier|public
specifier|final
name|SegmentInfo
name|segmentInfo
decl_stmt|;
comment|/** {@link FieldInfos} describing all fields in this    *  segment. */
DECL|field|fieldInfos
specifier|public
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
comment|/** Number of deleted documents set while flushing the    *  segment. */
DECL|field|delCountOnFlush
specifier|public
name|int
name|delCountOnFlush
decl_stmt|;
comment|/**    * Deletes and updates to apply while we are flushing the segment. A Term is    * enrolled in here if it was deleted/updated at one point, and it's mapped to    * the docIDUpto, meaning any docID&lt; docIDUpto containing this term should    * be deleted/updated.    */
DECL|field|segUpdates
specifier|public
specifier|final
name|BufferedUpdates
name|segUpdates
decl_stmt|;
comment|/** {@link MutableBits} recording live documents; this is    *  only set if there is one or more deleted documents. */
DECL|field|liveDocs
specifier|public
name|MutableBits
name|liveDocs
decl_stmt|;
comment|/** Unique suffix for any postings files written for this    *  segment.  {@link PerFieldPostingsFormat} sets this for    *  each of the postings formats it wraps.  If you create    *  a new {@link PostingsFormat} then any files you    *  write/read must be derived using this suffix (use    *  {@link IndexFileNames#segmentFileName(String,String,String)}).    *      *  Note: the suffix must be either empty, or be a textual suffix contain exactly two parts (separated by underscore), or be a base36 generation. */
DECL|field|segmentSuffix
specifier|public
specifier|final
name|String
name|segmentSuffix
decl_stmt|;
comment|/** {@link IOContext} for all writes; you should pass this    *  to {@link Directory#createOutput(String,IOContext)}. */
DECL|field|context
specifier|public
specifier|final
name|IOContext
name|context
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|SegmentWriteState
specifier|public
name|SegmentWriteState
parameter_list|(
name|InfoStream
name|infoStream
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|BufferedUpdates
name|segUpdates
parameter_list|,
name|IOContext
name|context
parameter_list|)
block|{
name|this
argument_list|(
name|infoStream
argument_list|,
name|directory
argument_list|,
name|segmentInfo
argument_list|,
name|fieldInfos
argument_list|,
name|segUpdates
argument_list|,
name|context
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor which takes segment suffix.    *     * @see #SegmentWriteState(InfoStream, Directory, SegmentInfo, FieldInfos,    *      BufferedUpdates, IOContext)    */
DECL|method|SegmentWriteState
specifier|public
name|SegmentWriteState
parameter_list|(
name|InfoStream
name|infoStream
parameter_list|,
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|BufferedUpdates
name|segUpdates
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
name|this
operator|.
name|segUpdates
operator|=
name|segUpdates
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segmentInfo
operator|=
name|segmentInfo
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|fieldInfos
expr_stmt|;
assert|assert
name|assertSegmentSuffix
argument_list|(
name|segmentSuffix
argument_list|)
assert|;
name|this
operator|.
name|segmentSuffix
operator|=
name|segmentSuffix
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|/** Create a shallow copy of {@link SegmentWriteState} with a new segment suffix. */
DECL|method|SegmentWriteState
specifier|public
name|SegmentWriteState
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
block|{
name|infoStream
operator|=
name|state
operator|.
name|infoStream
expr_stmt|;
name|directory
operator|=
name|state
operator|.
name|directory
expr_stmt|;
name|segmentInfo
operator|=
name|state
operator|.
name|segmentInfo
expr_stmt|;
name|fieldInfos
operator|=
name|state
operator|.
name|fieldInfos
expr_stmt|;
name|context
operator|=
name|state
operator|.
name|context
expr_stmt|;
name|this
operator|.
name|segmentSuffix
operator|=
name|segmentSuffix
expr_stmt|;
name|segUpdates
operator|=
name|state
operator|.
name|segUpdates
expr_stmt|;
name|delCountOnFlush
operator|=
name|state
operator|.
name|delCountOnFlush
expr_stmt|;
name|liveDocs
operator|=
name|state
operator|.
name|liveDocs
expr_stmt|;
block|}
comment|// currently only used by assert? clean up and make real check?
comment|// either it's a segment suffix (_X_Y) or it's a parseable generation
comment|// TODO: this is very confusing how ReadersAndUpdates passes generations via
comment|// this mechanism, maybe add 'generation' explicitly to ctor create the 'actual suffix' here?
DECL|method|assertSegmentSuffix
specifier|private
name|boolean
name|assertSegmentSuffix
parameter_list|(
name|String
name|segmentSuffix
parameter_list|)
block|{
assert|assert
name|segmentSuffix
operator|!=
literal|null
assert|;
if|if
condition|(
operator|!
name|segmentSuffix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|int
name|numParts
init|=
name|segmentSuffix
operator|.
name|split
argument_list|(
literal|"_"
argument_list|)
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|numParts
operator|==
literal|2
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|numParts
operator|==
literal|1
condition|)
block|{
name|Long
operator|.
name|parseLong
argument_list|(
name|segmentSuffix
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
comment|// invalid
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
