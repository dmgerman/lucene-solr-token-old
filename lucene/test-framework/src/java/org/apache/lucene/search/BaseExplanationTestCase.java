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
name|document
operator|.
name|SortedDocValuesField
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
name|search
operator|.
name|spans
operator|.
name|SpanQuery
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
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
operator|.
name|SpanTestUtil
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Tests primitive queries (ie: that rewrite to themselves) to  * insure they match the expected set of docs, and that the score of each  * match is equal to the value of the scores explanation.  *  *<p>  * The assumption is that if all of the "primitive" queries work well,  * then anything that rewrites to a primitive will work well also.  *</p>  *  */
end_comment
begin_class
DECL|class|BaseExplanationTestCase
specifier|public
specifier|abstract
class|class
name|BaseExplanationTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|protected
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|protected
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|directory
specifier|protected
specifier|static
name|Directory
name|directory
decl_stmt|;
DECL|field|analyzer
specifier|protected
specifier|static
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|"KEY"
decl_stmt|;
comment|// boost on this field is the same as the iterator for the doc
DECL|field|FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
comment|// same contents, but no field boost
DECL|field|ALTFIELD
specifier|public
specifier|static
specifier|final
name|String
name|ALTFIELD
init|=
literal|"alt"
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|afterClassTestExplanations
specifier|public
specifier|static
name|void
name|afterClassTestExplanations
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|=
literal|null
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|analyzer
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClassTestExplanations
specifier|public
specifier|static
name|void
name|beforeClassTestExplanations
parameter_list|()
throws|throws
name|Exception
block|{
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|analyzer
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
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
name|newStringField
argument_list|(
name|KEY
argument_list|,
literal|""
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
name|KEY
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|""
operator|+
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
name|newTextField
argument_list|(
name|FIELD
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
argument_list|)
decl_stmt|;
name|f
operator|.
name|setBoost
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|ALTFIELD
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
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|field|docFields
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|docFields
init|=
block|{
literal|"w1 w2 w3 w4 w5"
block|,
literal|"w1 w3 w2 w3 zz"
block|,
literal|"w1 xx w2 yy w3"
block|,
literal|"w1 w3 xx w2 yy w3 zz"
block|}
decl_stmt|;
comment|/** check the expDocNrs first, then check the query (and the explanations) */
DECL|method|qtest
specifier|public
name|void
name|qtest
parameter_list|(
name|Query
name|q
parameter_list|,
name|int
index|[]
name|expDocNrs
parameter_list|)
throws|throws
name|Exception
block|{
name|CheckHits
operator|.
name|checkHitCollector
argument_list|(
name|random
argument_list|()
argument_list|,
name|q
argument_list|,
name|FIELD
argument_list|,
name|searcher
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests a query using qtest after wrapping it with both optB and reqB    * @see #qtest    * @see #reqB    * @see #optB    */
DECL|method|bqtest
specifier|public
name|void
name|bqtest
parameter_list|(
name|Query
name|q
parameter_list|,
name|int
index|[]
name|expDocNrs
parameter_list|)
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
name|reqB
argument_list|(
name|q
argument_list|)
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|optB
argument_list|(
name|q
argument_list|)
argument_list|,
name|expDocNrs
argument_list|)
expr_stmt|;
block|}
comment|/**     * Convenience subclass of FieldCacheTermsFilter    */
DECL|class|ItemizedQuery
specifier|public
specifier|static
class|class
name|ItemizedQuery
extends|extends
name|DocValuesTermsQuery
block|{
DECL|method|int2str
specifier|private
specifier|static
name|String
index|[]
name|int2str
parameter_list|(
name|int
index|[]
name|terms
parameter_list|)
block|{
name|String
index|[]
name|out
init|=
operator|new
name|String
index|[
name|terms
operator|.
name|length
index|]
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|out
index|[
name|i
index|]
operator|=
literal|""
operator|+
name|terms
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
DECL|method|ItemizedQuery
specifier|public
name|ItemizedQuery
parameter_list|(
name|int
index|[]
name|keys
parameter_list|)
block|{
name|super
argument_list|(
name|KEY
argument_list|,
name|int2str
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** helper for generating MultiPhraseQueries */
DECL|method|ta
specifier|public
specifier|static
name|Term
index|[]
name|ta
parameter_list|(
name|String
index|[]
name|s
parameter_list|)
block|{
name|Term
index|[]
name|t
init|=
operator|new
name|Term
index|[
name|s
operator|.
name|length
index|]
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
name|s
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|t
index|[
name|i
index|]
operator|=
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
name|s
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
comment|/** MACRO for SpanTermQuery */
DECL|method|st
specifier|public
name|SpanQuery
name|st
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|spanTermQuery
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|)
return|;
block|}
comment|/** MACRO for SpanNotQuery */
DECL|method|snot
specifier|public
name|SpanQuery
name|snot
parameter_list|(
name|SpanQuery
name|i
parameter_list|,
name|SpanQuery
name|e
parameter_list|)
block|{
return|return
name|spanNotQuery
argument_list|(
name|i
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/** MACRO for SpanOrQuery containing two SpanTerm queries */
DECL|method|sor
specifier|public
name|SpanQuery
name|sor
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|e
parameter_list|)
block|{
return|return
name|spanOrQuery
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/** MACRO for SpanOrQuery containing two SpanQueries */
DECL|method|sor
specifier|public
name|SpanQuery
name|sor
parameter_list|(
name|SpanQuery
name|s
parameter_list|,
name|SpanQuery
name|e
parameter_list|)
block|{
return|return
name|spanOrQuery
argument_list|(
name|s
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/** MACRO for SpanOrQuery containing three SpanTerm queries */
DECL|method|sor
specifier|public
name|SpanQuery
name|sor
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|m
parameter_list|,
name|String
name|e
parameter_list|)
block|{
return|return
name|spanOrQuery
argument_list|(
name|FIELD
argument_list|,
name|s
argument_list|,
name|m
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/** MACRO for SpanOrQuery containing two SpanQueries */
DECL|method|sor
specifier|public
name|SpanQuery
name|sor
parameter_list|(
name|SpanQuery
name|s
parameter_list|,
name|SpanQuery
name|m
parameter_list|,
name|SpanQuery
name|e
parameter_list|)
block|{
return|return
name|spanOrQuery
argument_list|(
name|s
argument_list|,
name|m
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/** MACRO for SpanNearQuery containing two SpanTerm queries */
DECL|method|snear
specifier|public
name|SpanQuery
name|snear
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|e
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
return|return
name|snear
argument_list|(
name|st
argument_list|(
name|s
argument_list|)
argument_list|,
name|st
argument_list|(
name|e
argument_list|)
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
comment|/** MACRO for SpanNearQuery containing two SpanQueries */
DECL|method|snear
specifier|public
name|SpanQuery
name|snear
parameter_list|(
name|SpanQuery
name|s
parameter_list|,
name|SpanQuery
name|e
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
if|if
condition|(
name|inOrder
condition|)
block|{
return|return
name|spanNearOrderedQuery
argument_list|(
name|slop
argument_list|,
name|s
argument_list|,
name|e
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|spanNearUnorderedQuery
argument_list|(
name|slop
argument_list|,
name|s
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
comment|/** MACRO for SpanNearQuery containing three SpanTerm queries */
DECL|method|snear
specifier|public
name|SpanQuery
name|snear
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|m
parameter_list|,
name|String
name|e
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
return|return
name|snear
argument_list|(
name|st
argument_list|(
name|s
argument_list|)
argument_list|,
name|st
argument_list|(
name|m
argument_list|)
argument_list|,
name|st
argument_list|(
name|e
argument_list|)
argument_list|,
name|slop
argument_list|,
name|inOrder
argument_list|)
return|;
block|}
comment|/** MACRO for SpanNearQuery containing three SpanQueries */
DECL|method|snear
specifier|public
name|SpanQuery
name|snear
parameter_list|(
name|SpanQuery
name|s
parameter_list|,
name|SpanQuery
name|m
parameter_list|,
name|SpanQuery
name|e
parameter_list|,
name|int
name|slop
parameter_list|,
name|boolean
name|inOrder
parameter_list|)
block|{
if|if
condition|(
name|inOrder
condition|)
block|{
return|return
name|spanNearOrderedQuery
argument_list|(
name|slop
argument_list|,
name|s
argument_list|,
name|m
argument_list|,
name|e
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|spanNearUnorderedQuery
argument_list|(
name|slop
argument_list|,
name|s
argument_list|,
name|m
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
comment|/** MACRO for SpanFirst(SpanTermQuery) */
DECL|method|sf
specifier|public
name|SpanQuery
name|sf
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|b
parameter_list|)
block|{
return|return
name|spanFirstQuery
argument_list|(
name|st
argument_list|(
name|s
argument_list|)
argument_list|,
name|b
argument_list|)
return|;
block|}
comment|/**    * MACRO: Wraps a Query in a BooleanQuery so that it is optional, along    * with a second prohibited clause which will never match anything    */
DECL|method|optB
specifier|public
name|Query
name|optB
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
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
literal|"NEVER"
argument_list|,
literal|"MATCH"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
return|return
name|bq
return|;
block|}
comment|/**    * MACRO: Wraps a Query in a BooleanQuery so that it is required, along    * with a second optional clause which will match everything    */
DECL|method|reqB
specifier|public
name|Query
name|reqB
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|q
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
name|FIELD
argument_list|,
literal|"w1"
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
return|return
name|bq
return|;
block|}
block|}
end_class
end_unit
