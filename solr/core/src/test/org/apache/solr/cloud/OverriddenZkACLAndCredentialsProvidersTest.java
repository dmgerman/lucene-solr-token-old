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
name|common
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
name|common
operator|.
name|cloud
operator|.
name|DefaultZkACLProvider
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
name|DefaultZkCredentialsProvider
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
name|VMParamsAllAndReadonlyDigestZkACLProvider
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
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
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
name|ZkACLProvider
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
name|ZkCredentialsProvider
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooDefs
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|ACL
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Id
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|auth
operator|.
name|DigestAuthenticationProvider
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
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
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
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
name|Collection
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|OverriddenZkACLAndCredentialsProvidersTest
specifier|public
class|class
name|OverriddenZkACLAndCredentialsProvidersTest
extends|extends
name|SolrTestCaseJ4
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
DECL|field|DATA_ENCODING
specifier|private
specifier|static
specifier|final
name|Charset
name|DATA_ENCODING
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
DECL|field|zkServer
specifier|protected
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|zkDir
specifier|protected
name|String
name|zkDir
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|)
expr_stmt|;
block|}
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
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|createTempDir
argument_list|()
expr_stmt|;
name|zkDir
operator|=
name|createTempDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"ZooKeeper dataDir:"
operator|+
name|zkDir
argument_list|)
expr_stmt|;
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
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClientFactoryUsingCompletelyNewProviders
argument_list|(
literal|"connectAndAllACLUsername"
argument_list|,
literal|"connectAndAllACLPassword"
argument_list|,
literal|"readonlyACLUsername"
argument_list|,
literal|"readonlyACLPassword"
argument_list|)
operator|.
name|getSolrZkClient
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
name|makePath
argument_list|(
literal|"/solr"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClientFactoryUsingCompletelyNewProviders
argument_list|(
literal|"connectAndAllACLUsername"
argument_list|,
literal|"connectAndAllACLPassword"
argument_list|,
literal|"readonlyACLUsername"
argument_list|,
literal|"readonlyACLPassword"
argument_list|)
operator|.
name|getSolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|create
argument_list|(
literal|"/protectedCreateNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/protectedMakePathNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClientFactoryUsingCompletelyNewProviders
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getSolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|addAuthInfo
argument_list|(
literal|"digest"
argument_list|,
operator|(
literal|"connectAndAllACLUsername:connectAndAllACLPassword"
operator|)
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|create
argument_list|(
literal|"/unprotectedCreateNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/unprotectedMakePathNode"
argument_list|,
literal|"content"
operator|.
name|getBytes
argument_list|(
name|DATA_ENCODING
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_END "
operator|+
name|getTestName
argument_list|()
argument_list|)
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
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|clearSecuritySystemProperties
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoCredentialsSolrZkClientFactoryUsingCompletelyNewProviders
specifier|public
name|void
name|testNoCredentialsSolrZkClientFactoryUsingCompletelyNewProviders
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClientFactoryUsingCompletelyNewProviders
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getSolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|VMParamsZkACLAndCredentialsProvidersTest
operator|.
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWrongCredentialsSolrZkClientFactoryUsingCompletelyNewProviders
specifier|public
name|void
name|testWrongCredentialsSolrZkClientFactoryUsingCompletelyNewProviders
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClientFactoryUsingCompletelyNewProviders
argument_list|(
literal|"connectAndAllACLUsername"
argument_list|,
literal|"connectAndAllACLPasswordWrong"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getSolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|VMParamsZkACLAndCredentialsProvidersTest
operator|.
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAllCredentialsSolrZkClientFactoryUsingCompletelyNewProviders
specifier|public
name|void
name|testAllCredentialsSolrZkClientFactoryUsingCompletelyNewProviders
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClientFactoryUsingCompletelyNewProviders
argument_list|(
literal|"connectAndAllACLUsername"
argument_list|,
literal|"connectAndAllACLPassword"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getSolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|VMParamsZkACLAndCredentialsProvidersTest
operator|.
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadonlyCredentialsSolrZkClientFactoryUsingCompletelyNewProviders
specifier|public
name|void
name|testReadonlyCredentialsSolrZkClientFactoryUsingCompletelyNewProviders
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClientFactoryUsingCompletelyNewProviders
argument_list|(
literal|"readonlyACLUsername"
argument_list|,
literal|"readonlyACLPassword"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getSolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|VMParamsZkACLAndCredentialsProvidersTest
operator|.
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNoCredentialsSolrZkClientFactoryUsingVMParamsProvidersButWithDifferentVMParamsNames
specifier|public
name|void
name|testNoCredentialsSolrZkClientFactoryUsingVMParamsProvidersButWithDifferentVMParamsNames
parameter_list|()
throws|throws
name|Exception
block|{
name|useNoCredentials
argument_list|()
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClientUsingVMParamsProvidersButWithDifferentVMParamsNames
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|VMParamsZkACLAndCredentialsProvidersTest
operator|.
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWrongCredentialsSolrZkClientFactoryUsingVMParamsProvidersButWithDifferentVMParamsNames
specifier|public
name|void
name|testWrongCredentialsSolrZkClientFactoryUsingVMParamsProvidersButWithDifferentVMParamsNames
parameter_list|()
throws|throws
name|Exception
block|{
name|useWrongCredentials
argument_list|()
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClientUsingVMParamsProvidersButWithDifferentVMParamsNames
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|VMParamsZkACLAndCredentialsProvidersTest
operator|.
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testAllCredentialsSolrZkClientFactoryUsingVMParamsProvidersButWithDifferentVMParamsNames
specifier|public
name|void
name|testAllCredentialsSolrZkClientFactoryUsingVMParamsProvidersButWithDifferentVMParamsNames
parameter_list|()
throws|throws
name|Exception
block|{
name|useAllCredentials
argument_list|()
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClientUsingVMParamsProvidersButWithDifferentVMParamsNames
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|VMParamsZkACLAndCredentialsProvidersTest
operator|.
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReadonlyCredentialsSolrZkClientFactoryUsingVMParamsProvidersButWithDifferentVMParamsNames
specifier|public
name|void
name|testReadonlyCredentialsSolrZkClientFactoryUsingVMParamsProvidersButWithDifferentVMParamsNames
parameter_list|()
throws|throws
name|Exception
block|{
name|useReadonlyCredentials
argument_list|()
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClientUsingVMParamsProvidersButWithDifferentVMParamsNames
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
decl_stmt|;
try|try
block|{
name|VMParamsZkACLAndCredentialsProvidersTest
operator|.
name|doTest
argument_list|(
name|zkClient
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|SolrZkClientFactoryUsingCompletelyNewProviders
specifier|private
class|class
name|SolrZkClientFactoryUsingCompletelyNewProviders
block|{
DECL|field|digestUsername
specifier|final
name|String
name|digestUsername
decl_stmt|;
DECL|field|digestPassword
specifier|final
name|String
name|digestPassword
decl_stmt|;
DECL|field|digestReadonlyUsername
specifier|final
name|String
name|digestReadonlyUsername
decl_stmt|;
DECL|field|digestReadonlyPassword
specifier|final
name|String
name|digestReadonlyPassword
decl_stmt|;
DECL|method|SolrZkClientFactoryUsingCompletelyNewProviders
specifier|public
name|SolrZkClientFactoryUsingCompletelyNewProviders
parameter_list|(
specifier|final
name|String
name|digestUsername
parameter_list|,
specifier|final
name|String
name|digestPassword
parameter_list|,
specifier|final
name|String
name|digestReadonlyUsername
parameter_list|,
specifier|final
name|String
name|digestReadonlyPassword
parameter_list|)
block|{
name|this
operator|.
name|digestUsername
operator|=
name|digestUsername
expr_stmt|;
name|this
operator|.
name|digestPassword
operator|=
name|digestPassword
expr_stmt|;
name|this
operator|.
name|digestReadonlyUsername
operator|=
name|digestReadonlyUsername
expr_stmt|;
name|this
operator|.
name|digestReadonlyPassword
operator|=
name|digestReadonlyPassword
expr_stmt|;
block|}
DECL|method|getSolrZkClient
specifier|public
name|SolrZkClient
name|getSolrZkClient
parameter_list|(
name|String
name|zkServerAddress
parameter_list|,
name|int
name|zkClientTimeout
parameter_list|)
block|{
return|return
operator|new
name|SolrZkClient
argument_list|(
name|zkServerAddress
argument_list|,
name|zkClientTimeout
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|ZkCredentialsProvider
name|createZkCredentialsToAddAutomatically
parameter_list|()
block|{
return|return
operator|new
name|DefaultZkCredentialsProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Collection
argument_list|<
name|ZkCredentials
argument_list|>
name|createCredentials
parameter_list|()
block|{
name|List
argument_list|<
name|ZkCredentials
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|ZkCredentials
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|digestUsername
argument_list|)
operator|&&
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|digestPassword
argument_list|)
condition|)
block|{
try|try
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|ZkCredentials
argument_list|(
literal|"digest"
argument_list|,
operator|(
name|digestUsername
operator|+
literal|":"
operator|+
name|digestPassword
operator|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|ZkACLProvider
name|createZkACLProvider
parameter_list|()
block|{
return|return
operator|new
name|DefaultZkACLProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|ACL
argument_list|>
name|createGlobalACLsToAdd
parameter_list|()
block|{
try|try
block|{
name|List
argument_list|<
name|ACL
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|ACL
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|digestUsername
argument_list|)
operator|&&
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|digestPassword
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|ACL
argument_list|(
name|ZooDefs
operator|.
name|Perms
operator|.
name|ALL
argument_list|,
operator|new
name|Id
argument_list|(
literal|"digest"
argument_list|,
name|DigestAuthenticationProvider
operator|.
name|generateDigest
argument_list|(
name|digestUsername
operator|+
literal|":"
operator|+
name|digestPassword
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|digestReadonlyUsername
argument_list|)
operator|&&
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|digestReadonlyPassword
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|ACL
argument_list|(
name|ZooDefs
operator|.
name|Perms
operator|.
name|READ
argument_list|,
operator|new
name|Id
argument_list|(
literal|"digest"
argument_list|,
name|DigestAuthenticationProvider
operator|.
name|generateDigest
argument_list|(
name|digestReadonlyUsername
operator|+
literal|":"
operator|+
name|digestReadonlyPassword
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|ZooDefs
operator|.
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
DECL|class|SolrZkClientUsingVMParamsProvidersButWithDifferentVMParamsNames
specifier|private
class|class
name|SolrZkClientUsingVMParamsProvidersButWithDifferentVMParamsNames
extends|extends
name|SolrZkClient
block|{
DECL|method|SolrZkClientUsingVMParamsProvidersButWithDifferentVMParamsNames
specifier|public
name|SolrZkClientUsingVMParamsProvidersButWithDifferentVMParamsNames
parameter_list|(
name|String
name|zkServerAddress
parameter_list|,
name|int
name|zkClientTimeout
parameter_list|)
block|{
name|super
argument_list|(
name|zkServerAddress
argument_list|,
name|zkClientTimeout
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createZkCredentialsToAddAutomatically
specifier|protected
name|ZkCredentialsProvider
name|createZkCredentialsToAddAutomatically
parameter_list|()
block|{
return|return
operator|new
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createZkACLProvider
specifier|public
name|ZkACLProvider
name|createZkACLProvider
parameter_list|()
block|{
return|return
operator|new
name|VMParamsAllAndReadonlyDigestZkACLProvider
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|,
literal|"alternative"
operator|+
name|VMParamsAllAndReadonlyDigestZkACLProvider
operator|.
name|DEFAULT_DIGEST_READONLY_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"alternative"
operator|+
name|VMParamsAllAndReadonlyDigestZkACLProvider
operator|.
name|DEFAULT_DIGEST_READONLY_PASSWORD_VM_PARAM_NAME
argument_list|)
return|;
block|}
block|}
DECL|method|useNoCredentials
specifier|public
name|void
name|useNoCredentials
parameter_list|()
block|{
name|clearSecuritySystemProperties
argument_list|()
expr_stmt|;
block|}
DECL|method|useWrongCredentials
specifier|public
name|void
name|useWrongCredentials
parameter_list|()
block|{
name|clearSecuritySystemProperties
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLUsername"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLPasswordWrong"
argument_list|)
expr_stmt|;
block|}
DECL|method|useAllCredentials
specifier|public
name|void
name|useAllCredentials
parameter_list|()
block|{
name|clearSecuritySystemProperties
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLUsername"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLPassword"
argument_list|)
expr_stmt|;
block|}
DECL|method|useReadonlyCredentials
specifier|public
name|void
name|useReadonlyCredentials
parameter_list|()
block|{
name|clearSecuritySystemProperties
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"readonlyACLUsername"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|,
literal|"readonlyACLPassword"
argument_list|)
expr_stmt|;
block|}
DECL|method|setSecuritySystemProperties
specifier|public
name|void
name|setSecuritySystemProperties
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLUsername"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|,
literal|"connectAndAllACLPassword"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsAllAndReadonlyDigestZkACLProvider
operator|.
name|DEFAULT_DIGEST_READONLY_USERNAME_VM_PARAM_NAME
argument_list|,
literal|"readonlyACLUsername"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsAllAndReadonlyDigestZkACLProvider
operator|.
name|DEFAULT_DIGEST_READONLY_PASSWORD_VM_PARAM_NAME
argument_list|,
literal|"readonlyACLPassword"
argument_list|)
expr_stmt|;
block|}
DECL|method|clearSecuritySystemProperties
specifier|public
name|void
name|clearSecuritySystemProperties
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_USERNAME_VM_PARAM_NAME
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsSingleSetCredentialsDigestZkCredentialsProvider
operator|.
name|DEFAULT_DIGEST_PASSWORD_VM_PARAM_NAME
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsAllAndReadonlyDigestZkACLProvider
operator|.
name|DEFAULT_DIGEST_READONLY_USERNAME_VM_PARAM_NAME
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"alternative"
operator|+
name|VMParamsAllAndReadonlyDigestZkACLProvider
operator|.
name|DEFAULT_DIGEST_READONLY_PASSWORD_VM_PARAM_NAME
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
