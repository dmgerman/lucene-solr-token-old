begin_unit
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
begin_comment
comment|/** Iterates through combinations of start/end positions per-doc.  *  Each start/end position represents a range of term positions within the current document.  *  These are enumerated in order, by increasing document number, within that by  *  increasing start position and finally by increasing end position.  */
end_comment
begin_class
DECL|class|Spans
specifier|public
specifier|abstract
class|class
name|Spans
extends|extends
name|Scorer
block|{
DECL|field|NO_MORE_POSITIONS
specifier|public
specifier|static
specifier|final
name|int
name|NO_MORE_POSITIONS
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|docScorer
specifier|protected
specifier|final
name|Similarity
operator|.
name|SimScorer
name|docScorer
decl_stmt|;
DECL|method|Spans
specifier|protected
name|Spans
parameter_list|(
name|SpanWeight
name|weight
parameter_list|,
name|SimScorer
name|docScorer
parameter_list|)
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
block|}
comment|/** accumulated sloppy freq (computed in setFreqCurrentDoc) */
DECL|field|freq
specifier|protected
name|float
name|freq
decl_stmt|;
comment|/** number of matches (computed in setFreqCurrentDoc) */
DECL|field|numMatches
specifier|protected
name|int
name|numMatches
decl_stmt|;
DECL|field|lastScoredDoc
specifier|private
name|int
name|lastScoredDoc
init|=
operator|-
literal|1
decl_stmt|;
comment|// last doc we called setFreqCurrentDoc() for
comment|/**    * Returns the next start position for the current doc.    * There is always at least one start/end position per doc.    * After the last start/end position at the current doc this returns {@link #NO_MORE_POSITIONS}.    */
DECL|method|nextStartPosition
specifier|public
specifier|abstract
name|int
name|nextStartPosition
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the start position in the current doc, or -1 when {@link #nextStartPosition} was not yet called on the current doc.    * After the last start/end position at the current doc this returns {@link #NO_MORE_POSITIONS}.    */
DECL|method|startPosition
specifier|public
specifier|abstract
name|int
name|startPosition
parameter_list|()
function_decl|;
comment|/**    * Returns the end position for the current start position, or -1 when {@link #nextStartPosition} was not yet called on the current doc.    * After the last start/end position at the current doc this returns {@link #NO_MORE_POSITIONS}.    */
DECL|method|endPosition
specifier|public
specifier|abstract
name|int
name|endPosition
parameter_list|()
function_decl|;
comment|/**    * Return the width of the match, which is typically used to compute    * the {@link SimScorer#computeSlopFactor(int) slop factor}. It is only legal    * to call this method when the iterator is on a valid doc ID and positioned.    * The return value must be positive, and lower values means that the match is    * better.    */
DECL|method|width
specifier|public
specifier|abstract
name|int
name|width
parameter_list|()
function_decl|;
comment|/**    * Collect postings data from the leaves of the current Spans.    *    * This method should only be called after {@link #nextStartPosition()}, and before    * {@link #NO_MORE_POSITIONS} has been reached.    *    * @param collector a SpanCollector    *    * @lucene.experimental    */
DECL|method|collect
specifier|public
specifier|abstract
name|void
name|collect
parameter_list|(
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return an estimation of the cost of using the positions of    * this {@link Spans} for any single document, but only after    * {@link #asTwoPhaseIterator} returned {@code null}.    * Otherwise this method should not be called.    * The returned value is independent of the current document.    *    * @lucene.experimental    */
DECL|method|positionsCost
specifier|public
specifier|abstract
name|float
name|positionsCost
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|Spans
argument_list|>
name|clazz
init|=
name|getClass
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|clazz
operator|.
name|isAnonymousClass
argument_list|()
condition|?
name|clazz
operator|.
name|getName
argument_list|()
else|:
name|clazz
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"(doc="
argument_list|)
operator|.
name|append
argument_list|(
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",start="
argument_list|)
operator|.
name|append
argument_list|(
name|startPosition
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",end="
argument_list|)
operator|.
name|append
argument_list|(
name|endPosition
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Ensure setFreqCurrentDoc is called, if not already called for the current doc.    */
DECL|method|ensureFreq
specifier|private
name|void
name|ensureFreq
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|currentDoc
init|=
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastScoredDoc
operator|!=
name|currentDoc
condition|)
block|{
name|setFreqCurrentDoc
argument_list|()
expr_stmt|;
name|lastScoredDoc
operator|=
name|currentDoc
expr_stmt|;
block|}
block|}
comment|/**    * Sets {@link #freq} and {@link #numMatches} for the current document.    *<p>    * This will be called at most once per document.    */
DECL|method|setFreqCurrentDoc
specifier|protected
specifier|final
name|void
name|setFreqCurrentDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|freq
operator|=
literal|0.0f
expr_stmt|;
name|numMatches
operator|=
literal|0
expr_stmt|;
name|doStartCurrentDoc
argument_list|()
expr_stmt|;
assert|assert
name|startPosition
argument_list|()
operator|==
operator|-
literal|1
operator|:
literal|"incorrect initial start position, "
operator|+
name|this
operator|.
name|toString
argument_list|()
assert|;
assert|assert
name|endPosition
argument_list|()
operator|==
operator|-
literal|1
operator|:
literal|"incorrect initial end position, "
operator|+
name|this
operator|.
name|toString
argument_list|()
assert|;
name|int
name|prevStartPos
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|prevEndPos
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|startPos
init|=
name|nextStartPosition
argument_list|()
decl_stmt|;
assert|assert
name|startPos
operator|!=
name|Spans
operator|.
name|NO_MORE_POSITIONS
operator|:
literal|"initial startPos NO_MORE_POSITIONS, "
operator|+
name|this
operator|.
name|toString
argument_list|()
assert|;
do|do
block|{
assert|assert
name|startPos
operator|>=
name|prevStartPos
assert|;
name|int
name|endPos
init|=
name|endPosition
argument_list|()
decl_stmt|;
assert|assert
name|endPos
operator|!=
name|Spans
operator|.
name|NO_MORE_POSITIONS
assert|;
comment|// This assertion can fail for Or spans on the same term:
comment|// assert (startPos != prevStartPos) || (endPos> prevEndPos) : "non increased endPos="+endPos;
assert|assert
operator|(
name|startPos
operator|!=
name|prevStartPos
operator|)
operator|||
operator|(
name|endPos
operator|>=
name|prevEndPos
operator|)
operator|:
literal|"decreased endPos="
operator|+
name|endPos
assert|;
name|numMatches
operator|++
expr_stmt|;
if|if
condition|(
name|docScorer
operator|==
literal|null
condition|)
block|{
comment|// scores not required, break out here
name|freq
operator|=
literal|1
expr_stmt|;
return|return;
block|}
name|freq
operator|+=
name|docScorer
operator|.
name|computeSlopFactor
argument_list|(
name|width
argument_list|()
argument_list|)
expr_stmt|;
name|doCurrentSpans
argument_list|()
expr_stmt|;
name|prevStartPos
operator|=
name|startPos
expr_stmt|;
name|prevEndPos
operator|=
name|endPos
expr_stmt|;
name|startPos
operator|=
name|nextStartPosition
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|startPos
operator|!=
name|Spans
operator|.
name|NO_MORE_POSITIONS
condition|)
do|;
assert|assert
name|startPosition
argument_list|()
operator|==
name|Spans
operator|.
name|NO_MORE_POSITIONS
operator|:
literal|"incorrect final start position, "
operator|+
name|this
operator|.
name|toString
argument_list|()
assert|;
assert|assert
name|endPosition
argument_list|()
operator|==
name|Spans
operator|.
name|NO_MORE_POSITIONS
operator|:
literal|"incorrect final end position, "
operator|+
name|this
operator|.
name|toString
argument_list|()
assert|;
block|}
comment|/**    * Called before the current doc's frequency is calculated    */
DECL|method|doStartCurrentDoc
specifier|protected
name|void
name|doStartCurrentDoc
parameter_list|()
throws|throws
name|IOException
block|{}
comment|/**    * Called each time the scorer's SpanScorer is advanced during frequency calculation    */
DECL|method|doCurrentSpans
specifier|protected
name|void
name|doCurrentSpans
parameter_list|()
throws|throws
name|IOException
block|{}
comment|/**    * Score the current doc. The default implementation scores the doc    * with the similarity using the slop-adjusted {@link #freq}.    */
DECL|method|scoreCurrentDoc
specifier|protected
name|float
name|scoreCurrentDoc
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|docScorer
operator|!=
literal|null
operator|:
name|getClass
argument_list|()
operator|+
literal|" has a null docScorer!"
assert|;
return|return
name|docScorer
operator|.
name|score
argument_list|(
name|docID
argument_list|()
argument_list|,
name|freq
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureFreq
argument_list|()
expr_stmt|;
return|return
name|scoreCurrentDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
specifier|final
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureFreq
argument_list|()
expr_stmt|;
return|return
name|numMatches
return|;
block|}
comment|/** Returns the intermediate "sloppy freq" adjusted for edit distance    *  @lucene.internal */
DECL|method|sloppyFreq
specifier|final
name|float
name|sloppyFreq
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureFreq
argument_list|()
expr_stmt|;
return|return
name|freq
return|;
block|}
block|}
end_class
end_unit
