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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|util
operator|.
name|BytesRef
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
begin_comment
comment|/**  * This class is very similar to  * {@link org.apache.lucene.search.spans.SpanTermQuery} except that it factors  * in the value of the payload located at each of the positions where the  * {@link org.apache.lucene.index.Term} occurs.  *<p>  * In order to take advantage of this, you must override  * {@link org.apache.lucene.search.Similarity#scorePayload(int, String, int, int, byte[],int,int)}  * which returns 1 by default.  *<p>  * Payload scores are aggregated using a pluggable {@link PayloadFunction}.  **/
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
name|AtomicReaderContext
name|context
parameter_list|,
name|ScorerContext
name|scorerContext
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
operator|.
name|reader
argument_list|)
argument_list|,
name|this
argument_list|,
name|similarity
argument_list|,
name|context
operator|.
name|reader
operator|.
name|norms
argument_list|(
name|query
operator|.
name|getField
argument_list|()
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
name|similarity
parameter_list|,
name|byte
index|[]
name|norms
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
name|similarity
argument_list|,
name|norms
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
name|similarity
operator|.
name|sloppyFreq
argument_list|(
name|matchLength
argument_list|)
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
specifier|final
name|DocsAndPositionsEnum
name|postings
init|=
name|termSpans
operator|.
name|getPostings
argument_list|()
decl_stmt|;
if|if
condition|(
name|postings
operator|.
name|hasPayload
argument_list|()
condition|)
block|{
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
name|similarity
operator|.
name|scorePayload
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
name|similarity
operator|.
name|scorePayload
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
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
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
comment|/**        *         * @return {@link #getSpanScore()} * {@link #getPayloadScore()}        * @throws IOException        */
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
comment|/**        * Returns the SpanScorer score only.        *<p/>        * Should not be overridden without good cause!        *         * @return the score for just the Span part w/o the payload        * @throws IOException        *         * @see #score()        */
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
annotation|@
name|Override
DECL|method|explain
specifier|protected
name|Explanation
name|explain
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|Explanation
name|nonPayloadExpl
init|=
name|super
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|nonPayloadExpl
argument_list|)
expr_stmt|;
comment|// QUESTION: Is there a way to avoid this skipTo call? We need to know
comment|// whether to load the payload or not
name|Explanation
name|payloadBoost
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|payloadBoost
argument_list|)
expr_stmt|;
name|float
name|payloadScore
init|=
name|getPayloadScore
argument_list|()
decl_stmt|;
name|payloadBoost
operator|.
name|setValue
argument_list|(
name|payloadScore
argument_list|)
expr_stmt|;
comment|// GSI: I suppose we could toString the payload, but I don't think that
comment|// would be a good idea
name|payloadBoost
operator|.
name|setDescription
argument_list|(
literal|"scorePayload(...)"
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|nonPayloadExpl
operator|.
name|getValue
argument_list|()
operator|*
name|payloadScore
argument_list|)
expr_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"btq, product of:"
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMatch
argument_list|(
name|nonPayloadExpl
operator|.
name|getValue
argument_list|()
operator|==
literal|0
condition|?
name|Boolean
operator|.
name|FALSE
else|:
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
comment|// LUCENE-1303
return|return
name|result
return|;
block|}
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
