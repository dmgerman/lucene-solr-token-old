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
name|java
operator|.
name|util
operator|.
name|Objects
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
name|TwoPhaseIterator
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
begin_comment
comment|/**  * A basic {@link Scorer} over {@link Spans}.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SpanScorer
specifier|public
class|class
name|SpanScorer
extends|extends
name|Scorer
block|{
DECL|field|spans
specifier|protected
specifier|final
name|Spans
name|spans
decl_stmt|;
DECL|field|docScorer
specifier|protected
specifier|final
name|Similarity
operator|.
name|SimScorer
name|docScorer
decl_stmt|;
comment|/** accumulated sloppy freq (computed in setFreqCurrentDoc) */
DECL|field|freq
specifier|private
name|float
name|freq
decl_stmt|;
comment|/** number of matches (computed in setFreqCurrentDoc) */
DECL|field|numMatches
specifier|private
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
comment|/** Sole constructor. */
DECL|method|SpanScorer
specifier|public
name|SpanScorer
parameter_list|(
name|SpanWeight
name|weight
parameter_list|,
name|Spans
name|spans
parameter_list|,
name|Similarity
operator|.
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
name|spans
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|spans
argument_list|)
expr_stmt|;
name|this
operator|.
name|docScorer
operator|=
name|docScorer
expr_stmt|;
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
name|spans
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
name|spans
return|;
block|}
annotation|@
name|Override
DECL|method|twoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|twoPhaseIterator
parameter_list|()
block|{
return|return
name|spans
operator|.
name|asTwoPhaseIterator
argument_list|()
return|;
block|}
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
name|spans
operator|.
name|doStartCurrentDoc
argument_list|()
expr_stmt|;
assert|assert
name|spans
operator|.
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
name|spans
operator|.
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
name|spans
operator|.
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
name|spans
operator|.
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
name|spans
operator|.
name|width
argument_list|()
argument_list|)
expr_stmt|;
name|spans
operator|.
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
name|spans
operator|.
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
name|spans
operator|.
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
name|spans
operator|.
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
