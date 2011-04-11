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
name|store
operator|.
name|BufferedIndexInput
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
name|index
operator|.
name|codecs
operator|.
name|CodecProvider
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
name|Collection
import|;
end_import
begin_class
DECL|class|TestSegmentMerger
specifier|public
class|class
name|TestSegmentMerger
extends|extends
name|LuceneTestCase
block|{
comment|//The variables for the new merged segment
DECL|field|mergedDir
specifier|private
name|Directory
name|mergedDir
decl_stmt|;
DECL|field|mergedSegment
specifier|private
name|String
name|mergedSegment
init|=
literal|"test"
decl_stmt|;
comment|//First segment to be merged
DECL|field|merge1Dir
specifier|private
name|Directory
name|merge1Dir
decl_stmt|;
DECL|field|doc1
specifier|private
name|Document
name|doc1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
DECL|field|reader1
specifier|private
name|SegmentReader
name|reader1
init|=
literal|null
decl_stmt|;
comment|//Second Segment to be merged
DECL|field|merge2Dir
specifier|private
name|Directory
name|merge2Dir
decl_stmt|;
DECL|field|doc2
specifier|private
name|Document
name|doc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
DECL|field|reader2
specifier|private
name|SegmentReader
name|reader2
init|=
literal|null
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
name|mergedDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|merge1Dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|merge2Dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|SegmentInfo
name|info1
init|=
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|random
argument_list|,
name|merge1Dir
argument_list|,
name|doc1
argument_list|)
decl_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|SegmentInfo
name|info2
init|=
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|random
argument_list|,
name|merge2Dir
argument_list|,
name|doc2
argument_list|)
decl_stmt|;
name|reader1
operator|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|true
argument_list|,
name|info1
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
argument_list|)
expr_stmt|;
name|reader2
operator|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|true
argument_list|,
name|info2
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
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
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader2
operator|.
name|close
argument_list|()
expr_stmt|;
name|mergedDir
operator|.
name|close
argument_list|()
expr_stmt|;
name|merge1Dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|merge2Dir
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
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|mergedDir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|merge1Dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|merge2Dir
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader1
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reader2
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testMerge
specifier|public
name|void
name|testMerge
parameter_list|()
throws|throws
name|IOException
block|{
name|SegmentMerger
name|merger
init|=
operator|new
name|SegmentMerger
argument_list|(
name|mergedDir
argument_list|,
name|IndexWriterConfig
operator|.
name|DEFAULT_TERM_INDEX_INTERVAL
argument_list|,
name|mergedSegment
argument_list|,
literal|null
argument_list|,
name|CodecProvider
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|new
name|FieldInfos
argument_list|()
argument_list|)
decl_stmt|;
name|merger
operator|.
name|add
argument_list|(
name|reader1
argument_list|)
expr_stmt|;
name|merger
operator|.
name|add
argument_list|(
name|reader2
argument_list|)
expr_stmt|;
name|int
name|docsMerged
init|=
name|merger
operator|.
name|merge
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docsMerged
operator|==
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|FieldInfos
name|fieldInfos
init|=
name|merger
operator|.
name|fieldInfos
argument_list|()
decl_stmt|;
comment|//Should be able to open a new SegmentReader against the new directory
name|SegmentReader
name|mergedReader
init|=
name|SegmentReader
operator|.
name|get
argument_list|(
literal|false
argument_list|,
name|mergedDir
argument_list|,
operator|new
name|SegmentInfo
argument_list|(
name|mergedSegment
argument_list|,
name|docsMerged
argument_list|,
name|mergedDir
argument_list|,
literal|false
argument_list|,
name|fieldInfos
operator|.
name|hasProx
argument_list|()
argument_list|,
name|merger
operator|.
name|getSegmentCodecs
argument_list|()
argument_list|,
name|fieldInfos
operator|.
name|hasVectors
argument_list|()
argument_list|,
name|fieldInfos
argument_list|)
argument_list|,
name|BufferedIndexInput
operator|.
name|BUFFER_SIZE
argument_list|,
literal|true
argument_list|,
name|IndexReader
operator|.
name|DEFAULT_TERMS_INDEX_DIVISOR
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|mergedReader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mergedReader
operator|.
name|numDocs
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Document
name|newDoc1
init|=
name|mergedReader
operator|.
name|document
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newDoc1
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//There are 2 unstored fields on the document
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|newDoc1
argument_list|)
operator|==
name|DocHelper
operator|.
name|numFields
argument_list|(
name|doc1
argument_list|)
operator|-
name|DocHelper
operator|.
name|unstored
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Document
name|newDoc2
init|=
name|mergedReader
operator|.
name|document
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newDoc2
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|numFields
argument_list|(
name|newDoc2
argument_list|)
operator|==
name|DocHelper
operator|.
name|numFields
argument_list|(
name|doc2
argument_list|)
operator|-
name|DocHelper
operator|.
name|unstored
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DocsEnum
name|termDocs
init|=
name|MultiFields
operator|.
name|getTermDocsEnum
argument_list|(
name|mergedReader
argument_list|,
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|mergedReader
argument_list|)
argument_list|,
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"field"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|termDocs
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termDocs
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|stored
init|=
name|mergedReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED_WITH_TERMVECTOR
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|stored
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//System.out.println("stored size: " + stored.size());
name|assertTrue
argument_list|(
literal|"We do not have 3 fields that were indexed with term vector"
argument_list|,
name|stored
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|TermFreqVector
name|vector
init|=
name|mergedReader
operator|.
name|getTermFreqVector
argument_list|(
literal|0
argument_list|,
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|vector
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|BytesRef
index|[]
name|terms
init|=
name|vector
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|terms
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//System.out.println("Terms size: " + terms.length);
name|assertTrue
argument_list|(
name|terms
operator|.
name|length
operator|==
literal|3
argument_list|)
expr_stmt|;
name|int
index|[]
name|freqs
init|=
name|vector
operator|.
name|getTermFrequencies
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|freqs
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//System.out.println("Freqs size: " + freqs.length);
name|assertTrue
argument_list|(
name|vector
operator|instanceof
name|TermPositionVector
operator|==
literal|true
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|terms
index|[
name|i
index|]
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|int
name|freq
init|=
name|freqs
index|[
name|i
index|]
decl_stmt|;
comment|//System.out.println("Term: " + term + " Freq: " + freq);
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|FIELD_2_TEXT
operator|.
name|indexOf
argument_list|(
name|term
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DocHelper
operator|.
name|FIELD_2_FREQS
index|[
name|i
index|]
operator|==
name|freq
argument_list|)
expr_stmt|;
block|}
name|TestSegmentReader
operator|.
name|checkNorms
argument_list|(
name|mergedReader
argument_list|)
expr_stmt|;
name|mergedReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
