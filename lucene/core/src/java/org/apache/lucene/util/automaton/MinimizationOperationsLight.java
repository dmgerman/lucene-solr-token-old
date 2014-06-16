begin_unit
begin_comment
comment|/*  * dk.brics.automaton  *   * Copyright (c) 2001-2009 Anders Moeller  * All rights reserved.  *   * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in the  *    documentation and/or other materials provided with the distribution.  * 3. The name of the author may not be used to endorse or promote products  *    derived from this software without specific prior written permission.  *   * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR  * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,  * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT  * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,  * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY  * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF  * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util.automaton
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
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import
begin_comment
comment|/**  * Operations for minimizing automata.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|MinimizationOperationsLight
specifier|final
specifier|public
class|class
name|MinimizationOperationsLight
block|{
DECL|method|MinimizationOperationsLight
specifier|private
name|MinimizationOperationsLight
parameter_list|()
block|{}
comment|/**    * Minimizes (and determinizes if not already deterministic) the given    * automaton.    *     * @see Automaton#setMinimization(int)    */
DECL|method|minimize
specifier|public
specifier|static
name|LightAutomaton
name|minimize
parameter_list|(
name|LightAutomaton
name|a
parameter_list|)
block|{
return|return
name|minimizeHopcroft
argument_list|(
name|a
argument_list|)
return|;
block|}
comment|/**    * Minimizes the given automaton using Hopcroft's algorithm.    */
DECL|method|minimizeHopcroft
specifier|public
specifier|static
name|LightAutomaton
name|minimizeHopcroft
parameter_list|(
name|LightAutomaton
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|getNumStates
argument_list|()
operator|==
literal|0
operator|||
operator|(
name|a
operator|.
name|isAccept
argument_list|(
literal|0
argument_list|)
operator|==
literal|false
operator|&&
name|a
operator|.
name|getNumTransitions
argument_list|(
literal|0
argument_list|)
operator|==
literal|0
operator|)
condition|)
block|{
comment|// Fastmatch for common case
return|return
operator|new
name|LightAutomaton
argument_list|()
return|;
block|}
name|a
operator|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a
argument_list|)
expr_stmt|;
comment|//a.writeDot("adet");
if|if
condition|(
name|a
operator|.
name|getNumTransitions
argument_list|(
literal|0
argument_list|)
operator|==
literal|1
condition|)
block|{
name|Transition
name|t
init|=
operator|new
name|Transition
argument_list|()
decl_stmt|;
name|a
operator|.
name|getTransition
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|dest
operator|==
literal|0
operator|&&
name|t
operator|.
name|min
operator|==
name|Character
operator|.
name|MIN_CODE_POINT
operator|&&
name|t
operator|.
name|max
operator|==
name|Character
operator|.
name|MAX_CODE_POINT
condition|)
block|{
comment|// Accepts all strings
return|return
name|a
return|;
block|}
block|}
name|a
operator|=
name|a
operator|.
name|totalize
argument_list|()
expr_stmt|;
comment|//a.writeDot("atot");
comment|// initialize data structures
specifier|final
name|int
index|[]
name|sigma
init|=
name|a
operator|.
name|getStartPoints
argument_list|()
decl_stmt|;
specifier|final
name|int
name|sigmaLen
init|=
name|sigma
operator|.
name|length
decl_stmt|,
name|statesLen
init|=
name|a
operator|.
name|getNumStates
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|final
name|ArrayList
argument_list|<
name|Integer
argument_list|>
index|[]
index|[]
name|reverse
init|=
operator|(
name|ArrayList
argument_list|<
name|Integer
argument_list|>
index|[]
index|[]
operator|)
operator|new
name|ArrayList
index|[
name|statesLen
index|]
index|[
name|sigmaLen
index|]
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|final
name|HashSet
argument_list|<
name|Integer
argument_list|>
index|[]
name|partition
init|=
operator|(
name|HashSet
argument_list|<
name|Integer
argument_list|>
index|[]
operator|)
operator|new
name|HashSet
index|[
name|statesLen
index|]
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
specifier|final
name|ArrayList
argument_list|<
name|Integer
argument_list|>
index|[]
name|splitblock
init|=
operator|(
name|ArrayList
argument_list|<
name|Integer
argument_list|>
index|[]
operator|)
operator|new
name|ArrayList
index|[
name|statesLen
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|block
init|=
operator|new
name|int
index|[
name|statesLen
index|]
decl_stmt|;
specifier|final
name|StateList
index|[]
index|[]
name|active
init|=
operator|new
name|StateList
index|[
name|statesLen
index|]
index|[
name|sigmaLen
index|]
decl_stmt|;
specifier|final
name|StateListNode
index|[]
index|[]
name|active2
init|=
operator|new
name|StateListNode
index|[
name|statesLen
index|]
index|[
name|sigmaLen
index|]
decl_stmt|;
specifier|final
name|LinkedList
argument_list|<
name|IntPair
argument_list|>
name|pending
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|BitSet
name|pending2
init|=
operator|new
name|BitSet
argument_list|(
name|sigmaLen
operator|*
name|statesLen
argument_list|)
decl_stmt|;
specifier|final
name|BitSet
name|split
init|=
operator|new
name|BitSet
argument_list|(
name|statesLen
argument_list|)
decl_stmt|,
name|refine
init|=
operator|new
name|BitSet
argument_list|(
name|statesLen
argument_list|)
decl_stmt|,
name|refine2
init|=
operator|new
name|BitSet
argument_list|(
name|statesLen
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|q
init|=
literal|0
init|;
name|q
operator|<
name|statesLen
condition|;
name|q
operator|++
control|)
block|{
name|splitblock
index|[
name|q
index|]
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|partition
index|[
name|q
index|]
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|sigmaLen
condition|;
name|x
operator|++
control|)
block|{
name|active
index|[
name|q
index|]
index|[
name|x
index|]
operator|=
operator|new
name|StateList
argument_list|()
expr_stmt|;
block|}
block|}
comment|// find initial partition and reverse edges
for|for
control|(
name|int
name|q
init|=
literal|0
init|;
name|q
operator|<
name|statesLen
condition|;
name|q
operator|++
control|)
block|{
specifier|final
name|int
name|j
init|=
name|a
operator|.
name|isAccept
argument_list|(
name|q
argument_list|)
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|partition
index|[
name|j
index|]
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
name|block
index|[
name|q
index|]
operator|=
name|j
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|sigmaLen
condition|;
name|x
operator|++
control|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|Integer
argument_list|>
index|[]
name|r
init|=
name|reverse
index|[
name|a
operator|.
name|step
argument_list|(
name|q
argument_list|,
name|sigma
index|[
name|x
index|]
argument_list|)
index|]
decl_stmt|;
if|if
condition|(
name|r
index|[
name|x
index|]
operator|==
literal|null
condition|)
block|{
name|r
index|[
name|x
index|]
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|r
index|[
name|x
index|]
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
comment|// initialize active sets
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
literal|1
condition|;
name|j
operator|++
control|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|sigmaLen
condition|;
name|x
operator|++
control|)
block|{
for|for
control|(
name|int
name|q
range|:
name|partition
index|[
name|j
index|]
control|)
block|{
if|if
condition|(
name|reverse
index|[
name|q
index|]
index|[
name|x
index|]
operator|!=
literal|null
condition|)
block|{
name|active2
index|[
name|q
index|]
index|[
name|x
index|]
operator|=
name|active
index|[
name|j
index|]
index|[
name|x
index|]
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// initialize pending
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|sigmaLen
condition|;
name|x
operator|++
control|)
block|{
specifier|final
name|int
name|j
init|=
operator|(
name|active
index|[
literal|0
index|]
index|[
name|x
index|]
operator|.
name|size
operator|<=
name|active
index|[
literal|1
index|]
index|[
name|x
index|]
operator|.
name|size
operator|)
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|pending
operator|.
name|add
argument_list|(
operator|new
name|IntPair
argument_list|(
name|j
argument_list|,
name|x
argument_list|)
argument_list|)
expr_stmt|;
name|pending2
operator|.
name|set
argument_list|(
name|x
operator|*
name|statesLen
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
comment|// process pending until fixed point
name|int
name|k
init|=
literal|2
decl_stmt|;
comment|//System.out.println("start min");
while|while
condition|(
operator|!
name|pending
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//System.out.println("  cycle pending");
specifier|final
name|IntPair
name|ip
init|=
name|pending
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
specifier|final
name|int
name|p
init|=
name|ip
operator|.
name|n1
decl_stmt|;
specifier|final
name|int
name|x
init|=
name|ip
operator|.
name|n2
decl_stmt|;
comment|//System.out.println("    pop n1=" + ip.n1 + " n2=" + ip.n2);
name|pending2
operator|.
name|clear
argument_list|(
name|x
operator|*
name|statesLen
operator|+
name|p
argument_list|)
expr_stmt|;
comment|// find states that need to be split off their blocks
for|for
control|(
name|StateListNode
name|m
init|=
name|active
index|[
name|p
index|]
index|[
name|x
index|]
operator|.
name|first
init|;
name|m
operator|!=
literal|null
condition|;
name|m
operator|=
name|m
operator|.
name|next
control|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|r
init|=
name|reverse
index|[
name|m
operator|.
name|q
index|]
index|[
name|x
index|]
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
range|:
name|r
control|)
block|{
if|if
condition|(
operator|!
name|split
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|split
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
specifier|final
name|int
name|j
init|=
name|block
index|[
name|i
index|]
decl_stmt|;
name|splitblock
index|[
name|j
index|]
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|refine2
operator|.
name|get
argument_list|(
name|j
argument_list|)
condition|)
block|{
name|refine2
operator|.
name|set
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|refine
operator|.
name|set
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|// refine blocks
for|for
control|(
name|int
name|j
init|=
name|refine
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|j
operator|>=
literal|0
condition|;
name|j
operator|=
name|refine
operator|.
name|nextSetBit
argument_list|(
name|j
operator|+
literal|1
argument_list|)
control|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|sb
init|=
name|splitblock
index|[
name|j
index|]
decl_stmt|;
if|if
condition|(
name|sb
operator|.
name|size
argument_list|()
operator|<
name|partition
index|[
name|j
index|]
operator|.
name|size
argument_list|()
condition|)
block|{
specifier|final
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|b1
init|=
name|partition
index|[
name|j
index|]
decl_stmt|;
specifier|final
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|b2
init|=
name|partition
index|[
name|k
index|]
decl_stmt|;
for|for
control|(
name|int
name|s
range|:
name|sb
control|)
block|{
name|b1
operator|.
name|remove
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|b2
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|block
index|[
name|s
index|]
operator|=
name|k
expr_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|sigmaLen
condition|;
name|c
operator|++
control|)
block|{
specifier|final
name|StateListNode
name|sn
init|=
name|active2
index|[
name|s
index|]
index|[
name|c
index|]
decl_stmt|;
if|if
condition|(
name|sn
operator|!=
literal|null
operator|&&
name|sn
operator|.
name|sl
operator|==
name|active
index|[
name|j
index|]
index|[
name|c
index|]
condition|)
block|{
name|sn
operator|.
name|remove
argument_list|()
expr_stmt|;
name|active2
index|[
name|s
index|]
index|[
name|c
index|]
operator|=
name|active
index|[
name|k
index|]
index|[
name|c
index|]
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// update pending
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|sigmaLen
condition|;
name|c
operator|++
control|)
block|{
specifier|final
name|int
name|aj
init|=
name|active
index|[
name|j
index|]
index|[
name|c
index|]
operator|.
name|size
decl_stmt|,
name|ak
init|=
name|active
index|[
name|k
index|]
index|[
name|c
index|]
operator|.
name|size
decl_stmt|,
name|ofs
init|=
name|c
operator|*
name|statesLen
decl_stmt|;
if|if
condition|(
operator|!
name|pending2
operator|.
name|get
argument_list|(
name|ofs
operator|+
name|j
argument_list|)
operator|&&
literal|0
operator|<
name|aj
operator|&&
name|aj
operator|<=
name|ak
condition|)
block|{
name|pending2
operator|.
name|set
argument_list|(
name|ofs
operator|+
name|j
argument_list|)
expr_stmt|;
name|pending
operator|.
name|add
argument_list|(
operator|new
name|IntPair
argument_list|(
name|j
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pending2
operator|.
name|set
argument_list|(
name|ofs
operator|+
name|k
argument_list|)
expr_stmt|;
name|pending
operator|.
name|add
argument_list|(
operator|new
name|IntPair
argument_list|(
name|k
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|k
operator|++
expr_stmt|;
block|}
name|refine2
operator|.
name|clear
argument_list|(
name|j
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|s
range|:
name|sb
control|)
block|{
name|split
operator|.
name|clear
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|refine
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|LightAutomaton
name|result
init|=
operator|new
name|LightAutomaton
argument_list|()
decl_stmt|;
name|Transition
name|t
init|=
operator|new
name|Transition
argument_list|()
decl_stmt|;
comment|//System.out.println("  k=" + k);
comment|// make a new state for each equivalence class, set initial state
name|int
index|[]
name|stateMap
init|=
operator|new
name|int
index|[
name|statesLen
index|]
decl_stmt|;
name|int
index|[]
name|stateRep
init|=
operator|new
name|int
index|[
name|k
index|]
decl_stmt|;
name|result
operator|.
name|createState
argument_list|()
expr_stmt|;
comment|//System.out.println("min: k=" + k);
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|k
condition|;
name|n
operator|++
control|)
block|{
comment|//System.out.println("    n=" + n);
name|boolean
name|isInitial
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|q
range|:
name|partition
index|[
name|n
index|]
control|)
block|{
if|if
condition|(
name|q
operator|==
literal|0
condition|)
block|{
name|isInitial
operator|=
literal|true
expr_stmt|;
comment|//System.out.println("    isInitial!");
break|break;
block|}
block|}
name|int
name|newState
decl_stmt|;
if|if
condition|(
name|isInitial
condition|)
block|{
name|newState
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|newState
operator|=
name|result
operator|.
name|createState
argument_list|()
expr_stmt|;
block|}
comment|//System.out.println("  newState=" + newState);
for|for
control|(
name|int
name|q
range|:
name|partition
index|[
name|n
index|]
control|)
block|{
name|stateMap
index|[
name|q
index|]
operator|=
name|newState
expr_stmt|;
comment|//System.out.println("      q=" + q + " isAccept?=" + a.isAccept(q));
name|result
operator|.
name|setAccept
argument_list|(
name|newState
argument_list|,
name|a
operator|.
name|isAccept
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
name|stateRep
index|[
name|newState
index|]
operator|=
name|q
expr_stmt|;
comment|// select representative
block|}
block|}
comment|// build transitions and set acceptance
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|k
condition|;
name|n
operator|++
control|)
block|{
name|int
name|numTransitions
init|=
name|a
operator|.
name|initTransition
argument_list|(
name|stateRep
index|[
name|n
index|]
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
name|numTransitions
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
comment|//System.out.println("  add trans");
name|result
operator|.
name|addTransition
argument_list|(
name|n
argument_list|,
name|stateMap
index|[
name|t
operator|.
name|dest
index|]
argument_list|,
name|t
operator|.
name|min
argument_list|,
name|t
operator|.
name|max
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|.
name|finishState
argument_list|()
expr_stmt|;
comment|//System.out.println(result.getNumStates() + " states");
return|return
name|BasicOperations
operator|.
name|removeDeadStates
argument_list|(
name|result
argument_list|)
return|;
block|}
DECL|class|IntPair
specifier|static
specifier|final
class|class
name|IntPair
block|{
DECL|field|n1
DECL|field|n2
specifier|final
name|int
name|n1
decl_stmt|,
name|n2
decl_stmt|;
DECL|method|IntPair
name|IntPair
parameter_list|(
name|int
name|n1
parameter_list|,
name|int
name|n2
parameter_list|)
block|{
name|this
operator|.
name|n1
operator|=
name|n1
expr_stmt|;
name|this
operator|.
name|n2
operator|=
name|n2
expr_stmt|;
block|}
block|}
DECL|class|StateList
specifier|static
specifier|final
class|class
name|StateList
block|{
DECL|field|size
name|int
name|size
decl_stmt|;
DECL|field|first
DECL|field|last
name|StateListNode
name|first
decl_stmt|,
name|last
decl_stmt|;
DECL|method|add
name|StateListNode
name|add
parameter_list|(
name|int
name|q
parameter_list|)
block|{
return|return
operator|new
name|StateListNode
argument_list|(
name|q
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
DECL|class|StateListNode
specifier|static
specifier|final
class|class
name|StateListNode
block|{
DECL|field|q
specifier|final
name|int
name|q
decl_stmt|;
DECL|field|next
DECL|field|prev
name|StateListNode
name|next
decl_stmt|,
name|prev
decl_stmt|;
DECL|field|sl
specifier|final
name|StateList
name|sl
decl_stmt|;
DECL|method|StateListNode
name|StateListNode
parameter_list|(
name|int
name|q
parameter_list|,
name|StateList
name|sl
parameter_list|)
block|{
name|this
operator|.
name|q
operator|=
name|q
expr_stmt|;
name|this
operator|.
name|sl
operator|=
name|sl
expr_stmt|;
if|if
condition|(
name|sl
operator|.
name|size
operator|++
operator|==
literal|0
condition|)
name|sl
operator|.
name|first
operator|=
name|sl
operator|.
name|last
operator|=
name|this
expr_stmt|;
else|else
block|{
name|sl
operator|.
name|last
operator|.
name|next
operator|=
name|this
expr_stmt|;
name|prev
operator|=
name|sl
operator|.
name|last
expr_stmt|;
name|sl
operator|.
name|last
operator|=
name|this
expr_stmt|;
block|}
block|}
DECL|method|remove
name|void
name|remove
parameter_list|()
block|{
name|sl
operator|.
name|size
operator|--
expr_stmt|;
if|if
condition|(
name|sl
operator|.
name|first
operator|==
name|this
condition|)
name|sl
operator|.
name|first
operator|=
name|next
expr_stmt|;
else|else
name|prev
operator|.
name|next
operator|=
name|next
expr_stmt|;
if|if
condition|(
name|sl
operator|.
name|last
operator|==
name|this
condition|)
name|sl
operator|.
name|last
operator|=
name|prev
expr_stmt|;
else|else
name|next
operator|.
name|prev
operator|=
name|prev
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
