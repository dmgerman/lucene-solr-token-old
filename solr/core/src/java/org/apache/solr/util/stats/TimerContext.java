begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/*  * Forked from https://github.com/codahale/metrics  */
end_comment
begin_package
DECL|package|org.apache.solr.util.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|stats
package|;
end_package
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
begin_comment
comment|/**  * A timing context.  *  * @see Timer#time()  */
end_comment
begin_class
DECL|class|TimerContext
specifier|public
class|class
name|TimerContext
block|{
DECL|field|timer
specifier|private
specifier|final
name|Timer
name|timer
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
comment|/**    * Creates a new {@link TimerContext} with the current time as its starting value and with the    * given {@link Timer}.    *    * @param timer the {@link Timer} to report the elapsed time to    */
DECL|method|TimerContext
name|TimerContext
parameter_list|(
name|Timer
name|timer
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|this
operator|.
name|timer
operator|=
name|timer
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|clock
operator|.
name|getTick
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stops recording the elapsed time, updates the timer and returns the elapsed time    */
DECL|method|stop
specifier|public
name|long
name|stop
parameter_list|()
block|{
specifier|final
name|long
name|elapsedNanos
init|=
name|clock
operator|.
name|getTick
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|timer
operator|.
name|update
argument_list|(
name|elapsedNanos
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
return|return
name|elapsedNanos
return|;
block|}
block|}
end_class
end_unit
