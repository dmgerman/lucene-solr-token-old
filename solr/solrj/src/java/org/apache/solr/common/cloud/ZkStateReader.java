begin_unit
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
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
name|Arrays
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
name|Map
operator|.
name|Entry
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
name|Executors
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
name|ScheduledExecutorService
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
name|ThreadFactory
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
name|noggit
operator|.
name|CharArr
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|JSONWriter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
name|ByteUtils
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|WatchedEvent
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
name|Watcher
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
name|Watcher
operator|.
name|Event
operator|.
name|EventType
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
name|data
operator|.
name|Stat
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
DECL|class|ZkStateReader
specifier|public
class|class
name|ZkStateReader
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ZkStateReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BASE_URL_PROP
specifier|public
specifier|static
specifier|final
name|String
name|BASE_URL_PROP
init|=
literal|"base_url"
decl_stmt|;
DECL|field|NODE_NAME_PROP
specifier|public
specifier|static
specifier|final
name|String
name|NODE_NAME_PROP
init|=
literal|"node_name"
decl_stmt|;
DECL|field|ROLES_PROP
specifier|public
specifier|static
specifier|final
name|String
name|ROLES_PROP
init|=
literal|"roles"
decl_stmt|;
DECL|field|STATE_PROP
specifier|public
specifier|static
specifier|final
name|String
name|STATE_PROP
init|=
literal|"state"
decl_stmt|;
DECL|field|CORE_NAME_PROP
specifier|public
specifier|static
specifier|final
name|String
name|CORE_NAME_PROP
init|=
literal|"core"
decl_stmt|;
DECL|field|COLLECTION_PROP
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTION_PROP
init|=
literal|"collection"
decl_stmt|;
DECL|field|SHARD_ID_PROP
specifier|public
specifier|static
specifier|final
name|String
name|SHARD_ID_PROP
init|=
literal|"shard"
decl_stmt|;
DECL|field|NUM_SHARDS_PROP
specifier|public
specifier|static
specifier|final
name|String
name|NUM_SHARDS_PROP
init|=
literal|"numShards"
decl_stmt|;
DECL|field|LEADER_PROP
specifier|public
specifier|static
specifier|final
name|String
name|LEADER_PROP
init|=
literal|"leader"
decl_stmt|;
DECL|field|COLLECTIONS_ZKNODE
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTIONS_ZKNODE
init|=
literal|"/collections"
decl_stmt|;
DECL|field|LIVE_NODES_ZKNODE
specifier|public
specifier|static
specifier|final
name|String
name|LIVE_NODES_ZKNODE
init|=
literal|"/live_nodes"
decl_stmt|;
DECL|field|CLUSTER_STATE
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_STATE
init|=
literal|"/clusterstate.json"
decl_stmt|;
DECL|field|RECOVERING
specifier|public
specifier|static
specifier|final
name|String
name|RECOVERING
init|=
literal|"recovering"
decl_stmt|;
DECL|field|RECOVERY_FAILED
specifier|public
specifier|static
specifier|final
name|String
name|RECOVERY_FAILED
init|=
literal|"recovery_failed"
decl_stmt|;
DECL|field|ACTIVE
specifier|public
specifier|static
specifier|final
name|String
name|ACTIVE
init|=
literal|"active"
decl_stmt|;
DECL|field|DOWN
specifier|public
specifier|static
specifier|final
name|String
name|DOWN
init|=
literal|"down"
decl_stmt|;
DECL|field|SYNC
specifier|public
specifier|static
specifier|final
name|String
name|SYNC
init|=
literal|"sync"
decl_stmt|;
DECL|field|clusterState
specifier|private
specifier|volatile
name|ClusterState
name|clusterState
decl_stmt|;
DECL|field|SOLRCLOUD_UPDATE_DELAY
specifier|private
specifier|static
specifier|final
name|long
name|SOLRCLOUD_UPDATE_DELAY
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solrcloud.update.delay"
argument_list|,
literal|"5000"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|LEADER_ELECT_ZKNODE
specifier|public
specifier|static
specifier|final
name|String
name|LEADER_ELECT_ZKNODE
init|=
literal|"/leader_elect"
decl_stmt|;
DECL|field|SHARD_LEADERS_ZKNODE
specifier|public
specifier|static
specifier|final
name|String
name|SHARD_LEADERS_ZKNODE
init|=
literal|"leaders"
decl_stmt|;
comment|//
comment|// convenience methods... should these go somewhere else?
comment|//
DECL|method|toJSON
specifier|public
specifier|static
name|byte
index|[]
name|toJSON
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|CharArr
name|out
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
operator|new
name|JSONWriter
argument_list|(
name|out
argument_list|,
literal|2
argument_list|)
operator|.
name|write
argument_list|(
name|o
argument_list|)
expr_stmt|;
comment|// indentation by default
return|return
name|toUTF8
argument_list|(
name|out
argument_list|)
return|;
block|}
DECL|method|toUTF8
specifier|public
specifier|static
name|byte
index|[]
name|toUTF8
parameter_list|(
name|CharArr
name|out
parameter_list|)
block|{
name|byte
index|[]
name|arr
init|=
operator|new
name|byte
index|[
name|out
operator|.
name|size
argument_list|()
operator|<<
literal|2
index|]
decl_stmt|;
comment|// is 4x the real worst-case upper-bound?
name|int
name|nBytes
init|=
name|ByteUtils
operator|.
name|UTF16toUTF8
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|size
argument_list|()
argument_list|,
name|arr
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|arr
argument_list|,
name|nBytes
argument_list|)
return|;
block|}
DECL|method|fromJSON
specifier|public
specifier|static
name|Object
name|fromJSON
parameter_list|(
name|byte
index|[]
name|utf8
parameter_list|)
block|{
comment|// convert directly from bytes to chars
comment|// and parse directly from that instead of going through
comment|// intermediate strings or readers
name|CharArr
name|chars
init|=
operator|new
name|CharArr
argument_list|()
decl_stmt|;
name|ByteUtils
operator|.
name|UTF8toUTF16
argument_list|(
name|utf8
argument_list|,
literal|0
argument_list|,
name|utf8
operator|.
name|length
argument_list|,
name|chars
argument_list|)
expr_stmt|;
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|(
name|chars
operator|.
name|getArray
argument_list|()
argument_list|,
name|chars
operator|.
name|getStart
argument_list|()
argument_list|,
name|chars
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
name|parser
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
comment|// should never happen w/o using real IO
block|}
block|}
DECL|class|ZKTF
specifier|private
specifier|static
class|class
name|ZKTF
implements|implements
name|ThreadFactory
block|{
DECL|field|tg
specifier|private
specifier|static
name|ThreadGroup
name|tg
init|=
operator|new
name|ThreadGroup
argument_list|(
literal|"ZkStateReader"
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|newThread
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|td
init|=
operator|new
name|Thread
argument_list|(
name|tg
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|td
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|td
return|;
block|}
block|}
DECL|field|updateCloudExecutor
specifier|private
name|ScheduledExecutorService
name|updateCloudExecutor
init|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ZKTF
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|clusterStateUpdateScheduled
specifier|private
name|boolean
name|clusterStateUpdateScheduled
decl_stmt|;
DECL|field|zkClient
specifier|private
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|closeClient
specifier|private
name|boolean
name|closeClient
init|=
literal|false
decl_stmt|;
DECL|field|cmdExecutor
specifier|private
name|ZkCmdExecutor
name|cmdExecutor
init|=
operator|new
name|ZkCmdExecutor
argument_list|()
decl_stmt|;
DECL|method|ZkStateReader
specifier|public
name|ZkStateReader
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|)
block|{
name|this
operator|.
name|zkClient
operator|=
name|zkClient
expr_stmt|;
block|}
DECL|method|ZkStateReader
specifier|public
name|ZkStateReader
parameter_list|(
name|String
name|zkServerAddress
parameter_list|,
name|int
name|zkClientTimeout
parameter_list|,
name|int
name|zkClientConnectTimeout
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
throws|,
name|IOException
block|{
name|closeClient
operator|=
literal|true
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServerAddress
argument_list|,
name|zkClientTimeout
argument_list|,
name|zkClientConnectTimeout
argument_list|,
comment|// on reconnect, reload cloud info
operator|new
name|OnReconnect
argument_list|()
block|{
specifier|public
name|void
name|command
parameter_list|()
block|{
try|try
block|{
name|ZkStateReader
operator|.
name|this
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
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
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
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
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// load and publish a new CollectionInfo
DECL|method|updateClusterState
specifier|public
name|void
name|updateClusterState
parameter_list|(
name|boolean
name|immediate
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|updateClusterState
argument_list|(
name|immediate
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// load and publish a new CollectionInfo
DECL|method|updateLiveNodes
specifier|public
name|void
name|updateLiveNodes
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|updateClusterState
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|createClusterStateWatchersAndUpdate
specifier|public
specifier|synchronized
name|void
name|createClusterStateWatchersAndUpdate
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
comment|// We need to fetch the current cluster state and the set of live nodes
synchronized|synchronized
init|(
name|getUpdateLock
argument_list|()
init|)
block|{
name|cmdExecutor
operator|.
name|ensureExists
argument_list|(
name|CLUSTER_STATE
argument_list|,
name|zkClient
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Updating cluster state from ZooKeeper... "
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|exists
argument_list|(
name|CLUSTER_STATE
argument_list|,
operator|new
name|Watcher
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
comment|// session events are not change events,
comment|// and do not remove the watcher
if|if
condition|(
name|EventType
operator|.
name|None
operator|.
name|equals
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"A cluster state change has occurred - updating..."
argument_list|)
expr_stmt|;
try|try
block|{
comment|// delayed approach
comment|// ZkStateReader.this.updateClusterState(false, false);
synchronized|synchronized
init|(
name|ZkStateReader
operator|.
name|this
operator|.
name|getUpdateLock
argument_list|()
init|)
block|{
comment|// remake watch
specifier|final
name|Watcher
name|thisWatch
init|=
name|this
decl_stmt|;
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|CLUSTER_STATE
argument_list|,
name|thisWatch
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|load
argument_list|(
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|,
name|data
argument_list|,
name|ZkStateReader
operator|.
name|this
operator|.
name|clusterState
operator|.
name|getLiveNodes
argument_list|()
argument_list|)
decl_stmt|;
comment|// update volatile
name|ZkStateReader
operator|.
name|this
operator|.
name|clusterState
operator|=
name|clusterState
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|code
argument_list|()
operator|==
name|KeeperException
operator|.
name|Code
operator|.
name|SESSIONEXPIRED
operator|||
name|e
operator|.
name|code
argument_list|()
operator|==
name|KeeperException
operator|.
name|Code
operator|.
name|CONNECTIONLOSS
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"ZooKeeper watch triggered, but Solr cannot talk to ZK"
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
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
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|ZkStateReader
operator|.
name|this
operator|.
name|getUpdateLock
argument_list|()
init|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|LIVE_NODES_ZKNODE
argument_list|,
operator|new
name|Watcher
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
comment|// session events are not change events,
comment|// and do not remove the watcher
if|if
condition|(
name|EventType
operator|.
name|None
operator|.
name|equals
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Updating live nodes"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// delayed approach
comment|// ZkStateReader.this.updateClusterState(false, true);
synchronized|synchronized
init|(
name|ZkStateReader
operator|.
name|this
operator|.
name|getUpdateLock
argument_list|()
init|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|LIVE_NODES_ZKNODE
argument_list|,
name|this
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodesSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|liveNodesSet
operator|.
name|addAll
argument_list|(
name|liveNodes
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
operator|new
name|ClusterState
argument_list|(
name|ZkStateReader
operator|.
name|this
operator|.
name|clusterState
operator|.
name|getZkClusterStateVersion
argument_list|()
argument_list|,
name|liveNodesSet
argument_list|,
name|ZkStateReader
operator|.
name|this
operator|.
name|clusterState
operator|.
name|getCollectionStates
argument_list|()
argument_list|)
decl_stmt|;
name|ZkStateReader
operator|.
name|this
operator|.
name|clusterState
operator|=
name|clusterState
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|code
argument_list|()
operator|==
name|KeeperException
operator|.
name|Code
operator|.
name|SESSIONEXPIRED
operator|||
name|e
operator|.
name|code
argument_list|()
operator|==
name|KeeperException
operator|.
name|Code
operator|.
name|CONNECTIONLOSS
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"ZooKeeper watch triggered, but Solr cannot talk to ZK"
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
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
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodeSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|liveNodeSet
operator|.
name|addAll
argument_list|(
name|liveNodes
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|load
argument_list|(
name|zkClient
argument_list|,
name|liveNodeSet
argument_list|)
decl_stmt|;
name|this
operator|.
name|clusterState
operator|=
name|clusterState
expr_stmt|;
block|}
block|}
comment|// load and publish a new CollectionInfo
DECL|method|updateClusterState
specifier|private
specifier|synchronized
name|void
name|updateClusterState
parameter_list|(
name|boolean
name|immediate
parameter_list|,
specifier|final
name|boolean
name|onlyLiveNodes
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
comment|// build immutable CloudInfo
if|if
condition|(
name|immediate
condition|)
block|{
name|ClusterState
name|clusterState
decl_stmt|;
synchronized|synchronized
init|(
name|getUpdateLock
argument_list|()
init|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|LIVE_NODES_ZKNODE
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodesSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|liveNodesSet
operator|.
name|addAll
argument_list|(
name|liveNodes
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|onlyLiveNodes
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Updating cloud state from ZooKeeper... "
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|load
argument_list|(
name|zkClient
argument_list|,
name|liveNodesSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Updating live nodes from ZooKeeper... "
argument_list|)
expr_stmt|;
name|clusterState
operator|=
operator|new
name|ClusterState
argument_list|(
name|ZkStateReader
operator|.
name|this
operator|.
name|clusterState
operator|.
name|getZkClusterStateVersion
argument_list|()
argument_list|,
name|liveNodesSet
argument_list|,
name|ZkStateReader
operator|.
name|this
operator|.
name|clusterState
operator|.
name|getCollectionStates
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|clusterState
operator|=
name|clusterState
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|clusterStateUpdateScheduled
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Cloud state update for ZooKeeper already scheduled"
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Scheduling cloud state update from ZooKeeper..."
argument_list|)
expr_stmt|;
name|clusterStateUpdateScheduled
operator|=
literal|true
expr_stmt|;
name|updateCloudExecutor
operator|.
name|schedule
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Updating cluster state from ZooKeeper..."
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|getUpdateLock
argument_list|()
init|)
block|{
name|clusterStateUpdateScheduled
operator|=
literal|false
expr_stmt|;
name|ClusterState
name|clusterState
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|LIVE_NODES_ZKNODE
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodesSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|liveNodesSet
operator|.
name|addAll
argument_list|(
name|liveNodes
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|onlyLiveNodes
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Updating cloud state from ZooKeeper... "
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|load
argument_list|(
name|zkClient
argument_list|,
name|liveNodesSet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Updating live nodes from ZooKeeper... "
argument_list|)
expr_stmt|;
name|clusterState
operator|=
operator|new
name|ClusterState
argument_list|(
name|ZkStateReader
operator|.
name|this
operator|.
name|clusterState
operator|.
name|getZkClusterStateVersion
argument_list|()
argument_list|,
name|liveNodesSet
argument_list|,
name|ZkStateReader
operator|.
name|this
operator|.
name|clusterState
operator|.
name|getCollectionStates
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ZkStateReader
operator|.
name|this
operator|.
name|clusterState
operator|=
name|clusterState
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|code
argument_list|()
operator|==
name|KeeperException
operator|.
name|Code
operator|.
name|SESSIONEXPIRED
operator|||
name|e
operator|.
name|code
argument_list|()
operator|==
name|KeeperException
operator|.
name|Code
operator|.
name|CONNECTIONLOSS
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"ZooKeeper watch triggered, but Solr cannot talk to ZK"
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
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
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
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
comment|// update volatile
name|ZkStateReader
operator|.
name|this
operator|.
name|clusterState
operator|=
name|clusterState
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|SOLRCLOUD_UPDATE_DELAY
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return information about the cluster from ZooKeeper    */
DECL|method|getClusterState
specifier|public
name|ClusterState
name|getClusterState
parameter_list|()
block|{
return|return
name|clusterState
return|;
block|}
DECL|method|getUpdateLock
specifier|public
name|Object
name|getUpdateLock
parameter_list|()
block|{
return|return
name|this
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
name|closeClient
condition|)
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|RunnableWatcher
specifier|abstract
class|class
name|RunnableWatcher
implements|implements
name|Runnable
block|{
DECL|field|watcher
name|Watcher
name|watcher
decl_stmt|;
DECL|method|RunnableWatcher
specifier|public
name|RunnableWatcher
parameter_list|(
name|Watcher
name|watcher
parameter_list|)
block|{
name|this
operator|.
name|watcher
operator|=
name|watcher
expr_stmt|;
block|}
block|}
DECL|method|getLeaderUrl
specifier|public
name|String
name|getLeaderUrl
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|shard
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|ZkCoreNodeProps
name|props
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|getLeaderProps
argument_list|(
name|collection
argument_list|,
name|shard
argument_list|,
name|timeout
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|props
operator|.
name|getCoreUrl
argument_list|()
return|;
block|}
comment|/**    * Get shard leader properties.    */
DECL|method|getLeaderProps
specifier|public
name|ZkNodeProps
name|getLeaderProps
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|shard
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|getLeaderProps
argument_list|(
name|collection
argument_list|,
name|shard
argument_list|,
literal|1000
argument_list|)
return|;
block|}
DECL|method|getLeaderProps
specifier|public
name|ZkNodeProps
name|getLeaderProps
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|shard
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|timeoutAt
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|timeout
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|timeoutAt
condition|)
block|{
if|if
condition|(
name|clusterState
operator|!=
literal|null
condition|)
block|{
specifier|final
name|ZkNodeProps
name|nodeProps
init|=
name|clusterState
operator|.
name|getLeader
argument_list|(
name|collection
argument_list|,
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeProps
operator|!=
literal|null
condition|)
block|{
return|return
name|nodeProps
return|;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No registered leader was found, collection:"
operator|+
name|collection
operator|+
literal|" slice:"
operator|+
name|shard
argument_list|)
throw|;
block|}
comment|/**    * Get path where shard leader properties live in zookeeper.    */
DECL|method|getShardLeadersPath
specifier|public
specifier|static
name|String
name|getShardLeadersPath
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|shardId
parameter_list|)
block|{
return|return
name|COLLECTIONS_ZKNODE
operator|+
literal|"/"
operator|+
name|collection
operator|+
literal|"/"
operator|+
name|SHARD_LEADERS_ZKNODE
operator|+
operator|(
name|shardId
operator|!=
literal|null
condition|?
operator|(
literal|"/"
operator|+
name|shardId
operator|)
else|:
literal|""
operator|)
return|;
block|}
comment|/**    * Get CoreNodeName for a core. This name is unique across the collection.      * @param nodeName in form: 127.0.0.1:54065_solr    */
DECL|method|getCoreNodeName
specifier|public
specifier|static
name|String
name|getCoreNodeName
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
return|return
name|nodeName
operator|+
literal|"_"
operator|+
name|coreName
return|;
block|}
DECL|method|getReplicaProps
specifier|public
name|List
argument_list|<
name|ZkCoreNodeProps
argument_list|>
name|getReplicaProps
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|shardId
parameter_list|,
name|String
name|thisNodeName
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
return|return
name|getReplicaProps
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|,
name|thisNodeName
argument_list|,
name|coreName
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getReplicaProps
specifier|public
name|List
argument_list|<
name|ZkCoreNodeProps
argument_list|>
name|getReplicaProps
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|shardId
parameter_list|,
name|String
name|thisNodeName
parameter_list|,
name|String
name|coreName
parameter_list|,
name|String
name|mustMatchStateFilter
parameter_list|)
block|{
return|return
name|getReplicaProps
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|,
name|thisNodeName
argument_list|,
name|coreName
argument_list|,
name|mustMatchStateFilter
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getReplicaProps
specifier|public
name|List
argument_list|<
name|ZkCoreNodeProps
argument_list|>
name|getReplicaProps
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|shardId
parameter_list|,
name|String
name|thisNodeName
parameter_list|,
name|String
name|coreName
parameter_list|,
name|String
name|mustMatchStateFilter
parameter_list|,
name|String
name|mustNotMatchStateFilter
parameter_list|)
block|{
name|ClusterState
name|clusterState
init|=
name|this
operator|.
name|clusterState
decl_stmt|;
if|if
condition|(
name|clusterState
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getSlices
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|slices
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Could not find collection in zk: "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|clusterState
operator|.
name|getCollections
argument_list|()
argument_list|)
throw|;
block|}
name|Slice
name|replicas
init|=
name|slices
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicas
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Could not find shardId in zk: "
operator|+
name|shardId
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|shardMap
init|=
name|replicas
operator|.
name|getReplicasMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ZkCoreNodeProps
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<
name|ZkCoreNodeProps
argument_list|>
argument_list|(
name|shardMap
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|filterNodeName
init|=
name|thisNodeName
operator|+
literal|"_"
operator|+
name|coreName
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|entry
range|:
name|shardMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ZkCoreNodeProps
name|nodeProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|coreNodeName
init|=
name|nodeProps
operator|.
name|getNodeName
argument_list|()
operator|+
literal|"_"
operator|+
name|nodeProps
operator|.
name|getCoreName
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterState
operator|.
name|liveNodesContain
argument_list|(
name|nodeProps
operator|.
name|getNodeName
argument_list|()
argument_list|)
operator|&&
operator|!
name|coreNodeName
operator|.
name|equals
argument_list|(
name|filterNodeName
argument_list|)
condition|)
block|{
if|if
condition|(
name|mustMatchStateFilter
operator|==
literal|null
operator|||
name|mustMatchStateFilter
operator|.
name|equals
argument_list|(
name|nodeProps
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|mustNotMatchStateFilter
operator|==
literal|null
operator|||
operator|!
name|mustNotMatchStateFilter
operator|.
name|equals
argument_list|(
name|nodeProps
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|nodeProps
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|nodes
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// no replicas - go local
return|return
literal|null
return|;
block|}
return|return
name|nodes
return|;
block|}
DECL|method|getZkClient
specifier|public
name|SolrZkClient
name|getZkClient
parameter_list|()
block|{
return|return
name|zkClient
return|;
block|}
block|}
end_class
end_unit
