begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|handler
operator|.
name|RequestHandlerBase
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|MockQuerySenderListenerReqHandler
specifier|public
class|class
name|MockQuerySenderListenerReqHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|req
specifier|public
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|rsp
specifier|public
name|SolrQueryResponse
name|rsp
decl_stmt|;
DECL|field|initCounter
name|AtomicInteger
name|initCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|initCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
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
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
return|return
name|result
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
name|String
name|result
init|=
literal|null
decl_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getStatistics
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getStatistics
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|lst
init|=
name|super
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"initCount"
argument_list|,
name|initCounter
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
block|}
end_class
end_unit
