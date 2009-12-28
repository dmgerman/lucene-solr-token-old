begin_unit
begin_package
DECL|package|org.apache.lucene.util.cache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|cache
package|;
end_package
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment
begin_class
DECL|class|TestDoubleBarrelLRUCache
specifier|public
class|class
name|TestDoubleBarrelLRUCache
extends|extends
name|BaseTestLRU
block|{
DECL|method|testLRUCache
specifier|public
name|void
name|testLRUCache
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|n
init|=
literal|100
decl_stmt|;
name|testCache
argument_list|(
operator|new
name|DoubleBarrelLRUCache
argument_list|<
name|Integer
argument_list|,
name|Object
argument_list|>
argument_list|(
name|n
argument_list|)
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
DECL|class|CacheThread
specifier|private
class|class
name|CacheThread
extends|extends
name|Thread
block|{
DECL|field|objs
specifier|private
specifier|final
name|Object
index|[]
name|objs
decl_stmt|;
DECL|field|c
specifier|private
specifier|final
name|Cache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|c
decl_stmt|;
DECL|field|endTime
specifier|private
specifier|final
name|long
name|endTime
decl_stmt|;
DECL|field|failed
specifier|volatile
name|boolean
name|failed
decl_stmt|;
DECL|method|CacheThread
specifier|public
name|CacheThread
parameter_list|(
name|Cache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|c
parameter_list|,
name|Object
index|[]
name|objs
parameter_list|,
name|long
name|endTime
parameter_list|)
block|{
name|this
operator|.
name|c
operator|=
name|c
expr_stmt|;
name|this
operator|.
name|objs
operator|=
name|objs
expr_stmt|;
name|this
operator|.
name|endTime
operator|=
name|endTime
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
name|long
name|count
init|=
literal|0
decl_stmt|;
name|long
name|miss
init|=
literal|0
decl_stmt|;
name|long
name|hit
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|objs
operator|.
name|length
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|Object
name|obj
init|=
name|objs
index|[
call|(
name|int
call|)
argument_list|(
operator|(
name|count
operator|/
literal|2
operator|)
operator|%
name|limit
argument_list|)
index|]
decl_stmt|;
name|Object
name|v
init|=
name|c
operator|.
name|get
argument_list|(
name|obj
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|c
operator|.
name|put
argument_list|(
name|obj
argument_list|,
name|obj
argument_list|)
expr_stmt|;
name|miss
operator|++
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|obj
operator|==
name|v
assert|;
name|hit
operator|++
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|++
name|count
operator|%
literal|10000
operator|)
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>=
name|endTime
condition|)
block|{
break|break;
block|}
block|}
block|}
name|addResults
argument_list|(
name|miss
argument_list|,
name|hit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|totMiss
DECL|field|totHit
name|long
name|totMiss
decl_stmt|,
name|totHit
decl_stmt|;
DECL|method|addResults
name|void
name|addResults
parameter_list|(
name|long
name|miss
parameter_list|,
name|long
name|hit
parameter_list|)
block|{
name|totMiss
operator|+=
name|miss
expr_stmt|;
name|totHit
operator|+=
name|hit
expr_stmt|;
block|}
DECL|method|testThreadCorrectness
specifier|public
name|void
name|testThreadCorrectness
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUM_THREADS
init|=
literal|4
decl_stmt|;
specifier|final
name|int
name|CACHE_SIZE
init|=
literal|512
decl_stmt|;
specifier|final
name|int
name|OBJ_COUNT
init|=
literal|3
operator|*
name|CACHE_SIZE
decl_stmt|;
name|Cache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|c
init|=
operator|new
name|DoubleBarrelLRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|Object
index|[]
name|objs
init|=
operator|new
name|Object
index|[
name|OBJ_COUNT
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
name|OBJ_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|objs
index|[
name|i
index|]
operator|=
operator|new
name|Object
argument_list|()
expr_stmt|;
block|}
specifier|final
name|CacheThread
index|[]
name|threads
init|=
operator|new
name|CacheThread
index|[
name|NUM_THREADS
index|]
decl_stmt|;
specifier|final
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
operator|(
operator|(
name|long
operator|)
literal|1000
operator|)
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|CacheThread
argument_list|(
name|c
argument_list|,
name|objs
argument_list|,
name|endTime
argument_list|)
expr_stmt|;
name|threads
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
assert|assert
operator|!
name|threads
index|[
name|i
index|]
operator|.
name|failed
assert|;
block|}
comment|//System.out.println("hits=" + totHit + " misses=" + totMiss);
block|}
block|}
end_class
end_unit
