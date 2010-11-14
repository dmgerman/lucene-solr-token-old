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
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|HashMap
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
name|PriorityQueue
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Fields
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
name|Terms
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
name|queryParser
operator|.
name|QueryParser
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
name|spans
operator|.
name|SpanOrQuery
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
name|spans
operator|.
name|SpanTermQuery
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
name|AttributeSource
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
begin_class
DECL|class|ConstantScoreAutoRewrite
class|class
name|ConstantScoreAutoRewrite
extends|extends
name|TermCollectingRewrite
argument_list|<
name|BooleanQuery
argument_list|>
block|{
comment|// Defaults derived from rough tests with a 20.0 million
comment|// doc Wikipedia index.  With more than 350 terms in the
comment|// query, the filter method is fastest:
DECL|field|DEFAULT_TERM_COUNT_CUTOFF
specifier|public
specifier|static
name|int
name|DEFAULT_TERM_COUNT_CUTOFF
init|=
literal|350
decl_stmt|;
comment|// If the query will hit more than 1 in 1000 of the docs
comment|// in the index (0.1%), the filter method is fastest:
DECL|field|DEFAULT_DOC_COUNT_PERCENT
specifier|public
specifier|static
name|double
name|DEFAULT_DOC_COUNT_PERCENT
init|=
literal|0.1
decl_stmt|;
DECL|field|termCountCutoff
specifier|private
name|int
name|termCountCutoff
init|=
name|DEFAULT_TERM_COUNT_CUTOFF
decl_stmt|;
DECL|field|docCountPercent
specifier|private
name|double
name|docCountPercent
init|=
name|DEFAULT_DOC_COUNT_PERCENT
decl_stmt|;
comment|/** If the number of terms in this query is equal to or    *  larger than this setting then {@link    *  #CONSTANT_SCORE_FILTER_REWRITE} is used. */
DECL|method|setTermCountCutoff
specifier|public
name|void
name|setTermCountCutoff
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|termCountCutoff
operator|=
name|count
expr_stmt|;
block|}
comment|/** @see #setTermCountCutoff */
DECL|method|getTermCountCutoff
specifier|public
name|int
name|getTermCountCutoff
parameter_list|()
block|{
return|return
name|termCountCutoff
return|;
block|}
comment|/** If the number of documents to be visited in the    *  postings exceeds this specified percentage of the    *  maxDoc() for the index, then {@link    *  #CONSTANT_SCORE_FILTER_REWRITE} is used.    *  @param percent 0.0 to 100.0 */
DECL|method|setDocCountPercent
specifier|public
name|void
name|setDocCountPercent
parameter_list|(
name|double
name|percent
parameter_list|)
block|{
name|docCountPercent
operator|=
name|percent
expr_stmt|;
block|}
comment|/** @see #setDocCountPercent */
DECL|method|getDocCountPercent
specifier|public
name|double
name|getDocCountPercent
parameter_list|()
block|{
return|return
name|docCountPercent
return|;
block|}
annotation|@
name|Override
DECL|method|getTopLevelQuery
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
DECL|method|addClause
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
name|docFreq
parameter_list|,
name|float
name|boost
comment|/*ignored*/
parameter_list|)
block|{
name|topLevel
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|,
name|docFreq
argument_list|)
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
DECL|method|rewrite
specifier|public
name|Query
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
comment|// Get the enum and start visiting terms.  If we
comment|// exhaust the enum before hitting either of the
comment|// cutoffs, we use ConstantBooleanQueryRewrite; else,
comment|// ConstantFilterRewrite:
specifier|final
name|int
name|docCountCutoff
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|docCountPercent
operator|/
literal|100.
operator|)
operator|*
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|termCountLimit
init|=
name|Math
operator|.
name|min
argument_list|(
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
argument_list|,
name|termCountCutoff
argument_list|)
decl_stmt|;
specifier|final
name|CutOffTermCollector
name|col
init|=
operator|new
name|CutOffTermCollector
argument_list|(
name|docCountCutoff
argument_list|,
name|termCountLimit
argument_list|)
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
name|pendingTerms
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|col
operator|.
name|hasCutOff
condition|)
block|{
return|return
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_FILTER_REWRITE
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|,
name|query
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
name|getTopLevelQuery
argument_list|()
return|;
block|}
else|else
block|{
specifier|final
name|BooleanQuery
name|bq
init|=
name|getTopLevelQuery
argument_list|()
decl_stmt|;
specifier|final
name|Term
name|placeholderTerm
init|=
operator|new
name|Term
argument_list|(
name|query
operator|.
name|field
argument_list|)
decl_stmt|;
specifier|final
name|BytesRefHash
name|pendingTerms
init|=
name|col
operator|.
name|pendingTerms
decl_stmt|;
specifier|final
name|int
name|sort
index|[]
init|=
name|pendingTerms
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
comment|// docFreq is not used for constant score here, we pass 1
comment|// to explicitely set a fake value, so it's not calculated
name|addClause
argument_list|(
name|bq
argument_list|,
name|placeholderTerm
operator|.
name|createTerm
argument_list|(
name|pendingTerms
operator|.
name|get
argument_list|(
name|sort
index|[
name|i
index|]
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
comment|// Strip scores
specifier|final
name|Query
name|result
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
name|bq
argument_list|)
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
name|query
operator|.
name|incTotalNumberOfTerms
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
DECL|class|CutOffTermCollector
specifier|static
specifier|final
class|class
name|CutOffTermCollector
extends|extends
name|TermCollector
block|{
DECL|method|CutOffTermCollector
name|CutOffTermCollector
parameter_list|(
name|int
name|docCountCutoff
parameter_list|,
name|int
name|termCountLimit
parameter_list|)
block|{
name|this
operator|.
name|docCountCutoff
operator|=
name|docCountCutoff
expr_stmt|;
name|this
operator|.
name|termCountLimit
operator|=
name|termCountLimit
expr_stmt|;
block|}
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
name|pendingTerms
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|docVisitCount
operator|+=
name|termsEnum
operator|.
name|docFreq
argument_list|()
expr_stmt|;
if|if
condition|(
name|pendingTerms
operator|.
name|size
argument_list|()
operator|>=
name|termCountLimit
operator|||
name|docVisitCount
operator|>=
name|docCountCutoff
condition|)
block|{
name|hasCutOff
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|field|docVisitCount
name|int
name|docVisitCount
init|=
literal|0
decl_stmt|;
DECL|field|hasCutOff
name|boolean
name|hasCutOff
init|=
literal|false
decl_stmt|;
DECL|field|termsEnum
name|TermsEnum
name|termsEnum
decl_stmt|;
DECL|field|docCountCutoff
DECL|field|termCountLimit
specifier|final
name|int
name|docCountCutoff
decl_stmt|,
name|termCountLimit
decl_stmt|;
DECL|field|pendingTerms
specifier|final
name|BytesRefHash
name|pendingTerms
init|=
operator|new
name|BytesRefHash
argument_list|()
decl_stmt|;
block|}
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
literal|1279
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|prime
operator|*
name|termCountCutoff
operator|+
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|docCountPercent
argument_list|)
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
name|obj
operator|==
literal|null
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
name|ConstantScoreAutoRewrite
name|other
init|=
operator|(
name|ConstantScoreAutoRewrite
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|other
operator|.
name|termCountCutoff
operator|!=
name|termCountCutoff
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|other
operator|.
name|docCountPercent
argument_list|)
operator|!=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|docCountPercent
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
