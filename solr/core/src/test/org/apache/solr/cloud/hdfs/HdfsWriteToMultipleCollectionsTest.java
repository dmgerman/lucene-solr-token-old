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
name|index
operator|.
name|IndexWriter
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
name|store
operator|.
name|NRTCachingDirectory
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
name|Nightly
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
name|embedded
operator|.
name|JettySolrRunner
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
name|CloudSolrServer
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
name|cloud
operator|.
name|StopableIndexingThread
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
name|HdfsDirectoryFactory
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
name|SolrCore
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
name|servlet
operator|.
name|SolrDispatchFilter
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
name|store
operator|.
name|blockcache
operator|.
name|BlockCache
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
name|store
operator|.
name|blockcache
operator|.
name|BlockDirectory
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
name|store
operator|.
name|blockcache
operator|.
name|BlockDirectoryCache
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
name|store
operator|.
name|blockcache
operator|.
name|Cache
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
name|RefCounted
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
name|Nightly
annotation|@
name|ThreadLeakScope
argument_list|(
name|Scope
operator|.
name|NONE
argument_list|)
comment|// hdfs client currently leaks thread(s)
DECL|class|HdfsWriteToMultipleCollectionsTest
specifier|public
class|class
name|HdfsWriteToMultipleCollectionsTest
extends|extends
name|BasicDistributedZkTest
block|{
DECL|field|SOLR_HDFS_HOME
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HDFS_HOME
init|=
literal|"solr.hdfs.home"
decl_stmt|;
DECL|field|SOLR_HDFS_BLOCKCACHE_GLOBAL
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HDFS_BLOCKCACHE_GLOBAL
init|=
literal|"solr.hdfs.blockcache.global"
decl_stmt|;
DECL|field|ACOLLECTION
specifier|private
specifier|static
specifier|final
name|String
name|ACOLLECTION
init|=
literal|"acollection"
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
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
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
name|SOLR_HDFS_HOME
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
name|SOLR_HDFS_HOME
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
DECL|method|HdfsWriteToMultipleCollectionsTest
specifier|public
name|HdfsWriteToMultipleCollectionsTest
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
literal|3
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
name|docCount
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1313
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|cnt
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
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
name|createCollection
argument_list|(
name|ACOLLECTION
operator|+
name|i
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|9
argument_list|)
expr_stmt|;
block|}
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
name|waitForRecoveriesToFinish
argument_list|(
name|ACOLLECTION
operator|+
name|i
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|CloudSolrServer
argument_list|>
name|cloudServers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|StopableIndexingThread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|cnt
condition|;
name|i
operator|++
control|)
block|{
name|CloudSolrServer
name|server
init|=
operator|new
name|CloudSolrServer
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
decl_stmt|;
name|server
operator|.
name|setDefaultCollection
argument_list|(
name|ACOLLECTION
operator|+
name|i
argument_list|)
expr_stmt|;
name|cloudServers
operator|.
name|add
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|StopableIndexingThread
name|indexThread
init|=
operator|new
name|StopableIndexingThread
argument_list|(
literal|null
argument_list|,
name|server
argument_list|,
literal|"1"
argument_list|,
literal|true
argument_list|,
name|docCount
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|indexThread
argument_list|)
expr_stmt|;
name|indexThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|int
name|addCnt
init|=
literal|0
decl_stmt|;
for|for
control|(
name|StopableIndexingThread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
name|addCnt
operator|+=
name|thread
operator|.
name|getNumAdds
argument_list|()
operator|-
name|thread
operator|.
name|getNumDeletes
argument_list|()
expr_stmt|;
block|}
name|long
name|collectionsCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|CloudSolrServer
name|server
range|:
name|cloudServers
control|)
block|{
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|collectionsCount
operator|+=
name|server
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
expr_stmt|;
block|}
for|for
control|(
name|CloudSolrServer
name|server
range|:
name|cloudServers
control|)
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|addCnt
argument_list|,
name|collectionsCount
argument_list|)
expr_stmt|;
name|BlockCache
name|lastBlockCache
init|=
literal|null
decl_stmt|;
comment|// assert that we are using the block directory and that write and read caching are being used
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|CoreContainer
name|cores
init|=
operator|(
operator|(
name|SolrDispatchFilter
operator|)
name|jetty
operator|.
name|getDispatchFilter
argument_list|()
operator|.
name|getFilter
argument_list|()
operator|)
operator|.
name|getCores
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|SolrCore
argument_list|>
name|solrCores
init|=
name|cores
operator|.
name|getCores
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrCore
name|core
range|:
name|solrCores
control|)
block|{
if|if
condition|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|ACOLLECTION
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|instanceof
name|HdfsDirectoryFactory
argument_list|)
expr_stmt|;
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
name|iwRef
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getSolrCoreState
argument_list|()
operator|.
name|getIndexWriter
argument_list|(
name|core
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexWriter
name|iw
init|=
name|iwRef
operator|.
name|get
argument_list|()
decl_stmt|;
name|NRTCachingDirectory
name|directory
init|=
operator|(
name|NRTCachingDirectory
operator|)
name|iw
operator|.
name|getDirectory
argument_list|()
decl_stmt|;
name|BlockDirectory
name|blockDirectory
init|=
operator|(
name|BlockDirectory
operator|)
name|directory
operator|.
name|getDelegate
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|blockDirectory
operator|.
name|isBlockCacheReadEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|blockDirectory
operator|.
name|isBlockCacheWriteEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|Cache
name|cache
init|=
name|blockDirectory
operator|.
name|getCache
argument_list|()
decl_stmt|;
comment|// we know its a BlockDirectoryCache, but future proof
name|assertTrue
argument_list|(
name|cache
operator|instanceof
name|BlockDirectoryCache
argument_list|)
expr_stmt|;
name|BlockCache
name|blockCache
init|=
operator|(
operator|(
name|BlockDirectoryCache
operator|)
name|cache
operator|)
operator|.
name|getBlockCache
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastBlockCache
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|SOLR_HDFS_BLOCKCACHE_GLOBAL
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|lastBlockCache
argument_list|,
name|blockCache
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotSame
argument_list|(
name|lastBlockCache
argument_list|,
name|blockCache
argument_list|)
expr_stmt|;
block|}
block|}
name|lastBlockCache
operator|=
name|blockCache
expr_stmt|;
block|}
finally|finally
block|{
name|iwRef
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class
end_unit