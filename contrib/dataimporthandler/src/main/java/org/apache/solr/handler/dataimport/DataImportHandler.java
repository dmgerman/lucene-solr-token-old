begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
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
name|SolrInputDocument
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
name|CommonParams
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
name|params
operator|.
name|UpdateParams
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
name|ContentStreamBase
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
name|SolrConfig
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
name|SolrResourceLoader
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
name|handler
operator|.
name|RequestHandlerUtils
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
name|RawResponseWriter
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
name|schema
operator|.
name|IndexSchema
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessorChain
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
name|plugin
operator|.
name|SolrCoreAware
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import
begin_comment
comment|/**  *<p>  * Solr Request Handler for data import from databases and REST data sources.  *</p>  *<p>  * It is configured in solrconfig.xml  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|DataImportHandler
specifier|public
class|class
name|DataImportHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|SolrCoreAware
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DataImportHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|importer
specifier|private
name|DataImporter
name|importer
decl_stmt|;
DECL|field|variables
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|variables
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|field|initArgs
specifier|private
name|NamedList
name|initArgs
decl_stmt|;
DECL|field|dataSources
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Properties
argument_list|>
name|dataSources
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Properties
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|requestParams
specifier|private
name|DataImporter
operator|.
name|RequestParams
name|requestParams
decl_stmt|;
DECL|field|debugDocuments
specifier|private
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|debugDocuments
decl_stmt|;
DECL|field|debugLogger
specifier|private
name|DebugLogger
name|debugLogger
decl_stmt|;
DECL|field|debugEnabled
specifier|private
name|boolean
name|debugEnabled
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|initArgs
operator|=
name|args
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
try|try
block|{
name|String
name|debug
init|=
operator|(
name|String
operator|)
name|initArgs
operator|.
name|get
argument_list|(
name|ENABLE_DEBUG
argument_list|)
decl_stmt|;
if|if
condition|(
name|debug
operator|!=
literal|null
operator|&&
literal|"no"
operator|.
name|equals
argument_list|(
name|debug
argument_list|)
condition|)
name|debugEnabled
operator|=
literal|false
expr_stmt|;
name|NamedList
name|defaults
init|=
operator|(
name|NamedList
operator|)
name|initArgs
operator|.
name|get
argument_list|(
literal|"defaults"
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaults
operator|!=
literal|null
condition|)
block|{
name|String
name|configLoc
init|=
operator|(
name|String
operator|)
name|defaults
operator|.
name|get
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
if|if
condition|(
name|configLoc
operator|!=
literal|null
operator|&&
name|configLoc
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|processConfiguration
argument_list|(
name|defaults
argument_list|)
expr_stmt|;
name|importer
operator|=
operator|new
name|DataImporter
argument_list|(
name|SolrWriter
operator|.
name|getResourceAsString
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|configLoc
argument_list|)
argument_list|)
argument_list|,
name|core
argument_list|,
name|dataSources
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrConfig
operator|.
name|severeErrors
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|log
argument_list|(
name|Level
operator|.
name|SEVERE
argument_list|,
name|DataImporter
operator|.
name|MSG
operator|.
name|LOAD_EXP
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
name|DataImporter
operator|.
name|MSG
operator|.
name|INVALID_CONFIG
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|requestParams
operator|=
operator|new
name|DataImporter
operator|.
name|RequestParams
argument_list|(
name|getParamsMap
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|command
init|=
name|requestParams
operator|.
name|command
decl_stmt|;
if|if
condition|(
name|DataImporter
operator|.
name|SHOW_CONF_CMD
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
comment|// Modify incoming request params to add wt=raw
name|ModifiableSolrParams
name|rawParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|rawParams
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|,
literal|"raw"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|rawParams
argument_list|)
expr_stmt|;
name|String
name|dataConfigFile
init|=
name|defaults
operator|.
name|get
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
name|ContentStreamBase
name|content
init|=
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|SolrWriter
operator|.
name|getResourceAsString
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|dataConfigFile
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
name|RawResponseWriter
operator|.
name|CONTENT
argument_list|,
name|content
argument_list|)
expr_stmt|;
return|return;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"initArgs"
argument_list|,
name|initArgs
argument_list|)
expr_stmt|;
name|String
name|message
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|command
operator|!=
literal|null
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"command"
argument_list|,
name|command
argument_list|)
expr_stmt|;
if|if
condition|(
name|requestParams
operator|.
name|debug
condition|)
block|{
comment|// Reload the data-config.xml
name|importer
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|requestParams
operator|.
name|dataConfig
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|processConfiguration
argument_list|(
operator|(
name|NamedList
operator|)
name|initArgs
operator|.
name|get
argument_list|(
literal|"defaults"
argument_list|)
argument_list|)
expr_stmt|;
name|importer
operator|=
operator|new
name|DataImporter
argument_list|(
name|requestParams
operator|.
name|dataConfig
argument_list|,
name|req
operator|.
name|getCore
argument_list|()
argument_list|,
name|dataSources
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"exception"
argument_list|,
name|DebugLogger
operator|.
name|getStacktraceString
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|importer
operator|=
literal|null
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
name|inform
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|message
operator|=
name|DataImporter
operator|.
name|MSG
operator|.
name|CONFIG_RELOADED
expr_stmt|;
block|}
comment|// If importer is still null
if|if
condition|(
name|importer
operator|==
literal|null
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
name|DataImporter
operator|.
name|MSG
operator|.
name|NO_INIT
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|command
operator|!=
literal|null
operator|&&
name|DataImporter
operator|.
name|ABORT_CMD
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
name|importer
operator|.
name|runCmd
argument_list|(
name|requestParams
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|importer
operator|.
name|getStatus
argument_list|()
operator|!=
name|DataImporter
operator|.
name|Status
operator|.
name|IDLE
condition|)
block|{
name|message
operator|=
name|DataImporter
operator|.
name|MSG
operator|.
name|CMD_RUNNING
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|command
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|DataImporter
operator|.
name|FULL_IMPORT_CMD
operator|.
name|equals
argument_list|(
name|command
argument_list|)
operator|||
name|DataImporter
operator|.
name|DELTA_IMPORT_CMD
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
name|UpdateRequestProcessorChain
name|processorChain
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateProcessingChain
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_PROCESSOR
argument_list|)
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|processorChain
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
name|SolrResourceLoader
name|loader
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
decl_stmt|;
name|SolrWriter
name|sw
init|=
name|getSolrWriter
argument_list|(
name|processor
argument_list|,
name|loader
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|requestParams
operator|.
name|debug
condition|)
block|{
if|if
condition|(
name|debugEnabled
condition|)
block|{
comment|// Synchronous request for the debug mode
name|importer
operator|.
name|runCmd
argument_list|(
name|requestParams
argument_list|,
name|sw
argument_list|,
name|variables
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"mode"
argument_list|,
literal|"debug"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"documents"
argument_list|,
name|debugDocuments
argument_list|)
expr_stmt|;
if|if
condition|(
name|debugLogger
operator|!=
literal|null
condition|)
name|rsp
operator|.
name|add
argument_list|(
literal|"verbose-output"
argument_list|,
name|debugLogger
operator|.
name|output
argument_list|)
expr_stmt|;
name|debugLogger
operator|=
literal|null
expr_stmt|;
name|debugDocuments
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|message
operator|=
name|DataImporter
operator|.
name|MSG
operator|.
name|DEBUG_NOT_ENABLED
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Asynchronous request for normal mode
name|importer
operator|.
name|runAsync
argument_list|(
name|requestParams
argument_list|,
name|sw
argument_list|,
name|variables
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|DataImporter
operator|.
name|RELOAD_CONF_CMD
operator|.
name|equals
argument_list|(
name|command
argument_list|)
condition|)
block|{
name|importer
operator|=
literal|null
expr_stmt|;
name|inform
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|=
name|DataImporter
operator|.
name|MSG
operator|.
name|CONFIG_RELOADED
expr_stmt|;
block|}
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
name|importer
operator|.
name|getStatus
argument_list|()
operator|==
name|DataImporter
operator|.
name|Status
operator|.
name|IDLE
condition|?
literal|"idle"
else|:
literal|"busy"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"importResponse"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"statusMessages"
argument_list|,
name|importer
operator|.
name|getStatusMessages
argument_list|()
argument_list|)
expr_stmt|;
name|RequestHandlerUtils
operator|.
name|addExperimentalFormatWarning
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|getParamsMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getParamsMap
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|names
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|s
init|=
name|names
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
index|[]
name|val
init|=
name|params
operator|.
name|getParams
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
operator|||
name|val
operator|.
name|length
operator|<
literal|1
condition|)
continue|continue;
if|if
condition|(
name|val
operator|.
name|length
operator|==
literal|1
condition|)
name|result
operator|.
name|put
argument_list|(
name|s
argument_list|,
name|val
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
else|else
name|result
operator|.
name|put
argument_list|(
name|s
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|processConfiguration
specifier|private
name|void
name|processConfiguration
parameter_list|(
name|NamedList
name|defaults
parameter_list|)
block|{
if|if
condition|(
name|defaults
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No configuration specified in solrconfig.xml for DataImportHandler"
argument_list|)
expr_stmt|;
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Processing configuration from solrconfig.xml: "
operator|+
name|defaults
argument_list|)
expr_stmt|;
name|dataSources
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Properties
argument_list|>
argument_list|()
expr_stmt|;
name|variables
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|int
name|position
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|position
operator|<
name|defaults
operator|.
name|size
argument_list|()
condition|)
block|{
if|if
condition|(
name|defaults
operator|.
name|getName
argument_list|(
name|position
argument_list|)
operator|==
literal|null
condition|)
break|break;
name|String
name|name
init|=
name|defaults
operator|.
name|getName
argument_list|(
name|position
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"datasource"
argument_list|)
condition|)
block|{
name|NamedList
name|dsConfig
init|=
operator|(
name|NamedList
operator|)
name|defaults
operator|.
name|getVal
argument_list|(
name|position
argument_list|)
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
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
name|dsConfig
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|props
operator|.
name|put
argument_list|(
name|dsConfig
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|dsConfig
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding properties to datasource: "
operator|+
name|props
argument_list|)
expr_stmt|;
name|dataSources
operator|.
name|put
argument_list|(
operator|(
name|String
operator|)
name|dsConfig
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"config"
argument_list|)
condition|)
block|{
name|String
name|value
init|=
operator|(
name|String
operator|)
name|defaults
operator|.
name|getVal
argument_list|(
name|position
argument_list|)
decl_stmt|;
name|variables
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|position
operator|++
expr_stmt|;
block|}
block|}
DECL|method|getSolrWriter
specifier|private
name|SolrWriter
name|getSolrWriter
parameter_list|(
specifier|final
name|UpdateRequestProcessor
name|processor
parameter_list|,
specifier|final
name|SolrResourceLoader
name|loader
parameter_list|,
specifier|final
name|IndexSchema
name|schema
parameter_list|)
block|{
return|return
operator|new
name|SolrWriter
argument_list|(
name|processor
argument_list|,
name|loader
operator|.
name|getConfigDir
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|upload
parameter_list|(
name|SolrDoc
name|d
parameter_list|)
block|{
try|try
block|{
name|SolrInputDocument
name|document
init|=
operator|(
operator|(
name|SolrDocumentWrapper
operator|)
name|d
operator|)
operator|.
name|doc
decl_stmt|;
if|if
condition|(
name|requestParams
operator|.
name|debug
condition|)
block|{
if|if
condition|(
name|debugDocuments
operator|==
literal|null
condition|)
name|debugDocuments
operator|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
expr_stmt|;
name|debugDocuments
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|debugDocuments
operator|.
name|size
argument_list|()
operator|>=
name|requestParams
operator|.
name|rows
condition|)
block|{
comment|// Abort this operation now
name|importer
operator|.
name|getDocBuilder
argument_list|()
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|super
operator|.
name|upload
argument_list|(
name|document
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|log
argument_list|(
name|Level
operator|.
name|SEVERE
argument_list|,
literal|"Exception while adding: "
operator|+
name|d
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|void
name|log
parameter_list|(
name|int
name|event
parameter_list|,
name|String
name|name
parameter_list|,
name|Object
name|row
parameter_list|)
block|{
if|if
condition|(
name|debugLogger
operator|==
literal|null
condition|)
block|{
name|debugLogger
operator|=
operator|new
name|DebugLogger
argument_list|()
expr_stmt|;
block|}
name|debugLogger
operator|.
name|log
argument_list|(
name|event
argument_list|,
name|name
argument_list|,
name|row
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Class
name|loadClass
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
return|return
name|loader
operator|.
name|findClass
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|SolrDoc
name|getSolrDocInstance
parameter_list|()
block|{
return|return
operator|new
name|SolrDocumentWrapper
argument_list|()
return|;
block|}
block|}
return|;
block|}
DECL|class|SolrDocumentWrapper
specifier|static
class|class
name|SolrDocumentWrapper
implements|implements
name|SolrWriter
operator|.
name|SolrDoc
block|{
DECL|field|doc
name|SolrInputDocument
name|doc
decl_stmt|;
DECL|method|SolrDocumentWrapper
specifier|public
name|SolrDocumentWrapper
parameter_list|()
block|{
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
block|}
DECL|method|setDocumentBoost
specifier|public
name|void
name|setDocumentBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|doc
operator|.
name|setDocumentBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
block|}
DECL|method|getField
specifier|public
name|Object
name|getField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|doc
operator|.
name|getField
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|doc
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
if|if
condition|(
name|importer
operator|==
literal|null
condition|)
return|return
name|super
operator|.
name|getStatistics
argument_list|()
return|;
name|DocBuilder
operator|.
name|Statistics
name|cumulative
init|=
name|importer
operator|.
name|cumulativeStatistics
decl_stmt|;
name|NamedList
name|result
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"Status"
argument_list|,
name|importer
operator|.
name|getStatus
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|importer
operator|.
name|docBuilder
operator|!=
literal|null
condition|)
block|{
name|DocBuilder
operator|.
name|Statistics
name|running
init|=
name|importer
operator|.
name|docBuilder
operator|.
name|importStatistics
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"Documents Processed"
argument_list|,
name|running
operator|.
name|docCount
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"Requests made to DataSource"
argument_list|,
name|running
operator|.
name|queryCount
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"Rows Fetched"
argument_list|,
name|running
operator|.
name|rowsCount
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"Documents Deleted"
argument_list|,
name|running
operator|.
name|deletedDocCount
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"Documents Skipped"
argument_list|,
name|running
operator|.
name|skipDocCount
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|DataImporter
operator|.
name|MSG
operator|.
name|TOTAL_DOC_PROCESSED
argument_list|,
name|cumulative
operator|.
name|docCount
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|DataImporter
operator|.
name|MSG
operator|.
name|TOTAL_QUERIES_EXECUTED
argument_list|,
name|cumulative
operator|.
name|queryCount
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|DataImporter
operator|.
name|MSG
operator|.
name|TOTAL_ROWS_EXECUTED
argument_list|,
name|cumulative
operator|.
name|rowsCount
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|DataImporter
operator|.
name|MSG
operator|.
name|TOTAL_DOCS_DELETED
argument_list|,
name|cumulative
operator|.
name|deletedDocCount
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|DataImporter
operator|.
name|MSG
operator|.
name|TOTAL_DOCS_SKIPPED
argument_list|,
name|cumulative
operator|.
name|skipDocCount
argument_list|)
expr_stmt|;
name|NamedList
name|requestStatistics
init|=
name|super
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
if|if
condition|(
name|requestStatistics
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|requestStatistics
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|requestStatistics
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|requestStatistics
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|// //////////////////////SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|DataImporter
operator|.
name|MSG
operator|.
name|JMX_DESC
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
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"1.0"
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
DECL|field|ENABLE_DEBUG
specifier|public
specifier|static
specifier|final
name|String
name|ENABLE_DEBUG
init|=
literal|"enableDebug"
decl_stmt|;
block|}
end_class
end_unit
