begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package
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
name|server
operator|.
name|ServerConfig
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
name|server
operator|.
name|ZooKeeperServerMain
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
name|server
operator|.
name|quorum
operator|.
name|QuorumPeer
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
name|server
operator|.
name|quorum
operator|.
name|QuorumPeerConfig
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
name|server
operator|.
name|quorum
operator|.
name|QuorumPeerMain
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
name|server
operator|.
name|quorum
operator|.
name|flexible
operator|.
name|QuorumHierarchical
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
name|server
operator|.
name|quorum
operator|.
name|flexible
operator|.
name|QuorumMaj
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
name|io
operator|.
name|BufferedReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|InputStreamReader
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
name|net
operator|.
name|InetAddress
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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
name|Properties
import|;
end_import
begin_class
DECL|class|SolrZkServer
specifier|public
class|class
name|SolrZkServer
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
DECL|field|zkRun
name|String
name|zkRun
decl_stmt|;
DECL|field|zkHost
name|String
name|zkHost
decl_stmt|;
DECL|field|solrPort
name|int
name|solrPort
decl_stmt|;
DECL|field|props
name|Properties
name|props
decl_stmt|;
DECL|field|zkProps
name|SolrZkServerProps
name|zkProps
decl_stmt|;
DECL|field|zkThread
specifier|private
name|Thread
name|zkThread
decl_stmt|;
comment|// the thread running a zookeeper server, only if zkRun is set
DECL|field|dataHome
specifier|private
name|String
name|dataHome
decl_stmt|;
DECL|field|confHome
specifier|private
name|String
name|confHome
decl_stmt|;
DECL|method|SolrZkServer
specifier|public
name|SolrZkServer
parameter_list|(
name|String
name|zkRun
parameter_list|,
name|String
name|zkHost
parameter_list|,
name|String
name|dataHome
parameter_list|,
name|String
name|confHome
parameter_list|,
name|int
name|solrPort
parameter_list|)
block|{
name|this
operator|.
name|zkRun
operator|=
name|zkRun
expr_stmt|;
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|dataHome
operator|=
name|dataHome
expr_stmt|;
name|this
operator|.
name|confHome
operator|=
name|confHome
expr_stmt|;
name|this
operator|.
name|solrPort
operator|=
name|solrPort
expr_stmt|;
block|}
DECL|method|getClientString
specifier|public
name|String
name|getClientString
parameter_list|()
block|{
if|if
condition|(
name|zkHost
operator|!=
literal|null
condition|)
return|return
name|zkHost
return|;
if|if
condition|(
name|zkProps
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// if the string wasn't passed as zkHost, then use the standalone server we started
if|if
condition|(
name|zkRun
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
literal|"localhost:"
operator|+
name|zkProps
operator|.
name|getClientPortAddress
argument_list|()
operator|.
name|getPort
argument_list|()
return|;
block|}
DECL|method|parseConfig
specifier|public
name|void
name|parseConfig
parameter_list|()
block|{
if|if
condition|(
name|zkProps
operator|==
literal|null
condition|)
block|{
name|zkProps
operator|=
operator|new
name|SolrZkServerProps
argument_list|()
expr_stmt|;
comment|// set default data dir
comment|// TODO: use something based on IP+port???  support ensemble all from same solr home?
name|zkProps
operator|.
name|setDataDir
argument_list|(
name|dataHome
argument_list|)
expr_stmt|;
name|zkProps
operator|.
name|zkRun
operator|=
name|zkRun
expr_stmt|;
name|zkProps
operator|.
name|solrPort
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|solrPort
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|props
operator|=
name|SolrZkServerProps
operator|.
name|getProperties
argument_list|(
name|confHome
operator|+
literal|'/'
operator|+
literal|"zoo.cfg"
argument_list|)
expr_stmt|;
name|SolrZkServerProps
operator|.
name|injectServers
argument_list|(
name|props
argument_list|,
name|zkRun
argument_list|,
name|zkHost
argument_list|)
expr_stmt|;
name|zkProps
operator|.
name|parseProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
if|if
condition|(
name|zkProps
operator|.
name|getClientPortAddress
argument_list|()
operator|==
literal|null
condition|)
block|{
name|zkProps
operator|.
name|setClientPort
argument_list|(
name|solrPort
operator|+
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|QuorumPeerConfig
operator|.
name|ConfigException
decl||
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|zkRun
operator|!=
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
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getServers
specifier|public
name|Map
argument_list|<
name|Long
argument_list|,
name|QuorumPeer
operator|.
name|QuorumServer
argument_list|>
name|getServers
parameter_list|()
block|{
return|return
name|zkProps
operator|.
name|getServers
argument_list|()
return|;
block|}
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|zkRun
operator|==
literal|null
condition|)
return|return;
name|zkThread
operator|=
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
if|if
condition|(
name|zkProps
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
name|QuorumPeerMain
name|zkServer
init|=
operator|new
name|QuorumPeerMain
argument_list|()
decl_stmt|;
name|zkServer
operator|.
name|runFromConfig
argument_list|(
name|zkProps
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ServerConfig
name|sc
init|=
operator|new
name|ServerConfig
argument_list|()
decl_stmt|;
name|sc
operator|.
name|readFrom
argument_list|(
name|zkProps
argument_list|)
expr_stmt|;
name|ZooKeeperServerMain
name|zkServer
init|=
operator|new
name|ZooKeeperServerMain
argument_list|()
decl_stmt|;
name|zkServer
operator|.
name|runFromConfig
argument_list|(
name|sc
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"ZooKeeper Server exited."
argument_list|)
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
name|error
argument_list|(
literal|"ZooKeeper Server ERROR"
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
name|e
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
if|if
condition|(
name|zkProps
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
name|log
operator|.
name|info
argument_list|(
literal|"STARTING EMBEDDED ENSEMBLE ZOOKEEPER SERVER at port "
operator|+
name|zkProps
operator|.
name|getClientPortAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"STARTING EMBEDDED STANDALONE ZOOKEEPER SERVER at port "
operator|+
name|zkProps
operator|.
name|getClientPortAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|zkThread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|zkThread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
comment|// pause for ZooKeeper to start
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"STARTING ZOOKEEPER"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|zkRun
operator|==
literal|null
condition|)
return|return;
name|zkThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
end_class
begin_comment
comment|// Allows us to set a default for the data dir before parsing
end_comment
begin_comment
comment|// zoo.cfg (which validates that there is a dataDir)
end_comment
begin_class
DECL|class|SolrZkServerProps
class|class
name|SolrZkServerProps
extends|extends
name|QuorumPeerConfig
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
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
DECL|field|solrPort
name|String
name|solrPort
decl_stmt|;
comment|// port that Solr is listening on
DECL|field|zkRun
name|String
name|zkRun
decl_stmt|;
comment|/**    * Parse a ZooKeeper configuration file    * @param path the patch of the configuration file    * @throws ConfigException error processing configuration    */
DECL|method|getProperties
specifier|public
specifier|static
name|Properties
name|getProperties
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|ConfigException
block|{
name|File
name|configFile
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reading configuration from: "
operator|+
name|configFile
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|configFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|configFile
operator|.
name|toString
argument_list|()
operator|+
literal|" file is missing"
argument_list|)
throw|;
block|}
name|Properties
name|cfg
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|configFile
argument_list|)
decl_stmt|;
try|try
block|{
name|cfg
operator|.
name|load
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|cfg
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConfigException
argument_list|(
literal|"Error processing "
operator|+
name|path
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// Adds server.x if they don't exist, based on zkHost if it does exist.
comment|// Given zkHost=localhost:1111,localhost:2222 this will inject
comment|// server.0=localhost:1112:1113
comment|// server.1=localhost:2223:2224
DECL|method|injectServers
specifier|public
specifier|static
name|void
name|injectServers
parameter_list|(
name|Properties
name|props
parameter_list|,
name|String
name|zkRun
parameter_list|,
name|String
name|zkHost
parameter_list|)
block|{
comment|// if clientPort not already set, use zkRun
if|if
condition|(
name|zkRun
operator|!=
literal|null
operator|&&
name|props
operator|.
name|getProperty
argument_list|(
literal|"clientPort"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|int
name|portIdx
init|=
name|zkRun
operator|.
name|lastIndexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|portIdx
operator|>
literal|0
condition|)
block|{
name|String
name|portStr
init|=
name|zkRun
operator|.
name|substring
argument_list|(
name|portIdx
operator|+
literal|1
argument_list|)
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"clientPort"
argument_list|,
name|portStr
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|hasServers
init|=
name|hasServers
argument_list|(
name|props
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|hasServers
operator|&&
name|zkHost
operator|!=
literal|null
condition|)
block|{
name|int
name|alg
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"electionAlg"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|String
index|[]
name|hosts
init|=
name|zkHost
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|int
name|serverNum
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|hostAndPort
range|:
name|hosts
control|)
block|{
name|hostAndPort
operator|=
name|hostAndPort
operator|.
name|trim
argument_list|()
expr_stmt|;
name|int
name|portIdx
init|=
name|hostAndPort
operator|.
name|lastIndexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|String
name|clientPortStr
init|=
name|hostAndPort
operator|.
name|substring
argument_list|(
name|portIdx
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|clientPort
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|clientPortStr
argument_list|)
decl_stmt|;
name|String
name|host
init|=
name|hostAndPort
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|portIdx
argument_list|)
decl_stmt|;
name|String
name|serverStr
init|=
name|host
operator|+
literal|':'
operator|+
operator|(
name|clientPort
operator|+
literal|1
operator|)
decl_stmt|;
comment|// zk leader election algorithms other than 0 need an extra port for leader election.
if|if
condition|(
name|alg
operator|!=
literal|0
condition|)
block|{
name|serverStr
operator|=
name|serverStr
operator|+
literal|':'
operator|+
operator|(
name|clientPort
operator|+
literal|2
operator|)
expr_stmt|;
block|}
name|props
operator|.
name|setProperty
argument_list|(
literal|"server."
operator|+
name|serverNum
argument_list|,
name|serverStr
argument_list|)
expr_stmt|;
name|serverNum
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|method|hasServers
specifier|public
specifier|static
name|boolean
name|hasServers
parameter_list|(
name|Properties
name|props
parameter_list|)
block|{
for|for
control|(
name|Object
name|key
range|:
name|props
operator|.
name|keySet
argument_list|()
control|)
if|if
condition|(
operator|(
operator|(
name|String
operator|)
name|key
operator|)
operator|.
name|startsWith
argument_list|(
literal|"server."
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
comment|// called by the modified version of parseProperties
comment|// when the myid file is missing.
DECL|method|getMyServerId
specifier|public
name|Long
name|getMyServerId
parameter_list|()
block|{
if|if
condition|(
name|zkRun
operator|==
literal|null
operator|&&
name|solrPort
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Map
argument_list|<
name|Long
argument_list|,
name|QuorumPeer
operator|.
name|QuorumServer
argument_list|>
name|slist
init|=
name|getServers
argument_list|()
decl_stmt|;
name|String
name|myHost
init|=
literal|"localhost"
decl_stmt|;
name|InetSocketAddress
name|thisAddr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|zkRun
operator|!=
literal|null
operator|&&
name|zkRun
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|parts
index|[]
init|=
name|zkRun
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|myHost
operator|=
name|parts
index|[
literal|0
index|]
expr_stmt|;
name|thisAddr
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|myHost
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// default to localhost:<solrPort+1001>
name|thisAddr
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|myHost
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|solrPort
argument_list|)
operator|+
literal|1001
argument_list|)
expr_stmt|;
block|}
comment|// first try a straight match by host
name|Long
name|me
init|=
literal|null
decl_stmt|;
name|boolean
name|multiple
init|=
literal|false
decl_stmt|;
name|int
name|port
init|=
literal|0
decl_stmt|;
for|for
control|(
name|QuorumPeer
operator|.
name|QuorumServer
name|server
range|:
name|slist
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|server
operator|.
name|addr
operator|.
name|getHostName
argument_list|()
operator|.
name|equals
argument_list|(
name|myHost
argument_list|)
condition|)
block|{
name|multiple
operator|=
name|me
operator|!=
literal|null
expr_stmt|;
name|me
operator|=
name|server
operator|.
name|id
expr_stmt|;
name|port
operator|=
name|server
operator|.
name|addr
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|multiple
condition|)
block|{
comment|// only one host matched... assume it's me.
name|setClientPort
argument_list|(
name|port
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|me
return|;
block|}
if|if
condition|(
name|me
operator|==
literal|null
condition|)
block|{
comment|// no hosts matched.
return|return
literal|null
return|;
block|}
comment|// multiple matches... try to figure out by port.
for|for
control|(
name|QuorumPeer
operator|.
name|QuorumServer
name|server
range|:
name|slist
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|server
operator|.
name|addr
operator|.
name|equals
argument_list|(
name|thisAddr
argument_list|)
condition|)
block|{
if|if
condition|(
name|clientPortAddress
operator|==
literal|null
operator|||
name|clientPortAddress
operator|.
name|getPort
argument_list|()
operator|<=
literal|0
condition|)
name|setClientPort
argument_list|(
name|server
operator|.
name|addr
operator|.
name|getPort
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|server
operator|.
name|id
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|setDataDir
specifier|public
name|void
name|setDataDir
parameter_list|(
name|String
name|dataDir
parameter_list|)
block|{
name|this
operator|.
name|dataDir
operator|=
name|dataDir
expr_stmt|;
block|}
DECL|method|setClientPort
specifier|public
name|void
name|setClientPort
parameter_list|(
name|int
name|clientPort
parameter_list|)
block|{
if|if
condition|(
name|clientPortAddress
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|this
operator|.
name|clientPortAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
name|clientPortAddress
operator|.
name|getHostName
argument_list|()
argument_list|)
argument_list|,
name|clientPort
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
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
block|}
block|}
else|else
block|{
name|this
operator|.
name|clientPortAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|clientPort
argument_list|)
expr_stmt|;
block|}
block|}
comment|// NOTE: copied from ZooKeeper 3.2
comment|/**    * Parse config from a Properties.    * @param zkProp Properties to parse from.    */
annotation|@
name|Override
DECL|method|parseProperties
specifier|public
name|void
name|parseProperties
parameter_list|(
name|Properties
name|zkProp
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigException
block|{
for|for
control|(
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|zkProp
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"dataDir"
argument_list|)
condition|)
block|{
name|dataDir
operator|=
name|value
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"dataLogDir"
argument_list|)
condition|)
block|{
name|dataLogDir
operator|=
name|value
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"clientPort"
argument_list|)
condition|)
block|{
name|setClientPort
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"tickTime"
argument_list|)
condition|)
block|{
name|tickTime
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"initLimit"
argument_list|)
condition|)
block|{
name|initLimit
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"syncLimit"
argument_list|)
condition|)
block|{
name|syncLimit
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"electionAlg"
argument_list|)
condition|)
block|{
name|electionAlg
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"maxClientCnxns"
argument_list|)
condition|)
block|{
name|maxClientCnxns
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"server."
argument_list|)
condition|)
block|{
name|int
name|dot
init|=
name|key
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
name|long
name|sid
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|key
operator|.
name|substring
argument_list|(
name|dot
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|parts
index|[]
init|=
name|value
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|parts
operator|.
name|length
operator|!=
literal|2
operator|)
operator|&&
operator|(
name|parts
operator|.
name|length
operator|!=
literal|3
operator|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|value
operator|+
literal|" does not have the form host:port or host:port:port"
argument_list|)
expr_stmt|;
block|}
name|InetSocketAddress
name|addr
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|servers
operator|.
name|put
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|sid
argument_list|)
argument_list|,
operator|new
name|QuorumPeer
operator|.
name|QuorumServer
argument_list|(
name|sid
argument_list|,
name|addr
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parts
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|InetSocketAddress
name|electionAddr
init|=
operator|new
name|InetSocketAddress
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|2
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|servers
operator|.
name|put
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|sid
argument_list|)
argument_list|,
operator|new
name|QuorumPeer
operator|.
name|QuorumServer
argument_list|(
name|sid
argument_list|,
name|addr
argument_list|,
name|electionAddr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"group"
argument_list|)
condition|)
block|{
name|int
name|dot
init|=
name|key
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
name|long
name|gid
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|key
operator|.
name|substring
argument_list|(
name|dot
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|numGroups
operator|++
expr_stmt|;
name|String
name|parts
index|[]
init|=
name|value
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|parts
control|)
block|{
name|long
name|sid
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|serverGroup
operator|.
name|containsKey
argument_list|(
name|sid
argument_list|)
condition|)
throw|throw
operator|new
name|ConfigException
argument_list|(
literal|"Server "
operator|+
name|sid
operator|+
literal|"is in multiple groups"
argument_list|)
throw|;
else|else
name|serverGroup
operator|.
name|put
argument_list|(
name|sid
argument_list|,
name|gid
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"weight"
argument_list|)
condition|)
block|{
name|int
name|dot
init|=
name|key
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
name|long
name|sid
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|key
operator|.
name|substring
argument_list|(
name|dot
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|serverWeight
operator|.
name|put
argument_list|(
name|sid
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"zookeeper."
operator|+
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|dataDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"dataDir is not set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|dataLogDir
operator|==
literal|null
condition|)
block|{
name|dataLogDir
operator|=
name|dataDir
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
operator|new
name|File
argument_list|(
name|dataLogDir
argument_list|)
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"dataLogDir "
operator|+
name|dataLogDir
operator|+
literal|" is missing."
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|tickTime
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"tickTime is not set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|servers
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
if|if
condition|(
name|initLimit
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"initLimit is not set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|syncLimit
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"syncLimit is not set"
argument_list|)
throw|;
block|}
comment|/*       * If using FLE, then every server requires a separate election       * port.       */
if|if
condition|(
name|electionAlg
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|QuorumPeer
operator|.
name|QuorumServer
name|s
range|:
name|servers
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|electionAddr
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Missing election port for server: "
operator|+
name|s
operator|.
name|id
argument_list|)
throw|;
block|}
block|}
comment|/*       * Default of quorum config is majority       */
if|if
condition|(
name|serverGroup
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|servers
operator|.
name|size
argument_list|()
operator|!=
name|serverGroup
operator|.
name|size
argument_list|()
condition|)
throw|throw
operator|new
name|ConfigException
argument_list|(
literal|"Every server must be in exactly one group"
argument_list|)
throw|;
comment|/*          * The deafult weight of a server is 1          */
for|for
control|(
name|QuorumPeer
operator|.
name|QuorumServer
name|s
range|:
name|servers
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|serverWeight
operator|.
name|containsKey
argument_list|(
name|s
operator|.
name|id
argument_list|)
condition|)
name|serverWeight
operator|.
name|put
argument_list|(
name|s
operator|.
name|id
argument_list|,
operator|(
name|long
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/*                       * Set the quorumVerifier to be QuorumHierarchical                       */
name|quorumVerifier
operator|=
operator|new
name|QuorumHierarchical
argument_list|(
name|numGroups
argument_list|,
name|serverWeight
argument_list|,
name|serverGroup
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|/*                       * The default QuorumVerifier is QuorumMaj                       */
name|LOG
operator|.
name|info
argument_list|(
literal|"Defaulting to majority quorums"
argument_list|)
expr_stmt|;
name|quorumVerifier
operator|=
operator|new
name|QuorumMaj
argument_list|(
name|servers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|File
name|myIdFile
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"myid"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|myIdFile
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|///////////////// ADDED FOR SOLR //////
name|Long
name|myid
init|=
name|getMyServerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|myid
operator|!=
literal|null
condition|)
block|{
name|serverId
operator|=
name|myid
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|zkRun
operator|==
literal|null
condition|)
return|return;
comment|//////////////// END ADDED FOR SOLR //////
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|myIdFile
operator|.
name|toString
argument_list|()
operator|+
literal|" file is missing"
argument_list|)
throw|;
block|}
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|myIdFile
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|myIdString
decl_stmt|;
try|try
block|{
name|myIdString
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|serverId
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|myIdString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"serverid "
operator|+
name|myIdString
operator|+
literal|" is not a number"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
end_unit
