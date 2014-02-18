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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Map
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
name|AtomicReaderContext
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
name|index
operator|.
name|TermContext
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
name|Bits
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
implements|implements
name|Cloneable
block|{
DECL|field|clauses
specifier|private
name|List
argument_list|<
name|SpanQuery
argument_list|>
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
modifier|...
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
argument_list|<
name|SpanQuery
argument_list|>
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
name|addClause
argument_list|(
name|clauses
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Adds a clause to this query */
DECL|method|addClause
specifier|public
specifier|final
name|void
name|addClause
parameter_list|(
name|SpanQuery
name|clause
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
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
name|clause
operator|.
name|getField
argument_list|()
operator|!=
literal|null
operator|&&
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
comment|/** Return the clauses whose spans are matched. */
DECL|method|getClauses
specifier|public
name|SpanQuery
index|[]
name|getClauses
parameter_list|()
block|{
return|return
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
annotation|@
name|Override
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
annotation|@
name|Override
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
for|for
control|(
specifier|final
name|SpanQuery
name|clause
range|:
name|clauses
control|)
block|{
name|clause
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SpanOrQuery
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
name|newClauses
index|[
name|i
index|]
operator|=
operator|(
name|SpanQuery
operator|)
name|clauses
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
name|SpanOrQuery
name|soq
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|newClauses
argument_list|)
decl_stmt|;
name|soq
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|soq
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
literal|"spanOr(["
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|SpanQuery
argument_list|>
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
annotation|@
name|Override
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
annotation|@
name|Override
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
argument_list|<
name|Spans
argument_list|>
block|{
DECL|method|SpanQueue
specifier|public
name|SpanQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
name|Spans
name|spans1
parameter_list|,
name|Spans
name|spans2
parameter_list|)
block|{
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
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|AtomicReaderContext
name|context
parameter_list|,
specifier|final
name|Bits
name|acceptDocs
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|termContexts
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
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|termContexts
argument_list|)
return|;
return|return
operator|new
name|Spans
argument_list|()
block|{
specifier|private
name|SpanQueue
name|queue
init|=
literal|null
decl_stmt|;
specifier|private
name|long
name|cost
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
argument_list|<
name|SpanQuery
argument_list|>
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
name|Spans
name|spans
init|=
name|i
operator|.
name|next
argument_list|()
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|termContexts
argument_list|)
decl_stmt|;
name|cost
operator|+=
name|spans
operator|.
name|cost
argument_list|()
expr_stmt|;
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
name|add
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
annotation|@
name|Override
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
name|updateTop
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
name|Spans
name|top
parameter_list|()
block|{
return|return
name|queue
operator|.
name|top
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|boolean
name|skipCalled
init|=
literal|false
decl_stmt|;
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
name|updateTop
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
name|skipCalled
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|skipCalled
condition|)
block|{
return|return
name|queue
operator|.
name|size
argument_list|()
operator|!=
literal|0
return|;
block|}
return|return
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|byte
index|[]
argument_list|>
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
name|Spans
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
argument_list|<
name|byte
index|[]
argument_list|>
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
annotation|@
name|Override
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
throws|throws
name|IOException
block|{
name|Spans
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
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
