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
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|ScorerDocQueue
import|;
end_import
begin_comment
comment|/** A Scorer for OR like queries, counterpart of<code>ConjunctionScorer</code>.  * This Scorer implements {@link Scorer#skipTo(int)} and uses skipTo() on the given Scorers.   * @todo Implement score(HitCollector, int).  */
end_comment
begin_class
DECL|class|DisjunctionSumScorer
class|class
name|DisjunctionSumScorer
extends|extends
name|Scorer
block|{
comment|/** The number of subscorers. */
DECL|field|nrScorers
specifier|private
specifier|final
name|int
name|nrScorers
decl_stmt|;
comment|/** The subscorers. */
DECL|field|subScorers
specifier|protected
specifier|final
name|List
name|subScorers
decl_stmt|;
comment|/** The minimum number of scorers that should match. */
DECL|field|minimumNrMatchers
specifier|private
specifier|final
name|int
name|minimumNrMatchers
decl_stmt|;
comment|/** The scorerDocQueue contains all subscorers ordered by their current doc(),    * with the minimum at the top.    *<br>The scorerDocQueue is initialized the first time next() or skipTo() is called.    *<br>An exhausted scorer is immediately removed from the scorerDocQueue.    *<br>If less than the minimumNrMatchers scorers    * remain in the scorerDocQueue next() and skipTo() return false.    *<p>    * After each to call to next() or skipTo()    *<code>currentSumScore</code> is the total score of the current matching doc,    *<code>nrMatchers</code> is the number of matching scorers,    * and all scorers are after the matching doc, or are exhausted.    */
DECL|field|scorerDocQueue
specifier|private
name|ScorerDocQueue
name|scorerDocQueue
init|=
literal|null
decl_stmt|;
DECL|field|queueSize
specifier|private
name|int
name|queueSize
init|=
operator|-
literal|1
decl_stmt|;
comment|// used to avoid size() method calls on scorerDocQueue
comment|/** The document number of the current match. */
DECL|field|currentDoc
specifier|private
name|int
name|currentDoc
init|=
operator|-
literal|1
decl_stmt|;
comment|/** The number of subscorers that provide the current match. */
DECL|field|nrMatchers
specifier|protected
name|int
name|nrMatchers
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentScore
specifier|private
name|float
name|currentScore
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
comment|/** Construct a<code>DisjunctionScorer</code>.    * @param subScorers A collection of at least two subscorers.    * @param minimumNrMatchers The positive minimum number of subscorers that should    * match to match this query.    *<br>When<code>minimumNrMatchers</code> is bigger than    * the number of<code>subScorers</code>,    * no matches will be produced.    *<br>When minimumNrMatchers equals the number of subScorers,    * it more efficient to use<code>ConjunctionScorer</code>.    */
DECL|method|DisjunctionSumScorer
specifier|public
name|DisjunctionSumScorer
parameter_list|(
name|List
name|subScorers
parameter_list|,
name|int
name|minimumNrMatchers
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|nrScorers
operator|=
name|subScorers
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|minimumNrMatchers
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Minimum nr of matchers must be positive"
argument_list|)
throw|;
block|}
if|if
condition|(
name|nrScorers
operator|<=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"There must be at least 2 subScorers"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minimumNrMatchers
operator|=
name|minimumNrMatchers
expr_stmt|;
name|this
operator|.
name|subScorers
operator|=
name|subScorers
expr_stmt|;
block|}
comment|/** Construct a<code>DisjunctionScorer</code>, using one as the minimum number    * of matching subscorers.    */
DECL|method|DisjunctionSumScorer
specifier|public
name|DisjunctionSumScorer
parameter_list|(
name|List
name|subScorers
parameter_list|)
block|{
name|this
argument_list|(
name|subScorers
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** Called the first time next() or skipTo() is called to    * initialize<code>scorerDocQueue</code>.    */
DECL|method|initScorerDocQueue
specifier|private
name|void
name|initScorerDocQueue
parameter_list|()
throws|throws
name|IOException
block|{
name|Iterator
name|si
init|=
name|subScorers
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|scorerDocQueue
operator|=
operator|new
name|ScorerDocQueue
argument_list|(
name|nrScorers
argument_list|)
expr_stmt|;
name|queueSize
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|si
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Scorer
name|se
init|=
operator|(
name|Scorer
operator|)
name|si
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|se
operator|.
name|next
argument_list|()
condition|)
block|{
comment|// doc() method will be used in scorerDocQueue.
if|if
condition|(
name|scorerDocQueue
operator|.
name|insert
argument_list|(
name|se
argument_list|)
condition|)
block|{
name|queueSize
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Scores and collects all matching documents.    * @param hc The collector to which all matching documents are passed through    * {@link HitCollector#collect(int, float)}.    *<br>When this method is used the {@link #explain(int)} method should not be used.    */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|HitCollector
name|hc
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|next
argument_list|()
condition|)
block|{
name|hc
operator|.
name|collect
argument_list|(
name|currentDoc
argument_list|,
name|currentScore
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Expert: Collects matching documents in a range.  Hook for optimization.    * Note that {@link #next()} must be called once before this method is called    * for the first time.    * @param hc The collector to which all matching documents are passed through    * {@link HitCollector#collect(int, float)}.    * @param max Do not score documents past this.    * @return true if more matching documents may remain.    */
DECL|method|score
specifier|protected
name|boolean
name|score
parameter_list|(
name|HitCollector
name|hc
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|currentDoc
operator|<
name|max
condition|)
block|{
name|hc
operator|.
name|collect
argument_list|(
name|currentDoc
argument_list|,
name|currentScore
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|next
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|scorerDocQueue
operator|==
literal|null
condition|)
block|{
name|initScorerDocQueue
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|scorerDocQueue
operator|.
name|size
argument_list|()
operator|>=
name|minimumNrMatchers
operator|)
operator|&&
name|advanceAfterCurrent
argument_list|()
return|;
block|}
comment|/** Advance all subscorers after the current document determined by the    * top of the<code>scorerDocQueue</code>.    * Repeat until at least the minimum number of subscorers match on the same    * document and all subscorers are after that document or are exhausted.    *<br>On entry the<code>scorerDocQueue</code> has at least<code>minimumNrMatchers</code>    * available. At least the scorer with the minimum document number will be advanced.    * @return true iff there is a match.    *<br>In case there is a match,</code>currentDoc</code>,</code>currentSumScore</code>,    * and</code>nrMatchers</code> describe the match.    *    * @todo Investigate whether it is possible to use skipTo() when    * the minimum number of matchers is bigger than one, ie. try and use the    * character of ConjunctionScorer for the minimum number of matchers.    * Also delay calling score() on the sub scorers until the minimum number of    * matchers is reached.    *<br>For this, a Scorer array with minimumNrMatchers elements might    * hold Scorers at currentDoc that are temporarily popped from scorerQueue.    */
DECL|method|advanceAfterCurrent
specifier|protected
name|boolean
name|advanceAfterCurrent
parameter_list|()
throws|throws
name|IOException
block|{
do|do
block|{
comment|// repeat until minimum nr of matchers
name|currentDoc
operator|=
name|scorerDocQueue
operator|.
name|topDoc
argument_list|()
expr_stmt|;
name|currentScore
operator|=
name|scorerDocQueue
operator|.
name|topScore
argument_list|()
expr_stmt|;
name|nrMatchers
operator|=
literal|1
expr_stmt|;
do|do
block|{
comment|// Until all subscorers are after currentDoc
if|if
condition|(
operator|!
name|scorerDocQueue
operator|.
name|topNextAndAdjustElsePop
argument_list|()
condition|)
block|{
if|if
condition|(
operator|--
name|queueSize
operator|==
literal|0
condition|)
block|{
break|break;
comment|// nothing more to advance, check for last match.
block|}
block|}
if|if
condition|(
name|scorerDocQueue
operator|.
name|topDoc
argument_list|()
operator|!=
name|currentDoc
condition|)
block|{
break|break;
comment|// All remaining subscorers are after currentDoc.
block|}
name|currentScore
operator|+=
name|scorerDocQueue
operator|.
name|topScore
argument_list|()
expr_stmt|;
name|nrMatchers
operator|++
expr_stmt|;
block|}
do|while
condition|(
literal|true
condition|)
do|;
if|if
condition|(
name|nrMatchers
operator|>=
name|minimumNrMatchers
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|queueSize
operator|<
name|minimumNrMatchers
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
comment|/** Returns the score of the current document matching the query.    * Initially invalid, until {@link #next()} is called the first time.    */
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|currentScore
return|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|currentDoc
return|;
block|}
comment|/** Returns the number of subscorers matching the current document.    * Initially invalid, until {@link #next()} is called the first time.    */
DECL|method|nrMatchers
specifier|public
name|int
name|nrMatchers
parameter_list|()
block|{
return|return
name|nrMatchers
return|;
block|}
comment|/** Skips to the first match beyond the current whose document number is    * greater than or equal to a given target.    *<br>When this method is used the {@link #explain(int)} method should not be used.    *<br>The implementation uses the skipTo() method on the subscorers.    * @param target The target document number.    * @return true iff there is such a match.    */
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|scorerDocQueue
operator|==
literal|null
condition|)
block|{
name|initScorerDocQueue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|queueSize
operator|<
name|minimumNrMatchers
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|target
operator|<=
name|currentDoc
condition|)
block|{
return|return
literal|true
return|;
block|}
do|do
block|{
if|if
condition|(
name|scorerDocQueue
operator|.
name|topDoc
argument_list|()
operator|>=
name|target
condition|)
block|{
return|return
name|advanceAfterCurrent
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|scorerDocQueue
operator|.
name|topSkipToAndAdjustElsePop
argument_list|(
name|target
argument_list|)
condition|)
block|{
if|if
condition|(
operator|--
name|queueSize
operator|<
name|minimumNrMatchers
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
comment|/** @return An explanation for the score of a given document. */
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|res
init|=
operator|new
name|Explanation
argument_list|()
decl_stmt|;
name|Iterator
name|ssi
init|=
name|subScorers
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|float
name|sumScore
init|=
literal|0.0f
decl_stmt|;
name|int
name|nrMatches
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ssi
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Explanation
name|es
init|=
operator|(
operator|(
name|Scorer
operator|)
name|ssi
operator|.
name|next
argument_list|()
operator|)
operator|.
name|explain
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|es
operator|.
name|getValue
argument_list|()
operator|>
literal|0.0f
condition|)
block|{
comment|// indicates match
name|sumScore
operator|+=
name|es
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|nrMatches
operator|++
expr_stmt|;
block|}
name|res
operator|.
name|addDetail
argument_list|(
name|es
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nrMatchers
operator|>=
name|minimumNrMatchers
condition|)
block|{
name|res
operator|.
name|setValue
argument_list|(
name|sumScore
argument_list|)
expr_stmt|;
name|res
operator|.
name|setDescription
argument_list|(
literal|"sum over at least "
operator|+
name|minimumNrMatchers
operator|+
literal|" of "
operator|+
name|subScorers
operator|.
name|size
argument_list|()
operator|+
literal|":"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|.
name|setValue
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
name|res
operator|.
name|setDescription
argument_list|(
name|nrMatches
operator|+
literal|" match(es) but at least "
operator|+
name|minimumNrMatchers
operator|+
literal|" of "
operator|+
name|subScorers
operator|.
name|size
argument_list|()
operator|+
literal|" needed"
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
