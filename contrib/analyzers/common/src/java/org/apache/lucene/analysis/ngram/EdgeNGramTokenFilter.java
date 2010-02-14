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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * Tokenizes the given token into n-grams of given size(s).  *<p>  * This {@link TokenFilter} create n-grams from the beginning edge or ending edge of a input token.  *</p>  */
end_comment
begin_class
DECL|class|EdgeNGramTokenFilter
specifier|public
specifier|final
class|class
name|EdgeNGramTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|DEFAULT_SIDE
specifier|public
specifier|static
specifier|final
name|Side
name|DEFAULT_SIDE
init|=
name|Side
operator|.
name|FRONT
decl_stmt|;
DECL|field|DEFAULT_MAX_GRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_GRAM_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_MIN_GRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_GRAM_SIZE
init|=
literal|1
decl_stmt|;
comment|/** Specifies which side of the input the n-gram should be generated from */
DECL|enum|Side
specifier|public
specifier|static
enum|enum
name|Side
block|{
comment|/** Get the n-gram from the front of the input */
DECL|enum constant|FRONT
name|FRONT
block|{
annotation|@
name|Override
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
literal|"front"
return|;
block|}
block|}
block|,
comment|/** Get the n-gram from the end of the input */
DECL|enum constant|BACK
name|BACK
block|{
annotation|@
name|Override
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
literal|"back"
return|;
block|}
block|}
block|;
DECL|method|getLabel
specifier|public
specifier|abstract
name|String
name|getLabel
parameter_list|()
function_decl|;
comment|// Get the appropriate Side from a string
DECL|method|getSide
specifier|public
specifier|static
name|Side
name|getSide
parameter_list|(
name|String
name|sideName
parameter_list|)
block|{
if|if
condition|(
name|FRONT
operator|.
name|getLabel
argument_list|()
operator|.
name|equals
argument_list|(
name|sideName
argument_list|)
condition|)
block|{
return|return
name|FRONT
return|;
block|}
if|if
condition|(
name|BACK
operator|.
name|getLabel
argument_list|()
operator|.
name|equals
argument_list|(
name|sideName
argument_list|)
condition|)
block|{
return|return
name|BACK
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|field|minGram
specifier|private
specifier|final
name|int
name|minGram
decl_stmt|;
DECL|field|maxGram
specifier|private
specifier|final
name|int
name|maxGram
decl_stmt|;
DECL|field|side
specifier|private
name|Side
name|side
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
DECL|field|tokStart
specifier|private
name|int
name|tokStart
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
comment|/**    * Creates EdgeNGramTokenFilter that can generate n-grams in the sizes of the given range    *    * @param input {@link TokenStream} holding the input to be tokenized    * @param side the {@link Side} from which to chop off an n-gram    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenFilter
specifier|public
name|EdgeNGramTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Side
name|side
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
name|side
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"sideLabel must be either front or back"
argument_list|)
throw|;
block|}
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
name|side
operator|=
name|side
expr_stmt|;
name|this
operator|.
name|termAtt
operator|=
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
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates EdgeNGramTokenFilter that can generate n-grams in the sizes of the given range    *    * @param input {@link TokenStream} holding the input to be tokenized    * @param sideLabel the name of the {@link Side} from which to chop off an n-gram    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenFilter
specifier|public
name|EdgeNGramTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|String
name|sideLabel
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|Side
operator|.
name|getSide
argument_list|(
name|sideLabel
argument_list|)
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
block|}
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
name|tokStart
operator|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|curGramSize
operator|<=
name|maxGram
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|curGramSize
operator|>
name|curTermLength
comment|// if the remaining input is too short, we can't generate any n-grams
operator|||
name|curGramSize
operator|>
name|maxGram
operator|)
condition|)
block|{
comment|// if we have hit the end of our n-gram size range, quit
comment|// grab gramSize chars from front or back
name|int
name|start
init|=
name|side
operator|==
name|Side
operator|.
name|FRONT
condition|?
literal|0
else|:
name|curTermLength
operator|-
name|curGramSize
decl_stmt|;
name|int
name|end
init|=
name|start
operator|+
name|curGramSize
decl_stmt|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|tokStart
operator|+
name|start
argument_list|,
name|tokStart
operator|+
name|end
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|curTermBuffer
argument_list|,
name|start
argument_list|,
name|curGramSize
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
