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
name|SolrClient
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
name|HttpSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|CollectionAdminRequest
operator|.
name|Create
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
operator|.
name|RequestStatus
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
operator|.
name|SplitShard
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
name|response
operator|.
name|CollectionAdminResponse
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
name|Test
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
begin_comment
comment|/**  * Tests the Cloud Collections API.  */
end_comment
begin_class
annotation|@
name|Slow
DECL|class|CollectionsAPIAsyncDistributedZkTest
specifier|public
class|class
name|CollectionsAPIAsyncDistributedZkTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|MAX_TIMEOUT_SECONDS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_TIMEOUT_SECONDS
init|=
literal|60
decl_stmt|;
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|distribSetUp
specifier|public
name|void
name|distribSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
name|useJettyDataDir
operator|=
literal|false
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|method|CollectionsAPIAsyncDistributedZkTest
specifier|public
name|CollectionsAPIAsyncDistributedZkTest
parameter_list|()
block|{
name|sliceCount
operator|=
literal|1
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|1
argument_list|)
DECL|method|testSolrJAPICalls
specifier|public
name|void
name|testSolrJAPICalls
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|SolrClient
name|client
init|=
name|createNewSolrClient
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|Create
name|createCollectionRequest
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|createCollectionRequest
operator|.
name|setCollectionName
argument_list|(
literal|"testasynccollectioncreation"
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setConfigName
argument_list|(
literal|"conf1"
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setAsyncId
argument_list|(
literal|"1001"
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|String
name|state
init|=
name|getRequestStateAfterCompletion
argument_list|(
literal|"1001"
argument_list|,
name|MAX_TIMEOUT_SECONDS
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"CreateCollection task did not complete!"
argument_list|,
literal|"completed"
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|=
operator|new
name|Create
argument_list|()
expr_stmt|;
name|createCollectionRequest
operator|.
name|setCollectionName
argument_list|(
literal|"testasynccollectioncreation"
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setConfigName
argument_list|(
literal|"conf1"
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|setAsyncId
argument_list|(
literal|"1002"
argument_list|)
expr_stmt|;
name|createCollectionRequest
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|state
operator|=
name|getRequestStateAfterCompletion
argument_list|(
literal|"1002"
argument_list|,
name|MAX_TIMEOUT_SECONDS
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Recreating a collection with the same name didn't fail, should have."
argument_list|,
literal|"failed"
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|AddReplica
name|addReplica
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|AddReplica
argument_list|()
decl_stmt|;
name|addReplica
operator|.
name|setCollectionName
argument_list|(
literal|"testasynccollectioncreation"
argument_list|)
expr_stmt|;
name|addReplica
operator|.
name|setShardName
argument_list|(
literal|"shard1"
argument_list|)
expr_stmt|;
name|addReplica
operator|.
name|setAsyncId
argument_list|(
literal|"1003"
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|addReplica
argument_list|)
expr_stmt|;
name|state
operator|=
name|getRequestStateAfterCompletion
argument_list|(
literal|"1003"
argument_list|,
name|MAX_TIMEOUT_SECONDS
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Add replica did not complete"
argument_list|,
literal|"completed"
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|SplitShard
name|splitShardRequest
init|=
operator|new
name|SplitShard
argument_list|()
decl_stmt|;
name|splitShardRequest
operator|.
name|setCollectionName
argument_list|(
literal|"testasynccollectioncreation"
argument_list|)
expr_stmt|;
name|splitShardRequest
operator|.
name|setShardName
argument_list|(
literal|"shard1"
argument_list|)
expr_stmt|;
name|splitShardRequest
operator|.
name|setAsyncId
argument_list|(
literal|"1004"
argument_list|)
expr_stmt|;
name|splitShardRequest
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|state
operator|=
name|getRequestStateAfterCompletion
argument_list|(
literal|"1004"
argument_list|,
name|MAX_TIMEOUT_SECONDS
operator|*
literal|2
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Shard split did not complete. Last recorded state: "
operator|+
name|state
argument_list|,
literal|"completed"
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|DEBUG
condition|)
block|{
name|printLayout
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getRequestStateAfterCompletion
specifier|private
name|String
name|getRequestStateAfterCompletion
parameter_list|(
name|String
name|requestId
parameter_list|,
name|int
name|waitForSeconds
parameter_list|,
name|SolrClient
name|client
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|String
name|state
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|waitForSeconds
operator|--
operator|>
literal|0
condition|)
block|{
name|state
operator|=
name|getRequestState
argument_list|(
name|requestId
argument_list|,
name|client
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|equals
argument_list|(
literal|"completed"
argument_list|)
operator|||
name|state
operator|.
name|equals
argument_list|(
literal|"failed"
argument_list|)
condition|)
return|return
name|state
return|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{       }
block|}
return|return
name|state
return|;
block|}
DECL|method|getRequestState
specifier|private
name|String
name|getRequestState
parameter_list|(
name|String
name|requestId
parameter_list|,
name|SolrClient
name|client
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|RequestStatus
name|request
init|=
operator|new
name|RequestStatus
argument_list|()
decl_stmt|;
name|request
operator|.
name|setRequestId
argument_list|(
name|requestId
argument_list|)
expr_stmt|;
name|CollectionAdminResponse
name|response
init|=
name|request
operator|.
name|process
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|NamedList
name|innerResponse
init|=
operator|(
name|NamedList
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
decl_stmt|;
return|return
operator|(
name|String
operator|)
name|innerResponse
operator|.
name|get
argument_list|(
literal|"state"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"numShards"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|)
expr_stmt|;
comment|// insurance
name|DirectUpdateHandler2
operator|.
name|commitOnClose
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class
end_unit
