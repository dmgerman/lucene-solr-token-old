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
begin_class
DECL|class|TestNGramPhraseQuery
specifier|public
class|class
name|TestNGramPhraseQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|directory
specifier|private
specifier|static
name|Directory
name|directory
decl_stmt|;
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
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|directory
argument_list|)
decl_stmt|;
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
name|directory
argument_list|)
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
block|}
DECL|method|testRewrite
specifier|public
name|void
name|testRewrite
parameter_list|()
throws|throws
name|Exception
block|{
comment|// bi-gram test ABC => AB/BC => AB/BC
name|PhraseQuery
name|pq1
init|=
operator|new
name|NGramPhraseQuery
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|pq1
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"AB"
argument_list|)
argument_list|)
expr_stmt|;
name|pq1
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"BC"
argument_list|)
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|pq1
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|NGramPhraseQuery
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|pq1
argument_list|,
name|q
argument_list|)
expr_stmt|;
name|pq1
operator|=
operator|(
name|NGramPhraseQuery
operator|)
name|q
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"AB"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"BC"
argument_list|)
block|}
argument_list|,
name|pq1
operator|.
name|getTerms
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
argument_list|,
name|pq1
operator|.
name|getPositions
argument_list|()
argument_list|)
expr_stmt|;
comment|// bi-gram test ABCD => AB/BC/CD => AB//CD
name|PhraseQuery
name|pq2
init|=
operator|new
name|NGramPhraseQuery
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|pq2
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"AB"
argument_list|)
argument_list|)
expr_stmt|;
name|pq2
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"BC"
argument_list|)
argument_list|)
expr_stmt|;
name|pq2
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"CD"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|=
name|pq2
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|PhraseQuery
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|pq2
argument_list|,
name|q
argument_list|)
expr_stmt|;
name|pq2
operator|=
operator|(
name|PhraseQuery
operator|)
name|q
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"AB"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"CD"
argument_list|)
block|}
argument_list|,
name|pq2
operator|.
name|getTerms
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|,
name|pq2
operator|.
name|getPositions
argument_list|()
argument_list|)
expr_stmt|;
comment|// tri-gram test ABCDEFGH => ABC/BCD/CDE/DEF/EFG/FGH => ABC///DEF//FGH
name|PhraseQuery
name|pq3
init|=
operator|new
name|NGramPhraseQuery
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|pq3
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"ABC"
argument_list|)
argument_list|)
expr_stmt|;
name|pq3
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"BCD"
argument_list|)
argument_list|)
expr_stmt|;
name|pq3
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"CDE"
argument_list|)
argument_list|)
expr_stmt|;
name|pq3
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"DEF"
argument_list|)
argument_list|)
expr_stmt|;
name|pq3
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"EFG"
argument_list|)
argument_list|)
expr_stmt|;
name|pq3
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"FGH"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|=
name|pq3
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|q
operator|instanceof
name|PhraseQuery
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|pq3
argument_list|,
name|q
argument_list|)
expr_stmt|;
name|pq3
operator|=
operator|(
name|PhraseQuery
operator|)
name|q
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"ABC"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"DEF"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"FGH"
argument_list|)
block|}
argument_list|,
name|pq3
operator|.
name|getTerms
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|5
block|}
argument_list|,
name|pq3
operator|.
name|getPositions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
