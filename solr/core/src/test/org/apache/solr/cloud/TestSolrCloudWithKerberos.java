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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
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
name|Arrays
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|Charsets
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
name|io
operator|.
name|FileUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|minikdc
operator|.
name|MiniKdc
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
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|Krb5HttpClientConfigurer
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
name|zookeeper
operator|.
name|CreateMode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
annotation|@
name|Ignore
annotation|@
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
annotation|@
name|LuceneTestCase
operator|.
name|Slow
DECL|class|TestSolrCloudWithKerberos
specifier|public
class|class
name|TestSolrCloudWithKerberos
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|TIMEOUT
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
literal|10000
decl_stmt|;
DECL|field|kdc
specifier|private
name|MiniKdc
name|kdc
decl_stmt|;
DECL|field|brokenLocales
specifier|protected
specifier|final
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|brokenLocales
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"th_TH_TH_#u-nu-thai"
argument_list|,
literal|"ja_JP_JP_#u-ca-japanese"
argument_list|,
literal|"hi_IN"
argument_list|)
decl_stmt|;
DECL|field|originalConfig
name|Configuration
name|originalConfig
init|=
name|Configuration
operator|.
name|getConfiguration
argument_list|()
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
comment|//SSLTestConfig.setSSLSystemProperties();
if|if
condition|(
name|brokenLocales
operator|.
name|contains
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|Locale
operator|.
name|setDefault
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
block|}
comment|// Use just one jetty
name|this
operator|.
name|sliceCount
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|fixShardCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|setupMiniKdc
argument_list|()
expr_stmt|;
comment|//useExternalKdc();
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
try|try
init|(
name|ZkStateReader
name|zkStateReader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
name|TIMEOUT
argument_list|)
init|)
block|{
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|create
argument_list|(
name|ZkStateReader
operator|.
name|SOLR_SECURITY_CONF_PATH
argument_list|,
literal|"{\"authentication\":{\"class\":\"org.apache.solr.security.KerberosPlugin\"}}"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setupMiniKdc
specifier|private
name|void
name|setupMiniKdc
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.jaas.debug"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|String
name|kdcDir
init|=
name|createTempDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"minikdc"
decl_stmt|;
name|kdc
operator|=
name|KerberosTestUtil
operator|.
name|getKdc
argument_list|(
operator|new
name|File
argument_list|(
name|kdcDir
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|keytabFile
init|=
operator|new
name|File
argument_list|(
name|kdcDir
argument_list|,
literal|"keytabs"
argument_list|)
decl_stmt|;
name|String
name|solrServerPrincipal
init|=
literal|"HTTP/127.0.0.1"
decl_stmt|;
name|String
name|solrClientPrincipal
init|=
literal|"solr"
decl_stmt|;
name|kdc
operator|.
name|start
argument_list|()
expr_stmt|;
name|kdc
operator|.
name|createPrincipal
argument_list|(
name|keytabFile
argument_list|,
name|solrServerPrincipal
argument_list|,
name|solrClientPrincipal
argument_list|)
expr_stmt|;
name|String
name|jaas
init|=
literal|"SolrClient {\n"
operator|+
literal|" com.sun.security.auth.module.Krb5LoginModule required\n"
operator|+
literal|" useKeyTab=true\n"
operator|+
literal|" keyTab=\""
operator|+
name|keytabFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"\"\n"
operator|+
literal|" storeKey=true\n"
operator|+
literal|" useTicketCache=false\n"
operator|+
literal|" doNotPrompt=true\n"
operator|+
literal|" debug=true\n"
operator|+
literal|" principal=\""
operator|+
name|solrClientPrincipal
operator|+
literal|"\";\n"
operator|+
literal|"};"
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|KerberosTestUtil
operator|.
name|JaasConfiguration
argument_list|(
name|solrClientPrincipal
argument_list|,
name|keytabFile
argument_list|,
literal|"SolrClient"
argument_list|)
decl_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|jaasFilePath
init|=
name|kdcDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"jaas-client.conf"
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
operator|new
name|File
argument_list|(
name|jaasFilePath
argument_list|)
argument_list|,
name|jaas
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|,
name|jaasFilePath
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.jaas.appname"
argument_list|,
literal|"SolrClient"
argument_list|)
expr_stmt|;
comment|// Get this app name from the jaas file
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.cookie.domain"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.principal"
argument_list|,
name|solrServerPrincipal
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.keytab"
argument_list|,
name|keytabFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// Extracts 127.0.0.1 from HTTP/127.0.0.1@EXAMPLE.COM
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.name.rules"
argument_list|,
literal|"RULE:[1:$1@$0](.*EXAMPLE.COM)s/@.*//"
operator|+
literal|"\nRULE:[2:$2@$0](.*EXAMPLE.COM)s/@.*//"
operator|+
literal|"\nDEFAULT"
argument_list|)
expr_stmt|;
comment|// more debugging, if needed
comment|/*System.setProperty("sun.security.jgss.debug", "true");     System.setProperty("sun.security.krb5.debug", "true");     System.setProperty("sun.security.jgss.debug", "true");     System.setProperty("java.security.debug", "logincontext,policy,scl,gssloginconfig");*/
block|}
comment|//This method can be used for debugging i.e. to use an external KDC for the test.
DECL|method|useExternalKdc
specifier|public
specifier|static
name|void
name|useExternalKdc
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|jaas
init|=
literal|"SolrClient {\n"
operator|+
literal|"  com.sun.security.auth.module.Krb5LoginModule required\n"
operator|+
literal|"  useKeyTab=true\n"
operator|+
literal|"  keyTab=\"/opt/keytabs/solr.keytab\"\n"
operator|+
literal|"  storeKey=true\n"
operator|+
literal|" doNotPrompt=true\n"
operator|+
literal|"  useTicketCache=false\n"
operator|+
literal|"  debug=true\n"
operator|+
literal|"  principal=\"HTTP/127.0.0.1\";\n"
operator|+
literal|"};\n"
decl_stmt|;
name|String
name|tmpDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
operator|new
name|File
argument_list|(
name|tmpDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"jaas.conf"
argument_list|)
argument_list|,
name|jaas
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|KerberosTestUtil
operator|.
name|JaasConfiguration
argument_list|(
literal|"solr"
argument_list|,
operator|new
name|File
argument_list|(
literal|"/opt/keytabs/solr.keytab"
argument_list|)
argument_list|,
literal|"SolrClient"
argument_list|)
decl_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|,
name|tmpDir
operator|+
name|File
operator|.
name|separator
operator|+
literal|"jaas.conf"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.jaas.appname"
argument_list|,
literal|"SolrClient"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.cookie.domain"
argument_list|,
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.principal"
argument_list|,
literal|"HTTP/127.0.0.1@EXAMPLE.COM"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.kerberos.keytab"
argument_list|,
literal|"/opt/keytabs/solr.keytab"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"authenticationPlugin"
argument_list|,
literal|"org.apache.solr.security.KerberosPlugin"
argument_list|)
expr_stmt|;
comment|// Extracts 127.0.0.1 from HTTP/127.0.0.1@EXAMPLE.COM
comment|//System.setProperty("solr.kerberos.name.rules", "RULE:[2:$2@$0](.*EXAMPLE.COM)s/@.*//");
block|}
annotation|@
name|Test
DECL|method|testKerberizedSolr
specifier|public
name|void
name|testKerberizedSolr
parameter_list|()
throws|throws
name|Exception
block|{
name|CloudSolrClient
name|testClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|HttpClientUtil
operator|.
name|setConfigurer
argument_list|(
operator|new
name|Krb5HttpClientConfigurer
argument_list|()
argument_list|)
expr_stmt|;
name|testClient
operator|=
name|createCloudClient
argument_list|(
literal|"testcollection"
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|create
operator|.
name|setCollectionName
argument_list|(
literal|"testcollection"
argument_list|)
expr_stmt|;
name|create
operator|.
name|setConfigName
argument_list|(
literal|"conf1"
argument_list|)
expr_stmt|;
name|create
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|create
operator|.
name|setReplicationFactor
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|create
operator|.
name|process
argument_list|(
name|testClient
argument_list|)
expr_stmt|;
name|waitForCollection
argument_list|(
name|testClient
operator|.
name|getZkStateReader
argument_list|()
argument_list|,
literal|"testcollection"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|List
name|list
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|List
argument_list|()
decl_stmt|;
name|CollectionAdminResponse
name|response
init|=
name|list
operator|.
name|process
argument_list|(
name|testClient
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected to see testcollection but it doesn't exist"
argument_list|,
operator|(
operator|(
name|ArrayList
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"collections"
argument_list|)
operator|)
operator|.
name|contains
argument_list|(
literal|"testcollection"
argument_list|)
argument_list|)
expr_stmt|;
name|testClient
operator|.
name|setDefaultCollection
argument_list|(
literal|"testcollection"
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|testClient
argument_list|,
name|params
argument_list|(
literal|"commit"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
name|getDoc
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|QueryResponse
name|queryResponse
init|=
name|testClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected #docs and actual isn't the same"
argument_list|,
literal|1
argument_list|,
name|queryResponse
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|testClient
operator|!=
literal|null
condition|)
name|testClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|System
operator|.
name|clearProperty
argument_list|(
literal|"java.security.auth.login.config"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.kerberos.jaas.appname"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.cookie.domain"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.kerberos.principal"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.kerberos.keytab"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.jaas.debug"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.kerberos.name.rules"
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|setConfiguration
argument_list|(
name|originalConfig
argument_list|)
expr_stmt|;
if|if
condition|(
name|kdc
operator|!=
literal|null
condition|)
block|{
name|kdc
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|//SSLTestConfig.clearSSLSystemProperties();
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
