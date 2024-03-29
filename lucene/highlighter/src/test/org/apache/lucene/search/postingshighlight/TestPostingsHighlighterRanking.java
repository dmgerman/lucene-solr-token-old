begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.postingshighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|postingshighlight
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
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|FieldType
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
name|IndexOptions
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
name|BooleanQuery
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
name|BytesRef
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
begin_class
DECL|class|TestPostingsHighlighterRanking
specifier|public
class|class
name|TestPostingsHighlighterRanking
extends|extends
name|LuceneTestCase
block|{
comment|/**     * indexes a bunch of gibberish, and then highlights top(n).    * asserts that top(n) highlights is a subset of top(n+1) up to some max N    */
comment|// TODO: this only tests single-valued fields. we should also index multiple values per field!
DECL|method|testRanking
specifier|public
name|void
name|testRanking
parameter_list|()
throws|throws
name|Exception
block|{
comment|// number of documents: we will check each one
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|// number of top-N snippets, we will check 1 .. N
specifier|final
name|int
name|maxTopN
init|=
literal|5
decl_stmt|;
comment|// maximum number of elements to put in a sentence.
specifier|final
name|int
name|maxSentenceLength
init|=
literal|10
decl_stmt|;
comment|// maximum number of sentences in a document
specifier|final
name|int
name|maxNumSentences
init|=
literal|20
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
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
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
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
name|FieldType
name|offsetsType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|offsetsType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
expr_stmt|;
name|Field
name|body
init|=
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|""
argument_list|,
name|offsetsType
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|body
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|StringBuilder
name|bodyText
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|numSentences
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|maxNumSentences
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numSentences
condition|;
name|j
operator|++
control|)
block|{
name|bodyText
operator|.
name|append
argument_list|(
name|newSentence
argument_list|(
name|random
argument_list|()
argument_list|,
name|maxSentenceLength
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|body
operator|.
name|setStringValue
argument_list|(
name|bodyText
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
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
name|checkDocument
argument_list|(
name|searcher
argument_list|,
name|i
argument_list|,
name|maxTopN
argument_list|)
expr_stmt|;
block|}
name|iw
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
DECL|method|checkDocument
specifier|private
name|void
name|checkDocument
parameter_list|(
name|IndexSearcher
name|is
parameter_list|,
name|int
name|doc
parameter_list|,
name|int
name|maxTopN
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|ch
init|=
literal|'a'
init|;
name|ch
operator|<=
literal|'z'
condition|;
name|ch
operator|++
control|)
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|""
operator|+
operator|(
name|char
operator|)
name|ch
argument_list|)
decl_stmt|;
comment|// check a simple term query
name|checkQuery
argument_list|(
name|is
argument_list|,
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
argument_list|,
name|doc
argument_list|,
name|maxTopN
argument_list|)
expr_stmt|;
comment|// check a boolean query
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Term
name|nextTerm
init|=
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
name|ch
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|nextTerm
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|checkQuery
argument_list|(
name|is
argument_list|,
name|bq
operator|.
name|build
argument_list|()
argument_list|,
name|doc
argument_list|,
name|maxTopN
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkQuery
specifier|private
name|void
name|checkQuery
parameter_list|(
name|IndexSearcher
name|is
parameter_list|,
name|Query
name|query
parameter_list|,
name|int
name|doc
parameter_list|,
name|int
name|maxTopN
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|n
init|=
literal|1
init|;
name|n
operator|<
name|maxTopN
condition|;
name|n
operator|++
control|)
block|{
specifier|final
name|FakePassageFormatter
name|f1
init|=
operator|new
name|FakePassageFormatter
argument_list|()
decl_stmt|;
name|PostingsHighlighter
name|p1
init|=
operator|new
name|PostingsHighlighter
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|PassageFormatter
name|getFormatter
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"body"
argument_list|,
name|field
argument_list|)
expr_stmt|;
return|return
name|f1
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|FakePassageFormatter
name|f2
init|=
operator|new
name|FakePassageFormatter
argument_list|()
decl_stmt|;
name|PostingsHighlighter
name|p2
init|=
operator|new
name|PostingsHighlighter
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|PassageFormatter
name|getFormatter
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"body"
argument_list|,
name|field
argument_list|)
expr_stmt|;
return|return
name|f2
return|;
block|}
block|}
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|query
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
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
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
name|TopDocs
name|td
init|=
name|is
operator|.
name|search
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|p1
operator|.
name|highlight
argument_list|(
literal|"body"
argument_list|,
name|bq
operator|.
name|build
argument_list|()
argument_list|,
name|is
argument_list|,
name|td
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|p2
operator|.
name|highlight
argument_list|(
literal|"body"
argument_list|,
name|bq
operator|.
name|build
argument_list|()
argument_list|,
name|is
argument_list|,
name|td
argument_list|,
name|n
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f2
operator|.
name|seen
operator|.
name|containsAll
argument_list|(
name|f1
operator|.
name|seen
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * returns a new random sentence, up to maxSentenceLength "words" in length.    * each word is a single character (a-z). The first one is capitalized.    */
DECL|method|newSentence
specifier|private
name|String
name|newSentence
parameter_list|(
name|Random
name|r
parameter_list|,
name|int
name|maxSentenceLength
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|numElements
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
name|maxSentenceLength
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
name|numElements
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
name|sb
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// capitalize the first word to help breakiterator
name|sb
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|'A'
argument_list|,
literal|'Z'
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|". "
argument_list|)
expr_stmt|;
comment|// finalize sentence
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**     * a fake formatter that doesn't actually format passages.    * instead it just collects them for asserts!    */
DECL|class|FakePassageFormatter
specifier|static
class|class
name|FakePassageFormatter
extends|extends
name|PassageFormatter
block|{
DECL|field|seen
name|HashSet
argument_list|<
name|Pair
argument_list|>
name|seen
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|Passage
name|passages
index|[]
parameter_list|,
name|String
name|content
parameter_list|)
block|{
for|for
control|(
name|Passage
name|p
range|:
name|passages
control|)
block|{
comment|// verify some basics about the passage
name|assertTrue
argument_list|(
name|p
operator|.
name|getScore
argument_list|()
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|getNumMatches
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|getStartOffset
argument_list|()
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|getStartOffset
argument_list|()
operator|<=
name|content
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|getEndOffset
argument_list|()
operator|>=
name|p
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|getEndOffset
argument_list|()
operator|<=
name|content
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// we use a very simple analyzer. so we can assert the matches are correct
name|int
name|lastMatchStart
init|=
operator|-
literal|1
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
name|p
operator|.
name|getNumMatches
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|BytesRef
name|term
init|=
name|p
operator|.
name|getMatchTerms
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|int
name|matchStart
init|=
name|p
operator|.
name|getMatchStarts
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|matchStart
operator|>=
literal|0
argument_list|)
expr_stmt|;
comment|// must at least start within the passage
name|assertTrue
argument_list|(
name|matchStart
operator|<
name|p
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|matchEnd
init|=
name|p
operator|.
name|getMatchEnds
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|matchEnd
operator|>=
literal|0
argument_list|)
expr_stmt|;
comment|// always moving forward
name|assertTrue
argument_list|(
name|matchStart
operator|>=
name|lastMatchStart
argument_list|)
expr_stmt|;
name|lastMatchStart
operator|=
name|matchStart
expr_stmt|;
comment|// single character terms
name|assertEquals
argument_list|(
name|matchStart
operator|+
literal|1
argument_list|,
name|matchEnd
argument_list|)
expr_stmt|;
comment|// and the offsets must be correct...
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|term
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|char
operator|)
name|term
operator|.
name|bytes
index|[
name|term
operator|.
name|offset
index|]
argument_list|,
name|Character
operator|.
name|toLowerCase
argument_list|(
name|content
operator|.
name|charAt
argument_list|(
name|matchStart
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// record just the start/end offset for simplicity
name|seen
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|(
name|p
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|p
operator|.
name|getEndOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|"bogus!!!!!!"
return|;
block|}
block|}
DECL|class|Pair
specifier|static
class|class
name|Pair
block|{
DECL|field|start
specifier|final
name|int
name|start
decl_stmt|;
DECL|field|end
specifier|final
name|int
name|end
decl_stmt|;
DECL|method|Pair
name|Pair
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|end
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|start
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Pair
name|other
init|=
operator|(
name|Pair
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|end
operator|!=
name|other
operator|.
name|end
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|start
operator|!=
name|other
operator|.
name|start
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Pair [start="
operator|+
name|start
operator|+
literal|", end="
operator|+
name|end
operator|+
literal|"]"
return|;
block|}
block|}
comment|/** sets b=0 to disable passage length normalization */
DECL|method|testCustomB
specifier|public
name|void
name|testCustomB
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
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
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
name|iwc
argument_list|)
decl_stmt|;
name|FieldType
name|offsetsType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|offsetsType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
expr_stmt|;
name|Field
name|body
init|=
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|""
argument_list|,
name|offsetsType
argument_list|)
decl_stmt|;
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
name|body
argument_list|)
expr_stmt|;
name|body
operator|.
name|setStringValue
argument_list|(
literal|"This is a test.  This test is a better test but the sentence is excruiatingly long, "
operator|+
literal|"you have no idea how painful it was for me to type this long sentence into my IDE."
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|PostingsHighlighter
name|highlighter
init|=
operator|new
name|PostingsHighlighter
argument_list|(
literal|10000
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|PassageScorer
name|getScorer
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|PassageScorer
argument_list|(
literal|1.2f
argument_list|,
literal|0
argument_list|,
literal|87
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|,
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|String
name|snippets
index|[]
init|=
name|highlighter
operator|.
name|highlight
argument_list|(
literal|"body"
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|,
name|topDocs
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|snippets
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|snippets
index|[
literal|0
index|]
operator|.
name|startsWith
argument_list|(
literal|"This<b>test</b> is a better<b>test</b>"
argument_list|)
argument_list|)
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
comment|/** sets k1=0 for simple coordinate-level match (# of query terms present) */
DECL|method|testCustomK1
specifier|public
name|void
name|testCustomK1
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
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
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
name|iwc
argument_list|)
decl_stmt|;
name|FieldType
name|offsetsType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|offsetsType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
expr_stmt|;
name|Field
name|body
init|=
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|""
argument_list|,
name|offsetsType
argument_list|)
decl_stmt|;
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
name|body
argument_list|)
expr_stmt|;
name|body
operator|.
name|setStringValue
argument_list|(
literal|"This has only foo foo. "
operator|+
literal|"On the other hand this sentence contains both foo and bar. "
operator|+
literal|"This has only bar bar bar bar bar bar bar bar bar bar bar bar."
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|PostingsHighlighter
name|highlighter
init|=
operator|new
name|PostingsHighlighter
argument_list|(
literal|10000
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|PassageScorer
name|getScorer
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|PassageScorer
argument_list|(
literal|0
argument_list|,
literal|0.75f
argument_list|,
literal|87
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
operator|.
name|build
argument_list|()
argument_list|,
literal|10
argument_list|,
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|String
name|snippets
index|[]
init|=
name|highlighter
operator|.
name|highlight
argument_list|(
literal|"body"
argument_list|,
name|query
operator|.
name|build
argument_list|()
argument_list|,
name|searcher
argument_list|,
name|topDocs
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|snippets
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|snippets
index|[
literal|0
index|]
operator|.
name|startsWith
argument_list|(
literal|"On the other hand"
argument_list|)
argument_list|)
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
block|}
end_class
end_unit
