begin_unit
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|ContentStream
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
name|request
operator|.
name|SolrQueryRequest
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
begin_comment
comment|/**  * Load a {@link org.apache.solr.common.util.ContentStream} into Solr  *  **/
end_comment
begin_class
DECL|class|ContentStreamLoader
specifier|public
specifier|abstract
class|class
name|ContentStreamLoader
block|{
DECL|field|errHeader
specifier|protected
name|String
name|errHeader
decl_stmt|;
DECL|method|getErrHeader
specifier|public
name|String
name|getErrHeader
parameter_list|()
block|{
return|return
name|errHeader
return|;
block|}
DECL|method|setErrHeader
specifier|public
name|void
name|setErrHeader
parameter_list|(
name|String
name|errHeader
parameter_list|)
block|{
name|this
operator|.
name|errHeader
operator|=
name|errHeader
expr_stmt|;
block|}
comment|/**    * Loaders are responsible for closing the stream    *    * @param req The input {@link org.apache.solr.request.SolrQueryRequest}    * @param rsp The response, in case the Loader wishes to add anything    * @param stream The {@link org.apache.solr.common.util.ContentStream} to add    */
DECL|method|load
specifier|public
specifier|abstract
name|void
name|load
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|ContentStream
name|stream
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class
end_unit
