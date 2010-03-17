begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|_TestUtil
import|;
end_import
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
begin_class
DECL|class|TestDirectory
specifier|public
class|class
name|TestDirectory
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDetectClose
specifier|public
name|void
name|testDetectClose
parameter_list|()
throws|throws
name|Throwable
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
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
name|AlreadyClosedException
name|ace
parameter_list|)
block|{     }
name|dir
operator|=
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test"
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
name|AlreadyClosedException
name|ace
parameter_list|)
block|{     }
block|}
comment|// Test that different instances of FSDirectory can coexist on the same
comment|// path, can read, write, and lock files.
DECL|method|testDirectInstantiation
specifier|public
name|void
name|testDirectInstantiation
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|path
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|sz
init|=
literal|3
decl_stmt|;
name|Directory
index|[]
name|dirs
init|=
operator|new
name|Directory
index|[
name|sz
index|]
decl_stmt|;
name|dirs
index|[
literal|0
index|]
operator|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dirs
index|[
literal|1
index|]
operator|=
operator|new
name|NIOFSDirectory
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dirs
index|[
literal|2
index|]
operator|=
operator|new
name|MMapDirectory
argument_list|(
name|path
argument_list|,
literal|null
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|Directory
name|dir
init|=
name|dirs
index|[
name|i
index|]
decl_stmt|;
name|dir
operator|.
name|ensureOpen
argument_list|()
expr_stmt|;
name|String
name|fname
init|=
literal|"foo."
operator|+
name|i
decl_stmt|;
name|String
name|lockname
init|=
literal|"foo"
operator|+
name|i
operator|+
literal|".lck"
decl_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fname
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sz
condition|;
name|j
operator|++
control|)
block|{
name|Directory
name|d2
init|=
name|dirs
index|[
name|j
index|]
decl_stmt|;
name|d2
operator|.
name|ensureOpen
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|d2
operator|.
name|fileExists
argument_list|(
name|fname
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|d2
operator|.
name|fileLength
argument_list|(
name|fname
argument_list|)
argument_list|)
expr_stmt|;
comment|// don't test read on MMapDirectory, since it can't really be
comment|// closed and will cause a failure to delete the file.
if|if
condition|(
name|d2
operator|instanceof
name|MMapDirectory
condition|)
continue|continue;
name|IndexInput
name|input
init|=
name|d2
operator|.
name|openInput
argument_list|(
name|fname
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|,
name|input
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// delete with a different dir
name|dirs
index|[
operator|(
name|i
operator|+
literal|1
operator|)
operator|%
name|sz
index|]
operator|.
name|deleteFile
argument_list|(
name|fname
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
operator|<
name|sz
condition|;
name|j
operator|++
control|)
block|{
name|Directory
name|d2
init|=
name|dirs
index|[
name|j
index|]
decl_stmt|;
name|assertFalse
argument_list|(
name|d2
operator|.
name|fileExists
argument_list|(
name|fname
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Lock
name|lock
init|=
name|dir
operator|.
name|makeLock
argument_list|(
name|lockname
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|lock
operator|.
name|obtain
argument_list|()
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
operator|<
name|sz
condition|;
name|j
operator|++
control|)
block|{
name|Directory
name|d2
init|=
name|dirs
index|[
name|j
index|]
decl_stmt|;
name|Lock
name|lock2
init|=
name|d2
operator|.
name|makeLock
argument_list|(
name|lockname
argument_list|)
decl_stmt|;
try|try
block|{
name|assertFalse
argument_list|(
name|lock2
operator|.
name|obtain
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|e
parameter_list|)
block|{
comment|// OK
block|}
block|}
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// now lock with different dir
name|lock
operator|=
name|dirs
index|[
operator|(
name|i
operator|+
literal|1
operator|)
operator|%
name|sz
index|]
operator|.
name|makeLock
argument_list|(
name|lockname
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|lock
operator|.
name|obtain
argument_list|()
argument_list|)
expr_stmt|;
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|Directory
name|dir
init|=
name|dirs
index|[
name|i
index|]
decl_stmt|;
name|dir
operator|.
name|ensureOpen
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|dir
operator|.
name|isOpen
argument_list|)
expr_stmt|;
block|}
block|}
comment|// LUCENE-1464
DECL|method|testDontCreate
specifier|public
name|void
name|testDontCreate
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|path
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|,
literal|"doesnotexist"
argument_list|)
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
operator|!
name|path
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|path
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|// LUCENE-1468
DECL|method|testRAMDirectoryFilter
specifier|public
name|void
name|testRAMDirectoryFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|checkDirectoryFilter
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-1468
DECL|method|testFSDirectoryFilter
specifier|public
name|void
name|testFSDirectoryFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|checkDirectoryFilter
argument_list|(
name|FSDirectory
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-1468
DECL|method|checkDirectoryFilter
specifier|private
name|void
name|checkDirectoryFilter
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|name
init|=
literal|"file"
decl_stmt|;
try|try
block|{
name|dir
operator|.
name|createOutput
argument_list|(
name|name
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|dir
operator|.
name|fileExists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|dir
operator|.
name|listAll
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// LUCENE-1468
DECL|method|testCopySubdir
specifier|public
name|void
name|testCopySubdir
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|path
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|,
literal|"testsubdir"
argument_list|)
decl_stmt|;
try|try
block|{
name|path
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
operator|new
name|File
argument_list|(
name|path
argument_list|,
literal|"subdir"
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|Directory
name|fsDir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|new
name|RAMDirectory
argument_list|(
name|fsDir
argument_list|)
operator|.
name|listAll
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|// LUCENE-1468
DECL|method|testNotDirectory
specifier|public
name|void
name|testNotDirectory
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|path
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|,
literal|"testnotdir"
argument_list|)
decl_stmt|;
name|Directory
name|fsDir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexOutput
name|out
init|=
name|fsDir
operator|.
name|createOutput
argument_list|(
literal|"afile"
argument_list|)
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fsDir
operator|.
name|fileExists
argument_list|(
literal|"afile"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|SimpleFSDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|,
literal|"afile"
argument_list|)
argument_list|,
literal|null
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
name|NoSuchDirectoryException
name|nsde
parameter_list|)
block|{
comment|// Expected
block|}
block|}
finally|finally
block|{
name|fsDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|_TestUtil
operator|.
name|rmDir
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
