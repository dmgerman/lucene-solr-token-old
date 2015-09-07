begin_unit
begin_package
DECL|package|org.apache.lucene.facet.range
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|range
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
name|Collections
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|ConstantScoreWeight
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
name|TwoPhaseIterator
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
comment|/** Represents a range over long values.  *  * @lucene.experimental */
end_comment
begin_class
DECL|class|LongRange
specifier|public
specifier|final
class|class
name|LongRange
extends|extends
name|Range
block|{
DECL|field|minIncl
specifier|final
name|long
name|minIncl
decl_stmt|;
DECL|field|maxIncl
specifier|final
name|long
name|maxIncl
decl_stmt|;
comment|/** Minimum. */
DECL|field|min
specifier|public
specifier|final
name|long
name|min
decl_stmt|;
comment|/** Maximum. */
DECL|field|max
specifier|public
specifier|final
name|long
name|max
decl_stmt|;
comment|/** True if the minimum value is inclusive. */
DECL|field|minInclusive
specifier|public
specifier|final
name|boolean
name|minInclusive
decl_stmt|;
comment|/** True if the maximum value is inclusive. */
DECL|field|maxInclusive
specifier|public
specifier|final
name|boolean
name|maxInclusive
decl_stmt|;
comment|// TODO: can we require fewer args? (same for
comment|// Double/FloatRange too)
comment|/** Create a LongRange. */
DECL|method|LongRange
specifier|public
name|LongRange
parameter_list|(
name|String
name|label
parameter_list|,
name|long
name|minIn
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|long
name|maxIn
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|super
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|this
operator|.
name|min
operator|=
name|minIn
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|maxIn
expr_stmt|;
name|this
operator|.
name|minInclusive
operator|=
name|minInclusive
expr_stmt|;
name|this
operator|.
name|maxInclusive
operator|=
name|maxInclusive
expr_stmt|;
if|if
condition|(
operator|!
name|minInclusive
condition|)
block|{
if|if
condition|(
name|minIn
operator|!=
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|minIn
operator|++
expr_stmt|;
block|}
else|else
block|{
name|failNoMatch
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|maxInclusive
condition|)
block|{
if|if
condition|(
name|maxIn
operator|!=
name|Long
operator|.
name|MIN_VALUE
condition|)
block|{
name|maxIn
operator|--
expr_stmt|;
block|}
else|else
block|{
name|failNoMatch
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|minIn
operator|>
name|maxIn
condition|)
block|{
name|failNoMatch
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|minIncl
operator|=
name|minIn
expr_stmt|;
name|this
operator|.
name|maxIncl
operator|=
name|maxIn
expr_stmt|;
block|}
comment|/** True if this range accepts the provided value. */
DECL|method|accept
specifier|public
name|boolean
name|accept
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|value
operator|>=
name|minIncl
operator|&&
name|value
operator|<=
name|maxIncl
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"LongRange("
operator|+
name|minIncl
operator|+
literal|" to "
operator|+
name|maxIncl
operator|+
literal|")"
return|;
block|}
DECL|class|ValueSourceQuery
specifier|private
specifier|static
class|class
name|ValueSourceQuery
extends|extends
name|Query
block|{
DECL|field|range
specifier|private
specifier|final
name|LongRange
name|range
decl_stmt|;
DECL|field|fastMatchQuery
specifier|private
specifier|final
name|Query
name|fastMatchQuery
decl_stmt|;
DECL|field|valueSource
specifier|private
specifier|final
name|ValueSource
name|valueSource
decl_stmt|;
DECL|method|ValueSourceQuery
name|ValueSourceQuery
parameter_list|(
name|LongRange
name|range
parameter_list|,
name|Query
name|fastMatchQuery
parameter_list|,
name|ValueSource
name|valueSource
parameter_list|)
block|{
name|this
operator|.
name|range
operator|=
name|range
expr_stmt|;
name|this
operator|.
name|fastMatchQuery
operator|=
name|fastMatchQuery
expr_stmt|;
name|this
operator|.
name|valueSource
operator|=
name|valueSource
expr_stmt|;
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
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ValueSourceQuery
name|other
init|=
operator|(
name|ValueSourceQuery
operator|)
name|obj
decl_stmt|;
return|return
name|range
operator|.
name|equals
argument_list|(
name|other
operator|.
name|range
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|fastMatchQuery
argument_list|,
name|other
operator|.
name|fastMatchQuery
argument_list|)
operator|&&
name|valueSource
operator|.
name|equals
argument_list|(
name|other
operator|.
name|valueSource
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
literal|31
operator|*
name|Objects
operator|.
name|hash
argument_list|(
name|range
argument_list|,
name|fastMatchQuery
argument_list|,
name|valueSource
argument_list|)
operator|+
name|super
operator|.
name|hashCode
argument_list|()
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
literal|"Filter("
operator|+
name|range
operator|.
name|toString
argument_list|()
operator|+
literal|")"
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
if|if
condition|(
name|fastMatchQuery
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Query
name|fastMatchRewritten
init|=
name|fastMatchQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|fastMatchRewritten
operator|!=
name|fastMatchQuery
condition|)
block|{
return|return
operator|new
name|ValueSourceQuery
argument_list|(
name|range
argument_list|,
name|fastMatchRewritten
argument_list|,
name|valueSource
argument_list|)
return|;
block|}
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
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
name|fastMatchWeight
init|=
name|fastMatchQuery
operator|==
literal|null
condition|?
literal|null
else|:
name|searcher
operator|.
name|createWeight
argument_list|(
name|fastMatchQuery
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConstantScoreWeight
argument_list|(
name|this
argument_list|)
block|{
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
specifier|final
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|approximation
decl_stmt|;
if|if
condition|(
name|fastMatchWeight
operator|==
literal|null
condition|)
block|{
name|approximation
operator|=
name|DocIdSetIterator
operator|.
name|all
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|approximation
operator|=
name|fastMatchWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|approximation
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|final
name|FunctionValues
name|values
init|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|TwoPhaseIterator
name|twoPhase
init|=
operator|new
name|TwoPhaseIterator
argument_list|(
name|approximation
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|range
operator|.
name|accept
argument_list|(
name|values
operator|.
name|longVal
argument_list|(
name|approximation
operator|.
name|docID
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|twoPhase
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|(
specifier|final
name|Query
name|fastMatchQuery
parameter_list|,
specifier|final
name|ValueSource
name|valueSource
parameter_list|)
block|{
return|return
operator|new
name|ValueSourceQuery
argument_list|(
name|this
argument_list|,
name|fastMatchQuery
argument_list|,
name|valueSource
argument_list|)
return|;
block|}
block|}
end_class
end_unit
