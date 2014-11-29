begin_unit
begin_package
DECL|package|org.apache.solr.cloud.overseer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|overseer
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
name|Collections
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
name|cloud
operator|.
name|MockZkStateReader
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
name|ImplicitDocRouter
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
begin_class
DECL|class|TestClusterStateMutator
specifier|public
class|class
name|TestClusterStateMutator
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testCreateCollection
specifier|public
name|void
name|testCreateCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterState
name|state
init|=
operator|new
name|ClusterState
argument_list|(
operator|-
literal|1
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|DocCollection
operator|>
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|MockZkStateReader
name|zkStateReader
init|=
operator|new
name|MockZkStateReader
argument_list|(
name|state
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|ClusterStateMutator
name|mutator
init|=
operator|new
name|ClusterStateMutator
argument_list|(
name|zkStateReader
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|message
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|ZkNodeProps
operator|.
name|makeMap
argument_list|(
literal|"name"
argument_list|,
literal|"xyz"
argument_list|,
literal|"numShards"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
decl_stmt|;
name|ZkWriteCommand
name|cmd
init|=
name|mutator
operator|.
name|createCollection
argument_list|(
name|clusterState
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|DocCollection
name|collection
init|=
name|cmd
operator|.
name|collection
decl_stmt|;
name|assertEquals
argument_list|(
literal|"xyz"
argument_list|,
name|collection
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|collection
operator|.
name|getSlicesMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|collection
operator|.
name|getMaxShardsPerNode
argument_list|()
argument_list|)
expr_stmt|;
name|state
operator|=
operator|new
name|ClusterState
argument_list|(
operator|-
literal|1
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"xyz"
argument_list|,
name|collection
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|=
operator|new
name|ZkNodeProps
argument_list|(
name|ZkNodeProps
operator|.
name|makeMap
argument_list|(
literal|"name"
argument_list|,
literal|"abc"
argument_list|,
literal|"numShards"
argument_list|,
literal|"2"
argument_list|,
literal|"router.name"
argument_list|,
literal|"implicit"
argument_list|,
literal|"shards"
argument_list|,
literal|"x,y"
argument_list|,
literal|"replicationFactor"
argument_list|,
literal|"3"
argument_list|,
literal|"maxShardsPerNode"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|=
name|mutator
operator|.
name|createCollection
argument_list|(
name|state
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|collection
operator|=
name|cmd
operator|.
name|collection
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|collection
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|collection
operator|.
name|getSlicesMap
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|collection
operator|.
name|getSlicesMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|collection
operator|.
name|getSlicesMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|collection
operator|.
name|getSlicesMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getRange
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|collection
operator|.
name|getSlicesMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getRange
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"active"
argument_list|,
name|collection
operator|.
name|getSlicesMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"active"
argument_list|,
name|collection
operator|.
name|getSlicesMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|collection
operator|.
name|getMaxShardsPerNode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ImplicitDocRouter
operator|.
name|class
argument_list|,
name|collection
operator|.
name|getRouter
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|state
operator|.
name|getCollectionOrNull
argument_list|(
literal|"xyz"
argument_list|)
argument_list|)
expr_stmt|;
comment|// we still have the old collection
block|}
block|}
end_class
end_unit
