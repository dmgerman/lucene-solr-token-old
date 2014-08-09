begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockAnalyzer
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
name|document
operator|.
name|Document
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
name|store
operator|.
name|Directory
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
name|LuceneTestCase
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
name|TestUtil
import|;
end_import
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
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import
begin_comment
comment|/**  * Base test case for {@link MergePolicy}.  */
end_comment
begin_class
DECL|class|BaseMergePolicyTestCase
specifier|public
specifier|abstract
class|class
name|BaseMergePolicyTestCase
extends|extends
name|LuceneTestCase
block|{
comment|/** Create a new {@link MergePolicy} instance. */
DECL|method|mergePolicy
specifier|protected
specifier|abstract
name|MergePolicy
name|mergePolicy
parameter_list|()
function_decl|;
DECL|method|testForceMergeNotNeeded
specifier|public
name|void
name|testForceMergeNotNeeded
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|mayMerge
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|MergeScheduler
name|mergeScheduler
init|=
operator|new
name|SerialMergeScheduler
argument_list|()
block|{
annotation|@
name|Override
specifier|synchronized
specifier|public
name|void
name|merge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|MergeTrigger
name|trigger
parameter_list|,
name|boolean
name|newMergesFound
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|mayMerge
operator|.
name|get
argument_list|()
operator|&&
name|writer
operator|.
name|getNextMerge
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|super
operator|.
name|merge
argument_list|(
name|writer
argument_list|,
name|trigger
argument_list|,
name|newMergesFound
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergeScheduler
argument_list|(
name|mergeScheduler
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|mergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
operator|.
name|setNoCFSRatio
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|0
else|:
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numSegments
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|20
argument_list|)
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
name|numSegments
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|numDocs
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDocs
condition|;
operator|++
name|j
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|getReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|>=
literal|0
condition|;
operator|--
name|i
control|)
block|{
specifier|final
name|int
name|segmentCount
init|=
name|writer
operator|.
name|getSegmentCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxNumSegments
init|=
name|i
operator|==
literal|0
condition|?
literal|1
else|:
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|mayMerge
operator|.
name|set
argument_list|(
name|segmentCount
operator|>
name|maxNumSegments
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
name|maxNumSegments
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
