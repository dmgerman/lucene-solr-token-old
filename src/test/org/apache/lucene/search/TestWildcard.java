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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|SimpleAnalyzer
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
name|store
operator|.
name|RAMDirectory
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
begin_comment
comment|/**  * TestWildcard tests the '*' and '?' wildard characters.  *  * @author Otis Gospodnetic  */
end_comment
begin_class
DECL|class|TestWildcard
specifier|public
class|class
name|TestWildcard
extends|extends
name|TestCase
block|{
DECL|method|testEquals
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|WildcardQuery
name|wq1
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b*a"
argument_list|)
argument_list|)
decl_stmt|;
name|WildcardQuery
name|wq2
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b*a"
argument_list|)
argument_list|)
decl_stmt|;
name|WildcardQuery
name|wq3
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b*a"
argument_list|)
argument_list|)
decl_stmt|;
comment|// reflexive?
name|assertEquals
argument_list|(
name|wq1
argument_list|,
name|wq2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|wq2
argument_list|,
name|wq1
argument_list|)
expr_stmt|;
comment|// transitive?
name|assertEquals
argument_list|(
name|wq2
argument_list|,
name|wq3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|wq1
argument_list|,
name|wq3
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|wq1
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|FuzzyQuery
name|fq
init|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b*a"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|wq1
operator|.
name|equals
argument_list|(
name|fq
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fq
operator|.
name|equals
argument_list|(
name|wq1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests Wildcard queries with an asterisk.    */
DECL|method|testAsterisk
specifier|public
name|void
name|testAsterisk
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|indexStore
init|=
name|getIndexStore
argument_list|(
literal|"body"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"metal"
block|,
literal|"metals"
block|}
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|)
decl_stmt|;
name|Query
name|query1
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"metal"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query2
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"metal*"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query3
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"m*tal"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query4
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"m*tal*"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query5
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"m*tals"
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanQuery
name|query6
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query6
operator|.
name|add
argument_list|(
name|query5
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
name|query7
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query7
operator|.
name|add
argument_list|(
name|query3
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query7
operator|.
name|add
argument_list|(
name|query5
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
comment|// Queries do not automatically lower-case search terms:
name|Query
name|query8
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"M*tal*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query4
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query5
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query6
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query7
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query8
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests Wildcard queries with a question mark.    *    * @throws IOException if an error occurs    */
DECL|method|testQuestionmark
specifier|public
name|void
name|testQuestionmark
parameter_list|()
throws|throws
name|IOException
block|{
name|RAMDirectory
name|indexStore
init|=
name|getIndexStore
argument_list|(
literal|"body"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"metal"
block|,
literal|"metals"
block|,
literal|"mXtals"
block|,
literal|"mXtXls"
block|}
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|)
decl_stmt|;
name|Query
name|query1
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"m?tal"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query2
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"metal?"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query3
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"metals?"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query4
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"m?t?ls"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query5
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"M?t?ls"
argument_list|)
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query4
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|assertMatches
argument_list|(
name|searcher
argument_list|,
name|query5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|getIndexStore
specifier|private
name|RAMDirectory
name|getIndexStore
parameter_list|(
name|String
name|field
parameter_list|,
name|String
index|[]
name|contents
parameter_list|)
throws|throws
name|IOException
block|{
name|RAMDirectory
name|indexStore
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
name|indexStore
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|,
literal|true
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
name|contents
operator|.
name|length
condition|;
operator|++
name|i
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
name|contents
index|[
name|i
index|]
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
name|TOKENIZED
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
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|indexStore
return|;
block|}
DECL|method|assertMatches
specifier|private
name|void
name|assertMatches
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|q
parameter_list|,
name|int
name|expectedMatches
parameter_list|)
throws|throws
name|IOException
block|{
name|Hits
name|result
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedMatches
argument_list|,
name|result
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
