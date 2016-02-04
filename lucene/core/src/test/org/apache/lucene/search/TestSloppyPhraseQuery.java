begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|MockTokenizer
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
name|Term
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
begin_class
DECL|class|TestSloppyPhraseQuery
specifier|public
class|class
name|TestSloppyPhraseQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|S_1
specifier|private
specifier|static
specifier|final
name|String
name|S_1
init|=
literal|"A A A"
decl_stmt|;
DECL|field|S_2
specifier|private
specifier|static
specifier|final
name|String
name|S_2
init|=
literal|"A 1 2 3 A 4 5 6 A"
decl_stmt|;
DECL|field|DOC_1
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_1
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_1
operator|+
literal|" Y"
argument_list|)
decl_stmt|;
DECL|field|DOC_2
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_2
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_2
operator|+
literal|" Y"
argument_list|)
decl_stmt|;
DECL|field|DOC_3
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_3
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_1
operator|+
literal|" A Y"
argument_list|)
decl_stmt|;
DECL|field|DOC_1_B
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_1_B
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_1
operator|+
literal|" Y N N N N "
operator|+
name|S_1
operator|+
literal|" Z"
argument_list|)
decl_stmt|;
DECL|field|DOC_2_B
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_2_B
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_2
operator|+
literal|" Y N N N N "
operator|+
name|S_2
operator|+
literal|" Z"
argument_list|)
decl_stmt|;
DECL|field|DOC_3_B
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_3_B
init|=
name|makeDocument
argument_list|(
literal|"X "
operator|+
name|S_1
operator|+
literal|" A Y N N N N "
operator|+
name|S_1
operator|+
literal|" A Y"
argument_list|)
decl_stmt|;
DECL|field|DOC_4
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_4
init|=
name|makeDocument
argument_list|(
literal|"A A X A X B A X B B A A X B A A"
argument_list|)
decl_stmt|;
DECL|field|DOC_5_3
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_5_3
init|=
name|makeDocument
argument_list|(
literal|"H H H X X X H H H X X X H H H"
argument_list|)
decl_stmt|;
DECL|field|DOC_5_4
specifier|private
specifier|static
specifier|final
name|Document
name|DOC_5_4
init|=
name|makeDocument
argument_list|(
literal|"H H H H"
argument_list|)
decl_stmt|;
DECL|field|QUERY_1
specifier|private
specifier|static
specifier|final
name|PhraseQuery
name|QUERY_1
init|=
name|makePhraseQuery
argument_list|(
name|S_1
argument_list|)
decl_stmt|;
DECL|field|QUERY_2
specifier|private
specifier|static
specifier|final
name|PhraseQuery
name|QUERY_2
init|=
name|makePhraseQuery
argument_list|(
name|S_2
argument_list|)
decl_stmt|;
DECL|field|QUERY_4
specifier|private
specifier|static
specifier|final
name|PhraseQuery
name|QUERY_4
init|=
name|makePhraseQuery
argument_list|(
literal|"X A A"
argument_list|)
decl_stmt|;
DECL|field|QUERY_5_4
specifier|private
specifier|static
specifier|final
name|PhraseQuery
name|QUERY_5_4
init|=
name|makePhraseQuery
argument_list|(
literal|"H H H H"
argument_list|)
decl_stmt|;
comment|/**    * Test DOC_4 and QUERY_4.    * QUERY_4 has a fuzzy (len=1) match to DOC_4, so all slop values&gt; 0 should succeed.    * But only the 3rd sequence of A's in DOC_4 will do.    */
DECL|method|testDoc4_Query4_All_Slops_Should_match
specifier|public
name|void
name|testDoc4_Query4_All_Slops_Should_match
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|30
condition|;
name|slop
operator|++
control|)
block|{
name|int
name|numResultsExpected
init|=
name|slop
operator|<
literal|1
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|checkPhraseQuery
argument_list|(
name|DOC_4
argument_list|,
name|QUERY_4
argument_list|,
name|slop
argument_list|,
name|numResultsExpected
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test DOC_1 and QUERY_1.    * QUERY_1 has an exact match to DOC_1, so all slop values should succeed.    * Before LUCENE-1310, a slop value of 1 did not succeed.    */
DECL|method|testDoc1_Query1_All_Slops_Should_match
specifier|public
name|void
name|testDoc1_Query1_All_Slops_Should_match
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|30
condition|;
name|slop
operator|++
control|)
block|{
name|float
name|freq1
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_1
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|float
name|freq2
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_1_B
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"slop="
operator|+
name|slop
operator|+
literal|" freq2="
operator|+
name|freq2
operator|+
literal|" should be greater than score1 "
operator|+
name|freq1
argument_list|,
name|freq2
operator|>
name|freq1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test DOC_2 and QUERY_1.    * 6 should be the minimum slop to make QUERY_1 match DOC_2.    * Before LUCENE-1310, 7 was the minimum.    */
DECL|method|testDoc2_Query1_Slop_6_or_more_Should_match
specifier|public
name|void
name|testDoc2_Query1_Slop_6_or_more_Should_match
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|30
condition|;
name|slop
operator|++
control|)
block|{
name|int
name|numResultsExpected
init|=
name|slop
operator|<
literal|6
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|float
name|freq1
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_2
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
name|numResultsExpected
argument_list|)
decl_stmt|;
if|if
condition|(
name|numResultsExpected
operator|>
literal|0
condition|)
block|{
name|float
name|freq2
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_2_B
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"slop="
operator|+
name|slop
operator|+
literal|" freq2="
operator|+
name|freq2
operator|+
literal|" should be greater than freq1 "
operator|+
name|freq1
argument_list|,
name|freq2
operator|>
name|freq1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test DOC_2 and QUERY_2.    * QUERY_2 has an exact match to DOC_2, so all slop values should succeed.    * Before LUCENE-1310, 0 succeeds, 1 through 7 fail, and 8 or greater succeeds.    */
DECL|method|testDoc2_Query2_All_Slops_Should_match
specifier|public
name|void
name|testDoc2_Query2_All_Slops_Should_match
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|30
condition|;
name|slop
operator|++
control|)
block|{
name|float
name|freq1
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_2
argument_list|,
name|QUERY_2
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|float
name|freq2
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_2_B
argument_list|,
name|QUERY_2
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"slop="
operator|+
name|slop
operator|+
literal|" freq2="
operator|+
name|freq2
operator|+
literal|" should be greater than freq1 "
operator|+
name|freq1
argument_list|,
name|freq2
operator|>
name|freq1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test DOC_3 and QUERY_1.    * QUERY_1 has an exact match to DOC_3, so all slop values should succeed.    */
DECL|method|testDoc3_Query1_All_Slops_Should_match
specifier|public
name|void
name|testDoc3_Query1_All_Slops_Should_match
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|30
condition|;
name|slop
operator|++
control|)
block|{
name|float
name|freq1
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_3
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|float
name|freq2
init|=
name|checkPhraseQuery
argument_list|(
name|DOC_3_B
argument_list|,
name|QUERY_1
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"slop="
operator|+
name|slop
operator|+
literal|" freq2="
operator|+
name|freq2
operator|+
literal|" should be greater than freq1 "
operator|+
name|freq1
argument_list|,
name|freq2
operator|>
name|freq1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** LUCENE-3412 */
DECL|method|testDoc5_Query5_Any_Slop_Should_be_consistent
specifier|public
name|void
name|testDoc5_Query5_Any_Slop_Should_be_consistent
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|nRepeats
init|=
literal|5
decl_stmt|;
for|for
control|(
name|int
name|slop
init|=
literal|0
init|;
name|slop
operator|<
literal|3
condition|;
name|slop
operator|++
control|)
block|{
for|for
control|(
name|int
name|trial
init|=
literal|0
init|;
name|trial
operator|<
name|nRepeats
condition|;
name|trial
operator|++
control|)
block|{
comment|// should steadily always find this one
name|checkPhraseQuery
argument_list|(
name|DOC_5_4
argument_list|,
name|QUERY_5_4
argument_list|,
name|slop
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|trial
init|=
literal|0
init|;
name|trial
operator|<
name|nRepeats
condition|;
name|trial
operator|++
control|)
block|{
comment|// should steadily never find this one
name|checkPhraseQuery
argument_list|(
name|DOC_5_3
argument_list|,
name|QUERY_5_4
argument_list|,
name|slop
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|checkPhraseQuery
specifier|private
name|float
name|checkPhraseQuery
parameter_list|(
name|Document
name|doc
parameter_list|,
name|PhraseQuery
name|query
parameter_list|,
name|int
name|slop
parameter_list|,
name|int
name|expectedNumResults
parameter_list|)
throws|throws
name|Exception
block|{
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|Term
index|[]
name|terms
init|=
name|query
operator|.
name|getTerms
argument_list|()
decl_stmt|;
name|int
index|[]
name|positions
init|=
name|query
operator|.
name|getPositions
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
name|terms
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|,
name|positions
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|query
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|MockDirectoryWrapper
name|ramDir
init|=
operator|new
name|MockDirectoryWrapper
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|RAMDirectory
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
argument_list|()
argument_list|,
name|ramDir
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|MaxFreqCollector
name|c
init|=
operator|new
name|MaxFreqCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"slop: "
operator|+
name|slop
operator|+
literal|"  query: "
operator|+
name|query
operator|+
literal|"  doc: "
operator|+
name|doc
operator|+
literal|"  Wrong number of hits"
argument_list|,
name|expectedNumResults
argument_list|,
name|c
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//QueryUtils.check(query,searcher);
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// returns the max Scorer.freq() found, because even though norms are omitted, many index stats are different
comment|// with these different tokens/distributions/lengths.. otherwise this test is very fragile.
return|return
name|c
operator|.
name|max
return|;
block|}
DECL|method|makeDocument
specifier|private
specifier|static
name|Document
name|makeDocument
parameter_list|(
name|String
name|docText
parameter_list|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
literal|"f"
argument_list|,
name|docText
argument_list|,
name|customType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|makePhraseQuery
specifier|private
specifier|static
name|PhraseQuery
name|makePhraseQuery
parameter_list|(
name|String
name|terms
parameter_list|)
block|{
name|String
index|[]
name|t
init|=
name|terms
operator|.
name|split
argument_list|(
literal|" +"
argument_list|)
decl_stmt|;
return|return
operator|new
name|PhraseQuery
argument_list|(
literal|"f"
argument_list|,
name|t
argument_list|)
return|;
block|}
DECL|class|MaxFreqCollector
specifier|static
class|class
name|MaxFreqCollector
extends|extends
name|SimpleCollector
block|{
DECL|field|max
name|float
name|max
decl_stmt|;
DECL|field|totalHits
name|int
name|totalHits
decl_stmt|;
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|totalHits
operator|++
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|scorer
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/** checks that no scores or freqs are infinite */
DECL|method|assertSaneScoring
specifier|private
name|void
name|assertSaneScoring
parameter_list|(
name|PhraseQuery
name|pq
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|Exception
block|{
name|searcher
operator|.
name|search
argument_list|(
name|pq
argument_list|,
operator|new
name|SimpleCollector
argument_list|()
block|{
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|assertFalse
argument_list|(
name|Float
operator|.
name|isInfinite
argument_list|(
name|scorer
operator|.
name|freq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Float
operator|.
name|isInfinite
argument_list|(
name|scorer
operator|.
name|score
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|()
argument_list|,
name|pq
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-3215
DECL|method|testSlopWithHoles
specifier|public
name|void
name|testSlopWithHoles
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
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
literal|"lyrics"
argument_list|,
literal|""
argument_list|,
name|customType
argument_list|)
decl_stmt|;
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
name|f
argument_list|)
expr_stmt|;
name|f
operator|.
name|setStringValue
argument_list|(
literal|"drug drug"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|f
operator|.
name|setStringValue
argument_list|(
literal|"drug druggy drug"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|f
operator|.
name|setStringValue
argument_list|(
literal|"drug druggy druggy drug"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|f
operator|.
name|setStringValue
argument_list|(
literal|"drug druggy drug druggy drug"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"lyrics"
argument_list|,
literal|"drug"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"lyrics"
argument_list|,
literal|"drug"
argument_list|)
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|PhraseQuery
name|pq
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// "drug the drug"~1
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|is
operator|.
name|search
argument_list|(
name|pq
argument_list|,
literal|4
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|pq
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|is
operator|.
name|search
argument_list|(
name|pq
argument_list|,
literal|4
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setSlop
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|pq
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|is
operator|.
name|search
argument_list|(
name|pq
argument_list|,
literal|4
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|ir
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
comment|// LUCENE-3215
DECL|method|testInfiniteFreq1
specifier|public
name|void
name|testInfiniteFreq1
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|document
init|=
literal|"drug druggy drug drug drug"
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
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
name|newField
argument_list|(
literal|"lyrics"
argument_list|,
name|document
argument_list|,
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"lyrics"
argument_list|,
literal|"drug"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"lyrics"
argument_list|,
literal|"drug"
argument_list|)
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|PhraseQuery
name|pq
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// "drug the drug"~1
name|assertSaneScoring
argument_list|(
name|pq
argument_list|,
name|is
argument_list|)
expr_stmt|;
name|ir
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
comment|// LUCENE-3215
DECL|method|testInfiniteFreq2
specifier|public
name|void
name|testInfiniteFreq2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|document
init|=
literal|"So much fun to be had in my head "
operator|+
literal|"No more sunshine "
operator|+
literal|"So much fun just lying in my bed "
operator|+
literal|"No more sunshine "
operator|+
literal|"I can't face the sunlight and the dirt outside "
operator|+
literal|"Wanna stay in 666 where this darkness don't lie "
operator|+
literal|"Drug drug druggy "
operator|+
literal|"Got a feeling sweet like honey "
operator|+
literal|"Drug drug druggy "
operator|+
literal|"Need sensation like my baby "
operator|+
literal|"Show me your scars you're so aware "
operator|+
literal|"I'm not barbaric I just care "
operator|+
literal|"Drug drug drug "
operator|+
literal|"I need a reflection to prove I exist "
operator|+
literal|"No more sunshine "
operator|+
literal|"I am a victim of designer blitz "
operator|+
literal|"No more sunshine "
operator|+
literal|"Dance like a robot when you're chained at the knee "
operator|+
literal|"The C.I.A say you're all they'll ever need "
operator|+
literal|"Drug drug druggy "
operator|+
literal|"Got a feeling sweet like honey "
operator|+
literal|"Drug drug druggy "
operator|+
literal|"Need sensation like my baby "
operator|+
literal|"Snort your lines you're so aware "
operator|+
literal|"I'm not barbaric I just care "
operator|+
literal|"Drug drug druggy "
operator|+
literal|"Got a feeling sweet like honey "
operator|+
literal|"Drug drug druggy "
operator|+
literal|"Need sensation like my baby"
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
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
name|newField
argument_list|(
literal|"lyrics"
argument_list|,
name|document
argument_list|,
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|PhraseQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|PhraseQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"lyrics"
argument_list|,
literal|"drug"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"lyrics"
argument_list|,
literal|"drug"
argument_list|)
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setSlop
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|PhraseQuery
name|pq
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// "drug the drug"~5
name|assertSaneScoring
argument_list|(
name|pq
argument_list|,
name|is
argument_list|)
expr_stmt|;
name|ir
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
block|}
end_class
end_unit
