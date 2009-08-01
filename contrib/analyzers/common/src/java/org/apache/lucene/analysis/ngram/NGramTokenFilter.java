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
name|TermAttribute
import|;
end_import
begin_comment
comment|/**  * Tokenizes the input into n-grams of the given size(s).  */
end_comment
begin_class
DECL|class|NGramTokenFilter
specifier|public
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
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
comment|/**    * Creates NGramTokenFilter with given min and max n-grams.    * @param input TokenStream holding the input to be tokenized    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|NGramTokenFilter
specifier|public
name|NGramTokenFilter
parameter_list|(
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
name|input
argument_list|)
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
name|this
operator|.
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|offsetAtt
operator|=
operator|(
name|OffsetAttribute
operator|)
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates NGramTokenFilter with default min and max n-grams.    * @param input TokenStream holding the input to be tokenized    */
DECL|method|NGramTokenFilter
specifier|public
name|NGramTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|DEFAULT_MIN_NGRAM_SIZE
argument_list|,
name|DEFAULT_MAX_NGRAM_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the next token in the stream, or null at EOS. */
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
operator|(
name|char
index|[]
operator|)
name|termAtt
operator|.
name|termBuffer
argument_list|()
operator|.
name|clone
argument_list|()
expr_stmt|;
name|curTermLength
operator|=
name|termAtt
operator|.
name|termLength
argument_list|()
expr_stmt|;
name|curGramSize
operator|=
name|minGram
expr_stmt|;
name|curPos
operator|=
literal|0
expr_stmt|;
block|}
block|}
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
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|curTermBuffer
argument_list|,
name|curPos
argument_list|,
name|curGramSize
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|curPos
argument_list|,
name|curPos
operator|+
name|curGramSize
argument_list|)
expr_stmt|;
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
name|curTermBuffer
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** @deprecated Will be removed in Lucene 3.0. This method is final, as it should    * not be overridden. Delegates to the backwards compatibility layer. */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
return|return
name|super
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
return|;
block|}
comment|/** @deprecated Will be removed in Lucene 3.0. This method is final, as it should    * not be overridden. Delegates to the backwards compatibility layer. */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
return|return
name|super
operator|.
name|next
argument_list|()
return|;
block|}
block|}
end_class
end_unit
