begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|net
operator|.
name|URL
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
name|LinkedHashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|SolrRequestHandler
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
name|util
operator|.
name|plugin
operator|.
name|PluginInfoInitialized
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
begin_comment
comment|/**  */
end_comment
begin_class
DECL|class|RequestHandlers
specifier|public
specifier|final
class|class
name|RequestHandlers
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
name|RequestHandlers
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_HANDLER_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_HANDLER_NAME
init|=
literal|"standard"
decl_stmt|;
DECL|field|core
specifier|protected
specifier|final
name|SolrCore
name|core
decl_stmt|;
comment|// Use a synchronized map - since the handlers can be changed at runtime,
comment|// the map implementation should be thread safe
DECL|field|handlers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
name|handlers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Trim the trailing '/' if its there, and convert null to empty string.    *     * we want:    *  /update/csv   and    *  /update/csv/    * to map to the same handler     *     */
DECL|method|normalize
specifier|private
specifier|static
name|String
name|normalize
parameter_list|(
name|String
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|==
literal|null
condition|)
return|return
literal|""
return|;
if|if
condition|(
name|p
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|&&
name|p
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
return|return
name|p
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
return|return
name|p
return|;
block|}
DECL|method|RequestHandlers
specifier|public
name|RequestHandlers
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
block|}
comment|/**    * @return the RequestHandler registered at the given name     */
DECL|method|get
specifier|public
name|SolrRequestHandler
name|get
parameter_list|(
name|String
name|handlerName
parameter_list|)
block|{
return|return
name|handlers
operator|.
name|get
argument_list|(
name|normalize
argument_list|(
name|handlerName
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @return a Map of all registered handlers of the specified type.    */
DECL|method|getAll
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
name|getAll
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
argument_list|(
literal|7
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
name|e
range|:
name|handlers
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
name|result
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Handlers must be initialized before calling this function.  As soon as this is    * called, the handler can immediately accept requests.    *     * This call is thread safe.    *     * @return the previous handler at the given path or null    */
DECL|method|register
specifier|public
name|SolrRequestHandler
name|register
parameter_list|(
name|String
name|handlerName
parameter_list|,
name|SolrRequestHandler
name|handler
parameter_list|)
block|{
name|String
name|norm
init|=
name|normalize
argument_list|(
name|handlerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
return|return
name|handlers
operator|.
name|remove
argument_list|(
name|norm
argument_list|)
return|;
block|}
name|SolrRequestHandler
name|old
init|=
name|handlers
operator|.
name|put
argument_list|(
name|norm
argument_list|,
name|handler
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|!=
name|norm
operator|.
name|length
argument_list|()
operator|&&
name|handler
operator|instanceof
name|SolrInfoMBean
condition|)
block|{
name|core
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|put
argument_list|(
name|handlerName
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
return|return
name|old
return|;
block|}
comment|/**    * Returns an unmodifiable Map containing the registered handlers    */
DECL|method|getRequestHandlers
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
name|getRequestHandlers
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|handlers
argument_list|)
return|;
block|}
comment|/**    * Read solrconfig.xml and register the appropriate handlers    *     * This function should<b>only</b> be called from the SolrCore constructor.  It is    * not intended as a public API.    *     * While the normal runtime registration contract is that handlers MUST be initialized    * before they are registered, this function does not do that exactly.    *    * This function registers all handlers first and then calls init() for each one.    *    * This is OK because this function is only called at startup and there is no chance that    * a handler could be asked to handle a request before it is initialized.    *     * The advantage to this approach is that handlers can know what path they are registered    * to and what other handlers are available at startup.    *     * Handlers will be registered and initialized in the order they appear in solrconfig.xml    */
DECL|method|initHandlersFromConfig
name|void
name|initHandlersFromConfig
parameter_list|(
name|SolrConfig
name|config
parameter_list|)
block|{
comment|// use link map so we iterate in the same order
name|Map
argument_list|<
name|PluginInfo
argument_list|,
name|SolrRequestHandler
argument_list|>
name|handlers
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|PluginInfo
argument_list|,
name|SolrRequestHandler
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PluginInfo
name|info
range|:
name|config
operator|.
name|getPluginInfos
argument_list|(
name|SolrRequestHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
control|)
block|{
try|try
block|{
name|SolrRequestHandler
name|requestHandler
decl_stmt|;
name|String
name|startup
init|=
name|info
operator|.
name|attributes
operator|.
name|get
argument_list|(
literal|"startup"
argument_list|)
decl_stmt|;
if|if
condition|(
name|startup
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"lazy"
operator|.
name|equals
argument_list|(
name|startup
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"adding lazy requestHandler: "
operator|+
name|info
operator|.
name|className
argument_list|)
expr_stmt|;
name|requestHandler
operator|=
operator|new
name|LazyRequestHandlerWrapper
argument_list|(
name|core
argument_list|,
name|info
operator|.
name|className
argument_list|,
name|info
operator|.
name|initArgs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Unknown startup value: '"
operator|+
name|startup
operator|+
literal|"' for: "
operator|+
name|info
operator|.
name|className
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|requestHandler
operator|=
name|core
operator|.
name|createRequestHandler
argument_list|(
name|info
operator|.
name|className
argument_list|)
expr_stmt|;
block|}
name|handlers
operator|.
name|put
argument_list|(
name|info
argument_list|,
name|requestHandler
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|old
init|=
name|register
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|requestHandler
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Multiple requestHandler registered to the same name: "
operator|+
name|info
operator|.
name|name
operator|+
literal|" ignoring: "
operator|+
name|old
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|isDefault
argument_list|()
condition|)
block|{
name|old
operator|=
name|register
argument_list|(
literal|""
argument_list|,
name|requestHandler
argument_list|)
expr_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
name|log
operator|.
name|warn
argument_list|(
literal|"Multiple default requestHandler registered"
operator|+
literal|" ignoring: "
operator|+
name|old
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"created "
operator|+
name|info
operator|.
name|name
operator|+
literal|": "
operator|+
name|info
operator|.
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"RequestHandler init failure"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|// we've now registered all handlers, time to init them in the same order
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|PluginInfo
argument_list|,
name|SolrRequestHandler
argument_list|>
name|entry
range|:
name|handlers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|PluginInfo
name|info
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|SolrRequestHandler
name|requestHandler
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|requestHandler
operator|instanceof
name|PluginInfoInitialized
condition|)
block|{
operator|(
operator|(
name|PluginInfoInitialized
operator|)
name|requestHandler
operator|)
operator|.
name|init
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|requestHandler
operator|.
name|init
argument_list|(
name|info
operator|.
name|initArgs
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|get
argument_list|(
literal|""
argument_list|)
operator|==
literal|null
condition|)
name|register
argument_list|(
literal|""
argument_list|,
name|get
argument_list|(
name|DEFAULT_HANDLER_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * The<code>LazyRequestHandlerWrapper</core> wraps any {@link SolrRequestHandler}.      * Rather then instanciate and initalize the handler on startup, this wrapper waits    * until it is actually called.  This should only be used for handlers that are    * unlikely to be used in the normal lifecycle.    *     * You can enable lazy loading in solrconfig.xml using:    *     *<pre>    *&lt;requestHandler name="..." class="..." startup="lazy"&gt;    *    ...    *&lt;/requestHandler&gt;    *</pre>    *     * This is a private class - if there is a real need for it to be public, it could    * move    *     * @since solr 1.2    */
DECL|class|LazyRequestHandlerWrapper
specifier|public
specifier|static
specifier|final
class|class
name|LazyRequestHandlerWrapper
implements|implements
name|SolrRequestHandler
implements|,
name|SolrInfoMBean
block|{
DECL|field|core
specifier|private
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|_className
specifier|private
name|String
name|_className
decl_stmt|;
DECL|field|_args
specifier|private
name|NamedList
name|_args
decl_stmt|;
DECL|field|_handler
specifier|private
name|SolrRequestHandler
name|_handler
decl_stmt|;
DECL|method|LazyRequestHandlerWrapper
specifier|public
name|LazyRequestHandlerWrapper
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|className
parameter_list|,
name|NamedList
name|args
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|_className
operator|=
name|className
expr_stmt|;
name|_args
operator|=
name|args
expr_stmt|;
name|_handler
operator|=
literal|null
expr_stmt|;
comment|// don't initialize
block|}
comment|/**      * In normal use, this function will not be called      */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
comment|// do nothing
block|}
comment|/**      * Wait for the first request before initializing the wrapped handler       */
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|SolrRequestHandler
name|handler
init|=
name|_handler
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
name|handler
operator|=
name|getWrappedHandler
argument_list|()
expr_stmt|;
block|}
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|getWrappedHandler
specifier|public
specifier|synchronized
name|SolrRequestHandler
name|getWrappedHandler
parameter_list|()
block|{
if|if
condition|(
name|_handler
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|createRequestHandler
argument_list|(
name|_className
argument_list|)
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
name|_args
argument_list|)
expr_stmt|;
if|if
condition|(
name|handler
operator|instanceof
name|SolrCoreAware
condition|)
block|{
operator|(
operator|(
name|SolrCoreAware
operator|)
name|handler
operator|)
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
name|_handler
operator|=
name|handler
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
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
literal|"lazy loading error"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
return|return
name|_handler
return|;
block|}
DECL|method|getHandlerClass
specifier|public
name|String
name|getHandlerClass
parameter_list|()
block|{
return|return
name|_className
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"Lazy["
operator|+
name|_className
operator|+
literal|"]"
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
if|if
condition|(
name|_handler
operator|==
literal|null
condition|)
block|{
return|return
name|getName
argument_list|()
return|;
block|}
return|return
name|_handler
operator|.
name|getDescription
argument_list|()
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
name|String
name|rev
init|=
literal|"$Revision$"
decl_stmt|;
if|if
condition|(
name|_handler
operator|!=
literal|null
condition|)
block|{
name|rev
operator|+=
literal|" :: "
operator|+
name|_handler
operator|.
name|getVersion
argument_list|()
expr_stmt|;
block|}
return|return
name|rev
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
name|String
name|rev
init|=
literal|"$Id$"
decl_stmt|;
if|if
condition|(
name|_handler
operator|!=
literal|null
condition|)
block|{
name|rev
operator|+=
literal|" :: "
operator|+
name|_handler
operator|.
name|getSourceId
argument_list|()
expr_stmt|;
block|}
return|return
name|rev
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
name|String
name|rev
init|=
literal|"$URL$"
decl_stmt|;
if|if
condition|(
name|_handler
operator|!=
literal|null
condition|)
block|{
name|rev
operator|+=
literal|"\n"
operator|+
name|_handler
operator|.
name|getSource
argument_list|()
expr_stmt|;
block|}
return|return
name|rev
return|;
block|}
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
if|if
condition|(
name|_handler
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|_handler
operator|.
name|getDocs
argument_list|()
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|QUERYHANDLER
return|;
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
if|if
condition|(
name|_handler
operator|!=
literal|null
condition|)
block|{
return|return
name|_handler
operator|.
name|getStatistics
argument_list|()
return|;
block|}
name|NamedList
argument_list|<
name|String
argument_list|>
name|lst
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"note"
argument_list|,
literal|"not initialized yet"
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
block|}
block|}
end_class
end_unit
