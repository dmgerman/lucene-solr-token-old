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
name|search
operator|.
name|sampling
operator|.
name|BaseSampleTestTopK
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
name|sampling
operator|.
name|Sampler
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|AdaptiveAccumulatorTest
specifier|public
class|class
name|AdaptiveAccumulatorTest
extends|extends
name|BaseSampleTestTopK
block|{
annotation|@
name|Override
DECL|method|getSamplingAccumulator
specifier|protected
name|FacetsAccumulator
name|getSamplingAccumulator
parameter_list|(
name|Sampler
name|sampler
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|FacetSearchParams
name|searchParams
parameter_list|)
block|{
name|AdaptiveFacetsAccumulator
name|res
init|=
operator|new
name|AdaptiveFacetsAccumulator
argument_list|(
name|searchParams
argument_list|,
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
decl_stmt|;
name|res
operator|.
name|setSampler
argument_list|(
name|sampler
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
