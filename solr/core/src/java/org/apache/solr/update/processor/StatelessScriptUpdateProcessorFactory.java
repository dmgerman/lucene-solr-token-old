begin_unit
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|LocalSolrQueryRequest
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
name|*
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
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FilenameUtils
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|Invocable
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|ScriptEngine
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|ScriptEngineManager
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|ScriptEngineFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|ScriptException
import|;
end_import
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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|LinkedHashSet
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
comment|/**  *<p>  * An update request processor factory that enables the use of update   * processors implemented as scripts which can be loaded by the   * {@link SolrResourceLoader} (usually via the<code>conf</code> dir for   * the SolrCore).  *</p>  *<p>  * This factory requires at least one configuration parameter named  *<code>script</code> which may be the name of a script file as a string,   * or an array of multiple script files.  If multiple script files are   * specified, they are executed sequentially in the order specified in the   * configuration -- as if multiple factories were configured sequentially  *</p>  *<p>  * Each script file is expected to declare functions with the same name   * as each method in {@link UpdateRequestProcessor}, using the same   * arguments.  One slight deviation is in the optional return value from   * these functions: If a script function has a<code>boolean</code> return   * value, and that value is<code>false</code> then the processor will   * cleanly terminate processing of the command and return, without forwarding   * the command on to the next script or processor in the chain.  * Due to limitations in the {@link ScriptEngine} API used by   * this factory, it can not enforce that all functions exist on initialization,  * so errors from missing functions will only be generated at runtime when  * the chain attempts to use them.  *</p>  *<p>  * The factory may also be configured with an optional "params" argument,   * which can be an {@link NamedList} (or array, or any other simple Java   * object) which will be put into the global scope for each script.  *</p>  *<p>  * The following variables are define as global variables for each script:  *<ul>  *<li>req - The {@link SolrQueryRequest}</li>  *<li>rsp - The {@link SolrQueryResponse}</li>  *<li>logger - A {@link Logger} that can be used for logging purposes in the script</li>  *<li>params - The "params" init argument in the factory configuration (if any)</li>  *</ul>  *<p>  * Internally this update processor uses JDK 6 scripting engine support,   * and any {@link Invocable} implementations of<code>ScriptEngine</code>   * that can be loaded using the Solr Plugin ClassLoader may be used.    * By default, the engine used for each script is determined by the filed   * extension (ie: a *.js file will be treated as a JavaScript script) but   * this can be overridden by specifying an explicit "engine" name init   * param for the factory, which identifies a registered name of a   * {@link ScriptEngineFactory}.   * (This may be particularly useful if multiple engines are available for   * the same scripting language, and you wish to force the usage of a   * particular engine because of known quirks)  *</p>  *<p>  * A new {@link ScriptEngineManager} is created for each   *<code>SolrQueryRequest</code> defining a "global" scope for the script(s)   * which is request specific.  Separate<code>ScriptEngine</code> instances   * are then used to evaluate the script files, resulting in an "engine" scope   * that is specific to each script.  *</p>  *<p>  * A simple example...  *</p>  *<pre class="prettyprint">  *&lt;processor class="solr.StatelessScriptUpdateProcessorFactory"&gt;  *&lt;str name="script"&gt;updateProcessor.js&lt;/str&gt;  *&lt;/processor&gt;  *</pre>  *<p>  * A more complex example involving multiple scripts in different languages,   * and a "params"<code>NamedList</code> that will be put into the global   * scope of each script...  *</p>  *<pre class="prettyprint">  *&lt;processor class="solr.StatelessScriptUpdateProcessorFactory"&gt;  *&lt;arr name="script"&gt;  *&lt;str name="script"&gt;first-processor.js&lt;/str&gt;  *&lt;str name="script"&gt;second-processor.py&lt;/str&gt;  *&lt;/arr&gt;  *&lt;lst name="params"&gt;  *&lt;bool name="a_bool_value"&gt;true&lt;/bool&gt;  *&lt;int name="and_int_value"&gt;3&lt;/int&gt;  *&lt;/lst&gt;  *&lt;/processor&gt;  *</pre>  *<p>  * An example where the script file extensions are ignored, and an   * explicit script engine is used....  *</p>  *<pre class="prettyprint">  *&lt;processor class="solr.StatelessScriptUpdateProcessorFactory"&gt;  *&lt;arr name="script"&gt;  *&lt;str name="script"&gt;first-processor.txt&lt;/str&gt;  *&lt;str name="script"&gt;second-processor.txt&lt;/str&gt;  *&lt;/arr&gt;  *&lt;str name="engine"&gt;rhino&lt;/str&gt;  *&lt;/processor&gt;  *</pre>  *   */
end_comment
begin_class
DECL|class|StatelessScriptUpdateProcessorFactory
specifier|public
class|class
name|StatelessScriptUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
implements|implements
name|SolrCoreAware
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
DECL|field|SCRIPT_ARG
specifier|private
specifier|final
specifier|static
name|String
name|SCRIPT_ARG
init|=
literal|"script"
decl_stmt|;
DECL|field|PARAMS_ARG
specifier|private
specifier|final
specifier|static
name|String
name|PARAMS_ARG
init|=
literal|"params"
decl_stmt|;
DECL|field|ENGINE_NAME_ARG
specifier|private
specifier|final
specifier|static
name|String
name|ENGINE_NAME_ARG
init|=
literal|"engine"
decl_stmt|;
DECL|field|scriptFiles
specifier|private
name|List
argument_list|<
name|ScriptFile
argument_list|>
name|scriptFiles
decl_stmt|;
comment|/** if non null, this is an override for the engine for all scripts */
DECL|field|engineName
specifier|private
name|String
name|engineName
init|=
literal|null
decl_stmt|;
DECL|field|params
specifier|private
name|Object
name|params
init|=
literal|null
decl_stmt|;
DECL|field|resourceLoader
specifier|private
name|SolrResourceLoader
name|resourceLoader
decl_stmt|;
DECL|field|scriptEngineCustomizer
specifier|private
name|ScriptEngineCustomizer
name|scriptEngineCustomizer
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
name|Collection
argument_list|<
name|String
argument_list|>
name|scripts
init|=
name|args
operator|.
name|removeConfigArgs
argument_list|(
name|SCRIPT_ARG
argument_list|)
decl_stmt|;
if|if
condition|(
name|scripts
operator|.
name|isEmpty
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
name|SERVER_ERROR
argument_list|,
literal|"StatelessScriptUpdateProcessorFactory must be "
operator|+
literal|"initialized with at least one "
operator|+
name|SCRIPT_ARG
argument_list|)
throw|;
block|}
name|scriptFiles
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|script
range|:
name|scripts
control|)
block|{
name|scriptFiles
operator|.
name|add
argument_list|(
operator|new
name|ScriptFile
argument_list|(
name|script
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|params
operator|=
name|args
operator|.
name|remove
argument_list|(
name|PARAMS_ARG
argument_list|)
expr_stmt|;
name|Object
name|engine
init|=
name|args
operator|.
name|remove
argument_list|(
name|ENGINE_NAME_ARG
argument_list|)
decl_stmt|;
if|if
condition|(
name|engine
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|engine
operator|instanceof
name|String
condition|)
block|{
name|engineName
operator|=
operator|(
name|String
operator|)
name|engine
expr_stmt|;
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
literal|"'"
operator|+
name|ENGINE_NAME_ARG
operator|+
literal|"' init param must be a String (found: "
operator|+
name|engine
operator|.
name|getClass
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
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
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|List
argument_list|<
name|EngineInfo
argument_list|>
name|scriptEngines
init|=
literal|null
decl_stmt|;
name|scriptEngines
operator|=
name|initEngines
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return
operator|new
name|ScriptUpdateProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|scriptEngines
argument_list|,
name|next
argument_list|)
return|;
block|}
comment|// TODO: Make this useful outside of tests, such that a ScriptEngineCustomizer could be looked up through the resource loader
DECL|method|setScriptEngineCustomizer
name|void
name|setScriptEngineCustomizer
parameter_list|(
name|ScriptEngineCustomizer
name|scriptEngineCustomizer
parameter_list|)
block|{
name|this
operator|.
name|scriptEngineCustomizer
operator|=
name|scriptEngineCustomizer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|resourceLoader
operator|=
name|core
operator|.
name|getResourceLoader
argument_list|()
expr_stmt|;
comment|// test that our engines& scripts are valid
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|initEngines
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Unable to initialize scripts: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
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
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|//================================================ Helper Methods ==================================================
comment|/**    * Initializes a list of script engines - an engine per script file.    *    * @param req The solr request.    * @param rsp The solr response    * @return The list of initialized script engines.    */
DECL|method|initEngines
specifier|private
name|List
argument_list|<
name|EngineInfo
argument_list|>
name|initEngines
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|SolrException
block|{
name|List
argument_list|<
name|EngineInfo
argument_list|>
name|scriptEngines
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ScriptEngineManager
name|scriptEngineManager
init|=
operator|new
name|ScriptEngineManager
argument_list|(
name|resourceLoader
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|scriptEngineManager
operator|.
name|put
argument_list|(
literal|"logger"
argument_list|,
name|log
argument_list|)
expr_stmt|;
name|scriptEngineManager
operator|.
name|put
argument_list|(
literal|"req"
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|scriptEngineManager
operator|.
name|put
argument_list|(
literal|"rsp"
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|scriptEngineManager
operator|.
name|put
argument_list|(
literal|"params"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ScriptFile
name|scriptFile
range|:
name|scriptFiles
control|)
block|{
name|ScriptEngine
name|engine
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|engineName
condition|)
block|{
name|engine
operator|=
name|scriptEngineManager
operator|.
name|getEngineByName
argument_list|(
name|engineName
argument_list|)
expr_stmt|;
if|if
condition|(
name|engine
operator|==
literal|null
condition|)
block|{
name|String
name|details
init|=
name|getSupportedEngines
argument_list|(
name|scriptEngineManager
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
literal|"No ScriptEngine found by name: "
operator|+
name|engineName
operator|+
operator|(
literal|null
operator|!=
name|details
condition|?
literal|" -- supported names: "
operator|+
name|details
else|:
literal|""
operator|)
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|engine
operator|=
name|scriptEngineManager
operator|.
name|getEngineByExtension
argument_list|(
name|scriptFile
operator|.
name|getExtension
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|engine
operator|==
literal|null
condition|)
block|{
name|String
name|details
init|=
name|getSupportedEngines
argument_list|(
name|scriptEngineManager
argument_list|,
literal|true
argument_list|)
decl_stmt|;
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
literal|"No ScriptEngine found by file extension: "
operator|+
name|scriptFile
operator|.
name|getFileName
argument_list|()
operator|+
operator|(
literal|null
operator|!=
name|details
condition|?
literal|" -- supported extensions: "
operator|+
name|details
else|:
literal|""
operator|)
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
operator|(
name|engine
operator|instanceof
name|Invocable
operator|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Engine "
operator|+
operator|(
operator|(
literal|null
operator|!=
name|engineName
operator|)
condition|?
name|engineName
else|:
operator|(
literal|"for script "
operator|+
name|scriptFile
operator|.
name|getFileName
argument_list|()
operator|)
operator|)
operator|+
literal|" does not support function invocation (via Invocable): "
operator|+
name|engine
operator|.
name|getClass
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" ("
operator|+
name|engine
operator|.
name|getFactory
argument_list|()
operator|.
name|getEngineName
argument_list|()
operator|+
literal|")"
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
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
name|msg
argument_list|)
throw|;
block|}
if|if
condition|(
name|scriptEngineCustomizer
operator|!=
literal|null
condition|)
block|{
name|scriptEngineCustomizer
operator|.
name|customize
argument_list|(
name|engine
argument_list|)
expr_stmt|;
block|}
name|scriptEngines
operator|.
name|add
argument_list|(
operator|new
name|EngineInfo
argument_list|(
operator|(
name|Invocable
operator|)
name|engine
argument_list|,
name|scriptFile
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Reader
name|scriptSrc
init|=
name|scriptFile
operator|.
name|openReader
argument_list|(
name|resourceLoader
argument_list|)
decl_stmt|;
try|try
block|{
name|engine
operator|.
name|eval
argument_list|(
name|scriptSrc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ScriptException
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
literal|"Unable to evaluate script: "
operator|+
name|scriptFile
operator|.
name|getFileName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|scriptSrc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
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
literal|"Unable to evaluate script: "
operator|+
name|scriptFile
operator|.
name|getFileName
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
return|return
name|scriptEngines
return|;
block|}
comment|/**    * For error messages - returns null if there are any exceptions of any     * kind building the string (or of the list is empty for some unknown reason).    * @param ext - if true, list of extensions, otherwise a list of engine names    */
DECL|method|getSupportedEngines
specifier|private
specifier|static
name|String
name|getSupportedEngines
parameter_list|(
name|ScriptEngineManager
name|mgr
parameter_list|,
name|boolean
name|ext
parameter_list|)
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|ScriptEngineFactory
argument_list|>
name|factories
init|=
name|mgr
operator|.
name|getEngineFactories
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|factories
condition|)
return|return
name|result
return|;
name|Set
argument_list|<
name|String
argument_list|>
name|engines
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|(
name|factories
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ScriptEngineFactory
name|f
range|:
name|factories
control|)
block|{
if|if
condition|(
name|ext
condition|)
block|{
name|engines
operator|.
name|addAll
argument_list|(
name|f
operator|.
name|getExtensions
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|engines
operator|.
name|addAll
argument_list|(
name|f
operator|.
name|getNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|=
name|StringUtils
operator|.
name|join
argument_list|(
name|engines
argument_list|,
literal|", "
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|/* :NOOP: */
block|}
return|return
name|result
return|;
block|}
comment|//================================================= Inner Classes ==================================================
comment|/**    * The actual update processor. All methods delegate to scripts.    */
DECL|class|ScriptUpdateProcessor
specifier|private
specifier|static
class|class
name|ScriptUpdateProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|engines
specifier|private
name|List
argument_list|<
name|EngineInfo
argument_list|>
name|engines
decl_stmt|;
DECL|method|ScriptUpdateProcessor
specifier|private
name|ScriptUpdateProcessor
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|res
parameter_list|,
name|List
argument_list|<
name|EngineInfo
argument_list|>
name|engines
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|engines
operator|=
name|engines
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|invokeFunction
argument_list|(
literal|"processAdd"
argument_list|,
name|cmd
argument_list|)
condition|)
block|{
name|super
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|processDelete
specifier|public
name|void
name|processDelete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|invokeFunction
argument_list|(
literal|"processDelete"
argument_list|,
name|cmd
argument_list|)
condition|)
block|{
name|super
operator|.
name|processDelete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|processMergeIndexes
specifier|public
name|void
name|processMergeIndexes
parameter_list|(
name|MergeIndexesCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|invokeFunction
argument_list|(
literal|"processMergeIndexes"
argument_list|,
name|cmd
argument_list|)
condition|)
block|{
name|super
operator|.
name|processMergeIndexes
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|processCommit
specifier|public
name|void
name|processCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|invokeFunction
argument_list|(
literal|"processCommit"
argument_list|,
name|cmd
argument_list|)
condition|)
block|{
name|super
operator|.
name|processCommit
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|processRollback
specifier|public
name|void
name|processRollback
parameter_list|(
name|RollbackUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|invokeFunction
argument_list|(
literal|"processRollback"
argument_list|,
name|cmd
argument_list|)
condition|)
block|{
name|super
operator|.
name|processRollback
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|invokeFunction
argument_list|(
literal|"finish"
argument_list|)
condition|)
block|{
name|super
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * returns true if processing should continue, or false if the       * request should be ended now.  Result value is computed from the return       * value of the script function if: it exists, is non-null, and can be       * cast to a java Boolean.      */
DECL|method|invokeFunction
specifier|private
name|boolean
name|invokeFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
modifier|...
name|cmd
parameter_list|)
block|{
for|for
control|(
name|EngineInfo
name|engine
range|:
name|engines
control|)
block|{
try|try
block|{
name|Object
name|result
init|=
name|engine
operator|.
name|getEngine
argument_list|()
operator|.
name|invokeFunction
argument_list|(
name|name
argument_list|,
name|cmd
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|result
operator|&&
name|result
operator|instanceof
name|Boolean
condition|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|Boolean
operator|)
name|result
operator|)
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ScriptException
decl||
name|NoSuchMethodException
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
literal|"Unable to invoke function "
operator|+
name|name
operator|+
literal|" in script: "
operator|+
name|engine
operator|.
name|getScriptFile
argument_list|()
operator|.
name|getFileName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
comment|/**    * Holds the script engine and its associated script file.    */
DECL|class|EngineInfo
specifier|private
specifier|static
class|class
name|EngineInfo
block|{
DECL|field|engine
specifier|private
specifier|final
name|Invocable
name|engine
decl_stmt|;
DECL|field|scriptFile
specifier|private
specifier|final
name|ScriptFile
name|scriptFile
decl_stmt|;
DECL|method|EngineInfo
specifier|private
name|EngineInfo
parameter_list|(
name|Invocable
name|engine
parameter_list|,
name|ScriptFile
name|scriptFile
parameter_list|)
block|{
name|this
operator|.
name|engine
operator|=
name|engine
expr_stmt|;
name|this
operator|.
name|scriptFile
operator|=
name|scriptFile
expr_stmt|;
block|}
DECL|method|getEngine
specifier|public
name|Invocable
name|getEngine
parameter_list|()
block|{
return|return
name|engine
return|;
block|}
DECL|method|getScriptFile
specifier|public
name|ScriptFile
name|getScriptFile
parameter_list|()
block|{
return|return
name|scriptFile
return|;
block|}
block|}
comment|/**    * Represents a script file.    */
DECL|class|ScriptFile
specifier|private
specifier|static
class|class
name|ScriptFile
block|{
DECL|field|fileName
specifier|private
specifier|final
name|String
name|fileName
decl_stmt|;
DECL|field|extension
specifier|private
specifier|final
name|String
name|extension
decl_stmt|;
DECL|method|ScriptFile
specifier|private
name|ScriptFile
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
name|this
operator|.
name|extension
operator|=
name|FilenameUtils
operator|.
name|getExtension
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
DECL|method|getFileName
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
DECL|method|getExtension
specifier|public
name|String
name|getExtension
parameter_list|()
block|{
return|return
name|extension
return|;
block|}
DECL|method|openReader
specifier|public
name|Reader
name|openReader
parameter_list|(
name|SolrResourceLoader
name|resourceLoader
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|input
init|=
name|resourceLoader
operator|.
name|openResource
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
return|return
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|input
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
