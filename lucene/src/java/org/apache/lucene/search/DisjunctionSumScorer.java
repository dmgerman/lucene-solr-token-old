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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** A Scorer for OR like queries, counterpart of<code>ConjunctionScorer</code>.  * This Scorer implements {@link Scorer#advance(int)} and uses advance() on the given Scorers.   */
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
argument_list|<
name|Scorer
argument_list|>
name|subScorers
decl_stmt|;
comment|/** The minimum number of scorers that should match. */
DECL|field|minimumNrMatchers
specifier|private
specifier|final
name|int
name|minimumNrMatchers
decl_stmt|;
comment|/** The scorerDocQueue contains all subscorers ordered by their current doc(),    * with the minimum at the top.    *<br>The scorerDocQueue is initialized the first time nextDoc() or advance() is called.    *<br>An exhausted scorer is immediately removed from the scorerDocQueue.    *<br>If less than the minimumNrMatchers scorers    * remain in the scorerDocQueue nextDoc() and advance() return false.    *<p>    * After each to call to nextDoc() or advance()    *<code>currentSumScore</code> is the total score of the current matching doc,    *<code>nrMatchers</code> is the number of matching scorers,    * and all scorers are after the matching doc, or are exhausted.    */
DECL|field|scorerDocQueue
specifier|private
name|ScorerDocQueue
name|scorerDocQueue
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
comment|/** Construct a<code>DisjunctionScorer</code>.    * @param weight The weight to be used.    * @param subScorers A collection of at least two subscorers.    * @param minimumNrMatchers The positive minimum number of subscorers that should    * match to match this query.    *<br>When<code>minimumNrMatchers</code> is bigger than    * the number of<code>subScorers</code>,    * no matches will be produced.    *<br>When minimumNrMatchers equals the number of subScorers,    * it more efficient to use<code>ConjunctionScorer</code>.    */
DECL|method|DisjunctionSumScorer
specifier|public
name|DisjunctionSumScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|subScorers
parameter_list|,
name|int
name|minimumNrMatchers
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|weight
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
name|initScorerDocQueue
argument_list|()
expr_stmt|;
block|}
comment|/** Construct a<code>DisjunctionScorer</code>, using one as the minimum number    * of matching subscorers.    */
DECL|method|DisjunctionSumScorer
specifier|public
name|DisjunctionSumScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|subScorers
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|weight
argument_list|,
name|subScorers
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/** Called the first time nextDoc() or advance() is called to    * initialize<code>scorerDocQueue</code>.    */
DECL|method|initScorerDocQueue
specifier|private
name|void
name|initScorerDocQueue
parameter_list|()
throws|throws
name|IOException
block|{
name|scorerDocQueue
operator|=
operator|new
name|ScorerDocQueue
argument_list|(
name|nrScorers
argument_list|)
expr_stmt|;
for|for
control|(
name|Scorer
name|se
range|:
name|subScorers
control|)
block|{
if|if
condition|(
name|se
operator|.
name|nextDoc
argument_list|()
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|scorerDocQueue
operator|.
name|insert
argument_list|(
name|se
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Scores and collects all matching documents.    * @param collector The collector to which all matching documents are passed through.    */
annotation|@
name|Override
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
name|nextDoc
argument_list|()
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|currentDoc
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Expert: Collects matching documents in a range.  Hook for optimization.    * Note that {@link #nextDoc()} must be called once before this method is called    * for the first time.    * @param collector The collector to which all matching documents are passed through.    * @param max Do not score documents past this.    * @return true if more matching documents may remain.    */
annotation|@
name|Override
DECL|method|score
specifier|public
name|boolean
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|firstDocID
parameter_list|)
throws|throws
name|IOException
block|{
comment|// firstDocID is ignored since nextDoc() sets 'currentDoc'
name|collector
operator|.
name|setScorer
argument_list|(
name|this
argument_list|)
expr_stmt|;
while|while
condition|(
name|currentDoc
operator|<
name|max
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|currentDoc
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextDoc
argument_list|()
operator|==
name|NO_MORE_DOCS
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
if|if
condition|(
name|scorerDocQueue
operator|.
name|size
argument_list|()
operator|<
name|minimumNrMatchers
operator|||
operator|!
name|advanceAfterCurrent
argument_list|()
condition|)
block|{
name|currentDoc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
return|return
name|currentDoc
return|;
block|}
comment|/** Advance all subscorers after the current document determined by the    * top of the<code>scorerDocQueue</code>.    * Repeat until at least the minimum number of subscorers match on the same    * document and all subscorers are after that document or are exhausted.    *<br>On entry the<code>scorerDocQueue</code> has at least<code>minimumNrMatchers</code>    * available. At least the scorer with the minimum document number will be advanced.    * @return true iff there is a match.    *<br>In case there is a match,</code>currentDoc</code>,</code>currentSumScore</code>,    * and</code>nrMatchers</code> describe the match.    *    * TODO: Investigate whether it is possible to use advance() when    * the minimum number of matchers is bigger than one, ie. try and use the    * character of ConjunctionScorer for the minimum number of matchers.    * Also delay calling score() on the sub scorers until the minimum number of    * matchers is reached.    *<br>For this, a Scorer array with minimumNrMatchers elements might    * hold Scorers at currentDoc that are temporarily popped from scorerQueue.    */
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
name|scorerDocQueue
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
name|scorerDocQueue
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
comment|/** Returns the score of the current document matching the query.    * Initially invalid, until {@link #nextDoc()} is called the first time.    */
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
name|currentScore
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
name|currentDoc
return|;
block|}
comment|/** Returns the number of subscorers matching the current document.    * Initially invalid, until {@link #nextDoc()} is called the first time.    */
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
comment|/**    * Advances to the first match beyond the current whose document number is    * greater than or equal to a given target.<br>    * The implementation uses the advance() method on the subscorers.    *     * @param target    *          The target document number.    * @return the document whose number is greater than or equal to the given    *         target, or -1 if none exist.    */
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
if|if
condition|(
name|scorerDocQueue
operator|.
name|size
argument_list|()
operator|<
name|minimumNrMatchers
condition|)
block|{
return|return
name|currentDoc
operator|=
name|NO_MORE_DOCS
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
name|currentDoc
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
condition|?
name|currentDoc
else|:
operator|(
name|currentDoc
operator|=
name|NO_MORE_DOCS
operator|)
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
name|scorerDocQueue
operator|.
name|size
argument_list|()
operator|<
name|minimumNrMatchers
condition|)
block|{
return|return
name|currentDoc
operator|=
name|NO_MORE_DOCS
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
block|}
end_class
end_unit
