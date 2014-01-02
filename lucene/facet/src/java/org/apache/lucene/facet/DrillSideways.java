begin_unit
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sortedset
operator|.
name|SortedSetDocValuesFacetCounts
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
name|sortedset
operator|.
name|SortedSetDocValuesFacetField
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
name|sortedset
operator|.
name|SortedSetDocValuesReaderState
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
name|FastTaxonomyFacetCounts
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
name|search
operator|.
name|BooleanClause
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
name|BooleanQuery
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
name|ConstantScoreQuery
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
name|FieldDoc
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
name|MatchAllDocsQuery
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
name|MultiCollector
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
name|ScoreDoc
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
name|Sort
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
name|TermQuery
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
name|TopDocs
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
name|TopFieldCollector
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
name|TopScoreDocCollector
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
begin_comment
comment|/**       * Computes drill down and sideways counts for the provided  * {@link DrillDownQuery}.  Drill sideways counts include  * alternative values/aggregates for the drill-down  * dimensions so that a dimension does not disappear after  * the user drills down into it.  *  *<p> Use one of the static search  * methods to do the search, and then get the hits and facet  * results from the returned {@link DrillSidewaysResult}.  *  *<p><b>NOTE</b>: this allocates one {@link  * FacetsCollector} for each drill-down, plus one.  If your  * index has high number of facet labels then this will  * multiply your memory usage.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DrillSideways
specifier|public
class|class
name|DrillSideways
block|{
comment|/** {@link IndexSearcher} passed to constructor. */
DECL|field|searcher
specifier|protected
specifier|final
name|IndexSearcher
name|searcher
decl_stmt|;
comment|/** {@link TaxonomyReader} passed to constructor. */
DECL|field|taxoReader
specifier|protected
specifier|final
name|TaxonomyReader
name|taxoReader
decl_stmt|;
comment|/** {@link SortedSetDocValuesReaderState} passed to    *  constructor; can be null. */
DECL|field|state
specifier|protected
specifier|final
name|SortedSetDocValuesReaderState
name|state
decl_stmt|;
comment|/** {@link FacetsConfig} passed to constructor. */
DECL|field|config
specifier|protected
specifier|final
name|FacetsConfig
name|config
decl_stmt|;
comment|/** Create a new {@code DrillSideways} instance. */
DECL|method|DrillSideways
specifier|public
name|DrillSideways
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|)
block|{
name|this
argument_list|(
name|searcher
argument_list|,
name|config
argument_list|,
name|taxoReader
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new {@code DrillSideways} instance, assuming the categories were    *  indexed with {@link SortedSetDocValuesFacetField}. */
DECL|method|DrillSideways
specifier|public
name|DrillSideways
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|SortedSetDocValuesReaderState
name|state
parameter_list|)
block|{
name|this
argument_list|(
name|searcher
argument_list|,
name|config
argument_list|,
literal|null
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new {@code DrillSideways} instance, where some    *  dimensions were indexed with {@link    *  SortedSetDocValuesFacetField} and others were indexed    *  with {@link FacetField}. */
DECL|method|DrillSideways
specifier|public
name|DrillSideways
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|SortedSetDocValuesReaderState
name|state
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|taxoReader
operator|=
name|taxoReader
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
comment|/** Subclass can override to customize per-dim Facets    *  impl. */
DECL|method|buildFacetsResult
specifier|protected
name|Facets
name|buildFacetsResult
parameter_list|(
name|FacetsCollector
name|drillDowns
parameter_list|,
name|FacetsCollector
index|[]
name|drillSideways
parameter_list|,
name|String
index|[]
name|drillSidewaysDims
parameter_list|)
throws|throws
name|IOException
block|{
name|Facets
name|drillDownFacets
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Facets
argument_list|>
name|drillSidewaysFacets
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Facets
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|taxoReader
operator|!=
literal|null
condition|)
block|{
name|drillDownFacets
operator|=
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|drillDowns
argument_list|)
expr_stmt|;
if|if
condition|(
name|drillSideways
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|drillSideways
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|drillSidewaysFacets
operator|.
name|put
argument_list|(
name|drillSidewaysDims
index|[
name|i
index|]
argument_list|,
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|drillSideways
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|drillDownFacets
operator|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|drillDowns
argument_list|)
expr_stmt|;
if|if
condition|(
name|drillSideways
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|drillSideways
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|drillSidewaysFacets
operator|.
name|put
argument_list|(
name|drillSidewaysDims
index|[
name|i
index|]
argument_list|,
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|drillSideways
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|drillSidewaysFacets
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|drillDownFacets
return|;
block|}
else|else
block|{
return|return
operator|new
name|MultiFacets
argument_list|(
name|drillSidewaysFacets
argument_list|,
name|drillDownFacets
argument_list|)
return|;
block|}
block|}
comment|/**    * Search, collecting hits with a {@link Collector}, and    * computing drill down and sideways counts.    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|search
specifier|public
name|DrillSidewaysResult
name|search
parameter_list|(
name|DrillDownQuery
name|query
parameter_list|,
name|Collector
name|hitCollector
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
init|=
name|query
operator|.
name|getDims
argument_list|()
decl_stmt|;
name|FacetsCollector
name|drillDownCollector
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
if|if
condition|(
name|drillDownDims
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// There are no drill-down dims, so there is no
comment|// drill-sideways to compute:
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|hitCollector
argument_list|,
name|drillDownCollector
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|DrillSidewaysResult
argument_list|(
name|buildFacetsResult
argument_list|(
name|drillDownCollector
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
name|BooleanQuery
name|ddq
init|=
name|query
operator|.
name|getBooleanQuery
argument_list|()
decl_stmt|;
name|BooleanClause
index|[]
name|clauses
init|=
name|ddq
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|Query
name|baseQuery
decl_stmt|;
name|int
name|startClause
decl_stmt|;
if|if
condition|(
name|clauses
operator|.
name|length
operator|==
name|drillDownDims
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// TODO: we could optimize this pure-browse case by
comment|// making a custom scorer instead:
name|baseQuery
operator|=
operator|new
name|MatchAllDocsQuery
argument_list|()
expr_stmt|;
name|startClause
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|clauses
operator|.
name|length
operator|==
literal|1
operator|+
name|drillDownDims
operator|.
name|size
argument_list|()
assert|;
name|baseQuery
operator|=
name|clauses
index|[
literal|0
index|]
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|startClause
operator|=
literal|1
expr_stmt|;
block|}
name|FacetsCollector
index|[]
name|drillSidewaysCollectors
init|=
operator|new
name|FacetsCollector
index|[
name|drillDownDims
operator|.
name|size
argument_list|()
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
name|drillSidewaysCollectors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|drillSidewaysCollectors
index|[
name|i
index|]
operator|=
operator|new
name|FacetsCollector
argument_list|()
expr_stmt|;
block|}
name|boolean
name|useCollectorMethod
init|=
name|scoreSubDocsAtOnce
argument_list|()
decl_stmt|;
name|Term
index|[]
index|[]
name|drillDownTerms
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|useCollectorMethod
condition|)
block|{
comment|// Optimistic: assume subQueries of the DDQ are either
comment|// TermQuery or BQ OR of TermQuery; if this is wrong
comment|// then we detect it and fallback to the mome general
comment|// but slower DrillSidewaysCollector:
name|drillDownTerms
operator|=
operator|new
name|Term
index|[
name|clauses
operator|.
name|length
operator|-
name|startClause
index|]
index|[]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startClause
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
name|Query
name|q
init|=
name|clauses
index|[
name|i
index|]
operator|.
name|getQuery
argument_list|()
decl_stmt|;
comment|// DrillDownQuery always wraps each subQuery in
comment|// ConstantScoreQuery:
assert|assert
name|q
operator|instanceof
name|ConstantScoreQuery
assert|;
name|q
operator|=
operator|(
operator|(
name|ConstantScoreQuery
operator|)
name|q
operator|)
operator|.
name|getQuery
argument_list|()
expr_stmt|;
if|if
condition|(
name|q
operator|instanceof
name|TermQuery
condition|)
block|{
name|drillDownTerms
index|[
name|i
operator|-
name|startClause
index|]
operator|=
operator|new
name|Term
index|[]
block|{
operator|(
operator|(
name|TermQuery
operator|)
name|q
operator|)
operator|.
name|getTerm
argument_list|()
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|q
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|q2
init|=
operator|(
name|BooleanQuery
operator|)
name|q
decl_stmt|;
name|BooleanClause
index|[]
name|clauses2
init|=
name|q2
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|drillDownTerms
index|[
name|i
operator|-
name|startClause
index|]
operator|=
operator|new
name|Term
index|[
name|clauses2
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|clauses2
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|clauses2
index|[
name|j
index|]
operator|.
name|getQuery
argument_list|()
operator|instanceof
name|TermQuery
condition|)
block|{
name|drillDownTerms
index|[
name|i
operator|-
name|startClause
index|]
index|[
name|j
index|]
operator|=
operator|(
operator|(
name|TermQuery
operator|)
name|clauses2
index|[
name|j
index|]
operator|.
name|getQuery
argument_list|()
operator|)
operator|.
name|getTerm
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|useCollectorMethod
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
else|else
block|{
name|useCollectorMethod
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|useCollectorMethod
condition|)
block|{
comment|// TODO: maybe we could push the "collector method"
comment|// down into the optimized scorer to have a tighter
comment|// integration ... and so TermQuery clauses could
comment|// continue to run "optimized"
name|collectorMethod
argument_list|(
name|query
argument_list|,
name|baseQuery
argument_list|,
name|startClause
argument_list|,
name|hitCollector
argument_list|,
name|drillDownCollector
argument_list|,
name|drillSidewaysCollectors
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DrillSidewaysQuery
name|dsq
init|=
operator|new
name|DrillSidewaysQuery
argument_list|(
name|baseQuery
argument_list|,
name|drillDownCollector
argument_list|,
name|drillSidewaysCollectors
argument_list|,
name|drillDownTerms
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|dsq
argument_list|,
name|hitCollector
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|DrillSidewaysResult
argument_list|(
name|buildFacetsResult
argument_list|(
name|drillDownCollector
argument_list|,
name|drillSidewaysCollectors
argument_list|,
name|drillDownDims
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|drillDownDims
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Uses the more general but slower method of sideways    *  counting. This method allows an arbitrary subQuery to    *  implement the drill down for a given dimension. */
DECL|method|collectorMethod
specifier|private
name|void
name|collectorMethod
parameter_list|(
name|DrillDownQuery
name|ddq
parameter_list|,
name|Query
name|baseQuery
parameter_list|,
name|int
name|startClause
parameter_list|,
name|Collector
name|hitCollector
parameter_list|,
name|Collector
name|drillDownCollector
parameter_list|,
name|Collector
index|[]
name|drillSidewaysCollectors
parameter_list|)
throws|throws
name|IOException
block|{
name|BooleanClause
index|[]
name|clauses
init|=
name|ddq
operator|.
name|getBooleanQuery
argument_list|()
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
init|=
name|ddq
operator|.
name|getDims
argument_list|()
decl_stmt|;
name|BooleanQuery
name|topQuery
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|DrillSidewaysCollector
name|collector
init|=
operator|new
name|DrillSidewaysCollector
argument_list|(
name|hitCollector
argument_list|,
name|drillDownCollector
argument_list|,
name|drillSidewaysCollectors
argument_list|,
name|drillDownDims
argument_list|)
decl_stmt|;
comment|// TODO: if query is already a BQ we could copy that and
comment|// add clauses to it, instead of doing BQ inside BQ
comment|// (should be more efficient)?  Problem is this can
comment|// affect scoring (coord) ... too bad we can't disable
comment|// coord on a clause by clause basis:
name|topQuery
operator|.
name|add
argument_list|(
name|baseQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
comment|// NOTE: in theory we could just make a single BQ, with
comment|// +query a b c minShouldMatch=2, but in this case,
comment|// annoyingly, BS2 wraps a sub-scorer that always
comment|// returns 2 as the .freq(), not how many of the
comment|// SHOULD clauses matched:
name|BooleanQuery
name|subQuery
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Query
name|wrappedSubQuery
init|=
operator|new
name|QueryWrapper
argument_list|(
name|subQuery
argument_list|,
operator|new
name|SetWeight
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|set
parameter_list|(
name|Weight
name|w
parameter_list|)
block|{
name|collector
operator|.
name|setWeight
argument_list|(
name|w
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Query
name|constantScoreSubQuery
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|wrappedSubQuery
argument_list|)
decl_stmt|;
comment|// Don't impact score of original query:
name|constantScoreSubQuery
operator|.
name|setBoost
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
name|topQuery
operator|.
name|add
argument_list|(
name|constantScoreSubQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
comment|// Unfortunately this sub-BooleanQuery
comment|// will never get BS1 because today BS1 only works
comment|// if topScorer=true... and actually we cannot use BS1
comment|// anyways because we need subDocsScoredAtOnce:
name|int
name|dimIndex
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startClause
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
name|Query
name|q
init|=
name|clauses
index|[
name|i
index|]
operator|.
name|getQuery
argument_list|()
decl_stmt|;
comment|// DrillDownQuery always wraps each subQuery in
comment|// ConstantScoreQuery:
assert|assert
name|q
operator|instanceof
name|ConstantScoreQuery
assert|;
name|q
operator|=
operator|(
operator|(
name|ConstantScoreQuery
operator|)
name|q
operator|)
operator|.
name|getQuery
argument_list|()
expr_stmt|;
specifier|final
name|int
name|finalDimIndex
init|=
name|dimIndex
decl_stmt|;
name|subQuery
operator|.
name|add
argument_list|(
operator|new
name|QueryWrapper
argument_list|(
name|q
argument_list|,
operator|new
name|SetWeight
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|set
parameter_list|(
name|Weight
name|w
parameter_list|)
block|{
name|collector
operator|.
name|setWeight
argument_list|(
name|w
argument_list|,
name|finalDimIndex
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|dimIndex
operator|++
expr_stmt|;
block|}
comment|// TODO: we could better optimize the "just one drill
comment|// down" case w/ a separate [specialized]
comment|// collector...
name|int
name|minShouldMatch
init|=
name|drillDownDims
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|minShouldMatch
operator|==
literal|0
condition|)
block|{
comment|// Must add another "fake" clause so BQ doesn't erase
comment|// itself by rewriting to the single clause:
name|Query
name|end
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|end
operator|.
name|setBoost
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
name|subQuery
operator|.
name|add
argument_list|(
name|end
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|minShouldMatch
operator|++
expr_stmt|;
block|}
name|subQuery
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|minShouldMatch
argument_list|)
expr_stmt|;
comment|// System.out.println("EXE " + topQuery);
comment|// Collects against the passed-in
comment|// drillDown/SidewaysCollectors as a side effect:
name|searcher
operator|.
name|search
argument_list|(
name|topQuery
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
comment|/**    * Search, sorting by {@link Sort}, and computing    * drill down and sideways counts.    */
DECL|method|search
specifier|public
name|DrillSidewaysResult
name|search
parameter_list|(
name|DrillDownQuery
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|FieldDoc
name|after
parameter_list|,
name|int
name|topN
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|boolean
name|doDocScores
parameter_list|,
name|boolean
name|doMaxScore
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|DrillDownQuery
argument_list|(
name|config
argument_list|,
name|filter
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sort
operator|!=
literal|null
condition|)
block|{
name|int
name|limit
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|limit
operator|==
literal|0
condition|)
block|{
name|limit
operator|=
literal|1
expr_stmt|;
comment|// the collector does not alow numHits = 0
block|}
name|topN
operator|=
name|Math
operator|.
name|min
argument_list|(
name|topN
argument_list|,
name|limit
argument_list|)
expr_stmt|;
specifier|final
name|TopFieldCollector
name|hitCollector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|topN
argument_list|,
name|after
argument_list|,
literal|true
argument_list|,
name|doDocScores
argument_list|,
name|doMaxScore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DrillSidewaysResult
name|r
init|=
name|search
argument_list|(
name|query
argument_list|,
name|hitCollector
argument_list|)
decl_stmt|;
return|return
operator|new
name|DrillSidewaysResult
argument_list|(
name|r
operator|.
name|facets
argument_list|,
name|hitCollector
operator|.
name|topDocs
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|search
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|topN
argument_list|)
return|;
block|}
block|}
comment|/**    * Search, sorting by score, and computing    * drill down and sideways counts.    */
DECL|method|search
specifier|public
name|DrillSidewaysResult
name|search
parameter_list|(
name|DrillDownQuery
name|query
parameter_list|,
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|search
argument_list|(
literal|null
argument_list|,
name|query
argument_list|,
name|topN
argument_list|)
return|;
block|}
comment|/**    * Search, sorting by score, and computing    * drill down and sideways counts.    */
DECL|method|search
specifier|public
name|DrillSidewaysResult
name|search
parameter_list|(
name|ScoreDoc
name|after
parameter_list|,
name|DrillDownQuery
name|query
parameter_list|,
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|limit
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|limit
operator|==
literal|0
condition|)
block|{
name|limit
operator|=
literal|1
expr_stmt|;
comment|// the collector does not alow numHits = 0
block|}
name|topN
operator|=
name|Math
operator|.
name|min
argument_list|(
name|topN
argument_list|,
name|limit
argument_list|)
expr_stmt|;
name|TopScoreDocCollector
name|hitCollector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|topN
argument_list|,
name|after
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DrillSidewaysResult
name|r
init|=
name|search
argument_list|(
name|query
argument_list|,
name|hitCollector
argument_list|)
decl_stmt|;
return|return
operator|new
name|DrillSidewaysResult
argument_list|(
name|r
operator|.
name|facets
argument_list|,
name|hitCollector
operator|.
name|topDocs
argument_list|()
argument_list|)
return|;
block|}
comment|/** Override this and return true if your collector    *  (e.g., ToParentBlockJoinCollector) expects all    *  sub-scorers to be positioned on the document being    *  collected.  This will cause some performance loss;    *  default is false.  Note that if you return true from    *  this method (in a subclass) be sure your collector    *  also returns false from {@link    *  Collector#acceptsDocsOutOfOrder}: this will trick    *  BooleanQuery into also scoring all subDocs at once. */
DECL|method|scoreSubDocsAtOnce
specifier|protected
name|boolean
name|scoreSubDocsAtOnce
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Result of a drill sideways search, including the    *  {@link Facets} and {@link TopDocs}. */
DECL|class|DrillSidewaysResult
specifier|public
specifier|static
class|class
name|DrillSidewaysResult
block|{
comment|/** Combined drill down& sideways results. */
DECL|field|facets
specifier|public
specifier|final
name|Facets
name|facets
decl_stmt|;
comment|/** Hits. */
DECL|field|hits
specifier|public
specifier|final
name|TopDocs
name|hits
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|DrillSidewaysResult
specifier|public
name|DrillSidewaysResult
parameter_list|(
name|Facets
name|facets
parameter_list|,
name|TopDocs
name|hits
parameter_list|)
block|{
name|this
operator|.
name|facets
operator|=
name|facets
expr_stmt|;
name|this
operator|.
name|hits
operator|=
name|hits
expr_stmt|;
block|}
block|}
DECL|interface|SetWeight
specifier|private
interface|interface
name|SetWeight
block|{
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|Weight
name|w
parameter_list|)
function_decl|;
block|}
comment|/** Just records which Weight was given out for the    *  (possibly rewritten) Query. */
DECL|class|QueryWrapper
specifier|private
specifier|static
class|class
name|QueryWrapper
extends|extends
name|Query
block|{
DECL|field|originalQuery
specifier|private
specifier|final
name|Query
name|originalQuery
decl_stmt|;
DECL|field|setter
specifier|private
specifier|final
name|SetWeight
name|setter
decl_stmt|;
DECL|method|QueryWrapper
specifier|public
name|QueryWrapper
parameter_list|(
name|Query
name|originalQuery
parameter_list|,
name|SetWeight
name|setter
parameter_list|)
block|{
name|this
operator|.
name|originalQuery
operator|=
name|originalQuery
expr_stmt|;
name|this
operator|.
name|setter
operator|=
name|setter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
specifier|final
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|Weight
name|w
init|=
name|originalQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|setter
operator|.
name|set
argument_list|(
name|w
argument_list|)
expr_stmt|;
return|return
name|w
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
name|Query
name|rewritten
init|=
name|originalQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|!=
name|originalQuery
condition|)
block|{
return|return
operator|new
name|QueryWrapper
argument_list|(
name|rewritten
argument_list|,
name|setter
argument_list|)
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
name|s
parameter_list|)
block|{
return|return
name|originalQuery
operator|.
name|toString
argument_list|(
name|s
argument_list|)
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
operator|(
name|o
operator|instanceof
name|QueryWrapper
operator|)
condition|)
return|return
literal|false
return|;
specifier|final
name|QueryWrapper
name|other
init|=
operator|(
name|QueryWrapper
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|&&
name|originalQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|originalQuery
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
return|return
name|super
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
operator|+
name|originalQuery
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
