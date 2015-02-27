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
name|Bits
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
comment|/**  * A {@link FilterCachingPolicy} that tracks usage statistics of recently-used  * filters in order to decide on which filters are worth caching.  *  * It also uses some heuristics on segments, filters and the doc id sets that  * they produce in order to cache more aggressively when the execution cost  * significantly outweighs the caching overhead.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|UsageTrackingFilterCachingPolicy
specifier|public
specifier|final
class|class
name|UsageTrackingFilterCachingPolicy
implements|implements
name|FilterCachingPolicy
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
name|Filter
name|filter
parameter_list|)
block|{
comment|// This does not measure the cost of iterating over the filter (for this we
comment|// already have the DocIdSetIterator#cost API) but the cost to build the
comment|// DocIdSet in the first place
return|return
name|filter
operator|instanceof
name|QueryWrapperFilter
operator|&&
operator|(
operator|(
name|QueryWrapperFilter
operator|)
name|filter
operator|)
operator|.
name|getQuery
argument_list|()
operator|instanceof
name|MultiTermQuery
return|;
block|}
DECL|method|isCheapToCache
specifier|static
name|boolean
name|isCheapToCache
parameter_list|(
name|DocIdSet
name|set
parameter_list|)
block|{
comment|// the produced doc set is already cacheable, so caching has no
comment|// overhead at all. TODO: extend this to sets whose iterators have a low
comment|// cost?
return|return
name|set
operator|==
literal|null
operator|||
name|set
operator|.
name|isCacheable
argument_list|()
return|;
block|}
DECL|field|segmentPolicy
specifier|private
specifier|final
name|FilterCachingPolicy
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
DECL|field|minFrequencyCostlyFilters
specifier|private
specifier|final
name|int
name|minFrequencyCostlyFilters
decl_stmt|;
DECL|field|minFrequencyCheapFilters
specifier|private
specifier|final
name|int
name|minFrequencyCheapFilters
decl_stmt|;
DECL|field|minFrequencyOtherFilters
specifier|private
specifier|final
name|int
name|minFrequencyOtherFilters
decl_stmt|;
comment|/**    * Create a new instance.    *    * @param minSizeRatio              the minimum size ratio for segments to be cached, see {@link FilterCachingPolicy.CacheOnLargeSegments}    * @param historySize               the number of recently used filters to track    * @param minFrequencyCostlyFilters how many times filters whose {@link Filter#getDocIdSet(LeafReaderContext, Bits) getDocIdSet} method is expensive should have been seen before being cached    * @param minFrequencyCheapFilters  how many times filters that produce {@link DocIdSet}s that are cheap to cached should have been seen before being cached    * @param minFrequencyOtherFilters  how many times other filters should have been seen before being cached    */
DECL|method|UsageTrackingFilterCachingPolicy
specifier|public
name|UsageTrackingFilterCachingPolicy
parameter_list|(
name|float
name|minSizeRatio
parameter_list|,
name|int
name|historySize
parameter_list|,
name|int
name|minFrequencyCostlyFilters
parameter_list|,
name|int
name|minFrequencyCheapFilters
parameter_list|,
name|int
name|minFrequencyOtherFilters
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|FilterCachingPolicy
operator|.
name|CacheOnLargeSegments
argument_list|(
name|minSizeRatio
argument_list|)
argument_list|,
name|historySize
argument_list|,
name|minFrequencyCostlyFilters
argument_list|,
name|minFrequencyCheapFilters
argument_list|,
name|minFrequencyOtherFilters
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new instance with sensible defaults. */
DECL|method|UsageTrackingFilterCachingPolicy
specifier|public
name|UsageTrackingFilterCachingPolicy
parameter_list|()
block|{
comment|// we track the most 256 recently-used filters and cache filters that are
comment|// expensive to build or cheap to cache after we have seen them twice, and
comment|// cache regular filters after we have seen them 5 times
name|this
argument_list|(
name|FilterCachingPolicy
operator|.
name|CacheOnLargeSegments
operator|.
name|DEFAULT
argument_list|,
literal|256
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
DECL|method|UsageTrackingFilterCachingPolicy
specifier|private
name|UsageTrackingFilterCachingPolicy
parameter_list|(
name|FilterCachingPolicy
operator|.
name|CacheOnLargeSegments
name|segmentPolicy
parameter_list|,
name|int
name|historySize
parameter_list|,
name|int
name|minFrequencyCostlyFilters
parameter_list|,
name|int
name|minFrequencyCheapFilters
parameter_list|,
name|int
name|minFrequencyOtherFilters
parameter_list|)
block|{
name|this
operator|.
name|segmentPolicy
operator|=
name|segmentPolicy
expr_stmt|;
if|if
condition|(
name|minFrequencyOtherFilters
operator|<
name|minFrequencyCheapFilters
operator|||
name|minFrequencyOtherFilters
operator|<
name|minFrequencyCheapFilters
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"it does not make sense to cache regular filters more aggressively than filters that are costly to produce or cheap to cache"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minFrequencyCheapFilters
operator|>
name|historySize
operator|||
name|minFrequencyCostlyFilters
operator|>
name|historySize
operator|||
name|minFrequencyOtherFilters
operator|>
name|historySize
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The minimum frequencies should be less than the size of the history of filters that are being tracked"
argument_list|)
throw|;
block|}
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
name|this
operator|.
name|minFrequencyCostlyFilters
operator|=
name|minFrequencyCostlyFilters
expr_stmt|;
name|this
operator|.
name|minFrequencyCheapFilters
operator|=
name|minFrequencyCheapFilters
expr_stmt|;
name|this
operator|.
name|minFrequencyOtherFilters
operator|=
name|minFrequencyOtherFilters
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onUse
specifier|public
name|void
name|onUse
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
comment|// we only track hash codes, which
synchronized|synchronized
init|(
name|this
init|)
block|{
name|recentlyUsedFilters
operator|.
name|add
argument_list|(
name|filter
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|shouldCache
specifier|public
name|boolean
name|shouldCache
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|,
name|DocIdSet
name|set
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|segmentPolicy
operator|.
name|shouldCache
argument_list|(
name|filter
argument_list|,
name|context
argument_list|,
name|set
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
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|frequency
operator|=
name|recentlyUsedFilters
operator|.
name|frequency
argument_list|(
name|filter
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|frequency
operator|>=
name|minFrequencyOtherFilters
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|isCostly
argument_list|(
name|filter
argument_list|)
operator|&&
name|frequency
operator|>=
name|minFrequencyCostlyFilters
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|isCheapToCache
argument_list|(
name|set
argument_list|)
operator|&&
name|frequency
operator|>=
name|minFrequencyCheapFilters
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
