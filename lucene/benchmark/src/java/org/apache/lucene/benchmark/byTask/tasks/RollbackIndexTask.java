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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|IndexWriter
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
name|util
operator|.
name|InfoStream
import|;
end_import
begin_comment
comment|/**  * Rollback the index writer.  */
end_comment
begin_class
DECL|class|RollbackIndexTask
specifier|public
class|class
name|RollbackIndexTask
extends|extends
name|PerfTask
block|{
DECL|method|RollbackIndexTask
specifier|public
name|RollbackIndexTask
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
DECL|field|doWait
name|boolean
name|doWait
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriter
name|iw
init|=
name|getRunData
argument_list|()
operator|.
name|getIndexWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|iw
operator|!=
literal|null
condition|)
block|{
comment|// If infoStream was set to output to a file, close it.
name|InfoStream
name|infoStream
init|=
name|iw
operator|.
name|getConfig
argument_list|()
operator|.
name|getInfoStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|infoStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|iw
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|getRunData
argument_list|()
operator|.
name|setIndexWriter
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
literal|1
return|;
block|}
block|}
end_class
end_unit
