begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|IdentityHashMap
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|lucene40
operator|.
name|Lucene40PostingsFormat
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
name|AtomicIndexReader
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
name|DirectoryReader
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
name|DocsEnum
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
name|IndexReader
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
name|RandomIndexWriter
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
name|Terms
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
name|TermsEnum
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
name|Bits
operator|.
name|MatchNoBits
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
name|IOUtils
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
name|LineFileDocs
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
name|ReaderUtil
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
name|_TestUtil
import|;
end_import
begin_class
DECL|class|TestReuseDocsEnum
specifier|public
class|class
name|TestReuseDocsEnum
extends|extends
name|LuceneTestCase
block|{
DECL|method|testReuseDocsEnumNoReuse
specifier|public
name|void
name|testReuseDocsEnumNoReuse
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
name|Codec
name|cp
init|=
name|_TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Lucene40PostingsFormat
argument_list|()
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
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
name|setCodec
argument_list|(
name|cp
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numdocs
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|createRandomIndex
argument_list|(
name|numdocs
argument_list|,
name|writer
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|open
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
operator|new
name|ReaderUtil
operator|.
name|Gather
argument_list|(
name|open
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|add
parameter_list|(
name|int
name|base
parameter_list|,
name|AtomicIndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|Terms
name|terms
init|=
name|r
operator|.
name|terms
argument_list|(
literal|"body"
argument_list|)
decl_stmt|;
name|TermsEnum
name|iterator
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|IdentityHashMap
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
name|enums
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|MatchNoBits
name|bits
init|=
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|DocsEnum
name|docs
init|=
name|iterator
operator|.
name|docs
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|bits
else|:
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|enums
operator|.
name|put
argument_list|(
name|docs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|terms
operator|.
name|getUniqueTermCount
argument_list|()
argument_list|,
name|enums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|run
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|,
name|open
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
comment|// tests for reuse only if bits are the same either null or the same instance
DECL|method|testReuseDocsEnumSameBitsOrNull
specifier|public
name|void
name|testReuseDocsEnumSameBitsOrNull
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
name|Codec
name|cp
init|=
name|_TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Lucene40PostingsFormat
argument_list|()
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
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
name|setCodec
argument_list|(
name|cp
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numdocs
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|createRandomIndex
argument_list|(
name|numdocs
argument_list|,
name|writer
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|open
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexReader
index|[]
name|sequentialSubReaders
init|=
name|open
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexReader
name|indexReader
range|:
name|sequentialSubReaders
control|)
block|{
name|Terms
name|terms
init|=
operator|(
operator|(
name|AtomicIndexReader
operator|)
name|indexReader
operator|)
operator|.
name|terms
argument_list|(
literal|"body"
argument_list|)
decl_stmt|;
name|TermsEnum
name|iterator
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|IdentityHashMap
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
name|enums
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|MatchNoBits
name|bits
init|=
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|open
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|docs
operator|=
name|iterator
operator|.
name|docs
argument_list|(
name|bits
argument_list|,
name|docs
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|enums
operator|.
name|put
argument_list|(
name|docs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|enums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|enums
operator|.
name|clear
argument_list|()
expr_stmt|;
name|iterator
operator|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|docs
operator|=
literal|null
expr_stmt|;
while|while
condition|(
operator|(
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|docs
operator|=
name|iterator
operator|.
name|docs
argument_list|(
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|open
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|,
name|docs
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|enums
operator|.
name|put
argument_list|(
name|docs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|terms
operator|.
name|getUniqueTermCount
argument_list|()
argument_list|,
name|enums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|enums
operator|.
name|clear
argument_list|()
expr_stmt|;
name|iterator
operator|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|docs
operator|=
literal|null
expr_stmt|;
while|while
condition|(
operator|(
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|docs
operator|=
name|iterator
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docs
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|enums
operator|.
name|put
argument_list|(
name|docs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|enums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|,
name|open
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
comment|// make sure we never reuse from another reader even if it is the same field& codec etc
DECL|method|testReuseDocsEnumDifferentReader
specifier|public
name|void
name|testReuseDocsEnumDifferentReader
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
name|Codec
name|cp
init|=
name|_TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Lucene40PostingsFormat
argument_list|()
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
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
name|setCodec
argument_list|(
name|cp
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numdocs
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
decl_stmt|;
name|createRandomIndex
argument_list|(
name|numdocs
argument_list|,
name|writer
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|firstReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|DirectoryReader
name|secondReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexReader
index|[]
name|sequentialSubReaders
init|=
name|firstReader
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
name|IndexReader
index|[]
name|sequentialSubReaders2
init|=
name|secondReader
operator|.
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexReader
name|indexReader
range|:
name|sequentialSubReaders
control|)
block|{
name|Terms
name|terms
init|=
operator|(
operator|(
name|AtomicIndexReader
operator|)
name|indexReader
operator|)
operator|.
name|terms
argument_list|(
literal|"body"
argument_list|)
decl_stmt|;
name|TermsEnum
name|iterator
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|IdentityHashMap
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
name|enums
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|DocsEnum
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
name|MatchNoBits
name|bits
init|=
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|firstReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|iterator
operator|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
name|BytesRef
name|term
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|docs
operator|=
name|iterator
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|randomDocsEnum
argument_list|(
literal|"body"
argument_list|,
name|term
argument_list|,
name|sequentialSubReaders2
argument_list|,
name|bits
argument_list|)
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|enums
operator|.
name|put
argument_list|(
name|docs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|terms
operator|.
name|getUniqueTermCount
argument_list|()
argument_list|,
name|enums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|enums
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docs
operator|=
literal|null
expr_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|docs
operator|=
name|iterator
operator|.
name|docs
argument_list|(
name|bits
argument_list|,
name|randomDocsEnum
argument_list|(
literal|"body"
argument_list|,
name|term
argument_list|,
name|sequentialSubReaders2
argument_list|,
name|bits
argument_list|)
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|enums
operator|.
name|put
argument_list|(
name|docs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|terms
operator|.
name|getUniqueTermCount
argument_list|()
argument_list|,
name|enums
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|,
name|firstReader
argument_list|,
name|secondReader
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|randomDocsEnum
specifier|public
name|DocsEnum
name|randomDocsEnum
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|,
name|IndexReader
index|[]
name|readers
parameter_list|,
name|Bits
name|bits
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|AtomicIndexReader
name|indexReader
init|=
operator|(
name|AtomicIndexReader
operator|)
name|readers
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|readers
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
return|return
name|indexReader
operator|.
name|termDocsEnum
argument_list|(
name|bits
argument_list|,
name|field
argument_list|,
name|term
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * populates a writer with random stuff. this must be fully reproducable with    * the seed!    */
DECL|method|createRandomIndex
specifier|public
specifier|static
name|void
name|createRandomIndex
parameter_list|(
name|int
name|numdocs
parameter_list|,
name|RandomIndexWriter
name|writer
parameter_list|,
name|Random
name|random
parameter_list|)
throws|throws
name|IOException
block|{
name|LineFileDocs
name|lineFileDocs
init|=
operator|new
name|LineFileDocs
argument_list|(
name|random
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
name|numdocs
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|lineFileDocs
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
