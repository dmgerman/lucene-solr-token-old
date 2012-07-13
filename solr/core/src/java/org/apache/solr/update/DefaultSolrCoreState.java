begin_unit
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|IOException
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
name|IndexWriter
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
name|RecoveryStrategy
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
name|CoreContainer
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
begin_class
DECL|class|DefaultSolrCoreState
specifier|public
specifier|final
class|class
name|DefaultSolrCoreState
extends|extends
name|SolrCoreState
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
name|DefaultSolrCoreState
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SKIP_AUTO_RECOVERY
specifier|private
specifier|final
name|boolean
name|SKIP_AUTO_RECOVERY
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|)
decl_stmt|;
DECL|field|recoveryLock
specifier|private
specifier|final
name|Object
name|recoveryLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|refCnt
specifier|private
name|int
name|refCnt
init|=
literal|1
decl_stmt|;
DECL|field|indexWriter
specifier|private
name|SolrIndexWriter
name|indexWriter
init|=
literal|null
decl_stmt|;
DECL|field|directoryFactory
specifier|private
name|DirectoryFactory
name|directoryFactory
decl_stmt|;
DECL|field|recoveryRunning
specifier|private
name|boolean
name|recoveryRunning
decl_stmt|;
DECL|field|recoveryStrat
specifier|private
name|RecoveryStrategy
name|recoveryStrat
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|DefaultSolrCoreState
specifier|public
name|DefaultSolrCoreState
parameter_list|(
name|DirectoryFactory
name|directoryFactory
parameter_list|)
block|{
name|this
operator|.
name|directoryFactory
operator|=
name|directoryFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getIndexWriter
specifier|public
specifier|synchronized
name|IndexWriter
name|getIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexWriter
operator|==
literal|null
condition|)
block|{
name|indexWriter
operator|=
name|createMainIndexWriter
argument_list|(
name|core
argument_list|,
literal|"DirectUpdateHandler2"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|indexWriter
return|;
block|}
annotation|@
name|Override
DECL|method|newIndexWriter
specifier|public
specifier|synchronized
name|void
name|newIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|indexWriter
operator|=
name|createMainIndexWriter
argument_list|(
name|core
argument_list|,
literal|"DirectUpdateHandler2"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decref
specifier|public
name|void
name|decref
parameter_list|(
name|IndexWriterCloser
name|closer
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|refCnt
operator|--
expr_stmt|;
if|if
condition|(
name|refCnt
operator|==
literal|0
condition|)
block|{
try|try
block|{
if|if
condition|(
name|closer
operator|!=
literal|null
condition|)
block|{
name|closer
operator|.
name|closeWriter
argument_list|(
name|indexWriter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error during shutdown of writer."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|directoryFactory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error during shutdown of directory factory."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
comment|// TODO: we cannot cancel recovery here if its a CoreContainer shutdown
comment|// it can cause deadlock - but perhaps we want to if we are stopping early
comment|// and CoreContainer is not being shutdown?
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|incref
specifier|public
specifier|synchronized
name|void
name|incref
parameter_list|()
block|{
if|if
condition|(
name|refCnt
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"IndexWriter has been closed"
argument_list|)
throw|;
block|}
name|refCnt
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rollbackIndexWriter
specifier|public
specifier|synchronized
name|void
name|rollbackIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|indexWriter
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|newIndexWriter
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
DECL|method|createMainIndexWriter
specifier|protected
name|SolrIndexWriter
name|createMainIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|removeAllExisting
parameter_list|,
name|boolean
name|forceNewDirectory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SolrIndexWriter
argument_list|(
name|name
argument_list|,
name|core
operator|.
name|getNewIndexDir
argument_list|()
argument_list|,
name|core
operator|.
name|getDirectoryFactory
argument_list|()
argument_list|,
name|removeAllExisting
argument_list|,
name|core
operator|.
name|getSchema
argument_list|()
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
argument_list|,
name|core
operator|.
name|getDeletionPolicy
argument_list|()
argument_list|,
name|core
operator|.
name|getCodec
argument_list|()
argument_list|,
name|forceNewDirectory
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectoryFactory
specifier|public
name|DirectoryFactory
name|getDirectoryFactory
parameter_list|()
block|{
return|return
name|directoryFactory
return|;
block|}
annotation|@
name|Override
DECL|method|doRecovery
specifier|public
name|void
name|doRecovery
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|SKIP_AUTO_RECOVERY
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Skipping recovery according to sys prop solrcloud.skip.autorecovery"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|cc
operator|.
name|isShutDown
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Skipping recovery because Solr is shutdown"
argument_list|)
expr_stmt|;
return|return;
block|}
name|cancelRecovery
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|recoveryLock
init|)
block|{
while|while
condition|(
name|recoveryRunning
condition|)
block|{
try|try
block|{
name|recoveryLock
operator|.
name|wait
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
block|{          }
if|if
condition|(
name|closed
condition|)
return|return;
block|}
comment|// if true, we are recovering after startup and shouldn't have (or be receiving) additional updates (except for local tlog recovery)
name|boolean
name|recoveringAfterStartup
init|=
name|recoveryStrat
operator|==
literal|null
decl_stmt|;
name|recoveryStrat
operator|=
operator|new
name|RecoveryStrategy
argument_list|(
name|cc
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|recoveryStrat
operator|.
name|setRecoveringAfterStartup
argument_list|(
name|recoveringAfterStartup
argument_list|)
expr_stmt|;
name|recoveryStrat
operator|.
name|start
argument_list|()
expr_stmt|;
name|recoveryRunning
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|cancelRecovery
specifier|public
name|void
name|cancelRecovery
parameter_list|()
block|{
synchronized|synchronized
init|(
name|recoveryLock
init|)
block|{
if|if
condition|(
name|recoveryStrat
operator|!=
literal|null
condition|)
block|{
name|recoveryStrat
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|recoveryStrat
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                    }
name|recoveryRunning
operator|=
literal|false
expr_stmt|;
name|recoveryLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
