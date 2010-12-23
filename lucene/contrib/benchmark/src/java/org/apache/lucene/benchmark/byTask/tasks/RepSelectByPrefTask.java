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
name|List
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
begin_comment
comment|/**  * Report by-name-prefix statistics with no aggregations.  *<br>Other side effects: None.  */
end_comment
begin_class
DECL|class|RepSelectByPrefTask
specifier|public
class|class
name|RepSelectByPrefTask
extends|extends
name|RepSumByPrefTask
block|{
DECL|method|RepSelectByPrefTask
specifier|public
name|RepSelectByPrefTask
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
name|Report
name|rp
init|=
name|reportSelectByPrefix
argument_list|(
name|getRunData
argument_list|()
operator|.
name|getPoints
argument_list|()
operator|.
name|taskStats
argument_list|()
argument_list|)
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
literal|"------------> Report Select By Prefix ("
operator|+
name|prefix
operator|+
literal|") ("
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
DECL|method|reportSelectByPrefix
specifier|protected
name|Report
name|reportSelectByPrefix
parameter_list|(
name|List
argument_list|<
name|TaskStats
argument_list|>
name|taskStats
parameter_list|)
block|{
name|String
name|longestOp
init|=
name|longestOp
argument_list|(
name|taskStats
argument_list|)
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|tableTitle
argument_list|(
name|longestOp
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
name|int
name|reported
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|TaskStats
name|stat
range|:
name|taskStats
control|)
block|{
if|if
condition|(
name|stat
operator|.
name|getElapsed
argument_list|()
operator|>=
literal|0
operator|&&
name|stat
operator|.
name|getTask
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
comment|// only ended tasks with proper name
name|reported
operator|++
expr_stmt|;
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|newline
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
name|String
name|line
init|=
name|taskReportLine
argument_list|(
name|longestOp
argument_list|,
name|stat
argument_list|)
decl_stmt|;
if|if
condition|(
name|taskStats
operator|.
name|size
argument_list|()
operator|>
literal|2
operator|&&
name|reported
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|replaceAll
argument_list|(
literal|"   "
argument_list|,
literal|" - "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|reptxt
init|=
operator|(
name|reported
operator|==
literal|0
condition|?
literal|"No Matching Entries Were Found!"
else|:
name|sb
operator|.
name|toString
argument_list|()
operator|)
decl_stmt|;
return|return
operator|new
name|Report
argument_list|(
name|reptxt
argument_list|,
name|reported
argument_list|,
name|reported
argument_list|,
name|taskStats
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
