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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|common
operator|.
name|params
operator|.
name|CollectionParams
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
name|junit
operator|.
name|Before
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
begin_class
DECL|class|TestRequestStatusCollectionAPI
specifier|public
class|class
name|TestRequestStatusCollectionAPI
extends|extends
name|BasicDistributedZkTest
block|{
DECL|field|MAX_WAIT_TIMEOUT_SECONDS
specifier|public
specifier|static
specifier|final
name|int
name|MAX_WAIT_TIMEOUT_SECONDS
init|=
literal|90
decl_stmt|;
DECL|method|TestRequestStatusCollectionAPI
specifier|public
name|TestRequestStatusCollectionAPI
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
block|}
annotation|@
name|Override
annotation|@
name|Before
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
block|}
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
block|{
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
name|CollectionParams
operator|.
name|ACTION
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|CREATE
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
literal|"collection2"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"numShards"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"replicationFactor"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"maxShardsPerNode"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"collection.configName"
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"async"
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
try|try
block|{
name|sendRequest
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// Check for the request to be completed.
name|NamedList
name|r
init|=
literal|null
decl_stmt|;
name|NamedList
name|status
init|=
literal|null
decl_stmt|;
name|String
name|message
init|=
literal|null
decl_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|REQUESTSTATUS
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|REQUESTID
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
try|try
block|{
name|message
operator|=
name|sendStatusRequestWithRetry
argument_list|(
name|params
argument_list|,
name|MAX_WAIT_TIMEOUT_SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Task 1000 not found in completed tasks."
argument_list|,
literal|"found 1000 in completed tasks"
argument_list|,
name|message
argument_list|)
expr_stmt|;
comment|// Check for a random (hopefully non-existent request id
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CollectionParams
operator|.
name|ACTION
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|REQUESTSTATUS
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|REQUESTID
argument_list|,
literal|"9999999"
argument_list|)
expr_stmt|;
try|try
block|{
name|r
operator|=
name|sendRequest
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|status
operator|=
operator|(
name|NamedList
operator|)
name|r
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
expr_stmt|;
name|message
operator|=
operator|(
name|String
operator|)
name|status
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Task 9999999 found in tasks queue."
argument_list|,
literal|"Did not find taskid [9999999] in any tasks queue"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CollectionParams
operator|.
name|ACTION
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|SPLITSHARD
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
literal|"collection2"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"shard"
argument_list|,
literal|"shard1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"async"
argument_list|,
literal|"1001"
argument_list|)
expr_stmt|;
try|try
block|{
name|sendRequest
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// Check for the request to be completed.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|REQUESTSTATUS
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|REQUESTID
argument_list|,
literal|"1001"
argument_list|)
expr_stmt|;
try|try
block|{
name|message
operator|=
name|sendStatusRequestWithRetry
argument_list|(
name|params
argument_list|,
name|MAX_WAIT_TIMEOUT_SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Task 1001 not found in completed tasks."
argument_list|,
literal|"found 1001 in completed tasks"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CollectionParams
operator|.
name|ACTION
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|CREATE
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
literal|"collection2"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"numShards"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"replicationFactor"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"maxShardsPerNode"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"collection.configName"
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"async"
argument_list|,
literal|"1002"
argument_list|)
expr_stmt|;
try|try
block|{
name|sendRequest
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|REQUESTSTATUS
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|OverseerCollectionProcessor
operator|.
name|REQUESTID
argument_list|,
literal|"1002"
argument_list|)
expr_stmt|;
try|try
block|{
name|message
operator|=
name|sendStatusRequestWithRetry
argument_list|(
name|params
argument_list|,
name|MAX_WAIT_TIMEOUT_SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Task 1002 not found in completed tasks."
argument_list|,
literal|"found 1002 in failed tasks"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CollectionParams
operator|.
name|ACTION
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|CREATE
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
literal|"collection3"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"numShards"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"replicationFactor"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"maxShardsPerNode"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"collection.configName"
argument_list|,
literal|"conf1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"async"
argument_list|,
literal|"1002"
argument_list|)
expr_stmt|;
try|try
block|{
name|r
operator|=
name|sendRequest
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Did not error out on duplicate requests (same request id)"
argument_list|,
literal|"Task with the same requestid already exists."
argument_list|,
name|r
operator|.
name|get
argument_list|(
literal|"error"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper method to send a status request with specific retry limit and return    * the message/null from the success response.    */
DECL|method|sendStatusRequestWithRetry
specifier|private
name|String
name|sendStatusRequestWithRetry
parameter_list|(
name|ModifiableSolrParams
name|params
parameter_list|,
name|int
name|maxCounter
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|NamedList
name|status
init|=
literal|null
decl_stmt|;
name|String
name|state
init|=
literal|null
decl_stmt|;
name|String
name|message
init|=
literal|null
decl_stmt|;
name|NamedList
name|r
decl_stmt|;
while|while
condition|(
name|maxCounter
operator|--
operator|>
literal|0
condition|)
block|{
name|r
operator|=
name|sendRequest
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|status
operator|=
operator|(
name|NamedList
operator|)
name|r
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
expr_stmt|;
name|state
operator|=
operator|(
name|String
operator|)
name|status
operator|.
name|get
argument_list|(
literal|"state"
argument_list|)
expr_stmt|;
name|message
operator|=
operator|(
name|String
operator|)
name|status
operator|.
name|get
argument_list|(
literal|"msg"
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
operator|(
name|String
operator|)
name|status
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
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
block|{        }
block|}
comment|// Return last state?
return|return
name|message
return|;
block|}
DECL|method|sendRequest
specifier|protected
name|NamedList
name|sendRequest
parameter_list|(
name|ModifiableSolrParams
name|params
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|SolrRequest
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
name|String
name|baseUrl
init|=
operator|(
operator|(
name|HttpSolrClient
operator|)
name|shardToJetty
operator|.
name|get
argument_list|(
name|SHARD1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|client
operator|.
name|solrClient
operator|)
operator|.
name|getBaseURL
argument_list|()
decl_stmt|;
name|baseUrl
operator|=
name|baseUrl
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|baseUrl
operator|.
name|length
argument_list|()
operator|-
literal|"collection1"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|HttpSolrClient
name|baseClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
name|baseClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
return|return
name|baseClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
end_class
end_unit
