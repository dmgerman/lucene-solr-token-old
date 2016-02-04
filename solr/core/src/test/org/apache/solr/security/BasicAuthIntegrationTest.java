begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.security
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
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
name|ArrayList
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
name|Objects
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPost
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ByteArrayEntity
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|message
operator|.
name|AbstractHttpMessage
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|message
operator|.
name|BasicHeader
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
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
name|CloudSolrClient
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
name|GenericSolrRequest
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
name|UpdateRequest
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
name|MiniSolrCloudCluster
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
name|TestMiniSolrCloudClusterBase
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
name|Base64
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
name|ContentStreamBase
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
name|util
operator|.
name|StrUtils
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
name|Utils
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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import
begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import
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
name|ZkStateReader
operator|.
name|BASE_URL_PROP
import|;
end_import
begin_class
DECL|class|BasicAuthIntegrationTest
specifier|public
class|class
name|BasicAuthIntegrationTest
extends|extends
name|TestMiniSolrCloudClusterBase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
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
DECL|method|doExtraTests
specifier|protected
name|void
name|doExtraTests
parameter_list|(
name|MiniSolrCloudCluster
name|miniCluster
parameter_list|,
name|SolrZkClient
name|zkClient
parameter_list|,
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|CloudSolrClient
name|cloudSolrClient
parameter_list|,
name|String
name|defaultCollName
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|authcPrefix
init|=
literal|"/admin/authentication"
decl_stmt|;
name|String
name|authzPrefix
init|=
literal|"/admin/authorization"
decl_stmt|;
name|String
name|old
init|=
name|cloudSolrClient
operator|.
name|getDefaultCollection
argument_list|()
decl_stmt|;
name|cloudSolrClient
operator|.
name|setDefaultCollection
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
decl_stmt|;
name|HttpClient
name|cl
init|=
name|cloudSolrClient
operator|.
name|getLbClient
argument_list|()
operator|.
name|getHttpClient
argument_list|()
decl_stmt|;
name|String
name|baseUrl
init|=
name|getRandomReplica
argument_list|(
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|defaultCollName
argument_list|)
argument_list|,
name|random
argument_list|()
argument_list|)
operator|.
name|getStr
argument_list|(
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authcPrefix
argument_list|,
literal|"/errorMessages"
argument_list|,
literal|null
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|setData
argument_list|(
literal|"/security.json"
argument_list|,
name|STD_CONF
operator|.
name|replaceAll
argument_list|(
literal|"'"
argument_list|,
literal|"\""
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authcPrefix
argument_list|,
literal|"authentication/class"
argument_list|,
literal|"solr.BasicAuthPlugin"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|JettySolrRunner
name|jettySolrRunner
range|:
name|miniCluster
operator|.
name|getJettySolrRunners
argument_list|()
control|)
block|{
if|if
condition|(
name|baseUrl
operator|.
name|contains
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|jettySolrRunner
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|jettySolrRunner
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jettySolrRunner
operator|.
name|start
argument_list|()
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authcPrefix
argument_list|,
literal|"authentication/class"
argument_list|,
literal|"solr.BasicAuthPlugin"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
literal|"No server found to restart , looking for : "
operator|+
name|baseUrl
argument_list|,
name|found
argument_list|)
expr_stmt|;
name|String
name|command
init|=
literal|"{\n"
operator|+
literal|"'set-user': {'harry':'HarryIsCool'}\n"
operator|+
literal|"}"
decl_stmt|;
name|GenericSolrRequest
name|genericReq
init|=
operator|new
name|GenericSolrRequest
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|,
name|authcPrefix
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
name|genericReq
operator|.
name|setContentStreams
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|ByteArrayStream
argument_list|(
name|command
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|cloudSolrClient
operator|.
name|request
argument_list|(
name|genericReq
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed with a 401"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|e
parameter_list|)
block|{     }
name|command
operator|=
literal|"{\n"
operator|+
literal|"'set-user': {'harry':'HarryIsUberCool'}\n"
operator|+
literal|"}"
expr_stmt|;
name|HttpPost
name|httpPost
init|=
operator|new
name|HttpPost
argument_list|(
name|baseUrl
operator|+
name|authcPrefix
argument_list|)
decl_stmt|;
name|setBasicAuthHeader
argument_list|(
name|httpPost
argument_list|,
literal|"solr"
argument_list|,
literal|"SolrRocks"
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|setEntity
argument_list|(
operator|new
name|ByteArrayEntity
argument_list|(
name|command
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|addHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/json; charset=UTF-8"
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authcPrefix
argument_list|,
literal|"authentication.enabled"
argument_list|,
literal|"true"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|HttpResponse
name|r
init|=
name|cl
operator|.
name|execute
argument_list|(
name|httpPost
argument_list|)
decl_stmt|;
name|int
name|statusCode
init|=
name|r
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"proper_cred sent, but access denied"
argument_list|,
literal|200
argument_list|,
name|statusCode
argument_list|)
expr_stmt|;
name|baseUrl
operator|=
name|getRandomReplica
argument_list|(
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|defaultCollName
argument_list|)
argument_list|,
name|random
argument_list|()
argument_list|)
operator|.
name|getStr
argument_list|(
name|BASE_URL_PROP
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authcPrefix
argument_list|,
literal|"authentication/credentials/harry"
argument_list|,
name|NOT_NULL_PREDICATE
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|command
operator|=
literal|"{\n"
operator|+
literal|"'set-user-role': {'harry':'admin'}\n"
operator|+
literal|"}"
expr_stmt|;
name|httpPost
operator|=
operator|new
name|HttpPost
argument_list|(
name|baseUrl
operator|+
name|authzPrefix
argument_list|)
expr_stmt|;
name|setBasicAuthHeader
argument_list|(
name|httpPost
argument_list|,
literal|"solr"
argument_list|,
literal|"SolrRocks"
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|setEntity
argument_list|(
operator|new
name|ByteArrayEntity
argument_list|(
name|command
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|addHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/json; charset=UTF-8"
argument_list|)
expr_stmt|;
name|r
operator|=
name|cl
operator|.
name|execute
argument_list|(
name|httpPost
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|r
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|baseUrl
operator|=
name|getRandomReplica
argument_list|(
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|defaultCollName
argument_list|)
argument_list|,
name|random
argument_list|()
argument_list|)
operator|.
name|getStr
argument_list|(
name|BASE_URL_PROP
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authzPrefix
argument_list|,
literal|"authorization/user-role/harry"
argument_list|,
name|NOT_NULL_PREDICATE
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|httpPost
operator|=
operator|new
name|HttpPost
argument_list|(
name|baseUrl
operator|+
name|authzPrefix
argument_list|)
expr_stmt|;
name|setBasicAuthHeader
argument_list|(
name|httpPost
argument_list|,
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|setEntity
argument_list|(
operator|new
name|ByteArrayEntity
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|singletonMap
argument_list|(
literal|"set-permission"
argument_list|,
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"name"
argument_list|,
literal|"x-update"
argument_list|,
literal|"collection"
argument_list|,
literal|"x"
argument_list|,
literal|"path"
argument_list|,
literal|"/update/*"
argument_list|,
literal|"role"
argument_list|,
literal|"dev"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|addHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/json; charset=UTF-8"
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authzPrefix
argument_list|,
literal|"authorization/user-role/harry"
argument_list|,
name|NOT_NULL_PREDICATE
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|r
operator|=
name|cl
operator|.
name|execute
argument_list|(
name|httpPost
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|r
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authzPrefix
argument_list|,
literal|"authorization/permissions[1]/collection"
argument_list|,
literal|"x"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|httpPost
operator|=
operator|new
name|HttpPost
argument_list|(
name|baseUrl
operator|+
name|authzPrefix
argument_list|)
expr_stmt|;
name|setBasicAuthHeader
argument_list|(
name|httpPost
argument_list|,
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|setEntity
argument_list|(
operator|new
name|ByteArrayEntity
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|singletonMap
argument_list|(
literal|"set-permission"
argument_list|,
name|Utils
operator|.
name|makeMap
argument_list|(
literal|"name"
argument_list|,
literal|"collection-admin-edit"
argument_list|,
literal|"role"
argument_list|,
literal|"admin"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|cl
operator|.
name|execute
argument_list|(
name|httpPost
argument_list|)
expr_stmt|;
name|verifySecurityStatus
argument_list|(
name|cl
argument_list|,
name|baseUrl
operator|+
name|authzPrefix
argument_list|,
literal|"authorization/permissions[2]/name"
argument_list|,
literal|"collection-admin-edit"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|Reload
name|reload
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Reload
argument_list|()
decl_stmt|;
name|reload
operator|.
name|setCollectionName
argument_list|(
name|cloudSolrClient
operator|.
name|getDefaultCollection
argument_list|()
argument_list|)
expr_stmt|;
name|HttpSolrClient
name|solrClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
try|try
block|{
name|rsp
operator|=
name|solrClient
operator|.
name|request
argument_list|(
name|reload
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|e
parameter_list|)
block|{      }
name|reload
operator|.
name|setMethod
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|)
expr_stmt|;
try|try
block|{
name|rsp
operator|=
name|solrClient
operator|.
name|request
argument_list|(
name|reload
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|e
parameter_list|)
block|{      }
name|cloudSolrClient
operator|.
name|request
argument_list|(
operator|new
name|CollectionAdminRequest
operator|.
name|Reload
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|defaultCollName
argument_list|)
operator|.
name|setBasicAuthCredentials
argument_list|(
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|cloudSolrClient
operator|.
name|request
argument_list|(
operator|new
name|CollectionAdminRequest
operator|.
name|Reload
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|defaultCollName
argument_list|)
operator|.
name|setBasicAuthCredentials
argument_list|(
literal|"harry"
argument_list|,
literal|"Cool12345"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"This should not succeed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|e
parameter_list|)
block|{      }
name|cloudSolrClient
operator|.
name|setDefaultCollection
argument_list|(
name|old
argument_list|)
expr_stmt|;
name|httpPost
operator|=
operator|new
name|HttpPost
argument_list|(
name|baseUrl
operator|+
name|authzPrefix
argument_list|)
expr_stmt|;
name|setBasicAuthHeader
argument_list|(
name|httpPost
argument_list|,
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|setEntity
argument_list|(
operator|new
name|ByteArrayEntity
argument_list|(
literal|"{set-permission : { name : update , role : admin}}"
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|addHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/json; charset=UTF-8"
argument_list|)
expr_stmt|;
name|r
operator|=
name|cl
operator|.
name|execute
argument_list|(
name|httpPost
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|r
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|UpdateRequest
name|update
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|update
operator|.
name|setBasicAuthCredentials
argument_list|(
literal|"harry"
argument_list|,
literal|"HarryIsUberCool"
argument_list|)
expr_stmt|;
name|update
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|update
operator|.
name|setCommitWithin
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|cloudSolrClient
operator|.
name|request
argument_list|(
name|update
argument_list|)
expr_stmt|;
block|}
DECL|method|verifySecurityStatus
specifier|public
specifier|static
name|void
name|verifySecurityStatus
parameter_list|(
name|HttpClient
name|cl
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|objPath
parameter_list|,
name|Object
name|expected
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|String
name|s
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|hierarchy
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|objPath
argument_list|,
literal|'/'
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|HttpGet
name|get
init|=
operator|new
name|HttpGet
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|s
operator|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|cl
operator|.
name|execute
argument_list|(
name|get
argument_list|)
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSONString
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|Object
name|actual
init|=
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|m
argument_list|,
literal|true
argument_list|,
name|hierarchy
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|instanceof
name|Predicate
condition|)
block|{
name|Predicate
name|predicate
init|=
operator|(
name|Predicate
operator|)
name|expected
decl_stmt|;
if|if
condition|(
name|predicate
operator|.
name|test
argument_list|(
name|actual
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|actual
operator|==
literal|null
condition|?
literal|null
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|actual
argument_list|)
argument_list|,
name|expected
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"No match for "
operator|+
name|objPath
operator|+
literal|" = "
operator|+
name|expected
operator|+
literal|", full response = "
operator|+
name|s
argument_list|,
name|success
argument_list|)
expr_stmt|;
block|}
DECL|method|setBasicAuthHeader
specifier|public
specifier|static
name|void
name|setBasicAuthHeader
parameter_list|(
name|AbstractHttpMessage
name|httpMsg
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|pwd
parameter_list|)
block|{
name|String
name|userPass
init|=
name|user
operator|+
literal|":"
operator|+
name|pwd
decl_stmt|;
name|String
name|encoded
init|=
name|Base64
operator|.
name|byteArrayToBase64
argument_list|(
name|userPass
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|httpMsg
operator|.
name|setHeader
argument_list|(
operator|new
name|BasicHeader
argument_list|(
literal|"Authorization"
argument_list|,
literal|"Basic "
operator|+
name|encoded
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Added Basic Auth security Header {}"
argument_list|,
name|encoded
argument_list|)
expr_stmt|;
block|}
DECL|method|getRandomReplica
specifier|public
specifier|static
name|Replica
name|getRandomReplica
parameter_list|(
name|DocCollection
name|coll
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Replica
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|coll
operator|.
name|getSlices
argument_list|()
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|replica
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|l
argument_list|,
name|random
argument_list|)
expr_stmt|;
return|return
name|l
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|field|NOT_NULL_PREDICATE
specifier|static
specifier|final
name|Predicate
name|NOT_NULL_PREDICATE
init|=
operator|new
name|Predicate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|test
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|!=
literal|null
return|;
block|}
block|}
decl_stmt|;
comment|//the password is 'SolrRocks'
comment|//this could be generated everytime. But , then we will not know if there is any regression
DECL|field|STD_CONF
specifier|private
specifier|static
specifier|final
name|String
name|STD_CONF
init|=
literal|"{\n"
operator|+
literal|"  'authentication':{\n"
operator|+
literal|"    'class':'solr.BasicAuthPlugin',\n"
operator|+
literal|"    'credentials':{'solr':'orwp2Ghgj39lmnrZOTm7Qtre1VqHFDfwAEzr0ApbN3Y= Ju5osoAqOX8iafhWpPP01E5P+sg8tK8tHON7rCYZRRw='}},\n"
operator|+
literal|"  'authorization':{\n"
operator|+
literal|"    'class':'solr.RuleBasedAuthorizationPlugin',\n"
operator|+
literal|"    'user-role':{'solr':'admin'},\n"
operator|+
literal|"    'permissions':[{'name':'security-edit','role':'admin'}]}}"
decl_stmt|;
block|}
end_class
end_unit
