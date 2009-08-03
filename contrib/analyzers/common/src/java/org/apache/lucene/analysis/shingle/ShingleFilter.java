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
name|TermAttribute
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
class|class
name|ShingleFilter
extends|extends
name|TokenFilter
block|{
DECL|field|shingleBuf
specifier|private
name|LinkedList
name|shingleBuf
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
DECL|field|shingles
specifier|private
name|StringBuffer
index|[]
name|shingles
decl_stmt|;
DECL|field|tokenType
specifier|private
name|String
name|tokenType
init|=
literal|"shingle"
decl_stmt|;
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
comment|/**    * The string to use when joining adjacent tokens to form a shingle    */
DECL|field|TOKEN_SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_SEPARATOR
init|=
literal|" "
decl_stmt|;
comment|/**    * By default, we output unigrams (individual tokens) as well as shingles    * (token n-grams).    */
DECL|field|outputUnigrams
specifier|private
name|boolean
name|outputUnigrams
init|=
literal|true
decl_stmt|;
comment|/**    * maximum shingle size (number of tokens)    */
DECL|field|maxShingleSize
specifier|private
name|int
name|maxShingleSize
decl_stmt|;
comment|/**    * Constructs a ShingleFilter with the specified single size from the    * TokenStream<code>input</code>    *    * @param input input stream    * @param maxShingleSize maximum shingle size produced by the filter.    */
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
name|this
operator|.
name|posIncrAtt
operator|=
operator|(
name|PositionIncrementAttribute
operator|)
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeAtt
operator|=
operator|(
name|TypeAttribute
operator|)
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a ShingleFilter with default shingle size.    *    * @param input input stream    */
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
name|DEFAULT_MAX_SHINGLE_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a ShingleFilter with the specified token type for shingle tokens.    *    * @param input input stream    * @param tokenType token type for shingle tokens    */
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
name|shingles
operator|=
operator|new
name|StringBuffer
index|[
name|maxShingleSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|shingles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|shingles
index|[
name|i
index|]
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|maxShingleSize
operator|=
name|maxShingleSize
expr_stmt|;
block|}
comment|/**    * Clear the StringBuffers that are used for storing the output shingles.    */
DECL|method|clearShingles
specifier|private
name|void
name|clearShingles
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|shingles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|shingles
index|[
name|i
index|]
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|nextToken
specifier|private
name|AttributeSource
operator|.
name|State
name|nextToken
decl_stmt|;
DECL|field|shingleBufferPosition
specifier|private
name|int
name|shingleBufferPosition
decl_stmt|;
DECL|field|endOffsets
specifier|private
name|int
index|[]
name|endOffsets
decl_stmt|;
comment|/* (non-Javadoc)    * @see org.apache.lucene.analysis.TokenStream#next()    */
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
name|nextToken
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|fillShingleBuffer
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|nextToken
operator|=
operator|(
name|AttributeSource
operator|.
name|State
operator|)
name|shingleBuf
operator|.
name|getFirst
argument_list|()
expr_stmt|;
if|if
condition|(
name|shingleBufferPosition
operator|==
literal|0
operator|&&
operator|(
operator|!
name|shingleBuf
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
name|outputUnigrams
condition|)
block|{
name|restoreState
argument_list|(
name|nextToken
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|shingleBufferPosition
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|shingleBufferPosition
operator|<
name|shingleBuf
operator|.
name|size
argument_list|()
condition|)
block|{
name|restoreState
argument_list|(
name|nextToken
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|tokenType
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|endOffsets
index|[
name|shingleBufferPosition
index|]
argument_list|)
expr_stmt|;
name|StringBuffer
name|buf
init|=
name|shingles
index|[
name|shingleBufferPosition
index|]
decl_stmt|;
name|int
name|termLength
init|=
name|buf
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
index|[]
name|termBuffer
init|=
name|termAtt
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|termBuffer
operator|.
name|length
operator|<
name|termLength
condition|)
name|termBuffer
operator|=
name|termAtt
operator|.
name|resizeTermBuffer
argument_list|(
name|termLength
argument_list|)
expr_stmt|;
name|buf
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|termLength
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setTermLength
argument_list|(
name|termLength
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
operator|!
name|outputUnigrams
operator|)
operator|&&
name|shingleBufferPosition
operator|==
literal|1
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|shingleBufferPosition
operator|++
expr_stmt|;
if|if
condition|(
name|shingleBufferPosition
operator|==
name|shingleBuf
operator|.
name|size
argument_list|()
condition|)
block|{
name|nextToken
operator|=
literal|null
expr_stmt|;
name|shingleBufferPosition
operator|=
literal|0
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
name|nextToken
operator|=
literal|null
expr_stmt|;
name|shingleBufferPosition
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
DECL|field|numFillerTokensToInsert
specifier|private
name|int
name|numFillerTokensToInsert
decl_stmt|;
DECL|field|currentToken
specifier|private
name|AttributeSource
operator|.
name|State
name|currentToken
decl_stmt|;
DECL|field|hasCurrentToken
specifier|private
name|boolean
name|hasCurrentToken
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
DECL|field|posIncrAtt
specifier|private
name|PositionIncrementAttribute
name|posIncrAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
decl_stmt|;
comment|/**    * Get the next token from the input stream and push it on the token buffer.    * If we encounter a token with position increment> 1, we put filler tokens    * on the token buffer.    *<p/>    * Returns null when the end of the input stream is reached.    * @return the next token, or null if at end of input stream    * @throws IOException if the input stream has a problem    */
DECL|method|getNextToken
specifier|private
name|boolean
name|getNextToken
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
name|numFillerTokensToInsert
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|currentToken
operator|==
literal|null
condition|)
block|{
name|currentToken
operator|=
name|captureState
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|restoreState
argument_list|(
name|currentToken
argument_list|)
expr_stmt|;
block|}
name|numFillerTokensToInsert
operator|--
expr_stmt|;
comment|// A filler token occupies no space
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
name|termAtt
operator|.
name|setTermBuffer
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
return|return
literal|true
return|;
block|}
if|if
condition|(
name|hasCurrentToken
condition|)
block|{
if|if
condition|(
name|currentToken
operator|!=
literal|null
condition|)
block|{
name|restoreState
argument_list|(
name|currentToken
argument_list|)
expr_stmt|;
name|currentToken
operator|=
literal|null
expr_stmt|;
block|}
name|hasCurrentToken
operator|=
literal|false
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|false
return|;
name|hasCurrentToken
operator|=
literal|true
expr_stmt|;
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
name|numFillerTokensToInsert
operator|=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|-
literal|1
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Fill the output buffer with new shingles.    *    * @throws IOException if there's a problem getting the next token    */
DECL|method|fillShingleBuffer
specifier|private
name|boolean
name|fillShingleBuffer
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|addedToken
init|=
literal|false
decl_stmt|;
comment|/*      * Try to fill the shingle buffer.      */
do|do
block|{
if|if
condition|(
name|getNextToken
argument_list|()
condition|)
block|{
name|shingleBuf
operator|.
name|add
argument_list|(
name|captureState
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shingleBuf
operator|.
name|size
argument_list|()
operator|>
name|maxShingleSize
condition|)
block|{
name|shingleBuf
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|addedToken
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
do|while
condition|(
name|shingleBuf
operator|.
name|size
argument_list|()
operator|<
name|maxShingleSize
condition|)
do|;
if|if
condition|(
name|shingleBuf
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|/*      * If no new token could be added to the shingle buffer, we have reached      * the end of the input stream and have to discard the least recent token.      */
if|if
condition|(
operator|!
name|addedToken
condition|)
block|{
name|shingleBuf
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shingleBuf
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|clearShingles
argument_list|()
expr_stmt|;
name|endOffsets
operator|=
operator|new
name|int
index|[
name|shingleBuf
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|endOffsets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|endOffsets
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|shingleBuf
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|restoreState
argument_list|(
operator|(
name|AttributeSource
operator|.
name|State
operator|)
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|<
name|shingles
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|shingles
index|[
name|j
index|]
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|shingles
index|[
name|j
index|]
operator|.
name|append
argument_list|(
name|TOKEN_SEPARATOR
argument_list|)
expr_stmt|;
block|}
name|shingles
index|[
name|j
index|]
operator|.
name|append
argument_list|(
name|termAtt
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|termLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|endOffsets
index|[
name|i
index|]
operator|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
return|return
literal|true
return|;
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
