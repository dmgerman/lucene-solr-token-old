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
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
operator|.
name|LuceneTaxonomyReader
import|;
end_import
begin_comment
comment|/**  * Open a taxonomy index reader.  *<br>Other side effects: taxonomy reader object in perfRunData is set.  */
end_comment
begin_class
DECL|class|OpenTaxonomyReaderTask
specifier|public
class|class
name|OpenTaxonomyReaderTask
extends|extends
name|PerfTask
block|{
DECL|method|OpenTaxonomyReaderTask
specifier|public
name|OpenTaxonomyReaderTask
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
name|IOException
block|{
name|PerfRunData
name|runData
init|=
name|getRunData
argument_list|()
decl_stmt|;
name|LuceneTaxonomyReader
name|taxoReader
init|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|runData
operator|.
name|getTaxonomyDir
argument_list|()
argument_list|)
decl_stmt|;
name|runData
operator|.
name|setTaxonomyReader
argument_list|(
name|taxoReader
argument_list|)
expr_stmt|;
comment|// We transfer reference to the run data
name|taxoReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
end_class
end_unit
