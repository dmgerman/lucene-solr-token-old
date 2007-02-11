begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Iterator
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
import|;
end_import
begin_comment
comment|/**  * Sequence of parallel or sequential tasks.  */
end_comment
begin_class
DECL|class|TaskSequence
specifier|public
class|class
name|TaskSequence
extends|extends
name|PerfTask
block|{
DECL|field|tasks
specifier|private
name|ArrayList
name|tasks
decl_stmt|;
DECL|field|repetitions
specifier|private
name|int
name|repetitions
init|=
literal|1
decl_stmt|;
DECL|field|parallel
specifier|private
name|boolean
name|parallel
decl_stmt|;
DECL|field|parent
specifier|private
name|TaskSequence
name|parent
decl_stmt|;
DECL|field|letChildReport
specifier|private
name|boolean
name|letChildReport
init|=
literal|true
decl_stmt|;
DECL|field|rate
specifier|private
name|int
name|rate
init|=
literal|0
decl_stmt|;
DECL|field|perMin
specifier|private
name|boolean
name|perMin
init|=
literal|false
decl_stmt|;
comment|// rate, if set, is, by default, be sec.
DECL|field|seqName
specifier|private
name|String
name|seqName
decl_stmt|;
DECL|method|TaskSequence
specifier|public
name|TaskSequence
parameter_list|(
name|PerfRunData
name|runData
parameter_list|,
name|String
name|name
parameter_list|,
name|TaskSequence
name|parent
parameter_list|,
name|boolean
name|parallel
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
name|name
operator|=
operator|(
name|name
operator|!=
literal|null
condition|?
name|name
else|:
operator|(
name|parallel
condition|?
literal|"Par"
else|:
literal|"Seq"
operator|)
operator|)
expr_stmt|;
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|setSequenceName
argument_list|()
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|parallel
operator|=
name|parallel
expr_stmt|;
name|tasks
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return Returns the parallel.    */
DECL|method|isParallel
specifier|public
name|boolean
name|isParallel
parameter_list|()
block|{
return|return
name|parallel
return|;
block|}
comment|/**    * @return Returns the repetitions.    */
DECL|method|getRepetitions
specifier|public
name|int
name|getRepetitions
parameter_list|()
block|{
return|return
name|repetitions
return|;
block|}
comment|/**    * @param repetitions The repetitions to set.    */
DECL|method|setRepetitions
specifier|public
name|void
name|setRepetitions
parameter_list|(
name|int
name|repetitions
parameter_list|)
block|{
name|this
operator|.
name|repetitions
operator|=
name|repetitions
expr_stmt|;
name|setSequenceName
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return Returns the parent.    */
DECL|method|getParent
specifier|public
name|TaskSequence
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.tasks.PerfTask#doLogic()    */
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|(
name|parallel
condition|?
name|doParallelTasks
argument_list|()
else|:
name|doSerialTasks
argument_list|()
operator|)
return|;
block|}
DECL|method|doSerialTasks
specifier|private
name|int
name|doSerialTasks
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|rate
operator|>
literal|0
condition|)
block|{
return|return
name|doSerialTasksWithRate
argument_list|()
return|;
block|}
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|repetitions
condition|;
name|k
operator|++
control|)
block|{
for|for
control|(
name|Iterator
name|it
init|=
name|tasks
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|PerfTask
name|task
init|=
operator|(
name|PerfTask
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|count
operator|+=
name|task
operator|.
name|runAndMaybeStats
argument_list|(
name|letChildReport
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
DECL|method|doSerialTasksWithRate
specifier|private
name|int
name|doSerialTasksWithRate
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|delayStep
init|=
operator|(
name|perMin
condition|?
literal|60000
else|:
literal|1000
operator|)
operator|/
name|rate
decl_stmt|;
name|long
name|nextStartTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|repetitions
condition|;
name|k
operator|++
control|)
block|{
for|for
control|(
name|Iterator
name|it
init|=
name|tasks
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|PerfTask
name|task
init|=
operator|(
name|PerfTask
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|long
name|waitMore
init|=
name|nextStartTime
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|waitMore
operator|>
literal|0
condition|)
block|{
comment|//System.out.println("wait: "+waitMore+" for rate: "+ratePerMin+" (delayStep="+delayStep+")");
name|Thread
operator|.
name|sleep
argument_list|(
name|waitMore
argument_list|)
expr_stmt|;
block|}
name|nextStartTime
operator|+=
name|delayStep
expr_stmt|;
comment|// this aims at avarage rate.
name|count
operator|+=
name|task
operator|.
name|runAndMaybeStats
argument_list|(
name|letChildReport
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
DECL|method|doParallelTasks
specifier|private
name|int
name|doParallelTasks
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|count
index|[]
init|=
block|{
literal|0
block|}
decl_stmt|;
name|Thread
name|t
index|[]
init|=
operator|new
name|Thread
index|[
name|repetitions
operator|*
name|tasks
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// prepare threads
name|int
name|indx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|repetitions
condition|;
name|k
operator|++
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tasks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|PerfTask
name|task
init|=
call|(
name|PerfTask
call|)
argument_list|(
operator|(
name|PerfTask
operator|)
name|tasks
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|clone
argument_list|()
decl_stmt|;
name|t
index|[
name|indx
operator|++
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|n
decl_stmt|;
try|try
block|{
name|n
operator|=
name|task
operator|.
name|runAndMaybeStats
argument_list|(
name|letChildReport
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
synchronized|synchronized
init|(
name|count
init|)
block|{
name|count
index|[
literal|0
index|]
operator|+=
name|n
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
block|}
block|}
comment|// run threads
name|startThreads
argument_list|(
name|t
argument_list|)
expr_stmt|;
comment|// wait for all threads to complete
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|t
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|t
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|// return total count
return|return
name|count
index|[
literal|0
index|]
return|;
block|}
comment|// run threads
DECL|method|startThreads
specifier|private
name|void
name|startThreads
parameter_list|(
name|Thread
index|[]
name|t
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|rate
operator|>
literal|0
condition|)
block|{
name|startlThreadsWithRate
argument_list|(
name|t
argument_list|)
expr_stmt|;
return|return;
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
name|t
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|t
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|// run threadsm with rate
DECL|method|startlThreadsWithRate
specifier|private
name|void
name|startlThreadsWithRate
parameter_list|(
name|Thread
index|[]
name|t
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|delayStep
init|=
operator|(
name|perMin
condition|?
literal|60000
else|:
literal|1000
operator|)
operator|/
name|rate
decl_stmt|;
name|long
name|nextStartTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
name|t
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|long
name|waitMore
init|=
name|nextStartTime
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|waitMore
operator|>
literal|0
condition|)
block|{
comment|//System.out.println("thread wait: "+waitMore+" for rate: "+ratePerMin+" (delayStep="+delayStep+")");
name|Thread
operator|.
name|sleep
argument_list|(
name|waitMore
argument_list|)
expr_stmt|;
block|}
name|nextStartTime
operator|+=
name|delayStep
expr_stmt|;
comment|// this aims at avarage rate of starting threads.
name|t
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addTask
specifier|public
name|void
name|addTask
parameter_list|(
name|PerfTask
name|task
parameter_list|)
block|{
name|tasks
operator|.
name|add
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|task
operator|.
name|setDepth
argument_list|(
name|getDepth
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#toString()    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|padd
init|=
name|getPadding
argument_list|()
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|parallel
condition|?
literal|" ["
else|:
literal|" {"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NEW_LINE
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|tasks
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|PerfTask
name|task
init|=
operator|(
name|PerfTask
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|task
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|NEW_LINE
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|padd
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
operator|!
name|letChildReport
condition|?
literal|">"
else|:
operator|(
name|parallel
condition|?
literal|"]"
else|:
literal|"}"
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|repetitions
operator|>
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" * "
operator|+
name|repetitions
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rate
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|",  rate: "
operator|+
name|rate
operator|+
literal|"/"
operator|+
operator|(
name|perMin
condition|?
literal|"min"
else|:
literal|"sec"
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Execute child tasks in a way that they do not reprt their time separately.    * Current implementation if child tasks has child tasks of their own, those are not affected by this call.     */
DECL|method|setNoChildReport
specifier|public
name|void
name|setNoChildReport
parameter_list|()
block|{
name|letChildReport
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|tasks
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|PerfTask
name|task
init|=
operator|(
name|PerfTask
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|task
operator|instanceof
name|TaskSequence
condition|)
block|{
operator|(
operator|(
name|TaskSequence
operator|)
name|task
operator|)
operator|.
name|setNoChildReport
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Returns the rate per minute: how many operations should be performed in a minute.    * If 0 this has no effect.    * @return the rate per min: how many operations should be performed in a minute.    */
DECL|method|getRate
specifier|public
name|int
name|getRate
parameter_list|()
block|{
return|return
operator|(
name|perMin
condition|?
name|rate
else|:
literal|60
operator|*
name|rate
operator|)
return|;
block|}
comment|/**    * @param rate The rate to set.    */
DECL|method|setRate
specifier|public
name|void
name|setRate
parameter_list|(
name|int
name|rate
parameter_list|,
name|boolean
name|perMin
parameter_list|)
block|{
name|this
operator|.
name|rate
operator|=
name|rate
expr_stmt|;
name|this
operator|.
name|perMin
operator|=
name|perMin
expr_stmt|;
name|setSequenceName
argument_list|()
expr_stmt|;
block|}
DECL|method|setSequenceName
specifier|private
name|void
name|setSequenceName
parameter_list|()
block|{
name|seqName
operator|=
name|super
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|repetitions
operator|>
literal|1
condition|)
block|{
name|seqName
operator|+=
literal|"_"
operator|+
name|repetitions
expr_stmt|;
block|}
if|if
condition|(
name|rate
operator|>
literal|0
condition|)
block|{
name|seqName
operator|+=
literal|"_"
operator|+
name|rate
operator|+
operator|(
name|perMin
condition|?
literal|"/min"
else|:
literal|"/sec"
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|parallel
operator|&&
name|seqName
operator|.
name|toLowerCase
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"par"
argument_list|)
operator|<
literal|0
condition|)
block|{
name|seqName
operator|+=
literal|"_Par"
expr_stmt|;
block|}
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|seqName
return|;
comment|// overide to include more info
block|}
comment|/**    * @return Returns the tasks.    */
DECL|method|getTasks
specifier|public
name|ArrayList
name|getTasks
parameter_list|()
block|{
return|return
name|tasks
return|;
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#clone()    */
DECL|method|clone
specifier|protected
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|TaskSequence
name|res
init|=
operator|(
name|TaskSequence
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|res
operator|.
name|tasks
operator|=
operator|new
name|ArrayList
argument_list|()
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
name|tasks
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|res
operator|.
name|tasks
operator|.
name|add
argument_list|(
operator|(
operator|(
name|PerfTask
operator|)
name|tasks
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
