begin_unit
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrResponse
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
name|util
operator|.
name|NamedList
import|;
end_import
begin_class
DECL|class|OverseerSolrResponse
specifier|public
class|class
name|OverseerSolrResponse
extends|extends
name|SolrResponse
block|{
DECL|field|responseList
name|NamedList
name|responseList
init|=
literal|null
decl_stmt|;
DECL|method|OverseerSolrResponse
specifier|public
name|OverseerSolrResponse
parameter_list|(
name|NamedList
name|list
parameter_list|)
block|{
name|responseList
operator|=
name|list
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getElapsedTime
specifier|public
name|long
name|getElapsedTime
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|setResponse
specifier|public
name|void
name|setResponse
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
parameter_list|)
block|{
name|this
operator|.
name|responseList
operator|=
name|rsp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getResponse
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getResponse
parameter_list|()
block|{
return|return
name|responseList
return|;
block|}
block|}
end_class
end_unit
