begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.directory
package|package
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
name|Random
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
name|core
operator|.
name|KeywordAnalyzer
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
name|index
operator|.
name|LogByteSizeMergePolicy
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
operator|.
name|OpenMode
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
name|LogMergePolicy
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
name|AlreadyClosedException
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
name|store
operator|.
name|RAMDirectory
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
name|LuceneTestCase
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
DECL|class|TestDirectoryTaxonomyReader
specifier|public
class|class
name|TestDirectoryTaxonomyReader
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testCloseAfterIncRef
specifier|public
name|void
name|testCloseAfterIncRef
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|ltw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|ltw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|ltw
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|ltr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|ltr
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|ltr
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// should not fail as we incRef() before close
name|ltr
operator|.
name|getSize
argument_list|()
expr_stmt|;
name|ltr
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloseTwice
specifier|public
name|void
name|testCloseTwice
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|ltw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|ltw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|ltw
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|ltr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|ltr
operator|.
name|close
argument_list|()
expr_stmt|;
name|ltr
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// no exception should be thrown
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpenIfChangedResult
specifier|public
name|void
name|testOpenIfChangedResult
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
literal|null
decl_stmt|;
name|DirectoryTaxonomyWriter
name|ltw
init|=
literal|null
decl_stmt|;
name|DirectoryTaxonomyReader
name|ltr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|ltw
operator|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|ltw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|ltw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ltr
operator|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Nothing has changed"
argument_list|,
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|ltr
argument_list|)
argument_list|)
expr_stmt|;
name|ltw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|ltw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|newtr
init|=
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|ltr
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"changes were committed"
argument_list|,
name|newtr
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Nothing has changed"
argument_list|,
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|newtr
argument_list|)
argument_list|)
expr_stmt|;
name|newtr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|ltw
argument_list|,
name|ltr
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAlreadyClosed
specifier|public
name|void
name|testAlreadyClosed
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|ltw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|ltw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|ltw
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|ltr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|ltr
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|ltr
operator|.
name|getSize
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"An AlreadyClosedException should have been thrown here"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|ace
parameter_list|)
block|{
comment|// good!
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * recreating a taxonomy should work well with a freshly opened taxonomy reader     */
annotation|@
name|Test
DECL|method|testFreshReadRecreatedTaxonomy
specifier|public
name|void
name|testFreshReadRecreatedTaxonomy
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestReadRecreatedTaxonomy
argument_list|(
name|random
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOpenIfChangedReadRecreatedTaxonomy
specifier|public
name|void
name|testOpenIfChangedReadRecreatedTaxonomy
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestReadRecreatedTaxonomy
argument_list|(
name|random
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestReadRecreatedTaxonomy
specifier|private
name|void
name|doTestReadRecreatedTaxonomy
parameter_list|(
name|Random
name|random
parameter_list|,
name|boolean
name|closeReader
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
literal|null
decl_stmt|;
name|TaxonomyWriter
name|tw
init|=
literal|null
decl_stmt|;
name|TaxonomyReader
name|tr
init|=
literal|null
decl_stmt|;
comment|// prepare a few categories
name|int
name|n
init|=
literal|10
decl_stmt|;
name|CategoryPath
index|[]
name|cp
init|=
operator|new
name|CategoryPath
index|[
name|n
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|cp
index|[
name|i
index|]
operator|=
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|tw
operator|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|tw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|tr
operator|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|int
name|baseNumCategories
init|=
name|tr
operator|.
name|getSize
argument_list|()
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|int
name|k
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|tw
operator|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|k
condition|;
name|j
operator|++
control|)
block|{
name|tw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|cp
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|closeReader
condition|)
block|{
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|tr
operator|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|TaxonomyReader
name|newtr
init|=
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|tr
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newtr
argument_list|)
expr_stmt|;
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|tr
operator|=
name|newtr
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Wrong #categories in taxonomy (i="
operator|+
name|i
operator|+
literal|", k="
operator|+
name|k
operator|+
literal|")"
argument_list|,
name|baseNumCategories
operator|+
literal|1
operator|+
name|k
argument_list|,
name|tr
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|tr
argument_list|,
name|tw
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOpenIfChangedAndRefCount
specifier|public
name|void
name|testOpenIfChangedAndRefCount
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// no need for random directories here
name|DirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|taxoWriter
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|TaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong refCount"
argument_list|,
literal|1
argument_list|,
name|taxoReader
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|taxoReader
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong refCount"
argument_list|,
literal|2
argument_list|,
name|taxoReader
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|TaxonomyReader
name|newtr
init|=
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|taxoReader
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newtr
argument_list|)
expr_stmt|;
name|taxoReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoReader
operator|=
name|newtr
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong refCount"
argument_list|,
literal|1
argument_list|,
name|taxoReader
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoReader
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
annotation|@
name|Test
DECL|method|testOpenIfChangedManySegments
specifier|public
name|void
name|testOpenIfChangedManySegments
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test openIfChanged() when the taxonomy contains many segments
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|writer
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|IndexWriterConfig
name|createIndexWriterConfig
parameter_list|(
name|OpenMode
name|openMode
parameter_list|)
block|{
name|IndexWriterConfig
name|conf
init|=
name|super
operator|.
name|createIndexWriterConfig
argument_list|(
name|openMode
argument_list|)
decl_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|conf
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMergeFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
block|}
decl_stmt|;
name|TaxonomyReader
name|reader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|int
name|numRounds
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|10
decl_stmt|;
name|int
name|numCategories
init|=
literal|1
decl_stmt|;
comment|// one for root
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numRounds
condition|;
name|i
operator|++
control|)
block|{
name|int
name|numCats
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numCats
condition|;
name|j
operator|++
control|)
block|{
name|writer
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|numCategories
operator|+=
name|numCats
operator|+
literal|1
comment|/* one for round-parent */
expr_stmt|;
name|TaxonomyReader
name|newtr
init|=
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newtr
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|newtr
expr_stmt|;
comment|// assert categories
name|assertEquals
argument_list|(
name|numCategories
argument_list|,
name|reader
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|roundOrdinal
init|=
name|reader
operator|.
name|getOrdinal
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
index|[]
name|parents
init|=
name|reader
operator|.
name|getParallelTaxonomyArrays
argument_list|()
operator|.
name|parents
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|parents
index|[
name|roundOrdinal
index|]
argument_list|)
expr_stmt|;
comment|// round's parent is root
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numCats
condition|;
name|j
operator|++
control|)
block|{
name|int
name|ord
init|=
name|reader
operator|.
name|getOrdinal
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|roundOrdinal
argument_list|,
name|parents
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
comment|// round's parent is root
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
annotation|@
name|Test
DECL|method|testOpenIfChangedMergedSegment
specifier|public
name|void
name|testOpenIfChangedMergedSegment
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test openIfChanged() when all index segments were merged - used to be
comment|// a bug in ParentArray, caught by testOpenIfChangedManySegments - only
comment|// this test is not random
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// hold onto IW to forceMerge
comment|// note how we don't close it, since DTW will close it.
specifier|final
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|KeywordAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogByteSizeMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyWriter
name|writer
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|IndexWriter
name|openIndexWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|IndexWriterConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|iw
return|;
block|}
block|}
decl_stmt|;
name|TaxonomyReader
name|reader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|getParallelTaxonomyArrays
argument_list|()
operator|.
name|parents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// add category and call forceMerge -- this should flush IW and merge segments down to 1
comment|// in ParentArray.initFromReader, this used to fail assuming there are no parents.
name|writer
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// now calling openIfChanged should trip on the bug
name|TaxonomyReader
name|newtr
init|=
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newtr
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|newtr
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|getParallelTaxonomyArrays
argument_list|()
operator|.
name|parents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
annotation|@
name|Test
DECL|method|testOpenIfChangedNoChangesButSegmentMerges
specifier|public
name|void
name|testOpenIfChangedNoChangesButSegmentMerges
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test openIfChanged() when the taxonomy hasn't really changed, but segments
comment|// were merged. The NRT reader will be reopened, and ParentArray used to assert
comment|// that the new reader contains more ordinals than were given from the old
comment|// TaxReader version
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// hold onto IW to forceMerge
comment|// note how we don't close it, since DTW will close it.
specifier|final
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|KeywordAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogByteSizeMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyWriter
name|writer
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|IndexWriter
name|openIndexWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|IndexWriterConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|iw
return|;
block|}
block|}
decl_stmt|;
comment|// add a category so that the following DTR open will cause a flush and
comment|// a new segment will be created
name|writer
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|TaxonomyReader
name|reader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|getParallelTaxonomyArrays
argument_list|()
operator|.
name|parents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// merge all the segments so that NRT reader thinks there's a change
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// now calling openIfChanged should trip on the wrong assert in ParetArray's ctor
name|TaxonomyReader
name|newtr
init|=
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|newtr
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|newtr
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|getParallelTaxonomyArrays
argument_list|()
operator|.
name|parents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
annotation|@
name|Test
DECL|method|testOpenIfChangedReuseAfterRecreate
specifier|public
name|void
name|testOpenIfChangedReuseAfterRecreate
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests that if the taxonomy is recreated, no data is reused from the previous taxonomy
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|writer
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|CategoryPath
name|cp_a
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addCategory
argument_list|(
name|cp_a
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|r1
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|// fill r1's caches
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r1
operator|.
name|getOrdinal
argument_list|(
name|cp_a
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cp_a
argument_list|,
name|r1
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// now recreate, add a different category
name|writer
operator|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|)
expr_stmt|;
name|CategoryPath
name|cp_b
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addCategory
argument_list|(
name|cp_b
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|r2
init|=
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|r1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r2
argument_list|)
expr_stmt|;
comment|// fill r2's caches
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r2
operator|.
name|getOrdinal
argument_list|(
name|cp_b
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cp_b
argument_list|,
name|r2
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that r1 doesn't see cp_b
name|assertEquals
argument_list|(
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
argument_list|,
name|r1
operator|.
name|getOrdinal
argument_list|(
name|cp_b
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cp_a
argument_list|,
name|r1
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that r2 doesn't see cp_a
name|assertEquals
argument_list|(
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
argument_list|,
name|r2
operator|.
name|getOrdinal
argument_list|(
name|cp_a
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cp_b
argument_list|,
name|r2
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|r1
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
annotation|@
name|Test
DECL|method|testOpenIfChangedReuse
specifier|public
name|void
name|testOpenIfChangedReuse
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test the reuse of data from the old DTR instance
for|for
control|(
name|boolean
name|nrt
range|:
operator|new
name|boolean
index|[]
block|{
literal|false
block|,
literal|true
block|}
control|)
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|writer
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|CategoryPath
name|cp_a
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addCategory
argument_list|(
name|cp_a
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|nrt
condition|)
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|r1
init|=
name|nrt
condition|?
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|writer
argument_list|)
else|:
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|// fill r1's caches
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r1
operator|.
name|getOrdinal
argument_list|(
name|cp_a
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cp_a
argument_list|,
name|r1
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|CategoryPath
name|cp_b
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addCategory
argument_list|(
name|cp_b
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|nrt
condition|)
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|r2
init|=
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|r1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r2
argument_list|)
expr_stmt|;
comment|// add r2's categories to the caches
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r2
operator|.
name|getOrdinal
argument_list|(
name|cp_b
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cp_b
argument_list|,
name|r2
operator|.
name|getPath
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that r1 doesn't see cp_b
name|assertEquals
argument_list|(
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
argument_list|,
name|r1
operator|.
name|getOrdinal
argument_list|(
name|cp_b
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|r1
operator|.
name|getPath
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|r1
operator|.
name|close
argument_list|()
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
block|}
annotation|@
name|Test
DECL|method|testOpenIfChangedReplaceTaxonomy
specifier|public
name|void
name|testOpenIfChangedReplaceTaxonomy
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test openIfChanged when replaceTaxonomy is called, which is equivalent to recreate
comment|// only can work with NRT as well
name|Directory
name|src
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|w
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|CategoryPath
name|cp_b
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|w
operator|.
name|addCategory
argument_list|(
name|cp_b
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|boolean
name|nrt
range|:
operator|new
name|boolean
index|[]
block|{
literal|false
block|,
literal|true
block|}
control|)
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|writer
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|CategoryPath
name|cp_a
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addCategory
argument_list|(
name|cp_a
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|nrt
condition|)
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|r1
init|=
name|nrt
condition|?
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|writer
argument_list|)
else|:
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|// fill r1's caches
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r1
operator|.
name|getOrdinal
argument_list|(
name|cp_a
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cp_a
argument_list|,
name|r1
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// now replace taxonomy
name|writer
operator|.
name|replaceTaxonomy
argument_list|(
name|src
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|nrt
condition|)
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|r2
init|=
name|TaxonomyReader
operator|.
name|openIfChanged
argument_list|(
name|r1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r2
argument_list|)
expr_stmt|;
comment|// fill r2's caches
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r2
operator|.
name|getOrdinal
argument_list|(
name|cp_b
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cp_b
argument_list|,
name|r2
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that r1 doesn't see cp_b
name|assertEquals
argument_list|(
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
argument_list|,
name|r1
operator|.
name|getOrdinal
argument_list|(
name|cp_b
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cp_a
argument_list|,
name|r1
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that r2 doesn't see cp_a
name|assertEquals
argument_list|(
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
argument_list|,
name|r2
operator|.
name|getOrdinal
argument_list|(
name|cp_a
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cp_b
argument_list|,
name|r2
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|r2
operator|.
name|close
argument_list|()
expr_stmt|;
name|r1
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
name|src
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
