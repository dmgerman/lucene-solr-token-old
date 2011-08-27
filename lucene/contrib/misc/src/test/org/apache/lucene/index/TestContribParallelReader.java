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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|FieldSelector
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
name|FieldSelectorVisitor
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
name|MapFieldSelector
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
name|TextField
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
name|*
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
begin_class
DECL|class|TestContribParallelReader
specifier|public
class|class
name|TestContribParallelReader
extends|extends
name|LuceneTestCase
block|{
DECL|field|parallel
specifier|private
name|IndexSearcher
name|parallel
decl_stmt|;
DECL|field|single
specifier|private
name|IndexSearcher
name|single
decl_stmt|;
DECL|field|dir
DECL|field|dir1
DECL|field|dir2
specifier|private
name|Directory
name|dir
decl_stmt|,
name|dir1
decl_stmt|,
name|dir2
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|single
operator|=
name|single
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|parallel
operator|=
name|parallel
argument_list|(
name|random
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|single
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|single
operator|.
name|close
argument_list|()
expr_stmt|;
name|parallel
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|parallel
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|// Fields 1-4 indexed together:
DECL|method|single
specifier|private
name|IndexSearcher
name|single
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|Document
name|d2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f3"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f4"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|// Fields 1& 2 in one index, 3& 4 in other, with ParallelReader:
DECL|method|parallel
specifier|private
name|IndexSearcher
name|parallel
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|dir1
operator|=
name|getDir1
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|dir2
operator|=
name|getDir2
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|ParallelReader
name|pr
init|=
operator|new
name|ParallelReader
argument_list|()
decl_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|newSearcher
argument_list|(
name|pr
argument_list|)
return|;
block|}
DECL|method|getDocument
specifier|private
name|Document
name|getDocument
parameter_list|(
name|IndexReader
name|ir
parameter_list|,
name|int
name|docID
parameter_list|,
name|FieldSelector
name|selector
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FieldSelectorVisitor
name|visitor
init|=
operator|new
name|FieldSelectorVisitor
argument_list|(
name|selector
argument_list|)
decl_stmt|;
name|ir
operator|.
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
return|return
name|visitor
operator|.
name|getDocument
argument_list|()
return|;
block|}
DECL|method|testDocument
specifier|public
name|void
name|testDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
name|getDir1
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|Directory
name|dir2
init|=
name|getDir2
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|ParallelReader
name|pr
init|=
operator|new
name|ParallelReader
argument_list|()
decl_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|Document
name|doc11
init|=
name|getDocument
argument_list|(
name|pr
argument_list|,
literal|0
argument_list|,
operator|new
name|MapFieldSelector
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc24
init|=
name|getDocument
argument_list|(
name|pr
argument_list|,
literal|1
argument_list|,
operator|new
name|MapFieldSelector
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc223
init|=
name|getDocument
argument_list|(
name|pr
argument_list|,
literal|1
argument_list|,
operator|new
name|MapFieldSelector
argument_list|(
literal|"f2"
argument_list|,
literal|"f3"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc11
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc24
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|doc223
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v1"
argument_list|,
name|doc11
operator|.
name|get
argument_list|(
literal|"f1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v2"
argument_list|,
name|doc24
operator|.
name|get
argument_list|(
literal|"f4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v2"
argument_list|,
name|doc223
operator|.
name|get
argument_list|(
literal|"f2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"v2"
argument_list|,
name|doc223
operator|.
name|get
argument_list|(
literal|"f3"
argument_list|)
argument_list|)
expr_stmt|;
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getDir1
specifier|private
name|Directory
name|getDir1
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w1
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir1
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w1
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|Document
name|d2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f1"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d2
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f2"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w1
operator|.
name|addDocument
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|w1
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|dir1
return|;
block|}
DECL|method|getDir2
specifier|private
name|Directory
name|getDir2
parameter_list|(
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir2
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|d3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f3"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d3
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f4"
argument_list|,
literal|"v1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
name|d3
argument_list|)
expr_stmt|;
name|Document
name|d4
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d4
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f3"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|d4
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f4"
argument_list|,
literal|"v2"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w2
operator|.
name|addDocument
argument_list|(
name|d4
argument_list|)
expr_stmt|;
name|w2
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|dir2
return|;
block|}
block|}
end_class
end_unit
