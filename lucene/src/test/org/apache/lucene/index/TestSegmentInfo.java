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
name|document
operator|.
name|Field
operator|.
name|Index
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
operator|.
name|Store
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestSegmentInfo
specifier|public
class|class
name|TestSegmentInfo
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSizeInBytesCache
specifier|public
name|void
name|testSizeInBytesCache
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
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
name|setInfoStream
argument_list|(
name|VERBOSE
condition|?
name|System
operator|.
name|out
else|:
literal|null
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"a"
argument_list|,
literal|"value"
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|SegmentInfo
name|si
init|=
name|sis
operator|.
name|info
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|sizeInBytesNoStore
init|=
name|si
operator|.
name|sizeInBytes
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|long
name|sizeInBytesWithStore
init|=
name|si
operator|.
name|sizeInBytes
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"sizeInBytesNoStore="
operator|+
name|sizeInBytesNoStore
operator|+
literal|" sizeInBytesWithStore="
operator|+
name|sizeInBytesWithStore
argument_list|,
name|sizeInBytesWithStore
operator|>
name|sizeInBytesNoStore
argument_list|)
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
