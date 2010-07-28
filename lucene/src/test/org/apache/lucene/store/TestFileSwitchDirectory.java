begin_unit
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
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
name|index
operator|.
name|IndexFileNames
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
name|TestIndexWriterReader
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
begin_class
DECL|class|TestFileSwitchDirectory
specifier|public
class|class
name|TestFileSwitchDirectory
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Test if writing doc stores to disk and everything else to ram works.    * @throws IOException    */
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|fileExtensions
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|fileExtensions
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|FIELDS_EXTENSION
argument_list|)
expr_stmt|;
name|fileExtensions
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|FIELDS_INDEX_EXTENSION
argument_list|)
expr_stmt|;
name|Directory
name|primaryDir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|RAMDirectory
name|secondaryDir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|FileSwitchDirectory
name|fsd
init|=
operator|new
name|FileSwitchDirectory
argument_list|(
name|fileExtensions
argument_list|,
name|primaryDir
argument_list|,
name|secondaryDir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|fsd
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
operator|(
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
operator|)
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TestIndexWriterReader
operator|.
name|createIndexNoClose
argument_list|(
literal|true
argument_list|,
literal|"ram"
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// we should see only fdx,fdt files here
name|String
index|[]
name|files
init|=
name|primaryDir
operator|.
name|listAll
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|files
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|files
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|String
name|ext
init|=
name|FileSwitchDirectory
operator|.
name|getExtension
argument_list|(
name|files
index|[
name|x
index|]
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fileExtensions
operator|.
name|contains
argument_list|(
name|ext
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|files
operator|=
name|secondaryDir
operator|.
name|listAll
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|files
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// we should not see fdx,fdt files here
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|files
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|String
name|ext
init|=
name|FileSwitchDirectory
operator|.
name|getExtension
argument_list|(
name|files
index|[
name|x
index|]
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fileExtensions
operator|.
name|contains
argument_list|(
name|ext
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|files
operator|=
name|fsd
operator|.
name|listAll
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertNotNull
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|fsd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
