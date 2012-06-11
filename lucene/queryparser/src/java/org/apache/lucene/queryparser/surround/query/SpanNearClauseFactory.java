begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|query
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/* SpanNearClauseFactory:  Operations:  - create for a field name and an indexreader.  - add a weighted Term   this should add a corresponding SpanTermQuery, or   increase the weight of an existing one.    - add a weighted subquery SpanNearQuery   - create a clause for SpanNearQuery from the things added above.   For this, create an array of SpanQuery's from the added ones.   The clause normally is a SpanOrQuery over the added subquery SpanNearQuery   the SpanTermQuery's for the added Term's */
end_comment
begin_comment
comment|/* When  it is necessary to suppress double subqueries as much as possible:    hashCode() and equals() on unweighted SpanQuery are needed (possibly via getTerms(),    the terms are individually hashable).    Idem SpanNearQuery: hash on the subqueries and the slop.    Evt. merge SpanNearQuery's by adding the weights of the corresponding subqueries.  */
end_comment
begin_comment
comment|/* To be determined:    Are SpanQuery weights handled correctly during search by Lucene?    Should the resulting SpanOrQuery be sorted?    Could other SpanQueries be added for use in this factory:    - SpanOrQuery: in principle yes, but it only has access to it's terms                   via getTerms(); are the corresponding weights available?    - SpanFirstQuery: treat similar to subquery SpanNearQuery. (ok?)    - SpanNotQuery: treat similar to subquery SpanNearQuery. (ok?)  */
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
name|Iterator
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
name|spans
operator|.
name|SpanNearQuery
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
name|SpanQuery
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
begin_class
DECL|class|SpanNearClauseFactory
specifier|public
class|class
name|SpanNearClauseFactory
block|{
comment|// FIXME: rename to SpanClauseFactory
DECL|method|SpanNearClauseFactory
specifier|public
name|SpanNearClauseFactory
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|BasicQueryFactory
name|qf
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|weightBySpanQuery
operator|=
operator|new
name|HashMap
argument_list|<
name|SpanQuery
argument_list|,
name|Float
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|qf
operator|=
name|qf
expr_stmt|;
block|}
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|field|weightBySpanQuery
specifier|private
name|HashMap
argument_list|<
name|SpanQuery
argument_list|,
name|Float
argument_list|>
name|weightBySpanQuery
decl_stmt|;
DECL|field|qf
specifier|private
name|BasicQueryFactory
name|qf
decl_stmt|;
DECL|method|getIndexReader
specifier|public
name|IndexReader
name|getIndexReader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
DECL|method|getBasicQueryFactory
specifier|public
name|BasicQueryFactory
name|getBasicQueryFactory
parameter_list|()
block|{
return|return
name|qf
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|weightBySpanQuery
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|weightBySpanQuery
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|addSpanQueryWeighted
specifier|protected
name|void
name|addSpanQueryWeighted
parameter_list|(
name|SpanQuery
name|sq
parameter_list|,
name|float
name|weight
parameter_list|)
block|{
name|Float
name|w
init|=
name|weightBySpanQuery
operator|.
name|get
argument_list|(
name|sq
argument_list|)
decl_stmt|;
if|if
condition|(
name|w
operator|!=
literal|null
condition|)
name|w
operator|=
name|Float
operator|.
name|valueOf
argument_list|(
name|w
operator|.
name|floatValue
argument_list|()
operator|+
name|weight
argument_list|)
expr_stmt|;
else|else
name|w
operator|=
name|Float
operator|.
name|valueOf
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|weightBySpanQuery
operator|.
name|put
argument_list|(
name|sq
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
DECL|method|addTermWeighted
specifier|public
name|void
name|addTermWeighted
parameter_list|(
name|Term
name|t
parameter_list|,
name|float
name|weight
parameter_list|)
throws|throws
name|IOException
block|{
name|SpanTermQuery
name|stq
init|=
name|qf
operator|.
name|newSpanTermQuery
argument_list|(
name|t
argument_list|)
decl_stmt|;
comment|/* CHECKME: wrap in Hashable...? */
name|addSpanQueryWeighted
argument_list|(
name|stq
argument_list|,
name|weight
argument_list|)
expr_stmt|;
block|}
DECL|method|addSpanQuery
specifier|public
name|void
name|addSpanQuery
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
if|if
condition|(
name|q
operator|==
name|SrndQuery
operator|.
name|theEmptyLcnQuery
condition|)
return|return;
if|if
condition|(
operator|!
operator|(
name|q
operator|instanceof
name|SpanQuery
operator|)
condition|)
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Expected SpanQuery: "
operator|+
name|q
operator|.
name|toString
argument_list|(
name|getFieldName
argument_list|()
argument_list|)
argument_list|)
throw|;
name|addSpanQueryWeighted
argument_list|(
operator|(
name|SpanQuery
operator|)
name|q
argument_list|,
name|q
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|makeSpanClause
specifier|public
name|SpanQuery
name|makeSpanClause
parameter_list|()
block|{
name|SpanQuery
index|[]
name|spanQueries
init|=
operator|new
name|SpanQuery
index|[
name|size
argument_list|()
index|]
decl_stmt|;
name|Iterator
argument_list|<
name|SpanQuery
argument_list|>
name|sqi
init|=
name|weightBySpanQuery
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|sqi
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SpanQuery
name|sq
init|=
name|sqi
operator|.
name|next
argument_list|()
decl_stmt|;
name|sq
operator|.
name|setBoost
argument_list|(
name|weightBySpanQuery
operator|.
name|get
argument_list|(
name|sq
argument_list|)
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
name|spanQueries
index|[
name|i
operator|++
index|]
operator|=
name|sq
expr_stmt|;
block|}
if|if
condition|(
name|spanQueries
operator|.
name|length
operator|==
literal|1
condition|)
return|return
name|spanQueries
index|[
literal|0
index|]
return|;
else|else
return|return
operator|new
name|SpanOrQuery
argument_list|(
name|spanQueries
argument_list|)
return|;
block|}
block|}
end_class
end_unit
