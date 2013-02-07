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
name|FacetArrays
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
name|aggregator
operator|.
name|Aggregator
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
name|aggregator
operator|.
name|ComplementCountingAggregator
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
name|aggregator
operator|.
name|CountingAggregator
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Facet request for counting facets.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|CountFacetRequest
specifier|public
class|class
name|CountFacetRequest
extends|extends
name|FacetRequest
block|{
DECL|method|CountFacetRequest
specifier|public
name|CountFacetRequest
parameter_list|(
name|CategoryPath
name|path
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createAggregator
specifier|public
name|Aggregator
name|createAggregator
parameter_list|(
name|boolean
name|useComplements
parameter_list|,
name|FacetArrays
name|arrays
parameter_list|,
name|TaxonomyReader
name|taxonomy
parameter_list|)
block|{
comment|// we rely on that, if needed, result is cleared by arrays!
name|int
index|[]
name|a
init|=
name|arrays
operator|.
name|getIntArray
argument_list|()
decl_stmt|;
if|if
condition|(
name|useComplements
condition|)
block|{
return|return
operator|new
name|ComplementCountingAggregator
argument_list|(
name|a
argument_list|)
return|;
block|}
return|return
operator|new
name|CountingAggregator
argument_list|(
name|a
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueOf
specifier|public
name|double
name|getValueOf
parameter_list|(
name|FacetArrays
name|arrays
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|arrays
operator|.
name|getIntArray
argument_list|()
index|[
name|ordinal
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getFacetArraysSource
specifier|public
name|FacetArraysSource
name|getFacetArraysSource
parameter_list|()
block|{
return|return
name|FacetArraysSource
operator|.
name|INT
return|;
block|}
block|}
end_class
end_unit
