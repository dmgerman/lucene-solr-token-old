begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
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
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|benchmark
operator|.
name|BenchmarkTestCase
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|Version
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
begin_comment
comment|/** Tests the functionality of {@link AddIndexesTask}. */
end_comment
begin_class
DECL|class|AddIndexesTaskTest
specifier|public
class|class
name|AddIndexesTaskTest
extends|extends
name|BenchmarkTestCase
block|{
DECL|field|testDir
DECL|field|inputDir
specifier|private
specifier|static
name|File
name|testDir
decl_stmt|,
name|inputDir
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClassAddIndexesTaskTest
specifier|public
specifier|static
name|void
name|beforeClassAddIndexesTaskTest
parameter_list|()
throws|throws
name|Exception
block|{
name|testDir
operator|=
name|createTempDir
argument_list|(
literal|"addIndexesTask"
argument_list|)
expr_stmt|;
comment|// create a dummy index under inputDir
name|inputDir
operator|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"input"
argument_list|)
expr_stmt|;
name|Directory
name|tmpDir
init|=
name|newFSDirectory
argument_list|(
name|inputDir
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|tmpDir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|tmpDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createPerfRunData
specifier|private
name|PerfRunData
name|createPerfRunData
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"writer.version"
argument_list|,
name|Version
operator|.
name|LATEST
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"print.props"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// don't print anything
name|props
operator|.
name|setProperty
argument_list|(
literal|"directory"
argument_list|,
literal|"RAMDirectory"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|AddIndexesTask
operator|.
name|ADDINDEXES_INPUT_DIR
argument_list|,
name|inputDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|Config
name|config
init|=
operator|new
name|Config
argument_list|(
name|props
argument_list|)
decl_stmt|;
return|return
operator|new
name|PerfRunData
argument_list|(
name|config
argument_list|)
return|;
block|}
DECL|method|assertIndex
specifier|private
name|void
name|assertIndex
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|taskDir
init|=
name|runData
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|RAMDirectory
operator|.
name|class
argument_list|,
name|taskDir
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|taskDir
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testAddIndexesDefault
specifier|public
name|void
name|testAddIndexesDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|()
decl_stmt|;
comment|// create the target index first
operator|new
name|CreateIndexTask
argument_list|(
name|runData
argument_list|)
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|AddIndexesTask
name|task
init|=
operator|new
name|AddIndexesTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|task
operator|.
name|setup
argument_list|()
expr_stmt|;
comment|// add the input index
name|task
operator|.
name|doLogic
argument_list|()
expr_stmt|;
comment|// close the index
operator|new
name|CloseIndexTask
argument_list|(
name|runData
argument_list|)
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|assertIndex
argument_list|(
name|runData
argument_list|)
expr_stmt|;
name|runData
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testAddIndexesDir
specifier|public
name|void
name|testAddIndexesDir
parameter_list|()
throws|throws
name|Exception
block|{
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|()
decl_stmt|;
comment|// create the target index first
operator|new
name|CreateIndexTask
argument_list|(
name|runData
argument_list|)
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|AddIndexesTask
name|task
init|=
operator|new
name|AddIndexesTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|task
operator|.
name|setup
argument_list|()
expr_stmt|;
comment|// add the input index
name|task
operator|.
name|setParams
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
name|task
operator|.
name|doLogic
argument_list|()
expr_stmt|;
comment|// close the index
operator|new
name|CloseIndexTask
argument_list|(
name|runData
argument_list|)
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|assertIndex
argument_list|(
name|runData
argument_list|)
expr_stmt|;
name|runData
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testAddIndexesReader
specifier|public
name|void
name|testAddIndexesReader
parameter_list|()
throws|throws
name|Exception
block|{
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|()
decl_stmt|;
comment|// create the target index first
operator|new
name|CreateIndexTask
argument_list|(
name|runData
argument_list|)
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|AddIndexesTask
name|task
init|=
operator|new
name|AddIndexesTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|task
operator|.
name|setup
argument_list|()
expr_stmt|;
comment|// add the input index
name|task
operator|.
name|setParams
argument_list|(
literal|"false"
argument_list|)
expr_stmt|;
name|task
operator|.
name|doLogic
argument_list|()
expr_stmt|;
comment|// close the index
operator|new
name|CloseIndexTask
argument_list|(
name|runData
argument_list|)
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|assertIndex
argument_list|(
name|runData
argument_list|)
expr_stmt|;
name|runData
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
