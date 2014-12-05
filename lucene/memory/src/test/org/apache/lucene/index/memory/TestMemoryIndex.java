begin_unit
begin_package
DECL|package|org.apache.lucene.index.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|memory
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|index
operator|.
name|LeafReader
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
name|FieldInvertState
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
name|similarities
operator|.
name|BM25Similarity
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
name|similarities
operator|.
name|DefaultSimilarity
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
name|Before
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|not
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|internal
operator|.
name|matchers
operator|.
name|StringContains
operator|.
name|containsString
import|;
end_import
begin_class
DECL|class|TestMemoryIndex
specifier|public
class|class
name|TestMemoryIndex
extends|extends
name|LuceneTestCase
block|{
DECL|field|analyzer
specifier|private
name|MockAnalyzer
name|analyzer
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|analyzer
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|setEnableChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// MemoryIndex can close a TokenStream on init error
block|}
annotation|@
name|Test
DECL|method|testFreezeAPI
specifier|public
name|void
name|testFreezeAPI
parameter_list|()
block|{
name|MemoryIndex
name|mi
init|=
operator|new
name|MemoryIndex
argument_list|()
decl_stmt|;
name|mi
operator|.
name|addField
argument_list|(
literal|"f1"
argument_list|,
literal|"some text"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mi
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
argument_list|,
name|not
argument_list|(
name|is
argument_list|(
literal|0.0f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mi
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f1"
argument_list|,
literal|"some"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|not
argument_list|(
name|is
argument_list|(
literal|0.0f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// check we can add a new field after searching
name|mi
operator|.
name|addField
argument_list|(
literal|"f2"
argument_list|,
literal|"some more text"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mi
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f2"
argument_list|,
literal|"some"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|not
argument_list|(
name|is
argument_list|(
literal|0.0f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// freeze!
name|mi
operator|.
name|freeze
argument_list|()
expr_stmt|;
try|try
block|{
name|mi
operator|.
name|addField
argument_list|(
literal|"f3"
argument_list|,
literal|"and yet more"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an IllegalArgumentException when adding a field after calling freeze()"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"frozen"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|mi
operator|.
name|setSimilarity
argument_list|(
operator|new
name|BM25Similarity
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected an IllegalArgumentException when setting the Similarity after calling freeze()"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"frozen"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|mi
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f1"
argument_list|,
literal|"some"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|not
argument_list|(
name|is
argument_list|(
literal|0.0f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mi
operator|.
name|reset
argument_list|()
expr_stmt|;
name|mi
operator|.
name|addField
argument_list|(
literal|"f1"
argument_list|,
literal|"wibble"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mi
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f1"
argument_list|,
literal|"some"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|0.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mi
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f1"
argument_list|,
literal|"wibble"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|not
argument_list|(
name|is
argument_list|(
literal|0.0f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// check we can set the Similarity again
name|mi
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimilarities
specifier|public
name|void
name|testSimilarities
parameter_list|()
throws|throws
name|IOException
block|{
name|MemoryIndex
name|mi
init|=
operator|new
name|MemoryIndex
argument_list|()
decl_stmt|;
name|mi
operator|.
name|addField
argument_list|(
literal|"f1"
argument_list|,
literal|"a long text field that contains many many terms"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|mi
operator|.
name|createSearcher
argument_list|()
decl_stmt|;
name|LeafReader
name|reader
init|=
operator|(
name|LeafReader
operator|)
name|searcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|float
name|n1
init|=
name|reader
operator|.
name|getNormValues
argument_list|(
literal|"f1"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Norms are re-computed when we change the Similarity
name|mi
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|lengthNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
literal|74
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|float
name|n2
init|=
name|reader
operator|.
name|getNormValues
argument_list|(
literal|"f1"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|n1
operator|!=
name|n2
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
