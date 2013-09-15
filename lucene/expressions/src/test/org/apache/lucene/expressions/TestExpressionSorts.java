begin_unit
begin_package
DECL|package|org.apache.lucene.expressions
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|DoubleField
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
name|FloatDocValuesField
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
name|FloatField
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
name|LongField
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
name|expressions
operator|.
name|js
operator|.
name|JavascriptCompiler
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
name|BooleanQuery
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
name|CheckHits
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
name|Filter
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
name|MatchAllDocsQuery
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
name|Query
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
name|QueryWrapperFilter
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
name|SortField
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
name|TermQuery
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
begin_comment
comment|/**  * Tests some basic expressions against different queries,  * and fieldcache/docvalues fields against an equivalent sort.  */
end_comment
begin_class
DECL|class|TestExpressionSorts
specifier|public
class|class
name|TestExpressionSorts
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
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
name|int
name|numDocs
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2049
argument_list|,
literal|4000
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
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"english"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"oddeven"
argument_list|,
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
operator|)
condition|?
literal|"even"
else|:
literal|"odd"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"byte"
argument_list|,
literal|""
operator|+
operator|(
operator|(
name|byte
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"short"
argument_list|,
literal|""
operator|+
operator|(
operator|(
name|short
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|IntField
argument_list|(
literal|"int"
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|LongField
argument_list|(
literal|"long"
argument_list|,
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|FloatField
argument_list|(
literal|"float"
argument_list|,
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|DoubleField
argument_list|(
literal|"double"
argument_list|,
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"intdocvalues"
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|FloatDocValuesField
argument_list|(
literal|"floatdocvalues"
argument_list|,
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testQueries
specifier|public
name|void
name|testQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|n
init|=
name|atLeast
argument_list|(
literal|4
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|Filter
name|odd
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"oddeven"
argument_list|,
literal|"odd"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"english"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|odd
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"english"
argument_list|,
literal|"four"
argument_list|)
argument_list|)
argument_list|,
name|odd
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
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
literal|"english"
argument_list|,
literal|"one"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
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
literal|"oddeven"
argument_list|,
literal|"even"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|bq
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// force out of order
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
literal|"english"
argument_list|,
literal|"two"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|bq
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertQuery
name|void
name|assertQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|)
throws|throws
name|Exception
block|{
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
name|boolean
name|reversed
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|SortField
name|fields
index|[]
init|=
operator|new
name|SortField
index|[]
block|{
operator|new
name|SortField
argument_list|(
literal|"int"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
name|reversed
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"long"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
name|reversed
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"float"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
name|reversed
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"double"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
argument_list|,
name|reversed
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"intdocvalues"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|,
name|reversed
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"floatdocvalues"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
argument_list|,
name|reversed
argument_list|)
block|,
operator|new
name|SortField
argument_list|(
literal|"score"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
block|}
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|fields
argument_list|)
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|numSorts
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|fields
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
operator|new
name|Sort
argument_list|(
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|fields
argument_list|,
literal|0
argument_list|,
name|numSorts
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertQuery
name|void
name|assertQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|size
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
operator|/
literal|5
argument_list|)
decl_stmt|;
name|TopDocs
name|expected
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|size
argument_list|,
name|sort
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
comment|// make our actual sort, mutating original by replacing some of the
comment|// sortfields with equivalent expressions
name|SortField
name|original
index|[]
init|=
name|sort
operator|.
name|getSort
argument_list|()
decl_stmt|;
name|SortField
name|mutated
index|[]
init|=
operator|new
name|SortField
index|[
name|original
operator|.
name|length
index|]
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
name|mutated
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|>
literal|0
condition|)
block|{
name|SortField
name|s
init|=
name|original
index|[
name|i
index|]
decl_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
name|s
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
name|SimpleBindings
name|simpleBindings
init|=
operator|new
name|SimpleBindings
argument_list|()
decl_stmt|;
name|simpleBindings
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|boolean
name|reverse
init|=
name|s
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|Type
operator|.
name|SCORE
operator|||
name|s
operator|.
name|getReverse
argument_list|()
decl_stmt|;
name|mutated
index|[
name|i
index|]
operator|=
name|expr
operator|.
name|getSortField
argument_list|(
name|simpleBindings
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mutated
index|[
name|i
index|]
operator|=
name|original
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
name|Sort
name|mutatedSort
init|=
operator|new
name|Sort
argument_list|(
name|mutated
argument_list|)
decl_stmt|;
name|TopDocs
name|actual
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|size
argument_list|,
name|mutatedSort
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
name|CheckHits
operator|.
name|checkEqual
argument_list|(
name|query
argument_list|,
name|expected
operator|.
name|scoreDocs
argument_list|,
name|actual
operator|.
name|scoreDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|<
name|actual
operator|.
name|totalHits
condition|)
block|{
name|expected
operator|=
name|searcher
operator|.
name|searchAfter
argument_list|(
name|expected
operator|.
name|scoreDocs
index|[
name|size
operator|-
literal|1
index|]
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|size
argument_list|,
name|sort
argument_list|)
expr_stmt|;
name|actual
operator|=
name|searcher
operator|.
name|searchAfter
argument_list|(
name|actual
operator|.
name|scoreDocs
index|[
name|size
operator|-
literal|1
index|]
argument_list|,
name|query
argument_list|,
name|filter
argument_list|,
name|size
argument_list|,
name|mutatedSort
argument_list|)
expr_stmt|;
name|CheckHits
operator|.
name|checkEqual
argument_list|(
name|query
argument_list|,
name|expected
operator|.
name|scoreDocs
argument_list|,
name|actual
operator|.
name|scoreDocs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
