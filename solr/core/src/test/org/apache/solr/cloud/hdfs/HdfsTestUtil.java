begin_unit
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
name|util
operator|.
name|Locale
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
name|Timer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
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
name|ConcurrentHashMap
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
name|FSDataOutputStream
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
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNodeAdapter
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
name|SolrTestCaseJ4
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
name|IOUtils
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|HdfsTestUtil
specifier|public
class|class
name|HdfsTestUtil
block|{
DECL|field|savedLocale
specifier|private
specifier|static
name|Locale
name|savedLocale
decl_stmt|;
DECL|field|timers
specifier|private
specifier|static
name|Map
argument_list|<
name|MiniDFSCluster
argument_list|,
name|Timer
argument_list|>
name|timers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|badTlogOutStream
specifier|private
specifier|static
name|FSDataOutputStream
name|badTlogOutStream
decl_stmt|;
DECL|method|setupClass
specifier|public
specifier|static
name|MiniDFSCluster
name|setupClass
parameter_list|(
name|String
name|dir
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|setupClass
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|setupClass
specifier|public
specifier|static
name|MiniDFSCluster
name|setupClass
parameter_list|(
name|String
name|dir
parameter_list|,
name|boolean
name|safeModeTesting
parameter_list|)
throws|throws
name|Exception
block|{
name|LuceneTestCase
operator|.
name|assumeFalse
argument_list|(
literal|"HDFS tests were disabled by -Dtests.disableHdfs"
argument_list|,
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.disableHdfs"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|savedLocale
operator|=
name|Locale
operator|.
name|getDefault
argument_list|()
expr_stmt|;
comment|// TODO: we HACK around HADOOP-9643
name|Locale
operator|.
name|setDefault
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
name|int
name|dataNodes
init|=
literal|2
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.block.access.token.enable"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.permissions.enabled"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.security.authentication"
argument_list|,
literal|"simple"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hdfs.minidfs.basedir"
argument_list|,
name|dir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"hdfsBaseDir"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.namenode.name.dir"
argument_list|,
name|dir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"nameNodeNameDir"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"fs.hdfs.impl.disable.cache"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"test.build.data"
argument_list|,
name|dir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"hdfs"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"build"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"test.cache.data"
argument_list|,
name|dir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"hdfs"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"cache"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.lock.type"
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.hdfs.blockcache.global"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|LuceneTestCase
operator|.
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|MiniDFSCluster
name|dfsCluster
init|=
operator|new
name|MiniDFSCluster
argument_list|(
name|conf
argument_list|,
name|dataNodes
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|dfsCluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|,
name|getDataDir
argument_list|(
name|dfsCluster
argument_list|,
literal|"solr_hdfs_home"
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|rndMode
init|=
name|LuceneTestCase
operator|.
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|safeModeTesting
operator|&&
name|rndMode
operator|>
literal|4
condition|)
block|{
name|NameNodeAdapter
operator|.
name|enterSafeMode
argument_list|(
name|dfsCluster
operator|.
name|getNameNode
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|int
name|rnd
init|=
name|LuceneTestCase
operator|.
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|Timer
name|timer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
name|timer
operator|.
name|schedule
argument_list|(
operator|new
name|TimerTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|NameNodeAdapter
operator|.
name|leaveSafeMode
argument_list|(
name|dfsCluster
operator|.
name|getNameNode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
name|timers
operator|.
name|put
argument_list|(
name|dfsCluster
argument_list|,
name|timer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// force a lease recovery by creating a tlog file and not closing it
name|URI
name|uri
init|=
name|dfsCluster
operator|.
name|getURI
argument_list|()
decl_stmt|;
name|Path
name|hdfsDirPath
init|=
operator|new
name|Path
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
operator|+
literal|"/solr/collection1/core_node1/data/tlog/tlog.0000000000000000000"
argument_list|)
decl_stmt|;
comment|// tran log already being created testing
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
name|hdfsDirPath
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|badTlogOutStream
operator|=
name|fs
operator|.
name|create
argument_list|(
name|hdfsDirPath
argument_list|)
expr_stmt|;
block|}
name|SolrTestCaseJ4
operator|.
name|useFactory
argument_list|(
literal|"org.apache.solr.core.HdfsDirectoryFactory"
argument_list|)
expr_stmt|;
return|return
name|dfsCluster
return|;
block|}
DECL|method|teardownClass
specifier|public
specifier|static
name|void
name|teardownClass
parameter_list|(
name|MiniDFSCluster
name|dfsCluster
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|badTlogOutStream
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|badTlogOutStream
argument_list|)
expr_stmt|;
block|}
name|SolrTestCaseJ4
operator|.
name|resetFactory
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.lock.type"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"test.build.data"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"test.cache.data"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.hdfs.blockcache.global"
argument_list|)
expr_stmt|;
if|if
condition|(
name|dfsCluster
operator|!=
literal|null
condition|)
block|{
name|timers
operator|.
name|remove
argument_list|(
name|dfsCluster
argument_list|)
expr_stmt|;
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// TODO: we HACK around HADOOP-9643
if|if
condition|(
name|savedLocale
operator|!=
literal|null
condition|)
block|{
name|Locale
operator|.
name|setDefault
argument_list|(
name|savedLocale
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDataDir
specifier|public
specifier|static
name|String
name|getDataDir
parameter_list|(
name|MiniDFSCluster
name|dfsCluster
parameter_list|,
name|String
name|dataDir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dataDir
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|URI
name|uri
init|=
name|dfsCluster
operator|.
name|getURI
argument_list|()
decl_stmt|;
name|String
name|dir
init|=
name|uri
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
operator|new
name|File
argument_list|(
name|dataDir
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"/"
argument_list|,
literal|"_"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|" "
argument_list|,
literal|"_"
argument_list|)
decl_stmt|;
return|return
name|dir
return|;
block|}
block|}
end_class
end_unit
