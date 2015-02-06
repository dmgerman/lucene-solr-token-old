begin_unit
begin_package
DECL|package|org.apache.lucene.search.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|payloads
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
name|LeafReaderContext
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
name|PostingsEnum
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
name|Term
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
name|ComplexExplanation
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
name|Explanation
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
name|IndexSearcher
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
name|Scorer
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
name|Weight
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
name|DefaultSimilarity
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
operator|.
name|SimScorer
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
name|spans
operator|.
name|SpanQuery
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
name|spans
operator|.
name|SpanScorer
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
name|spans
operator|.
name|SpanTermQuery
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
name|spans
operator|.
name|SpanWeight
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
name|spans
operator|.
name|TermSpans
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
begin_comment
comment|/**  * This class is very similar to  * {@link org.apache.lucene.search.spans.SpanTermQuery} except that it factors  * in the value of the payload located at each of the positions where the  * {@link org.apache.lucene.index.Term} occurs.  *<p/>  * NOTE: In order to take advantage of this with the default scoring implementation  * ({@link DefaultSimilarity}), you must override {@link DefaultSimilarity#scorePayload(int, int, int, BytesRef)},  * which returns 1 by default.  *<p/>  * Payload scores are aggregated using a pluggable {@link PayloadFunction}.  * @see org.apache.lucene.search.similarities.Similarity.SimScorer#computePayloadFactor(int, int, int, BytesRef)  **/
end_comment
begin_class
DECL|class|PayloadTermQuery
specifier|public
class|class
name|PayloadTermQuery
extends|extends
name|SpanTermQuery
block|{
DECL|field|function
specifier|protected
name|PayloadFunction
name|function
decl_stmt|;
DECL|field|includeSpanScore
specifier|private
name|boolean
name|includeSpanScore
decl_stmt|;
DECL|method|PayloadTermQuery
specifier|public
name|PayloadTermQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|PayloadFunction
name|function
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
name|function
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|PayloadTermQuery
specifier|public
name|PayloadTermQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|PayloadFunction
name|function
parameter_list|,
name|boolean
name|includeSpanScore
parameter_list|)
block|{
name|super
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|this
operator|.
name|function
operator|=
name|function
expr_stmt|;
name|this
operator|.
name|includeSpanScore
operator|=
name|includeSpanScore
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|PayloadTermWeight
argument_list|(
name|this
argument_list|,
name|searcher
argument_list|)
return|;
block|}
DECL|class|PayloadTermWeight
specifier|protected
class|class
name|PayloadTermWeight
extends|extends
name|SpanWeight
block|{
DECL|method|PayloadTermWeight
specifier|public
name|PayloadTermWeight
parameter_list|(
name|PayloadTermQuery
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|PayloadTermSpanScorer
argument_list|(
operator|(
name|TermSpans
operator|)
name|query
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|termContexts
argument_list|)
argument_list|,
name|this
argument_list|,
name|similarity
operator|.
name|simScorer
argument_list|(
name|stats
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
DECL|class|PayloadTermSpanScorer
specifier|protected
class|class
name|PayloadTermSpanScorer
extends|extends
name|SpanScorer
block|{
DECL|field|payload
specifier|protected
name|BytesRef
name|payload
decl_stmt|;
DECL|field|payloadScore
specifier|protected
name|float
name|payloadScore
decl_stmt|;
DECL|field|payloadsSeen
specifier|protected
name|int
name|payloadsSeen
decl_stmt|;
DECL|field|termSpans
specifier|private
specifier|final
name|TermSpans
name|termSpans
decl_stmt|;
DECL|method|PayloadTermSpanScorer
specifier|public
name|PayloadTermSpanScorer
parameter_list|(
name|TermSpans
name|spans
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|Similarity
operator|.
name|SimScorer
name|docScorer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|spans
argument_list|,
name|weight
argument_list|,
name|docScorer
argument_list|)
expr_stmt|;
name|termSpans
operator|=
name|spans
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setFreqCurrentDoc
specifier|protected
name|boolean
name|setFreqCurrentDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|more
condition|)
block|{
return|return
literal|false
return|;
block|}
name|doc
operator|=
name|spans
operator|.
name|doc
argument_list|()
expr_stmt|;
name|freq
operator|=
literal|0.0f
expr_stmt|;
name|numMatches
operator|=
literal|0
expr_stmt|;
name|payloadScore
operator|=
literal|0
expr_stmt|;
name|payloadsSeen
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|more
operator|&&
name|doc
operator|==
name|spans
operator|.
name|doc
argument_list|()
condition|)
block|{
name|int
name|matchLength
init|=
name|spans
operator|.
name|end
argument_list|()
operator|-
name|spans
operator|.
name|start
argument_list|()
decl_stmt|;
name|freq
operator|+=
name|docScorer
operator|.
name|computeSlopFactor
argument_list|(
name|matchLength
argument_list|)
expr_stmt|;
name|numMatches
operator|++
expr_stmt|;
name|processPayload
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
name|more
operator|=
name|spans
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// this moves positions to the next match in this
comment|// document
block|}
return|return
name|more
operator|||
operator|(
name|freq
operator|!=
literal|0
operator|)
return|;
block|}
DECL|method|processPayload
specifier|protected
name|void
name|processPayload
parameter_list|(
name|Similarity
name|similarity
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|termSpans
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
specifier|final
name|PostingsEnum
name|postings
init|=
name|termSpans
operator|.
name|getPostings
argument_list|()
decl_stmt|;
name|payload
operator|=
name|postings
operator|.
name|getPayload
argument_list|()
expr_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|payloadScore
operator|=
name|function
operator|.
name|currentScore
argument_list|(
name|doc
argument_list|,
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|spans
operator|.
name|start
argument_list|()
argument_list|,
name|spans
operator|.
name|end
argument_list|()
argument_list|,
name|payloadsSeen
argument_list|,
name|payloadScore
argument_list|,
name|docScorer
operator|.
name|computePayloadFactor
argument_list|(
name|doc
argument_list|,
name|spans
operator|.
name|start
argument_list|()
argument_list|,
name|spans
operator|.
name|end
argument_list|()
argument_list|,
name|payload
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|payloadScore
operator|=
name|function
operator|.
name|currentScore
argument_list|(
name|doc
argument_list|,
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|spans
operator|.
name|start
argument_list|()
argument_list|,
name|spans
operator|.
name|end
argument_list|()
argument_list|,
name|payloadsSeen
argument_list|,
name|payloadScore
argument_list|,
literal|1F
argument_list|)
expr_stmt|;
block|}
name|payloadsSeen
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// zero out the payload?
block|}
block|}
comment|/**        *         * @return {@link #getSpanScore()} * {@link #getPayloadScore()}        * @throws IOException if there is a low-level I/O error        */
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|includeSpanScore
condition|?
name|getSpanScore
argument_list|()
operator|*
name|getPayloadScore
argument_list|()
else|:
name|getPayloadScore
argument_list|()
return|;
block|}
comment|/**        * Returns the SpanScorer score only.        *<p/>        * Should not be overridden without good cause!        *         * @return the score for just the Span part w/o the payload        * @throws IOException if there is a low-level I/O error        *         * @see #score()        */
DECL|method|getSpanScore
specifier|protected
name|float
name|getSpanScore
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|score
argument_list|()
return|;
block|}
comment|/**        * The score for the payload        *         * @return The score, as calculated by        *         {@link PayloadFunction#docScore(int, String, int, float)}        */
DECL|method|getPayloadScore
specifier|protected
name|float
name|getPayloadScore
parameter_list|()
block|{
return|return
name|function
operator|.
name|docScore
argument_list|(
name|doc
argument_list|,
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|payloadsSeen
argument_list|,
name|payloadScore
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|PayloadTermSpanScorer
name|scorer
init|=
operator|(
name|PayloadTermSpanScorer
operator|)
name|scorer
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
name|int
name|newDoc
init|=
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDoc
operator|==
name|doc
condition|)
block|{
name|float
name|freq
init|=
name|scorer
operator|.
name|sloppyFreq
argument_list|()
decl_stmt|;
name|SimScorer
name|docScorer
init|=
name|similarity
operator|.
name|simScorer
argument_list|(
name|stats
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|Explanation
name|expl
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|expl
operator|.
name|setDescription
argument_list|(
literal|"weight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|") ["
operator|+
name|similarity
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"], result of:"
argument_list|)
expr_stmt|;
name|Explanation
name|scoreExplanation
init|=
name|docScorer
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
operator|new
name|Explanation
argument_list|(
name|freq
argument_list|,
literal|"phraseFreq="
operator|+
name|freq
argument_list|)
argument_list|)
decl_stmt|;
name|expl
operator|.
name|addDetail
argument_list|(
name|scoreExplanation
argument_list|)
expr_stmt|;
name|expl
operator|.
name|setValue
argument_list|(
name|scoreExplanation
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// now the payloads part
comment|// QUESTION: Is there a way to avoid this skipTo call? We need to know
comment|// whether to load the payload or not
comment|// GSI: I suppose we could toString the payload, but I don't think that
comment|// would be a good idea
name|String
name|field
init|=
operator|(
operator|(
name|SpanQuery
operator|)
name|getQuery
argument_list|()
operator|)
operator|.
name|getField
argument_list|()
decl_stmt|;
name|Explanation
name|payloadExpl
init|=
name|function
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
name|field
argument_list|,
name|scorer
operator|.
name|payloadsSeen
argument_list|,
name|scorer
operator|.
name|payloadScore
argument_list|)
decl_stmt|;
name|payloadExpl
operator|.
name|setValue
argument_list|(
name|scorer
operator|.
name|getPayloadScore
argument_list|()
argument_list|)
expr_stmt|;
comment|// combined
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
if|if
condition|(
name|includeSpanScore
condition|)
block|{
name|result
operator|.
name|addDetail
argument_list|(
name|expl
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|payloadExpl
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|expl
operator|.
name|getValue
argument_list|()
operator|*
name|payloadExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"btq, product of:"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|addDetail
argument_list|(
name|payloadExpl
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|payloadExpl
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"btq(includeSpanScore=false), result of:"
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|setMatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// LUCENE-1303
return|return
name|result
return|;
block|}
block|}
return|return
operator|new
name|ComplexExplanation
argument_list|(
literal|false
argument_list|,
literal|0.0f
argument_list|,
literal|"no matching term"
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|function
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|function
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|includeSpanScore
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|PayloadTermQuery
name|other
init|=
operator|(
name|PayloadTermQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|function
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|function
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|function
operator|.
name|equals
argument_list|(
name|other
operator|.
name|function
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|includeSpanScore
operator|!=
name|other
operator|.
name|includeSpanScore
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
