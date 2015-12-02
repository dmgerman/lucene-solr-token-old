begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util.plugin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|plugin
package|;
end_package
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|List
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
name|util
operator|.
name|DOMUtil
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
comment|/**  * An abstract super class that manages standard solr-style plugin configuration.  *   *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|AbstractPluginLoader
specifier|public
specifier|abstract
class|class
name|AbstractPluginLoader
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|preRegister
specifier|private
specifier|final
name|boolean
name|preRegister
decl_stmt|;
DECL|field|requireName
specifier|private
specifier|final
name|boolean
name|requireName
decl_stmt|;
DECL|field|pluginClassType
specifier|private
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
name|pluginClassType
decl_stmt|;
comment|/**    * @param type is the 'type' name included in error messages.    * @param preRegister if true, this will first register all Plugins, then it will initialize them.    */
DECL|method|AbstractPluginLoader
specifier|public
name|AbstractPluginLoader
parameter_list|(
name|String
name|type
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|pluginClassType
parameter_list|,
name|boolean
name|preRegister
parameter_list|,
name|boolean
name|requireName
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|pluginClassType
operator|=
name|pluginClassType
expr_stmt|;
name|this
operator|.
name|preRegister
operator|=
name|preRegister
expr_stmt|;
name|this
operator|.
name|requireName
operator|=
name|requireName
expr_stmt|;
block|}
DECL|method|AbstractPluginLoader
specifier|public
name|AbstractPluginLoader
parameter_list|(
name|String
name|type
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|pluginClassType
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|pluginClassType
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Where to look for classes    */
DECL|method|getDefaultPackages
specifier|protected
name|String
index|[]
name|getDefaultPackages
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{}
return|;
block|}
comment|/**    * Create a plugin from an XML configuration.  Plugins are defined using:    *<pre class="prettyprint">    * {@code    *<plugin name="name1" class="solr.ClassName">    *      ...    *</plugin>}    *</pre>    *     * @param name - The registered name.  In the above example: "name1"    * @param className - class name for requested plugin.  In the above example: "solr.ClassName"    * @param node - the XML node defining this plugin    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|create
specifier|protected
name|T
name|create
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|className
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|loader
operator|.
name|newInstance
argument_list|(
name|className
argument_list|,
name|pluginClassType
argument_list|,
name|getDefaultPackages
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Register a plugin with a given name.    * @return The plugin previously registered to this name, or null    */
DECL|method|register
specifier|abstract
specifier|protected
name|T
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|plugin
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Initialize the plugin.      *     * @param plugin - the plugin to initialize    * @param node - the XML node defining this plugin    */
DECL|method|init
specifier|abstract
specifier|protected
name|void
name|init
parameter_list|(
name|T
name|plugin
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Initializes and registers each plugin in the list.    * Given a NodeList from XML in the form:    *<pre class="prettyprint">    * {@code    *<plugins>    *<plugin name="name1" class="solr.ClassName">    *      ...    *</plugin>    *<plugin name="name2" class="solr.ClassName">    *      ...    *</plugin>    *</plugins>}    *</pre>    *     * This will initialize and register each plugin from the list.  A class will     * be generated for each class name and registered to the given name.    *     * If 'preRegister' is true, each plugin will be registered *before* it is initialized    * This may be useful for implementations that need to inspect other registered     * plugins at startup.    *     * One (and only one) plugin may declare itself to be the 'default' plugin using:    *<pre class="prettyprint">    * {@code    *<plugin name="name2" class="solr.ClassName" default="true">}    *</pre>    * If a default element is defined, it will be returned from this function.    *     */
DECL|method|load
specifier|public
name|T
name|load
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|NodeList
name|nodes
parameter_list|)
block|{
name|List
argument_list|<
name|PluginInitInfo
argument_list|>
name|info
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|T
name|defaultPlugin
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nodes
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
name|String
name|name
init|=
literal|null
decl_stmt|;
try|try
block|{
name|name
operator|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
name|NAME
argument_list|,
name|requireName
condition|?
name|type
else|:
literal|null
argument_list|)
expr_stmt|;
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
name|type
argument_list|)
decl_stmt|;
name|String
name|defaultStr
init|=
name|DOMUtil
operator|.
name|getAttr
argument_list|(
name|node
argument_list|,
literal|"default"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|T
name|plugin
init|=
name|create
argument_list|(
name|loader
argument_list|,
name|name
argument_list|,
name|className
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"created "
operator|+
operator|(
operator|(
name|name
operator|!=
literal|null
operator|)
condition|?
name|name
else|:
literal|""
operator|)
operator|+
literal|": "
operator|+
name|plugin
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Either initialize now or wait till everything has been registered
if|if
condition|(
name|preRegister
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
operator|new
name|PluginInitInfo
argument_list|(
name|plugin
argument_list|,
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|init
argument_list|(
name|plugin
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
name|T
name|old
init|=
name|register
argument_list|(
name|name
argument_list|,
name|plugin
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|name
operator|==
literal|null
operator|&&
operator|!
name|requireName
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Multiple "
operator|+
name|type
operator|+
literal|" registered to the same name: "
operator|+
name|name
operator|+
literal|" ignoring: "
operator|+
name|old
argument_list|)
throw|;
block|}
if|if
condition|(
name|defaultStr
operator|!=
literal|null
operator|&&
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|defaultStr
argument_list|)
condition|)
block|{
if|if
condition|(
name|defaultPlugin
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Multiple default "
operator|+
name|type
operator|+
literal|" plugins: "
operator|+
name|defaultPlugin
operator|+
literal|" AND "
operator|+
name|name
argument_list|)
throw|;
block|}
name|defaultPlugin
operator|=
name|plugin
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|SolrException
name|e
init|=
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Plugin init failure for "
operator|+
name|type
operator|+
operator|(
literal|null
operator|!=
name|name
condition|?
operator|(
literal|" \""
operator|+
name|name
operator|+
literal|"\""
operator|)
else|:
literal|""
operator|)
operator|+
literal|": "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
decl_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
comment|// If everything needs to be registered *first*, this will initialize later
for|for
control|(
name|PluginInitInfo
name|pinfo
range|:
name|info
control|)
block|{
try|try
block|{
name|init
argument_list|(
name|pinfo
operator|.
name|plugin
argument_list|,
name|pinfo
operator|.
name|node
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|SolrException
name|e
init|=
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Plugin Initializing failure for "
operator|+
name|type
argument_list|,
name|ex
argument_list|)
decl_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
return|return
name|defaultPlugin
return|;
block|}
comment|/**    * Initializes and registers a single plugin.    *     * Given a NodeList from XML in the form:    *<pre class="prettyprint">    * {@code    *<plugin name="name1" class="solr.ClassName"> ...</plugin>}    *</pre>    *     * This will initialize and register a single plugin. A class will be    * generated for the plugin and registered to the given name.    *     * If 'preRegister' is true, the plugin will be registered *before* it is    * initialized This may be useful for implementations that need to inspect    * other registered plugins at startup.    *     * The created class for the plugin will be returned from this function.    *     */
DECL|method|loadSingle
specifier|public
name|T
name|loadSingle
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|,
name|Node
name|node
parameter_list|)
block|{
name|List
argument_list|<
name|PluginInitInfo
argument_list|>
name|info
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|T
name|plugin
init|=
literal|null
decl_stmt|;
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
name|NAME
argument_list|,
name|requireName
condition|?
name|type
else|:
literal|null
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
name|type
argument_list|)
decl_stmt|;
name|plugin
operator|=
name|create
argument_list|(
name|loader
argument_list|,
name|name
argument_list|,
name|className
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"created "
operator|+
name|name
operator|+
literal|": "
operator|+
name|plugin
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Either initialize now or wait till everything has been registered
if|if
condition|(
name|preRegister
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
operator|new
name|PluginInitInfo
argument_list|(
name|plugin
argument_list|,
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|init
argument_list|(
name|plugin
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
name|T
name|old
init|=
name|register
argument_list|(
name|name
argument_list|,
name|plugin
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|name
operator|==
literal|null
operator|&&
operator|!
name|requireName
operator|)
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
literal|"Multiple "
operator|+
name|type
operator|+
literal|" registered to the same name: "
operator|+
name|name
operator|+
literal|" ignoring: "
operator|+
name|old
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|SolrException
name|e
init|=
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Plugin init failure for "
operator|+
name|type
argument_list|,
name|ex
argument_list|)
decl_stmt|;
throw|throw
name|e
throw|;
block|}
comment|// If everything needs to be registered *first*, this will initialize later
for|for
control|(
name|PluginInitInfo
name|pinfo
range|:
name|info
control|)
block|{
try|try
block|{
name|init
argument_list|(
name|pinfo
operator|.
name|plugin
argument_list|,
name|pinfo
operator|.
name|node
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|SolrException
name|e
init|=
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Plugin init failure for "
operator|+
name|type
argument_list|,
name|ex
argument_list|)
decl_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
return|return
name|plugin
return|;
block|}
comment|/**    * Internal class to hold onto initialization info so that it can be initialized     * after it is registered.    */
DECL|class|PluginInitInfo
specifier|private
class|class
name|PluginInitInfo
block|{
DECL|field|plugin
specifier|final
name|T
name|plugin
decl_stmt|;
DECL|field|node
specifier|final
name|Node
name|node
decl_stmt|;
DECL|method|PluginInitInfo
name|PluginInitInfo
parameter_list|(
name|T
name|plugin
parameter_list|,
name|Node
name|node
parameter_list|)
block|{
name|this
operator|.
name|plugin
operator|=
name|plugin
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
