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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|CountDownLatch
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
name|Future
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
name|CloudSolrClient
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
name|CollectionAdminRequest
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
name|SolrCloudTestCase
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
name|Before
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
name|org
operator|.
name|junit
operator|.
name|Test
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
DECL|class|TestCollectionStateWatchers
specifier|public
class|class
name|TestCollectionStateWatchers
extends|extends
name|SolrCloudTestCase
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
DECL|field|CLUSTER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|CLUSTER_SIZE
init|=
literal|4
decl_stmt|;
DECL|field|executor
specifier|private
specifier|static
specifier|final
name|ExecutorService
name|executor
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareCachedThreadPool
argument_list|(
literal|"backgroundWatchers"
argument_list|)
decl_stmt|;
DECL|field|MAX_WAIT_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|MAX_WAIT_TIMEOUT
init|=
literal|30
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|startCluster
specifier|public
specifier|static
name|void
name|startCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCluster
argument_list|(
name|CLUSTER_SIZE
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"config"
argument_list|,
name|getFile
argument_list|(
literal|"solrj/solr/collection1/conf"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdownBackgroundExecutors
specifier|public
specifier|static
name|void
name|shutdownBackgroundExecutors
parameter_list|()
block|{
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|prepareCluster
specifier|public
name|void
name|prepareCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|missingServers
init|=
name|CLUSTER_SIZE
operator|-
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
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
name|missingServers
condition|;
name|i
operator|++
control|)
block|{
name|cluster
operator|.
name|startJettySolrRunner
argument_list|()
expr_stmt|;
block|}
name|cluster
operator|.
name|waitForAllNodes
argument_list|(
literal|30
argument_list|)
expr_stmt|;
block|}
DECL|method|waitInBackground
specifier|private
specifier|static
name|Future
argument_list|<
name|Boolean
argument_list|>
name|waitInBackground
parameter_list|(
name|String
name|collection
parameter_list|,
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|CollectionStatePredicate
name|predicate
parameter_list|)
block|{
return|return
name|executor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|waitForState
argument_list|(
name|collection
argument_list|,
name|timeout
argument_list|,
name|unit
argument_list|,
name|predicate
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|TimeoutException
name|e
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testSimpleCollectionWatch
specifier|public
name|void
name|testSimpleCollectionWatch
parameter_list|()
throws|throws
name|Exception
block|{
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"testcollection"
argument_list|,
literal|"config"
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|)
operator|.
name|processAndWait
argument_list|(
name|client
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|)
expr_stmt|;
name|client
operator|.
name|waitForState
argument_list|(
literal|"testcollection"
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|DocCollection
operator|.
name|isFullyActive
argument_list|(
name|n
argument_list|,
name|c
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// shutdown a node and check that we get notified about the change
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|client
operator|.
name|registerCollectionStateWatcher
argument_list|(
literal|"testcollection"
argument_list|,
parameter_list|(
name|liveNodes
parameter_list|,
name|collectionState
parameter_list|)
lambda|->
block|{
name|int
name|nodeCount
init|=
literal|0
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"State changed: {}"
argument_list|,
name|collectionState
argument_list|)
expr_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|collectionState
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
control|)
block|{
if|if
condition|(
name|replica
operator|.
name|isActive
argument_list|(
name|liveNodes
argument_list|)
condition|)
name|nodeCount
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nodeCount
operator|==
literal|3
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stopJettySolrRunner
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CollectionStateWatcher was never notified of cluster change"
argument_list|,
name|latch
operator|.
name|await
argument_list|(
name|MAX_WAIT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|CollectionStateWatcher
argument_list|>
name|watchers
init|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getStateWatchers
argument_list|(
literal|"testcollection"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"CollectionStateWatcher wasn't cleared after completion"
argument_list|,
name|watchers
operator|==
literal|null
operator|||
name|watchers
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStateWatcherChecksCurrentStateOnRegister
specifier|public
name|void
name|testStateWatcherChecksCurrentStateOnRegister
parameter_list|()
throws|throws
name|Exception
block|{
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"currentstate"
argument_list|,
literal|"config"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
operator|.
name|processAndWait
argument_list|(
name|client
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|client
operator|.
name|registerCollectionStateWatcher
argument_list|(
literal|"currentstate"
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CollectionStateWatcher isn't called on new registration"
argument_list|,
name|latch
operator|.
name|await
argument_list|(
name|MAX_WAIT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"CollectionStateWatcher should be retained"
argument_list|,
literal|1
argument_list|,
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getStateWatchers
argument_list|(
literal|"currentstate"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|latch2
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|client
operator|.
name|registerCollectionStateWatcher
argument_list|(
literal|"currentstate"
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
block|{
name|latch2
operator|.
name|countDown
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CollectionStateWatcher isn't called when registering for already-watched collection"
argument_list|,
name|latch
operator|.
name|await
argument_list|(
name|MAX_WAIT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"CollectionStateWatcher should be removed"
argument_list|,
literal|1
argument_list|,
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getStateWatchers
argument_list|(
literal|"currentstate"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWaitForStateChecksCurrentState
specifier|public
name|void
name|testWaitForStateChecksCurrentState
parameter_list|()
throws|throws
name|Exception
block|{
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"waitforstate"
argument_list|,
literal|"config"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
operator|.
name|processAndWait
argument_list|(
name|client
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|)
expr_stmt|;
name|client
operator|.
name|waitForState
argument_list|(
literal|"waitforstate"
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|DocCollection
operator|.
name|isFullyActive
argument_list|(
name|n
argument_list|,
name|c
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// several goes, to check that we're not getting delayed state changes
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|client
operator|.
name|waitForState
argument_list|(
literal|"waitforstate"
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|DocCollection
operator|.
name|isFullyActive
argument_list|(
name|n
argument_list|,
name|c
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"waitForState should return immediately if the predicate is already satisfied"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testCanWaitForNonexistantCollection
specifier|public
name|void
name|testCanWaitForNonexistantCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|Future
argument_list|<
name|Boolean
argument_list|>
name|future
init|=
name|waitInBackground
argument_list|(
literal|"delayed"
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|DocCollection
operator|.
name|isFullyActive
argument_list|(
name|n
argument_list|,
name|c
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"delayed"
argument_list|,
literal|"config"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
operator|.
name|processAndWait
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"waitForState was not triggered by collection creation"
argument_list|,
name|future
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPredicateFailureTimesOut
specifier|public
name|void
name|testPredicateFailureTimesOut
parameter_list|()
throws|throws
name|Exception
block|{
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|expectThrows
argument_list|(
name|TimeoutException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|client
operator|.
name|waitForState
argument_list|(
literal|"nosuchcollection"
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|(
parameter_list|(
name|liveNodes
parameter_list|,
name|collectionState
parameter_list|)
lambda|->
literal|false
operator|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|CollectionStateWatcher
argument_list|>
name|watchers
init|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getStateWatchers
argument_list|(
literal|"nosuchcollection"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Watchers for collection should be removed after timeout"
argument_list|,
name|watchers
operator|==
literal|null
operator|||
name|watchers
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWaitForStateWatcherIsRetainedOnPredicateFailure
specifier|public
name|void
name|testWaitForStateWatcherIsRetainedOnPredicateFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"falsepredicate"
argument_list|,
literal|"config"
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|)
operator|.
name|processAndWait
argument_list|(
name|client
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|)
expr_stmt|;
name|client
operator|.
name|waitForState
argument_list|(
literal|"falsepredicate"
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|DocCollection
operator|.
name|isFullyActive
argument_list|(
name|n
argument_list|,
name|c
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|firstCall
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// stop a node, then add a watch waiting for all nodes to be back up
name|JettySolrRunner
name|node1
init|=
name|cluster
operator|.
name|stopJettySolrRunner
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Future
argument_list|<
name|Boolean
argument_list|>
name|future
init|=
name|waitInBackground
argument_list|(
literal|"falsepredicate"
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|liveNodes
parameter_list|,
name|collectionState
parameter_list|)
lambda|->
block|{
name|firstCall
operator|.
name|countDown
argument_list|()
expr_stmt|;
return|return
name|DocCollection
operator|.
name|isFullyActive
argument_list|(
name|liveNodes
argument_list|,
name|collectionState
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|)
return|;
block|}
argument_list|)
decl_stmt|;
comment|// first, stop another node; the watch should not be fired after this!
name|JettySolrRunner
name|node2
init|=
name|cluster
operator|.
name|stopJettySolrRunner
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// now start them both back up
name|cluster
operator|.
name|startJettySolrRunner
argument_list|(
name|node1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CollectionStateWatcher not called after 30 seconds"
argument_list|,
name|firstCall
operator|.
name|await
argument_list|(
name|MAX_WAIT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|startJettySolrRunner
argument_list|(
name|node2
argument_list|)
expr_stmt|;
name|Boolean
name|result
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Did not see a fully active cluster after 30 seconds"
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWatcherIsRemovedAfterTimeout
specifier|public
name|void
name|testWatcherIsRemovedAfterTimeout
parameter_list|()
block|{
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"There should be no watchers for a non-existent collection!"
argument_list|,
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getStateWatchers
argument_list|(
literal|"no-such-collection"
argument_list|)
operator|==
literal|null
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|TimeoutException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|client
operator|.
name|waitForState
argument_list|(
literal|"no-such-collection"
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|DocCollection
operator|.
name|isFullyActive
argument_list|(
name|n
argument_list|,
name|c
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|CollectionStateWatcher
argument_list|>
name|watchers
init|=
name|client
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getStateWatchers
argument_list|(
literal|"no-such-collection"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Watchers for collection should be removed after timeout"
argument_list|,
name|watchers
operator|==
literal|null
operator|||
name|watchers
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeletionsTriggerWatches
specifier|public
name|void
name|testDeletionsTriggerWatches
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|createCollection
argument_list|(
literal|"tobedeleted"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|"config"
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|Future
argument_list|<
name|Boolean
argument_list|>
name|future
init|=
name|waitInBackground
argument_list|(
literal|"tobedeleted"
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|l
parameter_list|,
name|c
parameter_list|)
lambda|->
name|c
operator|==
literal|null
argument_list|)
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteCollection
argument_list|(
literal|"tobedeleted"
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CollectionStateWatcher not notified of delete call after 30 seconds"
argument_list|,
name|future
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWatchesWorkForStateFormat1
specifier|public
name|void
name|testWatchesWorkForStateFormat1
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|Boolean
argument_list|>
name|future
init|=
name|waitInBackground
argument_list|(
literal|"stateformat1"
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|DocCollection
operator|.
name|isFullyActive
argument_list|(
name|n
argument_list|,
name|c
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"stateformat1"
argument_list|,
literal|"config"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
operator|.
name|setStateFormat
argument_list|(
literal|1
argument_list|)
operator|.
name|processAndWait
argument_list|(
name|client
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CollectionStateWatcher not notified of stateformat=1 collection creation"
argument_list|,
name|future
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Future
argument_list|<
name|Boolean
argument_list|>
name|migrated
init|=
name|waitInBackground
argument_list|(
literal|"stateformat1"
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
name|c
operator|!=
literal|null
operator|&&
name|c
operator|.
name|getStateFormat
argument_list|()
operator|==
literal|2
argument_list|)
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|migrateCollectionFormat
argument_list|(
literal|"stateformat1"
argument_list|)
operator|.
name|processAndWait
argument_list|(
name|client
argument_list|,
name|MAX_WAIT_TIMEOUT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"CollectionStateWatcher did not persist over state format migration"
argument_list|,
name|migrated
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
