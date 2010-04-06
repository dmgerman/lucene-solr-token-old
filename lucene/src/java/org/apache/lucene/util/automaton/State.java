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
name|io
operator|.
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|List
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
comment|/**  *<tt>Automaton</tt> state.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|State
specifier|public
class|class
name|State
implements|implements
name|Serializable
implements|,
name|Comparable
argument_list|<
name|State
argument_list|>
block|{
DECL|field|accept
name|boolean
name|accept
decl_stmt|;
DECL|field|transitions
name|Set
argument_list|<
name|Transition
argument_list|>
name|transitions
decl_stmt|;
DECL|field|number
name|int
name|number
decl_stmt|;
DECL|field|id
name|int
name|id
decl_stmt|;
DECL|field|next_id
specifier|static
name|int
name|next_id
decl_stmt|;
comment|/**    * Constructs a new state. Initially, the new state is a reject state.    */
DECL|method|State
specifier|public
name|State
parameter_list|()
block|{
name|resetTransitions
argument_list|()
expr_stmt|;
name|id
operator|=
name|next_id
operator|++
expr_stmt|;
block|}
comment|/**    * Resets transition set.    */
DECL|method|resetTransitions
specifier|final
name|void
name|resetTransitions
parameter_list|()
block|{
name|transitions
operator|=
operator|new
name|HashSet
argument_list|<
name|Transition
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the set of outgoing transitions. Subsequent changes are reflected    * in the automaton.    *     * @return transition set    */
DECL|method|getTransitions
specifier|public
name|Set
argument_list|<
name|Transition
argument_list|>
name|getTransitions
parameter_list|()
block|{
return|return
name|transitions
return|;
block|}
comment|/**    * Adds an outgoing transition.    *     * @param t transition    */
DECL|method|addTransition
specifier|public
name|void
name|addTransition
parameter_list|(
name|Transition
name|t
parameter_list|)
block|{
name|transitions
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets acceptance for this state.    *     * @param accept if true, this state is an accept state    */
DECL|method|setAccept
specifier|public
name|void
name|setAccept
parameter_list|(
name|boolean
name|accept
parameter_list|)
block|{
name|this
operator|.
name|accept
operator|=
name|accept
expr_stmt|;
block|}
comment|/**    * Returns acceptance status.    *     * @return true is this is an accept state    */
DECL|method|isAccept
specifier|public
name|boolean
name|isAccept
parameter_list|()
block|{
return|return
name|accept
return|;
block|}
comment|/**    * Performs lookup in transitions, assuming determinism.    *     * @param c character to look up    * @return destination state, null if no matching outgoing transition    * @see #step(char, Collection)    */
DECL|method|step
specifier|public
name|State
name|step
parameter_list|(
name|char
name|c
parameter_list|)
block|{
for|for
control|(
name|Transition
name|t
range|:
name|transitions
control|)
if|if
condition|(
name|t
operator|.
name|min
operator|<=
name|c
operator|&&
name|c
operator|<=
name|t
operator|.
name|max
condition|)
return|return
name|t
operator|.
name|to
return|;
return|return
literal|null
return|;
block|}
comment|/**    * Performs lookup in transitions, allowing nondeterminism.    *     * @param c character to look up    * @param dest collection where destination states are stored    * @see #step(char)    */
DECL|method|step
specifier|public
name|void
name|step
parameter_list|(
name|char
name|c
parameter_list|,
name|Collection
argument_list|<
name|State
argument_list|>
name|dest
parameter_list|)
block|{
for|for
control|(
name|Transition
name|t
range|:
name|transitions
control|)
if|if
condition|(
name|t
operator|.
name|min
operator|<=
name|c
operator|&&
name|c
operator|<=
name|t
operator|.
name|max
condition|)
name|dest
operator|.
name|add
argument_list|(
name|t
operator|.
name|to
argument_list|)
expr_stmt|;
block|}
DECL|method|addEpsilon
name|void
name|addEpsilon
parameter_list|(
name|State
name|to
parameter_list|)
block|{
if|if
condition|(
name|to
operator|.
name|accept
condition|)
name|accept
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|Transition
name|t
range|:
name|to
operator|.
name|transitions
control|)
name|transitions
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns transitions sorted by (min, reverse max, to) or (to, min, reverse    * max)    */
DECL|method|getSortedTransitionArray
specifier|public
name|Transition
index|[]
name|getSortedTransitionArray
parameter_list|(
name|boolean
name|to_first
parameter_list|)
block|{
name|Transition
index|[]
name|e
init|=
name|transitions
operator|.
name|toArray
argument_list|(
operator|new
name|Transition
index|[
name|transitions
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|e
argument_list|,
operator|new
name|TransitionComparator
argument_list|(
name|to_first
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|e
return|;
block|}
comment|/**    * Returns sorted list of outgoing transitions.    *     * @param to_first if true, order by (to, min, reverse max); otherwise (min,    *          reverse max, to)    * @return transition list    */
DECL|method|getSortedTransitions
specifier|public
name|List
argument_list|<
name|Transition
argument_list|>
name|getSortedTransitions
parameter_list|(
name|boolean
name|to_first
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|getSortedTransitionArray
argument_list|(
name|to_first
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Return this state's number.     *<p>    * Expert: Will be useless unless {@link Automaton#setStateNumbers(Set)}    * has been called first to number the states.    * @return the number    */
DECL|method|getNumber
specifier|public
name|int
name|getNumber
parameter_list|()
block|{
return|return
name|number
return|;
block|}
comment|/**    * Returns string describing this state. Normally invoked via    * {@link Automaton#toString()}.    */
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
literal|"state "
argument_list|)
operator|.
name|append
argument_list|(
name|number
argument_list|)
expr_stmt|;
if|if
condition|(
name|accept
condition|)
name|b
operator|.
name|append
argument_list|(
literal|" [accept]"
argument_list|)
expr_stmt|;
else|else
name|b
operator|.
name|append
argument_list|(
literal|" [reject]"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|":\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Transition
name|t
range|:
name|transitions
control|)
name|b
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Compares this object with the specified object for order. States are    * ordered by the time of construction.    */
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|State
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|id
operator|-
name|id
return|;
block|}
comment|/**    * See {@link java.lang.Object#equals(java.lang.Object)}.    */
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
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
comment|/**    * See {@link java.lang.Object#hashCode()}.    */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class
end_unit
