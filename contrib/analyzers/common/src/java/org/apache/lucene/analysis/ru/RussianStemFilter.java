begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|TermAttribute
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * A {@link TokenFilter} that stems Russian words.   *<p>  * The implementation was inspired by GermanStemFilter.  * The input should be filtered by {@link RussianLowerCaseFilter} before passing it to RussianStemFilter ,  * because RussianStemFilter only works with lowercase part of any "russian" charset.  *</p>  *  * @version   $Id$  */
end_comment
begin_class
DECL|class|RussianStemFilter
specifier|public
specifier|final
class|class
name|RussianStemFilter
extends|extends
name|TokenFilter
block|{
comment|/**      * The actual token in the input stream.      */
DECL|field|stemmer
specifier|private
name|RussianStemmer
name|stemmer
init|=
literal|null
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|method|RussianStemFilter
specifier|public
name|RussianStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|char
index|[]
name|charset
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|stemmer
operator|=
operator|new
name|RussianStemmer
argument_list|(
name|charset
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
block|}
comment|/**      * Returns the next token in the stream, or null at EOS      */
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
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|term
init|=
name|termAtt
operator|.
name|term
argument_list|()
decl_stmt|;
name|String
name|s
init|=
name|stemmer
operator|.
name|stem
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
operator|&&
operator|!
name|s
operator|.
name|equals
argument_list|(
name|term
argument_list|)
condition|)
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|s
argument_list|)
expr_stmt|;
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
comment|/**      * Set a alternative/custom {@link RussianStemmer} for this filter.      */
DECL|method|setStemmer
specifier|public
name|void
name|setStemmer
parameter_list|(
name|RussianStemmer
name|stemmer
parameter_list|)
block|{
if|if
condition|(
name|stemmer
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|stemmer
operator|=
name|stemmer
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
