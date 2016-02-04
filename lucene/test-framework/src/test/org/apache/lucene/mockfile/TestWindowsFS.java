begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
package|;
end_package
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Exception
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|InterruptedException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|NoSuchFieldException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|RuntimeException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|FileSystem
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
name|Files
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
name|nio
operator|.
name|file
operator|.
name|Path
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
name|StandardCopyOption
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CyclicBarrier
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|mockfile
operator|.
name|FilterPath
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
name|mockfile
operator|.
name|WindowsFS
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
begin_comment
comment|/** Basic tests for WindowsFS */
end_comment
begin_class
DECL|class|TestWindowsFS
specifier|public
class|class
name|TestWindowsFS
extends|extends
name|MockFileSystemTestCase
block|{
comment|// currently we don't emulate windows well enough to work on windows!
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|assumeFalse
argument_list|(
literal|"windows is not supported"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|wrap
specifier|protected
name|Path
name|wrap
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|FileSystem
name|fs
init|=
operator|new
name|WindowsFS
argument_list|(
name|path
operator|.
name|getFileSystem
argument_list|()
argument_list|)
operator|.
name|getFileSystem
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"file:///"
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterPath
argument_list|(
name|path
argument_list|,
name|fs
argument_list|)
return|;
block|}
comment|/** Test Files.delete fails if a file has an open inputstream against it */
DECL|method|testDeleteOpenFile
specifier|public
name|void
name|testDeleteOpenFile
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|wrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|OutputStream
name|file
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"access denied"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test Files.deleteIfExists fails if a file has an open inputstream against it */
DECL|method|testDeleteIfExistsOpenFile
specifier|public
name|void
name|testDeleteIfExistsOpenFile
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|wrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|OutputStream
name|file
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"access denied"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test Files.rename fails if a file has an open inputstream against it */
comment|// TODO: what does windows do here?
DECL|method|testRenameOpenFile
specifier|public
name|void
name|testRenameOpenFile
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|dir
init|=
name|wrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|OutputStream
name|file
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
name|file
operator|.
name|write
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
name|dir
operator|.
name|resolve
argument_list|(
literal|"stillopen"
argument_list|)
argument_list|,
name|dir
operator|.
name|resolve
argument_list|(
literal|"target"
argument_list|)
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"access denied"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testOpenDeleteConcurrently
specifier|public
name|void
name|testOpenDeleteConcurrently
parameter_list|()
throws|throws
name|IOException
throws|,
name|Exception
block|{
specifier|final
name|Path
name|dir
init|=
name|wrap
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|file
init|=
name|dir
operator|.
name|resolve
argument_list|(
literal|"thefile"
argument_list|)
decl_stmt|;
specifier|final
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|stopped
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
while|while
condition|(
name|stopped
operator|.
name|get
argument_list|()
operator|==
literal|false
condition|)
block|{
try|try
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|Files
operator|.
name|delete
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Path
name|target
init|=
name|file
operator|.
name|resolveSibling
argument_list|(
literal|"other"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|move
argument_list|(
name|file
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|// continue
block|}
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|int
name|iters
init|=
literal|10
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|opened
init|=
literal|false
decl_stmt|;
try|try
init|(
name|OutputStream
name|stream
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|file
argument_list|)
init|)
block|{
name|opened
operator|=
literal|true
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// just create
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
decl||
name|NoSuchFileException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"File handle leaked - file is closed but still registered"
argument_list|,
literal|0
argument_list|,
operator|(
operator|(
name|WindowsFS
operator|)
name|dir
operator|.
name|getFileSystem
argument_list|()
operator|.
name|provider
argument_list|()
operator|)
operator|.
name|openFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"caught FNF on close"
argument_list|,
name|opened
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"File handle leaked - file is closed but still registered"
argument_list|,
literal|0
argument_list|,
operator|(
operator|(
name|WindowsFS
operator|)
name|dir
operator|.
name|getFileSystem
argument_list|()
operator|.
name|provider
argument_list|()
operator|)
operator|.
name|openFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|stopped
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
