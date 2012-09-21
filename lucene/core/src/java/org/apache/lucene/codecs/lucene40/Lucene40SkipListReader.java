begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|codecs
operator|.
name|MultiLevelSkipListReader
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
name|IndexInput
import|;
end_import
begin_comment
comment|/**  * Implements the skip list reader for the 4.0 posting list format  * that stores positions and payloads.  *   * @see Lucene40PostingsFormat  * @lucene.experimental  */
end_comment
begin_class
DECL|class|Lucene40SkipListReader
specifier|public
class|class
name|Lucene40SkipListReader
extends|extends
name|MultiLevelSkipListReader
block|{
DECL|field|currentFieldStoresPayloads
specifier|private
name|boolean
name|currentFieldStoresPayloads
decl_stmt|;
DECL|field|currentFieldStoresOffsets
specifier|private
name|boolean
name|currentFieldStoresOffsets
decl_stmt|;
DECL|field|freqPointer
specifier|private
name|long
name|freqPointer
index|[]
decl_stmt|;
DECL|field|proxPointer
specifier|private
name|long
name|proxPointer
index|[]
decl_stmt|;
DECL|field|payloadLength
specifier|private
name|int
name|payloadLength
index|[]
decl_stmt|;
DECL|field|offsetLength
specifier|private
name|int
name|offsetLength
index|[]
decl_stmt|;
DECL|field|lastFreqPointer
specifier|private
name|long
name|lastFreqPointer
decl_stmt|;
DECL|field|lastProxPointer
specifier|private
name|long
name|lastProxPointer
decl_stmt|;
DECL|field|lastPayloadLength
specifier|private
name|int
name|lastPayloadLength
decl_stmt|;
DECL|field|lastOffsetLength
specifier|private
name|int
name|lastOffsetLength
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Lucene40SkipListReader
specifier|public
name|Lucene40SkipListReader
parameter_list|(
name|IndexInput
name|skipStream
parameter_list|,
name|int
name|maxSkipLevels
parameter_list|,
name|int
name|skipInterval
parameter_list|)
block|{
name|super
argument_list|(
name|skipStream
argument_list|,
name|maxSkipLevels
argument_list|,
name|skipInterval
argument_list|)
expr_stmt|;
name|freqPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|proxPointer
operator|=
operator|new
name|long
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|payloadLength
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
name|offsetLength
operator|=
operator|new
name|int
index|[
name|maxSkipLevels
index|]
expr_stmt|;
block|}
comment|/** Per-term initialization. */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|long
name|skipPointer
parameter_list|,
name|long
name|freqBasePointer
parameter_list|,
name|long
name|proxBasePointer
parameter_list|,
name|int
name|df
parameter_list|,
name|boolean
name|storesPayloads
parameter_list|,
name|boolean
name|storesOffsets
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|skipPointer
argument_list|,
name|df
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentFieldStoresPayloads
operator|=
name|storesPayloads
expr_stmt|;
name|this
operator|.
name|currentFieldStoresOffsets
operator|=
name|storesOffsets
expr_stmt|;
name|lastFreqPointer
operator|=
name|freqBasePointer
expr_stmt|;
name|lastProxPointer
operator|=
name|proxBasePointer
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|freqPointer
argument_list|,
name|freqBasePointer
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|proxPointer
argument_list|,
name|proxBasePointer
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|payloadLength
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|offsetLength
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the freq pointer of the doc to which the last call of     * {@link MultiLevelSkipListReader#skipTo(int)} has skipped.  */
DECL|method|getFreqPointer
specifier|public
name|long
name|getFreqPointer
parameter_list|()
block|{
return|return
name|lastFreqPointer
return|;
block|}
comment|/** Returns the prox pointer of the doc to which the last call of     * {@link MultiLevelSkipListReader#skipTo(int)} has skipped.  */
DECL|method|getProxPointer
specifier|public
name|long
name|getProxPointer
parameter_list|()
block|{
return|return
name|lastProxPointer
return|;
block|}
comment|/** Returns the payload length of the payload stored just before     * the doc to which the last call of {@link MultiLevelSkipListReader#skipTo(int)}     * has skipped.  */
DECL|method|getPayloadLength
specifier|public
name|int
name|getPayloadLength
parameter_list|()
block|{
return|return
name|lastPayloadLength
return|;
block|}
comment|/** Returns the offset length (endOffset-startOffset) of the position stored just before     * the doc to which the last call of {@link MultiLevelSkipListReader#skipTo(int)}     * has skipped.  */
DECL|method|getOffsetLength
specifier|public
name|int
name|getOffsetLength
parameter_list|()
block|{
return|return
name|lastOffsetLength
return|;
block|}
annotation|@
name|Override
DECL|method|seekChild
specifier|protected
name|void
name|seekChild
parameter_list|(
name|int
name|level
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|seekChild
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|freqPointer
index|[
name|level
index|]
operator|=
name|lastFreqPointer
expr_stmt|;
name|proxPointer
index|[
name|level
index|]
operator|=
name|lastProxPointer
expr_stmt|;
name|payloadLength
index|[
name|level
index|]
operator|=
name|lastPayloadLength
expr_stmt|;
name|offsetLength
index|[
name|level
index|]
operator|=
name|lastOffsetLength
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLastSkipData
specifier|protected
name|void
name|setLastSkipData
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|super
operator|.
name|setLastSkipData
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|lastFreqPointer
operator|=
name|freqPointer
index|[
name|level
index|]
expr_stmt|;
name|lastProxPointer
operator|=
name|proxPointer
index|[
name|level
index|]
expr_stmt|;
name|lastPayloadLength
operator|=
name|payloadLength
index|[
name|level
index|]
expr_stmt|;
name|lastOffsetLength
operator|=
name|offsetLength
index|[
name|level
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readSkipData
specifier|protected
name|int
name|readSkipData
parameter_list|(
name|int
name|level
parameter_list|,
name|IndexInput
name|skipStream
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|delta
decl_stmt|;
if|if
condition|(
name|currentFieldStoresPayloads
operator|||
name|currentFieldStoresOffsets
condition|)
block|{
comment|// the current field stores payloads and/or offsets.
comment|// if the doc delta is odd then we have
comment|// to read the current payload/offset lengths
comment|// because it differs from the lengths of the
comment|// previous payload/offset
name|delta
operator|=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|delta
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|currentFieldStoresPayloads
condition|)
block|{
name|payloadLength
index|[
name|level
index|]
operator|=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|currentFieldStoresOffsets
condition|)
block|{
name|offsetLength
index|[
name|level
index|]
operator|=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
name|delta
operator|>>>=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|delta
operator|=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
name|freqPointer
index|[
name|level
index|]
operator|+=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|proxPointer
index|[
name|level
index|]
operator|+=
name|skipStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
return|return
name|delta
return|;
block|}
block|}
end_class
end_unit
