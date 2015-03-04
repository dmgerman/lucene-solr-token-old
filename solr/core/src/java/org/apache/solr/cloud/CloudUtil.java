begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|core
operator|.
name|CoreContainer
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
name|CoreDescriptor
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
name|SolrResourceLoader
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
begin_class
DECL|class|CloudUtil
specifier|public
class|class
name|CloudUtil
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
name|CloudUtil
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * See if coreNodeName has been taken over by another baseUrl and unload core    * + throw exception if it has been.    */
DECL|method|checkSharedFSFailoverReplaced
specifier|public
specifier|static
name|void
name|checkSharedFSFailoverReplaced
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|desc
parameter_list|)
block|{
name|ZkController
name|zkController
init|=
name|cc
operator|.
name|getZkController
argument_list|()
decl_stmt|;
name|String
name|thisCnn
init|=
name|zkController
operator|.
name|getCoreNodeName
argument_list|(
name|desc
argument_list|)
decl_stmt|;
name|String
name|thisBaseUrl
init|=
name|zkController
operator|.
name|getBaseUrl
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"checkSharedFSFailoverReplaced running for coreNodeName={} baseUrl={}"
argument_list|,
name|thisCnn
argument_list|,
name|thisBaseUrl
argument_list|)
expr_stmt|;
comment|// if we see our core node name on a different base url, unload
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slicesMap
init|=
name|zkController
operator|.
name|getClusterState
argument_list|()
operator|.
name|getSlicesMap
argument_list|(
name|desc
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|slicesMap
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Slice
name|slice
range|:
name|slicesMap
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|String
name|cnn
init|=
name|replica
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|baseUrl
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"compare against coreNodeName={} baseUrl={}"
argument_list|,
name|cnn
argument_list|,
name|baseUrl
argument_list|)
expr_stmt|;
if|if
condition|(
name|thisCnn
operator|!=
literal|null
operator|&&
name|thisCnn
operator|.
name|equals
argument_list|(
name|cnn
argument_list|)
operator|&&
operator|!
name|thisBaseUrl
operator|.
name|equals
argument_list|(
name|baseUrl
argument_list|)
condition|)
block|{
if|if
condition|(
name|cc
operator|.
name|getCoreNames
argument_list|()
operator|.
name|contains
argument_list|(
name|desc
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|cc
operator|.
name|unload
argument_list|(
name|desc
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|File
name|instanceDir
init|=
operator|new
name|File
argument_list|(
name|desc
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|instanceDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Failed to delete instance dir for core:"
operator|+
name|desc
operator|.
name|getName
argument_list|()
operator|+
literal|" dir:"
operator|+
name|instanceDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Will not load SolrCore "
operator|+
name|desc
operator|.
name|getName
argument_list|()
operator|+
literal|" because it has been replaced due to failover."
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Will not load SolrCore "
operator|+
name|desc
operator|.
name|getName
argument_list|()
operator|+
literal|" because it has been replaced due to failover."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Returns a displayable unified path to the given resource. For non-solrCloud that will be the    * same as getConfigDir, but for Cloud it will be getConfigSetZkPath ending in a /    *<p>    *<b>Note:</b> Do not use this to generate a valid file path, but for debug printing etc    * @param loader Resource loader instance    * @return a String of path to resource    */
DECL|method|unifiedResourcePath
specifier|public
specifier|static
name|String
name|unifiedResourcePath
parameter_list|(
name|SolrResourceLoader
name|loader
parameter_list|)
block|{
return|return
operator|(
name|loader
operator|instanceof
name|ZkSolrResourceLoader
operator|)
condition|?
operator|(
operator|(
name|ZkSolrResourceLoader
operator|)
name|loader
operator|)
operator|.
name|getConfigSetZkPath
argument_list|()
operator|+
literal|"/"
else|:
name|loader
operator|.
name|getConfigDir
argument_list|()
return|;
block|}
comment|/**Read the list of public keys from ZK    */
DECL|method|getTrustedKeys
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|getTrustedKeys
parameter_list|(
name|SolrZkClient
name|zk
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|zk
operator|.
name|getChildren
argument_list|(
literal|"/keys"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|children
control|)
block|{
name|result
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|zk
operator|.
name|getData
argument_list|(
literal|"/keys/"
operator|+
name|key
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error fetching key names"
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|EMPTY_MAP
return|;
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
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unable to read crypto keys"
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
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unable to read crypto keys"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
