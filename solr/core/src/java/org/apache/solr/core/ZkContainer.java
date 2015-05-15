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
name|file
operator|.
name|Files
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Collection
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
name|ExecutorService
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
name|TimeoutException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|MDC
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
name|CurrentCoreDescriptorProvider
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
name|SolrZkServer
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
name|ZkController
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
name|Replica
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
name|ZkConfigManager
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
name|logging
operator|.
name|MDCUtils
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
name|zookeeper
operator|.
name|KeeperException
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
import|;
end_import
begin_class
DECL|class|ZkContainer
specifier|public
class|class
name|ZkContainer
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
name|ZkContainer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zkController
specifier|protected
name|ZkController
name|zkController
decl_stmt|;
DECL|field|zkServer
specifier|private
name|SolrZkServer
name|zkServer
decl_stmt|;
DECL|field|coreZkRegister
specifier|private
name|ExecutorService
name|coreZkRegister
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareCachedThreadPool
argument_list|(
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"coreZkRegister"
argument_list|)
argument_list|)
decl_stmt|;
comment|// see ZkController.zkRunOnly
DECL|field|zkRunOnly
specifier|private
name|boolean
name|zkRunOnly
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"zkRunOnly"
argument_list|)
decl_stmt|;
comment|// expert
DECL|method|ZkContainer
specifier|public
name|ZkContainer
parameter_list|()
block|{        }
DECL|method|initZooKeeper
specifier|public
name|void
name|initZooKeeper
parameter_list|(
specifier|final
name|CoreContainer
name|cc
parameter_list|,
name|String
name|solrHome
parameter_list|,
name|CloudConfig
name|config
parameter_list|)
block|{
name|ZkController
name|zkController
init|=
literal|null
decl_stmt|;
name|String
name|zkRun
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"zkRun"
argument_list|)
decl_stmt|;
if|if
condition|(
name|zkRun
operator|!=
literal|null
operator|&&
name|config
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
literal|"Cannot start Solr in cloud mode - no cloud config provided"
argument_list|)
throw|;
if|if
condition|(
name|config
operator|==
literal|null
condition|)
return|return;
comment|// not in zk mode
name|String
name|zookeeperHost
init|=
name|config
operator|.
name|getZkHost
argument_list|()
decl_stmt|;
comment|// zookeeper in quorum mode currently causes a failure when trying to
comment|// register log4j mbeans.  See SOLR-2369
comment|// TODO: remove after updating to an slf4j based zookeeper
name|System
operator|.
name|setProperty
argument_list|(
literal|"zookeeper.jmx.log4j.disable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
name|zkRun
operator|!=
literal|null
condition|)
block|{
name|String
name|zkDataHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"zkServerDataDir"
argument_list|,
name|solrHome
operator|+
literal|"zoo_data"
argument_list|)
decl_stmt|;
name|String
name|zkConfHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"zkServerConfDir"
argument_list|,
name|solrHome
argument_list|)
decl_stmt|;
name|zkServer
operator|=
operator|new
name|SolrZkServer
argument_list|(
name|stripChroot
argument_list|(
name|zkRun
argument_list|)
argument_list|,
name|stripChroot
argument_list|(
name|config
operator|.
name|getZkHost
argument_list|()
argument_list|)
argument_list|,
name|zkDataHome
argument_list|,
name|zkConfHome
argument_list|,
name|config
operator|.
name|getSolrHostPort
argument_list|()
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|parseConfig
argument_list|()
expr_stmt|;
name|zkServer
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// set client from server config if not already set
if|if
condition|(
name|zookeeperHost
operator|==
literal|null
condition|)
block|{
name|zookeeperHost
operator|=
name|zkServer
operator|.
name|getClientString
argument_list|()
expr_stmt|;
block|}
block|}
name|int
name|zkClientConnectTimeout
init|=
literal|30000
decl_stmt|;
if|if
condition|(
name|zookeeperHost
operator|!=
literal|null
condition|)
block|{
comment|// we are ZooKeeper enabled
try|try
block|{
comment|// If this is an ensemble, allow for a long connect time for other servers to come up
if|if
condition|(
name|zkRun
operator|!=
literal|null
operator|&&
name|zkServer
operator|.
name|getServers
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|zkClientConnectTimeout
operator|=
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
expr_stmt|;
comment|// 1 day for embedded ensemble
name|log
operator|.
name|info
argument_list|(
literal|"Zookeeper client="
operator|+
name|zookeeperHost
operator|+
literal|"  Waiting for a quorum."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Zookeeper client="
operator|+
name|zookeeperHost
argument_list|)
expr_stmt|;
block|}
name|String
name|confDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"bootstrap_confdir"
argument_list|)
decl_stmt|;
name|boolean
name|boostrapConf
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"bootstrap_conf"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ZkController
operator|.
name|checkChrootPath
argument_list|(
name|zookeeperHost
argument_list|,
operator|(
name|confDir
operator|!=
literal|null
operator|)
operator|||
name|boostrapConf
operator|||
name|zkRunOnly
argument_list|)
condition|)
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
literal|"A chroot was specified in ZkHost but the znode doesn't exist. "
operator|+
name|zookeeperHost
argument_list|)
throw|;
block|}
name|zkController
operator|=
operator|new
name|ZkController
argument_list|(
name|cc
argument_list|,
name|zookeeperHost
argument_list|,
name|zkClientConnectTimeout
argument_list|,
name|config
argument_list|,
operator|new
name|CurrentCoreDescriptorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|getCurrentDescriptors
parameter_list|()
block|{
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|descriptors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|cc
operator|.
name|getCoreNames
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|SolrCore
argument_list|>
name|cores
init|=
name|cc
operator|.
name|getCores
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrCore
name|core
range|:
name|cores
control|)
block|{
name|descriptors
operator|.
name|add
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|descriptors
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|zkRun
operator|!=
literal|null
operator|&&
name|zkServer
operator|.
name|getServers
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|&&
name|confDir
operator|==
literal|null
operator|&&
name|boostrapConf
operator|==
literal|false
condition|)
block|{
comment|// we are part of an ensemble and we are not uploading the config - pause to give the config time
comment|// to get up
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|confDir
operator|!=
literal|null
condition|)
block|{
name|Path
name|configPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|confDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|configPath
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"bootstrap_confdir must be a directory of configuration files"
argument_list|)
throw|;
name|String
name|confName
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|ZkController
operator|.
name|COLLECTION_PARAM_PREFIX
operator|+
name|ZkController
operator|.
name|CONFIGNAME_PROP
argument_list|,
literal|"configuration1"
argument_list|)
decl_stmt|;
name|ZkConfigManager
name|configManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zkController
operator|.
name|getZkClient
argument_list|()
argument_list|)
decl_stmt|;
name|configManager
operator|.
name|uploadConfigDir
argument_list|(
name|configPath
argument_list|,
name|confName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|boostrapConf
condition|)
block|{
name|ZkController
operator|.
name|bootstrapConf
argument_list|(
name|zkController
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|cc
argument_list|,
name|solrHome
argument_list|)
expr_stmt|;
block|}
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
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not connect to ZooKeeper"
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
name|IOException
decl||
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
block|}
name|this
operator|.
name|zkController
operator|=
name|zkController
expr_stmt|;
block|}
DECL|method|stripChroot
specifier|private
name|String
name|stripChroot
parameter_list|(
name|String
name|zkRun
parameter_list|)
block|{
if|if
condition|(
name|zkRun
operator|==
literal|null
operator|||
name|zkRun
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|zkRun
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|<
literal|0
condition|)
return|return
name|zkRun
return|;
return|return
name|zkRun
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|zkRun
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
argument_list|)
return|;
block|}
DECL|method|registerInZk
specifier|public
name|void
name|registerInZk
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|,
name|boolean
name|background
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|zkController
operator|.
name|register
argument_list|(
name|core
operator|.
name|getName
argument_list|()
argument_list|,
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|)
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|zkController
operator|.
name|publish
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|,
name|Replica
operator|.
name|State
operator|.
name|DOWN
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
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
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
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
decl_stmt|;
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
block|{
name|MDCUtils
operator|.
name|setCore
argument_list|(
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|background
condition|)
block|{
name|coreZkRegister
operator|.
name|execute
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|thread
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|MDC
operator|.
name|remove
argument_list|(
name|CORE_NAME_PROP
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getZkController
specifier|public
name|ZkController
name|getZkController
parameter_list|()
block|{
return|return
name|zkController
return|;
block|}
DECL|method|publishCoresAsDown
specifier|public
name|void
name|publishCoresAsDown
parameter_list|(
name|List
argument_list|<
name|SolrCore
argument_list|>
name|cores
parameter_list|)
block|{
for|for
control|(
name|SolrCore
name|core
range|:
name|cores
control|)
block|{
try|try
block|{
name|zkController
operator|.
name|publish
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|,
name|Replica
operator|.
name|State
operator|.
name|DOWN
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|CoreContainer
operator|.
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
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
name|interrupted
argument_list|()
expr_stmt|;
name|CoreContainer
operator|.
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
block|{
name|zkController
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|zkServer
operator|!=
literal|null
condition|)
block|{
name|zkServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|ExecutorUtil
operator|.
name|shutdownNowAndAwaitTermination
argument_list|(
name|coreZkRegister
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getCoreZkRegisterExecutorService
specifier|public
name|ExecutorService
name|getCoreZkRegisterExecutorService
parameter_list|()
block|{
return|return
name|coreZkRegister
return|;
block|}
block|}
end_class
end_unit
