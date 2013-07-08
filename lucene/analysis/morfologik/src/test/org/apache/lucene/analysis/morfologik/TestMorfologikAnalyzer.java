begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.morfologik
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|morfologik
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
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|analysis
operator|.
name|Tokenizer
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
name|miscellaneous
operator|.
name|SetKeywordMarkerFilter
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
name|standard
operator|.
name|StandardFilter
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
name|standard
operator|.
name|StandardTokenizer
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|util
operator|.
name|CharArraySet
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
comment|/**  * TODO: The tests below rely on the order of returned lemmas, which is probably not good.   */
end_comment
begin_class
DECL|class|TestMorfologikAnalyzer
specifier|public
class|class
name|TestMorfologikAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|getTestAnalyzer
specifier|private
name|Analyzer
name|getTestAnalyzer
parameter_list|()
block|{
return|return
operator|new
name|MorfologikAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
return|;
block|}
comment|/** Test stemming of single tokens with Morfologik library. */
DECL|method|testSingleTokens
specifier|public
specifier|final
name|void
name|testSingleTokens
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
name|getTestAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"a"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"liÅcie"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"liÅcie"
block|,
literal|"liÅÄ"
block|,
literal|"list"
block|,
literal|"lista"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"danych"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dany"
block|,
literal|"dana"
block|,
literal|"dane"
block|,
literal|"daÄ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"ÄÃ³ÄÅÅÅ¼ÅºÄÅ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÄÃ³ÄÅÅÅ¼ÅºÄÅ"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test stemming of multiple tokens and proper term metrics. */
DECL|method|testMultipleTokens
specifier|public
specifier|final
name|void
name|testMultipleTokens
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
name|getTestAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"liÅcie danych"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"liÅcie"
block|,
literal|"liÅÄ"
block|,
literal|"list"
block|,
literal|"lista"
block|,
literal|"dany"
block|,
literal|"dana"
block|,
literal|"dane"
block|,
literal|"daÄ"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|7
block|,
literal|7
block|,
literal|7
block|,
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|6
block|,
literal|6
block|,
literal|6
block|,
literal|13
block|,
literal|13
block|,
literal|13
block|,
literal|13
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"T. Gl\u00FCcksberg"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"tom"
block|,
literal|"tona"
block|,
literal|"Gl\u00FCcksberg"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|3
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|13
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|dumpTokens
specifier|private
name|void
name|dumpTokens
parameter_list|(
name|String
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|ts
init|=
name|getTestAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
name|input
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|MorphosyntacticTagsAttribute
name|attribute
init|=
name|ts
operator|.
name|getAttribute
argument_list|(
name|MorphosyntacticTagsAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|charTerm
init|=
name|ts
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|charTerm
operator|.
name|toString
argument_list|()
operator|+
literal|" => "
operator|+
name|attribute
operator|.
name|getTags
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test reuse of MorfologikFilter with leftover stems. */
DECL|method|testLeftoverStems
specifier|public
specifier|final
name|void
name|testLeftoverStems
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
name|getTestAnalyzer
argument_list|()
decl_stmt|;
name|TokenStream
name|ts_1
init|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
literal|"liÅcie"
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt_1
init|=
name|ts_1
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|ts_1
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ts_1
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first stream"
argument_list|,
literal|"liÅcie"
argument_list|,
name|termAtt_1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ts_1
operator|.
name|end
argument_list|()
expr_stmt|;
name|ts_1
operator|.
name|close
argument_list|()
expr_stmt|;
name|TokenStream
name|ts_2
init|=
name|a
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
literal|"danych"
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt_2
init|=
name|ts_2
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|ts_2
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ts_2
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"second stream"
argument_list|,
literal|"dany"
argument_list|,
name|termAtt_2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ts_2
operator|.
name|end
argument_list|()
expr_stmt|;
name|ts_2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Test stemming of mixed-case tokens. */
DECL|method|testCase
specifier|public
specifier|final
name|void
name|testCase
parameter_list|()
throws|throws
name|IOException
block|{
name|Analyzer
name|a
init|=
name|getTestAnalyzer
argument_list|()
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"AGD"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"AGD"
block|,
literal|"artykuÅy gospodarstwa domowego"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"agd"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"artykuÅy gospodarstwa domowego"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"Poznania"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"PoznaÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"poznania"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"poznanie"
block|,
literal|"poznaÄ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"Aarona"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Aaron"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"aarona"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aarona"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"LiÅcie"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"liÅcie"
block|,
literal|"liÅÄ"
block|,
literal|"list"
block|,
literal|"lista"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|assertPOSToken
specifier|private
name|void
name|assertPOSToken
parameter_list|(
name|TokenStream
name|ts
parameter_list|,
name|String
name|term
parameter_list|,
name|String
modifier|...
name|tags
parameter_list|)
throws|throws
name|IOException
block|{
name|ts
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|term
argument_list|,
name|ts
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|TreeSet
argument_list|<
name|String
argument_list|>
name|actual
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|TreeSet
argument_list|<
name|String
argument_list|>
name|expected
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StringBuilder
name|b
range|:
name|ts
operator|.
name|getAttribute
argument_list|(
name|MorphosyntacticTagsAttribute
operator|.
name|class
argument_list|)
operator|.
name|getTags
argument_list|()
control|)
block|{
name|actual
operator|.
name|add
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|s
range|:
name|tags
control|)
block|{
name|expected
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|expected
operator|.
name|equals
argument_list|(
name|actual
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Expected:\n"
operator|+
name|expected
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Actual:\n"
operator|+
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test morphosyntactic annotations. */
DECL|method|testPOSAttribute
specifier|public
specifier|final
name|void
name|testPOSAttribute
parameter_list|()
throws|throws
name|IOException
block|{
name|TokenStream
name|ts
init|=
name|getTestAnalyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
literal|"liÅcie"
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertPOSToken
argument_list|(
name|ts
argument_list|,
literal|"liÅcie"
argument_list|,
literal|"subst:sg:acc:n2"
argument_list|,
literal|"subst:sg:nom:n2"
argument_list|,
literal|"subst:sg:voc:n2"
argument_list|)
expr_stmt|;
name|assertPOSToken
argument_list|(
name|ts
argument_list|,
literal|"liÅÄ"
argument_list|,
literal|"subst:pl:acc:m3"
argument_list|,
literal|"subst:pl:nom:m3"
argument_list|,
literal|"subst:pl:voc:m3"
argument_list|)
expr_stmt|;
name|assertPOSToken
argument_list|(
name|ts
argument_list|,
literal|"list"
argument_list|,
literal|"subst:sg:loc:m3"
argument_list|,
literal|"subst:sg:voc:m3"
argument_list|)
expr_stmt|;
name|assertPOSToken
argument_list|(
name|ts
argument_list|,
literal|"lista"
argument_list|,
literal|"subst:sg:dat:f"
argument_list|,
literal|"subst:sg:loc:f"
argument_list|)
expr_stmt|;
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** */
DECL|method|testKeywordAttrTokens
specifier|public
specifier|final
name|void
name|testKeywordAttrTokens
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Version
name|version
init|=
name|TEST_VERSION_CURRENT
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|MorfologikAnalyzer
argument_list|(
name|version
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|field
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
specifier|final
name|CharArraySet
name|keywords
init|=
operator|new
name|CharArraySet
argument_list|(
name|version
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|keywords
operator|.
name|add
argument_list|(
literal|"liÅcie"
argument_list|)
expr_stmt|;
specifier|final
name|Tokenizer
name|src
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|TokenStream
name|result
init|=
operator|new
name|StandardFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|src
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|SetKeywordMarkerFilter
argument_list|(
name|result
argument_list|,
name|keywords
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|MorfologikFilter
argument_list|(
name|result
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|src
argument_list|,
name|result
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"liÅcie danych"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"liÅcie"
block|,
literal|"dany"
block|,
literal|"dana"
block|,
literal|"dane"
block|,
literal|"daÄ"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|7
block|,
literal|7
block|,
literal|7
block|,
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|13
block|,
literal|13
block|,
literal|13
block|,
literal|13
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|getTestAnalyzer
argument_list|()
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
