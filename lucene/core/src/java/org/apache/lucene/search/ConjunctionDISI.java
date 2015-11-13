begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CollectionUtil
import|;
end_import
begin_comment
comment|/** A conjunction of DocIdSetIterators.  * This iterates over the doc ids that are present in each given DocIdSetIterator.  *<br>Public only for use in {@link org.apache.lucene.search.spans}.  * @lucene.internal  */
end_comment
begin_class
DECL|class|ConjunctionDISI
specifier|public
class|class
name|ConjunctionDISI
extends|extends
name|DocIdSetIterator
block|{
comment|/** Create a conjunction over the provided iterators, taking advantage of    *  {@link TwoPhaseIterator}. */
DECL|method|intersect
specifier|public
specifier|static
name|ConjunctionDISI
name|intersect
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|DocIdSetIterator
argument_list|>
name|iterators
parameter_list|)
block|{
if|if
condition|(
name|iterators
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot make a ConjunctionDISI of less than 2 iterators"
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|DocIdSetIterator
argument_list|>
name|allIterators
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|TwoPhaseIterator
argument_list|>
name|twoPhaseIterators
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DocIdSetIterator
name|iter
range|:
name|iterators
control|)
block|{
name|addIterator
argument_list|(
name|iter
argument_list|,
name|allIterators
argument_list|,
name|twoPhaseIterators
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|twoPhaseIterators
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|ConjunctionDISI
argument_list|(
name|allIterators
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TwoPhase
argument_list|(
name|allIterators
argument_list|,
name|twoPhaseIterators
argument_list|)
return|;
block|}
block|}
comment|/** Adds the iterator, possibly splitting up into two phases or collapsing if it is another conjunction */
DECL|method|addIterator
specifier|private
specifier|static
name|void
name|addIterator
parameter_list|(
name|DocIdSetIterator
name|disi
parameter_list|,
name|List
argument_list|<
name|DocIdSetIterator
argument_list|>
name|allIterators
parameter_list|,
name|List
argument_list|<
name|TwoPhaseIterator
argument_list|>
name|twoPhaseIterators
parameter_list|)
block|{
comment|// Check for exactly this class for collapsing. Subclasses can do their own optimizations.
if|if
condition|(
name|disi
operator|.
name|getClass
argument_list|()
operator|==
name|ConjunctionScorer
operator|.
name|class
condition|)
block|{
name|addIterator
argument_list|(
operator|(
operator|(
name|ConjunctionScorer
operator|)
name|disi
operator|)
operator|.
name|disi
argument_list|,
name|allIterators
argument_list|,
name|twoPhaseIterators
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|disi
operator|.
name|getClass
argument_list|()
operator|==
name|ConjunctionDISI
operator|.
name|class
operator|||
name|disi
operator|.
name|getClass
argument_list|()
operator|==
name|TwoPhase
operator|.
name|class
condition|)
block|{
name|ConjunctionDISI
name|conjunction
init|=
operator|(
name|ConjunctionDISI
operator|)
name|disi
decl_stmt|;
comment|// subconjuctions have already split themselves into two phase iterators and others, so we can take those
comment|// iterators as they are and move them up to this conjunction
name|allIterators
operator|.
name|add
argument_list|(
name|conjunction
operator|.
name|lead
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|allIterators
argument_list|,
name|conjunction
operator|.
name|others
argument_list|)
expr_stmt|;
if|if
condition|(
name|conjunction
operator|.
name|getClass
argument_list|()
operator|==
name|TwoPhase
operator|.
name|class
condition|)
block|{
name|TwoPhase
name|twoPhase
init|=
operator|(
name|TwoPhase
operator|)
name|conjunction
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|twoPhaseIterators
argument_list|,
name|twoPhase
operator|.
name|twoPhaseView
operator|.
name|twoPhaseIterators
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|TwoPhaseIterator
name|twoPhaseIter
init|=
name|TwoPhaseIterator
operator|.
name|asTwoPhaseIterator
argument_list|(
name|disi
argument_list|)
decl_stmt|;
if|if
condition|(
name|twoPhaseIter
operator|!=
literal|null
condition|)
block|{
name|allIterators
operator|.
name|add
argument_list|(
name|twoPhaseIter
operator|.
name|approximation
argument_list|()
argument_list|)
expr_stmt|;
name|twoPhaseIterators
operator|.
name|add
argument_list|(
name|twoPhaseIter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// no approximation support, use the iterator as-is
name|allIterators
operator|.
name|add
argument_list|(
name|disi
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|lead
specifier|final
name|DocIdSetIterator
name|lead
decl_stmt|;
DECL|field|others
specifier|final
name|DocIdSetIterator
index|[]
name|others
decl_stmt|;
DECL|method|ConjunctionDISI
name|ConjunctionDISI
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|DocIdSetIterator
argument_list|>
name|iterators
parameter_list|)
block|{
assert|assert
name|iterators
operator|.
name|size
argument_list|()
operator|>=
literal|2
assert|;
comment|// Sort the array the first time to allow the least frequent DocsEnum to
comment|// lead the matching.
name|CollectionUtil
operator|.
name|timSort
argument_list|(
name|iterators
argument_list|,
operator|new
name|Comparator
argument_list|<
name|DocIdSetIterator
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|DocIdSetIterator
name|o1
parameter_list|,
name|DocIdSetIterator
name|o2
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|cost
argument_list|()
argument_list|,
name|o2
operator|.
name|cost
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|lead
operator|=
name|iterators
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|others
operator|=
name|iterators
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|iterators
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|DocIdSetIterator
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|matches
specifier|protected
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|true
return|;
block|}
DECL|method|asTwoPhaseIterator
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|doNext
specifier|private
name|int
name|doNext
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|doc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
comment|// we need this check because it is only ok to call #matches when positioned
return|return
name|NO_MORE_DOCS
return|;
block|}
name|advanceHead
label|:
for|for
control|(
init|;
condition|;
control|)
block|{
for|for
control|(
name|DocIdSetIterator
name|other
range|:
name|others
control|)
block|{
comment|// invariant: docsAndFreqs[i].doc<= doc at this point.
comment|// docsAndFreqs[i].doc may already be equal to doc if we "broke advanceHead"
comment|// on the previous iteration and the advance on the lead scorer exactly matched.
if|if
condition|(
name|other
operator|.
name|docID
argument_list|()
operator|<
name|doc
condition|)
block|{
specifier|final
name|int
name|next
init|=
name|other
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|>
name|doc
condition|)
block|{
comment|// DocsEnum beyond the current doc - break and advance lead to the new highest doc.
name|doc
operator|=
name|lead
operator|.
name|advance
argument_list|(
name|next
argument_list|)
expr_stmt|;
break|break
name|advanceHead
break|;
block|}
block|}
block|}
if|if
condition|(
name|matches
argument_list|()
condition|)
block|{
comment|// success - all DocsEnums are on the same doc
return|return
name|doc
return|;
block|}
else|else
block|{
name|doc
operator|=
name|lead
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
break|break
name|advanceHead
break|;
block|}
block|}
block|}
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
name|doNext
argument_list|(
name|lead
operator|.
name|advance
argument_list|(
name|target
argument_list|)
argument_list|)
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
name|lead
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
name|doNext
argument_list|(
name|lead
operator|.
name|nextDoc
argument_list|()
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
name|lead
operator|.
name|cost
argument_list|()
return|;
comment|// overestimate
block|}
comment|/**    * {@link TwoPhaseIterator} view of a {@link TwoPhase} conjunction.    */
DECL|class|TwoPhaseConjunctionDISI
specifier|private
specifier|static
class|class
name|TwoPhaseConjunctionDISI
extends|extends
name|TwoPhaseIterator
block|{
DECL|field|twoPhaseIterators
specifier|private
specifier|final
name|TwoPhaseIterator
index|[]
name|twoPhaseIterators
decl_stmt|;
DECL|field|matchCost
specifier|private
specifier|final
name|float
name|matchCost
decl_stmt|;
DECL|method|TwoPhaseConjunctionDISI
specifier|private
name|TwoPhaseConjunctionDISI
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|DocIdSetIterator
argument_list|>
name|iterators
parameter_list|,
name|List
argument_list|<
name|TwoPhaseIterator
argument_list|>
name|twoPhaseIterators
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|ConjunctionDISI
argument_list|(
name|iterators
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|twoPhaseIterators
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
name|CollectionUtil
operator|.
name|timSort
argument_list|(
name|twoPhaseIterators
argument_list|,
operator|new
name|Comparator
argument_list|<
name|TwoPhaseIterator
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|TwoPhaseIterator
name|o1
parameter_list|,
name|TwoPhaseIterator
name|o2
parameter_list|)
block|{
return|return
name|Float
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|matchCost
argument_list|()
argument_list|,
name|o2
operator|.
name|matchCost
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|twoPhaseIterators
operator|=
name|twoPhaseIterators
operator|.
name|toArray
argument_list|(
operator|new
name|TwoPhaseIterator
index|[
name|twoPhaseIterators
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
comment|// Compute the matchCost as the total matchCost of the sub iterators.
comment|// TODO: This could be too high because the matching is done cheapest first: give the lower matchCosts a higher weight.
name|float
name|totalMatchCost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TwoPhaseIterator
name|tpi
range|:
name|twoPhaseIterators
control|)
block|{
name|totalMatchCost
operator|+=
name|tpi
operator|.
name|matchCost
argument_list|()
expr_stmt|;
block|}
name|matchCost
operator|=
name|totalMatchCost
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matches
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|TwoPhaseIterator
name|twoPhaseIterator
range|:
name|twoPhaseIterators
control|)
block|{
comment|// match cheapest first
if|if
condition|(
name|twoPhaseIterator
operator|.
name|matches
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|matchCost
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
comment|/**    * A conjunction DISI built on top of approximations. This implementation    * verifies that documents actually match by consulting the provided    * {@link TwoPhaseIterator}s.    *    * Another important difference with {@link ConjunctionDISI} is that this    * implementation supports approximations too: the approximation of this    * impl is the conjunction of the approximations of the wrapped iterators.    * This allows eg. {@code +"A B" +C} to be approximated as    * {@code +(+A +B) +C}.    */
comment|// NOTE: this is essentially the same as TwoPhaseDocIdSetIterator.asDocIdSetIterator
comment|// but is its own impl in order to be able to expose a two-phase view
DECL|class|TwoPhase
specifier|private
specifier|static
class|class
name|TwoPhase
extends|extends
name|ConjunctionDISI
block|{
DECL|field|twoPhaseView
specifier|final
name|TwoPhaseConjunctionDISI
name|twoPhaseView
decl_stmt|;
DECL|method|TwoPhase
specifier|private
name|TwoPhase
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|DocIdSetIterator
argument_list|>
name|iterators
parameter_list|,
name|List
argument_list|<
name|TwoPhaseIterator
argument_list|>
name|twoPhaseIterators
parameter_list|)
block|{
name|super
argument_list|(
name|iterators
argument_list|)
expr_stmt|;
name|twoPhaseView
operator|=
operator|new
name|TwoPhaseConjunctionDISI
argument_list|(
name|iterators
argument_list|,
name|twoPhaseIterators
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseConjunctionDISI
name|asTwoPhaseIterator
parameter_list|()
block|{
return|return
name|twoPhaseView
return|;
block|}
annotation|@
name|Override
DECL|method|matches
specifier|protected
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|twoPhaseView
operator|.
name|matches
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
