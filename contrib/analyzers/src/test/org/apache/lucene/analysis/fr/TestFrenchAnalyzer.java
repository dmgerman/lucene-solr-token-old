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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2004 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|Token
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
begin_comment
comment|/**  * Test case for FrenchAnalyzer.  *  * @version   $version$  */
end_comment
begin_class
DECL|class|TestFrenchAnalyzer
specifier|public
class|class
name|TestFrenchAnalyzer
extends|extends
name|TestCase
block|{
comment|// Method copied from TestAnalyzers, maybe should be refactored
DECL|method|assertAnalyzesTo
specifier|public
name|void
name|assertAnalyzesTo
parameter_list|(
name|Analyzer
name|a
parameter_list|,
name|String
name|input
parameter_list|,
name|String
index|[]
name|output
parameter_list|)
throws|throws
name|Exception
block|{
name|TokenStream
name|ts
init|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Token
name|reusableToken
init|=
operator|new
name|Token
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
name|output
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Token
name|nextToken
init|=
name|ts
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|nextToken
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nextToken
operator|.
name|term
argument_list|()
argument_list|,
name|output
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|ts
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
argument_list|)
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
argument_list|()
decl_stmt|;
comment|// test null reader
name|boolean
name|iaeFlag
init|=
literal|false
decl_stmt|;
try|try
block|{
name|fa
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|iaeFlag
operator|=
literal|true
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|iaeFlag
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// test null fieldname
name|iaeFlag
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|fa
operator|.
name|tokenStream
argument_list|(
literal|null
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"dummy"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|iaeFlag
operator|=
literal|true
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|iaeFlag
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
block|}
end_class
end_unit
