begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
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
name|FileOutputStream
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
name|io
operator|.
name|StringReader
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
name|Benchmark
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
name|LuceneTestCase
operator|.
name|SuppressSysoutChecks
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
comment|/** Base class for all Benchmark unit tests. */
end_comment
begin_class
annotation|@
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"very noisy"
argument_list|)
DECL|class|BenchmarkTestCase
specifier|public
specifier|abstract
class|class
name|BenchmarkTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|field|WORKDIR
specifier|private
specifier|static
name|File
name|WORKDIR
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClassBenchmarkTestCase
specifier|public
specifier|static
name|void
name|beforeClassBenchmarkTestCase
parameter_list|()
block|{
name|WORKDIR
operator|=
name|createTempDir
argument_list|(
literal|"benchmark"
argument_list|)
expr_stmt|;
name|WORKDIR
operator|.
name|delete
argument_list|()
expr_stmt|;
name|WORKDIR
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClassBenchmarkTestCase
specifier|public
specifier|static
name|void
name|afterClassBenchmarkTestCase
parameter_list|()
block|{
name|WORKDIR
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getWorkDir
specifier|public
name|File
name|getWorkDir
parameter_list|()
block|{
return|return
name|WORKDIR
return|;
block|}
comment|/** Copy a resource into the workdir */
DECL|method|copyToWorkDir
specifier|public
name|void
name|copyToWorkDir
parameter_list|(
name|String
name|resourceName
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|resource
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
name|OutputStream
name|dest
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
name|resourceName
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|8192
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|resource
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|dest
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
name|resource
operator|.
name|close
argument_list|()
expr_stmt|;
name|dest
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Return a path, suitable for a .alg config file, for a resource in the workdir */
DECL|method|getWorkDirResourcePath
specifier|public
name|String
name|getWorkDirResourcePath
parameter_list|(
name|String
name|resourceName
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
name|resourceName
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"/"
argument_list|)
return|;
block|}
comment|/** Return a path, suitable for a .alg config file, for the workdir */
DECL|method|getWorkDirPath
specifier|public
name|String
name|getWorkDirPath
parameter_list|()
block|{
return|return
name|getWorkDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"/"
argument_list|)
return|;
block|}
comment|// create the benchmark and execute it.
DECL|method|execBenchmark
specifier|public
name|Benchmark
name|execBenchmark
parameter_list|(
name|String
index|[]
name|algLines
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|algText
init|=
name|algLinesToText
argument_list|(
name|algLines
argument_list|)
decl_stmt|;
name|logTstLogic
argument_list|(
name|algText
argument_list|)
expr_stmt|;
name|Benchmark
name|benchmark
init|=
operator|new
name|Benchmark
argument_list|(
operator|new
name|StringReader
argument_list|(
name|algText
argument_list|)
argument_list|)
decl_stmt|;
name|benchmark
operator|.
name|execute
argument_list|()
expr_stmt|;
return|return
name|benchmark
return|;
block|}
comment|// properties in effect in all tests here
DECL|field|propLines
specifier|final
name|String
name|propLines
index|[]
init|=
block|{
literal|"work.dir="
operator|+
name|getWorkDirPath
argument_list|()
block|,
literal|"directory=RAMDirectory"
block|,
literal|"print.props=false"
block|,   }
decl_stmt|;
DECL|field|NEW_LINE
specifier|static
specifier|final
name|String
name|NEW_LINE
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
comment|// catenate alg lines to make the alg text
DECL|method|algLinesToText
specifier|private
name|String
name|algLinesToText
parameter_list|(
name|String
index|[]
name|algLines
parameter_list|)
block|{
name|String
name|indent
init|=
literal|"  "
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|propLines
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|indent
argument_list|)
operator|.
name|append
argument_list|(
name|propLines
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
name|NEW_LINE
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
name|algLines
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|indent
argument_list|)
operator|.
name|append
argument_list|(
name|algLines
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
name|NEW_LINE
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|logTstLogic
specifier|private
specifier|static
name|void
name|logTstLogic
parameter_list|(
name|String
name|txt
parameter_list|)
block|{
if|if
condition|(
operator|!
name|VERBOSE
condition|)
return|return;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Test logic of:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|txt
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
