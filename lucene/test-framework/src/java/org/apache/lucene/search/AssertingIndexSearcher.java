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
name|lang
operator|.
name|ref
operator|.
name|WeakReference
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
name|Random
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
name|ExecutorService
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
name|AssertingAtomicReader
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
name|AtomicReaderContext
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
name|DocsEnum
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
name|IndexReaderContext
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
name|VirtualMethod
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
name|_TestUtil
import|;
end_import
begin_comment
comment|/**  * Helper class that adds some extra checks to ensure correct  * usage of {@code IndexSearcher} and {@code Weight}.  */
end_comment
begin_class
DECL|class|AssertingIndexSearcher
specifier|public
class|class
name|AssertingIndexSearcher
extends|extends
name|IndexSearcher
block|{
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|AssertingIndexSearcher
specifier|public
name|AssertingIndexSearcher
parameter_list|(
name|Random
name|random
parameter_list|,
name|IndexReader
name|r
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AssertingIndexSearcher
specifier|public
name|AssertingIndexSearcher
parameter_list|(
name|Random
name|random
parameter_list|,
name|IndexReaderContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AssertingIndexSearcher
specifier|public
name|AssertingIndexSearcher
parameter_list|(
name|Random
name|random
parameter_list|,
name|IndexReader
name|r
parameter_list|,
name|ExecutorService
name|ex
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AssertingIndexSearcher
specifier|public
name|AssertingIndexSearcher
parameter_list|(
name|Random
name|random
parameter_list|,
name|IndexReaderContext
name|context
parameter_list|,
name|ExecutorService
name|ex
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Ensures, that the returned {@code Weight} is not normalized again, which may produce wrong scores. */
annotation|@
name|Override
DECL|method|createNormalizedWeight
specifier|public
name|Weight
name|createNormalizedWeight
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|w
init|=
name|super
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|)
decl_stmt|;
return|return
operator|new
name|AssertingWeight
argument_list|(
name|random
argument_list|,
name|w
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Weight already normalized."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Weight already normalized."
argument_list|)
throw|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|wrapFilter
specifier|protected
name|Query
name|wrapFilter
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
return|return
name|super
operator|.
name|wrapFilter
argument_list|(
name|query
argument_list|,
name|filter
argument_list|)
return|;
return|return
operator|(
name|filter
operator|==
literal|null
operator|)
condition|?
name|query
else|:
operator|new
name|FilteredQuery
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|_TestUtil
operator|.
name|randomFilterStrategy
argument_list|(
name|random
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|protected
name|void
name|search
parameter_list|(
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|search
argument_list|(
name|leaves
argument_list|,
name|AssertingWeight
operator|.
name|wrap
argument_list|(
name|random
argument_list|,
name|weight
argument_list|)
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
DECL|class|AssertingWeight
specifier|static
class|class
name|AssertingWeight
extends|extends
name|Weight
block|{
DECL|method|wrap
specifier|static
name|Weight
name|wrap
parameter_list|(
name|Random
name|random
parameter_list|,
name|Weight
name|other
parameter_list|)
block|{
return|return
name|other
operator|instanceof
name|AssertingWeight
condition|?
name|other
else|:
operator|new
name|AssertingWeight
argument_list|(
name|random
argument_list|,
name|other
argument_list|)
return|;
block|}
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|in
specifier|final
name|Weight
name|in
decl_stmt|;
DECL|method|AssertingWeight
name|AssertingWeight
parameter_list|(
name|Random
name|random
parameter_list|,
name|Weight
name|in
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|in
operator|.
name|getQuery
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getValueForNormalization
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|in
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if the caller asks for in-order scoring or if the weight does not support
comment|// out-of order scoring then collection will have to happen in-order.
specifier|final
name|boolean
name|inOrder
init|=
name|scoreDocsInOrder
operator|||
operator|!
name|scoresDocsOutOfOrder
argument_list|()
decl_stmt|;
specifier|final
name|Scorer
name|inScorer
init|=
name|in
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|scoreDocsInOrder
argument_list|,
name|topScorer
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
return|return
name|AssertingScorer
operator|.
name|wrap
argument_list|(
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|inScorer
argument_list|,
name|topScorer
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|scoresDocsOutOfOrder
specifier|public
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
return|return
name|in
operator|.
name|scoresDocsOutOfOrder
argument_list|()
return|;
block|}
block|}
DECL|enum|TopScorer
enum|enum
name|TopScorer
block|{
DECL|enum constant|YES
DECL|enum constant|NO
DECL|enum constant|UNKNOWN
name|YES
block|,
name|NO
block|,
name|UNKNOWN
block|;   }
DECL|class|AssertingScorer
specifier|public
specifier|static
class|class
name|AssertingScorer
extends|extends
name|Scorer
block|{
DECL|field|SCORE_COLLECTOR
specifier|private
specifier|static
specifier|final
name|VirtualMethod
argument_list|<
name|Scorer
argument_list|>
name|SCORE_COLLECTOR
init|=
operator|new
name|VirtualMethod
argument_list|<
name|Scorer
argument_list|>
argument_list|(
name|Scorer
operator|.
name|class
argument_list|,
literal|"score"
argument_list|,
name|Collector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SCORE_COLLECTOR_RANGE
specifier|private
specifier|static
specifier|final
name|VirtualMethod
argument_list|<
name|Scorer
argument_list|>
name|SCORE_COLLECTOR_RANGE
init|=
operator|new
name|VirtualMethod
argument_list|<
name|Scorer
argument_list|>
argument_list|(
name|Scorer
operator|.
name|class
argument_list|,
literal|"score"
argument_list|,
name|Collector
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// we need to track scorers using a weak hash map because otherwise we
comment|// could loose references because of eg.
comment|// AssertingScorer.score(Collector) which needs to delegate to work correctly
DECL|field|ASSERTING_INSTANCES
specifier|private
specifier|static
name|Map
argument_list|<
name|Scorer
argument_list|,
name|WeakReference
argument_list|<
name|AssertingScorer
argument_list|>
argument_list|>
name|ASSERTING_INSTANCES
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|Scorer
argument_list|,
name|WeakReference
argument_list|<
name|AssertingScorer
argument_list|>
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|wrap
specifier|private
specifier|static
name|Scorer
name|wrap
parameter_list|(
name|Random
name|random
parameter_list|,
name|Scorer
name|other
parameter_list|,
name|TopScorer
name|topScorer
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|other
operator|instanceof
name|AssertingScorer
condition|)
block|{
return|return
name|other
return|;
block|}
specifier|final
name|AssertingScorer
name|assertScorer
init|=
operator|new
name|AssertingScorer
argument_list|(
name|random
argument_list|,
name|other
argument_list|,
name|topScorer
argument_list|,
name|inOrder
argument_list|)
decl_stmt|;
name|ASSERTING_INSTANCES
operator|.
name|put
argument_list|(
name|other
argument_list|,
operator|new
name|WeakReference
argument_list|<
name|AssertingScorer
argument_list|>
argument_list|(
name|assertScorer
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|assertScorer
return|;
block|}
DECL|method|wrap
specifier|static
name|Scorer
name|wrap
parameter_list|(
name|Random
name|random
parameter_list|,
name|Scorer
name|other
parameter_list|,
name|boolean
name|topScorer
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|random
argument_list|,
name|other
argument_list|,
name|topScorer
condition|?
name|TopScorer
operator|.
name|YES
else|:
name|TopScorer
operator|.
name|NO
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
DECL|method|getAssertingScorer
specifier|static
name|Scorer
name|getAssertingScorer
parameter_list|(
name|Random
name|random
parameter_list|,
name|Scorer
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|other
operator|instanceof
name|AssertingScorer
condition|)
block|{
return|return
name|other
return|;
block|}
specifier|final
name|WeakReference
argument_list|<
name|AssertingScorer
argument_list|>
name|assertingScorerRef
init|=
name|ASSERTING_INSTANCES
operator|.
name|get
argument_list|(
name|other
argument_list|)
decl_stmt|;
specifier|final
name|AssertingScorer
name|assertingScorer
init|=
name|assertingScorerRef
operator|==
literal|null
condition|?
literal|null
else|:
name|assertingScorerRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|assertingScorer
operator|==
literal|null
condition|)
block|{
comment|// can happen in case of memory pressure or if
comment|// scorer1.score(collector) calls
comment|// collector.setScorer(scorer2) with scorer1 != scorer2, such as
comment|// BooleanScorer. In that case we can't enable all assertions
return|return
operator|new
name|AssertingScorer
argument_list|(
name|random
argument_list|,
name|other
argument_list|,
name|TopScorer
operator|.
name|UNKNOWN
argument_list|,
literal|false
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|assertingScorer
return|;
block|}
block|}
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|in
specifier|final
name|Scorer
name|in
decl_stmt|;
DECL|field|docsEnumIn
specifier|final
name|AssertingAtomicReader
operator|.
name|AssertingDocsEnum
name|docsEnumIn
decl_stmt|;
DECL|field|topScorer
specifier|final
name|TopScorer
name|topScorer
decl_stmt|;
DECL|field|inOrder
specifier|final
name|boolean
name|inOrder
decl_stmt|;
DECL|field|canCallNextDoc
specifier|final
name|boolean
name|canCallNextDoc
decl_stmt|;
DECL|method|AssertingScorer
specifier|private
name|AssertingScorer
parameter_list|(
name|Random
name|random
parameter_list|,
name|Scorer
name|in
parameter_list|,
name|TopScorer
name|topScorer
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|topScorer
operator|=
name|topScorer
expr_stmt|;
name|this
operator|.
name|inOrder
operator|=
name|inOrder
expr_stmt|;
name|this
operator|.
name|docsEnumIn
operator|=
operator|new
name|AssertingAtomicReader
operator|.
name|AssertingDocsEnum
argument_list|(
name|in
argument_list|,
name|topScorer
operator|==
name|TopScorer
operator|.
name|NO
argument_list|)
expr_stmt|;
name|this
operator|.
name|canCallNextDoc
operator|=
name|topScorer
operator|!=
name|TopScorer
operator|.
name|YES
comment|// not a top scorer
operator|||
operator|!
name|SCORE_COLLECTOR_RANGE
operator|.
name|isOverriddenAsOf
argument_list|(
name|in
operator|.
name|getClass
argument_list|()
argument_list|)
comment|// the default impl relies upon nextDoc()
operator|||
operator|!
name|SCORE_COLLECTOR
operator|.
name|isOverriddenAsOf
argument_list|(
name|in
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
comment|// the default impl relies upon nextDoc()
block|}
DECL|method|getIn
specifier|public
name|Scorer
name|getIn
parameter_list|()
block|{
return|return
name|in
return|;
block|}
DECL|method|iterating
name|boolean
name|iterating
parameter_list|()
block|{
switch|switch
condition|(
name|docID
argument_list|()
condition|)
block|{
case|case
operator|-
literal|1
case|:
case|case
name|NO_MORE_DOCS
case|:
return|return
literal|false
return|;
default|default:
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|iterating
argument_list|()
assert|;
specifier|final
name|float
name|score
init|=
name|in
operator|.
name|score
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|score
argument_list|)
assert|;
assert|assert
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|score
argument_list|)
assert|;
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|topScorer
operator|!=
name|TopScorer
operator|.
name|NO
assert|;
if|if
condition|(
name|SCORE_COLLECTOR
operator|.
name|isOverriddenAsOf
argument_list|(
name|this
operator|.
name|in
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|boolean
name|remaining
init|=
name|in
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|DocsEnum
operator|.
name|NO_MORE_DOCS
argument_list|,
name|in
operator|.
name|nextDoc
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
operator|!
name|remaining
assert|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|in
operator|.
name|score
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|in
operator|.
name|score
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// score(Collector) has not been overridden, use the super method in
comment|// order to benefit from all assertions
name|super
operator|.
name|score
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|boolean
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|firstDocID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|topScorer
operator|!=
name|TopScorer
operator|.
name|NO
assert|;
if|if
condition|(
name|SCORE_COLLECTOR_RANGE
operator|.
name|isOverriddenAsOf
argument_list|(
name|this
operator|.
name|in
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|in
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|max
argument_list|,
name|firstDocID
argument_list|)
return|;
block|}
else|else
block|{
comment|// score(Collector,int,int) has not been overridden, use the super
comment|// method in order to benefit from all assertions
return|return
name|super
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|max
argument_list|,
name|firstDocID
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|in
operator|.
name|getChildren
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|iterating
argument_list|()
assert|;
return|return
name|in
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|in
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|canCallNextDoc
operator|:
literal|"top scorers should not call nextDoc()"
assert|;
return|return
name|docsEnumIn
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|canCallNextDoc
operator|:
literal|"top scorers should not call advance(target)"
assert|;
return|return
name|docsEnumIn
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
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
name|in
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
DECL|class|AssertingCollector
specifier|static
class|class
name|AssertingCollector
extends|extends
name|Collector
block|{
DECL|method|wrap
specifier|static
name|Collector
name|wrap
parameter_list|(
name|Random
name|random
parameter_list|,
name|Collector
name|other
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
return|return
name|other
operator|instanceof
name|AssertingCollector
condition|?
name|other
else|:
operator|new
name|AssertingCollector
argument_list|(
name|random
argument_list|,
name|other
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
DECL|field|random
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|in
specifier|final
name|Collector
name|in
decl_stmt|;
DECL|field|inOrder
specifier|final
name|boolean
name|inOrder
decl_stmt|;
DECL|field|lastCollected
name|int
name|lastCollected
decl_stmt|;
DECL|method|AssertingCollector
name|AssertingCollector
parameter_list|(
name|Random
name|random
parameter_list|,
name|Collector
name|in
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|inOrder
operator|=
name|inOrder
expr_stmt|;
name|lastCollected
operator|=
operator|-
literal|1
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
name|in
operator|.
name|setScorer
argument_list|(
name|AssertingScorer
operator|.
name|getAssertingScorer
argument_list|(
name|random
argument_list|,
name|scorer
argument_list|)
argument_list|)
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
name|inOrder
operator|||
operator|!
name|acceptsDocsOutOfOrder
argument_list|()
condition|)
block|{
assert|assert
name|doc
operator|>
name|lastCollected
operator|:
literal|"Out of order : "
operator|+
name|lastCollected
operator|+
literal|" "
operator|+
name|doc
assert|;
block|}
name|in
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|lastCollected
operator|=
name|doc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|lastCollected
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|in
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
