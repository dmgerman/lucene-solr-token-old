begin_unit
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_package
DECL|package|de.lanlab.larm.threads
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|threads
package|;
end_package
begin_comment
comment|//import java.util.Vector;
end_comment
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
comment|/**  *  if you have many tasks to accomplish, you can do this with one of the  *  following strategies:  *<uL>  *<li> do it one after another (single threaded). this may often be  *    inefficient because most programs often wait for external resources  *<li> assign a new thread for each task (thread on demand). This will clog  *    up the system if many tasks have to be accomplished synchronously  *<li> hold a number of tasks, and queue the requests if there are more  *    tasks than threads (ThreadPool).  *</ul>  *  This thread pool is based on an article in Java-Magazin 06/2000.  *  synchronizations were removed unless necessary  *  *  */
end_comment
begin_class
DECL|class|ThreadPool
specifier|public
class|class
name|ThreadPool
implements|implements
name|ThreadingStrategy
implements|,
name|TaskReadyListener
block|{
DECL|field|maxThreads
specifier|private
name|int
name|maxThreads
init|=
name|MAX_THREADS
decl_stmt|;
comment|/**      *  references to all threads are stored here      */
DECL|field|allThreads
specifier|private
name|HashMap
name|allThreads
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|/**      *  this vector takes all idle threads      */
DECL|field|idleThreads
specifier|private
name|Vector
name|idleThreads
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
comment|/**      *  this vector takes all threads that are in operation (busy)      */
DECL|field|busyThreads
specifier|private
name|Vector
name|busyThreads
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
comment|/**      *  if there are no idleThreads, tasks will go here      */
DECL|field|queue
specifier|private
name|TaskQueue
name|queue
init|=
operator|new
name|TaskQueue
argument_list|()
decl_stmt|;
comment|/**      *  thread pool observers will be notified of status changes      */
DECL|field|threadPoolObservers
specifier|private
name|Vector
name|threadPoolObservers
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
DECL|field|isStopped
specifier|private
name|boolean
name|isStopped
init|=
literal|false
decl_stmt|;
comment|/**      *  default maximum number of threads, if not given by the user      */
DECL|field|MAX_THREADS
specifier|public
specifier|final
specifier|static
name|int
name|MAX_THREADS
init|=
literal|5
decl_stmt|;
comment|/**      *  thread was created      */
DECL|field|THREAD_CREATE
specifier|public
specifier|final
specifier|static
name|String
name|THREAD_CREATE
init|=
literal|"T_CREATE"
decl_stmt|;
comment|/**      *  thread was created      */
DECL|field|THREAD_START
specifier|public
specifier|final
specifier|static
name|String
name|THREAD_START
init|=
literal|"T_START"
decl_stmt|;
comment|/**      *  thread is running      */
DECL|field|THREAD_RUNNING
specifier|public
specifier|final
specifier|static
name|String
name|THREAD_RUNNING
init|=
literal|"T_RUNNING"
decl_stmt|;
comment|/**      *  thread was stopped      */
DECL|field|THREAD_STOP
specifier|public
specifier|final
specifier|static
name|String
name|THREAD_STOP
init|=
literal|"T_STOP"
decl_stmt|;
comment|/**      *  thread was destroyed      */
DECL|field|THREAD_END
specifier|public
specifier|final
specifier|static
name|String
name|THREAD_END
init|=
literal|"T_END"
decl_stmt|;
comment|/**      *  thread is idle      */
DECL|field|THREAD_IDLE
specifier|public
specifier|final
specifier|static
name|String
name|THREAD_IDLE
init|=
literal|"T_IDLE"
decl_stmt|;
comment|/**      *  a task was added to the queue, because all threads were busy      */
DECL|field|THREADQUEUE_ADD
specifier|public
specifier|final
specifier|static
name|String
name|THREADQUEUE_ADD
init|=
literal|"TQ_ADD"
decl_stmt|;
comment|/**      *  a task was removed from the queue, because a thread had finished and was      *  ready      */
DECL|field|THREADQUEUE_REMOVE
specifier|public
specifier|final
specifier|static
name|String
name|THREADQUEUE_REMOVE
init|=
literal|"TQ_REMOVE"
decl_stmt|;
comment|/**      *  this factory will create the tasks      */
DECL|field|factory
name|ThreadFactory
name|factory
decl_stmt|;
comment|/**      *  this constructor will create the pool with MAX_THREADS threads and the      *  default factory      */
DECL|method|ThreadPool
specifier|public
name|ThreadPool
parameter_list|()
block|{
name|this
argument_list|(
name|MAX_THREADS
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      *  this constructor will create the pool with the default Factory      *      *@param  max  the maximum number of threads      */
DECL|method|ThreadPool
specifier|public
name|ThreadPool
parameter_list|(
name|int
name|max
parameter_list|)
block|{
name|this
argument_list|(
name|max
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      *  constructor      *      *@param  max      maximum number of threads      *@param  factory  the thread factory with which the threads will be created      */
DECL|method|ThreadPool
specifier|public
name|ThreadPool
parameter_list|(
name|int
name|max
parameter_list|,
name|ThreadFactory
name|factory
parameter_list|)
block|{
name|maxThreads
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
comment|/**      *  this init method will create the tasks. It must be called by hand      */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
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
name|maxThreads
condition|;
name|i
operator|++
control|)
block|{
name|createThread
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Description of the Method      *      *@param  i  Description of the Parameter      */
DECL|method|createThread
specifier|public
name|void
name|createThread
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|ServerThread
name|s
init|=
name|factory
operator|.
name|createServerThread
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|idleThreads
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|allThreads
operator|.
name|put
argument_list|(
operator|new
name|Integer
argument_list|(
name|i
argument_list|)
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|s
operator|.
name|addTaskReadyListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|i
argument_list|,
name|THREAD_CREATE
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|s
operator|.
name|start
argument_list|()
expr_stmt|;
name|sendMessage
argument_list|(
name|i
argument_list|,
name|THREAD_IDLE
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
comment|// FIXME: synchronisationstechnisch buggy
comment|/**      *  Description of the Method      *      *@param  i  Description of the Parameter      */
DECL|method|restartThread
specifier|public
name|void
name|restartThread
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|sendMessage
argument_list|(
name|i
argument_list|,
name|THREAD_STOP
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ServerThread
name|t
init|=
operator|(
name|ServerThread
operator|)
name|allThreads
operator|.
name|get
argument_list|(
operator|new
name|Integer
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|idleThreads
operator|.
name|remove
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|busyThreads
operator|.
name|remove
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|allThreads
operator|.
name|remove
argument_list|(
operator|new
name|Integer
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|interruptTask
argument_list|()
expr_stmt|;
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|//t.join();
comment|// deprecated, I know, but the only way to overcome SUN's bugs
name|t
operator|=
literal|null
expr_stmt|;
name|createThread
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  t    Description of the Parameter      *@param  key  Description of the Parameter      */
DECL|method|doTask
specifier|public
specifier|synchronized
name|void
name|doTask
parameter_list|(
name|InterruptableTask
name|t
parameter_list|,
name|Object
name|key
parameter_list|)
block|{
if|if
condition|(
operator|!
name|idleThreads
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ServerThread
name|s
init|=
operator|(
name|ServerThread
operator|)
name|idleThreads
operator|.
name|firstElement
argument_list|()
decl_stmt|;
name|idleThreads
operator|.
name|remove
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|busyThreads
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|s
operator|.
name|getThreadNumber
argument_list|()
argument_list|,
name|THREAD_START
argument_list|,
name|t
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
name|s
operator|.
name|runTask
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|s
operator|.
name|getThreadNumber
argument_list|()
argument_list|,
name|THREAD_RUNNING
argument_list|,
name|t
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|insert
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
operator|-
literal|1
argument_list|,
name|THREADQUEUE_ADD
argument_list|,
name|t
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  this will interrupt all threads. Therefore the InterruptableTasks must      *  attend on the interrupted-flag      */
DECL|method|interrupt
specifier|public
name|void
name|interrupt
parameter_list|()
block|{
name|Iterator
name|tasks
init|=
name|queue
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|tasks
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|InterruptableTask
name|t
init|=
operator|(
name|InterruptableTask
operator|)
name|tasks
operator|.
name|next
argument_list|()
decl_stmt|;
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|sendMessage
argument_list|(
operator|-
literal|1
argument_list|,
name|THREADQUEUE_REMOVE
argument_list|,
name|t
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
comment|// In der Hoffnung, dass alles klappt...
block|}
name|queue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Iterator
name|threads
init|=
name|busyThreads
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|threads
operator|.
name|hasNext
argument_list|()
condition|)
block|{
operator|(
operator|(
name|ServerThread
operator|)
name|threads
operator|.
name|next
argument_list|()
operator|)
operator|.
name|interruptTask
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      *  this will interrupt the tasks and end all threads      */
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|isStopped
operator|=
literal|true
expr_stmt|;
name|interrupt
argument_list|()
expr_stmt|;
name|Iterator
name|threads
init|=
name|idleThreads
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|threads
operator|.
name|hasNext
argument_list|()
condition|)
block|{
operator|(
operator|(
name|ServerThread
operator|)
name|threads
operator|.
name|next
argument_list|()
operator|)
operator|.
name|interruptTask
argument_list|()
expr_stmt|;
block|}
name|idleThreads
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      *  wird von einem ServerThread aufgerufen, wenn dieser fertig ist      *      *@param  s  Description of the Parameter      *@param:    ServerThread s - der aufrufende Thread      */
DECL|method|taskReady
specifier|public
specifier|synchronized
name|void
name|taskReady
parameter_list|(
name|ServerThread
name|s
parameter_list|)
block|{
if|if
condition|(
name|isStopped
condition|)
block|{
name|s
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|sendMessage
argument_list|(
name|s
operator|.
name|getThreadNumber
argument_list|()
argument_list|,
name|THREAD_STOP
argument_list|,
name|s
operator|.
name|getTask
argument_list|()
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
name|busyThreads
operator|.
name|remove
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|InterruptableTask
name|t
init|=
operator|(
name|InterruptableTask
operator|)
name|queue
operator|.
name|remove
argument_list|()
decl_stmt|;
comment|//queue.remove(t);
name|sendMessage
argument_list|(
operator|-
literal|1
argument_list|,
name|THREADQUEUE_REMOVE
argument_list|,
name|t
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|s
operator|.
name|getThreadNumber
argument_list|()
argument_list|,
name|THREAD_START
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|s
operator|.
name|runTask
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|s
operator|.
name|getThreadNumber
argument_list|()
argument_list|,
name|THREAD_RUNNING
argument_list|,
name|s
operator|.
name|getTask
argument_list|()
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sendMessage
argument_list|(
name|s
operator|.
name|getThreadNumber
argument_list|()
argument_list|,
name|THREAD_IDLE
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|idleThreads
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|busyThreads
operator|.
name|remove
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|idleThreads
init|)
block|{
name|idleThreads
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      *  Description of the Method      */
DECL|method|waitForFinish
specifier|public
name|void
name|waitForFinish
parameter_list|()
block|{
synchronized|synchronized
init|(
name|idleThreads
init|)
block|{
while|while
condition|(
name|busyThreads
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
comment|//System.out.println("busyThreads: " + busyThreads.size());
try|try
block|{
name|idleThreads
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Interrupted: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//System.out.println("busyThreads: " + busyThreads.size());
block|}
block|}
comment|/**      *  Adds a feature to the ThreadPoolObserver attribute of the ThreadPool      *  object      *      *@param  o  The feature to be added to the ThreadPoolObserver attribute      */
DECL|method|addThreadPoolObserver
specifier|public
name|void
name|addThreadPoolObserver
parameter_list|(
name|ThreadPoolObserver
name|o
parameter_list|)
block|{
name|threadPoolObservers
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  threadNr  Description of the Parameter      *@param  action    Description of the Parameter      *@param  info      Description of the Parameter      */
DECL|method|sendMessage
specifier|protected
name|void
name|sendMessage
parameter_list|(
name|int
name|threadNr
parameter_list|,
name|String
name|action
parameter_list|,
name|String
name|info
parameter_list|)
block|{
name|Iterator
name|Ie
init|=
name|threadPoolObservers
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|//System.out.println("ThreadPool: Sende " + action + " message an " + threadPoolObservers.size() + " Observers");
if|if
condition|(
name|threadNr
operator|!=
operator|-
literal|1
condition|)
block|{
while|while
condition|(
name|Ie
operator|.
name|hasNext
argument_list|()
condition|)
block|{
operator|(
operator|(
name|ThreadPoolObserver
operator|)
name|Ie
operator|.
name|next
argument_list|()
operator|)
operator|.
name|threadUpdate
argument_list|(
name|threadNr
argument_list|,
name|action
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
while|while
condition|(
name|Ie
operator|.
name|hasNext
argument_list|()
condition|)
block|{
operator|(
operator|(
name|ThreadPoolObserver
operator|)
name|Ie
operator|.
name|next
argument_list|()
operator|)
operator|.
name|queueUpdate
argument_list|(
name|info
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      *  Gets the queueSize attribute of the ThreadPool object      *      *@return    The queueSize value      */
DECL|method|getQueueSize
specifier|public
specifier|synchronized
name|int
name|getQueueSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|queue
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      *  Gets the idleThreadsCount attribute of the ThreadPool object      *      *@return    The idleThreadsCount value      */
DECL|method|getIdleThreadsCount
specifier|public
specifier|synchronized
name|int
name|getIdleThreadsCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|idleThreads
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      *  Gets the busyThreadsCount attribute of the ThreadPool object      *      *@return    The busyThreadsCount value      */
DECL|method|getBusyThreadsCount
specifier|public
specifier|synchronized
name|int
name|getBusyThreadsCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|busyThreads
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      *  Gets the threadCount attribute of the ThreadPool object      *      *@return    The threadCount value      */
DECL|method|getThreadCount
specifier|public
specifier|synchronized
name|int
name|getThreadCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|idleThreads
operator|.
name|size
argument_list|()
operator|+
name|this
operator|.
name|busyThreads
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      *  Gets the threadIterator attribute of the ThreadPool object      *      *@return    The threadIterator value      */
DECL|method|getThreadIterator
specifier|public
name|Iterator
name|getThreadIterator
parameter_list|()
block|{
return|return
name|allThreads
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
comment|// return allThreads.iterator();
block|}
comment|/**      *  Description of the Method      *      *@param  queue  Description of the Parameter      */
DECL|method|setQueue
specifier|public
name|void
name|setQueue
parameter_list|(
name|TaskQueue
name|queue
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
block|}
DECL|method|getTaskQueue
specifier|public
name|TaskQueue
name|getTaskQueue
parameter_list|()
block|{
return|return
name|queue
return|;
block|}
block|}
end_class
end_unit
