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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_class
DECL|class|NearSpans
class|class
name|NearSpans
implements|implements
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
name|ordered
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|// spans in query order
DECL|field|slop
specifier|private
name|int
name|slop
decl_stmt|;
comment|// from query
DECL|field|inOrder
specifier|private
name|boolean
name|inOrder
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
block|{
DECL|method|CellQueue
specifier|public
name|CellQueue
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
name|SpansCell
name|spans1
init|=
operator|(
name|SpansCell
operator|)
name|o1
decl_stmt|;
name|SpansCell
name|spans2
init|=
operator|(
name|SpansCell
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
if|if
condition|(
name|spans1
operator|.
name|end
argument_list|()
operator|==
name|spans2
operator|.
name|end
argument_list|()
condition|)
block|{
return|return
name|spans1
operator|.
name|index
operator|>
name|spans2
operator|.
name|index
return|;
block|}
else|else
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
comment|/** Wraps a Spans, and can be used to form a linked list. */
DECL|class|SpansCell
specifier|private
class|class
name|SpansCell
implements|implements
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
name|length
operator|!=
operator|-
literal|1
condition|)
comment|// subtract old length
name|totalLength
operator|-=
name|length
expr_stmt|;
name|boolean
name|more
init|=
name|spans
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// move to next
if|if
condition|(
name|more
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
comment|// compute new length
name|totalLength
operator|+=
name|length
expr_stmt|;
comment|// add new length to total
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
comment|// maintain max
operator|(
name|doc
argument_list|()
operator|==
name|max
operator|.
name|doc
argument_list|()
operator|&&
name|end
argument_list|()
operator|>
name|max
operator|.
name|end
argument_list|()
operator|)
condition|)
name|max
operator|=
name|this
expr_stmt|;
block|}
return|return
name|more
return|;
block|}
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
name|length
operator|!=
operator|-
literal|1
condition|)
comment|// subtract old length
name|totalLength
operator|-=
name|length
expr_stmt|;
name|boolean
name|more
init|=
name|spans
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
decl_stmt|;
comment|// skip
if|if
condition|(
name|more
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
comment|// compute new length
name|totalLength
operator|+=
name|length
expr_stmt|;
comment|// add new length to total
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
comment|// maintain max
operator|(
name|doc
argument_list|()
operator|==
name|max
operator|.
name|doc
argument_list|()
operator|&&
name|end
argument_list|()
operator|>
name|max
operator|.
name|end
argument_list|()
operator|)
condition|)
name|max
operator|=
name|this
expr_stmt|;
block|}
return|return
name|more
return|;
block|}
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
DECL|method|NearSpans
specifier|public
name|NearSpans
parameter_list|(
name|SpanNearQuery
name|query
parameter_list|,
name|IndexReader
name|reader
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
name|this
operator|.
name|inOrder
operator|=
name|query
operator|.
name|isInOrder
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
comment|// initialize spans& list
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
comment|// construct clause spans
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
name|reader
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
comment|// add to ordered
block|}
block|}
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
name|more
operator|=
name|min
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// trigger further scanning
if|if
condition|(
name|more
condition|)
name|queue
operator|.
name|adjustTop
argument_list|()
expr_stmt|;
comment|// maintain queue
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
return|return
literal|true
return|;
comment|// trigger further scanning
if|if
condition|(
name|inOrder
operator|&&
name|checkSlop
argument_list|()
condition|)
block|{
comment|/* There is a non ordered match within slop and an ordered match is needed. */
name|more
operator|=
name|firstNonOrderedNextToPartialList
argument_list|()
expr_stmt|;
if|if
condition|(
name|more
condition|)
block|{
name|partialListToQueue
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
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
name|adjustTop
argument_list|()
expr_stmt|;
comment|// maintain queue
block|}
block|}
block|}
return|return
literal|false
return|;
comment|// no more matches
block|}
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
name|more
operator|=
name|min
argument_list|()
operator|.
name|skipTo
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|more
condition|)
name|queue
operator|.
name|adjustTop
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|more
condition|)
block|{
if|if
condition|(
name|atMatch
argument_list|()
condition|)
comment|// at a match?
return|return
literal|true
return|;
return|return
name|next
argument_list|()
return|;
comment|// no, scan
block|}
return|return
literal|false
return|;
block|}
DECL|method|min
specifier|private
name|SpansCell
name|min
parameter_list|()
block|{
return|return
operator|(
name|SpansCell
operator|)
name|queue
operator|.
name|top
argument_list|()
return|;
block|}
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
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"spans("
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
operator|(
name|SpansCell
operator|)
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
operator|(
name|SpansCell
operator|)
name|queue
operator|.
name|pop
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|firstNonOrderedNextToPartialList
specifier|private
name|boolean
name|firstNonOrderedNextToPartialList
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* Creates a partial list consisting of first non ordered and earlier.      * Returns first non ordered .next().      */
name|last
operator|=
name|first
operator|=
literal|null
expr_stmt|;
name|int
name|orderedIndex
init|=
literal|0
decl_stmt|;
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
name|SpansCell
name|cell
init|=
operator|(
name|SpansCell
operator|)
name|queue
operator|.
name|pop
argument_list|()
decl_stmt|;
name|addToList
argument_list|(
name|cell
argument_list|)
expr_stmt|;
if|if
condition|(
name|cell
operator|.
name|index
operator|==
name|orderedIndex
condition|)
block|{
name|orderedIndex
operator|++
expr_stmt|;
block|}
else|else
block|{
return|return
name|cell
operator|.
name|next
argument_list|()
return|;
comment|// FIXME: continue here, rename to eg. checkOrderedMatch():
comment|// when checkSlop() and not ordered, repeat cell.next().
comment|// when checkSlop() and ordered, add to list and repeat queue.pop()
comment|// without checkSlop(): no match, rebuild the queue from the partial list.
comment|// When queue is empty and checkSlop() and ordered there is a match.
block|}
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unexpected: ordered"
argument_list|)
throw|;
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
name|partialListToQueue
argument_list|()
expr_stmt|;
block|}
DECL|method|partialListToQueue
specifier|private
name|void
name|partialListToQueue
parameter_list|()
block|{
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
name|put
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
name|checkSlop
argument_list|()
operator|&&
operator|(
operator|!
name|inOrder
operator|||
name|matchIsOrdered
argument_list|()
operator|)
return|;
block|}
DECL|method|checkSlop
specifier|private
name|boolean
name|checkSlop
parameter_list|()
block|{
name|int
name|matchLength
init|=
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
decl_stmt|;
return|return
operator|(
name|matchLength
operator|-
name|totalLength
operator|)
operator|<=
name|slop
return|;
block|}
DECL|method|matchIsOrdered
specifier|private
name|boolean
name|matchIsOrdered
parameter_list|()
block|{
name|int
name|lastStart
init|=
operator|-
literal|1
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
name|ordered
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|start
init|=
operator|(
operator|(
name|SpansCell
operator|)
name|ordered
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|start
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|start
operator|>
name|lastStart
operator|)
condition|)
return|return
literal|false
return|;
name|lastStart
operator|=
name|start
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
