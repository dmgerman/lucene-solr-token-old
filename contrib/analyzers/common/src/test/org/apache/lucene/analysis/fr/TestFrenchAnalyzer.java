begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.fr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fr
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
name|util
operator|.
name|Version
import|;
end_import
begin_comment
comment|/**  * Test case for FrenchAnalyzer.  *  * @version   $version$  */
end_comment
begin_class
DECL|class|TestFrenchAnalyzer
specifier|public
class|class
name|TestFrenchAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testAnalyzer
specifier|public
name|void
name|testAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
name|FrenchAnalyzer
name|fa
init|=
operator|new
name|FrenchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[]
block|{ 		}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"chien chat cheval"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|,
literal|"chat"
block|,
literal|"cheval"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"chien CHAT CHEVAL"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|,
literal|"chat"
block|,
literal|"cheval"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"  chien  ,? + = -  CHAT /:> CHEVAL"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|,
literal|"chat"
block|,
literal|"cheval"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"chien++"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"mot \"entreguillemet\""
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mot"
block|,
literal|"entreguillemet"
block|}
argument_list|)
expr_stmt|;
comment|// let's do some french specific tests now
comment|/* 1. couldn't resist 		 I would expect this to stay one term as in French the minus  		sign is often used for composing words */
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"Jean-FranÃ§ois"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jean"
block|,
literal|"franÃ§ois"
block|}
argument_list|)
expr_stmt|;
comment|// 2. stopwords
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"le la chien les aux chat du des Ã  cheval"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|,
literal|"chat"
block|,
literal|"cheval"
block|}
argument_list|)
expr_stmt|;
comment|// some nouns and adjectives
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"lances chismes habitable chiste Ã©lÃ©ments captifs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lanc"
block|,
literal|"chism"
block|,
literal|"habit"
block|,
literal|"chist"
block|,
literal|"Ã©lÃ©ment"
block|,
literal|"captif"
block|}
argument_list|)
expr_stmt|;
comment|// some verbs
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"finissions souffrirent rugissante"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"fin"
block|,
literal|"souffr"
block|,
literal|"rug"
block|}
argument_list|)
expr_stmt|;
comment|// some everything else
comment|// aujourd'hui stays one term which is OK
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"C3PO aujourd'hui oeuf Ã¯Ã¢Ã¶Ã»Ã Ã¤ anticonstitutionnellement Java++ "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c3po"
block|,
literal|"aujourd'hui"
block|,
literal|"oeuf"
block|,
literal|"Ã¯Ã¢Ã¶Ã»Ã Ã¤"
block|,
literal|"anticonstitutionnel"
block|,
literal|"jav"
block|}
argument_list|)
expr_stmt|;
comment|// some more everything else
comment|// here 1940-1945 stays as one term, 1940:1945 not ?
name|assertAnalyzesTo
argument_list|(
name|fa
argument_list|,
literal|"33Bis 1940-1945 1940:1945 (---i+++)*"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"33bis"
block|,
literal|"1940-1945"
block|,
literal|"1940"
block|,
literal|"1945"
block|,
literal|"i"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|FrenchAnalyzer
name|fa
init|=
operator|new
name|FrenchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
comment|// stopwords
name|assertAnalyzesToReuse
argument_list|(
name|fa
argument_list|,
literal|"le la chien les aux chat du des Ã  cheval"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"chien"
block|,
literal|"chat"
block|,
literal|"cheval"
block|}
argument_list|)
expr_stmt|;
comment|// some nouns and adjectives
name|assertAnalyzesToReuse
argument_list|(
name|fa
argument_list|,
literal|"lances chismes habitable chiste Ã©lÃ©ments captifs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lanc"
block|,
literal|"chism"
block|,
literal|"habit"
block|,
literal|"chist"
block|,
literal|"Ã©lÃ©ment"
block|,
literal|"captif"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*  	 * Test that changes to the exclusion table are applied immediately 	 * when using reusable token streams. 	 */
DECL|method|testExclusionTableReuse
specifier|public
name|void
name|testExclusionTableReuse
parameter_list|()
throws|throws
name|Exception
block|{
name|FrenchAnalyzer
name|fa
init|=
operator|new
name|FrenchAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|fa
argument_list|,
literal|"habitable"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"habit"
block|}
argument_list|)
expr_stmt|;
name|fa
operator|.
name|setStemExclusionTable
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"habitable"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|fa
argument_list|,
literal|"habitable"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"habitable"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
