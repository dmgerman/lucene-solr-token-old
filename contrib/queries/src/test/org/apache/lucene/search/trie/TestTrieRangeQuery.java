begin_unit
begin_package
DECL|package|org.apache.lucene.search.trie
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|trie
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
name|search
operator|.
name|TopDocs
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
name|Sort
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
name|RangeQuery
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
DECL|class|TestTrieRangeQuery
specifier|public
class|class
name|TestTrieRangeQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|distance
specifier|private
specifier|static
specifier|final
name|long
name|distance
init|=
literal|66666
decl_stmt|;
DECL|field|rnd
specifier|private
specifier|static
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|directory
specifier|private
specifier|static
name|RAMDirectory
name|directory
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
static|static
block|{
try|try
block|{
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
comment|// Add a series of 10000 docs with increasing long values
for|for
control|(
name|long
name|l
init|=
literal|0L
init|;
name|l
operator|<
literal|10000L
condition|;
name|l
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
comment|// add fields, that have a distance to test general functionality
name|TrieUtils
operator|.
name|VARIANT_8BIT
operator|.
name|addLongTrieCodedDocumentField
argument_list|(
name|doc
argument_list|,
literal|"field8"
argument_list|,
name|distance
operator|*
name|l
argument_list|,
literal|true
comment|/*index it*/
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
expr_stmt|;
name|TrieUtils
operator|.
name|VARIANT_4BIT
operator|.
name|addLongTrieCodedDocumentField
argument_list|(
name|doc
argument_list|,
literal|"field4"
argument_list|,
name|distance
operator|*
name|l
argument_list|,
literal|true
comment|/*index it*/
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
expr_stmt|;
name|TrieUtils
operator|.
name|VARIANT_2BIT
operator|.
name|addLongTrieCodedDocumentField
argument_list|(
name|doc
argument_list|,
literal|"field2"
argument_list|,
name|distance
operator|*
name|l
argument_list|,
literal|true
comment|/*index it*/
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
expr_stmt|;
comment|// add ascending fields with a distance of 1 to test the correct splitting of range and inclusive/exclusive
name|TrieUtils
operator|.
name|VARIANT_8BIT
operator|.
name|addLongTrieCodedDocumentField
argument_list|(
name|doc
argument_list|,
literal|"ascfield8"
argument_list|,
name|l
argument_list|,
literal|true
comment|/*index it*/
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
name|TrieUtils
operator|.
name|VARIANT_4BIT
operator|.
name|addLongTrieCodedDocumentField
argument_list|(
name|doc
argument_list|,
literal|"ascfield4"
argument_list|,
name|l
argument_list|,
literal|true
comment|/*index it*/
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
name|TrieUtils
operator|.
name|VARIANT_2BIT
operator|.
name|addLongTrieCodedDocumentField
argument_list|(
name|doc
argument_list|,
literal|"ascfield2"
argument_list|,
name|l
argument_list|,
literal|true
comment|/*index it*/
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Error
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|testRange
specifier|private
name|void
name|testRange
parameter_list|(
specifier|final
name|TrieUtils
name|variant
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|field
init|=
literal|"field"
operator|+
name|variant
operator|.
name|TRIE_BITS
decl_stmt|;
name|int
name|count
init|=
literal|3000
decl_stmt|;
name|long
name|lower
init|=
literal|96666L
decl_stmt|,
name|upper
init|=
name|lower
operator|+
name|count
operator|*
name|distance
operator|+
literal|1234L
decl_stmt|;
name|TrieRangeQuery
name|q
init|=
operator|new
name|TrieRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|Long
argument_list|(
name|lower
argument_list|)
argument_list|,
operator|new
name|Long
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|variant
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|10000
argument_list|,
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found "
operator|+
name|q
operator|.
name|getLastNumberOfTerms
argument_list|()
operator|+
literal|" distinct terms in range for field '"
operator|+
name|field
operator|+
literal|"'."
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|assertNotNull
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Score docs must match "
operator|+
name|count
operator|+
literal|" docs, found "
operator|+
name|sd
operator|.
name|length
operator|+
literal|" docs"
argument_list|,
name|sd
operator|.
name|length
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"First doc should be "
operator|+
operator|(
literal|2
operator|*
name|distance
operator|)
argument_list|,
name|variant
operator|.
name|trieCodedToLong
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
argument_list|,
literal|2
operator|*
name|distance
argument_list|)
expr_stmt|;
name|doc
operator|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
name|sd
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Last doc should be "
operator|+
operator|(
operator|(
literal|1
operator|+
name|count
operator|)
operator|*
name|distance
operator|)
argument_list|,
name|variant
operator|.
name|trieCodedToLong
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
argument_list|,
operator|(
literal|1
operator|+
name|count
operator|)
operator|*
name|distance
argument_list|)
expr_stmt|;
block|}
DECL|method|testRange_8bit
specifier|public
name|void
name|testRange_8bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testRange
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testRange_4bit
specifier|public
name|void
name|testRange_4bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testRange
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testRange_2bit
specifier|public
name|void
name|testRange_2bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testRange
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_2BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testLeftOpenRange
specifier|private
name|void
name|testLeftOpenRange
parameter_list|(
specifier|final
name|TrieUtils
name|variant
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|field
init|=
literal|"field"
operator|+
name|variant
operator|.
name|TRIE_BITS
decl_stmt|;
name|int
name|count
init|=
literal|3000
decl_stmt|;
name|long
name|upper
init|=
operator|(
name|count
operator|-
literal|1
operator|)
operator|*
name|distance
operator|+
literal|1234L
decl_stmt|;
name|TrieRangeQuery
name|q
init|=
operator|new
name|TrieRangeQuery
argument_list|(
name|field
argument_list|,
literal|null
argument_list|,
operator|new
name|Long
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|variant
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|10000
argument_list|,
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found "
operator|+
name|q
operator|.
name|getLastNumberOfTerms
argument_list|()
operator|+
literal|" distinct terms in left open range for field '"
operator|+
name|field
operator|+
literal|"'."
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|assertNotNull
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Score docs must match "
operator|+
name|count
operator|+
literal|" docs, found "
operator|+
name|sd
operator|.
name|length
operator|+
literal|" docs"
argument_list|,
name|sd
operator|.
name|length
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"First doc should be 0"
argument_list|,
name|variant
operator|.
name|trieCodedToLong
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|doc
operator|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
name|sd
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Last doc should be "
operator|+
operator|(
operator|(
name|count
operator|-
literal|1
operator|)
operator|*
name|distance
operator|)
argument_list|,
name|variant
operator|.
name|trieCodedToLong
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
argument_list|,
operator|(
name|count
operator|-
literal|1
operator|)
operator|*
name|distance
argument_list|)
expr_stmt|;
block|}
DECL|method|testLeftOpenRange_8bit
specifier|public
name|void
name|testLeftOpenRange_8bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testLeftOpenRange
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testLeftOpenRange_4bit
specifier|public
name|void
name|testLeftOpenRange_4bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testLeftOpenRange
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testLeftOpenRange_2bit
specifier|public
name|void
name|testLeftOpenRange_2bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testLeftOpenRange
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_2BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomTrieAndClassicRangeQuery
specifier|private
name|void
name|testRandomTrieAndClassicRangeQuery
parameter_list|(
specifier|final
name|TrieUtils
name|variant
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|field
init|=
literal|"field"
operator|+
name|variant
operator|.
name|TRIE_BITS
decl_stmt|;
comment|// 50 random tests, the tests may also return 0 results, if min>max, but this is ok
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|long
name|lower
init|=
call|(
name|long
call|)
argument_list|(
name|rnd
operator|.
name|nextDouble
argument_list|()
operator|*
literal|10000L
operator|*
name|distance
argument_list|)
decl_stmt|;
name|long
name|upper
init|=
call|(
name|long
call|)
argument_list|(
name|rnd
operator|.
name|nextDouble
argument_list|()
operator|*
literal|10000L
operator|*
name|distance
argument_list|)
decl_stmt|;
comment|// test inclusive range
name|TrieRangeQuery
name|tq
init|=
operator|new
name|TrieRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|Long
argument_list|(
name|lower
argument_list|)
argument_list|,
operator|new
name|Long
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|variant
argument_list|)
decl_stmt|;
name|RangeQuery
name|cq
init|=
operator|new
name|RangeQuery
argument_list|(
name|field
argument_list|,
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|lower
argument_list|)
argument_list|,
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|cq
operator|.
name|setConstantScoreRewrite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TopDocs
name|tTopDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TopDocs
name|cTopDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|cq
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Returned count for TrieRangeQuery and RangeQuery must be equal"
argument_list|,
name|tTopDocs
operator|.
name|totalHits
argument_list|,
name|cTopDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// test exclusive range
name|tq
operator|=
operator|new
name|TrieRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|Long
argument_list|(
name|lower
argument_list|)
argument_list|,
operator|new
name|Long
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|variant
argument_list|)
expr_stmt|;
name|cq
operator|=
operator|new
name|RangeQuery
argument_list|(
name|field
argument_list|,
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|lower
argument_list|)
argument_list|,
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cq
operator|.
name|setConstantScoreRewrite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tTopDocs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cTopDocs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|cq
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Returned count for TrieRangeQuery and RangeQuery must be equal"
argument_list|,
name|tTopDocs
operator|.
name|totalHits
argument_list|,
name|cTopDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// test left exclusive range
name|tq
operator|=
operator|new
name|TrieRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|Long
argument_list|(
name|lower
argument_list|)
argument_list|,
operator|new
name|Long
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|variant
argument_list|)
expr_stmt|;
name|cq
operator|=
operator|new
name|RangeQuery
argument_list|(
name|field
argument_list|,
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|lower
argument_list|)
argument_list|,
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cq
operator|.
name|setConstantScoreRewrite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tTopDocs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cTopDocs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|cq
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Returned count for TrieRangeQuery and RangeQuery must be equal"
argument_list|,
name|tTopDocs
operator|.
name|totalHits
argument_list|,
name|cTopDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// test right exclusive range
name|tq
operator|=
operator|new
name|TrieRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|Long
argument_list|(
name|lower
argument_list|)
argument_list|,
operator|new
name|Long
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|variant
argument_list|)
expr_stmt|;
name|cq
operator|=
operator|new
name|RangeQuery
argument_list|(
name|field
argument_list|,
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|lower
argument_list|)
argument_list|,
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cq
operator|.
name|setConstantScoreRewrite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tTopDocs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cTopDocs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|cq
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Returned count for TrieRangeQuery and RangeQuery must be equal"
argument_list|,
name|tTopDocs
operator|.
name|totalHits
argument_list|,
name|cTopDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRandomTrieAndClassicRangeQuery_8bit
specifier|public
name|void
name|testRandomTrieAndClassicRangeQuery_8bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testRandomTrieAndClassicRangeQuery
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomTrieAndClassicRangeQuery_4bit
specifier|public
name|void
name|testRandomTrieAndClassicRangeQuery_4bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testRandomTrieAndClassicRangeQuery
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomTrieAndClassicRangeQuery_2bit
specifier|public
name|void
name|testRandomTrieAndClassicRangeQuery_2bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testRandomTrieAndClassicRangeQuery
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_2BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testRangeSplit
specifier|private
name|void
name|testRangeSplit
parameter_list|(
specifier|final
name|TrieUtils
name|variant
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|field
init|=
literal|"ascfield"
operator|+
name|variant
operator|.
name|TRIE_BITS
decl_stmt|;
comment|// 50 random tests
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|long
name|lower
init|=
call|(
name|long
call|)
argument_list|(
name|rnd
operator|.
name|nextDouble
argument_list|()
operator|*
literal|10000L
argument_list|)
decl_stmt|;
name|long
name|upper
init|=
call|(
name|long
call|)
argument_list|(
name|rnd
operator|.
name|nextDouble
argument_list|()
operator|*
literal|10000L
argument_list|)
decl_stmt|;
if|if
condition|(
name|lower
operator|>
name|upper
condition|)
block|{
name|long
name|a
init|=
name|lower
decl_stmt|;
name|lower
operator|=
name|upper
expr_stmt|;
name|upper
operator|=
name|a
expr_stmt|;
block|}
comment|// test inclusive range
name|TrieRangeQuery
name|tq
init|=
operator|new
name|TrieRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|Long
argument_list|(
name|lower
argument_list|)
argument_list|,
operator|new
name|Long
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|variant
argument_list|)
decl_stmt|;
name|TopDocs
name|tTopDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Returned count of range query must be equal to inclusive range length"
argument_list|,
name|tTopDocs
operator|.
name|totalHits
argument_list|,
name|upper
operator|-
name|lower
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// test exclusive range
name|tq
operator|=
operator|new
name|TrieRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|Long
argument_list|(
name|lower
argument_list|)
argument_list|,
operator|new
name|Long
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|variant
argument_list|)
expr_stmt|;
name|tTopDocs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Returned count of range query must be equal to exclusive range length"
argument_list|,
name|tTopDocs
operator|.
name|totalHits
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|upper
operator|-
name|lower
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// test left exclusive range
name|tq
operator|=
operator|new
name|TrieRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|Long
argument_list|(
name|lower
argument_list|)
argument_list|,
operator|new
name|Long
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|variant
argument_list|)
expr_stmt|;
name|tTopDocs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Returned count of range query must be equal to half exclusive range length"
argument_list|,
name|tTopDocs
operator|.
name|totalHits
argument_list|,
name|upper
operator|-
name|lower
argument_list|)
expr_stmt|;
comment|// test right exclusive range
name|tq
operator|=
operator|new
name|TrieRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|Long
argument_list|(
name|lower
argument_list|)
argument_list|,
operator|new
name|Long
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|variant
argument_list|)
expr_stmt|;
name|tTopDocs
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Returned count of range query must be equal to half exclusive range length"
argument_list|,
name|tTopDocs
operator|.
name|totalHits
argument_list|,
name|upper
operator|-
name|lower
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRangeSplit_8bit
specifier|public
name|void
name|testRangeSplit_8bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testRangeSplit
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testRangeSplit_4bit
specifier|public
name|void
name|testRangeSplit_4bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testRangeSplit
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testRangeSplit_2bit
specifier|public
name|void
name|testRangeSplit_2bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testRangeSplit
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_2BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testSorting
specifier|private
name|void
name|testSorting
parameter_list|(
specifier|final
name|TrieUtils
name|variant
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|field
init|=
literal|"field"
operator|+
name|variant
operator|.
name|TRIE_BITS
decl_stmt|;
comment|// 10 random tests, the index order is ascending,
comment|// so using a reverse sort field should retun descending documents
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
name|long
name|lower
init|=
call|(
name|long
call|)
argument_list|(
name|rnd
operator|.
name|nextDouble
argument_list|()
operator|*
literal|10000L
operator|*
name|distance
argument_list|)
decl_stmt|;
name|long
name|upper
init|=
call|(
name|long
call|)
argument_list|(
name|rnd
operator|.
name|nextDouble
argument_list|()
operator|*
literal|10000L
operator|*
name|distance
argument_list|)
decl_stmt|;
if|if
condition|(
name|lower
operator|>
name|upper
condition|)
block|{
name|long
name|a
init|=
name|lower
decl_stmt|;
name|lower
operator|=
name|upper
expr_stmt|;
name|upper
operator|=
name|a
expr_stmt|;
block|}
name|TrieRangeQuery
name|tq
init|=
operator|new
name|TrieRangeQuery
argument_list|(
name|field
argument_list|,
operator|new
name|Long
argument_list|(
name|lower
argument_list|)
argument_list|,
operator|new
name|Long
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|variant
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|null
argument_list|,
literal|10000
argument_list|,
operator|new
name|Sort
argument_list|(
name|variant
operator|.
name|getSortField
argument_list|(
name|field
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|topDocs
operator|.
name|totalHits
operator|==
literal|0
condition|)
continue|continue;
name|ScoreDoc
index|[]
name|sd
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|assertNotNull
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|long
name|last
init|=
name|variant
operator|.
name|trieCodedToLong
argument_list|(
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|sd
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|long
name|act
init|=
name|variant
operator|.
name|trieCodedToLong
argument_list|(
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
name|j
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Docs should be sorted backwards"
argument_list|,
name|last
operator|>
name|act
argument_list|)
expr_stmt|;
name|last
operator|=
name|act
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSorting_8bit
specifier|public
name|void
name|testSorting_8bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testSorting
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testSorting_4bit
specifier|public
name|void
name|testSorting_4bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testSorting
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testSorting_2bit
specifier|public
name|void
name|testSorting_2bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testSorting
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_2BIT
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
