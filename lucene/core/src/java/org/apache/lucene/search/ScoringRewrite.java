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
name|index
operator|.
name|TermState
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
name|TermsEnum
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
name|MultiTermQuery
operator|.
name|RewriteMethod
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
name|ByteBlockPool
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
name|BytesRef
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
name|BytesRefHash
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
name|TermContext
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
name|BytesRefHash
operator|.
name|DirectBytesStartArray
import|;
end_import
begin_comment
comment|/** @lucene.internal Only public to be accessible by spans package. */
end_comment
begin_class
DECL|class|ScoringRewrite
specifier|public
specifier|abstract
class|class
name|ScoringRewrite
parameter_list|<
name|Q
extends|extends
name|Query
parameter_list|>
extends|extends
name|TermCollectingRewrite
argument_list|<
name|Q
argument_list|>
block|{
comment|/** A rewrite method that first translates each term into    *  {@link BooleanClause.Occur#SHOULD} clause in a    *  BooleanQuery, and keeps the scores as computed by the    *  query.  Note that typically such scores are    *  meaningless to the user, and require non-trivial CPU    *  to compute, so it's almost always better to use {@link    *  MultiTermQuery#CONSTANT_SCORE_AUTO_REWRITE_DEFAULT} instead.    *    *<p><b>NOTE</b>: This rewrite method will hit {@link    *  BooleanQuery.TooManyClauses} if the number of terms    *  exceeds {@link BooleanQuery#getMaxClauseCount}.    *    *  @see MultiTermQuery#setRewriteMethod */
DECL|field|SCORING_BOOLEAN_QUERY_REWRITE
specifier|public
specifier|final
specifier|static
name|ScoringRewrite
argument_list|<
name|BooleanQuery
argument_list|>
name|SCORING_BOOLEAN_QUERY_REWRITE
init|=
operator|new
name|ScoringRewrite
argument_list|<
name|BooleanQuery
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|BooleanQuery
name|getTopLevelQuery
parameter_list|()
block|{
return|return
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addClause
parameter_list|(
name|BooleanQuery
name|topLevel
parameter_list|,
name|Term
name|term
parameter_list|,
name|int
name|docCount
parameter_list|,
name|float
name|boost
parameter_list|,
name|TermContext
name|states
parameter_list|)
block|{
specifier|final
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|,
name|states
argument_list|)
decl_stmt|;
name|tq
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|topLevel
operator|.
name|add
argument_list|(
name|tq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|checkMaxClauseCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|count
operator|>
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
condition|)
throw|throw
operator|new
name|BooleanQuery
operator|.
name|TooManyClauses
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
comment|/** Like {@link #SCORING_BOOLEAN_QUERY_REWRITE} except    *  scores are not computed.  Instead, each matching    *  document receives a constant score equal to the    *  query's boost.    *     *<p><b>NOTE</b>: This rewrite method will hit {@link    *  BooleanQuery.TooManyClauses} if the number of terms    *  exceeds {@link BooleanQuery#getMaxClauseCount}.    *    *  @see MultiTermQuery#setRewriteMethod */
DECL|field|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
specifier|public
specifier|final
specifier|static
name|RewriteMethod
name|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
init|=
operator|new
name|RewriteMethod
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BooleanQuery
name|bq
init|=
name|SCORING_BOOLEAN_QUERY_REWRITE
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|,
name|query
argument_list|)
decl_stmt|;
comment|// TODO: if empty boolean query return NullQuery?
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
return|return
name|bq
return|;
comment|// strip the scores off
specifier|final
name|Query
name|result
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|bq
argument_list|)
decl_stmt|;
name|result
operator|.
name|setBoost
argument_list|(
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
decl_stmt|;
comment|/** This method is called after every new term to check if the number of max clauses    * (e.g. in BooleanQuery) is not exceeded. Throws the corresponding {@link RuntimeException}. */
DECL|method|checkMaxClauseCount
specifier|protected
specifier|abstract
name|void
name|checkMaxClauseCount
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|rewrite
specifier|public
specifier|final
name|Q
name|rewrite
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|MultiTermQuery
name|query
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Q
name|result
init|=
name|getTopLevelQuery
argument_list|()
decl_stmt|;
specifier|final
name|ParallelArraysTermCollector
name|col
init|=
operator|new
name|ParallelArraysTermCollector
argument_list|()
decl_stmt|;
name|collectTerms
argument_list|(
name|reader
argument_list|,
name|query
argument_list|,
name|col
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|col
operator|.
name|terms
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|sort
index|[]
init|=
name|col
operator|.
name|terms
operator|.
name|sort
argument_list|(
name|col
operator|.
name|termsEnum
operator|.
name|getComparator
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|float
index|[]
name|boost
init|=
name|col
operator|.
name|array
operator|.
name|boost
decl_stmt|;
specifier|final
name|TermContext
index|[]
name|termStates
init|=
name|col
operator|.
name|array
operator|.
name|termState
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
name|size
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|pos
init|=
name|sort
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|,
name|col
operator|.
name|terms
operator|.
name|get
argument_list|(
name|pos
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
operator|==
name|termStates
index|[
name|pos
index|]
operator|.
name|docFreq
argument_list|()
assert|;
name|addClause
argument_list|(
name|result
argument_list|,
name|term
argument_list|,
name|termStates
index|[
name|pos
index|]
operator|.
name|docFreq
argument_list|()
argument_list|,
name|query
operator|.
name|getBoost
argument_list|()
operator|*
name|boost
index|[
name|pos
index|]
argument_list|,
name|termStates
index|[
name|pos
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|class|ParallelArraysTermCollector
specifier|final
class|class
name|ParallelArraysTermCollector
extends|extends
name|TermCollector
block|{
DECL|field|array
specifier|final
name|TermFreqBoostByteStart
name|array
init|=
operator|new
name|TermFreqBoostByteStart
argument_list|(
literal|16
argument_list|)
decl_stmt|;
DECL|field|terms
specifier|final
name|BytesRefHash
name|terms
init|=
operator|new
name|BytesRefHash
argument_list|(
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|ByteBlockPool
operator|.
name|DirectAllocator
argument_list|()
argument_list|)
argument_list|,
literal|16
argument_list|,
name|array
argument_list|)
decl_stmt|;
DECL|field|termsEnum
name|TermsEnum
name|termsEnum
decl_stmt|;
DECL|field|boostAtt
specifier|private
name|BoostAttribute
name|boostAtt
decl_stmt|;
annotation|@
name|Override
DECL|method|setNextEnum
specifier|public
name|void
name|setNextEnum
parameter_list|(
name|TermsEnum
name|termsEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|termsEnum
operator|=
name|termsEnum
expr_stmt|;
name|this
operator|.
name|boostAtt
operator|=
name|termsEnum
operator|.
name|attributes
argument_list|()
operator|.
name|addAttribute
argument_list|(
name|BoostAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|boolean
name|collect
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|e
init|=
name|terms
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
specifier|final
name|TermState
name|state
init|=
name|termsEnum
operator|.
name|termState
argument_list|()
decl_stmt|;
assert|assert
name|state
operator|!=
literal|null
assert|;
if|if
condition|(
name|e
operator|<
literal|0
condition|)
block|{
comment|// duplicate term: update docFreq
specifier|final
name|int
name|pos
init|=
operator|(
operator|-
name|e
operator|)
operator|-
literal|1
decl_stmt|;
name|array
operator|.
name|termState
index|[
name|pos
index|]
operator|.
name|register
argument_list|(
name|state
argument_list|,
name|readerContext
operator|.
name|ord
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|array
operator|.
name|boost
index|[
name|pos
index|]
operator|==
name|boostAtt
operator|.
name|getBoost
argument_list|()
operator|:
literal|"boost should be equal in all segment TermsEnums"
assert|;
block|}
else|else
block|{
comment|// new entry: we populate the entry initially
name|array
operator|.
name|boost
index|[
name|e
index|]
operator|=
name|boostAtt
operator|.
name|getBoost
argument_list|()
expr_stmt|;
name|array
operator|.
name|termState
index|[
name|e
index|]
operator|=
operator|new
name|TermContext
argument_list|(
name|topReaderContext
argument_list|,
name|state
argument_list|,
name|readerContext
operator|.
name|ord
argument_list|,
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|,
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
argument_list|)
expr_stmt|;
name|ScoringRewrite
operator|.
name|this
operator|.
name|checkMaxClauseCount
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
comment|/** Special implementation of BytesStartArray that keeps parallel arrays for boost and docFreq */
DECL|class|TermFreqBoostByteStart
specifier|static
specifier|final
class|class
name|TermFreqBoostByteStart
extends|extends
name|DirectBytesStartArray
block|{
DECL|field|boost
name|float
index|[]
name|boost
decl_stmt|;
DECL|field|termState
name|TermContext
index|[]
name|termState
decl_stmt|;
DECL|method|TermFreqBoostByteStart
specifier|public
name|TermFreqBoostByteStart
parameter_list|(
name|int
name|initSize
parameter_list|)
block|{
name|super
argument_list|(
name|initSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|int
index|[]
name|init
parameter_list|()
block|{
specifier|final
name|int
index|[]
name|ord
init|=
name|super
operator|.
name|init
argument_list|()
decl_stmt|;
name|boost
operator|=
operator|new
name|float
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|ord
operator|.
name|length
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_FLOAT
argument_list|)
index|]
expr_stmt|;
name|termState
operator|=
operator|new
name|TermContext
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|ord
operator|.
name|length
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
expr_stmt|;
assert|assert
name|termState
operator|.
name|length
operator|>=
name|ord
operator|.
name|length
operator|&&
name|boost
operator|.
name|length
operator|>=
name|ord
operator|.
name|length
assert|;
return|return
name|ord
return|;
block|}
annotation|@
name|Override
DECL|method|grow
specifier|public
name|int
index|[]
name|grow
parameter_list|()
block|{
specifier|final
name|int
index|[]
name|ord
init|=
name|super
operator|.
name|grow
argument_list|()
decl_stmt|;
name|boost
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|boost
argument_list|,
name|ord
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|termState
operator|.
name|length
operator|<
name|ord
operator|.
name|length
condition|)
block|{
name|TermContext
index|[]
name|tmpTermState
init|=
operator|new
name|TermContext
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|ord
operator|.
name|length
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|termState
argument_list|,
literal|0
argument_list|,
name|tmpTermState
argument_list|,
literal|0
argument_list|,
name|termState
operator|.
name|length
argument_list|)
expr_stmt|;
name|termState
operator|=
name|tmpTermState
expr_stmt|;
block|}
assert|assert
name|termState
operator|.
name|length
operator|>=
name|ord
operator|.
name|length
operator|&&
name|boost
operator|.
name|length
operator|>=
name|ord
operator|.
name|length
assert|;
return|return
name|ord
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|int
index|[]
name|clear
parameter_list|()
block|{
name|boost
operator|=
literal|null
expr_stmt|;
name|termState
operator|=
literal|null
expr_stmt|;
return|return
name|super
operator|.
name|clear
argument_list|()
return|;
block|}
block|}
block|}
end_class
end_unit
