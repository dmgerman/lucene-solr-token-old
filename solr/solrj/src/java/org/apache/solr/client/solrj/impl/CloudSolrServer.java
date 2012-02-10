begin_unit
begin_package
DECL|package|org.apache.solr.client.solrj.impl
package|package
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
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Random
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
name|TimeoutException
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|SolrServer
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
name|util
operator|.
name|ClientUtils
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
name|cloud
operator|.
name|CloudState
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
name|Slice
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
name|ZkCoreNodeProps
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
name|cloud
operator|.
name|ZkStateReader
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
name|ZooKeeperException
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
name|zookeeper
operator|.
name|KeeperException
import|;
end_import
begin_class
DECL|class|CloudSolrServer
specifier|public
class|class
name|CloudSolrServer
extends|extends
name|SolrServer
block|{
DECL|field|zkStateReader
specifier|private
specifier|volatile
name|ZkStateReader
name|zkStateReader
decl_stmt|;
DECL|field|zkHost
specifier|private
name|String
name|zkHost
decl_stmt|;
comment|// the zk server address
DECL|field|zkConnectTimeout
specifier|private
name|int
name|zkConnectTimeout
init|=
literal|10000
decl_stmt|;
DECL|field|zkClientTimeout
specifier|private
name|int
name|zkClientTimeout
init|=
literal|10000
decl_stmt|;
DECL|field|defaultCollection
specifier|private
name|String
name|defaultCollection
decl_stmt|;
DECL|field|lbServer
specifier|private
name|LBHttpSolrServer
name|lbServer
decl_stmt|;
DECL|field|rand
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|connManager
specifier|private
name|MultiThreadedHttpConnectionManager
name|connManager
decl_stmt|;
comment|/**    * @param zkHost The address of the zookeeper quorum containing the cloud state    */
DECL|method|CloudSolrServer
specifier|public
name|CloudSolrServer
parameter_list|(
name|String
name|zkHost
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|connManager
operator|=
operator|new
name|MultiThreadedHttpConnectionManager
argument_list|()
expr_stmt|;
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|lbServer
operator|=
operator|new
name|LBHttpSolrServer
argument_list|(
operator|new
name|HttpClient
argument_list|(
name|connManager
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param zkHost The address of the zookeeper quorum containing the cloud state    */
DECL|method|CloudSolrServer
specifier|public
name|CloudSolrServer
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|LBHttpSolrServer
name|lbServer
parameter_list|)
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|lbServer
operator|=
name|lbServer
expr_stmt|;
block|}
DECL|method|getZkStateReader
specifier|public
name|ZkStateReader
name|getZkStateReader
parameter_list|()
block|{
return|return
name|zkStateReader
return|;
block|}
comment|/** Sets the default collection for request */
DECL|method|setDefaultCollection
specifier|public
name|void
name|setDefaultCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|this
operator|.
name|defaultCollection
operator|=
name|collection
expr_stmt|;
block|}
comment|/** Set the connect timeout to the zookeeper ensemble in ms */
DECL|method|setZkConnectTimeout
specifier|public
name|void
name|setZkConnectTimeout
parameter_list|(
name|int
name|zkConnectTimeout
parameter_list|)
block|{
name|this
operator|.
name|zkConnectTimeout
operator|=
name|zkConnectTimeout
expr_stmt|;
block|}
comment|/** Set the timeout to the zookeeper ensemble in ms */
DECL|method|setZkClientTimeout
specifier|public
name|void
name|setZkClientTimeout
parameter_list|(
name|int
name|zkClientTimeout
parameter_list|)
block|{
name|this
operator|.
name|zkClientTimeout
operator|=
name|zkClientTimeout
expr_stmt|;
block|}
comment|/**    * Connect to the zookeeper ensemble.    * This is an optional method that may be used to force a connect before any other requests are sent.    *    * @throws IOException    * @throws TimeoutException    * @throws InterruptedException    */
DECL|method|connect
specifier|public
name|void
name|connect
parameter_list|()
block|{
if|if
condition|(
name|zkStateReader
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|zkStateReader
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|ZkStateReader
name|zk
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkHost
argument_list|,
name|zkConnectTimeout
argument_list|,
name|zkClientTimeout
argument_list|)
decl_stmt|;
name|zk
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
name|zkStateReader
operator|=
name|zk
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|request
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|request
parameter_list|(
name|SolrRequest
name|request
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|connect
argument_list|()
expr_stmt|;
comment|// TODO: if you can hash here, you could favor the shard leader
name|CloudState
name|cloudState
init|=
name|zkStateReader
operator|.
name|getCloudState
argument_list|()
decl_stmt|;
name|SolrParams
name|reqParams
init|=
name|request
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|reqParams
operator|==
literal|null
condition|)
block|{
name|reqParams
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
block|}
name|String
name|collection
init|=
name|reqParams
operator|.
name|get
argument_list|(
literal|"collection"
argument_list|,
name|defaultCollection
argument_list|)
decl_stmt|;
comment|// Extract each comma separated collection name and store in a List.
name|List
argument_list|<
name|String
argument_list|>
name|collectionList
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|collection
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Retrieve slices from the cloud state and, for each collection specified,
comment|// add it to the Map of slices.
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
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
name|collectionList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|coll
init|=
name|collectionList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|ClientUtils
operator|.
name|appendMap
argument_list|(
name|coll
argument_list|,
name|slices
argument_list|,
name|cloudState
operator|.
name|getSlices
argument_list|(
name|coll
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|cloudState
operator|.
name|getLiveNodes
argument_list|()
decl_stmt|;
comment|// IDEA: have versions on various things... like a global cloudState version
comment|// or shardAddressVersion (which only changes when the shards change)
comment|// to allow caching.
comment|// build a map of unique nodes
comment|// TODO: allow filtering by group, role, etc
name|Map
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
name|nodes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|urlList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|ZkNodeProps
name|nodeProps
range|:
name|slice
operator|.
name|getShards
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|ZkCoreNodeProps
name|coreNodeProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|nodeProps
argument_list|)
decl_stmt|;
name|String
name|node
init|=
name|coreNodeProps
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|liveNodes
operator|.
name|contains
argument_list|(
name|coreNodeProps
operator|.
name|getNodeName
argument_list|()
argument_list|)
operator|||
operator|!
name|coreNodeProps
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|nodes
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|nodeProps
argument_list|)
operator|==
literal|null
condition|)
block|{
name|String
name|url
init|=
name|coreNodeProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|urlList
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|urlList
argument_list|,
name|rand
argument_list|)
expr_stmt|;
comment|//System.out.println("########################## MAKING REQUEST TO " + urlList);
name|LBHttpSolrServer
operator|.
name|Req
name|req
init|=
operator|new
name|LBHttpSolrServer
operator|.
name|Req
argument_list|(
name|request
argument_list|,
name|urlList
argument_list|)
decl_stmt|;
name|LBHttpSolrServer
operator|.
name|Rsp
name|rsp
init|=
name|lbServer
operator|.
name|request
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|rsp
operator|.
name|getResponse
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|zkStateReader
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|zkStateReader
operator|!=
literal|null
condition|)
name|zkStateReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|zkStateReader
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|connManager
operator|!=
literal|null
condition|)
block|{
name|connManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getLbServer
specifier|public
name|LBHttpSolrServer
name|getLbServer
parameter_list|()
block|{
return|return
name|lbServer
return|;
block|}
block|}
end_class
end_unit
