begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.stats
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|stats
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * This class holds a data point measuring speed of processing.  *  * @author Andrzej Bialecki&lt;ab@getopt.org&gt;  */
end_comment
begin_class
DECL|class|TimeData
specifier|public
class|class
name|TimeData
block|{
comment|/** Name of the data point - usually one of a data series with the same name */
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
comment|/** Number of records processed. */
DECL|field|count
specifier|public
name|long
name|count
init|=
literal|0
decl_stmt|;
comment|/** Elapsed time in milliseconds. */
DECL|field|elapsed
specifier|public
name|long
name|elapsed
init|=
literal|0L
decl_stmt|;
DECL|field|delta
specifier|private
name|long
name|delta
init|=
literal|0L
decl_stmt|;
comment|/** Free memory at the end of measurement interval. */
DECL|field|freeMem
specifier|public
name|long
name|freeMem
init|=
literal|0L
decl_stmt|;
comment|/** Total memory at the end of measurement interval. */
DECL|field|totalMem
specifier|public
name|long
name|totalMem
init|=
literal|0L
decl_stmt|;
DECL|method|TimeData
specifier|public
name|TimeData
parameter_list|()
block|{}
empty_stmt|;
DECL|method|TimeData
specifier|public
name|TimeData
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/** Start counting elapsed time. */
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
block|{
name|delta
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
comment|/** Stop counting elapsed time. */
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|count
operator|++
expr_stmt|;
name|elapsed
operator|+=
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|delta
operator|)
expr_stmt|;
block|}
comment|/** Record memory usage. */
DECL|method|recordMemUsage
specifier|public
name|void
name|recordMemUsage
parameter_list|()
block|{
name|freeMem
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|freeMemory
argument_list|()
expr_stmt|;
name|totalMem
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|totalMemory
argument_list|()
expr_stmt|;
block|}
comment|/** Reset counters. */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|count
operator|=
literal|0
expr_stmt|;
name|elapsed
operator|=
literal|0L
expr_stmt|;
name|delta
operator|=
name|elapsed
expr_stmt|;
block|}
DECL|method|clone
specifier|protected
name|Object
name|clone
parameter_list|()
block|{
name|TimeData
name|td
init|=
operator|new
name|TimeData
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|td
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|td
operator|.
name|elapsed
operator|=
name|elapsed
expr_stmt|;
name|td
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|td
operator|.
name|delta
operator|=
name|delta
expr_stmt|;
name|td
operator|.
name|freeMem
operator|=
name|freeMem
expr_stmt|;
name|td
operator|.
name|totalMem
operator|=
name|totalMem
expr_stmt|;
return|return
name|td
return|;
block|}
comment|/** Get rate of processing, defined as number of processed records per second. */
DECL|method|getRate
specifier|public
name|double
name|getRate
parameter_list|()
block|{
name|double
name|rps
init|=
operator|(
name|double
operator|)
name|count
operator|*
literal|1000.0
operator|/
call|(
name|double
call|)
argument_list|(
name|elapsed
operator|>
literal|0
condition|?
name|elapsed
else|:
literal|1
argument_list|)
decl_stmt|;
comment|// assume atleast 1ms for any countable op
return|return
name|rps
return|;
block|}
comment|/** Get a short legend for toString() output. */
DECL|method|getLabels
specifier|public
specifier|static
name|String
name|getLabels
parameter_list|()
block|{
return|return
literal|"# count\telapsed\trec/s\tfreeMem\ttotalMem"
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|true
argument_list|)
return|;
block|}
comment|/**    * Return a tab-seprated string containing this data.    * @param withMem if true, append also memory information    * @return The String    */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|boolean
name|withMem
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|count
operator|+
literal|"\t"
operator|+
name|elapsed
operator|+
literal|"\t"
operator|+
name|getRate
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|withMem
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"\t"
operator|+
name|freeMem
operator|+
literal|"\t"
operator|+
name|totalMem
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
