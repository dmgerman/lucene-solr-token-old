begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.sep
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|sep
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
name|Collection
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
name|DocsAndPositionsEnum
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
name|codecs
operator|.
name|PostingsReaderBase
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
name|codecs
operator|.
name|TermState
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
name|IndexInput
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
name|Bits
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
name|CodecUtil
import|;
end_import
begin_comment
comment|/** Concrete class that reads the current doc/freq/skip  *  postings format.      *  * @lucene.experimental  */
end_comment
begin_comment
comment|// TODO: -- should we switch "hasProx" higher up?  and
end_comment
begin_comment
comment|// create two separate docs readers, one that also reads
end_comment
begin_comment
comment|// prox and one that doesn't?
end_comment
begin_class
DECL|class|SepPostingsReaderImpl
specifier|public
class|class
name|SepPostingsReaderImpl
extends|extends
name|PostingsReaderBase
block|{
DECL|field|freqIn
specifier|final
name|IntIndexInput
name|freqIn
decl_stmt|;
DECL|field|docIn
specifier|final
name|IntIndexInput
name|docIn
decl_stmt|;
DECL|field|posIn
specifier|final
name|IntIndexInput
name|posIn
decl_stmt|;
DECL|field|payloadIn
specifier|final
name|IndexInput
name|payloadIn
decl_stmt|;
DECL|field|skipIn
specifier|final
name|IndexInput
name|skipIn
decl_stmt|;
DECL|field|skipInterval
name|int
name|skipInterval
decl_stmt|;
DECL|field|maxSkipLevels
name|int
name|maxSkipLevels
decl_stmt|;
DECL|method|SepPostingsReaderImpl
specifier|public
name|SepPostingsReaderImpl
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|int
name|readBufferSize
parameter_list|,
name|IntStreamFactory
name|intFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|docFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|DOC_EXTENSION
argument_list|)
decl_stmt|;
name|docIn
operator|=
name|intFactory
operator|.
name|openInput
argument_list|(
name|dir
argument_list|,
name|docFileName
argument_list|)
expr_stmt|;
name|skipIn
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|SKIP_EXTENSION
argument_list|)
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|segmentInfo
operator|.
name|getHasProx
argument_list|()
condition|)
block|{
name|freqIn
operator|=
name|intFactory
operator|.
name|openInput
argument_list|(
name|dir
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|FREQ_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|posIn
operator|=
name|intFactory
operator|.
name|openInput
argument_list|(
name|dir
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|POS_EXTENSION
argument_list|)
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
name|payloadIn
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|PAYLOAD_EXTENSION
argument_list|)
argument_list|,
name|readBufferSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posIn
operator|=
literal|null
expr_stmt|;
name|payloadIn
operator|=
literal|null
expr_stmt|;
name|freqIn
operator|=
literal|null
expr_stmt|;
block|}
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
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|files
specifier|public
specifier|static
name|void
name|files
parameter_list|(
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|DOC_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|SKIP_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|segmentInfo
operator|.
name|getHasProx
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
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|FREQ_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|POS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|PAYLOAD_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|IndexInput
name|termsIn
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Make sure we are talking to the matching past writer
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|termsIn
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|CODEC
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|VERSION_START
argument_list|,
name|SepPostingsWriterImpl
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
name|skipInterval
operator|=
name|termsIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|maxSkipLevels
operator|=
name|termsIn
operator|.
name|readInt
argument_list|()
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
if|if
condition|(
name|freqIn
operator|!=
literal|null
condition|)
name|freqIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|docIn
operator|!=
literal|null
condition|)
name|docIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|skipIn
operator|!=
literal|null
condition|)
name|skipIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|posIn
operator|!=
literal|null
condition|)
block|{
name|posIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|payloadIn
operator|!=
literal|null
condition|)
block|{
name|payloadIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
DECL|class|SepTermState
specifier|private
specifier|static
class|class
name|SepTermState
extends|extends
name|TermState
block|{
comment|// We store only the seek point to the docs file because
comment|// the rest of the info (freqIndex, posIndex, etc.) is
comment|// stored in the docs file:
DECL|field|docIndex
name|IntIndexInput
operator|.
name|Index
name|docIndex
decl_stmt|;
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|SepTermState
name|other
init|=
operator|(
name|SepTermState
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|other
operator|.
name|docIndex
operator|=
operator|(
name|IntIndexInput
operator|.
name|Index
operator|)
name|docIndex
operator|.
name|clone
argument_list|()
expr_stmt|;
return|return
name|other
return|;
block|}
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|TermState
name|_other
parameter_list|)
block|{
name|super
operator|.
name|copy
argument_list|(
name|_other
argument_list|)
expr_stmt|;
name|SepTermState
name|other
init|=
operator|(
name|SepTermState
operator|)
name|_other
decl_stmt|;
name|docIndex
operator|.
name|set
argument_list|(
name|other
operator|.
name|docIndex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"tis.fp="
operator|+
name|filePointer
operator|+
literal|" docFreq="
operator|+
name|docFreq
operator|+
literal|" ord="
operator|+
name|ord
operator|+
literal|" docIndex="
operator|+
name|docIndex
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|newTermState
specifier|public
name|TermState
name|newTermState
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|SepTermState
name|state
init|=
operator|new
name|SepTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|docIndex
operator|=
name|docIn
operator|.
name|index
argument_list|()
expr_stmt|;
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|readTerm
specifier|public
name|void
name|readTerm
parameter_list|(
name|IndexInput
name|termsIn
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|TermState
name|termState
parameter_list|,
name|boolean
name|isIndexTerm
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|SepTermState
operator|)
name|termState
operator|)
operator|.
name|docIndex
operator|.
name|read
argument_list|(
name|termsIn
argument_list|,
name|isIndexTerm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|TermState
name|_termState
parameter_list|,
name|Bits
name|skipDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SepTermState
name|termState
init|=
operator|(
name|SepTermState
operator|)
name|_termState
decl_stmt|;
name|SepDocsEnum
name|docsEnum
decl_stmt|;
if|if
condition|(
name|reuse
operator|==
literal|null
operator|||
operator|!
operator|(
name|reuse
operator|instanceof
name|SepDocsEnum
operator|)
condition|)
block|{
name|docsEnum
operator|=
operator|new
name|SepDocsEnum
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|docsEnum
operator|=
operator|(
name|SepDocsEnum
operator|)
name|reuse
expr_stmt|;
if|if
condition|(
name|docsEnum
operator|.
name|startDocIn
operator|!=
name|docIn
condition|)
block|{
comment|// If you are using ParellelReader, and pass in a
comment|// reused DocsAndPositionsEnum, it could have come
comment|// from another reader also using sep codec
name|docsEnum
operator|=
operator|new
name|SepDocsEnum
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|docsEnum
operator|.
name|init
argument_list|(
name|fieldInfo
argument_list|,
name|termState
argument_list|,
name|skipDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|TermState
name|_termState
parameter_list|,
name|Bits
name|skipDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|!
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
assert|;
specifier|final
name|SepTermState
name|termState
init|=
operator|(
name|SepTermState
operator|)
name|_termState
decl_stmt|;
name|SepDocsAndPositionsEnum
name|postingsEnum
decl_stmt|;
if|if
condition|(
name|reuse
operator|==
literal|null
operator|||
operator|!
operator|(
name|reuse
operator|instanceof
name|SepDocsAndPositionsEnum
operator|)
condition|)
block|{
name|postingsEnum
operator|=
operator|new
name|SepDocsAndPositionsEnum
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|postingsEnum
operator|=
operator|(
name|SepDocsAndPositionsEnum
operator|)
name|reuse
expr_stmt|;
if|if
condition|(
name|postingsEnum
operator|.
name|startDocIn
operator|!=
name|docIn
condition|)
block|{
comment|// If you are using ParellelReader, and pass in a
comment|// reused DocsAndPositionsEnum, it could have come
comment|// from another reader also using sep codec
name|postingsEnum
operator|=
operator|new
name|SepDocsAndPositionsEnum
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|postingsEnum
operator|.
name|init
argument_list|(
name|fieldInfo
argument_list|,
name|termState
argument_list|,
name|skipDocs
argument_list|)
return|;
block|}
DECL|class|SepDocsEnum
class|class
name|SepDocsEnum
extends|extends
name|DocsEnum
block|{
DECL|field|docFreq
name|int
name|docFreq
decl_stmt|;
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|freq
name|int
name|freq
decl_stmt|;
DECL|field|freqStart
name|long
name|freqStart
decl_stmt|;
comment|// TODO: -- should we do omitTF with 2 different enum classes?
DECL|field|omitTF
specifier|private
name|boolean
name|omitTF
decl_stmt|;
DECL|field|storePayloads
specifier|private
name|boolean
name|storePayloads
decl_stmt|;
DECL|field|skipDocs
specifier|private
name|Bits
name|skipDocs
decl_stmt|;
DECL|field|docReader
specifier|private
specifier|final
name|IntIndexInput
operator|.
name|Reader
name|docReader
decl_stmt|;
DECL|field|freqReader
specifier|private
specifier|final
name|IntIndexInput
operator|.
name|Reader
name|freqReader
decl_stmt|;
DECL|field|skipOffset
specifier|private
name|long
name|skipOffset
decl_stmt|;
DECL|field|docIndex
specifier|private
specifier|final
name|IntIndexInput
operator|.
name|Index
name|docIndex
decl_stmt|;
DECL|field|freqIndex
specifier|private
specifier|final
name|IntIndexInput
operator|.
name|Index
name|freqIndex
decl_stmt|;
DECL|field|posIndex
specifier|private
specifier|final
name|IntIndexInput
operator|.
name|Index
name|posIndex
decl_stmt|;
DECL|field|startDocIn
specifier|private
specifier|final
name|IntIndexInput
name|startDocIn
decl_stmt|;
comment|// TODO: -- should we do hasProx with 2 different enum classes?
DECL|field|skipped
name|boolean
name|skipped
decl_stmt|;
DECL|field|skipper
name|SepSkipListReader
name|skipper
decl_stmt|;
DECL|method|SepDocsEnum
name|SepDocsEnum
parameter_list|()
throws|throws
name|IOException
block|{
name|startDocIn
operator|=
name|docIn
expr_stmt|;
name|docReader
operator|=
name|docIn
operator|.
name|reader
argument_list|()
expr_stmt|;
name|docIndex
operator|=
name|docIn
operator|.
name|index
argument_list|()
expr_stmt|;
if|if
condition|(
name|freqIn
operator|!=
literal|null
condition|)
block|{
name|freqReader
operator|=
name|freqIn
operator|.
name|reader
argument_list|()
expr_stmt|;
name|freqIndex
operator|=
name|freqIn
operator|.
name|index
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|freqReader
operator|=
literal|null
expr_stmt|;
name|freqIndex
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|posIn
operator|!=
literal|null
condition|)
block|{
name|posIndex
operator|=
name|posIn
operator|.
name|index
argument_list|()
expr_stmt|;
comment|// only init this so skipper can read it
block|}
else|else
block|{
name|posIndex
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|init
name|SepDocsEnum
name|init
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|SepTermState
name|termState
parameter_list|,
name|Bits
name|skipDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|skipDocs
operator|=
name|skipDocs
expr_stmt|;
name|omitTF
operator|=
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
expr_stmt|;
name|storePayloads
operator|=
name|fieldInfo
operator|.
name|storePayloads
expr_stmt|;
comment|// TODO: can't we only do this if consumer
comment|// skipped consuming the previous docs?
name|docIndex
operator|.
name|set
argument_list|(
name|termState
operator|.
name|docIndex
argument_list|)
expr_stmt|;
name|docIndex
operator|.
name|seek
argument_list|(
name|docReader
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|omitTF
condition|)
block|{
name|freqIndex
operator|.
name|read
argument_list|(
name|docReader
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|freqIndex
operator|.
name|seek
argument_list|(
name|freqReader
argument_list|)
expr_stmt|;
name|posIndex
operator|.
name|read
argument_list|(
name|docReader
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// skip payload offset
name|docReader
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|freq
operator|=
literal|1
expr_stmt|;
block|}
name|skipOffset
operator|=
name|docReader
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|docFreq
operator|=
name|termState
operator|.
name|docFreq
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|doc
operator|=
literal|0
expr_stmt|;
name|skipped
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|count
operator|==
name|docFreq
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
name|count
operator|++
expr_stmt|;
comment|// Decode next doc
name|doc
operator|+=
name|docReader
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|omitTF
condition|)
block|{
name|freq
operator|=
name|freqReader
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|skipDocs
operator|==
literal|null
operator|||
operator|!
name|skipDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: -- switch to bulk read api in IntIndexInput
specifier|final
name|int
index|[]
name|docs
init|=
name|bulkResult
operator|.
name|docs
operator|.
name|ints
decl_stmt|;
specifier|final
name|int
index|[]
name|freqs
init|=
name|bulkResult
operator|.
name|freqs
operator|.
name|ints
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|docs
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|length
operator|&&
name|count
operator|<
name|docFreq
condition|)
block|{
name|count
operator|++
expr_stmt|;
comment|// manually inlined call to next() for speed
name|doc
operator|+=
name|docReader
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|omitTF
condition|)
block|{
name|freq
operator|=
name|freqReader
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|skipDocs
operator|==
literal|null
operator|||
operator|!
name|skipDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|doc
expr_stmt|;
name|freqs
index|[
name|i
index|]
operator|=
name|freq
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
return|return
name|i
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: jump right to next() if target is< X away
comment|// from where we are now?
if|if
condition|(
name|docFreq
operator|>=
name|skipInterval
condition|)
block|{
comment|// There are enough docs in the posting to have
comment|// skip data
if|if
condition|(
name|skipper
operator|==
literal|null
condition|)
block|{
comment|// This DocsEnum has never done any skipping
name|skipper
operator|=
operator|new
name|SepSkipListReader
argument_list|(
operator|(
name|IndexInput
operator|)
name|skipIn
operator|.
name|clone
argument_list|()
argument_list|,
name|freqIn
argument_list|,
name|docIn
argument_list|,
name|posIn
argument_list|,
name|maxSkipLevels
argument_list|,
name|skipInterval
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|skipped
condition|)
block|{
comment|// We haven't yet skipped for this posting
name|skipper
operator|.
name|init
argument_list|(
name|skipOffset
argument_list|,
name|docIndex
argument_list|,
name|freqIndex
argument_list|,
name|posIndex
argument_list|,
literal|0
argument_list|,
name|docFreq
argument_list|,
name|storePayloads
argument_list|)
expr_stmt|;
name|skipper
operator|.
name|setOmitTF
argument_list|(
name|omitTF
argument_list|)
expr_stmt|;
name|skipped
operator|=
literal|true
expr_stmt|;
block|}
specifier|final
name|int
name|newCount
init|=
name|skipper
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|newCount
operator|>
name|count
condition|)
block|{
comment|// Skipper did move
if|if
condition|(
operator|!
name|omitTF
condition|)
block|{
name|skipper
operator|.
name|getFreqIndex
argument_list|()
operator|.
name|seek
argument_list|(
name|freqReader
argument_list|)
expr_stmt|;
block|}
name|skipper
operator|.
name|getDocIndex
argument_list|()
operator|.
name|seek
argument_list|(
name|docReader
argument_list|)
expr_stmt|;
name|count
operator|=
name|newCount
expr_stmt|;
name|doc
operator|=
name|skipper
operator|.
name|getDoc
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Now, linear scan for the rest:
do|do
block|{
if|if
condition|(
name|nextDoc
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
block|}
do|while
condition|(
name|target
operator|>
name|doc
condition|)
do|;
return|return
name|doc
return|;
block|}
block|}
DECL|class|SepDocsAndPositionsEnum
class|class
name|SepDocsAndPositionsEnum
extends|extends
name|DocsAndPositionsEnum
block|{
DECL|field|docFreq
name|int
name|docFreq
decl_stmt|;
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|freq
name|int
name|freq
decl_stmt|;
DECL|field|freqStart
name|long
name|freqStart
decl_stmt|;
DECL|field|storePayloads
specifier|private
name|boolean
name|storePayloads
decl_stmt|;
DECL|field|skipDocs
specifier|private
name|Bits
name|skipDocs
decl_stmt|;
DECL|field|docReader
specifier|private
specifier|final
name|IntIndexInput
operator|.
name|Reader
name|docReader
decl_stmt|;
DECL|field|freqReader
specifier|private
specifier|final
name|IntIndexInput
operator|.
name|Reader
name|freqReader
decl_stmt|;
DECL|field|posReader
specifier|private
specifier|final
name|IntIndexInput
operator|.
name|Reader
name|posReader
decl_stmt|;
DECL|field|payloadIn
specifier|private
specifier|final
name|IndexInput
name|payloadIn
decl_stmt|;
DECL|field|skipOffset
specifier|private
name|long
name|skipOffset
decl_stmt|;
DECL|field|docIndex
specifier|private
specifier|final
name|IntIndexInput
operator|.
name|Index
name|docIndex
decl_stmt|;
DECL|field|freqIndex
specifier|private
specifier|final
name|IntIndexInput
operator|.
name|Index
name|freqIndex
decl_stmt|;
DECL|field|posIndex
specifier|private
specifier|final
name|IntIndexInput
operator|.
name|Index
name|posIndex
decl_stmt|;
DECL|field|startDocIn
specifier|private
specifier|final
name|IntIndexInput
name|startDocIn
decl_stmt|;
DECL|field|payloadOffset
specifier|private
name|long
name|payloadOffset
decl_stmt|;
DECL|field|pendingPosCount
specifier|private
name|int
name|pendingPosCount
decl_stmt|;
DECL|field|position
specifier|private
name|int
name|position
decl_stmt|;
DECL|field|payloadLength
specifier|private
name|int
name|payloadLength
decl_stmt|;
DECL|field|pendingPayloadBytes
specifier|private
name|long
name|pendingPayloadBytes
decl_stmt|;
DECL|field|skipped
specifier|private
name|boolean
name|skipped
decl_stmt|;
DECL|field|skipper
specifier|private
name|SepSkipListReader
name|skipper
decl_stmt|;
DECL|field|payloadPending
specifier|private
name|boolean
name|payloadPending
decl_stmt|;
DECL|field|posSeekPending
specifier|private
name|boolean
name|posSeekPending
decl_stmt|;
DECL|method|SepDocsAndPositionsEnum
name|SepDocsAndPositionsEnum
parameter_list|()
throws|throws
name|IOException
block|{
name|startDocIn
operator|=
name|docIn
expr_stmt|;
name|docReader
operator|=
name|docIn
operator|.
name|reader
argument_list|()
expr_stmt|;
name|docIndex
operator|=
name|docIn
operator|.
name|index
argument_list|()
expr_stmt|;
name|freqReader
operator|=
name|freqIn
operator|.
name|reader
argument_list|()
expr_stmt|;
name|freqIndex
operator|=
name|freqIn
operator|.
name|index
argument_list|()
expr_stmt|;
name|posReader
operator|=
name|posIn
operator|.
name|reader
argument_list|()
expr_stmt|;
name|posIndex
operator|=
name|posIn
operator|.
name|index
argument_list|()
expr_stmt|;
name|payloadIn
operator|=
operator|(
name|IndexInput
operator|)
name|SepPostingsReaderImpl
operator|.
name|this
operator|.
name|payloadIn
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
DECL|method|init
name|SepDocsAndPositionsEnum
name|init
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|SepTermState
name|termState
parameter_list|,
name|Bits
name|skipDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|skipDocs
operator|=
name|skipDocs
expr_stmt|;
name|storePayloads
operator|=
name|fieldInfo
operator|.
name|storePayloads
expr_stmt|;
comment|// TODO: can't we only do this if consumer
comment|// skipped consuming the previous docs?
name|docIndex
operator|.
name|set
argument_list|(
name|termState
operator|.
name|docIndex
argument_list|)
expr_stmt|;
name|docIndex
operator|.
name|seek
argument_list|(
name|docReader
argument_list|)
expr_stmt|;
name|freqIndex
operator|.
name|read
argument_list|(
name|docReader
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|freqIndex
operator|.
name|seek
argument_list|(
name|freqReader
argument_list|)
expr_stmt|;
name|posIndex
operator|.
name|read
argument_list|(
name|docReader
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|posSeekPending
operator|=
literal|true
expr_stmt|;
name|payloadPending
operator|=
literal|false
expr_stmt|;
name|payloadOffset
operator|=
name|docReader
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|skipOffset
operator|=
name|docReader
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|docFreq
operator|=
name|termState
operator|.
name|docFreq
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|doc
operator|=
literal|0
expr_stmt|;
name|pendingPosCount
operator|=
literal|0
expr_stmt|;
name|pendingPayloadBytes
operator|=
literal|0
expr_stmt|;
name|skipped
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|count
operator|==
name|docFreq
condition|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
name|count
operator|++
expr_stmt|;
comment|// TODO: maybe we should do the 1-bit trick for encoding
comment|// freq=1 case?
comment|// Decode next doc
name|doc
operator|+=
name|docReader
operator|.
name|next
argument_list|()
expr_stmt|;
name|freq
operator|=
name|freqReader
operator|.
name|next
argument_list|()
expr_stmt|;
name|pendingPosCount
operator|+=
name|freq
expr_stmt|;
if|if
condition|(
name|skipDocs
operator|==
literal|null
operator|||
operator|!
name|skipDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
name|position
operator|=
literal|0
expr_stmt|;
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: jump right to next() if target is< X away
comment|// from where we are now?
if|if
condition|(
name|docFreq
operator|>=
name|skipInterval
condition|)
block|{
comment|// There are enough docs in the posting to have
comment|// skip data
if|if
condition|(
name|skipper
operator|==
literal|null
condition|)
block|{
comment|// This DocsEnum has never done any skipping
name|skipper
operator|=
operator|new
name|SepSkipListReader
argument_list|(
operator|(
name|IndexInput
operator|)
name|skipIn
operator|.
name|clone
argument_list|()
argument_list|,
name|freqIn
argument_list|,
name|docIn
argument_list|,
name|posIn
argument_list|,
name|maxSkipLevels
argument_list|,
name|skipInterval
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|skipped
condition|)
block|{
comment|// We haven't yet skipped for this posting
name|skipper
operator|.
name|init
argument_list|(
name|skipOffset
argument_list|,
name|docIndex
argument_list|,
name|freqIndex
argument_list|,
name|posIndex
argument_list|,
name|payloadOffset
argument_list|,
name|docFreq
argument_list|,
name|storePayloads
argument_list|)
expr_stmt|;
name|skipped
operator|=
literal|true
expr_stmt|;
block|}
specifier|final
name|int
name|newCount
init|=
name|skipper
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|newCount
operator|>
name|count
condition|)
block|{
comment|// Skipper did move
name|skipper
operator|.
name|getFreqIndex
argument_list|()
operator|.
name|seek
argument_list|(
name|freqReader
argument_list|)
expr_stmt|;
name|skipper
operator|.
name|getDocIndex
argument_list|()
operator|.
name|seek
argument_list|(
name|docReader
argument_list|)
expr_stmt|;
comment|//skipper.getPosIndex().seek(posReader);
name|posIndex
operator|.
name|set
argument_list|(
name|skipper
operator|.
name|getPosIndex
argument_list|()
argument_list|)
expr_stmt|;
name|posSeekPending
operator|=
literal|true
expr_stmt|;
name|count
operator|=
name|newCount
expr_stmt|;
name|doc
operator|=
name|skipper
operator|.
name|getDoc
argument_list|()
expr_stmt|;
comment|//payloadIn.seek(skipper.getPayloadPointer());
name|payloadOffset
operator|=
name|skipper
operator|.
name|getPayloadPointer
argument_list|()
expr_stmt|;
name|pendingPosCount
operator|=
literal|0
expr_stmt|;
name|pendingPayloadBytes
operator|=
literal|0
expr_stmt|;
name|payloadPending
operator|=
literal|false
expr_stmt|;
name|payloadLength
operator|=
name|skipper
operator|.
name|getPayloadLength
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Now, linear scan for the rest:
do|do
block|{
if|if
condition|(
name|nextDoc
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
block|}
do|while
condition|(
name|target
operator|>
name|doc
condition|)
do|;
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|posSeekPending
condition|)
block|{
name|posIndex
operator|.
name|seek
argument_list|(
name|posReader
argument_list|)
expr_stmt|;
name|payloadIn
operator|.
name|seek
argument_list|(
name|payloadOffset
argument_list|)
expr_stmt|;
name|posSeekPending
operator|=
literal|false
expr_stmt|;
block|}
comment|// scan over any docs that were iterated without their
comment|// positions
while|while
condition|(
name|pendingPosCount
operator|>
name|freq
condition|)
block|{
specifier|final
name|int
name|code
init|=
name|posReader
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|storePayloads
operator|&&
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// Payload length has changed
name|payloadLength
operator|=
name|posReader
operator|.
name|next
argument_list|()
expr_stmt|;
assert|assert
name|payloadLength
operator|>=
literal|0
assert|;
block|}
name|pendingPosCount
operator|--
expr_stmt|;
name|position
operator|=
literal|0
expr_stmt|;
name|pendingPayloadBytes
operator|+=
name|payloadLength
expr_stmt|;
block|}
specifier|final
name|int
name|code
init|=
name|posReader
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|storePayloads
condition|)
block|{
if|if
condition|(
operator|(
name|code
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// Payload length has changed
name|payloadLength
operator|=
name|posReader
operator|.
name|next
argument_list|()
expr_stmt|;
assert|assert
name|payloadLength
operator|>=
literal|0
assert|;
block|}
name|position
operator|+=
name|code
operator|>>
literal|1
expr_stmt|;
name|pendingPayloadBytes
operator|+=
name|payloadLength
expr_stmt|;
name|payloadPending
operator|=
name|payloadLength
operator|>
literal|0
expr_stmt|;
block|}
else|else
block|{
name|position
operator|+=
name|code
expr_stmt|;
block|}
name|pendingPosCount
operator|--
expr_stmt|;
assert|assert
name|pendingPosCount
operator|>=
literal|0
assert|;
return|return
name|position
return|;
block|}
DECL|field|payload
specifier|private
name|BytesRef
name|payload
decl_stmt|;
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|payloadPending
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Either no payload exists at this term position or an attempt was made to load it more than once."
argument_list|)
throw|;
block|}
assert|assert
name|pendingPayloadBytes
operator|>=
name|payloadLength
assert|;
if|if
condition|(
name|pendingPayloadBytes
operator|>
name|payloadLength
condition|)
block|{
name|payloadIn
operator|.
name|seek
argument_list|(
name|payloadIn
operator|.
name|getFilePointer
argument_list|()
operator|+
operator|(
name|pendingPayloadBytes
operator|-
name|payloadLength
operator|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|payload
operator|==
literal|null
condition|)
block|{
name|payload
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|payload
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|payloadLength
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|payload
operator|.
name|bytes
operator|.
name|length
operator|<
name|payloadLength
condition|)
block|{
name|payload
operator|.
name|grow
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
name|payloadIn
operator|.
name|readBytes
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|payloadLength
argument_list|)
expr_stmt|;
name|payloadPending
operator|=
literal|false
expr_stmt|;
name|payload
operator|.
name|length
operator|=
name|payloadLength
expr_stmt|;
name|pendingPayloadBytes
operator|=
literal|0
expr_stmt|;
return|return
name|payload
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayload
specifier|public
name|boolean
name|hasPayload
parameter_list|()
block|{
return|return
name|payloadPending
operator|&&
name|payloadLength
operator|>
literal|0
return|;
block|}
block|}
block|}
end_class
end_unit
