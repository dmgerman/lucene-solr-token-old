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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|BadApple
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
name|common
operator|.
name|SolrInputDocument
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
name|update
operator|.
name|DirectUpdateHandler2
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
begin_class
annotation|@
name|Slow
annotation|@
name|BadApple
DECL|class|ChaosMonkeySafeLeaderTest
specifier|public
class|class
name|ChaosMonkeySafeLeaderTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|BASE_RUN_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|BASE_RUN_LENGTH
init|=
literal|120000
decl_stmt|;
DECL|field|RUN_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|RUN_LENGTH
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.tests.cloud.cm.runlength"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|BASE_RUN_LENGTH
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeSuperClass
specifier|public
specifier|static
name|void
name|beforeSuperClass
parameter_list|()
block|{    }
annotation|@
name|AfterClass
DECL|method|afterSuperClass
specifier|public
specifier|static
name|void
name|afterSuperClass
parameter_list|()
block|{        }
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|useFactory
argument_list|(
literal|"solr.StandardDirectoryFactory"
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"numShards"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sliceCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"numShards"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
DECL|method|ChaosMonkeySafeLeaderTest
specifier|public
name|ChaosMonkeySafeLeaderTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.tests.cloud.cm.slicecount"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|shardCount
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.tests.cloud.cm.shardcount"
argument_list|,
literal|"12"
argument_list|)
argument_list|)
expr_stmt|;
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
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
comment|// randomly turn on 5 seconds 'soft' commit
name|randomlyEnableAutoSoftCommit
argument_list|()
expr_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StopableIndexingThread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|StopableIndexingThread
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|threadCount
init|=
literal|2
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
name|threadCount
condition|;
name|i
operator|++
control|)
block|{
name|StopableIndexingThread
name|indexThread
init|=
operator|new
name|StopableIndexingThread
argument_list|(
name|i
operator|*
literal|50000
argument_list|,
literal|true
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
name|chaosMonkey
operator|.
name|startTheMonkey
argument_list|(
literal|false
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|int
name|runLength
init|=
name|RUN_LENGTH
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|runLength
argument_list|)
expr_stmt|;
name|chaosMonkey
operator|.
name|stopTheMonkey
argument_list|()
expr_stmt|;
for|for
control|(
name|StopableIndexingThread
name|indexThread
range|:
name|threads
control|)
block|{
name|indexThread
operator|.
name|safeStop
argument_list|()
expr_stmt|;
block|}
comment|// wait for stop...
for|for
control|(
name|StopableIndexingThread
name|indexThread
range|:
name|threads
control|)
block|{
name|indexThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|StopableIndexingThread
name|indexThread
range|:
name|threads
control|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|indexThread
operator|.
name|getFails
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// try and wait for any replications and what not to finish...
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|180000
argument_list|)
expr_stmt|;
name|checkShardConsistency
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"control docs:"
operator|+
name|controlClient
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
operator|+
literal|"\n\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|randomlyEnableAutoSoftCommit
specifier|private
name|void
name|randomlyEnableAutoSoftCommit
parameter_list|()
block|{
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Turning on auto soft commit"
argument_list|)
expr_stmt|;
for|for
control|(
name|CloudJettyRunner
name|jetty
range|:
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
control|)
block|{
name|SolrCore
name|core
init|=
operator|(
operator|(
name|SolrDispatchFilter
operator|)
name|jetty
operator|.
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
operator|.
name|getCore
argument_list|(
literal|"collection1"
argument_list|)
decl_stmt|;
try|try
block|{
operator|(
operator|(
name|DirectUpdateHandler2
operator|)
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|)
operator|.
name|getCommitTracker
argument_list|()
operator|.
name|setTimeUpperBound
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Not turning on auto soft commit"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// skip the randoms - they can deadlock...
annotation|@
name|Override
DECL|method|indexr
specifier|protected
name|void
name|indexr
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
literal|"rnd_b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
