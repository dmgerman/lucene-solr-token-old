begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|TokenStream
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
name|ConcurrentMergeScheduler
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_comment
comment|/** Base class for all Lucene unit tests.  Currently the  *  only added functionality over JUnit's TestCase is  *  asserting that no unhandled exceptions occurred in  *  threads launched by ConcurrentMergeScheduler.  If you  *  override either<code>setUp()</code> or  *<code>tearDown()</code> in your unit test, make sure you  *  call<code>super.setUp()</code> and  *<code>super.tearDown()</code>.  */
end_comment
begin_class
DECL|class|LuceneTestCase
specifier|public
specifier|abstract
class|class
name|LuceneTestCase
extends|extends
name|TestCase
block|{
DECL|method|LuceneTestCase
specifier|public
name|LuceneTestCase
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|LuceneTestCase
specifier|public
name|LuceneTestCase
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
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ConcurrentMergeScheduler
operator|.
name|setTestMode
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|ConcurrentMergeScheduler
operator|.
name|anyUnhandledExceptions
argument_list|()
condition|)
block|{
comment|// Clear the failure so that we don't just keep
comment|// failing subsequent test cases
name|ConcurrentMergeScheduler
operator|.
name|clearUnhandledExceptions
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"ConcurrentMergeScheduler hit unhandled exceptions"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns a {@link Random} instance for generating random numbers during the test.    * The random seed is logged during test execution and printed to System.out on any failure    * for reproducing the test using {@link #newRandom(long)} with the recorded seed    *.    */
DECL|method|newRandom
specifier|public
name|Random
name|newRandom
parameter_list|()
block|{
if|if
condition|(
name|seed
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"please call LuceneTestCase.newRandom only once per test"
argument_list|)
throw|;
block|}
return|return
name|newRandom
argument_list|(
name|seedRnd
operator|.
name|nextLong
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns a {@link Random} instance for generating random numbers during the test.    * If an error occurs in the test that is not reproducible, you can use this method to    * initialize the number generator with the seed that was printed out during the failing test.    */
DECL|method|newRandom
specifier|public
name|Random
name|newRandom
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|seed
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"please call LuceneTestCase.newRandom only once per test"
argument_list|)
throw|;
block|}
name|this
operator|.
name|seed
operator|=
operator|new
name|Long
argument_list|(
name|seed
argument_list|)
expr_stmt|;
return|return
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
return|;
block|}
DECL|method|runTest
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|seed
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|runTest
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|seed
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NOTE: random seed of testcase '"
operator|+
name|getName
argument_list|()
operator|+
literal|"' was: "
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
comment|// recorded seed
DECL|field|seed
specifier|protected
name|Long
name|seed
init|=
literal|null
decl_stmt|;
comment|// static members
DECL|field|seedRnd
specifier|private
specifier|static
specifier|final
name|Random
name|seedRnd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
block|}
end_class
end_unit
