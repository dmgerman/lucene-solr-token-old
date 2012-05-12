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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ExecutorService
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
name|search
operator|.
name|IndexSearcher
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressCodecs
import|;
end_import
begin_comment
comment|// TODO
end_comment
begin_comment
comment|//   - mix in forceMerge, addIndexes
end_comment
begin_comment
comment|//   - randomoly mix in non-congruent docs
end_comment
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"SimpleText"
block|,
literal|"Memory"
block|}
argument_list|)
DECL|class|TestNRTThreads
specifier|public
class|class
name|TestNRTThreads
extends|extends
name|ThreadedIndexingAndSearchingTestCase
block|{
annotation|@
name|Override
DECL|method|doSearching
specifier|protected
name|void
name|doSearching
parameter_list|(
name|ExecutorService
name|es
parameter_list|,
name|long
name|stopTime
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|anyOpenDelFiles
init|=
literal|false
decl_stmt|;
name|DirectoryReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|stopTime
operator|&&
operator|!
name|failed
operator|.
name|get
argument_list|()
condition|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
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
literal|"TEST: now reopen r="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DirectoryReader
name|r2
init|=
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|r2
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|r2
expr_stmt|;
block|}
block|}
else|else
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
literal|"TEST: now close reader="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|openDeletedFiles
init|=
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|getOpenDeletedFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|openDeletedFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OBD files: "
operator|+
name|openDeletedFiles
argument_list|)
expr_stmt|;
block|}
name|anyOpenDelFiles
operator||=
name|openDeletedFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
expr_stmt|;
comment|//assertEquals("open but deleted: " + openDeletedFiles, 0, openDeletedFiles.size());
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
literal|"TEST: now open"
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
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
literal|"TEST: got new reader="
operator|+
name|r
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("numDocs=" + r.numDocs() + "
comment|//openDelFileCount=" + dir.openDeleteFileCount());
if|if
condition|(
name|r
operator|.
name|numDocs
argument_list|()
operator|>
literal|0
condition|)
block|{
name|fixedSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|,
name|es
argument_list|)
expr_stmt|;
name|smokeTestSearcher
argument_list|(
name|fixedSearcher
argument_list|)
expr_stmt|;
name|runSearchThreads
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//System.out.println("numDocs=" + r.numDocs() + " openDelFileCount=" + dir.openDeleteFileCount());
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|openDeletedFiles
init|=
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|getOpenDeletedFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|openDeletedFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OBD files: "
operator|+
name|openDeletedFiles
argument_list|)
expr_stmt|;
block|}
name|anyOpenDelFiles
operator||=
name|openDeletedFiles
operator|.
name|size
argument_list|()
operator|>
literal|0
expr_stmt|;
name|assertFalse
argument_list|(
literal|"saw non-zero open-but-deleted count"
argument_list|,
name|anyOpenDelFiles
argument_list|)
expr_stmt|;
block|}
DECL|field|fixedSearcher
specifier|private
name|IndexSearcher
name|fixedSearcher
decl_stmt|;
DECL|method|getCurrentSearcher
specifier|protected
name|IndexSearcher
name|getCurrentSearcher
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|fixedSearcher
return|;
block|}
annotation|@
name|Override
DECL|method|releaseSearcher
specifier|protected
name|void
name|releaseSearcher
parameter_list|(
name|IndexSearcher
name|s
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|s
operator|!=
name|fixedSearcher
condition|)
block|{
comment|// Final searcher:
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFinalSearcher
specifier|protected
name|IndexSearcher
name|getFinalSearcher
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|IndexReader
name|r2
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|r2
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|r2
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
return|return
name|newSearcher
argument_list|(
name|r2
argument_list|)
return|;
block|}
DECL|method|testNRTThreads
specifier|public
name|void
name|testNRTThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
literal|"TestNRTThreads"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
