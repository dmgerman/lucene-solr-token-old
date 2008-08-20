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
name|List
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
name|util
operator|.
name|PriorityQueue
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
begin_comment
comment|/** Matches the union of its clauses.*/
end_comment
begin_class
DECL|class|SpanOrQuery
specifier|public
class|class
name|SpanOrQuery
extends|extends
name|SpanQuery
block|{
DECL|field|clauses
specifier|private
name|List
name|clauses
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
comment|/** Construct a SpanOrQuery merging the provided clauses. */
DECL|method|SpanOrQuery
specifier|public
name|SpanOrQuery
parameter_list|(
name|SpanQuery
index|[]
name|clauses
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
comment|/** Returns a collection of all terms matched by this query.    * @deprecated use extractTerms instead    * @see #extractTerms(Set)    */
DECL|method|getTerms
specifier|public
name|Collection
name|getTerms
parameter_list|()
block|{
name|Collection
name|terms
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
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
name|terms
operator|.
name|addAll
argument_list|(
name|clause
operator|.
name|getTerms
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|terms
return|;
block|}
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
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
name|SpanOrQuery
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
name|SpanOrQuery
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
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"spanOr(["
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
literal|"])"
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
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|SpanOrQuery
name|that
init|=
operator|(
name|SpanOrQuery
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|clauses
operator|.
name|equals
argument_list|(
name|that
operator|.
name|clauses
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|that
operator|.
name|field
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
name|getBoost
argument_list|()
operator|==
name|that
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
name|h
init|=
name|clauses
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|^=
operator|(
name|h
operator|<<
literal|10
operator|)
operator||
operator|(
name|h
operator|>>>
literal|23
operator|)
expr_stmt|;
name|h
operator|^=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
DECL|class|SpanQueue
specifier|private
class|class
name|SpanQueue
extends|extends
name|PriorityQueue
block|{
DECL|method|SpanQueue
specifier|public
name|SpanQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|Spans
name|spans1
init|=
operator|(
name|Spans
operator|)
name|o1
decl_stmt|;
name|Spans
name|spans2
init|=
operator|(
name|Spans
operator|)
name|o2
decl_stmt|;
if|if
condition|(
name|spans1
operator|.
name|doc
argument_list|()
operator|==
name|spans2
operator|.
name|doc
argument_list|()
condition|)
block|{
if|if
condition|(
name|spans1
operator|.
name|start
argument_list|()
operator|==
name|spans2
operator|.
name|start
argument_list|()
condition|)
block|{
return|return
name|spans1
operator|.
name|end
argument_list|()
operator|<
name|spans2
operator|.
name|end
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|spans1
operator|.
name|start
argument_list|()
operator|<
name|spans2
operator|.
name|start
argument_list|()
return|;
block|}
block|}
else|else
block|{
return|return
name|spans1
operator|.
name|doc
argument_list|()
operator|<
name|spans2
operator|.
name|doc
argument_list|()
return|;
block|}
block|}
block|}
DECL|method|getPayloadSpans
specifier|public
name|PayloadSpans
name|getPayloadSpans
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|PayloadSpans
operator|)
name|getSpans
argument_list|(
name|reader
argument_list|)
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
name|getPayloadSpans
argument_list|(
name|reader
argument_list|)
return|;
return|return
operator|new
name|PayloadSpans
argument_list|()
block|{
specifier|private
name|SpanQueue
name|queue
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|initSpanQueue
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|queue
operator|=
operator|new
name|SpanQueue
argument_list|(
name|clauses
operator|.
name|size
argument_list|()
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
name|PayloadSpans
name|spans
init|=
operator|(
operator|(
name|SpanQuery
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|getPayloadSpans
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
operator|(
name|target
operator|==
operator|-
literal|1
operator|)
operator|&&
name|spans
operator|.
name|next
argument_list|()
operator|)
operator|||
operator|(
operator|(
name|target
operator|!=
operator|-
literal|1
operator|)
operator|&&
name|spans
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
operator|)
condition|)
block|{
name|queue
operator|.
name|put
argument_list|(
name|spans
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|queue
operator|.
name|size
argument_list|()
operator|!=
literal|0
return|;
block|}
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
return|return
name|initSpanQueue
argument_list|(
operator|-
literal|1
argument_list|)
return|;
block|}
if|if
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// all done
return|return
literal|false
return|;
block|}
if|if
condition|(
name|top
argument_list|()
operator|.
name|next
argument_list|()
condition|)
block|{
comment|// move to next
name|queue
operator|.
name|adjustTop
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
comment|// exhausted a clause
return|return
name|queue
operator|.
name|size
argument_list|()
operator|!=
literal|0
return|;
block|}
specifier|private
name|PayloadSpans
name|top
parameter_list|()
block|{
return|return
operator|(
name|PayloadSpans
operator|)
name|queue
operator|.
name|top
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
return|return
name|initSpanQueue
argument_list|(
name|target
argument_list|)
return|;
block|}
while|while
condition|(
name|queue
operator|.
name|size
argument_list|()
operator|!=
literal|0
operator|&&
name|top
argument_list|()
operator|.
name|doc
argument_list|()
operator|<
name|target
condition|)
block|{
if|if
condition|(
name|top
argument_list|()
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|queue
operator|.
name|adjustTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|queue
operator|.
name|size
argument_list|()
operator|!=
literal|0
return|;
block|}
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|top
argument_list|()
operator|.
name|doc
argument_list|()
return|;
block|}
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
name|top
argument_list|()
operator|.
name|start
argument_list|()
return|;
block|}
specifier|public
name|int
name|end
parameter_list|()
block|{
return|return
name|top
argument_list|()
operator|.
name|end
argument_list|()
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
specifier|public
name|Collection
comment|/*<byte[]>*/
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
name|ArrayList
name|result
init|=
literal|null
decl_stmt|;
name|PayloadSpans
name|theTop
init|=
name|top
argument_list|()
decl_stmt|;
if|if
condition|(
name|theTop
operator|!=
literal|null
operator|&&
name|theTop
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|result
operator|=
operator|new
name|ArrayList
argument_list|(
name|theTop
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
name|PayloadSpans
name|top
init|=
name|top
argument_list|()
decl_stmt|;
return|return
name|top
operator|!=
literal|null
operator|&&
name|top
operator|.
name|isPayloadAvailable
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"spans("
operator|+
name|SpanOrQuery
operator|.
name|this
operator|+
literal|")@"
operator|+
operator|(
operator|(
name|queue
operator|==
literal|null
operator|)
condition|?
literal|"START"
else|:
operator|(
name|queue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|?
operator|(
name|doc
argument_list|()
operator|+
literal|":"
operator|+
name|start
argument_list|()
operator|+
literal|"-"
operator|+
name|end
argument_list|()
operator|)
else|:
literal|"END"
operator|)
operator|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
