begin_unit
begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|Filter
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import
begin_comment
comment|/**  *<p>  * Allows multiple {@link Filter}s to be chained.  * Logical operations such as<b>NOT</b> and<b>XOR</b>  * are applied between filters. One operation can be used  * for all filters, or a specific operation can be declared  * for each filter.  *</p>  *<p>  * Order in which filters are called depends on  * the position of the filter in the chain. It's probably  * more efficient to place the most restrictive filters  * /least computationally-intensive filters first.  *</p>  *  * @author<a href="mailto:kelvint@apache.org">Kelvin Tan</a>  */
end_comment
begin_class
DECL|class|ChainedFilter
specifier|public
class|class
name|ChainedFilter
extends|extends
name|Filter
block|{
comment|/**      * {@link BitSet#or}.      */
DECL|field|OR
specifier|public
specifier|static
specifier|final
name|int
name|OR
init|=
literal|0
decl_stmt|;
comment|/**      * {@link BitSet#and}.      */
DECL|field|AND
specifier|public
specifier|static
specifier|final
name|int
name|AND
init|=
literal|1
decl_stmt|;
comment|/**      * {@link BitSet#andNot}.      */
DECL|field|ANDNOT
specifier|public
specifier|static
specifier|final
name|int
name|ANDNOT
init|=
literal|2
decl_stmt|;
comment|/**      * {@link BitSet#xor}.      */
DECL|field|XOR
specifier|public
specifier|static
specifier|final
name|int
name|XOR
init|=
literal|3
decl_stmt|;
comment|/**      * Logical operation when none is declared. Defaults to      * {@link BitSet#or}.      */
DECL|field|DEFAULT
specifier|public
specifier|static
name|int
name|DEFAULT
init|=
name|OR
decl_stmt|;
comment|/** The filter chain */
DECL|field|chain
specifier|private
name|Filter
index|[]
name|chain
init|=
literal|null
decl_stmt|;
DECL|field|logicArray
specifier|private
name|int
index|[]
name|logicArray
decl_stmt|;
DECL|field|logic
specifier|private
name|int
name|logic
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Ctor.      * @param chain The chain of filters      */
DECL|method|ChainedFilter
specifier|public
name|ChainedFilter
parameter_list|(
name|Filter
index|[]
name|chain
parameter_list|)
block|{
name|this
operator|.
name|chain
operator|=
name|chain
expr_stmt|;
block|}
comment|/**      * Ctor.      * @param chain The chain of filters      * @param logicArray Logical operations to apply between filters      */
DECL|method|ChainedFilter
specifier|public
name|ChainedFilter
parameter_list|(
name|Filter
index|[]
name|chain
parameter_list|,
name|int
index|[]
name|logicArray
parameter_list|)
block|{
name|this
operator|.
name|chain
operator|=
name|chain
expr_stmt|;
name|this
operator|.
name|logicArray
operator|=
name|logicArray
expr_stmt|;
block|}
comment|/**      * Ctor.      * @param chain The chain of filters      * @param logic Logicial operation to apply to ALL filters      */
DECL|method|ChainedFilter
specifier|public
name|ChainedFilter
parameter_list|(
name|Filter
index|[]
name|chain
parameter_list|,
name|int
name|logic
parameter_list|)
block|{
name|this
operator|.
name|chain
operator|=
name|chain
expr_stmt|;
name|this
operator|.
name|logic
operator|=
name|logic
expr_stmt|;
block|}
comment|/**      * {@link Filter#bits}.      */
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|logic
operator|!=
operator|-
literal|1
condition|)
return|return
name|bits
argument_list|(
name|reader
argument_list|,
name|logic
argument_list|)
return|;
elseif|else
if|if
condition|(
name|logicArray
operator|!=
literal|null
condition|)
return|return
name|bits
argument_list|(
name|reader
argument_list|,
name|logicArray
argument_list|)
return|;
else|else
return|return
name|bits
argument_list|(
name|reader
argument_list|,
name|DEFAULT
argument_list|)
return|;
block|}
comment|/**      * Delegates to each filter in the chain.      * @param reader IndexReader      * @param logic Logical operation      * @return BitSet      */
DECL|method|bits
specifier|private
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|logic
parameter_list|)
throws|throws
name|IOException
block|{
name|BitSet
name|result
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/**          * First AND operation takes place against a completely false          * bitset and will always return zero results. Thanks to          * Daniel Armbrust for pointing this out and suggesting workaround.          */
if|if
condition|(
name|logic
operator|==
name|AND
condition|)
block|{
name|result
operator|=
name|chain
index|[
name|i
index|]
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|BitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
init|;
name|i
operator|<
name|chain
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|doChain
argument_list|(
name|result
argument_list|,
name|reader
argument_list|,
name|logic
argument_list|,
name|chain
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Delegates to each filter in the chain.      * @param reader IndexReader      * @param logic Logical operation      * @return BitSet      */
DECL|method|bits
specifier|private
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
index|[]
name|logic
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|logic
operator|.
name|length
operator|!=
name|chain
operator|.
name|length
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid number of elements in logic array"
argument_list|)
throw|;
name|BitSet
name|result
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/**          * First AND operation takes place against a completely false          * bitset and will always return zero results. Thanks to          * Daniel Armbrust for pointing this out and suggesting workaround.          */
if|if
condition|(
name|logic
index|[
literal|0
index|]
operator|==
name|AND
condition|)
block|{
name|result
operator|=
name|chain
index|[
name|i
index|]
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|BitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
init|;
name|i
operator|<
name|chain
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|doChain
argument_list|(
name|result
argument_list|,
name|reader
argument_list|,
name|logic
index|[
name|i
index|]
argument_list|,
name|chain
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ChainedFilter: ["
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
name|chain
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|chain
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|doChain
specifier|private
name|void
name|doChain
parameter_list|(
name|BitSet
name|result
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|int
name|logic
parameter_list|,
name|Filter
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|logic
condition|)
block|{
case|case
name|OR
case|:
name|result
operator|.
name|or
argument_list|(
name|filter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
case|case
name|AND
case|:
name|result
operator|.
name|and
argument_list|(
name|filter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
case|case
name|ANDNOT
case|:
name|result
operator|.
name|andNot
argument_list|(
name|filter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
case|case
name|XOR
case|:
name|result
operator|.
name|xor
argument_list|(
name|filter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
default|default:
name|doChain
argument_list|(
name|result
argument_list|,
name|reader
argument_list|,
name|DEFAULT
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
