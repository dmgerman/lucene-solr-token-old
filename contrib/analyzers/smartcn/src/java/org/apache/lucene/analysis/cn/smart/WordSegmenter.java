begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|HHMMSegmenter
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
name|cn
operator|.
name|smart
operator|.
name|hhmm
operator|.
name|SegTokenFilter
import|;
end_import
begin_comment
comment|/**  * Segment a sentence of Chinese text into words.  */
end_comment
begin_class
DECL|class|WordSegmenter
class|class
name|WordSegmenter
block|{
DECL|field|hhmmSegmenter
specifier|private
name|HHMMSegmenter
name|hhmmSegmenter
init|=
operator|new
name|HHMMSegmenter
argument_list|()
decl_stmt|;
DECL|field|tokenFilter
specifier|private
name|SegTokenFilter
name|tokenFilter
init|=
operator|new
name|SegTokenFilter
argument_list|()
decl_stmt|;
comment|/**    * Segment a sentence into words with {@link HHMMSegmenter}    *     * @param sentence input sentence    * @param startOffset start offset of sentence    * @return {@link List} of {@link SegToken}    */
DECL|method|segmentSentence
specifier|public
name|List
name|segmentSentence
parameter_list|(
name|String
name|sentence
parameter_list|,
name|int
name|startOffset
parameter_list|)
block|{
name|List
name|segTokenList
init|=
name|hhmmSegmenter
operator|.
name|process
argument_list|(
name|sentence
argument_list|)
decl_stmt|;
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|// tokens from sentence, excluding WordType.SENTENCE_BEGIN and WordType.SENTENCE_END
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|segTokenList
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|convertSegToken
argument_list|(
operator|(
name|SegToken
operator|)
name|segTokenList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|sentence
argument_list|,
name|startOffset
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Process a {@link SegToken} so that it is ready for indexing.    *     * This method calculates offsets and normalizes the token with {@link SegTokenFilter}.    *     * @param st input {@link SegToken}    * @param sentence associated Sentence    * @param sentenceStartOffset offset into sentence    * @return Lucene {@link SegToken}    */
DECL|method|convertSegToken
specifier|public
name|SegToken
name|convertSegToken
parameter_list|(
name|SegToken
name|st
parameter_list|,
name|String
name|sentence
parameter_list|,
name|int
name|sentenceStartOffset
parameter_list|)
block|{
switch|switch
condition|(
name|st
operator|.
name|wordType
condition|)
block|{
case|case
name|WordType
operator|.
name|STRING
case|:
case|case
name|WordType
operator|.
name|NUMBER
case|:
case|case
name|WordType
operator|.
name|FULLWIDTH_NUMBER
case|:
case|case
name|WordType
operator|.
name|FULLWIDTH_STRING
case|:
name|st
operator|.
name|charArray
operator|=
name|sentence
operator|.
name|substring
argument_list|(
name|st
operator|.
name|startOffset
argument_list|,
name|st
operator|.
name|endOffset
argument_list|)
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
break|break;
default|default:
break|break;
block|}
name|st
operator|=
name|tokenFilter
operator|.
name|filter
argument_list|(
name|st
argument_list|)
expr_stmt|;
name|st
operator|.
name|startOffset
operator|+=
name|sentenceStartOffset
expr_stmt|;
name|st
operator|.
name|endOffset
operator|+=
name|sentenceStartOffset
expr_stmt|;
return|return
name|st
return|;
block|}
block|}
end_class
end_unit
