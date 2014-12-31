begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|update
operator|.
name|processor
operator|.
name|DocExpirationUpdateProcessorFactory
import|;
end_import
begin_comment
comment|// jdoc
end_comment
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
name|processor
operator|.
name|DocExpirationUpdateProcessorFactoryTest
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Map
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
name|HashSet
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
begin_comment
comment|/** Test of {@link DocExpirationUpdateProcessorFactory} in a cloud setup */
end_comment
begin_class
annotation|@
name|Slow
comment|// Has to do some sleeping to wait for a future expiration
DECL|class|DistribDocExpirationUpdateProcessorTest
specifier|public
class|class
name|DistribDocExpirationUpdateProcessorTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DistribDocExpirationUpdateProcessorTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|DistribDocExpirationUpdateProcessorTest
specifier|public
name|DistribDocExpirationUpdateProcessorTest
parameter_list|()
block|{
name|configString
operator|=
name|DocExpirationUpdateProcessorFactoryTest
operator|.
name|CONFIG_XML
expr_stmt|;
name|schemaString
operator|=
name|DocExpirationUpdateProcessorFactoryTest
operator|.
name|SCHEMA_XML
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
name|configString
return|;
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
name|assertTrue
argument_list|(
literal|"only one shard?!?!?!"
argument_list|,
literal|1
operator|<
name|shardToJetty
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"number of shards: {}"
argument_list|,
name|shardToJetty
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
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
comment|// some docs with no expiration
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|30
argument_list|)
expr_stmt|;
comment|// this doc better not already exist
name|waitForNoResults
argument_list|(
literal|0
argument_list|,
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"id:999"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"_trace"
argument_list|,
literal|"sanity_check"
argument_list|)
argument_list|)
expr_stmt|;
comment|// record the indexversion for each server so we can check later
comment|// that it only changes for one shard
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|initIndexVersions
init|=
name|getIndexVersionOfAllReplicas
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"WTF? no versions?"
argument_list|,
literal|0
operator|<
name|initIndexVersions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// add a doc with a short TTL
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"999"
argument_list|,
literal|"tTl_s"
argument_list|,
literal|"+30SECONDS"
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
comment|// wait for one doc to be deleted
name|waitForNoResults
argument_list|(
literal|180
argument_list|,
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"id:999"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"_trace"
argument_list|,
literal|"did_it_expire_yet"
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify only one shard changed
name|waitForThingsToLevelOut
argument_list|(
literal|30
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|finalIndexVersions
init|=
name|getIndexVersionOfAllReplicas
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"WTF? not same num versions?"
argument_list|,
name|initIndexVersions
operator|.
name|size
argument_list|()
argument_list|,
name|finalIndexVersions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|nodesThatChange
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|shardsThatChange
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|coresCompared
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|shard
range|:
name|shardToJetty
operator|.
name|keySet
argument_list|()
control|)
block|{
for|for
control|(
name|CloudJettyRunner
name|replicaRunner
range|:
name|shardToJetty
operator|.
name|get
argument_list|(
name|shard
argument_list|)
control|)
block|{
name|coresCompared
operator|++
expr_stmt|;
name|String
name|core
init|=
name|replicaRunner
operator|.
name|coreNodeName
decl_stmt|;
name|Long
name|initVersion
init|=
name|initIndexVersions
operator|.
name|get
argument_list|(
name|core
argument_list|)
decl_stmt|;
name|Long
name|finalVersion
init|=
name|finalIndexVersions
operator|.
name|get
argument_list|(
name|core
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|shard
operator|+
literal|": no init version for core: "
operator|+
name|core
argument_list|,
name|initVersion
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|shard
operator|+
literal|": no final version for core: "
operator|+
name|core
argument_list|,
name|finalVersion
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|initVersion
operator|.
name|equals
argument_list|(
name|finalVersion
argument_list|)
condition|)
block|{
name|nodesThatChange
operator|.
name|add
argument_list|(
name|core
operator|+
literal|"("
operator|+
name|shard
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|shardsThatChange
operator|.
name|add
argument_list|(
name|shard
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
literal|"Exactly one shard should have changed, instead: "
operator|+
name|shardsThatChange
operator|+
literal|" nodes=("
operator|+
name|nodesThatChange
operator|+
literal|")"
argument_list|,
literal|1
argument_list|,
name|shardsThatChange
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"somehow we missed some cores?"
argument_list|,
name|initIndexVersions
operator|.
name|size
argument_list|()
argument_list|,
name|coresCompared
argument_list|)
expr_stmt|;
comment|// TODO: above logic verifies that deleteByQuery happens on all nodes, and ...
comment|// doesn't affect searcher re-open on shards w/o expired docs ... can we also verify
comment|// that *only* one node is sending the deletes ?
comment|// (ie: no flood of redundent deletes?)
block|}
comment|/**    * returns a map whose key is the coreNodeName and whose value is what the replication    * handler returns for the indexversion    */
DECL|method|getIndexVersionOfAllReplicas
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getIndexVersionOfAllReplicas
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|results
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|listOfReplicas
range|:
name|shardToJetty
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|CloudJettyRunner
name|replicaRunner
range|:
name|listOfReplicas
control|)
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
literal|"command"
argument_list|,
literal|"indexversion"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"_trace"
argument_list|,
literal|"getIndexVersion"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"qt"
argument_list|,
literal|"/replication"
argument_list|)
expr_stmt|;
name|QueryRequest
name|req
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
name|replicaRunner
operator|.
name|client
operator|.
name|solrClient
operator|.
name|request
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"null response from server: "
operator|+
name|replicaRunner
operator|.
name|coreNodeName
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|Object
name|version
init|=
name|res
operator|.
name|get
argument_list|(
literal|"indexversion"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"null version from server: "
operator|+
name|replicaRunner
operator|.
name|coreNodeName
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"version isn't a long: "
operator|+
name|replicaRunner
operator|.
name|coreNodeName
argument_list|,
name|version
operator|instanceof
name|Long
argument_list|)
expr_stmt|;
name|results
operator|.
name|put
argument_list|(
name|replicaRunner
operator|.
name|coreNodeName
argument_list|,
operator|(
name|Long
operator|)
name|version
argument_list|)
expr_stmt|;
name|long
name|numDocs
init|=
name|replicaRunner
operator|.
name|client
operator|.
name|solrClient
operator|.
name|query
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"distrib"
argument_list|,
literal|"false"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"_trace"
argument_list|,
literal|"counting_docs"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"core="
operator|+
name|replicaRunner
operator|.
name|coreNodeName
operator|+
literal|"; ver="
operator|+
name|version
operator|+
literal|"; numDocs="
operator|+
name|numDocs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|results
return|;
block|}
comment|/**    * Executes a query over and over against the cloudClient every 5 seconds     * until the numFound is 0 or the maxTimeLimitSeconds is exceeded.     * Query is garunteed to be executed at least once.    */
DECL|method|waitForNoResults
specifier|private
name|void
name|waitForNoResults
parameter_list|(
name|int
name|maxTimeLimitSeconds
parameter_list|,
name|SolrParams
name|params
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|InterruptedException
block|{
specifier|final
name|long
name|giveUpAfter
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
operator|(
literal|1000L
operator|*
name|maxTimeLimitSeconds
operator|)
decl_stmt|;
name|long
name|numFound
init|=
name|cloudClient
operator|.
name|query
argument_list|(
name|params
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
while|while
condition|(
literal|0L
operator|<
name|numFound
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|giveUpAfter
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|5000
argument_list|,
name|giveUpAfter
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|numFound
operator|=
name|cloudClient
operator|.
name|query
argument_list|(
name|params
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Give up waiting for no results: "
operator|+
name|params
argument_list|,
literal|0L
argument_list|,
name|numFound
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
