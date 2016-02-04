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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import
begin_class
DECL|class|TestDocValuesNumbersQuery
specifier|public
class|class
name|TestDocValuesNumbersQuery
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"field"
argument_list|,
literal|17L
argument_list|,
literal|42L
argument_list|)
argument_list|,
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"field"
argument_list|,
literal|17L
argument_list|,
literal|42L
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"field"
argument_list|,
literal|17L
argument_list|,
literal|42L
argument_list|,
literal|32416190071L
argument_list|)
argument_list|,
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"field"
argument_list|,
literal|17L
argument_list|,
literal|32416190071L
argument_list|,
literal|42L
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"field"
argument_list|,
literal|42L
argument_list|)
operator|.
name|equals
argument_list|(
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"field2"
argument_list|,
literal|42L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"field"
argument_list|,
literal|17L
argument_list|,
literal|42L
argument_list|)
operator|.
name|equals
argument_list|(
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"field"
argument_list|,
literal|17L
argument_list|,
literal|32416190071L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDuelTermsQuery
specifier|public
name|void
name|testDuelTermsQuery
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
literal|2
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
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|allNumbers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numNumbers
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
operator|<<
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
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
name|numNumbers
condition|;
operator|++
name|i
control|)
block|{
name|allNumbers
operator|.
name|add
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|Long
name|number
init|=
name|allNumbers
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|allNumbers
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"text"
argument_list|,
name|number
operator|.
name|toString
argument_list|()
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"long"
argument_list|,
name|number
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"twolongs"
argument_list|,
name|number
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedNumericDocValuesField
argument_list|(
literal|"twolongs"
argument_list|,
name|number
operator|*
literal|2
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
block|}
if|if
condition|(
name|numNumbers
operator|>
literal|1
operator|&&
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
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
name|allNumbers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
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
if|if
condition|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// may occasionally happen if all documents got the same term
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|,
name|dir
argument_list|)
expr_stmt|;
continue|continue;
block|}
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
name|int
name|numQueryNumbers
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
operator|<<
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|8
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Long
argument_list|>
name|queryNumbers
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Long
argument_list|>
name|queryNumbersX2
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
name|numQueryNumbers
condition|;
operator|++
name|j
control|)
block|{
name|Long
name|number
init|=
name|allNumbers
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|allNumbers
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|queryNumbers
operator|.
name|add
argument_list|(
name|number
argument_list|)
expr_stmt|;
name|queryNumbersX2
operator|.
name|add
argument_list|(
literal|2
operator|*
name|number
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Long
name|number
range|:
name|queryNumbers
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
name|number
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|Query
name|q1
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|)
argument_list|,
name|boost
argument_list|)
decl_stmt|;
name|Query
name|q2
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"long"
argument_list|,
name|queryNumbers
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
name|q2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Query
name|q3
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"twolongs"
argument_list|,
name|queryNumbers
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
name|q3
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Query
name|q4
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"twolongs"
argument_list|,
name|queryNumbersX2
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
name|q4
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
block|}
DECL|method|testApproximation
specifier|public
name|void
name|testApproximation
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
literal|2
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
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|allNumbers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numNumbers
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
operator|<<
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
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
name|numNumbers
condition|;
operator|++
name|i
control|)
block|{
name|allNumbers
operator|.
name|add
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|Long
name|number
init|=
name|allNumbers
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|allNumbers
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"text"
argument_list|,
name|number
operator|.
name|toString
argument_list|()
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"long"
argument_list|,
name|number
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
block|}
if|if
condition|(
name|numNumbers
operator|>
literal|1
operator|&&
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
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
name|allNumbers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
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
if|if
condition|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// may occasionally happen if all documents got the same term
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|,
name|dir
argument_list|)
expr_stmt|;
continue|continue;
block|}
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
name|int
name|numQueryNumbers
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
operator|<<
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|8
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Long
argument_list|>
name|queryNumbers
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
name|numQueryNumbers
condition|;
operator|++
name|j
control|)
block|{
name|queryNumbers
operator|.
name|add
argument_list|(
name|allNumbers
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|allNumbers
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Long
name|number
range|:
name|queryNumbers
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
name|number
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|Query
name|q1
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|)
argument_list|,
name|boost
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|q2
init|=
operator|new
name|BoostQuery
argument_list|(
operator|new
name|DocValuesNumbersQuery
argument_list|(
literal|"long"
argument_list|,
name|queryNumbers
argument_list|)
argument_list|,
name|boost
argument_list|)
decl_stmt|;
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
name|q1
argument_list|,
name|Occur
operator|.
name|MUST
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
literal|"text"
argument_list|,
name|allNumbers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|FILTER
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
name|q2
argument_list|,
name|Occur
operator|.
name|MUST
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
literal|"text"
argument_list|,
name|allNumbers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
name|assertSameMatches
argument_list|(
name|searcher
argument_list|,
name|bq1
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
block|}
end_class
end_unit
