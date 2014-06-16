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
name|Arrays
import|;
end_import
begin_comment
comment|/**  * Finite-state automaton with fast run operation.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|RunAutomaton
specifier|public
specifier|abstract
class|class
name|RunAutomaton
block|{
DECL|field|automaton
specifier|final
name|LightAutomaton
name|automaton
decl_stmt|;
DECL|field|maxInterval
specifier|final
name|int
name|maxInterval
decl_stmt|;
DECL|field|size
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|accept
specifier|final
name|boolean
index|[]
name|accept
decl_stmt|;
DECL|field|initial
specifier|final
name|int
name|initial
decl_stmt|;
DECL|field|transitions
specifier|final
name|int
index|[]
name|transitions
decl_stmt|;
comment|// delta(state,c) = transitions[state*points.length +
comment|// getCharClass(c)]
DECL|field|points
specifier|final
name|int
index|[]
name|points
decl_stmt|;
comment|// char interval start points
DECL|field|classmap
specifier|final
name|int
index|[]
name|classmap
decl_stmt|;
comment|// map from char number to class class
comment|/**    * Returns a string representation of this automaton.    */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"initial state: "
argument_list|)
operator|.
name|append
argument_list|(
name|initial
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
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
block|{
name|b
operator|.
name|append
argument_list|(
literal|"state "
operator|+
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|accept
index|[
name|i
index|]
condition|)
name|b
operator|.
name|append
argument_list|(
literal|" [accept]:\n"
argument_list|)
expr_stmt|;
else|else
name|b
operator|.
name|append
argument_list|(
literal|" [reject]:\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|points
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|k
init|=
name|transitions
index|[
name|i
operator|*
name|points
operator|.
name|length
operator|+
name|j
index|]
decl_stmt|;
if|if
condition|(
name|k
operator|!=
operator|-
literal|1
condition|)
block|{
name|int
name|min
init|=
name|points
index|[
name|j
index|]
decl_stmt|;
name|int
name|max
decl_stmt|;
if|if
condition|(
name|j
operator|+
literal|1
operator|<
name|points
operator|.
name|length
condition|)
name|max
operator|=
operator|(
name|points
index|[
name|j
operator|+
literal|1
index|]
operator|-
literal|1
operator|)
expr_stmt|;
else|else
name|max
operator|=
name|maxInterval
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|LightAutomaton
operator|.
name|appendCharString
argument_list|(
name|min
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|min
operator|!=
name|max
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
name|LightAutomaton
operator|.
name|appendCharString
argument_list|(
name|max
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|" -> "
argument_list|)
operator|.
name|append
argument_list|(
name|k
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns number of states in automaton.    */
DECL|method|getSize
specifier|public
specifier|final
name|int
name|getSize
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**    * Returns acceptance status for given state.    */
DECL|method|isAccept
specifier|public
specifier|final
name|boolean
name|isAccept
parameter_list|(
name|int
name|state
parameter_list|)
block|{
return|return
name|accept
index|[
name|state
index|]
return|;
block|}
comment|/**    * Returns initial state.    */
DECL|method|getInitialState
specifier|public
specifier|final
name|int
name|getInitialState
parameter_list|()
block|{
return|return
name|initial
return|;
block|}
comment|/**    * Returns array of codepoint class interval start points. The array should    * not be modified by the caller.    */
DECL|method|getCharIntervals
specifier|public
specifier|final
name|int
index|[]
name|getCharIntervals
parameter_list|()
block|{
return|return
name|points
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/**    * Gets character class of given codepoint    */
DECL|method|getCharClass
specifier|final
name|int
name|getCharClass
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|SpecialOperations
operator|.
name|findIndex
argument_list|(
name|c
argument_list|,
name|points
argument_list|)
return|;
block|}
comment|/**    * Constructs a new<code>RunAutomaton</code> from a deterministic    *<code>Automaton</code>.    *     * @param a an automaton    */
DECL|method|RunAutomaton
specifier|public
name|RunAutomaton
parameter_list|(
name|LightAutomaton
name|a
parameter_list|,
name|int
name|maxInterval
parameter_list|,
name|boolean
name|tableize
parameter_list|)
block|{
name|this
operator|.
name|maxInterval
operator|=
name|maxInterval
expr_stmt|;
comment|//System.out.println("before det a=" + a.getNumStates());
name|a
operator|=
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|this
operator|.
name|automaton
operator|=
name|a
expr_stmt|;
comment|//System.out.println("AFTER DET tableize= " + tableize + ": ");
comment|//System.out.println(a.toDot());
name|points
operator|=
name|a
operator|.
name|getStartPoints
argument_list|()
expr_stmt|;
comment|//System.out.println("  points=" + Arrays.toString(points));
name|initial
operator|=
literal|0
expr_stmt|;
name|size
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|a
operator|.
name|getNumStates
argument_list|()
argument_list|)
expr_stmt|;
name|accept
operator|=
operator|new
name|boolean
index|[
name|size
index|]
expr_stmt|;
name|transitions
operator|=
operator|new
name|int
index|[
name|size
operator|*
name|points
operator|.
name|length
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|transitions
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|//System.out.println("RA: size=" + size + " points.length=" + points.length + " total=" + (size * points.length));
for|for
control|(
name|int
name|n
init|=
literal|0
init|;
name|n
operator|<
name|size
condition|;
name|n
operator|++
control|)
block|{
name|accept
index|[
name|n
index|]
operator|=
name|a
operator|.
name|isAccept
argument_list|(
name|n
argument_list|)
expr_stmt|;
comment|//System.out.println("n=" + n + " acc=" + accept[n] + " size=" + size);
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|points
operator|.
name|length
condition|;
name|c
operator|++
control|)
block|{
name|int
name|dest
init|=
name|a
operator|.
name|step
argument_list|(
name|n
argument_list|,
name|points
index|[
name|c
index|]
argument_list|)
decl_stmt|;
comment|//System.out.println("  step from point=" + c + " n=" + n + " label=" + (char) points[c] + " -> " + dest);
assert|assert
name|dest
operator|==
operator|-
literal|1
operator|||
name|dest
operator|<
name|size
assert|;
name|transitions
index|[
name|n
operator|*
name|points
operator|.
name|length
operator|+
name|c
index|]
operator|=
name|dest
expr_stmt|;
comment|//System.out.println("  trans label=" + points[c] + " dest=" + transitions[n * points.length + c]);
block|}
block|}
comment|/*      * Set alphabet table for optimal run performance.      */
if|if
condition|(
name|tableize
condition|)
block|{
name|classmap
operator|=
operator|new
name|int
index|[
name|maxInterval
operator|+
literal|1
index|]
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|maxInterval
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|points
operator|.
name|length
operator|&&
name|j
operator|==
name|points
index|[
name|i
operator|+
literal|1
index|]
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
name|classmap
index|[
name|j
index|]
operator|=
name|i
expr_stmt|;
comment|//System.out.println("classmap[" + (char) j + "]=" + i);
block|}
comment|//System.out.println("  after classmap i=" + i + " maxInterval=" + maxInterval);
block|}
else|else
block|{
name|classmap
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Returns the state obtained by reading the given char from the given state.    * Returns -1 if not obtaining any such state. (If the original    *<code>Automaton</code> had no dead states, -1 is returned here if and only    * if a dead state is entered in an equivalent automaton with a total    * transition function.)    */
DECL|method|step
specifier|public
specifier|final
name|int
name|step
parameter_list|(
name|int
name|state
parameter_list|,
name|int
name|c
parameter_list|)
block|{
comment|//System.out.println("  step state=" + state + " c=" + c + " points.length=" + points.length + " transitions.len=" + transitions.length);
if|if
condition|(
name|classmap
operator|==
literal|null
condition|)
block|{
return|return
name|transitions
index|[
name|state
operator|*
name|points
operator|.
name|length
operator|+
name|getCharClass
argument_list|(
name|c
argument_list|)
index|]
return|;
block|}
else|else
block|{
comment|//System.out.println("    classmap[c]=" + classmap[c]);
return|return
name|transitions
index|[
name|state
operator|*
name|points
operator|.
name|length
operator|+
name|classmap
index|[
name|c
index|]
index|]
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|initial
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|maxInterval
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|points
operator|.
name|length
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|size
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|RunAutomaton
name|other
init|=
operator|(
name|RunAutomaton
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|initial
operator|!=
name|other
operator|.
name|initial
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|maxInterval
operator|!=
name|other
operator|.
name|maxInterval
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|size
operator|!=
name|other
operator|.
name|size
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|points
argument_list|,
name|other
operator|.
name|points
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|accept
argument_list|,
name|other
operator|.
name|accept
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|transitions
argument_list|,
name|other
operator|.
name|transitions
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
