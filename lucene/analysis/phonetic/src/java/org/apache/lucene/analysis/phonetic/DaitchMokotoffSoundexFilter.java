begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.phonetic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|phonetic
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
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|DaitchMokotoffSoundex
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
begin_comment
comment|/**  * Create tokens for phonetic matches based on DaitchâMokotoff Soundex.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|DaitchMokotoffSoundexFilter
specifier|public
specifier|final
class|class
name|DaitchMokotoffSoundexFilter
extends|extends
name|TokenFilter
block|{
comment|/** true if encoded tokens should be added as synonyms */
DECL|field|inject
specifier|protected
name|boolean
name|inject
init|=
literal|true
decl_stmt|;
comment|/** phonetic encoder */
DECL|field|encoder
specifier|protected
name|DaitchMokotoffSoundex
name|encoder
init|=
operator|new
name|DaitchMokotoffSoundex
argument_list|()
decl_stmt|;
comment|// output is a string such as ab|ac|...
DECL|field|pattern
specifier|private
specifier|static
specifier|final
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([^|]+)"
argument_list|)
decl_stmt|;
comment|// matcher over any buffered output
DECL|field|matcher
specifier|private
specifier|final
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
decl_stmt|;
comment|// encoded representation
DECL|field|encoded
specifier|private
name|String
name|encoded
decl_stmt|;
comment|// preserves all attributes for any buffered outputs
DECL|field|state
specifier|private
name|State
name|state
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
DECL|field|posAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Creates a DaitchMokotoffSoundexFilter by either adding encoded forms as synonyms (    *<code>inject=true</code>) or replacing them.    */
DECL|method|DaitchMokotoffSoundexFilter
specifier|public
name|DaitchMokotoffSoundexFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|boolean
name|inject
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|inject
operator|=
name|inject
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
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
assert|assert
name|state
operator|!=
literal|null
operator|&&
name|encoded
operator|!=
literal|null
assert|;
name|restoreState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|encoded
argument_list|,
name|matcher
operator|.
name|start
argument_list|(
literal|1
argument_list|)
argument_list|,
name|matcher
operator|.
name|end
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|posAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
comment|// pass through zero-length terms
if|if
condition|(
name|termAtt
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
name|encoded
operator|=
name|encoder
operator|.
name|soundex
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|state
operator|=
name|captureState
argument_list|()
expr_stmt|;
name|matcher
operator|.
name|reset
argument_list|(
name|encoded
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|inject
condition|)
block|{
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|encoded
argument_list|,
name|matcher
operator|.
name|start
argument_list|(
literal|1
argument_list|)
argument_list|,
name|matcher
operator|.
name|end
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
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
name|matcher
operator|.
name|reset
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|state
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
