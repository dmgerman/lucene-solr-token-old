begin_unit
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|http
operator|.
name|HttpResponse
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
name|SolrClient
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
name|BinaryRequestWriter
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
name|BinaryResponseParser
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
name|ConcurrentUpdateSolrClient
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
name|update
operator|.
name|SolrCmdDistributor
operator|.
name|Error
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
name|DistributedUpdateProcessor
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
name|DistributingUpdateProcessorFactory
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
name|HashSet
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
name|ExecutorService
import|;
end_import
begin_class
DECL|class|StreamingSolrClients
specifier|public
class|class
name|StreamingSolrClients
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
name|StreamingSolrClients
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|httpClient
specifier|private
name|HttpClient
name|httpClient
decl_stmt|;
DECL|field|solrClients
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ConcurrentUpdateSolrClient
argument_list|>
name|solrClients
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|errors
specifier|private
name|List
argument_list|<
name|Error
argument_list|>
name|errors
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Error
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|updateExecutor
specifier|private
name|ExecutorService
name|updateExecutor
decl_stmt|;
DECL|method|StreamingSolrClients
specifier|public
name|StreamingSolrClients
parameter_list|(
name|UpdateShardHandler
name|updateShardHandler
parameter_list|)
block|{
name|this
operator|.
name|updateExecutor
operator|=
name|updateShardHandler
operator|.
name|getUpdateExecutor
argument_list|()
expr_stmt|;
name|httpClient
operator|=
name|updateShardHandler
operator|.
name|getHttpClient
argument_list|()
expr_stmt|;
block|}
DECL|method|getErrors
specifier|public
name|List
argument_list|<
name|Error
argument_list|>
name|getErrors
parameter_list|()
block|{
return|return
name|errors
return|;
block|}
DECL|method|clearErrors
specifier|public
name|void
name|clearErrors
parameter_list|()
block|{
name|errors
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|getSolrClient
specifier|public
specifier|synchronized
name|SolrClient
name|getSolrClient
parameter_list|(
specifier|final
name|SolrCmdDistributor
operator|.
name|Req
name|req
parameter_list|)
block|{
name|String
name|url
init|=
name|getFullUrl
argument_list|(
name|req
operator|.
name|node
operator|.
name|getUrl
argument_list|()
argument_list|)
decl_stmt|;
name|ConcurrentUpdateSolrClient
name|client
init|=
name|solrClients
operator|.
name|get
argument_list|(
name|url
argument_list|)
decl_stmt|;
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
comment|// NOTE: increasing to more than 1 threadCount for the client could cause updates to be reordered
comment|// on a greater scale since the current behavior is to only increase the number of connections/Runners when
comment|// the queue is more than half full.
name|client
operator|=
operator|new
name|ConcurrentUpdateSolrClient
argument_list|(
name|url
argument_list|,
name|httpClient
argument_list|,
literal|100
argument_list|,
literal|1
argument_list|,
name|updateExecutor
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleError
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|req
operator|.
name|trackRequestResult
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"error"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|Error
name|error
init|=
operator|new
name|Error
argument_list|()
decl_stmt|;
name|error
operator|.
name|e
operator|=
operator|(
name|Exception
operator|)
name|ex
expr_stmt|;
if|if
condition|(
name|ex
operator|instanceof
name|SolrException
condition|)
block|{
name|error
operator|.
name|statusCode
operator|=
operator|(
operator|(
name|SolrException
operator|)
name|ex
operator|)
operator|.
name|code
argument_list|()
expr_stmt|;
block|}
name|error
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|errors
operator|.
name|add
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|HttpResponse
name|resp
parameter_list|)
block|{
name|req
operator|.
name|trackRequestResult
argument_list|(
name|resp
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
name|client
operator|.
name|setParser
argument_list|(
operator|new
name|BinaryResponseParser
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|setRequestWriter
argument_list|(
operator|new
name|BinaryRequestWriter
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|setPollQueueTime
argument_list|(
name|req
operator|.
name|pollQueueTime
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|queryParams
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|queryParams
operator|.
name|add
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|DISTRIB_FROM
argument_list|)
expr_stmt|;
name|queryParams
operator|.
name|add
argument_list|(
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
argument_list|)
expr_stmt|;
name|client
operator|.
name|setQueryParams
argument_list|(
name|queryParams
argument_list|)
expr_stmt|;
name|solrClients
operator|.
name|put
argument_list|(
name|url
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
DECL|method|blockUntilFinished
specifier|public
specifier|synchronized
name|void
name|blockUntilFinished
parameter_list|()
block|{
for|for
control|(
name|ConcurrentUpdateSolrClient
name|client
range|:
name|solrClients
operator|.
name|values
argument_list|()
control|)
block|{
name|client
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|shutdown
specifier|public
specifier|synchronized
name|void
name|shutdown
parameter_list|()
block|{
for|for
control|(
name|ConcurrentUpdateSolrClient
name|client
range|:
name|solrClients
operator|.
name|values
argument_list|()
control|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getFullUrl
specifier|private
name|String
name|getFullUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|String
name|fullUrl
decl_stmt|;
if|if
condition|(
operator|!
name|url
operator|.
name|startsWith
argument_list|(
literal|"http://"
argument_list|)
operator|&&
operator|!
name|url
operator|.
name|startsWith
argument_list|(
literal|"https://"
argument_list|)
condition|)
block|{
name|fullUrl
operator|=
literal|"http://"
operator|+
name|url
expr_stmt|;
block|}
else|else
block|{
name|fullUrl
operator|=
name|url
expr_stmt|;
block|}
return|return
name|fullUrl
return|;
block|}
DECL|method|getHttpClient
specifier|public
name|HttpClient
name|getHttpClient
parameter_list|()
block|{
return|return
name|httpClient
return|;
block|}
DECL|method|getUpdateExecutor
specifier|public
name|ExecutorService
name|getUpdateExecutor
parameter_list|()
block|{
return|return
name|updateExecutor
return|;
block|}
block|}
end_class
end_unit
