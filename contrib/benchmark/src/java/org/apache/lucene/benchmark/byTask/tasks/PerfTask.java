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
name|stats
operator|.
name|Points
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
name|stats
operator|.
name|TaskStats
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
name|utils
operator|.
name|Format
import|;
end_import
begin_comment
comment|/**  * A (abstract)  task to be tested for performance.  *<br>  * Every performance task extends this class, and provides its own doLogic() method,   * which performss the actual task.  *<br>  * Tasks performing some work that should be measured for the task, can overide setup() and/or tearDown() and   * placed that work there.   *<br>  * Relevant properties:<code>task.max.depth.log</code>.  */
end_comment
begin_class
DECL|class|PerfTask
specifier|public
specifier|abstract
class|class
name|PerfTask
implements|implements
name|Cloneable
block|{
DECL|field|runData
specifier|private
name|PerfRunData
name|runData
decl_stmt|;
comment|// propeties that all tasks have
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|depth
specifier|private
name|int
name|depth
init|=
literal|0
decl_stmt|;
DECL|field|maxDepthLogStart
specifier|private
name|int
name|maxDepthLogStart
init|=
literal|0
decl_stmt|;
DECL|field|params
specifier|private
name|String
name|params
init|=
literal|null
decl_stmt|;
DECL|field|NEW_LINE
specifier|protected
specifier|static
specifier|final
name|String
name|NEW_LINE
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
comment|/**    * Should not be used externally    */
DECL|method|PerfTask
specifier|private
name|PerfTask
parameter_list|()
block|{
name|name
operator|=
name|Format
operator|.
name|simpleName
argument_list|(
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|"Task"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|4
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|PerfTask
specifier|public
name|PerfTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|this
operator|.
name|runData
operator|=
name|runData
expr_stmt|;
name|this
operator|.
name|maxDepthLogStart
operator|=
name|runData
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
literal|"task.max.depth.log"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
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
comment|// tasks having non primitive data structures should overide this.
comment|// otherwise parallel running of a task sequence might not run crrectly.
return|return
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
comment|/**    * Run the task, record statistics.    * @return number of work items done by this task.    */
DECL|method|runAndMaybeStats
specifier|public
specifier|final
name|int
name|runAndMaybeStats
parameter_list|(
name|boolean
name|reportStats
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|reportStats
operator|&&
name|depth
operator|<=
name|maxDepthLogStart
operator|&&
operator|!
name|shouldNeverLogAtStart
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------------> starting task: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shouldNotRecordStats
argument_list|()
operator|||
operator|!
name|reportStats
condition|)
block|{
name|setup
argument_list|()
expr_stmt|;
name|int
name|count
init|=
name|doLogic
argument_list|()
decl_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
return|return
name|count
return|;
block|}
name|setup
argument_list|()
expr_stmt|;
name|Points
name|pnts
init|=
name|runData
operator|.
name|getPoints
argument_list|()
decl_stmt|;
name|TaskStats
name|ts
init|=
name|pnts
operator|.
name|markTaskStart
argument_list|(
name|this
argument_list|,
name|runData
operator|.
name|getConfig
argument_list|()
operator|.
name|getRoundNumber
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|doLogic
argument_list|()
decl_stmt|;
name|pnts
operator|.
name|markTaskEnd
argument_list|(
name|ts
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
return|return
name|count
return|;
block|}
comment|/**    * Perform the task once (ignoring repetions specification)    * Return number of work items done by this task.    * For indexing that can be number of docs added.    * For warming that can be number of scanned items, etc.    * @return number of work items done by this task.    */
DECL|method|doLogic
specifier|public
specifier|abstract
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * @return Returns the name.    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
return|return
name|name
return|;
block|}
return|return
operator|new
name|StringBuffer
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|params
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @param name The name to set.    */
DECL|method|setName
specifier|protected
name|void
name|setName
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
comment|/**    * @return Returns the run data.    */
DECL|method|getRunData
specifier|public
name|PerfRunData
name|getRunData
parameter_list|()
block|{
return|return
name|runData
return|;
block|}
comment|/**    * @return Returns the depth.    */
DECL|method|getDepth
specifier|public
name|int
name|getDepth
parameter_list|()
block|{
return|return
name|depth
return|;
block|}
comment|/**    * @param depth The depth to set.    */
DECL|method|setDepth
specifier|public
name|void
name|setDepth
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
block|}
comment|// compute a blank string padding for printing this task indented by its depth
DECL|method|getPadding
name|String
name|getPadding
parameter_list|()
block|{
name|char
name|c
index|[]
init|=
operator|new
name|char
index|[
literal|4
operator|*
name|getDepth
argument_list|()
index|]
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
name|c
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|c
index|[
name|i
index|]
operator|=
literal|' '
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|c
argument_list|)
return|;
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
name|padd
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @return Returns the maxDepthLogStart.    */
DECL|method|getMaxDepthLogStart
name|int
name|getMaxDepthLogStart
parameter_list|()
block|{
return|return
name|maxDepthLogStart
return|;
block|}
comment|/**    * Tasks that should never log at start can overide this.      * @return true if this task should never log when it start.    */
DECL|method|shouldNeverLogAtStart
specifier|protected
name|boolean
name|shouldNeverLogAtStart
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Tasks that should not record statistics can overide this.      * @return true if this task should never record its statistics.    */
DECL|method|shouldNotRecordStats
specifier|protected
name|boolean
name|shouldNotRecordStats
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Task setup work that should not be measured for that specific task.    * By default it does nothing, but tasks can implement this, moving work from     * doLogic() to this method. Only the work done in doLogicis measured for this task.    * Notice that higher level (sequence) tasks containing this task would then     * measure larger time than the sum of their contained tasks.    * @throws Exception     */
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{   }
comment|/**    * Task tearDown work that should not be measured for that specific task.    * By default it does nothing, but tasks can implement this, moving work from     * doLogic() to this method. Only the work done in doLogicis measured for this task.    * Notice that higher level (sequence) tasks containing this task would then     * measure larger time than the sum of their contained tasks.    */
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{   }
comment|/**    * Sub classes that supports parameters must overide this method to return true.    * @return true iff this task supports command line params.    */
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Set the params of this task.    * @exception UnsupportedOperationException for tasks supporting command line parameters.    */
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
if|if
condition|(
operator|!
name|supportsParams
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|getName
argument_list|()
operator|+
literal|" does not support command line parameters."
argument_list|)
throw|;
block|}
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
comment|/**    * @return Returns the Params.    */
DECL|method|getParams
specifier|public
name|String
name|getParams
parameter_list|()
block|{
return|return
name|params
return|;
block|}
block|}
end_class
end_unit
