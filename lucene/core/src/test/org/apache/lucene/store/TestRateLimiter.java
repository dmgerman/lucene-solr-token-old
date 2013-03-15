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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|RateLimiter
operator|.
name|SimpleRateLimiter
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
begin_comment
comment|/**  * Simple testcase for RateLimiter.SimpleRateLimiter  */
end_comment
begin_class
DECL|class|TestRateLimiter
specifier|public
specifier|final
class|class
name|TestRateLimiter
extends|extends
name|LuceneTestCase
block|{
DECL|method|testPause
specifier|public
name|void
name|testPause
parameter_list|()
block|{
name|SimpleRateLimiter
name|limiter
init|=
operator|new
name|SimpleRateLimiter
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|// 10 MB / Sec
name|limiter
operator|.
name|pause
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|//init
name|long
name|pause
init|=
literal|0
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|pause
operator|+=
name|limiter
operator|.
name|pause
argument_list|(
literal|4
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// fire up 3 * 4 MB
block|}
specifier|final
name|long
name|convert
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|pause
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"we should sleep less than 2 seconds but did: "
operator|+
name|convert
operator|+
literal|" millis"
argument_list|,
name|convert
operator|<
literal|2000l
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"we should sleep at least 1 second but did only: "
operator|+
name|convert
operator|+
literal|" millis"
argument_list|,
name|convert
operator|>
literal|1000l
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
