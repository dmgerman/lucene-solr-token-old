begin_unit
begin_package
DECL|package|org.apache.lucene.facet.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|simple
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|index
operator|.
name|SlowCompositeReaderWrapper
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
begin_class
DECL|class|TestSortedSetDocValuesFacets
specifier|public
class|class
name|TestSortedSetDocValuesFacets
extends|extends
name|FacetTestCase
block|{
comment|// NOTE: TestDrillSideways.testRandom also sometimes
comment|// randomly uses SortedSetDV
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"here: "
operator|+
name|defaultCodecSupportsSortedSet
argument_list|()
argument_list|)
expr_stmt|;
name|assumeTrue
argument_list|(
literal|"Test requires SortedSetDV support"
argument_list|,
name|defaultCodecSupportsSortedSet
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setMultiValued
argument_list|(
literal|"a"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
argument_list|)
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
operator|new
name|DocumentBuilder
argument_list|(
literal|null
argument_list|,
name|config
argument_list|)
decl_stmt|;
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"zoo"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"b"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: now add"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Per-top-reader state:
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|SortedSetDocValuesReaderState
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|SimpleFacetsCollector
name|c
init|=
operator|new
name|SimpleFacetsCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|SortedSetDocValuesFacetCounts
name|facets
init|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|c
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a (4)\n  foo (2)\n  bar (1)\n  zoo (1)\n"
argument_list|,
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"a"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b (1)\n  baz (1)\n"
argument_list|,
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"b"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// DrillDown:
name|SimpleDrillDownQuery
name|q
init|=
operator|new
name|SimpleDrillDownQuery
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
literal|"b"
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-5090
DECL|method|testStaleState
specifier|public
name|void
name|testStaleState
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"Test requires SortedSetDV support"
argument_list|,
name|defaultCodecSupportsSortedSet
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
argument_list|)
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
operator|new
name|DocumentBuilder
argument_list|(
literal|null
argument_list|,
operator|new
name|FacetsConfig
argument_list|()
argument_list|)
decl_stmt|;
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|SortedSetDocValuesReaderState
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|SimpleFacetsCollector
name|c
init|=
operator|new
name|SimpleFacetsCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"did not hit expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-5333
DECL|method|testSparseFacets
specifier|public
name|void
name|testSparseFacets
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"Test requires SortedSetDV support"
argument_list|,
name|defaultCodecSupportsSortedSet
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
argument_list|)
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
operator|new
name|DocumentBuilder
argument_list|(
literal|null
argument_list|,
operator|new
name|FacetsConfig
argument_list|()
argument_list|)
decl_stmt|;
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo2"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"b"
argument_list|,
literal|"bar1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo3"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"b"
argument_list|,
literal|"bar2"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"c"
argument_list|,
literal|"baz1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Per-top-reader state:
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|SortedSetDocValuesReaderState
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|SimpleFacetsCollector
name|c
init|=
operator|new
name|SimpleFacetsCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|SortedSetDocValuesFacetCounts
name|facets
init|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|c
argument_list|)
decl_stmt|;
comment|// Ask for top 10 labels for any dims that have counts:
name|List
argument_list|<
name|SimpleFacetResult
argument_list|>
name|results
init|=
name|facets
operator|.
name|getAllDims
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a (3)\n  foo1 (1)\n  foo2 (1)\n  foo3 (1)\n"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b (2)\n  bar1 (1)\n  bar2 (1)\n"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c (1)\n  baz1 (1)\n"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// nocommit test different delim char& using the default
comment|// one in a dim
comment|// nocommit in the sparse case test that we are really
comment|// sorting by the correct dim count
DECL|method|testSlowCompositeReaderWrapper
specifier|public
name|void
name|testSlowCompositeReaderWrapper
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"Test requires SortedSetDV support"
argument_list|,
name|defaultCodecSupportsSortedSet
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
argument_list|)
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
operator|new
name|DocumentBuilder
argument_list|(
literal|null
argument_list|,
operator|new
name|FacetsConfig
argument_list|()
argument_list|)
decl_stmt|;
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo2"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|builder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// Per-top-reader state:
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|SortedSetDocValuesReaderState
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|SimpleFacetsCollector
name|c
init|=
operator|new
name|SimpleFacetsCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|c
argument_list|)
decl_stmt|;
comment|// Ask for top 10 labels for any dims that have counts:
name|assertEquals
argument_list|(
literal|"a (2)\n  foo1 (1)\n  foo2 (1)\n"
argument_list|,
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"a"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
