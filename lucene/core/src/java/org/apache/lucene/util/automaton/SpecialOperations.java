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
name|Collections
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
name|IdentityHashMap
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
name|BytesRef
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
name|RamUsageEstimator
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
comment|/**  * Special automata operations.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|SpecialOperations
specifier|final
specifier|public
class|class
name|SpecialOperations
block|{
DECL|method|SpecialOperations
specifier|private
name|SpecialOperations
parameter_list|()
block|{}
comment|/**    * Finds the largest entry whose value is less than or equal to c, or 0 if    * there is no such entry.    */
DECL|method|findIndex
specifier|static
name|int
name|findIndex
parameter_list|(
name|int
name|c
parameter_list|,
name|int
index|[]
name|points
parameter_list|)
block|{
name|int
name|a
init|=
literal|0
decl_stmt|;
name|int
name|b
init|=
name|points
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|b
operator|-
name|a
operator|>
literal|1
condition|)
block|{
name|int
name|d
init|=
operator|(
name|a
operator|+
name|b
operator|)
operator|>>>
literal|1
decl_stmt|;
if|if
condition|(
name|points
index|[
name|d
index|]
operator|>
name|c
condition|)
name|b
operator|=
name|d
expr_stmt|;
elseif|else
if|if
condition|(
name|points
index|[
name|d
index|]
operator|<
name|c
condition|)
name|a
operator|=
name|d
expr_stmt|;
else|else
return|return
name|d
return|;
block|}
return|return
name|a
return|;
block|}
comment|/**    * Returns true if the language of this automaton is finite.    */
DECL|method|isFinite
specifier|public
specifier|static
name|boolean
name|isFinite
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
return|return
literal|true
return|;
return|return
name|isFinite
argument_list|(
name|a
operator|.
name|initial
argument_list|,
operator|new
name|BitSet
argument_list|(
name|a
operator|.
name|getNumberOfStates
argument_list|()
argument_list|)
argument_list|,
operator|new
name|BitSet
argument_list|(
name|a
operator|.
name|getNumberOfStates
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Checks whether there is a loop containing s. (This is sufficient since    * there are never transitions to dead states.)    */
comment|// TODO: not great that this is recursive... in theory a
comment|// large automata could exceed java's stack
DECL|method|isFinite
specifier|private
specifier|static
name|boolean
name|isFinite
parameter_list|(
name|State
name|s
parameter_list|,
name|BitSet
name|path
parameter_list|,
name|BitSet
name|visited
parameter_list|)
block|{
name|path
operator|.
name|set
argument_list|(
name|s
operator|.
name|number
argument_list|)
expr_stmt|;
for|for
control|(
name|Transition
name|t
range|:
name|s
operator|.
name|getTransitions
argument_list|()
control|)
if|if
condition|(
name|path
operator|.
name|get
argument_list|(
name|t
operator|.
name|to
operator|.
name|number
argument_list|)
operator|||
operator|(
operator|!
name|visited
operator|.
name|get
argument_list|(
name|t
operator|.
name|to
operator|.
name|number
argument_list|)
operator|&&
operator|!
name|isFinite
argument_list|(
name|t
operator|.
name|to
argument_list|,
name|path
argument_list|,
name|visited
argument_list|)
operator|)
condition|)
return|return
literal|false
return|;
name|path
operator|.
name|clear
argument_list|(
name|s
operator|.
name|number
argument_list|)
expr_stmt|;
name|visited
operator|.
name|set
argument_list|(
name|s
operator|.
name|number
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Returns the longest string that is a prefix of all accepted strings and    * visits each state at most once.    *     * @return common prefix    */
DECL|method|getCommonPrefix
specifier|public
specifier|static
name|String
name|getCommonPrefix
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
return|return
name|a
operator|.
name|singleton
return|;
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|State
argument_list|>
name|visited
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|State
name|s
init|=
name|a
operator|.
name|initial
decl_stmt|;
name|boolean
name|done
decl_stmt|;
do|do
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|visited
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|accept
operator|&&
name|s
operator|.
name|numTransitions
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Transition
name|t
init|=
name|s
operator|.
name|getTransitions
argument_list|()
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
name|min
operator|==
name|t
operator|.
name|max
operator|&&
operator|!
name|visited
operator|.
name|contains
argument_list|(
name|t
operator|.
name|to
argument_list|)
condition|)
block|{
name|b
operator|.
name|appendCodePoint
argument_list|(
name|t
operator|.
name|min
argument_list|)
expr_stmt|;
name|s
operator|=
name|t
operator|.
name|to
expr_stmt|;
name|done
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
operator|!
name|done
condition|)
do|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// TODO: this currently requites a determinized machine,
comment|// but it need not -- we can speed it up by walking the
comment|// NFA instead.  it'd still be fail fast.
DECL|method|getCommonPrefixBytesRef
specifier|public
specifier|static
name|BytesRef
name|getCommonPrefixBytesRef
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
return|return
operator|new
name|BytesRef
argument_list|(
name|a
operator|.
name|singleton
argument_list|)
return|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|State
argument_list|>
name|visited
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|State
name|s
init|=
name|a
operator|.
name|initial
decl_stmt|;
name|boolean
name|done
decl_stmt|;
do|do
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|visited
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|accept
operator|&&
name|s
operator|.
name|numTransitions
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Transition
name|t
init|=
name|s
operator|.
name|getTransitions
argument_list|()
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
name|min
operator|==
name|t
operator|.
name|max
operator|&&
operator|!
name|visited
operator|.
name|contains
argument_list|(
name|t
operator|.
name|to
argument_list|)
condition|)
block|{
name|ref
operator|.
name|grow
argument_list|(
operator|++
name|ref
operator|.
name|length
argument_list|)
expr_stmt|;
name|ref
operator|.
name|bytes
index|[
name|ref
operator|.
name|length
operator|-
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
name|t
operator|.
name|min
expr_stmt|;
name|s
operator|=
name|t
operator|.
name|to
expr_stmt|;
name|done
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
operator|!
name|done
condition|)
do|;
return|return
name|ref
return|;
block|}
comment|/**    * Returns the longest string that is a suffix of all accepted strings and    * visits each state at most once.    *     * @return common suffix    */
DECL|method|getCommonSuffix
specifier|public
specifier|static
name|String
name|getCommonSuffix
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
comment|// if singleton, the suffix is the string itself.
return|return
name|a
operator|.
name|singleton
return|;
comment|// reverse the language of the automaton, then reverse its common prefix.
name|Automaton
name|r
init|=
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|reverse
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|determinize
argument_list|()
expr_stmt|;
return|return
operator|new
name|StringBuilder
argument_list|(
name|SpecialOperations
operator|.
name|getCommonPrefix
argument_list|(
name|r
argument_list|)
argument_list|)
operator|.
name|reverse
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getCommonSuffixBytesRef
specifier|public
specifier|static
name|BytesRef
name|getCommonSuffixBytesRef
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
comment|// if singleton, the suffix is the string itself.
return|return
operator|new
name|BytesRef
argument_list|(
name|a
operator|.
name|singleton
argument_list|)
return|;
comment|// reverse the language of the automaton, then reverse its common prefix.
name|Automaton
name|r
init|=
name|a
operator|.
name|clone
argument_list|()
decl_stmt|;
name|reverse
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|determinize
argument_list|()
expr_stmt|;
name|BytesRef
name|ref
init|=
name|SpecialOperations
operator|.
name|getCommonPrefixBytesRef
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|reverseBytes
argument_list|(
name|ref
argument_list|)
expr_stmt|;
return|return
name|ref
return|;
block|}
DECL|method|reverseBytes
specifier|private
specifier|static
name|void
name|reverseBytes
parameter_list|(
name|BytesRef
name|ref
parameter_list|)
block|{
if|if
condition|(
name|ref
operator|.
name|length
operator|<=
literal|1
condition|)
return|return;
name|int
name|num
init|=
name|ref
operator|.
name|length
operator|>>
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|ref
operator|.
name|offset
init|;
name|i
operator|<
operator|(
name|ref
operator|.
name|offset
operator|+
name|num
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|b
init|=
name|ref
operator|.
name|bytes
index|[
name|i
index|]
decl_stmt|;
name|ref
operator|.
name|bytes
index|[
name|i
index|]
operator|=
name|ref
operator|.
name|bytes
index|[
name|ref
operator|.
name|offset
operator|*
literal|2
operator|+
name|ref
operator|.
name|length
operator|-
name|i
operator|-
literal|1
index|]
expr_stmt|;
name|ref
operator|.
name|bytes
index|[
name|ref
operator|.
name|offset
operator|*
literal|2
operator|+
name|ref
operator|.
name|length
operator|-
name|i
operator|-
literal|1
index|]
operator|=
name|b
expr_stmt|;
block|}
block|}
comment|/**    * Reverses the language of the given (non-singleton) automaton while returning    * the set of new initial states.    */
DECL|method|reverse
specifier|public
specifier|static
name|Set
argument_list|<
name|State
argument_list|>
name|reverse
parameter_list|(
name|Automaton
name|a
parameter_list|)
block|{
name|a
operator|.
name|expandSingleton
argument_list|()
expr_stmt|;
comment|// reverse all edges
name|HashMap
argument_list|<
name|State
argument_list|,
name|HashSet
argument_list|<
name|Transition
argument_list|>
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|State
index|[]
name|states
init|=
name|a
operator|.
name|getNumberedStates
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|State
argument_list|>
name|accept
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|State
name|s
range|:
name|states
control|)
if|if
condition|(
name|s
operator|.
name|isAccept
argument_list|()
condition|)
name|accept
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
for|for
control|(
name|State
name|r
range|:
name|states
control|)
block|{
name|m
operator|.
name|put
argument_list|(
name|r
argument_list|,
operator|new
name|HashSet
argument_list|<
name|Transition
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|accept
operator|=
literal|false
expr_stmt|;
block|}
for|for
control|(
name|State
name|r
range|:
name|states
control|)
for|for
control|(
name|Transition
name|t
range|:
name|r
operator|.
name|getTransitions
argument_list|()
control|)
name|m
operator|.
name|get
argument_list|(
name|t
operator|.
name|to
argument_list|)
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
name|r
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|State
name|r
range|:
name|states
control|)
block|{
name|Set
argument_list|<
name|Transition
argument_list|>
name|tr
init|=
name|m
operator|.
name|get
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|r
operator|.
name|setTransitions
argument_list|(
name|tr
operator|.
name|toArray
argument_list|(
operator|new
name|Transition
index|[
name|tr
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// make new initial+final states
name|a
operator|.
name|initial
operator|.
name|accept
operator|=
literal|true
expr_stmt|;
name|a
operator|.
name|initial
operator|=
operator|new
name|State
argument_list|()
expr_stmt|;
for|for
control|(
name|State
name|r
range|:
name|accept
control|)
name|a
operator|.
name|initial
operator|.
name|addEpsilon
argument_list|(
name|r
argument_list|)
expr_stmt|;
comment|// ensures that all initial states are reachable
name|a
operator|.
name|deterministic
operator|=
literal|false
expr_stmt|;
name|a
operator|.
name|clearNumberedStates
argument_list|()
expr_stmt|;
return|return
name|accept
return|;
block|}
DECL|class|PathNode
specifier|private
specifier|static
class|class
name|PathNode
block|{
comment|/** Which state the path node ends on, whose      *  transitions we are enumerating. */
DECL|field|state
specifier|public
name|State
name|state
decl_stmt|;
comment|/** Which state the current transition leads to. */
DECL|field|to
specifier|public
name|State
name|to
decl_stmt|;
comment|/** Which transition we are on. */
DECL|field|transition
specifier|public
name|int
name|transition
decl_stmt|;
comment|/** Which label we are on, in the min-max range of the      *  current Transition */
DECL|field|label
specifier|public
name|int
name|label
decl_stmt|;
DECL|method|resetState
specifier|public
name|void
name|resetState
parameter_list|(
name|State
name|state
parameter_list|)
block|{
assert|assert
name|state
operator|.
name|numTransitions
argument_list|()
operator|!=
literal|0
assert|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|transition
operator|=
literal|0
expr_stmt|;
name|Transition
name|t
init|=
name|state
operator|.
name|transitionsArray
index|[
name|transition
index|]
decl_stmt|;
name|label
operator|=
name|t
operator|.
name|min
expr_stmt|;
name|to
operator|=
name|t
operator|.
name|to
expr_stmt|;
block|}
comment|/** Returns next label of current transition, or      *  advances to next transition and returns its first      *  label, if current one is exhausted.  If there are      *  no more transitions, returns -1. */
DECL|method|nextLabel
specifier|public
name|int
name|nextLabel
parameter_list|()
block|{
if|if
condition|(
name|label
operator|>
name|state
operator|.
name|transitionsArray
index|[
name|transition
index|]
operator|.
name|max
condition|)
block|{
comment|// We've exhaused the current transition's labels;
comment|// move to next transitions:
name|transition
operator|++
expr_stmt|;
if|if
condition|(
name|transition
operator|>=
name|state
operator|.
name|numTransitions
argument_list|()
condition|)
block|{
comment|// We're done iterating transitions leaving this state
return|return
operator|-
literal|1
return|;
block|}
name|Transition
name|t
init|=
name|state
operator|.
name|transitionsArray
index|[
name|transition
index|]
decl_stmt|;
name|label
operator|=
name|t
operator|.
name|min
expr_stmt|;
name|to
operator|=
name|t
operator|.
name|to
expr_stmt|;
block|}
return|return
name|label
operator|++
return|;
block|}
block|}
DECL|method|getNode
specifier|private
specifier|static
name|PathNode
name|getNode
parameter_list|(
name|PathNode
index|[]
name|nodes
parameter_list|,
name|int
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|<
name|nodes
operator|.
name|length
assert|;
if|if
condition|(
name|nodes
index|[
name|index
index|]
operator|==
literal|null
condition|)
block|{
name|nodes
index|[
name|index
index|]
operator|=
operator|new
name|PathNode
argument_list|()
expr_stmt|;
block|}
return|return
name|nodes
index|[
name|index
index|]
return|;
block|}
comment|// TODO: this is a dangerous method ... Automaton could be
comment|// huge ... and it's better in general for caller to
comment|// enumerate& process in a single walk:
comment|/** Returns the set of accepted strings, up to at most    *<code>limit</code> strings. If more than<code>limit</code>     *  strings are accepted, the first limit strings found are returned. If<code>limit</code> == -1, then     *  the limit is infinite.  If the {@link Automaton} has    *  cycles then this method might throw {@code    *  IllegalArgumentException} but that is not guaranteed    *  when the limit is set. */
DECL|method|getFiniteStrings
specifier|public
specifier|static
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|getFiniteStrings
parameter_list|(
name|Automaton
name|a
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|results
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|limit
operator|==
operator|-
literal|1
operator|||
name|limit
operator|>
literal|0
condition|)
block|{
comment|// OK
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"limit must be -1 (which means no limit), or> 0; got: "
operator|+
name|limit
argument_list|)
throw|;
block|}
if|if
condition|(
name|a
operator|.
name|isSingleton
argument_list|()
condition|)
block|{
comment|// Easy case: automaton accepts only 1 string
name|results
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toUTF32
argument_list|(
name|a
operator|.
name|singleton
argument_list|,
operator|new
name|IntsRef
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|a
operator|.
name|initial
operator|.
name|accept
condition|)
block|{
comment|// Special case the empty string, as usual:
name|results
operator|.
name|add
argument_list|(
operator|new
name|IntsRef
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|.
name|initial
operator|.
name|numTransitions
argument_list|()
operator|>
literal|0
operator|&&
operator|(
name|limit
operator|==
operator|-
literal|1
operator|||
name|results
operator|.
name|size
argument_list|()
operator|<
name|limit
operator|)
condition|)
block|{
comment|// TODO: we could use state numbers here and just
comment|// alloc array, but asking for states array can be
comment|// costly (it's lazily computed):
comment|// Tracks which states are in the current path, for
comment|// cycle detection:
name|Set
argument_list|<
name|State
argument_list|>
name|pathStates
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|State
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// Stack to hold our current state in the
comment|// recursion/iteration:
name|PathNode
index|[]
name|nodes
init|=
operator|new
name|PathNode
index|[
literal|4
index|]
decl_stmt|;
name|pathStates
operator|.
name|add
argument_list|(
name|a
operator|.
name|initial
argument_list|)
expr_stmt|;
name|PathNode
name|root
init|=
name|getNode
argument_list|(
name|nodes
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|root
operator|.
name|resetState
argument_list|(
name|a
operator|.
name|initial
argument_list|)
expr_stmt|;
name|IntsRef
name|string
init|=
operator|new
name|IntsRef
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|string
operator|.
name|length
operator|=
literal|1
expr_stmt|;
while|while
condition|(
name|string
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|PathNode
name|node
init|=
name|nodes
index|[
name|string
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
comment|// Get next label leaving the current node:
name|int
name|label
init|=
name|node
operator|.
name|nextLabel
argument_list|()
decl_stmt|;
if|if
condition|(
name|label
operator|!=
operator|-
literal|1
condition|)
block|{
name|string
operator|.
name|ints
index|[
name|string
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|label
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|to
operator|.
name|accept
condition|)
block|{
comment|// This transition leads to an accept state,
comment|// so we save the current string:
name|results
operator|.
name|add
argument_list|(
name|IntsRef
operator|.
name|deepCopyOf
argument_list|(
name|string
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|results
operator|.
name|size
argument_list|()
operator|==
name|limit
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|node
operator|.
name|to
operator|.
name|numTransitions
argument_list|()
operator|!=
literal|0
condition|)
block|{
comment|// Now recurse: the destination of this transition has
comment|// outgoing transitions:
if|if
condition|(
name|pathStates
operator|.
name|contains
argument_list|(
name|node
operator|.
name|to
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"automaton has cycles"
argument_list|)
throw|;
block|}
name|pathStates
operator|.
name|add
argument_list|(
name|node
operator|.
name|to
argument_list|)
expr_stmt|;
comment|// Push node onto stack:
if|if
condition|(
name|nodes
operator|.
name|length
operator|==
name|string
operator|.
name|length
condition|)
block|{
name|PathNode
index|[]
name|newNodes
init|=
operator|new
name|PathNode
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|nodes
operator|.
name|length
operator|+
literal|1
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
name|nodes
argument_list|,
literal|0
argument_list|,
name|newNodes
argument_list|,
literal|0
argument_list|,
name|nodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|nodes
operator|=
name|newNodes
expr_stmt|;
block|}
name|getNode
argument_list|(
name|nodes
argument_list|,
name|string
operator|.
name|length
argument_list|)
operator|.
name|resetState
argument_list|(
name|node
operator|.
name|to
argument_list|)
expr_stmt|;
name|string
operator|.
name|length
operator|++
expr_stmt|;
name|string
operator|.
name|grow
argument_list|(
name|string
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// No more transitions leaving this state,
comment|// pop/return back to previous state:
assert|assert
name|pathStates
operator|.
name|contains
argument_list|(
name|node
operator|.
name|state
argument_list|)
assert|;
name|pathStates
operator|.
name|remove
argument_list|(
name|node
operator|.
name|state
argument_list|)
expr_stmt|;
name|string
operator|.
name|length
operator|--
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|results
return|;
block|}
block|}
end_class
end_unit
