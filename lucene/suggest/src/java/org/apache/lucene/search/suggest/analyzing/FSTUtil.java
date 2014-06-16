begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.analyzing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|analyzing
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
name|ArrayList
import|;
end_import
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IntsRef
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
name|automaton
operator|.
name|LightAutomaton
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
name|automaton
operator|.
name|Transition
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|Util
import|;
end_import
begin_comment
comment|// TODO: move to core?  nobody else uses it yet though...
end_comment
begin_comment
comment|/**  * Exposes a utility method to enumerate all paths  * intersecting an {@link Automaton} with an {@link FST}.  */
end_comment
begin_class
DECL|class|FSTUtil
specifier|public
class|class
name|FSTUtil
block|{
DECL|method|FSTUtil
specifier|private
name|FSTUtil
parameter_list|()
block|{   }
comment|/** Holds a pair (automaton, fst) of states and accumulated output in the intersected machine. */
DECL|class|Path
specifier|public
specifier|static
specifier|final
class|class
name|Path
parameter_list|<
name|T
parameter_list|>
block|{
comment|/** Node in the automaton where path ends: */
DECL|field|state
specifier|public
specifier|final
name|int
name|state
decl_stmt|;
comment|/** Node in the FST where path ends: */
DECL|field|fstNode
specifier|public
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|fstNode
decl_stmt|;
comment|/** Output of the path so far: */
DECL|field|output
name|T
name|output
decl_stmt|;
comment|/** Input of the path so far: */
DECL|field|input
specifier|public
specifier|final
name|IntsRef
name|input
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|Path
specifier|public
name|Path
parameter_list|(
name|int
name|state
parameter_list|,
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|fstNode
parameter_list|,
name|T
name|output
parameter_list|,
name|IntsRef
name|input
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|fstNode
operator|=
name|fstNode
expr_stmt|;
name|this
operator|.
name|output
operator|=
name|output
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
block|}
block|}
comment|/**    * Enumerates all minimal prefix paths in the automaton that also intersect the FST,    * accumulating the FST end node and output for each path.    */
DECL|method|intersectPrefixPaths
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|Path
argument_list|<
name|T
argument_list|>
argument_list|>
name|intersectPrefixPaths
parameter_list|(
name|LightAutomaton
name|a
parameter_list|,
name|FST
argument_list|<
name|T
argument_list|>
name|fst
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|a
operator|.
name|isDeterministic
argument_list|()
assert|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|<
name|T
argument_list|>
argument_list|>
name|queue
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|<
name|T
argument_list|>
argument_list|>
name|endNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|getNumStates
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|endNodes
return|;
block|}
name|queue
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|<>
argument_list|(
literal|0
argument_list|,
name|fst
operator|.
name|getFirstArc
argument_list|(
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
argument_list|)
argument_list|,
name|fst
operator|.
name|outputs
operator|.
name|getNoOutput
argument_list|()
argument_list|,
operator|new
name|IntsRef
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|FST
operator|.
name|BytesReader
name|fstReader
init|=
name|fst
operator|.
name|getBytesReader
argument_list|()
decl_stmt|;
name|Transition
name|t
init|=
operator|new
name|Transition
argument_list|()
decl_stmt|;
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
specifier|final
name|Path
argument_list|<
name|T
argument_list|>
name|path
init|=
name|queue
operator|.
name|remove
argument_list|(
name|queue
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|isAccept
argument_list|(
name|path
operator|.
name|state
argument_list|)
condition|)
block|{
name|endNodes
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// we can stop here if we accept this path,
comment|// we accept all further paths too
continue|continue;
block|}
name|IntsRef
name|currentInput
init|=
name|path
operator|.
name|input
decl_stmt|;
name|int
name|count
init|=
name|a
operator|.
name|initTransition
argument_list|(
name|path
operator|.
name|state
argument_list|,
name|t
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|a
operator|.
name|getNextTransition
argument_list|(
name|t
argument_list|)
expr_stmt|;
specifier|final
name|int
name|min
init|=
name|t
operator|.
name|min
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|t
operator|.
name|max
decl_stmt|;
if|if
condition|(
name|min
operator|==
name|max
condition|)
block|{
specifier|final
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|nextArc
init|=
name|fst
operator|.
name|findTargetArc
argument_list|(
name|t
operator|.
name|min
argument_list|,
name|path
operator|.
name|fstNode
argument_list|,
name|scratchArc
argument_list|,
name|fstReader
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextArc
operator|!=
literal|null
condition|)
block|{
specifier|final
name|IntsRef
name|newInput
init|=
operator|new
name|IntsRef
argument_list|(
name|currentInput
operator|.
name|length
operator|+
literal|1
argument_list|)
decl_stmt|;
name|newInput
operator|.
name|copyInts
argument_list|(
name|currentInput
argument_list|)
expr_stmt|;
name|newInput
operator|.
name|ints
index|[
name|currentInput
operator|.
name|length
index|]
operator|=
name|t
operator|.
name|min
expr_stmt|;
name|newInput
operator|.
name|length
operator|=
name|currentInput
operator|.
name|length
operator|+
literal|1
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|<>
argument_list|(
name|t
operator|.
name|dest
argument_list|,
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
operator|.
name|copyFrom
argument_list|(
name|nextArc
argument_list|)
argument_list|,
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|path
operator|.
name|output
argument_list|,
name|nextArc
operator|.
name|output
argument_list|)
argument_list|,
name|newInput
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// TODO: if this transition's TO state is accepting, and
comment|// it accepts the entire range possible in the FST (ie. 0 to 255),
comment|// we can simply use the prefix as the accepted state instead of
comment|// looking up all the ranges and terminate early
comment|// here.  This just shifts the work from one queue
comment|// (this one) to another (the completion search
comment|// done in AnalyzingSuggester).
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
name|nextArc
init|=
name|Util
operator|.
name|readCeilArc
argument_list|(
name|min
argument_list|,
name|fst
argument_list|,
name|path
operator|.
name|fstNode
argument_list|,
name|scratchArc
argument_list|,
name|fstReader
argument_list|)
decl_stmt|;
while|while
condition|(
name|nextArc
operator|!=
literal|null
operator|&&
name|nextArc
operator|.
name|label
operator|<=
name|max
condition|)
block|{
assert|assert
name|nextArc
operator|.
name|label
operator|<=
name|max
assert|;
assert|assert
name|nextArc
operator|.
name|label
operator|>=
name|min
operator|:
name|nextArc
operator|.
name|label
operator|+
literal|" "
operator|+
name|min
assert|;
specifier|final
name|IntsRef
name|newInput
init|=
operator|new
name|IntsRef
argument_list|(
name|currentInput
operator|.
name|length
operator|+
literal|1
argument_list|)
decl_stmt|;
name|newInput
operator|.
name|copyInts
argument_list|(
name|currentInput
argument_list|)
expr_stmt|;
name|newInput
operator|.
name|ints
index|[
name|currentInput
operator|.
name|length
index|]
operator|=
name|nextArc
operator|.
name|label
expr_stmt|;
name|newInput
operator|.
name|length
operator|=
name|currentInput
operator|.
name|length
operator|+
literal|1
expr_stmt|;
name|queue
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|<>
argument_list|(
name|t
operator|.
name|dest
argument_list|,
operator|new
name|FST
operator|.
name|Arc
argument_list|<
name|T
argument_list|>
argument_list|()
operator|.
name|copyFrom
argument_list|(
name|nextArc
argument_list|)
argument_list|,
name|fst
operator|.
name|outputs
operator|.
name|add
argument_list|(
name|path
operator|.
name|output
argument_list|,
name|nextArc
operator|.
name|output
argument_list|)
argument_list|,
name|newInput
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|label
init|=
name|nextArc
operator|.
name|label
decl_stmt|;
comment|// used in assert
name|nextArc
operator|=
name|nextArc
operator|.
name|isLast
argument_list|()
condition|?
literal|null
else|:
name|fst
operator|.
name|readNextRealArc
argument_list|(
name|nextArc
argument_list|,
name|fstReader
argument_list|)
expr_stmt|;
assert|assert
name|nextArc
operator|==
literal|null
operator|||
name|label
operator|<
name|nextArc
operator|.
name|label
operator|:
literal|"last: "
operator|+
name|label
operator|+
literal|" next: "
operator|+
name|nextArc
operator|.
name|label
assert|;
block|}
block|}
block|}
block|}
return|return
name|endNodes
return|;
block|}
block|}
end_class
end_unit
