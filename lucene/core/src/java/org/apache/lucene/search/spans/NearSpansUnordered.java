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
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_comment
comment|/**  * Similar to {@link NearSpansOrdered}, but for the unordered case.  *   * Expert:  * Only public for subclassing.  Most implementations should not need this class  */
end_comment
begin_class
DECL|class|NearSpansUnordered
specifier|public
class|class
name|NearSpansUnordered
extends|extends
name|Spans
block|{
DECL|field|query
specifier|private
name|SpanNearQuery
name|query
decl_stmt|;
DECL|field|ordered
specifier|private
name|List
argument_list|<
name|SpansCell
argument_list|>
name|ordered
init|=
operator|new
name|ArrayList
argument_list|<
name|SpansCell
argument_list|>
argument_list|()
decl_stmt|;
comment|// spans in query order
DECL|field|subSpans
specifier|private
name|Spans
index|[]
name|subSpans
decl_stmt|;
DECL|field|slop
specifier|private
name|int
name|slop
decl_stmt|;
comment|// from query
DECL|field|first
specifier|private
name|SpansCell
name|first
decl_stmt|;
comment|// linked list of spans
DECL|field|last
specifier|private
name|SpansCell
name|last
decl_stmt|;
comment|// sorted by doc only
DECL|field|totalLength
specifier|private
name|int
name|totalLength
decl_stmt|;
comment|// sum of current lengths
DECL|field|queue
specifier|private
name|CellQueue
name|queue
decl_stmt|;
comment|// sorted queue of spans
DECL|field|max
specifier|private
name|SpansCell
name|max
decl_stmt|;
comment|// max element in queue
DECL|field|more
specifier|private
name|boolean
name|more
init|=
literal|true
decl_stmt|;
comment|// true iff not done
DECL|field|firstTime
specifier|private
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
comment|// true before first next()
DECL|class|CellQueue
specifier|private
class|class
name|CellQueue
extends|extends
name|PriorityQueue
argument_list|<
name|SpansCell
argument_list|>
block|{
DECL|method|CellQueue
specifier|public
name|CellQueue
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
name|SpansCell
name|spans1
parameter_list|,
name|SpansCell
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
return|return
name|NearSpansOrdered
operator|.
name|docSpansOrdered
argument_list|(
name|spans1
argument_list|,
name|spans2
argument_list|)
return|;
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
comment|/** Wraps a Spans, and can be used to form a linked list. */
DECL|class|SpansCell
specifier|private
class|class
name|SpansCell
extends|extends
name|Spans
block|{
DECL|field|spans
specifier|private
name|Spans
name|spans
decl_stmt|;
DECL|field|next
specifier|private
name|SpansCell
name|next
decl_stmt|;
DECL|field|length
specifier|private
name|int
name|length
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|index
specifier|private
name|int
name|index
decl_stmt|;
DECL|method|SpansCell
specifier|public
name|SpansCell
parameter_list|(
name|Spans
name|spans
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|this
operator|.
name|spans
operator|=
name|spans
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|adjust
argument_list|(
name|spans
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|skipTo
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
return|return
name|adjust
argument_list|(
name|spans
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
argument_list|)
return|;
block|}
DECL|method|adjust
specifier|private
name|boolean
name|adjust
parameter_list|(
name|boolean
name|condition
parameter_list|)
block|{
if|if
condition|(
name|length
operator|!=
operator|-
literal|1
condition|)
block|{
name|totalLength
operator|-=
name|length
expr_stmt|;
comment|// subtract old length
block|}
if|if
condition|(
name|condition
condition|)
block|{
name|length
operator|=
name|end
argument_list|()
operator|-
name|start
argument_list|()
expr_stmt|;
name|totalLength
operator|+=
name|length
expr_stmt|;
comment|// add new length
if|if
condition|(
name|max
operator|==
literal|null
operator|||
name|doc
argument_list|()
operator|>
name|max
operator|.
name|doc
argument_list|()
operator|||
operator|(
name|doc
argument_list|()
operator|==
name|max
operator|.
name|doc
argument_list|()
operator|)
operator|&&
operator|(
name|end
argument_list|()
operator|>
name|max
operator|.
name|end
argument_list|()
operator|)
condition|)
block|{
name|max
operator|=
name|this
expr_stmt|;
block|}
block|}
name|more
operator|=
name|condition
expr_stmt|;
return|return
name|condition
return|;
block|}
annotation|@
name|Override
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|spans
operator|.
name|doc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
name|spans
operator|.
name|start
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|int
name|end
parameter_list|()
block|{
return|return
name|spans
operator|.
name|end
argument_list|()
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
annotation|@
name|Override
DECL|method|getPayload
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
return|return
operator|new
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|(
name|spans
operator|.
name|getPayload
argument_list|()
argument_list|)
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
annotation|@
name|Override
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|spans
operator|.
name|isPayloadAvailable
argument_list|()
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
return|return
name|spans
operator|.
name|toString
argument_list|()
operator|+
literal|"#"
operator|+
name|index
return|;
block|}
block|}
DECL|method|NearSpansUnordered
specifier|public
name|NearSpansUnordered
parameter_list|(
name|SpanNearQuery
name|query
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
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
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|slop
operator|=
name|query
operator|.
name|getSlop
argument_list|()
expr_stmt|;
name|SpanQuery
index|[]
name|clauses
init|=
name|query
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|queue
operator|=
operator|new
name|CellQueue
argument_list|(
name|clauses
operator|.
name|length
argument_list|)
expr_stmt|;
name|subSpans
operator|=
operator|new
name|Spans
index|[
name|clauses
operator|.
name|length
index|]
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
name|SpansCell
name|cell
init|=
operator|new
name|SpansCell
argument_list|(
name|clauses
index|[
name|i
index|]
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|,
name|termContexts
argument_list|)
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|ordered
operator|.
name|add
argument_list|(
name|cell
argument_list|)
expr_stmt|;
name|subSpans
index|[
name|i
index|]
operator|=
name|cell
operator|.
name|spans
expr_stmt|;
block|}
block|}
DECL|method|getSubSpans
specifier|public
name|Spans
index|[]
name|getSubSpans
parameter_list|()
block|{
return|return
name|subSpans
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|firstTime
condition|)
block|{
name|initList
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|listToQueue
argument_list|()
expr_stmt|;
comment|// initialize queue
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|more
condition|)
block|{
if|if
condition|(
name|min
argument_list|()
operator|.
name|next
argument_list|()
condition|)
block|{
comment|// trigger further scanning
name|queue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
comment|// maintain queue
block|}
else|else
block|{
name|more
operator|=
literal|false
expr_stmt|;
block|}
block|}
while|while
condition|(
name|more
condition|)
block|{
name|boolean
name|queueStale
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|min
argument_list|()
operator|.
name|doc
argument_list|()
operator|!=
name|max
operator|.
name|doc
argument_list|()
condition|)
block|{
comment|// maintain list
name|queueToList
argument_list|()
expr_stmt|;
name|queueStale
operator|=
literal|true
expr_stmt|;
block|}
comment|// skip to doc w/ all clauses
while|while
condition|(
name|more
operator|&&
name|first
operator|.
name|doc
argument_list|()
operator|<
name|last
operator|.
name|doc
argument_list|()
condition|)
block|{
name|more
operator|=
name|first
operator|.
name|skipTo
argument_list|(
name|last
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
comment|// skip first upto last
name|firstToLast
argument_list|()
expr_stmt|;
comment|// and move it to the end
name|queueStale
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|more
condition|)
return|return
literal|false
return|;
comment|// found doc w/ all clauses
if|if
condition|(
name|queueStale
condition|)
block|{
comment|// maintain the queue
name|listToQueue
argument_list|()
expr_stmt|;
name|queueStale
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|atMatch
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|more
operator|=
name|min
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|more
condition|)
block|{
name|queue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
comment|// maintain queue
block|}
block|}
return|return
literal|false
return|;
comment|// no more matches
block|}
annotation|@
name|Override
DECL|method|skipTo
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
name|firstTime
condition|)
block|{
comment|// initialize
name|initList
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|SpansCell
name|cell
init|=
name|first
init|;
name|more
operator|&&
name|cell
operator|!=
literal|null
condition|;
name|cell
operator|=
name|cell
operator|.
name|next
control|)
block|{
name|more
operator|=
name|cell
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
comment|// skip all
block|}
if|if
condition|(
name|more
condition|)
block|{
name|listToQueue
argument_list|()
expr_stmt|;
block|}
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|// normal case
while|while
condition|(
name|more
operator|&&
name|min
argument_list|()
operator|.
name|doc
argument_list|()
operator|<
name|target
condition|)
block|{
comment|// skip as needed
if|if
condition|(
name|min
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
name|more
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
return|return
name|more
operator|&&
operator|(
name|atMatch
argument_list|()
operator|||
name|next
argument_list|()
operator|)
return|;
block|}
DECL|method|min
specifier|private
name|SpansCell
name|min
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
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|min
argument_list|()
operator|.
name|doc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
name|int
name|start
parameter_list|()
block|{
return|return
name|min
argument_list|()
operator|.
name|start
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|int
name|end
parameter_list|()
block|{
return|return
name|max
operator|.
name|end
argument_list|()
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
comment|/**    * WARNING: The List is not necessarily in order of the the positions    * @return Collection of<code>byte[]</code> payloads    * @throws IOException    */
annotation|@
name|Override
DECL|method|getPayload
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
name|Set
argument_list|<
name|byte
index|[]
argument_list|>
name|matchPayload
init|=
operator|new
name|HashSet
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SpansCell
name|cell
init|=
name|first
init|;
name|cell
operator|!=
literal|null
condition|;
name|cell
operator|=
name|cell
operator|.
name|next
control|)
block|{
if|if
condition|(
name|cell
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
name|matchPayload
operator|.
name|addAll
argument_list|(
name|cell
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|matchPayload
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
annotation|@
name|Override
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
throws|throws
name|IOException
block|{
name|SpansCell
name|pointer
init|=
name|min
argument_list|()
decl_stmt|;
while|while
condition|(
name|pointer
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|pointer
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|pointer
operator|=
name|pointer
operator|.
name|next
expr_stmt|;
block|}
return|return
literal|false
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
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"("
operator|+
name|query
operator|.
name|toString
argument_list|()
operator|+
literal|")@"
operator|+
operator|(
name|firstTime
condition|?
literal|"START"
else|:
operator|(
name|more
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
DECL|method|initList
specifier|private
name|void
name|initList
parameter_list|(
name|boolean
name|next
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|more
operator|&&
name|i
operator|<
name|ordered
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SpansCell
name|cell
init|=
name|ordered
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
condition|)
name|more
operator|=
name|cell
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// move to first entry
if|if
condition|(
name|more
condition|)
block|{
name|addToList
argument_list|(
name|cell
argument_list|)
expr_stmt|;
comment|// add to list
block|}
block|}
block|}
DECL|method|addToList
specifier|private
name|void
name|addToList
parameter_list|(
name|SpansCell
name|cell
parameter_list|)
block|{
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
comment|// add next to end of list
name|last
operator|.
name|next
operator|=
name|cell
expr_stmt|;
block|}
else|else
name|first
operator|=
name|cell
expr_stmt|;
name|last
operator|=
name|cell
expr_stmt|;
name|cell
operator|.
name|next
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|firstToLast
specifier|private
name|void
name|firstToLast
parameter_list|()
block|{
name|last
operator|.
name|next
operator|=
name|first
expr_stmt|;
comment|// move first to end of list
name|last
operator|=
name|first
expr_stmt|;
name|first
operator|=
name|first
operator|.
name|next
expr_stmt|;
name|last
operator|.
name|next
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|queueToList
specifier|private
name|void
name|queueToList
parameter_list|()
block|{
name|last
operator|=
name|first
operator|=
literal|null
expr_stmt|;
while|while
condition|(
name|queue
operator|.
name|top
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|addToList
argument_list|(
name|queue
operator|.
name|pop
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|listToQueue
specifier|private
name|void
name|listToQueue
parameter_list|()
block|{
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// rebuild queue
for|for
control|(
name|SpansCell
name|cell
init|=
name|first
init|;
name|cell
operator|!=
literal|null
condition|;
name|cell
operator|=
name|cell
operator|.
name|next
control|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|cell
argument_list|)
expr_stmt|;
comment|// add to queue from list
block|}
block|}
DECL|method|atMatch
specifier|private
name|boolean
name|atMatch
parameter_list|()
block|{
return|return
operator|(
name|min
argument_list|()
operator|.
name|doc
argument_list|()
operator|==
name|max
operator|.
name|doc
argument_list|()
operator|)
operator|&&
operator|(
operator|(
name|max
operator|.
name|end
argument_list|()
operator|-
name|min
argument_list|()
operator|.
name|start
argument_list|()
operator|-
name|totalLength
operator|)
operator|<=
name|slop
operator|)
return|;
block|}
block|}
end_class
end_unit
