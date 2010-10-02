begin_unit
begin_package
DECL|package|org.apache.lucene.index.codecs.pulsing
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
name|pulsing
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
name|pulsing
operator|.
name|PulsingPostingsWriterImpl
operator|.
name|Document
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
name|pulsing
operator|.
name|PulsingPostingsWriterImpl
operator|.
name|Position
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
comment|/** Concrete class that reads the current doc/freq/skip  *  postings format   *  @lucene.experimental */
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
DECL|class|PulsingPostingsReaderImpl
specifier|public
class|class
name|PulsingPostingsReaderImpl
extends|extends
name|PostingsReaderBase
block|{
comment|// Fallback reader for non-pulsed terms:
DECL|field|wrappedPostingsReader
specifier|final
name|PostingsReaderBase
name|wrappedPostingsReader
decl_stmt|;
DECL|field|maxPulsingDocFreq
name|int
name|maxPulsingDocFreq
decl_stmt|;
DECL|method|PulsingPostingsReaderImpl
specifier|public
name|PulsingPostingsReaderImpl
parameter_list|(
name|PostingsReaderBase
name|wrappedPostingsReader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|wrappedPostingsReader
operator|=
name|wrappedPostingsReader
expr_stmt|;
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
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|termsIn
argument_list|,
name|PulsingPostingsWriterImpl
operator|.
name|CODEC
argument_list|,
name|PulsingPostingsWriterImpl
operator|.
name|VERSION_START
argument_list|,
name|PulsingPostingsWriterImpl
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
name|maxPulsingDocFreq
operator|=
name|termsIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|wrappedPostingsReader
operator|.
name|init
argument_list|(
name|termsIn
argument_list|)
expr_stmt|;
block|}
DECL|class|PulsingTermState
specifier|private
specifier|static
class|class
name|PulsingTermState
extends|extends
name|TermState
block|{
DECL|field|docs
specifier|private
name|Document
name|docs
index|[]
decl_stmt|;
DECL|field|wrappedTermState
specifier|private
name|TermState
name|wrappedTermState
decl_stmt|;
DECL|field|pendingIndexTerm
specifier|private
name|boolean
name|pendingIndexTerm
decl_stmt|;
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|PulsingTermState
name|clone
decl_stmt|;
name|clone
operator|=
operator|(
name|PulsingTermState
operator|)
name|super
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|docs
operator|=
name|docs
operator|.
name|clone
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|clone
operator|.
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Document
name|doc
init|=
name|clone
operator|.
name|docs
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|clone
operator|.
name|docs
index|[
name|i
index|]
operator|=
operator|(
name|Document
operator|)
name|doc
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
block|}
name|clone
operator|.
name|wrappedTermState
operator|=
operator|(
name|TermState
operator|)
name|wrappedTermState
operator|.
name|clone
argument_list|()
expr_stmt|;
return|return
name|clone
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
name|PulsingTermState
name|other
init|=
operator|(
name|PulsingTermState
operator|)
name|_other
decl_stmt|;
name|pendingIndexTerm
operator|=
name|other
operator|.
name|pendingIndexTerm
expr_stmt|;
name|wrappedTermState
operator|.
name|copy
argument_list|(
name|other
operator|.
name|wrappedTermState
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|other
operator|.
name|docs
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|docs
index|[
name|i
index|]
operator|=
operator|(
name|Document
operator|)
name|other
operator|.
name|docs
index|[
name|i
index|]
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
block|}
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
name|PulsingTermState
name|state
init|=
operator|new
name|PulsingTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|wrappedTermState
operator|=
name|wrappedPostingsReader
operator|.
name|newTermState
argument_list|()
expr_stmt|;
name|state
operator|.
name|docs
operator|=
operator|new
name|Document
index|[
name|maxPulsingDocFreq
index|]
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
name|_termState
parameter_list|,
name|boolean
name|isIndexTerm
parameter_list|)
throws|throws
name|IOException
block|{
name|PulsingTermState
name|termState
init|=
operator|(
name|PulsingTermState
operator|)
name|_termState
decl_stmt|;
name|termState
operator|.
name|pendingIndexTerm
operator||=
name|isIndexTerm
expr_stmt|;
if|if
condition|(
name|termState
operator|.
name|docFreq
operator|<=
name|maxPulsingDocFreq
condition|)
block|{
comment|// Inlined into terms dict -- read everything in
comment|// TODO: maybe only read everything in lazily?  But
comment|// then we'd need to store length so we could seek
comment|// over it when docs/pos enum was not requested
comment|// TODO: it'd be better to share this encoding logic
comment|// in some inner codec that knows how to write a
comment|// single doc / single position, etc.  This way if a
comment|// given codec wants to store other interesting
comment|// stuff, it could use this pulsing codec to do so
name|int
name|docID
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
name|termState
operator|.
name|docFreq
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|termState
operator|.
name|docs
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|doc
operator|=
name|termState
operator|.
name|docs
index|[
name|i
index|]
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|code
init|=
name|termsIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
condition|)
block|{
name|docID
operator|+=
name|code
expr_stmt|;
name|doc
operator|.
name|numPositions
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|docID
operator|+=
name|code
operator|>>>
literal|1
expr_stmt|;
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
name|doc
operator|.
name|numPositions
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|.
name|numPositions
operator|=
name|termsIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|.
name|numPositions
operator|>
name|doc
operator|.
name|positions
operator|.
name|length
condition|)
block|{
name|doc
operator|.
name|reallocPositions
argument_list|(
name|doc
operator|.
name|numPositions
argument_list|)
expr_stmt|;
block|}
name|int
name|position
init|=
literal|0
decl_stmt|;
name|int
name|payloadLength
init|=
operator|-
literal|1
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
name|doc
operator|.
name|numPositions
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|Position
name|pos
init|=
name|doc
operator|.
name|positions
index|[
name|j
index|]
decl_stmt|;
specifier|final
name|int
name|code2
init|=
name|termsIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|.
name|storePayloads
condition|)
block|{
name|position
operator|+=
name|code2
operator|>>>
literal|1
expr_stmt|;
if|if
condition|(
operator|(
name|code2
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
name|payloadLength
operator|=
name|termsIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|payloadLength
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|pos
operator|.
name|payload
operator|==
literal|null
condition|)
block|{
name|pos
operator|.
name|payload
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|pos
operator|.
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
name|payloadLength
operator|>
name|pos
operator|.
name|payload
operator|.
name|bytes
operator|.
name|length
condition|)
block|{
name|pos
operator|.
name|payload
operator|.
name|grow
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
name|pos
operator|.
name|payload
operator|.
name|length
operator|=
name|payloadLength
expr_stmt|;
name|termsIn
operator|.
name|readBytes
argument_list|(
name|pos
operator|.
name|payload
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|payloadLength
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pos
operator|.
name|payload
operator|!=
literal|null
condition|)
block|{
name|pos
operator|.
name|payload
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
block|}
else|else
block|{
name|position
operator|+=
name|code2
expr_stmt|;
block|}
name|pos
operator|.
name|pos
operator|=
name|position
expr_stmt|;
block|}
block|}
name|doc
operator|.
name|docID
operator|=
name|docID
expr_stmt|;
block|}
block|}
else|else
block|{
name|termState
operator|.
name|wrappedTermState
operator|.
name|docFreq
operator|=
name|termState
operator|.
name|docFreq
expr_stmt|;
name|wrappedPostingsReader
operator|.
name|readTerm
argument_list|(
name|termsIn
argument_list|,
name|fieldInfo
argument_list|,
name|termState
operator|.
name|wrappedTermState
argument_list|,
name|termState
operator|.
name|pendingIndexTerm
argument_list|)
expr_stmt|;
name|termState
operator|.
name|pendingIndexTerm
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|// TODO: we could actually reuse, by having TL that
comment|// holds the last wrapped reuse, and vice-versa
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|FieldInfo
name|field
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
name|PulsingTermState
name|termState
init|=
operator|(
name|PulsingTermState
operator|)
name|_termState
decl_stmt|;
if|if
condition|(
name|termState
operator|.
name|docFreq
operator|<=
name|maxPulsingDocFreq
condition|)
block|{
if|if
condition|(
name|reuse
operator|instanceof
name|PulsingDocsEnum
condition|)
block|{
return|return
operator|(
operator|(
name|PulsingDocsEnum
operator|)
name|reuse
operator|)
operator|.
name|reset
argument_list|(
name|skipDocs
argument_list|,
name|termState
argument_list|)
return|;
block|}
else|else
block|{
name|PulsingDocsEnum
name|docsEnum
init|=
operator|new
name|PulsingDocsEnum
argument_list|()
decl_stmt|;
return|return
name|docsEnum
operator|.
name|reset
argument_list|(
name|skipDocs
argument_list|,
name|termState
argument_list|)
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|reuse
operator|instanceof
name|PulsingDocsEnum
condition|)
block|{
return|return
name|wrappedPostingsReader
operator|.
name|docs
argument_list|(
name|field
argument_list|,
name|termState
operator|.
name|wrappedTermState
argument_list|,
name|skipDocs
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|wrappedPostingsReader
operator|.
name|docs
argument_list|(
name|field
argument_list|,
name|termState
operator|.
name|wrappedTermState
argument_list|,
name|skipDocs
argument_list|,
name|reuse
argument_list|)
return|;
block|}
block|}
block|}
comment|// TODO: -- not great that we can't always reuse
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|FieldInfo
name|field
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
name|PulsingTermState
name|termState
init|=
operator|(
name|PulsingTermState
operator|)
name|_termState
decl_stmt|;
if|if
condition|(
name|termState
operator|.
name|docFreq
operator|<=
name|maxPulsingDocFreq
condition|)
block|{
if|if
condition|(
name|reuse
operator|instanceof
name|PulsingDocsAndPositionsEnum
condition|)
block|{
return|return
operator|(
operator|(
name|PulsingDocsAndPositionsEnum
operator|)
name|reuse
operator|)
operator|.
name|reset
argument_list|(
name|skipDocs
argument_list|,
name|termState
argument_list|)
return|;
block|}
else|else
block|{
name|PulsingDocsAndPositionsEnum
name|postingsEnum
init|=
operator|new
name|PulsingDocsAndPositionsEnum
argument_list|()
decl_stmt|;
return|return
name|postingsEnum
operator|.
name|reset
argument_list|(
name|skipDocs
argument_list|,
name|termState
argument_list|)
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|reuse
operator|instanceof
name|PulsingDocsAndPositionsEnum
condition|)
block|{
return|return
name|wrappedPostingsReader
operator|.
name|docsAndPositions
argument_list|(
name|field
argument_list|,
name|termState
operator|.
name|wrappedTermState
argument_list|,
name|skipDocs
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|wrappedPostingsReader
operator|.
name|docsAndPositions
argument_list|(
name|field
argument_list|,
name|termState
operator|.
name|wrappedTermState
argument_list|,
name|skipDocs
argument_list|,
name|reuse
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|PulsingDocsEnum
specifier|static
class|class
name|PulsingDocsEnum
extends|extends
name|DocsEnum
block|{
DECL|field|nextRead
specifier|private
name|int
name|nextRead
decl_stmt|;
DECL|field|skipDocs
specifier|private
name|Bits
name|skipDocs
decl_stmt|;
DECL|field|doc
specifier|private
name|Document
name|doc
decl_stmt|;
DECL|field|state
specifier|private
name|PulsingTermState
name|state
decl_stmt|;
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
DECL|method|reset
name|PulsingDocsEnum
name|reset
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|PulsingTermState
name|termState
parameter_list|)
block|{
comment|// TODO: -- not great we have to clone here --
comment|// merging is wasteful; TermRangeQuery too
name|state
operator|=
operator|(
name|PulsingTermState
operator|)
name|termState
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|skipDocs
operator|=
name|skipDocs
expr_stmt|;
name|nextRead
operator|=
literal|0
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
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|nextRead
operator|>=
name|state
operator|.
name|docFreq
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|doc
operator|=
name|state
operator|.
name|docs
index|[
name|nextRead
operator|++
index|]
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
operator|.
name|docID
argument_list|)
condition|)
block|{
return|return
name|doc
operator|.
name|docID
return|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|// TODO: -- ob1?
name|initBulkResult
argument_list|()
expr_stmt|;
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
while|while
condition|(
name|nextRead
operator|<
name|state
operator|.
name|docFreq
condition|)
block|{
name|doc
operator|=
name|state
operator|.
name|docs
index|[
name|nextRead
operator|++
index|]
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
operator|.
name|docID
argument_list|)
condition|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|doc
operator|.
name|docID
expr_stmt|;
name|freqs
index|[
name|i
index|]
operator|=
name|doc
operator|.
name|numPositions
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
name|doc
operator|.
name|numPositions
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
operator|.
name|docID
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
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|doc
operator|>=
name|target
condition|)
return|return
name|doc
return|;
block|}
return|return
name|NO_MORE_DOCS
return|;
block|}
block|}
DECL|class|PulsingDocsAndPositionsEnum
specifier|static
class|class
name|PulsingDocsAndPositionsEnum
extends|extends
name|DocsAndPositionsEnum
block|{
DECL|field|nextRead
specifier|private
name|int
name|nextRead
decl_stmt|;
DECL|field|nextPosRead
specifier|private
name|int
name|nextPosRead
decl_stmt|;
DECL|field|skipDocs
specifier|private
name|Bits
name|skipDocs
decl_stmt|;
DECL|field|doc
specifier|private
name|Document
name|doc
decl_stmt|;
DECL|field|pos
specifier|private
name|Position
name|pos
decl_stmt|;
DECL|field|state
specifier|private
name|PulsingTermState
name|state
decl_stmt|;
comment|// Only here to emulate limitation of standard codec,
comment|// which only allows retrieving payload more than once
DECL|field|payloadRetrieved
specifier|private
name|boolean
name|payloadRetrieved
decl_stmt|;
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
DECL|method|reset
name|PulsingDocsAndPositionsEnum
name|reset
parameter_list|(
name|Bits
name|skipDocs
parameter_list|,
name|PulsingTermState
name|termState
parameter_list|)
block|{
comment|// TODO: -- not great we have to clone here --
comment|// merging is wasteful; TermRangeQuery too
name|state
operator|=
operator|(
name|PulsingTermState
operator|)
name|termState
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|skipDocs
operator|=
name|skipDocs
expr_stmt|;
name|nextRead
operator|=
literal|0
expr_stmt|;
name|nextPosRead
operator|=
literal|0
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
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|nextRead
operator|>=
name|state
operator|.
name|docFreq
condition|)
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|doc
operator|=
name|state
operator|.
name|docs
index|[
name|nextRead
operator|++
index|]
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
operator|.
name|docID
argument_list|)
condition|)
block|{
name|nextPosRead
operator|=
literal|0
expr_stmt|;
return|return
name|doc
operator|.
name|docID
return|;
block|}
block|}
block|}
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
name|doc
operator|.
name|numPositions
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
operator|.
name|docID
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
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|nextDoc
argument_list|()
operator|)
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|doc
operator|>=
name|target
condition|)
block|{
return|return
name|doc
return|;
block|}
block|}
return|return
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
block|{
assert|assert
name|nextPosRead
operator|<
name|doc
operator|.
name|numPositions
assert|;
name|pos
operator|=
name|doc
operator|.
name|positions
index|[
name|nextPosRead
operator|++
index|]
expr_stmt|;
name|payloadRetrieved
operator|=
literal|false
expr_stmt|;
return|return
name|pos
operator|.
name|pos
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
operator|!
name|payloadRetrieved
operator|&&
name|pos
operator|.
name|payload
operator|!=
literal|null
operator|&&
name|pos
operator|.
name|payload
operator|.
name|length
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
block|{
name|payloadRetrieved
operator|=
literal|true
expr_stmt|;
return|return
name|pos
operator|.
name|payload
return|;
block|}
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
name|wrappedPostingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
