begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.cjk
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cjk
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2004 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|Tokenizer
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
begin_comment
comment|/**  * CJKTokenizer was modified from StopTokenizer which does a decent job for  * most European languages. It performs other token methods for double-byte  * Characters: the token will return at each two charactors with overlap match.<br>  * Example: "java C1C2C3C4" will be segment to: "java" "C1C2" "C2C3" "C3C4" it  * also need filter filter zero length token ""<br>  * for Digit: digit, '+', '#' will token as letter<br>  * for more info on Asia language(Chinese Japanese Korean) text segmentation:  * please search<a  * href="http://www.google.com/search?q=word+chinese+segment">google</a>  *  * @author Che, Dong  */
end_comment
begin_class
DECL|class|CJKTokenizer
specifier|public
specifier|final
class|class
name|CJKTokenizer
extends|extends
name|Tokenizer
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/** Max word length */
DECL|field|MAX_WORD_LEN
specifier|private
specifier|static
specifier|final
name|int
name|MAX_WORD_LEN
init|=
literal|255
decl_stmt|;
comment|/** buffer size: */
DECL|field|IO_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|IO_BUFFER_SIZE
init|=
literal|256
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/** word offset, used to imply which character(in ) is parsed */
DECL|field|offset
specifier|private
name|int
name|offset
init|=
literal|0
decl_stmt|;
comment|/** the index used only for ioBuffer */
DECL|field|bufferIndex
specifier|private
name|int
name|bufferIndex
init|=
literal|0
decl_stmt|;
comment|/** data length */
DECL|field|dataLen
specifier|private
name|int
name|dataLen
init|=
literal|0
decl_stmt|;
comment|/**      * character buffer, store the characters which are used to compose<br>      * the returned Token      */
DECL|field|buffer
specifier|private
specifier|final
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|MAX_WORD_LEN
index|]
decl_stmt|;
comment|/**      * I/O buffer, used to store the content of the input(one of the<br>      * members of Tokenizer)      */
DECL|field|ioBuffer
specifier|private
specifier|final
name|char
index|[]
name|ioBuffer
init|=
operator|new
name|char
index|[
name|IO_BUFFER_SIZE
index|]
decl_stmt|;
comment|/** word type: single=>ASCII  double=>non-ASCII word=>default */
DECL|field|tokenType
specifier|private
name|String
name|tokenType
init|=
literal|"word"
decl_stmt|;
comment|/**      * tag: previous character is a cached double-byte character  "C1C2C3C4"      * ----(set the C1 isTokened) C1C2 "C2C3C4" ----(set the C2 isTokened)      * C1C2 C2C3 "C3C4" ----(set the C3 isTokened) "C1C2 C2C3 C3C4"      */
DECL|field|preIsTokened
specifier|private
name|boolean
name|preIsTokened
init|=
literal|false
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Construct a token stream processing the given input.      *      * @param in I/O reader      */
DECL|method|CJKTokenizer
specifier|public
name|CJKTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|input
operator|=
name|in
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the next token in the stream, or null at EOS.      * See http://java.sun.com/j2se/1.3/docs/api/java/lang/Character.UnicodeBlock.html      * for detail.      *      * @return Token      *      * @throws java.io.IOException - throw IOException when read error<br>      *         hanppened in the InputStream      *      */
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
comment|/** how many character(s) has been stored in buffer */
name|int
name|length
init|=
literal|0
decl_stmt|;
comment|/** the position used to create Token */
name|int
name|start
init|=
name|offset
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|/** current charactor */
name|char
name|c
decl_stmt|;
comment|/** unicode block of current charactor for detail */
name|Character
operator|.
name|UnicodeBlock
name|ub
decl_stmt|;
name|offset
operator|++
expr_stmt|;
if|if
condition|(
name|bufferIndex
operator|>=
name|dataLen
condition|)
block|{
name|dataLen
operator|=
name|input
operator|.
name|read
argument_list|(
name|ioBuffer
argument_list|)
expr_stmt|;
name|bufferIndex
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|dataLen
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|preIsTokened
operator|==
literal|true
condition|)
block|{
name|length
operator|=
literal|0
expr_stmt|;
name|preIsTokened
operator|=
literal|false
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
comment|//get current character
name|c
operator|=
name|ioBuffer
index|[
name|bufferIndex
operator|++
index|]
expr_stmt|;
comment|//get the UnicodeBlock of the current character
name|ub
operator|=
name|Character
operator|.
name|UnicodeBlock
operator|.
name|of
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
comment|//if the current character is ASCII or Extend ASCII
if|if
condition|(
operator|(
name|ub
operator|==
name|Character
operator|.
name|UnicodeBlock
operator|.
name|BASIC_LATIN
operator|)
operator|||
operator|(
name|ub
operator|==
name|Character
operator|.
name|UnicodeBlock
operator|.
name|HALFWIDTH_AND_FULLWIDTH_FORMS
operator|)
condition|)
block|{
if|if
condition|(
name|ub
operator|==
name|Character
operator|.
name|UnicodeBlock
operator|.
name|HALFWIDTH_AND_FULLWIDTH_FORMS
condition|)
block|{
comment|/** convert  HALFWIDTH_AND_FULLWIDTH_FORMS to BASIC_LATIN */
name|int
name|i
init|=
operator|(
name|int
operator|)
name|c
decl_stmt|;
name|i
operator|=
name|i
operator|-
literal|65248
expr_stmt|;
name|c
operator|=
operator|(
name|char
operator|)
name|i
expr_stmt|;
block|}
comment|// if the current character is a letter or "_" "+" "#"
if|if
condition|(
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|c
argument_list|)
operator|||
operator|(
operator|(
name|c
operator|==
literal|'_'
operator|)
operator|||
operator|(
name|c
operator|==
literal|'+'
operator|)
operator|||
operator|(
name|c
operator|==
literal|'#'
operator|)
operator|)
condition|)
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
comment|// "javaC1C2C3C4linux"<br>
comment|//      ^--: the current character begin to token the ASCII
comment|// letter
name|start
operator|=
name|offset
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tokenType
operator|==
literal|"double"
condition|)
block|{
comment|// "javaC1C2C3C4linux"<br>
comment|//              ^--: the previous non-ASCII
comment|// : the current character
name|offset
operator|--
expr_stmt|;
name|bufferIndex
operator|--
expr_stmt|;
name|tokenType
operator|=
literal|"single"
expr_stmt|;
if|if
condition|(
name|preIsTokened
operator|==
literal|true
condition|)
block|{
comment|// there is only one non-ASCII has been stored
name|length
operator|=
literal|0
expr_stmt|;
name|preIsTokened
operator|=
literal|false
expr_stmt|;
break|break;
block|}
else|else
block|{
break|break;
block|}
block|}
comment|// store the LowerCase(c) in the buffer
name|buffer
index|[
name|length
operator|++
index|]
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|tokenType
operator|=
literal|"single"
expr_stmt|;
comment|// break the procedure if buffer overflowed!
if|if
condition|(
name|length
operator|==
name|MAX_WORD_LEN
condition|)
block|{
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|preIsTokened
operator|==
literal|true
condition|)
block|{
name|length
operator|=
literal|0
expr_stmt|;
name|preIsTokened
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
else|else
block|{
comment|// non-ASCII letter, eg."C1C2C3C4"
if|if
condition|(
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
condition|)
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
name|start
operator|=
name|offset
operator|-
literal|1
expr_stmt|;
name|buffer
index|[
name|length
operator|++
index|]
operator|=
name|c
expr_stmt|;
name|tokenType
operator|=
literal|"double"
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|tokenType
operator|==
literal|"single"
condition|)
block|{
name|offset
operator|--
expr_stmt|;
name|bufferIndex
operator|--
expr_stmt|;
comment|//return the previous ASCII characters
break|break;
block|}
else|else
block|{
name|buffer
index|[
name|length
operator|++
index|]
operator|=
name|c
expr_stmt|;
name|tokenType
operator|=
literal|"double"
expr_stmt|;
if|if
condition|(
name|length
operator|==
literal|2
condition|)
block|{
name|offset
operator|--
expr_stmt|;
name|bufferIndex
operator|--
expr_stmt|;
name|preIsTokened
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|preIsTokened
operator|==
literal|true
condition|)
block|{
comment|// empty the buffer
name|length
operator|=
literal|0
expr_stmt|;
name|preIsTokened
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
return|return
operator|new
name|Token
argument_list|(
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
argument_list|,
name|start
argument_list|,
name|start
operator|+
name|length
argument_list|,
name|tokenType
argument_list|)
return|;
block|}
block|}
end_class
end_unit
