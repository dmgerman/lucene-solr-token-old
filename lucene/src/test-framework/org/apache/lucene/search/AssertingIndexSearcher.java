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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|concurrent
operator|.
name|ExecutorService
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
name|IndexReader
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
name|IndexReader
operator|.
name|ReaderContext
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
begin_comment
comment|/**   * Helper class that adds some extra checks to ensure correct  * usage of {@code IndexSearcher} and {@code Weight}.  * TODO: Extend this by more checks, that's just a start.  */
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
name|ReaderContext
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
name|ReaderContext
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
name|Weight
argument_list|()
block|{
annotation|@
name|Override
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
name|w
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
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|w
operator|.
name|getQuery
argument_list|()
return|;
block|}
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
return|return
name|w
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
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
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
return|return
name|w
operator|.
name|scoresDocsOutOfOrder
argument_list|()
return|;
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
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|useRandomAccess
parameter_list|(
name|Bits
name|bits
parameter_list|,
name|int
name|firstFilterDoc
parameter_list|)
block|{
return|return
name|random
operator|.
name|nextBoolean
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
