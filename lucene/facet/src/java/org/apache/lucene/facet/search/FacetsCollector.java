begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|ArrayList
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|CountFacetRequest
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|FacetRequest
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|facet
operator|.
name|search
operator|.
name|results
operator|.
name|FacetResult
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|search
operator|.
name|Collector
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
name|FixedBitSet
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link Collector} which executes faceted search and computes the weight of  * requested facets. To get the facet results you should call  * {@link #getFacetResults()}.  * {@link #create(FacetSearchParams, IndexReader, TaxonomyReader)} returns the  * most optimized {@link FacetsCollector} for the given parameters.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FacetsCollector
specifier|public
specifier|abstract
class|class
name|FacetsCollector
extends|extends
name|Collector
block|{
DECL|class|DocsAndScoresCollector
specifier|private
specifier|static
specifier|final
class|class
name|DocsAndScoresCollector
extends|extends
name|FacetsCollector
block|{
DECL|field|context
specifier|private
name|AtomicReaderContext
name|context
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|field|bits
specifier|private
name|FixedBitSet
name|bits
decl_stmt|;
DECL|field|totalHits
specifier|private
name|int
name|totalHits
decl_stmt|;
DECL|field|scores
specifier|private
name|float
index|[]
name|scores
decl_stmt|;
DECL|method|DocsAndScoresCollector
specifier|public
name|DocsAndScoresCollector
parameter_list|(
name|FacetsAccumulator
name|accumulator
parameter_list|)
block|{
name|super
argument_list|(
name|accumulator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|protected
specifier|final
name|void
name|finish
parameter_list|()
block|{
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
name|matchingDocs
operator|.
name|add
argument_list|(
operator|new
name|MatchingDocs
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|bits
argument_list|,
name|totalHits
argument_list|,
name|scores
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|=
literal|null
expr_stmt|;
name|scores
operator|=
literal|null
expr_stmt|;
name|context
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
specifier|final
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|totalHits
operator|>=
name|scores
operator|.
name|length
condition|)
block|{
name|float
index|[]
name|newScores
init|=
operator|new
name|float
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|totalHits
operator|+
literal|1
argument_list|,
literal|4
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scores
argument_list|,
literal|0
argument_list|,
name|newScores
argument_list|,
literal|0
argument_list|,
name|totalHits
argument_list|)
expr_stmt|;
name|scores
operator|=
name|newScores
expr_stmt|;
block|}
name|scores
index|[
name|totalHits
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
name|totalHits
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
specifier|final
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
specifier|final
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
name|matchingDocs
operator|.
name|add
argument_list|(
operator|new
name|MatchingDocs
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|bits
argument_list|,
name|totalHits
argument_list|,
name|scores
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|totalHits
operator|=
literal|0
expr_stmt|;
name|scores
operator|=
operator|new
name|float
index|[
literal|64
index|]
expr_stmt|;
comment|// some initial size
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
block|}
DECL|class|DocsOnlyCollector
specifier|private
specifier|final
specifier|static
class|class
name|DocsOnlyCollector
extends|extends
name|FacetsCollector
block|{
DECL|field|context
specifier|private
name|AtomicReaderContext
name|context
decl_stmt|;
DECL|field|bits
specifier|private
name|FixedBitSet
name|bits
decl_stmt|;
DECL|field|totalHits
specifier|private
name|int
name|totalHits
decl_stmt|;
DECL|method|DocsOnlyCollector
specifier|public
name|DocsOnlyCollector
parameter_list|(
name|FacetsAccumulator
name|accumulator
parameter_list|)
block|{
name|super
argument_list|(
name|accumulator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|protected
specifier|final
name|void
name|finish
parameter_list|()
block|{
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
name|matchingDocs
operator|.
name|add
argument_list|(
operator|new
name|MatchingDocs
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|bits
argument_list|,
name|totalHits
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|=
literal|null
expr_stmt|;
name|context
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
specifier|final
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|totalHits
operator|++
expr_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
specifier|final
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
specifier|final
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
name|matchingDocs
operator|.
name|add
argument_list|(
operator|new
name|MatchingDocs
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|bits
argument_list|,
name|totalHits
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|totalHits
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
block|}
comment|/**    * Holds the documents that were matched in the {@link AtomicReaderContext}.    * If scores were required, then {@code scores} is not null.    */
DECL|class|MatchingDocs
specifier|public
specifier|final
specifier|static
class|class
name|MatchingDocs
block|{
DECL|field|context
specifier|public
specifier|final
name|AtomicReaderContext
name|context
decl_stmt|;
DECL|field|bits
specifier|public
specifier|final
name|FixedBitSet
name|bits
decl_stmt|;
DECL|field|scores
specifier|public
specifier|final
name|float
index|[]
name|scores
decl_stmt|;
DECL|field|totalHits
specifier|public
specifier|final
name|int
name|totalHits
decl_stmt|;
DECL|method|MatchingDocs
specifier|public
name|MatchingDocs
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|FixedBitSet
name|bits
parameter_list|,
name|int
name|totalHits
parameter_list|,
name|float
index|[]
name|scores
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|this
operator|.
name|scores
operator|=
name|scores
expr_stmt|;
name|this
operator|.
name|totalHits
operator|=
name|totalHits
expr_stmt|;
block|}
block|}
comment|/**    * Creates a {@link FacetsCollector} with the default    * {@link FacetsAccumulator}.    */
DECL|method|create
specifier|public
specifier|static
name|FacetsCollector
name|create
parameter_list|(
name|FacetSearchParams
name|fsp
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|)
block|{
if|if
condition|(
name|fsp
operator|.
name|indexingParams
operator|.
name|getPartitionSize
argument_list|()
operator|!=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|create
argument_list|(
operator|new
name|StandardFacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
argument_list|)
return|;
block|}
for|for
control|(
name|FacetRequest
name|fr
range|:
name|fsp
operator|.
name|facetRequests
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|fr
operator|instanceof
name|CountFacetRequest
operator|)
condition|)
block|{
return|return
name|create
argument_list|(
operator|new
name|StandardFacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
name|create
argument_list|(
operator|new
name|FacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates a {@link FacetsCollector} that satisfies the requirements of the    * given {@link FacetsAccumulator}.    */
DECL|method|create
specifier|public
specifier|static
name|FacetsCollector
name|create
parameter_list|(
name|FacetsAccumulator
name|accumulator
parameter_list|)
block|{
if|if
condition|(
name|accumulator
operator|.
name|getAggregator
argument_list|()
operator|.
name|requiresDocScores
argument_list|()
condition|)
block|{
return|return
operator|new
name|DocsAndScoresCollector
argument_list|(
name|accumulator
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|DocsOnlyCollector
argument_list|(
name|accumulator
argument_list|)
return|;
block|}
block|}
DECL|field|accumulator
specifier|private
specifier|final
name|FacetsAccumulator
name|accumulator
decl_stmt|;
DECL|field|matchingDocs
specifier|protected
specifier|final
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
init|=
operator|new
name|ArrayList
argument_list|<
name|MatchingDocs
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|FacetsCollector
specifier|protected
name|FacetsCollector
parameter_list|(
name|FacetsAccumulator
name|accumulator
parameter_list|)
block|{
name|this
operator|.
name|accumulator
operator|=
name|accumulator
expr_stmt|;
block|}
comment|/**    * Called when the Collector has finished, so that the last    * {@link MatchingDocs} can be added.    */
DECL|method|finish
specifier|protected
specifier|abstract
name|void
name|finish
parameter_list|()
function_decl|;
comment|/**    * Returns a {@link FacetResult} per {@link FacetRequest} set in    * {@link FacetSearchParams}. Note that if one of the {@link FacetRequest    * requests} is for a {@link CategoryPath} that does not exist in the taxonomy,    * no matching {@link FacetResult} will be returned.    */
DECL|method|getFacetResults
specifier|public
specifier|final
name|List
argument_list|<
name|FacetResult
argument_list|>
name|getFacetResults
parameter_list|()
throws|throws
name|IOException
block|{
name|finish
argument_list|()
expr_stmt|;
return|return
name|accumulator
operator|.
name|accumulate
argument_list|(
name|matchingDocs
argument_list|)
return|;
block|}
comment|/**    * Returns the documents matched by the query, one {@link MatchingDocs} per    * visited segment.    */
DECL|method|getMatchingDocs
specifier|public
specifier|final
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|getMatchingDocs
parameter_list|()
block|{
name|finish
argument_list|()
expr_stmt|;
return|return
name|matchingDocs
return|;
block|}
comment|/**    * Allows to reuse the collector between search requests. This method simply    * clears all collected documents (and scores) information, and does not    * attempt to reuse allocated memory spaces.    */
DECL|method|reset
specifier|public
specifier|final
name|void
name|reset
parameter_list|()
block|{
name|finish
argument_list|()
expr_stmt|;
name|matchingDocs
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
