begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.temp
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|temp
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41
operator|.
name|Lucene41PostingsFormat
operator|.
name|BLOCK_SIZE
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41
operator|.
name|ForUtil
operator|.
name|MAX_DATA_SIZE
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41
operator|.
name|ForUtil
operator|.
name|MAX_ENCODED_SIZE
import|;
end_import
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
name|ArrayList
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
name|CodecUtil
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
name|TempPostingsWriterBase
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
name|TermStats
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
name|lucene41
operator|.
name|Lucene41SkipWriter
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
name|lucene41
operator|.
name|ForUtil
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
name|CorruptIndexException
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
name|FieldInfo
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|SegmentWriteState
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
name|DataOutput
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
name|store
operator|.
name|RAMOutputStream
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
name|ArrayUtil
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
name|packed
operator|.
name|PackedInts
import|;
end_import
begin_comment
comment|/**  * Concrete class that writes docId(maybe frq,pos,offset,payloads) list  * with postings format.  *  * Postings list for each term will be stored separately.   *  * @see Lucene41SkipWriter for details about skipping setting and postings layout.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|TempPostingsWriter
specifier|public
specifier|final
class|class
name|TempPostingsWriter
extends|extends
name|TempPostingsWriterBase
block|{
comment|/**     * Expert: The maximum number of skip levels. Smaller values result in     * slightly smaller indexes, but slower skipping in big posting lists.    */
DECL|field|maxSkipLevels
specifier|static
specifier|final
name|int
name|maxSkipLevels
init|=
literal|10
decl_stmt|;
DECL|field|TERMS_CODEC
specifier|final
specifier|static
name|String
name|TERMS_CODEC
init|=
literal|"TempPostingsWriterTerms"
decl_stmt|;
DECL|field|DOC_CODEC
specifier|final
specifier|static
name|String
name|DOC_CODEC
init|=
literal|"TempPostingsWriterDoc"
decl_stmt|;
DECL|field|POS_CODEC
specifier|final
specifier|static
name|String
name|POS_CODEC
init|=
literal|"TempPostingsWriterPos"
decl_stmt|;
DECL|field|PAY_CODEC
specifier|final
specifier|static
name|String
name|PAY_CODEC
init|=
literal|"TempPostingsWriterPay"
decl_stmt|;
comment|// Increment version to change it
DECL|field|VERSION_START
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|final
specifier|static
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|docOut
specifier|final
name|IndexOutput
name|docOut
decl_stmt|;
DECL|field|posOut
specifier|final
name|IndexOutput
name|posOut
decl_stmt|;
DECL|field|payOut
specifier|final
name|IndexOutput
name|payOut
decl_stmt|;
comment|// How current field indexes postings:
DECL|field|fieldHasFreqs
specifier|private
name|boolean
name|fieldHasFreqs
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
comment|// Holds starting file pointers for each term:
DECL|field|docTermStartFP
specifier|private
name|long
name|docTermStartFP
decl_stmt|;
DECL|field|posTermStartFP
specifier|private
name|long
name|posTermStartFP
decl_stmt|;
DECL|field|payTermStartFP
specifier|private
name|long
name|payTermStartFP
decl_stmt|;
DECL|field|docDeltaBuffer
specifier|final
name|int
index|[]
name|docDeltaBuffer
decl_stmt|;
DECL|field|freqBuffer
specifier|final
name|int
index|[]
name|freqBuffer
decl_stmt|;
DECL|field|docBufferUpto
specifier|private
name|int
name|docBufferUpto
decl_stmt|;
DECL|field|posDeltaBuffer
specifier|final
name|int
index|[]
name|posDeltaBuffer
decl_stmt|;
DECL|field|payloadLengthBuffer
specifier|final
name|int
index|[]
name|payloadLengthBuffer
decl_stmt|;
DECL|field|offsetStartDeltaBuffer
specifier|final
name|int
index|[]
name|offsetStartDeltaBuffer
decl_stmt|;
DECL|field|offsetLengthBuffer
specifier|final
name|int
index|[]
name|offsetLengthBuffer
decl_stmt|;
DECL|field|posBufferUpto
specifier|private
name|int
name|posBufferUpto
decl_stmt|;
DECL|field|payloadBytes
specifier|private
name|byte
index|[]
name|payloadBytes
decl_stmt|;
DECL|field|payloadByteUpto
specifier|private
name|int
name|payloadByteUpto
decl_stmt|;
DECL|field|lastBlockDocID
specifier|private
name|int
name|lastBlockDocID
decl_stmt|;
DECL|field|lastBlockPosFP
specifier|private
name|long
name|lastBlockPosFP
decl_stmt|;
DECL|field|lastBlockPayFP
specifier|private
name|long
name|lastBlockPayFP
decl_stmt|;
DECL|field|lastBlockPosBufferUpto
specifier|private
name|int
name|lastBlockPosBufferUpto
decl_stmt|;
DECL|field|lastBlockPayloadByteUpto
specifier|private
name|int
name|lastBlockPayloadByteUpto
decl_stmt|;
DECL|field|lastDocID
specifier|private
name|int
name|lastDocID
decl_stmt|;
DECL|field|lastPosition
specifier|private
name|int
name|lastPosition
decl_stmt|;
DECL|field|lastStartOffset
specifier|private
name|int
name|lastStartOffset
decl_stmt|;
DECL|field|docCount
specifier|private
name|int
name|docCount
decl_stmt|;
DECL|field|encoded
specifier|final
name|byte
index|[]
name|encoded
decl_stmt|;
DECL|field|forUtil
specifier|private
specifier|final
name|ForUtil
name|forUtil
decl_stmt|;
DECL|field|skipWriter
specifier|private
specifier|final
name|Lucene41SkipWriter
name|skipWriter
decl_stmt|;
comment|/** Creates a postings writer with the specified PackedInts overhead ratio */
comment|// TODO: does this ctor even make sense?
DECL|method|TempPostingsWriter
specifier|public
name|TempPostingsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|docOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|TempPostingsFormat
operator|.
name|DOC_EXTENSION
argument_list|)
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|IndexOutput
name|posOut
init|=
literal|null
decl_stmt|;
name|IndexOutput
name|payOut
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|docOut
argument_list|,
name|DOC_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|forUtil
operator|=
operator|new
name|ForUtil
argument_list|(
name|acceptableOverheadRatio
argument_list|,
name|docOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|fieldInfos
operator|.
name|hasProx
argument_list|()
condition|)
block|{
name|posDeltaBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
name|posOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|TempPostingsFormat
operator|.
name|POS_EXTENSION
argument_list|)
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|posOut
argument_list|,
name|POS_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|fieldInfos
operator|.
name|hasPayloads
argument_list|()
condition|)
block|{
name|payloadBytes
operator|=
operator|new
name|byte
index|[
literal|128
index|]
expr_stmt|;
name|payloadLengthBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
block|}
else|else
block|{
name|payloadBytes
operator|=
literal|null
expr_stmt|;
name|payloadLengthBuffer
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|.
name|fieldInfos
operator|.
name|hasOffsets
argument_list|()
condition|)
block|{
name|offsetStartDeltaBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
name|offsetLengthBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
block|}
else|else
block|{
name|offsetStartDeltaBuffer
operator|=
literal|null
expr_stmt|;
name|offsetLengthBuffer
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|.
name|fieldInfos
operator|.
name|hasPayloads
argument_list|()
operator|||
name|state
operator|.
name|fieldInfos
operator|.
name|hasOffsets
argument_list|()
condition|)
block|{
name|payOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|TempPostingsFormat
operator|.
name|PAY_EXTENSION
argument_list|)
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|payOut
argument_list|,
name|PAY_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|posDeltaBuffer
operator|=
literal|null
expr_stmt|;
name|payloadLengthBuffer
operator|=
literal|null
expr_stmt|;
name|offsetStartDeltaBuffer
operator|=
literal|null
expr_stmt|;
name|offsetLengthBuffer
operator|=
literal|null
expr_stmt|;
name|payloadBytes
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|payOut
operator|=
name|payOut
expr_stmt|;
name|this
operator|.
name|posOut
operator|=
name|posOut
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
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|docOut
argument_list|,
name|posOut
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
block|}
block|}
name|docDeltaBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
name|freqBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
comment|// TODO: should we try skipping every 2/4 blocks...?
name|skipWriter
operator|=
operator|new
name|Lucene41SkipWriter
argument_list|(
name|maxSkipLevels
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|docOut
argument_list|,
name|posOut
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
name|encoded
operator|=
operator|new
name|byte
index|[
name|MAX_ENCODED_SIZE
index|]
expr_stmt|;
block|}
comment|/** Creates a postings writer with<code>PackedInts.COMPACT</code> */
DECL|method|TempPostingsWriter
specifier|public
name|TempPostingsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|state
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
name|IndexOutput
name|termsOut
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|termsOut
argument_list|,
name|TERMS_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|termsOut
operator|.
name|writeVInt
argument_list|(
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|IndexOptions
name|indexOptions
init|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
decl_stmt|;
name|fieldHasFreqs
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|fieldHasPositions
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|fieldHasOffsets
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|fieldHasPayloads
operator|=
name|fieldInfo
operator|.
name|hasPayloads
argument_list|()
expr_stmt|;
name|skipWriter
operator|.
name|setField
argument_list|(
name|fieldHasPositions
argument_list|,
name|fieldHasOffsets
argument_list|,
name|fieldHasPayloads
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|()
block|{
name|docTermStartFP
operator|=
name|docOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldHasPositions
condition|)
block|{
name|posTermStartFP
operator|=
name|posOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|fieldHasPayloads
operator|||
name|fieldHasOffsets
condition|)
block|{
name|payTermStartFP
operator|=
name|payOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
block|}
name|lastDocID
operator|=
literal|0
expr_stmt|;
name|lastBlockDocID
operator|=
operator|-
literal|1
expr_stmt|;
comment|// if (DEBUG) {
comment|//   System.out.println("FPW.startTerm startFP=" + docTermStartFP);
comment|// }
name|skipWriter
operator|.
name|resetSkip
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDoc
specifier|public
name|void
name|startDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|termDocFreq
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("FPW.startDoc docID["+docBufferUpto+"]=" + docID);
comment|// }
comment|// Have collected a block of docs, and get a new doc.
comment|// Should write skip data as well as postings list for
comment|// current block.
if|if
condition|(
name|lastBlockDocID
operator|!=
operator|-
literal|1
operator|&&
name|docBufferUpto
operator|==
literal|0
condition|)
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("  bufferSkip at writeBlock: lastDocID=" + lastBlockDocID + " docCount=" + (docCount-1));
comment|// }
name|skipWriter
operator|.
name|bufferSkip
argument_list|(
name|lastBlockDocID
argument_list|,
name|docCount
argument_list|,
name|lastBlockPosFP
argument_list|,
name|lastBlockPayFP
argument_list|,
name|lastBlockPosBufferUpto
argument_list|,
name|lastBlockPayloadByteUpto
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|docDelta
init|=
name|docID
operator|-
name|lastDocID
decl_stmt|;
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
operator|(
name|docCount
operator|>
literal|0
operator|&&
name|docDelta
operator|<=
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"docs out of order ("
operator|+
name|docID
operator|+
literal|"<= "
operator|+
name|lastDocID
operator|+
literal|" ) (docOut: "
operator|+
name|docOut
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|docDeltaBuffer
index|[
name|docBufferUpto
index|]
operator|=
name|docDelta
expr_stmt|;
comment|// if (DEBUG) {
comment|//   System.out.println("  docDeltaBuffer[" + docBufferUpto + "]=" + docDelta);
comment|// }
if|if
condition|(
name|fieldHasFreqs
condition|)
block|{
name|freqBuffer
index|[
name|docBufferUpto
index|]
operator|=
name|termDocFreq
expr_stmt|;
block|}
name|docBufferUpto
operator|++
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
if|if
condition|(
name|docBufferUpto
operator|==
name|BLOCK_SIZE
condition|)
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("  write docDelta block @ fp=" + docOut.getFilePointer());
comment|// }
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|docDeltaBuffer
argument_list|,
name|encoded
argument_list|,
name|docOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldHasFreqs
condition|)
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("  write freq block @ fp=" + docOut.getFilePointer());
comment|// }
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|freqBuffer
argument_list|,
name|encoded
argument_list|,
name|docOut
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: don't set docBufferUpto back to 0 here;
comment|// finishDoc will do so (because it needs to see that
comment|// the block was filled so it can save skip data)
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
name|lastPosition
operator|=
literal|0
expr_stmt|;
name|lastStartOffset
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Add a new position& payload */
annotation|@
name|Override
DECL|method|addPosition
specifier|public
name|void
name|addPosition
parameter_list|(
name|int
name|position
parameter_list|,
name|BytesRef
name|payload
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("FPW.addPosition pos=" + position + " posBufferUpto=" + posBufferUpto + (fieldHasPayloads ? " payloadByteUpto=" + payloadByteUpto: ""));
comment|// }
name|posDeltaBuffer
index|[
name|posBufferUpto
index|]
operator|=
name|position
operator|-
name|lastPosition
expr_stmt|;
if|if
condition|(
name|fieldHasPayloads
condition|)
block|{
if|if
condition|(
name|payload
operator|==
literal|null
operator|||
name|payload
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// no payload
name|payloadLengthBuffer
index|[
name|posBufferUpto
index|]
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|payloadLengthBuffer
index|[
name|posBufferUpto
index|]
operator|=
name|payload
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|payloadByteUpto
operator|+
name|payload
operator|.
name|length
operator|>
name|payloadBytes
operator|.
name|length
condition|)
block|{
name|payloadBytes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|payloadBytes
argument_list|,
name|payloadByteUpto
operator|+
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|payloadBytes
argument_list|,
name|payloadByteUpto
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|payloadByteUpto
operator|+=
name|payload
operator|.
name|length
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fieldHasOffsets
condition|)
block|{
assert|assert
name|startOffset
operator|>=
name|lastStartOffset
assert|;
assert|assert
name|endOffset
operator|>=
name|startOffset
assert|;
name|offsetStartDeltaBuffer
index|[
name|posBufferUpto
index|]
operator|=
name|startOffset
operator|-
name|lastStartOffset
expr_stmt|;
name|offsetLengthBuffer
index|[
name|posBufferUpto
index|]
operator|=
name|endOffset
operator|-
name|startOffset
expr_stmt|;
name|lastStartOffset
operator|=
name|startOffset
expr_stmt|;
block|}
name|posBufferUpto
operator|++
expr_stmt|;
name|lastPosition
operator|=
name|position
expr_stmt|;
if|if
condition|(
name|posBufferUpto
operator|==
name|BLOCK_SIZE
condition|)
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("  write pos bulk block @ fp=" + posOut.getFilePointer());
comment|// }
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|posDeltaBuffer
argument_list|,
name|encoded
argument_list|,
name|posOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldHasPayloads
condition|)
block|{
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|payloadLengthBuffer
argument_list|,
name|encoded
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
name|payOut
operator|.
name|writeVInt
argument_list|(
name|payloadByteUpto
argument_list|)
expr_stmt|;
name|payOut
operator|.
name|writeBytes
argument_list|(
name|payloadBytes
argument_list|,
literal|0
argument_list|,
name|payloadByteUpto
argument_list|)
expr_stmt|;
name|payloadByteUpto
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|fieldHasOffsets
condition|)
block|{
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|offsetStartDeltaBuffer
argument_list|,
name|encoded
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|offsetLengthBuffer
argument_list|,
name|encoded
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
block|}
name|posBufferUpto
operator|=
literal|0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finishDoc
specifier|public
name|void
name|finishDoc
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Since we don't know df for current term, we had to buffer
comment|// those skip data for each block, and when a new doc comes,
comment|// write them to skip file.
if|if
condition|(
name|docBufferUpto
operator|==
name|BLOCK_SIZE
condition|)
block|{
name|lastBlockDocID
operator|=
name|lastDocID
expr_stmt|;
if|if
condition|(
name|posOut
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|payOut
operator|!=
literal|null
condition|)
block|{
name|lastBlockPayFP
operator|=
name|payOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
name|lastBlockPosFP
operator|=
name|posOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|lastBlockPosBufferUpto
operator|=
name|posBufferUpto
expr_stmt|;
name|lastBlockPayloadByteUpto
operator|=
name|payloadByteUpto
expr_stmt|;
block|}
comment|// if (DEBUG) {
comment|//   System.out.println("  docBufferUpto="+docBufferUpto+" now get lastBlockDocID="+lastBlockDocID+" lastBlockPosFP=" + lastBlockPosFP + " lastBlockPosBufferUpto=" +  lastBlockPosBufferUpto + " lastBlockPayloadByteUpto=" + lastBlockPayloadByteUpto);
comment|// }
name|docBufferUpto
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|method|longsSize
specifier|public
name|int
name|longsSize
parameter_list|()
block|{
if|if
condition|(
name|fieldHasPositions
condition|)
block|{
return|return
literal|3
return|;
comment|// doc + pos + pay FP
block|}
else|else
block|{
return|return
literal|1
return|;
comment|// docFP
block|}
block|}
comment|/** Called when we are done adding docs to this term */
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|long
index|[]
name|longs
parameter_list|,
name|DataOutput
name|out
parameter_list|,
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|stats
operator|.
name|docFreq
operator|>
literal|0
assert|;
comment|// TODO: wasteful we are counting this (counting # docs
comment|// for this term) in two places?
assert|assert
name|stats
operator|.
name|docFreq
operator|==
name|docCount
operator|:
name|stats
operator|.
name|docFreq
operator|+
literal|" vs "
operator|+
name|docCount
assert|;
comment|// if (DEBUG) {
comment|//   System.out.println("FPW.finishTerm docFreq=" + stats.docFreq);
comment|// }
comment|// if (DEBUG) {
comment|//   if (docBufferUpto> 0) {
comment|//     System.out.println("  write doc/freq vInt block (count=" + docBufferUpto + ") at fp=" + docOut.getFilePointer() + " docTermStartFP=" + docTermStartFP);
comment|//   }
comment|// }
comment|// docFreq == 1, don't write the single docid/freq to a separate file along with a pointer to it.
specifier|final
name|int
name|singletonDocID
decl_stmt|;
if|if
condition|(
name|stats
operator|.
name|docFreq
operator|==
literal|1
condition|)
block|{
comment|// pulse the singleton docid into the term dictionary, freq is implicitly totalTermFreq
name|singletonDocID
operator|=
name|docDeltaBuffer
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|singletonDocID
operator|=
operator|-
literal|1
expr_stmt|;
comment|// vInt encode the remaining doc deltas and freqs:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docBufferUpto
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|docDelta
init|=
name|docDeltaBuffer
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|int
name|freq
init|=
name|freqBuffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|fieldHasFreqs
condition|)
block|{
name|docOut
operator|.
name|writeVInt
argument_list|(
name|docDelta
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|freqBuffer
index|[
name|i
index|]
operator|==
literal|1
condition|)
block|{
name|docOut
operator|.
name|writeVInt
argument_list|(
operator|(
name|docDelta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|docOut
operator|.
name|writeVInt
argument_list|(
name|docDelta
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|docOut
operator|.
name|writeVInt
argument_list|(
name|freq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|long
name|lastPosBlockOffset
decl_stmt|;
if|if
condition|(
name|fieldHasPositions
condition|)
block|{
comment|// if (DEBUG) {
comment|//   if (posBufferUpto> 0) {
comment|//     System.out.println("  write pos vInt block (count=" + posBufferUpto + ") at fp=" + posOut.getFilePointer() + " posTermStartFP=" + posTermStartFP + " hasPayloads=" + fieldHasPayloads + " hasOffsets=" + fieldHasOffsets);
comment|//   }
comment|// }
comment|// totalTermFreq is just total number of positions(or payloads, or offsets)
comment|// associated with current term.
assert|assert
name|stats
operator|.
name|totalTermFreq
operator|!=
operator|-
literal|1
assert|;
if|if
condition|(
name|stats
operator|.
name|totalTermFreq
operator|>
name|BLOCK_SIZE
condition|)
block|{
comment|// record file offset for last pos in last block
name|lastPosBlockOffset
operator|=
name|posOut
operator|.
name|getFilePointer
argument_list|()
operator|-
name|posTermStartFP
expr_stmt|;
block|}
else|else
block|{
name|lastPosBlockOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|posBufferUpto
operator|>
literal|0
condition|)
block|{
comment|// TODO: should we send offsets/payloads to
comment|// .pay...?  seems wasteful (have to store extra
comment|// vLong for low (< BLOCK_SIZE) DF terms = vast vast
comment|// majority)
comment|// vInt encode the remaining positions/payloads/offsets:
name|int
name|lastPayloadLength
init|=
operator|-
literal|1
decl_stmt|;
comment|// force first payload length to be written
name|int
name|lastOffsetLength
init|=
operator|-
literal|1
decl_stmt|;
comment|// force first offset length to be written
name|int
name|payloadBytesReadUpto
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
name|posBufferUpto
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|posDelta
init|=
name|posDeltaBuffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|fieldHasPayloads
condition|)
block|{
specifier|final
name|int
name|payloadLength
init|=
name|payloadLengthBuffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|payloadLength
operator|!=
name|lastPayloadLength
condition|)
block|{
name|lastPayloadLength
operator|=
name|payloadLength
expr_stmt|;
name|posOut
operator|.
name|writeVInt
argument_list|(
operator|(
name|posDelta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
name|posOut
operator|.
name|writeVInt
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posOut
operator|.
name|writeVInt
argument_list|(
name|posDelta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// if (DEBUG) {
comment|//   System.out.println("        i=" + i + " payloadLen=" + payloadLength);
comment|// }
if|if
condition|(
name|payloadLength
operator|!=
literal|0
condition|)
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("          write payload @ pos.fp=" + posOut.getFilePointer());
comment|// }
name|posOut
operator|.
name|writeBytes
argument_list|(
name|payloadBytes
argument_list|,
name|payloadBytesReadUpto
argument_list|,
name|payloadLength
argument_list|)
expr_stmt|;
name|payloadBytesReadUpto
operator|+=
name|payloadLength
expr_stmt|;
block|}
block|}
else|else
block|{
name|posOut
operator|.
name|writeVInt
argument_list|(
name|posDelta
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldHasOffsets
condition|)
block|{
comment|// if (DEBUG) {
comment|//   System.out.println("          write offset @ pos.fp=" + posOut.getFilePointer());
comment|// }
name|int
name|delta
init|=
name|offsetStartDeltaBuffer
index|[
name|i
index|]
decl_stmt|;
name|int
name|length
init|=
name|offsetLengthBuffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|length
operator|==
name|lastOffsetLength
condition|)
block|{
name|posOut
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posOut
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
name|posOut
operator|.
name|writeVInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|lastOffsetLength
operator|=
name|length
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|fieldHasPayloads
condition|)
block|{
assert|assert
name|payloadBytesReadUpto
operator|==
name|payloadByteUpto
assert|;
name|payloadByteUpto
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|// if (DEBUG) {
comment|//   System.out.println("  totalTermFreq=" + stats.totalTermFreq + " lastPosBlockOffset=" + lastPosBlockOffset);
comment|// }
block|}
else|else
block|{
name|lastPosBlockOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|long
name|skipOffset
decl_stmt|;
if|if
condition|(
name|docCount
operator|>
name|BLOCK_SIZE
condition|)
block|{
name|skipOffset
operator|=
name|skipWriter
operator|.
name|writeSkip
argument_list|(
name|docOut
argument_list|)
operator|-
name|docTermStartFP
expr_stmt|;
comment|// if (DEBUG) {
comment|//   System.out.println("skip packet " + (docOut.getFilePointer() - (docTermStartFP + skipOffset)) + " bytes");
comment|// }
block|}
else|else
block|{
name|skipOffset
operator|=
operator|-
literal|1
expr_stmt|;
comment|// if (DEBUG) {
comment|//   System.out.println("  no skip: docCount=" + docCount);
comment|// }
block|}
comment|// if (DEBUG) {
comment|//   System.out.println("  payStartFP=" + payStartFP);
comment|// }
comment|// write metadata
name|longs
index|[
literal|0
index|]
operator|=
name|docTermStartFP
expr_stmt|;
if|if
condition|(
name|fieldHasPositions
condition|)
block|{
name|longs
index|[
literal|1
index|]
operator|=
name|posTermStartFP
expr_stmt|;
name|longs
index|[
literal|2
index|]
operator|=
name|payTermStartFP
expr_stmt|;
block|}
if|if
condition|(
name|singletonDocID
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|singletonDocID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldHasPositions
condition|)
block|{
if|if
condition|(
name|lastPosBlockOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|lastPosBlockOffset
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|skipOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|skipOffset
argument_list|)
expr_stmt|;
block|}
name|docBufferUpto
operator|=
literal|0
expr_stmt|;
name|posBufferUpto
operator|=
literal|0
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
name|docCount
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|docOut
argument_list|,
name|posOut
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
