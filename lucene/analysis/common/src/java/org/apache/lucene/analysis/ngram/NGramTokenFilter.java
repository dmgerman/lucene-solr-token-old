begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ngram
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ngram
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenFilter
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
name|miscellaneous
operator|.
name|CodepointCountFilter
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionLengthAttribute
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
name|CharacterUtils
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
comment|/**  * Tokenizes the input into n-grams of the given size(s).  *<a name="version"/>  *<p>You must specify the required {@link Version} compatibility when  * creating a {@link NGramTokenFilter}. As of Lucene 4.4, this token filters:<ul>  *<li>handles supplementary characters correctly,</li>  *<li>emits all n-grams for the same token at the same position,</li>  *<li>does not modify offsets,</li>  *<li>sorts n-grams by their offset in the original token first, then  * increasing length (meaning that "abc" will give "a", "ab", "abc", "b", "bc",  * "c").</li></ul>  *<p>You can make this filter use the old behavior by providing a version&lt;  * {@link Version#LUCENE_4_4} in the constructor but this is not recommended as  * it will lead to broken {@link TokenStream}s that will cause highlighting  * bugs.  *<p>If you were using this {@link TokenFilter} to perform partial highlighting,  * this won't work anymore since this filter doesn't update offsets. You should  * modify your analysis chain to use {@link NGramTokenizer}, and potentially  * override {@link NGramTokenizer#isTokenChar(int)} to perform pre-tokenization.  */
end_comment
begin_class
DECL|class|NGramTokenFilter
specifier|public
specifier|final
class|class
name|NGramTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|DEFAULT_MIN_NGRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_NGRAM_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_MAX_NGRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_NGRAM_SIZE
init|=
literal|2
decl_stmt|;
DECL|field|minGram
DECL|field|maxGram
specifier|private
specifier|final
name|int
name|minGram
decl_stmt|,
name|maxGram
decl_stmt|;
DECL|field|curTermBuffer
specifier|private
name|char
index|[]
name|curTermBuffer
decl_stmt|;
DECL|field|curTermLength
specifier|private
name|int
name|curTermLength
decl_stmt|;
DECL|field|curCodePointCount
specifier|private
name|int
name|curCodePointCount
decl_stmt|;
DECL|field|curGramSize
specifier|private
name|int
name|curGramSize
decl_stmt|;
DECL|field|curPos
specifier|private
name|int
name|curPos
decl_stmt|;
DECL|field|curPosInc
DECL|field|curPosLen
specifier|private
name|int
name|curPosInc
decl_stmt|,
name|curPosLen
decl_stmt|;
DECL|field|tokStart
specifier|private
name|int
name|tokStart
decl_stmt|;
DECL|field|tokEnd
specifier|private
name|int
name|tokEnd
decl_stmt|;
DECL|field|hasIllegalOffsets
specifier|private
name|boolean
name|hasIllegalOffsets
decl_stmt|;
comment|// only if the length changed before this filter
DECL|field|version
specifier|private
specifier|final
name|Version
name|version
decl_stmt|;
DECL|field|charUtils
specifier|private
specifier|final
name|CharacterUtils
name|charUtils
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
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
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
decl_stmt|;
DECL|field|posLenAtt
specifier|private
specifier|final
name|PositionLengthAttribute
name|posLenAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
specifier|final
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
comment|/**    * Creates NGramTokenFilter with given min and max n-grams.    * @param version Lucene version to enable correct position increments.    *                See<a href="#version">above</a> for details.    * @param input {@link TokenStream} holding the input to be tokenized    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|NGramTokenFilter
specifier|public
name|NGramTokenFilter
parameter_list|(
name|Version
name|version
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|CodepointCountFilter
argument_list|(
name|version
argument_list|,
name|input
argument_list|,
name|minGram
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|charUtils
operator|=
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_4_4
argument_list|)
condition|?
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|version
argument_list|)
else|:
name|CharacterUtils
operator|.
name|getJava4Instance
argument_list|()
expr_stmt|;
if|if
condition|(
name|minGram
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minGram must be greater than zero"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minGram
operator|>
name|maxGram
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minGram must not be greater than maxGram"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minGram
operator|=
name|minGram
expr_stmt|;
name|this
operator|.
name|maxGram
operator|=
name|maxGram
expr_stmt|;
if|if
condition|(
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_4_4
argument_list|)
condition|)
block|{
name|posIncAtt
operator|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posLenAtt
operator|=
name|addAttribute
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posIncAtt
operator|=
operator|new
name|PositionIncrementAttribute
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setPositionIncrement
parameter_list|(
name|int
name|positionIncrement
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|int
name|getPositionIncrement
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
expr_stmt|;
name|posLenAtt
operator|=
operator|new
name|PositionLengthAttribute
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setPositionLength
parameter_list|(
name|int
name|positionLength
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|int
name|getPositionLength
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
expr_stmt|;
block|}
block|}
comment|/**    * Creates NGramTokenFilter with default min and max n-grams.    * @param version Lucene version to enable correct position increments.    *                See<a href="#version">above</a> for details.    * @param input {@link TokenStream} holding the input to be tokenized    */
DECL|method|NGramTokenFilter
specifier|public
name|NGramTokenFilter
parameter_list|(
name|Version
name|version
parameter_list|,
name|TokenStream
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|version
argument_list|,
name|input
argument_list|,
name|DEFAULT_MIN_NGRAM_SIZE
argument_list|,
name|DEFAULT_MAX_NGRAM_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the next token in the stream, or null at EOS. */
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|curTermBuffer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|curTermBuffer
operator|=
name|termAtt
operator|.
name|buffer
argument_list|()
operator|.
name|clone
argument_list|()
expr_stmt|;
name|curTermLength
operator|=
name|termAtt
operator|.
name|length
argument_list|()
expr_stmt|;
name|curCodePointCount
operator|=
name|charUtils
operator|.
name|codePointCount
argument_list|(
name|termAtt
argument_list|)
expr_stmt|;
name|curGramSize
operator|=
name|minGram
expr_stmt|;
name|curPos
operator|=
literal|0
expr_stmt|;
name|curPosInc
operator|=
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
name|curPosLen
operator|=
name|posLenAtt
operator|.
name|getPositionLength
argument_list|()
expr_stmt|;
name|tokStart
operator|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|tokEnd
operator|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
expr_stmt|;
comment|// if length by start + end offsets doesn't match the term text then assume
comment|// this is a synonym and don't adjust the offsets.
name|hasIllegalOffsets
operator|=
operator|(
name|tokStart
operator|+
name|curTermLength
operator|)
operator|!=
name|tokEnd
expr_stmt|;
block|}
block|}
if|if
condition|(
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_4_4
argument_list|)
condition|)
block|{
if|if
condition|(
name|curGramSize
operator|>
name|maxGram
operator|||
operator|(
name|curPos
operator|+
name|curGramSize
operator|)
operator|>
name|curCodePointCount
condition|)
block|{
operator|++
name|curPos
expr_stmt|;
name|curGramSize
operator|=
name|minGram
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|curPos
operator|+
name|curGramSize
operator|)
operator|<=
name|curCodePointCount
condition|)
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
specifier|final
name|int
name|start
init|=
name|charUtils
operator|.
name|offsetByCodePoints
argument_list|(
name|curTermBuffer
argument_list|,
literal|0
argument_list|,
name|curTermLength
argument_list|,
literal|0
argument_list|,
name|curPos
argument_list|)
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|charUtils
operator|.
name|offsetByCodePoints
argument_list|(
name|curTermBuffer
argument_list|,
literal|0
argument_list|,
name|curTermLength
argument_list|,
name|start
argument_list|,
name|curGramSize
argument_list|)
decl_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|curTermBuffer
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
name|curPosInc
argument_list|)
expr_stmt|;
name|curPosInc
operator|=
literal|0
expr_stmt|;
name|posLenAtt
operator|.
name|setPositionLength
argument_list|(
name|curPosLen
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|tokStart
argument_list|,
name|tokEnd
argument_list|)
expr_stmt|;
name|curGramSize
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
while|while
condition|(
name|curGramSize
operator|<=
name|maxGram
condition|)
block|{
while|while
condition|(
name|curPos
operator|+
name|curGramSize
operator|<=
name|curTermLength
condition|)
block|{
comment|// while there is input
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|curTermBuffer
argument_list|,
name|curPos
argument_list|,
name|curGramSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasIllegalOffsets
condition|)
block|{
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|tokStart
argument_list|,
name|tokEnd
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|tokStart
operator|+
name|curPos
argument_list|,
name|tokStart
operator|+
name|curPos
operator|+
name|curGramSize
argument_list|)
expr_stmt|;
block|}
name|curPos
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
name|curGramSize
operator|++
expr_stmt|;
comment|// increase n-gram size
name|curPos
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|curTermBuffer
operator|=
literal|null
expr_stmt|;
block|}
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
name|curTermBuffer
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
