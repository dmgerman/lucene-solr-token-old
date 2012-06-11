begin_unit
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|MergeState
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
name|search
operator|.
name|DocIdSetIterator
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
name|FixedBitSet
import|;
end_import
begin_comment
comment|/**  * Abstract API that consumes postings for an individual term.  *<p>  * The lifecycle is:  *<ol>  *<li>PostingsConsumer is returned for each term by  *        {@link TermsConsumer#startTerm(BytesRef)}.   *<li>{@link #startDoc(int, int)} is called for each  *        document where the term occurs, specifying id   *        and term frequency for that document.  *<li>If positions are enabled for the field, then  *        {@link #addPosition(int, BytesRef, int, int)}  *        will be called for each occurrence in the   *        document.  *<li>{@link #finishDoc()} is called when the producer  *        is done adding positions to the document.  *</ol>  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|PostingsConsumer
specifier|public
specifier|abstract
class|class
name|PostingsConsumer
block|{
comment|/** Adds a new doc in this term. */
DECL|method|startDoc
specifier|public
specifier|abstract
name|void
name|startDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|freq
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Add a new position& payload, and start/end offset.  A    *  null payload means no payload; a non-null payload with    *  zero length also means no payload.  Caller may reuse    *  the {@link BytesRef} for the payload between calls    *  (method must fully consume the payload). */
DECL|method|addPosition
specifier|public
specifier|abstract
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
function_decl|;
comment|/** Called when we are done adding positions& payloads    *  for each doc. */
DECL|method|finishDoc
specifier|public
specifier|abstract
name|void
name|finishDoc
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Default merge impl: append documents, mapping around    *  deletes */
DECL|method|merge
specifier|public
name|TermStats
name|merge
parameter_list|(
specifier|final
name|MergeState
name|mergeState
parameter_list|,
specifier|final
name|DocsEnum
name|postings
parameter_list|,
specifier|final
name|FixedBitSet
name|visitedDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|df
init|=
literal|0
decl_stmt|;
name|long
name|totTF
init|=
literal|0
decl_stmt|;
name|IndexOptions
name|indexOptions
init|=
name|mergeState
operator|.
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|doc
init|=
name|postings
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|visitedDocs
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|this
operator|.
name|startDoc
argument_list|(
name|doc
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
name|df
operator|++
expr_stmt|;
block|}
name|totTF
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
condition|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|doc
init|=
name|postings
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|visitedDocs
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|int
name|freq
init|=
name|postings
operator|.
name|freq
argument_list|()
decl_stmt|;
name|this
operator|.
name|startDoc
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|this
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
name|df
operator|++
expr_stmt|;
name|totTF
operator|+=
name|freq
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
condition|)
block|{
specifier|final
name|DocsAndPositionsEnum
name|postingsEnum
init|=
operator|(
name|DocsAndPositionsEnum
operator|)
name|postings
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|doc
init|=
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|visitedDocs
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|int
name|freq
init|=
name|postingsEnum
operator|.
name|freq
argument_list|()
decl_stmt|;
name|this
operator|.
name|startDoc
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|totTF
operator|+=
name|freq
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
name|freq
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|position
init|=
name|postingsEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|payload
decl_stmt|;
if|if
condition|(
name|postingsEnum
operator|.
name|hasPayload
argument_list|()
condition|)
block|{
name|payload
operator|=
name|postingsEnum
operator|.
name|getPayload
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|payload
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|addPosition
argument_list|(
name|position
argument_list|,
name|payload
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
name|df
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
assert|assert
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
assert|;
specifier|final
name|DocsAndPositionsEnum
name|postingsEnum
init|=
operator|(
name|DocsAndPositionsEnum
operator|)
name|postings
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|doc
init|=
name|postingsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|visitedDocs
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|int
name|freq
init|=
name|postingsEnum
operator|.
name|freq
argument_list|()
decl_stmt|;
name|this
operator|.
name|startDoc
argument_list|(
name|doc
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|totTF
operator|+=
name|freq
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
name|freq
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|position
init|=
name|postingsEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|payload
decl_stmt|;
if|if
condition|(
name|postingsEnum
operator|.
name|hasPayload
argument_list|()
condition|)
block|{
name|payload
operator|=
name|postingsEnum
operator|.
name|getPayload
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|payload
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|addPosition
argument_list|(
name|position
argument_list|,
name|payload
argument_list|,
name|postingsEnum
operator|.
name|startOffset
argument_list|()
argument_list|,
name|postingsEnum
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
name|df
operator|++
expr_stmt|;
block|}
block|}
return|return
operator|new
name|TermStats
argument_list|(
name|df
argument_list|,
name|totTF
argument_list|)
return|;
block|}
block|}
end_class
end_unit
