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
name|BytesRef
import|;
end_import
begin_class
DECL|class|TestMultiPassIndexSplitter
specifier|public
class|class
name|TestMultiPassIndexSplitter
extends|extends
name|LuceneTestCase
block|{
DECL|field|input
name|IndexReader
name|input
decl_stmt|;
DECL|field|NUM_DOCS
name|int
name|NUM_DOCS
init|=
literal|11
decl_stmt|;
DECL|field|dir
name|Directory
name|dir
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
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
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
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
name|NUM_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"id"
argument_list|,
name|i
operator|+
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
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f"
argument_list|,
name|i
operator|+
literal|" "
operator|+
name|i
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
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
name|input
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
comment|// delete the last doc
name|input
operator|.
name|deleteDocument
argument_list|(
name|input
operator|.
name|maxDoc
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|IndexReader
name|inputOld
init|=
name|input
decl_stmt|;
name|input
operator|=
name|input
operator|.
name|reopen
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|inputOld
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
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
comment|/**    * Test round-robin splitting.    */
DECL|method|testSplitRR
specifier|public
name|void
name|testSplitRR
parameter_list|()
throws|throws
name|Exception
block|{
name|MultiPassIndexSplitter
name|splitter
init|=
operator|new
name|MultiPassIndexSplitter
argument_list|()
decl_stmt|;
name|Directory
index|[]
name|dirs
init|=
operator|new
name|Directory
index|[]
block|{
name|newDirectory
argument_list|()
block|,
name|newDirectory
argument_list|()
block|,
name|newDirectory
argument_list|()
block|}
decl_stmt|;
name|splitter
operator|.
name|split
argument_list|(
name|input
argument_list|,
name|dirs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
decl_stmt|;
name|ir
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ir
operator|.
name|numDocs
argument_list|()
operator|-
name|NUM_DOCS
operator|/
literal|3
operator|<=
literal|1
argument_list|)
expr_stmt|;
comment|// rounding error
name|Document
name|doc
init|=
name|ir
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|ir
argument_list|,
literal|"id"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|NOT_FOUND
argument_list|,
name|te
operator|.
name|seek
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"1"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dirs
index|[
literal|1
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ir
operator|.
name|numDocs
argument_list|()
operator|-
name|NUM_DOCS
operator|/
literal|3
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|=
name|ir
operator|.
name|document
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|te
operator|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|ir
argument_list|,
literal|"id"
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|NOT_FOUND
argument_list|,
name|te
operator|.
name|seek
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"0"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dirs
index|[
literal|2
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ir
operator|.
name|numDocs
argument_list|()
operator|-
name|NUM_DOCS
operator|/
literal|3
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|=
name|ir
operator|.
name|document
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|te
operator|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|ir
argument_list|,
literal|"id"
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|NOT_FOUND
argument_list|,
name|te
operator|.
name|seek
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"1"
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|NOT_FOUND
argument_list|,
name|te
operator|.
name|seek
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"0"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|Directory
name|d
range|:
name|dirs
control|)
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test sequential splitting.    */
DECL|method|testSplitSeq
specifier|public
name|void
name|testSplitSeq
parameter_list|()
throws|throws
name|Exception
block|{
name|MultiPassIndexSplitter
name|splitter
init|=
operator|new
name|MultiPassIndexSplitter
argument_list|()
decl_stmt|;
name|Directory
index|[]
name|dirs
init|=
operator|new
name|Directory
index|[]
block|{
name|newDirectory
argument_list|()
block|,
name|newDirectory
argument_list|()
block|,
name|newDirectory
argument_list|()
block|}
decl_stmt|;
name|splitter
operator|.
name|split
argument_list|(
name|input
argument_list|,
name|dirs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
decl_stmt|;
name|ir
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ir
operator|.
name|numDocs
argument_list|()
operator|-
name|NUM_DOCS
operator|/
literal|3
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|ir
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|start
init|=
name|ir
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dirs
index|[
literal|1
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ir
operator|.
name|numDocs
argument_list|()
operator|-
name|NUM_DOCS
operator|/
literal|3
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|=
name|ir
operator|.
name|document
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|start
operator|+
literal|""
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|start
operator|+=
name|ir
operator|.
name|numDocs
argument_list|()
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dirs
index|[
literal|2
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ir
operator|.
name|numDocs
argument_list|()
operator|-
name|NUM_DOCS
operator|/
literal|3
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|=
name|ir
operator|.
name|document
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|start
operator|+
literal|""
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
comment|// make sure the deleted doc is not here
name|TermsEnum
name|te
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|ir
argument_list|,
literal|"id"
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Term
name|t
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
operator|(
name|NUM_DOCS
operator|-
literal|1
operator|)
operator|+
literal|""
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|NOT_FOUND
argument_list|,
name|te
operator|.
name|seek
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|t
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|t
operator|.
name|text
argument_list|()
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|Directory
name|d
range|:
name|dirs
control|)
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
