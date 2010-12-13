begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|core
operator|.
name|SolrCore
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|response
operator|.
name|SolrQueryResponse
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_class
DECL|class|SolrRequestInfo
specifier|public
class|class
name|SolrRequestInfo
block|{
DECL|field|threadLocal
specifier|protected
specifier|final
specifier|static
name|ThreadLocal
argument_list|<
name|SolrRequestInfo
argument_list|>
name|threadLocal
init|=
operator|new
name|ThreadLocal
argument_list|<
name|SolrRequestInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|req
specifier|protected
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|rsp
specifier|protected
name|SolrQueryResponse
name|rsp
decl_stmt|;
DECL|field|now
specifier|protected
name|Date
name|now
decl_stmt|;
DECL|field|rb
specifier|protected
name|ResponseBuilder
name|rb
decl_stmt|;
DECL|method|getRequestInfo
specifier|public
specifier|static
name|SolrRequestInfo
name|getRequestInfo
parameter_list|()
block|{
return|return
name|threadLocal
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|setRequestInfo
specifier|public
specifier|static
name|void
name|setRequestInfo
parameter_list|(
name|SolrRequestInfo
name|info
parameter_list|)
block|{
comment|// TODO: temporary sanity check... this can be changed to just an assert in the future
name|SolrRequestInfo
name|prev
init|=
name|threadLocal
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|prev
operator|!=
literal|null
condition|)
block|{
name|SolrCore
operator|.
name|log
operator|.
name|error
argument_list|(
literal|"Previous SolrRequestInfo was not closed!  req="
operator|+
name|prev
operator|.
name|req
operator|.
name|getOriginalParams
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
assert|assert
name|prev
operator|==
literal|null
assert|;
name|threadLocal
operator|.
name|set
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
DECL|method|clearRequestInfo
specifier|public
specifier|static
name|void
name|clearRequestInfo
parameter_list|()
block|{
name|threadLocal
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
DECL|method|SolrRequestInfo
specifier|public
name|SolrRequestInfo
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|rsp
operator|=
name|rsp
expr_stmt|;
block|}
DECL|method|getNOW
specifier|public
name|Date
name|getNOW
parameter_list|()
block|{
if|if
condition|(
name|now
operator|!=
literal|null
condition|)
return|return
name|now
return|;
name|long
name|ms
init|=
literal|0
decl_stmt|;
name|String
name|nowStr
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"NOW"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nowStr
operator|!=
literal|null
condition|)
block|{
name|ms
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|nowStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ms
operator|=
name|req
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
block|}
name|now
operator|=
operator|new
name|Date
argument_list|(
name|ms
argument_list|)
expr_stmt|;
return|return
name|now
return|;
block|}
DECL|method|getReq
specifier|public
name|SolrQueryRequest
name|getReq
parameter_list|()
block|{
return|return
name|req
return|;
block|}
DECL|method|getRsp
specifier|public
name|SolrQueryResponse
name|getRsp
parameter_list|()
block|{
return|return
name|rsp
return|;
block|}
comment|/** May return null if the request handler is not based on SearchHandler */
DECL|method|getResponseBuilder
specifier|public
name|ResponseBuilder
name|getResponseBuilder
parameter_list|()
block|{
return|return
name|rb
return|;
block|}
DECL|method|setResponseBuilder
specifier|public
name|void
name|setResponseBuilder
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|this
operator|.
name|rb
operator|=
name|rb
expr_stmt|;
block|}
block|}
end_class
end_unit
