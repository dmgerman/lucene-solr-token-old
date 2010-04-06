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
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
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
name|analysis
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
name|document
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
name|util
operator|.
name|*
import|;
end_import
begin_class
DECL|class|TestFlexExternalReader
specifier|public
class|class
name|TestFlexExternalReader
extends|extends
name|LuceneTestCase
block|{
DECL|method|testExternalReader
specifier|public
name|void
name|testExternalReader
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|d
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
specifier|final
name|int
name|DOC_COUNT
init|=
literal|177
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
name|w
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|7
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
literal|"field1"
argument_list|,
literal|"this is field1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
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
literal|"this is field2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
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
literal|"aaa"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
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
literal|"bbb"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
name|DOC_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|r
init|=
operator|new
name|FlexTestUtil
operator|.
name|ForcedExternalReader
argument_list|(
name|w
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRef
name|field1Term
init|=
operator|new
name|BytesRef
argument_list|(
literal|"field1"
argument_list|)
decl_stmt|;
name|BytesRef
name|field2Term
init|=
operator|new
name|BytesRef
argument_list|(
literal|"field2"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|DOC_COUNT
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DOC_COUNT
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DOC_COUNT
argument_list|,
name|r
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"field1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DOC_COUNT
argument_list|,
name|r
operator|.
name|docFreq
argument_list|(
literal|"field1"
argument_list|,
name|field1Term
argument_list|)
argument_list|)
expr_stmt|;
name|Fields
name|fields
init|=
name|r
operator|.
name|fields
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
literal|"field1"
argument_list|)
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
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
name|FOUND
argument_list|,
name|termsEnum
operator|.
name|seek
argument_list|(
name|field1Term
argument_list|)
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
name|termsEnum
operator|.
name|seek
argument_list|(
name|field2Term
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"is"
argument_list|)
operator|.
name|bytesEquals
argument_list|(
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|terms
operator|=
name|fields
operator|.
name|terms
argument_list|(
literal|"field2"
argument_list|)
expr_stmt|;
name|termsEnum
operator|=
name|terms
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
name|termsEnum
operator|.
name|seek
argument_list|(
name|field1Term
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termsEnum
operator|.
name|term
argument_list|()
operator|.
name|bytesEquals
argument_list|(
name|field2Term
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|termsEnum
operator|.
name|seek
argument_list|(
name|field2Term
argument_list|)
argument_list|)
expr_stmt|;
name|termsEnum
operator|=
name|fields
operator|.
name|terms
argument_list|(
literal|"field3"
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
name|END
argument_list|,
name|termsEnum
operator|.
name|seek
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"bbb"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|termsEnum
operator|.
name|seek
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|termsEnum
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
