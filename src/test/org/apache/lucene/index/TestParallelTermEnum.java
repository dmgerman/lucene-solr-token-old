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
name|analysis
operator|.
name|SimpleAnalyzer
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
name|RAMDirectory
import|;
end_import
begin_class
DECL|class|TestParallelTermEnum
specifier|public
class|class
name|TestParallelTermEnum
extends|extends
name|LuceneTestCase
block|{
DECL|field|ir1
specifier|private
name|IndexReader
name|ir1
decl_stmt|;
DECL|field|ir2
specifier|private
name|IndexReader
name|ir2
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
name|Document
name|doc
decl_stmt|;
name|RAMDirectory
name|rd1
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw1
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd1
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
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
literal|"field1"
argument_list|,
literal|"the quick brown fox jumps"
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field2"
argument_list|,
literal|"the quick brown fox jumps"
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field4"
argument_list|,
literal|""
argument_list|,
name|Store
operator|.
name|NO
argument_list|,
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|iw1
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw1
operator|.
name|close
argument_list|()
expr_stmt|;
name|RAMDirectory
name|rd2
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw2
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd2
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
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
literal|"field0"
argument_list|,
literal|""
argument_list|,
name|Store
operator|.
name|NO
argument_list|,
name|Index
operator|.
name|ANALYZED
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
literal|"field1"
argument_list|,
literal|"the fox jumps over the lazy dog"
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"field3"
argument_list|,
literal|"the fox jumps over the lazy dog"
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
name|iw2
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw2
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|ir1
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|rd1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|ir2
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|rd2
argument_list|,
literal|true
argument_list|)
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
name|super
operator|.
name|tearDown
argument_list|()
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
block|}
DECL|method|test1
specifier|public
name|void
name|test1
parameter_list|()
throws|throws
name|IOException
block|{
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
name|ir1
argument_list|)
expr_stmt|;
name|pr
operator|.
name|add
argument_list|(
name|ir2
argument_list|)
expr_stmt|;
name|TermDocs
name|td
init|=
name|pr
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|TermEnum
name|te
init|=
name|pr
operator|.
name|terms
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field1:brown"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field1:fox"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field1:jumps"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field1:quick"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field1:the"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field2:brown"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field2:fox"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field2:jumps"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field2:quick"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field2:the"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field3:dog"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field3:fox"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field3:jumps"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field3:lazy"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field3:over"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field3:the"
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|td
operator|.
name|seek
argument_list|(
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|td
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
