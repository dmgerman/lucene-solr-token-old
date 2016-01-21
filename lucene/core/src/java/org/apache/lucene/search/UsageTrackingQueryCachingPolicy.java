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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|index
operator|.
name|LeafReaderContext
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
name|FrequencyTrackingRingBuffer
import|;
end_import
begin_comment
comment|/**  * A {@link QueryCachingPolicy} that tracks usage statistics of recently-used  * filters in order to decide on which filters are worth caching.  *  * It also uses some heuristics on segments, filters and the doc id sets that  * they produce in order to cache more aggressively when the execution cost  * significantly outweighs the caching overhead.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|UsageTrackingQueryCachingPolicy
specifier|public
specifier|final
class|class
name|UsageTrackingQueryCachingPolicy
implements|implements
name|QueryCachingPolicy
block|{
comment|// the hash code that we use as a sentinel in the ring buffer.
DECL|field|SENTINEL
specifier|private
specifier|static
specifier|final
name|int
name|SENTINEL
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|method|isCostly
specifier|static
name|boolean
name|isCostly
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
comment|// This does not measure the cost of iterating over the filter (for this we
comment|// already have the DocIdSetIterator#cost API) but the cost to build the
comment|// DocIdSet in the first place
return|return
name|query
operator|instanceof
name|MultiTermQuery
operator|||
name|query
operator|instanceof
name|MultiTermQueryConstantScoreWrapper
operator|||
name|query
operator|instanceof
name|PointRangeQuery
return|;
block|}
DECL|method|isCheap
specifier|static
name|boolean
name|isCheap
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
comment|// same for cheap queries
comment|// these queries are so cheap that they usually do not need caching
return|return
name|query
operator|instanceof
name|TermQuery
return|;
block|}
DECL|field|segmentPolicy
specifier|private
specifier|final
name|QueryCachingPolicy
operator|.
name|CacheOnLargeSegments
name|segmentPolicy
decl_stmt|;
DECL|field|recentlyUsedFilters
specifier|private
specifier|final
name|FrequencyTrackingRingBuffer
name|recentlyUsedFilters
decl_stmt|;
comment|/**    * Create a new instance.    *    * @param minIndexSize              the minimum size of the top-level index    * @param minSizeRatio              the minimum size ratio for segments to be cached, see {@link QueryCachingPolicy.CacheOnLargeSegments}    * @param historySize               the number of recently used filters to track    */
DECL|method|UsageTrackingQueryCachingPolicy
specifier|public
name|UsageTrackingQueryCachingPolicy
parameter_list|(
name|int
name|minIndexSize
parameter_list|,
name|float
name|minSizeRatio
parameter_list|,
name|int
name|historySize
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|QueryCachingPolicy
operator|.
name|CacheOnLargeSegments
argument_list|(
name|minIndexSize
argument_list|,
name|minSizeRatio
argument_list|)
argument_list|,
name|historySize
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new instance with an history size of 256. */
DECL|method|UsageTrackingQueryCachingPolicy
specifier|public
name|UsageTrackingQueryCachingPolicy
parameter_list|()
block|{
name|this
argument_list|(
name|QueryCachingPolicy
operator|.
name|CacheOnLargeSegments
operator|.
name|DEFAULT
argument_list|,
literal|256
argument_list|)
expr_stmt|;
block|}
DECL|method|UsageTrackingQueryCachingPolicy
specifier|private
name|UsageTrackingQueryCachingPolicy
parameter_list|(
name|QueryCachingPolicy
operator|.
name|CacheOnLargeSegments
name|segmentPolicy
parameter_list|,
name|int
name|historySize
parameter_list|)
block|{
name|this
operator|.
name|segmentPolicy
operator|=
name|segmentPolicy
expr_stmt|;
name|this
operator|.
name|recentlyUsedFilters
operator|=
operator|new
name|FrequencyTrackingRingBuffer
argument_list|(
name|historySize
argument_list|,
name|SENTINEL
argument_list|)
expr_stmt|;
block|}
comment|/**    * For a given query, return how many times it should appear in the history    * before being cached.    */
DECL|method|minFrequencyToCache
specifier|protected
name|int
name|minFrequencyToCache
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
name|isCostly
argument_list|(
name|query
argument_list|)
condition|)
block|{
return|return
literal|2
return|;
block|}
elseif|else
if|if
condition|(
name|isCheap
argument_list|(
name|query
argument_list|)
condition|)
block|{
return|return
literal|20
return|;
block|}
else|else
block|{
comment|// default: cache after the filter has been seen 5 times
return|return
literal|5
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|onUse
specifier|public
name|void
name|onUse
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
assert|assert
name|query
operator|instanceof
name|BoostQuery
operator|==
literal|false
assert|;
assert|assert
name|query
operator|instanceof
name|ConstantScoreQuery
operator|==
literal|false
assert|;
comment|// call hashCode outside of sync block
comment|// in case it's somewhat expensive:
name|int
name|hashCode
init|=
name|query
operator|.
name|hashCode
argument_list|()
decl_stmt|;
comment|// we only track hash codes to avoid holding references to possible
comment|// large queries; this may cause rare false positives, but at worse
comment|// this just means we cache a query that was not in fact used enough:
synchronized|synchronized
init|(
name|this
init|)
block|{
name|recentlyUsedFilters
operator|.
name|add
argument_list|(
name|hashCode
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|frequency
name|int
name|frequency
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
assert|assert
name|query
operator|instanceof
name|BoostQuery
operator|==
literal|false
assert|;
assert|assert
name|query
operator|instanceof
name|ConstantScoreQuery
operator|==
literal|false
assert|;
comment|// call hashCode outside of sync block
comment|// in case it's somewhat expensive:
name|int
name|hashCode
init|=
name|query
operator|.
name|hashCode
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
return|return
name|recentlyUsedFilters
operator|.
name|frequency
argument_list|(
name|hashCode
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|shouldCache
specifier|public
name|boolean
name|shouldCache
parameter_list|(
name|Query
name|query
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|query
operator|instanceof
name|MatchAllDocsQuery
comment|// MatchNoDocsQuery currently rewrites to a BooleanQuery,
comment|// but who knows, it might get its own Weight one day
operator|||
name|query
operator|instanceof
name|MatchNoDocsQuery
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|query
decl_stmt|;
if|if
condition|(
name|bq
operator|.
name|clauses
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|query
operator|instanceof
name|DisjunctionMaxQuery
condition|)
block|{
name|DisjunctionMaxQuery
name|dmq
init|=
operator|(
name|DisjunctionMaxQuery
operator|)
name|query
decl_stmt|;
if|if
condition|(
name|dmq
operator|.
name|getDisjuncts
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|segmentPolicy
operator|.
name|shouldCache
argument_list|(
name|query
argument_list|,
name|context
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|int
name|frequency
init|=
name|frequency
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|int
name|minFrequency
init|=
name|minFrequencyToCache
argument_list|(
name|query
argument_list|)
decl_stmt|;
return|return
name|frequency
operator|>=
name|minFrequency
return|;
block|}
block|}
end_class
end_unit
