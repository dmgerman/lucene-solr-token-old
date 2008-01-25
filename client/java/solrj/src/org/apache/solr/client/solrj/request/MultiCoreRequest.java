begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.request
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
name|request
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|MultiCoreResponse
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
name|ModifiableSolrParams
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
name|MultiCoreParams
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
name|common
operator|.
name|params
operator|.
name|MultiCoreParams
operator|.
name|MultiCoreAction
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
name|ContentStream
import|;
end_import
begin_comment
comment|/**  *   * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|MultiCoreRequest
specifier|public
class|class
name|MultiCoreRequest
extends|extends
name|SolrRequest
block|{
DECL|field|action
specifier|private
name|MultiCoreParams
operator|.
name|MultiCoreAction
name|action
init|=
literal|null
decl_stmt|;
DECL|field|core
specifier|private
name|String
name|core
init|=
literal|null
decl_stmt|;
DECL|method|MultiCoreRequest
specifier|public
name|MultiCoreRequest
parameter_list|()
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
literal|"/admin/multicore"
argument_list|)
expr_stmt|;
block|}
DECL|method|MultiCoreRequest
specifier|public
name|MultiCoreRequest
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|setCoreParam
specifier|public
specifier|final
name|void
name|setCoreParam
parameter_list|(
name|String
name|v
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|v
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCore
specifier|public
specifier|final
name|void
name|setCore
parameter_list|(
name|String
name|v
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"MultiCoreRequest does not use a core."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getCore
specifier|public
specifier|final
name|String
name|getCore
parameter_list|()
block|{
return|return
literal|""
return|;
comment|// force it to invalid core
block|}
comment|//---------------------------------------------------------------------------------------
comment|//
comment|//---------------------------------------------------------------------------------------
DECL|method|setAction
specifier|public
name|void
name|setAction
parameter_list|(
name|MultiCoreAction
name|action
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
comment|//---------------------------------------------------------------------------------------
comment|//
comment|//---------------------------------------------------------------------------------------
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
if|if
condition|(
name|action
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no action specified!"
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|MultiCoreParams
operator|.
name|ACTION
argument_list|,
name|action
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|MultiCoreParams
operator|.
name|CORE
argument_list|,
name|core
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
comment|//---------------------------------------------------------------------------------------
comment|//
comment|//---------------------------------------------------------------------------------------
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
DECL|method|process
specifier|public
name|MultiCoreResponse
name|process
parameter_list|(
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|MultiCoreResponse
name|res
init|=
operator|new
name|MultiCoreResponse
argument_list|(
name|server
operator|.
name|request
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
name|res
operator|.
name|setElapsedTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
comment|//---------------------------------------------------------------------------------------
comment|//
comment|//---------------------------------------------------------------------------------------
DECL|method|reloadCore
specifier|public
specifier|static
name|MultiCoreResponse
name|reloadCore
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|MultiCoreRequest
name|req
init|=
operator|new
name|MultiCoreRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreParam
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|req
operator|.
name|setAction
argument_list|(
name|MultiCoreAction
operator|.
name|RELOAD
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|server
argument_list|)
return|;
block|}
DECL|method|getStatus
specifier|public
specifier|static
name|MultiCoreResponse
name|getStatus
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|MultiCoreRequest
name|req
init|=
operator|new
name|MultiCoreRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreParam
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|req
operator|.
name|setAction
argument_list|(
name|MultiCoreAction
operator|.
name|STATUS
argument_list|)
expr_stmt|;
return|return
name|req
operator|.
name|process
argument_list|(
name|server
argument_list|)
return|;
block|}
block|}
end_class
end_unit
