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
name|Collection
import|;
end_import
begin_comment
comment|/**  * Construction of basic automata.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|BasicAutomata
specifier|final
specifier|public
class|class
name|BasicAutomata
block|{
DECL|method|BasicAutomata
specifier|private
name|BasicAutomata
parameter_list|()
block|{}
comment|/**    * Returns a new (deterministic) automaton with the empty language.    */
DECL|method|makeEmpty
specifier|public
specifier|static
name|Automaton
name|makeEmpty
parameter_list|()
block|{
name|Automaton
name|a
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|State
name|s
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
name|a
operator|.
name|initial
operator|=
name|s
expr_stmt|;
name|a
operator|.
name|deterministic
operator|=
literal|true
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Returns a new (deterministic) automaton that accepts only the empty string.    */
DECL|method|makeEmptyString
specifier|public
specifier|static
name|Automaton
name|makeEmptyString
parameter_list|()
block|{
name|Automaton
name|a
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|a
operator|.
name|singleton
operator|=
literal|""
expr_stmt|;
name|a
operator|.
name|deterministic
operator|=
literal|true
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Returns a new (deterministic) automaton that accepts all strings.    */
DECL|method|makeAnyString
specifier|public
specifier|static
name|Automaton
name|makeAnyString
parameter_list|()
block|{
name|Automaton
name|a
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|State
name|s
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
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
literal|true
expr_stmt|;
name|s
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
name|Character
operator|.
name|MIN_CODE_POINT
argument_list|,
name|Character
operator|.
name|MAX_CODE_POINT
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|a
operator|.
name|deterministic
operator|=
literal|true
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Returns a new (deterministic) automaton that accepts any single codepoint.    */
DECL|method|makeAnyChar
specifier|public
specifier|static
name|Automaton
name|makeAnyChar
parameter_list|()
block|{
return|return
name|makeCharRange
argument_list|(
name|Character
operator|.
name|MIN_CODE_POINT
argument_list|,
name|Character
operator|.
name|MAX_CODE_POINT
argument_list|)
return|;
block|}
comment|/**    * Returns a new (deterministic) automaton that accepts a single codepoint of    * the given value.    */
DECL|method|makeChar
specifier|public
specifier|static
name|Automaton
name|makeChar
parameter_list|(
name|int
name|c
parameter_list|)
block|{
name|Automaton
name|a
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|a
operator|.
name|singleton
operator|=
operator|new
name|String
argument_list|(
name|Character
operator|.
name|toChars
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|a
operator|.
name|deterministic
operator|=
literal|true
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Returns a new (deterministic) automaton that accepts a single codepoint whose    * value is in the given interval (including both end points).    */
DECL|method|makeCharRange
specifier|public
specifier|static
name|Automaton
name|makeCharRange
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|min
operator|==
name|max
condition|)
return|return
name|makeChar
argument_list|(
name|min
argument_list|)
return|;
name|Automaton
name|a
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|State
name|s1
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
name|State
name|s2
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
name|a
operator|.
name|initial
operator|=
name|s1
expr_stmt|;
name|s2
operator|.
name|accept
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|min
operator|<=
name|max
condition|)
name|s1
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
name|min
argument_list|,
name|max
argument_list|,
name|s2
argument_list|)
argument_list|)
expr_stmt|;
name|a
operator|.
name|deterministic
operator|=
literal|true
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Constructs sub-automaton corresponding to decimal numbers of length    * x.substring(n).length().    */
DECL|method|anyOfRightLength
specifier|private
specifier|static
name|State
name|anyOfRightLength
parameter_list|(
name|String
name|x
parameter_list|,
name|int
name|n
parameter_list|)
block|{
name|State
name|s
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|.
name|length
argument_list|()
operator|==
name|n
condition|)
name|s
operator|.
name|setAccept
argument_list|(
literal|true
argument_list|)
expr_stmt|;
else|else
name|s
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
literal|'0'
argument_list|,
literal|'9'
argument_list|,
name|anyOfRightLength
argument_list|(
name|x
argument_list|,
name|n
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
comment|/**    * Constructs sub-automaton corresponding to decimal numbers of value at least    * x.substring(n) and length x.substring(n).length().    */
DECL|method|atLeast
specifier|private
specifier|static
name|State
name|atLeast
parameter_list|(
name|String
name|x
parameter_list|,
name|int
name|n
parameter_list|,
name|Collection
argument_list|<
name|State
argument_list|>
name|initials
parameter_list|,
name|boolean
name|zeros
parameter_list|)
block|{
name|State
name|s
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|.
name|length
argument_list|()
operator|==
name|n
condition|)
name|s
operator|.
name|setAccept
argument_list|(
literal|true
argument_list|)
expr_stmt|;
else|else
block|{
if|if
condition|(
name|zeros
condition|)
name|initials
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|char
name|c
init|=
name|x
operator|.
name|charAt
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|s
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
name|c
argument_list|,
name|atLeast
argument_list|(
name|x
argument_list|,
name|n
operator|+
literal|1
argument_list|,
name|initials
argument_list|,
name|zeros
operator|&&
name|c
operator|==
literal|'0'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|<
literal|'9'
condition|)
name|s
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
call|(
name|char
call|)
argument_list|(
name|c
operator|+
literal|1
argument_list|)
argument_list|,
literal|'9'
argument_list|,
name|anyOfRightLength
argument_list|(
name|x
argument_list|,
name|n
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/**    * Constructs sub-automaton corresponding to decimal numbers of value at most    * x.substring(n) and length x.substring(n).length().    */
DECL|method|atMost
specifier|private
specifier|static
name|State
name|atMost
parameter_list|(
name|String
name|x
parameter_list|,
name|int
name|n
parameter_list|)
block|{
name|State
name|s
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|.
name|length
argument_list|()
operator|==
name|n
condition|)
name|s
operator|.
name|setAccept
argument_list|(
literal|true
argument_list|)
expr_stmt|;
else|else
block|{
name|char
name|c
init|=
name|x
operator|.
name|charAt
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|s
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
name|c
argument_list|,
name|atMost
argument_list|(
name|x
argument_list|,
operator|(
name|char
operator|)
name|n
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|>
literal|'0'
condition|)
name|s
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
literal|'0'
argument_list|,
call|(
name|char
call|)
argument_list|(
name|c
operator|-
literal|1
argument_list|)
argument_list|,
name|anyOfRightLength
argument_list|(
name|x
argument_list|,
name|n
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/**    * Constructs sub-automaton corresponding to decimal numbers of value between    * x.substring(n) and y.substring(n) and of length x.substring(n).length()    * (which must be equal to y.substring(n).length()).    */
DECL|method|between
specifier|private
specifier|static
name|State
name|between
parameter_list|(
name|String
name|x
parameter_list|,
name|String
name|y
parameter_list|,
name|int
name|n
parameter_list|,
name|Collection
argument_list|<
name|State
argument_list|>
name|initials
parameter_list|,
name|boolean
name|zeros
parameter_list|)
block|{
name|State
name|s
init|=
operator|new
name|State
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|.
name|length
argument_list|()
operator|==
name|n
condition|)
name|s
operator|.
name|setAccept
argument_list|(
literal|true
argument_list|)
expr_stmt|;
else|else
block|{
if|if
condition|(
name|zeros
condition|)
name|initials
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|char
name|cx
init|=
name|x
operator|.
name|charAt
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|char
name|cy
init|=
name|y
operator|.
name|charAt
argument_list|(
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|cx
operator|==
name|cy
condition|)
name|s
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
name|cx
argument_list|,
name|between
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|n
operator|+
literal|1
argument_list|,
name|initials
argument_list|,
name|zeros
operator|&&
name|cx
operator|==
literal|'0'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
else|else
block|{
comment|// cx<cy
name|s
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
name|cx
argument_list|,
name|atLeast
argument_list|(
name|x
argument_list|,
name|n
operator|+
literal|1
argument_list|,
name|initials
argument_list|,
name|zeros
operator|&&
name|cx
operator|==
literal|'0'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
name|cy
argument_list|,
name|atMost
argument_list|(
name|y
argument_list|,
name|n
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|cx
operator|+
literal|1
operator|<
name|cy
condition|)
name|s
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
call|(
name|char
call|)
argument_list|(
name|cx
operator|+
literal|1
argument_list|)
argument_list|,
call|(
name|char
call|)
argument_list|(
name|cy
operator|-
literal|1
argument_list|)
argument_list|,
name|anyOfRightLength
argument_list|(
name|x
argument_list|,
name|n
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|s
return|;
block|}
comment|/**    * Returns a new automaton that accepts strings representing decimal    * non-negative integers in the given interval.    *     * @param min minimal value of interval    * @param max maximal value of interval (both end points are included in the    *          interval)    * @param digits if>0, use fixed number of digits (strings must be prefixed    *          by 0's to obtain the right length) - otherwise, the number of    *          digits is not fixed    * @exception IllegalArgumentException if min>max or if numbers in the    *              interval cannot be expressed with the given fixed number of    *              digits    */
DECL|method|makeInterval
specifier|public
specifier|static
name|Automaton
name|makeInterval
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|digits
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|Automaton
name|a
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|String
name|x
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|min
argument_list|)
decl_stmt|;
name|String
name|y
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|max
argument_list|)
decl_stmt|;
if|if
condition|(
name|min
operator|>
name|max
operator|||
operator|(
name|digits
operator|>
literal|0
operator|&&
name|y
operator|.
name|length
argument_list|()
operator|>
name|digits
operator|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
name|int
name|d
decl_stmt|;
if|if
condition|(
name|digits
operator|>
literal|0
condition|)
name|d
operator|=
name|digits
expr_stmt|;
else|else
name|d
operator|=
name|y
operator|.
name|length
argument_list|()
expr_stmt|;
name|StringBuilder
name|bx
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|x
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|d
condition|;
name|i
operator|++
control|)
name|bx
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|bx
operator|.
name|append
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|x
operator|=
name|bx
operator|.
name|toString
argument_list|()
expr_stmt|;
name|StringBuilder
name|by
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|y
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|d
condition|;
name|i
operator|++
control|)
name|by
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
name|by
operator|.
name|append
argument_list|(
name|y
argument_list|)
expr_stmt|;
name|y
operator|=
name|by
operator|.
name|toString
argument_list|()
expr_stmt|;
name|Collection
argument_list|<
name|State
argument_list|>
name|initials
init|=
operator|new
name|ArrayList
argument_list|<
name|State
argument_list|>
argument_list|()
decl_stmt|;
name|a
operator|.
name|initial
operator|=
name|between
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
literal|0
argument_list|,
name|initials
argument_list|,
name|digits
operator|<=
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|digits
operator|<=
literal|0
condition|)
block|{
name|ArrayList
argument_list|<
name|StatePair
argument_list|>
name|pairs
init|=
operator|new
name|ArrayList
argument_list|<
name|StatePair
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|State
name|p
range|:
name|initials
control|)
if|if
condition|(
name|a
operator|.
name|initial
operator|!=
name|p
condition|)
name|pairs
operator|.
name|add
argument_list|(
operator|new
name|StatePair
argument_list|(
name|a
operator|.
name|initial
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|BasicOperations
operator|.
name|addEpsilons
argument_list|(
name|a
argument_list|,
name|pairs
argument_list|)
expr_stmt|;
name|a
operator|.
name|initial
operator|.
name|addTransition
argument_list|(
operator|new
name|Transition
argument_list|(
literal|'0'
argument_list|,
name|a
operator|.
name|initial
argument_list|)
argument_list|)
expr_stmt|;
name|a
operator|.
name|deterministic
operator|=
literal|false
expr_stmt|;
block|}
else|else
name|a
operator|.
name|deterministic
operator|=
literal|true
expr_stmt|;
name|a
operator|.
name|checkMinimizeAlways
argument_list|()
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Returns a new (deterministic) automaton that accepts the single given    * string.    */
DECL|method|makeString
specifier|public
specifier|static
name|Automaton
name|makeString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|Automaton
name|a
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|a
operator|.
name|singleton
operator|=
name|s
expr_stmt|;
name|a
operator|.
name|deterministic
operator|=
literal|true
expr_stmt|;
return|return
name|a
return|;
block|}
block|}
end_class
end_unit
