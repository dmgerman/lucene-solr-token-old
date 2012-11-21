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
name|File
import|;
end_import
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
name|Arrays
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|_TestUtil
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
name|FacetTestUtils
operator|.
name|IndexTaxonomyReaderPair
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
operator|.
name|IndexTaxonomyWriterPair
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
name|DefaultFacetIndexingParams
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestTotalFacetCounts
specifier|public
class|class
name|TestTotalFacetCounts
extends|extends
name|LuceneTestCase
block|{
DECL|method|initCache
specifier|private
specifier|static
name|void
name|initCache
parameter_list|(
name|int
name|numEntries
parameter_list|)
block|{
name|TotalFacetCountsCache
operator|.
name|getSingleton
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|TotalFacetCountsCache
operator|.
name|getSingleton
argument_list|()
operator|.
name|setCacheSize
argument_list|(
name|numEntries
argument_list|)
expr_stmt|;
comment|// Set to keep one in mem
block|}
annotation|@
name|Test
DECL|method|testWriteRead
specifier|public
name|void
name|testWriteRead
parameter_list|()
throws|throws
name|IOException
block|{
name|doTestWriteRead
argument_list|(
literal|14
argument_list|)
expr_stmt|;
name|doTestWriteRead
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|doTestWriteRead
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|doTestWriteRead
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|doTestWriteRead
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestWriteRead
specifier|private
name|void
name|doTestWriteRead
parameter_list|(
specifier|final
name|int
name|partitionSize
parameter_list|)
throws|throws
name|IOException
block|{
name|initCache
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// Create temporary RAMDirectories
name|Directory
index|[]
index|[]
name|dirs
init|=
name|FacetTestUtils
operator|.
name|createIndexTaxonomyDirs
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// Create our index/taxonomy writers
name|IndexTaxonomyWriterPair
index|[]
name|writers
init|=
name|FacetTestUtils
operator|.
name|createIndexTaxonomyWriterPair
argument_list|(
name|dirs
argument_list|)
decl_stmt|;
name|DefaultFacetIndexingParams
name|iParams
init|=
operator|new
name|DefaultFacetIndexingParams
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|int
name|fixedPartitionSize
parameter_list|()
block|{
return|return
name|partitionSize
return|;
block|}
block|}
decl_stmt|;
comment|// The counts that the TotalFacetCountsArray should have after adding
comment|// the below facets to the index.
name|int
index|[]
name|expectedCounts
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|1
block|,
literal|3
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
decl_stmt|;
comment|// Add a facet to the index
name|TestTotalFacetCountsCache
operator|.
name|addFacets
argument_list|(
name|iParams
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|indexWriter
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|taxWriter
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|TestTotalFacetCountsCache
operator|.
name|addFacets
argument_list|(
name|iParams
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|indexWriter
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|taxWriter
argument_list|,
literal|"c"
argument_list|,
literal|"d"
argument_list|)
expr_stmt|;
name|TestTotalFacetCountsCache
operator|.
name|addFacets
argument_list|(
name|iParams
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|indexWriter
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|taxWriter
argument_list|,
literal|"a"
argument_list|,
literal|"e"
argument_list|)
expr_stmt|;
name|TestTotalFacetCountsCache
operator|.
name|addFacets
argument_list|(
name|iParams
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|indexWriter
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|taxWriter
argument_list|,
literal|"a"
argument_list|,
literal|"d"
argument_list|)
expr_stmt|;
name|TestTotalFacetCountsCache
operator|.
name|addFacets
argument_list|(
name|iParams
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|indexWriter
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|taxWriter
argument_list|,
literal|"c"
argument_list|,
literal|"g"
argument_list|)
expr_stmt|;
name|TestTotalFacetCountsCache
operator|.
name|addFacets
argument_list|(
name|iParams
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|indexWriter
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|taxWriter
argument_list|,
literal|"c"
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
name|TestTotalFacetCountsCache
operator|.
name|addFacets
argument_list|(
name|iParams
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|indexWriter
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|taxWriter
argument_list|,
literal|"b"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|TestTotalFacetCountsCache
operator|.
name|addFacets
argument_list|(
name|iParams
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|indexWriter
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|taxWriter
argument_list|,
literal|"1"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|TestTotalFacetCountsCache
operator|.
name|addFacets
argument_list|(
name|iParams
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|indexWriter
argument_list|,
name|writers
index|[
literal|0
index|]
operator|.
name|taxWriter
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
comment|// Commit Changes
name|writers
index|[
literal|0
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexTaxonomyReaderPair
index|[]
name|readers
init|=
name|FacetTestUtils
operator|.
name|createIndexTaxonomyReaderPair
argument_list|(
name|dirs
argument_list|)
decl_stmt|;
name|int
index|[]
name|intArray
init|=
operator|new
name|int
index|[
name|iParams
operator|.
name|getPartitionSize
argument_list|()
index|]
decl_stmt|;
name|TotalFacetCountsCache
name|tfcc
init|=
name|TotalFacetCountsCache
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
name|File
name|tmpFile
init|=
name|_TestUtil
operator|.
name|createTempFile
argument_list|(
literal|"test"
argument_list|,
literal|"tmp"
argument_list|,
name|TEMP_DIR
argument_list|)
decl_stmt|;
name|tfcc
operator|.
name|store
argument_list|(
name|tmpFile
argument_list|,
name|readers
index|[
literal|0
index|]
operator|.
name|indexReader
argument_list|,
name|readers
index|[
literal|0
index|]
operator|.
name|taxReader
argument_list|,
name|iParams
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|tfcc
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// not really required because TFCC overrides on load(), but in the test we need not rely on this.
name|tfcc
operator|.
name|load
argument_list|(
name|tmpFile
argument_list|,
name|readers
index|[
literal|0
index|]
operator|.
name|indexReader
argument_list|,
name|readers
index|[
literal|0
index|]
operator|.
name|taxReader
argument_list|,
name|iParams
argument_list|)
expr_stmt|;
comment|// now retrieve the one just loaded
name|TotalFacetCounts
name|totalCounts
init|=
name|tfcc
operator|.
name|getTotalCounts
argument_list|(
name|readers
index|[
literal|0
index|]
operator|.
name|indexReader
argument_list|,
name|readers
index|[
literal|0
index|]
operator|.
name|taxReader
argument_list|,
name|iParams
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|partition
init|=
literal|0
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
name|expectedCounts
operator|.
name|length
condition|;
name|i
operator|+=
name|partitionSize
control|)
block|{
name|totalCounts
operator|.
name|fillTotalCountsForPartition
argument_list|(
name|intArray
argument_list|,
name|partition
argument_list|)
expr_stmt|;
name|int
index|[]
name|partitionExpectedCounts
init|=
operator|new
name|int
index|[
name|partitionSize
index|]
decl_stmt|;
name|int
name|nToCopy
init|=
name|Math
operator|.
name|min
argument_list|(
name|partitionSize
argument_list|,
name|expectedCounts
operator|.
name|length
operator|-
name|i
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|expectedCounts
argument_list|,
name|i
argument_list|,
name|partitionExpectedCounts
argument_list|,
literal|0
argument_list|,
name|nToCopy
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Wrong counts! for partition "
operator|+
name|partition
operator|+
literal|"\nExpected:\n"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|partitionExpectedCounts
argument_list|)
operator|+
literal|"\nActual:\n"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|intArray
argument_list|)
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|partitionExpectedCounts
argument_list|,
name|intArray
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|partition
expr_stmt|;
block|}
name|readers
index|[
literal|0
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|tmpFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
