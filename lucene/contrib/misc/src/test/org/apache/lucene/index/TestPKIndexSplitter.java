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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
name|analysis
operator|.
name|MockTokenizer
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
name|Bits
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
DECL|class|TestPKIndexSplitter
specifier|public
class|class
name|TestPKIndexSplitter
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSplit
specifier|public
name|void
name|testSplit
parameter_list|()
throws|throws
name|Exception
block|{
name|NumberFormat
name|format
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"000000000"
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
literal|11
condition|;
name|x
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|createDocument
argument_list|(
name|x
argument_list|,
literal|"1"
argument_list|,
literal|3
argument_list|,
name|format
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|11
init|;
name|x
operator|<
literal|20
condition|;
name|x
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|createDocument
argument_list|(
name|x
argument_list|,
literal|"2"
argument_list|,
literal|3
argument_list|,
name|format
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|Term
name|midTerm
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|format
operator|.
name|format
argument_list|(
literal|11
argument_list|)
argument_list|)
decl_stmt|;
name|checkSplitting
argument_list|(
name|dir
argument_list|,
name|midTerm
argument_list|,
literal|11
argument_list|,
literal|9
argument_list|)
expr_stmt|;
comment|// delete some documents
name|w
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
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
name|midTerm
argument_list|)
expr_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|format
operator|.
name|format
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkSplitting
argument_list|(
name|dir
argument_list|,
name|midTerm
argument_list|,
literal|10
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|checkSplitting
specifier|private
name|void
name|checkSplitting
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Term
name|splitTerm
parameter_list|,
name|int
name|leftCount
parameter_list|,
name|int
name|rightCount
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|dir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|PKIndexSplitter
name|splitter
init|=
operator|new
name|PKIndexSplitter
argument_list|(
name|dir
argument_list|,
name|dir1
argument_list|,
name|dir2
argument_list|,
name|splitTerm
argument_list|)
decl_stmt|;
name|splitter
operator|.
name|split
argument_list|()
expr_stmt|;
name|IndexReader
name|ir1
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
decl_stmt|;
name|IndexReader
name|ir2
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|leftCount
argument_list|,
name|ir1
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rightCount
argument_list|,
name|ir2
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|checkContents
argument_list|(
name|ir1
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|checkContents
argument_list|(
name|ir2
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|ir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
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
DECL|method|checkContents
specifier|private
name|void
name|checkContents
parameter_list|(
name|IndexReader
name|ir
parameter_list|,
name|String
name|indexname
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Bits
name|liveDocs
init|=
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|ir
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
name|ir
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|indexname
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|(
literal|"indexname"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createDocument
specifier|private
name|Document
name|createDocument
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|indexName
parameter_list|,
name|int
name|numFields
parameter_list|,
name|NumberFormat
name|format
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|format
operator|.
name|format
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"indexname"
argument_list|,
name|indexName
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field1"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
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
name|sb
operator|.
name|append
argument_list|(
literal|" b"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|n
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numFields
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"field"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
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
block|}
return|return
name|doc
return|;
block|}
block|}
end_class
end_unit
