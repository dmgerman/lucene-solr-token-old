begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package
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
name|BooleanQuery
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
name|ConstantScoreQuery
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
name|TermQuery
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
operator|.
name|Occur
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
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|search
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Utility class for creating drill-down {@link Query queries} or {@link Term  * terms} over {@link CategoryPath}. This can be used to e.g. narrow down a  * user's search to selected categories.  *<p>  *<b>NOTE:</b> if you choose to create your own {@link Query} by calling  * {@link #term}, it is recommended to wrap it with {@link ConstantScoreQuery}  * and set the {@link ConstantScoreQuery#setBoost(float) boost} to {@code 0.0f},  * so that it does not affect the scores of the documents.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|DrillDown
specifier|public
specifier|final
class|class
name|DrillDown
block|{
comment|/**    * @see #term(FacetIndexingParams, CategoryPath)    */
DECL|method|term
specifier|public
specifier|static
specifier|final
name|Term
name|term
parameter_list|(
name|FacetSearchParams
name|sParams
parameter_list|,
name|CategoryPath
name|path
parameter_list|)
block|{
return|return
name|term
argument_list|(
name|sParams
operator|.
name|indexingParams
argument_list|,
name|path
argument_list|)
return|;
block|}
comment|/** Return a drill-down {@link Term} for a category. */
DECL|method|term
specifier|public
specifier|static
specifier|final
name|Term
name|term
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|CategoryPath
name|path
parameter_list|)
block|{
name|CategoryListParams
name|clp
init|=
name|iParams
operator|.
name|getCategoryListParams
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|path
operator|.
name|fullPathLength
argument_list|()
index|]
decl_stmt|;
name|iParams
operator|.
name|drillDownTermText
argument_list|(
name|path
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
return|return
operator|new
name|Term
argument_list|(
name|clp
operator|.
name|field
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|buffer
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Wraps a given {@link Query} by a drill-down query over the given    * categories. {@link Occur} defines the relationship between the cateories    * (e.g. {@code OR} or {@code AND}. If you need to construct a more    * complicated relationship, e.g. {@code AND} of {@code ORs}), call this    * method with every group of categories with the same relationship and then    * construct a {@link BooleanQuery} which will wrap all returned queries. It    * is advised to construct that boolean query with coord disabled, and also    * wrap the final query with {@link ConstantScoreQuery} and set its boost to    * {@code 0.0f}.    *<p>    *<b>NOTE:</b> {@link Occur} only makes sense when there is more than one    * {@link CategoryPath} given.    *<p>    *<b>NOTE:</b> {@code baseQuery} can be {@code null}, in which case only the    * {@link Query} over the categories will is returned.    */
DECL|method|query
specifier|public
specifier|static
specifier|final
name|Query
name|query
parameter_list|(
name|FacetIndexingParams
name|iParams
parameter_list|,
name|Query
name|baseQuery
parameter_list|,
name|Occur
name|occur
parameter_list|,
name|CategoryPath
modifier|...
name|paths
parameter_list|)
block|{
if|if
condition|(
name|paths
operator|==
literal|null
operator|||
name|paths
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Empty category path not allowed for drill down query!"
argument_list|)
throw|;
block|}
specifier|final
name|Query
name|q
decl_stmt|;
if|if
condition|(
name|paths
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|q
operator|=
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|(
name|iParams
argument_list|,
name|paths
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// disable coord
for|for
control|(
name|CategoryPath
name|cp
range|:
name|paths
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|(
name|iParams
argument_list|,
name|cp
argument_list|)
argument_list|)
argument_list|,
name|occur
argument_list|)
expr_stmt|;
block|}
name|q
operator|=
name|bq
expr_stmt|;
block|}
specifier|final
name|ConstantScoreQuery
name|drillDownQuery
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|drillDownQuery
operator|.
name|setBoost
argument_list|(
literal|0.0f
argument_list|)
expr_stmt|;
if|if
condition|(
name|baseQuery
operator|==
literal|null
condition|)
block|{
return|return
name|drillDownQuery
return|;
block|}
else|else
block|{
name|BooleanQuery
name|res
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
name|baseQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
name|drillDownQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
comment|/**    * @see #query    */
DECL|method|query
specifier|public
specifier|static
specifier|final
name|Query
name|query
parameter_list|(
name|FacetSearchParams
name|sParams
parameter_list|,
name|Query
name|baseQuery
parameter_list|,
name|Occur
name|occur
parameter_list|,
name|CategoryPath
modifier|...
name|paths
parameter_list|)
block|{
return|return
name|query
argument_list|(
name|sParams
operator|.
name|indexingParams
argument_list|,
name|baseQuery
argument_list|,
name|occur
argument_list|,
name|paths
argument_list|)
return|;
block|}
block|}
end_class
end_unit
