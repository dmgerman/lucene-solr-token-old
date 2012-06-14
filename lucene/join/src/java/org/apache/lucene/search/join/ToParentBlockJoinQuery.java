begin_unit
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
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
name|Collection
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
name|IndexWriter
import|;
end_import
begin_comment
comment|// javadocs
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
name|ComplexExplanation
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
name|Explanation
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
name|Scorer
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
name|Weight
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
name|grouping
operator|.
name|TopGroups
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
name|ArrayUtil
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
name|FixedBitSet
import|;
end_import
begin_comment
comment|/**  * This query requires that you index  * children and parent docs as a single block, using the  * {@link IndexWriter#addDocuments IndexWriter.addDocuments()} or {@link  * IndexWriter#updateDocuments IndexWriter.updateDocuments()} API.  In each block, the  * child documents must appear first, ending with the parent  * document.  At search time you provide a Filter  * identifying the parents, however this Filter must provide  * an {@link FixedBitSet} per sub-reader.  *  *<p>Once the block index is built, use this query to wrap  * any sub-query matching only child docs and join matches in that  * child document space up to the parent document space.  * You can then use this Query as a clause with  * other queries in the parent document space.</p>  *  *<p>See {@link ToChildBlockJoinQuery} if you need to join  * in the reverse order.  *  *<p>The child documents must be orthogonal to the parent  * documents: the wrapped child query must never  * return a parent document.</p>  *  * If you'd like to retrieve {@link TopGroups} for the  * resulting query, use the {@link ToParentBlockJoinCollector}.  * Note that this is not necessary, ie, if you simply want  * to collect the parent documents and don't need to see  * which child documents matched under that parent, then  * you can use any collector.  *  *<p><b>NOTE</b>: If the overall query contains parent-only  * matches, for example you OR a parent-only query with a  * joined child-only query, then the resulting collected documents  * will be correct, however the {@link TopGroups} you get  * from {@link ToParentBlockJoinCollector} will not contain every  * child for parents that had matched.  *  *<p>See {@link org.apache.lucene.search.join} for an  * overview.</p>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|ToParentBlockJoinQuery
specifier|public
class|class
name|ToParentBlockJoinQuery
extends|extends
name|Query
block|{
DECL|field|parentsFilter
specifier|private
specifier|final
name|Filter
name|parentsFilter
decl_stmt|;
DECL|field|childQuery
specifier|private
specifier|final
name|Query
name|childQuery
decl_stmt|;
comment|// If we are rewritten, this is the original childQuery we
comment|// were passed; we use this for .equals() and
comment|// .hashCode().  This makes rewritten query equal the
comment|// original, so that user does not have to .rewrite() their
comment|// query before searching:
DECL|field|origChildQuery
specifier|private
specifier|final
name|Query
name|origChildQuery
decl_stmt|;
DECL|field|scoreMode
specifier|private
specifier|final
name|ScoreMode
name|scoreMode
decl_stmt|;
comment|/** Create a ToParentBlockJoinQuery.    *     * @param childQuery Query matching child documents.    * @param parentsFilter Filter (must produce FixedBitSet    * per-seegment) identifying the parent documents.    * @param scoreMode How to aggregate multiple child scores    * into a single parent score.    **/
DECL|method|ToParentBlockJoinQuery
specifier|public
name|ToParentBlockJoinQuery
parameter_list|(
name|Query
name|childQuery
parameter_list|,
name|Filter
name|parentsFilter
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|origChildQuery
operator|=
name|childQuery
expr_stmt|;
name|this
operator|.
name|childQuery
operator|=
name|childQuery
expr_stmt|;
name|this
operator|.
name|parentsFilter
operator|=
name|parentsFilter
expr_stmt|;
name|this
operator|.
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
block|}
DECL|method|ToParentBlockJoinQuery
specifier|private
name|ToParentBlockJoinQuery
parameter_list|(
name|Query
name|origChildQuery
parameter_list|,
name|Query
name|childQuery
parameter_list|,
name|Filter
name|parentsFilter
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|origChildQuery
operator|=
name|origChildQuery
expr_stmt|;
name|this
operator|.
name|childQuery
operator|=
name|childQuery
expr_stmt|;
name|this
operator|.
name|parentsFilter
operator|=
name|parentsFilter
expr_stmt|;
name|this
operator|.
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BlockJoinWeight
argument_list|(
name|this
argument_list|,
name|childQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
argument_list|,
name|parentsFilter
argument_list|,
name|scoreMode
argument_list|)
return|;
block|}
DECL|class|BlockJoinWeight
specifier|private
specifier|static
class|class
name|BlockJoinWeight
extends|extends
name|Weight
block|{
DECL|field|joinQuery
specifier|private
specifier|final
name|Query
name|joinQuery
decl_stmt|;
DECL|field|childWeight
specifier|private
specifier|final
name|Weight
name|childWeight
decl_stmt|;
DECL|field|parentsFilter
specifier|private
specifier|final
name|Filter
name|parentsFilter
decl_stmt|;
DECL|field|scoreMode
specifier|private
specifier|final
name|ScoreMode
name|scoreMode
decl_stmt|;
DECL|method|BlockJoinWeight
specifier|public
name|BlockJoinWeight
parameter_list|(
name|Query
name|joinQuery
parameter_list|,
name|Weight
name|childWeight
parameter_list|,
name|Filter
name|parentsFilter
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|joinQuery
operator|=
name|joinQuery
expr_stmt|;
name|this
operator|.
name|childWeight
operator|=
name|childWeight
expr_stmt|;
name|this
operator|.
name|parentsFilter
operator|=
name|parentsFilter
expr_stmt|;
name|this
operator|.
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|joinQuery
return|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|childWeight
operator|.
name|getValueForNormalization
argument_list|()
operator|*
name|joinQuery
operator|.
name|getBoost
argument_list|()
operator|*
name|joinQuery
operator|.
name|getBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|childWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
operator|*
name|joinQuery
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: acceptDocs applies (and is checked) only in the
comment|// parent document space
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Pass scoreDocsInOrder true, topScorer false to our sub:
specifier|final
name|Scorer
name|childScorer
init|=
name|childWeight
operator|.
name|scorer
argument_list|(
name|readerContext
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|childScorer
operator|==
literal|null
condition|)
block|{
comment|// No matches
return|return
literal|null
return|;
block|}
specifier|final
name|int
name|firstChildDoc
init|=
name|childScorer
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstChildDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
comment|// No matches
return|return
literal|null
return|;
block|}
comment|// NOTE: we cannot pass acceptDocs here because this
comment|// will (most likely, justifiably) cause the filter to
comment|// not return a FixedBitSet but rather a
comment|// BitsFilteredDocIdSet.  Instead, we filter by
comment|// acceptDocs when we score:
specifier|final
name|DocIdSet
name|parents
init|=
name|parentsFilter
operator|.
name|getDocIdSet
argument_list|(
name|readerContext
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|parents
operator|==
literal|null
operator|||
name|parents
operator|.
name|iterator
argument_list|()
operator|.
name|docID
argument_list|()
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
comment|//<-- means DocIdSet#EMPTY_DOCIDSET
comment|// No matches
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|parents
operator|instanceof
name|FixedBitSet
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"parentFilter must return FixedBitSet; got "
operator|+
name|parents
argument_list|)
throw|;
block|}
return|return
operator|new
name|BlockJoinScorer
argument_list|(
name|this
argument_list|,
name|childScorer
argument_list|,
operator|(
name|FixedBitSet
operator|)
name|parents
argument_list|,
name|firstChildDoc
argument_list|,
name|scoreMode
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockJoinScorer
name|scorer
init|=
operator|(
name|BlockJoinScorer
operator|)
name|scorer
argument_list|(
name|context
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|scorer
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
condition|)
block|{
return|return
name|scorer
operator|.
name|explain
argument_list|(
name|context
operator|.
name|docBase
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|ComplexExplanation
argument_list|(
literal|false
argument_list|,
literal|0.0f
argument_list|,
literal|"Not a match"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|scoresDocsOutOfOrder
specifier|public
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|BlockJoinScorer
specifier|static
class|class
name|BlockJoinScorer
extends|extends
name|Scorer
block|{
DECL|field|childScorer
specifier|private
specifier|final
name|Scorer
name|childScorer
decl_stmt|;
DECL|field|parentBits
specifier|private
specifier|final
name|FixedBitSet
name|parentBits
decl_stmt|;
DECL|field|scoreMode
specifier|private
specifier|final
name|ScoreMode
name|scoreMode
decl_stmt|;
DECL|field|acceptDocs
specifier|private
specifier|final
name|Bits
name|acceptDocs
decl_stmt|;
DECL|field|parentDoc
specifier|private
name|int
name|parentDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|prevParentDoc
specifier|private
name|int
name|prevParentDoc
decl_stmt|;
DECL|field|parentScore
specifier|private
name|float
name|parentScore
decl_stmt|;
DECL|field|nextChildDoc
specifier|private
name|int
name|nextChildDoc
decl_stmt|;
DECL|field|pendingChildDocs
specifier|private
name|int
index|[]
name|pendingChildDocs
init|=
operator|new
name|int
index|[
literal|5
index|]
decl_stmt|;
DECL|field|pendingChildScores
specifier|private
name|float
index|[]
name|pendingChildScores
decl_stmt|;
DECL|field|childDocUpto
specifier|private
name|int
name|childDocUpto
decl_stmt|;
DECL|method|BlockJoinScorer
specifier|public
name|BlockJoinScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Scorer
name|childScorer
parameter_list|,
name|FixedBitSet
name|parentBits
parameter_list|,
name|int
name|firstChildDoc
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
comment|//System.out.println("Q.init firstChildDoc=" + firstChildDoc);
name|this
operator|.
name|parentBits
operator|=
name|parentBits
expr_stmt|;
name|this
operator|.
name|childScorer
operator|=
name|childScorer
expr_stmt|;
name|this
operator|.
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
name|this
operator|.
name|acceptDocs
operator|=
name|acceptDocs
expr_stmt|;
if|if
condition|(
name|scoreMode
operator|!=
name|ScoreMode
operator|.
name|None
condition|)
block|{
name|pendingChildScores
operator|=
operator|new
name|float
index|[
literal|5
index|]
expr_stmt|;
block|}
name|nextChildDoc
operator|=
name|firstChildDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|childScorer
argument_list|,
literal|"BLOCK_JOIN"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getChildCount
name|int
name|getChildCount
parameter_list|()
block|{
return|return
name|childDocUpto
return|;
block|}
DECL|method|swapChildDocs
name|int
index|[]
name|swapChildDocs
parameter_list|(
name|int
index|[]
name|other
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|ret
init|=
name|pendingChildDocs
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
name|pendingChildDocs
operator|=
operator|new
name|int
index|[
literal|5
index|]
expr_stmt|;
block|}
else|else
block|{
name|pendingChildDocs
operator|=
name|other
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|swapChildScores
name|float
index|[]
name|swapChildScores
parameter_list|(
name|float
index|[]
name|other
parameter_list|)
block|{
if|if
condition|(
name|scoreMode
operator|==
name|ScoreMode
operator|.
name|None
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"ScoreMode is None; you must pass trackScores=false to ToParentBlockJoinCollector"
argument_list|)
throw|;
block|}
specifier|final
name|float
index|[]
name|ret
init|=
name|pendingChildScores
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
name|pendingChildScores
operator|=
operator|new
name|float
index|[
literal|5
index|]
expr_stmt|;
block|}
else|else
block|{
name|pendingChildScores
operator|=
name|other
expr_stmt|;
block|}
return|return
name|ret
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
comment|//System.out.println("Q.nextDoc() nextChildDoc=" + nextChildDoc);
comment|// Loop until we hit a parentDoc that's accepted
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|nextChildDoc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
comment|//System.out.println("  end");
return|return
name|parentDoc
operator|=
name|NO_MORE_DOCS
return|;
block|}
comment|// Gather all children sharing the same parent as
comment|// nextChildDoc
name|parentDoc
operator|=
name|parentBits
operator|.
name|nextSetBit
argument_list|(
name|nextChildDoc
argument_list|)
expr_stmt|;
comment|//System.out.println("  parentDoc=" + parentDoc);
assert|assert
name|parentDoc
operator|!=
operator|-
literal|1
assert|;
comment|//System.out.println("  nextChildDoc=" + nextChildDoc);
if|if
condition|(
name|acceptDocs
operator|!=
literal|null
operator|&&
operator|!
name|acceptDocs
operator|.
name|get
argument_list|(
name|parentDoc
argument_list|)
condition|)
block|{
comment|// Parent doc not accepted; skip child docs until
comment|// we hit a new parent doc:
do|do
block|{
name|nextChildDoc
operator|=
name|childScorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|nextChildDoc
operator|<
name|parentDoc
condition|)
do|;
continue|continue;
block|}
name|float
name|totalScore
init|=
literal|0
decl_stmt|;
name|float
name|maxScore
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|childDocUpto
operator|=
literal|0
expr_stmt|;
do|do
block|{
comment|//System.out.println("  c=" + nextChildDoc);
if|if
condition|(
name|pendingChildDocs
operator|.
name|length
operator|==
name|childDocUpto
condition|)
block|{
name|pendingChildDocs
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|pendingChildDocs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scoreMode
operator|!=
name|ScoreMode
operator|.
name|None
operator|&&
name|pendingChildScores
operator|.
name|length
operator|==
name|childDocUpto
condition|)
block|{
name|pendingChildScores
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|pendingChildScores
argument_list|)
expr_stmt|;
block|}
name|pendingChildDocs
index|[
name|childDocUpto
index|]
operator|=
name|nextChildDoc
expr_stmt|;
if|if
condition|(
name|scoreMode
operator|!=
name|ScoreMode
operator|.
name|None
condition|)
block|{
comment|// TODO: specialize this into dedicated classes per-scoreMode
specifier|final
name|float
name|childScore
init|=
name|childScorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|pendingChildScores
index|[
name|childDocUpto
index|]
operator|=
name|childScore
expr_stmt|;
name|maxScore
operator|=
name|Math
operator|.
name|max
argument_list|(
name|childScore
argument_list|,
name|maxScore
argument_list|)
expr_stmt|;
name|totalScore
operator|+=
name|childScore
expr_stmt|;
block|}
name|childDocUpto
operator|++
expr_stmt|;
name|nextChildDoc
operator|=
name|childScorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|nextChildDoc
operator|<
name|parentDoc
condition|)
do|;
comment|// Parent& child docs are supposed to be orthogonal:
assert|assert
name|nextChildDoc
operator|!=
name|parentDoc
assert|;
switch|switch
condition|(
name|scoreMode
condition|)
block|{
case|case
name|Avg
case|:
name|parentScore
operator|=
name|totalScore
operator|/
name|childDocUpto
expr_stmt|;
break|break;
case|case
name|Max
case|:
name|parentScore
operator|=
name|maxScore
expr_stmt|;
break|break;
case|case
name|Total
case|:
name|parentScore
operator|=
name|totalScore
expr_stmt|;
break|break;
case|case
name|None
case|:
break|break;
block|}
comment|//System.out.println("  return parentDoc=" + parentDoc);
return|return
name|parentDoc
return|;
block|}
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
name|parentDoc
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|parentScore
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
name|parentTarget
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println("Q.advance parentTarget=" + parentTarget);
if|if
condition|(
name|parentTarget
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|parentDoc
operator|=
name|NO_MORE_DOCS
return|;
block|}
if|if
condition|(
name|parentTarget
operator|==
literal|0
condition|)
block|{
comment|// Callers should only be passing in a docID from
comment|// the parent space, so this means this parent
comment|// has no children (it got docID 0), so it cannot
comment|// possibly match.  We must handle this case
comment|// separately otherwise we pass invalid -1 to
comment|// prevSetBit below:
return|return
name|nextDoc
argument_list|()
return|;
block|}
name|prevParentDoc
operator|=
name|parentBits
operator|.
name|prevSetBit
argument_list|(
name|parentTarget
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|//System.out.println("  rolled back to prevParentDoc=" + prevParentDoc + " vs parentDoc=" + parentDoc);
assert|assert
name|prevParentDoc
operator|>=
name|parentDoc
assert|;
if|if
condition|(
name|prevParentDoc
operator|>
name|nextChildDoc
condition|)
block|{
name|nextChildDoc
operator|=
name|childScorer
operator|.
name|advance
argument_list|(
name|prevParentDoc
argument_list|)
expr_stmt|;
comment|// System.out.println("  childScorer advanced to child docID=" + nextChildDoc);
comment|//} else {
comment|//System.out.println("  skip childScorer advance");
block|}
comment|// Parent& child docs are supposed to be orthogonal:
assert|assert
name|nextChildDoc
operator|!=
name|prevParentDoc
assert|;
specifier|final
name|int
name|nd
init|=
name|nextDoc
argument_list|()
decl_stmt|;
comment|//System.out.println("  return nextParentDoc=" + nd);
return|return
name|nd
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|start
init|=
name|docBase
operator|+
name|prevParentDoc
operator|+
literal|1
decl_stmt|;
comment|// +1 b/c prevParentDoc is previous parent doc
name|int
name|end
init|=
name|docBase
operator|+
name|parentDoc
operator|-
literal|1
decl_stmt|;
comment|// -1 b/c parentDoc is parent doc
return|return
operator|new
name|ComplexExplanation
argument_list|(
literal|true
argument_list|,
name|score
argument_list|()
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"Score based on child doc range from %d to %d"
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
argument_list|)
return|;
block|}
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
name|childQuery
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
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
specifier|final
name|Query
name|childRewrite
init|=
name|childQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|childRewrite
operator|!=
name|childQuery
condition|)
block|{
name|Query
name|rewritten
init|=
operator|new
name|ToParentBlockJoinQuery
argument_list|(
name|childQuery
argument_list|,
name|childRewrite
argument_list|,
name|parentsFilter
argument_list|,
name|scoreMode
argument_list|)
decl_stmt|;
name|rewritten
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rewritten
return|;
block|}
else|else
block|{
return|return
name|this
return|;
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
return|return
literal|"ToParentBlockJoinQuery ("
operator|+
name|childQuery
operator|.
name|toString
argument_list|()
operator|+
literal|")"
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
name|_other
parameter_list|)
block|{
if|if
condition|(
name|_other
operator|instanceof
name|ToParentBlockJoinQuery
condition|)
block|{
specifier|final
name|ToParentBlockJoinQuery
name|other
init|=
operator|(
name|ToParentBlockJoinQuery
operator|)
name|_other
decl_stmt|;
return|return
name|origChildQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|origChildQuery
argument_list|)
operator|&&
name|parentsFilter
operator|.
name|equals
argument_list|(
name|other
operator|.
name|parentsFilter
argument_list|)
operator|&&
name|scoreMode
operator|==
name|other
operator|.
name|scoreMode
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|hash
init|=
literal|1
decl_stmt|;
name|hash
operator|=
name|prime
operator|*
name|hash
operator|+
name|origChildQuery
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|prime
operator|*
name|hash
operator|+
name|scoreMode
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|prime
operator|*
name|hash
operator|+
name|parentsFilter
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|hash
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|ToParentBlockJoinQuery
name|clone
parameter_list|()
block|{
return|return
operator|new
name|ToParentBlockJoinQuery
argument_list|(
name|origChildQuery
operator|.
name|clone
argument_list|()
argument_list|,
name|parentsFilter
argument_list|,
name|scoreMode
argument_list|)
return|;
block|}
block|}
end_class
end_unit
