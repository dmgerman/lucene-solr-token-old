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
name|queryParser
operator|.
name|ParseException
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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_comment
comment|/** Test BooleanQuery2 against BooleanQuery by overriding the standard query parser.  * This also tests the scoring order of BooleanQuery.  */
end_comment
begin_class
DECL|class|TestBoolean2
specifier|public
class|class
name|TestBoolean2
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|field
specifier|public
specifier|static
specifier|final
name|String
name|field
init|=
literal|"field"
decl_stmt|;
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
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
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
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
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
name|docFields
operator|.
name|length
condition|;
name|i
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|docFields
index|[
name|i
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
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
DECL|field|docFields
specifier|private
name|String
index|[]
name|docFields
init|=
block|{
literal|"w1 w2 w3 w4 w5"
block|,
literal|"w1 w3 w2 w3"
block|,
literal|"w1 xx w2 yy w3"
block|,
literal|"w1 w3 xx w2 yy w3"
block|}
decl_stmt|;
DECL|method|makeQuery
specifier|public
name|Query
name|makeQuery
parameter_list|(
name|String
name|queryText
parameter_list|)
throws|throws
name|ParseException
block|{
name|Query
name|q
init|=
operator|(
operator|new
name|QueryParser
argument_list|(
name|field
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
operator|)
operator|.
name|parse
argument_list|(
name|queryText
argument_list|)
decl_stmt|;
return|return
name|q
return|;
block|}
DECL|method|queriesTest
specifier|public
name|void
name|queriesTest
parameter_list|(
name|String
name|queryText
parameter_list|,
name|int
index|[]
name|expDocNrs
parameter_list|)
throws|throws
name|Exception
block|{
comment|//System.out.println();
comment|//System.out.println("Query: " + queryText);
try|try
block|{
name|Query
name|query1
init|=
name|makeQuery
argument_list|(
name|queryText
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|setAllowDocsOutOfOrder
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits1
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query1
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|Query
name|query2
init|=
name|makeQuery
argument_list|(
name|queryText
argument_list|)
decl_stmt|;
comment|// there should be no need to parse again...
name|BooleanQuery
operator|.
name|setAllowDocsOutOfOrder
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits2
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query2
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|CheckHits
operator|.
name|checkHitsQuery
argument_list|(
name|query2
argument_list|,
name|hits1
argument_list|,
name|hits2
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// even when a test fails.
name|BooleanQuery
operator|.
name|setAllowDocsOutOfOrder
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testQueries01
specifier|public
name|void
name|testQueries01
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryText
init|=
literal|"+w3 +xx"
decl_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|queryText
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueries02
specifier|public
name|void
name|testQueries02
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryText
init|=
literal|"+w3 xx"
decl_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|,
literal|1
block|,
literal|0
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|queryText
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueries03
specifier|public
name|void
name|testQueries03
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryText
init|=
literal|"w3 xx"
decl_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|,
literal|1
block|,
literal|0
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|queryText
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueries04
specifier|public
name|void
name|testQueries04
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryText
init|=
literal|"w3 -xx"
decl_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|1
block|,
literal|0
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|queryText
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueries05
specifier|public
name|void
name|testQueries05
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryText
init|=
literal|"+w3 -xx"
decl_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|1
block|,
literal|0
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|queryText
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueries06
specifier|public
name|void
name|testQueries06
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryText
init|=
literal|"+w3 -xx -w5"
decl_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|1
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|queryText
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueries07
specifier|public
name|void
name|testQueries07
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryText
init|=
literal|"-w3 -xx -w5"
decl_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{}
decl_stmt|;
name|queriesTest
argument_list|(
name|queryText
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueries08
specifier|public
name|void
name|testQueries08
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryText
init|=
literal|"+w3 xx -w5"
decl_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|,
literal|1
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|queryText
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueries09
specifier|public
name|void
name|testQueries09
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryText
init|=
literal|"+w3 +xx +w2 zz"
decl_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|}
decl_stmt|;
name|queriesTest
argument_list|(
name|queryText
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
DECL|method|testQueries10
specifier|public
name|void
name|testQueries10
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryText
init|=
literal|"+w3 +xx +w2 zz"
decl_stmt|;
name|int
index|[]
name|expDocNrs
init|=
block|{
literal|2
block|,
literal|3
block|}
decl_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
block|{
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
name|overlap
operator|/
operator|(
operator|(
name|float
operator|)
name|maxOverlap
operator|-
literal|1
operator|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|queriesTest
argument_list|(
name|queryText
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomQueries
specifier|public
name|void
name|testRandomQueries
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|rnd
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|String
index|[]
name|vals
init|=
block|{
literal|"w1"
block|,
literal|"w2"
block|,
literal|"w3"
block|,
literal|"w4"
block|,
literal|"w5"
block|,
literal|"xx"
block|,
literal|"yy"
block|,
literal|"zzz"
block|}
decl_stmt|;
name|int
name|tot
init|=
literal|0
decl_stmt|;
try|try
block|{
comment|// increase number of iterations for more complete testing
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|int
name|level
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|BooleanQuery
name|q1
init|=
name|randBoolQuery
argument_list|(
operator|new
name|Random
argument_list|(
name|rnd
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|level
argument_list|,
name|field
argument_list|,
name|vals
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Can't sort by relevance since floating point numbers may not quite
comment|// match up.
name|Sort
name|sort
init|=
name|Sort
operator|.
name|INDEXORDER
decl_stmt|;
name|BooleanQuery
operator|.
name|setAllowDocsOutOfOrder
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|q1
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits1
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q1
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|,
name|sort
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|BooleanQuery
operator|.
name|setAllowDocsOutOfOrder
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits2
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q1
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|,
name|sort
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|tot
operator|+=
name|hits2
operator|.
name|length
expr_stmt|;
name|CheckHits
operator|.
name|checkEqual
argument_list|(
name|q1
argument_list|,
name|hits1
argument_list|,
name|hits2
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// even when a test fails.
name|BooleanQuery
operator|.
name|setAllowDocsOutOfOrder
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// System.out.println("Total hits:"+tot);
block|}
comment|// used to set properties or change every BooleanQuery
comment|// generated from randBoolQuery.
DECL|interface|Callback
specifier|public
specifier|static
interface|interface
name|Callback
block|{
DECL|method|postCreate
specifier|public
name|void
name|postCreate
parameter_list|(
name|BooleanQuery
name|q
parameter_list|)
function_decl|;
block|}
comment|// Random rnd is passed in so that the exact same random query may be created
comment|// more than once.
DECL|method|randBoolQuery
specifier|public
specifier|static
name|BooleanQuery
name|randBoolQuery
parameter_list|(
name|Random
name|rnd
parameter_list|,
name|int
name|level
parameter_list|,
name|String
name|field
parameter_list|,
name|String
index|[]
name|vals
parameter_list|,
name|Callback
name|cb
parameter_list|)
block|{
name|BooleanQuery
name|current
init|=
operator|new
name|BooleanQuery
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|()
operator|<
literal|0
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
name|rnd
operator|.
name|nextInt
argument_list|(
name|vals
operator|.
name|length
argument_list|)
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|int
name|qType
init|=
literal|0
decl_stmt|;
comment|// term query
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|qType
operator|=
name|rnd
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|Query
name|q
decl_stmt|;
if|if
condition|(
name|qType
operator|<
literal|7
condition|)
name|q
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|vals
index|[
name|rnd
operator|.
name|nextInt
argument_list|(
name|vals
operator|.
name|length
argument_list|)
index|]
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|q
operator|=
name|randBoolQuery
argument_list|(
name|rnd
argument_list|,
name|level
operator|-
literal|1
argument_list|,
name|field
argument_list|,
name|vals
argument_list|,
name|cb
argument_list|)
expr_stmt|;
name|int
name|r
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|BooleanClause
operator|.
name|Occur
name|occur
decl_stmt|;
if|if
condition|(
name|r
operator|<
literal|2
condition|)
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
expr_stmt|;
elseif|else
if|if
condition|(
name|r
operator|<
literal|5
condition|)
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
expr_stmt|;
else|else
name|occur
operator|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
expr_stmt|;
name|current
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|occur
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cb
operator|!=
literal|null
condition|)
name|cb
operator|.
name|postCreate
argument_list|(
name|current
argument_list|)
expr_stmt|;
return|return
name|current
return|;
block|}
block|}
end_class
end_unit
