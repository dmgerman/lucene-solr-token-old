begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|IndexWriter
operator|.
name|MaxFieldLength
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
name|queryParser
operator|.
name|QueryParser
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
name|BitSet
import|;
end_import
begin_comment
comment|/**  * Tests the TimeLimitedCollector.  This test checks (1) search  * correctness (regardless of timeout), (2) expected timeout behavior,  * and (3) a sanity test with multiple searching threads.  */
end_comment
begin_class
DECL|class|TestTimeLimitedCollector
specifier|public
class|class
name|TestTimeLimitedCollector
extends|extends
name|LuceneTestCase
block|{
DECL|field|SLOW_DOWN
specifier|private
specifier|static
specifier|final
name|int
name|SLOW_DOWN
init|=
literal|47
decl_stmt|;
DECL|field|TIME_ALLOWED
specifier|private
specifier|static
specifier|final
name|long
name|TIME_ALLOWED
init|=
literal|17
operator|*
name|SLOW_DOWN
decl_stmt|;
comment|// so searches can find about 17 docs.
comment|// max time allowed is relaxed for multithreading tests.
comment|// the multithread case fails when setting this to 1 (no slack) and launching many threads (>2000).
comment|// but this is not a real failure, just noise.
DECL|field|MULTI_THREAD_SLACK
specifier|private
specifier|static
specifier|final
name|double
name|MULTI_THREAD_SLACK
init|=
literal|7
decl_stmt|;
DECL|field|N_DOCS
specifier|private
specifier|static
specifier|final
name|int
name|N_DOCS
init|=
literal|3000
decl_stmt|;
DECL|field|N_THREADS
specifier|private
specifier|static
specifier|final
name|int
name|N_THREADS
init|=
literal|50
decl_stmt|;
DECL|field|searcher
specifier|private
name|Searcher
name|searcher
decl_stmt|;
DECL|field|FIELD_NAME
specifier|private
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"body"
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|method|TestTimeLimitedCollector
specifier|public
name|TestTimeLimitedCollector
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
comment|/**    * initializes searcher with a document set    */
DECL|method|setUp
specifier|protected
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
specifier|final
name|String
name|docText
index|[]
init|=
block|{
literal|"docThatNeverMatchesSoWeCanRequireLastDocCollectedToBeGreaterThanZero"
block|,
literal|"one blah three"
block|,
literal|"one foo three multiOne"
block|,
literal|"one foobar three multiThree"
block|,
literal|"blueberry pancakes"
block|,
literal|"blueberry pie"
block|,
literal|"blueberry strudel"
block|,
literal|"blueberry pizza"
block|,     }
decl_stmt|;
name|Directory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|MaxFieldLength
operator|.
name|UNLIMITED
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
name|N_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|docText
index|[
name|i
operator|%
name|docText
operator|.
name|length
index|]
argument_list|,
name|iw
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|String
name|qtxt
init|=
literal|"one"
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
name|docText
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|qtxt
operator|+=
literal|' '
operator|+
name|docText
index|[
name|i
index|]
expr_stmt|;
comment|// large query so that search will be longer
block|}
name|QueryParser
name|queryParser
init|=
operator|new
name|QueryParser
argument_list|(
name|FIELD_NAME
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|query
operator|=
name|queryParser
operator|.
name|parse
argument_list|(
name|qtxt
argument_list|)
expr_stmt|;
comment|// warm the searcher
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|add
specifier|private
name|void
name|add
parameter_list|(
name|String
name|value
parameter_list|,
name|IndexWriter
name|iw
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD_NAME
argument_list|,
name|value
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
DECL|method|search
specifier|private
name|void
name|search
parameter_list|(
name|HitCollector
name|collector
parameter_list|)
throws|throws
name|Exception
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
comment|/**    * test search correctness with no timeout    */
DECL|method|testSearch
specifier|public
name|void
name|testSearch
parameter_list|()
block|{
name|doTestSearch
argument_list|()
expr_stmt|;
block|}
DECL|method|doTestSearch
specifier|private
name|void
name|doTestSearch
parameter_list|()
block|{
name|int
name|totalResults
init|=
literal|0
decl_stmt|;
name|int
name|totalTLCResults
init|=
literal|0
decl_stmt|;
try|try
block|{
name|MyHitCollector
name|myHc
init|=
operator|new
name|MyHitCollector
argument_list|()
decl_stmt|;
name|search
argument_list|(
name|myHc
argument_list|)
expr_stmt|;
name|totalResults
operator|=
name|myHc
operator|.
name|hitCount
argument_list|()
expr_stmt|;
name|myHc
operator|=
operator|new
name|MyHitCollector
argument_list|()
expr_stmt|;
name|long
name|oneHour
init|=
literal|3600000
decl_stmt|;
name|HitCollector
name|tlCollector
init|=
name|createTimedCollector
argument_list|(
name|myHc
argument_list|,
name|oneHour
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|search
argument_list|(
name|tlCollector
argument_list|)
expr_stmt|;
name|totalTLCResults
operator|=
name|myHc
operator|.
name|hitCount
argument_list|()
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
name|assertTrue
argument_list|(
literal|"Unexpected exception: "
operator|+
name|e
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//==fail
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of results!"
argument_list|,
name|totalResults
argument_list|,
name|totalTLCResults
argument_list|)
expr_stmt|;
block|}
DECL|method|createTimedCollector
specifier|private
name|HitCollector
name|createTimedCollector
parameter_list|(
name|MyHitCollector
name|hc
parameter_list|,
name|long
name|timeAllowed
parameter_list|,
name|boolean
name|greedy
parameter_list|)
block|{
name|TimeLimitedCollector
name|res
init|=
operator|new
name|TimeLimitedCollector
argument_list|(
name|hc
argument_list|,
name|timeAllowed
argument_list|)
decl_stmt|;
name|res
operator|.
name|setGreedy
argument_list|(
name|greedy
argument_list|)
expr_stmt|;
comment|// set to true to make sure at least one doc is collected.
return|return
name|res
return|;
block|}
comment|/**    * Test that timeout is obtained, and soon enough!    */
DECL|method|testTimeoutGreedy
specifier|public
name|void
name|testTimeoutGreedy
parameter_list|()
block|{
name|doTestTimeout
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that timeout is obtained, and soon enough!    */
DECL|method|testTimeoutNotGreedy
specifier|public
name|void
name|testTimeoutNotGreedy
parameter_list|()
block|{
name|doTestTimeout
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestTimeout
specifier|private
name|void
name|doTestTimeout
parameter_list|(
name|boolean
name|multiThreaded
parameter_list|,
name|boolean
name|greedy
parameter_list|)
block|{
comment|// setup
name|MyHitCollector
name|myHc
init|=
operator|new
name|MyHitCollector
argument_list|()
decl_stmt|;
name|myHc
operator|.
name|setSlowDown
argument_list|(
name|SLOW_DOWN
argument_list|)
expr_stmt|;
name|HitCollector
name|tlCollector
init|=
name|createTimedCollector
argument_list|(
name|myHc
argument_list|,
name|TIME_ALLOWED
argument_list|,
name|greedy
argument_list|)
decl_stmt|;
comment|// search
name|TimeLimitedCollector
operator|.
name|TimeExceededException
name|timoutException
init|=
literal|null
decl_stmt|;
try|try
block|{
name|search
argument_list|(
name|tlCollector
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeLimitedCollector
operator|.
name|TimeExceededException
name|x
parameter_list|)
block|{
name|timoutException
operator|=
name|x
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Unexpected exception: "
operator|+
name|e
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//==fail
block|}
comment|// must get exception
name|assertNotNull
argument_list|(
literal|"Timeout expected!"
argument_list|,
name|timoutException
argument_list|)
expr_stmt|;
comment|// greediness affect last doc collected
name|int
name|exceptionDoc
init|=
name|timoutException
operator|.
name|getLastDocCollected
argument_list|()
decl_stmt|;
name|int
name|lastCollected
init|=
name|myHc
operator|.
name|getLastDocCollected
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"doc collected at timeout must be> 0!"
argument_list|,
name|exceptionDoc
operator|>
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|greedy
condition|)
block|{
name|assertTrue
argument_list|(
literal|"greedy="
operator|+
name|greedy
operator|+
literal|" exceptionDoc="
operator|+
name|exceptionDoc
operator|+
literal|" != lastCollected="
operator|+
name|lastCollected
argument_list|,
name|exceptionDoc
operator|==
name|lastCollected
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"greedy, but no hits found!"
argument_list|,
name|myHc
operator|.
name|hitCount
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"greedy="
operator|+
name|greedy
operator|+
literal|" exceptionDoc="
operator|+
name|exceptionDoc
operator|+
literal|" not> lastCollected="
operator|+
name|lastCollected
argument_list|,
name|exceptionDoc
operator|>
name|lastCollected
argument_list|)
expr_stmt|;
block|}
comment|// verify that elapsed time at exception is within valid limits
name|assertEquals
argument_list|(
name|timoutException
operator|.
name|getTimeAllowed
argument_list|()
argument_list|,
name|TIME_ALLOWED
argument_list|)
expr_stmt|;
comment|// a) Not too early
name|assertTrue
argument_list|(
literal|"elapsed="
operator|+
name|timoutException
operator|.
name|getTimeElapsed
argument_list|()
operator|+
literal|"<= (allowed-resolution)="
operator|+
operator|(
name|TIME_ALLOWED
operator|-
name|TimeLimitedCollector
operator|.
name|getResolution
argument_list|()
operator|)
argument_list|,
name|timoutException
operator|.
name|getTimeElapsed
argument_list|()
operator|>
name|TIME_ALLOWED
operator|-
name|TimeLimitedCollector
operator|.
name|getResolution
argument_list|()
argument_list|)
expr_stmt|;
comment|// b) Not too late.
comment|//    This part is problematic in a busy test system, so we just print a warning.
comment|//    We already verified that a timeout occurred, we just can't be picky about how long it took.
if|if
condition|(
name|timoutException
operator|.
name|getTimeElapsed
argument_list|()
operator|>
name|maxTime
argument_list|(
name|multiThreaded
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Informative: timeout exceeded (no action required: most probably just "
operator|+
literal|" because the test machine is slower than usual):  "
operator|+
literal|"lastDoc="
operator|+
name|exceptionDoc
operator|+
literal|" ,&& allowed="
operator|+
name|timoutException
operator|.
name|getTimeAllowed
argument_list|()
operator|+
literal|" ,&& elapsed="
operator|+
name|timoutException
operator|.
name|getTimeElapsed
argument_list|()
operator|+
literal|">= "
operator|+
name|maxTimeStr
argument_list|(
name|multiThreaded
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|maxTime
specifier|private
name|long
name|maxTime
parameter_list|(
name|boolean
name|multiThreaded
parameter_list|)
block|{
name|long
name|res
init|=
literal|2
operator|*
name|TimeLimitedCollector
operator|.
name|getResolution
argument_list|()
operator|+
name|TIME_ALLOWED
operator|+
name|SLOW_DOWN
decl_stmt|;
comment|// some slack for less noise in this test
if|if
condition|(
name|multiThreaded
condition|)
block|{
name|res
operator|*=
name|MULTI_THREAD_SLACK
expr_stmt|;
comment|// larger slack
block|}
return|return
name|res
return|;
block|}
DECL|method|maxTimeStr
specifier|private
name|String
name|maxTimeStr
parameter_list|(
name|boolean
name|multiThreaded
parameter_list|)
block|{
name|String
name|s
init|=
literal|"( "
operator|+
literal|"2*resolution +  TIME_ALLOWED + SLOW_DOWN = "
operator|+
literal|"2*"
operator|+
name|TimeLimitedCollector
operator|.
name|getResolution
argument_list|()
operator|+
literal|" + "
operator|+
name|TIME_ALLOWED
operator|+
literal|" + "
operator|+
name|SLOW_DOWN
operator|+
literal|")"
decl_stmt|;
if|if
condition|(
name|multiThreaded
condition|)
block|{
name|s
operator|=
name|MULTI_THREAD_SLACK
operator|+
literal|" * "
operator|+
name|s
expr_stmt|;
block|}
return|return
name|maxTime
argument_list|(
name|multiThreaded
argument_list|)
operator|+
literal|" = "
operator|+
name|s
return|;
block|}
comment|/**    * Test timeout behavior when resolution is modified.     */
DECL|method|testModifyResolution
specifier|public
name|void
name|testModifyResolution
parameter_list|()
block|{
try|try
block|{
comment|// increase and test
name|long
name|resolution
init|=
literal|20
operator|*
name|TimeLimitedCollector
operator|.
name|DEFAULT_RESOLUTION
decl_stmt|;
comment|//400
name|TimeLimitedCollector
operator|.
name|setResolution
argument_list|(
name|resolution
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|resolution
argument_list|,
name|TimeLimitedCollector
operator|.
name|getResolution
argument_list|()
argument_list|)
expr_stmt|;
name|doTestTimeout
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// decrease much and test
name|resolution
operator|=
literal|5
expr_stmt|;
name|TimeLimitedCollector
operator|.
name|setResolution
argument_list|(
name|resolution
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|resolution
argument_list|,
name|TimeLimitedCollector
operator|.
name|getResolution
argument_list|()
argument_list|)
expr_stmt|;
name|doTestTimeout
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// return to default and test
name|resolution
operator|=
name|TimeLimitedCollector
operator|.
name|DEFAULT_RESOLUTION
expr_stmt|;
name|TimeLimitedCollector
operator|.
name|setResolution
argument_list|(
name|resolution
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|resolution
argument_list|,
name|TimeLimitedCollector
operator|.
name|getResolution
argument_list|()
argument_list|)
expr_stmt|;
name|doTestTimeout
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TimeLimitedCollector
operator|.
name|setResolution
argument_list|(
name|TimeLimitedCollector
operator|.
name|DEFAULT_RESOLUTION
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Test correctness with multiple searching threads.    */
DECL|method|testSearchMultiThreaded
specifier|public
name|void
name|testSearchMultiThreaded
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMultiThreads
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**     * Test correctness with multiple searching threads.    */
DECL|method|testTimeoutMultiThreaded
specifier|public
name|void
name|testTimeoutMultiThreaded
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestMultiThreads
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestMultiThreads
specifier|private
name|void
name|doTestMultiThreads
parameter_list|(
specifier|final
name|boolean
name|withTimeout
parameter_list|)
throws|throws
name|Exception
block|{
name|Thread
index|[]
name|threadArray
init|=
operator|new
name|Thread
index|[
name|N_THREADS
index|]
decl_stmt|;
specifier|final
name|BitSet
name|success
init|=
operator|new
name|BitSet
argument_list|(
name|N_THREADS
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
name|threadArray
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|num
init|=
name|i
decl_stmt|;
name|threadArray
index|[
name|num
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|withTimeout
condition|)
block|{
name|doTestTimeout
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doTestSearch
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|success
init|)
block|{
name|success
operator|.
name|set
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|threadArray
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|threadArray
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
name|threadArray
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|threadArray
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"some threads failed!"
argument_list|,
name|N_THREADS
argument_list|,
name|success
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// counting hit collector that can slow down at collect().
DECL|class|MyHitCollector
specifier|private
class|class
name|MyHitCollector
extends|extends
name|HitCollector
block|{
DECL|field|bits
specifier|private
specifier|final
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
DECL|field|slowdown
specifier|private
name|int
name|slowdown
init|=
literal|0
decl_stmt|;
DECL|field|lastDocCollected
specifier|private
name|int
name|lastDocCollected
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * amount of time to wait on each collect to simulate a long iteration      */
DECL|method|setSlowDown
specifier|public
name|void
name|setSlowDown
parameter_list|(
name|int
name|milliseconds
parameter_list|)
block|{
name|slowdown
operator|=
name|milliseconds
expr_stmt|;
block|}
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
specifier|final
name|int
name|docId
parameter_list|,
specifier|final
name|float
name|score
parameter_list|)
block|{
if|if
condition|(
name|slowdown
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|slowdown
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
assert|assert
name|docId
operator|>=
literal|0
operator|:
literal|" doc="
operator|+
name|docId
assert|;
name|bits
operator|.
name|set
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|lastDocCollected
operator|=
name|docId
expr_stmt|;
block|}
DECL|method|hitCount
specifier|public
name|int
name|hitCount
parameter_list|()
block|{
return|return
name|bits
operator|.
name|cardinality
argument_list|()
return|;
block|}
DECL|method|getLastDocCollected
specifier|public
name|int
name|getLastDocCollected
parameter_list|()
block|{
return|return
name|lastDocCollected
return|;
block|}
block|}
block|}
end_class
end_unit
