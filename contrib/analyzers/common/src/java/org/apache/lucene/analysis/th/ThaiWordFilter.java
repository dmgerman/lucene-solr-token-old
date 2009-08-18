begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.th
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|th
package|;
end_package
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Character
operator|.
name|UnicodeBlock
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
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|BreakIterator
import|;
end_import
begin_comment
comment|/**  * {@link TokenFilter} that use {@link java.text.BreakIterator} to break each   * Token that is Thai into separate Token(s) for each Thai word.  * @version 0.2  */
end_comment
begin_class
DECL|class|ThaiWordFilter
specifier|public
class|class
name|ThaiWordFilter
extends|extends
name|TokenFilter
block|{
DECL|field|breaker
specifier|private
name|BreakIterator
name|breaker
init|=
literal|null
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
DECL|field|thaiState
specifier|private
name|State
name|thaiState
init|=
literal|null
decl_stmt|;
DECL|method|ThaiWordFilter
specifier|public
name|ThaiWordFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|breaker
operator|=
name|BreakIterator
operator|.
name|getWordInstance
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"th"
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|thaiState
operator|!=
literal|null
condition|)
block|{
name|int
name|start
init|=
name|breaker
operator|.
name|current
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|breaker
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|end
operator|!=
name|BreakIterator
operator|.
name|DONE
condition|)
block|{
name|restoreState
argument_list|(
name|thaiState
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|termAtt
operator|.
name|termBuffer
argument_list|()
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
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
operator|+
name|start
argument_list|,
name|offsetAtt
operator|.
name|startOffset
argument_list|()
operator|+
name|end
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|thaiState
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
operator|==
literal|false
operator|||
name|termAtt
operator|.
name|termLength
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|false
return|;
name|String
name|text
init|=
name|termAtt
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|UnicodeBlock
operator|.
name|of
argument_list|(
name|text
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|!=
name|UnicodeBlock
operator|.
name|THAI
condition|)
block|{
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|text
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|thaiState
operator|=
name|captureState
argument_list|()
expr_stmt|;
name|breaker
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|int
name|end
init|=
name|breaker
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|end
operator|!=
name|BreakIterator
operator|.
name|DONE
condition|)
block|{
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|end
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
name|offsetAtt
operator|.
name|startOffset
argument_list|()
operator|+
name|end
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
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
name|thaiState
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
