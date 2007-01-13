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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Reset all index and input data and call gc, erase index and dir, does NOT clear statistics.  * This contains ResetInputs.  * Other side effects: writers/readers nulified, deleted, closed.  * Index is erased.  * Directory is erased.  */
end_comment
begin_class
DECL|class|ResetSystemEraseTask
specifier|public
class|class
name|ResetSystemEraseTask
extends|extends
name|PerfTask
block|{
DECL|method|ResetSystemEraseTask
specifier|public
name|ResetSystemEraseTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|getRunData
argument_list|()
operator|.
name|reinit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.lucene.benchmark.byTask.tasks.PerfTask#shouldNotRecordStats()    */
DECL|method|shouldNotRecordStats
specifier|protected
name|boolean
name|shouldNotRecordStats
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
