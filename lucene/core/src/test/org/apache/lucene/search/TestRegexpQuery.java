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
name|Arrays
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
name|Operations
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
name|Automaton
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
name|AutomatonProvider
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
name|RegExp
import|;
end_import
begin_comment
comment|/**  * Some simple regex tests, mostly converted from contrib's TestRegexQuery.  */
end_comment
begin_class
DECL|class|TestRegexpQuery
specifier|public
class|class
name|TestRegexpQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|FN
specifier|private
specifier|final
name|String
name|FN
init|=
literal|"field"
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
argument_list|()
argument_list|,
name|directory
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
name|newTextField
argument_list|(
name|FN
argument_list|,
literal|"the quick brown fox jumps over the lazy ??? dog 493432 49344"
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
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|shutdown
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
name|directory
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
DECL|method|newTerm
specifier|private
name|Term
name|newTerm
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|FN
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|regexQueryNrHits
specifier|private
name|int
name|regexQueryNrHits
parameter_list|(
name|String
name|regex
parameter_list|)
throws|throws
name|IOException
block|{
name|RegexpQuery
name|query
init|=
operator|new
name|RegexpQuery
argument_list|(
name|newTerm
argument_list|(
name|regex
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|5
argument_list|)
operator|.
name|totalHits
return|;
block|}
DECL|method|testRegex1
specifier|public
name|void
name|testRegex1
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"q.[aeiou]c.*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegex2
specifier|public
name|void
name|testRegex2
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|".[aeiou]c.*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegex3
specifier|public
name|void
name|testRegex3
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"q.[aeiou]c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNumericRange
specifier|public
name|void
name|testNumericRange
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"<420000-600000>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"<493433-600000>"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegexComplement
specifier|public
name|void
name|testRegexComplement
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"4934~[3]"
argument_list|)
argument_list|)
expr_stmt|;
comment|// not the empty lang, i.e. match all docs
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"~#"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCustomProvider
specifier|public
name|void
name|testCustomProvider
parameter_list|()
throws|throws
name|IOException
block|{
name|AutomatonProvider
name|myProvider
init|=
operator|new
name|AutomatonProvider
argument_list|()
block|{
comment|// automaton that matches quick or brown
specifier|private
name|Automaton
name|quickBrownAutomaton
init|=
name|Operations
operator|.
name|union
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|Automata
operator|.
name|makeString
argument_list|(
literal|"quick"
argument_list|)
argument_list|,
name|Automata
operator|.
name|makeString
argument_list|(
literal|"brown"
argument_list|)
argument_list|,
name|Automata
operator|.
name|makeString
argument_list|(
literal|"bob"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Automaton
name|getAutomaton
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"quickBrown"
argument_list|)
condition|)
return|return
name|quickBrownAutomaton
return|;
else|else
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|RegexpQuery
name|query
init|=
operator|new
name|RegexpQuery
argument_list|(
name|newTerm
argument_list|(
literal|"<quickBrown>"
argument_list|)
argument_list|,
name|RegExp
operator|.
name|ALL
argument_list|,
name|myProvider
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|5
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test a corner case for backtracking: In this case the term dictionary has    * 493432 followed by 49344. When backtracking from 49343... to 4934, its    * necessary to test that 4934 itself is ok before trying to append more    * characters.    */
DECL|method|testBacktracking
specifier|public
name|void
name|testBacktracking
parameter_list|()
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|regexQueryNrHits
argument_list|(
literal|"4934[314]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
