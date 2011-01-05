begin_unit
begin_package
DECL|package|org.apache.lucene.util.automaton.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|fst
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
name|util
operator|.
name|ArrayUtil
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
name|RamUsageEstimator
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
comment|/** Can next() and advance() through the terms in an FST   * @lucene.experimental */
end_comment
begin_class
DECL|class|FSTEnum
specifier|abstract
class|class
name|FSTEnum
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|fst
specifier|protected
specifier|final
name|FST
argument_list|<
name|T
argument_list|>
name|fst
decl_stmt|;
DECL|field|arcs
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
index|[]
name|arcs
init|=
operator|new
name|FST
operator|.
name|Arc
index|[
literal|10
index|]
decl_stmt|;
comment|// outputs are cumulative
DECL|field|output
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
name|T
index|[]
name|output
init|=
operator|(
name|T
index|[]
operator|)
operator|new
name|Object
index|[
literal|10
index|]
decl_stmt|;
DECL|field|NO_OUTPUT
specifier|protected
specifier|final
name|T
name|NO_OUTPUT
decl_stmt|;
DECL|field|scratchArc
specifier|protected
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|scratchArc
init|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|upto
specifier|protected
name|int
name|upto
decl_stmt|;
DECL|field|targetLength
specifier|protected
name|int
name|targetLength
decl_stmt|;
comment|/** doFloor controls the behavior of advance: if it's true    *  doFloor is true, advance positions to the biggest    *  term before target.  */
DECL|method|FSTEnum
specifier|protected
name|FSTEnum
parameter_list|(
name|FST
argument_list|<
name|T
argument_list|>
name|fst
parameter_list|)
block|{
name|this
operator|.
name|fst
operator|=
name|fst
expr_stmt|;
name|NO_OUTPUT
operator|=
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
expr_stmt|;
name|fst
operator|.
name|getFirstArc
argument_list|(
name|getArc
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|output
index|[
literal|0
index|]
operator|=
name|NO_OUTPUT
expr_stmt|;
block|}
DECL|method|getTargetLabel
specifier|protected
specifier|abstract
name|int
name|getTargetLabel
parameter_list|()
function_decl|;
DECL|method|getCurrentLabel
specifier|protected
specifier|abstract
name|int
name|getCurrentLabel
parameter_list|()
function_decl|;
DECL|method|setCurrentLabel
specifier|protected
specifier|abstract
name|void
name|setCurrentLabel
parameter_list|(
name|int
name|label
parameter_list|)
function_decl|;
DECL|method|grow
specifier|protected
specifier|abstract
name|void
name|grow
parameter_list|()
function_decl|;
comment|/** Rewinds enum state to match the shared prefix between    *  current term and target term */
DECL|method|rewindPrefix
specifier|protected
specifier|final
name|void
name|rewindPrefix
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|upto
operator|==
literal|0
condition|)
block|{
comment|//System.out.println("  init");
name|upto
operator|=
literal|1
expr_stmt|;
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|getArc
argument_list|(
literal|0
argument_list|)
argument_list|,
name|getArc
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//System.out.println("  rewind upto=" + upto + " vs targetLength=" + targetLength);
specifier|final
name|int
name|currentLimit
init|=
name|upto
decl_stmt|;
name|upto
operator|=
literal|1
expr_stmt|;
while|while
condition|(
name|upto
operator|<
name|currentLimit
operator|&&
name|upto
operator|<=
name|targetLength
operator|+
literal|1
condition|)
block|{
specifier|final
name|int
name|cmp
init|=
name|getCurrentLabel
argument_list|()
operator|-
name|getTargetLabel
argument_list|()
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
comment|// seek forward
break|break;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
comment|// seek backwards -- reset this arc to the first arc
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|getArc
argument_list|(
name|upto
argument_list|)
decl_stmt|;
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|getArc
argument_list|(
name|upto
operator|-
literal|1
argument_list|)
argument_list|,
name|arc
argument_list|)
expr_stmt|;
comment|//System.out.println("    seek first arc");
break|break;
block|}
name|upto
operator|++
expr_stmt|;
block|}
block|}
DECL|method|doNext
specifier|protected
name|void
name|doNext
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("FE: next upto=" + upto);
if|if
condition|(
name|upto
operator|==
literal|0
condition|)
block|{
comment|//System.out.println("  init");
name|upto
operator|=
literal|1
expr_stmt|;
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|getArc
argument_list|(
literal|0
argument_list|)
argument_list|,
name|getArc
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// pop
comment|//System.out.println("  check pop curArc target=" + arcs[upto].target + " label=" + arcs[upto].label + " isLast?=" + arcs[upto].isLast());
while|while
condition|(
name|arcs
index|[
name|upto
index|]
operator|.
name|isLast
argument_list|()
condition|)
block|{
name|upto
operator|--
expr_stmt|;
if|if
condition|(
name|upto
operator|==
literal|0
condition|)
block|{
comment|//System.out.println("  eof");
return|return;
block|}
block|}
name|fst
operator|.
name|readNextArc
argument_list|(
name|arcs
index|[
name|upto
index|]
argument_list|)
expr_stmt|;
block|}
name|pushFirst
argument_list|()
expr_stmt|;
block|}
comment|// TODO: should we return a status here (SEEK_FOUND / SEEK_NOT_FOUND /
comment|// SEEK_END)?  saves the eq check above?
comment|/** Seeks to smallest term that's>= target. */
DECL|method|doSeekCeil
specifier|protected
name|void
name|doSeekCeil
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("    advance len=" + target.length + " curlen=" + current.length);
comment|// TODO: possibly caller could/should provide common
comment|// prefix length?  ie this work may be redundant if
comment|// caller is in fact intersecting against its own
comment|// automaton
comment|//System.out.println("FE.seekCeil upto=" + upto);
comment|// Save time by starting at the end of the shared prefix
comment|// b/w our current term& the target:
name|rewindPrefix
argument_list|()
expr_stmt|;
comment|//System.out.println("  after rewind upto=" + upto);
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|getArc
argument_list|(
name|upto
argument_list|)
decl_stmt|;
name|int
name|targetLabel
init|=
name|getTargetLabel
argument_list|()
decl_stmt|;
comment|//System.out.println("  init targetLabel=" + targetLabel);
comment|// Now scan forward, matching the new suffix of the target
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println("  cycle upto=" + upto + " arc.label=" + arc.label + " (" + (char) arc.label + ") vs targetLabel=" + targetLabel);
if|if
condition|(
name|arc
operator|.
name|bytesPerArc
operator|!=
literal|0
operator|&&
name|arc
operator|.
name|label
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Arcs are fixed array -- use binary search to find
comment|// the target.
specifier|final
name|FST
operator|.
name|BytesReader
name|in
init|=
name|fst
operator|.
name|getBytesReader
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|low
init|=
name|arc
operator|.
name|arcIdx
decl_stmt|;
name|int
name|high
init|=
name|arc
operator|.
name|numArcs
operator|-
literal|1
decl_stmt|;
name|int
name|mid
init|=
literal|0
decl_stmt|;
comment|//System.out.println("do arc array low=" + low + " high=" + high + " targetLabel=" + targetLabel);
name|boolean
name|found
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|mid
operator|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
expr_stmt|;
name|in
operator|.
name|pos
operator|=
name|arc
operator|.
name|posArcsStart
operator|-
name|arc
operator|.
name|bytesPerArc
operator|*
name|mid
operator|-
literal|1
expr_stmt|;
specifier|final
name|int
name|midLabel
init|=
name|fst
operator|.
name|readLabel
argument_list|(
name|in
argument_list|)
decl_stmt|;
specifier|final
name|int
name|cmp
init|=
name|midLabel
operator|-
name|targetLabel
decl_stmt|;
comment|//System.out.println("  cycle low=" + low + " high=" + high + " mid=" + mid + " midLabel=" + midLabel + " cmp=" + cmp);
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
comment|// NOTE: this code is dup'd w/ the code below (in
comment|// the outer else clause):
if|if
condition|(
name|found
condition|)
block|{
comment|// Match
name|arc
operator|.
name|arcIdx
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
name|fst
operator|.
name|readNextRealArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
assert|assert
name|arc
operator|.
name|arcIdx
operator|==
name|mid
assert|;
assert|assert
name|arc
operator|.
name|label
operator|==
name|targetLabel
operator|:
literal|"arc.label="
operator|+
name|arc
operator|.
name|label
operator|+
literal|" vs targetLabel="
operator|+
name|targetLabel
operator|+
literal|" mid="
operator|+
name|mid
assert|;
name|output
index|[
name|upto
index|]
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
index|[
name|upto
operator|-
literal|1
index|]
argument_list|,
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|targetLabel
operator|==
name|FST
operator|.
name|END_LABEL
condition|)
block|{
return|return;
block|}
name|setCurrentLabel
argument_list|(
name|arc
operator|.
name|label
argument_list|)
expr_stmt|;
name|incr
argument_list|()
expr_stmt|;
name|arc
operator|=
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|getArc
argument_list|(
name|upto
argument_list|)
argument_list|)
expr_stmt|;
name|targetLabel
operator|=
name|getTargetLabel
argument_list|()
expr_stmt|;
continue|continue;
block|}
elseif|else
if|if
condition|(
name|low
operator|==
name|arc
operator|.
name|numArcs
condition|)
block|{
comment|// Dead end
name|arc
operator|.
name|arcIdx
operator|=
name|arc
operator|.
name|numArcs
operator|-
literal|2
expr_stmt|;
name|fst
operator|.
name|readNextRealArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
assert|assert
name|arc
operator|.
name|isLast
argument_list|()
assert|;
comment|// Dead end (target is after the last arc);
comment|// rollback to last fork then push
name|upto
operator|--
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|upto
operator|==
literal|0
condition|)
block|{
return|return;
block|}
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|prevArc
init|=
name|getArc
argument_list|(
name|upto
argument_list|)
decl_stmt|;
comment|//System.out.println("  rollback upto=" + upto + " arc.label=" + prevArc.label + " isLast?=" + prevArc.isLast());
if|if
condition|(
operator|!
name|prevArc
operator|.
name|isLast
argument_list|()
condition|)
block|{
name|fst
operator|.
name|readNextArc
argument_list|(
name|prevArc
argument_list|)
expr_stmt|;
name|pushFirst
argument_list|()
expr_stmt|;
return|return;
block|}
name|upto
operator|--
expr_stmt|;
block|}
block|}
else|else
block|{
name|arc
operator|.
name|arcIdx
operator|=
operator|(
name|low
operator|>
name|high
condition|?
name|low
else|:
name|high
operator|)
operator|-
literal|1
expr_stmt|;
name|fst
operator|.
name|readNextRealArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
assert|assert
name|arc
operator|.
name|label
operator|>
name|targetLabel
assert|;
name|pushFirst
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
comment|// Arcs are not array'd -- must do linear scan:
if|if
condition|(
name|arc
operator|.
name|label
operator|==
name|targetLabel
condition|)
block|{
comment|// recurse
name|output
index|[
name|upto
index|]
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
index|[
name|upto
operator|-
literal|1
index|]
argument_list|,
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|targetLabel
operator|==
name|FST
operator|.
name|END_LABEL
condition|)
block|{
return|return;
block|}
name|setCurrentLabel
argument_list|(
name|arc
operator|.
name|label
argument_list|)
expr_stmt|;
name|incr
argument_list|()
expr_stmt|;
name|arc
operator|=
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|getArc
argument_list|(
name|upto
argument_list|)
argument_list|)
expr_stmt|;
name|targetLabel
operator|=
name|getTargetLabel
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|label
operator|>
name|targetLabel
condition|)
block|{
name|pushFirst
argument_list|()
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|isLast
argument_list|()
condition|)
block|{
comment|// Dead end (target is after the last arc);
comment|// rollback to last fork then push
name|upto
operator|--
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|upto
operator|==
literal|0
condition|)
block|{
return|return;
block|}
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|prevArc
init|=
name|getArc
argument_list|(
name|upto
argument_list|)
decl_stmt|;
comment|//System.out.println("  rollback upto=" + upto + " arc.label=" + prevArc.label + " isLast?=" + prevArc.isLast());
if|if
condition|(
operator|!
name|prevArc
operator|.
name|isLast
argument_list|()
condition|)
block|{
name|fst
operator|.
name|readNextArc
argument_list|(
name|prevArc
argument_list|)
expr_stmt|;
name|pushFirst
argument_list|()
expr_stmt|;
return|return;
block|}
name|upto
operator|--
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// keep scanning
comment|//System.out.println("    next scan");
name|fst
operator|.
name|readNextArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// TODO: should we return a status here (SEEK_FOUND / SEEK_NOT_FOUND /
comment|// SEEK_END)?  saves the eq check above?
comment|/** Seeks to largest term that's<= target. */
DECL|method|doSeekFloor
specifier|protected
name|void
name|doSeekFloor
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: possibly caller could/should provide common
comment|// prefix length?  ie this work may be redundant if
comment|// caller is in fact intersecting against its own
comment|// automaton
comment|//System.out.println("FE: seek floor upto=" + upto);
comment|// Save CPU by starting at the end of the shared prefix
comment|// b/w our current term& the target:
name|rewindPrefix
argument_list|()
expr_stmt|;
comment|//System.out.println("FE: after rewind upto=" + upto);
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|getArc
argument_list|(
name|upto
argument_list|)
decl_stmt|;
name|int
name|targetLabel
init|=
name|getTargetLabel
argument_list|()
decl_stmt|;
comment|//System.out.println("FE: init targetLabel=" + targetLabel);
comment|// Now scan forward, matching the new suffix of the target
while|while
condition|(
literal|true
condition|)
block|{
comment|//System.out.println("  cycle upto=" + upto + " arc.label=" + arc.label + " (" + (char) arc.label + ") targetLabel=" + targetLabel + " isLast?=" + arc.isLast());
if|if
condition|(
name|arc
operator|.
name|bytesPerArc
operator|!=
literal|0
operator|&&
name|arc
operator|.
name|label
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Arcs are fixed array -- use binary search to find
comment|// the target.
specifier|final
name|FST
operator|.
name|BytesReader
name|in
init|=
name|fst
operator|.
name|getBytesReader
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|low
init|=
name|arc
operator|.
name|arcIdx
decl_stmt|;
name|int
name|high
init|=
name|arc
operator|.
name|numArcs
operator|-
literal|1
decl_stmt|;
name|int
name|mid
init|=
literal|0
decl_stmt|;
comment|//System.out.println("do arc array low=" + low + " high=" + high + " targetLabel=" + targetLabel);
name|boolean
name|found
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|mid
operator|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
expr_stmt|;
name|in
operator|.
name|pos
operator|=
name|arc
operator|.
name|posArcsStart
operator|-
name|arc
operator|.
name|bytesPerArc
operator|*
name|mid
operator|-
literal|1
expr_stmt|;
specifier|final
name|int
name|midLabel
init|=
name|fst
operator|.
name|readLabel
argument_list|(
name|in
argument_list|)
decl_stmt|;
specifier|final
name|int
name|cmp
init|=
name|midLabel
operator|-
name|targetLabel
decl_stmt|;
comment|//System.out.println("  cycle low=" + low + " high=" + high + " mid=" + mid + " midLabel=" + midLabel + " cmp=" + cmp);
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
comment|// NOTE: this code is dup'd w/ the code below (in
comment|// the outer else clause):
if|if
condition|(
name|found
condition|)
block|{
comment|// Match -- recurse
comment|//System.out.println("  match!  arcIdx=" + mid);
name|arc
operator|.
name|arcIdx
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
name|fst
operator|.
name|readNextRealArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
assert|assert
name|arc
operator|.
name|arcIdx
operator|==
name|mid
assert|;
assert|assert
name|arc
operator|.
name|label
operator|==
name|targetLabel
operator|:
literal|"arc.label="
operator|+
name|arc
operator|.
name|label
operator|+
literal|" vs targetLabel="
operator|+
name|targetLabel
operator|+
literal|" mid="
operator|+
name|mid
assert|;
name|output
index|[
name|upto
index|]
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
index|[
name|upto
operator|-
literal|1
index|]
argument_list|,
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|targetLabel
operator|==
name|FST
operator|.
name|END_LABEL
condition|)
block|{
return|return;
block|}
name|setCurrentLabel
argument_list|(
name|arc
operator|.
name|label
argument_list|)
expr_stmt|;
name|incr
argument_list|()
expr_stmt|;
name|arc
operator|=
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|getArc
argument_list|(
name|upto
argument_list|)
argument_list|)
expr_stmt|;
name|targetLabel
operator|=
name|getTargetLabel
argument_list|()
expr_stmt|;
continue|continue;
block|}
elseif|else
if|if
condition|(
name|high
operator|==
operator|-
literal|1
condition|)
block|{
comment|//System.out.println("  before first");
comment|// Very first arc is after our target
comment|// TODO: if each arc could somehow read the arc just
comment|// before, we can save this re-scan.  The ceil case
comment|// doesn't need this because it reads the next arc
comment|// instead:
while|while
condition|(
literal|true
condition|)
block|{
comment|// First, walk backwards until we find a first arc
comment|// that's before our target label:
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|getArc
argument_list|(
name|upto
operator|-
literal|1
argument_list|)
argument_list|,
name|arc
argument_list|)
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|label
operator|<
name|targetLabel
condition|)
block|{
comment|// Then, scan forwards to the arc just before
comment|// the targetLabel:
while|while
condition|(
operator|!
name|arc
operator|.
name|isLast
argument_list|()
operator|&&
name|fst
operator|.
name|readNextArcLabel
argument_list|(
name|arc
argument_list|)
operator|<
name|targetLabel
condition|)
block|{
name|fst
operator|.
name|readNextArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
block|}
name|pushLast
argument_list|()
expr_stmt|;
return|return;
block|}
name|upto
operator|--
expr_stmt|;
if|if
condition|(
name|upto
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|targetLabel
operator|=
name|getTargetLabel
argument_list|()
expr_stmt|;
name|arc
operator|=
name|getArc
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// There is a floor arc:
name|arc
operator|.
name|arcIdx
operator|=
operator|(
name|low
operator|>
name|high
condition|?
name|high
else|:
name|low
operator|)
operator|-
literal|1
expr_stmt|;
comment|//System.out.println(" hasFloor arcIdx=" + (arc.arcIdx+1));
name|fst
operator|.
name|readNextRealArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
assert|assert
name|arc
operator|.
name|isLast
argument_list|()
operator|||
name|fst
operator|.
name|readNextArcLabel
argument_list|(
name|arc
argument_list|)
operator|>
name|targetLabel
assert|;
assert|assert
name|arc
operator|.
name|label
operator|<
name|targetLabel
assert|;
name|pushLast
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
if|if
condition|(
name|arc
operator|.
name|label
operator|==
name|targetLabel
condition|)
block|{
comment|// Match -- recurse
name|output
index|[
name|upto
index|]
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
index|[
name|upto
operator|-
literal|1
index|]
argument_list|,
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|targetLabel
operator|==
name|FST
operator|.
name|END_LABEL
condition|)
block|{
return|return;
block|}
name|setCurrentLabel
argument_list|(
name|arc
operator|.
name|label
argument_list|)
expr_stmt|;
name|incr
argument_list|()
expr_stmt|;
name|arc
operator|=
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|getArc
argument_list|(
name|upto
argument_list|)
argument_list|)
expr_stmt|;
name|targetLabel
operator|=
name|getTargetLabel
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|arc
operator|.
name|label
operator|>
name|targetLabel
condition|)
block|{
comment|// TODO: if each arc could somehow read the arc just
comment|// before, we can save this re-scan.  The ceil case
comment|// doesn't need this because it reads the next arc
comment|// instead:
while|while
condition|(
literal|true
condition|)
block|{
comment|// First, walk backwards until we find a first arc
comment|// that's before our target label:
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|getArc
argument_list|(
name|upto
operator|-
literal|1
argument_list|)
argument_list|,
name|arc
argument_list|)
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|label
operator|<
name|targetLabel
condition|)
block|{
comment|// Then, scan forwards to the arc just before
comment|// the targetLabel:
while|while
condition|(
operator|!
name|arc
operator|.
name|isLast
argument_list|()
operator|&&
name|fst
operator|.
name|readNextArcLabel
argument_list|(
name|arc
argument_list|)
operator|<
name|targetLabel
condition|)
block|{
name|fst
operator|.
name|readNextArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
block|}
name|pushLast
argument_list|()
expr_stmt|;
return|return;
block|}
name|upto
operator|--
expr_stmt|;
if|if
condition|(
name|upto
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|targetLabel
operator|=
name|getTargetLabel
argument_list|()
expr_stmt|;
name|arc
operator|=
name|getArc
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|arc
operator|.
name|isLast
argument_list|()
condition|)
block|{
comment|//System.out.println("  check next label=" + fst.readNextArcLabel(arc) + " (" + (char) fst.readNextArcLabel(arc) + ")");
if|if
condition|(
name|fst
operator|.
name|readNextArcLabel
argument_list|(
name|arc
argument_list|)
operator|>
name|targetLabel
condition|)
block|{
name|pushLast
argument_list|()
expr_stmt|;
return|return;
block|}
else|else
block|{
comment|// keep scanning
name|fst
operator|.
name|readNextArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|pushLast
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
DECL|method|incr
specifier|private
name|void
name|incr
parameter_list|()
block|{
name|upto
operator|++
expr_stmt|;
name|grow
argument_list|()
expr_stmt|;
if|if
condition|(
name|arcs
operator|.
name|length
operator|<=
name|upto
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
index|[]
name|newArcs
init|=
operator|new
name|FST
operator|.
name|Arc
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|upto
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|arcs
argument_list|,
literal|0
argument_list|,
name|newArcs
argument_list|,
literal|0
argument_list|,
name|arcs
operator|.
name|length
argument_list|)
expr_stmt|;
name|arcs
operator|=
name|newArcs
expr_stmt|;
block|}
if|if
condition|(
name|output
operator|.
name|length
operator|<=
name|upto
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|T
index|[]
name|newOutput
init|=
operator|(
name|T
index|[]
operator|)
operator|new
name|Object
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|upto
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|newOutput
argument_list|,
literal|0
argument_list|,
name|output
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|=
name|newOutput
expr_stmt|;
block|}
block|}
comment|// Appends current arc, and then recurses from its target,
comment|// appending first arc all the way to the final node
DECL|method|pushFirst
specifier|private
name|void
name|pushFirst
parameter_list|()
throws|throws
name|IOException
block|{
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|arcs
index|[
name|upto
index|]
decl_stmt|;
assert|assert
name|arc
operator|!=
literal|null
assert|;
while|while
condition|(
literal|true
condition|)
block|{
name|output
index|[
name|upto
index|]
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
index|[
name|upto
operator|-
literal|1
index|]
argument_list|,
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|label
operator|==
name|FST
operator|.
name|END_LABEL
condition|)
block|{
comment|// Final node
break|break;
block|}
comment|//System.out.println("  pushFirst label=" + (char) arc.label + " upto=" + upto + " output=" + fst.outputs.outputToString(output[upto]));
name|setCurrentLabel
argument_list|(
name|arc
operator|.
name|label
argument_list|)
expr_stmt|;
name|incr
argument_list|()
expr_stmt|;
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|nextArc
init|=
name|getArc
argument_list|(
name|upto
argument_list|)
decl_stmt|;
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|nextArc
argument_list|)
expr_stmt|;
name|arc
operator|=
name|nextArc
expr_stmt|;
block|}
block|}
comment|// Recurses from current arc, appending last arc all the
comment|// way to the first final node
DECL|method|pushLast
specifier|private
name|void
name|pushLast
parameter_list|()
throws|throws
name|IOException
block|{
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|arc
init|=
name|arcs
index|[
name|upto
index|]
decl_stmt|;
assert|assert
name|arc
operator|!=
literal|null
assert|;
while|while
condition|(
literal|true
condition|)
block|{
name|setCurrentLabel
argument_list|(
name|arc
operator|.
name|label
argument_list|)
expr_stmt|;
name|output
index|[
name|upto
index|]
operator|=
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|output
index|[
name|upto
operator|-
literal|1
index|]
argument_list|,
name|arc
operator|.
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|arc
operator|.
name|label
operator|==
name|FST
operator|.
name|END_LABEL
condition|)
block|{
comment|// Final node
break|break;
block|}
name|incr
argument_list|()
expr_stmt|;
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|nextArc
init|=
name|getArc
argument_list|(
name|upto
argument_list|)
decl_stmt|;
name|fst
operator|.
name|readFirstTargetArc
argument_list|(
name|arc
argument_list|,
name|nextArc
argument_list|)
expr_stmt|;
name|arc
operator|=
name|nextArc
expr_stmt|;
while|while
condition|(
operator|!
name|arc
operator|.
name|isLast
argument_list|()
condition|)
block|{
name|fst
operator|.
name|readNextArc
argument_list|(
name|arc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getArc
specifier|private
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|getArc
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
if|if
condition|(
name|arcs
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
name|arcs
index|[
name|idx
index|]
operator|=
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
expr_stmt|;
block|}
return|return
name|arcs
index|[
name|idx
index|]
return|;
block|}
block|}
end_class
end_unit
