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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexSearcher
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
name|PhraseQuery
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
name|ScoreDoc
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
name|IOContext
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
name|IndexInput
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
name|MockDirectoryWrapper
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
begin_comment
comment|/**  * Tests lazy skipping on the proximity file.  *  */
end_comment
begin_class
DECL|class|TestLazyProxSkipping
specifier|public
class|class
name|TestLazyProxSkipping
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|seeksCounter
specifier|private
name|int
name|seeksCounter
init|=
literal|0
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
init|=
literal|"tokens"
decl_stmt|;
DECL|field|term1
specifier|private
name|String
name|term1
init|=
literal|"xx"
decl_stmt|;
DECL|field|term2
specifier|private
name|String
name|term2
init|=
literal|"yy"
decl_stmt|;
DECL|field|term3
specifier|private
name|String
name|term3
init|=
literal|"zz"
decl_stmt|;
DECL|class|SeekCountingDirectory
specifier|private
class|class
name|SeekCountingDirectory
extends|extends
name|MockDirectoryWrapper
block|{
DECL|method|SeekCountingDirectory
specifier|public
name|SeekCountingDirectory
parameter_list|(
name|Directory
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|random
argument_list|()
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|ii
init|=
name|super
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".prx"
argument_list|)
operator|||
name|name
operator|.
name|endsWith
argument_list|(
literal|".pos"
argument_list|)
condition|)
block|{
comment|// we decorate the proxStream with a wrapper class that allows to count the number of calls of seek()
name|ii
operator|=
operator|new
name|SeeksCountingStream
argument_list|(
name|ii
argument_list|)
expr_stmt|;
block|}
return|return
name|ii
return|;
block|}
block|}
DECL|method|createIndex
specifier|private
name|void
name|createIndex
parameter_list|(
name|int
name|numHits
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numDocs
init|=
literal|500
decl_stmt|;
specifier|final
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Directory
name|directory
init|=
operator|new
name|SeekCountingDirectory
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|)
decl_stmt|;
comment|// note: test explicitly disables payloads
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|10
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|false
argument_list|)
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|content
decl_stmt|;
if|if
condition|(
name|i
operator|%
operator|(
name|numDocs
operator|/
name|numHits
operator|)
operator|==
literal|0
condition|)
block|{
comment|// add a document that matches the query "term1 term2"
name|content
operator|=
name|this
operator|.
name|term1
operator|+
literal|" "
operator|+
name|this
operator|.
name|term2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|%
literal|15
operator|==
literal|0
condition|)
block|{
comment|// add a document that only contains term1
name|content
operator|=
name|this
operator|.
name|term1
operator|+
literal|" "
operator|+
name|this
operator|.
name|term1
expr_stmt|;
block|}
else|else
block|{
comment|// add a document that contains term2 but not term 1
name|content
operator|=
name|this
operator|.
name|term3
operator|+
literal|" "
operator|+
name|this
operator|.
name|term2
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|this
operator|.
name|field
argument_list|,
name|content
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
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// make sure the index has only a single segment
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|SegmentReader
name|reader
init|=
name|getOnlySegmentReader
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|search
specifier|private
name|ScoreDoc
index|[]
name|search
parameter_list|()
throws|throws
name|IOException
block|{
comment|// create PhraseQuery "term1 term2" and search
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|this
operator|.
name|field
argument_list|,
name|this
operator|.
name|term1
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|this
operator|.
name|field
argument_list|,
name|this
operator|.
name|term2
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|searcher
operator|.
name|search
argument_list|(
name|pq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
return|;
block|}
DECL|method|performTest
specifier|private
name|void
name|performTest
parameter_list|(
name|int
name|numHits
parameter_list|)
throws|throws
name|IOException
block|{
name|createIndex
argument_list|(
name|numHits
argument_list|)
expr_stmt|;
name|this
operator|.
name|seeksCounter
operator|=
literal|0
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|search
argument_list|()
decl_stmt|;
comment|// verify that the right number of docs was found
name|assertEquals
argument_list|(
name|numHits
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// check if the number of calls of seek() does not exceed the number of hits
name|assertTrue
argument_list|(
name|this
operator|.
name|seeksCounter
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"seeksCounter="
operator|+
name|this
operator|.
name|seeksCounter
operator|+
literal|" numHits="
operator|+
name|numHits
argument_list|,
name|this
operator|.
name|seeksCounter
operator|<=
name|numHits
operator|+
literal|1
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testLazySkipping
specifier|public
name|void
name|testLazySkipping
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fieldFormat
init|=
name|TestUtil
operator|.
name|getPostingsFormat
argument_list|(
name|this
operator|.
name|field
argument_list|)
decl_stmt|;
name|assumeFalse
argument_list|(
literal|"This test cannot run with Memory postings format"
argument_list|,
name|fieldFormat
operator|.
name|equals
argument_list|(
literal|"Memory"
argument_list|)
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
literal|"This test cannot run with Direct postings format"
argument_list|,
name|fieldFormat
operator|.
name|equals
argument_list|(
literal|"Direct"
argument_list|)
argument_list|)
expr_stmt|;
name|assumeFalse
argument_list|(
literal|"This test cannot run with SimpleText postings format"
argument_list|,
name|fieldFormat
operator|.
name|equals
argument_list|(
literal|"SimpleText"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test whether only the minimum amount of seeks()
comment|// are performed
name|performTest
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|performTest
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|testSeek
specifier|public
name|void
name|testSeek
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
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
name|newTextField
argument_list|(
name|this
operator|.
name|field
argument_list|,
literal|"a b"
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
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
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
name|DocsAndPositionsEnum
name|tp
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
name|this
operator|.
name|field
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"b"
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
name|tp
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|tp
operator|.
name|docID
argument_list|()
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tp
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|tp
operator|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
name|this
operator|.
name|field
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"a"
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|tp
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|tp
operator|.
name|docID
argument_list|()
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tp
operator|.
name|nextPosition
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Simply extends IndexInput in a way that we are able to count the number
comment|// of invocations of seek()
DECL|class|SeeksCountingStream
class|class
name|SeeksCountingStream
extends|extends
name|IndexInput
block|{
DECL|field|input
specifier|private
name|IndexInput
name|input
decl_stmt|;
DECL|method|SeeksCountingStream
name|SeeksCountingStream
parameter_list|(
name|IndexInput
name|input
parameter_list|)
block|{
name|super
argument_list|(
literal|"SeekCountingStream("
operator|+
name|input
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|input
operator|.
name|readByte
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|input
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|this
operator|.
name|input
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|TestLazyProxSkipping
operator|.
name|this
operator|.
name|seeksCounter
operator|++
expr_stmt|;
name|this
operator|.
name|input
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|this
operator|.
name|input
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SeeksCountingStream
name|clone
parameter_list|()
block|{
return|return
operator|new
name|SeeksCountingStream
argument_list|(
name|this
operator|.
name|input
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|slice
specifier|public
name|IndexInput
name|slice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SeeksCountingStream
argument_list|(
name|this
operator|.
name|input
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
