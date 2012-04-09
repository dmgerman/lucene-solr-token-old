begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.shingle
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|shingle
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
name|LinkedList
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
name|util
operator|.
name|AttributeSource
import|;
end_import
begin_comment
comment|/**  *<p>A ShingleFilter constructs shingles (token n-grams) from a token stream.  * In other words, it creates combinations of tokens as a single token.  *  *<p>For example, the sentence "please divide this sentence into shingles"  * might be tokenized into shingles "please divide", "divide this",  * "this sentence", "sentence into", and "into shingles".  *  *<p>This filter handles position increments> 1 by inserting filler tokens  * (tokens with termtext "_"). It does not handle a position increment of 0.  */
end_comment
begin_class
DECL|class|ShingleFilter
specifier|public
specifier|final
class|class
name|ShingleFilter
extends|extends
name|TokenFilter
block|{
comment|/**    * filler token for when positionIncrement is more than 1    */
DECL|field|FILLER_TOKEN
specifier|public
specifier|static
specifier|final
name|char
index|[]
name|FILLER_TOKEN
init|=
block|{
literal|'_'
block|}
decl_stmt|;
comment|/**    * default maximum shingle size is 2.    */
DECL|field|DEFAULT_MAX_SHINGLE_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_SHINGLE_SIZE
init|=
literal|2
decl_stmt|;
comment|/**    * default minimum shingle size is 2.    */
DECL|field|DEFAULT_MIN_SHINGLE_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_SHINGLE_SIZE
init|=
literal|2
decl_stmt|;
comment|/**    * default token type attribute value is "shingle"     */
DECL|field|DEFAULT_TOKEN_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_TOKEN_TYPE
init|=
literal|"shingle"
decl_stmt|;
comment|/**    * The default string to use when joining adjacent tokens to form a shingle    */
DECL|field|TOKEN_SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_SEPARATOR
init|=
literal|" "
decl_stmt|;
comment|/**    * The sequence of input stream tokens (or filler tokens, if necessary)    * that will be composed to form output shingles.    */
DECL|field|inputWindow
specifier|private
name|LinkedList
argument_list|<
name|InputWindowToken
argument_list|>
name|inputWindow
init|=
operator|new
name|LinkedList
argument_list|<
name|InputWindowToken
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * The number of input tokens in the next output token.  This is the "n" in    * "token n-grams".    */
DECL|field|gramSize
specifier|private
name|CircularSequence
name|gramSize
decl_stmt|;
comment|/**    * Shingle and unigram text is composed here.    */
DECL|field|gramBuilder
specifier|private
name|StringBuilder
name|gramBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|/**    * The token type attribute value to use - default is "shingle"    */
DECL|field|tokenType
specifier|private
name|String
name|tokenType
init|=
name|DEFAULT_TOKEN_TYPE
decl_stmt|;
comment|/**    * The string to use when joining adjacent tokens to form a shingle    */
DECL|field|tokenSeparator
specifier|private
name|String
name|tokenSeparator
init|=
name|TOKEN_SEPARATOR
decl_stmt|;
comment|/**    * By default, we output unigrams (individual tokens) as well as shingles    * (token n-grams).    */
DECL|field|outputUnigrams
specifier|private
name|boolean
name|outputUnigrams
init|=
literal|true
decl_stmt|;
comment|/**    * By default, we don't override behavior of outputUnigrams.    */
DECL|field|outputUnigramsIfNoShingles
specifier|private
name|boolean
name|outputUnigramsIfNoShingles
init|=
literal|false
decl_stmt|;
comment|/**    * maximum shingle size (number of tokens)    */
DECL|field|maxShingleSize
specifier|private
name|int
name|maxShingleSize
decl_stmt|;
comment|/**    * minimum shingle size (number of tokens)    */
DECL|field|minShingleSize
specifier|private
name|int
name|minShingleSize
decl_stmt|;
comment|/**    * The remaining number of filler tokens to be inserted into the input stream    * from which shingles are composed, to handle position increments greater    * than one.    */
DECL|field|numFillerTokensToInsert
specifier|private
name|int
name|numFillerTokensToInsert
decl_stmt|;
comment|/**    * When the next input stream token has a position increment greater than    * one, it is stored in this field until sufficient filler tokens have been    * inserted to account for the position increment.     */
DECL|field|nextInputStreamToken
specifier|private
name|AttributeSource
name|nextInputStreamToken
decl_stmt|;
comment|/**    * Whether or not there is a next input stream token.    */
DECL|field|isNextInputStreamToken
specifier|private
name|boolean
name|isNextInputStreamToken
init|=
literal|false
decl_stmt|;
comment|/**    * Whether at least one unigram or shingle has been output at the current     * position.    */
DECL|field|isOutputHere
specifier|private
name|boolean
name|isOutputHere
init|=
literal|false
decl_stmt|;
comment|/**    * true if no shingles have been output yet (for outputUnigramsIfNoShingles).    */
DECL|field|noShingleOutput
name|boolean
name|noShingleOutput
init|=
literal|true
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
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posLenAtt
specifier|private
specifier|final
name|PositionLengthAttribute
name|posLenAtt
init|=
name|addAttribute
argument_list|(
name|PositionLengthAttribute
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
comment|/**    * Constructs a ShingleFilter with the specified shingle size from the    * {@link TokenStream}<code>input</code>    *    * @param input input stream    * @param minShingleSize minimum shingle size produced by the filter.    * @param maxShingleSize maximum shingle size produced by the filter.    */
DECL|method|ShingleFilter
specifier|public
name|ShingleFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|int
name|minShingleSize
parameter_list|,
name|int
name|maxShingleSize
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|setMaxShingleSize
argument_list|(
name|maxShingleSize
argument_list|)
expr_stmt|;
name|setMinShingleSize
argument_list|(
name|minShingleSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a ShingleFilter with the specified shingle size from the    * {@link TokenStream}<code>input</code>    *    * @param input input stream    * @param maxShingleSize maximum shingle size produced by the filter.    */
DECL|method|ShingleFilter
specifier|public
name|ShingleFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|int
name|maxShingleSize
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|maxShingleSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a ShingleFilter with default shingle size: 2.    *    * @param input input stream    */
DECL|method|ShingleFilter
specifier|public
name|ShingleFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a ShingleFilter with the specified token type for shingle tokens    * and the default shingle size: 2    *    * @param input input stream    * @param tokenType token type for shingle tokens    */
DECL|method|ShingleFilter
specifier|public
name|ShingleFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|String
name|tokenType
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|DEFAULT_MIN_SHINGLE_SIZE
argument_list|,
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|)
expr_stmt|;
name|setTokenType
argument_list|(
name|tokenType
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the type of the shingle tokens produced by this filter.    * (default: "shingle")    *    * @param tokenType token tokenType    */
DECL|method|setTokenType
specifier|public
name|void
name|setTokenType
parameter_list|(
name|String
name|tokenType
parameter_list|)
block|{
name|this
operator|.
name|tokenType
operator|=
name|tokenType
expr_stmt|;
block|}
comment|/**    * Shall the output stream contain the input tokens (unigrams) as well as    * shingles? (default: true.)    *    * @param outputUnigrams Whether or not the output stream shall contain    * the input tokens (unigrams)    */
DECL|method|setOutputUnigrams
specifier|public
name|void
name|setOutputUnigrams
parameter_list|(
name|boolean
name|outputUnigrams
parameter_list|)
block|{
name|this
operator|.
name|outputUnigrams
operator|=
name|outputUnigrams
expr_stmt|;
name|gramSize
operator|=
operator|new
name|CircularSequence
argument_list|()
expr_stmt|;
block|}
comment|/**    *<p>Shall we override the behavior of outputUnigrams==false for those    * times when no shingles are available (because there are fewer than    * minShingleSize tokens in the input stream)? (default: false.)    *<p>Note that if outputUnigrams==true, then unigrams are always output,    * regardless of whether any shingles are available.    *    * @param outputUnigramsIfNoShingles Whether or not to output a single    * unigram when no shingles are available.    */
DECL|method|setOutputUnigramsIfNoShingles
specifier|public
name|void
name|setOutputUnigramsIfNoShingles
parameter_list|(
name|boolean
name|outputUnigramsIfNoShingles
parameter_list|)
block|{
name|this
operator|.
name|outputUnigramsIfNoShingles
operator|=
name|outputUnigramsIfNoShingles
expr_stmt|;
block|}
comment|/**    * Set the max shingle size (default: 2)    *    * @param maxShingleSize max size of output shingles    */
DECL|method|setMaxShingleSize
specifier|public
name|void
name|setMaxShingleSize
parameter_list|(
name|int
name|maxShingleSize
parameter_list|)
block|{
if|if
condition|(
name|maxShingleSize
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Max shingle size must be>= 2"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxShingleSize
operator|=
name|maxShingleSize
expr_stmt|;
block|}
comment|/**    *<p>Set the min shingle size (default: 2).    *<p>This method requires that the passed in minShingleSize is not greater    * than maxShingleSize, so make sure that maxShingleSize is set before    * calling this method.    *<p>The unigram output option is independent of the min shingle size.    *    * @param minShingleSize min size of output shingles    */
DECL|method|setMinShingleSize
specifier|public
name|void
name|setMinShingleSize
parameter_list|(
name|int
name|minShingleSize
parameter_list|)
block|{
if|if
condition|(
name|minShingleSize
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Min shingle size must be>= 2"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minShingleSize
operator|>
name|maxShingleSize
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Min shingle size must be<= max shingle size"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minShingleSize
operator|=
name|minShingleSize
expr_stmt|;
name|gramSize
operator|=
operator|new
name|CircularSequence
argument_list|()
expr_stmt|;
block|}
comment|/**    * Sets the string to use when joining adjacent tokens to form a shingle    * @param tokenSeparator used to separate input stream tokens in output shingles    */
DECL|method|setTokenSeparator
specifier|public
name|void
name|setTokenSeparator
parameter_list|(
name|String
name|tokenSeparator
parameter_list|)
block|{
name|this
operator|.
name|tokenSeparator
operator|=
literal|null
operator|==
name|tokenSeparator
condition|?
literal|""
else|:
name|tokenSeparator
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
name|boolean
name|tokenAvailable
init|=
literal|false
decl_stmt|;
name|int
name|builtGramSize
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|gramSize
operator|.
name|atMinValue
argument_list|()
operator|||
name|inputWindow
operator|.
name|size
argument_list|()
operator|<
name|gramSize
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|shiftInputWindow
argument_list|()
expr_stmt|;
name|gramBuilder
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builtGramSize
operator|=
name|gramSize
operator|.
name|getPreviousValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|inputWindow
operator|.
name|size
argument_list|()
operator|>=
name|gramSize
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|boolean
name|isAllFiller
init|=
literal|true
decl_stmt|;
name|InputWindowToken
name|nextToken
init|=
literal|null
decl_stmt|;
name|Iterator
argument_list|<
name|InputWindowToken
argument_list|>
name|iter
init|=
name|inputWindow
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|gramNum
init|=
literal|1
init|;
name|iter
operator|.
name|hasNext
argument_list|()
operator|&&
name|builtGramSize
operator|<
name|gramSize
operator|.
name|getValue
argument_list|()
condition|;
operator|++
name|gramNum
control|)
block|{
name|nextToken
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|builtGramSize
operator|<
name|gramNum
condition|)
block|{
if|if
condition|(
name|builtGramSize
operator|>
literal|0
condition|)
block|{
name|gramBuilder
operator|.
name|append
argument_list|(
name|tokenSeparator
argument_list|)
expr_stmt|;
block|}
name|gramBuilder
operator|.
name|append
argument_list|(
name|nextToken
operator|.
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|nextToken
operator|.
name|termAtt
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
operator|++
name|builtGramSize
expr_stmt|;
block|}
if|if
condition|(
name|isAllFiller
operator|&&
name|nextToken
operator|.
name|isFiller
condition|)
block|{
if|if
condition|(
name|gramNum
operator|==
name|gramSize
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|gramSize
operator|.
name|advance
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|isAllFiller
operator|=
literal|false
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|isAllFiller
operator|&&
name|builtGramSize
operator|==
name|gramSize
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|inputWindow
operator|.
name|getFirst
argument_list|()
operator|.
name|attSource
operator|.
name|copyTo
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|isOutputHere
condition|?
literal|0
else|:
literal|1
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|gramBuilder
argument_list|)
expr_stmt|;
if|if
condition|(
name|gramSize
operator|.
name|getValue
argument_list|()
operator|>
literal|1
condition|)
block|{
name|typeAtt
operator|.
name|setType
argument_list|(
name|tokenType
argument_list|)
expr_stmt|;
name|noShingleOutput
operator|=
literal|false
expr_stmt|;
block|}
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|nextToken
operator|.
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|posLenAtt
operator|.
name|setPositionLength
argument_list|(
name|builtGramSize
argument_list|)
expr_stmt|;
name|isOutputHere
operator|=
literal|true
expr_stmt|;
name|gramSize
operator|.
name|advance
argument_list|()
expr_stmt|;
name|tokenAvailable
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|tokenAvailable
return|;
block|}
DECL|field|exhausted
specifier|private
name|boolean
name|exhausted
decl_stmt|;
comment|/**    *<p>Get the next token from the input stream.    *<p>If the next token has<code>positionIncrement> 1</code>,    *<code>positionIncrement - 1</code> {@link #FILLER_TOKEN}s are    * inserted first.    * @param target Where to put the new token; if null, a new instance is created.    * @return On success, the populated token; null otherwise    * @throws IOException if the input stream has a problem    */
DECL|method|getNextToken
specifier|private
name|InputWindowToken
name|getNextToken
parameter_list|(
name|InputWindowToken
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|InputWindowToken
name|newTarget
init|=
name|target
decl_stmt|;
if|if
condition|(
name|numFillerTokensToInsert
operator|>
literal|0
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|target
condition|)
block|{
name|newTarget
operator|=
operator|new
name|InputWindowToken
argument_list|(
name|nextInputStreamToken
operator|.
name|cloneAttributes
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nextInputStreamToken
operator|.
name|copyTo
argument_list|(
name|target
operator|.
name|attSource
argument_list|)
expr_stmt|;
block|}
comment|// A filler token occupies no space
name|newTarget
operator|.
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|newTarget
operator|.
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|newTarget
operator|.
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|newTarget
operator|.
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|FILLER_TOKEN
argument_list|,
literal|0
argument_list|,
name|FILLER_TOKEN
operator|.
name|length
argument_list|)
expr_stmt|;
name|newTarget
operator|.
name|isFiller
operator|=
literal|true
expr_stmt|;
operator|--
name|numFillerTokensToInsert
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isNextInputStreamToken
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|target
condition|)
block|{
name|newTarget
operator|=
operator|new
name|InputWindowToken
argument_list|(
name|nextInputStreamToken
operator|.
name|cloneAttributes
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nextInputStreamToken
operator|.
name|copyTo
argument_list|(
name|target
operator|.
name|attSource
argument_list|)
expr_stmt|;
block|}
name|isNextInputStreamToken
operator|=
literal|false
expr_stmt|;
name|newTarget
operator|.
name|isFiller
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|exhausted
operator|&&
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|target
condition|)
block|{
name|newTarget
operator|=
operator|new
name|InputWindowToken
argument_list|(
name|cloneAttributes
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|copyTo
argument_list|(
name|target
operator|.
name|attSource
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// Each output shingle must contain at least one input token,
comment|// so no more than (maxShingleSize - 1) filler tokens will be inserted.
name|numFillerTokensToInsert
operator|=
name|Math
operator|.
name|min
argument_list|(
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|-
literal|1
argument_list|,
name|maxShingleSize
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Save the current token as the next input stream token
if|if
condition|(
literal|null
operator|==
name|nextInputStreamToken
condition|)
block|{
name|nextInputStreamToken
operator|=
name|cloneAttributes
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|copyTo
argument_list|(
name|nextInputStreamToken
argument_list|)
expr_stmt|;
block|}
name|isNextInputStreamToken
operator|=
literal|true
expr_stmt|;
comment|// A filler token occupies no space
name|newTarget
operator|.
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|newTarget
operator|.
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|FILLER_TOKEN
argument_list|,
literal|0
argument_list|,
name|FILLER_TOKEN
operator|.
name|length
argument_list|)
expr_stmt|;
name|newTarget
operator|.
name|isFiller
operator|=
literal|true
expr_stmt|;
operator|--
name|numFillerTokensToInsert
expr_stmt|;
block|}
else|else
block|{
name|newTarget
operator|.
name|isFiller
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
name|newTarget
operator|=
literal|null
expr_stmt|;
name|exhausted
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|newTarget
return|;
block|}
comment|/**    *<p>Fills {@link #inputWindow} with input stream tokens, if available,     * shifting to the right if the window was previously full.    *<p>Resets {@link #gramSize} to its minimum value.    *    * @throws IOException if there's a problem getting the next token    */
DECL|method|shiftInputWindow
specifier|private
name|void
name|shiftInputWindow
parameter_list|()
throws|throws
name|IOException
block|{
name|InputWindowToken
name|firstToken
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|inputWindow
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|firstToken
operator|=
name|inputWindow
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|inputWindow
operator|.
name|size
argument_list|()
operator|<
name|maxShingleSize
condition|)
block|{
if|if
condition|(
literal|null
operator|!=
name|firstToken
condition|)
block|{
comment|// recycle the firstToken, if available
if|if
condition|(
literal|null
operator|!=
name|getNextToken
argument_list|(
name|firstToken
argument_list|)
condition|)
block|{
name|inputWindow
operator|.
name|add
argument_list|(
name|firstToken
argument_list|)
expr_stmt|;
comment|// the firstToken becomes the last
name|firstToken
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
break|break;
comment|// end of input stream
block|}
block|}
else|else
block|{
name|InputWindowToken
name|nextToken
init|=
name|getNextToken
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|nextToken
condition|)
block|{
name|inputWindow
operator|.
name|add
argument_list|(
name|nextToken
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
comment|// end of input stream
block|}
block|}
block|}
if|if
condition|(
name|outputUnigramsIfNoShingles
operator|&&
name|noShingleOutput
operator|&&
name|gramSize
operator|.
name|minValue
operator|>
literal|1
operator|&&
name|inputWindow
operator|.
name|size
argument_list|()
operator|<
name|minShingleSize
condition|)
block|{
name|gramSize
operator|.
name|minValue
operator|=
literal|1
expr_stmt|;
block|}
name|gramSize
operator|.
name|reset
argument_list|()
expr_stmt|;
name|isOutputHere
operator|=
literal|false
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
name|gramSize
operator|.
name|reset
argument_list|()
expr_stmt|;
name|inputWindow
operator|.
name|clear
argument_list|()
expr_stmt|;
name|numFillerTokensToInsert
operator|=
literal|0
expr_stmt|;
name|isOutputHere
operator|=
literal|false
expr_stmt|;
name|noShingleOutput
operator|=
literal|true
expr_stmt|;
name|exhausted
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|outputUnigramsIfNoShingles
operator|&&
operator|!
name|outputUnigrams
condition|)
block|{
comment|// Fix up gramSize if minValue was reset for outputUnigramsIfNoShingles
name|gramSize
operator|.
name|minValue
operator|=
name|minShingleSize
expr_stmt|;
block|}
block|}
comment|/**    *<p>An instance of this class is used to maintain the number of input    * stream tokens that will be used to compose the next unigram or shingle:    * {@link #gramSize}.    *<p><code>gramSize</code> will take on values from the circular sequence    *<b>{ [ 1, ] {@link #minShingleSize} [ , ... , {@link #maxShingleSize} ] }</b>.    *<p>1 is included in the circular sequence only if     * {@link #outputUnigrams} = true.    */
DECL|class|CircularSequence
specifier|private
class|class
name|CircularSequence
block|{
DECL|field|value
specifier|private
name|int
name|value
decl_stmt|;
DECL|field|previousValue
specifier|private
name|int
name|previousValue
decl_stmt|;
DECL|field|minValue
specifier|private
name|int
name|minValue
decl_stmt|;
DECL|method|CircularSequence
specifier|public
name|CircularSequence
parameter_list|()
block|{
name|minValue
operator|=
name|outputUnigrams
condition|?
literal|1
else|:
name|minShingleSize
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return the current value.        * @see #advance()      */
DECL|method|getValue
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**      *<p>Increments this circular number's value to the next member in the      * circular sequence      *<code>gramSize</code> will take on values from the circular sequence      *<b>{ [ 1, ] {@link #minShingleSize} [ , ... , {@link #maxShingleSize} ] }</b>.      *<p>1 is included in the circular sequence only if       * {@link #outputUnigrams} = true.      */
DECL|method|advance
specifier|public
name|void
name|advance
parameter_list|()
block|{
name|previousValue
operator|=
name|value
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|1
condition|)
block|{
name|value
operator|=
name|minShingleSize
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
name|maxShingleSize
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
operator|++
name|value
expr_stmt|;
block|}
block|}
comment|/**      *<p>Sets this circular number's value to the first member of the       * circular sequence      *<p><code>gramSize</code> will take on values from the circular sequence      *<b>{ [ 1, ] {@link #minShingleSize} [ , ... , {@link #maxShingleSize} ] }</b>.      *<p>1 is included in the circular sequence only if       * {@link #outputUnigrams} = true.      */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|previousValue
operator|=
name|value
operator|=
name|minValue
expr_stmt|;
block|}
comment|/**      *<p>Returns true if the current value is the first member of the circular      * sequence.      *<p>If {@link #outputUnigrams} = true, the first member of the circular      * sequence will be 1; otherwise, it will be {@link #minShingleSize}.      *       * @return true if the current value is the first member of the circular      *  sequence; false otherwise      */
DECL|method|atMinValue
specifier|public
name|boolean
name|atMinValue
parameter_list|()
block|{
return|return
name|value
operator|==
name|minValue
return|;
block|}
comment|/**      * @return the value this instance had before the last advance() call      */
DECL|method|getPreviousValue
specifier|public
name|int
name|getPreviousValue
parameter_list|()
block|{
return|return
name|previousValue
return|;
block|}
block|}
DECL|class|InputWindowToken
specifier|private
class|class
name|InputWindowToken
block|{
DECL|field|attSource
specifier|final
name|AttributeSource
name|attSource
decl_stmt|;
DECL|field|termAtt
specifier|final
name|CharTermAttribute
name|termAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|final
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|field|isFiller
name|boolean
name|isFiller
init|=
literal|false
decl_stmt|;
DECL|method|InputWindowToken
specifier|public
name|InputWindowToken
parameter_list|(
name|AttributeSource
name|attSource
parameter_list|)
block|{
name|this
operator|.
name|attSource
operator|=
name|attSource
expr_stmt|;
name|this
operator|.
name|termAtt
operator|=
name|attSource
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|offsetAtt
operator|=
name|attSource
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
