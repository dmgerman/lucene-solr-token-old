begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|Map
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
name|KeywordAttribute
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
name|util
operator|.
name|CharArrayMap
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
begin_comment
comment|/**  * Provides the ability to override any {@link KeywordAttribute} aware stemmer  * with custom dictionary-based stemming.  */
end_comment
begin_class
DECL|class|StemmerOverrideFilter
specifier|public
specifier|final
class|class
name|StemmerOverrideFilter
extends|extends
name|TokenFilter
block|{
DECL|field|dictionary
specifier|private
specifier|final
name|CharArrayMap
argument_list|<
name|String
argument_list|>
name|dictionary
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
DECL|field|keywordAtt
specifier|private
specifier|final
name|KeywordAttribute
name|keywordAtt
init|=
name|addAttribute
argument_list|(
name|KeywordAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Create a new StemmerOverrideFilter, performing dictionary-based stemming    * with the provided<code>dictionary</code>.    *<p>    * Any dictionary-stemmed terms will be marked with {@link KeywordAttribute}    * so that they will not be stemmed with stemmers down the chain.    *</p>    */
DECL|method|StemmerOverrideFilter
specifier|public
name|StemmerOverrideFilter
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|TokenStream
name|input
parameter_list|,
name|Map
argument_list|<
name|?
argument_list|,
name|String
argument_list|>
name|dictionary
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|dictionary
operator|=
name|dictionary
operator|instanceof
name|CharArrayMap
condition|?
operator|(
name|CharArrayMap
argument_list|<
name|String
argument_list|>
operator|)
name|dictionary
else|:
name|CharArrayMap
operator|.
name|copy
argument_list|(
name|matchVersion
argument_list|,
name|dictionary
argument_list|)
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
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|keywordAtt
operator|.
name|isKeyword
argument_list|()
condition|)
block|{
comment|// don't muck with already-keyworded terms
name|String
name|stem
init|=
name|dictionary
operator|.
name|get
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|stem
operator|!=
literal|null
condition|)
block|{
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|stem
argument_list|)
expr_stmt|;
name|keywordAtt
operator|.
name|setKeyword
argument_list|(
literal|true
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
block|}
end_class
end_unit
