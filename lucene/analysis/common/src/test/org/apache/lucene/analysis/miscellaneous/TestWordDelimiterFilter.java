begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package
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
name|*
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
name|core
operator|.
name|KeywordTokenizer
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
name|core
operator|.
name|StopFilter
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
name|StandardAnalyzer
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
name|PositionIncrementAttribute
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
name|IOUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|analysis
operator|.
name|miscellaneous
operator|.
name|WordDelimiterFilter
operator|.
name|*
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
name|analysis
operator|.
name|miscellaneous
operator|.
name|WordDelimiterIterator
operator|.
name|DEFAULT_WORD_DELIM_TABLE
import|;
end_import
begin_comment
comment|/**  * New WordDelimiterFilter tests... most of the tests are in ConvertedLegacyTest  * TODO: should explicitly test things like protWords and not rely on  * the factory tests in Solr.  */
end_comment
begin_class
DECL|class|TestWordDelimiterFilter
specifier|public
class|class
name|TestWordDelimiterFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/*   public void testPerformance() throws IOException {     String s = "now is the time-for all good men to come to-the aid of their country.";     Token tok = new Token();     long start = System.currentTimeMillis();     int ret=0;     for (int i=0; i<1000000; i++) {       StringReader r = new StringReader(s);       TokenStream ts = new WhitespaceTokenizer(r);       ts = new WordDelimiterFilter(ts, 1,1,1,1,0);        while (ts.next(tok) != null) ret++;     }      System.out.println("ret="+ret+" time="+(System.currentTimeMillis()-start));   }   ***/
annotation|@
name|Test
DECL|method|testOffsets
specifier|public
name|void
name|testOffsets
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|flags
init|=
name|GENERATE_WORD_PARTS
operator||
name|GENERATE_NUMBER_PARTS
operator||
name|CATENATE_ALL
operator||
name|SPLIT_ON_CASE_CHANGE
operator||
name|SPLIT_ON_NUMERICS
operator||
name|STEM_ENGLISH_POSSESSIVE
decl_stmt|;
comment|// test that subwords and catenated subwords have
comment|// the correct offsets.
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"foo-bar"
argument_list|,
literal|5
argument_list|,
literal|12
argument_list|)
argument_list|)
argument_list|,
name|DEFAULT_WORD_DELIM_TABLE
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"foobar"
block|,
literal|"bar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|,
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|8
block|,
literal|12
block|,
literal|12
block|}
argument_list|)
expr_stmt|;
name|wdf
operator|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"foo-bar"
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
argument_list|)
argument_list|,
name|DEFAULT_WORD_DELIM_TABLE
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"bar"
block|,
literal|"foobar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|5
block|,
literal|5
block|,
literal|5
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
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOffsetChange
specifier|public
name|void
name|testOffsetChange
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|flags
init|=
name|GENERATE_WORD_PARTS
operator||
name|GENERATE_NUMBER_PARTS
operator||
name|CATENATE_ALL
operator||
name|SPLIT_ON_CASE_CHANGE
operator||
name|SPLIT_ON_NUMERICS
operator||
name|STEM_ENGLISH_POSSESSIVE
decl_stmt|;
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"Ã¼belkeit)"
argument_list|,
literal|7
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|,
name|DEFAULT_WORD_DELIM_TABLE
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ã¼belkeit"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|15
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOffsetChange2
specifier|public
name|void
name|testOffsetChange2
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|flags
init|=
name|GENERATE_WORD_PARTS
operator||
name|GENERATE_NUMBER_PARTS
operator||
name|CATENATE_ALL
operator||
name|SPLIT_ON_CASE_CHANGE
operator||
name|SPLIT_ON_NUMERICS
operator||
name|STEM_ENGLISH_POSSESSIVE
decl_stmt|;
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"(Ã¼belkeit"
argument_list|,
literal|7
argument_list|,
literal|17
argument_list|)
argument_list|)
argument_list|,
name|DEFAULT_WORD_DELIM_TABLE
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ã¼belkeit"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|17
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOffsetChange3
specifier|public
name|void
name|testOffsetChange3
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|flags
init|=
name|GENERATE_WORD_PARTS
operator||
name|GENERATE_NUMBER_PARTS
operator||
name|CATENATE_ALL
operator||
name|SPLIT_ON_CASE_CHANGE
operator||
name|SPLIT_ON_NUMERICS
operator||
name|STEM_ENGLISH_POSSESSIVE
decl_stmt|;
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"(Ã¼belkeit"
argument_list|,
literal|7
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|,
name|DEFAULT_WORD_DELIM_TABLE
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ã¼belkeit"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|16
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOffsetChange4
specifier|public
name|void
name|testOffsetChange4
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|flags
init|=
name|GENERATE_WORD_PARTS
operator||
name|GENERATE_NUMBER_PARTS
operator||
name|CATENATE_ALL
operator||
name|SPLIT_ON_CASE_CHANGE
operator||
name|SPLIT_ON_NUMERICS
operator||
name|STEM_ENGLISH_POSSESSIVE
decl_stmt|;
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|CannedTokenStream
argument_list|(
operator|new
name|Token
argument_list|(
literal|"(foo,bar)"
argument_list|,
literal|7
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|,
name|DEFAULT_WORD_DELIM_TABLE
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"foobar"
block|,
literal|"bar"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|8
block|,
literal|8
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|11
block|,
literal|15
block|,
literal|15
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|doSplit
specifier|public
name|void
name|doSplit
parameter_list|(
specifier|final
name|String
name|input
parameter_list|,
name|String
modifier|...
name|output
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|flags
init|=
name|GENERATE_WORD_PARTS
operator||
name|GENERATE_NUMBER_PARTS
operator||
name|SPLIT_ON_CASE_CHANGE
operator||
name|SPLIT_ON_NUMERICS
operator||
name|STEM_ENGLISH_POSSESSIVE
decl_stmt|;
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
name|keywordMockTokenizer
argument_list|(
name|input
argument_list|)
argument_list|,
name|WordDelimiterIterator
operator|.
name|DEFAULT_WORD_DELIM_TABLE
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSplits
specifier|public
name|void
name|testSplits
parameter_list|()
throws|throws
name|Exception
block|{
name|doSplit
argument_list|(
literal|"basic-split"
argument_list|,
literal|"basic"
argument_list|,
literal|"split"
argument_list|)
expr_stmt|;
name|doSplit
argument_list|(
literal|"camelCase"
argument_list|,
literal|"camel"
argument_list|,
literal|"Case"
argument_list|)
expr_stmt|;
comment|// non-space marking symbol shouldn't cause split
comment|// this is an example in Thai
name|doSplit
argument_list|(
literal|"\u0e1a\u0e49\u0e32\u0e19"
argument_list|,
literal|"\u0e1a\u0e49\u0e32\u0e19"
argument_list|)
expr_stmt|;
comment|// possessive followed by delimiter
name|doSplit
argument_list|(
literal|"test's'"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
comment|// some russian upper and lowercase
name|doSplit
argument_list|(
literal|"Ð Ð¾Ð±ÐµÑÑ"
argument_list|,
literal|"Ð Ð¾Ð±ÐµÑÑ"
argument_list|)
expr_stmt|;
comment|// now cause a split (russian camelCase)
name|doSplit
argument_list|(
literal|"Ð Ð¾Ð±ÐÑÑ"
argument_list|,
literal|"Ð Ð¾Ð±"
argument_list|,
literal|"ÐÑÑ"
argument_list|)
expr_stmt|;
comment|// a composed titlecase character, don't split
name|doSplit
argument_list|(
literal|"aÇungla"
argument_list|,
literal|"aÇungla"
argument_list|)
expr_stmt|;
comment|// a modifier letter, don't split
name|doSplit
argument_list|(
literal|"Ø³ÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙØ§Ù"
argument_list|,
literal|"Ø³ÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙØ§Ù"
argument_list|)
expr_stmt|;
comment|// enclosing mark, don't split
name|doSplit
argument_list|(
literal|"testâ"
argument_list|,
literal|"testâ"
argument_list|)
expr_stmt|;
comment|// combining spacing mark (the virama), don't split
name|doSplit
argument_list|(
literal|"à¤¹à¤¿à¤¨à¥à¤¦à¥"
argument_list|,
literal|"à¤¹à¤¿à¤¨à¥à¤¦à¥"
argument_list|)
expr_stmt|;
comment|// don't split non-ascii digits
name|doSplit
argument_list|(
literal|"Ù¡Ù¢Ù£Ù¤"
argument_list|,
literal|"Ù¡Ù¢Ù£Ù¤"
argument_list|)
expr_stmt|;
comment|// don't split supplementaries into unpaired surrogates
name|doSplit
argument_list|(
literal|"ð ð "
argument_list|,
literal|"ð ð "
argument_list|)
expr_stmt|;
block|}
DECL|method|doSplitPossessive
specifier|public
name|void
name|doSplitPossessive
parameter_list|(
name|int
name|stemPossessive
parameter_list|,
specifier|final
name|String
name|input
parameter_list|,
specifier|final
name|String
modifier|...
name|output
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|flags
init|=
name|GENERATE_WORD_PARTS
operator||
name|GENERATE_NUMBER_PARTS
operator||
name|SPLIT_ON_CASE_CHANGE
operator||
name|SPLIT_ON_NUMERICS
decl_stmt|;
name|flags
operator||=
operator|(
name|stemPossessive
operator|==
literal|1
operator|)
condition|?
name|STEM_ENGLISH_POSSESSIVE
else|:
literal|0
expr_stmt|;
name|WordDelimiterFilter
name|wdf
init|=
operator|new
name|WordDelimiterFilter
argument_list|(
name|keywordMockTokenizer
argument_list|(
name|input
argument_list|)
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|wdf
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test option that allows disabling the special "'s" stemming, instead treating the single quote like other delimiters.     */
annotation|@
name|Test
DECL|method|testPossessives
specifier|public
name|void
name|testPossessives
parameter_list|()
throws|throws
name|Exception
block|{
name|doSplitPossessive
argument_list|(
literal|1
argument_list|,
literal|"ra's"
argument_list|,
literal|"ra"
argument_list|)
expr_stmt|;
name|doSplitPossessive
argument_list|(
literal|0
argument_list|,
literal|"ra's"
argument_list|,
literal|"ra"
argument_list|,
literal|"s"
argument_list|)
expr_stmt|;
block|}
comment|/*    * Set a large position increment gap of 10 if the token is "largegap" or "/"    */
DECL|class|LargePosIncTokenFilter
specifier|private
specifier|final
class|class
name|LargePosIncTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|termAtt
specifier|private
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncAtt
specifier|private
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|LargePosIncTokenFilter
specifier|protected
name|LargePosIncTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|termAtt
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"largegap"
argument_list|)
operator|||
name|termAtt
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|10
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testPositionIncrements
specifier|public
name|void
name|testPositionIncrements
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|flags
init|=
name|GENERATE_WORD_PARTS
operator||
name|GENERATE_NUMBER_PARTS
operator||
name|CATENATE_ALL
operator||
name|SPLIT_ON_CASE_CHANGE
operator||
name|SPLIT_ON_NUMERICS
operator||
name|STEM_ENGLISH_POSSESSIVE
decl_stmt|;
specifier|final
name|CharArraySet
name|protWords
init|=
operator|new
name|CharArraySet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"NUTCH"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/* analyzer that uses whitespace + wdf */
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|WordDelimiterFilter
argument_list|(
name|tokenizer
argument_list|,
name|flags
argument_list|,
name|protWords
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/* in this case, works as expected. */
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"LUCENE / SOLR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"SOLR"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
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
block|}
argument_list|)
expr_stmt|;
comment|/* only in this case, posInc of 2 ?! */
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"LUCENE / solR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"sol"
block|,
literal|"solR"
block|,
literal|"R"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|,
literal|9
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|12
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
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"LUCENE / NUTCH SOLR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"NUTCH"
block|,
literal|"SOLR"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|,
literal|15
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|14
block|,
literal|19
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
literal|1
block|}
argument_list|)
expr_stmt|;
comment|/* analyzer that will consume tokens with large position increments */
name|Analyzer
name|a2
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|WordDelimiterFilter
argument_list|(
operator|new
name|LargePosIncTokenFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|,
name|flags
argument_list|,
name|protWords
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|/* increment of "largegap" is preserved */
name|assertAnalyzesTo
argument_list|(
name|a2
argument_list|,
literal|"LUCENE largegap SOLR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"largegap"
block|,
literal|"SOLR"
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
literal|16
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|15
block|,
literal|20
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|10
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
comment|/* the "/" had a position increment of 10, where did it go?!?!! */
name|assertAnalyzesTo
argument_list|(
name|a2
argument_list|,
literal|"LUCENE / SOLR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"SOLR"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
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
literal|11
block|}
argument_list|)
expr_stmt|;
comment|/* in this case, the increment of 10 from the "/" is carried over */
name|assertAnalyzesTo
argument_list|(
name|a2
argument_list|,
literal|"LUCENE / solR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"sol"
block|,
literal|"solR"
block|,
literal|"R"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|,
literal|9
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|12
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
literal|11
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a2
argument_list|,
literal|"LUCENE / NUTCH SOLR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"LUCENE"
block|,
literal|"NUTCH"
block|,
literal|"SOLR"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|,
literal|15
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|14
block|,
literal|19
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|11
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|Analyzer
name|a3
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|StopFilter
name|filter
init|=
operator|new
name|StopFilter
argument_list|(
name|tokenizer
argument_list|,
name|StandardAnalyzer
operator|.
name|STOP_WORDS_SET
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|WordDelimiterFilter
argument_list|(
name|filter
argument_list|,
name|flags
argument_list|,
name|protWords
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a3
argument_list|,
literal|"lucene.solr"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lucene"
block|,
literal|"lucenesolr"
block|,
literal|"solr"
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
literal|7
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|11
block|,
literal|11
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
comment|/* the stopword should add a gap here */
name|assertAnalyzesTo
argument_list|(
name|a3
argument_list|,
literal|"the lucene.solr"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"lucene"
block|,
literal|"lucenesolr"
block|,
literal|"solr"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|4
block|,
literal|11
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|10
block|,
literal|15
block|,
literal|15
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|a
argument_list|,
name|a2
argument_list|,
name|a3
argument_list|)
expr_stmt|;
block|}
comment|/** concat numbers + words + all */
DECL|method|testLotsOfConcatenating
specifier|public
name|void
name|testLotsOfConcatenating
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|flags
init|=
name|GENERATE_WORD_PARTS
operator||
name|GENERATE_NUMBER_PARTS
operator||
name|CATENATE_WORDS
operator||
name|CATENATE_NUMBERS
operator||
name|CATENATE_ALL
operator||
name|SPLIT_ON_CASE_CHANGE
operator||
name|SPLIT_ON_NUMERICS
operator||
name|STEM_ENGLISH_POSSESSIVE
decl_stmt|;
comment|/* analyzer that uses whitespace + wdf */
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|WordDelimiterFilter
argument_list|(
name|tokenizer
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"abc-def-123-456"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abc"
block|,
literal|"abcdef"
block|,
literal|"abcdef123456"
block|,
literal|"def"
block|,
literal|"123"
block|,
literal|"123456"
block|,
literal|"456"
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
literal|4
block|,
literal|8
block|,
literal|8
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|7
block|,
literal|15
block|,
literal|7
block|,
literal|11
block|,
literal|15
block|,
literal|15
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
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** concat numbers + words + all + preserve original */
DECL|method|testLotsOfConcatenating2
specifier|public
name|void
name|testLotsOfConcatenating2
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|flags
init|=
name|PRESERVE_ORIGINAL
operator||
name|GENERATE_WORD_PARTS
operator||
name|GENERATE_NUMBER_PARTS
operator||
name|CATENATE_WORDS
operator||
name|CATENATE_NUMBERS
operator||
name|CATENATE_ALL
operator||
name|SPLIT_ON_CASE_CHANGE
operator||
name|SPLIT_ON_NUMERICS
operator||
name|STEM_ENGLISH_POSSESSIVE
decl_stmt|;
comment|/* analyzer that uses whitespace + wdf */
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|WordDelimiterFilter
argument_list|(
name|tokenizer
argument_list|,
name|flags
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"abc-def-123-456"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abc-def-123-456"
block|,
literal|"abc"
block|,
literal|"abcdef"
block|,
literal|"abcdef123456"
block|,
literal|"def"
block|,
literal|"123"
block|,
literal|"123456"
block|,
literal|"456"
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
literal|4
block|,
literal|8
block|,
literal|8
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|15
block|,
literal|3
block|,
literal|7
block|,
literal|15
block|,
literal|7
block|,
literal|11
block|,
literal|15
block|,
literal|15
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
literal|1
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|5
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|flags
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|512
argument_list|)
decl_stmt|;
specifier|final
name|CharArraySet
name|protectedWords
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
name|protectedWords
operator|=
operator|new
name|CharArraySet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"cd"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|protectedWords
operator|=
literal|null
expr_stmt|;
block|}
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|WordDelimiterFilter
argument_list|(
name|tokenizer
argument_list|,
name|flags
argument_list|,
name|protectedWords
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// TODO: properly support positionLengthAttribute
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|200
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|20
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** blast some enormous random strings through the analyzer */
DECL|method|testRandomHugeStrings
specifier|public
name|void
name|testRandomHugeStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numIterations
init|=
name|atLeast
argument_list|(
literal|5
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
name|numIterations
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|flags
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|512
argument_list|)
decl_stmt|;
specifier|final
name|CharArraySet
name|protectedWords
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
name|protectedWords
operator|=
operator|new
name|CharArraySet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"cd"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|protectedWords
operator|=
literal|null
expr_stmt|;
block|}
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|WordDelimiterFilter
argument_list|(
name|tokenizer
argument_list|,
name|flags
argument_list|,
name|protectedWords
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// TODO: properly support positionLengthAttribute
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|20
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
name|random
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
literal|512
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|flags
init|=
name|i
decl_stmt|;
specifier|final
name|CharArraySet
name|protectedWords
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|protectedWords
operator|=
operator|new
name|CharArraySet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"cd"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|protectedWords
operator|=
literal|null
expr_stmt|;
block|}
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|WordDelimiterFilter
argument_list|(
name|tokenizer
argument_list|,
name|flags
argument_list|,
name|protectedWords
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// depending upon options, this thing may or may not preserve the empty term
name|checkAnalysisConsistency
argument_list|(
name|random
argument_list|,
name|a
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
