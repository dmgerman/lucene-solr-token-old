begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Iterator
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
name|search
operator|.
name|DocIdSetIterator
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
name|TestUtil
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
name|LeafReader
name|ir1
decl_stmt|;
DECL|field|ir2
specifier|private
name|LeafReader
name|ir2
decl_stmt|;
DECL|field|rd1
specifier|private
name|Directory
name|rd1
decl_stmt|;
DECL|field|rd2
specifier|private
name|Directory
name|rd2
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
name|Document
name|doc
decl_stmt|;
name|rd1
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|iw1
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd1
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
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
name|newTextField
argument_list|(
literal|"field1"
argument_list|,
literal|"the quick brown fox jumps"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field2"
argument_list|,
literal|"the quick brown fox jumps"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
name|rd2
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|iw2
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd2
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
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
name|newTextField
argument_list|(
literal|"field1"
argument_list|,
literal|"the fox jumps over the lazy dog"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"field3"
argument_list|,
literal|"the fox jumps over the lazy dog"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|rd1
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|ir2
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|rd2
argument_list|)
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
name|rd1
operator|.
name|close
argument_list|()
expr_stmt|;
name|rd2
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
DECL|method|checkTerms
specifier|private
name|void
name|checkTerms
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|String
modifier|...
name|termsList
parameter_list|)
throws|throws
name|IOException
block|{
name|assertNotNull
argument_list|(
name|terms
argument_list|)
expr_stmt|;
specifier|final
name|TermsEnum
name|te
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|t
range|:
name|termsList
control|)
block|{
name|BytesRef
name|b
init|=
name|te
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|t
argument_list|,
name|b
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|PostingsEnum
name|td
init|=
name|TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|te
argument_list|,
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|NONE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|td
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|td
operator|.
name|nextDoc
argument_list|()
argument_list|,
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
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
name|ParallelLeafReader
name|pr
init|=
operator|new
name|ParallelLeafReader
argument_list|(
name|ir1
argument_list|,
name|ir2
argument_list|)
decl_stmt|;
name|Fields
name|fields
init|=
name|pr
operator|.
name|fields
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|fe
init|=
name|fields
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|f
init|=
name|fe
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"field1"
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|checkTerms
argument_list|(
name|fields
operator|.
name|terms
argument_list|(
name|f
argument_list|)
argument_list|,
literal|"brown"
argument_list|,
literal|"fox"
argument_list|,
literal|"jumps"
argument_list|,
literal|"quick"
argument_list|,
literal|"the"
argument_list|)
expr_stmt|;
name|f
operator|=
name|fe
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field2"
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|checkTerms
argument_list|(
name|fields
operator|.
name|terms
argument_list|(
name|f
argument_list|)
argument_list|,
literal|"brown"
argument_list|,
literal|"fox"
argument_list|,
literal|"jumps"
argument_list|,
literal|"quick"
argument_list|,
literal|"the"
argument_list|)
expr_stmt|;
name|f
operator|=
name|fe
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"field3"
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|checkTerms
argument_list|(
name|fields
operator|.
name|terms
argument_list|(
name|f
argument_list|)
argument_list|,
literal|"dog"
argument_list|,
literal|"fox"
argument_list|,
literal|"jumps"
argument_list|,
literal|"lazy"
argument_list|,
literal|"over"
argument_list|,
literal|"the"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fe
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
