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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|LinkedHashMap
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
name|Set
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
name|LeafReader
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
name|LeafReader
operator|.
name|CoreClosedListener
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
name|Accountable
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
name|Accountables
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
name|RamUsageEstimator
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
name|RoaringDocIdSet
import|;
end_import
begin_comment
comment|/**  * A {@link FilterCache} that evicts filters using a LRU (least-recently-used)  * eviction policy in order to remain under a given maximum size and number of  * bytes used.  *  * This class is thread-safe.  *  * Note that filter eviction runs in linear time with the total number of  * segments that have cache entries so this cache works best with  * {@link FilterCachingPolicy caching policies} that only cache on "large"  * segments, and it is advised to not share this cache across too many indices.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|LRUFilterCache
specifier|public
class|class
name|LRUFilterCache
implements|implements
name|FilterCache
implements|,
name|Accountable
block|{
comment|// memory usage of a simple query-wrapper filter around a term query
DECL|field|FILTER_DEFAULT_RAM_BYTES_USED
specifier|static
specifier|final
name|long
name|FILTER_DEFAULT_RAM_BYTES_USED
init|=
literal|216
decl_stmt|;
DECL|field|HASHTABLE_RAM_BYTES_PER_ENTRY
specifier|static
specifier|final
name|long
name|HASHTABLE_RAM_BYTES_PER_ENTRY
init|=
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
comment|// key + value
operator|*
literal|2
decl_stmt|;
comment|// hash tables need to be oversized to avoid collisions, assume 2x capacity
DECL|field|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
specifier|static
specifier|final
name|long
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
init|=
name|HASHTABLE_RAM_BYTES_PER_ENTRY
operator|+
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
decl_stmt|;
comment|// previous& next references
DECL|field|maxSize
specifier|private
specifier|final
name|int
name|maxSize
decl_stmt|;
DECL|field|maxRamBytesUsed
specifier|private
specifier|final
name|long
name|maxRamBytesUsed
decl_stmt|;
comment|// maps filters that are contained in the cache to a singleton so that this
comment|// cache does not store several copies of the same filter
DECL|field|uniqueFilters
specifier|private
specifier|final
name|Map
argument_list|<
name|Filter
argument_list|,
name|Filter
argument_list|>
name|uniqueFilters
decl_stmt|;
comment|// The contract between this set and the per-leaf caches is that per-leaf caches
comment|// are only allowed to store sub-sets of the filters that are contained in
comment|// mostRecentlyUsedFilters. This is why write operations are performed under a lock
DECL|field|mostRecentlyUsedFilters
specifier|private
specifier|final
name|Set
argument_list|<
name|Filter
argument_list|>
name|mostRecentlyUsedFilters
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|LeafCache
argument_list|>
name|cache
decl_stmt|;
DECL|field|ramBytesUsed
specifier|private
specifier|volatile
name|long
name|ramBytesUsed
decl_stmt|;
comment|// all updates of this number must be performed under a lock
comment|/**    * Create a new instance that will cache at most<code>maxSize</code> filters    * with at most<code>maxRamBytesUsed</code> bytes of memory.    */
DECL|method|LRUFilterCache
specifier|public
name|LRUFilterCache
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|long
name|maxRamBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
name|this
operator|.
name|maxRamBytesUsed
operator|=
name|maxRamBytesUsed
expr_stmt|;
name|uniqueFilters
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|Filter
argument_list|,
name|Filter
argument_list|>
argument_list|(
literal|16
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mostRecentlyUsedFilters
operator|=
name|uniqueFilters
operator|.
name|keySet
argument_list|()
expr_stmt|;
name|cache
operator|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|ramBytesUsed
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Whether evictions are required. */
DECL|method|requiresEviction
name|boolean
name|requiresEviction
parameter_list|()
block|{
specifier|final
name|int
name|size
init|=
name|mostRecentlyUsedFilters
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|size
operator|>
name|maxSize
operator|||
name|ramBytesUsed
argument_list|()
operator|>
name|maxRamBytesUsed
return|;
block|}
block|}
DECL|method|get
specifier|synchronized
name|DocIdSet
name|get
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
block|{
specifier|final
name|LeafCache
name|leafCache
init|=
name|cache
operator|.
name|get
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|leafCache
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// this get call moves the filter to the most-recently-used position
specifier|final
name|Filter
name|singleton
init|=
name|uniqueFilters
operator|.
name|get
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleton
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|leafCache
operator|.
name|get
argument_list|(
name|singleton
argument_list|)
return|;
block|}
DECL|method|putIfAbsent
specifier|synchronized
name|void
name|putIfAbsent
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
block|{
comment|// under a lock to make sure that mostRecentlyUsedFilters and cache remain sync'ed
assert|assert
name|set
operator|.
name|isCacheable
argument_list|()
assert|;
name|Filter
name|singleton
init|=
name|uniqueFilters
operator|.
name|putIfAbsent
argument_list|(
name|filter
argument_list|,
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleton
operator|==
literal|null
condition|)
block|{
name|ramBytesUsed
operator|+=
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
operator|+
name|ramBytesUsed
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|filter
operator|=
name|singleton
expr_stmt|;
block|}
name|LeafCache
name|leafCache
init|=
name|cache
operator|.
name|get
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|leafCache
operator|==
literal|null
condition|)
block|{
name|leafCache
operator|=
operator|new
name|LeafCache
argument_list|()
expr_stmt|;
specifier|final
name|LeafCache
name|previous
init|=
name|cache
operator|.
name|put
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|,
name|leafCache
argument_list|)
decl_stmt|;
name|ramBytesUsed
operator|+=
name|HASHTABLE_RAM_BYTES_PER_ENTRY
expr_stmt|;
assert|assert
name|previous
operator|==
literal|null
assert|;
comment|// we just created a new leaf cache, need to register a close listener
name|context
operator|.
name|reader
argument_list|()
operator|.
name|addCoreClosedListener
argument_list|(
operator|new
name|CoreClosedListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onClose
parameter_list|(
name|Object
name|ownerCoreCacheKey
parameter_list|)
block|{
name|clearCoreCacheKey
argument_list|(
name|ownerCoreCacheKey
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|leafCache
operator|.
name|putIfAbsent
argument_list|(
name|filter
argument_list|,
name|set
argument_list|)
expr_stmt|;
name|evictIfNecessary
argument_list|()
expr_stmt|;
block|}
DECL|method|evictIfNecessary
specifier|synchronized
name|void
name|evictIfNecessary
parameter_list|()
block|{
comment|// under a lock to make sure that mostRecentlyUsedFilters and cache keep sync'ed
if|if
condition|(
name|requiresEviction
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|Filter
argument_list|>
name|iterator
init|=
name|mostRecentlyUsedFilters
operator|.
name|iterator
argument_list|()
decl_stmt|;
do|do
block|{
specifier|final
name|Filter
name|filter
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|onEviction
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
operator|&&
name|requiresEviction
argument_list|()
condition|)
do|;
block|}
block|}
comment|/**    * Remove all cache entries for the given core cache key.    */
DECL|method|clearCoreCacheKey
specifier|public
specifier|synchronized
name|void
name|clearCoreCacheKey
parameter_list|(
name|Object
name|coreKey
parameter_list|)
block|{
specifier|final
name|LeafCache
name|leafCache
init|=
name|cache
operator|.
name|remove
argument_list|(
name|coreKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|leafCache
operator|!=
literal|null
condition|)
block|{
name|ramBytesUsed
operator|-=
name|leafCache
operator|.
name|ramBytesUsed
operator|+
name|HASHTABLE_RAM_BYTES_PER_ENTRY
expr_stmt|;
block|}
block|}
comment|/**    * Remove all cache entries for the given filter.    */
DECL|method|clearFilter
specifier|public
specifier|synchronized
name|void
name|clearFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
specifier|final
name|Filter
name|singleton
init|=
name|uniqueFilters
operator|.
name|remove
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleton
operator|!=
literal|null
condition|)
block|{
name|onEviction
argument_list|(
name|singleton
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onEviction
specifier|private
name|void
name|onEviction
parameter_list|(
name|Filter
name|singleton
parameter_list|)
block|{
name|ramBytesUsed
operator|-=
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
operator|+
name|ramBytesUsed
argument_list|(
name|singleton
argument_list|)
expr_stmt|;
for|for
control|(
name|LeafCache
name|leafCache
range|:
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
name|leafCache
operator|.
name|remove
argument_list|(
name|singleton
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Clear the content of this cache.    */
DECL|method|clear
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
name|mostRecentlyUsedFilters
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ramBytesUsed
operator|=
literal|0
expr_stmt|;
block|}
comment|// pkg-private for testing
DECL|method|assertConsistent
specifier|synchronized
name|void
name|assertConsistent
parameter_list|()
block|{
if|if
condition|(
name|requiresEviction
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"requires evictions: size="
operator|+
name|mostRecentlyUsedFilters
operator|.
name|size
argument_list|()
operator|+
literal|", maxSize="
operator|+
name|maxSize
operator|+
literal|", ramBytesUsed="
operator|+
name|ramBytesUsed
argument_list|()
operator|+
literal|", maxRamBytesUsed="
operator|+
name|maxRamBytesUsed
argument_list|)
throw|;
block|}
for|for
control|(
name|LeafCache
name|leafCache
range|:
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|Filter
argument_list|>
name|keys
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|keys
operator|.
name|addAll
argument_list|(
name|leafCache
operator|.
name|cache
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|keys
operator|.
name|removeAll
argument_list|(
name|mostRecentlyUsedFilters
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|keys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"One leaf cache contains more keys than the top-level cache: "
operator|+
name|keys
argument_list|)
throw|;
block|}
block|}
name|long
name|recomputedRamBytesUsed
init|=
name|HASHTABLE_RAM_BYTES_PER_ENTRY
operator|*
name|cache
operator|.
name|size
argument_list|()
operator|+
name|LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY
operator|*
name|uniqueFilters
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|Filter
name|filter
range|:
name|mostRecentlyUsedFilters
control|)
block|{
name|recomputedRamBytesUsed
operator|+=
name|ramBytesUsed
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|LeafCache
name|leafCache
range|:
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
name|recomputedRamBytesUsed
operator|+=
name|HASHTABLE_RAM_BYTES_PER_ENTRY
operator|*
name|leafCache
operator|.
name|cache
operator|.
name|size
argument_list|()
expr_stmt|;
for|for
control|(
name|DocIdSet
name|set
range|:
name|leafCache
operator|.
name|cache
operator|.
name|values
argument_list|()
control|)
block|{
name|recomputedRamBytesUsed
operator|+=
name|set
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|recomputedRamBytesUsed
operator|!=
name|ramBytesUsed
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"ramBytesUsed mismatch : "
operator|+
name|ramBytesUsed
operator|+
literal|" != "
operator|+
name|recomputedRamBytesUsed
argument_list|)
throw|;
block|}
block|}
comment|// pkg-private for testing
comment|// return the list of cached filters in LRU order
DECL|method|cachedFilters
specifier|synchronized
name|List
argument_list|<
name|Filter
argument_list|>
name|cachedFilters
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|mostRecentlyUsedFilters
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doCache
specifier|public
name|Filter
name|doCache
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|FilterCachingPolicy
name|policy
parameter_list|)
block|{
while|while
condition|(
name|filter
operator|instanceof
name|CachingWrapperFilter
condition|)
block|{
comment|// should we throw an exception instead?
name|filter
operator|=
operator|(
operator|(
name|CachingWrapperFilter
operator|)
name|filter
operator|)
operator|.
name|in
expr_stmt|;
block|}
return|return
operator|new
name|CachingWrapperFilter
argument_list|(
name|filter
argument_list|,
name|policy
argument_list|)
return|;
block|}
comment|/**    *  Provide the DocIdSet to be cached, using the DocIdSet provided    *  by the wrapped Filter.<p>This implementation returns the given {@link DocIdSet},    *  if {@link DocIdSet#isCacheable} returns<code>true</code>, else it calls    *  {@link #cacheImpl(DocIdSetIterator, org.apache.lucene.index.LeafReader)}    *<p>Note: This method returns {@linkplain DocIdSet#EMPTY} if the given docIdSet    *  is<code>null</code> or if {@link DocIdSet#iterator()} return<code>null</code>. The empty    *  instance is use as a placeholder in the cache instead of the<code>null</code> value.    */
DECL|method|docIdSetToCache
specifier|protected
name|DocIdSet
name|docIdSetToCache
parameter_list|(
name|DocIdSet
name|docIdSet
parameter_list|,
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docIdSet
operator|==
literal|null
operator|||
name|docIdSet
operator|.
name|isCacheable
argument_list|()
condition|)
block|{
return|return
name|docIdSet
return|;
block|}
else|else
block|{
specifier|final
name|DocIdSetIterator
name|it
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|cacheImpl
argument_list|(
name|it
argument_list|,
name|reader
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|ramBytesUsed
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
return|return
name|Accountables
operator|.
name|namedAccountables
argument_list|(
literal|"segment"
argument_list|,
name|cache
argument_list|)
return|;
block|}
block|}
comment|/**    * Return the number of bytes used by the given filter. The default    * implementation returns {@link Accountable#ramBytesUsed()} if the filter    * implements {@link Accountable} and<code>1024</code> otherwise.    */
DECL|method|ramBytesUsed
specifier|protected
name|long
name|ramBytesUsed
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|instanceof
name|Accountable
condition|)
block|{
return|return
operator|(
operator|(
name|Accountable
operator|)
name|filter
operator|)
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
return|return
name|FILTER_DEFAULT_RAM_BYTES_USED
return|;
block|}
comment|/**    * Default cache implementation: uses {@link RoaringDocIdSet}.    */
DECL|method|cacheImpl
specifier|protected
name|DocIdSet
name|cacheImpl
parameter_list|(
name|DocIdSetIterator
name|iterator
parameter_list|,
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|RoaringDocIdSet
operator|.
name|Builder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|iterator
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|// this class is not thread-safe, everything but ramBytesUsed needs to be called under a lock
DECL|class|LeafCache
specifier|private
class|class
name|LeafCache
implements|implements
name|Accountable
block|{
DECL|field|cache
specifier|private
specifier|final
name|Map
argument_list|<
name|Filter
argument_list|,
name|DocIdSet
argument_list|>
name|cache
decl_stmt|;
DECL|field|ramBytesUsed
specifier|private
specifier|volatile
name|long
name|ramBytesUsed
decl_stmt|;
DECL|method|LeafCache
name|LeafCache
parameter_list|()
block|{
name|cache
operator|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|ramBytesUsed
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|incrementRamBytesUsed
specifier|private
name|void
name|incrementRamBytesUsed
parameter_list|(
name|long
name|inc
parameter_list|)
block|{
name|ramBytesUsed
operator|+=
name|inc
expr_stmt|;
name|LRUFilterCache
operator|.
name|this
operator|.
name|ramBytesUsed
operator|+=
name|inc
expr_stmt|;
block|}
DECL|method|get
name|DocIdSet
name|get
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|filter
argument_list|)
return|;
block|}
DECL|method|putIfAbsent
name|void
name|putIfAbsent
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|DocIdSet
name|set
parameter_list|)
block|{
if|if
condition|(
name|cache
operator|.
name|putIfAbsent
argument_list|(
name|filter
argument_list|,
name|set
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// the set was actually put
name|incrementRamBytesUsed
argument_list|(
name|HASHTABLE_RAM_BYTES_PER_ENTRY
operator|+
name|set
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|remove
name|void
name|remove
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|DocIdSet
name|removed
init|=
name|cache
operator|.
name|remove
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed
operator|!=
literal|null
condition|)
block|{
name|incrementRamBytesUsed
argument_list|(
operator|-
operator|(
name|HASHTABLE_RAM_BYTES_PER_ENTRY
operator|+
name|removed
operator|.
name|ramBytesUsed
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|ramBytesUsed
return|;
block|}
block|}
DECL|class|CachingWrapperFilter
specifier|private
class|class
name|CachingWrapperFilter
extends|extends
name|Filter
block|{
DECL|field|in
specifier|private
specifier|final
name|Filter
name|in
decl_stmt|;
DECL|field|policy
specifier|private
specifier|final
name|FilterCachingPolicy
name|policy
decl_stmt|;
DECL|method|CachingWrapperFilter
name|CachingWrapperFilter
parameter_list|(
name|Filter
name|in
parameter_list|,
name|FilterCachingPolicy
name|policy
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|policy
operator|=
name|policy
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|context
operator|.
name|ord
operator|==
literal|0
condition|)
block|{
name|policy
operator|.
name|onUse
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|DocIdSet
name|set
init|=
name|get
argument_list|(
name|in
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
comment|// do not apply acceptDocs yet, we want the cached filter to not take them into account
name|set
operator|=
name|in
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|policy
operator|.
name|shouldCache
argument_list|(
name|in
argument_list|,
name|context
argument_list|,
name|set
argument_list|)
condition|)
block|{
name|set
operator|=
name|docIdSetToCache
argument_list|(
name|set
argument_list|,
name|context
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
comment|// null values are not supported
name|set
operator|=
name|DocIdSet
operator|.
name|EMPTY
expr_stmt|;
block|}
comment|// it might happen that another thread computed the same set in parallel
comment|// although this might incur some CPU overhead, it is probably better
comment|// this way than trying to lock and preventing other filters to be
comment|// computed at the same time?
name|putIfAbsent
argument_list|(
name|in
argument_list|,
name|context
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|set
operator|==
name|DocIdSet
operator|.
name|EMPTY
condition|?
literal|null
else|:
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
name|set
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|instanceof
name|CachingWrapperFilter
operator|&&
name|in
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|CachingWrapperFilter
operator|)
name|obj
operator|)
operator|.
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|in
operator|.
name|hashCode
argument_list|()
operator|^
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
