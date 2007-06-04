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
name|TermPositions
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *<p/>  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * The BoostingTermQuery is very similar to the {@link org.apache.lucene.search.spans.SpanTermQuery} except  * that it factors in the value of the payload located at each of the positions where the  * {@link org.apache.lucene.index.Term} occurs.  *<p>  * In order to take advantage of this, you must override {@link org.apache.lucene.search.Similarity#scorePayload(byte[],int,int)}  * which returns 1 by default.  *<p>  * Payload scores are averaged across term occurrences in the document.    *   *  * @see org.apache.lucene.search.Similarity#scorePayload(byte[], int, int)  */
end_comment
begin_class
DECL|class|BoostingTermQuery
specifier|public
class|class
name|BoostingTermQuery
extends|extends
name|SpanTermQuery
block|{
DECL|method|BoostingTermQuery
specifier|public
name|BoostingTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|super
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|createWeight
specifier|protected
name|Weight
name|createWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BoostingTermWeight
argument_list|(
name|this
argument_list|,
name|searcher
argument_list|)
return|;
block|}
DECL|class|BoostingTermWeight
specifier|protected
class|class
name|BoostingTermWeight
extends|extends
name|SpanWeight
implements|implements
name|Weight
block|{
DECL|method|BoostingTermWeight
specifier|public
name|BoostingTermWeight
parameter_list|(
name|BoostingTermQuery
name|query
parameter_list|,
name|Searcher
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
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BoostingSpanScorer
argument_list|(
operator|(
name|TermSpans
operator|)
name|query
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
argument_list|,
name|this
argument_list|,
name|similarity
argument_list|,
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
DECL|class|BoostingSpanScorer
class|class
name|BoostingSpanScorer
extends|extends
name|SpanScorer
block|{
comment|//TODO: is this the best way to allocate this?
DECL|field|payload
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
DECL|field|positions
specifier|private
name|TermPositions
name|positions
decl_stmt|;
DECL|field|payloadScore
specifier|protected
name|float
name|payloadScore
decl_stmt|;
DECL|field|payloadsSeen
specifier|private
name|int
name|payloadsSeen
decl_stmt|;
DECL|method|BoostingSpanScorer
specifier|public
name|BoostingSpanScorer
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
name|positions
operator|=
name|spans
operator|.
name|getPositions
argument_list|()
expr_stmt|;
block|}
comment|/**        * Go to the next document        *         */
comment|/*public boolean next() throws IOException {          boolean result = super.next();         //set the payload.  super.next() properly increments the term positions         if (result) {           //Load the payloads for all            processPayload();         }          return result;       }        public boolean skipTo(int target) throws IOException {         boolean result = super.skipTo(target);          if (result) {           processPayload();         }          return result;       }*/
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
name|Similarity
name|similarity1
init|=
name|getSimilarity
argument_list|()
decl_stmt|;
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
name|similarity1
operator|.
name|sloppyFreq
argument_list|(
name|matchLength
argument_list|)
expr_stmt|;
name|processPayload
argument_list|(
name|similarity1
argument_list|)
expr_stmt|;
name|more
operator|=
name|spans
operator|.
name|next
argument_list|()
expr_stmt|;
comment|//this moves positions to the next match in this document
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
name|positions
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|payload
operator|=
name|positions
operator|.
name|getPayload
argument_list|(
name|payload
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|payloadScore
operator|+=
name|similarity
operator|.
name|scorePayload
argument_list|(
name|payload
argument_list|,
literal|0
argument_list|,
name|positions
operator|.
name|getPayloadLength
argument_list|()
argument_list|)
expr_stmt|;
name|payloadsSeen
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|//zero out the payload?
block|}
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|score
argument_list|()
operator|*
operator|(
name|payloadsSeen
operator|>
literal|0
condition|?
operator|(
name|payloadScore
operator|/
name|payloadsSeen
operator|)
else|:
literal|1
operator|)
return|;
block|}
DECL|method|explain
specifier|public
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
name|Explanation
name|result
init|=
operator|new
name|Explanation
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
comment|//QUESTION: Is there a wau to avoid this skipTo call?  We need to know whether to load the payload or not
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
comment|/*         if (skipTo(doc) == true) {           processPayload();         } */
name|float
name|avgPayloadScore
init|=
name|payloadScore
operator|/
name|payloadsSeen
decl_stmt|;
name|payloadBoost
operator|.
name|setValue
argument_list|(
name|avgPayloadScore
argument_list|)
expr_stmt|;
comment|//GSI: I suppose we could toString the payload, but I don't think that would be a good idea
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
name|avgPayloadScore
argument_list|)
expr_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
literal|"btq, product of:"
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|BoostingTermQuery
operator|)
condition|)
return|return
literal|false
return|;
name|BoostingTermQuery
name|other
init|=
operator|(
name|BoostingTermQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|)
operator|&&
name|this
operator|.
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
argument_list|)
return|;
block|}
block|}
end_class
end_unit
