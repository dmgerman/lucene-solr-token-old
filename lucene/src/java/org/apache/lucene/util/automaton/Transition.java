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
name|Comparator
import|;
end_import
begin_comment
comment|/**  *<tt>Automaton</tt> transition.  *<p>  * A transition, which belongs to a source state, consists of a Unicode  * codepoint interval and a destination state.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|Transition
specifier|public
class|class
name|Transition
implements|implements
name|Cloneable
block|{
comment|/*    * CLASS INVARIANT: min<=max    */
DECL|field|min
specifier|final
name|int
name|min
decl_stmt|;
DECL|field|max
specifier|final
name|int
name|max
decl_stmt|;
DECL|field|to
specifier|final
name|State
name|to
decl_stmt|;
comment|/**    * Constructs a new singleton interval transition.    *     * @param c transition codepoint    * @param to destination state    */
DECL|method|Transition
specifier|public
name|Transition
parameter_list|(
name|int
name|c
parameter_list|,
name|State
name|to
parameter_list|)
block|{
assert|assert
name|c
operator|>=
literal|0
assert|;
name|min
operator|=
name|max
operator|=
name|c
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
block|}
comment|/**    * Constructs a new transition. Both end points are included in the interval.    *     * @param min transition interval minimum    * @param max transition interval maximum    * @param to destination state    */
DECL|method|Transition
specifier|public
name|Transition
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|,
name|State
name|to
parameter_list|)
block|{
assert|assert
name|min
operator|>=
literal|0
assert|;
assert|assert
name|max
operator|>=
literal|0
assert|;
if|if
condition|(
name|max
operator|<
name|min
condition|)
block|{
name|int
name|t
init|=
name|max
decl_stmt|;
name|max
operator|=
name|min
expr_stmt|;
name|min
operator|=
name|t
expr_stmt|;
block|}
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
block|}
comment|/** Returns minimum of this transition interval. */
DECL|method|getMin
specifier|public
name|int
name|getMin
parameter_list|()
block|{
return|return
name|min
return|;
block|}
comment|/** Returns maximum of this transition interval. */
DECL|method|getMax
specifier|public
name|int
name|getMax
parameter_list|()
block|{
return|return
name|max
return|;
block|}
comment|/** Returns destination of this transition. */
DECL|method|getDest
specifier|public
name|State
name|getDest
parameter_list|()
block|{
return|return
name|to
return|;
block|}
comment|/**    * Checks for equality.    *     * @param obj object to compare with    * @return true if<tt>obj</tt> is a transition with same character interval    *         and destination state as this transition.    */
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
name|obj
operator|instanceof
name|Transition
condition|)
block|{
name|Transition
name|t
init|=
operator|(
name|Transition
operator|)
name|obj
decl_stmt|;
return|return
name|t
operator|.
name|min
operator|==
name|min
operator|&&
name|t
operator|.
name|max
operator|==
name|max
operator|&&
name|t
operator|.
name|to
operator|==
name|to
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
comment|/**    * Returns hash code. The hash code is based on the character interval (not    * the destination state).    *     * @return hash code    */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|min
operator|*
literal|2
operator|+
name|max
operator|*
literal|3
return|;
block|}
comment|/**    * Clones this transition.    *     * @return clone with same character interval and destination state    */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Transition
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|Transition
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|appendCharString
specifier|static
name|void
name|appendCharString
parameter_list|(
name|int
name|c
parameter_list|,
name|StringBuilder
name|b
parameter_list|)
block|{
if|if
condition|(
name|c
operator|>=
literal|0x21
operator|&&
name|c
operator|<=
literal|0x7e
operator|&&
name|c
operator|!=
literal|'\\'
operator|&&
name|c
operator|!=
literal|'"'
condition|)
name|b
operator|.
name|appendCodePoint
argument_list|(
name|c
argument_list|)
expr_stmt|;
else|else
block|{
name|b
operator|.
name|append
argument_list|(
literal|"\\\\U"
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0x10
condition|)
name|b
operator|.
name|append
argument_list|(
literal|"0000000"
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|c
operator|<
literal|0x100
condition|)
name|b
operator|.
name|append
argument_list|(
literal|"000000"
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|c
operator|<
literal|0x1000
condition|)
name|b
operator|.
name|append
argument_list|(
literal|"00000"
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|c
operator|<
literal|0x10000
condition|)
name|b
operator|.
name|append
argument_list|(
literal|"0000"
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|c
operator|<
literal|0x100000
condition|)
name|b
operator|.
name|append
argument_list|(
literal|"000"
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|c
operator|<
literal|0x1000000
condition|)
name|b
operator|.
name|append
argument_list|(
literal|"00"
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|c
operator|<
literal|0x10000000
condition|)
name|b
operator|.
name|append
argument_list|(
literal|"0"
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
else|else
name|b
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns a string describing this state. Normally invoked via    * {@link Automaton#toString()}.    */
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
name|to
operator|.
name|number
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|appendDot
name|void
name|appendDot
parameter_list|(
name|StringBuilder
name|b
parameter_list|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" -> "
argument_list|)
operator|.
name|append
argument_list|(
name|to
operator|.
name|number
argument_list|)
operator|.
name|append
argument_list|(
literal|" [label=\""
argument_list|)
expr_stmt|;
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
literal|"\"]\n"
argument_list|)
expr_stmt|;
block|}
DECL|class|CompareByDestThenMinMaxSingle
specifier|private
specifier|static
specifier|final
class|class
name|CompareByDestThenMinMaxSingle
implements|implements
name|Comparator
argument_list|<
name|Transition
argument_list|>
block|{
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Transition
name|t1
parameter_list|,
name|Transition
name|t2
parameter_list|)
block|{
if|if
condition|(
name|t1
operator|.
name|to
operator|!=
name|t2
operator|.
name|to
condition|)
block|{
if|if
condition|(
name|t1
operator|.
name|to
operator|.
name|number
operator|<
name|t2
operator|.
name|to
operator|.
name|number
condition|)
return|return
operator|-
literal|1
return|;
elseif|else
if|if
condition|(
name|t1
operator|.
name|to
operator|.
name|number
operator|>
name|t2
operator|.
name|to
operator|.
name|number
condition|)
return|return
literal|1
return|;
block|}
if|if
condition|(
name|t1
operator|.
name|min
operator|<
name|t2
operator|.
name|min
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|t1
operator|.
name|min
operator|>
name|t2
operator|.
name|min
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|t1
operator|.
name|max
operator|>
name|t2
operator|.
name|max
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|t1
operator|.
name|max
operator|<
name|t2
operator|.
name|max
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
block|}
DECL|field|CompareByDestThenMinMax
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Transition
argument_list|>
name|CompareByDestThenMinMax
init|=
operator|new
name|CompareByDestThenMinMaxSingle
argument_list|()
decl_stmt|;
DECL|class|CompareByMinMaxThenDestSingle
specifier|private
specifier|static
specifier|final
class|class
name|CompareByMinMaxThenDestSingle
implements|implements
name|Comparator
argument_list|<
name|Transition
argument_list|>
block|{
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Transition
name|t1
parameter_list|,
name|Transition
name|t2
parameter_list|)
block|{
if|if
condition|(
name|t1
operator|.
name|min
operator|<
name|t2
operator|.
name|min
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|t1
operator|.
name|min
operator|>
name|t2
operator|.
name|min
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|t1
operator|.
name|max
operator|>
name|t2
operator|.
name|max
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|t1
operator|.
name|max
operator|<
name|t2
operator|.
name|max
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|t1
operator|.
name|to
operator|!=
name|t2
operator|.
name|to
condition|)
block|{
if|if
condition|(
name|t1
operator|.
name|to
operator|.
name|number
operator|<
name|t2
operator|.
name|to
operator|.
name|number
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|t1
operator|.
name|to
operator|.
name|number
operator|>
name|t2
operator|.
name|to
operator|.
name|number
condition|)
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
DECL|field|CompareByMinMaxThenDest
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Transition
argument_list|>
name|CompareByMinMaxThenDest
init|=
operator|new
name|CompareByMinMaxThenDestSingle
argument_list|()
decl_stmt|;
block|}
end_class
end_unit
