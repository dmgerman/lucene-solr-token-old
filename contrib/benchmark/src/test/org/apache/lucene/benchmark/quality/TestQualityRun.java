begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.quality
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|quality
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
name|FileReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|TestPerfTasksLogic
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
name|ReutersDocMaker
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
name|quality
operator|.
name|Judge
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
name|quality
operator|.
name|QualityQuery
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
name|quality
operator|.
name|QualityQueryParser
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
name|quality
operator|.
name|QualityBenchmark
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
name|quality
operator|.
name|trec
operator|.
name|TrecJudge
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
name|quality
operator|.
name|trec
operator|.
name|TrecTopicsReader
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
name|quality
operator|.
name|utils
operator|.
name|SimpleQQParser
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
name|quality
operator|.
name|utils
operator|.
name|SubmissionReport
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
name|IndexSearcher
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
name|FSDirectory
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_comment
comment|/**  * Test that quality run does its job.  */
end_comment
begin_class
DECL|class|TestQualityRun
specifier|public
class|class
name|TestQualityRun
extends|extends
name|TestCase
block|{
DECL|field|DEBUG
specifier|private
specifier|static
name|boolean
name|DEBUG
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"tests.verbose"
argument_list|)
decl_stmt|;
comment|/**    * @param arg0    */
DECL|method|TestQualityRun
specifier|public
name|TestQualityRun
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|testTrecQuality
specifier|public
name|void
name|testTrecQuality
parameter_list|()
throws|throws
name|Exception
block|{
comment|// first create the complete reuters index
name|createReutersIndex
argument_list|()
expr_stmt|;
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"benchmark.work.dir"
argument_list|,
literal|"work"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Bad workDir: "
operator|+
name|workDir
argument_list|,
name|workDir
operator|.
name|exists
argument_list|()
operator|&&
name|workDir
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|maxResults
init|=
literal|1000
decl_stmt|;
name|String
name|docNameField
init|=
literal|"docid"
decl_stmt|;
name|PrintWriter
name|logger
init|=
name|DEBUG
condition|?
operator|new
name|PrintWriter
argument_list|(
name|System
operator|.
name|out
argument_list|,
literal|true
argument_list|)
else|:
literal|null
decl_stmt|;
comment|//<tests src dir> for topics/qrels files - src/test/org/apache/lucene/benchmark/quality
name|File
name|srcTestDir
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|workDir
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|getParentFile
argument_list|()
argument_list|,
literal|"src"
argument_list|)
argument_list|,
literal|"test"
argument_list|)
argument_list|,
literal|"org"
argument_list|)
argument_list|,
literal|"apache"
argument_list|)
argument_list|,
literal|"lucene"
argument_list|)
argument_list|,
literal|"benchmark"
argument_list|)
argument_list|,
literal|"quality"
argument_list|)
decl_stmt|;
comment|// prepare topics
name|File
name|topicsFile
init|=
operator|new
name|File
argument_list|(
name|srcTestDir
argument_list|,
literal|"trecTopics.txt"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Bad topicsFile: "
operator|+
name|topicsFile
argument_list|,
name|topicsFile
operator|.
name|exists
argument_list|()
operator|&&
name|topicsFile
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|TrecTopicsReader
name|qReader
init|=
operator|new
name|TrecTopicsReader
argument_list|()
decl_stmt|;
name|QualityQuery
name|qqs
index|[]
init|=
name|qReader
operator|.
name|readQueries
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|topicsFile
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// prepare judge
name|File
name|qrelsFile
init|=
operator|new
name|File
argument_list|(
name|srcTestDir
argument_list|,
literal|"trecQRels.txt"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Bad qrelsFile: "
operator|+
name|qrelsFile
argument_list|,
name|qrelsFile
operator|.
name|exists
argument_list|()
operator|&&
name|qrelsFile
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|Judge
name|judge
init|=
operator|new
name|TrecJudge
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|qrelsFile
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// validate topics& judgments match each other
name|judge
operator|.
name|validateData
argument_list|(
name|qqs
argument_list|,
name|logger
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"index"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|QualityQueryParser
name|qqParser
init|=
operator|new
name|SimpleQQParser
argument_list|(
literal|"title"
argument_list|,
literal|"body"
argument_list|)
decl_stmt|;
name|QualityBenchmark
name|qrun
init|=
operator|new
name|QualityBenchmark
argument_list|(
name|qqs
argument_list|,
name|qqParser
argument_list|,
name|searcher
argument_list|,
name|docNameField
argument_list|)
decl_stmt|;
name|SubmissionReport
name|submitLog
init|=
name|DEBUG
condition|?
operator|new
name|SubmissionReport
argument_list|(
name|logger
argument_list|)
else|:
literal|null
decl_stmt|;
name|QualityStats
name|stats
index|[]
init|=
name|qrun
operator|.
name|execute
argument_list|(
name|maxResults
argument_list|,
name|judge
argument_list|,
name|submitLog
argument_list|,
name|logger
argument_list|)
decl_stmt|;
comment|// --------- verify by the way judgments were altered for this test:
comment|// for some queries, depending on m = qnum % 8
comment|// m==0: avg_precision and recall are hurt, by marking fake docs as relevant
comment|// m==1: precision_at_n and avg_precision are hurt, by unmarking relevant docs
comment|// m==2: all precision, precision_at_n and recall are hurt.
comment|// m>=3: these queries remain perfect
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|stats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|QualityStats
name|s
init|=
name|stats
index|[
name|i
index|]
decl_stmt|;
switch|switch
condition|(
name|i
operator|%
literal|8
condition|)
block|{
case|case
literal|0
case|:
name|assertTrue
argument_list|(
literal|"avg-p should be hurt: "
operator|+
name|s
operator|.
name|getAvp
argument_list|()
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getAvp
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"recall should be hurt: "
operator|+
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getRecall
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|QualityStats
operator|.
name|MAX_POINTS
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"p_at_"
operator|+
name|j
operator|+
literal|" should be perfect: "
operator|+
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1.0
argument_list|,
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1E
operator|-
literal|9
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|1
case|:
name|assertTrue
argument_list|(
literal|"avg-p should be hurt"
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getAvp
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"recall should be perfect: "
operator|+
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1.0
argument_list|,
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1E
operator|-
literal|9
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|QualityStats
operator|.
name|MAX_POINTS
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"p_at_"
operator|+
name|j
operator|+
literal|" should be hurt: "
operator|+
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|2
case|:
name|assertTrue
argument_list|(
literal|"avg-p should be hurt: "
operator|+
name|s
operator|.
name|getAvp
argument_list|()
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getAvp
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"recall should be hurt: "
operator|+
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getRecall
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|QualityStats
operator|.
name|MAX_POINTS
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"p_at_"
operator|+
name|j
operator|+
literal|" should be hurt: "
operator|+
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1.0
operator|>
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
block|{
name|assertEquals
argument_list|(
literal|"avg-p should be perfect: "
operator|+
name|s
operator|.
name|getAvp
argument_list|()
argument_list|,
literal|1.0
argument_list|,
name|s
operator|.
name|getAvp
argument_list|()
argument_list|,
literal|1E
operator|-
literal|9
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"recall should be perfect: "
operator|+
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1.0
argument_list|,
name|s
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1E
operator|-
literal|9
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|QualityStats
operator|.
name|MAX_POINTS
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"p_at_"
operator|+
name|j
operator|+
literal|" should be perfect: "
operator|+
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1.0
argument_list|,
name|s
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1E
operator|-
literal|9
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|QualityStats
name|avg
init|=
name|QualityStats
operator|.
name|average
argument_list|(
name|stats
argument_list|)
decl_stmt|;
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
block|{
name|avg
operator|.
name|log
argument_list|(
literal|"Average statistis:"
argument_list|,
literal|1
argument_list|,
name|logger
argument_list|,
literal|"  "
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"mean avg-p should be hurt: "
operator|+
name|avg
operator|.
name|getAvp
argument_list|()
argument_list|,
literal|1.0
operator|>
name|avg
operator|.
name|getAvp
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"avg recall should be hurt: "
operator|+
name|avg
operator|.
name|getRecall
argument_list|()
argument_list|,
literal|1.0
operator|>
name|avg
operator|.
name|getRecall
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|QualityStats
operator|.
name|MAX_POINTS
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"avg p_at_"
operator|+
name|j
operator|+
literal|" should be hurt: "
operator|+
name|avg
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|,
literal|1.0
operator|>
name|avg
operator|.
name|getPrecisionAt
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// use benchmark logic to create the full Reuters index
DECL|method|createReutersIndex
specifier|private
name|void
name|createReutersIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 1. alg definition
name|String
name|algLines
index|[]
init|=
block|{
literal|"# ----- properties "
block|,
literal|"doc.maker="
operator|+
name|ReutersDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
literal|"doc.add.log.step=2500"
block|,
literal|"doc.term.vector=false"
block|,
literal|"doc.maker.forever=false"
block|,
literal|"directory=FSDirectory"
block|,
literal|"doc.stored=true"
block|,
literal|"doc.tokenized=true"
block|,
literal|"# ----- alg "
block|,
literal|"ResetSystemErase"
block|,
literal|"CreateIndex"
block|,
literal|"{ AddDoc } : *"
block|,
literal|"CloseIndex"
block|,     }
decl_stmt|;
comment|// 2. execute the algorithm  (required in every "logic" test)
name|TestPerfTasksLogic
operator|.
name|execBenchmark
argument_list|(
name|algLines
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
