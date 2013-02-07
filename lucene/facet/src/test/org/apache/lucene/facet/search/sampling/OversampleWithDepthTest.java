begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search.sampling
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
operator|.
name|sampling
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
name|Collections
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
name|document
operator|.
name|Document
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
name|FacetTestCase
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
name|FacetFields
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
name|FacetsCollector
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
name|StandardFacetsAccumulator
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
name|FacetRequest
operator|.
name|ResultMode
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyWriter
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
name|directory
operator|.
name|DirectoryTaxonomyReader
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
name|directory
operator|.
name|DirectoryTaxonomyWriter
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
name|DirectoryReader
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
name|IndexWriterConfig
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
name|store
operator|.
name|Directory
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
name|IOUtils
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
DECL|class|OversampleWithDepthTest
specifier|public
class|class
name|OversampleWithDepthTest
extends|extends
name|FacetTestCase
block|{
annotation|@
name|Test
DECL|method|testCountWithdepthUsingSampling
specifier|public
name|void
name|testCountWithdepthUsingSampling
parameter_list|()
throws|throws
name|Exception
throws|,
name|IOException
block|{
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|FacetIndexingParams
name|fip
init|=
operator|new
name|FacetIndexingParams
argument_list|(
name|randomCategoryListParams
argument_list|()
argument_list|)
decl_stmt|;
comment|// index 100 docs, each with one category: ["root", docnum/10, docnum]
comment|// e.g. root/8/87
name|index100Docs
argument_list|(
name|indexDir
argument_list|,
name|taxoDir
argument_list|,
name|fip
argument_list|)
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|TaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|CountFacetRequest
name|facetRequest
init|=
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
comment|// Setting the depth to '2', should potentially get all categories
name|facetRequest
operator|.
name|setDepth
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|facetRequest
operator|.
name|setResultMode
argument_list|(
name|ResultMode
operator|.
name|PER_NODE_IN_TREE
argument_list|)
expr_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|fip
argument_list|,
name|facetRequest
argument_list|)
decl_stmt|;
comment|// Craft sampling params to enforce sampling
specifier|final
name|SamplingParams
name|params
init|=
operator|new
name|SamplingParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|setMinSampleSize
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|params
operator|.
name|setMaxSampleSize
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|params
operator|.
name|setOversampleFactor
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|params
operator|.
name|setSamplingThreshold
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|params
operator|.
name|setSampleRatio
argument_list|(
literal|0.1
argument_list|)
expr_stmt|;
name|FacetResult
name|res
init|=
name|searchWithFacets
argument_list|(
name|r
argument_list|,
name|tr
argument_list|,
name|fsp
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|FacetRequest
name|req
init|=
name|res
operator|.
name|getFacetRequest
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|facetRequest
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|FacetResultNode
name|rootNode
init|=
name|res
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
comment|// Each node below root should also have sub-results as the requested depth was '2'
for|for
control|(
name|FacetResultNode
name|node
range|:
name|rootNode
operator|.
name|subResults
control|)
block|{
name|assertTrue
argument_list|(
literal|"node "
operator|+
name|node
operator|.
name|label
operator|+
literal|" should have had children as the requested depth was '2'"
argument_list|,
name|node
operator|.
name|subResults
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|r
argument_list|,
name|tr
argument_list|,
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
DECL|method|index100Docs
specifier|private
name|void
name|index100Docs
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|Directory
name|taxoDir
parameter_list|,
name|FacetIndexingParams
name|fip
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|TaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|tw
argument_list|,
name|fip
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|/
literal|10
argument_list|)
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|tw
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
comment|/** search reader<code>r</code>*/
DECL|method|searchWithFacets
specifier|private
name|FacetResult
name|searchWithFacets
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|TaxonomyReader
name|tr
parameter_list|,
name|FacetSearchParams
name|fsp
parameter_list|,
specifier|final
name|SamplingParams
name|params
parameter_list|)
throws|throws
name|IOException
block|{
comment|// a FacetsCollector with a sampling accumulator
name|Sampler
name|sampler
init|=
operator|new
name|RandomSampler
argument_list|(
name|params
argument_list|,
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|StandardFacetsAccumulator
name|sfa
init|=
operator|new
name|SamplingAccumulator
argument_list|(
name|sampler
argument_list|,
name|fsp
argument_list|,
name|r
argument_list|,
name|tr
argument_list|)
decl_stmt|;
name|FacetsCollector
name|fcWithSampling
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|sfa
argument_list|)
decl_stmt|;
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fcWithSampling
argument_list|)
expr_stmt|;
comment|// there's only one expected result, return just it.
return|return
name|fcWithSampling
operator|.
name|getFacetResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
end_class
end_unit
