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
name|Collection
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
name|Objects
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
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|atomic
operator|.
name|AtomicBoolean
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
name|IndexReader
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
name|index
operator|.
name|Term
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
name|RoaringDocIdSet
import|;
end_import
begin_comment
comment|/**  * Wraps another {@link Query}'s result and caches it when scores are not  * needed.  The purpose is to allow queries to simply care about matching and  * scoring, and then wrap with this class to add caching.  */
end_comment
begin_class
DECL|class|CachingWrapperQuery
specifier|public
class|class
name|CachingWrapperQuery
extends|extends
name|Query
implements|implements
name|Accountable
implements|,
name|Cloneable
block|{
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
comment|// not final because of clone
DECL|field|policy
specifier|private
specifier|final
name|QueryCachingPolicy
name|policy
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|DocIdSet
argument_list|>
name|cache
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|Object
argument_list|,
name|DocIdSet
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Wraps another query's result and caches it according to the provided policy.    * @param query Query to cache results of    * @param policy policy defining which filters should be cached on which segments    */
DECL|method|CachingWrapperQuery
specifier|public
name|CachingWrapperQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|QueryCachingPolicy
name|policy
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|query
argument_list|,
literal|"Query must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|policy
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|policy
argument_list|,
literal|"QueryCachingPolicy must not be null"
argument_list|)
expr_stmt|;
block|}
comment|/** Same as {@link CachingWrapperQuery#CachingWrapperQuery(Query, QueryCachingPolicy)}    *  but enforces the use of the    *  {@link QueryCachingPolicy.CacheOnLargeSegments#DEFAULT} policy. */
DECL|method|CachingWrapperQuery
specifier|public
name|CachingWrapperQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
argument_list|(
name|query
argument_list|,
name|QueryCachingPolicy
operator|.
name|CacheOnLargeSegments
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the contained query.    * @return the contained query.    */
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
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
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|rewritten
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|==
name|rewritten
condition|)
block|{
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
else|else
block|{
name|CachingWrapperQuery
name|clone
decl_stmt|;
try|try
block|{
name|clone
operator|=
operator|(
name|CachingWrapperQuery
operator|)
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|query
operator|=
name|rewritten
expr_stmt|;
return|return
name|clone
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|// for testing
DECL|field|hitCount
DECL|field|missCount
name|int
name|hitCount
decl_stmt|,
name|missCount
decl_stmt|;
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|weight
init|=
name|query
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|)
decl_stmt|;
if|if
condition|(
name|needsScores
condition|)
block|{
comment|// our cache is not sufficient, we need scores too
return|return
name|weight
return|;
block|}
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|weight
operator|.
name|getQuery
argument_list|()
argument_list|)
block|{
specifier|final
name|AtomicBoolean
name|used
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|weight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|used
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|policy
operator|.
name|onUse
argument_list|(
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|key
init|=
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
decl_stmt|;
name|DocIdSet
name|docIdSet
init|=
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|docIdSet
operator|!=
literal|null
condition|)
block|{
name|hitCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|policy
operator|.
name|shouldCache
argument_list|(
name|query
argument_list|,
name|context
argument_list|)
condition|)
block|{
name|missCount
operator|++
expr_stmt|;
specifier|final
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
name|docIdSet
operator|=
name|DocIdSet
operator|.
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
name|docIdSet
operator|=
name|cacheImpl
argument_list|(
name|scorer
argument_list|,
name|context
operator|.
name|reader
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|docIdSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
return|;
block|}
assert|assert
name|docIdSet
operator|!=
literal|null
assert|;
if|if
condition|(
name|docIdSet
operator|==
name|DocIdSet
operator|.
name|EMPTY
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|DocIdSetIterator
name|disi
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|disi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
literal|0f
argument_list|,
name|disi
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
operator|+
literal|")"
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
operator|||
operator|!
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|o
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
specifier|final
name|CachingWrapperQuery
name|other
init|=
operator|(
name|CachingWrapperQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
name|other
operator|.
name|query
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
operator|(
name|query
operator|.
name|hashCode
argument_list|()
operator|^
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
comment|// Sync only to pull the current set of values:
name|List
argument_list|<
name|DocIdSet
argument_list|>
name|docIdSets
decl_stmt|;
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|docIdSets
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|cache
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|total
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DocIdSet
name|dis
range|:
name|docIdSets
control|)
block|{
name|total
operator|+=
name|dis
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
comment|// Sync to pull the current set of values:
synchronized|synchronized
init|(
name|cache
init|)
block|{
comment|// no need to clone, Accountable#namedAccountables already copies the data
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
block|}
end_class
end_unit
