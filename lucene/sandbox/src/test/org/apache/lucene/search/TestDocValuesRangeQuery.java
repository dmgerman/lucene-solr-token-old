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
name|document
operator|.
name|LongPoint
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
name|document
operator|.
name|NumericDocValuesField
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
name|SortedDocValuesField
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
name|SortedNumericDocValuesField
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
name|SortedSetDocValuesField
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
name|LeafReaderContext
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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|NumericUtils
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
DECL|class|TestDocValuesRangeQuery
specifier|public
class|class
name|TestDocValuesRangeQuery
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDuelNumericRangeQuery
specifier|public
name|void
name|testDuelNumericRangeQuery
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
operator|++
name|iter
control|)
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
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
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
operator|++
name|i
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numValues
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
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
name|numValues
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|long
name|value
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"dv"
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LongPoint
argument_list|(
literal|"idx"
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|iw
operator|.
name|deleteDocuments
argument_list|(
name|LongPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"idx"
argument_list|,
literal|0L
argument_list|,
literal|10L
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Long
name|min
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|Long
name|max
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|q1
init|=
name|LongPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"idx"
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|q2
init|=
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"dv"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertSameMatches
argument_list|(
name|searcher
argument_list|,
name|q1
argument_list|,
name|q2
argument_list|,
literal|false
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
block|}
DECL|method|toSortableBytes
specifier|private
specifier|static
name|BytesRef
name|toSortableBytes
parameter_list|(
name|Long
name|l
parameter_list|)
block|{
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|Long
operator|.
name|BYTES
index|]
decl_stmt|;
name|NumericUtils
operator|.
name|longToSortableBytes
argument_list|(
name|l
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
DECL|method|testDuelNumericSorted
specifier|public
name|void
name|testDuelNumericSorted
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
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
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
operator|++
name|i
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numValues
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
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
name|numValues
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|long
name|value
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"dv1"
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"dv2"
argument_list|,
name|toSortableBytes
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|iw
operator|.
name|deleteDocuments
argument_list|(
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"dv1"
argument_list|,
literal|0L
argument_list|,
literal|10L
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Long
name|min
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|Long
name|max
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|minInclusive
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|maxInclusive
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|Query
name|q1
init|=
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"dv1"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|q2
init|=
name|DocValuesRangeQuery
operator|.
name|newBytesRefRange
argument_list|(
literal|"dv2"
argument_list|,
name|toSortableBytes
argument_list|(
name|min
argument_list|)
argument_list|,
name|toSortableBytes
argument_list|(
name|max
argument_list|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
decl_stmt|;
name|assertSameMatches
argument_list|(
name|searcher
argument_list|,
name|q1
argument_list|,
name|q2
argument_list|,
literal|true
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
DECL|method|testScore
specifier|public
name|void
name|testScore
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
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
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
operator|++
name|i
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numValues
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
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
name|numValues
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|long
name|value
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"dv1"
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"dv2"
argument_list|,
name|toSortableBytes
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|iw
operator|.
name|deleteDocuments
argument_list|(
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"dv1"
argument_list|,
literal|0L
argument_list|,
literal|10L
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Long
name|min
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|Long
name|max
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|null
else|:
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|minInclusive
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|maxInclusive
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|float
name|boost
init|=
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|*
literal|10
decl_stmt|;
specifier|final
name|Query
name|q1
init|=
operator|new
name|BoostQuery
argument_list|(
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"dv1"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|,
name|boost
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|csq1
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"dv1"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
argument_list|,
name|boost
argument_list|)
decl_stmt|;
name|assertSameMatches
argument_list|(
name|searcher
argument_list|,
name|q1
argument_list|,
name|csq1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Query
name|q2
init|=
operator|new
name|BoostQuery
argument_list|(
name|DocValuesRangeQuery
operator|.
name|newBytesRefRange
argument_list|(
literal|"dv2"
argument_list|,
name|toSortableBytes
argument_list|(
name|min
argument_list|)
argument_list|,
name|toSortableBytes
argument_list|(
name|max
argument_list|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|,
name|boost
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|csq2
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|DocValuesRangeQuery
operator|.
name|newBytesRefRange
argument_list|(
literal|"dv2"
argument_list|,
name|toSortableBytes
argument_list|(
name|min
argument_list|)
argument_list|,
name|toSortableBytes
argument_list|(
name|max
argument_list|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
argument_list|,
name|boost
argument_list|)
decl_stmt|;
name|assertSameMatches
argument_list|(
name|searcher
argument_list|,
name|q2
argument_list|,
name|csq2
argument_list|,
literal|true
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
DECL|method|testApproximation
specifier|public
name|void
name|testApproximation
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
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
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
operator|++
name|i
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numValues
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
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
name|numValues
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|long
name|value
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"dv1"
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"dv2"
argument_list|,
name|toSortableBytes
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LongPoint
argument_list|(
literal|"idx"
argument_list|,
name|value
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
literal|"f"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"a"
else|:
literal|"b"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|iw
operator|.
name|deleteDocuments
argument_list|(
name|LongPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"idx"
argument_list|,
literal|0L
argument_list|,
literal|10L
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
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
literal|100
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Long
name|min
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|final
name|Long
name|max
init|=
name|TestUtil
operator|.
name|nextLong
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|ref
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|ref
operator|.
name|add
argument_list|(
name|LongPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"idx"
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
name|ref
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq1
operator|.
name|add
argument_list|(
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"dv1"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
name|bq1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameMatches
argument_list|(
name|searcher
argument_list|,
name|ref
operator|.
name|build
argument_list|()
argument_list|,
name|bq1
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq2
operator|.
name|add
argument_list|(
name|DocValuesRangeQuery
operator|.
name|newBytesRefRange
argument_list|(
literal|"dv2"
argument_list|,
name|toSortableBytes
argument_list|(
name|min
argument_list|)
argument_list|,
name|toSortableBytes
argument_list|(
name|max
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
name|bq2
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertSameMatches
argument_list|(
name|searcher
argument_list|,
name|ref
operator|.
name|build
argument_list|()
argument_list|,
name|bq2
operator|.
name|build
argument_list|()
argument_list|,
literal|true
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
DECL|method|assertSameMatches
specifier|private
name|void
name|assertSameMatches
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|q1
parameter_list|,
name|Query
name|q2
parameter_list|,
name|boolean
name|scores
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|TopDocs
name|td1
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q1
argument_list|,
name|maxDoc
argument_list|,
name|scores
condition|?
name|Sort
operator|.
name|RELEVANCE
else|:
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
specifier|final
name|TopDocs
name|td2
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q2
argument_list|,
name|maxDoc
argument_list|,
name|scores
condition|?
name|Sort
operator|.
name|RELEVANCE
else|:
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|td1
operator|.
name|totalHits
argument_list|,
name|td2
operator|.
name|totalHits
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
name|td1
operator|.
name|scoreDocs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|td1
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|,
name|td2
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|scores
condition|)
block|{
name|assertEquals
argument_list|(
name|td1
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|,
name|td2
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|,
literal|10e-7
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"f:[2 TO 5]"
argument_list|,
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"f"
argument_list|,
literal|2L
argument_list|,
literal|5L
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f:{2 TO 5]"
argument_list|,
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"f"
argument_list|,
literal|2L
argument_list|,
literal|5L
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f:{2 TO 5}"
argument_list|,
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"f"
argument_list|,
literal|2L
argument_list|,
literal|5L
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f:{* TO 5}"
argument_list|,
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"f"
argument_list|,
literal|null
argument_list|,
literal|5L
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f:[2 TO *}"
argument_list|,
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"f"
argument_list|,
literal|2L
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|min
init|=
operator|new
name|BytesRef
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|BytesRef
name|max
init|=
operator|new
name|BytesRef
argument_list|(
literal|"b"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"f:[[61] TO [62]]"
argument_list|,
name|DocValuesRangeQuery
operator|.
name|newBytesRefRange
argument_list|(
literal|"f"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f:{[61] TO [62]]"
argument_list|,
name|DocValuesRangeQuery
operator|.
name|newBytesRefRange
argument_list|(
literal|"f"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f:{[61] TO [62]}"
argument_list|,
name|DocValuesRangeQuery
operator|.
name|newBytesRefRange
argument_list|(
literal|"f"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f:{* TO [62]}"
argument_list|,
name|DocValuesRangeQuery
operator|.
name|newBytesRefRange
argument_list|(
literal|"f"
argument_list|,
literal|null
argument_list|,
name|max
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f:[[61] TO *}"
argument_list|,
name|DocValuesRangeQuery
operator|.
name|newBytesRefRange
argument_list|(
literal|"f"
argument_list|,
name|min
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocValuesRangeSupportsApproximation
specifier|public
name|void
name|testDocValuesRangeSupportsApproximation
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
operator|new
name|NumericDocValuesField
argument_list|(
literal|"dv1"
argument_list|,
literal|5L
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
literal|"dv2"
argument_list|,
name|toSortableBytes
argument_list|(
literal|42L
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
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
specifier|final
name|LeafReaderContext
name|ctx
init|=
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|Query
name|q1
init|=
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"dv1"
argument_list|,
literal|0L
argument_list|,
literal|100L
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|Weight
name|w
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|q1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Scorer
name|s
init|=
name|w
operator|.
name|scorer
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|s
operator|.
name|twoPhaseIterator
argument_list|()
argument_list|)
expr_stmt|;
name|Query
name|q2
init|=
name|DocValuesRangeQuery
operator|.
name|newBytesRefRange
argument_list|(
literal|"dv2"
argument_list|,
name|toSortableBytes
argument_list|(
literal|0L
argument_list|)
argument_list|,
name|toSortableBytes
argument_list|(
literal|100L
argument_list|)
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|q2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|s
operator|=
name|w
operator|.
name|scorer
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|s
operator|.
name|twoPhaseIterator
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|testLongRangeBoundaryValues
specifier|public
name|void
name|testLongRangeBoundaryValues
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
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|100l
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
name|SortedNumericDocValuesField
argument_list|(
literal|"dv"
argument_list|,
literal|200l
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
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|IndexReader
name|reader
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|Long
name|min
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|Long
name|max
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|Query
name|query
init|=
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"dv"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|searcher
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|min
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
name|max
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
name|query
operator|=
name|DocValuesRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"dv"
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|td
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|searcher
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|Sort
operator|.
name|INDEXORDER
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
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
block|}
end_class
end_unit
