begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * A TokenFilter which applies a Pattern to each token in the stream,  * replacing match occurances with the specified replacement string.  *  *<p>  *<b>Note:</b> Depending on the input and the pattern used and the input  * TokenStream, this TokenFilter may produce Tokens whose text is the empty  * string.  *</p>  *   * @version $Id:$  * @see Pattern  */
end_comment
begin_class
DECL|class|PatternReplaceFilter
specifier|public
specifier|final
class|class
name|PatternReplaceFilter
extends|extends
name|TokenFilter
block|{
DECL|field|p
specifier|private
specifier|final
name|Pattern
name|p
decl_stmt|;
DECL|field|replacement
specifier|private
specifier|final
name|String
name|replacement
decl_stmt|;
DECL|field|all
specifier|private
specifier|final
name|boolean
name|all
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
decl_stmt|;
DECL|field|m
specifier|private
specifier|final
name|Matcher
name|m
decl_stmt|;
comment|/**    * Constructs an instance to replace either the first, or all occurances    *    * @param in the TokenStream to process    * @param p the patterm to apply to each Token    * @param replacement the "replacement string" to substitute, if null a    *        blank string will be used. Note that this is not the literal    *        string that will be used, '$' and '\' have special meaning.    * @param all if true, all matches will be replaced otherwise just the first match.    * @see Matcher#quoteReplacement    */
DECL|method|PatternReplaceFilter
specifier|public
name|PatternReplaceFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|Pattern
name|p
parameter_list|,
name|String
name|replacement
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|p
operator|=
name|p
expr_stmt|;
name|this
operator|.
name|replacement
operator|=
operator|(
literal|null
operator|==
name|replacement
operator|)
condition|?
literal|""
else|:
name|replacement
expr_stmt|;
name|this
operator|.
name|all
operator|=
name|all
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
name|m
operator|=
name|p
operator|.
name|matcher
argument_list|(
name|termAtt
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
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|false
return|;
name|m
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
comment|// replaceAll/replaceFirst will reset() this previous find.
name|String
name|transformed
init|=
name|all
condition|?
name|m
operator|.
name|replaceAll
argument_list|(
name|replacement
argument_list|)
else|:
name|m
operator|.
name|replaceFirst
argument_list|(
name|replacement
argument_list|)
decl_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|transformed
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
