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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|document
operator|.
name|Document
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
name|TestCase
block|{
comment|//The variables for the new merged segment
DECL|field|mergedDir
specifier|private
name|Directory
name|mergedDir
init|=
operator|new
name|RAMDirectory
argument_list|()
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
init|=
operator|new
name|RAMDirectory
argument_list|()
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
DECL|field|merge1Segment
specifier|private
name|String
name|merge1Segment
init|=
literal|"test-1"
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
init|=
operator|new
name|RAMDirectory
argument_list|()
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
DECL|field|merge2Segment
specifier|private
name|String
name|merge2Segment
init|=
literal|"test-2"
decl_stmt|;
DECL|field|reader2
specifier|private
name|SegmentReader
name|reader2
init|=
literal|null
decl_stmt|;
DECL|method|TestSegmentMerger
specifier|public
name|TestSegmentMerger
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|merge1Dir
argument_list|,
name|merge1Segment
argument_list|,
name|doc1
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|setupDoc
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|DocHelper
operator|.
name|writeDoc
argument_list|(
name|merge2Dir
argument_list|,
name|merge2Segment
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
try|try
block|{
name|reader1
operator|=
operator|new
name|SegmentReader
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|merge1Segment
argument_list|,
literal|1
argument_list|,
name|merge1Dir
argument_list|)
argument_list|)
expr_stmt|;
name|reader2
operator|=
operator|new
name|SegmentReader
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|merge2Segment
argument_list|,
literal|1
argument_list|,
name|merge2Dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{    }
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
block|{
comment|//System.out.println("----------------TestMerge------------------");
name|SegmentMerger
name|merger
init|=
operator|new
name|SegmentMerger
argument_list|(
name|mergedDir
argument_list|,
name|mergedSegment
argument_list|,
literal|false
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
try|try
block|{
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
comment|//Should be able to open a new SegmentReader against the new directory
name|SegmentReader
name|mergedReader
init|=
operator|new
name|SegmentReader
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|mergedSegment
argument_list|,
name|docsMerged
argument_list|,
name|mergedDir
argument_list|)
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
literal|2
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
literal|2
argument_list|)
expr_stmt|;
name|TermDocs
name|termDocs
init|=
name|mergedReader
operator|.
name|termDocs
argument_list|(
operator|new
name|Term
argument_list|(
name|DocHelper
operator|.
name|TEXT_FIELD_2_KEY
argument_list|,
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
name|next
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|Collection
name|stored
init|=
name|mergedReader
operator|.
name|getIndexedFieldNames
argument_list|(
literal|true
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
name|stored
operator|.
name|size
argument_list|()
operator|==
literal|2
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
name|String
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
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|//System.out.println("---------------------end TestMerge-------------------");
block|}
block|}
end_class
end_unit
