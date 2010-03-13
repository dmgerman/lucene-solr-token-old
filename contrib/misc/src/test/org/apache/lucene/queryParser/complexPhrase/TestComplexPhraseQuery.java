begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.complexPhrase
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|complexPhrase
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
name|HashSet
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import
begin_class
DECL|class|TestComplexPhraseQuery
specifier|public
class|class
name|TestComplexPhraseQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|analyzer
name|Analyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
DECL|field|docsContent
name|DocData
name|docsContent
index|[]
init|=
block|{
operator|new
name|DocData
argument_list|(
literal|"john smith"
argument_list|,
literal|"1"
argument_list|)
block|,
operator|new
name|DocData
argument_list|(
literal|"johathon smith"
argument_list|,
literal|"2"
argument_list|)
block|,
operator|new
name|DocData
argument_list|(
literal|"john percival smith"
argument_list|,
literal|"3"
argument_list|)
block|,
operator|new
name|DocData
argument_list|(
literal|"jackson waits tom"
argument_list|,
literal|"4"
argument_list|)
block|}
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|defaultFieldName
name|String
name|defaultFieldName
init|=
literal|"name"
decl_stmt|;
DECL|method|testComplexPhrases
specifier|public
name|void
name|testComplexPhrases
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMatches
argument_list|(
literal|"\"john smith\""
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// Simple multi-term still works
name|checkMatches
argument_list|(
literal|"\"j*   smyth~\""
argument_list|,
literal|"1,2"
argument_list|)
expr_stmt|;
comment|// wildcards and fuzzies are OK in
comment|// phrases
name|checkMatches
argument_list|(
literal|"\"(jo* -john)  smith\""
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
comment|// boolean logic works
name|checkMatches
argument_list|(
literal|"\"jo*  smith\"~2"
argument_list|,
literal|"1,2,3"
argument_list|)
expr_stmt|;
comment|// position logic works.
name|checkMatches
argument_list|(
literal|"\"jo* [sma TO smZ]\" "
argument_list|,
literal|"1,2"
argument_list|)
expr_stmt|;
comment|// range queries supported
name|checkMatches
argument_list|(
literal|"\"john\""
argument_list|,
literal|"1,3"
argument_list|)
expr_stmt|;
comment|// Simple single-term still works
name|checkMatches
argument_list|(
literal|"\"(john OR johathon)  smith\""
argument_list|,
literal|"1,2"
argument_list|)
expr_stmt|;
comment|// boolean logic with
comment|// brackets works.
name|checkMatches
argument_list|(
literal|"\"(jo* -john) smyth~\""
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
comment|// boolean logic with
comment|// brackets works.
comment|// checkMatches("\"john -percival\"", "1"); // not logic doesn't work
comment|// currently :(.
name|checkMatches
argument_list|(
literal|"\"john  nosuchword*\""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// phrases with clauses producing
comment|// empty sets
name|checkBadQuery
argument_list|(
literal|"\"jo*  id:1 smith\""
argument_list|)
expr_stmt|;
comment|// mixing fields in a phrase is bad
name|checkBadQuery
argument_list|(
literal|"\"jo* \"smith\" \""
argument_list|)
expr_stmt|;
comment|// phrases inside phrases is bad
block|}
DECL|method|checkBadQuery
specifier|private
name|void
name|checkBadQuery
parameter_list|(
name|String
name|qString
parameter_list|)
block|{
name|QueryParser
name|qp
init|=
operator|new
name|ComplexPhraseQueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|defaultFieldName
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Throwable
name|expected
init|=
literal|null
decl_stmt|;
try|try
block|{
name|qp
operator|.
name|parse
argument_list|(
name|qString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|expected
operator|=
name|e
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"Expected parse error in "
operator|+
name|qString
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
DECL|method|checkMatches
specifier|private
name|void
name|checkMatches
parameter_list|(
name|String
name|qString
parameter_list|,
name|String
name|expectedVals
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryParser
name|qp
init|=
operator|new
name|ComplexPhraseQueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|defaultFieldName
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|qp
operator|.
name|setFuzzyPrefixLength
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// usually a good idea
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|qString
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|expecteds
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|vals
init|=
name|expectedVals
operator|.
name|split
argument_list|(
literal|","
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
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|vals
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|expecteds
operator|.
name|add
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|td
operator|.
name|scoreDocs
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
name|sd
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
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|qString
operator|+
literal|"matched doc#"
operator|+
name|id
operator|+
literal|" not expected"
argument_list|,
name|expecteds
operator|.
name|contains
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|expecteds
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|qString
operator|+
literal|" missing some matches "
argument_list|,
literal|0
argument_list|,
name|expecteds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|protected
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
name|rd
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
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
name|docsContent
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
literal|"name"
argument_list|,
name|docsContent
index|[
name|i
index|]
operator|.
name|name
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
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|docsContent
index|[
name|i
index|]
operator|.
name|id
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
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|rd
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
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
DECL|class|DocData
specifier|static
class|class
name|DocData
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|method|DocData
specifier|public
name|DocData
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
