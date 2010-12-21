begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|io
operator|.
name|Reader
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
name|util
operator|.
name|AttributeSource
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
name|CharacterUtils
operator|.
name|CharacterBuffer
import|;
end_import
begin_comment
comment|/**  * An abstract base class for simple, character-oriented tokenizers.   *<p>  *<a name="version">You must specify the required {@link Version} compatibility  * when creating {@link CharTokenizer}:  *<ul>  *<li>As of 3.1, {@link CharTokenizer} uses an int based API to normalize and  * detect token codepoints. See {@link #isTokenChar(int)} and  * {@link #normalize(int)} for details.</li>  *</ul>  *<p>  * A new {@link CharTokenizer} API has been introduced with Lucene 3.1. This API  * moved from UTF-16 code units to UTF-32 codepoints to eventually add support  * for<a href=  * "http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Character.html#supplementary"  *>supplementary characters</a>. The old<i>char</i> based API has been  * deprecated and should be replaced with the<i>int</i> based methods  * {@link #isTokenChar(int)} and {@link #normalize(int)}.  *</p>  *<p>  * As of Lucene 3.1 each {@link CharTokenizer} - constructor expects a  * {@link Version} argument. Based on the given {@link Version} either the new  * API or a backwards compatibility layer is used at runtime. For  * {@link Version}< 3.1 the backwards compatibility layer ensures correct  * behavior even for indexes build with previous versions of Lucene. If a  * {@link Version}>= 3.1 is used {@link CharTokenizer} requires the new API to  * be implemented by the instantiated class. Yet, the old<i>char</i> based API  * is not required anymore even if backwards compatibility must be preserved.  * {@link CharTokenizer} subclasses implementing the new API are fully backwards  * compatible if instantiated with {@link Version}< 3.1.  *</p>  *<p>  *<strong>Note:</strong> If you use a subclass of {@link CharTokenizer} with {@link Version}>=  * 3.1 on an index build with a version< 3.1, created tokens might not be  * compatible with the terms in your index.  *</p>  **/
end_comment
begin_class
DECL|class|CharTokenizer
specifier|public
specifier|abstract
class|class
name|CharTokenizer
extends|extends
name|Tokenizer
block|{
comment|/**    * Creates a new {@link CharTokenizer} instance    *     * @param matchVersion    *          Lucene version to match See {@link<a href="#version">above</a>}    * @param input    *          the input to split up into tokens    */
DECL|method|CharTokenizer
specifier|public
name|CharTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|charUtils
operator|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|matchVersion
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link CharTokenizer} instance    *     * @param matchVersion    *          Lucene version to match See {@link<a href="#version">above</a>}    * @param source    *          the attribute source to use for this {@link Tokenizer}    * @param input    *          the input to split up into tokens    */
DECL|method|CharTokenizer
specifier|public
name|CharTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|charUtils
operator|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|matchVersion
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link CharTokenizer} instance    *     * @param matchVersion    *          Lucene version to match See {@link<a href="#version">above</a>}    * @param factory    *          the attribute factory to use for this {@link Tokenizer}    * @param input    *          the input to split up into tokens    */
DECL|method|CharTokenizer
specifier|public
name|CharTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|charUtils
operator|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|(
name|matchVersion
argument_list|)
expr_stmt|;
block|}
DECL|field|offset
DECL|field|bufferIndex
DECL|field|dataLen
DECL|field|finalOffset
specifier|private
name|int
name|offset
init|=
literal|0
decl_stmt|,
name|bufferIndex
init|=
literal|0
decl_stmt|,
name|dataLen
init|=
literal|0
decl_stmt|,
name|finalOffset
init|=
literal|0
decl_stmt|;
DECL|field|MAX_WORD_LEN
specifier|private
specifier|static
specifier|final
name|int
name|MAX_WORD_LEN
init|=
literal|255
decl_stmt|;
DECL|field|IO_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|IO_BUFFER_SIZE
init|=
literal|4096
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
DECL|field|charUtils
specifier|private
specifier|final
name|CharacterUtils
name|charUtils
decl_stmt|;
DECL|field|ioBuffer
specifier|private
specifier|final
name|CharacterBuffer
name|ioBuffer
init|=
name|CharacterUtils
operator|.
name|newCharacterBuffer
argument_list|(
name|IO_BUFFER_SIZE
argument_list|)
decl_stmt|;
comment|/**    * Returns true iff a codepoint should be included in a token. This tokenizer    * generates as tokens adjacent sequences of codepoints which satisfy this    * predicate. Codepoints for which this is false are used to define token    * boundaries and are not included in tokens.    */
DECL|method|isTokenChar
specifier|protected
specifier|abstract
name|boolean
name|isTokenChar
parameter_list|(
name|int
name|c
parameter_list|)
function_decl|;
comment|/**    * Called on each token character to normalize it before it is added to the    * token. The default implementation does nothing. Subclasses may use this to,    * e.g., lowercase tokens.    */
DECL|method|normalize
specifier|protected
name|int
name|normalize
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|c
return|;
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
name|clearAttributes
argument_list|()
expr_stmt|;
name|int
name|length
init|=
literal|0
decl_stmt|;
name|int
name|start
init|=
operator|-
literal|1
decl_stmt|;
comment|// this variable is always initialized
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|bufferIndex
operator|>=
name|dataLen
condition|)
block|{
name|offset
operator|+=
name|dataLen
expr_stmt|;
if|if
condition|(
operator|!
name|charUtils
operator|.
name|fill
argument_list|(
name|ioBuffer
argument_list|,
name|input
argument_list|)
condition|)
block|{
comment|// read supplementary char aware with CharacterUtils
name|dataLen
operator|=
literal|0
expr_stmt|;
comment|// so next offset += dataLen won't decrement offset
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
break|break;
block|}
else|else
block|{
name|finalOffset
operator|=
name|correctOffset
argument_list|(
name|offset
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
name|dataLen
operator|=
name|ioBuffer
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|bufferIndex
operator|=
literal|0
expr_stmt|;
block|}
comment|// use CharacterUtils here to support< 3.1 UTF-16 code unit behavior if the char based methods are gone
specifier|final
name|int
name|c
init|=
name|charUtils
operator|.
name|codePointAt
argument_list|(
name|ioBuffer
operator|.
name|getBuffer
argument_list|()
argument_list|,
name|bufferIndex
argument_list|)
decl_stmt|;
name|bufferIndex
operator|+=
name|Character
operator|.
name|charCount
argument_list|(
name|c
argument_list|)
expr_stmt|;
if|if
condition|(
name|isTokenChar
argument_list|(
name|c
argument_list|)
condition|)
block|{
comment|// if it's a token char
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
comment|// start of token
assert|assert
name|start
operator|==
operator|-
literal|1
assert|;
name|start
operator|=
name|offset
operator|+
name|bufferIndex
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|length
operator|>=
name|buffer
operator|.
name|length
operator|-
literal|1
condition|)
block|{
comment|// check if a supplementary could run out of bounds
name|buffer
operator|=
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
literal|2
operator|+
name|length
argument_list|)
expr_stmt|;
comment|// make sure a supplementary fits in the buffer
block|}
name|length
operator|+=
name|Character
operator|.
name|toChars
argument_list|(
name|normalize
argument_list|(
name|c
argument_list|)
argument_list|,
name|buffer
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// buffer it, normalized
if|if
condition|(
name|length
operator|>=
name|MAX_WORD_LEN
condition|)
comment|// buffer overflow! make sure to check for>= surrogate pair could break == test
break|break;
block|}
elseif|else
if|if
condition|(
name|length
operator|>
literal|0
condition|)
comment|// at non-Letter w/ chars
break|break;
comment|// return 'em
block|}
name|termAtt
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
assert|assert
name|start
operator|!=
operator|-
literal|1
assert|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|start
argument_list|)
argument_list|,
name|finalOffset
operator|=
name|correctOffset
argument_list|(
name|start
operator|+
name|length
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
block|{
comment|// set final offset
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|finalOffset
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|bufferIndex
operator|=
literal|0
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|dataLen
operator|=
literal|0
expr_stmt|;
name|finalOffset
operator|=
literal|0
expr_stmt|;
name|ioBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// make sure to reset the IO buffer!!
block|}
block|}
end_class
end_unit
