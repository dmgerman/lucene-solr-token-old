begin_unit
begin_package
DECL|package|org.apache.lucene.util.junitcompat
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|junitcompat
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
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|Constants
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
name|util
operator|.
name|TestUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|junit
operator|.
name|runner
operator|.
name|JUnitCore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Result
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
import|;
end_import
begin_class
DECL|class|TestLeaveFilesIfTestFails
specifier|public
class|class
name|TestLeaveFilesIfTestFails
extends|extends
name|WithNestedTests
block|{
DECL|method|TestLeaveFilesIfTestFails
specifier|public
name|TestLeaveFilesIfTestFails
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|Nested1
specifier|public
specifier|static
class|class
name|Nested1
extends|extends
name|WithNestedTests
operator|.
name|AbstractNestedTest
block|{
DECL|field|file
specifier|static
name|File
name|file
decl_stmt|;
DECL|method|testDummy
specifier|public
name|void
name|testDummy
parameter_list|()
block|{
name|file
operator|=
name|createTempDir
argument_list|(
literal|"leftover"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLeaveFilesIfTestFails
specifier|public
name|void
name|testLeaveFilesIfTestFails
parameter_list|()
block|{
name|Result
name|r
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Nested1
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Nested1
operator|.
name|file
operator|!=
literal|null
operator|&&
name|Nested1
operator|.
name|file
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Nested1
operator|.
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
DECL|class|Nested2
specifier|public
specifier|static
class|class
name|Nested2
extends|extends
name|WithNestedTests
operator|.
name|AbstractNestedTest
block|{
DECL|field|file
specifier|static
name|File
name|file
decl_stmt|;
DECL|field|parent
specifier|static
name|File
name|parent
decl_stmt|;
DECL|field|openFile
specifier|static
name|RandomAccessFile
name|openFile
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testDummy
specifier|public
name|void
name|testDummy
parameter_list|()
throws|throws
name|Exception
block|{
name|file
operator|=
operator|new
name|File
argument_list|(
name|createTempDir
argument_list|(
literal|"leftover"
argument_list|)
argument_list|,
literal|"child.locked"
argument_list|)
expr_stmt|;
name|openFile
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|file
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|parent
operator|=
name|LuceneTestCase
operator|.
name|getBaseTempDirForTestClass
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWindowsUnremovableFile
specifier|public
name|void
name|testWindowsUnremovableFile
parameter_list|()
throws|throws
name|IOException
block|{
name|RandomizedTest
operator|.
name|assumeTrue
argument_list|(
literal|"Requires Windows."
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|RandomizedTest
operator|.
name|assumeFalse
argument_list|(
name|LuceneTestCase
operator|.
name|LEAVE_TEMPORARY
argument_list|)
expr_stmt|;
name|Result
name|r
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Nested2
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|Nested2
operator|.
name|openFile
operator|.
name|close
argument_list|()
expr_stmt|;
name|TestUtil
operator|.
name|rm
argument_list|(
name|Nested2
operator|.
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
