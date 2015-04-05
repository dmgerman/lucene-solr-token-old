begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|text
operator|.
name|SimpleDateFormat
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
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|lucene
operator|.
name|index
operator|.
name|IndexCommit
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
name|FSDirectory
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
name|NoLockFactory
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
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|core
operator|.
name|DirectoryFactory
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
name|DirectoryFactory
operator|.
name|DirContext
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
name|SolrCore
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
comment|/**  *<p> Provides functionality equivalent to the snapshooter script</p>  * This is no longer used in standard replication.  *  *  * @since solr 1.4  */
end_comment
begin_class
DECL|class|SnapShooter
specifier|public
class|class
name|SnapShooter
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
name|SnapShooter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|snapDir
specifier|private
name|String
name|snapDir
init|=
literal|null
decl_stmt|;
DECL|field|solrCore
specifier|private
name|SolrCore
name|solrCore
decl_stmt|;
DECL|field|snapshotName
specifier|private
name|String
name|snapshotName
init|=
literal|null
decl_stmt|;
DECL|field|directoryName
specifier|private
name|String
name|directoryName
init|=
literal|null
decl_stmt|;
DECL|field|snapShotDir
specifier|private
name|File
name|snapShotDir
init|=
literal|null
decl_stmt|;
DECL|method|SnapShooter
specifier|public
name|SnapShooter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|location
parameter_list|,
name|String
name|snapshotName
parameter_list|)
block|{
name|solrCore
operator|=
name|core
expr_stmt|;
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
name|snapDir
operator|=
name|core
operator|.
name|getDataDir
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|snapDir
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
name|location
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|snapshotName
operator|=
name|snapshotName
expr_stmt|;
if|if
condition|(
name|snapshotName
operator|!=
literal|null
condition|)
block|{
name|directoryName
operator|=
literal|"snapshot."
operator|+
name|snapshotName
expr_stmt|;
block|}
else|else
block|{
name|SimpleDateFormat
name|fmt
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|DATE_FMT
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|directoryName
operator|=
literal|"snapshot."
operator|+
name|fmt
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createSnapAsync
name|void
name|createSnapAsync
parameter_list|(
specifier|final
name|IndexCommit
name|indexCommit
parameter_list|,
specifier|final
name|int
name|numberToKeep
parameter_list|,
specifier|final
name|ReplicationHandler
name|replicationHandler
parameter_list|)
block|{
name|replicationHandler
operator|.
name|core
operator|.
name|getDeletionPolicy
argument_list|()
operator|.
name|saveCommitPoint
argument_list|(
name|indexCommit
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|snapshotName
operator|!=
literal|null
condition|)
block|{
name|createSnapshot
argument_list|(
name|indexCommit
argument_list|,
name|replicationHandler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|createSnapshot
argument_list|(
name|indexCommit
argument_list|,
name|replicationHandler
argument_list|)
expr_stmt|;
name|deleteOldBackups
argument_list|(
name|numberToKeep
argument_list|)
expr_stmt|;
block|}
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|validateDeleteSnapshot
specifier|public
name|void
name|validateDeleteSnapshot
parameter_list|()
block|{
name|boolean
name|dirFound
init|=
literal|false
decl_stmt|;
name|File
index|[]
name|files
init|=
operator|new
name|File
argument_list|(
name|snapDir
argument_list|)
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|files
control|)
block|{
if|if
condition|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"snapshot."
operator|+
name|snapshotName
argument_list|)
condition|)
block|{
name|dirFound
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|dirFound
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Snapshot cannot be found in directory: "
operator|+
name|snapDir
argument_list|)
throw|;
block|}
block|}
DECL|method|deleteSnapAsync
specifier|protected
name|void
name|deleteSnapAsync
parameter_list|(
specifier|final
name|ReplicationHandler
name|replicationHandler
parameter_list|)
block|{
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|deleteNamedSnapshot
argument_list|(
name|replicationHandler
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|validateCreateSnapshot
name|void
name|validateCreateSnapshot
parameter_list|()
throws|throws
name|IOException
block|{
name|snapShotDir
operator|=
operator|new
name|File
argument_list|(
name|snapDir
argument_list|,
name|directoryName
argument_list|)
expr_stmt|;
if|if
condition|(
name|snapShotDir
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Snapshot directory already exists: "
operator|+
name|snapShotDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|snapShotDir
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unable to create snapshot directory: "
operator|+
name|snapShotDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|createSnapshot
name|void
name|createSnapshot
parameter_list|(
specifier|final
name|IndexCommit
name|indexCommit
parameter_list|,
name|ReplicationHandler
name|replicationHandler
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating backup snapshot "
operator|+
operator|(
name|snapshotName
operator|==
literal|null
condition|?
literal|"<not named>"
else|:
name|snapshotName
operator|)
operator|+
literal|" at "
operator|+
name|snapDir
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|details
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"startTime"
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|files
init|=
name|indexCommit
operator|.
name|getFileNames
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|solrCore
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|solrCore
operator|.
name|getIndexDir
argument_list|()
argument_list|,
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|solrCore
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
decl_stmt|;
try|try
block|{
name|copyFiles
argument_list|(
name|dir
argument_list|,
name|files
argument_list|,
name|snapShotDir
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|solrCore
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|details
operator|.
name|add
argument_list|(
literal|"fileCount"
argument_list|,
name|files
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"success"
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"snapshotCompletedAt"
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"snapshotName"
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Done creating backup snapshot: "
operator|+
operator|(
name|snapshotName
operator|==
literal|null
condition|?
literal|"<not named>"
else|:
name|snapshotName
operator|)
operator|+
literal|" at "
operator|+
name|snapDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|IndexFetcher
operator|.
name|delTree
argument_list|(
name|snapShotDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while creating snapshot"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"snapShootException"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|replicationHandler
operator|.
name|core
operator|.
name|getDeletionPolicy
argument_list|()
operator|.
name|releaseCommitPoint
argument_list|(
name|indexCommit
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
name|replicationHandler
operator|.
name|snapShootDetails
operator|=
name|details
expr_stmt|;
block|}
block|}
DECL|method|deleteOldBackups
specifier|private
name|void
name|deleteOldBackups
parameter_list|(
name|int
name|numberToKeep
parameter_list|)
block|{
name|File
index|[]
name|files
init|=
operator|new
name|File
argument_list|(
name|snapDir
argument_list|)
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|OldBackupDirectory
argument_list|>
name|dirs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|files
control|)
block|{
name|OldBackupDirectory
name|obd
init|=
operator|new
name|OldBackupDirectory
argument_list|(
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
name|obd
operator|.
name|dir
operator|!=
literal|null
condition|)
block|{
name|dirs
operator|.
name|add
argument_list|(
name|obd
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|numberToKeep
operator|>
name|dirs
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
return|return;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
for|for
control|(
name|OldBackupDirectory
name|dir
range|:
name|dirs
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
name|numberToKeep
condition|)
block|{
name|IndexFetcher
operator|.
name|delTree
argument_list|(
name|dir
operator|.
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|deleteNamedSnapshot
specifier|protected
name|void
name|deleteNamedSnapshot
parameter_list|(
name|ReplicationHandler
name|replicationHandler
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting snapshot: "
operator|+
name|snapshotName
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|details
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|isSuccess
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|snapDir
argument_list|,
literal|"snapshot."
operator|+
name|snapshotName
argument_list|)
decl_stmt|;
name|isSuccess
operator|=
name|IndexFetcher
operator|.
name|delTree
argument_list|(
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSuccess
condition|)
block|{
name|details
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"success"
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"snapshotDeletedAt"
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|details
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"Unable to delete snapshot: "
operator|+
name|snapshotName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to delete snapshot: "
operator|+
name|snapshotName
argument_list|)
expr_stmt|;
block|}
name|replicationHandler
operator|.
name|snapShootDetails
operator|=
name|details
expr_stmt|;
block|}
DECL|field|DATE_FMT
specifier|public
specifier|static
specifier|final
name|String
name|DATE_FMT
init|=
literal|"yyyyMMddHHmmssSSS"
decl_stmt|;
DECL|method|copyFiles
specifier|private
specifier|static
name|void
name|copyFiles
parameter_list|(
name|Directory
name|sourceDir
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|,
name|File
name|destDir
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|FSDirectory
name|dir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|destDir
operator|.
name|toPath
argument_list|()
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|)
init|)
block|{
for|for
control|(
name|String
name|indexFile
range|:
name|files
control|)
block|{
name|dir
operator|.
name|copyFrom
argument_list|(
name|sourceDir
argument_list|,
name|indexFile
argument_list|,
name|indexFile
argument_list|,
name|DirectoryFactory
operator|.
name|IOCONTEXT_NO_CACHE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
