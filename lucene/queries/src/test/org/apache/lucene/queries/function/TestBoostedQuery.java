begin_unit
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|ConstValueSource
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
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Basic tests for {@link BoostedQuery}  */
end_comment
begin_comment
comment|// TODO: more tests
end_comment
begin_class
DECL|class|TestBoostedQuery
specifier|public
class|class
name|TestBoostedQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|ir
specifier|static
name|IndexReader
name|ir
decl_stmt|;
DECL|field|is
specifier|static
name|IndexSearcher
name|is
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|iwConfig
init|=
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
decl_stmt|;
name|iwConfig
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
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
argument_list|,
name|iwConfig
argument_list|)
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|idField
init|=
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|ir
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|is
operator|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|is
operator|=
literal|null
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
operator|=
literal|null
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|TopDocs
name|docs
init|=
name|is
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|docs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
decl_stmt|;
name|Query
name|boostedQ
init|=
operator|new
name|BoostedQuery
argument_list|(
name|q
argument_list|,
operator|new
name|ConstValueSource
argument_list|(
literal|2.0f
argument_list|)
argument_list|)
decl_stmt|;
name|assertHits
argument_list|(
name|boostedQ
argument_list|,
operator|new
name|float
index|[]
block|{
name|score
operator|*
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|assertHits
name|void
name|assertHits
parameter_list|(
name|Query
name|q
parameter_list|,
name|float
name|scores
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
name|ScoreDoc
name|expected
index|[]
init|=
operator|new
name|ScoreDoc
index|[
name|scores
operator|.
name|length
index|]
decl_stmt|;
name|int
name|expectedDocs
index|[]
init|=
operator|new
name|int
index|[
name|scores
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
name|expected
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|expectedDocs
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
name|expected
index|[
name|i
index|]
operator|=
operator|new
name|ScoreDoc
argument_list|(
name|i
argument_list|,
name|scores
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|TopDocs
name|docs
init|=
name|is
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"id"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|CheckHits
operator|.
name|checkHits
argument_list|(
name|random
argument_list|()
argument_list|,
name|q
argument_list|,
literal|""
argument_list|,
name|is
argument_list|,
name|expectedDocs
argument_list|)
expr_stmt|;
name|CheckHits
operator|.
name|checkHitsQuery
argument_list|(
name|q
argument_list|,
name|expected
argument_list|,
name|docs
operator|.
name|scoreDocs
argument_list|,
name|expectedDocs
argument_list|)
expr_stmt|;
name|CheckHits
operator|.
name|checkExplanations
argument_list|(
name|q
argument_list|,
literal|""
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
