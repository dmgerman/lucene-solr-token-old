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
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|PriorityQueue
import|;
end_import
begin_comment
comment|/** A Scorer for OR like queries, counterpart of Lucene's<code>ConjunctionScorer</code>.  * This Scorer implements {@link Scorer#skipTo(int)} and uses skipTo() on the given Scorers.   */
end_comment
begin_class
DECL|class|DisjunctionSumScorer
specifier|public
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
comment|/** The scorerQueue contains all subscorers ordered by their current doc(),    * with the minimum at the top.    *<br>The scorerQueue is initialized the first time next() or skipTo() is called.    *<br>An exhausted scorer is immediately removed from the scorerQueue.    *<br>If less than the minimumNrMatchers scorers    * remain in the scorerQueue next() and skipTo() return false.    *<p>    * After each to call to next() or skipTo()    *<code>currentSumScore</code> is the total score of the current matching doc,    *<code>nrMatchers</code> is the number of matching scorers,    * and all scorers are after the matching doc, or are exhausted.    */
DECL|field|scorerQueue
specifier|private
name|ScorerQueue
name|scorerQueue
init|=
literal|null
decl_stmt|;
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
comment|/** Called the first time next() or skipTo() is called to    * initialize<code>scorerQueue</code>.    */
DECL|method|initScorerQueue
specifier|private
name|void
name|initScorerQueue
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
name|scorerQueue
operator|=
operator|new
name|ScorerQueue
argument_list|(
name|nrScorers
argument_list|)
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
comment|// doc() method will be used in scorerQueue.
name|scorerQueue
operator|.
name|insert
argument_list|(
name|se
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** A<code>PriorityQueue</code> that orders by {@link Scorer#doc()}. */
DECL|class|ScorerQueue
specifier|private
class|class
name|ScorerQueue
extends|extends
name|PriorityQueue
block|{
DECL|method|ScorerQueue
name|ScorerQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Scorer
operator|)
name|o1
operator|)
operator|.
name|doc
argument_list|()
operator|<
operator|(
operator|(
name|Scorer
operator|)
name|o2
operator|)
operator|.
name|doc
argument_list|()
return|;
block|}
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
name|scorerQueue
operator|==
literal|null
condition|)
block|{
name|initScorerQueue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|scorerQueue
operator|.
name|size
argument_list|()
operator|<
name|minimumNrMatchers
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|advanceAfterCurrent
argument_list|()
return|;
block|}
block|}
comment|/** Advance all subscorers after the current document determined by the    * top of the<code>scorerQueue</code>.    * Repeat until at least the minimum number of subscorers match on the same    * document and all subscorers are after that document or are exhausted.    *<br>On entry the<code>scorerQueue</code> has at least<code>minimumNrMatchers</code>    * available. At least the scorer with the minimum document number will be advanced.    * @return true iff there is a match.    *<br>In case there is a match,</code>currentDoc</code>,</code>currentSumScore</code>,    * and</code>nrMatchers</code> describe the match.    *    * @todo Investigate whether it is possible to use skipTo() when    * the minimum number of matchers is bigger than one, ie. try and use the    * character of ConjunctionScorer for the minimum number of matchers.    */
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
name|Scorer
name|top
init|=
operator|(
name|Scorer
operator|)
name|scorerQueue
operator|.
name|top
argument_list|()
decl_stmt|;
name|currentDoc
operator|=
name|top
operator|.
name|doc
argument_list|()
expr_stmt|;
name|currentScore
operator|=
name|top
operator|.
name|score
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
name|top
operator|.
name|next
argument_list|()
condition|)
block|{
name|scorerQueue
operator|.
name|adjustTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|scorerQueue
operator|.
name|pop
argument_list|()
expr_stmt|;
if|if
condition|(
name|scorerQueue
operator|.
name|size
argument_list|()
operator|<
operator|(
name|minimumNrMatchers
operator|-
name|nrMatchers
operator|)
condition|)
block|{
comment|// Not enough subscorers left for a match on this document,
comment|// and also no more chance of any further match.
return|return
literal|false
return|;
block|}
if|if
condition|(
name|scorerQueue
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
break|break;
comment|// nothing more to advance, check for last match.
block|}
block|}
name|top
operator|=
operator|(
name|Scorer
operator|)
name|scorerQueue
operator|.
name|top
argument_list|()
expr_stmt|;
if|if
condition|(
name|top
operator|.
name|doc
argument_list|()
operator|!=
name|currentDoc
condition|)
block|{
break|break;
comment|// All remaining subscorers are after currentDoc.
block|}
else|else
block|{
name|currentScore
operator|+=
name|top
operator|.
name|score
argument_list|()
expr_stmt|;
name|nrMatchers
operator|++
expr_stmt|;
block|}
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
name|scorerQueue
operator|.
name|size
argument_list|()
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
name|scorerQueue
operator|==
literal|null
condition|)
block|{
name|initScorerQueue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|scorerQueue
operator|.
name|size
argument_list|()
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
name|Scorer
name|top
init|=
operator|(
name|Scorer
operator|)
name|scorerQueue
operator|.
name|top
argument_list|()
decl_stmt|;
if|if
condition|(
name|top
operator|.
name|doc
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
name|top
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|scorerQueue
operator|.
name|adjustTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|scorerQueue
operator|.
name|pop
argument_list|()
expr_stmt|;
if|if
condition|(
name|scorerQueue
operator|.
name|size
argument_list|()
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
comment|/** Gives and explanation for the score of a given document.   * @todo Show the resulting score. See BooleanScorer.explain() on how to do this.   */
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
name|res
operator|.
name|setDescription
argument_list|(
literal|"At least "
operator|+
name|minimumNrMatchers
operator|+
literal|" of"
argument_list|)
expr_stmt|;
name|Iterator
name|ssi
init|=
name|subScorers
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|ssi
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|res
operator|.
name|addDetail
argument_list|(
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
