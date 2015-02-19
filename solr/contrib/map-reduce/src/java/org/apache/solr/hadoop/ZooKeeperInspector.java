begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
package|;
end_package
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
name|Collection
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
name|Comparator
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|Aliases
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
name|ClusterState
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
name|DocCollection
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
name|SolrZkClient
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Files
import|;
end_import
begin_comment
comment|/**  * Extracts SolrCloud information from ZooKeeper.  */
end_comment
begin_class
DECL|class|ZooKeeperInspector
specifier|final
class|class
name|ZooKeeperInspector
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
name|ZooKeeperInspector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|extractShardUrls
specifier|public
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|extractShardUrls
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|)
block|{
name|DocCollection
name|docCollection
init|=
name|extractDocCollection
argument_list|(
name|zkHost
argument_list|,
name|collection
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|getSortedSlices
argument_list|(
name|docCollection
operator|.
name|getSlices
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|solrUrls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|slices
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
if|if
condition|(
name|slice
operator|.
name|getLeader
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot find SolrCloud slice leader. "
operator|+
literal|"It looks like not all of your shards are registered in ZooKeeper yet"
argument_list|)
throw|;
block|}
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|replicas
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
name|ZkCoreNodeProps
name|props
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|replica
argument_list|)
decl_stmt|;
name|urls
operator|.
name|add
argument_list|(
name|props
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|solrUrls
operator|.
name|add
argument_list|(
name|urls
argument_list|)
expr_stmt|;
block|}
return|return
name|solrUrls
return|;
block|}
DECL|method|extractDocCollection
specifier|public
name|DocCollection
name|extractDocCollection
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|String
name|collection
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"collection must not be null"
argument_list|)
throw|;
block|}
name|SolrZkClient
name|zkClient
init|=
name|getZkClient
argument_list|(
name|zkHost
argument_list|)
decl_stmt|;
try|try
block|{
name|ZkStateReader
name|zkStateReader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
try|try
block|{
comment|// first check for alias
name|collection
operator|=
name|checkForAlias
argument_list|(
name|zkClient
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|zkStateReader
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot find expected information for SolrCloud in ZooKeeper: "
operator|+
name|zkHost
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|collection
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot find collection '"
operator|+
name|collection
operator|+
literal|"' in ZooKeeper: "
operator|+
name|zkHost
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getZkClient
specifier|public
name|SolrZkClient
name|getZkClient
parameter_list|(
name|String
name|zkHost
parameter_list|)
block|{
if|if
condition|(
name|zkHost
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"zkHost must not be null"
argument_list|)
throw|;
block|}
name|SolrZkClient
name|zkClient
decl_stmt|;
try|try
block|{
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkHost
argument_list|,
literal|30000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot connect to ZooKeeper: "
operator|+
name|zkHost
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|zkClient
return|;
block|}
DECL|method|getSortedSlices
specifier|public
name|List
argument_list|<
name|Slice
argument_list|>
name|getSortedSlices
parameter_list|(
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
parameter_list|)
block|{
name|List
argument_list|<
name|Slice
argument_list|>
name|sorted
init|=
operator|new
name|ArrayList
argument_list|(
name|slices
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|sorted
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Slice
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Slice
name|slice1
parameter_list|,
name|Slice
name|slice2
parameter_list|)
block|{
name|Comparator
name|c
init|=
operator|new
name|AlphaNumericComparator
argument_list|()
decl_stmt|;
return|return
name|c
operator|.
name|compare
argument_list|(
name|slice1
operator|.
name|getName
argument_list|()
argument_list|,
name|slice2
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Sorted slices: {}"
argument_list|,
name|sorted
argument_list|)
expr_stmt|;
return|return
name|sorted
return|;
block|}
comment|/**    * Returns config value given collection name    * Borrowed heavily from Solr's ZKController.    */
DECL|method|readConfigName
specifier|public
name|String
name|readConfigName
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"collection must not be null"
argument_list|)
throw|;
block|}
name|String
name|configName
init|=
literal|null
decl_stmt|;
comment|// first check for alias
name|collection
operator|=
name|checkForAlias
argument_list|(
name|zkClient
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|String
name|path
init|=
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/"
operator|+
name|collection
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Load collection config from:"
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|ZkNodeProps
name|props
init|=
name|ZkNodeProps
operator|.
name|load
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|configName
operator|=
name|props
operator|.
name|getStr
argument_list|(
name|ZkController
operator|.
name|CONFIGNAME_PROP
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|configName
operator|!=
literal|null
operator|&&
operator|!
name|zkClient
operator|.
name|exists
argument_list|(
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|configName
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Specified config does not exist in ZooKeeper:"
operator|+
name|configName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Specified config does not exist in ZooKeeper:"
operator|+
name|configName
argument_list|)
throw|;
block|}
return|return
name|configName
return|;
block|}
DECL|method|checkForAlias
specifier|private
name|String
name|checkForAlias
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|byte
index|[]
name|aliasData
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|ZkStateReader
operator|.
name|ALIASES
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Aliases
name|aliases
init|=
name|ClusterState
operator|.
name|load
argument_list|(
name|aliasData
argument_list|)
decl_stmt|;
name|String
name|alias
init|=
name|aliases
operator|.
name|getCollectionAlias
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|alias
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|aliasList
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|alias
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|aliasList
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"collection cannot be an alias that maps to multiple collections"
argument_list|)
throw|;
block|}
name|collection
operator|=
name|aliasList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|collection
return|;
block|}
comment|/**    * Download and return the config directory from ZK    */
DECL|method|downloadConfigDir
specifier|public
name|File
name|downloadConfigDir
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|configName
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|KeeperException
block|{
name|File
name|dir
init|=
name|Files
operator|.
name|createTempDir
argument_list|()
decl_stmt|;
name|dir
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|ZkController
operator|.
name|downloadConfigDir
argument_list|(
name|zkClient
argument_list|,
name|configName
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|File
name|confDir
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|confDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
comment|// create a temporary directory with "conf" subdir and mv the config in there.  This is
comment|// necessary because of CDH-11188; solrctl does not generate nor accept directories with e.g.
comment|// conf/solrconfig.xml which is necessary for proper solr operation.  This should work
comment|// even if solrctl changes.
name|confDir
operator|=
operator|new
name|File
argument_list|(
name|Files
operator|.
name|createTempDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
name|confDir
operator|.
name|getParentFile
argument_list|()
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|Files
operator|.
name|move
argument_list|(
name|dir
argument_list|,
name|confDir
argument_list|)
expr_stmt|;
name|dir
operator|=
name|confDir
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|,
literal|"<solr></solr>"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|verifyConfigDir
argument_list|(
name|confDir
argument_list|)
expr_stmt|;
return|return
name|dir
return|;
block|}
DECL|method|verifyConfigDir
specifier|private
name|void
name|verifyConfigDir
parameter_list|(
name|File
name|confDir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|solrConfigFile
init|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|solrConfigFile
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Detected invalid Solr config dir in ZooKeeper - Reason: File not found: "
operator|+
name|solrConfigFile
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|solrConfigFile
operator|.
name|isFile
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Detected invalid Solr config dir in ZooKeeper - Reason: Not a file: "
operator|+
name|solrConfigFile
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|solrConfigFile
operator|.
name|canRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Insufficient permissions to read file: "
operator|+
name|solrConfigFile
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
