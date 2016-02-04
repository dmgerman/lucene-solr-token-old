begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.pattern
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pattern
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
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|CharsRef
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
name|CharsRefBuilder
import|;
end_import
begin_comment
comment|/**  * CaptureGroup uses Java regexes to emit multiple tokens - one for each capture  * group in one or more patterns.  *  *<p>  * For example, a pattern like:  *</p>  *  *<p>  *<code>"(https?://([a-zA-Z\-_0-9.]+))"</code>  *</p>  *  *<p>  * when matched against the string "http://www.foo.com/index" would return the  * tokens "https://www.foo.com" and "www.foo.com".  *</p>  *  *<p>  * If none of the patterns match, or if preserveOriginal is true, the original  * token will be preserved.  *</p>  *<p>  * Each pattern is matched as often as it can be, so the pattern  *<code> "(...)"</code>, when matched against<code>"abcdefghi"</code> would  * produce<code>["abc","def","ghi"]</code>  *</p>  *<p>  * A camelCaseFilter could be written as:  *</p>  *<p>  *<code>  *   "([A-Z]{2,})",                                   *   "(?&lt;![A-Z])([A-Z][a-z]+)",                       *   "(?:^|\\b|(?&lt;=[0-9_])|(?&lt;=[A-Z]{2}))([a-z]+)",  *   "([0-9]+)"  *</code>  *</p>  *<p>  * plus if {@link #preserveOriginal} is true, it would also return  *<code>"camelCaseFilter"</code>  *</p>  */
end_comment
begin_class
DECL|class|PatternCaptureGroupTokenFilter
specifier|public
specifier|final
class|class
name|PatternCaptureGroupTokenFilter
extends|extends
name|TokenFilter
block|{
DECL|field|charTermAttr
specifier|private
specifier|final
name|CharTermAttribute
name|charTermAttr
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posAttr
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posAttr
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|state
specifier|private
name|State
name|state
decl_stmt|;
DECL|field|matchers
specifier|private
specifier|final
name|Matcher
index|[]
name|matchers
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|CharsRefBuilder
name|spare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
DECL|field|groupCounts
specifier|private
specifier|final
name|int
index|[]
name|groupCounts
decl_stmt|;
DECL|field|preserveOriginal
specifier|private
specifier|final
name|boolean
name|preserveOriginal
decl_stmt|;
DECL|field|currentGroup
specifier|private
name|int
index|[]
name|currentGroup
decl_stmt|;
DECL|field|currentMatcher
specifier|private
name|int
name|currentMatcher
decl_stmt|;
comment|/**    * @param input    *          the input {@link TokenStream}    * @param preserveOriginal    *          set to true to return the original token even if one of the    *          patterns matches    * @param patterns    *          an array of {@link Pattern} objects to match against each token    */
DECL|method|PatternCaptureGroupTokenFilter
specifier|public
name|PatternCaptureGroupTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|boolean
name|preserveOriginal
parameter_list|,
name|Pattern
modifier|...
name|patterns
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|preserveOriginal
operator|=
name|preserveOriginal
expr_stmt|;
name|this
operator|.
name|matchers
operator|=
operator|new
name|Matcher
index|[
name|patterns
operator|.
name|length
index|]
expr_stmt|;
name|this
operator|.
name|groupCounts
operator|=
operator|new
name|int
index|[
name|patterns
operator|.
name|length
index|]
expr_stmt|;
name|this
operator|.
name|currentGroup
operator|=
operator|new
name|int
index|[
name|patterns
operator|.
name|length
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
name|patterns
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|matchers
index|[
name|i
index|]
operator|=
name|patterns
index|[
name|i
index|]
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupCounts
index|[
name|i
index|]
operator|=
name|this
operator|.
name|matchers
index|[
name|i
index|]
operator|.
name|groupCount
argument_list|()
expr_stmt|;
name|this
operator|.
name|currentGroup
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|method|nextCapture
specifier|private
name|boolean
name|nextCapture
parameter_list|()
block|{
name|int
name|min_offset
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|currentMatcher
operator|=
operator|-
literal|1
expr_stmt|;
name|Matcher
name|matcher
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
name|matchers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|matcher
operator|=
name|matchers
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|currentGroup
index|[
name|i
index|]
operator|==
operator|-
literal|1
condition|)
block|{
name|currentGroup
index|[
name|i
index|]
operator|=
name|matcher
operator|.
name|find
argument_list|()
condition|?
literal|1
else|:
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|currentGroup
index|[
name|i
index|]
operator|!=
literal|0
condition|)
block|{
while|while
condition|(
name|currentGroup
index|[
name|i
index|]
operator|<
name|groupCounts
index|[
name|i
index|]
operator|+
literal|1
condition|)
block|{
specifier|final
name|int
name|start
init|=
name|matcher
operator|.
name|start
argument_list|(
name|currentGroup
index|[
name|i
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|matcher
operator|.
name|end
argument_list|(
name|currentGroup
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|start
operator|==
name|end
operator|||
name|preserveOriginal
operator|&&
name|start
operator|==
literal|0
operator|&&
name|spare
operator|.
name|length
argument_list|()
operator|==
name|end
condition|)
block|{
name|currentGroup
index|[
name|i
index|]
operator|++
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|start
operator|<
name|min_offset
condition|)
block|{
name|min_offset
operator|=
name|start
expr_stmt|;
name|currentMatcher
operator|=
name|i
expr_stmt|;
block|}
break|break;
block|}
if|if
condition|(
name|currentGroup
index|[
name|i
index|]
operator|==
name|groupCounts
index|[
name|i
index|]
operator|+
literal|1
condition|)
block|{
name|currentGroup
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|i
operator|--
expr_stmt|;
block|}
block|}
block|}
return|return
name|currentMatcher
operator|!=
operator|-
literal|1
return|;
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
name|currentMatcher
operator|!=
operator|-
literal|1
operator|&&
name|nextCapture
argument_list|()
condition|)
block|{
assert|assert
name|state
operator|!=
literal|null
assert|;
name|clearAttributes
argument_list|()
expr_stmt|;
name|restoreState
argument_list|(
name|state
argument_list|)
expr_stmt|;
specifier|final
name|int
name|start
init|=
name|matchers
index|[
name|currentMatcher
index|]
operator|.
name|start
argument_list|(
name|currentGroup
index|[
name|currentMatcher
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|matchers
index|[
name|currentMatcher
index|]
operator|.
name|end
argument_list|(
name|currentGroup
index|[
name|currentMatcher
index|]
argument_list|)
decl_stmt|;
name|posAttr
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|charTermAttr
operator|.
name|copyBuffer
argument_list|(
name|spare
operator|.
name|chars
argument_list|()
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
name|currentGroup
index|[
name|currentMatcher
index|]
operator|++
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
block|{
return|return
literal|false
return|;
block|}
name|char
index|[]
name|buffer
init|=
name|charTermAttr
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|length
init|=
name|charTermAttr
operator|.
name|length
argument_list|()
decl_stmt|;
name|spare
operator|.
name|copyChars
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|state
operator|=
name|captureState
argument_list|()
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
name|matchers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|matchers
index|[
name|i
index|]
operator|.
name|reset
argument_list|(
name|spare
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|currentGroup
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|preserveOriginal
condition|)
block|{
name|currentMatcher
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nextCapture
argument_list|()
condition|)
block|{
specifier|final
name|int
name|start
init|=
name|matchers
index|[
name|currentMatcher
index|]
operator|.
name|start
argument_list|(
name|currentGroup
index|[
name|currentMatcher
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|end
init|=
name|matchers
index|[
name|currentMatcher
index|]
operator|.
name|end
argument_list|(
name|currentGroup
index|[
name|currentMatcher
index|]
argument_list|)
decl_stmt|;
comment|// if we start at 0 we can simply set the length and save the copy
if|if
condition|(
name|start
operator|==
literal|0
condition|)
block|{
name|charTermAttr
operator|.
name|setLength
argument_list|(
name|end
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|charTermAttr
operator|.
name|copyBuffer
argument_list|(
name|spare
operator|.
name|chars
argument_list|()
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
name|currentGroup
index|[
name|currentMatcher
index|]
operator|++
expr_stmt|;
block|}
return|return
literal|true
return|;
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
name|state
operator|=
literal|null
expr_stmt|;
name|currentMatcher
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
end_class
end_unit
