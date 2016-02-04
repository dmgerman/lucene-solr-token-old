begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
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
name|Executors
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
name|ThreadFactory
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
begin_comment
comment|/**  * A default {@link ThreadFactory} implementation that accepts the name prefix  * of the created threads as a constructor argument. Otherwise, this factory  * yields the same semantics as the thread factory returned by  * {@link Executors#defaultThreadFactory()}.  */
end_comment
begin_class
DECL|class|NamedThreadFactory
specifier|public
class|class
name|NamedThreadFactory
implements|implements
name|ThreadFactory
block|{
DECL|field|threadPoolNumber
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|threadPoolNumber
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|group
specifier|private
specifier|final
name|ThreadGroup
name|group
decl_stmt|;
DECL|field|threadNumber
specifier|private
specifier|final
name|AtomicInteger
name|threadNumber
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|NAME_PATTERN
specifier|private
specifier|static
specifier|final
name|String
name|NAME_PATTERN
init|=
literal|"%s-%d-thread"
decl_stmt|;
DECL|field|threadNamePrefix
specifier|private
specifier|final
name|String
name|threadNamePrefix
decl_stmt|;
comment|/**    * Creates a new {@link NamedThreadFactory} instance    *     * @param threadNamePrefix the name prefix assigned to each thread created.    */
DECL|method|NamedThreadFactory
specifier|public
name|NamedThreadFactory
parameter_list|(
name|String
name|threadNamePrefix
parameter_list|)
block|{
specifier|final
name|SecurityManager
name|s
init|=
name|System
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
name|group
operator|=
operator|(
name|s
operator|!=
literal|null
operator|)
condition|?
name|s
operator|.
name|getThreadGroup
argument_list|()
else|:
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getThreadGroup
argument_list|()
expr_stmt|;
name|this
operator|.
name|threadNamePrefix
operator|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|NAME_PATTERN
argument_list|,
name|checkPrefix
argument_list|(
name|threadNamePrefix
argument_list|)
argument_list|,
name|threadPoolNumber
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkPrefix
specifier|private
specifier|static
name|String
name|checkPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
name|prefix
operator|==
literal|null
operator|||
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|"Lucene"
else|:
name|prefix
return|;
block|}
comment|/**    * Creates a new {@link Thread}    *     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)    */
annotation|@
name|Override
DECL|method|newThread
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
specifier|final
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|group
argument_list|,
name|r
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s-%d"
argument_list|,
name|this
operator|.
name|threadNamePrefix
argument_list|,
name|threadNumber
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|t
operator|.
name|setDaemon
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|t
operator|.
name|setPriority
argument_list|(
name|Thread
operator|.
name|NORM_PRIORITY
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
end_class
end_unit
