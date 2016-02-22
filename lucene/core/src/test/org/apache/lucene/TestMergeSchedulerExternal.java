begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|PrintStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|document
operator|.
name|Field
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
name|IndexWriterConfig
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
name|LogMergePolicy
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
operator|.
name|OneMerge
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
name|MergeScheduler
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
name|MergeTrigger
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
name|store
operator|.
name|MockDirectoryWrapper
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
name|RAMDirectory
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
name|IOUtils
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
name|PrintStreamInfoStream
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
DECL|field|infoStream
specifier|volatile
specifier|static
name|InfoStream
name|infoStream
decl_stmt|;
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
name|Directory
name|dir
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|excCalled
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"IW"
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
literal|"IW"
argument_list|,
literal|"TEST: now handleMergeException"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doMerge
specifier|protected
name|void
name|doMerge
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
name|mergeCalled
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|doMerge
argument_list|(
name|writer
argument_list|,
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
name|MockDirectoryWrapper
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
name|MockDirectoryWrapper
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
block|{
name|IOException
name|ioe
init|=
operator|new
name|IOException
argument_list|(
literal|"now failing during merge"
argument_list|)
decl_stmt|;
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|ioe
operator|.
name|printStackTrace
argument_list|(
name|pw
argument_list|)
expr_stmt|;
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"IW"
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
literal|"IW"
argument_list|,
literal|"TEST: now throw exc:\n"
operator|+
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ioe
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|infoStream
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testSubclassConcurrentMergeScheduler
specifier|public
name|void
name|testSubclassConcurrentMergeScheduler
parameter_list|()
throws|throws
name|IOException
block|{
name|MockDirectoryWrapper
name|dir
init|=
name|newMockDirectory
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
name|newStringField
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
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
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
operator|new
name|MyMergeScheduler
argument_list|()
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|infoStream
operator|=
operator|new
name|PrintStreamInfoStream
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|,
literal|true
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setInfoStream
argument_list|(
name|infoStream
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
name|iwc
argument_list|)
decl_stmt|;
name|LogMergePolicy
name|logMP
init|=
operator|(
name|LogMergePolicy
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|logMP
operator|.
name|setMergeFactor
argument_list|(
literal|10
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
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|(
operator|(
name|MyMergeScheduler
operator|)
name|writer
operator|.
name|getConfig
argument_list|()
operator|.
name|getMergeScheduler
argument_list|()
operator|)
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// OK
block|}
name|writer
operator|.
name|rollback
argument_list|()
expr_stmt|;
try|try
block|{
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
block|}
catch|catch
parameter_list|(
name|AssertionError
name|ae
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST FAILED; IW infoStream output:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|baos
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|ae
throw|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|ReportingMergeScheduler
specifier|private
specifier|static
class|class
name|ReportingMergeScheduler
extends|extends
name|MergeScheduler
block|{
annotation|@
name|Override
DECL|method|merge
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
name|OneMerge
name|merge
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|merge
operator|=
name|writer
operator|.
name|getNextMerge
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"executing merge "
operator|+
name|merge
operator|.
name|segString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|merge
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
DECL|method|testCustomMergeScheduler
specifier|public
name|void
name|testCustomMergeScheduler
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we don't really need to execute anything, just to make sure the custom MS
comment|// compiles. But ensure that it can be used as well, e.g., no other hidden
comment|// dependencies or something. Therefore, don't use any random API !
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|ReportingMergeScheduler
argument_list|()
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
name|conf
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// trigger flush
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// trigger flush
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
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
