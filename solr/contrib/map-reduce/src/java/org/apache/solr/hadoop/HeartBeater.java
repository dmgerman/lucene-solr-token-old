begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
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
name|Locale
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
name|CountDownLatch
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskInputOutputContext
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
name|util
operator|.
name|Progressable
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
comment|/**  * This class runs a background thread that once every 60 seconds checks to see if  * a progress report is needed. If a report is needed it is issued.  *   * A simple counter {@link #threadsNeedingHeartBeat} handles the number of  * threads requesting a heart beat.  *   * The expected usage pattern is  *   *<pre>  *  try {  *       heartBeater.needHeartBeat();  *       do something that may take a while  *    } finally {  *       heartBeater.cancelHeartBeat();  *    }  *</pre>  *   *   */
end_comment
begin_class
DECL|class|HeartBeater
specifier|public
class|class
name|HeartBeater
extends|extends
name|Thread
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * count of threads asking for heart beat, at 0 no heart beat done. This could    * be an atomic long but then missmatches in need/cancel could result in    * negative counts.    */
DECL|field|threadsNeedingHeartBeat
specifier|private
specifier|volatile
name|int
name|threadsNeedingHeartBeat
init|=
literal|0
decl_stmt|;
DECL|field|progress
specifier|private
name|Progressable
name|progress
decl_stmt|;
comment|/**    * The amount of time to wait between checks for the need to issue a heart    * beat. In milliseconds.    */
DECL|field|waitTimeMs
specifier|private
specifier|final
name|long
name|waitTimeMs
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
DECL|field|isClosing
specifier|private
specifier|final
name|CountDownLatch
name|isClosing
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/**    * Create the heart beat object thread set it to daemon priority and start the    * thread. When the count in {@link #threadsNeedingHeartBeat} is positive, the    * heart beat will be issued on the progress object every 60 seconds.    */
DECL|method|HeartBeater
specifier|public
name|HeartBeater
parameter_list|(
name|Progressable
name|progress
parameter_list|)
block|{
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Heart beat reporting class is "
operator|+
name|progress
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|getProgress
specifier|public
name|Progressable
name|getProgress
parameter_list|()
block|{
return|return
name|progress
return|;
block|}
DECL|method|setProgress
specifier|public
name|void
name|setProgress
parameter_list|(
name|Progressable
name|progress
parameter_list|)
block|{
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HeartBeat thread running"
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|threadsNeedingHeartBeat
operator|>
literal|0
condition|)
block|{
name|progress
operator|.
name|progress
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"Issuing heart beat for %d threads"
argument_list|,
name|threadsNeedingHeartBeat
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"heartbeat skipped count %d"
argument_list|,
name|threadsNeedingHeartBeat
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|isClosing
operator|.
name|await
argument_list|(
name|waitTimeMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"HeartBeat throwable"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * inform the background thread that heartbeats are to be issued. Issue a    * heart beat also    */
DECL|method|needHeartBeat
specifier|public
specifier|synchronized
name|void
name|needHeartBeat
parameter_list|()
block|{
name|threadsNeedingHeartBeat
operator|++
expr_stmt|;
comment|// Issue a progress report right away,
comment|// just in case the the cancel comes before the background thread issues a
comment|// report.
comment|// If enough cases like this happen the 600 second timeout can occur
name|progress
operator|.
name|progress
argument_list|()
expr_stmt|;
if|if
condition|(
name|threadsNeedingHeartBeat
operator|==
literal|1
condition|)
block|{
comment|// this.notify(); // wake up the heartbeater
block|}
block|}
comment|/**    * inform the background thread that this heartbeat request is not needed.    * This must be called at some point after each {@link #needHeartBeat()}    * request.    */
DECL|method|cancelHeartBeat
specifier|public
specifier|synchronized
name|void
name|cancelHeartBeat
parameter_list|()
block|{
if|if
condition|(
name|threadsNeedingHeartBeat
operator|>
literal|0
condition|)
block|{
name|threadsNeedingHeartBeat
operator|--
expr_stmt|;
block|}
else|else
block|{
name|Exception
name|e
init|=
operator|new
name|Exception
argument_list|(
literal|"Dummy"
argument_list|)
decl_stmt|;
name|e
operator|.
name|fillInStackTrace
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"extra call to cancelHeartBeat"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setStatus
specifier|public
name|void
name|setStatus
parameter_list|(
name|String
name|status
parameter_list|)
block|{
if|if
condition|(
name|progress
operator|instanceof
name|TaskInputOutputContext
condition|)
block|{
operator|(
operator|(
name|TaskInputOutputContext
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|progress
operator|)
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Releases any resources */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|isClosing
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
