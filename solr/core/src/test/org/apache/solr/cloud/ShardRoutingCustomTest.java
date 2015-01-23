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
begin_class
DECL|class|ShardRoutingCustomTest
specifier|public
class|class
name|ShardRoutingCustomTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|collection
name|String
name|collection
init|=
name|DEFAULT_COLLECTION
decl_stmt|;
comment|// enable this to be configurable (more work needs to be done)
annotation|@
name|BeforeClass
DECL|method|beforeShardHashingTest
specifier|public
specifier|static
name|void
name|beforeShardHashingTest
parameter_list|()
throws|throws
name|Exception
block|{
name|useFactory
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|ShardRoutingCustomTest
specifier|public
name|ShardRoutingCustomTest
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
name|sliceCount
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|testFinished
init|=
literal|false
decl_stmt|;
try|try
block|{
name|doCustomSharding
argument_list|()
expr_stmt|;
name|testFinished
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|testFinished
condition|)
block|{
name|printLayout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|doCustomSharding
specifier|private
name|void
name|doCustomSharding
parameter_list|()
throws|throws
name|Exception
block|{
name|printLayout
argument_list|()
expr_stmt|;
name|startCloudJetty
argument_list|(
name|collection
argument_list|,
literal|"shardA"
argument_list|)
expr_stmt|;
name|printLayout
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
