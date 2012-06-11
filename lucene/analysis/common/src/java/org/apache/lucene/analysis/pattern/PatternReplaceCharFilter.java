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
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|CharStream
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
name|charfilter
operator|.
name|BaseCharFilter
import|;
end_import
begin_comment
comment|/**  * CharFilter that uses a regular expression for the target of replace string.  * The pattern match will be done in each "block" in char stream.  *   *<p>  * ex1) source="aa&nbsp;&nbsp;bb&nbsp;aa&nbsp;bb", pattern="(aa)\\s+(bb)" replacement="$1#$2"<br/>  * output="aa#bb&nbsp;aa#bb"  *</p>  *   * NOTE: If you produce a phrase that has different length to source string  * and the field is used for highlighting for a term of the phrase, you will  * face a trouble.  *   *<p>  * ex2) source="aa123bb", pattern="(aa)\\d+(bb)" replacement="$1&nbsp;$2"<br/>  * output="aa&nbsp;bb"<br/>  * and you want to search bb and highlight it, you will get<br/>  * highlight snippet="aa1&lt;em&gt;23bb&lt;/em&gt;"  *</p>  *   * @since Solr 1.5  */
end_comment
begin_class
DECL|class|PatternReplaceCharFilter
specifier|public
class|class
name|PatternReplaceCharFilter
extends|extends
name|BaseCharFilter
block|{
DECL|field|pattern
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
DECL|field|replacement
specifier|private
specifier|final
name|String
name|replacement
decl_stmt|;
DECL|field|transformedInput
specifier|private
name|Reader
name|transformedInput
decl_stmt|;
DECL|method|PatternReplaceCharFilter
specifier|public
name|PatternReplaceCharFilter
parameter_list|(
name|Pattern
name|pattern
parameter_list|,
name|String
name|replacement
parameter_list|,
name|CharStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|replacement
operator|=
name|replacement
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Buffer all input on the first call.
if|if
condition|(
name|transformedInput
operator|==
literal|null
condition|)
block|{
name|StringBuilder
name|buffered
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
index|[]
name|temp
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
for|for
control|(
name|int
name|cnt
init|=
name|input
operator|.
name|read
argument_list|(
name|temp
argument_list|)
init|;
name|cnt
operator|>
literal|0
condition|;
name|cnt
operator|=
name|input
operator|.
name|read
argument_list|(
name|temp
argument_list|)
control|)
block|{
name|buffered
operator|.
name|append
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
block|}
name|transformedInput
operator|=
operator|new
name|StringReader
argument_list|(
name|processPattern
argument_list|(
name|buffered
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|transformedInput
operator|.
name|read
argument_list|(
name|cbuf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|correct
specifier|protected
name|int
name|correct
parameter_list|(
name|int
name|currentOff
parameter_list|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|super
operator|.
name|correct
argument_list|(
name|currentOff
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Replace pattern in input and mark correction offsets.     */
DECL|method|processPattern
name|CharSequence
name|processPattern
parameter_list|(
name|CharSequence
name|input
parameter_list|)
block|{
specifier|final
name|Matcher
name|m
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|input
argument_list|)
decl_stmt|;
specifier|final
name|StringBuffer
name|cumulativeOutput
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|int
name|cumulative
init|=
literal|0
decl_stmt|;
name|int
name|lastMatchEnd
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
specifier|final
name|int
name|groupSize
init|=
name|m
operator|.
name|end
argument_list|()
operator|-
name|m
operator|.
name|start
argument_list|()
decl_stmt|;
specifier|final
name|int
name|skippedSize
init|=
name|m
operator|.
name|start
argument_list|()
operator|-
name|lastMatchEnd
decl_stmt|;
name|lastMatchEnd
operator|=
name|m
operator|.
name|end
argument_list|()
expr_stmt|;
specifier|final
name|int
name|lengthBeforeReplacement
init|=
name|cumulativeOutput
operator|.
name|length
argument_list|()
operator|+
name|skippedSize
decl_stmt|;
name|m
operator|.
name|appendReplacement
argument_list|(
name|cumulativeOutput
argument_list|,
name|replacement
argument_list|)
expr_stmt|;
comment|// Matcher doesn't tell us how many characters have been appended before the replacement.
comment|// So we need to calculate it. Skipped characters have been added as part of appendReplacement.
specifier|final
name|int
name|replacementSize
init|=
name|cumulativeOutput
operator|.
name|length
argument_list|()
operator|-
name|lengthBeforeReplacement
decl_stmt|;
if|if
condition|(
name|groupSize
operator|!=
name|replacementSize
condition|)
block|{
if|if
condition|(
name|replacementSize
operator|<
name|groupSize
condition|)
block|{
comment|// The replacement is smaller.
comment|// Add the 'backskip' to the next index after the replacement (this is possibly
comment|// after the end of string, but it's fine -- it just means the last character
comment|// of the replaced block doesn't reach the end of the original string.
name|cumulative
operator|+=
name|groupSize
operator|-
name|replacementSize
expr_stmt|;
name|int
name|atIndex
init|=
name|lengthBeforeReplacement
operator|+
name|replacementSize
decl_stmt|;
comment|// System.err.println(atIndex + "!" + cumulative);
name|addOffCorrectMap
argument_list|(
name|atIndex
argument_list|,
name|cumulative
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// The replacement is larger. Every new index needs to point to the last
comment|// element of the original group (if any).
for|for
control|(
name|int
name|i
init|=
name|groupSize
init|;
name|i
operator|<
name|replacementSize
condition|;
name|i
operator|++
control|)
block|{
name|addOffCorrectMap
argument_list|(
name|lengthBeforeReplacement
operator|+
name|i
argument_list|,
operator|--
name|cumulative
argument_list|)
expr_stmt|;
comment|// System.err.println((lengthBeforeReplacement + i) + " " + cumulative);
block|}
block|}
block|}
block|}
comment|// Append the remaining output, no further changes to indices.
name|m
operator|.
name|appendTail
argument_list|(
name|cumulativeOutput
argument_list|)
expr_stmt|;
return|return
name|cumulativeOutput
return|;
block|}
block|}
end_class
end_unit
