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
name|KeeperState
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
DECL|class|ConnectionManager
specifier|public
class|class
name|ConnectionManager
implements|implements
name|Watcher
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConnectionManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|connected
specifier|private
specifier|volatile
name|boolean
name|connected
init|=
literal|false
decl_stmt|;
DECL|field|connectionStrategy
specifier|private
specifier|final
name|ZkClientConnectionStrategy
name|connectionStrategy
decl_stmt|;
DECL|field|zkServerAddress
specifier|private
specifier|final
name|String
name|zkServerAddress
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|SolrZkClient
name|client
decl_stmt|;
DECL|field|onReconnect
specifier|private
specifier|final
name|OnReconnect
name|onReconnect
decl_stmt|;
DECL|field|beforeReconnect
specifier|private
specifier|final
name|BeforeReconnect
name|beforeReconnect
decl_stmt|;
DECL|field|isClosed
specifier|private
specifier|volatile
name|boolean
name|isClosed
init|=
literal|false
decl_stmt|;
comment|// Track the likely expired state
DECL|class|LikelyExpiredState
specifier|private
specifier|static
class|class
name|LikelyExpiredState
block|{
DECL|field|NOT_EXPIRED
specifier|private
specifier|static
name|LikelyExpiredState
name|NOT_EXPIRED
init|=
operator|new
name|LikelyExpiredState
argument_list|(
name|StateType
operator|.
name|NOT_EXPIRED
argument_list|,
literal|0
argument_list|)
decl_stmt|;
DECL|field|EXPIRED
specifier|private
specifier|static
name|LikelyExpiredState
name|EXPIRED
init|=
operator|new
name|LikelyExpiredState
argument_list|(
name|StateType
operator|.
name|EXPIRED
argument_list|,
literal|0
argument_list|)
decl_stmt|;
DECL|enum|StateType
specifier|public
enum|enum
name|StateType
block|{
DECL|enum constant|NOT_EXPIRED
name|NOT_EXPIRED
block|,
comment|// definitely not expired
DECL|enum constant|EXPIRED
name|EXPIRED
block|,
comment|// definitely expired
DECL|enum constant|TRACKING_TIME
name|TRACKING_TIME
comment|// not sure, tracking time of last disconnect
block|}
DECL|field|stateType
specifier|private
name|StateType
name|stateType
decl_stmt|;
DECL|field|lastDisconnectTime
specifier|private
name|long
name|lastDisconnectTime
decl_stmt|;
DECL|method|LikelyExpiredState
specifier|public
name|LikelyExpiredState
parameter_list|(
name|StateType
name|stateType
parameter_list|,
name|long
name|lastDisconnectTime
parameter_list|)
block|{
name|this
operator|.
name|stateType
operator|=
name|stateType
expr_stmt|;
name|this
operator|.
name|lastDisconnectTime
operator|=
name|lastDisconnectTime
expr_stmt|;
block|}
DECL|method|isLikelyExpired
specifier|public
name|boolean
name|isLikelyExpired
parameter_list|(
name|long
name|timeToExpire
parameter_list|)
block|{
return|return
name|stateType
operator|==
name|StateType
operator|.
name|EXPIRED
operator|||
operator|(
name|stateType
operator|==
name|StateType
operator|.
name|TRACKING_TIME
operator|&&
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|lastDisconnectTime
operator|>
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|timeToExpire
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|)
operator|)
return|;
block|}
block|}
DECL|field|likelyExpiredState
specifier|private
specifier|volatile
name|LikelyExpiredState
name|likelyExpiredState
init|=
name|LikelyExpiredState
operator|.
name|EXPIRED
decl_stmt|;
DECL|method|ConnectionManager
specifier|public
name|ConnectionManager
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrZkClient
name|client
parameter_list|,
name|String
name|zkServerAddress
parameter_list|,
name|ZkClientConnectionStrategy
name|strat
parameter_list|,
name|OnReconnect
name|onConnect
parameter_list|,
name|BeforeReconnect
name|beforeReconnect
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|connectionStrategy
operator|=
name|strat
expr_stmt|;
name|this
operator|.
name|zkServerAddress
operator|=
name|zkServerAddress
expr_stmt|;
name|this
operator|.
name|onReconnect
operator|=
name|onConnect
expr_stmt|;
name|this
operator|.
name|beforeReconnect
operator|=
name|beforeReconnect
expr_stmt|;
block|}
DECL|method|connected
specifier|private
specifier|synchronized
name|void
name|connected
parameter_list|()
block|{
name|connected
operator|=
literal|true
expr_stmt|;
name|likelyExpiredState
operator|=
name|LikelyExpiredState
operator|.
name|NOT_EXPIRED
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
DECL|method|disconnected
specifier|private
specifier|synchronized
name|void
name|disconnected
parameter_list|()
block|{
name|connected
operator|=
literal|false
expr_stmt|;
comment|// record the time we expired unless we are already likely expired
if|if
condition|(
operator|!
name|likelyExpiredState
operator|.
name|isLikelyExpired
argument_list|(
literal|0
argument_list|)
condition|)
block|{
name|likelyExpiredState
operator|=
operator|new
name|LikelyExpiredState
argument_list|(
name|LikelyExpiredState
operator|.
name|StateType
operator|.
name|TRACKING_TIME
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|notifyAll
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Watcher "
operator|+
name|this
operator|+
literal|" name:"
operator|+
name|name
operator|+
literal|" got event "
operator|+
name|event
operator|+
literal|" path:"
operator|+
name|event
operator|.
name|getPath
argument_list|()
operator|+
literal|" type:"
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isClosed
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Client->ZooKeeper status change trigger but we are already closed"
argument_list|)
expr_stmt|;
return|return;
block|}
name|KeeperState
name|state
init|=
name|event
operator|.
name|getState
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|==
name|KeeperState
operator|.
name|SyncConnected
condition|)
block|{
name|connected
argument_list|()
expr_stmt|;
name|connectionStrategy
operator|.
name|connected
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|KeeperState
operator|.
name|Expired
condition|)
block|{
comment|// we don't call disconnected here, because we know we are expired
name|connected
operator|=
literal|false
expr_stmt|;
name|likelyExpiredState
operator|=
name|LikelyExpiredState
operator|.
name|EXPIRED
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Our previous ZooKeeper session was expired. Attempting to reconnect to recover relationship with ZooKeeper..."
argument_list|)
expr_stmt|;
if|if
condition|(
name|beforeReconnect
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|beforeReconnect
operator|.
name|command
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Exception running beforeReconnect command"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|connectionStrategy
operator|.
name|reconnect
argument_list|(
name|zkServerAddress
argument_list|,
name|client
operator|.
name|getZkClientTimeout
argument_list|()
argument_list|,
name|this
argument_list|,
operator|new
name|ZkClientConnectionStrategy
operator|.
name|ZkUpdate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
name|SolrZooKeeper
name|keeper
parameter_list|)
block|{
try|try
block|{
name|waitForConnected
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
name|closeKeeper
argument_list|(
name|keeper
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e1
argument_list|)
throw|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Connection with ZooKeeper reestablished."
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|updateKeeper
argument_list|(
name|keeper
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|closeKeeper
argument_list|(
name|keeper
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// we must have been asked to stop
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|t
parameter_list|)
block|{
name|closeKeeper
argument_list|(
name|keeper
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|t
argument_list|)
throw|;
block|}
if|if
condition|(
name|onReconnect
operator|!=
literal|null
condition|)
block|{
name|onReconnect
operator|.
name|command
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Connected:"
operator|+
name|connected
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|KeeperState
operator|.
name|Disconnected
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"zkClient has disconnected"
argument_list|)
expr_stmt|;
name|disconnected
argument_list|()
expr_stmt|;
name|connectionStrategy
operator|.
name|disconnected
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|==
name|KeeperState
operator|.
name|AuthFailed
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"zkClient received AuthFailed"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isConnected
specifier|public
specifier|synchronized
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
operator|!
name|isClosed
operator|&&
name|connected
return|;
block|}
comment|// we use a volatile rather than sync
comment|// to avoid possible deadlock on shutdown
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|this
operator|.
name|isClosed
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|likelyExpiredState
operator|=
name|LikelyExpiredState
operator|.
name|EXPIRED
expr_stmt|;
block|}
DECL|method|isLikelyExpired
specifier|public
name|boolean
name|isLikelyExpired
parameter_list|()
block|{
return|return
name|isClosed
operator|||
name|likelyExpiredState
operator|.
name|isLikelyExpired
argument_list|(
call|(
name|long
call|)
argument_list|(
name|client
operator|.
name|getZkClientTimeout
argument_list|()
operator|*
literal|0.90
argument_list|)
argument_list|)
return|;
block|}
DECL|method|waitForConnected
specifier|public
specifier|synchronized
name|void
name|waitForConnected
parameter_list|(
name|long
name|waitForConnection
parameter_list|)
throws|throws
name|TimeoutException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Waiting for client to connect to ZooKeeper"
argument_list|)
expr_stmt|;
name|long
name|expire
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|waitForConnection
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|long
name|left
init|=
literal|1
decl_stmt|;
while|while
condition|(
operator|!
name|connected
operator|&&
name|left
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|isClosed
condition|)
block|{
break|break;
block|}
try|try
block|{
name|wait
argument_list|(
literal|500
argument_list|)
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
break|break;
block|}
name|left
operator|=
name|expire
operator|-
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|connected
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"Could not connect to ZooKeeper "
operator|+
name|zkServerAddress
operator|+
literal|" within "
operator|+
name|waitForConnection
operator|+
literal|" ms"
argument_list|)
throw|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Client is connected to ZooKeeper"
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForDisconnected
specifier|public
specifier|synchronized
name|void
name|waitForDisconnected
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|long
name|expire
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|long
name|left
init|=
name|timeout
decl_stmt|;
while|while
condition|(
name|connected
operator|&&
name|left
operator|>
literal|0
condition|)
block|{
name|wait
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|left
operator|=
name|expire
operator|-
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|connected
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"Did not disconnect"
argument_list|)
throw|;
block|}
block|}
DECL|method|closeKeeper
specifier|private
name|void
name|closeKeeper
parameter_list|(
name|SolrZooKeeper
name|keeper
parameter_list|)
block|{
try|try
block|{
name|keeper
operator|.
name|close
argument_list|()
expr_stmt|;
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
end_class
end_unit
