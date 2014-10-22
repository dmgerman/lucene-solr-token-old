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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|RamUsageEstimator
import|;
end_import
begin_class
DECL|class|TermVectorsConsumerPerField
specifier|final
class|class
name|TermVectorsConsumerPerField
extends|extends
name|TermsHashPerField
block|{
DECL|field|termVectorsPostingsArray
specifier|private
name|TermVectorsPostingsArray
name|termVectorsPostingsArray
decl_stmt|;
DECL|field|termsWriter
specifier|final
name|TermVectorsConsumer
name|termsWriter
decl_stmt|;
DECL|field|doVectors
name|boolean
name|doVectors
decl_stmt|;
DECL|field|doVectorPositions
name|boolean
name|doVectorPositions
decl_stmt|;
DECL|field|doVectorOffsets
name|boolean
name|doVectorOffsets
decl_stmt|;
DECL|field|doVectorPayloads
name|boolean
name|doVectorPayloads
decl_stmt|;
DECL|field|offsetAttribute
name|OffsetAttribute
name|offsetAttribute
decl_stmt|;
DECL|field|payloadAttribute
name|PayloadAttribute
name|payloadAttribute
decl_stmt|;
DECL|field|hasPayloads
name|boolean
name|hasPayloads
decl_stmt|;
comment|// if enabled, and we actually saw any for this field
DECL|method|TermVectorsConsumerPerField
specifier|public
name|TermVectorsConsumerPerField
parameter_list|(
name|FieldInvertState
name|invertState
parameter_list|,
name|TermVectorsConsumer
name|termsWriter
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|super
argument_list|(
literal|2
argument_list|,
name|invertState
argument_list|,
name|termsWriter
argument_list|,
literal|null
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|termsWriter
operator|=
name|termsWriter
expr_stmt|;
block|}
comment|/** Called once per field per document if term vectors    *  are enabled, to write the vectors to    *  RAMOutputStream, which is then quickly flushed to    *  the real term vectors files in the Directory. */
annotation|@
name|Override
DECL|method|finish
name|void
name|finish
parameter_list|()
block|{
if|if
condition|(
operator|!
name|doVectors
operator|||
name|bytesHash
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|termsWriter
operator|.
name|addFieldToFlush
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|doVectors
operator|==
literal|false
condition|)
block|{
return|return;
block|}
name|doVectors
operator|=
literal|false
expr_stmt|;
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"TermVectorsTermsWriterPerField.finish start"
argument_list|)
assert|;
specifier|final
name|int
name|numPostings
init|=
name|bytesHash
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|flushTerm
init|=
name|termsWriter
operator|.
name|flushTerm
decl_stmt|;
assert|assert
name|numPostings
operator|>=
literal|0
assert|;
comment|// This is called once, after inverting all occurrences
comment|// of a given field in the doc.  At this point we flush
comment|// our hash into the DocWriter.
name|TermVectorsPostingsArray
name|postings
init|=
name|termVectorsPostingsArray
decl_stmt|;
specifier|final
name|TermVectorsWriter
name|tv
init|=
name|termsWriter
operator|.
name|writer
decl_stmt|;
specifier|final
name|int
index|[]
name|termIDs
init|=
name|sortPostings
argument_list|()
decl_stmt|;
name|tv
operator|.
name|startField
argument_list|(
name|fieldInfo
argument_list|,
name|numPostings
argument_list|,
name|doVectorPositions
argument_list|,
name|doVectorOffsets
argument_list|,
name|hasPayloads
argument_list|)
expr_stmt|;
specifier|final
name|ByteSliceReader
name|posReader
init|=
name|doVectorPositions
condition|?
name|termsWriter
operator|.
name|vectorSliceReaderPos
else|:
literal|null
decl_stmt|;
specifier|final
name|ByteSliceReader
name|offReader
init|=
name|doVectorOffsets
condition|?
name|termsWriter
operator|.
name|vectorSliceReaderOff
else|:
literal|null
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numPostings
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|termID
init|=
name|termIDs
index|[
name|j
index|]
decl_stmt|;
specifier|final
name|int
name|freq
init|=
name|postings
operator|.
name|freqs
index|[
name|termID
index|]
decl_stmt|;
comment|// Get BytesRef
name|termBytePool
operator|.
name|setBytesRef
argument_list|(
name|flushTerm
argument_list|,
name|postings
operator|.
name|textStarts
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
name|tv
operator|.
name|startTerm
argument_list|(
name|flushTerm
argument_list|,
name|freq
argument_list|)
expr_stmt|;
if|if
condition|(
name|doVectorPositions
operator|||
name|doVectorOffsets
condition|)
block|{
if|if
condition|(
name|posReader
operator|!=
literal|null
condition|)
block|{
name|initReader
argument_list|(
name|posReader
argument_list|,
name|termID
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|offReader
operator|!=
literal|null
condition|)
block|{
name|initReader
argument_list|(
name|offReader
argument_list|,
name|termID
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|tv
operator|.
name|addProx
argument_list|(
name|freq
argument_list|,
name|posReader
argument_list|,
name|offReader
argument_list|)
expr_stmt|;
block|}
name|tv
operator|.
name|finishTerm
argument_list|()
expr_stmt|;
block|}
name|tv
operator|.
name|finishField
argument_list|()
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|fieldInfo
operator|.
name|setStoreTermVectors
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start
name|boolean
name|start
parameter_list|(
name|IndexableField
name|field
parameter_list|,
name|boolean
name|first
parameter_list|)
block|{
assert|assert
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|!=
literal|null
assert|;
if|if
condition|(
name|first
condition|)
block|{
if|if
condition|(
name|bytesHash
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
comment|// Only necessary if previous doc hit a
comment|// non-aborting exception while writing vectors in
comment|// this field:
name|reset
argument_list|()
expr_stmt|;
block|}
name|bytesHash
operator|.
name|reinit
argument_list|()
expr_stmt|;
name|hasPayloads
operator|=
literal|false
expr_stmt|;
name|doVectors
operator|=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
expr_stmt|;
if|if
condition|(
name|doVectors
condition|)
block|{
name|termsWriter
operator|.
name|hasVectors
operator|=
literal|true
expr_stmt|;
name|doVectorPositions
operator|=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
expr_stmt|;
comment|// Somewhat confusingly, unlike postings, you are
comment|// allowed to index TV offsets without TV positions:
name|doVectorOffsets
operator|=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
expr_stmt|;
if|if
condition|(
name|doVectorPositions
condition|)
block|{
name|doVectorPayloads
operator|=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|doVectorPayloads
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
condition|)
block|{
comment|// TODO: move this check somewhere else, and impl the other missing ones
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vector payloads without term vector positions (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vector offsets when term vectors are not indexed (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vector positions when term vectors are not indexed (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vector payloads when term vectors are not indexed (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|doVectors
operator|!=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all instances of a given field name must have the same term vectors settings (storeTermVectors changed for field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|doVectorPositions
operator|!=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all instances of a given field name must have the same term vectors settings (storeTermVectorPositions changed for field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|doVectorOffsets
operator|!=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all instances of a given field name must have the same term vectors settings (storeTermVectorOffsets changed for field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|doVectorPayloads
operator|!=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all instances of a given field name must have the same term vectors settings (storeTermVectorPayloads changed for field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|doVectors
condition|)
block|{
if|if
condition|(
name|doVectorOffsets
condition|)
block|{
name|offsetAttribute
operator|=
name|fieldState
operator|.
name|offsetAttribute
expr_stmt|;
assert|assert
name|offsetAttribute
operator|!=
literal|null
assert|;
block|}
if|if
condition|(
name|doVectorPayloads
condition|)
block|{
comment|// Can be null:
name|payloadAttribute
operator|=
name|fieldState
operator|.
name|payloadAttribute
expr_stmt|;
block|}
else|else
block|{
name|payloadAttribute
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|doVectors
return|;
block|}
DECL|method|writeProx
name|void
name|writeProx
parameter_list|(
name|TermVectorsPostingsArray
name|postings
parameter_list|,
name|int
name|termID
parameter_list|)
block|{
if|if
condition|(
name|doVectorOffsets
condition|)
block|{
name|int
name|startOffset
init|=
name|fieldState
operator|.
name|offset
operator|+
name|offsetAttribute
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|endOffset
init|=
name|fieldState
operator|.
name|offset
operator|+
name|offsetAttribute
operator|.
name|endOffset
argument_list|()
decl_stmt|;
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|startOffset
operator|-
name|postings
operator|.
name|lastOffsets
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|endOffset
operator|-
name|startOffset
argument_list|)
expr_stmt|;
name|postings
operator|.
name|lastOffsets
index|[
name|termID
index|]
operator|=
name|endOffset
expr_stmt|;
block|}
if|if
condition|(
name|doVectorPositions
condition|)
block|{
specifier|final
name|BytesRef
name|payload
decl_stmt|;
if|if
condition|(
name|payloadAttribute
operator|==
literal|null
condition|)
block|{
name|payload
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|payload
operator|=
name|payloadAttribute
operator|.
name|getPayload
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|pos
init|=
name|fieldState
operator|.
name|position
operator|-
name|postings
operator|.
name|lastPositions
index|[
name|termID
index|]
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
operator|&&
name|payload
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|writeVInt
argument_list|(
literal|0
argument_list|,
operator|(
name|pos
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|writeBytes
argument_list|(
literal|0
argument_list|,
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|hasPayloads
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|pos
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
name|postings
operator|.
name|lastPositions
index|[
name|termID
index|]
operator|=
name|fieldState
operator|.
name|position
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|newTerm
name|void
name|newTerm
parameter_list|(
specifier|final
name|int
name|termID
parameter_list|)
block|{
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"TermVectorsTermsWriterPerField.newTerm start"
argument_list|)
assert|;
name|TermVectorsPostingsArray
name|postings
init|=
name|termVectorsPostingsArray
decl_stmt|;
name|postings
operator|.
name|freqs
index|[
name|termID
index|]
operator|=
literal|1
expr_stmt|;
name|postings
operator|.
name|lastOffsets
index|[
name|termID
index|]
operator|=
literal|0
expr_stmt|;
name|postings
operator|.
name|lastPositions
index|[
name|termID
index|]
operator|=
literal|0
expr_stmt|;
name|writeProx
argument_list|(
name|postings
argument_list|,
name|termID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addTerm
name|void
name|addTerm
parameter_list|(
specifier|final
name|int
name|termID
parameter_list|)
block|{
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"TermVectorsTermsWriterPerField.addTerm start"
argument_list|)
assert|;
name|TermVectorsPostingsArray
name|postings
init|=
name|termVectorsPostingsArray
decl_stmt|;
name|postings
operator|.
name|freqs
index|[
name|termID
index|]
operator|++
expr_stmt|;
name|writeProx
argument_list|(
name|postings
argument_list|,
name|termID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newPostingsArray
specifier|public
name|void
name|newPostingsArray
parameter_list|()
block|{
name|termVectorsPostingsArray
operator|=
operator|(
name|TermVectorsPostingsArray
operator|)
name|postingsArray
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createPostingsArray
name|ParallelPostingsArray
name|createPostingsArray
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|TermVectorsPostingsArray
argument_list|(
name|size
argument_list|)
return|;
block|}
DECL|class|TermVectorsPostingsArray
specifier|static
specifier|final
class|class
name|TermVectorsPostingsArray
extends|extends
name|ParallelPostingsArray
block|{
DECL|method|TermVectorsPostingsArray
specifier|public
name|TermVectorsPostingsArray
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|freqs
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
name|lastOffsets
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
name|lastPositions
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
block|}
DECL|field|freqs
name|int
index|[]
name|freqs
decl_stmt|;
comment|// How many times this term occurred in the current doc
DECL|field|lastOffsets
name|int
index|[]
name|lastOffsets
decl_stmt|;
comment|// Last offset we saw
DECL|field|lastPositions
name|int
index|[]
name|lastPositions
decl_stmt|;
comment|// Last position where this term occurred
annotation|@
name|Override
DECL|method|newInstance
name|ParallelPostingsArray
name|newInstance
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|TermVectorsPostingsArray
argument_list|(
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyTo
name|void
name|copyTo
parameter_list|(
name|ParallelPostingsArray
name|toArray
parameter_list|,
name|int
name|numToCopy
parameter_list|)
block|{
assert|assert
name|toArray
operator|instanceof
name|TermVectorsPostingsArray
assert|;
name|TermVectorsPostingsArray
name|to
init|=
operator|(
name|TermVectorsPostingsArray
operator|)
name|toArray
decl_stmt|;
name|super
operator|.
name|copyTo
argument_list|(
name|toArray
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|freqs
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|freqs
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastOffsets
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|lastOffsets
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastPositions
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|lastPositions
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bytesPerPosting
name|int
name|bytesPerPosting
parameter_list|()
block|{
return|return
name|super
operator|.
name|bytesPerPosting
argument_list|()
operator|+
literal|3
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
return|;
block|}
block|}
block|}
end_class
end_unit
