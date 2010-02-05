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
name|IndexWriter
operator|.
name|MaxFieldLength
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
name|Version
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_class
DECL|class|TestMultiPassIndexSplitter
specifier|public
class|class
name|TestMultiPassIndexSplitter
extends|extends
name|TestCase
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
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
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
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|MaxFieldLength
operator|.
name|LIMITED
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
operator|new
name|Field
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
operator|new
name|Field
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
name|input
operator|=
name|input
operator|.
name|reopen
argument_list|(
literal|true
argument_list|)
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
operator|new
name|RAMDirectory
argument_list|()
block|,
operator|new
name|RAMDirectory
argument_list|()
block|,
operator|new
name|RAMDirectory
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
name|Term
name|t
decl_stmt|;
name|TermEnum
name|te
decl_stmt|;
name|t
operator|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|te
operator|=
name|ir
operator|.
name|terms
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|t
argument_list|,
name|te
operator|.
name|term
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
name|t
operator|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|te
operator|=
name|ir
operator|.
name|terms
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|t
argument_list|,
name|te
operator|.
name|term
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
name|t
operator|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|te
operator|=
name|ir
operator|.
name|terms
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|t
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|te
operator|=
name|ir
operator|.
name|terms
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|t
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
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
operator|new
name|RAMDirectory
argument_list|()
block|,
operator|new
name|RAMDirectory
argument_list|()
block|,
operator|new
name|RAMDirectory
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
name|Term
name|t
decl_stmt|;
name|TermEnum
name|te
decl_stmt|;
name|t
operator|=
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
expr_stmt|;
name|te
operator|=
name|ir
operator|.
name|terms
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|t
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
