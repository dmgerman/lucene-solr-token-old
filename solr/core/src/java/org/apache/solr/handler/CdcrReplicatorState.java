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
name|update
operator|.
name|CdcrUpdateLog
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
name|UpdateLog
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
name|DateFormatUtil
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
name|*
import|;
end_import
begin_comment
comment|/**  * The state of the replication with a target cluster.  */
end_comment
begin_class
DECL|class|CdcrReplicatorState
class|class
name|CdcrReplicatorState
block|{
DECL|field|targetCollection
specifier|private
specifier|final
name|String
name|targetCollection
decl_stmt|;
DECL|field|zkHost
specifier|private
specifier|final
name|String
name|zkHost
decl_stmt|;
DECL|field|targetClient
specifier|private
specifier|final
name|CloudSolrClient
name|targetClient
decl_stmt|;
DECL|field|logReader
specifier|private
name|CdcrUpdateLog
operator|.
name|CdcrLogReader
name|logReader
decl_stmt|;
DECL|field|consecutiveErrors
specifier|private
name|long
name|consecutiveErrors
init|=
literal|0
decl_stmt|;
DECL|field|errorCounters
specifier|private
specifier|final
name|Map
argument_list|<
name|ErrorType
argument_list|,
name|Long
argument_list|>
name|errorCounters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|errorsQueue
specifier|private
specifier|final
name|FixedQueue
argument_list|<
name|ErrorQueueEntry
argument_list|>
name|errorsQueue
init|=
operator|new
name|FixedQueue
argument_list|<>
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|// keep the last 100 errors
DECL|field|benchmarkTimer
specifier|private
name|BenchmarkTimer
name|benchmarkTimer
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CdcrReplicatorState
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|CdcrReplicatorState
name|CdcrReplicatorState
parameter_list|(
specifier|final
name|String
name|targetCollection
parameter_list|,
specifier|final
name|String
name|zkHost
parameter_list|,
specifier|final
name|CloudSolrClient
name|targetClient
parameter_list|)
block|{
name|this
operator|.
name|targetCollection
operator|=
name|targetCollection
expr_stmt|;
name|this
operator|.
name|targetClient
operator|=
name|targetClient
expr_stmt|;
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|benchmarkTimer
operator|=
operator|new
name|BenchmarkTimer
argument_list|()
expr_stmt|;
block|}
comment|/**    * Initialise the replicator state with a {@link org.apache.solr.update.CdcrUpdateLog.CdcrLogReader}    * that is positioned at the last target cluster checkpoint.    */
DECL|method|init
name|void
name|init
parameter_list|(
specifier|final
name|CdcrUpdateLog
operator|.
name|CdcrLogReader
name|logReader
parameter_list|)
block|{
name|this
operator|.
name|logReader
operator|=
name|logReader
expr_stmt|;
block|}
DECL|method|closeLogReader
name|void
name|closeLogReader
parameter_list|()
block|{
if|if
condition|(
name|logReader
operator|!=
literal|null
condition|)
block|{
name|logReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|logReader
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getLogReader
name|CdcrUpdateLog
operator|.
name|CdcrLogReader
name|getLogReader
parameter_list|()
block|{
return|return
name|logReader
return|;
block|}
DECL|method|getTargetCollection
name|String
name|getTargetCollection
parameter_list|()
block|{
return|return
name|targetCollection
return|;
block|}
DECL|method|getZkHost
name|String
name|getZkHost
parameter_list|()
block|{
return|return
name|zkHost
return|;
block|}
DECL|method|getClient
name|CloudSolrClient
name|getClient
parameter_list|()
block|{
return|return
name|targetClient
return|;
block|}
DECL|method|shutdown
name|void
name|shutdown
parameter_list|()
block|{
try|try
block|{
name|targetClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Caught exception trying to close server: "
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|closeLogReader
argument_list|()
expr_stmt|;
block|}
DECL|method|reportError
name|void
name|reportError
parameter_list|(
name|ErrorType
name|error
parameter_list|)
block|{
if|if
condition|(
operator|!
name|errorCounters
operator|.
name|containsKey
argument_list|(
name|error
argument_list|)
condition|)
block|{
name|errorCounters
operator|.
name|put
argument_list|(
name|error
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
block|}
name|errorCounters
operator|.
name|put
argument_list|(
name|error
argument_list|,
name|errorCounters
operator|.
name|get
argument_list|(
name|error
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|errorsQueue
operator|.
name|add
argument_list|(
operator|new
name|ErrorQueueEntry
argument_list|(
name|error
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|consecutiveErrors
operator|++
expr_stmt|;
block|}
DECL|method|resetConsecutiveErrors
name|void
name|resetConsecutiveErrors
parameter_list|()
block|{
name|consecutiveErrors
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Returns the number of consecutive errors encountered while trying to forward updates to the target.    */
DECL|method|getConsecutiveErrors
name|long
name|getConsecutiveErrors
parameter_list|()
block|{
return|return
name|consecutiveErrors
return|;
block|}
comment|/**    * Gets the number of errors of a particular type.    */
DECL|method|getErrorCount
name|long
name|getErrorCount
parameter_list|(
name|ErrorType
name|type
parameter_list|)
block|{
if|if
condition|(
name|errorCounters
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|errorCounters
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/**    * Gets the last errors ordered by timestamp (most recent first)    */
DECL|method|getLastErrors
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|getLastErrors
parameter_list|()
block|{
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|lastErrors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|errorsQueue
init|)
block|{
name|Iterator
argument_list|<
name|ErrorQueueEntry
argument_list|>
name|it
init|=
name|errorsQueue
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ErrorQueueEntry
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|lastErrors
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
name|DateFormatUtil
operator|.
name|formatExternal
argument_list|(
name|entry
operator|.
name|timestamp
argument_list|)
block|,
name|entry
operator|.
name|type
operator|.
name|toLower
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|lastErrors
return|;
block|}
comment|/**    * Return the timestamp of the last processed operations    */
DECL|method|getTimestampOfLastProcessedOperation
name|String
name|getTimestampOfLastProcessedOperation
parameter_list|()
block|{
if|if
condition|(
name|logReader
operator|!=
literal|null
operator|&&
name|logReader
operator|.
name|getLastVersion
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// Shift back to the right by 20 bits the version number - See VersionInfo#getNewClock
return|return
name|DateFormatUtil
operator|.
name|formatExternal
argument_list|(
operator|new
name|Date
argument_list|(
name|logReader
operator|.
name|getLastVersion
argument_list|()
operator|>>
literal|20
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|String
argument_list|()
return|;
block|}
comment|/**    * Gets the benchmark timer.    */
DECL|method|getBenchmarkTimer
name|BenchmarkTimer
name|getBenchmarkTimer
parameter_list|()
block|{
return|return
name|this
operator|.
name|benchmarkTimer
return|;
block|}
DECL|enum|ErrorType
enum|enum
name|ErrorType
block|{
DECL|enum constant|INTERNAL
name|INTERNAL
block|,
DECL|enum constant|BAD_REQUEST
name|BAD_REQUEST
block|;
DECL|method|toLower
specifier|public
name|String
name|toLower
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
block|}
DECL|class|BenchmarkTimer
class|class
name|BenchmarkTimer
block|{
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|runTime
specifier|private
name|long
name|runTime
init|=
literal|0
decl_stmt|;
DECL|field|opCounters
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|Long
argument_list|>
name|opCounters
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Start recording time.      */
DECL|method|start
name|void
name|start
parameter_list|()
block|{
name|startTime
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
comment|/**      * Stop recording time.      */
DECL|method|stop
name|void
name|stop
parameter_list|()
block|{
name|runTime
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
expr_stmt|;
name|startTime
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|incrementCounter
name|void
name|incrementCounter
parameter_list|(
specifier|final
name|int
name|operationType
parameter_list|)
block|{
switch|switch
condition|(
name|operationType
condition|)
block|{
case|case
name|UpdateLog
operator|.
name|ADD
case|:
case|case
name|UpdateLog
operator|.
name|DELETE
case|:
case|case
name|UpdateLog
operator|.
name|DELETE_BY_QUERY
case|:
block|{
if|if
condition|(
operator|!
name|opCounters
operator|.
name|containsKey
argument_list|(
name|operationType
argument_list|)
condition|)
block|{
name|opCounters
operator|.
name|put
argument_list|(
name|operationType
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
block|}
name|opCounters
operator|.
name|put
argument_list|(
name|operationType
argument_list|,
name|opCounters
operator|.
name|get
argument_list|(
name|operationType
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return;
block|}
default|default:
return|return;
block|}
block|}
DECL|method|getRunTime
name|long
name|getRunTime
parameter_list|()
block|{
name|long
name|totalRunTime
init|=
name|runTime
decl_stmt|;
if|if
condition|(
name|startTime
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// we are currently recording the time
name|totalRunTime
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
expr_stmt|;
block|}
return|return
name|totalRunTime
return|;
block|}
DECL|method|getOperationsPerSecond
name|double
name|getOperationsPerSecond
parameter_list|()
block|{
name|long
name|total
init|=
literal|0
decl_stmt|;
for|for
control|(
name|long
name|counter
range|:
name|opCounters
operator|.
name|values
argument_list|()
control|)
block|{
name|total
operator|+=
name|counter
expr_stmt|;
block|}
name|double
name|elapsedTimeInSeconds
init|=
operator|(
operator|(
name|double
operator|)
name|this
operator|.
name|getRunTime
argument_list|()
operator|/
literal|1E9
operator|)
decl_stmt|;
return|return
name|total
operator|/
name|elapsedTimeInSeconds
return|;
block|}
DECL|method|getAddsPerSecond
name|double
name|getAddsPerSecond
parameter_list|()
block|{
name|long
name|total
init|=
name|opCounters
operator|.
name|get
argument_list|(
name|UpdateLog
operator|.
name|ADD
argument_list|)
operator|!=
literal|null
condition|?
name|opCounters
operator|.
name|get
argument_list|(
name|UpdateLog
operator|.
name|ADD
argument_list|)
else|:
literal|0
decl_stmt|;
name|double
name|elapsedTimeInSeconds
init|=
operator|(
operator|(
name|double
operator|)
name|this
operator|.
name|getRunTime
argument_list|()
operator|/
literal|1E9
operator|)
decl_stmt|;
return|return
name|total
operator|/
name|elapsedTimeInSeconds
return|;
block|}
DECL|method|getDeletesPerSecond
name|double
name|getDeletesPerSecond
parameter_list|()
block|{
name|long
name|total
init|=
name|opCounters
operator|.
name|get
argument_list|(
name|UpdateLog
operator|.
name|DELETE
argument_list|)
operator|!=
literal|null
condition|?
name|opCounters
operator|.
name|get
argument_list|(
name|UpdateLog
operator|.
name|DELETE
argument_list|)
else|:
literal|0
decl_stmt|;
name|total
operator|+=
name|opCounters
operator|.
name|get
argument_list|(
name|UpdateLog
operator|.
name|DELETE_BY_QUERY
argument_list|)
operator|!=
literal|null
condition|?
name|opCounters
operator|.
name|get
argument_list|(
name|UpdateLog
operator|.
name|DELETE_BY_QUERY
argument_list|)
else|:
literal|0
expr_stmt|;
name|double
name|elapsedTimeInSeconds
init|=
operator|(
operator|(
name|double
operator|)
name|this
operator|.
name|getRunTime
argument_list|()
operator|/
literal|1E9
operator|)
decl_stmt|;
return|return
name|total
operator|/
name|elapsedTimeInSeconds
return|;
block|}
block|}
DECL|class|ErrorQueueEntry
specifier|private
class|class
name|ErrorQueueEntry
block|{
DECL|field|type
specifier|private
name|ErrorType
name|type
decl_stmt|;
DECL|field|timestamp
specifier|private
name|Date
name|timestamp
decl_stmt|;
DECL|method|ErrorQueueEntry
specifier|private
name|ErrorQueueEntry
parameter_list|(
name|ErrorType
name|type
parameter_list|,
name|Date
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
block|}
DECL|class|FixedQueue
specifier|private
class|class
name|FixedQueue
parameter_list|<
name|E
parameter_list|>
extends|extends
name|LinkedList
argument_list|<
name|E
argument_list|>
block|{
DECL|field|maxSize
specifier|private
name|int
name|maxSize
decl_stmt|;
DECL|method|FixedQueue
specifier|public
name|FixedQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
specifier|synchronized
name|boolean
name|add
parameter_list|(
name|E
name|e
parameter_list|)
block|{
name|super
operator|.
name|addFirst
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
argument_list|()
operator|>
name|maxSize
condition|)
block|{
name|removeLast
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
block|}
end_class
end_unit
