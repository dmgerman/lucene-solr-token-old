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
name|Collection
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
name|logging
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
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
name|request
operator|.
name|StandardRequestHandler
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
name|DOMUtil
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
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import
begin_comment
comment|/**  * @author yonik  */
end_comment
begin_class
DECL|class|RequestHandlers
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
name|Logger
operator|.
name|getLogger
argument_list|(
name|RequestHandlers
operator|.
name|class
operator|.
name|getName
argument_list|()
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
comment|// Use a synchronized map - since the handlers can be changed at runtime,
comment|// the map implementaion should be thread safe
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
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
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
name|handlerName
argument_list|)
return|;
block|}
comment|/**    * Handlers must be initalized before calling this function.  As soon as this is    * called, the handler can immediatly accept requests.    *     * This call is thread safe.    *     * @return the previous handler at the given path or null    */
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
name|handlerName
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
name|handlerName
argument_list|,
name|handler
argument_list|)
decl_stmt|;
if|if
condition|(
name|handlerName
operator|!=
literal|null
operator|&&
name|handlerName
operator|!=
literal|""
condition|)
block|{
if|if
condition|(
name|handler
operator|instanceof
name|SolrInfoMBean
condition|)
block|{
name|SolrInfoRegistry
operator|.
name|getRegistry
argument_list|()
operator|.
name|put
argument_list|(
name|handlerName
argument_list|,
operator|(
name|SolrInfoMBean
operator|)
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|old
return|;
block|}
comment|/**    * Returns an unmodifieable Map containing the registered handlers    */
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
comment|/**    * Read solrconfig.xml and register the appropriate handlers    *     * This function should<b>only</b> be called from the SolrCore constructor.  It is    * not intended as a public API.    *     * While the normal runtime registration contract is that handlers MUST be initalizad     * before they are registered, this function does not do that exactly.    *     * This funciton registers all handlers first and then calls init() for each one.      *     * This is OK because this function is only called at startup and there is no chance that    * a handler could be asked to handle a request before it is initalized.    *     * The advantage to this approach is that handlers can know what path they are registered    * to and what other handlers are avaliable at startup.    *     * Handlers will be registered and initalized in the order they appear in solrconfig.xml    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|initHandlersFromConfig
name|void
name|initHandlersFromConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|NodeList
name|nodes
init|=
operator|(
name|NodeList
operator|)
name|config
operator|.
name|evaluate
argument_list|(
literal|"requestHandler"
argument_list|,
name|XPathConstants
operator|.
name|NODESET
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|!=
literal|null
condition|)
block|{
comment|// make sure it only once/handler and that that handlers get initalized in the
comment|// order they were defined
name|Map
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|names
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
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
name|nodes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|node
init|=
name|nodes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// In a production environment, we can tolerate an error in some request handlers,
comment|// still load the others, and have a working system.
try|try
block|{
name|String
name|name
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"name"
argument_list|,
literal|"requestHandler config"
argument_list|)
decl_stmt|;
name|String
name|className
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"class"
argument_list|,
literal|"requestHandler config"
argument_list|)
decl_stmt|;
name|String
name|startup
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"startup"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|args
init|=
name|DOMUtil
operator|.
name|childNodesToNamedList
argument_list|(
name|node
argument_list|)
decl_stmt|;
comment|// Perhaps lazy load the request handler with a wrapper
name|SolrRequestHandler
name|handler
init|=
literal|null
decl_stmt|;
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
name|name
operator|+
literal|"="
operator|+
name|className
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|LazyRequestHandlerWrapper
argument_list|(
name|className
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Class
argument_list|<
name|?
extends|extends
name|SolrRequestHandler
argument_list|>
name|clazz
init|=
name|Config
operator|.
name|findClass
argument_list|(
name|className
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"adding requestHandler: "
operator|+
name|name
operator|+
literal|"="
operator|+
name|className
argument_list|)
expr_stmt|;
name|handler
operator|=
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
name|SolrRequestHandler
name|old
init|=
name|register
argument_list|(
name|name
argument_list|,
name|handler
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
comment|// TODO: SOLR-179?
name|log
operator|.
name|warning
argument_list|(
literal|"multiple handlers registered on the same path! ignoring: "
operator|+
name|old
argument_list|)
expr_stmt|;
block|}
name|names
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO: SOLR-179
name|SolrException
operator|.
name|logOnce
argument_list|(
name|log
argument_list|,
literal|null
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Call init() on each handler after they have all been registered
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|reg
range|:
name|names
operator|.
name|entrySet
argument_list|()
control|)
block|{
try|try
block|{
name|handlers
operator|.
name|get
argument_list|(
name|reg
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|init
argument_list|(
name|reg
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO: SOLR-179
name|SolrException
operator|.
name|logOnce
argument_list|(
name|log
argument_list|,
literal|null
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//
comment|// Get the default handler and add it in the map under null and empty
comment|// to act as the default.
comment|//
name|SolrRequestHandler
name|handler
init|=
name|get
argument_list|(
name|RequestHandlers
operator|.
name|DEFAULT_HANDLER_NAME
argument_list|)
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
operator|new
name|StandardRequestHandler
argument_list|()
expr_stmt|;
name|register
argument_list|(
name|RequestHandlers
operator|.
name|DEFAULT_HANDLER_NAME
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
name|register
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|)
expr_stmt|;
name|register
argument_list|(
literal|""
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
comment|/**    * The<code>LazyRequestHandlerWrapper</core> wraps any {@link SolrRequestHandler}.      * Rather then instanciate and initalize the handler on startup, this wrapper waits    * untill it is actually called.  This should only be used for handlers that are    * unlikely to be used in the normal lifecycle.    *     * You can enable lazy loading in solrconfig.xml using:    *     *<pre>    *&lt;requestHandler name="..." class="..." startup="lazy"&gt;    *    ...    *&lt;/requestHandler&gt;    *</pre>    *     * This is a private class - if there is a real need for it to be public, it could    * move    *     * @author ryan    * @version $Id$    * @since solr 1.2    */
DECL|class|LazyRequestHandlerWrapper
specifier|private
specifier|static
specifier|final
class|class
name|LazyRequestHandlerWrapper
implements|implements
name|SolrRequestHandler
implements|,
name|SolrInfoMBean
block|{
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
name|String
name|className
parameter_list|,
name|NamedList
name|args
parameter_list|)
block|{
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
comment|// don't initalize
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
comment|/**      * Wait for the first request before initalizing the wrapped handler       */
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
name|getWrappedHandler
argument_list|()
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
name|Class
name|clazz
init|=
name|Config
operator|.
name|findClass
argument_list|(
name|_className
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
decl_stmt|;
name|_handler
operator|=
operator|(
name|SolrRequestHandler
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|_handler
operator|.
name|init
argument_list|(
name|_args
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
literal|500
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
literal|"not initaized yet"
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
