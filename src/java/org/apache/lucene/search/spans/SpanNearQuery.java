begin_unit
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|List
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
name|Iterator
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
name|index
operator|.
name|Term
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
name|Query
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
name|ToStringUtils
import|;
end_import
begin_comment
comment|/** Matches spans which are near one another.  One can specify<i>slop</i>, the  * maximum number of intervening unmatched positions, as well as whether  * matches are required to be in-order. */
end_comment
begin_class
DECL|class|SpanNearQuery
specifier|public
class|class
name|SpanNearQuery
extends|extends
name|SpanQuery
implements|implements
name|Cloneable
block|{
DECL|field|clauses
specifier|protected
name|List
name|clauses
decl_stmt|;
DECL|field|slop
specifier|protected
name|int
name|slop
decl_stmt|;
DECL|field|inOrder
specifier|protected
name|boolean
name|inOrder
decl_stmt|;
DECL|field|field
specifier|protected
name|String
name|field
decl_stmt|;
DECL|field|collectPayloads
specifier|private
name|boolean
name|collectPayloads
decl_stmt|;
comment|/** Construct a SpanNearQuery.  Matches spans matching a span from each    * clause, with up to<code>slop</code> total unmatched positions between    * them.  * When<code>inOrder</code> is true, the spans from each clause    * must be * ordered as in<code>clauses</code>. */
DECL|method|SpanNearQuery
specifier|public
name|SpanNearQuery
parameter_list|(
name|SpanQuery
index|[]
name|clauses
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
name|this
argument_list|(
name|clauses
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|SpanNearQuery
specifier|public
name|SpanNearQuery
parameter_list|(
name|SpanQuery
index|[]
name|clauses
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|,
name|boolean
name|collectPayloads
parameter_list|)
block|{
comment|// copy clauses array into an ArrayList
name|this
operator|.
name|clauses
operator|=
operator|new
name|ArrayList
argument_list|(
name|clauses
operator|.
name|length
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
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SpanQuery
name|clause
init|=
name|clauses
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
comment|// check field
name|field
operator|=
name|clause
operator|.
name|getField
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|clause
operator|.
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Clauses must have same field."
argument_list|)
throw|;
block|}
name|this
operator|.
name|clauses
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|collectPayloads
operator|=
name|collectPayloads
expr_stmt|;
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
name|this
operator|.
name|inOrder
operator|=
name|inOrder
expr_stmt|;
block|}
comment|/** Return the clauses whose spans are matched. */
DECL|method|getClauses
specifier|public
name|SpanQuery
index|[]
name|getClauses
parameter_list|()
block|{
return|return
operator|(
name|SpanQuery
index|[]
operator|)
name|clauses
operator|.
name|toArray
argument_list|(
operator|new
name|SpanQuery
index|[
name|clauses
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/** Return the maximum number of intervening unmatched positions permitted.*/
DECL|method|getSlop
specifier|public
name|int
name|getSlop
parameter_list|()
block|{
return|return
name|slop
return|;
block|}
comment|/** Return true if matches are required to be in-order.*/
DECL|method|isInOrder
specifier|public
name|boolean
name|isInOrder
parameter_list|()
block|{
return|return
name|inOrder
return|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|Iterator
name|i
init|=
name|clauses
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SpanQuery
name|clause
init|=
operator|(
name|SpanQuery
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|clause
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"spanNear(["
argument_list|)
expr_stmt|;
name|Iterator
name|i
init|=
name|clauses
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SpanQuery
name|clause
init|=
operator|(
name|SpanQuery
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|clause
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|inOrder
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
comment|// optimize 0-clause case
return|return
operator|new
name|SpanOrQuery
argument_list|(
name|getClauses
argument_list|()
argument_list|)
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
return|;
if|if
condition|(
name|clauses
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
comment|// optimize 1-clause case
return|return
operator|(
operator|(
name|SpanQuery
operator|)
name|clauses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
return|;
return|return
name|inOrder
condition|?
operator|(
name|Spans
operator|)
operator|new
name|NearSpansOrdered
argument_list|(
name|this
argument_list|,
name|reader
argument_list|,
name|collectPayloads
argument_list|)
else|:
operator|(
name|Spans
operator|)
operator|new
name|NearSpansUnordered
argument_list|(
name|this
argument_list|,
name|reader
argument_list|)
return|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanNearQuery
name|clone
init|=
literal|null
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
name|clauses
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SpanQuery
name|c
init|=
operator|(
name|SpanQuery
operator|)
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SpanQuery
name|query
init|=
operator|(
name|SpanQuery
operator|)
name|c
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
name|c
condition|)
block|{
comment|// clause rewrote: must clone
if|if
condition|(
name|clone
operator|==
literal|null
condition|)
name|clone
operator|=
operator|(
name|SpanNearQuery
operator|)
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|clauses
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clone
operator|!=
literal|null
condition|)
block|{
return|return
name|clone
return|;
comment|// some clauses rewrote
block|}
else|else
block|{
return|return
name|this
return|;
comment|// no clauses rewrote
block|}
block|}
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|int
name|sz
init|=
name|clauses
operator|.
name|size
argument_list|()
decl_stmt|;
name|SpanQuery
index|[]
name|newClauses
init|=
operator|new
name|SpanQuery
index|[
name|sz
index|]
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|SpanQuery
name|clause
init|=
operator|(
name|SpanQuery
operator|)
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|newClauses
index|[
name|i
index|]
operator|=
operator|(
name|SpanQuery
operator|)
name|clause
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
name|SpanNearQuery
name|spanNearQuery
init|=
operator|new
name|SpanNearQuery
argument_list|(
name|newClauses
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
decl_stmt|;
name|spanNearQuery
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|spanNearQuery
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|SpanNearQuery
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|SpanNearQuery
name|spanNearQuery
init|=
operator|(
name|SpanNearQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|inOrder
operator|!=
name|spanNearQuery
operator|.
name|inOrder
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|slop
operator|!=
name|spanNearQuery
operator|.
name|slop
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|clauses
operator|.
name|equals
argument_list|(
name|spanNearQuery
operator|.
name|clauses
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
name|getBoost
argument_list|()
operator|==
name|spanNearQuery
operator|.
name|getBoost
argument_list|()
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
decl_stmt|;
name|result
operator|=
name|clauses
operator|.
name|hashCode
argument_list|()
expr_stmt|;
comment|// Mix bits before folding in things like boost, since it could cancel the
comment|// last element of clauses.  This particular mix also serves to
comment|// differentiate SpanNearQuery hashcodes from others.
name|result
operator|^=
operator|(
name|result
operator|<<
literal|14
operator|)
operator||
operator|(
name|result
operator|>>>
literal|19
operator|)
expr_stmt|;
comment|// reversible
name|result
operator|+=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|+=
name|slop
expr_stmt|;
name|result
operator|^=
operator|(
name|inOrder
condition|?
literal|0x99AFD3BD
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
