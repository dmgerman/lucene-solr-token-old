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
name|Collections
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
name|document
operator|.
name|NumericDocValuesField
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
name|FacetTestUtils
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
name|params
operator|.
name|CategoryListParams
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
name|queries
operator|.
name|function
operator|.
name|FunctionQuery
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|DoubleDocValues
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|LongFieldSource
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
DECL|class|TestSumValueSourceFacetRequest
specifier|public
class|class
name|TestSumValueSourceFacetRequest
extends|extends
name|FacetTestCase
block|{
annotation|@
name|Test
DECL|method|testNoScore
specifier|public
name|void
name|testNoScore
parameter_list|()
throws|throws
name|Exception
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
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
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
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxonomyWriter
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
literal|4
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"price"
argument_list|,
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
argument_list|)
expr_stmt|;
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
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|%
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
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
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|ValueSource
name|valueSource
init|=
operator|new
name|LongFieldSource
argument_list|(
literal|"price"
argument_list|)
decl_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|SumValueSourceFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|,
name|valueSource
argument_list|,
literal|false
argument_list|)
argument_list|)
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
name|r
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
name|newSearcher
argument_list|(
name|r
argument_list|)
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
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
name|assertEquals
argument_list|(
literal|"a (0)\n  1 (6)\n  0 (4)\n"
argument_list|,
name|FacetTestUtils
operator|.
name|toSimpleString
argument_list|(
name|res
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|taxo
argument_list|,
name|taxoDir
argument_list|,
name|r
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithScore
specifier|public
name|void
name|testWithScore
parameter_list|()
throws|throws
name|Exception
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
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
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
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxonomyWriter
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
literal|4
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"price"
argument_list|,
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
argument_list|)
expr_stmt|;
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
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|%
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
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
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|ValueSource
name|valueSource
init|=
operator|new
name|ValueSource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|Map
name|context
parameter_list|,
name|AtomicReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|scorer
init|=
operator|(
name|Scorer
operator|)
name|context
operator|.
name|get
argument_list|(
literal|"scorer"
argument_list|)
decl_stmt|;
assert|assert
name|scorer
operator|!=
literal|null
assert|;
return|return
operator|new
name|DoubleDocValues
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|document
parameter_list|)
block|{
try|try
block|{
return|return
name|scorer
operator|.
name|score
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|exception
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|exception
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|==
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"score()"
return|;
block|}
block|}
decl_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|SumValueSourceFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|,
name|valueSource
argument_list|,
literal|true
argument_list|)
argument_list|)
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
name|r
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
name|TopScoreDocCollector
name|tsdc
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// score documents by their 'price' field - makes asserting the correct counts for the categories easier
name|Query
name|q
init|=
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|LongFieldSource
argument_list|(
literal|"price"
argument_list|)
argument_list|)
decl_stmt|;
name|newSearcher
argument_list|(
name|r
argument_list|)
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|tsdc
argument_list|,
name|fc
argument_list|)
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
name|assertEquals
argument_list|(
literal|"a (0)\n  1 (6)\n  0 (4)\n"
argument_list|,
name|FacetTestUtils
operator|.
name|toSimpleString
argument_list|(
name|res
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|taxo
argument_list|,
name|taxoDir
argument_list|,
name|r
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRollupValues
specifier|public
name|void
name|testRollupValues
parameter_list|()
throws|throws
name|Exception
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
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
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
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FacetIndexingParams
name|fip
init|=
operator|new
name|FacetIndexingParams
argument_list|(
operator|new
name|CategoryListParams
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|OrdinalPolicy
name|getOrdinalPolicy
parameter_list|(
name|String
name|dimension
parameter_list|)
block|{
return|return
name|OrdinalPolicy
operator|.
name|NO_PARENTS
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxonomyWriter
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
literal|4
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"price"
argument_list|,
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
argument_list|)
expr_stmt|;
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
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|%
literal|2
argument_list|)
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
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
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|ValueSource
name|valueSource
init|=
operator|new
name|LongFieldSource
argument_list|(
literal|"price"
argument_list|)
decl_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|fip
argument_list|,
operator|new
name|SumValueSourceFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|,
name|valueSource
argument_list|,
literal|false
argument_list|)
argument_list|)
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
name|r
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
name|newSearcher
argument_list|(
name|r
argument_list|)
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
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
name|assertEquals
argument_list|(
literal|"a (10)\n  1 (6)\n  0 (4)\n"
argument_list|,
name|FacetTestUtils
operator|.
name|toSimpleString
argument_list|(
name|res
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|taxo
argument_list|,
name|taxoDir
argument_list|,
name|r
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit