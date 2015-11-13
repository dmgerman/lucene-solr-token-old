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
name|DisjunctionDISIApproximation
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
name|IndexSearcher
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
name|TwoPhaseIterator
import|;
end_import
begin_comment
comment|/** Matches the union of its clauses.  */
end_comment
begin_class
DECL|class|SpanOrQuery
specifier|public
specifier|final
class|class
name|SpanOrQuery
extends|extends
name|SpanQuery
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
specifier|private
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
name|rewritten
init|=
operator|new
name|SpanOrQuery
argument_list|()
decl_stmt|;
name|boolean
name|actuallyRewritten
init|=
literal|false
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
name|actuallyRewritten
operator||=
name|query
operator|!=
name|c
expr_stmt|;
name|rewritten
operator|.
name|addClause
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|actuallyRewritten
condition|)
block|{
return|return
name|rewritten
return|;
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
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
DECL|method|createWeight
specifier|public
name|SpanWeight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|SpanWeight
argument_list|>
name|subWeights
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
name|q
range|:
name|clauses
control|)
block|{
name|subWeights
operator|.
name|add
argument_list|(
name|q
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SpanOrWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
condition|?
name|getTermContexts
argument_list|(
name|subWeights
argument_list|)
else|:
literal|null
argument_list|,
name|subWeights
argument_list|)
return|;
block|}
DECL|class|SpanOrWeight
specifier|public
class|class
name|SpanOrWeight
extends|extends
name|SpanWeight
block|{
DECL|field|subWeights
specifier|final
name|List
argument_list|<
name|SpanWeight
argument_list|>
name|subWeights
decl_stmt|;
DECL|method|SpanOrWeight
specifier|public
name|SpanOrWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|terms
parameter_list|,
name|List
argument_list|<
name|SpanWeight
argument_list|>
name|subWeights
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|SpanOrQuery
operator|.
name|this
argument_list|,
name|searcher
argument_list|,
name|terms
argument_list|)
expr_stmt|;
name|this
operator|.
name|subWeights
operator|=
name|subWeights
expr_stmt|;
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
name|SpanWeight
name|w
range|:
name|subWeights
control|)
block|{
name|w
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
DECL|method|extractTermContexts
specifier|public
name|void
name|extractTermContexts
parameter_list|(
name|Map
argument_list|<
name|Term
argument_list|,
name|TermContext
argument_list|>
name|contexts
parameter_list|)
block|{
for|for
control|(
name|SpanWeight
name|w
range|:
name|subWeights
control|)
block|{
name|w
operator|.
name|extractTermContexts
argument_list|(
name|contexts
argument_list|)
expr_stmt|;
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
name|LeafReaderContext
name|context
parameter_list|,
name|Postings
name|requiredPostings
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
name|SpanWeight
name|w
range|:
name|subWeights
control|)
block|{
name|Spans
name|spans
init|=
name|w
operator|.
name|getSpans
argument_list|(
name|context
argument_list|,
name|requiredPostings
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
operator|new
name|ScoringWrapperSpans
argument_list|(
name|subSpans
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|getSimScorer
argument_list|(
name|context
argument_list|)
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
argument_list|(
name|this
argument_list|,
name|getSimScorer
argument_list|(
name|context
argument_list|)
argument_list|)
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
name|float
name|sumMatchCost
init|=
literal|0
decl_stmt|;
comment|// See also DisjunctionScorer.asTwoPhaseIterator()
name|long
name|sumApproxCost
init|=
literal|0
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
name|long
name|costWeight
init|=
operator|(
name|w
operator|.
name|cost
operator|<=
literal|1
operator|)
condition|?
literal|1
else|:
name|w
operator|.
name|cost
decl_stmt|;
name|sumMatchCost
operator|+=
name|w
operator|.
name|twoPhaseView
operator|.
name|matchCost
argument_list|()
operator|*
name|costWeight
expr_stmt|;
name|sumApproxCost
operator|+=
name|costWeight
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sumApproxCost
operator|==
literal|0
condition|)
block|{
comment|// no sub spans supports approximations
name|computePositionsCost
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|final
name|float
name|matchCost
init|=
name|sumMatchCost
operator|/
name|sumApproxCost
decl_stmt|;
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
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
name|matchCost
return|;
block|}
block|}
return|;
block|}
name|float
name|positionsCost
init|=
operator|-
literal|1
decl_stmt|;
name|void
name|computePositionsCost
parameter_list|()
block|{
name|float
name|sumPositionsCost
init|=
literal|0
decl_stmt|;
name|long
name|sumCost
init|=
literal|0
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
name|long
name|costWeight
init|=
operator|(
name|w
operator|.
name|cost
operator|<=
literal|1
operator|)
condition|?
literal|1
else|:
name|w
operator|.
name|cost
decl_stmt|;
name|sumPositionsCost
operator|+=
name|w
operator|.
name|iterator
operator|.
name|positionsCost
argument_list|()
operator|*
name|costWeight
expr_stmt|;
name|sumCost
operator|+=
name|costWeight
expr_stmt|;
block|}
name|positionsCost
operator|=
name|sumPositionsCost
operator|/
name|sumCost
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|positionsCost
parameter_list|()
block|{
comment|// This may be called when asTwoPhaseIterator returned null,
comment|// which happens when none of the sub spans supports approximations.
assert|assert
name|positionsCost
operator|>
literal|0
assert|;
return|return
name|positionsCost
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
name|int
name|width
parameter_list|()
block|{
return|return
name|topPositionSpans
operator|.
name|width
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|topPositionSpans
operator|!=
literal|null
condition|)
name|topPositionSpans
operator|.
name|collect
argument_list|(
name|collector
argument_list|)
expr_stmt|;
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
block|}
end_class
end_unit
