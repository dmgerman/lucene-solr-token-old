begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.benchmark.byTask
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
package|;
end_package
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
name|FileFilter
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
name|FileReader
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
name|InputStreamReader
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|AbstractQueryMaker
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
name|ContentSource
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
name|DocData
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
name|NoMoreDataException
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
name|tasks
operator|.
name|PerfTask
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
name|tasks
operator|.
name|TaskSequence
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
name|Algorithm
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
name|search
operator|.
name|Query
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
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|conf
operator|.
name|ConfLoader
import|;
end_import
begin_comment
comment|/** Test very simply that perf tasks are parses as expected. */
end_comment
begin_class
DECL|class|TestPerfTasksParse
specifier|public
class|class
name|TestPerfTasksParse
extends|extends
name|LuceneTestCase
block|{
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
DECL|field|INDENT
specifier|static
specifier|final
name|String
name|INDENT
init|=
literal|"  "
decl_stmt|;
comment|// properties in effect in all tests here
DECL|field|propPart
specifier|static
specifier|final
name|String
name|propPart
init|=
name|INDENT
operator|+
literal|"directory=RAMDirectory"
operator|+
name|NEW_LINE
operator|+
name|INDENT
operator|+
literal|"print.props=false"
operator|+
name|NEW_LINE
decl_stmt|;
comment|/** Test the repetiotion parsing for parallel tasks */
DECL|method|testParseParallelTaskSequenceRepetition
specifier|public
name|void
name|testParseParallelTaskSequenceRepetition
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|taskStr
init|=
literal|"AddDoc"
decl_stmt|;
name|String
name|parsedTasks
init|=
literal|"[ "
operator|+
name|taskStr
operator|+
literal|" ] : 1000"
decl_stmt|;
name|Benchmark
name|benchmark
init|=
operator|new
name|Benchmark
argument_list|(
operator|new
name|StringReader
argument_list|(
name|propPart
operator|+
name|parsedTasks
argument_list|)
argument_list|)
decl_stmt|;
name|Algorithm
name|alg
init|=
name|benchmark
operator|.
name|getAlgorithm
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|PerfTask
argument_list|>
name|algTasks
init|=
name|alg
operator|.
name|extractTasks
argument_list|()
decl_stmt|;
name|boolean
name|foundAdd
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|PerfTask
name|task
range|:
name|algTasks
control|)
block|{
if|if
condition|(
name|task
operator|.
name|toString
argument_list|()
operator|.
name|indexOf
argument_list|(
name|taskStr
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|foundAdd
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|task
operator|instanceof
name|TaskSequence
condition|)
block|{
name|assertEquals
argument_list|(
literal|"repetions should be 1000 for "
operator|+
name|parsedTasks
argument_list|,
literal|1000
argument_list|,
operator|(
operator|(
name|TaskSequence
operator|)
name|task
operator|)
operator|.
name|getRepetitions
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sequence for "
operator|+
name|parsedTasks
operator|+
literal|" should be parallel!"
argument_list|,
operator|(
operator|(
name|TaskSequence
operator|)
name|task
operator|)
operator|.
name|isParallel
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Task "
operator|+
name|taskStr
operator|+
literal|" was not found in "
operator|+
name|alg
operator|.
name|toString
argument_list|()
argument_list|,
name|foundAdd
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test the repetiotion parsing for sequential  tasks */
DECL|method|testParseTaskSequenceRepetition
specifier|public
name|void
name|testParseTaskSequenceRepetition
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|taskStr
init|=
literal|"AddDoc"
decl_stmt|;
name|String
name|parsedTasks
init|=
literal|"{ "
operator|+
name|taskStr
operator|+
literal|" } : 1000"
decl_stmt|;
name|Benchmark
name|benchmark
init|=
operator|new
name|Benchmark
argument_list|(
operator|new
name|StringReader
argument_list|(
name|propPart
operator|+
name|parsedTasks
argument_list|)
argument_list|)
decl_stmt|;
name|Algorithm
name|alg
init|=
name|benchmark
operator|.
name|getAlgorithm
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|PerfTask
argument_list|>
name|algTasks
init|=
name|alg
operator|.
name|extractTasks
argument_list|()
decl_stmt|;
name|boolean
name|foundAdd
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|PerfTask
name|task
range|:
name|algTasks
control|)
block|{
if|if
condition|(
name|task
operator|.
name|toString
argument_list|()
operator|.
name|indexOf
argument_list|(
name|taskStr
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|foundAdd
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|task
operator|instanceof
name|TaskSequence
condition|)
block|{
name|assertEquals
argument_list|(
literal|"repetions should be 1000 for "
operator|+
name|parsedTasks
argument_list|,
literal|1000
argument_list|,
operator|(
operator|(
name|TaskSequence
operator|)
name|task
operator|)
operator|.
name|getRepetitions
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"sequence for "
operator|+
name|parsedTasks
operator|+
literal|" should be sequential!"
argument_list|,
operator|(
operator|(
name|TaskSequence
operator|)
name|task
operator|)
operator|.
name|isParallel
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Task "
operator|+
name|taskStr
operator|+
literal|" was not found in "
operator|+
name|alg
operator|.
name|toString
argument_list|()
argument_list|,
name|foundAdd
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MockContentSource
specifier|public
specifier|static
class|class
name|MockContentSource
extends|extends
name|ContentSource
block|{
annotation|@
name|Override
DECL|method|getNextDocData
specifier|public
name|DocData
name|getNextDocData
parameter_list|(
name|DocData
name|docData
parameter_list|)
throws|throws
name|NoMoreDataException
throws|,
name|IOException
block|{
return|return
name|docData
return|;
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
block|{ }
block|}
DECL|class|MockQueryMaker
specifier|public
specifier|static
class|class
name|MockQueryMaker
extends|extends
name|AbstractQueryMaker
block|{
annotation|@
name|Override
DECL|method|prepareQueries
specifier|protected
name|Query
index|[]
name|prepareQueries
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|Query
index|[
literal|0
index|]
return|;
block|}
block|}
comment|/** Test the parsing of example scripts **/
DECL|method|testParseExamples
specifier|public
name|void
name|testParseExamples
parameter_list|()
throws|throws
name|Exception
block|{
comment|// hackedy-hack-hack
name|boolean
name|foundFiles
init|=
literal|false
decl_stmt|;
specifier|final
name|File
name|examplesDir
init|=
operator|new
name|File
argument_list|(
name|ConfLoader
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"."
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|algFile
range|:
name|examplesDir
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|pathname
parameter_list|)
block|{
return|return
name|pathname
operator|.
name|isFile
argument_list|()
operator|&&
name|pathname
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".alg"
argument_list|)
return|;
block|}
block|}
argument_list|)
control|)
block|{
try|try
block|{
name|Config
name|config
init|=
operator|new
name|Config
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|algFile
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|contentSource
init|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentSource
operator|!=
literal|null
condition|)
block|{
name|Class
operator|.
name|forName
argument_list|(
name|contentSource
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|set
argument_list|(
literal|"work.dir"
argument_list|,
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"work"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
literal|"content.source"
argument_list|,
name|MockContentSource
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|dir
init|=
name|config
operator|.
name|get
argument_list|(
literal|"content.source"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
name|Class
operator|.
name|forName
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|set
argument_list|(
literal|"directory"
argument_list|,
name|RAMDirectory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
literal|"line.file.out"
argument_list|,
literal|null
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|set
argument_list|(
literal|"line.file.out"
argument_list|,
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
literal|"o.txt"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|get
argument_list|(
literal|"query.maker"
argument_list|,
literal|null
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|Class
operator|.
name|forName
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"query.maker"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
literal|"query.maker"
argument_list|,
name|MockQueryMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PerfRunData
name|data
init|=
operator|new
name|PerfRunData
argument_list|(
name|config
argument_list|)
decl_stmt|;
operator|new
name|Algorithm
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Could not parse sample file: "
operator|+
name|algFile
operator|+
literal|" reason:"
operator|+
name|t
operator|.
name|getClass
argument_list|()
operator|+
literal|":"
operator|+
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|foundFiles
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|foundFiles
condition|)
block|{
name|fail
argument_list|(
literal|"could not find any .alg files!"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
