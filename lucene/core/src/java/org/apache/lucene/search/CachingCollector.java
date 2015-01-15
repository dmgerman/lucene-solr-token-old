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
name|ArrayUtil
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
name|Arrays
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
begin_comment
comment|/**  * Caches all docs, and optionally also scores, coming from  * a search, and is then able to replay them to another  * collector.  You specify the max RAM this class may use.  * Once the collection is done, call {@link #isCached}. If  * this returns true, you can use {@link #replay(Collector)}  * against a new collector.  If it returns false, this means  * too much RAM was required and you must instead re-run the  * original search.  *  *<p><b>NOTE</b>: this class consumes 4 (or 8 bytes, if  * scoring is cached) per collected document.  If the result  * set is large this can easily be a very substantial amount  * of RAM!  *  *<p>See the Lucene<tt>modules/grouping</tt> module for more  * details including a full code example.</p>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|CachingCollector
specifier|public
specifier|abstract
class|class
name|CachingCollector
extends|extends
name|FilterCollector
block|{
DECL|field|INITIAL_ARRAY_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_ARRAY_SIZE
init|=
literal|128
decl_stmt|;
DECL|class|CachedScorer
specifier|private
specifier|static
specifier|final
class|class
name|CachedScorer
extends|extends
name|Scorer
block|{
comment|// NOTE: these members are package-private b/c that way accessing them from
comment|// the outer class does not incur access check by the JVM. The same
comment|// situation would be if they were defined in the outer class as private
comment|// members.
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|field|score
name|float
name|score
decl_stmt|;
DECL|method|CachedScorer
specifier|private
name|CachedScorer
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|()
block|{
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
specifier|final
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
specifier|final
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
specifier|final
name|int
name|freq
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
specifier|final
name|int
name|nextDoc
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
DECL|class|NoScoreCachingCollector
specifier|private
specifier|static
class|class
name|NoScoreCachingCollector
extends|extends
name|CachingCollector
block|{
DECL|field|contexts
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|contexts
decl_stmt|;
DECL|field|docs
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|docs
decl_stmt|;
DECL|field|maxDocsToCache
name|int
name|maxDocsToCache
decl_stmt|;
DECL|field|lastCollector
name|NoScoreCachingLeafCollector
name|lastCollector
decl_stmt|;
DECL|method|NoScoreCachingCollector
name|NoScoreCachingCollector
parameter_list|(
name|Collector
name|in
parameter_list|,
name|int
name|maxDocsToCache
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDocsToCache
operator|=
name|maxDocsToCache
expr_stmt|;
name|contexts
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|docs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|wrap
specifier|protected
name|NoScoreCachingLeafCollector
name|wrap
parameter_list|(
name|LeafCollector
name|in
parameter_list|,
name|int
name|maxDocsToCache
parameter_list|)
block|{
return|return
operator|new
name|NoScoreCachingLeafCollector
argument_list|(
name|in
argument_list|,
name|maxDocsToCache
argument_list|)
return|;
block|}
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|postCollection
argument_list|()
expr_stmt|;
specifier|final
name|LeafCollector
name|in
init|=
name|this
operator|.
name|in
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|contexts
operator|!=
literal|null
condition|)
block|{
name|contexts
operator|.
name|add
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxDocsToCache
operator|>=
literal|0
condition|)
block|{
return|return
name|lastCollector
operator|=
name|wrap
argument_list|(
name|in
argument_list|,
name|maxDocsToCache
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|in
return|;
block|}
block|}
DECL|method|invalidate
specifier|protected
name|void
name|invalidate
parameter_list|()
block|{
name|maxDocsToCache
operator|=
operator|-
literal|1
expr_stmt|;
name|contexts
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|docs
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|postCollect
specifier|protected
name|void
name|postCollect
parameter_list|(
name|NoScoreCachingLeafCollector
name|collector
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|docs
init|=
name|collector
operator|.
name|cachedDocs
argument_list|()
decl_stmt|;
name|maxDocsToCache
operator|-=
name|docs
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|docs
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
DECL|method|postCollection
specifier|private
name|void
name|postCollection
parameter_list|()
block|{
if|if
condition|(
name|lastCollector
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|lastCollector
operator|.
name|hasCache
argument_list|()
condition|)
block|{
name|invalidate
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|postCollect
argument_list|(
name|lastCollector
argument_list|)
expr_stmt|;
block|}
name|lastCollector
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|collect
specifier|protected
name|void
name|collect
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
index|[]
name|docs
init|=
name|this
operator|.
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|doc
range|:
name|docs
control|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|replay
specifier|public
name|void
name|replay
parameter_list|(
name|Collector
name|other
parameter_list|)
throws|throws
name|IOException
block|{
name|postCollection
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isCached
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot replay: cache was cleared because too much RAM was required"
argument_list|)
throw|;
block|}
assert|assert
name|docs
operator|.
name|size
argument_list|()
operator|==
name|contexts
operator|.
name|size
argument_list|()
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|contexts
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|LeafReaderContext
name|context
init|=
name|contexts
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|LeafCollector
name|collector
init|=
name|other
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|collect
argument_list|(
name|collector
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|ScoreCachingCollector
specifier|private
specifier|static
class|class
name|ScoreCachingCollector
extends|extends
name|NoScoreCachingCollector
block|{
DECL|field|scores
name|List
argument_list|<
name|float
index|[]
argument_list|>
name|scores
decl_stmt|;
DECL|method|ScoreCachingCollector
name|ScoreCachingCollector
parameter_list|(
name|Collector
name|in
parameter_list|,
name|int
name|maxDocsToCache
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
name|maxDocsToCache
argument_list|)
expr_stmt|;
name|scores
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|wrap
specifier|protected
name|NoScoreCachingLeafCollector
name|wrap
parameter_list|(
name|LeafCollector
name|in
parameter_list|,
name|int
name|maxDocsToCache
parameter_list|)
block|{
return|return
operator|new
name|ScoreCachingLeafCollector
argument_list|(
name|in
argument_list|,
name|maxDocsToCache
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|postCollect
specifier|protected
name|void
name|postCollect
parameter_list|(
name|NoScoreCachingLeafCollector
name|collector
parameter_list|)
block|{
specifier|final
name|ScoreCachingLeafCollector
name|coll
init|=
operator|(
name|ScoreCachingLeafCollector
operator|)
name|collector
decl_stmt|;
name|super
operator|.
name|postCollect
argument_list|(
name|coll
argument_list|)
expr_stmt|;
name|scores
operator|.
name|add
argument_list|(
name|coll
operator|.
name|cachedScores
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|collect
specifier|protected
name|void
name|collect
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
index|[]
name|docs
init|=
name|this
operator|.
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|float
index|[]
name|scores
init|=
name|this
operator|.
name|scores
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
assert|assert
name|docs
operator|.
name|length
operator|==
name|scores
operator|.
name|length
assert|;
specifier|final
name|CachedScorer
name|scorer
init|=
operator|new
name|CachedScorer
argument_list|()
decl_stmt|;
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|docs
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|scorer
operator|.
name|doc
operator|=
name|docs
index|[
name|j
index|]
expr_stmt|;
name|scorer
operator|.
name|score
operator|=
name|scores
index|[
name|j
index|]
expr_stmt|;
name|collector
operator|.
name|collect
argument_list|(
name|scorer
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|NoScoreCachingLeafCollector
specifier|private
class|class
name|NoScoreCachingLeafCollector
extends|extends
name|FilterLeafCollector
block|{
DECL|field|maxDocsToCache
specifier|final
name|int
name|maxDocsToCache
decl_stmt|;
DECL|field|docs
name|int
index|[]
name|docs
decl_stmt|;
DECL|field|docCount
name|int
name|docCount
decl_stmt|;
DECL|method|NoScoreCachingLeafCollector
name|NoScoreCachingLeafCollector
parameter_list|(
name|LeafCollector
name|in
parameter_list|,
name|int
name|maxDocsToCache
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDocsToCache
operator|=
name|maxDocsToCache
expr_stmt|;
name|docs
operator|=
operator|new
name|int
index|[
name|Math
operator|.
name|min
argument_list|(
name|maxDocsToCache
argument_list|,
name|INITIAL_ARRAY_SIZE
argument_list|)
index|]
expr_stmt|;
name|docCount
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|grow
specifier|protected
name|void
name|grow
parameter_list|(
name|int
name|newLen
parameter_list|)
block|{
name|docs
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|docs
argument_list|,
name|newLen
argument_list|)
expr_stmt|;
block|}
DECL|method|invalidate
specifier|protected
name|void
name|invalidate
parameter_list|()
block|{
name|docs
operator|=
literal|null
expr_stmt|;
name|docCount
operator|=
operator|-
literal|1
expr_stmt|;
name|cached
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|buffer
specifier|protected
name|void
name|buffer
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|docs
index|[
name|docCount
index|]
operator|=
name|doc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docs
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|docCount
operator|>=
name|docs
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|docCount
operator|>=
name|maxDocsToCache
condition|)
block|{
name|invalidate
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|newLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|docCount
operator|+
literal|1
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
argument_list|,
name|maxDocsToCache
argument_list|)
decl_stmt|;
name|grow
argument_list|(
name|newLen
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|docs
operator|!=
literal|null
condition|)
block|{
name|buffer
argument_list|(
name|doc
argument_list|)
expr_stmt|;
operator|++
name|docCount
expr_stmt|;
block|}
block|}
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|hasCache
name|boolean
name|hasCache
parameter_list|()
block|{
return|return
name|docs
operator|!=
literal|null
return|;
block|}
DECL|method|cachedDocs
name|int
index|[]
name|cachedDocs
parameter_list|()
block|{
return|return
name|docs
operator|==
literal|null
condition|?
literal|null
else|:
name|Arrays
operator|.
name|copyOf
argument_list|(
name|docs
argument_list|,
name|docCount
argument_list|)
return|;
block|}
block|}
DECL|class|ScoreCachingLeafCollector
specifier|private
class|class
name|ScoreCachingLeafCollector
extends|extends
name|NoScoreCachingLeafCollector
block|{
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
DECL|field|scores
name|float
index|[]
name|scores
decl_stmt|;
DECL|method|ScoreCachingLeafCollector
name|ScoreCachingLeafCollector
parameter_list|(
name|LeafCollector
name|in
parameter_list|,
name|int
name|maxDocsToCache
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
name|maxDocsToCache
argument_list|)
expr_stmt|;
name|scores
operator|=
operator|new
name|float
index|[
name|docs
operator|.
name|length
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|super
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|grow
specifier|protected
name|void
name|grow
parameter_list|(
name|int
name|newLen
parameter_list|)
block|{
name|super
operator|.
name|grow
argument_list|(
name|newLen
argument_list|)
expr_stmt|;
name|scores
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|scores
argument_list|,
name|newLen
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|invalidate
specifier|protected
name|void
name|invalidate
parameter_list|()
block|{
name|super
operator|.
name|invalidate
argument_list|()
expr_stmt|;
name|scores
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buffer
specifier|protected
name|void
name|buffer
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|buffer
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|scores
index|[
name|docCount
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
DECL|method|cachedScores
name|float
index|[]
name|cachedScores
parameter_list|()
block|{
return|return
name|docs
operator|==
literal|null
condition|?
literal|null
else|:
name|Arrays
operator|.
name|copyOf
argument_list|(
name|scores
argument_list|,
name|docCount
argument_list|)
return|;
block|}
block|}
comment|/**    * Creates a {@link CachingCollector} which does not wrap another collector.    * The cached documents and scores can later be {@link #replay(Collector)    * replayed}.    */
DECL|method|create
specifier|public
specifier|static
name|CachingCollector
name|create
parameter_list|(
name|boolean
name|cacheScores
parameter_list|,
name|double
name|maxRAMMB
parameter_list|)
block|{
name|Collector
name|other
init|=
operator|new
name|SimpleCollector
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{}
block|}
decl_stmt|;
return|return
name|create
argument_list|(
name|other
argument_list|,
name|cacheScores
argument_list|,
name|maxRAMMB
argument_list|)
return|;
block|}
comment|/**    * Create a new {@link CachingCollector} that wraps the given collector and    * caches documents and scores up to the specified RAM threshold.    *    * @param other    *          the Collector to wrap and delegate calls to.    * @param cacheScores    *          whether to cache scores in addition to document IDs. Note that    *          this increases the RAM consumed per doc    * @param maxRAMMB    *          the maximum RAM in MB to consume for caching the documents and    *          scores. If the collector exceeds the threshold, no documents and    *          scores are cached.    */
DECL|method|create
specifier|public
specifier|static
name|CachingCollector
name|create
parameter_list|(
name|Collector
name|other
parameter_list|,
name|boolean
name|cacheScores
parameter_list|,
name|double
name|maxRAMMB
parameter_list|)
block|{
name|int
name|bytesPerDoc
init|=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
decl_stmt|;
if|if
condition|(
name|cacheScores
condition|)
block|{
name|bytesPerDoc
operator|+=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_FLOAT
expr_stmt|;
block|}
specifier|final
name|int
name|maxDocsToCache
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|maxRAMMB
operator|*
literal|1024
operator|*
literal|1024
operator|)
operator|/
name|bytesPerDoc
argument_list|)
decl_stmt|;
return|return
name|create
argument_list|(
name|other
argument_list|,
name|cacheScores
argument_list|,
name|maxDocsToCache
argument_list|)
return|;
block|}
comment|/**    * Create a new {@link CachingCollector} that wraps the given collector and    * caches documents and scores up to the specified max docs threshold.    *    * @param other    *          the Collector to wrap and delegate calls to.    * @param cacheScores    *          whether to cache scores in addition to document IDs. Note that    *          this increases the RAM consumed per doc    * @param maxDocsToCache    *          the maximum number of documents for caching the documents and    *          possible the scores. If the collector exceeds the threshold,    *          no documents and scores are cached.    */
DECL|method|create
specifier|public
specifier|static
name|CachingCollector
name|create
parameter_list|(
name|Collector
name|other
parameter_list|,
name|boolean
name|cacheScores
parameter_list|,
name|int
name|maxDocsToCache
parameter_list|)
block|{
return|return
name|cacheScores
condition|?
operator|new
name|ScoreCachingCollector
argument_list|(
name|other
argument_list|,
name|maxDocsToCache
argument_list|)
else|:
operator|new
name|NoScoreCachingCollector
argument_list|(
name|other
argument_list|,
name|maxDocsToCache
argument_list|)
return|;
block|}
DECL|field|cached
specifier|private
name|boolean
name|cached
decl_stmt|;
DECL|method|CachingCollector
specifier|private
name|CachingCollector
parameter_list|(
name|Collector
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|cached
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Return true is this collector is able to replay collection.    */
DECL|method|isCached
specifier|public
specifier|final
name|boolean
name|isCached
parameter_list|()
block|{
return|return
name|cached
return|;
block|}
comment|/**    * Replays the cached doc IDs (and scores) to the given Collector. If this    * instance does not cache scores, then Scorer is not set on    * {@code other.setScorer} as well as scores are not replayed.    *    * @throws IllegalStateException    *           if this collector is not cached (i.e., if the RAM limits were too    *           low for the number of documents + scores to cache).    * @throws IllegalArgumentException    *           if the given Collect's does not support out-of-order collection,    *           while the collector passed to the ctor does.    */
DECL|method|replay
specifier|public
specifier|abstract
name|void
name|replay
parameter_list|(
name|Collector
name|other
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class
end_unit
