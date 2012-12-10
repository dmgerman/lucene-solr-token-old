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
name|BufferedReader
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
name|FileInputStream
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
name|InputStreamReader
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|feeds
operator|.
name|DocMaker
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
name|document
operator|.
name|Field
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
name|StringField
import|;
end_import
begin_comment
comment|/** Tests the functionality of {@link WriteEnwikiLineDocTask}. */
end_comment
begin_class
DECL|class|WriteEnwikiLineDocTaskTest
specifier|public
class|class
name|WriteEnwikiLineDocTaskTest
extends|extends
name|BenchmarkTestCase
block|{
comment|// class has to be public so that Class.forName.newInstance() will work
comment|/** Interleaves category docs with regular docs */
DECL|class|WriteLineCategoryDocMaker
specifier|public
specifier|static
specifier|final
class|class
name|WriteLineCategoryDocMaker
extends|extends
name|DocMaker
block|{
DECL|field|flip
name|AtomicInteger
name|flip
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|isCategory
init|=
operator|(
name|flip
operator|.
name|incrementAndGet
argument_list|()
operator|%
literal|2
operator|==
literal|0
operator|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|BODY_FIELD
argument_list|,
literal|"body text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|TITLE_FIELD
argument_list|,
name|isCategory
condition|?
literal|"Category:title text"
else|:
literal|"title text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|DATE_FIELD
argument_list|,
literal|"date text"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
DECL|method|createPerfRunData
specifier|private
name|PerfRunData
name|createPerfRunData
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|docMakerName
parameter_list|)
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
literal|"doc.maker"
argument_list|,
name|docMakerName
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"line.file.out"
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"directory"
argument_list|,
literal|"RAMDirectory"
argument_list|)
expr_stmt|;
comment|// no accidental FS dir.
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
DECL|method|doReadTest
specifier|private
name|void
name|doReadTest
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|expTitle
parameter_list|,
name|String
name|expDate
parameter_list|,
name|String
name|expBody
parameter_list|)
throws|throws
name|Exception
block|{
name|doReadTest
argument_list|(
literal|2
argument_list|,
name|file
argument_list|,
name|expTitle
argument_list|,
name|expDate
argument_list|,
name|expBody
argument_list|)
expr_stmt|;
name|File
name|categoriesFile
init|=
name|WriteEnwikiLineDocTask
operator|.
name|categoriesLineFile
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|doReadTest
argument_list|(
literal|2
argument_list|,
name|categoriesFile
argument_list|,
literal|"Category:"
operator|+
name|expTitle
argument_list|,
name|expDate
argument_list|,
name|expBody
argument_list|)
expr_stmt|;
block|}
DECL|method|doReadTest
specifier|private
name|void
name|doReadTest
parameter_list|(
name|int
name|n
parameter_list|,
name|File
name|file
parameter_list|,
name|String
name|expTitle
parameter_list|,
name|String
name|expDate
parameter_list|,
name|String
name|expBody
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
literal|"utf-8"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|WriteLineDocTaskTest
operator|.
name|assertHeaderLine
argument_list|(
name|line
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|String
index|[]
name|parts
init|=
name|line
operator|.
name|split
argument_list|(
name|Character
operator|.
name|toString
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numExpParts
init|=
name|expBody
operator|==
literal|null
condition|?
literal|2
else|:
literal|3
decl_stmt|;
name|assertEquals
argument_list|(
name|numExpParts
argument_list|,
name|parts
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expTitle
argument_list|,
name|parts
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expDate
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|expBody
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|expBody
argument_list|,
name|parts
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNull
argument_list|(
name|br
operator|.
name|readLine
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testCategoryLines
specifier|public
name|void
name|testCategoryLines
parameter_list|()
throws|throws
name|Exception
block|{
comment|// WriteLineDocTask replaced only \t characters w/ a space, since that's its
comment|// separator char. However, it didn't replace newline characters, which
comment|// resulted in errors in LineDocSource.
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
literal|"two-lines-each.txt"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|file
argument_list|,
name|WriteLineCategoryDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|WriteLineDocTask
name|wldt
init|=
operator|new
name|WriteEnwikiLineDocTask
argument_list|(
name|runData
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
comment|// four times so that each file should have 2 lines.
name|wldt
operator|.
name|doLogic
argument_list|()
expr_stmt|;
block|}
name|wldt
operator|.
name|close
argument_list|()
expr_stmt|;
name|doReadTest
argument_list|(
name|file
argument_list|,
literal|"title text"
argument_list|,
literal|"date text"
argument_list|,
literal|"body text"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
