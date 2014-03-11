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
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|AlreadyClosedException
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
name|FieldType
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
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|ArrayList
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
begin_class
DECL|class|TestIndexWriterMerging
specifier|public
class|class
name|TestIndexWriterMerging
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Tests that index merging (specifically addIndexes(Directory...)) doesn't    * change the index order of documents.    */
DECL|method|testLucene
specifier|public
name|void
name|testLucene
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|num
init|=
literal|100
decl_stmt|;
name|Directory
name|indexA
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|indexB
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|fillIndex
argument_list|(
name|random
argument_list|()
argument_list|,
name|indexA
argument_list|,
literal|0
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|boolean
name|fail
init|=
name|verifyIndex
argument_list|(
name|indexA
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|fail
condition|)
block|{
name|fail
argument_list|(
literal|"Index a is invalid"
argument_list|)
expr_stmt|;
block|}
name|fillIndex
argument_list|(
name|random
argument_list|()
argument_list|,
name|indexB
argument_list|,
name|num
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|fail
operator|=
name|verifyIndex
argument_list|(
name|indexB
argument_list|,
name|num
argument_list|)
expr_stmt|;
if|if
condition|(
name|fail
condition|)
block|{
name|fail
argument_list|(
literal|"Index b is invalid"
argument_list|)
expr_stmt|;
block|}
name|Directory
name|merged
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|merged
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addIndexes
argument_list|(
name|indexA
argument_list|,
name|indexB
argument_list|)
expr_stmt|;
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
name|fail
operator|=
name|verifyIndex
argument_list|(
name|merged
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"The merged index is invalid"
argument_list|,
name|fail
argument_list|)
expr_stmt|;
name|indexA
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexB
operator|.
name|close
argument_list|()
expr_stmt|;
name|merged
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyIndex
specifier|private
name|boolean
name|verifyIndex
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|int
name|startAt
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|int
name|max
init|=
name|reader
operator|.
name|maxDoc
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
name|max
condition|;
name|i
operator|++
control|)
block|{
name|StoredDocument
name|temp
init|=
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|//System.out.println("doc "+i+"="+temp.getField("count").stringValue());
comment|//compare the index doc number to the value that it should be
if|if
condition|(
operator|!
name|temp
operator|.
name|getField
argument_list|(
literal|"count"
argument_list|)
operator|.
name|stringValue
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
name|i
operator|+
name|startAt
operator|)
operator|+
literal|""
argument_list|)
condition|)
block|{
name|fail
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Document "
operator|+
operator|(
name|i
operator|+
name|startAt
operator|)
operator|+
literal|" is returning document "
operator|+
name|temp
operator|.
name|getField
argument_list|(
literal|"count"
argument_list|)
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|fail
return|;
block|}
DECL|method|fillIndex
specifier|private
name|void
name|fillIndex
parameter_list|(
name|Random
name|random
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
operator|(
name|start
operator|+
name|numDocs
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|temp
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|temp
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"count"
argument_list|,
operator|(
literal|""
operator|+
name|i
operator|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-325: test forceMergeDeletes, when 2 singular merges
comment|// are required
DECL|method|testForceMergeDeletes
specifier|public
name|void
name|testForceMergeDeletes
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
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
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|customType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FieldType
name|customType1
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType1
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|customType1
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType1
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType1
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|NO
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|Field
name|storedField
init|=
name|newField
argument_list|(
literal|"stored"
argument_list|,
literal|"stored"
argument_list|,
name|customType
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|storedField
argument_list|)
expr_stmt|;
name|Field
name|termVectorField
init|=
name|newField
argument_list|(
literal|"termVector"
argument_list|,
literal|"termVector"
argument_list|,
name|customType1
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|termVectorField
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|ir
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|dontMergeConfig
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|COMPOUND_FILES
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|dontMergeConfig
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
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
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMergeDeletes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|ir
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
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
comment|// LUCENE-325: test forceMergeDeletes, when many adjacent merges are required
DECL|method|testForceMergeDeletes2
specifier|public
name|void
name|testForceMergeDeletes2
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
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
argument_list|(
literal|50
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|customType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FieldType
name|customType1
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType1
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|customType1
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType1
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType1
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|storedField
init|=
name|newField
argument_list|(
literal|"stored"
argument_list|,
literal|"stored"
argument_list|,
name|customType
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|storedField
argument_list|)
expr_stmt|;
name|Field
name|termVectorField
init|=
name|newField
argument_list|(
literal|"termVector"
argument_list|,
literal|"termVector"
argument_list|,
name|customType1
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|termVectorField
argument_list|)
expr_stmt|;
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
name|NO
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|idField
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
literal|98
condition|;
name|i
operator|++
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|98
argument_list|,
name|ir
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|98
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|dontMergeConfig
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|COMPOUND_FILES
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|dontMergeConfig
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
literal|98
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|49
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
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
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|49
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMergeDeletes
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|49
argument_list|,
name|ir
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|49
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
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
comment|// LUCENE-325: test forceMergeDeletes without waiting, when
comment|// many adjacent merges are required
DECL|method|testForceMergeDeletes3
specifier|public
name|void
name|testForceMergeDeletes3
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
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
argument_list|(
literal|50
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|customType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FieldType
name|customType1
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType1
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|customType1
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType1
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType1
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|storedField
init|=
name|newField
argument_list|(
literal|"stored"
argument_list|,
literal|"stored"
argument_list|,
name|customType
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|storedField
argument_list|)
expr_stmt|;
name|Field
name|termVectorField
init|=
name|newField
argument_list|(
literal|"termVector"
argument_list|,
literal|"termVector"
argument_list|,
name|customType1
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|termVectorField
argument_list|)
expr_stmt|;
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
name|NO
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|idField
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
literal|98
condition|;
name|i
operator|++
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|98
argument_list|,
name|ir
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|98
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|dontMergeConfig
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|COMPOUND_FILES
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|dontMergeConfig
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
literal|98
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|49
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
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
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMergeDeletes
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|49
argument_list|,
name|ir
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|49
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
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
comment|// Just intercepts all merges& verifies that we are never
comment|// merging a segment with>= 20 (maxMergeDocs) docs
DECL|class|MyMergeScheduler
specifier|private
class|class
name|MyMergeScheduler
extends|extends
name|MergeScheduler
block|{
annotation|@
name|Override
DECL|method|merge
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
while|while
condition|(
literal|true
condition|)
block|{
name|MergePolicy
operator|.
name|OneMerge
name|merge
init|=
name|writer
operator|.
name|getNextMerge
argument_list|()
decl_stmt|;
if|if
condition|(
name|merge
operator|==
literal|null
condition|)
block|{
break|break;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|merge
operator|.
name|segments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
assert|assert
name|merge
operator|.
name|segments
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
operator|<
literal|20
assert|;
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
block|{}
block|}
comment|// LUCENE-1013
DECL|method|testSetMaxMergeDocs
specifier|public
name|void
name|testSetMaxMergeDocs
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
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
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
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|(
name|LogMergePolicy
operator|)
name|conf
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMaxMergeDocs
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setMergeFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"tvtest"
argument_list|,
literal|"a b c"
argument_list|,
name|customType
argument_list|)
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
literal|177
condition|;
name|i
operator|++
control|)
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|iw
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
DECL|method|testNoWaitClose
specifier|public
name|void
name|testNoWaitClose
parameter_list|()
throws|throws
name|Throwable
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Field
name|idField
init|=
name|newField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|customType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|pass
init|=
literal|0
init|;
name|pass
operator|<
literal|2
condition|;
name|pass
operator|++
control|)
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
literal|"TEST: pass="
operator|+
name|pass
argument_list|)
expr_stmt|;
block|}
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pass
operator|==
literal|2
condition|)
block|{
name|conf
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|conf
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
name|setMergeFactor
argument_list|(
literal|100
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|10
condition|;
name|iter
operator|++
control|)
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
literal|"TEST: iter="
operator|+
name|iter
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|199
condition|;
name|j
operator|++
control|)
block|{
name|idField
operator|.
name|setStringValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|iter
operator|*
literal|201
operator|+
name|j
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
block|}
name|int
name|delID
init|=
name|iter
operator|*
literal|199
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
literal|20
condition|;
name|j
operator|++
control|)
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|delID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|delID
operator|+=
literal|5
expr_stmt|;
block|}
comment|// Force a bunch of merge threads to kick off so we
comment|// stress out aborting them on close:
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
name|setMergeFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|IndexWriter
name|finalWriter
init|=
name|writer
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
name|failure
init|=
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|Thread
name|t1
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|boolean
name|done
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|done
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|finalWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|failure
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|done
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
if|if
condition|(
name|failure
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
name|failure
operator|.
name|get
argument_list|(
literal|0
argument_list|)
throw|;
block|}
name|t1
operator|.
name|start
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|t1
operator|.
name|join
argument_list|()
expr_stmt|;
comment|// Make sure reader can read
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Reopen
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
