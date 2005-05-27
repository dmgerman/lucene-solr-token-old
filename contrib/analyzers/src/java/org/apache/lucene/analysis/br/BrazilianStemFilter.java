begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.br
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|br
package|;
end_package
begin_comment
comment|/**  * Copyright 2004-2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Based on GermanStemFilter  *  * @author Jo&atilde;o Kramer  */
end_comment
begin_class
DECL|class|BrazilianStemFilter
specifier|public
specifier|final
class|class
name|BrazilianStemFilter
extends|extends
name|TokenFilter
block|{
comment|/**    * The actual token in the input stream.    */
DECL|field|token
specifier|private
name|Token
name|token
init|=
literal|null
decl_stmt|;
DECL|field|stemmer
specifier|private
name|BrazilianStemmer
name|stemmer
init|=
literal|null
decl_stmt|;
DECL|field|exclusions
specifier|private
name|Set
name|exclusions
init|=
literal|null
decl_stmt|;
DECL|method|BrazilianStemFilter
specifier|public
name|BrazilianStemFilter
parameter_list|(
name|TokenStream
name|in
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
name|BrazilianStemmer
argument_list|()
expr_stmt|;
block|}
comment|/**    * Builds a BrazilianStemFilter that uses an exclusiontable.    *    * @deprecated    */
DECL|method|BrazilianStemFilter
specifier|public
name|BrazilianStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|Hashtable
name|exclusiontable
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|exclusions
operator|=
operator|new
name|HashSet
argument_list|(
name|exclusiontable
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|BrazilianStemFilter
specifier|public
name|BrazilianStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|Set
name|exclusiontable
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|exclusions
operator|=
name|exclusiontable
expr_stmt|;
block|}
comment|/**    * @return Returns the next token in the stream, or null at EOS.    */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|token
operator|=
name|input
operator|.
name|next
argument_list|()
operator|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Check the exclusiontable.
elseif|else
if|if
condition|(
name|exclusions
operator|!=
literal|null
operator|&&
name|exclusions
operator|.
name|contains
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|token
return|;
block|}
else|else
block|{
name|String
name|s
init|=
name|stemmer
operator|.
name|stem
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
decl_stmt|;
comment|// If not stemmed, dont waste the time creating a new token.
if|if
condition|(
operator|(
name|s
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|s
operator|.
name|equals
argument_list|(
name|token
operator|.
name|termText
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|Token
argument_list|(
name|s
argument_list|,
name|token
operator|.
name|startOffset
argument_list|()
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
argument_list|,
name|token
operator|.
name|type
argument_list|()
argument_list|)
return|;
block|}
return|return
name|token
return|;
block|}
block|}
block|}
end_class
end_unit
