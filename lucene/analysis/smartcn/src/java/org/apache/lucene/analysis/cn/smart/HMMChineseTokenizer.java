begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.cn.smart
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
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
name|Iterator
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
name|cn
operator|.
name|smart
operator|.
name|hhmm
operator|.
name|SegToken
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
name|TypeAttribute
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
name|SegmentingTokenizerBase
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
name|AttributeFactory
import|;
end_import
begin_comment
comment|/**  * Tokenizer for Chinese or mixed Chinese-English text.  *<p>  * The analyzer uses probabilistic knowledge to find the optimal word segmentation for Simplified Chinese text.  * The text is first broken into sentences, then each sentence is segmented into words.  */
end_comment
begin_class
DECL|class|HMMChineseTokenizer
specifier|public
class|class
name|HMMChineseTokenizer
extends|extends
name|SegmentingTokenizerBase
block|{
comment|/** used for breaking the text into sentences */
DECL|field|sentenceProto
specifier|private
specifier|static
specifier|final
name|BreakIterator
name|sentenceProto
init|=
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
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
DECL|field|typeAtt
specifier|private
specifier|final
name|TypeAttribute
name|typeAtt
init|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|wordSegmenter
specifier|private
specifier|final
name|WordSegmenter
name|wordSegmenter
init|=
operator|new
name|WordSegmenter
argument_list|()
decl_stmt|;
DECL|field|tokens
specifier|private
name|Iterator
argument_list|<
name|SegToken
argument_list|>
name|tokens
decl_stmt|;
comment|/** Creates a new HMMChineseTokenizer */
DECL|method|HMMChineseTokenizer
specifier|public
name|HMMChineseTokenizer
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_TOKEN_ATTRIBUTE_FACTORY
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a new HMMChineseTokenizer, supplying the AttributeFactory */
DECL|method|HMMChineseTokenizer
specifier|public
name|HMMChineseTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
operator|(
name|BreakIterator
operator|)
name|sentenceProto
operator|.
name|clone
argument_list|()
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
name|String
name|sentence
init|=
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
name|sentenceStart
argument_list|,
name|sentenceEnd
operator|-
name|sentenceStart
argument_list|)
decl_stmt|;
name|tokens
operator|=
name|wordSegmenter
operator|.
name|segmentSentence
argument_list|(
name|sentence
argument_list|,
name|offset
operator|+
name|sentenceStart
argument_list|)
operator|.
name|iterator
argument_list|()
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
name|tokens
operator|==
literal|null
operator|||
operator|!
name|tokens
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|SegToken
name|token
init|=
name|tokens
operator|.
name|next
argument_list|()
decl_stmt|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|token
operator|.
name|charArray
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|charArray
operator|.
name|length
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|token
operator|.
name|startOffset
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|token
operator|.
name|endOffset
argument_list|)
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
literal|"word"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
name|tokens
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
