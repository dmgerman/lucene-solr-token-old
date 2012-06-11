begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
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
name|search
operator|.
name|vectorhighlight
operator|.
name|BoundaryScanner
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
name|vectorhighlight
operator|.
name|FragmentsBuilder
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
begin_class
DECL|class|ScoreOrderFragmentsBuilder
specifier|public
class|class
name|ScoreOrderFragmentsBuilder
extends|extends
name|SolrFragmentsBuilder
block|{
annotation|@
name|Override
DECL|method|getFragmentsBuilder
specifier|protected
name|FragmentsBuilder
name|getFragmentsBuilder
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|String
index|[]
name|preTags
parameter_list|,
name|String
index|[]
name|postTags
parameter_list|,
name|BoundaryScanner
name|bs
parameter_list|)
block|{
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
operator|.
name|ScoreOrderFragmentsBuilder
name|sofb
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
operator|.
name|ScoreOrderFragmentsBuilder
argument_list|(
name|preTags
argument_list|,
name|postTags
argument_list|,
name|bs
argument_list|)
decl_stmt|;
name|sofb
operator|.
name|setMultiValuedSeparator
argument_list|(
name|getMultiValuedSeparatorChar
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sofb
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////
comment|//////////////////////// SolrInfoMBeans methods ///////////////////////
comment|///////////////////////////////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"ScoreOrderFragmentsBuilder"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class
end_unit
