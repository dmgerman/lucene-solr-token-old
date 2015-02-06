begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|index
operator|.
name|*
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
name|similarities
operator|.
name|Similarity
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
begin_class
DECL|class|ExactPhraseScorer
specifier|final
class|class
name|ExactPhraseScorer
extends|extends
name|Scorer
block|{
DECL|field|endMinus1
specifier|private
specifier|final
name|int
name|endMinus1
decl_stmt|;
DECL|field|CHUNK
specifier|private
specifier|final
specifier|static
name|int
name|CHUNK
init|=
literal|4096
decl_stmt|;
DECL|field|gen
specifier|private
name|int
name|gen
decl_stmt|;
DECL|field|counts
specifier|private
specifier|final
name|int
index|[]
name|counts
init|=
operator|new
name|int
index|[
name|CHUNK
index|]
decl_stmt|;
DECL|field|gens
specifier|private
specifier|final
name|int
index|[]
name|gens
init|=
operator|new
name|int
index|[
name|CHUNK
index|]
decl_stmt|;
DECL|field|cost
specifier|private
specifier|final
name|long
name|cost
decl_stmt|;
DECL|class|ChunkState
specifier|private
specifier|final
specifier|static
class|class
name|ChunkState
block|{
DECL|field|posEnum
specifier|final
name|PostingsEnum
name|posEnum
decl_stmt|;
DECL|field|offset
specifier|final
name|int
name|offset
decl_stmt|;
DECL|field|posUpto
name|int
name|posUpto
decl_stmt|;
DECL|field|posLimit
name|int
name|posLimit
decl_stmt|;
DECL|field|pos
name|int
name|pos
decl_stmt|;
DECL|field|lastPos
name|int
name|lastPos
decl_stmt|;
DECL|method|ChunkState
specifier|public
name|ChunkState
parameter_list|(
name|PostingsEnum
name|posEnum
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|posEnum
operator|=
name|posEnum
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
block|}
DECL|field|chunkStates
specifier|private
specifier|final
name|ChunkState
index|[]
name|chunkStates
decl_stmt|;
DECL|field|lead
specifier|private
specifier|final
name|PostingsEnum
name|lead
decl_stmt|;
DECL|field|docID
specifier|private
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|freq
specifier|private
name|int
name|freq
decl_stmt|;
DECL|field|docScorer
specifier|private
specifier|final
name|Similarity
operator|.
name|SimScorer
name|docScorer
decl_stmt|;
DECL|field|needsScores
specifier|private
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|method|ExactPhraseScorer
name|ExactPhraseScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|PhraseQuery
operator|.
name|PostingsAndFreq
index|[]
name|postings
parameter_list|,
name|Similarity
operator|.
name|SimScorer
name|docScorer
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|docScorer
operator|=
name|docScorer
expr_stmt|;
name|this
operator|.
name|needsScores
operator|=
name|needsScores
expr_stmt|;
name|chunkStates
operator|=
operator|new
name|ChunkState
index|[
name|postings
operator|.
name|length
index|]
expr_stmt|;
name|endMinus1
operator|=
name|postings
operator|.
name|length
operator|-
literal|1
expr_stmt|;
name|lead
operator|=
name|postings
index|[
literal|0
index|]
operator|.
name|postings
expr_stmt|;
comment|// min(cost)
name|cost
operator|=
name|lead
operator|.
name|cost
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
name|postings
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|chunkStates
index|[
name|i
index|]
operator|=
operator|new
name|ChunkState
argument_list|(
name|postings
index|[
name|i
index|]
operator|.
name|postings
argument_list|,
operator|-
name|postings
index|[
name|i
index|]
operator|.
name|position
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doNext
specifier|private
name|int
name|doNext
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
comment|// TODO: don't dup this logic from conjunctionscorer :)
name|advanceHead
label|:
for|for
control|(
init|;
condition|;
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|chunkStates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|PostingsEnum
name|de
init|=
name|chunkStates
index|[
name|i
index|]
operator|.
name|posEnum
decl_stmt|;
if|if
condition|(
name|de
operator|.
name|docID
argument_list|()
operator|<
name|doc
condition|)
block|{
name|int
name|d
init|=
name|de
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|>
name|doc
condition|)
block|{
comment|// DocsEnum beyond the current doc - break and advance lead to the new highest doc.
name|doc
operator|=
name|d
expr_stmt|;
break|break
name|advanceHead
break|;
block|}
block|}
block|}
comment|// all DocsEnums are on the same doc
if|if
condition|(
name|doc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|doc
return|;
block|}
elseif|else
if|if
condition|(
name|phraseFreq
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|doc
return|;
comment|// success: matches phrase
block|}
else|else
block|{
name|doc
operator|=
name|lead
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
comment|// doesn't match phrase
block|}
block|}
comment|// advance head for next iteration
name|doc
operator|=
name|lead
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
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
return|return
name|docID
operator|=
name|doNext
argument_list|(
name|lead
operator|.
name|nextDoc
argument_list|()
argument_list|)
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
return|return
name|docID
operator|=
name|doNext
argument_list|(
name|lead
operator|.
name|advance
argument_list|(
name|target
argument_list|)
argument_list|)
return|;
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
literal|"ExactPhraseScorer("
operator|+
name|weight
operator|+
literal|")"
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
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|startOffset
specifier|public
name|int
name|startOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|endOffset
specifier|public
name|int
name|endOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
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
return|return
literal|null
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
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
block|{
return|return
name|docScorer
operator|.
name|score
argument_list|(
name|docID
argument_list|,
name|freq
argument_list|)
return|;
block|}
DECL|method|phraseFreq
specifier|private
name|int
name|phraseFreq
parameter_list|()
throws|throws
name|IOException
block|{
name|freq
operator|=
literal|0
expr_stmt|;
comment|// init chunks
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chunkStates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|ChunkState
name|cs
init|=
name|chunkStates
index|[
name|i
index|]
decl_stmt|;
name|cs
operator|.
name|posLimit
operator|=
name|cs
operator|.
name|posEnum
operator|.
name|freq
argument_list|()
expr_stmt|;
name|cs
operator|.
name|pos
operator|=
name|cs
operator|.
name|offset
operator|+
name|cs
operator|.
name|posEnum
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|cs
operator|.
name|posUpto
operator|=
literal|1
expr_stmt|;
name|cs
operator|.
name|lastPos
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|int
name|chunkStart
init|=
literal|0
decl_stmt|;
name|int
name|chunkEnd
init|=
name|CHUNK
decl_stmt|;
comment|// process chunk by chunk
name|boolean
name|end
init|=
literal|false
decl_stmt|;
comment|// TODO: we could fold in chunkStart into offset and
comment|// save one subtract per pos incr
while|while
condition|(
operator|!
name|end
condition|)
block|{
name|gen
operator|++
expr_stmt|;
if|if
condition|(
name|gen
operator|==
literal|0
condition|)
block|{
comment|// wraparound
name|Arrays
operator|.
name|fill
argument_list|(
name|gens
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|gen
operator|++
expr_stmt|;
block|}
comment|// first term
block|{
specifier|final
name|ChunkState
name|cs
init|=
name|chunkStates
index|[
literal|0
index|]
decl_stmt|;
while|while
condition|(
name|cs
operator|.
name|pos
operator|<
name|chunkEnd
condition|)
block|{
if|if
condition|(
name|cs
operator|.
name|pos
operator|>
name|cs
operator|.
name|lastPos
condition|)
block|{
name|cs
operator|.
name|lastPos
operator|=
name|cs
operator|.
name|pos
expr_stmt|;
specifier|final
name|int
name|posIndex
init|=
name|cs
operator|.
name|pos
operator|-
name|chunkStart
decl_stmt|;
name|counts
index|[
name|posIndex
index|]
operator|=
literal|1
expr_stmt|;
assert|assert
name|gens
index|[
name|posIndex
index|]
operator|!=
name|gen
assert|;
name|gens
index|[
name|posIndex
index|]
operator|=
name|gen
expr_stmt|;
block|}
if|if
condition|(
name|cs
operator|.
name|posUpto
operator|==
name|cs
operator|.
name|posLimit
condition|)
block|{
name|end
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|cs
operator|.
name|posUpto
operator|++
expr_stmt|;
name|cs
operator|.
name|pos
operator|=
name|cs
operator|.
name|offset
operator|+
name|cs
operator|.
name|posEnum
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
block|}
block|}
comment|// middle terms
name|boolean
name|any
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|1
init|;
name|t
operator|<
name|endMinus1
condition|;
name|t
operator|++
control|)
block|{
specifier|final
name|ChunkState
name|cs
init|=
name|chunkStates
index|[
name|t
index|]
decl_stmt|;
name|any
operator|=
literal|false
expr_stmt|;
while|while
condition|(
name|cs
operator|.
name|pos
operator|<
name|chunkEnd
condition|)
block|{
if|if
condition|(
name|cs
operator|.
name|pos
operator|>
name|cs
operator|.
name|lastPos
condition|)
block|{
name|cs
operator|.
name|lastPos
operator|=
name|cs
operator|.
name|pos
expr_stmt|;
specifier|final
name|int
name|posIndex
init|=
name|cs
operator|.
name|pos
operator|-
name|chunkStart
decl_stmt|;
if|if
condition|(
name|posIndex
operator|>=
literal|0
operator|&&
name|gens
index|[
name|posIndex
index|]
operator|==
name|gen
operator|&&
name|counts
index|[
name|posIndex
index|]
operator|==
name|t
condition|)
block|{
comment|// viable
name|counts
index|[
name|posIndex
index|]
operator|++
expr_stmt|;
name|any
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|cs
operator|.
name|posUpto
operator|==
name|cs
operator|.
name|posLimit
condition|)
block|{
name|end
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|cs
operator|.
name|posUpto
operator|++
expr_stmt|;
name|cs
operator|.
name|pos
operator|=
name|cs
operator|.
name|offset
operator|+
name|cs
operator|.
name|posEnum
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|any
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|any
condition|)
block|{
comment|// petered out for this chunk
name|chunkStart
operator|+=
name|CHUNK
expr_stmt|;
name|chunkEnd
operator|+=
name|CHUNK
expr_stmt|;
continue|continue;
block|}
comment|// last term
block|{
specifier|final
name|ChunkState
name|cs
init|=
name|chunkStates
index|[
name|endMinus1
index|]
decl_stmt|;
while|while
condition|(
name|cs
operator|.
name|pos
operator|<
name|chunkEnd
condition|)
block|{
if|if
condition|(
name|cs
operator|.
name|pos
operator|>
name|cs
operator|.
name|lastPos
condition|)
block|{
name|cs
operator|.
name|lastPos
operator|=
name|cs
operator|.
name|pos
expr_stmt|;
specifier|final
name|int
name|posIndex
init|=
name|cs
operator|.
name|pos
operator|-
name|chunkStart
decl_stmt|;
if|if
condition|(
name|posIndex
operator|>=
literal|0
operator|&&
name|gens
index|[
name|posIndex
index|]
operator|==
name|gen
operator|&&
name|counts
index|[
name|posIndex
index|]
operator|==
name|endMinus1
condition|)
block|{
name|freq
operator|++
expr_stmt|;
if|if
condition|(
operator|!
name|needsScores
condition|)
block|{
return|return
name|freq
return|;
comment|// we determined there was a match.
block|}
block|}
block|}
if|if
condition|(
name|cs
operator|.
name|posUpto
operator|==
name|cs
operator|.
name|posLimit
condition|)
block|{
name|end
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|cs
operator|.
name|posUpto
operator|++
expr_stmt|;
name|cs
operator|.
name|pos
operator|=
name|cs
operator|.
name|offset
operator|+
name|cs
operator|.
name|posEnum
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
block|}
block|}
name|chunkStart
operator|+=
name|CHUNK
expr_stmt|;
name|chunkEnd
operator|+=
name|CHUNK
expr_stmt|;
block|}
return|return
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
block|}
end_class
end_unit
