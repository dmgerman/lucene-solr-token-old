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
name|Collections
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
name|ArrayBlockingQueue
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
name|BlockingQueue
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
name|CompletionService
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
name|ExecutorCompletionService
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
name|http
operator|.
name|client
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|HttpClientUtil
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|QueryRequest
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
name|util
operator|.
name|ExecutorUtil
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
name|util
operator|.
name|DefaultSolrThreadFactory
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
specifier|private
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
DECL|field|defaultClient
specifier|private
name|HttpClient
name|defaultClient
decl_stmt|;
DECL|field|loadbalancer
specifier|private
name|LBHttpSolrServer
name|loadbalancer
decl_stmt|;
comment|//default values:
DECL|field|soTimeout
name|int
name|soTimeout
init|=
literal|0
decl_stmt|;
DECL|field|connectionTimeout
name|int
name|connectionTimeout
init|=
literal|0
decl_stmt|;
DECL|field|maxConnectionsPerHost
name|int
name|maxConnectionsPerHost
init|=
literal|20
decl_stmt|;
DECL|field|corePoolSize
name|int
name|corePoolSize
init|=
literal|0
decl_stmt|;
DECL|field|maximumPoolSize
name|int
name|maximumPoolSize
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|keepAliveTime
name|int
name|keepAliveTime
init|=
literal|5
decl_stmt|;
DECL|field|queueSize
name|int
name|queueSize
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|accessPolicy
name|boolean
name|accessPolicy
init|=
literal|false
decl_stmt|;
DECL|field|scheme
specifier|private
name|String
name|scheme
init|=
literal|"http://"
decl_stmt|;
comment|//current default values
DECL|field|r
specifier|private
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
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
comment|// The core size of the threadpool servicing requests
DECL|field|INIT_CORE_POOL_SIZE
specifier|static
specifier|final
name|String
name|INIT_CORE_POOL_SIZE
init|=
literal|"corePoolSize"
decl_stmt|;
comment|// The maximum size of the threadpool servicing requests
DECL|field|INIT_MAX_POOL_SIZE
specifier|static
specifier|final
name|String
name|INIT_MAX_POOL_SIZE
init|=
literal|"maximumPoolSize"
decl_stmt|;
comment|// The amount of time idle threads persist for in the queue, before being killed
DECL|field|MAX_THREAD_IDLE_TIME
specifier|static
specifier|final
name|String
name|MAX_THREAD_IDLE_TIME
init|=
literal|"maxThreadIdleTime"
decl_stmt|;
comment|// If the threadpool uses a backing queue, what is its maximum size (-1) to use direct handoff
DECL|field|INIT_SIZE_OF_QUEUE
specifier|static
specifier|final
name|String
name|INIT_SIZE_OF_QUEUE
init|=
literal|"sizeOfQueue"
decl_stmt|;
comment|// Configure if the threadpool favours fairness over throughput
DECL|field|INIT_FAIRNESS_POLICY
specifier|static
specifier|final
name|String
name|INIT_FAIRNESS_POLICY
init|=
literal|"fairnessPolicy"
decl_stmt|;
comment|/**    * Get {@link ShardHandler} that uses the default http client.    */
annotation|@
name|Override
DECL|method|getShardHandler
specifier|public
name|ShardHandler
name|getShardHandler
parameter_list|()
block|{
return|return
name|getShardHandler
argument_list|(
name|defaultClient
argument_list|)
return|;
block|}
comment|/**    * Get {@link ShardHandler} that uses custom http client.    */
DECL|method|getShardHandler
specifier|public
name|ShardHandler
name|getShardHandler
parameter_list|(
specifier|final
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
name|NamedList
name|args
init|=
name|info
operator|.
name|initArgs
decl_stmt|;
name|this
operator|.
name|soTimeout
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|HttpClientUtil
operator|.
name|PROP_SO_TIMEOUT
argument_list|,
name|soTimeout
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheme
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_URL_SCHEME
argument_list|,
literal|"http://"
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheme
operator|=
operator|(
name|this
operator|.
name|scheme
operator|.
name|endsWith
argument_list|(
literal|"://"
argument_list|)
operator|)
condition|?
name|this
operator|.
name|scheme
else|:
name|this
operator|.
name|scheme
operator|+
literal|"://"
expr_stmt|;
name|this
operator|.
name|connectionTimeout
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|HttpClientUtil
operator|.
name|PROP_CONNECTION_TIMEOUT
argument_list|,
name|connectionTimeout
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxConnectionsPerHost
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS_PER_HOST
argument_list|,
name|maxConnectionsPerHost
argument_list|)
expr_stmt|;
name|this
operator|.
name|corePoolSize
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_CORE_POOL_SIZE
argument_list|,
name|corePoolSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|maximumPoolSize
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_MAX_POOL_SIZE
argument_list|,
name|maximumPoolSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|keepAliveTime
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|MAX_THREAD_IDLE_TIME
argument_list|,
name|keepAliveTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueSize
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_SIZE_OF_QUEUE
argument_list|,
name|queueSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|accessPolicy
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_FAIRNESS_POLICY
argument_list|,
name|accessPolicy
argument_list|)
expr_stmt|;
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|blockingQueue
init|=
operator|(
name|this
operator|.
name|queueSize
operator|==
operator|-
literal|1
operator|)
condition|?
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|this
operator|.
name|accessPolicy
argument_list|)
else|:
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|this
operator|.
name|queueSize
argument_list|,
name|this
operator|.
name|accessPolicy
argument_list|)
decl_stmt|;
name|this
operator|.
name|commExecutor
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|this
operator|.
name|corePoolSize
argument_list|,
name|this
operator|.
name|maximumPoolSize
argument_list|,
name|this
operator|.
name|keepAliveTime
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
name|blockingQueue
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"httpShardExecutor"
argument_list|)
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|clientParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|clientParams
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS_PER_HOST
argument_list|,
name|maxConnectionsPerHost
argument_list|)
expr_stmt|;
name|clientParams
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|clientParams
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_SO_TIMEOUT
argument_list|,
name|soTimeout
argument_list|)
expr_stmt|;
name|clientParams
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_CONNECTION_TIMEOUT
argument_list|,
name|connectionTimeout
argument_list|)
expr_stmt|;
name|clientParams
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_USE_RETRY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultClient
operator|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
name|clientParams
argument_list|)
expr_stmt|;
try|try
block|{
name|loadbalancer
operator|=
operator|new
name|LBHttpSolrServer
argument_list|(
name|defaultClient
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
DECL|method|getParameter
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|getParameter
parameter_list|(
name|NamedList
name|initArgs
parameter_list|,
name|String
name|configKey
parameter_list|,
name|T
name|defaultValue
parameter_list|)
block|{
name|T
name|toReturn
init|=
name|defaultValue
decl_stmt|;
if|if
condition|(
name|initArgs
operator|!=
literal|null
condition|)
block|{
name|T
name|temp
init|=
operator|(
name|T
operator|)
name|initArgs
operator|.
name|get
argument_list|(
name|configKey
argument_list|)
decl_stmt|;
name|toReturn
operator|=
operator|(
name|temp
operator|!=
literal|null
operator|)
condition|?
name|temp
else|:
name|defaultValue
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Setting {} to: {}"
argument_list|,
name|configKey
argument_list|,
name|toReturn
argument_list|)
expr_stmt|;
return|return
name|toReturn
return|;
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
name|ExecutorUtil
operator|.
name|shutdownNowAndAwaitTermination
argument_list|(
name|commExecutor
argument_list|)
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
if|if
condition|(
name|defaultClient
operator|!=
literal|null
condition|)
block|{
name|defaultClient
operator|.
name|getConnectionManager
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|loadbalancer
operator|!=
literal|null
condition|)
block|{
name|loadbalancer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
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
comment|/**    * Makes a request to one or more of the given urls, using the configured load balancer.    *    * @param req The solr search request that should be sent through the load balancer    * @param urls The list of solr server urls to load balance across    * @return The response from the request    */
DECL|method|makeLoadBalancedRequest
specifier|public
name|LBHttpSolrServer
operator|.
name|Rsp
name|makeLoadBalancedRequest
parameter_list|(
specifier|final
name|QueryRequest
name|req
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|urls
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|loadbalancer
operator|.
name|request
argument_list|(
operator|new
name|LBHttpSolrServer
operator|.
name|Req
argument_list|(
name|req
argument_list|,
name|urls
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates a randomized list of urls for the given shard.    *    * @param shard the urls for the shard (minus "http://"), separated by '|'    * @return A list of valid urls (including protocol) that are replicas for the shard    */
DECL|method|makeURLList
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|makeURLList
parameter_list|(
name|String
name|shard
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|shard
argument_list|,
literal|"|"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// convert shard to URL
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|urls
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|urls
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|scheme
operator|+
name|urls
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// Shuffle the list instead of use round-robin by default.
comment|// This prevents accidental synchronization where multiple shards could get in sync
comment|// and query the same replica at the same time.
comment|//
if|if
condition|(
name|urls
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
name|Collections
operator|.
name|shuffle
argument_list|(
name|urls
argument_list|,
name|r
argument_list|)
expr_stmt|;
return|return
name|urls
return|;
block|}
comment|/**    * Creates a new completion service for use by a single set of distributed requests.    */
DECL|method|newCompletionService
specifier|public
name|CompletionService
name|newCompletionService
parameter_list|()
block|{
return|return
operator|new
name|ExecutorCompletionService
argument_list|<
name|ShardResponse
argument_list|>
argument_list|(
name|commExecutor
argument_list|)
return|;
block|}
block|}
end_class
end_unit
