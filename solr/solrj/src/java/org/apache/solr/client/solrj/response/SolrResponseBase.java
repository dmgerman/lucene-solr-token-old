begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
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
name|response
package|;
end_package
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
begin_comment
comment|/**  *   *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrResponseBase
specifier|public
class|class
name|SolrResponseBase
extends|extends
name|SolrResponse
block|{
DECL|field|elapsedTime
specifier|private
name|long
name|elapsedTime
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|response
specifier|private
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
init|=
literal|null
decl_stmt|;
DECL|field|requestUrl
specifier|private
name|String
name|requestUrl
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|getElapsedTime
specifier|public
name|long
name|getElapsedTime
parameter_list|()
block|{
return|return
name|elapsedTime
return|;
block|}
DECL|method|setElapsedTime
specifier|public
name|void
name|setElapsedTime
parameter_list|(
name|long
name|elapsedTime
parameter_list|)
block|{
name|this
operator|.
name|elapsedTime
operator|=
name|elapsedTime
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
name|response
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
name|response
parameter_list|)
block|{
name|this
operator|.
name|response
operator|=
name|response
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
return|return
name|response
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getResponseHeader
specifier|public
name|NamedList
name|getResponseHeader
parameter_list|()
block|{
return|return
operator|(
name|NamedList
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"responseHeader"
argument_list|)
return|;
block|}
comment|// these two methods are based on the logic in SolrCore.setResponseHeaderValues(...)
DECL|method|getStatus
specifier|public
name|int
name|getStatus
parameter_list|()
block|{
name|NamedList
name|header
init|=
name|getResponseHeader
argument_list|()
decl_stmt|;
if|if
condition|(
name|header
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|Integer
operator|)
name|header
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|method|getQTime
specifier|public
name|int
name|getQTime
parameter_list|()
block|{
name|NamedList
name|header
init|=
name|getResponseHeader
argument_list|()
decl_stmt|;
if|if
condition|(
name|header
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|Integer
operator|)
name|header
operator|.
name|get
argument_list|(
literal|"QTime"
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|method|getRequestUrl
specifier|public
name|String
name|getRequestUrl
parameter_list|()
block|{
return|return
name|requestUrl
return|;
block|}
DECL|method|setRequestUrl
specifier|public
name|void
name|setRequestUrl
parameter_list|(
name|String
name|requestUrl
parameter_list|)
block|{
name|this
operator|.
name|requestUrl
operator|=
name|requestUrl
expr_stmt|;
block|}
block|}
end_class
end_unit
