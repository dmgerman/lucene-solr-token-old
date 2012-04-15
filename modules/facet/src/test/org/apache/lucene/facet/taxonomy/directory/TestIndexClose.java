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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|CorruptIndexException
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
name|LockObtainFailedException
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
name|InconsistentTaxonomyException
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * This test case attempts to catch index "leaks" in LuceneTaxonomyReader/Writer,  * i.e., cases where an index has been opened, but never closed; In that case,  * Java would eventually collect this object and close the index, but leaving  * the index open might nevertheless cause problems - e.g., on Windows it prevents  * deleting it.  */
end_comment
begin_class
DECL|class|TestIndexClose
specifier|public
class|class
name|TestIndexClose
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testLeaks
specifier|public
name|void
name|testLeaks
parameter_list|()
throws|throws
name|Exception
block|{
name|LeakChecker
name|checker
init|=
operator|new
name|LeakChecker
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|tw
init|=
name|checker
operator|.
name|openWriter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|tw
operator|=
name|checker
operator|.
name|openWriter
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
literal|"animal"
argument_list|,
literal|"dog"
argument_list|)
argument_list|)
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryTaxonomyReader
name|tr
init|=
name|checker
operator|.
name|openReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|tr
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|tr
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|tr
operator|=
name|checker
operator|.
name|openReader
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|tw
operator|=
name|checker
operator|.
name|openWriter
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
literal|"animal"
argument_list|,
literal|"cat"
argument_list|)
argument_list|)
expr_stmt|;
name|tr
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|tw
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|tr
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|tw
operator|=
name|checker
operator|.
name|openWriter
argument_list|(
name|dir
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
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
literal|"number"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|tw
operator|=
name|checker
operator|.
name|openWriter
argument_list|(
name|dir
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
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
literal|"number"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|*
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|checker
operator|.
name|nopen
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|LeakChecker
specifier|private
specifier|static
class|class
name|LeakChecker
block|{
DECL|field|readers
name|Set
argument_list|<
name|DirectoryReader
argument_list|>
name|readers
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|DirectoryReader
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|iwriter
name|int
name|iwriter
init|=
literal|0
decl_stmt|;
DECL|field|openWriters
name|Set
argument_list|<
name|Integer
argument_list|>
name|openWriters
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|LeakChecker
name|LeakChecker
parameter_list|()
block|{ }
DECL|method|openWriter
specifier|public
name|DirectoryTaxonomyWriter
name|openWriter
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
return|return
operator|new
name|InstrumentedTaxonomyWriter
argument_list|(
name|dir
argument_list|)
return|;
block|}
DECL|method|openReader
specifier|public
name|DirectoryTaxonomyReader
name|openReader
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
return|return
operator|new
name|InstrumentedTaxonomyReader
argument_list|(
name|dir
argument_list|)
return|;
block|}
DECL|method|nopen
specifier|public
name|int
name|nopen
parameter_list|()
block|{
name|int
name|ret
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DirectoryReader
name|r
range|:
name|readers
control|)
block|{
if|if
condition|(
name|r
operator|.
name|getRefCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"reader "
operator|+
name|r
operator|+
literal|" still open"
argument_list|)
expr_stmt|;
name|ret
operator|++
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
range|:
name|openWriters
control|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"writer "
operator|+
name|i
operator|+
literal|" still open"
argument_list|)
expr_stmt|;
name|ret
operator|++
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|class|InstrumentedTaxonomyWriter
specifier|private
class|class
name|InstrumentedTaxonomyWriter
extends|extends
name|DirectoryTaxonomyWriter
block|{
DECL|method|InstrumentedTaxonomyWriter
specifier|public
name|InstrumentedTaxonomyWriter
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openReader
specifier|protected
name|DirectoryReader
name|openReader
parameter_list|()
throws|throws
name|IOException
block|{
name|DirectoryReader
name|r
init|=
name|super
operator|.
name|openReader
argument_list|()
decl_stmt|;
name|readers
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
annotation|@
name|Override
DECL|method|refreshReader
specifier|protected
specifier|synchronized
name|void
name|refreshReader
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|refreshReader
argument_list|()
expr_stmt|;
specifier|final
name|DirectoryReader
name|r
init|=
name|getInternalIndexReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
name|readers
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openIndexWriter
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
operator|new
name|InstrumentedIndexWriter
argument_list|(
name|directory
argument_list|,
name|config
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createIndexWriterConfig
specifier|protected
name|IndexWriterConfig
name|createIndexWriterConfig
parameter_list|(
name|OpenMode
name|openMode
parameter_list|)
block|{
return|return
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
operator|.
name|setOpenMode
argument_list|(
name|openMode
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|InstrumentedTaxonomyReader
specifier|private
class|class
name|InstrumentedTaxonomyReader
extends|extends
name|DirectoryTaxonomyReader
block|{
DECL|method|InstrumentedTaxonomyReader
specifier|public
name|InstrumentedTaxonomyReader
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openIndexReader
specifier|protected
name|DirectoryReader
name|openIndexReader
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|DirectoryReader
name|r
init|=
name|super
operator|.
name|openIndexReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|readers
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
annotation|@
name|Override
DECL|method|refresh
specifier|public
specifier|synchronized
name|boolean
name|refresh
parameter_list|()
throws|throws
name|IOException
throws|,
name|InconsistentTaxonomyException
block|{
specifier|final
name|boolean
name|ret
init|=
name|super
operator|.
name|refresh
argument_list|()
decl_stmt|;
name|readers
operator|.
name|add
argument_list|(
name|getInternalIndexReader
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
DECL|class|InstrumentedIndexWriter
specifier|private
class|class
name|InstrumentedIndexWriter
extends|extends
name|IndexWriter
block|{
DECL|field|mynum
name|int
name|mynum
decl_stmt|;
DECL|method|InstrumentedIndexWriter
specifier|public
name|InstrumentedIndexWriter
parameter_list|(
name|Directory
name|d
parameter_list|,
name|IndexWriterConfig
name|conf
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|d
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|mynum
operator|=
name|iwriter
operator|++
expr_stmt|;
name|openWriters
operator|.
name|add
argument_list|(
name|mynum
argument_list|)
expr_stmt|;
comment|//        System.err.println("openedw "+mynum);
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|openWriters
operator|.
name|contains
argument_list|(
name|mynum
argument_list|)
condition|)
block|{
comment|// probably can't happen...
name|fail
argument_list|(
literal|"Writer #"
operator|+
name|mynum
operator|+
literal|" was closed twice!"
argument_list|)
expr_stmt|;
block|}
name|openWriters
operator|.
name|remove
argument_list|(
name|mynum
argument_list|)
expr_stmt|;
comment|//        System.err.println("closedw "+mynum);
block|}
block|}
block|}
block|}
end_class
end_unit
