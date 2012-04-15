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
name|java
operator|.
name|io
operator|.
name|IOException
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
begin_class
DECL|class|TestCopyBytes
specifier|public
class|class
name|TestCopyBytes
extends|extends
name|LuceneTestCase
block|{
DECL|method|value
specifier|private
name|byte
name|value
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
return|return
call|(
name|byte
call|)
argument_list|(
operator|(
name|idx
operator|%
literal|256
operator|)
operator|*
operator|(
literal|1
operator|+
operator|(
name|idx
operator|/
literal|256
operator|)
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testCopyBytes
specifier|public
name|void
name|testCopyBytes
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|num
condition|;
name|iter
operator|++
control|)
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
literal|"TEST: iter="
operator|+
name|iter
operator|+
literal|" dir="
operator|+
name|dir
argument_list|)
expr_stmt|;
block|}
comment|// make random file
name|IndexOutput
name|out
init|=
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
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|77777
argument_list|)
index|]
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1777777
argument_list|)
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
name|int
name|byteUpto
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|upto
operator|<
name|size
condition|)
block|{
name|bytes
index|[
name|byteUpto
operator|++
index|]
operator|=
name|value
argument_list|(
name|upto
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
if|if
condition|(
name|byteUpto
operator|==
name|bytes
operator|.
name|length
condition|)
block|{
name|out
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|byteUpto
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|out
operator|.
name|writeBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|byteUpto
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|out
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|dir
operator|.
name|fileLength
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
comment|// copy from test -> test2
specifier|final
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"test2"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|upto
operator|<
name|size
condition|)
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
name|out
operator|.
name|writeByte
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|chunk
init|=
name|Math
operator|.
name|min
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|,
name|size
operator|-
name|upto
argument_list|)
decl_stmt|;
name|out
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|chunk
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|size
argument_list|,
name|upto
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// verify
name|IndexInput
name|in2
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"test2"
argument_list|,
name|newIOContext
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|upto
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|upto
operator|<
name|size
condition|)
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
specifier|final
name|byte
name|v
init|=
name|in2
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|(
name|upto
argument_list|)
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|limit
init|=
name|Math
operator|.
name|min
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
argument_list|,
name|size
operator|-
name|upto
argument_list|)
decl_stmt|;
name|in2
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|limit
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|byteIdx
init|=
literal|0
init|;
name|byteIdx
operator|<
name|limit
condition|;
name|byteIdx
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|value
argument_list|(
name|upto
argument_list|)
argument_list|,
name|bytes
index|[
name|byteIdx
index|]
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
block|}
block|}
name|in2
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|deleteFile
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|dir
operator|.
name|deleteFile
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// LUCENE-3541
DECL|method|testCopyBytesWithThreads
specifier|public
name|void
name|testCopyBytesWithThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|datalen
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|101
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
name|datalen
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Directory
name|d
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|output
init|=
name|d
operator|.
name|createOutput
argument_list|(
literal|"data"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|datalen
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|d
operator|.
name|openInput
argument_list|(
literal|"data"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|IndexOutput
name|outputHeader
init|=
name|d
operator|.
name|createOutput
argument_list|(
literal|"header"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
comment|// copy our 100-byte header
name|input
operator|.
name|copyBytes
argument_list|(
name|outputHeader
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|outputHeader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now make N copies of the remaining bytes
name|CopyThread
name|copies
index|[]
init|=
operator|new
name|CopyThread
index|[
literal|10
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
name|copies
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|copies
index|[
name|i
index|]
operator|=
operator|new
name|CopyThread
argument_list|(
operator|(
name|IndexInput
operator|)
name|input
operator|.
name|clone
argument_list|()
argument_list|,
name|d
operator|.
name|createOutput
argument_list|(
literal|"copy"
operator|+
name|i
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
argument_list|)
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
name|copies
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|copies
index|[
name|i
index|]
operator|.
name|start
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
name|copies
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|copies
index|[
name|i
index|]
operator|.
name|join
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
name|copies
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexInput
name|copiedData
init|=
name|d
operator|.
name|openInput
argument_list|(
literal|"copy"
operator|+
name|i
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|byte
index|[]
name|dataCopy
init|=
operator|new
name|byte
index|[
name|datalen
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|dataCopy
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// copy the header for easy testing
name|copiedData
operator|.
name|readBytes
argument_list|(
name|dataCopy
argument_list|,
literal|100
argument_list|,
name|datalen
operator|-
literal|100
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|dataCopy
argument_list|)
expr_stmt|;
name|copiedData
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|CopyThread
specifier|static
class|class
name|CopyThread
extends|extends
name|Thread
block|{
DECL|field|src
specifier|final
name|IndexInput
name|src
decl_stmt|;
DECL|field|dst
specifier|final
name|IndexOutput
name|dst
decl_stmt|;
DECL|method|CopyThread
name|CopyThread
parameter_list|(
name|IndexInput
name|src
parameter_list|,
name|IndexOutput
name|dst
parameter_list|)
block|{
name|this
operator|.
name|src
operator|=
name|src
expr_stmt|;
name|this
operator|.
name|dst
operator|=
name|dst
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|src
operator|.
name|copyBytes
argument_list|(
name|dst
argument_list|,
name|src
operator|.
name|length
argument_list|()
operator|-
literal|100
argument_list|)
expr_stmt|;
name|dst
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
block|}
block|}
block|}
end_class
end_unit
