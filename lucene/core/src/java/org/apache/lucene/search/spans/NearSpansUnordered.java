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
name|search
operator|.
name|TwoPhaseIterator
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
comment|/**  * Similar to {@link NearSpansOrdered}, but for the unordered case.  *  * Expert:  * Only public for subclassing.  Most implementations should not need this class  */
end_comment
begin_class
DECL|class|NearSpansUnordered
specifier|public
class|class
name|NearSpansUnordered
extends|extends
name|NearSpans
block|{
DECL|field|subSpanCells
specifier|private
name|List
argument_list|<
name|SpansCell
argument_list|>
name|subSpanCells
decl_stmt|;
comment|// in query order
DECL|field|spanPositionQueue
specifier|private
name|SpanPositionQueue
name|spanPositionQueue
decl_stmt|;
DECL|method|NearSpansUnordered
specifier|public
name|NearSpansUnordered
parameter_list|(
name|SpanNearQuery
name|query
parameter_list|,
name|List
argument_list|<
name|Spans
argument_list|>
name|subSpans
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|,
name|subSpans
argument_list|)
expr_stmt|;
name|this
operator|.
name|subSpanCells
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|subSpans
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Spans
name|subSpan
range|:
name|subSpans
control|)
block|{
comment|// sub spans in query order
name|this
operator|.
name|subSpanCells
operator|.
name|add
argument_list|(
operator|new
name|SpansCell
argument_list|(
name|subSpan
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|spanPositionQueue
operator|=
operator|new
name|SpanPositionQueue
argument_list|(
name|subSpans
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|singleCellToPositionQueue
argument_list|()
expr_stmt|;
comment|// -1 startPosition/endPosition also at doc -1
block|}
DECL|method|singleCellToPositionQueue
specifier|private
name|void
name|singleCellToPositionQueue
parameter_list|()
block|{
name|maxEndPositionCell
operator|=
name|subSpanCells
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
assert|assert
name|maxEndPositionCell
operator|.
name|docID
argument_list|()
operator|==
operator|-
literal|1
assert|;
assert|assert
name|maxEndPositionCell
operator|.
name|startPosition
argument_list|()
operator|==
operator|-
literal|1
assert|;
name|spanPositionQueue
operator|.
name|add
argument_list|(
name|maxEndPositionCell
argument_list|)
expr_stmt|;
block|}
DECL|method|subSpanCellsToPositionQueue
specifier|private
name|void
name|subSpanCellsToPositionQueue
parameter_list|()
throws|throws
name|IOException
block|{
comment|// used when all subSpanCells arrived at the same doc.
name|spanPositionQueue
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|SpansCell
name|cell
range|:
name|subSpanCells
control|)
block|{
assert|assert
name|cell
operator|.
name|startPosition
argument_list|()
operator|==
operator|-
literal|1
assert|;
name|cell
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
assert|assert
name|cell
operator|.
name|startPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
assert|;
name|spanPositionQueue
operator|.
name|add
argument_list|(
name|cell
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** SpansCell wraps a sub Spans to maintain totalSpanLength and maxEndPositionCell */
DECL|field|totalSpanLength
specifier|private
name|int
name|totalSpanLength
decl_stmt|;
DECL|field|maxEndPositionCell
specifier|private
name|SpansCell
name|maxEndPositionCell
decl_stmt|;
DECL|class|SpansCell
specifier|private
class|class
name|SpansCell
extends|extends
name|Spans
block|{
DECL|field|spanLength
specifier|private
name|int
name|spanLength
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|in
specifier|final
name|Spans
name|in
decl_stmt|;
DECL|method|SpansCell
specifier|public
name|SpansCell
parameter_list|(
name|Spans
name|spans
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|spans
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextStartPosition
specifier|public
name|int
name|nextStartPosition
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|res
init|=
name|in
operator|.
name|nextStartPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|res
operator|!=
name|NO_MORE_POSITIONS
condition|)
block|{
name|adjustLength
argument_list|()
expr_stmt|;
block|}
name|adjustMax
argument_list|()
expr_stmt|;
comment|// also after last end position in current doc.
return|return
name|res
return|;
block|}
DECL|method|adjustLength
specifier|private
name|void
name|adjustLength
parameter_list|()
block|{
if|if
condition|(
name|spanLength
operator|!=
operator|-
literal|1
condition|)
block|{
name|totalSpanLength
operator|-=
name|spanLength
expr_stmt|;
comment|// subtract old, possibly from a previous doc
block|}
assert|assert
name|in
operator|.
name|startPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
assert|;
name|spanLength
operator|=
name|endPosition
argument_list|()
operator|-
name|startPosition
argument_list|()
expr_stmt|;
assert|assert
name|spanLength
operator|>=
literal|0
assert|;
name|totalSpanLength
operator|+=
name|spanLength
expr_stmt|;
comment|// add new
block|}
DECL|method|adjustMax
specifier|private
name|void
name|adjustMax
parameter_list|()
block|{
assert|assert
name|docID
argument_list|()
operator|==
name|maxEndPositionCell
operator|.
name|docID
argument_list|()
assert|;
if|if
condition|(
name|endPosition
argument_list|()
operator|>
name|maxEndPositionCell
operator|.
name|endPosition
argument_list|()
condition|)
block|{
name|maxEndPositionCell
operator|=
name|this
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|startPosition
specifier|public
name|int
name|startPosition
parameter_list|()
block|{
return|return
name|in
operator|.
name|startPosition
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|endPosition
specifier|public
name|int
name|endPosition
parameter_list|()
block|{
return|return
name|in
operator|.
name|endPosition
argument_list|()
return|;
block|}
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
name|in
operator|.
name|getPayload
argument_list|()
return|;
block|}
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
name|in
operator|.
name|isPayloadAvailable
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
return|return
name|in
operator|.
name|asTwoPhaseIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|in
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|in
operator|.
name|cost
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
literal|"NearSpansUnordered.SpansCell("
operator|+
name|in
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
DECL|class|SpanPositionQueue
specifier|private
specifier|static
class|class
name|SpanPositionQueue
extends|extends
name|PriorityQueue
argument_list|<
name|SpansCell
argument_list|>
block|{
DECL|method|SpanPositionQueue
specifier|public
name|SpanPositionQueue
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
return|return
name|positionsOrdered
argument_list|(
name|spans1
argument_list|,
name|spans2
argument_list|)
return|;
block|}
block|}
comment|/** Check whether two Spans in the same document are ordered with possible overlap.    * @return true iff spans1 starts before spans2    *              or the spans start at the same position,    *              and spans1 ends before spans2.    */
DECL|method|positionsOrdered
specifier|static
specifier|final
name|boolean
name|positionsOrdered
parameter_list|(
name|Spans
name|spans1
parameter_list|,
name|Spans
name|spans2
parameter_list|)
block|{
assert|assert
name|spans1
operator|.
name|docID
argument_list|()
operator|==
name|spans2
operator|.
name|docID
argument_list|()
operator|:
literal|"doc1 "
operator|+
name|spans1
operator|.
name|docID
argument_list|()
operator|+
literal|" != doc2 "
operator|+
name|spans2
operator|.
name|docID
argument_list|()
assert|;
name|int
name|start1
init|=
name|spans1
operator|.
name|startPosition
argument_list|()
decl_stmt|;
name|int
name|start2
init|=
name|spans2
operator|.
name|startPosition
argument_list|()
decl_stmt|;
return|return
operator|(
name|start1
operator|==
name|start2
operator|)
condition|?
operator|(
name|spans1
operator|.
name|endPosition
argument_list|()
operator|<
name|spans2
operator|.
name|endPosition
argument_list|()
operator|)
else|:
operator|(
name|start1
operator|<
name|start2
operator|)
return|;
block|}
DECL|method|minPositionCell
specifier|private
name|SpansCell
name|minPositionCell
parameter_list|()
block|{
return|return
name|spanPositionQueue
operator|.
name|top
argument_list|()
return|;
block|}
DECL|method|atMatch
specifier|private
name|boolean
name|atMatch
parameter_list|()
block|{
assert|assert
name|minPositionCell
argument_list|()
operator|.
name|docID
argument_list|()
operator|==
name|maxEndPositionCell
operator|.
name|docID
argument_list|()
assert|;
return|return
operator|(
name|maxEndPositionCell
operator|.
name|endPosition
argument_list|()
operator|-
name|minPositionCell
argument_list|()
operator|.
name|startPosition
argument_list|()
operator|-
name|totalSpanLength
operator|)
operator|<=
name|allowedSlop
return|;
block|}
annotation|@
name|Override
DECL|method|twoPhaseCurrentDocMatches
name|boolean
name|twoPhaseCurrentDocMatches
parameter_list|()
throws|throws
name|IOException
block|{
comment|// at doc with all subSpans
name|subSpanCellsToPositionQueue
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|atMatch
argument_list|()
condition|)
block|{
name|atFirstInCurrentDoc
operator|=
literal|true
expr_stmt|;
name|oneExhaustedInCurrentDoc
operator|=
literal|false
expr_stmt|;
return|return
literal|true
return|;
block|}
assert|assert
name|minPositionCell
argument_list|()
operator|.
name|startPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
assert|;
if|if
condition|(
name|minPositionCell
argument_list|()
operator|.
name|nextStartPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
condition|)
block|{
name|spanPositionQueue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// exhausted a subSpan in current doc
return|return
literal|false
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|nextStartPosition
specifier|public
name|int
name|nextStartPosition
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|atFirstInCurrentDoc
condition|)
block|{
name|atFirstInCurrentDoc
operator|=
literal|false
expr_stmt|;
return|return
name|minPositionCell
argument_list|()
operator|.
name|startPosition
argument_list|()
return|;
block|}
while|while
condition|(
name|minPositionCell
argument_list|()
operator|.
name|startPosition
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
comment|// initially at current doc
name|minPositionCell
argument_list|()
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|spanPositionQueue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
assert|assert
name|minPositionCell
argument_list|()
operator|.
name|startPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
assert|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|minPositionCell
argument_list|()
operator|.
name|nextStartPosition
argument_list|()
operator|==
name|NO_MORE_POSITIONS
condition|)
block|{
name|oneExhaustedInCurrentDoc
operator|=
literal|true
expr_stmt|;
return|return
name|NO_MORE_POSITIONS
return|;
block|}
name|spanPositionQueue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
if|if
condition|(
name|atMatch
argument_list|()
condition|)
block|{
return|return
name|minPositionCell
argument_list|()
operator|.
name|startPosition
argument_list|()
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|startPosition
specifier|public
name|int
name|startPosition
parameter_list|()
block|{
assert|assert
name|minPositionCell
argument_list|()
operator|!=
literal|null
assert|;
return|return
name|atFirstInCurrentDoc
condition|?
operator|-
literal|1
else|:
name|oneExhaustedInCurrentDoc
condition|?
name|NO_MORE_POSITIONS
else|:
name|minPositionCell
argument_list|()
operator|.
name|startPosition
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|endPosition
specifier|public
name|int
name|endPosition
parameter_list|()
block|{
return|return
name|atFirstInCurrentDoc
condition|?
operator|-
literal|1
else|:
name|oneExhaustedInCurrentDoc
condition|?
name|NO_MORE_POSITIONS
else|:
name|maxEndPositionCell
operator|.
name|endPosition
argument_list|()
return|;
block|}
comment|/**    * WARNING: The List is not necessarily in order of the positions.    * @return Collection of<code>byte[]</code> payloads    * @throws IOException if there is a low-level I/O error    */
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
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SpansCell
name|cell
range|:
name|subSpanCells
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
for|for
control|(
name|SpansCell
name|cell
range|:
name|subSpanCells
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
return|return
literal|true
return|;
block|}
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
if|if
condition|(
name|minPositionCell
argument_list|()
operator|!=
literal|null
condition|)
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
name|docID
argument_list|()
operator|+
literal|":"
operator|+
name|startPosition
argument_list|()
operator|+
literal|"-"
operator|+
name|endPosition
argument_list|()
operator|)
return|;
block|}
else|else
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
literal|")@ ?START?"
return|;
block|}
block|}
block|}
end_class
end_unit
