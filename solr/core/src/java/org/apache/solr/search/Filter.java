begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
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
name|ConstantScoreScorer
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
name|util
operator|.
name|Bits
import|;
end_import
begin_comment
comment|/**  *  Convenient base class for building queries that only perform matching, but  *  no scoring. The scorer produced by such queries always returns 0 as score.  */
end_comment
begin_class
DECL|class|Filter
specifier|public
specifier|abstract
class|class
name|Filter
extends|extends
name|Query
block|{
DECL|field|applyLazily
specifier|private
specifier|final
name|boolean
name|applyLazily
decl_stmt|;
comment|/** Filter constructor. When {@code applyLazily} is true and the produced    *  {@link DocIdSet}s support {@link DocIdSet#bits() random-access}, Lucene    *  will only apply this filter after other clauses. */
DECL|method|Filter
specifier|protected
name|Filter
parameter_list|(
name|boolean
name|applyLazily
parameter_list|)
block|{
name|this
operator|.
name|applyLazily
operator|=
name|applyLazily
expr_stmt|;
block|}
comment|/** Default Filter constructor that will use the    *  {@link DocIdSet#iterator() doc id set iterator} when consumed through    *  the {@link Query} API. */
DECL|method|Filter
specifier|protected
name|Filter
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a {@link DocIdSet} enumerating the documents that should be    * permitted in search results.<b>NOTE:</b> null can be    * returned if no documents are accepted by this Filter.    *<p>    * Note: This method will be called once per segment in    * the index during searching.  The returned {@link DocIdSet}    * must refer to document IDs for that segment, not for    * the top-level reader.    *    * @param context a {@link org.apache.lucene.index.LeafReaderContext} instance opened on the index currently    *         searched on. Note, it is likely that the provided reader info does not    *         represent the whole underlying index i.e. if the index has more than    *         one segment the given reader only represents a single segment.    *         The provided context is always an atomic context, so you can call    *         {@link org.apache.lucene.index.LeafReader#fields()}    *         on the context's reader, for example.    *    * @param acceptDocs    *          Bits that represent the allowable docs to match (typically deleted docs    *          but possibly filtering other documents)    *    * @return a DocIdSet that provides the documents which should be permitted or    *         prohibited in search results.<b>NOTE:</b><code>null</code> should be returned if    *         the filter doesn't accept any documents otherwise internal optimization might not apply    *         in the case an<i>empty</i> {@link DocIdSet} is returned.    */
DECL|method|getDocIdSet
specifier|public
specifier|abstract
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|//
comment|// Query compatibility
comment|//
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
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
return|return
operator|new
name|Weight
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
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
block|{}
annotation|@
name|Override
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0f
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|boost
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|scorer
init|=
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|match
init|=
operator|(
name|scorer
operator|!=
literal|null
operator|&&
name|scorer
operator|.
name|iterator
argument_list|()
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
operator|)
decl_stmt|;
if|if
condition|(
name|match
condition|)
block|{
assert|assert
name|scorer
operator|.
name|score
argument_list|()
operator|==
literal|0f
assert|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
literal|0f
argument_list|,
literal|"Match on id "
operator|+
name|doc
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
literal|0f
argument_list|,
literal|"No match on id "
operator|+
name|doc
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocIdSet
name|set
init|=
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|applyLazily
operator|&&
name|set
operator|.
name|bits
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Bits
name|bits
init|=
name|set
operator|.
name|bits
argument_list|()
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|approximation
init|=
name|DocIdSetIterator
operator|.
name|all
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|TwoPhaseIterator
name|twoPhase
init|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|approximation
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
name|bits
operator|.
name|get
argument_list|(
name|approximation
operator|.
name|docID
argument_list|()
argument_list|)
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
literal|10
return|;
comment|// TODO use cost of bits.get()
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
literal|0f
argument_list|,
name|twoPhase
argument_list|)
return|;
block|}
specifier|final
name|DocIdSetIterator
name|iterator
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iterator
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
literal|0f
argument_list|,
name|iterator
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
