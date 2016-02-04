begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
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
name|text
operator|.
name|BreakIterator
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
name|java
operator|.
name|util
operator|.
name|Locale
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|util
operator|.
name|IOUtils
import|;
end_import
begin_comment
comment|/** Basic tests for {@link SegmentingTokenizerBase} */
end_comment
begin_class
DECL|class|TestSegmentingTokenizerBase
specifier|public
class|class
name|TestSegmentingTokenizerBase
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|sentence
DECL|field|sentenceAndWord
specifier|private
name|Analyzer
name|sentence
decl_stmt|,
name|sentenceAndWord
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
name|sentence
operator|=
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
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|WholeSentenceTokenizer
argument_list|()
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|sentenceAndWord
operator|=
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
return|return
operator|new
name|TokenStreamComponents
argument_list|(
operator|new
name|SentenceAndWordTokenizer
argument_list|()
argument_list|)
return|;
block|}
block|}
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
name|IOUtils
operator|.
name|close
argument_list|(
name|sentence
argument_list|,
name|sentenceAndWord
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/** Some simple examples, just outputting the whole sentence boundaries as "terms" */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|sentence
argument_list|,
literal|"The acronym for United States is U.S. but this doesn't end a sentence"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"The acronym for United States is U.S. but this doesn't end a sentence"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sentence
argument_list|,
literal|"He said, \"Are you going?\" John shook his head."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"He said, \"Are you going?\" "
block|,
literal|"John shook his head."
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test a subclass that sets some custom attribute values */
DECL|method|testCustomAttributes
specifier|public
name|void
name|testCustomAttributes
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|sentenceAndWord
argument_list|,
literal|"He said, \"Are you going?\" John shook his head."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"He"
block|,
literal|"said"
block|,
literal|"Are"
block|,
literal|"you"
block|,
literal|"going"
block|,
literal|"John"
block|,
literal|"shook"
block|,
literal|"his"
block|,
literal|"head"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|10
block|,
literal|14
block|,
literal|18
block|,
literal|26
block|,
literal|31
block|,
literal|37
block|,
literal|41
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|7
block|,
literal|13
block|,
literal|17
block|,
literal|23
block|,
literal|30
block|,
literal|36
block|,
literal|40
block|,
literal|45
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
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Tests tokenstream reuse */
DECL|method|testReuse
specifier|public
name|void
name|testReuse
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|sentenceAndWord
argument_list|,
literal|"He said, \"Are you going?\""
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"He"
block|,
literal|"said"
block|,
literal|"Are"
block|,
literal|"you"
block|,
literal|"going"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|10
block|,
literal|14
block|,
literal|18
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|7
block|,
literal|13
block|,
literal|17
block|,
literal|23
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
block|,
literal|1
block|,
literal|1
block|,}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sentenceAndWord
argument_list|,
literal|"John shook his head."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"John"
block|,
literal|"shook"
block|,
literal|"his"
block|,
literal|"head"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|5
block|,
literal|11
block|,
literal|15
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|10
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
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Tests TokenStream.end() */
DECL|method|testEnd
specifier|public
name|void
name|testEnd
parameter_list|()
throws|throws
name|IOException
block|{
comment|// BaseTokenStreamTestCase asserts that end() is set to our StringReader's length for us here.
comment|// we add some junk whitespace to the end just to test it.
name|assertAnalyzesTo
argument_list|(
name|sentenceAndWord
argument_list|,
literal|"John shook his head          "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"John"
block|,
literal|"shook"
block|,
literal|"his"
block|,
literal|"head"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sentenceAndWord
argument_list|,
literal|"John shook his head.          "
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"John"
block|,
literal|"shook"
block|,
literal|"his"
block|,
literal|"head"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Tests terms which span across boundaries */
DECL|method|testHugeDoc
specifier|public
name|void
name|testHugeDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
name|whitespace
index|[]
init|=
operator|new
name|char
index|[
literal|4094
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|whitespace
argument_list|,
literal|'\n'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|whitespace
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"testing 1234"
argument_list|)
expr_stmt|;
name|String
name|input
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sentenceAndWord
argument_list|,
name|input
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"testing"
block|,
literal|"1234"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the handling of binary/malformed data */
DECL|method|testHugeTerm
specifier|public
name|void
name|testHugeTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
literal|10240
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
block|}
name|String
name|input
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|char
name|token
index|[]
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|token
argument_list|,
literal|'a'
argument_list|)
expr_stmt|;
name|String
name|expectedToken
init|=
operator|new
name|String
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|String
name|expected
index|[]
init|=
block|{
name|expectedToken
block|,
name|expectedToken
block|,
name|expectedToken
block|,
name|expectedToken
block|,
name|expectedToken
block|,
name|expectedToken
block|,
name|expectedToken
block|,
name|expectedToken
block|,
name|expectedToken
block|,
name|expectedToken
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|sentence
argument_list|,
name|input
argument_list|,
name|expected
argument_list|)
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
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|sentence
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|sentenceAndWord
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
comment|// some tokenizers for testing
comment|/** silly tokenizer that just returns whole sentences as tokens */
DECL|class|WholeSentenceTokenizer
specifier|static
class|class
name|WholeSentenceTokenizer
extends|extends
name|SegmentingTokenizerBase
block|{
DECL|field|sentenceStart
DECL|field|sentenceEnd
name|int
name|sentenceStart
decl_stmt|,
name|sentenceEnd
decl_stmt|;
DECL|field|hasSentence
name|boolean
name|hasSentence
decl_stmt|;
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
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|WholeSentenceTokenizer
specifier|public
name|WholeSentenceTokenizer
parameter_list|()
block|{
name|super
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|,
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextSentence
specifier|protected
name|void
name|setNextSentence
parameter_list|(
name|int
name|sentenceStart
parameter_list|,
name|int
name|sentenceEnd
parameter_list|)
block|{
name|this
operator|.
name|sentenceStart
operator|=
name|sentenceStart
expr_stmt|;
name|this
operator|.
name|sentenceEnd
operator|=
name|sentenceEnd
expr_stmt|;
name|hasSentence
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementWord
specifier|protected
name|boolean
name|incrementWord
parameter_list|()
block|{
if|if
condition|(
name|hasSentence
condition|)
block|{
name|hasSentence
operator|=
literal|false
expr_stmt|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|buffer
argument_list|,
name|sentenceStart
argument_list|,
name|sentenceEnd
operator|-
name|sentenceStart
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|offset
operator|+
name|sentenceStart
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|offset
operator|+
name|sentenceEnd
argument_list|)
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
comment|/**     * simple tokenizer, that bumps posinc + 1 for tokens after a     * sentence boundary to inhibit phrase queries without slop.    */
DECL|class|SentenceAndWordTokenizer
specifier|static
class|class
name|SentenceAndWordTokenizer
extends|extends
name|SegmentingTokenizerBase
block|{
DECL|field|sentenceStart
DECL|field|sentenceEnd
name|int
name|sentenceStart
decl_stmt|,
name|sentenceEnd
decl_stmt|;
DECL|field|wordStart
DECL|field|wordEnd
name|int
name|wordStart
decl_stmt|,
name|wordEnd
decl_stmt|;
DECL|field|posBoost
name|int
name|posBoost
init|=
operator|-
literal|1
decl_stmt|;
comment|// initially set to -1 so the first word in the document doesn't get a pos boost
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
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
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
DECL|method|SentenceAndWordTokenizer
specifier|public
name|SentenceAndWordTokenizer
parameter_list|()
block|{
name|super
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|,
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextSentence
specifier|protected
name|void
name|setNextSentence
parameter_list|(
name|int
name|sentenceStart
parameter_list|,
name|int
name|sentenceEnd
parameter_list|)
block|{
name|this
operator|.
name|wordStart
operator|=
name|this
operator|.
name|wordEnd
operator|=
name|this
operator|.
name|sentenceStart
operator|=
name|sentenceStart
expr_stmt|;
name|this
operator|.
name|sentenceEnd
operator|=
name|sentenceEnd
expr_stmt|;
name|posBoost
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|posBoost
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementWord
specifier|protected
name|boolean
name|incrementWord
parameter_list|()
block|{
name|wordStart
operator|=
name|wordEnd
expr_stmt|;
while|while
condition|(
name|wordStart
operator|<
name|sentenceEnd
condition|)
block|{
if|if
condition|(
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|buffer
index|[
name|wordStart
index|]
argument_list|)
condition|)
break|break;
name|wordStart
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|wordStart
operator|==
name|sentenceEnd
condition|)
return|return
literal|false
return|;
name|wordEnd
operator|=
name|wordStart
operator|+
literal|1
expr_stmt|;
while|while
condition|(
name|wordEnd
operator|<
name|sentenceEnd
operator|&&
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|buffer
index|[
name|wordEnd
index|]
argument_list|)
condition|)
name|wordEnd
operator|++
expr_stmt|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|buffer
argument_list|,
name|wordStart
argument_list|,
name|wordEnd
operator|-
name|wordStart
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|offset
operator|+
name|wordStart
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|offset
operator|+
name|wordEnd
argument_list|)
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|+
name|posBoost
argument_list|)
expr_stmt|;
name|posBoost
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class
end_unit
