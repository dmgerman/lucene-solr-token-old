begin_unit
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|MockRAMDirectory
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
name|index
operator|.
name|MergePolicy
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
name|ConcurrentMergeScheduler
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
name|document
operator|.
name|Field
import|;
end_import
begin_comment
comment|/**  * Holds tests cases to verify external APIs are accessible  * while not being in org.apache.lucene.index package.  */
end_comment
begin_class
DECL|class|TestMergeSchedulerExternal
specifier|public
class|class
name|TestMergeSchedulerExternal
extends|extends
name|LuceneTestCase
block|{
DECL|field|mergeCalled
specifier|volatile
name|boolean
name|mergeCalled
decl_stmt|;
DECL|field|mergeThreadCreated
specifier|volatile
name|boolean
name|mergeThreadCreated
decl_stmt|;
DECL|field|excCalled
specifier|volatile
name|boolean
name|excCalled
decl_stmt|;
DECL|class|MyMergeException
specifier|private
class|class
name|MyMergeException
extends|extends
name|RuntimeException
block|{
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
DECL|method|MyMergeException
specifier|public
name|MyMergeException
parameter_list|(
name|Throwable
name|exc
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|exc
argument_list|)
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
block|}
DECL|class|MyMergeScheduler
specifier|private
class|class
name|MyMergeScheduler
extends|extends
name|ConcurrentMergeScheduler
block|{
DECL|class|MyMergeThread
specifier|private
class|class
name|MyMergeThread
extends|extends
name|ConcurrentMergeScheduler
operator|.
name|MergeThread
block|{
DECL|method|MyMergeThread
specifier|public
name|MyMergeThread
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|writer
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|mergeThreadCreated
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getMergeThread
specifier|protected
name|MergeThread
name|getMergeThread
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
throws|throws
name|IOException
block|{
name|MergeThread
name|thread
init|=
operator|new
name|MyMergeThread
argument_list|(
name|writer
argument_list|,
name|merge
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setThreadPriority
argument_list|(
name|getMergeThreadPriority
argument_list|()
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|thread
operator|.
name|setName
argument_list|(
literal|"MyMergeThread"
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
annotation|@
name|Override
DECL|method|handleMergeException
specifier|protected
name|void
name|handleMergeException
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|excCalled
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doMerge
specifier|protected
name|void
name|doMerge
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
throws|throws
name|IOException
block|{
name|mergeCalled
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|doMerge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FailOnlyOnMerge
specifier|private
specifier|static
class|class
name|FailOnlyOnMerge
extends|extends
name|MockRAMDirectory
operator|.
name|Failure
block|{
annotation|@
name|Override
DECL|method|eval
specifier|public
name|void
name|eval
parameter_list|(
name|MockRAMDirectory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|StackTraceElement
index|[]
name|trace
init|=
operator|new
name|Exception
argument_list|()
operator|.
name|getStackTrace
argument_list|()
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
name|trace
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|"doMerge"
operator|.
name|equals
argument_list|(
name|trace
index|[
name|i
index|]
operator|.
name|getMethodName
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"now failing during merge"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|testSubclassConcurrentMergeScheduler
specifier|public
name|void
name|testSubclassConcurrentMergeScheduler
parameter_list|()
throws|throws
name|IOException
block|{
name|MockRAMDirectory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|dir
operator|.
name|failOn
argument_list|(
operator|new
name|FailOnlyOnMerge
argument_list|()
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|idField
init|=
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|MyMergeScheduler
name|ms
init|=
operator|new
name|MyMergeScheduler
argument_list|()
decl_stmt|;
name|writer
operator|.
name|setMergeScheduler
argument_list|(
name|ms
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|writer
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|ms
operator|.
name|sync
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|mergeThreadCreated
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mergeCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|excCalled
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ConcurrentMergeScheduler
operator|.
name|anyUnhandledExceptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
