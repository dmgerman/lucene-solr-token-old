begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package
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
name|QueryResponse
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
name|handler
operator|.
name|component
operator|.
name|TrackingShardHandlerFactory
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
name|handler
operator|.
name|component
operator|.
name|TrackingShardHandlerFactory
operator|.
name|ShardRequestAndParams
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
name|handler
operator|.
name|component
operator|.
name|TrackingShardHandlerFactory
operator|.
name|RequestTrackingQueue
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
name|Collections
import|;
end_import
begin_comment
comment|/**  * super simple sanity check that SimpleTrackingShardHandler can be used in a   * {@link BaseDistributedSearchTestCase} subclass  */
end_comment
begin_class
DECL|class|TestSimpleTrackingShardHandler
specifier|public
class|class
name|TestSimpleTrackingShardHandler
extends|extends
name|BaseDistributedSearchTestCase
block|{
annotation|@
name|Override
DECL|method|getSolrXml
specifier|protected
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"solr-trackingshardhandler.xml"
return|;
block|}
DECL|method|testSolrXmlOverrideAndCorrectShardHandler
specifier|public
name|void
name|testSolrXmlOverrideAndCorrectShardHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|RequestTrackingQueue
name|trackingQueue
init|=
operator|new
name|RequestTrackingQueue
argument_list|()
decl_stmt|;
name|TrackingShardHandlerFactory
operator|.
name|setTrackingQueue
argument_list|(
name|jettys
argument_list|,
name|trackingQueue
argument_list|)
expr_stmt|;
comment|// sanity check that our control jetty has the correct configs as well
name|TrackingShardHandlerFactory
operator|.
name|setTrackingQueue
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|controlJetty
argument_list|)
argument_list|,
name|trackingQueue
argument_list|)
expr_stmt|;
name|QueryResponse
name|ignored
init|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
decl_stmt|;
name|int
name|numShardRequests
init|=
literal|0
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|ShardRequestAndParams
argument_list|>
name|shard
range|:
name|trackingQueue
operator|.
name|getAllRequests
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|ShardRequestAndParams
name|shardReq
range|:
name|shard
control|)
block|{
name|numShardRequests
operator|++
expr_stmt|;
block|}
block|}
name|TrackingShardHandlerFactory
operator|.
name|setTrackingQueue
argument_list|(
name|jettys
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|TrackingShardHandlerFactory
operator|.
name|setTrackingQueue
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|controlJetty
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
