begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|query
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|BaseTokenStreamTestCase
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
name|TokenStream
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
name|core
operator|.
name|LetterTokenizer
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
name|core
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
name|core
operator|.
name|WhitespaceTokenizer
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
name|store
operator|.
name|RAMDirectory
import|;
end_import
begin_class
DECL|class|QueryAutoStopWordAnalyzerTest
specifier|public
class|class
name|QueryAutoStopWordAnalyzerTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|variedFieldValues
name|String
name|variedFieldValues
index|[]
init|=
block|{
literal|"the"
block|,
literal|"quick"
block|,
literal|"brown"
block|,
literal|"fox"
block|,
literal|"jumped"
block|,
literal|"over"
block|,
literal|"the"
block|,
literal|"lazy"
block|,
literal|"boring"
block|,
literal|"dog"
block|}
decl_stmt|;
DECL|field|repetitiveFieldValues
name|String
name|repetitiveFieldValues
index|[]
init|=
block|{
literal|"boring"
block|,
literal|"boring"
block|,
literal|"vaguelyboring"
block|}
decl_stmt|;
DECL|field|dir
name|RAMDirectory
name|dir
decl_stmt|;
DECL|field|appAnalyzer
name|Analyzer
name|appAnalyzer
decl_stmt|;
DECL|field|reader
name|IndexReader
name|reader
decl_stmt|;
DECL|field|protectedAnalyzer
name|QueryAutoStopWordAnalyzer
name|protectedAnalyzer
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
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|appAnalyzer
operator|=
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
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
name|TEST_VERSION_CURRENT
argument_list|,
name|appAnalyzer
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
literal|200
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
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|variedFieldValue
init|=
name|variedFieldValues
index|[
name|i
operator|%
name|variedFieldValues
operator|.
name|length
index|]
decl_stmt|;
name|String
name|repetitiveFieldValue
init|=
name|repetitiveFieldValues
index|[
name|i
operator|%
name|repetitiveFieldValues
operator|.
name|length
index|]
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"variedField"
argument_list|,
name|variedFieldValue
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
literal|"repetitiveField"
argument_list|,
name|repetitiveFieldValue
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
name|reader
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|protectedAnalyzer
operator|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|appAnalyzer
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|//Helper method to query
DECL|method|search
specifier|private
name|int
name|search
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|queryString
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"repetitiveField"
argument_list|,
name|a
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|qp
operator|.
name|parse
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|int
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|totalHits
decl_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|hits
return|;
block|}
DECL|method|testUninitializedAnalyzer
specifier|public
name|void
name|testUninitializedAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Note: no calls to "addStopWord"
name|String
name|query
init|=
literal|"variedField:quick repetitiveField:boring"
decl_stmt|;
name|int
name|numHits1
init|=
name|search
argument_list|(
name|protectedAnalyzer
argument_list|,
name|query
argument_list|)
decl_stmt|;
name|int
name|numHits2
init|=
name|search
argument_list|(
name|appAnalyzer
argument_list|,
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"No filtering test"
argument_list|,
name|numHits1
argument_list|,
name|numHits2
argument_list|)
expr_stmt|;
block|}
comment|/*     * Test method for 'org.apache.lucene.analysis.QueryAutoStopWordAnalyzer.addStopWords(IndexReader)'     */
DECL|method|testDefaultAddStopWordsIndexReader
specifier|public
name|void
name|testDefaultAddStopWordsIndexReader
parameter_list|()
throws|throws
name|Exception
block|{
name|protectedAnalyzer
operator|.
name|addStopWords
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|int
name|numHits
init|=
name|search
argument_list|(
name|protectedAnalyzer
argument_list|,
literal|"repetitiveField:boring"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Default filter should remove all docs"
argument_list|,
literal|0
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
comment|/*     * Test method for 'org.apache.lucene.analysis.QueryAutoStopWordAnalyzer.addStopWords(IndexReader, int)'     */
DECL|method|testAddStopWordsIndexReaderInt
specifier|public
name|void
name|testAddStopWordsIndexReaderInt
parameter_list|()
throws|throws
name|Exception
block|{
name|protectedAnalyzer
operator|.
name|addStopWords
argument_list|(
name|reader
argument_list|,
literal|1f
operator|/
literal|2f
argument_list|)
expr_stmt|;
name|int
name|numHits
init|=
name|search
argument_list|(
name|protectedAnalyzer
argument_list|,
literal|"repetitiveField:boring"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"A filter on terms in> one half of docs remove boring docs"
argument_list|,
literal|0
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
name|numHits
operator|=
name|search
argument_list|(
name|protectedAnalyzer
argument_list|,
literal|"repetitiveField:vaguelyboring"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"A filter on terms in> half of docs should not remove vaguelyBoring docs"
argument_list|,
name|numHits
operator|>
literal|1
argument_list|)
expr_stmt|;
name|protectedAnalyzer
operator|.
name|addStopWords
argument_list|(
name|reader
argument_list|,
literal|1f
operator|/
literal|4f
argument_list|)
expr_stmt|;
name|numHits
operator|=
name|search
argument_list|(
name|protectedAnalyzer
argument_list|,
literal|"repetitiveField:vaguelyboring"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A filter on terms in> quarter of docs should remove vaguelyBoring docs"
argument_list|,
literal|0
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddStopWordsIndexReaderStringFloat
specifier|public
name|void
name|testAddStopWordsIndexReaderStringFloat
parameter_list|()
throws|throws
name|Exception
block|{
name|protectedAnalyzer
operator|.
name|addStopWords
argument_list|(
name|reader
argument_list|,
literal|"variedField"
argument_list|,
literal|1f
operator|/
literal|2f
argument_list|)
expr_stmt|;
name|int
name|numHits
init|=
name|search
argument_list|(
name|protectedAnalyzer
argument_list|,
literal|"repetitiveField:boring"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"A filter on one Field should not affect queris on another"
argument_list|,
name|numHits
operator|>
literal|0
argument_list|)
expr_stmt|;
name|protectedAnalyzer
operator|.
name|addStopWords
argument_list|(
name|reader
argument_list|,
literal|"repetitiveField"
argument_list|,
literal|1f
operator|/
literal|2f
argument_list|)
expr_stmt|;
name|numHits
operator|=
name|search
argument_list|(
name|protectedAnalyzer
argument_list|,
literal|"repetitiveField:boring"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A filter on the right Field should affect queries on it"
argument_list|,
name|numHits
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddStopWordsIndexReaderStringInt
specifier|public
name|void
name|testAddStopWordsIndexReaderStringInt
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numStopWords
init|=
name|protectedAnalyzer
operator|.
name|addStopWords
argument_list|(
name|reader
argument_list|,
literal|"repetitiveField"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have identified stop words"
argument_list|,
name|numStopWords
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Term
index|[]
name|t
init|=
name|protectedAnalyzer
operator|.
name|getStopWords
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"num terms should = num stopwords returned"
argument_list|,
name|t
operator|.
name|length
argument_list|,
name|numStopWords
argument_list|)
expr_stmt|;
name|int
name|numNewStopWords
init|=
name|protectedAnalyzer
operator|.
name|addStopWords
argument_list|(
name|reader
argument_list|,
literal|"variedField"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have identified more stop words"
argument_list|,
name|numNewStopWords
operator|>
literal|0
argument_list|)
expr_stmt|;
name|t
operator|=
name|protectedAnalyzer
operator|.
name|getStopWords
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"num terms should = num stopwords returned"
argument_list|,
name|t
operator|.
name|length
argument_list|,
name|numStopWords
operator|+
name|numNewStopWords
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoFieldNamePollution
specifier|public
name|void
name|testNoFieldNamePollution
parameter_list|()
throws|throws
name|Exception
block|{
name|protectedAnalyzer
operator|.
name|addStopWords
argument_list|(
name|reader
argument_list|,
literal|"repetitiveField"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|int
name|numHits
init|=
name|search
argument_list|(
name|protectedAnalyzer
argument_list|,
literal|"repetitiveField:boring"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Check filter set up OK"
argument_list|,
literal|0
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
name|numHits
operator|=
name|search
argument_list|(
name|protectedAnalyzer
argument_list|,
literal|"variedField:boring"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Filter should not prevent stopwords in one field being used in another "
argument_list|,
name|numHits
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/*    * analyzer that does not support reuse    * it is LetterTokenizer on odd invocations, WhitespaceTokenizer on even.    */
DECL|class|NonreusableAnalyzer
specifier|private
class|class
name|NonreusableAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|invocationCount
name|int
name|invocationCount
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
if|if
condition|(
operator|++
name|invocationCount
operator|%
literal|2
operator|==
literal|0
condition|)
return|return
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
return|;
else|else
return|return
operator|new
name|LetterTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
return|;
block|}
block|}
DECL|method|testWrappingNonReusableAnalyzer
specifier|public
name|void
name|testWrappingNonReusableAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
name|QueryAutoStopWordAnalyzer
name|a
init|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|NonreusableAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|a
operator|.
name|addStopWords
argument_list|(
name|reader
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|int
name|numHits
init|=
name|search
argument_list|(
name|a
argument_list|,
literal|"repetitiveField:boring"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|numHits
operator|==
literal|0
argument_list|)
expr_stmt|;
name|numHits
operator|=
name|search
argument_list|(
name|a
argument_list|,
literal|"repetitiveField:vaguelyboring"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numHits
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testTokenStream
specifier|public
name|void
name|testTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|QueryAutoStopWordAnalyzer
name|a
init|=
operator|new
name|QueryAutoStopWordAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|)
decl_stmt|;
name|a
operator|.
name|addStopWords
argument_list|(
name|reader
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|TokenStream
name|ts
init|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"repetitiveField"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"this boring"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
