begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Analyzer
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
name|queryParser
operator|.
name|QueryParser
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
comment|/**  * Tests MatchAllDocsQuery.  *  */
end_comment
begin_class
DECL|class|TestMatchAllDocsQuery
specifier|public
class|class
name|TestMatchAllDocsQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|()
decl_stmt|;
DECL|method|testQuery
specifier|public
name|void
name|testQuery
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
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|addDoc
argument_list|(
literal|"one"
argument_list|,
name|iw
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
literal|"two"
argument_list|,
name|iw
argument_list|,
literal|20f
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
literal|"three four"
argument_list|,
name|iw
argument_list|,
literal|300f
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
decl_stmt|;
comment|// assert with norms scoring turned off
name|hits
operator|=
name|is
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one"
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"two"
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|hits
index|[
literal|1
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"three four"
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|hits
index|[
literal|2
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
comment|// assert with norms scoring turned on
name|MatchAllDocsQuery
name|normsQuery
init|=
operator|new
name|MatchAllDocsQuery
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
name|hits
operator|=
name|is
operator|.
name|search
argument_list|(
name|normsQuery
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"three four"
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"two"
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|hits
index|[
literal|1
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one"
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|hits
index|[
literal|2
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
comment|// change norm& retest
name|ir
operator|.
name|setNorm
argument_list|(
literal|0
argument_list|,
literal|"key"
argument_list|,
name|is
operator|.
name|getSimilarityProvider
argument_list|()
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
operator|.
name|encodeNormValue
argument_list|(
literal|400f
argument_list|)
argument_list|)
expr_stmt|;
name|normsQuery
operator|=
operator|new
name|MatchAllDocsQuery
argument_list|(
literal|"key"
argument_list|)
expr_stmt|;
name|hits
operator|=
name|is
operator|.
name|search
argument_list|(
name|normsQuery
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one"
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"three four"
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|hits
index|[
literal|1
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"two"
argument_list|,
name|ir
operator|.
name|document
argument_list|(
name|hits
index|[
literal|2
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
comment|// some artificial queries to trigger the use of skipTo():
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
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|hits
operator|=
name|is
operator|.
name|search
argument_list|(
name|bq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|bq
operator|=
operator|new
name|BooleanQuery
argument_list|()
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
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
literal|"key"
argument_list|,
literal|"three"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|hits
operator|=
name|is
operator|.
name|search
argument_list|(
name|bq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// delete a document:
name|ir
operator|.
name|deleteDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|hits
operator|=
name|is
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test parsable toString()
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"key"
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|hits
operator|=
name|is
operator|.
name|search
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// test parsable toString() with non default boost
name|Query
name|maq
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|maq
operator|.
name|setBoost
argument_list|(
literal|2.3f
argument_list|)
expr_stmt|;
name|Query
name|pq
init|=
name|qp
operator|.
name|parse
argument_list|(
name|maq
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|hits
operator|=
name|is
operator|.
name|search
argument_list|(
name|pq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
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
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|Query
name|q1
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|Query
name|q2
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|q1
operator|.
name|equals
argument_list|(
name|q2
argument_list|)
argument_list|)
expr_stmt|;
name|q1
operator|.
name|setBoost
argument_list|(
literal|1.5f
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|q1
operator|.
name|equals
argument_list|(
name|q2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|String
name|text
parameter_list|,
name|IndexWriter
name|iw
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|f
init|=
name|newField
argument_list|(
literal|"key"
argument_list|,
name|text
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
name|f
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
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
block|}
end_class
end_unit
