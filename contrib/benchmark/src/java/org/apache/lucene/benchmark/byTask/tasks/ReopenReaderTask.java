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
name|io
operator|.
name|IOException
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
name|index
operator|.
name|IndexReader
import|;
end_import
begin_comment
comment|/** * Reopens IndexReader and closes old IndexReader. * */
end_comment
begin_class
DECL|class|ReopenReaderTask
specifier|public
class|class
name|ReopenReaderTask
extends|extends
name|PerfTask
block|{
DECL|method|ReopenReaderTask
specifier|public
name|ReopenReaderTask
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
name|IOException
block|{
name|IndexReader
name|ir
init|=
name|getRunData
argument_list|()
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|IndexReader
name|or
init|=
name|ir
decl_stmt|;
name|IndexReader
name|nr
init|=
name|ir
operator|.
name|reopen
argument_list|()
decl_stmt|;
if|if
condition|(
name|nr
operator|!=
name|or
condition|)
block|{
name|getRunData
argument_list|()
operator|.
name|setIndexReader
argument_list|(
name|nr
argument_list|)
expr_stmt|;
name|or
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
literal|1
return|;
block|}
block|}
end_class
end_unit
