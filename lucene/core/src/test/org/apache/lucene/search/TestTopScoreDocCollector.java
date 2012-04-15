begin_unit
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
name|LuceneTestCase
import|;
end_import
begin_class
DECL|class|TestTopScoreDocCollector
specifier|public
class|class
name|TestTopScoreDocCollector
extends|extends
name|LuceneTestCase
block|{
DECL|method|testOutOfOrderCollection
specifier|public
name|void
name|testOutOfOrderCollection
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
name|writer
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
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
index|[]
name|inOrder
init|=
operator|new
name|boolean
index|[]
block|{
literal|false
block|,
literal|true
block|}
decl_stmt|;
name|String
index|[]
name|actualTSDCClass
init|=
operator|new
name|String
index|[]
block|{
literal|"OutOfOrderTopScoreDocCollector"
block|,
literal|"InOrderTopScoreDocCollector"
block|}
decl_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
comment|// Add a Query with SHOULD, since bw.scorer() returns BooleanScorer2
comment|// which delegates to BS if there are no mandatory clauses.
name|bq
operator|.
name|add
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// Set minNrShouldMatch to 1 so that BQ will not optimize rewrite to return
comment|// the clause instead of BQ.
name|bq
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|1
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|inOrder
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
name|tdc
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|3
argument_list|,
name|inOrder
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"org.apache.lucene.search.TopScoreDocCollector$"
operator|+
name|actualTSDCClass
index|[
name|i
index|]
argument_list|,
name|tdc
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|tdc
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|tdc
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|sd
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
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
name|assertEquals
argument_list|(
literal|"expected doc Id "
operator|+
name|j
operator|+
literal|" found "
operator|+
name|sd
index|[
name|j
index|]
operator|.
name|doc
argument_list|,
name|j
argument_list|,
name|sd
index|[
name|j
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
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
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
