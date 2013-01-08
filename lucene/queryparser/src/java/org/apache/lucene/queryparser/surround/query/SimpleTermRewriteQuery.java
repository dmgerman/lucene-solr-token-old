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
name|List
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
name|BooleanClause
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
begin_class
DECL|class|SimpleTermRewriteQuery
class|class
name|SimpleTermRewriteQuery
extends|extends
name|RewriteQuery
argument_list|<
name|SimpleTerm
argument_list|>
block|{
DECL|method|SimpleTermRewriteQuery
name|SimpleTermRewriteQuery
parameter_list|(
name|SimpleTerm
name|srndQuery
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|BasicQueryFactory
name|qf
parameter_list|)
block|{
name|super
argument_list|(
name|srndQuery
argument_list|,
name|fieldName
argument_list|,
name|qf
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
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|Query
argument_list|>
name|luceneSubQueries
init|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
name|srndQuery
operator|.
name|visitMatchingTerms
argument_list|(
name|reader
argument_list|,
name|fieldName
argument_list|,
operator|new
name|SimpleTerm
operator|.
name|MatchingTermVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|visitMatchingTerm
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|luceneSubQueries
operator|.
name|add
argument_list|(
name|qf
operator|.
name|newTermQuery
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
operator|(
name|luceneSubQueries
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|?
name|SrndQuery
operator|.
name|theEmptyLcnQuery
else|:
operator|(
name|luceneSubQueries
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|)
condition|?
name|luceneSubQueries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
name|SrndBooleanQuery
operator|.
name|makeBooleanQuery
argument_list|(
comment|/* luceneSubQueries all have default weight */
name|luceneSubQueries
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
return|;
comment|/* OR the subquery terms */
block|}
block|}
end_class
end_unit
