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
name|document
operator|.
name|BinaryField
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
name|SetBasedFieldSelector
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
name|StringField
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
name|BytesRef
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
DECL|class|TestContribIndexReader
specifier|public
class|class
name|TestContribIndexReader
extends|extends
name|LuceneTestCase
block|{
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
DECL|method|addDoc
specifier|static
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
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
name|newField
argument_list|(
literal|"content"
argument_list|,
name|value
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
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
DECL|method|addDocumentWithFields
specifier|static
name|void
name|addDocumentWithFields
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType3
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|customType3
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"keyword"
argument_list|,
literal|"test1"
argument_list|,
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"text"
argument_list|,
literal|"test1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"unindexed"
argument_list|,
literal|"test1"
argument_list|,
name|customType3
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"unstored"
argument_list|,
literal|"test1"
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
DECL|method|addDocumentWithDifferentFields
specifier|static
name|void
name|addDocumentWithDifferentFields
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType3
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|customType3
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"keyword2"
argument_list|,
literal|"test1"
argument_list|,
name|StringField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"text2"
argument_list|,
literal|"test1"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"unindexed2"
argument_list|,
literal|"test1"
argument_list|,
name|customType3
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"unstored2"
argument_list|,
literal|"test1"
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
DECL|method|addDocumentWithTermVectorFields
specifier|static
name|void
name|addDocumentWithTermVectorFields
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType5
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType5
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FieldType
name|customType6
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType6
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType6
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FieldType
name|customType7
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType7
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType7
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FieldType
name|customType8
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|customType8
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType8
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|customType8
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"tvnot"
argument_list|,
literal|"tvnot"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"termvector"
argument_list|,
literal|"termvector"
argument_list|,
name|customType5
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"tvoffset"
argument_list|,
literal|"tvoffset"
argument_list|,
name|customType6
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"tvposition"
argument_list|,
literal|"tvposition"
argument_list|,
name|customType7
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"tvpositionoffset"
argument_list|,
literal|"tvpositionoffset"
argument_list|,
name|customType8
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
DECL|method|testBinaryFields
specifier|public
name|void
name|testBinaryFields
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
name|byte
index|[]
name|bin
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"document number "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|addDocumentWithFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|addDocumentWithDifferentFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|addDocumentWithTermVectorFields
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|writer
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
name|BinaryField
argument_list|(
literal|"bin1"
argument_list|,
name|bin
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"junk"
argument_list|,
literal|"junk text"
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
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Document
name|doc2
init|=
name|reader
operator|.
name|document
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|fields
init|=
name|doc2
operator|.
name|getFields
argument_list|(
literal|"bin1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|Field
name|b1
init|=
operator|(
name|Field
operator|)
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|isBinary
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|bytesRef
init|=
name|b1
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|bin
operator|.
name|length
argument_list|,
name|bytesRef
operator|.
name|length
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
name|bin
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|bin
index|[
name|i
index|]
argument_list|,
name|bytesRef
operator|.
name|bytes
index|[
name|i
operator|+
name|bytesRef
operator|.
name|offset
index|]
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|lazyFields
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|lazyFields
operator|.
name|add
argument_list|(
literal|"bin1"
argument_list|)
expr_stmt|;
name|FieldSelector
name|sel
init|=
operator|new
name|SetBasedFieldSelector
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|,
name|lazyFields
argument_list|)
decl_stmt|;
name|doc2
operator|=
name|getDocument
argument_list|(
name|reader
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
operator|-
literal|1
argument_list|,
name|sel
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc2
operator|.
name|getFields
argument_list|(
literal|"bin1"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexableField
name|fb1
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|fb1
operator|.
name|binaryValue
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|bytesRef
operator|=
name|fb1
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|bin
operator|.
name|length
argument_list|,
name|bytesRef
operator|.
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bin
operator|.
name|length
argument_list|,
name|bytesRef
operator|.
name|length
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
name|bin
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|bin
index|[
name|i
index|]
argument_list|,
name|bytesRef
operator|.
name|bytes
index|[
name|i
operator|+
name|bytesRef
operator|.
name|offset
index|]
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// force optimize
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
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|doc2
operator|=
name|reader
operator|.
name|document
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc2
operator|.
name|getFields
argument_list|(
literal|"bin1"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|b1
operator|=
operator|(
name|Field
operator|)
name|fields
index|[
literal|0
index|]
expr_stmt|;
name|assertTrue
argument_list|(
name|b1
operator|.
name|isBinary
argument_list|()
argument_list|)
expr_stmt|;
name|bytesRef
operator|=
name|b1
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|bin
operator|.
name|length
argument_list|,
name|bytesRef
operator|.
name|length
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
name|bin
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|bin
index|[
name|i
index|]
argument_list|,
name|bytesRef
operator|.
name|bytes
index|[
name|i
operator|+
name|bytesRef
operator|.
name|offset
index|]
argument_list|)
expr_stmt|;
block|}
name|reader
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
