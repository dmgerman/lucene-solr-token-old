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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|BaseDistributedSearchTestCase
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
name|SolrConfig
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
begin_class
DECL|class|AbstractDistributedZkTestCase
specifier|public
specifier|abstract
class|class
name|AbstractDistributedZkTestCase
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
DECL|field|zkServer
specifier|protected
name|ZkTestServer
name|zkServer
decl_stmt|;
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_START "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"java.nio.channels.ClosedChannelException"
argument_list|)
expr_stmt|;
name|String
name|zkDir
init|=
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
decl_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|buildZooKeeper
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
comment|// set some system properties for use by tests
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createServers
specifier|protected
name|void
name|createServers
parameter_list|(
name|int
name|numShards
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"collection"
argument_list|,
literal|"control_collection"
argument_list|)
expr_stmt|;
name|controlJetty
operator|=
name|createJetty
argument_list|(
name|testDir
argument_list|,
name|testDir
operator|+
literal|"/control/data"
argument_list|,
literal|"control_shard"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"collection"
argument_list|)
expr_stmt|;
name|controlClient
operator|=
name|createNewSolrServer
argument_list|(
name|controlJetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numShards
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|j
init|=
name|createJetty
argument_list|(
name|testDir
argument_list|,
name|testDir
operator|+
literal|"/jetty"
operator|+
name|i
argument_list|,
literal|"shard"
operator|+
operator|(
name|i
operator|+
literal|2
operator|)
argument_list|)
decl_stmt|;
name|jettys
operator|.
name|add
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|clients
operator|.
name|add
argument_list|(
name|createNewSolrServer
argument_list|(
name|j
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"localhost:"
argument_list|)
operator|.
name|append
argument_list|(
name|j
operator|.
name|getLocalPort
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|shards
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
name|printLayout
argument_list|()
expr_stmt|;
block|}
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkHost"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"collection"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.test.sys.prop2"
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
name|SolrConfig
operator|.
name|severeErrors
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|printLayout
specifier|protected
name|void
name|printLayout
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
