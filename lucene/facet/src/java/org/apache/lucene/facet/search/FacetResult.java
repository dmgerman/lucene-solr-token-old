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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Result of faceted search.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FacetResult
specifier|public
class|class
name|FacetResult
block|{
DECL|field|facetRequest
specifier|private
specifier|final
name|FacetRequest
name|facetRequest
decl_stmt|;
DECL|field|rootNode
specifier|private
specifier|final
name|FacetResultNode
name|rootNode
decl_stmt|;
DECL|field|numValidDescendants
specifier|private
specifier|final
name|int
name|numValidDescendants
decl_stmt|;
DECL|method|FacetResult
specifier|public
name|FacetResult
parameter_list|(
name|FacetRequest
name|facetRequest
parameter_list|,
name|FacetResultNode
name|rootNode
parameter_list|,
name|int
name|numValidDescendants
parameter_list|)
block|{
name|this
operator|.
name|facetRequest
operator|=
name|facetRequest
expr_stmt|;
name|this
operator|.
name|rootNode
operator|=
name|rootNode
expr_stmt|;
name|this
operator|.
name|numValidDescendants
operator|=
name|numValidDescendants
expr_stmt|;
block|}
comment|/**    * Facet result node matching the root of the {@link #getFacetRequest() facet request}.    * @see #getFacetRequest()    * @see FacetRequest#categoryPath    */
DECL|method|getFacetResultNode
specifier|public
specifier|final
name|FacetResultNode
name|getFacetResultNode
parameter_list|()
block|{
return|return
name|rootNode
return|;
block|}
comment|/**    * Number of descendants of {@link #getFacetResultNode() root facet result    * node}, up till the requested depth.    */
DECL|method|getNumValidDescendants
specifier|public
specifier|final
name|int
name|getNumValidDescendants
parameter_list|()
block|{
return|return
name|numValidDescendants
return|;
block|}
comment|/**    * Request for which this result was obtained.    */
DECL|method|getFacetRequest
specifier|public
specifier|final
name|FacetRequest
name|getFacetRequest
parameter_list|()
block|{
return|return
name|this
operator|.
name|facetRequest
return|;
block|}
comment|/**    * String representation of this facet result.    * Use with caution: might return a very long string.    * @param prefix prefix for each result line    * @see #toString()    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|nl
init|=
literal|""
decl_stmt|;
comment|// request
if|if
condition|(
name|this
operator|.
name|facetRequest
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|nl
argument_list|)
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
literal|"Request: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|facetRequest
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|nl
operator|=
literal|"\n"
expr_stmt|;
block|}
comment|// total facets
name|sb
operator|.
name|append
argument_list|(
name|nl
argument_list|)
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
literal|"Num valid Descendants (up to specified depth): "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|numValidDescendants
argument_list|)
expr_stmt|;
name|nl
operator|=
literal|"\n"
expr_stmt|;
comment|// result node
if|if
condition|(
name|this
operator|.
name|rootNode
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|nl
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|rootNode
operator|.
name|toString
argument_list|(
name|prefix
operator|+
literal|"\t"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
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
name|toString
argument_list|(
literal|""
argument_list|)
return|;
block|}
block|}
end_class
end_unit
