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
name|Report
import|;
end_import
begin_comment
comment|/**  * Report all statistics aggregated by name.  * Other side effects: None.  */
end_comment
begin_class
DECL|class|RepSumByNameTask
specifier|public
class|class
name|RepSumByNameTask
extends|extends
name|ReportTask
block|{
DECL|method|RepSumByNameTask
specifier|public
name|RepSumByNameTask
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
name|Report
name|rp
init|=
name|getRunData
argument_list|()
operator|.
name|getPoints
argument_list|()
operator|.
name|reportSumByName
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"------------> Report Sum By (any) Name ("
operator|+
name|rp
operator|.
name|getSize
argument_list|()
operator|+
literal|" about "
operator|+
name|rp
operator|.
name|getReported
argument_list|()
operator|+
literal|" out of "
operator|+
name|rp
operator|.
name|getOutOf
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rp
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
end_class
end_unit
