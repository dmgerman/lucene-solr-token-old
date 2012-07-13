begin_unit
begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
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
name|cloud
operator|.
name|Overseer
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
name|cloud
operator|.
name|OverseerCollectionProcessor
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
name|SolrException
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
name|cloud
operator|.
name|ZkNodeProps
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
name|cloud
operator|.
name|ZkStateReader
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
name|CollectionParams
operator|.
name|CollectionAction
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
name|CoreAdminParams
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
name|util
operator|.
name|NamedList
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
name|core
operator|.
name|CoreContainer
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
name|zookeeper
operator|.
name|KeeperException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_class
DECL|class|CollectionsHandler
specifier|public
class|class
name|CollectionsHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CollectionsHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|coreContainer
specifier|protected
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|method|CollectionsHandler
specifier|public
name|CollectionsHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// Unlike most request handlers, CoreContainer initialization
comment|// should happen in the constructor...
name|this
operator|.
name|coreContainer
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Overloaded ctor to inject CoreContainer into the handler.    *    * @param coreContainer Core Container of the solr webapp installed.    */
DECL|method|CollectionsHandler
specifier|public
name|CollectionsHandler
parameter_list|(
specifier|final
name|CoreContainer
name|coreContainer
parameter_list|)
block|{
name|this
operator|.
name|coreContainer
operator|=
name|coreContainer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|final
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{    }
comment|/**    * The instance of CoreContainer this handler handles. This should be the CoreContainer instance that created this    * handler.    *    * @return a CoreContainer instance    */
DECL|method|getCoreContainer
specifier|public
name|CoreContainer
name|getCoreContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|coreContainer
return|;
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
comment|// Make sure the cores is enabled
name|CoreContainer
name|cores
init|=
name|getCoreContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|cores
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Core container instance missing"
argument_list|)
throw|;
block|}
comment|// Pick the action
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|CollectionAction
name|action
init|=
literal|null
decl_stmt|;
name|String
name|a
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|action
operator|=
name|CollectionAction
operator|.
name|get
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|action
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|action
condition|)
block|{
case|case
name|CREATE
case|:
block|{
name|this
operator|.
name|handleCreateAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|DELETE
case|:
block|{
name|this
operator|.
name|handleDeleteAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|RELOAD
case|:
block|{
name|this
operator|.
name|handleReloadAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
block|}
default|default:
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown action: "
operator|+
name|action
argument_list|)
throw|;
block|}
block|}
block|}
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|handleReloadAction
specifier|private
name|void
name|handleReloadAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Reloading Collection : "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerCollectionProcessor
operator|.
name|RELOADCOLLECTION
argument_list|,
literal|"name"
argument_list|,
name|name
argument_list|)
decl_stmt|;
comment|// TODO: what if you want to block until the collection is available?
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getOverseerCollectionQueue
argument_list|()
operator|.
name|offer
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|handleDeleteAction
specifier|private
name|void
name|handleDeleteAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Deleting Collection : "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerCollectionProcessor
operator|.
name|DELETECOLLECTION
argument_list|,
literal|"name"
argument_list|,
name|name
argument_list|)
decl_stmt|;
comment|// TODO: what if you want to block until the collection is available?
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getOverseerCollectionQueue
argument_list|()
operator|.
name|offer
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// very simple currently, you can pass a template collection, and the new collection is created on
comment|// every node the template collection is on
comment|// there is a lot more to add - you should also be able to create with an explicit server list
comment|// we might also want to think about error handling (add the request to a zk queue and involve overseer?)
comment|// as well as specific replicas= options
DECL|method|handleCreateAction
specifier|private
name|void
name|handleCreateAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Creating Collection : "
operator|+
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|Integer
name|numReplicas
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getInt
argument_list|(
literal|"numReplicas"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|configName
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"collection.configName"
argument_list|)
decl_stmt|;
name|String
name|numShards
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"numShards"
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|OverseerCollectionProcessor
operator|.
name|CREATECOLLECTION
argument_list|,
literal|"numReplicas"
argument_list|,
name|numReplicas
operator|.
name|toString
argument_list|()
argument_list|,
literal|"name"
argument_list|,
name|name
argument_list|,
literal|"collection.configName"
argument_list|,
name|configName
argument_list|,
literal|"numShards"
argument_list|,
name|numShards
argument_list|)
decl_stmt|;
comment|// TODO: what if you want to block until the collection is available?
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getOverseerCollectionQueue
argument_list|()
operator|.
name|offer
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|params
specifier|public
specifier|static
name|ModifiableSolrParams
name|params
parameter_list|(
name|String
modifier|...
name|params
parameter_list|)
block|{
name|ModifiableSolrParams
name|msp
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|msp
operator|.
name|add
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
name|params
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|msp
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Manage SolrCloud Collections"
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
literal|"$URL: https://svn.apache.org/repos/asf/lucene/dev/trunk/solr/core/src/java/org/apache/solr/handler/admin/CollectionHandler.java $"
return|;
block|}
block|}
end_class
end_unit
