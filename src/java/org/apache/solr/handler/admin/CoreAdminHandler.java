begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|io
operator|.
name|File
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
name|CoreAdminParams
operator|.
name|CoreAdminAction
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|core
operator|.
name|CoreDescriptor
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
name|request
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
name|search
operator|.
name|SolrIndexSearcher
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
name|util
operator|.
name|RefCounted
import|;
end_import
begin_comment
comment|/**  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|CoreAdminHandler
specifier|public
specifier|abstract
class|class
name|CoreAdminHandler
extends|extends
name|RequestHandlerBase
block|{
DECL|method|CoreAdminHandler
specifier|public
name|CoreAdminHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// Unlike most request handlers, CoreContainer initialization
comment|// should happen in the constructor...
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
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"CoreAdminHandler should not be configured in solrconf.xml\n"
operator|+
literal|"it is a special Handler configured directly by the RequestDispatcher"
argument_list|)
throw|;
block|}
comment|/**    * The instance of CoreContainer this handler handles.    * This should be the CoreContainer instance that created this handler.    * @return a CoreContainer instance    */
DECL|method|getCoreContainer
specifier|public
specifier|abstract
name|CoreContainer
name|getCoreContainer
parameter_list|()
function_decl|;
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
name|boolean
name|do_persist
init|=
literal|false
decl_stmt|;
comment|// Pick the action
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|SolrParams
name|required
init|=
name|params
operator|.
name|required
argument_list|()
decl_stmt|;
name|CoreAdminAction
name|action
init|=
name|CoreAdminAction
operator|.
name|STATUS
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
name|CoreAdminAction
operator|.
name|get
argument_list|(
name|a
argument_list|)
expr_stmt|;
if|if
condition|(
name|action
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
literal|"Unknown 'action' value.  Use: "
operator|+
name|CoreAdminAction
operator|.
name|values
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|String
name|cname
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|action
condition|)
block|{
case|case
name|CREATE
case|:
block|{
name|String
name|name
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|CoreDescriptor
name|dcore
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cores
argument_list|,
name|name
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|)
argument_list|)
decl_stmt|;
comment|// fillup optional parameters
name|String
name|opts
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|opts
operator|!=
literal|null
condition|)
name|dcore
operator|.
name|setConfigName
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|opts
operator|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|)
expr_stmt|;
if|if
condition|(
name|opts
operator|!=
literal|null
condition|)
name|dcore
operator|.
name|setSchemaName
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|cores
operator|.
name|create
argument_list|(
name|dcore
argument_list|)
decl_stmt|;
name|cores
operator|.
name|register
argument_list|(
name|name
argument_list|,
name|core
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"core"
argument_list|,
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|do_persist
operator|=
name|cores
operator|.
name|isPersistent
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
name|RENAME
case|:
block|{
name|String
name|name
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|OTHER
argument_list|)
decl_stmt|;
if|if
condition|(
name|cname
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
break|break;
name|SolrCore
name|core
init|=
name|cores
operator|.
name|getCore
argument_list|(
name|cname
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|do_persist
operator|=
name|cores
operator|.
name|isPersistent
argument_list|()
expr_stmt|;
name|cores
operator|.
name|register
argument_list|(
name|name
argument_list|,
name|core
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cores
operator|.
name|remove
argument_list|(
name|cname
argument_list|)
expr_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
case|case
name|ALIAS
case|:
block|{
name|String
name|name
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|OTHER
argument_list|)
decl_stmt|;
if|if
condition|(
name|cname
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
break|break;
name|SolrCore
name|core
init|=
name|cores
operator|.
name|getCore
argument_list|(
name|cname
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|do_persist
operator|=
name|cores
operator|.
name|isPersistent
argument_list|()
expr_stmt|;
name|cores
operator|.
name|register
argument_list|(
name|name
argument_list|,
name|core
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// no core.close() since each entry in the cores map should increase the ref
block|}
break|break;
block|}
case|case
name|UNLOAD
case|:
block|{
name|SolrCore
name|core
init|=
name|cores
operator|.
name|remove
argument_list|(
name|cname
argument_list|)
decl_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
name|do_persist
operator|=
name|cores
operator|.
name|isPersistent
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
name|STATUS
case|:
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|status
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|cname
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|String
name|name
range|:
name|cores
operator|.
name|getCoreNames
argument_list|()
control|)
block|{
name|status
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|getCoreStatus
argument_list|(
name|cores
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|status
operator|.
name|add
argument_list|(
name|cname
argument_list|,
name|getCoreStatus
argument_list|(
name|cores
argument_list|,
name|cname
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|do_persist
operator|=
literal|false
expr_stmt|;
comment|// no state change
break|break;
block|}
case|case
name|PERSIST
case|:
block|{
name|String
name|fileName
init|=
name|params
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|FILE
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileName
operator|!=
literal|null
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|cores
operator|.
name|getConfigFile
argument_list|()
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|cores
operator|.
name|persistFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"saved"
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|do_persist
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|cores
operator|.
name|isPersistent
argument_list|()
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
name|FORBIDDEN
argument_list|,
literal|"Persistence is not enabled"
argument_list|)
throw|;
block|}
else|else
name|do_persist
operator|=
literal|true
expr_stmt|;
break|break;
block|}
case|case
name|RELOAD
case|:
block|{
name|cores
operator|.
name|reload
argument_list|(
name|cname
argument_list|)
expr_stmt|;
name|do_persist
operator|=
literal|false
expr_stmt|;
comment|// no change on reload
break|break;
block|}
case|case
name|SWAP
case|:
block|{
name|do_persist
operator|=
name|params
operator|.
name|getBool
argument_list|(
name|CoreAdminParams
operator|.
name|PERSISTENT
argument_list|,
name|cores
operator|.
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|other
init|=
name|required
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|OTHER
argument_list|)
decl_stmt|;
name|cores
operator|.
name|swap
argument_list|(
name|cname
argument_list|,
name|other
argument_list|)
expr_stmt|;
break|break;
block|}
default|default:
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
literal|"TODO: IMPLEMENT: "
operator|+
name|action
argument_list|)
throw|;
block|}
block|}
comment|// switch
comment|// Should we persist the changes?
if|if
condition|(
name|do_persist
condition|)
block|{
name|cores
operator|.
name|persist
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"saved"
argument_list|,
name|cores
operator|.
name|getConfigFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getCoreStatus
specifier|private
specifier|static
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getCoreStatus
parameter_list|(
name|CoreContainer
name|cores
parameter_list|,
name|String
name|cname
parameter_list|)
throws|throws
name|IOException
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|SolrCore
name|core
init|=
name|cores
operator|.
name|getCore
argument_list|(
name|cname
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|info
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"instanceDir"
argument_list|,
name|normalizePath
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"dataDir"
argument_list|,
name|normalizePath
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"startTime"
argument_list|,
operator|new
name|Date
argument_list|(
name|core
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"uptime"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|core
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"index"
argument_list|,
name|LukeRequestHandler
operator|.
name|getIndexInfo
argument_list|(
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getReader
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|info
return|;
block|}
DECL|method|normalizePath
specifier|private
specifier|static
name|String
name|normalizePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
name|File
operator|.
name|separatorChar
argument_list|)
expr_stmt|;
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
name|File
operator|.
name|separatorChar
argument_list|)
expr_stmt|;
return|return
name|path
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
literal|"Manage Multiple Solr Cores"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
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
