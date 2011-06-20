begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  *  Licensed to the Apache Software Foundation (ASF) under one or more  *  contributor license agreements.  See the NOTICE file distributed with  *  this work for additional information regarding copyright ownership.  *  The ASF licenses this file to You under the Apache License, Version 2.0  *  (the "License"); you may not use this file except in compliance with  *  the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  *  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|MockDirectoryWrapper
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
name|_TestUtil
import|;
end_import
begin_comment
comment|/**  * Runs TestNRTThreads in a separate process, crashes the JRE in the middle  * of execution, then runs checkindex to make sure its not corrupt.  */
end_comment
begin_class
DECL|class|TestIndexWriterOnJRECrash
specifier|public
class|class
name|TestIndexWriterOnJRECrash
extends|extends
name|TestNRTThreads
block|{
DECL|field|tempDir
specifier|private
name|File
name|tempDir
decl_stmt|;
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
name|tempDir
operator|=
name|_TestUtil
operator|.
name|getTempDir
argument_list|(
literal|"jrecrash"
argument_list|)
expr_stmt|;
name|tempDir
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tempDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testNRTThreads
specifier|public
name|void
name|testNRTThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|vendor
init|=
name|Constants
operator|.
name|JAVA_VENDOR
decl_stmt|;
name|assumeTrue
argument_list|(
name|vendor
operator|+
literal|" JRE not supported."
argument_list|,
name|vendor
operator|.
name|startsWith
argument_list|(
literal|"Sun"
argument_list|)
operator|||
name|vendor
operator|.
name|startsWith
argument_list|(
literal|"Apple"
argument_list|)
argument_list|)
expr_stmt|;
comment|// if we are not the fork
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.crashmode"
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// try up to 10 times to create an index
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
name|forkTest
argument_list|()
expr_stmt|;
comment|// if we succeeded in finding an index, we are done.
if|if
condition|(
name|checkIndexes
argument_list|(
name|tempDir
argument_list|)
condition|)
return|return;
block|}
block|}
else|else
block|{
comment|// we are the fork, setup a crashing thread
specifier|final
name|int
name|crashTime
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|3000
argument_list|,
literal|4000
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
name|Thread
operator|.
name|sleep
argument_list|(
name|crashTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
name|crashJRE
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|t
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|MAX_PRIORITY
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// run the test until we crash.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|super
operator|.
name|testNRTThreads
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** fork ourselves in a new jvm. sets -Dtests.crashmode=true */
DECL|method|forkTest
specifier|public
name|void
name|forkTest
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|cmd
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|add
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.home"
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
literal|"bin"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
literal|"java"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Xmx512m"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.crashmode=true"
argument_list|)
expr_stmt|;
comment|// passing NIGHTLY to this test makes it run for much longer, easier to catch it in the act...
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.nightly=true"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-DtempDir="
operator|+
name|tempDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-Dtests.seed="
operator|+
name|random
operator|.
name|nextLong
argument_list|()
operator|+
literal|":"
operator|+
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-ea"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-cp"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.class.path"
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"org.junit.runner.JUnitCore"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|pb
operator|.
name|directory
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
name|pb
operator|.
name|redirectErrorStream
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Process
name|p
init|=
name|pb
operator|.
name|start
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
name|p
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|BufferedInputStream
name|isl
init|=
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|">>> Begin subprocess output"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|isl
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"<<< End subprocess output"
argument_list|)
expr_stmt|;
name|p
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
comment|/**    * Recursively looks for indexes underneath<code>file</code>,    * and runs checkindex on them. returns true if it found any indexes.    */
DECL|method|checkIndexes
specifier|public
name|boolean
name|checkIndexes
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newFSDirectory
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|dir
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// don't double-checkindex
if|if
condition|(
name|IndexReader
operator|.
name|indexExists
argument_list|(
name|dir
argument_list|)
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Checking index: "
operator|+
name|file
argument_list|)
expr_stmt|;
block|}
name|_TestUtil
operator|.
name|checkIndex
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|File
name|f
range|:
name|file
operator|.
name|listFiles
argument_list|()
control|)
if|if
condition|(
name|checkIndexes
argument_list|(
name|f
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**    * currently, this only works/tested on Sun and IBM.    */
DECL|method|crashJRE
specifier|public
name|void
name|crashJRE
parameter_list|()
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"sun.misc.Unsafe"
argument_list|)
decl_stmt|;
comment|// we should use getUnsafe instead, harmony implements it, etc.
name|Field
name|field
init|=
name|clazz
operator|.
name|getDeclaredField
argument_list|(
literal|"theUnsafe"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|field
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Method
name|m
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"putAddress"
argument_list|,
name|long
operator|.
name|class
argument_list|,
name|long
operator|.
name|class
argument_list|)
decl_stmt|;
name|m
operator|.
name|invoke
argument_list|(
name|o
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
