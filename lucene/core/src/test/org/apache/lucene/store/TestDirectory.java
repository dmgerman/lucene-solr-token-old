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
name|FileNotFoundException
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
name|nio
operator|.
name|file
operator|.
name|NoSuchFileException
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
name|MockDirectoryWrapper
operator|.
name|Throttling
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
name|File
name|tempDir
init|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
name|LuceneTestCase
operator|.
name|getTestClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
name|Directory
index|[]
name|dirs
init|=
operator|new
name|Directory
index|[]
block|{
operator|new
name|RAMDirectory
argument_list|()
block|,
operator|new
name|SimpleFSDirectory
argument_list|(
name|tempDir
argument_list|)
block|,
operator|new
name|NIOFSDirectory
argument_list|(
name|tempDir
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|Directory
name|dir
range|:
name|dirs
control|)
block|{
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
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
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
block|{       }
block|}
block|}
comment|// test is occasionally very slow, i dont know why
comment|// try this seed: 7D7E036AD12927F5:93333EF9E6DE44DE
annotation|@
name|Nightly
DECL|method|testThreadSafety
specifier|public
name|void
name|testThreadSafety
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BaseDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we arent making an index
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setThrottling
argument_list|(
name|Throttling
operator|.
name|NEVER
argument_list|)
expr_stmt|;
comment|// makes this test really slow
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
class|class
name|TheThread
extends|extends
name|Thread
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|public
name|TheThread
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3000
condition|;
name|i
operator|++
control|)
block|{
name|String
name|fileName
init|=
name|this
operator|.
name|name
operator|+
name|i
decl_stmt|;
try|try
block|{
comment|//System.out.println("create:" + fileName);
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
empty_stmt|;
class|class
name|TheThread2
extends|extends
name|Thread
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|public
name|TheThread2
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
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
try|try
block|{
name|String
index|[]
name|files
init|=
name|dir
operator|.
name|listAll
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
comment|//System.out.println("file:" + file);
try|try
block|{
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|file
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
decl||
name|NoSuchFileException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"still open for writing"
argument_list|)
condition|)
block|{
comment|// ignore
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
empty_stmt|;
name|TheThread
name|theThread
init|=
operator|new
name|TheThread
argument_list|(
literal|"t1"
argument_list|)
decl_stmt|;
name|TheThread2
name|theThread2
init|=
operator|new
name|TheThread2
argument_list|(
literal|"t2"
argument_list|)
decl_stmt|;
name|theThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|theThread2
operator|.
name|start
argument_list|()
expr_stmt|;
name|theThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|theThread2
operator|.
name|join
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
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
specifier|final
name|File
name|path
init|=
name|TestUtil
operator|.
name|createTempDir
argument_list|(
literal|"testDirectInstantiation"
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|largeBuffer
init|=
operator|new
name|byte
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
operator|*
literal|1024
argument_list|)
index|]
decl_stmt|,
name|largeReadBuffer
init|=
operator|new
name|byte
index|[
name|largeBuffer
operator|.
name|length
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
name|largeBuffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|largeBuffer
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
comment|// automatically loops with modulo
block|}
specifier|final
name|FSDirectory
index|[]
name|dirs
init|=
operator|new
name|FSDirectory
index|[]
block|{
operator|new
name|SimpleFSDirectory
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|NIOFSDirectory
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|MMapDirectory
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
block|}
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
name|dirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|FSDirectory
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
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
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
name|writeBytes
argument_list|(
name|largeBuffer
argument_list|,
name|largeBuffer
operator|.
name|length
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
name|dirs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|FSDirectory
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
name|slowFileExists
argument_list|(
name|d2
argument_list|,
name|fname
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
operator|+
name|largeBuffer
operator|.
name|length
argument_list|,
name|d2
operator|.
name|fileLength
argument_list|(
name|fname
argument_list|)
argument_list|)
expr_stmt|;
comment|// don't do read tests if unmapping is not supported!
if|if
condition|(
name|d2
operator|instanceof
name|MMapDirectory
operator|&&
operator|!
operator|(
operator|(
name|MMapDirectory
operator|)
name|d2
operator|)
operator|.
name|getUseUnmap
argument_list|()
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
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
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
comment|// read array with buffering enabled
name|Arrays
operator|.
name|fill
argument_list|(
name|largeReadBuffer
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|largeReadBuffer
argument_list|,
literal|0
argument_list|,
name|largeReadBuffer
operator|.
name|length
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|largeBuffer
argument_list|,
name|largeReadBuffer
argument_list|)
expr_stmt|;
comment|// read again without using buffer
name|input
operator|.
name|seek
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|largeReadBuffer
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|largeReadBuffer
argument_list|,
literal|0
argument_list|,
name|largeReadBuffer
operator|.
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|largeBuffer
argument_list|,
name|largeReadBuffer
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
name|dirs
operator|.
name|length
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
name|dirs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|FSDirectory
name|d2
init|=
name|dirs
index|[
name|j
index|]
decl_stmt|;
name|assertFalse
argument_list|(
name|slowFileExists
argument_list|(
name|d2
argument_list|,
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
name|dirs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|FSDirectory
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
name|close
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
name|dirs
operator|.
name|length
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
name|close
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
name|dirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|FSDirectory
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
name|TestUtil
operator|.
name|rm
argument_list|(
name|path
argument_list|)
expr_stmt|;
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
name|TestUtil
operator|.
name|createTempDir
argument_list|(
name|LuceneTestCase
operator|.
name|getTestClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
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
name|TestUtil
operator|.
name|rm
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
name|newFSDirectory
argument_list|(
name|TestUtil
operator|.
name|createTempDir
argument_list|(
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
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|dir
argument_list|,
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
name|TestUtil
operator|.
name|createTempDir
argument_list|(
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
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
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
name|TestUtil
operator|.
name|rm
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
name|TestUtil
operator|.
name|createTempDir
argument_list|(
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
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|fsDir
argument_list|,
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
name|TestUtil
operator|.
name|rm
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
