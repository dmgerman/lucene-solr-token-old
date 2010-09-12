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
name|store
operator|.
name|Directory
import|;
end_import
begin_class
DECL|class|TestBooleanOr
specifier|public
class|class
name|TestBooleanOr
extends|extends
name|LuceneTestCase
block|{
DECL|field|FIELD_T
specifier|private
specifier|static
name|String
name|FIELD_T
init|=
literal|"T"
decl_stmt|;
DECL|field|FIELD_C
specifier|private
specifier|static
name|String
name|FIELD_C
init|=
literal|"C"
decl_stmt|;
DECL|field|t1
specifier|private
name|TermQuery
name|t1
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_T
argument_list|,
literal|"files"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|t2
specifier|private
name|TermQuery
name|t2
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_T
argument_list|,
literal|"deleting"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|c1
specifier|private
name|TermQuery
name|c1
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_C
argument_list|,
literal|"production"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|c2
specifier|private
name|TermQuery
name|c2
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_C
argument_list|,
literal|"optimize"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|method|search
specifier|private
name|int
name|search
parameter_list|(
name|Query
name|q
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryUtils
operator|.
name|check
argument_list|(
name|q
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
return|return
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
return|;
block|}
DECL|method|testElements
specifier|public
name|void
name|testElements
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|search
argument_list|(
name|t1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|search
argument_list|(
name|t2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|search
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|search
argument_list|(
name|c2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    *<code>T:files T:deleting C:production C:optimize</code>    * it works.    *    * @throws IOException    */
DECL|method|testFlat
specifier|public
name|void
name|testFlat
parameter_list|()
throws|throws
name|IOException
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|t1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|t2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|c1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|c2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|search
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    *<code>(T:files T:deleting) (+C:production +C:optimize)</code>    * it works.    *    * @throws IOException    */
DECL|method|testParenthesisMust
specifier|public
name|void
name|testParenthesisMust
parameter_list|()
throws|throws
name|IOException
block|{
name|BooleanQuery
name|q3
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q3
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|t1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|q3
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|t2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|q4
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q4
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|c1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|q4
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|c2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|q2
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|q4
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|search
argument_list|(
name|q2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    *<code>(T:files T:deleting) +(C:production C:optimize)</code>    * not working. results NO HIT.    *    * @throws IOException    */
DECL|method|testParenthesisMust2
specifier|public
name|void
name|testParenthesisMust2
parameter_list|()
throws|throws
name|IOException
block|{
name|BooleanQuery
name|q3
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q3
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|t1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|q3
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|t2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|q4
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q4
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|c1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|q4
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|c2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|q2
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|q4
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|search
argument_list|(
name|q2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    *<code>(T:files T:deleting) (C:production C:optimize)</code>    * not working. results NO HIT.    *    * @throws IOException    */
DECL|method|testParenthesisShould
specifier|public
name|void
name|testParenthesisShould
parameter_list|()
throws|throws
name|IOException
block|{
name|BooleanQuery
name|q3
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q3
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|t1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|q3
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|t2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|q4
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q4
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|c1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|q4
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|c2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|BooleanQuery
name|q2
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|q3
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|q4
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|search
argument_list|(
name|q2
argument_list|)
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
comment|//
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
comment|//
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|//
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|FIELD_T
argument_list|,
literal|"Optimize not deleting all files"
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
name|d
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|FIELD_C
argument_list|,
literal|"Deleted When I run an optimize in our production environment."
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
comment|//
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
comment|//
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
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
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
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
block|}
end_class
end_unit
