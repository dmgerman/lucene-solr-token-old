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
name|LeafReaderContext
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
name|DisiPriorityQueue
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
name|DisiWrapper
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
name|search
operator|.
name|DisjunctionDISIApproximation
import|;
end_import
begin_comment
comment|/** Matches the union of its clauses.  */
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
comment|/** Construct a SpanOrQuery merging the provided clauses.    * All clauses must have the same field.    */
DECL|method|SpanOrQuery
specifier|public
name|SpanOrQuery
parameter_list|(
name|SpanQuery
modifier|...
name|clauses
parameter_list|)
block|{
name|this
operator|.
name|clauses
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|clauses
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|SpanQuery
name|seq
range|:
name|clauses
control|)
block|{
name|addClause
argument_list|(
name|seq
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
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|SpanOrQuery
name|that
init|=
operator|(
name|SpanOrQuery
operator|)
name|o
decl_stmt|;
return|return
name|clauses
operator|.
name|equals
argument_list|(
name|that
operator|.
name|clauses
argument_list|)
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
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|=
operator|(
name|h
operator|*
literal|7
operator|)
operator|^
name|clauses
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
specifier|final
name|LeafReaderContext
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
name|ArrayList
argument_list|<
name|Spans
argument_list|>
name|subSpans
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|clauses
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SpanQuery
name|sq
range|:
name|clauses
control|)
block|{
name|Spans
name|spans
init|=
name|sq
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
if|if
condition|(
name|spans
operator|!=
literal|null
condition|)
block|{
name|subSpans
operator|.
name|add
argument_list|(
name|spans
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|subSpans
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|subSpans
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|subSpans
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|DisiPriorityQueue
argument_list|<
name|Spans
argument_list|>
name|byDocQueue
init|=
operator|new
name|DisiPriorityQueue
argument_list|<>
argument_list|(
name|subSpans
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Spans
name|spans
range|:
name|subSpans
control|)
block|{
name|byDocQueue
operator|.
name|add
argument_list|(
operator|new
name|DisiWrapper
argument_list|<>
argument_list|(
name|spans
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SpanPositionQueue
name|byPositionQueue
init|=
operator|new
name|SpanPositionQueue
argument_list|(
name|subSpans
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|// when empty use -1
return|return
operator|new
name|Spans
argument_list|()
block|{
name|Spans
name|topPositionSpans
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|topPositionSpans
operator|=
literal|null
expr_stmt|;
name|DisiWrapper
argument_list|<
name|Spans
argument_list|>
name|topDocSpans
init|=
name|byDocQueue
operator|.
name|top
argument_list|()
decl_stmt|;
name|int
name|currentDoc
init|=
name|topDocSpans
operator|.
name|doc
decl_stmt|;
do|do
block|{
name|topDocSpans
operator|.
name|doc
operator|=
name|topDocSpans
operator|.
name|iterator
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|topDocSpans
operator|=
name|byDocQueue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|topDocSpans
operator|.
name|doc
operator|==
name|currentDoc
condition|)
do|;
return|return
name|topDocSpans
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
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
name|topPositionSpans
operator|=
literal|null
expr_stmt|;
name|DisiWrapper
argument_list|<
name|Spans
argument_list|>
name|topDocSpans
init|=
name|byDocQueue
operator|.
name|top
argument_list|()
decl_stmt|;
do|do
block|{
name|topDocSpans
operator|.
name|doc
operator|=
name|topDocSpans
operator|.
name|iterator
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|topDocSpans
operator|=
name|byDocQueue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|topDocSpans
operator|.
name|doc
operator|<
name|target
condition|)
do|;
return|return
name|topDocSpans
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
name|DisiWrapper
argument_list|<
name|Spans
argument_list|>
name|topDocSpans
init|=
name|byDocQueue
operator|.
name|top
argument_list|()
decl_stmt|;
return|return
name|topDocSpans
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
name|boolean
name|hasApproximation
init|=
literal|false
decl_stmt|;
for|for
control|(
name|DisiWrapper
argument_list|<
name|Spans
argument_list|>
name|w
range|:
name|byDocQueue
control|)
block|{
if|if
condition|(
name|w
operator|.
name|twoPhaseView
operator|!=
literal|null
condition|)
block|{
name|hasApproximation
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|hasApproximation
condition|)
block|{
comment|// none of the sub spans supports approximations
return|return
literal|null
return|;
block|}
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
operator|new
name|DisjunctionDISIApproximation
argument_list|<
name|Spans
argument_list|>
argument_list|(
name|byDocQueue
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|twoPhaseCurrentDocMatches
argument_list|()
return|;
block|}
block|}
return|;
block|}
name|int
name|lastDocTwoPhaseMatched
init|=
operator|-
literal|1
decl_stmt|;
name|boolean
name|twoPhaseCurrentDocMatches
parameter_list|()
throws|throws
name|IOException
block|{
name|DisiWrapper
argument_list|<
name|Spans
argument_list|>
name|listAtCurrentDoc
init|=
name|byDocQueue
operator|.
name|topList
argument_list|()
decl_stmt|;
comment|// remove the head of the list as long as it does not match
specifier|final
name|int
name|currentDoc
init|=
name|listAtCurrentDoc
operator|.
name|doc
decl_stmt|;
while|while
condition|(
name|listAtCurrentDoc
operator|.
name|twoPhaseView
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|listAtCurrentDoc
operator|.
name|twoPhaseView
operator|.
name|matches
argument_list|()
condition|)
block|{
comment|// use this spans for positions at current doc:
name|listAtCurrentDoc
operator|.
name|lastApproxMatchDoc
operator|=
name|currentDoc
expr_stmt|;
break|break;
block|}
comment|// do not use this spans for positions at current doc:
name|listAtCurrentDoc
operator|.
name|lastApproxNonMatchDoc
operator|=
name|currentDoc
expr_stmt|;
name|listAtCurrentDoc
operator|=
name|listAtCurrentDoc
operator|.
name|next
expr_stmt|;
if|if
condition|(
name|listAtCurrentDoc
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|lastDocTwoPhaseMatched
operator|=
name|currentDoc
expr_stmt|;
name|topPositionSpans
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
name|void
name|fillPositionQueue
parameter_list|()
throws|throws
name|IOException
block|{
comment|// called at first nextStartPosition
assert|assert
name|byPositionQueue
operator|.
name|size
argument_list|()
operator|==
literal|0
assert|;
comment|// add all matching Spans at current doc to byPositionQueue
name|DisiWrapper
argument_list|<
name|Spans
argument_list|>
name|listAtCurrentDoc
init|=
name|byDocQueue
operator|.
name|topList
argument_list|()
decl_stmt|;
while|while
condition|(
name|listAtCurrentDoc
operator|!=
literal|null
condition|)
block|{
name|Spans
name|spansAtDoc
init|=
name|listAtCurrentDoc
operator|.
name|iterator
decl_stmt|;
if|if
condition|(
name|lastDocTwoPhaseMatched
operator|==
name|listAtCurrentDoc
operator|.
name|doc
condition|)
block|{
comment|// matched by DisjunctionDisiApproximation
if|if
condition|(
name|listAtCurrentDoc
operator|.
name|twoPhaseView
operator|!=
literal|null
condition|)
block|{
comment|// matched by approximation
if|if
condition|(
name|listAtCurrentDoc
operator|.
name|lastApproxNonMatchDoc
operator|==
name|listAtCurrentDoc
operator|.
name|doc
condition|)
block|{
comment|// matches() returned false
name|spansAtDoc
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|listAtCurrentDoc
operator|.
name|lastApproxMatchDoc
operator|!=
name|listAtCurrentDoc
operator|.
name|doc
condition|)
block|{
if|if
condition|(
operator|!
name|listAtCurrentDoc
operator|.
name|twoPhaseView
operator|.
name|matches
argument_list|()
condition|)
block|{
name|spansAtDoc
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|spansAtDoc
operator|!=
literal|null
condition|)
block|{
assert|assert
name|spansAtDoc
operator|.
name|docID
argument_list|()
operator|==
name|listAtCurrentDoc
operator|.
name|doc
assert|;
assert|assert
name|spansAtDoc
operator|.
name|startPosition
argument_list|()
operator|==
operator|-
literal|1
assert|;
name|spansAtDoc
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
assert|assert
name|spansAtDoc
operator|.
name|startPosition
argument_list|()
operator|!=
name|NO_MORE_POSITIONS
assert|;
name|byPositionQueue
operator|.
name|add
argument_list|(
name|spansAtDoc
argument_list|)
expr_stmt|;
block|}
name|listAtCurrentDoc
operator|=
name|listAtCurrentDoc
operator|.
name|next
expr_stmt|;
block|}
assert|assert
name|byPositionQueue
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextStartPosition
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|topPositionSpans
operator|==
literal|null
condition|)
block|{
name|byPositionQueue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fillPositionQueue
argument_list|()
expr_stmt|;
comment|// fills byPositionQueue at first position
name|topPositionSpans
operator|=
name|byPositionQueue
operator|.
name|top
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|topPositionSpans
operator|.
name|nextStartPosition
argument_list|()
expr_stmt|;
name|topPositionSpans
operator|=
name|byPositionQueue
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
return|return
name|topPositionSpans
operator|.
name|startPosition
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|startPosition
parameter_list|()
block|{
return|return
name|topPositionSpans
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|topPositionSpans
operator|.
name|startPosition
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|endPosition
parameter_list|()
block|{
return|return
name|topPositionSpans
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|topPositionSpans
operator|.
name|endPosition
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
return|return
name|topPositionSpans
operator|==
literal|null
condition|?
literal|null
else|:
name|topPositionSpans
operator|.
name|isPayloadAvailable
argument_list|()
condition|?
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|topPositionSpans
operator|.
name|getPayload
argument_list|()
argument_list|)
else|:
literal|null
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
return|return
operator|(
name|topPositionSpans
operator|!=
literal|null
operator|)
operator|&&
name|topPositionSpans
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
literal|"spanOr("
operator|+
name|SpanOrQuery
operator|.
name|this
operator|+
literal|")@"
operator|+
name|docID
argument_list|()
operator|+
literal|": "
operator|+
name|startPosition
argument_list|()
operator|+
literal|" - "
operator|+
name|endPosition
argument_list|()
return|;
block|}
name|long
name|cost
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
if|if
condition|(
name|cost
operator|==
operator|-
literal|1
condition|)
block|{
name|cost
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|Spans
name|spans
range|:
name|subSpans
control|)
block|{
name|cost
operator|+=
name|spans
operator|.
name|cost
argument_list|()
expr_stmt|;
block|}
block|}
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
