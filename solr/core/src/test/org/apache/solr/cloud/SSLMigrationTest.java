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
import|import static
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
operator|.
name|makeMap
import|;
end_import
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
name|java
operator|.
name|util
operator|.
name|Locale
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|HttpClientUtil
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
name|LBHttpSolrServer
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
name|Replica
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
name|Slice
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
name|ZkStateReader
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
operator|.
name|CollectionAction
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
name|MapSolrParams
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
name|util
operator|.
name|SSLTestConfig
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
operator|.
name|SuppressSSL
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
begin_comment
comment|/**  * We want to make sure that when migrating between http and https modes the  * replicas will not be rejoined as new nodes, but rather take off where it left  * off in the cluster.  */
end_comment
begin_class
annotation|@
name|Slow
annotation|@
name|SuppressSSL
comment|// tests starts with SSL off
DECL|class|SSLMigrationTest
specifier|public
class|class
name|SSLMigrationTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
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
comment|//Migrate from HTTP -> HTTPS -> HTTP
name|assertReplicaInformation
argument_list|(
literal|"http"
argument_list|)
expr_stmt|;
name|testMigrateSSL
argument_list|(
operator|new
name|SSLTestConfig
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|testMigrateSSL
argument_list|(
operator|new
name|SSLTestConfig
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMigrateSSL
specifier|public
name|void
name|testMigrateSSL
parameter_list|(
name|SSLTestConfig
name|sslConfig
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|urlScheme
init|=
name|sslConfig
operator|.
name|isSSLMode
argument_list|()
condition|?
literal|"https"
else|:
literal|"http"
decl_stmt|;
name|setUrlScheme
argument_list|(
name|urlScheme
argument_list|)
expr_stmt|;
for|for
control|(
name|JettySolrRunner
name|runner
range|:
name|jettys
control|)
block|{
name|runner
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|HttpClientUtil
operator|.
name|setConfigurer
argument_list|(
name|sslConfig
operator|.
name|getHttpClientConfigurer
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|this
operator|.
name|jettys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|JettySolrRunner
name|runner
init|=
name|jettys
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|JettySolrRunner
name|newRunner
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|runner
operator|.
name|getSolrHome
argument_list|()
argument_list|,
name|context
argument_list|,
name|runner
operator|.
name|getLocalPort
argument_list|()
argument_list|,
name|getSolrConfigFile
argument_list|()
argument_list|,
name|getSchemaFile
argument_list|()
argument_list|,
literal|false
argument_list|,
name|getExtraServlets
argument_list|()
argument_list|,
name|sslConfig
argument_list|,
name|getExtraRequestFilters
argument_list|()
argument_list|)
decl_stmt|;
name|newRunner
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|testDir
operator|+
literal|"/shard"
operator|+
name|i
operator|+
literal|"/data"
argument_list|)
argument_list|)
expr_stmt|;
name|newRunner
operator|.
name|start
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|jettys
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|newRunner
argument_list|)
expr_stmt|;
block|}
name|assertReplicaInformation
argument_list|(
name|urlScheme
argument_list|)
expr_stmt|;
block|}
DECL|method|assertReplicaInformation
specifier|private
name|void
name|assertReplicaInformation
parameter_list|(
name|String
name|urlScheme
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|getReplicas
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of replicas found"
argument_list|,
literal|4
argument_list|,
name|replicas
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
name|assertTrue
argument_list|(
literal|"Replica didn't have the proper urlScheme in the ClusterState"
argument_list|,
name|StringUtils
operator|.
name|startsWith
argument_list|(
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
argument_list|,
name|urlScheme
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getReplicas
specifier|private
name|List
argument_list|<
name|Replica
argument_list|>
name|getReplicas
parameter_list|()
block|{
name|List
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
operator|new
name|ArrayList
argument_list|<
name|Replica
argument_list|>
argument_list|()
decl_stmt|;
name|DocCollection
name|collection
init|=
name|this
operator|.
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|DEFAULT_COLLECTION
argument_list|)
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|collection
operator|.
name|getSlices
argument_list|()
control|)
block|{
name|replicas
operator|.
name|addAll
argument_list|(
name|slice
operator|.
name|getReplicas
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|replicas
return|;
block|}
DECL|method|setUrlScheme
specifier|private
name|void
name|setUrlScheme
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|Exception
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
name|Map
name|m
init|=
name|makeMap
argument_list|(
literal|"action"
argument_list|,
name|CollectionAction
operator|.
name|CLUSTERPROP
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
literal|"name"
argument_list|,
literal|"urlScheme"
argument_list|,
literal|"val"
argument_list|,
name|value
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|SolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
name|m
argument_list|)
decl_stmt|;
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
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|getReplicas
argument_list|()
control|)
block|{
name|urls
operator|.
name|add
argument_list|(
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Create new SolrServer to configure new HttpClient w/ SSL config
operator|new
name|LBHttpSolrServer
argument_list|(
name|urls
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
argument_list|)
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
