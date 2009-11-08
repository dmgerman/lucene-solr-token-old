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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|OpenBitSet
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
name|OpenBitSetDISI
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
name|SortedVIntList
import|;
end_import
begin_comment
comment|/**  *<p>  * Allows multiple {@link Filter}s to be chained.  * Logical operations such as<b>NOT</b> and<b>XOR</b>  * are applied between filters. One operation can be used  * for all filters, or a specific operation can be declared  * for each filter.  *</p>  *<p>  * Order in which filters are called depends on  * the position of the filter in the chain. It's probably  * more efficient to place the most restrictive filters  * /least computationally-intensive filters first.  *</p>  *  */
end_comment
begin_class
DECL|class|ChainedFilter
specifier|public
class|class
name|ChainedFilter
extends|extends
name|Filter
block|{
DECL|field|OR
specifier|public
specifier|static
specifier|final
name|int
name|OR
init|=
literal|0
decl_stmt|;
DECL|field|AND
specifier|public
specifier|static
specifier|final
name|int
name|AND
init|=
literal|1
decl_stmt|;
DECL|field|ANDNOT
specifier|public
specifier|static
specifier|final
name|int
name|ANDNOT
init|=
literal|2
decl_stmt|;
DECL|field|XOR
specifier|public
specifier|static
specifier|final
name|int
name|XOR
init|=
literal|3
decl_stmt|;
comment|/**      * Logical operation when none is declared. Defaults to      * OR.      */
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
comment|/**      * Ctor.      * @param chain The chain of filters      * @param logic Logical operation to apply to ALL filters      */
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
comment|/**      * {@link Filter#getDocIdSet}.      */
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|int
index|[]
name|index
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
comment|// use array as reference to modifiable int;
name|index
index|[
literal|0
index|]
operator|=
literal|0
expr_stmt|;
comment|// an object attribute would not be thread safe.
if|if
condition|(
name|logic
operator|!=
operator|-
literal|1
condition|)
return|return
name|getDocIdSet
argument_list|(
name|reader
argument_list|,
name|logic
argument_list|,
name|index
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
name|getDocIdSet
argument_list|(
name|reader
argument_list|,
name|logicArray
argument_list|,
name|index
argument_list|)
return|;
else|else
return|return
name|getDocIdSet
argument_list|(
name|reader
argument_list|,
name|DEFAULT
argument_list|,
name|index
argument_list|)
return|;
block|}
DECL|method|getDISI
specifier|private
name|DocIdSetIterator
name|getDISI
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|DocIdSet
name|docIdSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|docIdSet
operator|==
literal|null
condition|)
block|{
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
name|DocIdSetIterator
name|iter
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iter
operator|==
literal|null
condition|)
block|{
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|iter
return|;
block|}
block|}
block|}
DECL|method|initialResult
specifier|private
name|OpenBitSetDISI
name|initialResult
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|logic
parameter_list|,
name|int
index|[]
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|OpenBitSetDISI
name|result
decl_stmt|;
comment|/**          * First AND operation takes place against a completely false          * bitset and will always return zero results.          */
if|if
condition|(
name|logic
operator|==
name|AND
condition|)
block|{
name|result
operator|=
operator|new
name|OpenBitSetDISI
argument_list|(
name|getDISI
argument_list|(
name|chain
index|[
name|index
index|[
literal|0
index|]
index|]
argument_list|,
name|reader
argument_list|)
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
operator|++
name|index
index|[
literal|0
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|logic
operator|==
name|ANDNOT
condition|)
block|{
name|result
operator|=
operator|new
name|OpenBitSetDISI
argument_list|(
name|getDISI
argument_list|(
name|chain
index|[
name|index
index|[
literal|0
index|]
index|]
argument_list|,
name|reader
argument_list|)
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// NOTE: may set bits for deleted docs.
operator|++
name|index
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|OpenBitSetDISI
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|// TODO: in 3.0, instead of removing this deprecated
comment|// method, make it a no-op and mark it final
comment|/** Provide a SortedVIntList when it is definitely      *  smaller than an OpenBitSet      *  @deprecated Either use CachingWrapperFilter, or      *  switch to a different DocIdSet implementation yourself. */
DECL|method|finalResult
specifier|protected
name|DocIdSet
name|finalResult
parameter_list|(
name|OpenBitSetDISI
name|result
parameter_list|,
name|int
name|maxDocs
parameter_list|)
block|{
return|return
operator|(
name|result
operator|.
name|cardinality
argument_list|()
operator|<
operator|(
name|maxDocs
operator|/
literal|9
operator|)
operator|)
condition|?
operator|(
name|DocIdSet
operator|)
operator|new
name|SortedVIntList
argument_list|(
name|result
argument_list|)
else|:
operator|(
name|DocIdSet
operator|)
name|result
return|;
block|}
comment|/**      * Delegates to each filter in the chain.      * @param reader IndexReader      * @param logic Logical operation      * @return DocIdSet      */
DECL|method|getDocIdSet
specifier|private
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|logic
parameter_list|,
name|int
index|[]
name|index
parameter_list|)
throws|throws
name|IOException
block|{
name|OpenBitSetDISI
name|result
init|=
name|initialResult
argument_list|(
name|reader
argument_list|,
name|logic
argument_list|,
name|index
argument_list|)
decl_stmt|;
for|for
control|(
init|;
name|index
index|[
literal|0
index|]
operator|<
name|chain
operator|.
name|length
condition|;
name|index
index|[
literal|0
index|]
operator|++
control|)
block|{
name|doChain
argument_list|(
name|result
argument_list|,
name|logic
argument_list|,
name|chain
index|[
name|index
index|[
literal|0
index|]
index|]
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|finalResult
argument_list|(
name|result
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Delegates to each filter in the chain.      * @param reader IndexReader      * @param logic Logical operation      * @return DocIdSet      */
DECL|method|getDocIdSet
specifier|private
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
index|[]
name|logic
parameter_list|,
name|int
index|[]
name|index
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
name|OpenBitSetDISI
name|result
init|=
name|initialResult
argument_list|(
name|reader
argument_list|,
name|logic
index|[
literal|0
index|]
argument_list|,
name|index
argument_list|)
decl_stmt|;
for|for
control|(
init|;
name|index
index|[
literal|0
index|]
operator|<
name|chain
operator|.
name|length
condition|;
name|index
index|[
literal|0
index|]
operator|++
control|)
block|{
name|doChain
argument_list|(
name|result
argument_list|,
name|logic
index|[
name|index
index|[
literal|0
index|]
index|]
argument_list|,
name|chain
index|[
name|index
index|[
literal|0
index|]
index|]
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|finalResult
argument_list|(
name|result
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|OpenBitSetDISI
name|result
parameter_list|,
name|int
name|logic
parameter_list|,
name|DocIdSet
name|dis
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dis
operator|instanceof
name|OpenBitSet
condition|)
block|{
comment|// optimized case for OpenBitSets
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
operator|(
name|OpenBitSet
operator|)
name|dis
argument_list|)
expr_stmt|;
break|break;
case|case
name|AND
case|:
name|result
operator|.
name|and
argument_list|(
operator|(
name|OpenBitSet
operator|)
name|dis
argument_list|)
expr_stmt|;
break|break;
case|case
name|ANDNOT
case|:
name|result
operator|.
name|andNot
argument_list|(
operator|(
name|OpenBitSet
operator|)
name|dis
argument_list|)
expr_stmt|;
break|break;
case|case
name|XOR
case|:
name|result
operator|.
name|xor
argument_list|(
operator|(
name|OpenBitSet
operator|)
name|dis
argument_list|)
expr_stmt|;
break|break;
default|default:
name|doChain
argument_list|(
name|result
argument_list|,
name|DEFAULT
argument_list|,
name|dis
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|DocIdSetIterator
name|disi
decl_stmt|;
if|if
condition|(
name|dis
operator|==
literal|null
condition|)
block|{
name|disi
operator|=
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|disi
operator|=
name|dis
operator|.
name|iterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|disi
operator|==
literal|null
condition|)
block|{
name|disi
operator|=
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
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
name|inPlaceOr
argument_list|(
name|disi
argument_list|)
expr_stmt|;
break|break;
case|case
name|AND
case|:
name|result
operator|.
name|inPlaceAnd
argument_list|(
name|disi
argument_list|)
expr_stmt|;
break|break;
case|case
name|ANDNOT
case|:
name|result
operator|.
name|inPlaceNot
argument_list|(
name|disi
argument_list|)
expr_stmt|;
break|break;
case|case
name|XOR
case|:
name|result
operator|.
name|inPlaceXor
argument_list|(
name|disi
argument_list|)
expr_stmt|;
break|break;
default|default:
name|doChain
argument_list|(
name|result
argument_list|,
name|DEFAULT
argument_list|,
name|dis
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class
end_unit
