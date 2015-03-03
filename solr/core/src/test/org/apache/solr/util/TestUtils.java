begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|Callable
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
name|ExecutionException
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
name|ExecutorService
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
name|Executors
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
name|Future
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|ExecutorUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|StrUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|TestUtils
specifier|public
class|class
name|TestUtils
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testJoin
specifier|public
name|void
name|testJoin
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"a|b|c"
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
literal|'|'
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a,b,c"
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\\,b,c"
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a,b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a,b|c"
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a,b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
literal|'|'
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\\\\b|c"
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a\\b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
literal|'|'
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEscapeTextWithSeparator
specifier|public
name|void
name|testEscapeTextWithSeparator
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|StrUtils
operator|.
name|escapeTextWithSeparator
argument_list|(
literal|"a"
argument_list|,
literal|'|'
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|StrUtils
operator|.
name|escapeTextWithSeparator
argument_list|(
literal|"a"
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\\|b"
argument_list|,
name|StrUtils
operator|.
name|escapeTextWithSeparator
argument_list|(
literal|"a|b"
argument_list|,
literal|'|'
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a|b"
argument_list|,
name|StrUtils
operator|.
name|escapeTextWithSeparator
argument_list|(
literal|"a|b"
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a,b"
argument_list|,
name|StrUtils
operator|.
name|escapeTextWithSeparator
argument_list|(
literal|"a,b"
argument_list|,
literal|'|'
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\\,b"
argument_list|,
name|StrUtils
operator|.
name|escapeTextWithSeparator
argument_list|(
literal|"a,b"
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\\\\b"
argument_list|,
name|StrUtils
operator|.
name|escapeTextWithSeparator
argument_list|(
literal|"a\\b"
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a\\\\\\,b"
argument_list|,
name|StrUtils
operator|.
name|escapeTextWithSeparator
argument_list|(
literal|"a\\,b"
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSplitEscaping
specifier|public
name|void
name|testSplitEscaping
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|arr
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
literal|"\\r\\n:\\t\\f\\b"
argument_list|,
literal|":"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|arr
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\r\n"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\t\f\b"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|arr
operator|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
literal|"\\r\\n:\\t\\f\\b"
argument_list|,
literal|":"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|arr
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\\r\\n"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\\t\\f\\b"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|arr
operator|=
name|StrUtils
operator|.
name|splitWS
argument_list|(
literal|"\\r\\n \\t\\f\\b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|arr
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\r\n"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\t\f\b"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|arr
operator|=
name|StrUtils
operator|.
name|splitWS
argument_list|(
literal|"\\r\\n \\t\\f\\b"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|arr
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\\r\\n"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\\t\\f\\b"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|arr
operator|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
literal|"\\:foo\\::\\:bar\\:"
argument_list|,
literal|":"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|arr
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|":foo:"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|":bar:"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|arr
operator|=
name|StrUtils
operator|.
name|splitWS
argument_list|(
literal|"\\ foo\\  \\ bar\\ "
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|arr
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|" foo "
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|" bar "
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|arr
operator|=
name|StrUtils
operator|.
name|splitFileNames
argument_list|(
literal|"/h/s,/h/\\,s,"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|arr
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/h/s"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/h/,s"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|arr
operator|=
name|StrUtils
operator|.
name|splitFileNames
argument_list|(
literal|"/h/s"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|arr
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/h/s"
argument_list|,
name|arr
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNamedLists
specifier|public
name|void
name|testNamedLists
parameter_list|()
block|{
name|SimpleOrderedMap
argument_list|<
name|Integer
argument_list|>
name|map
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"test"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
argument_list|<
name|Integer
argument_list|>
name|clone
init|=
name|map
operator|.
name|clone
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|map
operator|.
name|toString
argument_list|()
argument_list|,
name|clone
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|10
argument_list|)
argument_list|,
name|clone
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|realMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|realMap
operator|.
name|put
argument_list|(
literal|"one"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|realMap
operator|.
name|put
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|realMap
operator|.
name|put
argument_list|(
literal|"three"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|map
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
expr_stmt|;
name|map
operator|.
name|addAll
argument_list|(
name|realMap
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"one"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"three"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"one"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one"
argument_list|,
name|map
operator|.
name|getName
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|setName
argument_list|(
literal|0
argument_list|,
literal|"ONE"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ONE"
argument_list|,
name|map
operator|.
name|getName
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|100
argument_list|)
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"one"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|map
operator|.
name|indexOf
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"one"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"two"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|iter
init|=
name|map
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|v
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|v
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// coverage
name|v
operator|.
name|setValue
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
operator|*
literal|10
argument_list|)
expr_stmt|;
try|try
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"should be unsupported..."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ignored
parameter_list|)
block|{}
block|}
comment|// the values should be bigger
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|10
argument_list|)
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|20
argument_list|)
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNumberUtils
specifier|public
name|void
name|testNumberUtils
parameter_list|()
block|{
name|double
name|number
init|=
literal|1.234
decl_stmt|;
name|String
name|sortable
init|=
name|NumberUtils
operator|.
name|double2sortableStr
argument_list|(
name|number
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|number
argument_list|,
name|NumberUtils
operator|.
name|SortableStr2double
argument_list|(
name|sortable
argument_list|)
argument_list|,
literal|0.001
argument_list|)
expr_stmt|;
name|long
name|num
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|sortable
operator|=
name|NumberUtils
operator|.
name|long2sortableStr
argument_list|(
name|num
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|num
argument_list|,
name|NumberUtils
operator|.
name|SortableStr2long
argument_list|(
name|sortable
argument_list|,
literal|0
argument_list|,
name|sortable
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|num
argument_list|)
argument_list|,
name|NumberUtils
operator|.
name|SortableStr2long
argument_list|(
name|sortable
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNanoTimeSpeed
specifier|public
name|void
name|testNanoTimeSpeed
parameter_list|()
block|{
specifier|final
name|int
name|maxNumThreads
init|=
literal|100
decl_stmt|;
specifier|final
name|int
name|numIters
init|=
literal|1000
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|log
operator|.
name|info
argument_list|(
literal|"testNanoTime: maxNumThreads = {}, numIters = {}"
argument_list|,
name|maxNumThreads
argument_list|,
name|numIters
argument_list|)
expr_stmt|;
specifier|final
name|ExecutorService
name|workers
init|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"nanoTimeTestThread"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|numThreads
init|=
literal|1
init|;
name|numThreads
operator|<=
name|maxNumThreads
condition|;
name|numThreads
operator|++
control|)
block|{
name|List
argument_list|<
name|Callable
argument_list|<
name|Long
argument_list|>
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|tasks
operator|.
name|add
argument_list|(
operator|new
name|Callable
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Long
name|call
parameter_list|()
block|{
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
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
name|numIters
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
return|return
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|List
argument_list|<
name|Future
argument_list|<
name|Long
argument_list|>
argument_list|>
name|results
init|=
name|workers
operator|.
name|invokeAll
argument_list|(
name|tasks
argument_list|)
decl_stmt|;
name|long
name|totalTime
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Future
argument_list|<
name|Long
argument_list|>
name|res
range|:
name|results
control|)
block|{
name|totalTime
operator|+=
name|res
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|long
name|timePerIter
init|=
name|totalTime
operator|/
operator|(
name|numIters
operator|*
name|numThreads
operator|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Time taken for System.nanoTime is too high"
argument_list|,
name|timePerIter
operator|<
literal|10000
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|log
operator|.
name|info
argument_list|(
literal|"numThreads = {}, time_per_call = {}ns"
argument_list|,
name|numThreads
argument_list|,
name|timePerIter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|ExecutionException
name|ignored
parameter_list|)
block|{}
block|}
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|workers
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
