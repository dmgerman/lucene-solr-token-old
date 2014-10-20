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
begin_comment
comment|/** Consumes doc& freq, writing them using the current  *  index file format */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|BlockTermState
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
name|PushPostingsWriterBase
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
name|DocsEnum
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
begin_comment
comment|/**  * Writer for 4.0 postings format  * @deprecated for test purposes only  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|Lucene40PostingsWriter
specifier|final
class|class
name|Lucene40PostingsWriter
extends|extends
name|PushPostingsWriterBase
block|{
DECL|field|freqOut
specifier|final
name|IndexOutput
name|freqOut
decl_stmt|;
DECL|field|proxOut
specifier|final
name|IndexOutput
name|proxOut
decl_stmt|;
DECL|field|skipListWriter
specifier|final
name|Lucene40SkipListWriter
name|skipListWriter
decl_stmt|;
comment|/** Expert: The fraction of TermDocs entries stored in skip tables,    * used to accelerate {@link DocsEnum#advance(int)}.  Larger values result in    * smaller indexes, greater acceleration, but fewer accelerable cases, while    * smaller values result in bigger indexes, less acceleration and more    * accelerable cases. More detailed experiments would be useful here. */
DECL|field|DEFAULT_SKIP_INTERVAL
specifier|static
specifier|final
name|int
name|DEFAULT_SKIP_INTERVAL
init|=
literal|16
decl_stmt|;
DECL|field|skipInterval
specifier|final
name|int
name|skipInterval
decl_stmt|;
comment|/**    * Expert: minimum docFreq to write any skip data at all    */
DECL|field|skipMinimum
specifier|final
name|int
name|skipMinimum
decl_stmt|;
comment|/** Expert: The maximum number of skip levels. Smaller values result in     * slightly smaller indexes, but slower skipping in big posting lists.    */
DECL|field|maxSkipLevels
specifier|final
name|int
name|maxSkipLevels
init|=
literal|10
decl_stmt|;
DECL|field|totalNumDocs
specifier|final
name|int
name|totalNumDocs
decl_stmt|;
comment|// Starts a new term
DECL|field|freqStart
name|long
name|freqStart
decl_stmt|;
DECL|field|proxStart
name|long
name|proxStart
decl_stmt|;
DECL|field|lastPayloadLength
name|int
name|lastPayloadLength
decl_stmt|;
DECL|field|lastOffsetLength
name|int
name|lastOffsetLength
decl_stmt|;
DECL|field|lastPosition
name|int
name|lastPosition
decl_stmt|;
DECL|field|lastOffset
name|int
name|lastOffset
decl_stmt|;
DECL|field|emptyState
specifier|final
specifier|static
name|StandardTermState
name|emptyState
init|=
operator|new
name|StandardTermState
argument_list|()
decl_stmt|;
DECL|field|lastState
name|StandardTermState
name|lastState
decl_stmt|;
comment|// private String segment;
comment|/** Creates a {@link Lucene40PostingsWriter}, with the    *  {@link #DEFAULT_SKIP_INTERVAL}. */
DECL|method|Lucene40PostingsWriter
specifier|public
name|Lucene40PostingsWriter
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
name|DEFAULT_SKIP_INTERVAL
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a {@link Lucene40PostingsWriter}, with the    *  specified {@code skipInterval}. */
DECL|method|Lucene40PostingsWriter
specifier|public
name|Lucene40PostingsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|int
name|skipInterval
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|skipInterval
operator|=
name|skipInterval
expr_stmt|;
name|this
operator|.
name|skipMinimum
operator|=
name|skipInterval
expr_stmt|;
comment|/* set to the same for now */
comment|// this.segment = state.segmentName;
name|String
name|fileName
init|=
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
name|Lucene40PostingsFormat
operator|.
name|FREQ_EXTENSION
argument_list|)
decl_stmt|;
name|freqOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexOutput
name|proxOut
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|freqOut
argument_list|,
name|Lucene40PostingsReader
operator|.
name|FRQ_CODEC
argument_list|,
name|Lucene40PostingsReader
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
comment|// TODO: this is a best effort, if one of these fields has no postings
comment|// then we make an empty prx file, same as if we are wrapped in
comment|// per-field postingsformat. maybe... we shouldn't
comment|// bother w/ this opto?  just create empty prx file...?
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
comment|// At least one field does not omit TF, so create the
comment|// prox file
name|fileName
operator|=
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
name|Lucene40PostingsFormat
operator|.
name|PROX_EXTENSION
argument_list|)
expr_stmt|;
name|proxOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|fileName
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
name|proxOut
argument_list|,
name|Lucene40PostingsReader
operator|.
name|PRX_CODEC
argument_list|,
name|Lucene40PostingsReader
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Every field omits TF so we will write no prox file
name|proxOut
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|proxOut
operator|=
name|proxOut
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
name|freqOut
argument_list|,
name|proxOut
argument_list|)
expr_stmt|;
block|}
block|}
name|totalNumDocs
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|skipListWriter
operator|=
operator|new
name|Lucene40SkipListWriter
argument_list|(
name|skipInterval
argument_list|,
name|maxSkipLevels
argument_list|,
name|totalNumDocs
argument_list|,
name|freqOut
argument_list|,
name|proxOut
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|IndexOutput
name|termsOut
parameter_list|,
name|SegmentWriteState
name|state
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
name|Lucene40PostingsReader
operator|.
name|TERMS_CODEC
argument_list|,
name|Lucene40PostingsReader
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|termsOut
operator|.
name|writeInt
argument_list|(
name|skipInterval
argument_list|)
expr_stmt|;
comment|// write skipInterval
name|termsOut
operator|.
name|writeInt
argument_list|(
name|maxSkipLevels
argument_list|)
expr_stmt|;
comment|// write maxSkipLevels
name|termsOut
operator|.
name|writeInt
argument_list|(
name|skipMinimum
argument_list|)
expr_stmt|;
comment|// write skipMinimum
block|}
annotation|@
name|Override
DECL|method|newTermState
specifier|public
name|BlockTermState
name|newTermState
parameter_list|()
block|{
return|return
operator|new
name|StandardTermState
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|()
block|{
name|freqStart
operator|=
name|freqOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("SPW: startTerm freqOut.fp=" + freqStart);
if|if
condition|(
name|proxOut
operator|!=
literal|null
condition|)
block|{
name|proxStart
operator|=
name|proxOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
comment|// force first payload to write its length
name|lastPayloadLength
operator|=
operator|-
literal|1
expr_stmt|;
comment|// force first offset to write its length
name|lastOffsetLength
operator|=
operator|-
literal|1
expr_stmt|;
name|skipListWriter
operator|.
name|resetSkip
argument_list|()
expr_stmt|;
block|}
comment|// Currently, this instance is re-used across fields, so
comment|// our parent calls setField whenever the field changes
annotation|@
name|Override
DECL|method|setField
specifier|public
name|int
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|super
operator|.
name|setField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
comment|//System.out.println("SPW: setField");
comment|/*     if (BlockTreeTermsWriter.DEBUG&& fieldInfo.name.equals("id")) {       DEBUG = true;     } else {       DEBUG = false;     }     */
name|lastState
operator|=
name|emptyState
expr_stmt|;
comment|//System.out.println("  set init blockFreqStart=" + freqStart);
comment|//System.out.println("  set init blockProxStart=" + proxStart);
return|return
literal|0
return|;
block|}
DECL|field|lastDocID
name|int
name|lastDocID
decl_stmt|;
DECL|field|df
name|int
name|df
decl_stmt|;
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
comment|// if (DEBUG) System.out.println("SPW:   startDoc seg=" + segment + " docID=" + docID + " tf=" + termDocFreq + " freqOut.fp=" + freqOut.getFilePointer());
specifier|final
name|int
name|delta
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
name|df
operator|>
literal|0
operator|&&
name|delta
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
literal|" )"
argument_list|,
name|freqOut
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
operator|++
name|df
operator|%
name|skipInterval
operator|)
operator|==
literal|0
condition|)
block|{
name|skipListWriter
operator|.
name|setSkipData
argument_list|(
name|lastDocID
argument_list|,
name|writePayloads
argument_list|,
name|lastPayloadLength
argument_list|,
name|writeOffsets
argument_list|,
name|lastOffsetLength
argument_list|)
expr_stmt|;
name|skipListWriter
operator|.
name|bufferSkip
argument_list|(
name|df
argument_list|)
expr_stmt|;
block|}
assert|assert
name|docID
operator|<
name|totalNumDocs
operator|:
literal|"docID="
operator|+
name|docID
operator|+
literal|" totalNumDocs="
operator|+
name|totalNumDocs
assert|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|1
operator|==
name|termDocFreq
condition|)
block|{
name|freqOut
operator|.
name|writeVInt
argument_list|(
operator|(
name|delta
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
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|freqOut
operator|.
name|writeVInt
argument_list|(
name|termDocFreq
argument_list|)
expr_stmt|;
block|}
name|lastPosition
operator|=
literal|0
expr_stmt|;
name|lastOffset
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
comment|//if (DEBUG) System.out.println("SPW:     addPos pos=" + position + " payload=" + (payload == null ? "null" : (payload.length + " bytes")) + " proxFP=" + proxOut.getFilePointer());
assert|assert
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
operator|:
literal|"invalid indexOptions: "
operator|+
name|indexOptions
assert|;
assert|assert
name|proxOut
operator|!=
literal|null
assert|;
specifier|final
name|int
name|delta
init|=
name|position
operator|-
name|lastPosition
decl_stmt|;
assert|assert
name|delta
operator|>=
literal|0
operator|:
literal|"position="
operator|+
name|position
operator|+
literal|" lastPosition="
operator|+
name|lastPosition
assert|;
comment|// not quite right (if pos=0 is repeated twice we don't catch it)
name|lastPosition
operator|=
name|position
expr_stmt|;
name|int
name|payloadLength
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|writePayloads
condition|)
block|{
name|payloadLength
operator|=
name|payload
operator|==
literal|null
condition|?
literal|0
else|:
name|payload
operator|.
name|length
expr_stmt|;
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
name|proxOut
operator|.
name|writeVInt
argument_list|(
operator|(
name|delta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writeOffsets
condition|)
block|{
comment|// don't use startOffset - lastEndOffset, because this creates lots of negative vints for synonyms,
comment|// and the numbers aren't that much smaller anyways.
name|int
name|offsetDelta
init|=
name|startOffset
operator|-
name|lastOffset
decl_stmt|;
name|int
name|offsetLength
init|=
name|endOffset
operator|-
name|startOffset
decl_stmt|;
assert|assert
name|offsetDelta
operator|>=
literal|0
operator|&&
name|offsetLength
operator|>=
literal|0
operator|:
literal|"startOffset="
operator|+
name|startOffset
operator|+
literal|",lastOffset="
operator|+
name|lastOffset
operator|+
literal|",endOffset="
operator|+
name|endOffset
assert|;
if|if
condition|(
name|offsetLength
operator|!=
name|lastOffsetLength
condition|)
block|{
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|offsetDelta
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|offsetLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|proxOut
operator|.
name|writeVInt
argument_list|(
name|offsetDelta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
name|lastOffset
operator|=
name|startOffset
expr_stmt|;
name|lastOffsetLength
operator|=
name|offsetLength
expr_stmt|;
block|}
if|if
condition|(
name|payloadLength
operator|>
literal|0
condition|)
block|{
name|proxOut
operator|.
name|writeBytes
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|payloadLength
argument_list|)
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
block|{   }
DECL|class|StandardTermState
specifier|private
specifier|static
class|class
name|StandardTermState
extends|extends
name|BlockTermState
block|{
DECL|field|freqStart
specifier|public
name|long
name|freqStart
decl_stmt|;
DECL|field|proxStart
specifier|public
name|long
name|proxStart
decl_stmt|;
DECL|field|skipOffset
specifier|public
name|long
name|skipOffset
decl_stmt|;
block|}
comment|/** Called when we are done adding docs to this term */
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BlockTermState
name|_state
parameter_list|)
throws|throws
name|IOException
block|{
name|StandardTermState
name|state
init|=
operator|(
name|StandardTermState
operator|)
name|_state
decl_stmt|;
comment|// if (DEBUG) System.out.println("SPW: finishTerm seg=" + segment + " freqStart=" + freqStart);
assert|assert
name|state
operator|.
name|docFreq
operator|>
literal|0
assert|;
comment|// TODO: wasteful we are counting this (counting # docs
comment|// for this term) in two places?
assert|assert
name|state
operator|.
name|docFreq
operator|==
name|df
assert|;
name|state
operator|.
name|freqStart
operator|=
name|freqStart
expr_stmt|;
name|state
operator|.
name|proxStart
operator|=
name|proxStart
expr_stmt|;
if|if
condition|(
name|df
operator|>=
name|skipMinimum
condition|)
block|{
name|state
operator|.
name|skipOffset
operator|=
name|skipListWriter
operator|.
name|writeSkip
argument_list|(
name|freqOut
argument_list|)
operator|-
name|freqStart
expr_stmt|;
block|}
else|else
block|{
name|state
operator|.
name|skipOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|lastDocID
operator|=
literal|0
expr_stmt|;
name|df
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encodeTerm
specifier|public
name|void
name|encodeTerm
parameter_list|(
name|long
index|[]
name|empty
parameter_list|,
name|DataOutput
name|out
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|_state
parameter_list|,
name|boolean
name|absolute
parameter_list|)
throws|throws
name|IOException
block|{
name|StandardTermState
name|state
init|=
operator|(
name|StandardTermState
operator|)
name|_state
decl_stmt|;
if|if
condition|(
name|absolute
condition|)
block|{
name|lastState
operator|=
name|emptyState
expr_stmt|;
block|}
name|out
operator|.
name|writeVLong
argument_list|(
name|state
operator|.
name|freqStart
operator|-
name|lastState
operator|.
name|freqStart
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|skipOffset
operator|!=
operator|-
literal|1
condition|)
block|{
assert|assert
name|state
operator|.
name|skipOffset
operator|>
literal|0
assert|;
name|out
operator|.
name|writeVLong
argument_list|(
name|state
operator|.
name|skipOffset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
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
condition|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|state
operator|.
name|proxStart
operator|-
name|lastState
operator|.
name|proxStart
argument_list|)
expr_stmt|;
block|}
name|lastState
operator|=
name|state
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
try|try
block|{
name|freqOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|proxOut
operator|!=
literal|null
condition|)
block|{
name|proxOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
