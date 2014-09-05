begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|SolrException
operator|.
name|ErrorCode
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
name|MapSolrParams
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
name|PluginInfo
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
name|loader
operator|.
name|CSVLoader
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
name|loader
operator|.
name|ContentStreamLoader
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
name|loader
operator|.
name|JavabinLoader
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
name|loader
operator|.
name|JsonLoader
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
name|loader
operator|.
name|XMLLoader
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
begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import
begin_import
import|import static
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
operator|.
name|makeMap
import|;
end_import
begin_comment
comment|/**  * UpdateHandler that uses content-type to pick the right Loader  */
end_comment
begin_class
DECL|class|UpdateRequestHandler
specifier|public
class|class
name|UpdateRequestHandler
extends|extends
name|ContentStreamHandlerBase
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UpdateRequestHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// XML Constants
DECL|field|ADD
specifier|public
specifier|static
specifier|final
name|String
name|ADD
init|=
literal|"add"
decl_stmt|;
DECL|field|DELETE
specifier|public
specifier|static
specifier|final
name|String
name|DELETE
init|=
literal|"delete"
decl_stmt|;
DECL|field|OPTIMIZE
specifier|public
specifier|static
specifier|final
name|String
name|OPTIMIZE
init|=
literal|"optimize"
decl_stmt|;
DECL|field|COMMIT
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT
init|=
literal|"commit"
decl_stmt|;
DECL|field|ROLLBACK
specifier|public
specifier|static
specifier|final
name|String
name|ROLLBACK
init|=
literal|"rollback"
decl_stmt|;
DECL|field|WAIT_SEARCHER
specifier|public
specifier|static
specifier|final
name|String
name|WAIT_SEARCHER
init|=
literal|"waitSearcher"
decl_stmt|;
DECL|field|SOFT_COMMIT
specifier|public
specifier|static
specifier|final
name|String
name|SOFT_COMMIT
init|=
literal|"softCommit"
decl_stmt|;
DECL|field|OVERWRITE
specifier|public
specifier|static
specifier|final
name|String
name|OVERWRITE
init|=
literal|"overwrite"
decl_stmt|;
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|String
name|VERSION
init|=
literal|"version"
decl_stmt|;
comment|// NOTE: This constant is for use with the<add> XML tag, not the HTTP param with same name
DECL|field|COMMIT_WITHIN
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_WITHIN
init|=
literal|"commitWithin"
decl_stmt|;
DECL|field|loaders
name|Map
argument_list|<
name|String
argument_list|,
name|ContentStreamLoader
argument_list|>
name|loaders
init|=
literal|null
decl_stmt|;
DECL|field|instance
name|ContentStreamLoader
name|instance
init|=
operator|new
name|ContentStreamLoader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
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
parameter_list|,
name|UpdateRequestProcessor
name|processor
parameter_list|)
throws|throws
name|Exception
block|{
name|ContentStreamLoader
name|loader
init|=
name|pathVsLoaders
operator|.
name|get
argument_list|(
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"$$$$$$$ used the pathVsLoaders {} "
argument_list|,
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
name|String
name|type
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|UpdateParams
operator|.
name|ASSUME_CONTENT_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|stream
operator|.
name|getContentType
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
comment|// Normal requests will not get here.
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|UNSUPPORTED_MEDIA_TYPE
argument_list|,
literal|"Missing ContentType"
argument_list|)
throw|;
block|}
name|int
name|idx
init|=
name|type
operator|.
name|indexOf
argument_list|(
literal|';'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|type
operator|=
name|type
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
name|loader
operator|=
name|loaders
operator|.
name|get
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|UNSUPPORTED_MEDIA_TYPE
argument_list|,
literal|"Unsupported ContentType: "
operator|+
name|type
operator|+
literal|"  Not in: "
operator|+
name|loaders
operator|.
name|keySet
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|loader
operator|.
name|getDefaultWT
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|setDefaultWT
argument_list|(
name|req
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
name|loader
operator|.
name|load
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|stream
argument_list|,
name|processor
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setDefaultWT
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|ContentStreamLoader
name|loader
parameter_list|)
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|)
operator|==
literal|null
condition|)
block|{
name|String
name|wt
init|=
name|loader
operator|.
name|getDefaultWT
argument_list|()
decl_stmt|;
comment|// Make sure it is a valid writer
if|if
condition|(
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getQueryResponseWriter
argument_list|(
name|wt
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|,
name|wt
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|SolrParams
operator|.
name|wrapDefaults
argument_list|(
name|params
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|map
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
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
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// Since backed by a non-thread safe Map, it should not be modifiable
name|loaders
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|createDefaultLoaders
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setAssumeContentType
specifier|protected
name|void
name|setAssumeContentType
parameter_list|(
name|String
name|ct
parameter_list|)
block|{
if|if
condition|(
name|invariants
operator|==
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|UpdateParams
operator|.
name|ASSUME_CONTENT_TYPE
argument_list|,
name|ct
argument_list|)
expr_stmt|;
name|invariants
operator|=
operator|new
name|MapSolrParams
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|invariants
argument_list|)
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|UpdateParams
operator|.
name|ASSUME_CONTENT_TYPE
argument_list|,
name|ct
argument_list|)
expr_stmt|;
name|invariants
operator|=
name|params
expr_stmt|;
block|}
block|}
DECL|field|pathVsLoaders
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ContentStreamLoader
argument_list|>
name|pathVsLoaders
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|createDefaultLoaders
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|ContentStreamLoader
argument_list|>
name|createDefaultLoaders
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|SolrParams
name|p
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
name|p
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|ContentStreamLoader
argument_list|>
name|registry
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"application/xml"
argument_list|,
operator|new
name|XMLLoader
argument_list|()
operator|.
name|init
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"application/json"
argument_list|,
operator|new
name|JsonLoader
argument_list|()
operator|.
name|init
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"application/csv"
argument_list|,
operator|new
name|CSVLoader
argument_list|()
operator|.
name|init
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"application/javabin"
argument_list|,
operator|new
name|JavabinLoader
argument_list|()
operator|.
name|init
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"text/csv"
argument_list|,
name|registry
operator|.
name|get
argument_list|(
literal|"application/csv"
argument_list|)
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"text/xml"
argument_list|,
name|registry
operator|.
name|get
argument_list|(
literal|"application/xml"
argument_list|)
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"text/json"
argument_list|,
name|registry
operator|.
name|get
argument_list|(
literal|"application/json"
argument_list|)
argument_list|)
expr_stmt|;
name|pathVsLoaders
operator|.
name|put
argument_list|(
name|JSON_PATH
argument_list|,
name|registry
operator|.
name|get
argument_list|(
literal|"application/json"
argument_list|)
argument_list|)
expr_stmt|;
name|pathVsLoaders
operator|.
name|put
argument_list|(
name|DOC_PATH
argument_list|,
name|registry
operator|.
name|get
argument_list|(
literal|"application/json"
argument_list|)
argument_list|)
expr_stmt|;
name|pathVsLoaders
operator|.
name|put
argument_list|(
name|CSV_PATH
argument_list|,
name|registry
operator|.
name|get
argument_list|(
literal|"application/csv"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|registry
return|;
block|}
annotation|@
name|Override
DECL|method|newLoader
specifier|protected
name|ContentStreamLoader
name|newLoader
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
specifier|final
name|UpdateRequestProcessor
name|processor
parameter_list|)
block|{
return|return
name|instance
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
literal|"Add documents using XML (with XSLT), CSV, JSON, or javabin"
return|;
block|}
DECL|method|addImplicits
specifier|public
specifier|static
name|void
name|addImplicits
parameter_list|(
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|implicits
parameter_list|)
block|{
name|implicits
operator|.
name|add
argument_list|(
name|getPluginInfo
argument_list|(
literal|"/update"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getPluginInfo
argument_list|(
name|JSON_PATH
argument_list|,
name|singletonMap
argument_list|(
literal|"update.contentType"
argument_list|,
literal|"application/json"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getPluginInfo
argument_list|(
name|CSV_PATH
argument_list|,
name|singletonMap
argument_list|(
literal|"update.contentType"
argument_list|,
literal|"application/csv"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|implicits
operator|.
name|add
argument_list|(
name|getPluginInfo
argument_list|(
name|DOC_PATH
argument_list|,
name|makeMap
argument_list|(
literal|"update.contentType"
argument_list|,
literal|"application/json"
argument_list|,
literal|"json.command"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getPluginInfo
specifier|static
name|PluginInfo
name|getPluginInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
name|defaults
parameter_list|)
block|{
name|Map
name|m
init|=
name|makeMap
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|,
literal|"class"
argument_list|,
name|UpdateRequestHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|PluginInfo
argument_list|(
literal|"requestHandler"
argument_list|,
name|m
argument_list|,
operator|new
name|NamedList
argument_list|<>
argument_list|(
name|singletonMap
argument_list|(
literal|"defaults"
argument_list|,
operator|new
name|NamedList
argument_list|(
name|defaults
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|field|DOC_PATH
specifier|public
specifier|static
specifier|final
name|String
name|DOC_PATH
init|=
literal|"/update/json/docs"
decl_stmt|;
DECL|field|JSON_PATH
specifier|public
specifier|static
specifier|final
name|String
name|JSON_PATH
init|=
literal|"/update/json"
decl_stmt|;
DECL|field|CSV_PATH
specifier|public
specifier|static
specifier|final
name|String
name|CSV_PATH
init|=
literal|"/update/csv"
decl_stmt|;
block|}
end_class
end_unit
