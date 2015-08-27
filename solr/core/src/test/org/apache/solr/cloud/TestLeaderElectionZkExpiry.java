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
name|core
operator|.
name|CloudConfig
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
name|Test
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
name|TimeUnit
import|;
end_import
begin_class
DECL|class|TestLeaderElectionZkExpiry
specifier|public
class|class
name|TestLeaderElectionZkExpiry
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|SOLRXML
specifier|public
specifier|static
specifier|final
name|String
name|SOLRXML
init|=
literal|"<solr></solr>"
decl_stmt|;
DECL|field|MAX_NODES
specifier|private
specifier|static
specifier|final
name|int
name|MAX_NODES
init|=
literal|16
decl_stmt|;
DECL|field|MIN_NODES
specifier|private
specifier|static
specifier|final
name|int
name|MIN_NODES
init|=
literal|4
decl_stmt|;
annotation|@
name|Test
DECL|method|testLeaderElectionWithZkExpiry
specifier|public
name|void
name|testLeaderElectionWithZkExpiry
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|String
name|ccDir
init|=
name|createTempDir
argument_list|(
literal|"testLeaderElectionWithZkExpiry-solr"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|CoreContainer
name|cc
init|=
name|createCoreContainer
argument_list|(
name|ccDir
argument_list|,
name|SOLRXML
argument_list|)
decl_stmt|;
specifier|final
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
name|server
operator|.
name|setTheTickTime
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|CloudConfig
name|cloudConfig
init|=
operator|new
name|CloudConfig
operator|.
name|CloudConfigBuilder
argument_list|(
literal|"dummy.host.com"
argument_list|,
literal|8984
argument_list|,
literal|"solr"
argument_list|)
operator|.
name|setLeaderConflictResolveWait
argument_list|(
literal|180000
argument_list|)
operator|.
name|setLeaderVoteWait
argument_list|(
literal|180000
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|ZkController
name|zkController
init|=
operator|new
name|ZkController
argument_list|(
name|cc
argument_list|,
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
literal|15000
argument_list|,
name|cloudConfig
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
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|Thread
name|killer
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
name|long
name|timeout
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
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
condition|)
block|{
name|long
name|sessionId
init|=
name|zkController
operator|.
name|getZkClient
argument_list|()
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|getSessionId
argument_list|()
decl_stmt|;
name|server
operator|.
name|expire
argument_list|(
name|sessionId
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
block|}
block|}
decl_stmt|;
name|killer
operator|.
name|start
argument_list|()
expr_stmt|;
name|killer
operator|.
name|join
argument_list|()
expr_stmt|;
name|long
name|timeout
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
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|zc
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|LeaderElectionTest
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
condition|)
block|{
try|try
block|{
name|String
name|leaderNode
init|=
name|OverseerCollectionConfigSetProcessor
operator|.
name|getLeaderNode
argument_list|(
name|zc
argument_list|)
decl_stmt|;
if|if
condition|(
name|leaderNode
operator|!=
literal|null
operator|&&
operator|!
name|leaderNode
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Time={} Overseer leader is = {}"
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
name|leaderNode
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|nne
parameter_list|)
block|{
comment|// ignore
block|}
block|}
name|assertTrue
argument_list|(
name|found
argument_list|)
expr_stmt|;
block|}
finally|finally
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
if|if
condition|(
name|zc
operator|!=
literal|null
condition|)
name|zc
operator|.
name|close
argument_list|()
expr_stmt|;
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
