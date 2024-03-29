begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
comment|/**  * Reset all index and input data and call gc, does NOT erase index/dir, does NOT clear statistics.  * This contains ResetInputs.  *<br>Other side effects: writers/readers nullified, closed.  * Index is NOT erased.  * Directory is NOT erased.  */
end_comment
begin_class
DECL|class|ResetSystemSoftTask
specifier|public
class|class
name|ResetSystemSoftTask
extends|extends
name|ResetInputsTask
block|{
DECL|method|ResetSystemSoftTask
specifier|public
name|ResetSystemSoftTask
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
annotation|@
name|Override
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
literal|false
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
end_class
end_unit
