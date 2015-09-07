begin_unit
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|DrillSidewaysScorer
operator|.
name|DocsAndCost
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
name|search
operator|.
name|BulkScorer
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
name|search
operator|.
name|Collector
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
name|search
operator|.
name|ConstantScoreScorer
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
name|search
operator|.
name|DocIdSetIterator
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
name|search
operator|.
name|Explanation
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|Scorer
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
name|search
operator|.
name|Weight
import|;
end_import
begin_comment
comment|/** Only purpose is to punch through and return a  *  DrillSidewaysScorer*/
end_comment
begin_comment
comment|// TODO change the way DrillSidewaysScorer is used, this query does not work
end_comment
begin_comment
comment|// with filter caching
end_comment
begin_class
DECL|class|DrillSidewaysQuery
class|class
name|DrillSidewaysQuery
extends|extends
name|Query
block|{
DECL|field|baseQuery
specifier|final
name|Query
name|baseQuery
decl_stmt|;
DECL|field|drillDownCollector
specifier|final
name|Collector
name|drillDownCollector
decl_stmt|;
DECL|field|drillSidewaysCollectors
specifier|final
name|Collector
index|[]
name|drillSidewaysCollectors
decl_stmt|;
DECL|field|drillDownQueries
specifier|final
name|Query
index|[]
name|drillDownQueries
decl_stmt|;
DECL|field|scoreSubDocsAtOnce
specifier|final
name|boolean
name|scoreSubDocsAtOnce
decl_stmt|;
DECL|method|DrillSidewaysQuery
name|DrillSidewaysQuery
parameter_list|(
name|Query
name|baseQuery
parameter_list|,
name|Collector
name|drillDownCollector
parameter_list|,
name|Collector
index|[]
name|drillSidewaysCollectors
parameter_list|,
name|Query
index|[]
name|drillDownQueries
parameter_list|,
name|boolean
name|scoreSubDocsAtOnce
parameter_list|)
block|{
name|this
operator|.
name|baseQuery
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|baseQuery
argument_list|)
expr_stmt|;
name|this
operator|.
name|drillDownCollector
operator|=
name|drillDownCollector
expr_stmt|;
name|this
operator|.
name|drillSidewaysCollectors
operator|=
name|drillSidewaysCollectors
expr_stmt|;
name|this
operator|.
name|drillDownQueries
operator|=
name|drillDownQueries
expr_stmt|;
name|this
operator|.
name|scoreSubDocsAtOnce
operator|=
name|scoreSubDocsAtOnce
expr_stmt|;
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
literal|"DrillSidewaysQuery"
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
name|Query
name|newQuery
init|=
name|baseQuery
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Query
name|rewrittenQuery
init|=
name|newQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewrittenQuery
operator|==
name|newQuery
condition|)
block|{
break|break;
block|}
name|newQuery
operator|=
name|rewrittenQuery
expr_stmt|;
block|}
if|if
condition|(
name|newQuery
operator|==
name|baseQuery
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
return|return
operator|new
name|DrillSidewaysQuery
argument_list|(
name|newQuery
argument_list|,
name|drillDownCollector
argument_list|,
name|drillSidewaysCollectors
argument_list|,
name|drillDownQueries
argument_list|,
name|scoreSubDocsAtOnce
argument_list|)
return|;
block|}
block|}
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
name|baseWeight
init|=
name|baseQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|)
decl_stmt|;
specifier|final
name|Weight
index|[]
name|drillDowns
init|=
operator|new
name|Weight
index|[
name|drillDownQueries
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|drillDownQueries
operator|.
name|length
condition|;
name|dim
operator|++
control|)
block|{
name|drillDowns
index|[
name|dim
index|]
operator|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|drillDownQueries
index|[
name|dim
index|]
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Weight
argument_list|(
name|DrillSidewaysQuery
operator|.
name|this
argument_list|)
block|{
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
block|{}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|baseWeight
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
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|baseWeight
operator|.
name|getValueForNormalization
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
name|boost
parameter_list|)
block|{
name|baseWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|boost
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
comment|// We can only run as a top scorer:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|baseScorer
init|=
name|baseWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|DrillSidewaysScorer
operator|.
name|DocsAndCost
index|[]
name|dims
init|=
operator|new
name|DrillSidewaysScorer
operator|.
name|DocsAndCost
index|[
name|drillDowns
operator|.
name|length
index|]
decl_stmt|;
name|int
name|nullCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|dim
init|=
literal|0
init|;
name|dim
operator|<
name|dims
operator|.
name|length
condition|;
name|dim
operator|++
control|)
block|{
name|Scorer
name|scorer
init|=
name|drillDowns
index|[
name|dim
index|]
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
name|nullCount
operator|++
expr_stmt|;
name|scorer
operator|=
operator|new
name|ConstantScoreScorer
argument_list|(
name|drillDowns
index|[
name|dim
index|]
argument_list|,
literal|0f
argument_list|,
name|DocIdSetIterator
operator|.
name|empty
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dims
index|[
name|dim
index|]
operator|=
operator|new
name|DrillSidewaysScorer
operator|.
name|DocsAndCost
argument_list|(
name|scorer
argument_list|,
name|drillSidewaysCollectors
index|[
name|dim
index|]
argument_list|)
expr_stmt|;
block|}
comment|// If more than one dim has no matches, then there
comment|// are no hits nor drill-sideways counts.  Or, if we
comment|// have only one dim and that dim has no matches,
comment|// same thing.
comment|//if (nullCount> 1 || (nullCount == 1&& dims.length == 1)) {
if|if
condition|(
name|nullCount
operator|>
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Sort drill-downs by most restrictive first:
name|Arrays
operator|.
name|sort
argument_list|(
name|dims
argument_list|,
operator|new
name|Comparator
argument_list|<
name|DrillSidewaysScorer
operator|.
name|DocsAndCost
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|DocsAndCost
name|o1
parameter_list|,
name|DocsAndCost
name|o2
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|approximation
operator|.
name|cost
argument_list|()
argument_list|,
name|o2
operator|.
name|approximation
operator|.
name|cost
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|baseScorer
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
name|DrillSidewaysScorer
argument_list|(
name|context
argument_list|,
name|baseScorer
argument_list|,
name|drillDownCollector
argument_list|,
name|dims
argument_list|,
name|scoreSubDocsAtOnce
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|// TODO: these should do "deeper" equals/hash on the 2-D drillDownTerms array
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|baseQuery
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|baseQuery
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|drillDownCollector
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|drillDownCollector
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|drillDownQueries
argument_list|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|drillSidewaysCollectors
argument_list|)
expr_stmt|;
return|return
name|result
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
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|DrillSidewaysQuery
name|other
init|=
operator|(
name|DrillSidewaysQuery
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|baseQuery
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|baseQuery
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|baseQuery
operator|.
name|equals
argument_list|(
name|other
operator|.
name|baseQuery
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|drillDownCollector
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|drillDownCollector
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|drillDownCollector
operator|.
name|equals
argument_list|(
name|other
operator|.
name|drillDownCollector
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|drillDownQueries
argument_list|,
name|other
operator|.
name|drillDownQueries
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|drillSidewaysCollectors
argument_list|,
name|other
operator|.
name|drillSidewaysCollectors
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
