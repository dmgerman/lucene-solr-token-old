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
name|HashMap
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
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|search
operator|.
name|results
operator|.
name|FacetResultNode
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
name|Query
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestTopKResultsHandlerRandom
specifier|public
class|class
name|TestTopKResultsHandlerRandom
extends|extends
name|BaseTestTopK
block|{
DECL|method|countFacets
specifier|private
name|List
argument_list|<
name|FacetResult
argument_list|>
name|countFacets
parameter_list|(
name|FacetIndexingParams
name|fip
parameter_list|,
name|int
name|numResults
parameter_list|,
specifier|final
name|boolean
name|doComplement
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|FacetSearchParams
name|facetSearchParams
init|=
name|searchParamsWithRequests
argument_list|(
name|numResults
argument_list|,
name|fip
argument_list|)
decl_stmt|;
name|FacetsCollector
name|fc
init|=
operator|new
name|StandardFacetsCollector
argument_list|(
name|facetSearchParams
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|FacetsAccumulator
name|initFacetsAccumulator
parameter_list|(
name|FacetSearchParams
name|facetSearchParams
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|TaxonomyReader
name|taxonomyReader
parameter_list|)
block|{
name|FacetsAccumulator
name|accumulator
init|=
operator|new
name|StandardFacetsAccumulator
argument_list|(
name|facetSearchParams
argument_list|,
name|indexReader
argument_list|,
name|taxonomyReader
argument_list|)
decl_stmt|;
name|double
name|complement
init|=
name|doComplement
condition|?
name|FacetsAccumulator
operator|.
name|FORCE_COMPLEMENT
else|:
name|FacetsAccumulator
operator|.
name|DISABLE_COMPLEMENT
decl_stmt|;
name|accumulator
operator|.
name|setComplementThreshold
argument_list|(
name|complement
argument_list|)
expr_stmt|;
return|return
name|accumulator
return|;
block|}
block|}
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
return|return
name|facetResults
return|;
block|}
comment|/**    * Test that indeed top results are returned, ordered same as all results     * also when some facets have the same counts.    */
annotation|@
name|Test
DECL|method|testTopCountsOrder
specifier|public
name|void
name|testTopCountsOrder
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|partitionSize
range|:
name|partitionSizes
control|)
block|{
name|FacetIndexingParams
name|fip
init|=
name|getFacetIndexingParams
argument_list|(
name|partitionSize
argument_list|)
decl_stmt|;
name|initIndex
argument_list|(
name|fip
argument_list|)
expr_stmt|;
comment|/*        * Try out faceted search in it's most basic form (no sampling nor complement        * that is). In this test lots (and lots..) of randomly generated data is        * being indexed, and later on an "over-all" faceted search is performed. The        * results are checked against the DF of each facet by itself        */
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
init|=
name|countFacets
argument_list|(
name|fip
argument_list|,
literal|100000
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertCountsAndCardinality
argument_list|(
name|facetCountsTruth
argument_list|()
argument_list|,
name|facetResults
argument_list|)
expr_stmt|;
comment|/*        * Try out faceted search with complements. In this test lots (and lots..) of        * randomly generated data is being indexed, and later on, a "beta" faceted        * search is performed - retrieving ~90% of the documents so complements takes        * place in here. The results are checked against the a regular (a.k.a        * no-complement, no-sampling) faceted search with the same parameters.        */
name|facetResults
operator|=
name|countFacets
argument_list|(
name|fip
argument_list|,
literal|100000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertCountsAndCardinality
argument_list|(
name|facetCountsTruth
argument_list|()
argument_list|,
name|facetResults
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|allFacetResults
init|=
name|countFacets
argument_list|(
name|fip
argument_list|,
literal|100000
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|all
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|maxNumNodes
init|=
literal|0
decl_stmt|;
name|int
name|k
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FacetResult
name|fr
range|:
name|allFacetResults
control|)
block|{
name|FacetResultNode
name|topResNode
init|=
name|fr
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|maxNumNodes
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxNumNodes
argument_list|,
name|topResNode
operator|.
name|subResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|prevCount
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FacetResultNode
name|frn
range|:
name|topResNode
operator|.
name|subResults
control|)
block|{
name|assertTrue
argument_list|(
literal|"wrong counts order: prev="
operator|+
name|prevCount
operator|+
literal|" curr="
operator|+
name|frn
operator|.
name|value
argument_list|,
name|prevCount
operator|>=
name|frn
operator|.
name|value
argument_list|)
expr_stmt|;
name|prevCount
operator|=
operator|(
name|int
operator|)
name|frn
operator|.
name|value
expr_stmt|;
name|String
name|key
init|=
name|k
operator|+
literal|"--"
operator|+
name|frn
operator|.
name|label
operator|+
literal|"=="
operator|+
name|frn
operator|.
name|value
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|frn
operator|.
name|label
operator|+
literal|" - "
operator|+
name|frn
operator|.
name|value
operator|+
literal|"  "
operator|+
name|key
operator|+
literal|"  "
operator|+
name|pos
argument_list|)
expr_stmt|;
block|}
name|all
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|pos
operator|++
argument_list|)
expr_stmt|;
comment|// will use this later to verify order of sub-results
block|}
name|k
operator|++
expr_stmt|;
block|}
comment|// verify that when asking for less results, they are always of highest counts
comment|// also verify that the order is stable
for|for
control|(
name|int
name|n
init|=
literal|1
init|;
name|n
operator|<
name|maxNumNodes
condition|;
name|n
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-------  verify for "
operator|+
name|n
operator|+
literal|" top results"
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|FacetResult
argument_list|>
name|someResults
init|=
name|countFacets
argument_list|(
name|fip
argument_list|,
name|n
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|k
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|FacetResult
name|fr
range|:
name|someResults
control|)
block|{
name|FacetResultNode
name|topResNode
init|=
name|fr
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"too many results: n="
operator|+
name|n
operator|+
literal|" but got "
operator|+
name|topResNode
operator|.
name|subResults
operator|.
name|size
argument_list|()
argument_list|,
name|n
operator|>=
name|topResNode
operator|.
name|subResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FacetResultNode
name|frn
range|:
name|topResNode
operator|.
name|subResults
control|)
block|{
name|String
name|key
init|=
name|k
operator|+
literal|"--"
operator|+
name|frn
operator|.
name|label
operator|+
literal|"=="
operator|+
name|frn
operator|.
name|value
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|frn
operator|.
name|label
operator|+
literal|" - "
operator|+
name|frn
operator|.
name|value
operator|+
literal|"  "
operator|+
name|key
operator|+
literal|"  "
operator|+
name|pos
argument_list|)
expr_stmt|;
block|}
name|Integer
name|origPos
init|=
name|all
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"missing in all results: "
operator|+
name|frn
argument_list|,
name|origPos
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong order of sub-results!"
argument_list|,
name|pos
operator|++
argument_list|,
name|origPos
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify order of sub-results
block|}
name|k
operator|++
expr_stmt|;
block|}
block|}
name|closeAll
argument_list|()
expr_stmt|;
comment|// done with this partition
block|}
block|}
annotation|@
name|Override
DECL|method|numDocsToIndex
specifier|protected
name|int
name|numDocsToIndex
parameter_list|()
block|{
return|return
name|TEST_NIGHTLY
condition|?
literal|20000
else|:
literal|1000
return|;
block|}
block|}
end_class
end_unit
