begin_unit
begin_package
DECL|package|org.apache.solr.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|similarities
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|similarities
operator|.
name|LMDirichletSimilarity
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
name|similarities
operator|.
name|Similarity
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SimilarityFactory
import|;
end_import
begin_comment
comment|/**  * Factory for {@link LMDirichletSimilarity}  *<p>  * Parameters:  *<ul>  *<li>parameter mu (float): smoothing parameter&mu;. The default is  *<code>2000</code></li>  *</ul>  *<p>  * Optional settings:  *<ul>  *<li>discountOverlaps (bool): Sets  *       {@link LMDirichletSimilarity#setDiscountOverlaps(boolean)}</li>  *</ul>  * @lucene.experimental  */
end_comment
begin_class
DECL|class|LMDirichletSimilarityFactory
specifier|public
class|class
name|LMDirichletSimilarityFactory
extends|extends
name|SimilarityFactory
block|{
DECL|field|discountOverlaps
specifier|private
name|boolean
name|discountOverlaps
decl_stmt|;
DECL|field|mu
specifier|private
name|Float
name|mu
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|discountOverlaps
operator|=
name|params
operator|.
name|getBool
argument_list|(
literal|"discountOverlaps"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mu
operator|=
name|params
operator|.
name|getFloat
argument_list|(
literal|"mu"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
name|LMDirichletSimilarity
name|sim
init|=
operator|(
name|mu
operator|!=
literal|null
operator|)
condition|?
operator|new
name|LMDirichletSimilarity
argument_list|(
name|mu
argument_list|)
else|:
operator|new
name|LMDirichletSimilarity
argument_list|()
decl_stmt|;
name|sim
operator|.
name|setDiscountOverlaps
argument_list|(
name|discountOverlaps
argument_list|)
expr_stmt|;
return|return
name|sim
return|;
block|}
block|}
end_class
end_unit
