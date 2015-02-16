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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|BitSet
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
name|analysis
operator|.
name|MockTokenizer
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
name|document
operator|.
name|TextField
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
name|TestUtil
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
name|automaton
operator|.
name|Automata
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
name|automaton
operator|.
name|CharacterRunAutomaton
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
comment|/**  * Simple base class for checking search equivalence.  * Extend it, and write tests that create {@link #randomTerm()}s  * (all terms are single characters a-z), and use   * {@link #assertSameSet(Query, Query)} and   * {@link #assertSubsetOf(Query, Query)}  */
end_comment
begin_class
DECL|class|SearchEquivalenceTestBase
specifier|public
specifier|abstract
class|class
name|SearchEquivalenceTestBase
extends|extends
name|LuceneTestCase
block|{
DECL|field|s1
DECL|field|s2
specifier|protected
specifier|static
name|IndexSearcher
name|s1
decl_stmt|,
name|s2
decl_stmt|;
DECL|field|directory
specifier|protected
specifier|static
name|Directory
name|directory
decl_stmt|;
DECL|field|reader
specifier|protected
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|analyzer
specifier|protected
specifier|static
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|stopword
specifier|protected
specifier|static
name|String
name|stopword
decl_stmt|;
comment|// we always pick a character as a stopword
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
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|stopword
operator|=
literal|""
operator|+
name|randomChar
argument_list|()
expr_stmt|;
name|CharacterRunAutomaton
name|stopset
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
name|stopword
argument_list|)
argument_list|)
decl_stmt|;
name|analyzer
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|,
name|stopset
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|directory
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|id
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
name|Field
name|field
init|=
operator|new
name|TextField
argument_list|(
literal|"field"
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
name|doc
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
comment|// index some docs
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|1000
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
name|id
operator|.
name|setStringValue
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
name|randomFieldContents
argument_list|()
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
comment|// delete some docs
name|int
name|numDeletes
init|=
name|numDocs
operator|/
literal|20
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
name|numDeletes
condition|;
name|i
operator|++
control|)
block|{
name|Term
name|toDelete
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|numDocs
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|iw
operator|.
name|deleteDocuments
argument_list|(
name|toDelete
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|iw
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|toDelete
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|s1
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|s2
operator|=
name|newSearcher
argument_list|(
name|reader
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
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
name|analyzer
operator|=
literal|null
expr_stmt|;
name|s1
operator|=
name|s2
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * populate a field with random contents.    * terms should be single characters in lowercase (a-z)    * tokenization can be assumed to be on whitespace.    */
DECL|method|randomFieldContents
specifier|static
name|String
name|randomFieldContents
parameter_list|()
block|{
comment|// TODO: zipf-like distribution
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|numTerms
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|15
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
name|numTerms
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
comment|// whitespace
block|}
name|sb
operator|.
name|append
argument_list|(
name|randomChar
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * returns random character (a-z)    */
DECL|method|randomChar
specifier|static
name|char
name|randomChar
parameter_list|()
block|{
return|return
operator|(
name|char
operator|)
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
return|;
block|}
comment|/**    * returns a term suitable for searching.    * terms are single characters in lowercase (a-z)    */
DECL|method|randomTerm
specifier|protected
name|Term
name|randomTerm
parameter_list|()
block|{
return|return
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|""
operator|+
name|randomChar
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns a random filter over the document set    */
DECL|method|randomFilter
specifier|protected
name|Filter
name|randomFilter
parameter_list|()
block|{
return|return
operator|new
name|QueryWrapperFilter
argument_list|(
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"field"
argument_list|,
literal|"a"
argument_list|,
literal|""
operator|+
name|randomChar
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Asserts that the documents returned by<code>q1</code>    * are the same as of those returned by<code>q2</code>    */
DECL|method|assertSameSet
specifier|public
name|void
name|assertSameSet
parameter_list|(
name|Query
name|q1
parameter_list|,
name|Query
name|q2
parameter_list|)
throws|throws
name|Exception
block|{
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|q2
argument_list|,
name|q1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Asserts that the documents returned by<code>q1</code>    * are a subset of those returned by<code>q2</code>    */
DECL|method|assertSubsetOf
specifier|public
name|void
name|assertSubsetOf
parameter_list|(
name|Query
name|q1
parameter_list|,
name|Query
name|q2
parameter_list|)
throws|throws
name|Exception
block|{
comment|// test without a filter
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// test with a filter (this will sometimes cause advance'ing enough to test it)
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|,
name|randomFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Asserts that the documents returned by<code>q1</code>    * are a subset of those returned by<code>q2</code>.    *     * Both queries will be filtered by<code>filter</code>    */
DECL|method|assertSubsetOf
specifier|protected
name|void
name|assertSubsetOf
parameter_list|(
name|Query
name|q1
parameter_list|,
name|Query
name|q2
parameter_list|,
name|Filter
name|filter
parameter_list|)
throws|throws
name|Exception
block|{
comment|// TRUNK ONLY: test both filter code paths
if|if
condition|(
name|filter
operator|!=
literal|null
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|q1
operator|=
operator|new
name|FilteredQuery
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|,
name|TestUtil
operator|.
name|randomFilterStrategy
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|q2
operator|=
operator|new
name|FilteredQuery
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|,
name|TestUtil
operator|.
name|randomFilterStrategy
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
literal|null
expr_stmt|;
block|}
comment|// not efficient, but simple!
name|TopDocs
name|td1
init|=
name|s1
operator|.
name|search
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|Sort
operator|.
name|INDEXORDER
else|:
name|Sort
operator|.
name|RELEVANCE
argument_list|)
decl_stmt|;
name|TopDocs
name|td2
init|=
name|s2
operator|.
name|search
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|Sort
operator|.
name|INDEXORDER
else|:
name|Sort
operator|.
name|RELEVANCE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|td1
operator|.
name|totalHits
operator|<=
name|td2
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// fill the superset into a bitset
name|BitSet
name|bitset
init|=
operator|new
name|BitSet
argument_list|()
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
name|td2
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bitset
operator|.
name|set
argument_list|(
name|td2
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// check in the subset, that every bit was set by the super
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|td1
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|bitset
operator|.
name|get
argument_list|(
name|td1
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Assert that two queries return the same documents and with the same scores.    */
DECL|method|assertSameScores
specifier|protected
name|void
name|assertSameScores
parameter_list|(
name|Query
name|q1
parameter_list|,
name|Query
name|q2
parameter_list|)
throws|throws
name|Exception
block|{
name|assertSameSet
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// also test with a filter to test advancing
name|assertSameScores
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|,
name|randomFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSameScores
specifier|protected
name|void
name|assertSameScores
parameter_list|(
name|Query
name|q1
parameter_list|,
name|Query
name|q2
parameter_list|,
name|Filter
name|filter
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|filter
operator|!=
literal|null
operator|&&
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|q1
operator|=
operator|new
name|FilteredQuery
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|,
name|TestUtil
operator|.
name|randomFilterStrategy
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|q2
operator|=
operator|new
name|FilteredQuery
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|,
name|TestUtil
operator|.
name|randomFilterStrategy
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
literal|null
expr_stmt|;
block|}
comment|// not efficient, but simple!
name|TopDocs
name|td1
init|=
name|s1
operator|.
name|search
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|TopDocs
name|td2
init|=
name|s2
operator|.
name|search
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|td1
operator|.
name|totalHits
argument_list|,
name|td2
operator|.
name|totalHits
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
name|td1
operator|.
name|scoreDocs
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|td1
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|,
name|td2
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|td1
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|,
name|td2
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|,
literal|10e-5
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
