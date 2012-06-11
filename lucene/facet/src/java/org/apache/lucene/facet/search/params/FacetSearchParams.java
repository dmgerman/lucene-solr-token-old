begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search.params
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
operator|.
name|params
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
name|List
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
name|DefaultFacetIndexingParams
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
name|cache
operator|.
name|CategoryListCache
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
name|FacetResult
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Faceted search parameters indicate for which facets should info be gathered.  *<p>  * The contained facet requests define for which facets should info be gathered.  *<p>  * Contained faceted indexing parameters provide required info on how  * to read and interpret the underlying faceted information in the search index.     *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|FacetSearchParams
specifier|public
class|class
name|FacetSearchParams
block|{
DECL|field|indexingParams
specifier|protected
specifier|final
name|FacetIndexingParams
name|indexingParams
decl_stmt|;
DECL|field|facetRequests
specifier|protected
specifier|final
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|facetRequests
decl_stmt|;
DECL|field|clCache
specifier|private
name|CategoryListCache
name|clCache
init|=
literal|null
decl_stmt|;
comment|/**    * Construct with specific faceted indexing parameters.    * It is important to know the indexing parameters so as to e.g.     * read facets data correctly from the index.    * {@link #addFacetRequest(FacetRequest)} must be called at least once     * for this faceted search to find any faceted result.    * @param indexingParams Indexing faceted parameters which were used at indexing time.    * @see #addFacetRequest(FacetRequest)    */
DECL|method|FacetSearchParams
specifier|public
name|FacetSearchParams
parameter_list|(
name|FacetIndexingParams
name|indexingParams
parameter_list|)
block|{
name|this
operator|.
name|indexingParams
operator|=
name|indexingParams
expr_stmt|;
name|facetRequests
operator|=
operator|new
name|ArrayList
argument_list|<
name|FacetRequest
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Construct with default faceted indexing parameters.    * Usage of this constructor is valid only if also during indexing the     * default faceted indexing parameters were used.       * {@link #addFacetRequest(FacetRequest)} must be called at least once     * for this faceted search to find any faceted result.    * @see #addFacetRequest(FacetRequest)    */
DECL|method|FacetSearchParams
specifier|public
name|FacetSearchParams
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|DefaultFacetIndexingParams
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * A list of {@link FacetRequest} objects, determining what to count.    * If the returned collection is empty, the faceted search will return no facet results!    */
DECL|method|getFacetIndexingParams
specifier|public
specifier|final
name|FacetIndexingParams
name|getFacetIndexingParams
parameter_list|()
block|{
return|return
name|indexingParams
return|;
block|}
comment|/**    * Parameters which controlled the indexing of facets, and which are also    * needed during search.    */
DECL|method|getFacetRequests
specifier|public
specifier|final
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|getFacetRequests
parameter_list|()
block|{
return|return
name|facetRequests
return|;
block|}
comment|/**    * Add a facet request to apply for this faceted search.    * This method must be called at least once for faceted search     * to find any faceted result.<br>    * NOTE: The order of addition implies the order of the {@link FacetResult}s    * @param facetRequest facet request to be added.    */
DECL|method|addFacetRequest
specifier|public
name|void
name|addFacetRequest
parameter_list|(
name|FacetRequest
name|facetRequest
parameter_list|)
block|{
if|if
condition|(
name|facetRequest
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Provided facetRequest must not be null"
argument_list|)
throw|;
block|}
name|facetRequests
operator|.
name|add
argument_list|(
name|facetRequest
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|char
name|TAB
init|=
literal|'\t'
decl_stmt|;
specifier|final
name|char
name|NEWLINE
init|=
literal|'\n'
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"IndexingParams: "
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NEWLINE
argument_list|)
operator|.
name|append
argument_list|(
name|TAB
argument_list|)
operator|.
name|append
argument_list|(
name|getFacetIndexingParams
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NEWLINE
argument_list|)
operator|.
name|append
argument_list|(
literal|"FacetRequests:"
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetRequest
name|facetRequest
range|:
name|getFacetRequests
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|NEWLINE
argument_list|)
operator|.
name|append
argument_list|(
name|TAB
argument_list|)
operator|.
name|append
argument_list|(
name|facetRequest
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
comment|/**    * @return the cldCache in effect    */
DECL|method|getClCache
specifier|public
name|CategoryListCache
name|getClCache
parameter_list|()
block|{
return|return
name|clCache
return|;
block|}
comment|/**    * Set Cached Category Lists data to be used in Faceted search.    * @param clCache the cldCache to set    */
DECL|method|setClCache
specifier|public
name|void
name|setClCache
parameter_list|(
name|CategoryListCache
name|clCache
parameter_list|)
block|{
name|this
operator|.
name|clCache
operator|=
name|clCache
expr_stmt|;
block|}
block|}
end_class
end_unit
