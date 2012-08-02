begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.block
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|block
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
name|Arrays
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
name|IndexOutput
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
name|MultiLevelSkipListWriter
import|;
end_import
begin_comment
comment|// nocommit do we need more frequent skips at level> 0?
end_comment
begin_comment
comment|// 128*128 is immense?  may need to decouple
end_comment
begin_comment
comment|// baseSkipInterval& theRestSkipInterval?
end_comment
begin_class
DECL|class|BlockSkipWriter
specifier|final
class|class
name|BlockSkipWriter
extends|extends
name|MultiLevelSkipListWriter
block|{
DECL|field|DEBUG
specifier|private
name|boolean
name|DEBUG
init|=
name|BlockPostingsReader
operator|.
name|DEBUG
decl_stmt|;
DECL|field|lastSkipDoc
specifier|private
name|int
index|[]
name|lastSkipDoc
decl_stmt|;
DECL|field|lastSkipDocPointer
specifier|private
name|long
index|[]
name|lastSkipDocPointer
decl_stmt|;
DECL|field|lastSkipPosPointer
specifier|private
name|long
index|[]
name|lastSkipPosPointer
decl_stmt|;
DECL|field|lastSkipPayPointer
specifier|private
name|long
index|[]
name|lastSkipPayPointer
decl_stmt|;
DECL|field|lastStartOffset
specifier|private
name|int
index|[]
name|lastStartOffset
decl_stmt|;
DECL|field|lastPayloadByteUpto
specifier|private
name|int
index|[]
name|lastPayloadByteUpto
decl_stmt|;
DECL|field|docOut
specifier|private
specifier|final
name|IndexOutput
name|docOut
decl_stmt|;
DECL|field|posOut
specifier|private
specifier|final
name|IndexOutput
name|posOut
decl_stmt|;
DECL|field|payOut
specifier|private
specifier|final
name|IndexOutput
name|payOut
decl_stmt|;
DECL|field|curDoc
specifier|private
name|int
name|curDoc
decl_stmt|;
DECL|field|curDocPointer
specifier|private
name|long
name|curDocPointer
decl_stmt|;
DECL|field|curPosPointer
specifier|private
name|long
name|curPosPointer
decl_stmt|;
DECL|field|curPayPointer
specifier|private
name|long
name|curPayPointer
decl_stmt|;
DECL|field|curPosBufferUpto
specifier|private
name|int
name|curPosBufferUpto
decl_stmt|;
DECL|field|curStartOffset
specifier|private
name|int
name|curStartOffset
decl_stmt|;
DECL|field|curPayloadByteUpto
specifier|private
name|int
name|curPayloadByteUpto
decl_stmt|;
DECL|field|fieldHasPositions
specifier|private
name|boolean
name|fieldHasPositions
decl_stmt|;
DECL|field|fieldHasOffsets
specifier|private
name|boolean
name|fieldHasOffsets
decl_stmt|;
DECL|field|fieldHasPayloads
specifier|private
name|boolean
name|fieldHasPayloads
decl_stmt|;
DECL|method|BlockSkipWriter
specifier|public
name|BlockSkipWriter
parameter_list|(
name|int
name|skipInterval
parameter_list|,
name|int
name|maxSkipLevels
parameter_list|,
name|int
name|docCount
parameter_list|,
name|IndexOutput
name|docOut
parameter_list|,
name|IndexOutput
name|posOut
parameter_list|,
name|IndexOutput
name|payOut
parameter_list|)
block|{
name|super
argument_list|(
name|skipInterval
argument_list|,
name|maxSkipLevels
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|docOut
operator|=
name|docOut
expr_stmt|;
name|this
operator|.
name|posOut
operator|=
name|posOut
expr_stmt|;
name|this
operator|.
name|payOut
operator|=
name|payOut
expr_stmt|;
name|lastSkipDoc
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|lastSkipDocPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
if|if
condition|(
name|posOut
operator|!=
literal|null
condition|)
block|{
name|lastSkipPosPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
if|if
condition|(
name|payOut
operator|!=
literal|null
condition|)
block|{
name|lastSkipPayPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
block|}
name|lastStartOffset
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|lastPayloadByteUpto
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
block|}
block|}
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|boolean
name|fieldHasPositions
parameter_list|,
name|boolean
name|fieldHasOffsets
parameter_list|,
name|boolean
name|fieldHasPayloads
parameter_list|)
block|{
name|this
operator|.
name|fieldHasPositions
operator|=
name|fieldHasPositions
expr_stmt|;
name|this
operator|.
name|fieldHasOffsets
operator|=
name|fieldHasOffsets
expr_stmt|;
name|this
operator|.
name|fieldHasPayloads
operator|=
name|fieldHasPayloads
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|resetSkip
specifier|public
name|void
name|resetSkip
parameter_list|()
block|{
name|super
operator|.
name|resetSkip
argument_list|()
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipDoc
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipDocPointer
argument_list|,
name|docOut
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldHasPositions
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipPosPointer
argument_list|,
name|posOut
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldHasOffsets
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|lastStartOffset
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldHasPayloads
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|lastPayloadByteUpto
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldHasOffsets
operator|||
name|fieldHasPayloads
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|lastSkipPayPointer
argument_list|,
name|payOut
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Sets the values for the current skip data.     */
DECL|method|bufferSkip
specifier|public
name|void
name|bufferSkip
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|long
name|posFP
parameter_list|,
name|long
name|payFP
parameter_list|,
name|int
name|posBufferUpto
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|payloadByteUpto
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|curDoc
operator|=
name|doc
expr_stmt|;
name|this
operator|.
name|curDocPointer
operator|=
name|docOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|this
operator|.
name|curPosPointer
operator|=
name|posFP
expr_stmt|;
name|this
operator|.
name|curPayPointer
operator|=
name|payFP
expr_stmt|;
name|this
operator|.
name|curPosBufferUpto
operator|=
name|posBufferUpto
expr_stmt|;
name|this
operator|.
name|curPayloadByteUpto
operator|=
name|payloadByteUpto
expr_stmt|;
name|this
operator|.
name|curStartOffset
operator|=
name|startOffset
expr_stmt|;
name|bufferSkip
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeSkipData
specifier|protected
name|void
name|writeSkipData
parameter_list|(
name|int
name|level
parameter_list|,
name|IndexOutput
name|skipBuffer
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|delta
init|=
name|curDoc
operator|-
name|lastSkipDoc
index|[
name|level
index|]
decl_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"writeSkipData level="
operator|+
name|level
operator|+
literal|" lastDoc="
operator|+
name|curDoc
operator|+
literal|" delta="
operator|+
name|delta
operator|+
literal|" curDocPointer="
operator|+
name|curDocPointer
argument_list|)
expr_stmt|;
block|}
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|lastSkipDoc
index|[
name|level
index|]
operator|=
name|curDoc
expr_stmt|;
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|curDocPointer
operator|-
name|lastSkipDocPointer
index|[
name|level
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|lastSkipDocPointer
index|[
name|level
index|]
operator|=
name|curDocPointer
expr_stmt|;
if|if
condition|(
name|fieldHasPositions
condition|)
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  curPosPointer="
operator|+
name|curPosPointer
operator|+
literal|" curPosBufferUpto="
operator|+
name|curPosBufferUpto
argument_list|)
expr_stmt|;
block|}
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|curPosPointer
operator|-
name|lastSkipPosPointer
index|[
name|level
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|lastSkipPosPointer
index|[
name|level
index|]
operator|=
name|curPosPointer
expr_stmt|;
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|curPosBufferUpto
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldHasPayloads
condition|)
block|{
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|curPayloadByteUpto
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldHasOffsets
condition|)
block|{
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
name|curStartOffset
operator|-
name|lastStartOffset
index|[
name|level
index|]
argument_list|)
expr_stmt|;
name|lastStartOffset
index|[
name|level
index|]
operator|=
name|curStartOffset
expr_stmt|;
block|}
if|if
condition|(
name|fieldHasOffsets
operator|||
name|fieldHasPayloads
condition|)
block|{
name|skipBuffer
operator|.
name|writeVInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|curPayPointer
operator|-
name|lastSkipPayPointer
index|[
name|level
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|lastSkipPayPointer
index|[
name|level
index|]
operator|=
name|curPayPointer
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
