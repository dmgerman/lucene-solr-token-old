begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|ZkCredentialsProvider
operator|.
name|ZkCredentials
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
comment|/**  *  */
end_comment
begin_class
DECL|class|ZkClientConnectionStrategy
specifier|public
specifier|abstract
class|class
name|ZkClientConnectionStrategy
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
DECL|field|zkCredentialsToAddAutomatically
specifier|private
specifier|volatile
name|ZkCredentialsProvider
name|zkCredentialsToAddAutomatically
decl_stmt|;
DECL|field|zkCredentialsToAddAutomaticallyUsed
specifier|private
specifier|volatile
name|boolean
name|zkCredentialsToAddAutomaticallyUsed
decl_stmt|;
DECL|field|disconnectedListeners
specifier|private
name|List
argument_list|<
name|DisconnectedListener
argument_list|>
name|disconnectedListeners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|connectedListeners
specifier|private
name|List
argument_list|<
name|ConnectedListener
argument_list|>
name|connectedListeners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|connect
specifier|public
specifier|abstract
name|void
name|connect
parameter_list|(
name|String
name|zkServerAddress
parameter_list|,
name|int
name|zkClientTimeout
parameter_list|,
name|Watcher
name|watcher
parameter_list|,
name|ZkUpdate
name|updater
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
function_decl|;
DECL|method|reconnect
specifier|public
specifier|abstract
name|void
name|reconnect
parameter_list|(
name|String
name|serverAddress
parameter_list|,
name|int
name|zkClientTimeout
parameter_list|,
name|Watcher
name|watcher
parameter_list|,
name|ZkUpdate
name|updater
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
function_decl|;
DECL|method|ZkClientConnectionStrategy
specifier|public
name|ZkClientConnectionStrategy
parameter_list|()
block|{
name|zkCredentialsToAddAutomaticallyUsed
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|disconnected
specifier|public
specifier|synchronized
name|void
name|disconnected
parameter_list|()
block|{
for|for
control|(
name|DisconnectedListener
name|listener
range|:
name|disconnectedListeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|disconnected
argument_list|()
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
block|}
block|}
DECL|method|connected
specifier|public
specifier|synchronized
name|void
name|connected
parameter_list|()
block|{
for|for
control|(
name|ConnectedListener
name|listener
range|:
name|connectedListeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|connected
argument_list|()
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
block|}
block|}
DECL|interface|DisconnectedListener
specifier|public
interface|interface
name|DisconnectedListener
block|{
DECL|method|disconnected
specifier|public
name|void
name|disconnected
parameter_list|()
function_decl|;
block|}
empty_stmt|;
DECL|interface|ConnectedListener
specifier|public
interface|interface
name|ConnectedListener
block|{
DECL|method|connected
specifier|public
name|void
name|connected
parameter_list|()
function_decl|;
block|}
empty_stmt|;
DECL|method|addDisconnectedListener
specifier|public
specifier|synchronized
name|void
name|addDisconnectedListener
parameter_list|(
name|DisconnectedListener
name|listener
parameter_list|)
block|{
name|disconnectedListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|addConnectedListener
specifier|public
specifier|synchronized
name|void
name|addConnectedListener
parameter_list|(
name|ConnectedListener
name|listener
parameter_list|)
block|{
name|connectedListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|class|ZkUpdate
specifier|public
specifier|static
specifier|abstract
class|class
name|ZkUpdate
block|{
DECL|method|update
specifier|public
specifier|abstract
name|void
name|update
parameter_list|(
name|SolrZooKeeper
name|zooKeeper
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
throws|,
name|IOException
function_decl|;
block|}
DECL|method|setZkCredentialsToAddAutomatically
specifier|public
name|void
name|setZkCredentialsToAddAutomatically
parameter_list|(
name|ZkCredentialsProvider
name|zkCredentialsToAddAutomatically
parameter_list|)
block|{
if|if
condition|(
name|zkCredentialsToAddAutomaticallyUsed
operator|||
operator|(
name|zkCredentialsToAddAutomatically
operator|==
literal|null
operator|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot change zkCredentialsToAddAutomatically after it has been (connect or reconnect was called) used or to null"
argument_list|)
throw|;
name|this
operator|.
name|zkCredentialsToAddAutomatically
operator|=
name|zkCredentialsToAddAutomatically
expr_stmt|;
block|}
DECL|method|hasZkCredentialsToAddAutomatically
specifier|public
name|boolean
name|hasZkCredentialsToAddAutomatically
parameter_list|()
block|{
return|return
name|zkCredentialsToAddAutomatically
operator|!=
literal|null
return|;
block|}
DECL|method|createSolrZooKeeper
specifier|protected
name|SolrZooKeeper
name|createSolrZooKeeper
parameter_list|(
specifier|final
name|String
name|serverAddress
parameter_list|,
specifier|final
name|int
name|zkClientTimeout
parameter_list|,
specifier|final
name|Watcher
name|watcher
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrZooKeeper
name|result
init|=
operator|new
name|SolrZooKeeper
argument_list|(
name|serverAddress
argument_list|,
name|zkClientTimeout
argument_list|,
name|watcher
argument_list|)
decl_stmt|;
name|zkCredentialsToAddAutomaticallyUsed
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|ZkCredentials
name|zkCredentials
range|:
name|zkCredentialsToAddAutomatically
operator|.
name|getCredentials
argument_list|()
control|)
block|{
name|result
operator|.
name|addAuthInfo
argument_list|(
name|zkCredentials
operator|.
name|getScheme
argument_list|()
argument_list|,
name|zkCredentials
operator|.
name|getAuth
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
