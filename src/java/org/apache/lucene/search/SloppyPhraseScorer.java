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
name|HashMap
import|;
end_import
begin_class
DECL|class|SloppyPhraseScorer
specifier|final
class|class
name|SloppyPhraseScorer
extends|extends
name|PhraseScorer
block|{
DECL|field|slop
specifier|private
name|int
name|slop
decl_stmt|;
DECL|field|repeats
specifier|private
name|PhrasePositions
name|repeats
index|[]
decl_stmt|;
DECL|field|tmpPos
specifier|private
name|PhrasePositions
name|tmpPos
index|[]
decl_stmt|;
comment|// for flipping repeating pps.
DECL|field|checkedRepeats
specifier|private
name|boolean
name|checkedRepeats
decl_stmt|;
DECL|method|SloppyPhraseScorer
name|SloppyPhraseScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|TermPositions
index|[]
name|tps
parameter_list|,
name|int
index|[]
name|offsets
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|int
name|slop
parameter_list|,
name|byte
index|[]
name|norms
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|tps
argument_list|,
name|offsets
argument_list|,
name|similarity
argument_list|,
name|norms
argument_list|)
expr_stmt|;
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
block|}
comment|/**      * Score a candidate doc for all slop-valid position-combinations (matches)       * encountered while traversing/hopping the PhrasePositions.      *<br> The score contribution of a match depends on the distance:       *<br> - highest score for distance=0 (exact match).      *<br> - score gets lower as distance gets higher.      *<br>Example: for query "a b"~2, a document "x a b a y" can be scored twice:       * once for "a b" (distance=0), and once for "b a" (distance=2).      *<br>Possibly not all valid combinations are encountered, because for efficiency        * we always propagate the least PhrasePosition. This allows to base on       * PriorityQueue and move forward faster.       * As result, for example, document "a b c b a"      * would score differently for queries "a b c"~4 and "c b a"~4, although       * they really are equivalent.       * Similarly, for doc "a b c b a f g", query "c b"~2       * would get same score as "g f"~2, although "c b"~2 could be matched twice.      * We may want to fix this in the future (currently not, for performance reasons).      */
DECL|method|phraseFreq
specifier|protected
specifier|final
name|float
name|phraseFreq
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|end
init|=
name|initPhrasePositions
argument_list|()
decl_stmt|;
name|float
name|freq
init|=
literal|0.0f
decl_stmt|;
name|boolean
name|done
init|=
operator|(
name|end
operator|<
literal|0
operator|)
decl_stmt|;
while|while
condition|(
operator|!
name|done
condition|)
block|{
name|PhrasePositions
name|pp
init|=
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|pp
operator|.
name|position
decl_stmt|;
name|int
name|next
init|=
name|pq
operator|.
name|top
argument_list|()
operator|.
name|position
decl_stmt|;
name|boolean
name|tpsDiffer
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|pos
init|=
name|start
init|;
name|pos
operator|<=
name|next
operator|||
operator|!
name|tpsDiffer
condition|;
name|pos
operator|=
name|pp
operator|.
name|position
control|)
block|{
if|if
condition|(
name|pos
operator|<=
name|next
operator|&&
name|tpsDiffer
condition|)
name|start
operator|=
name|pos
expr_stmt|;
comment|// advance pp to min window
if|if
condition|(
operator|!
name|pp
operator|.
name|nextPosition
argument_list|()
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
comment|// ran out of a term -- done
break|break;
block|}
name|PhrasePositions
name|pp2
init|=
literal|null
decl_stmt|;
name|tpsDiffer
operator|=
operator|!
name|pp
operator|.
name|repeats
operator|||
operator|(
name|pp2
operator|=
name|termPositionsDiffer
argument_list|(
name|pp
argument_list|)
operator|)
operator|==
literal|null
expr_stmt|;
if|if
condition|(
name|pp2
operator|!=
literal|null
operator|&&
name|pp2
operator|!=
name|pp
condition|)
block|{
name|pp
operator|=
name|flip
argument_list|(
name|pp
argument_list|,
name|pp2
argument_list|)
expr_stmt|;
comment|// flip pp to pp2
block|}
block|}
name|int
name|matchLength
init|=
name|end
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|matchLength
operator|<=
name|slop
condition|)
name|freq
operator|+=
name|getSimilarity
argument_list|()
operator|.
name|sloppyFreq
argument_list|(
name|matchLength
argument_list|)
expr_stmt|;
comment|// score match
if|if
condition|(
name|pp
operator|.
name|position
operator|>
name|end
condition|)
name|end
operator|=
name|pp
operator|.
name|position
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
name|pp
argument_list|)
expr_stmt|;
comment|// restore pq
block|}
return|return
name|freq
return|;
block|}
comment|// flip pp2 and pp in the queue: pop until finding pp2, insert back all but pp2, insert pp back.
comment|// assumes: pp!=pp2, pp2 in pq, pp not in pq.
comment|// called only when there are repeating pps.
DECL|method|flip
specifier|private
name|PhrasePositions
name|flip
parameter_list|(
name|PhrasePositions
name|pp
parameter_list|,
name|PhrasePositions
name|pp2
parameter_list|)
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
name|PhrasePositions
name|pp3
decl_stmt|;
comment|//pop until finding pp2
while|while
condition|(
operator|(
name|pp3
operator|=
name|pq
operator|.
name|pop
argument_list|()
operator|)
operator|!=
name|pp2
condition|)
block|{
name|tmpPos
index|[
name|n
operator|++
index|]
operator|=
name|pp3
expr_stmt|;
block|}
comment|//insert back all but pp2
for|for
control|(
name|n
operator|--
init|;
name|n
operator|>=
literal|0
condition|;
name|n
operator|--
control|)
block|{
name|pq
operator|.
name|insertWithOverflow
argument_list|(
name|tmpPos
index|[
name|n
index|]
argument_list|)
expr_stmt|;
block|}
comment|//insert pp back
name|pq
operator|.
name|add
argument_list|(
name|pp
argument_list|)
expr_stmt|;
return|return
name|pp2
return|;
block|}
comment|/**      * Init PhrasePositions in place.      * There is a one time initialization for this scorer:      *<br>- Put in repeats[] each pp that has another pp with same position in the doc.      *<br>- Also mark each such pp by pp.repeats = true.      *<br>Later can consult with repeats[] in termPositionsDiffer(pp), making that check efficient.      * In particular, this allows to score queries with no repetitions with no overhead due to this computation.      *<br>- Example 1 - query with no repetitions: "ho my"~2      *<br>- Example 2 - query with repetitions: "ho my my"~2      *<br>- Example 3 - query with repetitions: "my ho my"~2      *<br>Init per doc w/repeats in query, includes propagating some repeating pp's to avoid false phrase detection.        * @return end (max position), or -1 if any term ran out (i.e. done)       * @throws IOException       */
DECL|method|initPhrasePositions
specifier|private
name|int
name|initPhrasePositions
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|end
init|=
literal|0
decl_stmt|;
comment|// no repeats at all (most common case is also the simplest one)
if|if
condition|(
name|checkedRepeats
operator|&&
name|repeats
operator|==
literal|null
condition|)
block|{
comment|// build queue from list
name|pq
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|first
init|;
name|pp
operator|!=
literal|null
condition|;
name|pp
operator|=
name|pp
operator|.
name|next
control|)
block|{
name|pp
operator|.
name|firstPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|pp
operator|.
name|position
operator|>
name|end
condition|)
name|end
operator|=
name|pp
operator|.
name|position
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
name|pp
argument_list|)
expr_stmt|;
comment|// build pq from list
block|}
return|return
name|end
return|;
block|}
comment|// position the pp's
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|first
init|;
name|pp
operator|!=
literal|null
condition|;
name|pp
operator|=
name|pp
operator|.
name|next
control|)
name|pp
operator|.
name|firstPosition
argument_list|()
expr_stmt|;
comment|// one time initializatin for this scorer
if|if
condition|(
operator|!
name|checkedRepeats
condition|)
block|{
name|checkedRepeats
operator|=
literal|true
expr_stmt|;
comment|// check for repeats
name|HashMap
argument_list|<
name|PhrasePositions
argument_list|,
name|Object
argument_list|>
name|m
init|=
literal|null
decl_stmt|;
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|first
init|;
name|pp
operator|!=
literal|null
condition|;
name|pp
operator|=
name|pp
operator|.
name|next
control|)
block|{
name|int
name|tpPos
init|=
name|pp
operator|.
name|position
operator|+
name|pp
operator|.
name|offset
decl_stmt|;
for|for
control|(
name|PhrasePositions
name|pp2
init|=
name|pp
operator|.
name|next
init|;
name|pp2
operator|!=
literal|null
condition|;
name|pp2
operator|=
name|pp2
operator|.
name|next
control|)
block|{
name|int
name|tpPos2
init|=
name|pp2
operator|.
name|position
operator|+
name|pp2
operator|.
name|offset
decl_stmt|;
if|if
condition|(
name|tpPos2
operator|==
name|tpPos
condition|)
block|{
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|m
operator|=
operator|new
name|HashMap
argument_list|<
name|PhrasePositions
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|pp
operator|.
name|repeats
operator|=
literal|true
expr_stmt|;
name|pp2
operator|.
name|repeats
operator|=
literal|true
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|pp
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|pp2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
name|repeats
operator|=
name|m
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|PhrasePositions
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
comment|// with repeats must advance some repeating pp's so they all start with differing tp's
if|if
condition|(
name|repeats
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|repeats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PhrasePositions
name|pp
init|=
name|repeats
index|[
name|i
index|]
decl_stmt|;
name|PhrasePositions
name|pp2
decl_stmt|;
while|while
condition|(
operator|(
name|pp2
operator|=
name|termPositionsDiffer
argument_list|(
name|pp
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|pp2
operator|.
name|nextPosition
argument_list|()
condition|)
comment|// out of pps that do not differ, advance the pp with higher offset
return|return
operator|-
literal|1
return|;
comment|// ran out of a term -- done
block|}
block|}
block|}
comment|// build queue from list
name|pq
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|PhrasePositions
name|pp
init|=
name|first
init|;
name|pp
operator|!=
literal|null
condition|;
name|pp
operator|=
name|pp
operator|.
name|next
control|)
block|{
if|if
condition|(
name|pp
operator|.
name|position
operator|>
name|end
condition|)
name|end
operator|=
name|pp
operator|.
name|position
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
name|pp
argument_list|)
expr_stmt|;
comment|// build pq from list
block|}
if|if
condition|(
name|repeats
operator|!=
literal|null
condition|)
block|{
name|tmpPos
operator|=
operator|new
name|PhrasePositions
index|[
name|pq
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
block|}
return|return
name|end
return|;
block|}
comment|/**      * We disallow two pp's to have the same TermPosition, thereby verifying multiple occurrences       * in the query of the same word would go elsewhere in the matched doc.      * @return null if differ (i.e. valid) otherwise return the higher offset PhrasePositions      * out of the first two PPs found to not differ.      */
DECL|method|termPositionsDiffer
specifier|private
name|PhrasePositions
name|termPositionsDiffer
parameter_list|(
name|PhrasePositions
name|pp
parameter_list|)
block|{
comment|// efficiency note: a more efficient implementation could keep a map between repeating
comment|// pp's, so that if pp1a, pp1b, pp1c are repeats term1, and pp2a, pp2b are repeats
comment|// of term2, pp2a would only be checked against pp2b but not against pp1a, pp1b, pp1c.
comment|// However this would complicate code, for a rather rare case, so choice is to compromise here.
name|int
name|tpPos
init|=
name|pp
operator|.
name|position
operator|+
name|pp
operator|.
name|offset
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
name|repeats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PhrasePositions
name|pp2
init|=
name|repeats
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|pp2
operator|==
name|pp
condition|)
continue|continue;
name|int
name|tpPos2
init|=
name|pp2
operator|.
name|position
operator|+
name|pp2
operator|.
name|offset
decl_stmt|;
if|if
condition|(
name|tpPos2
operator|==
name|tpPos
condition|)
return|return
name|pp
operator|.
name|offset
operator|>
name|pp2
operator|.
name|offset
condition|?
name|pp
else|:
name|pp2
return|;
comment|// do not differ: return the one with higher offset.
block|}
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
