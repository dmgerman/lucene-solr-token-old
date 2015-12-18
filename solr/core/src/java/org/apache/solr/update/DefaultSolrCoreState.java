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
name|concurrent
operator|.
name|ExecutionException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|RejectedExecutionException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|ActionThrottle
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
name|SolrException
operator|.
name|ErrorCode
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
name|CoreDescriptor
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
name|apache
operator|.
name|solr
operator|.
name|logging
operator|.
name|MDCLoggingContext
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
name|RefCounted
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
implements|implements
name|RecoveryStrategy
operator|.
name|RecoveryListener
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
name|ReentrantLock
name|recoveryLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|recoveryThrottle
specifier|private
specifier|final
name|ActionThrottle
name|recoveryThrottle
init|=
operator|new
name|ActionThrottle
argument_list|(
literal|"recovery"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
DECL|field|leaderThrottle
specifier|private
specifier|final
name|ActionThrottle
name|leaderThrottle
init|=
operator|new
name|ActionThrottle
argument_list|(
literal|"leader"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
DECL|field|recoveryWaiting
specifier|private
specifier|final
name|AtomicInteger
name|recoveryWaiting
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|// Use the readLock to retrieve the current IndexWriter (may be lazily opened)
comment|// Use the writeLock for changing index writers
DECL|field|iwLock
specifier|private
specifier|final
name|ReentrantReadWriteLock
name|iwLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
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
DECL|field|recoveryStrat
specifier|private
specifier|volatile
name|RecoveryStrategy
name|recoveryStrat
decl_stmt|;
DECL|field|future
specifier|private
specifier|volatile
name|Future
name|future
decl_stmt|;
DECL|field|lastReplicationSuccess
specifier|private
specifier|volatile
name|boolean
name|lastReplicationSuccess
init|=
literal|true
decl_stmt|;
comment|// will we attempt recovery as if we just started up (i.e. use starting versions rather than recent versions for peersync
comment|// so we aren't looking at update versions that have started buffering since we came up.
DECL|field|recoveringAfterStartup
specifier|private
specifier|volatile
name|boolean
name|recoveringAfterStartup
init|=
literal|true
decl_stmt|;
DECL|field|refCntWriter
specifier|private
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
name|refCntWriter
decl_stmt|;
DECL|field|commitLock
specifier|protected
specifier|final
name|ReentrantLock
name|commitLock
init|=
operator|new
name|ReentrantLock
argument_list|()
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
DECL|method|closeIndexWriter
specifier|private
name|void
name|closeIndexWriter
parameter_list|(
name|IndexWriterCloser
name|closer
parameter_list|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrCoreState ref count has reached 0 - closing IndexWriter"
argument_list|)
expr_stmt|;
if|if
condition|(
name|closer
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"closing IndexWriter with IndexWriterCloser"
argument_list|)
expr_stmt|;
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
name|log
operator|.
name|info
argument_list|(
literal|"closing IndexWriter..."
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|indexWriter
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error during close of writer."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getIndexWriter
specifier|public
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
name|getIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|succeeded
init|=
literal|false
decl_stmt|;
name|lock
argument_list|(
name|iwLock
operator|.
name|readLock
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Multiple readers may be executing this, but we only want one to open the writer on demand.
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
comment|// core == null is a signal to just return the current writer, or null if none.
name|initRefCntWriter
argument_list|()
expr_stmt|;
if|if
condition|(
name|refCntWriter
operator|==
literal|null
condition|)
return|return
literal|null
return|;
block|}
else|else
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
argument_list|)
expr_stmt|;
block|}
name|initRefCntWriter
argument_list|()
expr_stmt|;
block|}
name|refCntWriter
operator|.
name|incref
argument_list|()
expr_stmt|;
name|succeeded
operator|=
literal|true
expr_stmt|;
comment|// the returned RefCounted<IndexWriter> will release the readLock on a decref()
return|return
name|refCntWriter
return|;
block|}
block|}
finally|finally
block|{
comment|// if we failed to return the IW for some other reason, we should unlock.
if|if
condition|(
operator|!
name|succeeded
condition|)
block|{
name|iwLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|initRefCntWriter
specifier|private
name|void
name|initRefCntWriter
parameter_list|()
block|{
comment|// TODO: since we moved to a read-write lock, and don't rely on the count to close the writer, we don't really
comment|// need this class any more.  It could also be a singleton created at the same time as SolrCoreState
comment|// or we could change the API of SolrCoreState to just return the writer and then add a releaseWriter() call.
if|if
condition|(
name|refCntWriter
operator|==
literal|null
operator|&&
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|refCntWriter
operator|=
operator|new
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
argument_list|(
name|indexWriter
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|decref
parameter_list|()
block|{
name|iwLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|super
operator|.
name|decref
argument_list|()
expr_stmt|;
comment|// This is now redundant (since we switched to read-write locks), we don't really need to maintain our own reference count.
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|//  We rely on other code to actually close the IndexWriter, and there's nothing special to do when the ref count hits 0
block|}
block|}
expr_stmt|;
block|}
block|}
comment|// acquires the lock or throws an exception if the CoreState has been closed.
DECL|method|lock
specifier|private
name|void
name|lock
parameter_list|(
name|Lock
name|lock
parameter_list|)
block|{
name|boolean
name|acquired
init|=
literal|false
decl_stmt|;
do|do
block|{
try|try
block|{
name|acquired
operator|=
name|lock
operator|.
name|tryLock
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"WARNING - Dangerous interrupt"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// even if we failed to acquire, check if we are closed
if|if
condition|(
name|closed
condition|)
block|{
if|if
condition|(
name|acquired
condition|)
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
literal|"SolrCoreState already closed."
argument_list|)
throw|;
block|}
block|}
do|while
condition|(
operator|!
name|acquired
condition|)
do|;
block|}
comment|// closes and opens index writers without any locking
DECL|method|changeWriter
specifier|private
name|void
name|changeWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|boolean
name|rollback
parameter_list|,
name|boolean
name|openNewWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|coreName
init|=
name|core
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// We need to null this so it picks up the new writer next get call.
comment|// We do this before anything else in case we hit an exception.
name|refCntWriter
operator|=
literal|null
expr_stmt|;
name|IndexWriter
name|iw
init|=
name|indexWriter
decl_stmt|;
comment|// temp reference just for closing
name|indexWriter
operator|=
literal|null
expr_stmt|;
comment|// null this out now in case we fail, so we won't use the writer again
if|if
condition|(
name|iw
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|rollback
condition|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Closing old IndexWriter... core="
operator|+
name|coreName
argument_list|)
expr_stmt|;
name|iw
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
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error closing old IndexWriter. core="
operator|+
name|coreName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Rollback old IndexWriter... core="
operator|+
name|coreName
argument_list|)
expr_stmt|;
name|iw
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error rolling back old IndexWriter. core="
operator|+
name|coreName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|openNewWriter
condition|)
block|{
name|indexWriter
operator|=
name|createMainIndexWriter
argument_list|(
name|core
argument_list|,
literal|"DirectUpdateHandler2"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"New IndexWriter is ready to be used."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|newIndexWriter
specifier|public
name|void
name|newIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|boolean
name|rollback
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
argument_list|(
name|iwLock
operator|.
name|writeLock
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|changeWriter
argument_list|(
name|core
argument_list|,
name|rollback
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|iwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|closeIndexWriter
specifier|public
name|void
name|closeIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|boolean
name|rollback
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
argument_list|(
name|iwLock
operator|.
name|writeLock
argument_list|()
argument_list|)
expr_stmt|;
name|changeWriter
argument_list|(
name|core
argument_list|,
name|rollback
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Do not unlock the writeLock in this method.  It will be unlocked by the openIndexWriter call (see base class javadoc)
block|}
annotation|@
name|Override
DECL|method|openIndexWriter
specifier|public
name|void
name|openIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|changeWriter
argument_list|(
name|core
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|iwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
comment|//unlock even if we failed
block|}
block|}
annotation|@
name|Override
DECL|method|rollbackIndexWriter
specifier|public
name|void
name|rollbackIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|changeWriter
argument_list|(
name|core
argument_list|,
literal|true
argument_list|,
literal|true
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
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SolrIndexWriter
operator|.
name|create
argument_list|(
name|core
argument_list|,
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
literal|false
argument_list|,
name|core
operator|.
name|getLatestSchema
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
name|CoreDescriptor
name|cd
parameter_list|)
block|{
name|Thread
name|thread
init|=
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
name|MDCLoggingContext
operator|.
name|setCoreDescriptor
argument_list|(
name|cd
argument_list|)
expr_stmt|;
try|try
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
comment|// check before we grab the lock
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
comment|// if we can't get the lock, another recovery is running
comment|// we check to see if there is already one waiting to go
comment|// after the current one, and if there is, bail
name|boolean
name|locked
init|=
name|recoveryLock
operator|.
name|tryLock
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|locked
condition|)
block|{
if|if
condition|(
name|recoveryWaiting
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return;
block|}
name|recoveryWaiting
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|recoveryWaiting
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|cancelRecovery
argument_list|()
expr_stmt|;
block|}
name|recoveryLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|recoveryWaiting
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
comment|// to be air tight we must also check after lock
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
name|log
operator|.
name|info
argument_list|(
literal|"Running recovery"
argument_list|)
expr_stmt|;
name|recoveryThrottle
operator|.
name|minimumWaitBetweenActions
argument_list|()
expr_stmt|;
name|recoveryThrottle
operator|.
name|markAttemptingAction
argument_list|()
expr_stmt|;
name|recoveryStrat
operator|=
operator|new
name|RecoveryStrategy
argument_list|(
name|cc
argument_list|,
name|cd
argument_list|,
name|DefaultSolrCoreState
operator|.
name|this
argument_list|)
expr_stmt|;
name|recoveryStrat
operator|.
name|setRecoveringAfterStartup
argument_list|(
name|recoveringAfterStartup
argument_list|)
expr_stmt|;
name|future
operator|=
name|cc
operator|.
name|getUpdateShardHandler
argument_list|()
operator|.
name|getRecoveryExecutor
argument_list|()
operator|.
name|submit
argument_list|(
name|recoveryStrat
argument_list|)
expr_stmt|;
try|try
block|{
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|recoveryLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|locked
condition|)
name|recoveryLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|MDCLoggingContext
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
try|try
block|{
comment|// we make recovery requests async - that async request may
comment|// have to 'wait in line' a bit or bail if a recovery is
comment|// already queued up - the recovery execution itself is run
comment|// in another thread on another 'recovery' executor.
comment|// The update executor is interrupted on shutdown and should
comment|// not do disk IO.
comment|// The recovery executor is not interrupted on shutdown.
comment|//
comment|// avoid deadlock: we can't use the recovery executor here
name|cc
operator|.
name|getUpdateShardHandler
argument_list|()
operator|.
name|getUpdateExecutor
argument_list|()
operator|.
name|submit
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RejectedExecutionException
name|e
parameter_list|)
block|{
comment|// fine, we are shutting down
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
if|if
condition|(
name|recoveryStrat
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|recoveryStrat
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// okay
block|}
block|}
block|}
comment|/** called from recoveryStrat on a successful recovery */
annotation|@
name|Override
DECL|method|recovered
specifier|public
name|void
name|recovered
parameter_list|()
block|{
name|recoveringAfterStartup
operator|=
literal|false
expr_stmt|;
comment|// once we have successfully recovered, we no longer need to act as if we are recovering after startup
block|}
comment|/** called from recoveryStrat on a failed recovery */
annotation|@
name|Override
DECL|method|failed
specifier|public
name|void
name|failed
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|(
name|IndexWriterCloser
name|closer
parameter_list|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|cancelRecovery
argument_list|()
expr_stmt|;
name|closeIndexWriter
argument_list|(
name|closer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCommitLock
specifier|public
name|Lock
name|getCommitLock
parameter_list|()
block|{
return|return
name|commitLock
return|;
block|}
annotation|@
name|Override
DECL|method|getLeaderThrottle
specifier|public
name|ActionThrottle
name|getLeaderThrottle
parameter_list|()
block|{
return|return
name|leaderThrottle
return|;
block|}
annotation|@
name|Override
DECL|method|getLastReplicateIndexSuccess
specifier|public
name|boolean
name|getLastReplicateIndexSuccess
parameter_list|()
block|{
return|return
name|lastReplicationSuccess
return|;
block|}
annotation|@
name|Override
DECL|method|setLastReplicateIndexSuccess
specifier|public
name|void
name|setLastReplicateIndexSuccess
parameter_list|(
name|boolean
name|success
parameter_list|)
block|{
name|this
operator|.
name|lastReplicationSuccess
operator|=
name|success
expr_stmt|;
block|}
block|}
end_class
end_unit
