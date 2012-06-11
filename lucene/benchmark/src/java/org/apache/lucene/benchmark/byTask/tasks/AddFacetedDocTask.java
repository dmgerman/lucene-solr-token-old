begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|FacetSource
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
name|CategoryContainer
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
name|CategoryDocumentBuilder
import|;
end_import
begin_comment
comment|/**  * Add a faceted document.  *<p>  * Config properties:  *<ul>  *<li><b>with.facets</b>=&lt;tells whether to actually add any facets to the document| Default: true&gt;  *<br>This config property allows to easily compare the performance of adding docs with and without facets.  *  Note that facets are created even when this is false, just that they are not added to the document (nor to the taxonomy).  *</ul>   *<p>  * See {@link AddDocTask} for general document parameters and configuration.  *<p>  * Makes use of the {@link FacetSource} in effect - see {@link PerfRunData} for facet source settings.     */
end_comment
begin_class
DECL|class|AddFacetedDocTask
specifier|public
class|class
name|AddFacetedDocTask
extends|extends
name|AddDocTask
block|{
DECL|method|AddFacetedDocTask
specifier|public
name|AddFacetedDocTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|field|facets
specifier|private
name|CategoryContainer
name|facets
init|=
literal|null
decl_stmt|;
DECL|field|categoryDocBuilder
specifier|private
name|CategoryDocumentBuilder
name|categoryDocBuilder
init|=
literal|null
decl_stmt|;
DECL|field|withFacets
specifier|private
name|boolean
name|withFacets
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
comment|// create the facets even if they should not be added - allows to measure the effect of just adding facets
name|facets
operator|=
name|getRunData
argument_list|()
operator|.
name|getFacetSource
argument_list|()
operator|.
name|getNextFacets
argument_list|(
name|facets
argument_list|)
expr_stmt|;
name|withFacets
operator|=
name|getRunData
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"with.facets"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|withFacets
condition|)
block|{
name|categoryDocBuilder
operator|=
operator|new
name|CategoryDocumentBuilder
argument_list|(
name|getRunData
argument_list|()
operator|.
name|getTaxonomyWriter
argument_list|()
argument_list|)
expr_stmt|;
name|categoryDocBuilder
operator|.
name|setCategories
argument_list|(
name|facets
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLogMessage
specifier|protected
name|String
name|getLogMessage
parameter_list|(
name|int
name|recsCount
parameter_list|)
block|{
if|if
condition|(
operator|!
name|withFacets
condition|)
block|{
return|return
name|super
operator|.
name|getLogMessage
argument_list|(
name|recsCount
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|getLogMessage
argument_list|(
name|recsCount
argument_list|)
operator|+
literal|" with facets"
return|;
block|}
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|withFacets
condition|)
block|{
name|categoryDocBuilder
operator|.
name|build
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|doLogic
argument_list|()
return|;
block|}
block|}
end_class
end_unit
