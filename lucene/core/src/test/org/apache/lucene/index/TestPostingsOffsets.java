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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|Analyzer
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
name|CannedTokenStream
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
name|MockPayloadAnalyzer
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
name|Token
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
name|IntField
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
name|search
operator|.
name|FieldCache
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
name|English
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
name|_TestUtil
import|;
end_import
begin_class
DECL|class|TestPostingsOffsets
specifier|public
class|class
name|TestPostingsOffsets
extends|extends
name|LuceneTestCase
block|{
DECL|field|iwc
name|IndexWriterConfig
name|iwc
decl_stmt|;
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
comment|// Currently only SimpleText and Lucene40 can index offsets into postings:
name|assumeTrue
argument_list|(
literal|"codec does not support offsets"
argument_list|,
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"SimpleText"
argument_list|)
operator|||
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Lucene40"
argument_list|)
argument_list|)
expr_stmt|;
name|iwc
operator|=
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
expr_stmt|;
if|if
condition|(
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Lucene40"
argument_list|)
condition|)
block|{
comment|// pulsing etc are not implemented
name|iwc
operator|.
name|setCodec
argument_list|(
name|_TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Lucene40PostingsFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
expr_stmt|;
name|Token
index|[]
name|tokens
init|=
operator|new
name|Token
index|[]
block|{
name|makeToken
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|6
argument_list|)
block|,
name|makeToken
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|)
block|,
name|makeToken
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|9
argument_list|,
literal|17
argument_list|)
block|,
name|makeToken
argument_list|(
literal|"c"
argument_list|,
literal|1
argument_list|,
literal|19
argument_list|,
literal|50
argument_list|)
block|,     }
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
operator|new
name|CannedTokenStream
argument_list|(
name|tokens
argument_list|)
argument_list|,
name|ft
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
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|DocsAndPositionsEnum
name|dp
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
literal|"content"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|dp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dp
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dp
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dp
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dp
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|dp
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dp
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|dp
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|17
argument_list|,
name|dp
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocsEnum
operator|.
name|NO_MORE_DOCS
argument_list|,
name|dp
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|dp
operator|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
literal|"content"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|dp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dp
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dp
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dp
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|dp
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|dp
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocsEnum
operator|.
name|NO_MORE_DOCS
argument_list|,
name|dp
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|dp
operator|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|r
argument_list|,
literal|null
argument_list|,
literal|"content"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"c"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|dp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dp
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dp
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dp
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|19
argument_list|,
name|dp
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|dp
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocsEnum
operator|.
name|NO_MORE_DOCS
argument_list|,
name|dp
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|r
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
DECL|method|testSkipping
specifier|public
name|void
name|testSkipping
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestNumbers
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testPayloads
specifier|public
name|void
name|testPayloads
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestNumbers
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestNumbers
specifier|public
name|void
name|doTestNumbers
parameter_list|(
name|boolean
name|withPayloads
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|withPayloads
condition|?
operator|new
name|MockPayloadAnalyzer
argument_list|()
else|:
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|iwc
operator|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
if|if
condition|(
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Lucene40"
argument_list|)
condition|)
block|{
comment|// pulsing etc are not implemented
name|iwc
operator|.
name|setCodec
argument_list|(
name|_TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Lucene40PostingsFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
comment|// will rely on docids a bit for skipping
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"numbers"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|ft
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
literal|"oddeven"
argument_list|,
operator|(
name|i
operator|%
literal|2
operator|)
operator|==
literal|0
condition|?
literal|"even"
else|:
literal|"odd"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
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
name|IndexReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|terms
index|[]
init|=
block|{
literal|"one"
block|,
literal|"two"
block|,
literal|"three"
block|,
literal|"four"
block|,
literal|"five"
block|,
literal|"six"
block|,
literal|"seven"
block|,
literal|"eight"
block|,
literal|"nine"
block|,
literal|"ten"
block|,
literal|"hundred"
block|}
decl_stmt|;
for|for
control|(
name|String
name|term
range|:
name|terms
control|)
block|{
name|DocsAndPositionsEnum
name|dp
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
literal|null
argument_list|,
literal|"numbers"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|dp
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|String
name|storedNumbers
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"numbers"
argument_list|)
decl_stmt|;
name|int
name|freq
init|=
name|dp
operator|.
name|freq
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
name|freq
condition|;
name|i
operator|++
control|)
block|{
name|dp
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|int
name|start
init|=
name|dp
operator|.
name|startOffset
argument_list|()
decl_stmt|;
assert|assert
name|start
operator|>=
literal|0
assert|;
name|int
name|end
init|=
name|dp
operator|.
name|endOffset
argument_list|()
decl_stmt|;
assert|assert
name|end
operator|>=
literal|0
operator|&&
name|end
operator|>=
name|start
assert|;
comment|// check that the offsets correspond to the term in the src text
name|assertTrue
argument_list|(
name|storedNumbers
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
operator|.
name|equals
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|withPayloads
condition|)
block|{
comment|// check that we have a payload and it starts with "pos"
name|assertTrue
argument_list|(
name|dp
operator|.
name|hasPayload
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|payload
init|=
name|dp
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|payload
operator|.
name|utf8ToString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"pos:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// note: withPayloads=false doesnt necessarily mean we dont have them from MockAnalyzer!
block|}
block|}
block|}
comment|// check we can skip correctly
name|int
name|numSkippingTests
init|=
name|atLeast
argument_list|(
literal|50
argument_list|)
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
name|numSkippingTests
condition|;
name|j
operator|++
control|)
block|{
name|int
name|num
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|100
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|numDocs
operator|-
literal|1
argument_list|,
literal|999
argument_list|)
argument_list|)
decl_stmt|;
name|DocsAndPositionsEnum
name|dp
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|reader
argument_list|,
literal|null
argument_list|,
literal|"numbers"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"hundred"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|doc
init|=
name|dp
operator|.
name|advance
argument_list|(
name|num
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|num
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|int
name|freq
init|=
name|dp
operator|.
name|freq
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
name|freq
condition|;
name|i
operator|++
control|)
block|{
name|String
name|storedNumbers
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"numbers"
argument_list|)
decl_stmt|;
name|dp
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|int
name|start
init|=
name|dp
operator|.
name|startOffset
argument_list|()
decl_stmt|;
assert|assert
name|start
operator|>=
literal|0
assert|;
name|int
name|end
init|=
name|dp
operator|.
name|endOffset
argument_list|()
decl_stmt|;
assert|assert
name|end
operator|>=
literal|0
operator|&&
name|end
operator|>=
name|start
assert|;
comment|// check that the offsets correspond to the term in the src text
name|assertTrue
argument_list|(
name|storedNumbers
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
operator|.
name|equals
argument_list|(
literal|"hundred"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|withPayloads
condition|)
block|{
comment|// check that we have a payload and it starts with "pos"
name|assertTrue
argument_list|(
name|dp
operator|.
name|hasPayload
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|payload
init|=
name|dp
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|payload
operator|.
name|utf8ToString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"pos:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// note: withPayloads=false doesnt necessarily mean we dont have them from MockAnalyzer!
block|}
block|}
comment|// check that other fields (without offsets) work correctly
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
name|DocsEnum
name|dp
init|=
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|reader
argument_list|,
literal|null
argument_list|,
literal|"id"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|""
operator|+
name|i
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|dp
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|dp
operator|.
name|nextDoc
argument_list|()
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
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
comment|// token -> docID -> tokens
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Token
argument_list|>
argument_list|>
argument_list|>
name|actualTokens
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Token
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|20
argument_list|)
decl_stmt|;
comment|//final int numDocs = atLeast(5);
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
comment|// TODO: randomize what IndexOptions we use; also test
comment|// changing this up in one IW buffered segment...:
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|docCount
init|=
literal|0
init|;
name|docCount
operator|<
name|numDocs
condition|;
name|docCount
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
operator|new
name|IntField
argument_list|(
literal|"id"
argument_list|,
name|docCount
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numTokens
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|//final int numTokens = atLeast(20);
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
comment|//System.out.println("doc id=" + docCount);
for|for
control|(
name|int
name|tokenCount
init|=
literal|0
init|;
name|tokenCount
operator|<
name|numTokens
condition|;
name|tokenCount
operator|++
control|)
block|{
specifier|final
name|String
name|text
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|text
operator|=
literal|"a"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|text
operator|=
literal|"b"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|text
operator|=
literal|"c"
expr_stmt|;
block|}
else|else
block|{
name|text
operator|=
literal|"d"
expr_stmt|;
block|}
name|int
name|posIncr
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|1
else|:
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenCount
operator|==
literal|0
operator|&&
name|posIncr
operator|==
literal|0
condition|)
block|{
name|posIncr
operator|=
literal|1
expr_stmt|;
block|}
specifier|final
name|int
name|offIncr
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|0
else|:
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|int
name|tokenOffset
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|Token
name|token
init|=
name|makeToken
argument_list|(
name|text
argument_list|,
name|posIncr
argument_list|,
name|offset
operator|+
name|offIncr
argument_list|,
name|offset
operator|+
name|offIncr
operator|+
name|tokenOffset
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|actualTokens
operator|.
name|containsKey
argument_list|(
name|text
argument_list|)
condition|)
block|{
name|actualTokens
operator|.
name|put
argument_list|(
name|text
argument_list|,
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Token
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Token
argument_list|>
argument_list|>
name|postingsByDoc
init|=
name|actualTokens
operator|.
name|get
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|postingsByDoc
operator|.
name|containsKey
argument_list|(
name|docCount
argument_list|)
condition|)
block|{
name|postingsByDoc
operator|.
name|put
argument_list|(
name|docCount
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|postingsByDoc
operator|.
name|get
argument_list|(
name|docCount
argument_list|)
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|posIncr
expr_stmt|;
comment|// stuff abs position into type:
name|token
operator|.
name|setType
argument_list|(
literal|""
operator|+
name|pos
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|offIncr
operator|+
name|tokenOffset
expr_stmt|;
comment|//System.out.println("  " + token + " posIncr=" + token.getPositionIncrement() + " pos=" + pos + " off=" + token.startOffset() + "/" + token.endOffset() + " (freq=" + postingsByDoc.get(docCount).size() + ")");
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
operator|new
name|CannedTokenStream
argument_list|(
name|tokens
operator|.
name|toArray
argument_list|(
operator|new
name|Token
index|[
name|tokens
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|,
name|ft
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
specifier|final
name|DirectoryReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|String
index|[]
name|terms
init|=
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|}
decl_stmt|;
for|for
control|(
name|IndexReader
name|reader
range|:
name|r
operator|.
name|getSequentialSubReaders
argument_list|()
control|)
block|{
comment|// TODO: improve this
name|AtomicReader
name|sub
init|=
operator|(
name|AtomicReader
operator|)
name|reader
decl_stmt|;
comment|//System.out.println("\nsub=" + sub);
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|sub
operator|.
name|fields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"content"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|DocsEnum
name|docs
init|=
literal|null
decl_stmt|;
name|DocsAndPositionsEnum
name|docsAndPositions
init|=
literal|null
decl_stmt|;
name|DocsAndPositionsEnum
name|docsAndPositionsAndOffsets
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|docIDToID
index|[]
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getInts
argument_list|(
name|sub
argument_list|,
literal|"id"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|term
range|:
name|terms
control|)
block|{
comment|//System.out.println("  term=" + term);
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|term
argument_list|)
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
condition|)
block|{
name|docs
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|int
name|doc
decl_stmt|;
comment|//System.out.println("    doc/freq");
while|while
condition|(
operator|(
name|doc
operator|=
name|docs
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Token
argument_list|>
name|expected
init|=
name|actualTokens
operator|.
name|get
argument_list|(
name|term
argument_list|)
operator|.
name|get
argument_list|(
name|docIDToID
index|[
name|doc
index|]
argument_list|)
decl_stmt|;
comment|//System.out.println("      doc=" + docIDToID[doc] + " docID=" + doc + " " + expected.size() + " freq");
name|assertNotNull
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|,
name|docs
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|docsAndPositions
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|docsAndPositions
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|docsAndPositions
argument_list|)
expr_stmt|;
comment|//System.out.println("    doc/freq/pos");
while|while
condition|(
operator|(
name|doc
operator|=
name|docsAndPositions
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Token
argument_list|>
name|expected
init|=
name|actualTokens
operator|.
name|get
argument_list|(
name|term
argument_list|)
operator|.
name|get
argument_list|(
name|docIDToID
index|[
name|doc
index|]
argument_list|)
decl_stmt|;
comment|//System.out.println("      doc=" + docIDToID[doc] + " " + expected.size() + " freq");
name|assertNotNull
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|,
name|docsAndPositions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
name|token
range|:
name|expected
control|)
block|{
name|int
name|pos
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|token
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
comment|//System.out.println("        pos=" + pos);
name|assertEquals
argument_list|(
name|pos
argument_list|,
name|docsAndPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|docsAndPositionsAndOffsets
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|docsAndPositions
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|docsAndPositionsAndOffsets
argument_list|)
expr_stmt|;
comment|//System.out.println("    doc/freq/pos/offs");
while|while
condition|(
operator|(
name|doc
operator|=
name|docsAndPositions
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Token
argument_list|>
name|expected
init|=
name|actualTokens
operator|.
name|get
argument_list|(
name|term
argument_list|)
operator|.
name|get
argument_list|(
name|docIDToID
index|[
name|doc
index|]
argument_list|)
decl_stmt|;
comment|//System.out.println("      doc=" + docIDToID[doc] + " " + expected.size() + " freq");
name|assertNotNull
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|,
name|docsAndPositions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Token
name|token
range|:
name|expected
control|)
block|{
name|int
name|pos
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|token
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
comment|//System.out.println("        pos=" + pos);
name|assertEquals
argument_list|(
name|pos
argument_list|,
name|docsAndPositions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|token
operator|.
name|startOffset
argument_list|()
argument_list|,
name|docsAndPositions
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|token
operator|.
name|endOffset
argument_list|()
argument_list|,
name|docsAndPositions
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// TODO: test advance:
block|}
name|r
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
DECL|method|makeToken
specifier|private
name|Token
name|makeToken
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|posIncr
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
specifier|final
name|Token
name|t
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|t
operator|.
name|append
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|t
operator|.
name|setPositionIncrement
argument_list|(
name|posIncr
argument_list|)
expr_stmt|;
name|t
operator|.
name|setOffset
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
end_class
end_unit
