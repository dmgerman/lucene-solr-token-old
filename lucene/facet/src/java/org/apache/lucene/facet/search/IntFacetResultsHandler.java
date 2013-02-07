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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|FacetRequest
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
name|results
operator|.
name|FacetResultNode
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
name|TaxonomyReader
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
name|PriorityQueue
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A {@link DepthOneFacetResultsHandler} which fills the categories values from  * {@link FacetArrays#getIntArray()}.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|IntFacetResultsHandler
specifier|public
specifier|final
class|class
name|IntFacetResultsHandler
extends|extends
name|DepthOneFacetResultsHandler
block|{
DECL|field|values
specifier|private
specifier|final
name|int
index|[]
name|values
decl_stmt|;
DECL|method|IntFacetResultsHandler
specifier|public
name|IntFacetResultsHandler
parameter_list|(
name|TaxonomyReader
name|taxonomyReader
parameter_list|,
name|FacetRequest
name|facetRequest
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|)
block|{
name|super
argument_list|(
name|taxonomyReader
argument_list|,
name|facetRequest
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|facetArrays
operator|.
name|getIntArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|valueOf
specifier|protected
specifier|final
name|double
name|valueOf
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|values
index|[
name|ordinal
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|addSiblings
specifier|protected
specifier|final
name|int
name|addSiblings
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|PriorityQueue
argument_list|<
name|FacetResultNode
argument_list|>
name|pq
parameter_list|)
block|{
name|FacetResultNode
name|top
init|=
name|pq
operator|.
name|top
argument_list|()
decl_stmt|;
name|int
name|numResults
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ordinal
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|int
name|value
init|=
name|values
index|[
name|ordinal
index|]
decl_stmt|;
if|if
condition|(
name|value
operator|>
name|top
operator|.
name|value
condition|)
block|{
name|top
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|top
operator|.
name|ordinal
operator|=
name|ordinal
expr_stmt|;
name|top
operator|=
name|pq
operator|.
name|updateTop
argument_list|()
expr_stmt|;
operator|++
name|numResults
expr_stmt|;
block|}
name|ordinal
operator|=
name|siblings
index|[
name|ordinal
index|]
expr_stmt|;
block|}
return|return
name|numResults
return|;
block|}
annotation|@
name|Override
DECL|method|addSiblings
specifier|protected
specifier|final
name|void
name|addSiblings
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|siblings
parameter_list|,
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
name|nodes
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|ordinal
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|int
name|value
init|=
name|values
index|[
name|ordinal
index|]
decl_stmt|;
if|if
condition|(
name|value
operator|>
literal|0
condition|)
block|{
name|FacetResultNode
name|node
init|=
operator|new
name|FacetResultNode
argument_list|()
decl_stmt|;
name|node
operator|.
name|label
operator|=
name|taxonomyReader
operator|.
name|getPath
argument_list|(
name|ordinal
argument_list|)
expr_stmt|;
name|node
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|ordinal
operator|=
name|siblings
index|[
name|ordinal
index|]
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
