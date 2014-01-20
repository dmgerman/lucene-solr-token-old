begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.cloud.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|hdfs
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
name|net
operator|.
name|URI
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|MiniDFSCluster
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|SolrQuery
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
name|impl
operator|.
name|HttpSolrServer
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
name|cloud
operator|.
name|BasicDistributedZkTest
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
name|CollectionParams
operator|.
name|CollectionAction
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
name|NamedList
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
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
operator|.
name|Scope
import|;
end_import
begin_class
annotation|@
name|Slow
annotation|@
name|ThreadLeakScope
argument_list|(
name|Scope
operator|.
name|NONE
argument_list|)
comment|// hdfs client currently leaks thread(s)
DECL|class|StressHdfsTest
specifier|public
class|class
name|StressHdfsTest
extends|extends
name|BasicDistributedZkTest
block|{
DECL|field|DELETE_DATA_DIR_COLLECTION
specifier|private
specifier|static
specifier|final
name|String
name|DELETE_DATA_DIR_COLLECTION
init|=
literal|"delete_data_dir"
decl_stmt|;
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupClass
specifier|public
specifier|static
name|void
name|setupClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dfsCluster
operator|=
name|HdfsTestUtil
operator|.
name|setupClass
argument_list|(
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|HdfsBasicDistributedZk2Test
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"_"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|,
name|dfsCluster
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/solr"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownClass
specifier|public
specifier|static
name|void
name|teardownClass
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsTestUtil
operator|.
name|teardownClass
argument_list|(
name|dfsCluster
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDataDir
specifier|protected
name|String
name|getDataDir
parameter_list|(
name|String
name|dataDir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|HdfsTestUtil
operator|.
name|getDataDir
argument_list|(
name|dfsCluster
argument_list|,
name|dataDir
argument_list|)
return|;
block|}
DECL|method|StressHdfsTest
specifier|public
name|StressHdfsTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|1
expr_stmt|;
name|shardCount
operator|=
name|TEST_NIGHTLY
condition|?
literal|13
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|+
literal|1
expr_stmt|;
block|}
DECL|method|getSolrXml
specifier|protected
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"solr-no-core.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|cnt
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
operator|+
literal|1
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
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|createAndDeleteCollection
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createAndDeleteCollection
specifier|private
name|void
name|createAndDeleteCollection
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
throws|,
name|Exception
throws|,
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|URISyntaxException
block|{
name|boolean
name|overshard
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|int
name|rep
decl_stmt|;
name|int
name|nShards
decl_stmt|;
name|int
name|maxReplicasPerNode
decl_stmt|;
if|if
condition|(
name|overshard
condition|)
block|{
name|nShards
operator|=
name|shardCount
operator|*
literal|2
expr_stmt|;
name|maxReplicasPerNode
operator|=
literal|8
expr_stmt|;
name|rep
operator|=
literal|2
expr_stmt|;
block|}
else|else
block|{
name|nShards
operator|=
name|shardCount
operator|/
literal|2
expr_stmt|;
name|maxReplicasPerNode
operator|=
literal|1
expr_stmt|;
name|rep
operator|=
literal|2
expr_stmt|;
if|if
condition|(
name|nShards
operator|==
literal|0
condition|)
name|nShards
operator|=
literal|1
expr_stmt|;
block|}
name|createCollection
argument_list|(
name|DELETE_DATA_DIR_COLLECTION
argument_list|,
name|nShards
argument_list|,
name|rep
argument_list|,
name|maxReplicasPerNode
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|DELETE_DATA_DIR_COLLECTION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|DELETE_DATA_DIR_COLLECTION
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|nShards
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|DELETE_DATA_DIR_COLLECTION
argument_list|,
literal|"shard"
operator|+
name|i
argument_list|,
literal|15000
argument_list|)
expr_stmt|;
block|}
comment|// collect the data dirs
name|List
argument_list|<
name|String
argument_list|>
name|dataDirs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SolrServer
name|client
range|:
name|clients
control|)
block|{
name|HttpSolrServer
name|c
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|getBaseUrl
argument_list|(
name|client
argument_list|)
operator|+
literal|"/delete_data_dir"
argument_list|)
decl_stmt|;
try|try
block|{
name|c
operator|.
name|add
argument_list|(
name|getDoc
argument_list|(
literal|"id"
argument_list|,
name|i
operator|++
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
name|c
operator|.
name|add
argument_list|(
name|getDoc
argument_list|(
literal|"id"
argument_list|,
name|i
operator|++
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
name|c
operator|.
name|add
argument_list|(
name|getDoc
argument_list|(
literal|"id"
argument_list|,
name|i
operator|++
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|c
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|c
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|c
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|setConnectionTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
init|=
name|c
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|()
operator|.
name|setRequestHandler
argument_list|(
literal|"/admin/system"
argument_list|)
argument_list|)
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|coreInfo
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"core"
argument_list|)
decl_stmt|;
name|String
name|dataDir
init|=
call|(
name|String
call|)
argument_list|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|coreInfo
operator|.
name|get
argument_list|(
literal|"directory"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"data"
argument_list|)
decl_stmt|;
name|dataDirs
operator|.
name|add
argument_list|(
name|dataDir
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|c
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|cloudClient
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cloudClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"action"
argument_list|,
name|CollectionAction
operator|.
name|DELETE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"name"
argument_list|,
name|DELETE_DATA_DIR_COLLECTION
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/collections"
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// check that all dirs are gone
for|for
control|(
name|String
name|dataDir
range|:
name|dataDirs
control|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
operator|new
name|URI
argument_list|(
name|dataDir
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Data directory exists after collection removal : "
operator|+
name|dataDir
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|dataDir
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
