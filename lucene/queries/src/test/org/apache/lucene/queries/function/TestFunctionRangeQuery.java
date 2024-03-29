begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
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
name|IndexWriterConfig
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
name|Explanation
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
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|TestFunctionRangeQuery
specifier|public
class|class
name|TestFunctionRangeQuery
extends|extends
name|FunctionTestSetup
block|{
DECL|field|indexReader
name|IndexReader
name|indexReader
decl_stmt|;
DECL|field|indexSearcher
name|IndexSearcher
name|indexSearcher
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//doMultiSegment
block|}
annotation|@
name|Before
DECL|method|before
specifier|protected
name|void
name|before
parameter_list|()
throws|throws
name|IOException
block|{
name|indexReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|indexSearcher
operator|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|IOException
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRangeInt
specifier|public
name|void
name|testRangeInt
parameter_list|()
throws|throws
name|IOException
block|{
name|doTestRange
argument_list|(
name|INT_VALUESOURCE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRangeFloat
specifier|public
name|void
name|testRangeFloat
parameter_list|()
throws|throws
name|IOException
block|{
name|doTestRange
argument_list|(
name|FLOAT_VALUESOURCE
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestRange
specifier|private
name|void
name|doTestRange
parameter_list|(
name|ValueSource
name|valueSource
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|rangeQuery
init|=
operator|new
name|FunctionRangeQuery
argument_list|(
name|valueSource
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|rangeQuery
argument_list|,
name|N_DOCS
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|expectScores
argument_list|(
name|scoreDocs
argument_list|,
literal|3
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|rangeQuery
operator|=
operator|new
name|FunctionRangeQuery
argument_list|(
name|valueSource
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|scoreDocs
operator|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|rangeQuery
argument_list|,
name|N_DOCS
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|expectScores
argument_list|(
name|scoreDocs
argument_list|,
literal|4
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleted
specifier|public
name|void
name|testDeleted
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We delete doc with #3. Note we don't commit it to disk; we search using a near eal-time reader.
specifier|final
name|ValueSource
name|valueSource
init|=
name|INT_VALUESOURCE
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|FunctionRangeQuery
argument_list|(
name|valueSource
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|//delete the one with #3
assert|assert
name|writer
operator|.
name|hasDeletions
argument_list|()
assert|;
try|try
init|(
name|IndexReader
name|indexReader2
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
init|)
block|{
name|IndexSearcher
name|indexSearcher2
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader2
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|indexSearcher2
operator|.
name|search
argument_list|(
operator|new
name|FunctionRangeQuery
argument_list|(
name|valueSource
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
name|N_DOCS
argument_list|)
decl_stmt|;
name|expectScores
argument_list|(
name|topDocs
operator|.
name|scoreDocs
argument_list|,
literal|4
argument_list|)
expr_stmt|;
comment|//missing #3 because it's deleted
block|}
block|}
finally|finally
block|{
name|writer
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testExplain
specifier|public
name|void
name|testExplain
parameter_list|()
throws|throws
name|IOException
block|{
name|Query
name|rangeQuery
init|=
operator|new
name|FunctionRangeQuery
argument_list|(
name|INT_VALUESOURCE
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|rangeQuery
argument_list|,
name|N_DOCS
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|Explanation
name|explain
init|=
name|indexSearcher
operator|.
name|explain
argument_list|(
name|rangeQuery
argument_list|,
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
comment|// Just validate it looks reasonable
name|assertEquals
argument_list|(
literal|"2.0 = frange(int("
operator|+
name|INT_FIELD
operator|+
literal|")):[2 TO 2]\n"
operator|+
literal|"  2.0 = int("
operator|+
name|INT_FIELD
operator|+
literal|")=2\n"
argument_list|,
name|explain
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|expectScores
specifier|private
name|void
name|expectScores
parameter_list|(
name|ScoreDoc
index|[]
name|scoreDocs
parameter_list|,
name|int
modifier|...
name|docScores
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|docScores
operator|.
name|length
argument_list|,
name|scoreDocs
operator|.
name|length
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
name|docScores
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|docScores
index|[
name|i
index|]
argument_list|,
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
