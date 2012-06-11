begin_unit
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
begin_comment
comment|/**  * This class produces a special form of reversed tokens, suitable for  * better handling of leading wildcards. Tokens from the input TokenStream  * are reversed and prepended with a special "reversed" marker character.  * If<code>withOriginal<code> argument is<code>true</code> then first the  * original token is returned, and then the reversed token (with  *<code>positionIncrement == 0</code>) is returned. Otherwise only reversed  * tokens are returned.  *<p>Note: this filter doubles the number of tokens in the input stream when  *<code>withOriginal == true</code>, which proportionally increases the size  * of postings and term dictionary in the index.  */
end_comment
begin_class
DECL|class|ReversedWildcardFilter
specifier|public
specifier|final
class|class
name|ReversedWildcardFilter
extends|extends
name|TokenFilter
block|{
DECL|field|withOriginal
specifier|private
name|boolean
name|withOriginal
decl_stmt|;
DECL|field|markerChar
specifier|private
name|char
name|markerChar
decl_stmt|;
DECL|field|save
specifier|private
name|State
name|save
decl_stmt|;
DECL|field|termAtt
specifier|private
name|CharTermAttribute
name|termAtt
decl_stmt|;
DECL|field|posAtt
specifier|private
name|PositionIncrementAttribute
name|posAtt
decl_stmt|;
DECL|method|ReversedWildcardFilter
specifier|protected
name|ReversedWildcardFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|boolean
name|withOriginal
parameter_list|,
name|char
name|markerChar
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|posAtt
operator|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|withOriginal
operator|=
name|withOriginal
expr_stmt|;
name|this
operator|.
name|markerChar
operator|=
name|markerChar
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
name|save
operator|!=
literal|null
condition|)
block|{
comment|// clearAttributes();  // not currently necessary
name|restoreState
argument_list|(
name|save
argument_list|)
expr_stmt|;
name|save
operator|=
literal|null
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
comment|// pass through zero-length terms
name|int
name|oldLen
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldLen
operator|==
literal|0
condition|)
return|return
literal|true
return|;
name|int
name|origOffset
init|=
name|posAtt
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|withOriginal
operator|==
literal|true
condition|)
block|{
name|posAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|save
operator|=
name|captureState
argument_list|()
expr_stmt|;
block|}
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
name|oldLen
operator|+
literal|1
argument_list|)
decl_stmt|;
name|buffer
index|[
name|oldLen
index|]
operator|=
name|markerChar
expr_stmt|;
name|reverse
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|oldLen
operator|+
literal|1
argument_list|)
expr_stmt|;
name|posAtt
operator|.
name|setPositionIncrement
argument_list|(
name|origOffset
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|oldLen
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Partially reverses the given input buffer in-place from the given offset    * up to the given length, keeping surrogate pairs in the correct (non-reversed) order.    * @param buffer the input char array to reverse    * @param start the offset from where to reverse the buffer    * @param len the length in the buffer up to where the    *        buffer should be reversed    */
DECL|method|reverse
specifier|public
specifier|static
name|void
name|reverse
parameter_list|(
specifier|final
name|char
index|[]
name|buffer
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
block|{
comment|/* modified version of Apache Harmony AbstractStringBuilder reverse0() */
if|if
condition|(
name|len
operator|<
literal|2
condition|)
return|return;
name|int
name|end
init|=
operator|(
name|start
operator|+
name|len
operator|)
operator|-
literal|1
decl_stmt|;
name|char
name|frontHigh
init|=
name|buffer
index|[
name|start
index|]
decl_stmt|;
name|char
name|endLow
init|=
name|buffer
index|[
name|end
index|]
decl_stmt|;
name|boolean
name|allowFrontSur
init|=
literal|true
decl_stmt|,
name|allowEndSur
init|=
literal|true
decl_stmt|;
specifier|final
name|int
name|mid
init|=
name|start
operator|+
operator|(
name|len
operator|>>
literal|1
operator|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|mid
condition|;
operator|++
name|i
operator|,
operator|--
name|end
control|)
block|{
specifier|final
name|char
name|frontLow
init|=
name|buffer
index|[
name|i
operator|+
literal|1
index|]
decl_stmt|;
specifier|final
name|char
name|endHigh
init|=
name|buffer
index|[
name|end
operator|-
literal|1
index|]
decl_stmt|;
specifier|final
name|boolean
name|surAtFront
init|=
name|allowFrontSur
operator|&&
name|Character
operator|.
name|isSurrogatePair
argument_list|(
name|frontHigh
argument_list|,
name|frontLow
argument_list|)
decl_stmt|;
if|if
condition|(
name|surAtFront
operator|&&
operator|(
name|len
operator|<
literal|3
operator|)
condition|)
block|{
comment|// nothing to do since surAtFront is allowed and 1 char left
return|return;
block|}
specifier|final
name|boolean
name|surAtEnd
init|=
name|allowEndSur
operator|&&
name|Character
operator|.
name|isSurrogatePair
argument_list|(
name|endHigh
argument_list|,
name|endLow
argument_list|)
decl_stmt|;
name|allowFrontSur
operator|=
name|allowEndSur
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|surAtFront
operator|==
name|surAtEnd
condition|)
block|{
if|if
condition|(
name|surAtFront
condition|)
block|{
comment|// both surrogates
name|buffer
index|[
name|end
index|]
operator|=
name|frontLow
expr_stmt|;
name|buffer
index|[
operator|--
name|end
index|]
operator|=
name|frontHigh
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|endHigh
expr_stmt|;
name|buffer
index|[
operator|++
name|i
index|]
operator|=
name|endLow
expr_stmt|;
name|frontHigh
operator|=
name|buffer
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
name|endLow
operator|=
name|buffer
index|[
name|end
operator|-
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
comment|// neither surrogates
name|buffer
index|[
name|end
index|]
operator|=
name|frontHigh
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|endLow
expr_stmt|;
name|frontHigh
operator|=
name|frontLow
expr_stmt|;
name|endLow
operator|=
name|endHigh
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|surAtFront
condition|)
block|{
comment|// surrogate only at the front
name|buffer
index|[
name|end
index|]
operator|=
name|frontLow
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|endLow
expr_stmt|;
name|endLow
operator|=
name|endHigh
expr_stmt|;
name|allowFrontSur
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|// surrogate only at the end
name|buffer
index|[
name|end
index|]
operator|=
name|frontHigh
expr_stmt|;
name|buffer
index|[
name|i
index|]
operator|=
name|endHigh
expr_stmt|;
name|frontHigh
operator|=
name|frontLow
expr_stmt|;
name|allowEndSur
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|(
name|len
operator|&
literal|0x01
operator|)
operator|==
literal|1
operator|&&
operator|!
operator|(
name|allowFrontSur
operator|&&
name|allowEndSur
operator|)
condition|)
block|{
comment|// only if odd length
name|buffer
index|[
name|end
index|]
operator|=
name|allowFrontSur
condition|?
name|endLow
else|:
name|frontHigh
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
