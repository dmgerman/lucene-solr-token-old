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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2004 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
comment|/**  * Based on (copied) the GermanStemFilter  *  * @author JoÃ£o Kramer  *<p/>  *<p/>  *         A filter that stemms german words. It supports a table of words that should  *         not be stemmed at all.  * @author Gerhard Schwarz  */
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
literal|0
argument_list|,
name|s
operator|.
name|length
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
