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
name|BitDocIdSet
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
name|Bits
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
specifier|final
name|Query
name|query
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|query
operator|=
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
expr_stmt|;
block|}
else|else
block|{
comment|// use a query with a two-phase approximation
name|PhraseQuery
name|phrase
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|phrase
operator|.
name|add
argument_list|(
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
argument_list|)
expr_stmt|;
name|phrase
operator|.
name|add
argument_list|(
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
argument_list|)
expr_stmt|;
name|phrase
operator|.
name|setSlop
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|query
operator|=
name|phrase
expr_stmt|;
block|}
comment|// now wrap the query as a filter. QWF has its own codepath
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
return|return
operator|new
name|QueryWrapperFilter
argument_list|(
name|query
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SlowWrapperFilter
argument_list|(
name|query
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|SlowWrapperFilter
specifier|static
class|class
name|SlowWrapperFilter
extends|extends
name|Filter
block|{
DECL|field|query
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|useBits
specifier|final
name|boolean
name|useBits
decl_stmt|;
DECL|method|SlowWrapperFilter
name|SlowWrapperFilter
parameter_list|(
name|Query
name|query
parameter_list|,
name|boolean
name|useBits
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|useBits
operator|=
name|useBits
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|q
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
name|query
condition|)
block|{
return|return
operator|new
name|SlowWrapperFilter
argument_list|(
name|q
argument_list|,
name|useBits
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
comment|// get a private context that is used to rewrite, createWeight and score eventually
specifier|final
name|LeafReaderContext
name|privateContext
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getContext
argument_list|()
decl_stmt|;
specifier|final
name|Weight
name|weight
init|=
operator|new
name|IndexSearcher
argument_list|(
name|privateContext
argument_list|)
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocIdSet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|scorer
argument_list|(
name|privateContext
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
annotation|@
name|Override
specifier|public
name|Bits
name|bits
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|useBits
condition|)
block|{
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|DocIdSetIterator
name|disi
init|=
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|disi
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|or
argument_list|(
name|disi
argument_list|)
expr_stmt|;
block|}
name|BitDocIdSet
name|bitset
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitset
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Bits
operator|.
name|MatchNoBits
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|bitset
operator|.
name|bits
argument_list|()
return|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"SlowQWF("
operator|+
name|query
operator|+
literal|")"
return|;
block|}
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
comment|// test with some filters (this will sometimes cause advance'ing enough to test it)
name|int
name|numFilters
init|=
name|atLeast
argument_list|(
literal|10
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
name|numFilters
condition|;
name|i
operator|++
control|)
block|{
name|Filter
name|filter
init|=
name|randomFilter
argument_list|()
decl_stmt|;
comment|// incorporate the filter in different ways.
name|assertSubsetOf
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|,
name|filter
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|filteredQuery
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|)
argument_list|,
name|filteredQuery
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|filteredQuery
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|)
argument_list|,
name|filteredBooleanQuery
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|filteredBooleanQuery
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|)
argument_list|,
name|filteredBooleanQuery
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertSubsetOf
argument_list|(
name|filteredBooleanQuery
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|)
argument_list|,
name|filteredQuery
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
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
name|QueryUtils
operator|.
name|check
argument_list|(
name|q1
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|q2
argument_list|)
expr_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
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
argument_list|)
expr_stmt|;
block|}
comment|// we test both INDEXORDER and RELEVANCE because we want to test needsScores=true/false
for|for
control|(
name|Sort
name|sort
range|:
operator|new
name|Sort
index|[]
block|{
name|Sort
operator|.
name|INDEXORDER
block|,
name|Sort
operator|.
name|RELEVANCE
block|}
control|)
block|{
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
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|sort
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
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|sort
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"too many hits: "
operator|+
name|td1
operator|.
name|totalHits
operator|+
literal|"> "
operator|+
name|td2
operator|.
name|totalHits
argument_list|,
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
comment|// also test with some filters to test advancing
name|int
name|numFilters
init|=
name|atLeast
argument_list|(
literal|10
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
name|numFilters
condition|;
name|i
operator|++
control|)
block|{
name|Filter
name|filter
init|=
name|randomFilter
argument_list|()
decl_stmt|;
comment|// incorporate the filter in different ways.
name|assertSameScores
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|,
name|filter
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|filteredQuery
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|)
argument_list|,
name|filteredQuery
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|filteredQuery
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|)
argument_list|,
name|filteredBooleanQuery
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|filteredBooleanQuery
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|)
argument_list|,
name|filteredBooleanQuery
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertSameScores
argument_list|(
name|filteredBooleanQuery
argument_list|(
name|q1
argument_list|,
name|filter
argument_list|)
argument_list|,
name|filteredQuery
argument_list|(
name|q2
argument_list|,
name|filter
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
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
comment|// not efficient, but simple!
if|if
condition|(
name|filter
operator|!=
literal|null
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
argument_list|)
expr_stmt|;
block|}
name|TopDocs
name|td1
init|=
name|s1
operator|.
name|search
argument_list|(
name|q1
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
DECL|method|filteredQuery
specifier|protected
name|Query
name|filteredQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
return|return
operator|new
name|FilteredQuery
argument_list|(
name|query
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
return|;
block|}
DECL|method|filteredBooleanQuery
specifier|protected
name|Query
name|filteredBooleanQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
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
name|query
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|filter
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
return|return
name|bq
return|;
block|}
block|}
end_class
end_unit
