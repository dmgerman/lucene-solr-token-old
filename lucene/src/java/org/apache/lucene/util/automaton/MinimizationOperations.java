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
name|ArrayList
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Operations for minimizing automata.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|MinimizationOperations
specifier|final
specifier|public
class|class
name|MinimizationOperations
block|{
DECL|method|MinimizationOperations
specifier|private
name|MinimizationOperations
parameter_list|()
block|{}
comment|/**    * Minimizes (and determinizes if not already deterministic) the given    * automaton.    *     * @see Automaton#setMinimization(int)    */
DECL|method|minimize
specifier|public
specifier|static
name|void
name|minimize
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
if|if
condition|(
operator|!
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
block|{
name|minimizeHopcroft
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
comment|// recompute hash code
name|a
operator|.
name|hash_code
operator|=
name|a
operator|.
name|getNumberOfStates
argument_list|()
operator|*
literal|3
operator|+
name|a
operator|.
name|getNumberOfTransitions
argument_list|()
operator|*
literal|2
expr_stmt|;
if|if
condition|(
name|a
operator|.
name|hash_code
operator|==
literal|0
condition|)
name|a
operator|.
name|hash_code
operator|=
literal|1
expr_stmt|;
block|}
DECL|method|initialize
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|initialize
parameter_list|(
name|ArrayList
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|int
name|size
parameter_list|)
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
name|size
condition|;
name|i
operator|++
control|)
name|list
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Minimizes the given automaton using Hopcroft's algorithm.    */
DECL|method|minimizeHopcroft
specifier|public
specifier|static
name|void
name|minimizeHopcroft
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
name|a
operator|.
name|determinize
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|Transition
argument_list|>
name|tr
init|=
name|a
operator|.
name|initial
operator|.
name|getTransitions
argument_list|()
decl_stmt|;
if|if
condition|(
name|tr
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Transition
name|t
init|=
name|tr
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|to
operator|==
name|a
operator|.
name|initial
operator|&&
name|t
operator|.
name|min
operator|==
name|Character
operator|.
name|MIN_VALUE
operator|&&
name|t
operator|.
name|max
operator|==
name|Character
operator|.
name|MAX_VALUE
condition|)
return|return;
block|}
name|a
operator|.
name|totalize
argument_list|()
expr_stmt|;
comment|// make arrays for numbered states and effective alphabet
name|Set
argument_list|<
name|State
argument_list|>
name|ss
init|=
name|a
operator|.
name|getStates
argument_list|()
decl_stmt|;
name|State
index|[]
name|states
init|=
operator|new
name|State
index|[
name|ss
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|number
init|=
literal|0
decl_stmt|;
for|for
control|(
name|State
name|q
range|:
name|ss
control|)
block|{
name|states
index|[
name|number
index|]
operator|=
name|q
expr_stmt|;
name|q
operator|.
name|number
operator|=
name|number
operator|++
expr_stmt|;
block|}
name|char
index|[]
name|sigma
init|=
name|a
operator|.
name|getStartPoints
argument_list|()
decl_stmt|;
comment|// initialize data structures
name|ArrayList
argument_list|<
name|ArrayList
argument_list|<
name|LinkedList
argument_list|<
name|State
argument_list|>
argument_list|>
argument_list|>
name|reverse
init|=
operator|new
name|ArrayList
argument_list|<
name|ArrayList
argument_list|<
name|LinkedList
argument_list|<
name|State
argument_list|>
argument_list|>
argument_list|>
argument_list|()
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
name|states
operator|.
name|length
condition|;
name|q
operator|++
control|)
block|{
name|ArrayList
argument_list|<
name|LinkedList
argument_list|<
name|State
argument_list|>
argument_list|>
name|v
init|=
operator|new
name|ArrayList
argument_list|<
name|LinkedList
argument_list|<
name|State
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|initialize
argument_list|(
name|v
argument_list|,
name|sigma
operator|.
name|length
argument_list|)
expr_stmt|;
name|reverse
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|boolean
index|[]
index|[]
name|reverse_nonempty
init|=
operator|new
name|boolean
index|[
name|states
operator|.
name|length
index|]
index|[
name|sigma
operator|.
name|length
index|]
decl_stmt|;
name|ArrayList
argument_list|<
name|LinkedList
argument_list|<
name|State
argument_list|>
argument_list|>
name|partition
init|=
operator|new
name|ArrayList
argument_list|<
name|LinkedList
argument_list|<
name|State
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|initialize
argument_list|(
name|partition
argument_list|,
name|states
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
index|[]
name|block
init|=
operator|new
name|int
index|[
name|states
operator|.
name|length
index|]
decl_stmt|;
name|StateList
index|[]
index|[]
name|active
init|=
operator|new
name|StateList
index|[
name|states
operator|.
name|length
index|]
index|[
name|sigma
operator|.
name|length
index|]
decl_stmt|;
name|StateListNode
index|[]
index|[]
name|active2
init|=
operator|new
name|StateListNode
index|[
name|states
operator|.
name|length
index|]
index|[
name|sigma
operator|.
name|length
index|]
decl_stmt|;
name|LinkedList
argument_list|<
name|IntPair
argument_list|>
name|pending
init|=
operator|new
name|LinkedList
argument_list|<
name|IntPair
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
index|[]
index|[]
name|pending2
init|=
operator|new
name|boolean
index|[
name|sigma
operator|.
name|length
index|]
index|[
name|states
operator|.
name|length
index|]
decl_stmt|;
name|ArrayList
argument_list|<
name|State
argument_list|>
name|split
init|=
operator|new
name|ArrayList
argument_list|<
name|State
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
index|[]
name|split2
init|=
operator|new
name|boolean
index|[
name|states
operator|.
name|length
index|]
decl_stmt|;
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|refine
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
index|[]
name|refine2
init|=
operator|new
name|boolean
index|[
name|states
operator|.
name|length
index|]
decl_stmt|;
name|ArrayList
argument_list|<
name|ArrayList
argument_list|<
name|State
argument_list|>
argument_list|>
name|splitblock
init|=
operator|new
name|ArrayList
argument_list|<
name|ArrayList
argument_list|<
name|State
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|initialize
argument_list|(
name|splitblock
argument_list|,
name|states
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|q
init|=
literal|0
init|;
name|q
operator|<
name|states
operator|.
name|length
condition|;
name|q
operator|++
control|)
block|{
name|splitblock
operator|.
name|set
argument_list|(
name|q
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|State
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|partition
operator|.
name|set
argument_list|(
name|q
argument_list|,
operator|new
name|LinkedList
argument_list|<
name|State
argument_list|>
argument_list|()
argument_list|)
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
name|sigma
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|reverse
operator|.
name|get
argument_list|(
name|q
argument_list|)
operator|.
name|set
argument_list|(
name|x
argument_list|,
operator|new
name|LinkedList
argument_list|<
name|State
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
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
name|states
operator|.
name|length
condition|;
name|q
operator|++
control|)
block|{
name|State
name|qq
init|=
name|states
index|[
name|q
index|]
decl_stmt|;
name|int
name|j
decl_stmt|;
if|if
condition|(
name|qq
operator|.
name|accept
condition|)
name|j
operator|=
literal|0
expr_stmt|;
else|else
name|j
operator|=
literal|1
expr_stmt|;
name|partition
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|add
argument_list|(
name|qq
argument_list|)
expr_stmt|;
name|block
index|[
name|qq
operator|.
name|number
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
name|sigma
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|char
name|y
init|=
name|sigma
index|[
name|x
index|]
decl_stmt|;
name|State
name|p
init|=
name|qq
operator|.
name|step
argument_list|(
name|y
argument_list|)
decl_stmt|;
name|reverse
operator|.
name|get
argument_list|(
name|p
operator|.
name|number
argument_list|)
operator|.
name|get
argument_list|(
name|x
argument_list|)
operator|.
name|add
argument_list|(
name|qq
argument_list|)
expr_stmt|;
name|reverse_nonempty
index|[
name|p
operator|.
name|number
index|]
index|[
name|x
index|]
operator|=
literal|true
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
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|sigma
operator|.
name|length
condition|;
name|x
operator|++
control|)
for|for
control|(
name|State
name|qq
range|:
name|partition
operator|.
name|get
argument_list|(
name|j
argument_list|)
control|)
if|if
condition|(
name|reverse_nonempty
index|[
name|qq
operator|.
name|number
index|]
index|[
name|x
index|]
condition|)
name|active2
index|[
name|qq
operator|.
name|number
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
name|qq
argument_list|)
expr_stmt|;
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
name|sigma
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|int
name|a0
init|=
name|active
index|[
literal|0
index|]
index|[
name|x
index|]
operator|.
name|size
decl_stmt|;
name|int
name|a1
init|=
name|active
index|[
literal|1
index|]
index|[
name|x
index|]
operator|.
name|size
decl_stmt|;
name|int
name|j
decl_stmt|;
if|if
condition|(
name|a0
operator|<=
name|a1
condition|)
name|j
operator|=
literal|0
expr_stmt|;
else|else
name|j
operator|=
literal|1
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
name|x
argument_list|)
argument_list|)
expr_stmt|;
name|pending2
index|[
name|x
index|]
index|[
name|j
index|]
operator|=
literal|true
expr_stmt|;
block|}
comment|// process pending until fixed point
name|int
name|k
init|=
literal|2
decl_stmt|;
while|while
condition|(
operator|!
name|pending
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|IntPair
name|ip
init|=
name|pending
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|int
name|p
init|=
name|ip
operator|.
name|n1
decl_stmt|;
name|int
name|x
init|=
name|ip
operator|.
name|n2
decl_stmt|;
name|pending2
index|[
name|x
index|]
index|[
name|p
index|]
operator|=
literal|false
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
for|for
control|(
name|State
name|s
range|:
name|reverse
operator|.
name|get
argument_list|(
name|m
operator|.
name|q
operator|.
name|number
argument_list|)
operator|.
name|get
argument_list|(
name|x
argument_list|)
control|)
if|if
condition|(
operator|!
name|split2
index|[
name|s
operator|.
name|number
index|]
condition|)
block|{
name|split2
index|[
name|s
operator|.
name|number
index|]
operator|=
literal|true
expr_stmt|;
name|split
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|int
name|j
init|=
name|block
index|[
name|s
operator|.
name|number
index|]
decl_stmt|;
name|splitblock
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|refine2
index|[
name|j
index|]
condition|)
block|{
name|refine2
index|[
name|j
index|]
operator|=
literal|true
expr_stmt|;
name|refine
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
block|}
block|}
comment|// refine blocks
for|for
control|(
name|int
name|j
range|:
name|refine
control|)
block|{
if|if
condition|(
name|splitblock
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|size
argument_list|()
operator|<
name|partition
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|size
argument_list|()
condition|)
block|{
name|LinkedList
argument_list|<
name|State
argument_list|>
name|b1
init|=
name|partition
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|LinkedList
argument_list|<
name|State
argument_list|>
name|b2
init|=
name|partition
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
for|for
control|(
name|State
name|s
range|:
name|splitblock
operator|.
name|get
argument_list|(
name|j
argument_list|)
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
operator|.
name|number
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
name|sigma
operator|.
name|length
condition|;
name|c
operator|++
control|)
block|{
name|StateListNode
name|sn
init|=
name|active2
index|[
name|s
operator|.
name|number
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
operator|.
name|number
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
name|sigma
operator|.
name|length
condition|;
name|c
operator|++
control|)
block|{
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
decl_stmt|;
name|int
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
decl_stmt|;
if|if
condition|(
operator|!
name|pending2
index|[
name|c
index|]
index|[
name|j
index|]
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
index|[
name|c
index|]
index|[
name|j
index|]
operator|=
literal|true
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
index|[
name|c
index|]
index|[
name|k
index|]
operator|=
literal|true
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
for|for
control|(
name|State
name|s
range|:
name|splitblock
operator|.
name|get
argument_list|(
name|j
argument_list|)
control|)
name|split2
index|[
name|s
operator|.
name|number
index|]
operator|=
literal|false
expr_stmt|;
name|refine2
index|[
name|j
index|]
operator|=
literal|false
expr_stmt|;
name|splitblock
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|split
operator|.
name|clear
argument_list|()
expr_stmt|;
name|refine
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// make a new state for each equivalence class, set initial state
name|State
index|[]
name|newstates
init|=
operator|new
name|State
index|[
name|k
index|]
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|newstates
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
name|State
name|s
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
name|newstates
index|[
name|n
index|]
operator|=
name|s
expr_stmt|;
for|for
control|(
name|State
name|q
range|:
name|partition
operator|.
name|get
argument_list|(
name|n
argument_list|)
control|)
block|{
if|if
condition|(
name|q
operator|==
name|a
operator|.
name|initial
condition|)
name|a
operator|.
name|initial
operator|=
name|s
expr_stmt|;
name|s
operator|.
name|accept
operator|=
name|q
operator|.
name|accept
expr_stmt|;
name|s
operator|.
name|number
operator|=
name|q
operator|.
name|number
expr_stmt|;
comment|// select representative
name|q
operator|.
name|number
operator|=
name|n
expr_stmt|;
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
name|newstates
operator|.
name|length
condition|;
name|n
operator|++
control|)
block|{
name|State
name|s
init|=
name|newstates
index|[
name|n
index|]
decl_stmt|;
name|s
operator|.
name|accept
operator|=
name|states
index|[
name|s
operator|.
name|number
index|]
operator|.
name|accept
expr_stmt|;
for|for
control|(
name|Transition
name|t
range|:
name|states
index|[
name|s
operator|.
name|number
index|]
operator|.
name|transitions
control|)
name|s
operator|.
name|transitions
operator|.
name|add
argument_list|(
operator|new
name|Transition
argument_list|(
name|t
operator|.
name|min
argument_list|,
name|t
operator|.
name|max
argument_list|,
name|newstates
index|[
name|t
operator|.
name|to
operator|.
name|number
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|a
operator|.
name|removeDeadTransitions
argument_list|()
expr_stmt|;
block|}
DECL|class|IntPair
specifier|static
class|class
name|IntPair
block|{
DECL|field|n1
DECL|field|n2
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
name|State
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
class|class
name|StateListNode
block|{
DECL|field|q
name|State
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
name|StateList
name|sl
decl_stmt|;
DECL|method|StateListNode
name|StateListNode
parameter_list|(
name|State
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
