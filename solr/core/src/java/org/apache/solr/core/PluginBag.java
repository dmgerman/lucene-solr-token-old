begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|ResourceLoader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|ResourceLoaderAware
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
name|CloudUtil
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
name|util
operator|.
name|StrUtils
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
name|component
operator|.
name|SearchComponent
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
name|util
operator|.
name|CryptoKeys
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
name|NamedListInitializedPlugin
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
name|params
operator|.
name|CommonParams
operator|.
name|NAME
import|;
end_import
begin_comment
comment|/**  * This manages the lifecycle of a set of plugin of the same type .  */
end_comment
begin_class
DECL|class|PluginBag
specifier|public
class|class
name|PluginBag
parameter_list|<
name|T
parameter_list|>
implements|implements
name|AutoCloseable
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
name|PluginBag
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PluginHolder
argument_list|<
name|T
argument_list|>
argument_list|>
name|registry
decl_stmt|;
DECL|field|immutableRegistry
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PluginHolder
argument_list|<
name|T
argument_list|>
argument_list|>
name|immutableRegistry
decl_stmt|;
DECL|field|def
specifier|private
name|String
name|def
decl_stmt|;
DECL|field|klass
specifier|private
specifier|final
name|Class
name|klass
decl_stmt|;
DECL|field|core
specifier|private
name|SolrCore
name|core
decl_stmt|;
DECL|field|meta
specifier|private
specifier|final
name|SolrConfig
operator|.
name|SolrPluginInfo
name|meta
decl_stmt|;
comment|/**    * Pass needThreadSafety=true if plugins can be added and removed concurrently with lookups.    */
DECL|method|PluginBag
specifier|public
name|PluginBag
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|klass
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|boolean
name|needThreadSafety
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|klass
operator|=
name|klass
expr_stmt|;
comment|// TODO: since reads will dominate writes, we could also think about creating a new instance of a map each time it changes.
comment|// Not sure how much benefit this would have over ConcurrentHashMap though
comment|// We could also perhaps make this constructor into a factory method to return different implementations depending on thread safety needs.
name|this
operator|.
name|registry
operator|=
name|needThreadSafety
condition|?
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
else|:
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|immutableRegistry
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|registry
argument_list|)
expr_stmt|;
name|meta
operator|=
name|SolrConfig
operator|.
name|classVsSolrPluginInfo
operator|.
name|get
argument_list|(
name|klass
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|meta
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
name|SERVER_ERROR
argument_list|,
literal|"Unknown Plugin : "
operator|+
name|klass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Constructs a non-threadsafe plugin registry    */
DECL|method|PluginBag
specifier|public
name|PluginBag
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|klass
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
name|this
argument_list|(
name|klass
argument_list|,
name|core
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|initInstance
specifier|static
name|void
name|initInstance
parameter_list|(
name|Object
name|inst
parameter_list|,
name|PluginInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|inst
operator|instanceof
name|PluginInfoInitialized
condition|)
block|{
operator|(
operator|(
name|PluginInfoInitialized
operator|)
name|inst
operator|)
operator|.
name|init
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inst
operator|instanceof
name|NamedListInitializedPlugin
condition|)
block|{
operator|(
operator|(
name|NamedListInitializedPlugin
operator|)
name|inst
operator|)
operator|.
name|init
argument_list|(
name|info
operator|.
name|initArgs
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inst
operator|instanceof
name|SolrRequestHandler
condition|)
block|{
operator|(
operator|(
name|SolrRequestHandler
operator|)
name|inst
operator|)
operator|.
name|init
argument_list|(
name|info
operator|.
name|initArgs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|inst
operator|instanceof
name|SearchComponent
condition|)
block|{
operator|(
operator|(
name|SearchComponent
operator|)
name|inst
operator|)
operator|.
name|setName
argument_list|(
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|inst
operator|instanceof
name|RequestHandlerBase
condition|)
block|{
operator|(
operator|(
name|RequestHandlerBase
operator|)
name|inst
operator|)
operator|.
name|setPluginInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createPlugin
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|createPlugin
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|info
operator|.
name|attributes
operator|.
name|get
argument_list|(
literal|"runtimeLib"
argument_list|)
argument_list|)
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|" {} : '{}'  created with runtimeLib=true "
argument_list|,
name|meta
operator|.
name|getCleanTag
argument_list|()
argument_list|,
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|LazyPluginHolder
argument_list|<>
argument_list|(
name|meta
argument_list|,
name|info
argument_list|,
name|core
argument_list|,
name|core
operator|.
name|getMemClassLoader
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
literal|"lazy"
operator|.
name|equals
argument_list|(
name|info
operator|.
name|attributes
operator|.
name|get
argument_list|(
literal|"startup"
argument_list|)
argument_list|)
operator|&&
name|meta
operator|.
name|options
operator|.
name|contains
argument_list|(
name|SolrConfig
operator|.
name|PluginOpts
operator|.
name|LAZY
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"{} : '{}' created with startup=lazy "
argument_list|,
name|meta
operator|.
name|getCleanTag
argument_list|()
argument_list|,
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
return|return
operator|new
name|LazyPluginHolder
argument_list|<
name|T
argument_list|>
argument_list|(
name|meta
argument_list|,
name|info
argument_list|,
name|core
argument_list|,
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
name|T
name|inst
init|=
name|core
operator|.
name|createInstance
argument_list|(
name|info
operator|.
name|className
argument_list|,
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|meta
operator|.
name|clazz
argument_list|,
name|meta
operator|.
name|getCleanTag
argument_list|()
argument_list|,
literal|null
argument_list|,
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
decl_stmt|;
name|initInstance
argument_list|(
name|inst
argument_list|,
name|info
argument_list|)
expr_stmt|;
return|return
operator|new
name|PluginHolder
argument_list|<>
argument_list|(
name|info
argument_list|,
name|inst
argument_list|)
return|;
block|}
block|}
DECL|method|alias
name|boolean
name|alias
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|target
parameter_list|)
block|{
if|if
condition|(
name|src
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|a
init|=
name|registry
operator|.
name|get
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|b
init|=
name|registry
operator|.
name|get
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
name|registry
operator|.
name|put
argument_list|(
name|target
argument_list|,
name|a
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Get a plugin by name. If the plugin is not already instantiated, it is    * done here    */
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|result
init|=
name|registry
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|result
operator|==
literal|null
condition|?
literal|null
else|:
name|result
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Fetches a plugin by name , or the default    *    * @param name       name using which it is registered    * @param useDefault Return the default , if a plugin by that name does not exist    */
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|useDefault
parameter_list|)
block|{
name|T
name|result
init|=
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|useDefault
operator|&&
name|result
operator|==
literal|null
condition|)
return|return
name|get
argument_list|(
name|def
argument_list|)
return|;
return|return
name|result
return|;
block|}
DECL|method|keySet
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|keySet
parameter_list|()
block|{
return|return
name|immutableRegistry
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/**    * register a plugin by a name    */
DECL|method|put
specifier|public
name|T
name|put
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|plugin
parameter_list|)
block|{
if|if
condition|(
name|plugin
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|old
init|=
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|PluginHolder
argument_list|<
name|T
argument_list|>
argument_list|(
literal|null
argument_list|,
name|plugin
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|old
operator|==
literal|null
condition|?
literal|null
else|:
name|old
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|put
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|put
parameter_list|(
name|String
name|name
parameter_list|,
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|plugin
parameter_list|)
block|{
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|old
init|=
name|registry
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|plugin
argument_list|)
decl_stmt|;
if|if
condition|(
name|plugin
operator|.
name|pluginInfo
operator|!=
literal|null
operator|&&
name|plugin
operator|.
name|pluginInfo
operator|.
name|isDefault
argument_list|()
condition|)
block|{
name|setDefault
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|plugin
operator|.
name|isLoaded
argument_list|()
condition|)
name|registerMBean
argument_list|(
name|plugin
operator|.
name|get
argument_list|()
argument_list|,
name|core
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|old
return|;
block|}
DECL|method|setDefault
name|void
name|setDefault
parameter_list|(
name|String
name|def
parameter_list|)
block|{
if|if
condition|(
operator|!
name|registry
operator|.
name|containsKey
argument_list|(
name|def
argument_list|)
condition|)
return|return;
if|if
condition|(
name|this
operator|.
name|def
operator|!=
literal|null
condition|)
name|log
operator|.
name|warn
argument_list|(
literal|"Multiple defaults for : "
operator|+
name|meta
operator|.
name|getCleanTag
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|def
operator|=
name|def
expr_stmt|;
block|}
DECL|method|getRegistry
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|PluginHolder
argument_list|<
name|T
argument_list|>
argument_list|>
name|getRegistry
parameter_list|()
block|{
return|return
name|immutableRegistry
return|;
block|}
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|registry
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|getDefault
name|String
name|getDefault
parameter_list|()
block|{
return|return
name|def
return|;
block|}
DECL|method|remove
name|T
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|removed
init|=
name|registry
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|removed
operator|==
literal|null
condition|?
literal|null
else|:
name|removed
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|init
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|defaults
parameter_list|,
name|SolrCore
name|solrCore
parameter_list|)
block|{
name|init
argument_list|(
name|defaults
argument_list|,
name|solrCore
argument_list|,
name|solrCore
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getPluginInfos
argument_list|(
name|klass
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Initializes the plugins after reading the meta data from {@link org.apache.solr.core.SolrConfig}.    *    * @param defaults These will be registered if not explicitly specified    */
DECL|method|init
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|defaults
parameter_list|,
name|SolrCore
name|solrCore
parameter_list|,
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|infos
parameter_list|)
block|{
name|core
operator|=
name|solrCore
expr_stmt|;
for|for
control|(
name|PluginInfo
name|info
range|:
name|infos
control|)
block|{
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|o
init|=
name|createPlugin
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|info
operator|.
name|name
decl_stmt|;
if|if
condition|(
name|meta
operator|.
name|clazz
operator|.
name|equals
argument_list|(
name|SolrRequestHandler
operator|.
name|class
argument_list|)
condition|)
name|name
operator|=
name|RequestHandlers
operator|.
name|normalize
argument_list|(
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|old
init|=
name|put
argument_list|(
name|name
argument_list|,
name|o
argument_list|)
decl_stmt|;
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
literal|"Multiple entries of {} with name {}"
argument_list|,
name|meta
operator|.
name|getCleanTag
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|e
range|:
name|defaults
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|contains
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|PluginHolder
argument_list|<
name|T
argument_list|>
argument_list|(
literal|null
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * To check if a plugin by a specified name is already loaded    */
DECL|method|isLoaded
specifier|public
name|boolean
name|isLoaded
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|PluginHolder
argument_list|<
name|T
argument_list|>
name|result
init|=
name|registry
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|result
operator|.
name|isLoaded
argument_list|()
return|;
block|}
DECL|method|registerMBean
specifier|private
specifier|static
name|void
name|registerMBean
parameter_list|(
name|Object
name|inst
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|String
name|pluginKey
parameter_list|)
block|{
if|if
condition|(
name|core
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|inst
operator|instanceof
name|SolrInfoMBean
condition|)
block|{
name|SolrInfoMBean
name|mBean
init|=
operator|(
name|SolrInfoMBean
operator|)
name|inst
decl_stmt|;
name|String
name|name
init|=
operator|(
name|inst
operator|instanceof
name|SolrRequestHandler
operator|)
condition|?
name|pluginKey
else|:
name|mBean
operator|.
name|getName
argument_list|()
decl_stmt|;
name|core
operator|.
name|registerInfoBean
argument_list|(
name|name
argument_list|,
name|mBean
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Close this registry. This will in turn call a close on all the contained plugins    */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PluginHolder
argument_list|<
name|T
argument_list|>
argument_list|>
name|e
range|:
name|registry
operator|.
name|entrySet
argument_list|()
control|)
block|{
try|try
block|{
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exp
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error closing plugin "
operator|+
name|e
operator|.
name|getKey
argument_list|()
operator|+
literal|" of type : "
operator|+
name|meta
operator|.
name|getCleanTag
argument_list|()
argument_list|,
name|exp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * An indirect reference to a plugin. It just wraps a plugin instance.    * subclasses may choose to lazily load the plugin    */
DECL|class|PluginHolder
specifier|public
specifier|static
class|class
name|PluginHolder
parameter_list|<
name|T
parameter_list|>
implements|implements
name|AutoCloseable
block|{
DECL|field|inst
specifier|private
name|T
name|inst
decl_stmt|;
DECL|field|pluginInfo
specifier|protected
specifier|final
name|PluginInfo
name|pluginInfo
decl_stmt|;
DECL|method|PluginHolder
specifier|public
name|PluginHolder
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|pluginInfo
operator|=
name|info
expr_stmt|;
block|}
DECL|method|PluginHolder
specifier|public
name|PluginHolder
parameter_list|(
name|PluginInfo
name|info
parameter_list|,
name|T
name|inst
parameter_list|)
block|{
name|this
operator|.
name|inst
operator|=
name|inst
expr_stmt|;
name|this
operator|.
name|pluginInfo
operator|=
name|info
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|T
name|get
parameter_list|()
block|{
return|return
name|inst
return|;
block|}
DECL|method|isLoaded
specifier|public
name|boolean
name|isLoaded
parameter_list|()
block|{
return|return
name|inst
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: there may be a race here.  One thread can be creating a plugin
comment|// and another thread can come along and close everything (missing the plugin
comment|// that is in the state of being created and will probably never have close() called on it).
comment|// can close() be called concurrently with other methods?
if|if
condition|(
name|isLoaded
argument_list|()
condition|)
block|{
name|T
name|myInst
init|=
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|myInst
operator|!=
literal|null
operator|&&
name|myInst
operator|instanceof
name|AutoCloseable
condition|)
operator|(
operator|(
name|AutoCloseable
operator|)
name|myInst
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getClassName
specifier|public
name|String
name|getClassName
parameter_list|()
block|{
if|if
condition|(
name|isLoaded
argument_list|()
condition|)
return|return
name|inst
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
return|;
if|if
condition|(
name|pluginInfo
operator|!=
literal|null
condition|)
return|return
name|pluginInfo
operator|.
name|className
return|;
return|return
literal|null
return|;
block|}
block|}
comment|/**    * A class that loads plugins Lazily. When the get() method is invoked    * the Plugin is initialized and returned.    */
DECL|class|LazyPluginHolder
specifier|public
specifier|static
class|class
name|LazyPluginHolder
parameter_list|<
name|T
parameter_list|>
extends|extends
name|PluginHolder
argument_list|<
name|T
argument_list|>
block|{
DECL|field|lazyInst
specifier|private
specifier|volatile
name|T
name|lazyInst
decl_stmt|;
DECL|field|pluginMeta
specifier|private
specifier|final
name|SolrConfig
operator|.
name|SolrPluginInfo
name|pluginMeta
decl_stmt|;
DECL|field|solrException
specifier|protected
name|SolrException
name|solrException
decl_stmt|;
DECL|field|core
specifier|private
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|resourceLoader
specifier|protected
name|ResourceLoader
name|resourceLoader
decl_stmt|;
DECL|method|LazyPluginHolder
name|LazyPluginHolder
parameter_list|(
name|SolrConfig
operator|.
name|SolrPluginInfo
name|pluginMeta
parameter_list|,
name|PluginInfo
name|pluginInfo
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|super
argument_list|(
name|pluginInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|pluginMeta
operator|=
name|pluginMeta
expr_stmt|;
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|resourceLoader
operator|=
name|loader
expr_stmt|;
if|if
condition|(
name|loader
operator|instanceof
name|MemClassLoader
condition|)
block|{
if|if
condition|(
operator|!
literal|"true"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"enable.runtime.lib"
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|s
init|=
literal|"runtime library loading is not enabled, start Solr with -Denable.runtime.lib=true"
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|solrException
operator|=
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|isLoaded
specifier|public
name|boolean
name|isLoaded
parameter_list|()
block|{
return|return
name|lazyInst
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|T
name|get
parameter_list|()
block|{
if|if
condition|(
name|lazyInst
operator|!=
literal|null
condition|)
return|return
name|lazyInst
return|;
if|if
condition|(
name|solrException
operator|!=
literal|null
condition|)
throw|throw
name|solrException
throw|;
if|if
condition|(
name|createInst
argument_list|()
condition|)
block|{
comment|// check if we created the instance to avoid registering it again
name|registerMBean
argument_list|(
name|lazyInst
argument_list|,
name|core
argument_list|,
name|pluginInfo
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|lazyInst
return|;
block|}
DECL|method|createInst
specifier|private
specifier|synchronized
name|boolean
name|createInst
parameter_list|()
block|{
if|if
condition|(
name|lazyInst
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
name|log
operator|.
name|info
argument_list|(
literal|"Going to create a new {} with {} "
argument_list|,
name|pluginMeta
operator|.
name|getCleanTag
argument_list|()
argument_list|,
name|pluginInfo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|resourceLoader
operator|instanceof
name|MemClassLoader
condition|)
block|{
name|MemClassLoader
name|loader
init|=
operator|(
name|MemClassLoader
operator|)
name|resourceLoader
decl_stmt|;
name|loader
operator|.
name|loadJars
argument_list|()
expr_stmt|;
block|}
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
init|=
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|pluginMeta
operator|.
name|clazz
decl_stmt|;
name|T
name|localInst
init|=
name|core
operator|.
name|createInstance
argument_list|(
name|pluginInfo
operator|.
name|className
argument_list|,
name|clazz
argument_list|,
name|pluginMeta
operator|.
name|getCleanTag
argument_list|()
argument_list|,
literal|null
argument_list|,
name|resourceLoader
argument_list|)
decl_stmt|;
name|initInstance
argument_list|(
name|localInst
argument_list|,
name|pluginInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|localInst
operator|instanceof
name|SolrCoreAware
condition|)
block|{
name|SolrResourceLoader
operator|.
name|assertAwareCompatibility
argument_list|(
name|SolrCoreAware
operator|.
name|class
argument_list|,
name|localInst
argument_list|)
expr_stmt|;
operator|(
operator|(
name|SolrCoreAware
operator|)
name|localInst
operator|)
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|localInst
operator|instanceof
name|ResourceLoaderAware
condition|)
block|{
name|SolrResourceLoader
operator|.
name|assertAwareCompatibility
argument_list|(
name|ResourceLoaderAware
operator|.
name|class
argument_list|,
name|localInst
argument_list|)
expr_stmt|;
try|try
block|{
operator|(
operator|(
name|ResourceLoaderAware
operator|)
name|localInst
operator|)
operator|.
name|inform
argument_list|(
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
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
literal|"error initializing component"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|lazyInst
operator|=
name|localInst
expr_stmt|;
comment|// only assign the volatile until after the plugin is completely ready to use
return|return
literal|true
return|;
block|}
block|}
comment|/**    * This represents a Runtime Jar. A jar requires two details , name and version    */
DECL|class|RuntimeLib
specifier|public
specifier|static
class|class
name|RuntimeLib
implements|implements
name|PluginInfoInitialized
implements|,
name|AutoCloseable
block|{
DECL|field|name
DECL|field|version
DECL|field|sig
specifier|private
name|String
name|name
decl_stmt|,
name|version
decl_stmt|,
name|sig
decl_stmt|;
DECL|field|jarContent
specifier|private
name|JarRepository
operator|.
name|JarContentRef
name|jarContent
decl_stmt|;
DECL|field|coreContainer
specifier|private
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|field|verified
specifier|private
name|boolean
name|verified
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|name
operator|=
name|info
operator|.
name|attributes
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|Object
name|v
init|=
name|info
operator|.
name|attributes
operator|.
name|get
argument_list|(
literal|"version"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|v
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
name|SERVER_ERROR
argument_list|,
literal|"runtimeLib must have name and version"
argument_list|)
throw|;
block|}
name|version
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|sig
operator|=
name|info
operator|.
name|attributes
operator|.
name|get
argument_list|(
literal|"sig"
argument_list|)
expr_stmt|;
block|}
DECL|method|RuntimeLib
specifier|public
name|RuntimeLib
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|coreContainer
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
expr_stmt|;
block|}
DECL|method|loadJar
name|void
name|loadJar
parameter_list|()
block|{
if|if
condition|(
name|jarContent
operator|!=
literal|null
condition|)
return|return;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|jarContent
operator|!=
literal|null
condition|)
return|return;
name|jarContent
operator|=
name|coreContainer
operator|.
name|getJarRepository
argument_list|()
operator|.
name|getJarIncRef
argument_list|(
name|name
operator|+
literal|"/"
operator|+
name|version
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|getSig
specifier|public
name|String
name|getSig
parameter_list|()
block|{
return|return
name|sig
return|;
block|}
DECL|method|getFileContent
specifier|public
name|ByteBuffer
name|getFileContent
parameter_list|(
name|String
name|entryName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|jarContent
operator|==
literal|null
condition|)
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
literal|"jar not available: "
operator|+
name|name
operator|+
literal|"/"
operator|+
name|version
argument_list|)
throw|;
return|return
name|jarContent
operator|.
name|jar
operator|.
name|getFileContent
argument_list|(
name|entryName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|jarContent
operator|!=
literal|null
condition|)
name|coreContainer
operator|.
name|getJarRepository
argument_list|()
operator|.
name|decrementJarRefCount
argument_list|(
name|jarContent
argument_list|)
expr_stmt|;
block|}
DECL|method|getLibObjects
specifier|public
specifier|static
name|List
argument_list|<
name|RuntimeLib
argument_list|>
name|getLibObjects
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|List
argument_list|<
name|PluginInfo
argument_list|>
name|libs
parameter_list|)
block|{
name|List
argument_list|<
name|RuntimeLib
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|libs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PluginInfo
name|lib
range|:
name|libs
control|)
block|{
name|RuntimeLib
name|rtl
init|=
operator|new
name|RuntimeLib
argument_list|(
name|core
argument_list|)
decl_stmt|;
name|rtl
operator|.
name|init
argument_list|(
name|lib
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|rtl
argument_list|)
expr_stmt|;
block|}
return|return
name|l
return|;
block|}
DECL|method|verify
specifier|public
name|void
name|verify
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|verified
condition|)
return|return;
if|if
condition|(
name|jarContent
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Calling verify before loading the jar"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|coreContainer
operator|.
name|isZooKeeperAware
argument_list|()
condition|)
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
literal|"Signing jar is possible only in cloud"
argument_list|)
throw|;
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|keys
init|=
name|CloudUtil
operator|.
name|getTrustedKeys
argument_list|(
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|,
literal|"exe"
argument_list|)
decl_stmt|;
if|if
condition|(
name|keys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|sig
operator|==
literal|null
condition|)
block|{
name|verified
operator|=
literal|true
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"A run time lib {} is loaded  without verification "
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
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
literal|"No public keys are available in ZK to verify signature for runtime lib  "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|sig
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
name|SERVER_ERROR
argument_list|,
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"runtimelib {0} should be signed with one of the keys in ZK /keys/exe "
argument_list|,
name|name
argument_list|)
argument_list|)
throw|;
block|}
try|try
block|{
name|String
name|matchedKey
init|=
name|jarContent
operator|.
name|jar
operator|.
name|checkSignature
argument_list|(
name|sig
argument_list|,
operator|new
name|CryptoKeys
argument_list|(
name|keys
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchedKey
operator|==
literal|null
condition|)
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
literal|"No key matched signature for jar : "
operator|+
name|name
operator|+
literal|" version: "
operator|+
name|version
argument_list|)
throw|;
name|log
operator|.
name|info
argument_list|(
literal|"Jar {} signed with {} successfully verified"
argument_list|,
name|name
argument_list|,
name|matchedKey
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|SolrException
condition|)
throw|throw
name|e
throw|;
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
literal|"Error verifying key "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
end_unit
