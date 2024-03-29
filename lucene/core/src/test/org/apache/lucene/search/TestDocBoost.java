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
name|document
operator|.
name|*
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/** Document boost unit test.  *  *  */
end_comment
begin_class
DECL|class|TestDocBoost
specifier|public
class|class
name|TestDocBoost
extends|extends
name|LuceneTestCase
block|{
DECL|method|testDocBoost
specifier|public
name|void
name|testDocBoost
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|store
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
name|store
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Field
name|f1
init|=
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|"word"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|Field
name|f2
init|=
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|"word"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|f2
operator|.
name|setBoost
argument_list|(
literal|2.0f
argument_list|)
expr_stmt|;
name|Document
name|d1
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Document
name|d2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d1
operator|.
name|add
argument_list|(
name|f1
argument_list|)
expr_stmt|;
comment|// boost = 1
name|d2
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
comment|// boost = 2
name|writer
operator|.
name|addDocument
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d2
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
literal|4
index|]
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"word"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SimpleCollector
argument_list|()
block|{
specifier|private
name|int
name|base
init|=
literal|0
decl_stmt|;
specifier|private
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
specifier|final
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|scores
index|[
name|doc
operator|+
name|base
index|]
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|base
operator|=
name|context
operator|.
name|docBase
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
name|float
name|lastScore
init|=
literal|0.0f
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|searcher
operator|.
name|explain
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"word"
argument_list|)
argument_list|)
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scores
index|[
name|i
index|]
operator|!=
literal|0.0
condition|)
block|{
name|assertTrue
argument_list|(
literal|"score: "
operator|+
name|scores
index|[
name|i
index|]
operator|+
literal|" should be> lastScore: "
operator|+
name|lastScore
argument_list|,
name|scores
index|[
name|i
index|]
operator|>
name|lastScore
argument_list|)
expr_stmt|;
block|}
name|lastScore
operator|=
name|scores
index|[
name|i
index|]
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
