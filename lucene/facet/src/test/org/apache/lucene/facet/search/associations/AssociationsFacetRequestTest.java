begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search.associations
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
name|associations
package|;
end_package
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
name|analysis
operator|.
name|MockAnalyzer
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
name|analysis
operator|.
name|MockTokenizer
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
name|associations
operator|.
name|AssociationsFacetFields
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
name|associations
operator|.
name|CategoryAssociationsContainer
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
name|associations
operator|.
name|CategoryFloatAssociation
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
name|associations
operator|.
name|CategoryIntAssociation
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
name|params
operator|.
name|associations
operator|.
name|AssociationFloatSumFacetRequest
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
name|associations
operator|.
name|AssociationIntSumFacetRequest
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
name|RandomIndexWriter
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
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
begin_comment
comment|/** Test for associations */
end_comment
begin_class
DECL|class|AssociationsFacetRequestTest
specifier|public
class|class
name|AssociationsFacetRequestTest
extends|extends
name|FacetTestCase
block|{
DECL|field|dir
specifier|private
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|taxoDir
specifier|private
specifier|static
name|Directory
name|taxoDir
decl_stmt|;
DECL|field|aint
specifier|private
specifier|static
specifier|final
name|CategoryPath
name|aint
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"int"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
DECL|field|bint
specifier|private
specifier|static
specifier|final
name|CategoryPath
name|bint
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"int"
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
DECL|field|afloat
specifier|private
specifier|static
specifier|final
name|CategoryPath
name|afloat
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"float"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
DECL|field|bfloat
specifier|private
specifier|static
specifier|final
name|CategoryPath
name|bfloat
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"float"
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClassAssociationsFacetRequestTest
specifier|public
specifier|static
name|void
name|beforeClassAssociationsFacetRequestTest
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|taxoDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
comment|// preparations - index, taxonomy, content
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|AssociationsFacetFields
name|assocFacetFields
init|=
operator|new
name|AssociationsFacetFields
argument_list|(
name|taxoWriter
argument_list|)
decl_stmt|;
comment|// index documents, 50% have only 'b' and all have 'a'
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
name|CategoryAssociationsContainer
name|associations
init|=
operator|new
name|CategoryAssociationsContainer
argument_list|()
decl_stmt|;
name|associations
operator|.
name|setAssociation
argument_list|(
name|aint
argument_list|,
operator|new
name|CategoryIntAssociation
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|associations
operator|.
name|setAssociation
argument_list|(
name|afloat
argument_list|,
operator|new
name|CategoryFloatAssociation
argument_list|(
literal|0.5f
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
comment|// 50
name|associations
operator|.
name|setAssociation
argument_list|(
name|bint
argument_list|,
operator|new
name|CategoryIntAssociation
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|associations
operator|.
name|setAssociation
argument_list|(
name|bfloat
argument_list|,
operator|new
name|CategoryFloatAssociation
argument_list|(
literal|0.2f
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assocFacetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|associations
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClassAssociationsFacetRequestTest
specifier|public
specifier|static
name|void
name|afterClassAssociationsFacetRequestTest
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
name|taxoDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoDir
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntSumAssociation
specifier|public
name|void
name|testIntSumAssociation
parameter_list|()
throws|throws
name|Exception
block|{
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
comment|// facet requests for two facets
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|AssociationIntSumFacetRequest
argument_list|(
name|aint
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|AssociationIntSumFacetRequest
argument_list|(
name|bint
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|fsp
argument_list|,
name|reader
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
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
name|res
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No results!"
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of results!"
argument_list|,
literal|2
argument_list|,
name|res
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'a'!"
argument_list|,
literal|200
argument_list|,
operator|(
name|int
operator|)
name|res
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|value
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'b'!"
argument_list|,
literal|150
argument_list|,
operator|(
name|int
operator|)
name|res
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|value
argument_list|)
expr_stmt|;
name|taxo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFloatSumAssociation
specifier|public
name|void
name|testFloatSumAssociation
parameter_list|()
throws|throws
name|Exception
block|{
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
comment|// facet requests for two facets
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|AssociationFloatSumFacetRequest
argument_list|(
name|afloat
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|AssociationFloatSumFacetRequest
argument_list|(
name|bfloat
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|fsp
argument_list|,
name|reader
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
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
name|res
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"No results!"
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of results!"
argument_list|,
literal|2
argument_list|,
name|res
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'a'!"
argument_list|,
literal|50f
argument_list|,
operator|(
name|float
operator|)
name|res
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|value
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong count for category 'b'!"
argument_list|,
literal|10f
argument_list|,
operator|(
name|float
operator|)
name|res
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|value
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|taxo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDifferentAggregatorsSameCategoryList
specifier|public
name|void
name|testDifferentAggregatorsSameCategoryList
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Same category list cannot be aggregated by two different aggregators. If
comment|// you want to do that, you need to separate the categories into two
comment|// category list (you'll still have one association list).
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
comment|// facet requests for two facets
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|AssociationIntSumFacetRequest
argument_list|(
name|aint
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|AssociationIntSumFacetRequest
argument_list|(
name|bint
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|AssociationFloatSumFacetRequest
argument_list|(
name|afloat
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|AssociationFloatSumFacetRequest
argument_list|(
name|bfloat
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|fsp
argument_list|,
name|reader
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
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
try|try
block|{
name|fc
operator|.
name|getFacetResults
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"different aggregators for same category list should not be supported"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// ok - expected
block|}
name|taxo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
