begin_unit
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|Executor
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
name|SynchronousQueue
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
name|ThreadPoolExecutor
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
name|TimeUnit
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
name|httpclient
operator|.
name|DefaultHttpMethodRetryHandler
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
name|httpclient
operator|.
name|HttpClient
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
name|httpclient
operator|.
name|MultiThreadedHttpConnectionManager
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
name|httpclient
operator|.
name|params
operator|.
name|HttpMethodParams
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
name|impl
operator|.
name|LBHttpSolrServer
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
name|util
operator|.
name|DefaultSolrThreadFactory
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
DECL|class|HttpShardHandlerFactory
specifier|public
class|class
name|HttpShardHandlerFactory
extends|extends
name|ShardHandlerFactory
implements|implements
name|PluginInfoInitialized
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
name|HttpShardHandlerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// We want an executor that doesn't take up any resources if
comment|// it's not used, so it could be created statically for
comment|// the distributed search component if desired.
comment|//
comment|// Consider CallerRuns policy and a lower max threads to throttle
comment|// requests at some point (or should we simply return failure?)
DECL|field|commExecutor
name|ThreadPoolExecutor
name|commExecutor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
comment|// terminate idle threads after 5 sec
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
comment|// directly hand off tasks
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"httpShardExecutor"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|client
name|HttpClient
name|client
decl_stmt|;
DECL|field|r
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|loadbalancer
name|LBHttpSolrServer
name|loadbalancer
decl_stmt|;
DECL|field|soTimeout
name|int
name|soTimeout
init|=
literal|0
decl_stmt|;
comment|//current default values
DECL|field|connectionTimeout
name|int
name|connectionTimeout
init|=
literal|0
decl_stmt|;
comment|//current default values
DECL|field|scheme
specifier|public
name|String
name|scheme
init|=
literal|"http://"
decl_stmt|;
comment|//current default values
DECL|field|mgr
specifier|private
name|MultiThreadedHttpConnectionManager
name|mgr
decl_stmt|;
comment|// socket timeout measured in ms, closes a socket if read
comment|// takes longer than x ms to complete. throws
comment|// java.net.SocketTimeoutException: Read timed out exception
DECL|field|INIT_SO_TIMEOUT
specifier|static
specifier|final
name|String
name|INIT_SO_TIMEOUT
init|=
literal|"socketTimeout"
decl_stmt|;
comment|// connection timeout measures in ms, closes a socket if connection
comment|// cannot be established within x ms. with a
comment|// java.net.SocketTimeoutException: Connection timed out
DECL|field|INIT_CONNECTION_TIMEOUT
specifier|static
specifier|final
name|String
name|INIT_CONNECTION_TIMEOUT
init|=
literal|"connTimeout"
decl_stmt|;
comment|// URL scheme to be used in distributed search.
DECL|field|INIT_URL_SCHEME
specifier|static
specifier|final
name|String
name|INIT_URL_SCHEME
init|=
literal|"urlScheme"
decl_stmt|;
DECL|method|getShardHandler
specifier|public
name|ShardHandler
name|getShardHandler
parameter_list|()
block|{
return|return
name|getShardHandler
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|getShardHandler
specifier|public
name|ShardHandler
name|getShardHandler
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|)
block|{
return|return
operator|new
name|HttpShardHandler
argument_list|(
name|this
argument_list|,
name|httpClient
argument_list|)
return|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|.
name|initArgs
operator|!=
literal|null
condition|)
block|{
name|Object
name|so
init|=
name|info
operator|.
name|initArgs
operator|.
name|get
argument_list|(
name|INIT_SO_TIMEOUT
argument_list|)
decl_stmt|;
if|if
condition|(
name|so
operator|!=
literal|null
condition|)
block|{
name|soTimeout
operator|=
operator|(
name|Integer
operator|)
name|so
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Setting socketTimeout to: "
operator|+
name|soTimeout
argument_list|)
expr_stmt|;
block|}
name|Object
name|urlScheme
init|=
name|info
operator|.
name|initArgs
operator|.
name|get
argument_list|(
name|INIT_URL_SCHEME
argument_list|)
decl_stmt|;
if|if
condition|(
name|urlScheme
operator|!=
literal|null
condition|)
block|{
name|scheme
operator|=
name|urlScheme
operator|+
literal|"://"
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Setting urlScheme to: "
operator|+
name|urlScheme
argument_list|)
expr_stmt|;
block|}
name|Object
name|co
init|=
name|info
operator|.
name|initArgs
operator|.
name|get
argument_list|(
name|INIT_CONNECTION_TIMEOUT
argument_list|)
decl_stmt|;
if|if
condition|(
name|co
operator|!=
literal|null
condition|)
block|{
name|connectionTimeout
operator|=
operator|(
name|Integer
operator|)
name|co
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Setting shard-connection-timeout to: "
operator|+
name|connectionTimeout
argument_list|)
expr_stmt|;
block|}
block|}
name|mgr
operator|=
operator|new
name|MultiThreadedHttpConnectionManager
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|getParams
argument_list|()
operator|.
name|setDefaultMaxConnectionsPerHost
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|getParams
argument_list|()
operator|.
name|setMaxTotalConnections
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|getParams
argument_list|()
operator|.
name|setConnectionTimeout
argument_list|(
name|connectionTimeout
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|getParams
argument_list|()
operator|.
name|setSoTimeout
argument_list|(
name|soTimeout
argument_list|)
expr_stmt|;
comment|// mgr.getParams().setStaleCheckingEnabled(false);
name|client
operator|=
operator|new
name|HttpClient
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
comment|// prevent retries  (note: this didn't work when set on mgr.. needed to be set on client)
name|DefaultHttpMethodRetryHandler
name|retryhandler
init|=
operator|new
name|DefaultHttpMethodRetryHandler
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|client
operator|.
name|getParams
argument_list|()
operator|.
name|setParameter
argument_list|(
name|HttpMethodParams
operator|.
name|RETRY_HANDLER
argument_list|,
name|retryhandler
argument_list|)
expr_stmt|;
try|try
block|{
name|loadbalancer
operator|=
operator|new
name|LBHttpSolrServer
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
comment|// should be impossible since we're not passing any URLs here
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
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|mgr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|loadbalancer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|commExecutor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
