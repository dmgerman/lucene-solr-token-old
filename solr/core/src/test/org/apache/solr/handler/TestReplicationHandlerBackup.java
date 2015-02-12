begin_unit
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|IOUtils
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
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|MatchAllDocsQuery
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
name|search
operator|.
name|TopDocs
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|SimpleFSDirectory
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
name|TestUtil
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
name|SolrJettyTestBase
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
name|util
operator|.
name|FileUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
annotation|@
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
comment|// Currently unknown why SSL does not work with this test
DECL|class|TestReplicationHandlerBackup
specifier|public
class|class
name|TestReplicationHandlerBackup
extends|extends
name|SolrJettyTestBase
block|{
DECL|field|masterJetty
name|JettySolrRunner
name|masterJetty
decl_stmt|;
DECL|field|master
name|TestReplicationHandler
operator|.
name|SolrInstance
name|master
init|=
literal|null
decl_stmt|;
DECL|field|masterClient
name|SolrClient
name|masterClient
decl_stmt|;
DECL|field|CONF_DIR
specifier|private
specifier|static
specifier|final
name|String
name|CONF_DIR
init|=
literal|"solr"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"collection1"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
operator|+
name|File
operator|.
name|separator
decl_stmt|;
DECL|field|context
specifier|private
specifier|static
name|String
name|context
init|=
literal|"/solr"
decl_stmt|;
DECL|field|addNumberToKeepInRequest
name|boolean
name|addNumberToKeepInRequest
init|=
literal|true
decl_stmt|;
DECL|field|backupKeepParamName
name|String
name|backupKeepParamName
init|=
name|ReplicationHandler
operator|.
name|NUMBER_BACKUPS_TO_KEEP_REQUEST_PARAM
decl_stmt|;
DECL|method|createJetty
specifier|private
specifier|static
name|JettySolrRunner
name|createJetty
parameter_list|(
name|TestReplicationHandler
operator|.
name|SolrInstance
name|instance
parameter_list|)
throws|throws
name|Exception
block|{
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|instance
operator|.
name|getHomeDir
argument_list|()
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|jetty
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|instance
operator|.
name|getHomeDir
argument_list|()
argument_list|,
literal|"/solr"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|jetty
operator|.
name|setDataDir
argument_list|(
name|instance
operator|.
name|getDataDir
argument_list|()
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|jetty
return|;
block|}
DECL|method|createNewSolrClient
specifier|private
specifier|static
name|SolrClient
name|createNewSolrClient
parameter_list|(
name|int
name|port
parameter_list|)
block|{
try|try
block|{
comment|// setup the client...
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|buildUrl
argument_list|(
name|port
argument_list|,
name|context
argument_list|)
operator|+
literal|"/"
operator|+
name|DEFAULT_TEST_CORENAME
argument_list|)
decl_stmt|;
name|client
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|client
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|client
operator|.
name|setDefaultMaxConnectionsPerHost
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|client
operator|.
name|setMaxTotalConnections
argument_list|(
literal|100
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
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
name|String
name|configFile
init|=
literal|"solrconfig-master1.xml"
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|configFile
operator|=
literal|"solrconfig-master1-keepOneBackup.xml"
expr_stmt|;
name|addNumberToKeepInRequest
operator|=
literal|false
expr_stmt|;
name|backupKeepParamName
operator|=
name|ReplicationHandler
operator|.
name|NUMBER_BACKUPS_TO_KEEP_INIT_PARAM
expr_stmt|;
block|}
name|master
operator|=
operator|new
name|TestReplicationHandler
operator|.
name|SolrInstance
argument_list|(
name|createTempDir
argument_list|(
literal|"solr-instance"
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|,
literal|"master"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|master
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|master
operator|.
name|copyConfigFile
argument_list|(
name|CONF_DIR
operator|+
name|configFile
argument_list|,
literal|"solrconfig.xml"
argument_list|)
expr_stmt|;
name|masterJetty
operator|=
name|createJetty
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|masterClient
operator|=
name|createNewSolrClient
argument_list|(
name|masterJetty
operator|.
name|getLocalPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|masterClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|masterClient
operator|=
literal|null
expr_stmt|;
name|masterJetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|masterJetty
operator|=
literal|null
expr_stmt|;
name|master
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBackupOnCommit
specifier|public
name|void
name|testBackupOnCommit
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Index
name|int
name|nDocs
init|=
name|indexDocs
argument_list|()
decl_stmt|;
comment|//Confirm if completed
name|CheckBackupStatus
name|checkBackupStatus
init|=
operator|new
name|CheckBackupStatus
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|masterClient
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|checkBackupStatus
operator|.
name|success
condition|)
block|{
name|checkBackupStatus
operator|.
name|fetchStatus
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|//Validate
name|Path
name|snapDir
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|master
operator|.
name|getDataDir
argument_list|()
argument_list|)
argument_list|,
literal|"snapshot*"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|verify
argument_list|(
name|snapDir
argument_list|,
name|nDocs
argument_list|)
expr_stmt|;
block|}
DECL|method|verify
specifier|private
name|void
name|verify
parameter_list|(
name|Path
name|backup
parameter_list|,
name|int
name|nDocs
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|Directory
name|dir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|backup
argument_list|)
init|)
block|{
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nDocs
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|indexDocs
specifier|private
name|int
name|indexDocs
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|int
name|nDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|masterClient
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
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
name|nDocs
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
literal|"name = "
operator|+
name|i
argument_list|)
expr_stmt|;
name|masterClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|masterClient
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
name|nDocs
return|;
block|}
annotation|@
name|Test
DECL|method|doTestBackup
specifier|public
name|void
name|doTestBackup
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|nDocs
init|=
name|indexDocs
argument_list|()
decl_stmt|;
name|Path
index|[]
name|snapDir
init|=
operator|new
name|Path
index|[
literal|2
index|]
decl_stmt|;
name|boolean
name|namedBackup
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|String
name|firstBackupTimestamp
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|backupNames
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|namedBackup
condition|)
block|{
name|backupNames
operator|=
operator|new
name|String
index|[
literal|2
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|BackupCommand
name|backupCommand
decl_stmt|;
specifier|final
name|String
name|backupName
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|namedBackup
condition|)
block|{
name|backupCommand
operator|=
operator|new
name|BackupCommand
argument_list|(
name|addNumberToKeepInRequest
argument_list|,
name|backupKeepParamName
argument_list|,
name|ReplicationHandler
operator|.
name|CMD_BACKUP
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|backupCommand
operator|=
operator|new
name|BackupCommand
argument_list|(
name|backupName
argument_list|,
name|ReplicationHandler
operator|.
name|CMD_BACKUP
argument_list|)
expr_stmt|;
name|backupNames
index|[
name|i
index|]
operator|=
name|backupName
expr_stmt|;
block|}
name|backupCommand
operator|.
name|runCommand
argument_list|()
expr_stmt|;
if|if
condition|(
name|backupCommand
operator|.
name|fail
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
name|backupCommand
operator|.
name|fail
argument_list|)
expr_stmt|;
block|}
name|CheckBackupStatus
name|checkBackupStatus
init|=
operator|new
name|CheckBackupStatus
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|masterClient
argument_list|,
name|firstBackupTimestamp
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|checkBackupStatus
operator|.
name|success
condition|)
block|{
name|checkBackupStatus
operator|.
name|fetchStatus
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|firstBackupTimestamp
operator|=
name|checkBackupStatus
operator|.
name|backupTimestamp
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|namedBackup
condition|)
block|{
name|snapDir
index|[
name|i
index|]
operator|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|master
operator|.
name|getDataDir
argument_list|()
argument_list|)
argument_list|,
literal|"snapshot*"
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|snapDir
index|[
name|i
index|]
operator|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|master
operator|.
name|getDataDir
argument_list|()
argument_list|)
argument_list|,
literal|"snapshot."
operator|+
name|backupName
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|verify
argument_list|(
name|snapDir
index|[
name|i
index|]
argument_list|,
name|nDocs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|namedBackup
operator|&&
name|Files
operator|.
name|exists
argument_list|(
name|snapDir
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"The first backup should have been cleaned up because "
operator|+
name|backupKeepParamName
operator|+
literal|" was set to 1."
argument_list|)
expr_stmt|;
block|}
comment|//Test Deletion of named backup
if|if
condition|(
name|namedBackup
condition|)
block|{
name|testDeleteNamedBackup
argument_list|(
name|backupNames
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDeleteNamedBackup
specifier|private
name|void
name|testDeleteNamedBackup
parameter_list|(
name|String
name|backupNames
index|[]
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|String
name|lastTimestamp
init|=
literal|null
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|BackupCommand
name|deleteBackupCommand
init|=
operator|new
name|BackupCommand
argument_list|(
name|backupNames
index|[
name|i
index|]
argument_list|,
name|ReplicationHandler
operator|.
name|CMD_DELETE_BACKUP
argument_list|)
decl_stmt|;
name|deleteBackupCommand
operator|.
name|runCommand
argument_list|()
expr_stmt|;
name|CheckDeleteBackupStatus
name|checkDeleteBackupStatus
init|=
operator|new
name|CheckDeleteBackupStatus
argument_list|(
name|backupNames
index|[
name|i
index|]
argument_list|,
name|lastTimestamp
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|boolean
name|success
init|=
name|checkDeleteBackupStatus
operator|.
name|fetchStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|lastTimestamp
operator|=
name|checkDeleteBackupStatus
operator|.
name|lastTimestamp
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|//make the timestamp change
block|}
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deleteBackupCommand
operator|.
name|fail
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
name|deleteBackupCommand
operator|.
name|fail
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|BackupCommand
specifier|private
class|class
name|BackupCommand
block|{
DECL|field|fail
name|String
name|fail
init|=
literal|null
decl_stmt|;
DECL|field|addNumberToKeepInRequest
specifier|final
name|boolean
name|addNumberToKeepInRequest
decl_stmt|;
DECL|field|backupKeepParamName
name|String
name|backupKeepParamName
decl_stmt|;
DECL|field|backupName
name|String
name|backupName
decl_stmt|;
DECL|field|cmd
name|String
name|cmd
decl_stmt|;
DECL|method|BackupCommand
name|BackupCommand
parameter_list|(
name|boolean
name|addNumberToKeepInRequest
parameter_list|,
name|String
name|backupKeepParamName
parameter_list|,
name|String
name|command
parameter_list|)
block|{
name|this
operator|.
name|addNumberToKeepInRequest
operator|=
name|addNumberToKeepInRequest
expr_stmt|;
name|this
operator|.
name|backupKeepParamName
operator|=
name|backupKeepParamName
expr_stmt|;
name|this
operator|.
name|cmd
operator|=
name|command
expr_stmt|;
block|}
DECL|method|BackupCommand
name|BackupCommand
parameter_list|(
name|String
name|backupName
parameter_list|,
name|String
name|command
parameter_list|)
block|{
name|this
operator|.
name|backupName
operator|=
name|backupName
expr_stmt|;
name|addNumberToKeepInRequest
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|cmd
operator|=
name|command
expr_stmt|;
block|}
DECL|method|runCommand
specifier|public
name|void
name|runCommand
parameter_list|()
block|{
name|String
name|masterUrl
decl_stmt|;
if|if
condition|(
name|backupName
operator|!=
literal|null
condition|)
block|{
name|masterUrl
operator|=
name|buildUrl
argument_list|(
name|masterJetty
operator|.
name|getLocalPort
argument_list|()
argument_list|,
name|context
argument_list|)
operator|+
literal|"/"
operator|+
name|DEFAULT_TEST_CORENAME
operator|+
literal|"/replication?command="
operator|+
name|cmd
operator|+
literal|"&name="
operator|+
name|backupName
expr_stmt|;
block|}
else|else
block|{
name|masterUrl
operator|=
name|buildUrl
argument_list|(
name|masterJetty
operator|.
name|getLocalPort
argument_list|()
argument_list|,
name|context
argument_list|)
operator|+
literal|"/"
operator|+
name|DEFAULT_TEST_CORENAME
operator|+
literal|"/replication?command="
operator|+
name|cmd
operator|+
operator|(
name|addNumberToKeepInRequest
condition|?
literal|"&"
operator|+
name|backupKeepParamName
operator|+
literal|"=1"
else|:
literal|""
operator|)
expr_stmt|;
block|}
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|masterUrl
argument_list|)
decl_stmt|;
name|stream
operator|=
name|url
operator|.
name|openStream
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
operator|=
name|e
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|CheckDeleteBackupStatus
specifier|private
class|class
name|CheckDeleteBackupStatus
block|{
DECL|field|response
name|String
name|response
init|=
literal|null
decl_stmt|;
DECL|field|backupName
specifier|private
name|String
name|backupName
decl_stmt|;
DECL|field|p
specifier|final
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"<str name=\"snapshotDeletedAt\">(.*?)</str>"
argument_list|)
decl_stmt|;
DECL|field|lastTimestamp
name|String
name|lastTimestamp
decl_stmt|;
DECL|method|CheckDeleteBackupStatus
specifier|private
name|CheckDeleteBackupStatus
parameter_list|(
name|String
name|backupName
parameter_list|,
name|String
name|lastTimestamp
parameter_list|)
block|{
name|this
operator|.
name|backupName
operator|=
name|backupName
expr_stmt|;
name|this
operator|.
name|lastTimestamp
operator|=
name|lastTimestamp
expr_stmt|;
block|}
DECL|method|fetchStatus
specifier|public
name|boolean
name|fetchStatus
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|masterUrl
init|=
name|buildUrl
argument_list|(
name|masterJetty
operator|.
name|getLocalPort
argument_list|()
argument_list|,
name|context
argument_list|)
operator|+
literal|"/"
operator|+
name|DEFAULT_TEST_CORENAME
operator|+
literal|"/replication?command="
operator|+
name|ReplicationHandler
operator|.
name|CMD_DETAILS
decl_stmt|;
name|URL
name|url
decl_stmt|;
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|url
operator|=
operator|new
name|URL
argument_list|(
name|masterUrl
argument_list|)
expr_stmt|;
name|stream
operator|=
name|url
operator|.
name|openStream
argument_list|()
expr_stmt|;
name|response
operator|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|contains
argument_list|(
literal|"<str name=\"status\">success</str>"
argument_list|)
condition|)
block|{
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
operator|&&
operator|(
name|lastTimestamp
operator|==
literal|null
operator|||
operator|!
name|lastTimestamp
operator|.
name|equals
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|)
condition|)
block|{
name|lastTimestamp
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|response
operator|.
name|contains
argument_list|(
literal|"<str name=\"status\">Unable to delete snapshot: "
operator|+
name|backupName
operator|+
literal|"</str>"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
empty_stmt|;
block|}
block|}
end_class
end_unit
