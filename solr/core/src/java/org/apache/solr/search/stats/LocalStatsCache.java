begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|stats
package|;
end_package
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
name|List
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
name|PluginInfo
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
name|ResponseBuilder
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
name|ShardRequest
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
name|ShardResponse
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
name|request
operator|.
name|SolrQueryRequest
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
name|search
operator|.
name|SolrIndexSearcher
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
begin_comment
comment|/**  * Default implementation that simply ignores global term statistics, and always  * uses local term statistics.  */
end_comment
begin_class
DECL|class|LocalStatsCache
specifier|public
class|class
name|LocalStatsCache
extends|extends
name|StatsCache
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
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
annotation|@
name|Override
DECL|method|get
specifier|public
name|StatsSource
name|get
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"## GET {}"
argument_list|,
name|req
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|LocalStatsSource
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{   }
comment|// by returning null we don't create additional round-trip request.
annotation|@
name|Override
DECL|method|retrieveStatsRequest
specifier|public
name|ShardRequest
name|retrieveStatsRequest
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"## RDR {}"
argument_list|,
name|rb
operator|.
name|req
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|mergeToGlobalStats
specifier|public
name|void
name|mergeToGlobalStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|List
argument_list|<
name|ShardResponse
argument_list|>
name|responses
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"## MTGD {}"
argument_list|,
name|req
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardResponse
name|r
range|:
name|responses
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|" - {}"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|returnLocalStats
specifier|public
name|void
name|returnLocalStats
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"## RLD {}"
argument_list|,
name|rb
operator|.
name|req
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|receiveGlobalStats
specifier|public
name|void
name|receiveGlobalStats
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"## RGD {}"
argument_list|,
name|req
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendGlobalStats
specifier|public
name|void
name|sendGlobalStats
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|outgoing
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"## SGD {}"
argument_list|,
name|outgoing
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
