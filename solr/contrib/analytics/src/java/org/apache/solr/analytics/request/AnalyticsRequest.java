begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|request
package|;
end_package
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
name|HashSet
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
name|Set
import|;
end_import
begin_comment
comment|/**  * Contains the specifications of an Analytics Request, specifically a name,  * a list of Expressions, a list of field facets, a list of range facets, a list of query facets  * and the list of expressions and their results calculated in previous AnalyticsRequests.  */
end_comment
begin_class
DECL|class|AnalyticsRequest
specifier|public
class|class
name|AnalyticsRequest
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|expressions
specifier|private
name|List
argument_list|<
name|ExpressionRequest
argument_list|>
name|expressions
decl_stmt|;
DECL|field|hiddenExpressions
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|hiddenExpressions
decl_stmt|;
DECL|field|fieldFacets
specifier|private
name|List
argument_list|<
name|FieldFacetRequest
argument_list|>
name|fieldFacets
decl_stmt|;
DECL|field|rangeFacets
specifier|private
name|List
argument_list|<
name|RangeFacetRequest
argument_list|>
name|rangeFacets
decl_stmt|;
DECL|field|queryFacets
specifier|private
name|List
argument_list|<
name|QueryFacetRequest
argument_list|>
name|queryFacets
decl_stmt|;
DECL|method|AnalyticsRequest
specifier|public
name|AnalyticsRequest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|expressions
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|hiddenExpressions
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|fieldFacets
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|rangeFacets
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|queryFacets
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setExpressions
specifier|public
name|void
name|setExpressions
parameter_list|(
name|List
argument_list|<
name|ExpressionRequest
argument_list|>
name|expressions
parameter_list|)
block|{
name|this
operator|.
name|expressions
operator|=
name|expressions
expr_stmt|;
block|}
DECL|method|addExpression
specifier|public
name|void
name|addExpression
parameter_list|(
name|ExpressionRequest
name|expressionRequest
parameter_list|)
block|{
name|expressions
operator|.
name|add
argument_list|(
name|expressionRequest
argument_list|)
expr_stmt|;
block|}
DECL|method|getExpressions
specifier|public
name|List
argument_list|<
name|ExpressionRequest
argument_list|>
name|getExpressions
parameter_list|()
block|{
return|return
name|expressions
return|;
block|}
DECL|method|addHiddenExpression
specifier|public
name|void
name|addHiddenExpression
parameter_list|(
name|ExpressionRequest
name|expressionRequest
parameter_list|)
block|{
name|expressions
operator|.
name|add
argument_list|(
name|expressionRequest
argument_list|)
expr_stmt|;
name|hiddenExpressions
operator|.
name|add
argument_list|(
name|expressionRequest
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getHiddenExpressions
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getHiddenExpressions
parameter_list|()
block|{
return|return
name|hiddenExpressions
return|;
block|}
DECL|method|setFieldFacets
specifier|public
name|void
name|setFieldFacets
parameter_list|(
name|List
argument_list|<
name|FieldFacetRequest
argument_list|>
name|fieldFacets
parameter_list|)
block|{
name|this
operator|.
name|fieldFacets
operator|=
name|fieldFacets
expr_stmt|;
block|}
DECL|method|getFieldFacets
specifier|public
name|List
argument_list|<
name|FieldFacetRequest
argument_list|>
name|getFieldFacets
parameter_list|()
block|{
return|return
name|fieldFacets
return|;
block|}
DECL|method|setRangeFacets
specifier|public
name|void
name|setRangeFacets
parameter_list|(
name|List
argument_list|<
name|RangeFacetRequest
argument_list|>
name|rangeFacets
parameter_list|)
block|{
name|this
operator|.
name|rangeFacets
operator|=
name|rangeFacets
expr_stmt|;
block|}
DECL|method|getRangeFacets
specifier|public
name|List
argument_list|<
name|RangeFacetRequest
argument_list|>
name|getRangeFacets
parameter_list|()
block|{
return|return
name|rangeFacets
return|;
block|}
DECL|method|setQueryFacets
specifier|public
name|void
name|setQueryFacets
parameter_list|(
name|List
argument_list|<
name|QueryFacetRequest
argument_list|>
name|queryFacets
parameter_list|)
block|{
name|this
operator|.
name|queryFacets
operator|=
name|queryFacets
expr_stmt|;
block|}
DECL|method|getQueryFacets
specifier|public
name|List
argument_list|<
name|QueryFacetRequest
argument_list|>
name|getQueryFacets
parameter_list|()
block|{
return|return
name|queryFacets
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
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"<AnalyticsRequest name="
operator|+
name|name
operator|+
literal|">"
argument_list|)
decl_stmt|;
for|for
control|(
name|ExpressionRequest
name|exp
range|:
name|expressions
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|exp
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FieldFacetRequest
name|facet
range|:
name|fieldFacets
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|facet
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RangeFacetRequest
name|facet
range|:
name|rangeFacets
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|facet
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|QueryFacetRequest
name|facet
range|:
name|queryFacets
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|facet
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"</AnalyticsRequest>"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit